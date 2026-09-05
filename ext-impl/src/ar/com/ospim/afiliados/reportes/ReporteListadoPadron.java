<<<<<<< .mine
package ar.com.ospim.afiliados.reportes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;
import ar.com.ospim.afiliados.reportes.beans.ReportePadronTotalResult;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.StringUtils;

public class ReporteListadoPadron extends ReporteXLS {

	private static Logger _log = Logger.getLogger(ReporteListadoPadron.class);

	private static final String SEPARATOR = ";";

	public static void getReporte(HttpServletRequest req, HttpServletResponse res, 
			ZipOutputStream out) throws IOException {
		
		BusquedaReportePadronFiltro filtro = getFiltrosPadron(req, res);

		List<ReportePadronResult> list = procesaPadron(filtro);
		
		int sh = 1;
		int index = 0;
		out.putNextEntry(new ZipEntry(sh + ".csv"));
		out.write(createHeader().getBytes());
		for (ReportePadronResult r : list) {
			index++;
			if (index >= 1048576) {
				index = 0;
				sh++;
				out.putNextEntry(new ZipEntry(sh + ".csv"));
				out.write(createHeader().getBytes());
			}

			String line = getLine(r);
			out.write(line.getBytes());
		}
	}

	private static String getLine(ReportePadronResult r) {
		StringBuilder str = new StringBuilder();
		str.append(r.getCuil_titular());
		str.append(SEPARATOR);
		str.append(r.getCuil());
		str.append(SEPARATOR);
		str.append(r.getInte());
		str.append(SEPARATOR);
		str.append(r.getId_ospim() != 0 ? String.valueOf(r.getId_ospim()) : "");
		str.append(SEPARATOR);
		str.append(r.getId_uoma() != 0 ? String.valueOf(r.getId_uoma()) : "");
		str.append(SEPARATOR);
		str.append(r.getId_amtima() != 0 ? String.valueOf(r.getId_amtima())
				: "");
		str.append(SEPARATOR);
		if (r.getFecha_ospim() != null) {
			str.append(r.getFecha_ospim());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		if (r.getAlta_fecha() != null) {
			str.append(r.getAlta_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		str.append(r.getUnifica());
		str.append(SEPARATOR);
		str.append(r.getSeccional());
		str.append(SEPARATOR);
		str.append(r.getId_tercerizadora());
		str.append(SEPARATOR);
		str.append(r.getParentesco());
		str.append(SEPARATOR);
		str.append(r.getApellido());
		str.append(SEPARATOR);
		str.append(r.getNombre());
		str.append(SEPARATOR);
		str.append(r.getDocumento_tipo());
		str.append(SEPARATOR);
		str.append(r.getDocu_numero());
		str.append(SEPARATOR);
		str.append(r.getNaci_fecha());
		str.append(SEPARATOR);
		str.append(r.getSexo());
		str.append(SEPARATOR);
		str.append(r.getCivil_esta());
		str.append(SEPARATOR);
		str.append(r.getNacionalidad());
		str.append(SEPARATOR);
		str.append(r.getProvincia());
		str.append(SEPARATOR);
		str.append(r.getLocalidad());
		str.append(SEPARATOR);
		str.append(r.getPostal_codi());
		str.append(SEPARATOR);
		str.append(r.getCalle());
		str.append(SEPARATOR);
		str.append(getValue(r.getNumero()));
		str.append(SEPARATOR);
		str.append(getValue(r.getPiso()));
		str.append(SEPARATOR);
		str.append(getValue(r.getDepto()));
		str.append(SEPARATOR);
		str.append(getValue(r.getTelefono()));
		str.append(SEPARATOR);
		str.append(getValue(r.getEmail()));
		str.append(SEPARATOR);
		str.append(r.getCategoria());
		str.append(SEPARATOR);
		str.append(r.getPlan());
		str.append(SEPARATOR);
		if (r.getIngre_fecha() != null) {
			str.append(r.getIngre_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		if (r.getBaja_fecha() != null) {
			str.append(r.getBaja_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		str.append(r.getCuit());
		str.append(SEPARATOR);
		str.append(r.getRazon_soc());
		str.append(SEPARATOR);
		str.append(r.getRamo());
		str.append(SEPARATOR);
		str.append(r.getEscala_salarial());
		str.append(SEPARATOR);
		str.append(r.getDiscapacitado());
		str.append("\n");
		return str.toString();
	}

	private static String createHeader() {
		StringBuilder str = new StringBuilder();
		str.append("CUIL TITULAR");
		str.append(SEPARATOR);
		str.append("CUIL");
		str.append(SEPARATOR);
		str.append("INTE");
		str.append(SEPARATOR);
		str.append("ID_OSPIM");
		str.append(SEPARATOR);
		str.append("ID_UOMA");
		str.append(SEPARATOR);
		str.append("ID_AMTIMA");
		str.append(SEPARATOR);
		str.append("FECHA OSPIM");
		str.append(SEPARATOR);
		str.append("FECHA_REGISTRO");
		str.append(SEPARATOR);
		str.append("UNIFICA");
		str.append(SEPARATOR);
		str.append("SECCIONAL");
		str.append(SEPARATOR);
		str.append("TERCERIZADORA");
		str.append(SEPARATOR);
		str.append("PARENTESCO");
		str.append(SEPARATOR);
		str.append("APELLIDO");
		str.append(SEPARATOR);
		str.append("NOMBRE");
		str.append(SEPARATOR);
		str.append("TIPO DOC");
		str.append(SEPARATOR);
		str.append("NRO DOC");
		str.append(SEPARATOR);
		str.append("FECHA NAC");
		str.append(SEPARATOR);
		str.append("SEXO");
		str.append(SEPARATOR);
		str.append("ESTADO CIVIL");
		str.append(SEPARATOR);
		str.append("NACIONALIDAD");
		str.append(SEPARATOR);
		str.append("PROVINCIA");
		str.append(SEPARATOR);
		str.append("LOCALIDAD");
		str.append(SEPARATOR);
		str.append("CP");
		str.append(SEPARATOR);
		str.append("CALLE");
		str.append(SEPARATOR);
		str.append("NUMERO");
		str.append(SEPARATOR);
		str.append("PISO");
		str.append(SEPARATOR);
		str.append("DEPTO");
		str.append(SEPARATOR);
		str.append("TELEFONO");
		str.append(SEPARATOR);
		str.append("CORREO ELECTRONICO");
		str.append(SEPARATOR);
		str.append("CATEGORIA");
		str.append(SEPARATOR);
		str.append("PLAN");
		str.append(SEPARATOR);
		str.append("FECHA PLAN");
		str.append(SEPARATOR);
		str.append("FECHA BAJA");
		str.append(SEPARATOR);
		str.append("CUIT");
		str.append(SEPARATOR);
		str.append("RAZON SOCIAL");
		str.append(SEPARATOR);
		str.append("RAMO");
		str.append(SEPARATOR);
		str.append("ESCALA SALARIAL");
		str.append(SEPARATOR);
		str.append("DISCAPACITADO");
		str.append("\n");
		return str.toString();
	}
	
	public static List<ReportePadronTotalResult> getTotalesEntidad(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronTotalResult> repoTotales = null;

		try {
			repoTotales=ReportesAfiliadoServiceUtil.getReportePadronTotalesEntidad(filtro.getFechaDesde(), 
					filtro.getIdsTercerizadora(), filtro);
			
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return repoTotales;
	}

	public static List<ReportePadronTotalResult> generaPadronTotales(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronTotalResult> repoTotales = null;
	
		if (filtro.isTotalesPorTercerizadora() || filtro.isTotalesPorPlan() 
				|| filtro.isTotalesPorSeccional() || filtro.isTotalesPorEmpresa()) {
			
			try {
				repoTotales = ReportesAfiliadoServiceUtil.getReportePadronTotales(filtro);
			} catch (SystemException e) {
				_log.error(e);
			}

		}
		
		return repoTotales;
	}

	public static BusquedaReportePadronFiltro getFiltrosPadron(HttpServletRequest req, HttpServletResponse res) {
		
		BusquedaReportePadronFiltro filtro = new BusquedaReportePadronFiltro();
		
		String fechaDesdeDia = req.getParameter("fechaDesdeDia");
		String fechaDesdeMes = req.getParameter("fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
		String fechaHastaDia = req.getParameter("fechaHastaDia");
		String fechaHastaMes = req.getParameter("fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = req.getParameter("fechaHastaAnio");
//		String fechaProcDesdeDia = req.getParameter("fechaProcDesdeDia");
//		String fechaProcDesdeMes = req.getParameter("fechaProcDesdeMes");
//		fechaProcDesdeMes = String.valueOf(Integer.valueOf(fechaProcDesdeMes) + 1);
//		String fechaProcDesdeAnio = req.getParameter("fechaProcDesdeAnio");
//		String fechaProcHastaDia = req.getParameter("fechaProcHastaDia");
//		String fechaProcHastaMes = req.getParameter("fechaProcHastaMes");
//		fechaProcHastaMes = String.valueOf(Integer.valueOf(fechaProcHastaMes) + 1);
//		String fechaProcHastaAnio = req.getParameter("fechaProcHastaAnio");
		String cuit = req.getParameter("cuit");
		String sucursal = req.getParameter("sucursal");
		String razonSocial = req.getParameter("razonSocial");
		String edadIni = req.getParameter("edadIni");
		String edadFin = req.getParameter("edadFin");
		String tituYFliares = req.getParameter("tituYFliares");
		String tituYFliaresDesc = req.getParameter("tituYFliaresDesc");
		
		String idsTercerizadoras = req.getParameter("idTercerizadora");
		String descTercerizadoras = "Todas";
		if (null==idsTercerizadoras || idsTercerizadoras.equals("null") || idsTercerizadoras.trim().length()==0) {
			idsTercerizadoras = null;
		}else{
			idsTercerizadoras += ",";
			descTercerizadoras = req.getParameter("descTercerizadora");
		}
		String idLoca = req.getParameter("idLoca");
		String descLocalidades = "Todas";
		if (null==idLoca || idLoca.equals("null") || idLoca.trim().length()==0) {
			idLoca = null;
		}else{
			idLoca += ",";
			descLocalidades = req.getParameter("descLocalidades");
		}
		String idProv = req.getParameter("idProv");
		String descProvincias = "Todas";
		if (null==idProv || idProv.equals("null") || idProv.trim().length()==0) {
			idProv = null;
		}else{
			idProv += ",";
			descProvincias = req.getParameter("descProvincias");
		}
		String idPlan = req.getParameter("idPlan");
		String descPlanes = "Todos";
		if (null==idPlan || idPlan.equals("null") || idPlan.trim().length()==0) {
			idPlan = null;
		}else{
			idPlan += ",";
			descPlanes = req.getParameter("descPlanes");
		}
		
		String tipoAportes = ParamUtil.getString(req, "tipoAporte");
		String descTiposAporte = "Todos";
		if (null==tipoAportes || tipoAportes.equals("null") || tipoAportes.trim().length()==0) {
			tipoAportes = null;
		}else{
			tipoAportes += ",";
			descTiposAporte = req.getParameter("descTiposAporte");
		}
		
		String parentesco = req.getParameter("parentesco");
		String descParentesco = "Todos";
		Integer idParentescoSss = null;
		try{
			idParentescoSss = Integer.parseInt(parentesco);
			descParentesco = req.getParameter("descParentesco");
		}catch (NumberFormatException e) {
			idParentescoSss = null;
		}
		
		String idSeccional = req.getParameter("idSeccional");
		String descSeccionales = "Todas";
		if (null==idSeccional || idSeccional.equals("null") || idSeccional.trim().length()==0) {
			idSeccional = null;
		}else{
			idSeccional += ",";
			descSeccionales = req.getParameter("descSeccionales");
		}
		String escalaSalarial = req.getParameter("escala_salarial");
		String motivoBajaIds = req.getParameter("idsMotivoBaja");
		String motivosBajaDesc = "Todos";

		if (null==motivoBajaIds || motivoBajaIds.equals("null") || motivoBajaIds.trim().length()==0) {
			motivoBajaIds = null;
		}else{
			motivoBajaIds += ",";
			motivosBajaDesc = req.getParameter("motivosBajaDesc");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		int tipoBusqueda= ParamUtil.getInteger(req,"tipoBusqueda");
		String descTipoBusqueda= req.getParameter("descTipoBusqueda");
		String proyecto = req.getParameter("proyecto");
		if(StringUtils.checkEmpty(proyecto)){
			proyecto = null;
		}
		boolean esExportaTercerizadora= ParamUtil.getBoolean(req, "vistaTercerizadora");
		
		if(esExportaTercerizadora) {
			idPlan="";
			List<Plan> pls=TraeListasServiceUtil.getPlanesSoloOspim();
			for(Plan p:pls) {
				idPlan+=p.getId()+",";
			}
//			idPlan=idPlan.substring(0,idPlan.length()-1);
			
		}
		
		
		Date fechaDsd=null, fechaHta = null;
		Date fechaNacimIni = null, fechaNacimFin = null;
		
		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			
			fechaDsd = fechaIni;
			fechaHta = fechaFin;
			
//			Date fechaProcDesde = format.parse(fechaProcDesdeDia + "-" + fechaProcDesdeMes
//					+ "-" + fechaProcDesdeAnio);
//			Date fechaProcHasta = format.parse(fechaProcHastaDia + "-" + fechaProcHastaMes
//					+ "-" + fechaProcHastaAnio);
						
//			la fecha inicio debe considerar desde el 1 de enero, y la fecha hasta el 31 de diciembre
			if(!StringUtils.checkEmpty(edadIni) && !StringUtils.checkEmpty(edadFin)){

				Calendar fin = Calendar.getInstance();
				if(Integer.parseInt(edadIni) > 0){ 
					fin.setTime(fechaFin);
					fin.add(Calendar.YEAR, -1 * Integer.valueOf(edadFin));
				}else{
					fin.setTime(fechaFin);
				}
				fechaNacimFin = fin.getTime();
				
				Calendar ini = Calendar.getInstance();
				ini.setTime(fechaIni);
				if(Integer.parseInt(edadIni) == 0){ 
					edadIni = "1";
				}
				ini.add(Calendar.YEAR, -1 * Integer.valueOf(edadIni));
				fechaNacimIni = ini.getTime();
				
				if(fechaNacimIni.after(fechaNacimFin)){
					Date auxCambia = null;
					auxCambia = fechaNacimFin;
					fechaNacimFin = fechaNacimIni;
					fechaNacimIni = auxCambia;
				}
			}
		} catch (ParseException e) {
			_log.error(e);
		}
		boolean totalesPorTercerizadora = ParamUtil.getBoolean(req,"total_tercerizadora");
		boolean totalesPorPlan = ParamUtil.getBoolean(req, "total_plan");
		boolean totalesPorSeccional = ParamUtil.getBoolean(req, "total_seccional");
		boolean totalesPorEmpresa = ParamUtil.getBoolean(req, "total_empresa");
		boolean totalesPorEntidad = ParamUtil.getBoolean(req, "total_entidad");
		
		filtro.setCategoriaUoma(escalaSalarial);
		filtro.setCodigosAportes(tipoAportes);
		filtro.setCodigosLocalidad(idLoca);
		filtro.setCodigosPlan(idPlan);
		filtro.setCodigosProvincia(idProv);
		filtro.setCodigosSeccional(idSeccional);
		filtro.setCuit(cuit);
		filtro.setDescAportes(descTiposAporte);
		filtro.setDescBusqueda(descTipoBusqueda);
		filtro.setDescLocalidad(descLocalidades);
		filtro.setDescMotivoBaja(motivosBajaDesc); 
		filtro.setDescPlan(descPlanes);
		filtro.setDescProvincia(descProvincias);
		filtro.setDescSeccional(descSeccionales);
		filtro.setDescTercerizadora(descTercerizadoras);
		try{
			filtro.setEdadFinal(Integer.parseInt(edadFin));
		}catch (Exception e) {
			filtro.setEdadFinal(0);
		}
		try{
			filtro.setEdadInicial(Integer.parseInt(edadIni));
		}catch (Exception e) {
			filtro.setEdadInicial(0);
		}
		filtro.setFechaDesde(fechaDsd);
		filtro.setFechaHasta(fechaHta);
		filtro.setFechaNacimIni(fechaNacimIni);
		filtro.setFechaNacimFin(fechaNacimFin);
		filtro.setIdsMotivoBaja(motivoBajaIds );
		filtro.setIdsTercerizadora(idsTercerizadoras);
		filtro.setParentescoDesc(descParentesco);
		filtro.setParentescoId(idParentescoSss);
		filtro.setRazonSocial(razonSocial);
		filtro.setSucursal(sucursal);
		filtro.setTipoBusqueda(tipoBusqueda);
		filtro.setTitularesYFliares(tituYFliaresDesc);
		filtro.setTituyfliares(Integer.parseInt(tituYFliares));
		filtro.setTotalesPorEmpresa(totalesPorEmpresa);
		filtro.setTotalesPorEntidad(totalesPorEntidad);
		filtro.setTotalesPorPlan(totalesPorPlan);
		filtro.setTotalesPorSeccional(totalesPorSeccional);
		filtro.setTotalesPorTercerizadora(totalesPorTercerizadora);
		filtro.setProyecto(proyecto);
		filtro.setVistaPrevencion(esExportaTercerizadora);
		
		req.getSession().setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PADRON, filtro);
		
		return filtro;
	}
	
	public static List<ReportePadronResult> procesaPadron(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronResult> repo = null;

		try {
			repo = ReportesAfiliadoServiceUtil.getReportePadron(filtro);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return repo;
	}

	public static SXSSFWorkbook generaReportePadron(HttpServletRequest req,
			HttpServletResponse res) {

		req.getSession().removeAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PADRON);
		
//		Recuperamos el filtro para que todos los reportes unifiquen lso criterios de busqueda
//		pegamos el filtro en la session
		BusquedaReportePadronFiltro filtro = getFiltrosPadron(req,res);
		boolean esVistaAdmifarm = ParamUtil.getBoolean(req, "vistaAdmifarm");
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		//HSSFWorkbook wb = null;

//		boolean totales_tercerizadora = ParamUtil.getBoolean(req,
//				"total_tercerizadora");
//		boolean totales_plan = ParamUtil.getBoolean(req, "total_plan");
//		boolean totales_seccional = ParamUtil
//				.getBoolean(req, "total_seccional");
//		boolean totales_empresa = ParamUtil.getBoolean(req, "total_empresa");
//		boolean totales_entidad = ParamUtil.getBoolean(req, "total_entidad");
		
		List<ReportePadronTotalResult> repoTotales = null;
		List<ReportePadronResult> repo = null;
		
		if(filtro.isTotalesPorEntidad()){
			repoTotales= getTotalesEntidad(filtro);		
//			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//			String fechaDesdeDia = req.getParameter("fechaDesdeDia");
//			String fechaDesdeMes = req.getParameter("fechaDesdeMes");
//			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
//			String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
//			Date fechaIni=null;
//			try {
//			fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
//					+ "-" + fechaDesdeAnio);			
//			} catch (ParseException e) {
//				_log.error(e);
//			}
			wb = getReporteTotalesEntidad(repoTotales, filtro);
			
		}else if (filtro.isTotalesPorTercerizadora() || filtro.isTotalesPorPlan() 
				|| filtro.isTotalesPorSeccional() || filtro.isTotalesPorEmpresa()) {
			
			repoTotales = generaPadronTotales(filtro);
			
			wb = getReporteTotales(repoTotales, filtro);
		} else {
			repo = procesaPadron(filtro); // getFiltrosPadron(req, res);

//			TODO:
//			Sacar esto cuando todos los reportes esten bajo el mismo formato..bajo.
			if(filtro.getTipoBusqueda() == 2 ){ // baja fecha proceso
				wb = getReporteBajas(repo);
			}else{
				wb = getReporte(repo, filtro, esVistaAdmifarm);
			}	
		}

		return wb;
	}

	private static SXSSFWorkbook getReporteTotalesEntidad(List<ReportePadronTotalResult> repo, BusquedaReportePadronFiltro filtro) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1;
		//HSSFSheet sheet = wb.createSheet("Hoja " + sh);
        Sheet sheet = wb.createSheet("Hoja "  + sh);

		
		int index = 0;
		int indexColumn = 0;
		sheet.createRow(index);
		CellStyle styleHeader = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleCell = getStyleAllWithBorderWbs(wb);
		CellStyle style = getStyleAllWbs(wb);
		CellStyle styleCellTotal=getStyleBoldAlignedWbs(wb,HorizontalAlignment.RIGHT);
		
		index=createHeaderTotalEntidades(sheet, index, styleHeader, style, filtro);
		
		int uoma_titular=0;
		//int uoma_adherente=0;
		int ospim_titular=0;
		int ospim_adherente=0;
		int amtima_titular=0;
		//int amtima_adherente=0;
		int totalTitulares=0;
		int totalIntegrantes=0;
		int ospim_capitas_titular=0;
		int ospim_capitas_adherente=0;
		int ospim_desregulados_titular=0;
		int ospim_desregulados_adherente=0;
		
		if (repo != null) {
			for (ReportePadronTotalResult r : repo) {
				index++;
				indexColumn = 0;
				Row rowI = sheet.createRow(index);
				
				CellUtil.createCell(rowI, indexColumn,r.getSeccional(), styleCell);
				indexColumn++;				
				Cell col = rowI.createCell(indexColumn);
				col.setCellValue(r.getUoma_titular());
				uoma_titular+=r.getUoma_titular();
				col.setCellStyle(styleCell);
				indexColumn++;
				Cell col3 = rowI.createCell(indexColumn);
				col3.setCellValue(r.getOspim_titular());
				ospim_titular+=r.getOspim_titular();
				col3.setCellStyle(styleCell);				
				indexColumn++;	
				Cell col6 = rowI.createCell(indexColumn);
				col6.setCellValue(r.getOspim_adherente());
				ospim_adherente+=r.getOspim_adherente();
				col6.setCellStyle(styleCell);
				indexColumn++;	
				Cell col4 = rowI.createCell(indexColumn);
				col4.setCellValue(r.getTotalCapitasTitular());
				ospim_capitas_titular+=r.getTotalCapitasTitular();
				col4.setCellStyle(styleCell);				
				indexColumn++;
				Cell col7 = rowI.createCell(indexColumn);
				col7.setCellValue(r.getTotalCapitasAdherente());
				ospim_capitas_adherente+=r.getTotalCapitasAdherente();
				col7.setCellStyle(styleCell);
				indexColumn++;				
				Cell col5 = rowI.createCell(indexColumn);
				col5.setCellValue(r.getTotalDesreguladosTitular());
				ospim_desregulados_titular+=r.getTotalDesreguladosTitular();
				col5.setCellStyle(styleCell);				
				indexColumn++;
				Cell col8 = rowI.createCell(indexColumn);
				col8.setCellValue(r.getTotalDesreguladosAdherente());
				ospim_desregulados_adherente+=r.getTotalDesreguladosAdherente();
				col8.setCellStyle(styleCell);
				indexColumn++;				
				Cell col9 = rowI.createCell(indexColumn);
				col9.setCellValue(r.getAmtima_titular());
				amtima_titular+=r.getAmtima_titular();
				col9.setCellStyle(styleCell);
				indexColumn++;
				Cell col10 = rowI.createCell(indexColumn);
				col10.setCellValue(r.getTotalTitulares());
				col10.setCellStyle(styleCell);
				totalTitulares+=r.getTotalTitulares();
				indexColumn++;
				Cell col11 = rowI.createCell(indexColumn);
				col11.setCellValue(r.getTotalIntegrantes());
				col11.setCellStyle(styleCell);
				totalIntegrantes+=r.getTotalIntegrantes();
				
			}
			index++;
			indexColumn = 0;
			Row rowI = sheet.createRow(index);
			
			CellUtil.createCell(rowI, indexColumn,"TOTAL", styleCellTotal);
			indexColumn++;
			Cell col = rowI.createCell(indexColumn);
			col.setCellValue(uoma_titular);			
			col.setCellStyle(styleCell);
			indexColumn++;				
			Cell col3 = rowI.createCell(indexColumn);
			col3.setCellValue(ospim_titular);			
			col3.setCellStyle(styleCell);				
			indexColumn++;
			Cell col6 = rowI.createCell(indexColumn);
			col6.setCellValue(ospim_adherente);			
			col6.setCellStyle(styleCell);
			indexColumn++;	
			Cell col4 = rowI.createCell(indexColumn);
			col4.setCellValue(ospim_capitas_titular);			
			col4.setCellStyle(styleCell);				
			indexColumn++;	
			Cell col7 = rowI.createCell(indexColumn);
			col7.setCellValue(ospim_capitas_adherente);			
			col7.setCellStyle(styleCell);
			indexColumn++;	
			Cell col5 = rowI.createCell(indexColumn);
			col5.setCellValue(ospim_desregulados_titular);			
			col5.setCellStyle(styleCell);				
			indexColumn++;	
			Cell col8 = rowI.createCell(indexColumn);
			col8.setCellValue(ospim_desregulados_adherente);			
			col8.setCellStyle(styleCell);
			indexColumn++;	
			Cell col9 = rowI.createCell(indexColumn);
			col9.setCellValue(amtima_titular);			
			col9.setCellStyle(styleCell);
			indexColumn++;				
			Cell col10 = rowI.createCell(indexColumn);
			col10.setCellValue(totalTitulares);			
			col10.setCellStyle(styleCell);
			indexColumn++;				
			Cell col11 = rowI.createCell(indexColumn);
			col11.setCellValue(totalIntegrantes);			
			col11 .setCellStyle(styleCell);
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
		
		return wb;

	}

	private static SXSSFWorkbook getReporteTotales(List<ReportePadronTotalResult> repo, 
			BusquedaReportePadronFiltro filtro) {
		
		SXSSFWorkbook wb = new SXSSFWorkbook();
		CellStyle style = getStyleAllWbs(wb);
		
		int sh = 1;
		//HSSFSheet sheet = wb.createSheet("Hoja " + sh);
		
		Sheet sheet = wb.createSheet("Hoja "  + sh);
		
		int index = 0;
		int indexColumn = 0;
		
		Row rowTitulo = sheet.createRow(index);
		crearTitulosTotales(sheet,rowTitulo,style,filtro);
		
		Row row = sheet.createRow(index++);
		CellStyle styleHeader = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleCell = getStyleAllWithBorderWbs(wb);
		
		createHeaderTotales(row, styleHeader, filtro.isTotalesPorTercerizadora(), filtro.isTotalesPorSeccional(),
				filtro.isTotalesPorPlan(), filtro.isTotalesPorEmpresa());
		
		row = sheet.createRow(index);	
		createHeaderDatosTotales(row, styleCell, filtro.isTotalesPorTercerizadora(), filtro.isTotalesPorSeccional(),
				filtro.isTotalesPorPlan(), filtro.isTotalesPorEmpresa(),filtro.isVistaPrevencion());

		if (repo != null) {
			for (ReportePadronTotalResult r : repo) {
				index++;
				indexColumn = 0;
				Row rowI = sheet.createRow(index);
				if (filtro.isTotalesPorTercerizadora()) {
					CellUtil.createCell(rowI, indexColumn,
							r.getTercerizadora(), styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorSeccional()) {
					CellUtil.createCell(rowI, indexColumn,
							r.getSeccional(), styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorPlan()) {
					CellUtil.createCell(rowI, indexColumn, r.getPlan(),
							styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorEmpresa()) {
					CellUtil.createCell(rowI, indexColumn, r.getCuit(),
							styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							r.getRazon_soc(), styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getRamoEmpresa().getId_ramo_empresa()) , styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							r.getRamoEmpresa().getDescripcion()  , styleCell);
					indexColumn++;
				}
				if(filtro.isTotalesPorPlan() && filtro.isVistaPrevencion()) {
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getCantTitular()) , styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getCantAdherente()) , styleCell);
					
				}else {					
					CellUtil.createCell(rowI, indexColumn++, r.getParentesco(),
							styleCell);
					//indexColumn++;
					Cell col = rowI.createCell(indexColumn);
					col.setCellValue(r.getTotal());
					col.setCellStyle(styleCell);
				}
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


		return wb;
	}


	
	private static void createHeaderDatosTotales(Row row,
			CellStyle styleCell, boolean total_tercerizadora,
			boolean total_seccional, boolean total_plan, boolean total_empresa, boolean vistaPrevencion) {
		int indexColumn = 0;

		if (total_tercerizadora) {
			Cell cell21 = row.createCell(indexColumn++);
			cell21.setCellValue(new HSSFRichTextString("Cod Ter."));
			cell21.setCellStyle(styleCell);			
		}
		if (total_seccional) {
			Cell cell1 = row.createCell(indexColumn);
			cell1.setCellValue(new HSSFRichTextString("Cod Seccional"));
			cell1.setCellStyle(styleCell);
			indexColumn++;
		}
		if (total_plan) {
			Cell cell2 = row.createCell(indexColumn);
			cell2.setCellValue(new HSSFRichTextString("Cod Plan"));
			cell2.setCellStyle(styleCell);
			indexColumn++;
		}
		if (total_empresa) {
			Cell cell21 = row.createCell(indexColumn++);
			cell21.setCellValue(new HSSFRichTextString("Cuit"));
			cell21.setCellStyle(styleCell);
			
			Cell cell22 = row.createCell(indexColumn++);
			cell22.setCellValue(new HSSFRichTextString("Empresa"));
			cell22.setCellStyle(styleCell);
			
			Cell cell23 = row.createCell(indexColumn++);
			cell23.setCellValue(new HSSFRichTextString("Id Ramo"));
			cell23.setCellStyle(styleCell);
			
			Cell cell24 = row.createCell(indexColumn++);
			cell24.setCellValue(new HSSFRichTextString("Descripción Ramo"));
			cell24.setCellStyle(styleCell);
			
		}
		
		if (total_plan && vistaPrevencion) {
			Cell cell25 = row.createCell(indexColumn++);
			cell25.setCellValue(new HSSFRichTextString("Titular"));
			cell25.setCellStyle(styleCell);
			
			Cell cell26 = row.createCell(indexColumn++);
			cell26.setCellValue(new HSSFRichTextString("Adherente"));
			cell26.setCellStyle(styleCell);
		}else {			
			Cell cell25 = row.createCell(indexColumn++);
			cell25.setCellValue(new HSSFRichTextString("Parentesco"));
			cell25.setCellStyle(styleCell);
			
			Cell cell26 = row.createCell(indexColumn++);
			cell26.setCellValue(new HSSFRichTextString("Cant"));
			cell26.setCellStyle(styleCell);
			
		}
	}



	private static void createHeaderTotales(Row row,
			CellStyle styleHeader, boolean total_tercerizadora,
			boolean total_seccional, boolean total_plan, boolean total_empresa) {
		int indexColumn = 0;

		if (total_tercerizadora) {
			Cell cell0 = row.createCell(indexColumn);
			cell0.setCellValue(new HSSFRichTextString("Reporte Totales por Tercerizadora"));
			cell0.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_seccional) {
			Cell cell1 = row.createCell(indexColumn);
			cell1.setCellValue(new HSSFRichTextString("Reporte Totales por Seccional"));
			cell1.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_plan) {
			Cell cell2 = row.createCell(indexColumn);
			cell2.setCellValue(new HSSFRichTextString("Reporte Totales por Plan"));
			cell2.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_empresa) {
			Cell cell21 = row.createCell(indexColumn);
			cell21.setCellValue(new HSSFRichTextString("Reporte de Totales por Empresa"));
			cell21.setCellStyle(styleHeader);
			indexColumn++;
		}else{
			Cell cell3 = row.createCell(indexColumn++);
			cell3.setCellValue(new HSSFRichTextString("Parentesco"));
			cell3.setCellStyle(styleHeader);

			Cell cell4 = row.createCell(indexColumn);
			cell4.setCellValue(new HSSFRichTextString("Total"));
			cell4.setCellStyle(styleHeader);
	
		}
		
			}

	private static void crearTitulosTotales(Sheet sheet, Row rowTitulo, CellStyle style, 
			BusquedaReportePadronFiltro filtro){
		
		String tituloReporte = "";
		if(filtro.isTotalesPorEntidad()){
			tituloReporte="Reporte de Totales por entidad - ".toUpperCase();
		}else if (filtro.isTotalesPorEmpresa()){
			tituloReporte="Reporte de Totales por empresa - ".toUpperCase();
		}else if (filtro.isTotalesPorPlan()){
			tituloReporte="Reporte de Totales por plan - ".toUpperCase();
		}else if (filtro.isTotalesPorSeccional()){
			tituloReporte="Reporte de Totales por seccional - ".toUpperCase();
		}else if (filtro.isTotalesPorTercerizadora()){
			tituloReporte="Reporte de Totales por tercerizadora - ".toUpperCase();
		}				
//		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
//		gmtMenos3.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int colNum = 0;
		
		style.setWrapText(true);

		Cell cell0 = rowTitulo.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString(tituloReporte +filtro.getDescripcionFiltros()) + 
				"        Fecha Listado: " + sdf.format(gmtMenos3.getTime()));
		
		rowTitulo.setHeight((short)650);
//		sheet.setColumnGroupCollapsed(colNum, true);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
	}
	private static int createHeaderTotalEntidades(Sheet sheet, int index,
			CellStyle styleHeader, CellStyle style, BusquedaReportePadronFiltro filtro) {
		Row rowTitulo = sheet.createRow(index++);
//		Cell cellTitulo = rowTitulo.createCell(0);
//		cellTitulo.setCellValue(new HSSFRichTextString("Reporte de Totales por entidad al "+fecha));
//		cellTitulo.setCellStyle(style);
		crearTitulosTotales(sheet,rowTitulo,style,filtro);
		
		Row row = sheet.createRow(index++);
		int indexColumn = 0;

		Cell cell0 = row.createCell(indexColumn++);
		cell0.setCellValue(new HSSFRichTextString("Seccional"));
		cell0.setCellStyle(styleHeader);
		
		Cell cell2 = row.createCell(indexColumn++);
		cell2.setCellValue(new HSSFRichTextString("UOMA"));
		cell2.setCellStyle(styleHeader);	
		
		Cell cell3 = row.createCell(indexColumn++);
		cell3.setCellValue(new HSSFRichTextString("OSPIM"));
		cell3.setCellStyle(styleHeader);
		
		Cell cell4 = row.createCell(indexColumn++);
		cell4.setCellStyle(styleHeader);
		Cell cell5 = row.createCell(indexColumn++);
		cell5.setCellStyle(styleHeader);
		Cell cell6 = row.createCell(indexColumn++);
		cell6.setCellStyle(styleHeader);
		Cell cell7 = row.createCell(indexColumn++);
		cell7.setCellStyle(styleHeader);
		Cell cell8 = row.createCell(indexColumn++);
		cell8.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 7));
		
		Cell cell9 = row.createCell(indexColumn++);
		cell9.setCellValue(new HSSFRichTextString("AMTIMA"));
		cell9.setCellStyle(styleHeader);
		
		indexColumn=0;
		Row row2 = sheet.createRow(index);
		row2.createCell(indexColumn++); //Seccional
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		row2.createCell(indexColumn++); //UOMA
				
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
		

		Cell cell23 = row2.createCell(indexColumn++);
		cell23.setCellValue(new HSSFRichTextString("OSPIM TITULARES"));
		cell23.setCellStyle(styleHeader);
		
		Cell cell26 = row2.createCell(indexColumn++);
		cell26.setCellValue(new HSSFRichTextString("OSPIM ADHERENTES"));
		cell26.setCellStyle(styleHeader);
		
		Cell cell24 = row2.createCell(indexColumn++);
		cell24.setCellValue(new HSSFRichTextString("CAPITAS TITU."));
		cell24.setCellStyle(styleHeader);

		Cell cell27 = row2.createCell(indexColumn++);
		cell27.setCellValue(new HSSFRichTextString("CAPITAS ADHE."));
		cell27.setCellStyle(styleHeader);
		
		Cell cell25 = row2.createCell(indexColumn++);
		cell25.setCellValue(new HSSFRichTextString("DEREGULADOS TITU."));
		cell25.setCellStyle(styleHeader);

		Cell cell28 = row2.createCell(indexColumn++);
		cell28.setCellValue(new HSSFRichTextString("DESREGULADOS ADHE."));
		cell28.setCellStyle(styleHeader);
		
		row2.createCell(indexColumn++); //AMTIMA		
		
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8));
		
		Cell cell10 = row.createCell(indexColumn++);
		cell10.setCellValue(new HSSFRichTextString("TOTAL TITULARES"));
		cell10.setCellStyle(styleHeader);	

		Cell cell11 = row.createCell(indexColumn++);
		cell11.setCellValue(new HSSFRichTextString("TOTAL ADHERENTES"));
		cell11.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 9, 9));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 10, 10));

		return index;
	}

	private static SXSSFWorkbook getReporte(List<ReportePadronResult> repo, BusquedaReportePadronFiltro filtro, boolean esVistaAdmifarm) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1, colNum=0;
		Sheet sheet = wb.createSheet("Hoja " + sh);

		CellStyle styleDate = getStyleDateWbs(wb);

		int index = 0;
		Row row1 = sheet.createRow(index);
		
		if (esVistaAdmifarm) {
		    createHeaderAdmifarm(row1);
		} else if (filtro.isVistaPrevencion()) {
			createTitulos(sheet, row1, filtro);

			index++;
			Row row2 = sheet.createRow(index);
			createHeaderTercerizadora(row2);
		}else{
			createHeader(row1);

		}
		
		if (repo != null) {
			for (ReportePadronResult r : repo) {
				index++;
				colNum = 0;
				if (index >= 1048576) {
					index = 0;
					sh++;
					sheet = wb.createSheet("Hoja " + sh);
					Row rowNew = sheet.createRow(index);

					if (esVistaAdmifarm) {
					    createHeaderAdmifarm(rowNew);
					} else if(filtro.isVistaPrevencion()){
						createHeaderTercerizadora(rowNew);
					}else{
						createHeader(rowNew);
					}

					index++;
				}
				Row rowI = sheet.createRow(index);
				if (esVistaAdmifarm) {
				    createReportePadronDetalleAdmifarm(colNum, styleDate, r, rowI);
				} else if(filtro.isVistaPrevencion()){
					createReportePadronDetalleTercerizadora(colNum, styleDate, r, rowI);
				}else{
					createReportePadronDetalle(colNum, styleDate, r, rowI );
				}
			}
		}
		
		index++;
		sheet.createRow(index);
		for(int j=0;j<50;j++){
			try {
				sheet.autoSizeColumn((short) j);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}	
		
		
		return wb;
	}

	private static void createTitulos(Sheet sheet, Row row, BusquedaReportePadronFiltro filtro) {
		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
		gmtMenos3.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int colNum = 0;
		
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("REPORTE PADRÓN - " +filtro.getDescripcionFiltros()) + "        Fecha Listado: " + sdf.format(gmtMenos3.getTime()));
		
		row.setHeight((short)550);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 45));
	}
	
	private static void createReportePadronDetalle(int colNum,
			CellStyle styleDate, ReportePadronResult r, Row rowI ) {
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty((r.getCuil_titular())));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuil()));
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getInte()));
		colNum++;
		Cell cell6 = rowI.createCell(colNum);
		cell6.setCellValue(r.getId_ospim());
		colNum++;
		Cell cell7 = rowI.createCell(colNum);
		cell7.setCellValue(r.getId_uoma());
		colNum++;
		Cell cell8 = rowI.createCell(colNum);
		cell8.setCellValue(r.getId_amtima());
		colNum++;
		
		Cell cell6Info = rowI.createCell(colNum);
		colNum++;
		if (r.getFecha_ospim() != null) {
			cell6Info.setCellValue(r.getFecha_ospim());
			cell6Info.setCellStyle(styleDate);
		} else {
			cell6Info.setCellValue(new HSSFRichTextString());
		}
		Cell cell7Info = rowI.createCell(colNum);
		if (r.getAlta_fecha() != null) {
			cell7Info.setCellValue(r.getAlta_fecha());
			cell7Info.setCellStyle(styleDate);
		} else {
			cell7Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getUnifica()));
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
					StringUtils.getValueOrEmpty(r.getSeccional()));			
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getId_tercerizadora()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getParentesco()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getApellido()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNombre()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocu_numero()));
		colNum++;
		Cell cell16Info = rowI.createCell(colNum);
		cell16Info.setCellValue(r.getNaci_fecha());
		cell16Info.setCellStyle(styleDate);
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSexo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCivil_esta()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNacionalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProvincia()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getLocalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPostal_codi()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCalle()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNumero()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPiso()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDepto()));
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getTelefono()));
//		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum, StringUtils.getValueOrEmpty(r.getTelefono1()));
//				StringUtils.getValueOrEmpty(r.getTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelLaboral()));
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEmail()));
		
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getUnsuscribeEmail()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCategoria()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPlan()));
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getPlanOmint()));
//		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanPrevencion())));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getFarmaciaPrevencion()));
		colNum++;
		
		Cell cell32Info = rowI.createCell(colNum);
		if (r.getIngre_fecha() != null) {
			cell32Info.setCellValue(r.getIngre_fecha());
			cell32Info.setCellStyle(styleDate);
		} else {
			cell32Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		Cell cell33Info = rowI.createCell(colNum);
		if (r.getBaja_fecha() != null) {
			cell33Info.setCellValue(r.getBaja_fecha());
			cell33Info.setCellStyle(styleDate);
		} else {
			cell33Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuit()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRazon_soc()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRamo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEscala_salarial()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDiscapacitado()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getMotivoBaja()));
		colNum++;
		
		if(r.getFecha_uoma()!=null){
			Cell cell40Info = rowI.createCell(colNum);
			cell40Info.setCellValue(r.getFecha_uoma());
			cell40Info.setCellStyle(styleDate);
		}else{
			CellUtil.createCell(rowI, colNum,"");
		}
		colNum++;
		if(r.getFecha_amtima()!=null){
			Cell cell41Info = rowI.createCell(colNum);
			cell41Info.setCellValue(r.getFecha_amtima());
			cell41Info.setCellStyle(styleDate);
		}else{
			CellUtil.createCell(rowI, colNum,"");
		}
		colNum++;				
		CellUtil.createCell(rowI, colNum, r.getPerteneceAlaOrganizacion()==1?"SI":"NO");
		colNum++;				
		CellUtil.createCell(rowI, colNum, StringUtils.getValueOrEmpty(r.getProyecto()));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getObraSocAnterior()));
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getTieneAntecedentesJudiciales()==1?"SI":"NO");
		/*colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroSocio() != 0 ? String.valueOf(r.getNroSocio()):""));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroCredencial() != null ? String.valueOf(r.getNroCredencial()):""));
		*/
		
	}
	
	private static void createReportePadronDetalleTercerizadora(int colNum,
			CellStyle styleDate, ReportePadronResult r, Row rowI) {

		
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty((r.getCuil_titular())));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuil()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getInte()));
		colNum++;
		Cell cell0 = rowI.createCell(colNum);
		cell0.setCellValue(r.getId_ospim());
		
		colNum++;
		Cell cell1 = rowI.createCell(colNum);
		if (r.getFecha_ospim() != null) {
			cell1.setCellValue(r.getFecha_ospim());
			cell1.setCellStyle(styleDate);
		} else {
			cell1.setCellValue(new HSSFRichTextString());
		}			
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdSeccional()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSeccional()));
		colNum++;	
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getId_parentesco_sss()));
		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getParentesco()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getApellido()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNombre()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocu_numero()));
		colNum++;
		Cell cell2 = rowI.createCell(colNum);
		cell2.setCellValue(r.getNaci_fecha());
		cell2.setCellStyle(styleDate);
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSexo()));		
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getId_estado_civil_sss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCivil_esta()));
		
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdNacionalidadSSS()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNacionalidad()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdProvinciaSss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProvincia()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdLocalidadSss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getLocalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPostal_codi()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCalle()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNumero()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPiso()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDepto()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelefono1()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEmail()));
		
//		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getUnsuscribeEmail()));
		
		colNum++;
		//CellUtil.createCell(rowI, colNum,
		//		StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanPrevencion())));
		
		CellUtil.createCell(rowI, colNum,
						StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanTercerizadora())));
		
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getFarmaciaPrevencion()));
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getFarmaciaTercerizadora()));
		
		colNum++;
		Cell cell5 = rowI.createCell(colNum);
		if (r.getVigenFecha() != null) {
			cell5.setCellValue(r.getVigenFecha());
			cell5.setCellStyle(styleDate);
		} else {
			cell5.setCellValue(new HSSFRichTextString());
		}
		
		/*
		Cell cell5 = rowI.createCell(colNum);
		if (r.getIngre_fecha() != null) {
			cell5.setCellValue(r.getIngre_fecha());
			cell5.setCellStyle(styleDate);
		} else {
			cell5.setCellValue(new HSSFRichTextString());
		}
		*/
		
		colNum++;
		Cell cell3 = rowI.createCell(colNum);
		if (r.getBaja_fecha() != null) {
			cell3.setCellValue(r.getBaja_fecha());
			cell3.setCellStyle(styleDate);
		} else {
			cell3.setCellValue(new HSSFRichTextString());
		}

		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuit()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRazon_soc()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDiscapacitado()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getIdMotivoBaja()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getMotivoBaja()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getPerteneceAlaOrganizacion()==1?"SI":"NO");
		
		
		colNum++;
		Cell cell6 = rowI.createCell(colNum);
		if (r.getFpp()!= null) {
			cell6.setCellValue(r.getFpp());
			cell6.setCellStyle(styleDate);
		} else {
			cell6.setCellValue(new HSSFRichTextString());
		}
        
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getCopago());
		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getCategoria());
		
/*		
		colNum++;			
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProyecto()));		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getObraSocAnterior()));		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getTieneAntecedentesJudiciales()==1?"SI":"NO");	
		
		// datos no encontrados en listado de padron 
		
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.isFarmaciaUoma()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.isFarmaciaAmtima()));		
		colNum++;
		Cell cell4 = rowI.createCell(colNum);
		if (r.getFpp() != null) {
			cell4.setCellValue(r.getFpp());
			cell4.setCellStyle(styleDate);
		} else {
			cell4.setCellValue(new HSSFRichTextString());
		}
		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroSocio() != 0 ? String.valueOf(r.getNroSocio()):""));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroCredencial() != null ? String.valueOf(r.getNroCredencial()):""));
*/		
	}
	
	private static void createHeader(Row row) {
		int colNum = 0;
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		Cell cell1 = row.createCell(colNum);
		colNum++;
		cell1.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell2 = row.createCell(colNum);
		colNum++;
		cell2.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell3 = row.createCell(colNum);
		colNum++;
		cell3.setCellValue(new HSSFRichTextString("ID_OSPIM"));
		Cell cell4 = row.createCell(colNum);
		colNum++;
		cell4.setCellValue(new HSSFRichTextString("ID_UOMA"));
		Cell cell5 = row.createCell(colNum);
		colNum++;
		cell5.setCellValue(new HSSFRichTextString("ID_AMTIMA"));
		Cell cell6 = row.createCell(colNum);
		colNum++;
		cell6.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
		Cell cell7 = row.createCell(colNum);
		colNum++;
		cell7.setCellValue(new HSSFRichTextString("FECHA REGISTRO"));
		Cell cell8 = row.createCell(colNum);
		colNum++;
		cell8.setCellValue(new HSSFRichTextString("UNIFICA"));
		Cell cell9 = row.createCell(colNum);
		colNum++;
		cell9.setCellValue(new HSSFRichTextString("SECCIONAL"));
		Cell cell10 = row.createCell(colNum);
		colNum++;
		cell10.setCellValue(new HSSFRichTextString("TERCERIZADORA"));
		Cell cell11 = row.createCell(colNum);
		colNum++;
		cell11.setCellValue(new HSSFRichTextString("PARENTESCO"));
		Cell cell12 = row.createCell(colNum);
		colNum++;
		cell12.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell13 = row.createCell(colNum);
		colNum++;
		cell13.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell14 = row.createCell(colNum);
		colNum++;
		cell14.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell15 = row.createCell(colNum);
		colNum++;
		cell15.setCellValue(new HSSFRichTextString("NRO DOC"));
		Cell cell16 = row.createCell(colNum);
		colNum++;
		cell16.setCellValue(new HSSFRichTextString("FECHA NAC"));
		Cell cell17 = row.createCell(colNum);
		colNum++;
		cell17.setCellValue(new HSSFRichTextString("SEXO"));
		Cell cell18 = row.createCell(colNum);
		colNum++;
		cell18.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		Cell cell19 = row.createCell(colNum);
		colNum++;
		cell19.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		Cell cell20 = row.createCell(colNum);
		colNum++;
		cell20.setCellValue(new HSSFRichTextString("PROVINCIA"));
		Cell cell21 = row.createCell(colNum);
		colNum++;
		cell21.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		Cell cell22 = row.createCell(colNum);
		colNum++;
		cell22.setCellValue(new HSSFRichTextString("CP"));
		Cell cell23 = row.createCell(colNum);
		colNum++;
		cell23.setCellValue(new HSSFRichTextString("CALLE"));
		Cell cell24 = row.createCell(colNum);
		colNum++;
		cell24.setCellValue(new HSSFRichTextString("NUMERO"));
		Cell cell25 = row.createCell(colNum);
		colNum++;
		cell25.setCellValue(new HSSFRichTextString("PISO"));
		Cell cell26 = row.createCell(colNum);
		colNum++;
		cell26.setCellValue(new HSSFRichTextString("DEPTO"));
//		Cell cell27 = row.createCell(colNum);
//		colNum++;
//		cell27.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell28_ = row.createCell(colNum);
		colNum++;
		cell28_.setCellValue(new HSSFRichTextString("COD.AREA TEL."));
		Cell cell29_ = row.createCell(colNum);
		colNum++;
		cell29_.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell30_ = row.createCell(colNum);
		colNum++;
		cell30_.setCellValue(new HSSFRichTextString("COD.AREA CELU."));
		Cell cell31_ = row.createCell(colNum);
		colNum++;
		cell31_.setCellValue(new HSSFRichTextString("CELULAR"));
		Cell cell32_ = row.createCell(colNum);
		colNum++;
		cell32_.setCellValue(new HSSFRichTextString("COD.AREA TEL.LABO."));
		Cell cell33_ = row.createCell(colNum);
		colNum++;
		cell33_.setCellValue(new HSSFRichTextString("TELEF. LABORAL"));
		
		Cell cell28 = row.createCell(colNum);
		colNum++;
		cell28.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		
		Cell cell281 = row.createCell(colNum);
		colNum++;
		cell281.setCellValue(new HSSFRichTextString("UNSUSCRIBE EMAIL"));
		
		Cell cell29 = row.createCell(colNum);
		colNum++;
		cell29.setCellValue(new HSSFRichTextString("CATEGORIA"));
		Cell cell30 = row.createCell(colNum);
		colNum++;
		cell30.setCellValue(new HSSFRichTextString("PLAN"));
//		Cell cell31 = row.createCell(colNum);
//		colNum++;
//		cell31.setCellValue(new HSSFRichTextString("PLAN OMINT"));
		Cell cell31a = row.createCell(colNum);
		colNum++;
		cell31a.setCellValue(new HSSFRichTextString("PLAN ENSALUD"));
		Cell cell31b = row.createCell(colNum);
		colNum++;
		cell31b.setCellValue(new HSSFRichTextString("FARMACIA ENSALUD"));
		Cell cell32 = row.createCell(colNum);
		colNum++;
		cell32.setCellValue(new HSSFRichTextString("FECHA PROCESO"));
		Cell cell33 = row.createCell(colNum);
		colNum++;
		cell33.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell34 = row.createCell(colNum);
		colNum++;
		cell34.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell35 = row.createCell(colNum);
		colNum++;
		cell35.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		Cell cell36 = row.createCell(colNum);
		colNum++;
		cell36.setCellValue(new HSSFRichTextString("RAMO"));
		Cell cell37 = row.createCell(colNum);
		colNum++;
		cell37.setCellValue(new HSSFRichTextString("ESCALA SALARIAL"));
		Cell cell38 = row.createCell(colNum);
		colNum++;
		cell38.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		Cell cell39 = row.createCell(colNum);
		colNum++;
		cell39.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		Cell cell40 = row.createCell(colNum);
		colNum++;
		cell40.setCellValue(new HSSFRichTextString("FECHA UOMA"));
		Cell cell41 = row.createCell(colNum);
		colNum++;
		cell41.setCellValue(new HSSFRichTextString("FECHA AMTIMA"));
		Cell cell42 = row.createCell(colNum);
		colNum++;
		cell42.setCellValue(new HSSFRichTextString("PERTENECE A LA ORG."));
		Cell cell43 = row.createCell(colNum);
		colNum++;
		cell43.setCellValue(new HSSFRichTextString("PROYECTO"));
		Cell cell44 = row.createCell(colNum);
		colNum++;
		cell44.setCellValue(new HSSFRichTextString("OOSS ANTERIOR"));
		Cell cell45 = row.createCell(colNum);
		colNum++;
		cell45.setCellValue(new HSSFRichTextString("ANTEC.JUDICIALES"));
		Cell cell46 = row.createCell(colNum);
		/*colNum++;
		cell46.setCellValue(new HSSFRichTextString("NRO SOCIO"));
		Cell cell47 = row.createCell(colNum);
		colNum++;
		cell47.setCellValue(new HSSFRichTextString("NRO CREDENCIAL PREVENCION"));
		*/
	}
	
	private static void createHeaderTercerizadora(Row row) {
	
		int colNum = 0;
		
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		Cell cell00 = row.createCell(colNum);
		colNum++;
		cell00.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell1 = row.createCell(colNum);
		colNum++;
		cell1.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell2 = row.createCell(colNum);
		colNum++;
		cell2.setCellValue(new HSSFRichTextString("ID_OSPIM"));
		Cell cell3 = row.createCell(colNum);
		colNum++;
		cell3.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
		
		
		Cell cell51 = row.createCell(colNum);
		colNum++;
		cell51.setCellValue(new HSSFRichTextString("ID SECCIONAL"));
		
		Cell cell4 = row.createCell(colNum);
		colNum++;
		cell4.setCellValue(new HSSFRichTextString("SECCIONAL"));
		
		Cell cell50 = row.createCell(colNum);
		colNum++;
		cell50.setCellValue(new HSSFRichTextString("ID PARENT."));
		
		Cell cell5 = row.createCell(colNum);
		colNum++;
		cell5.setCellValue(new HSSFRichTextString("PARENTESCO"));
		
		Cell cell6 = row.createCell(colNum);
		colNum++;
		cell6.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell7 = row.createCell(colNum);
		colNum++;
		cell7.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell8 = row.createCell(colNum);
		colNum++;
		cell8.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell9 = row.createCell(colNum);
		colNum++;
		cell9.setCellValue(new HSSFRichTextString("NRO DOC"));
		Cell cell10 = row.createCell(colNum);
		colNum++;
		cell10.setCellValue(new HSSFRichTextString("FECHA NAC"));
		Cell cell12 = row.createCell(colNum);
		colNum++;
		cell12.setCellValue(new HSSFRichTextString("SEXO"));
		
		
		Cell cell49 = row.createCell(colNum);
		colNum++;
		cell49.setCellValue(new HSSFRichTextString("ID EST.CIVIL"));
		
		Cell cell13 = row.createCell(colNum);
		colNum++;
		cell13.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		
		
		Cell cell45 = row.createCell(colNum);
		colNum++;
		cell45.setCellValue(new HSSFRichTextString("ID NACION."));
		
		Cell cell14 = row.createCell(colNum);
		colNum++;
		cell14.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		
		Cell cell46 = row.createCell(colNum);
		colNum++;
		cell46.setCellValue(new HSSFRichTextString("ID PROV."));
		
		Cell cell15 = row.createCell(colNum);
		colNum++;
		cell15.setCellValue(new HSSFRichTextString("PROVINCIA"));
		
		
		Cell cell47 = row.createCell(colNum);
		colNum++;
		cell47.setCellValue(new HSSFRichTextString("ID LOC."));
		
		
		Cell cell16 = row.createCell(colNum);
		colNum++;
		cell16.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		
		
		Cell cell17 = row.createCell(colNum);
		colNum++;
		cell17.setCellValue(new HSSFRichTextString("CP"));
		Cell cell18 = row.createCell(colNum);
		colNum++;
		cell18.setCellValue(new HSSFRichTextString("CALLE"));
		Cell cell19 = row.createCell(colNum);
		colNum++;
		cell19.setCellValue(new HSSFRichTextString("NUMERO"));
		Cell cell20 = row.createCell(colNum);
		colNum++;
		cell20.setCellValue(new HSSFRichTextString("PISO"));
		Cell cell21 = row.createCell(colNum);
		colNum++;
		cell21.setCellValue(new HSSFRichTextString("DEPTO"));
		Cell cell22 = row.createCell(colNum);
		colNum++;
		cell22.setCellValue(new HSSFRichTextString("COD.AREA TEL."));
		Cell cell23 = row.createCell(colNum);
		colNum++;
		cell23.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell24 = row.createCell(colNum);
		colNum++;
		cell24.setCellValue(new HSSFRichTextString("COD.AREA CELU."));
		Cell cell25 = row.createCell(colNum);
		colNum++;
		cell25.setCellValue(new HSSFRichTextString("CELULAR"));
		Cell cell26 = row.createCell(colNum);
		colNum++;
		cell26.setCellValue(new HSSFRichTextString("COD.AREA TEL.LABO."));
		Cell cell27 = row.createCell(colNum);
		colNum++;
		cell27.setCellValue(new HSSFRichTextString("TELEF. LABORAL"));
		Cell cell28 = row.createCell(colNum);
		colNum++;
		cell28.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		Cell cell281 = row.createCell(colNum);
		
		//colNum++;
		//cell281.setCellValue(new HSSFRichTextString("UNSUSCRIBE EMAIL"));
		
		Cell cell29 = row.createCell(colNum);
		colNum++;
		cell29.setCellValue(new HSSFRichTextString("PLAN TERCERIZADORA"));
		Cell cell30 = row.createCell(colNum);
		colNum++;
		cell30.setCellValue(new HSSFRichTextString("FARMACIA TERCERIZADORA"));
		Cell cell31 = row.createCell(colNum);
		colNum++;
		cell31.setCellValue(new HSSFRichTextString("FECHA VIGENCIA"));
		Cell cell32 = row.createCell(colNum);
		colNum++;
		cell32.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell33 = row.createCell(colNum);
		colNum++;
		cell33.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell34 = row.createCell(colNum);
		colNum++;
		cell34.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		Cell cell35 = row.createCell(colNum);
		colNum++;
		cell35.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		
		Cell cell48 = row.createCell(colNum);
		colNum++;
		cell48.setCellValue(new HSSFRichTextString("ID MOT. BAJA"));
		
		Cell cell36 = row.createCell(colNum);
		colNum++;
		cell36.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		Cell cell38 = row.createCell(colNum);
		colNum++;
		cell38.setCellValue(new HSSFRichTextString("PERTENECE A LA ORG."));
		Cell cell39 = row.createCell(colNum);
		cell39.setCellValue(new HSSFRichTextString("VTO. PMI"));
		colNum++;
		Cell cell40 = row.createCell(colNum);
		cell40.setCellValue(new HSSFRichTextString("COPAGO"));
		colNum++;
		Cell cell41 = row.createCell(colNum);
		cell41.setCellValue(new HSSFRichTextString("CATEGORIA"));
		
/*		
		colNum++;
		cell39.setCellValue(new HSSFRichTextString("PROYECTO"));
		Cell cell40 = row.createCell(colNum);
		colNum++;
		cell40.setCellValue(new HSSFRichTextString("OOSS ANTERIOR"));
		Cell cell41 = row.createCell(colNum);
		colNum++;
		cell41.setCellValue(new HSSFRichTextString("ANTEC.JUDICIALES"));
		Cell cell42 = row.createCell(colNum);
		colNum++;
		cell42.setCellValue(new HSSFRichTextString("FARMACIA AMTIMA"));
		Cell cell43 = row.createCell(colNum);
		colNum++;
		cell43.setCellValue(new HSSFRichTextString("FAMACIA UOMA"));
		Cell cell44 = row.createCell(colNum);
		colNum++;
		cell44.setCellValue(new HSSFRichTextString("PMI"));
		Cell cell52 = row.createCell(colNum);
		colNum++;
		cell52.setCellValue(new HSSFRichTextString("NRO SOCIO"));
		Cell cell53 = row.createCell(colNum);
		colNum++;
		cell53.setCellValue(new HSSFRichTextString("NRO CREDENCIAL PREVENCION"));
*/		
		
		
		
	}

	private static String getValue(String o) {
		if (o != null) {
			return o;
		} else {
			return "";
		}
	}
	
	
	private static SXSSFWorkbook getReporteBajas(List<ReportePadronResult> repo) {
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1;
		Sheet sheet = wb.createSheet("Hoja " + sh);

		CellStyle styleDate = getStyleDateWbs(wb);

		int index = 0;
		Row row = sheet.createRow(index);
		createHeaderBajas(row);
		if (repo != null) {
			for (ReportePadronResult r : repo) {
				index++;
				if (index >= 1048576) {
					index = 0;
					sh++;
					sheet = wb.createSheet("Hoja " + sh);
					Row rowNew = sheet.createRow(index);
					createHeaderBajas(rowNew);
				}
				int col = 0;
				Row rowI = sheet.createRow(index);
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty((r.getCuil_titular())));
//				CellUtil.createCell(rowI, 1,
//						StringUtils.getValueOrEmpty(r.getCuil()));
				CellUtil.createCell(rowI, col++, String.valueOf(r.getInte()));
				
				Cell cell6 = rowI.createCell(col++);
				cell6.setCellValue(r.getId_ospim());
				
//				Cell cell7 = rowI.createCell(col++);
//				cell7.setCellValue(r.getId_uoma());
//				
//				Cell cell8 = rowI.createCell(col++);
//				cell8.setCellValue(r.getId_amtima());
								
				Cell cell6Info = rowI.createCell(col++);
				if (r.getFecha_ospim() != null) {
					cell6Info.setCellValue(r.getFecha_ospim());
					cell6Info.setCellStyle(styleDate);
				} else {
					cell6Info.setCellValue(new HSSFRichTextString());
				}
				
//				Cell cell7Info = rowI.createCell(col++);
//				if (r.getFecha_uoma() != null) {
//					cell7Info.setCellValue(r.getFecha_uoma());
//					cell7Info.setCellStyle(styleDate);
//				} else {
//					cell7Info.setCellValue(new HSSFRichTextString());
//				}
//				
//				Cell cell8Info = rowI.createCell(col++);
//				if (r.getFecha_amtima() != null) {
//					cell8Info.setCellValue(r.getFecha_amtima());
//					cell8Info.setCellStyle(styleDate);
//				} else {
//					cell8Info.setCellValue(new HSSFRichTextString());
//				}
				
				Cell cell9Info = rowI.createCell(col++);
				if (r.getFecha_proceso() != null) {
					cell9Info.setCellValue(r.getFecha_proceso() /*Alta_fecha()*/);
					cell9Info.setCellStyle(styleDate);
				} else {
					cell9Info.setCellValue(new HSSFRichTextString());
				}
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getUnifica()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getSeccional()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getId_tercerizadora()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getParentesco()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getApellido()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getNombre()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getDocu_numero()));
//				Cell cell18Info = rowI.createCell(col++);
//				cell18Info.setCellValue(r.getNaci_fecha());
//				cell18Info.setCellStyle(styleDate);
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getSexo()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getCivil_esta()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getNacionalidad()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getProvincia()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getLocalidad()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getPostal_codi()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getCalle()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getNumero()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getPiso()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getDepto()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getTelefono()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getEmail()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getCategoria()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getPlan()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getPlanOmint()));
//				Cell cell34Info = rowI.createCell(col++);
//				if (r.getIngre_fecha() != null) {
//					cell34Info.setCellValue(r.getIngre_fecha());
//					cell34Info.setCellStyle(styleDate);
//				} else {
//					cell34Info.setCellValue(new HSSFRichTextString());
//				}
				Cell cell35Info = rowI.createCell(col++);
				if (r.getBaja_fecha() != null) {
					cell35Info.setCellValue(r.getBaja_fecha());
					cell35Info.setCellStyle(styleDate);
				} else {
					cell35Info.setCellValue(new HSSFRichTextString());
				}
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getCuit()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getRazon_soc()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getRamo()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getEscala_salarial()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getDiscapacitado()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getMotivoBaja()));
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
		
		return wb;
	}

	private static void createHeaderBajas(Row row) {
		int col = 0;
		Cell cell0 = row.createCell(col++);
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
//		Cell cell1 = row.createCell(col++);
//		cell1.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell2 = row.createCell(col++);
		cell2.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell3 = row.createCell(col++);
		cell3.setCellValue(new HSSFRichTextString("ID_OSPIM"));
//		Cell cell4 = row.createCell(col++);
//		cell4.setCellValue(new HSSFRichTextString("ID_UOMA"));
//		Cell cell5 = row.createCell(col++);
//		cell5.setCellValue(new HSSFRichTextString("ID_AMTIMA"));
		Cell cell6 = row.createCell(col++);
		cell6.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
//		Cell cell7 = row.createCell(col++);
//		cell7.setCellValue(new HSSFRichTextString("FECHA UOMA"));
//		Cell cell8 = row.createCell(col++);
//		cell8.setCellValue(new HSSFRichTextString("FECHA AMTIMA"));
		Cell cell9 = row.createCell(col++);
		cell9.setCellValue(new HSSFRichTextString("FECHA PROCESO"));
//		Cell cell10 = row.createCell(col++);
//		cell10.setCellValue(new HSSFRichTextString("UNIFICA"));
		Cell cell11 = row.createCell(col++);
		cell11.setCellValue(new HSSFRichTextString("SECCIONAL"));
//		Cell cell12 = row.createCell(col++);
//		cell12.setCellValue(new HSSFRichTextString("TERCERIZADORA"));
		Cell cell13 = row.createCell(col++);
		cell13.setCellValue(new HSSFRichTextString("PARENTESCO"));
		Cell cell14 = row.createCell(col++);
		cell14.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell15 = row.createCell(col++);
		cell15.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell16 = row.createCell(col++);
		cell16.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell17 = row.createCell(col++);
		cell17.setCellValue(new HSSFRichTextString("NRO DOC"));
//		Cell cell18 = row.createCell(col++);
//		cell18.setCellValue(new HSSFRichTextString("FECHA NAC"));
//		Cell cell19 = row.createCell(col++);
//		cell19.setCellValue(new HSSFRichTextString("SEXO"));
//		Cell cell20 = row.createCell(col++);
//		cell20.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
//		Cell cell21 = row.createCell(col++);
//		cell21.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
//		Cell cell22 = row.createCell(col++);
//		cell22.setCellValue(new HSSFRichTextString("PROVINCIA"));
//		Cell cell23 = row.createCell(col++);
//		cell23.setCellValue(new HSSFRichTextString("LOCALIDAD"));
//		Cell cell24 = row.createCell(col++);
//		cell24.setCellValue(new HSSFRichTextString("CP"));
//		Cell cell25 = row.createCell(col++);
//		cell25.setCellValue(new HSSFRichTextString("CALLE"));
//		Cell cell26 = row.createCell(col++);
//		cell26.setCellValue(new HSSFRichTextString("NUMERO"));
//		Cell cell27 = row.createCell(col++);
//		cell27.setCellValue(new HSSFRichTextString("PISO"));
//		Cell cell28 = row.createCell(col++);
//		cell28.setCellValue(new HSSFRichTextString("DEPTO"));
//		Cell cell29 = row.createCell(col++);
//		cell29.setCellValue(new HSSFRichTextString("TELEFONO"));
//		Cell cell30 = row.createCell(col++);
//		cell30.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		Cell cell31 = row.createCell(col++);
		cell31.setCellValue(new HSSFRichTextString("CATEGORIA"));
		Cell cell32 = row.createCell(col++);
		cell32.setCellValue(new HSSFRichTextString("PLAN"));
		Cell cell33 = row.createCell(col++);
		cell33.setCellValue(new HSSFRichTextString("PLAN OMINT"));
//		Cell cell34 = row.createCell(col++);
//		cell34.setCellValue(new HSSFRichTextString("FECHA INGRESO"));
		Cell cell35 = row.createCell(col++);
		cell35.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell36 = row.createCell(col++);
		cell36.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell37 = row.createCell(col++);
		cell37.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
//		Cell cell38 = row.createCell(col++);
//		cell38.setCellValue(new HSSFRichTextString("RAMO"));
//		Cell cell39 = row.createCell(col++);
//		cell39.setCellValue(new HSSFRichTextString("ESCALA SALARIAL"));
//		Cell cell40 = row.createCell(col++); 
//		cell40.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		Cell cell41 = row.createCell(col++);
		cell41.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
	}

	private static void createHeaderAdmifarm(Row row) {

	    int colNum = 0;

	    CellUtil.createCell(row, colNum++, "TIPO DOC");
	    CellUtil.createCell(row, colNum++, "NRO DOC");
	    CellUtil.createCell(row, colNum++, "NUMERO AFILIADO");
	    CellUtil.createCell(row, colNum++, "CATEGORIA");
	    CellUtil.createCell(row, colNum++, "PARENTESCO");
	    CellUtil.createCell(row, colNum++, "APELLIDO");
	    CellUtil.createCell(row, colNum++, "NOMBRE");
	    CellUtil.createCell(row, colNum++, "FECHA NAC");
	    CellUtil.createCell(row, colNum++, "SEXO");
	    CellUtil.createCell(row, colNum++, "ESTADO CIVIL");
	    CellUtil.createCell(row, colNum++, "NACIONALIDAD");
	    CellUtil.createCell(row, colNum++, "PROVINCIA");
	    CellUtil.createCell(row, colNum++, "LOCALIDAD");
	    CellUtil.createCell(row, colNum++, "CP");
	    CellUtil.createCell(row, colNum++, "CALLE");
	    CellUtil.createCell(row, colNum++, "NUMERO");
	    CellUtil.createCell(row, colNum++, "PISO");
	    CellUtil.createCell(row, colNum++, "DEPTO");
	    CellUtil.createCell(row, colNum++, "COD.AREA TEL.");
	    CellUtil.createCell(row, colNum++, "TELEFONO");
	    CellUtil.createCell(row, colNum++, "COD.AREA CELU.");
	    CellUtil.createCell(row, colNum++, "CELULAR");
	    CellUtil.createCell(row, colNum++, "COD.AREA TEL.LABO.");
	    CellUtil.createCell(row, colNum++, "TELEF. LABORAL");
	    CellUtil.createCell(row, colNum++, "CORREO ELECTRONICO");
	    CellUtil.createCell(row, colNum++, "PLAN TERCERIZADORA");
	    CellUtil.createCell(row, colNum++, "FARMACIA TERCERIZADORA");
	    CellUtil.createCell(row, colNum++, "FECHA VIGENCIA");
	    CellUtil.createCell(row, colNum++, "FECHA BAJA");
	    CellUtil.createCell(row, colNum++, "CUIT");
	    CellUtil.createCell(row, colNum++, "RAZON SOCIAL");
	    CellUtil.createCell(row, colNum++, "DISCAPACITADO");
	    CellUtil.createCell(row, colNum++, "ID MOT. BAJA");
	    CellUtil.createCell(row, colNum++, "MOTIVO BAJA");
	    CellUtil.createCell(row, colNum++, "PERTENECE A LA ORG.");
	    CellUtil.createCell(row, colNum++, "VTO. PMI");
	    CellUtil.createCell(row, colNum++, "COPAGO");
	    CellUtil.createCell(row, colNum++, "CATEGORIA");
	    CellUtil.createCell(row, colNum++, "CUIL TITULAR");
	    CellUtil.createCell(row, colNum++, "CUIL");
	    CellUtil.createCell(row, colNum++, "FECHA OSPIM");
	    CellUtil.createCell(row, colNum++, "SECCIONAL");
	    CellUtil.createCell(row, colNum++, "PLAN AFILIADO");
	    CellUtil.createCell(row, colNum++, "PMI");
	    CellUtil.createCell(row, colNum++, "ACO");
	}

	private static void createReportePadronDetalleAdmifarm(int colNum, CellStyle styleDate,
			ReportePadronResult r, Row rowI) {

	    // TIPO DOC
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDocumento_tipo()));

	    // NRO DOC
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDocu_numero()));

	    // NUMERO AFILIADO
	    CellUtil.createCell(rowI, colNum++, String.valueOf(r.getId_ospim()));

	    // CATEGORIA
	    CellUtil.createCell(rowI, colNum++, String.valueOf(r.getInte()));

	    // PARENTESCO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getParentesco()));

	    // APELLIDO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getApellido()));

	    // NOMBRE
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNombre()));

	    // FECHA NAC
	    Cell fechaNac = rowI.createCell(colNum++);
	    if (r.getNaci_fecha() != null) {
	        fechaNac.setCellValue(r.getNaci_fecha());
	        fechaNac.setCellStyle(styleDate);
	    } else {
	        fechaNac.setCellValue("");
	    }

	    // SEXO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getSexo()));

	    // ESTADO CIVIL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCivil_esta()));

	    // NACIONALIDAD
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNacionalidad()));

	    // PROVINCIA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getProvincia()));

	    // LOCALIDAD
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getLocalidad()));

	    // CP
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getPostal_codi()));

	    // CALLE
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCalle()));

	    // NUMERO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNumero()));

	    // PISO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getPiso()));

	    // DEPTO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDepto()));

	    // COD.AREA TEL.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));

	    // TELEFONO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getTelefono1()));

	    // COD.AREA CELU.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaCelular()));

	    // CELULAR
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCelular()));

	    // COD.AREA TEL.LABO.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));

	    // TELEF. LABORAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getTelLaboral()));

	    // CORREO ELECTRONICO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getEmail()));

	    // PLAN TERCERIZADORA
	    String planTercerizadora = StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanTercerizadora()));

	    CellUtil.createCell(rowI, colNum++, planTercerizadora);

	    // FARMACIA TERCERIZADORA
	    String farmaciaTercerizadora =StringUtils.getValueOrEmpty(r.getFarmaciaTercerizadora());

	    CellUtil.createCell(rowI, colNum++, farmaciaTercerizadora);

	    // FECHA VIGENCIA
	    Cell fechaVigencia = rowI.createCell(colNum++);
	    if (r.getVigenFecha() != null) {
	        fechaVigencia.setCellValue(r.getVigenFecha());
	        fechaVigencia.setCellStyle(styleDate);
	    } else {
	        fechaVigencia.setCellValue("");
	    }

	    // FECHA BAJA
	    Cell fechaBaja = rowI.createCell(colNum++);
	    if (r.getBaja_fecha() != null) {
	        fechaBaja.setCellValue(r.getBaja_fecha());
	        fechaBaja.setCellStyle(styleDate);
	    } else {
	        fechaBaja.setCellValue("");
	    }

	    // CUIT
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuit()));

	    // RAZON SOCIAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getRazon_soc()));

	    // DISCAPACITADO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDiscapacitado()));

	    // ID MOT. BAJA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getIdMotivoBaja()));

	    // MOTIVO BAJA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getMotivoBaja()));

	    // PERTENECE A LA ORG.
	    CellUtil.createCell(rowI, colNum++, r.getPerteneceAlaOrganizacion() == 1 ? "SI" : "NO");

	    // VTO. PMI
	    Cell fechaPmi = rowI.createCell(colNum++);
	    if (r.getFpp() != null) {
	        fechaPmi.setCellValue(r.getFpp());
	        fechaPmi.setCellStyle(styleDate);
	    } else {
	        fechaPmi.setCellValue("");
	    }

	    // COPAGO
	    CellUtil.createCell(rowI, colNum++, r.getCopago());

	    // CATEGORIA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCategoria()));

	    // CUIL TITULAR
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuil_titular()));

	    // CUIL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuil()));

	    // FECHA OSPIM
	    Cell fechaOspim = rowI.createCell(colNum++);
	    if (r.getFecha_ospim() != null) {
	        fechaOspim.setCellValue(r.getFecha_ospim());
	        fechaOspim.setCellStyle(styleDate);
	    } else {
	        fechaOspim.setCellValue("");
	    }

	    // SECCIONAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getSeccional()));

	    // PLAN AFILIADO = PLAN TERCERIZADORA + FARMACIA TERCERIZADORA
	    String planAfiliado = (planTercerizadora + " " + farmaciaTercerizadora).trim();

	    CellUtil.createCell(rowI, colNum++, planAfiliado);

	    // PMI
	    CellUtil.createCell(rowI, colNum++, "");

	    // ACO
	    String aco = "";

	    if ("F".equalsIgnoreCase(r.getSexo()) && r.getNaci_fecha() != null) {
	        int edad = calcularEdad(r.getNaci_fecha());

	        if (edad >= 15 && edad <= 60) {
	            aco = "S";
	        }
	    }

	    CellUtil.createCell(rowI, colNum++, aco);

	}

	private static int calcularEdad(Date fechaNacimiento) {
	    Calendar nacimiento = Calendar.getInstance();
	    nacimiento.setTime(fechaNacimiento);
	    Calendar hoy = Calendar.getInstance();
	    int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

	    if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
	        edad--;
	    }

	    return edad;
	}

}
||||||| .r7319
package ar.com.ospim.afiliados.reportes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;
import ar.com.ospim.afiliados.reportes.beans.ReportePadronTotalResult;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.StringUtils;

public class ReporteListadoPadron extends ReporteXLS {

	private static Logger _log = Logger.getLogger(ReporteListadoPadron.class);

	private static final String SEPARATOR = ";";

	public static void getReporte(HttpServletRequest req, HttpServletResponse res, 
			ZipOutputStream out) throws IOException {
		
		BusquedaReportePadronFiltro filtro = getFiltrosPadron(req, res);

		List<ReportePadronResult> list = procesaPadron(filtro);
		
		int sh = 1;
		int index = 0;
		out.putNextEntry(new ZipEntry(sh + ".csv"));
		out.write(createHeader().getBytes());
		for (ReportePadronResult r : list) {
			index++;
			if (index >= 1048576) {
				index = 0;
				sh++;
				out.putNextEntry(new ZipEntry(sh + ".csv"));
				out.write(createHeader().getBytes());
			}

			String line = getLine(r);
			out.write(line.getBytes());
		}
	}

	private static String getLine(ReportePadronResult r) {
		StringBuilder str = new StringBuilder();
		str.append(r.getCuil_titular());
		str.append(SEPARATOR);
		str.append(r.getCuil());
		str.append(SEPARATOR);
		str.append(r.getInte());
		str.append(SEPARATOR);
		str.append(r.getId_ospim() != 0 ? String.valueOf(r.getId_ospim()) : "");
		str.append(SEPARATOR);
		str.append(r.getId_uoma() != 0 ? String.valueOf(r.getId_uoma()) : "");
		str.append(SEPARATOR);
		str.append(r.getId_amtima() != 0 ? String.valueOf(r.getId_amtima())
				: "");
		str.append(SEPARATOR);
		if (r.getFecha_ospim() != null) {
			str.append(r.getFecha_ospim());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		if (r.getAlta_fecha() != null) {
			str.append(r.getAlta_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		str.append(r.getUnifica());
		str.append(SEPARATOR);
		str.append(r.getSeccional());
		str.append(SEPARATOR);
		str.append(r.getId_tercerizadora());
		str.append(SEPARATOR);
		str.append(r.getParentesco());
		str.append(SEPARATOR);
		str.append(r.getApellido());
		str.append(SEPARATOR);
		str.append(r.getNombre());
		str.append(SEPARATOR);
		str.append(r.getDocumento_tipo());
		str.append(SEPARATOR);
		str.append(r.getDocu_numero());
		str.append(SEPARATOR);
		str.append(r.getNaci_fecha());
		str.append(SEPARATOR);
		str.append(r.getSexo());
		str.append(SEPARATOR);
		str.append(r.getCivil_esta());
		str.append(SEPARATOR);
		str.append(r.getNacionalidad());
		str.append(SEPARATOR);
		str.append(r.getProvincia());
		str.append(SEPARATOR);
		str.append(r.getLocalidad());
		str.append(SEPARATOR);
		str.append(r.getPostal_codi());
		str.append(SEPARATOR);
		str.append(r.getCalle());
		str.append(SEPARATOR);
		str.append(getValue(r.getNumero()));
		str.append(SEPARATOR);
		str.append(getValue(r.getPiso()));
		str.append(SEPARATOR);
		str.append(getValue(r.getDepto()));
		str.append(SEPARATOR);
		str.append(getValue(r.getTelefono()));
		str.append(SEPARATOR);
		str.append(getValue(r.getEmail()));
		str.append(SEPARATOR);
		str.append(r.getCategoria());
		str.append(SEPARATOR);
		str.append(r.getPlan());
		str.append(SEPARATOR);
		if (r.getIngre_fecha() != null) {
			str.append(r.getIngre_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		if (r.getBaja_fecha() != null) {
			str.append(r.getBaja_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		str.append(r.getCuit());
		str.append(SEPARATOR);
		str.append(r.getRazon_soc());
		str.append(SEPARATOR);
		str.append(r.getRamo());
		str.append(SEPARATOR);
		str.append(r.getEscala_salarial());
		str.append(SEPARATOR);
		str.append(r.getDiscapacitado());
		str.append("\n");
		return str.toString();
	}

	private static String createHeader() {
		StringBuilder str = new StringBuilder();
		str.append("CUIL TITULAR");
		str.append(SEPARATOR);
		str.append("CUIL");
		str.append(SEPARATOR);
		str.append("INTE");
		str.append(SEPARATOR);
		str.append("ID_OSPIM");
		str.append(SEPARATOR);
		str.append("ID_UOMA");
		str.append(SEPARATOR);
		str.append("ID_AMTIMA");
		str.append(SEPARATOR);
		str.append("FECHA OSPIM");
		str.append(SEPARATOR);
		str.append("FECHA_REGISTRO");
		str.append(SEPARATOR);
		str.append("UNIFICA");
		str.append(SEPARATOR);
		str.append("SECCIONAL");
		str.append(SEPARATOR);
		str.append("TERCERIZADORA");
		str.append(SEPARATOR);
		str.append("PARENTESCO");
		str.append(SEPARATOR);
		str.append("APELLIDO");
		str.append(SEPARATOR);
		str.append("NOMBRE");
		str.append(SEPARATOR);
		str.append("TIPO DOC");
		str.append(SEPARATOR);
		str.append("NRO DOC");
		str.append(SEPARATOR);
		str.append("FECHA NAC");
		str.append(SEPARATOR);
		str.append("SEXO");
		str.append(SEPARATOR);
		str.append("ESTADO CIVIL");
		str.append(SEPARATOR);
		str.append("NACIONALIDAD");
		str.append(SEPARATOR);
		str.append("PROVINCIA");
		str.append(SEPARATOR);
		str.append("LOCALIDAD");
		str.append(SEPARATOR);
		str.append("CP");
		str.append(SEPARATOR);
		str.append("CALLE");
		str.append(SEPARATOR);
		str.append("NUMERO");
		str.append(SEPARATOR);
		str.append("PISO");
		str.append(SEPARATOR);
		str.append("DEPTO");
		str.append(SEPARATOR);
		str.append("TELEFONO");
		str.append(SEPARATOR);
		str.append("CORREO ELECTRONICO");
		str.append(SEPARATOR);
		str.append("CATEGORIA");
		str.append(SEPARATOR);
		str.append("PLAN");
		str.append(SEPARATOR);
		str.append("FECHA PLAN");
		str.append(SEPARATOR);
		str.append("FECHA BAJA");
		str.append(SEPARATOR);
		str.append("CUIT");
		str.append(SEPARATOR);
		str.append("RAZON SOCIAL");
		str.append(SEPARATOR);
		str.append("RAMO");
		str.append(SEPARATOR);
		str.append("ESCALA SALARIAL");
		str.append(SEPARATOR);
		str.append("DISCAPACITADO");
		str.append("\n");
		return str.toString();
	}
	
	public static List<ReportePadronTotalResult> getTotalesEntidad(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronTotalResult> repoTotales = null;

		try {
			repoTotales=ReportesAfiliadoServiceUtil.getReportePadronTotalesEntidad(filtro.getFechaDesde(), 
					filtro.getIdsTercerizadora(), filtro);
			
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return repoTotales;
	}

	public static List<ReportePadronTotalResult> generaPadronTotales(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronTotalResult> repoTotales = null;
	
		if (filtro.isTotalesPorTercerizadora() || filtro.isTotalesPorPlan() 
				|| filtro.isTotalesPorSeccional() || filtro.isTotalesPorEmpresa()) {
			
			try {
				repoTotales = ReportesAfiliadoServiceUtil.getReportePadronTotales(filtro);
			} catch (SystemException e) {
				_log.error(e);
			}

		}
		
		return repoTotales;
	}

	public static BusquedaReportePadronFiltro getFiltrosPadron(HttpServletRequest req, HttpServletResponse res) {
		
		BusquedaReportePadronFiltro filtro = new BusquedaReportePadronFiltro();
		
		String fechaDesdeDia = req.getParameter("fechaDesdeDia");
		String fechaDesdeMes = req.getParameter("fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
		String fechaHastaDia = req.getParameter("fechaHastaDia");
		String fechaHastaMes = req.getParameter("fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = req.getParameter("fechaHastaAnio");
//		String fechaProcDesdeDia = req.getParameter("fechaProcDesdeDia");
//		String fechaProcDesdeMes = req.getParameter("fechaProcDesdeMes");
//		fechaProcDesdeMes = String.valueOf(Integer.valueOf(fechaProcDesdeMes) + 1);
//		String fechaProcDesdeAnio = req.getParameter("fechaProcDesdeAnio");
//		String fechaProcHastaDia = req.getParameter("fechaProcHastaDia");
//		String fechaProcHastaMes = req.getParameter("fechaProcHastaMes");
//		fechaProcHastaMes = String.valueOf(Integer.valueOf(fechaProcHastaMes) + 1);
//		String fechaProcHastaAnio = req.getParameter("fechaProcHastaAnio");
		String cuit = req.getParameter("cuit");
		String sucursal = req.getParameter("sucursal");
		String razonSocial = req.getParameter("razonSocial");
		String edadIni = req.getParameter("edadIni");
		String edadFin = req.getParameter("edadFin");
		String tituYFliares = req.getParameter("tituYFliares");
		String tituYFliaresDesc = req.getParameter("tituYFliaresDesc");
		
		String idsTercerizadoras = req.getParameter("idTercerizadora");
		String descTercerizadoras = "Todas";
		if (null==idsTercerizadoras || idsTercerizadoras.equals("null") || idsTercerizadoras.trim().length()==0) {
			idsTercerizadoras = null;
		}else{
			idsTercerizadoras += ",";
			descTercerizadoras = req.getParameter("descTercerizadora");
		}
		String idLoca = req.getParameter("idLoca");
		String descLocalidades = "Todas";
		if (null==idLoca || idLoca.equals("null") || idLoca.trim().length()==0) {
			idLoca = null;
		}else{
			idLoca += ",";
			descLocalidades = req.getParameter("descLocalidades");
		}
		String idProv = req.getParameter("idProv");
		String descProvincias = "Todas";
		if (null==idProv || idProv.equals("null") || idProv.trim().length()==0) {
			idProv = null;
		}else{
			idProv += ",";
			descProvincias = req.getParameter("descProvincias");
		}
		String idPlan = req.getParameter("idPlan");
		String descPlanes = "Todos";
		if (null==idPlan || idPlan.equals("null") || idPlan.trim().length()==0) {
			idPlan = null;
		}else{
			idPlan += ",";
			descPlanes = req.getParameter("descPlanes");
		}
		
		String tipoAportes = ParamUtil.getString(req, "tipoAporte");
		String descTiposAporte = "Todos";
		if (null==tipoAportes || tipoAportes.equals("null") || tipoAportes.trim().length()==0) {
			tipoAportes = null;
		}else{
			tipoAportes += ",";
			descTiposAporte = req.getParameter("descTiposAporte");
		}
		
		String parentesco = req.getParameter("parentesco");
		String descParentesco = "Todos";
		Integer idParentescoSss = null;
		try{
			idParentescoSss = Integer.parseInt(parentesco);
			descParentesco = req.getParameter("descParentesco");
		}catch (NumberFormatException e) {
			idParentescoSss = null;
		}
		
		String idSeccional = req.getParameter("idSeccional");
		String descSeccionales = "Todas";
		if (null==idSeccional || idSeccional.equals("null") || idSeccional.trim().length()==0) {
			idSeccional = null;
		}else{
			idSeccional += ",";
			descSeccionales = req.getParameter("descSeccionales");
		}
		String escalaSalarial = req.getParameter("escala_salarial");
		String motivoBajaIds = req.getParameter("idsMotivoBaja");
		String motivosBajaDesc = "Todos";

		if (null==motivoBajaIds || motivoBajaIds.equals("null") || motivoBajaIds.trim().length()==0) {
			motivoBajaIds = null;
		}else{
			motivoBajaIds += ",";
			motivosBajaDesc = req.getParameter("motivosBajaDesc");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		int tipoBusqueda= ParamUtil.getInteger(req,"tipoBusqueda");
		String descTipoBusqueda= req.getParameter("descTipoBusqueda");
		String proyecto = req.getParameter("proyecto");
		if(StringUtils.checkEmpty(proyecto)){
			proyecto = null;
		}
		boolean esExportaTercerizadora= ParamUtil.getBoolean(req, "vistaTercerizadora");
		
		if(esExportaTercerizadora) {
			idPlan="";
			List<Plan> pls=TraeListasServiceUtil.getPlanesSoloOspim();
			for(Plan p:pls) {
				idPlan+=p.getId()+",";
			}
//			idPlan=idPlan.substring(0,idPlan.length()-1);
			
		}
		
		
		Date fechaDsd=null, fechaHta = null;
		Date fechaNacimIni = null, fechaNacimFin = null;
		
		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			
			fechaDsd = fechaIni;
			fechaHta = fechaFin;
			
//			Date fechaProcDesde = format.parse(fechaProcDesdeDia + "-" + fechaProcDesdeMes
//					+ "-" + fechaProcDesdeAnio);
//			Date fechaProcHasta = format.parse(fechaProcHastaDia + "-" + fechaProcHastaMes
//					+ "-" + fechaProcHastaAnio);
						
//			la fecha inicio debe considerar desde el 1 de enero, y la fecha hasta el 31 de diciembre
			if(!StringUtils.checkEmpty(edadIni) && !StringUtils.checkEmpty(edadFin)){

				Calendar fin = Calendar.getInstance();
				if(Integer.parseInt(edadIni) > 0){ 
					fin.setTime(fechaFin);
					fin.add(Calendar.YEAR, -1 * Integer.valueOf(edadFin));
				}else{
					fin.setTime(fechaFin);
				}
				fechaNacimFin = fin.getTime();
				
				Calendar ini = Calendar.getInstance();
				ini.setTime(fechaIni);
				if(Integer.parseInt(edadIni) == 0){ 
					edadIni = "1";
				}
				ini.add(Calendar.YEAR, -1 * Integer.valueOf(edadIni));
				fechaNacimIni = ini.getTime();
				
				if(fechaNacimIni.after(fechaNacimFin)){
					Date auxCambia = null;
					auxCambia = fechaNacimFin;
					fechaNacimFin = fechaNacimIni;
					fechaNacimIni = auxCambia;
				}
			}
		} catch (ParseException e) {
			_log.error(e);
		}
		boolean totalesPorTercerizadora = ParamUtil.getBoolean(req,"total_tercerizadora");
		boolean totalesPorPlan = ParamUtil.getBoolean(req, "total_plan");
		boolean totalesPorSeccional = ParamUtil.getBoolean(req, "total_seccional");
		boolean totalesPorEmpresa = ParamUtil.getBoolean(req, "total_empresa");
		boolean totalesPorEntidad = ParamUtil.getBoolean(req, "total_entidad");
		
		filtro.setCategoriaUoma(escalaSalarial);
		filtro.setCodigosAportes(tipoAportes);
		filtro.setCodigosLocalidad(idLoca);
		filtro.setCodigosPlan(idPlan);
		filtro.setCodigosProvincia(idProv);
		filtro.setCodigosSeccional(idSeccional);
		filtro.setCuit(cuit);
		filtro.setDescAportes(descTiposAporte);
		filtro.setDescBusqueda(descTipoBusqueda);
		filtro.setDescLocalidad(descLocalidades);
		filtro.setDescMotivoBaja(motivosBajaDesc); 
		filtro.setDescPlan(descPlanes);
		filtro.setDescProvincia(descProvincias);
		filtro.setDescSeccional(descSeccionales);
		filtro.setDescTercerizadora(descTercerizadoras);
		try{
			filtro.setEdadFinal(Integer.parseInt(edadFin));
		}catch (Exception e) {
			filtro.setEdadFinal(0);
		}
		try{
			filtro.setEdadInicial(Integer.parseInt(edadIni));
		}catch (Exception e) {
			filtro.setEdadInicial(0);
		}
		filtro.setFechaDesde(fechaDsd);
		filtro.setFechaHasta(fechaHta);
		filtro.setFechaNacimIni(fechaNacimIni);
		filtro.setFechaNacimFin(fechaNacimFin);
		filtro.setIdsMotivoBaja(motivoBajaIds );
		filtro.setIdsTercerizadora(idsTercerizadoras);
		filtro.setParentescoDesc(descParentesco);
		filtro.setParentescoId(idParentescoSss);
		filtro.setRazonSocial(razonSocial);
		filtro.setSucursal(sucursal);
		filtro.setTipoBusqueda(tipoBusqueda);
		filtro.setTitularesYFliares(tituYFliaresDesc);
		filtro.setTituyfliares(Integer.parseInt(tituYFliares));
		filtro.setTotalesPorEmpresa(totalesPorEmpresa);
		filtro.setTotalesPorEntidad(totalesPorEntidad);
		filtro.setTotalesPorPlan(totalesPorPlan);
		filtro.setTotalesPorSeccional(totalesPorSeccional);
		filtro.setTotalesPorTercerizadora(totalesPorTercerizadora);
		filtro.setProyecto(proyecto);
		filtro.setVistaPrevencion(esExportaTercerizadora);
		
		req.getSession().setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PADRON, filtro);
		
		return filtro;
	}
	
	public static List<ReportePadronResult> procesaPadron(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronResult> repo = null;

		try {
			repo = ReportesAfiliadoServiceUtil.getReportePadron(filtro);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return repo;
	}

	public static SXSSFWorkbook generaReportePadron(HttpServletRequest req,
			HttpServletResponse res) {

		req.getSession().removeAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PADRON);
		
//		Recuperamos el filtro para que todos los reportes unifiquen lso criterios de busqueda
//		pegamos el filtro en la session
		BusquedaReportePadronFiltro filtro = getFiltrosPadron(req,res);
		boolean esVistaAdmifarm = ParamUtil.getBoolean(req, "vistaAdmifarm");
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		//HSSFWorkbook wb = null;

//		boolean totales_tercerizadora = ParamUtil.getBoolean(req,
//				"total_tercerizadora");
//		boolean totales_plan = ParamUtil.getBoolean(req, "total_plan");
//		boolean totales_seccional = ParamUtil
//				.getBoolean(req, "total_seccional");
//		boolean totales_empresa = ParamUtil.getBoolean(req, "total_empresa");
//		boolean totales_entidad = ParamUtil.getBoolean(req, "total_entidad");
		
		List<ReportePadronTotalResult> repoTotales = null;
		List<ReportePadronResult> repo = null;
		
		if(filtro.isTotalesPorEntidad()){
			repoTotales= getTotalesEntidad(filtro);		
//			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//			String fechaDesdeDia = req.getParameter("fechaDesdeDia");
//			String fechaDesdeMes = req.getParameter("fechaDesdeMes");
//			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
//			String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
//			Date fechaIni=null;
//			try {
//			fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
//					+ "-" + fechaDesdeAnio);			
//			} catch (ParseException e) {
//				_log.error(e);
//			}
			wb = getReporteTotalesEntidad(repoTotales, filtro);
			
		}else if (filtro.isTotalesPorTercerizadora() || filtro.isTotalesPorPlan() 
				|| filtro.isTotalesPorSeccional() || filtro.isTotalesPorEmpresa()) {
			
			repoTotales = generaPadronTotales(filtro);
			
			wb = getReporteTotales(repoTotales, filtro);
		} else {
			repo = procesaPadron(filtro); // getFiltrosPadron(req, res);

//			TODO:
//			Sacar esto cuando todos los reportes esten bajo el mismo formato..bajo.
			if(filtro.getTipoBusqueda() == 2 ){ // baja fecha proceso
				wb = getReporteBajas(repo);
			}else{
				wb = getReporte(repo, filtro, esVistaAdmifarm);
			}	
		}

		return wb;
	}

	private static SXSSFWorkbook getReporteTotalesEntidad(List<ReportePadronTotalResult> repo, BusquedaReportePadronFiltro filtro) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1;
		//HSSFSheet sheet = wb.createSheet("Hoja " + sh);
        Sheet sheet = wb.createSheet("Hoja "  + sh);

		
		int index = 0;
		int indexColumn = 0;
		sheet.createRow(index);
		CellStyle styleHeader = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleCell = getStyleAllWithBorderWbs(wb);
		CellStyle style = getStyleAllWbs(wb);
		CellStyle styleCellTotal=getStyleBoldAlignedWbs(wb,HorizontalAlignment.RIGHT);
		
		index=createHeaderTotalEntidades(sheet, index, styleHeader, style, filtro);
		
		int uoma_titular=0;
		//int uoma_adherente=0;
		int ospim_titular=0;
		int ospim_adherente=0;
		int amtima_titular=0;
		//int amtima_adherente=0;
		int totalTitulares=0;
		int totalIntegrantes=0;
		int ospim_capitas_titular=0;
		int ospim_capitas_adherente=0;
		int ospim_desregulados_titular=0;
		int ospim_desregulados_adherente=0;
		
		if (repo != null) {
			for (ReportePadronTotalResult r : repo) {
				index++;
				indexColumn = 0;
				Row rowI = sheet.createRow(index);
				
				CellUtil.createCell(rowI, indexColumn,r.getSeccional(), styleCell);
				indexColumn++;				
				Cell col = rowI.createCell(indexColumn);
				col.setCellValue(r.getUoma_titular());
				uoma_titular+=r.getUoma_titular();
				col.setCellStyle(styleCell);
				indexColumn++;
				Cell col3 = rowI.createCell(indexColumn);
				col3.setCellValue(r.getOspim_titular());
				ospim_titular+=r.getOspim_titular();
				col3.setCellStyle(styleCell);				
				indexColumn++;	
				Cell col6 = rowI.createCell(indexColumn);
				col6.setCellValue(r.getOspim_adherente());
				ospim_adherente+=r.getOspim_adherente();
				col6.setCellStyle(styleCell);
				indexColumn++;	
				Cell col4 = rowI.createCell(indexColumn);
				col4.setCellValue(r.getTotalCapitasTitular());
				ospim_capitas_titular+=r.getTotalCapitasTitular();
				col4.setCellStyle(styleCell);				
				indexColumn++;
				Cell col7 = rowI.createCell(indexColumn);
				col7.setCellValue(r.getTotalCapitasAdherente());
				ospim_capitas_adherente+=r.getTotalCapitasAdherente();
				col7.setCellStyle(styleCell);
				indexColumn++;				
				Cell col5 = rowI.createCell(indexColumn);
				col5.setCellValue(r.getTotalDesreguladosTitular());
				ospim_desregulados_titular+=r.getTotalDesreguladosTitular();
				col5.setCellStyle(styleCell);				
				indexColumn++;
				Cell col8 = rowI.createCell(indexColumn);
				col8.setCellValue(r.getTotalDesreguladosAdherente());
				ospim_desregulados_adherente+=r.getTotalDesreguladosAdherente();
				col8.setCellStyle(styleCell);
				indexColumn++;				
				Cell col9 = rowI.createCell(indexColumn);
				col9.setCellValue(r.getAmtima_titular());
				amtima_titular+=r.getAmtima_titular();
				col9.setCellStyle(styleCell);
				indexColumn++;
				Cell col10 = rowI.createCell(indexColumn);
				col10.setCellValue(r.getTotalTitulares());
				col10.setCellStyle(styleCell);
				totalTitulares+=r.getTotalTitulares();
				indexColumn++;
				Cell col11 = rowI.createCell(indexColumn);
				col11.setCellValue(r.getTotalIntegrantes());
				col11.setCellStyle(styleCell);
				totalIntegrantes+=r.getTotalIntegrantes();
				
			}
			index++;
			indexColumn = 0;
			Row rowI = sheet.createRow(index);
			
			CellUtil.createCell(rowI, indexColumn,"TOTAL", styleCellTotal);
			indexColumn++;
			Cell col = rowI.createCell(indexColumn);
			col.setCellValue(uoma_titular);			
			col.setCellStyle(styleCell);
			indexColumn++;				
			Cell col3 = rowI.createCell(indexColumn);
			col3.setCellValue(ospim_titular);			
			col3.setCellStyle(styleCell);				
			indexColumn++;
			Cell col6 = rowI.createCell(indexColumn);
			col6.setCellValue(ospim_adherente);			
			col6.setCellStyle(styleCell);
			indexColumn++;	
			Cell col4 = rowI.createCell(indexColumn);
			col4.setCellValue(ospim_capitas_titular);			
			col4.setCellStyle(styleCell);				
			indexColumn++;	
			Cell col7 = rowI.createCell(indexColumn);
			col7.setCellValue(ospim_capitas_adherente);			
			col7.setCellStyle(styleCell);
			indexColumn++;	
			Cell col5 = rowI.createCell(indexColumn);
			col5.setCellValue(ospim_desregulados_titular);			
			col5.setCellStyle(styleCell);				
			indexColumn++;	
			Cell col8 = rowI.createCell(indexColumn);
			col8.setCellValue(ospim_desregulados_adherente);			
			col8.setCellStyle(styleCell);
			indexColumn++;	
			Cell col9 = rowI.createCell(indexColumn);
			col9.setCellValue(amtima_titular);			
			col9.setCellStyle(styleCell);
			indexColumn++;				
			Cell col10 = rowI.createCell(indexColumn);
			col10.setCellValue(totalTitulares);			
			col10.setCellStyle(styleCell);
			indexColumn++;				
			Cell col11 = rowI.createCell(indexColumn);
			col11.setCellValue(totalIntegrantes);			
			col11 .setCellStyle(styleCell);
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
		
		return wb;

	}

	private static SXSSFWorkbook getReporteTotales(List<ReportePadronTotalResult> repo, 
			BusquedaReportePadronFiltro filtro) {
		
		SXSSFWorkbook wb = new SXSSFWorkbook();
		CellStyle style = getStyleAllWbs(wb);
		
		int sh = 1;
		//HSSFSheet sheet = wb.createSheet("Hoja " + sh);
		
		Sheet sheet = wb.createSheet("Hoja "  + sh);
		
		int index = 0;
		int indexColumn = 0;
		
		Row rowTitulo = sheet.createRow(index);
		crearTitulosTotales(sheet,rowTitulo,style,filtro);
		
		Row row = sheet.createRow(index++);
		CellStyle styleHeader = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleCell = getStyleAllWithBorderWbs(wb);
		
		createHeaderTotales(row, styleHeader, filtro.isTotalesPorTercerizadora(), filtro.isTotalesPorSeccional(),
				filtro.isTotalesPorPlan(), filtro.isTotalesPorEmpresa());
		
		row = sheet.createRow(index);	
		createHeaderDatosTotales(row, styleCell, filtro.isTotalesPorTercerizadora(), filtro.isTotalesPorSeccional(),
				filtro.isTotalesPorPlan(), filtro.isTotalesPorEmpresa(),filtro.isVistaPrevencion());

		if (repo != null) {
			for (ReportePadronTotalResult r : repo) {
				index++;
				indexColumn = 0;
				Row rowI = sheet.createRow(index);
				if (filtro.isTotalesPorTercerizadora()) {
					CellUtil.createCell(rowI, indexColumn,
							r.getTercerizadora(), styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorSeccional()) {
					CellUtil.createCell(rowI, indexColumn,
							r.getSeccional(), styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorPlan()) {
					CellUtil.createCell(rowI, indexColumn, r.getPlan(),
							styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorEmpresa()) {
					CellUtil.createCell(rowI, indexColumn, r.getCuit(),
							styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							r.getRazon_soc(), styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getRamoEmpresa().getId_ramo_empresa()) , styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							r.getRamoEmpresa().getDescripcion()  , styleCell);
					indexColumn++;
				}
				if(filtro.isTotalesPorPlan() && filtro.isVistaPrevencion()) {
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getCantTitular()) , styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getCantAdherente()) , styleCell);
					
				}else {					
					CellUtil.createCell(rowI, indexColumn++, r.getParentesco(),
							styleCell);
					//indexColumn++;
					Cell col = rowI.createCell(indexColumn);
					col.setCellValue(r.getTotal());
					col.setCellStyle(styleCell);
				}
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


		return wb;
	}


	
	private static void createHeaderDatosTotales(Row row,
			CellStyle styleCell, boolean total_tercerizadora,
			boolean total_seccional, boolean total_plan, boolean total_empresa, boolean vistaPrevencion) {
		int indexColumn = 0;

		if (total_tercerizadora) {
			Cell cell21 = row.createCell(indexColumn++);
			cell21.setCellValue(new HSSFRichTextString("Cod Ter."));
			cell21.setCellStyle(styleCell);			
		}
		if (total_seccional) {
			Cell cell1 = row.createCell(indexColumn);
			cell1.setCellValue(new HSSFRichTextString("Cod Seccional"));
			cell1.setCellStyle(styleCell);
			indexColumn++;
		}
		if (total_plan) {
			Cell cell2 = row.createCell(indexColumn);
			cell2.setCellValue(new HSSFRichTextString("Cod Plan"));
			cell2.setCellStyle(styleCell);
			indexColumn++;
		}
		if (total_empresa) {
			Cell cell21 = row.createCell(indexColumn++);
			cell21.setCellValue(new HSSFRichTextString("Cuit"));
			cell21.setCellStyle(styleCell);
			
			Cell cell22 = row.createCell(indexColumn++);
			cell22.setCellValue(new HSSFRichTextString("Empresa"));
			cell22.setCellStyle(styleCell);
			
			Cell cell23 = row.createCell(indexColumn++);
			cell23.setCellValue(new HSSFRichTextString("Id Ramo"));
			cell23.setCellStyle(styleCell);
			
			Cell cell24 = row.createCell(indexColumn++);
			cell24.setCellValue(new HSSFRichTextString("Descripción Ramo"));
			cell24.setCellStyle(styleCell);
			
		}
		
		if (total_plan && vistaPrevencion) {
			Cell cell25 = row.createCell(indexColumn++);
			cell25.setCellValue(new HSSFRichTextString("Titular"));
			cell25.setCellStyle(styleCell);
			
			Cell cell26 = row.createCell(indexColumn++);
			cell26.setCellValue(new HSSFRichTextString("Adherente"));
			cell26.setCellStyle(styleCell);
		}else {			
			Cell cell25 = row.createCell(indexColumn++);
			cell25.setCellValue(new HSSFRichTextString("Parentesco"));
			cell25.setCellStyle(styleCell);
			
			Cell cell26 = row.createCell(indexColumn++);
			cell26.setCellValue(new HSSFRichTextString("Cant"));
			cell26.setCellStyle(styleCell);
			
		}
	}



	private static void createHeaderTotales(Row row,
			CellStyle styleHeader, boolean total_tercerizadora,
			boolean total_seccional, boolean total_plan, boolean total_empresa) {
		int indexColumn = 0;

		if (total_tercerizadora) {
			Cell cell0 = row.createCell(indexColumn);
			cell0.setCellValue(new HSSFRichTextString("Reporte Totales por Tercerizadora"));
			cell0.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_seccional) {
			Cell cell1 = row.createCell(indexColumn);
			cell1.setCellValue(new HSSFRichTextString("Reporte Totales por Seccional"));
			cell1.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_plan) {
			Cell cell2 = row.createCell(indexColumn);
			cell2.setCellValue(new HSSFRichTextString("Reporte Totales por Plan"));
			cell2.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_empresa) {
			Cell cell21 = row.createCell(indexColumn);
			cell21.setCellValue(new HSSFRichTextString("Reporte de Totales por Empresa"));
			cell21.setCellStyle(styleHeader);
			indexColumn++;
		}else{
			Cell cell3 = row.createCell(indexColumn++);
			cell3.setCellValue(new HSSFRichTextString("Parentesco"));
			cell3.setCellStyle(styleHeader);

			Cell cell4 = row.createCell(indexColumn);
			cell4.setCellValue(new HSSFRichTextString("Total"));
			cell4.setCellStyle(styleHeader);
	
		}
		
			}

	private static void crearTitulosTotales(Sheet sheet, Row rowTitulo, CellStyle style, 
			BusquedaReportePadronFiltro filtro){
		
		String tituloReporte = "";
		if(filtro.isTotalesPorEntidad()){
			tituloReporte="Reporte de Totales por entidad - ".toUpperCase();
		}else if (filtro.isTotalesPorEmpresa()){
			tituloReporte="Reporte de Totales por empresa - ".toUpperCase();
		}else if (filtro.isTotalesPorPlan()){
			tituloReporte="Reporte de Totales por plan - ".toUpperCase();
		}else if (filtro.isTotalesPorSeccional()){
			tituloReporte="Reporte de Totales por seccional - ".toUpperCase();
		}else if (filtro.isTotalesPorTercerizadora()){
			tituloReporte="Reporte de Totales por tercerizadora - ".toUpperCase();
		}				
//		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
//		gmtMenos3.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int colNum = 0;
		
		style.setWrapText(true);

		Cell cell0 = rowTitulo.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString(tituloReporte +filtro.getDescripcionFiltros()) + 
				"        Fecha Listado: " + sdf.format(gmtMenos3.getTime()));
		
		rowTitulo.setHeight((short)650);
//		sheet.setColumnGroupCollapsed(colNum, true);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
	}
	private static int createHeaderTotalEntidades(Sheet sheet, int index,
			CellStyle styleHeader, CellStyle style, BusquedaReportePadronFiltro filtro) {
		Row rowTitulo = sheet.createRow(index++);
//		Cell cellTitulo = rowTitulo.createCell(0);
//		cellTitulo.setCellValue(new HSSFRichTextString("Reporte de Totales por entidad al "+fecha));
//		cellTitulo.setCellStyle(style);
		crearTitulosTotales(sheet,rowTitulo,style,filtro);
		
		Row row = sheet.createRow(index++);
		int indexColumn = 0;

		Cell cell0 = row.createCell(indexColumn++);
		cell0.setCellValue(new HSSFRichTextString("Seccional"));
		cell0.setCellStyle(styleHeader);
		
		Cell cell2 = row.createCell(indexColumn++);
		cell2.setCellValue(new HSSFRichTextString("UOMA"));
		cell2.setCellStyle(styleHeader);	
		
		Cell cell3 = row.createCell(indexColumn++);
		cell3.setCellValue(new HSSFRichTextString("OSPIM"));
		cell3.setCellStyle(styleHeader);
		
		Cell cell4 = row.createCell(indexColumn++);
		cell4.setCellStyle(styleHeader);
		Cell cell5 = row.createCell(indexColumn++);
		cell5.setCellStyle(styleHeader);
		Cell cell6 = row.createCell(indexColumn++);
		cell6.setCellStyle(styleHeader);
		Cell cell7 = row.createCell(indexColumn++);
		cell7.setCellStyle(styleHeader);
		Cell cell8 = row.createCell(indexColumn++);
		cell8.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 7));
		
		Cell cell9 = row.createCell(indexColumn++);
		cell9.setCellValue(new HSSFRichTextString("AMTIMA"));
		cell9.setCellStyle(styleHeader);
		
		indexColumn=0;
		Row row2 = sheet.createRow(index);
		row2.createCell(indexColumn++); //Seccional
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		row2.createCell(indexColumn++); //UOMA
				
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
		

		Cell cell23 = row2.createCell(indexColumn++);
		cell23.setCellValue(new HSSFRichTextString("OSPIM TITULARES"));
		cell23.setCellStyle(styleHeader);
		
		Cell cell26 = row2.createCell(indexColumn++);
		cell26.setCellValue(new HSSFRichTextString("OSPIM ADHERENTES"));
		cell26.setCellStyle(styleHeader);
		
		Cell cell24 = row2.createCell(indexColumn++);
		cell24.setCellValue(new HSSFRichTextString("CAPITAS TITU."));
		cell24.setCellStyle(styleHeader);

		Cell cell27 = row2.createCell(indexColumn++);
		cell27.setCellValue(new HSSFRichTextString("CAPITAS ADHE."));
		cell27.setCellStyle(styleHeader);
		
		Cell cell25 = row2.createCell(indexColumn++);
		cell25.setCellValue(new HSSFRichTextString("DEREGULADOS TITU."));
		cell25.setCellStyle(styleHeader);

		Cell cell28 = row2.createCell(indexColumn++);
		cell28.setCellValue(new HSSFRichTextString("DESREGULADOS ADHE."));
		cell28.setCellStyle(styleHeader);
		
		row2.createCell(indexColumn++); //AMTIMA		
		
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8));
		
		Cell cell10 = row.createCell(indexColumn++);
		cell10.setCellValue(new HSSFRichTextString("TOTAL TITULARES"));
		cell10.setCellStyle(styleHeader);	

		Cell cell11 = row.createCell(indexColumn++);
		cell11.setCellValue(new HSSFRichTextString("TOTAL ADHERENTES"));
		cell11.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 9, 9));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 10, 10));

		return index;
	}

	private static SXSSFWorkbook getReporte(List<ReportePadronResult> repo, BusquedaReportePadronFiltro filtro, boolean esVistaAdmifarm) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1, colNum=0;
		Sheet sheet = wb.createSheet("Hoja " + sh);

		CellStyle styleDate = getStyleDateWbs(wb);

		int index = 0;
		Row row1 = sheet.createRow(index);		
		
		if (esVistaAdmifarm) {
		    createHeaderAdmifarm(row1);	   
		} else if (filtro.isVistaPrevencion()) {
			createTitulos(sheet, row1, filtro);
			
			index++;
			Row row2 = sheet.createRow(index);
			createHeaderTercerizadora(row2);
		}else{
			createHeader(row1);
			
		}
		
		if (repo != null) {
			for (ReportePadronResult r : repo) {
				index++;
				colNum = 0;
				if (index >= 1048576) {
					index = 0;
					sh++;
					sheet = wb.createSheet("Hoja " + sh);
					Row rowNew = sheet.createRow(index);
					
					if (esVistaAdmifarm) {
					    createHeaderAdmifarm(rowNew);	   
					} else if(filtro.isVistaPrevencion()){
						createHeaderTercerizadora(rowNew);
					}else{
						createHeader(rowNew);
					}
					
					index++;
				}
				Row rowI = sheet.createRow(index);
				if (esVistaAdmifarm) {
				    createReportePadronDetalleAdmifarm(colNum, styleDate, r, rowI);
				} else if(filtro.isVistaPrevencion()){
					createReportePadronDetalleTercerizadora(colNum, styleDate, r, rowI);
				}else{
					createReportePadronDetalle(colNum, styleDate, r, rowI );
				}
			}
		}
		
		index++;
		sheet.createRow(index);
		for(int j=0;j<50;j++){
			try {
				sheet.autoSizeColumn((short) j);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}	
		
		
		return wb;
	}

	private static void createTitulos(Sheet sheet, Row row, BusquedaReportePadronFiltro filtro) {
		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
		gmtMenos3.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int colNum = 0;
		
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("REPORTE PADRÓN - " +filtro.getDescripcionFiltros()) + "        Fecha Listado: " + sdf.format(gmtMenos3.getTime()));
		
		row.setHeight((short)550);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 45));
	}
	
	private static void createReportePadronDetalle(int colNum,
			CellStyle styleDate, ReportePadronResult r, Row rowI ) {
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty((r.getCuil_titular())));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuil()));
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getInte()));
		colNum++;
		Cell cell6 = rowI.createCell(colNum);
		cell6.setCellValue(r.getId_ospim());
		colNum++;
		Cell cell7 = rowI.createCell(colNum);
		cell7.setCellValue(r.getId_uoma());
		colNum++;
		Cell cell8 = rowI.createCell(colNum);
		cell8.setCellValue(r.getId_amtima());
		colNum++;
		
		Cell cell6Info = rowI.createCell(colNum);
		colNum++;
		if (r.getFecha_ospim() != null) {
			cell6Info.setCellValue(r.getFecha_ospim());
			cell6Info.setCellStyle(styleDate);
		} else {
			cell6Info.setCellValue(new HSSFRichTextString());
		}
		Cell cell7Info = rowI.createCell(colNum);
		if (r.getAlta_fecha() != null) {
			cell7Info.setCellValue(r.getAlta_fecha());
			cell7Info.setCellStyle(styleDate);
		} else {
			cell7Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getUnifica()));
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
					StringUtils.getValueOrEmpty(r.getSeccional()));			
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getId_tercerizadora()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getParentesco()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getApellido()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNombre()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocu_numero()));
		colNum++;
		Cell cell16Info = rowI.createCell(colNum);
		cell16Info.setCellValue(r.getNaci_fecha());
		cell16Info.setCellStyle(styleDate);
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSexo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCivil_esta()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNacionalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProvincia()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getLocalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPostal_codi()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCalle()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNumero()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPiso()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDepto()));
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getTelefono()));
//		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum, StringUtils.getValueOrEmpty(r.getTelefono1()));
//				StringUtils.getValueOrEmpty(r.getTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelLaboral()));
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEmail()));
		
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getUnsuscribeEmail()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCategoria()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPlan()));
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getPlanOmint()));
//		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanPrevencion())));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getFarmaciaPrevencion()));
		colNum++;
		
		Cell cell32Info = rowI.createCell(colNum);
		if (r.getIngre_fecha() != null) {
			cell32Info.setCellValue(r.getIngre_fecha());
			cell32Info.setCellStyle(styleDate);
		} else {
			cell32Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		Cell cell33Info = rowI.createCell(colNum);
		if (r.getBaja_fecha() != null) {
			cell33Info.setCellValue(r.getBaja_fecha());
			cell33Info.setCellStyle(styleDate);
		} else {
			cell33Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuit()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRazon_soc()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRamo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEscala_salarial()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDiscapacitado()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getMotivoBaja()));
		colNum++;
		
		if(r.getFecha_uoma()!=null){
			Cell cell40Info = rowI.createCell(colNum);
			cell40Info.setCellValue(r.getFecha_uoma());
			cell40Info.setCellStyle(styleDate);
		}else{
			CellUtil.createCell(rowI, colNum,"");
		}
		colNum++;
		if(r.getFecha_amtima()!=null){
			Cell cell41Info = rowI.createCell(colNum);
			cell41Info.setCellValue(r.getFecha_amtima());
			cell41Info.setCellStyle(styleDate);
		}else{
			CellUtil.createCell(rowI, colNum,"");
		}
		colNum++;				
		CellUtil.createCell(rowI, colNum, r.getPerteneceAlaOrganizacion()==1?"SI":"NO");
		colNum++;				
		CellUtil.createCell(rowI, colNum, StringUtils.getValueOrEmpty(r.getProyecto()));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getObraSocAnterior()));
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getTieneAntecedentesJudiciales()==1?"SI":"NO");
		/*colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroSocio() != 0 ? String.valueOf(r.getNroSocio()):""));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroCredencial() != null ? String.valueOf(r.getNroCredencial()):""));
		*/
		
	}
	
	private static void createReportePadronDetalleTercerizadora(int colNum,
			CellStyle styleDate, ReportePadronResult r, Row rowI) {

		
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty((r.getCuil_titular())));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuil()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getInte()));
		colNum++;
		Cell cell0 = rowI.createCell(colNum);
		cell0.setCellValue(r.getId_ospim());
		
		colNum++;
		Cell cell1 = rowI.createCell(colNum);
		if (r.getFecha_ospim() != null) {
			cell1.setCellValue(r.getFecha_ospim());
			cell1.setCellStyle(styleDate);
		} else {
			cell1.setCellValue(new HSSFRichTextString());
		}			
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdSeccional()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSeccional()));
		colNum++;	
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getId_parentesco_sss()));
		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getParentesco()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getApellido()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNombre()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocu_numero()));
		colNum++;
		Cell cell2 = rowI.createCell(colNum);
		cell2.setCellValue(r.getNaci_fecha());
		cell2.setCellStyle(styleDate);
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSexo()));		
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getId_estado_civil_sss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCivil_esta()));
		
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdNacionalidadSSS()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNacionalidad()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdProvinciaSss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProvincia()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdLocalidadSss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getLocalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPostal_codi()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCalle()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNumero()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPiso()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDepto()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelefono1()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEmail()));
		
//		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getUnsuscribeEmail()));
		
		colNum++;
		//CellUtil.createCell(rowI, colNum,
		//		StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanPrevencion())));
		
		CellUtil.createCell(rowI, colNum,
						StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanTercerizadora())));
		
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getFarmaciaPrevencion()));
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getFarmaciaTercerizadora()));
		
		colNum++;
		Cell cell5 = rowI.createCell(colNum);
		if (r.getVigenFecha() != null) {
			cell5.setCellValue(r.getVigenFecha());
			cell5.setCellStyle(styleDate);
		} else {
			cell5.setCellValue(new HSSFRichTextString());
		}
		
		/*
		Cell cell5 = rowI.createCell(colNum);
		if (r.getIngre_fecha() != null) {
			cell5.setCellValue(r.getIngre_fecha());
			cell5.setCellStyle(styleDate);
		} else {
			cell5.setCellValue(new HSSFRichTextString());
		}
		*/
		
		colNum++;
		Cell cell3 = rowI.createCell(colNum);
		if (r.getBaja_fecha() != null) {
			cell3.setCellValue(r.getBaja_fecha());
			cell3.setCellStyle(styleDate);
		} else {
			cell3.setCellValue(new HSSFRichTextString());
		}

		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuit()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRazon_soc()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDiscapacitado()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getIdMotivoBaja()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getMotivoBaja()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getPerteneceAlaOrganizacion()==1?"SI":"NO");
		
		
		colNum++;
		Cell cell6 = rowI.createCell(colNum);
		if (r.getFpp()!= null) {
			cell6.setCellValue(r.getFpp());
			cell6.setCellStyle(styleDate);
		} else {
			cell6.setCellValue(new HSSFRichTextString());
		}
        
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getCopago());
		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getCategoria());
		
/*		
		colNum++;			
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProyecto()));		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getObraSocAnterior()));		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getTieneAntecedentesJudiciales()==1?"SI":"NO");	
		
		// datos no encontrados en listado de padron 
		
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.isFarmaciaUoma()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.isFarmaciaAmtima()));		
		colNum++;
		Cell cell4 = rowI.createCell(colNum);
		if (r.getFpp() != null) {
			cell4.setCellValue(r.getFpp());
			cell4.setCellStyle(styleDate);
		} else {
			cell4.setCellValue(new HSSFRichTextString());
		}
		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroSocio() != 0 ? String.valueOf(r.getNroSocio()):""));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroCredencial() != null ? String.valueOf(r.getNroCredencial()):""));
*/		
	}
	
	private static void createHeader(Row row) {
		int colNum = 0;
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		Cell cell1 = row.createCell(colNum);
		colNum++;
		cell1.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell2 = row.createCell(colNum);
		colNum++;
		cell2.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell3 = row.createCell(colNum);
		colNum++;
		cell3.setCellValue(new HSSFRichTextString("ID_OSPIM"));
		Cell cell4 = row.createCell(colNum);
		colNum++;
		cell4.setCellValue(new HSSFRichTextString("ID_UOMA"));
		Cell cell5 = row.createCell(colNum);
		colNum++;
		cell5.setCellValue(new HSSFRichTextString("ID_AMTIMA"));
		Cell cell6 = row.createCell(colNum);
		colNum++;
		cell6.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
		Cell cell7 = row.createCell(colNum);
		colNum++;
		cell7.setCellValue(new HSSFRichTextString("FECHA REGISTRO"));
		Cell cell8 = row.createCell(colNum);
		colNum++;
		cell8.setCellValue(new HSSFRichTextString("UNIFICA"));
		Cell cell9 = row.createCell(colNum);
		colNum++;
		cell9.setCellValue(new HSSFRichTextString("SECCIONAL"));
		Cell cell10 = row.createCell(colNum);
		colNum++;
		cell10.setCellValue(new HSSFRichTextString("TERCERIZADORA"));
		Cell cell11 = row.createCell(colNum);
		colNum++;
		cell11.setCellValue(new HSSFRichTextString("PARENTESCO"));
		Cell cell12 = row.createCell(colNum);
		colNum++;
		cell12.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell13 = row.createCell(colNum);
		colNum++;
		cell13.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell14 = row.createCell(colNum);
		colNum++;
		cell14.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell15 = row.createCell(colNum);
		colNum++;
		cell15.setCellValue(new HSSFRichTextString("NRO DOC"));
		Cell cell16 = row.createCell(colNum);
		colNum++;
		cell16.setCellValue(new HSSFRichTextString("FECHA NAC"));
		Cell cell17 = row.createCell(colNum);
		colNum++;
		cell17.setCellValue(new HSSFRichTextString("SEXO"));
		Cell cell18 = row.createCell(colNum);
		colNum++;
		cell18.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		Cell cell19 = row.createCell(colNum);
		colNum++;
		cell19.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		Cell cell20 = row.createCell(colNum);
		colNum++;
		cell20.setCellValue(new HSSFRichTextString("PROVINCIA"));
		Cell cell21 = row.createCell(colNum);
		colNum++;
		cell21.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		Cell cell22 = row.createCell(colNum);
		colNum++;
		cell22.setCellValue(new HSSFRichTextString("CP"));
		Cell cell23 = row.createCell(colNum);
		colNum++;
		cell23.setCellValue(new HSSFRichTextString("CALLE"));
		Cell cell24 = row.createCell(colNum);
		colNum++;
		cell24.setCellValue(new HSSFRichTextString("NUMERO"));
		Cell cell25 = row.createCell(colNum);
		colNum++;
		cell25.setCellValue(new HSSFRichTextString("PISO"));
		Cell cell26 = row.createCell(colNum);
		colNum++;
		cell26.setCellValue(new HSSFRichTextString("DEPTO"));
//		Cell cell27 = row.createCell(colNum);
//		colNum++;
//		cell27.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell28_ = row.createCell(colNum);
		colNum++;
		cell28_.setCellValue(new HSSFRichTextString("COD.AREA TEL."));
		Cell cell29_ = row.createCell(colNum);
		colNum++;
		cell29_.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell30_ = row.createCell(colNum);
		colNum++;
		cell30_.setCellValue(new HSSFRichTextString("COD.AREA CELU."));
		Cell cell31_ = row.createCell(colNum);
		colNum++;
		cell31_.setCellValue(new HSSFRichTextString("CELULAR"));
		Cell cell32_ = row.createCell(colNum);
		colNum++;
		cell32_.setCellValue(new HSSFRichTextString("COD.AREA TEL.LABO."));
		Cell cell33_ = row.createCell(colNum);
		colNum++;
		cell33_.setCellValue(new HSSFRichTextString("TELEF. LABORAL"));
		
		Cell cell28 = row.createCell(colNum);
		colNum++;
		cell28.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		
		Cell cell281 = row.createCell(colNum);
		colNum++;
		cell281.setCellValue(new HSSFRichTextString("UNSUSCRIBE EMAIL"));
		
		Cell cell29 = row.createCell(colNum);
		colNum++;
		cell29.setCellValue(new HSSFRichTextString("CATEGORIA"));
		Cell cell30 = row.createCell(colNum);
		colNum++;
		cell30.setCellValue(new HSSFRichTextString("PLAN"));
//		Cell cell31 = row.createCell(colNum);
//		colNum++;
//		cell31.setCellValue(new HSSFRichTextString("PLAN OMINT"));
		Cell cell31a = row.createCell(colNum);
		colNum++;
		cell31a.setCellValue(new HSSFRichTextString("PLAN ENSALUD"));
		Cell cell31b = row.createCell(colNum);
		colNum++;
		cell31b.setCellValue(new HSSFRichTextString("FARMACIA ENSALUD"));
		Cell cell32 = row.createCell(colNum);
		colNum++;
		cell32.setCellValue(new HSSFRichTextString("FECHA PROCESO"));
		Cell cell33 = row.createCell(colNum);
		colNum++;
		cell33.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell34 = row.createCell(colNum);
		colNum++;
		cell34.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell35 = row.createCell(colNum);
		colNum++;
		cell35.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		Cell cell36 = row.createCell(colNum);
		colNum++;
		cell36.setCellValue(new HSSFRichTextString("RAMO"));
		Cell cell37 = row.createCell(colNum);
		colNum++;
		cell37.setCellValue(new HSSFRichTextString("ESCALA SALARIAL"));
		Cell cell38 = row.createCell(colNum);
		colNum++;
		cell38.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		Cell cell39 = row.createCell(colNum);
		colNum++;
		cell39.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		Cell cell40 = row.createCell(colNum);
		colNum++;
		cell40.setCellValue(new HSSFRichTextString("FECHA UOMA"));
		Cell cell41 = row.createCell(colNum);
		colNum++;
		cell41.setCellValue(new HSSFRichTextString("FECHA AMTIMA"));
		Cell cell42 = row.createCell(colNum);
		colNum++;
		cell42.setCellValue(new HSSFRichTextString("PERTENECE A LA ORG."));
		Cell cell43 = row.createCell(colNum);
		colNum++;
		cell43.setCellValue(new HSSFRichTextString("PROYECTO"));
		Cell cell44 = row.createCell(colNum);
		colNum++;
		cell44.setCellValue(new HSSFRichTextString("OOSS ANTERIOR"));
		Cell cell45 = row.createCell(colNum);
		colNum++;
		cell45.setCellValue(new HSSFRichTextString("ANTEC.JUDICIALES"));
		Cell cell46 = row.createCell(colNum);
		/*colNum++;
		cell46.setCellValue(new HSSFRichTextString("NRO SOCIO"));
		Cell cell47 = row.createCell(colNum);
		colNum++;
		cell47.setCellValue(new HSSFRichTextString("NRO CREDENCIAL PREVENCION"));
		*/
	}
	
	private static void createHeaderTercerizadora(Row row) {
	
		int colNum = 0;
		
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		Cell cell00 = row.createCell(colNum);
		colNum++;
		cell00.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell1 = row.createCell(colNum);
		colNum++;
		cell1.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell2 = row.createCell(colNum);
		colNum++;
		cell2.setCellValue(new HSSFRichTextString("ID_OSPIM"));
		Cell cell3 = row.createCell(colNum);
		colNum++;
		cell3.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
		
		
		Cell cell51 = row.createCell(colNum);
		colNum++;
		cell51.setCellValue(new HSSFRichTextString("ID SECCIONAL"));
		
		Cell cell4 = row.createCell(colNum);
		colNum++;
		cell4.setCellValue(new HSSFRichTextString("SECCIONAL"));
		
		Cell cell50 = row.createCell(colNum);
		colNum++;
		cell50.setCellValue(new HSSFRichTextString("ID PARENT."));
		
		Cell cell5 = row.createCell(colNum);
		colNum++;
		cell5.setCellValue(new HSSFRichTextString("PARENTESCO"));
		
		Cell cell6 = row.createCell(colNum);
		colNum++;
		cell6.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell7 = row.createCell(colNum);
		colNum++;
		cell7.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell8 = row.createCell(colNum);
		colNum++;
		cell8.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell9 = row.createCell(colNum);
		colNum++;
		cell9.setCellValue(new HSSFRichTextString("NRO DOC"));
		Cell cell10 = row.createCell(colNum);
		colNum++;
		cell10.setCellValue(new HSSFRichTextString("FECHA NAC"));
		Cell cell12 = row.createCell(colNum);
		colNum++;
		cell12.setCellValue(new HSSFRichTextString("SEXO"));
		
		
		Cell cell49 = row.createCell(colNum);
		colNum++;
		cell49.setCellValue(new HSSFRichTextString("ID EST.CIVIL"));
		
		Cell cell13 = row.createCell(colNum);
		colNum++;
		cell13.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		
		
		Cell cell45 = row.createCell(colNum);
		colNum++;
		cell45.setCellValue(new HSSFRichTextString("ID NACION."));
		
		Cell cell14 = row.createCell(colNum);
		colNum++;
		cell14.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		
		Cell cell46 = row.createCell(colNum);
		colNum++;
		cell46.setCellValue(new HSSFRichTextString("ID PROV."));
		
		Cell cell15 = row.createCell(colNum);
		colNum++;
		cell15.setCellValue(new HSSFRichTextString("PROVINCIA"));
		
		
		Cell cell47 = row.createCell(colNum);
		colNum++;
		cell47.setCellValue(new HSSFRichTextString("ID LOC."));
		
		
		Cell cell16 = row.createCell(colNum);
		colNum++;
		cell16.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		
		
		Cell cell17 = row.createCell(colNum);
		colNum++;
		cell17.setCellValue(new HSSFRichTextString("CP"));
		Cell cell18 = row.createCell(colNum);
		colNum++;
		cell18.setCellValue(new HSSFRichTextString("CALLE"));
		Cell cell19 = row.createCell(colNum);
		colNum++;
		cell19.setCellValue(new HSSFRichTextString("NUMERO"));
		Cell cell20 = row.createCell(colNum);
		colNum++;
		cell20.setCellValue(new HSSFRichTextString("PISO"));
		Cell cell21 = row.createCell(colNum);
		colNum++;
		cell21.setCellValue(new HSSFRichTextString("DEPTO"));
		Cell cell22 = row.createCell(colNum);
		colNum++;
		cell22.setCellValue(new HSSFRichTextString("COD.AREA TEL."));
		Cell cell23 = row.createCell(colNum);
		colNum++;
		cell23.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell24 = row.createCell(colNum);
		colNum++;
		cell24.setCellValue(new HSSFRichTextString("COD.AREA CELU."));
		Cell cell25 = row.createCell(colNum);
		colNum++;
		cell25.setCellValue(new HSSFRichTextString("CELULAR"));
		Cell cell26 = row.createCell(colNum);
		colNum++;
		cell26.setCellValue(new HSSFRichTextString("COD.AREA TEL.LABO."));
		Cell cell27 = row.createCell(colNum);
		colNum++;
		cell27.setCellValue(new HSSFRichTextString("TELEF. LABORAL"));
		Cell cell28 = row.createCell(colNum);
		colNum++;
		cell28.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		Cell cell281 = row.createCell(colNum);
		
		//colNum++;
		//cell281.setCellValue(new HSSFRichTextString("UNSUSCRIBE EMAIL"));
		
		Cell cell29 = row.createCell(colNum);
		colNum++;
		cell29.setCellValue(new HSSFRichTextString("PLAN TERCERIZADORA"));
		Cell cell30 = row.createCell(colNum);
		colNum++;
		cell30.setCellValue(new HSSFRichTextString("FARMACIA TERCERIZADORA"));
		Cell cell31 = row.createCell(colNum);
		colNum++;
		cell31.setCellValue(new HSSFRichTextString("FECHA VIGENCIA"));
		Cell cell32 = row.createCell(colNum);
		colNum++;
		cell32.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell33 = row.createCell(colNum);
		colNum++;
		cell33.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell34 = row.createCell(colNum);
		colNum++;
		cell34.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		Cell cell35 = row.createCell(colNum);
		colNum++;
		cell35.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		
		Cell cell48 = row.createCell(colNum);
		colNum++;
		cell48.setCellValue(new HSSFRichTextString("ID MOT. BAJA"));
		
		Cell cell36 = row.createCell(colNum);
		colNum++;
		cell36.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		Cell cell38 = row.createCell(colNum);
		colNum++;
		cell38.setCellValue(new HSSFRichTextString("PERTENECE A LA ORG."));
		Cell cell39 = row.createCell(colNum);
		cell39.setCellValue(new HSSFRichTextString("VTO. PMI"));
		colNum++;
		Cell cell40 = row.createCell(colNum);
		cell40.setCellValue(new HSSFRichTextString("COPAGO"));
		colNum++;
		Cell cell41 = row.createCell(colNum);
		cell41.setCellValue(new HSSFRichTextString("CATEGORIA"));
		
/*		
		colNum++;
		cell39.setCellValue(new HSSFRichTextString("PROYECTO"));
		Cell cell40 = row.createCell(colNum);
		colNum++;
		cell40.setCellValue(new HSSFRichTextString("OOSS ANTERIOR"));
		Cell cell41 = row.createCell(colNum);
		colNum++;
		cell41.setCellValue(new HSSFRichTextString("ANTEC.JUDICIALES"));
		Cell cell42 = row.createCell(colNum);
		colNum++;
		cell42.setCellValue(new HSSFRichTextString("FARMACIA AMTIMA"));
		Cell cell43 = row.createCell(colNum);
		colNum++;
		cell43.setCellValue(new HSSFRichTextString("FAMACIA UOMA"));
		Cell cell44 = row.createCell(colNum);
		colNum++;
		cell44.setCellValue(new HSSFRichTextString("PMI"));
		Cell cell52 = row.createCell(colNum);
		colNum++;
		cell52.setCellValue(new HSSFRichTextString("NRO SOCIO"));
		Cell cell53 = row.createCell(colNum);
		colNum++;
		cell53.setCellValue(new HSSFRichTextString("NRO CREDENCIAL PREVENCION"));
*/		
		
		
		
	}

	private static String getValue(String o) {
		if (o != null) {
			return o;
		} else {
			return "";
		}
	}
	
	
	private static SXSSFWorkbook getReporteBajas(List<ReportePadronResult> repo) {
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1;
		Sheet sheet = wb.createSheet("Hoja " + sh);

		CellStyle styleDate = getStyleDateWbs(wb);

		int index = 0;
		Row row = sheet.createRow(index);
		createHeaderBajas(row);
		if (repo != null) {
			for (ReportePadronResult r : repo) {
				index++;
				if (index >= 1048576) {
					index = 0;
					sh++;
					sheet = wb.createSheet("Hoja " + sh);
					Row rowNew = sheet.createRow(index);
					createHeaderBajas(rowNew);
				}
				int col = 0;
				Row rowI = sheet.createRow(index);
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty((r.getCuil_titular())));
//				CellUtil.createCell(rowI, 1,
//						StringUtils.getValueOrEmpty(r.getCuil()));
				CellUtil.createCell(rowI, col++, String.valueOf(r.getInte()));
				
				Cell cell6 = rowI.createCell(col++);
				cell6.setCellValue(r.getId_ospim());
				
//				Cell cell7 = rowI.createCell(col++);
//				cell7.setCellValue(r.getId_uoma());
//				
//				Cell cell8 = rowI.createCell(col++);
//				cell8.setCellValue(r.getId_amtima());
								
				Cell cell6Info = rowI.createCell(col++);
				if (r.getFecha_ospim() != null) {
					cell6Info.setCellValue(r.getFecha_ospim());
					cell6Info.setCellStyle(styleDate);
				} else {
					cell6Info.setCellValue(new HSSFRichTextString());
				}
				
//				Cell cell7Info = rowI.createCell(col++);
//				if (r.getFecha_uoma() != null) {
//					cell7Info.setCellValue(r.getFecha_uoma());
//					cell7Info.setCellStyle(styleDate);
//				} else {
//					cell7Info.setCellValue(new HSSFRichTextString());
//				}
//				
//				Cell cell8Info = rowI.createCell(col++);
//				if (r.getFecha_amtima() != null) {
//					cell8Info.setCellValue(r.getFecha_amtima());
//					cell8Info.setCellStyle(styleDate);
//				} else {
//					cell8Info.setCellValue(new HSSFRichTextString());
//				}
				
				Cell cell9Info = rowI.createCell(col++);
				if (r.getFecha_proceso() != null) {
					cell9Info.setCellValue(r.getFecha_proceso() /*Alta_fecha()*/);
					cell9Info.setCellStyle(styleDate);
				} else {
					cell9Info.setCellValue(new HSSFRichTextString());
				}
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getUnifica()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getSeccional()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getId_tercerizadora()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getParentesco()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getApellido()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getNombre()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getDocu_numero()));
//				Cell cell18Info = rowI.createCell(col++);
//				cell18Info.setCellValue(r.getNaci_fecha());
//				cell18Info.setCellStyle(styleDate);
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getSexo()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getCivil_esta()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getNacionalidad()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getProvincia()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getLocalidad()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getPostal_codi()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getCalle()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getNumero()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getPiso()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getDepto()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getTelefono()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getEmail()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getCategoria()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getPlan()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getPlanOmint()));
//				Cell cell34Info = rowI.createCell(col++);
//				if (r.getIngre_fecha() != null) {
//					cell34Info.setCellValue(r.getIngre_fecha());
//					cell34Info.setCellStyle(styleDate);
//				} else {
//					cell34Info.setCellValue(new HSSFRichTextString());
//				}
				Cell cell35Info = rowI.createCell(col++);
				if (r.getBaja_fecha() != null) {
					cell35Info.setCellValue(r.getBaja_fecha());
					cell35Info.setCellStyle(styleDate);
				} else {
					cell35Info.setCellValue(new HSSFRichTextString());
				}
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getCuit()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getRazon_soc()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getRamo()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getEscala_salarial()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getDiscapacitado()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getMotivoBaja()));
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
		
		return wb;
	}

	private static void createHeaderBajas(Row row) {
		int col = 0;
		Cell cell0 = row.createCell(col++);
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
//		Cell cell1 = row.createCell(col++);
//		cell1.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell2 = row.createCell(col++);
		cell2.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell3 = row.createCell(col++);
		cell3.setCellValue(new HSSFRichTextString("ID_OSPIM"));
//		Cell cell4 = row.createCell(col++);
//		cell4.setCellValue(new HSSFRichTextString("ID_UOMA"));
//		Cell cell5 = row.createCell(col++);
//		cell5.setCellValue(new HSSFRichTextString("ID_AMTIMA"));
		Cell cell6 = row.createCell(col++);
		cell6.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
//		Cell cell7 = row.createCell(col++);
//		cell7.setCellValue(new HSSFRichTextString("FECHA UOMA"));
//		Cell cell8 = row.createCell(col++);
//		cell8.setCellValue(new HSSFRichTextString("FECHA AMTIMA"));
		Cell cell9 = row.createCell(col++);
		cell9.setCellValue(new HSSFRichTextString("FECHA PROCESO"));
//		Cell cell10 = row.createCell(col++);
//		cell10.setCellValue(new HSSFRichTextString("UNIFICA"));
		Cell cell11 = row.createCell(col++);
		cell11.setCellValue(new HSSFRichTextString("SECCIONAL"));
//		Cell cell12 = row.createCell(col++);
//		cell12.setCellValue(new HSSFRichTextString("TERCERIZADORA"));
		Cell cell13 = row.createCell(col++);
		cell13.setCellValue(new HSSFRichTextString("PARENTESCO"));
		Cell cell14 = row.createCell(col++);
		cell14.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell15 = row.createCell(col++);
		cell15.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell16 = row.createCell(col++);
		cell16.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell17 = row.createCell(col++);
		cell17.setCellValue(new HSSFRichTextString("NRO DOC"));
//		Cell cell18 = row.createCell(col++);
//		cell18.setCellValue(new HSSFRichTextString("FECHA NAC"));
//		Cell cell19 = row.createCell(col++);
//		cell19.setCellValue(new HSSFRichTextString("SEXO"));
//		Cell cell20 = row.createCell(col++);
//		cell20.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
//		Cell cell21 = row.createCell(col++);
//		cell21.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
//		Cell cell22 = row.createCell(col++);
//		cell22.setCellValue(new HSSFRichTextString("PROVINCIA"));
//		Cell cell23 = row.createCell(col++);
//		cell23.setCellValue(new HSSFRichTextString("LOCALIDAD"));
//		Cell cell24 = row.createCell(col++);
//		cell24.setCellValue(new HSSFRichTextString("CP"));
//		Cell cell25 = row.createCell(col++);
//		cell25.setCellValue(new HSSFRichTextString("CALLE"));
//		Cell cell26 = row.createCell(col++);
//		cell26.setCellValue(new HSSFRichTextString("NUMERO"));
//		Cell cell27 = row.createCell(col++);
//		cell27.setCellValue(new HSSFRichTextString("PISO"));
//		Cell cell28 = row.createCell(col++);
//		cell28.setCellValue(new HSSFRichTextString("DEPTO"));
//		Cell cell29 = row.createCell(col++);
//		cell29.setCellValue(new HSSFRichTextString("TELEFONO"));
//		Cell cell30 = row.createCell(col++);
//		cell30.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		Cell cell31 = row.createCell(col++);
		cell31.setCellValue(new HSSFRichTextString("CATEGORIA"));
		Cell cell32 = row.createCell(col++);
		cell32.setCellValue(new HSSFRichTextString("PLAN"));
		Cell cell33 = row.createCell(col++);
		cell33.setCellValue(new HSSFRichTextString("PLAN OMINT"));
//		Cell cell34 = row.createCell(col++);
//		cell34.setCellValue(new HSSFRichTextString("FECHA INGRESO"));
		Cell cell35 = row.createCell(col++);
		cell35.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell36 = row.createCell(col++);
		cell36.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell37 = row.createCell(col++);
		cell37.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
//		Cell cell38 = row.createCell(col++);
//		cell38.setCellValue(new HSSFRichTextString("RAMO"));
//		Cell cell39 = row.createCell(col++);
//		cell39.setCellValue(new HSSFRichTextString("ESCALA SALARIAL"));
//		Cell cell40 = row.createCell(col++); 
//		cell40.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		Cell cell41 = row.createCell(col++);
		cell41.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
	}
	
	private static void createHeaderAdmifarm(Row row) {

	    int colNum = 0;

	    CellUtil.createCell(row, colNum++, "TIPO DOC");
	    CellUtil.createCell(row, colNum++, "NRO DOC");
	    CellUtil.createCell(row, colNum++, "NUMERO AFILIADO");
	    CellUtil.createCell(row, colNum++, "CATEGORIA");
	    CellUtil.createCell(row, colNum++, "PARENTESCO");
	    CellUtil.createCell(row, colNum++, "APELLIDO");
	    CellUtil.createCell(row, colNum++, "NOMBRE");
	    CellUtil.createCell(row, colNum++, "FECHA NAC");
	    CellUtil.createCell(row, colNum++, "SEXO");
	    CellUtil.createCell(row, colNum++, "ESTADO CIVIL");
	    CellUtil.createCell(row, colNum++, "NACIONALIDAD");
	    CellUtil.createCell(row, colNum++, "PROVINCIA");
	    CellUtil.createCell(row, colNum++, "LOCALIDAD");
	    CellUtil.createCell(row, colNum++, "CP");
	    CellUtil.createCell(row, colNum++, "CALLE");
	    CellUtil.createCell(row, colNum++, "NUMERO");
	    CellUtil.createCell(row, colNum++, "PISO");
	    CellUtil.createCell(row, colNum++, "DEPTO");
	    CellUtil.createCell(row, colNum++, "COD.AREA TEL.");
	    CellUtil.createCell(row, colNum++, "TELEFONO");
	    CellUtil.createCell(row, colNum++, "COD.AREA CELU.");
	    CellUtil.createCell(row, colNum++, "CELULAR");
	    CellUtil.createCell(row, colNum++, "COD.AREA TEL.LABO.");
	    CellUtil.createCell(row, colNum++, "TELEF. LABORAL");
	    CellUtil.createCell(row, colNum++, "CORREO ELECTRONICO");
	    CellUtil.createCell(row, colNum++, "PLAN TERCERIZADORA");
	    CellUtil.createCell(row, colNum++, "FARMACIA TERCERIZADORA");
	    CellUtil.createCell(row, colNum++, "FECHA VIGENCIA");
	    CellUtil.createCell(row, colNum++, "FECHA BAJA");
	    CellUtil.createCell(row, colNum++, "CUIT");
	    CellUtil.createCell(row, colNum++, "RAZON SOCIAL");
	    CellUtil.createCell(row, colNum++, "DISCAPACITADO");
	    CellUtil.createCell(row, colNum++, "ID MOT. BAJA");
	    CellUtil.createCell(row, colNum++, "MOTIVO BAJA");
	    CellUtil.createCell(row, colNum++, "PERTENECE A LA ORG.");
	    CellUtil.createCell(row, colNum++, "VTO. PMI");
	    CellUtil.createCell(row, colNum++, "COPAGO");
	    CellUtil.createCell(row, colNum++, "CATEGORIA");
	    CellUtil.createCell(row, colNum++, "CUIL TITULAR");
	    CellUtil.createCell(row, colNum++, "CUIL");
	    CellUtil.createCell(row, colNum++, "FECHA OSPIM");
	    CellUtil.createCell(row, colNum++, "SECCIONAL");
	    CellUtil.createCell(row, colNum++, "PLAN AFILIADO");
	    CellUtil.createCell(row, colNum++, "PMI");
	    CellUtil.createCell(row, colNum++, "ACO");
	}
	
	private static void createReportePadronDetalleAdmifarm(int colNum, CellStyle styleDate,
			ReportePadronResult r, Row rowI) {

	    // TIPO DOC
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDocumento_tipo()));

	    // NRO DOC
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDocu_numero()));

	    // NUMERO AFILIADO
	    CellUtil.createCell(rowI, colNum++, String.valueOf(r.getId_ospim()));

	    // CATEGORIA
	    CellUtil.createCell(rowI, colNum++, String.valueOf(r.getInte()));

	    // PARENTESCO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getParentesco()));

	    // APELLIDO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getApellido()));

	    // NOMBRE
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNombre()));

	    // FECHA NAC
	    Cell fechaNac = rowI.createCell(colNum++);
	    if (r.getNaci_fecha() != null) {
	        fechaNac.setCellValue(r.getNaci_fecha());
	        fechaNac.setCellStyle(styleDate);
	    } else {
	        fechaNac.setCellValue("");
	    }

	    // SEXO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getSexo()));

	    // ESTADO CIVIL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCivil_esta()));

	    // NACIONALIDAD
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNacionalidad()));

	    // PROVINCIA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getProvincia()));

	    // LOCALIDAD
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getLocalidad()));

	    // CP
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getPostal_codi()));

	    // CALLE
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCalle()));

	    // NUMERO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNumero()));

	    // PISO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getPiso()));

	    // DEPTO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDepto()));

	    // COD.AREA TEL.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));

	    // TELEFONO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getTelefono1()));

	    // COD.AREA CELU.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaCelular()));

	    // CELULAR
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCelular()));

	    // COD.AREA TEL.LABO.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));

	    // TELEF. LABORAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getTelLaboral()));

	    // CORREO ELECTRONICO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getEmail()));

	    // PLAN TERCERIZADORA
	    String planTercerizadora = StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanTercerizadora()));

	    CellUtil.createCell(rowI, colNum++, planTercerizadora);

	    // FARMACIA TERCERIZADORA
	    String farmaciaTercerizadora =StringUtils.getValueOrEmpty(r.getFarmaciaTercerizadora());

	    CellUtil.createCell(rowI, colNum++, farmaciaTercerizadora);

	    // FECHA VIGENCIA
	    Cell fechaVigencia = rowI.createCell(colNum++);
	    if (r.getVigenFecha() != null) {
	        fechaVigencia.setCellValue(r.getVigenFecha());
	        fechaVigencia.setCellStyle(styleDate);
	    } else {
	        fechaVigencia.setCellValue("");
	    }

	    // FECHA BAJA
	    Cell fechaBaja = rowI.createCell(colNum++);
	    if (r.getBaja_fecha() != null) {
	        fechaBaja.setCellValue(r.getBaja_fecha());
	        fechaBaja.setCellStyle(styleDate);
	    } else {
	        fechaBaja.setCellValue("");
	    }

	    // CUIT
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuit()));

	    // RAZON SOCIAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getRazon_soc()));

	    // DISCAPACITADO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDiscapacitado()));

	    // ID MOT. BAJA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getIdMotivoBaja()));

	    // MOTIVO BAJA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getMotivoBaja()));

	    // PERTENECE A LA ORG.
	    CellUtil.createCell(rowI, colNum++, r.getPerteneceAlaOrganizacion() == 1 ? "SI" : "NO");

	    // VTO. PMI
	    Cell fechaPmi = rowI.createCell(colNum++);
	    if (r.getFpp() != null) {
	        fechaPmi.setCellValue(r.getFpp());
	        fechaPmi.setCellStyle(styleDate);
	    } else {
	        fechaPmi.setCellValue("");
	    }

	    // COPAGO
	    CellUtil.createCell(rowI, colNum++, r.getCopago());

	    // CATEGORIA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCategoria()));

	    // CUIL TITULAR
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuil_titular()));

	    // CUIL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuil()));

	    // FECHA OSPIM
	    Cell fechaOspim = rowI.createCell(colNum++);
	    if (r.getFecha_ospim() != null) {
	        fechaOspim.setCellValue(r.getFecha_ospim());
	        fechaOspim.setCellStyle(styleDate);
	    } else {
	        fechaOspim.setCellValue("");
	    }

	    // SECCIONAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getSeccional()));

	    // PLAN AFILIADO = PLAN TERCERIZADORA + FARMACIA TERCERIZADORA
	    String planAfiliado = (planTercerizadora + " " + farmaciaTercerizadora).trim();

	    CellUtil.createCell(rowI, colNum++, planAfiliado);

	    // PMI
	    CellUtil.createCell(rowI, colNum++, "");

	    // ACO
	    String aco = "";

	    if ("F".equalsIgnoreCase(r.getSexo()) && r.getNaci_fecha() != null) {
	        int edad = calcularEdad(r.getNaci_fecha());

	        if (edad >= 15 && edad <= 60) {
	            aco = "S";
	        }
	    }

	    CellUtil.createCell(rowI, colNum++, aco);
	    
	}
	
	private static int calcularEdad(Date fechaNacimiento) {
	    Calendar nacimiento = Calendar.getInstance();
	    nacimiento.setTime(fechaNacimiento);
	    Calendar hoy = Calendar.getInstance();
	    int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

	    if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
	        edad--;
	    }
	    
	    return edad;
	}

}
=======
package ar.com.ospim.afiliados.reportes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;
import ar.com.ospim.afiliados.reportes.beans.ReportePadronTotalResult;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.StringUtils;

public class ReporteListadoPadron extends ReporteXLS {

	private static Logger _log = Logger.getLogger(ReporteListadoPadron.class);

	private static final String SEPARATOR = ";";

	public static void getReporte(HttpServletRequest req, HttpServletResponse res, 
			ZipOutputStream out) throws IOException {
		
		BusquedaReportePadronFiltro filtro = getFiltrosPadron(req, res);

		List<ReportePadronResult> list = procesaPadron(filtro);
		
		int sh = 1;
		int index = 0;
		out.putNextEntry(new ZipEntry(sh + ".csv"));
		out.write(createHeader().getBytes());
		for (ReportePadronResult r : list) {
			index++;
			if (index >= 1048576) {
				index = 0;
				sh++;
				out.putNextEntry(new ZipEntry(sh + ".csv"));
				out.write(createHeader().getBytes());
			}

			String line = getLine(r);
			out.write(line.getBytes());
		}
	}

	private static String getLine(ReportePadronResult r) {
		StringBuilder str = new StringBuilder();
		str.append(r.getCuil_titular());
		str.append(SEPARATOR);
		str.append(r.getCuil());
		str.append(SEPARATOR);
		str.append(r.getInte());
		str.append(SEPARATOR);
		str.append(r.getId_ospim() != 0 ? String.valueOf(r.getId_ospim()) : "");
		str.append(SEPARATOR);
		str.append(r.getId_uoma() != 0 ? String.valueOf(r.getId_uoma()) : "");
		str.append(SEPARATOR);
		str.append(r.getId_amtima() != 0 ? String.valueOf(r.getId_amtima())
				: "");
		str.append(SEPARATOR);
		if (r.getFecha_ospim() != null) {
			str.append(r.getFecha_ospim());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		if (r.getAlta_fecha() != null) {
			str.append(r.getAlta_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		str.append(r.getUnifica());
		str.append(SEPARATOR);
		str.append(r.getSeccional());
		str.append(SEPARATOR);
		str.append(r.getId_tercerizadora());
		str.append(SEPARATOR);
		str.append(r.getParentesco());
		str.append(SEPARATOR);
		str.append(r.getApellido());
		str.append(SEPARATOR);
		str.append(r.getNombre());
		str.append(SEPARATOR);
		str.append(r.getDocumento_tipo());
		str.append(SEPARATOR);
		str.append(r.getDocu_numero());
		str.append(SEPARATOR);
		str.append(r.getNaci_fecha());
		str.append(SEPARATOR);
		str.append(r.getSexo());
		str.append(SEPARATOR);
		str.append(r.getCivil_esta());
		str.append(SEPARATOR);
		str.append(r.getNacionalidad());
		str.append(SEPARATOR);
		str.append(r.getProvincia());
		str.append(SEPARATOR);
		str.append(r.getLocalidad());
		str.append(SEPARATOR);
		str.append(r.getPostal_codi());
		str.append(SEPARATOR);
		str.append(r.getCalle());
		str.append(SEPARATOR);
		str.append(getValue(r.getNumero()));
		str.append(SEPARATOR);
		str.append(getValue(r.getPiso()));
		str.append(SEPARATOR);
		str.append(getValue(r.getDepto()));
		str.append(SEPARATOR);
		str.append(getValue(r.getTelefono()));
		str.append(SEPARATOR);
		str.append(getValue(r.getEmail()));
		str.append(SEPARATOR);
		str.append(r.getCategoria());
		str.append(SEPARATOR);
		str.append(r.getPlan());
		str.append(SEPARATOR);
		if (r.getIngre_fecha() != null) {
			str.append(r.getIngre_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		if (r.getBaja_fecha() != null) {
			str.append(r.getBaja_fecha());
		} else {
			str.append("");
		}
		str.append(SEPARATOR);
		str.append(r.getCuit());
		str.append(SEPARATOR);
		str.append(r.getRazon_soc());
		str.append(SEPARATOR);
		str.append(r.getRamo());
		str.append(SEPARATOR);
		str.append(r.getEscala_salarial());
		str.append(SEPARATOR);
		str.append(r.getDiscapacitado());
		str.append("\n");
		return str.toString();
	}

	private static String createHeader() {
		StringBuilder str = new StringBuilder();
		str.append("CUIL TITULAR");
		str.append(SEPARATOR);
		str.append("CUIL");
		str.append(SEPARATOR);
		str.append("INTE");
		str.append(SEPARATOR);
		str.append("ID_OSPIM");
		str.append(SEPARATOR);
		str.append("ID_UOMA");
		str.append(SEPARATOR);
		str.append("ID_AMTIMA");
		str.append(SEPARATOR);
		str.append("FECHA OSPIM");
		str.append(SEPARATOR);
		str.append("FECHA_REGISTRO");
		str.append(SEPARATOR);
		str.append("UNIFICA");
		str.append(SEPARATOR);
		str.append("SECCIONAL");
		str.append(SEPARATOR);
		str.append("TERCERIZADORA");
		str.append(SEPARATOR);
		str.append("PARENTESCO");
		str.append(SEPARATOR);
		str.append("APELLIDO");
		str.append(SEPARATOR);
		str.append("NOMBRE");
		str.append(SEPARATOR);
		str.append("TIPO DOC");
		str.append(SEPARATOR);
		str.append("NRO DOC");
		str.append(SEPARATOR);
		str.append("FECHA NAC");
		str.append(SEPARATOR);
		str.append("SEXO");
		str.append(SEPARATOR);
		str.append("ESTADO CIVIL");
		str.append(SEPARATOR);
		str.append("NACIONALIDAD");
		str.append(SEPARATOR);
		str.append("PROVINCIA");
		str.append(SEPARATOR);
		str.append("LOCALIDAD");
		str.append(SEPARATOR);
		str.append("CP");
		str.append(SEPARATOR);
		str.append("CALLE");
		str.append(SEPARATOR);
		str.append("NUMERO");
		str.append(SEPARATOR);
		str.append("PISO");
		str.append(SEPARATOR);
		str.append("DEPTO");
		str.append(SEPARATOR);
		str.append("TELEFONO");
		str.append(SEPARATOR);
		str.append("CORREO ELECTRONICO");
		str.append(SEPARATOR);
		str.append("CATEGORIA");
		str.append(SEPARATOR);
		str.append("PLAN");
		str.append(SEPARATOR);
		str.append("FECHA PLAN");
		str.append(SEPARATOR);
		str.append("FECHA BAJA");
		str.append(SEPARATOR);
		str.append("CUIT");
		str.append(SEPARATOR);
		str.append("RAZON SOCIAL");
		str.append(SEPARATOR);
		str.append("RAMO");
		str.append(SEPARATOR);
		str.append("ESCALA SALARIAL");
		str.append(SEPARATOR);
		str.append("DISCAPACITADO");
		str.append("\n");
		return str.toString();
	}
	
	public static List<ReportePadronTotalResult> getTotalesEntidad(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronTotalResult> repoTotales = null;

		try {
			repoTotales=ReportesAfiliadoServiceUtil.getReportePadronTotalesEntidad(filtro.getFechaDesde(), 
					filtro.getIdsTercerizadora(), filtro);
			
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return repoTotales;
	}

	public static List<ReportePadronTotalResult> generaPadronTotales(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronTotalResult> repoTotales = null;
	
		if (filtro.isTotalesPorTercerizadora() || filtro.isTotalesPorPlan() 
				|| filtro.isTotalesPorSeccional() || filtro.isTotalesPorEmpresa()) {
			
			try {
				repoTotales = ReportesAfiliadoServiceUtil.getReportePadronTotales(filtro);
			} catch (SystemException e) {
				_log.error(e);
			}

		}
		
		return repoTotales;
	}

	public static BusquedaReportePadronFiltro getFiltrosPadron(HttpServletRequest req, HttpServletResponse res) {
		
		BusquedaReportePadronFiltro filtro = new BusquedaReportePadronFiltro();
		
		String fechaDesdeDia = req.getParameter("fechaDesdeDia");
		String fechaDesdeMes = req.getParameter("fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
		String fechaHastaDia = req.getParameter("fechaHastaDia");
		String fechaHastaMes = req.getParameter("fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = req.getParameter("fechaHastaAnio");
//		String fechaProcDesdeDia = req.getParameter("fechaProcDesdeDia");
//		String fechaProcDesdeMes = req.getParameter("fechaProcDesdeMes");
//		fechaProcDesdeMes = String.valueOf(Integer.valueOf(fechaProcDesdeMes) + 1);
//		String fechaProcDesdeAnio = req.getParameter("fechaProcDesdeAnio");
//		String fechaProcHastaDia = req.getParameter("fechaProcHastaDia");
//		String fechaProcHastaMes = req.getParameter("fechaProcHastaMes");
//		fechaProcHastaMes = String.valueOf(Integer.valueOf(fechaProcHastaMes) + 1);
//		String fechaProcHastaAnio = req.getParameter("fechaProcHastaAnio");
		String cuit = req.getParameter("cuit");
		String sucursal = req.getParameter("sucursal");
		String razonSocial = req.getParameter("razonSocial");
		String edadIni = req.getParameter("edadIni");
		String edadFin = req.getParameter("edadFin");
		String tituYFliares = req.getParameter("tituYFliares");
		String tituYFliaresDesc = req.getParameter("tituYFliaresDesc");
		
		String idsTercerizadoras = req.getParameter("idTercerizadora");
		String descTercerizadoras = "Todas";
		if (null==idsTercerizadoras || idsTercerizadoras.equals("null") || idsTercerizadoras.trim().length()==0) {
			idsTercerizadoras = null;
		}else{
			idsTercerizadoras += ",";
			descTercerizadoras = req.getParameter("descTercerizadora");
		}
		String idLoca = req.getParameter("idLoca");
		String descLocalidades = "Todas";
		if (null==idLoca || idLoca.equals("null") || idLoca.trim().length()==0) {
			idLoca = null;
		}else{
			idLoca += ",";
			descLocalidades = req.getParameter("descLocalidades");
		}
		String idProv = req.getParameter("idProv");
		String descProvincias = "Todas";
		if (null==idProv || idProv.equals("null") || idProv.trim().length()==0) {
			idProv = null;
		}else{
			idProv += ",";
			descProvincias = req.getParameter("descProvincias");
		}
		String idPlan = req.getParameter("idPlan");
		String descPlanes = "Todos";
		if (null==idPlan || idPlan.equals("null") || idPlan.trim().length()==0) {
			idPlan = null;
		}else{
			idPlan += ",";
			descPlanes = req.getParameter("descPlanes");
		}
		
		String tipoAportes = ParamUtil.getString(req, "tipoAporte");
		String descTiposAporte = "Todos";
		if (null==tipoAportes || tipoAportes.equals("null") || tipoAportes.trim().length()==0) {
			tipoAportes = null;
		}else{
			tipoAportes += ",";
			descTiposAporte = req.getParameter("descTiposAporte");
		}
		
		String parentesco = req.getParameter("parentesco");
		String descParentesco = "Todos";
		Integer idParentescoSss = null;
		try{
			idParentescoSss = Integer.parseInt(parentesco);
			descParentesco = req.getParameter("descParentesco");
		}catch (NumberFormatException e) {
			idParentescoSss = null;
		}
		
		String idSeccional = req.getParameter("idSeccional");
		String descSeccionales = "Todas";
		if (null==idSeccional || idSeccional.equals("null") || idSeccional.trim().length()==0) {
			idSeccional = null;
		}else{
			idSeccional += ",";
			descSeccionales = req.getParameter("descSeccionales");
		}
		String escalaSalarial = req.getParameter("escala_salarial");
		String motivoBajaIds = req.getParameter("idsMotivoBaja");
		String motivosBajaDesc = "Todos";

		if (null==motivoBajaIds || motivoBajaIds.equals("null") || motivoBajaIds.trim().length()==0) {
			motivoBajaIds = null;
		}else{
			motivoBajaIds += ",";
			motivosBajaDesc = req.getParameter("motivosBajaDesc");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		int tipoBusqueda= ParamUtil.getInteger(req,"tipoBusqueda");
		String descTipoBusqueda= req.getParameter("descTipoBusqueda");
		String proyecto = req.getParameter("proyecto");
		if(StringUtils.checkEmpty(proyecto)){
			proyecto = null;
		}
		boolean esExportaTercerizadora= ParamUtil.getBoolean(req, "vistaTercerizadora");
		
		boolean esVistaAdmifarm = ParamUtil.getBoolean(req, "vistaAdmifarm");
		
		if(esExportaTercerizadora) {
			idPlan="";
			List<Plan> pls=TraeListasServiceUtil.getPlanesSoloOspim();
			for(Plan p:pls) {
				idPlan+=p.getId()+",";
			}
//			idPlan=idPlan.substring(0,idPlan.length()-1);
			
		}
		
		
		Date fechaDsd=null, fechaHta = null;
		Date fechaNacimIni = null, fechaNacimFin = null;
		
		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			
			fechaDsd = fechaIni;
			fechaHta = fechaFin;
			
//			Date fechaProcDesde = format.parse(fechaProcDesdeDia + "-" + fechaProcDesdeMes
//					+ "-" + fechaProcDesdeAnio);
//			Date fechaProcHasta = format.parse(fechaProcHastaDia + "-" + fechaProcHastaMes
//					+ "-" + fechaProcHastaAnio);
						
//			la fecha inicio debe considerar desde el 1 de enero, y la fecha hasta el 31 de diciembre
			if(!StringUtils.checkEmpty(edadIni) && !StringUtils.checkEmpty(edadFin)){

				Calendar fin = Calendar.getInstance();
				if(Integer.parseInt(edadIni) > 0){ 
					fin.setTime(fechaFin);
					fin.add(Calendar.YEAR, -1 * Integer.valueOf(edadFin));
				}else{
					fin.setTime(fechaFin);
				}
				fechaNacimFin = fin.getTime();
				
				Calendar ini = Calendar.getInstance();
				ini.setTime(fechaIni);
				if(Integer.parseInt(edadIni) == 0){ 
					edadIni = "1";
				}
				ini.add(Calendar.YEAR, -1 * Integer.valueOf(edadIni));
				fechaNacimIni = ini.getTime();
				
				if(fechaNacimIni.after(fechaNacimFin)){
					Date auxCambia = null;
					auxCambia = fechaNacimFin;
					fechaNacimFin = fechaNacimIni;
					fechaNacimIni = auxCambia;
				}
			}
		} catch (ParseException e) {
			_log.error(e);
		}
		boolean totalesPorTercerizadora = ParamUtil.getBoolean(req,"total_tercerizadora");
		boolean totalesPorPlan = ParamUtil.getBoolean(req, "total_plan");
		boolean totalesPorSeccional = ParamUtil.getBoolean(req, "total_seccional");
		boolean totalesPorEmpresa = ParamUtil.getBoolean(req, "total_empresa");
		boolean totalesPorEntidad = ParamUtil.getBoolean(req, "total_entidad");
		
		filtro.setCategoriaUoma(escalaSalarial);
		filtro.setCodigosAportes(tipoAportes);
		filtro.setCodigosLocalidad(idLoca);
		filtro.setCodigosPlan(idPlan);
		filtro.setCodigosProvincia(idProv);
		filtro.setCodigosSeccional(idSeccional);
		filtro.setCuit(cuit);
		filtro.setDescAportes(descTiposAporte);
		filtro.setDescBusqueda(descTipoBusqueda);
		filtro.setDescLocalidad(descLocalidades);
		filtro.setDescMotivoBaja(motivosBajaDesc); 
		filtro.setDescPlan(descPlanes);
		filtro.setDescProvincia(descProvincias);
		filtro.setDescSeccional(descSeccionales);
		filtro.setDescTercerizadora(descTercerizadoras);
		try{
			filtro.setEdadFinal(Integer.parseInt(edadFin));
		}catch (Exception e) {
			filtro.setEdadFinal(0);
		}
		try{
			filtro.setEdadInicial(Integer.parseInt(edadIni));
		}catch (Exception e) {
			filtro.setEdadInicial(0);
		}
		filtro.setFechaDesde(fechaDsd);
		filtro.setFechaHasta(fechaHta);
		filtro.setFechaNacimIni(fechaNacimIni);
		filtro.setFechaNacimFin(fechaNacimFin);
		filtro.setIdsMotivoBaja(motivoBajaIds );
		filtro.setIdsTercerizadora(idsTercerizadoras);
		filtro.setParentescoDesc(descParentesco);
		filtro.setParentescoId(idParentescoSss);
		filtro.setRazonSocial(razonSocial);
		filtro.setSucursal(sucursal);
		filtro.setTipoBusqueda(tipoBusqueda);
		filtro.setTitularesYFliares(tituYFliaresDesc);
		filtro.setTituyfliares(Integer.parseInt(tituYFliares));
		filtro.setTotalesPorEmpresa(totalesPorEmpresa);
		filtro.setTotalesPorEntidad(totalesPorEntidad);
		filtro.setTotalesPorPlan(totalesPorPlan);
		filtro.setTotalesPorSeccional(totalesPorSeccional);
		filtro.setTotalesPorTercerizadora(totalesPorTercerizadora);
		filtro.setProyecto(proyecto);
		filtro.setVistaPrevencion(esExportaTercerizadora);
		filtro.setVistaAdmifarm(esVistaAdmifarm);
		
		req.getSession().setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PADRON, filtro);
		
		return filtro;
	}
	
	public static List<ReportePadronResult> procesaPadron(BusquedaReportePadronFiltro filtro) {
		
		List<ReportePadronResult> repo = null;

		try {
			repo = ReportesAfiliadoServiceUtil.getReportePadron(filtro);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return repo;
	}

	public static SXSSFWorkbook generaReportePadron(HttpServletRequest req,
			HttpServletResponse res) {

		req.getSession().removeAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PADRON);
		
//		Recuperamos el filtro para que todos los reportes unifiquen lso criterios de busqueda
//		pegamos el filtro en la session
		BusquedaReportePadronFiltro filtro = getFiltrosPadron(req,res);
		boolean esVistaAdmifarm = ParamUtil.getBoolean(req, "vistaAdmifarm");
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		//HSSFWorkbook wb = null;

//		boolean totales_tercerizadora = ParamUtil.getBoolean(req,
//				"total_tercerizadora");
//		boolean totales_plan = ParamUtil.getBoolean(req, "total_plan");
//		boolean totales_seccional = ParamUtil
//				.getBoolean(req, "total_seccional");
//		boolean totales_empresa = ParamUtil.getBoolean(req, "total_empresa");
//		boolean totales_entidad = ParamUtil.getBoolean(req, "total_entidad");
		
		List<ReportePadronTotalResult> repoTotales = null;
		List<ReportePadronResult> repo = null;
		
		if(filtro.isTotalesPorEntidad()){
			repoTotales= getTotalesEntidad(filtro);		
//			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//			String fechaDesdeDia = req.getParameter("fechaDesdeDia");
//			String fechaDesdeMes = req.getParameter("fechaDesdeMes");
//			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
//			String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
//			Date fechaIni=null;
//			try {
//			fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
//					+ "-" + fechaDesdeAnio);			
//			} catch (ParseException e) {
//				_log.error(e);
//			}
			wb = getReporteTotalesEntidad(repoTotales, filtro);
			
		}else if (filtro.isTotalesPorTercerizadora() || filtro.isTotalesPorPlan() 
				|| filtro.isTotalesPorSeccional() || filtro.isTotalesPorEmpresa()) {
			
			repoTotales = generaPadronTotales(filtro);
			
			wb = getReporteTotales(repoTotales, filtro);
		} else {
			repo = procesaPadron(filtro); // getFiltrosPadron(req, res);

//			TODO:
//			Sacar esto cuando todos los reportes esten bajo el mismo formato..bajo.
			if(filtro.getTipoBusqueda() == 2 ){ // baja fecha proceso
				wb = getReporteBajas(repo);
			}else{
				wb = getReporte(repo, filtro, esVistaAdmifarm);
			}	
		}

		return wb;
	}

	private static SXSSFWorkbook getReporteTotalesEntidad(List<ReportePadronTotalResult> repo, BusquedaReportePadronFiltro filtro) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1;
		//HSSFSheet sheet = wb.createSheet("Hoja " + sh);
        Sheet sheet = wb.createSheet("Hoja "  + sh);

		
		int index = 0;
		int indexColumn = 0;
		sheet.createRow(index);
		CellStyle styleHeader = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleCell = getStyleAllWithBorderWbs(wb);
		CellStyle style = getStyleAllWbs(wb);
		CellStyle styleCellTotal=getStyleBoldAlignedWbs(wb,HorizontalAlignment.RIGHT);
		
		index=createHeaderTotalEntidades(sheet, index, styleHeader, style, filtro);
		
		int uoma_titular=0;
		//int uoma_adherente=0;
		int ospim_titular=0;
		int ospim_adherente=0;
		int amtima_titular=0;
		//int amtima_adherente=0;
		int totalTitulares=0;
		int totalIntegrantes=0;
		int ospim_capitas_titular=0;
		int ospim_capitas_adherente=0;
		int ospim_desregulados_titular=0;
		int ospim_desregulados_adherente=0;
		
		if (repo != null) {
			for (ReportePadronTotalResult r : repo) {
				index++;
				indexColumn = 0;
				Row rowI = sheet.createRow(index);
				
				CellUtil.createCell(rowI, indexColumn,r.getSeccional(), styleCell);
				indexColumn++;				
				Cell col = rowI.createCell(indexColumn);
				col.setCellValue(r.getUoma_titular());
				uoma_titular+=r.getUoma_titular();
				col.setCellStyle(styleCell);
				indexColumn++;
				Cell col3 = rowI.createCell(indexColumn);
				col3.setCellValue(r.getOspim_titular());
				ospim_titular+=r.getOspim_titular();
				col3.setCellStyle(styleCell);				
				indexColumn++;	
				Cell col6 = rowI.createCell(indexColumn);
				col6.setCellValue(r.getOspim_adherente());
				ospim_adherente+=r.getOspim_adherente();
				col6.setCellStyle(styleCell);
				indexColumn++;	
				Cell col4 = rowI.createCell(indexColumn);
				col4.setCellValue(r.getTotalCapitasTitular());
				ospim_capitas_titular+=r.getTotalCapitasTitular();
				col4.setCellStyle(styleCell);				
				indexColumn++;
				Cell col7 = rowI.createCell(indexColumn);
				col7.setCellValue(r.getTotalCapitasAdherente());
				ospim_capitas_adherente+=r.getTotalCapitasAdherente();
				col7.setCellStyle(styleCell);
				indexColumn++;				
				Cell col5 = rowI.createCell(indexColumn);
				col5.setCellValue(r.getTotalDesreguladosTitular());
				ospim_desregulados_titular+=r.getTotalDesreguladosTitular();
				col5.setCellStyle(styleCell);				
				indexColumn++;
				Cell col8 = rowI.createCell(indexColumn);
				col8.setCellValue(r.getTotalDesreguladosAdherente());
				ospim_desregulados_adherente+=r.getTotalDesreguladosAdherente();
				col8.setCellStyle(styleCell);
				indexColumn++;				
				Cell col9 = rowI.createCell(indexColumn);
				col9.setCellValue(r.getAmtima_titular());
				amtima_titular+=r.getAmtima_titular();
				col9.setCellStyle(styleCell);
				indexColumn++;
				Cell col10 = rowI.createCell(indexColumn);
				col10.setCellValue(r.getTotalTitulares());
				col10.setCellStyle(styleCell);
				totalTitulares+=r.getTotalTitulares();
				indexColumn++;
				Cell col11 = rowI.createCell(indexColumn);
				col11.setCellValue(r.getTotalIntegrantes());
				col11.setCellStyle(styleCell);
				totalIntegrantes+=r.getTotalIntegrantes();
				
			}
			index++;
			indexColumn = 0;
			Row rowI = sheet.createRow(index);
			
			CellUtil.createCell(rowI, indexColumn,"TOTAL", styleCellTotal);
			indexColumn++;
			Cell col = rowI.createCell(indexColumn);
			col.setCellValue(uoma_titular);			
			col.setCellStyle(styleCell);
			indexColumn++;				
			Cell col3 = rowI.createCell(indexColumn);
			col3.setCellValue(ospim_titular);			
			col3.setCellStyle(styleCell);				
			indexColumn++;
			Cell col6 = rowI.createCell(indexColumn);
			col6.setCellValue(ospim_adherente);			
			col6.setCellStyle(styleCell);
			indexColumn++;	
			Cell col4 = rowI.createCell(indexColumn);
			col4.setCellValue(ospim_capitas_titular);			
			col4.setCellStyle(styleCell);				
			indexColumn++;	
			Cell col7 = rowI.createCell(indexColumn);
			col7.setCellValue(ospim_capitas_adherente);			
			col7.setCellStyle(styleCell);
			indexColumn++;	
			Cell col5 = rowI.createCell(indexColumn);
			col5.setCellValue(ospim_desregulados_titular);			
			col5.setCellStyle(styleCell);				
			indexColumn++;	
			Cell col8 = rowI.createCell(indexColumn);
			col8.setCellValue(ospim_desregulados_adherente);			
			col8.setCellStyle(styleCell);
			indexColumn++;	
			Cell col9 = rowI.createCell(indexColumn);
			col9.setCellValue(amtima_titular);			
			col9.setCellStyle(styleCell);
			indexColumn++;				
			Cell col10 = rowI.createCell(indexColumn);
			col10.setCellValue(totalTitulares);			
			col10.setCellStyle(styleCell);
			indexColumn++;				
			Cell col11 = rowI.createCell(indexColumn);
			col11.setCellValue(totalIntegrantes);			
			col11 .setCellStyle(styleCell);
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
		
		return wb;

	}

	private static SXSSFWorkbook getReporteTotales(List<ReportePadronTotalResult> repo, 
			BusquedaReportePadronFiltro filtro) {
		
		SXSSFWorkbook wb = new SXSSFWorkbook();
		CellStyle style = getStyleAllWbs(wb);
		
		int sh = 1;
		//HSSFSheet sheet = wb.createSheet("Hoja " + sh);
		
		Sheet sheet = wb.createSheet("Hoja "  + sh);
		
		int index = 0;
		int indexColumn = 0;
		
		Row rowTitulo = sheet.createRow(index);
		crearTitulosTotales(sheet,rowTitulo,style,filtro);
		
		Row row = sheet.createRow(index++);
		CellStyle styleHeader = getStyleHeaderWithBorderWbs(wb);
		CellStyle styleCell = getStyleAllWithBorderWbs(wb);
		
		createHeaderTotales(row, styleHeader, filtro.isTotalesPorTercerizadora(), filtro.isTotalesPorSeccional(),
				filtro.isTotalesPorPlan(), filtro.isTotalesPorEmpresa());
		
		row = sheet.createRow(index);	
		createHeaderDatosTotales(row, styleCell, filtro.isTotalesPorTercerizadora(), filtro.isTotalesPorSeccional(),
				filtro.isTotalesPorPlan(), filtro.isTotalesPorEmpresa(),filtro.isVistaPrevencion());

		if (repo != null) {
			for (ReportePadronTotalResult r : repo) {
				index++;
				indexColumn = 0;
				Row rowI = sheet.createRow(index);
				if (filtro.isTotalesPorTercerizadora()) {
					CellUtil.createCell(rowI, indexColumn,
							r.getTercerizadora(), styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorSeccional()) {
					CellUtil.createCell(rowI, indexColumn,
							r.getSeccional(), styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorPlan()) {
					CellUtil.createCell(rowI, indexColumn, r.getPlan(),
							styleCell);
					indexColumn++;
				}
				if (filtro.isTotalesPorEmpresa()) {
					CellUtil.createCell(rowI, indexColumn, r.getCuit(),
							styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							r.getRazon_soc(), styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getRamoEmpresa().getId_ramo_empresa()) , styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							r.getRamoEmpresa().getDescripcion()  , styleCell);
					indexColumn++;
				}
				if(filtro.isTotalesPorPlan() && filtro.isVistaPrevencion()) {
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getCantTitular()) , styleCell);
					indexColumn++;
					CellUtil.createCell(rowI, indexColumn,
							String.valueOf(r.getCantAdherente()) , styleCell);
					
				}else {					
					CellUtil.createCell(rowI, indexColumn++, r.getParentesco(),
							styleCell);
					//indexColumn++;
					Cell col = rowI.createCell(indexColumn);
					col.setCellValue(r.getTotal());
					col.setCellStyle(styleCell);
				}
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


		return wb;
	}


	
	private static void createHeaderDatosTotales(Row row,
			CellStyle styleCell, boolean total_tercerizadora,
			boolean total_seccional, boolean total_plan, boolean total_empresa, boolean vistaPrevencion) {
		int indexColumn = 0;

		if (total_tercerizadora) {
			Cell cell21 = row.createCell(indexColumn++);
			cell21.setCellValue(new HSSFRichTextString("Cod Ter."));
			cell21.setCellStyle(styleCell);			
		}
		if (total_seccional) {
			Cell cell1 = row.createCell(indexColumn);
			cell1.setCellValue(new HSSFRichTextString("Cod Seccional"));
			cell1.setCellStyle(styleCell);
			indexColumn++;
		}
		if (total_plan) {
			Cell cell2 = row.createCell(indexColumn);
			cell2.setCellValue(new HSSFRichTextString("Cod Plan"));
			cell2.setCellStyle(styleCell);
			indexColumn++;
		}
		if (total_empresa) {
			Cell cell21 = row.createCell(indexColumn++);
			cell21.setCellValue(new HSSFRichTextString("Cuit"));
			cell21.setCellStyle(styleCell);
			
			Cell cell22 = row.createCell(indexColumn++);
			cell22.setCellValue(new HSSFRichTextString("Empresa"));
			cell22.setCellStyle(styleCell);
			
			Cell cell23 = row.createCell(indexColumn++);
			cell23.setCellValue(new HSSFRichTextString("Id Ramo"));
			cell23.setCellStyle(styleCell);
			
			Cell cell24 = row.createCell(indexColumn++);
			cell24.setCellValue(new HSSFRichTextString("Descripción Ramo"));
			cell24.setCellStyle(styleCell);
			
		}
		
		if (total_plan && vistaPrevencion) {
			Cell cell25 = row.createCell(indexColumn++);
			cell25.setCellValue(new HSSFRichTextString("Titular"));
			cell25.setCellStyle(styleCell);
			
			Cell cell26 = row.createCell(indexColumn++);
			cell26.setCellValue(new HSSFRichTextString("Adherente"));
			cell26.setCellStyle(styleCell);
		}else {			
			Cell cell25 = row.createCell(indexColumn++);
			cell25.setCellValue(new HSSFRichTextString("Parentesco"));
			cell25.setCellStyle(styleCell);
			
			Cell cell26 = row.createCell(indexColumn++);
			cell26.setCellValue(new HSSFRichTextString("Cant"));
			cell26.setCellStyle(styleCell);
			
		}
	}



	private static void createHeaderTotales(Row row,
			CellStyle styleHeader, boolean total_tercerizadora,
			boolean total_seccional, boolean total_plan, boolean total_empresa) {
		int indexColumn = 0;

		if (total_tercerizadora) {
			Cell cell0 = row.createCell(indexColumn);
			cell0.setCellValue(new HSSFRichTextString("Reporte Totales por Tercerizadora"));
			cell0.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_seccional) {
			Cell cell1 = row.createCell(indexColumn);
			cell1.setCellValue(new HSSFRichTextString("Reporte Totales por Seccional"));
			cell1.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_plan) {
			Cell cell2 = row.createCell(indexColumn);
			cell2.setCellValue(new HSSFRichTextString("Reporte Totales por Plan"));
			cell2.setCellStyle(styleHeader);
			indexColumn++;
		}
		if (total_empresa) {
			Cell cell21 = row.createCell(indexColumn);
			cell21.setCellValue(new HSSFRichTextString("Reporte de Totales por Empresa"));
			cell21.setCellStyle(styleHeader);
			indexColumn++;
		}else{
			Cell cell3 = row.createCell(indexColumn++);
			cell3.setCellValue(new HSSFRichTextString("Parentesco"));
			cell3.setCellStyle(styleHeader);

			Cell cell4 = row.createCell(indexColumn);
			cell4.setCellValue(new HSSFRichTextString("Total"));
			cell4.setCellStyle(styleHeader);
	
		}
		
			}

	private static void crearTitulosTotales(Sheet sheet, Row rowTitulo, CellStyle style, 
			BusquedaReportePadronFiltro filtro){
		
		String tituloReporte = "";
		if(filtro.isTotalesPorEntidad()){
			tituloReporte="Reporte de Totales por entidad - ".toUpperCase();
		}else if (filtro.isTotalesPorEmpresa()){
			tituloReporte="Reporte de Totales por empresa - ".toUpperCase();
		}else if (filtro.isTotalesPorPlan()){
			tituloReporte="Reporte de Totales por plan - ".toUpperCase();
		}else if (filtro.isTotalesPorSeccional()){
			tituloReporte="Reporte de Totales por seccional - ".toUpperCase();
		}else if (filtro.isTotalesPorTercerizadora()){
			tituloReporte="Reporte de Totales por tercerizadora - ".toUpperCase();
		}				
//		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
//		gmtMenos3.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int colNum = 0;
		
		style.setWrapText(true);

		Cell cell0 = rowTitulo.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString(tituloReporte +filtro.getDescripcionFiltros()) + 
				"        Fecha Listado: " + sdf.format(gmtMenos3.getTime()));
		
		rowTitulo.setHeight((short)650);
//		sheet.setColumnGroupCollapsed(colNum, true);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
	}
	private static int createHeaderTotalEntidades(Sheet sheet, int index,
			CellStyle styleHeader, CellStyle style, BusquedaReportePadronFiltro filtro) {
		Row rowTitulo = sheet.createRow(index++);
//		Cell cellTitulo = rowTitulo.createCell(0);
//		cellTitulo.setCellValue(new HSSFRichTextString("Reporte de Totales por entidad al "+fecha));
//		cellTitulo.setCellStyle(style);
		crearTitulosTotales(sheet,rowTitulo,style,filtro);
		
		Row row = sheet.createRow(index++);
		int indexColumn = 0;

		Cell cell0 = row.createCell(indexColumn++);
		cell0.setCellValue(new HSSFRichTextString("Seccional"));
		cell0.setCellStyle(styleHeader);
		
		Cell cell2 = row.createCell(indexColumn++);
		cell2.setCellValue(new HSSFRichTextString("UOMA"));
		cell2.setCellStyle(styleHeader);	
		
		Cell cell3 = row.createCell(indexColumn++);
		cell3.setCellValue(new HSSFRichTextString("OSPIM"));
		cell3.setCellStyle(styleHeader);
		
		Cell cell4 = row.createCell(indexColumn++);
		cell4.setCellStyle(styleHeader);
		Cell cell5 = row.createCell(indexColumn++);
		cell5.setCellStyle(styleHeader);
		Cell cell6 = row.createCell(indexColumn++);
		cell6.setCellStyle(styleHeader);
		Cell cell7 = row.createCell(indexColumn++);
		cell7.setCellStyle(styleHeader);
		Cell cell8 = row.createCell(indexColumn++);
		cell8.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 7));
		
		Cell cell9 = row.createCell(indexColumn++);
		cell9.setCellValue(new HSSFRichTextString("AMTIMA"));
		cell9.setCellStyle(styleHeader);
		
		indexColumn=0;
		Row row2 = sheet.createRow(index);
		row2.createCell(indexColumn++); //Seccional
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		row2.createCell(indexColumn++); //UOMA
				
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
		

		Cell cell23 = row2.createCell(indexColumn++);
		cell23.setCellValue(new HSSFRichTextString("OSPIM TITULARES"));
		cell23.setCellStyle(styleHeader);
		
		Cell cell26 = row2.createCell(indexColumn++);
		cell26.setCellValue(new HSSFRichTextString("OSPIM ADHERENTES"));
		cell26.setCellStyle(styleHeader);
		
		Cell cell24 = row2.createCell(indexColumn++);
		cell24.setCellValue(new HSSFRichTextString("CAPITAS TITU."));
		cell24.setCellStyle(styleHeader);

		Cell cell27 = row2.createCell(indexColumn++);
		cell27.setCellValue(new HSSFRichTextString("CAPITAS ADHE."));
		cell27.setCellStyle(styleHeader);
		
		Cell cell25 = row2.createCell(indexColumn++);
		cell25.setCellValue(new HSSFRichTextString("DEREGULADOS TITU."));
		cell25.setCellStyle(styleHeader);

		Cell cell28 = row2.createCell(indexColumn++);
		cell28.setCellValue(new HSSFRichTextString("DESREGULADOS ADHE."));
		cell28.setCellStyle(styleHeader);
		
		row2.createCell(indexColumn++); //AMTIMA		
		
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8));
		
		Cell cell10 = row.createCell(indexColumn++);
		cell10.setCellValue(new HSSFRichTextString("TOTAL TITULARES"));
		cell10.setCellStyle(styleHeader);	

		Cell cell11 = row.createCell(indexColumn++);
		cell11.setCellValue(new HSSFRichTextString("TOTAL ADHERENTES"));
		cell11.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 9, 9));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 10, 10));

		return index;
	}

	private static SXSSFWorkbook getReporte(List<ReportePadronResult> repo, BusquedaReportePadronFiltro filtro, boolean esVistaAdmifarm) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1, colNum=0;
		Sheet sheet = wb.createSheet("Hoja " + sh);

		CellStyle styleDate = getStyleDateWbs(wb);

		int index = 0;
		Row row1 = sheet.createRow(index);		
		
		if (esVistaAdmifarm) {
		    createHeaderAdmifarm(row1);	   
		} else if (filtro.isVistaPrevencion()) {
			createTitulos(sheet, row1, filtro);
			
			index++;
			Row row2 = sheet.createRow(index);
			createHeaderTercerizadora(row2);
		}else{
			createHeader(row1);
			
		}
		
		if (repo != null) {
			for (ReportePadronResult r : repo) {
				index++;
				colNum = 0;
				if (index >= 1048576) {
					index = 0;
					sh++;
					sheet = wb.createSheet("Hoja " + sh);
					Row rowNew = sheet.createRow(index);
					
					if (esVistaAdmifarm) {
					    createHeaderAdmifarm(rowNew);	   
					} else if(filtro.isVistaPrevencion()){
						createHeaderTercerizadora(rowNew);
					}else{
						createHeader(rowNew);
					}
					
					index++;
				}
				Row rowI = sheet.createRow(index);
				if (esVistaAdmifarm) {
				    createReportePadronDetalleAdmifarm(colNum, styleDate, r, rowI);
				} else if(filtro.isVistaPrevencion()){
					createReportePadronDetalleTercerizadora(colNum, styleDate, r, rowI);
				}else{
					createReportePadronDetalle(colNum, styleDate, r, rowI );
				}
			}
		}
		
		index++;
		sheet.createRow(index);
		for(int j=0;j<50;j++){
			try {
				sheet.autoSizeColumn((short) j);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}	
		
		
		return wb;
	}

	private static void createTitulos(Sheet sheet, Row row, BusquedaReportePadronFiltro filtro) {
		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
		gmtMenos3.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int colNum = 0;
		
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("REPORTE PADRÓN - " +filtro.getDescripcionFiltros()) + "        Fecha Listado: " + sdf.format(gmtMenos3.getTime()));
		
		row.setHeight((short)550);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 45));
	}
	
	private static void createReportePadronDetalle(int colNum,
			CellStyle styleDate, ReportePadronResult r, Row rowI ) {
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty((r.getCuil_titular())));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuil()));
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getInte()));
		colNum++;
		Cell cell6 = rowI.createCell(colNum);
		cell6.setCellValue(r.getId_ospim());
		colNum++;
		Cell cell7 = rowI.createCell(colNum);
		cell7.setCellValue(r.getId_uoma());
		colNum++;
		Cell cell8 = rowI.createCell(colNum);
		cell8.setCellValue(r.getId_amtima());
		colNum++;
		
		Cell cell6Info = rowI.createCell(colNum);
		colNum++;
		if (r.getFecha_ospim() != null) {
			cell6Info.setCellValue(r.getFecha_ospim());
			cell6Info.setCellStyle(styleDate);
		} else {
			cell6Info.setCellValue(new HSSFRichTextString());
		}
		Cell cell7Info = rowI.createCell(colNum);
		if (r.getAlta_fecha() != null) {
			cell7Info.setCellValue(r.getAlta_fecha());
			cell7Info.setCellStyle(styleDate);
		} else {
			cell7Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getUnifica()));
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
					StringUtils.getValueOrEmpty(r.getSeccional()));			
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getId_tercerizadora()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getParentesco()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getApellido()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNombre()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocu_numero()));
		colNum++;
		Cell cell16Info = rowI.createCell(colNum);
		cell16Info.setCellValue(r.getNaci_fecha());
		cell16Info.setCellStyle(styleDate);
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSexo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCivil_esta()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNacionalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProvincia()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getLocalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPostal_codi()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCalle()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNumero()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPiso()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDepto()));
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getTelefono()));
//		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum, StringUtils.getValueOrEmpty(r.getTelefono1()));
//				StringUtils.getValueOrEmpty(r.getTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelLaboral()));
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEmail()));
		
		colNum++;
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getUnsuscribeEmail()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCategoria()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPlan()));
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getPlanOmint()));
//		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanPrevencion())));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getFarmaciaPrevencion()));
		colNum++;
		
		Cell cell32Info = rowI.createCell(colNum);
		if (r.getIngre_fecha() != null) {
			cell32Info.setCellValue(r.getIngre_fecha());
			cell32Info.setCellStyle(styleDate);
		} else {
			cell32Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		Cell cell33Info = rowI.createCell(colNum);
		if (r.getBaja_fecha() != null) {
			cell33Info.setCellValue(r.getBaja_fecha());
			cell33Info.setCellStyle(styleDate);
		} else {
			cell33Info.setCellValue(new HSSFRichTextString());
		}
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuit()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRazon_soc()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRamo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEscala_salarial()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDiscapacitado()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getMotivoBaja()));
		colNum++;
		
		if(r.getFecha_uoma()!=null){
			Cell cell40Info = rowI.createCell(colNum);
			cell40Info.setCellValue(r.getFecha_uoma());
			cell40Info.setCellStyle(styleDate);
		}else{
			CellUtil.createCell(rowI, colNum,"");
		}
		colNum++;
		if(r.getFecha_amtima()!=null){
			Cell cell41Info = rowI.createCell(colNum);
			cell41Info.setCellValue(r.getFecha_amtima());
			cell41Info.setCellStyle(styleDate);
		}else{
			CellUtil.createCell(rowI, colNum,"");
		}
		colNum++;				
		CellUtil.createCell(rowI, colNum, r.getPerteneceAlaOrganizacion()==1?"SI":"NO");
		colNum++;				
		CellUtil.createCell(rowI, colNum, StringUtils.getValueOrEmpty(r.getProyecto()));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getObraSocAnterior()));
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getTieneAntecedentesJudiciales()==1?"SI":"NO");
		/*colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroSocio() != 0 ? String.valueOf(r.getNroSocio()):""));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroCredencial() != null ? String.valueOf(r.getNroCredencial()):""));
		*/
		
	}
	
	private static void createReportePadronDetalleTercerizadora(int colNum,
			CellStyle styleDate, ReportePadronResult r, Row rowI) {

		
		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty((r.getCuil_titular())));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuil()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getInte()));
		colNum++;
		Cell cell0 = rowI.createCell(colNum);
		cell0.setCellValue(r.getId_ospim());
		
		colNum++;
		Cell cell1 = rowI.createCell(colNum);
		if (r.getFecha_ospim() != null) {
			cell1.setCellValue(r.getFecha_ospim());
			cell1.setCellStyle(styleDate);
		} else {
			cell1.setCellValue(new HSSFRichTextString());
		}			
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdSeccional()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSeccional()));
		colNum++;	
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getId_parentesco_sss()));
		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getParentesco()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getApellido()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNombre()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDocu_numero()));
		colNum++;
		Cell cell2 = rowI.createCell(colNum);
		cell2.setCellValue(r.getNaci_fecha());
		cell2.setCellStyle(styleDate);
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getSexo()));		
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getId_estado_civil_sss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCivil_esta()));
		
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdNacionalidadSSS()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNacionalidad()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdProvinciaSss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProvincia()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, String.valueOf(r.getIdLocalidadSss()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getLocalidad()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPostal_codi()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCalle()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNumero()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getPiso()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDepto()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelefono1()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCelular()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getTelLaboral()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getEmail()));
		
//		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getUnsuscribeEmail()));
		
		colNum++;
		//CellUtil.createCell(rowI, colNum,
		//		StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanPrevencion())));
		
		CellUtil.createCell(rowI, colNum,
						StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanTercerizadora())));
		
		colNum++;
//		CellUtil.createCell(rowI, colNum,
//				StringUtils.getValueOrEmpty(r.getFarmaciaPrevencion()));
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getFarmaciaTercerizadora()));
		
		colNum++;
		Cell cell5 = rowI.createCell(colNum);
		if (r.getVigenFecha() != null) {
			cell5.setCellValue(r.getVigenFecha());
			cell5.setCellStyle(styleDate);
		} else {
			cell5.setCellValue(new HSSFRichTextString());
		}
		
		/*
		Cell cell5 = rowI.createCell(colNum);
		if (r.getIngre_fecha() != null) {
			cell5.setCellValue(r.getIngre_fecha());
			cell5.setCellStyle(styleDate);
		} else {
			cell5.setCellValue(new HSSFRichTextString());
		}
		*/
		
		colNum++;
		Cell cell3 = rowI.createCell(colNum);
		if (r.getBaja_fecha() != null) {
			cell3.setCellValue(r.getBaja_fecha());
			cell3.setCellStyle(styleDate);
		} else {
			cell3.setCellValue(new HSSFRichTextString());
		}

		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getCuit()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getRazon_soc()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getDiscapacitado()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getIdMotivoBaja()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getMotivoBaja()));
		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getPerteneceAlaOrganizacion()==1?"SI":"NO");
		
		
		colNum++;
		Cell cell6 = rowI.createCell(colNum);
		if (r.getFpp()!= null) {
			cell6.setCellValue(r.getFpp());
			cell6.setCellStyle(styleDate);
		} else {
			cell6.setCellValue(new HSSFRichTextString());
		}
        
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getCopago());
		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getCategoria());
		
/*		
		colNum++;			
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getProyecto()));		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getObraSocAnterior()));		
		colNum++;
		CellUtil.createCell(rowI, colNum, r.getTieneAntecedentesJudiciales()==1?"SI":"NO");	
		
		// datos no encontrados en listado de padron 
		
		
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.isFarmaciaUoma()));
		colNum++;
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.isFarmaciaAmtima()));		
		colNum++;
		Cell cell4 = rowI.createCell(colNum);
		if (r.getFpp() != null) {
			cell4.setCellValue(r.getFpp());
			cell4.setCellStyle(styleDate);
		} else {
			cell4.setCellValue(new HSSFRichTextString());
		}
		
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroSocio() != 0 ? String.valueOf(r.getNroSocio()):""));
		colNum++;		
		CellUtil.createCell(rowI, colNum,
				StringUtils.getValueOrEmpty(r.getNroCredencial() != null ? String.valueOf(r.getNroCredencial()):""));
*/		
	}
	
	private static void createHeader(Row row) {
		int colNum = 0;
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		Cell cell1 = row.createCell(colNum);
		colNum++;
		cell1.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell2 = row.createCell(colNum);
		colNum++;
		cell2.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell3 = row.createCell(colNum);
		colNum++;
		cell3.setCellValue(new HSSFRichTextString("ID_OSPIM"));
		Cell cell4 = row.createCell(colNum);
		colNum++;
		cell4.setCellValue(new HSSFRichTextString("ID_UOMA"));
		Cell cell5 = row.createCell(colNum);
		colNum++;
		cell5.setCellValue(new HSSFRichTextString("ID_AMTIMA"));
		Cell cell6 = row.createCell(colNum);
		colNum++;
		cell6.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
		Cell cell7 = row.createCell(colNum);
		colNum++;
		cell7.setCellValue(new HSSFRichTextString("FECHA REGISTRO"));
		Cell cell8 = row.createCell(colNum);
		colNum++;
		cell8.setCellValue(new HSSFRichTextString("UNIFICA"));
		Cell cell9 = row.createCell(colNum);
		colNum++;
		cell9.setCellValue(new HSSFRichTextString("SECCIONAL"));
		Cell cell10 = row.createCell(colNum);
		colNum++;
		cell10.setCellValue(new HSSFRichTextString("TERCERIZADORA"));
		Cell cell11 = row.createCell(colNum);
		colNum++;
		cell11.setCellValue(new HSSFRichTextString("PARENTESCO"));
		Cell cell12 = row.createCell(colNum);
		colNum++;
		cell12.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell13 = row.createCell(colNum);
		colNum++;
		cell13.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell14 = row.createCell(colNum);
		colNum++;
		cell14.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell15 = row.createCell(colNum);
		colNum++;
		cell15.setCellValue(new HSSFRichTextString("NRO DOC"));
		Cell cell16 = row.createCell(colNum);
		colNum++;
		cell16.setCellValue(new HSSFRichTextString("FECHA NAC"));
		Cell cell17 = row.createCell(colNum);
		colNum++;
		cell17.setCellValue(new HSSFRichTextString("SEXO"));
		Cell cell18 = row.createCell(colNum);
		colNum++;
		cell18.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		Cell cell19 = row.createCell(colNum);
		colNum++;
		cell19.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		Cell cell20 = row.createCell(colNum);
		colNum++;
		cell20.setCellValue(new HSSFRichTextString("PROVINCIA"));
		Cell cell21 = row.createCell(colNum);
		colNum++;
		cell21.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		Cell cell22 = row.createCell(colNum);
		colNum++;
		cell22.setCellValue(new HSSFRichTextString("CP"));
		Cell cell23 = row.createCell(colNum);
		colNum++;
		cell23.setCellValue(new HSSFRichTextString("CALLE"));
		Cell cell24 = row.createCell(colNum);
		colNum++;
		cell24.setCellValue(new HSSFRichTextString("NUMERO"));
		Cell cell25 = row.createCell(colNum);
		colNum++;
		cell25.setCellValue(new HSSFRichTextString("PISO"));
		Cell cell26 = row.createCell(colNum);
		colNum++;
		cell26.setCellValue(new HSSFRichTextString("DEPTO"));
//		Cell cell27 = row.createCell(colNum);
//		colNum++;
//		cell27.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell28_ = row.createCell(colNum);
		colNum++;
		cell28_.setCellValue(new HSSFRichTextString("COD.AREA TEL."));
		Cell cell29_ = row.createCell(colNum);
		colNum++;
		cell29_.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell30_ = row.createCell(colNum);
		colNum++;
		cell30_.setCellValue(new HSSFRichTextString("COD.AREA CELU."));
		Cell cell31_ = row.createCell(colNum);
		colNum++;
		cell31_.setCellValue(new HSSFRichTextString("CELULAR"));
		Cell cell32_ = row.createCell(colNum);
		colNum++;
		cell32_.setCellValue(new HSSFRichTextString("COD.AREA TEL.LABO."));
		Cell cell33_ = row.createCell(colNum);
		colNum++;
		cell33_.setCellValue(new HSSFRichTextString("TELEF. LABORAL"));
		
		Cell cell28 = row.createCell(colNum);
		colNum++;
		cell28.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		
		Cell cell281 = row.createCell(colNum);
		colNum++;
		cell281.setCellValue(new HSSFRichTextString("UNSUSCRIBE EMAIL"));
		
		Cell cell29 = row.createCell(colNum);
		colNum++;
		cell29.setCellValue(new HSSFRichTextString("CATEGORIA"));
		Cell cell30 = row.createCell(colNum);
		colNum++;
		cell30.setCellValue(new HSSFRichTextString("PLAN"));
//		Cell cell31 = row.createCell(colNum);
//		colNum++;
//		cell31.setCellValue(new HSSFRichTextString("PLAN OMINT"));
		Cell cell31a = row.createCell(colNum);
		colNum++;
		cell31a.setCellValue(new HSSFRichTextString("PLAN ENSALUD"));
		Cell cell31b = row.createCell(colNum);
		colNum++;
		cell31b.setCellValue(new HSSFRichTextString("FARMACIA ENSALUD"));
		Cell cell32 = row.createCell(colNum);
		colNum++;
		cell32.setCellValue(new HSSFRichTextString("FECHA PROCESO"));
		Cell cell33 = row.createCell(colNum);
		colNum++;
		cell33.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell34 = row.createCell(colNum);
		colNum++;
		cell34.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell35 = row.createCell(colNum);
		colNum++;
		cell35.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		Cell cell36 = row.createCell(colNum);
		colNum++;
		cell36.setCellValue(new HSSFRichTextString("RAMO"));
		Cell cell37 = row.createCell(colNum);
		colNum++;
		cell37.setCellValue(new HSSFRichTextString("ESCALA SALARIAL"));
		Cell cell38 = row.createCell(colNum);
		colNum++;
		cell38.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		Cell cell39 = row.createCell(colNum);
		colNum++;
		cell39.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		Cell cell40 = row.createCell(colNum);
		colNum++;
		cell40.setCellValue(new HSSFRichTextString("FECHA UOMA"));
		Cell cell41 = row.createCell(colNum);
		colNum++;
		cell41.setCellValue(new HSSFRichTextString("FECHA AMTIMA"));
		Cell cell42 = row.createCell(colNum);
		colNum++;
		cell42.setCellValue(new HSSFRichTextString("PERTENECE A LA ORG."));
		Cell cell43 = row.createCell(colNum);
		colNum++;
		cell43.setCellValue(new HSSFRichTextString("PROYECTO"));
		Cell cell44 = row.createCell(colNum);
		colNum++;
		cell44.setCellValue(new HSSFRichTextString("OOSS ANTERIOR"));
		Cell cell45 = row.createCell(colNum);
		colNum++;
		cell45.setCellValue(new HSSFRichTextString("ANTEC.JUDICIALES"));
		Cell cell46 = row.createCell(colNum);
		/*colNum++;
		cell46.setCellValue(new HSSFRichTextString("NRO SOCIO"));
		Cell cell47 = row.createCell(colNum);
		colNum++;
		cell47.setCellValue(new HSSFRichTextString("NRO CREDENCIAL PREVENCION"));
		*/
	}
	
	private static void createHeaderTercerizadora(Row row) {
	
		int colNum = 0;
		
		Cell cell0 = row.createCell(colNum);
		colNum++;
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		Cell cell00 = row.createCell(colNum);
		colNum++;
		cell00.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell1 = row.createCell(colNum);
		colNum++;
		cell1.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell2 = row.createCell(colNum);
		colNum++;
		cell2.setCellValue(new HSSFRichTextString("ID_OSPIM"));
		Cell cell3 = row.createCell(colNum);
		colNum++;
		cell3.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
		
		
		Cell cell51 = row.createCell(colNum);
		colNum++;
		cell51.setCellValue(new HSSFRichTextString("ID SECCIONAL"));
		
		Cell cell4 = row.createCell(colNum);
		colNum++;
		cell4.setCellValue(new HSSFRichTextString("SECCIONAL"));
		
		Cell cell50 = row.createCell(colNum);
		colNum++;
		cell50.setCellValue(new HSSFRichTextString("ID PARENT."));
		
		Cell cell5 = row.createCell(colNum);
		colNum++;
		cell5.setCellValue(new HSSFRichTextString("PARENTESCO"));
		
		Cell cell6 = row.createCell(colNum);
		colNum++;
		cell6.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell7 = row.createCell(colNum);
		colNum++;
		cell7.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell8 = row.createCell(colNum);
		colNum++;
		cell8.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell9 = row.createCell(colNum);
		colNum++;
		cell9.setCellValue(new HSSFRichTextString("NRO DOC"));
		Cell cell10 = row.createCell(colNum);
		colNum++;
		cell10.setCellValue(new HSSFRichTextString("FECHA NAC"));
		Cell cell12 = row.createCell(colNum);
		colNum++;
		cell12.setCellValue(new HSSFRichTextString("SEXO"));
		
		
		Cell cell49 = row.createCell(colNum);
		colNum++;
		cell49.setCellValue(new HSSFRichTextString("ID EST.CIVIL"));
		
		Cell cell13 = row.createCell(colNum);
		colNum++;
		cell13.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		
		
		Cell cell45 = row.createCell(colNum);
		colNum++;
		cell45.setCellValue(new HSSFRichTextString("ID NACION."));
		
		Cell cell14 = row.createCell(colNum);
		colNum++;
		cell14.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		
		Cell cell46 = row.createCell(colNum);
		colNum++;
		cell46.setCellValue(new HSSFRichTextString("ID PROV."));
		
		Cell cell15 = row.createCell(colNum);
		colNum++;
		cell15.setCellValue(new HSSFRichTextString("PROVINCIA"));
		
		
		Cell cell47 = row.createCell(colNum);
		colNum++;
		cell47.setCellValue(new HSSFRichTextString("ID LOC."));
		
		
		Cell cell16 = row.createCell(colNum);
		colNum++;
		cell16.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		
		
		Cell cell17 = row.createCell(colNum);
		colNum++;
		cell17.setCellValue(new HSSFRichTextString("CP"));
		Cell cell18 = row.createCell(colNum);
		colNum++;
		cell18.setCellValue(new HSSFRichTextString("CALLE"));
		Cell cell19 = row.createCell(colNum);
		colNum++;
		cell19.setCellValue(new HSSFRichTextString("NUMERO"));
		Cell cell20 = row.createCell(colNum);
		colNum++;
		cell20.setCellValue(new HSSFRichTextString("PISO"));
		Cell cell21 = row.createCell(colNum);
		colNum++;
		cell21.setCellValue(new HSSFRichTextString("DEPTO"));
		Cell cell22 = row.createCell(colNum);
		colNum++;
		cell22.setCellValue(new HSSFRichTextString("COD.AREA TEL."));
		Cell cell23 = row.createCell(colNum);
		colNum++;
		cell23.setCellValue(new HSSFRichTextString("TELEFONO"));
		Cell cell24 = row.createCell(colNum);
		colNum++;
		cell24.setCellValue(new HSSFRichTextString("COD.AREA CELU."));
		Cell cell25 = row.createCell(colNum);
		colNum++;
		cell25.setCellValue(new HSSFRichTextString("CELULAR"));
		Cell cell26 = row.createCell(colNum);
		colNum++;
		cell26.setCellValue(new HSSFRichTextString("COD.AREA TEL.LABO."));
		Cell cell27 = row.createCell(colNum);
		colNum++;
		cell27.setCellValue(new HSSFRichTextString("TELEF. LABORAL"));
		Cell cell28 = row.createCell(colNum);
		colNum++;
		cell28.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		Cell cell281 = row.createCell(colNum);
		
		//colNum++;
		//cell281.setCellValue(new HSSFRichTextString("UNSUSCRIBE EMAIL"));
		
		Cell cell29 = row.createCell(colNum);
		colNum++;
		cell29.setCellValue(new HSSFRichTextString("PLAN TERCERIZADORA"));
		Cell cell30 = row.createCell(colNum);
		colNum++;
		cell30.setCellValue(new HSSFRichTextString("FARMACIA TERCERIZADORA"));
		Cell cell31 = row.createCell(colNum);
		colNum++;
		cell31.setCellValue(new HSSFRichTextString("FECHA VIGENCIA"));
		Cell cell32 = row.createCell(colNum);
		colNum++;
		cell32.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell33 = row.createCell(colNum);
		colNum++;
		cell33.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell34 = row.createCell(colNum);
		colNum++;
		cell34.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		Cell cell35 = row.createCell(colNum);
		colNum++;
		cell35.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		
		Cell cell48 = row.createCell(colNum);
		colNum++;
		cell48.setCellValue(new HSSFRichTextString("ID MOT. BAJA"));
		
		Cell cell36 = row.createCell(colNum);
		colNum++;
		cell36.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		Cell cell38 = row.createCell(colNum);
		colNum++;
		cell38.setCellValue(new HSSFRichTextString("PERTENECE A LA ORG."));
		Cell cell39 = row.createCell(colNum);
		cell39.setCellValue(new HSSFRichTextString("VTO. PMI"));
		colNum++;
		Cell cell40 = row.createCell(colNum);
		cell40.setCellValue(new HSSFRichTextString("COPAGO"));
		colNum++;
		Cell cell41 = row.createCell(colNum);
		cell41.setCellValue(new HSSFRichTextString("CATEGORIA"));
		
/*		
		colNum++;
		cell39.setCellValue(new HSSFRichTextString("PROYECTO"));
		Cell cell40 = row.createCell(colNum);
		colNum++;
		cell40.setCellValue(new HSSFRichTextString("OOSS ANTERIOR"));
		Cell cell41 = row.createCell(colNum);
		colNum++;
		cell41.setCellValue(new HSSFRichTextString("ANTEC.JUDICIALES"));
		Cell cell42 = row.createCell(colNum);
		colNum++;
		cell42.setCellValue(new HSSFRichTextString("FARMACIA AMTIMA"));
		Cell cell43 = row.createCell(colNum);
		colNum++;
		cell43.setCellValue(new HSSFRichTextString("FAMACIA UOMA"));
		Cell cell44 = row.createCell(colNum);
		colNum++;
		cell44.setCellValue(new HSSFRichTextString("PMI"));
		Cell cell52 = row.createCell(colNum);
		colNum++;
		cell52.setCellValue(new HSSFRichTextString("NRO SOCIO"));
		Cell cell53 = row.createCell(colNum);
		colNum++;
		cell53.setCellValue(new HSSFRichTextString("NRO CREDENCIAL PREVENCION"));
*/		
		
		
		
	}

	private static String getValue(String o) {
		if (o != null) {
			return o;
		} else {
			return "";
		}
	}
	
	
	private static SXSSFWorkbook getReporteBajas(List<ReportePadronResult> repo) {
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int sh = 1;
		Sheet sheet = wb.createSheet("Hoja " + sh);

		CellStyle styleDate = getStyleDateWbs(wb);

		int index = 0;
		Row row = sheet.createRow(index);
		createHeaderBajas(row);
		if (repo != null) {
			for (ReportePadronResult r : repo) {
				index++;
				if (index >= 1048576) {
					index = 0;
					sh++;
					sheet = wb.createSheet("Hoja " + sh);
					Row rowNew = sheet.createRow(index);
					createHeaderBajas(rowNew);
				}
				int col = 0;
				Row rowI = sheet.createRow(index);
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty((r.getCuil_titular())));
//				CellUtil.createCell(rowI, 1,
//						StringUtils.getValueOrEmpty(r.getCuil()));
				CellUtil.createCell(rowI, col++, String.valueOf(r.getInte()));
				
				Cell cell6 = rowI.createCell(col++);
				cell6.setCellValue(r.getId_ospim());
				
//				Cell cell7 = rowI.createCell(col++);
//				cell7.setCellValue(r.getId_uoma());
//				
//				Cell cell8 = rowI.createCell(col++);
//				cell8.setCellValue(r.getId_amtima());
								
				Cell cell6Info = rowI.createCell(col++);
				if (r.getFecha_ospim() != null) {
					cell6Info.setCellValue(r.getFecha_ospim());
					cell6Info.setCellStyle(styleDate);
				} else {
					cell6Info.setCellValue(new HSSFRichTextString());
				}
				
//				Cell cell7Info = rowI.createCell(col++);
//				if (r.getFecha_uoma() != null) {
//					cell7Info.setCellValue(r.getFecha_uoma());
//					cell7Info.setCellStyle(styleDate);
//				} else {
//					cell7Info.setCellValue(new HSSFRichTextString());
//				}
//				
//				Cell cell8Info = rowI.createCell(col++);
//				if (r.getFecha_amtima() != null) {
//					cell8Info.setCellValue(r.getFecha_amtima());
//					cell8Info.setCellStyle(styleDate);
//				} else {
//					cell8Info.setCellValue(new HSSFRichTextString());
//				}
				
				Cell cell9Info = rowI.createCell(col++);
				if (r.getFecha_proceso() != null) {
					cell9Info.setCellValue(r.getFecha_proceso() /*Alta_fecha()*/);
					cell9Info.setCellStyle(styleDate);
				} else {
					cell9Info.setCellValue(new HSSFRichTextString());
				}
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getUnifica()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getSeccional()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getId_tercerizadora()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getParentesco()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getApellido()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getNombre()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getDocumento_tipo()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getDocu_numero()));
//				Cell cell18Info = rowI.createCell(col++);
//				cell18Info.setCellValue(r.getNaci_fecha());
//				cell18Info.setCellStyle(styleDate);
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getSexo()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getCivil_esta()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getNacionalidad()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getProvincia()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getLocalidad()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getPostal_codi()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getCalle()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getNumero()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getPiso()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getDepto()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getTelefono()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getEmail()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getCategoria()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getPlan()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getPlanOmint()));
//				Cell cell34Info = rowI.createCell(col++);
//				if (r.getIngre_fecha() != null) {
//					cell34Info.setCellValue(r.getIngre_fecha());
//					cell34Info.setCellStyle(styleDate);
//				} else {
//					cell34Info.setCellValue(new HSSFRichTextString());
//				}
				Cell cell35Info = rowI.createCell(col++);
				if (r.getBaja_fecha() != null) {
					cell35Info.setCellValue(r.getBaja_fecha());
					cell35Info.setCellStyle(styleDate);
				} else {
					cell35Info.setCellValue(new HSSFRichTextString());
				}
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getCuit()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getRazon_soc()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getRamo()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getEscala_salarial()));
//				CellUtil.createCell(rowI, col++,
//						StringUtils.getValueOrEmpty(r.getDiscapacitado()));
				CellUtil.createCell(rowI, col++,
						StringUtils.getValueOrEmpty(r.getMotivoBaja()));
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
		
		return wb;
	}

	private static void createHeaderBajas(Row row) {
		int col = 0;
		Cell cell0 = row.createCell(col++);
		cell0.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
//		Cell cell1 = row.createCell(col++);
//		cell1.setCellValue(new HSSFRichTextString("CUIL"));
		Cell cell2 = row.createCell(col++);
		cell2.setCellValue(new HSSFRichTextString("INTE"));
		Cell cell3 = row.createCell(col++);
		cell3.setCellValue(new HSSFRichTextString("ID_OSPIM"));
//		Cell cell4 = row.createCell(col++);
//		cell4.setCellValue(new HSSFRichTextString("ID_UOMA"));
//		Cell cell5 = row.createCell(col++);
//		cell5.setCellValue(new HSSFRichTextString("ID_AMTIMA"));
		Cell cell6 = row.createCell(col++);
		cell6.setCellValue(new HSSFRichTextString("FECHA OSPIM"));
//		Cell cell7 = row.createCell(col++);
//		cell7.setCellValue(new HSSFRichTextString("FECHA UOMA"));
//		Cell cell8 = row.createCell(col++);
//		cell8.setCellValue(new HSSFRichTextString("FECHA AMTIMA"));
		Cell cell9 = row.createCell(col++);
		cell9.setCellValue(new HSSFRichTextString("FECHA PROCESO"));
//		Cell cell10 = row.createCell(col++);
//		cell10.setCellValue(new HSSFRichTextString("UNIFICA"));
		Cell cell11 = row.createCell(col++);
		cell11.setCellValue(new HSSFRichTextString("SECCIONAL"));
//		Cell cell12 = row.createCell(col++);
//		cell12.setCellValue(new HSSFRichTextString("TERCERIZADORA"));
		Cell cell13 = row.createCell(col++);
		cell13.setCellValue(new HSSFRichTextString("PARENTESCO"));
		Cell cell14 = row.createCell(col++);
		cell14.setCellValue(new HSSFRichTextString("APELLIDO"));
		Cell cell15 = row.createCell(col++);
		cell15.setCellValue(new HSSFRichTextString("NOMBRE"));
		Cell cell16 = row.createCell(col++);
		cell16.setCellValue(new HSSFRichTextString("TIPO DOC"));
		Cell cell17 = row.createCell(col++);
		cell17.setCellValue(new HSSFRichTextString("NRO DOC"));
//		Cell cell18 = row.createCell(col++);
//		cell18.setCellValue(new HSSFRichTextString("FECHA NAC"));
//		Cell cell19 = row.createCell(col++);
//		cell19.setCellValue(new HSSFRichTextString("SEXO"));
//		Cell cell20 = row.createCell(col++);
//		cell20.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
//		Cell cell21 = row.createCell(col++);
//		cell21.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
//		Cell cell22 = row.createCell(col++);
//		cell22.setCellValue(new HSSFRichTextString("PROVINCIA"));
//		Cell cell23 = row.createCell(col++);
//		cell23.setCellValue(new HSSFRichTextString("LOCALIDAD"));
//		Cell cell24 = row.createCell(col++);
//		cell24.setCellValue(new HSSFRichTextString("CP"));
//		Cell cell25 = row.createCell(col++);
//		cell25.setCellValue(new HSSFRichTextString("CALLE"));
//		Cell cell26 = row.createCell(col++);
//		cell26.setCellValue(new HSSFRichTextString("NUMERO"));
//		Cell cell27 = row.createCell(col++);
//		cell27.setCellValue(new HSSFRichTextString("PISO"));
//		Cell cell28 = row.createCell(col++);
//		cell28.setCellValue(new HSSFRichTextString("DEPTO"));
//		Cell cell29 = row.createCell(col++);
//		cell29.setCellValue(new HSSFRichTextString("TELEFONO"));
//		Cell cell30 = row.createCell(col++);
//		cell30.setCellValue(new HSSFRichTextString("CORREO ELECTRONICO"));
		Cell cell31 = row.createCell(col++);
		cell31.setCellValue(new HSSFRichTextString("CATEGORIA"));
		Cell cell32 = row.createCell(col++);
		cell32.setCellValue(new HSSFRichTextString("PLAN"));
		Cell cell33 = row.createCell(col++);
		cell33.setCellValue(new HSSFRichTextString("PLAN OMINT"));
//		Cell cell34 = row.createCell(col++);
//		cell34.setCellValue(new HSSFRichTextString("FECHA INGRESO"));
		Cell cell35 = row.createCell(col++);
		cell35.setCellValue(new HSSFRichTextString("FECHA BAJA"));
		Cell cell36 = row.createCell(col++);
		cell36.setCellValue(new HSSFRichTextString("CUIT"));
		Cell cell37 = row.createCell(col++);
		cell37.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
//		Cell cell38 = row.createCell(col++);
//		cell38.setCellValue(new HSSFRichTextString("RAMO"));
//		Cell cell39 = row.createCell(col++);
//		cell39.setCellValue(new HSSFRichTextString("ESCALA SALARIAL"));
//		Cell cell40 = row.createCell(col++); 
//		cell40.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		Cell cell41 = row.createCell(col++);
		cell41.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
	}
	
	private static void createHeaderAdmifarm(Row row) {

	    int colNum = 0;

	    CellUtil.createCell(row, colNum++, "TIPO DOC");
	    CellUtil.createCell(row, colNum++, "NRO DOC");
	    CellUtil.createCell(row, colNum++, "NUMERO AFILIADO");
	    CellUtil.createCell(row, colNum++, "CATEGORIA");
	    CellUtil.createCell(row, colNum++, "PARENTESCO");
	    CellUtil.createCell(row, colNum++, "APELLIDO");
	    CellUtil.createCell(row, colNum++, "NOMBRE");
	    CellUtil.createCell(row, colNum++, "FECHA NAC");
	    CellUtil.createCell(row, colNum++, "SEXO");
	    CellUtil.createCell(row, colNum++, "ESTADO CIVIL");
	    CellUtil.createCell(row, colNum++, "NACIONALIDAD");
	    CellUtil.createCell(row, colNum++, "PROVINCIA");
	    CellUtil.createCell(row, colNum++, "LOCALIDAD");
	    CellUtil.createCell(row, colNum++, "CP");
	    CellUtil.createCell(row, colNum++, "CALLE");
	    CellUtil.createCell(row, colNum++, "NUMERO");
	    CellUtil.createCell(row, colNum++, "PISO");
	    CellUtil.createCell(row, colNum++, "DEPTO");
	    CellUtil.createCell(row, colNum++, "COD.AREA TEL.");
	    CellUtil.createCell(row, colNum++, "TELEFONO");
	    CellUtil.createCell(row, colNum++, "COD.AREA CELU.");
	    CellUtil.createCell(row, colNum++, "CELULAR");
	    CellUtil.createCell(row, colNum++, "COD.AREA TEL.LABO.");
	    CellUtil.createCell(row, colNum++, "TELEF. LABORAL");
	    CellUtil.createCell(row, colNum++, "CORREO ELECTRONICO");
	    CellUtil.createCell(row, colNum++, "PLAN TERCERIZADORA");
	    CellUtil.createCell(row, colNum++, "FARMACIA TERCERIZADORA");
	    CellUtil.createCell(row, colNum++, "FECHA VIGENCIA");
	    CellUtil.createCell(row, colNum++, "FECHA BAJA");
	    CellUtil.createCell(row, colNum++, "CUIT");
	    CellUtil.createCell(row, colNum++, "RAZON SOCIAL");
	    CellUtil.createCell(row, colNum++, "DISCAPACITADO");
	    CellUtil.createCell(row, colNum++, "ID MOT. BAJA");
	    CellUtil.createCell(row, colNum++, "MOTIVO BAJA");
	    CellUtil.createCell(row, colNum++, "PERTENECE A LA ORG.");
	    CellUtil.createCell(row, colNum++, "VTO. PMI");
	    CellUtil.createCell(row, colNum++, "COPAGO");
	    CellUtil.createCell(row, colNum++, "CATEGORIA");
	    CellUtil.createCell(row, colNum++, "CUIL TITULAR");
	    CellUtil.createCell(row, colNum++, "CUIL");
	    CellUtil.createCell(row, colNum++, "FECHA OSPIM");
	    CellUtil.createCell(row, colNum++, "SECCIONAL");
	    CellUtil.createCell(row, colNum++, "PLAN AFILIADO");
	    CellUtil.createCell(row, colNum++, "PMI");
	    CellUtil.createCell(row, colNum++, "ACO");
	}
	
	private static void createReportePadronDetalleAdmifarm(int colNum, CellStyle styleDate,
			ReportePadronResult r, Row rowI) {

	    // TIPO DOC
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDocumento_tipo()));

	    // NRO DOC
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDocu_numero()));

	    // NUMERO AFILIADO
	    CellUtil.createCell(rowI, colNum++, String.valueOf(r.getId_ospim()));

	    // CATEGORIA
	    CellUtil.createCell(rowI, colNum++, String.valueOf(r.getInte()));

	    // PARENTESCO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getParentesco()));

	    // APELLIDO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getApellido()));

	    // NOMBRE
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNombre()));

	    // FECHA NAC
	    Cell fechaNac = rowI.createCell(colNum++);
	    if (r.getNaci_fecha() != null) {
	        fechaNac.setCellValue(r.getNaci_fecha());
	        fechaNac.setCellStyle(styleDate);
	    } else {
	        fechaNac.setCellValue("");
	    }

	    // SEXO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getSexo()));

	    // ESTADO CIVIL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCivil_esta()));

	    // NACIONALIDAD
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNacionalidad()));

	    // PROVINCIA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getProvincia()));

	    // LOCALIDAD
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getLocalidad()));

	    // CP
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getPostal_codi()));

	    // CALLE
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCalle()));

	    // NUMERO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getNumero()));

	    // PISO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getPiso()));

	    // DEPTO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDepto()));

	    // COD.AREA TEL.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaTelefono()));

	    // TELEFONO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getTelefono1()));

	    // COD.AREA CELU.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaCelular()));

	    // CELULAR
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCelular()));

	    // COD.AREA TEL.LABO.
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCodAreaTelLaboral()));

	    // TELEF. LABORAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getTelLaboral()));

	    // CORREO ELECTRONICO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getEmail()));

	    // PLAN TERCERIZADORA
	    String planTercerizadora = StringUtils.getValueOrEmpty(Plan.getHealthPlan(r.getPlanTercerizadora()));

	    CellUtil.createCell(rowI, colNum++, planTercerizadora);

	    // FARMACIA TERCERIZADORA
	    String farmaciaTercerizadora =StringUtils.getValueOrEmpty(r.getFarmaciaTercerizadora());

	    CellUtil.createCell(rowI, colNum++, farmaciaTercerizadora);

	    // FECHA VIGENCIA
	    Cell fechaVigencia = rowI.createCell(colNum++);
	    if (r.getVigenFecha() != null) {
	        fechaVigencia.setCellValue(r.getVigenFecha());
	        fechaVigencia.setCellStyle(styleDate);
	    } else {
	        fechaVigencia.setCellValue("");
	    }

	    // FECHA BAJA
	    Cell fechaBaja = rowI.createCell(colNum++);
	    if (r.getBaja_fecha() != null) {
	        fechaBaja.setCellValue(r.getBaja_fecha());
	        fechaBaja.setCellStyle(styleDate);
	    } else {
	        fechaBaja.setCellValue("");
	    }

	    // CUIT
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuit()));

	    // RAZON SOCIAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getRazon_soc()));

	    // DISCAPACITADO
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getDiscapacitado()));

	    // ID MOT. BAJA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getIdMotivoBaja()));

	    // MOTIVO BAJA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getMotivoBaja()));

	    // PERTENECE A LA ORG.
	    CellUtil.createCell(rowI, colNum++, r.getPerteneceAlaOrganizacion() == 1 ? "SI" : "NO");

	    // VTO. PMI
	    Cell fechaPmi = rowI.createCell(colNum++);
	    if (r.getFpp() != null) {
	        fechaPmi.setCellValue(r.getFpp());
	        fechaPmi.setCellStyle(styleDate);
	    } else {
	        fechaPmi.setCellValue("");
	    }

	    // COPAGO
	    CellUtil.createCell(rowI, colNum++, r.getCopago());

	    // CATEGORIA
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCategoria()));

	    // CUIL TITULAR
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuil_titular()));

	    // CUIL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getCuil()));

	    // FECHA OSPIM
	    Cell fechaOspim = rowI.createCell(colNum++);
	    if (r.getFecha_ospim() != null) {
	        fechaOspim.setCellValue(r.getFecha_ospim());
	        fechaOspim.setCellStyle(styleDate);
	    } else {
	        fechaOspim.setCellValue("");
	    }

	    // SECCIONAL
	    CellUtil.createCell(rowI, colNum++, StringUtils.getValueOrEmpty(r.getSeccional()));

	    // PLAN AFILIADO
	    CellUtil.createCell(
	            rowI,
	            colNum++,
	            StringUtils.getValueOrEmpty(r.getPlanAfiliado())
	    );

	    // PMI
	    CellUtil.createCell(
	            rowI,
	            colNum++,
	            StringUtils.getValueOrEmpty(r.getPmi())
	    );

	    // ACO
	    CellUtil.createCell(
	            rowI,
	            colNum++,
	            StringUtils.getValueOrEmpty(r.getAco())
	    );

	}

}
>>>>>>> .r7323
