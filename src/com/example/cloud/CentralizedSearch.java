package com.example.cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.example.cloud.DropboxFolderList.DropboxInterTransfer;
import com.example.cloud.DropboxFolderList.upload_skydrive;
import com.example.cloud.SkydriveFolderList.Skydrive_InterTransfer;
import com.example.cloud.Util.DropboxBean;
import com.example.cloud.Util.DropboxDataBean;
import com.example.cloud.Util.DropboxTransfer;
import com.example.cloud.Util.LiveConnectHandler;
import com.example.cloud.Util.MyCustomBaseAdapterForSearching;
import com.example.cloud.Util.SearchResults;
import com.example.cloud.Util.SkydriveBean;
import com.example.cloud.Util.SkydriveTransfer;
import com.example.cloud.database.DBController;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveUploadOperationListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CentralizedSearch extends Activity implements OnClickListener
{
	
	EditText search;
	Button data_search;
	ListView search_result;
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	
	final static private String APP_KEY = "qwhxbi7bea749s3";
	final static private String APP_SECRET = "8iuupmixtus726i";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	ArrayList<SearchResults> dropbox;
	ArrayList<SearchResults> skydrive;
	ArrayList<SearchResults> finalresult;
	protected static final int CONTEXTMENU_OPTION1 = 1;
	protected static final int CONTEXTMENU_OPTION2 = 2;
	protected static final int CONTEXTMENU_OPTION3 = 3;
	MyCustomBaseAdapterForSearching search_adapter;
	String service="";
	String se;
	int fileid;
	String folderpath="";
	//DropboxAPI<AndroidAuthSession> mDBApi;
	DropboxBean db=new DropboxBean();
	SkydriveBean sb=new SkydriveBean();
	LiveConnectClient client;
	String skydrive_folder_path="";
	private LiveConnectClient mClient;
	SkydriveTransfer st=new SkydriveTransfer();
	DropboxAPI<AndroidAuthSession> mDBApi;
	@Override
	protected void onCreate(Bundle CentralSearch)
	{
		
		super.onCreate(CentralSearch);
		setContentView(R.layout.centralsearch);
		search=(EditText)findViewById(R.id.inputSearch);
		data_search=(Button)findViewById(R.id.Search);
		search_result=(ListView)findViewById(R.id.SearchResult);
		mClient=LiveConnectHandler.client;
		 client=sb.getClient();
		data_search.setOnClickListener(this);
		registerForContextMenu(search_result);
		
		search_result.setOnItemClickListener( new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id)
			{
				Object o = parent.getItemAtPosition(position);
			       SearchResults fullObject = (SearchResults)o;
			      
			       
				
			}
			
		});
		
		search_result.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Object o = parent.getItemAtPosition(position);
			       SearchResults fullObject = (SearchResults)o;
			      
			       se=fullObject.getService().toString();
			       service=fullObject.getFilename().toString();
			       fileid=fullObject.getFileid();
			       search_result.showContextMenu();
				return true;
			}
			
		});
		
		
	
	}

	
	
	
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.Search:
			
			//new search_result().execute();
			if(search.getText().toString().equalsIgnoreCase("") ||search.getText().toString().equalsIgnoreCase(null))
			return;
			else
			search_data(search.getText().toString());
			break;
		
		
		
		}
		
		
	}

	
	
	public void search_data(String parameter)
	{
		DBController dbc=new DBController(this);
		skydrive=new ArrayList<SearchResults>();
		dropbox=new ArrayList<SearchResults>();
		finalresult=new ArrayList<SearchResults>();
		skydrive=dbc.getSearchDataFromSkydrive(parameter);
		dropbox=dbc.getSearchDataFromDropbox(parameter);
		
		finalresult.addAll(skydrive);
		finalresult.addAll(dropbox);
		search_adapter=new MyCustomBaseAdapterForSearching(CentralizedSearch.this, finalresult);
		search_result.setAdapter(search_adapter);
		search_adapter.notifyDataSetChanged();
	}
	
	
	   public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	   	{ 
	   	    super.onCreateContextMenu(menu, v, menuInfo); 
	   	 
	   	    // Set title for the context menu
	   	    menu.setHeaderTitle(service); 
	   	    
	   	// String getName=getService();
	   	    // Add all the menu options
	   	 if(se.equalsIgnoreCase("Dropbox"))
	   	 {
	   		
	   	    menu.add(Menu.NONE, CONTEXTMENU_OPTION1, 0, "Send To Skydrive");
	   	    menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Download From Dropbox");
	   	    menu.setHeaderIcon(R.drawable.dropbox_icon);
	   	 }
	   	 else
	   	 {
	   		menu.add(Menu.NONE, CONTEXTMENU_OPTION1, 0, "Send To Dropbox");
	   		menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Download From Skydrive");
	   		menu.setHeaderIcon(R.drawable.icon_skydrive);
	   	 }
	   	 
	   	 
	   	    
	   	}  
	
	
	@Override 
	
	public boolean onContextItemSelected(MenuItem item)
	{ 
		 
	    // Get extra info about list item that was long-pressed
	    AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
	    DBController dbc2=new DBController(this);
	    folderpath=dbc2.getDropboxFilePath(fileid);
	    skydrive_folder_path=dbc2.getSkydriveFilePath(fileid);
	    AndroidAuthSession session=buildSession();
		mDBApi=new DropboxAPI<AndroidAuthSession>(session);
       // mDBApi=db.getmDBApi();
	    
	    // Perform action according to selected item from context menu
	    switch (item.getItemId()) 
	    {
	 
	    case CONTEXTMENU_OPTION1:
	        // Show message
	    	
	       // Toast.makeText(getApplicationContext(), "Option 1: ID "+fileid+"",  Toast.LENGTH_SHORT).show();
	        
	        
	             
			
	        	Toast.makeText(getApplicationContext(), folderpath, Toast.LENGTH_LONG).show();
	        if(item.getTitle().equals("Send To Skydrive"))
		    {
	        	new DropboxInterTransfer().execute();
				//DownloadFromDropbox(folderpath, mDBApi);
				//UploadToSkydrive();
	        	
	        }
	        else
	        {
	        	new Skydrive_InterTransfer().execute();
	        	
	        }
	        
	        break;
	    case CONTEXTMENU_OPTION2:
	    	ProgressDialog downloadProgressDialog =new ProgressDialog(CentralizedSearch.this);
            downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	
	    	if(item.getTitle().equals("Download From Dropbox"))
	    	{
	    		try 
	    	{
	    		    downloadProgressDialog =new ProgressDialog(CentralizedSearch.this);
	                downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	                downloadProgressDialog.setMessage("Downloading..." +service+ "From Dropbox");
	                downloadProgressDialog.setCancelable(true);
	                downloadProgressDialog.show();
	                
					
					DropboxTransfer dt=new DropboxTransfer();
	                dt.DownloadFromDropbox("/Dropbox",folderpath, mDBApi, service);
	                downloadProgressDialog.dismiss();
					
			} 
	    	catch (IOException e)
	    	{
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    	}
	    	else
	    		
				try {
					downloadProgressDialog =new ProgressDialog(CentralizedSearch.this);
	                downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	                downloadProgressDialog.setMessage("Downloading..." +service+ "From Skydrive");
	                downloadProgressDialog.setCancelable(true);
	                downloadProgressDialog.show();
	                
					SkydriveTransfer st=new SkydriveTransfer();
					st.downloadFromSkydrive("/Skydrive",skydrive_folder_path, service, client);
					
					downloadProgressDialog.dismiss();
					
				} 
	    		catch (IOException e)
	    	   {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	break;
	    }
	 
	    return true; 
	}  
	
	private class DropboxInterTransfer extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog downloadProgressDialog =new ProgressDialog(CentralizedSearch.this);
		
		@Override
		protected void onPostExecute(Void result)
		{
			
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		// downloadProgressDialog.dismiss();
		// Looper.myLooper().quit();
		}

		@Override
		protected void onPreExecute()
		{
			
			
			// TODO Auto-generated method stub
			super.onPreExecute();
			downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        downloadProgressDialog.setMessage("Sending..."+service +"To Skydrive");
	        downloadProgressDialog.setCancelable(true);
	        downloadProgressDialog.show();
			
		}

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			Looper.prepare();
			AndroidAuthSession session=buildSession();
			DropboxAPI<AndroidAuthSession> mDBApi;
			mDBApi=new DropboxAPI<AndroidAuthSession>(session);
			Toast.makeText(getApplicationContext(),mDBApi.toString(), Toast.LENGTH_LONG).show();
			
			
            
            
			//DownloadFromDropbox(folderpath,mDBApi);
            DropboxTransfer dt=new DropboxTransfer();
            try
            {
				dt.DownloadFromDropbox(null,folderpath, mDBApi, service);
				new upload_skydrive().execute();
				downloadProgressDialog.dismiss();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //downloadProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Sending Successful",Toast.LENGTH_LONG).show();
            //Looper.loop();
          
            
            return null;
		}
		
	}	
	
	private AndroidAuthSession buildSession()
	{
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null)
		{
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,accessToken);
		}
		else 
		{
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	/* getting dropbox stored keys from shared preferences */
	private String[] getKeys()
	{
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
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
	
	
	private class upload_skydrive extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected Void doInBackground(Void... arg0)
		{
			
			LiveConnectClient client1=LiveConnectHandler.client;
			st.UploadToSkydrive(service, client1);
			return null;
		}
		
	}
	
	private class Skydrive_InterTransfer extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog downloadProgressDialog =new ProgressDialog(CentralizedSearch.this);
		
		@Override
		protected void onPostExecute(Void result)
		{
			
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		 downloadProgressDialog.dismiss();
		// Looper.myLooper().quit();
		}

		@Override
		protected void onPreExecute()
		{
			
			
			// TODO Auto-generated method stub
			super.onPreExecute();
			downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        downloadProgressDialog.setMessage("Sending to Dropbox...."+service);
	        downloadProgressDialog.setCancelable(true);
	        downloadProgressDialog.show();
			
		}
		
		
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			Looper.prepare();
			
			DropboxTransfer dt=new DropboxTransfer();
			try {
				
				
				st.downloadFromSkydrive(null,folderpath, service, mClient);
				AndroidAuthSession session=buildSession();
				DropboxAPI<AndroidAuthSession> mDBApi;
				mDBApi=new DropboxAPI<AndroidAuthSession>(session);
				dt.UploadToDropbox(service, mDBApi);
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Toast.makeText(getApplicationContext(), "Sending Successful",Toast.LENGTH_LONG).show();
	        //Looper.loop();
	      
	        
	        return null;
		}
		
	}	
	
	
	
	
	
	
	
	
 }
