package org.osgeye.domain;

import org.osgi.framework.Bundle;

public enum StopBundleOptions
{
  /**
   * TThe bundle stop is transient and the persistent autostart setting of 
   * the bundle is not modified. 
   */
  STOP_TRANSIENT("Transient", Bundle.STOP_TRANSIENT);

  static public StopBundleOptions fromText(String text)
  {
    for (StopBundleOptions sbo : values())
    {
      if (sbo.text.equals(text)) return sbo;
    }
    throw new IllegalArgumentException("Invalid text value " + text);
  }
  private String text;
  private int osgiValue;
  
  private StopBundleOptions(String text, int osgiValue)
  {
    this.text = text;
    this.osgiValue = osgiValue;
  }

  public int getOsgiValue()
  {
    return osgiValue;
  }

  public String getText()
  {
    return text;
  }
}
