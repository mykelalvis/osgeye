package org.osgeye.client;

import org.osgeye.client.network.NetworkServerIdentity;
import org.osgeye.events.ServiceEvent;

public interface ServiceListener extends OSGiEventListener
{
  void serviceChanged(ServiceEvent event, NetworkServerIdentity serverId);
}
