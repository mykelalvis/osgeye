package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.osgeye.client.BundleStore;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.domain.manifest.Resolution;
import org.osgeye.utils.Pair;

public class ImportsCommand extends AbstractExecuteOnBundlesCommand
{
  private BundleStore bundleStore;
  
  public ImportsCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public String getName()
  {
    return "imports";
  }
  
  @Override
  public String getShortDescription()
  {
    return "Displays the imported package wiring details for matching bundles.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }

  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    assertEmpty(subcommands);
    
    for (int i = 0; i < matchingBundles.size(); i++)
    {
      Bundle bundle  = matchingBundles.get(i);
      if (i != 0) printer.println();
      printer.clearIndent();
      printer.println(bundle);
      List<Pair<ImportPackagesDeclaration, String>> matchedDeclarations = new ArrayList<Pair<ImportPackagesDeclaration, String>>();
      Manifest manifest = bundle.getManifest();

      List<ImportPackagesDeclaration> importDeclarations = (manifest == null) ? new ArrayList<ImportPackagesDeclaration>() : manifest.getImportDeclarations();
      
      for (ImportPackagesDeclaration importDeclaration : importDeclarations)
      {
        for (String packageImport : importDeclaration.getPackages())
        {
          matchedDeclarations.add(new Pair<ImportPackagesDeclaration, String>(importDeclaration, packageImport));
        }
      }
      
      Collections.sort(matchedDeclarations, new Comparator<Pair<ImportPackagesDeclaration, String>>()
      {
        public int compare(Pair<ImportPackagesDeclaration, String> pair1, Pair<ImportPackagesDeclaration, String> pair2)
        {
          return pair1.y.compareTo(pair2.y);
        }
      });
      
      printer.pushIndent();
      PACKAGE_LOOP: for (Pair<ImportPackagesDeclaration, String> packageImport : matchedDeclarations)
      {
        String resolutionStr = (packageImport.x.getResolution() == Resolution.OPTIONAL) ? " ?" : "";
        String importStr = packageImport.y + " " + packageImport.x.getVersion() + resolutionStr;
        
        for (Bundle exportBundle : bundleStore.getBundles())
        {
          if (exportBundle == bundle) continue;
          
          for (ExportedPackage exportPackage : exportBundle.getExportedPackages())
          {
            if (exportPackage.getName().equals(packageImport.y))
            {
              if (exportPackage.getImportedBundleIds().contains(bundle.getId()))
              {
                printer.println(importStr + " -> " + exportBundle + " [" + exportPackage.getVersion() + "]");
                continue PACKAGE_LOOP;
              }
              else
              {
                break; // This bundle contains the imported package but we aren't wired to it
              }
            }
          }
        }
        printer.println(importStr + " -> NOT WIRED");
      }
    }
  }
}
