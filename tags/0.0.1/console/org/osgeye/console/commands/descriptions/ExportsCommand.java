package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.List;

import org.osgeye.console.BundleStore;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;

public class ExportsCommand extends AbstractExecuteOnBundlesCommand
{
  private BundleStore bundleStore;
  
  public ExportsCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public String getName()
  {
    return "exports";
  }

  @Override
  public String getShortDescription()
  {
    return "Displays the exported package wiring details for matching bundles.";
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
      
      List<ExportedPackage> exportedPckages = bundle.getExportedPackages();
      printer.pushIndent();
      for (ExportedPackage exportedPackage : exportedPckages)
      {
        String exportedPackageName = exportedPackage.getName();
        printer.println(exportedPackage.getName() + " " + exportedPackage.getVersion());
        printer.pushIndent();
        for (Long importingBundleId : exportedPackage.getImportedBundleIds())
        {
          Bundle importingBundle = bundleStore.getBundle(importingBundleId);
          /*
           * Find the import declaration that resulted in this wiring.
           */
          ImportPackagesDeclaration importDeclaration = null;
          Manifest manifest = importingBundle.getManifest();
          if (manifest != null)
          {
            for (ImportPackagesDeclaration impDecl : manifest.getImportDeclarations())
            {
              if (impDecl.getPackages().contains(exportedPackageName))
              {
                importDeclaration = impDecl;
                break;
              }
            }
          }

          printer.println(importingBundle + ((importDeclaration == null) ? "" : (" " + importDeclaration.getVersion())));
        }
        printer.popupIndent();
      }
      printer.popupIndent();
    }
  }
}
