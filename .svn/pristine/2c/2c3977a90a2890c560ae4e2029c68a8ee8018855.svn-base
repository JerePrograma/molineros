package ar.com.ospim.afiliados.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteEOAFExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteEOAFExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
    	  
    	  Calendar fechaIniCalendar=null;
     		
     		if(ParamUtil.getInteger(req, "fechaDesdeMes")>=0 && ParamUtil.getInteger(req, "fechaDesdeAnio")>0 ){
     			fechaIniCalendar=Calendar.getInstance();
     			fechaIniCalendar.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaDesdeAnio"));
     			fechaIniCalendar.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaDesdeMes"));
     			fechaIniCalendar.set(Calendar.DATE,1);
     		}
     		
     		Calendar fechaFinCalendar=null;
     		
    		fechaFinCalendar=Calendar.getInstance();
     		fechaFinCalendar.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaDesdeAnio"));
     		fechaFinCalendar.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaDesdeMes"));
     		fechaFinCalendar.set(Calendar.DATE, 1);
     		   
     		fechaFinCalendar.add(Calendar.MONTH, 1);
     		fechaFinCalendar.add(Calendar.DAY_OF_YEAR, -1); 
     		

    		Empresa empresa = new Empresa();
    		
     		Boolean incluirBcrios=true, incluirRecibos=true, incluirAfip=true;
     		int entidad =ParamUtil.getInteger(req, "entidad");
     		
     		
     		TreeMap<String, ItemSubdiarioIngreso> reporte = new TreeMap<String, ItemSubdiarioIngreso>();
     		
// Ingresos     		
     		
     		List<ItemSubdiarioIngreso> ingresos = ContabilidadServiceUtil
					.subdiarioIngresos(fechaIniCalendar.getTime(), fechaFinCalendar.getTime(), empresa,
							incluirBcrios, incluirRecibos, incluirAfip, false, entidad);
     		
//Egresos
     		List<ItemSubdiarioEgreso> egresosAux = new ArrayList<ItemSubdiarioEgreso>();
     		boolean incluirProveedores = true;
     		boolean incluirReintegros = true;
     		boolean incluirLiquidaciones= true;
     		boolean incluirMovBcrios=true;
     		
			List<? extends ItemSubdiarioEgreso> reporteEgr = null;
			if (entidad != WebKeysGlobal.OSPIM && incluirProveedores) {
				reporteEgr = OrdenPagoServiceUtil
						.reporteOrdenPagoCompletoParaSubdiario(fechaIniCalendar.getTime(),
								fechaFinCalendar.getTime(), true, false, incluirReintegros, false,
								entidad, 0);

			} else {
				reporteEgr = OrdenPagoServiceUtil
						.reporteOrdenPagoOspimCompletoParaSubdiario(fechaIniCalendar.getTime(),
								fechaFinCalendar.getTime(), incluirProveedores,
								incluirLiquidaciones, incluirReintegros);
			}
			egresosAux.addAll(reporteEgr);
			if (incluirMovBcrios) {
				List<? extends ItemSubdiarioEgreso> reporteParaSubdiario = MovimientoBancarioServiceUtil
						.reporteParaSubdiario(fechaIniCalendar.getTime(), fechaFinCalendar.getTime(), entidad);
				egresosAux.addAll(reporteParaSubdiario);
			}
	
			List<ItemSubdiarioIngreso> egresos = new ArrayList<ItemSubdiarioIngreso>();
			for (ItemSubdiarioEgreso repo : egresosAux) {
				for (SubdiarioEgresoColumna s:repo.getHacia()){
					ItemSubdiarioIngreso it = new ItemSubdiarioIngreso();
					it.setNumeroCuenta(s.getCuenta(entidad));
					it.setImporte(s.getImporte());
					egresos.add(it);
				}
				
				for (SubdiarioEgresoColumna s:repo.getDesde()){
					PlanCuentasSSS m = ContabilidadServiceUtil.getEquivalenciaPlanCuentaSSS(s.getCuenta(), entidad,"EOAF");
					if(m!=null){
					   ItemSubdiarioIngreso it = new ItemSubdiarioIngreso();
					   it.setNumeroCuenta(s.getCuenta(entidad));
					   it.setImporte(s.getImporte().multiply(new BigDecimal(-1)) );
					   egresos.add(it);
					}
				}
				
			}
     
			_log.debug("INICIANDO INGRESOS");
     		reporte = procesaDatos(reporte,ingresos,entidad,1);
			_log.debug("INICIANDO EGRESOS");
			reporte = procesaDatos(reporte,egresos,entidad,-1);
     		return generarReporte(reporte,fechaIniCalendar,fechaFinCalendar);
     		
		} catch (Exception e) {
			_log.error("Error al generar reporte EOAF", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(TreeMap<String, ItemSubdiarioIngreso> reporte,Calendar fechaDesde,Calendar fechaHasta) throws SystemException {
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
		
		HSSFCellStyle styleMoneyRightGris= getStyleMoneyFondoGris(wb);
		
		HSSFCellStyle styleAllFondoGris = getStyleAllFondoGris(wb);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(false);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i,fechaDesde,fechaHasta);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		i++;
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		for(ItemSubdiarioIngreso p : reporte.values()) {
			i = generarDatos(sheet, i, p, styleFechaLeft, styleAll,
					styleMoneyRight, styleFechaLeftTop, styleAllTop,
					styleMoneyRightTop);
			
			
		}
		
		i++;
		
		for(int x=0;x<7;x++){
			sheet.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ItemSubdiarioIngreso repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {
		
		if(repo.getNumeroCuenta().trim().length()==1) i++;
			
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0 = row.createCell(0);
		if(repo.getNumeroCuenta()   !=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getNumeroCuenta() ));
		  cell0.setCellStyle(styleAll);
	    }else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getCuenta()  !=null){
		  cell1.setCellValue(new HSSFRichTextString(repo.getCuenta()));
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		HSSFCell cell17 = row.createCell(2);
		if(repo.getImporte() !=null){
		  cell17.setCellValue(Math.abs(repo.getImporte().doubleValue()));
		  cell17.setCellStyle(styleMoneyRight);
	    }else{
		  cell17.setCellValue(new HSSFRichTextString(""));
		  cell17.setCellStyle(styleAll);
		}
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
				return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila,Calendar fechaDesde,Calendar fechaHasta) throws SystemException {
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		String tituloReporte = "ESTADO DE ORIGEN Y APLICACION DE FONDOS DESDE " + sdf.format(fechaDesde.getTime()) + " AL " + sdf.format(fechaHasta.getTime());
		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 4));
		
		fila++;
		rowTitulo = sheet.createRow(fila);
		HSSFCell cell12 = rowTitulo.createCell(0);
		cell12.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
//		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 5, 10));
		fila++;

		return fila;
	}
	
	
	private static TreeMap<String, ItemSubdiarioIngreso> procesaDatos(TreeMap<String, ItemSubdiarioIngreso>r,List<ItemSubdiarioIngreso> ingresos,int entidad,int signo) throws SystemException{
		for(ItemSubdiarioIngreso i:ingresos){
			r=procesaDatosCuenta(r,i,entidad,signo);
		}
		
		return r;
	};
	
	
	private static TreeMap<String, ItemSubdiarioIngreso>procesaDatosCuenta(TreeMap<String, ItemSubdiarioIngreso>r,ItemSubdiarioIngreso cuenta,int entidad,int signo) throws SystemException{
		PlanCuentasSSS m = ContabilidadServiceUtil.getEquivalenciaPlanCuentaSSS(cuenta.getNumeroCuenta(), entidad,"EOAF");
		if(m!=null && m.getNumero()!=null){
			ItemSubdiarioIngreso i= r.get(m.getNumero());
			if(i==null){
				i= new ItemSubdiarioIngreso();
				i.setImporte(BigDecimal.ZERO);
			}
			
			i.setNumeroCuenta(m.getNumero());
			i.setCuenta(m.getCuenta());
			BigDecimal importe = cuenta.getImporte().multiply(BigDecimal.valueOf(signo));
			i.setImporte(importe.add(i.getImporte()));
			
			r.put(m.getNumero(), i);
			
			if(m.getAcumulaSobre()!=null && !"".equalsIgnoreCase(m.getAcumulaSobre())){
			  ItemSubdiarioIngreso	ac= new ItemSubdiarioIngreso();
			  ac.setNumeroCuenta(m.getAcumulaSobre());
			  ac.setImporte(cuenta.getImporte());
			  r=procesaDatosCuenta(r,ac,entidad,signo);
			}
			
		}
		return r;
	}
}
