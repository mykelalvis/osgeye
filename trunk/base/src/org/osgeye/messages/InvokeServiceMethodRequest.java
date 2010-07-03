package org.osgeye.messages;

import java.util.List;

import org.osgeye.domain.ServiceClass;
import org.osgeye.remotereflect.SimpleTypeInstance;

public class InvokeServiceMethodRequest extends AbstractMessage
{
  static private final long serialVersionUID = -1937803648172959155L;

  private ServiceClass serviceClass;
  
  private String methodName;
  
  private List<SimpleTypeInstance> parameters;

  public InvokeServiceMethodRequest(ServiceClass serviceClass, String methodName, List<SimpleTypeInstance> parameters)
  {
    this.serviceClass = serviceClass;
    this.methodName = methodName;
    this.parameters = parameters;
  }

  public ServiceClass getServiceClass()
  {
    return serviceClass;
  }

  public String getMethodName()
  {
    return methodName;
  }

  public List<SimpleTypeInstance> getParameters()
  {
    return parameters;
  }
}
