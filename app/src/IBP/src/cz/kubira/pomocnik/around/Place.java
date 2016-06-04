package cz.kubira.pomocnik.around;

import cz.kubira.pomocnik.logger.ParentTable;

public class Place extends ParentTable {
	protected String type;
	protected String name;
	protected String address;
	protected String phone;
	protected String city;
	protected String description;
	protected int stars;
	protected double rating;
	protected double longitude;
	protected double latitude;
	protected String url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		if (phone != null) {
			return phone;
		} else {
			return "";
		}
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getUrl() {
		if (url != null) {
			return url;
		} else {
			return "";
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}
}