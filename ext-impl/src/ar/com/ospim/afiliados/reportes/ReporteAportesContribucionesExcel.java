package ar.com.ospim.afiliados.reportes;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.afiliados.services.AporteServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class ReporteAportesContribucionesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAportesContribucionesExcel.class);

	public static HSSFWorkbook generaReporteAportes(HttpServletRequest req,
			HttpServletResponse res) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAllWithHeader = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleBoldWithBorder = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleHeaderWithBorder = getStyleHeaderWithBorder(wb);

		try {

			String cuil_titular = ParamUtil.getString(req, "cuil");
			String solo_derivacion = ParamUtil
					.getString(req, "solo_derivacion");

			boolean cuota_amtima = ParamUtil.getBoolean(req, "cuota_amtima");
			boolean cuota_usufructo = ParamUtil.getBoolean(req,
					"cuota_usufructo");
			boolean art_46 = ParamUtil.getBoolean(req, "art_46");
			boolean cuota_social_uoma = ParamUtil.getBoolean(req,
					"cuota_social_uoma");
			boolean aporte_solidario_uoma = ParamUtil.getBoolean(req,
					"aporte_solidario_uoma");
			boolean aporte_afip_ospim = ParamUtil.getBoolean(req,
					"aporte_afip_ospim");
			boolean boleta_blanca_ospim = ParamUtil.getBoolean(req,
					"boleta_blanca_ospim");
			boolean boleta_blanca_uoma = ParamUtil.getBoolean(req,
					"boleta_blanca_uoma");
			boolean boleta_blanca_amtima = ParamUtil.getBoolean(req,
					"boleta_blanca_amtima");

			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio[] = ParamUtil.getString(req,
					"periodoDesdeMesAnio").split("_");
			Date periodoDesde = null;
			try {
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeMesAnio[0])
						+ 1
						+ "/"
						+ periodoDesdeMesAnio[1]);
			} catch (Exception e) {
				periodoDesde = null;
			}
			if (periodoDesde == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				periodoDesde = formatoDePeriodos.parse(Integer.parseInt("01")
						+ "/" + (calendar.get(Calendar.YEAR) - 5));
			}

			List<AporteAfiliado> afiAportes = null;
			afiAportes = AporteServiceUtil.buscaAportesAfipAfiliado(cuil_titular, periodoDesde);
			
			afiAportes.addAll(AporteServiceUtil.buscaAportesEmpleadoresAfiliado(cuil_titular, periodoDesde));

			AporteServiceUtil.sortByDate(afiAportes);
			
			List<AporteAfiliado> afiliadosList = null;
			afiliadosList = AporteServiceUtil.filtrarLista(afiAportes,
					cuota_amtima, cuota_usufructo, art_46, cuota_social_uoma,
					aporte_solidario_uoma, aporte_afip_ospim,
					boleta_blanca_ospim, boleta_blanca_uoma,
					boleta_blanca_amtima);

			/*Regla para evitar que se vena aportes Ospim antes del 01/01/2013*/
			User user = PortalUtil.getUser(req);   //PortalUtil.getHttpServletRequest(renderRequest));
			boolean permiteVerAportesOOSSdesde2011 = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VER_APORTES_OSPIM);
			
			if(!permiteVerAportesOOSSdesde2011){

				Calendar fechaRestriccion = Calendar.getInstance();
//				fechaRestriccion.set(2013, 1,1);
//				fechaRestriccion.set(2012, 12, 31, 23, 59 );
				fechaRestriccion.set(Calendar.YEAR, 2013);
				fechaRestriccion.set(Calendar.MONTH, 0);
				fechaRestriccion.set(Calendar.DATE, 1);
				fechaRestriccion.set(Calendar.HOUR_OF_DAY, 0);
				fechaRestriccion.set(Calendar.MINUTE, 0);
				fechaRestriccion.set(Calendar.SECOND, 0);
				fechaRestriccion.set(Calendar.MILLISECOND, 0);
				
				List<AporteAfiliado> auxAportesRestringidos = new ArrayList<AporteAfiliado>();

				for (Iterator<AporteAfiliado> iterator = afiAportes.iterator(); iterator.hasNext();) {
					
					AporteAfiliado aa = iterator.next();
					if(aa.getTipoAporte() == WebKeysGlobal.TIPO_BOLETA_OS && (aa.getPeriodo().getTime() < fechaRestriccion.getTimeInMillis())){ // aporte_os
//						aa.setMostrar(false);
						auxAportesRestringidos.add(aa);
					}
				}
				afiAportes.removeAll(auxAportesRestringidos);
			}
			/*fin regla*/
			
			HSSFSheet sheet = wb.createSheet("Hoja 1");
			int index = 0;
			Collections.sort(afiliadosList, new Comparator<AporteAfiliado>() {
				public int compare(AporteAfiliado o1, AporteAfiliado o2) {
					if (o1.getPeriodo().equals(o2.getPeriodo())) {
						return o1.getPeriodo().compareTo(o2.getPeriodo());
					} else {
						return o1.getPeriodo().compareTo(o2.getPeriodo());
					}
				}
			});

			crearHeaderReporteAportes(sheet, styleHeaderWithBorder);
			for (AporteAfiliado repo : afiliadosList) {
				if ((solo_derivacion.trim().equals("true") && repo
						.getAfiliado().getApeNombre().trim()
						.contains("DERIVACION"))
						|| ((solo_derivacion.trim().equals("false")) || (null == solo_derivacion))) {
					if (repo.isMostrar()) {
						++index;
						crearInfoAportes(sheet, repo.getTipoAporteDeno(), repo
								.getAfiliado().getCuil_titular(), repo
								.getAfiliado().getApellido(), repo
								.getAfiliado().getNombre(), repo.getAfiliado()
								.getIngre_fechaAsString(), repo.getAfiliado()
								.getBaja_fechaAsString(), repo.getEmpleador()
								.getCuit(), repo.getEmpleador().getRazon_soc(),
								repo.getPeriodoAsString(), repo.getImporte(),
								repo.getContribucionEstimada(),
								repo.getLiqActas(), repo.getComisionOS(),
								repo.getTotalLiqTercerizadora(),
								repo.getFechaLiqTercerizadoraString(),
								repo.getFechaTransfAsString(),
								repo.getFechaRecaudaAsString(),
								repo.getRemuneracion(), repo.getConcepto(),
								repo.getIdTerc(), index, styleDateWithBorder,
								styleAllWithHeader, styleBoldWithBorder);
					}
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
			sheet.autoSizeColumn((short) 14);
			sheet.autoSizeColumn((short) 15);
			sheet.autoSizeColumn((short) 16);
			sheet.autoSizeColumn((short) 17);
			sheet.autoSizeColumn((short) 18);

		} catch (ParseException e) {
			_log.error("Error al generar reporte", e);
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return wb;
	}

	private static void crearInfoAportes(HSSFSheet sheet, String tipoAporte,
			String cuil, String ape, String nombre, String altaFecha,
			String bajaFecha, String cuit, String razonSoc, String periodo,
			BigDecimal aporte, BigDecimal contrib, BigDecimal actas,
			BigDecimal comisionOS, BigDecimal totalTerc, String fechaLiqTerce,
			String fechaTransferencia, String fechaRecauda,
			BigDecimal remuneracion, String concepto, String id_terc,
			int index, HSSFCellStyle styleDate, HSSFCellStyle styleAll,
			HSSFCellStyle styleBold) {
		int i = 0;
		HSSFRow row = sheet.createRow(index);
		HSSFCell cell0 = row.createCell(i++);
		cell0.setCellValue(new HSSFRichTextString(tipoAporte));
		cell0.setCellStyle(styleAll);
		HSSFCell cell = row.createCell(i++);
		cell.setCellValue(new HSSFRichTextString(cuil));
		boolean derivacion = ape.trim().contains("DERIVACION");
		if (derivacion) {
			cell.setCellStyle(styleBold);
		} else {
			cell.setCellStyle(styleAll);
		}
		HSSFCell cell1 = row.createCell(i++);
		cell1.setCellValue(new HSSFRichTextString(ape + ", " + nombre));
		if (derivacion) {
			cell1.setCellStyle(styleBold);
		} else {
			cell1.setCellStyle(styleAll);
		}
		HSSFCell cell2 = row.createCell(i++);
		cell2.setCellValue(altaFecha);
		cell2.setCellStyle(styleDate);

		HSSFCell cell3 = row.createCell(i++);
		cell3.setCellStyle(styleDate);
		HSSFCell cell4 = row.createCell(i++);
		cell4.setCellValue(new HSSFRichTextString(cuit != null ? cuit : ""));
		if (derivacion) {
			cell4.setCellStyle(styleBold);
		} else {
			cell4.setCellStyle(styleAll);
		}
		HSSFCell cell5 = row.createCell(i++);
		cell5.setCellValue(new HSSFRichTextString(razonSoc != null ? razonSoc
				: ""));
		if (derivacion) {
			cell5.setCellStyle(styleBold);
		} else {
			cell5.setCellStyle(styleAll);
		}
		HSSFCell cell6 = row.createCell(i++);
		cell6.setCellValue(new HSSFRichTextString(periodo));
		if (derivacion) {
			cell6.setCellStyle(styleBold);
		} else {
			cell6.setCellStyle(styleAll);
		}

		HSSFCell cell71 = row.createCell(i++);
		cell71.setCellValue(new HSSFRichTextString(fechaRecauda));
		cell71.setCellStyle(styleDate);

		HSSFCell cell7 = row.createCell(i++);
		cell7.setCellValue(new HSSFRichTextString(fechaTransferencia));
		cell7.setCellStyle(styleDate);

		HSSFCell cell72 = row.createCell(i++);
		cell72.setCellValue(remuneracion.doubleValue());

		if (derivacion) {
			cell72.setCellStyle(styleBold);
		} else {
			cell72.setCellStyle(styleAll);
		}

		HSSFCell cell8 = row.createCell(i++);
		cell8.setCellValue(aporte != null ? aporte.doubleValue() : 0);
		if (derivacion) {
			cell8.setCellStyle(styleBold);
		} else {
			cell8.setCellStyle(styleAll);
		}
		HSSFCell cell9 = row.createCell(i++);
		cell9.setCellValue(contrib != null ? contrib.doubleValue() : 0);
		if (derivacion) {
			cell9.setCellStyle(styleBold);
		} else {
			cell9.setCellStyle(styleAll);
		}

		HSSFCell cell10 = row.createCell(i++);
		cell10.setCellValue(actas != null ? actas.doubleValue() : 0);
		if (derivacion) {
			cell10.setCellStyle(styleBold);
		} else {
			cell10.setCellStyle(styleAll);
		}
		HSSFCell cell11 = row.createCell(i++);
		cell11.setCellValue(null != comisionOS ? comisionOS.doubleValue() : 0);
		if (derivacion) {
			cell11.setCellStyle(styleBold);
		} else {
			cell11.setCellStyle(styleAll);
		}
		HSSFCell cell12 = row.createCell(i++);
		cell12.setCellValue(null != totalTerc ? totalTerc.doubleValue() : 0);
		if (derivacion) {
			cell12.setCellStyle(styleBold);
		} else {
			cell12.setCellStyle(styleAll);
		}
		HSSFCell cell13 = row.createCell(i++);
		cell13.setCellValue(new HSSFRichTextString(fechaLiqTerce));
		if (derivacion) {
			cell13.setCellStyle(styleBold);
		} else {
			cell13.setCellStyle(styleAll);
		}
		HSSFCell cell131 = row.createCell(i++);
		cell131.setCellValue(new HSSFRichTextString(id_terc));
		if (derivacion) {
			cell131.setCellStyle(styleBold);
		} else {
			cell131.setCellStyle(styleAll);
		}
	}

	private static void crearHeaderReporteAportes(HSSFSheet sheet,
			HSSFCellStyle styleHeader) {
		int i = 0;
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell0 = row.createCell(i++);
		cell0.setCellValue(new HSSFRichTextString("Tipo Aporte"));
		cell0.setCellStyle(styleHeader);
		HSSFCell cell = row.createCell(i++);
		cell.setCellValue(new HSSFRichTextString("CUIL"));
		cell.setCellStyle(styleHeader);
		HSSFCell cell1 = row.createCell(i++);
		cell1.setCellValue(new HSSFRichTextString("Nombre"));
		cell1.setCellStyle(styleHeader);
		HSSFCell cell2 = row.createCell(i++);
		cell2.setCellValue(new HSSFRichTextString("Ultima Alta Afiliado"));
		cell2.setCellStyle(styleHeader);
		HSSFCell cell3 = row.createCell(i++);
		cell3.setCellValue(new HSSFRichTextString("Fecha Baja"));
		cell3.setCellStyle(styleHeader);
		HSSFCell cell4 = row.createCell(i++);
		cell4.setCellValue(new HSSFRichTextString("CUIT"));
		cell4.setCellStyle(styleHeader);
		HSSFCell cell5 = row.createCell(i++);
		cell5.setCellValue(new HSSFRichTextString("Razón Social"));
		cell5.setCellStyle(styleHeader);
		HSSFCell cell6 = row.createCell(i++);
		cell6.setCellValue(new HSSFRichTextString("Período"));
		cell6.setCellStyle(styleHeader);
		HSSFCell cell61 = row.createCell(i++);
		cell61.setCellValue(new HSSFRichTextString("Fecha Recauda."));
		cell61.setCellStyle(styleHeader);
		HSSFCell cell7 = row.createCell(i++);
		cell7.setCellValue(new HSSFRichTextString("Fecha Transf."));
		cell7.setCellStyle(styleHeader);
		HSSFCell cell71 = row.createCell(i++);
		cell71.setCellValue(new HSSFRichTextString("Remuneración"));
		cell71.setCellStyle(styleHeader);
		HSSFCell cell8 = row.createCell(i++);
		cell8.setCellValue(new HSSFRichTextString("Aporte Estimado"));
		cell8.setCellStyle(styleHeader);
		HSSFCell cell9 = row.createCell(i++);
		cell9.setCellValue(new HSSFRichTextString("Contrib. Estimada"));
		cell9.setCellStyle(styleHeader);
		HSSFCell cell10 = row.createCell(i++);
		cell10.setCellValue(new HSSFRichTextString("Pago por Actas"));
		cell10.setCellStyle(styleHeader);
		HSSFCell cell11 = row.createCell(i++);
		cell11.setCellValue(new HSSFRichTextString("Comisión OS"));
		cell11.setCellStyle(styleHeader);
		HSSFCell cell12 = row.createCell(i++);
		cell12.setCellValue(new HSSFRichTextString("Total Liq. a Terc."));
		cell12.setCellStyle(styleHeader);
		HSSFCell cell13 = row.createCell(i++);
		cell13.setCellValue(new HSSFRichTextString("Fecha Liq a Tercerizadora"));
		cell13.setCellStyle(styleHeader);
		HSSFCell cell131 = row.createCell(i++);
		cell131.setCellValue(new HSSFRichTextString("Id Terc"));
		cell131.setCellStyle(styleHeader);
	}

}
