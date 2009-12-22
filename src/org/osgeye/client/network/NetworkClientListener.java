package org.osgeye.client.network;

public interface NetworkClientListener
{
  void unexpectedClientError(NetworkClient client, String message, Exception exc);
}
