package ar.com.ospim.afiliados.reportes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.Baja;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;
import ar.com.ospim.afiliados.reportes.beans.PadronInformado;
import ar.com.ospim.afiliados.reportes.beans.PanelControlAfiliado;
import ar.com.ospim.afiliados.reportes.beans.ReporteCredenResult;
import ar.com.ospim.afiliados.reportes.beans.ReporteLegajosCred;
import ar.com.ospim.afiliados.reportes.beans.ReportePadronTotalResult;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReportesAfiliadoServiceImpl {
	private static Log logger = LogFactoryUtil.getLog(ReportesAfiliadoServiceImpl.class);
	
	public List <PadronInformado> getUltimoPadronInformado() throws Exception{
		Connection con = null;
		CallableStatement stmt = null;
		List<PadronInformado> result=null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  informes.traer_ultimo_padron()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PadronInformado>();
			while (rs.next()) {
				PadronInformado padron = new PadronInformado(
						rs.getDate("fecha"), rs.getString("id_terc"), 
						rs.getString("tercerizadora"), rs.getString("tipo"),
						rs.getDate("vigencia_listado"));
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}

		return result;
	}
	
	public List <PadronInformado> getUltimoListadoCredenInformado() throws Exception{
		Connection con = null;
		CallableStatement stmt = null;
		List<PadronInformado> result=null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  informes.traer_ultimo_reporte_credencial()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PadronInformado>();
			while (rs.next()) {
				PadronInformado padron = new PadronInformado(
						rs.getDate("fecha"), rs.getString("id_terc"), rs.getString("tercerizadora"), rs.getString("tipo"), null);
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}

		return result;
	}
	

	public List<ReportePosiblesInconsistenciasResult> getReportePosiblesInconsistencias()
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<ReportePosiblesInconsistenciasResult> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  reporte_errores_padron()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<ReportePosiblesInconsistenciasResult>();
			while (rs.next()) {
				ReportePosiblesInconsistenciasResult padron = new ReportePosiblesInconsistenciasResult(
						rs.getString("cuil_titular"), rs.getInt("inte"),
						rs.getString("id_terc"), rs.getString("observaciones"));
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}

		return result;

	}

	public List<ReportePadronResult> getReportePadron(BusquedaReportePadronFiltro filtro) throws SystemException {

		logger.debug("Buscando padron");

		Connection con = null;
		CallableStatement stmt = null;
		List<ReportePadronResult> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql=null;
			if(filtro.getTipoBusqueda()==1){
				sql = "{call  reporte_padron_alta(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}else if(filtro.getTipoBusqueda()==2){
//				sql = "{call  reporte_padron_baja(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				sql = "{call  reporte_padron_baja_f_proceso(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}else if(filtro.getTipoBusqueda()==3){
//				Padrón de vigentes, si es marca vista Prevencion (se usa ahora para Ensalud)
//				excluimos los beneficiario suspendidos en la cobertura médica
				if(filtro.isVistaAdmifarm()) {

			        sql = "{call reporte_padron_admifarm(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			    }else if(filtro.isVistaPrevencion()) {
					sql = "{call  reporte_padron_con_cobertura(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				}else {
					sql = "{call  reporte_padron(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				}
				
			}else if(filtro.getTipoBusqueda()==4){
				sql = "{call  reporte_padron_seccional(?)}";
			}
			stmt = con.prepareCall(sql.toString());

			setearParametrosQueryPadron(filtro, stmt);

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReportePadronResult>();
			while (rs.next()) {
				ReportePadronResult padron = null;
						
				if(filtro.getTipoBusqueda()==2){
					padron = ReportePadronResult.getMapping2(rs);
				}else if(filtro.isVistaAdmifarm()) {

				    padron = ReportePadronResult.getMappingAdmifarm(rs);

				}else{
					padron = ReportePadronResult.getMapping(rs);
				}
						
				list.add(padron);
			}
		} catch (Exception e) {
			logger.error("Error al buscar reporte padron", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de buscar padron");
		return list;
	}

	private void setearParametrosQueryPadron(BusquedaReportePadronFiltro filtro, 
			CallableStatement stmt) throws SQLException {
		
		if(filtro.getTipoBusqueda()==4) {
			String idSeccSinComa = filtro.getCodigosSeccional().substring(0, filtro.getCodigosSeccional().length()-1);
			stmt.setInt(1, Integer.parseInt(idSeccSinComa));
		}else {
			if(filtro.getIdsTercerizadora()==null){
				stmt.setNull(1, Types.VARCHAR);
			}else{
				stmt.setString(1, filtro.getIdsTercerizadora());			
			}
			
			if(filtro.getCodigosSeccional() == null){
				stmt.setNull(2, Types.VARCHAR);
			}else{
				stmt.setString(2, filtro.getCodigosSeccional());	
			}
			
			stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			
			stmt.setDate(4, new java.sql.Date(filtro.getFechaHasta().getTime()));
			
			if(filtro.getCodigosProvincia() == null){
				stmt.setNull(5, Types.VARCHAR);
			}else{
				stmt.setString(5, filtro.getCodigosProvincia());	
			}
			
			if(filtro.getCodigosLocalidad() == null || filtro.getCodigosLocalidad().equals("0,")){
				stmt.setNull(6, Types.VARCHAR);
			}else{
				stmt.setString(6, filtro.getCodigosLocalidad());	
			}
			
			stmt.setString(7, String.valueOf(filtro.getTituyfliares()));
	
			if(filtro.getParentescoId() == null){
				stmt.setNull(8, Types.INTEGER);
			}else{
				stmt.setInt(8, filtro.getParentescoId());
			}
	
			if(StringUtils.checkEmpty(filtro.getCuit())){
				stmt.setNull(9, Types.VARCHAR);
			}else{
				stmt.setString(9, filtro.getCuit());	
			}
			
			// invierto las fechas , porque el año de nacimIni es una fecha mayor que el año de la fecha nacimFin
			if(filtro.getFechaNacimIni() != null && filtro.getFechaNacimFin() != null ){
				stmt.setDate(10, new java.sql.Date(filtro.getFechaNacimIni().getTime()));
				stmt.setDate(11, new java.sql.Date(filtro.getFechaNacimFin().getTime()));
			}else{
				stmt.setNull(10, Types.DATE);
				stmt.setNull(11, Types.DATE);
			}
	
			if(filtro.getCodigosPlan() == null){
				stmt.setNull(12, Types.VARCHAR);
			}else{
				stmt.setString(12, filtro.getCodigosPlan());	
			}
			
			if(filtro.getCodigosAportes() == null){
				stmt.setNull(13, Types.CHAR);
			}else{
				stmt.setString(13, filtro.getCodigosAportes());	
			}
			
			if (StringUtils.checkEmpty(filtro.getCategoriaUoma())) {
				stmt.setNull(14, Types.CHAR);
			}else{
				stmt.setString(14, filtro.getCategoriaUoma());
			}
			
			if (StringUtils.checkEmpty(filtro.getProyecto())) {
				stmt.setNull(15, Types.VARCHAR);
			}else{
				stmt.setString(15, filtro.getProyecto());
			}
			
			if(filtro.getTipoBusqueda()==2){
				if (filtro.getIdsMotivoBaja() == null) {
					stmt.setNull(16, Types.VARCHAR);
				}else{
					stmt.setString(16, filtro.getIdsMotivoBaja());
				}
			}
		}	
		
	}
		
	
	public List<ReportePadronTotalResult> getReportePadronTotales(BusquedaReportePadronFiltro filtro) throws SystemException {

		logger.debug("Buscando padron totales");

		Connection con = null;
		PreparedStatement stmt = null;
		List<ReportePadronTotalResult> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			StringBuffer query= new StringBuffer("select ");
			
			if (filtro.isTotalesPorPlan() && filtro.isVistaPrevencion()) {
				query.append(" plan, \r\n" + 
						"           IsNull(SUM(CASE WHEN parentesco='TITULAR' THEN total ELSE 0 END),0) AS TITULAR,\r\n" + 
						"           IsNull(SUM(CASE WHEN parentesco='ADHERENTE' THEN total ELSE 0 END),0) AS ADHERENTE\r\n" + 
						" FROM ( select ");
			}
			
			boolean agregarSeparador=false;
			if(filtro.isTotalesPorTercerizadora()){
				query.append("id_tercerizadora");
				agregarSeparador=true;
			}
			if(filtro.isTotalesPorSeccional()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append("seccional");
				agregarSeparador=true;
			}
			if(filtro.isTotalesPorPlan()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append("plan");
				agregarSeparador=true;
			}
			if(filtro.isTotalesPorEmpresa()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append("cuit , razon_soc , ramo , coalesce(r.descripcion ,'SIN ENCUADRAMIENTO') as descripcion_ramo ");
				agregarSeparador=true;
			}
			query.append(", case when id_parentesco_sss <> 0 then 'ADHERENTE' else 'TITULAR' end as parentesco, count(*) as total ");
			
			if(filtro.isTotalesPorEmpresa()){
				query.append(" from reporte_padron (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  left join ramo_empresa r on  r.id_ramo_empresa =  cast(ramo as smallint) group by case when id_parentesco_sss <>0 then 'ADHERENTE' else 'TITULAR' end");
			}else{
				query.append(" from reporte_padron (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) group by case when id_parentesco_sss <>0 then 'ADHERENTE' else 'TITULAR' end");	
			}
						
			
			if(filtro.isTotalesPorTercerizadora()){
				query.append(", id_tercerizadora");
				agregarSeparador=true;			
			}
			if(filtro.isTotalesPorSeccional()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append(" seccional");				
				agregarSeparador=true;
			}
			if(filtro.isTotalesPorPlan()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append(" plan");
				agregarSeparador=true;
			}
			if(filtro.isTotalesPorEmpresa()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append(" cuit, razon_soc, ramo , descripcion_ramo ");
				agregarSeparador=true;
			}
			
			query.append(" order by ");
			
			agregarSeparador=false;
			
			if(filtro.isTotalesPorTercerizadora()){
				query.append("id_tercerizadora");
				agregarSeparador=true;
			}
			
			if(filtro.isTotalesPorSeccional()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append("seccional");
				agregarSeparador=true;
				
			}
			if(filtro.isTotalesPorPlan()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append("plan");
				agregarSeparador=true;
			}
			
			if(filtro.isTotalesPorEmpresa()){
				if(agregarSeparador){
					query.append(", ");
				}
				query.append("cuit, razon_soc , ramo ");
				agregarSeparador=true;
			}
			
			query.append(", parentesco");
			
			if (filtro.isTotalesPorPlan() && filtro.isVistaPrevencion()) {
				query.append(") s\r\n" + 
						" GROUP BY plan\r\n" + 
						" ");
			}
			
			stmt = con.prepareCall(query.toString());

			setearParametrosQueryPadron(filtro, (CallableStatement) stmt);

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReportePadronTotalResult>();
			while (rs.next()) {
				ReportePadronTotalResult padron = ReportePadronTotalResult.getMapping(rs, filtro);
				list.add(padron);
			}
		} catch (Exception e) {
			logger.error("Error al buscar reporte padron totales", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de buscar padron");
		return list;
	}

	public List<ReportePadronTotalResult> getReportePadronTotalesEntidad(Date fechaVig, 
			String tercerizadora, BusquedaReportePadronFiltro filtro) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ReportePadronTotalResult> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  reporte_entidad_seccional(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	
			stmt = con.prepareCall(sql.toString());

			setearParametrosQueryPadron(filtro, stmt);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReportePadronTotalResult>();
			while (rs.next()) {
				ReportePadronTotalResult repo = ReportePadronTotalResult.getMapping(rs, filtro);
				list.add(repo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar reporte totales entidad-seccional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	
	public List<Baja> getReporteListadoBajas(Date fechaIni, Date fechaFin)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Baja> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  reporte_listado_bajas(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Baja>();
			while (rs.next()) {
				Baja baja = Baja.getMapping(rs);
				list.add(baja);
			}
		} catch (Exception e) {
			logger.error("Error al buscar listado de bajas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<Afiliado> getListadoVigentesTercerizadoras(String id_terc, boolean informar, int tipo, Date vigencia)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<Afiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
//			String sql = "{call  listado_vigentes(?,?,?,?)}";
			String sql = "{call  informes.listado_vigentes(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, id_terc);
			stmt.setBoolean(2, informar);
			stmt.setInt(3, tipo);
			stmt.setDate(4, new java.sql.Date(vigencia.getTime()));			
			
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado afi = Afiliado.getMappingVigentesTercerizadora(rs);
				result.add(afi);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public List<Afiliado> getListadoVigentesTercerizadorasHistorico(String id_terc, int tipo, Date fecha)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<Afiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call informes.listado_vigentes_historico(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, id_terc);
			stmt.setInt(2, tipo);
			stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado afi = Afiliado.getMappingVigentesTercerizadora(rs);
				result.add(afi);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

									   
	public List<PanelControlAfiliado> getReportePanelControlAfiliadoTitBenef(Date fechaDesde, Date fechaHasta)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<PanelControlAfiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  panel_control_afilia_tit_benef(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PanelControlAfiliado>();
			while (rs.next()) {
				PanelControlAfiliado padron = new PanelControlAfiliado(
						rs.getDate("periodo"), rs.getString("descripcion"),
						rs.getInt("titulares"), rs.getInt("beneficiarios"));
				result.add(padron);
			}
			
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public List<PanelControlAfiliado> getReportePanelControlAfiliadoMoliDesreg(Date fechaDesde, Date fechaHasta)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<PanelControlAfiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  panel_control_afilia_desreg(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PanelControlAfiliado>();
			while (rs.next()) {
				PanelControlAfiliado padron = new PanelControlAfiliado(
						rs.getDate("periodo"), rs.getString("descripcion"),
						rs.getInt("titulares"), rs.getInt("beneficiarios"));
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public List<PanelControlAfiliado> getReportePanelControlProvincia(Date fechaDesde, Date fechaHasta)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<PanelControlAfiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  panel_control_afilia_provincia(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PanelControlAfiliado>();
			while (rs.next()) {
				PanelControlAfiliado padron = new PanelControlAfiliado(
						rs.getDate("periodo"), rs.getString("descripcion"),
						rs.getInt("titulares"), rs.getInt("beneficiarios"));
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public List<PanelControlAfiliado> getReportePanelControlPlan(Date fechaDesde, Date fechaHasta)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<PanelControlAfiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  panel_control_afilia_plan(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PanelControlAfiliado>();
			while (rs.next()) {
				PanelControlAfiliado padron = new PanelControlAfiliado(
						rs.getDate("periodo"), rs.getString("descripcion"),
						rs.getInt("titulares"), rs.getInt("beneficiarios"));
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}
	
	public List<PanelControlAfiliado> getReportePanelControlPromedio(Date fechaDesde, Date fechaHasta)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<PanelControlAfiliado> result = null;// new
		// ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  panel_control_prom_os(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PanelControlAfiliado>();
			while (rs.next()) {
				PanelControlAfiliado padron = new PanelControlAfiliado(
						rs.getDate("periodo"),
						rs.getInt("titulares"), rs.getBigDecimal("promedio"));
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}

		return result;

	}
	
	public List<ReporteCredenResult> getReporteCredencialesEmitidas(Date fechaIni, Date fechaFin, boolean informar) throws SystemException {

		logger.debug("Buscando reporte creden");

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteCredenResult> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql=null;
			
			sql = "{call  informes.reporte_credencial_emitidas(?,?,?)}";
		
			stmt = con.prepareCall(sql.toString());
			if(null!=fechaIni){
				stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			if(null!=fechaFin){
				stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			}else{
				stmt.setNull(2, Types.DATE);
			}
			stmt.setBoolean(3, informar);

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteCredenResult>();
			while (rs.next()) {
				ReporteCredenResult padron = ReporteCredenResult.getMapping(rs);
				list.add(padron);
			}
		} catch (Exception e) {
			logger.error("Error al buscar reporte credenciales", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<ReporteCredenResult> getReporteCredencialesEmitidasHistorico(int idReporte) throws SystemException {

		logger.debug("Buscando reporte creden historico");

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteCredenResult> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql=null;
			
			sql = "{call  informes.reporte_credencial_emitidas_historico(?)}";
		
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idReporte);			

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteCredenResult>();
			while (rs.next()) {
				ReporteCredenResult padron = ReporteCredenResult.getMapping(rs);
				list.add(padron);
			}
		} catch (Exception e) {
			logger.error("Error al buscar reporte credenciales", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
//	horrendo reutilizar esto ;S
	public List <PadronInformado> getUltimosLegajosCredenInformado() throws Exception{
		Connection con = null;
		CallableStatement stmt = null;
		List<PadronInformado> result=null;
		try {
			
			con = ConnectionHelper.getConnection();
			String sql = "{call informes.traer_ultimo_reporte_legajo_credencial()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<PadronInformado>();
			while (rs.next()) {
				PadronInformado padron = new PadronInformado(
						rs.getDate("fecha"), rs.getString("id_terc"), rs.getString("tercerizadora"), rs.getString("tipo"), null);
				result.add(padron);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}

		return result;
	}
	
	public List<ReporteLegajosCred> getReporteLegajosCredEmitidasHistorico(int idLote) throws SystemException {

		logger.debug("Buscando reporte legajos");

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteLegajosCred> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql=null;
			
			sql = "{call informes.reporte_legajos_credenciales_emitidas_detalle(?) }";
		
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idLote);			

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteLegajosCred>();
			while (rs.next()) {
				ReporteLegajosCred reporteResults = ReporteLegajosCred.getMapping(rs);
				list.add(reporteResults);
			}
		} catch (Exception e) {
			logger.error("Error al buscar reporte legajos historico", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
}