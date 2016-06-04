package cz.kubira.pomocnik.around;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cz.kubira.pomocnik.StartActivity;

public class PlaceMarker {
	protected Marker marker;
	protected Place place;

	public PlaceMarker(Place place) {
		this.place = place;

		marker = StartActivity.map.addMarker(new MarkerOptions()
		.position(new LatLng(place.latitude, place.longitude))
		.title(place.name)
		.snippet(getSnippet())
		.icon(BitmapDescriptorFactory.defaultMarker(getColor())));

		System.out.println("Marker added to map "+place.latitude+":"+place.longitude);

		StartActivity.markers.add(this);

		System.out.println("Marker added to list");
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public String getSnippet() {
		String result = "";

		if (place.type.equals("hotel")) {
			result += place.address.replace(", ", "\n").replace(",", "\n")+"\n";
			result += place.phone+"\n";
			result += place.url+"\n";
			result += String.format("%,.1f", place.rating)+"\t";
			result += ActivityHotels.stars[place.stars];
		} else {
			result += place.address.replace(", ", "\n").replace(",", "\n")+"\n";
		}
		return result;
	}

	public float getColor() {
		if (place.type.equals("hotel")) {
			return BitmapDescriptorFactory.HUE_YELLOW;
		} else if (place.type.equals("restaurant")) {
			return BitmapDescriptorFactory.HUE_RED;
		} else if (place.type.equals("atm")) {
			return BitmapDescriptorFactory.HUE_VIOLET;
		} else if (place.type.equals("gas_station")) {
			return BitmapDescriptorFactory.HUE_BLUE;
		} else if (place.type.equals("pharmacy")) {
			return BitmapDescriptorFactory.HUE_GREEN;
		} else {
			return BitmapDescriptorFactory.HUE_AZURE;
		}
	}
}