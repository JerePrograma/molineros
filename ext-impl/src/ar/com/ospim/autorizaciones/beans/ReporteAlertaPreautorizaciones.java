package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ReporteAlertaPreautorizaciones extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = -8178471218090516550L;
	private static Log logger = LogFactoryUtil.getLog(ReporteAlertaPreautorizaciones.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo ReporteAlertaPreautorizaciones");
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		FeriadosServiceUtil feriadosServiceUtil=new FeriadosServiceUtil();
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			List<Feriado> feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
			
			List<PreAutorizacion> ccs = PreAutorizacionServiceUtil.getAlertaPreAutorizaciones();
			
			int qDiasA1 = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_A1_DIAS"));
			int qDiasAG = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_AG_DIAS"));
			
			int qDiasA12 = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_A1_DIAS_2"));
			int qDiasAG2 = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_AG_DIAS_2"));
			
			int qDiasA13 = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_A1_DIAS_3"));
			int qDiasAG3 = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_AG_DIAS_3"));
			
			Map<Integer, List<PreAutorizacion>> mSeccionales = new HashMap<Integer, List<PreAutorizacion>>();
			List<PreAutorizacion> mPrestacionesMedicasCA = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mPrestacionesMedicasOB = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mGerencial = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mPrestacionesMedicasFechaCarga = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mAlertasRojas = new ArrayList<PreAutorizacion>();
			
			List<PreAutorizacion> mPrestacionesMedicasCADisca = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mPrestacionesMedicasOBDisca = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mPrestacionesMedicasFechaCargaDisca = new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> mPrestacionesDesestimadas = new ArrayList<PreAutorizacion>();
//			List<PreAutorizacion> mAlertasRojasDisca = new ArrayList<PreAutorizacion>();
			
			
			Date dc= DateUtils.getFechaTruncadaEnDia(new Date()).getTime();			  
			for(PreAutorizacion p:ccs){
//
			  if("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion()) ||
			     "AG".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())	|| 
			     "A3".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())){
				  
				  
				if("DE".equalsIgnoreCase(p.getUltimoEstado().getId())){
					mPrestacionesDesestimadas.add(p);
				}
				  
				// Fecha para Seccional
				Calendar c = Calendar.getInstance(); 
				c.setTime(p.getFecha());
				c.add(Calendar.DATE, 1); 
				Calendar siguienteDia = feriadosServiceUtil.obtenerSiguienteDiaHabil(c);
				int qDias =  DateUtils.calculaDiasHabilesEntreFechasHoraCero(siguienteDia.getTime(), dc, false, feriados);
					
				//Fecha para Prestaciones Medicas y Gerencial
				Calendar cEmail = Calendar.getInstance(); 
				if(p.getFechaEmail()!=null){
				   cEmail.setTime(p.getFechaEmail());
				}   
				cEmail.add(Calendar.DATE, 1); 
				Calendar siguienteDiaEmail = feriadosServiceUtil.obtenerSiguienteDiaHabil(cEmail);
				int qDiasEmail =  DateUtils.calculaDiasHabilesEntreFechasHoraCero(siguienteDiaEmail.getTime(), dc, false, feriados);
				  
				if("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion()) ||
						"A3".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())	
						){
					 if(p.isAlertaRoja()) mAlertasRojas.add(p);
					
//					 if(qDias>=qDiasA1){ 
					   List<PreAutorizacion> l= mSeccionales.get(p.getAfiliado().getSeccional().getId());
					   if(l==null) l=new ArrayList<PreAutorizacion>();
					   l.add(p);
					   mSeccionales.put(p.getAfiliado().getSeccional().getId(), l);
//					 }
					
					
					if(p.getFechaEmail()!=null){
					   if(qDiasEmail>900) continue;
					   if(qDiasEmail>=qDiasA1+qDiasA12){
						 if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())
								 || "GO".equalsIgnoreCase(p.getUltimoEstado().getId()) 
								 || "DE".equalsIgnoreCase(p.getUltimoEstado().getId())
								 || "AP".equalsIgnoreCase(p.getUltimoEstado().getId())
								 ){
							 if(qDiasEmail>=qDiasA1+qDiasA12+qDiasA13){
								 p.setDiasParaAlertaGerencial(qDiasA1+qDiasA12+qDiasA13);	
								 mGerencial.add(p); 
							 }else{
							   if(!p.isDiscapacidad()){	 
						          mPrestacionesMedicasCA.add(p);
							   }else{
								   mPrestacionesMedicasCADisca.add(p);  
							   }
							 }
						    
						 }else if("OB".equalsIgnoreCase(p.getUltimoEstado().getId())){
							 if(qDiasEmail==qDiasA1+qDiasA12){
							   if(!p.isDiscapacidad()){	 
							      mPrestacionesMedicasOB.add(p);
							   }else{
								  mPrestacionesMedicasOBDisca.add(p); 
							   }
							 }
						 }
						 
						 /*
						 if(qDiasEmail>=qDiasA1+qDiasA12+qDiasA13){
							if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())){ 
							   p.setDiasParaAlertaGerencial(qDiasA1+qDiasA12+qDiasA13);	
							   mGerencial.add(p);
							}   
						 }
						 */
						 
					   }
					}else{
					  if(qDias>900) continue;
					  if(qDias>=qDiasA1+qDiasA12){
						  if(!p.isDiscapacidad()){
						     mPrestacionesMedicasFechaCarga.add(p);
						  }else{
							  mPrestacionesMedicasFechaCargaDisca.add(p); 
						  }
					  }
					}
					
					
				}else if("AG".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())){
					if(p.isAlertaRoja()) mAlertasRojas.add(p);
					
//					if(qDias>=qDiasAG){ /// Corregir para definitivo
						 List<PreAutorizacion> l= mSeccionales.get(p.getAfiliado().getSeccional().getId());
						 if(l==null) l=new ArrayList<PreAutorizacion>();
						 l.add(p);
						 mSeccionales.put(p.getAfiliado().getSeccional().getId(), l);
						   
//					}
					if(p.getFechaEmail()!=null){
					   if(qDiasEmail>900) continue;
					   if(qDiasEmail>=qDiasAG+qDiasAG2){
						//mPrestacionesMedicas.add(p);
						
						if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())
								|| "GO".equalsIgnoreCase(p.getUltimoEstado().getId())
								|| "DE".equalsIgnoreCase(p.getUltimoEstado().getId())
								|| "AP".equalsIgnoreCase(p.getUltimoEstado().getId())){  
							if(qDiasEmail>=qDiasAG+qDiasAG2+qDiasAG3){
								p.setDiasParaAlertaGerencial(qDiasAG+qDiasAG2+qDiasAG3);	
								mGerencial.add(p);
							}else{
								if(!p.isDiscapacidad()){
								   mPrestacionesMedicasCA.add(p);
								}else{
								   mPrestacionesMedicasCADisca.add(p);	
								}
							}
						 }else if("OB".equalsIgnoreCase(p.getUltimoEstado().getId())){
							 if(qDiasEmail==qDiasAG+qDiasAG2){
							   if(!p.isDiscapacidad()){	 
							      mPrestacionesMedicasOB.add(p);
							   }else{
								   mPrestacionesMedicasOBDisca.add(p); 
							   }
							 }
						 }
						/*
						if(qDiasEmail>=qDiasAG+qDiasAG2+qDiasAG3){
							if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())){
							  p.setDiasParaAlertaGerencial(qDiasAG+qDiasAG2+qDiasAG3);	
							  mGerencial.add(p);
							}  
						}
						*/
					   }
					}else{
						if(qDias>900) continue;
						if(qDias>=qDiasA1+qDiasA12){
							if(!p.isDiscapacidad()){
							   mPrestacionesMedicasFechaCarga.add(p);
							}else{
							   mPrestacionesMedicasFechaCargaDisca.add(p);	
							}
						}
					}
					
					
				}
			  }	
			}
			
			
			
			String body ="Informe de Preautorizaciones con Demoras";
			String to="";
				
			
			for (Integer key : mSeccionales.keySet()) {
				List<PreAutorizacion> preAuts = mSeccionales.get(key);
				

                if(key==0){
                   to=WebKeysAutorizaciones.EMAIL_AUDITORIA_MEDICA;
                   
                   //correos adicionales
                   String otros = TraeListasServiceUtil.getSystemConfig("EMAIL_MONOTRIBUTO");
                   
                   if (!StringUtils.checkEmpty(otros)) {
                       to = to + otros;
                   }
                }else {
                    List<ContactoElectronico> lc =  SeccionalServiceUtil.getInstance().buscarContactosSeccionalEmail(key);
				    to="";
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
				   String sec = preAuts.get(0).getAfiliado().getSeccional().getDescripcion();	
				   generarInforme(to,sec,preAuts,rac);
				}
				
			}

			
			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_GERENCIAL_EMAIL");
//to="dsulfaro@uoma.org.ar";			
			if(mGerencial.size()>0){
				generarInformePrestacionesMedicas(to," Preautorizaciones con Demora - Gerencia General ",mGerencial,"AvisoPreautorizacion_con_demora_GerenciaGeneral.xls",rac,true,true,false);
			}
			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ALERTA_ROJA_EMAIL");
//to="dsulfaro@uoma.org.ar";				
			if(mAlertasRojas.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones Alerta Roja  ",mAlertasRojas,"AvisoPreautorizacion_alertas_rojas.xls",rac,false,true,false);
			}
			
			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_PRESTACIONES_MEDICAS_FECHA_CARGA_EMAIL");
//to="dsulfaro@uoma.org.ar";			
			if(mPrestacionesMedicasFechaCarga.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones - Prestaciones Médicas (Sin Enviar) ",mPrestacionesMedicasFechaCarga,"AvisoPreautorizacion_con_demora_Sin_Enviar.xls",rac,false,false,false);
			}
			

			List<PreAutorizacion> rech0 = PreAutorizacionServiceUtil.getAlertaPreAutorizacionesRechazados();
			List<PreAutorizacion> rechazados= new ArrayList<PreAutorizacion>();
			List<PreAutorizacion> rechazadosDiscapacidad= new ArrayList<PreAutorizacion>();
			
			for(PreAutorizacion r:rech0){
				if(r.isDiscapacidad()){
					rechazadosDiscapacidad.add(r);
				}else{
					rechazados.add(r);
				}
			}
			
			if(rechazados!=null && rechazados.size()>0){
 		      to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_PRESTACIONES_MEDICAS_RECHAZADOS_EMAIL");
//to="dsulfaro@uoma.org.ar;";				 
	          try{	
			    generarInformePrestacionesMedicas(to," Preautorizaciones Rechazadas  ",rechazados,"AvisoPreautorizacion_rechazadas.xls",rac,false,false,true);
			    for(PreAutorizacion p:rechazados){
			      	PreAutorizacionServiceUtil.saveInformeRechazo(p.getId());
			    }
			  }catch(Exception e){
				 logger.debug("Error al generar Informe Preautorizaciones Rechazadas");
				 logger.error(e);
			  }
			}
			
//Discapacidad			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_PRESTACIONES_MEDICAS_DISCAPACIDAD_EMAIL");
//to="dsulfaro@uoma.org.ar";

			if(mPrestacionesMedicasOBDisca.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones con Demora - Prestaciones Discapacidad (Observados)",mPrestacionesMedicasOBDisca,"AvisoPreautorizacion_con_demora_Prestaciones_Discapacidad_OB.xls",rac,false,false,false);
			}
			
			if(mPrestacionesMedicasFechaCargaDisca.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones - Prestaciones Discapacidad (Sin Enviar) ",mPrestacionesMedicasFechaCargaDisca,"AvisoPreautorizacion_con_demora_discapacidad_Sin_Enviar.xls",rac,false,false,false);
			}
			
			if(rechazadosDiscapacidad!=null && rechazadosDiscapacidad.size()>0){
		          try{	
				    generarInformePrestacionesMedicas(to," Preautorizaciones Discapacidad Rechazadas   ",rechazadosDiscapacidad,"AvisoPreautorizacion_rechazadas_discapacidad.xls",rac,false,false,true);
				    for(PreAutorizacion p:rechazadosDiscapacidad){
				      	PreAutorizacionServiceUtil.saveInformeRechazo(p.getId());
				    }
				  }catch(Exception e){
					 logger.error("Error al generar Informe Preautorizaciones Rechazadas Discapacidad");
					 logger.error(e);
				  }
			}
			
//Fin Discapacidad			
			
			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DESESTIMADAS_EMAIL");
//to="dsulfaro@uoma.org.ar";			
			if(mPrestacionesDesestimadas.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones Desestimadas",mPrestacionesDesestimadas,"AvisoPreautorizacion_Desestimadas.xls",rac,false,false,false);
			}
			
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Avisos de Preautorizaciones");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

	protected static HSSFCellStyle getStyleAllWithBorder(HSSFWorkbook wb,
			int size) {
		HSSFCellStyle styleAll = getStyleAll(wb, size);
		setThinBorders(styleAll);
		return styleAll;
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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); 
		gmtMenos3.setTimeZone(tz);

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Preautorizaciones de " + titulo));
		cell.setCellStyle(styleHeaderEnca);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
		
		HSSFRow row1 = sheet.createRow(index++);

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(gmtMenos3.getTime())));
		
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 14));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Fecha Carga"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Envio Ensalud"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Nro"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Nro Documento"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Plan"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell40 = row3a.createCell(column++);
		cell40.setCellValue(new HSSFRichTextString("Farmacia"));
		cell40.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Estado"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Prestaciones"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Alerta"));
		cell38.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell39 = row3a.createCell(column++);
		cell39.setCellValue(new HSSFRichTextString("Discapacidad"));
		cell39.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell41 = row3a.createCell(column++);
		cell41.setCellValue(new HSSFRichTextString("Solicitud Tercerizadora"));
		cell41.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell42 = row3a.createCell(column++);
		cell42.setCellValue(new HSSFRichTextString("Supra"));
		cell42.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell43 = row3a.createCell(column++);
		cell43.setCellValue(new HSSFRichTextString("Medicamento"));
		cell43.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell44 = row3a.createCell(column++);
		cell44.setCellValue(new HSSFRichTextString("Cirugía"));
		cell44.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell45 = row3a.createCell(column++);
		cell45.setCellValue(new HSSFRichTextString("Posible A.R.T."));
		cell45.setCellStyle(styleHeaderEnca2);
		
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
    
    
    private static void generarInforme(String to,String titulo,List<PreAutorizacion>preAuts,ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac){
    	HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		HSSFCellStyle styleAll =getStyleAll(wb);
		styleAll.setWrapText(true);
		HSSFSheet sheet = wb.createSheet("Preautorizaciones");
	    
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);
		ArrayList<String> emails = new ArrayList<String>();
		
		int index = createHeader(wb, sheet,titulo);
		  index++;
		
		for(PreAutorizacion p:preAuts){
			int column = 0;
			  HSSFRow row = sheet.createRow(index++);
			  
			  HSSFCell cell01 = row.createCell(column++);
			  cell01.setCellValue(new HSSFRichTextString(p.getFecha_string()));
			  
			  HSSFCell cell09 = row.createCell(column++);
			  cell09.setCellValue(new HSSFRichTextString(p.getFechaEnvioMail_string()));
			  
			  HSSFCell cell02 = row.createCell(column++);
			  cell02.setCellValue(p.getId());
			  
			  HSSFCell cell03 = row.createCell(column++);
			  cell03.setCellValue(new HSSFRichTextString(p.getAfiliado().getApeNombre()));
			  
			  HSSFCell cell04 = row.createCell(column++);
			  cell04.setCellValue(new HSSFRichTextString(String.valueOf(p.getAfiliado().getDocu_numero())));
			  
			  HSSFCell cell05 = row.createCell(column++);
			  cell05.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getDescripcionEnsalud()!=null?
					  p.getAfiliado().getAfiPlan().getPlan().getDescripcionEnsalud():""));
			  
			  /*
			  cell05.setCellValue(new HSSFRichTextString("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())
					 || "A3".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())  ?"A MOLINEROS":"A GENERAL"));
			  */	
			  
			  HSSFCell cell11 = row.createCell(column++);
			  cell11.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getFarmaciaPrevencion()!=null ?p.getAfiliado().getAfiPlan().getPlan().getFarmaciaPrevencion():""));
			  
			  HSSFCell cell06 = row.createCell(column++);
			  if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())){
			    cell06.setCellValue(new HSSFRichTextString("CARGADO"));
			  }else if("GO".equalsIgnoreCase(p.getUltimoEstado().getId())){
				    cell06.setCellValue(new HSSFRichTextString("GESTION OSPIM"));
			  }else if("DE".equalsIgnoreCase(p.getUltimoEstado().getId())){
				    cell06.setCellValue(new HSSFRichTextString("DESESTIMADO"));				    
			  }else if("AP".equalsIgnoreCase(p.getUltimoEstado().getId())){
				    cell06.setCellValue(new HSSFRichTextString("DESDE APLICACION"));				    
			  }else{
				cell06.setCellValue(new HSSFRichTextString("OBSERVADO"));  
			  }
			  
			  HSSFCell cell08 = row.createCell(column++);
			  HSSFCellStyle style = wb.createCellStyle();
			  cell08.setCellValue(new HSSFRichTextString(p.getPrestaciones()));
			  style.setWrapText(true);
			  cell08.setCellStyle(style);
			  
			  HSSFCell cell10 = row.createCell(column++);
			  if(p.isAlertaRoja()){
			    cell10.setCellValue(new HSSFRichTextString("Alerta Roja"));
			  }else{
				cell10.setCellValue(new HSSFRichTextString(""));  
			  }
			  
			  HSSFCell cell13 = row.createCell(column++);
			  if(p.isDiscapacidad()){
			      cell13.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell13.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell12 = row.createCell(column++);
			  cell12.setCellValue(new HSSFRichTextString(String.valueOf(p.getIdAutorizacionWS())));
			  
			  HSSFCell cell14 = row.createCell(column++);
			  if(p.isSupra()){
			      cell14.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell14.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell15 = row.createCell(column++);
			  if(p.isMedicamento()){
			      cell15.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell15.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell16 = row.createCell(column++);
			  if(p.isCirugia()){
			      cell16.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell16.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell17 = row.createCell(column++);
			  if(p.isART()){
			      cell17.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell17.setCellValue(new HSSFRichTextString(""));
			  }
		}
		
		for(int i=0;i<24;i++)
			     sheet.autoSizeColumn((short) i);
		
		
		String[] vTo = to.split(";");
		for(int i=0;i<vTo.length;i++){
			emails.clear();
			emails.add(vTo[i]);
			MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails, "Informes Preautorizaciones " + titulo,
					  "Informe de Preautorizaciones Pendientes", wb, "AvisoPreautorizacion_"+ titulo+".xls");
		}
		
    }
    
    
private static int createHeaderPrestacionesMedicas(HSSFWorkbook wb, HSSFSheet sheet,String titulo,
		boolean mostrarPrimeraVez,boolean mostrarDiscapacidad, boolean mostrarMotivoRechazo) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); 
		gmtMenos3.setTimeZone(tz);

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("" + titulo));
		cell.setCellStyle(styleHeaderEnca);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));
		
		HSSFRow row1 = sheet.createRow(index++);

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(gmtMenos3.getTime())));
		
		cell2.setCellStyle(styleHeaderEnca2);
		
		
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 17));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Fecha Carga"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Envio Ensalud"));
		cell38.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Nro"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Nro Documento"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Seccional"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Plan"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell43 = row3a.createCell(column++);
		cell43.setCellValue(new HSSFRichTextString("Farmacia"));
		cell43.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Estado"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Prestaciones"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		if(mostrarPrimeraVez){
			HSSFCell cell39 = row3a.createCell(column++);
			cell39.setCellValue(new HSSFRichTextString("Incluído"));
			cell39.setCellStyle(styleHeaderEnca2);
		}
		
		HSSFCell cell40 = row3a.createCell(column++);
		cell40.setCellValue(new HSSFRichTextString("Alerta"));
		cell40.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell41 = row3a.createCell(column++);
		cell41.setCellValue(new HSSFRichTextString("Fecha Alerta"));
		cell41.setCellStyle(styleHeaderEnca2);
		
		if(mostrarDiscapacidad){
			HSSFCell cell42 = row3a.createCell(column++);
			cell42.setCellValue(new HSSFRichTextString("Discapacidad"));
			cell42.setCellStyle(styleHeaderEnca2);
		}
		HSSFCell cell44 = row3a.createCell(column++);
		cell44.setCellValue(new HSSFRichTextString("Solicitud Tercerizadora"));
		cell44.setCellStyle(styleHeaderEnca2);
		
		if(mostrarMotivoRechazo) {
			HSSFCell cell45 = row3a.createCell(column++);
			cell45.setCellValue(new HSSFRichTextString("Motivo Rechazo"));
			cell45.setCellStyle(styleHeaderEnca2);
			
			HSSFCell cell48 = row3a.createCell(column++);
			cell48.setCellValue(new HSSFRichTextString("Obs. Externas"));
			cell48.setCellStyle(styleHeaderEnca2);
		}
		
		HSSFCell cell42 = row3a.createCell(column++);
		cell42.setCellValue(new HSSFRichTextString("Supra"));
		cell42.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell46 = row3a.createCell(column++);
		cell46.setCellValue(new HSSFRichTextString("Medicamento"));
		cell46.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell47 = row3a.createCell(column++);
		cell47.setCellValue(new HSSFRichTextString("Cirugía"));
		cell47.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell48 = row3a.createCell(column++);
		cell48.setCellValue(new HSSFRichTextString("Posible A.R.T."));
		cell48.setCellStyle(styleHeaderEnca2);
		
		return index;
	}

    
    
    
    private static void generarInformePrestacionesMedicas(String to,String titulo,List<PreAutorizacion>preAuts,String xlsArchivo,
    		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac, 
    		boolean mostrarPrimeraVez,boolean mostrarDiscapacidad, boolean mostrarMotivoRechazo){
    	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
    	HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		HSSFCellStyle styleAll =getStyleAll(wb);
		styleAll.setWrapText(true);
		List<Feriado> feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
		HSSFSheet sheet = wb.createSheet("Preautorizaciones");
	    
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
		FeriadosServiceUtil feriadosServiceUtil=new FeriadosServiceUtil();
		int index = createHeaderPrestacionesMedicas(wb, sheet,titulo,mostrarPrimeraVez,mostrarDiscapacidad,mostrarMotivoRechazo);
		  index++;
		
		for(PreAutorizacion p:preAuts){
			int column = 0;
			  HSSFRow row = sheet.createRow(index++);
			  
			  HSSFCell cell01 = row.createCell(column++);
			  cell01.setCellValue(new HSSFRichTextString(p.getFecha_string()));
			  
			  HSSFCell cell09 = row.createCell(column++);
			  cell09.setCellValue(new HSSFRichTextString(p.getFechaEnvioMail_string()));
			  
			  HSSFCell cell02 = row.createCell(column++);
			  cell02.setCellValue(p.getId());
			  
			  HSSFCell cell03 = row.createCell(column++);
			  cell03.setCellValue(new HSSFRichTextString(p.getAfiliado().getApeNombre()));
			  
			  HSSFCell cell04 = row.createCell(column++);
			  cell04.setCellValue(new HSSFRichTextString(String.valueOf(p.getAfiliado().getDocu_numero())));
			  
			  HSSFCell cell07 = row.createCell(column++);
			  cell07.setCellValue(new HSSFRichTextString(String.valueOf(p.getAfiliado().getSeccional().getId()) +"-"+ p.getAfiliado().getSeccional().getDescripcion() ));
			  
			  HSSFCell cell05 = row.createCell(column++);
			  cell05.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getDescripcionEnsalud()!=null?
					  p.getAfiliado().getAfiPlan().getPlan().getDescripcionEnsalud():""));
//			  cell05.setCellValue(new HSSFRichTextString("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())?"A MOLINEROS":"A GENERAL"));
			  
			  HSSFCell cell14 = row.createCell(column++);
			  cell14.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getFarmaciaPrevencion()!=null ?p.getAfiliado().getAfiPlan().getPlan().getFarmaciaPrevencion():""));
			  
			  
			  HSSFCell cell06 = row.createCell(column++);
			  if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())){
			    cell06.setCellValue(new HSSFRichTextString("CARGADO"));
			  }else if("OB".equalsIgnoreCase(p.getUltimoEstado().getId())){
				cell06.setCellValue(new HSSFRichTextString("OBSERVADO"));  
			  }else if("RE".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("RECHAZADO"));
			  }else if("DE".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("DESESTIMADO"));	  
			  }else if("GO".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("GESTION OSPIM"));	  
			  }else if("AP".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("DESDE APLICACION"));	  
			  }
			  
			  HSSFCell cell08 = row.createCell(column++);
			  HSSFCellStyle style = wb.createCellStyle();
			  cell08.setCellValue(new HSSFRichTextString(p.getPrestaciones()));
			  style.setWrapText(true);
			  cell08.setCellStyle(style);
			  
              if(mostrarPrimeraVez){
            	Calendar cEmail = Calendar.getInstance(); 
  				if(p.getFechaEmail()!=null){
  				   cEmail.setTime(p.getFechaEmail());
  				}   
  				cEmail.add(Calendar.DAY_OF_YEAR, 1); 
  				Calendar siguienteDiaEmail = DateUtils.getFechaTruncadaEnDia(feriadosServiceUtil.obtenerSiguienteDiaHabil(cEmail).getTime());
  				Calendar aux=DateUtils.getFechaTruncadaEnDia(feriadosServiceUtil.obtenerSiguienteDiaHabil(cEmail).getTime());
  				int qDias=0;
  				while (true) {         
  					qDias=DateUtils.calculaDiasHabilesEntreFechas(siguienteDiaEmail.getTime(), aux.getTime(), true, feriados);
  					if (qDias>=p.getDiasParaAlertaGerencial()) { break;}
  					aux.add(Calendar.DAY_OF_YEAR, 1);
  		        }       
            	HSSFCell cell10 = row.createCell(column++);
    			cell10.setCellValue(new HSSFRichTextString(sdf.format(aux.getTime())));
              }
              
              HSSFCell cell11 = row.createCell(column++);
			  if(p.isAlertaRoja() ){
			    cell11.setCellValue(new HSSFRichTextString("Alerta Roja"));
			  }else {
				cell11.setCellValue(new HSSFRichTextString(""));  
			  }
			  
			  HSSFCell cell12 = row.createCell(column++);
			  if(p.isAlertaRoja() ){
			    cell12.setCellValue(new HSSFRichTextString(sdf.format(p.getAlertaRojaFecha()) ));
			  }else {
				cell12.setCellValue(new HSSFRichTextString(""));  
			  }
			 
			  if(mostrarDiscapacidad){
				HSSFCell cell13 = row.createCell(column++);  
			    if(p.isDiscapacidad()){
			      cell13.setCellValue(new HSSFRichTextString("SI"));
			    }else {
			      cell13.setCellValue(new HSSFRichTextString(""));	
			    }
			  }
			  
			  HSSFCell cell15 = row.createCell(column++);
			  cell15.setCellValue(new HSSFRichTextString(String.valueOf(p.getIdAutorizacionWS())));
			  
			  if(mostrarMotivoRechazo){
				  HSSFCell cell16 = row.createCell(column++);
			      cell16.setCellValue(new HSSFRichTextString(p.getUltimoEstado().getMotivoRechazo()!=null?p.getUltimoEstado().getMotivoRechazo():""));
			      
			      HSSFCell cell19 = row.createCell(column++);
			      cell19.setCellValue(new HSSFRichTextString(p.getUltimoEstado().getObservacionesExternas()!=null?p.getUltimoEstado().getObservacionesExternas():""));
			  }
			  
			  HSSFCell cell16 = row.createCell(column++);
			  if(p.isSupra()){
			      cell16.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell16.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell17 = row.createCell(column++);
			  if(p.isMedicamento()){
			      cell17.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell17.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell18 = row.createCell(column++);
			  if(p.isCirugia()){
			      cell18.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell18.setCellValue(new HSSFRichTextString(""));
			  }
			  
			  HSSFCell cell19 = row.createCell(column++);
			  if(p.isART()){
			      cell19.setCellValue(new HSSFRichTextString("SI"));
			  }else {
				  cell19.setCellValue(new HSSFRichTextString(""));
			  }
		}
		
		for(int i=0;i<24;i++)
			     sheet.autoSizeColumn((short) i);
		
		  List<String>lm = new ArrayList<String>();
		  for(String s:emails) {
			lm.clear();
			lm.add(s);
		    MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), lm, titulo ,
				  titulo, wb, xlsArchivo);
		  }  
    }

    
    
    
}
