package org.osgeye.messages;

import java.io.Serializable;


abstract public class AbstractMessage implements Serializable
{
  static private int MESSAGE_ID_COUNTER = 0;
  
  private String messageId;
  
  protected AbstractMessage()
  {
    synchronized(AbstractMessage.class)
    {
      messageId = Integer.toString(++MESSAGE_ID_COUNTER);
    }
  }
  
  protected AbstractMessage(String messageId)
  {
    this.messageId = messageId;
  }

  public String getMessageId()
  {
    return messageId;
  }
}
