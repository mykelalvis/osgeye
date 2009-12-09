package org.osgeye.console.commands.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import org.osgeye.console.BundleStore;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;

public class BundleStatesCommand extends AbstractCommand
{
  private BundleStore bundleStore;
  
  public BundleStatesCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public String getName()
  {
    return "states";
  }

  @Override
  public String getShortDescription()
  {
    return "List each bundle under its bundle state.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }
  
  @Override
  public Completor[] getSubCompletors()
  {
    String[] bundleStates = new String[] {BundleState.UNINSTALLED.getName(), BundleState.RESOLVED.getName(), 
        BundleState.STOPING.getName(), BundleState.STARTING.getName(), BundleState.INSTALLED.getName(), 
        BundleState.ACTIVE.getName()};
    return new Completor[] {new SimpleCompletor(bundleStates)};
  }
  
  @Override
  protected boolean repeatLastCompletor()
  {
    return true;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    List<BundleState> bundleStates;
    
    if (subcommands.size() == 0)
    {
      bundleStates = Arrays.asList(BundleState.UNINSTALLED, BundleState.RESOLVED, BundleState.STOPING, BundleState.STARTING, BundleState.INSTALLED, BundleState.ACTIVE);
    }
    else
    {
      bundleStates = new ArrayList<BundleState>();
      for (String subcommand : subcommands)
      {
        try
        {
          bundleStates.add(BundleState.fromName(subcommand));
        }
        catch (IllegalArgumentException iaexc)
        {
          throw new InvalidCommandException("Invalid bundle state " + subcommand);
        }
      }
    }
    
    List<Bundle> bundles = bundleStore.getBundles();
    boolean firstPrint = true;
    for (int i = 0; i < bundleStates.size(); i++)
    {
      BundleState bundleState = bundleStates.get(i);
      List<Bundle> matchingBundles = new ArrayList<Bundle>();
      
      for (Bundle bundle : bundles)
      {
        if (bundle.getState() == bundleState)
        {
          matchingBundles.add(bundle);
        }
      }
      
      if (matchingBundles.size() > 0)
      {
        Collections.sort(matchingBundles);
        if (!firstPrint) printer.println();
        firstPrint = false;
        printer.println(bundleState.getName());
        printer.pushIndent();
        for (Bundle matchingBundle : matchingBundles)
        {
          printer.println(matchingBundle + (matchingBundle.isFragment() ? " (fragment)" : ""));
        }
        
        printer.popupIndent();
      }
    }
  }
}
