package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteCajaChicaExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCajaChicaExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		int idCajaChica = ParamUtil.getInteger(req, "id_caja_chica");
		int entidad = ParamUtil.getInteger(req, "entidad");
		
		try {
			Date fechaIni = null;
			Date fechaFin = null;
			Date fechaImpre = null;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			try {
				String fechaIniDia = ParamUtil.getString(req,
						"fechaDesdeCajaChicaDia");
				String fechaIniMes = ParamUtil.getString(req,
						"fechaDesdeCajaChicaMes");
				fechaIniMes = String
						.valueOf(Integer.valueOf(fechaIniMes) + 1);
				String fechaIniAnio = ParamUtil.getString(req,
						"fechaDesdeCajaChicaAnio");
				fechaIni = format.parse(fechaIniDia + "-" + fechaIniMes
						+ "-" + fechaIniAnio);

			} catch (Exception e) {
				fechaIni = new Date();
			}


			try {
				String fechaFinDia = ParamUtil.getString(req,
						"fechaHastaCajaChicaDia");
				String fechaFinMes = ParamUtil.getString(req,
						"fechaHastaCajaChicaMes");
				fechaFinMes = String
						.valueOf(Integer.valueOf(fechaFinMes) + 1);
				String fechaFinAnio = ParamUtil.getString(req,
						"fechaHastaCajaChicaAnio");
				fechaFin = format.parse(fechaFinDia + "-" + fechaFinMes
						+ "-" + fechaFinAnio);

			} catch (Exception e) {
				fechaFin = new Date();
			}

			
			List<ComprobanteCajaChica>reporte =CajaChicaServiceUtil.reporteCajaChica(entidad, idCajaChica, fechaFin);
			
			List<ComprobanteCajaChica>listPrn = new ArrayList<ComprobanteCajaChica>();
			Double saldoInicial=0D;
			for(ComprobanteCajaChica c:reporte){
				if(c.getFechaEmision().compareTo(fechaIni)<0){
					saldoInicial+= c.getImporte().doubleValue();
				}else{
					listPrn.add(c);
				}
			}
			
			return generarReporte(fechaIni, fechaFin, listPrn, saldoInicial,entidad);
			
			
		} catch (Exception e) {
			_log.error("Error al generar reporte caja chica", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ComprobanteCajaChica> reporte,Double saldoInicial,int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
		// styleHeaderLeft.setBorderLeft(BorderStyle.THIN);
		// styleHeaderLeft.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);

		// styleHeaderRight.setBorderRight(BorderStyle.THIN);
		// styleHeaderRight.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
		// styleHeader.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		// styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
		// styleFechaLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		// styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
		// styleFechaLeftTop.setBorderLeft(BorderStyle.THIN);
		// styleFechaLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		// styleMoneyRightTop.setBorderRight(BorderStyle.THIN);
		// styleMoneyRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);
		// styleMoneyRightBold.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
/*		
		if (entidad == WebKeysGlobal.OSPIM) {
			i = crearHeaderPrincipal(wb, sheet, 7, entidad);
		}
		if (entidad == WebKeysGlobal.UOMA) {
			i = crearHeaderPrincipalUoma(wb, sheet, 7, fechaImpresion);
		}
*/
		
		i = createTitulosHeader(wb, sheet, i, entidad, fechaIni, fechaFin);

		
		if (entidad == WebKeysGlobal.OSPIM) {
			i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		} else {
			
			i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
			
//			i = generarHeaderUoma(sheet, i, styleHeader, styleHeaderLeft,
//					styleHeaderRight, wb);
		}

		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		boolean mostrarFecha = true;
		Date auxDate = null;
		BigDecimal totalParcial = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		
		String comprobante = new String();
		
		Double saldo =saldoInicial;
		
		
		HSSFRow row = sheet.createRow(i);
		HSSFCell createCell = row.createCell(6);
		createCell.setCellValue(new HSSFRichTextString("Saldo Inicial:"));
		createCell.setCellStyle(styleAll);

		HSSFCell createCell2 = row.createCell(7);
		createCell2.setCellValue(saldo);
		createCell2.setCellStyle(styleMoneyRightBold);
		i++;
		
		
		Map<String, Double> map=new HashMap<String, Double>();
		
		for (ComprobanteCajaChica repo : reporte) {
			Double importeAgrupado = map.get(repo.getConceptos().get(0).getConceptoComprobante().getDescripcion() );
			if(importeAgrupado==null){
				map.put(repo.getConceptos().get(0).getConceptoComprobante().getDescripcion(), repo.getImporteComprobante().doubleValue());
			}else{
				map.put(repo.getConceptos().get(0).getConceptoComprobante().getDescripcion(), repo.getImporteComprobante().doubleValue()+importeAgrupado);
			}
			if (entidad == WebKeysGlobal.OSPIM) {
				saldo += repo.getImporteComprobante().doubleValue();
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop, saldo);
			} else {
				saldo += repo.getImporteComprobante().doubleValue();
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop, saldo);
			}
		}
		
		i++;
		row = sheet.createRow(i);
		HSSFCell createCell00 = row.createCell(5);
		createCell00.setCellValue(new HSSFRichTextString("Agrupado por Conceptos:"));
		createCell00.setCellStyle(getStyleBoldUnderlined(wb));
		i++;
		
		
		Iterator it = map.entrySet().iterator();
		Integer xi=0;
		while (it.hasNext()) {
		   row = sheet.createRow(i);
		   Map.Entry e = (Map.Entry)it.next();
		   HSSFCell cell2 = row.createCell(5);
		   cell2.setCellValue( new HSSFRichTextString(e.getKey().toString() )  );
		   cell2.setCellStyle(styleAllTop);
			
		   HSSFCell cell3 = row.createCell(6);
		   cell3.setCellValue(Double.parseDouble(e.getValue().toString()));
		   cell3.setCellStyle(styleMoneyRight);
		   i++;
		   
		}
		
		

		if (entidad == WebKeysGlobal.OSPIM) {
			sheet.autoSizeColumn((short) 0);
			sheet.setColumnWidth(1, 8200);
			sheet.setColumnWidth(2, 10200);
			sheet.setColumnWidth(3, 10200);
			sheet.setColumnWidth(4, 10200);
			
			sheet.setColumnWidth(5, 8200);
			sheet.setColumnWidth(6, 8200);
			
			sheet.autoSizeColumn((short) 7);
			sheet.autoSizeColumn((short) 8);
		} else {
			sheet.autoSizeColumn((short) 0);
			sheet.setColumnWidth(1, 8200);
			sheet.setColumnWidth(2, 10200);
			sheet.setColumnWidth(3, 10200);
			sheet.setColumnWidth(4, 10200);
			
			sheet.setColumnWidth(5, 8200);
			sheet.setColumnWidth(6, 8200);
			
			sheet.autoSizeColumn((short) 7);
			sheet.autoSizeColumn((short) 8);
		}
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ComprobanteCajaChica repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop, Double saldo) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(repo.getFechaEmision() );
		cell0.setCellStyle(styleFechaLeftTop);
		

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(repo.getTipoComprobante() + " " + repo.getLetraComprobante() + " " + 
				repo.getPtoVenta() + " - " + repo.getNroComprobante()));
		cell1.setCellStyle(styleAll);
		
		
		HSSFCell cell20 = row.createCell(2);
		cell20.setCellValue(new HSSFRichTextString(repo.getAcreedorEmpresa().getCuit()));
		cell20.setCellStyle(styleAllTop);
		
		HSSFCell cell21 = row.createCell(3);
		cell21.setCellValue(new HSSFRichTextString(repo.getAcreedorEmpresa().getRazon_soc()));
		cell21.setCellStyle(styleAllTop);
		

		HSSFCell cell22 = row.createCell(4);
		cell22.setCellValue(new HSSFRichTextString(repo.getSeccional().getDescripcion()));
		cell22.setCellStyle(styleAllTop);
		
		HSSFCell cell2 = row.createCell(5);
		cell2.setCellValue(new HSSFRichTextString(repo.getConceptos().get(0).getConceptoComprobante().getDescripcion() ));
		cell2.setCellStyle(styleAllTop);
		
		
		HSSFCell cell24 = row.createCell(6);
		cell24.setCellValue(new HSSFRichTextString(repo.getObservaciones()));
		cell24.setCellStyle(styleAllTop);
		
		
		HSSFCell cell3 = row.createCell(7);
		cell3.setCellValue(repo.getImporte().doubleValue());
		cell3.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4 = row.createCell(8);
		cell4.setCellValue(saldo);
		cell4.setCellStyle(styleMoneyRight);
		
		
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Comprobante"));
		cell1.setCellStyle(styleHeader);
		
		
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("CUIT"));
		cell2.setCellStyle(styleHeader);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Razón Social"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell5 = row.createCell(4);
		cell5.setCellValue(new HSSFRichTextString("Seccional"));
		cell5.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(5);
		cell4.setCellValue(new HSSFRichTextString("Concepto"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell40 = row.createCell(6);
		cell40.setCellValue(new HSSFRichTextString("Observaciones"));
		cell40.setCellStyle(styleHeader);
		
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Importe"));
		cell7.setCellStyle(styleHeaderL);

		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Saldo"));
		cell8.setCellStyle(styleHeaderL);
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, int entidad, Date fechaIni, Date fechaFin) {

		String tituloReporte = "Reporte de Caja Chica";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		if (entidad == WebKeysGlobal.UOMA) {
			cell.setCellValue(new HSSFRichTextString(tituloReporte
					.toUpperCase()));
			cell.setCellStyle(getStyleBoldUnderlinedHeader(wb, 12));
		} else {
			cell.setCellValue(new HSSFRichTextString(tituloReporte));
			cell.setCellStyle(getStyleBoldUnderlined(wb));
		}

		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;

		HSSFRow rowTitulo2 = sheet.createRow(fila);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;

		return fila;
	}
}
