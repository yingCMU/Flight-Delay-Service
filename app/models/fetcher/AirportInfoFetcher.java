package models.fetcher;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import models.Airline;
import models.Airport;
import models.FlightQuality;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class AirportInfoFetcher {
	public static void main(String [ ] args) throws IOException, ParseException{
		//System.out.println(new AirportInfoFetcher().fetch());
		//new AirportInfoFetcher().fetch(Airport.findAirport("SFO"), Airport.findAirport("SEA"));
	}
	public AirportInfoFetcher() throws IOException{
		
	}
	/*
	public ArrayList<FlightQuality> fetch(Airport airport) throws IOException, ParseException{
		if(airport.getName().equals("PHL")) 
			return this.fetchPHL();
		else
			return this.fetchGeneral(airport);
		
	}*/
	/*
	public ArrayList<FlightQuality> fetch(Airport depart, Airport arrival) throws IOException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		Date queryDate = new Date();
		String strDate = sdf.format(queryDate);
		String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
		Document doc = Jsoup.connect("http://www.flightstats.com/go/FlightStatus/flightStatusByRoute.do?departure=" + depart.getName() + "&arrival="+arrival.getName()+"&departureDate="+ strDate).userAgent(ua).get();
		Elements table = doc.select("table.tableListingTable tr");
		ArrayList<FlightQuality> fs = new ArrayList<FlightQuality>();
		for (Element tr : table) {
			if(tr.hasClass("tableHeader")) continue;
			String flightID = tr.child(0).child(0).text();
			String airline = tr.child(2).text();
			Date scheduledDepartureDate = this.mergeDate(queryDate, tr.child(3).text());
			Date actualDeparture = this.mergeDate(queryDate, tr.child(4).text());
			String departGate = tr.child(5).text();
			Date scheduledArrival = this.mergeDate(queryDate, tr.child(6).text());
			Date actualArrival = this.mergeDate(queryDate, tr.child(7).text());
			String arrivalGate = tr.child(8).text();
			
			int depatureDelay = (int) ((actualDeparture.getTime() - scheduledDepartureDate.getTime())/60000);
			int arrivalDelay = (int) ((actualArrival.getTime() - scheduledArrival.getTime())/60000);
			
			String status = tr.child(9).childNode(4).toString(); 
		
			//Rating	Airline	Sched	Actual	Gate	Sched	Actual	Gate	Status	Equip.	Track
			FlightQuality f = new FlightQuality(airline, flightID, scheduledDepartureDate, scheduledArrival);
			f.setArrivalAirport(arrival.getName());
			f.setDepartureAirport(depart.getName());
			f.setDelay(depatureDelay, arrivalDelay);
			fs.add(f);
			
		}
		return fs;
	}*/
	protected Date mergeDate(Date d, String t){
		SimpleDateFormat sdf = new SimpleDateFormat("K:mm a");
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.setTime(d);
		try {
			cal2.setTime(sdf.parse(t));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return cal.getTime();
		}
		cal.set(Calendar.HOUR, cal2.get(Calendar.HOUR));
		cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
		return cal.getTime();
	}
	public ArrayList<FlightQuality> fetchGeneral(Airport airport) throws IOException, ParseException{
		Document doc = Jsoup.connect("http://travel.flightexplorer.com/airportFlights.aspx?aid="+ airport.getName() +"&status=2").get();
		Elements table = doc.select("table.txt11[rules] tr");
		ArrayList<FlightQuality> fs = new ArrayList<FlightQuality>();
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy K:mma z");
		for (Element tr : table) {
			if(tr.hasClass("tbHeader") || tr.children().size()==1) continue;
			String flightID = tr.child(0).text();
			String departAirport = tr.child(1).text();
			String arrivalAirport = tr.child(2).text();
			Date departTime = null;
			try{
				 departTime = sdf.parse(tr.child(3).text());  //1/3/2013 5:00PM MST
			}
			catch(ParseException e){
				SimpleDateFormat sf = new SimpleDateFormat("M/d/yyyyK:mma");
				String d = tr.child(3).text();
				ArrayList<String> t = new ArrayList<String>(Arrays.asList(d.split(" ")));
				t.remove(t.size()-1);
				departTime = sf.parse( StringUtils.join(t.toArray()));
			}
			Date arrivalTime = null;
			try{
				 arrivalTime = sdf.parse(tr.child(4).text());
			}
			catch(ParseException e){
				SimpleDateFormat sf = new SimpleDateFormat("M/d/yyyyK:mma");
				String d = tr.child(4).text();
				ArrayList<String> t = new ArrayList<String>(Arrays.asList(d.split(" ")));
				t.remove(t.size()-1);
				arrivalTime = sf.parse( StringUtils.join(t.toArray()));
			}
			String airline = Airline.getAirlineName(flightID.substring(0, 3));
			/*FlightQuality f = new FlightQuality(airline, flightID, departTime, arrivalTime);
			f.setArrivalAirport(arrivalAirport);
			f.setDepartureAirport(departAirport);
			f.setDelay(0,0);
			fs.add(f);*/
		}
		
		return fs;
	}
	/*
	public ArrayList<FlightQuality> fetchPHL() throws IOException{
		Document doc = Jsoup.connect("http://www.phl.org/passengerinfo/Pages/FlightInformation.aspx").get();
		Elements table = doc.select("#ctl00_m_g_c8b2de17_9e20_49ea_b527_51ac8eed7317_ctl00_flightGrid_ctl00 tbody tr");
		ArrayList<FlightQuality> fs = new ArrayList<FlightQuality>();
		for (Element tr : table) {
			
			if(tr.hasClass("rgRow") || tr.hasClass("rgAltRow")){
				String airline = tr.child(0).child(0).attr("title");
				String flightNumber = tr.child(1).text();
				String City = tr.child(2).text();
				String time = tr.child(3).text();
				String gate = tr.child(4).text();
				String Direction = tr.child(5).text();
				String Status = tr.child(6).text();
				String arrival = "", departure = "";
				int delayArrival=0, delayDeparture=0;
				int diff = 0;
				SimpleDateFormat format = new SimpleDateFormat("HH:mm a");
				Date schedule = new Date(), actual;
				try {
					schedule = format.parse(time);
				
					if(!Status.equals("Arrived") && !Status.equals("On Time") && !Status.equals("At Gate") && !Status.equals("Customs") && !Status.equals("Departed") && !Status.equals("Closed")){
						actual = format.parse(Status);
						diff = (int) ((actual.getTime() - schedule.getTime())/(60*1000));
						System.out.println("'" + Status+ "'/" + "'" + time + "': " + String.valueOf(diff));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					diff = 0;
					e.printStackTrace();
				}
				if(Direction == "Departure"){
					departure = "PHL";
					arrival = City;
					delayDeparture = diff;
				}
				else{
					arrival = "PHL";
					departure = City;
					delayArrival = diff;
				}	
				Date date = new Date();
				date.setHours(schedule.getHours());
				date.setMinutes(schedule.getMinutes());
				date.setSeconds(0);
				FlightQuality f = new FlightQuality(airline, flightNumber, date , departure, arrival, false);
				f.setDelay(delayDeparture, delayArrival);
				fs.add(f);
			}
		}
		
		return fs;
	}*/
}
