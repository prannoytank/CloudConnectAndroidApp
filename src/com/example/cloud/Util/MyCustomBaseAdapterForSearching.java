package com.example.cloud.Util;

import java.util.ArrayList;

import com.example.cloud.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomBaseAdapterForSearching extends BaseAdapter
{

private static ArrayList<SearchResults> searchArrayList;
	
	private LayoutInflater mInflater;

	public MyCustomBaseAdapterForSearching(Context context, ArrayList<SearchResults> searchResults) 
	{
		searchArrayList = searchResults;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount()
	{
		return searchArrayList.size();
	}

	public Object getItem(int position) 
	{
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
			convertView = mInflater.inflate(R.layout.central_search, null);
			holder = new ViewHolder();
			holder.txtFileName = (TextView) convertView.findViewById(R.id.filename);
		//	holder.txtIsFolder = (TextView) convertView.findViewById(R.id.isfolder);
			//holder.txtFolderPath = (TextView) convertView.findViewById(R.id.filepath);
			holder.txtFileId=(TextView)convertView.findViewById(R.id.file_id);
			holder.txtService=(TextView)convertView.findViewById(R.id.Service);
			holder.iv=(ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtFileName.setText(searchArrayList.get(position).getFilename());
		//holder.txtIsFolder.setText(searchArrayList.get(position).getIsFolder());
		//holder.txtFolderPath.setText(searchArrayList.get(position).getPath());
		//holder.txtFileId.setId(searchArrayList.get(position).getFileid());
		holder.txtService.setText(searchArrayList.get(position).getService());
		if(searchArrayList.get(position).getService().equalsIgnoreCase("Skydrive"))
		{
			holder.iv.setVisibility(View.VISIBLE);
			holder.iv.setImageResource(R.drawable.icon_skydrive);
		}
			else
				holder.iv.setImageResource(R.drawable.dropbox_icon);
		
		
		return convertView;
	}

	static class ViewHolder
	{
		TextView txtFileName;
		//TextView txtIsFolder;
		//TextView txtFolderPath;
		TextView txtFileId;
		TextView txtService;
		ImageView iv;
	
	}

}
