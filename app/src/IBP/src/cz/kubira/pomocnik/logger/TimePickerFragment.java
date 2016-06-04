package cz.kubira.pomocnik.logger;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

public class TimePickerFragment extends DialogFragment {
	private int hour = 0;
	private int minute = -1;
	
	public TimePickerFragment() {
		
	}
	
	public TimePickerFragment(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
    	if (minute == -1) {
    		final Calendar c = Calendar.getInstance();
    		hour = c.get(Calendar.HOUR_OF_DAY);
    		minute = c.get(Calendar.MINUTE);
    	}

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), (OnTimeSetListener)getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}