package org.osgeye.client;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;
import org.osgeye.domain.Configuration;
import org.osgeye.domain.Framework;
import org.osgeye.domain.Service;
import org.osgeye.domain.ServiceClass;
import org.osgeye.domain.Version;
import org.osgeye.events.BundleEvent;
import org.osgeye.events.FrameworkEvent;
import org.osgeye.events.ServiceEvent;

public class ServerState implements BundleListener, ServiceListener, FrameworkListener
{
  private NetworkClient client;
  
  private Framework framework;
  private List<Bundle> bundles;
  private List<Configuration> configurations;
  private HashMap<Long, Bundle> bundleIdMap;
  private HashMap<String, List<Bundle>> bundleNameMap;
  private List<String> bundleNames;
  private List<Service> services;
  private List<ServiceClass> serviceClasses;
  private List<String> serviceClassNames;
  
  private boolean stateLoaded;
  
  private List<ServerStateListener> listeners;
  
  public ServerState(NetworkClient client)
  {
    this.client = client;
    client.addOsgiListener(this);
    
    listeners = new ArrayList<ServerStateListener>();
  }

  /**
   * Synchronously reloads the entire state from the server.
   * 
   * @throws ConnectException If the {@link NetworkClient} cannot connect to the server.
   * @throws IllegalStateException If the {@link NetworkClient} is not connected.
   * @throws RemoteServerException If an error occurs on the server side.
   */
  public synchronized void loadState() throws ConnectException, IllegalStateException, RemoteServerException
  {
    Framework frameworkCopy = client.getFramework();
    List<Bundle> bundlesCopy = client.getAllBundles();

    HashMap<Long, Bundle> bundleIdMapCopy = new HashMap<Long, Bundle>();
    HashMap<String, List<Bundle>> bundleNameMapCopy = new HashMap<String, List<Bundle>>();
    List<String> bundleNamesCopy = new ArrayList<String>();
    List<Service> servicesCopy = new ArrayList<Service>();
    List<ServiceClass> serviceClassesCopy = new ArrayList<ServiceClass>();
    List<String> serviceClassNamesCopy = new ArrayList<String>();
    
    for (Bundle bundle : bundlesCopy)
    {
      String bundleName = bundle.getSymbolicName();
      if (!bundleNamesCopy.contains(bundleName))
      {
        bundleNamesCopy.add(bundleName);
      }
      
      bundleIdMapCopy.put(bundle.getId(), bundle);
      
      List<Bundle> sameNameBundles;
      if (bundleNameMapCopy.containsKey(bundleName))
      {
        sameNameBundles = bundleNameMapCopy.get(bundleName);
      }
      else
      {
        sameNameBundles = new ArrayList<Bundle>();
        bundleNameMapCopy.put(bundleName, sameNameBundles);
      }
      sameNameBundles.add(bundle);
      
      for (Service service : bundle.getServices())
      {
        servicesCopy.add(service);
        for (ServiceClass serviceClass : service.getRegisteredClasses())
        {
          serviceClassesCopy.add(serviceClass);
          String className = serviceClass.getClassName();
          if (!serviceClassNamesCopy.contains(className))
          {
            serviceClassNamesCopy.add(className);
          }
        }
      }
    }
    
    List<Configuration> configurationsCopy = client.getConfigurations(null);
    
    Collections.sort(bundlesCopy);
    Collections.sort(bundleNamesCopy);
    Collections.sort(servicesCopy);
    Collections.sort(serviceClassesCopy);
    Collections.sort(serviceClassNamesCopy);
    
    framework = frameworkCopy;
    bundles = bundlesCopy;
    bundleNames = bundleNamesCopy;
    bundleIdMap = bundleIdMapCopy;
    bundleNameMap = bundleNameMapCopy;
    services = servicesCopy;
    serviceClasses = serviceClassesCopy;
    serviceClassNames = serviceClassNamesCopy;
    configurations = configurationsCopy;
    
    stateLoaded = true;
    
    notifyListenersOfUpdate();
  }
  
  public void addListener(ServerStateListener listener)
  {
    synchronized (listeners)
    {
      listeners.add(listener);
    }
  }

  public void removeListener(ServerStateListener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
  }
  
  /**
   * 
   * @return Current framework state.
   */
  public Framework getFramework()
  {
    assertStateLoaded();
    return framework;
  }

  /**
   * 
   * @param bundleId
   * @return The bundle whose id matches the given bundle id or null if no 
   * bundle's in the system have this id.
   */
  public Bundle getBundle(long bundleId)
  {
    assertStateLoaded();
    return bundleIdMap.get(bundleId);
  }

  /**
   * 
   * @param symbolicName
   * @param version
   * @return The matching bundle with the same given symbolic name and version
   * or <code>null</code> if no such bundle is found.
   */
  public Bundle getBundle(String symbolicName, Version version)
  {
    assertStateLoaded();
    
    for (Bundle bundle : getBundles(symbolicName))
    {
      if (bundle.getVersion().equals(version))
      {
        return bundle;
      }
    }
    
    return null;
  }

  /**
   * 
   * @param symbolicName
   * @return A list of bundles that have the given symbolicName. If no bundles
   * are found an empty list will be returned.
   */
  public List<Bundle> getBundles(String symbolicName)
  {
    assertStateLoaded();
    
    List<Bundle> bundles = new ArrayList<Bundle>();
    if (bundleNameMap.containsKey(symbolicName))
    {
      bundles.addAll(bundleNameMap.get(symbolicName));
    }
    
    return bundles;
  }

  /**
   * 
   * @return A sorted list copy of all the system's bundles.
   */
  public List<Bundle> getBundles()
  {
    assertStateLoaded();
    return new ArrayList<Bundle>(bundles);
  }


  /**
   * 
   * @param bundleId
   * @return A list of bundles that have a required dependency on the given bundle id.
   */
  public List<Bundle> getWiredBundles(Long bundleId)
  {
    assertStateLoaded();
    
    List<Bundle> wiredBundles = new ArrayList<Bundle>();
    for (Bundle bundle : getBundles())
    {
      if (bundle.getRequiredBundleIds().contains(bundleId))
      {
        wiredBundles.add(bundle);
      }
    }
    
    return wiredBundles;
  }
  
  /**
   * 
   * @return Sorted list of all the unique bundle names in the system.
   */
  public List<String> getBundleNames()
  {
    assertStateLoaded();
    return new ArrayList<String>(bundleNames);
  }
  
  /**
   * 
   * @param states Required states that the returned bundles must be in. If empty
   * or null all bundle names will be returned.
   * @return A list of bundle names that are in one of the given states.
   */
  public List<String> getBundleNames(List<BundleState> states)
  {
    assertStateLoaded();
    if ((states == null) || (states.size() == 0))
    {
      return getBundleNames();
    }
    else
    {
      List<String> bundleNames =  new ArrayList<String>();
      for (Bundle bundle : bundles)
      {
        if (states.contains(bundle.getState()))
        {
          bundleNames.add(bundle.getSymbolicName());
        }
      }
      Collections.sort(bundleNames);
      return bundleNames;
    }
  }

  /**
   * 
   * @return A map of all the bundles in the system keyed by the bundle id.
   */
  @SuppressWarnings("unchecked")
  public Map<Long, Bundle> getBundleIdMap()
  {
    assertStateLoaded();
    return (Map)bundleIdMap.clone();
  }

  /**
   * 
   * @return A sorted list copy of the services in the system.
   */
  public List<Service> getServices()
  {
    assertStateLoaded();
    return new ArrayList<Service>(services);
  }
  
  /**
   * 
   * @return A sorted list of all the service classes in the system.
   */
  public List<ServiceClass> getServiceClasses()
  {
    assertStateLoaded();
    return new ArrayList<ServiceClass>(serviceClasses);
  }

  /**
   * 
   * @return A sorted list of all the unique service class names in the
   * system.
   */
  public List<String> getServiceClassNames()
  {
    assertStateLoaded();
    return new ArrayList<String>(serviceClassNames);
  }
  
  /**
   * 
   * @return A sorted list copy of the configurations in the system.
   */
  public List<Configuration> getConfigurations()
  {
    assertStateLoaded();
    return new ArrayList<Configuration>(configurations);
  }

  public synchronized void bundleChanged(BundleEvent event, ServerIdentity serverId)
  {
    if (!stateLoaded) return;
    
    Bundle bundle = event.getBundle();
    
    switch (event.getEventType())
    {
      case UNINSTALLED:
        Bundle bundleToRemove = bundleIdMap.remove(event.getUninstalledBundleId());
        if (bundleToRemove != null)
        {
          bundles.remove(bundleToRemove);
          bundleNames.remove(bundleToRemove.getSymbolicName());
        }
        break;
      
      default:
        Bundle updatedBundle = bundleIdMap.remove(bundle.getId());
        if (updatedBundle != null)
        {
          bundles.remove(updatedBundle);
        }
        
        bundleIdMap.put(bundle.getId(), bundle);
        bundles.add(bundle);
        if (!bundleNames.contains(bundle.getSymbolicName()))
        {
          bundleNames.add(bundle.getSymbolicName());
        }
        break;
    }
    
    notifyListenersOfUpdate();
  }

  public synchronized void serviceChanged(ServiceEvent event, ServerIdentity serverId)
  {
    if (!stateLoaded) return;

    Service eventService = event.getService();
    
    switch (event.getEventType())
    {
      case REGISTERED:
        services.add(eventService);
        for (ServiceClass serviceClass : eventService.getRegisteredClasses())
        {
          serviceClasses.add(serviceClass);
          if (!serviceClassNames.contains(serviceClass.getClassName()))
          {
            serviceClassNames.add(serviceClass.getClassName());
          }
        }
        break;
        
      case MODIFIED:
        break;
      
      case UNREGISTERING:
        services.remove(eventService);
        for (ServiceClass serviceClass : eventService.getRegisteredClasses())
        {
          serviceClasses.remove(serviceClass);
          boolean classNameFound = false;
          String className = serviceClass.getClassName();
          for (ServiceClass sc : serviceClasses)
          {
            if (sc.getClassName().equals(className) && !sc.getService().equals(eventService))
            {
              classNameFound = true;
              break;
            }
          }
          
          if (!classNameFound)
          {
            serviceClassNames.remove(className);
          }
        }
        break;
    }
    
    Collections.sort(services);
    Collections.sort(serviceClasses);
    Collections.sort(serviceClassNames);
    notifyListenersOfUpdate();
  }

  public synchronized void frameworkStateChanged(FrameworkEvent event, ServerIdentity serverIdentity)
  {
    framework = event.getFramework();
    notifyListenersOfUpdate();
  }
  
  private void notifyListenersOfUpdate()
  {
    synchronized (listeners)
    {
      for (ServerStateListener listener : listeners) 
      {
        try
        {
          listener.serverStateUpdated();
        }
        catch (Exception exc)
        {
          exc.printStackTrace();
        }
      }
    }
  }
  
  private void assertStateLoaded()
  {
    if (!stateLoaded) throw new IllegalStateException("The server state has not been loaded.");
  }
}
