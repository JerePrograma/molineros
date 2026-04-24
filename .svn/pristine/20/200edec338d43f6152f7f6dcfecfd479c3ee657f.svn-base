package ar.com.ospim.liquidaciones.reportes.action;

import java.math.BigDecimal;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.FichaConsumo;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.BusquedaLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteLiquidacionesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteLiquidacionesExcel.class);

	
	private static final String FILE_SEPARATOR =  System.getProperty("file.separator");
	private static final String TMPDIR = System.getProperty("java.io.tmpdir");
	
	public static HSSFWorkbook generaReporteLiquidacionesExcel(
			HttpServletRequest renderRequest, HttpServletResponse res) {

		String entidad = ParamUtil.getString(renderRequest, "entidad", null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,"periodoDesdeMesAnio");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodos.parse(Integer.parseInt(periodoDesdeMesAnio.substring(0, 1))
					+ 1 + "/" + periodoDesdeMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoDesde = null;
		}
		String periodoHastaMesAnio = ParamUtil.getString(renderRequest,"periodoHastaMesAnio");
		Date periodoHasta = null;
		try {
			periodoHasta = formatoDePeriodos.parse(Integer.parseInt(periodoHastaMesAnio.substring(0, 1))
					+ 1 + "/" + periodoHastaMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoHasta = null;
		}

		int numero = ParamUtil.getInteger(renderRequest, "numero", 0);

		String tipo_liquidacion = ParamUtil.getString(renderRequest,"tipo_liquidacion", 
				WebKeysLiquidaciones.LIQUIDACION_PRE);

		int codPrest = ParamUtil.getInteger(renderRequest, "codPrest", 0);
		int id_prestador = ParamUtil.getInteger(renderRequest, "id_prestador",0);
		String cuit = ParamUtil.getString(renderRequest, "cuit", null);
		String prestador = ParamUtil.getString(renderRequest, "prestador", null);

		int estado = ParamUtil.getInteger(renderRequest, "estado", 0);
		int id_orden_compra = ParamUtil.getInteger(renderRequest, "nro_oc", 0);
		
		BusquedaLiquidacionServiceUtil.getInstance();

		String tipo_compro = ParamUtil.getString(renderRequest,
				"comprobante_tipo", null);
		String letra_compro = ParamUtil.getString(renderRequest,
				"comprobante_letra", null);
		int sucu = ParamUtil.getInteger(renderRequest, "sucu", 0);
		String nro_compro = ParamUtil.getString(renderRequest,
				"comprobante_nro", null);
		
		Integer sector = ParamUtil.getInteger(renderRequest, "sector", -1);

		List<Liquidacion> busqueda = new ArrayList<Liquidacion>();
		try {
			busqueda = BusquedaLiquidacionServiceUtil
					.getBusquedaLiquidaciones(entidad, fechaDesde, fechaHasta,
							periodoDesde, periodoHasta, codPrest, id_prestador,
							cuit, prestador, numero, tipo_compro, letra_compro,
							sucu, nro_compro, estado, id_orden_compra,sector);
		} catch (Exception e) {
			e.printStackTrace();
		}

		renderRequest.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION);
		renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION,busqueda);

		return generarReporte(busqueda);
	}

	private static HSSFWorkbook generarReporte(List<Liquidacion> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Hoja 1");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("Número"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("Cuit Prestad."));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("Desc. Prestad."));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("Importe"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("Fecha"));
		cell4H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("Comprobante"));
		cell5H.setCellStyle(styleBold);

		HSSFCell cell6H = rowHeader.createCell(6);
		cell6H.setCellValue(new HSSFRichTextString("Fecha Emisión"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell7H = rowHeader.createCell(7);
		cell7H.setCellValue(new HSSFRichTextString("N° Orden Compra"));
		cell7H.setCellStyle(styleBold);

		HSSFCell cell71H = rowHeader.createCell(8);
		cell71H.setCellValue(new HSSFRichTextString("Alta Usuario"));
		cell71H.setCellStyle(styleBold);
		
		
		HSSFCell cell72H = rowHeader.createCell(9);
		cell72H.setCellValue(new HSSFRichTextString("Alta Fecha"));
		cell72H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(10);
		cell8H.setCellValue(new HSSFRichTextString("Observaciones"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(11);
		cell9H.setCellValue(new HSSFRichTextString("Imágenes"));
		cell9H.setCellStyle(styleBold);

		BigDecimal total = new BigDecimal("0");
		for (Liquidacion liquidacion : list) {
			if(liquidacion.getBaja_fecha()==null) {
			  index++;
			  total = total.add(crearHeader(sheet, index, liquidacion, styleBold,
					        styleAll, styleDate, styleMoney));
			}
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(2);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(3);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);

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

	private static BigDecimal crearHeader(HSSFSheet sheet, int index,
			Liquidacion liquidacion, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		
		List<DLFileEntryImpl>imagenes = EditarLiquidacionServiceUtil.getImagenes(liquidacion);

		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(liquidacion.getId_liquidacion());
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(liquidacion
				.getPrestador_lugar_atencion().getPrestador().getCuit()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(liquidacion
						.getPrestador_lugar_atencion().getPrestador()
						.getDescripcion()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(liquidacion.getImporte() != null ? new HSSFRichTextString(
						liquidacion.getImporte().toString())
						: new HSSFRichTextString("0"));
		cell3.setCellStyle(styleMoney);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(liquidacion.getFecha()
				.toString()));
		cell4.setCellStyle(styleDate);

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(liquidacion
				.getCompro_a_debitar_tipo()
				+ "-"
				+ liquidacion.getCompro_a_debitar_letra()
				+ "-"
				+ liquidacion.getCompro_a_debitar_numero()
				+ "-"
				+ liquidacion.getSucu()));
		cell5.setCellStyle(styleAll);

		HSSFCell cell6 = rowHeader.createCell(6);
		cell6.setCellValue(new HSSFRichTextString(liquidacion
				.getFecha_emitido() != null ? liquidacion.getFecha_emitido()
				.toString() : ""));
		cell6.setCellStyle(styleDate);

		HSSFCell cell7 = rowHeader.createCell(7);
		cell7.setCellValue(new HSSFRichTextString(liquidacion
				.getIdOC() != 0 ? String.valueOf(liquidacion.getIdOC()) : ""));
		cell7.setCellStyle(styleAll);
		
		HSSFCell cell71 = rowHeader.createCell(8);
		cell71.setCellValue(new HSSFRichTextString(liquidacion
				.getAlta_usr() != null ? String.valueOf(liquidacion.getAlta_usr()) : ""));
		cell71.setCellStyle(styleAll);
		
		HSSFCell cell72 = rowHeader.createCell(9);
		cell72.setCellValue(new HSSFRichTextString(liquidacion.getAlta_fecha() != null ? String.valueOf(liquidacion.getAlta_fecha().toString()) : ""));
		cell72.setCellStyle(styleAll);
		
		HSSFCell cell8 = rowHeader.createCell(10);
		cell8.setCellValue(new HSSFRichTextString(liquidacion
				.getObservaciones() != null ? liquidacion.getObservaciones()
				: ""));
		cell8.setCellStyle(styleAll);
		
		
		HSSFCell cell9 = rowHeader.createCell(11);
		cell9.setCellValue(new HSSFRichTextString(imagenes!=null && imagenes.size()>0  ? "SI"
				: ""));
		cell9.setCellStyle(styleAll);

		return liquidacion.getImporte() != null ? liquidacion.getImporte()
				: BigDecimal.ZERO;
	}

	public static SXSSFWorkbook generaFichaDeConsumo(
			HttpServletRequest renderRequest, HttpServletResponse res) {

		String entidad = ParamUtil.getString(renderRequest, "entidad", null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");

		String cuit = ParamUtil.getString(renderRequest, "cuit", "");
		String sucu = ParamUtil.getString(renderRequest, "sucu", "");

		String codPrestaci = ParamUtil.getString(renderRequest, "codPrest",
				null);

		String presta = ParamUtil.getString(renderRequest, "presta", "0");
		String ortop = ParamUtil.getString(renderRequest, "ortop", "0");
		String protesis = ParamUtil.getString(renderRequest, "protesis", "0");
		String odontogeneral = ParamUtil.getString(renderRequest, "odontogral","0");
		String liquidaciones = ParamUtil.getString(renderRequest,"liquidaciones", "0");
		String preAutorizaciones = ParamUtil.getString(renderRequest,"pre_autoriz", "0");
		String rtaPrevencion = ParamUtil.getString(renderRequest,"rta_prevencion", "0");
		
		String discapacidad = ParamUtil.getString(renderRequest,"discapacidad", "0");
		
		String farmacia = ParamUtil.getString(renderRequest,"farmacia", "0");
		
		String liqFarmacia=ParamUtil.getString(renderRequest, "liq_farmacia");

		List<FichaConsumo> fichas = new ArrayList<FichaConsumo>();
		try {
			fichas = BusquedaLiquidacionServiceUtil.getConsumoAfiliado(entidad,
					fechaDesde, fechaHasta, codPrestaci, nroAfi, inte,
					cuil_titular, cuit, sucu, presta, ortop, protesis,
					odontogeneral, liquidaciones, "0", // discapacidad
					farmacia, liqFarmacia, preAutorizaciones, rtaPrevencion);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte de ficha de consumos", e);
			return null;
		}
		return generarReporteFichaConsumo(fichas);
	}

	
	
	public static HSSFWorkbook generaReporteDiscapacidad(
			HttpServletRequest renderRequest, HttpServletResponse res) {

		String entidad = ParamUtil.getString(renderRequest, "entidad", null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			
		String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
				"periodoDesdeMesAnio");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeMesAnio.substring(0, periodoDesdeMesAnio.indexOf("_")))
					+ 1 + "/" + periodoDesdeMesAnio.substring(periodoDesdeMesAnio.indexOf("_")+1, periodoDesdeMesAnio.length()));
		} catch (Exception e) {
			periodoDesde = null;
		}
		String periodoHastaMesAnio = ParamUtil.getString(renderRequest,
				"periodoHastaMesAnio");
		Date periodoHasta = null;
		try {
			periodoHasta = formatoDePeriodos.parse(Integer
					.parseInt(periodoHastaMesAnio.substring(0, periodoHastaMesAnio.indexOf("_")))
					+ 1 + "/" + periodoHastaMesAnio.substring(periodoHastaMesAnio.indexOf("_")+1, periodoHastaMesAnio.length()));
		} catch (Exception e) {
			periodoHasta = null;
		}
		
		int estado = ParamUtil.getInteger(renderRequest, "estado", 0);

		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
		String cuil_titular = ParamUtil
				.getString(renderRequest, "cuil_titular");

		String cuit = ParamUtil.getString(renderRequest, "cuit", "");
		String sucu = ParamUtil.getString(renderRequest, "sucu", "");

		String codPrestaci = ParamUtil.getString(renderRequest, "codPrestaci",
				null);

		String presta = ParamUtil.getString(renderRequest, "presta", "0");
		String liquidaciones = ParamUtil.getString(renderRequest,
				"liquidaciones", "0");

		String diagnostico = ParamUtil.getString(renderRequest, "diagnostico",
				null);
		String ciex = ParamUtil.getString(renderRequest, "ciex", null);

		List<FichaConsumo> fichas = new ArrayList<FichaConsumo>();
		try {
			fichas = BusquedaLiquidacionServiceUtil.getReporteDiscapacidad(
					entidad, fechaDesde, fechaHasta, periodoDesde,
					periodoHasta, codPrestaci, estado, nroAfi, inte,
					cuil_titular, cuit, sucu, presta, liquidaciones,
					diagnostico, ciex);

		} catch (Exception e) {
			_log.error("Error al generar reporte de discapacidad", e);
			return null;
		}
		return generarReporteDiscapacidad(fichas);

	}

	private static BigDecimal crearHeader(HSSFSheet sheet, int index,
			ReporteOrdenPagoReintegros repo, Reintegro reintegro,
			HSSFCellStyle styleBold, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {

		BigDecimal importe = BigDecimal.ZERO;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(reintegro.getFecha());
		cell0.setCellStyle(styleDate);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(reintegro.getAfiliado()
				.getSeccional().getDescripcion()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		if (!reintegro.getTipo_reintegro().equals(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cell2.setCellValue(reintegro.getId_reintegro_user());
		} else {
			cell2.setCellValue(reintegro.getDetalleCuota().get(0)
					.getId_reintegro_user());
		}
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(repo.getAfiliado()
				.getCuil_titular()
				+ ", " + repo.getAfiliado().getInte()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(4);
		if (!reintegro.getTipo_reintegro().equals(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cell4.setCellValue(reintegro.getImporteTotal().doubleValue());
			cell4.setCellStyle(styleAll);
			importe = reintegro.getImporteTotal();
		} else {
			cell4.setCellValue((reintegro.getDetalleCuota() != null
					&& reintegro.getDetalleCuota().get(0) != null && reintegro
					.getDetalleCuota().get(0).getImporte() != null) ? reintegro
					.getDetalleCuota().get(0).getImporte().doubleValue()
					: new Double(0).doubleValue());
			cell4.setCellStyle(styleAll);
			importe = (reintegro.getDetalleCuota() != null
					&& reintegro.getDetalleCuota().get(0) != null && reintegro
					.getDetalleCuota().get(0).getImporte() != null) ? reintegro
					.getDetalleCuota().get(0).getImporte() : BigDecimal.ZERO;
		}
		return importe;
	}

	private static SXSSFWorkbook generarReporteFichaConsumo(
			List<FichaConsumo> list) {


       // XSSFWorkbook wb = new XSSFWorkbook ();
		
	    SXSSFWorkbook wb = new SXSSFWorkbook(100);	//chache en disck
		
        
        
        Sheet s = wb.createSheet("Ficha");
        //HSSFSheet sheet = wb.createSheet("Ficha");
		
		
        XSSFPrintSetup ps = (XSSFPrintSetup)s.getPrintSetup();
		s.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		CellStyle styleAll = getStyleAllWbs(wb);
		CellStyle styleBold = getStyleBoldWbs(wb);
		CellStyle styleDate = getStyleDateWbs(wb);
		CellStyle styleMoney = getStyleMoneyWbs(wb);
		CellStyle styleNumber= getStyleNumberWbs(wb);

		if (list == null || list.isEmpty()) {
			
			int index = 0;
			Row row = s.createRow(index++);
			Cell cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString("Reporte de Ficha de Consumos"));
			cell.setCellStyle(styleAll);

			s.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			Row row1 = s.createRow(index++);
			Cell cell1 = row1.createCell(0);
			StringBuffer aux = new StringBuffer(" No se encontraron resultados ");

			cell1.setCellValue(new HSSFRichTextString(aux.toString()));
			cell1.setCellStyle(styleBold);

			s.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
			return wb;
		}

		int index = 0;
		int col = -1;
		Row rowHeader = s.createRow(index);

		Cell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Detalle"));
		cell0H.setCellStyle(styleBold);

		Cell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Número"));
		cell1H.setCellStyle(styleBold);

		Cell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Apellido"));
		cell3H.setCellStyle(styleBold);

		Cell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);

		Cell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Documento"));
		cell5H.setCellStyle(styleBold);

		Cell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell28H.setCellStyle(styleBold);

		Cell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Seccional"));
		cell6H.setCellStyle(styleBold);
		
		Cell cell6Hb = rowHeader.createCell(++col);
		cell6Hb.setCellValue(new HSSFRichTextString("Provincia Secc."));
		cell6Hb.setCellStyle(styleBold);

		Cell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Fecha Prestación"));
		cell2H.setCellStyle(styleBold);

		Cell cell2primaH = rowHeader.createCell(++col);
		cell2primaH.setCellValue(new HSSFRichTextString("Periodo"));
		cell2primaH.setCellStyle(styleBold);

		Cell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Código"));
		cell9H.setCellStyle(styleBold);

		Cell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Descripción"));
		cell10H.setCellStyle(styleBold);

		Cell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell18H.setCellStyle(styleBold);
						
		Cell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Importe"));
		cell19H.setCellStyle(styleBold);

		Cell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Importe Total"));
		cell15H.setCellStyle(styleBold);
			
		Cell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Cuit"));
		cell7H.setCellStyle(styleBold);

		Cell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell8H.setCellStyle(styleBold);

		
		
		

		Cell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Localidad Prestador"));
		cell24H.setCellStyle(styleBold);

		Cell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("Provincia Prestador"));
		cell25H.setCellStyle(styleBold);

		Cell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("Debitado"));
		cell26H.setCellStyle(styleBold);
		
		Cell cell26Hb = rowHeader.createCell(++col);
		cell26Hb.setCellValue(new HSSFRichTextString("Tercerizado"));
		cell26Hb.setCellStyle(styleBold);

		Cell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("N. OP"));
		cell27H.setCellStyle(styleBold);
		
		Cell cell271H = rowHeader.createCell(++col);
		cell271H.setCellValue(new HSSFRichTextString("Fecha OP"));
		cell271H.setCellStyle(styleBold);


		Cell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Pieza"));
		cell13H.setCellStyle(styleBold);

		Cell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Cara"));
		cell14H.setCellStyle(styleBold);

		Cell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Nro. Cuota"));
		cell16H.setCellStyle(styleBold);

		Cell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Porc. Cuota"));
		cell17H.setCellStyle(styleBold);

		Cell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Laboratorio"));
		cell12H.setCellStyle(styleBold);

		Cell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Presentación"));
		cell11H.setCellStyle(styleBold);

		Cell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("OSPIM"));
		cell20H.setCellStyle(styleBold);

		Cell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("AMTIMA"));
		cell21H.setCellStyle(styleBold);
		
		Cell cell21HH = rowHeader.createCell(++col);
		cell21HH.setCellValue(new HSSFRichTextString("UOMA"));
		cell21HH.setCellStyle(styleBold);

		Cell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Receta"));
		cell22H.setCellStyle(styleBold);

		Cell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Porcentaje"));
		cell23H.setCellStyle(styleBold);

		Cell cell024H = rowHeader.createCell(++col);
		cell024H.setCellValue(new HSSFRichTextString("Diagnóstico"));
		cell024H.setCellStyle(styleBold);
		
		Cell cell025H = rowHeader.createCell(++col);
		cell025H.setCellValue(new HSSFRichTextString("Plan"));
		cell025H.setCellStyle(styleBold);
		
		BigDecimal total = new BigDecimal("0");
		for (FichaConsumo ficha : list) {
			index++;
			total = total.add(crearHeaderFicha(s, index, ficha, styleBold,
					styleAll, styleDate, styleMoney, styleNumber));
		}
		index++;
		Row rowTotal = s.createRow(index);

		Cell cell = rowTotal.createCell(2);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		Cell cell1 = rowTotal.createCell(3);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);

		index++;
		s.createRow(index);
		for(int j=0;j<30;j++){
			try {
			    s.autoSizeColumn((short) j);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}	
	
	  
		return wb;
	}

	private static void WorkbookFactory() {
		// TODO Auto-generated method stub
		
	}

	private static HSSFWorkbook generarReporteDiscapacidad(
			List<FichaConsumo> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Reporte Discapacidad");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		int index = 0;
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Detalle"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Número"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Apellido"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell3PH = rowHeader.createCell(++col);
		cell3PH.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell3PH.setCellStyle(styleBold);

		HSSFCell cell4PH = rowHeader.createCell(++col);
		cell4PH.setCellValue(new HSSFRichTextString("Inte"));
		cell4PH.setCellStyle(styleBold);

		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Seccional"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell2PH = rowHeader.createCell(++col);
		cell2PH.setCellValue(new HSSFRichTextString("Diagnóstico"));
		cell2PH.setCellStyle(styleBold);

		HSSFCell cell2PPH = rowHeader.createCell(++col);
		cell2PPH.setCellValue(new HSSFRichTextString("Cie X"));
		cell2PPH.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Fecha"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell2primaH = rowHeader.createCell(++col);
		cell2primaH.setCellValue(new HSSFRichTextString("Periodo"));
		cell2primaH.setCellStyle(styleBold);
				
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Código"));
		cell9H.setCellStyle(styleBold);
			
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Descripción"));
		cell10H.setCellStyle(styleBold);

		HSSFCell cell10PH = rowHeader.createCell(++col);
		cell10PH.setCellValue(new HSSFRichTextString("Fecha Prestacion"));
		cell10PH.setCellStyle(styleBold);
				
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell18H.setCellStyle(styleBold);
				
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Importe"));
		cell19H.setCellStyle(styleBold);

		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Importe Total"));
		cell15H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Cuit"));
		cell7H.setCellStyle(styleBold);

		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell8H.setCellStyle(styleBold);
						
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Fecha Emitido"));
		cell24H.setCellStyle(styleBold);

		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("importe"));
		cell25H.setCellStyle(styleBold);
			
		HSSFCell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("Comprobante"));
		cell26H.setCellStyle(styleBold);
				
		HSSFCell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("Tercerizado"));
		cell27H.setCellStyle(styleBold);

		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("OP Número"));
		cell13H.setCellStyle(styleBold);

		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("OP Fecha"));
		cell14H.setCellStyle(styleBold);				
		
		BigDecimal total = new BigDecimal("0");
		for (FichaConsumo ficha : list) {
			index++;
			total = total.add(crearHeaderDiscapacidad(sheet, index, ficha, styleBold,
					styleAll, styleDate, styleMoney));
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(15);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(16);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);

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

		return wb;
	}
	
	private static BigDecimal crearHeaderFicha(Sheet sheet, int index,
			FichaConsumo ficha, CellStyle styleBold,
			CellStyle styleAll, CellStyle styleDate,
			CellStyle styleMoney, CellStyle styleNumber) {

		Row rowHeader = sheet.createRow(index);

		int col = -1;
		
		

		Cell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(ficha.getTipo_consumo()));
		cell0.setCellStyle(styleAll);

		Cell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(ficha.getId_liquidacion());
		cell1.setCellStyle(styleAll);

		Cell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(ficha.getApellido()));
		cell3.setCellStyle(styleAll);

		Cell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(ficha.getNombre()));
		cell4.setCellStyle(styleAll);

		Cell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(ficha.getDocu_numero()));
		cell5.setCellStyle(styleAll);

		Cell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString(
				ficha.getDiscapacitado() != null
						&& ficha.getDiscapacitado().equals("1") ? "Sí" : "No"));
		cell28H.setCellStyle(styleBold);

		Cell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(ficha.getSecciona()));
		cell6.setCellStyle(styleAll);
		
		Cell cell6b = rowHeader.createCell(++col);
		cell6b.setCellValue(new HSSFRichTextString(ficha.getProvinciaSecc()));
		cell6b.setCellStyle(styleAll);

		Cell cell2 = rowHeader.createCell(++col);
		if(ficha.getFecha_prestacion()!=null) {
			cell2.setCellValue(ficha.getFecha_prestacion()!=null);
			cell2.setCellStyle(styleDate);
		} else {
			cell2.setCellValue("");
			cell2.setCellStyle(styleDate);
		}	

		Cell cellprima2 = rowHeader.createCell(++col);
		cellprima2.setCellValue(new HSSFRichTextString(
				ficha.getPeriodo() != null ? DateUtils.format(ficha
						.getPeriodo(), DateUtils.PERIODO) : ""));
		cellprima2.setCellStyle(styleAll);

		Cell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(new HSSFRichTextString(ficha.getCodigo()));
		cell9.setCellStyle(styleAll);

		Cell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(ficha.getDescripcion()));
		cell10.setCellStyle(styleAll);

		Cell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(ficha.getCantidad()
				.toString()));
		cell18.setCellStyle(styleNumber);

		Cell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(ficha.getImporte()
				.toString()));
		cell19.setCellStyle(styleMoney);

		Cell cell15 = rowHeader.createCell(++col);
		cell15.setCellValue(ficha.getImporte_total().doubleValue());
		cell15.setCellStyle(styleMoney);

		Cell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(ficha.getCuit()));
		cell7.setCellStyle(styleAll);

		Cell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(ficha.getRazon_soc()));
		cell8.setCellStyle(styleAll);

		Cell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString(ficha
				.getLocalidad_prestador()));
		cell24.setCellStyle(styleAll);

		Cell cell25 = rowHeader.createCell(++col);
		cell25.setCellValue(new HSSFRichTextString(ficha.getProv_prestador()));
		cell25.setCellStyle(styleAll);

		Cell cell26 = rowHeader.createCell(++col);
		cell26.setCellValue(new HSSFRichTextString(
				ficha.getDebitado_omint() == null ? "0.0" : ficha
						.getDebitado_omint().toString()));
		cell26.setCellValue(
				ficha.getDebitado_omint()==null ? 0D :ficha.getDebitado_omint().doubleValue());
		cell26.setCellStyle(styleMoney);
		
		Cell cell26b = rowHeader.createCell(++col);
		cell26b.setCellValue(new HSSFRichTextString(ficha.getTercerizado()!=null?ficha.getTercerizado():""));
		cell26b.setCellStyle(styleMoney);

		Cell cell27 = rowHeader.createCell(++col);
		cell27.setCellValue(ficha.getId_orden_pago());
		cell27.setCellStyle(styleAll);
		
		Cell cell271 = rowHeader.createCell(++col);
		cell271.setCellValue(new HSSFRichTextString(
				ficha.getOp_fecha() != null ? DateUtils.format(ficha
						.getOp_fecha(), DateUtils.SHORT) : ""));
		cell271.setCellStyle(styleAll);


		Cell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(ficha.getPieza()));
		cell13.setCellStyle(styleAll);

		Cell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(new HSSFRichTextString(ficha.getCara()));
		cell14.setCellStyle(styleAll);

		Cell cell16 = rowHeader.createCell(++col);
		cell16.setCellValue(ficha.getNro_cuota());
		cell16.setCellStyle(styleAll);

		Cell cell17 = rowHeader.createCell(++col);
		cell17.setCellValue(ficha.getPorcentaje_cuota());
		cell17.setCellStyle(styleNumber);

		Cell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString(ficha.getLaboratorio()));
		cell12.setCellStyle(styleAll);

		Cell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(new HSSFRichTextString(ficha.getPresentacion()));
		cell11.setCellStyle(styleAll);

		Cell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(null!=ficha.getOspim()?ficha.getOspim().doubleValue():0);
		cell20.setCellStyle(styleMoney);

		Cell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(null!=ficha.getAmtima()?ficha.getAmtima().doubleValue():0);
		cell21.setCellStyle(styleMoney);
		
		Cell cell211 = rowHeader.createCell(++col);
		cell211.setCellValue(null!=ficha.getUoma()?ficha.getUoma().doubleValue():0);
		cell211.setCellStyle(styleMoney);

		Cell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(new HSSFRichTextString(ficha.getReceta()));
		cell22.setCellStyle(styleAll);

		Cell cell23 = rowHeader.createCell(++col);
		cell23.setCellValue(new HSSFRichTextString(
				ficha.getPorcentaje() == null ? "" : ficha.getPorcentaje()
						.toString()));
		cell23.setCellStyle(styleMoney);

		Cell cell024 = rowHeader.createCell(++col);
		cell024.setCellValue(new HSSFRichTextString(ficha.getDiagnostico()));
		cell024.setCellStyle(styleMoney);

		Cell cell025 = rowHeader.createCell(++col);
		cell025.setCellValue(new HSSFRichTextString(ficha.getPlan()==null ?"":ficha.getPlan()));
		cell025.setCellStyle(styleMoney);
		
		
		return ficha.getImporte_total() != null ? ficha.getImporte_total()
				: BigDecimal.ZERO;
	}
	
	private static BigDecimal crearHeaderDiscapacidad(HSSFSheet sheet, int index,
			FichaConsumo ficha, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		HSSFRow rowHeader = sheet.createRow(index);

		int col = -1;

		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(ficha.getTipo_consumo()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(ficha.getId_liquidacion());
		cell1.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(ficha.getApellido()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(ficha.getNombre()));
		cell4.setCellStyle(styleAll);

		HSSFCell cell3P = rowHeader.createCell(++col);
		cell3P.setCellValue(new HSSFRichTextString(ficha.getCuil_titular()));
		cell3P.setCellStyle(styleAll);

		HSSFCell cell4P = rowHeader.createCell(++col);
		cell4P.setCellValue(new HSSFRichTextString(String.valueOf(ficha.getInte())));
		cell4P.setCellStyle(styleAll);								

		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(ficha.getSecciona()));
		cell6.setCellStyle(styleAll);

		HSSFCell cell2P = rowHeader.createCell(++col);
		cell2P.setCellValue(new HSSFRichTextString(ficha.getDiagnostico() == null ? "" : ficha.getDiagnostico()));
		cell2P.setCellStyle(styleDate);

		HSSFCell cell2PP = rowHeader.createCell(++col);
		cell2PP.setCellValue(new HSSFRichTextString(ficha.getCiex() == null ? "" : ficha.getCiex()));
		cell2PP.setCellStyle(styleDate);
		
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(ficha.getFecha());
		cell2.setCellStyle(styleDate);

		HSSFCell cellprima2 = rowHeader.createCell(++col);
		cellprima2.setCellValue(new HSSFRichTextString(
				ficha.getPeriodo() != null ? DateUtils.format(ficha
						.getPeriodo(), DateUtils.PERIODO) : ""));
		cellprima2.setCellStyle(styleAll);

		HSSFCell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(new HSSFRichTextString(ficha.getCodigo()));
		cell9.setCellStyle(styleAll);

		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(ficha.getDescripcion()));
		cell10.setCellStyle(styleAll);

		HSSFCell cell10P = rowHeader.createCell(++col);
		cell10P.setCellValue(ficha.getFecha_prestacion());
		cell10P.setCellStyle(styleDate);
						
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(ficha.getCantidad()
				.toString()));
		cell18.setCellStyle(styleAll);
				
		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(ficha.getImporte().doubleValue());
		cell19.setCellStyle(styleMoney);

		
		HSSFCell cell15 = rowHeader.createCell(++col);
		cell15.setCellValue(ficha.getImporte_total().doubleValue());
		cell15.setCellStyle(styleMoney);

		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(ficha.getCuit()));
		cell7.setCellStyle(styleAll);

		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(ficha.getRazon_soc()));
		cell8.setCellStyle(styleAll);				
				
		HSSFCell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString(ficha.getFecha_comprobante() == null ? "" : ficha.getFecha_comprobante().toString()));
		cell24.setCellStyle(styleDate);
			
		HSSFCell cell25 = rowHeader.createCell(++col);
		cell25.setCellValue(ficha.getImporte_comprobante() == null ? 0 : ficha.getImporte_comprobante().doubleValue());
		cell25.setCellStyle(styleMoney);
		
		HSSFCell cell26 = rowHeader.createCell(++col);
		cell26.setCellValue(new HSSFRichTextString(
				ficha.getComprobante()));
		cell26.setCellStyle(styleMoney);
		
		HSSFCell cell27 = rowHeader.createCell(++col);
		cell27.setCellValue(new HSSFRichTextString(ficha.getTercerizado().equals("1") ? "Si" : "No"));
		cell27.setCellStyle(styleAll);

		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(String.valueOf(ficha.getId_orden_pago())));
		cell13.setCellStyle(styleAll);

		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(new HSSFRichTextString(ficha.getOp_fecha().toString()));
		cell14.setCellStyle(styleDate);

		return ficha.getImporte_total() != null ? ficha.getImporte_total()
				: BigDecimal.ZERO;
	}
}