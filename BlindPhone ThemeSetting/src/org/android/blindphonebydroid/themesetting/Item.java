package org.android.blindphonebydroid.themesetting;

public class Item implements Comparable<Item>{
	private long id;
	private String caption;
	private String description;
	
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
	private void setId(long id) {
		this.id = id;
	}
	
	public String getCaption() {
		return caption;
	}
	private void setCaption(String caption) {
		this.caption = caption;
	}
	public String getDescription() {
		return description;
	}
	private void setDescription(String description) {
		this.description = description;
	}
}
