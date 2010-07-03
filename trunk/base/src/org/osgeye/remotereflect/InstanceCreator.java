package org.osgeye.remotereflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class InstanceCreator
{
  private DefinitionCreator definitionCreator;
  
  public InstanceCreator()
  {
    definitionCreator = new DefinitionCreator();
  }

  public AbstractTypeInstance createInstance(Object obj, String name)
  {
    return createInstance(obj, obj.getClass(), name);
  }

  public AbstractTypeInstance createInstance(Object obj, Type type, String name)
  {
    Class clazz = obj.getClass();
    TypeDefinition typeDef = definitionCreator.createDefinition(type);
    
    if (typeDef.isSimpleType())
    {
      return new SimpleTypeInstance(name, typeDef.getSimpleTypeDef(), obj);
    }
    else
    {
      ComplexTypeDefinition complexTypeDef = typeDef.getComplexTypeDef();
      ComplexTypeInstance complexTypeInstance = new ComplexTypeInstance(name, complexTypeDef);
      
      if (complexTypeDef.isIterable())
      {
        Iterable iterable = (Iterable)obj;
        for (Object itrObj : iterable)
        {
          complexTypeInstance.addListInstance(createInstance(itrObj, null));
        }
        
      }
      else if (complexTypeDef.isArray())
      {
        for (int i = 0; i < Array.getLength(obj); i++)
        {
          complexTypeInstance.addListInstance(createInstance(Array.get(obj, i), null));
        }
      }
      else
      {
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
      }
      
      return complexTypeInstance;
    }
  }
}
