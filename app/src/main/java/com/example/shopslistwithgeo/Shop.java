package com.example.shopslistwithgeo;

public class Shop {
	private String name;
	private String longitude;
	private String latitude;
	private String range;
	private String description;
	private int id;

	public Shop() {
	}

	public Shop(String name, String longitude, String latitude, String description) {
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.description = description;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String quantity) {
		this.longitude = quantity;
	}



	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String state) {
		this.latitude = state;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
