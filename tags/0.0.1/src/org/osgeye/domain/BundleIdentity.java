package org.osgeye.domain;

import java.io.Serializable;

public class BundleIdentity implements Serializable
{
  static private final long serialVersionUID = -904125329182549651L;

  private long id;
  
  private String symbolicName;
  
  private Version version;

  public BundleIdentity(long id, String symbolicName, Version version)
  {
    this.id = id;
    this.symbolicName = symbolicName;
    this.version = version;
  }

  public long getId()
  {
    return id;
  }

  public String getSymbolicName()
  {
    return symbolicName;
  }

  public Version getVersion()
  {
    return version;
  }
}
