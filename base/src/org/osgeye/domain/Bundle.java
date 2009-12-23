package org.osgeye.domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgeye.domain.manifest.Manifest;
import org.osgeye.utils.IOUtils;
import org.osgeye.utils.OSGiUtils;
import org.osgi.framework.ServiceReference;

public class Bundle implements Comparable<Bundle>, Serializable
{
  static private final long serialVersionUID = 3036570200537286684L;

  private long id;
  
  private String symbolicName;
  
  private long lastModified;
  
  private Manifest manifest;
  
  private String location;

  private BundleState state;
  
  private List<Service> services;
  
  private List<ExportedPackage> exportedPackages;
  
  private List<Long> fragmentBundleIds;
  
  private List<Long> hostBundleIds;
  
  private List<Long> requiredBundleIds;
  
  private int startLevel;
  
  private transient Version version;
  
  public Bundle(org.osgi.framework.Bundle bundle, org.osgi.service.packageadmin.ExportedPackage[] exportedPckgs, 
      org.osgi.framework.Bundle[] fragments, org.osgi.framework.Bundle[] hosts, List<Long> requiredBundleIds, 
      int startLevel)
  {
    
    id = bundle.getBundleId();
    lastModified = bundle.getLastModified();
    state = BundleState.fromState(bundle.getState());
    symbolicName = bundle.getSymbolicName();
    location = bundle.getLocation();
    
    services = new ArrayList<Service>();
    ServiceReference[] serviceRefs = bundle.getRegisteredServices();
    if (serviceRefs != null)
    {
      for (ServiceReference serviceRef : serviceRefs)
      {
        services.add(new Service(this, serviceRef, bundle));
      }
    }
    
    exportedPackages = new ArrayList<ExportedPackage>();
    if (exportedPckgs != null)
    {
      for (org.osgi.service.packageadmin.ExportedPackage exportedPckg : exportedPckgs)
      {
        exportedPackages.add(new ExportedPackage(this, exportedPckg));
      }
    }
    
    fragmentBundleIds = new ArrayList<Long>();
    if (fragments != null)
    {
      for (org.osgi.framework.Bundle fragment : fragments) fragmentBundleIds.add(fragment.getBundleId());
    }

    
    hostBundleIds = new ArrayList<Long>();
    if (hosts != null)
    {
      for (org.osgi.framework.Bundle host : hosts) hostBundleIds.add(host.getBundleId());
    }
    
    this.requiredBundleIds = requiredBundleIds;
    this.startLevel = startLevel;
    
    try
    {
      String manifestText = OSGiUtils.getManifest(bundle);
      if (manifestText != null)  manifest = new Manifest(manifestText);
    }
    catch (IOException ioexc)
    {}
  }
  
  public String toString()
  {
    return symbolicName + " " + getVersion();
  }
  
  public boolean equals(Object object)
  {
    if (object instanceof Bundle)
    {
      return id == ((Bundle)object).id;
    }
    else
    {
      return false;
    }
  }

  public long getId()
  {
    return id;
  }
  
  public boolean isFragment()
  {
    return (hostBundleIds.size() > 0);
  }
  
  public boolean isHost()
  {
    return (fragmentBundleIds.size() > 0);
  }
  
  public List<Long> getFragmentBundleIds()
  {
    return new ArrayList<Long>(fragmentBundleIds);
  }

  public List<Long> getHostBundleIds()
  {
    return new ArrayList<Long>(hostBundleIds);
  }

  public List<Long> getRequiredBundleIds()
  {
    return requiredBundleIds;
  }

  public int getStartLevel()
  {
    return startLevel;
  }

  public String getSymbolicName()
  {
    return symbolicName;
  }
  
  public Version getVersion()
  {
    if (version == null)
    {
      version = (manifest == null) ? new Version() : manifest.getVersion();
    }
    return version;
  }

  public long getLastModified()
  {
    return lastModified;
  }

  public Manifest getManifest()
  {
    return manifest;
  }

  public String getLocation()
  {
    return location;
  }

  public BundleState getState()
  {
    return state;
  }

  public List<Service> getServices()
  {
    return services;
  }

  public List<ExportedPackage> getExportedPackages()
  {
    return exportedPackages;
  }
  
  public boolean exports(String packge)
  {
    if (exportedPackages == null)
    {
      return false;
    }
    else
    {
      for (ExportedPackage export : exportedPackages)
      {
        if (packge.equals(export.getName()))
        {
          return true;
        }
      }
      return false;
    }
  }

  public int compareTo(Bundle bundle)
  {
    int value = symbolicName.compareTo(bundle.symbolicName);
    return (value == 0) ? (getVersion().compareTo(bundle.getVersion())) : value;
  }
}
