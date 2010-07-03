/*
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * $HeaderURL$
 */
package org.osgeye.client;


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.CredentialException;

import org.osgeye.client.ServerEvent.ServerEventType;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleIdentity;
import org.osgeye.domain.Configuration;
import org.osgeye.domain.Framework;
import org.osgeye.domain.ServiceClass;
import org.osgeye.domain.StartBundleOptions;
import org.osgeye.domain.StopBundleOptions;
import org.osgeye.domain.VersionRange;
import org.osgeye.events.AbstractEvent;
import org.osgeye.events.BundleEvent;
import org.osgeye.events.FrameworkEvent;
import org.osgeye.events.ServiceEvent;
import org.osgeye.messages.AbstractMessage;
import org.osgeye.messages.BooleanResponse;
import org.osgeye.messages.BundleIdsResponse;
import org.osgeye.messages.BundlesResponse;
import org.osgeye.messages.ExceptionResponse;
import org.osgeye.messages.GetAllBundlesRequest;
import org.osgeye.messages.GetBundleIds;
import org.osgeye.messages.GetConfigurationsRequest;
import org.osgeye.messages.GetConfigurationsResponse;
import org.osgeye.messages.GetConnectedClientsRequest;
import org.osgeye.messages.GetConnectionClientsResponse;
import org.osgeye.messages.GetFrameworkStateRequest;
import org.osgeye.messages.GetFrameworkStateResponse;
import org.osgeye.messages.GetServiceTypeDefinitionRequest;
import org.osgeye.messages.GetServiceTypeDefinitionResponse;
import org.osgeye.messages.InstallBundleRequest;
import org.osgeye.messages.InstallBundleResponse;
import org.osgeye.messages.InvokeServiceMethodRequest;
import org.osgeye.messages.InvokeServiceMethodResponse;
import org.osgeye.messages.LoginRequest;
import org.osgeye.messages.LoginResponse;
import org.osgeye.messages.RefreshPackagesRequest;
import org.osgeye.messages.ResolveBundlesRequest;
import org.osgeye.messages.SetBundlesStartLevelRequest;
import org.osgeye.messages.SetInitBundleStartLevelRequest;
import org.osgeye.messages.SetStartLevelRequest;
import org.osgeye.messages.StartBundlesRequest;
import org.osgeye.messages.StopBundlesRequest;
import org.osgeye.messages.UninstallBundlesRequest;
import org.osgeye.messages.UpdateBundleRequest;
import org.osgeye.messages.VoidResponse;
import org.osgeye.remotereflect.AbstractTypeInstance;
import org.osgeye.remotereflect.SimpleTypeInstance;
import org.osgeye.remotereflect.TypeDefinition;
import org.osgeye.utils.IOUtils;

/**
 * Client interface for all commands executed against a remote OSGi server manager.
 * 
 * @author Corey Baswell
 */
public class NetworkClient implements Runnable
{
  static private int DEFAUL_TIMEOUT_SECONDS = Integer.MAX_VALUE;
  
  private int timeoutSeconds;
  
  private ServerIdentity remoteId;
  private String userName;
  private String password;
  
  private Socket socket;
  private Thread serverProcessThread;
  private Map<Thread, Boolean> connectedMap;
  
  private Map<String, BlockingQueue<AbstractMessage>> responseQueueMap;
  
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  
  private List<NetworkClientListener> clientListeners;
  private List<ServerListener> serverListeners;
  private List<OSGiEventListener> osgiListeners;
  
  public NetworkClient(ServerIdentity remoteId, String userName, String password)
  {
    this.remoteId = remoteId;
    this.userName = userName;
    this.password = password;
    
    timeoutSeconds = DEFAUL_TIMEOUT_SECONDS;
    
    connectedMap = new HashMap<Thread, Boolean>();
    clientListeners = new ArrayList<NetworkClientListener>();
    serverListeners = new ArrayList<ServerListener>();
    osgiListeners = new ArrayList<OSGiEventListener>();
  }

  /**
   * The minimum amount of seconds we'll wait for a response back from the server
   * we are waiting on. If a timeout occurs a disconnect from the server will be
   * initiated.
   * 
   * If not set the default timeout is 60 seconds.
   * 
   * @param timeoutSeconds
   */
  public void setTimeoutSeconds(int timeoutSeconds)
  {
    this.timeoutSeconds = timeoutSeconds;
  }

  /**
   * Connects on the remote esb server.
   * 
   * @param userName
   * @param password
   * @throws IllegalStateException If currently connected.
   * @throws ConnectException If a network connection failure occurs.
   * @throws RemoteServerException If an unexpected error occurs on the server.
   * @throws CredentialException If the server rejects the given userName and password.
   */
  public void connect() throws IllegalStateException, ConnectException, RemoteServerException, CredentialException
  {
    assertNotConnected();
    
    responseQueueMap = new HashMap<String, BlockingQueue<AbstractMessage>>();

    try
    {
      socket = new Socket(this.remoteId.getHost(), this.remoteId.getPort());
      socket.setSoTimeout(timeoutSeconds);
      outputStream = new ObjectOutputStream(this.socket.getOutputStream());
      outputStream.writeObject(new LoginRequest(userName, password));
      inputStream = new ObjectInputStream(this.socket.getInputStream());

      Object response = this.inputStream.readObject();
      
      if (response instanceof ExceptionResponse)
      {
        disconnectCleanup();
        throw new RemoteServerException((ExceptionResponse)response);
      }
      else if (response instanceof LoginResponse)
      {
        LoginResponse loginResponse = (LoginResponse)response;
        if (!loginResponse.isSuccess())
        {
          throw new CredentialException(loginResponse.getErrorMessage());
        }
        
        serverProcessThread = new Thread(this);
        serverProcessThread.setDaemon(true);
        connectedMap.put(this.serverProcessThread, true);
        serverProcessThread.start();
        
        ServerEvent event = new ServerEvent(remoteId, ServerEventType.CONNECTED);
        synchronized (serverListeners)
        {
          for (ServerListener listener : serverListeners)
          {
            listener.serverUpdate(event);
          }
        }
      }
      else
      {
        disconnectCleanup();
        throw new SocketException("Received unexpected login response: " + response + " of class: " + response.getClass());
      }
    }
    catch (UnknownHostException uhexc)
    {
      disconnectCleanup();
      throw new ConnectException(uhexc.getMessage());
    }
    catch (IOException ioexc)
    {
      disconnectCleanup();
      throw new ConnectException(ioexc.getMessage());
    }
    catch (ClassNotFoundException cnfexc)
    {
      disconnectCleanup();
      throw new ConnectException(cnfexc.getMessage());
    }
  }

  /**
   * Disconnects from the remote esb.
   * 
   * @throws IllegalStateException If currently connected.
   */
  public void disconnect() throws IllegalStateException
  {
    assertConnected();
    
    if ((this.serverProcessThread != null) && this.connectedMap.get(this.serverProcessThread))
    {
      this.connectedMap.put(this.serverProcessThread, false);
      
      disconnectCleanup();
      
      ServerEvent event = new ServerEvent(remoteId, ServerEventType.DISCONNECTED);
      synchronized (serverListeners)
      {
        for (ServerListener listener : serverListeners)
        {
          listener.serverUpdate(event);
        }
      }
    }
  }

  public boolean isConnected()
  {
    return ((socket != null) && (serverProcessThread != null));
  }

  /**
   * Registers listener for remote OSGi events.
   * @throws RemoteEsbException 
   */
  public void addOsgiListener(OSGiEventListener listener)
  {
    synchronized(osgiListeners) {osgiListeners.add(listener);}
  }
  
  /**
   * Unregisters listener for remote OSGi events.
   * @throws RemoteEsbException 
   */
  public void removeOsgiListener(OSGiEventListener listener)
  {
    synchronized(osgiListeners) {osgiListeners.remove(listener);}
  }

  /**
   * Registers listener for remote OSGi events.
   * @throws RemoteEsbException 
   */
  public void addServerListener(ServerListener listener)
  {
    synchronized(serverListeners) {serverListeners.add(listener);}
  }
  
  /**
   * Unregisters listener for remote OSGi events.
   * @throws RemoteEsbException 
   */
  public void removeServerListener(ServerListener listener)
  {
    synchronized(serverListeners) {serverListeners.remove(listener);}
  }

  /**
   * Registers listener for remote OSGi events.
   * @throws RemoteEsbException 
   */
  public void addClientListener(NetworkClientListener listener)
  {
    synchronized(clientListeners) {clientListeners.add(listener);}
  }
  
  /**
   * Unregisters listener for remote OSGi events.
   * @throws RemoteEsbException 
   */
  public void removeClientListener(NetworkClientListener listener)
  {
    synchronized(clientListeners) {clientListeners.remove(listener);}
  }

  
  /**
   * Returns the identity of the client for use in later comparisons, e.g. a pool of clients
   * 
   * @return 
   */
  public ServerIdentity getIdentity()
  {
    return remoteId;
  }
  
  public Framework getFramework() throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetFrameworkStateRequest(), GetFrameworkStateResponse.class).getFrameworkState();
  }
  
  /**
   * 
   * @return All bundles on the OSGi server.
   * @throws ConnectException
   * @throws IllegalStateException
   * @throws RemoteServerException
   */
  public List<Bundle> getAllBundles() throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetAllBundlesRequest(), BundlesResponse.class).getBundles();
  }

  public List<BundleIdentity> getAllBundleIds() throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetBundleIds(".*"), BundleIdsResponse.class).getBundleIds();
  }

  public List<BundleIdentity> getBundleIds(String symbolicNamePattern, VersionRange withinRange) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetBundleIds(symbolicNamePattern, withinRange), BundleIdsResponse.class).getBundleIds();
  }

  public void startBundle(Long bundleId) throws ConnectException, IllegalStateException, RemoteServerException
  {
    startBundle(bundleId, null);
  }

  public void startBundle(Long bundleId, StartBundleOptions startOptions) throws ConnectException, IllegalStateException, RemoteServerException
  {
    startBundles(Arrays.asList(bundleId), startOptions);
  }

  public void startBundles(List<Long> bundleIds, StartBundleOptions startOptions) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new StartBundlesRequest(bundleIds, startOptions), VoidResponse.class);
  }

  public void stopBundle(Long bundleId) throws ConnectException, IllegalStateException, RemoteServerException
  {
    stopBundles(Arrays.asList(bundleId), null);
  }

  public void stopBundle(Long bundleId, StopBundleOptions stopOptions) throws ConnectException, IllegalStateException, RemoteServerException
  {
    stopBundles(Arrays.asList(bundleId), stopOptions);
  }
  
  public void stopBundles(List<Long> bundleIds, StopBundleOptions stopOptions) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new StopBundlesRequest(bundleIds, stopOptions), VoidResponse.class);
  }

  public void uninstallBundle(Long bundleId) throws ConnectException, IllegalStateException, RemoteServerException
  {
    uninstallBundles(Arrays.asList(bundleId));
  }

  public void uninstallBundles(List<Long> bundleIds) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new UninstallBundlesRequest(bundleIds), VoidResponse.class);
  }
  
  public void updateBundle(Long bundleId) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new UpdateBundleRequest(bundleId), VoidResponse.class);
  }
  
  public void setStartLevel(int startLevel) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new SetStartLevelRequest(startLevel), VoidResponse.class);
  }
  
  public void setInitialBundleStartLevel(int startLevel) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new SetInitBundleStartLevelRequest(startLevel), VoidResponse.class);
  }

  public void setBundleStartLevel(int startLevel, Long bundleId) throws ConnectException, IllegalStateException, RemoteServerException
  {
    setBundlesStartLevel(startLevel, Arrays.asList(bundleId));
  }

  public void setBundlesStartLevel(int startLevel, List<Long> bundleIds) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new SetBundlesStartLevelRequest(startLevel, bundleIds), VoidResponse.class);
  }

  /**
   * Forces the update (replacement) or removal of packages exported by the specified bundle. 
   * 
   * @param bundleId The bundle to refresh packages for.
   * @throws ConnectException
   * @throws IllegalStateException
   * @throws RemoteServerException
   */
  public void refreshPackages(Long bundleId) throws ConnectException, IllegalStateException, RemoteServerException
  {
    refreshPackages(Arrays.asList(bundleId));
  }

  
  /**
   * Forces the update (replacement) or removal of packages exported by the specified bundles. 
   * 
   * @param bundleIds
   * @throws ConnectException
   * @throws IllegalStateException
   * @throws RemoteServerException
   */
  public void refreshPackages(List<Long> bundleIds) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    sendRequest(new RefreshPackagesRequest(bundleIds), VoidResponse.class);
  }
  
  /**
   * Resolve the specified bundles. The Framework must attempt to resolve the specified 
   * bundles that are unresolved. Additional bundles that are not included in the specified 
   * bundles may be resolved as a result of calling this method. 
   * 
   * @param bundleIds
   * @return true if all specified bundles are resolved.
   * @throws ConnectException
   * @throws IllegalStateException
   * @throws RemoteServerException
   */
  public boolean resolveBundles(List<Long> bundleIds) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new ResolveBundlesRequest(bundleIds), BooleanResponse.class).getResult();
  }
  
  public Bundle installBundle(File bundleFile) throws IOException
  {
    InstallBundleRequest request = new InstallBundleRequest(bundleFile.getName(), IOUtils.getContents(bundleFile));
    return sendRequest(request, InstallBundleResponse.class).getInstalledBundle();
  }
  
  public List<Configuration> getConfigurations(String filter) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetConfigurationsRequest(filter), GetConfigurationsResponse.class).getConfigurations();
  }
  
  public TypeDefinition getServiceTypeDefinition(ServiceClass serviceClass) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetServiceTypeDefinitionRequest(serviceClass), GetServiceTypeDefinitionResponse.class).getTypeDefinition();
  }
  
  public AbstractTypeInstance invokeServiceMethod(ServiceClass serviceClass, String methodName, List<SimpleTypeInstance> parameters) throws ConnectException, IllegalStateException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new InvokeServiceMethodRequest(serviceClass, methodName, parameters), InvokeServiceMethodResponse.class).getReturnType();
  }
  
  /**
   * Returns a list of host names (or ip addresses) of all clients that are currently
   * connected to this remote esb.
   * 
   * @return
   * @throws IllegalStateException If not currently connected.
   * @throws ConnectException If a network error occurs.
   * @throws RemoteServerException If an error occurs on the remote server while processing this request.
   */
  public List<String> getConnectedClients() throws IllegalStateException, ConnectException, RemoteServerException
  {
    assertConnected();
    return sendRequest(new GetConnectedClientsRequest(), GetConnectionClientsResponse.class).getConnectedClients();
  }


  /**
   * Processes all server responses (synchronous and asynchronous).
   * 
   * @see Runnable
   */
  public void run()
  {
    Thread currentThread = Thread.currentThread();

    while(this.connectedMap.get(currentThread))
    {
      try
      {
        Object obj = this.inputStream.readObject();
        if (obj instanceof AbstractEvent)
        {
          dispatchEvent((AbstractEvent)obj);
        }
        else if (obj instanceof AbstractMessage)
        {
          AbstractMessage response = (AbstractMessage)obj;
          
          if (this.responseQueueMap.containsKey(response.getMessageId()))
          {
            try
            {
              this.responseQueueMap.get(response.getMessageId()).put(response);
            }
            catch (InterruptedException iexc)
            {
              notifyClientError("Interrupt exception attempting to put response to queue: " + iexc.getMessage(), iexc);
            }
          }
          else
          {
            notifyClientError("Received response with no matching queue of message id: " + response.getMessageId());
          }
        }
        else
        {
          notifyClientError("Received unknown object type from server " + obj + " with class " + obj.getClass());
        }
      }
      catch (IOException ioexc)
      {
        if (this.connectedMap.get(currentThread))
        {
          /*
           * Only treat this as error if this wasn't brought upon by manual disconnect (socket.close()).
           */
          notifyClientError("Received io exception from server: " + ioexc.getMessage(), ioexc);
          disconnect();
        }
      }
      catch (ClassNotFoundException cnfexc)
      {
        notifyClientError("Received object with unknown class: " + cnfexc.getMessage(), cnfexc);
        disconnect();
      }
    }
    
    this.connectedMap.remove(currentThread);
  }
  
  /**
   * Dispatcher for all asynchronous events received.
   * 
   * @param event
   */
  protected void dispatchEvent(AbstractEvent event)
  {
    synchronized (osgiListeners)
    {
      for (OSGiEventListener listener : osgiListeners)
      {
        if ((listener instanceof BundleListener) && (event instanceof BundleEvent))
        {
          ((BundleListener)listener).bundleChanged((BundleEvent)event, remoteId);
        }
        else if ((listener instanceof ServiceListener) && (event instanceof ServiceEvent))
        {
          ((ServiceListener)listener).serviceChanged((ServiceEvent)event, remoteId);
        }
        else if ((listener instanceof FrameworkListener) && (event instanceof FrameworkEvent))
        {
          ((FrameworkListener)listener).frameworkStateChanged((FrameworkEvent)event, remoteId);
        }
      }
    }
  }
  

  /**
   * Convienance method for capturing all network send logic to remote esb.
   * 
   */
  protected <T> T sendRequest(AbstractMessage request, Class<T> expectedResponseType) throws IllegalStateException, ConnectException, RemoteServerException
  {
    assertConnected();
    
    LinkedBlockingQueue<AbstractMessage> queue = new LinkedBlockingQueue<AbstractMessage>(1);
    this.responseQueueMap.put(request.getMessageId(), queue);
    
    try
    {
      synchronized(this.outputStream)
      {
        this.outputStream.writeObject(request);
        this.outputStream.reset();
      }

      Object response = null;

      try
      {
        response = queue.poll(this.timeoutSeconds, TimeUnit.SECONDS);
        
        if (response != null)
        {
          if (response instanceof ExceptionResponse)
          {
            throw new RemoteServerException((ExceptionResponse)response);
          }
          else
          {
            try
            {
              return (T)response;
            }
            catch (ClassCastException ccexc)
            {
              notifyClientError("Received unknown resopnse from server " + response + " of class " + response.getClass());
              throw new RemoteServerException("Unknown response from server.");
            }
          }
        }
        else
        {
          StackTraceElement callingFrame = new Exception().getStackTrace()[1];
          notifyClientError("Timeout occured for: " + callingFrame);
          disconnect();
          
          throw new ConnectException("Timeout occurred while waiting for server response.");
        }
      }
      catch (InterruptedException iexc)
      {
        StackTraceElement callingFrame = new Exception().getStackTrace()[1];
        notifyClientError("Intterupt exception occured for: " + callingFrame + " with message: " + iexc.getMessage());
        disconnect();
        throw new ConnectException("Timeout occurred while waiting for server response.");
      }

    }
    catch (IOException  ioexc)
    {
      StackTraceElement callingFrame = new Exception().getStackTrace()[1];
      notifyClientError("IO exception occurred while trying to communicate with server for: " + callingFrame + " with message: " + ioexc.getMessage(), ioexc);
      disconnect();
      throw new ConnectException(ioexc.getMessage());
    }
    finally
    {
      this.responseQueueMap.remove(request.getMessageId());
    }
  }

  protected void notifyClientError(String message)
  {
    notifyClientError(message, null);
  }

  protected void notifyClientError(String message, Exception exception)
  {
    synchronized (clientListeners)
    {
      for (NetworkClientListener listener : clientListeners)
      {
        listener.unexpectedClientError(this, message, exception);
      }
    }
  }
  
  protected void assertConnected()
  {
    if (!isConnected())
    {
      throw new IllegalArgumentException("Not currently connected.");
    }
  }

  protected void assertNotConnected()
  {
    if (isConnected())
    {
      throw new IllegalArgumentException("Currently connected.");
    }
  }
  
  protected void disconnectCleanup()
  {
    try
    {
      this.socket.close();
    }
    catch (Exception exc)
    {
      
    }
    
    this.socket = null;
    this.serverProcessThread = null;
  }
}
