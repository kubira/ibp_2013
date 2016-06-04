package cz.kubira.pomocnik.logger;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.R;

public class RecordMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_menu);
	}


	public void onPridej(View view) {
		Intent intent = new Intent(this, AddRecordActivity.class);
		startActivity(intent);
	}

	public void onFiltr(View view) {
		DataSource ds = new DataSource(this);
		ds.open();
		Cursor c = ds.database.query("record", null, null, null, null, null, null);

		if (c.getCount() == 0) {
			(new ErrorDialog(this, R.string.information, R.string.no_costs)).show();
			c.close();
			ds.close();
			return;
		}

		Intent intent = new Intent(this, FilterRecordActivity.class);
		startActivity(intent);

		c.close();
		ds.close();
	}

	public void onManage(View view) {
		Intent intent = new Intent(this, CostsOptionsMenuActivity.class);
		startActivity(intent);
	}

	public void onPossibilities(View view) {
		Intent intent = new Intent(this, DatabasePossibilities.class);
		startActivity(intent);
	}
}
