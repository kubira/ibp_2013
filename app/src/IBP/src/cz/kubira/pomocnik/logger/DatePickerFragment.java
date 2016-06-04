package cz.kubira.pomocnik.logger;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	private int day = 0;
	private int month = 0;
	private int year = -1;
	
	public DatePickerFragment() {
		
	}
	
	public DatePickerFragment(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
    	if (year == -1) {
    		final Calendar c = Calendar.getInstance();
    		year = c.get(Calendar.YEAR);
    		month = c.get(Calendar.MONTH);
    		day = c.get(Calendar.DAY_OF_MONTH);
    	}

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), (OnDateSetListener)getActivity(), year, month, day);
    }
}