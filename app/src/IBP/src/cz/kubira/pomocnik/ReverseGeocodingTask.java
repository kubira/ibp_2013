package cz.kubira.pomocnik;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

public class ReverseGeocodingTask extends AsyncTask<Location, Void, String> {
	private Context context;
	private AsyncTaskCompleteListener<String> callback;

    public ReverseGeocodingTask(Context context, AsyncTaskCompleteListener<String> cb) {
        super();
        this.context = context;
        this.callback = cb;
    }

    @Override
	protected void onPostExecute(String result) {
        callback.onTaskComplete(result);
    }

    @Override
    protected String doInBackground(Location... params) {
    	Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    	String addressText = "";
        Location loc = params[0];
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            addressText = String.format("%s, %s, %s",
            		address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getMaxAddressLineIndex() > 1 ? address.getAddressLine(1) : "",
                    address.getLocality(),
                    address.getCountryName());
        }

        if (addressText.trim().equals("")) {
        	addressText = "Sluûba zjiöùov·nÌ adresy nenÌ moment·lnÏ k dispozici.";
        }

        return addressText;
    }
}