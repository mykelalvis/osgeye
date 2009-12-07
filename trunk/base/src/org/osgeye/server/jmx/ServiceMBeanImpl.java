package org.osgeye.server.jmx;

import static org.osgi.framework.Constants.*;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgeye.utils.OSGiUtils;
import org.osgi.framework.ServiceReference;

public class ServiceMBeanImpl extends StandardMBean implements ServiceMBean
{
  private String interfce;
  
  private ServiceReference serviceReference;
  
  public ServiceMBeanImpl(ServiceReference serviceReference, String interfce) throws NotCompliantMBeanException
  {
    super(ServiceMBean.class);
    
    this.serviceReference = serviceReference;
    this.interfce = interfce;
  }

  public Long getId()
  {
    return (Long)serviceReference.getProperty(SERVICE_ID);
  }

  public String description()
  {
    return (String)serviceReference.getProperty(SERVICE_DESCRIPTION);
  }

  public String getInterface()
  {
    return interfce;
  }

  public Integer getRanking()
  {
    return (Integer)serviceReference.getProperty(SERVICE_RANKING);
  }

  public String getBundle()
  {
    return OSGiUtils.toString(serviceReference.getBundle());
  }

  public String getPid()
  {
    return (String)serviceReference.getProperty(SERVICE_PID);
  }

}
