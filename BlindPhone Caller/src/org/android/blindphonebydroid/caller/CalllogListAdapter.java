package org.android.blindphonebydroid.caller;

import java.util.List;

import org.android.blindphonebydroid.SelectableListAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CalllogListAdapter extends SelectableListAdapter<CalllogItem> {

	public CalllogListAdapter(Context context,List<CalllogItem> objects,String backgroundColor,String selectColor){
		super(context,objects,backgroundColor,selectColor);
		setLayout(R.layout.calllog_item);
	}
	
	public CalllogListAdapter(Context context, int textViewResourceId,
			List<CalllogItem> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		//setLayoutByResource(R.layout.item,backgroundColor,selectColor);
		setLayout(R.layout.calllog_item);
	}
	
	public CalllogListAdapter(Context context,List<CalllogItem> objects) {
		// TODO Auto-generated constructor stub
		this(context,0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub	
		convertView = super.getView(position, convertView, parent);
		
		final CalllogItem item = getItem(position);
		
		final TextView captionView = (TextView) convertView.findViewById(R.id.itemCalllogCaption);
		final TextView captionView2 = (TextView) convertView.findViewById(R.id.itemCalllogCallDate);
		final ImageView callTypeView = (ImageView) convertView.findViewById(R.id.imageViewCallType);
		//final TextView captionView = (TextView) convertView.findViewById(android.R.id.text1);
		if(captionView != null){
			// change the row color based on selected state
			captionView.setText(item.getCaption());
			if(getSelectedPosition() == position){
				captionView.setTextAppearance(getContext(), R.style.Row_Selected);
				captionView.setTextColor(backgroundColor);
			}else{
	        	captionView.setTextAppearance(getContext(), R.style.Row_Unselected);
	        	captionView.setTextColor(fontColor);
	        }
		}    
		if(captionView2 != null){
			captionView2.setText(item.getDateString());
			if(getSelectedPosition() == position){
				captionView2.setTextColor(backgroundColor);
			}else{
	        	captionView2.setTextColor(fontColor);
	        }
		}
		if(callTypeView != null){
			switch(item.getCallType()){
			case android.provider.CallLog.Calls.INCOMING_TYPE:
				callTypeView.setImageResource(R.drawable.ic_list_incoming_call);
				break;
			case android.provider.CallLog.Calls.MISSED_TYPE:
				callTypeView.setImageResource(R.drawable.ic_list_phone);
				break;
			case android.provider.CallLog.Calls.OUTGOING_TYPE:
				callTypeView.setImageResource(R.drawable.ic_list_outgoing_call);
				break;
				
			}
		}
		
		return convertView;
	}

}
