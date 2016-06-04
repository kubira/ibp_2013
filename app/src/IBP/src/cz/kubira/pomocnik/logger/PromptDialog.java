package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import cz.kubira.pomocnik.R;

public class PromptDialog extends Dialog implements View.OnClickListener {
	private RecordActionDialog raDialog;
	private Context context;

	public PromptDialog(Context context, RecordActionDialog raDialog) {
		super(context);
		this.context = context;
		this.raDialog = raDialog;
		setContentView(R.layout.prompt_dialog);

		setTitle("Smazání výdaje");

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
				Toast.makeText(context, R.string.deleting_record, Toast.LENGTH_SHORT).show();
				ds.deleteRecord(raDialog.getRecord().getRecord());
				Toast.makeText(context, R.string.record_deleted, Toast.LENGTH_SHORT).show();
				ds.close();
				dismiss();
				raDialog.dismiss();
				((ViewRecords)context).getTable().removeView(raDialog.getRow().getNextView());
				((ViewRecords)context).getTable().removeView(raDialog.getRow());
				break;
			}
			case R.id.noButton: {
				dismiss();
				break;
			}
		}
	}
}