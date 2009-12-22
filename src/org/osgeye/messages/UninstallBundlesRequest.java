package org.osgeye.messages;

import java.util.List;


public class UninstallBundlesRequest extends AbstractMessage
{
  static private final long serialVersionUID = -8244182847899996552L;

  private List<Long> bundleIds;
  
  public UninstallBundlesRequest(List<Long> bundleIds)
  {
    this.bundleIds = bundleIds;
  }

  public List<Long> getBundleIds()
  {
    return bundleIds;
  }

}
