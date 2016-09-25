package org.android.blindphonebydroid.caller;

import java.util.ArrayList;
import java.util.List;

import org.android.blindphonebydroid.BlindListActivity;
import org.android.blindphonebydroid.BlindTouch;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainListActivity extends BlindListActivity{
	
	private List<Item> data;
	private ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		data = new ArrayList<Item>();
		
		String[] menuList = new String[]{"Caller","Contact","Call Log"};
		
		for(String menu:menuList){
			data.add(new Item(0,menu));
		}
        
        adapter = new ListAdapter(this,data);
        loadCurrentTheme(adapter);		
        setListAdapter(adapter);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		super.onItemSelected(arg0, v, position, id);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}
	
	private void selectedAction(){
		ListView l = this.getListView();
		int position = adapter.getSelectedPosition();
		Item item =  (Item) l.getItemAtPosition(position);
		
		Toast.makeText(this,item.getCaption()+" selected",Toast.LENGTH_SHORT).show();
		
		if(item.getCaption().equals("Caller")){
			startActivity(new Intent(Intent.ACTION_DIAL));
		}
		else{
			if(item.getCaption().equals("Contact")){
				startActivity(new Intent(this,ContactActivity.class));
			}
			else if(item.getCaption().equals("Call Log")){
				startActivity(new Intent(this,CallLogActivity.class));
			}
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouch(v, event);
		switch(blindTouch.getStateTouch()){
			case BlindTouch.isDOUBLE_TAB: 
				selectedAction();
				break;
			case BlindTouch.isUP:
				setSelected(adapter.getSelectedPosition()+1);
				break;
			case BlindTouch.isDOWN:
				setSelected(adapter.getSelectedPosition()-1);
				break;
			case BlindTouch.isLEFT:
				finish();
				break;
		}
		return false;
	}
}
