package cz.kubira.pomocnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cz.kubira.pomocnik.R;

public class InformationSharing extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information_sharing);
	}

	public void facebookShare(View view) {
		Intent intent = new Intent(this, FacebookShare.class);
		startActivity(intent);
	}

}
