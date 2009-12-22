package org.osgeye.server.jmx;

public interface ServiceMBean
{
  public String getInterface();

  public String getBundle();

  public Long getId();
  
  public String description();
  
  public String getPid();
  
  public Integer getRanking();
}