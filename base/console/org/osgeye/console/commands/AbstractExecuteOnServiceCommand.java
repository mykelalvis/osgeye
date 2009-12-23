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
import org.osgeye.domain.ServiceClass;

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
