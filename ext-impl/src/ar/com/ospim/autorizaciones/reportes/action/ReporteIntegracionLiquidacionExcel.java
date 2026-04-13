package ar.com.ospim.autorizaciones.reportes.action;

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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteIntegracionLiquidacionExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteIntegracionLiquidacionExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		
		Integer idLote = ParamUtil.getInteger(renderRequest, "id");
		String tercerizadora = ParamUtil.getString(renderRequest, "tercerizadora");
		   
		List<IntegracionDetalleDS>lista = new ArrayList<IntegracionDetalleDS>();
		List<IntegracionDetalleDS>reporte = new ArrayList<IntegracionDetalleDS>();
		
		if("CABECERA".equals(tercerizadora)) {
		  reporte=IntegracionServiceUtil.detalleLiquidacionByIdCab(idLote);	
		}else {
		   lista=IntegracionServiceUtil.detalleLiquidacionByIdLote(idLote);
		   if(!"0".equalsIgnoreCase(tercerizadora)) {
			for(IntegracionDetalleDS d:lista) {
				if(d.getTercerizadoraId().equalsIgnoreCase(tercerizadora)) {
					reporte.add(d);
				}
			}
		   }else {
		     reporte=lista;	
		   }
		}   
		
		
		return generaReporte(reporte,idLote);
		
	}

	private static HSSFWorkbook generaReporte(
			List<IntegracionDetalleDS> list,Integer idLote) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Detalle Liquidación Integracion");

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
				
		StringBuffer titulo1=new StringBuffer("Reporte Liquidación Lote Integracion: "+idLote.toString() + " ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Orden de Pago"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Fecha  O.Pago"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("CUIT Prestador"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Descripción"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Comprobante"));
		cell18H.setCellStyle(styleBold);
		
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Importe Comprobante"));
		cell19H.setCellStyle(styleBold);
		
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("CBU"));
		cell21H.setCellStyle(styleBold);
		
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell13H.setCellStyle(styleBold);
		
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Importe Orden de Pago"));
		cell23H.setCellStyle(styleBold);
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Estado OP"));
		cell24H.setCellStyle(styleBold);
		
   	    index++;
		
		for(IntegracionDetalleDS det: list){
			index=crearDatos(sheet, det, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		
		HSSFRow rowTotales=sheet.createRow(index);
		
		HSSFCell cell17F = rowTotales.createCell(4);
		cell17F.setCellValue(new HSSFRichTextString("TOTAL"));
		cell17F.setCellStyle(styleBold);
		
		HSSFCell cell3F = rowTotales.createCell(5);
		cell3F.setCellFormula("SUM(F"+Integer.toString(3)  +":F"+ Integer.toString(index-1) +")");
		cell3F.setCellStyle(styleMoney);
		
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

	private static int crearDatos(HSSFSheet sheet,IntegracionDetalleDS det, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(det.getOrdenPago());
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell028 = rowHeader.createCell(++col);
		cell028.setCellValue(new HSSFRichTextString(det.getOpFecha()));
		cell028.setCellStyle(styleAll);
		
		HSSFCell cell022 = rowHeader.createCell(++col);
		cell022.setCellValue(new HSSFRichTextString(det.getCuitPrestador()));
		cell022.setCellStyle(styleAll);
				
		HSSFCell cell023 = rowHeader.createCell(++col);
		cell023.setCellValue(new HSSFRichTextString(det.getDescripcionPrestador()));
		cell023.setCellStyle(styleAll);
		
		HSSFCell cell024 = rowHeader.createCell(++col);
		cell024.setCellValue(new HSSFRichTextString(det.getComprobanteString()));
		cell024.setCellStyle(styleAll);
		
		HSSFCell cell025 = rowHeader.createCell(++col);
		cell025.setCellValue(det.getComprobanteImporte());
		cell025.setCellStyle(styleMoney);
		
		HSSFCell cell026 = rowHeader.createCell(++col);
		cell026.setCellValue(new HSSFRichTextString(det.getCbu()));
		cell026.setCellStyle(styleAll);
		
		HSSFCell cell027 = rowHeader.createCell(++col);
		cell027.setCellValue(new HSSFRichTextString(det.getTercerizadora()));
		cell027.setCellStyle(styleAll);
		
		
		HSSFCell cell029 = rowHeader.createCell(++col);
		cell029.setCellValue(det.getOpImporte());
		cell029.setCellStyle(styleMoney);
		
		
		if(det.getOrdenPago()!=null) {
			String estado="";
			try {
				OrdenPagoOspim op =OrdenPagoServiceUtil.getOrdenPagoOspim(det.getOrdenPago());
				estado=op.getBaja_fecha()==null?"":"ANULADA";
			} catch (Exception e) {}
			HSSFCell cell030 = rowHeader.createCell(++col);
			cell030.setCellValue(new HSSFRichTextString(estado));
			cell030.setCellStyle(styleMoney);
		}
		
		rowHeader.setHeight((short) 0);
		return index++;
	}
        
}


