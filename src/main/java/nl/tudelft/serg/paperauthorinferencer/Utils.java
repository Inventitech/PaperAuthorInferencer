package nl.tudelft.serg.paperauthorinferencer;

import java.util.Calendar;

public class Utils {

	static int currentYear = Calendar.getInstance().get(Calendar.YEAR);

	public static boolean isReasonablyPossilbeYear(int year) {
		return year > 1800 && year <= currentYear;
	}
}
