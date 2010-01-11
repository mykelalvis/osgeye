package org.osgeye.client;

import org.osgeye.events.FrameworkEvent;

public interface FrameworkListener extends OSGiEventListener
{
  void frameworkStateChanged(FrameworkEvent event, ServerIdentity serverIdentity);
}
