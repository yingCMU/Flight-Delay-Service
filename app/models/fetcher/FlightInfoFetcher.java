package models.fetcher;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Airport;
import models.FlightQuality;
import models.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;


public class FlightInfoFetcher {
	public static void main(String [ ] args) throws IOException, ParseException{
		 FlightInfoFetcher.fetch("UA502","2013-12-01","SFO");
	}
	public FlightInfoFetcher() throws IOException{
		
	}
	public static FlightQuality fetch(String flightID,String date, String dep_para){
		//AA/1673/departing/2013/11/17?=&appKey=&extendedOptions=useInlinedReferences
		HttpClient httpclient = new DefaultHttpClient();
		try {
			String uc = flightID.substring(0, 2);
			String num = flightID.substring(2);
			String res = uc+"/"+num;
			 String newdate = date.replace('-', '/');
			//List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			//qparams.add(new BasicNameValuePair("q", airport.getLatitude()+","+airport.getLongitude()));
			URIBuilder uri = new URIBuilder("https://api.flightstats.com/flex/schedules/rest/v1/json/flight/"+res+"/departing/"+newdate);
			uri.addParameter("extendedOptions", "useInlinedReferences");
			uri.addParameter("appId", "a2d81565");
			
			uri.addParameter("appKey", "90c0ac5457d9ac6d80ddb19545b3ca29");
		
//http://api.worldweatheronline.com/free/v1/weather.ashx?q=sunnyvale&format=json&num_of_days=5&key=xekxkssj32w832j6bkkxets7
			HttpGet httpget = new HttpGet(uri.build());

			System.out.println("executing request " + httpget.getURI());

			// Create a response handler
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpget, responseHandler);
			//System.out.println(responseBody);
			JSONArray schedules = new JSONObject(responseBody).getJSONArray("scheduledFlights");
			for(int i=0;i<schedules.length();i++){
				JSONObject schedule=  (JSONObject) schedules.get(0);
				JSONObject departureAirport =(JSONObject) schedule.getJSONObject("departureAirport");
				if(departureAirport.getString("iata").equals(dep_para)){
					JSONObject carrie = (JSONObject) schedule.getJSONObject("carrier");
					
					JSONObject arrivalAirport = (JSONObject) schedule.getJSONObject("arrivalAirport");
					System.out.println("acrrie:  -"+carrie.toString());
					System.out.println("departureAirport:  -"+departureAirport.toString());
					System.out.println("arrivalAirport:  -"+arrivalAirport.toString());
					/*
					 * departureTerminal: "3",
		arrivalTerminal: "7",
		departureTime: "2013-11-18T16:35:00.000",
		arrivalTime: "2013-11-19T00:54:00.000",
					 */
					String departureTime = schedule.getString("departureTime");
					String arrivalTime = schedule.getString("arrivalTime");
					System.out.println(departureTime+" ;"+arrivalTime);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss.SSS");
					/*JSONObject current = myjson.getJSONArray("time_zone").getJSONObject(0);
					System.out.println("***********"+current.getString("localtime"));
					*/
					Date depDate = format.parse(departureTime);
					Date arrivalDate = format.parse(arrivalTime);
					//int depTZ = departureAirport.getInt("utcOffsetHours");
					//int arrTZ = arrivalAirport.getInt("utcOffsetHours");
					
					String dep = depDate.getHours()+""+depDate.getMinutes();
					String arr = arrivalDate.getHours()+""+arrivalDate.getMinutes();
					String departCity=departureAirport.getString("fs");
					String arrivalCity= arrivalAirport.getString("fs");
					String carrier_name = carrie.getString("name");
					System.out.println(flightID+" "+departCity+" "+" "+arrivalCity+ 
					" carrier_name "+carrier_name+" ;  time:"+dep+" "+arr);
					
					//System.out.println("aircraft");
					FlightQuality fl=new FlightQuality(flightID, dep, arr);
					fl.setDepartAirport(Airport.findAirport(departCity)) ;
					//System.out.println("!!!!"+fl.getDepartAirport().getGeoLocation().getLatitude());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(depDate);
					
					fl.setDAY_OF_MONTH(calendar.get(Calendar.MONTH)+1);
					fl.setDAY_OF_WEEK((calendar.get(Calendar.DAY_OF_WEEK)-1));
					fl.setDepDate(date);
					fl.setAirlineName(carrier_name);
					fl.setArrivalDate(arrivalTime.substring(0,10));
					System.out.println("@@@@@@@@@@@@@@@@@@ setdepdata "+fl.getDepDate());
					fl.setArrivalAirport(arrivalCity);
					fl.setDepartureAirport(departCity);
					System.out.println("end of flgitinfofetcher : "+fl.toString());
					return fl;
				}
				
			}
			return null;
			
			
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}
	/*
	public static FlightQuality fetch(String flightID,String date){
		Document doc;
		try {
			doc = Jsoup.connect("http://travel.flightexplorer.com/FlightTracker/" + flightID).get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		Elements table = doc.select("#FastTrack1_resultGrid tr");
		
		int i=0;
		for (Element tr : table) {
			//skip first row
			if (i==0) {
				i+= 1;
				continue;
			}
			if(tr.children().size() < 6){
				break;
			}
			
			String departCity = tr.child(1).child(0).text();
			String arrivalCity = tr.child(2).child(0).text();
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mma");
			Date departureTime = null, arrivalTime = null;
			try {
				departureTime = format.parse(tr.child(3).text());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				departureTime = new Date();
				
			}
			try {
				arrivalTime = format.parse(tr.child(4).text());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				arrivalTime = new Date();
				e.printStackTrace();
			}
			String dep = departureTime.getHours()+""+departureTime.getMinutes();
			String arr = arrivalTime.getHours()+""+arrivalTime.getMinutes();
			String aircraft = tr.child(5).text();
			String status = tr.child(6).text();
			System.out.println(flightID+" "+departCity+" "+" "+arrivalCity+ 
				"\naircraft "+aircraft+" status "+status+" ; deptime "+departureTime.getDate()+" time:"+dep+" "+arr);
			
			//System.out.println("aircraft");
			FlightQuality fl=new FlightQuality(flightID, dep, arr);
			fl.setDepartAirport(Airport.findAirport(departCity)) ;
			//System.out.println("!!!!"+fl.getDepartAirport().getGeoLocation().getLatitude());
			Calendar calendar = Calendar.getInstance();
			try {
				Date dateD = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
				calendar.setTime(dateD);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			fl.setDAY_OF_MONTH(calendar.get(Calendar.MONTH)+1);
			fl.setDAY_OF_WEEK((calendar.get(Calendar.DAY_OF_WEEK)-1));
			fl.setDepDate(date);
			fl.setArrivalDate(date);
			System.out.println("@@@@@@@@@@@@@@@@@@ setdepdata "+fl.getDepDate());
			fl.setArrivalAirport(arrivalCity);
			fl.setDepartureAirport(departCity);
			try {
				Date dateD = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(fl.toString());
			return fl;
		}
		return null;
		

	}*/
}
