package org.osgeye.domain.manifest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AbstractDeclaration
{
  protected String declaration;
  protected List<String> names;
  protected Map<String, String> directives;
  protected Map<String, String > attributes;

  protected AbstractDeclaration(String declaration)
  {
    this.declaration = declaration;
    
    String[] values = declaration.split(";");
    
    names = new ArrayList<String>();
    directives = new HashMap<String, String>();
    attributes = new HashMap<String, String>();
    
    for (String value : values)
    {
      if (value.contains(":="))
      {
        String[] directive = value.split(":=");
        if (directive.length != 2)
        {
          throw new IllegalArgumentException("Invalid directive for packages declaration " + declaration);
        }
        else
        {
          directives.put(directive[0], stripQuotes(directive[1]));
        }
      }
      else if (value.contains("="))
      {
        String[] attribute = value.split("=");
        if (attribute.length != 2)
        {
          throw new IllegalArgumentException("Invalid attribute for packages declaration " + declaration);
        }
        else
        {
          attributes.put(attribute[0], stripQuotes(attribute[1]));
        }
      }
      else
      {
        names.add(value);
      }
    }
  }
  
  public String toString()
  {
    return declaration;
  }
  
  public String getDirective(String name)
  {
    return directives.get(name);
  }
  
  public String getAttribute(String name)
  {
    return attributes.get(name);
  }
  
  protected String stripQuotes(String text)
  {
    if (text.charAt(0) == '"')
    {
      text = text.substring(1, text.length());
    }
    
    if (text.charAt(text.length() - 1) == '"')
    {
      text = text.substring(0, (text.length() - 1));
    }
    
    return text;
  }
}
