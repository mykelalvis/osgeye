package org.osgeye.client;

import org.osgeye.client.network.NetworkServerIdentity;
import org.osgeye.events.FrameworkEvent;

public interface FrameworkListener extends OSGiEventListener
{
  void frameworkStateChanged(FrameworkEvent event, NetworkServerIdentity serverIdentity);
}
