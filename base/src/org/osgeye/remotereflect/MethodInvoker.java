package org.osgeye.remotereflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MethodInvoker
{
  private DefinitionCreator definitionCreator;
  private InstanceCreator instanceCreator;
  
  public MethodInvoker()
  {
    definitionCreator = new DefinitionCreator();
    instanceCreator = new InstanceCreator();
  }
  
  /**
   * Invokes the method with the given method name and given list of simple parameters on the given object.
   * 
   * @param object The object to invoke the method on.
   * @param methodName The method name to invoke.
   * @param parameterInstances The list of simple parameters that will be converted to native objects used in the method invoke. If not parameters are used this should an empty list.
   * @return The converted type instance of the returned method's object or null if this method is defined as null.
   * @throws IllegalArgumentException If a matching method can be found on the given object that matches the method mame and parameter instances.
   * @throws IllegalAccessException If the method can be invoked.
   * @throws InvocationTargetException If the method invoke throws an exception.
   */
  public AbstractTypeInstance invokeMethod(Object object, String methodName, List<SimpleTypeInstance> parameterInstances) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
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
    
    if (returnVal == null)
    {
      return null;
    }
    else
    {
      return instanceCreator.createInstance(returnVal, matchedMethod.getGenericReturnType(), null);
    }
  }
}
 