package ar.com.uoma.unidad_operativa.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.global.WebKeysPortal;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.Incidente;
import ar.com.uoma.beans.IncidenteTotal;
import ar.com.uoma.beans.SeguimientoIncidente;
import ar.com.uoma.unidad_operativa.BusquedaIncidentesUnidadOpeFiltro;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class UnidadOperativaServiceImpl {
	private static Log logger = LogFactoryUtil			
			.getLog(UnidadOperativaServiceImpl.class);
	
	public Incidente buscarIncidente(int id_incidente) throws SystemException{
		Incidente incidente=null;
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoIncidente> detalle=new ArrayList();
		int result=0;
		try {			
			String sql = "{call uoma.buscar_incidente(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setInt(1, id_incidente);
						
			ResultSet rs = stmt.executeQuery();			
			while (rs.next()) {
				incidente=Incidente.getMappingIncidente(rs);
			}
			sql = "{call uoma.buscar_seguimiento_incidente(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_incidente);
			rs = stmt.executeQuery();
			while (rs.next()) {
				SeguimientoIncidente seguimiento=SeguimientoIncidente.getMapping(rs);
				detalle.add(seguimiento);
			}
			if(null!=incidente && null!=detalle){
				incidente.setSeguimientoIncidente(detalle);
			}
			
		} catch (Exception e) {
			logger.error("error al buscar incidente", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	
		return incidente;
	}
	
	
	public List<IncidenteTotal> buscarIncidentes(BusquedaIncidentesUnidadOpeFiltro filtro) throws SystemException{
		List<IncidenteTotal> incidentes=null;
		Connection con = null;
		CallableStatement stmt = null;
		try {
			incidentes=new ArrayList<IncidenteTotal>();
			String sql = "{call uoma.buscar_incidentes(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(StringUtils.checkEmpty(filtro.getCuil())){
				stmt.setNull(1,Types.VARCHAR);
			}else{
				stmt.setString(1, filtro.getCuil());
			}
			
			stmt.setInt(2, filtro.getInte());
			
			if(StringUtils.checkEmpty(filtro.getTipoDoc())){
				stmt.setNull(3,Types.VARCHAR);
			}else{
				stmt.setString(3, filtro.getTipoDoc());
			}
			
			if(StringUtils.checkEmpty(filtro.getNroDoc())){
				stmt.setNull(4,Types.VARCHAR);
			}else{
				stmt.setString(4, filtro.getNroDoc());
			}
			
			if(0==filtro.getSeccional_int()){
				stmt.setNull(5,Types.INTEGER);
			}else{
				stmt.setInt(5, filtro.getSeccional_int());
			}
			
			if(StringUtils.checkEmpty(filtro.getApellido())){
				stmt.setNull(6,Types.VARCHAR);
			}else{
				stmt.setString(6, filtro.getApellido());
			}
			
			if(StringUtils.checkEmpty(filtro.getNombre())){
				stmt.setNull(7,Types.VARCHAR);
			}else{
				stmt.setString(7, filtro.getNombre());
			}
			
			if(StringUtils.checkEmpty(filtro.getEntidad())){
				stmt.setNull(8,Types.INTEGER);
				stmt.setNull(9,Types.INTEGER);
				stmt.setNull(10,Types.INTEGER);
			}else if(filtro.getEntidad().equals(WebKeysPortal.ENTIDAD_OSPIM)){
				if(filtro.getNroAfiliado()==0){
					stmt.setNull(8,Types.INTEGER);
				}else{
					stmt.setInt(8, filtro.getNroAfiliado());	
				}								
				stmt.setNull(9,Types.INTEGER);
				stmt.setNull(10,Types.INTEGER);
			}else if(filtro.getEntidad().equals(WebKeysPortal.ENTIDAD_UOMA)){
				if(filtro.getNroAfiliado()==0){
					stmt.setNull(9,Types.INTEGER);
				}else{
					stmt.setInt(9, filtro.getNroAfiliado());	
				}
				stmt.setNull(8,Types.INTEGER);				
				stmt.setNull(10,Types.INTEGER);
			}else if(filtro.getEntidad().equals(WebKeysPortal.ENTIDAD_AMTIMA)){
				if(filtro.getNroAfiliado()==0){
					stmt.setNull(10,Types.INTEGER);
				}else{
					stmt.setInt(10, filtro.getNroAfiliado());	
				}				
				stmt.setNull(8,Types.INTEGER);
				stmt.setNull(9,Types.INTEGER);				
			}
			
			if(null!=filtro.getFechaDesde()){
				stmt.setDate(11, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}else{
				stmt.setNull(11, Types.DATE);
			}
			
			if(null!=filtro.getFechaHasta()){
				stmt.setDate(12, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}else{
				stmt.setNull(12, Types.DATE);
			}
			
			if(0==filtro.getSeccional_afiliado_int()){
				stmt.setNull(13,Types.INTEGER);
			}else{
				stmt.setInt(13, filtro.getSeccional_afiliado_int());
			}
			
			stmt.setInt(14, filtro.getPagina());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				incidentes.add(IncidenteTotal.getMappingIncidentes(rs));
			}
			
		} catch (Exception e) {
			logger.error("error al buscar casos de unidad operativa", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	
		return incidentes;
	}

	public int grabarIncidente(Incidente incidente, User usuario) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{? = call uoma.inserta_incidente_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, incidente.getIdSeccional());
			stmt.setDate(3, new java.sql.Date(incidente.getFecha().getTime()));
			stmt.setString(4, incidente.getAfiliado().getCuil_titular());
			stmt.setInt(5, incidente.getAfiliado().getInte());
			stmt.setInt(6, incidente.getLugarIncidente().getLocalidad().getId());
			stmt.setInt(7, incidente.getLugarIncidente().getProvincia().getId());
			stmt.setString(8, incidente.getLugarIncidente().getCalle());
			stmt.setString(9, incidente.getLugarIncidente().getNumero());
			stmt.setString(10, incidente.getLugarIncidente().getPiso());
			stmt.setString(11, incidente.getLugarIncidente().getDepto());
			stmt.setString(12, incidente.getLugarIncidente().getPostal_codi());
			stmt.setString(13, incidente.getLugarIncidente().getObservaciones());
			stmt.setString(14, incidente.getDetalleIncidente());
			stmt.setString(15, incidente.getSeguimientoIncidenteNuevo());
			stmt.setDate(16, new java.sql.Date(incidente.getFechaRecepcion().getTime()));
			stmt.setString(17, usuario.getScreenName());
			stmt.executeUpdate();
			int result=stmt.getInt(1);
			return result;
		} catch (Exception e) {
			logger.error("error al insertar incidente", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public int editarIncidente(Incidente incidente, User usuario, int compara) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call uoma.editar_incidente_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, incidente.getIdSeccional());
			stmt.setDate(2, new java.sql.Date(incidente.getFecha().getTime()));
			stmt.setString(3, incidente.getAfiliado().getCuil_titular());
			stmt.setInt(4, incidente.getAfiliado().getInte());
			stmt.setInt(5, incidente.getLugarIncidente().getLocalidad().getId());
			stmt.setInt(6, incidente.getLugarIncidente().getProvincia().getId());
			stmt.setString(7, incidente.getLugarIncidente().getCalle());
			stmt.setString(8, incidente.getLugarIncidente().getNumero());
			stmt.setString(9, incidente.getLugarIncidente().getPiso());
			stmt.setString(10, incidente.getLugarIncidente().getDepto());
			stmt.setString(11, incidente.getLugarIncidente().getPostal_codi());
			stmt.setString(12, incidente.getLugarIncidente().getObservaciones());
			stmt.setString(13, incidente.getDetalleIncidente());
			stmt.setString(14, incidente.getSeguimientoIncidenteNuevo());
			stmt.setString(15, usuario.getScreenName());
			stmt.setInt(16, incidente.getIdIncidente());
			stmt.setInt(17, compara);
			stmt.setInt(18, incidente.getLugarIncidente().getId_domicilio());
			stmt.setDate(19, new java.sql.Date(incidente.getFechaRecepcion().getTime()));
			stmt.executeUpdate();
			return 0;
		} catch (Exception e) {
			logger.error("error al actualizar incidente", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
}

