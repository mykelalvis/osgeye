package org.osgeye.server.osgi.servicewrappers;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

public class OsgiPackageAdmin implements PackageAdmin
{
  private BundleContext bundleContext;
  private ServiceReference packageAdminRef;
  
  public OsgiPackageAdmin(BundleContext bundleContext)
  {
    this.bundleContext = bundleContext;
    packageAdminRef = bundleContext.getServiceReference(PackageAdmin.class.getName());
  }

  public Bundle getBundle(Class clazz)
  {
    return getPackageAdmin().getBundle(clazz);
  }

  public int getBundleType(Bundle bundle)
  {
    return getPackageAdmin().getBundleType(bundle);
  }

  public Bundle[] getBundles(String symbolicName, String versionRange)
  {
    return getPackageAdmin().getBundles(symbolicName, versionRange);
  }

  public ExportedPackage getExportedPackage(String name)
  {
    return getPackageAdmin().getExportedPackage(name);
  }

  public ExportedPackage[] getExportedPackages(Bundle bundle)
  {
    return getPackageAdmin().getExportedPackages(bundle);
  }

  public ExportedPackage[] getExportedPackages(String name)
  {
    return getPackageAdmin().getExportedPackages(name);
  }

  public Bundle[] getFragments(Bundle bundle)
  {
    return getPackageAdmin().getFragments(bundle);
  }

  public Bundle[] getHosts(Bundle bundle)
  {
    return getPackageAdmin().getHosts(bundle);
  }

  public RequiredBundle[] getRequiredBundles(String symbolicName)
  {
    return getPackageAdmin().getRequiredBundles(symbolicName);
  }

  public void refreshPackages(Bundle[] bundles)
  {
    getPackageAdmin().refreshPackages(bundles);
  }

  public boolean resolveBundles(Bundle[] bundles)
  {
    return getPackageAdmin().resolveBundles(bundles);
  }
  
  protected PackageAdmin getPackageAdmin()
  {
    return (PackageAdmin)bundleContext.getService(packageAdminRef); 
  }
}
