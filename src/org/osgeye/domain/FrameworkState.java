package org.osgeye.domain;

import java.io.Serializable;

public class FrameworkState implements Serializable
{
  private int startLevel;
  private int initialBundleStartLevel;

  public FrameworkState(int startLevel, int initialBundleStartLevel)
  {
    this.startLevel = startLevel;
    this.initialBundleStartLevel = initialBundleStartLevel;
  }

  public int getStartLevel()
  {
    return startLevel;
  }

  public int getInitialBundleStartLevel()
  {
    return initialBundleStartLevel;
  }
  
}
