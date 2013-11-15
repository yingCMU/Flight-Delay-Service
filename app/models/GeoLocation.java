package models;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;


public class GeoLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3454142792990964691L;
	protected String city;
	protected String zipcode;
	protected double longitude;
	protected double latitude;
	protected String address;
	public GeoLocation(String address, String city, String zipcode, double longitude, double latitude){
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
		this.city = city;
		this.zipcode = zipcode;
	}
	public GeoLocation(String city, String zipcode, double longtitude, double latitude){
		this("", city, zipcode, longtitude, latitude);
	}
	public GeoLocation(double longtitude, double latitude){
		this("","", longtitude, latitude);
	}
	public String getCity(){
		return this.city;
	}
	public String getZipCode(){
		return this.zipcode;
	}
	public double getLongitude(){
		return this.longitude;
	}
	public double getLatitude(){
		return this.latitude;
	}
	public String getAddress(){
		return this.address;
	}
	public static GeoLocation getGeoLocation(){
		Random random = new Random();
		int i = (random.nextBoolean())? -1:1;
		System.out.println(i);
		double longtitude = i * random.nextInt(18000)/100.0;
		i = (random.nextBoolean())? -1:1;
		System.out.println(i);
		double latitude = i * random.nextInt(9000)/100.0;
		return new GeoLocation(latitude, longtitude);
	}

	public String toString(){
		return this.city + "-"+ this.zipcode + "(lat: " + String.valueOf(this.latitude) + ", lng:" + String.valueOf(this.longitude) + ")";
	}

}
