package ar.com.ospim.estudioisidro.reportes;

import java.text.ParseException;
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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.estudioisidro.beans.ReporteSeguimientoEmpresa;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.recibos.service.ReciboNoOSServiceUtil;


public class ReporteSeguimientoEmpresasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSeguimientoEmpresasExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);
		Integer nroLote = ParamUtil.getInteger(req, "nro_lote",0);
		String tipoLote = ParamUtil.getString(req, "tipo_lote");

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			Empresa empresa = new Empresa(cuit, sucu, "");
			empresa.setId_seccional(seccional);

			List<ReporteSeguimientoEmpresa> recibos = LlamadoServiceUtil
					.getReporteSeguimientoEmpresa(fechaIni, fechaFin, empresa,nroLote,tipoLote);

			return generarReporte(fechaIni, fechaFin, recibos);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	
	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteSeguimientoEmpresa> reporte) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleFechaLeft = getStyleDateWithBorder(wb);		

		HSSFCellStyle styleTop = getStyleAllWithBorder(wb);
		

		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

		HSSFCellStyle styleHeader = getStyleWhiteHeaderWithBorder(wb);

		HSSFCellStyle styleHeaderLeft = getStyleHeaderWithBorder(wb);
		

		HSSFCellStyle styleHeaderRight = getStyleAllWithBorder(wb);

		HSSFCellStyle styleFechaLeftTop = getStyleDateWithBorder(wb);
		
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte Seguimiento Empresas - Desde:"
						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " - Hasta:"
						+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		
		int i = 1;
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb);
		
		String cuit = "";
		for (ReporteSeguimientoEmpresa repo : reporte) {
			boolean nuevo = false;
			if (!repo.getCuit().equals(cuit)) {
				nuevo = true;
				cuit = repo.getCuit();		
			}
			HSSFRow row = sheet.createRow(i);
			i++;
			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue(new HSSFRichTextString(repo.getCuit()));
			if (nuevo) {
				cell0.setCellStyle(styleFechaLeftTop);
			} else {
				cell0.setCellStyle(styleFechaLeft);
			}

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
			if (nuevo) {
				cell1.setCellStyle(styleTop);
			} else {
				cell1.setCellStyle(styleAll);
			}

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(new HSSFRichTextString(repo.getEstado()));
			if (nuevo) {
				cell2.setCellStyle(styleTop);
			} else {
				cell2.setCellStyle(styleAll);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
			String fecha_aux = sdf.format(repo.getFechaEstado());

			HSSFCell cell3 = row.createCell(3);

			try {
				cell3.setCellValue(sdf.parse(fecha_aux));
			} catch (ParseException e) {
				cell3.setCellValue(new HSSFRichTextString(""));
			}

			if (nuevo) {
				cell3.setCellStyle(styleFechaLeftTop);
			} else {
				cell3.setCellStyle(styleFechaLeft);
			}

			HSSFCell cell31 = row.createCell(4);
			cell31.setCellValue(new HSSFRichTextString(repo.isMolinera() ? "Si"
					: "No"));
			if (nuevo) {
				cell31.setCellStyle(styleTop);
			} else {
				cell31.setCellStyle(styleAll);
			}

			HSSFCell cell32 = row.createCell(5);
			cell32.setCellValue(new HSSFRichTextString(repo.getCartaDoc()));
			if (nuevo) {
				cell32.setCellStyle(styleTop);
			} else {
				cell32.setCellStyle(styleAll);
			}

			HSSFCell cell33 = row.createCell(6);
			cell33.setCellValue(new HSSFRichTextString(repo
					.getUbicacionCarpeta()));
			if (nuevo) {
				cell33.setCellStyle(styleTop);
			} else {
				cell33.setCellStyle(styleAll);
			}
			
			
			fecha_aux = sdf.format(repo.getFechaLlamado());
			String hora_aux = sdf2.format(repo.getFechaLlamado());
			
			HSSFCell cell4 = row.createCell(7);
			try{
				cell4.setCellValue(sdf.parse(fecha_aux));
			}catch(ParseException e){
				cell4.setCellValue(new HSSFRichTextString(""));
			}
			if (nuevo) {
				cell4.setCellStyle(styleFechaLeftTop);
			} else {
				cell4.setCellStyle(styleFechaLeft);
			}
			
			HSSFCell cell41 = row.createCell(8);
			
			cell41.setCellValue(new HSSFRichTextString(hora_aux));
			
			if (nuevo) {
				cell41.setCellStyle(styleFechaLeftTop);
			} else {
				cell41.setCellStyle(styleFechaLeft);
			}
	
			HSSFCell cell5 = row.createCell(9);
			cell5.setCellValue(new HSSFRichTextString(repo.getObservaciones()));
			if (nuevo) {
				cell5.setCellStyle(styleTop);
			} else {
				cell5.setCellStyle(styleAll);
			}

			HSSFCell cell6 = row.createCell(10);
			cell6.setCellValue(new HSSFRichTextString(repo.getUser()));
			if (nuevo) {
				cell6.setCellStyle(styleTop);
			} else {
				cell6.setCellStyle(styleAll);
			}
			
			HSSFCell cell11 = row.createCell(11);
			cell11.setCellValue(repo.getLoteNro());
			if (nuevo) {
				cell11.setCellStyle(styleTop);
			} else {
				cell11.setCellStyle(styleAll);
			}
			
			
			HSSFCell cell12 = row.createCell(12);
			String tipoLote="";
			if("PL".equalsIgnoreCase(repo.getLoteTipo())){
				tipoLote="PRELEGAL";
			}
			if("MT".equalsIgnoreCase(repo.getLoteTipo())){
				tipoLote="MORA TEMPRANA";
			}	
				
			cell12.setCellValue(new HSSFRichTextString(tipoLote));
			if (nuevo) {
				cell12.setCellStyle(styleTop);
			} else {
				cell12.setCellStyle(styleAll);
			}
			
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

		return wb;
	}

	

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("CUIT"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell0 = row.createCell(1);
		cell0.setCellValue(new HSSFRichTextString("Razón Social"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("Estado"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Fecha Estado"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(4);
		cell3.setCellValue(new HSSFRichTextString("Molinera"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(5);
		cell4.setCellValue(new HSSFRichTextString("Carta Doc."));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(6);
		cell5.setCellValue(new HSSFRichTextString("Ubicación Carpeta"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(7);
		cell6.setCellValue(new HSSFRichTextString("Fecha Llamado"));
		cell6.setCellStyle(styleHeader);
		
		HSSFCell cell61 = row.createCell(8);
		cell61.setCellValue(new HSSFRichTextString("Hora Llamado"));
		cell61.setCellStyle(styleHeader);
		
		HSSFCell cell7 = row.createCell(9);
		cell7.setCellValue(new HSSFRichTextString("Observaciones"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(10);
		cell8.setCellValue(new HSSFRichTextString("Usuario"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell11 = row.createCell(11);
		cell11.setCellValue(new HSSFRichTextString("Lote"));
		cell11.setCellStyle(styleHeader);
		
		HSSFCell cell12 = row.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Tipo Lote"));
		cell12.setCellStyle(styleHeader);
		
		//wb.setRepeatingRowsAndColumns(0, 0, 13, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return ++i;
	}
	
	public static HSSFWorkbook generaReporteRecibos(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		try {
			String empresa = null;
			String cuit = null;
			String actaNroStr = null;
			String entidad = null;
			String sacarRecibos = null;

			Date fechaDesde = DateUtils.getFechaDesde(req);
			Date fechaHasta = DateUtils.getFechaHasta(req);

			if (req.getParameter("recibo") != null) {
				actaNroStr = req.getParameter("recibo").trim().length() > 0 ? req
						.getParameter("recibo") : null;
			}

			if (null != req.getParameter("empresa")) {
				empresa = req.getParameter("empresa").trim().length() > 0 ? req
						.getParameter("empresa") : null;
			}

			if (null != req.getParameter("cuit")) {
				cuit = req.getParameter("cuit").trim().length() > 0 ? req
						.getParameter("cuit") : null;
			}

			if (null != req.getParameter("entidad_bla")) {
				entidad = req.getParameter("entidad_bla").trim().length() > 0 ? req
						.getParameter("entidad_bla") : null;
			}
			if (null != req.getParameter("sacarRecibos")) {
				sacarRecibos = req.getParameter("sacarRecibos").trim().length() > 0 ? req
						.getParameter("sacarRecibos") : null;
			}

			List<Recibo> recibos = ReciboNoOSServiceUtil.get(actaNroStr, cuit,
					empresa, entidad, sacarRecibos, fechaDesde, fechaHasta);

			return generarReporteRecibos(fechaDesde, fechaHasta, recibos);
		} catch (Exception e) {
			_log.error("Error al generar reporte recibo", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporteRecibos(Date fechaIni,
			Date fechaFin, List<Recibo> reporte) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
		styleFechaLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleTop = getStyleAll(wb);
		styleTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoney = getStyleMoney(wb);

		HSSFCellStyle styleHeader = getStyleHeader(wb);

		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleAll(wb);
		styleHeaderRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		styleMoneyRightTop.setBorderTop(BorderStyle.THIN);
		styleMoneyRightTop.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
		styleFechaLeftTop.setBorderLeft(BorderStyle.THIN);
		styleFechaLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyTop = getStyleMoney(wb);
		styleMoneyTop.setBorderTop(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte Recibos Estudio - Desde:"
						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " - Hasta:"
						+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		int i = 1;
		i = generarHeaderRecibo(sheet, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb);		
		String cuit = "";
		for (Recibo repo : reporte) {
			boolean nuevo = false;
			if (!repo.getEmpresa().getCuit().equals(cuit)) {
				nuevo = true;
				cuit = repo.getEmpresa().getCuit();				
			}
			HSSFRow row = sheet.createRow(i);
			i++;
			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue(repo.getId());
			if (nuevo) {
				cell0.setCellStyle(styleTop);
			} else {
				cell0.setCellStyle(styleAll);
			}

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(repo.getNumero()));
			if (nuevo) {
				cell1.setCellStyle(styleTop);
			} else {
				cell1.setCellStyle(styleAll);
			}

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(new HSSFRichTextString(repo.getEmpresa()
					.getCuit()));
			if (nuevo) {
				cell2.setCellStyle(styleTop);
			} else {
				cell2.setCellStyle(styleAll);
			}

			HSSFCell cell22 = row.createCell(3);
			cell22.setCellValue(new HSSFRichTextString(repo.getEmpresa()
					.getRazon_soc()));
			if (nuevo) {
				cell22.setCellStyle(styleTop);
			} else {
				cell22.setCellStyle(styleAll);
			}

			HSSFCell cell3 = row.createCell(4);
			cell3.setCellValue(repo.getFecha());
			if (nuevo) {
				cell3.setCellStyle(styleFechaLeftTop);
			} else {
				cell3.setCellStyle(styleFechaLeft);
			}

			HSSFCell cell6 = row.createCell(5);
			cell6.setCellValue(repo.getImporte().doubleValue());
			if (nuevo) {
				cell6.setCellStyle(styleMoneyTop);
			} else {
				cell6.setCellStyle(styleMoney);
			}
		}

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);

		return wb;
	}
	
	private static int generarHeaderRecibo(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("ID Recibo"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell0 = row.createCell(1);
		cell0.setCellValue(new HSSFRichTextString("Número Recibo"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeaderL);

		HSSFCell cellAcreed2 = row.createCell(3);
		cellAcreed2.setCellValue(new HSSFRichTextString("Razón Social"));
		cellAcreed2.setCellStyle(styleHeaderL);

		HSSFCell cellRaz = row.createCell(4);
		cellRaz.setCellValue(new HSSFRichTextString("Fecha"));
		cellRaz.setCellStyle(styleHeaderL);

		HSSFCell cell3 = row.createCell(5);
		cell3.setCellValue(new HSSFRichTextString("Importe"));
		cell3.setCellStyle(styleHeaderL);

		//wb.setRepeatingRowsAndColumns(0, 0, 6, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		return ++i;
	}

}
