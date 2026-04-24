package ar.com.ospim.farmacia.reportes;

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

import ar.com.ospim.farmacia.beans.Vademecumreporte;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosFarmaciaServiceImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class GeneraVademecumAltasBajasXLS extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(GeneraVademecumAltasBajasXLS.class);
	public enum solapaExcelVademecum  {
	    ALTAS_SSS, BAJAS_SSS, BAJAS_MANUAL_DAT , DROGAS_DE_BAJA
	}
	public static HSSFWorkbook generaVademecumAltasBajas(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			ProcesaArchivosFarmaciaServiceImpl farmImpl = new ProcesaArchivosFarmaciaServiceImpl();
			List<Vademecumreporte> vademecum = farmImpl.getVademecumAltasBajas();
			return generarVademecum(vademecum);
		} catch (Exception e) {
			_log.error("Error al generar Reporte Altas Bajas Vademecum", e);
			return null;
		}
	}

	private static HSSFWorkbook generarVademecum(List<Vademecumreporte> vademecum) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFSheet sheet = wb.createSheet("Altas SSS");
		try {
			int index = createHeader(wb, sheet, solapaExcelVademecum.ALTAS_SSS );
			
			for (Vademecumreporte regVade : vademecum) {
				int column=0;
				if (regVade.getTipoDatoVademecum() ==0) {
					HSSFRow row = sheet.createRow(index++);
					HSSFCell cell0 = row.createCell(column++);					
					cell0.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getPeriodoAltasBajas())  ));
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell1 = row.createCell(column++);					
					cell1.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getRegistro() ) ));
					cell1.setCellStyle(styleAllWithBorder);
					HSSFCell cell2 = row.createCell(column++);
					cell2.setCellValue(new HSSFRichTextString(regVade.getNombre().toUpperCase()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(column++);
					cell3.setCellValue(new HSSFRichTextString(regVade.getPresentacion().toUpperCase()));
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(column++);
					cell4.setCellValue(new HSSFRichTextString(regVade.getLaboratorio().toUpperCase()));
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(column++);
					cell5.setCellValue(new HSSFRichTextString(regVade.getDroga().toUpperCase()));
					cell5.setCellStyle(styleAllWithBorder);
					
				}
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);
			
			HSSFSheet sheet2 = wb.createSheet("Bajas SSS");						
			index = createHeader(wb, sheet2, solapaExcelVademecum.BAJAS_SSS );
			
			for (Vademecumreporte regVade : vademecum) {
				int column=0;
				if (regVade.getTipoDatoVademecum() ==1) {
					HSSFRow row = sheet2.createRow(index++);
					HSSFCell cell0 = row.createCell(column++);
					cell0.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getPeriodoAltasBajas())  ));
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell1 = row.createCell(column++);					
					cell1.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getRegistro() ) ));
					cell1.setCellStyle(styleAllWithBorder);
					HSSFCell cell2 = row.createCell(column++);
					cell2.setCellValue(new HSSFRichTextString(regVade.getNombre().toUpperCase()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(column++);
					cell3.setCellValue(new HSSFRichTextString(regVade.getPresentacion().toUpperCase()));
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(column++);
					cell4.setCellValue(new HSSFRichTextString(regVade.getLaboratorio().toUpperCase()));
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(column++);
					cell5.setCellValue(new HSSFRichTextString(regVade.getDroga().toUpperCase()));
					cell5.setCellStyle(styleAllWithBorder);
					
				}
			}

			sheet2.autoSizeColumn((short) 0);
			sheet2.autoSizeColumn((short) 1);
			sheet2.autoSizeColumn((short) 2);
			sheet2.autoSizeColumn((short) 3);
			sheet2.autoSizeColumn((short) 4);
			sheet2.autoSizeColumn((short) 5);
			sheet2.autoSizeColumn((short) 6);

			HSSFSheet sheet3 = wb.createSheet("Bajas Medicación");			
			index = createHeader(wb, sheet3, solapaExcelVademecum.BAJAS_MANUAL_DAT );
			
			for (Vademecumreporte regVade : vademecum) {
				int column=0;
				if (regVade.getTipoDatoVademecum() ==2) {  
					HSSFRow row = sheet3.createRow(index++);
					
					HSSFCell cell0 = row.createCell(column++);
					cell0.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getPeriodoAltasBajas())  ));
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell1 = row.createCell(column++);					
					cell1.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getRegistro() ) ));
					cell1.setCellStyle(styleAllWithBorder);
					HSSFCell cell2 = row.createCell(column++);
					cell2.setCellValue(new HSSFRichTextString(regVade.getNombre().toUpperCase()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(column++);
					cell3.setCellValue(new HSSFRichTextString(regVade.getPresentacion().toUpperCase()));
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(column++);
					cell4.setCellValue(new HSSFRichTextString(regVade.getLaboratorio().toUpperCase()));
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(column++);
					cell5.setCellValue(new HSSFRichTextString(regVade.getDroga().toUpperCase()));
					cell5.setCellStyle(styleAllWithBorder);
					HSSFCell cell6 = row.createCell(column++);
					cell6.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getCantidadGenericos() ) ));
					cell6.setCellStyle(styleAllWithBorder);
					
					
				}
			}

			sheet3.autoSizeColumn((short) 0);
			sheet3.autoSizeColumn((short) 1);
			sheet3.autoSizeColumn((short) 2);
			sheet3.autoSizeColumn((short) 3);
			sheet3.autoSizeColumn((short) 4);
			sheet3.autoSizeColumn((short) 5);
			sheet3.autoSizeColumn((short) 6);
			
			HSSFSheet sheet4 = wb.createSheet("Genéricos vigentes en Vademécum actual");			
			index = createHeader(wb, sheet4, solapaExcelVademecum.DROGAS_DE_BAJA );
			
			for (Vademecumreporte regVade : vademecum) {
				int column=0;
				if (regVade.getTipoDatoVademecum() ==3) { 
					HSSFRow row = sheet4.createRow(index++);
					
					HSSFCell cell0 = row.createCell(column++);
					cell0.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getPeriodoAltasBajas())  ));
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell1 = row.createCell(column++);					
					cell1.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getRegistro() ) ));
					cell1.setCellStyle(styleAllWithBorder);
					HSSFCell cell2 = row.createCell(column++);
					cell2.setCellValue(new HSSFRichTextString(regVade.getNombre().toUpperCase()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(column++);
					cell3.setCellValue(new HSSFRichTextString(regVade.getPresentacion().toUpperCase()));
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(column++);
					cell4.setCellValue(new HSSFRichTextString(regVade.getLaboratorio().toUpperCase()));
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(column++);
					cell5.setCellValue(new HSSFRichTextString(regVade.getDroga().toUpperCase()));
					cell5.setCellStyle(styleAllWithBorder);
					
				}
			}

			sheet4.autoSizeColumn((short) 0);
			sheet4.autoSizeColumn((short) 1);
			sheet4.autoSizeColumn((short) 2);
			sheet4.autoSizeColumn((short) 3);
			sheet4.autoSizeColumn((short) 4);
			sheet4.autoSizeColumn((short) 5);
			sheet4.autoSizeColumn((short) 6);


		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, solapaExcelVademecum    solapaExcel) {
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 12);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorder(wb, 10);
		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		
		switch (solapaExcel)
		{
		case  ALTAS_SSS   :
			cell.setCellValue(new HSSFRichTextString("NUEVOS MEDICAMENTOS INFORMADOS POR LA SUPER INTENDENCIA EN EL PERIODO (ALTAS)" ));
		  break;
		case BAJAS_SSS:
			cell.setCellValue(new HSSFRichTextString("MEDICAMENTOS NO INFORMADOS POR LA SUPER INTENDENCIA EN EL PERIODO (BAJAS)"));
		  break;
		case BAJAS_MANUAL_DAT:
			cell.setCellValue(new HSSFRichTextString("MEDICAMENTOS NO INFORMADOS EN EL MANUAL DAT (BAJAS)"));
		  break;
		case  DROGAS_DE_BAJA:
			cell.setCellValue(new HSSFRichTextString("GENERICOS VIGENTES EN VADEMECUM ACTUAL"));
		  break;  
		}
		
		cell.setCellStyle(styleHeaderEnca);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
		
		HSSFRow row2 = sheet.createRow(index++);
		int column=0;
		HSSFCell cell1 = row2.createCell(column++);
		cell1.setCellStyle(styleHeaderEnca2);
		cell1.setCellValue(new HSSFRichTextString("Período"));
		HSSFCell cell2 = row2.createCell(column++);
		cell2.setCellStyle(styleHeaderEnca2);
		cell2.setCellValue(new HSSFRichTextString("Registro"));
		HSSFCell cell3 = row2.createCell(column++);
		cell3.setCellStyle(styleHeaderEnca2);
		cell3.setCellValue(new HSSFRichTextString("Nombre"));
		HSSFCell cell4 = row2.createCell(column++);
		cell4.setCellStyle(styleHeaderEnca2);
		cell4.setCellValue(new HSSFRichTextString("Presentación"));
		HSSFCell cell5 = row2.createCell(column++);
		cell5.setCellStyle(styleHeaderEnca2);
		cell5.setCellValue(new HSSFRichTextString("Laboratorio"));
		HSSFCell cell6 = row2.createCell(column++);
		cell6.setCellStyle(styleHeaderEnca2);
		cell6.setCellValue(new HSSFRichTextString("Droga"));
		if (solapaExcel==solapaExcelVademecum.BAJAS_MANUAL_DAT){
			HSSFCell cell7 = row2.createCell(column++);
			cell7.setCellStyle(styleHeaderEnca2);
			cell7.setCellValue(new HSSFRichTextString("Cant Genéricos"));
		}
		return index;
	}

}
