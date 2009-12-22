package org.osgeye.server.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;

public interface BundleMBean
{
  public long getId();

  public String getSymbolicName();

  public String getVersion();
  
  public String getLocation();
  
  public Date getLastModified();
  
  public int getStartLevel();

  public boolean isFragment();
  
  public String[] getHosts();
  
  public String[] getFragments();
  
  public String[] getExportedPackages();
  
  public String[] getImportedPackages();

  public Map<String, String> getHeaders();
  
  public String getState();
  
  public void start();
  
  public void start(int options);
  
  public void stop();
  
  public void stop(int options);
  
  public void uninstall();
  
  public void update();
  
  public void update(String url) throws MalformedURLException, IOException;
  
  public void refreshPackages();
  
  public boolean resolve();
  
  public String viewManifest() throws IOException;
}
