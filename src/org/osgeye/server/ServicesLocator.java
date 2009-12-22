package org.osgeye.server;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.packageadmin.PackageAdmin;

public class ServicesLocator
{
  private BundleContext bundleContext;
  
  private ServiceReference packageAdminRef;
  private ServiceReference logServiceRef;
  
  public ServicesLocator(BundleContext bundleContext)
  {
    this.bundleContext = bundleContext;
    packageAdminRef = bundleContext.getServiceReference(PackageAdmin.class.getName());
    logServiceRef = bundleContext.getServiceReference(LogService.class.getName());
  }
  
  public PackageAdmin getPackageAdmin()
  {
    return (PackageAdmin)bundleContext.getService(packageAdminRef);
  }
  
  public LogService getLogService()
  {
    return (LogService)bundleContext.getService(logServiceRef);
  }
}
