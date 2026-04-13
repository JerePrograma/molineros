package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.beans.BalanceSumasYSaldos;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteContabilidadBalanceSumasSaldosExcel extends
		ReporteConabilidad {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteContabilidadBalanceSumasSaldosExcel.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			
			int entidad=WebKeysGlobal.OSPIM;
			if(ParamUtil.getInteger(req, "entidad")>0){
				entidad=ParamUtil.getInteger(req, "entidad");
			}
			
			Calendar desdeC = DateUtils.getDesdePeriodo(req, entidad);
			Calendar hastaC = DateUtils.getHastaPeriodo(req, entidad);

			String cuentas = req.getParameter("cuentas");

			boolean incluirAutomaticos = ParamUtil.getBoolean(req,
					"incluir_automaticos");
			boolean incluirManuales = ParamUtil.getBoolean(req,
					"incluir_manuales");
			boolean incluir_asiento_inicial = ParamUtil.getBoolean(req,
					"incluir_asiento_inicial");
			// boolean incluir_asiento_final = ParamUtil.getBoolean(req,
			// "incluir_asiento_final");
			boolean incluir_saldo_inicial = ParamUtil.getBoolean(req,
					"incluir_saldo_inicial");
			
			
			boolean incluir_ajuste_inflacion = ParamUtil.getBoolean(req,
					"incluir_ajuste_inflacion");

			List<BalanceSumasYSaldos> balanceSumasYSaldos = AsientoServiceUtil
					.buscarBalanceSumasYSaldos(desdeC.getTime(),
							hastaC.getTime(), incluirAutomaticos,
							incluirManuales, false, entidad);

			List<BalanceSumasYSaldos> saldosIniciales = null;
			Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(req, entidad);
			Calendar hastaEjercicio = DateUtils.getHastaEjercicio(req, entidad);
			if (incluir_saldo_inicial) {
				saldosIniciales = getSaldoInicial(desdeC, desdeEjercicio,
						hastaEjercicio, incluirAutomaticos, incluirManuales,
						incluir_asiento_inicial, entidad);
			}
			mergearCuentas(balanceSumasYSaldos, saldosIniciales);

			Set<String> filtroCuentas = new HashSet<String>();
			if (StringUtils.isNotBlank(cuentas) && !cuentas.equals("null")) {
				String numerosCuentas[] = cuentas.split(",");
				for (String nro : numerosCuentas) {
					filtroCuentas.add(nro.trim());
				}
			}
			
			if(incluir_ajuste_inflacion){
			  ajustarCuentas(balanceSumasYSaldos,desdeC.getTime(),hastaC.getTime(),desdeEjercicio.getTime(),hastaEjercicio.getTime(),filtroCuentas,entidad);	
			}
			
			return generarReporte(desdeC.getTime(), hastaC.getTime(),
					balanceSumasYSaldos, saldosIniciales, filtroCuentas, entidad,incluir_ajuste_inflacion);
		} catch (Exception e) {
			_log.error("Error al generar diario", e);
			return null;
		}
	}

	private static void mergearCuentas(
			List<BalanceSumasYSaldos> balanceSumasYSaldos,
			List<BalanceSumasYSaldos> saldosIniciales) {
		// agrego todas las cuentas para las que exista un saldo
		// inicial/anterior pero que no existan asientos para el periodo dado
		if (saldosIniciales != null) {
			for (BalanceSumasYSaldos saldos : saldosIniciales) {
				BalanceSumasYSaldos balanceSaldoInicial = new BalanceSumasYSaldos(
						new PlanCuentas(saldos.getNumeroCuenta(),
								saldos.getDescripcionCuenta()));
				if (!balanceSumasYSaldos.contains(balanceSaldoInicial)) {
					balanceSaldoInicial.setDebe(BigDecimal.ZERO);
					balanceSaldoInicial.setHaber(BigDecimal.ZERO);
					balanceSumasYSaldos.add(balanceSaldoInicial);
				}
			}
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<BalanceSumasYSaldos> balanceSumasYSaldos,
			List<BalanceSumasYSaldos> saldosIniciales, Set<String> filtroCuentas, int entidad,
			boolean incluir_ajuste_inflacion) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = getStyleAllWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleAllBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);
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
		ps.setLandscape(false);

		int i = crearHeaderPrincipal(wb, sheet, 6, entidad);

		if (balanceSumasYSaldos == null || balanceSumasYSaldos.size() == 0) {
			return wb;
		}
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
		String titulo = "Balance de sumas y saldos. Ejercicio: "
				+ format.format(fechaIni)
				+ ". "
				+ formatFecha.format(fechaIni)
				+ " al "
				+ formatFecha.format(fechaFin)
				+ ". Cuentas: "
				+ balanceSumasYSaldos.get(0).getNumeroCuenta()
				+ " al "
				+ balanceSumasYSaldos.get(balanceSumasYSaldos.size() - 1)
						.getNumeroCuenta();
		cell.setCellValue(new HSSFRichTextString(titulo));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;
		i = crearHeader(sheet, i, styleAllBorder,incluir_ajuste_inflacion);

		Collections.sort(balanceSumasYSaldos);
		i = generarDatos(balanceSumasYSaldos, saldosIniciales, i, styleAll,
				styleDate, styleMoney, styleNumber, sheet, styleAllBorder,
				styleMoneyBold, styleAllBold, filtroCuentas,incluir_ajuste_inflacion);

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
		return wb;
	}

	private static int generarDatos(
			List<BalanceSumasYSaldos> balanceSumasYSaldos,
			List<BalanceSumasYSaldos> saldosIniciales, int i,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber,
			HSSFSheet sheet, HSSFCellStyle styleAllBorder,
			HSSFCellStyle styleMoneyBold, HSSFCellStyle styleAllBold,
			Set<String> filtroCuentas,boolean incluir_ajuste_inflacion) {

		BigDecimal debe = BigDecimal.ZERO;
		BigDecimal haber = BigDecimal.ZERO;
		BigDecimal saldo = BigDecimal.ZERO;
		
		BigDecimal debeAcumulado = BigDecimal.ZERO;
		BigDecimal haberAcumulado = BigDecimal.ZERO;
		BigDecimal saldoAcumulado = BigDecimal.ZERO;

		for (BalanceSumasYSaldos repo : balanceSumasYSaldos) {
			if (filtroCuentas.size() > 0
					&& !filtroCuentas.contains(repo.getNumeroCuenta())) {
				continue;
			}
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue(new HSSFRichTextString(repo.getNumeroCuenta()));
			cell0.setCellStyle(styleAllBorder);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(repo
					.getDescripcionCuenta()));
			cell1.setCellStyle(styleAllBorder);

			BigDecimal saldoInicial = BigDecimal.ZERO;
			if (saldosIniciales != null) {
				int indexOf = saldosIniciales.indexOf(repo);
				if (indexOf != -1) {
					BalanceSumasYSaldos balanceInicial = saldosIniciales
							.get(indexOf);
					saldoInicial = balanceInicial.getDebe().subtract(
							balanceInicial.getHaber());
				}
			}
			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(saldoInicial.doubleValue());
			cell2.setCellStyle(styleMoney);

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(repo.getDebe().doubleValue());
			cell3.setCellStyle(styleMoney);
			debe = debe.add(repo.getDebe());

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(repo.getHaber().doubleValue());
			cell4.setCellStyle(styleMoney);
			haber = haber.add(repo.getHaber());

			HSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(repo.getDebe().subtract(repo.getHaber())
					.add(saldoInicial).doubleValue());
			cell5.setCellStyle(styleMoney);
			saldo = saldo.add(repo.getDebe().subtract(repo.getHaber())).add(
					saldoInicial);
			
			
			if(incluir_ajuste_inflacion) {
				  HSSFCell cell6 = row.createCell(6);
				  cell6.setCellValue(repo.getDebeAjustado().doubleValue());
				  cell6.setCellStyle(styleMoney);
				  debeAcumulado = debeAcumulado.add(repo.getDebeAjustado());

				  HSSFCell cell7 = row.createCell(7);
				  cell7.setCellValue(repo.getHaberAjustado().doubleValue());
				  cell7.setCellStyle(styleMoney);
				  haberAcumulado = haberAcumulado.add(repo.getHaberAjustado());

				   HSSFCell cell8 = row.createCell(8);
//				      cell8.setCellValue(repo.getDebeAjustado().subtract(repo.getHaberAjustado())
//						   .add(saldoInicial).doubleValue());
				      
				      cell8.setCellValue(repo.getDebeAjustado().subtract(repo.getHaberAjustado())
							   .add(BigDecimal.ZERO).doubleValue());   
				      
				   cell8.setCellStyle(styleMoney);
				
				   //saldoAcumulado = saldoAcumulado.add(repo.getDebeAjustado().subtract(repo.getHaberAjustado())).add(
				   //		saldoInicial);
				   
				   saldoAcumulado = saldoAcumulado.add(repo.getDebeAjustado().subtract(repo.getHaberAjustado())).add(
							BigDecimal.ZERO);
			}

			i++;
		}

		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Total"));
		cell0.setCellStyle(styleAllBold);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(debe.doubleValue());
		cellRaz.setCellStyle(styleMoneyBold);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(haber.doubleValue());
		cell4.setCellStyle(styleMoneyBold);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(saldo.doubleValue());
		cell5.setCellStyle(styleMoneyBold);
		
		if(incluir_ajuste_inflacion) {
		
		   HSSFCell cell6 = row.createCell(6);
		   cell6.setCellValue(debeAcumulado.doubleValue());
		   cell6.setCellStyle(styleMoneyBold);

		   HSSFCell cell7 = row.createCell(7);
		   cell7.setCellValue(haberAcumulado.doubleValue());
		   cell7.setCellStyle(styleMoneyBold);

		   HSSFCell cell8 = row.createCell(8);
		   cell8.setCellValue(saldoAcumulado .doubleValue());
		   cell8.setCellStyle(styleMoneyBold);
		
		}

		return i;
	}

	private static int crearHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader,boolean incluir_ajuste_inflacion) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Cuenta"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Descripción Cuenta"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("Saldo Anterior"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Debe"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Haber"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Saldo"));
		cell5.setCellStyle(styleHeader);
		
		if(incluir_ajuste_inflacion) {
			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(new HSSFRichTextString("Debe Ajustado"));
			cell6.setCellStyle(styleHeader);

			HSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(new HSSFRichTextString("Haber Ajustado"));
			cell7.setCellStyle(styleHeader);

			HSSFCell cell8 = row.createCell(8);
			cell8.setCellValue(new HSSFRichTextString("Saldo Ajustado"));
			cell8.setCellStyle(styleHeader);
			
		}

		return ++i;
	}
	
	public static void ajustarCuentas(List<BalanceSumasYSaldos> balanceSumasYSaldos,Date desdeC,Date hastaC,Date desdeEjercicio,
			Date hastaEjercicio,Set<String>filtroCuentas, int entidad) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Integer qDecimales =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONTABILIDAD_CANTIDAD_DECIMALES_AJUSTE_INFLACION"));
		
		List<Date>periodos=periodosComprendidos(desdeC,hastaC);
		Map<Date,List<Asiento>> asientosMap= new HashMap<Date,List<Asiento>>();
		
		for(Date d:periodos) {
			List<Asiento> asientos = AsientoServiceUtil.buscarDetalleAsientos(
					d, DateUtils.getLastDateOfMonth(d, false) , null, null,
					true, true, entidad);
			asientosMap.put(d, asientos);
		}	
		
		Integer dde = Integer.parseInt(sdf.format(periodos.get(0)));
		Integer hta = Integer.parseInt(sdf.format(periodos.get(periodos.size()-1)));
		
		Map<Integer,BigDecimal>coeficientes = ContabilidadServiceUtil.getCoeficientesAjustesInflacion(WebKeysGlobal.ENTIDADESUNIFICADAS,dde, hta);
		
//Coeficientes Ejercicio Anterior para ajustar Bs.Uso
		Calendar calAux = Calendar.getInstance();
		calAux.setTime(desdeC);
		calAux.add(Calendar.MONTH,-1);
		Integer anterior = Integer.parseInt(sdf.format(calAux.getTime()));
		Map<Integer,BigDecimal>coefEjAnt = ContabilidadServiceUtil.getCoeficientesAjustesInflacion(WebKeysGlobal.ENTIDADESUNIFICADAS,anterior, anterior);
//		
		for(BalanceSumasYSaldos b:balanceSumasYSaldos) {
			PlanCuentas cuenta=CuentaServiceUtil.getCuentaByNroCuenta(b.getNumeroCuenta(),desdeEjercicio,hastaEjercicio, entidad);
			BigDecimal debeAcumulado=BigDecimal.ZERO;
			BigDecimal haberAcumulado=BigDecimal.ZERO;
			
			BigDecimal debeAcumuladoEsp=BigDecimal.ZERO;
			BigDecimal haberAcumuladoEsp=BigDecimal.ZERO;
			
			BigDecimal origen=BigDecimal.ZERO;
			BigDecimal destino=BigDecimal.ZERO;
			
			BigDecimal coeficiente = BigDecimal.ONE;
			if(cuenta.getAjustaInflacion()){
				for(Date d:periodos) {
					for(Asiento a:asientosMap.get(d)) {
						if (filtroCuentas.size() > 0
								&& !filtroCuentas.contains(a.getDetalle().get(0).getCuenta().getNumero())) {
							continue;
						}
						if(cuenta.getNumero().equals(a.getDetalle().get(0).getCuenta().getNumero())) {
						  //Agregado para Bs.Uso que necesitan actualizar a inicio del ejercicio el saldo de cierre
						  //Para ello se toma el asiento de Apertura y se actualiza desde el cierre.
					
						  debeAcumuladoEsp=BigDecimal.ZERO;
						  haberAcumuladoEsp=BigDecimal.ZERO;
						  origen=BigDecimal.ZERO;
						  destino=BigDecimal.ZERO;
						  if(a.getNro()==1 && cuenta.isAjustaInflacionConPeriodoEjercicioAnterior()) {
							  origen =coefEjAnt.get(anterior);
							  destino=coeficientes.get(Integer.parseInt(sdf.format(hastaEjercicio)));
							  coeficiente = destino.divide(origen, qDecimales, RoundingMode.HALF_EVEN);
							 
						  }else {
						     try {
							    origen=coeficientes.get(Integer.parseInt(sdf.format(a.getFecha())));
							    destino=coeficientes.get(Integer.parseInt(sdf.format(hastaEjercicio)));
						        coeficiente= destino.divide(origen, qDecimales, RoundingMode.HALF_EVEN);
						        if(coeficiente==null) coeficiente=BigDecimal.ONE;
						      }catch(Exception e) {
							    coeficiente = BigDecimal.ONE;
						      }
						  }   
						  debeAcumulado=debeAcumulado.add(a.getTotalDebe().add(debeAcumuladoEsp).multiply(coeficiente));
						  haberAcumulado=haberAcumulado.add(a.getTotalHaber().add(haberAcumuladoEsp).multiply(coeficiente));
					    }
					}
				}
				b.setDebeAjustado(debeAcumulado);
				b.setHaberAjustado(haberAcumulado);
			}else {
				b.setDebeAjustado(b.getDebe()!=null?b.getDebe():BigDecimal.ZERO);
				b.setHaberAjustado(b.getHaber()!=null?b.getHaber():BigDecimal.ZERO);
			}
		}
	}
	
	private static List<Date> periodosComprendidos(Date desde,Date hasta){
		 List<Date> ret = new ArrayList<Date>();
		 Calendar start = Calendar.getInstance();
		 start.setTime(desde);
		 start.set(Calendar.DAY_OF_MONTH, 1);
		 
		 Calendar end = Calendar.getInstance();
		 end.setTime(hasta);
		 
		 Calendar current = Calendar.getInstance();

		 while (start.compareTo(end)<0){
			 Calendar aux = Calendar.getInstance();
			 aux.setTime(start.getTime());
			 aux.set(Calendar.DAY_OF_MONTH, 1);
			 if(!ret.contains(aux.getTime())) {
				 ret.add(start.getTime());
			 }
			 start.add(Calendar.DAY_OF_YEAR, 1);  
		 }
		
		return ret;
	}
	
}
