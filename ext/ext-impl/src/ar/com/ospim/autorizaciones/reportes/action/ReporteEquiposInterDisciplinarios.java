package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinarioExcel;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReporteEquiposInterDisciplinarios extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteEquiposInterDisciplinarios.class);


	public static HSSFWorkbook generaReporteEquiposInterdisciplinarios (
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		// *************************************************************
		// carga de variables recibidas de la JSP 
		// *************************************************************
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaOspimDia = ParamUtil.getString(renderRequest,"fechaDia");
		String fechaOspimMes = ParamUtil.getString(renderRequest,"fechaMes");
		String fechaOspimAnio = ParamUtil.getString(renderRequest,"fechaAnio");
		Date fechaOspim = null;
		
		try {
			fechaOspim = formatoDeFechas.parse(fechaOspimDia + "/"
					+ (Integer.parseInt(fechaOspimMes) + 1) + "/"
					+ fechaOspimAnio);
		} catch (Exception e) {
			fechaOspim = null;
		}
		
		
	// resto de parametros de la busqueda 			
		
		String estado = ParamUtil.getString(renderRequest, "estado", null);		
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		int nroRegistro = ParamUtil.getInteger(renderRequest, "nroRegistro", 0);			 			
		String cuilTitular = ParamUtil.getString(renderRequest,"cuil_titular", null);
		String motivo= ParamUtil.getString(renderRequest,"motivo", null);
	    
	    // *************************************************************		
		//  fin  de carga de variables de la JSP 
	    // *************************************************************		
		
		List<EquipoInterdisciplinarioExcel> registrosEquiposInterdisciplinarios= new ArrayList<EquipoInterdisciplinarioExcel>();

		try {
			registrosEquiposInterdisciplinarios= AutorizacionesServiceUtil.getListaEquiposInterdisciplinarios(fechaOspim , inte , cuilTitular ,nroRegistro , estado , motivo);
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de reclamos prestacionales",e);
			return null;
		}
		return generaReporte(registrosEquiposInterdisciplinarios);
	}

	private static HSSFWorkbook generaReporte(
			List<EquipoInterdisciplinarioExcel> list) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Ficha");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber= getStyleNumber(wb);
				
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte Equipos Interdisciplinarios: ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);		
		cell0H.setCellValue(new HSSFRichTextString("Nro Dictamen"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Fecha Dictamen"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Cuil Afiliado"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Inte"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Apellido Nombre"));
		cell4H.setCellStyle(styleBold);		

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Nro Doc"));
		cell5H.setCellStyle(styleBold);
		
	
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Seccional"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Diagnostico"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Cie X"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Fecha Vto"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Observaciones"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Participantes"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Diagnostico CIE X"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("plan Molineros"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Edad"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Prestación"));
		cell15H.setCellStyle(styleBold);
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Antecedentes"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Dictamen Médico Auditor"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Dictamen Asistente Social"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Dictamen Licenciado Kinesiologia"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Dictamen Legales"));
		cell20H.setCellStyle(styleBold);

		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Dictamen Equipo Interdisciplinario"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);		
		cell22H.setCellValue(new HSSFRichTextString("Estado"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell23H = rowHeader.createCell(++col);		
		cell23H.setCellValue(new HSSFRichTextString("Motivo Cierre"));
		cell23H.setCellStyle(styleBold);
		
				
		index++;
		
		for(EquipoInterdisciplinarioExcel  autorizaciones: list){
			index=crearDatosFicha(sheet, autorizaciones, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight);
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
		
		
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,EquipoInterdisciplinarioExcel   equipoInterdisciplinario , 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
		
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString( String.valueOf(equipoInterdisciplinario.getDictamen()  )   ));
		cell0.setCellStyle(styleNumber);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getFechaDictamen()!= null ? DateUtils.format(equipoInterdisciplinario.getFechaDictamen(), "dd/MM/yyyy") : "" ));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getCuilTitular() ));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(String.valueOf(equipoInterdisciplinario.getAfiliado().getInte())  ));
		cell3.setCellStyle(styleNumber);
		
		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getAfiliado().getApellidoNombre()));
		cell4.setCellStyle(styleNumber);
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDocuNumero()  ));
		cell5.setCellStyle(styleNumber);

		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getseccional()  ));
		cell6.setCellStyle(styleNumber);
		
		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDiagnostico()  ));
		cell7.setCellStyle(styleNumber);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getCieDiez() ));
		cell8.setCellStyle(styleNumber);
		
		HSSFCell cell9 = rowHeader.createCell(++col);		
		cell9.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getFechaVto() != null ? DateUtils.format(equipoInterdisciplinario.getFechaVto(), "dd/MM/yyyy") : ""));
		cell9.setCellStyle(styleNumber);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getObservaciones()  ));
		cell10.setCellStyle(styleNumber);
	

		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getParticipantes()  ));
		cell11.setCellStyle(styleNumber);

		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDiagnosticoCieDiez() ));
		cell12.setCellStyle(styleNumber);

		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getPlanMolineros()  ));
		cell13.setCellStyle(styleNumber);

		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(new HSSFRichTextString(String.valueOf(equipoInterdisciplinario.getEdad()) ));
		cell14.setCellStyle(styleNumber);

		HSSFCell cell15 = rowHeader.createCell(++col);
		cell15.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getprestacion()  ));
		cell15.setCellStyle(styleNumber);
		
		HSSFCell cell16 = rowHeader.createCell(++col);
		cell16.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getAntecedentes() ));
		cell16.setCellStyle(styleNumber);
		
		HSSFCell cell17 = rowHeader.createCell(++col);
		cell17.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDictamenMedicoAuditor() ));
		cell17.setCellStyle(styleNumber);
		
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDictamenAsistenteSocial()  ));
		cell18.setCellStyle(styleNumber);
		
		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDictamenLicinciadoKinesiologia() ));
		cell19.setCellStyle(styleNumber);
		
		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDictamenLegales()  ));
		cell20.setCellStyle(styleNumber);
		
		HSSFCell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getDictamenEquipoInterdisciplinario()  ));
		cell21.setCellStyle(styleNumber);
		
		HSSFCell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getEstadoRegEquipoInter() ));
		cell22.setCellStyle(styleNumber);
		
		HSSFCell cell23 = rowHeader.createCell(++col);
		cell23.setCellValue(new HSSFRichTextString(equipoInterdisciplinario.getMotivoCierreEquipoInter()   ));
		cell23.setCellStyle(styleNumber);
		
		
		
		}catch(Exception e){
			_log.error("Error al generar Excel Equipos en crearDatosFicha", e);			
		}
		
		return index++;
	}	

}
