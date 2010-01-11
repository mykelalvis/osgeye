package org.osgeye.console.completors;

import static org.osgeye.utils.UtilityMethods.*;
import jline.SimpleCompletor;

import org.osgeye.client.ServerState;
import org.osgeye.client.ServerStateListener;

public class ServiceNamesCompletor extends SimpleCompletor implements ServerStateListener
{
  private ServerState bundleStore;
  
  public ServiceNamesCompletor(ServerState bundleStore)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    bundleStore.addListener(this);
    serverStateUpdated();
  }

  public void serverStateUpdated()
  {
    setCandidateStrings(toArray(bundleStore.getServiceClassNames(), String.class));
  }
}
