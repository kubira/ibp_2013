package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

public class GoToDialog extends Dialog implements View.OnClickListener {

	private Context context;
	private Button btnGoTo;
	private Button degree;
	private Button degreeMinute;
	private Button degreeMinuteSecond;
	private enum TYPE {
		DEGREE, DEGREE_MINUTE, DEGREE_MINUTE_SECOND;
	}
	private TYPE actual;

	public GoToDialog(Context context) {
		super(context);
		this.context = context;
		setContentView(R.layout.go_to_dialog_d);

		actual = TYPE.DEGREE;

		setTitle("Jdi na souøadnice");

		btnGoTo = (Button)findViewById(R.id.goToButton);
		btnGoTo.setOnClickListener(this);

		degree = (Button)findViewById(R.id.degree);
		degree.setOnClickListener(null);

		degreeMinute = (Button)findViewById(R.id.degreeMinute);
		degreeMinute.setOnClickListener(this);

		degreeMinuteSecond = (Button)findViewById(R.id.degreeMinuteSecond);
		degreeMinuteSecond.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
			case R.id.degree : {
				setContentView(R.layout.go_to_dialog_d);

				actual = TYPE.DEGREE;

				btnGoTo = (Button)findViewById(R.id.goToButton);
				btnGoTo.setOnClickListener(this);

				degree = (Button)findViewById(R.id.degree);
				degree.setOnClickListener(null);

				degreeMinute = (Button)findViewById(R.id.degreeMinute);
				degreeMinute.setOnClickListener(this);

				degreeMinuteSecond = (Button)findViewById(R.id.degreeMinuteSecond);
				degreeMinuteSecond.setOnClickListener(this);

				return;
			}
			case R.id.degreeMinute : {
				setContentView(R.layout.go_to_dialog_dm);

				actual = TYPE.DEGREE_MINUTE;

				btnGoTo = (Button)findViewById(R.id.goToButton);
				btnGoTo.setOnClickListener(this);

				degree = (Button)findViewById(R.id.degree);
				degree.setOnClickListener(this);

				degreeMinute = (Button)findViewById(R.id.degreeMinute);
				degreeMinute.setOnClickListener(null);

				degreeMinuteSecond = (Button)findViewById(R.id.degreeMinuteSecond);
				degreeMinuteSecond.setOnClickListener(this);

				return;
			}
			case R.id.degreeMinuteSecond : {
				setContentView(R.layout.go_to_dialog_dms);

				actual = TYPE.DEGREE_MINUTE_SECOND;

				btnGoTo = (Button)findViewById(R.id.goToButton);
				btnGoTo.setOnClickListener(this);

				degree = (Button)findViewById(R.id.degree);
				degree.setOnClickListener(this);

				degreeMinute = (Button)findViewById(R.id.degreeMinute);
				degreeMinute.setOnClickListener(this);

				degreeMinuteSecond = (Button)findViewById(R.id.degreeMinuteSecond);
				degreeMinuteSecond.setOnClickListener(null);

				return;
			}
			case R.id.goToButton : {

				// TODO Auto-generated method stub
				double lat = 0;
				double lon = 0;

				try {
					lat = Double.valueOf(((EditText)findViewById(R.id.latitudeDegree)).getText().toString());
				} catch (Exception e) {
					(new ErrorDialog(context, R.string.input_error, R.string.bad_latitude)).show();
					return;
				}

				try {
					lon = Double.valueOf(((EditText)findViewById(R.id.longitudeDegree)).getText().toString());
				} catch (Exception e) {
					(new ErrorDialog(context, R.string.input_error, R.string.bad_longitude)).show();
					return;
				}

				if (actual == TYPE.DEGREE_MINUTE || actual == TYPE.DEGREE_MINUTE_SECOND) {
					try {
						lat += (Double.valueOf(((EditText)findViewById(R.id.latitudeMinute)).getText().toString())/60);
					} catch (Exception e) {
						(new ErrorDialog(context, R.string.input_error, R.string.bad_latitude)).show();
						return;
					}

					try {
						lon += (Double.valueOf(((EditText)findViewById(R.id.longitudeMinute)).getText().toString())/60);
					} catch (Exception e) {
						(new ErrorDialog(context, R.string.input_error, R.string.bad_longitude)).show();
						return;
					}
				}

				if (actual == TYPE.DEGREE_MINUTE_SECOND) {
					try {
						lat += (Double.valueOf(((EditText)findViewById(R.id.latitudeSecond)).getText().toString())/3600);
					} catch (Exception e) {
						(new ErrorDialog(context, R.string.input_error, R.string.bad_latitude)).show();
						return;
					}

					try {
						lon += (Double.valueOf(((EditText)findViewById(R.id.longitudeSecond)).getText().toString())/3600);
					} catch (Exception e) {
						(new ErrorDialog(context, R.string.input_error, R.string.bad_longitude)).show();
						return;
					}
				}

				System.out.println(String.format("%,.8f\n%,.8f", lat, lon));

				StartActivity.stred = false;
				StartActivity.toggle.setChecked(false);
				StartActivity.you.setVisible(true);

				LatLng l = new LatLng(lat, lon);
				StartActivity.map.animateCamera(CameraUpdateFactory.newLatLng(l));
				StartActivity.map.setOnCameraChangeListener(StartActivity.sActivity);
				StartActivity.you.setPosition(l);
			}
		}

		dismiss();
	}
}