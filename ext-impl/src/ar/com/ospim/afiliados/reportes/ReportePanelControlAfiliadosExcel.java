package ar.com.ospim.afiliados.reportes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.afiliados.reportes.beans.PanelControlAfiliado;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReportePanelControlAfiliadosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePanelControlAfiliadosExcel.class);

	public static HSSFWorkbook generaReportePanelControlAfiliados(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat formatHoy = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		ReportesAfiliadoServiceImpl reporteService = new ReportesAfiliadoServiceImpl();

		try {
			String periodoDesde = ParamUtil.getString(req, "periodoDesde");
			String periodoHasta = ParamUtil.getString(req, "periodoHasta");

			Date fechaIni = format.parse(Integer.parseInt(periodoDesde
					.split("_")[0]) + 1 + "-" + periodoDesde.split("_")[1]);
			Date fechaHasta = format.parse(Integer.parseInt(periodoHasta
					.split("_")[0]) + 1 + "-" + periodoHasta.split("_")[1]);

			List<PanelControlAfiliado> reporte = reporteService
					.getReportePanelControlAfiliadoTitBenef(fechaIni,
							fechaHasta);
			String fecha = formatHoy
					.format(new Date(System.currentTimeMillis()));
			String periodoDesdeString = format.format(fechaIni);
			String periodoHastaString = format.format(fechaHasta);

			HSSFWorkbook reporteFinal = generarReporteTitBenef(fecha,
					periodoDesdeString, periodoHastaString, reporte);
			
			List<PanelControlAfiliado> reporteMolDesreg = reporteService
					.getReportePanelControlAfiliadoMoliDesreg(fechaIni,
							fechaHasta);
			reporteFinal = generarReporteMoliDesreg(reporteFinal, fecha,
					periodoDesdeString, periodoHastaString, reporteMolDesreg);
			
			List<PanelControlAfiliado> reporteProv = reporteService
					.getReportePanelControlProvincia(fechaIni, fechaHasta);
			
			reporteFinal = generarReporteProvincia(reporteFinal, fecha,
					periodoDesdeString, periodoHastaString, reporteProv);
			
			List<PanelControlAfiliado> reportePlan = reporteService
					.getReportePanelControlPlan(fechaIni, fechaHasta);
			
			reporteFinal = generarReportePlan(reporteFinal, fecha,
					periodoDesdeString, periodoHastaString, reportePlan);
			
			List<PanelControlAfiliado> reporteProm = reporteService
					.getReportePanelControlPromedio(fechaIni, fechaHasta);
			
			reporteFinal = generarReportePromedio(reporteFinal, fecha,
					periodoDesdeString, periodoHastaString, reporteProm);
			
			return reporteFinal;
		} catch (Exception e) {
			_log.error("Error al generar reporte inconsistencias", e);
			return null;
		}
	}	

	private static int createHeaderPanelControl(HSSFWorkbook wb,
			HSSFSheet sheet, String fechaHoy, String periodoDesde,
			String periodoHasta, String tipo) {
		int index = 0;

		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		HSSFCellStyle styleHeaderEnca1 = getStyleBold(wb, 10);

		HSSFRow row1 = sheet.createRow(index);

		HSSFCell cell1 = row1.createCell(0);
		if (tipo.equals("titu")) {
			cell1.setCellValue(new HSSFRichTextString(
					"Progresión de titulares y beneficiarios vigentes "
							+ "desde " + periodoDesde + " al " + periodoHasta
							+ "- " + fechaHoy));
		} else if (tipo.equals("moli")) {
			cell1.setCellValue(new HSSFRichTextString(
					"Progresión de molineros y desregulados vigentes "
							+ "desde " + periodoDesde + " al " + periodoHasta
							+ "- " + fechaHoy));
		} else if (tipo.equals("prov")) {
			cell1.setCellValue(new HSSFRichTextString(
					"Progresión de titulares y beneficiarios por provincia "
							+ "desde " + periodoDesde + " al " + periodoHasta
							+ "- " + fechaHoy));
		} else if (tipo.equals("plan")) {
			cell1.setCellValue(new HSSFRichTextString(
					"Progresión de titulares y beneficiarios por plan OMINT "
							+ "desde " + periodoDesde + " al " + periodoHasta
							+ "- " + fechaHoy));
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
		
		cell1.setCellStyle(styleHeaderEnca1);

		HSSFRow row2 = sheet.createRow(++index);
		HSSFCell cell21 = row2.createCell(0);

		HSSFRow row3 = sheet.createRow(++index);

		HSSFCell cell31 = row3.createCell(0);
		cell31.setCellValue(new HSSFRichTextString("Período"));
		cell31.setCellStyle(styleHeaderEnca3);

		HSSFCell cell311 = row3.createCell(1);
		cell311.setCellValue(new HSSFRichTextString("Descripción"));
		cell311.setCellStyle(styleHeaderEnca3);

		HSSFCell cell32 = row3.createCell(2);
		cell32.setCellValue(new HSSFRichTextString("Titulares"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(3);
		cell33.setCellValue(new HSSFRichTextString("Beneficiarios"));
		cell33.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row3.createCell(4);
		cell34.setCellValue(new HSSFRichTextString("Total"));
		cell34.setCellStyle(styleHeaderEnca3);

		return ++index;
	}
	
	private static HSSFWorkbook generarReporteTitBenef(String fechaHoy,
			String fechaDesde, String fechaHasta,
			List<PanelControlAfiliado> lista) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Titu-Benef");
		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		int indexColumn = 0;
		try {

			int index = createHeaderPanelControl(wb, sheet, fechaHoy, fechaDesde,
					fechaHasta, "titu");
			indexColumn = 0;
			for (PanelControlAfiliado rep : lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(indexColumn++);
				cell0.setCellValue(new HSSFRichTextString(format.format(rep
						.getPeriodo())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(indexColumn++);
				cell1.setCellValue(new HSSFRichTextString(rep.getDescripcion()));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(indexColumn++);
				cell2.setCellValue(rep.getTitulares());
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(indexColumn++);
				cell3.setCellValue(rep.getAdherentes());
				cell3.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(indexColumn++);
				cell4.setCellValue(rep.getAdherentes() + rep.getTitulares());
				cell4.setCellStyle(styleAllWithBorder);
				indexColumn = 0;
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}
	
	
	private static int createHeaderPanelControlPromedio(HSSFWorkbook wb,
			HSSFSheet sheet, String fechaHoy, String periodoDesde,
			String periodoHasta) {
		int index = 0;

		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		HSSFCellStyle styleHeaderEnca1 = getStyleBold(wb, 10);

		HSSFRow row1 = sheet.createRow(index);

		HSSFCell cell1 = row1.createCell(0);
		
		cell1.setCellValue(new HSSFRichTextString(
					"Progresión de promedios de sueldos declarados por la AFIP "
							+ "desde " + periodoDesde + " al " + periodoHasta
							+ "- " + fechaHoy));
		
		cell1.setCellStyle(styleHeaderEnca1);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

		HSSFRow row2 = sheet.createRow(++index);
		HSSFCell cell21 = row2.createCell(0);

		HSSFRow row3 = sheet.createRow(++index);

		HSSFCell cell31 = row3.createCell(0);
		cell31.setCellValue(new HSSFRichTextString("Período"));
		cell31.setCellStyle(styleHeaderEnca3);

		HSSFCell cell311 = row3.createCell(1);
		cell311.setCellValue(new HSSFRichTextString("Cant. Afiliados"));
		cell311.setCellStyle(styleHeaderEnca3);

		HSSFCell cell32 = row3.createCell(2);
		cell32.setCellValue(new HSSFRichTextString("Promedio"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(3);
		cell33.setCellValue(new HSSFRichTextString("Prom. Ajustado"));
		cell33.setCellStyle(styleHeaderEnca3);

		return ++index;
	}

	private static HSSFWorkbook generarReporteMoliDesreg(HSSFWorkbook wb,
			String fechaHoy, String fechaDesde, String fechaHasta,
			List<PanelControlAfiliado> lista) {

		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Molineros-Desreg");
		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		int indexColumn = 0;
		try {

			int index = createHeaderPanelControl(wb, sheet, fechaHoy, fechaDesde,
					fechaHasta,"moli");
			indexColumn = 0;
			for (PanelControlAfiliado rep : lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(indexColumn++);
				cell0.setCellValue(new HSSFRichTextString(format.format(rep
						.getPeriodo())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(indexColumn++);
				cell1.setCellValue(new HSSFRichTextString(rep.getDescripcion()));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(indexColumn++);
				cell2.setCellValue(rep.getTitulares());
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(indexColumn++);
				cell3.setCellValue(rep.getAdherentes());
				cell3.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(indexColumn++);
				cell4.setCellValue(rep.getAdherentes() + rep.getTitulares());
				cell4.setCellStyle(styleAllWithBorder);
				indexColumn = 0;
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}
	
	private static HSSFWorkbook generarReporteProvincia(HSSFWorkbook wb,
			String fechaHoy, String fechaDesde, String fechaHasta,
			List<PanelControlAfiliado> lista) {

		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Provincia");
		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		int indexColumn = 0;
		try {

			int index = createHeaderPanelControl(wb, sheet, fechaHoy, fechaDesde,
					fechaHasta,"prov");
			indexColumn = 0;
			for (PanelControlAfiliado rep : lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(indexColumn++);
				cell0.setCellValue(new HSSFRichTextString(format.format(rep
						.getPeriodo())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(indexColumn++);
				cell1.setCellValue(new HSSFRichTextString(rep.getDescripcion()));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(indexColumn++);
				cell2.setCellValue(rep.getTitulares());
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(indexColumn++);
				cell3.setCellValue(rep.getAdherentes());
				cell3.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(indexColumn++);
				cell4.setCellValue(rep.getAdherentes() + rep.getTitulares());
				cell4.setCellStyle(styleAllWithBorder);
				indexColumn = 0;
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}
	
	private static HSSFWorkbook generarReportePlan(HSSFWorkbook wb,
			String fechaHoy, String fechaDesde, String fechaHasta,
			List<PanelControlAfiliado> lista) {

		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Plan");
		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		int indexColumn = 0;
		try {

			int index = createHeaderPanelControl(wb, sheet, fechaHoy, fechaDesde,
					fechaHasta,"plan");
			indexColumn = 0;
			for (PanelControlAfiliado rep : lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(indexColumn++);
				cell0.setCellValue(new HSSFRichTextString(format.format(rep
						.getPeriodo())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(indexColumn++);
				cell1.setCellValue(new HSSFRichTextString(rep.getDescripcion()));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(indexColumn++);
				cell2.setCellValue(rep.getTitulares());
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(indexColumn++);
				cell3.setCellValue(rep.getAdherentes());
				cell3.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(indexColumn++);
				cell4.setCellValue(rep.getAdherentes() + rep.getTitulares());
				cell4.setCellStyle(styleAllWithBorder);
				indexColumn = 0;
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}
	
	private static HSSFWorkbook generarReportePromedio(HSSFWorkbook wb,
			String fechaHoy, String fechaDesde, String fechaHasta,
			List<PanelControlAfiliado> lista) {

		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Promedio Sueldo AFIP");
		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		int indexColumn = 0;
		try {

			int index = createHeaderPanelControlPromedio(wb, sheet, fechaHoy, fechaDesde,
					fechaHasta);
			indexColumn = 0;
			for (PanelControlAfiliado rep : lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(indexColumn++);
				cell0.setCellValue(new HSSFRichTextString(format.format(rep
						.getPeriodo())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(indexColumn++);
				cell1.setCellValue(rep.getTitulares());
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(indexColumn++);
				cell2.setCellValue(rep.getPromedio().setScale(2).doubleValue());
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(indexColumn++);
				cell3.setCellValue(rep.getPromedio().divide(new BigDecimal(1.5), 2, RoundingMode.HALF_UP).doubleValue());
				cell3.setCellStyle(styleAllWithBorder);				
				indexColumn = 0;
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}

}
