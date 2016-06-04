package cz.kubira.pomocnik;

import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;

public class CountryLocale implements Comparable<CountryLocale> {

	protected ULocale locale;
	protected String language;

	public CountryLocale(ULocale locale, String language) {
		this.locale = locale;
		this.language = language;
	}

	@Override
	public int compareTo(CountryLocale another) {
		Collator c = Collator.getInstance(new ULocale(language));
		return c.compare(this.locale.getDisplayCountry(new ULocale(language)), another.locale.getDisplayCountry(new ULocale(language)));
	}

	@Override
	public String toString() {
		return locale.getDisplayCountry(new ULocale(language));
	}
}