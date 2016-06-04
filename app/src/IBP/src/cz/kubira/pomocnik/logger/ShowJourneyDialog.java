package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.R;

public class ShowJourneyDialog extends Dialog {

	public ShowJourneyDialog(final Context context, final Journey journey) {
		super(context);

		setContentView(R.layout.new_journey);

		((EditText)findViewById(R.id.new_journey_name_field)).setText(journey.getName());

		setTitle(R.string.new_journey_dialog_title);

		final Button cancelButton = (Button)findViewById(R.id.new_journey_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});

		final Button okButton = (Button)findViewById(R.id.new_journey_ok_button);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				DataSource dataSource = new DataSource(context);
				dataSource.open();

				String name = ((EditText)findViewById(R.id.new_journey_name_field)).getText().toString();
				if (name.trim().equals("")) {
					(new ErrorDialog(context, R.string.input_error,
							R.string.empty_journey_name)).show();
					dataSource.close();
					return;
				} else if (dataSource.isInTable("journey", "name_journey", name, journey.getId())) {
					(new ErrorDialog(context, R.string.input_error,
							R.string.journey_name_exists)).show();
					dataSource.close();
					return;
				}

				Toast.makeText(context, R.string.saving_journey, Toast.LENGTH_SHORT).show();
				dataSource.updateJourney(journey.getId(), name);
				Toast.makeText(context, R.string.journey_saved, Toast.LENGTH_SHORT).show();

				dataSource.close();

				dismiss();
				((ViewJourneys)context).finish();
				Intent i = new Intent(getContext(), ViewJourneys.class);
				getContext().startActivity(i);
			}
		});
	}
}