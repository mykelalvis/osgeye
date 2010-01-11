package org.osgeye.console.completors;

import static org.osgeye.utils.UtilityMethods.*;

import java.util.List;

import jline.SimpleCompletor;

import org.osgeye.client.ServerState;
import org.osgeye.client.ServerStateListener;
import org.osgeye.domain.BundleState;

public class BundleNamesCompletor extends SimpleCompletor implements ServerStateListener
{
  private ServerState bundleStore;
  private List<BundleState> bundleStates;
  public BundleNamesCompletor(ServerState bundleStore)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    bundleStore.addListener(this);
    serverStateUpdated();
  }

  public BundleNamesCompletor(ServerState bundleStore, List<BundleState> bundleStates)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    this.bundleStates = bundleStates;
    bundleStore.addListener(this);
    serverStateUpdated();
  }

  public void serverStateUpdated()
  {
    List<String> bundleNames = (bundleStates == null) ? bundleStore.getBundleNames() 
        : bundleStore.getBundleNames(bundleStates);
    bundleNames.add("*");
    setCandidateStrings(toArray(bundleNames, String.class));
  }
}
