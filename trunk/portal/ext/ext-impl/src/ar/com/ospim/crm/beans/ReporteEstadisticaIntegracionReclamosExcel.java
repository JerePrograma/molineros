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

import ar.com.ospim.autorizaciones.beans.ReporteIntegracionReclamo;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReporteEstadisticaIntegracionReclamosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteEstadisticaIntegracionReclamosExcel.class);

	public static HSSFWorkbook generaReporte() {

		
		try {
			

			List<ReporteIntegracionReclamo> busqueda = ReclamosPrestacionesServiceUtil.reclamosPrestaEstadisticaIntegracion();

			return generarReporte( busqueda);
			
		} catch (Exception e) {
			_log.error("Error ReporteEstadisticaIntegracionReclamosExcel", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(List<ReporteIntegracionReclamo> reclamos) {
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleAllWithBorderWrapped = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		HSSFCellStyle styleMoneydWithBorder =  getStyleMoneyWithBorder(wb);
		styleAllWithBorderWrapped.setWrapText(true);
		styleAllWithBorder.setVerticalAlignment(VerticalAlignment.CENTER);

		
		HSSFSheet sheet = wb.createSheet("Reclamos Integración");
		try {
			
			int index = createHeader(wb, sheet);
			
			
			for (ReporteIntegracionReclamo recl : reclamos) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(String.valueOf(recl.getIdReclamo())));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(recl.getDescIntegracion()));
				cell11.setCellStyle(styleAllWithBorder);

				
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(recl.getApellido()));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(recl.getNombre()));
				cell2.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(recl.getSexo()));
				cell3.setCellStyle(styleAllWithBorder);

				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(recl.getTipoDocu()));
				cell4.setCellStyle(styleAllWithBorder);
				
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(recl.getDocuNumero()));
				cell5.setCellStyle(styleAllWithBorder);
								
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(recl.getCodigo()));
				cell7.setCellStyle(styleAllWithBorder);

	
				HSSFCell cell8 = row.createCell(column++);
			    cell8.setCellValue(new HSSFRichTextString(recl.getNombrePrestacion()));				
				cell8.setCellStyle(styleAllWithBorderWrapped);
				
			
				
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(recl.getCantidad());
				cell9.setCellStyle(styleMoneydWithBorder);
			
				
				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(recl.getImporte());
				cell10.setCellStyle(styleMoneydWithBorder);
				
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(recl.getTotal());			
				cell12.setCellStyle(styleMoneydWithBorder);
				
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(String.valueOf(recl.getComprobanteTipo())));			
				cell13.setCellStyle(styleAllWithBorderWrapped);
				
				
				HSSFCell cell131 = row.createCell(column++);
				cell131.setCellValue(new HSSFRichTextString(String.valueOf(recl.getComprobanteLetra())));			
				cell131.setCellStyle(styleAllWithBorderWrapped);
				
				HSSFCell cell14 = row.createCell(column++);
				cell14.setCellValue(new HSSFRichTextString(recl.getComprobanteNro()));			
				cell14.setCellStyle(styleAllWithBorderWrapped);
				
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(sdf2.format(recl.getComprobanteFecha())));
				cell15.setCellStyle(styleAllWithBorderWrapped);
				
				
				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(recl.getComprobanteCantidad());			
				cell16.setCellStyle(styleMoneydWithBorder);
				
	
				
				HSSFCell cell17 = row.createCell(column++);
				cell17.setCellValue(recl.getComprobanteImporte());			
				cell17.setCellStyle(styleMoneydWithBorder);
				
				HSSFCell cell18 = row.createCell(column++);
				cell18.setCellValue(recl.getComprobanteTotal());			
				cell18.setCellStyle(styleMoneydWithBorder);
				
				
	
				HSSFCell cell20 = row.createCell(column++);
				cell20.setCellValue(new HSSFRichTextString(String.valueOf(recl.getComprobanteCUITSucursal())));			
				cell20.setCellStyle(styleAllWithBorderWrapped);
				
				HSSFCell cell19 = row.createCell(column++);
				cell19.setCellValue(new HSSFRichTextString(String.valueOf(recl.getComprobanteCUIT())));			
				cell19.setCellStyle(styleAllWithBorderWrapped);
			
				HSSFCell cell22 = row.createCell(column++);
				cell22.setCellValue(new HSSFRichTextString(String.valueOf(recl.getComprobanteRazonSocial())));			
				cell22.setCellStyle(styleAllWithBorderWrapped);
				
				
				HSSFCell cell23 = row.createCell(column++);
				cell23.setCellValue(new HSSFRichTextString(String.valueOf(recl.getEstado())));			
				cell23.setCellStyle(styleAllWithBorderWrapped);
			  	
				HSSFCell cell25 = row.createCell(column++);
				cell25.setCellValue(recl.getCargoEnsalud());			
				cell25.setCellStyle(styleMoneydWithBorder);
			  	
			
			  	
				
	
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
			sheet.setColumnWidth(10, 10000);
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
		cell.setCellValue(new HSSFRichTextString("Reporte estadística integración " + strDate));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
		
		index = 1;
	
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;
		
		

		HSSFCell cell01 = row3a.createCell(column++);
		cell01.setCellValue(new HSSFRichTextString("N° Reclamo"));
		cell01.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell021 = row3a.createCell(column++);
		cell021.setCellValue(new HSSFRichTextString("Integración"));
		cell021.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell02 = row3a.createCell(column++);
		cell02.setCellValue(new HSSFRichTextString("Apellido"));
		cell02.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell03 = row3a.createCell(column++);
		cell03.setCellValue(new HSSFRichTextString("Nombre"));
		cell03.setCellStyle(styleHeaderEnca2);
		

		HSSFCell cell04 = row3a.createCell(column++);
		cell04.setCellValue(new HSSFRichTextString("Sexo"));
		cell04.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell05 = row3a.createCell(column++);
		cell05.setCellValue(new HSSFRichTextString("Tipo Doc"));
		cell05.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell06 = row3a.createCell(column++);
		cell06.setCellValue(new HSSFRichTextString("Documento"));
		cell06.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell07 = row3a.createCell(column++);
		cell07.setCellValue(new HSSFRichTextString("Código"));
		cell07.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell08 = row3a.createCell(column++);
		cell08.setCellValue(new HSSFRichTextString("Nombre Prestación"));
		cell08.setCellStyle(styleHeaderEnca2);

		HSSFCell cell09 = row3a.createCell(column++);
		cell09.setCellValue(new HSSFRichTextString("Cantidad"));
		cell09.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell10 = row3a.createCell(column++);
		cell10.setCellValue(new HSSFRichTextString("Importe"));
		cell10.setCellStyle(styleHeaderEnca2);
		

		HSSFCell cell11 = row3a.createCell(column++);
		cell11.setCellValue(new HSSFRichTextString("Total"));
		cell11.setCellStyle(styleHeaderEnca2);
		
		

		HSSFCell cell12 = row3a.createCell(column++);
		cell12.setCellValue(new HSSFRichTextString("Tipo Comprobante"));
		cell12.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell13 = row3a.createCell(column++);
		cell13.setCellValue(new HSSFRichTextString("Letra"));
		cell13.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell14 = row3a.createCell(column++);
		cell14.setCellValue(new HSSFRichTextString("Comprobante Nro"));
		cell14.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell15 = row3a.createCell(column++);
		cell15.setCellValue(new HSSFRichTextString("Fecha Comprobante"));
		cell15.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell16 = row3a.createCell(column++);
		cell16.setCellValue(new HSSFRichTextString("Cantidad Comprobante"));
		cell16.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell17 = row3a.createCell(column++);
		cell17.setCellValue(new HSSFRichTextString("Importe Comprobante"));
		cell17.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell18 = row3a.createCell(column++);
		cell18.setCellValue(new HSSFRichTextString("Total Comprobante"));
		cell18.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell19 = row3a.createCell(column++);
		cell19.setCellValue(new HSSFRichTextString("Sucursal"));
		cell19.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell20 = row3a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("CUIT"));
		cell20.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Razon Social"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell22 = row3a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Estado Reclamo"));
		cell22.setCellStyle(styleHeaderEnca2);
		

		HSSFCell cell23 = row3a.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Cargo Ensalud"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		
		
		index = index +1;
		return index;
	}
}
