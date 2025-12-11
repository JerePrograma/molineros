package ar.com.uoma.reportes;

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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteLibroIVAExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReporteLibroIVAExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		Integer entidad = ParamUtil.getInteger(renderRequest, "entidad");
		String fechaDesdeDia = ParamUtil.getString(renderRequest, "fechadesdedia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest, "fechadesdemes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechadesdeanio");
		String fechaHastaDia = ParamUtil.getString(renderRequest, "fechahastadia");
		String fechaHastaMes = ParamUtil.getString(renderRequest, "fechahastames");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(renderRequest, "fechahastaanio");
		String libro=ParamUtil.getString(renderRequest,"libro");
		String cuitEntidad="";
		
		
		Date fechaIni=new Date();
		Date fechaFin=new Date();
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
		} catch (Exception e) {
			_log.error("Error al generar reporte Libro de Iva", e);
			return null;
		}
		
		
		List<Comprobante> comprobantes=new ArrayList<Comprobante>();
		
		switch (entidad) {
		case WebKeysGlobal.UOMA:
			cuitEntidad=WebKeysGlobal.CUIT_UOMA;
			break;
		case WebKeysGlobal.OSPIM:
			cuitEntidad=WebKeysGlobal.CUIT_OSPIM;
			break;
		case WebKeysGlobal.AMTIMA:
			cuitEntidad=WebKeysGlobal.CUIT_AMTIMA;
			break;	
		}
		
		try {
		    comprobantes = ComprobanteServiceUtil.getLibroIVA(fechaIni, fechaFin, libro, entidad, null) ;
	    } catch (SystemException e) {}	
		
	    return generaReporteLibro(comprobantes,fechaIni,fechaFin,cuitEntidad,libro);
	}

	private static HSSFWorkbook generaReporteLibro(
			List<Comprobante> list,Date fechaIni,Date fechaFin,String cuitEntidad,String libro) {
	
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
	
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("IVA "+libro);

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
				
		StringBuffer titulo1=new StringBuffer("Libro IVA " + libro + " - CUIT "+ 
		 cuitEntidad.substring(0,2)+ "-"+ cuitEntidad.substring(2,10)+"-"+cuitEntidad.substring(10)
				+ "- Desde el "+sdf.format(fechaIni)+" hasta el "+sdf.format(fechaFin));
	
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
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Fecha"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Comprobante"));
		cell19H.setCellStyle(styleBold);
		
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("CUIT"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		if("COMPRAS".equalsIgnoreCase(libro)) {
		    cell20H.setCellValue(new HSSFRichTextString("Proveedor"));
		}else {
			cell20H.setCellValue(new HSSFRichTextString("Cliente"));	
		}
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Condición IVA"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Jurisdicción"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell31H = rowHeader.createCell(++col);
		cell31H.setCellValue(new HSSFRichTextString("Gravado 27%"));
		cell31H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Gravado 21%"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Gravado 10.5%"));
		cell23H.setCellStyle(styleBold);
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Exento"));
		cell24H.setCellStyle(styleBold);
		
		HSSFCell cell35H = rowHeader.createCell(++col);
		cell35H.setCellValue(new HSSFRichTextString("IVA 27%"));
		cell35H.setCellStyle(styleBold);
		
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("IVA 21%"));
		cell25H.setCellStyle(styleBold);
		
		HSSFCell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("IVA 10.5%"));
		cell26H.setCellStyle(styleBold);
		
		HSSFCell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("Retenciones"));
		cell27H.setCellStyle(styleBold);
		
		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Percepción IVA"));
		cell28H.setCellStyle(styleBold);
		
		HSSFCell cell29H = rowHeader.createCell(++col);
		cell29H.setCellValue(new HSSFRichTextString("Percepción IIBB"));
		cell29H.setCellStyle(styleBold);
		
		HSSFCell cell32H = rowHeader.createCell(++col);
		cell32H.setCellValue(new HSSFRichTextString("Otros Tributos"));
		cell32H.setCellStyle(styleBold);
		
		HSSFCell cell33H = rowHeader.createCell(++col);
		cell33H.setCellValue(new HSSFRichTextString("Reintegro IVA"));
		cell33H.setCellStyle(styleBold);
		
		HSSFCell cell30H = rowHeader.createCell(++col);
		cell30H.setCellValue(new HSSFRichTextString("Total"));
		cell30H.setCellStyle(styleBold);
		
		
		index++;
		
		for(Comprobante comprobantes: list){
			index=crearDatos(sheet, comprobantes, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		
		
		HSSFRow rowTotales = sheet.createRow(index);
		
		HSSFCell cell0 = rowTotales.createCell(2);
		cell0.setCellValue(new HSSFRichTextString("Totales"));
		cell0.setCellStyle(styleAll);
		
		int colT=6;
		HSSFCell cell1 = rowTotales.createCell(colT++);
		cell1.setCellFormula("SUM(G4:G"+ Integer.toString(index-1) +")");
		cell1.setCellStyle(styleMoney);
		
		HSSFCell cell2 = rowTotales.createCell(colT++);
		cell2.setCellFormula("SUM(H4:H"+ Integer.toString(index-1) +")");
		cell2.setCellStyle(styleMoney);
		
		
		HSSFCell cell3 = rowTotales.createCell(colT++);
		cell3.setCellFormula("SUM(I4:I"+ Integer.toString(index-1) +")");
		cell3.setCellStyle(styleMoney);
		
		HSSFCell cell4 = rowTotales.createCell(colT++);
		cell4.setCellFormula("SUM(J4:J"+ Integer.toString(index-1) +")");
		cell4.setCellStyle(styleMoney);
		
		
		HSSFCell cell5 = rowTotales.createCell(colT++);
		cell5.setCellFormula("SUM(K4:K"+ Integer.toString(index-1) +")");
		cell5.setCellStyle(styleMoney);
		
		HSSFCell cell6 = rowTotales.createCell(colT++);
		cell6.setCellFormula("SUM(L4:L"+ Integer.toString(index-1) +")");
		cell6.setCellStyle(styleMoney);
		
		HSSFCell cell7 = rowTotales.createCell(colT++);
		cell7.setCellFormula("SUM(M4:M"+ Integer.toString(index-1) +")");
		cell7.setCellStyle(styleMoney);
		
		
		HSSFCell cell8 = rowTotales.createCell(colT++);
		cell8.setCellFormula("SUM(N4:N"+ Integer.toString(index-1) +")");
		cell8.setCellStyle(styleMoney);
		
		
		HSSFCell cell9 = rowTotales.createCell(colT++);
		cell9.setCellFormula("SUM(O4:O"+ Integer.toString(index-1) +")");
		cell9.setCellStyle(styleMoney);
		
		HSSFCell cell10 = rowTotales.createCell(colT++);
		cell10.setCellFormula("SUM(P4:P"+ Integer.toString(index-1) +")");
		cell10.setCellStyle(styleMoney);
		
		HSSFCell cell11 = rowTotales.createCell(colT++);
		cell11.setCellFormula("SUM(Q4:Q"+ Integer.toString(index-1) +")");
		cell11.setCellStyle(styleMoney);
		
		HSSFCell cell12 = rowTotales.createCell(colT++);
		cell12.setCellFormula("SUM(R4:R"+ Integer.toString(index-1) +")");
		cell12.setCellStyle(styleMoney);
		
		HSSFCell cell13 = rowTotales.createCell(colT++);
		cell13.setCellFormula("SUM(S4:S"+ Integer.toString(index-1) +")");
		cell13.setCellStyle(styleMoney);
		
		sheet.createRow(++index);
		
		for (int i = 0; i < 43; i++) {
			sheet.autoSizeColumn((short) i);
		}
		
		return wb;
	}

	private static int crearDatos(HSSFSheet sheet,Comprobante cbte, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
		SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
		
		int col = -1;
		Double signo=1D;
		if("NCR".equalsIgnoreCase(cbte.getTipoComprobante()) || "NCE".equalsIgnoreCase(cbte.getTipoComprobante())) {
			signo=-1D;
		}
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(sdf.format( cbte.getFechaEmision())));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell003 = rowHeader.createCell(++col);
		cell003.setCellValue(new HSSFRichTextString(cbte.getLetraComprobante()+" "+
				String.format("%05d",cbte.getSucuComprobante())+
//		        pre.getSucuComprobante()+"-"+pre.getNroComprobante()));
				"-"+cbte.getNroComprobante()));
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
//		cell001.setCellValue(new HSSFRichTextString(pre.getAcreedorEmpresa().getCuit().substring(0,2)+ "-"+
//				pre.getAcreedorEmpresa().getCuit().substring(2,9) + pre.getAcreedorEmpresa().getCuit().substring(9)
//				));
		cell001.setCellValue(new HSSFRichTextString(cbte.getAcreedorEmpresa().getCuit()));
		
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(cbte.getAcreedorEmpresa().getRazon_soc()));
		cell020.setCellStyle(styleAll);
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		String condIva="";
		if("RI".equalsIgnoreCase( cbte.getAcreedorEmpresa().getImpIva()) ||
				"AC".equalsIgnoreCase( cbte.getAcreedorEmpresa().getImpIva())) {
			condIva="RINSCRIPTO";
		}else if("CS".equalsIgnoreCase( cbte.getAcreedorEmpresa().getImpIva())) {
			condIva="CFINAL";
		}else {
			condIva="EXENTO";
		}
		cell017.setCellValue(new HSSFRichTextString(condIva));
		cell017.setCellStyle(styleAll);
		
		HSSFCell cell004 = rowHeader.createCell(++col);
		cell004.setCellValue(new HSSFRichTextString(cbte.getAcreedorEmpresa().getDomicilioAfip() ));
		cell004.setCellStyle(styleAll);
		
		
		HSSFCell cell031= rowHeader.createCell(++col);
		cell031.setCellValue(cbte.getGravadoIVA27()==null?0:cbte.getGravadoIVA27().doubleValue()*signo );
		cell031.setCellStyle(styleMoney);
		
		
		HSSFCell cell022= rowHeader.createCell(++col);
		cell022.setCellValue(cbte.getGravadoIVA21()==null?0:cbte.getGravadoIVA21().doubleValue()*signo );
		cell022.setCellStyle(styleMoney);
		
		HSSFCell cell023= rowHeader.createCell(++col);
		cell023.setCellValue(cbte.getGravadoIVA105()==null?0:cbte.getGravadoIVA105().doubleValue()*signo );
		cell023.setCellStyle(styleMoney);
		
		HSSFCell cell024= rowHeader.createCell(++col);
		cell024.setCellValue(cbte.getExento()==null?0:cbte.getExento().doubleValue()*signo );
		cell024.setCellStyle(styleMoney);
		
		HSSFCell cell032= rowHeader.createCell(++col);
		cell032.setCellValue(cbte.getIva27()==null?0:cbte.getIva27().doubleValue()*signo);
		cell032.setCellStyle(styleMoney);
		
		
		HSSFCell cell025= rowHeader.createCell(++col);
		cell025.setCellValue(cbte.getIva21()==null?0:cbte.getIva21().doubleValue()*signo);
		cell025.setCellStyle(styleMoney);
		
		HSSFCell cell026= rowHeader.createCell(++col);
		cell026.setCellValue(cbte.getIva105()==null?0:cbte.getIva105().doubleValue()*signo);
		cell026.setCellStyle(styleMoney);
		
		HSSFCell cell027= rowHeader.createCell(++col);
		cell027.setCellValue(cbte.getRetenciones()==null?0:cbte.getRetenciones().doubleValue()*signo);
		cell027.setCellStyle(styleMoney);
		
		HSSFCell cell028= rowHeader.createCell(++col);
		cell028.setCellValue(cbte.getPercepcionIVA()==null?0:cbte.getPercepcionIVA().doubleValue()*signo);
		cell028.setCellStyle(styleMoney);
		
		HSSFCell cell029= rowHeader.createCell(++col);
		cell029.setCellValue(cbte.getPercepcionIIBB()==null?0:cbte.getPercepcionIIBB().doubleValue()*signo);
		cell029.setCellStyle(styleMoney);
		
		HSSFCell cell033= rowHeader.createCell(++col);
		cell033.setCellValue(cbte.getOtrosTributos()==null?0:cbte.getOtrosTributos().doubleValue()*signo);
		cell033.setCellStyle(styleMoney);
		
		HSSFCell cell034= rowHeader.createCell(++col);
		cell034.setCellValue(cbte.getReintegroIVA()==null?0:cbte.getReintegroIVA().doubleValue()*signo*-1D);
		cell034.setCellStyle(styleMoney);
		
		HSSFCell cell030= rowHeader.createCell(++col);
		cell030.setCellValue(cbte.getImporteComprobante()==null?0:cbte.getImporteComprobante().doubleValue()*signo);
		cell030.setCellStyle(styleMoney);
		
        rowHeader.setHeight((short) 0);
		return index++;
	}
}


