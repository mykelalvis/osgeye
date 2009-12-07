package org.osgeye.domain.manifest;

import java.util.List;

abstract public class AbstractPackagesDeclaration extends AbstractDeclaration
{
  protected AbstractPackagesDeclaration(String declarationStr)
  {
    super(declarationStr);
  }
  
  public List<String> getPackages()
  {
    return names;
  }
}
