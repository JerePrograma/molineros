package ar.com.ospim.tesoreria.reportes;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.global.services.CalculaCapitalCuotaServiceUtil;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteOPReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.TotalActaNoOS;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa.Detalle;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;

public class ReporteAportesNoOSExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteOPReintegros.class);

	public static HSSFWorkbook generaReporteGralFromActaNoOS(
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();

		int acta_id = ParamUtil.getInteger(req, "acta_id");

		Acta acta = ActaNoOSServiceUtil.getActa(acta_id, 0);

		HSSFSheet sheet = wb.createSheet("Reporte General");

		if (acta == null) {
			return wb;
		}
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int index = -1;

		index = getResumenGeneral(wb, sheet, index, acta);

		crearTotalesGral(wb, sheet, acta, index);

		pieLegal(wb, sheet, acta, index);

		for (int i = 1; i < 5; i++) {
			sheet.autoSizeColumn((short) i);
		}

		return wb;

	}

	public static HSSFWorkbook generaReporteNominaEmpresaFromActaNoOS(
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();

		int acta_id = ParamUtil.getInteger(req, "acta_id");

		Acta acta = ActaNoOSServiceUtil.getActa(acta_id,0);

		HSSFSheet sheet = wb.createSheet("Hoja 1");

		if (acta == null) {
			return wb;
		}
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int index = -1;
		int[] colRow = { 0, 0 };

		index = getEncabezadoNomina(wb, sheet, index, acta);
		index++;
		colRow = crearInfoParaActaNomina(wb, sheet, acta, index, req);

		crearTotalesNomina(wb, sheet, acta, colRow[1], colRow[0]);

		pieLegal(wb, sheet, acta, colRow[1]);

		for (int i = 1; i < colRow[0] + 1; i++) {
			sheet.autoSizeColumn((short) i);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 13, 7, 7);
		
		return wb;

	}
	public static HSSFWorkbook generaReportePeriodoEmpresaFromActaNoOSWorkBook(
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		generaReportePeriodoEmpresaFromActaNoOS(req,res,wb);
		return wb;
	}

	public static HSSFSheet generaReportePeriodoEmpresaFromActaNoOS(
			HttpServletRequest req, HttpServletResponse res, HSSFWorkbook wb) throws Exception {

		

		int acta_id = ParamUtil.getInteger(req, "acta_id");

		Acta acta = ActaNoOSServiceUtil.getActa(acta_id,0);

		HSSFSheet sheet = wb.createSheet("Reporte Nomina");

		if (acta == null) {
			return sheet;			
		}
		
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);		
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int index = -1;
		int[] colRow = { 0, 0 };
		if (acta.getEntidad().contains("A.M.T.I.M.A")) {
			index = getEncabezadoAmtima(wb, sheet, index, acta);
			index++;
			colRow = crearInfoParaActaAMTIMA(wb, sheet, acta, index);
			colRow[1]++;
		} else {
			index = getEncabezado(wb, sheet, index, acta);						
			if(acta.getCapitalArt46().add(acta.getInteresArt46()).compareTo(BigDecimal.ZERO)>0){
				index = getEncabezadoArt46(wb, sheet, index, acta);
				index++;				
				index = crearInfoArt46(wb, sheet, acta, index, req);
			}
			if(acta.getCapitalUsufructo().add(acta.getInteresUsufructo()).compareTo(BigDecimal.ZERO)>0){
				index = getEncabezadoUsufructo(wb, sheet, index, acta);
				index++;
				index = crearInfoUsufructo(wb, sheet, acta, index, req);
			}
			if(acta.getCapitalSindicato().add(acta.getInteresSindicato()).compareTo(BigDecimal.ZERO)>0){						
				index = getEncabezadoSocial(wb, sheet, index, acta);
				index++;
				index = crearInfoSocial(wb, sheet, acta, index, req);			
			}
			if(acta.getCapitalSolidario().add(acta.getInteresSolidario()).compareTo(BigDecimal.ZERO)>0){
				index = getEncabezadoSolidario(wb, sheet, index, acta);
				index++;
				index = crearInfoSolidario(wb, sheet, acta, index, req);
			}
			index++;			
		}
		
		if (acta.getEntidad().contains("A.M.T.I.M.A")) {
			crearTotales(wb, sheet, acta, colRow[1]++);
		}else{
			crearTotales(wb, sheet, acta, index);
		}

		//crearTotales(wb, sheet, acta, colRow[1], colRow[0]);
		if (acta.getEntidad().contains("A.M.T.I.M.A")) {
			pieLegal(wb, sheet, acta, colRow[1]);
		}else{
			pieLegal(wb, sheet, acta, index++);
		}

		for (int i = 1; i < colRow[0] + 1; i++) {
			// sheet.autoSizeColumn((short) i);
			sheet.setColumnWidth(i, 3000);
		}
		
		
		//----		
		if(acta.getInspectoresFirmantes()!=null && acta.getInspectoresFirmantes().size()>0) {
					Integer col=0;
					index=index+7;
					int x=1;
					for(Inspector i:acta.getInspectoresFirmantes()) {
						FileInputStream is;
						
						try {
							is = new FileInputStream(req.getPathTranslated() + "html/images/Firma_Inspector_"+ i.getId() +".jpg");
							byte[] bytes = IOUtils.toByteArray(is);
							int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
							is.close();
							HSSFCreationHelper helper = wb.getCreationHelper();
							Drawing drawing = sheet.createDrawingPatriarch();
							HSSFClientAnchor anchor = helper.createClientAnchor();
							anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
							anchor.setCol1(col);
							anchor.setCol2(col);
							anchor.setRow1(index);
							anchor.setRow2(index);
							anchor.setDx1(0);
							anchor.setDy1(0);
							
							col=col+5;
							
							if(acta.getInspectoresFirmantes().size()>2) {
								if(x % 2==0) {
									index=index +20;
									col=0;
								}
							}
							
							Picture pict = drawing.createPicture(anchor, pictureIdx);
							pict.resize();
							x++;
						} catch (Exception e) {
							_log.debug(e.getMessage());
						}
					}
		}
		//----
		
		
		return sheet;
	}

	private static void pieLegal(HSSFWorkbook wb, HSSFSheet sheet, Acta acta,
			int indexRow) {
		HSSFCellStyle styleBoldNoBorder = getStyleBold(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		styleBold.setVerticalAlignment(VerticalAlignment.TOP);
		styleBold.setAlignment(HorizontalAlignment.CENTER);
		styleBold.setWrapText(true);

		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		indexRow = indexRow + 3;
		int index = 0;
		// TITULO
		HSSFRow rowTitulo = sheet.createRow(indexRow++);
		HSSFCell cell0 = rowTitulo.createCell(index);
		cell0.setCellValue(new HSSFRichTextString(
				"INTERESES RESARCITORIOS Y PUNITORIOS - TASAS DECRETO 507/93"));
		cell0.setCellStyle(styleBoldNoBorder);

		// ENCABEZADO
		HSSFRow row = sheet.createRow(indexRow++);
		row.setHeight((short) 1000);
		HSSFCell cell41 = row.createCell(index++);
		cell41.setCellValue(new HSSFRichTextString("Período"));
		cell41.setCellStyle(styleBold);

		HSSFCell cell51 = row.createCell(index++);
		cell51.setCellValue(new HSSFRichTextString("Norma"));
		cell51.setCellStyle(styleBold);

		HSSFCell cell61 = row.createCell(index++);
		cell61.setCellValue(new HSSFRichTextString(
				"Resarcitorios s/deuda sin actualiz. % mensual"));
		cell61.setCellStyle(styleBold);

		HSSFCell cell71 = row.createCell(index++);
		cell71.setCellValue(new HSSFRichTextString(
				"Punitorios s/deuda sin actualiz. % mensual"));
		cell71.setCellStyle(styleBold);

		// Detalle
		index = 0;
		HSSFRow row2 = sheet.createRow(indexRow++);

		HSSFCell cell = row2.createCell(index++);
		cell.setCellValue(new HSSFRichTextString("Desde el 01/01/2011"));
		cell.setCellStyle(styleAll);

		HSSFCell cell2 = row2.createCell(index++);
		cell2.setCellValue(new HSSFRichTextString("RESOL AFIP 841/2010"));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = row2.createCell(index++);
		cell3.setCellValue(3.00);
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = row2.createCell(index++);
		cell4.setCellValue(4.00);
		cell4.setCellStyle(styleAll);
		// Detalle2
		index = 0;
		HSSFRow row3 = sheet.createRow(indexRow++);

		HSSFCell cell11 = row3.createCell(index++);
		cell11.setCellValue(new HSSFRichTextString("01/07/2006 - 31/12/2010"));
		cell11.setCellStyle(styleAll);

		HSSFCell cell21 = row3.createCell(index++);
		cell21.setCellValue(new HSSFRichTextString("Res. 492/2006"));
		cell21.setCellStyle(styleAll);

		HSSFCell cell31 = row3.createCell(index++);
		cell31.setCellValue(2.00);
		cell31.setCellStyle(styleAll);

		HSSFCell cell411 = row3.createCell(index++);
		cell411.setCellValue(3.00);
		cell411.setCellStyle(styleAll);
		
	}

	// Col,Row
	private static void crearTotales(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int indexRow) {
		int index=0;
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

		HSSFRow row = sheet.createRow(indexRow++);

		HSSFCell cell41 = row.createCell(index++);
		cell41.setCellValue(new HSSFRichTextString("DEUDA TOTAL"));
		cell41.setCellStyle(styleBold);

		HSSFCell cell = row.createCell(index);
		cell.setCellValue(acta.getCapital().add(acta.getInteres()).doubleValue());
		cell.setCellStyle(styleAll);

	}

	private static void crearTotalesGral(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int indexRow) {
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		styleBold.setAlignment(HorizontalAlignment.RIGHT);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

		HSSFRow row = sheet.createRow(indexRow++);

		HSSFCell cell41 = row.createCell(0);
		cell41.setCellValue(new HSSFRichTextString("DEUDA TOTAL"));
		cell41.setCellStyle(styleBold);

		HSSFCell cell = row.createCell(1);
		cell.setCellValue(acta.getTotal().doubleValue());
		cell.setCellStyle(styleAll);

	}

	private static void crearTotalesNomina(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int indexRow, int indexCol) {
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

		HSSFRow row = sheet.createRow(indexRow++);

		HSSFCell cell41 = row.createCell(indexCol - 3);
		cell41.setCellValue(new HSSFRichTextString("TOTAL"));
		cell41.setCellStyle(styleBold);

		HSSFCell cell = row.createCell(indexCol - 2);
		cell.setCellValue(acta.getInteres().doubleValue());
		cell.setCellStyle(styleAll);

		HSSFCell cell412 = row.createCell(indexCol - 1);
		cell412.setCellValue(acta.getCapital().doubleValue());
		cell412.setCellStyle(styleAll);

	}

	// Col,Row
	private static int crearInfoArt46(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int index, HttpServletRequest request) {

		int rowCol = 0;

		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);

		HashMap<Date, TotalActaNoOS> peris = acta.generarTotalesAgrupados();

		ArrayList<Date> periodos = new ArrayList<Date>();
		periodos.addAll(peris.keySet());
		Collections.sort(periodos);		
		HSSFRow row=null;
		// ART 46
		for (Date periodoL : periodos) {
			rowCol = 0;
			TotalActaNoOS totalPeriodo = peris.get(periodoL);
			row = sheet.createRow(index++);
			HSSFCell cell = row.createCell(rowCol++);
			cell.setCellValue(totalPeriodo.getPeriodo());
			cell.setCellStyle(styleDate);			
			BigDecimal interesAPago46 = BigDecimal.ZERO;			
			Date fechaPago46 = null;
			

			List<ActaPeriodoDeudaEmpresa> detalles = acta.getPeriodos(periodoL);

			for (ActaPeriodoDeudaEmpresa detalle : detalles) {
				if (detalle.getDetalle() != null) {
					Detalle d = detalle.getDetalle().get(0);
					if (d.getTipoAporte() == CalculaCapitalCuotaServiceUtil.ART_46) {
						interesAPago46 = interesAPago46.add(d
								.getInteresAFechaPagada());
						fechaPago46 = d.getFechaPagado();
					}
				}
			}

			Date vtoSocialUsu46 = new Date();
			
			
			Calendar periodoCalendar = Calendar.getInstance();
			periodoCalendar.setTime(periodoL);
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);

			vtoSocialUsu46 = AfipServiceUtil.getVencimientoOriginalAFIP(acta
					.getEmpresa().getCuit(), periodoL, request);

			HSSFCell cell1 = row.createCell(rowCol++);
			cell1.setCellValue(vtoSocialUsu46);
			cell1.setCellStyle(styleDate);

			HSSFCell cell11 = row.createCell(rowCol++);
			cell11.setCellValue(totalPeriodo.getCalculadoArt46().doubleValue());
			cell11.setCellStyle(styleAll);

			HSSFCell cell412 = row.createCell(rowCol++);
			if (null != fechaPago46) {
				cell412.setCellValue(fechaPago46);
			}
			cell412.setCellStyle(styleDate);

			HSSFCell cell41 = row.createCell(rowCol++);
			cell41.setCellValue(totalPeriodo.getPagadoArt46().doubleValue());
			cell41.setCellStyle(styleAll);

			HSSFCell cell411 = row.createCell(rowCol++);
			cell411.setCellValue(interesAPago46 != null ? interesAPago46
					.doubleValue() : 0);
			cell411.setCellStyle(styleAll);

			HSSFCell cell3 = row.createCell(rowCol++);
			cell3.setCellValue(totalPeriodo.getCapitalArt46().doubleValue());
			cell3.setCellStyle(styleAll);

			HSSFCell cell4 = row.createCell(rowCol++);
			cell4.setCellValue(totalPeriodo.getInteresArt46().doubleValue());
			cell4.setCellStyle(styleAll);

			HSSFCell cell40 = row.createCell(rowCol++);
			cell40.setCellValue(totalPeriodo.getCapitalArt46()
					.add(totalPeriodo.getInteresArt46()).doubleValue());
			cell40.setCellStyle(styleAll);
		}
		row = sheet.createRow(index++);
		HSSFCell cellTituloTotal = row.createCell(rowCol-4);
		cellTituloTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTituloTotal.setCellStyle(styleBold);
		HSSFCell cellTotalCapital = row.createCell(rowCol-3);
		cellTotalCapital.setCellValue(acta.getCapitalArt46().doubleValue());
		cellTotalCapital.setCellStyle(styleAll);
		HSSFCell cellTotalInteres = row.createCell(rowCol-2);
		cellTotalInteres.setCellValue(acta.getInteresArt46().doubleValue());
		cellTotalInteres.setCellStyle(styleAll);
		HSSFCell cellTotalTotal= row.createCell(rowCol-1);
		cellTotalTotal.setCellValue(acta.getCapitalArt46().add(acta.getInteresArt46()).doubleValue());		
		cellTotalTotal.setCellStyle(styleAll);		

		return index;
	}

	// Col,Row
	private static int crearInfoUsufructo(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int index, HttpServletRequest request) {
		int rowCol = 0;

		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);

		HashMap<Date, TotalActaNoOS> peris = acta.generarTotalesAgrupados();

		ArrayList<Date> periodos = new ArrayList<Date>();
		periodos.addAll(peris.keySet());
		Collections.sort(periodos);
		HSSFRow row = null;
		// USUFRUCTO
		for (Date periodoL : periodos) {
			rowCol = 0;
			TotalActaNoOS totalPeriodo = peris.get(periodoL);
			row = sheet.createRow(index++);
			HSSFCell cell = row.createCell(rowCol++);
			cell.setCellValue(totalPeriodo.getPeriodo());
			cell.setCellStyle(styleDate);
		
			BigDecimal interesAPagoUsufructo = BigDecimal.ZERO;
			
			Date fechaPagoUsufructo = null;

			List<ActaPeriodoDeudaEmpresa> detalles = acta.getPeriodos(periodoL);

			for (ActaPeriodoDeudaEmpresa detalle : detalles) {
				if (detalle.getDetalle() != null) {
					Detalle d = detalle.getDetalle().get(0);
					if (d.getTipoAporte() == CalculaCapitalCuotaServiceUtil.USUFRUCTO) {
						interesAPagoUsufructo = interesAPagoUsufructo.add(d
								.getInteresAFechaPagada());
						fechaPagoUsufructo = d.getFechaPagado();
					}
				}
			}

			Date vtoSocialUsu46 = new Date();			
			
			Calendar periodoCalendar = Calendar.getInstance();
			periodoCalendar.setTime(periodoL);
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			
			vtoSocialUsu46 = AfipServiceUtil.getVencimientoOriginalAFIP(acta
					.getEmpresa().getCuit(), periodoL, request);

			HSSFCell cell42 = row.createCell(rowCol++);
			cell42.setCellValue(vtoSocialUsu46);
			cell42.setCellStyle(styleDate);

			HSSFCell cell421 = row.createCell(rowCol++);
			cell421.setCellValue(totalPeriodo.getCalculadoUsufructo()
					.doubleValue());
			cell421.setCellStyle(styleAll);

			HSSFCell cell612 = row.createCell(rowCol++);
			if (null != fechaPagoUsufructo) {
				cell612.setCellValue(fechaPagoUsufructo);
			}
			cell612.setCellStyle(styleDate);

			HSSFCell cell61 = row.createCell(rowCol++);
			cell61.setCellValue(totalPeriodo.getPagadoUsufructo().doubleValue());
			cell61.setCellStyle(styleAll);

			HSSFCell cell611 = row.createCell(rowCol++);
			cell611.setCellValue(interesAPagoUsufructo != null ? interesAPagoUsufructo
					.doubleValue() : 0);
			cell611.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(rowCol++);
			cell5.setCellValue(totalPeriodo.getCapitalUsufructo().doubleValue());
			cell5.setCellStyle(styleAll);

			HSSFCell cell6 = row.createCell(rowCol++);
			cell6.setCellValue(totalPeriodo.getInteresUsufructo().doubleValue());
			cell6.setCellStyle(styleAll);

			HSSFCell cell60 = row.createCell(rowCol++);
			cell60.setCellValue(totalPeriodo.getCapitalUsufructo()
					.add(totalPeriodo.getInteresUsufructo()).doubleValue());
			cell60.setCellStyle(styleAll);
		}
		
		row = sheet.createRow(index++);
		
		HSSFCell cellTituloTotal = row.createCell(rowCol-4);
		cellTituloTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTituloTotal.setCellStyle(styleBold);
		HSSFCell cellTotalCapital = row.createCell(rowCol-3);
		cellTotalCapital.setCellValue(acta.getCapitalUsufructo().doubleValue());
		cellTotalCapital.setCellStyle(styleAll);
		HSSFCell cellTotalInteres = row.createCell(rowCol-2);
		cellTotalInteres.setCellValue(acta.getInteresUsufructo().doubleValue());
		cellTotalInteres.setCellStyle(styleAll);
		HSSFCell cellTotalTotal= row.createCell(rowCol-1);
		cellTotalTotal.setCellValue(acta.getCapitalUsufructo().add(acta.getInteresUsufructo()).doubleValue());		
		cellTotalTotal.setCellStyle(styleAll);
		
		
		return index;
	}

	private static int crearInfoSocial(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int index, HttpServletRequest request) {
		int rowCol = 0;

		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);

		HashMap<Date, TotalActaNoOS> peris = acta.generarTotalesAgrupados();

		ArrayList<Date> periodos = new ArrayList<Date>();
		periodos.addAll(peris.keySet());
		Collections.sort(periodos);
		HSSFRow row =null;
		// SINDICATO
		for (Date periodoL : periodos) {
			rowCol = 0;
			TotalActaNoOS totalPeriodo = peris.get(periodoL);
			row = sheet.createRow(index++);
			HSSFCell cell = row.createCell(rowCol++);
			cell.setCellValue(totalPeriodo.getPeriodo());
			cell.setCellStyle(styleDate);

			BigDecimal interesAPagoSocial = BigDecimal.ZERO;		
			Date fechaPagoSocial = null;
		
			List<ActaPeriodoDeudaEmpresa> detalles = acta.getPeriodos(periodoL);

			for (ActaPeriodoDeudaEmpresa detalle : detalles) {
				if (detalle.getDetalle() != null) {
					Detalle d = detalle.getDetalle().get(0);
					if (d.getTipoAporte() == CalculaCapitalCuotaServiceUtil.SOCIAL) {
						interesAPagoSocial = interesAPagoSocial.add(d
								.getInteresAFechaPagada());
						fechaPagoSocial = d.getFechaPagado();
					}
				}
			}
			
			Date vtoSocial= new Date();
			
			
			Calendar periodoCalendar = Calendar.getInstance();
			periodoCalendar.setTime(periodoL);
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);

			vtoSocial= AfipServiceUtil.getVencimientoOriginalAFIP(acta
					.getEmpresa().getCuit(), periodoL, request);

			HSSFCell cell62 = row.createCell(rowCol++);
			//SACAR ESTO VER::::::
			cell62.setCellValue(vtoSocial);
			cell62.setCellStyle(styleDate);
			
			

			HSSFCell cell621 = row.createCell(rowCol++);
			cell621.setCellValue(totalPeriodo.getCalculadoSindicato()
					.doubleValue());
			cell621.setCellStyle(styleAll);

			HSSFCell cell812 = row.createCell(rowCol++);
			if (null != fechaPagoSocial) {
				cell812.setCellValue(fechaPagoSocial);
			}
			cell812.setCellStyle(styleDate);

			HSSFCell cell81 = row.createCell(rowCol++);
			cell81.setCellValue(totalPeriodo.getPagadoSindicato().doubleValue());
			cell81.setCellStyle(styleAll);

			HSSFCell cell811 = row.createCell(rowCol++);
			cell811.setCellValue(interesAPagoSocial != null ? interesAPagoSocial
					.doubleValue() : 0);
			cell811.setCellStyle(styleAll);

			HSSFCell cell7 = row.createCell(rowCol++);
			cell7.setCellValue(totalPeriodo.getCapitalSindicato().doubleValue());
			cell7.setCellStyle(styleAll);

			HSSFCell cell8 = row.createCell(rowCol++);
			cell8.setCellValue(totalPeriodo.getInteresSindicato().doubleValue());
			cell8.setCellStyle(styleAll);

			HSSFCell cell80 = row.createCell(rowCol++);
			cell80.setCellValue(totalPeriodo.getCapitalSindicato()
					.add(totalPeriodo.getInteresSindicato()).doubleValue());
			cell80.setCellStyle(styleAll);
		}
		
		row = sheet.createRow(index++);
		
		HSSFCell cellTituloTotal = row.createCell(rowCol-4);
		cellTituloTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTituloTotal.setCellStyle(styleBold);
		HSSFCell cellTotalCapital = row.createCell(rowCol-3);
		cellTotalCapital.setCellValue(acta.getCapitalSindicato().doubleValue());
		cellTotalCapital.setCellStyle(styleAll);
		HSSFCell cellTotalInteres = row.createCell(rowCol-2);
		cellTotalInteres.setCellValue(acta.getInteresSindicato().doubleValue());
		cellTotalInteres.setCellStyle(styleAll);
		HSSFCell cellTotalTotal= row.createCell(rowCol-1);
		cellTotalTotal.setCellValue(acta.getCapitalSindicato().add(acta.getInteresSindicato()).doubleValue());		
		cellTotalTotal.setCellStyle(styleAll);		
			
		
		return index;
	}

	private static int crearInfoSolidario(HSSFWorkbook wb, HSSFSheet sheet,
			Acta acta, int index, HttpServletRequest request) {
		int rowCol = 0;

		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);

		HashMap<Date, TotalActaNoOS> peris = acta.generarTotalesAgrupados();

		ArrayList<Date> periodos = new ArrayList<Date>();
		periodos.addAll(peris.keySet());
		Collections.sort(periodos);
		
		HSSFRow row =null;
		// SOLIDARIO
		for (Date periodoL : periodos) {
			rowCol = 0;
			TotalActaNoOS totalPeriodo = peris.get(periodoL);
			row = sheet.createRow(index++);
			HSSFCell cell = row.createCell(rowCol++);
			cell.setCellValue(totalPeriodo.getPeriodo());
			cell.setCellStyle(styleDate);
			
			BigDecimal interesAPagoSolidario = BigDecimal.ZERO;
			
			Date fechaPagoSolidario = null;
			
			List<ActaPeriodoDeudaEmpresa> detalles = acta.getPeriodos(periodoL);

			for (ActaPeriodoDeudaEmpresa detalle : detalles) {
				if (detalle.getDetalle() != null) {
					Detalle d = detalle.getDetalle().get(0);
					if (d.getTipoAporte() == CalculaCapitalCuotaServiceUtil.SOLIDARIO) {
						interesAPagoSolidario = interesAPagoSolidario.add(d
								.getInteresAFechaPagada());
						fechaPagoSolidario = d.getFechaPagado();
					} 
				}
			}
			
			Date vtoSoli= new Date();
			
			FeriadosServiceUtil feri = new FeriadosServiceUtil();
			Calendar periodoCalendar = Calendar.getInstance();
			periodoCalendar.setTime(periodoL);
			

			vtoSoli= AfipServiceUtil.getVencimientoOriginalAFIP(acta
					.getEmpresa().getCuit(), periodoL, request);
			
			periodoCalendar.setTime(vtoSoli);
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);

			periodoCalendar = feri.obtenerSiguienteDiaHabil(periodoCalendar);
		
			HSSFCell cell82 = row.createCell(rowCol++);
			cell82.setCellValue(periodoCalendar.getTime());
			cell82.setCellStyle(styleDate);

			HSSFCell cell821 = row.createCell(rowCol++);
			cell821.setCellValue(totalPeriodo.getCalculadoSolidario()
					.doubleValue());
			cell821.setCellStyle(styleAll);

			HSSFCell cell112 = row.createCell(rowCol++);
			if (null != fechaPagoSolidario) {
				cell112.setCellValue(fechaPagoSolidario);
			}
			cell112.setCellStyle(styleDate);

			HSSFCell cell111 = row.createCell(rowCol++);
			cell111.setCellValue(totalPeriodo.getPagadoSolidario()
					.doubleValue());
			cell111.setCellStyle(styleAll);

			HSSFCell cell113 = row.createCell(rowCol++);
			cell113.setCellValue(interesAPagoSolidario != null ? interesAPagoSolidario
					.doubleValue() : 0);
			cell113.setCellStyle(styleAll);

			HSSFCell cell9 = row.createCell(rowCol++);
			cell9.setCellValue(totalPeriodo.getCapitalSolidario().doubleValue());
			cell9.setCellStyle(styleAll);
			HSSFCell cell10 = row.createCell(rowCol++);
			cell10.setCellValue(totalPeriodo.getInteresSolidario()
					.doubleValue());
			cell10.setCellStyle(styleAll);

			HSSFCell cell90 = row.createCell(rowCol++);
			cell90.setCellValue(totalPeriodo.getCapitalSolidario()
					.add(totalPeriodo.getInteresSolidario()).doubleValue());
			cell90.setCellStyle(styleAll);
		}
		row = sheet.createRow(index++);
		
		HSSFCell cellTituloTotal = row.createCell(rowCol-4);
		cellTituloTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTituloTotal.setCellStyle(styleBold);
		HSSFCell cellTotalCapital = row.createCell(rowCol-3);
		cellTotalCapital.setCellValue(acta.getCapitalSolidario().doubleValue());
		cellTotalCapital.setCellStyle(styleAll);
		HSSFCell cellTotalInteres = row.createCell(rowCol-2);
		cellTotalInteres.setCellValue(acta.getInteresSolidario().doubleValue());
		cellTotalInteres.setCellStyle(styleAll);
		HSSFCell cellTotalTotal= row.createCell(rowCol-1);
		cellTotalTotal.setCellValue(acta.getCapitalSolidario().add(acta.getInteresSolidario()).doubleValue());		
		cellTotalTotal.setCellStyle(styleAll);		

		return index;

	}

	private static int[] crearInfoParaActaNomina(HSSFWorkbook wb,
			HSSFSheet sheet, Acta acta, int index, HttpServletRequest request) {

		int rowCol = 0;
		try {
			HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
			HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

			HashMap<Date, TotalActaNoOS> peris = acta.generarTotalesAgrupados();

			ArrayList<Date> periodos = new ArrayList<Date>();
			periodos.addAll(peris.keySet());
			Collections.sort(periodos);

			for (Date periodoL : periodos) {
				TotalActaNoOS totalPeriodo = peris.get(periodoL);

				List<ActaPeriodoDeudaEmpresa> detalles = acta
						.getPeriodos(periodoL);

				Date vtoSocialUsu46 = new Date();
				Date vtoSoliAmtima = new Date();
				FeriadosServiceUtil feri = new FeriadosServiceUtil();
				Calendar periodoCalendar = Calendar.getInstance();				
				periodoCalendar.setTime(periodoL);
				periodoCalendar.add(Calendar.MONTH, 1);
				periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);

				vtoSoliAmtima = feri.obtenerSiguienteDiaHabil(periodoCalendar)
						.getTime();

				vtoSocialUsu46 = AfipServiceUtil.getVencimientoOriginalAFIP(
						acta.getEmpresa().getCuit(), periodoL, request);

				for (ActaPeriodoDeudaEmpresa detalle : detalles) {
					rowCol = 0;
					if (detalle.getDetalle() != null) {
						HSSFRow row = sheet.createRow(index++);
						HSSFCell cell = row.createCell(rowCol++);
						cell.setCellValue(totalPeriodo.getPeriodo());
						cell.setCellStyle(styleDate);

						Detalle d = detalle.getDetalle().get(0);
						HSSFCell cell1 = row.createCell(rowCol++);
						cell1.setCellValue(new HSSFRichTextString(detalle
								.getApellido()));
						cell1.setCellStyle(styleAll);

						HSSFCell cell11 = row.createCell(rowCol++);
						cell11.setCellValue(new HSSFRichTextString(detalle
								.getNombre()));
						cell11.setCellStyle(styleAll);

						HSSFCell cell41 = row.createCell(rowCol++);
						cell41.setCellValue(detalle.getFechaIngreso());
						cell41.setCellStyle(styleDate);

						HSSFCell cell411 = row.createCell(rowCol++);
						cell411.setCellValue(new HSSFRichTextString(detalle
								.getCamara()));
						cell411.setCellStyle(styleAll);

						HSSFCell cell3 = row.createCell(rowCol++);
						cell3.setCellValue(new HSSFRichTextString(detalle
								.getTipoAporteAsString()));
						cell3.setCellStyle(styleAll);

						HSSFCell cell4 = row.createCell(rowCol++);
						cell4.setCellValue(detalle.getRemuneracionDeclarada()
								.doubleValue());
						cell4.setCellStyle(styleAll);

						HSSFCell cell40 = row.createCell(rowCol++);
						cell40.setCellValue(detalle.getCalculado()
								.doubleValue());
						cell40.setCellStyle(styleAll);

						HSSFCell cell612 = row.createCell(rowCol++);
						if (detalle.getTipoAporte() == CalculaCapitalCuotaServiceUtil.SOCIAL
								|| detalle.getTipoAporte() == CalculaCapitalCuotaServiceUtil.ART_46
								|| detalle.getTipoAporte() == CalculaCapitalCuotaServiceUtil.USUFRUCTO) {
							cell612.setCellValue(vtoSocialUsu46);
						} else if (detalle.getTipoAporte() == CalculaCapitalCuotaServiceUtil.SOLIDARIO
								|| detalle.getTipoAporte() == CalculaCapitalCuotaServiceUtil.AMTIMA) {
							cell612.setCellValue(vtoSoliAmtima);
						}
						cell612.setCellStyle(styleDate);

						HSSFCell cell42 = row.createCell(rowCol++);
						cell42.setCellValue(detalle.getDetalle().get(0)
								.getInteresAFechaPagada().doubleValue());
						cell42.setCellStyle(styleAll);

						HSSFCell cell142 = row.createCell(rowCol++);
						cell142.setCellValue(detalle.getDetalle().get(0)
								.getMontoPagado().doubleValue());
						cell142.setCellStyle(styleAll);

						HSSFCell cell421 = row.createCell(rowCol++);
						if (null != detalle.getDetalle()
								&& null != detalle.getDetalle().get(0)
										.getFechaPagado()) {
							cell421.setCellValue(detalle.getDetalle().get(0)
									.getFechaPagado());
						}
						cell421.setCellStyle(styleDate);

						HSSFCell cell8 = row.createCell(rowCol++);
						cell8.setCellValue(detalle.getInteres().doubleValue());
						cell8.setCellStyle(styleAll);

						HSSFCell cell80 = row.createCell(rowCol++);
						cell80.setCellValue(detalle.getSubtotalNoOS()
								.doubleValue());
						cell80.setCellStyle(styleAll);
					}
				}

			}
		} catch (Exception e) {
			_log.error(e);
		}
		int[] aux = { rowCol, index };
		return aux;

	}

	private static int getEncabezado(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {

		HSSFCellStyle styleBoldNoBorder = getStyleBold(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
		styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
		styleBoldCenter.setWrapText(true);
		HSSFCellStyle styleAll = getStyleAll(wb);
		index++;
		HSSFRow rowUOMA = sheet.createRow(index);
		HSSFCell cellActaNro0 = rowUOMA.createCell(0);
		cellActaNro0.setCellValue(new HSSFRichTextString(
				"UNION OBRERA MOLINERA ARGENTINA"));
		cellActaNro0.setCellStyle(styleBoldNoBorder);
		HSSFCell cellActaNro1 = rowUOMA.createCell(1);
		cellActaNro1.setCellValue(new HSSFRichTextString(acta.getNumero()));
		cellActaNro1.setCellStyle(styleAll);
		index++;

		HSSFRow rowPlanilla = sheet.createRow(index);
		HSSFCell cell0 = rowPlanilla.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(
				"Planilla de determinación de deuda. Res.108/96"));
		cell0.setCellStyle(styleBoldNoBorder);
		index = index + 2;

		HSSFRow rowEmpresa = sheet.createRow(index);
		HSSFCell cell1 = rowEmpresa.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Empresa: "
				+ acta.getEmpresa().getRazon_soc()));
		cell1.setCellStyle(styleBoldNoBorder);
		index++;

		HSSFRow rowCuit = sheet.createRow(index);
		HSSFCell cell2 = rowCuit.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("C.U.I.T.: "
				+ acta.getEmpresa().getCuit()));
		cell2.setCellStyle(styleBoldNoBorder);
		index++;

		HSSFRow rowLiq = sheet.createRow(index);
		HSSFCell cell3 = rowLiq.createCell(0);
		cell3.setCellValue(new HSSFRichTextString("Liquidación de deuda al: "
				+ acta.getFechaPagoAsString()));
		cell3.setCellStyle(styleBoldNoBorder);
		
		index++;
		
		HSSFRow rowCamaras = sheet.createRow(index);
		HSSFCell cell4= rowCamaras.createCell(0);
		cell4.setCellValue(new HSSFRichTextString("Cámaras: "
				+ acta.getCamaras()));
		cell4.setCellStyle(styleBoldNoBorder);
		
		
		String inspectores="";
		for(Inspector i:acta.getInspectoresFirmantes()) {
			inspectores += i.getNombre()+ " - ";
		}
		
		if(inspectores.length()>0) {
		   index++;	
		   HSSFRow rowInspectores = sheet.createRow(index);
		   Cell cell000 = rowInspectores.createCell(0);
		   cell000.setCellValue(new HSSFRichTextString(
				"Inspectores Firmantes "+ inspectores));
		   cell000.setCellStyle(styleBold);
		}
		
		

		return index+1;

	}

	private static int getEncabezadoArt46(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {		
		HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
		styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
		styleBoldCenter.setWrapText(true);		

		index = index + 2;

		int indexCol = 0;

		HSSFRow rowSubEncab = sheet.createRow(index++);
		HSSFCell cell04 = rowSubEncab.createCell(indexCol++);
		cell04.setCellValue(new HSSFRichTextString("Período"));
		cell04.setCellStyle(styleBoldCenter);

		HSSFRow rowEncab = sheet.createRow(index);
		rowEncab.setHeight((short) 1000);
		sheet.addMergedRegion(new CellRangeAddress(index - 1, index, 0, 0));

		HSSFCell cell41 = rowEncab.createCell(indexCol++);
		cell41.setCellValue(new HSSFRichTextString("Vencimiento"));
		cell41.setCellStyle(styleBoldCenter);

		HSSFCell cell041 = rowSubEncab.createCell(indexCol - 1);
		cell041.setCellValue(new HSSFRichTextString("ART. 46"));
		cell041.setCellStyle(styleBoldCenter);

		HSSFCell cell411 = rowEncab.createCell(indexCol++);
		cell411.setCellValue(new HSSFRichTextString("Capital a Depositar"));
		cell411.setCellStyle(styleBoldCenter);

		HSSFCell cell4411 = rowEncab.createCell(indexCol++);
		cell4411.setCellValue(new HSSFRichTextString("Fecha Pago"));
		cell4411.setCellStyle(styleBoldCenter);

		HSSFCell cell441 = rowEncab.createCell(indexCol++);
		cell441.setCellValue(new HSSFRichTextString("Pagado"));
		cell441.setCellStyle(styleBoldCenter);

		HSSFCell cell4412 = rowEncab.createCell(indexCol++);
		cell4412.setCellValue(new HSSFRichTextString("Int.Deveng. al Pago"));
		cell4412.setCellStyle(styleBoldCenter);

		HSSFCell cell43 = rowEncab.createCell(indexCol++);
		cell43.setCellValue(new HSSFRichTextString("Deuda Capital"));
		cell43.setCellStyle(styleBoldCenter);

		HSSFCell cell44 = rowEncab.createCell(indexCol++);
		cell44.setCellValue(new HSSFRichTextString("Interés"));
		cell44.setCellStyle(styleBoldCenter);

		HSSFCell cell45 = rowEncab.createCell(indexCol++);
		cell45.setCellValue(new HSSFRichTextString("Total"));
		cell45.setCellStyle(styleBoldCenter);

		sheet.addMergedRegion(new CellRangeAddress(index - 1, index - 1, 1,
				indexCol - 1));
		return index;
	}

	private static int getEncabezadoUsufructo(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {		
		HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
		styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
		styleBoldCenter.setWrapText(true);
		
		index = index + 2;

		int indexCol = 0;

		HSSFRow rowSubUsufructo = sheet.createRow(index++);
		HSSFCell cell04U = rowSubUsufructo.createCell(indexCol++);
		cell04U.setCellValue(new HSSFRichTextString("Período"));
		cell04U.setCellStyle(styleBoldCenter);
		
		HSSFRow rowUsufructo = sheet.createRow(index);
		rowUsufructo.setHeight((short) 1000);
		
		sheet.addMergedRegion(new CellRangeAddress(index - 1, index, 0, 0));

		HSSFCell cell442 = rowUsufructo.createCell(indexCol++);
		cell442.setCellValue(new HSSFRichTextString("Vencimiento"));
		cell442.setCellStyle(styleBoldCenter);

		HSSFCell cell0442 = rowSubUsufructo.createCell(indexCol - 1);
		cell0442.setCellValue(new HSSFRichTextString("CTA. USUFRUCTO"));
		cell0442.setCellStyle(styleBoldCenter);

		HSSFCell cell431 = rowUsufructo.createCell(indexCol++);
		cell431.setCellValue(new HSSFRichTextString("Capital a Depositar"));
		cell431.setCellStyle(styleBoldCenter);

		HSSFCell cell451 = rowUsufructo.createCell(indexCol++);
		cell451.setCellValue(new HSSFRichTextString("Fecha Pago"));
		cell451.setCellStyle(styleBoldCenter);

		HSSFCell cell46 = rowUsufructo.createCell(indexCol++);
		cell46.setCellValue(new HSSFRichTextString("Pagado"));
		cell46.setCellStyle(styleBoldCenter);

		HSSFCell cell4612 = rowUsufructo.createCell(indexCol++);
		cell4612.setCellValue(new HSSFRichTextString("Int.Deveng. al Pago"));
		cell4612.setCellStyle(styleBoldCenter);

		HSSFCell cell461 = rowUsufructo.createCell(indexCol++);
		cell461.setCellValue(new HSSFRichTextString("Deuda Capital"));
		cell461.setCellStyle(styleBoldCenter);

		HSSFCell cell4611 = rowUsufructo.createCell(indexCol++);
		cell4611.setCellValue(new HSSFRichTextString("Interés"));
		cell4611.setCellStyle(styleBoldCenter);

		HSSFCell cell4613 = rowUsufructo.createCell(indexCol++);
		cell4613.setCellValue(new HSSFRichTextString("Total"));
		cell4613.setCellStyle(styleBoldCenter);
		sheet.addMergedRegion(new CellRangeAddress(index - 1, index - 1, 1,
				indexCol - 1));
		return index;
	}

	private static int getEncabezadoSocial(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {		
		HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
		styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
		styleBoldCenter.setWrapText(true);		

		index = index + 2;

		int indexCol = 0;
		
		HSSFRow rowSubSocial = sheet.createRow(index++);
		HSSFCell cell04C = rowSubSocial.createCell(indexCol++);
		cell04C.setCellValue(new HSSFRichTextString("Período"));
		cell04C.setCellStyle(styleBoldCenter);
		
		HSSFRow rowSocial = sheet.createRow(index);
		rowSocial.setHeight((short) 1000);
		sheet.addMergedRegion(new CellRangeAddress(index - 1, index, 0, 0));

		HSSFCell cell462 = rowSocial.createCell(indexCol++);
		cell462.setCellValue(new HSSFRichTextString("Vencimiento"));
		cell462.setCellStyle(styleBoldCenter);

		HSSFCell cell4621 = rowSubSocial.createCell(indexCol - 1);
		cell4621.setCellValue(new HSSFRichTextString("CUOTA SOCIAL UOMA"));
		cell4621.setCellStyle(styleBoldCenter);

		HSSFCell cell452 = rowSocial.createCell(indexCol++);
		cell452.setCellValue(new HSSFRichTextString("Capital a Depositar"));
		cell452.setCellStyle(styleBoldCenter);

		HSSFCell cell47 = rowSocial.createCell(indexCol++);
		cell47.setCellValue(new HSSFRichTextString("Fecha Pago"));
		cell47.setCellStyle(styleBoldCenter);

		HSSFCell cell48 = rowSocial.createCell(indexCol++);
		cell48.setCellValue(new HSSFRichTextString("Pagado"));
		cell48.setCellStyle(styleBoldCenter);

		HSSFCell cell4812 = rowSocial.createCell(indexCol++);
		cell4812.setCellValue(new HSSFRichTextString("Int.Deveng. al Pago"));
		cell4812.setCellStyle(styleBoldCenter);

		HSSFCell cell481 = rowSocial.createCell(indexCol++);
		cell481.setCellValue(new HSSFRichTextString("Deuda Capital"));
		cell481.setCellStyle(styleBoldCenter);

		HSSFCell cell4811 = rowSocial.createCell(indexCol++);
		cell4811.setCellValue(new HSSFRichTextString("Interes"));
		cell4811.setCellStyle(styleBoldCenter);

		HSSFCell cell4813 = rowSocial.createCell(indexCol++);
		cell4813.setCellValue(new HSSFRichTextString("Total"));
		cell4813.setCellStyle(styleBoldCenter);

		sheet.addMergedRegion(new CellRangeAddress(index - 1, index - 1, 1,
				indexCol - 1));

		return index;
	}

	private static int getEncabezadoSolidario(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {
		
		HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
		styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
		styleBoldCenter.setWrapText(true);
		

		index = index + 2;

		int indexCol = 0;

		HSSFRow rowSubSolidario = sheet.createRow(index++);
		HSSFCell cell04S = rowSubSolidario.createCell(indexCol++);
		cell04S.setCellValue(new HSSFRichTextString("Período"));
		cell04S.setCellStyle(styleBoldCenter);
		
		HSSFRow rowSolidario = sheet.createRow(index);
		rowSolidario.setHeight((short) 1000);
		sheet.addMergedRegion(new CellRangeAddress(index - 1, index, 0, 0));

		HSSFCell cell482 = rowSolidario.createCell(indexCol++);
		cell482.setCellValue(new HSSFRichTextString("Vencimiento"));
		cell482.setCellStyle(styleBoldCenter);

		HSSFCell cell4821 = rowSubSolidario.createCell(indexCol - 1);
		cell4821.setCellValue(new HSSFRichTextString("APORTE SOLIDARIO"));
		cell4821.setCellStyle(styleBoldCenter);

		HSSFCell cell491 = rowSolidario.createCell(indexCol++);
		cell491.setCellValue(new HSSFRichTextString("Capital a Depositar"));
		cell491.setCellStyle(styleBoldCenter);

		HSSFCell cell49 = rowSolidario.createCell(indexCol++);
		cell49.setCellValue(new HSSFRichTextString("Fecha Pago"));
		cell49.setCellStyle(styleBoldCenter);

		HSSFCell cell50 = rowSolidario.createCell(indexCol++);
		cell50.setCellValue(new HSSFRichTextString("Pago"));
		cell50.setCellStyle(styleBoldCenter);

		HSSFCell cell5012 = rowSolidario.createCell(indexCol++);
		cell5012.setCellValue(new HSSFRichTextString("Int.Deveng. al Pago"));
		cell5012.setCellStyle(styleBoldCenter);

		HSSFCell cell501 = rowSolidario.createCell(indexCol++);
		cell501.setCellValue(new HSSFRichTextString("Deuda Capital"));
		cell501.setCellStyle(styleBoldCenter);

		HSSFCell cell5011 = rowSolidario.createCell(indexCol++);
		cell5011.setCellValue(new HSSFRichTextString("Interes"));
		cell5011.setCellStyle(styleBoldCenter);

		HSSFCell cell5013 = rowSolidario.createCell(indexCol++);
		cell5013.setCellValue(new HSSFRichTextString("Total"));
		cell5013.setCellStyle(styleBoldCenter);

		sheet.addMergedRegion(new CellRangeAddress(index - 1, index - 1, 1,
				indexCol - 1));
		return index;
	}

	private static int getEncabezadoTotal(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {

		HSSFCellStyle styleBoldNoBorder = getStyleBold(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
		styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
		styleBoldCenter.setWrapText(true);
		HSSFCellStyle styleAll = getStyleAll(wb);

		index = index + 2;

		int indexCol = 0;

		indexCol = 0;

		indexCol = 0;
		index = index + 2;
		HSSFRow rowTotal = sheet.createRow(index);
		rowTotal.setHeight((short) 1000);
		sheet.addMergedRegion(new CellRangeAddress(index - 1, index, 0, 0));

		HSSFCell cell51 = rowTotal.createCell(indexCol++);
		cell51.setCellValue(new HSSFRichTextString("DEUDA TOTAL"));
		cell51.setCellStyle(styleBoldCenter);

		sheet.addMergedRegion(new CellRangeAddress(index - 1, index,
				indexCol - 1, indexCol - 1));

		return index;

	}

	private static int getEncabezadoAmtima(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {
		try {
			HSSFCellStyle styleBoldNoBorder = getStyleBold(wb);
			HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
			HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
			styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
			styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
			styleBoldCenter.setWrapText(true);
			HSSFCellStyle styleAll = getStyleAll(wb);
			index++;
			HSSFRow rowUOMA = sheet.createRow(index);
			HSSFCell cellActaNro0 = rowUOMA.createCell(0);
			cellActaNro0
					.setCellValue(new HSSFRichTextString(
							"ASOCIACION MUTUAL DE TRABAJADORES DE LA INDUSTRIA MOLINERA ARGENTINA"));
			cellActaNro0.setCellStyle(styleBoldNoBorder);
			HSSFCell cellActaNro1 = rowUOMA.createCell(1);
			cellActaNro1.setCellValue(new HSSFRichTextString(acta.getNumero()));
			cellActaNro1.setCellStyle(styleAll);
			index++;

			HSSFRow rowPlanilla = sheet.createRow(index);
			HSSFCell cell0 = rowPlanilla.createCell(0);
			cell0.setCellValue(new HSSFRichTextString(
					"Planilla de determinación de deuda. Res.108/96"));
			cell0.setCellStyle(styleBoldNoBorder);
			index = index + 2;

			HSSFRow rowEmpresa = sheet.createRow(index);
			HSSFCell cell1 = rowEmpresa.createCell(0);
			cell1.setCellValue(new HSSFRichTextString("Empresa: "
					+ acta.getEmpresa().getRazon_soc()));
			cell1.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowCuit = sheet.createRow(index);
			HSSFCell cell2 = rowCuit.createCell(0);
			cell2.setCellValue(new HSSFRichTextString("C.U.I.T.: "
					+ acta.getEmpresa().getCuit()));
			cell2.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowLiq = sheet.createRow(index);
			HSSFCell cell3 = rowLiq.createCell(0);
			cell3.setCellValue(new HSSFRichTextString(
					"Liquidación de deuda al: " + acta.getFechaPagoAsString()));
			cell3.setCellStyle(styleBoldNoBorder);

			index = index + 2;

			int indexCol = 0;

			HSSFRow rowSubEncab = sheet.createRow(index++);
			HSSFCell cell04 = rowSubEncab.createCell(indexCol++);
			cell04.setCellValue(new HSSFRichTextString("Período"));
			cell04.setCellStyle(styleBoldCenter);

			HSSFRow rowEncab = sheet.createRow(index);

			sheet.addMergedRegion(new CellRangeAddress(index - 1, index, 0, 0));

			HSSFCell cell41 = rowEncab.createCell(indexCol++);
			cell41.setCellValue(new HSSFRichTextString("Vencimiento"));
			cell41.setCellStyle(styleBoldCenter);

			HSSFCell cell041 = rowSubEncab.createCell(indexCol - 1);
			cell041.setCellValue(new HSSFRichTextString("CUOTA A.M.T.I.M.A."));
			cell041.setCellStyle(styleBoldCenter);

			HSSFCell cell411 = rowEncab.createCell(indexCol++);
			cell411.setCellValue(new HSSFRichTextString("Capital a Depositar"));
			cell411.setCellStyle(styleBoldCenter);

			HSSFCell cell4411 = rowEncab.createCell(indexCol++);
			cell4411.setCellValue(new HSSFRichTextString("Fecha Pago"));
			cell4411.setCellStyle(styleBoldCenter);

			HSSFCell cell441 = rowEncab.createCell(indexCol++);
			cell441.setCellValue(new HSSFRichTextString("Pagado"));
			cell441.setCellStyle(styleBoldCenter);

			HSSFCell cell4412 = rowEncab.createCell(indexCol++);
			cell4412.setCellValue(new HSSFRichTextString("Int.Deveng. al Pago"));
			cell4412.setCellStyle(styleBoldCenter);

			HSSFCell cell43 = rowEncab.createCell(indexCol++);
			cell43.setCellValue(new HSSFRichTextString("Deuda Capital"));
			cell43.setCellStyle(styleBoldCenter);

			HSSFCell cell44 = rowEncab.createCell(indexCol++);
			cell44.setCellValue(new HSSFRichTextString("Interés"));
			cell44.setCellStyle(styleBoldCenter);

			HSSFCell cell45 = rowEncab.createCell(indexCol++);
			cell45.setCellValue(new HSSFRichTextString("Total"));
			cell45.setCellStyle(styleBoldCenter);

			sheet.addMergedRegion(new CellRangeAddress(index - 1, index - 1, 1,
					indexCol - 1));

			/*HSSFCell cell51 = rowSubEncab.createCell(indexCol++);
			cell51.setCellValue(new HSSFRichTextString("DEUDA TOTAL"));
			cell51.setCellStyle(styleBoldCenter);*/

			/*sheet.addMergedRegion(new CellRangeAddress(index - 1, index,
					indexCol - 1, indexCol - 1));*/

		} catch (Exception e) {
			_log.error(e);
		}

		return index;

	}

	private static int[] crearInfoParaActaAMTIMA(HSSFWorkbook wb,
			HSSFSheet sheet, Acta acta, int index) {

		int rowCol = 0;
		try {
			HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
			HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
			HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);

			HashMap<Date, TotalActaNoOS> peris = acta.generarTotalesAgrupados();

			ArrayList<Date> periodos = new ArrayList<Date>();
			periodos.addAll(peris.keySet());
			Collections.sort(periodos);
			HSSFRow row =null;
			for (Date periodoL : periodos) {
				rowCol = 0;
				TotalActaNoOS totalPeriodo = peris.get(periodoL);
				row = sheet.createRow(index++);
				HSSFCell cell = row.createCell(rowCol++);
				cell.setCellValue(totalPeriodo.getPeriodo());
				cell.setCellStyle(styleDate);

				BigDecimal interesAPagoAmtima = BigDecimal.ZERO;

				Date fechaPagoAmtima = null;

				List<ActaPeriodoDeudaEmpresa> detalles = acta
						.getPeriodos(periodoL);

				for (ActaPeriodoDeudaEmpresa detalle : detalles) {
					if (detalle.getDetalle() != null) {
						Detalle d = detalle.getDetalle().get(0);
						if (d.getTipoAporte() == CalculaCapitalCuotaServiceUtil.AMTIMA) {
							interesAPagoAmtima = interesAPagoAmtima.add(d
									.getInteresAFechaPagada());
							fechaPagoAmtima = d.getFechaPagado();
						}
					}
				}

				Date vtoSoliAmtima = new Date();
				FeriadosServiceUtil feri = new FeriadosServiceUtil();
				Calendar periodoCalendar = Calendar.getInstance();
				periodoCalendar.setTime(periodoL);
				periodoCalendar.add(Calendar.MONTH,1);
				periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);

				vtoSoliAmtima = feri.obtenerSiguienteDiaHabil(periodoCalendar)
						.getTime();

				HSSFCell cell1 = row.createCell(rowCol++);
				cell1.setCellValue(vtoSoliAmtima);
				cell1.setCellStyle(styleDate);

				HSSFCell cell11 = row.createCell(rowCol++);
				cell11.setCellValue(totalPeriodo.getCalculadoAmtima()
						.doubleValue());
				cell11.setCellStyle(styleAll);

				HSSFCell cell412 = row.createCell(rowCol++);
				if (null != fechaPagoAmtima) {
					cell412.setCellValue(fechaPagoAmtima);
				}
				cell412.setCellStyle(styleDate);

				HSSFCell cell41 = row.createCell(rowCol++);
				cell41.setCellValue(totalPeriodo.getPagadoAmtima()
						.doubleValue());
				cell41.setCellStyle(styleAll);

				HSSFCell cell411 = row.createCell(rowCol++);
				cell411.setCellValue(interesAPagoAmtima != null ? interesAPagoAmtima
						.doubleValue() : 0);
				cell411.setCellStyle(styleAll);

				HSSFCell cell3 = row.createCell(rowCol++);
				cell3.setCellValue(totalPeriodo.getCapitalAmtima()
						.doubleValue());
				cell3.setCellStyle(styleAll);

				HSSFCell cell4 = row.createCell(rowCol++);
				cell4.setCellValue(totalPeriodo.getInteresAmtima()
						.doubleValue());
				cell4.setCellStyle(styleAll);

				HSSFCell cell40 = row.createCell(rowCol++);
				cell40.setCellValue(totalPeriodo.getCapitalAmtima()
						.add(totalPeriodo.getInteresAmtima()).doubleValue());
				cell40.setCellStyle(styleAll);

				/*HSSFCell cell12 = row.createCell(rowCol++);
				cell12.setCellValue(totalPeriodo.getTotal().doubleValue());
				cell12.setCellStyle(styleAll);*/

			}			
			row = sheet.createRow(index++);			
			HSSFCell cellTituloTotal = row.createCell(rowCol-4);
			cellTituloTotal.setCellValue(new HSSFRichTextString("Total"));
			cellTituloTotal.setCellStyle(styleBold);
			HSSFCell cellTotalCapital = row.createCell(rowCol-3);
			cellTotalCapital.setCellValue(acta.getCapitalAmtima().doubleValue());
			cellTotalCapital.setCellStyle(styleAll);
			HSSFCell cellTotalInteres = row.createCell(rowCol-2);
			cellTotalInteres.setCellValue(acta.getInteresAmtima().doubleValue());
			cellTotalInteres.setCellStyle(styleAll);
			HSSFCell cellTotalTotal= row.createCell(rowCol-1);
			cellTotalTotal.setCellValue(acta.getCapitalAmtima().add(acta.getInteresAmtima()).doubleValue());		
			cellTotalTotal.setCellStyle(styleAll);		
		} catch (Exception e) {
			_log.error(e);
		}
		int[] aux = { rowCol, index };
		return aux;

	}

	private static int getEncabezadoNomina(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {
		try {
			HSSFCellStyle styleBoldNoBorder = getStyleBold(wb);
			HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
			HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
			styleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
			styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
			styleBoldCenter.setWrapText(true);
			HSSFCellStyle styleAll = getStyleAll(wb);
			index++;
			HSSFRow rowUOMA = sheet.createRow(index);
			HSSFCell cellActaNro0 = rowUOMA.createCell(0);
			cellActaNro0.setCellValue(new HSSFRichTextString(
					"UNION OBRERA MOLINERA ARGENTINA"));
			cellActaNro0.setCellStyle(styleBoldNoBorder);
			HSSFCell cellActaNro1 = rowUOMA.createCell(1);
			cellActaNro1.setCellValue(new HSSFRichTextString(acta.getNumero()));
			cellActaNro1.setCellStyle(styleAll);
			index++;

			HSSFRow rowPlanilla = sheet.createRow(index);
			HSSFCell cell0 = rowPlanilla.createCell(0);
			cell0.setCellValue(new HSSFRichTextString(
					"Planilla de determinación de deuda. Res.108/96"));
			cell0.setCellStyle(styleBoldNoBorder);
			index = index + 2;

			HSSFRow rowEmpresa = sheet.createRow(index);
			HSSFCell cell1 = rowEmpresa.createCell(0);
			cell1.setCellValue(new HSSFRichTextString("Empresa: "
					+ acta.getEmpresa().getRazon_soc()));
			cell1.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowCuit = sheet.createRow(index);
			HSSFCell cell2 = rowCuit.createCell(0);
			cell2.setCellValue(new HSSFRichTextString("C.U.I.T.: "
					+ acta.getEmpresa().getCuit()));
			cell2.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowLiq = sheet.createRow(index);
			HSSFCell cell3 = rowLiq.createCell(0);
			cell3.setCellValue(new HSSFRichTextString(
					"Liquidación de deuda al: " + acta.getFechaPagoAsString()));
			cell3.setCellStyle(styleBoldNoBorder);

			index = index + 2;

			int indexCol = 0;

			HSSFRow rowEncab = sheet.createRow(index);
			HSSFCell cell04 = rowEncab.createCell(indexCol++);
			cell04.setCellValue(new HSSFRichTextString("Período"));
			cell04.setCellStyle(styleBoldCenter);

			HSSFCell cell41 = rowEncab.createCell(indexCol++);
			cell41.setCellValue(new HSSFRichTextString("Apellido"));
			cell41.setCellStyle(styleBoldCenter);

			HSSFCell cell041 = rowEncab.createCell(indexCol++);
			cell041.setCellValue(new HSSFRichTextString("Nombre"));
			cell041.setCellStyle(styleBoldCenter);

			HSSFCell cell411 = rowEncab.createCell(indexCol++);
			cell411.setCellValue(new HSSFRichTextString("Fecha de Ingreso"));
			cell411.setCellStyle(styleBold);

			HSSFCell cell4411 = rowEncab.createCell(indexCol++);
			cell4411.setCellValue(new HSSFRichTextString("Cámara"));
			cell4411.setCellStyle(styleBoldCenter);

			HSSFCell cell441 = rowEncab.createCell(indexCol++);
			cell441.setCellValue(new HSSFRichTextString("Tipo de Aporte"));
			cell441.setCellStyle(styleBoldCenter);

			HSSFCell cell4412 = rowEncab.createCell(indexCol++);
			cell4412.setCellValue(new HSSFRichTextString(
					"Remuneración declarada"));
			cell4412.setCellStyle(styleBoldCenter);

			HSSFCell cell43 = rowEncab.createCell(indexCol++);
			cell43.setCellValue(new HSSFRichTextString("Calculado"));
			cell43.setCellStyle(styleBoldCenter);

			HSSFCell cell443 = rowEncab.createCell(indexCol++);
			cell443.setCellValue(new HSSFRichTextString("Fecha Vto."));
			cell443.setCellStyle(styleBoldCenter);

			HSSFCell cell44 = rowEncab.createCell(indexCol++);
			cell44.setCellValue(new HSSFRichTextString(
					"Int.Dev. al día de pago"));
			cell44.setCellStyle(styleBoldCenter);

			HSSFCell cell45 = rowEncab.createCell(indexCol++);
			cell45.setCellValue(new HSSFRichTextString("Pagado"));
			cell45.setCellStyle(styleBold);

			HSSFCell cell442 = rowEncab.createCell(indexCol++);
			cell442.setCellValue(new HSSFRichTextString("Fecha de Pago"));
			cell442.setCellStyle(styleBoldCenter);

			HSSFCell cell0442 = rowEncab.createCell(indexCol++);
			cell0442.setCellValue(new HSSFRichTextString("Interés"));
			cell0442.setCellStyle(styleBoldCenter);

			HSSFCell cell431 = rowEncab.createCell(indexCol++);
			cell431.setCellValue(new HSSFRichTextString("Subtotal"));
			cell431.setCellStyle(styleBoldCenter);

		} catch (Exception e) {
			_log.error(e);
		}

		return index;

	}

	private static int getResumenGeneral(HSSFWorkbook wb, HSSFSheet sheet,
			int index, Acta acta) {
		try {
			HSSFCellStyle styleBoldNoBorder = getStyleBold(wb);
			HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
			HSSFCellStyle styleBoldCenter = getStyleBoldWithBorder(wb);
			styleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
			HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
			index++;
			HSSFRow rowUOMA = sheet.createRow(index);
			HSSFCell cellActaNro0 = rowUOMA.createCell(0);
			cellActaNro0.setCellValue(new HSSFRichTextString(
					"UNION OBRERA MOLINERA ARGENTINA"));
			cellActaNro0.setCellStyle(styleBoldNoBorder);
			HSSFCell cellActaNro1 = rowUOMA.createCell(1);
			cellActaNro1.setCellValue(new HSSFRichTextString(acta.getNumero()));
			cellActaNro1.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowPlanilla = sheet.createRow(index);
			HSSFCell cell0 = rowPlanilla.createCell(0);
			cell0.setCellValue(new HSSFRichTextString(
					"Planilla de determinación de deuda. Res.108/96"));
			cell0.setCellStyle(styleBoldNoBorder);
			index = index + 2;

			HSSFRow rowEmpresa = sheet.createRow(index);
			HSSFCell cell1 = rowEmpresa.createCell(0);
			cell1.setCellValue(new HSSFRichTextString("Empresa: "
					+ acta.getEmpresa().getRazon_soc()));
			cell1.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowCuit = sheet.createRow(index);
			HSSFCell cell2 = rowCuit.createCell(0);
			cell2.setCellValue(new HSSFRichTextString("C.U.I.T.: "
					+ acta.getEmpresa().getCuit()));
			cell2.setCellStyle(styleBoldNoBorder);
			index++;

			HSSFRow rowLiq = sheet.createRow(index);
			HSSFCell cell3 = rowLiq.createCell(0);
			cell3.setCellValue(new HSSFRichTextString(
					"Liquidación de deuda al: " + acta.getFechaPagoAsString()));
			cell3.setCellStyle(styleBoldNoBorder);

			index = index + 3;

			int indexCol = 0;

			HSSFRow rowEncab0 = sheet.createRow(index++);
			HSSFCell cell004 = rowEncab0.createCell(indexCol++);
			cell004.setCellValue(new HSSFRichTextString("TIPO DE APORTE"));
			cell004.setCellStyle(styleBoldCenter);

			HSSFCell cell041 = rowEncab0.createCell(indexCol++);
			cell041.setCellValue(new HSSFRichTextString("IMPORTE"));
			cell041.setCellStyle(styleBoldCenter);

			indexCol = 0;
			HSSFRow rowEncab = sheet.createRow(index++);
			HSSFCell cell04 = rowEncab.createCell(indexCol++);
			cell04.setCellValue(new HSSFRichTextString("Cap.Cta.Social UOMA"));
			cell04.setCellStyle(styleBold);

			HSSFCell cell41 = rowEncab.createCell(indexCol++);
			cell41.setCellValue(acta.getCapitalSindicato().doubleValue());
			cell41.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab1 = sheet.createRow(index++);
			HSSFCell cell0141 = rowEncab1.createCell(indexCol++);
			cell0141.setCellValue(new HSSFRichTextString("Int.Cta.Social UOMA "));
			cell0141.setCellStyle(styleBold);

			HSSFCell cell411 = rowEncab1.createCell(indexCol++);
			cell411.setCellValue(acta.getInteresSindicato().doubleValue());
			cell411.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab2 = sheet.createRow(index++);

			HSSFCell cell4411 = rowEncab2.createCell(indexCol++);
			cell4411.setCellValue(new HSSFRichTextString("Cap.Apo.Solidario"));
			cell4411.setCellStyle(styleBold);

			HSSFCell cell441 = rowEncab2.createCell(indexCol++);
			cell441.setCellValue(acta.getCapitalSolidario().doubleValue());
			cell441.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab3 = sheet.createRow(index++);

			HSSFCell cell4412 = rowEncab3.createCell(indexCol++);
			cell4412.setCellValue(new HSSFRichTextString("Int.Apo.Solidario"));
			cell4412.setCellStyle(styleBold);

			HSSFCell cell443 = rowEncab3.createCell(indexCol++);
			cell443.setCellValue(acta.getInteresSolidario().doubleValue());
			cell443.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab4 = sheet.createRow(index++);

			HSSFCell cell43 = rowEncab4.createCell(indexCol++);
			cell43.setCellValue(new HSSFRichTextString("Cap.Usufructo"));
			cell43.setCellStyle(styleBold);

			HSSFCell cell44 = rowEncab4.createCell(indexCol++);
			cell44.setCellValue(acta.getCapitalUsufructo().doubleValue());
			cell44.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab5 = sheet.createRow(index++);

			HSSFCell cell45 = rowEncab5.createCell(indexCol++);
			cell45.setCellValue(new HSSFRichTextString("Int.Usufructo"));
			cell45.setCellStyle(styleBold);

			HSSFCell cell442 = rowEncab5.createCell(indexCol++);
			cell442.setCellValue(acta.getInteresUsufructo().doubleValue());
			cell442.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab6 = sheet.createRow(index++);

			HSSFCell cell0442 = rowEncab6.createCell(indexCol++);
			cell0442.setCellValue(new HSSFRichTextString("Cap.Art.46"));
			cell0442.setCellStyle(styleBold);

			HSSFCell cell04421 = rowEncab6.createCell(indexCol++);
			cell04421.setCellValue(acta.getCapitalArt46().doubleValue());
			cell04421.setCellStyle(styleAll);

			indexCol = 0;
			HSSFRow rowEncab7 = sheet.createRow(index++);

			HSSFCell cell431 = rowEncab7.createCell(indexCol++);
			cell431.setCellValue(new HSSFRichTextString("Int.Art.46"));
			cell431.setCellStyle(styleBold);

			HSSFCell cell4311 = rowEncab7.createCell(indexCol++);
			cell4311.setCellValue(acta.getInteresArt46().doubleValue());
			cell4311.setCellStyle(styleAll);

		} catch (Exception e) {
			_log.error(e);
		}

		return index;

	}

}
