package org.osgeye.remotereflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MethodExecutor
{
  private DefinitionCreator definitionCreator;
  
  public MethodExecutor()
  {
    definitionCreator = new DefinitionCreator();
  }
  
  public AbstractTypeInstance executeMethod(Object object, String methodName, List<SimpleTypeInstance> parameterInstances) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
    Method[] methods = object.getClass().getDeclaredMethods();

    Method matchedMethod = null;
    METHOD_LOOP: for (Method method : methods)
    {
      if (method.getName().equals(methodName))
      {
        Class[] parameterClasses = method.getParameterTypes();
        
        if (parameterClasses.length == parameterInstances.size())
        {
          for (int i = 0; i < parameterClasses.length; i++)
          {
            Class parameterClass = parameterClasses[i];
            SimpleTypeInstance parameterInstance = parameterInstances.get(i);
            if (!parameterInstance.getDefinition().equalsClass(parameterClass))
            {
              continue METHOD_LOOP;
            }
          }
          matchedMethod = method;
          break;
        }
      }
    }
    
    if (matchedMethod == null)
    {
      throw new IllegalArgumentException("No matching method name: " + methodName 
          + " found for the given object with: " + parameterInstances.size() + " parameters.");
    }
    
    Object[] parameters = new Object[parameterInstances.size()];
    for (int i = 0; i < parameterInstances.size(); i++)
    {
      parameters[i] = parameterInstances.get(i).getObject();
    }
    
    Object returnVal = matchedMethod.invoke(object, parameters);
    TypeDefinition returnTypeDef = definitionCreator.createDefinition(returnVal.getClass());
    
    if (returnTypeDef.isSimpleType())
    {
      return new SimpleTypeInstance(returnTypeDef.getSimpleTypeDef(), returnVal);
    }
    else
    {
      Class complexClass = returnVal.getClass();
      ComplexTypeDefinition complexTypeDef = returnTypeDef.getComplexTypeDef();
      ComplexTypeInstance complexTypeInstance = new ComplexTypeInstance(complexTypeDef);
      
      for (FieldDefinition fieldDef : complexTypeDef.getFields())
      {
        try
        {
          Field field = complexClass.getDeclaredField(fieldDef.getName());
          
          try
          {
            field.setAccessible(true);
            
          }
          catch (SecurityException sexc)
          {
            
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
          
        }
      }
      
    }
    
  }
}
