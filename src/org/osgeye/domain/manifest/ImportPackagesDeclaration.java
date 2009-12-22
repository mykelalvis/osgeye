package org.osgeye.domain.manifest;

import org.osgeye.domain.VersionRange;

public class ImportPackagesDeclaration extends AbstractPackagesDeclaration
{
  private VersionRange version;
  private VersionRange specificationVersion;
  private String bundleSymbolicName;
  private VersionRange bundleVersion;
  private Resolution resolution;
  
  public ImportPackagesDeclaration(String declString)
  {
    super(declString);

    version = attributes.containsKey("version") ? 
        new VersionRange(attributes.get("version")) : new VersionRange();

    specificationVersion = attributes.containsKey("specification-version") ? 
        new VersionRange(attributes.get("specification-version")) : null;    
    
    bundleSymbolicName = attributes.containsKey("bundle-version") ? 
        attributes.get("bundle-version") : null;

    bundleVersion = attributes.containsKey("version") ? 
        new VersionRange(attributes.get("version")) : new VersionRange();

    if (directives.containsKey("resolution"))
    {
      resolution = directives.get("resolution").equals("optional") 
        ? Resolution.OPTIONAL : Resolution.MANDATORY;
    }
    else
    {
      resolution = Resolution.MANDATORY;
    }
  }

  public VersionRange getVersion()
  {
    return version;
  }

  public VersionRange getSpecificationVersion()
  {
    return specificationVersion;
  }  
  
  public Resolution getResolution()
  {
    return resolution;
  }

  public String getBundleSymbolicName()
  {
    return bundleSymbolicName;
  }

  public VersionRange getBundleVersion()
  {
    return bundleVersion;
  }


}
