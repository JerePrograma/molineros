package ar.com.ospim.autorizaciones.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
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

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.RespuestaPreAutorizPSDTO;
import ar.com.ospim.autorizaciones.reportes.action.ReporteRespuestasPSaPreautorizacionesExcel;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;


public class UploadArchivoPreautorizacionesAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoPreautorizacionesAction.class);

	private List<String> errores = new ArrayList<String>();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);

		Boolean proceso=false;
		
		try {
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			logger.info("subiendo archivo :" + fileName);
			if (fileName != null) {
				File fileSelec = uploadReq.getFile("archivo");
				if ((fileName.startsWith("autorizaciones") || fileName.startsWith("Autorizaciones")) && fileName.endsWith(".xls")) {
					proceso=true;
					errores = procesarArchivoPreautorizaciones(actionRequest, fileSelec,fileName);
				}else if (fileName.startsWith("autorizaciones") && fileName.endsWith(".txt")) {
					proceso=true;
					errores = procesarRespuestasPreautorizaciones(actionRequest, fileSelec,fileName);	
				}else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}		
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			logger.error(e);
		}
		if (null!=errores && !errores.isEmpty()) {
			ErrorProcesandoArchivosAfipException e = new ErrorProcesandoArchivosAfipException();
			SessionErrors.add(actionRequest, e.getClass().getName());
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
		
		setForward(actionRequest, "portlet.autorizaciones.preautorizacion_procesa_archivo");
		
	}

	private List<String> procesarArchivoPreautorizaciones(ActionRequest actionRequest, File zip,String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);                                                   

		List<ArchivoPrevencion> lista= new ArrayList<ArchivoPrevencion>();
		Map<Integer, List<ArchivoPrevencion>> mSeccionales = new HashMap<Integer, List<ArchivoPrevencion>>();
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		rac = ReportesServiceUtil.getConfiguracion();
		
/*		
		actionRequest.removeAttribute("pagosImputadosSeguimientoSur");
*/		
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
//logger.debug(qRow);		    
		    if(qRow>0){
		       ArchivoPrevencion archivo = new ArchivoPrevencion();	
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       
		       while (cellIterator.hasNext()){
		    	   
				celda = cellIterator.next();
				try{
				
//				DIA     	Nro Doc    Nro.Aut  blanco    prestacion   nombre   estado Cant.Autorizaciones
//					0            1        2         3           4         5       6          7              
				Double xval;
				Date dval;
//				xval= celda.getNumericCellValue();
				if(qCel==0){
//					dval=celda.getDateCellValue();
//					archivo.setFecha(sdf.format(dval) );
//					archivo.setFecha(celda.getDateCellValue());
					archivo.setFecha(celda.toString());
				}else if(qCel==1){
//					xval=celda.getNumericCellValue();
//					archivo.setNroDocumento(xval.toString());
					archivo.setNroDocumento(celda.toString());
				}else if(qCel==2){
					archivo.setNroAutorizacion(celda.toString());
				}else if(qCel==3){
					archivo.setPrestacion(celda.toString());
				}else if(qCel==4){
					archivo.setPrestacionNombre(celda.toString());
				}else if(qCel==5){	
					archivo.setEstado(celda.toString());
				}else if(qCel==6){
					xval=celda.getNumericCellValue();
					xval=xval==null?0:xval;
					archivo.setCantidad(xval.intValue() );
				}
				}catch(Exception e){
					logger.debug(e);
				}
				qCel++;
				
			  }
		       
		      if(archivo.getCantidad()==null){
		    	  archivo.setCantidad(0);
		      }
		      if(archivo.getEstado()==null){
		    	  archivo.setEstado("");
		      }
		      if(archivo.getNroDocumento()!=null && !"".equalsIgnoreCase(archivo.getNroDocumento())){
		         lista.add(archivo); 
		      }   
		   }
		   qRow++; 
		} 

		
		if(lista.size()>0){
				
			for(ArchivoPrevencion s:lista){
			    List<Afiliado>la=EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(s.nroDocumento, "DU");
			    if(la.size()>0){
			    	Afiliado a = la.get(0);
			    	s.setAfiliado(a.getApellidoNombre());
			    	s.setSeccionalId(a.getSeccional().getId());
			    	s.setSeccionalDescripcion(a.getSeccional().getDescripcion());
			    }else{
			    	s.setAfiliado("No Encontrado en Padron");
			    	s.setSeccionalId(0);
			    	s.setSeccionalDescripcion("No Encontrado en Padron");
			    }
			    
			    Integer idSeccional=s.getSeccionalId()!=null?s.getSeccionalId():0;
			    
			    List<ArchivoPrevencion> l= mSeccionales.get(idSeccional);
				if(l==null) l=new ArrayList<ArchivoPrevencion>();
				l.add(s);
				mSeccionales.put(idSeccional, l);
			}
			
			String to="";
			logger.debug("Inicio Proceso Informe Prevencion ");
			for (Integer key : mSeccionales.keySet()) {
				List<ArchivoPrevencion> preAuts = mSeccionales.get(key);
				

                if(key==0){
                   to=WebKeysAutorizaciones.EMAIL_AUTORIZACIONES;
                }else {
                    List<ContactoElectronico> lc =  SeccionalServiceUtil.getInstance().buscarContactosSeccionalEmail(key);
				
				    for(ContactoElectronico ce:lc){
					  if(ce.getTipo().equals(ContactoElectronico.Tipo.EMAIL)){
						to=ce.getContacto();
						break;
					  }
				    }
                }
                
				if(StringUtils.checkEmpty(to)){
					to=WebKeysAutorizaciones.EMAIL_SISTEMAS;
				}
				
//to="dsulfaro@uoma.org.ar";
				
				if(preAuts.size()>0 && to.length()>0){
				   String sec = preAuts.get(0).getSeccionalDescripcion();	
				   generarInforme(to,sec,preAuts,rac);
				   logger.debug("Enviando Mail Proceso Informe Prevencion " + to);
				}
			}
			
			logger.debug("Grabando Lote Proceso Informe Prevencion");
			PreAutorizacionServiceUtil.saveLote(lista,user.getScreenName(),fileName);
			logger.debug("Fin Proceso Informe Prevencion ");
		}
				
		return errores;
	}
	
	
	private List<String> procesarRespuestasPreautorizaciones(ActionRequest actionRequest, File fileSel, String fileName)
			throws Exception {
		
	    User user = PortalUtil.getUser(actionRequest);                                                   

		List<RespuestaPreAutorizPSDTO> respuestas= new ArrayList<RespuestaPreAutorizPSDTO>();
		Map<Integer, List<RespuestaPreAutorizPSDTO>> mSeccionales = new HashMap<Integer, List<RespuestaPreAutorizPSDTO>>();
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
//		rac = ReportesServiceUtil.getConfiguracion();
		
		FileInputStream in = new FileInputStream(fileSel);

		BufferedReader scanner = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		
		RespuestaPreAutorizPSDTO dto = null;
		String line = null;
		int renglon = 0;
		
		String to="";
		Afiliado a = null;
		Seccional s = null;
		
		while ((line = scanner.readLine()) != null) {
			if(renglon > 0){ // saltea primer renglon de titulos columnas...
				dto = new RespuestaPreAutorizPSDTO(line);
				
//				sacamos los registros que son estado = 'P' 
//				y los estados R que solo traen cuil, sin otros datos
				if(dto.getAuthorizationStatus().equalsIgnoreCase("A") ||
					(dto.getAuthorizationStatus().equalsIgnoreCase("R") && dto.getMedicalPractice() != null) ||
					(dto.getAuthorizationStatus().equalsIgnoreCase("X") && dto.getMedicalPractice() != null) ||
					 dto.getAuthorizationStatus().equalsIgnoreCase("D") && dto.getTransactionId() != null ){
						
					respuestas.add(dto);
				}
			}
			renglon++;
		}
		
		if(respuestas.size()>0){
				
			logger.debug("Grabando Lote Proceso Informe Prevencion");
			
			try {
				PreAutorizacionServiceUtil.saveLoteRespuestasWS(respuestas,user.getScreenName(),fileName);
				
			
				for(RespuestaPreAutorizPSDTO rta : respuestas){
					
				    List<Afiliado> afiAux = EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(rta.getTributaryCodeNumber().substring(2, 10), "DU");
				    
				    if( afiAux .size()>0){
				    	a =  afiAux .get(0);
				    	rta.setAfiliadoApeyNom(a.getApellidoNombre());
				    	rta.setSeccional(new Seccional(a.getSeccional().getId(),a.getSeccional().getDescripcion()));
				    	
				    }else{
				    	rta.setAfiliadoApeyNom("No Encontrado en Padrón");
				    	rta.setSeccional(new Seccional(0,"No Encontrado en Padrón"));
				    }
				    			    
				    List<RespuestaPreAutorizPSDTO> seccionalRespuestas = mSeccionales.get(rta.getSeccional().getId_seccional());
				    
					if(seccionalRespuestas==null) {
						seccionalRespuestas=new ArrayList<RespuestaPreAutorizPSDTO>();
					}
					seccionalRespuestas.add(rta);
					mSeccionales.put(rta.getSeccional().getId_seccional(), seccionalRespuestas);
				}
				
				
				logger.debug("Inicio Proceso Informe Prevención ");
				for (Integer key : mSeccionales.keySet()) {
					List<RespuestaPreAutorizPSDTO> preAuts = mSeccionales.get(key);
					
	
	                if(key==0){
	                   to=WebKeysAutorizaciones.EMAIL_AUTORIZACIONES;
	                }else {
	                    List<ContactoElectronico> contactosSeccional =  SeccionalServiceUtil.getInstance().buscarContactosSeccionalEmail(key);
					
					    for(ContactoElectronico ce : contactosSeccional){
					    	
						  if(ce.getTipo().equals(ContactoElectronico.Tipo.EMAIL)){
							to=ce.getContacto();
							break;
						  }
					    }
	                }
	                
					if(StringUtils.checkEmpty(to)){
						to=WebKeysAutorizaciones.EMAIL_SISTEMAS;
					}
									
					if(preAuts.size()>0 && to.length()>0){
					   String sec = preAuts.get(0).getSeccional()!=null?preAuts.get(0).getSeccional().getDescripcion():"AFILIADO NO ESTA EN PADRON";	
					   generarInformeWS(to,sec,preAuts,rac);
					   logger.debug("Enviando Mail Proceso Informe Prevención " + to);
					}
				}
				
				logger.debug("Fin Proceso Informe Prevencion ");
				
			}catch (Exception e) {
				logger.error(e);
				errores.add(e.getMessage());
			}	
		}
				
		return errores;
	}
	
	private static void generarInforme(String to,String titulo,List<ArchivoPrevencion>preAuts,ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac){
    	HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAll =getStyleAll(wb);
		styleAll.setWrapText(true);
		HSSFSheet sheet = wb.createSheet("Autorizaciones");
	    
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);
		ArrayList<String> emails = new ArrayList<String>();
		String[] vTo = to.split(";");
		for(int i=0;i<vTo.length;i++){
			emails.add(vTo[i]);	
		}
		
		int index = createHeader(wb, sheet,titulo);
		  index++;
		
		for(ArchivoPrevencion p:preAuts){
			int column = 0;
			  HSSFRow row = sheet.createRow(index++);
			  
			  HSSFCell cell01 = row.createCell(column++);
			  cell01.setCellValue(new HSSFRichTextString(p.fecha ));
			  
			  HSSFCell cell09 = row.createCell(column++);
			  cell09.setCellValue(new HSSFRichTextString(p.getNroDocumento() ));
			  
			  HSSFCell cell02 = row.createCell(column++);
			  cell02.setCellValue(new HSSFRichTextString(p.getAfiliado() ));
			  
			  HSSFCell cell03 = row.createCell(column++);
			  cell03.setCellValue(new HSSFRichTextString(p.getNroAutorizacion()));
			  
			  HSSFCell cell04 = row.createCell(column++);
			  cell04.setCellValue(new HSSFRichTextString(p.getPrestacion()));
			  
			  HSSFCell cell05 = row.createCell(column++);
			  cell05.setCellValue(new HSSFRichTextString(p.getPrestacionNombre()));
				
			  HSSFCell cell06 = row.createCell(column++);
			  cell06.setCellValue(new HSSFRichTextString(p.getEstado()));
			  
		}
		
		for(int i=0;i<21;i++)
			     sheet.autoSizeColumn((short) i);
			
		String body ="Se adjunta planilla con la actualización de las autorizaciones que Prevención Salud tiene registradas, " +
			     "correspondientes a afiliados de la Seccional. "+
                "Por favor si tiene autorizaciones pendientes, busque en la planilla si Prevención indica un número de autorización. "+
			     "En ese caso, y después de verificar que no haya recibido la autorización, debe solicitar a " +
                "autorizaciones@ospim.org.ar que le reenvíen la misma, para poder cerrar el caso en el Portal Molineros y " +
			     "entregarle la autorización al afiliado. " +
                " Muchas gracias.";
		
//		MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails,"Para revisar pedidos de autorización pendientes",
//					  body, wb, "Autorizaciones_pendientes_"+ titulo+".xls");
		
		EnviaEmailsThread.enviarMailDesatendido("Para revisar pedidos de autorización pendientes", body, emails, wb, "Autorizaciones_pendientes_"+ titulo+".xls");
    }

	
	private static void generarInformeWS(String to,String titulo,List<RespuestaPreAutorizPSDTO> rtasPreAutoriz,ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac){
    	
		HSSFWorkbook wb = ReporteRespuestasPSaPreautorizacionesExcel.generaReporte(to, titulo, rtasPreAutoriz);
		
		ArrayList<String> emails = new ArrayList<String>();
		String[] vTo = to.split(";");
		for(int i=0;i<vTo.length;i++){
			emails.add(vTo[i]);	
		}
			
		String body ="Se adjunta planilla con la actualización de las autorizaciones que Prevención Salud tiene procesadas, correspondientes a afiliados de la Seccional. \r\n" +
				     "Por favor verifique los casos incluidos y de corresponder, gestione la entrega de las autorizaciones a los afiliados. \r\n" + 
				     "Muchas gracias.";
		
//		MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails,"Informe de Autorizaciones de Prevención Salud",
//					  body, wb, "Respuestas_Autorizaciones_"+ titulo+".xls");
		EnviaEmailsThread.enviarMailDesatendido("Informe de Autorizaciones de Prevención Salud", body, emails, wb, "Respuestas_Autorizaciones_"+ titulo+".xls");
    }
	
	protected static HSSFCellStyle getStyleAll(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) size);
		styleAll.setFont(font);
		return styleAll;
	}
	
	protected static void setThinBorders(HSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}
	
    private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet,String titulo) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar gmtMenos3 = DateUtils.getCalendarGMTMenos3();

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Autorizaciones de " + titulo));
		cell.setCellStyle(styleHeaderEnca);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		
		HSSFRow row1 = sheet.createRow(index++);

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(gmtMenos3.getTime())));
		
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Fecha"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Documento"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Autorizacion"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Prestacion"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Nombre Prestacion"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Estado"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		return index;
	}
    
    protected static HSSFCellStyle getStyleHeaderWithBorderNoColor(
			HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
		styleHeader.setAlignment(HorizontalAlignment.CENTER);
		return styleHeader;
	}
    
    protected static HSSFCellStyle getStyleBold(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) size);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}
    
    protected static HSSFCellStyle getStyleHeaderWithBorderLeftNoColor(
			HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
		styleHeader.setAlignment(HorizontalAlignment.LEFT);
		return styleHeader;
	}
    
    protected static HSSFCellStyle getStyleMoney(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}
    
    protected static HSSFCellStyle getStyleAll(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		return styleAll;
	}

	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.preautorizacion_procesa_archivo"));
	}
	
	public class ArchivoPrevencion{
		String fecha;
		String nroDocumento;
		String nroAutorizacion;
		String prestacion;
		String prestacionNombre;
		String estado;
		Integer cantidad;
		String Afiliado;
		Integer seccionalId;
		String seccionalDescripcion;
		
		public String getFecha() {
			return fecha;
		}
		public void setFecha(String fecha) {
			this.fecha = fecha;
		}
		public String getNroDocumento() {
			return nroDocumento;
		}
		public void setNroDocumento(String nroDocumento) {
			this.nroDocumento = nroDocumento;
		}
		public String getNroAutorizacion() {
			return nroAutorizacion;
		}
		public void setNroAutorizacion(String nroAutorizacion) {
			this.nroAutorizacion = nroAutorizacion;
		}
		public String getPrestacion() {
			return prestacion;
		}
		public void setPrestacion(String prestacion) {
			this.prestacion = prestacion;
		}
		public String getPrestacionNombre() {
			return prestacionNombre;
		}
		public void setPrestacionNombre(String prestacionNombre) {
			this.prestacionNombre = prestacionNombre;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
		public Integer getCantidad() {
			return cantidad;
		}
		public void setCantidad(Integer cantidad) {
			this.cantidad = cantidad;
		}
		public String getAfiliado() {
			return Afiliado;
		}
		public void setAfiliado(String afiliado) {
			Afiliado = afiliado;
		}
		public Integer getSeccionalId() {
			return seccionalId;
		}
		public void setSeccionalId(Integer seccionalId) {
			this.seccionalId = seccionalId;
		}
		public String getSeccionalDescripcion() {
			return seccionalDescripcion;
		}
		public void setSeccionalDescripcion(String seccionalDescripcion) {
			this.seccionalDescripcion = seccionalDescripcion;
		}
		
		
	}

}
