package org.osgeye.events;

import org.osgeye.domain.Service;

public class ServiceEvent extends AbstractEvent
{
  static private final long serialVersionUID = -3880385979725484412L;
  
  static public enum ServiceEventType
  {
    MODIFIED("Modified", "The properties of a registered service have been modified.", org.osgi.framework.ServiceEvent.MODIFIED),
    REGISTERED("Registered", "This service has been registered.", org.osgi.framework.ServiceEvent.REGISTERED),
    UNREGISTERING("Unregistering", "This service is in the process of being unregistered.", org.osgi.framework.ServiceEvent.UNREGISTERING);
    
    static public ServiceEventType fromOsgiValue(int osgiValue)
    {
      for (ServiceEventType set : values())
      {
        if (set.osgiValue == osgiValue) return set;
      }
      throw new IllegalArgumentException("Invalid service event type osgi value " + osgiValue);
    }
    
    private String text;
    private String description;
    private int osgiValue;
    
    private ServiceEventType(String text, String description, int osgiValue)
    {
      this.text = text;
      this.description = description;
      this.osgiValue = osgiValue;
    }

    public String getText()
    {
      return text;
    }

    public String getDescription()
    {
      return description;
    }

    public int getOsgiValue()
    {
      return osgiValue;
    }
  }
  
  private Service service;
  private ServiceEventType eventType;
  
  public ServiceEvent(Service service, ServiceEventType eventType)
  {
    this.service = service;
    this.eventType = eventType;
  }

  public Service getService()
  {
    return service;
  }

  public ServiceEventType getEventType()
  {
    return eventType;
  }
  
}
