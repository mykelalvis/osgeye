package org.osgeye.console.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgeye.console.BundleStore;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.Service;
import org.osgeye.domain.ServiceClass;

abstract public class AbstractExecuteOnPackagesCommand extends AbstractCommand
{
  abstract protected void executeOnServices(List<String> matchedInterfaces, 
      Map<String, List<Service>> allServices, List<String> subcommands) throws InvalidCommandException;

  protected BundleStore bundleStore;
  
  public AbstractExecuteOnPackagesCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }
  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }
  
  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    String interfacePattern = (subcommands.size() == 0) ? ".*" : subcommands.remove(0);
    if (interfacePattern.equals("*")) interfacePattern = ".*";

    List<String> matchedInterfaces = new ArrayList<String>();
    Map<String, List<Service>> serviceMap = new HashMap<String, List<Service>>();
    
    for (Bundle bundle : bundleStore.getBundles())
    {
      for (Service service : bundle.getServices())
      {
        List<ServiceClass> serviceClasses = service.getRegisteredClasses();
        for (ServiceClass serviceClass : serviceClasses)
        {
          String className = serviceClass.getClassName();
          List<Service> services;
          if (serviceMap.containsKey(serviceClass))
          {
            services = serviceMap.get(serviceClass);
          }
          else
          {
            services = new ArrayList<Service>();
            serviceMap.put(className, services);
          }
          services.add(service);
          
          
          if (className.matches(interfacePattern) && !matchedInterfaces.contains(className))
          {
            matchedInterfaces.add(className);
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
