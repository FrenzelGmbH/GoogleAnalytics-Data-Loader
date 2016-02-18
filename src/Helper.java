import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Helper {
	public static String StringListToString(List<String> list){
		if(list==null){
			return "(null)";
		}
		String result="";
		for(String s:list){
			result+=s+",";
		}
		return result;		
	}
	
	public static String getLastDayOfMonthDate(int month, int year) {
	    Calendar calendar = Calendar.getInstance();
	    // passing month-1 because 0-->jan, 1-->feb... 11-->dec
	    calendar.set(year, month - 1, 1);
	    calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
	    Date date = calendar.getTime();
	    DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	    return DATE_FORMAT.format(date);
	}
	
	public static String getDateFormat(int month, int year) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month - 1, 1);
	    Date date = calendar.getTime();	    
	    DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	    return DATE_FORMAT.format(date);
	}
}
