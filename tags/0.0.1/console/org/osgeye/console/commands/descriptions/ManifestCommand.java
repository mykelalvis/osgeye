package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.console.completors.VersionRangeCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.manifest.Manifest;

public class ManifestCommand extends AbstractExecuteOnBundlesCommand
{
  static public final String SUBCOMMAND_PRETTY = "pretty";

  static public final String SUBCOMMAND_RAW = "raw";
  
  private boolean printPretty;
  
  public ManifestCommand()
  {}

  @Override
  public String getName()
  {
    return "manifest";
  }

  @Override
  public String getShortDescription()
  {
    return "Prints the manifest file for matching bundles.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }

  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] {new BundleNamesCompletor(bundleStore), 
        new SimpleCompletor(new String[] {SUBCOMMAND_PRETTY, SUBCOMMAND_RAW}),
        new VersionRangeCompletor()};
  }
  
  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertMinLength(subcommands, 1);
    
    printPretty = (subcommands.size() < 2) ? true 
        : (assertValue(subcommands, 1, SUBCOMMAND_PRETTY, SUBCOMMAND_RAW).equals(SUBCOMMAND_PRETTY));

    super.executeInternal(subcommands);
  }


  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    assertEmpty(subcommands);
    
    for (int i = 0; i < matchingBundles.size(); i++)
    {
      Bundle bundle = matchingBundles.get(i);
      if (i != 0) printer.println("\n");
      
      printer.println(bundle + "\n");
      Manifest manifest = bundle.getManifest();
      if (manifest == null)
      {
        printer.println("NO MANIFEST FOUND");
      }
      else
      {
        printer.println(printPretty ? manifest.getPrettyFile() : manifest.getFile());
      }
    }
  }
}
