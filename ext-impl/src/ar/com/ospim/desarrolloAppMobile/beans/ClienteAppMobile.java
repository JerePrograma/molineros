package ar.com.ospim.desarrolloAppMobile.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.AfiCuentasBancariasServiceUtil;
import ar.com.ospim.afiliados.services.AfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.action.CuentaDocumentoHelper;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamoAppMobileAuthClient;
import ar.com.ospim.desarrolloAppMobile.services.ClienteAppMobileServiceUtil;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import jcifs.smb.FileEntry;

public class ClienteAppMobile {		
	
	private static Log logger = LogFactoryUtil.getLog(ClienteAppMobile.class);
	private static final String HOST = TraeListasServiceUtil.getSystemConfig("APP_HOST_WEBSERVICE");
	private static final String PEDIDOS_URL = HOST + "/api/auth/pedidoautorizacion/estado?estado=";
	
	private static final String ACTUALIZAR_ESTADO_PEDIDO_URL = HOST + "/api/auth/pedidoautorizacion/estado/";
	
	private static final String REINTEGROS_URL = HOST + "/api/auth/pedidoreintegro/estado?estado=";
	private static final String ACTUALIZAR_ESTADO_REINTEGRO_URL = HOST + "/api/auth/pedidoreintegro/estado/";
	private static final String IMAGENES_URL_BASE = HOST + "/api/auth/pedidoautorizacion/documentos/";
	
	//validaciones para los adjuntos
	private static boolean hasUrl(String s) {
	    return s != null && !(s = s.trim()).isEmpty() && !"null".equalsIgnoreCase(s);
	}
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Compatibilidad para consumidores legacy. La autenticación y sus secretos
	 * se resuelven exclusivamente desde configuración externa.
	 */
	public static String obtenerToken() {
		return ReclamoAppMobileAuthClient.obtenerToken();
	}

	public static List<PreAutorizacion> getPreAutorizacionessByEstado(String estado, Date fechaIni, Date fechaFin) throws JSONException {
		List<PreAutorizacion> lista = new ArrayList<PreAutorizacion>();
		String token = obtenerToken();

		if (token == null) {
			logger.error("No se pudo obtener token.");
			return lista;
		}
		
		//Si las fechas son nulas, se pone un rango por defecto
		if (fechaFin == null) fechaFin = new Date();
		if (fechaIni == null) {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(fechaFin);
		    cal.add(Calendar.DAY_OF_MONTH, -60);
		    fechaIni = cal.getTime();
		}
		
		String url = PEDIDOS_URL + estado 
		           + "&fechaDesde=" + sdf.format(fechaIni) 
		           + "&fechaHasta=" + sdf.format(fechaFin);

		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(url);

		get.addRequestHeader("accept", "application/json");
		get.addRequestHeader("Authorization", "Bearer " + token);
		
		
		try {
			int status = httpClient.executeMethod(get);
			String response = get.getResponseBodyAsString();
            
			if (status == 200) {
			    JSONObject results = new JSONObject(response);
			    try {
			        Integer code = results.getInt("code");
			    } catch (Exception e) {
			        JSONArray items = results.getJSONArray("data");
			        
			        for (int i = 0; i < items.length(); i++) {
			            try {
			                JSONObject j = items.getJSONObject(i);
			                PreAutorizacion p = new PreAutorizacion();
			                Afiliado a = new Afiliado();
			                
			                
			                //formato de fecha
			                SimpleDateFormat SDF_DATE     = new SimpleDateFormat("yyyy-MM-dd");
			                SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			                
			                String fechaStr = j.optString("fecha_alta", null);
			                
			                Timestamp tsAlta = null;
			                if (fechaStr != null && !fechaStr.trim().isEmpty()) {
			                    String s = fechaStr.trim().replace('T',' ');
			                    Date d = (s.length() >= 19) ? SDF_DATETIME.parse(s) : SDF_DATE.parse(s.substring(0,10));
			                    tsAlta = new Timestamp(d.getTime());		                    
			                }
			                
			                int id = j.getInt("id");
			                p.setIdPedidoApp(id);
			                
			                Calendar cf = Calendar.getInstance();
			                cf.setTime(fechaFin);
			                cf.set(Calendar.HOUR_OF_DAY, 23);
			                cf.set(Calendar.MINUTE, 59);
			                cf.set(Calendar.SECOND, 59);
			                cf.set(Calendar.MILLISECOND, 999);
			                Date fechaFinEOD = cf.getTime();
			                
			                if (tsAlta != null && (tsAlta.before(fechaIni) || tsAlta.after(fechaFinEOD))) {
			                    continue;
			                }
			                
			                String cuil = j.getString("cuil");
			                Integer inte = j.getInt("inte");
			                String nombre   = j.optString("name");
			                String apellido   = j.optString("lastname");
			                
			                if (cuil != null && !"null".equalsIgnoreCase(cuil)) {
			                    a.setCuil_titular(cuil);
			                }
			                a.setInte(inte);
			                
			                if (apellido != null && !apellido.trim().isEmpty()) a.setApellido(apellido.trim());
			                if (nombre   != null && !nombre.trim().isEmpty())   a.setNombre(nombre.trim());
			                
			                p.setAfiliado(a);
			                p.setAlta_fecha(tsAlta);
			                
			                lista.add(p);

			            } catch (Exception e1) {
			                logger.error("Error parseando JSON del pedido", e1);
			            }
			        }
			    }
			} else {
			    logger.error("Error HTTP al obtener pedidos. Status: " + status + " Response: " + response);
			}
		} catch (Exception e) {
			logger.error("Excepción al obtener pedidos", e);
		} finally {
			get.releaseConnection();
		}

		return lista;
	}
	
	public static JSONArray getDocumentosDePedido(int pedidoId, String token) {       
		String url = IMAGENES_URL_BASE + pedidoId;
        
        HttpClient httpClient = new HttpClient();
        GetMethod get = new GetMethod(url);

        get.addRequestHeader("accept", "application/json");
        get.addRequestHeader("Authorization", "Bearer " + token);

        try {
            
            int status = httpClient.executeMethod(get);
            String response = get.getResponseBodyAsString();
            
            if (status == 200) {
                JSONObject results = new JSONObject(response);
                if (results.has("data")) {
                    return results.getJSONArray("data");
                } else {
                    logger.warn("La respuesta no contiene data. JSON: " + results.toString());
                }
            } else {
                logger.error("Error HTTP al obtener documentos. Status: " + status + " Response: " + response);
            }
        } catch (Exception e) {
            logger.error("Excepción al obtener documentos del pedido " + pedidoId, e);
        } finally {
            get.releaseConnection();
        }

        return new JSONArray();
    }

	public static void procesarDocumentosDePedido(int idPedidoApp, String token, ServiceContext serviceContext) {
	    if (token == null) {
	        logger.error("Token inválido para procesar documentos del pedido " + idPedidoApp);
	        return;
	    }
	    
	    //obtener el ID de la preautorización
	    Integer idPreaut = ClienteAppMobileServiceUtil.getIdPreautorizacionPorPedidoApp(idPedidoApp);
	    
	    JSONArray docs = getDocumentosDePedido(idPedidoApp, token);
	    
	    for (int j = 0; j < docs.length(); j++) {
	        try {
	            JSONObject doc = docs.getJSONObject(j);
	            String nombreArchivoOriginal = doc.getString("name");
	            String urlDescarga = doc.getString("pedido_aut_doc_url");
	            
	            String extension = extUrl(urlDescarga);
	            if (extension == null) {
	                logger.error("Documento sin formato " + urlDescarga);
	                continue;
	            }
	            
	            String baseName = nombreArchivoOriginal;
	            int lastDot = baseName.lastIndexOf('.');
	            if (lastDot > 0) {
	                baseName = baseName.substring(0, lastDot);
	            }
	            baseName = baseName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
	            if (baseName.matches("^\\d+$") || baseName.length() < 5) {
	                baseName = "archivo_" + idPedidoApp + "_" + j;
	            }

	            File archivo = descargarPreAutorizacionDesdeApp(urlDescarga, idPedidoApp, baseName, extension);
	            if (archivo == null || !archivo.exists()) {
	                logger.warn("Archivo no descargado: " + nombreArchivoOriginal);
	                continue;
	            }

	            String fileName = baseName + "." + extension;
	            String description = doc.optString("doc_code", "Sin código");

	            DLFolder folder = DLFolderLocalServiceUtil.getFolder(10136, 0L, "PREAUTORIZACIONES");
	            long folderId = folder.getFolderId();
	            
	            Random rnd = new Random();
	            String title;
	            DLFileEntry dl = null;
	            do {
	                title = "PREAUT_" + idPreaut + "-" + (int)(rnd.nextDouble() * 100);
	                try {
	                    dl = DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title + (extension.length() > 0 ? "." : "") + extension);
	                } catch (Exception e) {
	                    dl = null;
	                }
	            } while (dl != null);
	            
	            if (!"".equalsIgnoreCase(fileName)) {
	                try {
	                    DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
	                        serviceContext.getUserId(),
	                        folderId,
	                        fileName,
	                        fileName,
	                        title,
	                        description,
	                        "",
	                        archivo,
	                        serviceContext
	                    );

	                    PreAutorizacionServiceUtil.insertaSeguimientoDocumento((int) idPreaut, entry.getName(), serviceContext.getUserId() + "");

	                } catch (Exception e) {
	                    logger.error("Error al subir archivo", e);
	                }
	            }

	        } catch (Exception e) {
	            logger.error("Error al procesar un documento del pedido " + idPedidoApp, e);
	        }
	    }
	}
	
	public static File descargarPreAutorizacionDesdeApp(String url, Integer idPreautorizacion, String nombreBase, String extension) throws Exception {
		if (extension == null || extension.trim().isEmpty()) {
	        throw new IllegalArgumentException("Extensión vacía para preaut " + idPreautorizacion + " (URL: " + url + ")");
	    }
	    extension = extension.toLowerCase(Locale.ROOT);
	    
		HttpClient httpclient = new HttpClient();
	    GetMethod httpGet = new GetMethod(url);
	    httpGet.addRequestHeader("User-Agent", "telnet");

	    try {
	        int statusCode = httpclient.executeMethod(httpGet);
	        if (statusCode != 200) {
	            throw new RuntimeException("La URL devolvió un status diferente a 200: " + statusCode);
	        }

	        byte[] responseBody = httpGet.getResponseBody();
	        File file = File.createTempFile(nombreBase, "." + extension);
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.write(responseBody);
	        fos.close();

	        return file;

	    } finally {
	        httpGet.releaseConnection();
	    }
	}
	
	public static List<ReclamoPrestacional> getReintegrosByEstado(String estado, Date fechaIni, Date fechaFin) {
		List<ReclamoPrestacional> lista = new ArrayList<ReclamoPrestacional>();
		String token = obtenerToken();

		SimpleDateFormat SDF_DATE     = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
		if (token == null) {
			logger.error("No se pudo obtener token.");
			return lista;
		}	 
		
		//Si las fechas son nulas, se pone un rango por defecto
		if (fechaFin == null) fechaFin = new Date();
		if (fechaIni == null) {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(fechaFin);
		    cal.add(Calendar.DAY_OF_MONTH, -60);
		    fechaIni = cal.getTime();
		}
		
		String url = REINTEGROS_URL + estado 
		           + "&fechaDesde=" + sdf.format(fechaIni) 
		           + "&fechaHasta=" + sdf.format(fechaFin);

		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(url);

		get.addRequestHeader("accept", "application/json");
		get.addRequestHeader("Authorization", "Bearer " + token);

		try {
			int status = httpClient.executeMethod(get);
			String response = get.getResponseBodyAsString();
            
			if (status == 200) {
			    JSONObject results = new JSONObject(response);
			    try {
			        Integer code = results.getInt("code");
			    } catch (Exception e) {
			        JSONArray items = results.getJSONArray("data");
			        for (int i = 0; i < items.length(); i++) {
			            try {
			                JSONObject j = items.getJSONObject(i);
			                ReclamoPrestacional r = new ReclamoPrestacional();
			                Afiliado a = new Afiliado();			    			                
			                
			                //valores del comprobante
			                r.setUrlComprobante(j.optString("url_comprobante", null));
			                r.setUserCbu(j.optString("pedidoreintegro_cbu", null));
			                r.setUserCbuConstUrl(j.optString("pedidoreintegro_cbu_constancia", null));
			                r.setUrlDocExtra(j.optString("url_doc_extra", null));
			                r.setCbuAutorizante(j.optString("pedidoreintegro_cbu_autorizacion", null)); //nota autorizante
			                r.setTitular(j.optInt("titular", 0));
			                r.setLetraComprobante(j.optString("comprobante_code", null));
			                r.setNroComprobante(j.optString("nro_comprobante", null));
			                r.setCuitPrestador(j.optString("prestador_cuit", null)); 
			                r.setCuilTitular(j.optString("cuil_titular_cuenta", null));			                
			                
			                String sucursalStr = j.optString("sucursal_pto_venta", "0");
			                int sucursal = 0;
			                try {
			                    sucursal = Integer.parseInt(sucursalStr);
			                } catch (NumberFormatException e1) {
			                	
			                }
			                r.setSucuComprobante(sucursal);
			                
			                r.setImporteComprobante(BigDecimal.valueOf(j.optDouble("importe", 0)));
			                r.setSector("PRESTACIONES MEDICAS");
			                
			                //crea prestaciones
			                if (r.getPrestaciones() == null || r.getPrestaciones().isEmpty()) {
			                    PrestacionesReclamo prestacion = new PrestacionesReclamo();
			                    prestacion.setFrecuencia("UNICA");
			                    
			                    String tipoOriginal = j.optString("comprobante_tipo_code", null);
			                    r.setTipoComprobante(tipoOriginal);

			                    String tipoComprobanteCodigo = "OTR";
			                    if ("TIPO_RECIBO".equalsIgnoreCase(tipoOriginal)) {
			                        tipoComprobanteCodigo = "RCB";
			                    } else if ("FCP".equalsIgnoreCase(tipoOriginal) || "TIPO_FACTURA".equalsIgnoreCase(tipoOriginal)) {
			                        tipoComprobanteCodigo = "FCP";
			                    }
			                    prestacion.setComprobanteTipo(tipoComprobanteCodigo);
			                    
			                    prestacion.setComprobanteLetra(r.getLetraComprobante());
			                    prestacion.setComprobanteNro(String.format("%08d", Integer.parseInt(r.getNroComprobante())));

			                    prestacion.setComprobanteSucursal(String.format("%05d", r.getSucuComprobante()));

			                    prestacion.setComprobanteCantidad(1.0);
			                    BigDecimal importe = r.getImporteComprobante();
			                    prestacion.setComprobanteImporte(importe != null ? importe.doubleValue() : 0.0);
			                    
			                    BigDecimal importeTotal = r.getImporteComprobante();
			                    prestacion.setComprobanteTotal(importeTotal != null ? importeTotal.doubleValue() : 0.0);
			                    
			                    String cuitPrestador = j.optString("prestador_cuit", null);
			                    if (cuitPrestador != null && !"null".equalsIgnoreCase(cuitPrestador)) {
			                        r.setCuitPrestador(cuitPrestador);
			                        prestacion.setComprobanteCUIT(cuitPrestador);
			                    }
			                    
			                    prestacion.setRecuperable(0);
			                    prestacion.setReconocidoSSS(0.0);
			                    prestacion.setComprobanteCUITSucursal("000");
			                    
			                    Date fechaComprobante = null;
			                    String fechaStr = j.optString("fecha", null);
			                    if (fechaStr != null && !fechaStr.trim().isEmpty() && !"null".equalsIgnoreCase(fechaStr)) {
			                        String s = fechaStr.trim().replace('T', ' ');
			                        try {
			                            fechaComprobante = (s.length() >= 19)
			                                    ? SDF_DATETIME.parse(s.substring(0, 19))
			                                    : SDF_DATE.parse(s.substring(0, 10));
			                        } catch (Exception e1) {
			                            logger.warn("Fecha de comprobante inválida: " + fechaStr);
			                        }
			                    }
			                    prestacion.setComprobanteFecha(fechaComprobante);
			                    prestacion.setFechaPrestacion(fechaComprobante);
			                    
			                    List<PrestacionesReclamo> listaPrestacion = new ArrayList<PrestacionesReclamo>();
			                    listaPrestacion.add(prestacion);
			                    r.setPrestaciones(listaPrestacion);
			                }			                
			                
			                String sUpdate = j.optString("fecha_alta", null);
			                Timestamp tsAlta = null;
			                if (sUpdate != null) {
			                    String s = sUpdate.trim().replace('T',' ');
			                    if (s.length() >= 19) {
			                        tsAlta = new Timestamp(SDF_DATETIME.parse(s.substring(0,19)).getTime());
			                    } else if (s.length() >= 10) {
			                        tsAlta = new Timestamp(SDF_DATE.parse(s.substring(0,10)).getTime());
			                    } else {
			                        logger.warn("fecha_alta demasiado corta: [" + s + "]");
			                    }
			                }
			                
			                Calendar cf = Calendar.getInstance();
			                cf.setTime(fechaFin);
			                cf.set(Calendar.HOUR_OF_DAY, 23);
			                cf.set(Calendar.MINUTE, 59);
			                cf.set(Calendar.SECOND, 59);
			                cf.set(Calendar.MILLISECOND, 999);
			                Date fechaFinEOD = cf.getTime();
			                
			                if (tsAlta == null || tsAlta.before(fechaIni) || tsAlta.after(fechaFinEOD)) {
			                    continue;
			                }
			                
			                int id = j.getInt("id");
			                r.setId(id);
			                r.setIdReintegroApp(id);		                
			                			                
			                String cuil = j.optString("reintegro_cuil", null);
			                if (cuil == null || cuil.trim().isEmpty() || "null".equalsIgnoreCase(cuil)) {
			                    cuil = j.optString("cuil_titular", null);
			                }
			                
			                if (cuil != null && !cuil.trim().isEmpty() && !"null".equalsIgnoreCase(cuil)) {
			                    a.setCuil_titular(cuil);
			                    r.setReintegroCuil(cuil);
			                    r.setCuilTitular(cuil);
			                }
			                
			                String cuilCuenta = j.optString("cuil_titular_cuenta", null);
			                if (cuilCuenta != null && !cuilCuenta.trim().isEmpty() && !"null".equalsIgnoreCase(cuilCuenta)) {
			                    r.setCuilTitularCuenta(cuilCuenta);
			                }
			                
			                Integer inte = j.getInt("inte");			                
			                a.setInte(inte);
			                r.setAfiliado(a);
			                r.setAlta_fecha(tsAlta);
			                r.setSeccional_fecha(tsAlta);
			                
			                lista.add(r);

			            } catch (Exception e1) {
			                logger.error("Error parseando JSON del reintegro", e1);
			            }
			        }
			    }
			} else {
			    logger.error("Error HTTP al obtener reintegro. Status: " + status + " Response: " + response);
			}
		} catch (Exception e) {
			logger.error("Excepción al obtener reintegro", e);
		} finally {
			get.releaseConnection();
		}
		return lista;
	}
	
	private static String extUrl(String url) {
	    try {
	        if (url == null) return null;
	        String path = new URL(url).getPath();
	        int p = (path != null) ? path.lastIndexOf('.') : -1;
	        if (p != -1 && p < path.length() - 1) {
	            return path.substring(p + 1).toLowerCase(Locale.ROOT);
	        }
	    } catch (Exception ignore) {}
	    return null;
	}

	public static void procesarDocumentosDeReintegro(Comprobante comprobante, ServiceContext serviceContext) {
		ReclamoPrestacional r = comprobante.getReintegro();
	    Integer idReintegro = comprobante.getIdReintegro();

	    String urlComprobante   = comprobante.getUrlComprobante();
	    String urlConstanciaCbu = r.getUserCbuConstUrl();
	    String urlDocExtra      = r.getUrlDocExtra();
	    String cbuAutorizante = r.getCbuAutorizante();
	    String cbuNuevo  = r.getUserCbu();
	    String cuilLocal = (r.getCuilTitularCuenta() != null && !r.getCuilTitularCuenta().trim().isEmpty())
	            ? r.getCuilTitularCuenta()
	            : (r.getAfiliado() != null ? r.getAfiliado().getCuil_titular() : null);

	    try {
	        try {	        	
	        	if (hasUrl(urlComprobante)) {
	                String baseName = "reintegro_" + idReintegro;
	                String extComp = extUrl(urlComprobante);
	                if (extComp == null) {
	                    logger.error("REINTEGRO URL sin extensión: " + urlComprobante);
	                } else {
	                    File archivoComprobante = descargarReintegroDesdeApp(urlComprobante, idReintegro, baseName, extComp);
	                    if (archivoComprobante != null && archivoComprobante.exists()) {
	                        DLFolder folder = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales");
	                        long folderId = folder.getFolderId();

	                        String title = generaTituloUnico(folderId, String.valueOf(idReintegro), extComp);
	                        DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
	                                serviceContext.getUserId(),
	                                folderId,
	                                archivoComprobante.getName(),
	                                archivoComprobante.getName(),
	                                title,
	                                "Factura APP",
	                                "",
	                                archivoComprobante,
	                                serviceContext
	                        );
	                    logger.info("Comprobante subido - titulo:" + title + " nombre:" + entry.getName());

	                    ReclamosPrestacionesServiceUtil.updateNombreImagen(
                                r, entry.getName(), entry.getDescription(), String.valueOf(serviceContext.getUserId()));
                    } else {
                        logger.warn("No se pudo descargar comprobante desde: " + urlComprobante);
                    }
	            }
	            } else {
	                logger.info("Sin URL de comprobante");
	            }
	           
	        } catch (Exception e) {
	            logger.warn("Error al subir comprobante", e);
	        }
	        
	        String imagenNotaAutorizada = null;
	        try {
	            if (hasUrl(cbuAutorizante)) {
	                DLFolder folder = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales");
	                long folderId = folder.getFolderId();

	                String descNota = "NOTA AUTORIZACION PAGO";
	                String msgErrorNota = CuentaDocumentoHelper.validaExisteImagen(r, descNota);
	                if (msgErrorNota == null) {
	                    String extNota = extUrl(cbuAutorizante);
	                    if (extNota == null) {
	                        logger.error("Nota autorizada URL sin extensión): " + cbuAutorizante);
	                    } else {
	                        File archivoNota = descargarReintegroDesdeApp(
	                        		cbuAutorizante,
	                                r.getId_reclamo(),
	                                "nota_autorizada_" + r.getId_reclamo() + "_" + System.currentTimeMillis(),
	                                extNota
	                        );
	                    if (archivoNota != null && archivoNota.exists()) {
                            String titleNota = generaTituloUnico(folderId, String.valueOf(r.getId_reclamo()), extNota);
	                        DLFileEntry entryNota = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
	                                serviceContext.getUserId(), folderId,
	                                archivoNota.getName(), archivoNota.getName(),
	                                titleNota, descNota, "", archivoNota, serviceContext
	                        );
	                        ReclamosPrestacionesServiceUtil.updateNombreImagen(
	                                r, entryNota.getName(), entryNota.getDescription(), String.valueOf(serviceContext.getUserId())
	                        );
	                        imagenNotaAutorizada = entryNota.getName();
	                        logger.info("Nota autorizada subida " + imagenNotaAutorizada);
	                    } else {
	                        logger.warn("No se pudo descargar nota autorizada");
	                    }
	                }
	                } else {
	                    logger.info("Nota autorizada ya existente");
	                }
	            } else {
	                logger.info("Sin URL de nota autorizada.");
	            }
	        } catch (Exception e) {
	            logger.warn("Error al procesar nota autorizada", e);
	        }
	        
	        String imagenDocExtra = null;
	        try {
	            if (hasUrl(urlDocExtra)) {
	                DLFolder folder = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales");
	                long folderId = folder.getFolderId();
	                    
	                String descDocExtra = "DOCUMENTACION EXTRA";
	                String msgErrorDocExtra = CuentaDocumentoHelper.validaExisteImagen(r, descDocExtra);
	                if (msgErrorDocExtra == null) {
	                    String extDocExtra = extUrl(urlDocExtra);
	                    if (extDocExtra == null) {
	                        logger.error("Doc extra URL sin extensión: " + urlDocExtra);
	                    } else {
	                        File archivoDocExtra = descargarReintegroDesdeApp(
	                                urlDocExtra,
	                                r.getId_reclamo(),
	                                "doc_extra_" + r.getId_reclamo() + "_" + System.currentTimeMillis(),
	                                extDocExtra
	                        );
	                        if (archivoDocExtra != null && archivoDocExtra.exists()) {
	                            String titleDocExtra = generaTituloUnico(folderId, String.valueOf(r.getId_reclamo()), extDocExtra);
	                            DLFileEntry entryDocExtra = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
	                                    serviceContext.getUserId(), folderId,
	                                    archivoDocExtra.getName(), archivoDocExtra.getName(),
	                                    titleDocExtra, descDocExtra, "", archivoDocExtra, serviceContext
	                            );
	                            ReclamosPrestacionesServiceUtil.updateNombreImagen(
	                                    r, entryDocExtra.getName(), entryDocExtra.getDescription(), String.valueOf(serviceContext.getUserId())
	                            );
	                            imagenDocExtra = entryDocExtra.getName();
	                            logger.info("Doc extra subido  " + imagenDocExtra);
	                        } else {
	                            logger.warn("No se pudo descargar doc extra desde: " + urlDocExtra);
	                        }
	                    }
	                } else {
	                    logger.info("Doc extra ya existente: " + msgErrorDocExtra);
	                }
	            } else {
	                logger.info("Sin URL de doc extra.");
	            }
	        } catch (Exception e) {
	            logger.warn("Error al procesar doc extra", e);
	        }	        
	        
	        boolean tengoCBU = (cbuNuevo != null && !cbuNuevo.trim().isEmpty());
	        DLFileEntry entryCbuSubida = null;
	        String imagenCBU = null;

	        // intenta traer cuentas por si debemos reutilizar una imagen ya existente
	        List<ReclamoPrestacionalCuenta> cuentas = null;
	        try {
	            if (cuilLocal != null && !cuilLocal.trim().isEmpty()) {
	                cuentas = ReclamosPrestacionesServiceUtil.getCuentasPorCuil(cuilLocal);
	                logger.info("Cuentas para CUIL " + cuilLocal + ": " + (cuentas != null ? cuentas.size() : 0));
	            } else {
	                logger.warn("CUIL vacio o nulo");
	            }
	        } catch (Exception e) {
	            logger.warn("No se pudo consultar cuentas", e);
	        }
	        
	        logger.info("tengoCBU=" + tengoCBU + " urlConstanciaCbu=" + urlConstanciaCbu);
	        logger.warn("urlConstanciaCbu vacio o nulo");

	        // si viene URL de constancia, se sube
	        if (tengoCBU && hasUrl(urlConstanciaCbu)) {
	            try {
	                String descCbu = "CBU";
	                String msgError = CuentaDocumentoHelper.validaExisteImagen(r, descCbu);
	                if (msgError == null) {
	                	String extCBU = extUrl(urlConstanciaCbu);
		                if (extCBU == null) {
		                    logger.error("Comprobante sin formato");
		                } else {
	                    File archivoCbu = descargarReintegroDesdeApp(
	                            urlConstanciaCbu,
	                            r.getId_reclamo(),
	                            "cbu_constancia_" + r.getId_reclamo() + "_" + System.currentTimeMillis(),
	                            extCBU
	                    );
	                    if (archivoCbu != null && archivoCbu.exists()) {
	                        DLFolder folder = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales");
	                        long folderId = folder.getFolderId();

                            String titleCbu = generaTituloUnico(folderId, String.valueOf(r.getId_reclamo()), extCBU);
	                        entryCbuSubida = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
	                                serviceContext.getUserId(),
	                                folderId,
	                                archivoCbu.getName(), archivoCbu.getName(),
	                                titleCbu, descCbu, "", archivoCbu, serviceContext
	                        );
	                        ReclamosPrestacionesServiceUtil.updateNombreImagen(
	                                r, entryCbuSubida.getName(), entryCbuSubida.getDescription(), String.valueOf(serviceContext.getUserId())
	                        );
	                        imagenCBU = entryCbuSubida.getName();
	                        logger.info("Constancia CBU subida " + imagenCBU);
	                    }
		            }
		          
	                } else {
	                    logger.info("Constancia CBU ya existente para el reclamo: " + msgError);           
	                    
	                    try {
	                        ReclamoPrestacional tmp = new ReclamoPrestacional();
	                        ReclamoPrestacionalCuenta tmpCta = new ReclamoPrestacionalCuenta();
	                        tmpCta.setIdReclamoPrestacional(r.getId_reclamo());
	                        tmp.setCuenta(tmpCta);
	                        
	                        CuentaDocumentoHelper.getImagenNombre(tmp);

	                        if (tmp.getCuenta() != null && tmp.getCuenta().getImagenCBU() != null) {
	                            imagenCBU = tmp.getCuenta().getImagenCBU();
	                            logger.info("Reutilizo archivo CBU existente " + imagenCBU);
	                        }
	                    } catch (Exception ex) {
	                        logger.warn("No pude recuperar archivo CBU existente", ex);
	                    }	                    
	                }
	            } catch (Exception e) {
	                logger.warn("Error al subir constancia CBU", e);
	            }
	        }

	        // si no se subio constancia, intent usar la que ya existe
	        if (tengoCBU && imagenCBU == null && cuentas != null) {
	            for (ReclamoPrestacionalCuenta c : cuentas) {
	                boolean tieneImg = (c.getImagenCBU() != null && !c.getImagenCBU().isEmpty());
	                if (tieneImg && cbuNuevo.equals(c.getCbu())) {
	                    imagenCBU = c.getImagenCBU();
	                    break;
	                }
	            }
	        }
	        
	        logger.info("imagenCBU " + imagenCBU);

	        
	        if (tengoCBU) {
	            try {
	                ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
	                cuenta.setIdReclamoPrestacional(r.getId_reclamo());

	                // cuil_titular (grupo familiar)
	                String cuilGrupoFamiliar = r.getReintegroCuil();
	                if (cuilGrupoFamiliar == null || cuilGrupoFamiliar.trim().isEmpty()) {
	                    cuilGrupoFamiliar = (r.getAfiliado() != null) ? r.getAfiliado().getCuil_titular() : null;
	                    logger.info("Cuil titular: "+r.getAfiliado().getCuil_titular());
	                    logger.info("Inte: "+r.getAfiliado().getInte());
	                }
	                cuenta.setCuilGrupoFamiliar(cuilGrupoFamiliar);

	             	// CUIL del titular del CBU (puede ser el afiliado o un apoderado)
	                String cuilCBU = r.getCuilTitularCuenta();
	                if (cuilCBU == null || cuilCBU.trim().isEmpty()) {
	                    cuilCBU = cuilGrupoFamiliar;
	                }
	                cuenta.setCuil(cuilCBU);

	                // datos principales
	                cuenta.setCbu(cbuNuevo);
	                cuenta.setImagenCBU(imagenCBU);

	                // titularidad
	                boolean esApoderado = (imagenNotaAutorizada != null);
	                cuenta.setCmbTitular(esApoderado ? "1" : "0");
	                cuenta.setImagenNotaAutorizada(esApoderado ? imagenNotaAutorizada : null);	               
	                
	                //traer siempre al titular (inte = 0)
	                Afiliado titular = null;
	                String cuilTitular = (r.getAfiliado() != null)
	                    ? r.getAfiliado().getCuil_titular()
	                    : r.getCuilTitularCuenta();

	                try {
	                    titular = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuilTitular, 0);

	                    if (titular == null) {
	                        logger.warn("No se encontró afiliado");
	                        titular = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(cuilTitular, 0);
	                    }

	                    if (titular != null) {
	                        logger.info("Titular encontrado: "
	                            + titular.getApellido() + ", " + titular.getNombre() + " Email: " + titular.getEmail());
	                    } else {
	                        logger.warn("No se encontró ningun afiliado para el CUIL " + cuilTitular);
	                    }
	                } catch (Exception e) {
	                    logger.error("Error buscando afiliado titular", e);
	                }

	                //datos del titular (siempre se muestran, incluso si es apoderado)
	                String emailTitular = (titular != null && titular.getEmail() != null) ? titular.getEmail() : "";
	                String apellidoTitular = (titular != null) ? titular.getApellido() : "";
	                String nombreTitular = (titular != null) ? titular.getNombre() : "";

	                cuenta.setEmail(emailTitular);
	                
	                if (!esApoderado) {
	                    cuenta.setApellido(apellidoTitular);
	                    cuenta.setNombre(nombreTitular);
	                } else {
	                    // si es apoderado, los nombres se limpian pero el email sigue siendo el del titular
	                    cuenta.setApellido("");
	                    cuenta.setNombre("");
	                }
	                
	                /*
	                
	                // datos de afiliado
	                if (!esApoderado && r.getAfiliado() != null) {
	                    cuenta.setApellido(r.getAfiliado().getApellido());
	                    cuenta.setNombre(r.getAfiliado().getNombre());
	                    cuenta.setEmail(r.getAfiliado().getEmail() != null ? r.getAfiliado().getEmail() : "");
	                } else {
	                    cuenta.setApellido(""); cuenta.setNombre(""); cuenta.setEmail("");
	                }*/
	                
	                // validaciones mínimas antes de insertar
	                if (cuenta.getCuilGrupoFamiliar() == null || cuenta.getCuilGrupoFamiliar().trim().isEmpty()) {
	                } else if (cuenta.getCuil() == null || cuenta.getCuil().trim().isEmpty()) {
	                } else {
	                    r.setCuenta(cuenta);

	                    User user = UserLocalServiceUtil.getUser(serviceContext.getUserId());
	                    user.setScreenName("AppMobile");
	                    ReclamosPrestacionesServiceUtil.altaModiCuenta(r, user);
	                    
	                    cuenta.setImagenCBU(imagenCBU != null ? imagenCBU : "");
	                    cuenta.setImagenNotaAutorizada(imagenNotaAutorizada != null ? imagenNotaAutorizada : "");	                    	                
	                
	                    boolean esTitular = "0".equals(r.getCuenta().getCmbTitular());

	                    // cuil de la cuenta bancaria (solo si es apoderado)
	                    String cuilCbu = esTitular ? null : r.getCuenta().getCuil();

	                    // apellido/nombre que vas a guardar en afi_cuentas_bancarias:
	                    // - si es titular: apellido/nombre del titular
	                    // - si es apoderado: apellido/nombre del autorizado (del reclamo)
	                    String apellidoGuardar = esTitular ? apellidoTitular : r.getCuenta().getApellido();
	                    String nombreGuardar   = esTitular ? nombreTitular   : r.getCuenta().getNombre();
	                    
	                    String emailGuardar = emailTitular;
	                    
	                    // guarda/actualiza cuenta bancaria en afi_cuentas_bancarias
	                    AfiCuentasBancariasServiceUtil.insertaOActualizaCuentaBancaria(
	                        cuilTitular,
	                        0, // inte siempre 0
	                        apellidoGuardar,
	                        nombreGuardar,
	                        emailGuardar,
	                        esTitular,
	                        r.getCuenta().getCbu(),
	                        cuilCbu,
	                        r.getCuenta().getImagenCBU(),
	                        r.getCuenta().getImagenNotaAutorizada(),
	                        user
	                    );
	                }

	            } catch (Exception e) {
	                logger.error("Error al ejecutar altaModiCuenta para reintegro ID: " + r.getId_reclamo(), e);
	            }
	        }

	    } catch (Exception e) {
	        logger.error("Error general al procesar reintegro ID: " + idReintegro, e);
	    }
	}
	
	private static String generaTituloUnico(long folderId, String titulo, String ext) {
	    java.util.Random rnd = new java.util.Random();
	    while (true) {
	        String t = titulo + "-" + rnd.nextInt(100);
	        try {
	            DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, t + "." + ext);
	        } catch (Exception notFound) {
	            return t;
	        }
	    }
	}
	
	public static File descargarReintegroDesdeApp(String url, Integer idReintegro, String nombreBase, String extension) throws Exception {
		 //validaciones
	    if (url == null || (url = url.trim()).isEmpty() || "null".equalsIgnoreCase(url)) {
	        throw new IllegalArgumentException("URL vacía para reintegro " + idReintegro);
	    }
	    try {
	        java.net.URL u = new java.net.URL(url);
	        if (u.getHost() == null || u.getHost().isEmpty()) {
	            throw new IllegalArgumentException("URL sin host para reintegro " + idReintegro + ": " + url);
	        }
	    } catch (java.net.MalformedURLException e) {
	        throw new IllegalArgumentException("URL inválida para reintegro " + idReintegro + ": " + url, e);
	    }

	    if (extension == null || extension.trim().isEmpty()) {
	        throw new IllegalArgumentException(
	            "Extensión vacía para reintegro " + idReintegro + " (URL: " + url + ")"
	        );
	    }
	    extension = extension.toLowerCase(Locale.ROOT);
	    
		HttpClient httpclient = new HttpClient();
	    GetMethod httpGet = new GetMethod(url);
	    httpGet.addRequestHeader("User-Agent", "telnet");

	    try {
	        int statusCode = httpclient.executeMethod(httpGet);
	        if (statusCode != 200) {
	            throw new RuntimeException("La URL devolvió un status diferente a 200: " + statusCode);
	        }

	        byte[] responseBody = httpGet.getResponseBody();
	        File file = File.createTempFile(nombreBase, "." + extension);
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.write(responseBody);
	        fos.close();

	        return file;

	    } finally {
	        httpGet.releaseConnection();
	    }
	}
	
	public static void actualizarEstadoPedidoAutorizacion(int idPedido, String nuevoEstado, String token) {
	    if (token == null) {
	        logger.error("Token inválido para actualizar estado del pedido " + idPedido);
	        return;
	    }

	    String url = ACTUALIZAR_ESTADO_PEDIDO_URL + idPedido + "?estado=" + nuevoEstado;
	    
	    HttpClient httpClient = new HttpClient();
	    PostMethod post = new PostMethod(url);

	    post.addRequestHeader("accept", "application/json");
	    post.addRequestHeader("Authorization", "Bearer " + token);

	    try {
	        int status = httpClient.executeMethod(post);
	        String response = post.getResponseBodyAsString();

	        if (status == 200 || status == 204) {
	            logger.info("Estado actualizado correctamente para el pedido " + idPedido + " a: " + nuevoEstado);
	        } else {
	            logger.error("Error al actualizar estado. Status: " + status + " Response: " + response);
	        }
	    } catch (Exception e) {
	        logger.error("Excepción al actualizar estado del pedido " + idPedido, e);
	    } finally {
	        post.releaseConnection();
	    }
	}
	
	public static void actualizarEstadoReintegro(int idReintegro, String nuevoEstado, String token) {
	    if (token == null) {
	        logger.error("Token inválido para actualizar estado del reintegro " + idReintegro);
	        return;
	    }

	    String url = ACTUALIZAR_ESTADO_REINTEGRO_URL + idReintegro + "?estado=" + nuevoEstado;

	    HttpClient httpClient = new HttpClient();
	    PostMethod post = new PostMethod(url);

	    post.addRequestHeader("accept", "application/json");
	    post.addRequestHeader("Authorization", "Bearer " + token);

	    try {
	        int status = httpClient.executeMethod(post);
	        String response = post.getResponseBodyAsString();

	        if (status == 200 || status == 204) {
	            logger.info("Estado de reintegro actualizado correctamente para el id " + idReintegro + " a: " + nuevoEstado);
	        } else {
	            logger.error("Error al actualizar estado del reintegro. Status: " + status + " Response: " + response);
	        }
	    } catch (Exception e) {
	        logger.error("Excepción al actualizar estado del reintegro " + idReintegro, e);
	    } finally {
	        post.releaseConnection();
	    }
	}
	
	public static JSONObject getPedidoReintegroById(int idReintegroApp, String token) {
	    String url = HOST + "/api/auth/pedidoreintegro/" + idReintegroApp;

	    HttpClient httpClient = new HttpClient();
	    GetMethod get = new GetMethod(url);
	    get.addRequestHeader("accept", "application/json");
	    get.addRequestHeader("Authorization", "Bearer " + token);

	    try {
	        int status = httpClient.executeMethod(get);
	        String response = get.getResponseBodyAsString();

	        if (status == 200) {
	            JSONObject obj = new JSONObject(response);
	            if (obj.has("data")) return obj.getJSONObject("data");
	            return obj;
	        } else {
	            logger.error("Error HTTP (" + status + ") al obtener reintegro app id=" + idReintegroApp + ". Resp: " + response);
	        }
	    } catch (Exception e) {
	        logger.error("Excepción al obtener reintegro app id=" + idReintegroApp, e);
	    } finally {
	        get.releaseConnection();
	    }
	    return null;
	}
}
