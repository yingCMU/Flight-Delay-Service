package models.fetcher;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import models.Airport;
import models.FlightQuality;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FlightInfoFetcher {
	public static void main(String [ ] args) throws IOException, ParseException{
		 FlightInfoFetcher.fetch("UA502","2013-11-8");
	}
	public FlightInfoFetcher() throws IOException{
		
	}
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
		

	}
}
