package ar.com.ospim.tesoreria.reportes;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
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

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteUltimosComprobantesCajaChicaExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteUltimosComprobantesCajaChicaExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) throws SystemException, SQLException {
		_log.debug("generando reporte");

		int idCajaChica = ParamUtil.getInteger(req, "id_caja_chica");
		int entidad = ParamUtil.getInteger(req, "entidad");
		CajaChica cajaChica=CajaChicaServiceUtil.get((int)idCajaChica,entidad );
		List<ComprobanteCajaChica>reporte =cajaChica.getComprobantesPendientesRendicion();
		Double saldo = cajaChica.getSaldo();
		for(ComprobanteCajaChica repo : reporte) {
			saldo+=repo.getImporte().doubleValue();
		}
		if(entidad == WebKeysGlobal.UOMA){
			saldo=0D;
		}
		return generarReporte(reporte,entidad,saldo,cajaChica);
		
	}

	private static HSSFWorkbook generarReporte(List<ComprobanteCajaChica> reporte,int entidad,Double saldoInicial,CajaChica cajaChica) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);

		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);

		HSSFCellStyle styleHeader = getStyleHeader(wb);

		HSSFCellStyle styleAllTop = getStyleAll(wb);

		HSSFCellStyle styleFechaLeft = getStyleDate(wb);

		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);

		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);

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
		ps.setLandscape(true);
		int i = 0;
		Double saldo=saldoInicial;
		
		i = createTitulosHeader(wb, sheet, i, entidad,cajaChica);

		
		if (entidad == WebKeysGlobal.OSPIM) {
			i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		} else {
			i = generarHeaderUoma(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		}

		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		String comprobante = new String();
		HSSFRow row = sheet.createRow(i);
		
		
		Map<String, Double> map=new HashMap<String, Double>();
		
		for (ComprobanteCajaChica repo : reporte) {
			if (entidad == WebKeysGlobal.OSPIM) {
				saldo -= repo.getImporteComprobante().doubleValue();
				
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop, saldo);
						
			} else {
                //saldo -= repo.getImporteComprobante().doubleValue();
				
				saldo += repo.getImporteComprobante().doubleValue();
				
				i = generarDatosExtendido(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop, saldo);
			}
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
		} else {
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.setColumnWidth(3, 10200);
			sheet.autoSizeColumn((short) 4);
			sheet.setColumnWidth(5, 10200);
			
			sheet.setColumnWidth(13, 5000);
			sheet.setColumnWidth(13, 5000);
			
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
		
		HSSFCell cell3 = row.createCell(6);
		cell3.setCellValue(repo.getImporte().doubleValue());
		cell3.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4 = row.createCell(7);
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
		
		HSSFCell cell7 = row.createCell(6);
		cell7.setCellValue(new HSSFRichTextString("Importe"));
		cell7.setCellStyle(styleHeaderL);

		HSSFCell cell8 = row.createCell(7);
		cell8.setCellValue(new HSSFRichTextString("Saldo"));
		cell8.setCellStyle(styleHeaderL);
		return ++i;
	}


	private static int generarHeaderUoma(HSSFSheet sheet, int i,
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
			
			
			HSSFCell cell10 = row.createCell(6);
			cell10.setCellValue(new HSSFRichTextString("Gravado"));
			cell10.setCellStyle(styleHeader);
			
			HSSFCell cell11 = row.createCell(7);
			cell11.setCellValue(new HSSFRichTextString("T.IVA"));
			cell11.setCellStyle(styleHeader);
			
			HSSFCell cell12 = row.createCell(8);
			cell12.setCellValue(new HSSFRichTextString("IVA"));
			cell12.setCellStyle(styleHeader);
			
			HSSFCell cell13 = row.createCell(9);
			cell13.setCellValue(new HSSFRichTextString("Percep.IVA"));
			cell13.setCellStyle(styleHeader);
			
			HSSFCell cell14 = row.createCell(10);
			cell14.setCellValue(new HSSFRichTextString("Percep.IIBB"));
			cell14.setCellStyle(styleHeader);

			HSSFCell cell15 = row.createCell(11);
			cell15.setCellValue(new HSSFRichTextString("Jurisdicción IIBB"));
			cell15.setCellStyle(styleHeader);
			
			HSSFCell cell16 = row.createCell(12);
			cell16.setCellValue(new HSSFRichTextString("Otros"));
			cell16.setCellStyle(styleHeader);
			
			HSSFCell cell7 = row.createCell(13);
			cell7.setCellValue(new HSSFRichTextString("Importe"));
			cell7.setCellStyle(styleHeaderL);

			HSSFCell cell8 = row.createCell(14);
			cell8.setCellValue(new HSSFRichTextString("Saldo"));
			cell8.setCellStyle(styleHeaderL);
			return ++i;
		}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, int entidad,CajaChica cajaChica) {

		String tituloReporte = "Reporte de Ultimos Comprobantes de Caja Chica";
		if(cajaChica !=null && cajaChica.getDescripcion()!=null) {
			tituloReporte+=" " + cajaChica.getDescripcion();
		}

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

		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 7));
		fila++;

		HSSFRow rowTitulo2 = sheet.createRow(fila);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Del  "
				+ DateUtils.format((new Date()), DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 7));
		fila++;

		return fila;
	}
	
	private static int generarDatosExtendido(HSSFSheet sheet, int i,
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
		
		
		HSSFCell cell10 = row.createCell(6);
		if(repo.getGravadoIVA()!=null) {
		   cell10.setCellValue(repo.getGravadoIVA().doubleValue());
		}else {
		   cell10.setCellValue(new HSSFRichTextString(""));	
		}
		cell10.setCellStyle(styleAllTop);
		
		HSSFCell cell11 = row.createCell(7);
		if(repo.getTasaIva()!=null) {
		   cell11.setCellValue(repo.getTasaIva()*100D);
		} else {
		   cell11.setCellValue(new HSSFRichTextString(""));		
		}
		cell11.setCellStyle(styleAllTop);
		
		HSSFCell cell12 = row.createCell(8);
		if(repo.getIva()!=null) {
			cell12.setCellValue(repo.getIva().doubleValue());
		} else {
			cell12.setCellValue(new HSSFRichTextString(""));		
		}
		cell12.setCellStyle(styleAllTop);
		
		HSSFCell cell13 = row.createCell(9);
		if(repo.getPercepcionIVA()!=null) {
			cell13.setCellValue(repo.getPercepcionIVA().doubleValue());
		} else {
			cell13.setCellValue(new HSSFRichTextString(""));
		}
		cell13.setCellStyle(styleAllTop);
		
		HSSFCell cell15 = row.createCell(10);
		if(repo.getPercepcionIIBB()!=null) {
		   cell15.setCellValue(repo.getPercepcionIIBB().doubleValue());
		}else {
		   cell15.setCellValue(new HSSFRichTextString(""));	
		}
		cell15.setCellStyle(styleAllTop);
		
		HSSFCell cell16 = row.createCell(11);
		if(repo.getJurisdiccionIIBB()!=null) {
		  cell16.setCellValue(repo.getJurisdiccionIIBB());
		}else {
		  cell16.setCellValue(new HSSFRichTextString(""));		
		}
		cell16.setCellStyle(styleAllTop);
		
		
		HSSFCell cell17 = row.createCell(12);
		if(repo.getOtrosTributos()!=null) {
		   cell17.setCellValue(repo.getOtrosTributos().doubleValue());
		}else {
		   cell17.setCellValue(new HSSFRichTextString(""));	
		}
		cell17.setCellStyle(styleAllTop);
		
		
		HSSFCell cell3 = row.createCell(13);
		cell3.setCellValue(repo.getImporte().doubleValue());
		cell3.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4 = row.createCell(14);
		cell4.setCellValue(saldo);
		cell4.setCellStyle(styleMoneyRight);
		
		return ++i;
	}

	
}
