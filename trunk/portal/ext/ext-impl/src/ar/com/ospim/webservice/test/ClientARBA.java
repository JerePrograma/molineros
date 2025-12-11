package ar.com.ospim.webservice.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;


public class ClientARBA {
	
	
	private static Log logger = LogFactoryUtil.getLog(ClientARBA.class);
	private String error;
	private Double alicuotaRetencion;
	private Double alicuotaPercepcion;
	private static Double alicuotaRetencionDefecto=0.00D;
	private static Double alicuotaPercepcionDefecto=0.00D;
	private static Double alicuotaRetencionError=0.0175D;
	private static Double alicuotaPercepcionError=0.03D;
	
	
	//TEST
	//private static String host="https://dfe.test.arba.gov.ar/DomicilioElectronico/SeguridadCliente/dfeServicioConsulta.do";

	
	//PRODUCCion
	private static String host="https://dfe.arba.gov.ar/DomicilioElectronico/SeguridadCliente/dfeServicioConsulta.do";
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		
	private static String strRq="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"+
		"<CONSULTA-ALICUOTA>" +
		"<fechaDesde>__fechaDde</fechaDesde>"+
		"<fechaHasta>__fechaHta</fechaHasta>"+
		"<cantidadContribuyentes>1</cantidadContribuyentes>" +
		"<contribuyentes class=\"list\">" +
		"<contribuyente>" +
		"<cuitContribuyente>__cuit</cuitContribuyente>" +
		"</contribuyente>"+
		"</contribuyentes>"+
		"</CONSULTA-ALICUOTA>";
	
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Double getAlicuotaRetencion() {
		return alicuotaRetencion;
	}

	public void setAlicuotaRetencion(Double alicuotaRetencion) {
		this.alicuotaRetencion = alicuotaRetencion;
	}

	public Double getAlicuotaPercepcion() {
		return alicuotaPercepcion;
	}

	public void setAlicuotaPercepcion(Double alicuotaPercepcion) {
		this.alicuotaPercepcion = alicuotaPercepcion;
	}

/*
	public static void main(String[] args) {
		  ClientARBA t = getAlicuota("34686233318");
          System.out.println("Error " +t.getError());
          System.out.println("Alicuota Retencion " + t.getAlicuotaRetencion());
          System.out.println("Alicuota Percepcion " + t.getAlicuotaPercepcion());
        
	}
*/	
	
	
	public static ClientARBA getAlicuota(String cuit){
			ClientARBA ret=new ClientARBA();
		   
		    String error="";
            Double alicuotaRetencion=0D;
            Double alicuotaPercepcion=0D;
		
		   try {
	           strRq=strRq.replace("__fechaDde",sdf.format(DateUtils.getFirstDateOfMonth(new Date(), false)));
	           strRq=strRq.replace("__fechaHta",sdf.format(DateUtils.getLastDateOfMonth(new Date(), false)));
	           strRq=strRq.replace("__cuit", cuit);
	           
	           String md5Hex = DigestUtils
	 			      .md5Hex(strRq).toUpperCase();
	           
	           File file = File.createTempFile("DFEServicioConsulta_"+md5Hex, ".xml");
	            // Si el archivo no existe es creado
	            //if (!file.exists()) {
	                //file.createNewFile();
	               
	            //}
	            FileWriter fw = new FileWriter(file);
	            BufferedWriter bw = new BufferedWriter(fw);
	            bw.write(strRq);
	            bw.close();
	            
	            
////////////////////////////////////////////
	            HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
	            //here should set HttpConnectionManagerParams but not important for you
	            HttpClient httpClient = new HttpClient(httpConnectionManager);

	            PostMethod postMethod = new PostMethod(host);
	            
	            FilePart filePart = new FilePart("file", file);
	            StringPart user = new StringPart("user", "30531143856", "utf-8");
	            StringPart pass = new StringPart("password", "Az28121970", "utf-8");
	            Part[] parts = { user, pass,  filePart };
	            
	            MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, postMethod.getParams());
	            postMethod.setRequestEntity(multipartRequestEntity);
	            int status = httpClient.executeMethod(postMethod);
	            System.out.println("Status: " + status);
	            String responseStr = postMethod.getResponseBodyAsString();
	            
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder;
	            dBuilder = dbFactory.newDocumentBuilder();
	            
	            Document doc = dBuilder.parse(new InputSource(new StringReader(responseStr)));
	            doc.getDocumentElement().normalize();
	            
	            
	            
	            NodeList lista = doc.getElementsByTagName("DFEError");
	            for (int temp = 0; temp < lista.getLength(); temp++) {
	                Node nodo = lista.item(temp);
	                System.out.println("Elemento:" + nodo.getNodeName());
	                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
	                    Element element = (Element) nodo;
	                    error= element.getElementsByTagName("codigoError").item(0).getTextContent();
	                }
	            }
	            
	            if(alicuotaRetencion==0D) {
	              try {	
	                 alicuotaRetencion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_RETENCION_ARBA"));
	              }catch(Exception e) {
	            	 alicuotaRetencion=alicuotaRetencionDefecto; 
	              }
	            }
	            if(alicuotaPercepcion==0D) {
	               try { 	
   	                  alicuotaPercepcion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_PERCEPCION_ARBA"));
	               }catch(Exception e) {
	            	  alicuotaPercepcion=alicuotaPercepcionDefecto; 
	               }
		        }
	            
	            ret.setError(error);
	            ret.setAlicuotaRetencion(alicuotaRetencion);
	            ret.setAlicuotaPercepcion(alicuotaPercepcion);
	            
	            System.out.println(responseStr);
	            
//////////////////////////////////////////	           
			} catch (Exception e) {
				
				
				if(alicuotaRetencion==0D) {
					try {
		              alicuotaRetencion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_RETENCION_ARBA_ERROR"));
					}catch(Exception e1) {
					  alicuotaRetencion=alicuotaRetencionError;	
					}
		        }
		        if(alicuotaPercepcion==0D) {
		        	try {
		              alicuotaPercepcion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_PERCEPCION_ARBA_ERROR"));
		        	}catch(Exception e1) {
		        	  alicuotaPercepcion=alicuotaPercepcionError; 		
		        	}
			    }
		            
		        ret.setError("Sin conexión WebService");
		        ret.setAlicuotaRetencion(alicuotaRetencion);
		        ret.setAlicuotaPercepcion(alicuotaPercepcion);
				// TODO Auto-generated catch block
				logger.error("Error al llamar a Client ARBA: " + cuit,e);
				e.printStackTrace();
			}
		   
		  return ret;
	}   
		 
	public static ClientARBA getAlicuota(String cuit,Date fecha){
		ClientARBA ret=new ClientARBA();
	   
	    String error="";
        Double alicuotaRetencion=0D;
        Double alicuotaPercepcion=0D;
	
	   try {
		    alicuotaRetencion=OrdenPagoServiceUtil.getAlicuotaARBA(cuit, fecha, "R");
            if(alicuotaRetencion<0D) {
              if(alicuotaRetencion!=-99D) {	
                try {	
                   alicuotaRetencion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_RETENCION_ARBA"));
                }catch(Exception e) {
            	   alicuotaRetencion=alicuotaRetencionDefecto; 
                }
              }else {
            	  error="Padrón de alícuotas de  ARBA desactualizado";              
              }
            }
            if(alicuotaPercepcion<0D) {
               try { 	
	                  alicuotaPercepcion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_PERCEPCION_ARBA"));
               }catch(Exception e) {
            	  alicuotaPercepcion=alicuotaPercepcionDefecto; 
               }
	        }
            
            ret.setError(error);
            ret.setAlicuotaRetencion(alicuotaRetencion);
            ret.setAlicuotaPercepcion(alicuotaPercepcion);
            
		} catch (Exception e) {
			
			
			if(alicuotaRetencion==0D) {
				try {
	              alicuotaRetencion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_RETENCION_ARBA_ERROR"));
				}catch(Exception e1) {
				  alicuotaRetencion=alicuotaRetencionError;	
				}
	        }
	        if(alicuotaPercepcion==0D) {
	        	try {
	              alicuotaPercepcion=Double.parseDouble(TraeListasServiceUtil.getSystemConfig("IIBB_PERCEPCION_ARBA_ERROR"));
	        	}catch(Exception e1) {
	        	  alicuotaPercepcion=alicuotaPercepcionError; 		
	        	}
		    }
	            
	        ret.setError("Sin conexión WebService");
	        ret.setAlicuotaRetencion(alicuotaRetencion);
	        ret.setAlicuotaPercepcion(alicuotaPercepcion);
			// TODO Auto-generated catch block
			logger.error("Error al llamar a Client ARBA: " + cuit,e);
			e.printStackTrace();
		}
	   
	  return ret;
}

}
