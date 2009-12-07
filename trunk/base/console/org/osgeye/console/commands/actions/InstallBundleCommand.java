package org.osgeye.console.commands.actions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.io.File;
import java.util.List;

import jline.Completor;
import jline.FileNameCompletor;

import org.osgeye.client.network.NetworkClient;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;

public class InstallBundleCommand extends AbstractCommand
{
  private NetworkClient client;
  
  public InstallBundleCommand(NetworkClient client)
  {
    this.client = client;
  }

  @Override
  public String getName()
  {
    return "install";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.ACTIONS;
  }

  @Override
  public String getShortDescription()
  {
    return "Installs a bundle on the remote server.";
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new FileNameCompletor()};
  }
  
  protected boolean supportsOutToFile()
  {
    return false;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    String bundleFilePath = drain(subcommands);
    File bundleFile = new File(bundleFilePath);
    
    if (!bundleFile.exists())
    {
      throw new InvalidCommandException("Invalid bundle file path " + bundleFilePath);
    }

    if (confirmation("Confirm bundle installation " + bundleFile.getAbsolutePath()))
    {
      try
      {
        printer.println("Sending bundle to install...");
        Bundle bundle = client.installBundle(bundleFile);
        printer.println("Bundle " + bundle + " installed.");
      }
      catch (Exception exc)
      {
        printer.println("Unable to install bundles due to error " + exc.getMessage());
        exc.printStackTrace();
      }
    }
  }
}
