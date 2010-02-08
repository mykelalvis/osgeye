package org.osgeye.domain;

import java.io.Serializable;

public class ServiceClass implements Serializable, Comparable<ServiceClass>
{
  private final String className;
  
  private Service service;
  
  private final boolean interfce;

  public ServiceClass(String className, Service service)
  {
    this.className = className;
    this.service = service;
    interfce = false;
  }

  
  public ServiceClass(Class clazz, Service service)
  {
    this.className = clazz.getCanonicalName();
    this.service = service;
    this.interfce = clazz.isInterface();
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public Service getService()
  {
    return service;
  }

  public boolean isInterface()
  {
    return interfce;
  }
  
  public String toString()
  {
    return className;
  }
  
  public boolean equals(Object obj)
  {
    if (obj instanceof ServiceClass)
    {
      ServiceClass serviceClass = (ServiceClass)obj;
      return (className.equals(serviceClass.className) && service.equals(serviceClass.service));
    }
    else
    {
      return false;
    }
  }


  public int compareTo(ServiceClass sc)
  {
    return className.compareTo(sc.className);
  }
}
