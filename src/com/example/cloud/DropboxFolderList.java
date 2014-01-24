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
import com.microsoft.live.LiveConnectClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

@SuppressLint("NewApi")
public class DropboxFolderList extends Activity implements View.OnClickListener
{
		
	protected static final int CONTEXTMENU_OPTION1 = 1;
	protected static final int CONTEXTMENU_OPTION2 = 2;
	ListView lv;
	EditText et;
	ArrayAdapter<String> arrayAdapter;
	Button back,search;
	TextView path;
	String prevpath;
	boolean isConnected=false;
	Stack<Integer> prevId;
	Stack<String> prevPath;
	String service="";
	String se;
	int fileid;
	String folderpath="";
	String IsFolder="";
	
	static String jsonString="";
	
	 DBController dbc=new DBController(this);
	 MyCustomBaseAdapterForDropbox dropboxadapt;
	 
	 LiveConnectClient client;
	 SkydriveBean sb=new SkydriveBean();
	 
	
	public void onCreate(Bundle DropboxFolderList)
	{
		
		super.onCreate(DropboxFolderList);
	
		setContentView(R.layout.filelist);
		
		if (android.os.Build.VERSION.SDK_INT > 9) 
		{
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		
		ArrayList<DropboxDataBean> list = getData("0");
		
		
		
		lv = (ListView)findViewById(R.id.skydrive_list);
		et=(EditText)findViewById(R.id.inputSearch);
		path=(TextView)findViewById(R.id.filepath);
		back=(Button)findViewById(R.id.back);
		search=(Button)findViewById(R.id.search);
		path.setTextSize(14);
	    path.setTextColor(Color.BLUE);
		
		prevId=new Stack<Integer>();
		prevPath=new Stack<String>();
		prevPath.push("HOME");
		prevId.push(0);
		back.setEnabled(false);
		dropboxadapt=new MyCustomBaseAdapterForDropbox(DropboxFolderList.this, list);
        //arrayAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Dropbox_List);
        lv.setAdapter(dropboxadapt);
        //lv.setAdapter(arrayAdapter); 
	
        back.setOnClickListener(this);
        registerForContextMenu(lv);
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id)
			{
				
				
				Object o = parent.getItemAtPosition(position);
			       DropboxDataBean fullObject = (DropboxDataBean)o;
			       service=fullObject.getFilename();
			       fileid=fullObject.getFileid();
			       IsFolder=fullObject.getIsfolder();
			       if(fullObject.getIsfolder().equalsIgnoreCase("Folder"))
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
        	//DropboxFolderList.this.arrayAdapter.getFilter().filter(cs);
        	if(et.getText().toString().equalsIgnoreCase("") ||et.getText().toString().equalsIgnoreCase(null)||et.getText().toString().length()==0)
        	{
        		ArrayList<DropboxDataBean> refreshed= getData("0");
        	    dropboxadapt=new MyCustomBaseAdapterForDropbox(DropboxFolderList.this, refreshed);
      	        
      	        lv.setAdapter(dropboxadapt);
      	        
      	        dropboxadapt.notifyDataSetChanged();
      	        prevPath.removeAllElements();
      	        prevId.removeAllElements();
      	        prevPath.push("HOME");
     		    prevId.push(0);
     		    path.setText("HOME");
        		back.setEnabled(false);
        		
        	}
        	else
        		
        	{
        		ArrayList<DropboxDataBean> refreshed1= getSearchData(et.getText().toString());
        	    dropboxadapt=new MyCustomBaseAdapterForDropbox(DropboxFolderList.this, refreshed1);
      	        
      	        lv.setAdapter(dropboxadapt);
      	        
      	        dropboxadapt.notifyDataSetChanged();
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
            DropboxDataBean fullObject = (DropboxDataBean)o;
            
       if(fullObject.getIsfolder().equalsIgnoreCase("Folder"))
       {
    	   prevpath=path.getText().toString();
           
         
           int dropbox_id=fullObject.getFileid();
           
          
           
           ArrayList<DropboxDataBean> Results =getData(String.valueOf(dropbox_id));
           dropboxadapt=new MyCustomBaseAdapterForDropbox(DropboxFolderList.this, Results);
           
           lv.setAdapter(dropboxadapt);
           dropboxadapt.notifyDataSetChanged();
           path.setText(prevpath +"/"+ fullObject.getFilename());
          // prevPath.push("/" + fullObject.getFilename().toString());
           prevPath.push(path.getText().toString());
           prevId.push(dropbox_id);
           if(path.getText().equals("HOME"))
              	back.setEnabled(false);
              else
              	back.setEnabled(true);
   
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
	   	    
	   	    if(IsFolder.equalsIgnoreCase("Folder"))
	   	    return;
	   	    else
	   	    {
	   	    menu.add(Menu.NONE, CONTEXTMENU_OPTION1, 0, "Send To Skydrive");
	   	    menu.add(Menu.NONE,CONTEXTMENU_OPTION2,0,"Download From Dropbox");
	   	    menu.setHeaderIcon(R.drawable.dropbox_icon);
	   	    }
	   	 
	   	    
	   	}  
	
	 public boolean onContextItemSelected(MenuItem item)
		{ 
			 
		    // Get extra info about list item that was long-pressed
		    AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		    DBController dbc2=new DBController(this);
		    folderpath=dbc2.getDropboxFilePath(fileid);
		   
		   
		    
	        //mDBApi=db.getmDBApi();
	        
	        
	        client=sb.getClient();
		    // Perform action according to selected item from context menu
		    switch (item.getItemId()) 
		    {
		 
		    case CONTEXTMENU_OPTION1:
		       
		    	
		    	//new build_session().execute();
		    	
		    new DropboxInterTransfer().execute();	
		             
				
		        	Toast.makeText(getApplicationContext(),"File Transfered Successfully", Toast.LENGTH_LONG).show();
		        
				//dt.DownloadFromDropbox(folderpath, mDBApi, service);
				//st.UploadToSkydrive(service, client);
		        	
		        
		        
		        
		        break;
		    case CONTEXTMENU_OPTION2:
		    	
				new DownloadFromDropbox().execute();
				
		    	break;
		}
			return true;
	
	
		}
	 public ArrayList<DropboxDataBean> getSearchData(String FileName)
	 {
		 ArrayList<DropboxDataBean> list=new ArrayList<DropboxDataBean>();
		 DropboxDataBean ddb=null;
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
		    	
		    	ddb=new DropboxDataBean();
		        ddb.setFileid(data.getInt("_id"));
		        ddb.setFilename(data.getString("fileName"));
		        if(data.getInt("isFolder")==0)
		        ddb.setIsfolder("");
		        else
		        ddb.setIsfolder("Folder");
		        ddb.setFilepath(data.getString("folderPath"));
		        list.add(ddb);
		    }
		    jsonString="";
		    
			}
			catch(Exception e)
			{
				
			}
		 
		 return list;
		 
}	 
	 
	 public ArrayList<DropboxDataBean> getData(String Parentid)
	 {
		 ArrayList<DropboxDataBean> list=new ArrayList<DropboxDataBean>();
		 DropboxDataBean ddb=null;
		 try
			{
			ArrayList<NameValuePair> SkydriveMasterValuePairs = new ArrayList<NameValuePair>(2);
			SkydriveMasterValuePairs.add(new BasicNameValuePair("uId","40246148"));
			SkydriveMasterValuePairs.add(new BasicNameValuePair("parentId",Parentid));
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://10.0.2.2:8080/CloudConnectWebApp/FolderClickServlet");
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
		    	
		    	ddb=new DropboxDataBean();
		        ddb.setFileid(data.getInt("_id"));
		        ddb.setFilename(data.getString("fileName"));
		        if(data.getInt("isFolder")==0)
		        ddb.setIsfolder("");
		        else
		        ddb.setIsfolder("Folder");
		        ddb.setFilepath(data.getString("folderPath"));
		        list.add(ddb);
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
		
		
	           
		       ArrayList<DropboxDataBean> Results =getData(String.valueOf(present_id));
		       dropboxadapt=new MyCustomBaseAdapterForDropbox(DropboxFolderList.this, Results);
		       
		       lv.setAdapter(dropboxadapt);
		       dropboxadapt.notifyDataSetChanged();
		       path.setText(present_path);
			
			
			
		break;
		}
		
		// TODO Auto-generated method stub
		
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
	
	public String getDropboxUid() 
	{
		SharedPreferences prefs1 = getSharedPreferences("DropboxUid", 0);
		String key = prefs1.getString("id", null);
		return key;
		
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
	
	public class upload_skydrive extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected Void doInBackground(Void... arg0)
		{
			SkydriveTransfer st=new SkydriveTransfer();
			LiveConnectClient client1=LiveConnectHandler.client;
			st.UploadToSkydrive(service, client1);
			return null;
		}
		
	}
	public class DropboxInterTransfer extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog downloadProgressDialog =new ProgressDialog(DropboxFolderList.this);
		
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

	
	
	
	
	
	
	private class DownloadFromDropbox extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog downloadProgressDialog =new ProgressDialog(DropboxFolderList.this);
		
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
	        downloadProgressDialog.setMessage("Downloading..."+service);
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
            	String path="/Dropbox";
				dt.DownloadFromDropbox(path,folderpath,mDBApi, service);
				//new upload_skydrive().execute();
				downloadProgressDialog.dismiss();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //downloadProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Download Successful",Toast.LENGTH_LONG).show();
            //Looper.loop();
          
            
            return null;
		}
		
	}
}

