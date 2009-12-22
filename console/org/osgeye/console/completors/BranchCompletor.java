package org.osgeye.console.completors;

import java.util.List;

import jline.Completor;

import org.osgeye.utils.Pair;

public class BranchCompletor implements Completor
{
  private List<Pair<String, Completor>> completorsPair;
  
  public BranchCompletor(List<Pair<String, Completor>> completorsPair)
  {
    this.completorsPair = completorsPair;
  }

  @SuppressWarnings("unchecked")
  public int complete(String buffer, int cursor, List candidates)
  {
    buffer = (buffer == null) ? "" : buffer.substring(0, cursor);
    
    int index = (buffer == null) ? -1 : buffer.indexOf(' ');
    if (index == -1)
    {
      for (Pair<String, Completor> completorPair : completorsPair)
      {
        if ((buffer == null) || completorPair.x.startsWith(buffer))
        {
          candidates.add(completorPair.x + " ");
        }
      }
      return 0;
    }
    else
    {
      String command = buffer.substring(0, index);
      for (Pair<String, Completor> completorPair : completorsPair)
      {
        if (completorPair.x.equals(command)) 
        {
          String completorBuffer = (index == (buffer.length() - 1)) ? "" : buffer.substring((index + 1), buffer.length());
          int completorIndex = completorPair.y.complete(completorBuffer, completorBuffer.length(), candidates);
          return (index + 1 + completorIndex);
        }
      }
      return -1;
    }
  }

}
