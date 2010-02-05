package org.osgeye.remotereflect;

import java.io.Serializable;
import java.util.List;

public class MethodDefinition implements Serializable
{
  private String name;
  
  private AccessLevel accessLevel;
  
  private boolean statc;
  
  private List<TypeDefinition> parameters;
  
  private TypeDefinition returnType;

  public MethodDefinition(String name, AccessLevel accessLevel, boolean statc, List<TypeDefinition> parameters, TypeDefinition returnType)
  {
    this.name = name;
    this.accessLevel = accessLevel;
    this.statc = statc;
    this.parameters = parameters;
    this.returnType = returnType;
  }
  
  public String getName()
  {
    return name;
  }

  public AccessLevel getAccessLevel()
  {
    return accessLevel;
  }
  
  public boolean isStatic()
  {
    return statc;
  }

  public List<TypeDefinition> getParameters()
  {
    return parameters;
  }

  public TypeDefinition getReturnType()
  {
    return returnType;
  }
}