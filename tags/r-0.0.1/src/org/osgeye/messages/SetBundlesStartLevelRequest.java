package org.osgeye.messages;

import java.util.List;

public class SetBundlesStartLevelRequest extends AbstractMessage
{
  static private final long serialVersionUID = -754326735820815566L;

  private int startLevel;
  
  private List<Long> bundleIds;
  
  public SetBundlesStartLevelRequest(int startLevel, List<Long> bundleIds)
  {
    this.startLevel = startLevel;
    this.bundleIds = bundleIds;
  }

  public int getStartLevel()
  {
    return startLevel;
  }

  public List<Long> getBundleIds()
  {
    return bundleIds;
  }

}
