package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
/*The data comes from http://openflights.org/data.html */

import com.csvreader.CsvReader;

public class Airline {

	
	int id;				//Unique OpenFlights identifier for this airline.
	String name;		//Name of the airline.
	String alias;		//Alias of the airline. For example, All Nippon Airways is commonly known as "ANA".
	String iataCode;	//2-letter IATA code, if available.
	String icaoCode; 	//3-letter ICAO code, if available.
	String callsign;	//Airline callsign.
	String country;		//Country or territory where airline is incorporated.
	Boolean active;		//"Y" if the airline is or has until recently been operational, "N" if it is defunct. (This is only a rough indication and should not be taken as 100% accurate.)
	
	protected static HashMap<String, Airline>airlines = null; 
	
	public Airline(int id, String name, String alias, String iataCode, String icaoCode, String callsign, String country, Boolean active){
		this.id = id;
		this.name = name; 
		this.alias = alias;
		this.iataCode = iataCode;
		this.icaoCode = icaoCode;
		this.callsign = callsign;
		this.country = country;
		this.active = active;
	}
	public static String getAirlineName(String icaoCode){
		if(airlines == null){
			airlines = new HashMap<String, Airline>();
			CsvReader csv;
			try {
				csv = new CsvReader("res/airlines.dat");
				while (csv.readRecord()){
					int id = Integer.parseInt(csv.get(0));
					String name = csv.get(1);
					String alias = csv.get(2);
					String iataCode = csv.get(3);
					String icaoCode1 = csv.get(4);
					String callsign = csv.get(5);
					String country = csv.get(6);
					boolean active = csv.get(7).equals("Y");
					
					Airline a = new Airline(id, name, alias,iataCode, icaoCode1, callsign, country, active);
					airlines.put(icaoCode1, a);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(airlines.containsKey(icaoCode)) return airlines.get(icaoCode).name;
		
		return "";
		
	}
}
