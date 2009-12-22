package org.osgeye.domain;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class VersionRangeTest
{
  @Test
  public void testWithinRange()
  {
    VersionRange range = new VersionRange("[2.5.6, 3.0.0)");
    Version version = new Version(2, 5, 6, "A");

    assertTrue(range.isWithinRange(version));
    
    range = new VersionRange("(2.5.6, 3.0.0)");
    assertTrue(range.isWithinRange(version));

  }
}
