package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;

public class ViewJourneyDialog extends Dialog implements View.OnClickListener {

	protected Journey journey;
	protected Context context;
	protected ObjectTableRow row;

	public ViewJourneyDialog(Context context, View view) {
		super(context);
		row = ((ObjectTableRow)view);
		this.setJourney((Journey)row.getObject());
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.journey_action);

		TextView infoName = (TextView)findViewById(R.id.info_name);

		Button editButton = (Button)findViewById(R.id.edit_button);
		editButton.setOnClickListener(this);
		Button deleteButton = (Button)findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(this);

		infoName.setText(journey.getName());
	}

	public Journey getJourney() {
		return journey;
	}

	public void setJourney(Journey journey) {
		this.journey = journey;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.edit_button: {
				(new ShowJourneyDialog(context, journey)).show();
				this.dismiss();
				break;
			}

			case R.id.delete_button: {
				(new DeleteJourneyPromptDialog(context, this)).show();
				break;
			}
		}
	}

	public ObjectTableRow getRow() {
		return row;
	}

}