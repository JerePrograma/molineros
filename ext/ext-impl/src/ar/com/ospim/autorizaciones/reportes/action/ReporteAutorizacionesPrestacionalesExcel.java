package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
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

import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ReporteAutorizacionesPrestacionalesExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReporteAutorizacionesPrestacionalesExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		
		List<AutorizacionPrestacional> auts = new ArrayList<AutorizacionPrestacional> ();
		auts= (ArrayList<AutorizacionPrestacional>)renderRequest.getSession().getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD);
		if(auts==null) {
			
				String entidad = ParamUtil
						.getString(renderRequest, "entidad", null);
				
				String fechaDesdeDia = ParamUtil.getString(renderRequest,
						"fechaDesdeDia");
				String fechaDesdeMes = ParamUtil.getString(renderRequest,
						"fechaDesdeMes");
				String fechaDesdeAnio = ParamUtil.getString(renderRequest,
						"fechaDesdeAnio");
				Date fechaDesde = null;
				try {
					fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
							+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
							+ fechaDesdeAnio);
				} catch (Exception e) {
					fechaDesde = null;
				}
				String fechaHastaDia = ParamUtil.getString(renderRequest,
						"fechaHastaDia");
				String fechaHastaMes = ParamUtil.getString(renderRequest,
						"fechaHastaMes");
				String fechaHastaAnio = ParamUtil.getString(renderRequest,
						"fechaHastaAnio");
				Date fechaHasta = null;
				try {
					fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
							+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
							+ fechaHastaAnio);
				} catch (Exception e) {
					fechaHasta = null;
				}

				int numero = ParamUtil.getInteger(renderRequest, "numero", 0);

				String codPrest = ParamUtil.getString(renderRequest, "codPrest", null);
				String codPrestaci = ParamUtil.getString(renderRequest, "codPrestaci", null);
				String prestador = ParamUtil.getString(renderRequest, "prestador",
						null);

				int estado = ParamUtil.getInteger(renderRequest, "estado", 0);
				int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
				int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
				String cuil_titular = ParamUtil.getString(renderRequest,
						"cuil_titular", null);
				
			    boolean antiguos= ParamUtil.getBoolean(renderRequest,"antiguos");
			    Integer nroAutorizacion=ParamUtil.getInteger(renderRequest, "nroautorizacion", 0);
			    boolean discapacidad= ParamUtil.getBoolean(renderRequest,"discapacidad");
			    boolean leche= ParamUtil.getBoolean(renderRequest,"leche");
			    boolean dependencia= ParamUtil.getBoolean(renderRequest,"dependencia");
			    
			    try {
					auts = AutorizacionPrestacionalServiceUtil.buscarAutorizacionPrestacional(entidad, fechaDesde,
							fechaHasta, nroAfi, inte,cuil_titular, 0, 0, codPrest, prestador,numero,
							estado, codPrestaci,antiguos,nroAutorizacion,discapacidad,leche,dependencia,null);
				} catch (Exception e) {}
					
		}
		return generaReporteAutorizacionesPrestacionales(auts);
	}

	private static HSSFWorkbook generaReporteAutorizacionesPrestacionales(List<AutorizacionPrestacional> list) {
				
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Autorizaciones Prestacionales");

		HSSFPrintSetup ps = sheet.getPrintSetup();
//		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
//		ps.setFitHeight((short) 0);
//		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
		
		int index = createHeader(wb, sheet, styleBold);
		

		for(AutorizacionPrestacional l: list){
			index=crearDatosAutorizacionPrestacional(sheet,l, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber);
		}

		index++;
		sheet.createRow(index);
		
		for (int j = 0; j < 25; j++) {
			sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}
	
	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle styleBold) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
		styleHeaderEnca.setWrapText(true);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		int index = 0;
		
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		row.setHeight((short)-1);
		cell.setCellValue(new HSSFRichTextString("Reporte Autorizaciones Prestacionales"));
		cell.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 24));
		
		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		Calendar hoy = DateUtils.getCalendarGMTMenos3();
		
		cell1.setCellValue(new HSSFRichTextString("Fecha: " +sdf.format(hoy.getTime() )));
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 24));
		
		HSSFRow row2 = sheet.createRow(index++);
		HSSFCell cell2 = row2.createCell(0);
		
		HSSFRow rowHeader = sheet.createRow(index++);

		int col = 0;

		HSSFCell cell16H = rowHeader.createCell(col++);
		cell16H.setCellValue(new HSSFRichTextString("Nro.Autorización"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(col++);
		cell20H.setCellValue(new HSSFRichTextString("Fecha Emisión"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(col++);
		cell13H.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(col++);
		cell18H.setCellValue(new HSSFRichTextString("DNI"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(col++);
		cell19H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell191H = rowHeader.createCell(col++);
		cell191H.setCellValue(new HSSFRichTextString("Dependencia"));
		cell191H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(col++);
		cell6H.setCellValue(new HSSFRichTextString("Leches"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(col++);
		cell3H.setCellValue(new HSSFRichTextString("Prestación"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(col++);
		cell4H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(col++);
		cell17H.setCellValue(new HSSFRichTextString("Importe"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell000H = rowHeader.createCell(col++);
		cell000H.setCellValue(new HSSFRichTextString("Total"));
		cell000H.setCellStyle(styleBold);
		
		HSSFCell cell0030H = rowHeader.createCell(col++);
		cell0030H.setCellValue(new HSSFRichTextString("Periodicidad"));
		cell0030H.setCellStyle(styleBold);
		
		HSSFCell cell003H = rowHeader.createCell(col++);
		cell003H.setCellValue(new HSSFRichTextString("Desde"));
		cell003H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(col++);
		cell12H.setCellValue(new HSSFRichTextString("Hasta"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(col++);
		cell11H.setCellValue(new HSSFRichTextString("Cuit Prestador"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell002H = rowHeader.createCell(col++);
		cell002H.setCellValue(new HSSFRichTextString("Nombre Prestador"));
		cell002H.setCellStyle(styleBold);
		
		HSSFCell cell0020H = rowHeader.createCell(col++);
		cell0020H.setCellValue(new HSSFRichTextString("Estado"));
		cell0020H.setCellStyle(styleBold);		
		
		HSSFCell cell0021H = rowHeader.createCell(col++);
		cell0021H.setCellValue(new HSSFRichTextString("Seccional"));
		cell0021H.setCellStyle(styleBold);	
		return index;
	}

		
	private static int crearDatosAutorizacionPrestacional(HSSFSheet sheet,AutorizacionPrestacional pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		int col = 0;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(col++);
		cell001.setCellValue(pre.getNroAutorizacion());
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(col++);
		cell002.setCellValue(new HSSFRichTextString(pre.getAlta_fechaAsString()));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell005 = rowHeader.createCell(col++);
		cell005.setCellValue(new HSSFRichTextString(pre.getAfiliado().getApeNombre()));
		cell005.setCellStyle(styleAll);
		
		HSSFCell cell017 = rowHeader.createCell(col++);
		cell017.setCellValue(new HSSFRichTextString(pre.getAfiliado().getDocu_numero()));
		cell017.setCellStyle(styleAll);
		
		HSSFCell cell022 = rowHeader.createCell(col++);
		cell022.setCellValue(new HSSFRichTextString(pre.isDiscapacitado()?"SI":"NO"));
		cell022.setCellStyle(styleAll);
		
		HSSFCell cell0221 = rowHeader.createCell(col++);
		cell0221.setCellValue(new HSSFRichTextString(pre.isConDependencia()?"SI":"NO"));
		cell0221.setCellStyle(styleAll);
		
		HSSFCell cell023 = rowHeader.createCell(col++);
		cell023.setCellValue(new HSSFRichTextString(pre.isLecheMaternizada() ?"SI":"NO"));
		cell023.setCellStyle(styleAll);
		
		HSSFCell cell019 = rowHeader.createCell(col++);
		cell019.setCellValue(new HSSFRichTextString(pre.getPrestacion().getCodigo() + " " + 
		pre.getPrestacion().getDescripcion()) );
		cell019.setCellStyle(styleAll);
		
		HSSFCell cell024 = rowHeader.createCell(col++);
		cell024.setCellValue(pre.getCantidad() != null ? pre.getCantidad().doubleValue() : 0);
		cell024.setCellStyle(styleMoney);
		
		HSSFCell cell008 = rowHeader.createCell(col++);
		cell008.setCellValue(pre.getImporte_total() != null ? pre.getImporte_total().doubleValue() : 0 );
		cell008.setCellStyle(styleMoney);
		
		HSSFCell cell025 = rowHeader.createCell(col++);
		cell025.setCellValue(pre.getCantidad() != null &&
				pre.getImporte_total() != null ? pre.getCantidad().multiply(pre.getImporte_total()).doubleValue() : 0);
		cell025.setCellStyle(styleMoney);
		
		
		HSSFCell cell0080 = rowHeader.createCell(col++);
		cell0080.setCellValue(new HSSFRichTextString(pre.getPeriodicidad() != null ? pre.getPeriodicidad() : "" ));
		cell0080.setCellStyle(styleAll);
		
		HSSFCell cell003 = rowHeader.createCell(col++);
		cell003.setCellValue(new HSSFRichTextString(pre.getPeriodo_desde() != null ? pre.getPeriodoDesdeString() : "" ));
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell004 = rowHeader.createCell(col++);
		cell004.setCellValue(new HSSFRichTextString(pre.getPeriodo_hasta() != null ? pre.getPeriodoHastaString() : ""));
		cell004.setCellStyle(styleAll);
		
		HSSFCell cell006 = rowHeader.createCell(col++);
		cell006.setCellValue(new HSSFRichTextString(pre.getAcreedor().getCuit() != null? pre.getAcreedor().getCuit():"" ));
		cell006.setCellStyle(styleAll);
		
		HSSFCell cell007 = rowHeader.createCell(col++);
		cell007.setCellValue(new HSSFRichTextString(pre.getAcreedor().getCuit() != null && !"".equalsIgnoreCase(pre.getAcreedor().getCuit())?pre.getAcreedor().getRazon_soc():"" ));
		cell007.setCellStyle(styleAll);
		
		HSSFCell cell027 = rowHeader.createCell(col++);
		cell027.setCellValue(new HSSFRichTextString(WebKeysAutorizaciones.ESTADOS_AUTORIZACIONES_PRESTACIONALES[pre.getEstado()]));
		cell027.setCellStyle(styleAll);
		
		HSSFCell cell028 = rowHeader.createCell(col++);
		cell028.setCellValue(new HSSFRichTextString(pre.getSeccional().getDescripcion() != null? pre.getSeccional().getDescripcion():"" ));
		cell028.setCellStyle(styleAll);
		
//        rowHeader.setHeight((short) 0);
		return index++;
	}
        
}


