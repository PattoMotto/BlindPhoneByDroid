package org.android.blindphonebydroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/** Theme for Blindness*/
public class BlindTheme {
	
	/**font color index in colorCode array*/
	public static final int FONT_COLOR = 0;
	/**background color index in colorCode array*/
	public static final int BACKGROUND_COLOR = 1;
	/**highlight color index in colorCode array*/
	public static final int SELECT_COLOR = 2;
	/**divider color index in colorCode array*/
	public static final int DIVIDER_COLOR = 3;
	
	private static final String DEFAULT_THEME = "theme0";
	private static final String CURRENT_TAG	= "current";
	private static final String THEME_TAG	= "theme";
	private static final String LOAD_THEME_TAG = "loadTheme";
	
	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;

	private String fileName = "theme.xml";
	private String currentTheme = null;
	private String[] tags = new String[]{"fontColor","bacgroundColor","selectColor","dividerColor"};
	private String[] colorCode = new String[]{
			"#0066ff","#000000","#00dcff","#000099"
			,"#ff0000","#000000","#ff66cc","#800000"
			,"#0066ff","#ffffff","#000099","#00dcff"
			,"#ff0000","#ffffff","#800000","#ff66cc"
			//,"#ff6699","#000000",""
			,"#7ADAE1","#000000","#00FFFF","#55ccff"
			};
	
	 /**Initial Class 's Object*/
	public BlindTheme() {
		// TODO Auto-generated constructor stub
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		try {
			String[] tmp = parserXML(CURRENT_TAG);
			if(tmp!=null) currentTheme = tmp[0];
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		if(currentTheme == null){
			currentTheme = DEFAULT_THEME;
			
			try {
				createFile(fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**Get the current theme name*/
	public String getCurrentTheme() {
		return currentTheme;
	}
	
	/**Set the current theme name*/
	public void setCurrentTheme(String currentTheme) {
		this.currentTheme = currentTheme;		
		try {
			createFile(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**Get the current color code*/
	public String[] getThemeColorCode() {
		String[] themeColorCode = null;
		try {
			themeColorCode = parserXML(currentTheme);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return themeColorCode;
	}
	
	/**Get list of theme*/
	public String[] getNameTheme() {
		String value[] = null;
		try {
			value = parserXML(LOAD_THEME_TAG);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	private boolean createFile(String fileName) throws IOException{
		
		if( mExternalStorageAvailable == false || mExternalStorageWriteable == false){
			return false;
		}
		
		File dir,file;
		dir = Environment.getExternalStorageDirectory();

		if(!dir.canWrite()) return false;
		
		file = new File(dir, fileName);
		FileOutputStream fileos = null;
		
		//if(!file.exists()){
			/*try {
				//file.createNewFile();
			} catch (IOException e) {
				// TODO: handle exception
				Log.e("IOException","can't createNewFile");
			}*/
			
	    	try {
	    		fileos = new FileOutputStream(file);
	    		createXML(fileos);
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				Log.e("FileNotFoundException","can't create FileOutputStream ");
			}
		/*}else{
			if(file.canRead()&file.canWrite()){
				
			}
		}*/
		
		return true;
	}
	
	private void createXML(FileOutputStream fileos){
		
		XmlSerializer serializer = Xml.newSerializer();
    	
    	try {
    		serializer.setOutput(fileos, "UTF-8");
	    	serializer.startDocument(null, true);
	    	
	    	serializer.startTag(null,THEME_TAG);
	    	
	    	//create current theme tag
	    	serializer.startTag(null,CURRENT_TAG);
	    	serializer.attribute(null, THEME_TAG,currentTheme);
	    	serializer.endTag(null,CURRENT_TAG);
	    	
	    	//create theme tag
	    	int countColorCode = 0;
	    	for(int nTheme = 0 ; nTheme < (colorCode.length/4) ; nTheme++){
	    		serializer.startTag(null,THEME_TAG+nTheme);
	    		
	    		for(String tag:tags){
	    			
			    	
			    	serializer.attribute(null, tag, colorCode[countColorCode]);
			    	
			    	
			    	countColorCode++;
		    	}
		    	
		    	serializer.endTag(null, THEME_TAG+nTheme);
		    	
	    	}
		    serializer.endTag(null, THEME_TAG);
		    
		    serializer.endDocument();
		    serializer.flush();
		    
		    fileos.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Exception","error occurred while creating xml file");
		}
	}
		
	private String[] parserXML(String tag) throws XmlPullParserException, IOException{
		String value[] = null;
		ArrayList<String> arrayList = null;
		if( mExternalStorageAvailable == false || mExternalStorageWriteable == false){
			return null;
		}
		
		
		String path = Environment.getExternalStorageDirectory()+"/"+fileName;
		File file = new File(path);
				
		if(!file.exists()) return null;
		
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		
		FileInputStream fis = new FileInputStream(file) ;
		
		xpp.setInput(new InputStreamReader(fis));
		int eventType = xpp.getEventType();
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch(eventType){
				case XmlPullParser.START_DOCUMENT : 
					break;
				case XmlPullParser.START_TAG : 
					if(xpp.getName().equals(tag)){
						if(tag.equals(CURRENT_TAG)){
							value = loadCurrentTheme(xpp);
						}else{
							value = loadColorCode(xpp);
						}		
						
					}else{
						if(tag.equals(LOAD_THEME_TAG)){
							if(arrayList==null){
								arrayList = new ArrayList<String>();
							}
							arrayList.add(xpp.getName());
						}
					}
					break;
				case XmlPullParser.END_TAG : 
					break;
				case XmlPullParser.TEXT : 
					break;
			}
			
			if(value!=null) break;
			eventType = xpp.next();
		
		}
		if(arrayList!=null){
			arrayList.remove(CURRENT_TAG);
			arrayList.remove(THEME_TAG);
			value = arrayList.toArray(new  String[arrayList.size()]);
		}
		return value;
	}
	
	private String[] loadColorCode(XmlPullParser xpp){
		String [] colorCode = new String[tags.length];
		
		for(int i = 0; i < tags.length ; i++){
			colorCode[i] = xpp.getAttributeValue(null,tags[i]);
		}
		
		return colorCode;
	}
	
	private String[] loadCurrentTheme(XmlPullParser xpp){
		String [] current = new String[1];
		
		current[0] = xpp.getAttributeValue(null, THEME_TAG);
		
		return current;
		
	}
}
