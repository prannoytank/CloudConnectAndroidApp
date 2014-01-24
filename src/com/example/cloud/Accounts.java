package com.example.cloud;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.example.cloud.Util.CloudConnectionSelectionBean;
import com.example.cloud.Util.CloudConnectionStatus;
import com.example.cloud.Util.DropboxApiKeys;
import com.example.cloud.Util.LiveConnectHandler;
import com.example.cloud.Util.MyCustomBaseAdapterForAccounts;
import com.example.cloud.Util.MyCustomBaseAdapterForDropbox;
import com.example.cloud.database.DBController;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Accounts extends Activity
{
    ListView ListOfService,ConnectedService;
     String sky_id = null;
	// String username = null;
    ArrayList<String> list=new ArrayList<String>();
    ArrayList<String> l=new ArrayList<String>();
    String sky="Skydrive";
	String drop="Dropbox";
	String empty="Empty";
	private LiveAuthClient auth;
	
	private LiveConnectClient client;
	private static final String HOME_FOLDER = "me/skydrive";
	DropboxAPI<AndroidAuthSession> mDBApi;
	String mCurrentFolderId;
	DBController controller = new DBController(this);
	int file_id=0;
	int dropbox_file_id=0;
	MyCustomBaseAdapterForAccounts accounts;
	ArrayList<CloudConnectionSelectionBean> allservice=new ArrayList<CloudConnectionSelectionBean>();
	String uid1;
	String token_key;
	String token_value;
	String username;
	String accessToken;
	String refreshToken;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts_layout);
		
		if (android.os.Build.VERSION.SDK_INT > 9) 
		{
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		
		
		
		ListOfService=(ListView)findViewById(R.id.ListOfService);
		ConnectedService=(ListView)findViewById(R.id.ConnectedService);
		l.add(drop);
		l.add(sky);
		this.auth = new LiveAuthClient(this,Config.CLIENT_ID);
		
		list.add(empty);
		//ConnectedService.setAdapter(new ArrayAdapter<String>(Accounts.this,R.layout.listofservice,R.id.list_of_service,list));
		ListOfService.setAdapter(new ArrayAdapter<String>(Accounts.this,R.layout.listofservice,R.id.list_of_service, l));
		
		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		
		ConnectedService.setOnItemClickListener(new OnItemClickListener() 
		 {
	          public void onItemClick(AdapterView<?> parent1, View view,
	              int position, long id) 
	          {
	 
	              // selected item
	              String product1 = parent1.getItemAtPosition(position).toString();
	               if(product1.equalsIgnoreCase("Dropbox"))
	               {
	            	   
	            	   Toast.makeText(getApplicationContext(), "ABC",Toast.LENGTH_LONG).show();
	            	   
	               }
	               else if(product1.equalsIgnoreCase("Skydrive"))
	               {
	            	   
	            	   
	               }
	              // Launching new Activity on selecting single List Item
	           //Toast.makeText(getBaseContext(), product, Toast.LENGTH_LONG).show();
	              
	          }
	        });
	
		
		
		
		
		
		
		
		
		
		
		
		ListOfService.setOnItemClickListener(new OnItemClickListener() 
		 {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) 
	          {
	 
	              // selected item
	              String product = parent.getItemAtPosition(position).toString();
	               if(product.equalsIgnoreCase("Dropbox"))
	               {
	            	   
	            	   ConnectToDropbox();
	            	   Toast.makeText(getBaseContext(),"Dropbox is now Connected", Toast.LENGTH_LONG).show();
	                   CloudConnectionStatus.isDropboxConnected=true;
	               }
	               else if(product.equalsIgnoreCase("Skydrive"))
	               {
	            	   
	            	   ConnectToSkydrive();
	            	   Toast.makeText(getBaseContext(),"Skydrive is now Connected", Toast.LENGTH_LONG).show();
	            	   CloudConnectionStatus.isDropboxConnected=true;
	               }
	              
	              
	          }
	        });
	}
	
	public class GetSkydriveList extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog downloadProgressDialog =new ProgressDialog(Accounts.this);
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			//skydrive_folder.clear();
			getFolder(HOME_FOLDER,0);
			return null;
		}
		
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
			//downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        downloadProgressDialog.setMessage("Getting files and folders from skydrive");
	        downloadProgressDialog.setCancelable(false);
	        downloadProgressDialog.show();
			
		}
		
		
		
		
	}		
	public void getFolder(String folderId,final int id)
	{
		 	assert folderId != null;
	        mCurrentFolderId = folderId;
	        folderId=folderId+"/files";
	        
	        final int parent_id =id;
	    
	    client.getAsync("me", new LiveOperationListener()
	    {

			@Override
			public void onComplete(LiveOperation operation)
			{
				JSONObject result = operation.getResult();
				
				
				
					String id=result.optString("id");
					String skydrive_email=result.optString("name");
				
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(LiveOperationException exception,
					LiveOperation operation)
			{
				// TODO Auto-generated method stub
				
			}
	    	
	    });    
	        
	        
	/*	client.getAsync(folderId,new LiveOperationListener()
        {
			int i;
			@Override
			public void onComplete(LiveOperation operation)
			{
				JSONObject result = operation.getResult();
				
				JSONArray data = result.optJSONArray("data");
				
				
				
				i=0;
				
				for(i=0;i<data.length();i++)
				{
					try {
						
						org.json.JSONObject data1=data.optJSONObject(i);
						Log.i("Type:",data1.getString("type"));
						if(data1.getString("type").equalsIgnoreCase("folder"))
						{
							controller.insertSkydriveData(++file_id,data1.getString("name"),data1.getString("id"),parent_id,"true",data1.getString("id"));
							
							
						
							Log.i("ParentIdForFolder",""+parent_id);
							getFolder(data1.getString("id"),file_id);
							
							
							
						}
						else
						{
							
							
							Log.i("ParentIdForFile",""+parent_id);
							
							controller.insertSkydriveData(++file_id,data1.getString("name"),"null",parent_id,"false",data1.getString("id"));
						
						}
					}
					catch (JSONException e) 
					
					{
						
						Log.i("Json Error",e.getMessage());
						e.printStackTrace();
					}
				}
				
			}

			@Override
			public void onError(LiveOperationException exception,
					LiveOperation operation) 
			{
				Log.i("Skydrive Folder Error",exception.getMessage());
				
			}
        });*/
	}	
	
public void ConnectToDropbox()
{
	AlertDialog.Builder skydrivelist = new AlertDialog.Builder(Accounts.this);
	skydrivelist.setTitle("Connect");
	skydrivelist.setMessage("Connect To Dropbox").setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener()
	{
			public void onClick(DialogInterface dialog,int id) 
			{
				//ArrayList<CloudConnectionSelectionBean> dropbox =new ArrayList<CloudConnectionSelectionBean>();
				
				
				
				//list.add("Dropbox");
				mDBApi.getSession().startAuthentication(Accounts.this);
				l.remove(drop);
				
				
				//ConnectedService.setAdapter(new ArrayAdapter<String>(Accounts.this,R.layout.connectcloud,R.id.Cloud_Service,list));
				//accounts=new MyCustomBaseAdapterForAccounts(Accounts.this,allservice);
				//ConnectedService.setAdapter(accounts);
				ListOfService.setAdapter(new ArrayAdapter<String>(Accounts.this,R.layout.listofservice,R.id.list_of_service, l));	
				
			}
		  })
		.setNegativeButton("No",new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog,int id) 
			{
				
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog skydriveDialog = skydrivelist.create();

		// show it
		skydriveDialog.show();
	}
	
	

	
public void ConnectToSkydrive()
{
	AlertDialog.Builder skydrivelist = new AlertDialog.Builder(Accounts.this);
	skydrivelist.setTitle("Connect");
	skydrivelist.setMessage("Connect To Skydrive").setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener()
	{
			public void onClick(DialogInterface dialog,int id) 
			{
				
				try
				{
				
				auth.login(Accounts.this, Arrays.asList(Config.SCOPES),new LiveAuthListener() 
				{
					
					@Override
					public void onAuthError(LiveAuthException exception, Object userState)
					{
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAuthComplete(LiveStatus status, LiveConnectSession session,
							Object userState)
					{
						if (status == LiveStatus.CONNECTED)
						{
							  
							client = new LiveConnectClient(session);
						    accessToken=session.getAccessToken();
							refreshToken=session.getRefreshToken();
							LiveConnectHandler.client=client;
							
							
							
							Log.i("message", client.toString());
							Log.i("AccessToken",accessToken);
							LiveConnectHandler.client=client;
							CloudConnectionStatus.isSkydriveConnected=true;
							
							
							
							
							
							client.getAsync("me", new LiveOperationListener()
						    {

								@Override
								public void onComplete(LiveOperation operation)
								{
									JSONObject result = operation.getResult();
									
									
									
										  sky_id=result.optString("id");
										  username=result.optString("name");
										  Toast.makeText(getApplicationContext(), sky_id +":"+ username,Toast.LENGTH_LONG).show();
										 
										  l.remove(sky);
											CloudConnectionSelectionBean ccsb1=new CloudConnectionSelectionBean();
											ccsb1.setCloud_type("Skydrive");
											ccsb1.setEmailid("prannoy23@gmail.com");
											ccsb1.setService("Skydrive");
											allservice.add(ccsb1);
											accounts=new MyCustomBaseAdapterForAccounts(Accounts.this,allservice);
											ConnectedService.setAdapter(accounts);
											ListOfService.setAdapter(new ArrayAdapter<String>(Accounts.this,R.layout.listofservice,R.id.list_of_service, l));
										  
											ArrayList<NameValuePair> SkydriveMasterValuePairs = new ArrayList<NameValuePair>(4);
											SkydriveMasterValuePairs.add(new BasicNameValuePair("UserName",username));
											SkydriveMasterValuePairs.add(new BasicNameValuePair("uid",sky_id));
											SkydriveMasterValuePairs.add(new BasicNameValuePair("Access",accessToken));
											SkydriveMasterValuePairs.add(new BasicNameValuePair("Refresh",refreshToken));
									        HttpClient httpclient = new DefaultHttpClient();
									        HttpPost httppost = new HttpPost("http://10.0.2.2:8080/CloudConnectWebApp/SkydriveLoginServlet");
									        try {
												httppost.setEntity(new UrlEncodedFormEntity(SkydriveMasterValuePairs));
											} catch (UnsupportedEncodingException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
									        try {
												HttpResponse response = httpclient.execute(httppost);
											} catch (ClientProtocolException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										  
										  //sky_id=Sky_id;
									// TODO Auto-generated method stub
										  
								}
								
								@Override
								public void onError(LiveOperationException exception,
										LiveOperation operation) 
								{
									// TODO Auto-generated method stub
									
								}
						    });
							
							
						// TODO Auto-generated method stub
						
					   }
					}
				});
				
				//dropbox.add(ccsb);
					
				}
				catch(Exception e)
				{
					
				}
				finally
				{
					//new addSkydriveData().execute();
				}
			}
		  })
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{
				
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog skydriveDialog = skydrivelist.create();

		// show it
		skydriveDialog.show();
	}
@Override
protected void onResume() 
{
	super.onResume();
	
	
	AndroidAuthSession session = buildSession();
	
	if (session.authenticationSuccessful()) 
	{
		try {
			
			
			
			// MANDATORY call to complete auth.
			// Sets the access token on the session
			session.finishAuthentication();
			TokenPair tokens = session.getAccessTokenPair();
			storeKeys(tokens.key, tokens.secret);
			long uid=mDBApi.accountInfo().uid;
			 uid1=String.valueOf(uid);
			 token_key=tokens.key;
			 token_value=tokens.secret;
			 username=mDBApi.accountInfo().displayName;
			storeDropboxUid(uid1);
			
           
            CloudConnectionSelectionBean ccsb=new CloudConnectionSelectionBean();
			ccsb.setCloud_type("Dropbox");
			ccsb.setEmailid(username);
			ccsb.setService("Dropbox");
			//dropbox.add(ccsb);
			allservice.add(ccsb);
			
			accounts=new MyCustomBaseAdapterForAccounts(Accounts.this,allservice);
			ConnectedService.setAdapter(accounts);
			File dboxfolder=new File(Environment.getExternalStorageDirectory().getPath(),"Dropbox");
			if(dboxfolder.exists())
			{
				
			}
			else
			dboxfolder.mkdir();
		//	new GetDropboxList().execute();
			new addDropboxData().execute();
		} 
		catch (Exception e) 
		{
			System.out.print(e);
			Log.i("button_selection", "Error authenticating", e);
		}
	}
}
public void storeDropboxUid(String id) 
{
	SharedPreferences prefs1 = getSharedPreferences("DropboxUid", 0);
	Editor edit = prefs1.edit();
	edit.putString("id",id);
	
	edit.commit();
}

public class addDropboxData extends AsyncTask<Void,Void,Void>
{
	JSONObject jsonObject;
    JSONObject meta;  
    String limit;
    ProgressDialog downloadProgressDialog =new ProgressDialog(Accounts.this);
	@Override
	protected void onPostExecute(Void result)
	{
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		downloadProgressDialog.dismiss();
		Toast.makeText(getApplicationContext(), limit, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPreExecute()
	{
		// TODO Auto-generated method stub
		super.onPreExecute();
		downloadProgressDialog.setMessage("Getting files and folders from Dropbox");
        downloadProgressDialog.setCancelable(false);
        downloadProgressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... arg0) 
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("UserName",username));
        nameValuePairs.add(new BasicNameValuePair("uid",uid1));
        nameValuePairs.add(new BasicNameValuePair("key",token_key));
        nameValuePairs.add(new BasicNameValuePair("secret",token_value));
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://10.0.2.2:8080/CloudConnectWebApp/DropboxLoginServlet");
        try {
			
        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
	        InputStream is = entity.getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) 
            {
                sb.append((line + "\n"));
            }
             jsonObject = new JSONObject(sb.toString());
             meta = jsonObject.getJSONObject("id");  
            limit = meta.getString("MaxId");
        	
		} 
        catch (UnsupportedEncodingException e)
        
       {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
			

	        
	  
		
		
		
		
		// TODO Auto-generated method stub
		return null;
        
		
	}
}

public class addSkydriveData extends AsyncTask<Void,Void,Void>
{
     
	@Override
	protected Void doInBackground(Void... params) 
	{
		ArrayList<NameValuePair> SkydriveMasterValuePairs = new ArrayList<NameValuePair>(4);
		SkydriveMasterValuePairs.add(new BasicNameValuePair("UserName",username));
		SkydriveMasterValuePairs.add(new BasicNameValuePair("uid",sky_id));
		SkydriveMasterValuePairs.add(new BasicNameValuePair("Access",accessToken));
		SkydriveMasterValuePairs.add(new BasicNameValuePair("Refresh",refreshToken));
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://10.0.2.2:8080/CloudConnectWebApp/SkydriveLoginServlet");
        try {
			httppost.setEntity(new UrlEncodedFormEntity(SkydriveMasterValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			HttpResponse response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
	}
}




public void storeKeys(String key, String secret) 
{
	SharedPreferences prefs1 = getSharedPreferences(DropboxApiKeys.ACCOUNT_PREFS_NAME, 0);
	Editor edit = prefs1.edit();
	edit.putString(DropboxApiKeys.ACCESS_KEY_NAME, key);
	edit.putString(DropboxApiKeys.ACCESS_SECRET_NAME, secret);
	edit.commit();
}

// * Creating new session or getting already stored keys and secret *//
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
private class GetDropboxList extends AsyncTask<Void, Void, Void>
{
	ProgressDialog downloadProgressDialog =new ProgressDialog(Accounts.this);
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
		//downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgressDialog.setMessage("Getting files and folders from Dropbox");
        downloadProgressDialog.setCancelable(false);
        downloadProgressDialog.show();
		
	}
	
	
	
	
	@Override
	protected Void doInBackground(Void... arg0) 
	{
		//dboxfilelist.clear();
		Entry list = null;
		try {
			
			list = mDBApi.metadata("/",1000, null, true, null);
			listFilesForFolder(list,dropbox_file_id);
			
			
			
		} 
		catch (DropboxException e) 
		{
			
			e.printStackTrace();
		}
		
		return null;
	}
	
}	

private void listFilesForFolder(Entry list,int fileid) throws DropboxException
{
	
	final int parentId =fileid;
	for (Entry ent : list.contents) 
	{
		if (ent.isDir) 
		{
			
			Entry list1 = mDBApi.metadata(ent.path,1000, null,true,null);
			controller.insertDropboxData(++dropbox_file_id,ent.fileName(), parentId,ent.path,"true");
			listFilesForFolder(list1,dropbox_file_id);
			
		}
		else 
		{
			controller.insertDropboxData(++dropbox_file_id,ent.fileName(), parentId, "null","false");
			Log.i("List", ent.fileName());
		}
	}

}
	
/*public void onAuthComplete(LiveStatus status, LiveConnectSession session,Object userState) throws ClientProtocolException, IOException
{
	Log.i("status",status.name().toString());
	if (status == LiveStatus.CONNECTED)
	{
		  
		client = new LiveConnectClient(session);
	    accessToken=session.getAccessToken();
		refreshToken=session.getRefreshToken();
		LiveConnectHandler.client=client;
		
		
		
		Log.i("message", client.toString());
		LiveConnectHandler.client=client;
		CloudConnectionStatus.isSkydriveConnected=true;
		
		
		
		CloudConnectionSelectionBean ccsb1=new CloudConnectionSelectionBean();
		ccsb1.setCloud_type("Skydrive");
		ccsb1.setEmailid("prannoy23@gmail.com");
		ccsb1.setService("Skydrive");
		//dropbox.add(ccsb);
		allservice.add(ccsb1);
		accounts=new MyCustomBaseAdapterForAccounts(Accounts.this,allservice);
		ConnectedService.setAdapter(accounts);*/
		
		/*client.getAsync("me", new LiveOperationListener()
	    {

			@Override
			public void onComplete(LiveOperation operation)
			{
				JSONObject result = operation.getResult();
				
				
				
					 sky_id=result.optString("id");
					  username=result.optString("name");
				//sky_id=Sky_id;
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(LiveOperationException exception,
					LiveOperation operation) {
				// TODO Auto-generated method stub
				
			}
	    });*/
		
		
		//new GetSkydriveList().execute();
		//txtskydrive.setText("Connected");
		//skydrivefolder.setEnabled(true);
		//isSkydriveConnected=true;
		
		/*File skydrivefolder=new File(Environment.getExternalStorageDirectory().getPath(),"Skydrive");
		if(skydrivefolder.exists())
		{
			
		}
		else
		skydrivefolder.mkdir();
		//new addSkydriveData().execute();
	} 
	else
	{
		//txtskydrive.setText("Not Connected");
		Toast.makeText(getApplicationContext(), "Not Signed In",Toast.LENGTH_LONG).show();
		// this.resultTextView.setText("Not signed in.");
		client = null;
	}


}

public void onAuthError(LiveAuthException exception, Object userState) 
{
	Log.i("Error", exception.getMessage());
	
}*/
}


