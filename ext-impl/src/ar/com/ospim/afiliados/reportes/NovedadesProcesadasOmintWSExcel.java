package ar.com.ospim.afiliados.reportes;

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
import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.automatico.beans.MensajeEnvioyRespuestaWSOmint;
import ar.com.ospim.automatico.service.AgendaReporteUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class NovedadesProcesadasOmintWSExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(NovedadesProcesadasOmintWSExcel.class);

	public static HSSFWorkbook generaPlanillaNovedadesProcesadas(List<MensajeEnvioyRespuestaWSOmint> novedades) {

		try {
			AgendaReporteUtil agendaRepoUtil = new AgendaReporteUtil();
			
			if(novedades == null){
//				Calendar c = Calendar.getInstance();
//				c.set(2014, 6, 01);
//				System.out.println(c.getTime());
//				novedades = agendaRepoUtil.getNovedadesProcesadas(c.getTime());
				novedades = agendaRepoUtil.getNovedadesProcesadas(new Date());
			}
			
			return generarReporte(novedades);
		} catch (Exception e) {
			_log.error("Error al generar reporte novedades Omint WS", e);
			return null;
		}
	}
	
	public static HSSFWorkbook generaPlanillaNovedadesProcesadas(
			HttpServletRequest req, HttpServletResponse res, Date fechaProceso) {

		try {
			AgendaReporteUtil agendaRepoUtil = new AgendaReporteUtil();
			
			List<MensajeEnvioyRespuestaWSOmint> novedades = agendaRepoUtil.getNovedadesProcesadas(fechaProceso);

			return generarReporte(novedades);
		} catch (Exception e) {
			_log.error("Error al generar reporte novedades Omint WS", e);
			return null;
		}
	}
	

	private static HSSFWorkbook generarReporte(List<MensajeEnvioyRespuestaWSOmint> novedades) {
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet);
			index++;
			for (MensajeEnvioyRespuestaWSOmint nove : novedades) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(String.valueOf(nove.getIdOspim())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(nove.getSeccional()));
				cell1.setCellStyle(styleAllWithBorder);				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(nove.getCUILTitular()));
				cell2.setCellStyle(styleAllWithBorder);		
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(nove.getCUIL()));
				cell3.setCellStyle(styleAllWithBorder);		
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf(nove.getInte())));
				cell4.setCellStyle(styleAllWithBorder);
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(nove.getParentesco()));
				cell5.setCellStyle(styleAllWithBorder);		
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(nove.getApellido()));
				cell6.setCellStyle(styleAllWithBorder);	
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(nove.getNombre()));
				cell7.setCellStyle(styleAllWithBorder);	
				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(nove.getTipoDoc()));
				cell8.setCellStyle(styleAllWithBorder);	
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(new HSSFRichTextString(nove.getNroDoc()));
				cell9.setCellStyle(styleAllWithBorder);	
				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(new HSSFRichTextString(nove.getSexo().toUpperCase()));
				cell10.setCellStyle(styleAllWithBorder);	
				HSSFCell cell11 = row.createCell(column++);				
				if(nove.getFecNac() != null){
					try{
						cell11.setCellValue(new HSSFRichTextString(format.format(nove.getFecNac().getTime())));
						cell11.setCellStyle(styleAllWithBorder);
					}catch (Exception e) {
						_log.error("Error Parseando fecha de nacimiento");
						_log.error("fecha de nacimiento: " + nove.getFecNac());
						_log.error(e);
					}	
				}else{
					cell11.setCellValue(new HSSFRichTextString(""));
					cell11.setCellStyle(styleAllWithBorder);
				}
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(new HSSFRichTextString(nove.getEstadoCivilDesc()));
				cell12.setCellStyle(styleAllWithBorder);	
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(nove.getNacionalidadDesc()));
				cell13.setCellStyle(styleAllWithBorder);	
				HSSFCell cell14 = row.createCell(column++);
				cell14.setCellValue(new HSSFRichTextString(nove.getProvincia()));
				cell14.setCellStyle(styleAllWithBorder);	
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(nove.getLocalidad()));
				cell15.setCellStyle(styleAllWithBorder);	
				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(nove.getCP()));
				cell16.setCellStyle(styleAllWithBorder);	
				HSSFCell cell17 = row.createCell(column++);
				cell17.setCellValue(new HSSFRichTextString(nove.getCalle()));
				cell17.setCellStyle(styleAllWithBorder);	
				HSSFCell cell18 = row.createCell(column++);
				cell18.setCellValue(new HSSFRichTextString(nove.getNroCalle()));
				cell18.setCellStyle(styleAllWithBorder);	
				HSSFCell cell19 = row.createCell(column++);
				cell19.setCellValue(new HSSFRichTextString(nove.getResto())); // Piso/Dpto
				cell19.setCellStyle(styleAllWithBorder);	
				HSSFCell cell20 = row.createCell(column++);
				cell20.setCellValue(new HSSFRichTextString(nove.getTelefono()));
				cell20.setCellStyle(styleAllWithBorder);
				HSSFCell cell32 = row.createCell(column++);
				cell32.setCellValue(new HSSFRichTextString(nove.getCategoriaDesc()));
				cell32.setCellStyle(styleAllWithBorder);
				HSSFCell cell21 = row.createCell(column++);
				cell21.setCellValue(new HSSFRichTextString(nove.getPlanDesc()));
				cell21.setCellStyle(styleAllWithBorder);	
				HSSFCell cell22 = row.createCell(column++);
				if(nove.getFecVig() != null){
					cell22.setCellValue(new HSSFRichTextString(format.format(nove.getFecVig().getTime())));
					cell22.setCellStyle(styleAllWithBorder);
				}else{
					cell22.setCellValue(new HSSFRichTextString(""));
					cell22.setCellStyle(styleAllWithBorder);
				}
				HSSFCell cell23 = row.createCell(column++);
				if(nove.getFecBaja() != null){
					cell23.setCellValue(new HSSFRichTextString(format.format(nove.getFecBaja().getTime())));
					cell23.setCellStyle(styleAllWithBorder);
				}else{
					cell23.setCellValue(new HSSFRichTextString(""));
					cell23.setCellStyle(styleAllWithBorder);
				}
				HSSFCell cell24 = row.createCell(column++);
				cell24.setCellValue(new HSSFRichTextString(nove.getCuitLaboral()));
				cell24.setCellStyle(styleAllWithBorder);
				HSSFCell cell25 = row.createCell(column++);
				cell25.setCellValue(new HSSFRichTextString(nove.getRazonSocLaboral()));
				cell25.setCellStyle(styleAllWithBorder);
				HSSFCell cell26 = row.createCell(column++);
				cell26.setCellValue(new HSSFRichTextString(nove.getDiscapacidad()));
				cell26.setCellStyle(styleAllWithBorder);
				HSSFCell cell27 = row.createCell(column++);
				if(nove.getFPP() != null){
					cell27.setCellValue(new HSSFRichTextString(format.format(nove.getFPP().getTime())));
					cell27.setCellStyle(styleAllWithBorder);
				}else{
					cell27.setCellValue(new HSSFRichTextString(""));
					cell27.setCellStyle(styleAllWithBorder);				
				}
				String opeDesc = "";
				switch (nove.getOperacion()) {
				case 0:
					opeDesc = "Alta Grupo Familiar";
					break;
				case 1:
					opeDesc = "Alta Beneficiario";
					break;
				case 2:
					opeDesc = "Modifica Beneficiario";
					break;
				case 3:
					opeDesc = "Baja Grupo Familiar";
					break;
				case 4:
					opeDesc = "Baja Beneficiario";
					break;
				case 5:
					opeDesc = "Modifica Plan Grupo Familiares";
					break;	
				default:
					opeDesc = "Desconocido" ;
					break;
				}

				HSSFCell cell28 = row.createCell(column++);
				cell28.setCellValue(new HSSFRichTextString(opeDesc));
				cell28.setCellStyle(styleAllWithBorder);
				HSSFCell cell29 = row.createCell(column++);
				cell29.setCellValue(new HSSFRichTextString(String.valueOf(nove.getId_transaction())));
				cell29.setCellStyle(styleAllWithBorder);
				HSSFCell cell30 = row.createCell(column++);
				cell30.setCellValue(new HSSFRichTextString(nove.getMessageErrorCode()));
				cell30.setCellStyle(styleAllWithBorder);
				HSSFCell cell31 = row.createCell(column++);
				cell31.setCellValue(new HSSFRichTextString(nove.getMessageDescription()));
				cell31.setCellStyle(styleAllWithBorder);

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
						

		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Novedades enviadas por Web Service "));
		cell.setCellStyle(styleHeaderEnca);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		
		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
//		StringBuffer aux = new StringBuffer("Destino: ");
//		aux.append(destino).append(" Lugar Recepción: ").append(lugarRecepcion)
//				.append("Razón Soc. Prestador: " + razonPrestador);
//		aux.append(" Provincia: ").append(provincia).append(" Localidad: ")
//				.append(localidad).append(" Seccional: ").append(seccional);
//		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
//		cell1.setCellStyle(styleHeaderEnca2);
//
//		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("ID OSPIM"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Seccional"));
		cell31.setCellStyle(styleHeaderEnca2);

		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Cuil"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Inte"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Parentesco"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Apellido"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Nombre"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Tipo Doc."));
		cell38.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell39 = row3a.createCell(column++);
		cell39.setCellValue(new HSSFRichTextString("Nro. Doc."));
		cell39.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell41 = row3a.createCell(column++);
		cell41.setCellValue(new HSSFRichTextString("Sexo"));
		cell41.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell40 = row3a.createCell(column++);
		cell40.setCellValue(new HSSFRichTextString("F. Nacimiento"));
		cell40.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell42 = row3a.createCell(column++);
		cell42.setCellValue(new HSSFRichTextString("Estado Civil"));
		cell42.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell43 = row3a.createCell(column++);
		cell43.setCellValue(new HSSFRichTextString("Nacionalidad"));
		cell43.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell44 = row3a.createCell(column++);
		cell44.setCellValue(new HSSFRichTextString("Provincia"));
		cell44.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell45 = row3a.createCell(column++);
		cell45.setCellValue(new HSSFRichTextString("Localidad"));
		cell45.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell46 = row3a.createCell(column++);
		cell46.setCellValue(new HSSFRichTextString("Cod.Postal"));
		cell46.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell47 = row3a.createCell(column++);
		cell47.setCellValue(new HSSFRichTextString("Calle"));
		cell47.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell48 = row3a.createCell(column++);
		cell48.setCellValue(new HSSFRichTextString("N°"));
		cell48.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell49 = row3a.createCell(column++);
		cell49.setCellValue(new HSSFRichTextString("Piso/Dpto."));
		cell49.setCellStyle(styleHeaderEnca2);
		
//		HSSFCell cell50 = row3a.createCell(column++);
//		cell50.setCellValue(new HSSFRichTextString("Dpto."));
//		cell50.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell51 = row3a.createCell(column++);
		cell51.setCellValue(new HSSFRichTextString("Telefono"));
		cell51.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell50 = row3a.createCell(column++);
		cell50.setCellValue(new HSSFRichTextString("Categoria"));
		cell50.setCellStyle(styleHeaderEnca2);

		HSSFCell cell52 = row3a.createCell(column++);
		cell52.setCellValue(new HSSFRichTextString("Plan"));
		cell52.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell53 = row3a.createCell(column++);
		cell53.setCellValue(new HSSFRichTextString("F. Ingreso"));
		cell53.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell54 = row3a.createCell(column++);
		cell54.setCellValue(new HSSFRichTextString("F. Baja"));
		cell54.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell55 = row3a.createCell(column++);
		cell55.setCellValue(new HSSFRichTextString("CUIT"));
		cell55.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell56 = row3a.createCell(column++);
		cell56.setCellValue(new HSSFRichTextString("Razon Social"));
		cell56.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell57 = row3a.createCell(column++);
		cell57.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell57.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell58 = row3a.createCell(column++);
		cell58.setCellValue(new HSSFRichTextString("FPP"));
		cell58.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell59 = row3a.createCell(column++);
		cell59.setCellValue(new HSSFRichTextString("Operacion"));
		cell59.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell60 = row3a.createCell(column++);
		cell60.setCellValue(new HSSFRichTextString("Id Transaccion"));
		cell60.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell61 = row3a.createCell(column++);
		cell61.setCellValue(new HSSFRichTextString("Cod. error"));
		cell61.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell62 = row3a.createCell(column++);
		cell62.setCellValue(new HSSFRichTextString("Descripcion error"));
		cell62.setCellStyle(styleHeaderEnca2);

		return index;
	}
	
}
