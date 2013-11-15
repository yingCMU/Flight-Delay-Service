package models.fetcher;

import java.io.IOException;
import java.net.URISyntaxException;

import models.GeoLocation;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class GeoLocationFetcher {
	public static void main(String [ ] args){
		String city = "PHL";
		System.out.println("geo of " + city + ":");
		System.out.println(GeoLocationFetcher.Fetch(city));
		
	}	
	public static GeoLocation Fetch(String airportCode){
		HttpClient httpclient = new DefaultHttpClient();
		GeoLocation geo = null;
		try {
            URIBuilder uri = new URIBuilder("http://maps.googleapis.com/maps/api/geocode/json");
            uri.addParameter("address", airportCode.replace(" ", "+"));
            uri.addParameter("sensor", "false");
            HttpGet httpget = new HttpGet(uri.build());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println(responseBody);
            ObjectMapper objectMapper = new ObjectMapper();  
            try  
            {  
            	JsonFactory factory = objectMapper.getJsonFactory();
    			JsonParser jsonParser = factory.createJsonParser(responseBody);
            	ObjectCodec oc = jsonParser.getCodec();
        		JsonNode node = (JsonNode) oc.readTree(jsonParser).get("results").get(0);
        		JsonNode geometry = node.get("geometry").get("location");
        		JsonNode address = node.get("address_components");
        		String city = "";
        		String zipcode = "";
        		for(int i=0;i<address.size();i++){
        			JsonNode component = address.get(i);
        			String types = component.get("types").get(0).asText();
        			if(types.equals("administrative_area_level_2") || types.equals("locality")){
        				city = component.get("long_name").asText();
        			}
        			if(types.equals("administrative_area_level_1")){
        				city += ", " + component.get("long_name").asText();
        			}
        			/*
        			if(type.equals("country")){
        				city += "," + component.get("long_name");
        			}
        			*/
        			if(types.equals("postal_code")){
        				zipcode = component.get("long_name").asText();
        			}
        		}
        		return  new GeoLocation(city, zipcode, geometry.get("lng").asDouble(), geometry.get("lat").asDouble());  
               
            }  
            catch(Exception e)  
            {  
                e.printStackTrace();  
            }  
         
        } catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		} catch (URISyntaxException e) {
		
			e.printStackTrace();
		} finally {
            httpclient.getConnectionManager().shutdown();
        }
		return geo;
	}

}