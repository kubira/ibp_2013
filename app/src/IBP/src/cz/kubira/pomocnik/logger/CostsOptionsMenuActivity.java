package cz.kubira.pomocnik.logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cz.kubira.pomocnik.R;

public class CostsOptionsMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costs_options_menu);
	}

	public void onCategory(View view) {
		Intent i = new Intent(this, ViewCategories.class);
		startActivity(i);
	}

	public void onJourney(View view) {
		Intent i = new Intent(this, ViewJourneys.class);
		startActivity(i);
	}
}
