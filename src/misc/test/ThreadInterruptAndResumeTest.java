package misc.test;

import java.io.IOException;

public class ThreadInterruptAndResumeTest
{
	public static void main(String _args[]) throws IOException
	{
		Runnable runnable = new Runnable(){
			private int i = 0;

			@Override
			public void run()
			{
				while(!Thread.interrupted()){
					try{
						System.out.println(i++);
						Thread.sleep(1000);
					}catch(InterruptedException _e){
						_e.printStackTrace();
						break;
					}
				}
			}
		};
		
		Thread th = new Thread(runnable);
		th.start();
		
		while(true){
			System.in.read();
			
			if(th.isAlive()){
				th.interrupt();
			}else{
				th = new Thread(runnable);
				th.start();
			}
		}
	}
}
