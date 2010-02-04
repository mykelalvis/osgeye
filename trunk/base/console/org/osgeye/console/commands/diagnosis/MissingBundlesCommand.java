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
import org.osgeye.domain.manifest.RequireBundleDeclaration;
import org.osgeye.domain.manifest.Resolution;

public class MissingBundlesCommand extends AbstractExecuteOnBundlesCommand
{
  static public final String OPTIONAL = "optional";

  static public final String MANDATORY = "mandatory";

  static public final String ALL = "all";

  private ServerState serverState;
  
  private DiagnosisUtils diagnosisUtils;
  
  private boolean firstPrint;
  
  public MissingBundlesCommand(ServerState bundleStore)
  {
    this.serverState = bundleStore;
    diagnosisUtils = new DiagnosisUtils();
  }

  @Override
  public String getName()
  {
    return "missingbundles";
  }
  
  @Override
  public String getShortDescription()
  {
    return "Displays unwired bundles.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DIAGNOSIS;
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new BundleNamesCompletor(serverState), 
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
    
    firstPrint = true;

    List<Bundle> allBundles = serverState.getBundles();
    
    /*
     * First go through and find all the bundles that are unresolved and print
     * their missing packages as those the ones people care about the most. 
     */
    for (Bundle bundle : matchingBundles)
    {
      if (bundle.getState() == BundleState.INSTALLED)
      {
        printMissingBundles(bundle, true, printOptional, allBundles);
      }
    }

    for (Bundle bundle : matchingBundles)
    {
      if (bundle.getState() != BundleState.INSTALLED)
      {
        printMissingBundles(bundle, printMandatory, printOptional, allBundles);
      }
    }
    
    if (firstPrint)
    {
      printer.println("None of the matching bundles had unwired bundles.");
    }
  }
  
  private void printMissingBundles(Bundle bundle, boolean printMandatory, boolean printOptional, List<Bundle> allBundles)
  {
    List<RequireBundleDeclaration> missingBundles = diagnosisUtils.findMissingBundles(bundle.getManifest(), printMandatory, printOptional, allBundles);
    if (missingBundles.size() > 0)
    {
      if (!firstPrint) printer.println();
      firstPrint = false;
      printer.println(bundle + " (" + bundle.getState() + ")");
      printer.pushIndent();
      
      boolean firstLoopPrinted = false;
      for (RequireBundleDeclaration missingBundle : missingBundles)
      {
        if (missingBundle.getResolution() == Resolution.MANDATORY)
        {
          printer.println(missingBundle);
          firstLoopPrinted = true;
        }
      }

      boolean secondLoopPrinted = false;
      if (printOptional)
      {
        for (RequireBundleDeclaration missingBundle : missingBundles)
        {
          if (missingBundle.getResolution() == Resolution.OPTIONAL)
          {
            if (firstLoopPrinted && !secondLoopPrinted)
            {
              printer.println();
              secondLoopPrinted = true;
            }
            printer.println(missingBundle);
          }
        }
      }
      printer.popIndent();
    }
  }
}
