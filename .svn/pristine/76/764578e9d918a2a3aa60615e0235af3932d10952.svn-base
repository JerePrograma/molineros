package ar.com.ospim.webservice.xmlparser;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ar.com.ospim.webservice.omint.Beneficiario;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class OmintResponseXMLParser {

	private static Log logger = LogFactoryUtil.getLog(OmintResponseXMLParser.class );
	
	/**
	 * 
	 * @param xml
	 * @return String[] 
	 *  		     [0] TransactionId, [1] MessageCode, [2] MessageDescription
	 *  Si el TransactionId es > 0 se realizo correctamente la transaccion.
	 *  Si es TransactionId es = 0 entonces se informará el codigo de error y descripcion del error.
 	 */
	
	public String[] parsearResponseXML(String xml){
//		String xml = "<DataCommonResponse xmlns='http://tempuri.org/DataCommonResponse.xsd'><DataCommonResponse><TrasactionId>194</TrasactionId><MessageCode>0</MessageCode><MessageDescription/></DataCommonResponse></DataCommonResponse>";
		
		logger.info("Mensaje de Omint a parsear: " + xml);
			
		String[] respuesta = null;
	   
	    Document XMLDoc = null;
		try {
			XMLDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (SAXException e) {
			logger.error(e);
			return respuesta;
		} catch (IOException e) {
			logger.error(e);
			return respuesta;
		} catch (ParserConfigurationException e) {
			logger.error(e);
			return respuesta;
		}
	    
	    XMLDoc.getDocumentElement().normalize();
	
//	  System.out.println("El elemento raíz es: " + XMLDoc.getDocumentElement().getNodeName());
	    NodeList nodos = XMLDoc.getElementsByTagName("DataCommonResponse");
	
	    for (int i = 0; i < nodos.getLength(); i ++) {
	    	i++; // xq viene repetido el tag DataCommonResponse  
	    	Node nodito = nodos.item(i);
	
	    	if (nodito.getNodeType() == Node.ELEMENT_NODE) {
	
	    		Element elemento = (Element) nodito;
	      
//	    		logger.debug("TrasactionId : " + getTagValue("TrasactionId", elemento));
//	    		logger.debug("MessageCode : " + getTagValue("MessageCode", elemento));
//	    		logger.debug("MessageDescription : " + getTagValue("MessageDescription", elemento));
	    		
	    		respuesta = new String[3];
	    		respuesta[0] = getTagValue("TrasactionId", elemento);
	    		respuesta[1] = getTagValue("MessageCode", elemento);
	    		respuesta[2] = getTagValue("MessageDescription", elemento);
	    	}
	    }	  	 
	  	return respuesta;
	}
	
	private String getTagValue(String sTag, Element eElement){
		
 		  String valor = "";
 		  NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
 		  Node nValue = (Node) nlList.item(0);

 		  try{
 			  valor = nValue.getNodeValue();
 		  }catch (NullPointerException e) {
			 valor=null ;
 		  }
 		 return valor;

 	}
	
//	private static MessageElement[] convertXMLStringtoMessageElement(String xmlString) throws SAXException, IOException, ParserConfigurationException{
//        MessageElement[] m = new MessageElement[1];
//        Document XMLDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));
//        Element element = XMLDoc.getDocumentElement();
//        m[0] = new MessageElement(element);
//        return m;
//    }
	public MessageElement[] generarXMLGrupoFamiliar(List<Beneficiario> integrantes, String company, Date fechaVig, String planMedico){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		MessageElement[] mensaje = new MessageElement[1];
//		String company = "2";
//		Date fechaVig = new Date();
//		String planMedico = "OSPIM_0";
		
		try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("TransactionData");
				doc.appendChild(rootElement);
		 
				// *****************
				// cabecera elements
				// *****************
				Element cabecera = doc.createElement("Cabecera");
				rootElement.appendChild(cabecera);
				// compania element
						Element compania = doc.createElement("Compania");
						compania.appendChild(doc.createTextNode( company ));
						cabecera.appendChild(compania);
				// fecVig element
						Element fecVig = doc.createElement("FecVig");
						fecVig.appendChild(doc.createTextNode( sdf.format(fechaVig)  ));
						cabecera.appendChild(fecVig);				
				// planMed element
						Element planMed = doc.createElement("PlanMed");
						planMed.appendChild(doc.createTextNode( planMedico ));
						cabecera.appendChild(planMed);				
				// *****************
				// detalle elements
				// *****************
				
				// detalle elements
				
				for (Iterator<Beneficiario> iterator = integrantes.iterator(); iterator.hasNext();) {
					Beneficiario ben = iterator.next();
					
					Element detalle = doc.createElement("Detalle");
					rootElement.appendChild(detalle);
					
					// Apellido element
					Element apellido = doc.createElement("Apellido");
					apellido.appendChild(doc.createTextNode( ben.getApellido() ));
					detalle.appendChild(apellido);				
					// Nombre element
					Element nombre = doc.createElement("Nombre");
					nombre.appendChild(doc.createTextNode( ben.getNombre() ));
					detalle.appendChild(nombre);	
					// Parentesco element
					Element parent = doc.createElement("Parentesco");
					parent.appendChild(doc.createTextNode( ben.getParentesco() ));
					detalle.appendChild(parent);
					// Sexo element
					Element sexo = doc.createElement("Sexo");
					sexo.appendChild(doc.createTextNode( ben.getSexo()));
					detalle.appendChild(sexo);
					// Fecha Nac element
					Element fecNac = doc.createElement("FecNac");
					fecNac.appendChild(doc.createTextNode( sdf.format(ben.getFecNac().getTime()) ));
					detalle.appendChild(fecNac);
					// calle element
					Element calle = doc.createElement("Calle");
					calle.appendChild(doc.createTextNode( ben.getCalle() ));
					detalle.appendChild(calle);
					// NroCalle element
					Element nroCalle = doc.createElement("NroCalle");
					nroCalle.appendChild(doc.createTextNode( ben.getNroCalle() ));
					detalle.appendChild(nroCalle);
					// Resto element
					Element resto = doc.createElement("Resto");
					resto.appendChild(doc.createTextNode( ben.getResto() ));
					detalle.appendChild(resto);
					// Localidad element
					Element localidad = doc.createElement("Localidad");
					localidad.appendChild(doc.createTextNode( ben.getLocalidad() ));
					detalle.appendChild(localidad);
					// CP element
					Element cp = doc.createElement("CP");
					cp.appendChild(doc.createTextNode( ben.getCP() ));
					detalle.appendChild(cp);
					// Provincia element
					Element prov = doc.createElement("Provincia");
					prov.appendChild(doc.createTextNode( ben.getProvincia() ));
					detalle.appendChild(prov);
					// Telefono element
					Element tel = doc.createElement("Telefono");
					tel.appendChild(doc.createTextNode( ben.getTelefono() ));
					detalle.appendChild(tel);
					// TipoDoc element
					Element tdoc = doc.createElement("TipoDoc");
					tdoc.appendChild(doc.createTextNode( ben.getTipoDoc() ));
					detalle.appendChild(tdoc);
					// NroDoc element
					Element nrodoc = doc.createElement("NroDoc");
					nrodoc.appendChild(doc.createTextNode( ben.getNroDoc() ));
					detalle.appendChild(nrodoc);
					// Seccional element
					Element secc = doc.createElement("Seccional");
					secc.appendChild(doc.createTextNode( ben.getSeccional() ));
					detalle.appendChild(secc);
					// Categoria element
					Element cat = doc.createElement("Categoria");
					cat.appendChild(doc.createTextNode( String.valueOf(ben.getCategoria()) ));
					detalle.appendChild(cat);
					// Cuil element
					Element cuil = doc.createElement("CUIL");
					cuil.appendChild(doc.createTextNode( ben.getCUIL() ));
					detalle.appendChild(cuil);
					// FPP element
					if(ben.getFPP() != null){
						Element fpp = doc.createElement("FPP");
						fpp.appendChild(doc.createTextNode( sdf.format(ben.getFPP().getTime()) ));
						detalle.appendChild(fpp);
					}
					// Inte element
					Element inte = doc.createElement("NroIntegrante");
					inte.appendChild(doc.createTextNode( String.valueOf(ben.getNroIntegrante()) ));
					detalle.appendChild(inte);	
					// Nacionalidad element
					Element nac = doc.createElement("Nacionalidad");
					nac.appendChild(doc.createTextNode( String.valueOf(ben.getNacionalidad()) ));
					detalle.appendChild(nac);	
					// EstadoCivil element
					Element ec = doc.createElement("EstadoCivil");
					ec.appendChild(doc.createTextNode( String.valueOf(ben.getEstadoCivil()) ));
					detalle.appendChild(ec);
					// Discapacidad element
					Element d = doc.createElement("Discapacidad");
					d.appendChild(doc.createTextNode( ben.getDiscapacidad() ));
					detalle.appendChild(d);
					
				}
				mensaje[0] = new MessageElement(doc.getDocumentElement() );
				
//				ya esta en otro metodo...
//				try {
//					logger.debug(mensaje[0].getAsString());
//				} catch (Exception e) {
//					//nada...
//				}
			  } catch (ParserConfigurationException pce) {
				return null;
			  }
		
		return mensaje;
	}
	
}
