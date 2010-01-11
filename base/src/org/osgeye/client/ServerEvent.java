package org.osgeye.client;


public class ServerEvent
{
  static public enum ServerEventType
  {
    CONNECTED,
    DISCONNECTED
  }
  
  private ServerIdentity serverIdentity;
  private ServerEventType eventType;
  public ServerEvent(ServerIdentity serverIdentity, ServerEventType eventType)
  {
    super();
    this.serverIdentity = serverIdentity;
    this.eventType = eventType;
  }
  
  public ServerIdentity getServerIdentity()
  {
    return serverIdentity;
  }
  
  public ServerEventType getEventType()
  {
    return eventType;
  }
}
