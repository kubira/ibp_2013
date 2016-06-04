package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import cz.kubira.pomocnik.around.ActivityHotels;
import cz.kubira.pomocnik.around.Place;
import cz.kubira.pomocnik.around.PlaceMarker;

public class PlaceInfoDialog extends Dialog implements View.OnClickListener {

	protected Place place;
	protected Context context;
	protected ObjectTableRow row;

	public PlaceInfoDialog(Context context, View view) {
		super(context);
		SpannableString s;
		row = ((ObjectTableRow)view);
		this.setPlace(((PlaceMarker)row.getObject()).getPlace());
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.place_info);

		TextView name = (TextView)findViewById(R.id.name);
		TextView address = (TextView)findViewById(R.id.address);
		TextView phone = (TextView)findViewById(R.id.phone);
		TextView stars = (TextView)findViewById(R.id.stars);
		TextView web = (TextView)findViewById(R.id.web);
		TextView gpsDistance = (TextView)findViewById(R.id.gps_distance);
		TextView camDistance = (TextView)findViewById(R.id.cam_distance);

		name.setText(place.getName());
		address.setText(place.getAddress().replace(", ", "\n").replace(",", "\n"));

		s = new SpannableString(place.getPhone());
		Linkify.addLinks(s, Linkify.PHONE_NUMBERS);
		phone.setText(s);
		phone.setMovementMethod(LinkMovementMethod.getInstance());

		stars.setText(ActivityHotels.stars[place.getStars()]);

		s = new SpannableString(place.getUrl());
		Linkify.addLinks(s, Linkify.WEB_URLS);
		web.setText(s);
		web.setMovementMethod(LinkMovementMethod.getInstance());

		Location placeLocation = new Location("");
		placeLocation.setLatitude(place.getLatitude());
		placeLocation.setLongitude(place.getLongitude());

		if (StartActivity.location != null) {
			gpsDistance.setText(StaticMethods.getNiceMeasure(StartActivity.location.distanceTo(placeLocation), StaticMethods.Units.METER));
		} else {
			gpsDistance.setText("Není k dispozici.");
		}

		if (StartActivity.map.getCameraPosition().target != null) {
			Location cam = new Location("Tady");
			cam.setLongitude(StartActivity.map.getCameraPosition().target.longitude);
			cam.setLatitude(StartActivity.map.getCameraPosition().target.latitude);
			camDistance.setText(StaticMethods.getNiceMeasure(cam.distanceTo(placeLocation), StaticMethods.Units.METER));
		} else {
			camDistance.setText("Není k dispozici.");
		}

		Button add = (Button)findViewById(R.id.delete_marker);
        add.setOnClickListener(this);

        Button goTo = (Button)findViewById(R.id.go_to_marker);
        goTo.setOnClickListener(this);
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public ObjectTableRow getRow() {
		return row;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.delete_marker : {
				(new DeletePlacePromptDialog(context, this)).show();
				break;
			}
			case R.id.go_to_marker : {
				((PlaceMarker)row.getObject()).getMarker().showInfoWindow();
				StartActivity.stred = false;
				StartActivity.toggle.setChecked(false);
				//StartActivity.img.setVisibility(ImageView.VISIBLE);
				StartActivity.you.setVisible(true);
				StartActivity.map.setOnCameraChangeListener(StartActivity.sActivity);
				((SavedPlaces)context).finish();
				dismiss();
				LatLng l = new LatLng(place.getLatitude(), place.getLongitude());
				StartActivity.map.animateCamera(CameraUpdateFactory.newLatLng(l));
				StartActivity.you.setPosition(l);
				break;
			}
		}
	}
}