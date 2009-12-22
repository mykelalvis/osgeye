package org.osgeye.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Collection of IO related utilities used throughout the project.
 * 
 * @author Corey Baswell
 * @since 1.0
 */
public class IOUtils
{
  static public String getContentAsString(String classpathRelativePath) throws IOException
  {
    return new String(getContents(IOUtils.class.getResourceAsStream(classpathRelativePath)));
  }
  
  /**
   * Reads in the resource for the given url and returns the contents as String.
   * 
   * @param url The URL resource to load.
   * @return String contents of the given url resource.
   * @throws IOException If the resource cannot be loaded or read from.
   */
  static public String getContentsAsString(URL url) throws IOException
  {
    return new String(getContents(url.openStream()));
  }
  
  /**
   * Reads in the give file and returns the contents as a String.
   * 
   * @param file The file resource to load.
   * @return String contents of the given file resource.
   * @throws IOException If the file cannot be loaded or read from.
   */
  static public String getContentsAsString(File file) throws IOException
  {
    return new String(getContents(file));
  }

  /**
   * Reads in the give file and returns the contents as a Byte array.
   * 
   * @param file The file resource to load.
   * @return Byte array contents of the given file resource.
   * @throws IOException If the file cannot be loaded or read from.
   */
  static public byte[] getContents(File file) throws IOException
  {
    return getContents(new FileInputStream(file));
  }
  
  static public byte[] getContents(InputStream is) throws IOException
  {
    try
    {
      ByteArrayList byteArrayList = new ByteArrayList();
      byte[] buffer = new byte[1024];
      int read = is.read(buffer);
      while (read != -1)
      {
        byteArrayList.add(buffer, 0, read);
        read = is.read(buffer);
      }
      return byteArrayList.getArray();
    }
    finally
    {
      try
      {
        is.close();
      }
      catch (Exception exc)
      {}
    }
  }
  
  /**
   * Writes the given text String to the specified file.
   * @param text The contents to write.
   * @param file The file resource to write to.
   * @throws IOException If the given file cannot be written to.
   */
  static public void writeToFile(String text, File file) throws IOException
  {
    writeToFile(text.getBytes(), file);
  }

  /**
   * Writes the given byte array to the specified file.
   * @param bytes The contents to write.
   * @param file The file resource to write to.
   * @throws IOException If the given file cannot be written to.
   */
  static public void writeToFile(byte[] bytes, File file) throws IOException
  {
    FileOutputStream fos = new FileOutputStream(file);
    try
    {
      fos.write(bytes);
    }
    finally
    {
      try
      {
        fos.close();
      }
      catch (IOException ioexc)
      {}
    }
  }
}
