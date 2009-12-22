package org.osgeye.client.events;

import org.osgeye.client.network.NetworkServerIdentity;

public class ServerEvent
{
  static public enum ServerEventType
  {
    CONNECTED,
    DISCONNECTED
  }
  
  private NetworkServerIdentity serverIdentity;
  private ServerEventType eventType;
  public ServerEvent(NetworkServerIdentity serverIdentity, ServerEventType eventType)
  {
    super();
    this.serverIdentity = serverIdentity;
    this.eventType = eventType;
  }
  
  public NetworkServerIdentity getServerIdentity()
  {
    return serverIdentity;
  }
  
  public ServerEventType getEventType()
  {
    return eventType;
  }
}
