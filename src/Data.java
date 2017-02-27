import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;

public class Data {
	public static Analytics initializeAnalytics(JsonFactory jFactory, String serviceAccount, String keyFilePath, String appName) throws Exception {
	    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	    GoogleCredential credential = new GoogleCredential.Builder()
	        .setTransport(httpTransport)
	        .setJsonFactory(jFactory)
	        .setServiceAccountId(serviceAccount)
	        .setServiceAccountPrivateKeyFromP12File(new File(keyFilePath))
	        .setServiceAccountScopes(AnalyticsScopes.all())
	        .build();

	    // Construct the Analytics service object.
	    return new Analytics.Builder(httpTransport, jFactory, credential)
	        .setApplicationName(appName).build();
	  }

	  public static java.util.List<Account> getAccounts(Analytics analytics) throws IOException {
		  Accounts accounts = analytics.management().accounts().list().execute();

		  if (accounts.getItems().isEmpty()) {
			  System.err.println("No accounts found");
		  } else {
		      return accounts.getItems();
		  }
		  return null;
	  }

	  public static void printAccountInfos(Analytics analytics, java.util.List<Account> aList) throws IOException{
	      for(Account a:aList){
	    	  System.out.println(a.getName() + ": " + a.getId());
	    	  
	          Webproperties properties = analytics.management().webproperties()
	                  .list(a.getId()).execute();
	          java.util.List<Webproperty> pList = properties.getItems();
	          
	    	  for(Webproperty wp:pList){
	              Profiles profiles = analytics.management().profiles().list(a.getId(), wp.getId()).execute();
	    		  System.out.println("->" + wp.getName() + ": " + wp.getId() + "  => "+ wp.getDefaultProfileId() + " -> " + profiles.getItems().get(0).getId());
	    	  }
	      }
	  }

	  public static GaData getQuery(Analytics analytics, String profileId,String startDate, String endDate, GoogleQuery query, int startIndex) throws IOException {
		  	String metricString = "";
		  	String dimensionString = "";
		  	for(String x:query.getMetrics()){
		  		metricString+=x+",";
		  	}
		  	metricString = metricString.substring(0,metricString.length()-1);
		  	
		  	if(query.getDimensionFlag()){
			  	for(String y:query.getDimensions()){
			  		dimensionString +=y+",";
			  	}
			  	dimensionString = dimensionString.substring(0,dimensionString.length()-1);
		  	}	  	
		  	
		  	if(!query.getDimensionFlag() && !query.getSegmentFlag()){
		  		return analytics.data().ga().get(profileId, startDate, endDate, metricString).setMaxResults(10000).setStartIndex(startIndex).execute();
		  	}else if(!query.getDimensionFlag() && query.getSegmentFlag()){
		  		return analytics.data().ga().get(profileId, startDate, endDate, metricString).setSegment(query.getSegment()).setMaxResults(10000).setStartIndex(startIndex).execute();
		  	}else if(query.getDimensionFlag() && !query.getSegmentFlag()){
		  		return analytics.data().ga().get(profileId, startDate, endDate, metricString).setDimensions(dimensionString).setMaxResults(10000).setStartIndex(startIndex).execute();
		  	}else{
		  		return analytics.data().ga().get(profileId, startDate, endDate, metricString).setDimensions(dimensionString).setSegment(query.getSegment()).setMaxResults(10000).setStartIndex(startIndex).execute();
		  	}
		  }

	  public static void writeToCSV(List<GaData> gQueryData, String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		  PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		  
		  String header = "";
		  int i = 0;
	      while(i<gQueryData.get(0).getColumnHeaders().size()){
	    	  header+=gQueryData.get(0).getColumnHeaders().get(i).getName();
	    	  if(i<gQueryData.get(0).getColumnHeaders().size()-1){
	    		  header+=";";
	    	  }
	    	  i++;
	      }
	      writer.println(header);
	      
	      for(GaData gQuery:gQueryData){
	    	  i=0;      
	          while(i<gQuery.getRows().size()){
	        	  int j = 0;
	        	  String row ="";
	        	  while(j<gQuery.getColumnHeaders().size()){
	        		  row +=gQuery.getRows().get(i).get(j);
	        		  if(j<gQuery.getColumnHeaders().size()-1){
	        			  row+=";";
	        		  }
	        		  j++;
	        	  }
	        	  writer.println(row);
	        	  i++;
	          }
	      }
	      
	      
	      writer.close();
	  }
	  
	  public static void writeToJSON(List<GaData> gQueryData, String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		  PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		  int i,j = 0;
		  
		  writer.println("[");
		  
		  for(GaData gQuery:gQueryData){
			  i = 0;
			  while(i<gQuery.getRows().size()){
				  j = 0;
				  String row="{";
				  while(j<gQuery.getColumnHeaders().size()){
					  row+= "\""+gQuery.getColumnHeaders().get(j).getName() + "\": \"" + gQuery.getRows().get(i).get(j) + "\"";
					  if(j<gQuery.getColumnHeaders().size()-1){
						  row+=",";
					  }
					  j++;
				  }
				  row+="}";
				  if(i<gQuery.getRows().size()-1){
					  row+=",";
				  }
				  writer.println(row);
				  i++;
			  }
		  }		  
		  writer.println("]");
		  writer.close();
	  }
}
