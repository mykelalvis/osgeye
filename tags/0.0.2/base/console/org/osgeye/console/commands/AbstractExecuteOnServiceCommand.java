package org.osgeye.console.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.Completor;
import jline.FileNameCompletor;
import jline.SimpleCompletor;

import org.osgeye.console.BundleStore;
import org.osgeye.console.completors.ServiceNamesCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.Service;

abstract public class AbstractExecuteOnServiceCommand extends AbstractCommand
{
  abstract protected void executeOnServices(List<String> matchedInterfaces, 
      Map<String, List<Service>> allServices, List<String> subcommands) throws InvalidCommandException;

  protected BundleStore bundleStore;
  
  public AbstractExecuteOnServiceCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public String getName()
  {
    return "services";
  }

  @Override
  public String getShortDescription()
  {
    return "List the matching service interfaces.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }
  
  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new ServiceNamesCompletor(bundleStore), 
        new SimpleCompletor(new String[] {">"}), new FileNameCompletor()};
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    String interfacePattern = (subcommands.size() == 0) ? ".*" : subcommands.remove(0);

    List<String> matchedInterfaces = new ArrayList<String>();
    Map<String, List<Service>> serviceMap = new HashMap<String, List<Service>>();
    
    for (Bundle bundle : bundleStore.getBundles())
    {
      for (Service service : bundle.getServices())
      {
        List<String> interfaces = service.getInterfaces();
        for (String serviceInterface : interfaces)
        {
          List<Service> services;
          if (serviceMap.containsKey(serviceInterface))
          {
            services = serviceMap.get(serviceInterface);
          }
          else
          {
            services = new ArrayList<Service>();
            serviceMap.put(serviceInterface, services);
          }
          services.add(service);
          
          
          if (serviceInterface.matches(interfacePattern) && !matchedInterfaces.contains(serviceInterface))
          {
            matchedInterfaces.add(serviceInterface);
          }
        }
      }
    }
    
    if (matchedInterfaces.size() == 0)
    {
      printer.println("No services match the given interface name pattern.");
    }
    else
    {
      Collections.sort(matchedInterfaces);
      executeOnServices(matchedInterfaces, serviceMap, subcommands);
    }
  }
}
