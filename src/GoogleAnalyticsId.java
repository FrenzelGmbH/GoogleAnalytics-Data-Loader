import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GoogleAnalyticsId {
	private String ID;
	private String ShortName;
	
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getShortName() {
		return ShortName;
	}

	public void setShortName(String shortName) {
		ShortName = shortName;
	}

	public GoogleAnalyticsId(String id, String shortName){
		this.ID = id;
		this.ShortName = shortName;
	}
	
	public GoogleAnalyticsId() {
		// TODO Auto-generated constructor stub
	}

	public static List<GoogleAnalyticsId> ReadIdsFromXml(String filename){
		List<GoogleAnalyticsId> gaList = new ArrayList<GoogleAnalyticsId>();
		
		try{
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("GoogleID");			
			
			GoogleAnalyticsId cacheId;
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				cacheId = new GoogleAnalyticsId();				
				Node nNode = nList.item(temp);						
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					cacheId.ID = eElement.getElementsByTagName("Id").item(0).getTextContent();
					cacheId.ShortName = eElement.getElementsByTagName("ShortName").item(0).getTextContent();
					
					gaList.add(cacheId);
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}		
		return gaList;
	}
}
