package org.osgeye.messages;

import java.util.List;


public class ResolveBundlesRequest extends AbstractMessage
{
  static private final long serialVersionUID = 6279974732844158900L;

  private List<Long> bundleIds;
  
  public ResolveBundlesRequest(List<Long> bundleIds)
  {
    this.bundleIds = bundleIds;
  }

  public List<Long> getBundleIds()
  {
    return bundleIds;
  }
}
