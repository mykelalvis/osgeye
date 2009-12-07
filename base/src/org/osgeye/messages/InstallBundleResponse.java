package org.osgeye.messages;

import org.osgeye.domain.Bundle;

public class InstallBundleResponse extends AbstractMessage
{
  private Bundle installedBundle;
  
  public InstallBundleResponse(String messageId, Bundle installedBundle)
  {
    super(messageId);
    this.installedBundle = installedBundle;
  }

  public Bundle getInstalledBundle()
  {
    return installedBundle;
  }
  
}
