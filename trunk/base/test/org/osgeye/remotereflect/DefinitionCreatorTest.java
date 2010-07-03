package org.osgeye.remotereflect;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class DefinitionCreatorTest
{
  private DefinitionCreator defCreator = new DefinitionCreator();

  @Test
  public void complexDefCreateOne()
  {
    TypeDefinition typeDef = defCreator.createDefinition(SampleA.class);
    basicSampleAValidation(typeDef);

    ComplexTypeDefinition complexTypeDef = typeDef.getComplexTypeDef();
    
    List<FieldDefinition> fieldDefs = complexTypeDef.getFields();
    
    List<MethodDefinition> methodDefs = complexTypeDef.getMethods();
    
    for (MethodDefinition methodDef : methodDefs)
    {
      String methodName = methodDef.getName();

      if (methodName.equals("getOne"))
      {
        assertEquals(methodDef.getParameters().size(), 0);
        TypeDefinition returnType = methodDef.getReturnType();
        assertTrue(returnType.isSimpleType());
        assertFalse(returnType.isComplexType());
        assertNull(returnType.getComplexTypeDef());
        assertEquals(returnType.getSimpleTypeDef(), SimpleTypeDefinition.STRING);
      }
      else if (methodName.equals("setOne"))
      {
        assertNull(methodDef.getReturnType());
        List<TypeDefinition> parameters = methodDef.getParameters();
        assertEquals(parameters.size(), 1);
        TypeDefinition parameter = parameters.get(0);
        assertFalse(parameter.isComplexType());
        assertNull(parameter.getComplexTypeDef());
        assertEquals(parameter.getSimpleTypeDef(), SimpleTypeDefinition.STRING);
        
      }
      else if (methodName.equals("getTwo"))
      {
        assertEquals(methodDef.getParameters().size(), 0);
        TypeDefinition returnType = methodDef.getReturnType();
        assertTrue(returnType.isSimpleType());
        assertFalse(returnType.isComplexType());
        assertNull(returnType.getComplexTypeDef());
        assertEquals(returnType.getSimpleTypeDef(), SimpleTypeDefinition.INTEGER);
      }
      else if (methodName.equals("setTwo"))
      {
        assertNull(methodDef.getReturnType());
        List<TypeDefinition> parameters = methodDef.getParameters();
        assertEquals(parameters.size(), 1);
        TypeDefinition parameter = parameters.get(0);
        assertFalse(parameter.isComplexType());
        assertNull(parameter.getComplexTypeDef());
        assertEquals(parameter.getSimpleTypeDef(), SimpleTypeDefinition.INTEGER);
      }
      else if (methodName.equals("getThree"))
      {
        assertEquals(methodDef.getParameters().size(), 0);
        TypeDefinition returnType = methodDef.getReturnType();
        assertTrue(returnType.isSimpleType());
        assertFalse(returnType.isComplexType());
        assertNull(returnType.getComplexTypeDef());
        assertEquals(returnType.getSimpleTypeDef(), SimpleTypeDefinition.LONG);
      }
      else if (methodName.equals("setThree"))
      {
        assertNull(methodDef.getReturnType());
        List<TypeDefinition> parameters = methodDef.getParameters();
        assertEquals(parameters.size(), 1);
        TypeDefinition parameter = parameters.get(0);
        assertFalse(parameter.isComplexType());
        assertNull(parameter.getComplexTypeDef());
        assertEquals(parameter.getSimpleTypeDef(), SimpleTypeDefinition.LONG);
      }
      else if (methodName.equals("getFour"))
      {
        assertEquals(methodDef.getParameters().size(), 0);
        TypeDefinition returnType = methodDef.getReturnType();
        assertFalse(returnType.isSimpleType());
        assertTrue(returnType.isComplexType());
        assertNull(returnType.getSimpleTypeDef());
        
        ComplexTypeDefinition returnComplexType = returnType.getComplexTypeDef();
        assertTrue(returnComplexType.isArray());
        assertFalse(returnComplexType.isIterable());
        
        TypeDefinition arrayContainerType = returnComplexType.getContainerType();
        assertTrue(arrayContainerType.isSimpleType());
        assertFalse(arrayContainerType.isComplexType());
        assertEquals(arrayContainerType.getSimpleTypeDef(), SimpleTypeDefinition.STRING);
      }
      else if (methodName.equals("setFour"))
      {
        assertNull(methodDef.getReturnType());
        List<TypeDefinition> parameters = methodDef.getParameters();
        assertEquals(parameters.size(), 1);
        TypeDefinition parameter = parameters.get(0);

        assertFalse(parameter.isSimpleType());
        assertTrue(parameter.isComplexType());
        assertNull(parameter.getSimpleTypeDef());
        
        ComplexTypeDefinition parameterComplexType = parameter.getComplexTypeDef();
        assertTrue(parameterComplexType.isArray());
        assertFalse(parameterComplexType.isIterable());
        
        TypeDefinition arrayContainerType = parameterComplexType.getContainerType();
        assertTrue(arrayContainerType.isSimpleType());
        assertFalse(arrayContainerType.isComplexType());
        assertEquals(arrayContainerType.getSimpleTypeDef(), SimpleTypeDefinition.STRING);
      }
      else if (methodName.equals("getFive"))
      {
        assertEquals(methodDef.getParameters().size(), 0);
        TypeDefinition returnType = methodDef.getReturnType();
        assertFalse(returnType.isSimpleType());
        assertTrue(returnType.isComplexType());
        assertNull(returnType.getSimpleTypeDef());
        
        ComplexTypeDefinition returnComplexType = returnType.getComplexTypeDef();
        assertFalse(returnComplexType.isArray());
        assertTrue(returnComplexType.isIterable());
        
        TypeDefinition arrayContainerType = returnComplexType.getContainerType();
        assertFalse(arrayContainerType.isSimpleType());
        assertTrue(arrayContainerType.isComplexType());

        basicSampleAValidation(arrayContainerType);
      }
      else if (methodName.equals("setFive"))
      {
        assertNull(methodDef.getReturnType());
        List<TypeDefinition> parameters = methodDef.getParameters();
        assertEquals(parameters.size(), 1);
        TypeDefinition parameter = parameters.get(0);

        assertFalse(parameter.isSimpleType());
        assertTrue(parameter.isComplexType());
        assertNull(parameter.getSimpleTypeDef());
        
        ComplexTypeDefinition parameterComplexType = parameter.getComplexTypeDef();
        assertFalse(parameterComplexType.isArray());
        assertTrue(parameterComplexType.isIterable());
        
        TypeDefinition arrayContainerType = parameterComplexType.getContainerType();
        assertFalse(arrayContainerType.isSimpleType());
        assertTrue(arrayContainerType.isComplexType());

        basicSampleAValidation(arrayContainerType);
      }
      else
      {
        fail("Invalid method name: " + methodName);
      }
    }
  }

  
  private void basicSampleAValidation(TypeDefinition typeDef)
  {
    assertEquals(typeDef.getName(), "SampleA");
    assertEquals(typeDef.getPackage(), "org.osgeye.remotereflect");
    assertEquals(typeDef.getQualifiedName(), "org.osgeye.remotereflect.SampleA");
    
    assertFalse(typeDef.isSimpleType());
    assertTrue(typeDef.isComplexType());
    assertFalse(typeDef.isInterface());
    assertNull(typeDef.getSimpleTypeDef());
    
    ComplexTypeDefinition complexTypeDef = typeDef.getComplexTypeDef();
    assertNotNull(complexTypeDef);
    
    assertEquals(complexTypeDef.getName(), "SampleA");
    assertEquals(complexTypeDef.getPackage(), "org.osgeye.remotereflect");
    assertEquals(complexTypeDef.getQualifiedName(), "org.osgeye.remotereflect.SampleA");
    
    List<FieldDefinition> fieldDefs = complexTypeDef.getFields();
    assertEquals(fieldDefs.size(), 5);
    
    List<MethodDefinition> methodDefs = complexTypeDef.getMethods();
    assertEquals(methodDefs.size(), 10);
  }
}
