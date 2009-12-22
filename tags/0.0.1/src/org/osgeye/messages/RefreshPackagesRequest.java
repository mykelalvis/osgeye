package org.osgeye.messages;

import java.util.List;


public class RefreshPackagesRequest extends AbstractMessage
{
  private static final long serialVersionUID = -9199418902520381461L;
  
  private List<Long> bundleIds;
  
  public RefreshPackagesRequest(List<Long> bundleIds)
  {
    this.bundleIds = bundleIds;
  }

  public List<Long> getBundleIds()
  {
    return bundleIds;
  }

}
