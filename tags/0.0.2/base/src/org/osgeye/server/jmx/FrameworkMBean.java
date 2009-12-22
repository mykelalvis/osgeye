package org.osgeye.server.jmx;

public interface FrameworkMBean
{
  public int getStartLevel();
  
  public void setStartLevel(int startLevel);
  
  public int getInitialBundleStartLevel();
  
  public void setInitialBundleStartLevel(int initialBundleStartLevel);
}
