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

	/** Spinner pro výbìr státu uživatele. */
	private Spinner countrySpinner = null;
	/** Spinner pro výbìr jazyka uživatele. */
	private Spinner languageSpinner = null;
	/** Spinner pro výbìr mìny uživatele. */
	private Spinner currencySpinner = null;
	/** Spinner pro výbìr jednotek vzdáleností. */
	private Spinner measureSpinner = null;
	/** Spinner pro výbìr jednotky informace. */
	private Spinner unitSpinner = null;
	/** Textové pole pro limit pøenesených dat. */
	private EditText limitText = null;
	/** Checkbox pro použití/nepoužití limitu pøenesených dat. */
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
		/** Voláni funkce onCreate rodièovské tøídy. */
		super.onCreate(savedInstanceState);

		/** Zrušení titulku okna aplikace. */
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Nastavení okna aplikace do celoobrazovkového režimu. */
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/** Nastavení vzhledu okna - layout s nastavením aplikace. */
		setContentView(R.layout.activity_settings);

		s = new Settings(this);
		settingsMap = s.readFile();

		/** Získání ISO kódù všech státù. */
		String[] isoCountries = ULocale.getISOCountries();
		String[] isoLanguages = ULocale.getISOLanguages();

		Set<Currency> currencies = Currency.getAvailableCurrencies();
		ArrayList<CurrencyLocale> al = new ArrayList<CurrencyLocale>();

		for (Currency currency : currencies) {
			/** Vytvoøení objektu pro jeden jazyk. */
			CurrencyLocale obj = new CurrencyLocale(currency, settingsMap.get("language"));
			/** Vložení objektu do pole. */
			al.add(obj);

			if (currency.getCurrencyCode().equals(settingsMap.get("currency"))) {
				cll = obj;
			}
		}

		ArrayList<LanguageLocale> languages = new ArrayList<LanguageLocale>();

		for (String languageCode : isoLanguages) {
			/** Vytvoøení objektu pro jeden jazyk. */
			LanguageLocale obj = new LanguageLocale(new ULocale(languageCode), settingsMap.get("language"));
			/** Vložení objektu do pole. */
			languages.add(obj);

			if (languageCode.equals(settingsMap.get("language"))) {
				ll = obj;
			}
		}

		/** Inicializace pole pro názvy státù. */
		ArrayList<CountryLocale> countries = new ArrayList<CountryLocale>();

		/** Vložení názvù všech státù do inicializovaného pole. */
		for (String countryCode : isoCountries) {
			/** Vytvoøení objektu pro jeden stát. */
			CountryLocale obj = new CountryLocale(new ULocale("", countryCode), settingsMap.get("language"));
			/** Vložení objektu do pole. */
			countries.add(obj);

			if (countryCode.equals(settingsMap.get("country"))) {
				cl = obj;
			}
		}

		/** Seøazení pole státù podle jejich názvù s respektováním øazení podle jazyka telefonu. */
		Collections.sort(countries);
		Collections.sort(languages);
		Collections.sort(al);

		position = countries.indexOf(cl);
		position2 = languages.indexOf(ll);
		position3 = al.indexOf(cll);

		/** Naplnìní spinneru pro výbìr státu daty a nastavení jeho vzhledu. */
		countrySpinner = (Spinner)findViewById(R.id.country_spinner);
		ArrayAdapter<CountryLocale> countrySpinnerArrayAdapter = new ArrayAdapter<CountryLocale>(this, android.R.layout.simple_spinner_item, countries);
		countrySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(countrySpinnerArrayAdapter);
		countrySpinner.setOnItemSelectedListener(this);
		countrySpinner.setSelection(position);

		/** Naplnìní spinneru pro výbìr státu daty a nastavení jeho vzhledu. */
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

		/** Naplnìní spinneru pro výbìr jednotky dat daty a nastavení jeho vzhledu. */
		unitSpinner = (Spinner)findViewById(R.id.unit_spinner);
		ArrayAdapter<String> limitSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.limit_units));
		limitSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		unitSpinner.setAdapter(limitSpinnerArrayAdapter);

		/** Zakázání funkènosti pole pro poèet jednotek limitu pøenesených dat. */
		limitText = (EditText)findViewById(R.id.limit_text);

		/** Inicializace objektu pro zatržítko limitu pøenesených dat. */
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
	 * Funkce, která se volá pøi stisknutí tlaèítka
	 * pro uložení uživatelského nastavení.
	 * @param Tlaèítko, které bylo stisknuto.
	 */
	public void saveButtonAction(View view) {
		/** Pokud je zatrženo povolení limitu, ale poèet jednotek není vyplnìný. */
		if (limitCheckBox.isChecked() && limitText.getText().toString().trim().equals("")) {
			// TODO: error dialog
			System.out.println("Prazdna jednotka!");
			return;
		}

		if (limitCheckBox.isChecked()) {
			/** Zapíšu uživatelské nastavení do souboru SETTINGS_FILENAME. */
			s.writeFile(((CountryLocale)countrySpinner.getSelectedItem()).locale.getCountry(),
					((LanguageLocale)languageSpinner.getSelectedItem()).locale.getLanguage(),
					((CurrencyLocale)currencySpinner.getSelectedItem()).currency.getCurrencyCode(),
					measureSpinner.getSelectedItem().toString(),
					limitText.getText()+":"+((String)unitSpinner.getSelectedItem()));
		} else {
			/** Zapíšu uživatelské nastavení do souboru SETTINGS_FILENAME. */
			s.writeFile(((CountryLocale)countrySpinner.getSelectedItem()).locale.getCountry(),
					((LanguageLocale)languageSpinner.getSelectedItem()).locale.getLanguage(),
					((CurrencyLocale)currencySpinner.getSelectedItem()).currency.getCurrencyCode(),
					measureSpinner.getSelectedItem().toString(),
					"no_check");
		}

		finish();
	}

	/**
	 * Funkce, která se volá pøi zatržení/odtržení povolení limitu pøenesených dat.
	 * @param Zatržítko, u kterého se zmìnil stav.
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
