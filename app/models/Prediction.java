package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.auth.AuthScope;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import models.fetcher.FlightInfoFetcher;

import scala.util.Random;
import com.csvreader.CsvReader;

public class Prediction {
	protected static HashMap<Integer, Double> delayTime;
	protected static HashMap<Integer, Double> delayDate;
	protected static HashMap<Integer, Double> delayMonth;
	protected static HashMap<Integer, Double> delayWeek;
	
	public Prediction() throws IOException{
		CsvReader csv = null;
		if(delayTime == null){
			delayTime = new HashMap<Integer, Double>();
			csv = new CsvReader("res/prediction time.csv");
			while (csv.readRecord())
			{
				int time = (int) Math.round(Double.parseDouble(csv.get(0))*10);
				double probability = Double.parseDouble(csv.get(1));
				delayTime.put(time, probability);
			}
		}
		if(delayDate == null){
			delayDate = new HashMap<Integer, Double>();
			csv = new CsvReader("res/prediction date.csv");
			while (csv.readRecord())
			{
				int time = Integer.parseInt(csv.get(0));
				double probability = Double.parseDouble(csv.get(1));
				delayDate.put(time, probability);
			}
		}
		if(delayMonth == null){
			delayMonth = new HashMap<Integer, Double>();
			csv = new CsvReader("res/prediction month.csv");
			while (csv.readRecord())
			{
				int time = Integer.parseInt(csv.get(0));
				double probability = Double.parseDouble(csv.get(1));
				delayMonth.put(time, probability);
			}
		}
		if(delayWeek == null){
			delayWeek = new HashMap<Integer, Double>();
			csv = new CsvReader("res/prediction week.csv");
			while (csv.readRecord())
			{
				int time = Integer.parseInt(csv.get(0)) % 7;
				double probability = Double.parseDouble(csv.get(1));
				delayWeek.put(time, probability);
			}
		}
	}
	public static void main(String[] args) {
		try {
			Prediction p = new Prediction();
			//p.predict("", "", new Date(), "SFO", "SEA");
			String strDate="2013-11-02";
			Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(strDate);
			//System.out.println(date);
			//p.makePrediction2(date);
			p.predict("AA1637", strDate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void buildModel(){
		return;
	}
	public FlightQuality predict(String flightID,  String strDate){
		FlightQuality quality = FlightInfoFetcher.fetch(flightID,strDate);  
		if(quality == null){
			System.out.println("$$$$$$$$$$$$$$$$$4 "+flightID+" "+strDate);
			//quality = new FlightQuality(airline, flightNumber, d, departure, arrival);
		
		}
		
		//quality.fetchWeather();
		
		makePrediction2(quality);
		
		//quality.fetchRecommendations();
		return quality;
	}
	protected int makePrediction(Date d){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		double p = 0;
		p += delayMonth.get(calendar.get(Calendar.MONTH) + 1);
		p += delayWeek.get(calendar.get(Calendar.DAY_OF_WEEK) - 1 );
		p += delayDate.get(calendar.get(Calendar.DATE));
		p += delayTime.get(calendar.get(Calendar.HOUR_OF_DAY)*10 + (calendar.get(Calendar.MINUTE)/30) * 5 );
		return (int) (p/4);

	}
	protected void makePrediction2(FlightQuality fq){
		System.out.println(fq.toString());
		
		//HANASQL hana = new HANASQL(fq);
		String url = "http://54.235.127.76:8000/HANASample1/pp.xsjs?" +
				"dom=11&dow=5&q=4&uc=AA&o=SFO&dest=JFK&dep=1544";
		URIBuilder uri;
		HttpGet httpget = null;
		try {
			uri = new URIBuilder(url);
		    httpget = new HttpGet(uri.build());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String username = "System";
        String password = "Cmusv2012";
        String host = "54.235.127.76";
        httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, 8000), new UsernamePasswordCredentials(username, password));
        
		HttpResponse responseBody = null;
		try {
			responseBody = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream content = null;
		try {
			content = responseBody.getEntity().getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String response = getStringFromInputStream(content);
		System.out.println("responseBody " + response);
		fq.setDelay(0, 0);
		//fq.setDelay(Integer.parseInt(response)*15,11);
		return ;

	}
	private  String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
 
}
