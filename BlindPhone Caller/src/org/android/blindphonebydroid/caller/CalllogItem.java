package org.android.blindphonebydroid.caller;

public class CalllogItem extends Item{
	private String phoneNumber;
	private String dateString;
	private int callType;
	
	public CalllogItem(long id,String caption,String phoneNumber,String dateString,int callType) {
		// TODO Auto-generated constructor stub
		this(id,caption,phoneNumber,dateString,callType,"");
	}
	
	public CalllogItem(long id,String caption,String phoneNumber,String dateString,int CallType,String description) {
		// TODO Auto-generated constructor stub
		super(id,caption,description);
		this.setDateString(dateString);
		this.setCallType(CallType);
	    this.setPhoneNumber(phoneNumber);
	    
	}

	public int compareTo(CalllogItem another) {
		// TODO Auto-generated method stub
		return this.toString().compareTo(another.toString());
	}
	
	public int getCallType() {
		return callType;
	}
	private void setCallType(int callType) {
		this.callType = callType;
	}
	public String getDateString() {
		return dateString;
	}
	private void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	private void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
