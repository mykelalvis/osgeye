package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;
import java.util.List;

import org.osgeye.client.ServerState;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Framework;

public class FrameworkCommand extends AbstractCommand
{
  private ServerState bundleStore;
  
  public FrameworkCommand(ServerState bundleStore)
  {
    this.bundleStore = bundleStore;
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }

  @Override
  public String getName()
  {
    return "framework";
  }

  @Override
  public String getShortDescription()
  {
    return "Prints the current state of the OSGi framework.";
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertEmpty(subcommands);
    
    Framework frameworkState = bundleStore.getFramework();
    
    printer.println("Framework State:");
    printer.pushIndent();
    printer.println("Start Level: " + frameworkState.getStartLevel());
    printer.println("Initial Bundle Start Level: " + frameworkState.getInitialBundleStartLevel());
    printer.popIndent();
  }

}
