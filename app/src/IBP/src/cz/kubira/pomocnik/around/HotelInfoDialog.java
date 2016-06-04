package cz.kubira.pomocnik.around;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cz.kubira.pomocnik.ObjectTableRow;
import cz.kubira.pomocnik.R;
import cz.kubira.pomocnik.StaticMethods;
import cz.kubira.pomocnik.StaticMethods.Units;
import cz.kubira.pomocnik.logger.DataSource;

public class HotelInfoDialog extends Dialog implements View.OnClickListener {

	protected Hotel hotel;
	protected Context context;
	protected ObjectTableRow row;

	public HotelInfoDialog(Context context, View view) {
		super(context);
		SpannableString s;
		row = ((ObjectTableRow)view);
		this.setHotel((Hotel)row.getObject());
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hotel_info);

		TextView name = (TextView)findViewById(R.id.name);
		TextView address = (TextView)findViewById(R.id.address);
		TextView phone = (TextView)findViewById(R.id.phone);
		TextView stars = (TextView)findViewById(R.id.stars);
		TextView distance = (TextView)findViewById(R.id.distance);
		TextView web = (TextView)findViewById(R.id.web);

		name.setText(hotel.name);
		address.setText(hotel.address.replace(", ", "\n").replace(",", "\n"));
		s = new SpannableString(hotel.phone);
		Linkify.addLinks(s, Linkify.PHONE_NUMBERS);
		phone.setText(s);
		phone.setMovementMethod(LinkMovementMethod.getInstance());

		stars.setText(ActivityHotels.stars[hotel.stars]);
		distance.setText(StaticMethods.getNiceMeasure(hotel.distanceMiles, Units.MILE));

		s = new SpannableString(hotel.url);
		Linkify.addLinks(s, Linkify.WEB_URLS);
		web.setText(s);
		web.setMovementMethod(LinkMovementMethod.getInstance());

		Button add = (Button)findViewById(R.id.add_marker);
        add.setOnClickListener(this);
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public ObjectTableRow getRow() {
		return row;
	}

	@Override
	public void onClick(View view) {
		DataSource ds = new DataSource(context);
		ds.open();
		Place place = ds.createPlace(hotel);
		ds.close();

		new PlaceMarker(place);

		dismiss();
	}
}