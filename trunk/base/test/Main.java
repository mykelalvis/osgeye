import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import org.osgeye.remotereflect.SampleA;


public class Main
{
  private int xxx;
  
  private List<String> vals = new ArrayList<String>();
  
  public List<SampleA> getSamples()
  {
    return null;
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    ArrayList<SampleA> sampleAList = new ArrayList<SampleA>();
    Class clazz = sampleAList.getClass();
    
    
    Method[] methods = Main.class.getDeclaredMethods();
    for (Method method : methods)
    {
      if (method.getName().equals("getSamples"))
      {
        Type returnClass = method.getGenericReturnType();
        System.out.println(returnClass instanceof ParameterizedType);
        ParameterizedType pt = (ParameterizedType)returnClass;
        System.out.println(pt.getActualTypeArguments()[0]);
      }
    }
    
//    System.out.println(clazz instanceof GenericDeclaration);
   // TypeVariable typeParam = clazz.getTypeParameters()[0];
   //  ParameterizedType pt = (ParameterizedType)typeParam.getGenericDeclaration();

    //    System.out.println(typeParam.getBounds()[0]);
    
    /*
    File file = new File("test.xml");
    
    Field[] fields = file.getClass().getDeclaredFields();
    System.out.println(fields.length);
    
    for (Field field : fields)
    {
      field.setAccessible(true);
      System.out.println(field.getName() + " = " + field.get(file) + "  " + field.getDeclaringClass());
      if (field.getName().equals("path"))
      {
        field.set(file, "hello.html");
      }
    }
    
    System.out.println(file.getAbsolutePath());
    */
    
    /*
    Field x = Main.class.getDeclaredField("xxx");
    System.out.println(x.getType());
       
    Field f = Main.class.getDeclaredField("vals");
    System.out.println(f.getGenericType());
    ParameterizedType pt = (ParameterizedType)f.getGenericType();
    System.out.println(pt.getRawType() == List.class);
    System.out.println(pt.getOwnerType() + " " + pt.getRawType() + " " + pt.getActualTypeArguments()[0]);
    
    Method[] methods = Main.class.getDeclaredMethods();
    for (Method method : methods)
    {
      if (method.getName().equals("test"))
      {
        System.out.println(method.getReturnType());
        Type[] types = method.getGenericParameterTypes();
        for (Type type : types)
        {
          if (type instanceof ParameterizedType)
          {
            pt = (ParameterizedType)type;
            Class clazz = (Class)pt.getActualTypeArguments()[0];
            System.out.println(clazz.getSimpleName());
          }
        }
      }
    }
    */
  }
  
  public void test(List<String> abc, Integer xx)
  {
    
  }

}
