package cz.kubira.pomocnik.logger;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;

public class ViewJourneys extends Activity implements View.OnLongClickListener {

	private DataSource datasource;
	private TableLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		datasource = new DataSource(this);
	    datasource.open();

	    List<Journey> values = datasource.getAllJournies();

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
        text.setText("Název cesty");
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        View x = new View(this);
        x.setBackgroundColor(Color.BLACK);
        x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));

        row.setNextView(x);
        row.setBackgroundColor(Color.DKGRAY);
        table.addView(row);

        table.addView(x);

        Iterator<Journey> i = values.iterator();

        while (i.hasNext()) {
        	Journey r = i.next();
            row = new ObjectTableRow(this, r);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(r.getName());
            text.setTextSize((float) 18);
            row.addView(text);

            row.setOnLongClickListener(this);
            row.setBackgroundResource(android.R.drawable.list_selector_background);

            x = new View(this);
            x.setBackgroundColor(Color.GRAY);
            x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));

            row.setNextView(x);

            table.addView(row);

            table.addView(x);
        }

	    hsv.addView(table);

	    sv.addView(hsv);

	    setContentView(sv);

	    datasource.close();
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		(new ViewJourneyDialog((Context)this, view)).show();
		return true;
	}

	public TableLayout getTable() {
		return table;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_journeys_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.add_journey : {
		    	(new NewJourneyDialog(this, this)).show();
		        return true;
		    }
		    default : {
		        return super.onOptionsItemSelected(item);
		    }
	    }
	}
}
