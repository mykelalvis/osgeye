package org.osgeye.messages;

public class LoginRequest extends AbstractMessage
{
  static private final long serialVersionUID = -1183988184610921792L;

  private String userName;
  
  private String password;

  public LoginRequest(String userName, String password)
  {
    super();
    
    this.userName = userName;
    this.password = password;
  }

  public String getUserName()
  {
    return userName;
  }

  public String getPassword()
  {
    return password;
  }

}
