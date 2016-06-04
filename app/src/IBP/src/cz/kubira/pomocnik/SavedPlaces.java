package cz.kubira.pomocnik;

import java.util.Iterator;

import android.app.Activity;
import android.content.res.ColorStateList;
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
import cz.kubira.pomocnik.around.PlaceMarker;

public class SavedPlaces extends Activity implements OnLongClickListener {

	private LayoutParams params;
	private TableLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    params.setMargins(5, 3, 5, 3);

	    ScrollView sv = new ScrollView(this);
	    HorizontalScrollView hsv = new HorizontalScrollView(this);
	    hsv.setFillViewport(true);
	    sv.setFillViewport(true);
        table = new TableLayout(this);
		ObjectTableRow row = new ObjectTableRow(this, null);

		TextView text = new TextView(this);
        text.setLayoutParams(params);
        text.setText("Název");
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.LEFT);
        row.addView(text);

        View x = new View(this);
        x.setBackgroundColor(Color.BLACK);
        x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));

        row.setNextView(x);
        row.setBackgroundColor(Color.DKGRAY);

        table.addView(row);

        table.addView(x);

        Iterator<PlaceMarker> i = StartActivity.markers.iterator();

        while (i.hasNext()) {
        	PlaceMarker pm = i.next();
            row = new ObjectTableRow(this, pm);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(pm.getPlace().getName());
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
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		(new PlaceInfoDialog(this, view)).show();
		return true;
	}

	public TableLayout getTable() {
		return table;
	}

	public void setTable(TableLayout table) {
		this.table = table;
	}

}
