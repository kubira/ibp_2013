package cz.kubira.pomocnik.logger;

public class ParentTable {
	protected long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return Long.toString(id);
	}
}