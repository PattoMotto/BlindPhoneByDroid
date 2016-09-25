package org.android.blindphonebydroid;

import java.util.Locale;

import android.widget.ListView;
import android.widget.Toast;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;


/** Text-to-Speech for Blindness */
public class TTS extends AccessibilityService{

    // message types we are passing around

    /** Speak */
    private static final int WHAT_SPEAK = 1;

    /** Stop speaking */
    private static final int WHAT_STOP_SPEAK = 2;

    /** Start the TTS service */
    private static final int WHAT_START_TTS = 3;

    /** Stop the TTS service */
    private static final int WHAT_SHUTDOWN_TTS = 4;

    /** Stop playing an earcon */
    private static final int WHAT_STOP_PLAY_EARCON = 6;

    /** Vibrate a pattern */
    private static final int WHAT_VIBRATE = 7;

    /** Stop vibrating */
    private static final int WHAT_STOP_VIBRATE = 8;
    
    
    private static final int WHAT_SPEAK_QUEUE = 10;

    //screen state broadcast related constants
    
    /** Feedback mapping index used as a key for the screen on broadcast */
    private static final int INDEX_SCREEN_ON = 0x00000100;

    /** Feedback mapping index used as a key for the screen off broadcast */
    private static final int INDEX_SCREEN_OFF = 0x00000200;

    /** Feedback mapping index used as a key for little vibrate */
    private static final int LITTLE_VIBRATE = 0x00000300;

    // speech related constants

    /**
     * The queuing mode we are using - interrupt a spoken utterance before
     * speaking another one
     */
    private static final int QUEUING_MODE_INTERRUPT = 2;
    private static final int QUEUE_ADD = 0x00000001;

    /** The empty string constant */
    private static final String SPACE = " ";
 
    /** Mapping from integers to vibration patterns for haptic feedback */
    private static final SparseArray<long[]> sVibrationPatterns = new SparseArray<long[]>();
    static {
        sVibrationPatterns.put(AccessibilityEvent.TYPE_VIEW_CLICKED, new long[] {
                0L, 100L
        });
        sVibrationPatterns.put(AccessibilityEvent.TYPE_VIEW_SELECTED, new long[] {
                0L, 15L, 10L, 15L
        });
        sVibrationPatterns.put(AccessibilityEvent.TYPE_VIEW_FOCUSED, new long[] {
                0L, 15L, 10L, 15L
        });
        sVibrationPatterns.put(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, new long[] {
                0L, 25L, 50L, 25L, 50L, 25L
        });
        sVibrationPatterns.put(INDEX_SCREEN_ON, new long[] {
                0L, 10L, 10L, 20L, 20L, 30L
        });
        sVibrationPatterns.put(INDEX_SCREEN_OFF, new long[] {
                0L, 30L, 20L, 20L, 10L, 10L
        });
        sVibrationPatterns.put(LITTLE_VIBRATE, new long[] {
                0L, 100L, 50L, 200L, 50L, 100L
        });
    }

    /**
     * Handle to this service to enable inner classes to access the {@link Context}
     */
    private Context mContext;

    /** The feedback this service is currently providing */
    private int mProvidedFeedbackType;

    // feedback providing services

    /** The {@link TextToSpeech} used for speaking */
    private TextToSpeech mTts;

    /** Vibrator for providing haptic feedback */
    private Vibrator mVibrator;

    /** Flag if the infrastructure is initialized */
    private boolean isInfrastructureInitialized;
    private static boolean isDebug = false;
    /** {@link Handler} for executing messages on the service main thread */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case WHAT_SPEAK:
                    String utterance = (String) message.obj;
                    mTts.speak(utterance, QUEUING_MODE_INTERRUPT, null);
                    return;
                case WHAT_SPEAK_QUEUE:
                    String utterance2 = (String) message.obj;
                    mTts.speak(utterance2, QUEUE_ADD, null);
                    return;
                case WHAT_STOP_SPEAK:
                    mTts.stop();
                    return;
                case WHAT_START_TTS:
                    mTts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                            	debug("Text-To-Speech engine is initialized");
                            }
                            else if (status == TextToSpeech.ERROR) {
                                debug("Error occurred while initializing Text-To-Speech engine");
                            }
                        }
                    });
                    return;
                case WHAT_SHUTDOWN_TTS:
                    mTts.shutdown();
                    return;
                case WHAT_VIBRATE:
                    int key = message.arg1;
                    long[] pattern = sVibrationPatterns.get(key);
                    mVibrator.vibrate(pattern, -1);
                    return;
                case WHAT_STOP_VIBRATE:
                    mVibrator.cancel();
                    return;
            }
        }
    };
    /**Initial Class 's Object*/
    public TTS(Context con) {
        if (isInfrastructureInitialized) {
            return;
        }
        mContext = con;
        // send a message to start the TTS
        mHandler.sendEmptyMessage(WHAT_START_TTS);
        isInfrastructureInitialized = true;
    }

    //Override onInterrupt in AccessibilityService
    @Override
    public void onInterrupt() {
        // here we act according to the feedback type we are currently providing
        if (mProvidedFeedbackType == AccessibilityServiceInfo.FEEDBACK_SPOKEN) {
            mHandler.obtainMessage(WHAT_STOP_SPEAK);
        } else if (mProvidedFeedbackType == AccessibilityServiceInfo.FEEDBACK_AUDIBLE) {
            mHandler.obtainMessage(WHAT_STOP_PLAY_EARCON);
        } else if (mProvidedFeedbackType == AccessibilityServiceInfo.FEEDBACK_HAPTIC) {
            mHandler.obtainMessage(WHAT_STOP_VIBRATE);
        } else {
            throw new IllegalStateException("Unexpected feedback type " + mProvidedFeedbackType);
        }
    }

    private String createRepeat (String str,boolean isRepeatFirstChar) {
		return (isRepeatFirstChar) ? str.trim().charAt(0)+SPACE+str:str;
	}
    
    /**Speak text QUEUE_MODE_INTERRUPT*/
    public void speak(String text){
    	mProvidedFeedbackType=AccessibilityServiceInfo.FEEDBACK_SPOKEN;
    	debug(text);
    	mHandler.obtainMessage(WHAT_SPEAK, text).sendToTarget();
    }
    /**Speak text QUEUE_MODE_ADD*/
    public void speakQueueAddMode(String text){
    	mProvidedFeedbackType=AccessibilityServiceInfo.FEEDBACK_SPOKEN;
    	debug(text);
    	mHandler.obtainMessage(WHAT_SPEAK_QUEUE, text).sendToTarget();
    }
    /**Speak text repeat mode or not*/
    public void speak(String text,boolean isRepeatFirstChar){
    	speak(createRepeat(text,isRepeatFirstChar));
    }
    /**Speak text from ListView Object where position and repeat mode or not*/
    public void speak(ListView l,int position,boolean isRepeatFirstChar){
    	speak(createRepeat(l.getItemAtPosition(position).toString(),isRepeatFirstChar));
    }
    /**Stop speak*/
    public void stop(){
    	mHandler.obtainMessage(WHAT_STOP_SPEAK);
    }
    /**Initial Vibrator and vibrate*/
    public void vibrate(Context con) {
    	setVibrate(con);
    	//Toast.makeText(mContext,sVibrationPatterns.get(feedbackType).toString() ,Toast.LENGTH_LONG).show();
    	vibrate();
	}
    /**Vibrate*/
    public void vibrate() {
    	//Toast.makeText(mContext,sVibrationPatterns.get(feedbackType).toString() ,Toast.LENGTH_LONG).show();
		mHandler.obtainMessage(WHAT_VIBRATE,0x00000300,0).sendToTarget();
	}
    /**Initial Vibrator*/
    private void setVibrate(Context con){
    	mVibrator =  (Vibrator) con.getSystemService(Service.VIBRATOR_SERVICE);
    }
    /**Sets the listener that will be notified when synthesis of an utterance completes.*/
    public int setOnUtteranceCompletedListener (TextToSpeech.OnUtteranceCompletedListener listener){
    	return mTts.setOnUtteranceCompletedListener (listener);
    }
    /**Set speed for the TextToSpeech engine. rate normal is 1 if 0.5 is 50% of speed(slower)
     * 1.5 is 150% of speed(faster)*/
    public int setSpeechRate (float speechRate) {
    	return mTts.setSpeechRate(speechRate);
    }
    /**Sets the speech pitch for the TextToSpeech engine. pitch 1.0 is the normal pitch
     * lower values lower the tone of the synthesized voice, greater values increase it.*/
    public int setPitch (float pitch){
		return mTts.setPitch(pitch);
	}
    /**Set language follow by LANG_AVAILABLE*/
    public int setLanguage (Locale loc){
    	return setLanguage (loc);
	}
    /**Start debug enable toast*/
    public void startDebug() {
		isDebug=true;
	}
    /**Start debug disable toast*/
    public void stopDebug() {
    	isDebug=false;
    }
    /**Start toast (debug mode)*/
    private void debug(String text){
    	if(isDebug)
    		Toast.makeText(mContext,text, Toast.LENGTH_LONG).show();
    }
    
    //Override onAccessibilityEvent in AccessibilityService
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		
	}
}