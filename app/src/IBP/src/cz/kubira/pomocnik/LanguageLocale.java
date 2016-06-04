package cz.kubira.pomocnik;

import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;

public class LanguageLocale implements Comparable<LanguageLocale> {

	protected ULocale locale;
	protected String language;

	public LanguageLocale(ULocale locale, String language) {
		this.locale = locale;
		this.language = language;
	}

	@Override
	public int compareTo(LanguageLocale another) {
		Collator c = Collator.getInstance(new ULocale(language));
		return c.compare(this.locale.getDisplayLanguage(new ULocale(language)), another.locale.getDisplayLanguage(new ULocale(language)));
	}

	@Override
	public String toString() {
		return locale.getDisplayLanguage(new ULocale(language));
	}
}