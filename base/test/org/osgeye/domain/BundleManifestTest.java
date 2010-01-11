package org.osgeye.domain;

import static org.osgeye.TestUtilities.*;
import static org.testng.Assert.*;

import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.domain.manifest.Manifest;
import org.osgeye.domain.manifest.RequireBundleDeclaration;
import org.testng.annotations.Test;

public class BundleManifestTest
{
  public void testOne()
  {
    String manifestFile = getFileAsString("/fixtures/manifest1.mf");
    Manifest manifest = new Manifest(manifestFile);
    
    assertEquals(manifest.getVersion(), new Version(3, 3, 1, "GA"));
    assertEquals(manifest.getSymbolicName(), "com.springsource.org.hibernate");
    assertEquals(manifest.getVendor(), "SpringSource");
    assertTrue(manifest.getExportDeclarations().size() > 0);
    assertTrue(manifest.getImportDeclarations().size() > 0);
    
    assertEquals(manifest.getFile(), manifestFile);
  }
  
  @Test
  public void testTwo()
  {
    String manifestFile = getFileAsString("/fixtures/manifest2.mf");
    Manifest manifest = new Manifest(manifestFile);
    
    for (RequireBundleDeclaration ipd : manifest.getRequireBundleDeclarations())
    {
      System.out.println(ipd.getSymbolicName() + " " + ipd.getResolution() + " " + ipd.getVisibility());
    }
    
  }
}
