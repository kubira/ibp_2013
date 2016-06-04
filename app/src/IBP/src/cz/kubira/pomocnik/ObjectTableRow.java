package cz.kubira.pomocnik;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;

public class ObjectTableRow extends TableRow {
	private Object object;
	private View nextView;
	
	public ObjectTableRow(Context context, Object object) {
		super(context);
		this.setObject(object);
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public View getNextView() {
		return nextView;
	}

	public void setNextView(View nextView) {
		this.nextView = nextView;
	}
}