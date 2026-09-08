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

import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ReporteAlertaPreautorizacionesPrestacionesMedicas extends AgendadoJava implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -756816064378939453L;
	private static Log logger = LogFactoryUtil.getLog(ReporteAlertaPreautorizacionesPrestacionesMedicas.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

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
			List<PreAutorizacion> mAlertasRojasDisca = new ArrayList<PreAutorizacion>();
			
			
			Date dc= DateUtils.getFechaTruncadaEnDia(new Date()).getTime();			  
			for(PreAutorizacion p:ccs){
//
			  if("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion()) ||
			     "AG".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())	||
			     "A3".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())	  ){	
				// Fecha para Seccional
				Calendar c = Calendar.getInstance(); 
				c.setTime(p.getFecha());
				c.add(Calendar.DAY_OF_YEAR, 1); 
				Calendar siguienteDia = feriadosServiceUtil.obtenerSiguienteDiaHabil(c);
				int qDias =  DateUtils.calculaDiasHabilesEntreFechas(siguienteDia.getTime(), dc, true, feriados);
					
				//Fecha para Prestaciones Medicas y Gerencial
				Calendar cEmail = Calendar.getInstance(); 
				if(p.getFechaEmail()!=null){
				   cEmail.setTime(p.getFechaEmail());
				}   
				cEmail.add(Calendar.DAY_OF_YEAR, 1); 
				Calendar siguienteDiaEmail = feriadosServiceUtil.obtenerSiguienteDiaHabil(cEmail);
				int qDiasEmail =  DateUtils.calculaDiasHabilesEntreFechas(siguienteDiaEmail.getTime(), dc, true, feriados);
				  
				if("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion()) ||
						"A3".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())	){
					 if(p.isAlertaRoja()) mAlertasRojas.add(p);
					
					 if(qDias>=qDiasA1){ 
					   List<PreAutorizacion> l= mSeccionales.get(p.getAfiliado().getSeccional().getId());
					   if(l==null) l=new ArrayList<PreAutorizacion>();
					   l.add(p);
					   mSeccionales.put(p.getAfiliado().getSeccional().getId(), l);
					 }
					
					
					if(p.getFechaEmail()!=null){
					   if(qDiasEmail>900) continue;
					   if(qDiasEmail>=qDiasA1+qDiasA12){
						 if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())
							|| "GO".equalsIgnoreCase(p.getUltimoEstado().getId())){
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
					
					if(qDias>=qDiasAG){ /// Corregir para definitivo
						 List<PreAutorizacion> l= mSeccionales.get(p.getAfiliado().getSeccional().getId());
						 if(l==null) l=new ArrayList<PreAutorizacion>();
						 l.add(p);
						 mSeccionales.put(p.getAfiliado().getSeccional().getId(), l);
						   
					}
					if(p.getFechaEmail()!=null){
					   if(qDiasEmail>900) continue;
					   if(qDiasEmail>=qDiasAG+qDiasAG2){
						//mPrestacionesMedicas.add(p);
						
						if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())
								|| "GO".equalsIgnoreCase(p.getUltimoEstado().getId()) ){  
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
//////////
///			

			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_PRESTACIONES_MEDICAS_EMAIL");
		
//to="dsulfaro@uoma.org.ar";			
			if(mPrestacionesMedicasCA.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones con Demora - Prestaciones Médicas (Cargados) ",mPrestacionesMedicasCA,"AvisoPreautorizacion_con_demora_Prestaciones_Medicas_CA.xls",rac,false,false);
			}
			
						
//Discapacidad			
			to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_PRESTACIONES_MEDICAS_DISCAPACIDAD_EMAIL");
//to="dsulfaro@uoma.org.ar";			
			if(mPrestacionesMedicasCADisca.size()>0){
				generarInformePrestacionesMedicas(to,"Preautorizaciones con Demora - Prestaciones Discapacidad (Cargados) ",mPrestacionesMedicasCADisca,"AvisoPreautorizacion_con_demora_Prestaciones_Discapacidad_CA.xls",rac,false,false);
			}
//Fin Discapacidad			
			
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Avisos de Preautorizaciones Prestaciones Médicas");
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
    
    
private static int createHeaderPrestacionesMedicas(HSSFWorkbook wb, HSSFSheet sheet,String titulo,boolean mostrarPrimeraVez,boolean mostrarDiscapacidad) {
		
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
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
		
		HSSFRow row1 = sheet.createRow(index++);

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(gmtMenos3.getTime())));
		
		cell2.setCellStyle(styleHeaderEnca2);
		
		
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 13));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Fecha Carga"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Envio PS"));
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
		return index;
	}

    
    
    
    private static void generarInformePrestacionesMedicas(String to,String titulo,List<PreAutorizacion>preAuts,String xlsArchivo,ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac,boolean mostrarPrimeraVez,boolean mostrarDiscapacidad){
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
		
		FeriadosServiceUtil feriadosServiceUtil=new FeriadosServiceUtil();
		int index = createHeaderPrestacionesMedicas(wb, sheet,titulo,mostrarPrimeraVez,mostrarDiscapacidad);
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
//			  cell05.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getDescripcion()));
			  
			  cell05.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getDescripcionEnsalud()));
			  
			  /*
			  cell05.setCellValue(new HSSFRichTextString("A1".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())
					  || "A3".equalsIgnoreCase(p.getAfiliado().getAfiPlan().getPlan().getDescripcion())?"A MOLINEROS":"A GENERAL"));
			  */
			  HSSFCell cell14 = row.createCell(column++);
			  cell14.setCellValue(new HSSFRichTextString(p.getAfiliado().getAfiPlan().getPlan().getFarmaciaPrevencion()!=null ?p.getAfiliado().getAfiPlan().getPlan().getFarmaciaPrevencion():""));
			  
			  HSSFCell cell06 = row.createCell(column++);
			  if("CA".equalsIgnoreCase(p.getUltimoEstado().getId())){
			    cell06.setCellValue(new HSSFRichTextString("CARGADO"));
			  }else if("OB".equalsIgnoreCase(p.getUltimoEstado().getId())){
				cell06.setCellValue(new HSSFRichTextString("OBSERVADO"));  
			  }else if("RE".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("RECHAZADO"));   
			  }else if("GO".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("GESTION OSPIM"));   
			  }else if("DE".equalsIgnoreCase(p.getUltimoEstado().getId())){
				  cell06.setCellValue(new HSSFRichTextString("DESESTIMADO"));
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
			    if(p.isDiscapacidad()){
			      HSSFCell cell13 = row.createCell(column++);
			      cell13.setCellValue(new HSSFRichTextString("SI"));
			    }
			  }
			  
		}
		
		for(int i=0;i<21;i++)
			     sheet.autoSizeColumn((short) i);
		
		
		String[] vTo = to.split(";");
		for(int i=0;i<vTo.length;i++){
			emails.clear();
			emails.add(vTo[i]);	
			MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails, titulo ,
					  titulo, wb, xlsArchivo);
		}
		  
    }

    
    
    
}
