package com.example.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

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
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.example.cloud.Util.DropboxApiKeys;
import com.example.cloud.Util.DropboxBean;
import com.example.cloud.Util.DropboxDataBean;
import com.example.cloud.Util.DropboxTransfer;
import com.example.cloud.Util.LiveConnectHandler;
import com.example.cloud.Util.MyCustomBaseAdapter;
import com.example.cloud.Util.MyCustomBaseAdapterForDropbox;
import com.example.cloud.Util.SearchResults;
import com.example.cloud.Util.SkydriveBean;
import com.example.cloud.Util.SkydriveTransfer;
import com.example.cloud.database.DBController;
import com.google.gson.Gson;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SkydriveFolderList extends Activity implements View.OnClickListener
{
	
	ArrayList<SearchResults> skydrive;
	
	
	boolean isConnected=false;
	
	Stack<Integer> prevId;
	Stack<String> prevPath;
	String SkyFolderPath="/Skydrive";
	 DBController dbc=new DBController(this);
	 MyCustomBaseAdapter adapt;
	
	 String service="";
	 String se;
	 int file_id;
	 String folderpath=""; 
	 String IsFolder="";
	 
	 protected static final int CONTEXTMENU_OPTION1 = 1;
	 protected static final int CONTEXTMENU_OPTION2 = 2;
	 LiveConnectClient client;
	ListView lv;
	EditText et;
	ArrayAdapter<SearchResults> arrayAdapter;
	ArrayList<SearchResults> arrayAdapter1;
	Button back,search;
	TextView path;
	String prevpath;
	SkydriveBean sb=new SkydriveBean();
	private LiveConnectClient mClient;
	SkydriveTransfer st;
	static String jsonString="";
	
	public void onCreate(Bundle SkydriveFolderList)
	{
		
		super.onCreate(SkydriveFolderList);
		//ArrayList<SearchResults> searchResults = GetSearchResults();
		ArrayList<SearchResults> list = getData("0");
		
		setContentView(R.layout.filelist);
		
		lv = (ListView)findViewById(R.id.skydrive_list);
		et=(EditText)findViewById(R.id.inputSearch);
		path=(TextView)findViewById(R.id.filepath);
		back=(Button)findViewById(R.id.back);
		search=(Button)findViewById(R.id.search);
		
		prevId=new Stack<Integer>();
		prevPath=new Stack<String>();
		prevPath.push("HOME");
		prevId.push(0);
		back.setEnabled(false);
		
		
		
		
		adapt=new MyCustomBaseAdapter(SkydriveFolderList.this, list);
        arrayAdapter =new ArrayAdapter<SearchResults>(this,android.R.layout.simple_list_item_1,list);
		// ((ListView) arrayAdapter).setAdapter();
		lv.setAdapter(adapt); 
		//lv.setAdapter(arrayAdapter);
		back.setOnClickListener(this);
		search.setOnClickListener(this);
		registerForContextMenu(lv);
		mClient=LiveConnectHandler.client;
		//LiveSdkSampleApplication app = (LiveSdkSampleApplication) getApplication();
		st=new SkydriveTransfer();
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id)
			{
				
				
				Object o = parent.getItemAtPosition(position);
			       SearchResults fullObject = (SearchResults)o;
			       service=fullObject.getFilename();
			       file_id=fullObject.getFileid();
			       IsFolder=fullObject.getIsFolder();
			       if(fullObject.getIsFolder().equalsIgnoreCase("Folder"))
			    	   return false;
			       else
			       {
			       lv.showContextMenu();
			       return true;
			       }
			}
			
		});    
        	
		
		
		
	et.addTextChangedListener(new TextWatcher()
	{
		 
        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
        {
            // When user changed the Text
           // SkydriveFolderList.this.arrayAdapter.getFilter().filter(cs);
        	
        	if(et.getText().toString().equalsIgnoreCase("") ||et.getText().toString().equalsIgnoreCase(null)||et.getText().toString().length()==0)
        	{
        		ArrayList<SearchResults> refreshed= getData("0");
        	    adapt=new MyCustomBaseAdapter(SkydriveFolderList.this, refreshed);
      	        adapt.notifyDataSetChanged();
      	        lv.setAdapter(adapt);
      	        prevPath.removeAllElements();
      	        prevId.removeAllElements();
      	        prevPath.push("HOME");
     		    prevId.push(0);
     		    path.setText("HOME");
        		back.setEnabled(false);
        		
        	}
        	else
        	{	
        	ArrayList<SearchResults> refreshed1= getSearchData(et.getText().toString());
        	adapt=new MyCustomBaseAdapter(SkydriveFolderList.this, refreshed1);
  	        
  	        lv.setAdapter(adapt);
  	        
  	        adapt.notifyDataSetChanged();
        	}
        
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
	
	
	lv.setOnItemClickListener(new OnItemClickListener()
	
	{
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
        {
        	
        	 Object o = parent.getItemAtPosition(position);
             SearchResults fullObject = (SearchResults)o;
             
             if(fullObject.getIsFolder().equalsIgnoreCase("Folder"))
             {
        	
        prevpath=path.getText().toString();
        
      
      
   	   int fileid=fullObject.getFileid();
   	   
       
       
       ArrayList<SearchResults> Results =getData(String.valueOf(fileid));
       adapt=new MyCustomBaseAdapter(SkydriveFolderList.this, Results);
       adapt.notifyDataSetChanged();
       lv.setAdapter(adapt);
       path.setText(prevpath + "/" + fullObject.getFilename().toString());
       path.setTextSize(14);
       path.setTextColor(Color.BLUE);
       //prevPath.push("/" + fullObject.getFilename().toString());
       prevPath.push(path.getText().toString());
       prevId.push(fileid);
       if(path.getText().equals("HOME"))
       	back.setEnabled(false);
       else
       	back.setEnabled(true);
             }
             else
             {
            	 
             }
      // prestationEco str=(prestationEco)o;//As you are using Default String Adapter
       //Toast.makeText(getBaseContext(),fullObject.getFilename(),Toast.LENGTH_SHORT).show();
     }
        });
	
	
}
	
	public ArrayList<SearchResults> getSearchData(String FileName)
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
		        list.add(ddb);
		    }
		    jsonString="";
		    
			}
			catch(Exception e)
			{
				
			}
		 
		 return list;
		 
}	 	
	
	
	
	public ArrayList<SearchResults> getData(String Parentid)
	 {
		 ArrayList<SearchResults> list=new ArrayList<SearchResults>();
		 SearchResults sr=null;
		 try
			{
			ArrayList<NameValuePair> SkydriveMasterValuePairs = new ArrayList<NameValuePair>(2);
			SkydriveMasterValuePairs.add(new BasicNameValuePair("uId","47ee3ccbc1009f18"));
			SkydriveMasterValuePairs.add(new BasicNameValuePair("parentId",Parentid));
			SkydriveMasterValuePairs.add(new BasicNameValuePair("request_type","GetUserData"));
			
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
		    	
		    	sr=new SearchResults();
		        sr.setFileid(data.getInt("_id"));
		        sr.setFilename(data.getString("fileName"));
		        if(data.getInt("isFolder")==0)
		        sr.setIsFolder("");
		        else
		        sr.setIsFolder("Folder");
		        sr.setPath(data.getString("folderPath"));
		        list.add(sr);
		    }
		    jsonString="";
		    
			}
			catch(Exception e)
			{
				
			}
		 
		 return list;
		 
}	
	
	
	
	
	
	

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.back:
		prevId.pop();
		prevPath.pop();
		String present_path=prevPath.elementAt(prevPath.size()-1);
		
		if(present_path.equalsIgnoreCase("HOME"))
			back.setEnabled(false);
		else
			back.setEnabled(true);
		int present_id=prevId.elementAt(prevId.size()-1);
		Log.i("id",""+present_id);
		Log.i("path",present_path);
		   
	       ArrayList<SearchResults> Results =getData(String.valueOf(present_id));
	       adapt=new MyCustomBaseAdapter(SkydriveFolderList.this, Results);
	      
	       lv.setAdapter(adapt);
	       adapt.notifyDataSetChanged();
	       
	       path.setText(present_path);
			
			
			
		break;
		case R.id.search:
			
			//getResult(et.getText().toString());
			
			
			break;
		
		
		}
		// TODO Auto-generated method stub
		
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
   	{ 
   	    super.onCreateContextMenu(menu, v, menuInfo); 
   	    menu.setHeaderTitle(service);
   	    if(IsFolder.equalsIgnoreCase("Folder"))
   	    	return;
   	    else
   	    {
   	    menu.add(Menu.NONE, CONTEXTMENU_OPTION1, 0, "Send To Dropbox");
   	    menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Download From Skydrive");
   	    menu.setHeaderIcon(R.drawable.icon_skydrive);
   	    }
   	    }  
	
	public boolean onContextItemSelected(MenuItem item)
	{ 
		 
	    // Get extra info about list item that was long-pressed
	    AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
	    DBController dbc2=new DBController(this);
	    folderpath=dbc2.getSkydriveFilePath(file_id);
	    DropboxBean db=new DropboxBean();
	  
        //mDBApi=db.getmDBApi();
	    
        
      
	    switch (item.getItemId()) 
	    {
	 
	    case CONTEXTMENU_OPTION1:
	       
	        
	             
			new Skydrive_InterTransfer().execute();
			
			
	        	//Toast.makeText(getApplicationContext(),folderpath , Toast.LENGTH_LONG).show();
	        
			//dt.DownloadFromDropbox(folderpath, mDBApi, service);
			//st.UploadToSkydrive(service, client);
	        	
	        
	        
	        
	        break;
	    case CONTEXTMENU_OPTION2:
	    	
	    	
	    	try {
	    		
	    		ProgressDialog downloadProgressDialog =new ProgressDialog(SkydriveFolderList.this);
	    		downloadProgressDialog.setTitle("Downloading...");
	    		downloadProgressDialog.setMessage("Downloading" +service +"from skydrive");
	    		downloadProgressDialog.show();
	    		
				st.downloadFromSkydrive(SkyFolderPath,folderpath, service, mClient);
				downloadProgressDialog.dismiss();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//new skydrive_download().execute();
	    	
	    	break;
	}
		return true;


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
		
	
private void getResult(String parameter)
{
	DBController dbc=new DBController(this);
	skydrive=new ArrayList<SearchResults>();
	skydrive=dbc.getSearchDataFromSkydrive(parameter);
	 adapt=new MyCustomBaseAdapter(SkydriveFolderList.this,skydrive);
     adapt.notifyDataSetChanged();
     lv.setAdapter(adapt);
	
	
}



public class Skydrive_InterTransfer extends AsyncTask<Void, Void, Void>
{
	ProgressDialog downloadProgressDialog =new ProgressDialog(SkydriveFolderList.this);
	
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
