package com.example.cloud;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.example.cloud.DropboxFolderList.DropboxInterTransfer;
import com.example.cloud.Util.DropboxApiKeys;
import com.example.cloud.Util.DropboxBean;
import com.example.cloud.Util.DropboxDataBean;
import com.example.cloud.Util.DropboxTransfer;
import com.example.cloud.Util.FileUploadBean;
import com.example.cloud.Util.LiveConnectHandler;
import com.example.cloud.Util.MyCustomBaseAdapterForDropbox;
import com.example.cloud.Util.MyCustomBaseAdapterForUpload;
import com.example.cloud.Util.SkydriveTransfer;
import com.example.cloud.database.DBController;
import com.microsoft.live.LiveConnectClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FileUpload extends Activity 
{
	protected static final int CONTEXTMENU_OPTION1 = 1;
	protected static final int CONTEXTMENU_OPTION2 = 2;
	ListView lv;
	EditText et;
	ArrayList<FileUploadBean> arrayAdapter;
	Button back,search;
	TextView path;
	Stack<String> prevPath;
	String filepath;
	String service;
	MyCustomBaseAdapterForUpload sdCardList;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);
		lv = (ListView)findViewById(R.id.skydrive_list);
		et=(EditText)findViewById(R.id.inputSearch);
		et.setVisibility(View.GONE);
		search=(Button)findViewById(R.id.search);
		search.setVisibility(View.GONE);
		
		path=(TextView)findViewById(R.id.filepath);
		back=(Button)findViewById(R.id.back);
		prevPath=new Stack<String>();
		prevPath.add(Environment.getExternalStorageDirectory().getPath());
		back.setVisibility(View.GONE);
		path.setVisibility(View.GONE);
		arrayAdapter=getSearchResults();
		
		sdCardList=new MyCustomBaseAdapterForUpload(FileUpload.this, arrayAdapter);
		lv.setAdapter(sdCardList);
		registerForContextMenu(lv);
		
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id)
			{
				
				
				Object o = parent.getItemAtPosition(position);
			       FileUploadBean fullObject = (FileUploadBean)o;
			       filepath=fullObject.getPath();
			       service=fullObject.getFilename();
			       if(fullObject.getIsFolder().equalsIgnoreCase("Folder"))
			    	   return false;
			       else
			       {
			       lv.showContextMenu();
			       return true;
			       }
			}
			
		});    
		
		
		
		
		
		
		 lv.setOnItemClickListener(new OnItemClickListener()
			
			{
		        public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
		        {
		        	
		        	Object o = parent.getItemAtPosition(position);
		            FileUploadBean object1 = (FileUploadBean)o;
		            
		       if(object1.getIsFolder().equalsIgnoreCase("Folder"))
		       {
		    	   
		    	   String FolderPath=object1.getPath().toString();
		    	   ArrayList<FileUploadBean> nxt=getFolderClick(FolderPath);
		    	   sdCardList=new MyCustomBaseAdapterForUpload(FileUpload.this, nxt);
		   		   lv.setAdapter(sdCardList);
		   		   sdCardList.notifyDataSetChanged();
		   		   prevPath.add(FolderPath);
		    	   
		       }
		       else
		       {
		    	   
		    	   
		       }
		              
		      
		     
		        }

				});
			
		
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
   	{ 
   	    super.onCreateContextMenu(menu, v, menuInfo); 
   	    menu.setHeaderTitle(service);
   	    
        menu.add(Menu.NONE,CONTEXTMENU_OPTION1,0,"Upload To Dropbox");
   	    menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Upload To Skydrive");
        //menu.setHeaderIcon(R.drawable.dropbox_icon);
   	}  
	 public boolean onContextItemSelected(MenuItem item)
		{ 
			 
		  
		    switch (item.getItemId()) 
		    {
		 
		    case CONTEXTMENU_OPTION1:
		       
		    	AndroidAuthSession session=buildSession();
				DropboxAPI<AndroidAuthSession> mDBApi=new DropboxAPI<AndroidAuthSession>(session);
		    	DropboxTransfer dt=new DropboxTransfer();
				dt.DropboxUpload(filepath , mDBApi);
		        	
		        break;
		        
		    case CONTEXTMENU_OPTION2:
		    	SkydriveTransfer st=new SkydriveTransfer();
				LiveConnectClient client1=LiveConnectHandler.client;
				st.SkydriveUpload( filepath,client1);
		    	break;
		}
			return true;
	
	
		}
	
	
	private ArrayList<FileUploadBean> getSearchResults()
	{
		ArrayList<FileUploadBean> list=new ArrayList<FileUploadBean>();
		FileUploadBean fub;
		File f=new File(Environment.getExternalStorageDirectory().getPath());
		//String[] list1=f.list();
		File[] f1=f.listFiles();
		for(int i=0;i<f1.length;i++)
		
		{
			if(f1[i].isDirectory())
			{
			fub=new FileUploadBean();	
			fub.setFilename(f1[i].getName());
			fub.setPath(f1[i].getAbsolutePath());
			fub.setIsFolder("Folder");
			list.add(fub);
			}
			else
			{
				fub=new FileUploadBean();
				fub.setFilename(f1[i].getName());
				fub.setPath(f1[i].getAbsolutePath());
				fub.setIsFolder("");
				list.add(fub);
			}
			
		}
		return list;
	}
	private ArrayList<FileUploadBean> getFolderClick(String path)
	{
		ArrayList<FileUploadBean> list=new ArrayList<FileUploadBean>();
		FileUploadBean fub;
		File f=new File(path);
		//String[] list1=f.list();
		File[] f1=f.listFiles();
		for(int i=0;i<f1.length;i++)
		
		{
			if(f1[i].isDirectory())
			{
			fub=new FileUploadBean();	
			fub.setFilename(f1[i].getName());
			fub.setPath(f1[i].getAbsolutePath());
			fub.setIsFolder("Folder");
			list.add(fub);
			}
			else
			{
				fub=new FileUploadBean();
				fub.setFilename(f1[i].getName());
				fub.setPath(f1[i].getAbsolutePath());
				fub.setIsFolder("");
				list.add(fub);
			}
			
		}
		return list;
	}
	private AndroidAuthSession buildSession()
	{
		AppKeyPair appKeyPair = new AppKeyPair(DropboxApiKeys.APP_KEY, DropboxApiKeys.APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null)
		{
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],stored[1]);
			session = new AndroidAuthSession(appKeyPair, DropboxApiKeys.ACCESS_TYPE,accessToken);
		}
		else 
		{
			session = new AndroidAuthSession(appKeyPair, DropboxApiKeys.ACCESS_TYPE);
		}

		return session;
	}

	/* getting dropbox stored keys from shared preferences */
	private String[] getKeys()
	{
		SharedPreferences prefs = getSharedPreferences(DropboxApiKeys.ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(DropboxApiKeys.ACCESS_KEY_NAME, null);
		String secret = prefs.getString(DropboxApiKeys.ACCESS_SECRET_NAME, null);
		if (key != null && secret != null)
		{
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		}
		else
		{
			return null;
		}
	}	
	
	
	
}
