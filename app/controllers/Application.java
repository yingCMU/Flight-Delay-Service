package controllers;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import models.Airport;
import models.GeoLocation;
import models.Prediction;
import models.Weather;
import models.YelpRecommendations;
import models.fetcher.AirportInfoFetcher;
import models.fetcher.WeatherFetcher;
import models.fetcher.YelpFetcher;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Application extends Controller {
  
  public static Result index() {
    return ok(index.render(""));
  }
  
  public static Result weathers(String format, String city) throws JsonGenerationException, JsonMappingException, IOException, IntrospectionException{
	  
	   Weather ws = WeatherFetcher.Fetch(city,"");
	  if(format.equals("csv")){
		  
		  return ok(ws.toCSV());//.as("text/csv");
	  }	
	  return ok(new ObjectMapper().writeValueAsString(ws));
  }
  public  static Result weather(String format, String city, String date) throws ParseException, JsonGenerationException, JsonMappingException, IOException{
	  
	  Weather w = WeatherFetcher.Fetch(city, date);
	  if(format.equals("csv")){
		  return ok(w.toCSV());
	  }
	  return ok(new ObjectMapper().writeValueAsString(w));
  }
  /*
  public  static Result recommendations(String query, String location) throws ParseException, JsonGenerationException, JsonMappingException, IOException{
	  YelpFetcher yf = new YelpFetcher();
	  YelpRecommendations yrs = yf.fetch(query, location);
	  return ok(new ObjectMapper().writeValueAsString(yrs));
  }
  */
  public  static Result recommendations(String query, String log, String lat, String radius,String limit) throws ParseException, JsonGenerationException, JsonMappingException, IOException{
	  YelpFetcher yf = new YelpFetcher();
	  GeoLocation geo = new GeoLocation(Double.parseDouble(log),Double.parseDouble(lat));
	  /* System.out.println("application :"+log+" "+lat);
	//  YelpRecommendations yrs = yf.fetch(query,geo,radius);*/
	  System.out.println("!!!!!!!!!!!!!!");
	  String[] words = query.split(",");
	  HashMap<String, YelpRecommendations> yrs = new HashMap<String, YelpRecommendations>(words.length);
	  for (String word: words){
		  System.out.println(word);
		  yrs.put(word, yf.fetch(word, geo,radius,limit));
	  }
	 	
	 		
	 return ok(new ObjectMapper().writeValueAsString(yrs));
  }
  
  public  static Result recommendations2( String log, String lat, String radius) throws ParseException, JsonGenerationException, JsonMappingException, IOException{
	  YelpFetcher yf = new YelpFetcher();
	  GeoLocation geo = new GeoLocation(Double.parseDouble(log),Double.parseDouble(lat));
	  /* System.out.println("application :"+log+" "+lat);
	//  YelpRecommendations yrs = yf.fetch(query,geo,radius);*/
	  System.out.println("!!!!!!!!!!!!!!");
	  HashMap<String, YelpRecommendations> yrs = new HashMap<String, YelpRecommendations>();
	  yrs.put("hotel", yf.fetch("hotel", geo,radius,"5"));
	
	  yrs.put("res", yf.fetch("res", geo,radius,"5"));
		
	 		
	 return ok(new ObjectMapper().writeValueAsString(yrs));
  }
  public static Result predictions(String flightID, String strDate) throws JsonGenerationException, JsonMappingException, IOException, ParseException{
	  Prediction p = new Prediction();
	  Date d = new Date();
	  if(!strDate.equals("")){
		  d = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
	  }
	  return ok( new ObjectMapper().writeValueAsString(p.predict( flightID, strDate)) );
  }
  /*
  public static Result flights(String airportCode) throws JsonGenerationException, JsonMappingException, IOException, ParseException{
	  return ok(new ObjectMapper().writeValueAsString(new AirportInfoFetcher().fetch(Airport.findAirport(airportCode))));
  }
  public static Result flights2(String departAirportCode, String arrivalAirportCode) throws JsonGenerationException, JsonMappingException, IOException, ParseException{
	  return ok(new ObjectMapper().writeValueAsString(new AirportInfoFetcher().fetch(Airport.findAirport(departAirportCode),  Airport.findAirport(arrivalAirportCode))));
  }*/
}