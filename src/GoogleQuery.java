import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GoogleQuery {
	private String QueryName;
	private List<String> Metrics = new ArrayList<String>();
	private List<String> Dimensions = new ArrayList<String>();
	private Boolean DimensionFlag;
	private Boolean SegmentFlag;
	private String Segment;
	
	
	
	public String getQueryName() {
		return QueryName;
	}

	public void setQueryName(String queryName) {
		QueryName = queryName;
	}

	public List<String> getMetrics() {
		return Metrics;
	}

	public void setMetrics(List<String> metrics) {
		Metrics = metrics;
	}

	public List<String> getDimensions() {
		return Dimensions;
	}

	public void setDimensions(List<String> dimensions) {
		Dimensions = dimensions;
	}

	public Boolean getDimensionFlag() {
		return DimensionFlag;
	}

	public void setDimensionFlag(Boolean dimensionFlag) {
		DimensionFlag = dimensionFlag;
	}

	public Boolean getSegmentFlag() {
		return SegmentFlag;
	}

	public void setSegmentFlag(Boolean segmentFlag) {
		SegmentFlag = segmentFlag;
	}

	public String getSegment() {
		return Segment;
	}

	public void setSegment(String segment) {
		Segment = segment;
	}

	public GoogleQuery(String queryName, List<String> metrics, List<String> dimensions,Boolean dimensionFlag, String segment, Boolean segmentFlag){
		this.QueryName = queryName;
		this.Metrics = metrics;
		this.Dimensions = dimensions;
		this.DimensionFlag = dimensionFlag;
		this.Segment = segment;
		this.SegmentFlag = segmentFlag;
	}
	
	public GoogleQuery() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString(){
		return "QueryName: " + this.QueryName + "\nMetrics: " + Helper.StringListToString(this.Metrics) + "\nDimensions: " + Helper.StringListToString(this.Dimensions) + "\nDimensionFlag: " +
					this.DimensionFlag + "\nSegment: " + this.Segment + "\nSegmentFlag: " + this.SegmentFlag;		
	}

	public static List<GoogleQuery> readQueriesFromXML(String fileName){
		List<GoogleQuery> gList = new ArrayList<GoogleQuery>();
		try{
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("Query");	
			
			GoogleQuery cacheQuery;
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				cacheQuery = new GoogleQuery();
				
				Node nNode = nList.item(temp);						
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					cacheQuery.QueryName = eElement.getAttribute("name");
					cacheQuery.Metrics = new ArrayList<String>(Arrays.asList(eElement.getElementsByTagName("Metrics").item(0).getTextContent().split(",")));
					cacheQuery.Dimensions = new ArrayList<String>(Arrays.asList(eElement.getElementsByTagName("Dimensions").item(0).getTextContent().split(",")));
					cacheQuery.DimensionFlag = Boolean.parseBoolean(eElement.getElementsByTagName("DimensionFlag").item(0).getTextContent());
					cacheQuery.Segment = eElement.getElementsByTagName("Segment").item(0).getTextContent();
					cacheQuery.SegmentFlag = Boolean.parseBoolean(eElement.getElementsByTagName("SegmentFlag").item(0).getTextContent());
					
					if(cacheQuery.Dimensions.get(0) ==""){
						cacheQuery.Dimensions = null;
					}
					if(cacheQuery.Segment ==""){
						cacheQuery.Segment = null;
					}
					gList.add(cacheQuery);
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}		
		return gList;		
	}
}
