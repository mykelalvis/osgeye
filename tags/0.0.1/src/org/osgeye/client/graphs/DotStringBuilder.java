package org.osgeye.client.graphs;

import java.util.List;

import org.osgeye.utils.Pair;

public class DotStringBuilder
{
  private StringBuilder builder;
  
  public DotStringBuilder()
  {
    builder = new StringBuilder();
  }
  
  public DotStringBuilder append(Object text)
  {
    builder.append(text);
    return this;
  }
  
  public DotStringBuilder appendLine(Object line)
  {
    builder.append(line).append("\n");
    return this;
  }
  
  public DotStringBuilder appendDigraphOpening(Object graphId)
  {
    builder.append("digraph ").append(graphId).append(" {\n");
    return this;
  }
  
  public DotStringBuilder appendStatement(Object statement)
  {
    builder.append(indent()).append(statement).append(";\n");
    return this;
  }
  
  public DotStringBuilder appendEdge(Object source, Object destination)
  {
    builder.append(indent()).append(source).append(" -> ").append(destination).append(";\n");
    return this;
  }
  
  public DotStringBuilder appendDeclaration(Object id, Pair ...parameters)
  {
    builder.append(indent()).append(id);
    if ((parameters != null) && (parameters.length > 0))
    {
      builder.append(" [");
      for (int i = 0; i < parameters.length; i++)
      {
        Pair parameter = parameters[i];
        if (i != 0)  builder.append(",");
        builder.append(parameter.x).append("=\"").append(parameter.y).append('"');
      }
      builder.append("]");
    }
    builder.append(";\n");
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public DotStringBuilder appendSameRank(List ids)
  {
    builder.append("{rank=same; ");
    for (int i = 0; i < ids.size(); i++)
    {
      if (i != 0) builder.append(" ");
      builder.append(ids.get(i));
    }
    builder.append(" }");
    return this;
  }
  
  public void closeGraph()
  {
    builder.append("}");
  }
  
  public String toString()
  {
    return builder.toString();
  }

  private String indent()
  {
    return indents(1);
  }

  private String indents(int numIndents)
  {
    final int indentSize = 4;
    String indent = "";
    for (int i = 0; i < numIndents; i++)
    {
      for (int j = 0; j < indentSize; j++)
      {
        indent += " ";
      }
    }
    return indent;
  }
}
