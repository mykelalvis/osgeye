package org.osgeye.domain;

import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Resolution;

/**
 * An imported package and its associated import declaration.
 * 
 * @author Corey Baswell
 */
public class ImportedPackage
{
  private ImportPackagesDeclaration declaration;
  
  private String packge;
  
  public ImportedPackage(ImportPackagesDeclaration declaration, String packge)
  {
    this.declaration = declaration;
    this.packge = packge;
  }

  public ImportPackagesDeclaration getDeclaration()
  {
    return declaration;
  }

  public String getPackage()
  {
    return packge;
  }
  
  @Override
  public String toString()
  {
    return packge + " " + declaration.getVersion()  + ((declaration.getResolution() == Resolution.OPTIONAL) ? " ?" : "");
  }
}
