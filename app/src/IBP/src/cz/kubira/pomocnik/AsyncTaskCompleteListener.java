package cz.kubira.pomocnik;

public interface AsyncTaskCompleteListener<T> {
	public void onTaskComplete(T result);
}
