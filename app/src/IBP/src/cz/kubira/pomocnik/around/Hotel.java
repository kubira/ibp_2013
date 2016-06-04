package cz.kubira.pomocnik.around;

import org.w3c.dom.Element;

public class Hotel implements Comparable<Hotel> {
	protected String name;
	protected String address;
	protected String phone;
	protected String city;
	protected String description;
	protected int stars;
	protected double rating;
	protected double longitude;
	protected double latitude;
	protected double distanceMiles;
	protected String url;

	public Hotel(Element element) {
		name = element.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
		address = element.getElementsByTagName("fulladdress").item(0).getChildNodes().item(0).getNodeValue();
		try {
			phone = element.getElementsByTagName("phone").item(0).getChildNodes().item(0).getNodeValue();
		} catch (NullPointerException e) {
			phone = "";
		}
		try {
			city = element.getElementsByTagName("city").item(0).getChildNodes().item(0).getNodeValue();
		} catch (NullPointerException e) {
			city = "";
		}
		description = element.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue();
		try {
			stars = Integer.valueOf(element.getElementsByTagName("stars").item(0).getChildNodes().item(0).getNodeValue());
		} catch (NumberFormatException e) {
			stars = 0;
		} catch (NullPointerException e) {
			rating = 0;
		}
		try {
			rating = Double.valueOf(element.getElementsByTagName("rating").item(0).getChildNodes().item(0).getNodeValue());
		} catch (NumberFormatException e) {
			rating = 0;
		} catch (NullPointerException e) {
			rating = 0;
		}
		longitude = Double.valueOf(element.getElementsByTagName("long").item(0).getChildNodes().item(0).getNodeValue());
		latitude = Double.valueOf(element.getElementsByTagName("lat").item(0).getChildNodes().item(0).getNodeValue());
		distanceMiles = Double.valueOf(element.getElementsByTagName("dist").item(0).getChildNodes().item(0).getNodeValue());
		try {
			url = element.getElementsByTagName("url").item(0).getChildNodes().item(0).getNodeValue();
		} catch (NullPointerException e) {
			url = "";
		}
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
		return phone;
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

	public double getDistanceMiles() {
		return distanceMiles;
	}

	public void setDistanceMiles(double distanceMiles) {
		this.distanceMiles = distanceMiles;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(Hotel hotel) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if (this.distanceMiles < hotel.distanceMiles) return BEFORE;
	    if (this.distanceMiles > hotel.distanceMiles) return AFTER;

	    return EQUAL;
	}
}