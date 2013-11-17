package models;

public class TimeZone {

	
	private String localTime;
	private String utcOffset;		//Name of the airline.
	
	public TimeZone(String utcOffset, String localTime){
		this.setUtcOffset(utcOffset);
		this.setLocalTime(localTime);
		
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getUtcOffset() {
		return utcOffset;
	}

	public void setUtcOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}
	
}
