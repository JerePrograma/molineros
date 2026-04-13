package ar.com.global.action;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.constantes.interbaking.ConstantesInterbanking;
import ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos;


public class InterbankingAction extends PortletAction {
	private String strList = null;
	List<String> lista=  null;
	private static Log logger = LogFactoryUtil
			.getLog(InterbankingAction.class);

	private List<String> errores = new ArrayList<String>();
		
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);

		Boolean proceso=false;
		String retEjecutar="";
		String ctaBcria="";
		String entidad="";
		boolean conEmail=ParamUtil.getBoolean(actionRequest, "cE");
		errores.clear();
		try {
				
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			
			logger.info("subiendo archivo :" + fileName);
			if (fileName != null ) {
				
				if (fileName.endsWith(".xls") ) {
					File fileSelec = uploadReq.getFile("archivo");
					ctaBcria=uploadReq.getParameter("id_cta_bcria");
					entidad=uploadReq.getParameter("entidad");
					proceso=true;
					
					errores = procesarOrdenesPagoInterbanking(actionRequest, fileSelec,fileName);
					retEjecutar="true";
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
			String successMessage = ParamUtil.getString(actionRequest,"successMessage");
			SessionMessages.add(actionRequest, "request_processed",successMessage);
		}
		actionResponse.removePublicRenderParameter(ConstantesInterbanking.PARAMETRO );
		actionResponse.setRenderParameter(ConstantesInterbanking.PARAMETRO ,strList);
		actionResponse.setRenderParameter("ejecutar", retEjecutar);
		actionResponse.setRenderParameter("flagOcultar", "true");
		actionResponse.setRenderParameter("ctabcria", ctaBcria);
		actionResponse.setRenderParameter("cemail", conEmail?"true":"false");
		
	}

	private List<String> procesarOrdenesPagoInterbanking(ActionRequest actionRequest, File zip,String fileName) throws Exception {
	    
		lista = new ArrayList<String>();
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    if(qRow>=0){
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       
		       while (cellIterator.hasNext()){
					celda = cellIterator.next();
					Double xval;
					try{
					    xval = celda.getNumericCellValue();
						lista.add(String.valueOf(xval.intValue()));
					}catch(Exception e){
						try {
							  if(qCel==0){
							   xval=celda.getNumericCellValue();
							   lista.add(String.valueOf(xval.intValue()));
							  }  
							}catch(Exception e1) {
							   logger.debug(e1);
							}   
					}
					qCel++;
			  }
		   }
		   qRow++; 
		} 
		StringBuilder sbString = new StringBuilder("");
		for(String op : lista){
            sbString.append(op).append("S");
        }
		String temp =   sbString.toString();
		temp = temp.substring(0, temp.length() - 1);
		
	    strList = temp;
		return errores;
	}
	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.liquidaciones.interbanking"));
	}
	
}
