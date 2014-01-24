package com.example.cloud;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class filelist extends ListActivity
{
	ArrayList<String> filelist1=new ArrayList<String>();
	ListView lv;
	EditText et;
	ArrayAdapter<String> arrayAdapter;
	Button back;
	TextView path;
	
	public void onCreate(Bundle filelist)
	{
		Intent i = getIntent();
		filelist1 = i.getStringArrayListExtra("FileList");
		super.onCreate(filelist);
		
		setContentView(R.layout.filelist);
		lv = (ListView)findViewById(R.id.skydrive_list);
		et=(EditText)findViewById(R.id.inputSearch);
		path=(TextView)findViewById(R.id.filepath);
		back=(Button)findViewById(R.id.back);
    
        arrayAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, filelist1);
        lv.setAdapter(arrayAdapter); 
	
	
	et.addTextChangedListener(new TextWatcher()
	{
		 
        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
        {
            // When user changed the Text
            filelist.this.arrayAdapter.getFilter().filter(cs);
        }

		@Override
		public void afterTextChanged(Editable arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3)
		{
			// TODO Auto-generated method stub
			
		}

       
    });
	
	
	
}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	
	
	
	
	
}
