package org.osgeye.messages;

public class GetConfigurationsRequest extends AbstractMessage
{
  static private final long serialVersionUID = 1405282634382127594L;

  private String filter;
  
  public GetConfigurationsRequest(String filter)
  {
    this.filter = filter;
  }

  public String getFilter()
  {
    return filter;
  }
}
