package org.android.blindphonebydroid.caller;

public class Item implements Comparable<Item>{
	protected long id;
	protected String caption;
	protected String description;
	
	public Item(long id,String caption) {
		// TODO Auto-generated constructor stub
		this(id,caption,"");
	}
	
	public Item(long id,String caption,String description) {
		// TODO Auto-generated constructor stub
		this.setId(id);
		this.setCaption(caption);
	    this.setDescription(description);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getCaption()+" "+this.getDescription();
	}
	
	@Override
	public int compareTo(Item another) {
		// TODO Auto-generated method stub
		return this.toString().compareTo(another.toString());
	}
	
	public long getId() {
		return id;
	}
	protected void setId(long id) {
		this.id = id;
	}
	
	public String getCaption() {
		return caption;
	}
	protected void setCaption(String caption) {
		this.caption = caption;
	}
	public String getDescription() {
		return description;
	}
	protected void setDescription(String description) {
		this.description = description;
	}
}
