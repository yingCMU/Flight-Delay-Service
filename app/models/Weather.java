package models;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
public @JsonDeserialize(using = Weather.class)
@JsonIgnoreProperties(ignoreUnknown=true)
class Weather extends JsonDeserializer<Weather>{
	double temp; // in F
	double windspeed; // in miles
	int code;
	String description;
	String icon;
	int visibility;
	int pressure;
	Date date;
	public Weather(){
		
	}
	public Weather(Date date, double temp, double windspeed, int visibility, int pressure, int code,String description, String icon){
		this.date = date;
		this.temp = temp;
		this.windspeed = windspeed;
		this.code = code;
		this.description = description;
		this.icon = icon;
		this.visibility = visibility;
		this.pressure = pressure;
	}
	public Weather(Date date, double temp, double windspeed, int code, String description, String icon){
		this(date, temp, windspeed, -1, -1, code, description, icon);
	}
	public void setDate(Date date){
		this.date = date;
	}
	public void setVisibility(int visibility){
		this.visibility = visibility;
	}
	public void setTemp(double temp){
		this.temp = temp;
	}
	public void setPressure(int pressure){
		this.pressure = pressure;
	}
	public void setWindSpeed(double windspeed){
		this.windspeed = windspeed;
	}
	public Date getDate(){
		return this.date;
	}
	public String getDateString(){
		return this.date.toGMTString();
	}
	public double getTemp(){
		return this.temp;
	}
	public double getWindSpeed(){
		return this.windspeed;
	}
	public int getWeatherCode(){
		return this.code;
	}
	public String getWeatherDescription(){
		return this.description;
	}
	public String getWeatherIcon(){
		return this.icon;
	}
	public int getVisibility(){
		return this.visibility;
	}
	public int getBarometricPressure(){
		return this.pressure;
	}
	
	public String toString(){
		return "Temp:" + String.valueOf(this.temp) + "F, windspeed: " + String.valueOf(this.windspeed) + "miles/hr, desc: " + this.description + ", icon: " + this.icon;
		
	}
	public static String toCSV(HashMap<Date, Weather> ws) throws IntrospectionException{
		Iterator<Date> it = ws.keySet().iterator();
		String output = "";
		output = "'date', 'tempF', 'windSpeed', 'visibility', 'BarometricPressure','weatherCode', 'weatherDescription', 'weatherIcon'\n";
		while(it.hasNext()){
			Date date = it.next();
			output += "'" + date.toGMTString() + "', ";
			Weather w = ws.get(date);
			output += w.getTemp() + ", ";
			output += w.getWindSpeed() + ", ";
			output += w.getVisibility() + ",";
			output += w.getBarometricPressure() + ",";
			output += "'" + w.getWeatherCode() + "', ";
			output += "'" + w.getWeatherDescription() + "', ";
			output += "'" + w.getWeatherIcon() ;
			output += "\n";
		}
		return output;
	}
	public String toCSV(){
		String output = "";
		
		output = "'date', 'tempF', 'windSpeed', 'weatherCode', 'weatherDescription', 'weatherIcon'\n";
		Date date = this.date;
		output += "'" + date.toString() + "', ";
		output += this.getTemp() + ", ";
		output += this.getWindSpeed() + ", ";
		output += "'" + this.getWeatherCode() + "', ";
		output += "'" + this.getWeatherDescription() + "', ";
		output += "'" + this.getWeatherIcon() ;
		output += "\n";
		return output;
	}
	@Override
	public Weather deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		return null;
	}

}
