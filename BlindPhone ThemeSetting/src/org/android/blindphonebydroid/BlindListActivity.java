package org.android.blindphonebydroid;

import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.app.ListActivity;
import android.app.Service;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;


/** List Activity for Blindness*/
public abstract class BlindListActivity extends ListActivity
		implements OnItemSelectedListener, OnTouchListener {
	
	/** Create mTts object from TTS class*/
	protected TTS mTts;
	/** Called when the activity is first created. */
	protected BlindTouch blindTouch;
	
	private boolean isAccessibilityEnable = false;
	
	 /**Initial Class 's Object*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN); // hide notification
															// bar
		ListView lv = this.getListView();
		lv.setOnItemSelectedListener(this);
		lv.setOnTouchListener(this);

		mTts = new TTS(this);
		AccessibilityManager accessibilityManager = ((AccessibilityManager) getSystemService(Service.ACCESSIBILITY_SERVICE));
		isAccessibilityEnable = accessibilityManager.isEnabled();
		
		blindTouch = new BlindTouch(this);
	}
	
	/**Re-initial Object after pause*/
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		AccessibilityManager accessibilityManager = ((AccessibilityManager) getSystemService(Service.ACCESSIBILITY_SERVICE));
		isAccessibilityEnable = accessibilityManager.isEnabled();
		
	}
	
	/**Load and Set theme from current theme that selected in BlindPhone ThemeSetting*/
	public <T> void loadCurrentTheme(SelectableListAdapter<T> adapter) {
		BlindTheme theme = new BlindTheme();
		String[] colorCode = theme.getThemeColorCode();
		if (colorCode != null) {
			this.getListView().setDivider(
					new ColorDrawable(Color
							.parseColor(colorCode[BlindTheme.DIVIDER_COLOR])));
			this.getListView().setDividerHeight(1);
			this.getListView().setBackgroundColor(
					Color.parseColor(colorCode[BlindTheme.BACKGROUND_COLOR]));

			adapter.setBackgroundColor(colorCode[BlindTheme.BACKGROUND_COLOR],
					colorCode[BlindTheme.SELECT_COLOR]);
			adapter.setFontColor(colorCode[BlindTheme.FONT_COLOR]);
			// theme.setCurrentTheme("theme0");
		}
	}
	/**Item Selected Event Listener*/
	@Override
	public void onItemSelected(AdapterView<?> l, View v, int position, long id) {
		// TODO Auto-generated method stub
		setSelected(position);
	}

	/**Nothing Selected Event Listener*/
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	/**List Item Click Event Listener*/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		setSelected(position);
	}
	
	/**Touch Event Listener*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		blindTouch.getTouch(v, event);
		return false;
	}
	
	/**Check Accessibility Mode is Enable*/
	public boolean getIsAccessibilityEnable(){
		return isAccessibilityEnable;
	}
	
	/**Set the selected position*/
	protected void setSelected(int position) {
		final SelectableListAdapter<?> adapter = (SelectableListAdapter<?>) this
				.getListAdapter();
		final ListView lv = this.getListView();

		final int fristVisibleIdx = lv.getFirstVisiblePosition();
		final int lastVisibleIdx = lv.getLastVisiblePosition();

		if (position < 0){
			position = 0;
		}
		
		if (position >= adapter.getCount()){
			position = adapter.getCount() - 1;
		}
		
		final int pos = position;

		lv.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (pos < fristVisibleIdx || pos > lastVisibleIdx) {
					lv.setSelection(pos);
				}
			}
		});

		adapter.setSelectedPosition(position);
		position = adapter.getSelectedPosition();

		if(isAccessibilityEnable){
			mTts.setSpeechRate(0.75F);
			mTts.speak(this.getListView().getItemAtPosition(position).toString(),
					true);
		}
	}
	
}