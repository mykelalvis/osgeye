package org.osgeye.domain;

import java.io.Serializable;

public class VersionRange implements Serializable
{
  static public boolean isVersionRange(String text)
  {
    if ((text == null) || (text.length() == 0)) return false;
    
    char firstChar = text.charAt(0);
    char lastChar = text.charAt(text.length() - 1);
    
    if ((firstChar != '(') || (firstChar != '[')) return false;
    
    if ((lastChar != ')') || (lastChar != ']')) return false;
    try
    {
      new VersionRange(text);
      return true;
    }
    catch (Exception exc)
    {
      return false;
    }
  }
  
  static private final long serialVersionUID = 3345630539326370123L;

  private Version floor;
  
  private boolean floorInclusive;
  
  private Version ceiling;
  
  private boolean ceilingIncusive;
  
  private String rangeStr;

  public VersionRange()
  {
    this(new Version());
  }
  
  public VersionRange(Version floor)
  {
    this(floor, true, null, false);
  }
  
  public VersionRange(Version floor, boolean floorInclusive,  Version ceiling, boolean ceilingIncusive)
  {
    this.floor = floor;
    this.floorInclusive = floorInclusive;
    this.ceiling = ceiling;
    this.ceilingIncusive = ceilingIncusive;
    
    if ((floor == null) && (ceiling == null))
    {
      rangeStr = "[" + floor.toString() + "," + '\u03C0' + "]";
    }
    else if (ceiling == null)
    {
      rangeStr = floor.toString();
    }
    else
    {
      rangeStr = (floorInclusive ? "[" : "(") + floor + "," + ceiling + (ceilingIncusive ? "]" : ")");
    }
  }
  
  public VersionRange(String rangeStr)
  {
    char ch = rangeStr.charAt(0);
    if ((ch == '[') || (ch == '('))
    {
      floorInclusive = (ch == '[');
      int index = rangeStr.indexOf(',');
      if (index == -1)
      {
        throw new IllegalArgumentException("Invalid version range " + rangeStr);
      }
      else
      {
        String floorStr = rangeStr.substring(1, index).trim();
        String ceilingStr = rangeStr.substring((index + 1), (rangeStr.length() - 1)).trim();
        
        ceilingIncusive = (rangeStr.charAt(rangeStr.length() - 1) == ']');
        floor = new Version(floorStr);
        ceiling = new Version(ceilingStr);
      }
    }
    else
    {
      floor = new Version(rangeStr);
      floorInclusive = true;
    }
    this.rangeStr = rangeStr;
  }
  
  public Version getFloor()
  {
    return floor;
  }

  public boolean isFloorInclusive()
  {
    return floorInclusive;
  }

  public Version getCeiling()
  {
    return ceiling;
  }

  public boolean isCeilingIncusive()
  {
    return ceilingIncusive;
  }
  
  public boolean isWithinRange(Version version)
  {
    int compareValue = floor.compareTo(version);
    if (compareValue == -1)
    {
      return false;
    }
    else if ((compareValue == 0) && !floorInclusive)
    {
      return false;
    }
    else if (ceiling == null)
    {
      return true;
    }
    else
    {
      compareValue = ceiling.compareTo(version);
      if (compareValue == 1)
      {
        return false;
      }
      else if ((compareValue == 0) && !ceilingIncusive)
      {
        return false;
      }
      else
      {
        return true;
      }
    }
  }
  
  public String toString()
  {
    return rangeStr;
  }
}
