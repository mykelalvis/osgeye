package org.osgeye.server.osgi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleIdentity;
import org.osgeye.domain.Configuration;
import org.osgeye.domain.FrameworkState;
import org.osgeye.domain.Version;
import org.osgeye.domain.VersionRange;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.events.BundleEvent;
import org.osgeye.events.FrameworkEvent;
import org.osgeye.events.BundleEvent.BundleEventType;
import org.osgeye.events.FrameworkEvent.FrameworkEventType;
import org.osgeye.messages.AbstractMessage;
import org.osgeye.messages.BooleanResponse;
import org.osgeye.messages.BundleIdsResponse;
import org.osgeye.messages.BundlesResponse;
import org.osgeye.messages.ExceptionResponse;
import org.osgeye.messages.GetAllBundlesRequest;
import org.osgeye.messages.GetBundleIds;
import org.osgeye.messages.GetConfigurationsRequest;
import org.osgeye.messages.GetConfigurationsResponse;
import org.osgeye.messages.GetFrameworkStateRequest;
import org.osgeye.messages.GetFrameworkStateResponse;
import org.osgeye.messages.InstallBundleRequest;
import org.osgeye.messages.InstallBundleResponse;
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
import org.osgeye.server.EventDispatcher;
import org.osgeye.server.MessageProcessor;
import org.osgeye.server.network.ClientConnection;
import org.osgeye.server.osgi.utils.BundleCreator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProcessorImpl implements MessageProcessor
{
  private BundleContext bundleContext;
  private PackageAdmin packageAdminService;
  private StartLevel startLevelService;
  private ConfigurationAdmin configAdminService;
  private EventDispatcher eventDispatcher;
  
  private Logger logger;
  private BundleCreator bundleCreator;
  
  public MessageProcessorImpl(BundleContext bundleContext, PackageAdmin packageAdminService, 
      StartLevel startLevelService, ConfigurationAdmin configAdminService)
  {
    this.bundleContext = bundleContext;
    this.packageAdminService = packageAdminService;
    this.startLevelService = startLevelService;
    this.configAdminService = configAdminService;
    
    logger = LoggerFactory.getLogger(getClass());
    
    bundleCreator = new BundleCreator(packageAdminService, startLevelService);
  }
  
  public void setEventDispatcher(EventDispatcher eventDispatcher)
  {
    this.eventDispatcher = eventDispatcher;
  }

  public AbstractMessage processRequest(AbstractMessage request, ClientConnection clientConnection)
  {
    try
    {
      if (request instanceof GetFrameworkStateRequest)
      {
        return new GetFrameworkStateResponse(request.getMessageId(), getFrameworkState());
      }
      if (request instanceof GetAllBundlesRequest)
      {
        return getAllBundles((GetAllBundlesRequest)request);
      }
      else if (request instanceof GetBundleIds)
      {
        return getBundleIds((GetBundleIds)request);
      }
      else if (request instanceof StartBundlesRequest)
      {
        start((StartBundlesRequest)request);
      }
      else if (request instanceof StopBundlesRequest)
      {
        stop((StopBundlesRequest)request);
      }
      else if (request instanceof UninstallBundlesRequest)
      {
        uninstall((UninstallBundlesRequest)request);
      }
      else if (request instanceof UpdateBundleRequest)
      {
        update((UpdateBundleRequest)request);
      }
      else if (request instanceof RefreshPackagesRequest)
      {
        refreshPackages((RefreshPackagesRequest)request);
      }
      else if (request instanceof ResolveBundlesRequest)
      {
        boolean result = resolveBundles((ResolveBundlesRequest)request);
        return new BooleanResponse(request.getMessageId(), result);
      }
      else if (request instanceof GetConfigurationsRequest)
      {
        List<Configuration> configs = getConfigurations(((GetConfigurationsRequest)request).getFilter());
        return new GetConfigurationsResponse(request.getMessageId(), configs);
      }
      else if (request instanceof InstallBundleRequest)
      {
        return new InstallBundleResponse(request.getMessageId(), install((InstallBundleRequest)request));
      }
      else if (request instanceof SetStartLevelRequest)
      {
        setStartLevel(((SetStartLevelRequest)request).getStartlevel());
      }
      else if (request instanceof SetInitBundleStartLevelRequest)
      {
        setInitialBundleLevel(((SetInitBundleStartLevelRequest)request).getStartLevel());
      }
      else if (request instanceof SetBundlesStartLevelRequest)
      {
        SetBundlesStartLevelRequest sbslr= (SetBundlesStartLevelRequest)request;
        setBundlesStartLevel(sbslr.getStartLevel(), sbslr.getBundleIds());
      }
      else
      {
        return new ExceptionResponse(request.getMessageId(), new Exception("Unknown request class " + request.getClass()));
      }
      
      return new VoidResponse(request.getMessageId());
    }
    catch (Exception exc)
    {
      logger.warn("Unexpected exception for request " + request.getClass() + " with error " + exc.getMessage(), exc);
      return new ExceptionResponse(request.getMessageId(), exc);
    }
  }
  
  protected FrameworkState getFrameworkState()
  {
    return new FrameworkState(startLevelService.getStartLevel(), startLevelService.getInitialBundleStartLevel());
  }
  
  protected BundlesResponse getAllBundles(GetAllBundlesRequest request)
  {
    List<Bundle> bundles = new ArrayList<Bundle>();
    org.osgi.framework.Bundle[] osgiBundles = bundleContext.getBundles();
    
    for (org.osgi.framework.Bundle osgiBundle : osgiBundles)
    {
      bundles.add(bundleCreator.createBundle(osgiBundle));
    }
    
    return new BundlesResponse(request.getMessageId(), bundles);
  }
  
  protected BundleIdsResponse getBundleIds(GetBundleIds request)
  {
    List<BundleIdentity> bundleIds = new ArrayList<BundleIdentity>();
    
    org.osgi.framework.Bundle[] osgiBundles = bundleContext.getBundles();
    
    String bundleNamePattern = request.getSymbolicNamePattern();
    VersionRange versionRange = request.getWithinRange();
    for (org.osgi.framework.Bundle osgiBundle : osgiBundles)
    {
      String symbolicName = osgiBundle.getSymbolicName();
      String versionStr = (String)osgiBundle.getHeaders().get(Manifest.VERSION);
      Version version = (versionStr == null) ? new Version() : new Version(versionStr);
      
      if (symbolicName.matches(bundleNamePattern))
      {
        if ((versionRange == null) || versionRange.isWithinRange(version))
        {
          bundleIds.add(new BundleIdentity(osgiBundle.getBundleId(), symbolicName, version));
        }
      }
    }
    
    return new BundleIdsResponse(request.getMessageId(), bundleIds);
  }
  
  protected void start(StartBundlesRequest request) throws BundleException
  {
    Integer options = (request.getOptions() == null) ? null : request.getOptions().getOsgiValue();
    for (Long bundleId : request.getBundleIds())
    {
      org.osgi.framework.Bundle osgiBundle = bundleContext.getBundle(bundleId);
      if (osgiBundle == null) throw new IllegalArgumentException("Invalid bundle id " + bundleId);
      if (options == null)
      {
        osgiBundle.start();
      }
      else 
      {
        osgiBundle.start(options);
      }
    }
  }

  protected void stop(StopBundlesRequest request) throws BundleException
  {
    Integer options = (request.getOptions() == null) ? null : request.getOptions().getOsgiValue();
    for (Long bundleId : request.getBundleIds())
    {
      org.osgi.framework.Bundle osgiBundle = bundleContext.getBundle(bundleId);
      if (osgiBundle == null) throw new IllegalArgumentException("Invalid bundle id " + bundleId);
      if (options == null)
      {
        osgiBundle.stop();
      }
      else 
      {
        osgiBundle.stop(options);
      }
    }
  }

  protected void uninstall(UninstallBundlesRequest request) throws BundleException
  {
    for (Long bundleId : request.getBundleIds())
    {
      org.osgi.framework.Bundle osgiBundle = bundleContext.getBundle(bundleId);
      if (osgiBundle == null) throw new IllegalArgumentException("Invalid bundle id " + bundleId);
      osgiBundle.uninstall();
    }
  }
  
  protected void update(UpdateBundleRequest request) throws BundleException
  {
    org.osgi.framework.Bundle osgiBundle = bundleContext.getBundle(request.getBundleId());
    if (osgiBundle == null) throw new IllegalArgumentException("Invalid bundle id " + request.getBundleId());
    
    osgiBundle.update();
  }
  
  protected void refreshPackages(RefreshPackagesRequest request)
  {
    packageAdminService.refreshPackages(getBundles(request.getBundleIds()));
  }
  
  protected boolean resolveBundles(ResolveBundlesRequest request)
  {
    return packageAdminService.resolveBundles(getBundles(request.getBundleIds()));
  }
  
  protected void setStartLevel(int startLevel)
  {
    startLevelService.setStartLevel(startLevel);
  }
  
  protected void setInitialBundleLevel(int startLevel)
  {
    startLevelService.setInitialBundleStartLevel(startLevel);
    
    /*
     * As far as I can tell the OSGi framework doesn't provide event notification
     * for this.
     */
    FrameworkEvent event = new FrameworkEvent(FrameworkEventType.BUNDLE_INITIAL_START_LEVEL_CHANGED, new Integer(startLevel));
    eventDispatcher.dispatchEvent(event);
  }
  
  protected void setBundlesStartLevel(int startLevel, List<Long> bundleIds)
  {
    for (Long bundleId : bundleIds)
    {
      org.osgi.framework.Bundle osgiBundle = bundleContext.getBundle(bundleId);
      if (osgiBundle != null)
      {
        startLevelService.setBundleStartLevel(osgiBundle, startLevel);
        eventDispatcher.dispatchEvent(new BundleEvent(bundleCreator.createBundle(osgiBundle), BundleEventType.START_LEVEL_CHANGED));
      }
    }
  }
  
  protected List<Configuration> getConfigurations(String filter) throws IOException, InvalidSyntaxException
  {
    List<Configuration> configurations = new ArrayList<Configuration>();
    org.osgi.service.cm.Configuration[] osgiConfigs = configAdminService.listConfigurations(filter);
    if (osgiConfigs != null)
    {
      for (org.osgi.service.cm.Configuration osgiConfig : osgiConfigs)
      {
        configurations.add(new Configuration(osgiConfig));
      }
    }
    return configurations;
  }
  
  protected Bundle install(InstallBundleRequest request) throws IOException, BundleException
  {
    File bundleFile = bundleContext.getDataFile(request.getFileName());
    
    if (bundleFile.exists())
    {
      if (!bundleFile.delete())
      {
        throw new IOException("Unable to delete exisiting bundle file " + bundleFile.getAbsolutePath());
      }
    }
    
    FileOutputStream fos = new FileOutputStream(bundleFile);
    fos.write(request.getBundleBytes());
    fos.close();
    String fileUrl = "file://" +  bundleFile.getAbsolutePath();
    org.osgi.framework.Bundle osgiBundle = bundleContext.installBundle(fileUrl);
    
    return bundleCreator.createBundle(osgiBundle);
  }
  
  private org.osgi.framework.Bundle[] getBundles(List<Long> bundleIds)
  {
    org.osgi.framework.Bundle[] bundles = new org.osgi.framework.Bundle[bundleIds.size()];
    for (int i = 0; i < bundleIds.size(); i++)
    {
      bundles[i] = bundleContext.getBundle(bundleIds.get(i));
    }
    return bundles;
  }
}
