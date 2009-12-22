package org.osgeye.messages;

public class LoginResponse extends AbstractMessage
{
  static private final long serialVersionUID = 3870083830039627229L;

  private boolean success;
  private String clientId;
  private String errorMessage;
  
  public LoginResponse(String messageId, boolean success, String clientId, String errorMessage)
  {
    super(messageId);
    
    this.success = success;
    this.clientId = clientId;
    this.errorMessage = errorMessage;
  }

  public boolean isSuccess()
  {
    return success;
  }

  public String getClientId()
  {
    return clientId;
  }

  public String getErrorMessage()
  {
    return errorMessage;
  }
}
