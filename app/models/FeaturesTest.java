package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FeaturesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String datestr= "2013-05-18";
		/*
		 * "DAY_OF_MONTH",
			"DAY_OF_WEEK",
			"QUARTER",
		 */
		
		try {
			String strDate ="2013-12-30";
			//DateFormat inputDF  = new SimpleDateFormat("yyyy-mm-dd");
			DateFormat inputDF  = new SimpleDateFormat("yyyy-mm-dd");
			
			Date date1 = inputDF.parse(strDate);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			String month = strDate.split("-")[1];
			String date = strDate.split("-")[2];
			int day = date1.getDay();
			//int day = cal.get(Calendar.DAY_OF_WEEK);
			day %=7;
			//int year = cal.get(Calendar.YEAR);

			System.out.println("strDate: "+strDate+"\n  -month: "+month+" -day: "+day+" - date :"+date);
			//Prints 8 - 30 - 2011 (because months are zero-based; demo)
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
