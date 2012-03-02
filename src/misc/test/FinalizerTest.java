package misc.test;

public class FinalizerTest
{
	public final Object finalizerGuardian = new Object(){
		protected void finalize() throws Throwable
		{
			finalize();
		}
	};
	
	public static void main(String[] _args)
	{
		FinalizerTest test = new FinalizerTest();
		test.test();
	}
	
	public void test()
	{
		System.out.println("testte");
	}
	
	
	@Override
	protected void finalize() throws Throwable
	{
		try{
			System.out.println("hogeeeeeeee");
		}catch(Exception _e){
			_e.printStackTrace();
		}finally{
			super.finalize();
		}
	}
}
