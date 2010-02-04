package org.osgeye.console.commands.misc;

import static java.lang.System.*;
import static org.osgeye.console.commands.CommandUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.Completor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.osgeye.Constants;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;

public class HelpCommand extends AbstractCommand
{
  private List<AbstractCommand> commands;
  private Map<CommandCategory, List<AbstractCommand>> categoryMap;
  private Map<String, AbstractCommand> commandMap;
  private List<CommandCategory> categories;
  
  public HelpCommand(List<AbstractCommand> commands)
  {
    this.commands = commands;
    categories = Arrays.asList(CommandCategory.DESCRIBES, CommandCategory.ACTIONS, 
        CommandCategory.DIAGNOSIS, CommandCategory.MISC);
  }

  @Override
  public String getName()
  {
    return "help";
  }
  
  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.MISC;
  }

  @Override
  public String getShortDescription()
  {
    return "Prints help for all commands.";
  }
  
  @Override
  public String getDescription()
  {
    return getShortDescription();
  }
  
  @Override
  protected boolean supportsOutToFile()
  {
    return false;
  }
  
  @Override
  public Completor[] getSubCompletors()
  {
    String[] commandNames = new String[commands.size()];
    for (int i = 0; i < commandNames.length; i++)
    {
      commandNames[i] = commands.get(i).getName();
    }
    return new Completor[] {new SimpleCompletor(commandNames), new NullCompletor()};
  }
  
  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    if ((commandMap == null) || (categoryMap == null)) initializeMaps();
    
    if (subcommands.size() == 0)
    {
      printer.println("OSGEye Console, v" + Constants.VERSION);
      printer.println("usage: <command> <options>");
      printer.println("Type 'help <command>' for help on a specific command.");
      printer.println("\nAvailable commands:");
      
      printer.pushIndent();
      for (CommandCategory category : categories)
      {
        printer.println();
        printer.println(category.text);
        printer.pushIndent();
        for (AbstractCommand command : categoryMap.get(category))
        {
          printer.printFixedSpaceLn(command.getName(), 20, command.getShortDescription());
        }
        printer.popIndent();
      }
    }
    else
    {
      String commandName = subcommands.remove(0);
      
      assertEmpty(subcommands);
      
      if (commandMap.containsKey(commandName))
      {
        AbstractCommand command = commandMap.get(commandName);
        out.println(command.getName() + " - " + command.getShortDescription());
        String description = command.getDescription();
        if (description != null)
        {
          out.println("\n" + description);
        }
      }
      else
      {
        out.println("'" + commandName + "': unknown command");
      }
    }
  }
  
  private void initializeMaps()
  {
    Collections.sort(commands);
    
    categoryMap = new HashMap<CommandCategory, List<AbstractCommand>>();
    for (AbstractCommand command : commands)
    {
      CommandCategory category = command.getCategory();
      if (categoryMap.containsKey(category))
      {
        categoryMap.get(category).add(command);
      }
      else
      {
        List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
        commands.add(command);
        categoryMap.put(category, commands);
      }
    }
    
    commandMap = new HashMap<String, AbstractCommand>();
    for (AbstractCommand command : commands)
    {
      commandMap.put(command.getName(), command);
    }    
  }
}
