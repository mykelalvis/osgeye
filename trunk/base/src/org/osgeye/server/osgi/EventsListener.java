package org.osgeye.server.osgi;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.Service;
import org.osgeye.events.BundleEvent;
import org.osgeye.events.FrameworkEvent;
import org.osgeye.events.ServiceEvent;
import org.osgeye.events.BundleEvent.BundleEventType;
import org.osgeye.events.FrameworkEvent.FrameworkEventType;
import org.osgeye.events.ServiceEvent.ServiceEventType;
import org.osgeye.server.EventDispatcher;
import org.osgeye.server.osgi.utils.BundleCreator;
import org.osgeye.server.osgi.utils.FrameworkCreator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class EventsListener implements BundleListener, ServiceListener, FrameworkListener
{
  private BundleContext osgeyeContext;
  private PackageAdmin packageAdmin;
  private StartLevel startLevelService;
  private EventDispatcher eventDispatcher;
  
  private BundleCreator bundleCreator;
  private FrameworkCreator frameworkCreator;
  
  public EventsListener(BundleContext osgeyeContext, PackageAdmin packageAdmin, StartLevel startLevelService, EventDispatcher eventDispatcher)
  {
    this.osgeyeContext = osgeyeContext;
    this.packageAdmin = packageAdmin;
    this.startLevelService = startLevelService;
    this.eventDispatcher = eventDispatcher;
    
    bundleCreator = new BundleCreator(packageAdmin, startLevelService);
    frameworkCreator = new FrameworkCreator(osgeyeContext, packageAdmin, startLevelService);
  }
  
  public void start()
  {
    osgeyeContext.addBundleListener(this);
    osgeyeContext.addServiceListener(this);
    osgeyeContext.addFrameworkListener(this);
  }
  
  public void stop()
  {
    osgeyeContext.removeBundleListener(this);
    osgeyeContext.removeServiceListener(this);
    osgeyeContext.removeFrameworkListener(this);
  }

  public void bundleChanged(org.osgi.framework.BundleEvent event)
  {
    int osgiEventType = event.getType();
    org.osgi.framework.Bundle osgiBundle = event.getBundle();
    
    BundleEvent bundleEvent;
    switch (osgiEventType)
    {
      case org.osgi.framework.BundleEvent.UNINSTALLED:
        bundleEvent = new BundleEvent(osgiBundle.getBundleId(), osgiBundle.getSymbolicName(), BundleEventType.UNINSTALLED);
        break;
        
      default:
        bundleEvent = new BundleEvent(bundleCreator.createBundle(osgiBundle), BundleEventType.fromOsgiValue(osgiEventType));
        break;
    }

    eventDispatcher.dispatchEvent(bundleEvent);
  }

  public void serviceChanged(org.osgi.framework.ServiceEvent event)
  {
    ServiceReference serviceRef = event.getServiceReference();
    org.osgi.framework.Bundle osgiBundle = serviceRef.getBundle();
    Bundle bundle = bundleCreator.createBundle(osgiBundle);
    Service service = new Service(bundle, serviceRef, osgiBundle);
    ServiceEventType eventType = ServiceEventType.fromOsgiValue(event.getType());
    eventDispatcher.dispatchEvent(new ServiceEvent(service, eventType));
  }

  public void frameworkEvent(org.osgi.framework.FrameworkEvent event)
  {    
    FrameworkEventType eventType = FrameworkEventType.fromOsgiValue(event.getType());
    Bundle bundle = (event.getBundle() == null) ? null : bundleCreator.createBundle(event.getBundle());
    Throwable osgiError = event.getThrowable();
    Exception error = null;
    
    if (osgiError != null)
    {
      error = new Exception(osgiError.getMessage());
      error.setStackTrace(osgiError.getStackTrace());
    }
    
    eventDispatcher.dispatchEvent(new FrameworkEvent(eventType, frameworkCreator.createFramework(), bundle, error));
  }
}
