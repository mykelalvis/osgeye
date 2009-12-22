package org.osgeye.messages;


public class ExceptionResponse extends AbstractMessage
{
  static private final long serialVersionUID = -8099108539353887868L;
  
  private String errorMessage;

  private StackTraceElement[] stackTraceElements;

  public ExceptionResponse(String messageId, String errorMessage)
  {
    super(messageId);
    this.errorMessage = errorMessage;
  }

  public ExceptionResponse(String messageId, Exception cause)
  {
    super(messageId);
    
    errorMessage = cause.getMessage();
    stackTraceElements = cause.getStackTrace();
  }

  public ExceptionResponse(String messageId, String errorMessage, Exception cause)
  {
    super(messageId);
    
    this.errorMessage = errorMessage;
    stackTraceElements = cause.getStackTrace();
  }

  public String getErrorMessage()
  {
    return errorMessage;
  }

  public StackTraceElement[] getStackTraceElements()
  {
    return stackTraceElements;
  }
}
