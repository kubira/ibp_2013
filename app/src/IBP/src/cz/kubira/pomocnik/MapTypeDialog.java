package cz.kubira.pomocnik;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;

public class MapTypeDialog extends Dialog {

	public MapTypeDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_type_dialog);

		this.setTitle("Výbìr typu mapy");

		final Button normal = (Button)this.findViewById(R.id.normal);
		normal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartActivity.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				dismiss();
			}
		});

		final Button satellite = (Button)this.findViewById(R.id.satellite);
		satellite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartActivity.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				dismiss();
			}
		});

		final Button hybrid = (Button)this.findViewById(R.id.hybrid);
		hybrid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartActivity.map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				dismiss();
			}
		});

		final Button terrain = (Button)this.findViewById(R.id.terrain);
		terrain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartActivity.map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				dismiss();
			}
		});
	}
}