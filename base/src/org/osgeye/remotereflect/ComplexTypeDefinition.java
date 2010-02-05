package org.osgeye.remotereflect;

import java.io.Serializable;
import java.util.List;

public class ComplexTypeDefinition implements Serializable
{
  private static final long serialVersionUID = 3694932215751226340L;

  private String packge;
  
  private String name;
  
  private boolean interfce;
  
  private boolean array;
  
  private boolean iterable;
  
  private TypeDefinition containerType;
  
  private List<FieldDefinition> fields;
  
  private List<MethodDefinition> methods;

  public ComplexTypeDefinition()
  {}
  
  public ComplexTypeDefinition(String name, String packge, boolean interfce, boolean array,
      boolean iterable, TypeDefinition containerType, List<FieldDefinition> fields, 
      List<MethodDefinition> methods)
  {
    this.name = name;
    this.packge = packge;
    this.interfce = interfce;
    this.array = array;
    this.iterable = iterable;
    this.containerType = containerType;
    this.methods = methods;
    this.methods = methods;
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
  
  public boolean isInterface()
  {
    return interfce;
  }
  
  public void setInterface(boolean interfce)
  {
    this.interfce = interfce;
  }
  
  public boolean isArray()
  {
    return array;
  }
  
  public void setArray(boolean array)
  {
    this.array = array;
  }
  
  public boolean isIterable()
  {
    return iterable;
  }
  
  public void setPackage(String packge)
  {
    this.packge = packge;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setInterfce(boolean interfce)
  {
    this.interfce = interfce;
  }

  public void setIterable(boolean iterable)
  {
    this.iterable = iterable;
  }

  public TypeDefinition getContainerType()
  {
    return containerType;
  }

  public void setContainerType(TypeDefinition containerType)
  {
    this.containerType = containerType;
  }
  
  public List<FieldDefinition> getFields()
  {
    return fields;
  }

  public void setFields(List<FieldDefinition> fields)
  {
    this.fields = fields;
  }

  public List<MethodDefinition> getMethods()
  {
    return methods;
  }

  public void setMethods(List<MethodDefinition> methods)
  {
    this.methods = methods;
  }
}
