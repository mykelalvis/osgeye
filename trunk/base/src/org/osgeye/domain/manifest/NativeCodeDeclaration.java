package org.osgeye.domain.manifest;

import java.util.List;

public class NativeCodeDeclaration extends AbstractDeclaration
{
  private String osName;
  
  private String osVersion;
  
  private String processor;
  
  private String language;
  
  private String selectionFilter;

  public NativeCodeDeclaration(String declaration)
  {
    super(declaration);
    
    osName = attributes.get("osname");
    osVersion = attributes.get("osversion");
    processor = attributes.get("processor");
    language = attributes.get("language");
    selectionFilter = attributes.get("selection-filter");
  }
  
  public List<String> getPaths()
  {
    return names;
  }

  public String getOsName()
  {
    return osName;
  }

  public String getOsVersion()
  {
    return osVersion;
  }

  public String getProcessor()
  {
    return processor;
  }

  public String getLanguage()
  {
    return language;
  }

  public String getSelectionFilter()
  {
    return selectionFilter;
  }
}
