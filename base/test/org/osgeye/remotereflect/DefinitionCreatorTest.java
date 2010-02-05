package org.osgeye.remotereflect;

import static org.testng.Assert.*;

import java.util.List;

import org.testng.annotations.Test;

public class DefinitionCreatorTest
{
  @Test
  public void createComplexTest()
  {
    DefinitionCreator defCreator = new DefinitionCreator();
    TypeDefinition typeDef = defCreator.createDefinition(SampleA.class);
    
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
  }
}
