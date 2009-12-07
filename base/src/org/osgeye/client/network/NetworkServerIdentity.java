/*
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * $HeaderURL$
 */
package org.osgeye.client.network;

import java.util.Map;

/**
 * Identity  of a remote esb instance. This id will be accessible on all
 * remote esb events to determine where the event should be applied. This
 * class implements equals and hashCode to safely be used in {@link Map}.
 * 
 * @author Corey Baswell
 * @see AbstractRemoteEsbEvent
 */
public class NetworkServerIdentity
{
  private String host;
  private int port;
  
  public NetworkServerIdentity(String host, int port)
  {
    this.host = host;
    this.port = port;
  }

  public String getHost()
  {
    return host;
  }

  public int getPort()
  {
    return port;
  }
  
  @Override
  public int hashCode()
  {
    return (host + ":" + port).hashCode();
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof NetworkServerIdentity)
    {
      NetworkServerIdentity remoteEsbId = (NetworkServerIdentity)obj;
      
      return ((this.host.equals(remoteEsbId.host)) && (this.port == remoteEsbId.port));
    }
    else
    {
      return false;
    }
  }
  
  @Override
  public String toString()
  {
    return this.host + ":" + this.port;
  }
}
