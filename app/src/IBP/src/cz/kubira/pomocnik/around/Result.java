package cz.kubira.pomocnik.around;

import org.w3c.dom.Element;

import android.location.Location;

public class Result implements Comparable<Result> {
	protected String type;
	protected String name;
	protected String address;
	protected double longitude;
	protected double latitude;
	protected double distance;

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

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Result(Element element, Location location, String type) {
		this.type = type;
		name = element.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
		address = element.getElementsByTagName("vicinity").item(0).getChildNodes().item(0).getNodeValue();
		longitude = Double.valueOf(element.getElementsByTagName("lng").item(0).getChildNodes().item(0).getNodeValue());
		latitude = Double.valueOf(element.getElementsByTagName("lat").item(0).getChildNodes().item(0).getNodeValue());
		Location l = new Location("Result");
		l.setLatitude(latitude);
		l.setLongitude(longitude);
		distance = location.distanceTo(l);
	}

	@Override
	public int compareTo(Result hotel) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if (this.distance < hotel.distance) return BEFORE;
	    if (this.distance > hotel.distance) return AFTER;

	    return EQUAL;
	}
}