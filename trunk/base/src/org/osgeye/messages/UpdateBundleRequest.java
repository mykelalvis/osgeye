package org.osgeye.messages;


public class UpdateBundleRequest extends AbstractMessage
{
  static private final long serialVersionUID = -2064987905498306677L;

  private long bundleId;
  private byte[] bundleBytes;
  
  public UpdateBundleRequest(long bundleId)
  {
    this.bundleId = bundleId;
  }
  
  public UpdateBundleRequest(long bundleId, byte[] bundleBytes)
  {
    this.bundleId = bundleId;
    this.bundleBytes = bundleBytes;
  }

  public long getBundleId()
  {
    return bundleId;
  }

  public byte[] getBundleBytes()
  {
    return bundleBytes;
  }
}
