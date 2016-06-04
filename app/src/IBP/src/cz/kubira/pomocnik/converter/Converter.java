package cz.kubira.pomocnik.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;

import cz.kubira.pomocnik.AsyncTaskCompleteListener;
import cz.kubira.pomocnik.CurrencyLocale;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.NetworkDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.NetworkTask;
import cz.kubira.pomocnik.logger.DataSource;

public class Converter extends Activity implements AsyncTaskCompleteListener<String>, OnItemSelectedListener, OnCancelListener {

	private ProgressDialog pDialog;
    private TextView curs;
    private TextView output;
    private EditText input;
    private Spinner from;
    private Spinner to;
    private DataSource dataSource;
    private int positionFrom = -1;
    private int positionTo = -1;
    private double rate = 1;
    private boolean actualCurrencyInProgress = false;
    private ArrayList<CurrencyLocale> curValues = null;
    private NetworkTask a;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_converter);

		if (StartActivity.location != null) {
		    if (StaticMethods.isConnectivity(this)) {
		    	if (StartActivity.bytes > 0) {
			    	DataSource datasource = new DataSource(this);
					long percent = (long) ((datasource.getBytes()/(double)StartActivity.bytes)*100);
					System.out.println("Percent: "+percent);
					if (percent >= 75) {
						(new ErrorDialog(this,
								R.string.data_limit,
								this.getString(R.string.data_limit_text)
								+": "+percent+"%")).show();
						if (percent < 100) {
							NetworkTask t = new NetworkTask(this, this);
					    	actualCurrencyInProgress = true;
					    	t.execute("http://ws.geonames.org/countryCode?"
					    			+"lat="+StartActivity.location.getLatitude()
					    			+"&lng="+StartActivity.location.getLongitude());
						}
					}
		    	} else {
		    		NetworkTask t = new NetworkTask(this, this);
			    	actualCurrencyInProgress = true;
			    	t.execute("http://ws.geonames.org/countryCode?"
			    			+"lat="+StartActivity.location.getLatitude()
			    			+"&lng="+StartActivity.location.getLongitude());
		    	}
		    }
		}

		dataSource = new DataSource(this);
		dataSource.open();

		curs = (TextView)findViewById(R.id.curs);
		curs.setTextColor(ColorStateList.valueOf(Color.CYAN));
		input = (EditText)findViewById(R.id.input);
		output = (TextView)findViewById(R.id.output);
		output.setTextColor(ColorStateList.valueOf(Color.YELLOW));
		from = (Spinner)findViewById(R.id.from);
		from.setOnItemSelectedListener(this);
		to = (Spinner)findViewById(R.id.to);
		from.setOnItemSelectedListener(this);

		Set<Currency> currencies = Currency.getAvailableCurrencies();
		curValues = new ArrayList<CurrencyLocale>();

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

		ArrayAdapter<CurrencyLocale> curAdapter = new ArrayAdapter<CurrencyLocale>(this,
				android.R.layout.simple_spinner_item, curValues);
		curAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		from.setAdapter(curAdapter);
		from.setSelection(curValues.indexOf(cll));
		to.setAdapter(curAdapter);
		to.setSelection(curValues.indexOf(cll));

		dataSource.close();
	}

	public void search(View view) {
		if (from.getSelectedItemPosition() == to.getSelectedItemPosition()) {
			rate = 1;
			preved();
		} else if (positionFrom == from.getSelectedItemPosition() && positionTo == to.getSelectedItemPosition()) {
			preved();
		} else {
		    if (!StaticMethods.isConnectivity(this)) {
		    	curs.setText("Aktuální kurz");
		    	(new NetworkDialog((Context)this, R.string.net_error, R.string.no_connection_text)).show();
		    	return;
		    }

			positionFrom = from.getSelectedItemPosition();
			positionTo = to.getSelectedItemPosition();

			if (StartActivity.bytes > 0) {
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

	    	a = new NetworkTask(this, this);
	    	pDialog = ProgressDialog.show(this, "Hledání kurzu", "Hledám");
	    	pDialog.setCancelable(true);
	    	pDialog.setOnCancelListener(this);
	        a.execute("http://finance.yahoo.com/d/quotes.csv?s="
	    	+((CurrencyLocale)from.getSelectedItem()).getCurrency().getCurrencyCode()
	    	+((CurrencyLocale)to.getSelectedItem()).getCurrency().getCurrencyCode()
	    	+"=X&f=l1");
		}
    }

	public void prohod(View view) {
		int swap = from.getSelectedItemPosition();
		from.setSelection(to.getSelectedItemPosition());
		to.setSelection(swap);

		if (StaticMethods.isConnectivity(this)) {
			positionFrom = from.getSelectedItemPosition();
			positionTo = to.getSelectedItemPosition();
	    }

		rate = 1 / rate;

		curs.setText(String.format("%,.4f", rate));

		output.setText("Výsledek");
	}

	@Override
	public void onTaskComplete(String result) {
		if (actualCurrencyInProgress) {
			String code = Currency.getInstance(new ULocale("", result)).getCurrencyCode();

			for (CurrencyLocale currency : curValues) {
				if (currency.getCurrency().getCurrencyCode().equals(code)) {
					to.setSelection(curValues.indexOf(currency));
					break;
				}
			}
			actualCurrencyInProgress = false;
		} else {
			rate = Double.parseDouble(result);
			if (rate == 0) {
				(new ErrorDialog(this, R.string.unknow_rate, R.string.unknow_rate_text)).show();
			}
			preved();
	        pDialog.dismiss();
		}
	}

	public void preved() {
		double in;

		curs.setText(String.format("%,.4f", rate));
		String i = input.getText().toString();

		if (i.trim().equals("")) {
			if (pDialog != null) {
				pDialog.dismiss();
			}
			Toast.makeText(this, "Zadaná èástka je prázdná", Toast.LENGTH_LONG).show();
			output.setText("Výsledek");
			return;
		}
		try {
			in = Double.parseDouble(i);
		} catch (NumberFormatException e) {
			pDialog.dismiss();
			Toast.makeText(this, "Špatný formát zadané èástky", Toast.LENGTH_LONG).show();
			return;
		}
		output.setText(input.getText().toString()
				+" "
				+((CurrencyLocale)from.getSelectedItem()).getCurrency().getCurrencyCode()
				+"\n=\n"
				+String.format("%,.2f", in*rate)
				+" "
				+((CurrencyLocale)to.getSelectedItem()).getCurrency().getCurrencyCode());
		Toast.makeText(this, "Spoèteno", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		curs.setText("Aktuální kurz");
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		a.cancel(true);
	}
}
