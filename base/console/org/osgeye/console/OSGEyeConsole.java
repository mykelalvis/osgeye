package org.osgeye.console;

import static java.lang.System.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.Completor;
import jline.ConsoleReader;
import jline.History;

import org.osgeye.Constants;
import org.osgeye.client.ServerListener;
import org.osgeye.client.events.ServerEvent;
import org.osgeye.client.network.NetworkClient;
import org.osgeye.client.network.NetworkClientListener;
import org.osgeye.client.network.NetworkServerIdentity;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandUtils;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.commands.actions.InstallBundleCommand;
import org.osgeye.console.commands.actions.RefreshPackagesCommand;
import org.osgeye.console.commands.actions.ResolveBundlesCommand;
import org.osgeye.console.commands.actions.SetCommand;
import org.osgeye.console.commands.actions.StartBundlesCommand;
import org.osgeye.console.commands.actions.StopBundlesCommand;
import org.osgeye.console.commands.actions.UninstallBundlesCommand;
import org.osgeye.console.commands.descriptions.BundleStatesCommand;
import org.osgeye.console.commands.descriptions.BundlesCommand;
import org.osgeye.console.commands.descriptions.ConfigurationsCommand;
import org.osgeye.console.commands.descriptions.ExportsCommand;
import org.osgeye.console.commands.descriptions.FrameworkCommand;
import org.osgeye.console.commands.descriptions.GraphCommand;
import org.osgeye.console.commands.descriptions.ImportsCommand;
import org.osgeye.console.commands.descriptions.ListBundlesCommand;
import org.osgeye.console.commands.descriptions.ManifestCommand;
import org.osgeye.console.commands.descriptions.NotificationsCommand;
import org.osgeye.console.commands.descriptions.PackagesCommand;
import org.osgeye.console.commands.descriptions.ServicesCommand;
import org.osgeye.console.commands.diagnosis.MissingWiringCommand;
import org.osgeye.console.commands.diagnosis.UnresolvedCommand;
import org.osgeye.console.commands.misc.ClearCommand;
import org.osgeye.console.commands.misc.ExitCommand;
import org.osgeye.console.commands.misc.HelpCommand;
import org.osgeye.console.commands.misc.ReloadCommand;
import org.osgeye.console.completors.BranchCompletor;
import org.osgeye.utils.Pair;

public class OSGEyeConsole implements ServerListener, NetworkClientListener
{
  
  public static void main(String[] args) throws Exception
  {
    if ((args.length >= 1) 
        && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("-help") 
            || args[0].equalsIgnoreCase("?")))
    {
      printHelp();
      return;
    }
    
    String host = null;
    int port = Constants.DEFAULT_PORT;
    String user = null;
    String password = null;
    
    BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
    if (args.length > 0)
    {
      host = args[0];
    }
    else
    {
      out.print("\nEnter the host: ");
      host = inReader.readLine();
    }
    
    if (args.length > 1)
    {
      try
      {
        port = Integer.parseInt(args[1]);
        if ((port < 1) || (port > 65535)) throw new NumberFormatException();
      }
      catch (NumberFormatException nfexc)
      {
        out.println("\nThe port number must be a valid number between 1 - 65535.");
        printHelp();
        return;
      }
    }
    
    if (args.length > 2)
    {
      user = args[2];
    }
    
    if (args.length == 3)
    {
      out.println("\nIf a user name is provided a password must also be provided to login.");
      printHelp();
      return;
    }
    else if (args.length > 3)
    {
      password = args[3];
    }
    
    new OSGEyeConsole(host, port, user, password).readEvaluateRepeat();
  }
  
  static private void printHelp()
  {
    out.println(
        "\nThe OSGEye console takes the following four (in order) input arguments: host, port, user, password\n\n" +

        "    host:     This is the host name or IP address of the OSGEye server. This argument is required.\n" +
        "              If the console is started up without a host input argument it will prompt you to provide\n" +
        "              one.\n\n" +

        "    port:     The port number the OSGEye server is listening on. If not provided the default OSGEye port\n" +
        "              " + Constants.DEFAULT_PORT + " will be used.\n\n" +
        
        "    user:     The user name used to connect with the OSGEye server. If not provided anonymous access to the\n" +
        "              server will be assumed. If user is provided then a password must also be provided.\n\n" + 
        
        "    password: The password used to connect with the OSGEye server."
    );
  }
  
  
  private ConsoleReader consoleReader;
  private boolean inLoop;
  private NetworkServerIdentity serverId;
  private NetworkClient client;
  private String user;
  private List<AbstractCommand> commands;
  private Map<String, AbstractCommand> commandMap;
  private String prompt;
  private BundleStore bundleStore;
  private Object evaluateSynchronization;
  
  private OSGEyeConsole(String host, int port, String user, String password) throws Exception
  {
    out.println("OSGEye Console v" + Constants.VERSION);
    out.println("Loading bundles. This may take a second or two...");
    
    serverId = new NetworkServerIdentity(host, port);
    this.user = user;
    
    evaluateSynchronization = new Object();
    
    client = new NetworkClient(serverId, user, password);
    client.addClientListener(this);
    client.addServerListener(this);
    client.connect();

    prompt = (user == null) ? "" : (user + "@");
    prompt += serverId + "$ ";

    consoleReader = new ConsoleReader();
    consoleReader.setBellEnabled(false);
    consoleReader.setHistory(new History(new File(".release.history")));

    bundleStore = new BundleStore(client);
    bundleStore.loadBundles();

    out.println("Bundles are loaded. At anytime enter help for a list of available commands.");

    commands = createCommands();
    List<Pair<String, Completor>> completorsPair = new ArrayList<Pair<String, Completor>>();
    for (AbstractCommand command : commands)
    {
      completorsPair.add(new Pair<String, Completor>(command.getName(), command.getCompletor()));
    }
    consoleReader.addCompletor(new BranchCompletor(completorsPair));
    
    commandMap = new HashMap<String, AbstractCommand>();
    for (AbstractCommand command : commands)
    {
      commandMap.put(command.getName(), command);
    }
  }
  
  public void readEvaluateRepeat() throws IOException, InvalidCommandException
  {
    String line;
    inLoop = true;
    while (inLoop && ((line = consoleReader.readLine("\n" + prompt)) != null))
    {
      if (line.trim().length() == 0)
      {
        continue;
      }
      
      synchronized (evaluateSynchronization)
      {
        List<String> commands = new ArrayList<String>();
        String[] lineVals = line.split(" ");
        for (String lineVal : lineVals)
        {
          lineVal = lineVal.trim();
          if (lineVal.length() > 0) commands.add(lineVal);
        }
        
        out.println("");
        dispatchCommand(commands);
      }
    }
  }

  public void serverUpdate(ServerEvent event)
  {
    switch (event.getEventType())
    {
      case DISCONNECTED:
        err.println("\n\nLost connection to remote server. Shutting down now.");
        exit(-1);
        break;
    }
  }

  public void unexpectedClientError(NetworkClient client, String message, Exception exc)
  {
    err.println("An unexpected error occurred: " + message);
    if (exc != null) exc.printStackTrace();
  }

  private void dispatchCommand(List<String> commands)
  {
    String commandName = commands.remove(0);
    AbstractCommand command = commandMap.get(commandName);
    if (command != null)
    {
      try
      {
        command.execute(commands);
      }
      catch (InvalidCommandException icexc)
      {
        out.println(icexc.getMessage());
        out.println("Type 'help " + command.getName() + " for usage.");
      }
    }
    else
    {
      out.println("'" + commandName + "': unknown command");
      out.println("Type 'help' for usage.");
    }
  }

  private List<AbstractCommand> createCommands()
  {
    List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
    
    CommandUtils.bundleStore = bundleStore;
    CommandUtils.reader = consoleReader;

    commands.add(new SetCommand(bundleStore, client));
    commands.add(new StartBundlesCommand(client));
    commands.add(new StopBundlesCommand(client));
    commands.add(new BundleStatesCommand(bundleStore));
    commands.add(new InstallBundleCommand(client));
    commands.add(new UninstallBundlesCommand(client));
    commands.add(new RefreshPackagesCommand(client));
    commands.add(new ResolveBundlesCommand(client));
    commands.add(new NotificationsCommand(client, evaluateSynchronization));
    
    commands.add(new FrameworkCommand(bundleStore));
    commands.add(new BundlesCommand());
    commands.add(new ConfigurationsCommand(client));
    commands.add(new PackagesCommand(bundleStore));
    commands.add(new ListBundlesCommand(bundleStore, consoleReader));
    commands.add(new ManifestCommand());
    commands.add(new ServicesCommand(bundleStore));
    commands.add(new ImportsCommand(bundleStore));
    commands.add(new ExportsCommand(bundleStore));
    commands.add(new GraphCommand(bundleStore));

    commands.add(new UnresolvedCommand(bundleStore));
    commands.add(new MissingWiringCommand(bundleStore));

    commands.add(new ExitCommand());
    commands.add(new ClearCommand(consoleReader));
    commands.add(new HelpCommand(commands));
    commands.add(new ReloadCommand(bundleStore));

    return commands;
  }
}
