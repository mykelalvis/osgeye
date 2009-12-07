package org.osgeye.messages;

import java.util.List;

import org.osgeye.domain.StopBundleOptions;


public class StopBundlesRequest extends AbstractMessage
{
  static private final long serialVersionUID = 8060209568487119558L;
  
  private List<Long> bundleIds;
  
  private StopBundleOptions options;

  public StopBundlesRequest(List<Long> bundleIds)
  {
    this(bundleIds, null);
  }

  public StopBundlesRequest(List<Long> bundleIds, StopBundleOptions options)
  {
    this.bundleIds = bundleIds;
    this.options = options;
  }

  public StopBundleOptions getOptions()
  {
    return options;
  }

  public List<Long> getBundleIds()
  {
    return bundleIds;
  }

}
