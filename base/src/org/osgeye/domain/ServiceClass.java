package org.osgeye.domain;

import java.io.Serializable;

import org.osgeye.remotereflect.TypeDefinition;

public class ServiceClass implements Serializable, Comparable<ServiceClass>
{
  private Service service;
  
  private TypeDefinition typeDefinition;
  
  private String className;

  public ServiceClass(String className, Service service)
  {
    this.className = className;
    this.service = service;
  }

  public ServiceClass(TypeDefinition typeDefinition, Service service)
  {
    this.service = service;
    this.typeDefinition = typeDefinition;
    className = typeDefinition.getQualifiedName();
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
    return (typeDefinition == null) ?  false : typeDefinition.isInterface();
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
