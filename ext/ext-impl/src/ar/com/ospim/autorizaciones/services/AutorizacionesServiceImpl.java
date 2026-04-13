package ar.com.ospim.autorizaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.beans.BusquedaReporteReclamoFiltro;
import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinarioExcel;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalExcel;
import ar.com.ospim.autorizaciones.beans.SituacionMedicaExcel;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

/**
 * <a href="AutorizacionesServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Gustavo Fernandez
 * 
 */
public class AutorizacionesServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(AutorizacionesServiceImpl.class);

	public int getGenerarAutorizacionPmi(String tipoReceta, Date fechaReceta,
			String cuil, Integer inte, String observaciones, String altaUsuario)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int generarAutorizacionPmi = 0;
		try {
			String sql = "{call autorizaciones.alta_autorizaciones_pmi(?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, tipoReceta);
			if (null != fechaReceta) {
				stmt.setDate(2, new java.sql.Date(fechaReceta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			stmt.setString(3, cuil);
			stmt.setInt(4, inte);
			stmt.setString(5, observaciones);
			stmt.setString(6, altaUsuario);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				generarAutorizacionPmi = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al insertar autorizacion", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return generarAutorizacionPmi;
	}

	public int getBajaAutorizacionPmi(int idAutorizacion, String bajaUsuario)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int bajaAutorizacionPmi = 0;
		try {
			String sql = "{call autorizaciones.baja_autorizaciones_pmi(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idAutorizacion);
			stmt.setString(2, bajaUsuario);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				bajaAutorizacionPmi = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return bajaAutorizacionPmi;
	}

	public int getEditarAutorizacionPmi(int numReceta, String tipoReceta,
			Date fechaReceta, String cuil, int inte, String modiUsuario, String obs) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int editarAutorizacionPmi = 0;
		try {
			String sql = "{call autorizaciones.editar_autorizaciones_pmi(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numReceta);
			stmt.setString(2, tipoReceta);
			if (null != fechaReceta) {
				stmt.setDate(3, new java.sql.Date(fechaReceta.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			stmt.setString(4, cuil);
			stmt.setInt(5, inte);
			stmt.setString(6, modiUsuario);
			stmt.setString(7, obs);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				editarAutorizacionPmi = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return editarAutorizacionPmi;
	}

	
	public List<ReclamoPrestacionalExcel> getListaReclamosPrestacionales( BusquedaReporteReclamoFiltro filtro )
	
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReclamoPrestacionalExcel> list = null;
		try {
			String sql ="";
			switch (filtro.getTipoPrestacion()) {
				case (1):
					sql = "{call autorizaciones.reclamos_prestacionales_excel_clinica  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
					break;
				case (2):
					sql = "{call autorizaciones.reclamos_prestacionales_excel_farmacia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
					break;
				default:
					sql = "{call autorizaciones.reclamos_prestacionales_excel  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?,?,?,?,?)}";
					}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (filtro.getNroReclamo() != 0 ) {
				stmt.setInt(1, filtro.getNroReclamo());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (filtro.getFechaOspim() != null ) {
				stmt.setDate(2, new java.sql.Date(filtro.getFechaOspim().getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			if (filtro.getFechaOspim1() != null ) {
				stmt.setDate(3, new java.sql.Date(filtro.getFechaOspim1().getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			if (filtro.getCuilTitular()!="") {
				stmt.setString (4, filtro.getCuilTitular());
			} else {
				stmt.setNull(4, Types.CHAR );
			}			
			if (filtro.getCuilTitular()!="") {
				stmt.setInt(5, filtro.getInte());
			} else {
				stmt.setNull(5, Types.INTEGER );
			}
			if (filtro.getEstado() >=0) {
				stmt.setInt(6, filtro.getEstado());
			} else {
				stmt.setNull(6, Types.INTEGER );
			}	
		
			if (filtro.getFechaCierre()!=null ) {
				stmt.setDate(7, new java.sql.Date(filtro.getFechaCierre().getTime()));
			} else {
				stmt.setNull(7, Types.DATE);
			}			
			
			if (filtro.getFechaCierre1() != null ) {
				stmt.setDate(8, new java.sql.Date(filtro.getFechaCierre1().getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
			}			
			
			if (filtro.getResolucion() !="" ) {
				stmt.setString (9, filtro.getResolucion() );
			} else {
				stmt.setNull(9, Types.CHAR );
			}
			
			if (Integer.parseInt(filtro.getCodigoTipoGestion()) >0 ) {
				stmt.setInt(10, Integer.parseInt(filtro.getCodigoTipoGestion()) );
			} else {
				stmt.setNull(10, Types.INTEGER);
			}
			
			if (filtro.getSectorSeleccionado() !="" ) {
				stmt.setString (11, filtro.getSectorSeleccionado());
			} else {
				stmt.setNull(11, Types.CHAR );
			}
			
			if (filtro.getTipoPedido()!="" ) {
				stmt.setString (12, filtro.getTipoPedido());
			} else {
				stmt.setNull(12, Types.CHAR );
			}
			
			
			if ( filtro.getTipoPrestacion() >0){
				if (filtro.getTipoPrestacion() ==1){ // pretaciones clinicas 
					if (filtro.getCodePrestacion()  !="" ) {
						stmt.setString(13, filtro.getCodePrestacion()  );
					} else {
						stmt.setNull(13, Types.VARCHAR );
					}	
				}
				if (filtro.getTipoPrestacion()  ==2){ // prestaciones de farmacia 
					if (filtro.getCodePrestacion()   !="" ) {
						stmt.setInt(13, Integer.parseInt(filtro.getCodePrestacion()) );
					} else {
						stmt.setNull(13, Types.INTEGER);
					}	
				}	
				
				if (filtro.getNroLote() !=null && filtro.getNroLote()>0){ 
						stmt.setInt(14, filtro.getNroLote() );
				} else {
						stmt.setNull(14, Types.INTEGER);
				}
				
				if (filtro.getSeccional() == 0) {
					stmt.setNull(15, Types.INTEGER );
				}else{
					stmt.setInt(15, filtro.getSeccional()  );   
				}		
				
				if (filtro.getCodintegracion() !=0 ){ 
					stmt.setInt(16, filtro.getCodintegracion() );
			    } else {
					stmt.setNull(16, Types.INTEGER);
			    }
				if (filtro.getRecuperableSur() !=0){ 
					stmt.setInt(17, filtro.getRecuperableSur() );
			    } else {
					stmt.setNull(17, Types.INTEGER);
			    }
				
				
			}else {
				if (filtro.getNroLote() !=null && filtro.getNroLote()>0){ 
					stmt.setInt(13, filtro.getNroLote() );
			    } else {
					stmt.setNull(13, Types.INTEGER);
			    }
				//inicio reclamos_prestacionales_prestaciones
				if ("Seleccione".equalsIgnoreCase(filtro.getFrecuencia())) {
					stmt.setNull(14, Types.VARCHAR );
				}else{
					stmt.setString(14, filtro.getFrecuencia() );   
				}
				if ("Seleccione".equals(filtro.getComprobanteTipo())) {
					stmt.setNull(15, Types.VARCHAR );
				}else{
					stmt.setString(15, filtro.getComprobanteTipo() );   
				}
				if (StringUtils.checkEmpty(filtro.getSucursalComprobante())) {
					stmt.setNull(16, Types.VARCHAR );
				}else{
					stmt.setString(16, filtro.getSucursalComprobante() );   
				}
				if (StringUtils.checkEmpty(filtro.getNumeroComprobante())) {
					stmt.setNull(17, Types.VARCHAR );
				}else{
					stmt.setString(17, filtro.getNumeroComprobante() );   
				}
				if (filtro.getFechaComprobante() == null) {
					stmt.setNull(18, Types.DATE );
				}else{
					stmt.setDate(18, new java.sql.Date(filtro.getFechaComprobante().getTime()));   
				}
				if (StringUtils.checkEmpty(filtro.getCuitEntidadComprobante())) {
					stmt.setNull(19, Types.VARCHAR );
				}else{
					stmt.setString(19, filtro.getCuitEntidadComprobante() );   
				}		
				if (filtro.getSeccional() == 0) {
					stmt.setNull(20, Types.INTEGER );
				}else{
					stmt.setInt(20, filtro.getSeccional() );   
				}		
				if (filtro.getCodintegracion() !=0 ){ 
					stmt.setInt(21, filtro.getCodintegracion() );
			    } else {
					stmt.setNull(21, Types.INTEGER);
			    }
				if (filtro.getRecuperableSur() !=0){ 
					stmt.setInt(22, filtro.getRecuperableSur() );
			    } else {
					stmt.setNull(22, Types.INTEGER);
			    }
				
				//fin reclamos_prestacionales_prestaciones
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReclamoPrestacionalExcel>();
			while (rs.next()) {
				ReclamoPrestacionalExcel archivo = ReclamoPrestacionalExcel.getMapping(rs);
				try {
					  if("1".equals(rs.getString("rpt_afiliado_discapacitado"))) {
						  archivo.setDiscapacitado("SI");
					  }else {
						  archivo.setDiscapacitado("NO");
					  }
				}catch(Exception e) {
					
				}
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error en la busqueda de registros de exportacion a excel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
		
	public List<EquipoInterdisciplinarioExcel> getListaEquiposInterdisciplinarios( 
			Date fechaOspim,int inte,String cuilTitular,int nroRegistro ,String estado, String motivo )
	
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<EquipoInterdisciplinarioExcel> list = null;
		try {
			String sql ="";			
			sql = "{call autorizaciones.reporte_equipo_interdisciplinario(?,?,?,?,?,?)}";			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (nroRegistro != 0 ) {
				stmt.setInt(1, nroRegistro  );
			} else {
				stmt.setNull(1, Types.INTEGER);
			}			
			if (fechaOspim != null ) {
				stmt.setDate(2, new java.sql.Date(fechaOspim.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}			
			if (cuilTitular!="") {
				stmt.setString (3, cuilTitular);
			} else {
				stmt.setNull(3, Types.CHAR );
			}			
			if (cuilTitular!="") {
				stmt.setInt(4, inte);
			} else {
				stmt.setNull(4, Types.INTEGER );
			}			
			if (estado   !="" ) {
				stmt.setString(5, estado );
			} else {
				stmt.setNull(5, Types.VARCHAR );
			}
			if (motivo.equals("TODOS") ) {				
				stmt.setNull(6, Types.VARCHAR );
			} else {
				stmt.setString(6, motivo );
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<EquipoInterdisciplinarioExcel>();
			while (rs.next()) {
				EquipoInterdisciplinarioExcel  archivo = EquipoInterdisciplinarioExcel.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error en la busqueda de registros de exportacion a excel Equipos Interdisciplinarios", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<SituacionMedicaExcel> getListaSituacionMedica ( 
			Date fechaDesde ,Date fechaHasta , int inte,String cuilTitular, int tipoSitu )
	
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SituacionMedicaExcel> list = null;
		try {
			String sql ="";			
			sql = "{call reporte_situaciones_medicas  (?,?,?,?,?)}";			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (tipoSitu != 0 ) {
				stmt.setInt(1, tipoSitu  );
			} else {
				stmt.setNull(1, Types.INTEGER);
			}			
			if (fechaDesde != null ) {
				stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}			
			if (fechaHasta != null ) {
				stmt.setDate(3, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			if (cuilTitular!="") {
				stmt.setString (4, cuilTitular);
			} else {
				stmt.setNull(4, Types.CHAR );
			}			
			if (cuilTitular!="") {
				stmt.setInt(5, inte);
			} else {
				stmt.setNull(5, Types.INTEGER );
			}			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SituacionMedicaExcel>();
			while (rs.next()) {
				SituacionMedicaExcel archivo = SituacionMedicaExcel.getMapping(rs)  ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error en la busqueda de registros de exportacion a excel de Situacion Medica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public List<AutorizacionesPmi> getListaAutorizacionesPmi(Date fechaReceta,
			String cuil, int inte, int numReceta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<AutorizacionesPmi> list = null;
		try {
			String sql = "{call autorizaciones.busca_autorizaciones_pmi(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (null != fechaReceta) {
				stmt.setDate(1, new java.sql.Date(fechaReceta.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(2, cuil);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (inte>0) {
				stmt.setInt(3, inte);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (numReceta>0) {
				stmt.setInt(4, numReceta);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<AutorizacionesPmi>();
			while (rs.next()) {
				AutorizacionesPmi archivo = AutorizacionesPmi.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Recetas en autorizaciones pmi", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public List<AutorizacionesPmi> getListaAutorizacionesPmiXauto(Date fechaReceta,
			String cuil, int inte, int idAutorizacion)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<AutorizacionesPmi> list = null;
		try {
			String sql = "{call autorizaciones.trae_autorizacion_pmi(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (null != fechaReceta) {
				stmt.setDate(1, new java.sql.Date(fechaReceta.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(2, cuil);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (inte>0) {
				stmt.setInt(3, inte);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (idAutorizacion>0) {
				stmt.setInt(4, idAutorizacion);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<AutorizacionesPmi>();
			while (rs.next()) {
				AutorizacionesPmi archivo = AutorizacionesPmi.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar autorizaciones pmi ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public int getCantAutorizacionesAfiliado(String cuil,
			int inte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int cantAuto=0;
		try {
			String sql = "{call autorizaciones.obtener_cant_aut_pmi(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(1, cuil);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (inte>0) {
				stmt.setInt(2, inte);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cantAuto=rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error(
					"Error al intentar traer la cantidad de autorizaciones del afiliado",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cantAuto;
	}
	
	public boolean getValidaPlanMolinero(String cuil,Integer inte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean validaPlan = false;
		try {
			String sql = "{call autorizaciones.valida_plan_molinero(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(1, cuil);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != inte) {
				stmt.setInt(2, inte);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				validaPlan = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al validar plan molinero", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return validaPlan;
	}
	
	// Consultar Periodo no Consecutivo
	public Date getValidaPeriodoNoConsecutivo(String cuil,Integer inte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Date ultimoPeriodo = null;
		try {
			String sql = "{call autorizaciones.valida_periodo_no_consecutivo(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(1, cuil);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != inte) {
				stmt.setInt(2, inte);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ultimoPeriodo=rs.getDate(1);
			}
		} catch (Exception e) {
			_log.error("Error al validar periodo de autorizacion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ultimoPeriodo;
	}
	
	
public List<ReclamoPrestacionalExcel> getListaReclamosPrestacionalesAgrupado( BusquedaReporteReclamoFiltro filtro )
	
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReclamoPrestacionalExcel> list = null;
		try {
			String sql ="";
			switch (filtro.getTipoPrestacion()) {
				case (1):
//					sql = "{call autorizaciones.reclamos_prestacionales_excel_clinica  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
					break;
				case (2):
//					sql = "{call autorizaciones.reclamos_prestacionales_excel_farmacia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
					break;
				default:
					sql = "{call autorizaciones.reclamos_prestacionales_excel_agrupado  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?,?,?)}";
					}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (filtro.getNroReclamo() != 0 ) {
				stmt.setInt(1, filtro.getNroReclamo());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (filtro.getFechaOspim() != null ) {
				stmt.setDate(2, new java.sql.Date(filtro.getFechaOspim().getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			if (filtro.getFechaOspim1() != null ) {
				stmt.setDate(3, new java.sql.Date(filtro.getFechaOspim1().getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			if (filtro.getCuilTitular()!="") {
				stmt.setString (4, filtro.getCuilTitular());
			} else {
				stmt.setNull(4, Types.CHAR );
			}			
			if (filtro.getCuilTitular()!="") {
				stmt.setInt(5, filtro.getInte());
			} else {
				stmt.setNull(5, Types.INTEGER );
			}
			if (filtro.getEstado() >=0) {
				stmt.setInt(6, filtro.getEstado());
			} else {
				stmt.setNull(6, Types.INTEGER );
			}	
		
			if (filtro.getFechaCierre()!=null ) {
				stmt.setDate(7, new java.sql.Date(filtro.getFechaCierre().getTime()));
			} else {
				stmt.setNull(7, Types.DATE);
			}			
			
			if (filtro.getFechaCierre1() != null ) {
				stmt.setDate(8, new java.sql.Date(filtro.getFechaCierre1().getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
			}			
			
			if (filtro.getResolucion() !="" ) {
				stmt.setString (9, filtro.getResolucion() );
			} else {
				stmt.setNull(9, Types.CHAR );
			}
			
			if (Integer.parseInt(filtro.getCodigoTipoGestion()) >0 ) {
				stmt.setInt(10, Integer.parseInt(filtro.getCodigoTipoGestion()) );
			} else {
				stmt.setNull(10, Types.INTEGER);
			}
			
			if (filtro.getSectorSeleccionado() !="" ) {
				stmt.setString (11, filtro.getSectorSeleccionado());
			} else {
				stmt.setNull(11, Types.CHAR );
			}
			
			if (filtro.getTipoPedido()!="" ) {
				stmt.setString (12, filtro.getTipoPedido());
			} else {
				stmt.setNull(12, Types.CHAR );
			}
			
			
			if ( filtro.getTipoPrestacion() >0){
				if (filtro.getTipoPrestacion() ==1){ // pretaciones clinicas 
					if (filtro.getCodePrestacion()  !="" ) {
						stmt.setString(13, filtro.getCodePrestacion()  );
					} else {
						stmt.setNull(13, Types.VARCHAR );
					}	
				}
				if (filtro.getTipoPrestacion()  ==2){ // prestaciones de farmacia 
					if (filtro.getCodePrestacion()   !="" ) {
						stmt.setInt(13, Integer.parseInt(filtro.getCodePrestacion()) );
					} else {
						stmt.setNull(13, Types.INTEGER);
					}	
				}	
				
				if (filtro.getNroLote() !=null && filtro.getNroLote()>0){ 
						stmt.setInt(14, filtro.getNroLote() );
				} else {
						stmt.setNull(14, Types.INTEGER);
				}
				
				if (filtro.getSeccional() == 0) {
					stmt.setNull(15, Types.INTEGER );
				}else{
					stmt.setInt(15, filtro.getSeccional()  );   
				}		
				
				
			}else {
				if (filtro.getNroLote() !=null && filtro.getNroLote()>0){ 
					stmt.setInt(13, filtro.getNroLote() );
			    } else {
					stmt.setNull(13, Types.INTEGER);
			    }
				//inicio reclamos_prestacionales_prestaciones
				if ("Seleccione".equalsIgnoreCase(filtro.getFrecuencia())) {
					stmt.setNull(14, Types.VARCHAR );
				}else{
					stmt.setString(14, filtro.getFrecuencia() );   
				}
				if ("Seleccione".equals(filtro.getComprobanteTipo())) {
					stmt.setNull(15, Types.VARCHAR );
				}else{
					stmt.setString(15, filtro.getComprobanteTipo() );   
				}
				if (StringUtils.checkEmpty(filtro.getSucursalComprobante())) {
					stmt.setNull(16, Types.VARCHAR );
				}else{
					stmt.setString(16, filtro.getSucursalComprobante() );   
				}
				if (StringUtils.checkEmpty(filtro.getNumeroComprobante())) {
					stmt.setNull(17, Types.VARCHAR );
				}else{
					stmt.setString(17, filtro.getNumeroComprobante() );   
				}
				if (filtro.getFechaComprobante() == null) {
					stmt.setNull(18, Types.DATE );
				}else{
					stmt.setDate(18, new java.sql.Date(filtro.getFechaComprobante().getTime()));   
				}
				if (StringUtils.checkEmpty(filtro.getCuitEntidadComprobante())) {
					stmt.setNull(19, Types.VARCHAR );
				}else{
					stmt.setString(19, filtro.getCuitEntidadComprobante() );   
				}		
				if (filtro.getSeccional() == 0) {
					stmt.setNull(20, Types.INTEGER );
				}else{
					stmt.setInt(20, filtro.getSeccional() );   
				}		
				//fin reclamos_prestacionales_prestaciones

				
			}
			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReclamoPrestacionalExcel>();
			while (rs.next()) {
				ReclamoPrestacionalExcel archivo = new ReclamoPrestacionalExcel();
				archivo.setFecha_cierre(new SimpleDateFormat("yyyyMMdd").parse(rs.getString("periodo")+"01"));
				archivo.setTipoPedido(rs.getString("tipo_pedido"));
				archivo.setSector(rs.getString("sector"));
				archivo.setPrestacionTotalImporte(rs.getDouble("total"));
				archivo.setPrestacionCargoOspim(rs.getDouble("cargo_ospim"));
				archivo.setPrestacionCargoPs(rs.getDouble("cargo_ps"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error en la busqueda de registros de exportacion a excel Agrupado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

    public List<AfiDocumentacion> getListaVencimientosCUD(Date fechaOrigen,Integer diasAlVto)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<AfiDocumentacion> list = null;
	try {
		String sql = "{call autorizaciones.trae_vencimientos_discapacidad_a_fecha_by_dias(?,?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		
		if (null != fechaOrigen) {
			stmt.setDate(1, new java.sql.Date(fechaOrigen.getTime()));
		} else {
			stmt.setNull(1, Types.DATE);
		}
		if (null != diasAlVto ) {
			stmt.setInt(2, diasAlVto);
		} else {
			stmt.setNull(2, Types.VARCHAR);
		}
		ResultSet rs = stmt.executeQuery();
		list = new ArrayList<AfiDocumentacion>();
		while (rs.next()) {
			AfiDocumentacion afiDoc = new AfiDocumentacion();
			Afiliado a = new Afiliado();
			a.setDocu_numero(rs.getString("docu_numero"));
			a.setApellido(rs.getString("apellido"));
			a.setNombre(rs.getString("nombre"));
			a.setEmail(rs.getString("email"));
			if(rs.getInt("seccional_id")>0) {
			   a.setSeccional(new Seccional(rs.getInt("seccional_id"),rs.getString("seccional_descripcion")));
			}
			afiDoc.setFecha_baja(rs.getDate("fecha_vto"));
			afiDoc.setAfiliado(a);
			list.add(afiDoc);
		}
	} catch (Exception e) {
		_log.error("Error al buscar Vencimientos CUD", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return list;
}


}
