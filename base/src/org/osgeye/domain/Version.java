package org.osgeye.domain;

import static org.osgeye.utils.UtilityMethods.*;

import java.io.Serializable;

public class Version implements Comparable<Version>, Serializable
{
  static public boolean isVersion(String text)
  {
    try
    {
      new Version(text);
      return true;
    }
    catch (Exception exc)
    {
      return false;
    }
  }
  
  private static final long serialVersionUID = -829782696930733089L;

  private int major;
  
  private int minor;
  
  private int micro;
  
  private String qualifier;
  
  private String versionStr;
  
  public Version(org.osgi.framework.Version version)
  {
    this(version.getMajor(), version.getMinor(), version.getMicro(), version.getQualifier());
  }
  
  public Version()
  {
    createVersionStr();
  }
  
  public Version(int major, int minor, int micro)
  {
    this.major = major;
    this.minor = minor;
    this.micro = micro;
    createVersionStr();
  }
  
  public Version(int major, int minor, int micro, String qualifier)
  {
    this.major = major;
    this.minor = minor;
    this.micro = micro;
    this.qualifier = ((qualifier == null) || (qualifier.length()  == 0)) ? null : qualifier;
    createVersionStr();
  }
  
  public Version(String versionStr)
  {
    String[] values = versionStr.split("\\.");
    
    if (values.length > 4)
    {
      throw new IllegalArgumentException("Illegal version " + versionStr);
    }

    this.versionStr = versionStr;
    major = Integer.parseInt(values[0]);
    if (values.length > 1)  minor = Integer.parseInt(values[1]);
    if (values.length > 2)  micro = Integer.parseInt(values[2]);
    if (values.length > 3) qualifier = values[3];
  }
    
  public int getMajor()
  {
    return major;
  }

  public int getMinor()
  {
    return minor;
  }

  public int getMicro()
  {
    return micro;
  }

  public String getQualifier()
  {
    return qualifier;
  }

  public String toString()
  {
    return versionStr;
  }
  
  public int hashCode()
  {
    return versionStr.hashCode();
  }
  
  public boolean equals(Object obj)
  {
    if (obj instanceof Version)
    {
      Version version = (Version)obj;
      return ((major == version.major) && (minor == version.minor) 
          && (micro == version.micro) && nullEquals(qualifier, version.qualifier));
    }
    else
    {
      return false;
    }
  }

  public int compareTo(Version version)
  {
    int[] myVals = new int[] {major, minor, micro};
    int[] yourVals = new int[] {version.major, version.minor, version.micro};
    
    for (int i = 0; i < 3; i++)
    {
      if (myVals[i] != yourVals[i])
      {
        return (myVals[i] < yourVals[i]) ? 1 : -1;
      }
    }
    
    if ((qualifier != null) && (version.qualifier != null))
    {
      return qualifier.compareTo(version.qualifier);
    }
    else if (qualifier != null)
    {
      return -1;
    }
    else if (version.qualifier != null)
    {
      return 1;
    }
    else
    {
      return 0;
    }
  }
  
  
  private void createVersionStr()
  {
    versionStr = major + "." + minor + "." + micro;
    if ((qualifier != null) && (qualifier.trim().length() > 0)) versionStr += "." + qualifier;
  }
}
