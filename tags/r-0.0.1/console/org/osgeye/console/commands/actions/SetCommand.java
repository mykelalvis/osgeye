package org.osgeye.console.commands.actions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.ArrayList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.NullCompletor;

import org.osgeye.client.network.NetworkClient;
import org.osgeye.console.BundleStore;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.BranchCompletor;
import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.console.completors.VersionRangeCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.utils.Pair;

public class SetCommand extends AbstractCommand
{
  static public final String CMD_START_LEVEL = "startlevel";
  static public final String CMD_INIT_BUNDLE_LEVEL = "initbundlelevel";
  static public final String CMD_BUNDLE_LEVEL = "bundlelevel";
  
  private BundleStore bundleStore;
  private NetworkClient networkClient;
  
  public SetCommand(BundleStore bundleStore, NetworkClient networkClient)
  {
    this.bundleStore = bundleStore;
    this.networkClient = networkClient;
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.ACTIONS;
  }

  @Override
  public String getName()
  {
    return "set";
  }

  @Override
  public String getShortDescription()
  {
    return "Sets OSGi framework and bundle properties.";
  }
  
  @Override
  protected boolean supportsOutToFile()
  {
    return false;
  }
  
  @Override
  protected Completor[] getSubCompletors()
  {
    List<Pair<String, Completor>> branchedCompletors = new ArrayList<Pair<String,Completor>>();
    
    ArgumentCompletor bundleLevelCompletor = new ArgumentCompletor(
        new Completor[] {new NullCompletor(), new BundleNamesCompletor(bundleStore), new VersionRangeCompletor()});
    bundleLevelCompletor.setStrict(false);
    
    branchedCompletors.add(new Pair<String, Completor>(CMD_START_LEVEL, new NullCompletor()));
    branchedCompletors.add(new Pair<String, Completor>(CMD_INIT_BUNDLE_LEVEL, new NullCompletor()));
    branchedCompletors.add(new Pair<String, Completor>(CMD_BUNDLE_LEVEL, bundleLevelCompletor));
    
    return new Completor[] {new BranchCompletor(branchedCompletors)};
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertMinLength(subcommands, 1);
    
    String type = subcommands.remove(0);
    
    if (CMD_START_LEVEL.equals(type))
    {
      setStartLevel(subcommands);
    }
    else if (CMD_INIT_BUNDLE_LEVEL.equals(type))
    {
      setInitBundleStartLevel(subcommands);
    }
    else if (CMD_BUNDLE_LEVEL.equals(type))
    {
      setBundlesLevel(subcommands);
    }
    else
    {
      throw new InvalidCommandException("Invalid set subcommand " + type);
    }
  }
  
  private void setStartLevel(List<String> subcommands) throws InvalidCommandException
  {
    assertLength(subcommands, 1);
    int startLevel = parseStartLevel(subcommands);
    
    if (confirmation("Confirm setting start level to " + startLevel))
    {
      printer.println("Setting start level...");
      try
      {
        networkClient.setStartLevel(startLevel);
      }
      catch (Exception exc)
      {
        printer.println("Unable to set start level due to error: " + exc.getMessage());
        exc.printStackTrace();
      }
    }
  }
  
  private void setInitBundleStartLevel(List<String> subcommands) throws InvalidCommandException
  {
    assertLength(subcommands, 1);
    int startLevel = parseStartLevel(subcommands);
    
    if (confirmation("Confirm setting initial bundle start level to " + startLevel))
    {
      printer.println("Setting initial bundle start level...");
      try
      {
        networkClient.setInitialBundleStartLevel(startLevel);
      }
      catch (Exception exc)
      {
        printer.println("Unable to set start initial bundle start level due to error: " + exc.getMessage());
        exc.printStackTrace();
      }
    }
  }
  
  private void setBundlesLevel(List<String> subcommands) throws InvalidCommandException
  {
    assertMinMaxLength(subcommands, 2, 3);
    int startLevel = parseStartLevel(subcommands);
    
    String confirmation = "setting start level to " + startLevel;
    List<Bundle> matchingBundles = findMatchingBundles(subcommands, confirmation, true, true, null);
    
    if (matchingBundles != null)
    {
      printer.println("Setting bundles start level...");
      try
      {
        networkClient.setBundlesStartLevel(startLevel, toBundleIds(matchingBundles));
      }
      catch (Exception exc)
      {
        printer.println("Unable to bundles start level due to error: " + exc.getMessage());
        exc.printStackTrace();
      }
    }
  }
  
  private int parseStartLevel(List<String> subcommands) throws InvalidCommandException
  {
    String startLevelStr = subcommands.remove(0);
    try
    {
      return Integer.parseInt(startLevelStr);
    }
    catch (NumberFormatException nfexc)
    {
      throw new InvalidCommandException("Invalid start level " + startLevelStr + " must be an integer value.");
    }
  }
}
