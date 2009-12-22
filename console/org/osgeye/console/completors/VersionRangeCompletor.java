package org.osgeye.console.completors;

import static org.osgeye.utils.UtilityMethods.*;

import java.util.List;

import jline.ConsoleReader;
import jline.SimpleCompletor;

public class VersionRangeCompletor extends SimpleCompletor
{
  public VersionRangeCompletor()
  {
    super(new String[] {});
  }
  
  public int complete(String buffer, int cursor, List candidateList)
  {
    buffer = (buffer == null) ? "" : buffer.substring(0, cursor);
    
    if (buffer.length() == 0)
    {
      setCandidateStrings(buildAllList());
    }
    else if (buffer.contains("*"))
    {
      setCandidateStrings(new String[] {"*"});
    }
    else
    {
      String[] candidates;
      int index = buffer.lastIndexOf(' ');
      String currentText = buffer.substring((index + 1), buffer.length());
      char startChar = currentText.charAt(0);
      
      if ((startChar == '(') || (startChar == '['))
      {
        candidates = parseVersionRange(currentText);
      }
      else if (Character.isDigit(startChar))
      {
        candidates = parseVersion(currentText);
      }
      else
      {
        candidates = new String[] {};
      }
      setCandidateStrings(appendTextToCandidates(currentText, candidates));
    }
    return super.complete(buffer, cursor, candidateList);
  }
  
  private String[] parseVersionRange(String text) 
  {
    int index = text.indexOf(',');
    
    if ((index == -1) || (index == (text.length() - 1)))
    {
      if (text.length() == 1)
      {
        return buildVersionNumbersCandiates();
      }
      else
      {
        int numDots = countChars('.', text);
        char lastChar = text.charAt(text.length() - 1);
        if ((numDots >= 2) && (lastChar != '.'))
        {
          return buildNextVersionCandiates(",");
        }
        else if (lastChar != '.')
        {
          return buildNextVersionCandiates(".");
        }
        else
        {
          return buildVersionNumbersCandiates();
        }
      }
    }
    else
    {
      String floorText = text.substring((index + 1), text.length());
      int numDots = countChars('.', floorText);
      char lastChar = floorText.charAt(floorText.length() - 1);
      
      if ((lastChar == ']') || (lastChar == ')'))
      {
        return new String[0];
      }
      else if ((numDots >= 2) && (lastChar != '.'))
      {
        return new String[] {")", "]"};
      }
      else if (lastChar != '.')
      {
        return buildNextVersionCandiates(".");
      }
      else
      {
        return buildVersionNumbersCandiates();
      }
    }
  }
  
  private String[] parseVersion(String text)
  {
    int numDots = countChars('.', text);
    if (numDots >= 3)
    {
      return new String[0];
    }
    else
    {
      char lastChar = text.charAt(text.length() - 1);
      return (lastChar != '.') ? buildNextVersionCandiates(".") : new String[] {};
    }
  }
  
  private String[] buildAllList()
  {
    String[] rangeStart = new String[] {"*", "(", "["};
    String[] versionStart = buildVersionNumbersCandiates();

    return merge(rangeStart, versionStart);
  }

  private String[] buildNextVersionCandiates(String delimmiter)
  {
    return new String[] {delimmiter + "0", delimmiter + "1", delimmiter + "2", delimmiter + "3", 
        delimmiter + "4", delimmiter + "5", delimmiter + "6", delimmiter + "7", delimmiter + "8", 
        delimmiter + "9"};
  }

  private String[] buildVersionNumbersCandiates()
  {
    return new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  }
  
  private String[] appendTextToCandidates(String text, String[] candidates)
  {
    text = (text == null) ? "" : text;
    for (int i = 0; i < candidates.length; i++)
    {
      candidates[i] = text + candidates[i];
    }
    return candidates;
  }
}