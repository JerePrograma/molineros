package ar.com.ospim.autorizaciones.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos;


public class UploadArchivoIntegracionSSSAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoIntegracionSSSAction.class);

	private List<String> errores = new ArrayList<String>();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);

		Boolean proceso=false;
		errores.clear();
		try {
			
			String fileName = uploadReq.getFileName("archivoSSS").toLowerCase();
			String entidad =ParamUtil.getString(actionRequest, "tercerizadora");
			logger.info("subiendo archivo :" + fileName);
			if (fileName != null ) {
				File fileSelec = uploadReq.getFile("archivoSSS");
				if ((fileName.startsWith("112608") ) && (fileName.endsWith(".devok") || fileName.endsWith(".deverr") )) {
					proceso=true;
					errores = procesarArchivoIntegracion(actionRequest, fileSelec,fileName);
				}else if((fileName.startsWith("112608") ) && (fileName.endsWith(".subsidio") )) {
					proceso=true;
					errores = procesarArchivoIntegracionSubsidios(actionRequest, fileSelec,fileName);			
			    }else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}else {
				//Procesa respuesta superintendencia
			}
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if (null!=errores && !errores.isEmpty() && proceso) {
//			RendicionBancoNacionRegistroDuplicado e = new RendicionBancoNacionRegistroDuplicado();
			ErrorGeneralProcesandoArchivos e = new ErrorGeneralProcesandoArchivos();
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute("errores", errores);
		}
		
		if (SessionErrors.isEmpty(actionRequest) && !proceso) {
			errores.add("No se proceso el archivo solicitado");
			ErrorGeneralProcesandoArchivos e = new ErrorGeneralProcesandoArchivos();
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

	private List<String> procesarArchivoIntegracion(ActionRequest actionRequest, File zip,String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    
	    String tipoArchivo ="";
		if(fileName.endsWith(".devok")) {
			tipoArchivo="OK";
		}else {
			tipoArchivo="ERR";
		}

		List<IntegracionDetalleDS> lista= new ArrayList<IntegracionDetalleDS>();
		
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
				  try {
				     archivo.setError(vLine[19]);
				  }catch(Exception e) {} 
				  if("OK".equalsIgnoreCase(tipoArchivo)) {
					  archivo.setError("OK");
				  }
				}catch(Exception e){
					logger.debug(e);
				}
			
			lista.add(archivo);
		}
		 

		
		if(lista.size()>0){
			
			boolean resp = IntegracionServiceUtil.getValidaFTPProcesado(lista.get(0).getPeriodoPrestacion(),tipoArchivo);
			if(resp) {
				errores.add("Archivo ya Procesado");
			}else {
			      IntegracionServiceUtil.updateFTPDS_OK(lista);
			}    
			
		}
		return errores;
	}
	
	
	private List<String> procesarArchivoIntegracionSubsidios(ActionRequest actionRequest, File zip,String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<IntegracionDetalleDS> lista= new ArrayList<IntegracionDetalleDS>();
		
		FileInputStream file = new FileInputStream(zip);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file,"UTF-8"));
		String line = null;
		BigDecimal bd;
		while ((line = reader.readLine()) != null) {
			String[] vLine = line.split("\\|");
			IntegracionDetalleDS archivo = new IntegracionDetalleDS();
			try{
				  archivo.setNroLiquidacionSSS(vLine[0]); //Nro liquidacion SSS
				  archivo.setId(Integer.parseInt(vLine[1])); //Periodo o Carpeta - Corresponde al perido de la cabecera
				  archivo.setCuil(vLine[3]); //CUIL Beneficiario
				  archivo.setPeriodoPrestacion(Integer.parseInt(vLine[4])); //Periodo Prestacion
				  archivo.setImporteSolicitado(Double.parseDouble(vLine[5])); //Importe Solicitado
				  archivo.setPrestacionCodigo(String.valueOf(Integer.parseInt(vLine[6]))); //Código de Práctica
				  archivo.setImporteSubsidiado(Double.parseDouble(vLine[7])); //Importe Subsidiado
				}catch(Exception e){
					logger.debug(e);
				}
			
			lista.add(archivo);
		}
		
		if(lista.size()>0){
			
			List<IntegracionDetalleDS>detalles = IntegracionServiceUtil.detalleDSByPeriodo(lista.get(0).getId());
			
			
			//Agrupo por cuil, periodo y prestacion
			String tipo="";
			Map<String,IntegracionCabeceraDS> mapa = new HashMap<String,IntegracionCabeceraDS>();
			for(IntegracionDetalleDS l:lista) {
				if(l.getImporteSubsidiado()<0) {
					tipo="DB";
				}else {
				    tipo="DS";	
				}
				l.setTipoArchivo(tipo);
				IntegracionCabeceraDS c = mapa.get(l.getCuil()+";"+l.getPeriodoPrestacion().toString()+";"+l.getPrestacionCodigo()+";"+tipo);
				if(c==null) {
					c= new IntegracionCabeceraDS();
					c.setTotal(0D);
				}
				c.setTotal(c.getTotal()+l.getImporteSubsidiado());
				c.setTotalComprobantes(0D);
				for(IntegracionDetalleDS d:detalles) {
					
				   if((l.getCuil()+";"+l.getPeriodoPrestacion().toString()+";"+l.getPrestacionCodigo()+";"+l.getTipoArchivo()).equalsIgnoreCase(d.getCuil()+";"+d.getPeriodoPrestacion().toString()+";"+d.getPrestacionCodigo()+";"+d.getTipoArchivo())
						 && "OK".equalsIgnoreCase(d.getErrorSSS()) ) {
					  d.setNroLiquidacionSSS(l.getNroLiquidacionSSS()); 
				      c.getItems().add(d);
				      c.setTotalComprobantes(c.getTotalComprobantes()+d.getImporteSolicitadoSSS());
				   }   
				}
				mapa.put(l.getCuil()+";"+l.getPeriodoPrestacion().toString()+";"+l.getPrestacionCodigo()+";"+tipo, c);
			}
			
			
			//Estimacion de subsidio en al porcentaje de incidencia en el total de los comprobantes sin error 
			List<IntegracionDetalleDS> list= new ArrayList<IntegracionDetalleDS>();
			
			for (Map.Entry<String, IntegracionCabeceraDS> entry : mapa.entrySet()) {
				 String key = entry.getKey();
				 IntegracionCabeceraDS value = entry.getValue();
				 Double subsidioRestante = value.getTotal();
				 Double subsidioAplicado=0D;
				 Double porcentaje=0D;
				 
				 for(int xi=0;xi<value.getItems().size();xi++) {
					 
					IntegracionDetalleDS d =value.getItems().get(xi);
				    porcentaje=d.getComprobanteImporte()*100/value.getTotalComprobantes();
				    subsidioAplicado=value.getTotal()*porcentaje/100;
				    
				    if(xi==value.getItems().size()-1) {
				    	d.setImporteSubsidiado(subsidioRestante);	
				    }else {
				        d.setImporteSubsidiado(subsidioAplicado);
				    }
				    subsidioRestante-=subsidioAplicado;
				    list.add(d);
				 }
			}
			
			IntegracionServiceUtil.updateFTPDS_Subsidio(list);
		}
		return errores;
	}

	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.integracion_procesa_archivo"));
	}
	
}
