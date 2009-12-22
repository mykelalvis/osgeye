package org.osgeye.console.commands.diagnosis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgeye.console.BundleStore;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.VersionRange;
import org.osgeye.domain.manifest.ExportPackagesDeclaration;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.domain.manifest.Resolution;
import org.osgeye.utils.Pair;

/**
 * Shared utilities for the diagnosis commands.
 * 
 * @author Corey Baswell
 */
public class DiagnosisUtils
{
  private BundleStore bundleStore;
  
  public DiagnosisUtils(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }
  
  /**
   * Finds missing package imports that aren't wired for the given bundle.
   * 
   * @param bundle The bundle to find missing package imports for.
   * @param includeMandatory Include package imports with a resolution of mandatory.
   * @param includeOptional Include package imports with a resolution of optional.
   * @return A list of all the missing packages for this bundle or an empty if no
   * missing packages were found.
   */
  public List<Pair<ImportPackagesDeclaration, String>> findMissingImports(Bundle bundle, 
      boolean includeMandatory, boolean includeOptional)
  {
    Manifest manifest = bundle.getManifest();
    List<ImportPackagesDeclaration> importDeclarations = (manifest == null) 
        ? new ArrayList<ImportPackagesDeclaration>() : manifest.getImportDeclarations();

    List<String> importPackages = new ArrayList<String>();
    Map<String, ImportPackagesDeclaration> importDeclarationMap = new HashMap<String, ImportPackagesDeclaration>();

    for (ImportPackagesDeclaration importDeclaration : importDeclarations)
    {
      for (String packageImport : importDeclaration.getPackages())
      {
        importPackages.add(packageImport);
        importDeclarationMap.put(packageImport, importDeclaration);
      }
    }

    Collections.sort(importPackages);
    List<Pair<ImportPackagesDeclaration, String>> missingPackages = new ArrayList<Pair<ImportPackagesDeclaration, String>>();

    FIND_MISSING_PACKAGES: for (String importPackage : importPackages)
    {
      ImportPackagesDeclaration importDeclaration = importDeclarationMap.get(importPackage);
      Resolution importResolution = importDeclaration.getResolution();
      
      if ((!includeOptional && (importResolution == Resolution.OPTIONAL)) || (!includeMandatory && (importResolution == Resolution.MANDATORY)))
      {
        continue;
      }
      
      VersionRange importRange = importDeclaration.getVersion();

      /*
       * Go through all other bundles and see if there is matching exported
       * package. If there is then this package is not missing so continue
       * to the next one.
       */
      for (Bundle exportBundle : bundleStore.getBundles())
      {
        for (ExportedPackage exportedPackage : exportBundle.getExportedPackages())
        {
          if (exportedPackage.getName().equals(importPackage) && importRange.isWithinRange(exportedPackage.getVersion()))
          {
            continue FIND_MISSING_PACKAGES;
          }
        }
      }
      
      /*
       * Make sure this bundle doesn't export the package itself. If it does then
       * this package is located within this bundle's classpath and it's not keeping
       * the bundle from getting resolved (i.e. not considered missing).
       */
      for (ExportPackagesDeclaration exportDeclaration : manifest.getExportDeclarations())
      {
        if (importDeclaration.getVersion().isWithinRange(exportDeclaration.getVersion()))
        {
          for (String exportPackage : exportDeclaration.getPackages())
          {
            if (exportPackage.equals(importPackage) && importRange.isWithinRange(exportDeclaration.getVersion()))
            {
              continue FIND_MISSING_PACKAGES;
            }
          }
        }
      }
      
      missingPackages.add(new Pair<ImportPackagesDeclaration, String>(importDeclaration, importPackage));
    }

    return missingPackages;
  }
  
  public List<UsesConflict> findUsesConflicts(Bundle bundle)
  {
    List<UsesConflict> conflicts = new ArrayList<UsesConflict>();
    
    Manifest manifest = bundle.getManifest();
    List<ImportPackagesDeclaration> importDeclarations = (manifest == null) 
        ? new ArrayList<ImportPackagesDeclaration>() : manifest.getImportDeclarations();
    
    for (ImportPackagesDeclaration importDeclaration : importDeclarations)
    {
      VersionRange importVersionRange = importDeclaration.getVersion();
      for (String importPackage : importDeclaration.getPackages())
      {
        List<Bundle> exportingBundles = bundleStore.getBundles();
        for (Bundle exportingBundle : exportingBundles)
        {
          if ((exportingBundle == bundle) || (exportingBundle.getState() == BundleState.INSTALLED))
          {
            continue;
          }
          
          List<ExportedPackage> exportedPackages = exportingBundle.getExportedPackages();
          for (ExportedPackage exportedPackage : exportedPackages)
          {
            if (exportedPackage.getName().equals(importPackage) && importVersionRange.isWithinRange(exportedPackage.getVersion()))
            {
              List<Pair<ImportPackagesDeclaration, String>> usesImportDeclarations = findUsesDeclarations(exportedPackage);
              USES_LOOP: for (Pair<ImportPackagesDeclaration, String> usesImportDeclaration : usesImportDeclarations)
              {
                ExportedPackage usesWiredExportedPackage = findWiredPackage(usesImportDeclaration.x, usesImportDeclaration.y, exportingBundle);

                if (usesWiredExportedPackage == null) continue USES_LOOP;
                
                for (ImportPackagesDeclaration potentialImportDeclarationConflict : importDeclarations)
                {
                  for (String potentialImportPackageConflict : potentialImportDeclarationConflict.getPackages())
                  {
                    if (potentialImportPackageConflict.equals(usesWiredExportedPackage.getName()))
                    {
                      if (!potentialImportDeclarationConflict.getVersion().isWithinRange(usesWiredExportedPackage.getVersion()))
                      {
                        Pair<ImportPackagesDeclaration, String> exportedImport = new Pair<ImportPackagesDeclaration, String>(importDeclaration, importPackage);
                        Pair<ImportPackagesDeclaration, String> usesImportConflict = new Pair<ImportPackagesDeclaration, String>(potentialImportDeclarationConflict, potentialImportPackageConflict);
                        
                        conflicts.add(new UsesConflict(bundle, exportedImport, usesImportConflict, exportingBundle, exportedPackage, usesWiredExportedPackage));
                      }
                      continue USES_LOOP;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
        
    return conflicts;
  }
  
  List<Pair<ImportPackagesDeclaration, String>> findUsesDeclarations(ExportedPackage exportedPackage)
  {
    List<Pair<ImportPackagesDeclaration, String>> usesImportDelcarations = new ArrayList<Pair<ImportPackagesDeclaration, String>>();
    
    Manifest manifest = exportedPackage.getBundle().getManifest();
    if (manifest != null)
    {
      List<ExportPackagesDeclaration> exportDeclarations = manifest.getExportDeclarations();
      List<ImportPackagesDeclaration> importDeclarations = manifest.getImportDeclarations();
      
      for (ExportPackagesDeclaration exportDeclaration : exportDeclarations)
      {
        if (exportDeclaration.getVersion().equals(exportedPackage.getVersion()))
        {
          List<String> exportedPackageDeclarations = exportDeclaration.getPackages();
          for (String exportedPackageDeclaration : exportedPackageDeclarations)
          {
            if (exportedPackageDeclaration.equals(exportedPackage.getName()))
            {
              List<String> usesPackages = exportDeclaration.getUses();
              USES_LOOP: for (String usesPackage : usesPackages)
              {
                for (ImportPackagesDeclaration importDeclaration : importDeclarations)
                {
                  for (String importPackage : importDeclaration.getPackages())
                  {
                    if (importPackage.equals(usesPackage))
                    {
                      usesImportDelcarations.add(new Pair<ImportPackagesDeclaration, String>(importDeclaration, importPackage));
                      continue USES_LOOP;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    
    return usesImportDelcarations;
  }
  
  ExportedPackage findWiredPackage(ImportPackagesDeclaration importDeclaration, String packge, Bundle bundle)
  {
    for (Bundle exportingBundle : bundleStore.getBundles())
    {
      List<ExportedPackage> exportedPackages = exportingBundle.getExportedPackages();
      for (ExportedPackage exportedPackage : exportedPackages)
      {
        if (exportedPackage.getName().equals(packge) && exportedPackage.getImportedBundleIds().contains(bundle.getId()))
        {
          return exportedPackage;
        }
      }
    }
    
    return null;
  }
}
