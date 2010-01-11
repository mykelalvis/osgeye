package org.osgeye.events;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.Framework;

public class FrameworkEvent extends AbstractEvent
{
  static public final int BUNDLE_INITIAL_START_LEVEL_CHANGED_OSGI_VALUE = -1;
  
  static public enum FrameworkEventType
  {
    ERROR("Error", org.osgi.framework.FrameworkEvent.ERROR),
    WARNING("Warning", org.osgi.framework.FrameworkEvent.WARNING),
    INFO("Info", org.osgi.framework.FrameworkEvent.INFO),
    PACKAGES_REFRESHED("Packages Refreshed", org.osgi.framework.FrameworkEvent.PACKAGES_REFRESHED),
    STARTED("Started", org.osgi.framework.FrameworkEvent.STARTED),
    START_LEVEL_CHANGED("Start Level Changed", org.osgi.framework.FrameworkEvent.STARTLEVEL_CHANGED),
    BUNDLE_INITIAL_START_LEVEL_CHANGED("Bundle Initial Start Level Changed", BUNDLE_INITIAL_START_LEVEL_CHANGED_OSGI_VALUE);
    
    static public FrameworkEventType fromOsgiValue(int osgiValue)
    {
      for (FrameworkEventType fet : values())
      {
        if (fet.osgiValue == osgiValue) return fet;
      }
      throw new IllegalArgumentException("Invalid framework event osgi value " + osgiValue);
    }
    
    private String text;
    private int osgiValue;
    
    private FrameworkEventType(String text, int osgiValue)
    {
      this.text = text;
      this.osgiValue = osgiValue;
    }

    public String getText()
    {
      return text;
    }

    public int getOsgiValue()
    {
      return osgiValue;
    }
    
  }

  private FrameworkEventType eventType;
  
  private Framework framework;
  
  private Bundle bundle;
  
  private Exception error;
  
  public FrameworkEvent(FrameworkEventType eventType, Framework framework)
  {
    this.eventType = eventType;
    this.framework = framework;
  }

  public FrameworkEvent(FrameworkEventType eventType, Framework framework, Bundle bundle, Exception error)
  {
    this.eventType = eventType;
    this.framework = framework;
    this.bundle = bundle;
    this.error = error;
  }
  
  public FrameworkEventType getEventType()
  {
    return eventType;
  }

  public Framework getFramework()
  {
    return framework;
  }

  public Bundle getBundle()
  {
    return bundle;
  }

  public Exception getError()
  {
    return error;
  }
}
