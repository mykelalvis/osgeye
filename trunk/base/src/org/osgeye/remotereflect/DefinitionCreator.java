package org.osgeye.remotereflect;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionCreator
{
  public TypeDefinition createDefinition(Type type)
  {
    return createDefinition(type, new HashMap<String, ComplexTypeDefinition>());
  }
  
  public TypeDefinition createDefinition(Type type, Map<String, ComplexTypeDefinition> complexTypeMap)
  {
    if (type instanceof Class)
    {
      SimpleTypeDefinition simpleType = getSimpleTypeDef((Class)type);
      if (simpleType != null)
      {
        return new TypeDefinition(simpleType);
      }
    }
    
    return new TypeDefinition(createComplexDef(type, complexTypeMap));
  }
  
  ComplexTypeDefinition createComplexDef(Type type, Map<String, ComplexTypeDefinition> complexTypeMap)
  {
    String typeStr = type.toString();
    if (complexTypeMap.containsKey(typeStr))
    {
      return complexTypeMap.get(typeStr);
    }
    
    ComplexTypeDefinition complexTypeDef = new ComplexTypeDefinition();
    complexTypeMap.put(typeStr, complexTypeDef);
    
    Class clazz = null;
    if (type instanceof ParameterizedType)
    {
      ParameterizedType paramType = (ParameterizedType)type;
      clazz = getRawClass(paramType.getRawType());
    }
    else
    {
      clazz = (Class)type;
    }    
    
    complexTypeDef.setName(clazz.getSimpleName());
    if (clazz.getPackage() != null)
    {
      complexTypeDef.setPackage(clazz.getPackage().getName());
    }
    complexTypeDef.setArray(clazz.isArray());
    complexTypeDef.setInterface(clazz.isInterface());
    complexTypeDef.setIterable(implementsIterable(clazz));
    
    if (complexTypeDef.isArray())
    {
      complexTypeDef.setContainerType(createDefinition(clazz.getComponentType(), complexTypeMap));
    }
    else if (complexTypeDef.isIterable())
    {
      if (type instanceof ParameterizedType)
      {
        ParameterizedType paramType = (ParameterizedType)type;
        Type[] genericTypes = paramType.getActualTypeArguments();
        if ((genericTypes != null) && (genericTypes.length > 0))
        {
          complexTypeDef.setContainerType(createDefinition(paramType, complexTypeMap));
        }
      }
    }
    
    Field[] fields = clazz.getFields();
    List<FieldDefinition> fieldDefs = new ArrayList<FieldDefinition>();
    for (Field field : fields)
    {
      if (field.getDeclaringClass() != Object.class)
      {
        String fieldName = field.getName();
        AccessLevel accessLevel = getAccessLevel(field.getModifiers());
        boolean statc = Modifier.isStatic(field.getModifiers());
        Type parameterType = (field.getGenericType() instanceof ParameterizedType) ? field.getGenericType() : field.getType();
        TypeDefinition fieldTypeDef = createDefinition(parameterType, complexTypeMap);
        fieldDefs.add(new FieldDefinition(fieldName, accessLevel, statc, fieldTypeDef));
      }
    }
    complexTypeDef.setFields(fieldDefs);
    
    Method[] methods = clazz.getMethods();
    List<MethodDefinition> methodDefs = new ArrayList<MethodDefinition>();
    for (Method method : methods)
    {
      if (method.getDeclaringClass() != Object.class)
      {
        String methodName = method.getName();
        AccessLevel accesslevel = getAccessLevel(method.getModifiers());
        boolean statc = Modifier.isStatic(method.getModifiers());
        Type returnType = (method.getGenericReturnType() instanceof ParameterizedType) ? method.getGenericReturnType() : method.getReturnType();
        TypeDefinition returnTypeDef = (returnType == void.class) ? null : createDefinition(returnType, complexTypeMap);
        
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Type[] parameterTypes = method.getParameterTypes();
        List<TypeDefinition> parameterDefs = new ArrayList<TypeDefinition>();
        
        for (int i = 0; i < genericParameterTypes.length; i++)
        {
          Type parameterType = (genericParameterTypes[i] instanceof ParameterizedType) ? genericParameterTypes[i] : parameterTypes[i];
          parameterDefs.add(createDefinition(parameterType, complexTypeMap));
        }
        
        methodDefs.add(new MethodDefinition(methodName, accesslevel, statc, parameterDefs, returnTypeDef));
      }
    }

    complexTypeDef.setMethods(methodDefs);
    return complexTypeDef;
  }
  
  SimpleTypeDefinition getSimpleTypeDef(Class clazz)
  {
    if ((clazz == byte.class) || (clazz == Byte.class))
    {
      return SimpleTypeDefinition.BYTE;
    }
    else if ((clazz == boolean.class) || (clazz == Boolean.class))
    {
      return SimpleTypeDefinition.BOOLEAN;
    }
    else if ((clazz == short.class) || (clazz == Short.class))
    {
      return SimpleTypeDefinition.SHORT;
    }
    else if ((clazz == int.class) || (clazz == Integer.class))
    {
      return SimpleTypeDefinition.INTEGER;
    }
    else if ((clazz == long.class) || (clazz == Long.class))
    {
      return SimpleTypeDefinition.LONG;
    }
    else if ((clazz == float.class) || (clazz == Float.class))
    {
      return SimpleTypeDefinition.FLOAT;
    }
    else if ((clazz == double.class) || (clazz == Double.class))
    {
      return SimpleTypeDefinition.DOUBLE;
    }
    else if (clazz == String.class)
    {
      return SimpleTypeDefinition.STRING;
    }
    else if (clazz == Date.class)
    {
      return SimpleTypeDefinition.DATE;
    }
    else
    {
      return null;
    }
  }
  
  boolean implementsIterable(Class clazz)
  {
    Class[] interfaces = clazz.getInterfaces();
    if (interfaces != null)
    {
      for (Class interfce : interfaces)
      {
        if (isIterable(interfce))
        {
          return true;
        }
      }
    }
    
    Class superClass = clazz.getSuperclass();
    if (superClass != null)
    {
      return implementsIterable(superClass);
    }
    else
    {
      return false;
    }
  }
  
  boolean isIterable(Class interfce)
  {
    if (interfce == Iterable.class)
    {
      return true;
    }
    else
    {
      Class superClass = interfce.getSuperclass();
      if (superClass != null)
      {
        return isIterable(superClass);
      }
      else
      {
        return false;
      }
    }
  }
  
  AccessLevel getAccessLevel(int modifiers)
  {
    if (Modifier.isPublic(modifiers))
    {
      return AccessLevel.PUBLIC;
    }
    else if (Modifier.isPrivate(modifiers))
    {
      return AccessLevel.PRIVATE;
    }
    else if (Modifier.isProtected(modifiers))
    {
      return AccessLevel.PROTECTED;
    }
    else
    {
      return AccessLevel.PACKAGE;
    }
  }
  
  Class getRawClass(Type type)
  {
    if (type instanceof ParameterizedType)
    {
      ParameterizedType paramType = (ParameterizedType)type;
      return getRawClass(paramType.getRawType());
    }
    else if (type instanceof TypeVariable)
    {
      TypeVariable typeVar = (TypeVariable)type;
      return getRawClass((Type)typeVar.getGenericDeclaration());
    }
    else if (type instanceof GenericArrayType)
    {
      GenericArrayType genericArrayType = (GenericArrayType)type;
      return getRawClass(genericArrayType.getGenericComponentType());
    }
    else
    {
      return (Class)type;
    }    
  }
}
