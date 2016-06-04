package cz.kubira.pomocnik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import cz.kubira.pomocnik.logger.DataSource;



public class NetworkTask extends AsyncTask<String, Void, String> {
    private AsyncTaskCompleteListener<String> callback;
	private Context context;
    @SuppressWarnings("unused")
	private String finalResult;

    public NetworkTask(Context context, AsyncTaskCompleteListener<String> cb) {
        this.context = context;
        this.callback = cb;

        System.out.println(StartActivity.bytes+":"+(new DataSource(context)).getBytes());
    }

    @Override
	protected void onPostExecute(String result) {
       finalResult = result;
       callback.onTaskComplete(result);
   }

	@Override
	protected String doInBackground(String... arg0) {
		HttpClient client = new DefaultHttpClient();
    	HttpGet request = new HttpGet(arg0[0]);
    	HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	InputStream in = null;
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    	StringBuilder str = new StringBuilder();
    	String line = null;
    	try {
			while((line = reader.readLine()) != null)
			{
			    str.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	if (!StartActivity.settings.get("limit").equals("no_check")) {
			DataSource ds = new DataSource(context);
			ds.addBytes(str.toString().length());
		}

    	return str.toString();
	}
}