package models;

public class YelpBiz {
	protected double distance;
	protected double rating;
	protected String rating_img_url;
	protected String image_url;
	protected String name;
	protected String categories;
	protected String category;
	protected String display_phone;
	protected GeoLocation geoLocation;
	protected String address;
	public YelpBiz(String name, String category, double latitude, double longitude,
			String address, String city, String zipcode) {
		this.address = address;
		this.name = name;
		this.category = category;
		this.geoLocation = new GeoLocation(address, city, zipcode, longitude,
				latitude);
	}
	public String getCategory(){
		return this.category;
	}
	public String getImage_url(){
		return this.image_url;
	}
	public String getAddress(){
		return this.address;
	}
	public String getCategories(){
		return this.categories;
	}
	public String getRating_img_url(){
		return this.rating_img_url;
	}
	public String getName() {
		return this.name;
	}
	public String getPhone(){
		return this.display_phone;
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
	public void setCategories(String categories){
		this.categories = categories;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public void setPhone(String phone){
		this.display_phone = phone;
	}
	public void setRating_img_url(String url){
		this.rating_img_url = url;
	}
	public void setImage_url(String url){
		this.image_url = url;
	}
	public void setAddress(	String addr){
		this.address = addr;
	}
	public String toString(){
		return "name: "+name+"cat:"+category+" distance:"+distance;
		
	}
}
