package cz.kubira.pomocnik.logger;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;

import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;

public class RecordActionDialog extends Dialog implements View.OnClickListener {

	protected RecordToView record;
	protected Context context;
	protected ObjectTableRow row;

	public RecordActionDialog(Context context, View view) {
		super(context);
		row = ((ObjectTableRow)view);
		this.setRecord((RecordToView)row.getObject());
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.record_action);

		DataSource datasource = new DataSource(context);
		datasource.open();

		TextView infoDate = (TextView)findViewById(R.id.info_date);
		TextView infoJourney = (TextView)findViewById(R.id.info_journey);
		TextView infoCategory = (TextView)findViewById(R.id.info_category);
		TextView infoAmount = (TextView)findViewById(R.id.info_amount);
		TextView infoCurrency = (TextView)findViewById(R.id.info_currency);

		Button editButton = (Button)findViewById(R.id.edit_button);
		editButton.setOnClickListener(this);
		Button deleteButton = (Button)findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(this);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(record.getRecord().getTimestamp()*1000);
		String date = cal.get(Calendar.DAY_OF_MONTH)+context.getString(R.string.date_delimiter)+" "
				+(new DateFormatSymbols(Locale.getDefault())).getMonths()[cal.get(Calendar.MONTH)]
				+" "+cal.get(Calendar.YEAR)+"\n"+cal.get(Calendar.HOUR_OF_DAY)+context.getString(R.string.time_delimiter);
		if (cal.get(Calendar.MINUTE) < 10) {
        	date += "0"+cal.get(Calendar.MINUTE);
        } else {
        	date += cal.get(Calendar.MINUTE);
        }

		infoDate.setText(date);
		//infoDate.setText(String.valueOf(record.getRecord().getTimestamp()));
		infoJourney.setText(record.getJourney());
		infoCategory.setText(record.getCategory());
		infoAmount.setText(String.valueOf(record.getRecord().getAmount()));
		boolean[] x = {true};
		infoCurrency.setText(
				Currency.getInstance(record.getRecord().getCurrency())
					.getName(new ULocale(StartActivity.settings.get("language")), Currency.LONG_NAME, x)
				+" ("
				+record.getRecord().getCurrency()
				+")");

		datasource.close();
	}

	public RecordToView getRecord() {
		return record;
	}

	public void setRecord(RecordToView record) {
		this.record = record;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.edit_button: {
				Intent i = new Intent(context, RecordEdit.class);
				Bundle b = new Bundle();
				b.putLong("id_record", record.getRecord().getId());
				b.putString("where", ((ViewRecords)context).where);
				b.putString("orderBy", ((ViewRecords)context).orderBy);
				b.putBoolean("convert", ((ViewRecords)context).convert);
				i.putExtras(b);
				context.startActivity(i);
				((ViewRecords)context).finish(); // RecordView
				this.dismiss();
				break;
			}

			case R.id.delete_button: {
				(new PromptDialog(context, this)).show();
				break;
			}
		}
	}

	public ObjectTableRow getRow() {
		return row;
	}

}