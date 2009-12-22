package org.osgeye.server;

import org.osgeye.events.AbstractEvent;

public interface EventDispatcher
{
  public void dispatchEvent(AbstractEvent event);
}
