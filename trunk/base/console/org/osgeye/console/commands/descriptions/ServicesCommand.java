package org.osgeye.console.commands.descriptions;

import java.util.List;
import java.util.Map;

import org.osgeye.client.ServerState;
import org.osgeye.console.commands.AbstractExecuteOnServiceCommand;
import org.osgeye.console.commands.CommandCategory;
import org.osgeye.console.commands.InvalidCommandException;
import org.osgeye.domain.Service;

public class ServicesCommand extends AbstractExecuteOnServiceCommand
{
  public ServicesCommand(ServerState bundleStore)
  {
    super(bundleStore);
  }

  @Override
  public String getName()
  {
    return "services";
  }

  @Override
  public String getShortDescription()
  {
    return "List the matching service interfaces.";
  }

  @Override
  public CommandCategory getCategory()
  {
    return CommandCategory.DESCRIBES;
  }

  @Override
  protected void executeOnServices(List<String> matchedInterfaces, Map<String, List<Service>> allServices, List<String> subcommands)
      throws InvalidCommandException
  {
    for (int i = 0; i < matchedInterfaces.size(); i++)
    {
      String interfaceName = matchedInterfaces.get(i);
      if (i != 0) printer.println();
      printer.println(interfaceName);
      printer.pushIndent();
      
      List<Service> services = allServices.get(interfaceName); 
      for (int j = 0; j < services.size(); j++)
      {
        Service service = services.get(j);
        printer.println();
        printer.println(service.getBundle());
        printer.pushIndent();
        if (service.getId() != null)
        {
          printer.println("ID: " + service.getId());
        }

        if (service.getPid() != null)
        {
          printer.println("Service PID: " + service.getPid());
        }
        
        if (service.getDescription() != null)
        {
          printer.println("Description: " + service.getDescription());
        }
        
        if (service.getRanking() != null)
        {
          printer.println("Ranking: " + service.getRanking());
        }
        
        if (service.getVendor() != null)
        {
          printer.println("Vendor: " + service.getVendor());
        }
        printer.popIndent();
      }
      printer.popIndent();
    }
    
  }
}
