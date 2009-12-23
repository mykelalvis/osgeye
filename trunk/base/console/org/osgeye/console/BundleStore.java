package org.osgeye.console;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgeye.client.BundleListener;
import org.osgeye.client.FrameworkListener;
import org.osgeye.client.RemoteServerException;
import org.osgeye.client.ServiceListener;
import org.osgeye.client.network.NetworkClient;
import org.osgeye.client.network.NetworkServerIdentity;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;
import org.osgeye.domain.FrameworkState;
import org.osgeye.domain.Service;
import org.osgeye.domain.ServiceClass;
import org.osgeye.events.BundleEvent;
import org.osgeye.events.FrameworkEvent;
import org.osgeye.events.ServiceEvent;

public class BundleStore implements BundleListener, ServiceListener, FrameworkListener
{
  private NetworkClient client;
  
  
  private FrameworkState frameworkState;
  private List<Bundle> bundles;
  private HashMap<Long, Bundle> bundleMap;
  private List<String> bundleNames;
  private List<String> serviceInterfaces;
  
  private List<BundleStoreListener> listeners;
  
  public BundleStore(NetworkClient client)
  {
    this.client = client;
    client.addOsgiListener(this);
    
    listeners = new ArrayList<BundleStoreListener>();
    bundleMap = new HashMap<Long, Bundle>();
    bundleNames = new ArrayList<String>();
    serviceInterfaces = new ArrayList<String>();
  }
  
  public void addListener(BundleStoreListener listener)
  {
    listeners.add(listener);
  }
  
  public void loadBundles() throws ConnectException, IllegalStateException, RemoteServerException
  {
    frameworkState = client.getFrameworkState();
    List<Bundle> bundlesCopy = client.getAllBundles();
    synchronized(this)
    {
      bundles = bundlesCopy;
      Collections.sort(bundles);
      bundleMap.clear();
      serviceInterfaces.clear();
      for (Bundle bundle : bundles)
      {
        String bundleName = bundle.getSymbolicName();
        if (!bundleNames.contains(bundleName))
        {
          bundleNames.add(bundleName);
        }
        bundleMap.put(bundle.getId(), bundle);
        for (Service service : bundle.getServices())
        {
          for (ServiceClass serviceClass : service.getRegisteredClasses())
          {
            String className = serviceClass.getClassName();
            if (!serviceInterfaces.contains(className))
            {
              serviceInterfaces.add(className);
            }
          }
        }
      }
      Collections.sort(bundleNames);
      Collections.sort(serviceInterfaces);
    }
    
    notifyListeners();
  }
  
  public synchronized FrameworkState getFrameworkState()
  {
    return frameworkState;
  }

  public synchronized Bundle getBundle(long bundleId)
  {
    return bundleMap.get(bundleId);
  }
  
  public synchronized List<Bundle> getBundles()
  {
    return new ArrayList<Bundle>(bundles);
  }
  
  public synchronized List<String> getBundleNames()
  {
    return new ArrayList<String>(bundleNames);
  }
  
  public synchronized List<String> getBundleNames(List<BundleState> states)
  {
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

  public synchronized List<String> getServiceInterfaces()
  {
    return new ArrayList<String>(serviceInterfaces);
  }
  
  public synchronized Map<Long, Bundle> getBundleMap()
  {
    return (Map)bundleMap.clone();
  }

  public synchronized void bundleChanged(BundleEvent event, NetworkServerIdentity serverId)
  {
    if (bundles == null) return;
    
    Bundle bundle = event.getBundle();
    switch (event.getEventType())
    {
      case UNINSTALLED:
        Bundle bundleToRemove = bundleMap.remove(event.getUninstalledBundleId());
        if (bundleToRemove != null)
        {
          bundles.remove(bundleToRemove);
          bundleNames.remove(bundleToRemove.getSymbolicName());
        }
        break;
      
      default:
        Bundle updatedBundle = bundleMap.remove(bundle.getId());
        if (updatedBundle != null)
        {
          bundles.remove(updatedBundle);
        }
        
        bundleMap.put(bundle.getId(), bundle);
        bundles.add(bundle);
        if (!bundleNames.contains(bundle.getSymbolicName()))
        {
          bundleNames.add(bundle.getSymbolicName());
        }
        break;
    }
    
    notifyListeners();
  }

  public synchronized void serviceChanged(ServiceEvent event, NetworkServerIdentity serverId)
  {
    List<ServiceClass> serviceClasses = event.getService().getRegisteredClasses();
    
    switch (event.getEventType())
    {
      case UNREGISTERING:
        serviceInterfaces.removeAll(serviceClasses);
        break;
        
      default:
        for (ServiceClass serviceClass : serviceClasses)
        {
          String className = serviceClass.getClassName();
          if (!serviceInterfaces.contains(className))
          {
            serviceInterfaces.add(className);
          }
        }
        break;
    }
    
    notifyListeners();
  }

  public synchronized void frameworkStateChanged(FrameworkEvent event, NetworkServerIdentity serverIdentity)
  {
    switch (event.getEventType())
    {
      case START_LEVEL_CHANGED:
        int startLevel = ((Number)event.getValue()).intValue();
        frameworkState = new FrameworkState(startLevel, frameworkState.getInitialBundleStartLevel());
        break;
        
      case BUNDLE_INITIAL_START_LEVEL_CHANGED:
        int bundleIntitalLevel = ((Number)event.getValue()).intValue();
        frameworkState = new FrameworkState(frameworkState.getStartLevel(), bundleIntitalLevel);
        break;
    }
    notifyListeners();
  }
  
  private void notifyListeners()
  {
    for (BundleStoreListener listener : listeners) 
    {
      listener.bundleStoreUpdated();
    }
  }
}
