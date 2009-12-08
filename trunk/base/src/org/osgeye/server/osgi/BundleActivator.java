package org.osgeye.server.osgi;

import static org.osgi.framework.Constants.*;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgeye.server.jmx.MBeanManager;
import org.osgeye.server.network.NetworkServer;
import org.osgeye.server.osgi.servicewrappers.OsgiConfigAdmin;
import org.osgeye.server.osgi.servicewrappers.OsgiPackageAdmin;
import org.osgeye.server.osgi.servicewrappers.OsgiStartLevel;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleActivator implements org.osgi.framework.BundleActivator, ManagedService
{
  static public final String CONFIG_HOST = "HOST";
  static public final String CONFIG_PORT = "PORT";
  
  private Logger logger = LoggerFactory.getLogger(BundleActivator.class);
  
  private BundleContext context;
  
  private NetworkServer server;
  private MessageProcessorImpl messageProcessor;
  private EventsListener eventListener;
  
  private MBeanManager mbeanManager;
  
  private PackageAdmin packageAdmin;
  private StartLevel startLevel;
  private ConfigurationAdmin configAdmin;
  
  public BundleActivator()
  {}

  public void start(BundleContext context) throws Exception
  {
    this.context = context;
    String versionString = (String) context.getBundle().getHeaders().get(BUNDLE_VERSION);
    String servicePid = getClass().getName() + "." + versionString;
    Dictionary<String, Object> properties = new Hashtable<String, Object>();
    properties.put(SERVICE_PID, servicePid);
    properties.put(SERVICE_DESCRIPTION, getClass().getName() + " " + versionString + " configuration interface");
    context.registerService(ManagedService.class.getName(), this, properties);
  }

  public void updated(Dictionary properties) throws ConfigurationException
  {
    shutDown();
    
    packageAdmin = new OsgiPackageAdmin(context);
    startLevel = new OsgiStartLevel(context);
    configAdmin = new OsgiConfigAdmin(context);
    
    messageProcessor = new MessageProcessorImpl(context, packageAdmin, startLevel, configAdmin);
    
    try
    {
      if ((properties != null) && (properties.get(CONFIG_HOST) != null) && (properties.get(CONFIG_PORT) != null))
      {
        String host = (String)properties.get(CONFIG_HOST);
        Integer port = Integer.parseInt(properties.get(CONFIG_PORT).toString());
        server = new NetworkServer(messageProcessor, host, port);
      }
      else
      {
        server = new NetworkServer(messageProcessor);
      }
      
      messageProcessor.setEventDispatcher(server);
      server.start();
      eventListener = new EventsListener(packageAdmin, startLevel, server);
      eventListener.start(context);
    }
    catch (IOException ioexc)
    {
      logger.error("Unable to startup OSGEye network server due to IO exception: " + ioexc.getMessage(), ioexc);
    }
    
    mbeanManager = new MBeanManager(context.getBundle(), startLevel, packageAdmin, configAdmin);
    mbeanManager.start();
  }
  
  public void stop(BundleContext context) throws Exception
  {
    shutDown();
  }
  
  private void shutDown()
  {
    if (server != null)
    {
      try
      {
        server.stop();
      }
      catch (Exception exc) {}
      server = null;
    }
    
    if (mbeanManager != null)
    {
      mbeanManager.stop();
      mbeanManager = null;
    }
    
    if (eventListener != null)
    {
      try
      {
        eventListener.stop(context);
      }
      catch (Exception exc) {}
      eventListener = null;
    }
    
    messageProcessor = null;
    packageAdmin = null;
  }
}
