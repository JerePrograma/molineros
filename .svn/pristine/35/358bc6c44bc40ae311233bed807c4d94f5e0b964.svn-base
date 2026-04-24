package ar.com.uoma.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.uoma.WebKeysUOMA;

import ar.com.uoma.beans.CuentaCorrienteEmpresa;
import ar.com.uoma.cuentacorrienteempresa.services.CuentaCorrienteEmpresaServiceUtil;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.util.ParamUtil;
import com.opensymphony.oscache.util.StringUtil;

public class ReporteDeudaCuentaCorrienteExcel extends ReporteXLS {
	
	public static HSSFWorkbook generaReporteDeuda(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		String cuit=ParamUtil.getString(renderRequest,"cuit_entidad",null);
		String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
		
		String fechaDesdeMes=ParamUtil.getString(renderRequest,"desde_mes",null);
		String fechaDesdeAnio=ParamUtil.getString(renderRequest,"desde_anio",null);
		String fechaHastaMes=ParamUtil.getString(renderRequest,"hasta_mes",null);
		String fechaHastaAnio=ParamUtil.getString(renderRequest,"hasta_anio",null);
		boolean procesarConsulta=ParamUtil.getBoolean(renderRequest,"procesar_consulta",false);
		int tipoBoleta=ParamUtil.getInteger(renderRequest,"tipo_boleta");
		int qrySoloUoma=ParamUtil.getInteger(renderRequest,"solo_uoma");
		int qrySoloAmtima=ParamUtil.getInteger(renderRequest,"solo_amtima");
		int qryConsolidado=ParamUtil.getInteger(renderRequest,"consolidado");
		String periodo=ParamUtil.getString(renderRequest,"periodo",null);
		
		int vista = ParamUtil.getInteger(renderRequest, "vista");
		
		BigDecimal total = new BigDecimal(0);
		BigDecimal total_ddjj = new BigDecimal(0);
		BigDecimal total_boletas = new BigDecimal(0);
		BigDecimal total_actas = new BigDecimal(0);
		BigDecimal total_saldo_ini = new BigDecimal(0);
		BigDecimal aux = new BigDecimal(0);
		
		String query_tipocta = "";
		String sTotal_monto = "";  
		String sTotal_monto_ddjj = "";
		String sTotal_monto_boletas = "";
		String sTotal_monto_actas = "";
		String sTotal_saldo_ini = "";		
		
		int modo = 0;
		
		if (vista == 0) {
		  modo = qryConsolidado;	
		}	
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		Date fechaDesde= null;
		try {
			fechaDesde = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaDesdeMes) ) + 1)  + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}

		Date fechaHasta= null;
		try {
			fechaHasta = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaHastaMes) ) + 1)  + "/" + fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		List<CuentaCorrienteEmpresa>cuentacorriente= CuentaCorrienteEmpresaServiceUtil.getCuentaCorriente(
				cuit, sucursal, fechaDesde, fechaHasta, procesarConsulta, modo, 
				tipoBoleta, qrySoloUoma, qrySoloAmtima,
				vista, periodo, 0);
		for (int i = 0; i < cuentacorriente.size(); i++) {
			aux = new BigDecimal(cuentacorriente.get(i).getSaldo());
			total = total.add(aux);
			
			aux = new BigDecimal(cuentacorriente.get(i).getMontoDDJJ());
			total_ddjj = total_ddjj.add(aux);
			
			aux = new BigDecimal(cuentacorriente.get(i).getMontoBoletas());
			total_boletas = total_boletas.add(aux);

			aux = new BigDecimal(cuentacorriente.get(i).getMontoActas());
			total_actas = total_actas.add(aux);
			
			if (i==0) {
				aux = new BigDecimal(cuentacorriente.get(i).getSaldoAnt());
				total_saldo_ini = total_saldo_ini.add(aux);
			}		

		}
		
				
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("CuentaCorriente");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		
		HSSFCellStyle styleBoldTitulo = getStyleBold(wb);
		

		if (cuentacorriente == null || cuentacorriente.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1 = new StringBuffer("Reporte de Cuenta Corriente ").append(sdf.format(fechaDesde)).append(" al ").append(sdf.format(fechaHasta));
		
		if(null!=fechaDesde){
			titulo1.append(" - Período desde: ").append(sdf.format(fechaDesde.getTime()));
		}
		if(null!=fechaHasta){
			titulo1.append(" - Período hasta: ").append(sdf.format(fechaHasta.getTime()));
		}

		StringBuffer titulo2 = new StringBuffer("");
		if (vista > 0) {
			titulo2.append("CUIT: ").append(cuit);		
			if(!StringUtil.isEmpty(cuit)){
				titulo2.append(" - Razon Social: ").append(cuentacorriente.get(0).getRazSoc());		
			}
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		int index = 0;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBoldTitulo);
		
		index++;
		rowHeaderANT = sheet.createRow(index);
		
		HSSFCell cell0HB = rowHeaderANT.createCell(0);		
		cell0HB.setCellValue(new HSSFRichTextString(titulo2.toString()));
		cell0HB.setCellStyle(styleBoldTitulo);

		index++;
		rowHeaderANT = sheet.createRow(index);
		index++;
		rowHeaderANT = sheet.createRow(index);

		int iCol = 0;
		
		if (vista != 0) {
			HSSFCell cellTipoCta = rowHeaderANT.createCell(iCol);		
			cellTipoCta.setCellValue(new HSSFRichTextString("Tipo Cuenta"));
			cellTipoCta.setCellStyle(styleBoldTitulo);
			iCol++;			
		} 

		HSSFCell cellTit1 = rowHeaderANT.createCell(iCol);		
		cellTit1.setCellValue(new HSSFRichTextString("Saldo Inicial"));
		cellTit1.setCellStyle(styleBoldTitulo);
		iCol++;

		HSSFCell cellTit2 = rowHeaderANT.createCell(iCol);		
		cellTit2.setCellValue(new HSSFRichTextString("Tot.Declarado"));
		cellTit2.setCellStyle(styleBoldTitulo);
		iCol++;

		HSSFCell cellTit3 = rowHeaderANT.createCell(iCol);		
		cellTit3.setCellValue(new HSSFRichTextString("Ing.Boletas"));
		cellTit3.setCellStyle(styleBoldTitulo);
		iCol++;

		HSSFCell cellTit4 = rowHeaderANT.createCell(iCol);		
		cellTit4.setCellValue(new HSSFRichTextString("Tot.Actas Deudoras"));
		cellTit4.setCellStyle(styleBoldTitulo);
		iCol++;

		HSSFCell cellTit5 = rowHeaderANT.createCell(iCol);		
		cellTit5.setCellValue(new HSSFRichTextString("Tot.Actas Canceladas"));
		cellTit5.setCellStyle(styleBoldTitulo);
		iCol++;

		HSSFCell cellTit6 = rowHeaderANT.createCell(iCol);		
		cellTit6.setCellValue(new HSSFRichTextString("Saldo"));
		cellTit6.setCellStyle(styleBoldTitulo);
		iCol++;

		HSSFCell cellTit7 = rowHeaderANT.createCell(iCol);		
		cellTit7.setCellValue(new HSSFRichTextString("Otros Ingresos"));
		cellTit7.setCellStyle(styleBoldTitulo);
		iCol++;

		index++;
		rowHeaderANT = sheet.createRow(index);
		iCol = 0;
		
		HSSFCell _cell;
		
		if (vista != 0) {
			// Tipo de Cuenta
			query_tipocta = cuentacorriente.get(0).getCuentaNombre();
			_cell = rowHeaderANT.createCell(iCol);		
			_cell.setCellValue(new HSSFRichTextString(query_tipocta));
			_cell.setCellStyle(styleBoldTitulo);
			iCol++;			
		}

		// Saldo Inicial
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString(String.format("%,.2f", total_saldo_ini)));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;
		
		// Total Declarado
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString(String.format("%,.2f", total_ddjj)));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;

		// Ingreso Boletas
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString(String.format("%,.2f", total_boletas)));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;

		// Tot Actas Deudoras
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString("0.00"));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;

		// Tot Actas Canceladas
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString(String.format("%,.2f", total_actas)));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;

		// Saldo
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString(String.format("%,.2f", total)));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;

		// Otros
		_cell = rowHeaderANT.createCell(iCol);		
		_cell.setCellValue(new HSSFRichTextString("0.00"));
		_cell.setCellStyle(styleBoldTitulo);
		iCol++;

		index++;
		rowHeaderANT = sheet.createRow(index);
		index++;
		rowHeaderANT = sheet.createRow(index);

		index=crearHeaderCtaCte(sheet,wb,index, vista);
		index++;			
		for(CuentaCorrienteEmpresa ctacte: cuentacorriente){
			index=crearCtaCte(sheet, wb, ctacte, index, vista);						
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
		
		return wb;
	}
	
	private static int crearHeaderCtaCte(HSSFSheet sheet, HSSFWorkbook wb, int index, int vista ){
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
				
		int col = -1;
		
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
				
		HSSFCell cell0H = rowHeader.createCell(++col);
		if ((vista == 0) || (vista == 1)) {
			cell0H.setCellValue(new HSSFRichTextString("CUIT"));
			
		} else {
			cell0H.setCellValue(new HSSFRichTextString("Periodo"));		
		}
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(++col);
		if (vista == 0) {
			cell1H.setCellValue(new HSSFRichTextString("Razon Social"));
		} else if (vista == 1) {
			cell1H.setCellValue(new HSSFRichTextString("Tipo Cuenta"));
		} else {
			cell1H.setCellValue(new HSSFRichTextString("Nro.Boleta"));			
		}
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(++col);
		if ((vista == 0) || (vista == 1))  {
			cell2H.setCellValue(new HSSFRichTextString("Saldo Inicial"));
		} else {
			cell2H.setCellValue(new HSSFRichTextString("Nro.DJ"));
		}
		cell2H.setCellStyle(styleBold);
		
		if (vista != 1) {
			HSSFCell cell3H = rowHeader.createCell(++col);
			if (vista == 0) {
				cell3H.setCellValue(new HSSFRichTextString("Total Declarado"));
			} else if (vista == 2){
				cell3H.setCellValue(new HSSFRichTextString("Ent.Cobranza"));
			}
			cell3H.setCellStyle(styleBold);
		
			HSSFCell cell4H = rowHeader.createCell(++col);
			if (vista == 0) {
				cell4H.setCellValue(new HSSFRichTextString("Ingreso por Boletas"));
			} else {
				cell4H.setCellValue(new HSSFRichTextString("Fec.Cobranza"));
			}
			cell4H.setCellStyle(styleBold);
		}
		
		HSSFCell cell5H = rowHeader.createCell(++col);
		if (vista == 0) {
			cell5H.setCellValue(new HSSFRichTextString("Total por Actas"));
		} else {
			cell5H.setCellValue(new HSSFRichTextString("DDJJ"));
		}
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(++col);
		if (vista == 0) {
			cell6H.setCellValue(new HSSFRichTextString("Saldo"));
		} else {
			cell6H.setCellValue(new HSSFRichTextString("Boletas"));
		}
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell7H = rowHeader.createCell(++col);
		if (vista == 0) {
			cell7H.setCellValue(new HSSFRichTextString("Otros Ingresos"));
		} else {
			cell7H.setCellValue(new HSSFRichTextString("Actas"));
		}
		cell7H.setCellStyle(styleBold);
		
		if (vista == 0) {
			
		} else {
			HSSFCell cell8H = rowHeader.createCell(++col);
			cell8H.setCellValue(new HSSFRichTextString("Deuda"));
			cell8H.setCellStyle(styleBold);
			
			HSSFCell cell9H = rowHeader.createCell(++col);
			cell9H.setCellValue(new HSSFRichTextString("Pagos"));
			cell9H.setCellStyle(styleBold);
			
			HSSFCell cell10H = rowHeader.createCell(++col);
			cell10H.setCellValue(new HSSFRichTextString("Saldo"));
			cell10H.setCellStyle(styleBold);					
		}
			
		return index;
	}	

	private static int crearCtaCte(HSSFSheet sheet, HSSFWorkbook wb, CuentaCorrienteEmpresa ctacte, int index, int vista) {
		
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);		
		HSSFCellStyle styleNumber= getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cellData = rowHeader.createCell(++col);
		
		// v0: Cuit / else Periodo
		if ((vista == 0) || (vista == 1)) {
			cellData.setCellValue(new HSSFRichTextString(ctacte.getCuit()));
		} else {
			cellData.setCellValue(new HSSFRichTextString(ctacte.getPeriodo_yyyymm()));			
		}
		cellData.setCellStyle(styleAll);
		// v0: RazSoc / else Boleta
		cellData = rowHeader.createCell(++col);
		if (vista == 0) {
			cellData.setCellValue(new HSSFRichTextString(ctacte.getRazSoc()));
			cellData.setCellStyle(styleAll);
		} else if (vista == 1){
			cellData.setCellValue(new HSSFRichTextString(ctacte.getCuentaNombre()));
			cellData.setCellStyle(styleAll);
		} else {
			cellData.setCellValue(new HSSFRichTextString(ctacte.getNumeroSecuencia().toString()));
			cellData.setCellStyle(styleNumber);
		}
		
			
		String _max = "";
 		if ((ctacte.getDDJJ_Es_Max().equals("true")) && ctacte.getDDJJ_Seq() > 0)
 			_max = " (Rectificativa)";
 	
 		// v0: Saldo Inicial / else Nro DDJJ
 		cellData = rowHeader.createCell(++col);
 		if ((vista == 0) || (vista == 1)) {
 			cellData.setCellValue(new HSSFRichTextString("0.00"));
 		} else {
 			cellData.setCellValue(new HSSFRichTextString(ctacte.getDDJJ_Seq() + _max));
 		}
 		cellData.setCellStyle(styleAll);
 		
 		if (vista != 1) {
 	 		// v0: Total Declarado / else Ent Cob
 			cellData = rowHeader.createCell(++col);
 			if (vista == 0) { 
 				cellData.setCellValue(String.format("%,.2f", ctacte.getMontoDDJJ_BD()));
 			} else {
 				cellData.setCellValue(ctacte.getEntcob());			
 			}
 			cellData.setCellStyle(styleNumber);
 			
 			// v0 Monto Boletas / else Fecha Cobranza
 	 		cellData = rowHeader.createCell(++col);
 			if (vista == 0) {
 				cellData.setCellValue(String.format("%,.2f", ctacte.getMontoBoletas_BD()));
 		 		cellData.setCellStyle(styleNumber);
 			} else {
 				
 				StringBuilder sbFecCob = new StringBuilder();
 		 		if (ctacte.getFechaRecauda() != null) {
 		 			sbFecCob.append(ctacte.getFechaRecauda());
 		 		} else {
 		 			sbFecCob.append("");
 		 		}
 				
 		 		cellData.setCellValue(sbFecCob.toString());
 		 		cellData.setCellStyle(styleDate);			
 			} 		
 		}

		// v0: Total Actas / else Monto DDJJ
 		cellData = rowHeader.createCell(++col);
 		if (vista == 0) {
 			cellData.setCellValue(String.format("%,.2f", ctacte.getMontoActas_BD()));
 		} else {
 			cellData.setCellValue(String.format("%,.2f", ctacte.getMontoDDJJ_BD()));
 		}
 		cellData.setCellStyle(styleNumber);
 		
 		// v0: Saldo / else Monto Boletas 		
 		cellData = rowHeader.createCell(++col);
 		if (vista == 0) {
 			cellData.setCellValue(String.format("%,.2f", ctacte.getSaldo())); 	 			
 		} else {
 			cellData.setCellValue(String.format("%,.2f", ctacte.getMontoBoletas_BD()));	
 		}
 		cellData.setCellStyle(styleNumber);
 		
		// v0: Otros Ingresos / else Monto Actas   				
 		cellData = rowHeader.createCell(++col);
 		if (vista == 0) {
 			cellData.setCellValue(new HSSFRichTextString("0.00"));
 		} else {
 			cellData.setCellValue(String.format("%,.2f", ctacte.getMontoActas_BD()));
 		}
 		cellData.setCellStyle(styleNumber);
 		
 		if (vista == 0) {
 			
 		} else {
 	 		// Deuda
 	 		cellData = rowHeader.createCell(++col);
 	 		cellData.setCellValue(String.format("%,.2f", ctacte.getDebe()));
 	 		cellData.setCellStyle(styleNumber);
 	 		// Pagos
 	 		cellData = rowHeader.createCell(++col);
 	 		cellData.setCellValue(String.format("%,.2f", ctacte.getHaber()));
 	 		cellData.setCellStyle(styleNumber);
 	 		// Saldo
 	 		cellData = rowHeader.createCell(++col);
 	 		cellData.setCellValue(String.format("%,.2f", ctacte.getSaldo()));
 	 		cellData.setCellStyle(styleNumber); 			
 		}
		
		return index++;
	}	
}