package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import  org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.BusquedaConsultasIGSFiltro;
import ar.com.ospim.autorizaciones.beans.ConsultaIGSTotal;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteConsultasIGSExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReporteConsultasIGSExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
		String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
		
		List<ConsultaIGSTotal> busqueda =null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = null;
		try {
			fechaDesde = sdf.parse(fechaDesdeFinal);
		} catch (Exception e) {
			fechaDesde = null;
		}		
		Date fechaHasta = null;
		try {
			fechaHasta = sdf.parse(fechaHastaFinal);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		BusquedaConsultasIGSFiltro filtro = new BusquedaConsultasIGSFiltro(fechaDesde, fechaHasta, 0);

		// la lista en el request
		HttpSession session = (HttpSession) renderRequest.getSession();

		session.removeAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS_FILTRO);
		session.setAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS_FILTRO, filtro);
		
		try {
			busqueda = BusquedaAfiliadoServiceUtil.buscarConsultasIGS_xls(filtro);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte de consultas de IGS", e);
			return null;
		}
		return generarReporte(filtro, busqueda);
	}

	private static HSSFWorkbook generarReporte(BusquedaConsultasIGSFiltro filtro, List<ConsultaIGSTotal> consultasIGS) {
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleAllWithBorderWrapped = getStyleAllWithBorder(wb, 10);
		styleAllWithBorderWrapped.setWrapText(true);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, filtro);
			index++;
			for (ConsultaIGSTotal consIGS : consultasIGS) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(consIGS.getCuilParametro()!=null&&consIGS.getCuilParametro()!="null"?consIGS.getCuilParametro():""));
				cell0.setCellStyle(styleAllWithBorder);
				cell0.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(consIGS.getInteParam()!=null&&String.valueOf(consIGS.getInteParam())!="null"?String.valueOf(consIGS.getInteParam()):""));
				cell1.setCellStyle(styleAllWithBorder);
				cell1.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(consIGS.getDocuTipoParam()!=null&&consIGS.getDocuTipoParam()!="null"?consIGS.getDocuTipoParam():""));
				cell2.setCellStyle(styleAllWithBorder);
				cell2.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(consIGS.getDocuNumeroParam()!=null&&consIGS.getDocuNumeroParam()!="null"?consIGS.getDocuNumeroParam():""));
				cell3.setCellStyle(styleAllWithBorder);
				cell3.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(consIGS.getNroCredencialParam()!=null&&String.valueOf(consIGS.getNroCredencialParam())!="null"?String.valueOf(consIGS.getNroCredencialParam()):""));
				cell4.setCellStyle(styleAllWithBorder);
				cell4.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(sdf2.format(consIGS.getAltaFecha())));
				cell5.setCellStyle(styleAllWithBorder);
				cell5.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(consIGS.getEstado()!=null&&consIGS.getEstado()!="null"?consIGS.getEstado():""));
				cell6.setCellStyle(styleAllWithBorder);
				cell6.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(consIGS.getCuilTitular()!=null&&consIGS.getCuilTitular()!="null"?consIGS.getCuilTitular():""));
				cell7.setCellStyle(styleAllWithBorder);
				cell7.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(consIGS.getInte()!=null&&String.valueOf(consIGS.getInte())!="null"?String.valueOf(consIGS.getInte()):""));
				cell8.setCellStyle(styleAllWithBorder);
				cell8.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(new HSSFRichTextString(consIGS.getDocuTipo()!=null&&consIGS.getDocuTipo()!="null"?consIGS.getDocuTipo():""));
				cell9.setCellStyle(styleAllWithBorder);
				cell9.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(new HSSFRichTextString(consIGS.getDocuNumero()!=null&&consIGS.getDocuNumero()!="null"?consIGS.getDocuNumero():""));
				cell10.setCellStyle(styleAllWithBorder);
				cell10.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(consIGS.getApellido()!=null&&consIGS.getApellido()!="null"?consIGS.getApellido():""));
				cell11.setCellStyle(styleAllWithBorder);
				cell11.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(new HSSFRichTextString(consIGS.getNombre()!=null&&consIGS.getNombre()!="null"?consIGS.getNombre():""));
				cell12.setCellStyle(styleAllWithBorder);
				cell12.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(consIGS.getPlan()!=null&&consIGS.getPlan()!="null"?consIGS.getPlan():""));
				cell13.setCellStyle(styleAllWithBorder);
				cell13.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell14 = row.createCell(column++);
				cell14.setCellValue(new HSSFRichTextString(consIGS.getNroCredencial()!=null&&String.valueOf(consIGS.getNroCredencial())!="null"?String.valueOf(consIGS.getNroCredencial()):""));
				cell14.setCellStyle(styleAllWithBorder);
				cell14.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(consIGS.getProvincia()!=null?consIGS.getProvincia():""));
				cell15.setCellStyle(styleAllWithBorder);
				cell15.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(consIGS.getLocalidad()!=null?consIGS.getLocalidad():""));
				cell16.setCellStyle(styleAllWithBorder);
				cell16.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell17 = row.createCell(column++);
				cell17.setCellValue(new HSSFRichTextString(consIGS.getIp()!=null&&consIGS.getIp()!="null"?consIGS.getIp():""));
				cell17.setCellStyle(styleAllWithBorder);
				cell17.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
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
			sheet.autoSizeColumn((short) 14);
			sheet.autoSizeColumn((short) 15);
			sheet.autoSizeColumn((short) 16);
			sheet.autoSizeColumn((short) 17);
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, BusquedaConsultasIGSFiltro filtro) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Consultas IGS"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer("Período: " + sdf2.format(filtro.getFechaDesde().getTime()) + " al " +  sdf2.format(filtro.getFechaHasta().getTime()));
				
		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 17));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell20 = row3a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Parámetro CUIL"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Parámetro Integrante"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell22 = row3a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Parámetro Tipo Doc."));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row3a.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Parámetro Número Doc."));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row3a.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Parámetro Nro. Credencial"));
		cell24.setCellStyle(styleHeaderEnca2);

		HSSFCell cell251 = row3a.createCell(column++);
		cell251.setCellValue(new HSSFRichTextString("Alta Fecha"));
		cell251.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row3a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Estado"));
		cell25.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell26 = row3a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("CUIL Titular"));
		cell26.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell27 = row3a.createCell(column++);
		cell27.setCellValue(new HSSFRichTextString("Integrante"));
		cell27.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell28 = row3a.createCell(column++);
		cell28.setCellValue(new HSSFRichTextString("Tipo Doc."));
		cell28.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell29 = row3a.createCell(column++);
		cell29.setCellValue(new HSSFRichTextString("Número Doc."));
		cell29.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Apellido"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Nombre"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Plan"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Nro. Credencial"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Provincia"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Localidad"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("IP"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		return index;
	}
}