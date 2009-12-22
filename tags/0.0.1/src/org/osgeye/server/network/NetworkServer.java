package org.osgeye.server.network;

import static org.osgeye.utils.UtilityMethods.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;

import org.osgeye.events.AbstractEvent;
import org.osgeye.messages.LoginRequest;
import org.osgeye.messages.LoginResponse;
import org.osgeye.server.EventDispatcher;
import org.osgeye.server.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkServer implements Runnable, EventDispatcher
{
  /*
   * The maximum number of accept failures. Set this so this thread doesn't get
   * in a messed up state and just infinitely loop in the run method.
   */
  static private int MAX_ACCEPT_FAILURES = 20;
  
  private MessageProcessor messageProcessor;
  private Logger logger;
  private String host;
  private int port;
  private String user;
  private String password;
  private boolean performAuthentication;
  
  private boolean running;
  private List<ClientConnection> clientConnections;
  private ServerSocket serverSocket;
  private Thread serverThread;
  
  private int numberClientConnects;

  public NetworkServer(MessageProcessor messageProcessor, String host, int port, String user, String password)
  {
    this.messageProcessor = messageProcessor;
    this.host = host;
    this.port = port;
    this.user = user;
    this.password = password;
    
    performAuthentication = ((user != null) && (password != null));
    logger = LoggerFactory.getLogger(getClass());
    clientConnections = new ArrayList<ClientConnection>();
  }
  
  public synchronized void dispatchEvent(AbstractEvent event)
  {
    for (ClientConnection connection : clientConnections)
    {
      connection.sendEvent(event);
    }
  }
  
  public synchronized void disconnect(ClientConnection connection)
  {
    logger.info("Connection disconnet from " + connection.getConnectedHost());
    clientConnections.remove(connection);
    
    connection.disconnect();
  }
  
  public boolean isRunning()
  {
    return ((serverThread != null) && serverThread.isAlive());
  }
  
  public void start() throws IOException
  {
    if (!isRunning())
    {
      
      if (nullEmpty(host))
      {
        serverSocket = new ServerSocket(port, -1);
      }
      else
      {
        serverSocket = new ServerSocket(port, -1, InetAddress.getByName(host));
      }
      
      serverThread = new Thread(this, "OSGEye Server Socket");
      serverThread.setDaemon(true);
      running = true;
      serverThread.start();
      
      logger.info("OSGEye network server started.");
    }
  }
  
  public void stop() throws InterruptedException
  {
    running = false;

    try
    {
      serverSocket.close();
    }
    catch (Exception exc)
    {}
    
    synchronized (clientConnections)
    {
      for (ClientConnection connection : clientConnections)
      {
        try
        {
          disconnect(connection);
        }
        catch (Exception exc)
        {}
      }
    }
    
    try
    {
      serverThread.interrupt();
    }
    catch (Exception exc)
    {}
    
    serverThread = null;
  }

  public void run()
  {
    int acceptFailures = 0;
    
    while(running)
    {
      Socket clientSocket = null;
      try
      {
        logger.info("Waiting for client connection on -> ..." + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
        clientSocket = serverSocket.accept();
        logger.info("Connection received from " + clientSocket.getInetAddress().getHostAddress());
        ++numberClientConnects;
        
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        
        Object request = ois.readObject();
        
        if (request instanceof LoginRequest)
        {
          LoginRequest loginCmd = (LoginRequest)request;
          
          if (performAuthentication && (!user.equals(loginCmd.getUserName()) || !password.equals(loginCmd.getPassword())))
          {
            logger.warn("Received invalid login request with user name: " + loginCmd.getUserName());
            oos.writeObject(new LoginResponse(loginCmd.getMessageId(), false, null, "Invalid credentials."));
          }
          else
          {
            oos.writeObject(new LoginResponse(loginCmd.getMessageId(), true, "1", null));
            ClientConnection connection = new ClientConnection(this, messageProcessor, clientSocket, ois, oos);
            clientConnections.add(connection);
            connection.start();
          }
        }
        else
        {
          logger.error("Received invalid login object of class: " + request.getClass() + " and value: " + request);
          oos.writeObject(new AuthenticationException("Invalid login credentials."));

          try
          {
            clientSocket.close();
          }
          catch(Exception exc)
          {}
        }
      }
      catch (IOException ioexc)
      {
        try
        {
          clientSocket.close();
        }
        catch(Exception exc)
        {}
        
        if (running)
        {
          logger.warn("Received io exception on accept: " + ioexc.getMessage(), ioexc);
          if (++acceptFailures > MAX_ACCEPT_FAILURES)
          {
            logger.error("Maximum accept failures received. Server is shutting down.");
            running = false;
          }
        }
      }
      catch (ClassNotFoundException cnfexc)
      {
        try
        {
          clientSocket.close();
        }
        catch(Exception exc)
        {}

        logger.error("Received class not found exception from login request: " + cnfexc.getMessage(), cnfexc);
      }
    }
  }
}
