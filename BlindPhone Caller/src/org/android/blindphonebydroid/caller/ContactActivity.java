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

public class ContactActivity extends BlindListActivity{
	
	private List<Item> data;
	private ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		data = new ArrayList<Item>();
		
		/*for(int i = 0; i < 30 ; i++){
			data.add(new Item(i,i+""));
		}*/
					
		populateContactList();
        
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
		
		if(item.getCaption().equals("Call Log")){
			startActivity(new Intent(this,CallLogActivity.class));
		}
		else{
			//Run CallAcitivity
			Bundle stats = new Bundle();
			Intent intent = new Intent(this,CallActivity.class);
			stats.putLong("ID",item.getId());
			stats.putString("name",item.getCaption());
			intent.putExtras(stats);
			startActivity(intent);
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
	
	private void populateContactList() {
        // Build adapter with contact entries
		Cursor cursor = getContacts();
		if(cursor==null) return;
		if(cursor.moveToFirst()){
			
			final int idIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			final int displayNameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			//final int photoIdx = cursor.getColumnIndex( ContactsContract.Contacts.Photo.);
	        
			while(!cursor.isAfterLast()){
	        	data.add(new Item(cursor.getLong(idIdx),cursor.getString(displayNameIdx)));
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
    private Cursor getContacts()
    {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                //ContactsContract.Contacts.PHOTO_ID
        };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" +
                "1" + "'"+" AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER +" > 0";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }
}
