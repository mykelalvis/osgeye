import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;


public class Main
{
  private int xxx;
  
  private List<String> vals = new ArrayList<String>();
  
  

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
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
    
  }
  
  public void test(List<String> abc, Integer xx)
  {
    
  }

}
