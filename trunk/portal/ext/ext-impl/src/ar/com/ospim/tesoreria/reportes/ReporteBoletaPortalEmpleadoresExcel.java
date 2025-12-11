package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.afip.beans.ReporteAporteContribucionesEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class ReporteBoletaPortalEmpleadoresExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteBoletaPortalEmpleadoresExcel.class);

	public static HSSFWorkbook generaReporteBoletaPortalEmpleadores(
			HttpServletRequest renderRequest, HttpServletResponse res) {

		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
		String periodoDesdeDia = ParamUtil.getString(renderRequest,
				"periodoDesdeDia");
		String periodoDesdeMes = ParamUtil.getString(renderRequest,
				"periodoDesdeMes");
		String periodoDesdeAnio = ParamUtil.getString(renderRequest,
				"periodoDesdeAnio");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodo.parse(periodoDesdeDia + "/"
					+ (Integer.parseInt(periodoDesdeMes) + 1) + "/"
					+ periodoDesdeAnio);
		} catch (Exception e) {
			periodoDesde = null;
		}
		String periodoHastaDia = ParamUtil.getString(renderRequest,
				"periodoHastaDia");
		String periodoHastaMes = ParamUtil.getString(renderRequest,
				"periodoHastaMes");
		String periodoHastaAnio = ParamUtil.getString(renderRequest,
				"periodoHastaAnio");
		Date periodoHasta = null;
		try {
			periodoHasta = formatoDePeriodo.parse(periodoHastaDia + "/"
					+ (Integer.parseInt(periodoHastaMes) + 1) + "/"
					+ periodoHastaAnio);
		} catch (Exception e) {
			periodoHasta = null;
		}

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaRecDesdeDia = ParamUtil.getString(renderRequest,
				"fechaRecDesdeDia");
		String fechaRecDesdeMes = ParamUtil.getString(renderRequest,
				"fechaRecDesdeMes");
		String fechaRecDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaRecDesdeAnio");
		Date fechaRecDesde = null;
		try {
			fechaRecDesde = formatoDeFechas.parse(fechaRecDesdeDia + "/"
					+ (Integer.parseInt(fechaRecDesdeMes) + 1) + "/"
					+ fechaRecDesdeAnio);
		} catch (Exception e) {
			fechaRecDesde = null;
		}
		String fechaRecHastaDia = ParamUtil.getString(renderRequest,
				"fechaRecHastaDia");
		String fechaRecHastaMes = ParamUtil.getString(renderRequest,
				"fechaRecHastaMes");
		String fechaRecHastaAnio = ParamUtil.getString(renderRequest,
				"fechaRecHastaAnio");
		Date fechaRecHasta = null;
		try {
			fechaRecHasta = formatoDeFechas.parse(fechaRecHastaDia + "/"
					+ (Integer.parseInt(fechaRecHastaMes) + 1) + "/"
					+ fechaRecHastaAnio);
		} catch (Exception e) {
			fechaRecHasta = null;
		}

		String fechaRenDesdeDia = ParamUtil.getString(renderRequest,
				"fechaRenDesdeDia");
		String fechaRenDesdeMes = ParamUtil.getString(renderRequest,
				"fechaRenDesdeMes");
		String fechaRenDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaRenDesdeAnio");
		Date fechaRenDesde = null;
		try {
			fechaRenDesde = formatoDeFechas.parse(fechaRenDesdeDia + "/"
					+ (Integer.parseInt(fechaRenDesdeMes) + 1) + "/"
					+ fechaRenDesdeAnio);
		} catch (Exception e) {
			fechaRenDesde = null;
		}
		String fechaRenHastaDia = ParamUtil.getString(renderRequest,
				"fechaRenHastaDia");
		String fechaRenHastaMes = ParamUtil.getString(renderRequest,
				"fechaRenHastaMes");
		String fechaRenHastaAnio = ParamUtil.getString(renderRequest,
				"fechaRenHastaAnio");
		Date fechaRenHasta = null;
		try {
			fechaRenHasta = formatoDeFechas.parse(fechaRenHastaDia + "/"
					+ (Integer.parseInt(fechaRenHastaMes) + 1) + "/"
					+ fechaRenHastaAnio);
		} catch (Exception e) {
			fechaRenHasta = null;
		}

		String cuentaSuc = ParamUtil
				.getString(renderRequest, "cuentaSuc", null);
		String tipoBoleta = ParamUtil.getString(renderRequest, "tipoBoleta",
				null);
		String actaConvenio = ParamUtil.getString(renderRequest,
				"actaConvenio", null);
		String nroCheque = ParamUtil
				.getString(renderRequest, "nroCheque", null);
		int impDesde = ParamUtil.getInteger(renderRequest, "impDesde", 0);
		int impHasta = ParamUtil.getInteger(renderRequest, "impHasta", 0);
		String estadoCheque = ParamUtil.getString(renderRequest,
				"estadoCheque", null);
		String cuit_entidad = ParamUtil.getString(renderRequest,
				"cuit_entidad", null);
		boolean ddjj_todas_empresas = ParamUtil.getBoolean(renderRequest,
				"ddjj_todas_empresas");
		boolean consolidado = ParamUtil
				.getBoolean(renderRequest, "consolidado");
		boolean solo_ddjj = ParamUtil.getBoolean(renderRequest, "solo_ddjj");
		boolean cruzar_os = ParamUtil
				.getBoolean(renderRequest, "cruce_ddjj_os");

		int seccional_int = ParamUtil.getInteger(renderRequest, "id_seccional");

		List<FichaBoletaPortal> fichas = new ArrayList<FichaBoletaPortal>();

		// Si cruzar os debe ser consolidado;
		consolidado = consolidado || cruzar_os;

		try {
			// ME FIJO SI TIENE UNA SECCIONAL FIJA
			User user = PortalUtil.getUser(renderRequest);
			String seccionalDefecto = user.getExpandoBridge()
					.getAttribute("id_seccional").toString();
			// SI TIENE UNA SECCIONAL FIJADA LA SETEO
			seccional_int = seccionalDefecto != null
					&& !seccionalDefecto.trim().equals("")
					&& !seccionalDefecto.trim().equals("0") ? Integer
					.parseInt(seccionalDefecto) : seccional_int;

			if (ddjj_todas_empresas || solo_ddjj || cruzar_os) {

				fichas = PortalEmpleadoresServiceUtil
						.getReporteBoletaPortalTodasEmpresas(periodoDesde,
								periodoHasta, cuit_entidad, seccional_int,
								consolidado);

			} else {

				fichas = PortalEmpleadoresServiceUtil.getReporteBoletaPortal(
						periodoDesde, periodoHasta, cuentaSuc, tipoBoleta,
						actaConvenio, fechaRecDesde, fechaRecHasta, nroCheque,
						impDesde, impHasta, estadoCheque, cuit_entidad,
						fechaRenDesde, fechaRenHasta, seccional_int);

			}

			if (cruzar_os) {
				List<ReporteAporteContribucionesEmpresa> reporte = AfipServiceUtil
						.getReporteAportesContribucionEmpresas(periodoDesde, periodoHasta,
								999999, 0, cuit_entidad);
				HashMap<String, List<ReporteAporteContribucionesEmpresa>> hmOS = ReporteAporteContribucionesEmpresa
						.getHashMapApoCont(reporte);
				HashMap<String, List<FichaBoletaPortal>> hmBoleta = FichaBoletaPortal
						.getHashMapBoletaPortal(fichas);
				fichas = getCruceOSBoletas(hmOS, hmBoleta);
			}

			return generaReporteBoletaPortalEmpleadores(fichas, periodoDesde,
					periodoHasta, cuentaSuc, tipoBoleta, actaConvenio,
					fechaRecDesde, fechaRecHasta, nroCheque, impDesde,
					impHasta, estadoCheque, cuit_entidad, ddjj_todas_empresas,
					solo_ddjj, consolidado, cruzar_os, fechaRenDesde,
					fechaRenHasta, seccional_int);

		} catch (Exception e) {
			_log.error(
					"Error al generar reporte Boletas Portal Empleadores todas las ddjj",
					e);
			return null;
		}

	}

	private static HSSFWorkbook generaReporteBoletaPortalEmpleadores(
			List<FichaBoletaPortal> list, Date periodoDesde, Date periodoHasta,
			String cuentaSuc, String tipoBoleta, String actaConvenio,
			Date fechaRecDesde, Date fechaRecHasta, String nroCheque,
			int impDesde, int impHasta, String estadoCheque,
			String cuit_entidad, boolean ddjj_todas_empresas,
			boolean solo_ddjj, boolean consolidado, boolean cruzar_os,
			Date fechaRenDesde, Date fechaRenHasta, int seccional_int)
			throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date hoy = new Date();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Reporte");
		
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber = getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleDatePeriodo =  getStyleDate(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		if (ddjj_todas_empresas || solo_ddjj || cruzar_os) {

			StringBuffer titulo1 = new StringBuffer("Reporte ");
			if (consolidado) {
				titulo1.append("consolidado");
			}
			titulo1.append(" de Boletas del Portal Empleadores DDJJ: ").append(
					sdf.format(hoy));

			if (periodoDesde != null) {
				titulo1.append(" - Periodo Desde: ").append(
						sdf.format(periodoDesde));
			}

			if (periodoHasta != null) {
				titulo1.append(" - Periodo Hasta: ").append(
						sdf.format(periodoHasta));
			}

			if (ddjj_todas_empresas == true) {
				titulo1.append(" - CUIT Entidad: ")
						.append("Todas Las Empresas");
			} else {
				titulo1.append(" - CUIT Entidad: ").append(cuit_entidad);
			}
			if (cruzar_os) {
				titulo1.append(" Cruce con DDJJ de OS ");
			}

			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

			int index = 0;
			int col = -1;
			HSSFRow rowHeaderANT = sheet.createRow(index);
			HSSFCell cell0HA = rowHeaderANT.createCell(0);

			cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
			cell0HA.setCellStyle(styleBold);

			index++;
			HSSFRow rowHeader = sheet.createRow(index);

			HSSFCell cell0H = rowHeader.createCell(++col);
			cell0H.setCellValue(new HSSFRichTextString("CUIT Entidad "));
			cell0H.setCellStyle(styleBold);

			HSSFCell cell1H = rowHeader.createCell(++col);
			cell1H.setCellValue(new HSSFRichTextString("Razón Social "));
			cell1H.setCellStyle(styleBold);

			HSSFCell cell2H = rowHeader.createCell(++col);
			cell2H.setCellValue(new HSSFRichTextString("Cámara "));
			cell2H.setCellStyle(styleBold);

			HSSFCell cell2bH = rowHeader.createCell(++col);
			cell2bH.setCellValue(new HSSFRichTextString("Período "));
			cell2bH.setCellStyle(styleBold);

			HSSFCell cell2cH = rowHeader.createCell(++col);
			cell2cH.setCellValue(new HSSFRichTextString("Total Afi. Declarados"));
			cell2cH.setCellStyle(styleBold);

			if (!consolidado && !cruzar_os) {
				HSSFCell cell3H = rowHeader.createCell(++col);
				cell3H.setCellValue(new HSSFRichTextString("CUIL Titular "));
				cell3H.setCellStyle(styleBold);

				HSSFCell cell4H = rowHeader.createCell(++col);
				cell4H.setCellValue(new HSSFRichTextString("Apellido y Nombre "));
				cell4H.setCellStyle(styleBold);

				HSSFCell cell5H = rowHeader.createCell(++col);
				cell5H.setCellValue(new HSSFRichTextString("Fecha de ingreso "));
				cell5H.setCellStyle(styleBold);

				if (seccional_int == 0) {
					HSSFCell cell6H = rowHeader.createCell(++col);
					cell6H.setCellValue(new HSSFRichTextString(
							"Categoria Salarial "));
					cell6H.setCellStyle(styleBold);
				}
			}

			if (seccional_int == 0) {
				HSSFCell cell7H = rowHeader.createCell(++col);
				cell7H.setCellValue(new HSSFRichTextString("Remuneración "));
				cell7H.setCellStyle(styleBold);

				HSSFCell cell8H = rowHeader.createCell(++col);
				cell8H.setCellValue(new HSSFRichTextString(
						"Importe no remunerativo "));
				cell8H.setCellStyle(styleBold);
			}

			HSSFCell cell9H = rowHeader.createCell(++col);
			cell9H.setCellValue(new HSSFRichTextString("Aporte Solidario UOMA "));
			cell9H.setCellStyle(styleBold);

			HSSFCell cell9Hb = rowHeader.createCell(++col);
			cell9Hb.setCellValue(new HSSFRichTextString("Total Afi. Sol. UOMA"));
			cell9Hb.setCellStyle(styleBold);

			HSSFCell cell10H = rowHeader.createCell(++col);
			cell10H.setCellValue(new HSSFRichTextString("Articulo 46 "));
			cell10H.setCellStyle(styleBold);

			HSSFCell cell10Hb = rowHeader.createCell(++col);
			cell10Hb.setCellValue(new HSSFRichTextString("Total Afi. Art.46"));
			cell10Hb.setCellStyle(styleBold);

			HSSFCell cell11H = rowHeader.createCell(++col);
			cell11H.setCellValue(new HSSFRichTextString("Cuota AMTIMA "));
			cell11H.setCellStyle(styleBold);

			HSSFCell cell11Hb = rowHeader.createCell(++col);
			cell11Hb.setCellValue(new HSSFRichTextString("Total Afi. AMTIMA "));
			cell11Hb.setCellStyle(styleBold);

			HSSFCell cell12H = rowHeader.createCell(++col);
			cell12H.setCellValue(new HSSFRichTextString("Cuota Social UOMA "));
			cell12H.setCellStyle(styleBold);

			HSSFCell cell12Hb = rowHeader.createCell(++col);
			cell12Hb.setCellValue(new HSSFRichTextString(
					"Total Afi. Cuota Soc. UOMA"));
			cell12Hb.setCellStyle(styleBold);

			HSSFCell cell13H = rowHeader.createCell(++col);
			cell13H.setCellValue(new HSSFRichTextString("Cuota Usufructo "));
			cell13H.setCellStyle(styleBold);

			HSSFCell cell13Hb = rowHeader.createCell(++col);
			cell13Hb.setCellValue(new HSSFRichTextString("Total Afi. Usufructo"));
			cell13Hb.setCellStyle(styleBold);

			HSSFCell cell14H = rowHeader.createCell(++col);
			cell14H.setCellValue(new HSSFRichTextString("Adherente AMTIMA "));
			cell14H.setCellStyle(styleBold);

			HSSFCell cell14Hb = rowHeader.createCell(++col);
			cell14Hb.setCellValue(new HSSFRichTextString(
					"Total Afi. Adh. AMTIMA"));			
			cell14Hb.setCellStyle(styleBold);
			
			HSSFCell cell15Hb = rowHeader.createCell(++col);
			cell15Hb.setCellValue(new HSSFRichTextString(
					"Planta"));
			cell15Hb.setCellStyle(styleBold);
			
			HSSFCell cell16Hb = rowHeader.createCell(++col);
			cell16Hb.setCellValue(new HSSFRichTextString(
					"Calle"));
			cell16Hb.setCellStyle(styleBold);
			
			HSSFCell cell17Hb = rowHeader.createCell(++col);
			cell17Hb.setCellValue(new HSSFRichTextString(
					"Numero"));
			cell17Hb.setCellStyle(styleBold);
			
			HSSFCell cell18Hb = rowHeader.createCell(++col);
			cell18Hb.setCellValue(new HSSFRichTextString(
					"Piso"));
			cell18Hb.setCellStyle(styleBold);
			
			HSSFCell cell19Hb = rowHeader.createCell(++col);
			cell19Hb.setCellValue(new HSSFRichTextString(
					"Depto."));
			cell19Hb.setCellStyle(styleBold);
			
			HSSFCell cell20Hb = rowHeader.createCell(++col);
			cell20Hb.setCellValue(new HSSFRichTextString(
					"Cod. Postal"));
			cell20Hb.setCellStyle(styleBold);
			
			HSSFCell cell21Hb = rowHeader.createCell(++col);
			cell21Hb.setCellValue(new HSSFRichTextString(
					"Teléfono"));
			cell21Hb.setCellStyle(styleBold);
			
			HSSFCell cell22Hb = rowHeader.createCell(++col);
			cell22Hb.setCellValue(new HSSFRichTextString(
					"Localidad"));
			cell22Hb.setCellStyle(styleBold);

			HSSFCell cell23Hb = rowHeader.createCell(++col);
			cell23Hb.setCellValue(new HSSFRichTextString(
					"Provincia"));
			cell23Hb.setCellStyle(styleBold);
			
			if (cruzar_os) {
				HSSFCell cell15H = rowHeader.createCell(++col);
				cell15H.setCellValue(new HSSFRichTextString("OSPIM"));
				cell15H.setCellStyle(styleBold);

				HSSFCell cell15Hbb = rowHeader.createCell(++col);
				cell15Hbb.setCellValue(new HSSFRichTextString("Total OSPIM"));
				cell15Hbb.setCellStyle(styleBold);

			}

			index++;

			for (FichaBoletaPortal fichaBoletaPortal : list) {
				index = crearDatosFicha(solo_ddjj, ddjj_todas_empresas, sheet,
						fichaBoletaPortal, index, styleAll, styleNumber,
						styleNumber, styleMoney, styleNumber, styleDatePeriodo, 
						consolidado, cruzar_os, wb, seccional_int);
			}

			index++;
			sheet.createRow(index);
			
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

			return wb;

		}// cierro el if de ddjj_todas_empresas

		else {

			StringBuffer titulo1 = new StringBuffer(
					"Reporte de Boletas del Portal Empleadores: ").append(sdf
					.format(hoy));
			
			StringBuffer titulo2 = new StringBuffer("");

			if (periodoDesde != null) {
				titulo1.append(" - Periodo Desde: ").append(
						sdf.format(periodoDesde));
			}

			if (periodoHasta != null) {
				titulo1.append(" - Periodo Hasta: ").append(
						sdf.format(periodoHasta));
			}

			if (null != cuentaSuc && cuentaSuc.trim().length() > 0) {
				titulo1.append(" - Cuenta Sucursal: ").append(cuentaSuc);
			}

			if (null != tipoBoleta && tipoBoleta.trim().length() > 0) {
				titulo1.append(" - Tipo de Boleta: ").append(tipoBoleta);
			}

			if (null != actaConvenio && actaConvenio.trim().length() > 0) {
				titulo1.append(" - Acta Conenio: ").append(actaConvenio);
			}

			if (fechaRecDesde != null) {
				titulo2.append(" - Fecha Recaudación desde: ").append(
						sdf.format(fechaRecDesde));
			}

			if (fechaRecHasta != null) {
				titulo2.append(" - Fecha Recaudación hasta: ").append(
						sdf.format(fechaRecHasta));
			}

			if (fechaRenDesde != null) {
				titulo2.append(" - Fecha Rendición Desde: ").append(
						sdf.format(fechaRenDesde));
			}

			if (fechaRenHasta != null) {
				titulo2.append(" - Hasta: ").append(sdf.format(fechaRenHasta));
			}

			if (null != nroCheque && nroCheque.trim().length() > 0) {
				titulo2.append(" - Nro. Cheque: ").append(nroCheque);
			}

			if (impDesde > 0) {
				titulo2.append(" - Importe desde: ").append(impDesde);
			}

			if (impHasta > 0) {
				titulo2.append(" - Importe hasta: ").append(impHasta);
			}

			if (null != estadoCheque && estadoCheque.trim().length() > 0) {
				titulo2.append(" - Estado del cheque: ").append(estadoCheque);
			}

			if (null != cuit_entidad && cuit_entidad.trim().length() > 0) {
				titulo2.append(" - CUIT Entidad: ").append(cuit_entidad);
			}

			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

			int index = 0;
			int col = -1;
			HSSFRow rowHeaderANT = sheet.createRow(index);
			HSSFCell cell0HA = rowHeaderANT.createCell(++col);

			cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
			cell0HA.setCellStyle(styleBold);
			
			HSSFRow rowHeaderANT2 = sheet.createRow(++index);
			HSSFCell cell2HA2 = rowHeaderANT2.createCell(col);

			cell2HA2.setCellValue(new HSSFRichTextString(titulo2.toString()));
			cell2HA2.setCellStyle(styleBold);

			index++;
			HSSFRow rowHeader = sheet.createRow(index);

			HSSFCell cell0H = rowHeader.createCell(col);
			cell0H.setCellValue(new HSSFRichTextString("Descripción"));
			cell0H.setCellStyle(styleBold);

			HSSFCell cell1H = rowHeader.createCell(++col);
			cell1H.setCellValue(new HSSFRichTextString("Cuenta Suc."));
			cell1H.setCellStyle(styleBold);

			HSSFCell cell2H = rowHeader.createCell(++col);
			cell2H.setCellValue(new HSSFRichTextString(
					"Código Suc."));
			cell2H.setCellStyle(styleBold);

			HSSFCell cell3H = rowHeader.createCell(++col);
			cell3H.setCellValue(new HSSFRichTextString(
					"Nombre Suc."));
			cell3H.setCellStyle(styleBold);

			HSSFCell cell4H = rowHeader.createCell(++col);
			cell4H.setCellValue(new HSSFRichTextString("Fecha de recaudación"));
			cell4H.setCellStyle(styleBold);

			HSSFCell cell4aH = rowHeader.createCell(++col);
			cell4aH.setCellValue(new HSSFRichTextString("Fecha de rendición"));
			cell4aH.setCellStyle(styleBold);

			HSSFCell cell5H = rowHeader.createCell(++col);
			cell5H.setCellValue(new HSSFRichTextString(
					"Período"));
			cell5H.setCellStyle(styleBold);

			HSSFCell cell6H = rowHeader.createCell(++col);
			cell6H.setCellValue(new HSSFRichTextString("Cuit"));
			cell6H.setCellStyle(styleBold);

			HSSFCell cell8H = rowHeader.createCell(++col);
			cell8H.setCellValue(new HSSFRichTextString("Razón Social"));
			cell8H.setCellStyle(styleBold);
			
			HSSFCell cell7H = rowHeader.createCell(++col);
			cell7H.setCellValue(new HSSFRichTextString(
					"N° Boleta"));
			cell7H.setCellStyle(styleBold);

			HSSFCell cell9H = rowHeader.createCell(++col);
			cell9H.setCellValue(new HSSFRichTextString("Importe"));
			cell9H.setCellStyle(styleBold);

			HSSFCell cell10H = rowHeader.createCell(++col);
			cell10H.setCellValue(new HSSFRichTextString("Nro. Cheque"));
			cell10H.setCellStyle(styleBold);

			HSSFCell cell11H = rowHeader.createCell(++col);
			cell11H.setCellValue(new HSSFRichTextString("Estado Cheque"));
			cell11H.setCellStyle(styleBold);

			HSSFCell cell12H = rowHeader.createCell(++col);
			cell12H.setCellValue(new HSSFRichTextString("Nro. Acta"));
			cell12H.setCellStyle(styleBold);

			HSSFCell cell13H = rowHeader.createCell(++col);
			cell13H.setCellValue(new HSSFRichTextString("Observaciones"));
			cell13H.setCellStyle(styleBold);
			index++;

			for (FichaBoletaPortal fichaBoletaPortal : list) {
				index = crearDatosFicha(solo_ddjj, ddjj_todas_empresas, sheet,
						fichaBoletaPortal, index, styleAll, styleNumber,
						styleNumber, styleMoney, styleNumber, styleDatePeriodo, 
						consolidado, cruzar_os, wb, seccional_int);
			}

			index++;
			sheet.createRow(index);
			
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

			return wb;

		}// cierro else de ddjj_todas_empresas
	}

	private static int crearDatosFicha(boolean solo_ddjj,
			boolean ddjj_todas_empresas, HSSFSheet sheet,
			FichaBoletaPortal fichaBoletaPortal, int index,
			HSSFCellStyle styleAll, HSSFCellStyle styleBold,
			HSSFCellStyle styleDate, HSSFCellStyle styleMoney,
			HSSFCellStyle styleNumber, HSSFCellStyle styleDatePeriodo,
			boolean consolidado, boolean cruzar_os,
			HSSFWorkbook wb, int seccional_int) throws Exception {

		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);

		if (ddjj_todas_empresas == true || solo_ddjj == true || cruzar_os) {

			HSSFCell cell0 = rowHeader.createCell(++col);
			cell0.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getEmpresa_cuit()));
			cell0.setCellStyle(styleNumber);

			HSSFCell cell1 = rowHeader.createCell(++col);
			cell1.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getRazon_soc()));
			cell1.setCellStyle(styleAll);

			HSSFCell cell2 = rowHeader.createCell(++col);
			cell2.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getCamara()));
			cell2.setCellStyle(styleAll);

			HSSFCell cell2a = rowHeader.createCell(++col);
			cell2a.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getPeriodoAsString()));
			cell2a.setCellStyle(styleAll);

			HSSFCell cell2b = rowHeader.createCell(++col);
			cell2b.setCellValue(fichaBoletaPortal.getTotalDeclarada());
			cell2b.setCellStyle(styleAll);
			if (!consolidado && !cruzar_os) {
				HSSFCell cell3 = rowHeader.createCell(++col);
				cell3.setCellValue(new HSSFRichTextString(fichaBoletaPortal
						.getCuil_titular()));
				cell3.setCellStyle(styleAll);

				HSSFCell cell4 = rowHeader.createCell(++col);
				cell4.setCellValue(new HSSFRichTextString(fichaBoletaPortal
						.getApellido()));
				cell4.setCellStyle(styleAll);

				HSSFCell cell5 = rowHeader.createCell(++col);
				cell5.setCellValue(new HSSFRichTextString(fichaBoletaPortal
						.getFecha_ing()));
				cell5.setCellStyle(styleAll);

				if (seccional_int == 0) {
					HSSFCell cell6 = rowHeader.createCell(++col);
					cell6.setCellValue(new HSSFRichTextString(fichaBoletaPortal
							.getCategoriasalarial()));
					cell6.setCellStyle(styleAll);
				}
			}
			if (seccional_int == 0) {
				HSSFCell cell7 = rowHeader.createCell(++col);
				cell7.setCellValue(null != fichaBoletaPortal.getRemuneracion() ? fichaBoletaPortal
						.getRemuneracion().doubleValue() : 0d);
				cell7.setCellStyle(styleMoney);

				HSSFCell cell8 = rowHeader.createCell(++col);
				cell8.setCellValue(fichaBoletaPortal.getImportenoremunerativo());
				cell8.setCellStyle(styleMoney);
			}

			if (seccional_int == 0) {
				HSSFCell cell9 = rowHeader.createCell(++col);
				cell9.setCellValue(null != fichaBoletaPortal
						.getAportesocialuoma() ? fichaBoletaPortal
						.getAportesocialuoma().doubleValue() : 0d);
				cell9.setCellStyle(styleMoney);
			} else {
				HSSFCell cell9 = rowHeader.createCell(++col);
				cell9.setCellValue(new HSSFRichTextString(
						null != fichaBoletaPortal.getAportesocialuoma() && fichaBoletaPortal.getAportesocialuoma().compareTo(BigDecimal.ZERO)>0? "SI"
								: "NO"));
				cell9.setCellStyle(styleAll);
			}

			HSSFCell cell9b = rowHeader.createCell(++col);
			cell9b.setCellValue(fichaBoletaPortal.getTotalSocialUoma());
			cell9b.setCellStyle(styleNumber);

			if (seccional_int == 0) {
				HSSFCell cell10 = rowHeader.createCell(++col);
				cell10.setCellValue(null != fichaBoletaPortal.getArticulo46()&& fichaBoletaPortal.getArticulo46().compareTo(BigDecimal.ZERO)>0 ? fichaBoletaPortal
						.getArticulo46().doubleValue() : 0d);
				cell10.setCellStyle(styleMoney);
			} else {
				HSSFCell cell10 = rowHeader.createCell(++col);
				cell10.setCellValue(new HSSFRichTextString(
						null != fichaBoletaPortal.getArticulo46() && fichaBoletaPortal.getArticulo46().compareTo(BigDecimal.ZERO)>0 ? "SI" : "NO"));
				cell10.setCellStyle(styleAll);
			}
			HSSFCell cell10b = rowHeader.createCell(++col);
			cell10b.setCellValue(fichaBoletaPortal.getTotalArt46());
			cell10b.setCellStyle(styleNumber);

			if (seccional_int == 0) {
				HSSFCell cell11 = rowHeader.createCell(++col);
				cell11.setCellValue(null != fichaBoletaPortal.getCuotaamtima() && fichaBoletaPortal.getCuotaamtima().compareTo(BigDecimal.ZERO)>0 ? fichaBoletaPortal
						.getCuotaamtima().doubleValue() : 0);
				cell11.setCellStyle(styleMoney);
			} else {
				HSSFCell cell11 = rowHeader.createCell(++col);
				cell11.setCellValue(new HSSFRichTextString(
						null != fichaBoletaPortal.getCuotaamtima() && fichaBoletaPortal.getCuotaamtima().compareTo(BigDecimal.ZERO)>0  ? "SI"
								: "NO"));
				cell11.setCellStyle(styleAll);
			}
			HSSFCell cell11b = rowHeader.createCell(++col);
			cell11b.setCellValue(fichaBoletaPortal.getTotalAmtima());
			cell11b.setCellStyle(styleNumber);

			if (seccional_int == 0) {
				HSSFCell cell12 = rowHeader.createCell(++col);
				cell12.setCellValue(null != fichaBoletaPortal
						.getCuotasocialuoma() && fichaBoletaPortal
						.getCuotasocialuoma().compareTo(BigDecimal.ZERO)>0  ? fichaBoletaPortal
						.getCuotasocialuoma().doubleValue() : 0);
				cell12.setCellStyle(styleMoney);
			} else {
				HSSFCell cell12 = rowHeader.createCell(++col);
				cell12.setCellValue(new HSSFRichTextString(
						null != fichaBoletaPortal.getCuotasocialuoma()  && fichaBoletaPortal.getCuotasocialuoma().compareTo(BigDecimal.ZERO)>0  ? "SI"
								: "NO"));
				cell12.setCellStyle(styleAll);

			}

			HSSFCell cell12b = rowHeader.createCell(++col);
			cell12b.setCellValue(fichaBoletaPortal.getTotalCuotaUoma());
			cell12b.setCellStyle(styleNumber);

			if (seccional_int == 0) {
				HSSFCell cell13 = rowHeader.createCell(++col);
				cell13.setCellValue(null != fichaBoletaPortal
						.getCuotausufructo() && fichaBoletaPortal
						.getCuotausufructo().compareTo(BigDecimal.ZERO)>0 ? fichaBoletaPortal
						.getCuotausufructo().doubleValue() : 0d);
				cell13.setCellStyle(styleMoney);
			} else {
				HSSFCell cell13 = rowHeader.createCell(++col);
				cell13.setCellValue(new HSSFRichTextString(null != fichaBoletaPortal
						.getCuotausufructo()&& fichaBoletaPortal
						.getCuotausufructo().compareTo(BigDecimal.ZERO)>0 ? "SI" : "NO"));
				cell13.setCellStyle(styleAll);

			}

			HSSFCell cell13b = rowHeader.createCell(++col);
			cell13b.setCellValue(fichaBoletaPortal.getTotalUsufructo());
			cell13b.setCellStyle(styleNumber);
			
			if (seccional_int == 0) {
			HSSFCell cell14 = rowHeader.createCell(++col);
			cell14.setCellValue(null != fichaBoletaPortal.getAdherenteamtima() && fichaBoletaPortal.getAdherenteamtima().compareTo(BigDecimal.ZERO)>0   
					? fichaBoletaPortal
					.getAdherenteamtima().doubleValue() : 0);
			cell14.setCellStyle(styleMoney);
			}else{				
				HSSFCell cell14 = rowHeader.createCell(++col);
				cell14.setCellValue(new HSSFRichTextString(null != fichaBoletaPortal.getAdherenteamtima() && fichaBoletaPortal.getAdherenteamtima().compareTo(BigDecimal.ZERO)>0 ? "SI": "NO"));
				cell14.setCellStyle(styleAll);				
			}

			HSSFCell cell14b = rowHeader.createCell(++col);
			cell14b.setCellValue(fichaBoletaPortal.getTotalAdhAmtima());
			cell14b.setCellStyle(styleNumber);
			
			HSSFCell cell15b = rowHeader.createCell(++col);
			cell15b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getPlanta()):new HSSFRichTextString(""));
			cell15b.setCellStyle(styleAll);
			
			HSSFCell cell16b = rowHeader.createCell(++col);
			cell16b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getCalle()):new HSSFRichTextString(""));
			cell16b.setCellStyle(styleAll);
			
			HSSFCell cell17b = rowHeader.createCell(++col);
			cell17b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getNumero()):new HSSFRichTextString(""));
			cell17b.setCellStyle(styleAll);
			
			HSSFCell cell18b = rowHeader.createCell(++col);
			cell18b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getPiso()):new HSSFRichTextString(""));
			cell18b.setCellStyle(styleAll);
			
			HSSFCell cell19b = rowHeader.createCell(++col);
			cell19b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getDepto()):new HSSFRichTextString(""));
			cell19b.setCellStyle(styleAll);
						
			
			HSSFCell cell21b = rowHeader.createCell(++col);
			cell21b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getPostal_codi()):new HSSFRichTextString(""));
			cell21b.setCellStyle(styleAll);
			
			HSSFCell cell22b = rowHeader.createCell(++col);
			cell22b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getTelefono()):new HSSFRichTextString(""));
			cell22b.setCellStyle(styleAll);
			
			HSSFCell cell23b = rowHeader.createCell(++col);
			cell23b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getLocalidad().getDescripcion()):new HSSFRichTextString(""));
			cell23b.setCellStyle(styleAll);
			
			HSSFCell cell24b = rowHeader.createCell(++col);
			cell24b.setCellValue(null!=fichaBoletaPortal.getDomicilio()?new HSSFRichTextString(fichaBoletaPortal.getDomicilio().getProvincia().getDescripcion()):new HSSFRichTextString(""));
			cell24b.setCellStyle(styleAll);

			if (cruzar_os) {
				HSSFCell cell15 = rowHeader.createCell(++col);
				cell15.setCellValue(fichaBoletaPortal.getTotalOS());
				cell15.setCellStyle(styleNumber);

				HSSFCell cell15bb = rowHeader.createCell(++col);
				cell15bb.setCellValue(null != fichaBoletaPortal
						.getRemuneracionOS() ? fichaBoletaPortal
						.getRemuneracionOS().doubleValue() : 0d);
				cell15bb.setCellStyle(styleMoney);
			}

		} else {

			HSSFCell cell0 = rowHeader.createCell(++col);
			cell0.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getDescripcion()));
			cell0.setCellStyle(styleAll);

			HSSFCell cell1 = rowHeader.createCell(++col);
			cell1.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getCuenta_sucursal()));
			cell1.setCellStyle(styleAll);

			HSSFCell cell2 = rowHeader.createCell(++col);
			cell2.setCellValue(fichaBoletaPortal.getCod_sucursal_nacion());
			cell2.setCellStyle(styleAll);

			HSSFCell cell3 = rowHeader.createCell(++col);
			cell3.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getNombre_suc_nacion()));
			cell3.setCellStyle(styleAll);

			
			HSSFDataFormat df = wb.createDataFormat();
			styleDate.setDataFormat(df.getFormat("dd-MM-yyyy"));
			
			HSSFCell cell4 = rowHeader.createCell(++col);
			cell4.setCellValue(fichaBoletaPortal.getFecha_recauda());
			cell4.setCellStyle(styleDate);

			HSSFCell cell5a = rowHeader.createCell(++col);
			cell5a.setCellValue(fichaBoletaPortal
					.getFecha_rendicion());
			cell5a.setCellStyle(styleDate);

			styleDatePeriodo.setDataFormat(df.getFormat("MM-yyyy"));
			
			HSSFCell cell5 = rowHeader.createCell(++col);
			cell5.setCellValue(fichaBoletaPortal
					.getPeriodo_cod_barras());
			cell5.setCellStyle(styleDatePeriodo);

			HSSFCell cell6 = rowHeader.createCell(++col);
			cell6.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getCuit()));
			cell6.setCellStyle(styleNumber);

			HSSFCell cell8 = rowHeader.createCell(++col);
			cell8.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getRazon_soc()));
			cell8.setCellStyle(styleAll);
			
			HSSFCell cell7 = rowHeader.createCell(++col);
			cell7.setCellValue(fichaBoletaPortal.getNro_boleta_portal_emple());
			cell7.setCellStyle(styleAll);

			HSSFCell cell9 = rowHeader.createCell(++col);
			cell9.setCellValue(null != fichaBoletaPortal.getImporte() ? fichaBoletaPortal
					.getImporte().doubleValue() : 0d);
			cell9.setCellStyle(styleMoney);

			HSSFCell cell10 = rowHeader.createCell(++col);
			cell10.setCellValue(new HSSFRichTextString(
					null != fichaBoletaPortal.getNro_cheque() ? fichaBoletaPortal
							.getNro_cheque().toString() : ""));
			cell10.setCellStyle(styleNumber);

			HSSFCell cell11 = rowHeader.createCell(++col);
			cell11.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getEstado_cheque()));
			cell11.setCellStyle(styleAll);

			HSSFCell cell12 = rowHeader.createCell(++col);
			cell12.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getNroacta()));
			cell12.setCellStyle(styleNumber);

			HSSFCell cell13 = rowHeader.createCell(++col);
			cell13.setCellValue(new HSSFRichTextString(fichaBoletaPortal
					.getObservacion()));
			cell13.setCellStyle(styleAll);

		}

		return index++;

	}

	public static List<FichaBoletaPortal> getCruceOSBoletas(
			HashMap<String, List<ReporteAporteContribucionesEmpresa>> hmOS,
			HashMap<String, List<FichaBoletaPortal>> hmBoleta) {
		List<FichaBoletaPortal> nuevaLista = new ArrayList<FichaBoletaPortal>();
		Iterator<String> keyBoletas = hmBoleta.keySet().iterator();
		// Partimos de EMPLEADORES
		while (keyBoletas.hasNext()) {
			String empresaCuit = keyBoletas.next();
			List<ReporteAporteContribucionesEmpresa> listApoCon = hmOS
					.get(empresaCuit);
			List<FichaBoletaPortal> listBoleta = hmBoleta.get(empresaCuit);
			for (FichaBoletaPortal ficha : listBoleta) {
				if (null != listApoCon) {
					for (ReporteAporteContribucionesEmpresa repo : listApoCon) {
						if (repo.getPeriodo().equals(
								ficha.getPeriodo_cod_barras())) {
							ficha.setRemuneracionOS(ficha.getRemuneracionOS() == null ? repo
									.getRemuneracionDeclarada() : ficha
									.getRemuneracionOS().add(
											repo.getRemuneracionDeclarada()));
							ficha.setTotalOS(repo
									.getCantidadAfiliadosDeclarados());
						}
					}
				}
				nuevaLista.add(ficha);
			}

		}
		Iterator<String> keyOS = hmOS.keySet().iterator();
		// LO QUE NO ESTE EN EMPLEADORES
		while (keyOS.hasNext()) {
			String cuitControl = null;
			String empresaCuit = keyOS.next();
			List<ReporteAporteContribucionesEmpresa> listApoCon = hmOS
					.get(empresaCuit);
			List<FichaBoletaPortal> listBoleta = hmBoleta.get(empresaCuit);
			for (ReporteAporteContribucionesEmpresa repo : listApoCon) {
				boolean encontrado = false;
				if (null != listBoleta) {
					for (FichaBoletaPortal ficha : listBoleta) {
						if (repo.getPeriodo().equals(
								ficha.getPeriodo_cod_barras())) {
							encontrado = true;
						}
					}
				}

				if (!encontrado
						&& (cuitControl == null || !cuitControl
								.equals(empresaCuit))) {
					List<ReporteAporteContribucionesEmpresa> listApoConNuev = hmOS
							.get(empresaCuit);
					BigDecimal remuneracionTotal = BigDecimal.ZERO;
					for (ReporteAporteContribucionesEmpresa repoNuevo : listApoConNuev) {
						remuneracionTotal = remuneracionTotal.add(repoNuevo
								.getRemuneracionDeclarada());
					}
					FichaBoletaPortal ficha = new FichaBoletaPortal();
					ficha.setEmpresa_cuit(repo.getCuitContribuyente());
					ficha.setRazon_soc(repo.getRazon());
					ficha.setTotalOS(repo.getCantidadAfiliadosDeclarados());
					ficha.setRemuneracionOS(remuneracionTotal);
					ficha.setCamara(String.valueOf(repo.getRamo()));
					nuevaLista.add(ficha);
				}
				cuitControl = empresaCuit;
			}

		}

		return nuevaLista;
	}
}