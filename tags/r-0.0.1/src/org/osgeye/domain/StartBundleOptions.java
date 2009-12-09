package org.osgeye.domain;

import org.osgi.framework.Bundle;

public enum StartBundleOptions
{
  /**
   * The bundle start operation is transient and the persistent 
   * autostart setting of the bundle is not modified. 
   */
  START_TRANSIENT("Transient", Bundle.START_TRANSIENT),
  /**
   * The bundle start operation must activate the bundle according to 
   * the bundle's declared activation policy. 
   */
  START_ACTIVATION_POLICY("ActivationPolicy", Bundle.START_ACTIVATION_POLICY);

  static public StartBundleOptions fromText(String text)
  {
    for (StartBundleOptions sbo : values())
    {
      if (sbo.text.equals(text)) return sbo;
    }
    throw new IllegalArgumentException("Invalid text value " + text);
  }
  
  private String text;
  private int osgiValue;
  
  private StartBundleOptions(String text, int osgiValue)
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
