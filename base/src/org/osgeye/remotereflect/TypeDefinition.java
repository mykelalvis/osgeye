package org.osgeye.remotereflect;

import java.io.Serializable;

public class TypeDefinition implements Serializable
{
  private static final long serialVersionUID = -113388466482596678L;

  private SimpleTypeDefinition simpleTypeDef;
  
  private ComplexTypeDefinition complexTypeDef;
  
  public TypeDefinition(SimpleTypeDefinition simpleTypeDef)
  {
    this.simpleTypeDef = simpleTypeDef;
  }
  
  public TypeDefinition(ComplexTypeDefinition complexTypeDef)
  {
    this.complexTypeDef = complexTypeDef;
  }

  public String getQualifiedName()
  {
    if (isSimpleType())
    {
      return simpleTypeDef.getQualifiedName();
    }
    else
    {
      return complexTypeDef.getQualifiedName();
    }
  }

  public String getName()
  {
    if (isSimpleType())
    {
      return simpleTypeDef.getName();
    }
    else
    {
      return complexTypeDef.getName();
    }
  }
  
  public String getPackage()
  {
    if (isSimpleType())
    {
      return simpleTypeDef.getPackage();
    }
    else
    {
      return complexTypeDef.getPackage();
    }
  }
  
  public boolean isInterface()
  {
    return (isSimpleType()) ? false : complexTypeDef.isInterface();
  }
  
  public boolean isSimpleType()
  {
    return (simpleTypeDef != null);
  }
  
  public boolean isComplexType()
  {
    return !isSimpleType();
  }

  public SimpleTypeDefinition getSimpleTypeDef()
  {
    return simpleTypeDef;
  }

  public ComplexTypeDefinition getComplexTypeDef()
  {
    return complexTypeDef;
  }
}
