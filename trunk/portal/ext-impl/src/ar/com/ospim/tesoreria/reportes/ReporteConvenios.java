package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
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
import  org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ReporteConvenioBean;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

public class ReporteConvenios extends ReporteXLS {

	private static Log _log = LogFactoryUtil.getLog(ReporteConvenios.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			String amtimaStr=(String)req.getAttribute("amtima");
			if(null==amtimaStr){
				amtimaStr=req.getParameter("amtima");
			}
			
			int entidad=WebKeysGlobal.OSPIM;
			if(null!= amtimaStr && amtimaStr.trim().equals("true")){
				entidad=WebKeysGlobal.AMTIMA;
			}
			
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<ReporteConvenioBean> reporte = ConvenioServiceUtil
					.reporteConvenios(fechaIni, fechaFin, entidad);
			return generarReporte(fechaIni, fechaFin, reporte, false);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}
	
	public static HSSFWorkbook generarReporteSeguimiento(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			String amtimaStr=(String)req.getAttribute("amtima");
			if(null==amtimaStr){
				amtimaStr=req.getParameter("amtima");
			}
			int entidad=WebKeysGlobal.OSPIM;
			if(null!= amtimaStr && amtimaStr.trim().equals("true")){
				entidad=WebKeysGlobal.AMTIMA;
			}
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<ReporteConvenioBean> reporte = ConvenioServiceUtil
					.reporteConvenios(fechaIni, fechaFin, entidad);
			reporte.addAll(ConvenioNoOSServiceUtil.reporteConvenios(fechaIni, fechaFin));
			return generarReporte(fechaIni, fechaFin, reporte, true);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}		
		
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteConvenioBean> reporte, boolean estudio) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeader(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		int i = 0;
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Convenios"));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
		i++;

		HSSFRow rowTitulo2 = sheet.createRow(i);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
		i++;

		i = crearHeaderPrincipal(wb, sheet, i, styleHeader, estudio);
		BigDecimal totalCapital = BigDecimal.ZERO;
		BigDecimal totalInteres = BigDecimal.ZERO;
		BigDecimal totalAjusteCapital = BigDecimal.ZERO;
		BigDecimal totalAjusteInteres = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		for (int j = 0; j < reporte.size(); j++) {
			// for (ReporteConvenioBean repo : reporte) {
			ReporteConvenioBean repo = reporte.get(j);
			boolean mostrarDatosConvenio = true;
			if (j > 0
					&& reporte.get(j - 1).getNumero().equals(repo.getNumero())) {
				mostrarDatosConvenio = false;
			}
			i = generarDatos(repo, i, styleAll, styleDate, styleMoney, sheet,
					mostrarDatosConvenio, estudio);
			if (mostrarDatosConvenio) {
				totalCapital = totalCapital.add(repo.getCapital());
				totalInteres = totalInteres.add(repo.getInteres());
				totalAjusteCapital = totalAjusteCapital.add(repo
						.getAjusteCapital());
				totalAjusteInteres = totalAjusteInteres.add(repo
						.getAjusteInteres());
				total = total.add(repo.getTotal());
			}
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cell7 = row.createCell(5);
		cell7.setCellValue(totalCapital.doubleValue());
		cell7.setCellStyle(styleMoneyBold);

		HSSFCell cell8 = row.createCell(6);
		cell8.setCellValue(totalInteres.doubleValue());
		cell8.setCellStyle(styleMoneyBold);

		HSSFCell cell9 = row.createCell(7);
		cell9.setCellValue(totalAjusteCapital.doubleValue());
		cell9.setCellStyle(styleMoneyBold);

		HSSFCell cell10 = row.createCell(8);
		cell10.setCellValue(totalAjusteInteres.doubleValue());
		cell10.setCellStyle(styleMoneyBold);

		HSSFCell cell12 = row.createCell(9);
		cell12.setCellValue(total.doubleValue());
		cell12.setCellStyle(styleMoneyBold);

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

		return wb;
	}

	public static int generarDatos(ReporteConvenioBean repo, int i,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFSheet sheet,
			boolean mostrarDatosConvenio, boolean estudio) {
		HSSFRow row = sheet.createRow(i);

		int j=0;
		if(estudio){
			HSSFCell cell00 = row.createCell(j++);
			cell00.setCellValue(new HSSFRichTextString(repo.getEntidad()));
			cell00.setCellStyle(styleAll);			
		}
		HSSFCell cell0 = row.createCell(j++);
		cell0.setCellValue(new HSSFRichTextString(repo.getNumero()));
		cell0.setCellStyle(styleAll);

		if (mostrarDatosConvenio) {
			try{
			HSSFCell cell1 = row.createCell(j++);
			cell1.setCellValue(repo.getFecha());
			cell1.setCellStyle(styleDate);

			HSSFCell cell4 = row.createCell(j++);
			cell4.setCellValue(new HSSFRichTextString(repo.getCuit()));
			cell4.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(j++);
			cell5.setCellValue(new HSSFRichTextString(repo.getSucursal()));
			cell5.setCellStyle(styleAll);

			HSSFCell cell6 = row.createCell(j++);
			cell6.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
			cell6.setCellStyle(styleAll);

			HSSFCell cell7 = row.createCell(j++);
			cell7.setCellValue(repo.getCapital().doubleValue());
			cell7.setCellStyle(styleMoney);

			HSSFCell cell8 = row.createCell(j++);
			cell8.setCellValue(repo.getInteres().doubleValue());
			cell8.setCellStyle(styleMoney);

			HSSFCell cell9 = row.createCell(j++);
			cell9.setCellValue(repo.getAjusteCapital().doubleValue());
			cell9.setCellStyle(styleMoney);

			HSSFCell cell10 = row.createCell(j++);
			cell10.setCellValue(repo.getAjusteInteres().doubleValue());
			cell10.setCellStyle(styleMoney);

			HSSFCell cell12 = row.createCell(j++);
			cell12.setCellValue(repo.getTotal().doubleValue());
			cell12.setCellStyle(styleMoney);
			}catch(Exception e){				
				_log.error("ERROR CONVENIO "+repo.getNumero(),e);
			}
		}
		HSSFCell cell11 = row.createCell(j++);
		cell11.setCellValue(new HSSFRichTextString(repo.getNumeroActaAsociada()));
		cell11.setCellStyle(styleAll);
		return ++i;
	}

	public static int crearHeaderPrincipal(HSSFWorkbook wb, HSSFSheet sheet,
			int i, HSSFCellStyle styleHeader, boolean estudio) {
		HSSFRow row = sheet.createRow(i);
		
		int j=0;
		
		if(estudio){
			HSSFCell cell00 = row.createCell(j++);
			cell00.setCellValue(new HSSFRichTextString("Entidad"));
			cell00.setCellStyle(styleHeader);
		}

		HSSFCell cell0 = row.createCell(j++);
		cell0.setCellValue(new HSSFRichTextString("Numero"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(j++);
		cell1.setCellValue(new HSSFRichTextString("Fecha"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(j++);
		cell4.setCellValue(new HSSFRichTextString("Cuit"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(j++);
		cell5.setCellValue(new HSSFRichTextString("Sucursal"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(j++);
		cell6.setCellValue(new HSSFRichTextString("Razon Social"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(j++);
		cell7.setCellValue(new HSSFRichTextString("Capital"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(j++);
		cell8.setCellValue(new HSSFRichTextString("Interes"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(j++);
		cell9.setCellValue(new HSSFRichTextString("Ajuste Capital"));
		cell9.setCellStyle(styleHeader);

		HSSFCell cell10 = row.createCell(j++);
		cell10.setCellValue(new HSSFRichTextString("Ajuste Interes"));
		cell10.setCellStyle(styleHeader);

		HSSFCell cell12 = row.createCell(j++);
		cell12.setCellValue(new HSSFRichTextString("Total"));
		cell12.setCellStyle(styleHeader);

		HSSFCell cell13 = row.createCell(j++);
		cell13.setCellValue(new HSSFRichTextString("Acta asociada"));
		cell13.setCellStyle(styleHeader);

		return ++i;
	}

}
