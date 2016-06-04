package cz.kubira.pomocnik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;

public class SettingsActivity extends Activity implements OnItemSelectedListener {

	/** Spinner pro v�b�r st�tu u�ivatele. */
	private Spinner countrySpinner = null;
	/** Spinner pro v�b�r jazyka u�ivatele. */
	private Spinner languageSpinner = null;
	/** Spinner pro v�b�r m�ny u�ivatele. */
	private Spinner currencySpinner = null;
	/** Spinner pro v�b�r jednotek vzd�lenost�. */
	private Spinner measureSpinner = null;
	/** Spinner pro v�b�r jednotky informace. */
	private Spinner unitSpinner = null;
	/** Textov� pole pro limit p�enesen�ch dat. */
	private EditText limitText = null;
	/** Checkbox pro pou�it�/nepou�it� limitu p�enesen�ch dat. */
	private CheckBox limitCheckBox = null;

	private Map<String, String> settingsMap = null;
	private Settings s = null;

	private int position = 0;
	private int position2 = 0;
	private int position3 = 0;
	private CountryLocale cl = null;
	private LanguageLocale ll = null;
	private CurrencyLocale cll = null;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/** Vol�ni funkce onCreate rodi�ovsk� t��dy. */
		super.onCreate(savedInstanceState);

		/** Zru�en� titulku okna aplikace. */
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Nastaven� okna aplikace do celoobrazovkov�ho re�imu. */
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/** Nastaven� vzhledu okna - layout s nastaven�m aplikace. */
		setContentView(R.layout.activity_settings);

		s = new Settings(this);
		settingsMap = s.readFile();

		/** Z�sk�n� ISO k�d� v�ech st�t�. */
		String[] isoCountries = ULocale.getISOCountries();
		String[] isoLanguages = ULocale.getISOLanguages();

		Set<Currency> currencies = Currency.getAvailableCurrencies();
		ArrayList<CurrencyLocale> al = new ArrayList<CurrencyLocale>();

		for (Currency currency : currencies) {
			/** Vytvo�en� objektu pro jeden jazyk. */
			CurrencyLocale obj = new CurrencyLocale(currency, settingsMap.get("language"));
			/** Vlo�en� objektu do pole. */
			al.add(obj);

			if (currency.getCurrencyCode().equals(settingsMap.get("currency"))) {
				cll = obj;
			}
		}

		ArrayList<LanguageLocale> languages = new ArrayList<LanguageLocale>();

		for (String languageCode : isoLanguages) {
			/** Vytvo�en� objektu pro jeden jazyk. */
			LanguageLocale obj = new LanguageLocale(new ULocale(languageCode), settingsMap.get("language"));
			/** Vlo�en� objektu do pole. */
			languages.add(obj);

			if (languageCode.equals(settingsMap.get("language"))) {
				ll = obj;
			}
		}

		/** Inicializace pole pro n�zvy st�t�. */
		ArrayList<CountryLocale> countries = new ArrayList<CountryLocale>();

		/** Vlo�en� n�zv� v�ech st�t� do inicializovan�ho pole. */
		for (String countryCode : isoCountries) {
			/** Vytvo�en� objektu pro jeden st�t. */
			CountryLocale obj = new CountryLocale(new ULocale("", countryCode), settingsMap.get("language"));
			/** Vlo�en� objektu do pole. */
			countries.add(obj);

			if (countryCode.equals(settingsMap.get("country"))) {
				cl = obj;
			}
		}

		/** Se�azen� pole st�t� podle jejich n�zv� s respektov�n�m �azen� podle jazyka telefonu. */
		Collections.sort(countries);
		Collections.sort(languages);
		Collections.sort(al);

		position = countries.indexOf(cl);
		position2 = languages.indexOf(ll);
		position3 = al.indexOf(cll);

		/** Napln�n� spinneru pro v�b�r st�tu daty a nastaven� jeho vzhledu. */
		countrySpinner = (Spinner)findViewById(R.id.country_spinner);
		ArrayAdapter<CountryLocale> countrySpinnerArrayAdapter = new ArrayAdapter<CountryLocale>(this, android.R.layout.simple_spinner_item, countries);
		countrySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(countrySpinnerArrayAdapter);
		countrySpinner.setOnItemSelectedListener(this);
		countrySpinner.setSelection(position);

		/** Napln�n� spinneru pro v�b�r st�tu daty a nastaven� jeho vzhledu. */
		languageSpinner = (Spinner)findViewById(R.id.language_spinner);
		ArrayAdapter<LanguageLocale> languageSpinnerArrayAdapter = new ArrayAdapter<LanguageLocale>(this, android.R.layout.simple_spinner_item, languages);
		languageSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languageSpinner.setAdapter(languageSpinnerArrayAdapter);
		languageSpinner.setOnItemSelectedListener(this);
		languageSpinner.setSelection(position2);

		currencySpinner = (Spinner)findViewById(R.id.currency_spinner);
		ArrayAdapter<CurrencyLocale> currencySpinnerArrayAdapter = new ArrayAdapter<CurrencyLocale>(this, android.R.layout.simple_spinner_item, al);
		currencySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencySpinner.setAdapter(currencySpinnerArrayAdapter);
		currencySpinner.setOnItemSelectedListener(this);
		currencySpinner.setSelection(position3);

		measureSpinner = (Spinner)findViewById(R.id.measurement_spinner);
		ArrayAdapter<String> measureSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.measurement_units));
		measureSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		measureSpinner.setAdapter(measureSpinnerArrayAdapter);

		if (StartActivity.settings.get("measure").startsWith("k")) {
			measureSpinner.setSelection(0);
		} else {
			measureSpinner.setSelection(1);
		}

		/** Napln�n� spinneru pro v�b�r jednotky dat daty a nastaven� jeho vzhledu. */
		unitSpinner = (Spinner)findViewById(R.id.unit_spinner);
		ArrayAdapter<String> limitSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.limit_units));
		limitSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		unitSpinner.setAdapter(limitSpinnerArrayAdapter);

		/** Zak�z�n� funk�nosti pole pro po�et jednotek limitu p�enesen�ch dat. */
		limitText = (EditText)findViewById(R.id.limit_text);

		/** Inicializace objektu pro zatr��tko limitu p�enesen�ch dat. */
		limitCheckBox = (CheckBox)findViewById(R.id.limit_checkbox);

		if (settingsMap.get("limit").equals("no_check")) {
			limitCheckBox.setChecked(false);
			limitText.setEnabled(false);
			unitSpinner.setEnabled(false);
		} else {
			String[] limit = settingsMap.get("limit").split(":");
			limitCheckBox.setChecked(true);
			limitText.setEnabled(true);
			limitText.setText(limit[0]);
			unitSpinner.setSelection(limitSpinnerArrayAdapter.getPosition(limit[1]));
			unitSpinner.setEnabled(true);
		}
	}

	/**
	 * Funkce, kter� se vol� p�i stisknut� tla��tka
	 * pro ulo�en� u�ivatelsk�ho nastaven�.
	 * @param Tla��tko, kter� bylo stisknuto.
	 */
	public void saveButtonAction(View view) {
		/** Pokud je zatr�eno povolen� limitu, ale po�et jednotek nen� vypln�n�. */
		if (limitCheckBox.isChecked() && limitText.getText().toString().trim().equals("")) {
			// TODO: error dialog
			System.out.println("Prazdna jednotka!");
			return;
		}

		if (limitCheckBox.isChecked()) {
			/** Zap�u u�ivatelsk� nastaven� do souboru SETTINGS_FILENAME. */
			s.writeFile(((CountryLocale)countrySpinner.getSelectedItem()).locale.getCountry(),
					((LanguageLocale)languageSpinner.getSelectedItem()).locale.getLanguage(),
					((CurrencyLocale)currencySpinner.getSelectedItem()).currency.getCurrencyCode(),
					measureSpinner.getSelectedItem().toString(),
					limitText.getText()+":"+((String)unitSpinner.getSelectedItem()));
		} else {
			/** Zap�u u�ivatelsk� nastaven� do souboru SETTINGS_FILENAME. */
			s.writeFile(((CountryLocale)countrySpinner.getSelectedItem()).locale.getCountry(),
					((LanguageLocale)languageSpinner.getSelectedItem()).locale.getLanguage(),
					((CurrencyLocale)currencySpinner.getSelectedItem()).currency.getCurrencyCode(),
					measureSpinner.getSelectedItem().toString(),
					"no_check");
		}

		finish();
	}

	/**
	 * Funkce, kter� se vol� p�i zatr�en�/odtr�en� povolen� limitu p�enesen�ch dat.
	 * @param Zatr��tko, u kter�ho se zm�nil stav.
	 */
	public void limitCheckBoxAction(View view) {
		if (limitCheckBox.isChecked()) {
			limitText.setEnabled(true);
			unitSpinner.getSelectedView().setEnabled(true);
			unitSpinner.setEnabled(true);
		} else {
			limitText.setEnabled(false);
			unitSpinner.setEnabled(false);
			unitSpinner.getSelectedView().setEnabled(false);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if (count == 2) {
			switch (arg0.getId()) {
				case R.id.language_spinner : {
					System.out.println("lang");
					break;
				}
				case R.id.country_spinner : {
					System.out.println("coun");
					break;
				}
			}
		} else {
			count++;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
