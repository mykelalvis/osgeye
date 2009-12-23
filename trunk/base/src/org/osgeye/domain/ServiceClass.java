package org.osgeye.domain;

import java.io.Serializable;

public class ServiceClass implements Serializable
{
  private final String className;
  
  private final boolean interfce;

  public ServiceClass(String className)
  {
    this.className = className;
    interfce = false;
  }

  
  public ServiceClass(Class clazz)
  {
    this.className = clazz.getCanonicalName();
    this.interfce = clazz.isInterface();
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public boolean isInterface()
  {
    return interfce;
  }
  
  public String toString()
  {
    return className;
  }
}
