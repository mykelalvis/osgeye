package org.osgeye.console.commands.diagnosis;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import org.osgeye.client.ServerState;
import org.osgeye.client.diagnosis.DiagnosisUtils;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.console.completors.VersionRangeCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;
import org.osgeye.domain.ImportedPackage;
import org.osgeye.domain.manifest.Resolution;

public class MissingWiringCommand extends AbstractExecuteOnBundlesCommand
{
  static public final String OPTIONAL = "optional";

  static public final String MANDATORY = "mandatory";

  static public final String ALL = "all";

  private ServerState bundleStore;
  
  private DiagnosisUtils diagnosisUtils;
  
  public MissingWiringCommand(ServerState bundleStore)
  {
    this.bundleStore = bundleStore;
    diagnosisUtils = new DiagnosisUtils();
  }

  @Override
  public String getName()
  {
    return "missing";
  }
  
  @Override
  public String getShortDescription()
  {
    return "Displays packages for bundles that are not wired.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DIAGNOSIS;
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new BundleNamesCompletor(bundleStore), 
        new VersionRangeCompletor(), 
        new SimpleCompletor(new String[] {ALL, OPTIONAL, MANDATORY})};
  }
  
  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    assertMaxLength(subcommands, 1);
    boolean printMandatory = true;
    boolean printOptional = true;
    if (subcommands.size() == 1)
    {
      String subcmd = assertNextValue(subcommands, ALL, OPTIONAL, MANDATORY);
      printMandatory = subcmd.equals(MANDATORY) || subcmd.equals(ALL);
      printOptional = subcmd.equals(OPTIONAL) || subcmd.equals(ALL);
    }
    
    boolean firstPrint = true;

    List<Bundle> allBundles = bundleStore.getBundles();
    
    if (printMandatory)
    {
      /*
       * First go through and find all the bundles that are unresolved and print
       * their missing packages as those the ones people care about the most. 
       */
      for (Bundle bundle : matchingBundles)
      {
        if (bundle.getState() == BundleState.INSTALLED)
        {
          List<ImportedPackage> missingPackages = diagnosisUtils.findMissingImports(bundle.getManifest(), true, printOptional, allBundles);
          if (missingPackages.size() > 0)
          {
            if (!firstPrint) printer.println();
            firstPrint = false;
            printer.println(bundle + " (" + bundle.getState() + ")");
            printer.pushIndent();
            
            boolean firstLoopPrinted = false;
            for (ImportedPackage missingPackage : missingPackages)
            {
              if (missingPackage.getDeclaration().getResolution() == Resolution.MANDATORY)
              {
                printer.println(missingPackage);
                firstLoopPrinted = true;
              }
            }
    
            boolean secondLoopPrinted = false;
            if (printOptional)
            {
              for (ImportedPackage missingPackage : missingPackages)
              {
                if (missingPackage.getDeclaration().getResolution() == Resolution.OPTIONAL)
                {
                  if (firstLoopPrinted && !secondLoopPrinted)
                  {
                    printer.println();
                    secondLoopPrinted = true;
                  }
                  printer.println(missingPackage);
                }
              }
            }
            printer.popIndent();
          }
        }
      }
    }

    if (printOptional)
    {
      for (Bundle bundle : matchingBundles)
      {
        if (bundle.getState() != BundleState.INSTALLED)
        {

          List<ImportedPackage> missingPackages = diagnosisUtils.findMissingImports(bundle.getManifest(), 
              false, true, allBundles);  // Resolved bundles can only have missing optional package imports.

          if (missingPackages.size() > 0)
          {
            if (!firstPrint) printer.println();
            firstPrint = false;
            printer.println(bundle + " (" + bundle.getState() + ")");
            printer.pushIndent();

            for (ImportedPackage missingPackage : missingPackages)
            {
              printer.println(missingPackage);
            }
    
            printer.popIndent();
          }
        }
      }
    }
    
    if (firstPrint)
    {
      printer.println("None of the matching bundles had unwired packages.");
    }
  }
}
