package org.osgeye.client;

import org.osgeye.events.ServiceEvent;

public interface ServiceListener extends OSGiEventListener
{
  void serviceChanged(ServiceEvent event, ServerIdentity serverId);
}
