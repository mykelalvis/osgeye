package org.osgeye.remotereflect;

import java.util.ArrayList;
import java.util.List;

public class InvokeTestA
{
  
  public int methodCalled;
  
  public void testOne()
  {
    methodCalled = 1;
  }
  
  public void testOne(String one, Integer two)
  {
    if (!"1".equals(one))
    {
      throw new RuntimeException("Invalid parameter value for one: " + one);
    }
    
    if (2 != two)
    {
      throw new RuntimeException("Invalid parameter value for two: " + two);
    }
    
    methodCalled = 2;
  }
  
  public List<String> testTwo()
  {
    List<String> list = new ArrayList<String>();
    list.add("one");
    list.add("two");
    
    return list;
  }
}
