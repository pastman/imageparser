package com.mpastor.app.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLUtils {

	private static Logger logger = LoggerFactory.getLogger(XMLUtils.class);
	
	private static final String BAG_XML_NODE = "rdf:Bag";
	
	private XMLUtils(){
	}
	
	public static List<String> extractMetadataFromXML(String xmlFilePath) {
		
		List<String> metadataLst = new ArrayList<String>();
		
		File xmlFile = new File(xmlFilePath);
	    Document doc = null;
	    
	    try {
	
	    	logger.info("***** Se procede a leer el fichero {}", xmlFilePath);
	    	
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	
	    	doc = dBuilder.parse(xmlFile);
	    	doc.getDocumentElement().normalize();
	    	
	    	//NodeList nList = doc.getDocumentElement().getElementsByTagName(BAG_XML_NODE);
	    	
	    	
	        NodeList bagNodeLst = doc.getElementsByTagName(BAG_XML_NODE);
	        
	        // Iteramos por todos los tag rdf:Bag encontrados en el xml
	        for (int i=0; i < bagNodeLst.getLength(); i++) {

	            Node nNode = bagNodeLst.item(i);

	            NodeList bagNodeChildLst = nNode.getChildNodes();
	            
	            for (int j=0; j < bagNodeChildLst.getLength(); j++) {
	            
	            	Node nChildNode = bagNodeChildLst.item(j);
	            	
	            	if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {

		                Element eElement = (Element) nChildNode;
		                
		                logger.info("Atributo encontrado bajo el nodo 'rdf:Bag' : {}", eElement.getTextContent());
		                
		                metadataLst.add(eElement.getTextContent());
		            }	
	            }
	        }

	    } catch (Exception e) {
	    	logger.error("Se ha producido un error al leer el fichero {} Error : {}", xmlFilePath, e.getMessage());
	    }
	    
	    return metadataLst;
	}
	
}