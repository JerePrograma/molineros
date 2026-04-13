package ar.com.ospim.rrhh.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.rrhh.beans.RegistroAcceso;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.rrhh.services.RegistroAccesoServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteRrhh extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteRrhh.class);
	
	private static final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
	
	public static HSSFWorkbook generaReporteInformacionPersonas(
			HttpServletRequest renderRequest, HttpServletResponse res) {

		List<RegistroAcceso> busqueda = null;

		try {
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");

			String periodicidad = ParamUtil.getString(renderRequest,
					"periodicidad");
			String verDetalle = ParamUtil
					.getString(renderRequest, "verDetalle");
			String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
					"periodoDesdeMesAnio");
			String periodoSemana = ParamUtil.getString(renderRequest,
					"periodoSemana");

			Date fecha = null;
			Date fechaDesde = null;
			Date fechaHasta = null;

			if (periodicidad.equalsIgnoreCase("mes")) {
				try {
					String[] periodoDesdeSplit = null;
					if (periodoDesdeMesAnio.length() > 0) {
						periodoDesdeSplit = periodoDesdeMesAnio.split("_");
					}
					fecha = formatoDePeriodos.parse(Integer
							.parseInt(periodoDesdeSplit[0])
							+ 1 + "/" + periodoDesdeSplit[1]);
				} catch (Exception e) {
					fecha = null;
				}
				fechaDesde = DateUtils.getFirstDateOfMonth(fecha, true);
				fechaHasta = DateUtils.getLastDateOfMonth(fecha, true);
			} else if (periodicidad.equalsIgnoreCase("semana")) {
				try {
					fecha = formatoDeFechas.parse(periodoSemana);
				} catch (Exception e) {
				}
				fechaDesde = DateUtils.getFirstDateOfWeek(fecha, true);
				fechaHasta = DateUtils.getLastDateOfWeek(fecha, true);
			}

			String id_tarjeta_acceso = ParamUtil.getString(renderRequest,
					"persona", null);

			if (id_tarjeta_acceso != null && id_tarjeta_acceso.length() > 0) {
				busqueda = RegistroAccesoServiceUtil.buscarInformacionUsuario(
						fechaDesde, fechaHasta, id_tarjeta_acceso, Boolean
								.valueOf(verDetalle));
			} else {
				busqueda = RegistroAccesoServiceUtil.buscarInformacionUsuarios(
						fechaDesde, fechaHasta, Boolean.valueOf(verDetalle));
			}

			renderRequest.removeAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS);
			renderRequest.setAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS,
					busqueda);

		} catch (Exception e) {
			_log.error(e);
		}

		return generarReporteInfo(busqueda);
	}

	private static HSSFWorkbook generarReporteLectura(
			List<RegistroAcceso> busqueda) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Hoja 1");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		if (busqueda == null || busqueda.isEmpty()) {
			return wb;
		}

		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("Entidad"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("Legajo"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("Fecha de Lectura"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("Hora"));
		cell4H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("Tipo"));
		cell5H.setCellStyle(styleBold);

		for (RegistroAcceso registro : busqueda) {
			if (registro.getId_tarjeta_acceso() == 99999999) {
				continue;
			}
			index++;
			crearHeader(sheet, index, registro, styleBold, styleAll, styleDate,
					styleMoney);
		}

		index++;
		sheet.createRow(index);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		return wb;
	}

	private static void crearHeader(HSSFSheet sheet, int index,
			RegistroAcceso registroAcceso, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(registroAcceso
				.getTarjetaAcceso().getEntidad()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(String.valueOf(registroAcceso
				.getTarjetaAcceso().getLegajo())));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(registroAcceso
				.getTarjetaAcceso().getApellido()
				+ ", " + registroAcceso.getTarjetaAcceso().getNombre()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(registroAcceso
				.getFecha_registroSinHora()));
		cell3.setCellStyle(styleDate);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(registroAcceso
				.getFecha_registroSoloHora()));
		cell4.setCellStyle(styleDate);

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(registroAcceso
				.getTipo_registro()));
		cell5.setCellStyle(styleDate);

		return;
	}

	public static HSSFWorkbook generaReporteLecturasAcceso(
			HttpServletRequest renderRequest, HttpServletResponse res) {

//		String entidad = ParamUtil.getString(renderRequest, "entidad", null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		String id_tarjeta_acceso = ParamUtil.getString(renderRequest,
				"id_tarjeta_acceso", null);

		List<RegistroAcceso> busqueda = null;
		try {
			busqueda = RegistroAccesoServiceUtil.buscarLecturasAcceso(
					fechaDesde, fechaHasta, id_tarjeta_acceso);
		} catch (Exception e) {
			_log.error(e);
		}
		renderRequest.removeAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS);
		renderRequest.setAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS, busqueda);

		return generarReporteLectura(busqueda);
	}

	private static HSSFWorkbook generarReporteInfo(List<RegistroAcceso> busqueda) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Ficha");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleNumber = getStyleNumber(wb);

		if (busqueda == null || busqueda.isEmpty()) {
			return wb;
		}

		int index = 0;
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Fecha Lectura"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Hora"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Tipo Lectura"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H
				.setCellValue(new HSSFRichTextString(
						"Horas Permanencia Lectura"));
		cell4H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Horas Laborales Día"));
		cell5H.setCellStyle(styleBold);

		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Horas Permanencia Día"));
		cell28H.setCellStyle(styleBold);

		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Diferencia por Día"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell2primaH = rowHeader.createCell(++col);
		cell2primaH.setCellValue(new HSSFRichTextString(
				"Horas Laborales Periodo"));
		cell2primaH.setCellStyle(styleBold);

		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H
				.setCellValue(new HSSFRichTextString(
						"Horas Permanencia Periodo"));
		cell9H.setCellStyle(styleBold);

		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Diferencia por Periodo"));
		cell10H.setCellStyle(styleBold);

		for (RegistroAcceso ficha : busqueda) {
			if (!ficha.isOcultar()) {
				index++;
				crearHeaderFicha(sheet, index, ficha, styleBold, styleAll,
						styleDate, styleMoney, styleNumber);
			}
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

		return wb;
	}

	private static void crearHeaderFicha(HSSFSheet sheet, int index,
			RegistroAcceso registro, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {

		HSSFRow rowHeader = sheet.createRow(index);

		int col = -1;

		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(registro.getTarjetaAcceso()
				.getApellido()
				+ ", " + registro.getTarjetaAcceso().getNombre()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(registro
				.getFecha_registroSinHora()));
		cell1.setCellStyle(styleDate);

		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(registro
				.getFecha_registroSoloHora()));
		cell2.setCellStyle(styleDate);

		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(registro.getTipo_registro()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(DateUtils.convertMS(registro
				.getMilisegundosPermanenciaLectura())));
		cell4.setCellStyle(styleDate);

		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(DateUtils.convertMS(registro
				.getMilisegundosLaboralesDia())));
		cell5.setCellStyle(styleDate);

		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString(DateUtils
				.convertMS(registro.getMilisegundosPermanenciaDia())));
		cell28H.setCellStyle(styleDate);

		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(DateUtils
				.convertMSInvertido(registro.getDiferenciaMilisegundosDia())));
		cell6.setCellStyle(styleNumber);

		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString(DateUtils.convertMS(registro
				.getMilisegundosLaboralesPeriodo())));
		cell2H.setCellStyle(styleDate);

		HSSFCell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(new HSSFRichTextString(DateUtils.convertMS(registro
				.getMilisegundosPermanenciaPeriodo())));
		cell9.setCellStyle(styleAll);

		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(

		new HSSFRichTextString(

		DateUtils.convertMSInvertido(registro
				.getDiferenciaMilisegundosPeriodo())));

		cell10.setCellStyle(styleNumber);

		return;
	}

	public static HSSFWorkbook generaReporteControlAccesoAgrupado(
			HttpServletRequest renderRequest, HttpServletResponse res) {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		List<RegistroAcceso> busqueda = null, resultadoFiltrado = null, resultado = null;
		TreeMap<String, List<TarjetaAcceso>> sectoresEmpleados = null;
		List<Feriado> feriados = null;
		try {
//			busqueda = RegistroAccesoServiceUtil.buscarControlAccesoAgrupado(fechaDesde, fechaHasta);
			busqueda = RegistroAccesoServiceUtil.buscarInformacionUsuarios(fechaDesde, fechaHasta, false);
//			resultadoFiltrado = RegistroAccesoServiceUtil.filtrarListaPorEntradasSalidas((ArrayList<RegistroAcceso>) busqueda);
//			resultadoFiltrado = RegistroAccesoServiceUtil.filtrarPorPrimeraEntradasUltimaSalida((ArrayList<RegistroAcceso>) resultadoFiltrado);
			resultadoFiltrado = busqueda;
			resultado = new ArrayList<RegistroAcceso>();
			for (Iterator<RegistroAcceso> iterator = resultadoFiltrado.iterator(); iterator.hasNext();) {
				RegistroAcceso ra = iterator.next();
				if(!ra.isOcultar()){
					resultado.add(ra);
				}
			}
			sectoresEmpleados = RegistroAccesoServiceUtil.getAccesoPersonalPorSector();
			
			feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
			
		} catch (Exception e) {
			_log.error(e);
		}

		return generarReporteControlAgrupado(resultado, sectoresEmpleados, fechaDesde, fechaHasta, feriados);
	}
	
	private static HSSFWorkbook generarReporteControlAgrupado(
			List<RegistroAcceso> busqueda, TreeMap<String, List<TarjetaAcceso>> sectores, 
			Date fechaDesde, Date fechaHasta, List<Feriado> feriados) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Hoja 1");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setLandscape(true); // apaisado true
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		if (busqueda == null || busqueda.isEmpty()) {
			return wb;
		}

		int index = 0;
		
		index = createTitulo(wb, sheet, fechaDesde, fechaHasta);	
		
//		ordenamos los registros por nro tarjeta
		HashMap<String, ArrayList<RegistroAcceso>> listaSeparada = new HashMap<String, ArrayList<RegistroAcceso>>();
		long id_tarjeta_acceso = 0;
		for (RegistroAcceso registroAcceso : busqueda) {
			id_tarjeta_acceso = registroAcceso.getId_tarjeta_acceso();
			if (listaSeparada.get(String.valueOf(id_tarjeta_acceso)) == null) {
				ArrayList<RegistroAcceso> lista = new ArrayList<RegistroAcceso>();
				lista.add(registroAcceso);
				listaSeparada.put(String.valueOf(id_tarjeta_acceso), lista);
			} else {
				listaSeparada.get(String.valueOf(id_tarjeta_acceso)).add(registroAcceso);
			}
		}
//		recorremos desde los sectores, para ir armando cabeceras de grupos de tarjetas (empleados)
		Set<String> sectoresKeys = sectores.keySet();
		for (Iterator<String> iterator = sectoresKeys.iterator(); iterator.hasNext();) {
			String sec = iterator.next();
			
			createHeaderBase(wb, sheet, sec.toUpperCase(), index);
			index = createHeaderDinamic(wb, sheet, index, fechaDesde, fechaHasta);
			
			List<TarjetaAcceso> tarjetas = sectores.get(sec);
			
//			recorremos las tarjetas del sector, para poder buscar en los registros de acceso, la informacion de E/S
			for (Iterator<TarjetaAcceso> iterator2 = tarjetas.iterator(); iterator2.hasNext();) {
				TarjetaAcceso tarjAcc = iterator2.next();
				
//				obtenemos la lista de registros de acceso de la tarjeta
				List<RegistroAcceso> registros = listaSeparada.get(String.valueOf(tarjAcc.getId_tarjeta_acceso())) ;
				
				_log.debug("tarj: " + tarjAcc.getId_tarjeta_acceso() );
				
//				Apellido y Nombre del titular de la tarjeta de acceso
//				Se hace doble cabecera x la fila de entradas, y la fila de salidas
				int posInicialEmpleado=index;
				
				index = createDetalleBase(wb, sheet, index, tarjAcc);

				index = createDetalleBase(wb, sheet, index, tarjAcc);
				
				if(registros == null){ // si no hay fichadas en el periodo...
					registros = new ArrayList<RegistroAcceso>();
				}
				for (Iterator<RegistroAcceso> iterator3 = registros.iterator(); iterator3.hasNext();) {
					
					RegistroAcceso registroAcceso =  iterator3.next();
					
					createDetalle(wb, sheet, posInicialEmpleado, fechaDesde, registroAcceso);
					
				}
//				index++;
//				aca vamos a completar las celdas que correspoden a feriados y findes de semana
				completayFormateaFindeOFeriados(fechaDesde, fechaHasta, wb, sheet, posInicialEmpleado, feriados);
				
			}
			index = index + 1;  //separacion entre sectores
		}
		
		Calendar inicio = Calendar.getInstance();
		Calendar fin = Calendar.getInstance();
		inicio.setTime(fechaDesde);
		fin.setTime(fechaHasta);		
		long maxDiff = (fin.getTimeInMillis() - inicio.getTimeInMillis() )/ MILLSECS_PER_DAY;
		
		for (int i = 0; i < maxDiff+5; i++) { //5 columnas que no son datos de dia de la semana(piso, sector, legajo, edificio)
			sheet.autoSizeColumn((short) i);
		}
		
		return wb;
	}

	private static void completayFormateaFindeOFeriados(Date fechaDesde, Date fechaHasta, HSSFWorkbook wb, HSSFSheet sheet, 
			int rowPosition, List<Feriado> feriados) {
		
		HSSFCellStyle styleBackGroundGris = getStyleFondoGris(wb);
		HSSFCellStyle styleAllCenter = getStyleAllCenter(wb);

		int posCol, column = 5;
		HSSFCell cellEntradaDiaMes = null;
		HSSFCell cellSalidaDiaMes = null;
		Calendar inicio = Calendar.getInstance();
		Calendar fin = Calendar.getInstance();
		inicio.setTime(fechaDesde);
		fin.setTime(fechaHasta);
		fin.add(Calendar.DATE, 1); // agrego 1 dia mas para que la comparacion se ajuste al ultimo dia del periodo
		
		long maxDiff = (fin.getTimeInMillis() - inicio.getTimeInMillis() )/ MILLSECS_PER_DAY;
		
		while(inicio.before(fin)){
			
			long diferencia = (fin.getTimeInMillis() - inicio.getTimeInMillis() )/ MILLSECS_PER_DAY;

			posCol = (int) (maxDiff - 1 + column - diferencia);
			
			if(!DateUtils.esFeriadoOFindeSemana(inicio.getTime(), feriados).isEmpty()){
				
				cellEntradaDiaMes = sheet.getRow(rowPosition).getCell(posCol); // evaluar que tiene la celda antes de crearla/sobreescribirla
				cellSalidaDiaMes = sheet.getRow(rowPosition+1).getCell(posCol); // evaluar que tiene la celda antes de crearla/sobreescribirla
//				hago 2 registros xq se muestra una fila para entrada y otra para salida
				if(DateUtils.esFeriadoOFindeSemana(inicio.getTime(), feriados).equalsIgnoreCase("Feriado")){
					if(cellEntradaDiaMes != null && cellEntradaDiaMes.getCellType() == CellType.STRING){
						// quiere decir que habia datos, alguien ficho un feriado
					}else{
						cellEntradaDiaMes = sheet.getRow(rowPosition).createCell(posCol);
						cellEntradaDiaMes.setCellValue(new HSSFRichTextString("Feriado"));
						cellEntradaDiaMes.setCellStyle(styleAllCenter);
					}
					if(cellSalidaDiaMes != null && cellSalidaDiaMes.getCellType() == CellType.STRING ){
						// quiere decir que habia datos, alguien ficho un feriado
					}else{
						cellSalidaDiaMes = sheet.getRow(rowPosition+1).createCell(posCol);
						cellSalidaDiaMes.setCellValue(new HSSFRichTextString("Feriado"));
						cellSalidaDiaMes.setCellStyle(styleAllCenter);
					}	
				}else{ // es Finde(Sabado o Domingo)
					if(cellEntradaDiaMes != null && cellEntradaDiaMes.getCellType() == CellType.STRING){
						// quiere decir que habia datos, alguien ficho un finde
					}else{
						cellEntradaDiaMes = sheet.getRow(rowPosition).createCell(posCol);
						cellEntradaDiaMes.setCellValue(new HSSFRichTextString(" "));
						cellEntradaDiaMes.setCellStyle(styleBackGroundGris);
					}
					if(cellSalidaDiaMes != null && cellSalidaDiaMes.getCellType() == CellType.STRING){
						// quiere decir que habia datos, alguien ficho un finde
					}else{
						cellSalidaDiaMes = sheet.getRow(rowPosition+1).createCell(posCol);
						cellSalidaDiaMes.setCellValue(new HSSFRichTextString(" "));
						cellSalidaDiaMes.setCellStyle(styleBackGroundGris);
					}	
				}
			}
			inicio.add(Calendar.DATE, 1);
		}
		
	}
	
	private static int createTitulo(HSSFWorkbook wb, HSSFSheet sheet, Date fechaDesde, Date fechaHasta) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String titulo = "Planilla de Ingresos y Egresos " + sdf.format(fechaDesde) + " al " + sdf.format(fechaHasta) ;
		
		int index = 0;
		
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(titulo));
		cell.setCellStyle(styleHeaderEnca);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
		
		index = index + 2;
		
		return index;
	}
	
	
	private static void createHeaderBase(HSSFWorkbook wb, HSSFSheet sheet, String sector, int rowPosition){
		
//		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		setThinBorders(styleBold);


		int index = rowPosition;
		int column = 0;
		
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(column);
		cell0H.setCellValue(new HSSFRichTextString("Legajo"));
		cell0H.setCellStyle(styleBold);
		column++;
		HSSFCell cell1H = rowHeader.createCell(column);
		cell1H.setCellValue(new HSSFRichTextString("Piso"));
		cell1H.setCellStyle(styleBold);
		column++;
		HSSFCell cell2H = rowHeader.createCell(column);
		cell2H.setCellValue(new HSSFRichTextString( sector ));
		cell2H.setCellStyle(styleBold);
		column++;
		HSSFCell cell3H = rowHeader.createCell(column);
		cell3H.setCellValue(new HSSFRichTextString("Entidad"));
		cell3H.setCellStyle(styleBold);
		
	}
	
	private static int createDetalleBase(HSSFWorkbook wb, HSSFSheet sheet, int rowPosition, TarjetaAcceso tarjetaAcc){
		
		HSSFCellStyle styleAll = getStyleAll(wb);
		setThinBorders(styleAll);

		int index = rowPosition;
		int column = 0;
		
		HSSFRow rowHeader = sheet.createRow(index);
//		HSSFRow rowHeader = sheet.getRow(index);

		HSSFCell cell0H = rowHeader.createCell(column);
		cell0H.setCellValue(new HSSFRichTextString(String.valueOf(tarjetaAcc.getLegajo())));
		cell0H.setCellStyle(styleAll);
		column++;
		HSSFCell cell1H = rowHeader.createCell(column);
		cell1H.setCellValue(new HSSFRichTextString( tarjetaAcc.getPiso() ));
		cell1H.setCellStyle(styleAll);
		column++;
		HSSFCell cell2H = rowHeader.createCell(column);
		cell2H.setCellValue(new HSSFRichTextString( tarjetaAcc.getApellido() + ", " + tarjetaAcc.getNombre() ));
		cell2H.setCellStyle(styleAll);
		column++;
		HSSFCell cell3H = rowHeader.createCell(column);
		cell3H.setCellValue(new HSSFRichTextString( tarjetaAcc.getEntidad() ));
		cell3H.setCellStyle(styleAll);
		
		index++;
		
		return index;
	}
	
//	Esta cabecera es dinamica segun el rango de dias seleccionados del periodo. 
//	Se genera a continuacion de la ultima column del metodo: createHeaderBase
	private static int createHeaderDinamic(HSSFWorkbook wb, HSSFSheet sheet, int rowPosition, Date fechaDesde, Date fechaHasta){
		
//		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		setThinBorders(styleBold);
		
		int index = rowPosition;
		int column = 4;
		
//		HSSFRow rowHeaderSemana = sheet.createRow(index);
		HSSFRow rowHeaderSemana = sheet.getRow(index);

		index++;
		
		HSSFRow rowHeaderDiaMes = sheet.createRow(index);
//		HSSFRow rowHeaderDiaMes = sheet.getRow(index);
		index++;
		
		Calendar inicio = Calendar.getInstance();
		Calendar fin = Calendar.getInstance();
		inicio.setTime(fechaDesde);
		fin.setTime(fechaHasta);
		fin.add(Calendar.DATE, 1); // agrego 1 dia mas para que la comparacion se ajuste al ultimo dia del periodo
		
		while(inicio.before(fin) ){
			
			HSSFCell cellSem = rowHeaderSemana.createCell(column);
			cellSem.setCellValue(new HSSFRichTextString( DateUtils.getNombreDiaSemana(inicio).substring(0, 2) ));
			cellSem.setCellStyle(styleBold);
			
			HSSFCell cellDiaMes = rowHeaderDiaMes.createCell(column);
			cellDiaMes.setCellValue(new HSSFRichTextString( String.valueOf(inicio.get(Calendar.DAY_OF_MONTH)) ));
			cellDiaMes.setCellStyle(styleBold);
			
			column++;
			
			inicio.add(Calendar.DATE, 1);
		}
		
		
		
		return index;
	}
	
//	Esta detalle es dinamico segun el rango de dias seleccionados del periodo y si existen los n dias de acceso de la tarjeta en dicho periodo. 
//	calculamos el desplazamiento de columnas desde la fechaDesde
	private static void createDetalle(HSSFWorkbook wb, HSSFSheet sheet, int rowPosition, Date fechaDesde, RegistroAcceso regAcc){
		
		HSSFCellStyle styleAll = getStyleAll(wb);
//		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCell cellDetalle = null;
		
		int index = rowPosition;
		int posCol, column = 4;

		if(regAcc.getTipo_registro().equalsIgnoreCase("S")){
			index++;
		}
		
		HSSFRow rowRegistro = sheet.getRow(index);
		
		long diferencia= ( regAcc.getFecha_registro().getTime() - fechaDesde.getTime() )/ MILLSECS_PER_DAY;

		posCol = (int) (column + diferencia);
		
		cellDetalle = rowRegistro.createCell(posCol);
		cellDetalle.setCellValue(new HSSFRichTextString(regAcc.getFecha_registroSoloHora()));
		cellDetalle.setCellStyle(styleAll);
	}
	
}