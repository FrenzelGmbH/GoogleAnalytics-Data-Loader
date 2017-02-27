import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Config {
	private String keyFilePath;
	private String serviceAccount;
	
	
	public String getKeyFilePath() {
		return keyFilePath;
	}

	public void setKeyFilePath(String keyFilePath) {
		this.keyFilePath = keyFilePath;
	}

	public String getServiceAccount() {
		return serviceAccount;
	}

	public void setServiceAccount(String serviceAccount) {
		this.serviceAccount = serviceAccount;
	}

	public Config(String keyFile, String serviceAccountEmail){
		this.keyFilePath = keyFile;
		this.serviceAccount = serviceAccountEmail;
	}
	
	public Config() {
		// TODO Auto-generated constructor stub
	}

	public static Config ReadConfigFromXML(String filename){
		Config config = new Config();
		
		try{
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("Config");		
			
			for (int temp = 0; temp < nList.getLength(); temp++) {			
				Node nNode = nList.item(temp);						
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					config.serviceAccount = eElement.getElementsByTagName("ServiceAccount").item(0).getTextContent();
					config.keyFilePath = eElement.getElementsByTagName("KeyFilePath").item(0).getTextContent();					
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}		
		return config;
	}
}
