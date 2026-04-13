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

import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.BusquedaVademecumFiltro;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosFarmaciaServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class GeneraVademecumXLS extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(GeneraVademecumXLS.class);

	public static HSSFWorkbook generaVademecum(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			List<Vademecum> vademecum=null; 
			ProcesaArchivosFarmaciaServiceImpl farmImpl = new ProcesaArchivosFarmaciaServiceImpl();
		
			BusquedaVademecumFiltro   filtroBusquedaVademecum   = new BusquedaVademecumFiltro(
					 ParamUtil.getBoolean(req, "pmiHijo") ,ParamUtil.getBoolean(req, "pmiMadre") 	,
					 ParamUtil.getBoolean(req, "todosLosPadrones")  ,ParamUtil.getBoolean(req, "anticonceptivo") ,
					 ParamUtil.getBoolean(req, "vadeGral") ,  ParamUtil.getBoolean(req, "molineros")  
					);
            if ( filtroBusquedaVademecum.isTodosLosPadrones() ){
            	vademecum = farmImpl.getVademecum();
            }else{
            	vademecum = farmImpl.getVademecum(filtroBusquedaVademecum);
            }
			return generarVademecum(vademecum,filtroBusquedaVademecum.getPadronDescripcionFiltros(), filtroBusquedaVademecum.getDescripcionTipoPadron()  );
		} catch (Exception e) {
			_log.error("Error al generar Reporte vademecum ", e);
			return null;
		}
	}

	private static HSSFWorkbook generarVademecum(List<Vademecum> vademecum , String tituloExcel , String descripcionTipoPadron) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleMoneyRight = getStyleMoneyWithBorder(wb,10);		
		HSSFSheet sheet = wb.createSheet(descripcionTipoPadron);
		
		try {

			int index = createHeaderPMO(wb, sheet, tituloExcel  );
			
			for (Vademecum regVade : vademecum) {
				int column=0;
					HSSFRow row = sheet.createRow(index++);
					HSSFCell cell0 = row.createCell(column++);					
					cell0.setCellValue(regVade.getRegistro());
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell1 = row.createCell(column++);
					cell1.setCellValue(regVade.getTroquel());
					cell1.setCellStyle(styleAllWithBorder);
					HSSFCell cell2 = row.createCell(column++);
					cell2.setCellValue(new HSSFRichTextString(regVade.getDroga().toUpperCase()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(column++);
					cell3.setCellValue(new HSSFRichTextString(regVade.getNombre().toUpperCase()));
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(column++);
					cell4.setCellValue(regVade.getUnidades());
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(column++);
					cell5.setCellValue(new HSSFRichTextString(regVade.getPresentacion().toUpperCase()));
					cell5.setCellStyle(styleAllWithBorder);
					HSSFCell cell6 = row.createCell(column++);
					cell6.setCellValue(new HSSFRichTextString(regVade.getAccion()!=null?regVade.getAccion().toUpperCase():""));
					cell6.setCellStyle(styleAllWithBorder);
					HSSFCell cell7 = row.createCell(column++);
					cell7.setCellValue(new HSSFRichTextString(regVade.getLaboratorio().toUpperCase()));
					cell7.setCellStyle(styleAllWithBorder);
					HSSFCell cell8 = row.createCell(column++);
					cell8.setCellValue(new HSSFRichTextString(String.valueOf(regVade.getPorc_sssalud()).substring(0,2)));
					cell8.setCellStyle(styleAllWithBorder);					
					HSSFCell cell9 = row.createCell(column++);
					cell9.setCellStyle(styleMoneyRight);
					cell9.setCellValue(regVade.getPmoe_n());
					cell9.setCellStyle(styleAllWithBorder);					
					HSSFCell cell10 = row.createCell(column++);
					cell10.setCellValue(new HSSFRichTextString(regVade.isAnticoncepcion()?"SI":"NO"));
					cell10.setCellStyle(styleAllWithBorder);					
					HSSFCell cell11 = row.createCell(column++);					
					cell11.setCellValue(new HSSFRichTextString(regVade.isPmiHijo()?"SI":"NO"));
					cell11.setCellStyle(styleAllWithBorder);					
					HSSFCell cell12 = row.createCell(column++);
					cell12.setCellValue(new HSSFRichTextString(regVade.isPmiMadre()?"SI":"NO"));
					cell12.setCellStyle(styleAllWithBorder);					
					HSSFCell cell13 = row.createCell(column++);
					cell13.setCellValue(new HSSFRichTextString(regVade.isVademecumGral()?"SI":"NO"));
					cell13.setCellStyle(styleAllWithBorder);
				
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
			sheet.autoSizeColumn((short) 10);
			sheet.autoSizeColumn((short) 11);
			sheet.autoSizeColumn((short) 12);
			sheet.autoSizeColumn((short) 13);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeaderPMO(HSSFWorkbook wb, HSSFSheet sheet , String tituloExcel ) {
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb,13);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorder(wb, 10);
		
		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(tituloExcel ));
		cell.setCellStyle(styleHeaderEnca);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

		HSSFRow row2 = sheet.createRow(index++);
		int column=0;
		HSSFCell cell0 = row2.createCell(column++);
		cell0.setCellStyle(styleHeaderEnca2);
		cell0.setCellValue(new HSSFRichTextString("Registro"));
		HSSFCell cell1 = row2.createCell(column++);
		cell1.setCellStyle(styleHeaderEnca2);
		cell1.setCellValue(new HSSFRichTextString("Troquel"));
		HSSFCell cell2 = row2.createCell(column++);
		cell2.setCellStyle(styleHeaderEnca2);
		cell2.setCellValue(new HSSFRichTextString("Principio Activo"));
		HSSFCell cell3 = row2.createCell(column++);
		cell3.setCellStyle(styleHeaderEnca2);
		cell3.setCellValue(new HSSFRichTextString("Nombre"));
		HSSFCell cell4 = row2.createCell(column++);
		cell4.setCellStyle(styleHeaderEnca2);
		cell4.setCellValue(new HSSFRichTextString("Contenido"));
		HSSFCell cell5 = row2.createCell(column++);
		cell5.setCellStyle(styleHeaderEnca2);
		cell5.setCellValue(new HSSFRichTextString("Presentación"));
		HSSFCell cell6 = row2.createCell(column++);
		cell6.setCellStyle(styleHeaderEnca2);
		cell6.setCellValue(new HSSFRichTextString("Acción"));
		HSSFCell cell7 = row2.createCell(column++);
		cell7.setCellStyle(styleHeaderEnca2);
		cell7.setCellValue(new HSSFRichTextString("Laboratorio"));		
		HSSFCell cell8 = row2.createCell(column++);
		cell8.setCellStyle(styleHeaderEnca2);
		cell8.setCellValue(new HSSFRichTextString("% SSS"));		
		HSSFCell cell9= row2.createCell(column++);
		cell9.setCellStyle(styleHeaderEnca2);
		cell9.setCellValue(new HSSFRichTextString("M F O.S."));				
		HSSFCell cell10 = row2.createCell(column++);
		cell10.setCellStyle(styleHeaderEnca2);
		cell10.setCellValue(new HSSFRichTextString("ACO"));		
		HSSFCell cell11 = row2.createCell(column++);
		cell11.setCellStyle(styleHeaderEnca2);
		cell11.setCellValue(new HSSFRichTextString("PMI Hijo"));		
		HSSFCell cell12 = row2.createCell(column++);
		cell12.setCellStyle(styleHeaderEnca2);
		cell12.setCellValue(new HSSFRichTextString("PMI Madre"));		
		HSSFCell cell13 = row2.createCell(column++);
		cell13.setCellStyle(styleHeaderEnca2);
		cell13.setCellValue(new HSSFRichTextString("Vade General"));		
		
		
		return index;
	}

}
