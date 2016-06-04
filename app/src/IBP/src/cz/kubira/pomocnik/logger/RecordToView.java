package cz.kubira.pomocnik.logger;

import java.util.Calendar;

public class RecordToView {
	private String journey;
	private String category;
	private Record record;
	private double converted = 0;

	public String getJourney() {
		return journey;
	}
	public void setJourney(String journey) {
		this.journey = journey;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public double getConverted() {
		return converted;
	}
	public void setConverted(double converted) {
		this.converted = converted;
	}

	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		this.record = record;
	}

	@Override
	public String toString() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(record.getTimestamp()*1000);
		return c.get(Calendar.DAY_OF_MONTH)+"."+(c.get(Calendar.MONTH)+1)+". "+journey+" "+category+" "+record.getAmount()+" "+record.getCurrency();
	}
}