package org.osgeye.domain.manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgeye.domain.Version;

public class ExportPackagesDeclaration extends AbstractPackagesDeclaration
{
  private Version version;
  private Version specificationVersion;
  private List<String> uses;
  private List<String> mandatoryAttributes;
  private List<String> includeClasses;
  private List<String> excludeClasses;
  
  public ExportPackagesDeclaration(String declarationStr)
  {
    super(declarationStr);

    version = attributes.containsKey("version") ? 
        new Version(attributes.get("version")) : new Version();
        
    specificationVersion = attributes.containsKey("specification-version") ? 
        new Version(attributes.get("specification-version")) : null;

    uses = directives.containsKey("uses") 
      ? Arrays.asList(directives.get("uses").split(",")) : new ArrayList<String>();

    mandatoryAttributes = directives.containsKey("mandatory") 
      ? Arrays.asList(directives.get("mandatory").split(",")) : new ArrayList<String>();

    includeClasses = directives.containsKey("include") 
      ? Arrays.asList(directives.get("include").split(",")) : new ArrayList<String>();

    excludeClasses = directives.containsKey("exclude") 
      ? Arrays.asList(directives.get("exclude").split(",")) : new ArrayList<String>();
  }

  public Version getVersion()
  {
    return version;
  }
  
  public Version getSpecificationVersion()
  {
    return specificationVersion;
  }

  public List<String> getUses()
  {
    return uses;
  }

  public List<String> getMandatoryAttributes()
  {
    return mandatoryAttributes;
  }

  public List<String> getIncludeClasses()
  {
    return includeClasses;
  }

  public List<String> getExcludeClasses()
  {
    return excludeClasses;
  }
}
