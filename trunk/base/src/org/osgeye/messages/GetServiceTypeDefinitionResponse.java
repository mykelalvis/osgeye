package org.osgeye.messages;

import org.osgeye.domain.ServiceClass;
import org.osgeye.remotereflect.TypeDefinition;

public class GetServiceTypeDefinitionResponse extends AbstractMessage
{
  static private final long serialVersionUID = -8154208513710716577L;

  private ServiceClass serviceClass;
  
  private TypeDefinition typeDefinition;
  
  public GetServiceTypeDefinitionResponse(String messageId, ServiceClass serviceClass, TypeDefinition typeDefinition)
  {
    super(messageId);
    this.serviceClass = serviceClass;
    this.typeDefinition = typeDefinition;
  }

  public ServiceClass getServiceClass()
  {
    return serviceClass;
  }

  public TypeDefinition getTypeDefinition()
  {
    return typeDefinition;
  }
}
