package org.osgeye.server;

import org.osgeye.messages.AbstractMessage;
import org.osgeye.server.network.ClientConnection;

public interface MessageProcessor
{
  AbstractMessage processRequest(AbstractMessage message, ClientConnection clientConnection);
}
