package org.osgeye.console.commands.descriptions;

import static org.osgeye.console.commands.CommandUtils.*;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgeye.client.NetworkClient;
import org.osgeye.console.commands.AbstractCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Configuration;

public class ConfigurationsCommand extends AbstractCommand
{
  private NetworkClient client;
  
  public ConfigurationsCommand(NetworkClient client)
  {
    this.client = client;
  }

  @Override
  public String getName()
  {
    return "configs";
  }

  @Override
  public String getShortDescription()
  {
    return "Prints configurations.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }

  @Override
  protected void executeInternal(List<String> subcommands) throws InvalidCommandException
  {
    String filter = (subcommands.size() > 0) ? drain(subcommands) : null;
    printer.println("Sending configuration request...");
    
    try
    {
      List<Configuration> configurations = client.getConfigurations(filter);
      if (configurations.size() == 0)
      {
        printer.println("No configurations returned.");
      }
      else
      {
        printer.println();
        for (int i = 0; i < configurations.size(); i++)
        {
          Configuration configuration = configurations.get(i);
          if (i != 0) printer.println();
          printer.println("PID: " + configuration.getPid());
          printer.println("Bundle Location: " + configuration.getBundleLocation());
          printer.println("Properties:");
          printer.pushIndent();
          for (Map.Entry<String, String> entry : configuration.getProperties().entrySet())
          {
            printer.println(entry.getKey() + " = " + entry.getValue());
          }
          printer.clearIndent();
        }
      }
    }
    catch (Exception exc)
    {
      throw new InvalidCommandException("Unable to get configurations due to error " + exc.getMessage());
    }
  }
}
