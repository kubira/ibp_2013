package cz.kubira.pomocnik.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import cz.kubira.pomocnik.R;

public class DatabasePossibilities extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database_possibilities);
	}

	public void exportToCSV(View view) {
		String filename = "database_"+Calendar.getInstance().getTimeInMillis()+".csv";
		File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+"/"+filename);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DataSource ds = new DataSource(this);
		ds.open();

		pw.println("id_category,name_category");

		List<Category> lcat = ds.getAllCategories();

		for (Category cat : lcat) {
			pw.println(cat.toCSV());
		}

		pw.println();
		pw.println("id_journey,name_journey");

		List<Journey> ljou = ds.getAllJournies();

		for (Journey jou : ljou) {
			pw.println(jou.toCSV());
		}

		pw.println();
		pw.println("id_record,id_category,code_currency,id_journey,amount,timestamp");

		List<Record> lrec = ds.getAllRecords();

		for (Record rec : lrec) {
			pw.println(rec.toCSV());
		}

		pw.println();

		pw.close();
		ds.close();

		Toast.makeText(this, "Saved to "+filename+" in Downloads folder.", Toast.LENGTH_LONG).show();
	}

	public void exportToXML(View view) {
		String filename = "database_"+Calendar.getInstance().getTimeInMillis()+".xml";
		File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+"/"+filename);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DataSource ds = new DataSource(this);
		ds.open();

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		pw.println("<database>");
		pw.println("<categories>");

		List<Category> lcat = ds.getAllCategories();

		for (Category cat : lcat) {
			pw.println(cat.toXML());
		}

		pw.println("</categories>");
		pw.println("<journeys>");

		List<Journey> ljou = ds.getAllJournies();

		for (Journey jou : ljou) {
			pw.println(jou.toXML());
		}

		pw.println("</journeys>");
		pw.println("<records>");

		List<Record> lrec = ds.getAllRecords();

		for (Record rec : lrec) {
			pw.println(rec.toXML());
		}

		pw.println("</records>");
		pw.println("</database>");

		pw.close();
		ds.close();

		Toast.makeText(this, "Saved to "+filename+" in Downloads folder.", Toast.LENGTH_LONG).show();
	}

	public void exportToSQL(View view) {
		String filename = "database_"+Calendar.getInstance().getTimeInMillis()+".sql";
		File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+"/"+filename);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DataSource ds = new DataSource(this);
		ds.open();

		pw.println("CREATE DATABASE '"+SQLiteAdapter.DATABASE_NAME+"';");
		pw.println();

		pw.println(SQLiteAdapter.CREATE_CATEGORY_TABLE);
		pw.println();

		List<Category> lcat = ds.getAllCategories();

		for (Category cat : lcat) {
			pw.println(cat.toSQL());
		}

		pw.println();
		pw.println(SQLiteAdapter.CREATE_JOURNEY_TABLE);
		pw.println();

		List<Journey> ljou = ds.getAllJournies();

		for (Journey jou : ljou) {
			pw.println(jou.toSQL());
		}

		pw.println();
		pw.println(SQLiteAdapter.CREATE_RECORD_TABLE);
		pw.println();

		List<Record> lrec = ds.getAllRecords();

		for (Record rec : lrec) {
			pw.println(rec.toSQL());
		}

		pw.println();

		pw.close();
		ds.close();

		Toast.makeText(this, "Saved to "+filename+" in Downloads folder.", Toast.LENGTH_LONG).show();
	}

	public void deleteDatabase(View view) {
		(new DeleteDatabasePromptDialog(this)).show();
	}
}
