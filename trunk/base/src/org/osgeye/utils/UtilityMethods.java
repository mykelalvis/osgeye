package org.osgeye.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods used throughout the project.
 * 
 * @author Corey Baswell
 * @since 1.0
 */
public class UtilityMethods
{
  /**
   * 
   * @param obj1
   * @param obj2
   * @return true if both objects are null or both objects are equal (using equals method). false otherwise.
   */
  static public boolean nullEquals(Object obj1, Object obj2)
  {
    if ((obj1 == null) && (obj2 == null))
    {
      return true;
    }
    else if (obj1 == null)
    {
      return false;
    }
    else
    {
      return obj1.equals(obj2);
    }
  }

  /**
   * 
   * @param ch
   * @param text
   * @return the number of times ch is in text.
   */
  static public int countChars(char ch, String text)
  {
    int num = 0;
    char[] chars = text.toCharArray();
    for (char charCompare : chars)
    {
      if (charCompare == ch) ++num;
    }
    return num;
  }

  /**
   * 
   * @param text
   * @return true if text is null or an empty string. false otherwise.
   */
  static public boolean nullEmpty(String text)
  {
    return ((text == null) || (text.length() == 0));
  }

  /**
   * Converts the given type list into a array.
   * 
   * @param <T>
   * @param list
   * @param clazz
   * @return The list converted to an array.
   */
  static public <T> T[] toArray(List<T> list, Class<T> clazz)
  {
    T[] array = (T[])Array.newInstance(clazz, list.size());
    for (int i = 0; i < list.size(); i++)
    {
      array[i] = list.get(i);
    }
    return array;
  }
  
  /**
   * Merges all given arrays into a single array.
   * 
   * @param arrays
   * @return a merge of all the given arrays or an empty array if arrays is null; 
   */
  static public String[] merge(String[]...arrays)
  {
    if (arrays == null) return new String[0];
    
    List<String> mergeList = new ArrayList<String>();
    for (String[] array: arrays)
    {
      for (String value : array)
      {
        mergeList.add(value);
      }
    }
    
    return toArray(mergeList, String.class);
  }
}
