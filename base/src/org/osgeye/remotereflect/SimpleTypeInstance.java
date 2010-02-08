package org.osgeye.remotereflect;

public class SimpleTypeInstance extends AbstractTypeInstance
{
  private SimpleTypeDefinition definition;
  
  private Object object;
  
  public SimpleTypeInstance(SimpleTypeDefinition definition, Object object)
  {
    this(null, definition, object);
  }

  public SimpleTypeInstance(String name, SimpleTypeDefinition definition, Object object)
  {
    super(name);
    this.definition = definition;
    this.object = object;
  }
  
  public SimpleTypeDefinition getDefinition()
  {
    return definition;
  }

  public Object getObject()
  {
    return object;
  }
}