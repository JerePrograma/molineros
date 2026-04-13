package ar.com.ospim.estudioisidro.reportes;

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
import ar.com.ospim.estudioisidro.beans.DemandaJudicial;
import ar.com.ospim.estudioisidro.service.DemandaJudicialServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ReporteDemandasExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReporteDemandasExcel.class);
	
	private static String[] estadosStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_ESTADOS").split(";");
	private static Map<String,String> estadosJud=new HashMap<String,String>();
	

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) throws Exception {
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		
		for(int i=0;i<=estadosStr.length-1;i++){
			String codigo = estadosStr[i].split("=")[0];
			String descripcion = estadosStr[i].split("=")[1];
			estadosJud.put(codigo,descripcion);
	    }
		
		Integer id = ParamUtil.getInteger(renderRequest, "id");
		String tipo = ParamUtil.getString(renderRequest, "tipo");
		
		String fechaMesDde = ParamUtil.getString(renderRequest,
				"fechaMesDde");
		String fechaDiaDde = ParamUtil.getString(renderRequest,
				"fechaDiaDde");
		String fechaAnioDde = ParamUtil.getString(renderRequest,
				"fechaAnioDde");
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDde = null;
		try {
			fechaDde = formatoDeFecha.parse(fechaDiaDde
					+ "/" + (Integer.parseInt(fechaMesDde) + 1)
					+ "/" + fechaAnioDde);
		} catch (Exception e) {	}

		String fechaMesHta = ParamUtil.getString(renderRequest,
				"fechaMesHta");
		String fechaDiaHta = ParamUtil.getString(renderRequest,
				"fechaDiaHta");
		String fechaAnioHta = ParamUtil.getString(renderRequest,
				"fechaAnioHta");
		Date fechaHta = null;
		try {
			fechaHta = formatoDeFecha.parse(fechaDiaHta
					+ "/" + (Integer.parseInt(fechaMesHta) + 1)
					+ "/" + fechaAnioHta);
		} catch (Exception e) {}
		String entidad = ParamUtil.getString(renderRequest, "entidad");
		String expediente = ParamUtil.getString(renderRequest, "expediente");
		String caratula = ParamUtil.getString(renderRequest, "caratula");
		String estado=ParamUtil.getString(renderRequest,"estado",null);
		String cuit=ParamUtil.getString(renderRequest,"cuit",null);
		String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
		
		DemandaJudicial filtro = new DemandaJudicial();
		
		filtro.setId(id);
		filtro.setTipo(tipo);
		filtro.setFechaDde(fechaDde);
		filtro.setFechaHta(fechaHta);
		filtro.setEntidad(entidad);
		filtro.setExpediente(expediente);
		filtro.setCaratula(caratula);
		filtro.setCuit(cuit);
		filtro.setSucursal(sucursal);
		filtro.setUltimoEstado(estado);
		
		List<DemandaJudicial> lista = DemandaJudicialServiceUtil.getLista(filtro,0);
		
		return generaReporteDemandas(lista);
	}

	private static HSSFWorkbook generaReporteDemandas(List<DemandaJudicial> list) {
				
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Demandas Judiciales");

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
		

		for(DemandaJudicial l: list){
			index=crearDatosDemandas(sheet,l, index, styleAll,
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
		cell.setCellValue(new HSSFRichTextString("Reporte Demandas Judiciales"));
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
		cell16H.setCellValue(new HSSFRichTextString("Entidad"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(col++);
		cell20H.setCellValue(new HSSFRichTextString("ID"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(col++);
		cell13H.setCellValue(new HSSFRichTextString("CUIT"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(col++);
		cell18H.setCellValue(new HSSFRichTextString("SUCURSAL"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(col++);
		cell19H.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell191H = rowHeader.createCell(col++);
		cell191H.setCellValue(new HSSFRichTextString("EXPEDIENTE"));
		cell191H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(col++);
		cell6H.setCellValue(new HSSFRichTextString("TIPO"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(col++);
		cell3H.setCellValue(new HSSFRichTextString("FECHA"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(col++);
		cell4H.setCellValue(new HSSFRichTextString("ESTADO"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(col++);
		cell17H.setCellValue(new HSSFRichTextString("MONTO"));
		cell17H.setCellStyle(styleBold);
		
		return index;
	}

		
	private static int crearDatosDemandas(HSSFSheet sheet,DemandaJudicial pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		int col = 0;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(col++);
		cell001.setCellValue(pre.getEntidad());
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(col++);
		cell002.setCellValue(pre.getId());
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell005 = rowHeader.createCell(col++);
		cell005.setCellValue(new HSSFRichTextString(pre.getCuit()));
		cell005.setCellStyle(styleAll);
		
		HSSFCell cell006 = rowHeader.createCell(col++);
		cell006.setCellValue(new HSSFRichTextString(pre.getSucursal()));
		cell006.setCellStyle(styleAll);
		
		HSSFCell cell007 = rowHeader.createCell(col++);
		cell007.setCellValue(new HSSFRichTextString(pre.getRazonSocial()));
		cell007.setCellStyle(styleAll);
		
		HSSFCell cell008 = rowHeader.createCell(col++);
		cell008.setCellValue(new HSSFRichTextString(pre.getExpediente()));
		cell008.setCellStyle(styleAll);
		
		HSSFCell cell009 = rowHeader.createCell(col++);
		cell009.setCellValue(new HSSFRichTextString(pre.getTipo()));
		cell009.setCellStyle(styleAll);
				
		HSSFCell cell011 = rowHeader.createCell(col++);
		cell011.setCellValue(new HSSFRichTextString(pre.getFechaAsString()));
		cell011.setCellStyle(styleAll);
		
		HSSFCell cell012 = rowHeader.createCell(col++);
		cell012.setCellValue(new HSSFRichTextString(estadosJud.get(pre.getUltimoEstado())));
		cell012.setCellStyle(styleAll);
		
		HSSFCell cell013 = rowHeader.createCell(col++);
		cell013.setCellValue(pre.getMontoOriginal() != null ? pre.getMontoOriginal() : 0);
		cell013.setCellStyle(styleMoney);
		
		return index++;
	}
        
}


