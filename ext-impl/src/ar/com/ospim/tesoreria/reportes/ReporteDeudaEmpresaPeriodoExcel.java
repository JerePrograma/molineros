package ar.com.ospim.tesoreria.reportes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
import  org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.beans.ReporteDeudaEmpresa;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaConsolidado;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaListado;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteOPReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteDeudaEmpresaPeriodoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteOPReintegros.class);

	public static HSSFWorkbook generaReporteDeudaEmpresaPeriodo(HttpServletRequest req,
			HttpServletResponse res) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAllWithHeader = getStyleAllWithBorder(wb);		
		HSSFCellStyle styleHeaderWithBorder = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleInt= getStyleIntWithBorder(wb);

		try {
			
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
			String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
			fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
			String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");
			String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
			fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
			String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
			
			int ramo_desde=ParamUtil.getInteger(req, "ramo_desde");
			int ramo_hasta=ParamUtil.getInteger(req, "ramo_hasta");
			
			boolean agrupar_remuneracion=ParamUtil.getBoolean(req, "agrupar_remuneracion");
			boolean sin_deuda=ParamUtil.getBoolean(req, "sin_deuda");

			Date fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes
					+ "-" + fechaInicioAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
					+ fechaHastaAnio);

			
			List<ReporteDeudaEmpresa> reporte = AfipServiceUtil.getReporteDeudaEmpresaPeriodo(fechaIni, fechaFin, sin_deuda, ramo_desde, ramo_hasta);		
			

			HSSFSheet sheet = wb.createSheet("Hoja 1");
			int index = 0;
			Collections.sort(reporte, new Comparator<ReporteDeudaEmpresa>() {
				public int compare(ReporteDeudaEmpresa o1, ReporteDeudaEmpresa o2) {
					if (o1.getPeriodo().equals(o2.getPeriodo())) {
						return o1.getPeriodo().compareTo(o2.getPeriodo());
					} else {
						return o1.getPeriodo().compareTo(o2.getPeriodo());
					}
				}
			});

			crearHeader(sheet, styleHeaderWithBorder, agrupar_remuneracion);			
			for (ReporteDeudaEmpresa repo : reporte) {
				++index;
				crearInfo(sheet, repo,styleMoney, styleInt, styleDateWithBorder, styleAllWithHeader, agrupar_remuneracion,index);
			}
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);			
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);
			

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

	private static void crearInfo2(HSSFSheet sheet, ReporteDeudaEmpresaConsolidado repo, HSSFCellStyle stylemoney,
			HSSFCellStyle styleint, HSSFCellStyle styleDate, HSSFCellStyle styleAll, int index ) {

		HSSFRow row = sheet.createRow(index);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);
		int col = 0;
		
		HSSFCell cell1 = row.createCell(col);
		cell1.setCellValue(new HSSFRichTextString(repo.getCuit()));
		cell1.setCellStyle(styleAll);
		col++;
		HSSFCell cell2 = row.createCell(col);		
		cell2.setCellStyle(styleAll);
		cell2.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		col++;
		HSSFCell cell4 = row.createCell(col);
		cell4.setCellValue(repo.getRamo());
		cell4.setCellStyle(styleAll);
		col++;
		HSSFCell cell5 = row.createCell(col);
		cell5.setCellValue(repo.getTotalCalculado()!=null?repo.getTotalCalculado().doubleValue():0);
		cell5.setCellStyle(stylemoney);
		col++;
		HSSFCell cell6 = row.createCell(col);
		cell6.setCellValue(repo.getPagado()!=null?repo.getPagado().doubleValue():0);
		cell6.setCellStyle(stylemoney);
		col++;
		HSSFCell cell7 = row.createCell(col);
		cell7.setCellValue(repo.getPagadoActaConvenio()!=null?repo.getPagadoActaConvenio().doubleValue():0);
		cell7.setCellStyle(stylemoney);
		col++;
		HSSFCell cell8 = row.createCell(col);
		cell8.setCellStyle(stylemoney);
		cell8.setCellValue(repo.getDeuda()!=null?repo.getDeuda().doubleValue():0);
		col++;
		HSSFCell cell9 = row.createCell(col);		
		cell9.setCellStyle(styleAll);
		cell9.setCellValue(new HSSFRichTextString(repo.getCalle()));
		col++;
		HSSFCell cell10 = row.createCell(col);		
		cell10.setCellStyle(styleAll);
		cell10.setCellValue(new HSSFRichTextString(repo.getNumero()));
		col++;
		HSSFCell cell11 = row.createCell(col);		
		cell11.setCellStyle(styleAll);
		cell11.setCellValue(new HSSFRichTextString(repo.getPiso()));
		col++;
		HSSFCell cell12 = row.createCell(col);		
		cell12.setCellStyle(styleAll);
		cell12.setCellValue(new HSSFRichTextString(repo.getDpto()));
		col++;
		HSSFCell cell13 = row.createCell(col);		
		cell13.setCellStyle(styleAll);
		cell13.setCellValue(new HSSFRichTextString(repo.getLocalidad()));
		col++;
		HSSFCell cell14 = row.createCell(col);		
		cell14.setCellStyle(styleAll);
		cell14.setCellValue(new HSSFRichTextString(repo.getProvincia()));
		col++;
		HSSFCell cell17 = row.createCell(col);		
		cell17.setCellStyle(styleAll);
		cell17.setCellValue(new HSSFRichTextString(repo.getCodigoPostal()));
//		col++;
		
	
	}
	
	private static void crearInfo(HSSFSheet sheet, 	ReporteDeudaEmpresa repo, HSSFCellStyle stylemoney,
			HSSFCellStyle styleint, HSSFCellStyle styleDate, HSSFCellStyle styleAll,boolean agrupar_remuneracion, int index ) {

		HSSFRow row = sheet.createRow(index);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);
		
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(repo.getPeriodoAsString()));
		cell.setCellStyle(styleDate);
		
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(repo.getCuit()));
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell2 = row.createCell(2);		
		cell2.setCellStyle(styleAll);
		cell2.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		
		HSSFCell cell4 = row.createCell(3);
		cell4.setCellValue(repo.getRamo());
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell3 = row.createCell(4);
		cell3.setCellStyle(stylemoney);
		cell3.setCellValue(repo.getDeuda()!=null?repo.getDeuda().doubleValue():0);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellStyle(stylemoney);
		cell5.setCellValue(repo.getRemDeclarada()!=null?repo.getRemDeclarada().doubleValue():0);
		
		HSSFCell cell6 = row.createCell(6);
		cell6.setCellStyle(styleint);
		cell6.setCellValue(repo.getCantAfiliadosDeclarados());
		
		
		int index_agr=6;
		if(agrupar_remuneracion){
			HSSFCell cell11 = row.createCell(++index_agr);
			cell11.setCellValue(repo.getTotal_calculado()!=null?repo.getTotal_calculado().doubleValue():0);
			cell11.setCellStyle(stylemoney);
			
			HSSFCell cell12 = row.createCell(++index_agr);
			cell12.setCellValue(repo.getPagado()!=null?repo.getPagado().doubleValue():0);
			cell12.setCellStyle(stylemoney);
			
			HSSFCell cell14 = row.createCell(++index_agr);
			cell14.setCellValue(repo.getPagado_acta_convenio()!=null?repo.getPagado_acta_convenio().doubleValue():0);
			cell14.setCellStyle(stylemoney);
			
			HSSFCell cell13 = row.createCell(++index_agr);
			cell13.setCellValue(repo.getPorc_pagado()!=null?repo.getPorc_pagado().doubleValue():0);
			cell13.setCellStyle(stylemoney);
			
			HSSFCell cell51 = row.createCell(++index_agr);
			cell51.setCellStyle(stylemoney);
			cell51.setCellValue(repo.getRemDeclarada_81()!=null?repo.getRemDeclarada_81().doubleValue():0);
			
			HSSFCell cell61 = row.createCell(++index_agr);
			cell61.setCellStyle(stylemoney);
			cell61.setCellValue(repo.getRemDeclarada_765()!=null?repo.getRemDeclarada_765().doubleValue():0);
			
			HSSFCell cell7 = row.createCell(++index_agr);
			cell7.setCellStyle(styleint);
			cell7.setCellValue(repo.getCantAfiliadosDeclarados_81());
			
			HSSFCell cell8 = row.createCell(++index_agr);
			cell8.setCellStyle(styleint);
			cell8.setCellValue(repo.getCantAfiliadosDeclarados_765());
			
			HSSFCell cell9 = row.createCell(++index_agr);
			cell9.setCellValue(repo.getCalculado_765()!=null?repo.getCalculado_765().doubleValue():0);
			cell9.setCellStyle(stylemoney);
			
			HSSFCell cell10 = row.createCell(++index_agr);
			cell10.setCellValue(repo.getCalculado_810()!=null?repo.getCalculado_810().doubleValue():0);
			cell10.setCellStyle(stylemoney);
			
		}	
		HSSFCell cell11 = row.createCell(++index_agr);		
		cell11.setCellStyle(styleAll);
		cell11.setCellValue(new HSSFRichTextString(repo.getCalle()));
		
		HSSFCell cell12 = row.createCell(++index_agr);		
		cell12.setCellStyle(styleAll);
		cell12.setCellValue(new HSSFRichTextString(repo.getNumero()));
		
		HSSFCell cell13 = row.createCell(++index_agr);		
		cell13.setCellStyle(styleAll);
		cell13.setCellValue(new HSSFRichTextString(repo.getPiso()));
		
		HSSFCell cell14 = row.createCell(++index_agr);		
		cell14.setCellStyle(styleAll);
		cell14.setCellValue(new HSSFRichTextString(repo.getDpto()));
		
		HSSFCell cell15 = row.createCell(++index_agr);		
		cell15.setCellStyle(styleAll);
		cell15.setCellValue(new HSSFRichTextString(repo.getLocalidad()));
		
		HSSFCell cell16 = row.createCell(++index_agr);		
		cell16.setCellStyle(styleAll);
		cell16.setCellValue(new HSSFRichTextString(repo.getProvincia()));
		
		HSSFCell cell17 = row.createCell(++index_agr);		
		cell17.setCellStyle(styleAll);
		cell17.setCellValue(new HSSFRichTextString(repo.getCodPostal()));
		
		
	
	}
	
	private static int crearCabecera(int rowNum, HSSFWorkbook wb, HSSFSheet sheet, ReporteDeudaEmpresaCab cab) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 12);
		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);
		
		int index = rowNum;
		
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(ReporteDeudaEmpresaListado.REPORTE_DEUDA_EMPRESAS_PERIODO));
		cell.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Periodo: "+ sdf.format(cab.getFechaDesdeParam()) + " al " +  sdf.format(cab.getFechaHastaParam())));
		cell1.setCellStyle(styleHeaderEnca3);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
		
		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Ramo: "+ cab.getRamoDesdeParam() + " al " + cab.getRamoHastaParam() ));
		cell2.setCellStyle(styleHeaderEnca3);
		
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
		
		HSSFRow row3 = sheet.createRow(index++);

		HSSFCell cell3 = row3.createCell(0);
		cell3.setCellValue(new HSSFRichTextString("Agrupa por Remuneracion: "+ (cab.isAgrupaXRemunerParam()==true?"SI":"NO") + 
												  "  Empresas sin deuda: "+ (cab.isEmpresasSinDeudaParam()==true?"SI":"NO")	));
		cell3.setCellStyle(styleHeaderEnca3);
		
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));
		
		return index;
	}	
	
	private static void crearHeader2(HSSFSheet sheet, HSSFCellStyle styleHeader,int rowNum) {
		
		int col = 0;
		HSSFRow row = sheet.createRow(rowNum);
		HSSFCell cell0 = row.createCell(col);
		cell0.setCellValue(new HSSFRichTextString("CUIT Contrib."));
		cell0.setCellStyle(styleHeader);
		col++;
		HSSFCell cell1 = row.createCell(col);
		cell1.setCellValue(new HSSFRichTextString("Razón Social"));
		cell1.setCellStyle(styleHeader);
		col++;
		HSSFCell cell2 = row.createCell(col);
		cell2.setCellValue(new HSSFRichTextString("Ramo"));
		cell2.setCellStyle(styleHeader);
		col++;
		HSSFCell cell3 = row.createCell(col);
		cell3.setCellValue(new HSSFRichTextString("Calculado"));
		cell3.setCellStyle(styleHeader);
		col++;
		HSSFCell cell4 = row.createCell(col);
		cell4.setCellValue(new HSSFRichTextString("Pagado"));
		cell4.setCellStyle(styleHeader);
		col++;
		HSSFCell cell5 = row.createCell(col);
		cell5.setCellValue(new HSSFRichTextString("Actas/Convenios"));
		cell5.setCellStyle(styleHeader);
		col++;
		HSSFCell cell6 = row.createCell(col);
		cell6.setCellValue(new HSSFRichTextString("Deuda"));
		cell6.setCellStyle(styleHeader);		
		col++;
		HSSFCell cell66 = row.createCell(col);
		cell66.setCellValue(new HSSFRichTextString("Calle"));
		cell66.setCellStyle(styleHeader);
		col++;
		HSSFCell cell67 = row.createCell(col);
		cell67.setCellValue(new HSSFRichTextString("Nro."));
		cell67.setCellStyle(styleHeader);			
		col++;
		HSSFCell cell68 = row.createCell(col);
		cell68.setCellValue(new HSSFRichTextString("Piso"));
		cell68.setCellStyle(styleHeader);
		col++;
		HSSFCell cell69 = row.createCell(col);
		cell69.setCellValue(new HSSFRichTextString("Depto."));
		cell69.setCellStyle(styleHeader);
		col++;
		HSSFCell cell70 = row.createCell(col);
		cell70.setCellValue(new HSSFRichTextString("Localidad"));
		cell70.setCellStyle(styleHeader);
		col++;
		HSSFCell cell71 = row.createCell(col);
		cell71.setCellValue(new HSSFRichTextString("Provincia"));
		cell71.setCellStyle(styleHeader);
		col++;
		HSSFCell cell72 = row.createCell(col);
		cell72.setCellValue(new HSSFRichTextString("Cod. Postal"));
		cell72.setCellStyle(styleHeader);
		

	}
	
	private static void crearHeader(HSSFSheet sheet, HSSFCellStyle styleHeader, boolean agrupar_remuneracion) {
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Período"));
		cell.setCellStyle(styleHeader);
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("CUIT Contrib."));
		cell1.setCellStyle(styleHeader);
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Razón Social"));
		cell2.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(3);
		cell4.setCellValue(new HSSFRichTextString("Ramo"));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell3 = row.createCell(4);
		cell3.setCellValue(new HSSFRichTextString("Deuda"));
		cell3.setCellStyle(styleHeader);		
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Total Remuneración"));
		cell5.setCellStyle(styleHeader);
		
		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Total Afiliados"));
		cell6.setCellStyle(styleHeader);
		
		int index=6;
		if(agrupar_remuneracion){
			HSSFCell cell11 = row.createCell(++index);
			cell11.setCellValue(new HSSFRichTextString("Calculado"));
			cell11.setCellStyle(styleHeader);
			
			HSSFCell cell12 = row.createCell(++index);
			cell12.setCellValue(new HSSFRichTextString("Pagado"));
			cell12.setCellStyle(styleHeader);
			
			HSSFCell cell121 = row.createCell(++index);
			cell121.setCellValue(new HSSFRichTextString("Actas/Convenios"));
			cell121.setCellStyle(styleHeader);
			
			HSSFCell cell13 = row.createCell(++index);
			cell13.setCellValue(new HSSFRichTextString("% Pagado"));
			cell13.setCellStyle(styleHeader);			
					
			HSSFCell cell51 = row.createCell(++index);
			cell51.setCellValue(new HSSFRichTextString("Rem. 8.1%"));
			cell51.setCellStyle(styleHeader);
			
			HSSFCell cell61 = row.createCell(++index);
			cell61.setCellValue(new HSSFRichTextString("Rem. 7.65%"));
			cell61.setCellStyle(styleHeader);
			
			HSSFCell cell7 = row.createCell(++index);
			cell7.setCellValue(new HSSFRichTextString("Cant. 8.1%"));
			cell7.setCellStyle(styleHeader);
			HSSFCell cell8 = row.createCell(++index);
			cell8.setCellValue(new HSSFRichTextString("Cant. 7.65%"));
			cell8.setCellStyle(styleHeader);
			
			HSSFCell cell9 = row.createCell(++index);
			cell9.setCellValue(new HSSFRichTextString("Calc. 7.65"));
			cell9.setCellStyle(styleHeader);
			
			HSSFCell cell10 = row.createCell(++index);
			cell10.setCellValue(new HSSFRichTextString("Calc. 8.10"));
			cell10.setCellStyle(styleHeader);
			
		}	
		
		HSSFCell cell66 = row.createCell(++index);
		cell66.setCellValue(new HSSFRichTextString("Calle"));
		cell66.setCellStyle(styleHeader);
		
		HSSFCell cell67 = row.createCell(++index);
		cell67.setCellValue(new HSSFRichTextString("Nro."));
		cell67.setCellStyle(styleHeader);			
		
		HSSFCell cell68 = row.createCell(++index);
		cell68.setCellValue(new HSSFRichTextString("Piso"));
		cell68.setCellStyle(styleHeader);
		
		HSSFCell cell69 = row.createCell(++index);
		cell69.setCellValue(new HSSFRichTextString("Depto."));
		cell69.setCellStyle(styleHeader);
		
		HSSFCell cell70 = row.createCell(++index);
		cell70.setCellValue(new HSSFRichTextString("Localidad"));
		cell70.setCellStyle(styleHeader);
		
		HSSFCell cell71 = row.createCell(++index);
		cell71.setCellValue(new HSSFRichTextString("Provincia"));
		cell71.setCellStyle(styleHeader);
		
		HSSFCell cell72 = row.createCell(++index);
		cell72.setCellValue(new HSSFRichTextString("Cod. Postal"));
		cell72.setCellStyle(styleHeader);
		

	}

	public static HSSFWorkbook generaReporteDeudaEmpresaPeriodoConsolidado(HttpServletRequest req,
			HttpServletResponse res) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAllWithHeader = getStyleAllWithBorder(wb);		
		HSSFCellStyle styleHeaderWithBorder = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleInt= getStyleIntWithBorder(wb);

		try {
			
			int idReporte=ParamUtil.getInteger(req, "idReporte");

			ReporteDeudaEmpresaListado reporte = AfipServiceUtil.getReporteDeudaEmpresaPeriodoConsolidado(idReporte);		
		
			HSSFSheet sheet1 = wb.createSheet("Consolidado");
			int index = 0;

			index = crearCabecera(index, wb, sheet1, reporte.getCabecera());
			
			crearHeader2(sheet1, styleHeaderWithBorder, index);			
			
			for (ReporteDeudaEmpresaConsolidado repo : reporte.getConsolidado()) {
				++index;
				crearInfo2(sheet1, repo, styleMoney, styleInt, styleDateWithBorder, styleAllWithHeader, index);
			}
			sheet1.autoSizeColumn((short) 0);
			sheet1.autoSizeColumn((short) 1);
			sheet1.autoSizeColumn((short) 2);
			sheet1.autoSizeColumn((short) 3);
			sheet1.autoSizeColumn((short) 4);
			sheet1.autoSizeColumn((short) 5);			
			sheet1.autoSizeColumn((short) 6);
			sheet1.autoSizeColumn((short) 7);		
			sheet1.autoSizeColumn((short) 8);
			sheet1.autoSizeColumn((short) 9);
			sheet1.autoSizeColumn((short) 10);
			sheet1.autoSizeColumn((short) 11);
			sheet1.autoSizeColumn((short) 12);
			sheet1.autoSizeColumn((short) 13);			
			sheet1.autoSizeColumn((short) 14);
			
			boolean agrupar_remuneracion = reporte.getCabecera().isAgrupaXRemunerParam();

			
			HSSFSheet sheet = wb.createSheet("Detalle");
			index = 0;

			crearHeader(sheet, styleHeaderWithBorder, agrupar_remuneracion);			
			for (ReporteDeudaEmpresa repo : reporte.getDetalle()) {
				++index;
				crearInfo(sheet, repo,styleMoney, styleInt, styleDateWithBorder, styleAllWithHeader, agrupar_remuneracion,index);
			}
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
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return wb;
	}

}
