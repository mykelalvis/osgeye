package org.osgeye.console.completors;

import static org.osgeye.utils.UtilityMethods.*;

import java.util.List;

import jline.SimpleCompletor;

import org.osgeye.console.BundleStore;
import org.osgeye.console.BundleStoreListener;
import org.osgeye.domain.BundleState;

public class BundleNamesCompletor extends SimpleCompletor implements BundleStoreListener
{
  private BundleStore bundleStore;
  private List<BundleState> bundleStates;
  public BundleNamesCompletor(BundleStore bundleStore)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    bundleStore.addListener(this);
    bundleStoreUpdated();
  }

  public BundleNamesCompletor(BundleStore bundleStore, List<BundleState> bundleStates)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    this.bundleStates = bundleStates;
    bundleStore.addListener(this);
    bundleStoreUpdated();
  }

  public void bundleStoreUpdated()
  {
    List<String> bundleNames = (bundleStates == null) ? bundleStore.getBundleNames() 
        : bundleStore.getBundleNames(bundleStates);
    bundleNames.add("*");
    setCandidateStrings(toArray(bundleNames, String.class));
  }
}
