package org.android.blindphonebydroid.caller;

import java.util.ArrayList;
import java.util.List;

import org.android.blindphonebydroid.BlindListActivity;
import org.android.blindphonebydroid.BlindTouch;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class CallActivity extends BlindListActivity {
	
	private List<Item> data;
	private ListAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		String name = null;
		if(extras != null){
			name = extras.getString("name");
		}
		
		data = new ArrayList<Item>();
        populateContactList(name);
        adapter = new ListAdapter(this,data);
        loadCurrentTheme(adapter);
	    setListAdapter(adapter);
	    
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		super.onItemSelected(arg0, v, position, id);
	}
	
	private void selectedAction(){
		ListView l = this.getListView();
		int position = adapter.getSelectedPosition();
		Item item =  (Item) l.getItemAtPosition(position);
		
		String phoneNumber = Uri.decode(item.getCaption());
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber ));
		//Toast.makeText(this, phoneNumber+"",Toast.LENGTH_SHORT).show();
		startActivity(intent);
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
	
	private void populateContactList(String name) {
        // Build adapter with contact entries
		
		Cursor cursor = getContacts(name);
		if(cursor==null) return;
		if(cursor.moveToFirst()){
			final int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			int i=0;
			while(!cursor.isAfterLast()){
	        	i++;
	        	
	        	String phoneNumber = cursor.getString(phoneNumberIdx);
	        	//remove prefix country
	        	if(phoneNumber.startsWith("66")){
	        		phoneNumber = "0"+phoneNumber.substring(2);
	        	}
	        	else if(phoneNumber.startsWith("+66")){
	        		phoneNumber = "0" + phoneNumber.substring(3);
	        	}
	        	//reduce duplicate number
	        	boolean duplicate=false;
	        	for(Item item:data){
	        	    if(phoneNumber.equals(item.getCaption())){
	        	    	duplicate=true;
	        	    	break;
	        	    } 	
	        	}
	        	if(duplicate!=true) data.add(new Item(i,phoneNumber));
	        	
	        	cursor.moveToNext();
	        }
		}
        cursor.close();
	}

    /**
     * 
     * Obtains the contact list for the currently selected account.
     *
     * @return A cursor for for accessing the contact list.
     */
    private Cursor getContacts(String name)
    {
    	Uri uri = null;
    	String[] projection = null;
    	String selection = null;
    	String[] selectionArgs = null;
        String sortOrder = null;
        
        //Query HasPhonenumber
    	uri = ContactsContract.Contacts.CONTENT_URI;
    	projection = new String[]{ContactsContract.Contacts.HAS_PHONE_NUMBER};
    	selection = ContactsContract.Contacts.DISPLAY_NAME + " = '"+ name +"'";
    	selectionArgs = null;
        sortOrder = null;
          
    	Cursor hasPhone = managedQuery(uri,projection,selection,selectionArgs,sortOrder);
    	
    	if(hasPhone.moveToFirst()){
    		int phoneCount = Integer.parseInt(hasPhone.getString(hasPhone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
    		if(phoneCount <= 0) return null;
    	}else return null;
    	
    	//QueryPhonenumber
        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        projection = new String[] {
             ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        //selection = selection;
        selectionArgs = null;
        sortOrder = null;

        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
        
        
    }
}
