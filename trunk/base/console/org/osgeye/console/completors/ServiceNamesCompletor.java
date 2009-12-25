package org.osgeye.console.completors;

import static org.osgeye.utils.UtilityMethods.*;
import jline.SimpleCompletor;

import org.osgeye.client.BundleStore;
import org.osgeye.client.BundleStoreListener;

public class ServiceNamesCompletor extends SimpleCompletor implements BundleStoreListener
{
  private BundleStore bundleStore;
  
  public ServiceNamesCompletor(BundleStore bundleStore)
  {
    super(new String[0]);
    
    this.bundleStore = bundleStore;
    bundleStore.addListener(this);
    bundleStoreUpdated();
  }

  public void bundleStoreUpdated()
  {
    setCandidateStrings(toArray(bundleStore.getServiceInterfaces(), String.class));
  }
}
