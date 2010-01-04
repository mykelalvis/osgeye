package org.osgeye.domain.manifest;

import org.osgeye.domain.VersionRange;

public class RequireBundleDeclaration extends AbstractDeclaration
{
  private String symbolicName;
  
  private Visibility visibility;
  
  private Resolution resolution;
  
  private VersionRange bundleVersion;
  
  public RequireBundleDeclaration(String declaration)
  {
    super(declaration);
    
    symbolicName = (names.size() > 0) ? names.get(0) : "N/A";
    
    if (directives.containsKey("visibility"))
    {
      visibility = directives.get("visibility").equals("reexport") ? Visibility.REEXPORT : Visibility.PRIVATE;
    }
    else
    {
      visibility = Visibility.PRIVATE;
    }
    
    
    if (directives.containsKey("resolution"))
    {
      resolution = directives.get("resolution").equals("optional") 
        ? Resolution.OPTIONAL : Resolution.MANDATORY;
    }
    else
    {
      resolution = Resolution.MANDATORY;
    }
    
    bundleVersion = attributes.containsKey("bundle-version") ? 
        new VersionRange(attributes.get("bundle-version")) : new VersionRange();
  }

  public String getSymbolicName()
  {
    return symbolicName;
  }

  public Visibility getVisibility()
  {
    return visibility;
  }

  public Resolution getResolution()
  {
    return resolution;
  }

  public VersionRange getBundleVersion()
  {
    return bundleVersion;
  }
}
