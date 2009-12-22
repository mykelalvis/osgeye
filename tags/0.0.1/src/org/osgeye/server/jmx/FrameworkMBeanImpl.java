package org.osgeye.server.jmx;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.startlevel.StartLevel;

public class FrameworkMBeanImpl extends StandardMBean implements FrameworkMBean
{
  private StartLevel startLevel;
  
  public FrameworkMBeanImpl(StartLevel startLevel) throws NotCompliantMBeanException
  {
    super(FrameworkMBean.class);
    
    this.startLevel = startLevel;
  }

  public int getInitialBundleStartLevel()
  {
    return startLevel.getInitialBundleStartLevel();
  }

  public int getStartLevel()
  {
    return startLevel.getStartLevel();
  }

  public void setInitialBundleStartLevel(int initialBundleStartLevel)
  {
    startLevel.setInitialBundleStartLevel(initialBundleStartLevel);;
  }

  public void setStartLevel(int startLevel)
  {
    this.startLevel.setStartLevel(startLevel);
  }
}
