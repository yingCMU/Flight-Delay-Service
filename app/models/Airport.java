/**
 * Info from http://openflights.org/data.html
 * 
 * Airport ID	Unique OpenFlights identifier for this airport.
 * Name	Name of airport. May or may not contain the City name.
 * City	Main city served by airport. May be spelled differently from Name.
 * Country	Country or territory where airport is located.
 * IATA/FAA	3-letter FAA code, for airports located in Country "United States of America".
 * 3-letter IATA code, for all other airports.
 * Blank if not assigned.
 * ICAO	4-letter ICAO code.
 * Blank if not assigned.
 * Latitude	Decimal degrees, usually to six significant digits. Negative is South, positive is North.
 * Longitude	Decimal degrees, usually to six significant digits. Negative is West, positive is East.
 * Altitude	In feet.
 * Timezone	Hours offset from UTC. Fractional hours are expressed as decimals, eg. India is 5.5.
 * DST	Daylight savings time. One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown). See also: Help: Time
 * */
package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Airport {

	int id; // Airport ID Unique OpenFlights identifier for this airport.
	String name; // Name of airport. May or may not contain the City name.
	String city; // Main city served by airport. May be spelled differently from
					// Name.
	String country; // Country or territory where airport is located.
	String iataCode; // IATA/FAA 3-letter FAA code, for airports located in
						// Country "United States of America". 3-letter IATA
						// code, for all other airports. Blank if not assigned.
	String icaoCode; // ICAO 4-letter ICAO code. Blank if not assigned.
	GeoLocation geolocation;
	double latitude; // Decimal degrees, usually to six significant digits.
						// Negative is South, positive is North.
	double longitude; // Decimal degrees, usually to six significant digits.
						// Negative is West, positive is East.
	double altitude; // In feet.
	double timezone; // Hours offset from UTC. Fractional hours are expressed as
						// decimals, eg. India is 5.5.
	String dst; // Daylight savings time. One of E (Europe), A (US/Canada), S
				// (South America), O (Australia), Z (New Zealand), N (None) or
				// U (Unknown). See also: Help: Time

	protected static HashMap<String, Airport> airports;

	public static void main(String [ ] args) throws IOException, ParseException{
		//System.out.println(new AirportInfoFetcher().fetch());
		System.out.println(new ObjectMapper().writeValueAsString(findAirport("SFO")));
		
	}
	public static Airport findAirport(String query) {
		if (airports == null) {
			airports = new HashMap<String, Airport>();
			CsvReader csv;
			try {
				csv = new CsvReader("res/airports.dat");
				while (csv.readRecord()) {
					int id = Integer.parseInt(csv.get(0));
					String name = csv.get(1);
					String city = csv.get(2);
					String country = csv.get(3);
					String iataCode = csv.get(4);
					String icaoCode = csv.get(5);
					double latitude = Double.parseDouble(csv.get(6));
					double longitude = Double.parseDouble(csv.get(7));
					double altitude = Double.parseDouble(csv.get(8));
					double timezone = Double.parseDouble(csv.get(9));
					String dst = csv.get(10);
					Airport a = new Airport(id, name, city, country, iataCode, icaoCode, latitude, longitude, altitude, timezone, dst);
					airports.put(icaoCode, a);
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
		if(airports.containsKey(query)){
			return airports.get(query);
		}
		else{
			Iterator<Airport> it = airports.values().iterator();
			while(it.hasNext()){
				Airport a = it.next();
				if(a.name.equals(query)) return a;
				else if(a.iataCode.equals(query)) return a;
			}
		}
		return null;
	}
	
	public Airport(int id, String name, String city, String country,
			String iataCode, String icaoCode, double latitude,
			double longitude, double altitude, double timezone, String dst) {
		this.id = id;
		this.name = name;
		this.country = country;
		this.iataCode = iataCode;
		this.icaoCode = icaoCode;
		this.geolocation = new GeoLocation(city, "", longitude, latitude);
		this.altitude = altitude;
		this.timezone = timezone;
		this.dst = dst;
	}

	public String getName() {
		return this.iataCode;
	}
	public String getFullName(){
		return this.name;
	}
	public String getIataCode() {
		return this.iataCode;
	}

	public String getIcaoCode() {
		return this.icaoCode;
	}

	public GeoLocation getGeoLocation() {
		return this.geolocation;
	}

	public double getTimezone() {
		return this.timezone;
	}

	public String getCountry() {
		return this.country;
	}

	public String getDst() {
		return this.dst;
	}

	

}
