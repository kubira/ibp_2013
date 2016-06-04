package cz.kubira.pomocnik.logger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;

public class ViewCategoryDialog extends Dialog implements View.OnClickListener {

	protected Category category;
	protected Context context;
	protected ObjectTableRow row;

	public ViewCategoryDialog(Context context, View view) {
		super(context);
		row = ((ObjectTableRow)view);
		this.setCategory((Category)row.getObject());
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.category_action);

		TextView infoName = (TextView)findViewById(R.id.info_name);

		Button editButton = (Button)findViewById(R.id.edit_button);
		editButton.setOnClickListener(this);
		Button deleteButton = (Button)findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(this);

		infoName.setText(category.getName());
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.edit_button: {
				(new ShowCategoryDialog(context, category)).show();
				this.dismiss();
				break;
			}

			case R.id.delete_button: {
				(new DeleteCategoryPromptDialog(context, this)).show();
				break;
			}
		}
	}

	public ObjectTableRow getRow() {
		return row;
	}

}