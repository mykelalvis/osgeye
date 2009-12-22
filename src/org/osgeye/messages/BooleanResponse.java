package org.osgeye.messages;

public class BooleanResponse extends AbstractMessage
{
  static private final long serialVersionUID = -8811755040793811530L;

  private boolean result;
  
  public BooleanResponse(String messageId, boolean value)
  {
    super(messageId);
  }

  public boolean getResult()
  {
    return result;
  }

}
