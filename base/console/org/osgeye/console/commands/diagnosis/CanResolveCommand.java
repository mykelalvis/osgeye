package org.osgeye.console.commands.diagnosis;

import static org.osgeye.console.commands.CommandUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jline.Completor;
import jline.FileNameCompletor;

import org.osgeye.client.BundleStore;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.ImportedPackage;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.utils.IOUtils;
import org.osgeye.utils.OSGiUtils;

public class CanResolveCommand extends AbstractCommand
{
  private BundleStore bundleStore;
  private DiagnosisUtils diagnosisUtils;
  
  public CanResolveCommand(BundleStore bundleStore)
  {
    this.bundleStore = bundleStore;
    diagnosisUtils = new DiagnosisUtils();
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DIAGNOSIS;
  }

  @Override
  public String getName()
  {
    return "canresolve";
  }

  @Override
  public String getShortDescription()
  {
    return "Attempts to determine if a manifest file can resolve against the current system.";
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new FileNameCompletor()};
  }
  
  @Override
  protected boolean supportsOutToFile()
  {
    return false;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertMinLength(subcommands, 1);
    String filePath = drain(subcommands);
    File file = new File(filePath);
    
    if (!file.isFile())
    {
      printer.println("Cound not find the file " + filePath);
      return;
    }
    
    try
    {
      String manifestContent;
      
      if (file.getName().endsWith(".jar"))
      {
        manifestContent = OSGiUtils.getManifest(file);
        
        if (manifestContent == null)
        {
          printer.println("Unable to find manifest file in bundle jar file.");
          return;
        }
      }
      else
      {
        manifestContent = IOUtils.getContentsAsString(file);
      }
      
      Manifest manifest = new Manifest(manifestContent);

      List<Bundle> allBundles = bundleStore.getBundles();
      List<ImportedPackage> missingPackages = diagnosisUtils.findMissingImports(manifest, true, false, allBundles);
      List<UsesConflict> usesConflicts = diagnosisUtils.findUsesConflicts(manifest, allBundles);
      
      if ((missingPackages.size() == 0) && (usesConflicts.size() == 0))
      {
        printer.println("No conflicts found. The bundle should resolve.");
      }
      else
      {
        printer.println("The following conflicts where found for " + manifest.getSymbolicName() + "\n");
        printer.pushIndent();
        if (missingPackages.size() > 0)
        {
          printer.println("Missing Mandatory Package Imports:");
          printer.pushIndent();

          for (ImportedPackage missingPackage : missingPackages)
          {
            printer.println(missingPackage);
          }

          printer.popIndent();
        }
        
        if (usesConflicts.size() > 0)
        {
          printer.println("Possible Uses Conflicts:");
          printer.pushIndent();
          
          for (UsesConflict usesConflict : usesConflicts)
          {
            printer.println(usesConflict.exportedImport);
            printer.pushIndent();
            
            printer.println("Exporting Bundle: " + usesConflict.exportBundle);
            printer.println("Exported Package: " + usesConflict.exportedPackage);
            printer.println("Exported Package Uses: " + usesConflict.usesWiredExport);
            printer.println("Conflicting Import: " + usesConflict.usesConflictImport);
            
            printer.popIndent();
          }
          
          printer.popIndent();
        }
      }

    }
    catch (IOException ioexc)
    {
      printer.println("Unable to read file due to io error " + ioexc.getMessage());
    }
    
  }
}
