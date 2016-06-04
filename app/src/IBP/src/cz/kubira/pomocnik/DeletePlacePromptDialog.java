package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cz.kubira.pomocnik.around.PlaceMarker;
import cz.kubira.pomocnik.logger.DataSource;

public class DeletePlacePromptDialog extends Dialog implements View.OnClickListener {
	private PlaceInfoDialog piDialog;
	private Context context;

	public DeletePlacePromptDialog(Context context, PlaceInfoDialog piDialog) {
		super(context);
		this.context = context;
		this.piDialog = piDialog;
		setContentView(R.layout.prompt_dialog);

		setTitle("Smazání místa");

		TextView tv = (TextView)findViewById(R.id.prompt_text);
		tv.setText("Opravdu chcete smazat místo?");

		Button yesButton = (Button)findViewById(R.id.yesButton);
		yesButton.setOnClickListener(this);

		Button noButton = (Button)findViewById(R.id.noButton);
		noButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.yesButton: {
				DataSource ds = new DataSource(context);
				ds.open();
				Toast.makeText(context, "Mažu místo", Toast.LENGTH_SHORT).show();
				ds.deletePlace(piDialog.getPlace());
				((PlaceMarker)piDialog.getRow().getObject()).getMarker().remove();
				StartActivity.markers.remove((PlaceMarker)piDialog.getRow().getObject());

				Toast.makeText(context, "Místo smazáno", Toast.LENGTH_SHORT).show();

				ds.close();
				dismiss();
				piDialog.dismiss();
				((SavedPlaces)context).getTable().removeView(piDialog.getRow().getNextView());
				((SavedPlaces)context).getTable().removeView(piDialog.getRow());
				break;
			}
			case R.id.noButton: {
				dismiss();
				break;
			}
		}
	}
}