package ar.com.cgt.ddhh.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hsqldb.Types;

import ar.com.cgt.ddhh.beans.NormaDdHh;
import ar.com.cgt.ddhh.beans.TemasNormasDDHH;
import ar.com.cgt.ddhh.beans.TiposNormasDDHH;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class NormaDDHHServiceImpl {
	
	private static Log _log = LogFactoryUtil.getLog(NormaDDHHServiceImpl.class);

	private static NormaDDHHServiceImpl instance = null;

	public static NormaDDHHServiceImpl getInstance() {
		if (null == instance) {
			instance = new NormaDDHHServiceImpl();
		}
		return instance;
	}

	public List<NormaDdHh> getNormasDhHh(Date fechaDesde, Date fechaHasta, String sistema, String numero, 
			int id_tema_normadh, int id_tipo_normadh, String autor, String lugar ) throws Exception{
		
		Connection con = null;
		CallableStatement stmt = null;
		List<NormaDdHh> normasdh = new ArrayList<NormaDdHh>();
		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call buscar_normasddhh( ?, ?, ?, ?, ?, ?, ?, ? ) }";
			stmt = con.prepareCall(sql.toString());
			if(fechaDesde != null){
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			if(fechaHasta != null){
				stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			}else{
				stmt.setNull(2, Types.DATE);
			}
			if (null != sistema && !sistema.trim().equals("")) {
				stmt.setString(3, sistema);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			if (null != numero && !numero.trim().equals("")) {
				stmt.setString(4, numero);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (id_tema_normadh  > 0 ) {
				stmt.setInt(5, id_tema_normadh);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			if (id_tipo_normadh  > 0 ) {
				stmt.setInt(6, id_tema_normadh);
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			if (null != autor && !autor.trim().equals("")) {
				stmt.setString(7, autor);
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (null != lugar && !lugar.trim().equals("")) {
				stmt.setString(8, lugar);
			} else {
				stmt.setNull(8, Types.VARCHAR);
			}

			ResultSet rs = stmt.executeQuery();
			NormaDdHh norma;
			TemasNormasDDHH tema;
			TiposNormasDDHH tipo;

			while (rs.next()) {
				norma = new NormaDdHh();
				norma.setId(rs.getInt("id"));
				norma.setSistema(rs.getString("sistema"));
				norma.setNumero(rs.getString("numero"));
				norma.setFuenteDependencia(rs.getString("fuente_dependencia"));
				norma.setAutor(rs.getString("autor"));
				norma.setFecha(rs.getDate("fecha"));
				norma.setLugar(rs.getString("lugar"));
				norma.setResumen(rs.getString("resumen"));
				norma.setContenido(rs.getString("contenido"));
				norma.setLink(rs.getString("link"));
				norma.setSigla(rs.getString("sigla"));
				norma.setIncLegisNac(rs.getString("inc_legis_nac"));
				
				tema = new TemasNormasDDHH(rs.getInt("id_tema_norma_ddhh"), rs.getString("descripcion_tema"));
				tipo = new TiposNormasDDHH(rs.getInt("id_tipo_norma_ddhh"), rs.getString("descripcion_tipo"));
				norma.setTema(tema);
				norma.setTipo(tipo);
				normasdh.add(norma);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar normas DDHH", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar normas DDHH", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return normasdh;

	}

	public NormaDdHh getNormaDDHH(int id_norma) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		NormaDdHh norma = null;
		TemasNormasDDHH tema;
		TiposNormasDDHH tipo;
		
		
		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call buscar_normaddhh(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_norma);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				norma = new NormaDdHh();
				norma.setId(rs.getInt("id"));
				norma.setSistema(rs.getString("sistema"));
				norma.setNumero(rs.getString("numero"));
				norma.setFuenteDependencia(rs.getString("fuente_dependencia"));
				norma.setAutor(rs.getString("autor"));
				norma.setFecha(rs.getDate("fecha"));
				norma.setLugar(rs.getString("lugar"));
				norma.setResumen(rs.getString("resumen"));
				norma.setContenido(rs.getString("contenido"));
				norma.setLink(rs.getString("link"));
				norma.setSigla(rs.getString("sigla"));
				norma.setIncLegisNac(rs.getString("inc_legis_nac"));
				
				tema = new TemasNormasDDHH(rs.getInt("id_tema_norma_ddhh"), rs.getString("descripcion_tema"));
				tipo = new TiposNormasDDHH(rs.getInt("id_tipo_norma_ddhh"), rs.getString("descripcion_tipo"));
				norma.setTema(tema);
				norma.setTipo(tipo);

			}
		} catch (SQLException e) {
			_log.error("Error al buscar normas de DDHH", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar normas de DDHH", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return norma;

	}

	public int save(NormaDdHh normaDH, String screenName, Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call insertar_norma_ddhh(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, normaDH.getSistema());
			stmt.setInt(2, normaDH.getTipo().getId());
			stmt.setString(3, normaDH.getNumero());
			stmt.setString(4, normaDH.getFuenteDependencia());
			stmt.setString(5, normaDH.getAutor());
			stmt.setDate(6, new java.sql.Date(normaDH.getFecha().getTime()));
			stmt.setString(7, normaDH.getLugar());
			stmt.setString(8, normaDH.getResumen());
			stmt.setString(9, normaDH.getContenido());
			stmt.setInt(10, normaDH.getTema().getId());
			stmt.setString(11, normaDH.getLink());
			stmt.setString(12, normaDH.getSigla());
			stmt.setString(13, normaDH.getIncLegisNac());
			stmt.setString(14, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar norma derechos humanos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar norma derechos humanos", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}
	
	public int update(NormaDdHh normaDH, String screenName, Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call actualizar_norma_ddhh(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, normaDH.getId());
			stmt.setString(2, normaDH.getSistema());
			stmt.setInt(3, normaDH.getTipo().getId());
			stmt.setString(4, normaDH.getNumero());
			stmt.setString(5, normaDH.getFuenteDependencia());
			stmt.setString(6, normaDH.getAutor());
			stmt.setDate(7, new java.sql.Date(normaDH.getFecha().getTime()));
			stmt.setString(8, normaDH.getLugar());
			stmt.setString(9, normaDH.getResumen());
			stmt.setString(10, normaDH.getContenido());
			stmt.setInt(11, normaDH.getTema().getId());
			stmt.setString(12, normaDH.getLink());
			stmt.setString(13, normaDH.getSigla());
			stmt.setString(14, normaDH.getIncLegisNac());
			stmt.setString(15, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar norma derechos humanos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al actualizar norma derechos humanos", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public void borrarNormaDDHH(int id_norma, String screenName) throws Exception {
	Connection con = null;
	CallableStatement stmt = null;
	
	try {
		con = ConnectionHelper.getConnectionCGT();
		String sql = "{call borrar_normaddhh(?,?)}";
		stmt = con.prepareCall(sql.toString());

		stmt.setInt(1, id_norma);
		stmt.setString(2, screenName);

		ResultSet rs = stmt.executeQuery();
		
	} catch (SQLException e) {
		_log.error("Error al borrar la norma DDHH", e);
		throw new SystemException(e);

	} catch (Exception e) {
		_log.error("Error al borrar la norma DDHH", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}		

}

}
