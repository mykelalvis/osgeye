package org.osgeye.console.commands;

public enum CommandCategory
{
  DESCRIBES("Descrptions"),
  ACTIONS("Actions"),
  DIAGNOSIS("Diagnosis"),
  MISC("Miscellaneous");
  
  public final String text;
  
  private CommandCategory(String text)
  {
    this.text = text;
  }
}
