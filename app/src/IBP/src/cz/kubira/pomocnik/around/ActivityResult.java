package cz.kubira.pomocnik.around;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import cz.kubira.pomocnik.AsyncTaskCompleteListener;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.NetworkDialog;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.StaticMethods.Units;
import cz.kubira.pomocnik.NetworkTask;
import cz.kubira.pomocnik.logger.DataSource;

public class ActivityResult extends Activity
	implements AsyncTaskCompleteListener<String>, OnLongClickListener, OnCancelListener {

    private ProgressDialog pDialog;
    private TableLayout table;
    private LayoutParams params;
    private int l = 0;
    private int distance = 0;
    private String type = null;
    private ObjectTableRow head = null;
    private final String KEY = "AIzaSyA-JR--NwitjrC2EHF4dDD_l5xkF3qBFmQ";
    double lat = 0;
    double lon = 0;
    private NetworkTask a;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
	    l = b.getInt("location");
	    distance = b.getInt("distance");
	    type = b.getString("type");

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
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText("Vzdálenost");
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.RIGHT);
        row.addView(text);

        text = new TextView(this);
        text.setLayoutParams(params);
        text.setText("Adresa");
        text.setTextSize((float) 18);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(text);

        View x = new View(this);
        x.setBackgroundColor(Color.BLACK);
        x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));

        row.setNextView(x);
        row.setBackgroundColor(Color.DKGRAY);

        head = row;

        table.addView(row);

        table.addView(x);

	    hsv.addView(table);

	    sv.addView(hsv);

	    setContentView(sv);

	    search();
    }

    public void search() {
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

	    if (!StaticMethods.isConnectivity(this)) {
	    	(new NetworkDialog((Context)this, R.string.net_error, R.string.no_connection_text)).show();
	    	return;
	    }

	    if (l == R.id.camera) {
	    	System.out.println("Loc: støed obrazovky");
	    	lat = StartActivity.map.getCameraPosition().target.latitude;
		    lon = StartActivity.map.getCameraPosition().target.longitude;
	    } else if (l == R.id.actual) {
	    	System.out.println("Loc: aktuální poloha");
	    	lat = StartActivity.location.getLatitude();
	    	lon = StartActivity.location.getLongitude();
	    } else {
	    	System.out.println("Loc: NIC");
	    }

    	a = new NetworkTask(this, this);
    	pDialog = ProgressDialog.show(this, "Hledání", "Hledám");
    	pDialog.setCancelable(true);
    	pDialog.setOnCancelListener(this);
    	pDialog.setCancelable(true);
    	a.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?"
    			+"key="+KEY
    			+"&location="+lat+","+lon
    			+"&radius="+(distance < 100 ? distance * 1000 : distance)
    			+"&sensor=true&types="
    			+type);
    }

	@Override
	public void onTaskComplete(String result) {
		// TODO Auto-generated method stub
		Document document = getDomElement(result);
		Element e = document.getDocumentElement();
		NodeList results = e.getElementsByTagName("result");
		List<Result> list = new ArrayList<Result>();
		Location myLocation = new Location("center");
		myLocation.setLatitude(lat);
		myLocation.setLongitude(lon);
		Result r = null;
		for (int i = 0; i < results.getLength(); i++) {
			r = new Result((Element)results.item(i), myLocation, type);
			list.add(r);
		}
		Collections.sort(list);

		Iterator<Result> i = list.iterator();

        while (i.hasNext()) {
    		ObjectTableRow row = new ObjectTableRow(this, null);
    		Result hi = i.next();
    		row.setObject(hi);

    		TextView text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(hi.name);
            text.setTextSize((float) 18);
            text.setGravity(Gravity.LEFT);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(StaticMethods.getNiceMeasure(hi.distance, Units.METER));
            text.setTextSize((float) 18);
            text.setGravity(Gravity.RIGHT);
            row.addView(text);

            text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(hi.address);
            text.setTextSize((float) 18);
            text.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(text);

            View x = new View(this);
            x.setBackgroundColor(Color.BLACK);
            x.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));

            row.setNextView(x);
            row.setOnLongClickListener(this);
            row.setBackgroundResource(android.R.drawable.list_selector_background);
            table.addView(row);

            table.addView(x);
        }
        pDialog.dismiss();
	}

	public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is);

            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		(new ResultInfoDialog(this, view)).show();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hotels_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.search : {
		    	table.removeAllViews();
		    	table.addView(head);
		    	table.addView(head.getNextView());
		    	search();
		        return true;
		    }
		    default : {
		        return super.onOptionsItemSelected(item);
		    }
	    }
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		a.cancel(true);
	}
}
