package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
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
import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.ReporteActaBean;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.reportes.ReporteListadoValoresExcel.ReporteListadoValores;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteActas extends ReporteXLS {

	private static Log _log = LogFactoryUtil.getLog(ReporteActas.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			String amtimaStr=(String)req.getAttribute("amtima");
			if(null==amtimaStr){
				amtimaStr=req.getParameter("amtima");
			}
			int entidad=WebKeysGlobal.OSPIM;			
			if(null!= amtimaStr && amtimaStr.trim().equals("true")){				
				entidad=WebKeysGlobal.AMTIMA;
			}
			
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<ReporteActaBean> reporte = ActaServiceUtil.reporteActas(
					fechaIni, fechaFin, entidad);
			return generarReporte(fechaIni, fechaFin, reporte, false);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	public static HSSFWorkbook generarReporteSeguimiento(
			HttpServletRequest req, HttpServletResponse res) {
		try {
			String amtimaStr=(String)req.getAttribute("amtima");
			if(null==amtimaStr){
				amtimaStr=req.getParameter("amtima");
			}
			int entidad=WebKeysGlobal.OSPIM;
			boolean amtima=false;
			if(null!= amtimaStr && amtimaStr.trim().equals("true")){
				amtima=true;
				entidad=WebKeysGlobal.AMTIMA;
			}
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<ReporteActaBean> reporte = ActaServiceUtil.reporteActas(
					fechaIni, fechaFin, entidad);
			reporte.addAll(ActaNoOSServiceUtil.reporteActas(fechaIni, fechaFin));
			return generarReporte(fechaIni, fechaFin, reporte, true);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteActaBean> reporte, boolean seguimiento) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeader(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		HSSFCellStyle styleNumber = getStyleNumber(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		int i = 0;
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Actas"));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 21));
		i++;

		HSSFRow rowTitulo2 = sheet.createRow(i);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 21));
		i++;

		i = crearHeaderPrincipal(wb, sheet, i, styleHeader, true, seguimiento);
		BigDecimal totalCapital = BigDecimal.ZERO;
		BigDecimal totalInteres = BigDecimal.ZERO;
		BigDecimal totalOtros = BigDecimal.ZERO;
		BigDecimal totalDeudaActasAsociadas = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal totalRemuneracion = BigDecimal.ZERO;
		BigDecimal totalDeuda = BigDecimal.ZERO;
		BigDecimal totalCalc = BigDecimal.ZERO;
		BigDecimal totalPagado = BigDecimal.ZERO;
		BigDecimal totalInteresCalc = BigDecimal.ZERO;
		for (ReporteActaBean repo : reporte) {
			i = generarDatos(repo, i, styleAll, styleDate, styleMoney,
					styleNumber, sheet, true, seguimiento);

			totalCapital = totalCapital.add(repo.getCapital()!=null?repo.getCapital():new BigDecimal(0));
			totalInteres = totalInteres.add(repo.getInteres()!=null?repo.getInteres():new BigDecimal(0));
			totalOtros = totalOtros.add(repo.getOtros()!=null?repo.getOtros():new BigDecimal(0));
			totalDeudaActasAsociadas = totalDeudaActasAsociadas.add(repo
					.getDeudaActasAsociadas()!=null?repo.getDeudaActasAsociadas():new BigDecimal(0));
			total = total.add(repo.getTotal()!=null?repo.getTotal():new BigDecimal(0));
			if (!seguimiento) {
				totalRemuneracion = totalRemuneracion.add(repo
						.getTotalRemuneraciones());
				totalDeuda = totalDeuda.add(repo.getTotalDeuda());
				totalCalc = totalCalc.add(repo.getTotalCalculado());
				totalPagado = totalPagado.add(repo.getTotalPagado());
				totalInteresCalc = totalInteresCalc.add(repo.getTotalInteres());
			}
		}

		HSSFRow row = sheet.createRow(i);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("TOTALES"));
		cell6.setCellStyle(styleAll);

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(totalCapital.doubleValue());
		cell7.setCellStyle(styleMoneyBold);

		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(totalInteres.doubleValue());
		cell8.setCellStyle(styleMoneyBold);

		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(totalOtros.doubleValue());
		cell9.setCellStyle(styleMoneyBold);

		HSSFCell cell10 = row.createCell(10);
		cell10.setCellValue(totalDeudaActasAsociadas.doubleValue());
		cell10.setCellStyle(styleMoneyBold);

		HSSFCell cell12 = row.createCell(12);
		cell12.setCellValue(total.doubleValue());
		cell12.setCellStyle(styleMoneyBold);
		if (!seguimiento) {
			HSSFCell cell17 = row.createCell(17);
			cell17.setCellValue(totalRemuneracion.doubleValue());
			cell17.setCellStyle(styleMoneyBold);

			HSSFCell cell18 = row.createCell(18);
			cell18.setCellValue(totalCalc.doubleValue());
			cell18.setCellStyle(styleMoneyBold);

			HSSFCell cell19 = row.createCell(19);
			cell19.setCellValue(totalPagado.doubleValue());
			cell19.setCellStyle(styleMoneyBold);

			HSSFCell cell20 = row.createCell(20);
			cell20.setCellValue(totalDeuda.doubleValue());
			cell20.setCellStyle(styleMoneyBold);

			HSSFCell cell21 = row.createCell(21);
			cell21.setCellValue(totalInteresCalc.doubleValue());
			cell21.setCellStyle(styleMoneyBold);
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
		return wb;
	}

	public static int generarDatos(ReporteActaBean repo, int i,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber,
			HSSFSheet sheet, boolean estadistica, boolean seguimiento) {
		HSSFRow row = sheet.createRow(i);
		int j = 0;

		if (seguimiento) {
			HSSFCell cell00 = row.createCell(j++);
			cell00.setCellValue(new HSSFRichTextString(repo.getEntidad()));
			cell00.setCellStyle(styleAll);
		}

		HSSFCell cell0 = row.createCell(j++);
		cell0.setCellValue(new HSSFRichTextString(repo.getNumero()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = row.createCell(j++);
		cell1.setCellValue(repo.getFechaActa());
		cell1.setCellStyle(styleDate);

		HSSFCell cellAcreed = row.createCell(j++);
		cellAcreed.setCellValue(repo.getFechaActualizacion());
		cellAcreed.setCellStyle(styleDate);

		
		HSSFCell cellRaz = row.createCell(j++);
		if (seguimiento) {
			cellRaz.setCellValue(new HSSFRichTextString(""));
		}else{
			cellRaz.setCellValue(repo.getFechaRecepcion());
		}
		cellRaz.setCellStyle(styleDate);

		HSSFCell cell4 = row.createCell(j++);
		cell4.setCellValue(new HSSFRichTextString(repo.getCuit()));
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = row.createCell(j++);
		cell5.setCellValue(new HSSFRichTextString(repo.getSucursal()));
		cell5.setCellStyle(styleAll);

		HSSFCell cell6 = row.createCell(j++);
		cell6.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		cell6.setCellStyle(styleAll);

		HSSFCell cell7 = row.createCell(j++);
		cell7.setCellValue(repo.getCapital()!=null?repo.getCapital().doubleValue():0);
		cell7.setCellStyle(styleMoney);

		HSSFCell cell8 = row.createCell(j++);
		cell8.setCellValue(repo.getInteres()!=null?repo.getInteres().doubleValue():0);
		cell8.setCellStyle(styleMoney);

		HSSFCell cell9 = row.createCell(j++);
		cell9.setCellValue(repo.getOtros()!=null?repo.getOtros().doubleValue():0);
		cell9.setCellStyle(styleMoney);

		HSSFCell cell10 = row.createCell(j++);
		cell10.setCellValue(repo.getDeudaActasAsociadas()!=null?repo.getDeudaActasAsociadas().doubleValue():0);
		cell10.setCellStyle(styleMoney);

		HSSFCell cell11 = row.createCell(j++);
		cell11.setCellValue(new HSSFRichTextString(repo.getNumeroActaAsociada()));
		cell11.setCellStyle(styleAll);

		HSSFCell cell12 = row.createCell(j++);
		cell12.setCellValue(repo.getTotal()!=null?repo.getTotal().doubleValue():0);
		cell12.setCellStyle(styleMoney);

		HSSFCell cell13 = row.createCell(j++);
		cell13.setCellValue(new HSSFRichTextString(repo.getMolinera()
				.booleanValue() ? "Molinera" : ""));
		cell13.setCellStyle(styleAll);

		if (estadistica && !seguimiento) {
			HSSFCell cell14 = row.createCell(j++);
			cell14.setCellValue(repo.getPeriodos());
			cell14.setCellStyle(styleNumber);

			HSSFCell cell15 = row.createCell(j++);
			cell15.setCellValue(repo.getPromedioEmpleados());
			cell15.setCellStyle(styleNumber);

			HSSFCell cell16 = row.createCell(j++);
			cell16.setCellValue(repo.getPromedioPagados());
			cell16.setCellStyle(styleNumber);

			HSSFCell cell17 = row.createCell(j++);
			cell17.setCellValue(repo.getTotalRemuneraciones().doubleValue());
			cell17.setCellStyle(styleMoney);

			HSSFCell cell18 = row.createCell(j++);
			cell18.setCellValue(repo.getTotalCalculado().doubleValue());
			cell18.setCellStyle(styleMoney);

			HSSFCell cell19 = row.createCell(j++);
			cell19.setCellValue(repo.getTotalPagado().doubleValue());
			cell19.setCellStyle(styleMoney);

			HSSFCell cell20 = row.createCell(j++);
			cell20.setCellValue(repo.getTotalDeuda().doubleValue());
			cell20.setCellStyle(styleMoney);

			HSSFCell cell21 = row.createCell(j++);
			cell21.setCellValue(repo.getTotalInteres().doubleValue());
			cell21.setCellStyle(styleMoney);
		}
		if (seguimiento) {
			HSSFCell cell22 = row.createCell(j++);
			cell22.setCellValue(new HSSFRichTextString(repo.getInspectores()));
			cell22.setCellStyle(styleAll);
		}
		return ++i;
	}

	public static int crearHeaderPrincipal(HSSFWorkbook wb, HSSFSheet sheet,
			int i, HSSFCellStyle styleHeader, boolean estadistica,
			boolean seguimiento) {
		HSSFRow row = sheet.createRow(i);
		int j = 0;
		if (seguimiento) {
			HSSFCell cell00 = row.createCell(j++);
			cell00.setCellValue(new HSSFRichTextString("Entidad"));
			cell00.setCellStyle(styleHeader);
		}

		HSSFCell cell0 = row.createCell(j++);
		cell0.setCellValue(new HSSFRichTextString("Numero"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(j++);
		cell1.setCellValue(new HSSFRichTextString("Fecha Acta"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(j++);
		cellAcreed.setCellValue(new HSSFRichTextString("Fecha Actualización"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(j++);
		cellRaz.setCellValue(new HSSFRichTextString("Fecha Recepcion"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(j++);
		cell4.setCellValue(new HSSFRichTextString("Cuit"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(j++);
		cell5.setCellValue(new HSSFRichTextString("Sucursal"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(j++);
		cell6.setCellValue(new HSSFRichTextString("Razon Social"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(j++);
		cell7.setCellValue(new HSSFRichTextString("Capital"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(j++);
		cell8.setCellValue(new HSSFRichTextString("Interes"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(j++);
		cell9.setCellValue(new HSSFRichTextString("Otros"));
		cell9.setCellStyle(styleHeader);

		HSSFCell cell10 = row.createCell(j++);
		cell10.setCellValue(new HSSFRichTextString("Deudas Actas Asoc"));
		cell10.setCellStyle(styleHeader);

		HSSFCell cell11 = row.createCell(j++);
		cell11.setCellValue(new HSSFRichTextString("Numero Acta Asoc"));
		cell11.setCellStyle(styleHeader);

		HSSFCell cell12 = row.createCell(j++);
		cell12.setCellValue(new HSSFRichTextString("Total"));
		cell12.setCellStyle(styleHeader);

		HSSFCell cell13 = row.createCell(j++);
		cell13.setCellValue(new HSSFRichTextString("Molinera"));
		cell13.setCellStyle(styleHeader);

		if (estadistica && !seguimiento) {
			HSSFCell cell14 = row.createCell(j++);
			cell14.setCellValue(new HSSFRichTextString("Cant Periodos"));
			cell14.setCellStyle(styleHeader);

			HSSFCell cell15 = row.createCell(j++);
			cell15.setCellValue(new HSSFRichTextString("Promedio Empleados"));
			cell15.setCellStyle(styleHeader);

			HSSFCell cell16 = row.createCell(j++);
			cell16.setCellValue(new HSSFRichTextString("Promedio Pagados"));
			cell16.setCellStyle(styleHeader);

			HSSFCell cell17 = row.createCell(j++);
			cell17.setCellValue(new HSSFRichTextString("Total Remuneraciones"));
			cell17.setCellStyle(styleHeader);

			HSSFCell cell18 = row.createCell(j++);
			cell18.setCellValue(new HSSFRichTextString("Total Calculado"));
			cell18.setCellStyle(styleHeader);

			HSSFCell cell19 = row.createCell(j++);
			cell19.setCellValue(new HSSFRichTextString("Total Pagado"));
			cell19.setCellStyle(styleHeader);

			HSSFCell cell20 = row.createCell(j++);
			cell20.setCellValue(new HSSFRichTextString("Total Deuda"));
			cell20.setCellStyle(styleHeader);

			HSSFCell cell21 = row.createCell(j++);
			cell21.setCellValue(new HSSFRichTextString(
					"Total Interes Calculado"));
			cell21.setCellStyle(styleHeader);
		}

		if (seguimiento) {
			HSSFCell cell22 = row.createCell(j++);
			cell22.setCellValue(new HSSFRichTextString("Inspectores"));
			cell22.setCellStyle(styleHeader);
		}

		return ++i;
	}
	

//-------------------------------
//-------------------------------
//-------------------------------	
	
	public static HSSFWorkbook generarReporteConvenioEstadistico(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			String amtimaStr=(String)req.getAttribute("amtima");
			if(null==amtimaStr){
				amtimaStr=req.getParameter("amtima");
			}
			int entidad=WebKeysGlobal.OSPIM;			
			if(null!= amtimaStr && amtimaStr.trim().equals("true")){				
				entidad=WebKeysGlobal.AMTIMA;
			}
			
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<ReporteActaBean> reporte = ActaServiceUtil.reporteActas(fechaIni, fechaFin, entidad);
			reporte.addAll(ActaNoOSServiceUtil.reporteActas(fechaIni, fechaFin));
			for(ReporteActaBean r : reporte){
				//List<ActaPago> aps = new ArrayList<ActaPago>();
				Acta ac = null;
				
				Integer ent =0;
				if("O.S.P.I.M.".equalsIgnoreCase(r.getEntidad())){
					ent= WebKeysGlobal.OSPIM;
				}else if("U.O.M.A.".equalsIgnoreCase(r.getEntidad())){
					ent= WebKeysGlobal.UOMA; 
				}else{
					ent= WebKeysGlobal.AMTIMA;  
				}
				
				
				if("O.S.P.I.M.".equalsIgnoreCase(r.getEntidad())){
                   ac=ActaServiceUtil.getActa(r.getId(), 0);
 				}else{
 				   ac=ActaNoOSServiceUtil.getActa(r.getId(), 0);	
 				}
				r.setCobrado("NO");
				if(ac!=null && ac.getPagos()!=null && ac.getPagos().size()>0){
				  for(ActaPago p:ac.getPagos()){
					if(p.getTipo().toString().equalsIgnoreCase(ConvenioPago.Tipo.PAGO.toString())){  
					  if(p.getConvenioCancelatorio() != null){
						Convenio convenio =   new Convenio();
						if("O.S.P.I.M.".equalsIgnoreCase(r.getEntidad())){
			                 convenio=ConvenioServiceUtil.getConvenio(p.getConvenioCancelatorio().getId(),0);
			 			}else{
			 				 convenio=ConvenioNoOSServiceUtil.getConvenio(p.getConvenioCancelatorio().getId(), 0, entidad);
			 			}
						r.setConvenio(convenio);
						r.setCobrado("CONVENIO"); 
						Double cobrado=0D;
						Double noCobrado=0D;
						Double aVencer=0D;
						Double cantidadPagos=0D;
						Double cantidadCobrado=0D;
						Double cantidadNoCobrado=0D;
						Double cantidadAVencer=0D;
						Double cantidadCuotasEnCondicionesCobrar=0D;
						for(ConvenioPago cp:convenio.getPagos()){
							if(cp.getTipo().toString().equalsIgnoreCase(ConvenioPago.Tipo.PAGO.toString())){  
								cantidadPagos++;
								if(cp.getFechaPago().before(fechaFin)){
									cantidadCuotasEnCondicionesCobrar++;
									if(cp.getRecibo()!=null){
									  cobrado+=cp.getImporte().doubleValue()+cp.getInteres().doubleValue();
									  cantidadCobrado++;
									}else{
										
										List<ReporteListadoValores> listadoValores = new ArrayList<ReporteListadoValores>();
										try{
										 
									     listadoValores = ContabilidadServiceUtil.listadoValores(
											  null, null,null, null, null,null, null, null,r.getCuit(), 
											  cp.getCheque().getBanco().getId_banco(), -1,-1,-1, null, 
											  ent,cp.getCheque().getNumero().intValue(), null, null);
										}catch(Exception e){} 
									    if(listadoValores.size()>0 && listadoValores.get(0).getFechaRechazado()==null){
										  cobrado+=cp.getImporte().doubleValue()+cp.getInteres().doubleValue();
										  cantidadCobrado++;
									    }else{
									      noCobrado+=cp.getImporte().doubleValue()+cp.getInteres().doubleValue();
									      cantidadNoCobrado++;
									    }  
									}
								}else if(cp.getFechaPago().after(fechaFin)){
									aVencer+=cp.getImporte().doubleValue()+cp.getInteres().doubleValue();
									cantidadAVencer++;
								}
							}
						}
						r.setConvenioCobrado(cobrado);
						r.setConvenioNoCobrado(noCobrado);
						r.setConvenioAVencer(aVencer);
						if(cantidadCuotasEnCondicionesCobrar!=0){
						   r.setCantidadConvenioCobrado(cantidadCobrado*100/cantidadCuotasEnCondicionesCobrar);
						   r.setCantidadConvenioNoCobrado(cantidadNoCobrado*100/cantidadCuotasEnCondicionesCobrar);
						}else{
						   r.setCantidadConvenioCobrado(0D);
						   r.setCantidadConvenioNoCobrado(0D);	
						}
						
						if(cantidadPagos!=0){
						   r.setCantidadConvenioAVencer(cantidadAVencer*100/cantidadPagos);
						} else {
						   r.setCantidadConvenioAVencer(0D);	
						}
					  }else if(p.getRecibo()!=null || ac.getPagosIngresados().size()>0){
						r.setCobrado("SI");  
					  }
					} 
					
					if("NO".equalsIgnoreCase(r.getCobrado()) &&  !"".equalsIgnoreCase(ac.getNumero())){
						List<CuentaCorriente> ctas = null;
						ctas = ContabilidadServiceUtil.cuentaCorrienteActasYConvenios(
									fechaIni, new Date(),r.getCuit(), "000",0,0,"", ent);
						for(CuentaCorriente cc:ctas){
						   for(CuentaCorriente.Informacion i:cc.getInfo()){
							  if(i.getDescripcion().startsWith( "AC: "+ac.getNumero()) &&  "D".equalsIgnoreCase(i.getDebitoCredito())){
								  r.setCobrado("SI");   
								  break;  
							  }
						   }
						   if("SI".equalsIgnoreCase(r.getCobrado())){
								 break; 
						   }
						}
					}
					
					
				  } //Fin for
				}  
				
			}
			
			
			
			return generarReporteConvenioEstadistico(fechaIni, fechaFin, reporte, true);
		} catch (Exception e) {
			_log.error("Error al generar actas convenios estadistico", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporteConvenioEstadistico(Date fechaIni, Date fechaFin,
			List<ReporteActaBean> reporte, boolean seguimiento) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeader(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		HSSFCellStyle styleNumber = getStyleNumber(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		int i = 0;
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Actas (Analisis Convenios)"));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 21));
		i++;

		HSSFRow rowTitulo2 = sheet.createRow(i);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 21));
		i++;

		i = crearHeaderPrincipalConvenioEstadistico(wb, sheet, i, styleHeader, true, seguimiento);
		BigDecimal totalCapital = BigDecimal.ZERO;
		BigDecimal totalInteres = BigDecimal.ZERO;
		BigDecimal totalOtros = BigDecimal.ZERO;
		BigDecimal totalDeudaActasAsociadas = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal totalRemuneracion = BigDecimal.ZERO;
		BigDecimal totalDeuda = BigDecimal.ZERO;
		BigDecimal totalCalc = BigDecimal.ZERO;
		BigDecimal totalPagado = BigDecimal.ZERO;
		BigDecimal totalInteresCalc = BigDecimal.ZERO;
		for (ReporteActaBean repo : reporte) {
			i = generarDatosConvenioEstadistico(repo, i, styleAll, styleDate, styleMoney,
					styleNumber, sheet, true, seguimiento);
		}

		HSSFRow row = sheet.createRow(i);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("TOTALES"));
		cell6.setCellStyle(styleAll);
		
		HSSFCell cellT1 = row.createCell(10);
		cellT1.setCellFormula("SUM(K"+Integer.toString(4)  +":K"+ Integer.toString(i) +")");
		cellT1.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT2 = row.createCell(11);
		cellT2.setCellFormula("SUM(L"+Integer.toString(4)  +":L"+ Integer.toString(i) +")");
		cellT2.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT3 = row.createCell(12);
		cellT3.setCellFormula("SUM(M"+Integer.toString(4)  +":M"+ Integer.toString(i) +")");
		cellT3.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT4 = row.createCell(13);
		cellT4.setCellFormula("SUM(N"+Integer.toString(4)  +":N"+ Integer.toString(i) +")");
		cellT4.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT5 = row.createCell(14);
		cellT5.setCellFormula("SUM(O"+Integer.toString(4)  +":O"+ Integer.toString(i) +")");
		cellT5.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT6 = row.createCell(15);
		cellT6.setCellFormula("SUM(P"+Integer.toString(4)  +":P"+ Integer.toString(i) +")");
		cellT6.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT7 = row.createCell(18);
		cellT7.setCellFormula("SUM(S"+Integer.toString(4)  +":S"+ Integer.toString(i) +")");
		cellT7.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT8 = row.createCell(19);
		cellT8.setCellFormula("SUM(T"+Integer.toString(4)  +":T"+ Integer.toString(i) +")");
		cellT8.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT9 = row.createCell(20);
		cellT9.setCellFormula("SUM(U"+Integer.toString(4)  +":U"+ Integer.toString(i) +")");
		cellT9.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT10 = row.createCell(21);
		cellT10.setCellFormula("SUM(V"+Integer.toString(4)  +":V"+ Integer.toString(i) +")");
		cellT10.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT11 = row.createCell(23);
		cellT11.setCellFormula("SUM(X"+Integer.toString(4)  +":X"+ Integer.toString(i) +")");
		cellT11.setCellStyle(styleMoneyBold);
		
		HSSFCell cellT12 = row.createCell(25);
		cellT12.setCellFormula("SUM(Z"+Integer.toString(4)  +":Z"+ Integer.toString(i) +")");
		cellT12.setCellStyle(styleMoneyBold);
		
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
		
		sheet.setColumnWidth(10, 4000);
		sheet.setColumnWidth(11, 4000);
		sheet.setColumnWidth(12, 4000);
		sheet.setColumnWidth(13, 4000);
		sheet.setColumnWidth(14, 4000);
		sheet.setColumnWidth(15, 4000);
		sheet.setColumnWidth(16, 4000);
		sheet.setColumnWidth(17, 4000);
		sheet.setColumnWidth(18, 4000);
		sheet.setColumnWidth(19, 4000);
		sheet.setColumnWidth(20, 4000);
		sheet.setColumnWidth(21, 4000);
		sheet.setColumnWidth(22, 4000);
		sheet.setColumnWidth(23, 4000);
		sheet.setColumnWidth(24, 4000);
		sheet.setColumnWidth(25, 4000);
		sheet.setColumnWidth(26, 4000);
		
		/*
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
	    */
		return wb;
	}

	public static int crearHeaderPrincipalConvenioEstadistico(HSSFWorkbook wb, HSSFSheet sheet,
			int i, HSSFCellStyle styleHeader, boolean estadistica,
			boolean seguimiento) {
		HSSFRow row = sheet.createRow(i);
		int j = 0;
		
		HSSFCell cell4 = row.createCell(j++);
		cell4.setCellValue(new HSSFRichTextString("Cuit"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(j++);
		cell5.setCellValue(new HSSFRichTextString("Sucursal"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(j++);
		cell6.setCellValue(new HSSFRichTextString("Razon Social"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell13 = row.createCell(j++);
		cell13.setCellValue(new HSSFRichTextString("Molinera"));
		cell13.setCellStyle(styleHeader);
		
		HSSFCell cell00 = row.createCell(j++);
		cell00.setCellValue(new HSSFRichTextString("Entidad"));
		cell00.setCellStyle(styleHeader);
		
		HSSFCell cell0 = row.createCell(j++);
		cell0.setCellValue(new HSSFRichTextString("Numero"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(j++);
		cell1.setCellValue(new HSSFRichTextString("Fecha Acta"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(j++);
		cellAcreed.setCellValue(new HSSFRichTextString("Fecha Actualización"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(j++);
		cellRaz.setCellValue(new HSSFRichTextString("Fecha Recepcion"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell22 = row.createCell(j++);
		cell22.setCellValue(new HSSFRichTextString("Inspectores"));
		cell22.setCellStyle(styleHeader);
		
		HSSFCell cell7 = row.createCell(j++);
		cell7.setCellValue(new HSSFRichTextString("Capital"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(j++);
		cell8.setCellValue(new HSSFRichTextString("Interes"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(j++);
		cell9.setCellValue(new HSSFRichTextString("Otros"));
		cell9.setCellStyle(styleHeader);

		HSSFCell cell10 = row.createCell(j++);
		cell10.setCellValue(new HSSFRichTextString("Deudas Actas Asoc"));
		cell10.setCellStyle(styleHeader);

		HSSFCell cell11 = row.createCell(j++);
		cell11.setCellValue(new HSSFRichTextString("Numero Acta Asoc"));
		cell11.setCellStyle(styleHeader);

		HSSFCell cell12 = row.createCell(j++);
		cell12.setCellValue(new HSSFRichTextString("Total"));
		cell12.setCellStyle(styleHeader);

		HSSFCell cell14 = row.createCell(j++);
		cell14.setCellValue(new HSSFRichTextString("Cobrado"));
		cell14.setCellStyle(styleHeader);
		
		HSSFCell cell15 = row.createCell(j++);
		cell15.setCellValue(new HSSFRichTextString("Conv.Nro"));
		cell15.setCellStyle(styleHeader);
		
		HSSFCell cell16 = row.createCell(j++);
		cell16.setCellValue(new HSSFRichTextString("Conv.Capital"));
		cell16.setCellStyle(styleHeader);
		
		HSSFCell cell17 = row.createCell(j++);
		cell17.setCellValue(new HSSFRichTextString("Conv.Interes"));
		cell17.setCellStyle(styleHeader);
		
		HSSFCell cell18 = row.createCell(j++);
		cell18.setCellValue(new HSSFRichTextString("Conv.Total"));
		cell18.setCellStyle(styleHeader);
		
		HSSFCell cell19 = row.createCell(j++);
		cell19.setCellValue(new HSSFRichTextString("Conv.Cobrado"));
		cell19.setCellStyle(styleHeader);
		
		HSSFCell cell20 = row.createCell(j++);
		cell20.setCellValue(new HSSFRichTextString("% Cumplimiento"));
		cell20.setCellStyle(styleHeader);
		
		HSSFCell cell21 = row.createCell(j++);
		cell21.setCellValue(new HSSFRichTextString("Conv.No Cobrado"));
		cell21.setCellStyle(styleHeader);
		
		HSSFCell cell23 = row.createCell(j++);
		cell23.setCellValue(new HSSFRichTextString("% Sin Cumplir"));
		cell23.setCellStyle(styleHeader);

		HSSFCell cell24 = row.createCell(j++);
		cell24.setCellValue(new HSSFRichTextString("Conv.A Vencer"));
		cell24.setCellStyle(styleHeader);
		
		HSSFCell cell25 = row.createCell(j++);
		cell25.setCellValue(new HSSFRichTextString("% A Vencer"));
		cell25.setCellStyle(styleHeader);
		
		return ++i;
	}

	public static int generarDatosConvenioEstadistico(ReporteActaBean repo, int i,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber,
			HSSFSheet sheet, boolean estadistica, boolean seguimiento) {
		HSSFRow row = sheet.createRow(i);
		int j = 0;

		HSSFCell cell4 = row.createCell(j++);
		cell4.setCellValue(new HSSFRichTextString(repo.getCuit()));
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = row.createCell(j++);
		cell5.setCellValue(new HSSFRichTextString(repo.getSucursal()));
		cell5.setCellStyle(styleAll);

		HSSFCell cell6 = row.createCell(j++);
		cell6.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		cell6.setCellStyle(styleAll);

		HSSFCell cell13 = row.createCell(j++);
		cell13.setCellValue(new HSSFRichTextString(repo.getMolinera()
				.booleanValue() ? "Molinera" : ""));
		cell13.setCellStyle(styleAll);
		
		HSSFCell cell00 = row.createCell(j++);
		cell00.setCellValue(new HSSFRichTextString(repo.getEntidad()));
		cell00.setCellStyle(styleAll);
		
		HSSFCell cell0 = row.createCell(j++);
		cell0.setCellValue(new HSSFRichTextString(repo.getNumero()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = row.createCell(j++);
		cell1.setCellValue(repo.getFechaActa());
		cell1.setCellStyle(styleDate);

		HSSFCell cellAcreed = row.createCell(j++);
		cellAcreed.setCellValue(repo.getFechaActualizacion());
		cellAcreed.setCellStyle(styleDate);

		HSSFCell cellRaz = row.createCell(j++);
		cellRaz.setCellValue(repo.getFechaRecepcion());
		cellRaz.setCellStyle(styleDate);

		HSSFCell cell22 = row.createCell(j++);
		cell22.setCellValue(new HSSFRichTextString(repo.getInspectores()));
		cell22.setCellStyle(styleAll);
		
		HSSFCell cell7 = row.createCell(j++);
		cell7.setCellValue(repo.getCapital()!=null?repo.getCapital().doubleValue():0);
		cell7.setCellStyle(styleMoney);

		HSSFCell cell8 = row.createCell(j++);
		cell8.setCellValue(repo.getInteres()!=null?repo.getInteres().doubleValue():0);
		cell8.setCellStyle(styleMoney);

		HSSFCell cell9 = row.createCell(j++);
		cell9.setCellValue(repo.getOtros()!=null?repo.getOtros().doubleValue():0);
		cell9.setCellStyle(styleMoney);

		HSSFCell cell10 = row.createCell(j++);
		cell10.setCellValue(repo.getDeudaActasAsociadas()!=null?repo.getDeudaActasAsociadas().doubleValue():0);
		cell10.setCellStyle(styleMoney);

		HSSFCell cell11 = row.createCell(j++);
		cell11.setCellValue(new HSSFRichTextString(repo.getNumeroActaAsociada()));
		cell11.setCellStyle(styleAll);

		HSSFCell cell12 = row.createCell(j++);
		cell12.setCellValue(repo.getTotal()!=null?repo.getTotal().doubleValue():0);
		cell12.setCellStyle(styleMoney);

		HSSFCell cell14 = row.createCell(j++);
		cell14.setCellValue(new HSSFRichTextString(repo.getCobrado()));
		cell14.setCellStyle(styleAll);
		
		HSSFCell cell15 = row.createCell(j++);
		cell15.setCellValue(new HSSFRichTextString(repo.getConvenio()!=null && 
				                                     repo.getConvenio().getNumero()!=null?repo.getConvenio().getNumero():""));
		cell15.setCellStyle(styleAll);
		
		HSSFCell cell16 = row.createCell(j++);
		if(repo.getConvenio()!=null && repo.getConvenio().getCapitalFromPagos()!=null){
		    cell16.setCellValue( repo.getConvenio().getCapitalFromPagos().doubleValue());
		}else{
			cell16.setCellValue( new HSSFRichTextString(""));
		}
		cell16.setCellStyle(styleMoney);

		
		HSSFCell cell17 = row.createCell(j++);
		if(repo.getConvenio()!=null && repo.getConvenio().getInteresFromPagos()!=null){
		    cell17.setCellValue( repo.getConvenio().getInteresFromPagos().doubleValue());
		}else{
			cell17.setCellValue( new HSSFRichTextString(""));
		}
		cell17.setCellStyle(styleMoney);
		
		
		HSSFCell cell18 = row.createCell(j++);
		if(repo.getConvenio()!=null && repo.getConvenio().getCapitalFromPagos()!=null){
		    cell18.setCellValue(repo.getConvenio().getCapitalFromPagos().doubleValue() +  
				repo.getConvenio().getInteresFromPagos().doubleValue());
		}else{
			cell18.setCellValue( new HSSFRichTextString(""));
		}    
		cell18.setCellStyle(styleMoney);
		
		HSSFCell cell19 = row.createCell(j++);
		if(repo.getConvenioCobrado()!=null){
		    cell19.setCellValue(repo.getConvenioCobrado());
		}else{
			cell19.setCellValue( new HSSFRichTextString(""));
		}    
		cell19.setCellStyle(styleMoney);
		
		HSSFCell cell20 = row.createCell(j++);
		if(repo.getCantidadConvenioCobrado()!=null){
		    cell20.setCellValue(repo.getCantidadConvenioCobrado());
		}else{
			cell20.setCellValue( new HSSFRichTextString(""));
		}    
		cell20.setCellStyle(styleMoney);

		
		HSSFCell cell21 = row.createCell(j++);
		if(repo.getConvenioNoCobrado()!=null){
		    cell21.setCellValue(repo.getConvenioNoCobrado());
		}else{
			cell21.setCellValue( new HSSFRichTextString(""));
		}    
		cell21.setCellStyle(styleMoney);
		
		HSSFCell cell23 = row.createCell(j++);
		if(repo.getCantidadConvenioNoCobrado()!=null){
		    cell23.setCellValue(repo.getCantidadConvenioNoCobrado());
		}else{
			cell23.setCellValue( new HSSFRichTextString(""));
		}    
		cell23.setCellStyle(styleMoney);
		
		
		HSSFCell cell24 = row.createCell(j++);
		if(repo.getConvenioAVencer()!=null){
		    cell24.setCellValue(repo.getConvenioAVencer());
		}else{
			cell24.setCellValue( new HSSFRichTextString(""));
		}    
		cell24.setCellStyle(styleMoney);
		
		
		HSSFCell cell25 = row.createCell(j++);
		if(repo.getCantidadConvenioAVencer()!=null){
		    cell25.setCellValue(repo.getCantidadConvenioAVencer());
		}else{
			cell25.setCellValue( new HSSFRichTextString(""));
		}    
		cell25.setCellStyle(styleMoney);
		
		return ++i;
	}

}
