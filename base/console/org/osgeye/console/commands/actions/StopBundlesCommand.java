package org.osgeye.console.commands.actions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.ArrayList;
import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import org.osgeye.client.NetworkClient;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.StopBundleOptions;

public class StopBundlesCommand extends AbstractExecuteOnBundlesCommand
{
  private NetworkClient networkClient;
  
  public StopBundlesCommand(NetworkClient networkClient)
  {
    this.networkClient = networkClient;
  }

  @Override
  public String getName()
  {
    return "stop";
  }

  @Override
  public String getShortDescription()
  {
    return "Stops the matching bundles.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.ACTIONS;
  }
  
  @Override
  protected boolean requireConfirmation()
  {
    return true;
  }
  
  @Override
  protected boolean displayMatchedBundles()
  {
    return true;
  }
  
  @Override
  protected boolean supportsOutToFile()
  {
    return false;
  }
  
  @Override
  public Completor[] getSubCompletors()
  {
    String[] bundleStates = new String[] {StopBundleOptions.STOP_TRANSIENT.getText()}; 
    return new Completor[] {new BundleNamesCompletor(bundleStore), new SimpleCompletor(bundleStates)}; 
  }
  
  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    StopBundleOptions stopOptions = null;
    if (subcommands.size() > 0)
    {
      String text = subcommands.remove(0);
      try
      {
        stopOptions = StopBundleOptions.fromText(text);
      }
      catch (IllegalArgumentException iaexc)
      {
        throw new InvalidCommandException("Invalid stop option " + text);
      }
    }
    
    assertEmpty(subcommands);
    
    List<Long> bundleIds = new ArrayList<Long>();
    for (Bundle bundle : matchingBundles) 
    { 
      bundleIds.add(bundle.getId());
    }
    
    try
    {
      printer.println("Sending stop request...");
      networkClient.stopBundles(bundleIds, stopOptions);
    }
    catch (Exception exc)
    {
      printer.println("Unable to stop bundles due to error " + exc.getMessage());
      exc.printStackTrace();
    }
  }

}
