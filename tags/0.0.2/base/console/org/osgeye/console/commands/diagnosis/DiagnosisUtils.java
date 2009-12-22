package org.osgeye.console.commands.diagnosis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.ImportedPackage;
import org.osgeye.domain.VersionRange;
import org.osgeye.domain.manifest.ExportPackagesDeclaration;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.domain.manifest.Resolution;

/**
 * Shared utilities for the diagnosis commands.
 * 
 * @author Corey Baswell
 */
public class DiagnosisUtils
{
  public DiagnosisUtils()
  {}
  
  /**
   * Finds missing package imports that aren't wired for the given bundle.
   * 
   * @param manifest The bundle manifest to find missing imports for.
   * @param includeMandatory Include package imports with a resolution of mandatory.
   * @param includeOptional Include package imports with a resolution of optional.
   * @return A list of all the missing imported packages for this bundle. If none
   * are found an empty list will be returned.
   */
  public List<ImportedPackage> findMissingImports(Manifest manifest, 
      boolean includeMandatory, boolean includeOptional, List<Bundle> allBundles)
  {
    List<ImportPackagesDeclaration> importDeclarations = (manifest == null) 
        ? new ArrayList<ImportPackagesDeclaration>() : manifest.getImportDeclarations();

    /*
     * Sort the results by package alphabetical order.
     */
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
    List<ImportedPackage> missingPackages = new ArrayList<ImportedPackage>();

    for (String importPackage : importPackages)
    {
      ImportPackagesDeclaration importDeclaration = importDeclarationMap.get(importPackage);
      Resolution importResolution = importDeclaration.getResolution();
      
      if ((includeOptional || (importResolution != Resolution.OPTIONAL)) && (includeMandatory || (importResolution != Resolution.MANDATORY)) 
          && isMissing(importPackage, importDeclaration, manifest, allBundles))
      {
        missingPackages.add(new ImportedPackage(importDeclaration, importPackage));
      }
    }

    return missingPackages;
  }

  /**
   * Finds uses conflict using the given bundle's manifest. 
   * 
   * @param bundle The bundle whose returned UsesConflicts will be for.
   * @param allBundles All bundles currently deployed in the system.
   * @return A list of UsesConflict. If none are found an empty list is returned.
   */
  public List<UsesConflict> findUsesConflicts(Bundle bundle, List<Bundle> allBundles)
  {
    return findUsesConflicts(bundle, bundle.getManifest(), allBundles);
  }

  /**
   * Finds uses conflict using the given manifest. 
   * 
   * @param manifest The manifest used to determine if any import declarations contain conflicts.
   * @param allBundles All bundles currently deployed in the system.
   * @return A list of UsesConflict. If none are found an empty list is returned.
   */
  public List<UsesConflict> findUsesConflicts(Manifest manifest, List<Bundle> allBundles)
  {
    return findUsesConflicts(null, manifest, allBundles);
  }

  /**
   * Finds uses conflict using the given manifest. 
   * 
   * @param bundle The bundle whose returned UsesConflicts will be for (can be null).
   * @param manifest The manifest used to determine if any import declarations contain conflicts.
   * @param allBundles All bundles currently deployed in the system.
   * @return A list of UsesConflict. If none are found an empty list is returned.
   */
  public List<UsesConflict> findUsesConflicts(Bundle bundle, Manifest manifest, List<Bundle> allBundles)
  {
    List<UsesConflict> conflicts = new ArrayList<UsesConflict>();
    
    List<ImportPackagesDeclaration> importDeclarations = (manifest == null) 
        ? new ArrayList<ImportPackagesDeclaration>() : manifest.getImportDeclarations();
    
    for (ImportPackagesDeclaration importDeclaration : importDeclarations)
    {
      VersionRange importVersionRange = importDeclaration.getVersion();
      for (String importPackage : importDeclaration.getPackages())
      {
        for (Bundle exportingBundle : allBundles)
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
              List<ImportedPackage> usesImportDeclarations = findUsesImportDeclarations(exportedPackage);
              USES_LOOP: for (ImportedPackage usesImportDeclaration : usesImportDeclarations)
              {
                ExportedPackage usesWiredExportedPackage = findWiredPackage(usesImportDeclaration.getDeclaration(), usesImportDeclaration.getPackage(), exportingBundle, allBundles);

                if (usesWiredExportedPackage == null) continue USES_LOOP;
                
                for (ImportPackagesDeclaration potentialImportDeclarationConflict : importDeclarations)
                {
                  for (String potentialImportPackageConflict : potentialImportDeclarationConflict.getPackages())
                  {
                    if (potentialImportPackageConflict.equals(usesWiredExportedPackage.getName()))
                    {
                      if (!potentialImportDeclarationConflict.getVersion().isWithinRange(usesWiredExportedPackage.getVersion()))
                      {
                        ImportedPackage exportedImport = new ImportedPackage(importDeclaration, importPackage);
                        ImportedPackage usesImportConflict = new ImportedPackage(potentialImportDeclarationConflict, potentialImportPackageConflict);
                        
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
  
  /**
   * Checks to see if the given import is missing. An import is considered missing 
   * if it does not match (package name and version) any of the exported packages 
   * for the given allBundles or it does not match an export from the given
   * importManifest.
   * 
   * @param importPackage The package name of the import.
   * @param importDeclaration The import declaration the import package was contained in.
   * @param importManifest The manifest the given import declaration came from.
   * @param allBundles All bundles currently deployed in the system.
   * @return true if the given import cannot be found otherwise false is returned.
   */
  boolean isMissing(String importPackage, ImportPackagesDeclaration importDeclaration, Manifest importManifest, List<Bundle> allBundles)
  {
    VersionRange importRange = importDeclaration.getVersion();

    /*
     * Go through all other bundles and see if there is matching exported
     * package. If there is then this package is not missing so continue
     * to the next one.
     */
    for (Bundle exportBundle : allBundles)
    {
      for (ExportedPackage exportedPackage : exportBundle.getExportedPackages())
      {
        if (exportedPackage.getName().equals(importPackage) && importRange.isWithinRange(exportedPackage.getVersion()))
        {
          return false;
        }
      }
    }
    
    /*
     * Make sure this bundle doesn't export the package itself. If it does then
     * this package is located within this bundle's classpath and it's not keeping
     * the bundle from getting resolved (i.e. not considered missing even if this
     * bundle is unresolved).
     */
    for (ExportPackagesDeclaration exportDeclaration : importManifest.getExportDeclarations())
    {
      if (importDeclaration.getVersion().isWithinRange(exportDeclaration.getVersion()))
      {
        for (String exportPackage : exportDeclaration.getPackages())
        {
          if (exportPackage.equals(importPackage) && importRange.isWithinRange(exportDeclaration.getVersion()))
          {
            return false;
          }
        }
      }
    }
    
    return true;
  }
  
  /**
   * Finds all uses declaration for the given exported package and matches them to the
   * import declaration of the same bundle (that exported package).
   * 
   * @param exportedPackage
   * @return A list of imported declarations that are used by the given exported package.
   */
  List<ImportedPackage> findUsesImportDeclarations(ExportedPackage exportedPackage)
  {
    List<ImportedPackage> usesImportDelcarations = new ArrayList<ImportedPackage>();
    
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
                      usesImportDelcarations.add(new ImportedPackage(importDeclaration, importPackage));
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
  
  ExportedPackage findWiredPackage(ImportPackagesDeclaration importDeclaration, String packge, Bundle bundle, List<Bundle> allBundles)
  {
    for (Bundle exportingBundle : allBundles)
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
