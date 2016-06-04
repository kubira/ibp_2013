package cz.kubira.pomocnik;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class StaticMethods {

	public static enum Units {
		MILE, FOOT, KILOMETER, METER, METER_ABOVE_SEA_LEVEL, METER_PER_SECOND, DISTANCE_KILOMETER, DISTANCE_METER;
	}

	public static double MILE_TO_M = 1609.344;
	public static double FOOT_TO_M = 0.3048;
	public static double KILOMETER_TO_M = 1000;
	public static double MILE_TO_FOOT = 5280;

	public static boolean isConnectivity(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

	    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
	    	return true;
	    } else {
	    	return false;
	    }
	}

	public static String getNiceMeasure(String value, Units unitFrom) {
		return getNiceMeasure(Double.valueOf(value), unitFrom);
	}

	public static String getNiceMeasure(double value, Units unitFrom) {
		double meters = 0;

		switch (unitFrom) {
			case MILE : {
				meters = value * MILE_TO_M;
				break;
			}
			case FOOT : {
				meters = value * FOOT_TO_M;
				break;
			}
			case DISTANCE_KILOMETER :
			case KILOMETER : {
				meters = value * KILOMETER_TO_M;
				break;
			}
			case DISTANCE_METER :
			case METER_ABOVE_SEA_LEVEL :
			case METER_PER_SECOND :
			case METER : {
				meters = value;
				break;
			}
		}

		if (StartActivity.settings.get("measure").startsWith("m")) {
			meters /= FOOT_TO_M;  // stopy

			if (unitFrom == Units.METER_ABOVE_SEA_LEVEL) {
				return String.format("%,.0f ft", meters);
			}

			if (unitFrom == Units.METER_PER_SECOND) {
				return String.format("%,.1f ft/s", meters);
			}

			if (meters > MILE_TO_FOOT) {
				meters /= MILE_TO_FOOT;
				return String.format("%,.3f mi", meters);
			} else {
				return String.format("%,.0f ft", meters);
			}
		} else {
			if (unitFrom == Units.METER_ABOVE_SEA_LEVEL) {
				return String.format("%,.0f m", meters);
			}

			if (unitFrom == Units.METER_PER_SECOND) {
				return String.format("%,.1f m/s", meters);
			}

			// meter or kilometer
			if (meters > KILOMETER_TO_M) {
				meters /= KILOMETER_TO_M;
				return String.format("%,.3f km", meters);
			} else {
				return String.format("%,.0f m", meters);
			}
		}
	}
}