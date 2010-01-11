package org.osgeye.console.completors;

import static org.osgeye.utils.UtilityMethods.*;

import java.util.ArrayList;
import java.util.List;

import jline.SimpleCompletor;

import org.osgeye.client.ServerState;
import org.osgeye.client.ServerStateListener;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;

public class PackagesCompletor extends SimpleCompletor implements ServerStateListener
{
  private ServerState bundleStore;
  
  public PackagesCompletor(ServerState bundleStore)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    bundleStore.addListener(this);
    serverStateUpdated();
  }

  public void serverStateUpdated()
  {
    List<Bundle> bundles = bundleStore.getBundles();
    List<String> packageNames = new ArrayList<String>();
    for (Bundle bundle : bundles)
    {
      List<ExportedPackage> exportedPackages = bundle.getExportedPackages();
      for (ExportedPackage exportedPackage : exportedPackages)
      {
        String packageName = exportedPackage.getName();
        if (!packageNames.contains(packageName)) packageNames.add(packageName);
      }
      
      if (bundle.getManifest() == null) continue;
      List<ImportPackagesDeclaration> importedPackageDeclarations = bundle.getManifest().getImportDeclarations();
      for (ImportPackagesDeclaration importedPackageDeclaration : importedPackageDeclarations)
      {
        for (String importedPackage : importedPackageDeclaration.getPackages())
        {
          if (!packageNames.contains(importedPackage)) packageNames.add(importedPackage);
        }
      }
    }
    
    setCandidateStrings(toArray(packageNames, String.class));
  }
}
