package org.osgeye.remotereflect;

import static org.testng.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class MethodInvokerTest
{
  private MethodInvoker methodInvoker;
  
  public MethodInvokerTest()
  {
    methodInvoker = new MethodInvoker();
  }
  
  @Test
  public void testOne() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
    InvokeTestA invokeTest = new InvokeTestA();
    
    AbstractTypeInstance returnVal = methodInvoker.invokeMethod(invokeTest, "testOne", new ArrayList<SimpleTypeInstance>());
    
    assertNull(returnVal);
    assertEquals(invokeTest.methodCalled, 1);
    
    List<SimpleTypeInstance> parameterInstances = new ArrayList<SimpleTypeInstance>();
    parameterInstances.add(new SimpleTypeInstance(SimpleTypeDefinition.STRING, "1"));
    parameterInstances.add(new SimpleTypeInstance(SimpleTypeDefinition.INTEGER, 2));
    
    returnVal = methodInvoker.invokeMethod(invokeTest, "testOne", parameterInstances);
    
    assertNull(returnVal);
    assertEquals(invokeTest.methodCalled, 2);
    
    returnVal = methodInvoker.invokeMethod(invokeTest, "testTwo", new ArrayList<SimpleTypeInstance>());
    assertNotNull(returnVal);
    assertTrue(returnVal instanceof ComplexTypeInstance);
    ComplexTypeInstance returnComplexVal = (ComplexTypeInstance)returnVal;
    ComplexTypeDefinition complexTypeDef = returnComplexVal.getDefinition();
    
    assertTrue(complexTypeDef.isIterable());
    assertNotNull(complexTypeDef.getContainerType());
    TypeDefinition containerReturnType = complexTypeDef.getContainerType();
    
    assertTrue(containerReturnType.isSimpleType());
    assertFalse(containerReturnType.isComplexType());
    
    assertEquals(containerReturnType.getSimpleTypeDef(), SimpleTypeDefinition.STRING);
  }
}
