package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jline.Completor;
import jline.FileNameCompletor;
import jline.SimpleCompletor;

import org.osgeye.client.ServerState;
import org.osgeye.client.graphs.DotGraphGenerator;
import org.osgeye.console.commands.AbstractExecuteOnBundlesCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.console.completors.BundleNamesCompletor;
import org.osgeye.console.completors.VersionRangeCompletor;
import org.osgeye.domain.Bundle;
import org.osgeye.utils.IOUtils;

public class GraphCommand extends AbstractExecuteOnBundlesCommand
{
  static public final String GRAPH_PACKAGE_WIRING = "packagewiring";
  static public final String GRAPH_BUNDLE_FRAGMENTS = "fragments";
  
  private ServerState bundleStore;
  
  private DotGraphGenerator dotGenerator;
  private String graphType;
  
  public GraphCommand(ServerState bundleStore)
  {
    this.bundleStore = bundleStore;

    dotGenerator = new DotGraphGenerator();
  }

  @Override
  public String getName()
  {
    return "graph";
  }
  
  @Override
  public String getShortDescription()
  {
    return "Generates DOT based graphs.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }
  
  @Override
  protected Completor[] getSubCompletors()
  {
    return new Completor[] 
    {
        new SimpleCompletor(new String[] {GRAPH_PACKAGE_WIRING, GRAPH_BUNDLE_FRAGMENTS}),
        new BundleNamesCompletor(bundleStore), 
        new VersionRangeCompletor(), 
        new FileNameCompletor()
    };
  }
  
  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertMinLength(subcommands, 1);
    graphType = assertNextValue(subcommands, GRAPH_PACKAGE_WIRING, GRAPH_BUNDLE_FRAGMENTS);

    super.executeInternal(subcommands);
  }

  @Override
  protected void executeOnBundles(List<Bundle> matchingBundles, List<String> subcommands) throws InvalidCommandException
  {
    assertMinLength(subcommands, 1);
    String filePath = drain(subcommands);
    File graphFile = new File(filePath);

    String graphText;
    if (graphType.equals(GRAPH_PACKAGE_WIRING))
    {
      graphText = dotGenerator.generatePackageWiringGraph(matchingBundles, bundleStore.getBundles());
    }
    else
    {
      graphText = dotGenerator.generateBundleFragments(matchingBundles, bundleStore.getBundleMap());
    }
    
    try
    {
      printer.println("Writting " + graphType + " graph to " + graphFile.getAbsolutePath());
      IOUtils.writeToFile(graphText, graphFile);
    }
    catch (IOException ioexc)
    {
      printer.println("Unable to write graph due to io exception " + ioexc.getMessage());
    }
  }
}
