package org.osgeye.console.completors;

import java.util.ArrayList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;

import org.osgeye.utils.Pair;

/**
 * This completor allows for serial completors to be stacked together. It's similiar
 * to the {@link ArgumentCompletor} except that instead of using a delimitter to find
 * completor boundaries, it uses the candidates returned from each completor to determine
 * this. This allows for completors that use spaces in their candidate list which causes
 * a problem in the {@link ArgumentCompletor} when uses spaces as the delimitter.
 * 
 */
public class SerialCompletor implements Completor
{
  private char delimitter = ' ';
  private boolean repeatLastCompletor = true;
  private List<Completor> completors;

  public SerialCompletor(List<Completor> completors)
  {
    this.completors = completors;
  }

  public SerialCompletor(Completor... completors)
  {
    this.completors = new ArrayList<Completor>();
    if (completors != null)
    {
      for (Completor completor : completors)
      {
        this.completors.add(completor);
      }
    }
  }
  
  /**
   * If true the last completor will be reused on the end of the buffer line for
   * completors.
   */
  public boolean isRepeatLastCompletor()
  {
    return repeatLastCompletor;
  }

  /**
   * If true the last completor will be reused on the end of the buffer line for
   * completors.
   */
  public void setRepeatLastCompletor(boolean repeatLastCompletor)
  {
    this.repeatLastCompletor = repeatLastCompletor;
  }

  @SuppressWarnings("unchecked")
  public int complete(String entireBuffer, int cursor, List candidates)
  {
    if ((entireBuffer == null) || (entireBuffer.length() == 0))
    {
      return (completors.size() == 0) ? 0 : completors.get(0).complete(entireBuffer, cursor, candidates);
    }
    
    String buffer = entireBuffer.substring(0, cursor);
    
    int bufferIndex = 0;
    int completorIndex = 0;
    
    try
    {
      while (bufferIndex < buffer.length())
      {
        if (buffer.charAt(bufferIndex) == delimitter)
        {
          ++bufferIndex;
          continue;
        }
        
        Completor completor = getNextCompletor(completorIndex++);
        List<String> completorCandidates = new ArrayList<String>();
        Pair<Integer, Integer> pair = findLongestCandidates(buffer, bufferIndex, completorCandidates, completor);
        
        if (pair == null)
        {
          /*
           * No matches were found for the current completor so skip to the next
           * delimiter section for the next completor.
           */
          bufferIndex = findNextDelimittedStart(buffer, bufferIndex);
        }
        else
        {
          if ((bufferIndex + pair.y) > buffer.length())
          {
            /*
             * The length of the candidates have gone past the end of the buffer
             * so this is the completor we'll use.
             */
            candidates.addAll(completorCandidates);
            return (bufferIndex + pair.x);
          }
          else
          {
            /*
             * Found a completor match but there is more left on the buffer so
             * go to the next completor.
             */
            bufferIndex += pair.y;
          }
        }
      }
      
      /*
       * We got to the end and didn't find a match so if the last character on
       * the buffer is a delimitter then we need to use the next completor
       * to fill the candidates.
       */
      if (buffer.charAt(buffer.length() - 1) == delimitter)
      {
        getNextCompletor(completorIndex).complete("", 0, candidates);
        return buffer.length();
      }
      else
      {
        return -1;
      }
    }
    catch (IllegalStateException isexc)
    {
      /*
       * Thrown if we've gone past the last completor index and repeatLastCompletor
       * is false.
       */
      return -1;
    }
  }
  
  /**
   * 
   * @param buffer
   * @param bufferStartIndex
   * @param candidates
   * @param completor
   * @return
   */
  protected Pair<Integer, Integer> findLongestCandidates(String buffer, int bufferStartIndex, List<String> candidates, Completor completor)
  {
    
    int bufferEndIndex = buffer.length() - 1;
    String remainingBuffer = buffer.substring(bufferStartIndex, buffer.length());
    
    while (bufferEndIndex >= bufferStartIndex)
    {
      String completorBuffer = buffer.substring(bufferStartIndex, (bufferEndIndex + 1));
      int completorIndex = completor.complete(completorBuffer, completorBuffer.length(), candidates);
      
      int longestCandidate = 0;
      for (int i = (candidates.size() - 1); i >= 0; i--)
      {
        String candidateBuffer = completorBuffer.substring(0, completorIndex) + candidates.get(i);
        if (!remainingBuffer.startsWith(candidateBuffer) 
           && !candidateBuffer.startsWith(remainingBuffer))
        {
          candidates.remove(i);
        }
        else
        {
          longestCandidate = Math.max(longestCandidate, candidateBuffer.length());
        }
      }
      
      if (candidates.size() > 0)
      {
        return new Pair<Integer, Integer>(completorIndex, longestCandidate);
      }
      --bufferEndIndex;
    }
    
    return null;
  }
  
  protected int findNextDelimittedStart(String buffer, int bufferIndex)
  {
    for (int i = bufferIndex; i < buffer.length(); i++)
    {
      if (buffer.charAt(i) == delimitter) return i + 1;
    }
    
    return buffer.length();
  }
  
  protected Completor getNextCompletor(int completorIndex) throws IllegalStateException
  {
    if (completorIndex < completors.size())
    {
      return completors.get(completorIndex);
    }
    else
    {
      if (repeatLastCompletor)
      {
        return completors.get(completors.size() - 1);
      }
      else
      {
        throw new IllegalStateException();
      }
    }
  }

}
