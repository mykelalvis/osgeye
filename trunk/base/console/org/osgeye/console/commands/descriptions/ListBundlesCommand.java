package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jline.Completor;
import jline.ConsoleReader;

import org.osgeye.client.BundleStore;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.BranchCompletor;
import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.console.completors.ServiceNamesCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.utils.Pair;

public class ListBundlesCommand extends AbstractCommand
{
  private BundleStore bundleStore;
  private ConsoleReader reader;
  
  public ListBundlesCommand(BundleStore bundleStore, ConsoleReader reader)
  {
    this.bundleStore = bundleStore;
    this.reader = reader;
  }

  @Override
  public String getName()
  {
    return "ls";
  }

  @Override
  public String getShortDescription()
  {
    return "List the matching bundle or service names.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }
  
  protected boolean supportsOutToFile()
  {
    return false;
  }
  
  @Override
  protected Completor[] getSubCompletors()
  {
    List<Pair<String, Completor>> branchedCompletors = new ArrayList<Pair<String,Completor>>();
    
    branchedCompletors.add(new Pair<String, Completor>("bundles", new BundleNamesCompletor(bundleStore)));
    branchedCompletors.add(new Pair<String, Completor>("services", new ServiceNamesCompletor(bundleStore)));
    
    return new Completor[] {new BranchCompletor(branchedCompletors)};
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    boolean lsBundles = true;
    if (subcommands.size() > 0)
    {
      String lsType = subcommands.remove(0);
      
      if (lsType.equals("bundles"))
      {
        lsBundles = true;
      }
      else if (lsType.equals("services"))
      {
        lsBundles = false;
      }
      else
      {
        throw new InvalidCommandException("Invalid ls type " + lsType);
      }
    }
    
    String pattern = (subcommands.size() == 0) ? ".*" : subcommands.remove(0);
    if (pattern.equals("*")) pattern = ".*";
    
    assertEmpty(subcommands);

    List<String> values = new ArrayList<String>();
    if (lsBundles)
    {
      for (Bundle bundle : bundleStore.getBundles())
      {
        if (bundle.getSymbolicName().matches(pattern))
        {
          values.add(bundle.toString());
        }
      }
    }
    else
    {
      for (String serviceInterface : bundleStore.getServiceInterfaces())
      {
        if (serviceInterface.matches(pattern))
        {
          values.add(serviceInterface);
        }
      }
    }
    
    try
    {
      reader.printColumns(values);
    }
    catch (IOException ioexc)
    {}
  }
}
