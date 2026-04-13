package ar.com.ospim.crm.beans;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.autorizaciones.beans.ReportePreCargaReclamo;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReportePreCargaMasReclamosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReportePreCargaMasReclamosExcel.class);

	public static HSSFWorkbook generaReporte() {

		
		try {
			

			List<ReportePreCargaReclamo> busqueda = ReclamosPrestacionesServiceUtil.reclamosPrestacionalPreCarga();

			return generarReporte( busqueda);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte reclamos pre carga", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(List<ReportePreCargaReclamo> reclamos) {
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleAllWithBorderWrapped = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		HSSFCellStyle styleMoneydWithBorder =  getStyleMoneyWithBorder(wb);
		styleAllWithBorderWrapped.setWrapText(true);
		styleAllWithBorder.setVerticalAlignment(VerticalAlignment.CENTER);

		
		HSSFSheet sheet = wb.createSheet("Reclamos");
		try {
			
			int index = createHeader(wb, sheet);
			
			
			for (ReportePreCargaReclamo recl : reclamos) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(String.valueOf(recl.getIdReclamo())));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(recl.getTipoPedido()));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(recl.getEstado()));
				cell2.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(sdf2.format(recl.getAltaFecha())));
				cell3.setCellStyle(styleAllWithBorder);

				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(recl.getSector()));
				cell4.setCellStyle(styleAllWithBorder);
				

				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(recl.getApellido()));
				cell5.setCellStyle(styleAllWithBorder);
								
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(recl.getNombre()));
				cell6.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell61 = row.createCell(column++);
				cell61.setCellValue(new HSSFRichTextString(recl.getPlan()));
				cell61.setCellStyle(styleAllWithBorder);

				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(recl.getSexo().toUpperCase()));
				cell7.setCellStyle(styleAllWithBorder);

				HSSFCell cell8 = row.createCell(column++);
			    cell8.setCellValue(new HSSFRichTextString(recl.getTipoDocu()));				
				cell8.setCellStyle(styleAllWithBorderWrapped);
				
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(new HSSFRichTextString(recl.getDocuNumero()));
				cell9.setCellStyle(styleAllWithBorderWrapped);
				
				String presta ;
				try{
					 presta = recl.getPrestacion().substring(0, recl.getPrestacion().length() - 1);
				}catch (Exception e) {
					presta = "";
				}

				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(new HSSFRichTextString(presta));
				cell10.setCellStyle(styleAllWithBorder);

				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(recl.getRevision()));
				cell11.setCellStyle(styleAllWithBorderWrapped);
				
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(recl.getTotalComprobante() != null ? recl.getTotalComprobante().doubleValue() : 0);			
				cell12.setCellStyle(styleMoneydWithBorder);
				
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(String.valueOf(recl.getCantPrestaciones())));			
				cell13.setCellStyle(styleAllWithBorderWrapped);
				
				HSSFCell cell14 = row.createCell(column++);
				cell14.setCellValue(new HSSFRichTextString(recl.getSeccionalDescripcion()));			
				cell14.setCellStyle(styleAllWithBorderWrapped);
				
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(recl.getFechaMailSeccional() != null ? sdf2.format(recl.getFechaMailSeccional()):""));
				cell15.setCellStyle(styleAllWithBorderWrapped);
				
				String obs ;
				try{
					 obs = recl.getObservaciones().trim();
				}catch (Exception e) {
					obs = "";
				}
				
				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(obs));
				cell16.setCellStyle(styleAllWithBorderWrapped);
			}
			
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);
			sheet.autoSizeColumn((short) 8);
			sheet.autoSizeColumn((short) 9);
			sheet.setColumnWidth(10, 8000);
			sheet.autoSizeColumn((short) 11);
			sheet.autoSizeColumn((short) 12);
			sheet.autoSizeColumn((short) 13);
			sheet.autoSizeColumn((short) 14);
			sheet.autoSizeColumn((short) 15);
			sheet.autoSizeColumn((short) 16);
			sheet.autoSizeColumn((short) 17);
			sheet.autoSizeColumn((short) 18);
			sheet.autoSizeColumn((short) 19);
			sheet.autoSizeColumn((short) 20);
			sheet.autoSizeColumn((short) 21);
			sheet.autoSizeColumn((short) 22);
			sheet.autoSizeColumn((short) 23);
			sheet.autoSizeColumn((short) 24);
			sheet.autoSizeColumn((short) 25);
			sheet.autoSizeColumn((short) 26);
			sheet.autoSizeColumn((short) 27);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		//for(int j=0;j<26;j++){
		 //    sheet.autoSizeColumn((short) j);
		//}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		Calendar formatoFecha = DateUtils.getCalendarGMTMenos3();
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	    String strDate = sdf.format(formatoFecha.getTime());
	
		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte Seguimiento Reclamos Pendientes " + strDate));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
		
		index = 1;
	
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell01 = row3a.createCell(column++);
		cell01.setCellValue(new HSSFRichTextString("N° Reclamo"));
		cell01.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell02 = row3a.createCell(column++);
		cell02.setCellValue(new HSSFRichTextString("Tipo Pedido"));
		cell02.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell03 = row3a.createCell(column++);
		cell03.setCellValue(new HSSFRichTextString("Estado"));
		cell03.setCellStyle(styleHeaderEnca2);
		

		HSSFCell cell04 = row3a.createCell(column++);
		cell04.setCellValue(new HSSFRichTextString("Fecha"));
		cell04.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell05 = row3a.createCell(column++);
		cell05.setCellValue(new HSSFRichTextString("Sector"));
		cell05.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell06 = row3a.createCell(column++);
		cell06.setCellValue(new HSSFRichTextString("Apellido"));
		cell06.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell07 = row3a.createCell(column++);
		cell07.setCellValue(new HSSFRichTextString("Nombre"));
		cell07.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell071 = row3a.createCell(column++);
		cell071.setCellValue(new HSSFRichTextString("Plan"));
		cell071.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell08 = row3a.createCell(column++);
		cell08.setCellValue(new HSSFRichTextString("Sexo"));
		cell08.setCellStyle(styleHeaderEnca2);

		HSSFCell cell09 = row3a.createCell(column++);
		cell09.setCellValue(new HSSFRichTextString("Tipo Documento"));
		cell09.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell10 = row3a.createCell(column++);
		cell10.setCellValue(new HSSFRichTextString("Numero Documento"));
		cell10.setCellStyle(styleHeaderEnca2);
		

		HSSFCell cell11 = row3a.createCell(column++);
		cell11.setCellValue(new HSSFRichTextString("Prestación"));
		cell11.setCellStyle(styleHeaderEnca2);
		
		

		HSSFCell cell12 = row3a.createCell(column++);
		cell12.setCellValue(new HSSFRichTextString("Revisión"));
		cell12.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell13 = row3a.createCell(column++);
		cell13.setCellValue(new HSSFRichTextString("Importe Total"));
		cell13.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell14 = row3a.createCell(column++);
		cell14.setCellValue(new HSSFRichTextString("Cantidad Prestaciones"));
		cell14.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell15 = row3a.createCell(column++);
		cell15.setCellValue(new HSSFRichTextString("Seccional    "));
		cell15.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell16 = row3a.createCell(column++);
		cell16.setCellValue(new HSSFRichTextString("Fecha Seccional"));
		cell16.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell17 = row3a.createCell(column++);
		cell17.setCellValue(new HSSFRichTextString("Observaciones"));
		cell17.setCellStyle(styleHeaderEnca2);
		
		index = index +1;
		return index;
	}
}
