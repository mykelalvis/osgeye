package org.osgeye.remotereflect;

import java.util.ArrayList;
import java.util.List;

public class ComplexTypeInstance extends AbstractTypeInstance
{
  private ComplexTypeDefinition definition;
  
  private List<AbstractTypeInstance> listInstances;
  
  private List<SimpleTypeInstance> simpleTypeFields;
  
  private List<ComplexTypeInstance> complexTypeFields;

  public ComplexTypeInstance(ComplexTypeDefinition definition)
  {
    this(null, definition);
  }

  public ComplexTypeInstance(String name, ComplexTypeDefinition definition)
  {
    super(name);
    this.definition = definition;
    
    listInstances = new ArrayList<AbstractTypeInstance>();
    simpleTypeFields = new ArrayList<SimpleTypeInstance>();
    complexTypeFields = new ArrayList<ComplexTypeInstance>();
  }
  
  public ComplexTypeDefinition getDefinition()
  {
    return definition;
  }
  
  public void addListInstance(AbstractTypeInstance typeInstance)
  {
    listInstances.add(typeInstance);
  }

  public List<AbstractTypeInstance> getListInstances()
  {
    return listInstances;
  }

  public void addSimpleTypeField(SimpleTypeInstance simpleTypeInstance)
  {
    simpleTypeFields.add(simpleTypeInstance);
  }
  
  public List<SimpleTypeInstance> getSimpleTypeField()
  {
    return simpleTypeFields;
  }
  
  public void addComplexTypeField(ComplexTypeInstance complexTypeInstance)
  {
    complexTypeFields.add(complexTypeInstance);
  }
  
  public List<ComplexTypeInstance> getComplexTypeField()
  {
    return complexTypeFields;
  } 
}
