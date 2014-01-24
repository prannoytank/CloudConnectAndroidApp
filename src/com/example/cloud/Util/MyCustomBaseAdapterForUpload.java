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

public class MyCustomBaseAdapterForUpload extends BaseAdapter
{
private static ArrayList<FileUploadBean> searchArrayList;
	
	private LayoutInflater mInflater;

	public MyCustomBaseAdapterForUpload(Context context, ArrayList<FileUploadBean> searchResults) 
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
			convertView = mInflater.inflate(R.layout.custom_row_view, null);
			holder = new ViewHolder();
			holder.txtFileName = (TextView) convertView.findViewById(R.id.filename);
			holder.txtIsFolder = (TextView) convertView.findViewById(R.id.isfolder);
			holder.txtFolderPath = (TextView) convertView.findViewById(R.id.filepath);
			
			holder.iv=(ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtFileName.setText(searchArrayList.get(position).getFilename());
		holder.txtIsFolder.setText(searchArrayList.get(position).getIsFolder());
		holder.txtFolderPath.setText(searchArrayList.get(position).getPath());
		
		
		if(searchArrayList.get(position).getIsFolder().equalsIgnoreCase("Folder"))
		{
			
			holder.iv.setVisibility(View.VISIBLE);
            holder.iv.setImageResource(R.drawable.folder_icon);		
		}	
			else
			holder.iv.setImageResource(R.drawable.fileicon);
		
		
		return convertView;
	}

	static class ViewHolder
	{
		TextView txtFileName;
		TextView txtIsFolder;
		TextView txtFolderPath;
		
		ImageView iv;
	}

}
