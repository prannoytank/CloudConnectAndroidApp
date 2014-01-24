package com.example.cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


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
import com.example.cloud.Util.CloudConnectionStatus;
import com.example.cloud.Util.DropboxApiKeys;
import com.example.cloud.Util.DropboxBean;
import com.example.cloud.Util.DropboxDataBean;
import com.example.cloud.Util.DropboxTransfer;
import com.example.cloud.Util.LiveConnectHandler;
import com.example.cloud.Util.MyCustomBaseAdapterForDropbox;
import com.example.cloud.Util.MyCustomBaseAdapterForSearching;
import com.example.cloud.Util.SearchResults;
import com.example.cloud.Util.SkydriveBean;
import com.example.cloud.Util.SkydriveTransfer;
import com.example.cloud.button_selection.skydrive_list;
import com.example.cloud.database.DBController;
import com.example.cloud.database.MakeDatabase;

import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveUploadOperationListener;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MetisMeHomePage extends Activity 
{
	
	EditText search;
	//Button data_search;
	ListView search_result,options1;
	
	
	
	
	
	ArrayList<SearchResults> dropbox;
	ArrayList<SearchResults> skydrive;
	ArrayList<SearchResults> finalresult;
	
	protected static final int CONTEXTMENU_OPTION1 = 1;
	protected static final int CONTEXTMENU_OPTION2 = 2;
	protected static final int CONTEXTMENU_OPTION3 = 3;
	private static final int REQUEST_CODE = 6384;
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
	String empty[]=new String[]{"No Records Found"};
	 
	static String jsonString="";
	
	 boolean isDropboxConnected=true;
	 boolean isSkydriveConnected=true;
	
	@Override
	protected void onCreate(Bundle CentralSearch)
	{
		
		super.onCreate(CentralSearch);
		setContentView(R.layout.metismehomepage);
		
		if (android.os.Build.VERSION.SDK_INT > 9) 
		{
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		
		
		options1=(ListView)findViewById(R.id.CloudSelection);
		 String[] options = getResources().getStringArray(R.array.options);
		
		 options1.setAdapter((ListAdapter) new ArrayAdapter<String>(this,R.layout.homepage_textview, R.id.label, options));
		 //ListView lv = getListView();
		 options1.setBackgroundColor(color.black);
		 
		 search=(EditText)findViewById(R.id.inputSearch);
		//data_search=(Button)findViewById(R.id.Search);
		search_result=(ListView)findViewById(R.id.SearchResult);
		//mClient=LiveConnectHandler.client;
		
		search_result.setAdapter((ListAdapter) new ArrayAdapter<String>(MetisMeHomePage.this,R.layout.noresults,R.id.NoResults,empty));
		 client=sb.getClient();
		//data_search.setOnClickListener(this);
		registerForContextMenu(search_result);
		
		if(CloudConnectionStatus.isDropboxConnected==false && CloudConnectionStatus.isSkydriveConnected==false)
		{
			//alert();
			CloudConnectionStatus.isDropboxConnected=true;
			CloudConnectionStatus.isSkydriveConnected=true;
		}
		
			
			
		
		
		 options1.setOnItemClickListener(new OnItemClickListener() 
		 {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) 
	          {
	 
	              // selected item
	              String product = parent.getItemAtPosition(position).toString();
	               if(product.equalsIgnoreCase("Dropbox"))
	               {
	            	   
	            	   DropboxFolder();
	       			
	            	   
	               }
	               else if(product.equalsIgnoreCase("Skydrive"))
	               {
	            	   SkydriveFolder();
	               }
	               else if(product.equalsIgnoreCase("Accounts"))
	            	   
	            	   
	               {
	            	   Intent account=new Intent("com.example.cloud.Accounts");
	   				//Toast.makeText(getApplicationContext(), "Account",Toast.LENGTH_LONG).show();
	   				  startActivity(account);
	               }
	               else
	               {
	            	   Intent i1=new Intent("com.example.cloud.FileUpload");
	            	   startActivity(i1);
	            	   
	            	   
	               }
	               
	              // Launching new Activity on selecting single List Item
	           //Toast.makeText(getBaseContext(), product, Toast.LENGTH_LONG).show();
	              
	          }
	        });
	    
		
		search.addTextChangedListener(new TextWatcher()
		{
			 
	        @Override
	        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
	        {
	            // When user changed the Text
	        	//DropboxFolderList.this.arrayAdapter.getFilter().filter(cs);
	        	if(search.getText().toString().equalsIgnoreCase("") ||search.getText().toString().equalsIgnoreCase(null)||search.getText().toString().length()==0)
	        	{
	        		
	        		search_result.setAdapter((ListAdapter) new ArrayAdapter<String>(MetisMeHomePage.this,R.layout.noresults,R.id.NoResults,empty));	
	        		
	        	}
	    		else
	    		{
	    			//search_data(search.getText().toString());
	    			 dropbox= getDropboxSearchData(search.getText().toString());
	    			
	    			skydrive=getSkydriveSearchData(search.getText().toString());
	    			finalresult=new ArrayList<SearchResults>();
	    			
	    			finalresult.addAll(skydrive);
	    			finalresult.addAll(dropbox);
	    			
	    			search_adapter=new MyCustomBaseAdapterForSearching(MetisMeHomePage.this, finalresult);
	    			search_result.setAdapter(search_adapter);
	    			search_adapter.notifyDataSetChanged();
	    		
	    		}	
	        }

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub
				
			}	
		});
		
		
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
	protected void onResume()
	
	{
		
		super.onResume();
		if(CloudConnectionStatus.isDropboxConnected==false && CloudConnectionStatus.isSkydriveConnected==false)
		{
			alert();
		}
		
	}

	public ArrayList<SearchResults> getSkydriveSearchData(String FileName)
	 {
		 ArrayList<SearchResults> list=new ArrayList<SearchResults>();
		 SearchResults ddb=null;
		 try
			{
			ArrayList<NameValuePair> SkydriveMasterValuePairs = new ArrayList<NameValuePair>(2);
			SkydriveMasterValuePairs.add(new BasicNameValuePair("uId","47ee3ccbc1009f18"));
			SkydriveMasterValuePairs.add(new BasicNameValuePair("FileName",FileName));
			SkydriveMasterValuePairs.add(new BasicNameValuePair("request_type","GetSearchData"));
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://10.0.2.2:8080/CloudConnectWebApp/SkydriveFuntionServlet");
		    httppost.setEntity(new UrlEncodedFormEntity(SkydriveMasterValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			Scanner scanner = new Scanner(response.getEntity().getContent(), "UTF-8");       
	        
		    while(scanner.hasNextLine()) 
		    { // scanner looks ahead for an end-of-line
		        jsonString += scanner.nextLine() + "\n"; // read the full line, you can append a \n
		    }   
		   Log.i("data",jsonString);
		    JSONArray jsonArray = new JSONArray(jsonString);
		    
		    
		    for (int i =0; i <jsonArray.length(); i++) 
		    {
		    	JSONObject data=jsonArray.optJSONObject(i);
		    	
		    	ddb=new SearchResults();
		        ddb.setFileid(data.getInt("_id"));
		        ddb.setFilename(data.getString("fileName"));
		        if(data.getInt("isFolder")==0)
		        ddb.setIsFolder("");
		        else
		        ddb.setIsFolder("Folder");
		        ddb.setPath(data.getString("folderPath"));
		        ddb.setService("Skydrive");
		        list.add(ddb);
		    }
		    jsonString="";
		    
			}
			catch(Exception e)
			{
				
			}
		 
		 return list;
		 
}	 	
	
	
	
	
	
	
	
	
	
	
	
	
public void alert()
{
	AlertDialog.Builder skydrivelist = new AlertDialog.Builder(MetisMeHomePage.this);
	skydrivelist.setTitle("Welcome!!!!");
	skydrivelist.setMessage("Welcome to MetisMe cloud connect tool. You need to first set the account at the Cloud Services..Thanks ").setCancelable(false).setPositiveButton("Set Accounts",new DialogInterface.OnClickListener()
	{
			public void onClick(DialogInterface dialog,int id) 
			{
				
				Intent i=new Intent("com.example.cloud.Accounts");
				//Toast.makeText(getApplicationContext(), "Account",Toast.LENGTH_LONG).show();
				startActivity(i);
				
			}
		  });
	
	AlertDialog skydriveDialog = skydrivelist.create();
	 
	// show it
	skydriveDialog.show();
}

public void DropboxFolder()
{
	
	Intent dropbox=new Intent("com.example.cloud.DropboxFolderList");
	startActivity(dropbox);
}


public void SkydriveFolder()
{
	
	Intent i=new Intent("com.example.cloud.SkydriveFolderList");

	
	startActivity(i);
}


public ArrayList<SearchResults> getDropboxSearchData(String FileName)
{
	 ArrayList<SearchResults> list=new ArrayList<SearchResults>();
	 SearchResults ddb=null;
	 try
		{
		ArrayList<NameValuePair> SkydriveMasterValuePairs = new ArrayList<NameValuePair>(2);
		SkydriveMasterValuePairs.add(new BasicNameValuePair("uId","40246148"));
		SkydriveMasterValuePairs.add(new BasicNameValuePair("FileName",FileName));
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://10.0.2.2:8080/CloudConnectWebApp/DropboxMetaDataInsertServlet");
	    httppost.setEntity(new UrlEncodedFormEntity(SkydriveMasterValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		Scanner scanner = new Scanner(response.getEntity().getContent(), "UTF-8");       
       
	    while(scanner.hasNextLine()) 
	    { // scanner looks ahead for an end-of-line
	        jsonString += scanner.nextLine() + "\n"; // read the full line, you can append a \n
	    }   
	   Log.i("data",jsonString);
	    JSONArray jsonArray = new JSONArray(jsonString);
	    
	    
	    for (int i =0; i <jsonArray.length(); i++) 
	    {
	    	JSONObject data=jsonArray.optJSONObject(i);
	    	
	    	ddb=new SearchResults();
	        ddb.setFileid(data.getInt("_id"));
	        ddb.setFilename(data.getString("fileName"));
	        if(data.getInt("isFolder")==0)
	        ddb.setIsFolder("");
	        else
	        ddb.setIsFolder("Folder");
	        ddb.setPath(data.getString("folderPath"));
	        ddb.setService("Dropbox");
	        list.add(ddb);
	    }
	    jsonString="";
	    
		}
		catch(Exception e)
		{
			
		}
	 
	 return list;
	 
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
		search_adapter=new MyCustomBaseAdapterForSearching(MetisMeHomePage.this, finalresult);
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
	   	    menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Download");
	   	    menu.setHeaderIcon(R.drawable.dropbox_icon);
	   	 }
	   	 else
	   	 {
	   		menu.add(Menu.NONE, CONTEXTMENU_OPTION1, 0, "Send To Dropbox");
	   		menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Download");
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
	    	ProgressDialog downloadProgressDialog =new ProgressDialog(MetisMeHomePage.this);
            downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	
	    	if(item.getTitle().equals("Download From Dropbox"))
	    	{
	    		try 
	    	{
	    		    downloadProgressDialog =new ProgressDialog(MetisMeHomePage.this);
	                downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	                downloadProgressDialog.setMessage("Downloading..." +service+ "From Dropbox");
	                //downloadProgressDialog.setCancelable(true);
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
					downloadProgressDialog =new ProgressDialog(MetisMeHomePage.this);
	                downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	                downloadProgressDialog.setMessage("Downloading..." +service+ "From Skydrive");
	               // downloadProgressDialog.setCancelable(true);
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
		ProgressDialog downloadProgressDialog =new ProgressDialog(MetisMeHomePage.this);
		
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
	       // downloadProgressDialog.setCancelable(true);
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
		ProgressDialog downloadProgressDialog =new ProgressDialog(MetisMeHomePage.this);
		
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
	        //downloadProgressDialog.setCancelable(true);
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
