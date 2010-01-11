package org.osgeye.console.commands.actions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.List;

import org.osgeye.client.NetworkClient;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;

public class ResolveBundlesCommand extends AbstractExecuteOnBundlesCommand
{
  private NetworkClient networkClient;
  
  public ResolveBundlesCommand(NetworkClient networkClient)
  {
    super(BundleState.INSTALLED);
    this.networkClient = networkClient;
  }

  @Override
  public String getName()
  {
    return "resolve";
  }

  @Override
  public String getShortDescription()
  {
    return "Resolves the specified bundles that are currently in the INSTALLED state.";
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
      printer.println("Sending resolve bundle request...");
      boolean result = networkClient.resolveBundles(toBundleIds(matchingBundles));
      if (!result)
      {
        printer.println("Unable to resolve all bundles.");
      }
    }
    catch (Exception exc)
    {
      printer.println("Unable to uninstall bundles due to error " + exc.getMessage());
      exc.printStackTrace();
    }
  }
}
