package ar.com.ospim.autorizaciones.action;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import ar.com.ospim.autorizaciones.beans.BusquedaSeguimientoSurFiltro;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;



public class UploadArchivoSeguimientoSurAction extends PortletAction {

	public static final int EN_ANALISIS_SUR = 3;
	public static final int PENDIENTE_DE_PAGO = 7;
	public static final int PAGO_IMPUTADO = 15;
	public static final int PAGADO_POR_MOV_BANCARIO = 9;
	public static final int CAMBIO_MASIVO_DE_ESTADO= 16;
	public static final int PENDIENTE_DE_PRESENTAR= 12;
	public static final int CON_TURNO_SUR= 1;
	
	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoSeguimientoSurAction.class);

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
				File zip = uploadReq.getFile("archivo");
				if (fileName.startsWith("enanalisissur112608") && fileName.endsWith(".xls")) {
					proceso=true;
					errores = procesarArchivoAnalisisSUR(actionRequest, zip);
				}else if (fileName.startsWith("pagadossur112608") && fileName.endsWith(".xls")) {
					proceso=true;
					errores = procesarArchivoPagosSUR(actionRequest, zip);
				}else if (fileName.toLowerCase().startsWith("reporteseguimientosur") && fileName.endsWith(".xls")) {
					proceso=true;
					errores = procesarArchivoUpdateEstadosMasivoSUR(actionRequest, zip);
				}else if (fileName.toLowerCase().startsWith("cambiomasivoestadosur") && fileName.endsWith(".xls")) {
					proceso=true;
					errores = procesarArchivoCambioEstadoSUR(actionRequest, zip);
				}else{
					errores.add("El nombre del archivo no coincide con los procesos habilitados");
				}
			}		
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
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
		
		setForward(actionRequest, "portlet.autorizaciones.opcion_pagos_seguimientosur");
		
	}

	private List<String> procesarArchivoAnalisisSUR(ActionRequest actionRequest, File zip)
			throws Exception {
		
        User user = PortalUtil.getUser(actionRequest);                                                  
		
		List<SeguimientoSur> lista= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> resultBusq= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> novedades= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> noEncontrados= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> existentes= new ArrayList<SeguimientoSur>();
		actionRequest.removeAttribute("pagosImputadosSeguimientoSur");
		
		SeguimientoSur ss = null;
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {

        	Row currentRow = rowIterator.next();
        	Iterator<Cell> cellIterator = currentRow.iterator();
        	ss = new SeguimientoSur();
//        	evitamos primera fila de títulos // Si viene vacia no hace nada, agarra la primera que tenga datos.	
        	while (/*currentRow.getRowNum() >1 &&*/ cellIterator.hasNext()) {

////			Solicitud	Expediente	Fecha	Normativa	Patologia	Nombre	Nro	Importe	Importe reconocido PAGO PROPORCIONAL FECHA PAGO PROP
////			0            1        2         3           4         5       6    7             8				  9             10
        	
        		Cell currentCell = cellIterator.next();
        		int cellIndex = currentCell.getColumnIndex();
        		
        		Double xval;
			
        		switch (cellIndex) {
				case 0:
					xval= currentCell.getNumericCellValue();
					ss.setNro_solicitud_sur(String.valueOf(xval.longValue()));
					break;
				case 1:
					try{
						xval=currentCell.getNumericCellValue();					
					}catch(NumberFormatException e){
	//					nada,x le seteo un 0
						xval = 0D;
					}
					ss.setNro_expediente(String.valueOf(xval.intValue()));
					break;
				case 2:	
					ss.setFecha_ingreso_area_sur(currentCell.getDateCellValue());
					break;
				case 5:
					ss.setAfiliadoNombre(currentCell.toString());
					break;
				case 6:	
					xval= currentCell.getNumericCellValue();
					ss.setCuilTitular(String.valueOf(xval.longValue()));	
					break;
				case 8:
					ss.setImporteReconocido(currentCell.getNumericCellValue());
					break;
				case 9:
					try {
						ss.setProporcionalAdelantado(currentCell.getNumericCellValue());
					} catch (Exception e) {
						ss.setProporcionalAdelantado(null);
					}
					break;
				case 10:
					try {
						ss.setFechaProporcionalAdelantado(currentCell.getDateCellValue());
					} catch (Exception e) {
						ss.setFechaProporcionalAdelantado(null);
					}
					break;	
				} // fin switch

        	}
        	if(ss != null) {
        		lista.add(ss);
        		logger.debug("Linea analisis xls: " + ss.toString());
        	}
        	
        }	

		boolean estaActualizadoRespectoDeSur = true;
		
		if(lista.size()>0){
		     
			BusquedaSeguimientoSurFiltro filtro = new BusquedaSeguimientoSurFiltro(0, 0, 0, 0, /*s.getNro_solicitud_sur(),*/ null, 
					   	null, null, null, null, null, null, null, false, null, null, null, null,0 , 
					   	null,null,0,null,null,null,null,null,null,null, null,null); 
			   
		   for(SeguimientoSur s:lista){
			   
			   filtro.setNroSolicitud(s.getNro_solicitud_sur());
						
//			   resultBusq = SeguimientoSurServiceUtil.getListaSeguimientoSur(0, 0, 0, 0, s.getNro_solicitud_sur(), 
//					   null, null, null, null, null, null, null, false, null, null, null, null,0 , null,null,0,null,null,null,null,null,null,null);
			   resultBusq = SeguimientoSurServiceUtil.getListaSeguimientoSur(filtro);
			   
			   if(resultBusq.size()>0){
				   
				   ss = resultBusq.get(0);
				   
				   if(!ss.getNro_expediente().equalsIgnoreCase(s.getNro_expediente())){
					   estaActualizadoRespectoDeSur = false;
					   ss.setNro_expediente(s.getNro_expediente());
				   }
				   if(!ss.getImporteReconocido().equals(s.getImporteReconocido())){
					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
					   ss.setImporteReconocido(s.getImporteReconocido());
				   }
				   if(!ss.getProporcionalAdelantado().equals(s.getProporcionalAdelantado())){
					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
					   ss.setProporcionalAdelantado(s.getProporcionalAdelantado());
				   }
				   if(ss.getFechaProporcionalAdelantado()!=null && 
						   s.getFechaProporcionalAdelantado() != null &&
						   ss.getFechaProporcionalAdelantado().getTime() == s.getFechaProporcionalAdelantado().getTime()
//						   .compareTo(s.getFechaProporcionalAdelantado()) != 0 
						   ){
//					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
					   ss.setFechaProporcionalAdelantado(s.getFechaProporcionalAdelantado());
				   }else{
					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
					   ss.setFechaProporcionalAdelantado(s.getFechaProporcionalAdelantado()!=null?s.getFechaProporcionalAdelantado():null);
				   }
				   SeguimientoSurEstado estadoUlti = SeguimientoSurServiceUtil.ultimoEstadoSeguimientoSUR(ss.getId());
				   if(PENDIENTE_DE_PAGO != (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)){
					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
				   }
				   
				   if(estaActualizadoRespectoDeSur){
					   ss.setTipoRegistro("EXI");
					   existentes.add(ss);
				   }else{ // nuevos o actualizados en algún campo.
					   ss.setTipoRegistro("NUE");
					   ss.setFecha_ingreso_area_sur(s.getFecha_ingreso_area_sur());
					   novedades.add(ss);
				   }
				   
				   ss.setBaja_fecha(s.getFecha_ingreso_area_sur());
				   
			   }else{
				 s.setBaja_fecha(s.getFecha_ingreso_area_sur()); 
				 s.setTipoRegistro("NOE");
				 noEncontrados.add(s);  
			   }
		   }
		   Integer nroLote = SeguimientoSurServiceUtil.proximoNroLotePago(null);

		   novedades.addAll(noEncontrados);
		   novedades.addAll(existentes);
		   
		   if(novedades.size()>0){
		       SeguimientoSurServiceUtil.actualizarPagoProporcional(novedades, nroLote, user.getScreenName(), PENDIENTE_DE_PAGO, null);
		       actionRequest.setAttribute("pagosImputadosSeguimientoSur", novedades);
		   }
		}
				
		return errores;
	}
	
	private List<String> procesarArchivoPagosSUR(ActionRequest actionRequest, File zip)
			throws Exception {
		
		User user = PortalUtil.getUser(actionRequest);                                                  
		
		List<SeguimientoSur> lista= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> resultBusq= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> resultBusqImputados= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> novedades= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> noEncontrados= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> existentes= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> imputados= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> vencidos= new ArrayList<SeguimientoSur>();
		
		actionRequest.removeAttribute("pagosImputadosSeguimientoSur");
		Integer nroLote = null;
		
		/**
		 * actualizarPagoPorTranfBancaria
		 * Este proceso revisa los expedientes en estado pago imputado y a los 9 meses los pasa a estado Pago por transferencia bancaria
		 * 
		 * */
		actualizarPagoPorTranfBancaria(actionRequest, user, resultBusqImputados);
		
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		SeguimientoSur ss = null;
		SeguimientoSurEstado seguimEstado = null;
		
        while (rowIterator.hasNext()) {

        	Row currentRow = rowIterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            ss = new SeguimientoSur();
            
//            logger.debug("currentRow " + currentRow.getRowNum() );
            
            //Evitar primera fila con titulos
            if(currentRow.getRowNum() == 0) {
            	continue;
            }
            while (cellIterator.hasNext()) {

////			Solicitud	Expediente	Fecha	Normativa	Patologia	Nombre	Nro	Importe	Importe reconocido	Fecha pago
////			0            1        2         3           4         5       6    7             8				  9  
            	
            	Cell currentCell = cellIterator.next();
            	int cellIndex = currentCell.getColumnIndex();
            	Double xval;
            	
            	switch (cellIndex) {
				case 0:
					xval= currentCell.getNumericCellValue();
					ss.setNro_solicitud_sur(String.valueOf(xval.longValue()));
					break;
				case 1:
					try{
						xval=currentCell.getNumericCellValue();					
					}catch(NumberFormatException e){
//						nada,x le seteo un 0
						xval = 0D;
					}
					ss.setNro_expediente(String.valueOf(xval.intValue()));
					break;	
				case 2:
					ss.setFecha_ingreso_area_sur(currentCell.getDateCellValue());
					break;
				case 5:
					ss.setAfiliadoNombre(currentCell.toString());
					ss.setCodigoHIV(currentCell.toString());
					break;	
				case 6:
					xval= currentCell.getNumericCellValue();
					ss.setCuilTitular(String.valueOf(xval.longValue()));	
					break;
				case 8:
					ss.setImporteReconocido(currentCell.getNumericCellValue());
					break;	
				case 9:
					seguimEstado = new SeguimientoSurEstado();
					seguimEstado.setIdEstado(PAGO_IMPUTADO);
					seguimEstado.setFechaEstado(currentCell.getDateCellValue());
					ss.setEstados(new ArrayList<SeguimientoSurEstado>());
					ss.getEstados().add(seguimEstado);
					break;	
					
				}
            }
            
            if(ss != null && StringUtils.checkNotEmpty(ss.getNro_solicitud_sur())) {
        		lista.add(ss);
        		logger.debug("Linea pago xls: " + ss.toString());
        	}
//            else {
//        		continue;
//        	}

        }
		
		if(lista.size()>0){
		   
			BusquedaSeguimientoSurFiltro filtro = new BusquedaSeguimientoSurFiltro(0, 0, 0, 0, /*s.getNro_solicitud_sur(),*/ null, 
				   	null, null, /*s.getNro_expediente()*/ null, null, null, null, null, false, null, null, null, null,0 , 
				   	null,null,0,null,null,null,null,null,null,null, null, null); 
			
		   for(SeguimientoSur s:lista){
			   
			   filtro.setNroSolicitud(s.getNro_solicitud_sur());
			   filtro.setNroExpediente(s.getNro_expediente());
			   
//			   resultBusq = SeguimientoSurServiceUtil.getListaSeguimientoSur(0, 0, 0, 0, s.getNro_solicitud_sur(), 
//					   null, null, s.getNro_expediente(), null, null, null, null, false, null, null, null, null,0,null,null,0,null,null,null,null,null,null,null);
			   
			   resultBusq = SeguimientoSurServiceUtil.getListaSeguimientoSur(filtro);
					   
			   if(resultBusq.size()>0){
				   
				   ss = resultBusq.get(0);
				   
				   SeguimientoSurEstado estadoUlti = SeguimientoSurServiceUtil.ultimoEstadoSeguimientoSUR(ss.getId());
				   if(PAGO_IMPUTADO != (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)
						   && PAGADO_POR_MOV_BANCARIO != (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)
					){ // nuevo estado pago imputado.
					   ss.setTipoRegistro("NUE");
					   ss.setEstados(s.getEstados());
					   ss.setProporcionalAdelantado(s.getImporteReconocido());
					   novedades.add(ss);
				   }else{ 
					   ss.setTipoRegistro("EXI");
					   existentes.add(ss);
					   
				   }
				   
				   ss.setBaja_fecha(s.getFecha_ingreso_area_sur());
				   
			   }else{
				 s.setBaja_fecha(s.getFecha_ingreso_area_sur()); 
				 s.setTipoRegistro("NOE");
				 noEncontrados.add(s);  
			   }
		   }
		   
			Calendar inicioImputados = Calendar.getInstance(); // 6 meses p atras
			inicioImputados.add(Calendar.MONTH, -6); 
			Calendar corteImputados = Calendar.getInstance(); //hoy
		   /* revisamos los que si estaban en pago imputado y ahora no estan */
		   imputados = SeguimientoSurServiceUtil.getListaSeguimientoSurImputados(0, 0, 0, 0, null, 
				   null, null, null, null, null, null, null, false, null, null, null, 
				   PAGO_IMPUTADO+",",inicioImputados.getTime(),corteImputados.getTime());
		   
		   for(SeguimientoSur s: imputados){
			   if(!lista.contains(s)){
				   s.setBaja_fecha(s.getFecha_ingreso_area_sur());
				   s.setTipoRegistro("VEN");
				   vencidos.add(s);
			   }
		   }
		   
//		   boolean encontro;
//		   for(SeguimientoSur s: imputados){
//			   encontro=false;  
//			   for(SeguimientoSur l:lista){
//				   if(s.getNro_expediente().equalsIgnoreCase(l.getNro_expediente())){
//					   encontro=true;
//					   break;
//				   }
//			   }
//			   if(!encontro){
//				  vencidos.add(s); 
//			   }
//		   }
		   /* fin revisar */
		   
		   nroLote = SeguimientoSurServiceUtil.proximoNroLotePago(null);

		   novedades.addAll(noEncontrados);
		   novedades.addAll(existentes);
		   novedades.addAll(vencidos);
		   
		   
		   
		   if(novedades.size()>0){
		       SeguimientoSurServiceUtil.actualizarPagoImputado(novedades, nroLote, user.getScreenName(), PAGO_IMPUTADO, null);
		       actionRequest.setAttribute("pagosImputadosSeguimientoSur", novedades);
		   }

		}
				
		return errores;
		
	}

	private void actualizarPagoPorTranfBancaria(ActionRequest actionRequest, User user, List<SeguimientoSur> novedades)
			throws SystemException, SQLException, Exception {
		List<SeguimientoSur> resultBusqImputados;
		Integer nroLote;
		//		los expedientes en estado pago imputados durante 9 meses (como si estuvieran embarazados)
		//		se pasan automaticamente a Pago por Transf Bancaria.
				Calendar inicioImputados = Calendar.getInstance();
				inicioImputados.add(Calendar.YEAR, -5); 
				Calendar corteImputados = Calendar.getInstance();
				corteImputados.add(Calendar.MONTH, -9); 
				
//				FIXME cambio nro x null
				resultBusqImputados = SeguimientoSurServiceUtil.getListaSeguimientoSurImputados(0, 0, 0, 0, null, 
						   null, null, null, null, null, null, null, false, null, null, null, 
						   PAGO_IMPUTADO+",",inicioImputados.getTime(),corteImputados.getTime());
				
				logger.debug("EXP.SUR. RESULTADOS PAGO IMPUTADO: " + resultBusqImputados.size());
				
				if(resultBusqImputados.size()>0){
						
						for (Iterator<SeguimientoSur> iterator = resultBusqImputados.iterator(); iterator.hasNext();) {
							SeguimientoSur ssi =  iterator.next();
							SeguimientoSurEstado seguimEstado = new SeguimientoSurEstado();
							seguimEstado.setIdEstado(PAGADO_POR_MOV_BANCARIO);
							seguimEstado.setFechaEstado(Calendar.getInstance().getTime());
							
							ssi.setEstados(new ArrayList<SeguimientoSurEstado>());
							ssi.getEstados().add(seguimEstado);
							ssi.setTipoRegistro("NUE");
							ssi.setBaja_fecha(ssi.getFecha_ingreso_area_sur());
						}
					
					
					   nroLote = SeguimientoSurServiceUtil.proximoNroLotePago(null);
				       SeguimientoSurServiceUtil.actualizarPagoImputado(resultBusqImputados, nroLote, user.getScreenName(), PAGADO_POR_MOV_BANCARIO, null);
				       actionRequest.setAttribute("pagosImputadosSeguimientoSur", novedades);
				}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.autorizaciones.opcion_pagos_seguimientosur"));
	}

	
	private List<String> procesarArchivoUpdateEstadosMasivoSUR(ActionRequest actionRequest, File zip)
			throws Exception {
		
        User user = PortalUtil.getUser(actionRequest);                                                  
        SeguimientoSur seguimiento = null;
		List<SeguimientoSur> lista= new ArrayList<SeguimientoSur>();		
		SeguimientoSurEstado seguimEstado = null;
		SeguimientoSur seguiDatosExtras = null; 
		actionRequest.removeAttribute("pagosImputadosSeguimientoSur");
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		int idSeguimientoInformado =0;
		int idEstadoDelEstadoTexto=0;
		SeguimientoSurEstado estadoUlti =null;
		
		Row row;
		Integer qRow=0; //saltear 2 filas de titulos
		
		List<ModalidadAtencion> listaDeEstadosSeguimientoSur = TraeListasServiceUtil.getEstadosSeguimientoSur();
		
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    
		    if(qRow > 1){ // qRow 0 y qRow 1 son titulos
		       seguimiento = new SeguimientoSur();
		       seguimEstado = new SeguimientoSurEstado();
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       while (cellIterator.hasNext()){
				celda = cellIterator.next();
				celda.getColumnIndex();
//				Id Seguimiento  Fecha Estado 	Estado Texto 
//					   26             27             28      
				
				if(qCel==25){
					try{
						idSeguimientoInformado =Integer.valueOf(celda.toString());						
					}catch(NumberFormatException e){
						idSeguimientoInformado = 0;
					}
					   seguiDatosExtras = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId(idSeguimientoInformado );
					   if(seguiDatosExtras!=null){ // actualiza datos para tabla informacion de procesados 
						   seguimiento.setAfiliadoNombre(seguiDatosExtras.getAfiliadoNombre()  );
						   seguimiento.setNro_expediente(seguiDatosExtras.getNro_expediente() );
						   seguimiento.setNro_solicitud_sur(seguiDatosExtras.getNro_solicitud_sur() );
						   seguimiento.setCuilTitular(seguiDatosExtras.getCuilTitular() );
						   seguimiento.setTipoRegistro(seguiDatosExtras.getTipoRegistro() );
					   }
					   seguimiento.setId(idSeguimientoInformado);					
				}else if(qCel==26){
					try {
						seguimEstado.setFechaEstado(celda.getDateCellValue());
					} catch (Exception e) {
						seguimEstado.setFechaEstado(null);
					}
					
				}else if(qCel==27){
					try {
						idEstadoDelEstadoTexto = buscaIdEstadoxTexto(listaDeEstadosSeguimientoSur , celda.toString() );
						estadoUlti = SeguimientoSurServiceUtil.ultimoEstadoSeguimientoSUR(seguimiento.getId());
						if(seguimEstado.getFechaEstado()!=null && idEstadoDelEstadoTexto != (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)){							
							seguimEstado.setIdEstado(idEstadoDelEstadoTexto) ;
							seguimiento.setTipoRegistro("EXI");
						}else{
							seguimEstado.setIdEstado(0) ;
							seguimiento.setTipoRegistro("NOE");
						}
					} catch (Exception e) {
						seguimEstado.setIdEstado(0) ;
						seguimiento.setTipoRegistro("NOE");
					}
				}
				qCel++;
			  }
		       logger.debug("Linea analisis xls: " + seguimiento.toString());
		       
		       seguimiento.setEstados(new ArrayList<SeguimientoSurEstado>());
		       seguimEstado.setIdMotivo(0);
		       seguimEstado.setUsuario(user.getScreenName());
		       seguimEstado.setObservaciones("");
		       
		       seguimiento.getEstados().add(seguimEstado);
		       lista.add(seguimiento);
		   }
		   qRow++; 
		} 
		Connection connection = null;
		connection = ConnectionHelper.getConnection();
		connection.setAutoCommit(false);
		try {	
			if(lista.size()>0){		   
			    SeguimientoSurServiceUtil.updateEstadosDesdeListaSeguimientoSur (lista,  user.getScreenName(),  connection);
			    Integer nroLote = SeguimientoSurServiceUtil.proximoNroLotePago(null);			    
			    SeguimientoSurServiceUtil.actualizarPagoImputado(lista, nroLote, user.getScreenName(), CAMBIO_MASIVO_DE_ESTADO, connection);
			    actionRequest.setAttribute("pagosImputadosSeguimientoSur", lista);
			}		
				connection.commit();
				return errores;	
		    } catch (Exception e) {
			  ConnectionHelper.rollback(connection);	
			  throw e;	
		    } finally {
			  ConnectionHelper.cerrar(connection);
		    }
		
	}

	public int  buscaIdEstadoxTexto(List<ModalidadAtencion>  ListaEstados , String textoEstadoExcel )
	{
		int idEstadoEncontrado =0 ;
		 for(ModalidadAtencion s: ListaEstados){
			   if (s.getDescripcion().equals(textoEstadoExcel)){
				   idEstadoEncontrado = s.getId();				   
			   }
		   }
			return idEstadoEncontrado  ;	
	}
	
	private List<String> procesarArchivoCambioEstadoSUR(ActionRequest actionRequest, File zip)
			throws Exception {
		
		User user = PortalUtil.getUser(actionRequest);                                                  
		
		List<SeguimientoSur> lista= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> resultBusq= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> novedades= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> noEncontrados= new ArrayList<SeguimientoSur>();
		List<SeguimientoSur> existentes= new ArrayList<SeguimientoSur>();
		actionRequest.removeAttribute("estadoMasivoSeguimientoSur");
		
		FileInputStream file = new FileInputStream(zip);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		SeguimientoSur ss = null;
		
		Row row;
		Integer qRow=0; 
		
		int idSeguimientoInformado =0;
		int idEstadoDelEstadoTexto=0;
		SeguimientoSurEstado estadoUlti =null;
		SeguimientoSurEstado seguimEstado = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fechaEstado = null;
		List<ModalidadAtencion> listaDeEstadosSeguimientoSur = TraeListasServiceUtil.getEstadosSeguimientoSur();
		
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    
//		    if(qRow > 0){ // qRow 0 y qRow 1 son titulos
		        
		       SeguimientoSur seguimiento = new SeguimientoSur();
		       List<SeguimientoSurEstado> estadosNuevos = new ArrayList<SeguimientoSurEstado>();
		       
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       while (cellIterator.hasNext()){
					celda = cellIterator.next();
					celda.getColumnIndex();
	//				Solicitud	
	//					0       
					Double xval;
	//				String nroExp = null;
					
					if(qCel==0){
						xval= celda.getNumericCellValue();
						seguimiento.setNro_solicitud_sur(String.valueOf(xval.longValue()));
//						seguimiento.setNro_solicitud_sur(celda.getStringCellValue());
					}
					
					if(qCel==1){
//						fechaEstado = celda.getStringCellValue();
//						seguimiento.setFecha_ingreso_area_sur(sdf.parse(fechaEstado));
//						seguimiento.setFecha_ingreso_area_sur(celda.getDateCellValue());
						seguimEstado = new SeguimientoSurEstado();
						seguimEstado.setFechaEstado(celda.getDateCellValue());
						seguimEstado.setIdMotivo(0);
						seguimEstado.setUsuario(user.getScreenName());
					}
					
					if(qCel==2){
//						try {
							idEstadoDelEstadoTexto = buscaIdEstadoxTexto(listaDeEstadosSeguimientoSur , celda.toString() );
//							estadoUlti = SeguimientoSurServiceUtil.ultimoEstadoSeguimientoSUR(seguimiento.getId());
//							if(seguimEstado.getFechaEstado()!=null && idEstadoDelEstadoTexto != (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)){							
								seguimEstado.setIdEstado(idEstadoDelEstadoTexto) ;
//								seguimiento.setTipoRegistro("EXI");
//							}else{
//								seguimEstado.setIdEstado(0) ;
//								seguimiento.setTipoRegistro("NOE");
//							}
//						} catch (Exception e) {
//							seguimEstado.setIdEstado(0) ;
//							seguimiento.setTipoRegistro("NOE");
//						}
						 
					}
					
					if(qCel==3){
						seguimEstado.setObservaciones(celda.getStringCellValue()); 						
					}
					
					
					qCel++;
			  }
		      estadosNuevos.add(seguimEstado);
			  seguimiento.setEstados(estadosNuevos); 
		      
		      logger.debug("Linea cambio estado xls: " + qRow );
		       
		      lista.add(seguimiento); 
//		   }
		   qRow++; 
		} 

		boolean estaActualizadoRespectoDeSur = true;
		
		if(lista.size()>0){
		     
			BusquedaSeguimientoSurFiltro filtro = new BusquedaSeguimientoSurFiltro(0, 0, 0, 0, /*s.getNro_solicitud_sur(),*/ null, 
					   	null, null, null, null, null, null, null, false, null, null, null, null,0 , 
					   	null,null,0,null,null,null,null,null,null,null, null, null);
			 
		   for(SeguimientoSur s:lista){
			   
			   filtro.setNroSolicitud(s.getNro_solicitud_sur());
			   
//			   resultBusq = SeguimientoSurServiceUtil.getListaSeguimientoSur(0, 0, 0, 0, s.getNro_solicitud_sur(), 
//					   null, null, null, null, null, null, null, false, null, null, null, null,0 , null,null,0,null,null,null,null,null,null,null);
			   
			   resultBusq = SeguimientoSurServiceUtil.getListaSeguimientoSur(filtro);
			   
			   if(resultBusq.size()>0){
				   
				   ss = resultBusq.get(0);
				   
//				   if(!ss.getNro_expediente().equalsIgnoreCase(s.getNro_expediente())){
//					   estaActualizadoRespectoDeSur = false;
//					   ss.setNro_expediente(s.getNro_expediente());
//				   }
//				   if(!ss.getImporteReconocido().equals(s.getImporteReconocido())){
//					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
//					   ss.setImporteReconocido(s.getImporteReconocido());
//				   }
//				   if(!ss.getProporcionalAdelantado().equals(s.getProporcionalAdelantado())){
//					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
//					   ss.setProporcionalAdelantado(s.getProporcionalAdelantado());
//				   }
//				   if(ss.getFechaProporcionalAdelantado()!=null && 
//						   s.getFechaProporcionalAdelantado() != null &&
//						   ss.getFechaProporcionalAdelantado().getTime() == s.getFechaProporcionalAdelantado().getTime()
////						   .compareTo(s.getFechaProporcionalAdelantado()) != 0 
//						   ){
////					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
//					   ss.setFechaProporcionalAdelantado(s.getFechaProporcionalAdelantado());
//				   }else{
//					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
//					   ss.setFechaProporcionalAdelantado(s.getFechaProporcionalAdelantado()!=null?s.getFechaProporcionalAdelantado():null);
//				   }
//				   estadoUlti = SeguimientoSurServiceUtil.ultimoEstadoSeguimientoSUR(ss.getId());
//				   if(EN_ANALISIS_SUR != (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)
//						&& (PENDIENTE_DE_PRESENTAR == (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0)
//							|| CON_TURNO_SUR == (StringUtils.checkNotEmpty(estadoUlti.getIdEstado())?estadoUlti.getIdEstado():0))   
//					){
//					   estaActualizadoRespectoDeSur = estaActualizadoRespectoDeSur && false;
//				   }
				   
				   if(estaActualizadoRespectoDeSur){
					   ss.setTipoRegistro("EXI");
					   existentes.add(ss);
				   }else{ // nuevos o actualizados en algún campo.
					   ss.setTipoRegistro("NUE");
					   ss.setFecha_ingreso_area_sur(s.getFecha_ingreso_area_sur()==null?DateUtils.getCalendarGMTMenos3().getTime():s.getFecha_ingreso_area_sur());
					   novedades.add(ss);
				   }
				   
				   ss.setBaja_fecha(s.getFecha_ingreso_area_sur());
				   
				   ss.setEstados(s.getEstados()); //pasamos el estado nuevo proveniente del archivo
				   
			   }else{
				 s.setBaja_fecha(s.getFecha_ingreso_area_sur()); 
				 s.setTipoRegistro("NOE");
				 noEncontrados.add(s);  
			   }
		   }
//		   Integer nroLote = SeguimientoSurServiceUtil.proximoNroLotePago(null);

		   novedades.addAll(noEncontrados);
		   novedades.addAll(existentes);
		   		   
//		   if(novedades.size()>0){
//		       SeguimientoSurServiceUtil.actualizarPagoProporcional(novedades, nroLote, user.getScreenName(), EN_ANALISIS_SUR, null);
//		       actionRequest.setAttribute("estadoMasivoSeguimientoSur", novedades);
//		   }
		   if(novedades.size()>0){
			   SeguimientoSurServiceUtil.updateEstadosDesdeListaSeguimientoSur(novedades, user.getScreenName(), null);
			   actionRequest.setAttribute("estadoMasivoSeguimientoSur", novedades);
		   }
   		   
		}
				
		return errores;
	}
	
}
