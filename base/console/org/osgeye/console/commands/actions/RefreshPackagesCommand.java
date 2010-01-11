package org.osgeye.console.commands.actions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.List;

import org.osgeye.client.NetworkClient;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;

public class RefreshPackagesCommand extends AbstractExecuteOnBundlesCommand
{
  private NetworkClient networkClient;
  
  public RefreshPackagesCommand(NetworkClient networkClient)
  {
    this.networkClient = networkClient;
  }

  @Override
  public String getName()
  {
    return "refreshpacks";
  }

  @Override
  public String getShortDescription()
  {
    return "Forces the update (replacement) or removal of packages exported by the specified bundles.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.ACTIONS;
  }
  
  @Override
  protected boolean displayMatchedBundles()
  {
    return true;
  }

  @Override
  protected boolean requireConfirmation()
  {
    return true;
  }
  
  protected boolean supportsOutToFile()
  {
    return false;
  }

  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    assertEmpty(subcommands);
    
    try
    {
      printer.println("Sending refresh packages request...");
      networkClient.refreshPackages(toBundleIds(matchingBundles));
    }
    catch (Exception exc)
    {
      printer.println("Unable to uninstall bundles due to error " + exc.getMessage());
      exc.printStackTrace();
    }
  }
}
