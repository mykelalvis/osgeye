package org.osgeye.console.commands;

import static org.osgeye.console.commands.CommandUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import org.osgeye.console.completors.PipeToFileCompletor;
import org.osgeye.console.completors.SerialCompletor;
import org.osgeye.utils.IOUtils;

abstract public class AbstractCommand implements Comparable<AbstractCommand>
{
  abstract public String getName();
  abstract public CommandCategory getCategory();
  abstract public String getShortDescription();
  abstract protected void executeInternal(List<String> subcommands) throws InvalidCommandException;
  protected CommandPrinter printer;
  protected SimpleDateFormat dateFormatter;
  
  public AbstractCommand()
  {
    printer = new CommandPrinter();
    dateFormatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
  }
  
  public final Completor getCompletor()
  {
    Completor[] subCompletors = getSubCompletors();
    int numSumCompletors = (subCompletors == null) ? 0 : subCompletors.length;
    
    Completor[] completors = new Completor[1 + numSumCompletors];

    completors[0] = new SimpleCompletor(getName());
    for (int i = 0; i < numSumCompletors; i++)
    {
      completors[i + 1] = subCompletors[i];
    }
    
    SerialCompletor completor = new SerialCompletor(subCompletors);
    completor.setRepeatLastCompletor(repeatLastCompletor());
    
    if (supportsOutToFile())
    {
      return new PipeToFileCompletor(completor);
    }
    else
    {
      return completor;
    }
  }

  public final void execute(List<String> subcommands) throws InvalidCommandException
  {
    String filePath = null;
    if (supportsOutToFile())
    {
      for (int i = 0; i < subcommands.size(); i++)
      {
        if (subcommands.get(i).equals(">"))
        {
          if (i == (subcommands.size() - 1))
          {
            throw new InvalidCommandException("No output file specified.");
          }
          
          subcommands.remove(i);
          filePath = drain(subcommands, i);
          File file = new File(filePath);
          if (file.canWrite())
          {
            throw new InvalidCommandException("Cannot write to file path '" + filePath + "'");
          }
          else
          {
            try
            {
              printer.writeToFile(file);
            }
            catch (IOException ioexc)
            {
              throw new InvalidCommandException("Unable to write to file '" + filePath + "' due to io exception " + ioexc.getMessage());
            }
          }
          break;
        }
      }
    }
    
    boolean excAlreadyThrown = false;
    try
    {
      executeInternal(subcommands);
    }
    catch (InvalidCommandException icexc)
    {
      excAlreadyThrown = true;
      throw icexc;
    }
    finally
    {
      try
      {
        printer.endCommandSession();
      }
      catch (IOException ioexc)
      {
        if (!excAlreadyThrown)
        {
          throw new InvalidCommandException("Unable to write to file '" + filePath + "' due to io exception " + ioexc.getMessage());
        }
      }
    }
  }

  public String getDescription()
  {
    String descriptionResource = "/" + getClass().getCanonicalName().replaceAll("\\.", "/") + ".desc";
    try
    {
      InputStream descIs = getClass().getResourceAsStream(descriptionResource);
      return IOUtils.getContentAsString(descIs);
    }
    catch (Exception exc)
    {
      return null;
    }
  }
  
  public int compareTo(AbstractCommand command)
  {
    return getName().compareTo(command.getName());
  }
  
  protected Completor[] getSubCompletors()
  {
    return null;
  }
  
  protected boolean repeatLastCompletor()
  {
    return false;
  }

  /**
   * 
   * @return true if this command supports output being piped to a file using the
   * syntax 'command > filename'. false otherwise.
   */
  protected boolean supportsOutToFile()
  {
    return true;
  }

  protected boolean outToFile(List<String> subcommands) throws InvalidCommandException
  {
    if (supportsOutToFile() && subcommands.get(0).equals(">"))
    {
      assertMinLength(subcommands, 2);
      subcommands.remove(0);
      String filePath = drain(subcommands);
      File file = new File(filePath);
      if (!file.getParentFile().exists())
      {
        throw new InvalidCommandException("Invalid file path '" + filePath + "'");
      }
      else
      {
        try
        {
          printer.writeToFile(file);
          return true;
        }
        catch (IOException ioexc)
        {
          throw new InvalidCommandException("Unable to write to file '" + filePath + "' due to io exception " + ioexc.getMessage());
        }
      }
    }
    else
    {
      return false;
    }
  }
}
