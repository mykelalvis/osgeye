package org.osgeye.domain;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Configuration implements Serializable
{
  static private final long serialVersionUID = 727221690355982977L;
  
  private String bundleLocation;
  
  private String factoryPid;
  
  public String pid;
  
  public Map<String, String> properties;

  public Configuration(org.osgi.service.cm.Configuration osgiConfig)
  {
    bundleLocation = osgiConfig.getBundleLocation();
    factoryPid = osgiConfig.getFactoryPid();
    pid = osgiConfig.getPid();
    
    properties = new HashMap<String, String>();
    Dictionary osgiProperties = osgiConfig.getProperties();
    if (osgiProperties != null)
    {
      Enumeration keysEnum = osgiProperties.keys();
      while (keysEnum.hasMoreElements())
      {
        String key = keysEnum.nextElement().toString();
        Object value = osgiProperties.get(key);
        properties.put(key, (value == null) ? null : value.toString());
      }
    }
  }
  
  public Configuration(String bundleLocation, String factoryPid, String pid, Map<String, String> properties)
  {
    this.bundleLocation = bundleLocation;
    this.factoryPid = factoryPid;
    this.pid = pid;
    this.properties = properties;
  }

  public String getBundleLocation()
  {
    return bundleLocation;
  }

  public String getFactoryPid()
  {
    return factoryPid;
  }

  public String getPid()
  {
    return pid;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }
}
