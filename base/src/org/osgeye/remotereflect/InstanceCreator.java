package org.osgeye.remotereflect;

import java.lang.reflect.Field;

public class InstanceCreator
{
  private DefinitionCreator definitionCreator;
  
  public InstanceCreator()
  {
    definitionCreator = new DefinitionCreator();
  }
  
  public AbstractTypeInstance createInstance(Object obj, String name)
  {
    Class clazz = obj.getClass();
    TypeDefinition typeDef = definitionCreator.createDefinition(clazz);
    
    if (typeDef.isSimpleType())
    {
      return new SimpleTypeInstance(name, typeDef.getSimpleTypeDef(), obj);
    }
    else
    {
      ComplexTypeDefinition complexTypeDef = typeDef.getComplexTypeDef();
      ComplexTypeInstance complexTypeInstance = new ComplexTypeInstance(name, complexTypeDef);
      
      for (FieldDefinition fieldDef : complexTypeDef.getFields())
      {
        try
        {
          Field field = clazz.getDeclaredField(fieldDef.getName());
          
          try
          {
            field.setAccessible(true);
            Object fieldObject = field.get(obj);
            AbstractTypeInstance fieldTypeInstance = createInstance(fieldObject, field.getName());
            if (fieldTypeInstance instanceof SimpleTypeInstance)
            {
              complexTypeInstance.addSimpleTypeField((SimpleTypeInstance)fieldTypeInstance);
            }
            else
            {
              complexTypeInstance.addComplexTypeField((ComplexTypeInstance)fieldTypeInstance);
            }
          }
          catch (SecurityException sexc)
          {
            sexc.printStackTrace();
          }
          catch (IllegalArgumentException iaexc)
          {
            iaexc.printStackTrace();
          }
          catch (IllegalAccessException iaexc)
          {
            iaexc.printStackTrace();
          }
          finally
          {
            try
            {
              field.setAccessible(false);
            }
            catch (Exception exc) {}
          }
        }
        catch (NoSuchFieldException nsfexc)
        {
          nsfexc.printStackTrace();
        }
      }
      
      return complexTypeInstance;
    }
  }
}
