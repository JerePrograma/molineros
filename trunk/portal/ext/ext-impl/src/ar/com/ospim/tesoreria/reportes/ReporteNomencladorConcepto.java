package ar.com.ospim.tesoreria.reportes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class ReporteNomencladorConcepto extends ReporteXLS {

	public static HSSFWorkbook generaNomencladorConceptos(
			HttpServletRequest req, HttpServletResponse res) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleNumber6DWithBorder(wb);

		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		Calendar desdeEjercicio = DateUtils.getDesdeEjercicioActual();
		Calendar hastaEjercicio = DateUtils.getHastaEjercicioActual();
		String ejercicio = req.getParameter("ejercicio");
		if (StringUtils.isNotBlank(ejercicio)) {
			String dd = ejercicio.split("-")[0];
			String hta = ejercicio.split("-")[1];
			desdeEjercicio.set(Calendar.YEAR, Integer.valueOf(dd));
			hastaEjercicio.set(Calendar.YEAR, Integer.valueOf(hta));
		}

		List<PrestacionConcepto> prestacionesConceptos = ConceptoServiceUtil
				.getPrestacionesConceptos(desdeEjercicio, hastaEjercicio);

		crearHeader(wb, sheet, styleHeader, desdeEjercicio.getTime(),
				hastaEjercicio.getTime());

		int i = 1;
		for (PrestacionConcepto pc : prestacionesConceptos) {
			i++;
			HSSFRow row = sheet.createRow(i);
			HSSFCell cellCodigo = row.createCell(0);
			cellCodigo.setCellStyle(styleAll);
			cellCodigo.setCellValue(new HSSFRichTextString(pc.getPrestacion()
					.getCodigo()));

			HSSFCell cellDesc = row.createCell(1);
			cellDesc.setCellStyle(styleAll);
			cellDesc.setCellValue(new HSSFRichTextString(pc.getPrestacion()
					.getDescripcion()));

			HSSFCell cellCA = row.createCell(2);
			cellCA.setCellStyle(styleMoney);
			if (pc.getCoeficienteGastos() != null) {
				cellCA.setCellValue(pc.getCoeficienteGastos().doubleValue());
			}

			HSSFCell cellCI = row.createCell(3);
			cellCI.setCellStyle(styleMoney);
			if (pc.getCoeficienteHonorarios() != null) {
				cellCI.setCellValue(pc.getCoeficienteHonorarios().doubleValue());
			}

			HSSFCell cellHA = row.createCell(4);
			cellHA.setCellStyle(styleAll);
			cellHA.setCellValue(new HSSFRichTextString(pc
					.getHonorariosAmbulatorio().getDescripcion()));

			HSSFCell cellHI = row.createCell(5);
			cellHI.setCellStyle(styleAll);
			cellHI.setCellValue(new HSSFRichTextString(pc
					.getHonorariosInternacion().getDescripcion()));

			HSSFCell cellGA = row.createCell(6);
			cellGA.setCellStyle(styleAll);
			cellGA.setCellValue(new HSSFRichTextString(pc
					.getGastosAmbulatorio().getDescripcion()));

			HSSFCell cellGI = row.createCell(7);
			cellGI.setCellStyle(styleAll);
			cellGI.setCellValue(new HSSFRichTextString(pc
					.getGastosInternacion().getDescripcion()));
		}

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

	private static void crearHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader, Date validoDesde, Date validoHasta) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		HSSFRow rowHeader = sheet.createRow(0);
		HSSFCell cellHeader = rowHeader.createCell(0);
		cellHeader.setCellValue(new HSSFRichTextString(
				"Nomenclador - Conceptos - Validos desde: "
						+ format.format(validoDesde) + " hasta: "
						+ format.format(validoHasta)));
		cellHeader.setCellStyle(styleHeader);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

		HSSFRow row = sheet.createRow(1);

		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Codigo"));
		cell.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(1);
		cellAcreed.setCellValue(new HSSFRichTextString("Descripcion"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellCoefAmb = row.createCell(2);
		cellCoefAmb.setCellValue(new HSSFRichTextString("Coef. Gastos"));
		cellCoefAmb.setCellStyle(styleHeader);

		HSSFCell cellCoefInt = row.createCell(3);
		cellCoefInt.setCellValue(new HSSFRichTextString("Coef. Honorarios"));
		cellCoefInt.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(4);
		cellRaz.setCellValue(new HSSFRichTextString("Honorarios Ambulatorio"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(5);
		cell3.setCellValue(new HSSFRichTextString("Honorarios Internacion"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(6);
		cell4.setCellValue(new HSSFRichTextString("Gastos Ambulatorio"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(7);
		cell5.setCellValue(new HSSFRichTextString("Gastos Internacion"));
		cell5.setCellStyle(styleHeader);

	}

}
