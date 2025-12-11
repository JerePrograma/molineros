package ar.com.ospim.liquidaciones.ordenespago.reportes;

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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoProtesis;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

public class ReporteOPReintegros extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteOPReintegros.class);

	public static HSSFWorkbook generaReporteOPReintegros(
			HttpServletRequest req, HttpServletResponse res) {
		int idLista = ParamUtil.getInteger(req, "idLista");	
		Integer idOP=0;
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			List<ReporteOrdenPagoReintegros> list = null;
			try {
				if (idLista != 0) {
					list = OrdenPagoServiceUtil.getReintegrosFromListaId(idLista);
				} 
			} catch (NoSuchReintegroEntryException nsree) {
				list = null; 
			}
			
			try {
				if (!list.isEmpty()) {
					idOP=ReintegroServiceUtil.getIdOPReintegroLista(idLista);
				} 
			} catch (Exception e) {
				
			}
			
			
			HSSFSheet sheet = wb.createSheet("Hoja 1");

			HSSFPrintSetup ps = sheet.getPrintSetup();
			sheet.setAutobreaks(true);
			ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
			ps.setFitHeight((short)0);
			ps.setFitWidth((short)1);
			
			HSSFCellStyle styleAll = getStyleAll(wb);
			HSSFCellStyle styleBold = getStyleBold(wb);
			HSSFCellStyle styleDate = getStyleDate(wb);

			if (list == null || list.isEmpty()) {
				return wb;
			}

			int index = 0;
			HSSFRow rowHeader = sheet.createRow(index);

			HSSFCell cell0H = rowHeader.createCell(0);
			cell0H.setCellValue(new HSSFRichTextString("Seccional"));
			cell0H.setCellStyle(styleBold);

			HSSFCell cell1H = rowHeader.createCell(1);
			cell1H.setCellValue(new HSSFRichTextString(list.get(0)
					.getReintegro().getSeccional().getId()
					+ " - "
					+ list.get(0).getReintegro().getSeccional()
							.getDescripcion()));
			cell1H.setCellStyle(styleAll);

			sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 2));

			HSSFCell cell5H = rowHeader.createCell(5);
			cell5H.setCellValue(list.get(0).getReintegro().getFecha());
			cell5H.setCellStyle(styleDate);

			index++;
			sheet.createRow(index);

			BigDecimal total = new BigDecimal("0");
			for (ReporteOrdenPagoReintegros repo : list) {
				int idxCuota = 0;
				String NroComp = ""; 
				
				if (repo.getReintegro().getDetalleCuota() != null) {
					NroComp =  (repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_tipo() !=null && !"null".equals(repo.getReintegro().getDetalleCuota().get(0).getCompro_a_debitar_tipo())?
							repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_tipo() : "") + " " + 
							(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarLetra()!=null && !"null".equals(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarLetra())?repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarLetra():"") + " " +
							(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarSucursal()!=null  && !"null".equals(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarSucursal())?repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarSucursal() +"-":"") +
							(repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_numero()!=null  && !"null".equals(repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_numero())?repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_numero():"");						
				} else {
					NroComp =  (repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_tipo() !=null && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_tipo())?
							repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_tipo() : "") + " " + 
							(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getComproaDebitarLetra()!=null && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getComproaDebitarLetra())?repo.getReintegro().getReintegroPrestacion().get(idxCuota).getComproaDebitarLetra():"") + " " +
							(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_sucursal() !=null  && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_sucursal())?repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_sucursal() +"-":"") +
							(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_numero() !=null  && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_numero())?repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_numero():"");			
				}

				index++;
				crearHeader(sheet, index, repo, repo.getReintegro(), styleBold,
						styleAll, styleDate);
				index++;
				crearHeaderPrestacion(sheet, index, styleBold);
				for (ReintegroPrestacion rp : repo.getReintegro()
						.getReintegroPrestacion()) {
					index++;
										
					crearFilaInfoPrestacion(sheet, index, rp, styleAll,
							styleDate, NroComp);
					
				}
				index++;
				HSSFRow rowSubtotal = sheet.createRow(index);
				HSSFCell cellSubtotalTexo = rowSubtotal.createCell(4);
				cellSubtotalTexo
						.setCellValue(new HSSFRichTextString("Subtotal"));
				cellSubtotalTexo.setCellStyle(styleBold);

				HSSFCell cellSubtotalValor = rowSubtotal.createCell(5);
				cellSubtotalValor.setCellValue(repo.getReintegro()
						.getImporteTotal().doubleValue());
				cellSubtotalValor.setCellStyle(styleAll);

				index++;
				sheet.createRow(index);
				total = total.add(repo.getTotal());
			}
			index++;
			sheet.createRow(index);
			index++;
			HSSFRow rowTotal = sheet.createRow(index);

			HSSFCell cell = rowTotal.createCell(4);
			cell.setCellValue(new HSSFRichTextString("Total"));
			cell.setCellStyle(styleBold);

			HSSFCell cell1 = rowTotal.createCell(5);
			cell1.setCellValue(total.doubleValue());
			cell1.setCellStyle(styleAll);

			index++;
			sheet.createRow(index);
			index++;
			HSSFRow rowFinal = sheet.createRow(index);

			HSSFCell cellH = rowFinal.createCell(0);
			if (idLista != 0) {
				cellH
						.setCellValue(new HSSFRichTextString("Lista N° "
								+ idLista));
			}
			
			cellH.setCellStyle(styleAll);
			
			
			HSSFCell cellOP = rowFinal.createCell(3);
			if (!list.isEmpty()) {
				cellOP
						.setCellValue(new HSSFRichTextString("OP N° "
								+ idOP));
			} 

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);

		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		}
		return wb;
	}
	
	
	
	public static void  addReintegroSheet(
			List<ReporteOrdenPagoReintegros> listaFarmacia, int idLista, HSSFWorkbook wb) {
				
		
		try {
			List<ReporteOrdenPagoReintegros> list = null;
			try {
				if (idLista != 0) {
					list = OrdenPagoServiceUtil.getReintegrosFromListaId(idLista);
				} 
			} catch (NoSuchReintegroEntryException nsree) {
				list = null; 
			}
			
			HSSFSheet sheet = wb.createSheet("Prestacional "+idLista);

			HSSFPrintSetup ps = sheet.getPrintSetup();
			sheet.setAutobreaks(true);
			ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
			ps.setFitHeight((short)0);
			ps.setFitWidth((short)1);
			
			HSSFCellStyle styleAll = getStyleAll(wb);
			HSSFCellStyle styleBold = getStyleBold(wb);
			HSSFCellStyle styleDate = getStyleDate(wb);

			
			int index = 0;
			HSSFRow rowHeader = sheet.createRow(index);

			HSSFCell cell0H = rowHeader.createCell(0);
			cell0H.setCellValue(new HSSFRichTextString("Seccional"));
			cell0H.setCellStyle(styleBold);

			HSSFCell cell1H = rowHeader.createCell(1);
			cell1H.setCellValue(new HSSFRichTextString(list.get(0)
					.getReintegro().getSeccional().getId()
					+ " - "
					+ list.get(0).getReintegro().getSeccional()
							.getDescripcion()));
			cell1H.setCellStyle(styleAll);

			sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 2));

			HSSFCell cell5H = rowHeader.createCell(5);
			cell5H.setCellValue(list.get(0).getReintegro().getFecha());
			cell5H.setCellStyle(styleDate);

			index++;
			sheet.createRow(index);

			BigDecimal total = new BigDecimal("0");
			for (ReporteOrdenPagoReintegros repo : list) {
				
				String NroComp = "";
				int idxCuota = 0;
				
				if (repo.getReintegro().getDetalleCuota() != null) {
					NroComp =  (repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_tipo() !=null && !"null".equals(repo.getReintegro().getDetalleCuota().get(0).getCompro_a_debitar_tipo())?
							repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_tipo() : "") + " " + 
							(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarLetra()!=null && !"null".equals(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarLetra())?repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarLetra():"") + " " +
							(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarSucursal()!=null  && !"null".equals(repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarSucursal())?repo.getReintegro().getDetalleCuota().get(idxCuota).getComproaDebitarSucursal() +"-":"") +
							(repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_numero()!=null  && !"null".equals(repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_numero())?repo.getReintegro().getDetalleCuota().get(idxCuota).getCompro_a_debitar_numero():"");						
				} else {
					NroComp =  (repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_tipo() !=null && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_tipo())?
							repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_tipo() : "") + " " + 
							(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getComproaDebitarLetra()!=null && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getComproaDebitarLetra())?repo.getReintegro().getReintegroPrestacion().get(idxCuota).getComproaDebitarLetra():"") + " " +
							(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_sucursal() !=null  && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_sucursal())?repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_sucursal() +"-":"") +
							(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_numero() !=null  && !"null".equals(repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_numero())?repo.getReintegro().getReintegroPrestacion().get(idxCuota).getCompro_a_debitar_numero():"");			
				}

				index++;
				crearHeader(sheet, index, repo, repo.getReintegro(), styleBold,
						styleAll, styleDate);
				index++;
				crearHeaderPrestacion(sheet, index, styleBold);
				for (ReintegroPrestacion rp : repo.getReintegro()
						.getReintegroPrestacion()) {
					index++;
										
					crearFilaInfoPrestacion(sheet, index, rp, styleAll,
							styleDate, NroComp);
					
				}
				index++;
				HSSFRow rowSubtotal = sheet.createRow(index);
				HSSFCell cellSubtotalTexo = rowSubtotal.createCell(4);
				cellSubtotalTexo
						.setCellValue(new HSSFRichTextString("Subtotal"));
				cellSubtotalTexo.setCellStyle(styleBold);

				HSSFCell cellSubtotalValor = rowSubtotal.createCell(5);
				cellSubtotalValor.setCellValue(repo.getReintegro()
						.getImporteTotal().doubleValue());
				cellSubtotalValor.setCellStyle(styleAll);

				index++;
				sheet.createRow(index);
				total = total.add(repo.getTotal());
			}
			index++;
			sheet.createRow(index);
			index++;
			HSSFRow rowTotal = sheet.createRow(index);

			HSSFCell cell = rowTotal.createCell(4);
			cell.setCellValue(new HSSFRichTextString("Total"));
			cell.setCellStyle(styleBold);

			HSSFCell cell1 = rowTotal.createCell(5);
			cell1.setCellValue(total.doubleValue());
			cell1.setCellStyle(styleAll);

			index++;
			sheet.createRow(index);
			index++;
			HSSFRow rowFinal = sheet.createRow(index);

			HSSFCell cellH = rowFinal.createCell(0);
			if (idLista != 0) {
				cellH
						.setCellValue(new HSSFRichTextString("Lista N° "
								+ idLista));
			}
			
			cellH.setCellStyle(styleAll);

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);

		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		}		
	}

	private static void crearHeaderPrestacion(HSSFSheet sheet, int index,
			HSSFCellStyle styleBold) {
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell = rowHeader.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Realizado"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(
				"Prestacion                   "));
		cell1.setCellStyle(styleBold);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Codigo NN"));
		cell2.setCellStyle(styleBold);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Cant"));
		cell3.setCellStyle(styleBold);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("M. Unit"));
		cell4.setCellStyle(styleBold);

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("M. Total"));
		cell5.setCellStyle(styleBold);
		
		HSSFCell cell6 = rowHeader.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Comprobante"));
		cell6.setCellStyle(styleBold);
		
		HSSFCell cell7 = rowHeader.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Nro. Reclamo"));
		cell7.setCellStyle(styleBold);
		
	}

	private static void crearFilaInfoPrestacion(HSSFSheet sheet, int index,
			ReintegroPrestacion rp, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate, String NroComprobante) {
		HSSFRow row = sheet.createRow(index);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(rp.getFecha_prestacion());
		cell.setCellStyle(styleDate);

		HSSFCell cell1 = row.createCell(1);
		String desc = rp.getPlan_prestacion().getNomenclador().getDescripcion();
		cell1.setCellValue(new HSSFRichTextString(desc.length() < 35 ? desc
				: desc.substring(0, 35)));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = row.createCell(2);
		//cell2.setCellValue(new HSSFRichTextString(rp.getCodigo()));
		cell2.setCellValue(new HSSFRichTextString(rp.getPlan_prestacion().getNomenclador().getCodigo()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = row.createCell(3);
		if (rp instanceof ReintegroPrestacionNormal) {
			cell3.setCellValue(((ReintegroPrestacionNormal) rp).getCantidad().doubleValue());
		} else if (rp instanceof ReintegroPrestacionOdoProtesis) {
			cell3.setCellValue(((ReintegroPrestacionOdoProtesis) rp).getCantidad().doubleValue());
		} if (rp instanceof ReintegroPrestacionOdoOrtopediaOrtodoncia) {
			cell3.setCellValue(1);
		}
		
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(rp.getImporte().doubleValue());
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(rp.getImporteTotal().doubleValue());
		cell5.setCellStyle(styleAll);
		
		HSSFCell cell6 = row.createCell(6);
		try {
			/*
		  cell6.setCellValue(new HSSFRichTextString((rp.getCompro_a_debitar_tipo()!=null && !"null".equals(rp.getCompro_a_debitar_tipo())?
				  rp.getCompro_a_debitar_tipo():"")+ " " +
		  (rp.getComproaDebitarLetra()!=null && !"null".equals(rp.getComproaDebitarLetra())?rp.getComproaDebitarLetra():"") + " " +
		  (rp.getCompro_a_debitar_sucursal()!=null  && !"null".equals(rp.getCompro_a_debitar_sucursal())?rp.getCompro_a_debitar_sucursal()+"-":"")+
		  (rp.getCompro_a_debitar_numero()!=null  && !"null".equals(rp.getCompro_a_debitar_numero())?rp.getCompro_a_debitar_numero():"")));
		  */
			
		  cell6.setCellValue(NroComprobante);
		  
		  cell6.setCellStyle(styleAll);
		}catch(Exception e) {}
		
		HSSFCell cell7 = row.createCell(7);
		try {			
		  cell7.setCellValue(rp.getId_reclamo_prestacional());		  
		  cell7.setCellStyle(styleAll);
		}catch(Exception e) {}  
		
	}

	private static void crearHeader(HSSFSheet sheet, int index,
			ReporteOrdenPagoReintegros repo, Reintegro reintegro,
			HSSFCellStyle styleBold, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleBold);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(reintegro.getFecha());
		cell1.setCellStyle(styleDate);

		HSSFCell cell2 = rowHeader.createCell(2);
		if (!reintegro.getTipo_reintegro().equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cell2.setCellValue(new HSSFRichTextString("Reintegro N° "
				+ reintegro.getId_reintegro_user()));
		} else {			
			cell2.setCellValue(new HSSFRichTextString("Reintegro N° "
				+ reintegro.getDetalleCuota().get(0).getId_reintegro_user()));
		}
		cell2.setCellStyle(styleBold);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Afiliado"));
		cell3.setCellStyle(styleBold);

		//int id = 0;
		//if (repo.getReintegro().getEntidad().equals("A.M.T.I.M.A.")) {
			//id = repo.getAfiliado().getId_amtima();
		//} else if (repo.getReintegro().getEntidad().equals("U.O.M.A.")) {
			//id = repo.getAfiliado().getId_uoma();
		//}
		//if (repo.getReintegro().getEntidad().equals("O.S.P.I.M.")) {
			//id = repo.getAfiliado().getId_ospim();
		//}

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(repo.getAfiliado().getCuil_titular() + " - "
				+ repo.getAfiliado().getApeNombre() + " - Doc." + repo.getAfiliado().getDocu_numero()) );
		cell4.setCellStyle(styleAll);

		sheet.addMergedRegion(new CellRangeAddress(index, // first row (0-based)
				index, // last row (0-based)
				4, // first column (0-based)
				8 // last column (0-based)
				));

	}

}
