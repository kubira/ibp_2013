package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cz.kubira.pomocnik.ErrorDialog;
import cz.kubira.pomocnik.R;

public class NewCategoryDialog extends Dialog {

	public NewCategoryDialog(final Context context, final Object object) {
		super(context);
		setContentView(R.layout.new_category);

		setTitle(R.string.new_category_dialog_title);

		final Button okButton = (Button)findViewById(R.id.new_category_ok_button);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DataSource dataSource = new DataSource(context);
				dataSource.open();

				String name = ((EditText)findViewById(R.id.new_category_name_field)).getText().toString();
				if (name.trim().equals("")) {
					(new ErrorDialog(context, R.string.input_error,
							R.string.empty_category_name)).show();
					dataSource.close();
					return;
				} else if (dataSource.isInTable("category", "name_category", name)) {
					(new ErrorDialog(context, R.string.input_error,
							R.string.category_name_exists)).show();
					dataSource.close();
					return;
				}

				Toast.makeText(context, R.string.saving_category, Toast.LENGTH_SHORT).show();
				Category c = dataSource.createCategory(name);
				Toast.makeText(context, R.string.category_saved, Toast.LENGTH_SHORT).show();

				if (object instanceof Spinner) {
					Spinner spinner = (Spinner)object;
					@SuppressWarnings("unchecked")
					ArrayAdapter<Category> catAdapter = (ArrayAdapter<Category>)spinner.getAdapter();
					catAdapter.add(c);
					catAdapter.notifyDataSetChanged();
					spinner.setSelection(spinner.getCount()-1);
				} else {
					((ViewCategories)object).finish();
					Intent i = new Intent(context, ViewCategories.class);
					context.startActivity(i);
				}

				dataSource.close();

				dismiss();
			}
		});

		final Button cancelButton = (Button)findViewById(R.id.new_category_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}
}