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

public class ShowCategoryDialog extends Dialog {

	public ShowCategoryDialog(final Context context, final Category category) {
		super(context);

		setContentView(R.layout.new_category);

		((EditText)findViewById(R.id.new_category_name_field)).setText(category.getName());

		setTitle(R.string.new_category_dialog_title);

		final Button cancelButton = (Button)findViewById(R.id.new_category_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});

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
				} else if (dataSource.isInTable("category", "name_category", name, category.getId())) {
					(new ErrorDialog(context, R.string.input_error,
							R.string.category_name_exists)).show();
					dataSource.close();
					return;
				}

				Toast.makeText(context, R.string.saving_category, Toast.LENGTH_SHORT).show();
				dataSource.updateCategory(category.getId(), name);
				Toast.makeText(context, R.string.category_saved, Toast.LENGTH_SHORT).show();

				dataSource.close();

				dismiss();
				((ViewCategories)context).finish();
				Intent i = new Intent(getContext(), ViewCategories.class);
				getContext().startActivity(i);
			}
		});
	}
}