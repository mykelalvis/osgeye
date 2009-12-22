package org.osgeye.utils;

import java.util.ArrayList;
import java.util.List;

public class ByteArrayList
{
  private List<Byte> bytesList;
  
  public ByteArrayList()
  {
    bytesList = new ArrayList<Byte>();
  }
  
  public void add(byte[] bytes)
  {
    add(bytes, 0, bytes.length);
  }
  
  public void add(byte[] bytes, int offset, int length)
  {
    for (int i = offset; i < (offset + length); i++)
    {
      bytesList.add(bytes[i]);
    }
  }
  
  public byte[] getArray()
  {
    byte[] bytes = new byte[bytesList.size()];
    for (int i = 0; i < bytesList.size(); i++)
    {
      bytes[i] = bytesList.get(i);
    }
    
    return bytes;
  }
}
