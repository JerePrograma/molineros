package ar.com.ospim.autorizaciones.action;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos;


public class UploadRecibosIntegracionAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadRecibosIntegracionAction.class);

	private List<String> errores = new ArrayList<String>();
		
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);

		Boolean proceso=false;
		errores.clear();
		try {
			
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			if(fileName==null || fileName.isEmpty()) {
				fileName = uploadReq.getFileName("archivoFTP").toLowerCase();
			}
			logger.info("subiendo archivo :" + fileName);
			if (fileName != null ) {
				
				if (fileName.endsWith(".xls") ) {
					File fileSelec = uploadReq.getFile("archivo");
					proceso=true;
					errores = procesarRecibosIntegracion(actionRequest, fileSelec,fileName);
				}else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}else {}
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
		
		setForward(actionRequest, "portlet.autorizaciones.integracion_recibos");
		
	}

	private List<String> procesarRecibosIntegracion(ActionRequest actionRequest, File zip,String fileName) throws Exception {
	    
		User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat();

		List<IntegracionDetalleDS> lista= new ArrayList<IntegracionDetalleDS>();
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    if(qRow>=0){
		       IntegracionDetalleDS archivo = new IntegracionDetalleDS();	
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       
		       while (cellIterator.hasNext()){
		    	   
				celda = cellIterator.next();
				try{
				
				  Double xval;
				  Date dval;
				  BigDecimal bval;
				  Integer ival;
				  if(qCel==0){//Orden Pago
					xval=celda.getNumericCellValue();
					archivo.setOrdenPago(xval.intValue());  
				  }else if(qCel==1){//Nro Recibo
					try {  
					   bval=BigDecimal.valueOf(celda.getNumericCellValue());
					   bval = bval.stripTrailingZeros();
					   archivo.setNroRecibo(bval.toPlainString());
					}catch(Exception e) {
					   archivo.setNroRecibo(celda.toString());	
					}
				  }
				}catch(Exception e){
					logger.debug(e);
				}
				qCel++;
			  }
		      lista.add(archivo);
		   }
		   qRow++; 
		} 

		
		if(lista.size()>0){
			
			for(IntegracionDetalleDS s:lista){
				IntegracionServiceUtil.asociarRecibo(s.getOrdenPago(), s.getNroRecibo());
			}
			
		}
		
		return errores;
	}
	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.integracion_recibos"));
	}
	
}
