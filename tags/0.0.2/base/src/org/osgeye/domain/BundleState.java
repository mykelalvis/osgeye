package org.osgeye.domain;

import java.io.Serializable;

import org.osgi.framework.Bundle;

public enum BundleState implements Serializable
{
  UNINSTALLED("Uninstalled", "The bundle is uninstalled and may not be used.", Bundle.UNINSTALLED),
  INSTALLED("Installed", "The bundle is installed but not yet resolved.", Bundle.INSTALLED),
  RESOLVED("Resolved", "The bundle is resolved and is able to be started.", Bundle.RESOLVED),
  STARTING("Starting", "The bundle is in the process of starting.", Bundle.STARTING),
  STOPING("Stopping", "The bundle is in the process of stopping.", Bundle.STOPPING),
  ACTIVE("Active", "The bundle is now running.", Bundle.ACTIVE);
  
  static public BundleState fromName(String name)
  {
    for (BundleState bs : values())
    {
      if (bs.name.equals(name)) return bs;
    }

    throw new IllegalArgumentException("Invalid bundle name " + name);
  }
  
  static public BundleState fromState(int state)
  {
    for (BundleState bs : values())
    {
      if (bs.state == state) return bs;
    }
    
    throw new IllegalArgumentException("Invalid bundle state " + state);
  }
  
  private String name;
  private String description;
  private int state;
  
  private BundleState(String name, String description, int state)
  {
    this.name = name;
    this.description = description;
    this.state = state;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public int getState()
  {
    return state;
  }
}
