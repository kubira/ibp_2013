package cz.kubira.pomocnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cz.kubira.pomocnik.NetworkDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.SettingsActivity;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.converter.Converter;
import cz.kubira.pomocnik.logger.RecordMenuActivity;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	}

	public void startTrackPosition(View view) {
		Intent intent = new Intent(this, TrackPosition.class);
		startActivity(intent);
	}

	public void startTouristPOIs(View view) {
		Intent intent = new Intent(this, TouristPOIs.class);
		startActivity(intent);
	}

	public void startServicesAround(View view) {
		Intent intent = new Intent(this, ServicesAround.class);
		startActivity(intent);
	}

	public void startCurrencyConverter(View view) {
		Intent intent = new Intent(this, Converter.class);
		startActivity(intent);
	}

	public void startTranslator(View view) {
		if (StaticMethods.isConnectivity(this)) {
			Intent intent = new Intent(this, Translator.class);
			startActivity(intent);
		} else {
			(new NetworkDialog(this, R.string.net_error, R.string.no_connection_text)).show();
		}
	}

	public void startCostsLogger(View view) {
		Intent intent = new Intent(this, RecordMenuActivity.class);
		startActivity(intent);
	}

	public void startInformationSharing(View view) {
		Intent intent = new Intent(this, InformationSharing.class);
		startActivity(intent);
	}

	public void startMoreDetails(View view) {
		Intent intent = new Intent(this, MoreDetails.class);
		startActivity(intent);
	}

	public void startSettings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

}
