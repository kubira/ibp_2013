package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GPSDialog extends Dialog {

	protected int title;
	protected int text;

	public GPSDialog(Context context, int title, int text) {
		super(context);
		this.title = title;
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_dialog);

		this.setTitle(this.title);

		final TextView errText = (TextView)this.findViewById(R.id.gps_dialog_text);
		errText.setText(this.text);

		final Button cancelButton = (Button)this.findViewById(R.id.gps_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		final Button wifiButton = (Button)this.findViewById(R.id.gps_settings_button);
		wifiButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				dismiss();
			}
		});
	}
}