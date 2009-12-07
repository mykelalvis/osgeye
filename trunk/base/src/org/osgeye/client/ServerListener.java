package org.osgeye.client;

import org.osgeye.client.events.ServerEvent;

public interface ServerListener
{
  void serverUpdate(ServerEvent event);
}
