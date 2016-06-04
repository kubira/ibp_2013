package cz.kubira.pomocnik.logger;

public class Category extends ParentTable {
    private String name_category;

    public String getName() {
        return name_category;
    }

    public void setName(String name_category) {
        this.name_category = name_category;
    }

    @Override
    public String toString() {
        return name_category;
    }

    public String toCSV() {
		return Long.toString(id)+",'"+name_category+"'";
	}

	public String toXML() {
		return "<category>"
				+"<id>"+Long.toString(id)+"</id>"
				+"<name>"+name_category+"</name>"
				+"</category>";
	}

	public String toSQL() {
		return "INSERT INTO category VALUES("+toCSV()+");";
	}
}