package ar.com.uoma.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;

public class AvisoComprobantesCajaChica extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = -6692533973918708714L;
	
	private static Log logger = LogFactoryUtil.getLog(AvisoComprobantesCajaChica.class);
	
	XSSFWorkbook wbOut = new XSSFWorkbook();    
	CellStyle style = wbOut.createCellStyle();

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		try {
			
			List<CajaChica> ccs = CajaChicaServiceUtil.list(null, 0,0, WebKeysGlobal.UOMA);
			
			
			String body ="Informe de los comprobantes dados de alta o modificados de la Caja Chica";
			
			
			for(CajaChica cc:ccs){
				ArrayList<String> emails = new ArrayList<String>();
		
				List<ComprobanteCajaChica> comps = CajaChicaServiceUtil.comprobantesPendientesInforme(WebKeysGlobal.UOMA, cc.getId());
				if(comps.size()>0 && cc.getEmailsController()!=null && cc.getEmailsController().length()>0){
				  HSSFWorkbook wb = new HSSFWorkbook();
				  HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
				  HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
				  HSSFCellStyle styleAll =getStyleAll(wb);
				  styleAll.setWrapText(true);
				  HSSFSheet sheet = wb.createSheet("Comprobantes");
				  
				  HSSFPrintSetup ps = sheet.getPrintSetup();
				  sheet.setAutobreaks(true);
				  ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
				  ps.setFitHeight((short) 0);
				  ps.setFitWidth((short) 1);
				  ps.setLandscape(true);
					
				  
				  
				  int index = createHeader(wb, sheet,cc.getDescripcion());
				  index++;	
					
                  String[] vEmail = cc.getEmailsController().split(";"); 				  
                  for(int i=0;i<vEmail.length;i++){
                	  emails.add(vEmail[i]);
                  }
                   
				  for(ComprobanteCajaChica ccc:comps){
					  int column = 0;
					  HSSFRow row = sheet.createRow(index++);
					  
					  HSSFCell cell01 = row.createCell(column++);
					  cell01.setCellValue(new HSSFRichTextString(ccc.getFechaEmisionAsString()));
					  
					  HSSFCell cell02 = row.createCell(column++);
					  cell02.setCellValue(new HSSFRichTextString(ccc.getTipoComprobante()));
					  
					  HSSFCell cell03 = row.createCell(column++);
					  cell03.setCellValue(new HSSFRichTextString(ccc.getLetraComprobante()));
					  
					  HSSFCell cell04 = row.createCell(column++);
					  cell04.setCellValue(new HSSFRichTextString(String.valueOf(ccc.getPtoVenta())));
					  
					  HSSFCell cell05 = row.createCell(column++);
					  cell05.setCellValue(new HSSFRichTextString(ccc.getNroComprobante() ));
						
					  HSSFCell cell06 = row.createCell(column++);
					  cell06.setCellValue(new HSSFRichTextString(ccc.getAcreedorEmpresa().getCuit()));
					  
					  HSSFCell cell08 = row.createCell(column++);
					  cell08.setCellValue(new HSSFRichTextString(ccc.getAcreedorEmpresa().getRazon_soc()));
					  
					  HSSFCell cell09 = row.createCell(column++);
					  cell09.setCellValue(new HSSFRichTextString(ccc.getConceptos().get(0).getConceptoComprobante().getDescripcion() ));
					  cell09.setCellStyle(styleAll);
					  //cell09.getCellStyle().setAlignment(VerticalAlignment.VERTICAL_TOP);
					  cell09.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
					  
					  
					  HSSFCell cell10 = row.createCell(column++);
					  cell10.setCellValue(ccc.getImporte().doubleValue());
					  cell10.setCellStyle(styleMoneyRight);
					  
					  
					  String clase ="";
					  if(ccc.getAlta_fecha()!= null && ccc.getModi_fecha() == null && ccc.getBaja_fecha() ==null){
						  clase="ALTA";
					  }
					  if(ccc.getModi_fecha() != null && ccc.getBaja_fecha() ==null){
						  clase="MODIFICACION";
					  }
					  if(ccc.getBaja_fecha() != null){
						  clase="BAJA";
					  }
					  HSSFCell cell11 = row.createCell(column++);
					  cell11.setCellValue(new HSSFRichTextString(clase ));
					  
					  
					  int rowHeight = (ccc.getConceptos().get(0).getConceptoComprobante().getDescripcion().length() / 25)+(
								(ccc.getConceptos().get(0).getConceptoComprobante().getDescripcion().length() % 25)>0?1:0) ;
								
					  row.setHeight((short)(row.getHeight() * rowHeight)); 
					  
					  
				  }
				  
				  for(int i=0;i<11;i++)
				     sheet.autoSizeColumn((short) i);
				  
				  rac = ReportesServiceUtil.getConfiguracion();
					
//				  MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails, "Informes Comprobantes " + cc.getDescripcion(),
//						  body, wb, "InformeComprobantes_"+ cc.getDescripcion()+".xls");
				  
				  EnviaEmailsThread.enviarMailDesatendido("Informes Comprobantes " + cc.getDescripcion(), body, emails, wb, "InformeComprobantes_"+ cc.getDescripcion()+".xls");
				  
				  CajaChicaServiceUtil.updateComprobantesPendientesInforme(WebKeysGlobal.UOMA, cc.getId());
				  
				}  
			}
			
			ra.setUltimaEjecucion(new Date());
	
			ReportesServiceUtil.reporteEjecutado(ra);

			logger.debug("Fin de Envío de Avisos de comprobantes Caja Chica UOMA");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (PortalException e) {
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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); 
		gmtMenos3.setTimeZone(tz);

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Comprobantes de " + titulo));
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
		cell32.setCellValue(new HSSFRichTextString("Emisión"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Tipo"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Letra"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Pto.Vta"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Número"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("CUIT"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Razón Social"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Concepto"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Importe"));
		cell38.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell39 = row3a.createCell(column++);
		cell39.setCellValue(new HSSFRichTextString("Clase"));
		cell39.setCellStyle(styleHeaderEnca2);
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
		//fontBold.setBold(true);
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
}
