package org.android.blindphonebydroid.caller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.android.blindphonebydroid.BlindListActivity;
import org.android.blindphonebydroid.BlindTouch;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class CallLogActivity extends BlindListActivity {

	private List<CalllogItem> data;
	private CalllogListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		data = new ArrayList<CalllogItem>();
		populateContactList();
		adapter = new CalllogListAdapter(this, data);
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

	private void selectedAction() {
		ListView l = this.getListView();
		int position = adapter.getSelectedPosition();
		CalllogItem item = (CalllogItem) l.getItemAtPosition(position);

		String phoneNumber = Uri.decode(item.getPhoneNumber());
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNumber));
		// Toast.makeText(this, phoneNumber+"",Toast.LENGTH_SHORT).show();
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouch(v, event);
		switch (blindTouch.getStateTouch()) {
		case BlindTouch.isDOUBLE_TAB:
			selectedAction();
			break;
		case BlindTouch.isUP:
			setSelected(adapter.getSelectedPosition() + 1);
			break;
		case BlindTouch.isDOWN:
			setSelected(adapter.getSelectedPosition() - 1);
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
		if (cursor == null)
			return;
		if (cursor.moveToFirst()) {

			final int idIdx = cursor
					.getColumnIndex(android.provider.CallLog.Calls._ID);
			final int numberIdx = cursor
					.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
			final int typeIdx = cursor
					.getColumnIndex(android.provider.CallLog.Calls.TYPE);
			final int dateIdx = cursor
					.getColumnIndex(android.provider.CallLog.Calls.DATE);
			final int nameIdx = cursor
					.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
			
			while (!cursor.isAfterLast()) {
				
			
				String phoneNumber = cursor.getString(numberIdx);
				String nameContact = cursor.getString(nameIdx);
				if(nameContact==null) nameContact = phoneNumber;
				
				Long id = cursor.getLong(idIdx);

				int callType = cursor.getInt(typeIdx);
				long callDate = cursor.getLong(dateIdx);
				
				SimpleDateFormat df  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = df.format(callDate);

				boolean duplicate = false;
				for (CalllogItem item : data) {
					if (phoneNumber.equals(item.getCaption())) {
						duplicate = true;
						break;
					}
				}
				if (duplicate != true)
					data.add(new CalllogItem(id,nameContact,phoneNumber,dateString,callType));
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
	private Cursor getContacts() {
		// Run query
		Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
		String[] projection = new String[] {
				android.provider.CallLog.Calls._ID,
				android.provider.CallLog.Calls.CACHED_NAME,
				android.provider.CallLog.Calls.NUMBER,
				android.provider.CallLog.Calls.TYPE,
				android.provider.CallLog.Calls.DATE, };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";

		return managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}

}
