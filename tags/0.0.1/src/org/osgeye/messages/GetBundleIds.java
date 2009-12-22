package org.osgeye.messages;

import org.osgeye.domain.VersionRange;

public class GetBundleIds extends AbstractMessage
{
  static private final long serialVersionUID = -4101350480248623742L;

  private String symbolicNamePattern;
  
  private VersionRange withinRange;
  
  public GetBundleIds(String symbolicNamePattern)
  {
    this.symbolicNamePattern = symbolicNamePattern;
  }
  
  public GetBundleIds(String symbolicNamePattern, VersionRange withinRange)
  {
    this.symbolicNamePattern = symbolicNamePattern;
    this.withinRange = withinRange;
  }

  public String getSymbolicNamePattern()
  {
    return symbolicNamePattern;
  }

  public VersionRange getWithinRange()
  {
    return withinRange;
  }
  
}
