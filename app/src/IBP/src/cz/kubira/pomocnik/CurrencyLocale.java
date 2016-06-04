package cz.kubira.pomocnik;

import com.ibm.icu.text.Collator;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;

public class CurrencyLocale implements Comparable<CurrencyLocale> {

	protected Currency currency;
	protected String language;
	boolean[] x = {true};

	public CurrencyLocale(Currency currency, String language) {
		this.currency = currency;
		this.language = language;
	}

	@Override
	public int compareTo(CurrencyLocale another) {
		Collator c = Collator.getInstance(new ULocale(language));
		return c.compare(this.currency.getName(new ULocale(language), Currency.LONG_NAME, x),
				another.currency.getName(new ULocale(language), Currency.LONG_NAME, x));
	}

	@Override
	public String toString() {
		return currency.getName(new ULocale(language), Currency.LONG_NAME, x)+" ("+currency.getCurrencyCode()+")";
	}

	public Currency getCurrency() {
		return currency;
	}
}