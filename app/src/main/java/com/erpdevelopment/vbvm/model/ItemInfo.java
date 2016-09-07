package com.erpdevelopment.vbvm.model;

public class ItemInfo {

	private String id;
	private String type;
	private Object item;
	private String position; //used only on Study items
	private String[] idPropertyArray; //used only on Study items
	
	public ItemInfo() {
		// TODO Auto-generated constructor stub
	}

	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String[] getIdPropertyArray() {
		return idPropertyArray;
	}

	public void setIdPropertyArray(String[] idPropertyArray) {
		this.idPropertyArray = idPropertyArray;
	}

}
