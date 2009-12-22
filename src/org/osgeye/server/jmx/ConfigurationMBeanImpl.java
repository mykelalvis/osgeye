package org.osgeye.server.jmx;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.cm.Configuration;

public class ConfigurationMBeanImpl extends StandardMBean implements ConfigurationMBean
{
  private Configuration configuration;
  
  public ConfigurationMBeanImpl(Configuration configuration) throws NotCompliantMBeanException
  {
    super(ConfigurationMBean.class);
    this.configuration = configuration;
  }

  public void delete() throws IOException
  {
    configuration.delete();
  }

  public String getBundleLocation()
  {
    return configuration.getBundleLocation();
  }

  public String getPid()
  {
    return configuration.getPid();
  }

  public Map<String, String> getProperties()
  {
    Map<String, String> properties = new HashMap<String, String>();
    Dictionary configProps = configuration.getProperties();
    if (configProps != null)
    {
      Enumeration keys = configProps.keys();
      while (keys.hasMoreElements())
      {
        Object key = keys.nextElement();
        properties.put(key.toString(), configProps.get(key).toString());
      }
    }
    return properties;
  }

  public void update() throws IOException
  {
    configuration.update();
  }

  public void update(String url) throws IOException
  {
    InputStream inStream = new URL(url).openStream();
    Properties properties = new Properties();
    properties.load(inStream);
    inStream.close();
    
    Dictionary configProps = configuration.getProperties();
    if (configProps == null)
    {
      configProps = new Hashtable();
    }
    
    for (Entry entry : properties.entrySet())
    {
      configProps.put(entry.getKey(), entry.getValue());
    }
    
    configuration.update(configProps);
  }
}
