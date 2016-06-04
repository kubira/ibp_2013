package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorDialog extends Dialog {

	protected int title;
	protected String text;

	public ErrorDialog(Context context, int title, int text) {
		super(context);
		this.title = title;
		this.text = context.getString(text);
	}

	public ErrorDialog(Context context, int title, String text) {
		super(context);
		this.title = title;
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error_dialog);

		this.setTitle(this.title);

		final TextView errText = (TextView)this.findViewById(R.id.error_dialog_text);
		errText.setText(this.text);

		final Button errButton = (Button)this.findViewById(R.id.error_dialog_button);
		errButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}