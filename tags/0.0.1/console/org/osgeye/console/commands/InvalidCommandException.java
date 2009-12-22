package org.osgeye.console.commands;

public class InvalidCommandException extends Exception
{
  public InvalidCommandException(String message)
  {
    super(message);
  }
}
