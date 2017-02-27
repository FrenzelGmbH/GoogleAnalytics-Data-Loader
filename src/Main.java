import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Main {

public static void main(String[] args) {
    try {
    	String configXML="";
    	String IDsXML="";
    	String QueryXML="";
    	String sDate = "";
    	String eDate = "";
    	String sYear = "";
    	int sMonth = 1;
    	int eMonth = 12;
    	String eYear = "";
    	String format = "csv";
    	String targetPath = "";
    	Boolean showAccountIds = false;
    	
    	int k = 0;
    	for(String arg:args){
    		if(arg.startsWith("-")){
    			if(arg.equals("-accountInfo")){
    				showAccountIds = true;
    				continue;
    			}
    			if(k<args.length-1){
        			if(!args[k+1].startsWith("-")){
            			switch(arg){
        				case "-q": 
        					QueryXML = args[k+1];
        					break;
        				case "-i":
        					IDsXML = args[k+1];
        					break;
        				case "-c":
        					configXML = args[k+1];
        					break;
        				case "-sd":
        					sDate = args[k+1];
        					break;
        				case "-ed":
        					eDate = args[k+1];
        					break;
        				case "-sy":
        					sYear = args[k+1];
        					break;
        				case "-ey":
        					eYear = args[k+1];
        					break;
        				case "-tp":
        					targetPath = args[k+1];
        					break;
        				case "-f":
        					if(args[k+1].equals("json")){
        						format = "json";
        					}
        					break;
        				case "-sm":
        					if(Integer.valueOf(args[k+1].toString())>=1 && Integer.valueOf(args[k+1].toString())<=12){
        						sMonth = Integer.valueOf(args[k+1]);
        					}
        					break;
            			case "-em":
        					if(Integer.valueOf(args[k+1].toString())>=1 && Integer.valueOf(args[k+1].toString())<=12){
        						eMonth = Integer.valueOf(args[k+1]);
        					}
        					break;
            			}
        			}
    			}
    		}
    		k++;
    	}
    	//Set default values
    	if(sDate.equals("") && eDate.equals("")){
    		if(sYear.equals("") && eYear.equals("")){
    			//Date of last Month
    			Calendar x = Calendar.getInstance();
    			x.add(Calendar.MONTH, -1);
    			
    			sYear = Integer.toString(x.get(Calendar.YEAR));
    			System.out.printf("startYear is set by default to %s%n", sYear);
    			eYear = sYear;
    			System.out.printf("endYear is set by default to %s%n", eYear);
    			sMonth = x.get(Calendar.MONTH)+1;
    			System.out.printf("startMonth is set by default to %s%n", sMonth);
    			eMonth=sMonth;
    			System.out.printf("endMonth is set by default to %s%n", eMonth);
    		}
    	}    	
    	
    	//Google Analytics Data:
    	String appName = "Google Analytics CSV Loader";
    	JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    	
    	//load config.xml
    	Config localConfig = Config.ReadConfigFromXML(configXML);
    	
        Analytics analytics = Data.initializeAnalytics(jsonFactory, localConfig.getServiceAccount(), localConfig.getKeyFilePath(), appName);
    	
        if(showAccountIds){   
        	Data.printAccountInfos(analytics, Data.getAccounts(analytics));  
        	return;
        }
        int counter = 1;
        
        if(sYear != "" && eYear!=""){
        	for(GoogleAnalyticsId gid:GoogleAnalyticsId.ReadIdsFromXml(IDsXML)){
        		for(GoogleQuery gq:GoogleQuery.readQueriesFromXML(QueryXML)){
        			for(int i = Integer.parseInt(sYear);i<=Integer.parseInt(eYear);i++){
        				int startMonth,endMonth,j =0;
        				if(i == Integer.parseInt(sYear)){
        					startMonth = sMonth;
        				}else{
        					startMonth = 1;
        				}
        				if(i == Integer.parseInt(eYear)){
        					endMonth = eMonth;
        				}else{
        					endMonth = 12;
        				}        				
        				for(j = startMonth; j<=endMonth;j++){
        					DecimalFormat decim = new DecimalFormat("00");
        					String monthString = decim.format(j);
        					GaData gdata;
                			List<GaData> pages = new ArrayList<GaData>();
                			int startIndex = 1;
                			do{
                				gdata = Data.getQuery(analytics,gid.getID(),Helper.getDateFormat(j, i), Helper.getLastDayOfMonthDate(j, i), gq, startIndex);
                				pages.add(gdata);
                				
                				System.out.println("Query Nr.: " + counter);
                				System.out.println("Year: " + i +"; Month: " + j);
                     			System.out.println(gid.getID() + ":" +gid.getShortName() +"->"+ gq.getQueryName());
                     			System.out.println("TotalResults: " + gdata.getTotalResults());
                     			System.out.println("CurrentResults: " + gdata.getRows().size());    				 
                				startIndex += 10000;
                				
                				counter++;
                			}while(gdata.getNextLink() != null);
                			
                			if(format=="json"){
                				Data.writeToJSON(pages, targetPath +"/"+gid.getShortName()+"_"+gq.getQueryName()+"_"+ i +"_"+ monthString +".json");
                			}else{
                				Data.writeToCSV(pages,targetPath +"/"+gid.getShortName() +"_"+gq.getQueryName()+"_"+ i +"_"+ monthString +".csv");
                			}               			
                			System.out.println("-------");
        				}
        			}
        		}
        	}    	
        }else{
        	System.out.println(eYear + "-" + sYear);
        	SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	java.util.Date l = DateFormat.parse(sDate);
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(l);
        	
        	for(GoogleAnalyticsId gid:GoogleAnalyticsId.ReadIdsFromXml(IDsXML)){
        		for(GoogleQuery gq:GoogleQuery.readQueriesFromXML(QueryXML)){
        			
        			GaData gdata;
        			List<GaData> pages = new ArrayList<GaData>();
        			int startIndex = 1;
        			do{
        				gdata = Data.getQuery(analytics,gid.getID(),sDate, eDate, gq, startIndex);
        				pages.add(gdata);
        				 
             			System.out.println(gid.getID() + ":" +gid.getShortName() +"->"+ gq.getQueryName());
             			System.out.println("TotalResults: " + gdata.getTotalResults());
             			System.out.println("CurrentResults: " + gdata.getRows().size());    				 
        				startIndex += 10000;
        			}while(gdata.getNextLink() != null);
        			
        			Data.writeToCSV(pages,targetPath +"/"+gid.getShortName()+"_"+gq.getQueryName()+"_"+ cal.get(Calendar.YEAR) +"_"+ (cal.get(Calendar.MONTH)+1) +".csv");
        			
        			System.out.println("-------");
        		}
        	}    	
        }   
      
    } catch (Exception e) {
      e.printStackTrace();
    }       
}
}
