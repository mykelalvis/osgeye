package org.osgeye.messages;

public class InstallBundleRequest extends AbstractMessage
{
  static private final long serialVersionUID = -5565120531157154221L;
  
  private String fileName;
  private byte[] bundleBytes;
  
  public InstallBundleRequest(String fileName, byte[] bundleBytes)
  {
    this.fileName = fileName;
    this.bundleBytes = bundleBytes;
  }

  public String getFileName()
  {
    return fileName;
  }

  public byte[] getBundleBytes()
  {
    return bundleBytes;
  }
}
