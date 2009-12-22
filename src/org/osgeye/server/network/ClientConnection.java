/*
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * $HeaderURL$
 */
package org.osgeye.server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgeye.events.AbstractEvent;
import org.osgeye.messages.AbstractMessage;
import org.osgeye.messages.ExceptionResponse;
import org.osgeye.server.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Corey Baswell
 */
public class ClientConnection extends Thread
{
  static private final int MAX_DISPATCH_THREADS = 5;
  
  private ExecutorService dispatchThreadPool;

  private NetworkServer server;
  private MessageProcessor messageProcessor;
  private Logger logger;
  private Socket clientSocket;
  private String connectedHost;
  
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  
  private boolean running;

  public ClientConnection(NetworkServer server, MessageProcessor messageProcessor, 
      Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream)
  {
    super("OSGi Manager Client Connection[" + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + "]");
    
    running = true;
    setDaemon(true);
    
    this.server = server;
    this.messageProcessor = messageProcessor;
    this.clientSocket = clientSocket;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    
    logger = LoggerFactory.getLogger(getClass());
    
    this.connectedHost = clientSocket.getInetAddress().getHostAddress();
    this.dispatchThreadPool = Executors.newFixedThreadPool(MAX_DISPATCH_THREADS, new ClientDispatchThreadFactory(this.connectedHost));
  }

  public String getConnectedHost()
  {
    return this.connectedHost;
  }
  
  /**
   * Synchronously sends the event to the connected client.
   * 
   * @param event
   * @throws IOException If a socket error occurs.
   */
  public void sendEvent(AbstractEvent event)
  {
    pushToStream(event);
  }

  @Override
  public void run()
  {
    try
    {
      while (running)
      {
        Object object = this.inputStream.readObject();
        
        if (object instanceof AbstractMessage)
        {
          final AbstractMessage request = (AbstractMessage)object;
          
          this.dispatchThreadPool.execute(new Runnable()
          {
            public void run()
            {
              AbstractMessage response;
              
              try
              {
                response = messageProcessor.processRequest(request, ClientConnection.this);
              }
              catch (Exception exc)
              {
                response = new ExceptionResponse(request.getMessageId(), exc);
              }
              
              if (response != null)
              {
                pushToStream(response);
              }
            }
          });
        }
        else
        {
          logger.error("Received request object from client that does not implement Messageable interface: " + object + " of class " + object.getClass());
        }
      }
    }
    catch (IOException ioexc)
    {}
    catch (ClassNotFoundException cnfexc)
    {
      logger.error("Received unknown request class: " + cnfexc.getMessage(), cnfexc);
    }
    catch (RuntimeException rexc)
    {
      logger.error("Received unknown error from connection: " + rexc.getMessage(), rexc);
    }
    finally
    {
      server.disconnect(this);
    }
  }
  
  @Override
  public int hashCode()
  {
    return this.connectedHost.hashCode();
  }
  
  public void disconnect()
  {
    try
    {
      this.clientSocket.close();
    }
    catch (Exception exc)
    {}
    
    this.dispatchThreadPool.shutdownNow();
  }
  
  /**
   * Synchronizes access to output stream.
   * 
   */
  private synchronized void pushToStream(Serializable object)
  {
    try
    {
      this.outputStream.writeObject(object);
    }
    catch (IOException ioexc)
    {
      logger.warn("Received unexpected io exception while writing to output stream: " + ioexc.getMessage(), ioexc);
      running = false;
    }
  }
}
