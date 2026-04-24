package ar.com.ospim.tesoreria.reportes;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboPrestamo;
import ar.com.ospim.util.DateUtils;

public class ReportePrestamosTurismoExcel extends
         ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePrestamosTurismoExcel.class);

	public static HSSFWorkbook generaReportePrestamosTurismo(HttpServletRequest req,
			HttpServletResponse res, boolean soloReporteConsolidado) {

		HttpSession session = (HttpSession) req.getSession();
		
		String cuil=ParamUtil.getString(req,"cuil",null);
		String inteParam =  ParamUtil.getString(req, "inte",null);
		String seccionalP =  ParamUtil.getString(req, "seccional",null);
		
		Integer inte = null;
		try {
			inte = Integer.parseInt(inteParam);
		} catch (Exception e) {}
		
		Integer seccional=null;
		try {
			seccional = Integer.parseInt(seccionalP);
		} catch (Exception e) {}

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
				
		String fechaDesdeDia = ParamUtil.getString(req,"fechadesdedia");
		String fechaDesdeMes = ParamUtil.getString(req,"fechadesdemes");
		String fechaDesdeAnio = ParamUtil.getString(req,"fechadesdeanio");		
		String fechaHastaDia = ParamUtil.getString(req,"fechahastadia");
		String fechaHastaMes = ParamUtil.getString(req,"fechahastames");
		String fechaHastaAnio = ParamUtil.getString(req,"fechahastaanio");
		
		String fechaDeudaDia = ParamUtil.getString(req,"fechadeudadia");
		String fechaDeudaMes = ParamUtil.getString(req,"fechadeudames");
		String fechaDeudaAnio = ParamUtil.getString(req,"fechadeudaanio");	

		boolean soloConSaldo = ParamUtil.getBoolean(req, "soloConSaldo");

		int entidad = ParamUtil.getInteger(req, "entidad");

		String tipoReporte = ParamUtil.getString(req, "tipoReporte");

		Long id = ParamUtil.getLong(req, "id",0);
		String hotel = ParamUtil.getString(req,"hotel");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaH = null;
		}
		
		String fechaCuotaDia = ParamUtil.getString(req,"fechadesdecuotadia");
		String fechaCuotaMes = ParamUtil.getString(req,"fechadesdecuotames");
		String fechaCuotaAnio = ParamUtil.getString(req,"fechadesdecuotaanio");
		
		String fechaCuotaDiaH = ParamUtil.getString(req,"fechahastacuotadia");
		String fechaCuotaMesH = ParamUtil.getString(req,"fechahastacuotames");
		String fechaCuotaAnioH = ParamUtil.getString(req,"fechahastacuotaanio");
		
		Date fechaCuotaD = null;
		try {
			fechaCuotaD= formatoDeFechas.parse(fechaCuotaDia + "/"
					+ (Integer.parseInt(fechaCuotaMes) + 1) + "/"
					+ fechaCuotaAnio);
		} catch (Exception e) {
			fechaCuotaD = null;
		}
		
		Date fechaCuotaH = null;
		try {
			fechaCuotaH = formatoDeFechas.parse(fechaCuotaDiaH + "/"
					+ (Integer.parseInt(fechaCuotaMesH) + 1) + "/"
					+ fechaCuotaAnioH);
		} catch (Exception e) {
			fechaCuotaH = null;
		}

		
		Date fechaDeuda = null;
		try {
			fechaDeuda = formatoDeFechas.parse(fechaDeudaDia + "/"
					+ (Integer.parseInt(fechaDeudaMes) + 1) + "/"
					+ fechaDeudaAnio);
		} catch (Exception e) {
			fechaDeuda = null;
		}
		
		session.removeAttribute(WebKeysHoteles.PRESTAMO_FILTRO);
		session.removeAttribute(WebKeysHoteles.PRESTAMOS_RESULT);
			
        Prestamo filtro = new Prestamo();
        filtro.setId(id);
        
        if(hotel!=null) {
          filtro.setHotel(hotel);	
        }
        Afiliado afiliado = new Afiliado();
        if(cuil!=null) {
        	afiliado.setCuil_titular(cuil);
        	if(inte!=null) {
        		afiliado.setInte(inte);
        	}
        	
        	filtro.setAfiliado(afiliado);
        }
        
        if(seccional!=null) {
           Seccional secc =new Seccional();
           secc.setId_seccional(seccional);
           afiliado.setSeccional(secc);
        }
        
        if(fechaD!=null) {
        	filtro.setFechaConvenioDesde(fechaD);
        }
        
        if(fechaH!=null) {
        	filtro.setFechaConvenioHasta(fechaH);
        }
        
        if(fechaCuotaD!=null) {
        	filtro.setFechaCuotaDesde(fechaCuotaD);
        }
        
        if(fechaCuotaH!=null) {
        	filtro.setFechaCuotaHasta(fechaCuotaH);
        }
        
        if(fechaDeuda!=null) {
        	filtro.setDeudaExigibleAl(fechaDeuda);
        }
        
        String fechaCCDia = ParamUtil.getString(req,"fechaccdia");
		String fechaCCMes = ParamUtil.getString(req,"fechaccmes");
		String fechaCCAnio = ParamUtil.getString(req,"fechaccanio");
		
		Date fechaCCH = null;
		try {
			fechaCCH = formatoDeFechas.parse(fechaCCDia + "/"
					+ (Integer.parseInt(fechaCCMes) + 1) + "/"
					+ fechaCCAnio);
		} catch (Exception e) {
			fechaCCH = null;
		}
        
        filtro.setCorteCuentaCorriente(fechaCCH);
        
		try {
			
	        List<Prestamo> lista = HotelesServiceUtil.getListaPrestamos(filtro);
	        
			session.setAttribute(WebKeysHoteles.PRESTAMO_FILTRO,filtro);
			session.setAttribute(WebKeysHoteles.PRESTAMOS_RESULT,lista);

			return generarReportePreTur(fechaD, fechaH, lista, 
					soloConSaldo, entidad, soloReporteConsolidado,fechaDeuda,fechaCCH);
		} catch (Exception e) {
			_log.error("Error al generar reporte prestamos turismo", e);
			return null;
		}
		
	}
	
	protected static HSSFWorkbook generarReportePreTur(
			Date fechaIni, Date fechaFin,
			List<Prestamo> prestamos, 
			boolean soloConSaldo,
			int entidad, 
			boolean soloReporteConsolidado,
			Date fechaDeuda,
			Date fechaCCH) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDate = getStyleDate(wb);
		styleDate.setBorderLeft(BorderStyle.THIN);
		styleDate.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBorder = getStyleMoney(wb);
		styleMoneyBorder.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle stylePeriodo = getStyleDate(wb);
		stylePeriodo.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleBoldLeft = getStyleBold(wb);
		styleBoldLeft.setBorderLeft(BorderStyle.THIN);
		styleBoldLeft.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldCenter = getStyleBold(wb);
		styleBoldCenter.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldRight = getStyleBold(wb);
		styleBoldRight.setBorderTop(BorderStyle.THIN);
		styleBoldRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleAlignRight = getStyleBoldAligned(wb, HorizontalAlignment.RIGHT);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());

 		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
 		cellTitulo.setCellValue(new HSSFRichTextString(
				"Beneficios Turismo - Desde: "
 						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " Hasta: "
						+ DateUtils.format(fechaFin, DateUtils.SHORT))
 				        + "  - Corte Cuenta Corriente al: "	
		                + DateUtils.format(fechaCCH, DateUtils.SHORT)
 				);
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

		int i = 1;
		BigDecimal totalSaldo = BigDecimal.ZERO;
		BigDecimal totalDebe = BigDecimal.ZERO;
		BigDecimal totalHaber = BigDecimal.ZERO;
		BigDecimal totalFacturado = BigDecimal.ZERO;
		BigDecimal totalMovilidad = BigDecimal.ZERO;
		i = crearHeaderPpal(sheet, i, styleHeader, false,
				false, entidad, wb,fechaDeuda);
				 
		for (Prestamo pre : prestamos) {
			
			double importeDebe = 0;
			double importeHaber = 0;
			double importeSaldo = 0;
			importeDebe = ((pre.getTotal()!=null) ? pre.getTotal() : 0);
			importeHaber = ((pre.getPagado()!=null) ? pre.getPagado() : 0);
			importeSaldo = importeDebe - importeHaber;

//			if ((!soloConSaldo) || (importeSaldo != 0)) {
//			if ((!soloConSaldo) || (Math.abs(importeSaldo) > 10)) {//Pedido MC 30/07/2024
			if ((!soloConSaldo) || importeSaldo > 100) {//Pedido MC 13/09/2024	
/*							
				i = crearHeaderPrestamo(pre, sheet, i, styleBoldLeft,
						styleBoldCenter, styleBoldRight, false,
						false, entidad);
*/				
				ResultadoAuxiliar ra = crearDatosPrestamo(pre, sheet, i, styleDate,
						stylePeriodo, styleAll, styleMoney, styleMoneyBorder,
						styleAlignRight,
						false, soloConSaldo,
						false, false, entidad, 
						soloReporteConsolidado);
				i = ra.getI();
				totalSaldo = totalSaldo.add(ra.getTotalSaldo());
				totalDebe = totalDebe.add(ra.getTotalDebe());
				totalHaber = totalHaber.add(ra.getTotalHaber());
				totalFacturado=totalFacturado.add(pre.getFactura().getImporteTotalCalculado());
				totalMovilidad=totalMovilidad.add(new BigDecimal(pre.getMovilidad()));
			}			
		}
		
		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleBoldCenter);
		int cant = 4;
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, cant));

		HSSFCellStyle styleOnlyBoldCenter = getStyleBold(wb);
		
		HSSFRow rowTotal = sheet.createRow(i + 1);
		HSSFCell cellFacturadoTxt = rowTotal.createCell(5);
		cellFacturadoTxt.setCellValue(new HSSFRichTextString("Facturado"));
		cellFacturadoTxt.setCellStyle(styleOnlyBoldCenter);	
		
		HSSFCell cellMovilidadTxt = rowTotal.createCell(6);
		cellMovilidadTxt.setCellValue(new HSSFRichTextString("Movilidad"));
		cellMovilidadTxt.setCellStyle(styleOnlyBoldCenter);	
		
		
		HSSFCell cellTotalTxt = rowTotal.createCell(7);
		cellTotalTxt.setCellValue(new HSSFRichTextString("Otorgado"));
		cellTotalTxt.setCellStyle(styleOnlyBoldCenter);		
		
		cellTotalTxt = rowTotal.createCell(8);
		cellTotalTxt.setCellValue(new HSSFRichTextString("Pagado"));
		cellTotalTxt.setCellStyle(styleOnlyBoldCenter);

		cellTotalTxt = rowTotal.createCell(9);
		cellTotalTxt.setCellValue(new HSSFRichTextString("Saldo"));
		cellTotalTxt.setCellStyle(styleOnlyBoldCenter);
		
		String totalStr = "Total General";

		rowTotal = sheet.createRow(i + 2);
		cellTotalTxt = rowTotal.createCell(3);
		cellTotalTxt.setCellValue(new HSSFRichTextString(totalStr));
		cellTotalTxt.setCellStyle(styleBoldCenter);

		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		
		HSSFCell cellTotalFacturado = rowTotal.createCell(5);
		cellTotalFacturado.setCellValue(totalFacturado.doubleValue());
		cellTotalFacturado.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalMovilidad = rowTotal.createCell(6);
		cellTotalMovilidad.setCellValue(totalMovilidad.doubleValue());
		cellTotalMovilidad.setCellStyle(styleMoneyBold);
		
		HSSFCell cellTotal = rowTotal.createCell(7);
		cellTotal.setCellValue(totalDebe.doubleValue());
		
		cellTotal.setCellStyle(styleMoneyBold);
		
		cellTotal = rowTotal.createCell(8);
		cellTotal.setCellValue(totalHaber.doubleValue());
		cellTotal.setCellStyle(styleMoneyBold);

		cellTotal = rowTotal.createCell(9);
		cellTotal.setCellValue(totalSaldo.doubleValue());
		cellTotal.setCellStyle(styleMoneyBold);

		for(int j=0;j<30;j++){
		     sheet.autoSizeColumn((short) j);
		}

		return wb;
	}

	private static int crearHeaderPpal(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, boolean mostrarPeriodo,
			boolean mostrarMasInfo, int entidad,  HSSFWorkbook wb,Date fechaDeuda) {
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Numero"));
		cell.setCellStyle(styleHeader);
		
		HSSFCell cell0 = row.createCell(1);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleHeader);

		int indexBase = 2;
		if (mostrarPeriodo) {
			HSSFCell cell1 = row.createCell(2);
			cell1.setCellValue(new HSSFRichTextString("Periodo"));
			cell1.setCellStyle(styleHeader);
			indexBase++;
		}

		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString("Afiliado"));
		cell2.setCellStyle(styleHeader);
		indexBase++;

		HSSFCell cell3 = row.createCell(indexBase);
		cell3.setCellValue(new HSSFRichTextString("Seccional"));
		cell3.setCellStyle(styleHeader);
		indexBase++;

		HSSFCell cell4 = row.createCell(indexBase);
		cell4.setCellValue(new HSSFRichTextString("Hotel"));
		cell4.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell5 = row.createCell(indexBase);
		cell5.setCellValue(new HSSFRichTextString("Cuotas"));
		cell5.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell51 = row.createCell(indexBase);
		cell51.setCellValue(new HSSFRichTextString("Facturado"));
		cell51.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell52 = row.createCell(indexBase);
		cell52.setCellValue(new HSSFRichTextString("Movilidad"));
		cell52.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell6 = row.createCell(indexBase);
		cell6.setCellValue(new HSSFRichTextString("Otorgado"));
		cell6.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell7 = row.createCell(indexBase);
		cell7.setCellValue(new HSSFRichTextString("Pagado"));
		cell7.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell8 = row.createCell(indexBase);
		cell8.setCellValue(new HSSFRichTextString("Saldo"));
		cell8.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell9 = row.createCell(indexBase);
		cell9.setCellValue(new HSSFRichTextString("Exigible al " + new SimpleDateFormat("dd/MM/yyyy").format(fechaDeuda) ));
		cell9.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell10 = row.createCell(indexBase);
		cell10.setCellValue(new HSSFRichTextString("Cuotas Adeudadas"));
		cell10.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell11 = row.createCell(indexBase);
		cell11.setCellValue(new HSSFRichTextString("Empleador"));
		cell11.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell12 = row.createCell(indexBase);
		cell12.setCellValue(new HSSFRichTextString("Baja AMTIMA"));
		cell12.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell13 = row.createCell(indexBase);
		cell13.setCellValue(new HSSFRichTextString("Factura"));
		cell13.setCellStyle(styleHeader);
		indexBase++;
		
		//wb.setRepeatingRowsAndColumns(0, 0, indexBase + index2 + 3, i, i);

		for(int j=0;j<40;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		
		i++;
		return i;
	}
	
	private static class ResultadoAuxiliar {
		private int i;
		private BigDecimal sumSaldo;
		private BigDecimal sumDebe;
		private BigDecimal sumHaber;
		private BigDecimal facturado;
		private BigDecimal movilidad;

		public void setTotalSaldo(BigDecimal total) {
			this.sumSaldo = total;
		}

		public BigDecimal getTotalSaldo() {
			return sumSaldo;
		}

		public void setTotalDebe(BigDecimal total) {
			this.sumDebe = total;
		}

		public BigDecimal getTotalDebe() {
			return sumDebe;
		}

		public void setTotalHaber(BigDecimal total) {
			this.sumHaber = total;
		}

		public BigDecimal getTotalHaber() {
			return sumHaber;
		}

		
		public void setI(int i) {
			this.i = i;
		}

		public int getI() {
			return i;
		}

		public BigDecimal getFacturado() {
			return facturado;
		}

		public void setFacturado(BigDecimal facturado) {
			this.facturado = facturado;
		}

		public BigDecimal getMovilidad() {
			return movilidad;
		}

		public void setMovilidad(BigDecimal movilidad) {
			this.movilidad = movilidad;
		}
		
		
	}
	
	private static ResultadoAuxiliar crearDatosPrestamo(Prestamo pre,
			HSSFSheet sheet, int i, HSSFCellStyle styleDate,
			HSSFCellStyle stylePeriodo, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney, HSSFCellStyle styleMoneyBorder,
			HSSFCellStyle styleAlignRight,
			boolean mostrarPeriodo,
			boolean soloConSaldo,
			boolean mostrarSoloComprobantesConSaldo, 
			boolean mostrarMasInfo, int entidad,
			boolean soloReporteConsolidado) {
		
		
		int indexBase = 2;	
		
		double importeDebe = 0;
		double importeHaber = 0;
		double importeSaldo = 0;
		double facturado= 0;
		double movilidad=0;
		String nroFactura="";
		
		try {
			nroFactura=pre.getFactura().getTipo() + " " + pre.getFactura().getLetra() + " " +pre.getFactura().getSucursal() + "-" +
					pre.getFactura().getNumero();
		}catch(Exception e) {
			
		}
		
		facturado = ((pre.getFactura().getImporteTotalCalculado()!=null) ? pre.getFactura().getImporteTotalCalculado().doubleValue() : 0);
		movilidad = ((pre.getMovilidad()!=null) ? pre.getMovilidad() : 0);
		
		importeDebe = ((pre.getTotal()!=null) ? pre.getTotal() : 0);
		importeHaber = ((pre.getPagado()!=null) ? pre.getPagado() : 0);
		importeSaldo = importeDebe - importeHaber;

		ResultadoAuxiliar ra = new ResultadoAuxiliar();
					
		HSSFRow row = sheet.createRow(i);
		
//------------------------------------
		
		HSSFCell cell = row.createCell(0);
		
		// Col numero y Fecha
		cell.setCellValue(new HSSFRichTextString((String.valueOf(pre.getId()))));
		cell.setCellStyle(styleAll);
		
		HSSFCell cell0 = row.createCell(1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		cell0.setCellValue(pre.getAcuerdoFecha()!=null ? sdf.format(pre.getAcuerdoFecha()) : "");
		cell0.setCellStyle(styleAll);

		// Col Apellido
		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString(
				(String.format("%-65s",pre.getAfiliado().getApellido()+" (" + pre.getAfiliado().getCuil_titular()+")"  ))));
		cell2.setCellStyle(styleAll);
		indexBase++;

		// Col Seccional
		HSSFCell cell3 = row.createCell(indexBase);
		cell3.setCellValue(new HSSFRichTextString(
				(String.format("%-50s",pre.getAfiliado().getSeccional()!=null && 
						pre.getAfiliado().getSeccional().getDescripcion()!=null?pre.getAfiliado().getSeccional().getDescripcion():
					""))));
		cell3.setCellStyle(styleAll);
		indexBase++;
		
		// Hotel
		HSSFCell cell4 = row.createCell(indexBase);
		cell4.setCellValue(new HSSFRichTextString((pre.getDescripcionHotel())));
		cell4.setCellStyle(styleAll);
		indexBase++;

		// Cuotas
		HSSFCell cell5 = row.createCell(indexBase);
		cell5.setCellValue(new HSSFRichTextString(String.valueOf(pre.getCantidadCuotas())));
		cell5.setCellStyle(styleAll);
		indexBase++;	

		
//------------------------------------		
		
		
		
//		indexBase += 4;
		HSSFCell cellFacturado = row.createCell(indexBase);
		cellFacturado.setCellValue(facturado);
		cellFacturado.setCellStyle(styleMoney);
		
		indexBase++;
		HSSFCell cellMovilidad = row.createCell(indexBase);
		cellMovilidad.setCellValue(movilidad);
		cellMovilidad.setCellStyle(styleMoney);
		
		indexBase++;
		HSSFCell cellDebe = row.createCell(indexBase);
		cellDebe.setCellValue(importeDebe);
		cellDebe.setCellStyle(styleMoney);

		indexBase++;
		HSSFCell cellHaber = row.createCell(indexBase);
		cellHaber.setCellValue(importeHaber);
		cellHaber.setCellStyle(styleMoney);

		indexBase++;
		HSSFCell cellSaldo = row.createCell(indexBase);
		cellSaldo.setCellValue(importeSaldo);
		cellSaldo.setCellStyle(styleMoneyBorder);
		
		indexBase++;
		HSSFCell cellExigible = row.createCell(indexBase);
		cellExigible.setCellValue(pre.getDeudaExigible());
		cellExigible.setCellStyle(styleMoneyBorder);
			
		indexBase++;
		HSSFCell cellAde = row.createCell(indexBase);
		cellAde.setCellValue(pre.getCuotasAdeudadas());
		cellAde.setCellStyle(styleMoneyBorder);
		
		indexBase++;
		HSSFCell cellEmple = row.createCell(indexBase);
		cellEmple.setCellValue(new HSSFRichTextString(((pre.getCuit()!=null?pre.getCuit():"") + " "+(pre.getRazonSocial()!=null?pre.getRazonSocial():"" ))));
		cellEmple.setCellStyle(styleAll);
				
		indexBase++;
		HSSFCell cellBaja = row.createCell(indexBase);
		cellBaja.setCellValue(pre.getBajaFechaAmtima()!=null ? sdf.format(pre.getBajaFechaAmtima()) : "");
		cellBaja.setCellStyle(styleAll);
		
		indexBase++;
		HSSFCell cellFAC = row.createCell(indexBase);
		cellFAC.setCellValue(new HSSFRichTextString(nroFactura));
		cellFAC.setCellStyle(styleAll);
		
		
		if (!soloReporteConsolidado) {
			
			Double sdo = ((pre.getTotal()!=null) ? pre.getTotal() : 0);
			List<Recibo> recibos = null;
			try {
				recibos =HotelesServiceUtil.getPrestamoPagos(pre.getId(),entidad,new Date());
				
				if(!recibos.isEmpty()) i++;
				
				for (int iRec = 0; iRec < recibos.size(); iRec++) {	    
					Recibo liq = (Recibo) recibos.get(iRec);					
					for(ReciboPrestamo rp : liq.getReciboPrestamos()){
			
						i++;
						HSSFRow rowDet = sheet.createRow(i);

						HSSFCell cellRecibo = rowDet.createCell(0);
						cellRecibo.setCellValue(new HSSFRichTextString("Recibo:"));
						cellRecibo.setCellStyle(styleAlignRight);						
						
						HSSFCell cellDetalle = rowDet.createCell(1);
						cellDetalle.setCellValue(sdf.format(rp.getPrestamo().getAcuerdoFecha()));

						cellDetalle = rowDet.createCell(2);
						cellDetalle.setCellValue(String.valueOf(liq.getNumero()));

						double importeRecibo = 0;
						importeRecibo = (rp.getPrestamo()!=null ? rp.getPrestamo().getMonto() : 0);
						HSSFCell cellMonto = rowDet.createCell(6);
						cellMonto.setCellValue(importeRecibo);
						cellMonto.setCellStyle(styleMoney);
						
						sdo -= importeRecibo;
						HSSFCell cellSdo = rowDet.createCell(7);
						cellSdo.setCellValue(sdo);
						cellSdo.setCellStyle(styleMoney);
					}
				}

			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
		i++;
		

		ra.setI(i);
		
		BigDecimal auxBD = new BigDecimal(importeSaldo);
		ra.setTotalSaldo(auxBD);
		auxBD = new BigDecimal(importeDebe);
		ra.setTotalDebe(auxBD);
		auxBD = new BigDecimal(importeHaber);
		ra.setTotalHaber(auxBD);
		return ra;
	}

}
