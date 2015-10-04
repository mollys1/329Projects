package test;

public class Test 
{
	//field tests
	public int TestField;
	public int TestDeletedField;
	
	//test field initializers
	public int Test = 0;
	public int TestChangeValue = 1;
	public int TestDelete = 5;
	public int TestModifierChange = 14;
	
	//test field that doesn't have modifier
	int TestNoModifier;
	
	public void TestMethod1()
	{
		return;
	}
	
	public void TestMethod2()
	{
		return;
	}
	
	public void TestMethod3()
	{
		return;
	}
	
	public void TestMethod_ToChange()
	{
		TestField =  1;
		TestField++;
		return;
	}
	
	public void TestMethod_ToDelete()
	{
		return;
	}
	
	public void TestMethod4()
	{
		return;
	}
	
	public void TestMethod_ToMove()
	{
		return;
	}
	
}
