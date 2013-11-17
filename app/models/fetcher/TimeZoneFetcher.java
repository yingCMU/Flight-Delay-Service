package models.fetcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jettison.json.JSONObject;

import models.GeoLocation;
import models.TimeZone;
import models.Weather;

public class TimeZoneFetcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static TimeZone fetch(GeoLocation airport){
		HttpClient httpclient = new DefaultHttpClient();
		try {
			System.out.println("QUERY TimeZone:" + airport.getCity());
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("q", airport.getLatitude()+","+airport.getLongitude()));
			URIBuilder uri = new URIBuilder("http://api.worldweatheronline.com/free/v1/tz.ashx");
			uri.addParameter("q", airport.getLatitude()+","+airport.getLongitude());
			uri.addParameter("format", "json");
			
			uri.addParameter("key", "xekxkssj32w832j6bkkxets7");
		
//http://api.worldweatheronline.com/free/v1/weather.ashx?q=sunnyvale&format=json&num_of_days=5&key=xekxkssj32w832j6bkkxets7
			HttpGet httpget = new HttpGet(uri.build());

			System.out.println("executing request " + httpget.getURI());

			// Create a response handler
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpget, responseHandler);
			responseBody = httpclient.execute(httpget, responseHandler);
			JSONObject myjson = new JSONObject(responseBody)
					.getJSONObject("data");
			JSONObject current = myjson.getJSONArray("time_zone").getJSONObject(0);
			System.out.println("***********"+current.getString("localtime"));
			TimeZone tz = new TimeZone(current.getString("utcOffset"),	current.getString("localtime"));
			return tz;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
