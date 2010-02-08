package org.osgeye.remotereflect;

import java.util.Date;

public enum SimpleTypeDefinition
{
  BYTE("java.lang", "Byte"),
  BOOLEAN("java.lang", "Boolean"),
  SHORT("java.lang", "Short"),
  INTEGER("java.lang", "Integer"),
  LONG("java.lang", "Long"),
  FLOAT("java.lang", "Float"),
  DOUBLE("java.lang", "Double"),
  STRING("java.lang", "String"),
  DATE("java.util", "Date");

  private String packge;
  
  private String name;
  
  private SimpleTypeDefinition(String packge, String name)
  {
    this.packge = packge;
    this.name = name;
  }
  
  public String getQualifiedName()
  {
    if (packge == null)
    {
      return name;
    }
    else
    {
      return packge + "." + name;
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getPackage()
  {
    return packge;
  }
  
  public boolean equalsClass(Class clazz)
  {
   switch (this)
   {
     case BYTE:
       return ((byte.class == clazz) || (Byte.class == clazz));
       
     case BOOLEAN:
       return ((boolean.class == clazz) || (Boolean.class == clazz));
       
     case SHORT:
       return ((short.class == clazz) || (Short.class == clazz));
       
     case INTEGER:
       return ((int.class == clazz) || (Integer.class == clazz));
       
     case LONG:
       return ((long.class == clazz) | (Long.class == clazz));
       
     case FLOAT:
       return ((float.class == clazz) || (Float.class == clazz));
       
     case DOUBLE:
       return ((double.class == clazz) || (Double.class == clazz));
       
     case STRING:
       return (String.class == clazz);
       
     case DATE:
       return (Date.class == clazz);
       
     default:
       return false;
   }
  }
}
