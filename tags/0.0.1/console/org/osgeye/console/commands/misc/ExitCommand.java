package org.osgeye.console.commands.misc;

import static org.osgeye.console.commands.CommandUtils.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;

public class ExitCommand extends AbstractCommand
{
  private long start = System.currentTimeMillis();
  
  public ExitCommand()
  {}

  @Override
  public String getName()
  {
    return "exit";
  }

  @Override
  public String getShortDescription()
  {
    return "Exits from the console.";
  }
  
  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.MISC;
  }
  
  @Override
  protected boolean supportsOutToFile()
  {
    return false;
  }
  
  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    assertEmpty(subcommands);
    
    int totalSeconds = (int)((System.currentTimeMillis() - start) / 1000);
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
    String timeStarted = formatter.format(new Date(start));
    
    printer.println("\nOSGEye console session ending. Started at " + timeStarted + " and lasted for " + totalSeconds + " seconds.\n");
    System.exit(-1);
  }
}
