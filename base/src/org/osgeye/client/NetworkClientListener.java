package org.osgeye.client;



public interface NetworkClientListener
{
  void unexpectedClientError(NetworkClient client, String message, Exception exc);
}
