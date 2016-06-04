package cz.kubira.pomocnik.logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.ibm.icu.util.Currency;

import cz.kubira.pomocnik.CurrencyLocale;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.NetworkDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;
import cz.kubira.pomocnik.StaticMethods;

public class FilterRecordActivity extends FragmentActivity
	implements OnCheckedChangeListener, DatePickerDialog.OnDateSetListener {

	private Spinner catSpinner;
	private Spinner curSpinner;
	private Spinner jouSpinner;
	private Spinner ordSpinner;

	private CheckBox catBox;
	private CheckBox curBox;
	private CheckBox jouBox;
	private CheckBox ordBox;
	private CheckBox boxDateFrom;
	private CheckBox boxDateTo;
	private CheckBox boxAmountFrom;
	private CheckBox boxAmountTo;
	private CheckBox boxConvert;

	private Button buttonDateFrom;
	private Button buttonDateTo;

	private EditText textAmountFrom;
	private EditText textAmountTo;

	final int FROM = 1;
	final int TO = 2;
	private int type = 0;

	private long from = -1;
	private long to = -1;

	final Context context = this;  // Activity context
	private DataSource dataSource;

	private String[] order = {"id_category", "code_currency", "id_journey",
			"amount", "timestamp"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_record);

		dataSource = new DataSource(context);
		dataSource.open();

		Calendar c = Calendar.getInstance();
		String datum = c.get(Calendar.DAY_OF_MONTH)+getString(R.string.date_delimiter)+(c.get(Calendar.MONTH)+1)
				+getString(R.string.date_delimiter)+c.get(Calendar.YEAR);

		buttonDateFrom = (Button)findViewById(R.id.buttonDateFrom);
		buttonDateFrom.setText(datum);
		buttonDateFrom.setEnabled(false);
		from = (new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH), 0, 0, 0)).getTimeInMillis()/1000;

		buttonDateTo = (Button)findViewById(R.id.buttonDateTo);
		buttonDateTo.setText(datum);
		buttonDateTo.setEnabled(false);
		to = (new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 23, 59, 59)).getTimeInMillis()/1000;

		textAmountFrom = (EditText)findViewById(R.id.textAmountFrom);
		textAmountFrom.setEnabled(false);

		textAmountTo = (EditText)findViewById(R.id.textAmountTo);
		textAmountTo.setEnabled(false);

		catBox = (CheckBox)findViewById(R.id.boxCategory);
		catBox.setOnCheckedChangeListener(this);
		curBox = (CheckBox)findViewById(R.id.boxCurrency);
		curBox.setOnCheckedChangeListener(this);
		jouBox = (CheckBox)findViewById(R.id.boxJourney);
		jouBox.setOnCheckedChangeListener(this);
		ordBox = (CheckBox)findViewById(R.id.boxOrderBy);
		ordBox.setOnCheckedChangeListener(this);
		boxDateFrom = (CheckBox)findViewById(R.id.boxDateFrom);
		boxDateFrom.setOnCheckedChangeListener(this);
		boxDateTo = (CheckBox)findViewById(R.id.boxDateTo);
		boxDateTo.setOnCheckedChangeListener(this);
		boxAmountFrom = (CheckBox)findViewById(R.id.boxAmountFrom);
		boxAmountFrom.setOnCheckedChangeListener(this);
		boxAmountTo = (CheckBox)findViewById(R.id.boxAmountTo);
		boxAmountTo.setOnCheckedChangeListener(this);

		boxConvert = (CheckBox)findViewById(R.id.boxConvert);

		catSpinner = (Spinner)findViewById(R.id.spinCategory);
		curSpinner = (Spinner)findViewById(R.id.spinCurrency);
		jouSpinner = (Spinner)findViewById(R.id.spinJourney);
		ordSpinner = (Spinner)findViewById(R.id.spinOrderBy);

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
		ArrayAdapter<CharSequence> ordAdapter = ArrayAdapter.createFromResource(this,
		        R.array.order_by, android.R.layout.simple_spinner_item);


		catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		curAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		jouAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		catSpinner.setAdapter(catAdapter);
		curSpinner.setAdapter(curAdapter);
		curSpinner.setSelection(curValues.indexOf(cll));
		jouSpinner.setAdapter(jouAdapter);
		ordSpinner.setAdapter(ordAdapter);

		catSpinner.setEnabled(false);
		curSpinner.setEnabled(false);
		jouSpinner.setEnabled(false);
		ordSpinner.setEnabled(false);

		dataSource.close();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.boxCategory : {
				catSpinner.setEnabled(isChecked);
				catSpinner.getSelectedView().setEnabled(isChecked);
				break;
			}
			case R.id.boxCurrency : {
				curSpinner.setEnabled(isChecked);
				curSpinner.getSelectedView().setEnabled(isChecked);
				break;
			}
			case R.id.boxJourney : {
				jouSpinner.setEnabled(isChecked);
				jouSpinner.getSelectedView().setEnabled(isChecked);
				break;
			}
			case R.id.boxOrderBy : {
				ordSpinner.setEnabled(isChecked);
				ordSpinner.getSelectedView().setEnabled(isChecked);
				break;
			}
			case R.id.boxDateFrom : {
				buttonDateFrom.setEnabled(isChecked);
				break;
			}
			case R.id.boxDateTo : {
				buttonDateTo.setEnabled(isChecked);
				break;
			}
			case R.id.boxAmountFrom : {
				textAmountFrom.setEnabled(isChecked);
				break;
			}
			case R.id.boxAmountTo : {
				textAmountTo.setEnabled(isChecked);
				break;
			}
		}
	}

	public void zobraz(View view) {
		if (boxConvert.isChecked() && !StaticMethods.isConnectivity(this)) {
			(new NetworkDialog(this, R.string.net_error, R.string.no_connection_text)).show();
			return;
		}

		String where = null;
		String orderBy = null;
		if (catBox.isChecked()) {
			where = "id_category = "+((Category)catSpinner.getSelectedItem()).getId();
		}
		if (curBox.isChecked()) {
			if (where == null) {
				where = "code_currency = '"+((CurrencyLocale)curSpinner.getSelectedItem()).getCurrency().getCurrencyCode()+"'";
			} else {
				where += " AND code_currency = "+((CurrencyLocale)curSpinner.getSelectedItem()).getCurrency().getCurrencyCode()+"'";
			}
		}
		if (jouBox.isChecked()) {
			if (where == null) {
				where = "id_journey = "+((Journey)jouSpinner.getSelectedItem()).getId();
			} else {
				where += " AND id_journey = "+((Journey)jouSpinner.getSelectedItem()).getId();
			}
		}
		if (ordBox.isChecked()) {
			orderBy = order[ordSpinner.getSelectedItemPosition()];
		}
		if (boxDateFrom.isChecked() && boxDateTo.isChecked()) {
			if (from > to) {
				(new ErrorDialog(context, R.string.input_error, R.string.bad_interval_date)).show();
				return;
			}
		}
		if (boxDateFrom.isChecked()) {
			if (where == null) {
				where = "timestamp >= "+from;
			} else {
				where += " AND timestamp >= "+from;
			}
		}
		if (boxDateTo.isChecked()) {
			if (where == null) {
				where = "timestamp <= "+to;
			} else {
				where += " AND timestamp <= "+to;
			}
		}
		double amountFrom = 0;
		double amountTo = 0;
		String strAmountFrom = ((EditText)findViewById(R.id.textAmountFrom)).getText().toString();
		String strAmountTo = ((EditText)findViewById(R.id.textAmountTo)).getText().toString();

		if (boxAmountFrom.isChecked()) {
			if (strAmountFrom.trim().equals("")) {
				(new ErrorDialog(context, R.string.input_error,
						R.string.empty_amount_from)).show();
				return;
			}
			try {
				// Check float format
				amountFrom = Double.parseDouble(strAmountFrom);
			} catch (NumberFormatException e) {
				(new ErrorDialog(context, R.string.input_error,
						R.string.bad_amount_from_format)).show();
				return;
			}
		}

		if (boxAmountTo.isChecked()) {
			if (strAmountTo.trim().equals("")) {
				(new ErrorDialog(context, R.string.input_error,
						R.string.empty_amount_to)).show();
				return;
			}
			try {
				// Check float format
				amountTo = Double.parseDouble(strAmountTo);
			} catch (NumberFormatException e) {
				(new ErrorDialog(context, R.string.input_error,
						R.string.bad_amount_to_format)).show();
				return;
			}
		}

		if (boxAmountFrom.isChecked() && boxAmountTo.isChecked()) {
			if (amountFrom > amountTo) {
				(new ErrorDialog(context, R.string.input_error, R.string.bad_interval_amount)).show();
				return;
			}
		}
		if (boxAmountFrom.isChecked()) {
			if (where == null) {
				where = "amount >= "+amountFrom;
			} else {
				where += " AND amount >= "+amountFrom;
			}
		}
		if (boxAmountTo.isChecked()) {
			if (where == null) {
				where = "amount <= "+amountTo;
			} else {
				where += " AND amount <= "+amountTo;
			}
		}

		if (boxConvert.isChecked() && StartActivity.bytes > 0) {
			DataSource datasource = new DataSource(this);
			long percent = (long) ((datasource.getBytes()/(double)StartActivity.bytes)*100);
			System.out.println("Percent: "+percent);
			if (percent >= 75) {
				(new ErrorDialog(this,
						R.string.data_limit,
						this.getString(R.string.data_limit_text)
						+": "+percent+"%")).show();
				if (percent >= 100) {
					return;
				}
			}
		}

		Intent i = new Intent(this, ViewRecords.class);
		Bundle b = new Bundle();
		b.putString("where", where);
		b.putString("orderBy", orderBy);
		b.putBoolean("convert", boxConvert.isChecked());
		i.putExtras(b);
		startActivity(i);
	}

	public void showDatePickerDialogFrom(View v) {
		this.type = FROM;
		DialogFragment newFragment = null;
		if (from == -1) {
			newFragment = new DatePickerFragment();
		} else {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(from*1000);
			newFragment = new DatePickerFragment(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showDatePickerDialogTo(View v) {
		this.type = TO;
		DialogFragment newFragment = null;
		if (to == -1) {
			newFragment = new DatePickerFragment();
		} else {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(to*1000);
			newFragment = new DatePickerFragment(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		GregorianCalendar cal;

		switch (this.type) {
			case FROM : {
				cal = new GregorianCalendar(year, month, day, 0, 0, 0);
				from = cal.getTimeInMillis()/1000;
				buttonDateFrom.setText(day+getString(R.string.date_delimiter)+(month+1)+getString(R.string.date_delimiter)+year);
				break;
			}
			case TO : {
				cal = new GregorianCalendar(year, month, day, 23, 59, 59);
				to = cal.getTimeInMillis()/1000;
				buttonDateTo.setText(day+getString(R.string.date_delimiter)+(month+1)+getString(R.string.date_delimiter)+year);
				break;
			}
		}
	}
}
