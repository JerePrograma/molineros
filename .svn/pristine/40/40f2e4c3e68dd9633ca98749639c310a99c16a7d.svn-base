package ar.com.uoma.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReportePercepcionesIIBBExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePercepcionesIIBBExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		Integer entidad = ParamUtil.getInteger(renderRequest, "entidad");
		String fechaDesdeDia = ParamUtil.getString(renderRequest, "fechadesdedia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest, "fechadesdemes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechadesdeanio");
		String fechaHastaDia = ParamUtil.getString(renderRequest, "fechahastadia");
		String fechaHastaMes = ParamUtil.getString(renderRequest, "fechahastames");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(renderRequest, "fechahastaanio");
		Integer concepto= ParamUtil.getInteger(renderRequest, "concepto");
		Date fechaIni=new Date();
		Date fechaFin=new Date();
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
		} catch (Exception e) {
			_log.error("Error al generar reporte percepcion IIBB", e);
			return null;
		}
		
		
		List<Comprobante> ops=new ArrayList<Comprobante>();
		List<Localidad> percepciones = TraeListasServiceUtil.getPercepcionesIIBB(entidad);
		try {
			ops = ComprobanteServiceUtil.getComprobantesIIBB(fechaIni, fechaFin ,entidad,concepto, null);
		} catch (SystemException e) {
			_log.debug(e.getMessage());
		}
		return generaReporte(ops,fechaIni,fechaFin,percepciones);
	}

	private static HSSFWorkbook generaReporte(
			List<Comprobante> list,Date fechaIni,Date fechaFin,List<Localidad>percepciones) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Percepciones");

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
				
		StringBuffer titulo1=new StringBuffer("Percepcion Ingresos Brutos desde "+sdf.format(fechaIni)+" hasta el "+sdf.format(fechaFin));
	
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
		cell16H.setCellValue(new HSSFRichTextString("CUIT"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Fecha"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Monto"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Jurisdicción"));
		cell19H.setCellStyle(styleBold);
		
		index++;
		
		for(Comprobante seguimiento: list){
			index=crearDatos(sheet, seguimiento,percepciones, index, styleAll,
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

	private static int crearDatos(HSSFSheet sheet,Comprobante pre,List<Localidad> percepciones, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
		SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
		String descripcion="";
		for(Localidad l:percepciones) {
			if(l.getId_provincia()==pre.getConceptos().get(0).getJurisdiccionIIBB()) {
				descripcion=l.getDescripcion();
				break;
			}
		}
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(new HSSFRichTextString(pre.getAcreedorEmpresa().getCuit() ));
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(pre.getAcreedorEmpresa().getRazon_soc()));
		cell020.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(sdf.format( pre.getFechaEmision())));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		cell017.setCellValue(pre.getConceptos().get(0).getPercepcionIIBB().doubleValue());
		cell017.setCellStyle(styleMoney);
		
		HSSFCell cell019 = rowHeader.createCell(++col);
		cell019.setCellValue(new HSSFRichTextString(pre.getConceptos().get(0).getJurisdiccionIIBB().toString() + " " + descripcion ));
		cell019.setCellStyle(styleAll);
		
        rowHeader.setHeight((short) 0);
		return index++;
	}
}


