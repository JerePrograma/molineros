package ar.com.uoma.centro_costo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import  org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.beans.CentroCosto;

public class ReporteCentrosDeCostoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCentrosDeCostoExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		List<CentroCosto> centros=(List<CentroCosto>)renderRequest.getSession().getAttribute(WebKeysUOMA.CENTRO_COSTO_FILTRO);
		
		return generaReporte(centros);
	}

	private static HSSFWorkbook generaReporte(
			List<CentroCosto> list) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Centros de Costo");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte De Centros de Costo");
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle( getStyleBoldAligned(wb, HorizontalAlignment.CENTER));
		
        index ++;
		HSSFRow rowHeaderANT1 = sheet.createRow(index);
		HSSFCell cell9HA = rowHeaderANT1.createCell(9);
		cell9HA.setCellValue(new HSSFRichTextString("Impreso: "+ sdf.format(hoy)));
		cell9HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Id"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Descripcion"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Presupuesto"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Ejecutado"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Saldo"));
		cell19H.setCellStyle(styleBold);
		
		index++;
		
		for(CentroCosto seguimiento: list){
			index=crearDatos(sheet, seguimiento, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		sheet.createRow(index);
		
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
		sheet.autoSizeColumn((short) 28);
		sheet.autoSizeColumn((short) 29);
		sheet.autoSizeColumn((short) 30);
		sheet.autoSizeColumn((short) 31);
		sheet.autoSizeColumn((short) 32);
		sheet.autoSizeColumn((short) 33);
		sheet.autoSizeColumn((short) 34);
		sheet.autoSizeColumn((short) 35);
		
		return wb;
	}

	private static int crearDatos(HSSFSheet sheet,CentroCosto pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(pre.getId());
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(pre.getDescripcion()));
		cell020.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(pre.getPresupuesto());
		cell002.setCellStyle(styleMoney);
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		cell017.setCellValue(pre.getEjecutado());
		cell017.setCellStyle(styleMoney);
		
		HSSFCell cell019 = rowHeader.createCell(++col);
		cell019.setCellValue(pre.getPresupuesto() - pre.getEjecutado());
		cell019.setCellStyle(styleMoney);
		
        rowHeader.setHeight((short) 0);
		return index++;
	}
	
	public static HSSFWorkbook generaReporteDetalle(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		CentroCosto centro=(CentroCosto)renderRequest.getSession().getAttribute(WebKeysUOMA.CENTRO_COSTO_EN_EDICION);
		Integer entidad = ParamUtil.getInteger(renderRequest, "entidad_centro");
		List<ComprobanteCajaChica> cptes = CentroCostoServiceUtil.comprobantesPorCentroCosto(centro.getId(), entidad, null);
		return generaReporteDetalle(centro,cptes);
	}
    
	
	private static HSSFWorkbook generaReporteDetalle(CentroCosto centro,
			List<ComprobanteCajaChica> list) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Detalle");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte De Centros de Costo Detallado: "+centro.getDescripcion() );
	    
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		
		int index = 0;		
		int col = -1;
		
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle( getStyleBoldAligned(wb, HorizontalAlignment.CENTER));
		index ++;
		
		HSSFRow rowHeaderANT1 = sheet.createRow(index);
		HSSFCell cell9HA = rowHeaderANT1.createCell(9);
		cell9HA.setCellValue(new HSSFRichTextString("Impreso: "+ sdf.format(hoy)));
		cell9HA.setCellStyle(styleBold);
/*		
		index ++;
		HSSFRow rowHeader000 = sheet.createRow(index);
		HSSFCell cell000H = rowHeader000.createCell(++col);
		cell000H.setCellValue(new HSSFRichTextString("Centro: "));
		cell000H.setCellStyle(styleBold);
		
		HSSFCell cell001H = rowHeader000.createCell(++col);
		cell001H.setCellValue(new HSSFRichTextString(centro.getDescripcion()));
		cell001H.setCellStyle(styleBold);
*/		
		index ++;
		index ++;
		
		HSSFRow rowHeader00 = sheet.createRow(index);
		col=-1;
		HSSFCell cell002H = rowHeader00.createCell(++col);
		cell002H.setCellValue(new HSSFRichTextString("Presupuesto: "));
		cell002H.setCellStyle(styleBold);
		
		HSSFCell cell003H = rowHeader00.createCell(++col);
		cell003H.setCellValue(centro.getPresupuesto());
		cell003H.setCellStyle(styleMoney);
		
		index ++;
		rowHeader00 = sheet.createRow(index);
		col=-1;
		HSSFCell cell004H = rowHeader00.createCell(++col);
		cell004H.setCellValue(new HSSFRichTextString("Ejecutado: "));
		cell004H.setCellStyle(styleBold);
		
		HSSFCell cell005H = rowHeader00.createCell(++col);
		cell005H.setCellValue(centro.getEjecutado());
		cell005H.setCellStyle(styleMoney);
		
		index ++;
		rowHeader00 = sheet.createRow(index);
		col=-1; 
		HSSFCell cell006H = rowHeader00.createCell(++col);
		cell006H.setCellValue(new HSSFRichTextString("Saldo: "));
		cell006H.setCellStyle(styleBold);
		
		HSSFCell cell007H = rowHeader00.createCell(++col);
		cell007H.setCellValue(centro.getPresupuesto()-centro.getEjecutado());
		cell007H.setCellStyle(styleMoney);
		
		
		col=-1;
		index++;
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Fecha"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Tipo"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Letra"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Pto.Venta"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Nro"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Cuit"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Razon Social"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Concepto"));
		cell23H.setCellStyle(styleBold);
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Importe"));
		cell24H.setCellStyle(styleBold);
		index++;
		
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("OP"));
		cell25H.setCellStyle(styleBold);
		index++;
		
		for(ComprobanteCajaChica seguimiento: list){
			index=crearDatosDetalle(sheet, seguimiento, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		sheet.createRow(index);
		
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
		sheet.autoSizeColumn((short) 28);
		sheet.autoSizeColumn((short) 29);
		sheet.autoSizeColumn((short) 30);
		sheet.autoSizeColumn((short) 31);
		sheet.autoSizeColumn((short) 32);
		sheet.autoSizeColumn((short) 33);
		sheet.autoSizeColumn((short) 34);
		sheet.autoSizeColumn((short) 35);
		
		return wb;
	}

	
	private static int crearDatosDetalle(HSSFSheet sheet,ComprobanteCajaChica pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(new HSSFRichTextString(pre.getFechaEmisionAsString()));
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(pre.getTipoComprobante() ));
		cell020.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(pre.getLetraComprobante()));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell003 = rowHeader.createCell(++col);
		cell003.setCellValue(pre.getPtoVenta());
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		cell017.setCellValue(new HSSFRichTextString(pre.getNroComprobante()));
		cell017.setCellStyle(styleAll);
		
		HSSFCell cell019 = rowHeader.createCell(++col);
		cell019.setCellValue(new HSSFRichTextString(pre.getCuit()));
		cell019.setCellStyle(styleAll);
		
		HSSFCell cell021 = rowHeader.createCell(++col);
		cell021.setCellValue(new HSSFRichTextString(pre.getAcreedorEmpresa().getRazon_soc()));
		cell021.setCellStyle(styleAll);
		
		HSSFCell cell022 = rowHeader.createCell(++col);
		cell022.setCellValue(new HSSFRichTextString(pre.getConceptos().get(0).getConceptoComprobante().getDescripcion()));
		cell022.setCellStyle(styleAll);
		
		HSSFCell cell023 = rowHeader.createCell(++col);
		cell023.setCellValue(pre.getImporteComprobante().doubleValue());
		cell023.setCellStyle(styleMoney);
		
		HSSFCell cell024 = rowHeader.createCell(++col);
		cell024.setCellValue(pre.getOrdenPago());
		cell024.setCellStyle(styleAll);
		
        rowHeader.setHeight((short) 0);
		return index++;
	}
}


