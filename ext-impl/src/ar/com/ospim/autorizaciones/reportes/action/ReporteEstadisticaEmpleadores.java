package ar.com.ospim.autorizaciones.reportes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;


public class ReporteEstadisticaEmpleadores extends ReporteXLS {
	private static Log logger = LogFactoryUtil.getLog(ReporteEstadisticaEmpleadores.class);
	static BigDecimal totalBoletas;
	static double totalBoletasSinDDJJ;
	
	public static HSSFWorkbook generaReporte(List<FichaBoletaPortal> fichasBoletas,List<FichaBoletaPortal> fichasSinDDJJ, int cantDDJJFinale, int cantDDJJ, int empresasActiva,String periodo) {
		return generaReporteEstadisticaEmpleadores(fichasBoletas,fichasSinDDJJ,   cantDDJJFinale,  cantDDJJ, empresasActiva,periodo);
	}

	
	private static HSSFWorkbook generaReporteEstadisticaEmpleadores(List<FichaBoletaPortal> fichasBoletas,List<FichaBoletaPortal> fichasSinDDJJ,  int cantDDJJFinale, int cantDDJJ, int empresasActiva, String periodo) {
		
		
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			Date hoy=new Date();
			
			int fichasBoleta = fichasBoletas.size();
			int fichasSin = fichasSinDDJJ.size();
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("BOLETA " + periodo);

			HSSFPrintSetup ps = sheet.getPrintSetup();
			sheet.setAutobreaks(true);
			ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
			ps.setFitHeight((short) 0);
			ps.setFitWidth((short) 1);

			HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
			HSSFCellStyle styleNumber=  getStyleNumber(wb);
			HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
			HSSFCellStyle syleAll = getStyleAll(wb);	
			HSSFCellStyle verticalCenter = getStyleAlignVerticalCenter(wb);	
			HSSFCellStyle boldAlignedCenter = getStyleBoldAligned(wb,HorizontalAlignment.CENTER);
			HSSFCellStyle fondoGrisWithBorder =  getStyleFondoGrisWithBorder(wb);
			HSSFCellStyle borderTop =  getBorderTop(wb);
			HSSFCellStyle styleRight = getStyleRight(wb);
			
			String  titulo1= "DETALLE DE BOLETAS";
		
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
					
			int index = 0;		
			int col = -1;
			HSSFRow rowHeaderANT = sheet.createRow(index);		
			HSSFCell cell0HA = rowHeaderANT.createCell(0);
			
			cell0HA.setCellValue(new HSSFRichTextString(titulo1));
			cell0HA.setCellStyle(boldAlignedCenter);
			
			index++;
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
			HSSFRow rowsBoletas = sheet.createRow(index); 

			
			sheet.addMergedRegion(new CellRangeAddress(2, fichasBoleta +1 , 0, 1));
			rowsBoletas = sheet.createRow(index); 
				
		
			
			//Boleta
			sheet.addMergedRegion(new CellRangeAddress(1, 1 , 2, 4));
			rowsBoletas = sheet.createRow(index); 
			col = col +2;
			HSSFCell cell016H = rowsBoletas.createCell(++col);
			cell016H.setCellValue(new HSSFRichTextString("Boleta"));
			cell016H.setCellStyle(fondoGrisWithBorder);
			
			col = col +2;
			HSSFCell cell16H = rowsBoletas.createCell(++col);
			cell16H.setCellValue(new HSSFRichTextString("Convenio"));
			cell16H.setCellStyle(fondoGrisWithBorder);
						
			HSSFCell cell20H = rowsBoletas.createCell(++col);
			cell20H.setCellValue(new HSSFRichTextString("Cuenta"));
			cell20H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell21H = rowsBoletas.createCell(++col);
			cell21H.setCellValue(new HSSFRichTextString("Cantidad"));
			cell21H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell13H = rowsBoletas.createCell(++col);
			cell13H.setCellValue(new HSSFRichTextString("Capital"));
			cell13H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell18H = rowsBoletas.createCell(++col);
			cell18H.setCellValue(new HSSFRichTextString("Interés"));
			cell18H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell19H = rowsBoletas.createCell(++col);
			cell19H.setCellValue(new HSSFRichTextString("Ajuste Capital"));
			cell19H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell6H = rowsBoletas.createCell(++col);
			cell6H.setCellValue(new HSSFRichTextString("Ajuste Interés"));
			cell6H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell12H = rowsBoletas.createCell(++col);
			cell12H.setCellValue(new HSSFRichTextString("Total"));
			cell12H.setCellStyle(fondoGrisWithBorder);
			
			index++;				
			index = crearDatosBoletasConDDJJ(sheet, rowsBoletas,  index,fichasBoletas, styleAll,  styleNumber, verticalCenter, styleMoney, borderTop );
		
			index++;
		
	
			sheet.addMergedRegion(new CellRangeAddress(8, 8 , 0, 1));
			rowsBoletas = sheet.createRow(index); 
			
			sheet.addMergedRegion(new CellRangeAddress(9, 9 +fichasSin-1  , 0, 1));
			rowsBoletas = sheet.createRow(index); 
							
			index++;
			col = 1;
			HSSFCell cell010B = rowsBoletas.createCell(++col);
			cell010B.setCellValue(new HSSFRichTextString("Entidad"));
			cell010B.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010C = rowsBoletas.createCell(++col);
			cell010C.setCellValue(new HSSFRichTextString("Desglose"));
			cell010C.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010D = rowsBoletas.createCell(++col);
			cell010D.setCellValue(new HSSFRichTextString("Motivo"));
			cell010D.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010E = rowsBoletas.createCell(++col);
			cell010E.setCellValue(new HSSFRichTextString("Convenio"));
			cell010E.setCellStyle(fondoGrisWithBorder);

			HSSFCell cell010F = rowsBoletas.createCell(++col);
			cell010F.setCellValue(new HSSFRichTextString("Cuenta"));
			cell010F.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010G = rowsBoletas.createCell(++col);
			cell010G.setCellValue(new HSSFRichTextString("Cantidad"));
			cell010G.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010H = rowsBoletas.createCell(++col);
			cell010H.setCellValue(new HSSFRichTextString("Capital"));
			cell010H.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010I = rowsBoletas.createCell(++col);
			cell010I.setCellValue(new HSSFRichTextString("Interés"));
			cell010I.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010J = rowsBoletas.createCell(++col);
			cell010J.setCellValue(new HSSFRichTextString(" - "));
			cell010J.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010K = rowsBoletas.createCell(++col);
			cell010K.setCellValue(new HSSFRichTextString(" - "));
			cell010K.setCellStyle(fondoGrisWithBorder);
			
			HSSFCell cell010L = rowsBoletas.createCell(++col);
			cell010L.setCellValue(new HSSFRichTextString("Total"));
			cell010L.setCellStyle(fondoGrisWithBorder);
					
			index = crearDatosBoletasSinDDJJ(sheet, rowsBoletas,  index,fichasSinDDJJ, styleAll,  styleNumber, verticalCenter, styleMoney, borderTop );
				
			index++;
			//sheet.createRow(0);
			
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

			
			index = index +2;
			
			rowsBoletas = sheet.createRow(index); 

			sheet.addMergedRegion(new CellRangeAddress(index, index   , 0, 7));
			HSSFCell cell19A = rowsBoletas.createCell(0);
			cell19A.setCellValue(new HSSFRichTextString("Cantidad de DDJJ finales creadas (teniendo en cuenta solo la ultima ddjj de cada periodo   "));
			cell19A.setCellStyle(styleRight);
			
			HSSFCell cell19F = rowsBoletas.createCell(8);
			cell19F.setCellValue(cantDDJJFinale);
			cell19F.setCellStyle(syleAll);
			
			index++;
			

			rowsBoletas = sheet.createRow(index); 

			sheet.addMergedRegion(new CellRangeAddress(index, index   , 0, 7));
			HSSFCell cell20A = rowsBoletas.createCell(0);
			cell20A.setCellValue(new HSSFRichTextString("Cantidad total de ddjj (originales + rectificativas)   "));
			cell20A.setCellStyle(styleRight);
			
			
			HSSFCell cell20F = rowsBoletas.createCell(8);
			cell20F.setCellValue(cantDDJJ);
			cell20F.setCellStyle(syleAll);
			
			index++;
			rowsBoletas = sheet.createRow(index); 

			sheet.addMergedRegion(new CellRangeAddress(index, index   , 0, 7));
			HSSFCell cell30A = rowsBoletas.createCell(0);
			cell30A.setCellValue(new HSSFRichTextString("Cantidad de empleadores que utilizaron el sistema   "));
			cell30A.setCellStyle(styleRight);
			
			
			HSSFCell cell30F = rowsBoletas.createCell(8);
			cell30F.setCellValue(empresasActiva);
			cell30F.setCellStyle(syleAll);

			
			return wb;
	
	}
	
	private static int crearDatosBoletasConDDJJ(HSSFSheet sheet,HSSFRow rowsBoletas,
			int index,List<FichaBoletaPortal> fichasBoletas ,HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle verticalCenter,HSSFCellStyle styleMoney, HSSFCellStyle borderTop) {
				
		int indexAux = index;
		int col = 1;
		int contadorRow = 1; 
		
		totalBoletas = new BigDecimal(0);
		
		for (FichaBoletaPortal fichaBoletaPortal : fichasBoletas) {
			contadorRow++;
			
			if ("UOMAAS".equalsIgnoreCase(fichaBoletaPortal.getDescripcion())) {
				//Boleta
				sheet.addMergedRegion(new CellRangeAddress(contadorRow, contadorRow , 2, 4));
				rowsBoletas = sheet.createRow(indexAux); 
				HSSFCell cell100 = rowsBoletas.createCell(++col);
				cell100.setCellValue(new HSSFRichTextString(fichaBoletaPortal.getDescripcion()));
				cell100.setCellStyle(styleAll);
				//Convenio
				col = col +2;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString("5784"));
				cell101.setCellStyle(styleAll);
				//Cuenta 
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString("79090/11"));
				cell102.setCellStyle(styleAll);
				
				//Boletas con DDJJ
				HSSFCell cell015H = rowsBoletas.createCell(0);
				cell015H.setCellValue(new HSSFRichTextString("Boletas con DDJJ"));
				cell015H.setCellStyle(verticalCenter);	
				
				col =crearDatosBoletasConDDJJHelper( sheet, rowsBoletas,
						 indexAux, fichaBoletaPortal,  col, styleAll ,  styleMoney,  borderTop);
			
			}else if ("ART46".equalsIgnoreCase(fichaBoletaPortal.getDescripcion())) {
				col = 1;
				indexAux++;	
				//Boleta
				sheet.addMergedRegion(new CellRangeAddress(contadorRow, contadorRow , 2, 4));
				rowsBoletas = sheet.createRow(indexAux); 
				HSSFCell cell200 = rowsBoletas.createCell(++col);
				cell200.setCellValue(new HSSFRichTextString(fichaBoletaPortal.getDescripcion()));
				cell200.setCellStyle(styleAll);
				//Convenio
				col = col +2;
				HSSFCell cell201 = rowsBoletas.createCell(++col);
				cell201.setCellValue(new HSSFRichTextString("5788"));
				cell201.setCellStyle(styleAll);
				//Cuenta 
				HSSFCell cell202 = rowsBoletas.createCell(++col);
				cell202.setCellValue(new HSSFRichTextString("78732/11"));
				cell202.setCellStyle(styleAll);
				col =crearDatosBoletasConDDJJHelper( sheet, rowsBoletas,indexAux, fichaBoletaPortal,  col, styleAll ,  styleMoney,  borderTop);
		}else if( "AMTIMACS".equalsIgnoreCase(fichaBoletaPortal.getDescripcion())) {
				col = 1;
				indexAux++;
				//Boleta
				sheet.addMergedRegion(new CellRangeAddress(contadorRow, contadorRow , 2, 4));
				rowsBoletas = sheet.createRow(indexAux); 
				HSSFCell cell300 = rowsBoletas.createCell(++col);
				cell300.setCellValue(new HSSFRichTextString(fichaBoletaPortal.getDescripcion()));
				cell300.setCellStyle(styleAll);
				//Convenio
				col = col +2;
				HSSFCell cell301 = rowsBoletas.createCell(++col);
				cell301.setCellValue(new HSSFRichTextString("5652"));
				cell301.setCellStyle(styleAll);
				//Cuenta 
				HSSFCell cell302 = rowsBoletas.createCell(++col);
				cell302.setCellValue(new HSSFRichTextString("59538/10"));
				cell302.setCellStyle(styleAll);
				col =crearDatosBoletasConDDJJHelper( sheet, rowsBoletas,indexAux, fichaBoletaPortal,  col, styleAll ,  styleMoney,  borderTop);
		}else if ("UOMACS".equalsIgnoreCase(fichaBoletaPortal.getDescripcion())) {
			
				col = 1;
				indexAux++;
				//Boleta
				sheet.addMergedRegion(new CellRangeAddress(contadorRow, contadorRow , 2, 4));
				rowsBoletas = sheet.createRow(indexAux); 
				HSSFCell cell400 = rowsBoletas.createCell(++col);
				cell400.setCellValue(new HSSFRichTextString(fichaBoletaPortal.getDescripcion()));
				cell400.setCellStyle(styleAll);
				//Convenio
				col = col +2;
				HSSFCell cell401 = rowsBoletas.createCell(++col);
				cell401.setCellValue(new HSSFRichTextString("5783"));
				cell401.setCellStyle(styleAll);
				//Cuenta 
				HSSFCell cell402 = rowsBoletas.createCell(++col);
				cell402.setCellValue(new HSSFRichTextString("79781/54"));
				cell402.setCellStyle(styleAll);
				col =crearDatosBoletasConDDJJHelper( sheet, rowsBoletas,indexAux, fichaBoletaPortal,  col, styleAll ,  styleMoney,  borderTop);
		}else if ("UOMACU".equalsIgnoreCase(fichaBoletaPortal.getDescripcion())) {
				col = 1;
				indexAux++;
				//Boleta
				sheet.addMergedRegion(new CellRangeAddress(contadorRow, contadorRow , 2, 4));
				rowsBoletas = sheet.createRow(indexAux); 
			    HSSFCell cell500 = rowsBoletas.createCell(++col);
				cell500.setCellValue(new HSSFRichTextString(fichaBoletaPortal.getDescripcion()));
				cell500.setCellStyle(styleAll);
				//Convenio
				col = col +2;
				HSSFCell cell501 = rowsBoletas.createCell(++col);
				cell501.setCellValue(new HSSFRichTextString("5783"));
				cell501.setCellStyle(styleAll);
				//Cuenta 
				HSSFCell cell502 = rowsBoletas.createCell(++col);
				cell502.setCellValue(new HSSFRichTextString("79781/54"));
				cell502.setCellStyle(styleAll);
				col =crearDatosBoletasConDDJJHelper( sheet, rowsBoletas,indexAux, fichaBoletaPortal,  col, styleAll ,  styleMoney,  borderTop);
		}else {
				logger.debug("No encontro categoria de boleta:  "  + fichaBoletaPortal.getDescripcion() );
				}
		}
		
		
		indexAux++;
		rowsBoletas = sheet.createRow(indexAux); 
		HSSFCell cell600 = rowsBoletas.createCell(0);
		cell600.setCellStyle(borderTop);
		HSSFCell cell601 = rowsBoletas.createCell(1);
		cell601.setCellStyle(borderTop);
		
		
		HSSFCell cell608 = rowsBoletas.createCell(12);
		cell608.setCellValue(totalBoletas.doubleValue());
		cell608.setCellStyle(styleMoney);
		
		index = indexAux;
		return index++;
	}
	
	private static int crearDatosBoletasSinDDJJ(HSSFSheet sheet,HSSFRow rowsBoletas,
			int index,List<FichaBoletaPortal> fichasSinDDJJ ,HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle verticalCenter,HSSFCellStyle styleMoney, HSSFCellStyle borderTop) {
				
		int indexAux = index;
		int col = 1;
		
		
		for (FichaBoletaPortal fichaBoleta : fichasSinDDJJ) {
			
			rowsBoletas = sheet.createRow(indexAux); 
			if("AMTIMA".equalsIgnoreCase(fichaBoleta.getEntidadBoleta()) &&
					fichaBoleta.getDescripcion().isEmpty()){
			
				col = 1;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString(fichaBoleta.getEntidadBoleta()));
				cell101.setCellStyle(styleAll);
				
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString(fichaBoleta.getDescripcion()));
				cell102.setCellStyle(styleAll);
				
				HSSFCell cell103 = rowsBoletas.createCell(++col);
				cell103.setCellValue(new HSSFRichTextString(fichaBoleta.getRazon_Soc()));
				cell103.setCellStyle(styleAll);
				
				HSSFCell cell104 = rowsBoletas.createCell(++col);
				cell104.setCellValue(new HSSFRichTextString("5652"));
				cell104.setCellStyle(styleAll);
				
				HSSFCell cell105 = rowsBoletas.createCell(++col);
				cell105.setCellValue(new HSSFRichTextString("59538/10"));
				cell105.setCellStyle(styleAll);
				//Boletas sin DDJJ
				HSSFCell cell015H = rowsBoletas.createCell(0);
				cell015H.setCellValue(new HSSFRichTextString("Boletas sin DDJJ"));
				cell015H.setCellStyle(verticalCenter);	
				
				crearDatosBoletasSinDDJJHelper( sheet, rowsBoletas,index, fichaBoleta,  col,  styleAll ,  styleMoney);
				indexAux++;
				rowsBoletas = sheet.createRow(indexAux); 
			}
			else if("OSPIM".equalsIgnoreCase(fichaBoleta.getEntidadBoleta()) &&
					fichaBoleta.getDescripcion().isEmpty()){
				col = 1;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString(fichaBoleta.getEntidadBoleta()));
				cell101.setCellStyle(styleAll);
				
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString(fichaBoleta.getDescripcion()));
				cell102.setCellStyle(styleAll);
				
				HSSFCell cell103 = rowsBoletas.createCell(++col);
				cell103.setCellValue(new HSSFRichTextString(fichaBoleta.getRazon_Soc()));
				cell103.setCellStyle(styleAll);
				
				HSSFCell cell104 = rowsBoletas.createCell(++col);
				cell104.setCellValue(new HSSFRichTextString("5782"));
				cell104.setCellStyle(styleAll);
				
				HSSFCell cell105 = rowsBoletas.createCell(++col);
				cell105.setCellValue(new HSSFRichTextString("79848/46"));
				cell105.setCellStyle(styleAll);
				
				crearDatosBoletasSinDDJJHelper( sheet, rowsBoletas,index, fichaBoleta,  col,  styleAll ,  styleMoney);
				indexAux++;
				rowsBoletas = sheet.createRow(indexAux); 
			}
			else if("UOMA".equalsIgnoreCase(fichaBoleta.getEntidadBoleta()) &&
					"UOMAAS".equalsIgnoreCase(fichaBoleta.getDescripcion())){
				col = 1;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString(fichaBoleta.getEntidadBoleta()));
				cell101.setCellStyle(styleAll);
				
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString(fichaBoleta.getDescripcion()));
				cell102.setCellStyle(styleAll);
				
				HSSFCell cell103 = rowsBoletas.createCell(++col);
				cell103.setCellValue(new HSSFRichTextString(fichaBoleta.getRazon_Soc()));
				cell103.setCellStyle(styleAll);
				
				HSSFCell cell104 = rowsBoletas.createCell(++col);
				cell104.setCellValue(new HSSFRichTextString("5784"));
				cell104.setCellStyle(styleAll);
				
				HSSFCell cell105 = rowsBoletas.createCell(++col);
				cell105.setCellValue(new HSSFRichTextString("79090/11"));
				cell105.setCellStyle(styleAll);
				crearDatosBoletasSinDDJJHelper( sheet, rowsBoletas,index, fichaBoleta,  col,  styleAll ,  styleMoney);	
				indexAux++;
				rowsBoletas = sheet.createRow(indexAux); 
			}
			else if("UOMA".equalsIgnoreCase(fichaBoleta.getEntidadBoleta()) &&
					"ART46".equalsIgnoreCase(fichaBoleta.getDescripcion())){
				col = 1;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString(fichaBoleta.getEntidadBoleta()));
				cell101.setCellStyle(styleAll);
				
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString(fichaBoleta.getDescripcion()));
				cell102.setCellStyle(styleAll);
				
				HSSFCell cell103 = rowsBoletas.createCell(++col);
				cell103.setCellValue(new HSSFRichTextString(fichaBoleta.getRazon_Soc()));
				cell103.setCellStyle(styleAll);
				
				HSSFCell cell104 = rowsBoletas.createCell(++col);
				cell104.setCellValue(new HSSFRichTextString("5784"));
				cell104.setCellStyle(styleAll);
				
				HSSFCell cell105 = rowsBoletas.createCell(++col);
				cell105.setCellValue(new HSSFRichTextString("79090/11"));
				cell105.setCellStyle(styleAll);
				crearDatosBoletasSinDDJJHelper( sheet, rowsBoletas,index, fichaBoleta,  col,  styleAll ,  styleMoney);	
				indexAux++;
				rowsBoletas = sheet.createRow(indexAux); 
			}
			else if("UOMA".equalsIgnoreCase(fichaBoleta.getEntidadBoleta()) &&
					"UOMACS".equalsIgnoreCase(fichaBoleta.getDescripcion())){
				col = 1;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString(fichaBoleta.getEntidadBoleta()));
				cell101.setCellStyle(styleAll);
				
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString(fichaBoleta.getDescripcion()));
				cell102.setCellStyle(styleAll);
				
				HSSFCell cell103 = rowsBoletas.createCell(++col);
				cell103.setCellValue(new HSSFRichTextString(fichaBoleta.getRazon_Soc()));
				cell103.setCellStyle(styleAll);
				
				HSSFCell cell104 = rowsBoletas.createCell(++col);
				cell104.setCellValue(new HSSFRichTextString("5784"));
				cell104.setCellStyle(styleAll);
				
				HSSFCell cell105 = rowsBoletas.createCell(++col);
				cell105.setCellValue(new HSSFRichTextString("79090/11"));
				cell105.setCellStyle(styleAll);
				crearDatosBoletasSinDDJJHelper( sheet, rowsBoletas,index, fichaBoleta,  col,  styleAll ,  styleMoney);	
				indexAux++;
				rowsBoletas = sheet.createRow(indexAux); 
			}
			else if("UOMA".equalsIgnoreCase(fichaBoleta.getEntidadBoleta()) &&
					"UOMACU".equalsIgnoreCase(fichaBoleta.getDescripcion())){
				col = 1;
				HSSFCell cell101 = rowsBoletas.createCell(++col);
				cell101.setCellValue(new HSSFRichTextString(fichaBoleta.getEntidadBoleta()));
				cell101.setCellStyle(styleAll);
				
				HSSFCell cell102 = rowsBoletas.createCell(++col);
				cell102.setCellValue(new HSSFRichTextString(fichaBoleta.getDescripcion()));
				cell102.setCellStyle(styleAll);
				
				HSSFCell cell103 = rowsBoletas.createCell(++col);
				cell103.setCellValue(new HSSFRichTextString(fichaBoleta.getRazon_Soc()));
				cell103.setCellStyle(styleAll);
				
				HSSFCell cell104 = rowsBoletas.createCell(++col);
				cell104.setCellValue(new HSSFRichTextString("5784"));
				cell104.setCellStyle(styleAll);
				
				HSSFCell cell105 = rowsBoletas.createCell(++col);
				cell105.setCellValue(new HSSFRichTextString("79090/11"));
				cell105.setCellStyle(styleAll);
				crearDatosBoletasSinDDJJHelper( sheet, rowsBoletas,index, fichaBoleta,  col,  styleAll ,  styleMoney);	
				indexAux++;
				rowsBoletas = sheet.createRow(indexAux); 
			}else {
				logger.debug("No encontro categoria de boleta:  "  + fichaBoleta.getDescripcion() );
			}
		}
		
	
		rowsBoletas = sheet.createRow(indexAux); 
		
		HSSFCell cell600 = rowsBoletas.createCell(0);
		cell600.setCellStyle(borderTop);
		HSSFCell cell601 = rowsBoletas.createCell(1);
		cell601.setCellStyle(borderTop);
		
		HSSFCell cell608 = rowsBoletas.createCell(12);
		cell608.setCellValue(totalBoletasSinDDJJ);
		cell608.setCellStyle(styleMoney);
		
		index = indexAux;
		return index;
	}

	private static int crearDatosBoletasSinDDJJHelper(HSSFSheet sheet,HSSFRow rowsBoletas,
			int index,FichaBoletaPortal fichaBoleta, int col, HSSFCellStyle styleAll , HSSFCellStyle styleMoney) {
		double total = 0;		
		HSSFCell cell106 = rowsBoletas.createCell(++col);
		cell106.setCellValue(fichaBoleta.getCantidad());
		cell106.setCellStyle(styleAll);
		
		HSSFCell cell107 = rowsBoletas.createCell(++col);
		cell107.setCellValue(fichaBoleta.getCapital().doubleValue());
		cell107.setCellStyle(styleMoney);
		
		HSSFCell cell108 = rowsBoletas.createCell(++col);
		cell108.setCellValue(fichaBoleta.getInteres().doubleValue());
		cell108.setCellStyle(styleMoney);
		
		HSSFCell cell109 = rowsBoletas.createCell(++col);
		cell109.setCellValue(fichaBoleta.getAjusteInteres().doubleValue());
		cell109.setCellStyle(styleMoney);
		
		HSSFCell cell110 = rowsBoletas.createCell(++col);
		cell110.setCellValue(fichaBoleta.getAjusteCapital().doubleValue());
		cell110.setCellStyle(styleMoney);
		
		total = fichaBoleta.getCapital().doubleValue() 
				+ fichaBoleta.getInteres().doubleValue() 
				+ fichaBoleta.getAjusteInteres().doubleValue() 
				+ fichaBoleta.getAjusteCapital().doubleValue();
		
		HSSFCell cell111 = rowsBoletas.createCell(++col);
		cell111.setCellValue(total);
		cell111.setCellStyle(styleMoney);
		totalBoletasSinDDJJ = totalBoletasSinDDJJ + total;
		return col;
		
	}
	
	private static int crearDatosBoletasConDDJJHelper(HSSFSheet sheet,HSSFRow rowsBoletas,
			int index,FichaBoletaPortal fichaBoletaPortal, int col, HSSFCellStyle styleAll , HSSFCellStyle styleMoney,  HSSFCellStyle styleNumber) {
				
		//Cantidad
		HSSFCell cell103 = rowsBoletas.createCell(++col);
		cell103.setCellValue(fichaBoletaPortal.getCantidad());
		cell103.setCellStyle(styleAll);
		//capital
		HSSFCell cell104 = rowsBoletas.createCell(++col);
		cell104.setCellValue(fichaBoletaPortal.getCapital().doubleValue());
		cell104.setCellStyle(styleMoney);
		//interes
		HSSFCell cell105 = rowsBoletas.createCell(++col);
		cell105.setCellValue(fichaBoletaPortal.getInteres().doubleValue());
		cell105.setCellStyle(styleMoney);
		//ajusteCapital
		HSSFCell cell106 = rowsBoletas.createCell(++col);
		cell106.setCellValue(fichaBoletaPortal.getAjusteCapital().doubleValue());
		cell106.setCellStyle(styleMoney);
		//ajusteinteres
		HSSFCell cell107 = rowsBoletas.createCell(++col);
		cell107.setCellValue(fichaBoletaPortal.getAjusteInteres().doubleValue());
		cell107.setCellStyle(styleMoney);
		//total Boleta
		HSSFCell cell108 = rowsBoletas.createCell(++col);
		cell108.setCellValue(fichaBoletaPortal.getImporte().doubleValue());
		cell108.setCellStyle(styleMoney);
		totalBoletas = totalBoletas.add(fichaBoletaPortal.getImporte());
		return col;
		
	}
}
