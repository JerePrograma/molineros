package ar.com.ospim.liquidaciones.reportes.action;

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

import ar.com.ospim.liquidaciones.beans.FichaFarmacia;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.BusquedaLiquidacionServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteLiquidacionesFarmaciaExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteLiquidacionesFarmaciaExcel.class);
	
	public static HSSFWorkbook generaFichaFarmacia(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
		
		String periodoDesdeDia = ParamUtil.getString(renderRequest,"periodoDesdeDia");
		String periodoDesdeMes = ParamUtil.getString(renderRequest,"periodoDesdeMes");
		String periodoDesdeAnio = ParamUtil.getString(renderRequest,"periodoDesdeAnio");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodo.parse(periodoDesdeDia + "/"
					+ (Integer.parseInt(periodoDesdeMes) + 1) + "/"
					+ periodoDesdeAnio);
		} catch (Exception e) {
			periodoDesde = null;
		}
		
		String periodoHastaDia = ParamUtil.getString(renderRequest,"periodoHastaDia");
		String periodoHastaMes = ParamUtil.getString(renderRequest,"periodoHastaMes");
		String periodoHastaAnio = ParamUtil.getString(renderRequest,"periodoHastaAnio");
		Date periodoHasta = null;
		try {
			periodoHasta = formatoDePeriodo.parse(periodoHastaDia + "/"
					+ (Integer.parseInt(periodoHastaMes) + 1) + "/"
					+ periodoHastaAnio);
		} catch (Exception e) {
			periodoHasta = null;
		}
		
		int opDesde = ParamUtil.getInteger(renderRequest, "opDesde", 0);
		int opHasta = ParamUtil.getInteger(renderRequest, "opHasta", 0);
		String cuil = ParamUtil.getString(renderRequest, "cuil",null);
		String inte = ParamUtil.getString(renderRequest, "inte",null);
		String id_farmacia = ParamUtil.getString(renderRequest, "id_farmacia",null);
		String farmacia = ParamUtil.getString(renderRequest, "farmacia",null);
		String troquel = ParamUtil.getString(renderRequest, "troquel",null);
		boolean pmi = ParamUtil.getBoolean(renderRequest, "pmi", false);

		List<FichaFarmacia> fichas = new ArrayList<FichaFarmacia>();
		try {
			
			fichas = BusquedaLiquidacionServiceUtil.getLiquidacionesFarmacia(periodoDesde,
					periodoHasta,troquel ,cuil ,inte!=null&&inte.trim().length()>0?Integer.parseInt(inte):null,
							id_farmacia ,farmacia ,opDesde,opHasta, pmi);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte de ficha de farmacia", e);
			return null;
		}
		
		return generarReporteFichaFarmacia(fichas, periodoDesde, periodoHasta, troquel, cuil, inte, id_farmacia, farmacia, opDesde, opHasta,  pmi);
	}
	
	private static HSSFWorkbook generarReporteFichaFarmacia(
			List<FichaFarmacia> list, Date periodoDesde, Date periodoHasta, String troquel, String cuil, String inte,
			String id_farmacia, String farmacia, int opDesde, int opHasta, Boolean pmi) {
		
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

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte de Liq. de Farmacia al ").append(sdf.format(hoy));
	
		if(periodoDesde!=null){
			titulo1.append(" - Periodo Desde: ").append(sdf.format(periodoDesde));
		}
		
		if(periodoHasta!=null){
			titulo1.append(" - Periodo Hasta: ").append(sdf.format(periodoHasta));
		}
		
		if(opDesde>0){
			titulo1.append(" - OP Desde: ").append(opDesde);
		}
		
		if(opHasta>0){
			titulo1.append(" - OP Hasta: ").append(opHasta);
		}
		
		if(null!=troquel && troquel.trim().length()>0){
			titulo1.append(" - Troquel: ").append(troquel);
		}
		
		if(null!=farmacia && farmacia.trim().length()>0){
			titulo1.append(" - Farmacia: ").append(farmacia);
		}
		
		if(null!=cuil && cuil.trim().length()>0){
			titulo1.append(" - CUIL Titular: ").append(troquel);
		}
		
		if(null!=inte && inte.trim().length()>0){
			titulo1.append(" - Inte: ").append(troquel);
		}
		
		if(pmi==false){
			titulo1.append(" - PMI: ").append("No");
		}else { 
			titulo1.append(" - PMI: ").append("Si");
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index=index+2;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Fecha Receta"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Periodo"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Nro Pestador"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Nombre Prestador "));
		cell4H.setCellStyle(styleBold);
	
		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Cod Farmacia"));
		cell5H.setCellStyle(styleBold);

		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Nombre Farmacia"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Nro Receta"));
		cell7H.setCellStyle(styleBold);

		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Nro Troquel "));
		cell8H.setCellStyle(styleBold);

		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Nombre Medicamento"));
		cell9H.setCellStyle(styleBold);

		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell10H.setCellStyle(styleBold);

		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("PVP"));
		cell11H.setCellStyle(styleBold);

		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Total Ospim "));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Total Amtima"));
		cell13H.setCellStyle(styleBold);

		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Debito"));
		cell14H.setCellStyle(styleBold);

		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Dif Ospim"));
		cell15H.setCellStyle(styleBold);

		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Dif Amtima "));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Porcentaje Ospim"));
		cell17H.setCellStyle(styleBold);

		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Porcentaje Amtima"));
		cell18H.setCellStyle(styleBold);

		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("PMI"));
		cell19H.setCellStyle(styleBold);

		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Id Ospim "));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Id Amtima"));
		cell21H.setCellStyle(styleBold);

		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell22H.setCellStyle(styleBold);

		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Inte"));
		cell23H.setCellStyle(styleBold);

		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Nombre y Apellido "));
		cell24H.setCellStyle(styleBold);
		
		HSSFCell cell34H = rowHeader.createCell(++col);
		cell34H.setCellValue(new HSSFRichTextString("Plan"));
		cell34H.setCellStyle(styleBold);		
		
		HSSFCell cell35H = rowHeader.createCell(++col);
		cell35H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell35H.setCellStyle(styleBold);
		
		
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("Fecha Proceso "));
		cell25H.setCellStyle(styleBold);

		HSSFCell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("Id Orden de Pago"));
		cell26H.setCellStyle(styleBold);

		HSSFCell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("Fecha OP"));
		cell27H.setCellStyle(styleBold);

		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Importe OP "));
		cell28H.setCellStyle(styleBold);
		
		HSSFCell cell29H = rowHeader.createCell(++col);
		cell29H.setCellValue(new HSSFRichTextString("Descuento"));
		cell29H.setCellStyle(styleBold);

		HSSFCell cell30H = rowHeader.createCell(++col);
		cell30H.setCellValue(new HSSFRichTextString("Descuento por Drogueria "));
		cell30H.setCellStyle(styleBold);

		HSSFCell cell31H = rowHeader.createCell(++col);
		cell31H.setCellValue(new HSSFRichTextString("Nro Cheque "));
		cell31H.setCellStyle(styleBold);

		HSSFCell cell32H = rowHeader.createCell(++col);
		cell32H.setCellValue(new HSSFRichTextString("Importe Cheque "));
		cell32H.setCellStyle(styleBold);
		
		HSSFCell cell33HA = rowHeader.createCell(++col);
		cell33HA.setCellValue(new HSSFRichTextString("Anticipos"));
		cell33HA.setCellStyle(styleBold);		
		
		HSSFCell cell33H = rowHeader.createCell(++col);
		cell33H.setCellValue(new HSSFRichTextString("Liquida"));
		cell33H.setCellStyle(styleBold);
		
		HSSFCell cell36H = rowHeader.createCell(++col);
		cell36H.setCellValue(new HSSFRichTextString("Seccional"));
		cell36H.setCellStyle(styleBold);
		
		index++;
			
		
		
		for(FichaFarmacia fichaFarmacia: list){
			index=crearDatosFicha(sheet, fichaFarmacia, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber );
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
		sheet.autoSizeColumn((short) 30);
		sheet.autoSizeColumn((short) 31);
		sheet.autoSizeColumn((short) 32);
		sheet.autoSizeColumn((short) 33);
		sheet.autoSizeColumn((short) 34);
		sheet.autoSizeColumn((short) 35);

		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,FichaFarmacia fichaFarmacia, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(fichaFarmacia.getFechaReceta()
				.toString()));
		cell0.setCellStyle(styleDate);

		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(fichaFarmacia.getPeriodo()
				.toString()));
		cell1.setCellStyle(styleDate);
		
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(fichaFarmacia.getNroPrestador()));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(fichaFarmacia.getNombrePrestador()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(++col); 
		cell4.setCellValue(fichaFarmacia.getCodFarmacia());
		cell4.setCellStyle(styleNumber);
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(fichaFarmacia.getFarmacia()));
		cell5.setCellStyle(styleAll);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(fichaFarmacia.getReceta()));
		cell6.setCellStyle(styleNumber);

		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(fichaFarmacia.getNro_troquel()));
		cell7.setCellStyle(styleNumber);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(fichaFarmacia.getMedicamento()));
		cell8.setCellStyle(styleAll);
		
		HSSFCell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(fichaFarmacia.getCantidad());
		cell9.setCellStyle(styleNumber);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(fichaFarmacia.getPvp().doubleValue());
		cell10.setCellStyle(styleAll);
		
		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(fichaFarmacia.getTotalOspim().doubleValue());
		cell11.setCellStyle(styleAll);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(fichaFarmacia.getTotalAmtima().doubleValue());
		cell12.setCellStyle(styleAll);
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(fichaFarmacia.getDebito()));
		cell13.setCellStyle(styleNumber);
		
		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(fichaFarmacia.getDif_ospim().doubleValue());
		cell14.setCellStyle(styleNumber);
		
		HSSFCell cell15 = rowHeader.createCell(++col);
		cell15.setCellValue(fichaFarmacia.getDif_amtima().doubleValue());
		cell15.setCellStyle(styleNumber);
		
		HSSFCell cell16 = rowHeader.createCell(++col);
		cell16.setCellValue(fichaFarmacia.getPorcentaje_ospim());
		cell16.setCellStyle(styleNumber);
		
		HSSFCell cell17 = rowHeader.createCell(++col);
		cell17.setCellValue(fichaFarmacia.getPorcentaje_amtima());
		cell17.setCellStyle(styleNumber);
		
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(fichaFarmacia.getPmi()));
		cell18.setCellStyle(styleAll);

		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(fichaFarmacia.getId_ospim());
		cell19.setCellStyle(styleNumber);
		
		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(fichaFarmacia.getId_amtima());
		cell20.setCellStyle(styleNumber);
		
		HSSFCell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(new HSSFRichTextString(fichaFarmacia.getCuil_titular()));
		cell21.setCellStyle(styleNumber);
		
		HSSFCell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(fichaFarmacia.getInte());
		cell22.setCellStyle(styleNumber);
		
		HSSFCell cell23 = rowHeader.createCell(++col);
		cell23.setCellValue(new HSSFRichTextString(fichaFarmacia.getNombre_apellido()));
		cell23.setCellStyle(styleAll);
		
		HSSFCell cell23a = rowHeader.createCell(++col);
		cell23a.setCellValue(new HSSFRichTextString(fichaFarmacia.getPlan()));
		cell23a.setCellStyle(styleAll);
		
		
		HSSFCell cell23b = rowHeader.createCell(++col);
		cell23b.setCellValue(new HSSFRichTextString(fichaFarmacia.getDiscapacitado()));
		cell23b.setCellStyle(styleAll);
		
		HSSFCell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString(fichaFarmacia.getFecha_proceso()
				.toString()));
		cell24.setCellStyle(styleDate);
		
		HSSFCell cell25 = rowHeader.createCell(++col);
		cell25.setCellValue(fichaFarmacia.getId_orden_pago());
		cell25.setCellStyle(styleNumber);

		HSSFCell cell26 = rowHeader.createCell(++col);
		cell26.setCellValue(new HSSFRichTextString(fichaFarmacia.getFecha_op()
				.toString()));
		cell26.setCellStyle(styleDate);
		
		HSSFCell cell27 = rowHeader.createCell(++col);
		cell27.setCellValue(fichaFarmacia.getImporte_op()!=null?fichaFarmacia.getImporte_op().doubleValue():0);
		cell27.setCellStyle(styleMoney);
		
		HSSFCell cell28 = rowHeader.createCell(++col);
		cell28.setCellValue(fichaFarmacia.getDescuento()!=null?fichaFarmacia.getDescuento().doubleValue():0);
		cell28.setCellStyle(styleNumber);
		
		HSSFCell cell29 = rowHeader.createCell(++col);
		cell29.setCellValue(fichaFarmacia.getDescuento_por_drogueria()!=null?fichaFarmacia.getDescuento_por_drogueria().doubleValue():0);
		cell29.setCellStyle(styleAll);
		
		HSSFCell cell30 = rowHeader.createCell(++col);
		cell30.setCellValue(new HSSFRichTextString(fichaFarmacia.getNro_cheque()!=null?fichaFarmacia.getNro_cheque().toString():""));
		cell30.setCellStyle(styleNumber);
		
		HSSFCell cell31 = rowHeader.createCell(++col);
		cell31.setCellValue(fichaFarmacia.getImporte_cheque()!=null?fichaFarmacia.getImporte_cheque().doubleValue():0);
		cell31.setCellStyle(styleMoney);
		
		HSSFCell cell31B = rowHeader.createCell(++col);
		cell31B.setCellValue(new HSSFRichTextString(fichaFarmacia.getAnticipos()));
		cell31B.setCellStyle(styleAll);
		
		HSSFCell cell32 = rowHeader.createCell(++col);
		cell32.setCellValue(new HSSFRichTextString(fichaFarmacia.getLiquida()
				.toString()));
		cell32.setCellStyle(styleAll);
		
		HSSFCell cell33 = rowHeader.createCell(++col);
		cell33.setCellValue(new HSSFRichTextString(fichaFarmacia.getSeccional()));
		cell33.setCellStyle(styleAll);
		
		return index++;
	
	}	
}