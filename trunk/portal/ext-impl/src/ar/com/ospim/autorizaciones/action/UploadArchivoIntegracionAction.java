package ar.com.ospim.autorizaciones.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDS;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.IntegracionReglasValidacion;
import ar.com.ospim.autorizaciones.beans.ReglaValidacion;
import ar.com.ospim.autorizaciones.beans.ReglaValidacionParametros;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado;
import ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException;


public class UploadArchivoIntegracionAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoIntegracionAction.class);

	private List<String> errores = new ArrayList<String>();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);

		Boolean proceso=false;
		errores = new ArrayList<String>();
		
		try {
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			String entidad =ParamUtil.getString(actionRequest, "tercerizadora");
			String carpetaStr=ParamUtil.getString(actionRequest, "carpetaMesAnio");
			Integer carpeta = Integer.parseInt(carpetaStr.split("_")[1]) * 100 + Integer.parseInt(carpetaStr.split("_")[0])+1;
			logger.info("subiendo archivo :" + fileName);
			if (fileName != null && !"SS".equalsIgnoreCase(entidad)) {
				File fileSelec = uploadReq.getFile("archivo");
				if ((fileName.startsWith("ds") || fileName.startsWith("DS")) && fileName.endsWith(".xls")) {
					proceso=true;
					errores = procesarArchivoIntegracion(actionRequest, fileSelec,fileName,entidad);
				}else if ((fileName.startsWith("ds") || fileName.startsWith("DS")) && fileName.endsWith(".txt")) {
						proceso=true;
						errores = procesarArchivoIntegracionTxt(actionRequest, fileSelec,fileName,entidad,carpeta);	
						
				}else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}else {
				//Procesa respuesta superintendencia
			}
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if (null!=errores && !errores.isEmpty()) {
			//ErrorProcesandoArchivosAfipException e = new ErrorProcesandoArchivosAfipException();
			RendicionBancoNacionRegistroDuplicado e = new RendicionBancoNacionRegistroDuplicado();
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute("errores", errores);
		}
		
		if (SessionErrors.isEmpty(actionRequest) && !proceso) {
			errores.add("No se proceso el archivo solicitado");
			ErrorProcesandoArchivosAfipException e = new ErrorProcesandoArchivosAfipException();
			SessionErrors.add(actionRequest,e.getClass().getName());
		}
		
		if (SessionErrors.isEmpty(actionRequest)) {
			actionRequest.setAttribute("errores", errores);
		}
		
		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,"successMessage");
			SessionMessages.add(actionRequest, "request_processed",successMessage);
		}
		
		setForward(actionRequest, "portlet.autorizaciones.integracion_procesa_archivo");
		
	}

	private List<String> procesarArchivoIntegracion(ActionRequest actionRequest, File zip,String fileName,String entidad)
			throws Exception {
			return errores;
	}
	
	
	
	private List<String> procesarArchivoIntegracionTxt(ActionRequest actionRequest, File zip,String fileName,String entidad,Integer carpeta)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		List<IntegracionDetalleDS> lista= new ArrayList<IntegracionDetalleDS>();
		IntegracionReglasValidacion reglas = IntegracionServiceUtil.getReglasValidacion();
		
		FileInputStream file = new FileInputStream(zip);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file,"UTF-8"));
		String line = null;
		BigDecimal bd;
		while ((line = reader.readLine()) != null) {
			String[] vLine = line.split("\\|");
			IntegracionDetalleDS archivo = new IntegracionDetalleDS();
			try{
				  bd= new BigDecimal(vLine[6].trim());
				  archivo.setTipoArchivo(vLine[0]); //Tipo de Archivo
				  archivo.setIdObraSocial(Integer.parseInt(vLine[1])); //Código de obra social
				  archivo.setCuil(vLine[2]); //CUIL Beneficiario
				  archivo.setCertificadoCodigo(vLine[3].trim()); //Código del Certificado
				  if(!vLine[4].trim().isEmpty()) {
				     archivo.setCertificadoVencimiento(sdf.parse(vLine[4])); //Vencimiento del Certificado
				  } 
				  archivo.setPeriodoPrestacion(Integer.parseInt(vLine[5])); //Periodo Prestacion
				  archivo.setCuitPrestador(bd.toPlainString()); //CUIT de prestador
				  archivo.setComprobanteTipo(Integer.parseInt(vLine[7]));  //Tipo de comprobante
				  archivo.setComprobanteTipoEmision(vLine[8].trim()); //Tipo de emisión
				  if(!vLine[9].trim().isEmpty()) {
				     archivo.setComprobanteFechaEmision(sdf.parse(vLine[9])); //Fecha Emision Comprobante
				  }   
				  bd= new BigDecimal(vLine[10].trim());
				  archivo.setComprobanteCAECAI(bd.toPlainString()); //Numero CAE-CAI
				  archivo.setComprobantePtoVta(Integer.parseInt(vLine[11]));//Punto de Venta
				  archivo.setComprobanteNro(Integer.parseInt(vLine[12])); //Número Comprobante
				  archivo.setComprobanteImporte(Double.parseDouble(vLine[13])); //Importe Comprobante
				  archivo.setImporteSolicitado(Double.parseDouble(vLine[14])); //Importe Solicitado
				  archivo.setPrestacionCodigo(String.valueOf(Integer.parseInt(vLine[15]))); //Código de Práctica
				  archivo.setPrestacionCantidad(Integer.parseInt(vLine[16])); //Cantidad de Practicas
				  archivo.setProvincia(Integer.parseInt(vLine[17])); // Provincia
				  archivo.setDependencia(vLine[18]); //Dependencia
				}catch(Exception e){
					logger.debug(e);
				}
			
			lista.add(archivo);
		}
		 

		
		if(lista.size()>0){
			Map<Integer,IntegracionCabeceraDS> mapa= new HashMap<Integer,IntegracionCabeceraDS>();
			
			IntegracionCabeceraDS cab = new IntegracionCabeceraDS();
			Integer qErrores=0;
			Integer qDuplicados=0;
			String codError="";
			Map<String,IntegracionCabeceraDS> presNoSolas= new HashMap<String,IntegracionCabeceraDS>();
			String prestacionesInvalidasSolas = TraeListasServiceUtil.getSystemConfig("INTEGRACION_PRESTACIONES_INVALIDAS_SOLAS");
			
//Nuevas validaciones reglas para buscar dentro del mismo archivo
			String prestacionesInconsistentesReglas = TraeListasServiceUtil.getSystemConfig("INTEGRACION_PRESTACIONES_INCONSISTENTES_REGLAS");
			String[] reglasValidar = prestacionesInconsistentesReglas.split(";");
			String codPrestacion="";
			String codPrestacionesInconsistentes="";
			List <String> listReglas = new ArrayList<String>();
			Map<String,IntegracionCabeceraDS> presInconsistentes= new HashMap<String,IntegracionCabeceraDS>();
        	for(int xi=0; xi<=reglasValidar.length-1; xi++) {
        	   for(ReglaValidacion r:reglas.getReglas()) {
        		   if(reglasValidar[xi].equalsIgnoreCase(r.getId())) {
        			   for(ReglaValidacionParametros p: r.getParametros()) {
        				   if("__prestaciones".equalsIgnoreCase(p.getNombre())) {
        					   codPrestacion = p.getValor();
        				   }
        				   if("__prestaciones_in".equalsIgnoreCase(p.getNombre())) {
        					   codPrestacionesInconsistentes = p.getValor();
        				   }
        			   }
        			   listReglas.add(r.getId()+"-"+codPrestacion+"-"+codPrestacionesInconsistentes);
        		   }
        	   }
        	}
//Fin nueva validacion				
			
			try {
			  for(IntegracionDetalleDS s:lista){
				if(s.getPeriodoPrestacion()!=null) {
				   codError= IntegracionServiceUtil.validaDetalle(s,true,reglas);
				   if(!"DU".equalsIgnoreCase(codError)) { //Duplicado					
					  s.setError(codError);
					  
					  cab = mapa.get(carpeta);
					  if(cab==null) {
						  cab = new IntegracionCabeceraDS();
						  cab.setEntidad(entidad);
						  cab.setPeriodo(carpeta);
						  cab.setFecha(new Date());
					  }
					  cab.getItems().add(s);
					  mapa.put(carpeta, cab);
					  
			       }else {
					  qDuplicados++; 
					  errores.add("Comprobante Duplicado: Cuit " + s.getCuitPrestador() +" - Cuil " +s.getCuil() +" - Comprobante tipo " +
					    s.getComprobanteTipo() + " -  Pto Venta " + s.getComprobantePtoVta() +" - Nro " + s.getComprobanteNro());
				   }
				   
				   if("".equalsIgnoreCase(codError) || "OK".equalsIgnoreCase(codError)
						   || "PS".equalsIgnoreCase(codError)){
					    int resultado = prestacionesInvalidasSolas.indexOf(s.getPrestacionCodigo());
				        if(resultado != -1) {
				            presNoSolas.put(s.getCuil()+";" + s.getPeriodoPrestacion().toString(), new IntegracionCabeceraDS());
				        }
//Nueva validacion				        	
				       	for(String cad:listReglas){
				       		String[] vcad = cad.split("-");
				       		resultado = vcad[1].indexOf(s.getPrestacionCodigo());
						    if(resultado != -1) {
						       presInconsistentes.put(s.getCuil()+";" + s.getPeriodoPrestacion().toString()+";"+vcad[0], new IntegracionCabeceraDS());
						    }
				       	}
//Fin nueva validacion				        	
				       
				   }
				   
				   
				}
				
				
			  }
			
			  // Valido existencia de otras prestaciones
			  if(prestacionesInvalidasSolas.length()>0) {
				  for(IntegracionDetalleDS s:lista){
					  IntegracionCabeceraDS c = presNoSolas.get(s.getCuil()+";" + s.getPeriodoPrestacion().toString());
					  if(c!=null) {
					    c.getItems().add(s);
					    presNoSolas.put(s.getCuil()+";" + s.getPeriodoPrestacion().toString(),c);
					  }  
				  }
				  
				  for (Map.Entry<String, IntegracionCabeceraDS> entry : presNoSolas.entrySet()) {
					    String key = entry.getKey();
					    IntegracionCabeceraDS value = entry.getValue();
					    boolean ret =false;
					    for(IntegracionDetalleDS d:value.getItems()) {
					    	int resultado = prestacionesInvalidasSolas.indexOf(d.getPrestacionCodigo());
					        if(resultado == -1) {
					        	ret=true;
					        	if("PS".equalsIgnoreCase(d.getError())){
					        	  d.setError("OK");
					        	}  
					        	break;
					        }
					    }
					    
					    if(!ret) {
					    	IntegracionCabeceraDS c = mapa.get(carpeta);
					    	for(IntegracionDetalleDS d:c.getItems()) {
					    		if(key.equalsIgnoreCase(d.getCuil()+";"+d.getPeriodoPrestacion().toString()) && !"OK".equalsIgnoreCase(d.getError())) {
					    			d.setError("PS");
					    		}
					    	}
					    	mapa.put(carpeta,c);
					    }
				  }
			  }
			  
//Nueva validaciones
			  if(!presInconsistentes.isEmpty()) {
				  IntegracionCabeceraDS ca = mapa.get(carpeta);
			      for(IntegracionDetalleDS d:ca.getItems()) {
			    		
			    		for(String cad:listReglas){  
				        	  String[] vcad = cad.split("-"); 
						      IntegracionCabeceraDS c = presInconsistentes.get(d.getCuil()+";" + d.getPeriodoPrestacion().toString()+";"+vcad[0]);
						      if(c!=null) {
						    	  Integer cod=Integer.parseInt(d.getPrestacionCodigo());
						    	  int resultado = vcad[2].indexOf(cod.toString().trim());
							      if(resultado != -1) {
						             d.setError("IC");
							      }   
						      }
			    	    }
			      }	
			      mapa.put(carpeta,ca);
			  }
// fin nueva validaciones			  
			
			}catch(Exception e) {logger.debug(e);}
			
			if(qDuplicados>0) {
//				errores.add("Existen registros ya procesados");
			}else {
				for (Integer key : mapa.keySet()) {
					
					IntegracionCabeceraDS cabecera = mapa.get(key);
					IntegracionServiceUtil.saveLote(cabecera,user.getScreenName());
					
				}	
			}
			
		}
				
		return errores;

	}

	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.integracion_procesa_archivo"));
	}
	
}
