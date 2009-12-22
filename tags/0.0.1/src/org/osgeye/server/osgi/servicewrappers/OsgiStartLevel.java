package org.osgeye.server.osgi.servicewrappers;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

public class OsgiStartLevel implements StartLevel
{
  private BundleContext bundleContext;
  private ServiceReference startLevelRef;
  
  public OsgiStartLevel(BundleContext bundleContext)
  {
    this.bundleContext = bundleContext;
    startLevelRef = bundleContext.getServiceReference(StartLevel.class.getName());
  }

  public int getBundleStartLevel(Bundle bundle)
  {
    return getStartLevelService().getBundleStartLevel(bundle);
  }

  public int getInitialBundleStartLevel()
  {
    return getStartLevelService().getInitialBundleStartLevel();
  }

  public int getStartLevel()
  {
    return getStartLevelService().getStartLevel();
  }

  public boolean isBundleActivationPolicyUsed(Bundle bundle)
  {
    return getStartLevelService().isBundleActivationPolicyUsed(bundle);
  }

  public boolean isBundlePersistentlyStarted(Bundle bundle)
  {
    return getStartLevelService().isBundlePersistentlyStarted(bundle);
  }

  public void setBundleStartLevel(Bundle bundle, int startlevel)
  {
    getStartLevelService().setBundleStartLevel(bundle, startlevel);
  }

  public void setInitialBundleStartLevel(int startlevel)
  {
    getStartLevelService().setInitialBundleStartLevel(startlevel);
  }

  public void setStartLevel(int startlevel)
  {
    getStartLevelService().setStartLevel(startlevel);
  }
  
  protected StartLevel getStartLevelService()
  {
    return (StartLevel)bundleContext.getService(startLevelRef); 
  }
}
