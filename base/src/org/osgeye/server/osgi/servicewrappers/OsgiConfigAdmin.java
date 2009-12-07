package org.osgeye.server.osgi.servicewrappers;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class OsgiConfigAdmin implements ConfigurationAdmin
{
  private BundleContext bundleContext;
  private ServiceReference configAdminRef;
  
  public OsgiConfigAdmin(BundleContext bundleContext)
  {
    this.bundleContext = bundleContext;
    configAdminRef = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
  }

  public Configuration createFactoryConfiguration(String factoryPid) throws IOException
  {
    return getConfigAdmin().createFactoryConfiguration(factoryPid);
  }

  public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException
  {
    return getConfigAdmin().createFactoryConfiguration(factoryPid, location);
  }

  public Configuration getConfiguration(String pid) throws IOException
  {
    return getConfigAdmin().getConfiguration(pid);
  }

  public Configuration getConfiguration(String pid, String location) throws IOException
  {
    return getConfigAdmin().getConfiguration(pid, location);
  }

  public Configuration[] listConfigurations(String filter) throws IOException, InvalidSyntaxException
  {
    return getConfigAdmin().listConfigurations(filter);
  }

  protected ConfigurationAdmin getConfigAdmin()
  {
    return (ConfigurationAdmin)bundleContext.getService(configAdminRef); 
  }
}
