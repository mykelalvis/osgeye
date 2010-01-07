package org.osgeye.server;

import org.osgeye.events.AbstractEvent;

public interface EventDispatcher
{
  public void dispatchEventAsynchronously(AbstractEvent event);
  
  public void dispatchEvent(AbstractEvent event);
}
