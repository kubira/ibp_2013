package cz.kubira.pomocnik.logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cz.kubira.pomocnik.CurrencyLocale;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.R;

public class RecordEdit extends AddRecordActivity {

	private Record record;
	private long id_record;
	private String where;
	private String orderBy;
	private boolean convert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
	    id_record = b.getLong("id_record");
	    where = b.getString("where");
	    orderBy = b.getString("orderBy");
	    convert = b.getBoolean("convert");


	    Cursor cursor = dataSource.database.query("record", null, "id_record = "+id_record, null, null, null, null);
	    cursor.moveToFirst();
	    record = dataSource.cursorToRecord(cursor);
	    cursor.close();

        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(record.getTimestamp()*1000);
        dateButton.setText(c.get(Calendar.DAY_OF_MONTH)+getString(R.string.date_delimiter)+(c.get(Calendar.MONTH)+1)+getString(R.string.date_delimiter)+c.get(Calendar.YEAR));
        if (c.get(Calendar.MINUTE) < 10) {
			timeButton.setText(c.get(Calendar.HOUR_OF_DAY)+getString(R.string.time_delimiter)+"0"+c.get(Calendar.MINUTE));
		} else {
			timeButton.setText(c.get(Calendar.HOUR_OF_DAY)+getString(R.string.time_delimiter)+c.get(Calendar.MINUTE));
		}
        date = ((new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))).getTimeInMillis()/1000);
        time = ((new GregorianCalendar(1970, 0, 1, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))).getTimeInMillis()/1000);

	    ((EditText)findViewById(R.id.amount_input_field)).setText(String.valueOf(record.getAmount()));

	    int pocet = catSpinner.getAdapter().getCount();
	    for (int i = 0; i < pocet; i++) {
	    	if (((Category)(catSpinner.getAdapter().getItem(i))).getId() == record.getCategory()) {
	    		catSpinner.setSelection(i);
	    		break;
	    	}
	    }

	    pocet = curSpinner.getAdapter().getCount();
	    for (int i = 0; i < pocet; i++) {
	    	if (((CurrencyLocale)(curSpinner.getAdapter().getItem(i))).getCurrency().getCurrencyCode().equals(record.getCurrency())) {
	    		curSpinner.setSelection(i);
	    		break;
	    	}
	    }

	    pocet = jouSpinner.getAdapter().getCount();
	    for (int i = 0; i < pocet; i++) {
	    	if (((Journey)(jouSpinner.getAdapter().getItem(i))).getId() == record.getJourney()) {
	    		jouSpinner.setSelection(i);
	    		break;
	    	}
	    }
	}

	// Method for save new costs record
	@Override
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
		dataSource.updateRecord(id_record, category.getId(), currency.getCurrency().getCurrencyCode(),
				journey.getId(), cislo, date, time);
		Toast.makeText(context, R.string.cost_saved, Toast.LENGTH_SHORT).show();
		//Toast.makeText(context, "OK ;)", Toast.LENGTH_SHORT).show();
		startView();
	}

	public void startView() {
		Intent i = new Intent(context, ViewRecords.class);
		Bundle b = new Bundle();
		b.putString("where", where);
		b.putString("orderBy", orderBy);
		b.putBoolean("convert", convert);
		i.putExtras(b);
		startActivity(i);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){
	    	startView();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}

}
