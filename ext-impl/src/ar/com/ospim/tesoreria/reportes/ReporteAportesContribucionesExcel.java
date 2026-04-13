package ar.com.ospim.tesoreria.reportes;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import  org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.afiliados.services.AporteServiceUtil;
import ar.com.ospim.afip.beans.ReporteAporteContribucionesEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceImpl;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteOPReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa.Detalle;
import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.util.PermissionUtil;
import ar.com.ospim.util.StringUtils;

public class ReporteAportesContribucionesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteOPReintegros.class);

	public static HSSFWorkbook generaReporteAportes(HttpServletRequest req,
			HttpServletResponse res) {

		HSSFWorkbook wb = new HSSFWorkbook();
		CellStyle styleDate = getStyleDate(wb);
		CellStyle styleAllWithHeader = getStyleAllWithBorder(wb);
		CellStyle styleBold = getStyleBold(wb);
		CellStyle styleHeaderWithBorder = getStyleHeaderWithBorder(wb);

		try {
					

			String cuil_titular = ParamUtil.getString(req, "cuil");

			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio = ParamUtil.getString(req,
					"periodoDesdeMesAnio");
			Date periodoDesde = null;
			try {
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeMesAnio.substring(0, 1))
						+ 1
						+ "/" + periodoDesdeMesAnio.substring(2, 6));
			} catch (Exception e) {
				periodoDesde = null;
			}
			if (periodoDesde == null) {
				periodoDesde = formatoDePeriodos.parse(Integer.parseInt("01")
						+ "/" + "1900");
			}

			List<AporteAfiliado> afiAportes = null;
			afiAportes = AporteServiceUtil.buscaAportesAfipAfiliado(
					cuil_titular, periodoDesde);

			HSSFSheet sheet = wb.createSheet("Hoja 1");
			sheet.setMargin(HSSFSheet.TopMargin, 0.8);
			addDefaultHeader(sheet);

			int index = 0;
			Collections.sort(afiAportes, new Comparator<AporteAfiliado>() {
				public int compare(AporteAfiliado o1, AporteAfiliado o2) {
					if (o1.getPeriodo().equals(o2.getPeriodo())) {
						return o1.getPeriodo().compareTo(o2.getPeriodo());
					} else {
						return o1.getPeriodo().compareTo(o2.getPeriodo());
					}
				}
			});

			crearHeaderReporteAportes(sheet, styleHeaderWithBorder);
			for (AporteAfiliado repo : afiAportes) {
				++index;
				crearInfoAportes(sheet, repo.getAfiliado().getCuil_titular(),
						repo.getAfiliado().getApellido(), repo.getAfiliado()
								.getNombre(), repo.getAfiliado()
								.getIngre_fecha(), repo.getAfiliado()
								.getBaja_fechaAsString(), repo.getEmpleador()
								.getCuit(), repo.getEmpleador().getRazon_soc(),
						repo.getPeriodo(), repo.getImporte(),
						repo.getContribucionEstimada(), repo.getLiqActas(),
						repo.getTotalLiqTercerizadora(),
						repo.getFechaLiqTercerizadoraString(), index,
						styleDate, styleAllWithHeader, styleBold);
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

		} catch (ParseException e) {
			_log.error("Error al generar reporte", e);
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return wb;
	}
	
	public static SXSSFWorkbook generaReporte(
			HttpServletRequest req, HttpServletResponse res) {
		boolean monotributistas = ParamUtil.getBoolean(req, "monotributistas");
		if(monotributistas){
			return generaReporteMonotributistas(req, res);
		}else{
			return generaReporteNominaEmpresa(req, res); 
		}
		
	}

	public static SXSSFWorkbook generaReporteNominaEmpresa(
			HttpServletRequest req, HttpServletResponse res) {

		int id_ramo = ParamUtil.getInteger(req, "id_ramo");
		int id_ramo_hasta = ParamUtil.getInteger(req, "id_ramo_hasta");
		String todas_empresas = ParamUtil.getString(req, "todas_empresas");

		if (todas_empresas.equals("true")) {
			id_ramo = 999999;
		}
		if (id_ramo != 0) {
			return generaReporteNominaEmpresas(req, res);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaPagoDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaPagoMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(req, "fechaHastaAnio");
		
		Calendar fechaAcreDesde=null;
		if(ParamUtil.getInteger(req, "fechaAcreDesdeMes")>=0 && ParamUtil.getInteger(req, "fechaAcreDesdeAnio")>0 ){
			fechaAcreDesde=Calendar.getInstance();
			fechaAcreDesde.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaAcreDesdeAnio"));
			fechaAcreDesde.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaAcreDesdeMes"));
			fechaAcreDesde.set(Calendar.DATE, ParamUtil.getInteger(req, "fechaAcreDesdeDia"));
		}
		
		Calendar fechaAcreHasta=null;
		if(ParamUtil.getInteger(req, "fechaAcreHastaMes")>=0 && ParamUtil.getInteger(req, "fechaAcreHastaAnio")>0 ){
			fechaAcreHasta=Calendar.getInstance();
			fechaAcreHasta.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaAcreHastaAnio"));
			fechaAcreHasta.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaAcreHastaMes"));
			fechaAcreHasta.set(Calendar.DATE, ParamUtil.getInteger(req, "fechaAcreHastaDia"));
		}

		String cuil = ParamUtil.getString(req, "cuil");
		int aleatorio = ParamUtil.getInteger(req, "aleatorio");
		String plano = ParamUtil.getString(req, "formato_procesar");
		boolean acta_conv = ParamUtil.getBoolean(req, "incluir_acta_conv");

		SXSSFWorkbook wb = new SXSSFWorkbook();
		CellStyle styleDate = getStyleDateWbs(wb);
		CellStyle styleDateWithBorder = getStyleDateWithBorderWbs(wb);
		CellStyle styleAll = getStyleAllWbs(wb);
		CellStyle styleBoldWithBorder = getStyleBoldWithBorderWbs(wb);
		CellStyle styleBold = getStyleBoldWbs(wb);
		CellStyle styleHeaderBorder = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleAllWithBorder = getStyleAllWithBorderWbs(wb);
		try {

			Date fechaIni;
			fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes + "-"
					+ fechaInicioAnio);
			Date fechaFin = format.parse(fechaPagoDia + "-" + fechaPagoMes
					+ "-" + fechaPagoAnio);

			String cuit = ParamUtil.getString(req, "cuit");
			List<Empresa> empresasRamo = null;
			if(id_ramo!=0 && id_ramo_hasta!=0){
				empresasRamo = EmpresaServiceImpl.getInstance()
						.getEmpresasPorRamo(id_ramo, id_ramo_hasta, fechaIni, fechaFin);
			}else if (id_ramo != 0) {
				empresasRamo = EmpresaServiceImpl.getInstance()
						.getEmpresasPorRamo(id_ramo, fechaIni, fechaFin);
			} else {
				empresasRamo = new ArrayList<Empresa>();
				empresasRamo.add(new Empresa(cuit));
			}
			List<ReporteAporteContribucionesEmpresa> reporte = null;
			int hoja = 1;
			int index = -1;
			int empresa = 0;
			Sheet sheet = wb.createSheet("Hoja 1");
			req.getSession().setAttribute("totalProgreso" + aleatorio,
					empresasRamo.size());
			
			index=crearInfoReporte(sheet, index, styleBold, fechaIni, fechaFin, null!=fechaAcreDesde?fechaAcreDesde.getTime():null, 
					null!=fechaAcreHasta?fechaAcreHasta.getTime():null, cuit, cuil, id_ramo, id_ramo_hasta, acta_conv);
			
			if (plano.trim().equals("true")) {
				index++;
				crearHeaderPlano(sheet, index, styleHeaderBorder, acta_conv);
				index++;
			}
			for (Empresa emp : empresasRamo) {
				empresa++;
				if (acta_conv) {
					reporte = AfipServiceUtil
							.getReporteAportesContribucionEmpresaActaConv(
									emp.getCuit(), cuil, fechaIni, fechaFin);
				} else {
					reporte = AfipServiceUtil
							.getReporteAportesContribucionEmpresa(
									emp.getCuit(), cuil, fechaIni, fechaFin, null!=fechaAcreDesde?fechaAcreDesde.getTime():null, 
											null!=fechaAcreHasta?fechaAcreHasta.getTime():null);
				}
				
//				Los aportes salen de informacion_afip.os_aportes_detalle y solo puedo filtrar por periodo, no se si hay diferentes tipos de aportes
				/*Regla para evitar que se vena aportes Ospim antes del 01/01/2013*/
				User user = null;
				boolean permiteVerAportesOOSSdesde2011 = false;
				try {
					user = PortalUtil.getUser(req);
					permiteVerAportesOOSSdesde2011 = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VER_APORTES_OSPIM);
				} catch (PortalException e) {
					_log.error(e);
				}  
		
				if(!permiteVerAportesOOSSdesde2011){

					Calendar fechaRestriccion = Calendar.getInstance();
					fechaRestriccion.set(Calendar.YEAR, 2013);
					fechaRestriccion.set(Calendar.MONTH, 0);
					fechaRestriccion.set(Calendar.DATE, 1);
					fechaRestriccion.set(Calendar.HOUR_OF_DAY, 0);
					fechaRestriccion.set(Calendar.MINUTE, 0);
					fechaRestriccion.set(Calendar.SECOND, 0);
					fechaRestriccion.set(Calendar.MILLISECOND, 0);
					
					List<ReporteAporteContribucionesEmpresa> auxAportesRestringidos = new ArrayList<ReporteAporteContribucionesEmpresa>();

					for (Iterator<ReporteAporteContribucionesEmpresa> iterator = reporte.iterator(); iterator.hasNext();) {
						
						ReporteAporteContribucionesEmpresa race = iterator.next();
						if(race.getPeriodo().getTime() < fechaRestriccion.getTimeInMillis()){ 
							auxAportesRestringidos.add(race);
						}
					}
					reporte.removeAll(auxAportesRestringidos);
				}
				/*fin regla*/
				
				if (reporte.size() > 0) {
					Collections
							.sort(reporte,
									new Comparator<ReporteAporteContribucionesEmpresa>() {
										public int compare(
												ReporteAporteContribucionesEmpresa o1,
												ReporteAporteContribucionesEmpresa o2) {
											if (o1.getPeriodo().equals(
													o2.getPeriodo())) {
												return o1
														.getCuilAportante()
														.compareTo(
																o2.getCuilAportante());
											} else {
												return o1
														.getPeriodo()
														.compareTo(
																o2.getPeriodo());
											}
										}
									});
					Date peri = null;

					if (plano.trim().equals("true")) {

					} else {
						index++;
						crearHeader(sheet, reporte.get(0)
								.getCuitContribuyente(), reporte.get(0)
								.getRazon(), index, styleDate, styleAll,
								styleBold);
					}
					for (ReporteAporteContribucionesEmpresa repo : reporte) {
						if (!plano.trim().equals("true")) {
							if (peri == null || !peri.equals(repo.getPeriodo())) {
								peri = repo.getPeriodo();
								index++;
								sheet.createRow(index);
								index++;
								addHeaderPeriodo(sheet, repo.getPeriodo(),
										repo.getCantidadAfiliadosDeclarados(),
										repo.getCantidadAfiliadosPagados(),
										index, styleDate, styleAll, styleBold);
								index++;
								crearHeaderInfo(sheet, index, styleDate,
										styleAll, styleBold, acta_conv);
							}
							index++;
						}
						if (repo != null) {
							if (!plano.trim().equals("true")) {
								crearInfo(sheet, repo, index, styleDate,
										styleAll, styleBold, acta_conv);
							} else {
								crearInfoPlano(sheet, repo, index,
										styleDateWithBorder,
										styleAllWithBorder,
										styleBoldWithBorder, acta_conv);
								index++;
							}
						}
						/*if (index >= 65000) {
							HSSFRow row = sheet.createRow(index);
							Cell cell = row.createCell(0);
							cell.setCellValue(new HSSFRichTextString(
									"CONTINUA EN LA HOJA: " + hoja));
							cell.setCellStyle(styleAll);
							hoja++;
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

							sheet = wb.createSheet("Hoja " + hoja + 1);
							index = -1;
						}*/

					}
					req.getSession().setAttribute("progreso" + aleatorio,
							empresa);
					if (!plano.trim().equals("true")) {
						index++;
					}
				}
				
				index++;
				sheet.createRow(index);
				for(int j=0;j<30;j++){
					try {
						sheet.autoSizeColumn((short) j);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}	

			}

		} catch (ParseException e) {
			_log.error("Error al generar reporte", e);
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		} catch (SQLException e) {
			_log.error("Error al generar reporte", e);
			e.printStackTrace();
		}

		return wb;
	}

	public static SXSSFWorkbook generaReporteNominaEmpresas(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");

		String fechaPagoMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);

		int id_ramo = ParamUtil.getInteger(req, "id_ramo");
		int id_ramo_hasta = ParamUtil.getInteger(req, "id_ramo_hasta");
		String todas_empresas = ParamUtil.getString(req, "todas_empresas");

		
		boolean acta_conv = ParamUtil.getBoolean(req, "incluir_acta_conv");

		SXSSFWorkbook wb = new SXSSFWorkbook();		
		CellStyle styleDateWithBorder = getStyleDateWithBorderWbs(wb);		
		CellStyle styleBoldWithBorder = getStyleBoldWithBorderWbs(wb);		
		CellStyle styleHeaderBorder = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleAllWithBorder = getStyleAllWithBorderWbs(wb);
		try {

			Date fechaIni;
			fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes + "-"
					+ fechaInicioAnio);

			if (todas_empresas.equals("true")) {
				id_ramo = 999999;
			}

			List<ReporteAporteContribucionesEmpresa> reporte = null;

			int index = -1;

			Sheet sheet = wb.createSheet("Hoja 1");

			index++;
			crearHeaderPlano(sheet, index, styleHeaderBorder, acta_conv);
			index++;

			reporte = AfipServiceUtil.getReporteAportesContribucionEmpresas(
					fechaIni, null, id_ramo, id_ramo_hasta, null);


			for (ReporteAporteContribucionesEmpresa repo : reporte) {
				crearInfoPlano(sheet, repo, index, styleDateWithBorder,
						styleAllWithBorder, styleBoldWithBorder, acta_conv);
				index++;
			}
			
			index++;
			sheet.createRow(index);
			for(int j=0;j<30;j++){
				try {
					sheet.autoSizeColumn((short) j);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}	

		} catch (ParseException e) {
			_log.error("Error al generar reporte", e);
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		}

		return wb;
	}
	
	
	public static SXSSFWorkbook generaReporteMonotributistas(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");

		String fechaPagoDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaPagoMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(req, "fechaHastaAnio");
		
		String cuil = ParamUtil.getString(req, "cuil");



	    SXSSFWorkbook wb = new SXSSFWorkbook(100);	//chache en disck
			
		
		CellStyle styleDateWithBorder = getStyleDateWithBorderWbs(wb);		
		CellStyle styleBold = getStyleBoldWbs(wb);
		CellStyle styleHeaderBorder = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleAllWithBorder = getStyleAllWithBorderWbs(wb);	
		CellStyle styleMoneyWithBorder = getStyleMoneyWithBorderWbs(wb);
		try {

			Date fechaIni;
			fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes + "-"
					+ fechaInicioAnio);
			
			Date fechaFin;
			fechaFin = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
					+ fechaPagoAnio);

			

			List<ReporteAporteContribucionesEmpresa> reporte = null;

			int index = -1;

			//HSSFSheet sheet = wb.createSheet("Hoja 1");
			Sheet sheet =  wb.createSheet("Hoja 1");

			index++;
			index=crearHeaderMonotributistas(sheet, index, styleBold, styleHeaderBorder, fechaIni, fechaFin);
			index++;

			reporte = AfipServiceUtil.getReporteMonotributistas(
					fechaIni, fechaFin, cuil);

//			Los aportes salen de informacion_afip.os_aportes_detalle y solo puedo filtrar por periodo, no se si hay diferentes tipos de aportes
			/*Regla para evitar que se vena aportes Ospim antes del 01/01/2013*/
			User user = null;
			boolean permiteVerAportesOOSSdesde2011 = false;
			try {
				user = PortalUtil.getUser(req);
				permiteVerAportesOOSSdesde2011 = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VER_APORTES_OSPIM);
			} catch (PortalException e) {
				_log.error(e);
			}  
	
			if(!permiteVerAportesOOSSdesde2011){

				Calendar fechaRestriccion = Calendar.getInstance();
				fechaRestriccion.set(Calendar.YEAR, 2013);
				fechaRestriccion.set(Calendar.MONTH, 0);
				fechaRestriccion.set(Calendar.DATE, 1);
				fechaRestriccion.set(Calendar.HOUR_OF_DAY, 0);
				fechaRestriccion.set(Calendar.MINUTE, 0);
				fechaRestriccion.set(Calendar.SECOND, 0);
				fechaRestriccion.set(Calendar.MILLISECOND, 0);
				
				List<ReporteAporteContribucionesEmpresa> auxAportesRestringidos = new ArrayList<ReporteAporteContribucionesEmpresa>();

				for (Iterator<ReporteAporteContribucionesEmpresa> iterator = reporte.iterator(); iterator.hasNext();) {
					
					ReporteAporteContribucionesEmpresa race = iterator.next();
					if(race.getPeriodo().getTime() < fechaRestriccion.getTimeInMillis()){ 
						auxAportesRestringidos.add(race);
					}
				}
				reporte.removeAll(auxAportesRestringidos);
			}
			/*fin regla*/

			for (ReporteAporteContribucionesEmpresa repo : reporte) {
				crearInfoMonotributistas(sheet, repo, index, styleDateWithBorder,
						styleAllWithBorder, styleMoneyWithBorder);
				index++;
			}

			index++;
			sheet.createRow(index);
			for(int j=0;j<30;j++){
				try {
					sheet.autoSizeColumn((short) j);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}	
			

		} catch (ParseException e) {
			_log.error("Error al generar reporte", e);
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		}

		return wb;
	}

	private static void crearInfo(Sheet sheet,
			ReporteAporteContribucionesEmpresa repo, int index,
			CellStyle styleDate, CellStyle styleAll,
			CellStyle styleBold, boolean acta_conv) {
		int colIndex = 0;
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Row row = sheet.createRow(index);
		Cell cell = row.createCell(colIndex++);
		cell.setCellValue(new HSSFRichTextString(repo.getCuilAportante()));
		cell.setCellStyle(styleAll);
		Cell cell1 = row.createCell(colIndex++);
		cell1.setCellValue(new HSSFRichTextString(repo.getApellido()));
		cell1.setCellStyle(styleAll);
		Cell cell2 = row.createCell(colIndex++);
		cell2.setCellValue(new HSSFRichTextString(repo.getNombre()));
		cell2.setCellStyle(styleAll);
		Cell cell3 = row.createCell(colIndex++);
		cell3.setCellValue(repo.getRemuneracionDeclarada().doubleValue());
		cell3.setCellStyle(styleAll);
		Cell cell4 = row.createCell(colIndex++);
		cell4.setCellValue(repo.getRemuneracionPagada().doubleValue());
		cell4.setCellStyle(styleAll);
		Cell cell5 = row.createCell(colIndex++);
		cell5.setCellValue(repo.getCalculado().doubleValue());
		cell5.setCellStyle(styleAll);
		Cell cell6 = row.createCell(colIndex++);
		cell6.setCellValue(repo.getPagado().doubleValue());
		cell6.setCellStyle(styleAll);
		
		Cell cell8 = row.createCell(colIndex++);
		cell8.setCellValue(new HSSFRichTextString(sdf.format( repo.getFechaPago())));
		cell8.setCellStyle(styleAll);
		
		if (acta_conv) {
			Cell cell121 = row.createCell(colIndex++);
			cell121.setCellValue(new HSSFRichTextString(null != repo
					.getDescripcion_ac_credito() ? repo
					.getDescripcion_ac_credito() : ""));
			cell121.setCellStyle(styleAll);
			Cell cell122 = row.createCell(colIndex++);
			cell122.setCellValue(null != repo.getImporte_ac_credito() ? repo
					.getImporte_ac_credito().doubleValue() : 0);
			cell122.setCellStyle(styleAll);
			Cell cell123 = row.createCell(colIndex++);
			cell123.setCellValue(new HSSFRichTextString(null != repo
					.getDescripcion_ac_debito() ? repo
					.getDescripcion_ac_debito() : ""));
			cell123.setCellStyle(styleAll);
			Cell cell124 = row.createCell(colIndex++);
			cell124.setCellValue(null != repo.getImporte_ac_debito() ? -repo
					.getImporte_ac_debito().doubleValue() : 0);
			cell124.setCellStyle(styleAll);
			Cell cell125 = row.createCell(colIndex++);
			cell125.setCellValue((repo.getImporte_ac_credito() != null ? repo
					.getImporte_ac_credito().doubleValue() : 0)
					+ (repo.getImporte_ac_debito() != null ? repo
							.getImporte_ac_debito().doubleValue() : 0));
			cell125.setCellStyle(styleAll);
		}
		Cell cell7 = row.createCell(colIndex++);
		cell7.setCellValue(new HSSFRichTextString(repo.getTercerizadora()));
		cell7.setCellStyle(styleAll);
		
		
	}
	
	private static void crearInfoMonotributistas(Sheet sheet,
			ReporteAporteContribucionesEmpresa repo, int index,
		    CellStyle styleDate, CellStyle styleAll,
			CellStyle styleMoney) {
		int colIndex = 0;
		Row row = sheet.createRow(index);
		Cell cell = row.createCell(colIndex++);
		cell.setCellValue(new HSSFRichTextString(repo.getCuilAportante()));
		cell.setCellStyle(styleAll);		
		Cell cell1 = row.createCell(colIndex++);
		cell1.setCellValue(repo.getAporte().doubleValue());
		cell1.setCellStyle(styleMoney);
		Cell cell2 = row.createCell(colIndex++);
		cell2.setCellValue(repo.getPeriodo());
		cell2.setCellStyle(styleDate);
		Cell cell3 = row.createCell(colIndex++);
		cell3.setCellValue(new HSSFRichTextString(repo.getTercerizadora()));
		cell3.setCellStyle(styleAll);
		Cell cell4 = row.createCell(colIndex++);
		cell4.setCellValue(new HSSFRichTextString(repo.getApellido()));
		cell4.setCellStyle(styleAll);
		Cell cell5 = row.createCell(colIndex++);
		cell5.setCellValue(new HSSFRichTextString(repo.getNombre()));
		cell5.setCellStyle(styleAll);
		Cell cell6 = row.createCell(colIndex++);
		cell6.setCellValue(new HSSFRichTextString(repo.getExistePadron()));
		cell6.setCellStyle(styleAll);
		Cell cell7 = row.createCell(colIndex++);
		cell7.setCellValue(repo.getIdSeccional());
		cell7.setCellStyle(styleAll);
		Cell cell8 = row.createCell(colIndex++);
		cell8.setCellValue(new HSSFRichTextString(repo.getSeccional()));
		cell8.setCellStyle(styleAll);
	}

	private static void crearInfoPlano(Sheet sheet,
			ReporteAporteContribucionesEmpresa repo, int index,
			CellStyle styleDate, CellStyle styleAll,
			CellStyle styleBold, boolean acta_conv) {
		int colIndex = 0;
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Row row = sheet.createRow(index);
		Cell cell = row.createCell(colIndex++);
		cell.setCellValue(repo.getPeriodo());
		cell.setCellStyle(styleDate);
		Cell cell1 = row.createCell(colIndex++);
		cell1.setCellValue(new HSSFRichTextString(repo.getCuitContribuyente()));
		cell1.setCellStyle(styleAll);
		Cell cell2 = row.createCell(colIndex++);
		cell2.setCellValue(new HSSFRichTextString(repo.getRazon()));
		cell2.setCellStyle(styleAll);
		Cell cell3 = row.createCell(colIndex++);
		cell3.setCellValue(repo.getCantidadAfiliadosDeclarados());
		cell3.setCellStyle(styleAll);
		Cell cell4 = row.createCell(colIndex++);
		cell4.setCellValue(repo.getCantidadAfiliadosPagados());
		cell4.setCellStyle(styleAll);
		Cell cell5 = row.createCell(colIndex++);
		cell5.setCellValue(new HSSFRichTextString(repo.getCuilAportante()));
		cell5.setCellStyle(styleAll);
		Cell cell6 = row.createCell(colIndex++);
		cell6.setCellValue(new HSSFRichTextString(repo.getApellido()));
		cell6.setCellStyle(styleAll);
		Cell cell7 = row.createCell(colIndex++);
		cell7.setCellValue(new HSSFRichTextString(repo.getNombre()));
		cell7.setCellStyle(styleAll);
		Cell cell8 = row.createCell(colIndex++);
		cell8.setCellValue(repo.getRemuneracionDeclarada().doubleValue());
		cell8.setCellStyle(styleAll);
		Cell cell9 = row.createCell(colIndex++);
		cell9.setCellValue(repo.getRemuneracionPagada().doubleValue());
		cell9.setCellStyle(styleAll);
		Cell cell10 = row.createCell(colIndex++);
		cell10.setCellValue(repo.getCalculado().doubleValue());
		cell10.setCellStyle(styleAll);
		Cell cell101 = row.createCell(colIndex++);
		cell101.setCellValue(repo.getAporte()!=null?repo.getAporte().doubleValue():0.00);
		cell101.setCellStyle(styleAll);
		Cell cell102 = row.createCell(colIndex++);
		cell102.setCellValue(repo.getContribucion()!=null?repo.getContribucion().doubleValue():0.00);
		cell102.setCellStyle(styleAll);
		Cell cell11 = row.createCell(colIndex++);		
		cell11.setCellValue(repo.getPagado().doubleValue());
		cell11.setCellStyle(styleAll);
		
		Cell cell80 = row.createCell(colIndex++);
		if(repo.getFechaPago()!=null) {
		  cell80.setCellValue(new HSSFRichTextString(sdf.format( repo.getFechaPago())));
		  cell80.setCellStyle(styleAll);
		}  
		
		if (acta_conv) {

			Cell cell121 = row.createCell(colIndex++);
			cell121.setCellValue(new HSSFRichTextString(repo
					.getDescripcion_ac_credito()));
			cell121.setCellStyle(styleAll);
			Cell cell122 = row.createCell(colIndex++);
			cell122.setCellValue(repo.getImporte_ac_credito() != null ? repo
					.getImporte_ac_credito().doubleValue() : 0);
			cell122.setCellStyle(styleAll);
			Cell cell123 = row.createCell(colIndex++);
			cell123.setCellValue(new HSSFRichTextString(repo
					.getDescripcion_ac_debito()));
			cell123.setCellStyle(styleAll);
			Cell cell124 = row.createCell(colIndex++);
			cell124.setCellValue(repo.getImporte_ac_debito() != null ? -repo
					.getImporte_ac_debito().doubleValue() : 0);
			cell124.setCellStyle(styleAll);
			Cell cell125 = row.createCell(colIndex++);
			cell125.setCellValue((repo.getImporte_ac_credito() != null ? repo
					.getImporte_ac_credito().doubleValue() : 0)
					+ (repo.getImporte_ac_debito() != null ? repo
							.getImporte_ac_debito().doubleValue() : 0));
			cell125.setCellStyle(styleAll);
		}
		Cell cell12 = row.createCell(colIndex++);
		cell12.setCellValue(new HSSFRichTextString(repo.getTercerizadora()));
		cell12.setCellStyle(styleAll);
		
		Cell cell13 = row.createCell(colIndex++);
		cell13.setCellValue(repo.getRamo());
		cell13.setCellStyle(styleAll);
		
		Cell cell14 = row.createCell(colIndex++);
		cell14.setCellValue(repo.getIdSeccional());
		cell14.setCellStyle(styleAll);
		
		Cell cell15 = row.createCell(colIndex++);
		cell15.setCellValue(new HSSFRichTextString(repo.getSeccional()));
		cell15.setCellStyle(styleAll);
	}

	private static void crearInfoAportes(HSSFSheet sheet, String cuil,
			String ape, String nombre, Date altaFecha, String bajaFecha,
			String cuit, String razonSoc, Date periodo, BigDecimal aporte,
			BigDecimal contrib, BigDecimal actas, BigDecimal totalTerc,
			String fechaLiqTerce, int index, CellStyle styleDate,
			CellStyle styleAll, CellStyle styleBold) {

		HSSFRow row = sheet.createRow(index);
		Cell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(cuil));
		cell.setCellStyle(styleAll);
		Cell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(ape + ", " + nombre));
		cell1.setCellStyle(styleAll);
		Cell cell2 = row.createCell(2);
		cell2.setCellValue(altaFecha);
		cell2.setCellStyle(styleAll);
		Cell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(bajaFecha));
		cell3.setCellStyle(styleAll);
		Cell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(cuit));
		cell4.setCellStyle(styleAll);
		Cell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(razonSoc));
		cell5.setCellStyle(styleAll);
		Cell cell6 = row.createCell(6);
		cell6.setCellValue(periodo);
		cell6.setCellStyle(styleAll);
		Cell cell7 = row.createCell(7);
		cell7.setCellValue(aporte.doubleValue());
		cell7.setCellStyle(styleAll);
		Cell cell8 = row.createCell(8);
		cell8.setCellValue(contrib.doubleValue());
		cell8.setCellStyle(styleAll);
		Cell cell9 = row.createCell(9);
		cell9.setCellValue(actas.doubleValue());
		cell9.setCellStyle(styleAll);
		Cell cell10 = row.createCell(10);
		cell10.setCellValue(null != totalTerc ? totalTerc.doubleValue() : 0);
		cell10.setCellStyle(styleAll);
		Cell cell11 = row.createCell(11);
		cell11.setCellValue(new HSSFRichTextString(fechaLiqTerce));
		cell11.setCellStyle(styleAll);
	}
	

	private static HSSFRow crearInfoParaActa(HSSFSheet sheet, Acta acta, ActaPeriodoDeudaEmpresa repo, int index, Detalle detalle,
			CellStyle styleDate, CellStyle styleAll,
			CellStyle styleBold, boolean conTotales, boolean incluirFechaPago, HttpServletRequest request) {
		

		HSSFRow row = sheet.createRow(index);
		Cell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(repo.getCuil()));
		cell.setCellStyle(styleAll);
		Cell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(repo.getApellido()));
		cell1.setCellStyle(styleAll);
		Cell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(repo.getNombre()));
		cell2.setCellStyle(styleAll);
		Cell cell3 = row.createCell(3);
		cell3.setCellValue(repo.getRemuneracionDeclarada().doubleValue());
		cell3.setCellStyle(styleAll);
		Cell cell4 = row.createCell(4);
		cell4.setCellValue(repo.getCalculado().doubleValue());
		cell4.setCellStyle(styleAll);
		Cell cell5 = row.createCell(5);
		cell5.setCellValue(repo.getMontoPagadoTotal().doubleValue());
		cell5.setCellStyle(styleAll);
		
		int cont=6;
		if(incluirFechaPago){
			
			Cell cell61 = row.createCell(cont++);
			Date vtoOriginal=AfipServiceUtil
			.getVencimientoOriginalAFIP(acta.getEmpresa().getCuit(), repo.getPeriodo(), request);
			cell61.setCellValue(vtoOriginal);
			cell61.setCellStyle(styleDate);
			Cell cell62 = row.createCell(cont++);
			Date fechaPagado=vtoOriginal;
			if(null==detalle.getFechaPagado()){
				cell62.setCellValue(new HSSFRichTextString(""));				
			}else{
				cell62.setCellValue(detalle.getFechaPagado());
				fechaPagado=detalle.getFechaPagado();
			}
			int diasCalcu=AfipServiceUtil.obtenerDiasAFIPParaInteres(fechaPagado, acta.getFechaPago());
			cell62.setCellStyle(styleDate);
			Cell cell63 = row.createCell(cont++);
			cell63.setCellValue(diasCalcu);
			cell63.setCellStyle(styleBold);
			
		}
		if(conTotales){
			Cell cell6 = row.createCell(cont++);
			cell6.setCellValue(detalle.getCapital().doubleValue());
			cell6.setCellStyle(styleAll);
			Cell cell7 = row.createCell(cont++);
			cell7.setCellValue(detalle.getInteres().doubleValue());
			cell7.setCellStyle(styleAll);
			Cell cell8 = row.createCell(cont++);
			cell8.setCellValue(detalle.getCapital().add(detalle.getInteres()).doubleValue());
			cell8.setCellStyle(styleAll);
		}
		return row;
	}
	

	private static void addHeaderPeriodo(Sheet sheet, Date periodo,
			int cantAfilDecl, int cantAfilPag, int index,
			CellStyle styleDate, CellStyle styleAll,
			CellStyle styleBold) {
		Row row = sheet.createRow(index);
		Cell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Periodo"));
		cell.setCellStyle(styleBold);
		Cell cell1 = row.createCell(1);
		cell1.setCellValue(periodo);
		Cell cell2 = row.createCell(2);
		cell1.setCellStyle(styleDate);
		cell2.setCellValue(new HSSFRichTextString("Cant. Afiliados Declarados"));
		cell2.setCellStyle(styleBold);
		Cell cell3 = row.createCell(3);
		cell3.setCellValue(cantAfilDecl);
		cell3.setCellStyle(styleAll);
		Cell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Cant. Afiliados Pagados"));
		cell4.setCellStyle(styleBold);
		Cell cell5 = row.createCell(5);
		cell5.setCellValue(cantAfilPag);
		cell5.setCellStyle(styleAll);
	}

	private static void crearHeaderInfoParaActa(HSSFSheet sheet, int index,
			boolean conTotales, CellStyle styleDate,
			CellStyle styleAll, CellStyle styleBold, boolean incluirFechaPago) {

		HSSFRow row = sheet.createRow(index);
		int cont=0;
		Cell cell = row.createCell(cont++);
		cell.setCellValue(new HSSFRichTextString("Cuil Aportante"));
		cell.setCellStyle(styleBold);
		Cell cell1 = row.createCell(cont++);
		cell1.setCellValue(new HSSFRichTextString("Apellido"));
		cell1.setCellStyle(styleBold);
		Cell cell2 = row.createCell(cont++);
		cell2.setCellStyle(styleBold);
		cell2.setCellValue(new HSSFRichTextString("Nombre"));
		Cell cell3 = row.createCell(cont++);
		cell3.setCellValue(new HSSFRichTextString("Rem. Declarada"));
		cell3.setCellStyle(styleBold);
		Cell cell4 = row.createCell(cont++);
		cell4.setCellValue(new HSSFRichTextString("Calculado sin interes"));
		cell4.setCellStyle(styleBold);
		Cell cell5 = row.createCell(cont++);
		cell5.setCellValue(new HSSFRichTextString("Pagado"));
		cell5.setCellStyle(styleBold);
		if(incluirFechaPago){
			Cell cell61 = row.createCell(cont++);
			cell61.setCellValue(new HSSFRichTextString("Fecha Obligación"));
			cell61.setCellStyle(styleBold);
			Cell cell62 = row.createCell(cont++);
			cell62.setCellValue(new HSSFRichTextString("Fecha Pago"));
			cell62.setCellStyle(styleBold);
			Cell cell63 = row.createCell(cont++);
			cell63.setCellValue(new HSSFRichTextString("Días Calculados para Interes"));
			cell63.setCellStyle(styleBold);
			
		}
		if (conTotales) {
			Cell cell6 = row.createCell(cont++);
			cell6.setCellValue(new HSSFRichTextString("Subtotal"));
			cell6.setCellStyle(styleBold);
			Cell cell7 = row.createCell(cont++);
			cell7.setCellValue(new HSSFRichTextString("Interes"));
			cell7.setCellStyle(styleBold);
			Cell cell8 = row.createCell(cont++);
			cell8.setCellValue(new HSSFRichTextString("Total"));
			cell8.setCellStyle(styleBold);
		}

	}
	
	private static int crearInfoReporte(Sheet sheet, int index, CellStyle styleBold, Date fechaIni, Date fechaFin, Date fechaAcreDesde, Date fechaAcreHasta, 
			String cuit, String cuil, int id_ramo, int id_ramo_hasta, boolean acta_conv){
		SimpleDateFormat sdfHora=new SimpleDateFormat("dd/MM/yyyy hh:mm");
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy= new Date();
		Row row = sheet.createRow(++index);
		int colIndex = 0;
		Cell cell = row.createCell(colIndex);
		cell.setCellValue(new HSSFRichTextString("Reporte de Aportes y Contribuciones por Empresa al "+sdfHora.format(hoy)));
		cell.setCellStyle(styleBold);
		index++;
		
		StringBuffer parametros=new StringBuffer("Fecha desde: ");
		parametros.append(sdf.format(fechaIni)).append(" Fecha hasta: ").append(sdf.format(fechaFin));
		if(null!=fechaAcreDesde){
		parametros.append(" Fecha Acre. Desde: ").append(sdf.format(fechaAcreDesde));
		}
		if(null!=fechaAcreHasta){
		parametros.append(" Fecha Acre. Hasta:  ").append(sdf.format(fechaAcreHasta));
		}
		if(id_ramo>0){
		parametros.append(" Ramo Desde: ").append(id_ramo);
		}
		if(id_ramo_hasta>0){
			parametros.append(" Ramo hasta" );
			parametros.append(id_ramo_hasta);
		}
		if(cuit!=null && cuit.trim().length()>0){
			parametros.append(" CUIT: ").append(cuit);
		}
		if(cuil!=null && cuil.trim().length()>0){
			parametros.append(" CUIL: ").append(cuil);
		}
		if(acta_conv){
			parametros.append(" Con actas y conv. ");
		}		
		
		Cell cell1 = row.createCell(colIndex);
		cell1.setCellValue(new HSSFRichTextString(parametros.toString()));
		cell1.setCellStyle(styleBold);
		return ++index;
	}

	private static void crearHeaderInfo(Sheet sheet, int index,
			CellStyle styleDate, CellStyle styleAll,
			CellStyle styleBold, boolean acta_conv) {

		Row row = sheet.createRow(index);
		int colIndex = 0;
		Cell cell = row.createCell(colIndex++);
		cell.setCellValue(new HSSFRichTextString("Cuil Aportante"));
		cell.setCellStyle(styleBold);
		Cell cell1 = row.createCell(colIndex++);
		cell1.setCellValue(new HSSFRichTextString("Apellido"));
		cell1.setCellStyle(styleBold);
		Cell cell2 = row.createCell(colIndex++);
		cell2.setCellValue(new HSSFRichTextString("Nombre"));
		cell2.setCellStyle(styleBold);
		Cell cell3 = row.createCell(colIndex++);
		cell3.setCellValue(new HSSFRichTextString("Rem. Declarada"));
		cell3.setCellStyle(styleBold);
		Cell cell4 = row.createCell(colIndex++);
		cell4.setCellValue(new HSSFRichTextString("Rem. Pagada"));
		cell4.setCellStyle(styleBold);
		Cell cell5 = row.createCell(colIndex++);
		cell5.setCellValue(new HSSFRichTextString("Calculado"));
		cell5.setCellStyle(styleBold);
		Cell cell51 = row.createCell(colIndex++);
		cell51.setCellValue(new HSSFRichTextString("Aporte"));
		cell51.setCellStyle(styleBold);
		Cell cell52 = row.createCell(colIndex++);
		cell52.setCellValue(new HSSFRichTextString("Contribución"));
		cell52.setCellStyle(styleBold);
		Cell cell6 = row.createCell(colIndex++);
		cell6.setCellValue(new HSSFRichTextString("Pagado"));
		cell6.setCellStyle(styleBold);
		
		Cell cell8 = row.createCell(colIndex++);
		cell8.setCellValue(new HSSFRichTextString("Fecha Recaudación"));
		cell8.setCellStyle(styleBold);
		
		if (acta_conv) {
			Cell cell121 = row.createCell(colIndex++);
			cell121.setCellValue(new HSSFRichTextString(
					"Observ. Acta y Conv. Cred."));
			cell121.setCellStyle(styleBold);
			Cell cell122 = row.createCell(colIndex++);
			cell122.setCellValue(new HSSFRichTextString(
					"Importe Cred. Acta y Conv."));
			cell122.setCellStyle(styleBold);
			Cell cell123 = row.createCell(colIndex++);
			cell123.setCellValue(new HSSFRichTextString(
					"Observ. Acta y Conv. Deb."));
			cell123.setCellStyle(styleBold);
			Cell cell124 = row.createCell(colIndex++);
			cell124.setCellValue(new HSSFRichTextString(
					"Importe Deb. Acta y Conv."));
			cell124.setCellStyle(styleBold);
			Cell cell125 = row.createCell(colIndex++);
			cell125.setCellValue(new HSSFRichTextString(
					"Saldo Deb. Acta y Conv."));
			cell125.setCellStyle(styleBold);
		}
		Cell cell7 = row.createCell(colIndex++);
		cell7.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell7.setCellStyle(styleBold);
		
		
	}

	private static void crearHeader(Sheet sheet, String cuit, String razon,
			int index, CellStyle styleDate, CellStyle styleAll,
			CellStyle styleBold) {
		Row row = sheet.createRow(index);
		Cell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Cuit Contribuyente"));
		cell.setCellStyle(styleBold);
		Cell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(cuit));
		cell1.setCellStyle(styleAll);
		Cell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Razon Social"));
		cell2.setCellStyle(styleBold);
		Cell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(razon));
		cell3.setCellStyle(styleAll);
		sheet.addMergedRegion(new CellRangeAddress(index, index, 3, 5));
	}
	
	private static int crearHeaderMonotributistas(Sheet sheet, int index, CellStyle styleAll, CellStyle styleBold,Date desde, Date hasta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHoy=new SimpleDateFormat("dd/MM/yyyy mm:ss");
		Row rowTitulo = sheet.createRow(index);
		Cell cellTitulo=rowTitulo.createCell(0);
		cellTitulo.setCellValue(new HSSFRichTextString("Reporte de aportes de monotributistas desde "+sdf.format(desde)+" al "+sdf.format(hasta)+" - Fecha del listado"+sdfHoy.format(new Date(System.currentTimeMillis()))));
		cellTitulo.setCellStyle(styleAll);
		
		index++;
		
		Row row = sheet.createRow(++index);
		Cell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Cuil Aportante"));
		cell.setCellStyle(styleBold);
		Cell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Importe"));
		cell1.setCellStyle(styleBold);
		Cell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Periodo"));
		cell2.setCellStyle(styleBold);
		Cell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell3.setCellStyle(styleBold);
		Cell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Apellido"));
		cell4.setCellStyle(styleBold);
		Cell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Nombre"));
		cell5.setCellStyle(styleBold);
		Cell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Existe en Padrón"));
		cell6.setCellStyle(styleBold);		
		Cell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Id Seccional"));
		cell7.setCellStyle(styleBold);
		Cell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Seccional"));
		cell8.setCellStyle(styleBold);	
		return index;
	}

	private static void crearHeaderPlano(Sheet sheet, int index,
			CellStyle styleBold, boolean acta_conv) {
		Row row = sheet.createRow(index);
		int columnIndex = 0;
		Cell cell0 = row.createCell(columnIndex++);
		cell0.setCellValue(new HSSFRichTextString("Período"));
		cell0.setCellStyle(styleBold);
		Cell cell = row.createCell(columnIndex++);
		cell.setCellValue(new HSSFRichTextString("Cuit Contribuyente"));
		cell.setCellStyle(styleBold);
		Cell cell2 = row.createCell(columnIndex++);
		cell2.setCellValue(new HSSFRichTextString("Razon Social"));
		cell2.setCellStyle(styleBold);
		Cell cell3 = row.createCell(columnIndex++);
		cell3.setCellValue(new HSSFRichTextString("Cant. Afiliados Declarados"));
		cell3.setCellStyle(styleBold);
		Cell cell5 = row.createCell(columnIndex++);
		cell5.setCellValue(new HSSFRichTextString("Cant. Afiliados Pagados"));
		cell5.setCellStyle(styleBold);
		Cell cell6 = row.createCell(columnIndex++);
		cell6.setCellValue(new HSSFRichTextString("Cuil Aportante"));
		cell6.setCellStyle(styleBold);
		Cell cell7 = row.createCell(columnIndex++);
		cell7.setCellValue(new HSSFRichTextString("Apellido"));
		cell7.setCellStyle(styleBold);
		Cell cell8 = row.createCell(columnIndex++);
		cell8.setCellValue(new HSSFRichTextString("Nombre"));
		cell8.setCellStyle(styleBold);
		Cell cell9 = row.createCell(columnIndex++);
		cell9.setCellValue(new HSSFRichTextString("Rem. Declarada"));
		cell9.setCellStyle(styleBold);
		Cell cell10 = row.createCell(columnIndex++);
		cell10.setCellValue(new HSSFRichTextString("Rem. Pagada"));
		cell10.setCellStyle(styleBold);
		Cell cell11 = row.createCell(columnIndex++);
		cell11.setCellValue(new HSSFRichTextString("Calculado"));
		cell11.setCellStyle(styleBold);
		Cell cell111 = row.createCell(columnIndex++);
		cell111.setCellValue(new HSSFRichTextString("Aporte"));
		cell111.setCellStyle(styleBold);
		Cell cell112 = row.createCell(columnIndex++);
		cell112.setCellValue(new HSSFRichTextString("Contribución"));
		cell112.setCellStyle(styleBold);
		Cell cell12 = row.createCell(columnIndex++);
		cell12.setCellValue(new HSSFRichTextString("Pagado"));
		cell12.setCellStyle(styleBold);
		
		Cell cell126 = row.createCell(columnIndex++);
		cell126.setCellValue(new HSSFRichTextString("Fecha Recaudación"));
		cell126.setCellStyle(styleBold);
		
		if (acta_conv) {
			Cell cell121 = row.createCell(columnIndex++);
			cell121.setCellValue(new HSSFRichTextString(
					"Observ. Acta y Conv. Cred."));
			cell121.setCellStyle(styleBold);
			Cell cell122 = row.createCell(columnIndex++);
			cell122.setCellValue(new HSSFRichTextString(
					"Importe Cred. Acta y Conv."));
			cell122.setCellStyle(styleBold);
			Cell cell123 = row.createCell(columnIndex++);
			cell123.setCellValue(new HSSFRichTextString(
					"Observ. Acta y Conv. Deb."));
			cell123.setCellStyle(styleBold);
			Cell cell124 = row.createCell(columnIndex++);
			cell124.setCellValue(new HSSFRichTextString(
					"Importe Deb. Acta y Conv."));
			cell124.setCellStyle(styleBold);
			Cell cell125 = row.createCell(columnIndex++);
			cell125.setCellValue(new HSSFRichTextString(
					"Saldo Deb. Acta y Conv."));
			cell125.setCellStyle(styleBold);
		}

		Cell cell13 = row.createCell(columnIndex++);
		cell13.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell13.setCellStyle(styleBold);
		
		Cell cell14 = row.createCell(columnIndex++);
		cell14.setCellValue(new HSSFRichTextString("Ramo"));
		cell14.setCellStyle(styleBold);
		
		Cell cell41 = row.createCell(columnIndex++);
		cell41.setCellValue(new HSSFRichTextString("Id Seccional"));
		cell41.setCellStyle(styleBold);
		
		Cell cell42 = row.createCell(columnIndex++);
		cell42.setCellValue(new HSSFRichTextString("Seccional"));
		cell42.setCellStyle(styleBold);
		
		
	}

	private static void crearHeaderReporteAportes(HSSFSheet sheet,
			CellStyle styleHeader) {
		HSSFRow row = sheet.createRow(0);
		int columnIndex = 0;
		Cell cell = row.createCell(columnIndex++);

		cell.setCellValue(new HSSFRichTextString("CUIL"));
		cell.setCellStyle(styleHeader);
		Cell cell1 = row.createCell(columnIndex++);
		cell1.setCellValue(new HSSFRichTextString("Nombre"));
		cell1.setCellStyle(styleHeader);
		Cell cell2 = row.createCell(columnIndex++);
		cell2.setCellValue(new HSSFRichTextString("Ultima Alta Afiliado"));
		cell2.setCellStyle(styleHeader);
		Cell cell3 = row.createCell(columnIndex++);
		cell3.setCellValue(new HSSFRichTextString("Fecha Baja"));
		cell3.setCellStyle(styleHeader);
		Cell cell4 = row.createCell(columnIndex++);
		cell4.setCellValue(new HSSFRichTextString("CUIT"));
		cell4.setCellStyle(styleHeader);
		Cell cell5 = row.createCell(columnIndex++);
		cell5.setCellValue(new HSSFRichTextString("Razón Social"));
		cell5.setCellStyle(styleHeader);
		Cell cell6 = row.createCell(columnIndex++);
		cell6.setCellValue(new HSSFRichTextString("Período"));
		cell6.setCellStyle(styleHeader);
		Cell cell7 = row.createCell(columnIndex++);
		cell7.setCellValue(new HSSFRichTextString("Aporte Estimado"));
		cell7.setCellStyle(styleHeader);
		Cell cell8 = row.createCell(columnIndex++);
		cell8.setCellValue(new HSSFRichTextString("Contrib. Estimada"));
		cell8.setCellStyle(styleHeader);
		Cell cell9 = row.createCell(columnIndex++);
		cell9.setCellValue(new HSSFRichTextString("Pago por Actas"));
		cell9.setCellStyle(styleHeader);
		Cell cell10 = row.createCell(columnIndex++);
		cell10.setCellValue(new HSSFRichTextString("Total Liq. a Terc."));
		cell10.setCellStyle(styleHeader);
		Cell cell11 = row.createCell(columnIndex++);
		cell11.setCellValue(new HSSFRichTextString("Fecha Liq a Tercerizadora"));
		cell11.setCellStyle(styleHeader);
	}

	public static HSSFWorkbook generaReporteNominaEmpresaFromActa(
			HttpServletRequest req, HttpServletResponse res) {

		HSSFWorkbook wb = new HSSFWorkbook();
		CellStyle styleDate = getStyleDate(wb,11);
		CellStyle styleAll = getStyleAll(wb,11);
		CellStyle styleBold = getStyleBold(wb,11);
		
		boolean incluirFechaPago=ParamUtil.getBoolean(req, "incluirFechaPago");
		
		Acta acta = (Acta) req.getSession().getAttribute(
				WebKeysTesoreria.ACTA_EN_EDICION);
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		if (acta == null) {
			return wb;
		}

		String nro = ParamUtil.getString(req, "acta_numero", "0");
		String otrosStr = ParamUtil.getString(req, "otros", "0");
		String subtotalStr = ParamUtil.getString(req, "subtotal", "0");
		String inteStr = ParamUtil.getString(req, "inte", "0");
		BigDecimal otros = new BigDecimal(otrosStr.equals("") ? "0" : otrosStr);
		BigDecimal subtotal = new BigDecimal(subtotalStr.equals("") ? "0"
				: subtotalStr);
		BigDecimal inte = new BigDecimal(inteStr.equals("") ? "0" : inteStr);
		acta.setOtros(otros);
		acta.setCapital(subtotal);
		acta.setInteres(inte);
		acta.setNumero(nro);
		String totales = req.getParameter("totales");
		boolean conTotales = false;
		if (StringUtils.checkNotEmpty(totales) && totales.equals("totales")) {
			conTotales = true;
		}
		int index = -1;

		if (conTotales && acta.getNumero() != null) {
			String obligD = req.getParameter("fechaObligDia");
			String obligM = req.getParameter("fechaObligMes");
			obligM = String.valueOf(Integer.valueOf(obligM) + 1);
			String obligA = req.getParameter("fechaObligAnio");
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			try {
				acta.setFechaPago(format.parse(obligD + "-" + obligM + "-"
						+ obligA));
			} catch (ParseException e) {
				acta.setFechaPago(null);
			}

			index++;
			
			HSSFRow rowOSPIM = sheet.createRow(index++);
			Cell cellActaNro00 = rowOSPIM.createCell(0);
			cellActaNro00.setCellValue(new HSSFRichTextString(
					"OBRA SOCIAL DEL PERSONAL DE LA INDUSTRIA MOLINERA"));
			cellActaNro00.setCellStyle(styleBold);
			
			HSSFRow rowPlanilla = sheet.createRow(index);
			Cell cell00 = rowPlanilla.createCell(0);
			cell00.setCellValue(new HSSFRichTextString(
					"Planilla de determinación de deuda. Res.108/96"));
			cell00.setCellStyle(styleBold);
			
			String inspectores="";
			for(Inspector i:acta.getInspectoresFirmantes()) {
				inspectores += i.getNombre()+ " - ";
			}
			
			if(inspectores.length()>0) {
			   index++;	
			   HSSFRow rowInspectores = sheet.createRow(index);
			   Cell cell000 = rowInspectores.createCell(0);
			   cell000.setCellValue(new HSSFRichTextString(
					"Inspectores Firmantes "+ inspectores));
			   cell000.setCellStyle(styleBold);
			}
			index = index + 2;
			
			
			HSSFRow rowActaNro = sheet.createRow(index);
			Cell cellActaNro0 = rowActaNro.createCell(0);
			cellActaNro0.setCellValue(new HSSFRichTextString("Acta N°"));
			cellActaNro0.setCellStyle(styleBold);
			Cell cellActaNro1 = rowActaNro.createCell(1);
			cellActaNro1.setCellValue(new HSSFRichTextString(acta.getNumero()));
			cellActaNro1.setCellStyle(styleAll);
			index++;
			HSSFRow rowFechaPago = sheet.createRow(index);
			Cell cell0 = rowFechaPago.createCell(0);
			cell0.setCellValue(new HSSFRichTextString("Fecha Obligación"));
			cell0.setCellStyle(styleBold);
			Cell cell1 = rowFechaPago.createCell(1);
			cell1.setCellValue(acta.getFechaPago());
			cell1.setCellStyle(styleDate);
			index++;
		}

		List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();

		if (acta.getEmpresa() == null) {
			String cuit = ParamUtil.getString(req, "cuit");
			String desc = ParamUtil.getString(req, "desc");
			acta.setEmpresa(new Empresa(cuit, "", desc));
		}

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		Date peri = null;
		index++;
		crearHeader(sheet, acta.getEmpresa().getCuit(), acta.getEmpresa()
				.getRazon_soc(), index, styleDate, styleAll, styleBold);
		for (ActaPeriodoDeudaEmpresa repo : peris) {
			if (repo.isBorradoLogico()) {
				continue;
			}
			if (peri == null || !peri.equals(repo.getPeriodo())) {
				peri = repo.getPeriodo();
				index++;
				sheet.createRow(index);
				index++;
				addHeaderPeriodo(sheet, repo.getPeriodo(),
						getCantAfilDeclarados(peris, peri),
						getCantAfilPagados(peris, peri), index, styleDate,
						styleAll, styleBold);
				index++;
				crearHeaderInfoParaActa(sheet, index, conTotales, styleDate,
						styleAll, styleBold, incluirFechaPago);
			}
			for (Detalle detalle : repo.getDetalle()) {
				if (repo.getDetalle().size() > 1
						&& detalle.getCapital().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				index++;
				if (conTotales) {
					crearInfoParaActa(sheet, acta, repo,
							index, detalle,
							styleDate, styleAll, styleBold, conTotales, incluirFechaPago, req);
				}
			}
		}

		if (conTotales) {
			index++;
			sheet.createRow(index);
			index++;
			sheet.createRow(index);
			index++;
			HSSFRow rowSubtotal = sheet.createRow(index);
			Cell cellSubt0 = rowSubtotal.createCell(0);
			cellSubt0.setCellValue(new HSSFRichTextString("Subtotal"));
			cellSubt0.setCellStyle(styleBold);
			Cell cellSubt1 = rowSubtotal.createCell(1);
			cellSubt1.setCellValue(acta.getCapital().doubleValue());
			cellSubt1.setCellStyle(styleAll);
			index++;
			HSSFRow rowDeudaActas = sheet.createRow(index);
			Cell cellDeud0 = rowDeudaActas.createCell(0);
			cellDeud0.setCellValue(new HSSFRichTextString("Deuda Actas"));
			cellDeud0.setCellStyle(styleBold);
			Cell cellDeud1 = rowDeudaActas.createCell(1);
			if (acta.getDeudaActasRelacionadas() != null) {
				cellDeud1.setCellValue(acta.getDeudaActasRelacionadas()
						.doubleValue());
			} else {
				cellDeud1.setCellValue(0);
			}
			cellDeud1.setCellStyle(styleAll);
			index++;
			HSSFRow rowOtros = sheet.createRow(index);
			Cell cellOtros0 = rowOtros.createCell(0);
			cellOtros0.setCellValue(new HSSFRichTextString("Otros"));
			cellOtros0.setCellStyle(styleBold);
			Cell cellOtros1 = rowOtros.createCell(1);
			if (acta.getOtros() != null) {
				cellOtros1.setCellValue(acta.getOtros().doubleValue());
			} else {
				cellDeud1.setCellValue(0);
			}
			cellOtros1.setCellStyle(styleAll);
			index++;
			HSSFRow rowInteres = sheet.createRow(index);
			Cell cellInt0 = rowInteres.createCell(0);
			cellInt0.setCellValue(new HSSFRichTextString("Interes"));
			cellInt0.setCellStyle(styleBold);
			Cell cellInt1 = rowInteres.createCell(1);
			cellInt1.setCellValue(acta.getInteres().doubleValue());
			cellInt1.setCellStyle(styleAll);
			index++;
			HSSFRow rowTotal = sheet.createRow(index);
			Cell cellTotal0 = rowTotal.createCell(0);
			cellTotal0.setCellValue(new HSSFRichTextString("Total"));
			cellTotal0.setCellStyle(styleBold);
			Cell cellTotal1 = rowTotal.createCell(1);
			cellTotal1.setCellValue(acta.getTotal().doubleValue());
			cellTotal1.setCellStyle(styleAll);
		}
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		if (conTotales) {
			sheet.autoSizeColumn((short) 8);
			sheet.autoSizeColumn((short) 9);
			sheet.autoSizeColumn((short) 10);
		}
//----		
		if(acta.getInspectoresFirmantes()!=null && acta.getInspectoresFirmantes().size()>0) {
			Integer col=0;
			index=index+3;
			int x=1;
			for(Inspector i:acta.getInspectoresFirmantes()) {
				FileInputStream is;
				
				try {
					is = new FileInputStream(req.getPathTranslated() + "html/images/Firma_Inspector_"+ i.getId() +".jpg");
					byte[] bytes = IOUtils.toByteArray(is);
					int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
					is.close();
					
					HSSFCreationHelper helper = wb.getCreationHelper();
					Drawing drawing = sheet.createDrawingPatriarch();
					HSSFClientAnchor anchor = helper.createClientAnchor();
					anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
					anchor.setCol1(col);
					anchor.setCol2(col);
					anchor.setRow1(index);
					anchor.setRow2(index);
					anchor.setDx1(0);
					anchor.setDy1(0);
					
					col=col+5;
					
					if(acta.getInspectoresFirmantes().size()>2) {
						if(x % 2==0) {
							index=index +20;
							col=0;
						}
					}
					
					Picture pict = drawing.createPicture(anchor, pictureIdx);
					pict.resize();
					x++;
				} catch (Exception e) {
					_log.debug(e.getMessage());
				}
			}
		}
//----		
		return wb;
	}	
	
	private static int getCantAfilPagados(List<ActaPeriodoDeudaEmpresa> peris,
			Date peri) {
		int res = 0;
		for (ActaPeriodoDeudaEmpresa r : peris) {
			if (r.getPeriodo().equals(peri)) {
				for (Detalle det : r.getDetalle()) {
					if (det.getMontoPagado() != null
							&& det.getMontoPagado().doubleValue() != 0D) {
						res++;
					}
				}
			}
		}
		return res;
	}

	private static int getCantAfilDeclarados(
			List<ActaPeriodoDeudaEmpresa> peris, Date peri) {
		int res = 0;
		for (ActaPeriodoDeudaEmpresa r : peris) {
			if (r.getPeriodo().equals(peri)) {
				if (!r.getCuil().equals("00000000000")
						&& r.getRemuneracionDeclarada() != null
						&& r.getRemuneracionDeclarada().doubleValue() != 0D) {
					res++;
				}
			}
		}
		return res;
	}

}
