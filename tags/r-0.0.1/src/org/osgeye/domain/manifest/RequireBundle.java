package org.osgeye.domain.manifest;

import java.util.List;

import org.osgeye.domain.Version;

public class RequireBundle extends AbstractDeclaration
{
  static public enum Visibility
  {
    PRIVATE,
    RE_EXPORT;
  }
  
  private Visibility visibility;
  private Version bundleVersion;
  private Resolution resolution;
  
  public RequireBundle(String declaration)
  {
    super(declaration);
    
    if (directives.containsKey("visibility"))
    {
      visibility = directives.get("visibility").equals("reexport") 
        ? Visibility.RE_EXPORT : Visibility.PRIVATE;
    }
    else
    {
      visibility = Visibility.PRIVATE;
    }
    
    bundleVersion = attributes.containsKey("bundle-version") ? 
        new Version(attributes.get("bundle-version")) : new Version();    
        
        
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
  
  public List<String> getSymbolicNames()
  {
    return names;
  }

  public Visibility getVisibility()
  {
    return visibility;
  }

  public Version getBundleVersion()
  {
    return bundleVersion;
  }

  public Resolution getResolution()
  {
    return resolution;
  }
}
