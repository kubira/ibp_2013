package cz.kubira.pomocnik.logger;

public class Journey extends ParentTable {
    private String name_journey;

    public String getName() {
        return name_journey;
    }

    public void setName(String name_journey) {
        this.name_journey = name_journey;
    }

    @Override
    public String toString() {
        return name_journey;
    }

    public String toCSV() {
		return Long.toString(id)+",'"+name_journey+"'";
	}

	public String toXML() {
		return "<journey>"
				+"<id>"+Long.toString(id)+"</id>"
				+"<name>"+name_journey+"</name>"
				+"</journey>";
	}

	public String toSQL() {
		return "INSERT INTO journey VALUES("+toCSV()+");";
	}
}