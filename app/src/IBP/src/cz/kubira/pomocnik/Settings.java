package cz.kubira.pomocnik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cz.kubira.pomocnik.logger.DataSource;

public class Settings {

	/** Konstanta pro index klíèe. */
	private final int KEY = 0;
	/** Konstanta pro index hodnoty. */
	private final int VALUE = 1;
	/** Mapa uživatelského nastavení. */
	private Map<String, String> settingsMap = new HashMap<String, String>();
	/** Název souboru s uživatelským nastavením. */
	private String SETTINGS_FILENAME = null;
	private Context context;

	public Settings(Context context) {
		this.context = context;
		SETTINGS_FILENAME = context.getFilesDir().getPath()+"/settings";
		System.out.println(SETTINGS_FILENAME);
	}

	/**
	 * Metoda ète uživatelské nastavení ze souboru SETTINGS_FILENAME
	 * a ukládá jednotlivé položky do mapy settingsMap.
	 */
	public Map<String, String> readFile() {
		String s = null;
		String[] line = null;

		try {
			File f = new File(SETTINGS_FILENAME);
			FileInputStream fis = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			while ((s = reader.readLine()) != null) {
				line = s.split("=");
				settingsMap.put(line[KEY], line[VALUE]);
			}

			reader.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setBytes();

		return settingsMap;
	}

	/**
	 * Metoda ukládá uživatelské nastavení z mapy settingsMap
	 * do souboru SETTINGS_FILENAME.
	 */
	public void writeFile(String country, String language, String currency, String measure, String limit) {

		settingsMap = new HashMap<String, String>();
		settingsMap.put("country", country);
		settingsMap.put("language", language);
		settingsMap.put("currency", currency);
		settingsMap.put("limit", limit);
		settingsMap.put("measure", measure);

		if (limit.equals("no_check")) {
			(new DataSource(context)).dropBytes();
		}

		String settingsString = String.format("country=%s\nlanguage=%s\ncurrency=%s\nmeasure=%s\nlimit=%s",
				country,
				language,
				currency,
				measure,
				limit);

		try {
			File f = new File(SETTINGS_FILENAME);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(settingsString.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(readFile());

		setBytes();

		StartActivity.settings = settingsMap;
	}

	public void setBytes() {
		String[] limit = settingsMap.get("limit").split(":");
		if (limit.length == 1) {
			StartActivity.bytes = 0;
		} else if (limit[1].equals("kB")) {
			StartActivity.bytes = Long.parseLong(limit[0]) * 1024;
		} else if (limit[1].equals("MB")) {
			StartActivity.bytes = Long.parseLong(limit[0]) * 1024 * 1024;
		} else if (limit[1].equals("GB")) {
			StartActivity.bytes = Long.parseLong(limit[0]) * 1024 * 1024 * 1024;
		}
	}
}
