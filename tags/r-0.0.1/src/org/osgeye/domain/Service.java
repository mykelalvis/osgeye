package org.osgeye.domain;

import static org.osgi.framework.Constants.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.ServiceReference;

public class Service implements Serializable
{
  static private final long serialVersionUID = -4994623107777929566L;

  private Long id;
  private List<String> interfaces;
  private String description;
  private String pid;
  private Integer ranking;
  private String vendor;
  private Bundle bundle;
  
  public Service(Bundle bundle, ServiceReference serviceReference)
  {
    this.bundle = bundle;
    
    id = (Long)serviceReference.getProperty(SERVICE_ID);
    pid = (String)serviceReference.getProperty(SERVICE_PID);
    description = (String)serviceReference.getProperty(SERVICE_DESCRIPTION);
    vendor = (String)serviceReference.getProperty(SERVICE_VENDOR);
    ranking = (Integer)serviceReference.getProperty(SERVICE_RANKING);
    interfaces = Arrays.asList((String[])serviceReference.getProperty(OBJECTCLASS));
  }

  public Long getId()
  {
    return id;
  }

  public List<String> getInterfaces()
  {
    return interfaces;
  }

  public String getDescription()
  {
    return description;
  }

  public String getPid()
  {
    return pid;
  }

  public Integer getRanking()
  {
    return ranking;
  }

  public String getVendor()
  {
    return vendor;
  }

  public Bundle getBundle()
  {
    return bundle;
  }
  
  public String toString()
  {
    String text = "";
    for (int i = 0; i < interfaces.size(); i++)
    {
      if (i != 0) text += ", ";
      text += interfaces.get(i);
    }
    if (description != null)  text += " - " + description;
    return text;
  }
}
