package ar.com.ospim.afiliados.reportes;

import java.text.ParseException;
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

import ar.com.ospim.afiliados.beans.ReporteDesreguladoSinAporteBean;
import ar.com.ospim.afiliados.services.DesreguladoSinAporteServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteOPReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteDesreguladoSinAporteExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteOPReintegros.class);

	public static HSSFWorkbook generaReporteDesreguladoSinAportePeriodo(
			HttpServletRequest req, HttpServletResponse res) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAllWithHeader = getStyleAllWithBorder(wb);
		HSSFCellStyle styleHeaderWithBorder = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);

		try {
			String periodoUltimoPeriodo = null;
			String periodoUltimaVigencia = null;

			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			periodoUltimoPeriodo = ParamUtil
					.getString(req, "periodoUltimoPago");
			Date periodoDesdeMesAnnoUltimoPago = null;
			try {
				periodoDesdeMesAnnoUltimoPago = formatoDePeriodos.parse(Integer.parseInt(periodoUltimoPeriodo.split("_")[0])
						+ 1
						+ "/" + periodoUltimoPeriodo.split("_")[1]);
			} catch (Exception e) {
				periodoDesdeMesAnnoUltimoPago = null;
			}

			periodoUltimaVigencia = ParamUtil.getString(req, "periodoVigencia");
			Date periodoHastaUltimaVigencia = null;
			try {
				periodoHastaUltimaVigencia = formatoDePeriodos.parse(Integer
						.parseInt(periodoUltimaVigencia.split("_")[0])
						+ 1
						+ "/" + periodoUltimaVigencia.split("_")[1]);
			} catch (Exception e) {
				periodoHastaUltimaVigencia = null;
			}

			List<ReporteDesreguladoSinAporteBean> reporteDesreguladoSinAporteBean = DesreguladoSinAporteServiceUtil
					.getReporteDesreguladoSinAporteMonotrib(
							periodoDesdeMesAnnoUltimoPago,
							periodoHastaUltimaVigencia);

			HSSFSheet sheet = wb.createSheet("Monotributo y Servicio");
			int index = 1;

			crearHeader(sheet, styleHeaderWithBorder,styleBold,
					periodoDesdeMesAnnoUltimoPago, periodoHastaUltimaVigencia);
			for (ReporteDesreguladoSinAporteBean repo : reporteDesreguladoSinAporteBean) {				
				++index;				
				crearInfo(sheet, repo, index, styleDateWithBorder,
						styleAllWithHeader);
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
			
			List<ReporteDesreguladoSinAporteBean> reporteDesreguladoSinAporteDesBean = DesreguladoSinAporteServiceUtil
					.getReporteDesreguladoSinAporteDesreg(
							periodoDesdeMesAnnoUltimoPago,
							periodoHastaUltimaVigencia);

			HSSFSheet sheet2 = wb.createSheet("Relación de Dependencia");
			index = 1;

			crearHeader(sheet2, styleHeaderWithBorder,styleBold,
					periodoDesdeMesAnnoUltimoPago, periodoHastaUltimaVigencia);
			for (ReporteDesreguladoSinAporteBean repo : reporteDesreguladoSinAporteDesBean) {				
				++index;				
				crearInfo(sheet2, repo, index, styleDateWithBorder,
						styleAllWithHeader);
			}
			sheet2.autoSizeColumn((short) 0);
			sheet2.autoSizeColumn((short) 1);
			sheet2.autoSizeColumn((short) 2);
			sheet2.autoSizeColumn((short) 3);
			sheet2.autoSizeColumn((short) 4);
			sheet2.autoSizeColumn((short) 5);
			sheet2.autoSizeColumn((short) 6);
			sheet2.autoSizeColumn((short) 7);
			sheet2.autoSizeColumn((short) 8);
			sheet2.autoSizeColumn((short) 9);

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

	private static void crearHeader(HSSFSheet sheet, HSSFCellStyle styleHeader,HSSFCellStyle styleNegrita,
			Date ultimo_pago, Date ultima_vigencia) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		HSSFRow row_titulo = sheet.createRow(0);
		HSSFCell cell_titulo = row_titulo.createCell(0);
		cell_titulo.setCellValue(new HSSFRichTextString(
				"Reporte de desregulados Morosos con periodo de último pago "
						+ sdf.format(ultimo_pago)
						+ " y fecha de vigencia de afiliado anterior a "
						+ sdf.format(ultima_vigencia)));
		cell_titulo.setCellStyle(styleHeader);
		HSSFRow row = sheet.createRow(1);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell.setCellStyle(styleHeader);
		HSSFCell cell00 = row.createCell(1);
		cell00.setCellValue(new HSSFRichTextString("Inte"));
		cell00.setCellStyle(styleHeader);
		HSSFCell cell1 = row.createCell(2);
		cell1.setCellValue(new HSSFRichTextString("Apellido"));
		cell1.setCellStyle(styleHeader);
		HSSFCell cell2 = row.createCell(3);
		cell2.setCellValue(new HSSFRichTextString("Nombre"));
		cell2.setCellStyle(styleHeader);
		HSSFCell cell3 = row.createCell(4);
		cell3.setCellValue(new HSSFRichTextString("Fecha Vigencia"));
		cell3.setCellStyle(styleHeader);
		HSSFCell cell4 = row.createCell(5);
		cell4.setCellValue(new HSSFRichTextString("Plan"));
		cell4.setCellStyle(styleHeader);
		HSSFCell cell5 = row.createCell(6);
		cell5.setCellValue(new HSSFRichTextString("Seccional"));
		cell5.setCellStyle(styleHeader);
		HSSFCell cell6 = row.createCell(7);
		cell6.setCellValue(new HSSFRichTextString("Categoria"));
		cell6.setCellStyle(styleHeader);
		HSSFCell cell7 = row.createCell(8);
		cell7.setCellValue(new HSSFRichTextString("Empresa"));
		cell7.setCellStyle(styleHeader);
		HSSFCell cell8 = row.createCell(9);
		cell8.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell8.setCellStyle(styleHeader);
	}

	private static void crearInfo(HSSFSheet sheet,
			ReporteDesreguladoSinAporteBean repo, int index,
			HSSFCellStyle styleDate, HSSFCellStyle styleAll) {

		HSSFRow row = sheet.createRow(index);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(repo.getCuil()));
		cell.setCellStyle(styleAll);

		HSSFCell cell00 = row.createCell(1);
		cell00.setCellValue(new HSSFRichTextString(String.valueOf(repo.getInte())));
		cell00.setCellStyle(styleAll);
		
		HSSFCell cell1 = row.createCell(2);
		cell1.setCellValue(new HSSFRichTextString(repo.getApellido()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = row.createCell(3);
		cell2.setCellStyle(styleAll);
		cell2.setCellValue(new HSSFRichTextString(repo.getNombre()));

		HSSFCell cell3 = row.createCell(4);
		cell3.setCellStyle(styleAll);
		cell3.setCellValue(new HSSFRichTextString(repo
				.getFecha_vigencia_as_String()));

		HSSFCell cell4 = row.createCell(5);
		cell4.setCellStyle(styleAll);
		cell4.setCellValue(new HSSFRichTextString(repo.getPlan()));

		HSSFCell cell5 = row.createCell(6);
		cell5.setCellStyle(styleAll);
		cell5.setCellValue(new HSSFRichTextString(repo.getSeccional()));

		HSSFCell cell6 = row.createCell(7);
		cell6.setCellStyle(styleAll);
		cell6.setCellValue(new HSSFRichTextString(repo.getCategoria()));

		HSSFCell cell7 = row.createCell(8);
		cell7.setCellStyle(styleAll);
		cell7.setCellValue(new HSSFRichTextString(repo.getRazon_social()));

		HSSFCell cell8 = row.createCell(9);
		cell8.setCellStyle(styleAll);
		cell8.setCellValue(new HSSFRichTextString(repo.getTercerizadora()));
	}
}