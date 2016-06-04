package cz.kubira.pomocnik.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import cz.kubira.pomocnik.MyRunnable;

public class TrackService extends Service implements LocationListener {

	public static String name;
	public static String description;
	private String filename = null;
	private static Handler handler = new Handler();
	private static PrintWriter pw = null;
	private static int interval = 0;
	private LocationManager lm = null;
	private static Location location = null;
	private Criteria criteria = null;
	private BroadcastReceiver receiver;
	private String provider = null;

	private static SimpleDateFormat POINT_DATE_FORMATTER;

    protected static MyRunnable runnable = new MyRunnable() {
    	private boolean stop = true;

        @Override
		public void run() {
            if (!stop && location != null) {
            	System.out.println(location.getLatitude()+":"+location.getLongitude());
            	if (location.getAltitude() == 0.0 && location.getTime() == 0) {
            		pw.println("\t\t\t<trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\" />");
            	} else if (location.getAltitude() != 0.0 && location.getTime() == 0) {
            		pw.println("\t\t\t<trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\">");
            		pw.println("\t\t\t\t<ele>"+location.getAltitude()+"</ele>");
            		pw.println("\t\t\t</trkpt>");
            	} else if (location.getAltitude() == 0.0 && location.getTime() != 0) {
            		pw.println("\t\t\t<trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\">");
            		pw.println("\t\t\t\t<time>"+POINT_DATE_FORMATTER.format(location.getTime())+"</time>");
            		pw.println("\t\t\t</trkpt>");
            	} else	if (location.getAltitude() != 0.0 && location.getTime() != 0) {
            		pw.println("\t\t\t<trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\">");
            		pw.println("\t\t\t\t<ele>"+location.getAltitude()+"</ele>");
            		pw.println("\t\t\t\t<time>"+POINT_DATE_FORMATTER.format(location.getTime())+"</time>");
            		pw.println("\t\t\t</trkpt>");
            	}
            } else if (location == null) {
        		System.out.println("NULL");
        	}
            handler.postDelayed(runnable, interval);
        }

		@Override
		public void kill() {
			stop = true;
			pw.println("\t\t</trkseg>");
		    pw.println("\t</trk>");
			pw.println("</gpx>");
			pw.close();
		}

		@Override
		public void alive() {
			stop = false;
		}
    };

	@Override
	public void onCreate() {
		super.onCreate();

		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		POINT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		POINT_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("tady :)");
				setProvider();
			}
		};

		registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));

		setProvider();

		System.out.println("Service starting...");
	}

	public void setProvider() {
		provider = lm.getBestProvider(criteria, true);
		location = lm.getLastKnownLocation(provider);

		lm.requestLocationUpdates(provider, 500, 1, this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Bundle b = intent.getExtras();

		name = b.getString("name");
		description = b.getString("description");
		interval = b.getInt("interval");

		System.out.println("File start: "+name);

		filename = "/track_"+name+"_"+Calendar.getInstance().getTimeInMillis()+".gpx";
		filename = filename.replace(" ", "_");

		System.out.println("Filename start: "+filename);

		File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+filename);

		try {
			pw = new PrintWriter(f);

			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<gpx version=\"1.0\">");
			pw.println("\t<trk>");
			pw.println("\t\t<name>"+name+"</name>");
			if (description != null) {
				pw.println("\t\t<desc>"+description+"</desc>");
			}
			pw.println("\t\t<trkseg>");

			runnable.alive();
			runnable.run();

			System.out.println("Service started.");

			return 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return 1;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		runnable.kill();
		lm.removeUpdates(this);
		System.out.println("Service stopped.");
	}

	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public TrackService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackService.this;
        }
    }

    public void writeNow(String name, String description) {
    	if (location != null) {
    		System.out.println("Writed now :)");
    		if (name == null && description == null && location.getAltitude() == 0.0 && location.getTime() == 0) {
    			pw.println("\t\t\t<trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\" />");
    		} else {
    			pw.println("\t\t\t<trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\">");
    			if (name != null) {
	    			pw.println("\t\t\t\t<name>"+name+"</name>");
	    		}
    			if (description != null) {
	    			pw.println("\t\t\t\t<desc>"+description+"</desc>");
	    		}

    			if (location.getAltitude() != 0.0) {
            		pw.println("\t\t\t\t<ele>"+location.getAltitude()+"</ele>");
            	}
    			if (location.getTime() != 0) {
            		pw.println("\t\t\t\t<time>"+POINT_DATE_FORMATTER.format(location.getTime())+"</time>");
            	}
    			pw.println("\t\t\t</trkpt>");
    		}
    	} else {
    		System.out.println("NULL");
    	}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		location = arg0;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		System.out.println("dis: "+provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		System.out.println("ena: "+provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}