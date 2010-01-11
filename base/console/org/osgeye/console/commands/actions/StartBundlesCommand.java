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
import org.osgeye.console.completors.VersionRangeCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.StartBundleOptions;

public class StartBundlesCommand extends AbstractExecuteOnBundlesCommand
{
  private NetworkClient networkClient;
  
  public StartBundlesCommand(NetworkClient networkClient)
  {
    this.networkClient = networkClient;
  }

  @Override
  public String getName()
  {
    return "start";
  }

  @Override
  public String getShortDescription()
  {
    return "Starts the matching bundles.";
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
    String[] bundleStates = new String[] {StartBundleOptions.START_ACTIVATION_POLICY.getText(), 
        StartBundleOptions.START_TRANSIENT.getText()}; 

    return new Completor[] {new BundleNamesCompletor(bundleStore), 
        new VersionRangeCompletor(), new SimpleCompletor(bundleStates)}; 
  }

  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    StartBundleOptions startOptions = null;
    if (subcommands.size() > 0)
    {
      String text = subcommands.remove(0);
      try
      {
        startOptions = StartBundleOptions.fromText(text);
      }
      catch (IllegalArgumentException iaexc)
      {
        throw new InvalidCommandException("Invalid start option " + text);
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
      printer.println("Sending start request...");
      networkClient.startBundles(bundleIds, startOptions);
    }
    catch (Exception exc)
    {
      printer.println("Unable to start bundles due to error " + exc.getMessage());
      exc.printStackTrace();
    }
  }
}
