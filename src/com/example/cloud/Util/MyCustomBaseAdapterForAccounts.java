package com.example.cloud.Util;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cloud.R;


public class MyCustomBaseAdapterForAccounts extends BaseAdapter 
{
	private static ArrayList<CloudConnectionSelectionBean> searchArrayList;
	
	private LayoutInflater mInflater;

	public MyCustomBaseAdapterForAccounts(Context context, ArrayList<CloudConnectionSelectionBean> searchResults) 
	{
		searchArrayList = searchResults;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return searchArrayList.size();
	}

	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.connectcloud, null);
			holder = new ViewHolder();
			holder.txtService = (TextView) convertView.findViewById(R.id.Cloud_Service);
			holder.txtEmailId= (TextView) convertView.findViewById(R.id.EmailId);
		    holder.iv=(ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtService.setText(searchArrayList.get(position).getService());
		holder.txtEmailId.setText(searchArrayList.get(position).getEmailid());
		
		
		if(searchArrayList.get(position).getService().equalsIgnoreCase("Dropbox"))
		{
			
			holder.iv.setVisibility(View.VISIBLE);
            holder.iv.setImageResource(R.drawable.dropbox_icon);		
		}	
			else
			holder.iv.setImageResource(R.drawable.icon_skydrive);
		
		
		return convertView;
	}

	static class ViewHolder
	{
		TextView txtService;
		TextView txtEmailId;
		ImageView iv;
	}
}
