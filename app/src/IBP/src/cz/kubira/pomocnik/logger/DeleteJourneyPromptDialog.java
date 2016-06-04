package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cz.kubira.pomocnik.R;

public class DeleteJourneyPromptDialog extends Dialog implements View.OnClickListener {
	private ViewJourneyDialog wjDialog;
	private Context context;

	public DeleteJourneyPromptDialog(Context context, ViewJourneyDialog wjDialog) {
		super(context);
		this.context = context;
		this.wjDialog = wjDialog;
		setContentView(R.layout.prompt_dialog);

		setTitle("Smaz�n� kategorie");

		TextView tv = (TextView)findViewById(R.id.prompt_text);
		tv.setText("Opravdu chcete smazat cestu a v�echny z�znamy s touto cestou?");

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
				Toast.makeText(context, "Ma�u cestu", Toast.LENGTH_SHORT).show();
				ds.deleteJourney(wjDialog.getJourney());
				Toast.makeText(context, "Cesta smaz�na", Toast.LENGTH_SHORT).show();

				Toast.makeText(context, "Ma�u z�znamy s cestou", Toast.LENGTH_SHORT).show();
				ds.database.delete("record", "id_journey = "+wjDialog.getJourney().getId(), null);
				Toast.makeText(context, "Z�znamy s cestou smaz�ny", Toast.LENGTH_SHORT).show();

				ds.close();
				dismiss();
				wjDialog.dismiss();
				((ViewJourneys)context).getTable().removeView(wjDialog.getRow().getNextView());
				((ViewJourneys)context).getTable().removeView(wjDialog.getRow());
				break;
			}
			case R.id.noButton: {
				dismiss();
				break;
			}
		}
	}
}