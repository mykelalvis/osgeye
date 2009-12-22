package org.osgeye.server.jmx;

import static java.lang.String.*;
import static org.osgi.framework.Constants.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.osgeye.utils.OSGiUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBeanManager implements BundleListener, ServiceListener, ConfigurationListener
{
  static public final String DOMAIN = "org.osgeye";

  static public final String FRAMEWORK_NAME = DOMAIN + ":Name=Framework";
  
  static public final String BUNDLE_NAME_TEMPLATE = DOMAIN + ":Type=Bundles,Name=%s";

  static public final String SERVICE_NAME_TEMPLATE = DOMAIN + ":Type=Services,Interface=%s,Bundle=%s,Id=%s";

  static public final String CONFIGURATION_NAME_TEMPLATE = DOMAIN + ":Type=Configuration,Pid=%s";

  private Bundle osgeyeBundle;
  
  private StartLevel startLevel;
  
  private PackageAdmin packageAdmin;
  
  private ConfigurationAdmin configAdmin;
  private Logger logger;
  
  private ServiceRegistration configListenerRegistration;
  
  private Map<String, BundleMBeanImpl> registeredBundles;
  private Map<String, ServiceMBeanImpl> registeredServices;
  private Map<String, ConfigurationMBeanImpl> registeredConfiguration;
  private FrameworkMBeanImpl registeredFramework;
  private MBeanServer server;
  
  public MBeanManager(Bundle osgeyeBundle, StartLevel startLevel, PackageAdmin packageAdmin, ConfigurationAdmin configAdmin)
  {
    this.osgeyeBundle = osgeyeBundle;
    this.startLevel = startLevel;
    this.packageAdmin = packageAdmin;
    this.configAdmin = configAdmin;
    
    logger = LoggerFactory.getLogger(getClass());
  }
  
  public void start()
  {
    registeredBundles = new HashMap<String, BundleMBeanImpl>();
    registeredServices = new HashMap<String, ServiceMBeanImpl>();
    registeredConfiguration = new HashMap<String, ConfigurationMBeanImpl>();
    server = ManagementFactory.getPlatformMBeanServer();

    try
    {
      registerFramework();
      
      BundleContext context = osgeyeBundle.getBundleContext();
      Bundle[] bundles = context.getBundles();
      for (Bundle bundle : bundles)
      {
        registerBundle(bundle);
      }
      
      ServiceReference[] serviceRefs = context.getAllServiceReferences(null, null);
      if (serviceRefs != null)
      {
        for (ServiceReference serviceRef : serviceRefs)
        {
          registerService(serviceRef);
        }
      }
      
      Configuration[] configurations = configAdmin.listConfigurations(null);
      if (configurations != null)
      {
        for (Configuration configuration : configurations)
        {
          registerConfiguration(configuration);
        }
      }
      
      context.addBundleListener(this);
      context.addServiceListener(this);
      configListenerRegistration = context.registerService(ConfigurationListener.class.getName(), this, null);
    }
    catch (IOException ioexc)
    {
      throw new RuntimeException(ioexc);
    }
    catch (InvalidSyntaxException isexc)
    {
      throw new RuntimeException(isexc);
    }
  }
  
  public void stop()
  {
    BundleContext context = osgeyeBundle.getBundleContext();
    context.removeBundleListener(this);
    context.removeServiceListener(this);
    configListenerRegistration.unregister();
    configListenerRegistration = null;
    
    try
    {
      server.unregisterMBean(new ObjectName(FRAMEWORK_NAME));
    }
    catch (Exception exc)
    {
      logger.error("Unable to unregister framework mbean with object name: " + FRAMEWORK_NAME, exc);
    }
    registeredFramework = null;
    
    for (Entry<String, BundleMBeanImpl> entry : registeredBundles.entrySet())
    {
      try
      {
        server.unregisterMBean(new ObjectName(entry.getKey()));
      }
      catch (Exception exc)
      {
        logger.error("Unable to unregister bundle mbean with object name: " + entry.getKey(), exc);
      }
    }
    registeredBundles = null;
    
    for (Entry<String, ServiceMBeanImpl> entry : registeredServices.entrySet())
    {
      try
      {
        server.unregisterMBean(new ObjectName(entry.getKey()));
      }
      catch (Exception exc)
      {
        logger.error("Unable to unregister service mbean with object name: " + entry.getKey(), exc);
      }
    }
    registeredServices = null;

    
    for (String objectName : registeredConfiguration.keySet())
    {
      try
      {
        server.unregisterMBean(new ObjectName(objectName));
      }
      catch (Exception exc)
      {
        logger.error("Unable to unregister service mbean with object name: " + objectName, exc);
      }
    }
    registeredConfiguration = null;
  }

  public void bundleChanged(BundleEvent event)
  {
    Bundle bundle = event.getBundle();
    switch (event.getType())
    {
      case BundleEvent.INSTALLED:
        registerBundle(bundle);
        break;
      
      case BundleEvent.UNINSTALLED:
        String objectName = format(BUNDLE_NAME_TEMPLATE, OSGiUtils.toString(bundle));
        if (registeredBundles.containsKey(objectName))
        {
          registeredBundles.remove(objectName);
          try
          {
            server.unregisterMBean(new ObjectName(objectName));
          }
          catch (Exception exc)
          {
            logger.error("Unable to unregister bundle mbean with object name: " + objectName, exc);
          }
        }
        break;
    }
  }

  public void serviceChanged(ServiceEvent event)
  {
    ServiceReference serviceRef = event.getServiceReference();
    switch (event.getType())
    {
      case ServiceEvent.REGISTERED:
        registerService(serviceRef);
        break;
        
      case ServiceEvent.UNREGISTERING:
        String[] interfaces = (String[])serviceRef.getProperty(OBJECTCLASS);
        String pid = (String)serviceRef.getProperty(SERVICE_PID);

        for (String interfce : interfaces)
        {
          String objectName = format(SERVICE_NAME_TEMPLATE, interfce, pid);
          if (registeredServices.containsKey(objectName))
          {
            registeredServices.remove(objectName);
            try
            {
              server.unregisterMBean(new ObjectName(objectName));
            }
            catch (Exception exc)
            {
              logger.error("Unable to unregister service mbean with object name: " + objectName, exc);
            }
          }
          
        }
        break;
    }
  }

  public void configurationEvent(ConfigurationEvent event)
  {
    try
    {
      Configuration configuration = configAdmin.getConfiguration(event.getPid());
      String objectName = format(CONFIGURATION_NAME_TEMPLATE, configuration.getPid());


      switch (event.getType())
      {
        case ConfigurationEvent.CM_UPDATED:
          if (!registeredConfiguration.containsKey(objectName))
          {
            registerConfiguration(configuration);
          }
          break;
          
        case ConfigurationEvent.CM_DELETED:
          if (registeredConfiguration.containsKey(objectName))
          {
            try
            {
              server.unregisterMBean(new ObjectName(objectName));
            }
            catch (Exception exc)
            {
              logger.error("Unable to unregister configuration mbean with object name: " + objectName, exc);
            }
          }
          break;
      }
    }
    catch (IOException ioexc)
    {
      logger.error("Unable to get configuration object from configuration admin on update.", ioexc);
    }
  }
  
  private void registerFramework()
  {
    try
    {
      registeredFramework = new FrameworkMBeanImpl(startLevel);
      server.registerMBean(registeredFramework, new ObjectName((FRAMEWORK_NAME)));
    }
    catch (Exception exc)
    {
      logger.error("Unable to register framework mbean with object name: "  + FRAMEWORK_NAME, exc);
    }
  }
  
  private void registerBundle(Bundle bundle)
  {
    String objectName = format(BUNDLE_NAME_TEMPLATE, OSGiUtils.toString(bundle));
    try
    {
      BundleMBeanImpl bundleMBean = new BundleMBeanImpl(bundle, startLevel, packageAdmin);
      server.registerMBean(bundleMBean, new ObjectName(objectName));
      registeredBundles.put(objectName, bundleMBean);
    }
    catch (Exception exc)
    {
      logger.error("Unable to register bundle mbean with object name: "  + objectName, exc);
    }
  }
  
  private void registerService(ServiceReference serviceRef)
  {
    String[] interfaces = (String[])serviceRef.getProperty(OBJECTCLASS);
    Long id = (Long)serviceRef.getProperty(SERVICE_ID);

    for (String interfce : interfaces)
    {
      String objectName = format(SERVICE_NAME_TEMPLATE, interfce, OSGiUtils.toString(serviceRef.getBundle()), id);
      try
      {
        ServiceMBeanImpl serviceMBean = new ServiceMBeanImpl(serviceRef, interfce);
        server.registerMBean(serviceMBean, new ObjectName(objectName));
        registeredServices.put(objectName, serviceMBean);
      }
      catch (Exception exc)
      {
        logger.error("Unable to register service mbean with object name: " + objectName, exc);
      }
    }
  }
  
  private void registerConfiguration(Configuration configuration)
  {
    String objectName = format(CONFIGURATION_NAME_TEMPLATE, configuration.getPid());
    try
    {
      ConfigurationMBeanImpl configMBean = new ConfigurationMBeanImpl(configuration);
      server.registerMBean(configMBean, new ObjectName(objectName));
      registeredConfiguration.put(objectName, configMBean);
    }
    catch (Exception exc)
    {
      logger.error("Unable to register configuration mbean with object name: " + objectName, exc);
    }
  }
}