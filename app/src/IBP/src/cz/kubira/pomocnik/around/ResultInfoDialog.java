package cz.kubira.pomocnik.around;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.StaticMethods.Units;
import cz.kubira.pomocnik.logger.DataSource;

public class ResultInfoDialog extends Dialog implements View.OnClickListener {

	protected Result result;
	protected Context context;
	protected ObjectTableRow row;

	public ResultInfoDialog(Context context, View view) {
		super(context);
		row = ((ObjectTableRow)view);
		this.setResult((Result)row.getObject());
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.result_info);

		TextView name = (TextView)findViewById(R.id.name);
		TextView address = (TextView)findViewById(R.id.address);
		TextView distance = (TextView)findViewById(R.id.distance);

		name.setText(result.name);
		address.setText(result.address.replace(", ", "\n").replace(",", "\n"));
        distance.setText(StaticMethods.getNiceMeasure(result.distance, Units.METER));

        Button add = (Button)findViewById(R.id.add_marker);
        add.setOnClickListener(this);
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public ObjectTableRow getRow() {
		return row;
	}

	@Override
	public void onClick(View view) {
		DataSource ds = new DataSource(context);
		ds.open();
		Toast.makeText(context, "Pøidávám místo do DB a mapy", Toast.LENGTH_SHORT).show();
		Place place = ds.createPlace(result);
		Toast.makeText(context, "Místo pøidáno", Toast.LENGTH_SHORT).show();
		ds.close();

		new PlaceMarker(place);

		dismiss();
	}
}