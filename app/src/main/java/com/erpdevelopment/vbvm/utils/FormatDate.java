package com.erpdevelopment.vbvm.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatDate {
	
	public static String formatDate(String date)
	{		
		if (date.substring(2, 3).equals(".") && date.substring(5, 6).equals("."))
			return date;
		else
			return ( date.substring(0, 2) + "." + date.substring(3, 5) + "." + date.substring(6) );		
	}
	
	//Validates date1 > date2
	public static boolean compareDates(String d1, String d2)
	{	
		boolean value = false;
		try{
			 
    		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        	Date date1 = sdf.parse(d1);
        	Date date2 = sdf.parse(d2);
 
        	if(date1.compareTo(date2)>0){
//        		System.out.println("Date1 is after Date2");
        		value = true;
        	}else if(date1.compareTo(date2)<0){
//        		System.out.println("Date1 is before Date2");
        		value = false;
        	}else if(date1.compareTo(date2)==0){
//        		System.out.println("Date1 is equal to Date2");
        		value = false;
        	}else{
        		System.out.println("How to get here?");
        	}
        	
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
		return value;
	}
	
	public static String[] getDateArray(Date date) {
//		Date date; // your date
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    String[] dateArray = new String[3];
	    dateArray[0] = String.valueOf(day);
	    dateArray[2] = String.valueOf(year);
	    
	    switch (month) {
			case 0: dateArray[1] = "Jan";			
				break;
			case 1: dateArray[1] = "Feb";			
				break;
			case 2: dateArray[1] = "Mar";			
				break;
			case 3: dateArray[1] = "Apr";			
				break;
			case 4: dateArray[1] = "May";			
				break;
			case 5: dateArray[1] = "Jun";			
				break;
			case 6: dateArray[1] = "Jul";			
				break;
			case 7: dateArray[1] = "Aug";			
				break;
			case 8: dateArray[1] = "Sep";			
				break;
			case 9: dateArray[1] = "Oct";			
				break;
			case 10: dateArray[1] = "Nov";			
				break;
			case 11: dateArray[1] = "Dec";			
				break;
			default:
				break;
		}
	    return dateArray;
	}
	
}
