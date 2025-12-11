package ar.com.ospim.autorizaciones.reportes.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.EstadisticaGastoMedico;
import ar.com.ospim.autorizaciones.beans.EstadisticaGastoMedicoDetalle;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.uoma.beans.CentroCosto;

@SuppressWarnings("unused")
public class ReporteEstadisticaGastoMedicoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePreautorizacionEstadosExcel.class);

	public static HSSFWorkbook generarReporteAgrupado(HSSFWorkbook wb,List<EstadisticaGastoMedico> list,Date fecha,Date fechaHta) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			 wb=generaReporteAgrupado(wb,list,fecha,fechaHta);
		} catch (Exception e) {
			_log.debug("Error al generar Estadistico de Gastos Médicos");
		}
		
		return wb;
	
	}

	public static HSSFWorkbook generaReporteAgrupado(HSSFWorkbook wb,
			List<EstadisticaGastoMedico> list,Date fecha,Date fechaHta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFSheet sheet = wb.createSheet("Agrupado G.Médico");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
		
		StringBuffer titulo1=new StringBuffer("Reporte De Estadísticas de Gastos Médicos");
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle( getStyleBoldAligned(wb, HorizontalAlignment.CENTER));
		
        index ++;
		HSSFRow rowHeaderANT1 = sheet.createRow(index);
		HSSFCell cell9HA = rowHeaderANT1.createCell(9);
		cell9HA.setCellValue(new HSSFRichTextString("Impreso: "+ sdf.format(hoy)));
		cell9HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("PERIODO"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("TIPO"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("DESCRIPCION"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("TOTAL"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("PORCENTAJE"));
		cell19H.setCellStyle(styleBold);
		
		index++;
		
		for(EstadisticaGastoMedico gasto: list){
			index=crearDatosAgrupado(sheet, gasto, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		sheet.createRow(index);
		
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
		
		return wb;
	}
	
	
	private static int crearDatosAgrupado(HSSFSheet sheet,EstadisticaGastoMedico pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(new HSSFRichTextString(pre.getPeriodo()));
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(pre.getTipo()));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(pre.getDescripcion()));
		cell020.setCellStyle(styleAll);
		
		
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		cell017.setCellValue(pre.getTotal());
		cell017.setCellStyle(styleMoney);
		
		HSSFCell cell019 = rowHeader.createCell(++col);
		cell019.setCellValue(pre.getPorcentaje());
		cell019.setCellStyle(styleMoney);
		
  	    return index++;
	}
	

	///////////////////////////
	///////////////////////////
	///////////////////////////
	
	public static HSSFWorkbook generarReporteDetallado(HSSFWorkbook wb,List<EstadisticaGastoMedicoDetalle> list,Date fecha,Date fechaHta) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			 wb=generaReporteDetallado(wb,list,fecha,fechaHta);
		} catch (Exception e) {
			_log.debug("Error al generar Estadistico de Gastos Médicos Detallado");
		}
		
		return wb;
	
	}

	public static HSSFWorkbook generaReporteDetallado(HSSFWorkbook wb,
			List<EstadisticaGastoMedicoDetalle> list,Date fecha,Date fechaHta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFSheet sheet = wb.createSheet("Detalle G.Médico");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
		
		StringBuffer titulo1=new StringBuffer("Reporte De Estadísticas de Gastos Médicos Detallado");
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle( getStyleBoldAligned(wb, HorizontalAlignment.CENTER));
		
        index ++;
		HSSFRow rowHeaderANT1 = sheet.createRow(index);
		HSSFCell cell9HA = rowHeaderANT1.createCell(9);
		cell9HA.setCellValue(new HSSFRichTextString("Impreso: "+ sdf.format(hoy)));
		cell9HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("CUIT"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("SUCURSAL"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell130H = rowHeader.createCell(++col);
		cell130H.setCellValue(new HSSFRichTextString("FECHA OP"));
		cell130H.setCellStyle(styleBold);
		
		HSSFCell cell131H = rowHeader.createCell(++col);
		cell131H.setCellValue(new HSSFRichTextString("NRO OP"));
		cell131H.setCellStyle(styleBold);
		
		HSSFCell cell132H = rowHeader.createCell(++col);
		cell132H.setCellValue(new HSSFRichTextString("ORIGEN"));
		cell132H.setCellStyle(styleBold);
		
		HSSFCell cell133H = rowHeader.createCell(++col);
		cell133H.setCellValue(new HSSFRichTextString("ID"));
		cell133H.setCellStyle(styleBold);
		
		HSSFCell cell134H = rowHeader.createCell(++col);
		cell134H.setCellValue(new HSSFRichTextString("TIPO"));
		cell134H.setCellStyle(styleBold);
		
		HSSFCell cell135H = rowHeader.createCell(++col);
		cell135H.setCellValue(new HSSFRichTextString("SECTOR"));
		cell135H.setCellStyle(styleBold);
		
		HSSFCell cell136H = rowHeader.createCell(++col);
		cell136H.setCellValue(new HSSFRichTextString("CANTIDAD"));
		cell136H.setCellStyle(styleBold);
		
		HSSFCell cell137H = rowHeader.createCell(++col);
		cell137H.setCellValue(new HSSFRichTextString("IMPORTE"));
		cell137H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("TOTAL"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("CARGO OSPIM"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("CARGO PRESTADORA"));
		cell21H.setCellStyle(styleBold);
		
		/*
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("TIPO GESTION"));
		cell22H.setCellStyle(styleBold);
		*/
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		cell23H.setCellStyle(styleBold);
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("INTE"));
		cell24H.setCellStyle(styleBold);
		
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("AFILIADO"));
		cell25H.setCellStyle(styleBold);
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Nro Reclamo"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell06H = rowHeader.createCell(++col);
		cell06H.setCellValue(new HSSFRichTextString("Baja Fecha"));
		cell06H.setCellStyle(styleBold);
		
		HSSFCell cell07H = rowHeader.createCell(++col);
		cell07H.setCellValue(new HSSFRichTextString("Amparo"));
		cell07H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Caso Asociado"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Seccional Afiliado"));
		cell17H.setCellStyle(styleBold);
		
	
		HSSFCell cell180H = rowHeader.createCell(++col);
		cell180H.setCellValue(new HSSFRichTextString("Plan Molineros"));
		cell180H.setCellStyle(styleBold);

		HSSFCell cell190H = rowHeader.createCell(++col);
		cell190H.setCellValue(new HSSFRichTextString("Plan Tercerizadora"));
		cell190H.setCellStyle(styleBold);
		
		
		HSSFCell cell200H = rowHeader.createCell(++col);
		cell200H.setCellValue(new HSSFRichTextString("Código"));
		cell200H.setCellStyle(styleBold);
	
	
		HSSFCell cell210H = rowHeader.createCell(++col);
		cell210H.setCellValue(new HSSFRichTextString("Prestación"));
		cell210H.setCellStyle(styleBold);
		
		HSSFCell cell29H = rowHeader.createCell(++col);
		cell29H.setCellValue(new HSSFRichTextString("Revisión Res."));
		cell29H.setCellStyle(styleBold);
		
		HSSFCell cell30H = rowHeader.createCell(++col);
		cell30H.setCellValue(new HSSFRichTextString("Resp Revisión"));
		cell30H.setCellStyle(styleBold);
		

		HSSFCell cell31H = rowHeader.createCell(++col);
		cell31H.setCellValue(new HSSFRichTextString("Observaciones Auditoría Médica"));
		cell31H.setCellStyle(styleBold);
		
		HSSFCell cell310H = rowHeader.createCell(++col);
		cell310H.setCellValue(new HSSFRichTextString("Observaciones Revisión"));
		cell310H.setCellStyle(styleBold);
		
		HSSFCell cell311H = rowHeader.createCell(++col);
		cell311H.setCellValue(new HSSFRichTextString("Observaciones Cierre"));
		cell311H.setCellStyle(styleBold);
		
		HSSFCell cell312H = rowHeader.createCell(++col);
		cell312H.setCellValue(new HSSFRichTextString("Justificación Médica"));
		cell312H.setCellStyle(styleBold);
		
		HSSFCell cell313H = rowHeader.createCell(++col);
		cell313H.setCellValue(new HSSFRichTextString("Dictamen Comisión"));
		cell313H.setCellStyle(styleBold);
		
		
		HSSFCell cell32H = rowHeader.createCell(++col);
		cell32H.setCellValue(new HSSFRichTextString("Fecha Cierre"));
		cell32H.setCellStyle(styleBold);
		
		HSSFCell cell33H = rowHeader.createCell(++col);
		cell33H.setCellValue(new HSSFRichTextString("Incluido Convenio"));
		cell33H.setCellStyle(styleBold);
		
		HSSFCell cell34H = rowHeader.createCell(++col);
		cell34H.setCellValue(new HSSFRichTextString("2 % "));
		cell34H.setCellStyle(styleBold);

		HSSFCell cell35H = rowHeader.createCell(++col);
		cell35H.setCellValue(new HSSFRichTextString("Débito Prestadora"));
		cell35H.setCellStyle(styleBold);
		
		HSSFCell cell36H = rowHeader.createCell(++col);
		cell36H.setCellValue(new HSSFRichTextString("Tipo Gestión"));
		cell36H.setCellStyle(styleBold);
		
		HSSFCell cell38H = rowHeader.createCell(++col);
		cell38H.setCellValue(new HSSFRichTextString("Lote"));
		cell38H.setCellStyle(styleBold);
		
		HSSFCell cell40H = rowHeader.createCell(++col);
		cell40H.setCellValue(new HSSFRichTextString("Fecha Envio Seccional"));
		cell40H.setCellStyle(styleBold);
		
		HSSFCell cell42H = rowHeader.createCell(++col);
		cell42H.setCellValue(new HSSFRichTextString("Recupero Sur"));
		cell42H.setCellStyle(styleBold);
		
		HSSFCell cell43H = rowHeader.createCell(++col);
		cell43H.setCellValue(new HSSFRichTextString("Integración"));
		cell43H.setCellStyle(styleBold);
		
		HSSFCell cell44H = rowHeader.createCell(++col);
		cell44H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell44H.setCellStyle(styleBold);
		
		index++;
		
		for(EstadisticaGastoMedicoDetalle gasto: list){
			index=crearDatosDetallado(sheet, gasto, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		sheet.createRow(index);
		
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
		sheet.autoSizeColumn((short) 14);
		sheet.autoSizeColumn((short) 15);
		sheet.autoSizeColumn((short) 16);
		sheet.autoSizeColumn((short) 17);
		sheet.autoSizeColumn((short) 18);
		sheet.autoSizeColumn((short) 19);
		sheet.autoSizeColumn((short) 20);
		sheet.autoSizeColumn((short) 21);
		sheet.autoSizeColumn((short) 22);
		sheet.autoSizeColumn((short) 23);
		sheet.autoSizeColumn((short) 24);
		sheet.autoSizeColumn((short) 25);
		sheet.autoSizeColumn((short) 26);
		sheet.autoSizeColumn((short) 27);
		sheet.autoSizeColumn((short) 28);
		sheet.autoSizeColumn((short) 29);
		sheet.autoSizeColumn((short) 30);
		sheet.autoSizeColumn((short) 31);
		sheet.autoSizeColumn((short) 32);
		sheet.autoSizeColumn((short) 33);
		sheet.autoSizeColumn((short) 34);
		sheet.autoSizeColumn((short) 35);
		sheet.autoSizeColumn((short) 36);
		sheet.autoSizeColumn((short) 37);
		sheet.autoSizeColumn((short) 38);
		sheet.autoSizeColumn((short) 39);
		sheet.autoSizeColumn((short) 40);
		sheet.autoSizeColumn((short) 41);
		
		return wb;
	}
	
	
	private static int crearDatosDetallado(HSSFSheet sheet,EstadisticaGastoMedicoDetalle pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(new HSSFRichTextString(pre.getCuit()));
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(pre.getSucursal()));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(pre.getRazonSocial()));
		cell020.setCellStyle(styleAll);

		HSSFCell cell021 = rowHeader.createCell(++col);
		cell021.setCellValue(new HSSFRichTextString(sdf.format(pre.getOrdenPagoFecha())));
		cell021.setCellStyle(styleAll);

		HSSFCell cell022 = rowHeader.createCell(++col);
		cell022.setCellValue(pre.getOrdenPagoId());
		cell022.setCellStyle(styleAll);

		HSSFCell cell023 = rowHeader.createCell(++col);
		cell023.setCellValue(new HSSFRichTextString(pre.getOrigen()));
		cell023.setCellStyle(styleAll);
		
		HSSFCell cell024 = rowHeader.createCell(++col);
		cell024.setCellValue(pre.getId());
		cell024.setCellStyle(styleAll);

		HSSFCell cell025 = rowHeader.createCell(++col);
		cell025.setCellValue(new HSSFRichTextString(pre.getTipo()));
		cell025.setCellStyle(styleAll);
		
		HSSFCell cell026 = rowHeader.createCell(++col);
		cell026.setCellValue(new HSSFRichTextString(pre.getSector()));
		cell026.setCellStyle(styleAll);
		
		HSSFCell cell027 = rowHeader.createCell(++col);
		cell027.setCellValue(pre.getCantidad());
		cell027.setCellStyle(styleMoney);
		
		HSSFCell cell028 = rowHeader.createCell(++col);
		cell028.setCellValue(pre.getImporte());
		cell028.setCellStyle(styleMoney);
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		cell017.setCellValue(pre.getTotal());
		cell017.setCellStyle(styleMoney);
		
		HSSFCell cell019 = rowHeader.createCell(++col);
		cell019.setCellValue(pre.getCargoOspim());
		cell019.setCellStyle(styleMoney);
		
		HSSFCell cell029 = rowHeader.createCell(++col);
		cell029.setCellValue(pre.getCargoPrestadora());
		cell029.setCellStyle(styleMoney);

		/*
		HSSFCell cell030 = rowHeader.createCell(++col);
		cell030.setCellValue(pre.getIdTipoGestion());
		cell030.setCellStyle(styleAll);
		*/
		
		HSSFCell cell031 = rowHeader.createCell(++col);
		cell031.setCellValue(new HSSFRichTextString(pre.getAfiliado().getCuil_titular() ));
		cell031.setCellStyle(styleAll);
		
		HSSFCell cell032 = rowHeader.createCell(++col);
		cell032.setCellValue(pre.getAfiliado().getInte());
		cell032.setCellStyle(styleAll);
		
		HSSFCell cell033 = rowHeader.createCell(++col);
		cell033.setCellValue(new HSSFRichTextString(pre.getAfiliado().getNombre() ));
		cell033.setCellStyle(styleAll);
		
		/////----------------------------------
		HSSFCell cell01 = rowHeader.createCell(++col);
		cell01.setCellValue(new HSSFRichTextString(String.valueOf(pre.getReclamo().getNroReclamo() )));
		cell01.setCellStyle(styleAll);
		
		HSSFCell cell07 = rowHeader.createCell(++col);
		cell07.setCellValue(new HSSFRichTextString(pre.getReclamo().getBaja_fechaAsString()));
		cell07.setCellStyle(styleAll);
		
		HSSFCell cell08 = rowHeader.createCell(++col);
		cell08.setCellValue(new HSSFRichTextString(pre.getReclamo().getAmparoTexto()));
		cell08.setCellStyle(styleAll);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString( String.valueOf( pre.getReclamo().getCaso_vinculado()) ));
		cell12.setCellStyle(styleNumber);
		
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(pre.getReclamo().getTextoSeccional()   ));
		cell18.setCellStyle(styleAll);
		
		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(pre.getAfiliado().getNombrePlan() ));
		cell19.setCellStyle(styleAll);
		
		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(new HSSFRichTextString(pre.getReclamo().getPlanPrevencion()));
		cell20.setCellStyle(styleAll);
		
		HSSFCell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(new HSSFRichTextString( String.valueOf( pre.getReclamo().getPrestacion().getCodigo() ) ));
		cell21.setCellStyle(styleNumber);
	
		HSSFCell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(new HSSFRichTextString(pre.getReclamo().getPrestacion().getDescripcion()));
		cell22.setCellStyle(styleAll);
		
		HSSFCell cell30= rowHeader.createCell(++col);
		cell30.setCellValue(new HSSFRichTextString(pre.getReclamo().getPrestacionRevisionResolucion()  ));
		cell30.setCellStyle(styleAll);

		HSSFCell cell31= rowHeader.createCell(++col);
		cell31.setCellValue(new HSSFRichTextString(pre.getReclamo().getPrestacionRevisionResponsable()));
		cell31.setCellStyle(styleAll);
		

		HSSFCell cell32= rowHeader.createCell(++col);
		cell32.setCellValue(new HSSFRichTextString(pre.getReclamo().getObsAuditoriaMedica()));
		cell32.setCellStyle(styleAll);
		

		HSSFCell cell321= rowHeader.createCell(++col);
		cell321.setCellValue(new HSSFRichTextString(pre.getReclamo().getObsRevision()));
		cell321.setCellStyle(styleAll);
		
		HSSFCell cell322= rowHeader.createCell(++col);
		cell322.setCellValue(new HSSFRichTextString(pre.getReclamo().getObsCierre()));
		cell322.setCellStyle(styleAll);
		
		HSSFCell cell323= rowHeader.createCell(++col);
		cell323.setCellValue(new HSSFRichTextString(pre.getReclamo().getJustificacionMedica()));
		cell323.setCellStyle(styleAll);
		
		HSSFCell cell324= rowHeader.createCell(++col);
		cell324.setCellValue(new HSSFRichTextString(pre.getReclamo().getDictamenComision()));
		cell324.setCellStyle(styleAll);
		
		HSSFCell cell33= rowHeader.createCell(++col);		
		cell33.setCellValue(new HSSFRichTextString(pre.getReclamo().getFecha_cierre_Texto() ));	
		cell33.setCellStyle(styleDate);
		
		HSSFCell cell34= rowHeader.createCell(++col);
		cell34.setCellValue(new HSSFRichTextString(pre.getReclamo().getCierreIncluidoGerenciadoraTexto()  ));
		cell34.setCellStyle(styleAll);

		HSSFCell cell35= rowHeader.createCell(++col);
		cell35.setCellValue(new HSSFRichTextString(pre.getReclamo().getCierreDosPorCientoTexto() ));	
		cell35.setCellStyle(styleAll);

		HSSFCell cell36= rowHeader.createCell(++col);
		cell36.setCellValue(new HSSFRichTextString(pre.getReclamo().getCierreDebitoPrestadoraTexto()   ));	
		cell36.setCellStyle(styleAll);
		
		HSSFCell cell37= rowHeader.createCell(++col);
		cell37.setCellValue(new HSSFRichTextString(pre.getReclamo().getCierreTipoGestion()  ));	
		cell37.setCellStyle(styleAll);
		
		HSSFCell cell39= rowHeader.createCell(++col);
		if (pre.getReclamo().getNroLote() !=null){
			cell39.setCellValue(pre.getReclamo().getNroLote());
		}
		else{
			cell39.setCellValue(new HSSFRichTextString(""));
		}
		cell39.setCellStyle(styleAll);
		
		HSSFCell cell42 = rowHeader.createCell(++col);
		if (pre.getReclamo().getFechaMailSeccional() !=null){
		cell42.setCellValue(new HSSFRichTextString(sdf.format(pre.getReclamo().getFechaMailSeccional())));
		}else {
			cell42.setCellValue(new HSSFRichTextString(""));	
		}
		cell42.setCellStyle(styleDate);
		
		HSSFCell cell44= rowHeader.createCell(++col);
		cell44.setCellValue(pre.getReclamo().getReclamoRecuperable());
		cell44.setCellStyle(styleAll);
		
		
		HSSFCell cell45= rowHeader.createCell(++col);
		if (pre.getReclamo().getDescIntegracion() !=null){
			cell45.setCellValue(pre.getReclamo().getDescIntegracion());
		}
		else{
			cell45.setCellValue(new HSSFRichTextString(""));
		}
		cell45.setCellStyle(styleAll);
		
		HSSFCell cell46= rowHeader.createCell(++col);
		if (pre.getAfiliado().getDiscapacitado() !=null){
			cell46.setCellValue(pre.getAfiliado().getDiscapacitado());
		}
		else{
			cell46.setCellValue(new HSSFRichTextString(""));
		}
		cell46.setCellStyle(styleAll);
		
		return index++;
	}

	
		
}


