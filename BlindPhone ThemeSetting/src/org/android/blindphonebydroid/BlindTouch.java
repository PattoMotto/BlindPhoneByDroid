/*
 * 	There 3 classes in this file
 * 1. public class blindTouch
 * 2. class WrapMotionEvent
 * 3. class EclairMotionEvent
 */
package org.android.blindphonebydroid;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/** Touch Calculate Event for Blindness */
public class BlindTouch {

	//Set debug tag in LogCat
	private static final String TAG = "Touch";
	
	// These matrices will be used to move and zoom image
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	// Control how to detect any gesture
	private static final long MIN_DIRECTION_DURATION = 150;
	private static final long DOUBLE_TAB_DURATION = 400;
	private static final long LONG_PRESS_DURATION = 1500;
	private static final float MIN_DISTANCE = 20;//must move more than 20 distance to detect gesture 
	private static final float MAX_DIRECTION_DISTANCE = 150;//must move more than 150 distance to send continuous state ex isUp

	// RETURN CODE
	/**Return on Event: SINGLE_TAB*/
	public static final int isSINGLE_TAB = 1;
	/**Return on Event: DOUBLE_TAB*/
	public static final int isDOUBLE_TAB = 2;
	/**Return on Event: Swipe Down*/
	public static final int isDOWN = 3;
	/**Return on Event: Swipe Up*/
	public static final int isUP = 4;
	/**Return on Event: Swipe Left*/
	public static final int isLEFT = 5;
	/**Return on Event: Swipe Right*/
	public static final int isRIGHT = 6;
	/**Return on Event: LongPress*/
	public static final int isLONG_PRESS = 7;
	
	/**Return on Event: LongPress then Swipe Down*/
	public static final int isLONG_PRESS_DOWN = 13;
	/**Return on Event: LongPress then Swipe Up*/
	public static final int isLONG_PRESS_UP = 14;
	/**Return on Event: LongPress then Swipe Left*/
	public static final int isLONG_PRESS_LEFT = 15;
	/**Return on Event: LongPress then Swipe Right*/
	public static final int isLONG_PRESS_RIGHT = 16;
	
	/**Return on Event: Press 3 point*/
	public static final int is3_POINT = 103;
	/**Return on Event: Press 4 point*/
	public static final int is4_POINT = 104;
	/**Return on Event: Press 5 point*/
	public static final int is5_POINT = 105;
	
	private boolean isDoubleTab = false;
	private boolean isLongPress = false;
	private int mode = NONE;
	private float x1, x2, xDistance;
	private float y1, y2, yDistance;
	private long touchDuration = 0; // Chk Long Press
	private int tmp;
	private int pointerCount;
	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private float oldDist = 1f;
	private boolean debug_mode = false;
	private Context mContext = null;
	private int stateTouch = 0;
	
	 /**Initial Class 's Object*/
	public BlindTouch(Context mContext) {
		this.mContext = mContext;	
		}
	
	//public int getTouch(View v, MotionEvent event) {
	//	return getTouch(v, event, false);
	//}
	/**Recognize all Touch State*/
	public int getTouch(View v, MotionEvent event) {
		WrapMotionEvent warpEvent = WrapMotionEvent.wrap(event);
		//Detect 3,4,5 point
		pointerCount = event.getPointerCount();
		switch (pointerCount) {
		case 3:
			stateTouch = returnCenter("is3_POINT", is3_POINT);
			break;
		case 4:
			stateTouch = returnCenter("is4_POINT", is4_POINT);
			break;
		case 5:
			stateTouch = returnCenter("is5_POINT", is5_POINT);
			break;
		}
		
		if (pointerCount < 3)
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				
				start.set(event.getX(), event.getY());
				x1 = start.x;
				y1 = start.y;
				mode = DRAG;

				if (getTouchDuration() < DOUBLE_TAB_DURATION) {
					isDoubleTab = true;
					stateTouch = returnCenter("isDOUBLEs_TAB", isDOUBLE_TAB);
				}
				touchDuration = System.currentTimeMillis();
				break;
				//Case 2 pointer using for zoom
			case MotionEvent.ACTION_POINTER_DOWN:
				 Log.d(TAG, "oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, warpEvent);
					mode = ZOOM;
					Log.d(TAG, "mode=ZOOM");
				}
				break;
				//Case second pointer up
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				Log.d(TAG, "mode=NONE");
				break;
				//Case OnMove
			case MotionEvent.ACTION_MOVE:
				getLongPress(event);
				// =========================================
				start.set(event.getX(), event.getY());
				// MAX_DIRECTION_DISTANCE
				xDistance = Math.abs(x1 - start.x) - MAX_DIRECTION_DISTANCE;
				yDistance = Math.abs(y1 - start.y) - MAX_DIRECTION_DISTANCE;
				if (xDistance > 0 || yDistance > 0) 
					getDirection(event);
				

				if (mode == DRAG) {
				} else if (mode == ZOOM) {
				}
				break;
			// DETECT DIRECTION
			case MotionEvent.ACTION_UP:
				isLongPress = false;
				getDirection(event);
			}
		// view.setImageMatrix(matrix);
		this.stateTouch = stateTouch;
		return 0;
	}

/**Recognize LongPress*/
	private void getLongPress(MotionEvent event) {
		if (getTouchDuration() > LONG_PRESS_DURATION && !isLongPress) {
			setChangeDirection(event);
			if (x2 < 50 && y2 < 50) {
				if (debug_mode)
					Toast.makeText(
							mContext,
							"[Long Press Detected]"
									+ (System.currentTimeMillis() - touchDuration),
							Toast.LENGTH_SHORT).show();
				tmp = isLONG_PRESS;
			}
			/*
			else {
				if (debug_mode)
					Toast.makeText(
							mContext,
							"[Long Press Detected But Out of range]"
									+ getTouchDuration(), Toast.LENGTH_SHORT)
							.show();
				tmp = isLONG_PRESS_OUT_OF_RANGE;
			}*/
			isLongPress = true;
			returnCenter("LONG_PRESS", tmp);

		}
	}

	/**Recognize Direction and also  LongPress+Direction*/
	private void getDirection(MotionEvent event) {
		if (!isDoubleTab && !isLongPress) {
			setChangeDirection(event);
			if (getTouchDuration() < MIN_DIRECTION_DURATION
					|| getTouchDuration() == 0) {
				// returnCenter("isSINGLE_TAB", isSINGLE_TAB);
			} else if (x1 < start.x && x2 >= y2 && x2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isRIGHT", isRIGHT);
			} else if (x1 > start.x && x2 >= y2 && x2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isLEFT", isLEFT);
			} else if (y1 > start.y && y2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isUP", isUP);
			} else if (y2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isDOWN", isDOWN);
			}
			// getDirection();
		}
		else if(isLongPress){
			setChangeDirection(event);
			if (getTouchDuration() < MIN_DIRECTION_DURATION
					|| getTouchDuration() == 0) {
				// returnCenter("isSINGLE_TAB", isSINGLE_TAB);
			} else if (x1 < start.x && x2 >= y2 && x2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isLONG_PRESS_RIGHT", isLONG_PRESS_RIGHT);
			} else if (x1 > start.x && x2 >= y2 && x2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isLONG_PRESS_LEFT", isLONG_PRESS_LEFT);
			} else if (y1 > start.y && y2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isLONG_PRESS_UP", isLONG_PRESS_UP);
			} else if (y2 > MIN_DISTANCE) {
				stateTouch = returnCenter("isLONG_PRESS_DOWN", isLONG_PRESS_DOWN);
			}
		}
		isDoubleTab = false;
	}
	/**Return distance in x axis*/
	public float getxDistance() {
		return xDistance;
	}
	/**Return distance in y axis*/
	public float getyDistance() {
		return yDistance;
	}
	/**Return touch state*/
	public int getStateTouch() {
		return stateTouch;
	}
	/**Return value and add log to watch in logcat*/
	private int returnCenter(String string, int x) {
		Log.d(TAG, string);
		return x;
	}

	private long getTouchDuration() {
		return System.currentTimeMillis() - touchDuration;
	}

	private void setChangeDirection(MotionEvent rawEvent) {
		WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
		start.set(event.getX(), event.getY());
		if (x1 - start.x > 0)
			x2 = x1 - start.x;
		else
			x2 = -(x1 - start.x);
		if (y1 - start.y > 0)
			y2 = y1 - start.y;
		else
			y2 = -(y1 - start.y);
	}

	/** Determine the space between the first two fingers */
	private float spacing(WrapMotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, WrapMotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	private void getZoom(WrapMotionEvent event){
		
		float newDist = spacing(event);
		Log.d(TAG, "newDist=" + newDist);
		//if (newDist > 10f) {
			// matrix.set(savedMatrix);
			//float scale = newDist / oldDist;
			// matrix.postScale(scale, scale, mid.x, mid.y);
		//}*/
	}
}

class WrapMotionEvent {
	protected MotionEvent event;

	protected WrapMotionEvent(MotionEvent event) {
		this.event = event;
	}

	static public WrapMotionEvent wrap(MotionEvent event) {
		try {
			return new EclairMotionEvent(event);
		} catch (VerifyError e) {
			return new WrapMotionEvent(event);
		}
	}

	public int getAction() {
		return event.getAction();
	}

	public float getX() {
		return event.getX();
	}

	public float getX(int pointerIndex) {
		verifyPointerIndex(pointerIndex);
		return getX();
	}

	public float getY() {
		return event.getY();
	}

	public float getY(int pointerIndex) {
		verifyPointerIndex(pointerIndex);
		return getY();
	}

	public int getPointerCount() {
		return 1;
	}

	public int getPointerId(int pointerIndex) {
		verifyPointerIndex(pointerIndex);
		return 0;
	}

	private void verifyPointerIndex(int pointerIndex) {
		if (pointerIndex > 0) {
			throw new IllegalArgumentException(
					"Invalid pointer index for Donut/Cupcake");
		}
	}

}

class EclairMotionEvent extends WrapMotionEvent {

	protected EclairMotionEvent(MotionEvent event) {
		super(event);
	}

	public float getX(int pointerIndex) {
		return event.getX(pointerIndex);
	}

	public float getY(int pointerIndex) {
		return event.getY(pointerIndex);
	}

	public int getPointerCount() {
		return event.getPointerCount();
	}

	public int getPointerId(int pointerIndex) {
		return event.getPointerId(pointerIndex);
	}
}