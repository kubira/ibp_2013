package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cz.kubira.pomocnik.R;

public class DeleteDatabasePromptDialog extends Dialog implements View.OnClickListener {
	private Context context;

	public DeleteDatabasePromptDialog(Context context) {
		super(context);
		this.context = context;
		setContentView(R.layout.prompt_dialog);

		TextView tv = (TextView)findViewById(R.id.prompt_text);
		tv.setText("Opravdu chcete vymazat celou databázi výdajù?");

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

				ds.database.execSQL("DELETE FROM category;");
				ds.database.execSQL("DELETE FROM journey;");
				ds.database.execSQL("DELETE FROM record;");

				ds.close();

				Toast.makeText(context, "Database deleted.", Toast.LENGTH_LONG).show();
				dismiss();
				break;
			}
			case R.id.noButton: {
				dismiss();
				break;
			}
		}
	}
}