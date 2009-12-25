package org.osgeye.console.commands.misc;

import static org.osgeye.console.commands.CommandUtils.*;
import java.util.List;

import org.osgeye.client.BundleStore;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;

public class ReloadCommand extends AbstractCommand
{
  private BundleStore bundleStore;
  
  public ReloadCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public String getName()
  {
    return "reload";
  }
  
  @Override
  protected boolean supportsOutToFile()
  {
    return false;
  }

  @Override
  public String getShortDescription()
  {
    return "Reloads all state stored by the console from the server.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.MISC;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertLength(subcommands, 0);
    
    printer.println("Reloading bundles. This may take a second or two...");
    try
    {
      bundleStore.loadBundles();
    }
    catch (Exception exc)
    {
      printer.println("Reload falied due to " + exc.getMessage());
      exc.printStackTrace();
    }
  }
}
