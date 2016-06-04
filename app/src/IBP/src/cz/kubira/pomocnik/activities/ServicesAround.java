package cz.kubira.pomocnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.around.PlaceOptions;

public class ServicesAround extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_services_around);
	}

	public void onClick(View view) {
		Bundle b = new Bundle();
		Intent i = new Intent(this, PlaceOptions.class);

		switch (view.getId()) {
			case R.id.hotel_button : {
				b.putString("type", "hotel");
				b.putInt("seekBarMax", 10);
				b.putInt("seekBarStep", 2);
				b.putString("unit", "km");
				break;
			}
			case R.id.restaurant_button : {
				b.putString("type", "restaurant");
				b.putInt("seekBarMax", 20);
				b.putInt("seekBarStep", 250);
				b.putString("unit", "m");
				break;
			}
			case R.id.atm_button : {
				b.putString("type", "atm");
				b.putInt("seekBarMax", 20);
				b.putInt("seekBarStep", 250);
				b.putString("unit", "m");
				break;
			}
			case R.id.gas_station_button : {
				b.putString("type", "gas_station");
				b.putInt("seekBarMax", 10);
				b.putInt("seekBarStep", 1);
				b.putString("unit", "km");
				break;
			}
			case R.id.pharmacy_button : {
				b.putString("type", "pharmacy");
				b.putInt("seekBarMax", 20);
				b.putInt("seekBarStep", 250);
				b.putString("unit", "m");
				break;
			}
		}

		i.putExtras(b);
		startActivity(i);
	}
}
