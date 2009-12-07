package org.osgeye.console.completors;

import java.util.List;

import jline.Completor;
import jline.FileNameCompletor;

public class PipeToFileCompletor implements Completor
{
  private SerialCompletor completor;
  private FileNameCompletor fileNameCompletor;
  
  public PipeToFileCompletor(SerialCompletor completor)
  {
    this.completor = completor;
    fileNameCompletor = new FileNameCompletor();
  }

  @SuppressWarnings("unchecked")
  public int complete(String buffer, int cursor, List candidates)
  {
    buffer = buffer.substring(0, cursor);
    
    int index = buffer.indexOf("> ");
    
    if (index == -1)
    {
      return completor.complete(buffer, buffer.length(), candidates);
    }
    else
    {
      buffer = (index == (buffer.length() - 1)) ? "" : buffer.substring((index + 2), buffer.length());
      return (index + 2 + fileNameCompletor.complete(buffer, buffer.length(), candidates));
    }
  }

}
