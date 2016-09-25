package org.android.blindphonebydroid;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/** ListAdapter for use in BlindListActivity */
public class SelectableListAdapter<T> extends ArrayAdapter<T> {

	private LayoutInflater li;
	private int selectedPos = 0;
	private Context mContext;

	/**Layout Resource Id*/
	protected int resIdLayout;
	/**Background color value*/
	protected int backgroundColor;
	/**Highlight color value*/
	protected int selectColor;
	/**Font color value*/
	protected int fontColor;
	
	 /**Initial Class 's Object*/
	public SelectableListAdapter(Context context,List<T> objects) {
		// TODO Auto-generated constructor stub
		this(context,0,objects);
	}
	
	 /**Initial Class 's Object and set background color and highlight color*/
	public SelectableListAdapter(Context context,List<T> objects,String backgroundColor,String selectColor){
		this(context,0,objects);
		
		
		mContext = context;
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setLayout(android.R.layout.simple_list_item_1,backgroundColor,selectColor);
		
		//setLayoutColor(backgroundColor,selectColor);
	}
	
	/**Initial Class 's Object*/
	public SelectableListAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mContext = context;
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setLayout(android.R.layout.simple_list_item_1,Color.BLACK,Color.CYAN);
	}
	
	/**Set Layout and Color by Resource ID*/
	public void setLayoutByResource(int layoutResId,int resIdBackground,int resIdSelect){
		String colorBackground = mContext.getResources().getString(resIdBackground);
		String colorSelect = mContext.getResources().getString(resIdSelect);
	
		setLayout(layoutResId,colorBackground,colorSelect);
	}
	
	/**Set Layout by Resource ID*/
	public void setLayout(int resIdLayout){
		setLayout(resIdLayout,this.backgroundColor,this.selectColor);
	}
	
	/**Set Layout by Resource ID and Color by String ex. "#FFFFF","#000000"*/
	public void setLayout(int resIdLayout,String backgroundColor,String selectColor){
		setLayout(resIdLayout,Color.parseColor(backgroundColor),Color.parseColor(selectColor));
	}
	
	/**Set Layout by Resource ID and Color by Color value*/
	public void setLayout(int resIdLayout,int backgroundColor,int selectColor){
		this.resIdLayout = resIdLayout;
		this.backgroundColor  = backgroundColor;
		this.selectColor = selectColor;		
		notifyDataSetChanged();
	}
	
	/**Set Font Color by String ex. "#FFFFF","#000000"*/
	public void setFontColor(String fontColor){
		setFontColor(Color.parseColor(fontColor));		
	}
	
	/**Set Font Color by Color value*/
	public void setFontColor(int fontColor){
		this.fontColor = fontColor;
		notifyDataSetChanged();
	}
	
	/**Set Background & Highlight Color by String ex. "#FFFFF","#000000"*/
	public void setBackgroundColor(String backgroundColor,String selectColor){
		setBackgroundColor(Color.parseColor(backgroundColor),Color.parseColor(selectColor));
	}
	
	/**Set Background & Highlight Color by Color value*/
	public void setBackgroundColor(int backgroundColor,int selectColor){
		this.backgroundColor  = backgroundColor;
		this.selectColor = selectColor;		
		notifyDataSetChanged();
	}
		
	/**Get a View that displays the data at the specified position in the data set*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		
		if(v == null){
			v = li.inflate(resIdLayout,null);			
		}
		if(selectedPos == position){
				v.setBackgroundColor(selectColor);
	    }else{
	        	v.setBackgroundColor(backgroundColor);
	    }
		return v;
	}
	
	/**Set the position of selected item*/
	public void setSelectedPosition(int pos){
		if(pos < 0) pos = 0;
		if(pos == this.getCount()) pos = this.getCount()-1;
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	/**Get the position of selected item*/
	public int getSelectedPosition(){
		return selectedPos;
	}
}
