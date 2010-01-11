package org.osgeye.client;

import org.osgeye.events.BundleEvent;

public interface BundleListener extends OSGiEventListener
{
  void bundleChanged(BundleEvent event, ServerIdentity serverId);
}
