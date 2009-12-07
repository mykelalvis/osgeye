package org.osgeye.messages;

import org.osgeye.domain.FrameworkState;

public class GetFrameworkStateResponse extends AbstractMessage
{
  private FrameworkState frameworkState;
  
  public GetFrameworkStateResponse(String messageId, FrameworkState frameworkState)
  {
    super(messageId);
    
    this.frameworkState = frameworkState;
  }

  public FrameworkState getFrameworkState()
  {
    return frameworkState;
  }
}
