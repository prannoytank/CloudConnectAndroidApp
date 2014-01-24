package com.example.cloud.database;

import java.io.IOException;

import com.example.cloud.R;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
    

       MakeDatabase md=new MakeDatabase(this);
       try {
	md.copyDataBase();
	}
       catch (IOException e)
	{
		
		e.printStackTrace();
}
   }
}

    
//}
