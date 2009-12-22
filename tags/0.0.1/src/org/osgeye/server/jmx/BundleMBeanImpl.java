package org.osgeye.server.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgeye.domain.manifest.ExportPackagesDeclaration;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.utils.OSGiUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class BundleMBeanImpl extends StandardMBean implements BundleMBean
{
  private Bundle bundle;
  private StartLevel startLevel;
  private PackageAdmin packageAdmin;
  
  private Manifest manfiest;
  
  public BundleMBeanImpl(Bundle bundle, StartLevel startLevel, PackageAdmin packageAdmin) throws NotCompliantMBeanException
  {
    super(BundleMBean.class);
    this.bundle = bundle;
    this.startLevel = startLevel;
    this.packageAdmin = packageAdmin;

    try
    {
      String manifestText = OSGiUtils.getManifest(bundle);
      if (manifestText != null) manfiest = new Manifest(manifestText);
    }
    catch (IOException ioexc)
    {}
  }

  public String[] getFragments()
  {
    if (!isFragment())
    {
      Bundle[] fragmentBundles = packageAdmin.getFragments(bundle);
      if ((fragmentBundles == null) || (fragmentBundles.length == 0))
      {
        return new String[0];
      }
      else
      {
        String[] fragments = new String[fragmentBundles.length];
        for (int i = 0; i < fragments.length; i++)
        {
          fragments[i] = OSGiUtils.toString(fragmentBundles[i]);
        }
        
        return fragments;
      }
    }
    else
    {
      return new String[0];
    }
  }

  public Map<String, String> getHeaders()
  {
    Map<String, String> headers = new HashMap<String, String>();
    Dictionary bundleHeaders = bundle.getHeaders();
    Enumeration keys = bundleHeaders.keys();
    while (keys.hasMoreElements())
    {
      Object key = keys.nextElement();
      headers.put(key.toString(), bundleHeaders.get(key).toString());
    }
    
    return headers;
  }

  public String[] getHosts()
  {
    if (isFragment())
    {
      Bundle[] hostBundles = packageAdmin.getHosts(bundle);
      if ((hostBundles == null) || (hostBundles.length == 0))
      {
        return new String[0];
      }
      else
      {
        String[] hosts = new String[hostBundles.length];
        for (int i = 0; i < hosts.length; i++)
        {
          hosts[i] = OSGiUtils.toString(hostBundles[i]);
        }
        
        return hosts;
      }
    }
    else
    {
      return new String[0];
    }
  }

  public long getId()
  {
    return bundle.getBundleId();
  }

  public String[] getExportedPackages()
  {
    if (manfiest == null)
    {
      return new String[] {};
    }
    else
    {
      List<ExportPackagesDeclaration> exportPackagesDeclarations = manfiest.getExportDeclarations();
      List<String> exports = new ArrayList<String>();
      for (int i = 0; i < exportPackagesDeclarations.size(); i++)
      {
        ExportPackagesDeclaration exportDeclaration = exportPackagesDeclarations.get(i);
        String version = exportDeclaration.getVersion().toString();
        List<String> packages = exportDeclaration.getPackages();
        for (String pckage : packages)
        {
          exports.add(pckage + " version=" + version);
        }
      }
      
      return exports.toArray(new String[exports.size()]);
    }
  }

  public String[] getImportedPackages()
  {
    if (manfiest == null)
    {
      return new String[] {};
    }
    else
    {
      List<ImportPackagesDeclaration> importDeclarations = manfiest.getImportDeclarations();
      List<String> imports = new ArrayList<String>();
      for (int i = 0; i < importDeclarations.size(); i++)
      {
        ImportPackagesDeclaration importDeclaration = importDeclarations.get(i);
        String versionRange = importDeclaration.getVersion().toString();
        List<String> packages = importDeclaration.getPackages();
        for (String pckage : packages)
        {
          imports.add(pckage + " version=" + versionRange);
        }
      }
      
      return imports.toArray(new String[imports.size()]);
    }
  }

  public Date getLastModified()
  {
    return new Date(bundle.getLastModified());
  }

  public String getLocation()
  {
    return bundle.getLocation();
  }

  public int getStartLevel()
  {
    return startLevel.getBundleStartLevel(bundle);
  }

  public String getState()
  {
    return OSGiUtils.bundleStateToString(bundle.getState());
  }

  public String getSymbolicName()
  {
    return bundle.getSymbolicName();
  }

  public String getVersion()
  {
    return getHeader("Bundle-Version");
  }

  public boolean isFragment()
  {
    return (getHeader("Fragment-Host") == null);
  }

  public void refreshPackages()
  {
    packageAdmin.refreshPackages(new Bundle[] {bundle});
  }

  public boolean resolve()
  {
    return packageAdmin.resolveBundles(new Bundle[] {bundle});
  }

  public void start()
  {
    try
    {
      bundle.start();
    }
    catch (BundleException bexc)
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public void start(int options)
  {
    try
    {
      bundle.start(options);
    }
    catch (BundleException bexc)
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public void stop()
  {
    try
    {
      bundle.stop();
    }
    catch (BundleException bexc)
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public void stop(int options)
  {
    try
    {
      bundle.stop(options);
    }
    catch (BundleException bexc)
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public void uninstall()
  {
    try
    {
      bundle.uninstall();
    }
    catch (BundleException bexc)
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public void update()
  {
    try
    {
      bundle.update();
    }
    catch (BundleException bexc)
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public void update(String urlStr) throws MalformedURLException, IOException
  {
    try
    {
      bundle.update(new URL(urlStr).openStream());
    }
    catch (BundleException bexc )
    {
      throw new RuntimeException(bexc.getMessage());
    }
  }

  public String viewManifest() throws IOException
  {
    return (manfiest == null) ? null : manfiest.getPrettyFile();
  }
  
  private String getHeader(String headerName)
  {
    Dictionary headers = bundle.getHeaders();
    return (String)headers.get(headerName);
  }
}
