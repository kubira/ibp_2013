package cz.kubira.pomocnik.around;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cz.kubira.pomocnik.GPSDialog;
import cz.kubira.pomocnik.NetworkDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.StaticMethods.Units;

public class PlaceOptions extends Activity implements OnSeekBarChangeListener {

	private RadioGroup radioGroup = null;
	private TextView tv = null;
	private SeekBar sb = null;
	private int SEEKBAR_STEP = 0;
	private String unit = null;
	Bundle b = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_options);

		b = getIntent().getExtras();

		SEEKBAR_STEP = b.getInt("seekBarStep");
		unit = b.getString("unit");

		radioGroup = (RadioGroup) findViewById(R.id.location_radio_group);

		tv = (TextView)findViewById(R.id.seekbar_field);

		sb = (SeekBar)findViewById(R.id.seekbar);
		sb.setMax(b.getInt("seekBarMax"));
		sb.setProgress(sb.getMax()/2);
		sb.setOnSeekBarChangeListener(this);

		if (unit.startsWith("m")) {
			tv.setText(StaticMethods.getNiceMeasure(sb.getProgress()*SEEKBAR_STEP, Units.DISTANCE_METER));
		} else {
			tv.setText(StaticMethods.getNiceMeasure(sb.getProgress()*SEEKBAR_STEP, Units.DISTANCE_KILOMETER));
		}
	}

	public void onClick(View view) {
		String s = b.getString("type");
		Intent i = null;
		Bundle bx = new Bundle();

		bx.putInt("location", radioGroup.getCheckedRadioButtonId());
		bx.putInt("distance", sb.getProgress()*SEEKBAR_STEP);

		if (radioGroup.getCheckedRadioButtonId() == R.id.actual && StartActivity.location == null) {
			(new GPSDialog(this, R.string.gps_error, R.string.no_position_text)).show();
			return;
		} else if (radioGroup.getCheckedRadioButtonId() == R.id.camera && StartActivity.map.getCameraPosition().target == null) {
			if (StaticMethods.isConnectivity(this)) {
				(new GPSDialog(this, R.string.gps_error, R.string.no_position_text)).show();
				return;
			} else {
				(new NetworkDialog(this, R.string.net_error, R.string.no_connection_text)).show();
				return;
			}
		}

		if (s.equals("hotel")) {
			i = new Intent(this, ActivityHotels.class);
		} else {
			i = new Intent(this, ActivityResult.class);
			bx.putString("type", s);
		}

		i.putExtras(bx);
		startActivity(i);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (unit.startsWith("m")) {
			tv.setText(StaticMethods.getNiceMeasure(sb.getProgress()*SEEKBAR_STEP, Units.DISTANCE_METER));
		} else {
			tv.setText(StaticMethods.getNiceMeasure(sb.getProgress()*SEEKBAR_STEP, Units.DISTANCE_KILOMETER));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
