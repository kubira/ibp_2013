package cz.kubira.pomocnik.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.GPSDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.services.TrackService;
import cz.kubira.pomocnik.services.TrackService.LocalBinder;

public class TrackPosition extends Activity implements View.OnClickListener, OnSeekBarChangeListener {

	private Button startButton;
	private Button stopButton;
	private Button writeButton;

	private TextView seconds;
	private TextView minutes;

	private EditText input;
	private EditText description;

	private SeekBar seekBarSeconds;
	private SeekBar seekBarMinutes;

	private TrackService mService;
    private boolean mBound = false;

	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_position);

		startButton = (Button)findViewById(R.id.start_button);
		startButton.setOnClickListener(this);
		stopButton = (Button)findViewById(R.id.stop_button);
		stopButton.setOnClickListener(this);
		writeButton = (Button)findViewById(R.id.write_button);
		writeButton.setOnClickListener(this);

		seconds = (TextView)findViewById(R.id.seek_second_field);
		minutes = (TextView)findViewById(R.id.seek_minute_field);

		input = (EditText)findViewById(R.id.name_field);
		description = (EditText)findViewById(R.id.desc_field);

		seekBarSeconds = (SeekBar)findViewById(R.id.seek_second);
		seekBarSeconds.setOnSeekBarChangeListener(this);
		seekBarMinutes = (SeekBar)findViewById(R.id.seek_minute);
		seekBarMinutes.setOnSeekBarChangeListener(this);

		if (!((LocationManager)getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			(new GPSDialog(this, R.string.gps_error, R.string.no_position_text)).show();
		}

		if (isMyServiceRunning()) {
			startButton.setEnabled(false);

			Intent i = new Intent(getBaseContext(), TrackService.class);
			bindService(i, mConnection, BIND_AUTO_CREATE);
		} else {
			stopButton.setEnabled(false);
			writeButton.setEnabled(false);
		}
	}

	@Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (TrackService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.start_button : {
				if (input.getText().toString().trim().equals("")) {
					(new ErrorDialog(this, R.string.input_error, "Prázdné jméno trasy.")).show();
					return;
				}

				int interval = (seekBarMinutes.getProgress() * 60) + seekBarSeconds.getProgress();
				if (interval == 0) {
					(new ErrorDialog(this, R.string.input_error, "Nulový interval záznamu trasy.")).show();
					return;
				}

				Intent intent = new Intent(getBaseContext(), TrackService.class);
				Bundle b = new Bundle();
				b.putString("name", input.getText().toString());
				if (description.getText().toString().equals("")) {
					b.putString("description", null);
				} else {
					b.putString("description", description.getText().toString());
				}
				b.putInt("interval", interval*1000);
				intent.putExtras(b);
				startService(intent);
				description.setText("");
				input.setText("");
				break;
			}
			case R.id.stop_button : {
				if (mBound) {
		            unbindService(mConnection);
		            mBound = false;
		        }
				stopService(new Intent(getBaseContext(), TrackService.class));
				break;
			}
			case R.id.write_button : {
				System.out.println("Write button clicked.");

				String in = input.getText().toString();
				String de = description.getText().toString();

				if (in.equals("") && de.equals("")) {
					mService.writeNow(null, null);
				} else if (!in.equals("") && de.equals("")) {
					mService.writeNow(in, null);
				} else if (in.equals("") && !de.equals("")) {
					mService.writeNow(null, de);
				} else if (!in.equals("") && !de.equals("")) {
					mService.writeNow(in, de);
				}

				System.out.println("Writed.");

				Toast.makeText(this, "Position saved.", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if (isMyServiceRunning()) {
			Intent i = new Intent(getBaseContext(), TrackService.class);
			bindService(i, mConnection, BIND_AUTO_CREATE);

			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			writeButton.setEnabled(true);
		} else {
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			writeButton.setEnabled(false);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == R.id.seek_second) {
			seconds.setText(String.valueOf(seekBar.getProgress()));
		} else if (seekBar.getId() == R.id.seek_minute) {
			minutes.setText(String.valueOf(seekBar.getProgress()));
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
