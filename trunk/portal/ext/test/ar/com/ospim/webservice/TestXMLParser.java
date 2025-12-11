package ar.com.ospim.webservice;

//import java.io.File;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.DocumentBuilder;
//import org.w3c.dom.Document;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.Node;
//import org.w3c.dom.Element;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.parsers.*;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestXMLParser {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws SAXException, IOException, Exception {
		
		String xml = "<DataCommonResponse xmlns='http://tempuri.org/DataCommonResponse.xsd'><DataCommonResponse><TrasactionId>194</TrasactionId><MessageCode>0</MessageCode><MessageDescription/></DataCommonResponse></DataCommonResponse>";
			
        MessageElement[] m = new MessageElement[1];
        Document XMLDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        
        XMLDoc.getDocumentElement().normalize();

	  	  System.out.println("El elemento raíz es: " + XMLDoc.getDocumentElement().getNodeName());
	  	  NodeList listaPersonas = XMLDoc.getElementsByTagName("DataCommonResponse");
	
	  	  for (int i = 0; i < listaPersonas.getLength(); i ++) {
	  		i++; // xq viene repetido el tag DataCommonResponse  
	  	    Node persona = listaPersonas.item(i);
	  	    System.out.println("Pasada " + i);
	  	    if (persona.getNodeType() == Node.ELEMENT_NODE) {
	
	              Element elemento = (Element) persona;
	              
	              System.out.println("TrasactionId : " + getTagValue("TrasactionId", elemento));
	              System.out.println("MessageCode : " + getTagValue("MessageCode", elemento));
	              System.out.println("MessageDescription : " + getTagValue("MessageDescription", elemento));
	
	  	    }
	  	  } 
        
//        Element element = XMLDoc.getDocumentElement();
//        m[0] = new MessageElement(element);
//		
//        
//        for (Iterator<Element> iterator = m[0].getChildElements(); iterator.hasNext();) {
//			Element e =  iterator.next();
//			System.out.println("Nodo Root: " +e.getNodeName());
//			
//			for(int i=0; i < e.getChildNodes().getLength(); i++){
//				Node n = e.getChildNodes().item(i);
//				System.out.print(n.getNodeName());
//				System.out.print(" = ");
//				if(n.getChildNodes().getLength()  > 0 )
//				System.out.print(n.getChildNodes().item(0).toString());
//				System.out.println("");
//			}
////			
////			for (NodeList iterator1 = ; iterator.hasNext();) {
////				NodeList n1 =  iterator1.next();
////				System.out.println(n1.item(0). getNodeName());
////				
////			}   
//		}    
		
	}

	  	private static String getTagValue(String sTag, Element eElement)
	  	 {
	  		  String valor = "";
	  		  NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	  		  Node nValue = (Node) nlList.item(0);

	  		  try{
	  			  valor = nValue.getNodeValue();
	  		  }catch (NullPointerException e) {
				
			}
	  		 return valor;

	  	 }

	
}
