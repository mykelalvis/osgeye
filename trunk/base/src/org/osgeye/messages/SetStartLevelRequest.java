package org.osgeye.messages;

public class SetStartLevelRequest extends AbstractMessage
{
  static private final long serialVersionUID = -4303290058865657790L;

  private int startlevel;
  
  public SetStartLevelRequest(int startLevel)
  {
    this.startlevel = startLevel;
  }

  public int getStartlevel()
  {
    return startlevel;
  }
}
