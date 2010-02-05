package org.osgeye.domain;

import static org.osgi.framework.Constants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.osgeye.remotereflect.DefinitionCreator;
import org.osgi.framework.ServiceReference;

public class Service implements Serializable, Comparable<Service>
{
  static private final long serialVersionUID = -4994623107777929566L;

  private Long id;
  private List<ServiceClass> registeredClasses;
  private String description;
  private String pid;
  private Integer ranking;
  private String vendor;
  private Bundle bundle;
  
  public Service(Bundle bundle, ServiceReference serviceReference, org.osgi.framework.Bundle osgiBundle)
  {
    this.bundle = bundle;
    
    id = (Long)serviceReference.getProperty(SERVICE_ID);
    pid = (String)serviceReference.getProperty(SERVICE_PID);
    description = (String)serviceReference.getProperty(SERVICE_DESCRIPTION);
    vendor = (String)serviceReference.getProperty(SERVICE_VENDOR);
    ranking = (Integer)serviceReference.getProperty(SERVICE_RANKING);
    String[] registeredNames = (String[])serviceReference.getProperty(OBJECTCLASS);
    registeredClasses = new ArrayList<ServiceClass>();
    
    DefinitionCreator defCreator = new DefinitionCreator();
    for (String registeredName : registeredNames)
    {
      try
      {
        Class serviceClass = osgiBundle.loadClass(registeredName);
        registeredClasses.add(new ServiceClass(defCreator.createDefinition(serviceClass), this));
      }
      catch (ClassNotFoundException cnfexc)
      {
        registeredClasses.add(new ServiceClass(registeredName, this));
      }
    }
  }

  public Long getId()
  {
    return id;
  }

  public List<ServiceClass> getRegisteredClasses()
  {
    return registeredClasses;
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
  
  public boolean equals(Object obj)
  {
    if (obj instanceof Service)
    {
      Service service = (Service)obj;
      return ((id == service.id) && bundle.equals(service.bundle));
    }
    else
    {
      return false;
    }
  }
  
  public String toString()
  {
    String text = "";
    for (int i = 0; i < registeredClasses.size(); i++)
    {
      if (i != 0) text += ", ";
      text += registeredClasses.get(i);
    }
    if (description != null)  text += " - " + description;
    return text;
  }

  public int compareTo(Service service)
  {
    return toString().compareTo(service.toString());
  }
}
