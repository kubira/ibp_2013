package cz.kubira.pomocnik;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.icu.util.Currency;

import cz.kubira.pomocnik.StaticMethods.Units;
import cz.kubira.pomocnik.activities.MainMenu;
import cz.kubira.pomocnik.around.Place;
import cz.kubira.pomocnik.around.PlaceMarker;
import cz.kubira.pomocnik.logger.DataSource;

public class StartActivity extends FragmentActivity implements OnMapLongClickListener, OnCameraChangeListener, LocationListener, android.location.GpsStatus.Listener, AsyncTaskCompleteListener<String> {

	static final LatLng ZERO_POSITION = new LatLng(0.0, 0.0);
	private boolean uz = false;
	protected static boolean stred = true;
	private Marker me;
	protected static Marker you;
	private LatLng now = null;
	double lat;
	double lng;
	public static Location location = null;
	public static GoogleMap map;
	public static Map<String, String> settings;
	private String provider;
	private LocationManager locationManager;
	private Criteria criteria;
	public static long bytes = 0;
	public static ArrayList<PlaceMarker> markers = new ArrayList<PlaceMarker>();

	private BroadcastReceiver receiver;
	protected static ToggleButton toggle;
	public static StartActivity sActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/** Voláni funkce onCreate rodièovské tøídy. */
		super.onCreate(savedInstanceState);

		/** Zrušení titulku okna aplikace. */
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		sActivity = this;

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("Tu som :)");
				setProvider();
			}
		};

		registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));

		File file = this.getFileStreamPath("settings");
		Settings s = new Settings(this);
		if (!file.exists()) {
			Locale l = new Locale("", Locale.getDefault().getCountry());
			Currency c = Currency.getInstance(l);
			s.writeFile(Locale.getDefault().getCountry(),
					Locale.getDefault().getLanguage(),
					c.getCurrencyCode(),
					(getResources().getTextArray(R.array.measurement_units))[0].toString(),
					"no_check");

			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
		} else {
			settings = s.readFile();
		}

		System.out.println("Settings: "+settings);

		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();

		map.setOnMapLongClickListener(this);
		map.setInfoWindowAdapter(new InfoWindowAdapter() {

	        @Override
	        public View getInfoWindow(Marker arg0) {
	            return null;
	        }

	        @Override
	        public View getInfoContents(Marker marker) {

	            View view = getLayoutInflater().inflate(R.layout.marker, null);

	            TextView info = (TextView) view.findViewById(R.id.title);

	            info.setText(marker.getTitle());
	            info.setTextColor(Color.BLACK);
	            info.setTypeface(null, Typeface.BOLD);

	            info = (TextView)view.findViewById(R.id.snippet);
	            info.setTextColor(Color.GRAY);

	            info.setText(marker.getSnippet().trim());

	            return view;
	        }
	    });

		me = map.addMarker(new MarkerOptions()
			.position(ZERO_POSITION)
			.title("Já")
			.snippet(getString(R.string.me))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.here)));
		me.setVisible(false);

		you = map.addMarker(new MarkerOptions()
			.position(ZERO_POSITION)
			.title("Ty")
			.snippet("Tvoje pozice")
			.anchor(0.5f, 0.5f)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.square)));
		you.setVisible(false);

		toggle = (ToggleButton)findViewById(R.id.togglebutton);

		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(context);

		if (savedInstanceState == null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			(new GPSDialog(this, R.string.gps_error, R.string.no_position_text)).show();
		}

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		DataSource ds = new DataSource(this);
		ds.open();
		List<Place> places = ds.getAllPlaces();
		ds.close();

		markers.clear();

		for (Place place : places) {
			new PlaceMarker(place);
		}

		setProvider();

		restore(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("toggle", toggle.isChecked());
		outState.putDouble("cameraLat", map.getCameraPosition().target.latitude);
		outState.putDouble("cameraLon", map.getCameraPosition().target.longitude);
		outState.putFloat("zoom", map.getCameraPosition().zoom);
		outState.putBoolean("youVisibility", you.isVisible());
		outState.putDouble("youLat", you.getPosition().latitude);
		outState.putDouble("youLon", you.getPosition().longitude);
		outState.putBoolean("meVisibility", me.isVisible());
		outState.putDouble("meLat", me.getPosition().latitude);
		outState.putDouble("meLon", me.getPosition().longitude);
		outState.putInt("mapType", map.getMapType());
	}

	private void restore(Bundle state) {

		if (state != null) {
			toggle.setChecked(state.getBoolean("toggle"));
			if (!toggle.isChecked()) {
				map.setOnCameraChangeListener(this);
			}
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(state.getDouble("cameraLat"), state.getDouble("cameraLon"))));
			map.animateCamera(CameraUpdateFactory.zoomTo(state.getFloat("zoom")));
			you.setVisible(state.getBoolean("youVisibility"));
			you.setPosition(new LatLng(state.getDouble("youLat"), state.getDouble("youLon")));
			me.setVisible(state.getBoolean("meVisibility"));
			me.setPosition(new LatLng(state.getDouble("meLat"), state.getDouble("meLon")));
			map.setMapType(state.getInt("mapType"));
		}
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if(keycode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(getBaseContext(), MainMenu.class);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keycode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.locationManager.removeUpdates(this);
		unregisterReceiver(receiver);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		System.out.println("Changed: "+provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		System.out.println("Enabled: "+provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		System.out.println("Disabled: "+provider);
	}

	@Override
	public void onLocationChanged(Location location) {
		StartActivity.location = location;
		updateWithNewLocation(location);
	}

	public void setProvider() {
		provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);

		updateWithNewLocation(location);

		System.out.println("set: "+provider);

		locationManager.requestLocationUpdates(provider, 500, 1, this);
	}

	private void updateWithNewLocation(Location location) {
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			now = new LatLng(lat, lng);

			me.setPosition(now);
			if (stred) {
				map.moveCamera(CameraUpdateFactory.newLatLng(now));
			}
			if (!uz) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 15));
				me.setVisible(true);
				uz = true;
			}
		}
	}

	public void jedem(View view) {
		if (now != null) {
			map.animateCamera(CameraUpdateFactory.newLatLng(now));
		}
	}

	public void savedPlaces(View view) {
		System.out.println("SAVED PLACES");
		Intent i = new Intent(this, SavedPlaces.class);
		startActivity(i);
	}

	public void ukaz(View view) {
		Location l = new Location("Tady");
		l.setLongitude(map.getCameraPosition().target.longitude);
		l.setLatitude(map.getCameraPosition().target.latitude);

		if (location != null) {
			Toast.makeText(this, "Vzdálenost: "+StaticMethods.getNiceMeasure(location.distanceTo(l), Units.METER), Toast.LENGTH_LONG).show();
		} else {
			System.out.println("Info: null 1");
		}
	}

	public void goTo(View view) {
		System.out.println("GO TO HELL!");
		(new GoToDialog(this)).show();
	}

	public void showInfo(View view) {
		if (location != null) {
			(new LocationInfoDialog(this, StartActivity.location)).show();
		} else {
			System.out.println("Info: null 2");
		}
	}

	public void showCameraInfo(View view) {
		Location l = new Location("Tady");
		l.setLongitude(map.getCameraPosition().target.longitude);
		l.setLatitude(map.getCameraPosition().target.latitude);
		(new LocationInfoDialog(this, l)).show();
	}

	public void getAddress(View view) {
		if (StaticMethods.isConnectivity(this)) {
			if (location != null) {
				ReverseGeocodingTask a = new ReverseGeocodingTask(this, this);
		        a.execute(new Location[] {location});
			} else {
				(new GPSDialog(this, R.string.gps_error, R.string.no_position_text)).show();
			}
		} else {
			(new NetworkDialog(this, R.string.net_error, R.string.no_connection_text)).show();
		}
	}

	public void getCamAddress(View view) {
		if (StaticMethods.isConnectivity(this)) {
			if (map.getCameraPosition().target != null) {
				Location l = new Location("");
				l.setLatitude(map.getCameraPosition().target.latitude);
				l.setLongitude(map.getCameraPosition().target.longitude);
				ReverseGeocodingTask a = new ReverseGeocodingTask(this, this);
		        a.execute(new Location[] {l});
			} else {
				(new GPSDialog(this, R.string.gps_error, R.string.no_position_text)).show();
			}
		} else {
			(new NetworkDialog(this, R.string.net_error, R.string.no_connection_text)).show();
		}
	}

	@Override
	public void onTaskComplete(String result) {
		result = result.replace("null", "").replace(", ", "\n").trim();
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	public void onToggleClicked(View view) {
	    stred = ((ToggleButton)view).isChecked();
	    if (stred) {
	    	map.setOnCameraChangeListener(null);
	    	you.setVisible(false);
	    } else {
	    	map.setOnCameraChangeListener(this);
	    	you.setVisible(true);
	    	you.setPosition(map.getCameraPosition().target);
	    }
	}

	@Override
	public void onGpsStatusChanged(int arg0) {
		System.out.println("GPS: "+arg0);
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		you.setPosition(arg0.target);
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		(new MapTypeDialog(this)).show();
	}

}
