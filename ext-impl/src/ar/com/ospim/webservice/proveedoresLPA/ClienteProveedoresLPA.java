package ar.com.ospim.webservice.proveedoresLPA;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import sun.misc.BASE64Decoder;


public class ClienteProveedoresLPA {

	private static final String CONFIG_HOST = "PROVEEDORES_LPA_HOST";
	private static final String CONFIG_USER = "PROVEEDORES_LPA_USER";
	private static final String CONFIG_PASSWORD = "PROVEEDORES_LPA_PASSWORD";
	private static Log logger = LogFactoryUtil.getLog(ClienteProveedoresLPA.class);
	private String error;

	private static String getRequiredSystemConfig(String key) {
		String value = TraeListasServiceUtil.getSystemConfig(key);

		if (value == null || value.trim().length() == 0) {
			throw new IllegalStateException("Falta configuracion requerida: " + key);
		}

		return value;
	}

	private static String getHost() {
		return getRequiredSystemConfig(CONFIG_HOST);
	}

	private static String getAuthHeader() {
		String auth = getRequiredSystemConfig(CONFIG_USER) + ":"
				+ getRequiredSystemConfig(CONFIG_PASSWORD);
		byte[] encodedAuth = Base64.getEncoder().encode(
				auth.getBytes(StandardCharsets.ISO_8859_1));

		return "Basic " + new String(encodedAuth, StandardCharsets.ISO_8859_1);
	}
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static void getComprobantes(List<Comprobante>cpbtes) throws Exception {
		List<File> fs = new ArrayList<File>();
		try {
			getComprobanteArchivos(cpbtes,"archivo");
		} catch (UnsupportedEncodingException e) {
			logger.debug("Error WS LPA" + e);
		}
	}
	
	public static void getAdjuntos(List<Comprobante>cpbtes) throws Exception {
		List<File> fs = new ArrayList<File>();
		try {
			getComprobanteArchivos(cpbtes,"adjuntos");
		} catch (UnsupportedEncodingException e) {
			logger.debug("Error WS LPA" + e);
		}
	}
	
	
    private static void getComprobanteArchivos(List<Comprobante>cpbtes,String opcion) throws Exception{
		List<Comprobante>cs=new ArrayList<Comprobante>();
		String endPoint= "api/v1/comprobante/read/";
//        String params= "__tipo/__puntoVta/__idCpbte/__cuit/"+opcion ;
		String params= "__idCpbte/"+opcion ;
		String tipo="";
		
		User user= UserLocalServiceUtil.getUserByScreenName(10112, "liquidaciones");
		
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setUserId(user.getUserId());
		serviceContext.setAddGuestPermissions(true);
		
		PrincipalThreadLocal.setName(user.getScreenName());
		PermissionChecker permissionChecker =PermissionCheckerFactoryUtil.create(user, true);
		PermissionThreadLocal.setPermissionChecker(permissionChecker);
			
		DLFolder f = DLFolderServiceUtil.getFolder(10136, 0L, "Comprobantes");
		long folderId = f.getFolderId();
		
		Random rnd = new Random();
		String extension="";
		
		for(Comprobante ix:cpbtes) {
			String p = params;
			//p= p.replace("__idCpbte", ix.getNroComprobante());
			
			p= p.replace("__idCpbte", ix.getId().toString());
			
			if("FCP".equals(ix.getTipoComprobante())&& "B".equals(ix.getLetraComprobante()) ) {
				tipo="Factura B";
	        }else if("FCP".equals(ix.getTipoComprobante())&& "C".equals(ix.getLetraComprobante()) ) {
	        	tipo="Factura C";
	        }else if("NCR".equals(ix.getTipoComprobante())&& "B".equals(ix.getLetraComprobante()) ) {
	        	tipo="Nota de Crédito B";
	        }else if("NCR".equals(ix.getTipoComprobante())&& "C".equals(ix.getLetraComprobante()) ) {
	        	tipo="Nota de Crédito C";
	        }else if("RCB".equals(ix.getTipoComprobante())&& "C".equals(ix.getLetraComprobante()) ) {
	        	tipo="Recibo C";
	        }
			
			String idFacturaImg = ix.getAcreedorEmpresa().getCuit()+"-"+ix.getTipoComprobante()+"-"+
					ix.getLetraComprobante()+String.format("%05d",ix.getPtoVenta())+ix.getNroComprobante();
			
			HttpClient httpclient = new HttpClient();
			String responseBodyAsString;
			String url =getHost() +endPoint+p;
			GetMethod httpGet = new GetMethod(url);
			httpGet.addRequestHeader("Authorization", getAuthHeader());
			httpGet.addRequestHeader("accept", "application/json");
			httpGet.addRequestHeader("content-type", "application/json");
			httpGet.addRequestHeader("User-Agent","telnet");
			
			try {
				httpclient.executeMethod(httpGet);
				responseBodyAsString = httpGet.getResponseBodyAsString();
				int statusCode = httpGet.getStatusLine().getStatusCode();
				if(statusCode==200) {
					JSONObject results = new JSONObject(responseBodyAsString);
					try {
					  Integer code =results.getInt("code");
					  logger.debug("Bajada de Archivos WS LPA" + responseBodyAsString);
					}catch(Exception e21) {
						
						JSONArray items = results.getJSONArray("comprobante");
						for (int i = 0; i < items.length(); i++) {
						    try {
						    	String title="";
						    	JSONObject j = items.getJSONObject(i);
						    						    	
						    	String[] vArchivo=j.getString("nombre").split("\\.");
						    	File file =  File.createTempFile(vArchivo[0],vArchivo[1]); 
						    	if(vArchivo!=null && vArchivo.length>1) extension=vArchivo[1];
						    	
						    	FileOutputStream fop = new FileOutputStream(file);
						    	
						    	BASE64Decoder decoder = new BASE64Decoder();
						    	byte[] decodedBytes = decoder.decodeBuffer(j.getString("adjunto"));

						    	fop.write(decodedBytes);
						    	fop.flush();
						    	fop.close();
						    	
						    	DLFileEntry dl=null;
						      	do {
						      		title=idFacturaImg +"-" + ("archivo".equals(opcion)?0:(int)(rnd.nextDouble()*100));
						      		try{
						      		   dl=null;
						      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title + (extension.length()>0?".":"") + extension);
						      		} catch(Exception e2){}   
						      	} while (dl!=null);  
						    		
						      	DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, j.getString("nombre"),
						      			j.getString("nombre"), title, j.getString("nombre"), "", file, serviceContext);
						    }catch(Exception e1) {
						    	logger.debug("Error WS LPA "+e1);
						    }
						}    
					}
				}
			}catch(Exception e) {
				logger.debug("Error WS LPA "+e);
			}
		}
	}
    
    
    
    public static List<Comprobante> getComprobantesByEstado(String status,Date fechaIni,Date fechaFin) throws UnsupportedEncodingException{
		List<Comprobante>cs=new ArrayList<Comprobante>();
		String endPoint= "api/v1/comprobante/read/status/";
		String params= HttpUtil.encodeURL(status,true) +"/range/"+sdf.format(fechaIni)+"/"+sdf.format(fechaFin);
		HttpClient httpclient = new HttpClient();
		String responseBodyAsString;
		GetMethod httpGet = new GetMethod( getHost() +endPoint+params);
		
		httpGet.addRequestHeader("Authorization", getAuthHeader());
		httpGet.addRequestHeader("accept", "application/json");
		httpGet.addRequestHeader("content-type", "application/json");
		httpGet.addRequestHeader("User-Agent","telnet");
		
		try {
			httpclient.executeMethod(httpGet);
			responseBodyAsString = httpGet.getResponseBodyAsString();
			int statusCode = httpGet.getStatusLine().getStatusCode();
			if(statusCode==200) {
				JSONObject results = new JSONObject(responseBodyAsString);
				try {
				  Integer code =results.getInt("code");
				}catch(Exception e) {
					JSONArray items = results.getJSONArray("comprobantes");
					for (int i = 0; i < items.length(); i++) {
					    try {
					    	Comprobante c = new Comprobante();
					        JSONObject j = items.getJSONObject(i);
					        String numero = j.getString("nroComprobante");
					        Integer id =j.getInt("idComprobante");
					        String fechaAlta= j.getString("fechaAlta");
					        String usuarioAlta=j.getString("usuarioAlta");
					        String fechaEmision=j.getString("fechaEmision");
					        String fechaVto=j.getString("fechaVencimiento");
					        String estado=j.getString("estado");
					        String tipoComprobante=j.getString("tipoComprobante");
					        String razonSoc=j.getString("proveedor");
					        String cuit=j.getString("cuit");
					        Double importe=j.getDouble("importe");
					        String comentario=j.getString("comentario");
					        String ptoVta=j.getString("puntoVenta");
					        
					        String dni=j.getString("DNI");
					        String codPrestacion=j.getString("codigoPrestacion");
					        String desPrestacion=j.getString("prestacion");
					        String periodoPrestacion=j.getString("periodoPrestacion");
					        String areaLiquidacion=j.getString("areaLiquidacion");
					        String cae=j.getString("CAE");
					        String observacion=j.getString("observacion");
					        Integer cantidad = 0;
					        try {
					        	cantidad=j.getInt("cantidad");
					        }catch(Exception e4) {}
					        
					        
					        try {
					          Afiliado a = new Afiliado();
					          if(dni!=null && !"null".equalsIgnoreCase(dni)) {
					             a.setDocu_numero(dni);
					          }   
					          c.setAfiliado(a);
					          
					          if(codPrestacion!=null && !"null".equalsIgnoreCase(codPrestacion)) {
					             c.setCodigoPrestacion(codPrestacion);
					          }
					          
					          if(desPrestacion!=null && !"null".equalsIgnoreCase(desPrestacion)) {
					             c.setDescripcionPrestacion(desPrestacion);
					          }   
					          c.setPeriodoPrestacion(sdf1.parse(periodoPrestacion));
					          c.setCae(cae);
					        }catch(Exception e2) {}
					        
					        c.setSectorDestino(areaLiquidacion);
					        if(observacion!=null && !"null".equalsIgnoreCase(observacion)) {
					          c.setObservaciones(observacion);
					        }  
					        
					        String tipo="";
					        String letra="";
					        if("Factura B".equalsIgnoreCase(tipoComprobante)) {
					        	tipo="FCP";
					        	letra="B";
					        }else if("Factura C".equalsIgnoreCase(tipoComprobante)) {
					        	tipo="FCP";
					        	letra="C";
					        }else if("Factura A".equalsIgnoreCase(tipoComprobante)) {
					        	tipo="FCP";
					        	letra="A";	
					        }else if("Nota de Crédito B".equalsIgnoreCase(tipoComprobante)) {
						tipo="NCR";
					        	letra="B";
					        }else if("Nota de Crédito C".equalsIgnoreCase(tipoComprobante)) {
						tipo="NCR";
					        	letra="C";
					        }else if("Recibo C".equalsIgnoreCase(tipoComprobante)) {
					        	tipo="RCB";
					        	letra="C";
					        }
					        
					        c.setTipoComprobante(tipo);
					        c.setLetraComprobante(letra);
					        c.setNroComprobante(numero);
					        c.setPtoVenta(Integer.parseInt(ptoVta));
					        c.setId(id);
					        c.setAlta_usr(usuarioAlta);
					        c.setAlta_fecha(sdf1.parse(fechaAlta));
					        c.setFechaEmision(sdf1.parse(fechaEmision));
					        if(fechaVto!=null) {
					           c.setFechaVencimiento(sdf1.parse(fechaVto));
					        }   
					        c.setEstado(estado);
					        c.setComentario(comentario);
					        
					        Empresa empresa = new Empresa();
					        empresa.setCuit(cuit);
					        empresa.setRazon_soc(razonSoc);
					        c.setAcreedorEmpresa(empresa);
					        
					        c.setSectorDestino(areaLiquidacion);
					        
					        c.setImporteComprobante(BigDecimal.valueOf(importe));
					        
					        c.setEntidad("O");
					        
					        c.setCantidad(cantidad);
					        
					        try {
					        	 Integer codPrestador=j.getInt("codigoPrestador");
					        	c.setIdPrestador(codPrestador);
					        }catch(Exception e3) {
					        	
					        }
					        
					        cs.add(c);
					    } catch (JSONException e1) {
					    	logger.debug("Error WS LPA" + e1);
					    }
					}
				}
			}else {
			}
			
		} catch (Exception e) {
			logger.debug("Error WS LPA" + e);
		}
		return cs;
	}


    public static List<Empresa> getUsuariosRegistrados(){
		List<Empresa>cs=new ArrayList<Empresa>();
		String endPoint= "api/v1/usuario/read/";
		String params= "";
		
		HttpClient httpclient = new HttpClient();
		String responseBodyAsString;
		GetMethod httpGet = new GetMethod(getHost() +endPoint+params);
		
		httpGet.addRequestHeader("Authorization", getAuthHeader());
		httpGet.addRequestHeader("accept", "application/json");
		httpGet.addRequestHeader("content-type", "application/json");
		httpGet.addRequestHeader("User-Agent","telnet");
		
		try {
			httpclient.executeMethod(httpGet);
			responseBodyAsString = httpGet.getResponseBodyAsString();
			int statusCode = httpGet.getStatusLine().getStatusCode();
			if(statusCode==200) {
				JSONObject results = new JSONObject(responseBodyAsString);
				try {
				  Integer code =results.getInt("code");
				}catch(Exception e) {
					JSONArray items = results.getJSONArray("comprobantes");
					for (int i = 0; i < items.length(); i++) {
					    try {
					    	
					    	Empresa c = new Empresa();
					        JSONObject j = items.getJSONObject(i);
					       } catch (JSONException e1) {
					    	logger.debug("Error WS LPA" + e1);
					    }
					}
				}
			}else {
			}
			
		} catch (Exception e) {
			logger.debug("Error WS LPA" + e);
		}
		return cs;
	}


    public static String setOrdenPago(Integer id,Integer idOP,Calendar fechaOP){
    	
    	SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String endPoint= "api/v1/comprobante/UploadOP";
		
		String rta="";
		
	    String ff=sdf.format(fechaOP.getTime());
		
		HttpClient httpclient = new HttpClient();
		String responseBodyAsString;
		//Produccion
		PostMethod httpPost = new PostMethod(getHost() +endPoint);
		
		//QA
		
		httpPost.addRequestHeader("Authorization", getAuthHeader());
		httpPost.addRequestHeader("accept", "application/json");
		httpPost.addRequestHeader("content-type", "application/json");
		httpPost.addRequestHeader("User-Agent","telnet");
		
		int statusCode=0;
		
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("nroOP",idOP);
			 json.put("fechaOP",ff);
			
			StringRequestEntity requestEntity = new StringRequestEntity(
					  json.toString(),
					  "application/json",
					  "UTF-8");

			httpPost.setRequestEntity(requestEntity);
			
			httpclient.executeMethod(httpPost);
			responseBodyAsString = httpPost.getResponseBodyAsString();
			statusCode = httpPost.getStatusLine().getStatusCode();
			if(statusCode==200) {
				rta="OK";
				logger.debug("Comprobante OK "+ json.toString() + " ---> "+ rta);
			}else {
				rta= statusCode+ " "+responseBodyAsString!=null?responseBodyAsString:"";
				logger.debug("Comprobante Error "+ json.toString() + " ---> "+ rta);
			}
			
		} catch (Exception e) {
			logger.debug("Error WS LPA --Seteo OP" + e);
		}
		return rta;
	}

    
public static String setOrdenPagoWithPDF(Integer id,Integer idOP,Calendar fechaOP){
    	
    	SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String endPoint= "api/v1/comprobante/UploadOPWithFile";
		
		String rta="";
		
	    String ff=sdf.format(fechaOP.getTime());
		
		HttpClient httpclient = new HttpClient();
		String responseBodyAsString;
		//Produccion
		PostMethod httpPost = new PostMethod(getHost() +endPoint);
		
		//QA
		
		httpPost.addRequestHeader("Authorization", getAuthHeader());
		httpPost.addRequestHeader("accept", "application/json");
		httpPost.addRequestHeader("content-type", "multipart/form-data");
		httpPost.addRequestHeader("User-Agent","telnet");
		
		int statusCode=0;
		
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("nroOP",idOP);
			json.put("fechaOP",ff);
			
			PdfServlet pdfServlet=new PdfServlet();
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("P_INI", String.valueOf(idOP));
			hm.put("P_FIN", String.valueOf(idOP));
			hm.put("SUBREPORT_DIR", "jasper/orden_pago/");
			
			String fileName = "OrdenPago_OSPIM_"+String.valueOf(idOP);
			byte[] pdfOp=pdfServlet.crearPdfComoAdjunto(PdfServlet.ORDEN_PAGO_OSPIM_INTEGRACION, hm, fileName+".pdf");
			
			File file = File.createTempFile(fileName,".pdf");
			FileOutputStream fw = new FileOutputStream(file);
			BufferedOutputStream bw = new BufferedOutputStream(fw);
            bw.write(pdfOp);
            bw.close();
			bw.flush();	
            
            FilePart filePart = new FilePart("pdf", file, "application/pdf", null);  
            StringPart data = new StringPart("data", json.toString(), "UTF-8");
            Part[] parts = { data,  filePart };
            
            MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, httpPost.getParams());
            
            //
            ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
            multipartRequestEntity.writeRequest(requestContent);
            httpPost.setRequestHeader("content-type", multipartRequestEntity.getContentType());
            //            
            httpPost.setRequestEntity(multipartRequestEntity);
           
			httpclient.executeMethod(httpPost);
			responseBodyAsString = httpPost.getResponseBodyAsString();
			statusCode = httpPost.getStatusLine().getStatusCode();
			if(statusCode==200) {
				rta="OK";
				logger.debug("Comprobante OK with PDF"+ json.toString() + " ---> "+ rta);
			}else {
				rta= statusCode+ " "+responseBodyAsString!=null? statusCode+responseBodyAsString:"";
				logger.debug("Comprobante Error With PDF "+ json.toString() + " ---> "+ rta+ 
					//	" REQUEST: " +" --> " + (requestContent.toString()) +
						" RESPONSE: "+" --> "+responseBodyAsString
						);
			}
			
		} catch (Exception e) {
			logger.debug("Error WS LPA --Seteo OP With PDF" + e);
		}
		return rta;
	}

    
   public static void getComprobanteRecibo(List<Comprobante>cpbtes) throws Exception{
	List<Comprobante>cs=new ArrayList<Comprobante>();
	String endPoint= "api/v1/comprobante/read/";
	String params= "__idCpbte/adjuntos" ;
	String tipo="";
	
	User user= UserLocalServiceUtil.getUserByScreenName(10112, "liquidaciones");
	
	ServiceContext serviceContext = new ServiceContext();
	serviceContext.setUserId(user.getUserId());
	serviceContext.setAddGuestPermissions(true);
	
	PrincipalThreadLocal.setName(user.getScreenName());
	PermissionChecker permissionChecker =PermissionCheckerFactoryUtil.create(user, true);
	PermissionThreadLocal.setPermissionChecker(permissionChecker);
		
	DLFolder f = DLFolderServiceUtil.getFolder(10136, 0L, "Comprobantes");
	long folderId = f.getFolderId();
	
	Random rnd = new Random();
	String extension="";
	
	for(Comprobante ix:cpbtes) {
		String p = params;
		p= p.replace("__idCpbte", ix.getId().toString());
		
		if("FCP".equals(ix.getTipoComprobante())&& "B".equals(ix.getLetraComprobante()) ) {
			tipo="Factura B";
        }else if("FCP".equals(ix.getTipoComprobante())&& "C".equals(ix.getLetraComprobante()) ) {
        	tipo="Factura C";
        }else if("NCR".equals(ix.getTipoComprobante())&& "B".equals(ix.getLetraComprobante()) ) {
        	tipo="Nota de Crédito B";
        }else if("NCR".equals(ix.getTipoComprobante())&& "C".equals(ix.getLetraComprobante()) ) {
        	tipo="Nota de Crédito C";
        }else if("RCB".equals(ix.getTipoComprobante())&& "C".equals(ix.getLetraComprobante()) ) {
        	tipo="Recibo C";
        }
		
		String idFacturaImg = ix.getAcreedorEmpresa().getCuit()+"-"+ix.getTipoComprobante()+"-"+
				ix.getLetraComprobante()+String.format("%05d",ix.getPtoVenta())+ix.getNroComprobante();
		
		HttpClient httpclient = new HttpClient();
		String responseBodyAsString;
		String url =getHost() +endPoint+p;
		GetMethod httpGet = new GetMethod(url);
		httpGet.addRequestHeader("Authorization", getAuthHeader());
		httpGet.addRequestHeader("accept", "application/json");
		httpGet.addRequestHeader("content-type", "application/json");
		httpGet.addRequestHeader("User-Agent","telnet");
		
		try {
			httpclient.executeMethod(httpGet);
			responseBodyAsString = httpGet.getResponseBodyAsString();
			int statusCode = httpGet.getStatusLine().getStatusCode();
			if(statusCode==200) {
				JSONObject results = new JSONObject(responseBodyAsString);
				try {
				  Integer code =results.getInt("code");
				  logger.debug("Bajada de Archivos WS LPA" + responseBodyAsString);
				}catch(Exception e21) {
					
					JSONArray items = results.getJSONArray("recibo");
					for (int i = 0; i < items.length(); i++) {
					    try {
					    	String title="";
					    	JSONObject j = items.getJSONObject(i);
					    						    	
					    	String[] vArchivo=j.getString("nombre").split("\\.");
					    	File file =  File.createTempFile(vArchivo[0],vArchivo[1]); 
					    	if(vArchivo!=null && vArchivo.length>1) extension=vArchivo[1];
					    	
					    	FileOutputStream fop = new FileOutputStream(file);
					    	
					    	BASE64Decoder decoder = new BASE64Decoder();
					    	byte[] decodedBytes = decoder.decodeBuffer(j.getString("adjunto"));

					    	fop.write(decodedBytes);
					    	fop.flush();
					    	fop.close();
					    	
					    	DLFileEntry dl=null;
					      	
					      	title=idFacturaImg +"-Recibo" ;
					      	try{
					      	   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title + (extension.length()>0?".":"") + extension);
					      	} catch(Exception e2){}   
					      	  
					    	if(dl==null) {	
					      	   DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, j.getString("nombre"),
					      			j.getString("nombre"), title, j.getString("nombre"), "", file, serviceContext);
					    	}
					    }catch(Exception e1) {
					    	logger.debug("Error WS LPA "+e1);
					    }
					}    
				}
			}
		}catch(Exception e) {
			logger.debug("Error WS LPA "+e);
		}
	}
  }



    

  
}
