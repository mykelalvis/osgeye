package org.osgeye.console.commands;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.ArrayList;
import java.util.List;

import jline.Completor;

import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.console.completors.VersionRangeCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;

abstract public class AbstractExecuteOnBundlesCommand extends AbstractCommand
{
  abstract protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException;
  
  private List<BundleState> states;
  
  public AbstractExecuteOnBundlesCommand()
  {}

  public AbstractExecuteOnBundlesCommand(BundleState... states)
  {
    if (states != null)
    {
      this.states = new ArrayList<BundleState>();
      for (BundleState state : states) this.states.add(state);
    }
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    BundleNamesCompletor namesCompletor;
    if (states == null)
    {
      namesCompletor = new BundleNamesCompletor(bundleStore);
    }
    else
    {
      namesCompletor = new BundleNamesCompletor(bundleStore, states);
    }
    
    return new Completor[] {namesCompletor, new VersionRangeCompletor()};
  }
  
  /**
   * Extending classes can override this so that all matched bundles will be
   * printed to the screen before the command is executed.
   */
  protected boolean displayMatchedBundles()
  {
    return false;
  }
  
  /**
   * Extending bundles can override this to require a confirmation from the user
   * before the command is executed. 
   */
  protected boolean requireConfirmation()
  {
    return false;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    List<Bundle> matchingBundles = findMatchingBundles(subcommands, getName(), displayMatchedBundles(), requireConfirmation(), states);
    if (matchingBundles != null)
    {
      executeOnBundles(matchingBundles, subcommands);
    }
  }

}
