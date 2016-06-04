package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cz.kubira.pomocnik.R;

public class NetworkDialog extends Dialog {

	protected int title;
	protected int text;

	public NetworkDialog(Context context, int title, int text) {
		super(context);
		this.title = title;
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_dialog);

		this.setTitle(this.title);

		final TextView errText = (TextView)this.findViewById(R.id.network_dialog_text);
		errText.setText(this.text);

		final Button cancelButton = (Button)this.findViewById(R.id.network_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		final Button wifiButton = (Button)this.findViewById(R.id.wifi_settings_button);
		wifiButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				dismiss();
			}
		});

		final Button networkButton = (Button)this.findViewById(R.id.network_settings_button);
		networkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				dismiss();
			}
		});
	}
}