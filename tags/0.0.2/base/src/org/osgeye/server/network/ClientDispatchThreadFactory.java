package org.osgeye.server.network;

import java.util.concurrent.ThreadFactory;

/**
 * Thread factory for creating client connection dispatch threads.
 * 
 * @author Corey Baswell
 *
 */
public class ClientDispatchThreadFactory implements ThreadFactory
{
  private String connectedClient;
  private int threadCount;
  
  public ClientDispatchThreadFactory(String connectedClient)
  {
    this.connectedClient = connectedClient;
  }

  public Thread newThread(Runnable runnable)
  {
    Thread dispatchThread = new Thread(runnable, ("Agent Connection Dispatch - " + connectedClient + ":" + ++threadCount));
    dispatchThread.setDaemon(true);
    dispatchThread.setPriority(Thread.MIN_PRIORITY);

    
    return dispatchThread;
  }
}
