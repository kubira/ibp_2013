package cz.kubira.pomocnik.logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import cz.kubira.pomocnik.AsyncTaskCompleteListener;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;
import cz.kubira.pomocnik.NetworkTask;

public class ViewRecords extends Activity implements OnLongClickListener, OnCancelListener, AsyncTaskCompleteListener<String> {
	private DataSource datasource;
	protected TableLayout table;
	protected String where;
    protected String orderBy;
    protected boolean convert = false;
    protected Map<String, Double> conversions = null;
    protected ProgressDialog pDialog;
    protected double sum = 0;
    private NetworkTask t;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    Bundle b = getIntent().getExtras();
	    where = b.getString("where");
	    orderBy = b.getString("orderBy");
	    convert = b.getBoolean("convert");

	    datasource = new DataSource(this);
	    datasource.open();

	    Cursor c = datasource.database.query("record", null, where, null, null, null, orderBy);
	    List<RecordToView> values = datasource.getRecordsToView(c);
	    c.close();

	    if (convert) {

	    	pDialog = ProgressDialog.show(this, "Hledání kurzù mìn", "Hledám");
	    	pDialog.setCancelable(true);
	    	pDialog.setOnCancelListener(this);
	    	conversions = new HashMap<String, Double>();

		    String[] cols = {"code_currency"};

		    c = datasource.database.query(true, "record", cols, where, null, null, null, null, null);
		    c.moveToFirst();
	 		while (!c.isAfterLast()) {
	 			System.out.println(c.getString(0));

	 			t = new NetworkTask(this, this);
	 			t.execute("http://finance.yahoo.com/d/quotes.csv?s="+c.getString(0)+StartActivity.settings.get("currency")+"=X&f=l1");
	 			try {
					conversions.put(c.getString(0), Double.valueOf(t.get()));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 			c.moveToNext();
	 		}

		    c.close();
		    pDialog.dismiss();
	    }

	    datasource.close();

	    LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    params.setMargins(5, 3, 5, 3);

	    ScrollView sv = new ScrollView(this);
	    HorizontalScrollView hsv = new HorizontalScrollView(this);
	    hsv.setFillViewport(true);
	    sv.setFillViewport(true);
        table = new TableLayout(this);

        ObjectTableRow row = new ObjectTableRow(this, null);

		TextView text = new TextView(this);
        text.setLayoutParams(params);
        text.setText(R.string.date_column);
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText(R.string.time_column);
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText(R.string.journey_column);
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText(R.string.category_column);
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText(R.string.amount_column);
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText(R.string.currency_column);
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        if (convert) {
        	text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(StartActivity.settings.get("currency"));
            text.setTextSize((float) 18);
            text.setTextColor(ColorStateList.valueOf(Color.YELLOW));
            text.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(text);
        }

        View x = new View(this);
        x.setBackgroundColor(Color.BLACK);
        x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));

        row.setNextView(x);
        row.setBackgroundColor(Color.DKGRAY);
        table.addView(row);

        table.addView(x);

        Iterator<RecordToView> i = values.iterator();

        while (i.hasNext()) {
        	RecordToView r = i.next();
            row = new ObjectTableRow(this, r);

            text = new TextView(this);
            text.setLayoutParams(params);
            Calendar cal = Calendar.getInstance();
    		cal.setTimeInMillis(r.getRecord().getTimestamp()*1000);
    		String result = "";
    		if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
    			result += "0"+cal.get(Calendar.DAY_OF_MONTH)+getString(R.string.date_delimiter);
    		} else {
    			result += cal.get(Calendar.DAY_OF_MONTH)+getString(R.string.date_delimiter);
    		}
    		if ((cal.get(Calendar.MONTH)+1) < 10) {
    			result += "0"+(cal.get(Calendar.MONTH)+1)+getString(R.string.date_delimiter);
    		} else {
    			result += (cal.get(Calendar.MONTH)+1)+getString(R.string.date_delimiter);
    		}
    		if ((cal.get(Calendar.YEAR)%100) < 10) {
    			result += "0"+(cal.get(Calendar.YEAR)%100);
    		} else {
    			result += (cal.get(Calendar.YEAR)%100);
    		}
            text.setText(result);
            text.setTextSize((float) 18);
            text.setGravity(Gravity.RIGHT);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            if (cal.get(Calendar.MINUTE) < 10) {
            	text.setText(cal.get(Calendar.HOUR_OF_DAY)+getString(R.string.time_delimiter)+"0"+cal.get(Calendar.MINUTE));
            } else {
            	text.setText(cal.get(Calendar.HOUR_OF_DAY)+getString(R.string.time_delimiter)+cal.get(Calendar.MINUTE));
            }
            text.setTextSize((float) 18);
            text.setGravity(Gravity.RIGHT);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(r.getJourney());
            text.setTextSize((float) 18);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(r.getCategory());
            text.setTextSize((float) 18);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(String.format("%,.2f", r.getRecord().getAmount()));
            text.setGravity(Gravity.RIGHT);
            text.setTextSize((float) 18);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(r.getRecord().getCurrency());
            text.setTextSize((float) 18);
            text.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(text);

            if (convert) {
            	text = new TextView(this);
	            text.setLayoutParams(params);
	            double xx = r.getRecord().getAmount()*conversions.get(r.getRecord().getCurrency());
	            sum += xx;
	            text.setText(String.format("%,.2f", xx));
	            text.setTextSize((float) 18);
	            text.setGravity(Gravity.RIGHT);
	            text.setTextColor(ColorStateList.valueOf(Color.YELLOW));
	            row.addView(text);
            }

            row.setOnLongClickListener(this);
            row.setBackgroundResource(android.R.drawable.list_selector_background);

            x = new View(this);
            x.setBackgroundColor(Color.GRAY);
            x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));

            row.setNextView(x);

            table.addView(row);

            table.addView(x);
        }

        if (convert) {
        	row = new ObjectTableRow(this, sum);

        	for (int ix = 0; ix < 5; ix++) {
        		text = new TextView(this);
        		text.setText("");
        		text.setLayoutParams(params);
        		row.addView(text);
        	}

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText("Celkem:");
            text.setTextSize((float) 18);
            text.setGravity(Gravity.RIGHT);
            text.setTextColor(ColorStateList.valueOf(Color.CYAN));
            row.addView(text);

        	text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(String.format("%,.2f", sum));
            text.setTextSize((float) 18);
            text.setGravity(Gravity.RIGHT);
            text.setTextColor(ColorStateList.valueOf(Color.CYAN));
            row.addView(text);

            x = new View(this);
            x.setBackgroundColor(Color.GRAY);
            x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));

            row.setNextView(x);

            table.addView(row);
        }

	    hsv.addView(table);

	    sv.addView(hsv);

	    setContentView(sv);
	}

	@Override
	public boolean onLongClick(View view) {
		(new RecordActionDialog(this, view)).show();
		return true;
	}

	public TableLayout getTable() {
		return this.table;
	}

	@Override
	public void onTaskComplete(String result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		t.cancel(true);
	}
}
