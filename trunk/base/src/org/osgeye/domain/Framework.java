package org.osgeye.domain;

import java.io.Serializable;

public class Framework implements Serializable
{
  private static final long serialVersionUID = -4614383599960017612L;

  private Bundle systemBundle;

  private int startLevel;
  
  private int initialBundleStartLevel;
  
  public Framework(Bundle systemBundle, int startLevel, int initialBundleStartLevel)
  {
    this.systemBundle = systemBundle;
    this.startLevel = startLevel;
    this.initialBundleStartLevel = initialBundleStartLevel;
  }

  public Bundle getSystemBundle()
  {
    return systemBundle;
  }

  public int getStartLevel()
  {
    return startLevel;
  }

  public int getInitialBundleStartLevel()
  {
    return initialBundleStartLevel;
  }
  
  public String toString()
  {
    return "System Bundle: " + systemBundle + "\nStart LeveL: " + startLevel 
        + "\nInital Bundle Level: " + initialBundleStartLevel;
  }
}
