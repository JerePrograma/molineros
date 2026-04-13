package ar.com.ospim.liquidaciones.reportes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal;
import ar.com.ospim.liquidaciones.services.BusquedaDebitosTercerizadorasServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteDebitosaTercerizadorasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteDebitosaTercerizadorasExcel.class);

	BigDecimal totalCargoPrestadora = new BigDecimal("0");

	
	@SuppressWarnings("unchecked")
	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res)  {

		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
		Date fechaDesde = null;
		Date fechaEjecucion = null;
		Date fechaFin = null;

		User user =  null;
		try {
			user = PortalUtil.getUser(renderRequest);
		} catch (SystemException e1) {
			_log.debug(e1.getMessage()); 
		} catch (PortalException e) {
			_log.debug(e.getMessage()); 
		}

		
		Calendar cal = DateUtils.getCalendarGMTMenos3();

	
				
		
		try {
			fechaDesde = formatoDeFechas.parse(01 + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		
		
		String dia = Integer.toString(cal.get(Calendar.DATE));
		String mes = Integer.toString(cal.get(Calendar.MONTH) -1); //menos dos meses para atras
		String annio = Integer.toString(cal.get(Calendar.YEAR));
				
//	String dia = Integer.toString(01);
	//	String mes = Integer.toString(Integer.parseInt(fechaDesdeMes)  -1); //menos dos meses para atras
	//	String annio = fechaDesdeAnio;
		
		
		try {
			fechaEjecucion = formatoDeFechas.parse(dia + "/"
							+ (mes) + "/"
							+ annio);
		} catch (Exception e) {
			fechaEjecucion = null;
		}			
	
		
		Date fechaHasta = null;
		fechaHasta = DateUtils.getLastDateOfMonth(fechaDesde, false);
		
		String tercerizadoras = ParamUtil.getString(renderRequest, "tipo_debitos_tercerizadoras");

		
		boolean grabarDebitos = ParamUtil.getBoolean(renderRequest, "grabarDebitos");

		DebitosaTotal debitosaTotal = new DebitosaTotal();
		DebitosaTotal debitosaTotalPersistido = null;
		
		debitosaTotal = BusquedaDebitosTercerizadorasServiceUtil.getBuscarTotalesDebitos(fechaHasta,  tercerizadoras);
		if (debitosaTotal != null && debitosaTotal.isExisteDebito()) {
			debitosaTotalPersistido = new DebitosaTotal();
			debitosaTotalPersistido = debitosaTotal;
		}
		
		
		
		List<DebitosLiquidacionesPendientes> debitosLiquidacionesPendientes = new ArrayList<DebitosLiquidacionesPendientes>();
		List<DebitosHospitales> debitosHospitales = new ArrayList<DebitosHospitales>();
		List<DebitosaReintegros> debitosReintegros = new ArrayList<DebitosaReintegros>();
		List<DebitosaPrestadores> debitosPrestadores = new ArrayList<DebitosaPrestadores>();
		

		try {
			if (debitosaTotal != null && debitosaTotal.isExisteDebito()) {
				debitosLiquidacionesPendientes =  (List<DebitosLiquidacionesPendientes>) BusquedaDebitosTercerizadorasServiceUtil.
						getBusquedaDebitosaGrabados(WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES, fechaHasta, tercerizadoras);
				debitosHospitales =   (List<DebitosHospitales>) BusquedaDebitosTercerizadorasServiceUtil.
						getBusquedaDebitosaGrabados(WebKeysLiquidaciones.DEBITOS_HOSPITALES, fechaHasta, tercerizadoras);
				debitosReintegros =   (List<DebitosaReintegros>) BusquedaDebitosTercerizadorasServiceUtil.
						getBusquedaDebitosaGrabados(WebKeysLiquidaciones.DEBITOS_REINTEGROS, fechaHasta, tercerizadoras);
				debitosPrestadores =   (List<DebitosaPrestadores>) BusquedaDebitosTercerizadorasServiceUtil.
						getBusquedaDebitosaGrabados(WebKeysLiquidaciones.DEBITOS_PRESTADORES, fechaHasta, tercerizadoras);
			}else{
				debitosLiquidacionesPendientes =  BusquedaDebitosTercerizadorasServiceUtil
						.getBusquedaDebitosaLiquidacionesPendientes(fechaEjecucion, fechaHasta, debitosaTotal, tercerizadoras);
				
				debitosHospitales =  BusquedaDebitosTercerizadorasServiceUtil
						.getBusquedaDebitosaHospitales(fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
				
				debitosReintegros = BusquedaDebitosTercerizadorasServiceUtil
						.getBusquedaDebitosReintegros(fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
				
				debitosPrestadores = BusquedaDebitosTercerizadorasServiceUtil
						.getBusquedaDebitosPrestadores(fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
			}
			
		} catch (Exception e) {
			_log.debug("No se pudo obtener debitos");
			_log.debug(e.getStackTrace());
		}
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
		
		
		//HSSFSheet sheet = wb.createSheet("AUTOGESTION");
		
		HSSFSheet sheet = wb.createSheet("LIQUIDACIONES PENDIENTES");
		
		generarReporteLiquidacionesPendientes(debitosLiquidacionesPendientes, wb, sheet);
		
		HSSFSheet sheet2 = wb.createSheet("HOSPITALES");

		generarReporteHospitales(debitosHospitales, wb, sheet2);
		
		HSSFSheet sheet3 = wb.createSheet("REINTEGROS");
		
		generarReporteReintegros(debitosReintegros, wb, sheet3);
		
		HSSFSheet sheet4 = wb.createSheet("PRESTADORES");
		
		generarReporterestadores(debitosPrestadores , wb, sheet4);
		
		HSSFSheet sheet5 = wb.createSheet("TOTAL");
		BigDecimal totalDebitoPrestadoras = new BigDecimal(0);
		
		 if (debitosaTotalPersistido != null) {
			 wb =  generarReporteTotal(debitosaTotalPersistido, wb, sheet5 ,  tercerizadoras);
			 totalDebitoPrestadoras = generarTotal(debitosaTotalPersistido);
		 }else {
			 wb =  generarReporteTotal(debitosaTotal, wb, sheet5, tercerizadoras);
			 totalDebitoPrestadoras = generarTotal(debitosaTotal);
		 }
		 
		
		if (grabarDebitos && debitosaTotalPersistido == null) {
			//grabo debitos
			try {
				BusquedaDebitosTercerizadorasServiceUtil.grabarTotalesDebitos(debitosaTotal,user.getScreenName(),fechaHasta, tercerizadoras);
				
			
				for (DebitosLiquidacionesPendientes deb : debitosLiquidacionesPendientes) {
					BusquedaDebitosTercerizadorasServiceUtil.grabarLiquidacionesPendientesDebitos(deb,user.getScreenName(),fechaHasta, tercerizadoras);
				}
				for (DebitosHospitales deb : debitosHospitales) {
					BusquedaDebitosTercerizadorasServiceUtil.grabarHospitalesDebitos(deb,user.getScreenName(),fechaHasta, tercerizadoras);
				}
				for (DebitosaReintegros deb : debitosReintegros) {
					BusquedaDebitosTercerizadorasServiceUtil.grabarReintegrosDebitos(deb,user.getScreenName(),fechaHasta, tercerizadoras);
				}
				for (DebitosaPrestadores deb : debitosPrestadores) {
					BusquedaDebitosTercerizadorasServiceUtil.grabarPrestadoresDebitos(deb,user.getScreenName(),fechaHasta, tercerizadoras);
				}
	
				// Genera nota de debito para luego usar en una op que se va a generar 
				BusquedaDebitosTercerizadorasServiceUtil.grabarNDB(totalDebitoPrestadoras, user, fechaHasta,fechaDesde, tercerizadoras);
				
			} catch (SystemException e) {
				_log.error(e.getStackTrace());
			}
		}

		
	
		
	
		return  wb;

	
		
	}

	private static HSSFWorkbook generarReporteLiquidacionesPendientes(List<DebitosLiquidacionesPendientes> list , HSSFWorkbook wb  , HSSFSheet sheet ) {


		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);


		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("HOSPITALES"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("FACTURA"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("MONTO"));
		cell2H.setCellStyle(styleBold);
		

		//HSSFCell cell3H = rowHeader.createCell(3);
		//cell3H.setCellValue(new HSSFRichTextString("MONTO PRESTADORA RECLAMO"));
		//cell3H.setCellStyle(styleBold);
		
		
		//HSSFCell cell4H = rowHeader.createCell(3);
		//cell4H.setCellValue(new HSSFRichTextString("NUMERO DE OP"));
		//cell4H.setCellStyle(styleBold);

		if (list == null || list.isEmpty()) {
			return wb;
		}


		BigDecimal total = new BigDecimal("0");
		BigDecimal totalPrestadorReclamos = new BigDecimal("0");

		for (DebitosLiquidacionesPendientes debitosaLiq : list) {
			index++;
			total = total.add(crearHeaderLiquidacionesPendientes(sheet, index, debitosaLiq, styleBold,
					styleAll, styleDate, styleMoney));
			totalPrestadorReclamos = totalPrestadorReclamos.add(debitosaLiq.getCargoPrestadoraReclamo() != null ? debitosaLiq.getCargoPrestadoraReclamo() : BigDecimal.ZERO);;
		
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(1);
		//cell.setCellValue(new HSSFRichTextString("Totales"));
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(2);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);
		

		//HSSFCell cell2 = rowTotal.createCell(3);
		//cell2.setCellValue(totalPrestadorReclamos.doubleValue());
		//cell2.setCellStyle(styleAll);
		
		
		
		//HSSFCell cell2 = rowTotal.createCell(3);
		//cell2.setCellValue(totalPrestador.doubleValue());
		//cell2.setCellStyle(styleAll);


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

		return wb;
	}
	
	private static BigDecimal crearHeaderLiquidacionesPendientes(HSSFSheet sheet, int index,
			DebitosLiquidacionesPendientes debitosaAutogestion, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(debitosaAutogestion.getHospitalesAutogestion()));
		cell0.setCellStyle(styleAll);
		
		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(debitosaAutogestion.getFactura()));
		cell1.setCellStyle(styleAll);
		
		if(BigDecimal.ZERO.compareTo(debitosaAutogestion.getMonto()) == 0) {
			HSSFCell cell2 = rowHeader.createCell(2);
			cell2.setCellValue("");
			cell2.setCellStyle(styleAll);
		}else {			
			HSSFCell cell2 = rowHeader.createCell(2);
			cell2.setCellValue(debitosaAutogestion.getMonto().doubleValue());
			cell2.setCellStyle(styleAll);
		}
		
		//HSSFCell cell3 = rowHeader.createCell(3);
		//cell3.setCellValue(debitosaAutogestion.getCargoPrestadoraReclamo().doubleValue());
		//cell3.setCellStyle(styleAll);
		
		//HSSFCell cell4 = rowHeader.createCell(3);
		//cell4.setCellValue(debitosaAutogestion.getCargoPrestadora().doubleValue());
		//cell4.setCellStyle(styleAll);
		
		
		//HSSFCell cell5 = rowHeader.createCell(4);
		//HSSFCell cell5 = rowHeader.createCell(3);
		//cell5.setCellValue(new HSSFRichTextString(debitosaAutogestion.getOrdenPago()));
		//cell5.setCellStyle(styleAll);
		
		
		return  debitosaAutogestion.getMonto() != null ? debitosaAutogestion.getMonto() : BigDecimal.ZERO;
	}


	private static BigDecimal crearHeaderHospitales(HSSFSheet sheet, int index,
			DebitosHospitales debitosaHospitales, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(debitosaHospitales.getHospital()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(debitosaHospitales.getFactura()));
		cell1.setCellStyle(styleAll);

		if (BigDecimal.ZERO.compareTo(debitosaHospitales.getMonto()) == 0){
			HSSFCell cell2 = rowHeader.createCell(2);
			cell2.setCellValue("");
			cell2.setCellStyle(styleAll);
		}else {			
			HSSFCell cell2 = rowHeader.createCell(2);
			cell2.setCellValue(debitosaHospitales.getMonto().doubleValue());
			cell2.setCellStyle(styleAll);
		}
		
		
		
		//HSSFCell cell3 = rowHeader.createCell(3);
		//cell3.setCellValue(debitosaHospitales.getCargoPrestadora().doubleValue());
		//cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(3);
		cell4.setCellValue(new HSSFRichTextString(debitosaHospitales.getOrdenPago()));
		cell4.setCellStyle(styleAll);
		
		return  debitosaHospitales.getMonto() != null ? debitosaHospitales.getMonto()  : BigDecimal.ZERO;
	}

	

	private static HSSFWorkbook generarReporteHospitales(List<DebitosHospitales> list , HSSFWorkbook wb  , HSSFSheet sheet ) {


		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);


		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("HOSPITALES AUTOGESTION"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("FACTURA"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("MONTO"));
		cell2H.setCellStyle(styleBold);
		
		//HSSFCell cell3H = rowHeader.createCell(3);
		//cell3H.setCellValue(new HSSFRichTextString("MONTO PRESTADORA"));
		//cell3H.setCellStyle(styleBold);
		
		
		HSSFCell cell4H = rowHeader.createCell(3);
		cell4H.setCellValue(new HSSFRichTextString("NUMERO DE OP"));
		cell4H.setCellStyle(styleBold);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		BigDecimal total = new BigDecimal("0");
		BigDecimal totalCargoPrestadora = new BigDecimal("0");

		for (DebitosHospitales debitosHospitales : list) {
			index++;
			total = total.add(crearHeaderHospitales(sheet, index, debitosHospitales, styleBold,
					styleAll, styleDate, styleMoney));
			//totalCargoPrestadora = totalCargoPrestadora.add(debitosHospitales.getCargoPrestadora() != null ? debitosHospitales.getCargoPrestadora() : BigDecimal.ZERO);
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(1);
		//cell.setCellValue(new HSSFRichTextString("Totales"));
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(2);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);
		
		//HSSFCell cell2 = rowTotal.createCell(3);
		//cell2.setCellValue(totalCargoPrestadora.doubleValue());
		//cell2.setCellStyle(styleAll);

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
		return wb;
	}


	

	private static HSSFWorkbook generarReporteReintegros(List<DebitosaReintegros> list , HSSFWorkbook wb  , HSSFSheet sheet ) {
	

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);


		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);
		
		
		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("NUMERO DE REINTEGRO"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("APELLIDO"));
		cell1H.setCellStyle(styleBold);


		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("NOMBRE"));
		cell2H.setCellStyle(styleBold);
		
		
		
		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("DOCUMENTO"));
		cell3H.setCellStyle(styleBold);
		
		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("SECCIONAL"));
		cell4H.setCellStyle(styleBold);


		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("DESCRIPCIÓN"));
		cell5H.setCellStyle(styleBold);
		
		
		HSSFCell cell6H = rowHeader.createCell(6);
		cell6H.setCellValue(new HSSFRichTextString("MONTO"));
		cell6H.setCellStyle(styleBold);
		
		//HSSFCell cell4H = rowHeader.createCell(4);
		//cell4H.setCellValue(new HSSFRichTextString("MONTO PRESTADORA"));
		//Scell4H.setCellStyle(styleBold);
		
		HSSFCell cell7H = rowHeader.createCell(7);
		cell7H.setCellValue(new HSSFRichTextString("N. OP"));
		cell7H.setCellStyle(styleBold);


		HSSFCell cell8H = rowHeader.createCell(8);
		cell8H.setCellValue(new HSSFRichTextString("FECHA OP"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(9);
		cell9H.setCellValue(new HSSFRichTextString("RECLAMOS"));
		cell9H.setCellStyle(styleBold);
		
		if (list == null || list.isEmpty()) {
			return wb;
		}

		BigDecimal total = new BigDecimal("0");

		for (DebitosaReintegros debitosaReintegros : list) {
			index++;
			total = total.add(crearHeaderDebitosaReintegros(sheet, index, debitosaReintegros, styleBold,
					styleAll, styleDate, styleMoney));
			//totalCargoPrestadora =  totalCargoPrestadora.add(debitosaReintegros.getCargoPrestadora() != null ? debitosaReintegros.getCargoPrestadora() : BigDecimal.ZERO);
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(5);
		//cell.setCellValue(new HSSFRichTextString("Totales"));
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(6);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);
		
	//	HSSFCell cell2 = rowTotal.createCell(4);
	//	cell2.setCellValue(totalCargoPrestadora.doubleValue());
	//	cell2.setCellStyle(styleAll);

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
		return wb;
	}

	private static BigDecimal crearHeaderDebitosaReintegros(HSSFSheet sheet, int index,
			DebitosaReintegros debitosaReintegros, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		HSSFRow rowHeader = sheet.createRow(index);

		
		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(debitosaReintegros.getNumReintegroToString()));
		cell0.setCellStyle(styleAll);

		
		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(debitosaReintegros.getApellido()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(debitosaReintegros.getNombre()));
		cell2.setCellStyle(styleAll);

		
		
		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(debitosaReintegros.getDocumento()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(debitosaReintegros.getSeccional()));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(debitosaReintegros.getDescripcion()));
		cell5.setCellStyle(styleAll);

		if (debitosaReintegros.getImporteTotal().compareTo(BigDecimal.ZERO) == 0) {
			HSSFCell cell6 = rowHeader.createCell(6);
			cell6.setCellValue("");
			cell6.setCellStyle(styleAll);			
		}else {
			HSSFCell cell6 = rowHeader.createCell(6);
			cell6.setCellValue(debitosaReintegros.getImporteTotal().doubleValue());
			cell6.setCellStyle(styleAll);	
		}
		
		//HSSFCell cell4 = rowHeader.createCell(4);
		//cell4.setCellValue(debitosaReintegros.getCargoPrestadora().doubleValue());
		//cell4.setCellStyle(styleAll);	

		HSSFCell cell7 = rowHeader.createCell(7);
		cell7.setCellValue(new HSSFRichTextString(debitosaReintegros.getNumeroOP()));
		cell7.setCellStyle(styleAll);

		
		HSSFCell cell8 = rowHeader.createCell(8);
		cell8.setCellValue(new HSSFRichTextString(debitosaReintegros.getFechaOP().toString()));
		cell8.setCellStyle(styleAll);
		
		
		HSSFCell cell9 = rowHeader.createCell(9);
		cell9.setCellValue(debitosaReintegros.getReclamoPrestacional());
		cell9.setCellStyle(styleAll);
		
		return  debitosaReintegros.getImporteTotal() != null ? debitosaReintegros.getImporteTotal() : BigDecimal.ZERO;
	}
	
	
	
	private static HSSFWorkbook generarReporterestadores(List<DebitosaPrestadores> list , HSSFWorkbook wb  , HSSFSheet sheet ) {
	

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

	
		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("PRESTADOR"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("FACTURA"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("MONTO"));
		cell2H.setCellStyle(styleBold);
		
		//HSSFCell cell3H = rowHeader.createCell(3);
		//cell3H.setCellValue(new HSSFRichTextString("MONTO PRESTADORA"));
		//cell3H.setCellStyle(styleBold);
		
		HSSFCell cell4H = rowHeader.createCell(3);
		cell4H.setCellValue(new HSSFRichTextString("NUMERO DE OP"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell5H = rowHeader.createCell(4);
		cell5H.setCellValue(new HSSFRichTextString("RECLAMO"));
		cell5H.setCellStyle(styleBold);
		

		
		if (list == null || list.isEmpty()) {
			return wb;
		}


		BigDecimal total = new BigDecimal("0");
		BigDecimal totalCargoPrestadora = new BigDecimal("0");

		for (DebitosaPrestadores debitosaPrestadores : list) {
			index++;
			total = total.add(crearHeaderDebitosaPrestadores(sheet, index, debitosaPrestadores, styleBold,
					styleAll, styleDate, styleMoney));
			//totalCargoPrestadora = totalCargoPrestadora.add(debitosaPrestadores.getCargoPrestadora() != null ? debitosaPrestadores.getCargoPrestadora() : BigDecimal.ZERO);
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(1);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(2);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);
		
		//HSSFCell cell2 = rowTotal.createCell(3);
		//cell2.setCellValue(totalCargoPrestadora.doubleValue());
		//cell2.setCellStyle(styleAll);

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
		return wb;
	}

	private static BigDecimal crearHeaderDebitosaPrestadores(HSSFSheet sheet, int index,
			DebitosaPrestadores debitosaPrestadores, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(debitosaPrestadores.getPrestador()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(debitosaPrestadores.getFactura()));
		cell1.setCellStyle(styleAll);

		if (debitosaPrestadores.getMonto().compareTo(BigDecimal.ZERO) ==0 ) {
			HSSFCell cell2 = rowHeader.createCell(2);
			cell2.setCellValue("");
			cell2.setCellStyle(styleAll);
		}else {			
			HSSFCell cell2 = rowHeader.createCell(2);
			cell2.setCellValue(debitosaPrestadores.getMonto().doubleValue());
			cell2.setCellStyle(styleAll);
		}
		
		//HSSFCell cell3 = rowHeader.createCell(3);
		//cell3.setCellValue(debitosaPrestadores.getCargoPrestadora().doubleValue());
		//cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(3);
		cell4.setCellValue(new HSSFRichTextString(debitosaPrestadores.getOrdenPago()));
		cell4.setCellStyle(styleAll);

		
		HSSFCell cell5 = rowHeader.createCell(4);
		if(debitosaPrestadores.getReclamoPrestacional()==null || debitosaPrestadores.getReclamoPrestacional()==0) {
		   cell5.setCellValue(new HSSFRichTextString(""));
		}else {
		   //cell5.setCellValue(debitosaPrestadores.getReclamoPrestacional());
		   cell5.setCellValue(new HSSFRichTextString(debitosaPrestadores.getReclamosPrestacionales()));	
		}
		cell5.setCellStyle(styleAll);
		
		return  debitosaPrestadores.getMonto() != null ? debitosaPrestadores.getMonto() : BigDecimal.ZERO;
	}


	
	private static BigDecimal generarTotal(DebitosaTotal deb) {
		BigDecimal total = new BigDecimal(0);

		//acumulador
		total = total.add(deb.getMontoLiquidacionPendiente() != null ? deb.getMontoLiquidacionPendiente() : new BigDecimal(0) );
		total = total.add(deb.getMontoHospitales()  != null ? deb.getMontoHospitales()  : new BigDecimal(0)  );
		total = total.add(deb.getMontoReintegros()  != null ?  deb.getMontoReintegros() : new BigDecimal(0) );
		total = total.add(deb.getMontoPrestadores() != null ?  deb.getMontoPrestadores() : new BigDecimal(0));
		
		return total;
		
	}

	
	
	
	private static HSSFWorkbook generarReporteTotal(DebitosaTotal deb , HSSFWorkbook wb  , HSSFSheet sheet , String tercerizadoras) {


		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (deb == null ) {
			return wb;
		}

		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("HOSPITALES"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("MONTO"));
		cell1H.setCellStyle(styleBold);

		/*if ("OMI".equalsIgnoreCase(tercerizadoras)) {
			HSSFCell cell2H = rowHeader.createCell(2);
			cell2H.setCellValue(new HSSFRichTextString("MONTO OMINT"));
			cell2H.setCellStyle(styleBold);			
		}else if ("MPS".equalsIgnoreCase(tercerizadoras)){
			HSSFCell cell2H = rowHeader.createCell(2);
			cell2H.setCellValue(new HSSFRichTextString("MOLINEROS POR PS"));
			cell2H.setCellStyle(styleBold);			
		}else if ("MEN".equalsIgnoreCase(tercerizadoras)){
			HSSFCell cell2H = rowHeader.createCell(2);
			cell2H.setCellValue(new HSSFRichTextString("MOLINEROS POR ENSALUD"));
			cell2H.setCellStyle(styleBold);		
		}	*/


		BigDecimal total = new BigDecimal(0);
		BigDecimal totalPrestador = new BigDecimal(0);

		index++;
		crearHeaderTotalLiquidacionesPendientes(sheet, index, deb , styleBold,styleAll, styleDate, styleMoney);
		index++;
		crearHeaderTotalReintegros(sheet, index, deb, styleBold,styleAll, styleDate, styleMoney);
		index++;
		crearHeaderTotalPrestadores(sheet, index, deb, styleBold,styleAll, styleDate, styleMoney);
		index++;
		crearHeaderTotalAutogestion(sheet, index, deb, styleBold,styleAll, styleDate, styleMoney);

		index++;
		index++;
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(0);
		//cell.setCellValue(new HSSFRichTextString("Totales"));
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(1);
		
		//acumulador
		total = total.add(deb.getMontoLiquidacionPendiente() != null ? deb.getMontoLiquidacionPendiente() : new BigDecimal(0) );
		total = total.add(deb.getMontoHospitales()  != null ? deb.getMontoHospitales()  : new BigDecimal(0)  );
		total = total.add(deb.getMontoReintegros()  != null ?  deb.getMontoReintegros() : new BigDecimal(0) );
		total = total.add(deb.getMontoPrestadores() != null ?  deb.getMontoPrestadores() : new BigDecimal(0));
		
		totalPrestador = totalPrestador.add(deb.getMontoLiquidacionPendienteDebito());
		totalPrestador = totalPrestador.add(deb.getMontoHospitaleDebito());
		totalPrestador = totalPrestador.add(deb.getMontoReintegroDebito()); 
		totalPrestador = totalPrestador.add(deb.getMontoPrestadoreDebito());
		
		
		
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);
		
		//cell1 = rowTotal.createCell(2);
		
		//cell1.setCellValue(totalPrestador.doubleValue());
		//cell1.setCellStyle(styleAll);

		

		index++;
		sheet.createRow(index);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		return wb;
	}
	
	private static void crearHeaderTotalLiquidacionesPendientes(HSSFSheet sheet, int index,
			DebitosaTotal debitoTotal, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("HOSPITALES"));
		cell0.setCellStyle(styleAll);
		
		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(debitoTotal.getMontoHospitales() != null ? debitoTotal.getMontoHospitales().doubleValue() : 0);
		cell1.setCellStyle(styleAll);
		
		//HSSFCell cell2 = rowHeader.createCell(2);
		//cell2.setCellValue(debitoTotal.getMontoHospitaleDebito() != null ? debitoTotal.getMontoHospitaleDebito().doubleValue() : 0);
		//cell2.setCellStyle(styleAll);
		



		
	}
	
	private static void crearHeaderTotalReintegros(HSSFSheet sheet, int index,
			DebitosaTotal debitoTotal, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		HSSFRow rowHeader1 = sheet.createRow(index);

		
		HSSFCell cell3 = rowHeader1.createCell(0);
		cell3.setCellValue(new HSSFRichTextString("REINTEGROS"));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader1.createCell(1);
		cell4.setCellValue(debitoTotal.getMontoReintegros() != null ? debitoTotal.getMontoReintegros().doubleValue() : 0);
		cell4.setCellStyle(styleAll);
		
		//HSSFCell cell5 = rowHeader1.createCell(2);
		//cell5.setCellValue(debitoTotal.getMontoReintegroDebito() != null ? debitoTotal.getMontoReintegroDebito().doubleValue() : 0);
		//cell5.setCellStyle(styleAll);
		
	}	
	
	
	private static void crearHeaderTotalPrestadores(HSSFSheet sheet, int index,
			DebitosaTotal debitoTotal, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		HSSFRow rowHeader2 = sheet.createRow(index);

		
		HSSFCell cell6 = rowHeader2.createCell(0);
		cell6.setCellValue(new HSSFRichTextString("PRESTADORES"));
		cell6.setCellStyle(styleAll);
		
		HSSFCell cell7 = rowHeader2.createCell(1);
		cell7.setCellValue(debitoTotal.getMontoPrestadores() != null ? debitoTotal.getMontoPrestadores().doubleValue() : 0);
		cell7.setCellStyle(styleAll);
		
		//HSSFCell cell8 = rowHeader2.createCell(2);
		//cell8.setCellValue(debitoTotal.getMontoPrestadoreDebito() != null ? debitoTotal.getMontoPrestadoreDebito().doubleValue() : 0);
		//cell8.setCellStyle(styleAll);
		
	}	
	
	private static void crearHeaderTotalAutogestion(HSSFSheet sheet, int index,
			DebitosaTotal debitoTotal, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		HSSFRow rowHeader3 = sheet.createRow(index);

		
		HSSFCell cell9 = rowHeader3.createCell(0);
		cell9.setCellValue(new HSSFRichTextString("LIQUIDACIONES PENDIENTES"));
		cell9.setCellStyle(styleAll);
		
		HSSFCell cell10 = rowHeader3.createCell(1);
		cell10.setCellValue(debitoTotal.getMontoLiquidacionPendiente() != null ? debitoTotal.getMontoLiquidacionPendiente().doubleValue() : 0);
		cell10.setCellStyle(styleAll);
		
		//HSSFCell cell11 = rowHeader3.createCell(2);
		//cell11.setCellValue(debitoTotal.getMontoAutogestionDebito() != null ? debitoTotal.getMontoAutogestionDebito().doubleValue() : 0);
		//cell11.setCellStyle(styleAll);

		
	}	
	
	
	
	
}