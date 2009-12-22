package org.osgeye.client;

import org.osgeye.client.network.NetworkServerIdentity;
import org.osgeye.events.BundleEvent;

public interface BundleListener extends OSGiEventListener
{
  void bundleChanged(BundleEvent event, NetworkServerIdentity serverId);
}
