package org.osgeye;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtilities
{
  static public InputStream getFileAsStream(String classpathRelativePath)
  {
    return TestUtilities.class.getResourceAsStream(classpathRelativePath);
  }
  
  static public String getFileAsString(String classpathRelativePath)
  {
    InputStream is = getFileAsStream(classpathRelativePath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    
    try
    {
      String text = "";
      String line = reader.readLine();
      while (line != null)
      {
        text += line + "\n";
        line = reader.readLine();
      }
      
      return text;
    }
    catch (IOException ioexc)
    {
      throw new RuntimeException(ioexc);
    }
  }  
}
