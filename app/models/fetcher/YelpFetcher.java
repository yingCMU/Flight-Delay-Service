package models.fetcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import models.GeoLocation;
import models.YelpBiz;
import models.YelpRecommendations;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Airport;
public class YelpFetcher {
	protected static OAuthService service;
	protected static ArrayList<HashMap<String, String>> oauthKeys;
	protected static Token accessToken;
	protected static int nextKeyIndex;
	
	public YelpFetcher() {
		
		CsvReader csv = null;
		if(oauthKeys == null){
			nextKeyIndex = 0;
			oauthKeys = new ArrayList<HashMap<String, String>>();
			try {
				csv = new CsvReader("res/yelp.csv");
			
				csv.readHeaders();
				while (csv.readRecord())
				{
					HashMap<String, String>  h = new HashMap<String, String>();
					h.put("consumerKey", csv.get(0));
					h.put("consumerSecret", csv.get(1));
					h.put("token", csv.get(2));
					h.put("tokenSecret", csv.get(3));
					oauthKeys.add(h);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.reset();
		}
	}
	public void reset(){
		HashMap<String, String> keySet = oauthKeys.get(nextKeyIndex);
		service = new ServiceBuilder().provider(YelpApi2.class)
				.apiKey(keySet.get("consumerKey")).apiSecret(keySet.get("consumerSecret")).build();
		accessToken = new Token(keySet.get("token"), keySet.get("tokenSecret"));
		nextKeyIndex += 1;
		if(nextKeyIndex >= oauthKeys.size()) nextKeyIndex %= oauthKeys.size();
		
	}
	public YelpRecommendations fetch(String term, GeoLocation city,String radius,String limit) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
		request.addQuerystringParameter("term", term);
		System.out.println("yelp fething term="+term);
		request.addQuerystringParameter("ll", city.getLatitude() + "," + city.getLongitude());
		request.addQuerystringParameter("radius_filter", ""+Integer.parseInt(radius)*1609);
		//radius in miles, filter in meters
		request.addQuerystringParameter("sort", "1");
		request.addQuerystringParameter("limit", limit);
		YelpRecommendations rs = this.fetch(request);
		return rs;
	}
	
	public YelpRecommendations fetch(String term, String city) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
		request.addQuerystringParameter("term", term);
		request.addQuerystringParameter("location", city);
		request.addQuerystringParameter("limit", "4");
		return this.fetch(request);
	}
	protected YelpRecommendations fetch(OAuthRequest request) {
		this.reset();
		
		
		service.signRequest(accessToken, request);
		Response response = request.send();
		ObjectMapper objectMapper = new ObjectMapper();
		YelpRecommendations recommendations = null;
		String responseBody = response.getBody(); 
		
		
		try {
			//recommendations = objectMapper.readValue(responseBody, YelpRecommendations.class);
			JsonFactory factory = objectMapper.getJsonFactory();
			JsonParser jsonParser = factory.createJsonParser(responseBody);
			ObjectCodec oc = jsonParser.getCodec();
			JsonNode node = (JsonNode) oc.readTree(jsonParser).get("businesses");
			recommendations = new YelpRecommendations("");
			if(node == null) {System.out.println("node is null");return recommendations;}
			for (int i = 0; i < node.size(); i++) {
				JsonNode business = node.get(i);
				String name = business.get("name").asText();
				double rating  = 0;
				String rating_img_url="";
				String image_url = "";
				String categories="";
				try{
					rating = business.get("rating").asDouble();
					rating_img_url = business.get("rating_img_url").asText();
					image_url = business.get("image_url").asText();
					categories = business.get("categories").asText();
					
				}
				catch(Exception e){
					
				}
				String address ="";
				try{
					address= business.get("location").get("display_address").get(0).asText();
				}
				catch(Exception e){
					
				}
				String city ="";
				try{
					city = business.get("location").get("city").asText();
					
				}
				catch (Exception e){
					
				}
				String zipcode ="";
				try{
					zipcode = business.get("location").get("postal_code").asText();
				}
				catch(Exception e){
					
				}
				double distance =0;
				try{
					distance = business.get("distance").asDouble() * 0.000621371;
				}
				catch (Exception e){
					
				}
				String category = "";
				try{
					category= business.get("categories").get(0).get(0).asText();
				}
				catch (Exception e){
					
				}
				String display_phone = "";
				try{
					display_phone = business.get("phone").asText();
				}
				catch (Exception e){
					
				}
				double longitude = business.get("location").get("coordinate")
						.get("longitude").asDouble();
				double latitude = business.get("location").get("coordinate")
						.get("latitude").asDouble();
				YelpBiz b = new YelpBiz(name, category, latitude, longitude,
						address, city,	zipcode);
				b.setRating(rating);
				b.setDistance(distance);
				b.setPhone(display_phone);
				b.setImage_url(image_url);
				b.setRating_img_url(rating_img_url);
				b.setCategories(categories);
				//System.out.println(b.toString());
				recommendations.addBiz(b);
			}
			
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return recommendations;
	}

	// CLI
	public static void main(String[] args) {
		
		YelpFetcher yf = new YelpFetcher();
		//ArrayList<YelpBiz> rl = yf.fetch("restaurant", Airport.findAirport("SFO").getGeoLocation(), "1").getRecommendations();
		 GeoLocation geo = new GeoLocation(Double.parseDouble("-122.374889"),Double.parseDouble("37.618972"));
		//HashMap<String, YelpRecommendations> map = yf.fetchMultiply("", geo, "5");
		yf.fetch("hotel",geo,"1","5");
		//YelpRecommendations rm = map.get("Transportation"); 
		//rm.
		/* ArrayList<YelpBiz> rl= yf.fetch("hotel",geo,"1").getRecommendations();
		 System.out.println(rl.size()); 
		 for(YelpBiz r :rl)
				System.out.println(r.toString());
			*/ 
		//System.out.println(yf.fetch("restaurant", "94086").getRecommendations());
		
	}

}
