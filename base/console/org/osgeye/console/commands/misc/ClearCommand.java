package org.osgeye.console.commands.misc;

import java.io.IOException;
import java.util.List;

import jline.ConsoleReader;

import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;

public class ClearCommand extends AbstractCommand
{
  private ConsoleReader consoleReader;
  
  public ClearCommand(ConsoleReader consoleReader)
  {
    this.consoleReader = consoleReader;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    try
    {
      /*
       * Not sure if this is a bug or just something I'm doing wrong but when
       * I just issue a clearScreen() the default propmpt shows up twice on the
       * new line.
       */
      String prompt = consoleReader.getDefaultPrompt();
      consoleReader.setDefaultPrompt("");
      consoleReader.clearScreen();
      consoleReader.setDefaultPrompt(prompt);
    }
    catch (IOException ioexc)
    {}
  }

  @Override
  public String getName()
  {
    return "clear";
  }

  @Override
  public String getShortDescription()
  {
    return "Clears the screen.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.MISC;
  }
  
  protected boolean supportsOutToFile()
  {
    return false;
  }
}
