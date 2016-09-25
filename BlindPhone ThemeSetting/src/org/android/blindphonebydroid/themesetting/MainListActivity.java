package org.android.blindphonebydroid.themesetting;

import java.util.ArrayList;
import java.util.List;

import org.android.blindphonebydroid.BlindListActivity;
import org.android.blindphonebydroid.BlindTheme;
import org.android.blindphonebydroid.BlindTouch;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainListActivity extends BlindListActivity{
	
	private List<Item> data;
	private ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		data = new ArrayList<Item>();
		
		BlindTheme theme = new BlindTheme();
		String[] nameTheme = theme.getNameTheme();
		if(nameTheme!=null){
			for(String name:nameTheme){
				data.add(new Item(0,name));
			}
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
	}
	
	private void selectedAction(){
		ListView l = this.getListView();
		int position = adapter.getSelectedPosition();
		Item item =  (Item) l.getItemAtPosition(position);
		
		//Toast.makeText(this,item.getCaption()+" selected",Toast.LENGTH_SHORT).show();
		
		if(item.getCaption().startsWith("theme")){
			BlindTheme theme = new BlindTheme();
			theme.setCurrentTheme(item.getCaption());
			loadCurrentTheme(adapter);
		}
		adapter.setSelectedPosition(position);   
		//Toast.makeText(this, adapter.getSelectedPosition()+"", Toast.LENGTH_SHORT).show();
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
		}
		return false;
	}
}
