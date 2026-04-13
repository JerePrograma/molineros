package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteListadoValoresExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteLibroCajaExcel.class);

	@SuppressWarnings( { "unchecked", "deprecation" })
	public static HSSFWorkbook generaListadoValores(HttpServletRequest req,
			HttpServletResponse res) {

		List<CuentaBancaria> ctas = (ArrayList<CuentaBancaria>) req
				.getSession().getAttribute(WebKeysTesoreria.CUENTAS_BCRIAS);
		if (ctas == null) {
			ctas = TraeListasServiceUtil.getCtasBcrias();
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String fechaVtoDesde = ParamUtil.getString(req, "fechaVtoDesde");
		String fechaVtoHasta = ParamUtil.getString(req, "fechaVtoHasta");

		String fechaDepositoDesde = ParamUtil.getString(req,
				"fechaDepositoDesde");
		String fechaDepositoHasta = ParamUtil.getString(req,
				"fechaDepositoHasta");

		String fechaRechazoDesde = ParamUtil
				.getString(req, "fechaRechazoDesde");
		String fechaRechazoHasta = ParamUtil
				.getString(req, "fechaRechazoHasta");

		String fechaReemplazoDesde = ParamUtil.getString(req,
				"fechaReemplazoDesde");
		String fechaReemplazoHasta = ParamUtil.getString(req,
				"fechaReemplazoHasta");
		
		String fechaReciboDesde = ParamUtil
				.getString(req, "fechaReciboDesde");
		String fechaReciboHasta = ParamUtil
				.getString(req, "fechaReciboHasta");
		
		
		String fechaJudicialDesde = ParamUtil
				.getString(req, "fechaJudicialDesde");
		String fechaJudicialHasta = ParamUtil
				.getString(req, "fechaJudicialHasta");

		

		Integer depositados = ParamUtil.getInteger(req, "depositados");
		Integer rechazados = ParamUtil.getInteger(req, "rechazados");
		Integer reemplazados = ParamUtil.getInteger(req, "reemplazados");
		Integer judicializados = ParamUtil.getInteger(req, "judicializados");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		Integer idBanco = ParamUtil.getInteger(req, "id_banco");

		Integer ctaBcria = ParamUtil.getInteger(req, "id_cta_bcria");
		
		int entidad= ParamUtil.getInteger(req, "entidad");
		int nro_cheque= ParamUtil.getInteger(req, "nro_cheque");		
		boolean formato =false;
		try{
			formato= ParamUtil.getBoolean(req, "formato");
		}catch(Exception e){}	

		try {
			Date fechaVtoIni= null;
			Date fechaVtoFin= null;
			Date fechaDptoIni= null;
			Date fechaDptoFin= null;
			Date fechaRechIni= null;
			Date fechaRechFin= null;
			Date fechaReemIni=null;
			Date fechaReemFin=null;
			Date fechaReciIni=null;
			Date fechaReciFin=null;
			Date fechaJudiIni= null;
			Date fechaJudiFin= null;
			try{
				fechaVtoIni = format.parse(fechaVtoDesde);
				fechaVtoFin = format.parse(fechaVtoHasta);
	
				fechaDptoIni = format.parse(fechaDepositoDesde);
				fechaDptoFin = format.parse(fechaDepositoHasta);
	
				fechaRechIni = format.parse(fechaRechazoDesde);
				fechaRechFin = format.parse(fechaRechazoHasta);
	
				fechaReemIni = format.parse(fechaReemplazoDesde);
				fechaReemFin = format.parse(fechaReemplazoHasta);
			}catch(ParseException e){
				
			}
			try{	
				fechaReciIni = format.parse(fechaReciboDesde);
				
			}catch(ParseException e){
				fechaReciIni=null;
			
			}
			try{
				fechaReciFin = format.parse(fechaReciboHasta);
			}catch(ParseException e){			
				fechaReciFin=null;
			}
			
			try{
				fechaJudiIni = format.parse(fechaJudicialDesde);
				fechaJudiFin = format.parse(fechaJudicialHasta);
			}catch(ParseException e){
				
			}
			
			
			
			List<ReporteListadoValores> libro = ContabilidadServiceUtil
					.listadoValores(fechaVtoIni, fechaVtoFin, fechaDptoIni,
							fechaDptoFin, fechaRechIni, fechaRechFin,
							fechaReemIni, fechaReemFin, cuit, idBanco,
							depositados, reemplazados, rechazados, ctaBcria, entidad, nro_cheque,fechaReciIni, fechaReciFin,judicializados,
							fechaJudiIni,fechaJudiFin);

			return generarReporte(fechaVtoIni, fechaVtoFin, fechaDptoIni,
					fechaDptoFin, fechaRechIni, fechaRechFin, fechaReemIni,
					fechaReemFin, fechaReciIni, fechaReciFin, cuit, idBanco, depositados, reemplazados,
					rechazados, ctaBcria.intValue(), ctas, libro, entidad,formato,judicializados,fechaJudiIni, fechaJudiFin);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			Date fechaDptoIni, Date fechaDptoFin, Date fechaRechIni,
			Date fechaRechFin, Date fechaReemIni, Date fechaReemFin,
			Date fechaReciIni, Date fechaReciFin, String cuit, Integer idBanco, int depositados, int reemplazados,
			int rechazados, int ctaBcria, List<CuentaBancaria> ctas,
			List<ReporteListadoValores> libro, int entidad,boolean formato,int judicializados,Date fechaJudiIni, Date fechaJudiFin) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		HSSFCellStyle styleDateTop = getStyleDate(wb);
		styleDateTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeaderTop = getStyleHeaderWithBorder(wb);
		styleHeaderTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyTop = getStyleMoney(wb);
		styleMoneyTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleDateTopLeft = getStyleDate(wb);
		styleDateTopLeft.setBorderTop(BorderStyle.THIN);
		styleDateTopLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleDateLeft = getStyleDate(wb);
		styleDateLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleDateTopRight = getStyleDate(wb);
		styleDateTopRight.setBorderTop(BorderStyle.THIN);
		styleDateTopRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleDateRight = getStyleDate(wb);
		styleDateRight.setBorderRight(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		addDefaultHeader(sheet);
		
		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);

		StringBuffer sb = new StringBuffer("Listado Valores");
		if(null!=fechaReciIni){
			sb.append(" - Fecha Rbo. Desde: ").append(DateUtils.format(fechaReciIni));
		}
		if(null!=fechaReciFin){
			sb.append(" - Fecha Rbo. Hasta: ").append(DateUtils.format(fechaReciFin));
		}
		sb.append(" - Fecha Vto.Ch. Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Fecha Vto.Ch. Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		if (depositados == -1) {
			sb.append(" - Depositados: Todos ");
		} else if (depositados == 0) {
			sb.append(" - No Depositados ");
		} else {
			sb.append(" - Depositados desde: "
					+ DateUtils.format(fechaDptoIni, DateUtils.SHORT));
			sb.append("hasta : "
					+ DateUtils.format(fechaDptoFin, DateUtils.SHORT));
		}

		if (reemplazados == -1) {
			sb.append(" - Reemplazados: Todos ");
		} else if (reemplazados == 0) {
			sb.append(" - No Reemplazados ");
		} else {
			sb.append(" - Reemplazados desde: "
					+ DateUtils.format(fechaReemIni, DateUtils.SHORT));
			sb.append(" hasta : "
					+ DateUtils.format(fechaReemFin, DateUtils.SHORT));
		}

		if (rechazados == -1) {
			sb.append(" - Rechazados: Todos ");
		} else if (rechazados == 0) {
			sb.append(" - No Rechazados ");
		} else {
			sb.append(" - Rechazados desde: "
					+ DateUtils.format(fechaRechIni, DateUtils.SHORT));
			sb.append(" hasta : "
					+ DateUtils.format(fechaRechFin, DateUtils.SHORT));
		}
		
		
		if (judicializados == -1) {
			sb.append(" - Judicializados: Todos ");
		} else if (judicializados == 0) {
			sb.append(" - No Judicializados ");
		} else {
			sb.append(" - Judicializados desde: "
					+ DateUtils.format(fechaJudiIni, DateUtils.SHORT));
			sb.append(" hasta : "
					+ DateUtils.format(fechaJudiFin, DateUtils.SHORT));
		}
		
		
		

		if (ctaBcria == -1) {
			sb.append(" - Cta.Bcria: Todas");
		} else {
			for (CuentaBancaria cta : ctas) {
				if (cta.getId_cuenta_bcria() == ctaBcria) {
					sb.append(" - Cta.Bcria: " + cta.getDescripcion() + "/"
							+ cta.getCtaBcriaAsString());
				}
			}
		}

		if (null != cuit && !cuit.trim().equals("")) {
			sb.append(" - CUIT: " + cuit);
		}
		if (null != idBanco && -1 != idBanco.intValue()) {
			try {
				List<Banco> bancos = TraeListasServiceUtil.getBancos();
				for (Banco banco : bancos) {
					if (banco.getId_banco() == idBanco.intValue()) {
						sb.append(" - BANCO: " + banco.getDescripcion_banco());
					}
				}
			} catch (Exception e) {
				_log.error(e);
			}
		} else {
			sb.append(" - BANCO: Todos");
		}

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		cellTitulo.setCellStyle(styleHeader);
		if(entidad!=WebKeysGlobal.UOMA){
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));
		}else{
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
		}
		createHeader(wb, sheet, styleHeader, entidad);

		String recibo = "";
		Date fecha=null;
		BigDecimal totalCheques = null;
		int i = 2;
		int cabecera = 2;
		for (ReporteListadoValores l : libro) {
			HSSFRow row = sheet.createRow(i);
			boolean nuevo = false;
			if (!l.getNumero().equals(recibo) || !l.getFecha().equals(fecha) || formato) {
				nuevo = true;
				recibo = l.getNumero();
				fecha=l.getFecha();
				if (totalCheques != null) {
					sheet.getRow(cabecera).getCell(3).setCellValue(
							totalCheques.doubleValue());
				}
				totalCheques = BigDecimal.ZERO;
				cabecera = i;
			}
			int contCol=0;
			totalCheques = totalCheques.add(l.getImporteCheque());
			HSSFCell cellR = row.createCell(contCol++);
			if (nuevo) {
				cellR.setCellValue(l.getFechaRecibo());
				cellR.setCellStyle(styleDateTopLeft);
			} else {
				cellR.setCellStyle(styleDateLeft);
			}

			HSSFCell cell1 = row.createCell(contCol++);
			if (nuevo) {
				cell1.setCellValue(new HSSFRichTextString(l.getNumero()));
				cell1.setCellStyle(styleAllTop);
			} else {
				cell1.setCellStyle(styleAll);
			}

			HSSFCell cell2 = row.createCell(contCol++);
			if (nuevo) {
				cell2.setCellValue(l.getImporteTotal().doubleValue());
				cell2.setCellStyle(styleMoneyTop);
			} else {
				cell2.setCellStyle(styleMoney);
			}

			HSSFCell cell3 = row.createCell(contCol++);
			if (nuevo) {
				cell3.setCellValue(totalCheques.doubleValue());
				cell3.setCellStyle(styleMoneyTop);
			} else {
				cell3.setCellStyle(styleMoney);
			}
			
			HSSFCell cell = row.createCell(contCol++);
			if (nuevo) {
				cell.setCellValue(l.getFecha());
				cell.setCellStyle(styleDateTopLeft);
			} else {
				cell.setCellStyle(styleDateLeft);
			}
			

			HSSFCell cell4 = row.createCell(contCol++);
			if (nuevo) {
				cell4.setCellValue(new HSSFRichTextString(l.getCuit()));
				cell4.setCellStyle(styleAllTop);
			} else {
				cell4.setCellStyle(styleAll);
			}

			HSSFCell cell5 = row.createCell(contCol++);
			if (nuevo) {
				cell5.setCellValue(new HSSFRichTextString(l.getRazonSoc()));
				cell5.setCellStyle(styleAllTop);
			} else {
				cell5.setCellStyle(styleAll);
			}
			
			HSSFCell cell51 = row.createCell(contCol++);
			if (nuevo) {
				cell51.setCellValue(new HSSFRichTextString(l.getIdRamoEmpresa().toString()));
				cell51.setCellStyle(styleAllTop);
			} else {
				cell51.setCellStyle(styleAll);
			}

			HSSFCell cell6 = row.createCell(contCol++);
			cell6.setCellValue(l.getNroCheque().doubleValue());
			if (nuevo) {
				cell6.setCellStyle(styleAllTop);
			} else {
				cell6.setCellStyle(styleAll);
			}

			HSSFCell cell7 = row.createCell(contCol++);
			cell7.setCellValue(new HSSFRichTextString(l.getBanco()));
			if (nuevo) {
				cell7.setCellStyle(styleAllTop);
			} else {
				cell7.setCellStyle(styleAll);
			}

			HSSFCell cell8 = row.createCell(contCol++);
			cell8.setCellValue(l.getImporteCheque().doubleValue());
			if (nuevo) {
				cell8.setCellStyle(styleMoneyTop);
			} else {
				cell8.setCellStyle(styleMoney);
			}

			HSSFRichTextString blanco = new HSSFRichTextString(" ");
			HSSFCell cell9 = row.createCell(contCol++);
			if (l.getFechaDeposito() != null) {
				cell9.setCellValue(l.getFechaDeposito());
			} else {
				cell9.setCellValue(blanco);
			}
			if (nuevo) {
				cell9.setCellStyle(styleDateTop);
			} else {
				cell9.setCellStyle(styleDate);
			}

			HSSFCell cell10 = row.createCell(contCol++);
			if (l.getCtaDeposito() != null) {
				cell10.setCellValue(new HSSFRichTextString(l.getCtaDeposito()));
			} else {
				cell10.setCellValue(blanco);
			}
			if (nuevo) {
				cell10.setCellStyle(styleAllTop);
			} else {
				cell10.setCellStyle(styleAll);
			}

			HSSFCell cell11 = row.createCell(contCol++);
			if (l.getFechaReemplazo() != null) {
				cell11.setCellValue(l.getFechaReemplazo());
			} else {
				cell11.setCellValue(blanco);
			}
			if (nuevo) {
				cell11.setCellStyle(styleDateTop);
			} else {
				cell11.setCellStyle(styleDate);
			}

			HSSFCell cell12 = row.createCell(contCol++);
			if (l.getFechaRechazado() != null) {
				cell12.setCellValue(l.getFechaRechazado());
			} else {
				cell12.setCellValue(blanco);
			}
			if (nuevo) {
				cell12.setCellStyle(styleDateTop);
			} else {
				cell12.setCellStyle(styleDate);
			}
			
			HSSFCell cell13 = row.createCell(contCol++);
			if (l.getFechaReemplazoRechazado() != null) {
				cell13.setCellValue(l.getFechaReemplazoRechazado());
			} else {
				cell13.setCellValue(blanco);
			}
			if (nuevo) {
				cell13.setCellStyle(styleDateTopRight);
			} else {
				cell13.setCellStyle(styleDateRight);
			}
			
			
			
			HSSFCell cell121 = row.createCell(contCol++);
			if (l.getFechaJudicializado() != null) {
				cell121.setCellValue(l.getFechaJudicializado());
			} else {
				cell121.setCellValue(blanco);
			}
			if (nuevo) {
				cell121.setCellStyle(styleDateTop);
			} else {
				cell121.setCellStyle(styleDate);
			}
			
			
			
			if(entidad!=WebKeysGlobal.UOMA){
								
				HSSFCell cell14 = row.createCell(contCol++);
				if (l.getIdOrdenPago() != null) {
					cell14.setCellValue(new HSSFRichTextString(l.getIdOrdenPago()));
				} else {
					cell14.setCellValue(blanco);
				}
				if (nuevo) {
					cell14.setCellStyle(styleAllTop);
				} else {
					cell14.setCellStyle(styleAll);
				}
				
				HSSFCell cell15 = row.createCell(contCol++);
				if (l.getIdOrdenPago() != null) {
					cell15.setCellValue(l.getFechaOrdenPago());
				} else {
					cell15.setCellValue(blanco);
				}
				if (nuevo) {
					cell15.setCellStyle(styleDateTopRight);
				} else {
					cell15.setCellStyle(styleDateRight);
				}
			}

			i++;
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleAllTop);
		if(entidad!=WebKeysGlobal.UOMA){
//			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 17));
		}else{
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 15));
		}

		sheet.setColumnWidth(0, 2360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.setColumnWidth(5, 7360);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.setColumnWidth(8, 5360);
		sheet.autoSizeColumn((short) 9);
		sheet.setColumnWidth(10, 2360);
		sheet.autoSizeColumn((short) 11);
		sheet.setColumnWidth(12, 2360);
		sheet.setColumnWidth(13, 2360);
		sheet.autoSizeColumn((short) 14);
		sheet.autoSizeColumn((short) 15);
		sheet.autoSizeColumn((short) 16);
		if(entidad!=WebKeysGlobal.UOMA){
			sheet.autoSizeColumn((short) 17);
			sheet.autoSizeColumn((short) 18);
		}

		return wb;
	}

	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader, int entidad) {
		int contCol=0;
		HSSFRow row = sheet.createRow(1);
		
		HSSFCell cellR = row.createCell(contCol++);
		cellR.setCellValue(new HSSFRichTextString("Fecha Recibo"));
		cellR.setCellStyle(styleHeader);
		

		HSSFCell cell1 = row.createCell(contCol++);
		cell1.setCellValue(new HSSFRichTextString("Numero"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(contCol++);
		cell2.setCellValue(new HSSFRichTextString("Importe Total"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(contCol++);
		cell3.setCellValue(new HSSFRichTextString("Importe Total Ch."));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell = row.createCell(contCol++);
		cell.setCellValue(new HSSFRichTextString("Fecha Diferida"));
		cell.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(contCol++);
		cell4.setCellValue(new HSSFRichTextString("Cuit"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(contCol++);
		cell5.setCellValue(new HSSFRichTextString("Razon Social"));
		cell5.setCellStyle(styleHeader);
		
		HSSFCell cell51 = row.createCell(contCol++);
		cell51.setCellValue(new HSSFRichTextString("Ramo"));
		cell51.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(contCol++);
		cell6.setCellValue(new HSSFRichTextString("Nro Cheque"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(contCol++);
		cell7.setCellValue(new HSSFRichTextString("Banco"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(contCol++);
		cell8.setCellValue(new HSSFRichTextString("Importe Ch."));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(contCol++);
		cell9.setCellValue(new HSSFRichTextString("Deposito"));
		cell9.setCellStyle(styleHeader);

		HSSFCell cell10 = row.createCell(contCol++);
		cell10.setCellValue(new HSSFRichTextString("Cuenta Deposito"));
		cell10.setCellStyle(styleHeader);

		HSSFCell cell11 = row.createCell(contCol++);
		cell11.setCellValue(new HSSFRichTextString("Reemplazo"));
		cell11.setCellStyle(styleHeader);

		HSSFCell cell12 = row.createCell(contCol++);
		cell12.setCellValue(new HSSFRichTextString("Rechazado"));
		cell12.setCellStyle(styleHeader);
		
		HSSFCell cell13 = row.createCell(contCol++);
		cell13.setCellValue(new HSSFRichTextString("Reemplazo Rech."));
		cell13.setCellStyle(styleHeader);
		
		HSSFCell cell130 = row.createCell(contCol++);
		cell130.setCellValue(new HSSFRichTextString("Judicializado"));
		cell130.setCellStyle(styleHeader);
		
		if(entidad!=WebKeysGlobal.UOMA){
			
			HSSFCell cell14 = row.createCell(contCol++);
			cell14.setCellValue(new HSSFRichTextString("Entregado a 3ros. OP"));
			cell14.setCellStyle(styleHeader);
			
			HSSFCell cell15 = row.createCell(contCol++);
			cell15.setCellValue(new HSSFRichTextString("Fecha OP"));
			cell15.setCellStyle(styleHeader);
		}

		//wb.setRepeatingRowsAndColumns(0, 0, 17, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	}

	public static class ReporteListadoValores {
		private Date fecha;
		private String numero;
		private BigDecimal importeTotal;
		private String cuit;
		private String razonSoc;
		private Integer idRamoEmpresa;
		private BigDecimal nroCheque;
		private BigDecimal importeCheque;
		private String banco;
		private Date fechaDeposito;
		private String ctaDeposito;
		private Date fechaReemplazo;
		private Date fechaRechazado;
		private Date fechaReemplazoRechazado;
		private String idOrdenPago;
		private Date fechaOrdenPago;
		private Date fechaRecibo;
		private int entidad;
		private int idRecibo;
		private Date fechaJudicializado;
		
		
		

		public int getIdRecibo() {
			return idRecibo;
		}

		public void setIdRecibo(int idRecibo) {
			this.idRecibo = idRecibo;
		}

		public int getEntidad() {
			return entidad;
		}

		public void setEntidad(int entidad) {
			this.entidad = entidad;
		}

		public String getIdOrdenPago() {
			return idOrdenPago;
		}

		public void setIdOrdenPago(String idOrdenPago) {
			this.idOrdenPago = idOrdenPago;
		}

		public Date getFechaOrdenPago() {
			return fechaOrdenPago;
		}

		public void setFechaOrdenPago(Date fechaOrdenPago) {
			this.fechaOrdenPago = fechaOrdenPago;
		}

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public String getNumero() {
			return numero;
		}

		public void setNumero(String numero) {
			this.numero = numero;
		}

		public BigDecimal getImporteTotal() {
			return importeTotal;
		}

		public void setImporteTotal(BigDecimal importeTotal) {
			this.importeTotal = importeTotal;
		}

		public String getCuit() {
			return cuit;
		}

		public void setCuit(String cuit) {
			this.cuit = cuit;
		}

		public String getRazonSoc() {
			return razonSoc;
		}

		public void setRazonSoc(String razonSoc) {
			this.razonSoc = razonSoc;
		}

		public BigDecimal getNroCheque() {
			return nroCheque;
		}

		public void setNroCheque(BigDecimal nroCheque) {
			this.nroCheque = nroCheque;
		}

		public String getBanco() {
			return banco;
		}

		public void setBanco(String banco) {
			this.banco = banco;
		}

		public Date getFechaDeposito() {
			return fechaDeposito;
		}

		public void setFechaDeposito(Date fechaDeposito) {
			this.fechaDeposito = fechaDeposito;
		}

		public String getCtaDeposito() {
			return ctaDeposito;
		}

		public void setCtaDeposito(String ctaDeposito) {
			this.ctaDeposito = ctaDeposito;
		}

		public Date getFechaReemplazo() {
			return fechaReemplazo;
		}

		public void setFechaReemplazo(Date fechaReemplazo) {
			this.fechaReemplazo = fechaReemplazo;
		}

		public Date getFechaRechazado() {
			return fechaRechazado;
		}

		public void setFechaRechazado(Date fechaRechazado) {
			this.fechaRechazado = fechaRechazado;
		}

		public Date getFechaReemplazoRechazado() {
			return fechaReemplazoRechazado;
		}

		public void setFechaReemplazoRechazado(Date fechaReemplazoRechazado) {
			this.fechaReemplazoRechazado = fechaReemplazoRechazado;
		}
		
		

		public Date getFechaRecibo() {
			return fechaRecibo;
		}

		public void setFechaRecibo(Date fechaRecibo) {
			this.fechaRecibo = fechaRecibo;
		}

		public static ReporteListadoValores getMapping(ResultSet rs)
				throws SQLException {
			ReporteListadoValores repo = new ReporteListadoValores();
			repo.setFecha(rs.getDate("fecha_vto_cheque"));
			repo.setNumero(rs.getString("numero"));
			repo.setImporteTotal(rs.getBigDecimal("importe_total"));
			repo.setCuit(rs.getString("cuit"));
			repo.setRazonSoc(rs.getString("razon_soc"));
			repo.setNroCheque(rs.getBigDecimal("nro_cheque"));
			repo.setBanco(rs.getString("banco"));
			repo.setIdRamoEmpresa(rs.getInt("id_ramo_empresa"));
			repo.setFechaDeposito(rs.getDate("fecha_deposito"));
			repo.setCtaDeposito(rs.getString("cta_deposito"));
			repo.setFechaReemplazo(rs.getDate("fecha_reemplazo"));
			repo.setFechaRechazado(rs.getDate("fecha_rechazado"));
			repo.setFechaReemplazoRechazado(rs
					.getDate("fecha_reemplazo_rechazado"));
			repo.setImporteCheque(rs.getBigDecimal("importe_cheque"));
			repo.setIdOrdenPago(rs.getString("id_orden_pago"));
			repo.setFechaOrdenPago(rs.getDate("fecha_orden_pago"));
			repo.setFechaRecibo(rs.getDate("fecha"));
			repo.setIdRecibo(rs.getInt("id_recibo"));
			try {
				repo.setFechaJudicializado(rs.getDate("fecha_judicializado"));
			}catch(Exception e) {}
			return repo;
		}

		public void setImporteCheque(BigDecimal importeCheque) {
			this.importeCheque = importeCheque;
		}

		public BigDecimal getImporteCheque() {
			return importeCheque;
		}

		public Integer getIdRamoEmpresa() {
			return idRamoEmpresa;
		}

		public void setIdRamoEmpresa(Integer idRamoEmpresa) {
			this.idRamoEmpresa = idRamoEmpresa;
		}

		public Date getFechaJudicializado() {
			return fechaJudicializado;
		}

		public void setFechaJudicializado(Date fechaJudicializado) {
			this.fechaJudicializado = fechaJudicializado;
		}

		
	}
}
