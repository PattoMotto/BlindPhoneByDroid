package org.android.blindphonebydroid.themesetting;

import java.util.List;

import org.android.blindphonebydroid.SelectableListAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListAdapter extends SelectableListAdapter<Item> {

	public ListAdapter(Context context,List<Item> objects,String backgroundColor,String selectColor){
		super(context,objects,backgroundColor,selectColor);
		setLayout(R.layout.item);
	}
	
	public ListAdapter(Context context, int textViewResourceId,
			List<Item> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		//setLayoutByResource(R.layout.item,backgroundColor,selectColor);
		setLayout(R.layout.item);
	}
	
	public ListAdapter(Context context,List<Item> objects) {
		// TODO Auto-generated constructor stub
		this(context,0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub	
		convertView = super.getView(position, convertView, parent);
		
		final Item item = getItem(position);
		
		final TextView captionView = (TextView) convertView.findViewById(R.id.itemCaption);
		//final TextView captionView = (TextView) convertView.findViewById(android.R.id.text1);
		if(captionView != null){
			// change the row color based on selected state
			captionView.setText(item.toString());
			if(getSelectedPosition() == position){
				//captionView.setTextAppearance(getContext(), R.style.Row_Selected);
				captionView.setTextColor(backgroundColor);
			}else{
	        	//captionView.setTextAppearance(getContext(), R.style.Row_Unselected);
	        	captionView.setTextColor(fontColor);
	        }
		}    
		
		return convertView;
	}

}
