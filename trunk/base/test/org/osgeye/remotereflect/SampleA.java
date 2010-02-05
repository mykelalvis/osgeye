package org.osgeye.remotereflect;

import java.util.List;

public class SampleA
{
  public String one;
  
  public Integer two;
  
  public Long three;
  
  public String[] four;
  
  public List<SampleA> five;
  
  public SampleA()
  {}

  public String getOne()
  {
    return one;
  }

  public void setOne(String one)
  {
    this.one = one;
  }

  public Integer getTwo()
  {
    return two;
  }

  public void setTwo(Integer two)
  {
    this.two = two;
  }

  public Long getThree()
  {
    return three;
  }

  public void setThree(Long three)
  {
    this.three = three;
  }

  public String[] getFour()
  {
    return four;
  }

  public void setFour(String[] four)
  {
    this.four = four;
  }

  public List<SampleA> getFive()
  {
    return five;
  }

  public void setFive(List<SampleA> five)
  {
    this.five = five;
  }
}
