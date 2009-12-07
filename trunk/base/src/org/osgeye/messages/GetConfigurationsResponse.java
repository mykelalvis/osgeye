package org.osgeye.messages;

import java.util.List;

import org.osgeye.domain.Configuration;

public class GetConfigurationsResponse extends AbstractMessage
{
  static private final long serialVersionUID = 4118182198005565872L;

  private List<Configuration> configurations;
  
  public GetConfigurationsResponse(String messageId, List<Configuration> configurations)
  {
    super(messageId);
    this.configurations = configurations;
  }

  public List<Configuration> getConfigurations()
  {
    return configurations;
  }
}
