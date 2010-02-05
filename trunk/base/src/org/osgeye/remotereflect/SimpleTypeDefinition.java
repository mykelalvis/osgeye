package org.osgeye.remotereflect;

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
}
