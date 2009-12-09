package org.osgeye.server.jmx;

import java.io.IOException;
import java.util.Map;

public interface ConfigurationMBean
{
  public String getPid();
  
  public String getBundleLocation();
  
  public Map<String, String> getProperties();
  
  public void update() throws IOException;
  
  public void update(String url) throws IOException;
  
  public void delete() throws IOException;
}
