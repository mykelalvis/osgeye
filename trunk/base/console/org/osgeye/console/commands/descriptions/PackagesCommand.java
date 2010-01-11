package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.Completor;

import org.osgeye.client.ServerState;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.PackagesCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.utils.OSGiUtils;
import org.osgeye.utils.Pair;

public class PackagesCommand extends AbstractCommand
{
  private ServerState bundleStore;
  
  public PackagesCommand(ServerState bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public String getName()
  {
    return "packages";
  }

  @Override
  public String getShortDescription()
  {
    return "Displays the wiring details for matching packages.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }
  
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new PackagesCompletor(bundleStore)};
  }
  
  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    String packagePattern = (subcommands.size() == 0) ? ".*" : subcommands.remove(0);
    if (packagePattern.equals("*")) packagePattern = ".*";
    
    assertEmpty(subcommands);

    List<String> packageNames = new ArrayList<String>();
    Map<String, Pair<List<ExportedPackage>, List<Bundle>>> packageMap 
        = new HashMap<String, Pair<List<ExportedPackage>, List<Bundle>>>();
    
    List<Bundle> bundles = bundleStore.getBundles();
    for (Bundle bundle : bundles)
    {
      List<ExportedPackage> exportedPckages = bundle.getExportedPackages();
      for (ExportedPackage exportedPackage : exportedPckages)
      {
        String packageName = exportedPackage.getName();
        if (packageName.matches(packagePattern))
        {
          if (!packageMap.containsKey(packageName))
          {
            packageNames.add(packageName);
            List<ExportedPackage> matchedPackages = new ArrayList<ExportedPackage>();
            matchedPackages.add(exportedPackage);
            Pair<List<ExportedPackage>, List<Bundle>> pair = new Pair<List<ExportedPackage>, List<Bundle>>();

            pair.x = matchedPackages;
            pair.y = new ArrayList<Bundle>();
            packageMap.put(packageName, pair);
          }
          else
          {
            packageMap.get(packageName).x.add(exportedPackage);
          }
        }
      }
    }
    
    for (Bundle bundle : bundles)
    {
      if (bundle.getManifest() == null) continue;
      
      List<ImportPackagesDeclaration> importDeclarations = bundle.getManifest().getImportDeclarations();
      
      for (ImportPackagesDeclaration importDelcaration : importDeclarations)
      {
        for (String importPackage : importDelcaration.getPackages())
        {
          if (importPackage.matches(packagePattern))
          {
            if (!packageMap.containsKey(importPackage))
            {
              List<Bundle> importBundles = new ArrayList<Bundle>();
              importBundles.add(bundle);
              Pair<List<ExportedPackage>, List<Bundle>> pair = new Pair<List<ExportedPackage>, List<Bundle>>();
              pair.x = new ArrayList<ExportedPackage>();
              pair.y = importBundles;
              packageMap.put(importPackage, pair);
              packageNames.add(importPackage);
            }
            else
            {
              packageMap.get(importPackage).y.add(bundle);
            }
          }
        }
      }
    }
    
    if (packageNames.size() == 0)
    {
      printer.println("No packages matched the given pattern.");
    }
    else
    {
      Collections.sort(packageNames);
      for (int i = 0; i < packageNames.size(); i++)
      {
        String packageName = packageNames.get(i);
        List<Long> importedBundleIds = new ArrayList<Long>();
        
        if (i != 0) printer.println();
        printer.println(packageName);
        List<ExportedPackage> exportedPackages = packageMap.get(packageName).x;
        Collections.sort(exportedPackages);
        List<Long> exportedBundleIds = new ArrayList<Long>();

        printer.pushIndent();
        for (ExportedPackage exportedPackage : exportedPackages)
        {
          Bundle exportingBundle = exportedPackage.getBundle();
          exportedBundleIds.add(exportingBundle.getId());
          printer.println(exportedPackage.getVersion() + " - " + exportingBundle);
          printer.pushIndent();
          for (Long importingBundleId : exportedPackage.getImportedBundleIds())
          {
            Bundle importingBundle = bundleStore.getBundle(importingBundleId);
            importedBundleIds.add(importingBundleId);
            
            ImportPackagesDeclaration importDeclaration = OSGiUtils.findImportDeclaration(importingBundle, exportedPackage.getName());
            printer.println(importingBundle + ((importDeclaration == null) ? "" : (" " + importDeclaration.getVersion())));
          }
          printer.popIndent();
        }
        
        List<Bundle> importBundles = packageMap.get(packageName).y;
        List<Bundle> unwiredBundles = new ArrayList<Bundle>();
        for (Bundle importBundle : importBundles)
        {
          if (!importedBundleIds.contains(importBundle.getId()) && !exportedBundleIds.contains(importBundle.getId()))
          {
            unwiredBundles.add(importBundle);
          }
        }
        
        if (unwiredBundles.size() > 0)
        {
          printer.println("NOT WIRED");
          printer.pushIndent();
          for (Bundle unwiredBundle : unwiredBundles) 
          {
            ImportPackagesDeclaration importDeclaration = OSGiUtils.findImportDeclaration(unwiredBundle, packageName);
            printer.println(unwiredBundle + ((importDeclaration == null) ? "" : (" " + importDeclaration.getVersion())));
          }
          printer.popIndent();
        }
        printer.popIndent();
      }
    }
  }
}
