package org.osgeye.domain.manifest;

import org.osgeye.domain.Version;

public class FragmentHost extends AbstractDeclaration
{
  private String bundleSymbolicName;
  private Version bundleVersion;

  public FragmentHost(String declaration)
  {
    super(declaration);
    
    bundleSymbolicName = names.get(0);

    bundleVersion = attributes.containsKey("bundle-version") ? 
        new Version(attributes.get("bundle-version")) : new Version();
  }

  public String getBundleSymbolicName()
  {
    return bundleSymbolicName;
  }

  public Version getBundleVersion()
  {
    return bundleVersion;
  }
}
