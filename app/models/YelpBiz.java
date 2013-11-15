package models;

public class YelpBiz {
	protected double distance;
	protected double rating;
	protected String name;
	protected String category;
	protected String phone;
	protected GeoLocation geoLocation;

	public YelpBiz(String name, String category, double latitude, double longitude,
			String address, String city, String zipcode) {
		this.name = name;
		this.category = category;
		this.geoLocation = new GeoLocation(address, city, zipcode, longitude,
				latitude);
	}
	public String getCategory(){
		return this.category;
	}
	public String getName() {
		return this.name;
	}
	public String getPhone(){
		return this.phone;
	}

	public GeoLocation getGeoLocation() {
		return this.geoLocation;
	}
	public double getRating() {
		return this.rating;
	}
	public double getDistance() {
		return this.distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
}
