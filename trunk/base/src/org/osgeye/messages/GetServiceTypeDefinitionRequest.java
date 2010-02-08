package org.osgeye.messages;

import org.osgeye.domain.ServiceClass;

public class GetServiceTypeDefinitionRequest extends AbstractMessage
{
  static private final long serialVersionUID = 8090170885956791197L;

  private ServiceClass serviceClass;
  
  public GetServiceTypeDefinitionRequest(ServiceClass serviceClass)
  {
    this.serviceClass = serviceClass;
  }

  public ServiceClass getServiceClass()
  {
    return serviceClass;
  }
}
