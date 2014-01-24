package com.example.cloud;


import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Splash extends Activity
{
   TextView t1,t2;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		t1=(TextView)findViewById(R.id.textView1);
		t2=(TextView)findViewById(R.id.textView2);
		//t2.setTextColor(color.holo_orange_light);
		Thread t=new Thread()
		{
			public void run()
			{
				
				try {
					sleep(5000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
					Intent start=new Intent("com.example.cloud.MetisMeHomePage");
					startActivity(start);
					
				}
			}
			
		
		};
		t.start();
		
		
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	

}
