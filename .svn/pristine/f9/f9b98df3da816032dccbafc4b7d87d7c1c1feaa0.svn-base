package ar.com.ospim.estudioisidro.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimientoResumen;
import ar.com.ospim.estudioisidro.beans.ArchivoSubidoEstudio;
import ar.com.ospim.estudioisidro.beans.Llamado;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio.ResumenActaAcuerdo;
import ar.com.ospim.estudioisidro.beans.ReporteEstadisticoSeguimientoEmpresa;
import ar.com.ospim.estudioisidro.beans.ReporteSeguimientoEmpresa;
import ar.com.ospim.estudioisidro.beans.TipoLoteEmpresa;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LlamadoServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(LlamadoServiceImpl.class);

	private static LlamadoServiceImpl instance = null;

	public static LlamadoServiceImpl getInstance() {
		if (null == instance) {
			instance = new LlamadoServiceImpl();
		}
		return instance;
	}
	
	public int getTotalLlamados(String cuit) {
		Connection con = null;
		CallableStatement stmt = null;
		int total=0;
		try {
			String sql = "{?= call buscar_total_llamados_estudio(?)}";
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, cuit);
			stmt.executeUpdate();
			total = stmt.getInt(1);
		} catch (Exception e) {
			_log.error("Error al traer total llamados estudio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return total;
	}

	public LlamadosEstudio getLlamados(String cuit, int cursor) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Llamado> llamados = null;
		LlamadosEstudio llamadosEstudio=new LlamadosEstudio();
		try {
			String sql = "{call buscar_llamados_estudio(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setString(1, cuit);			
			stmt.setInt(2,cursor);

			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<Llamado>();
			while (rs.next()) {				
				Llamado llama = Llamado.getMapping(rs);				
				llamados.add(llama);
			}
			llamadosEstudio.setLlamados(llamados);
		} catch (Exception e) {
			_log.error("Error al traer llamados estudio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llamadosEstudio;
	}
	
	public List<Llamado> getLlamadosList(String cuit, int cursor, Connection connection) {
		Connection con = null;
		CallableStatement stmt = null;		
		List<Llamado> llamados=null;
		try {
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			String sql = "{call buscar_llamados_estudio(?,?)}";
			
			stmt = con.prepareCall(sql.toString());			
			stmt.setString(1, cuit);			
			stmt.setInt(2,cursor);

			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<Llamado>();
			while (rs.next()) {				
				Llamado llama = Llamado.getMapping(rs);				
				llamados.add(llama);
			}
			
		} catch (Exception e) {
			_log.error("Error al traer llamados estudio", e);
		} finally {
			
			if(connection==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return llamados;
	}
	
	public int grabaLlamado(Llamado llamado, boolean molinera) throws Exception {
		Connection con = null;
		int result=0;
		CallableStatement stmt = null;
		
		try {
			
			// borro documentacion
			String sql = "{call inserta_llamado(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			int cont=1;
			stmt.setString(cont++, llamado.getCuit());
			stmt.setDate(cont++, new java.sql.Date(llamado.getFecha().getTime()));
			stmt.setString(cont++, llamado.getObservaciones());
			stmt.setString(cont++, llamado.getEstado());
			stmt.setString(cont++, llamado.getCartaDocumento());
			stmt.setString(cont++, llamado.getUbicacionCarpeta());			
			stmt.setBoolean(cont++, molinera);
			stmt.setString(cont++, llamado.getTipoContacto());
			if(null!=llamado.getFechaAgenda()){
				stmt.setTimestamp(cont++, new java.sql.Timestamp(llamado.getFechaAgenda().getTime()));
			}else{
				stmt.setNull(cont++,Types.DATE);
			}
			stmt.setString(cont++, llamado.getGoogleEvent());
			stmt.setInt(cont++, llamado.getEstadoGestion().getId());
			if(llamado.getLote() != null){
				stmt.setInt(cont++, llamado.getLote());
				stmt.setString(cont++, llamado.getTipoLote());
			}else{
				stmt.setNull(cont++, Types.INTEGER);
				stmt.setNull(cont++, Types.VARCHAR);
			}
			stmt.setString(cont++, llamado.getUser());			
			
			ResultSet rs=stmt.executeQuery();
			rs.next();
			result=rs.getInt(1);

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	
	public int bajaLlamado(int id, String username) throws Exception {
		Connection con = null;
		int result=0;
		CallableStatement stmt = null;
		
		try {
			// borro documentacion
			String sql = "{call baja_llamado_empresa(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			int cont=1;
			stmt.setInt(cont++, id);
			stmt.setString(cont++, username);
						
			stmt.executeUpdate();			

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public int actualizaLlamado(Llamado llamado) throws Exception {
		Connection con = null;
		int result=0;
		CallableStatement stmt = null;
		
		try {
			// borro documentacion
			String sql = "{call actualiza_llamado(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			int cont=1;
			stmt.setInt(cont++, llamado.getId());
			stmt.setDate(cont++, new java.sql.Date(llamado.getFecha().getTime()));
			stmt.setString(cont++, llamado.getObservaciones());
			stmt.setString(cont++, llamado.getEstado());
			stmt.setString(cont++, llamado.getCartaDocumento());
			stmt.setString(cont++, llamado.getUbicacionCarpeta());
			stmt.setString(cont++, llamado.getTipoContacto());
			if(null!=llamado.getFechaAgenda()){
				stmt.setTimestamp(cont++, new java.sql.Timestamp(llamado.getFechaAgenda().getTime()));
			}else{
				stmt.setNull(cont++,Types.DATE);
			}
			stmt.setString(cont++, llamado.getGoogleEvent());
			
			stmt.setString(cont++, llamado.getUser());	
			
			if(llamado.getLote() != null){
				stmt.setInt(cont++, llamado.getLote());
				stmt.setString(cont++, llamado.getTipoLote());
			}else{
				stmt.setNull(cont++, Types.INTEGER);
				stmt.setNull(cont++, Types.VARCHAR);
			}
			stmt.setInt(cont++, llamado.getEstadoGestion().getId());
			
			result=stmt.executeUpdate();			

		} catch (Exception e) {
			_log.error("Error al insertar llamados estudio", e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public List<ReporteSeguimientoEmpresa> getReporteSeguimientoEmpresa(Date fechaIni, Date fechaFin, Empresa empresa,Integer nroLote,String tipoLote){
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteSeguimientoEmpresa> llamados = null;
		
		try {
			String sql = "{call reporte_seguimiento_empresa(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=empresa.getCuit()&&!empresa.getCuit().trim().equals("")){
				stmt.setString(1, empresa.getCuit());
			}else{
				stmt.setNull(1, Types.VARCHAR);				
			}						
			stmt.setDate(2,new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(3,new java.sql.Date(fechaFin.getTime()));
			
			if(null!=nroLote && nroLote != 0){
				stmt.setInt(4, nroLote);
			}else{
				stmt.setNull(4, Types.INTEGER);				
			}	
			
			if(null!=tipoLote && !tipoLote.trim().equals("")){
				stmt.setString(5, tipoLote);
			}else{
				stmt.setNull(5, Types.VARCHAR);				
			}
			
			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<ReporteSeguimientoEmpresa>();
			while (rs.next()) {				
				ReporteSeguimientoEmpresa llama = ReporteSeguimientoEmpresa.getMapping(rs);				
				llamados.add(llama);
			}			
		} catch (Exception e) {
			_log.error("Error al traer llamados estudio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llamados;		
	}
	
	public void traeTotalesSeguimiento(LlamadosEstudio llest, Connection connectionParameter){
		Connection con = null;
		CallableStatement stmt = null;		  
		try {
			String sql = "{call trae_totales_seguimiento(?, ?)}";
			if(null==connectionParameter){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, llest.getEmpresa().getCuit());
			if(null!=llest.getEmpresa().getSucursal()&&!llest.getEmpresa().getSucursal().equals("000")){
				stmt.setString(2, llest.getEmpresa().getSucursal());
			}else{
				stmt.setString(2, "000");
			}
			ResultSet rs = stmt.executeQuery();
			HashMap<Integer,ResumenActaAcuerdo> map=null;
			while (rs.next()) {							
				map=new HashMap<Integer,ResumenActaAcuerdo>();				
				ResumenActaAcuerdo re=llest.new ResumenActaAcuerdo();
				re.setCantActas(rs.getInt("cant_actas_ospim"));
				re.setImporteActas(rs.getBigDecimal("importe_actas_ospim"));
				re.setSaldoActas(rs.getBigDecimal("importe_actas_ospim"));
				re.setImporteConvenios(rs.getBigDecimal("importe_convenios_ospim"));
				re.setCantConvenios(rs.getInt("cant_convenios_ospim"));
				re.setSaldoConvenios(rs.getBigDecimal("saldo_convenios_ospim"));
				re.setCantRecibos(rs.getInt("cant_recibos_ospim"));
				re.setImporteRecibos(rs.getBigDecimal("importe_recibos_ospim"));
				map.put(new Integer(WebKeysGlobal.OSPIM), re);
				re=llest.new ResumenActaAcuerdo();
				re.setCantActas(rs.getInt("cant_actas_uoma"));
				re.setImporteActas(rs.getBigDecimal("importe_actas_uoma"));
				re.setCantConvenios(rs.getInt("cant_convenios_uoma"));
				re.setSaldoActas(rs.getBigDecimal("importe_actas_uoma"));
				re.setImporteConvenios(rs.getBigDecimal("importe_convenios_uoma"));
				re.setSaldoConvenios(rs.getBigDecimal("saldo_convenios_uoma"));
				re.setCantRecibos(rs.getInt("cant_recibos_uoma"));
				re.setImporteRecibos(rs.getBigDecimal("importe_recibos_uoma"));
				map.put(new Integer(WebKeysGlobal.UOMA), re);
				re=llest.new ResumenActaAcuerdo();
				re.setCantActas(rs.getInt("cant_actas_amtima"));
				re.setImporteActas(rs.getBigDecimal("importe_actas_amtima"));
				re.setSaldoActas(rs.getBigDecimal("saldo_actas_amtima"));
				re.setCantConvenios(rs.getInt("cant_convenios_amtima"));
				re.setImporteConvenios(rs.getBigDecimal("importe_convenios_amtima"));
				re.setSaldoConvenios(rs.getBigDecimal("saldo_convenios_amtima"));	
				re.setCantRecibos(rs.getInt("cant_recibos_amtima"));
				re.setImporteRecibos(rs.getBigDecimal("importe_recibos_amtima"));
				map.put(new Integer(WebKeysGlobal.AMTIMA), re);
				   
				llest.setCantChequesRechazados(rs.getInt("cant_cheques_rechazados"));
				llest.setImporteChequesRechazados(rs.getBigDecimal("importe_cheques_rechazados"));
				llest.setCantReemplazadosRechazo(rs.getInt("cant_cheques_reemp_rechazo"));
				llest.setImporteReemplazadosRechazo(rs.getBigDecimal("importe_cheques_reemp_rechazo"));
				llest.setCantCanjeadosSinDepo(rs.getInt("cant_cheques_canjeados"));
				llest.setImporteCanjeadosSinDepo(rs.getBigDecimal("importe_cheques_canjeados"));
				llest.setCantChequesCartera(rs.getInt("cant_cheques_cartera"));
				llest.setImporteChequesCartera(rs.getBigDecimal("importe_cheques_cartera"));
				llest.setCantDeudas(rs.getInt("cant_deudas"));
			}
			llest.setResumenActaAcuerdo(map);
		} catch (Exception e) {
			_log.error("Error al buscar reporte entidad", e);
		} finally {
			if(null==connectionParameter){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		
	}

	
	public Llamado getProponeNroLote(String cuit, Connection connection) {
		Connection con = null;
		CallableStatement stmt = null;		
		Llamado llamado=null;
		try {
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			String sql = "{call propone_nro_lote_llamados_estudio(?)}";
			
			stmt = con.prepareCall(sql.toString());			
			stmt.setString(1, cuit);			
			
			ResultSet rs = stmt.executeQuery();
			llamado = new Llamado();
			llamado.setLote(null);
			llamado.setTipoLote("");
			while (rs.next()) {
				try{
					String numLote = rs.getString("lote");	
					llamado.setLote(Integer.parseInt(numLote));
				}catch(NumberFormatException e){
					llamado.setLote(null);
				}
//				llamado.setLote(rs.getInt("lote"));
				llamado.setTipoLote(rs.getString("tipo_lote"));
			}
			
		} catch (Exception e) {
			_log.error("Error al traer nro lote llamados estudio", e);
		} finally {
			
			if(connection==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return llamado;
	}
	
	public int grabaLlamadoLote(Llamado llamado, boolean molinera, Connection connection) throws Exception {
		Connection con = null;
		int result=0;
		CallableStatement stmt = null;
		try {
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			_log.debug("Cuit llamado procesado "+llamado.getCuit());			
			String sql = "{call inserta_llamado_lote(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
//			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			int cont=1;
			stmt.setString(cont++, llamado.getCuit());
			stmt.setDate(cont++, new java.sql.Date(llamado.getFecha().getTime()));
			stmt.setString(cont++, llamado.getObservaciones());
			stmt.setString(cont++, llamado.getEstado());
			stmt.setString(cont++, llamado.getCartaDocumento());
			stmt.setString(cont++, llamado.getUbicacionCarpeta());			
			stmt.setBoolean(cont++, molinera);
			stmt.setString(cont++, llamado.getTipoContacto());
			if(null!=llamado.getFechaAgenda()){
				stmt.setTimestamp(cont++, new java.sql.Timestamp(llamado.getFechaAgenda().getTime()));
			}else{
				stmt.setNull(cont++,Types.DATE);
			}
			stmt.setString(cont++, llamado.getGoogleEvent());
			stmt.setInt(cont++, llamado.getEstadoGestion().getId());
			stmt.setInt(cont++, llamado.getLote());
			stmt.setString(cont++, llamado.getTipoLote());
			stmt.setString(cont++, llamado.getUser());
			stmt.setDouble(cont++, llamado.getDeuda());
			
			ResultSet rs=stmt.executeQuery();
			rs.next();
			result=rs.getInt(1);
			
		} catch (Exception e) {
			_log.error("Error al verificar grabar llamados estudio " +llamado.getCuit(),e); 
	    }finally {
			
			if(null==connection){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return result;
	}

	
	public static boolean existeLote(String cuit, Integer lote, String tipoLote) throws Exception{
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret=false;
		try {
			String sql = "{?=call existe_lote_llamados_estudio(?,?,?)}";
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.BOOLEAN );
			stmt.setString(2,cuit);
			stmt.setInt(3, lote);
			stmt.setString(4,tipoLote);
			
			stmt.executeQuery();
			
			ret = stmt.getBoolean(1);
			
		} catch (Exception e) {
			_log.error("Error al verificar nro lote llamados estudio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public int grabaArchivoSubido(ArchivoSubidoEstudio a, Connection connection) throws Exception {
		Connection con = null;
		int result=0;
		CallableStatement stmt = null;
		
		try {
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			String sql = "{call inserta_archivo_subido_estudio(?,?,?,?,?,?)}";
//			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			int cont=1;
			stmt.setString(cont++, a.getTipo());
			stmt.setInt(cont++, a.getNroLote());
			stmt.setString(cont++,a.getTipoLote());
			stmt.setInt(cont++, a.getCantReg());
			stmt.setString(cont++,a.getUsuario());
			stmt.setDate(cont++, new java.sql.Date(a.getFechaProceso().getTime()));
			
			
			ResultSet rs=stmt.executeQuery();
			rs.next();
			result=rs.getInt(1);

		} finally {
			if(null==connection){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return result;
	}
	
	public List<ArchivoSubidoEstudio> getArchivosSubidosEstudio()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoSubidoEstudio> list = null;

		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_ultimos_archivos_estudio()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoSubidoEstudio>();
			while (rs.next()) {
				ArchivoSubidoEstudio archivo = ArchivoSubidoEstudio.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar archivo Estudios", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}
	
	
	public List<ReporteEstadisticoSeguimientoEmpresa> getReporteEstadisticoSeguimientoEmpresaLotes(Date fechaIni, Date fechaFin, Empresa empresa,Integer nroLote,String tipoLote){
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteEstadisticoSeguimientoEmpresa> llamados = null;
		
		try {
			String sql = "{call reporte_estadistico_seguimiento_empresa_lotes(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=empresa.getCuit()&&!empresa.getCuit().trim().equals("")){
				stmt.setString(1, empresa.getCuit());
			}else{
				stmt.setNull(1, Types.VARCHAR);				
			}						
			stmt.setDate(2,new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(3,new java.sql.Date(fechaFin.getTime()));
			
			if(null!=nroLote /*&& nroLote != 0*/){
				stmt.setInt(4, nroLote);
			}else{
				stmt.setNull(4, Types.INTEGER);				
			}	
			
			if(null!=tipoLote && !tipoLote.trim().equals("")){
				stmt.setString(5, tipoLote);
			}else{
				stmt.setNull(5, Types.VARCHAR);				
			}
			
			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<ReporteEstadisticoSeguimientoEmpresa>();
			while (rs.next()) {				
				ReporteEstadisticoSeguimientoEmpresa llama = ReporteEstadisticoSeguimientoEmpresa.getMapping(rs);				
				llamados.add(llama);
			}			
		} catch (Exception e) {
			_log.error("Error al traer estadistico seguimiento empresas lotes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llamados;		
	}
	
	public List<ReporteEstadisticoSeguimientoEmpresa> getReporteEstadisticoSeguimientoEmpresa(Empresa empresa,Integer nroLote,String tipoLote){
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteEstadisticoSeguimientoEmpresa> llamados = null;
		
		try {
			String sql = "{call reporte_estadistico_seguimiento_empresa(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=empresa.getCuit()&&!empresa.getCuit().trim().equals("")){
				stmt.setString(1, empresa.getCuit());
			}else{
				stmt.setNull(1, Types.VARCHAR);				
			}						
			
			if(null!=nroLote && nroLote != 0){
				stmt.setInt(2, nroLote);
			}else{
				stmt.setNull(2, Types.INTEGER);				
			}	
			
			if(null!=tipoLote && !tipoLote.trim().equals("")){
				stmt.setString(3, tipoLote);
			}else{
				stmt.setNull(3, Types.VARCHAR);				
			}
			
			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<ReporteEstadisticoSeguimientoEmpresa>();
			while (rs.next()) {				
				ReporteEstadisticoSeguimientoEmpresa llama = ReporteEstadisticoSeguimientoEmpresa.getMapping(rs);				
				llamados.add(llama);
			}			
		} catch (Exception e) {
			_log.error("Error al traer estadistico seguimiento empresas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llamados;		
	}
	
	public ReporteEstadisticoSeguimientoEmpresa getReporteEstadisticoSeguimientoEmpresaAsignado(Empresa empresa,Integer nroLote,String tipoLote,Integer estado){
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteEstadisticoSeguimientoEmpresa> llamados = null;
		ReporteEstadisticoSeguimientoEmpresa llama= new ReporteEstadisticoSeguimientoEmpresa();
		try {
			String sql = "{call reporte_estadistico_seguimiento_empresa_asignado(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=empresa.getCuit()&&!empresa.getCuit().trim().equals("")){
				stmt.setString(1, empresa.getCuit());
			}else{
				stmt.setNull(1, Types.VARCHAR);				
			}						
			
			if(null!=nroLote && nroLote != 0){
				stmt.setInt(2, nroLote);
			}else{
				stmt.setNull(2, Types.INTEGER);				
			}	
			
			if(null!=tipoLote && !tipoLote.trim().equals("")){
				stmt.setString(3, tipoLote);
			}else{
				stmt.setNull(3, Types.VARCHAR);				
			}
			
			if(null!=estado && estado != 0){
				stmt.setInt(4, estado);
			}else{
				stmt.setNull(4, Types.INTEGER);				
			}	
			
			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<ReporteEstadisticoSeguimientoEmpresa>();
			
			while (rs.next()) {				
				llama = ReporteEstadisticoSeguimientoEmpresa.getMapping(rs);				
				
			}			
		} catch (Exception e) {
			_log.error("Error al traer estadistico seguimiento empresas asginado", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llama;		
	}

	public ReporteEstadisticoSeguimientoEmpresa getReporteEstadisticoSeguimientoEmpresaRecuperado(Empresa empresa,Integer nroLote,String tipoLote,Integer estado){
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteEstadisticoSeguimientoEmpresa> llamados = null;
		ReporteEstadisticoSeguimientoEmpresa llama= new ReporteEstadisticoSeguimientoEmpresa();
		try {
			String sql = "{call reporte_estadistico_seguimiento_empresa_recuperado(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=empresa.getCuit()&&!empresa.getCuit().trim().equals("")){
				stmt.setString(1, empresa.getCuit());
			}else{
				stmt.setNull(1, Types.VARCHAR);				
			}						
			
			if(null!=nroLote && nroLote != 0){
				stmt.setInt(2, nroLote);
			}else{
				stmt.setNull(2, Types.INTEGER);				
			}	
			
			if(null!=tipoLote && !tipoLote.trim().equals("")){
				stmt.setString(3, tipoLote);
			}else{
				stmt.setNull(3, Types.VARCHAR);				
			}
			
			if(null!=estado && estado != 0){
				stmt.setInt(4, estado);
			}else{
				stmt.setNull(4, Types.INTEGER);				
			}	
			
			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<ReporteEstadisticoSeguimientoEmpresa>();
			
			while (rs.next()) {				
				llama = ReporteEstadisticoSeguimientoEmpresa.getMapping(rs);				
				
			}			
		} catch (Exception e) {
			_log.error("Error al traer estadistico seguimiento empresas recuperado", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llama;		
	}
	
	public ReporteEstadisticoSeguimientoEmpresa getReporteEstadisticoSeguimientoEmpresaCobrado(Empresa empresa,Integer nroLote,String tipoLote,Integer estado){
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteEstadisticoSeguimientoEmpresa> llamados = null;
		ReporteEstadisticoSeguimientoEmpresa llama= new ReporteEstadisticoSeguimientoEmpresa();
		try {
			String sql = "{call reporte_estadistico_seguimiento_empresa_cobrado(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=empresa.getCuit()&&!empresa.getCuit().trim().equals("")){
				stmt.setString(1, empresa.getCuit());
			}else{
				stmt.setNull(1, Types.VARCHAR);				
			}						
			
			if(null!=nroLote && nroLote != 0){
				stmt.setInt(2, nroLote);
			}else{
				stmt.setNull(2, Types.INTEGER);				
			}	
			
			if(null!=tipoLote && !tipoLote.trim().equals("")){
				stmt.setString(3, tipoLote);
			}else{
				stmt.setNull(3, Types.VARCHAR);				
			}
			
			if(null!=estado && estado != 0){
				stmt.setInt(4, estado);
			}else{
				stmt.setNull(4, Types.INTEGER);				
			}	
			
			ResultSet rs = stmt.executeQuery();
			llamados = new ArrayList<ReporteEstadisticoSeguimientoEmpresa>();
			
			while (rs.next()) {				
				llama = ReporteEstadisticoSeguimientoEmpresa.getMapping(rs);				
				
			}			
		} catch (Exception e) {
			_log.error("Error al traer estadistico seguimiento empresas recuperado", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return llama;		
	}

	public static boolean cierreAutomaticoLotes() throws Exception{
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret=true;
		try {
			String sql = "{call seguimiento_empresas_cierre_automatico_lotes()}";
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.executeQuery();
//			stmt.executeUpdate();
			
		} catch (Exception e) {
			_log.error("Error cierre automaticos lotes empresa", e);
			ret=false;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public ActaAcuerdoSeguimientoResumen getDesglosePagosActasConvenios(String cuit) {
		Connection con = null;
		CallableStatement stmt = null;
		
		ActaAcuerdoSeguimientoResumen resumen=new ActaAcuerdoSeguimientoResumen();
		try {
			String sql = "{call buscarActasConveniosPagoAtrasado(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setString(1, cuit);			
			
			ResultSet rs = stmt.executeQuery();
			resumen = new ActaAcuerdoSeguimientoResumen();
			while (rs.next()) {				
				resumen = ActaAcuerdoSeguimientoResumen.getMapping(rs);				
				
			}

		} catch (Exception e) {
			_log.error("Error al traer llamados estudio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return resumen;
	}
	
	
	public List<TipoLoteEmpresa> avisoVencimientoLotesSeguimientoEmpresas(Integer diasAlVencimiento, Integer diasAntesAviso , Connection connection) {
		Connection con = null;
		CallableStatement stmt = null;		
		List<TipoLoteEmpresa> lotes=null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			String sql = "{call avisoVencimientoLotesSeguimientoEmpresas(?,?)}";
			
			stmt = con.prepareCall(sql.toString());			
			stmt.setInt(1, diasAlVencimiento);			
			stmt.setInt(2,diasAntesAviso);

			ResultSet rs = stmt.executeQuery();
			lotes = new ArrayList<TipoLoteEmpresa>();
			while (rs.next()) {				
				TipoLoteEmpresa lote = new TipoLoteEmpresa();
				lote.setTipoLote(rs.getString("tipo_Lote"));
				lote.setLote(rs.getInt("lote"));
				lote.setDescripcionLote(sdf.format(rs.getDate("fecha_vencimiento")));
				lotes.add(lote);
			}
			
		} catch (Exception e) {
			_log.error("Error al vencimiento lotes estudio", e);
		} finally {
			
			if(connection==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return lotes;
	}

	
}
