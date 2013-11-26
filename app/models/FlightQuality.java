package models;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import models.fetcher.TimeZoneFetcher;
import models.fetcher.WeatherFetcher;
import models.fetcher.YelpFetcher;

public class FlightQuality{
	//protected TimeZone depTZ;
	//protected TimeZone arrTZ;
	protected Error error;
	protected String flightNumber;
	private int DAY_OF_WEEK;
	private int DAY_OF_MONTH;
	protected int departDelay;
	protected int arrivalDelay;
	private String airlineName;
	protected Airport departAirport;
	protected Airport arrivalAirport;
	protected Weather departWeather;
	protected Weather arrivalWeather;
	protected String airline;
	protected String depTime;
	protected String arrivalDate;
	protected HashMap<String, YelpRecommendations> recommendations;
	private String depDate;
	private String arrTime;
	private int quater=4;
	public FlightQuality(){
		
	}
	public FlightQuality(String airline, String flightNumber, String arrivalTime, String departureTime){
		this.airline = airline;
		this.flightNumber = flightNumber;
		this.arrTime = arrivalTime;
		this.depTime = departureTime;
		
		this.recommendations = new HashMap<String, YelpRecommendations>();
	}
	public FlightQuality(String flightNumber, String arrivalTime, String departureTime){
		this(flightNumber.substring(0,2), flightNumber.substring(2,flightNumber.length()), arrivalTime, departureTime);
	}
	public FlightQuality(String airline, String flightNumber, String date, String departure, String arrival){
		this(airline, flightNumber, date, departure, arrival, true);
	}
	public FlightQuality(String airline, String flightNumber, String date, String departure, String arrival, boolean fetchData){
		this.flightNumber = flightNumber;
		this.depDate = date;
		this.departAirport = Airport.findAirport(departure);
		
		this.arrivalDate =arrival;
		this.airline = airline;
		this.arrivalAirport = Airport.findAirport(arrival);
		this.recommendations = new HashMap<String, YelpRecommendations>();
	}
	public String getArrivalTime(){
		return this.arrTime;
	}
	
	public String getDepTime(){
		return this.depTime;
	}
	
	public String getArrivalDate(){
		return this.arrivalDate;//.toGMTString();
	}
	//public void setArrTZ(TimeZone )
	public void setArrivalDate(String date){
		this.arrivalDate = date;
	}
	public String getAirline(){
		return this.airline;
	}
	public String getDepDate(){
		return this.depDate;//.toGMTString();
	}
	public Weather getDepartWeather(){
		return this.departWeather;
	}
	public Weather getArrivalWeather(){
		return this.arrivalWeather;
	}
	public Airport getDepartAirport(){
		return this.departAirport;
	}
	public void setDepartAirport(Airport ap){
		this.departAirport= ap;
	}
	public Airport getArrivalAirport(){
		return this.arrivalAirport;
	}
	public HashMap<String, YelpRecommendations> getRecommendations(){
		return this.recommendations;
	}
	
	public int  getDepartDelay(){
		return this.departDelay;
	}
	public int getArrivalDelay(){
		return this.arrivalDelay;
	}
	public String getFlightNumber(){
		return this.flightNumber;
	}
	public void setQuater(int q){
		this.quater = q;
	}
	public void setDelay(int depart, int arrival){
		this.departDelay = depart;
		this.arrivalDelay = arrival;
		
	}
	public void setDepartureAirport(String airport){
		this.departAirport = Airport.findAirport(airport);
	}
	public void setArrivalAirport(String airport){
		this.arrivalAirport = Airport.findAirport(airport);
	}
	public void setAirline(String airline){
		this.airline = airline;
	}
	public void setDepDate(String date){
		this.depDate = date;
	}
	public void fetchWeather(){
		System.out.println("!!!!!!!"+this.depDate);
		this.departWeather = WeatherFetcher.Fetch(this.departAirport.getGeoLocation(), this.depDate);
		this.arrivalWeather = WeatherFetcher.Fetch(this.arrivalAirport.getGeoLocation(),this.arrivalDate);
	}
	/*public void fetchTimeZone(){
		//System.out.println("!!!!!!!"+this.depDate);
		this.depTZ = TimeZoneFetcher.fetch(this.departAirport.getGeoLocation());
		this.arrTZ = TimeZoneFetcher.fetch(this.arrivalAirport.getGeoLocation());
	}*/
	/*public void fetchRecommendations(){

		YelpFetcher yf = new YelpFetcher();
		if(this.departDelay <= 120){ //&& this.departDelay > 0){
			this.recommendations.put("Dining", yf.fetch("fast+food", this.departAirport.getGeoLocation(),"3"));
		}
		
		else if(this.departDelay >= 120){
			this.recommendations.put("Dining", yf.fetch("restaurant", this.departAirport.getGeoLocation(),"3"));
		}
		
		
		
		if(this.departDelay > 0){
			this.recommendations.put("Accomendation", yf.fetch("hotel", this.departAirport.getGeoLocation(),"3"));
			this.recommendations.put("Transportation", yf.fetch("transportation", this.departAirport.getGeoLocation(),"3"));
		}
		
		else{
			this.recommendations.put("Accomendation", yf.fetch("hotel", this.arrivalAirport.getGeoLocation(),"3"));
			this.recommendations.put("Transportation", yf.fetch("transportation", this.departAirport.getGeoLocation(),"3"));
		}
		
	}*/
	public String toString(){
		return "Date: "+this.depDate+"; Carrier : "+airline+"; Flight Number: " + this.flightNumber + "'s Service quality: \n" +
				"Delay on departure in " + this.departDelay + " minutes\n" +
				"Delay on Arrival in " + this.arrivalDelay + " minutes."; 
	}
	public int getDAY_OF_WEEK() {
		return DAY_OF_WEEK;
	}
	public void setDAY_OF_WEEK(int dAY_OF_WEEK) {
		DAY_OF_WEEK = dAY_OF_WEEK;
	}
	public int getDAY_OF_MONTH() {
		return DAY_OF_MONTH;
	}
	public void setDAY_OF_MONTH(int i) {
		DAY_OF_MONTH = i;
	}
	public int getQuater() {
		// TODO Auto-generated method stub
		return quater;
	}
	public String getAirlineName() {
		return airlineName;
	}
	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
}
