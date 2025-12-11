package ar.com.ospim.estudioisidro.reportes;

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
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.estudioisidro.beans.ReporteEstadisticoSeguimientoEmpresa;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;


public class ReporteEstadisticoSeguimientoEmpresasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSeguimientoEmpresasExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);
//		Integer nroLote = ParamUtil.getInteger(req, "nro_lote",0);
		Integer nroLote = null;
		String nroLoteStr = ParamUtil.getString(req, "nro_lote");
		try{
			nroLote = Integer.parseInt(nroLoteStr);
		}catch(NumberFormatException e){
			//nada
		}
		String tipoLote = ParamUtil.getString(req, "tipo_lote");

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			Empresa empresa = new Empresa(cuit, sucu, "");
			empresa.setId_seccional(seccional);

			List<ReporteEstadisticoSeguimientoEmpresa> recibos = LlamadoServiceUtil
					.getReporteEstadisticoSeguimientoEmpresaLotes(fechaIni, fechaFin, empresa,nroLote,tipoLote);

			return generarReporte(fechaIni, fechaFin, recibos,empresa);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	
	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteEstadisticoSeguimientoEmpresa> reporte,Empresa empresa) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleFechaLeft = getStyleDateWithBorder(wb);		

		HSSFCellStyle styleTop = getStyleAllWithBorder(wb);
		

		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

		HSSFCellStyle styleHeader = getStyleWhiteHeaderWithBorder(wb);

		HSSFCellStyle styleHeaderLeft = getStyleHeaderWithBorder(wb);
		

		HSSFCellStyle styleHeaderRight = getStyleAllWithBorder(wb);

		HSSFCellStyle styleFechaLeftTop = getStyleDateWithBorder(wb);
		HSSFCellStyle styleMoneyRight = getStyleMoneyWithBorder(wb);
		
		
		HSSFCellStyle st =  wb.createCellStyle();
		st.setDataFormat((short) 6);
//		st.setFillForegroundColor( HSSFColor.GREY_25_PERCENT.index);
		st.setFillForegroundColor( HSSFColorPredefined.AQUA.getIndex());
		st.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(false);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte Estadístico Seguimiento Empresas - Situación al "
						+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
		int i = 1;
		
		
		for (ReporteEstadisticoSeguimientoEmpresa repo : reporte) {
			
			i = generarHeader(sheet, i, styleHeader, st,
					styleHeaderRight, wb, repo);
			
			
			i=generarDetalle(sheet, i, styleAll, styleHeaderLeft,
					styleHeaderRight,styleMoneyRight, wb, repo,empresa);
				
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

		return wb;
	}

	

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR,
			HSSFWorkbook wb, ReporteEstadisticoSeguimientoEmpresa repo) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Lote "+ repo.getLote().toString()));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell0 = row.createCell(1);
		cell0.setCellValue(new HSSFRichTextString(repo.getFechaAsString()!=null?repo.getFechaAsString():"") );
		cell0.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(3);
		cellAcreed.setCellValue(new HSSFRichTextString("Días de Gestión "));
		cellAcreed.setCellStyle(styleHeader);

		
		HSSFCell cell3 = row.createCell(4);
		cell3.setCellValue(repo.getDiasGestion());
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(5);
		cell4.setCellValue(new HSSFRichTextString(repo.getDiasGestion()>120?"CERRADO":"") );
		cell4.setCellStyle(repo.getDiasGestion()>120?styleHeaderL:styleHeader);
				
		//wb.setRepeatingRowsAndColumns(0, 0, 13, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		i++;
		row = sheet.createRow(i);
		HSSFCell cell21 = row.createCell(1);
		cell21.setCellValue(new HSSFRichTextString("Casos"));
		cell21.setCellStyle(styleHeader);
		
		HSSFCell cell22 = row.createCell(2);
		cell22.setCellValue(new HSSFRichTextString("$ ASIGNADOS"));
		cell22.setCellStyle(styleHeader);
		
		HSSFCell cell23 = row.createCell(3);
		cell23.setCellValue(new HSSFRichTextString("$ RECUPERADOS"));
		cell23.setCellStyle(styleHeader);
		
		HSSFCell cell25 = row.createCell(4);
		cell25.setCellValue(new HSSFRichTextString("$ COBRADO"));
		cell25.setCellStyle(styleHeader);
		
		HSSFCell cell24 = row.createCell(5);
		cell24.setCellValue(new HSSFRichTextString("%"));
		cell24.setCellStyle(styleHeader);
		
		return ++i;
	}
	
	
	private static int generarDetalle(HSSFSheet sheet, int i,
			HSSFCellStyle styleAll, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR,
			HSSFCellStyle styleMoneyRight,HSSFWorkbook wb, ReporteEstadisticoSeguimientoEmpresa repo,Empresa empresa) {
		
		List<ReporteEstadisticoSeguimientoEmpresa> regs = LlamadoServiceUtil
				.getReporteEstadisticoSeguimientoEmpresa( empresa,repo.getLote(),null);
		
		Integer inicial =i;
		
		for (ReporteEstadisticoSeguimientoEmpresa r : regs) {
			Double asignado=0D;
			Double recuperado=0D;
			Double cobrado=0D;
			HSSFRow row = sheet.createRow(i);
			
			HSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(new HSSFRichTextString(r.getEstadoDescripcion()));
			cell1.setCellStyle(styleAll);
			
			HSSFCell cell0 = row.createCell(1);
			cell0.setCellValue(r.getCantidad() );
			cell0.setCellStyle(styleAll);
			
			
			ReporteEstadisticoSeguimientoEmpresa asig = LlamadoServiceUtil.getReporteEstadisticoSeguimientoEmpresaAsignado(empresa,
					repo.getLote(),null, r.getEstadoId());
			
			if(asig.getAsignado()!=null){
				asignado=asig.getAsignado();
			}
			
			
			ReporteEstadisticoSeguimientoEmpresa rec = LlamadoServiceUtil.getReporteEstadisticoSeguimientoEmpresaRecuperado(empresa,
					repo.getLote(),null, r.getEstadoId());
			
			if(rec.getRecuperado() !=null){
				recuperado=rec.getRecuperado() ;
			}
			
			
			ReporteEstadisticoSeguimientoEmpresa cob = LlamadoServiceUtil.getReporteEstadisticoSeguimientoEmpresaCobrado(empresa,
					repo.getLote(),null, r.getEstadoId());
			
			if(cob.getCobrado() !=null){
				cobrado=cob.getCobrado() ;
			}
			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(asignado);
			cell2.setCellStyle(styleMoneyRight);
			
			
			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(recuperado);
			cell3.setCellStyle(styleMoneyRight);
			
			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(cobrado);
			cell4.setCellStyle(styleMoneyRight);
			
			i++;
			
		}
		
		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell00 = row.createCell(0);
		cell00.setCellValue(new HSSFRichTextString("Total"));
		cell00.setCellStyle(styleHeaderR);
		
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellFormula("SUM(B"+Integer.toString(inicial)  +":B"+ Integer.toString(i) +")");
		cell1.setCellStyle(styleHeaderR);
		
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellFormula("SUM(C"+Integer.toString(inicial)  +":C"+ Integer.toString(i) +")");
		cell2.setCellStyle(styleMoneyRight);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellFormula("SUM(D"+Integer.toString(inicial)  +":D"+ Integer.toString(i) +")");
		cell3.setCellStyle(styleMoneyRight);
		
		HSSFCell cell5 = row.createCell(4);
		cell5.setCellFormula("SUM(E"+Integer.toString(inicial)  +":E"+ Integer.toString(i) +")");
		cell5.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4 = row.createCell(5);
		cell4.setCellFormula("D"+Integer.toString(i+1)  +"*100/C"+ Integer.toString(i+1));
		cell4.setCellStyle(styleMoneyRight);
		
		i++;
		return ++i;
	}
	
	
	
}
