package org.osgeye.messages;

public class SetInitBundleStartLevelRequest extends AbstractMessage
{
  static private final long serialVersionUID = -180202140744142859L;

  private int startLevel;
  
  public SetInitBundleStartLevelRequest(int startLevel)
  {
    this.startLevel = startLevel;
  }

  public int getStartLevel()
  {
    return startLevel;
  }
}
