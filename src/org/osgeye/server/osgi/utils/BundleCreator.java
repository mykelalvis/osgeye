package org.osgeye.server.osgi.utils;

import java.util.ArrayList;
import java.util.List;

import org.osgeye.domain.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.service.startlevel.StartLevel;

public class BundleCreator
{
  private PackageAdmin packageAdmin;
  private StartLevel startLevelService;
  
  public BundleCreator(PackageAdmin packageAdmin, StartLevel startLevelService)
  {
    this.packageAdmin = packageAdmin;
    this.startLevelService = startLevelService;
  }
  
  public Bundle createBundle(org.osgi.framework.Bundle osgiBundle)
  {
    RequiredBundle[] requiredBundles = packageAdmin.getRequiredBundles(null);
    List<Long> requiredBundleIds = new ArrayList<Long>();
    
    START_LOOP: for (RequiredBundle requiredBundle : requiredBundles)
    {
      for (org.osgi.framework.Bundle requiringBundle : requiredBundle.getRequiringBundles())
      {
        if (requiringBundle.getBundleId() == osgiBundle.getBundleId())
        {
          requiredBundleIds.add(requiredBundle.getBundle().getBundleId());
          continue START_LOOP;
        }
      }
    }

    Bundle bundle = new Bundle(osgiBundle, packageAdmin.getExportedPackages(osgiBundle), 
        packageAdmin.getFragments(osgiBundle), packageAdmin.getHosts(osgiBundle), requiredBundleIds,
        startLevelService.getBundleStartLevel(osgiBundle));
    return bundle;
  }
}
