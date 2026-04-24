package ar.com.ospim.farmacia.action;

import java.math.BigDecimal;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteReintegrosFarmacia extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteReintegrosFarmacia.class);

	public static HSSFWorkbook generaReporteReintegros(HttpServletRequest req,
			HttpServletResponse res) {
		int idReporte = ParamUtil.getInteger(req, "idReporte");		

		HSSFWorkbook wb = new HSSFWorkbook();
		
		try {
			List<ReporteOrdenPagoReintegrosFarmacia> list = null;
			if (idReporte != 0) {
				HSSFSheet sheet = wb.createSheet("Hoja 1");
				try {
					list = ReintegroActionUtil
							.getReintegrosFromReporteId(idReporte);

				} catch (NoSuchReintegroEntryException nsree) {
					list = null;
				}
				
				HSSFPrintSetup ps = sheet.getPrintSetup();
				sheet.setAutobreaks(true);
				ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
				ps.setFitHeight((short) 0);
				ps.setFitWidth((short) 1);

				HSSFCellStyle styleAll = getStyleAll(wb);
				HSSFCellStyle styleBold = getStyleBold(wb);
				HSSFCellStyle styleDate = getStyleDate(wb);
				HSSFCellStyle styleHeaderEnca = getStyleBoldAligned(wb,
						HorizontalAlignment.RIGHT);
				
				if (list == null || list.isEmpty()) {
					return wb;
				}

				int index = 0;
				HSSFRow rowHeader = sheet.createRow(index);

				BigDecimal total = new BigDecimal("0");
				BigDecimal totalPP = new BigDecimal("0");
				
				for (ReporteOrdenPagoReintegrosFarmacia repo : list) {
					index++;
					crearHeader(sheet, index, repo, repo.getReintegro(),
							styleBold, styleAll, styleDate);
					index++;
					sheet.createRow(index);
					index++;
					crearHeaderPrestacion(sheet, index, styleBold, styleHeaderEnca);
					for (ReintegroMedicamentoItem rp : repo.getReintegro()
							.getMedicamentos()) {
						index++;
						crearFilaInfoPrestacion(sheet, index, rp, styleAll,
								styleDate);
					}
					index++;
					HSSFRow rowSubtotal = sheet.createRow(index);
					HSSFCell cellSubtotalTexo = rowSubtotal.createCell(3);
					cellSubtotalTexo.setCellValue(new HSSFRichTextString(
							"Subtotal"));
					cellSubtotalTexo.setCellStyle(styleBold);

					HSSFCell cellSubtotalPPValor = rowSubtotal.createCell(4);
					cellSubtotalPPValor.setCellValue(repo.getReintegro()
							.getPrecioPublicoTotal().doubleValue());
					cellSubtotalPPValor.setCellStyle(styleAll);
					
					HSSFCell cellSubtotalValor = rowSubtotal.createCell(5);
					cellSubtotalValor.setCellValue(repo.getReintegro()
							.getImporteTotal().doubleValue());
					cellSubtotalValor.setCellStyle(styleAll);

					index++;
					sheet.createRow(index);
					total = total.add(repo.getTotal());
					totalPP = totalPP.add(repo.getReintegro().getPrecioPublicoTotal());
				}
				index++;
				sheet.createRow(index);
				index++;
				HSSFRow rowTotal = sheet.createRow(index);

				HSSFCell cell = rowTotal.createCell(3);
				cell.setCellValue(new HSSFRichTextString("Total"));
				cell.setCellStyle(styleBold);

				HSSFCell cell2 = rowTotal.createCell(4);
				cell2.setCellValue(totalPP.doubleValue());
				cell2.setCellStyle(styleAll);

				HSSFCell cell1 = rowTotal.createCell(5);
				cell1.setCellValue(total.doubleValue());
				cell1.setCellStyle(styleAll);

				index++;
				sheet.createRow(index);
				index++;

				sheet.autoSizeColumn((short) 0);
				sheet.autoSizeColumn((short) 1);
				sheet.autoSizeColumn((short) 2);
				sheet.autoSizeColumn((short) 3);
				sheet.autoSizeColumn((short) 4);
				sheet.autoSizeColumn((short) 5);
			}			
					
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		}		
						
		return wb;
		
	}

	private static void crearHeaderPrestacion(HSSFSheet sheet, int index,
			HSSFCellStyle styleBold, HSSFCellStyle styleHeaderEnca) {
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell = rowHeader.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Medicamento                  "));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("N. Receta"));
		cell1.setCellStyle(styleHeaderEnca);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Cant."));
		cell2.setCellStyle(styleHeaderEnca);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("%"));
		cell3.setCellStyle(styleHeaderEnca);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Tot. P. Pub."));
		cell4.setCellStyle(styleHeaderEnca);

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Tot. Cober."));
		cell5.setCellStyle(styleHeaderEnca);
	}

	private static void crearFilaInfoPrestacion(HSSFSheet sheet, int index,
			ReintegroMedicamentoItem rp, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {
		HSSFRow row = sheet.createRow(index);

		HSSFCell cell = row.createCell(0);
		String desc = rp.getMedicamento().getNombre().trim() + " "
				+ rp.getMedicamento().getPresentacion().trim();
		cell.setCellValue(new HSSFRichTextString(desc.length() < 35 ? desc
				: desc.substring(0, 35)));
		cell.setCellStyle(styleDate);

		HSSFCell cell1 = row.createCell(1);

		cell1.setCellValue(rp.getNumeroReceta());
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(rp.getCantidad());
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(rp.getTotalCobertura().doubleValue());
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(null != rp.getPrecio_al_publico() ? (rp
				.getPrecio_al_publico().multiply(new BigDecimal(rp
				.getCantidad()))).doubleValue() : 0);
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue((((null != rp.getImporteCoberturaOspim() ? rp
				.getImporteCoberturaOspim() : BigDecimal.ZERO).add(null != rp
				.getImporteCoberturaAmtima() ? rp.getImporteCoberturaAmtima()
				: BigDecimal.ZERO)).multiply(new BigDecimal(rp.getCantidad())))
				.doubleValue());
		cell5.setCellStyle(styleAll);
	}

	private static void crearHeader(HSSFSheet sheet, int index,
			ReporteOrdenPagoReintegrosFarmacia repo, ReintegroMedicamento reintegro,
			HSSFCellStyle styleBold, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Periodo"));
		cell0.setCellStyle(styleBold);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(reintegro.getPeriodo());
		cell1.setCellStyle(styleDate);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Reintegro N° "
				+ reintegro.getId_reintegroString()));
		cell2.setCellStyle(styleBold);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Afiliado"));
		cell3.setCellStyle(styleBold);

		String cuilTitu = repo.getAfiliado().getCuil_titular();
		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(cuilTitu + " - "
				+ repo.getAfiliado().getApeNombre()));
		cell4.setCellStyle(styleAll);

		sheet.addMergedRegion(new CellRangeAddress(index, // first row (0-based)
				index, // last row (0-based)
				4, // first column (0-based)
				6 // last column (0-based)
				));

	}

}
