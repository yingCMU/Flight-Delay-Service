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
import java.util.HashSet;
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
			String strDate="2013-12-02";
			Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(strDate);
			//System.out.println(date);
			//p.makePrediction2(date);
			p.predict("UA502", strDate,"SFO");
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
	public boolean supportAirport(String query){
		HashSet<String> support = new HashSet<String>();
		String a = "SFO,JFK,ORD,PIT,SJC,LAX";
		for(String port :a.split(",")){
			support.add(port);
			//System.out.println(a);
		}
		
		return support.contains(query);
		
	}
	public FlightQuality predict(String flightID,  String strDate,String dep){
		if(!supportAirport(dep)){
			FlightQuality quality = new FlightQuality(flightID.substring(0,2), flightID.substring(2), strDate, dep, "unknown arrival");
			quality.setError(new Error(1,"no support for airport --"+dep));
			return quality;
		}
			
		FlightQuality quality = FlightInfoFetcher.fetch(flightID,strDate,dep);  
		if(quality == null){
			System.out.println("$$$$$$$$$$$$$$$$$4 "+flightID+" "+strDate);
			quality = new FlightQuality(flightID.substring(0,2), flightID.substring(2), strDate, dep, "unknown arrival");
			quality.setError(new Error(1,"can not find flight  for "+flightID+" from "+dep+" on "+strDate));
			return quality;
			
		}
		
		makePrediction2(quality);
		
		//quality.fetchTimeZone();
		
		//quality.fetchRecommendations();
		return quality;
	}
	
	protected void makePrediction2(FlightQuality fq){
		//HANASQL hana = new HANASQL(fq);
		/*
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
		
		//fq.setDelay(Integer.parseInt(response)*15,11);*/
		
		
		//fq.setDelay(1*15, 0);
		int rd =  (int) ((int)5*Math.random());
		fq.setDelay(rd*15, 0);
		fq.setError(new Error(0,"everything good"));
		fq.fetchWeather();
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
