package org.osgeye.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgi.framework.Bundle;

public class OSGiUtils
{
  /**
   * Attempts to retrieve the manifest text for the given bundle. If not able
   * to located (for example the system bundle) <code>null</code> will be
   * returned.
   * 
   * @param bundle
   * @return The given bundle's manifest or <code>null</code> if not found.
   * @throws IOException
   */
  static public String getManifest(Bundle bundle) throws IOException
  {
    URL url = bundle.getResource("/META-INF/MANIFEST.MF");
    if (url != null)
    {
      return IOUtils.getContentsAsString(url);
    }
    else
    {
      String location = bundle.getLocation();
      if ((location != null) && location.contains(":"))
      {
        URL bundleUrl = new URL(location);
        File bundleFile = new File(bundleUrl.getFile());
        if (bundleFile.isFile())
        {
          ZipFile bundleZipFile = new ZipFile(bundleFile);
          ZipEntry entry = bundleZipFile.getEntry("META-INF/MANIFEST.MF");
          if (entry == null)
          {
            entry = bundleZipFile.getEntry("meta-inf/manifest.mf");
          }

          if (entry != null)
          {
            return new String(IOUtils.getContents(bundleZipFile.getInputStream(entry)));
          }
        }
      }
    }
    
    return null;
  }
  
  /**
   * 
   * @param bundleState The bundle state
   * @return A string representation of the given bundle state.
   * @see Bundle#getState()
   */
  static public String bundleStateToString(int bundleState)
  {
    switch (bundleState)
    {
      case Bundle.ACTIVE:
        return "ACTIVE";
        
      case Bundle.INSTALLED:
        return "INSTALLED";
        
      case Bundle.RESOLVED:
        return "RESOLVED";
        
      case Bundle.STARTING:
        return "STARTING";
        
      case Bundle.STOPPING:
        return "STOPPING";
        
      case Bundle.UNINSTALLED:
        return "UNINSTALLED";
        
      default:
        return "UNKNOWN";
    }
  }
  
  static public String getVersion(Bundle bundle)
  {
    Dictionary headers = bundle.getHeaders();
    String versionStr = (String)headers.get("Bundle-Version");
    return versionStr;
  }
  
  static public String toString(Bundle bundle)
  {
    return bundle.getSymbolicName() + " v" + getVersion(bundle);
  }
  
  static public ImportPackagesDeclaration findImportDeclaration(org.osgeye.domain.Bundle importingBundle, String packageName)
  {
    Manifest manifest = importingBundle.getManifest();
    if (manifest == null)
    {
      return null;
    }
    else
    {
      for (ImportPackagesDeclaration importDeclaration : manifest.getImportDeclarations())
      {
        for (String importPackage : importDeclaration.getPackages())
        {
          if (importPackage.equals(packageName))
          {
            return importDeclaration;
          }
        }
      }
      
      return null;
    }
  }
}
