package models;

import java.util.ArrayList;  
public class YelpRecommendations {
	protected String type;
	protected ArrayList<YelpBiz> list;

	public YelpRecommendations(String type) {
		this.type = type;
		this.list = new ArrayList<YelpBiz>();
	}

	public ArrayList<YelpBiz> getRecommendations() {
		return this.list;
	}

	public void addBiz(YelpBiz newBiz) {
		this.list.add(newBiz);
	}
}