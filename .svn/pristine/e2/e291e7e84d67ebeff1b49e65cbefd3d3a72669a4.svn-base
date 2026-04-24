package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;

public class ReporteOpcionesExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReporteOpcionesExcel.class);

	public static HSSFWorkbook generaReporteOpcionesSinEnviar(
			HttpServletRequest req, HttpServletResponse res) {

		try {
			List<DetalleOpcionesSS> opcionesxExportar = BusquedaAfiliadoServiceUtil.buscarOpcionesSSSpendientesExportarXls();

			return generarReporte(opcionesxExportar);
		} catch (Exception e) {
			_log.error("Error al generar reporte nuevas opciones", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(List<DetalleOpcionesSS> nuevasOpciones) {
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet);
			index++;
			for (DetalleOpcionesSS opcsss : nuevasOpciones) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(opcsss.getNroFormulario());
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(opcsss.getRegimen()));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(opcsss.getCuil()));
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(opcsss.getApellido()));
				cell3.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(opcsss.getNombre()));
				cell4.setCellStyle(styleAllWithBorder);
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(format.format(opcsss.getFechaCerti())) );
				cell5.setCellStyle(styleAllWithBorder);

			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);

		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Opciones sin exportar al sistema V6 "));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
//		StringBuffer aux = new StringBuffer("Destino: ");
//		aux.append(destino).append(" Lugar Recepción: ").append(lugarRecepcion)
//				.append("Razón Soc. Prestador: " + razonPrestador);
//		aux.append(" Provincia: ").append(provincia).append(" Localidad: ")
//				.append(localidad).append(" Seccional: ").append(seccional);
//		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
//		cell1.setCellStyle(styleHeaderEnca2);
//
//		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Nro. Formulario"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Régimen"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("C.U.I.L."));
		cell32.setCellStyle(styleHeaderEnca2);

		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Apellido"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Nombre"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35= row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Fecha Certificación"));
		cell35.setCellStyle(styleHeaderEnca2);

		return index;
	}
	
}
