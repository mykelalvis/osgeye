package org.osgeye.client.graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.utils.Pair;

public class DotGraphGenerator
{
  public DotGraphGenerator()
  {
    
  }

  public String generatePackageWiringGraph(List<Bundle> bundles, List<Bundle> allBundles) 
  {
    DotStringBuilder builder = new DotStringBuilder();
    builder.appendDigraphOpening(System.currentTimeMillis());

    for (Bundle bundle : bundles)
    {
      generatePackageWiring(bundle, allBundles, builder);
    }
    
    builder.closeGraph();
    return builder.toString();
  }
  
  public String generateBundleFragments(List<Bundle> bundles, Map<Long, Bundle> bundleMap)
  {
    List<Bundle> hostAndHostlessBundles = new ArrayList<Bundle>();
    for (Bundle bundle : bundles)
    {
      if (hostAndHostlessBundles.contains(bundle)) continue;
      
      if (!bundle.isFragment())
      {
        hostAndHostlessBundles.add(bundle);
      }
      else
      {
        if (bundle.getHostBundleIds().size() == 0)
        {
          hostAndHostlessBundles.add(bundle); // This fragment has not hosts but we still want to graph it.
        }
        else
        {
          for (Long hostBundleId : bundle.getHostBundleIds())
          {
            Bundle hostBundle = bundleMap.get(hostBundleId);
            if (!hostAndHostlessBundles.contains(hostBundle)) 
            {
              hostAndHostlessBundles.add(hostBundle);
            }
          }
        }
      }
    }
    
    
    DotStringBuilder builder = new DotStringBuilder();
    builder.appendDigraphOpening(System.currentTimeMillis());
    
    List<Long> hostIds = new ArrayList<Long>();
    List<Long> fragmentIds = new ArrayList<Long>();
    
    for (Bundle bundle : hostAndHostlessBundles)
    {
      if (bundle.isFragment())
      {
        fragmentIds.add(bundle.getId());
        builder.appendDeclaration(bundle.getId(), new Pair("label", bundle), 
                                                  new Pair("shape", "ellipse"),
                                                  new Pair("style", "dashed"),
                                                  new Pair("color", "black"),
                                                  new Pair("fontsize", "10"));
      }
      else
      {
        hostIds.add(bundle.getId());
        builder.appendDeclaration(bundle.getId(), new Pair("label", bundle), 
                                                  new Pair("shape", "ellipse"),
                                                  new Pair("style", "filled"),
                                                  new Pair("color", "black"),
                                                  new Pair("fillcolor", "lightblue"),
                                                  new Pair("fontsize", "10"));
                                            
        for (Long fragmentBundleId : bundle.getFragmentBundleIds())
        {
          fragmentIds.add(fragmentBundleId);
          Bundle fragmentBundle = bundleMap.get(fragmentBundleId);
          builder.appendEdge(fragmentBundle.getId(), bundle.getId());
          builder.appendDeclaration(fragmentBundle.getId(), new Pair("label", fragmentBundle), 
                                                            new Pair("shape", "ellipse"),
                                                            new Pair("style", "dashed"),
                                                            new Pair("color", "black"),
                                                            new Pair("fontsize", "10"));
        }
      }
    }
    
    builder.appendSameRank(hostIds).appendSameRank(fragmentIds).closeGraph();
    return builder.toString();
  }

  @SuppressWarnings("unchecked")
  protected void generatePackageWiring(Bundle bundle, List<Bundle> allBundles, DotStringBuilder builder) 
  {
    Manifest manifest = bundle.getManifest();
    List<ImportPackagesDeclaration> importDeclarations = (manifest == null) ? new ArrayList<ImportPackagesDeclaration>() : manifest.getImportDeclarations();
    List<String> importNames = new ArrayList<String>();
    for (ImportPackagesDeclaration importDeclaration : importDeclarations)
    {
      for (String importName : importDeclaration.getPackages()) importNames.add(importName);
    }
    Collections.sort(importNames);

    List<Pair<String, String>> imports = new ArrayList<Pair<String, String>>();
    for (String importName : importNames) 
    {
      String importId = createPackageId(bundle, importName);
      imports.add(new Pair<String, String>(importId, importName));
      builder.appendEdge(bundle.getId(), importId);
    }
    
    List<Bundle> matchedExportedBundles = new ArrayList<Bundle>();
    List<Pair<String, String>> resolvedImports = new ArrayList<Pair<String,String>>();
    List<Pair<String, String>> unresolvedImports = new ArrayList<Pair<String,String>>();
    
    PACKAGE_LOOP: for (Pair<String, String> packageImport : imports)
    {
      for (Bundle exportBundle : allBundles)
      {
        if (exportBundle == bundle) continue;
        
        for (ExportedPackage exportPackage : exportBundle.getExportedPackages())
        {
          if (exportPackage.getName().equals(packageImport.y))
          {
            if (exportPackage.getImportedBundleIds().contains(bundle.getId()))
            {
              builder.appendEdge(packageImport.x, exportBundle.getId());
              matchedExportedBundles.add(exportBundle);
              resolvedImports.add(packageImport);
              continue PACKAGE_LOOP;
            }
            else
            {
              break; // This bundle contains the imported package but we aren't wired to it
            }
          }
        }
      }
      
      unresolvedImports.add(packageImport);
    }
    
    builder.appendDeclaration(bundle.getId(), new Pair("label", bundle), 
                                              new Pair("group", bundle.getId()),
                                              new Pair("shape", "ellipse"),
                                              new Pair("style", "filled"),
                                              new Pair("color", "black"),
                                              new Pair("fillcolor", "lightblue"),
                                              new Pair("fontsize", "10"));

    for (Pair<String, String> resolvedImport : resolvedImports)
    {
      builder.appendDeclaration(resolvedImport.x, new Pair("label", resolvedImport.y),
                                                  new Pair("group", bundle.getId()),
                                                  new Pair("shape", "box"),
                                                  new Pair("style", "filled"),
                                                  new Pair("color", "black"),
                                                  new Pair("fillcolor", "lawngreen"),
                                                  new Pair("fontsize", "10"));
    }

    for (Pair<String, String> unresolvedImport : unresolvedImports)
    {
      builder.appendDeclaration(unresolvedImport.x, new Pair("label", unresolvedImport.y), 
                                                  new Pair("shape", "box"),
                                                  new Pair("style", "dashed"),
                                                  new Pair("color", "black"),
                                                  new Pair("fontsize", "10"));
    }
    
    for (Bundle matchedExportBundle : matchedExportedBundles)
    {
      builder.appendDeclaration(matchedExportBundle.getId(), new Pair("label", matchedExportBundle));
    }
  }
  
  private String createPackageId(Bundle bundle, String packageName)
  {
    packageName = packageName.replace('.', '_');
    packageName = packageName.replace('-', '_');
    return packageName + "_" + bundle.getId();
  }

}
