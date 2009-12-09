package org.osgeye.messages;

import java.util.List;

public class GetConnectionClientsResponse extends AbstractMessage
{
  static private final long serialVersionUID = 4858335433620948551L;

  private List<String> connectedClients;
  
  public GetConnectionClientsResponse(String messageId, List<String> connectedClients)
  {
    super(messageId);
  }

  public List<String> getConnectedClients()
  {
    return connectedClients;
  }

}
