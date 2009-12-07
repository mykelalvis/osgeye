package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.Date;
import java.util.List;

import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.Service;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.domain.manifest.Resolution;

public class BundlesCommand extends AbstractExecuteOnBundlesCommand
{
  public BundlesCommand()
  {}

  @Override
  public String getName()
  {
    return "bundles";
  }

  @Override
  public String getShortDescription()
  {
    return "Prints details on matching bundles.";
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
      Bundle bundle = matchingBundles.get(i);
      if (i != 0) printer.println("");
      printer.println(bundle);
      printer.pushIndent();
      printer.println("ID: " + bundle.getId());
      printer.println("Type: " + (bundle.isFragment() ? "Fragment" : "Normal"));
      printer.println("State: " + bundle.getState().getName());
      printer.println("Last Modified: " + dateFormatter.format(new Date(bundle.getLastModified())));
      printer.println("Location: " + bundle.getLocation());
      printer.println("Start Level: " + bundle.getStartLevel());
      
      if (bundle.isHost())
      {
        printer.println("Attached Bundle Fragments:");
        printer.pushIndent();
        for (Long bundleId : bundle.getFragmentBundleIds())
        {
          printer.println(bundleStore.getBundle(bundleId));
        }
        printer.popupIndent();
      }
      
      if (bundle.isFragment())
      {
        printer.println("Bundle Hosts:");
        printer.pushIndent();
        for (Long bundleId : bundle.getHostBundleIds())
        {
          printer.println(bundleStore.getBundle(bundleId));
        }
        printer.popupIndent();
      }
      
      if (bundle.getServices().size() > 0)
      {
        printer.println("Services:");
        printer.pushIndent();
        for (Service service : bundle.getServices())
        {
          for (String serviceInterface : service.getInterfaces())
          {
            printer.println(serviceInterface);
          }
        }
        printer.popupIndent();
      }
      
      if (bundle.getRequiredBundleIds().size() > 0)
      {
        printer.println("Required Bundles:");
        printer.pushIndent();
        for (Long bundleId : bundle.getRequiredBundleIds())
        {
          printer.println(bundleStore.getBundle(bundleId));
        }
        printer.popupIndent();
      }
      
      if (bundle.getExportedPackages().size() > 0)
      {
        printer.println("Exported Packages:");
        printer.pushIndent();
        for (ExportedPackage exportedPackage : bundle.getExportedPackages())
        {
          printer.println(exportedPackage.getName() + " " + exportedPackage.getVersion());
        }
        printer.popupIndent();
      }

      Manifest manifest = bundle.getManifest();
      if ((manifest != null) && (manifest.getImportDeclarations() != null) && (manifest.getImportDeclarations().size() > 0))
      {
        printer.println("Imported Packages:");
        printer.pushIndent();
        for (ImportPackagesDeclaration importedDeclaration : manifest.getImportDeclarations())
        {
          for (String importedPackage : importedDeclaration.getPackages())
          {
            printer.println(importedPackage + " " + importedDeclaration.getVersion() + ((importedDeclaration.getResolution() == Resolution.OPTIONAL) ? " ?" : ""));
          }
        }
        printer.popupIndent();
      }

      printer.clearIndent();
    }
  }
}
