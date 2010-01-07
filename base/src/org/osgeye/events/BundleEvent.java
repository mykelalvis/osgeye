package org.osgeye.events;

import org.osgeye.domain.Bundle;

public class BundleEvent extends AbstractEvent
{
  static private final long serialVersionUID = -8887391693766704852L;

  static public enum BundleEventType
  {
    INSTALLED("Installed", "The bundle has been installed.", org.osgi.framework.BundleEvent.INSTALLED),
    LAZY_ACTIVATION("Lazy Activation", "The bundle will be lazily activated.", org.osgi.framework.BundleEvent.LAZY_ACTIVATION),
    RESOLVED("Resolved", "The bundle has been resolved.", org.osgi.framework.BundleEvent.RESOLVED),
    STARTED("Started", "The bundle has been started.", org.osgi.framework.BundleEvent.STARTED),
    STARTING("Starting", "The bundle is about to be activated.", org.osgi.framework.BundleEvent.STARTING),
    STOPPED("Stopped", "The bundle has been stopped.", org.osgi.framework.BundleEvent.STOPPED),
    STOPPING("Stopping", "The bundle is about to deactivated.", org.osgi.framework.BundleEvent.STOPPING),
    UNINSTALLED("Uninstalled", "The bundle has been uninstalled.", org.osgi.framework.BundleEvent.UNINSTALLED),
    UNRESOLVED("Unresolved", "The bundle has been unresolved.", org.osgi.framework.BundleEvent.UNRESOLVED),
    UPDATED("Updated", "The bundle has been updated.", org.osgi.framework.BundleEvent.UPDATED),
    START_LEVEL_CHANGED("Start Level Changed", "The bundle's start level has been updated.", null);
    
    private String text;
    private String description;
    private Integer osgiValue;
    
    static public BundleEventType fromOsgiValue(Integer osgiValue)
    {
      for (BundleEventType bet : values())
      {
        if (bet.osgiValue == osgiValue) return bet;
      }
      throw new IllegalArgumentException("Invalid OSGi value " + osgiValue);
    }
    
    private BundleEventType(String text, String description, Integer osgiValue)
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

    public Integer getOsgiValue()
    {
      return osgiValue;
    }
  }
  
  private Long uninstalledBundleId;
  private String uninstalledBundleName;
  
  private Bundle bundle;
  private BundleEventType eventType;

  public BundleEvent(Long uninstalledBundleId, String uninstalledBundleName, BundleEventType eventType)
  {
    this.uninstalledBundleId = uninstalledBundleId;
    this.uninstalledBundleName = uninstalledBundleName;
    this.eventType = eventType;
  }

  public BundleEvent(Bundle bundle, BundleEventType eventType)
  {
    this.bundle = bundle;
    this.eventType = eventType;
  }

  public Long getUninstalledBundleId()
  {
    return uninstalledBundleId;
  }

  public String getUninstalledBundleName()
  {
    return uninstalledBundleName;
  }

  public Bundle getBundle()
  {
    return bundle;
  }

  public BundleEventType getEventType()
  {
    return eventType;
  }
}
