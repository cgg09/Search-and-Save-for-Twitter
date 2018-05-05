package application.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateChecker {

	public static String oldestDate(String d1, String d2) throws ParseException {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Date date1 = format.parse(d1);
		Date date2 = format.parse(d2);

		if (date1.compareTo(date2) < 0) {
			return d1;
		} else if (date1.compareTo(date2) > 0) {
			return d2;
		} else {
			return d1;
		}
	}
	
}
