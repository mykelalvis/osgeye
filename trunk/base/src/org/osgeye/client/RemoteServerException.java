package org.osgeye.client;

import org.osgeye.messages.ExceptionResponse;

/**
 * Indicator that something unexpected (and bad) happened on the server side.
 * 
 */
public class RemoteServerException extends RuntimeException
{
  
  public RemoteServerException(String message)
  {
    super(message);
  }

  public RemoteServerException(ExceptionResponse response)
  {
    super(response.getErrorMessage());
    setStackTrace(response.getStackTraceElements());
  }
}
