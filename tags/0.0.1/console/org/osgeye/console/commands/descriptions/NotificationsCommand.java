package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import org.osgeye.client.BundleListener;
import org.osgeye.client.FrameworkListener;
import org.osgeye.client.ServiceListener;
import org.osgeye.client.network.NetworkClient;
import org.osgeye.client.network.NetworkServerIdentity;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Service;
import org.osgeye.events.BundleEvent;
import org.osgeye.events.FrameworkEvent;
import org.osgeye.events.ServiceEvent;

public class NotificationsCommand extends AbstractCommand implements BundleListener, 
    ServiceListener, FrameworkListener
{
  static public final String SUB_BUNDLES = "bundles";
  
  static public final String SUB_SERVICES = "services";
  
  static public final String SUB_FRAMEWORK = "framework";
  
  private NetworkClient client;
  private Object evaluteSynchronization;
  
  private boolean printBundles;
  private boolean printServices;
  private boolean printFramework;
  
  public NotificationsCommand(NetworkClient client, Object evaluteSynchronization)
  {
    this.client = client;
    this.evaluteSynchronization = evaluteSynchronization;
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }

  @Override
  public String getName()
  {
    return "notifications";
  }

  @Override
  public String getShortDescription()
  {
    return "Prints OSGi notifications to the console.";
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new SimpleCompletor(new String[] {"on", "off"}),
        new SimpleCompletor(new String[] {SUB_BUNDLES, SUB_SERVICES, SUB_FRAMEWORK})};
  }


  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    String onOff = assertNextValue(subcommands, "on", "off");

    client.removeOsgiListener(this);
    
    printBundles = false;
    printFramework = false;
    printServices = false;
    
    if ("on".equalsIgnoreCase(onOff))
    {
      if (subcommands.size() == 0)
      {
        printBundles = true;
        printFramework = true;
        printServices = true;
        
        client.addOsgiListener(this);
      }
      else
      {
        assertAllValues(subcommands, SUB_BUNDLES, SUB_SERVICES, SUB_FRAMEWORK);
        
        while (!subcommands.isEmpty())
        {
          String nextSubcommand = subcommands.remove(0);
          if (SUB_BUNDLES.equalsIgnoreCase(nextSubcommand))
          {
            printBundles = true;
          }
          else if (SUB_SERVICES.equalsIgnoreCase(nextSubcommand))
          {
            printServices = true;
          }
          else if (SUB_FRAMEWORK.equalsIgnoreCase(nextSubcommand))
          {
            printFramework = true;
          }
        }
        
        if (printBundles || printServices || printFramework)
        {
          client.addOsgiListener(this);
        }
      }
    }
  }

  public void bundleChanged(BundleEvent event, NetworkServerIdentity serverId)
  {
    synchronized (evaluteSynchronization)
    {
      switch (event.getEventType())
      {
        case UNINSTALLED:
          notification("Bundle " + event.getUninstalledBundleName() + " (" + event.getUninstalledBundleId() + ") uninstalled.");
          break;
          
        default:
          notification("Bundle " + event.getBundle() + " " + event.getEventType().getText().toLowerCase() + ".");
          break;
      }
    }
  }

  public void serviceChanged(ServiceEvent event, NetworkServerIdentity serverId)
  {
    if (printServices)
    {
      Service service = event.getService();
      notification("Service " + service + " for bundle " + service.getBundle() + " " + event.getEventType().getText().toLowerCase() + ".");
    }
  }

  public void frameworkStateChanged(FrameworkEvent event, NetworkServerIdentity serverIdentity)
  {
    if (printFramework)
    {
      if (event.getValue() == null)
      {
        notification("Framework event " + event.getEventType().getText().toString());
      }
      else
      {
        notification("Framework event " + event.getEventType().getText().toString() + " value = " + event.getValue());
      }
    }
  }
  
  protected void notification(String message)
  {
    synchronized (evaluteSynchronization)
    {
      printer.pushIndent();
      printer.println();
      printer.println("Notification: " + message);
      printer.popupIndent();
    }
  }
}
