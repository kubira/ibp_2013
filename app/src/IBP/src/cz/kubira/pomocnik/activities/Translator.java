package cz.kubira.pomocnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.ibm.icu.util.ULocale;

import cz.kubira.pomocnik.AsyncTaskCompleteListener;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StartActivity;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.NetworkTask;
import cz.kubira.pomocnik.logger.DataSource;

public class Translator extends Activity implements AsyncTaskCompleteListener<String> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_translator);

		if (StartActivity.location != null) {
		    if (StaticMethods.isConnectivity(this)) {
		    	if (StartActivity.bytes > 0) {
			    	DataSource datasource = new DataSource(this);
					long percent = (long) ((datasource.getBytes()/(double)StartActivity.bytes)*100);
					System.out.println("Percent: "+percent);
					if (percent >= 75) {
						(new ErrorDialog(this,
								R.string.data_limit,
								this.getString(R.string.data_limit_text)
								+": "+percent+"%")).show();
						if (percent < 100) {
							NetworkTask t = new NetworkTask(this, this);
					    	t.execute("http://ws.geonames.org/countryCode?"
					    			+"lat="+StartActivity.location.getLatitude()
					    			+"&lng="+StartActivity.location.getLongitude());
						} else {
							openTranslator("auto");
						}
					}
		    	} else {
		    		NetworkTask t = new NetworkTask(this, this);
			    	t.execute("http://ws.geonames.org/countryCode?"
			    			+"lat="+StartActivity.location.getLatitude()
			    			+"&lng="+StartActivity.location.getLongitude());
		    	}
		    } else {
		    	openTranslator("auto");
		    }
		} else {
			openTranslator("auto");
		}
	}

	@Override
	public void onTaskComplete(String result) {
		String toLanguage = null;
		ULocale[] allLocales = ULocale.getAvailableLocales();

		for (ULocale ul : allLocales) {
			if (ul.getCountry().equals(result)) {
				toLanguage = ul.getLanguage();
				break;
			}
		}

		if (toLanguage == null) {
			openTranslator("auto");
		} else {
			openTranslator(toLanguage);
		}
	}

	public void openTranslator(String toLanguage) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://translate.google.com/m/translate?"
						+"hl="+StartActivity.settings.get("language")
						+"&sl="+StartActivity.settings.get("language")
						+"&tl="+toLanguage));
		startActivity(browserIntent);
		finish();
	}

}
