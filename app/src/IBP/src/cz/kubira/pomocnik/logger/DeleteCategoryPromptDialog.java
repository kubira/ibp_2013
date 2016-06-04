package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cz.kubira.pomocnik.R;

public class DeleteCategoryPromptDialog extends Dialog implements View.OnClickListener {
	private ViewCategoryDialog wcDialog;
	private Context context;

	public DeleteCategoryPromptDialog(Context context, ViewCategoryDialog wcDialog) {
		super(context);
		this.context = context;
		this.wcDialog = wcDialog;
		setContentView(R.layout.prompt_dialog);

		setTitle("Smazání kategorie");

		TextView tv = (TextView)findViewById(R.id.prompt_text);
		tv.setText("Opravdu chcete smazat kategorii a všechny záznamy s touto kategorií?");

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
				Toast.makeText(context, "Mažu kategorii", Toast.LENGTH_SHORT).show();
				ds.deleteCategory(wcDialog.getCategory());
				Toast.makeText(context, "Kategorie smazána", Toast.LENGTH_SHORT).show();

				Toast.makeText(context, "Mažu záznamy s kategorií", Toast.LENGTH_SHORT).show();
				ds.database.delete("record", "id_category = "+wcDialog.getCategory().getId(), null);
				Toast.makeText(context, "Záznamy s kategorií smazány", Toast.LENGTH_SHORT).show();

				ds.close();
				dismiss();
				wcDialog.dismiss();
				((ViewCategories)context).getTable().removeView(wcDialog.getRow().getNextView());
				((ViewCategories)context).getTable().removeView(wcDialog.getRow());
				break;
			}
			case R.id.noButton: {
				dismiss();
				break;
			}
		}
	}
}