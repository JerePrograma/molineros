package ar.com.ospim.autorizaciones.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos;


public class UploadTransferenciasIntegracionAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoIntegracionAction.class);

	private List<String> errores = new ArrayList<String>();
	
	private class MovimientoTransferencia{
		Date fechaMovimiento;
		Date fechaValor;
		String cuit;
		String referencia;
		Double importe;
		Integer ordenPagoId;
		
		public Date getFechaMovimiento() {
			return fechaMovimiento;
		}
		public void setFechaMovimiento(Date fechaMovimiento) {
			this.fechaMovimiento = fechaMovimiento;
		}
		public Date getFechaValor() {
			return fechaValor;
		}
		public void setFechaValor(Date fechaValor) {
			this.fechaValor = fechaValor;
		}
		public String getCuit() {
			return cuit;
		}
		public void setCuit(String cuit) {
			this.cuit = cuit;
		}
		public String getReferencia() {
			return referencia;
		}
		public void setReferencia(String referencia) {
			this.referencia = referencia;
		}
		public Double getImporte() {
			return importe;
		}
		public void setImporte(Double importe) {
			this.importe = importe;
		}
		public Integer getOrdenPagoId() {
			return ordenPagoId;
		}
		public void setOrdenPagoId(Integer ordenPagoId) {
			this.ordenPagoId = ordenPagoId;
		}
		
	}
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);

		Boolean proceso=false;
		errores.clear();
		try {
			
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
//			String entidad =ParamUtil.getString(actionRequest, "tercerizadora");
			logger.info("subiendo archivo :" + fileName);
			if (fileName != null ) {
				File fileSelec = uploadReq.getFile("archivo");
				if ((fileName.startsWith("Movimientos Conformados") || 
						fileName.startsWith("movimientos conformados")) && (fileName.endsWith(".csv") )) {
					proceso=true;
					errores = procesarTransferenciaIntegracion(actionRequest, fileSelec,fileName);
				}else if ((fileName.endsWith(".xls") )) {
						proceso=true;
						errores = procesarInconsistenciasTransferenciaIntegracion(actionRequest, fileSelec,fileName);
					
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
		
		setForward(actionRequest, "portlet.autorizaciones.integracion_upload_transferencias");
		
	}

	private List<String> procesarTransferenciaIntegracion(ActionRequest actionRequest, File zip,String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    
		List<MovimientoTransferencia> listaCuit= new ArrayList<MovimientoTransferencia>();
		List<MovimientoTransferencia> listaSinCuit= new ArrayList<MovimientoTransferencia>();
		
		FileInputStream file = new FileInputStream(zip);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file,"UTF-8"));
		String line = null;
		BigDecimal bd;
		boolean resultado = false;
		String cuit="";
		Double importe=0D;
		String referencia="";
		Integer nroLote =IntegracionServiceUtil.ultimoLoteTransferenciaProcesado();
		nroLote++;
		while ((line = reader.readLine()) != null) {
			cuit="";
			
			if( line.indexOf("TRANSF.") !=-1 ||
				//line.indexOf("TR.") !=-1 ||
				line.indexOf("DEB.TRAN.INTERB-LINK")!=-1 ){
				
				MovimientoTransferencia det= new MovimientoTransferencia();
				String[] vLine = line.split(";");
				
				Date dv0 = sdf.parse(vLine[0]);
				Date dv1 = sdf.parse(vLine[1]);
				importe = Double.parseDouble(vLine[2].replace(".","").replace(",", ".") );
				referencia = vLine[3];
				
				det.setFechaMovimiento(dv0);
				det.setFechaValor(dv1);
				det.setImporte(importe);
				det.setReferencia(referencia);
				
				if(vLine[4].indexOf("TR.")!=-1 || vLine[4].indexOf("TRANSF.")!=-1 ) {
		          String[] vAux = vLine[4].split("CUIL");
		          if(vAux.length>0) {
		        	  cuit=vAux[1].trim().replace(" ", "");
		        	  det.setCuit(cuit);
		        	  listaCuit.add(det);
		          }
				}else {
					listaSinCuit.add(det);
				}
			}						
		}
		 
		if(listaCuit.size()>0){
			for(MovimientoTransferencia m: listaCuit) {
				resultado=IntegracionServiceUtil.existeMovimientoTransferencia(m.getFechaMovimiento(),m.getFechaValor(),
						            m.getImporte(),m.getCuit(), m.getReferencia());
				if(!resultado) {	
				   List<OrdenPagoOspim>	lop = IntegracionServiceUtil.proponeOrdenPagoTransferenciaBancaria(m.getCuit(),
						   m.getFechaValor(), m.getImporte());
				   if( lop.size()>0 ) {
					   m.setOrdenPagoId(lop.get(0).getId());
				   }
				   IntegracionServiceUtil.insertaIntegracionTranferencia(nroLote,
						   m.getFechaValor(), m.getFechaMovimiento(),m.getImporte(),m.getReferencia(),null,m.getCuit(),m.getOrdenPagoId(),
						   user.getScreenName(), null);
				}   
			}
		}
		if(listaSinCuit.size()>0){
			
			for(MovimientoTransferencia m: listaSinCuit) {
				resultado=IntegracionServiceUtil.existeMovimientoTransferencia(m.getFechaMovimiento(),m.getFechaValor(),
						            m.getImporte(),null, m.getReferencia());
				if(!resultado) {	
				   List<OrdenPagoOspim>	lop = IntegracionServiceUtil.proponeOrdenPagoTransferenciaBancaria(null,
						   m.getFechaValor(), m.getImporte());
				   if( lop.size()>0 ) {
					   m.setOrdenPagoId(lop.get(0).getId());
				   }
				   
				   IntegracionServiceUtil.insertaIntegracionTranferencia(nroLote,
						   m.getFechaValor(), m.getFechaMovimiento(),m.getImporte(),m.getReferencia(),null,null,m.getOrdenPagoId(),
						   user.getScreenName(), null);
				}   
			}
			
		}
		return errores;
	}
	
	
	private List<String> procesarInconsistenciasTransferenciaIntegracion(ActionRequest actionRequest, File zip,String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);   
	    SimpleDateFormat sdf = new SimpleDateFormat();

		List<MovimientoTransferencia> lista= new ArrayList<MovimientoTransferencia>();
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
//		Iterator<HSSFRow> rowIterator = sheet.iterator();

		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    if(qRow>=0){
		       MovimientoTransferencia archivo = new MovimientoTransferencia();	
		       
		       //Iterator<HSSFCell> cellIterator = row.cellIterator();
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
				  
				  if(qCel==0){//Nro Orden Pago
					xval=celda.getNumericCellValue();
					archivo.setOrdenPagoId(xval.intValue()); 
				  }else if(qCel==1){//Fecha Transferencia
					try {  
					  dval=celda.getDateCellValue();
					  archivo.setFechaMovimiento(dval);
					  archivo.setFechaValor(dval);
					}catch(Exception e) {}
					
				  }
				 }catch(Exception e){
					logger.debug(e);
				 }
				qCel++;
				
			  }
		      archivo.setReferencia("Ingreso Manual OP "+archivo.getOrdenPagoId().toString());
		      archivo.setImporte(0D);
		      archivo.setCuit(archivo.getOrdenPagoId().toString());
		      lista.add(archivo);
		   }
		   qRow++; 
		} 

		
		if(lista.size()>0){
			Integer nroLote =IntegracionServiceUtil.ultimoLoteTransferenciaProcesado();
			nroLote++;
			boolean resultado = false;
			for(MovimientoTransferencia m: lista) {
				resultado=IntegracionServiceUtil.existeMovimientoTransferencia(m.getFechaMovimiento(),m.getFechaValor(),
						            m.getImporte(),m.getCuit(), m.getReferencia());
				if(!resultado) {	
				   IntegracionServiceUtil.insertaIntegracionTranferencia(nroLote,
						   m.getFechaValor(), m.getFechaMovimiento(),m.getImporte(),m.getReferencia(),null,m.getCuit(),m.getOrdenPagoId(),
						   user.getScreenName(), null);
				}   
			}
		}
				
		return errores;
	}

	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.integracion_upload_transferencias"));
	}
	
}
