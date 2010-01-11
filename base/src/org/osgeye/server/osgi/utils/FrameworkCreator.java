package org.osgeye.server.osgi.utils;

import org.osgeye.domain.Framework;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class FrameworkCreator
{
  private BundleContext osgeyeContext;
  
  private StartLevel startLevel;
  
  private BundleCreator bundleCreator;
  
  public FrameworkCreator(BundleContext osgeyeContext, PackageAdmin packageAdmin, StartLevel startLevel)
  {
    this.osgeyeContext = osgeyeContext;
    this.startLevel = startLevel;
    bundleCreator = new BundleCreator(packageAdmin, startLevel);
  }
  
  public Framework createFramework()
  {
    return new Framework(bundleCreator.createBundle(osgeyeContext.getBundle(0)), startLevel.getStartLevel(), startLevel.getInitialBundleStartLevel());
  }
}
