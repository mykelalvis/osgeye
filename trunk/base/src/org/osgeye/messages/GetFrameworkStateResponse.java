package org.osgeye.messages;

import org.osgeye.domain.Framework;

public class GetFrameworkStateResponse extends AbstractMessage
{
  private Framework frameworkState;
  
  public GetFrameworkStateResponse(String messageId, Framework frameworkState)
  {
    super(messageId);
    
    this.frameworkState = frameworkState;
  }

  public Framework getFrameworkState()
  {
    return frameworkState;
  }
}
