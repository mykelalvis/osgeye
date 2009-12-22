package org.osgeye.messages;

import java.util.List;

import org.osgeye.domain.StartBundleOptions;


public class StartBundlesRequest extends AbstractMessage
{
  static private final long serialVersionUID = -738786024977984521L;
  
  private List<Long> bundleIds;
  
  private StartBundleOptions options;

  public StartBundlesRequest(List<Long> bundleIds)
  {
    this(bundleIds, null);
  }

  public StartBundlesRequest(List<Long> bundleIds, StartBundleOptions options)
  {
    this.bundleIds = bundleIds;
    this.options = options;
  }

  public StartBundleOptions getOptions()
  {
    return options;
  }

  public List<Long> getBundleIds()
  {
    return bundleIds;
  }

}
