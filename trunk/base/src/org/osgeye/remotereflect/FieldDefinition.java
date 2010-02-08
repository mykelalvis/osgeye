package org.osgeye.remotereflect;

import java.io.Serializable;

public class FieldDefinition implements Serializable
{
  private String name;
  
  private AccessLevel accessLevel;
  
  private boolean statc;
  
  private TypeDefinition typeDefinition;

  public FieldDefinition(String name, AccessLevel accessLevel, boolean statc, TypeDefinition typeDefinition)
  {
    this.name = name;
    this.accessLevel = accessLevel;
    this.statc = statc;
    this.typeDefinition = typeDefinition;
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
  
  public TypeDefinition getTypeDefinition()
  {
    return typeDefinition;
  }
}
