package com.yyx.library;

import java.util.Calendar;

public class DateUtils {
	/**
     * Get the day of the month by year and month
     * 
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
		return dateOfMonth;

//		month++;
//		switch (month) {
//		case 1:
//		case 3:
//		case 5:
//		case 7:
//		case 8:
//		case 10:
//		case 12:
//		    return 31;
//		case 4:
//		case 6:
//		case 9:
//		case 11:
//		    return 30;
//		case 2:
//			if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)){
//				return 29;
//			}else{
//				return 28;
//			}
//		default:
//			return  -1;
//		}
    }
    /**
     * Returns the day of the week on the 1st of the current month
     * @param year
     * 		years
     * @param month
     * 		Month, passed into the system to obtain, does not need to be normal
    */
    public static int getFirstDayWeek(int year, int month){
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(year, month, 1);
    	return calendar.get(Calendar.DAY_OF_WEEK);
    }
    
}
