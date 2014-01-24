package com.example.cloud.Util;

import android.os.Parcel;
import android.os.Parcelable;

import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;

public class SkydriveBean implements Parcelable
{
	private LiveConnectSession session;
	private LiveConnectClient client;
	
	public SkydriveBean() 
	{ 
		;
		
	};
	
	
	public SkydriveBean(Parcel in)
	{
		readFromParcel(in);
	}
	
	public LiveConnectSession getSession()
	{
		return session;
	}
	
	public void setSession(LiveConnectSession session)
	{
		this.session = session;
	}
	
	public LiveConnectClient getClient()
	{
		return client;
	}
	
	public void setClient(LiveConnectClient client) 
	{
		this.client = client;
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
	dest.writeParcelable(client, flags);
		
		// TODO Auto-generated method stub
		
	}
	public void readFromParcel(Parcel in) 
	{
		 
		// readParcelable needs the ClassLoader
		// but that can be picked up from the class
		// This will solve the BadParcelableException
		// because of ClassNotFoundException
		client = in.readParcelable(LiveConnectClient.class.getClassLoader());
	}
	
	 @SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
		    	new Parcelable.Creator() {
		            @SuppressWarnings("unused")
					public LiveConnectClient createFromParcel(final LiveConnectSession in) 
		            {
		                return new LiveConnectClient(in);
		            }
		 
		            public LiveConnectClient[] newArray(int size) {
		                return new LiveConnectClient[size];
		            }

					@Override
					public Object createFromParcel(Parcel source) 
					{
						// TODO Auto-generated method stub
						return null;
					}
		        };

}
