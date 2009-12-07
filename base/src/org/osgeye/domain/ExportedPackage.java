package org.osgeye.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExportedPackage implements Serializable, Comparable<ExportedPackage>
{
  static private final long serialVersionUID = -6519873428076178678L;

  private Bundle bundle;
  
  private String name;
  
  private Version version;
  
  private boolean removalPending;
  
  private List<Long> importedBundleIds;
  
  public ExportedPackage(Bundle bundle, org.osgi.service.packageadmin.ExportedPackage exportedPackage)
  {
    this.bundle = bundle;
    name = exportedPackage.getName();
    version = (exportedPackage.getVersion() == null) ? new Version() : new Version(exportedPackage.getVersion());
    removalPending = exportedPackage.isRemovalPending();
    
    importedBundleIds = new ArrayList<Long>();
    org.osgi.framework.Bundle[] importedBundles = exportedPackage.getImportingBundles();
    if (importedBundles != null)
    {
      for (org.osgi.framework.Bundle importedBundle : importedBundles)
      {
        importedBundleIds.add(importedBundle.getBundleId());
      }
    }
  }

  public Bundle getBundle()
  {
    return bundle;
  }

  public String getName()
  {
    return name;
  }

  public Version getVersion()
  {
    return version;
  }

  public boolean isRemovalPending()
  {
    return removalPending;
  }

  public List<Long> getImportedBundleIds()
  {
    return importedBundleIds;
  }
  
  public String toString()
  {
    return name + " v" + version;
  }

  public int compareTo(ExportedPackage ep)
  {
    return toString().compareTo(ep.toString());
  }

}
