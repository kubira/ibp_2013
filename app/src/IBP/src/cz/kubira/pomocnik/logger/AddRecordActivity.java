package cz.kubira.pomocnik.logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ibm.icu.util.Currency;

import cz.kubira.pomocnik.CurrencyLocale;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;

public class AddRecordActivity extends FragmentActivity
	implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	protected Spinner catSpinner;
	protected Spinner curSpinner;
	protected Spinner jouSpinner;
	final Context context = this;  // Activity context
	protected DataSource dataSource;
	protected Button dateButton;
	protected Button timeButton;

	protected long time = -1;
	protected long date = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_record);

		GregorianCalendar cal = new GregorianCalendar();
		time = (((new GregorianCalendar(1970, 0, 1, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))).getTimeInMillis())/1000);
		date = (((new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0)).getTimeInMillis())/1000);


        catSpinner = (Spinner) findViewById(R.id.category_spinner);
        curSpinner = (Spinner) findViewById(R.id.currency_spinner);
        jouSpinner = (Spinner) findViewById(R.id.journey_spinner);

        dateButton = (Button) findViewById(R.id.date_button);
        timeButton = (Button) findViewById(R.id.time_button);

        final Calendar c = Calendar.getInstance();
        dateButton.setText(c.get(Calendar.DAY_OF_MONTH)
        		+getString(R.string.date_delimiter)
        		+(c.get(Calendar.MONTH)+1)
        		+getString(R.string.date_delimiter)
        		+c.get(Calendar.YEAR));
        if (c.get(Calendar.MINUTE) < 10) {
			timeButton.setText(c.get(Calendar.HOUR_OF_DAY)+getString(R.string.time_delimiter)+"0"+c.get(Calendar.MINUTE));
		} else {
			timeButton.setText(c.get(Calendar.HOUR_OF_DAY)+getString(R.string.time_delimiter)+c.get(Calendar.MINUTE));
		}

		dataSource = new DataSource(this);

		dataSource.open();

		List<Category> catValues = dataSource.getAllCategories();
		Set<Currency> currencies = Currency.getAvailableCurrencies();
		ArrayList<CurrencyLocale> curValues = new ArrayList<CurrencyLocale>();
		List<Journey> jouValues = dataSource.getAllJournies();

		CurrencyLocale cll = null;

		for (Currency currency : currencies) {
			/** Vytvoøení objektu pro jeden jazyk. */
			CurrencyLocale obj = new CurrencyLocale(currency, StartActivity.settings.get("language"));
			/** Vložení objektu do pole. */
			curValues.add(obj);

			if (currency.getCurrencyCode().equals(StartActivity.settings.get("currency"))) {
				cll = obj;
			}
		}

		Collections.sort(curValues);

		ArrayAdapter<Category> catAdapter = new ArrayAdapter<Category>(this,
				android.R.layout.simple_spinner_item, catValues);
		ArrayAdapter<CurrencyLocale> curAdapter = new ArrayAdapter<CurrencyLocale>(this,
				android.R.layout.simple_spinner_item, curValues);
		ArrayAdapter<Journey> jouAdapter = new ArrayAdapter<Journey>(this,
				android.R.layout.simple_spinner_item, jouValues);

		catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		curAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		jouAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		catSpinner.setAdapter(catAdapter);
		curSpinner.setAdapter(curAdapter);
		curSpinner.setSelection(curValues.indexOf(cll));
		jouSpinner.setAdapter(jouAdapter);
	}

	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.addCategoryButton : {
				(new NewCategoryDialog(this.context, this.catSpinner)).show();
				break;
			}
			case R.id.addJourneyButton : {
				(new NewJourneyDialog(this.context, this.jouSpinner)).show();
				break;
			}
			case R.id.date_button : {
				DialogFragment newFragment = null;
				if (date == -1) {
					newFragment = new DatePickerFragment();
				} else {
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTimeInMillis(date*1000);
					newFragment = new DatePickerFragment(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
				}
			    newFragment.show(getSupportFragmentManager(), "datePicker");
			    break;
			}
			case R.id.time_button : {
				DialogFragment newFragment = null;
				if (time == -1) {
					newFragment = new TimePickerFragment();
				} else {
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTimeInMillis(time*1000);
					newFragment = new TimePickerFragment(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
				}
			    newFragment.show(getSupportFragmentManager(), "timePicker");
				break;
			}
		}
	}

	// Method for save new costs record
	public void saveRecord(View view) {
		// String for costs amount
		final String amount = ((EditText)findViewById(R.id.amount_input_field)).getText().toString();
		// Float number for amount format check
		double cislo;

		if (amount.trim().equals("")) {
			(new ErrorDialog(context, R.string.input_error,
					R.string.empty_amount)).show();
			return;
		}
		try {
			// Check float format
			cislo = Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			(new ErrorDialog(context, R.string.input_error,
					R.string.bad_amount_format)).show();
			return;
		}
		CurrencyLocale currency = null;
		Category category = null;
		Journey journey = null;
		// String for selected currency
		try {
			currency = (CurrencyLocale)curSpinner.getAdapter().getItem(curSpinner.getSelectedItemPosition());
		} catch (ArrayIndexOutOfBoundsException e) {
			(new ErrorDialog(context, R.string.input_error, R.string.empty_currency_name)).show();
			return;
		}
		// String for selected category
		try {
			category = (Category)catSpinner.getAdapter().getItem(catSpinner.getSelectedItemPosition());
		} catch (ArrayIndexOutOfBoundsException e) {
			(new ErrorDialog(context, R.string.input_error, R.string.empty_category_name)).show();
			return;
		}
		// String for selected journey
		try {
			journey = (Journey)jouSpinner.getAdapter().getItem(jouSpinner.getSelectedItemPosition());
		} catch (ArrayIndexOutOfBoundsException e) {
			(new ErrorDialog(context, R.string.input_error, R.string.empty_journey_name)).show();
			return;
		}

		Toast.makeText(context, R.string.saving_cost, Toast.LENGTH_SHORT).show();
		dataSource.createRecord(category.getId(), currency.getCurrency().getCurrencyCode(), journey.getId(), cislo, date, time);
		Toast.makeText(context, R.string.cost_saved, Toast.LENGTH_SHORT).show();
		this.finish();
	}

    @Override
	protected void onResume() {
		dataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dataSource.close();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		dataSource.close();
		super.onDestroy();
	}

	@Override
	public void onTimeSet(TimePicker view, int hour, int minute) {
		GregorianCalendar cal = new GregorianCalendar(1970, 0, 1, hour, minute, 0);
		if (minute < 10) {
			timeButton.setText(hour+getString(R.string.time_delimiter)+"0"+minute);
		} else {
			timeButton.setText(hour+getString(R.string.time_delimiter)+minute);
		}
		time = (cal.getTimeInMillis()/1000);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		GregorianCalendar cal = new GregorianCalendar(year, month, day, 0, 0, 0);
		dateButton.setText(day+getString(R.string.date_delimiter)+(month+1)+getString(R.string.date_delimiter)+year);
		date = (cal.getTimeInMillis()/1000);
	}
}
