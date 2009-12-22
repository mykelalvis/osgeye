package org.osgeye.console.commands;

import static java.lang.System.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class CommandPrinter
{
  static public final int INDENT_SPACES = 4;
  
  private int indentLevel;

  private Writer currentWriter;
  private OutputStreamWriter sysOutWriter;
  private BufferedWriter fileWriter;
  
  public CommandPrinter()
  {
    currentWriter = sysOutWriter = new OutputStreamWriter(out);
  }
  
  public void writeToFile(File backingFile) throws IOException 
  {
    currentWriter = fileWriter = new BufferedWriter(new FileWriter(backingFile, true));
  }
  
  public void endCommandSession() throws IOException
  {
    clearIndent();
    currentWriter.flush();
    
    if (currentWriter == fileWriter)
    {
      try
      {
        fileWriter.close();
        fileWriter = null;
      }
      finally
      {
        currentWriter = sysOutWriter;
      }
    }
  }
  
  public void pushIndent()
  {
    ++indentLevel;
  }
  
  public void popIndent()
  {
    indentLevel = Math.max(0, (indentLevel -1));
  }
  
  public void clearIndent()
  {
    indentLevel = 0;
  }
  
  public void println()
  {
    println("");
  }
  
  public void println(Object text)
  {
    printIndent();
    writeLn(text);
  }
  
  public void printFixedSpaceLn(Object label, int minSpace, Object value)
  {
    printIndent();
    String labelStr = label.toString();
    write(labelStr);
    for (int i = (minSpace - labelStr.length()); i >= 0; i--)
    {
      write(' ');
    }
    writeLn(value);
  }
  
  private void printIndent()
  {
    for (int i = 0; i < (indentLevel * INDENT_SPACES); i++) write(' ');
  }
  
  private void write(char c)
  {
    try
    {
      currentWriter.write(c);
      currentWriter.flush();
    }
    catch (IOException ioexc)
    {
      throw new RuntimeException(ioexc);
    }
  }

  private void write(String str)
  {
    try
    {
      currentWriter.write(str);
      currentWriter.flush();
    }
    catch (IOException ioexc)
    {
      throw new RuntimeException(ioexc);
    }
  }

  private void writeLn(Object object)
  {
    try
    {
      String text = (object ==  null) ? "null" : object.toString();
      currentWriter.write(text + "\n");
      currentWriter.flush();
    }
    catch (IOException ioexc)
    {
      throw new RuntimeException(ioexc);
    }
  }
}
