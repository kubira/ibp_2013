package cz.kubira.pomocnik;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.view.Window;
import android.widget.TextView;
import cz.kubira.pomocnik.StaticMethods.Units;

public class LocationInfoDialog extends Dialog {

	public LocationInfoDialog(Context context, Location location) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_info_dialog);

		TextView longitude = (TextView)findViewById(R.id.longitude);
		TextView latitude = (TextView)findViewById(R.id.latitude);
		TextView altitude = (TextView)findViewById(R.id.altitude);
		TextView accuracy = (TextView)findViewById(R.id.accuracy);
		TextView date_and_time = (TextView)findViewById(R.id.date_and_time);
		TextView bearing = (TextView)findViewById(R.id.bearing);
		TextView speed = (TextView)findViewById(R.id.speed);

		if (location.getLongitude() != 0) {
			longitude.setText(String.format("%,.8f °", location.getLongitude()));
		} else {
			longitude.setText(R.string.not_available);
		}

		if (location.getLatitude() != 0) {
			latitude.setText(String.format("%,.8f °", location.getLatitude()));
		} else {
			latitude.setText(R.string.not_available);
		}

		if (location.getAltitude() > 1.0) {
			altitude.setText(StaticMethods.getNiceMeasure(location.getAltitude(), Units.METER_ABOVE_SEA_LEVEL));
		} else {
			altitude.setText(R.string.not_available);
		}

		if (location.getAccuracy() != 0) {
			accuracy.setText(StaticMethods.getNiceMeasure(location.getAccuracy(), StaticMethods.Units.METER));
		} else {
			accuracy.setText(R.string.not_available);
		}

		if (location.getTime() != 0) {
			Date d = new Date(location.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy\nHH:mm:ss.SSS");
			date_and_time.setText(sdf.format(d));
		} else {
			date_and_time.setText(R.string.not_available);
		}

		if (location.getBearing() != 0) {
			bearing.setText(String.format("%.2f °", location.getBearing()));
		} else {
			bearing.setText(R.string.not_available);
		}

		if (location.getSpeed() != 0) {
			speed.setText(StaticMethods.getNiceMeasure(location.getSpeed(), Units.METER_PER_SECOND));
		} else {
			speed.setText(R.string.not_available);
		}
	}

}