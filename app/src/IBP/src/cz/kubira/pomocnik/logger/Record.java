package cz.kubira.pomocnik.logger;

public class Record extends ParentTable {
	private long id_category;
	private String code_currency;
	private long id_journey;
	private double amount;
	private long timestamp;

	public long getCategory() {
		return id_category;
	}

	public void setCategory(long id_category) {
		this.id_category = id_category;
	}

	public String getCurrency() {
		return code_currency;
	}

	public void setCurrency(String code_currency) {
		this.code_currency = code_currency;
	}

	public long getJourney() {
		return id_journey;
	}

	public void setJourney(long id_journey) {
		this.id_journey = id_journey;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return Long.toString(id) + ";" + id_category + ";" + code_currency
				+ ";" + id_journey + ";" + amount + ";" + timestamp;
	}

	public String toCSV() {
		return Long.toString(id)+","+id_category+",'"+code_currency+"',"+id_journey+","+amount+","+timestamp;
	}

	public String toXML() {
		return "<record>"
				+"<id>"+Long.toString(id)+"</id>"
				+"<category>"+id_category+"</category>"
				+"<currency>"+code_currency+"</currency>"
				+"<journey>"+id_journey+"</journey>"
				+"<amount>"+amount+"</amount>"
				+"<timestamp>"+timestamp+"</timestamp>"
				+"</record>";
	}

	public String toSQL() {
		return "INSERT INTO record VALUES("+toCSV()+");";
	}
}