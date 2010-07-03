package org.osgeye.messages;

import org.osgeye.remotereflect.AbstractTypeInstance;

public class InvokeServiceMethodResponse extends AbstractMessage
{
  static private final long serialVersionUID = 762112726600747395L;

  private AbstractTypeInstance returnType;
  
  public InvokeServiceMethodResponse(String messageId, AbstractTypeInstance returnType)
  {
    super(messageId);
    this.returnType = returnType;
  }

  public AbstractTypeInstance getReturnType()
  {
    return returnType;
  }
}
