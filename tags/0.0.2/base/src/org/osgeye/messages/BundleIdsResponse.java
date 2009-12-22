package org.osgeye.messages;

import java.util.List;

import org.osgeye.domain.BundleIdentity;

public class BundleIdsResponse extends AbstractMessage
{
  static private final long serialVersionUID = -6902614357281017191L;
  
  private List<BundleIdentity> bundleIds;
  
  public BundleIdsResponse(String messageId, List<BundleIdentity> bundleIds)
  {
    super(messageId);
    this.bundleIds = bundleIds;
  }

  public List<BundleIdentity> getBundleIds()
  {
    return bundleIds;
  }

}
