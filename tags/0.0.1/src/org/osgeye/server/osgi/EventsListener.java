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
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class EventsListener implements BundleListener, ServiceListener, FrameworkListener
{
  private PackageAdmin packageAdmin;
  private StartLevel startLevelService;
  private EventDispatcher eventDispatcher;
  
  private BundleCreator bundleCreator;
  
  public EventsListener(PackageAdmin packageAdmin, StartLevel startLevelService, EventDispatcher eventDispatcher)
  {
    this.packageAdmin = packageAdmin;
    this.startLevelService = startLevelService;
    this.eventDispatcher = eventDispatcher;
    
    bundleCreator = new BundleCreator(packageAdmin, startLevelService);
  }
  
  public void start(BundleContext context)
  {
    context.addBundleListener(this);
    context.addServiceListener(this);
    context.addFrameworkListener(this);
  }
  
  public void stop(BundleContext context)
  {
    context.removeBundleListener(this);
    context.removeServiceListener(this);
    context.removeFrameworkListener(this);
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
    Service service = new Service(bundle, serviceRef);
    ServiceEventType eventType = ServiceEventType.fromOsgiValue(event.getType());
    eventDispatcher.dispatchEvent(new ServiceEvent(service, eventType));
  }

  public void frameworkEvent(org.osgi.framework.FrameworkEvent event)
  {    
    FrameworkEventType eventType = FrameworkEventType.fromOsgiValue(event.getType());
    Object source = event.getSource();
    Object value;
    switch (eventType)
    {
      case START_LEVEL_CHANGED:
        value = startLevelService.getStartLevel();
        break;
        
      default:
        value = (source == null) ? null : source.toString();
        break;
    }
    eventDispatcher.dispatchEvent(new FrameworkEvent(eventType, value));
  }
}
