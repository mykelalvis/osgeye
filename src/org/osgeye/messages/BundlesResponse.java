package org.osgeye.messages;

import java.util.List;

import org.osgeye.domain.Bundle;

public class BundlesResponse extends AbstractMessage
{
  static private final long serialVersionUID = -4649052794253306150L;

  private List<Bundle> bundles;
  
  public BundlesResponse(String messageId, List<Bundle> bundles)
  {
    super(messageId);
    this.bundles = bundles;
  }

  public List<Bundle> getBundles()
  {
    return bundles;
  }
}
