package ar.com.ospim.autorizaciones.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDR;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos;


public class UploadArchivoIntegracionRendicionAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoIntegracionRendicionAction.class);

	private List<String> errores = new ArrayList<String>();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);
		
		String forward ="";
		String origen=ParamUtil.getString(actionRequest, "origen");
		Boolean proceso=false;
		errores.clear();
		try {
			
			String fileName=null;
			
			try {
			   fileName = uploadReq.getFileName("archivoRendicion").toLowerCase();
			}catch(Exception e) {
				fileName=null;
			}
			//String entidad =ParamUtil.getString(actionRequest, "tercerizadora");
			
			if (fileName != null && !fileName.isEmpty()) {
				logger.info("subiendo archivo :" + fileName);
				forward= "portlet.autorizaciones.integracion_rendicion";
				File fileSelec = uploadReq.getFile("archivoRendicion");
				if ((fileName.startsWith("112608") ) && (fileName.endsWith(".envio"))) {
					proceso=true;
					errores = procesarArchivoIntegracionDREnvio(actionRequest, fileSelec,fileName);
				}else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}else {
				//Procesa respuesta superintendencia
			}
			try {
			   fileName = uploadReq.getFileName("archivoDevolucionOK").toLowerCase();
				logger.info("subiendo archivo :" + fileName);
			}catch(Exception e) {
				fileName=null;
			}   

			if (fileName != null && !fileName.isEmpty()) {
				File fileSelec = uploadReq.getFile("archivoDevolucionOK");
				forward= "portlet.autorizaciones.integracion_devolucion";
				Integer periodo = ParamUtil.getInteger(actionRequest,"periodo", 0);
				if ((fileName.startsWith("112608") ) && (fileName.endsWith("dr.devolucion_ok.txt"))) {
					proceso=true;
					errores = procesarArchivoIntegracionDRDevolucion(actionRequest, fileSelec,fileName,periodo);
					actionRequest.setAttribute("periodo",periodo);
					actualizaLista( periodo , actionRequest);
				}else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}else {
				//Procesa respuesta superintendencia
			}
			
			try {
			   fileName = uploadReq.getFileName("archivoDevolucionError").toLowerCase();
			   logger.info("subiendo archivo :" + fileName);
			}catch(Exception e) {
				fileName=null;
			}   
			if (fileName != null && !fileName.isEmpty() ) {
				forward= "portlet.autorizaciones.integracion_devolucion";
				File fileSelec = uploadReq.getFile("archivoDevolucionError");
				Integer periodo = ParamUtil.getInteger(actionRequest,"periodo", 0);
				if ((fileName.startsWith("112608") ) && (fileName.endsWith("dr.devolucion_err.txt"))) {
					proceso=true;
					errores = procesarArchivoIntegracionDRDevolucion(actionRequest, fileSelec,fileName,periodo);
					actionRequest.setAttribute("periodo",periodo);
					actualizaLista( periodo , actionRequest);
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
		if(origen!=null &&!origen.isEmpty()) {
			if("GES".equalsIgnoreCase(origen)) {
				forward= "portlet.autorizaciones.integracion_devolucion";
			}
		}else {
			forward= "portlet.autorizaciones.integracion_rendicion";
		}
			
		setForward(actionRequest,forward);
		
	}

	private List<String> procesarArchivoIntegracionDREnvio(ActionRequest actionRequest, File zip,String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    
		List<IntegracionDetalleDR> lista= new ArrayList<IntegracionDetalleDR>();
		Integer periodo=0;
		Double liquidado=0D;
		Double solicitado=0D;
		
		FileInputStream file = new FileInputStream(zip);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file,"UTF-8"));
		String line = null;
		BigDecimal bd;
		while ((line = reader.readLine()) != null) {
			String[] vLine = line.split("\\|");
			IntegracionDetalleDR archivo = new IntegracionDetalleDR();
			try{
				
				 archivo.setClave(vLine[0]);//Clave
				 archivo.setIdObraSocial(Integer.parseInt(vLine[1])); //Código de obra social
				 archivo.setTipoArchivo(vLine[2]); //Tipo de Archivo
				 archivo.setPeriodoPresentacion(Integer.parseInt(vLine[3])); //Periodo
				 archivo.setPeriodoPrestacion(Integer.parseInt(vLine[4])); //Periodo Prestacion
				 archivo.setCuil(vLine[5]); //CUIL Beneficiario
				 archivo.setPrestacionCodigo(Integer.parseInt(vLine[6])); //Código de Práctica
				 archivo.setImporteLiquidado(Double.parseDouble(vLine[7].replace(",","."))); //Importe Liquidado
				 archivo.setImporteSolicitado(Double.parseDouble(vLine[8].replace(",","."))); //Importe Solicitado
				 archivo.setCuitPrestador(vLine[9]); //CUIT de prestador
				 archivo.setComprobanteTipo(Integer.parseInt(vLine[10]));  //Tipo de comprobante
				 archivo.setComprobanteNro(Integer.parseInt(vLine[11])); //Número Comprobante
				 archivo.setComprobantePtoVta(Integer.parseInt(vLine[12]));//Punto de Venta
				 archivo.setNroEnvioAfip(Integer.parseInt(vLine[13])); //Nro envio Afip
				 
				}catch(Exception e){
					logger.debug(e);
				}
			periodo=archivo.getPeriodoPresentacion();
			liquidado += archivo.getImporteLiquidado();
			solicitado += archivo.getImporteSolicitado();
//			if(archivo.getImporteSolicitado()>0 || archivo.getImporteLiquidado()>0) {
			   lista.add(archivo);
//			}   
		}
		 

		
		if(lista.size()>0){
			IntegracionCabeceraDR cabecera = new IntegracionCabeceraDR();
			cabecera.setPeriodo(periodo);
			cabecera.setImporteLiquidado(liquidado);
			cabecera.setImporteSolicitado(solicitado);
			cabecera.setItems(lista);
			try {
			  Integer resp =IntegracionServiceUtil.saveDR_Envio(cabecera, user.getScreenName());
			}catch(SystemException e) {
				
				if(e.getMessage().contains("org.postgresql.util.PSQLException: ERROR: llave duplicada viola")) {	
				   errores.add("Período ya Procesado");
				}else {
					errores.add(e.getMessage());
				}
		    }catch(Exception e) {
				errores.add(e.getMessage());
			}
		}
		return errores;
	}
	

	
	private List<String> procesarArchivoIntegracionDRDevolucion(ActionRequest actionRequest, File zip,String fileName,Integer periodo)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    
	    String tipoArchivo ="";
		if(fileName.endsWith("dr.devolucion_ok.txt")) {
			tipoArchivo="OK";
		}else {
			tipoArchivo="ERR";
		}

	    
		List<IntegracionDetalleDR> lista= new ArrayList<IntegracionDetalleDR>();
		Double liquidado=0D;
		Double solicitado=0D;
		
		FileInputStream file = new FileInputStream(zip);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file,"UTF-8"));
		String line = null;
		BigDecimal bd;
		while ((line = reader.readLine()) != null) {
			String[] vLine = line.split("\\|");
			IntegracionDetalleDR archivo = new IntegracionDetalleDR();
			try{
				
				 archivo.setClave(vLine[0]);//Clave
				 archivo.setIdObraSocial(Integer.parseInt(vLine[1])); //Código de obra social
				 archivo.setTipoArchivo(vLine[2]); //Tipo de Archivo
				 archivo.setPeriodoPresentacion(Integer.parseInt(vLine[3])); //Periodo
				 archivo.setPeriodoPrestacion(Integer.parseInt(vLine[4])); //Periodo Prestacion
				 archivo.setCuil(vLine[5]); //CUIL Beneficiario
				 archivo.setPrestacionCodigo(Integer.parseInt(vLine[6])); //Código de Práctica
				 if("ERR".equalsIgnoreCase(tipoArchivo)) {
					 //archivo.setError(vLine[30].replace("-","").trim());
					 archivo.setError(vLine[30].trim());
				 }else {
					 archivo.setError("OK");
				 }
				 
				}catch(Exception e){
					logger.debug(e);
				}
			lista.add(archivo);
		}
		 

		
		if(lista.size()>0){
			try {
			  for(IntegracionDetalleDR d:lista) {
				  IntegracionServiceUtil.updateDetalleDR(d, user.getScreenName(),true);
			  }
			  
			}catch(SystemException e) {
				
				if(e.getMessage().contains("org.postgresql.util.PSQLException: ERROR: llave duplicada viola")) {	
				   errores.add("Período ya Procesado");
				}else {
					errores.add(e.getMessage());
				}
		    }catch(Exception e) {
				errores.add(e.getMessage());
			}
		}
		return errores;
	}

	
	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
        HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
        session.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_OFFSET_REG, 0);
        
		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.integracion_rendicion"));
	}
	
	private void actualizaLista(Integer periodo ,ActionRequest actionRequest) throws SystemException {
		periodo = ParamUtil.getInteger(actionRequest,"periodo", 0);
		  int pagina =0;  
		  IntegracionDetalleDR filtro =new IntegracionDetalleDR();
		  filtro.setSoloErrores(false);
		  filtro.setPeriodoPresentacion(periodo);
		  filtro.setId(null);
		  int totalrecords=0;	
		  List<IntegracionDetalleDR> busqueda= IntegracionServiceUtil.traeListaDetalleDR(pagina,filtro);
		  List<IntegracionDetalleDR> list = new ArrayList<IntegracionDetalleDR>(); 
		  if (busqueda.size()>0){
				totalrecords = busqueda.size();
				for(int i=0;i< (busqueda.size()>50?50:busqueda.size());i++) {
					list.add(busqueda.get(i));
				}
		  }else{
				totalrecords =0;
		  }
		  actionRequest.getPortletSession().removeAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO);
		  actionRequest.getPortletSession().setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO,	list);
		  actionRequest.getPortletSession().setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_TOTAL_REGISTROS, totalrecords );
		  actionRequest.getPortletSession().setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_OFFSET_REG, pagina++);
		  
	}
	
}
