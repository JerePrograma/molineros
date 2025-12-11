package ar.com.uoma.correspondencia.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.Correspondencia;
import ar.com.uoma.beans.TipoCorrespondencia;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class CorrespondenciaServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(CorrespondenciaServiceImpl.class);

	public static Correspondencia buscarCorrespondenciaPorId(
			int idCorrespondencia) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		Correspondencia corr = null;
		try {
			String sql = "{call uoma.buscar_correspondencia_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCorrespondencia);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				corr = Correspondencia.getMappingCorrespondenciaId(rs);
			}

		} catch (Exception e) {
			logger.error("error al buscar corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return corr;
	}

	public static List<Correspondencia> buscarCorrespondencia(String destino,
			String lugarRecepcion, Date envioRecepDesde, Date envioRecepHasta,
			int idCorrDesde, int idCorrHasta, int tipoCorr, String remitente,
			String destinatario, String receptor, String razon_prestador, 
			int provincia, int localidad, int id_seccional_remi) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<Correspondencia> listaCorr = new ArrayList<Correspondencia>();
		try {
			String sql = "{call uoma.buscar_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, destino);
			if (lugarRecepcion == null || lugarRecepcion.trim().equals("")) {
				stmt.setNull(2, Types.NULL);
			} else {
				stmt.setString(2, lugarRecepcion);
			}
			stmt.setDate(3, new java.sql.Date(envioRecepDesde.getTime()));
			stmt.setDate(4, new java.sql.Date(envioRecepHasta.getTime()));
			if (idCorrDesde > 0) {
				stmt.setInt(5, idCorrDesde);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			if (idCorrHasta > 0) {
				stmt.setInt(6, idCorrHasta);
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			if (tipoCorr > 0) {
				stmt.setInt(7, tipoCorr);
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			if (remitente.trim().equals("") || remitente == null || remitente.trim().equals("undefined") ) {
				stmt.setNull(8, Types.VARCHAR);
			} else {
				stmt.setString(8, remitente);
			}
			if (destinatario.trim().equals("") || destinatario == null || destinatario.trim().equals("undefined") ) {
				stmt.setNull(9, Types.VARCHAR);
			} else {
				stmt.setString(9, destinatario);
			}
			if (receptor.trim().equals("") || receptor == null || receptor.trim().equals("undefined") ) {
				stmt.setNull(10, Types.VARCHAR);
			} else {
				stmt.setString(10, receptor);
			}
			
			if (razon_prestador.trim().equals("") || razon_prestador == null || razon_prestador.trim().equals("undefined") ) {
				stmt.setNull(11, Types.VARCHAR);
			} else {
				stmt.setString(11, razon_prestador);
			}
			
			if (provincia > 0) {
				stmt.setInt(12, provincia);
			} else {
				stmt.setNull(12, Types.INTEGER);
			}
			
			if (localidad > 0) {
				stmt.setInt(13, localidad);
			} else {
				stmt.setNull(13, Types.INTEGER);
			}
			
			if (id_seccional_remi > 0) {
				stmt.setInt(14, id_seccional_remi);
			} else {
				stmt.setNull(14, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Correspondencia corr = Correspondencia
						.getMappingCorrespondencia(rs);
				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}

	public static List<TipoCorrespondencia> buscarTipoCorrespondencia()
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<TipoCorrespondencia> listaTipo = new ArrayList<TipoCorrespondencia>();
		try {
			String sql = "{call uoma.trae_tipo_correspondencia()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TipoCorrespondencia tipo = new TipoCorrespondencia(
						rs.getInt("id"), rs.getString("descripcion"));
				listaTipo.add(tipo);
			}

		} catch (Exception e) {
			logger.error("error al buscar tipo corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaTipo;
	}

	public Correspondencia grabarCorrespondencia(
			Correspondencia correspondencia, User user) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		int id_domicilio_remitente = 0;
		int id_domicilio_destinatario = 0;
		int id_correspondencia = 0;

		try {
			String sql = "{? = call uoma.inserta_domicilio_correspondencia(?,?,?,?,?,?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			stmt = con.prepareCall(sql.toString());

			if (correspondencia.getEdificioRemitente().trim()
					.equals("Particular")) {

				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, correspondencia.getDomicilioRemitente()
						.getLocalidadId());
				stmt.setInt(3, correspondencia.getDomicilioRemitente()
						.getProvinciaId());
				stmt.setString(4, correspondencia.getDomicilioRemitente()
						.getCalle());
				stmt.setString(5, correspondencia.getDomicilioRemitente()
						.getNumero());
				stmt.setString(6, correspondencia.getDomicilioRemitente()
						.getPiso());
				stmt.setString(7, correspondencia.getDomicilioRemitente()
						.getDepto());
				stmt.setString(8, correspondencia.getDomicilioRemitente()
						.getPostal_codi());
				stmt.setString(9, correspondencia.getDomicilioRemitente()
						.getObservaciones());
				stmt.setString(10, user.getScreenName());
				stmt.executeUpdate();
				id_domicilio_remitente = stmt.getInt(1);
			}

			if (correspondencia.getEdificioDestinatario().trim()
					.equals("Particular")) {
				stmt = con.prepareCall(sql.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, correspondencia.getDomicilioDestinatario()
						.getLocalidadId());
				stmt.setInt(3, correspondencia.getDomicilioDestinatario()
						.getProvinciaId());
				stmt.setString(4, correspondencia.getDomicilioDestinatario()
						.getCalle());
				stmt.setString(5, correspondencia.getDomicilioDestinatario()
						.getNumero());
				stmt.setString(6, correspondencia.getDomicilioDestinatario()
						.getPiso());
				stmt.setString(7, correspondencia.getDomicilioDestinatario()
						.getDepto());
				stmt.setString(8, correspondencia.getDomicilioDestinatario()
						.getPostal_codi());
				stmt.setString(9, correspondencia.getDomicilioDestinatario()
						.getObservaciones());
				stmt.setString(10, user.getScreenName());
				stmt.executeUpdate();
				id_domicilio_destinatario = stmt.getInt(1);
			}

			String sql2 = "{? =call uoma.inserta_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql2.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, correspondencia.getDestino());
			stmt.setDate(3, new java.sql.Date(correspondencia
					.getFechaEnvioRecepcion().getTime()));
			stmt.setString(4, correspondencia.getApellidoRemitente());
			stmt.setString(5, correspondencia.getNombreRemitente());
			stmt.setString(6, correspondencia.getApellidoDestinatario());
			stmt.setString(7, correspondencia.getNombreDestinatario());
			if(correspondencia.getTipo().getIdTipo()>0){
				stmt.setInt(8, correspondencia.getTipo().getIdTipo());
			}else{
				stmt.setNull(8, Types.INTEGER);
			}
			stmt.setString(9, correspondencia.getLugarRecepcion());
			stmt.setString(10, correspondencia.getObservaciones());
			if (correspondencia.getSeccionalRemitente() != null && correspondencia.getSeccionalRemitente().getIdSeccional()>0) {
				stmt.setInt(11, correspondencia.getSeccionalRemitente().getId());
			} else {
				stmt.setNull(11, Types.INTEGER);
			}
			if (correspondencia.getSeccionalDestinatario() != null && correspondencia.getSeccionalDestinatario().getIdSeccional()>0) {
				stmt.setInt(12, correspondencia.getSeccionalDestinatario()
						.getId());
			} else {
				stmt.setNull(12, Types.INTEGER);
			}
			stmt.setString(13, correspondencia.getEdificioRemitente());
			stmt.setString(14, correspondencia.getEdificioDestinatario());
			if(id_domicilio_remitente>0){
				stmt.setInt(15, id_domicilio_remitente);
			}else{
				stmt.setNull(15, Types.NULL);
			}
			if(id_domicilio_destinatario>0){
				stmt.setInt(16, id_domicilio_destinatario);
			}else{
				stmt.setNull(16, Types.NULL);
			}
			
			stmt.setString(17, correspondencia.getRazonPrestadorDestinatario());
			stmt.setString(18, correspondencia.getRazonPrestadorRemitente());
			stmt.setBoolean(19, correspondencia.isGastoSeccional());
			stmt.setBoolean(20, correspondencia.isReintegro());
			stmt.setBoolean(21, correspondencia.isPadrones());
			stmt.setBoolean(22, correspondencia.isDiscapacidad());
			stmt.setBoolean(23, correspondencia.isOtros());
			stmt.setBoolean(24, correspondencia.isDocumentacion());
			stmt.setBoolean(25, correspondencia.isFacturacion());
			stmt.setBoolean(26, correspondencia.isTesoreria());
			stmt.setBoolean(27, correspondencia.isMedicamentos());
			stmt.setInt(28, correspondencia.getDomicilioRemitente().getLocalidadId());
			stmt.setInt(29, correspondencia.getDomicilioRemitente().getProvinciaId());
			stmt.setString(30, correspondencia.getDatosFactura());
			stmt.setString(31, correspondencia.getTipoEnvio());
			stmt.setString(32, correspondencia.getOblea());
			stmt.setString(33, correspondencia.getCodFarmacia());
			stmt.setString(34, correspondencia.getFarmacia());			
			stmt.setString(35, user.getScreenName());

			stmt.executeUpdate();
			id_correspondencia = stmt.getInt(1);

			con.commit();

		} catch (Exception e) {
			logger.error("error al grabar correspondencia", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.fatal("ERROR AL HACER ROLLBACK CORRESPONDENCIA!", e);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		correspondencia.setIdCorrespondencia(id_correspondencia);
		return correspondencia;
	}

	public Correspondencia actualizarCorrespondencia(
			Correspondencia correspondencia, User user) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		int id_domicilio_remitente = 0;
		int id_domicilio_destinatario = 0;
		int id_correspondencia = 0;
		String sql = null;

		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			if (null != correspondencia.getDomicilioRemitente()
					&& correspondencia.getDomicilioRemitente()
							.getId_domicilio() != 0 && correspondencia.getEdificioRemitente().trim().equals("Particular")) {
				sql = "{? = call uoma.actualiza_domicilio_correspondencia(?,?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, correspondencia.getDomicilioRemitente()
						.getLocalidadId());
				stmt.setInt(3, correspondencia.getDomicilioRemitente()
						.getProvinciaId());
				stmt.setString(4, correspondencia.getDomicilioRemitente()
						.getCalle());
				stmt.setString(5, correspondencia.getDomicilioRemitente()
						.getNumero());
				stmt.setString(6, correspondencia.getDomicilioRemitente()
						.getPiso());
				stmt.setString(7, correspondencia.getDomicilioRemitente()
						.getDepto());
				stmt.setString(8, correspondencia.getDomicilioRemitente()
						.getPostal_codi());
				stmt.setString(9, correspondencia.getDomicilioRemitente()
						.getObservaciones());
				stmt.setInt(10, correspondencia.getDomicilioRemitente().getId_domicilio());
				stmt.setString(11, user.getScreenName());
				stmt.executeUpdate();
				id_domicilio_remitente=correspondencia.getDomicilioRemitente().getId_domicilio();
			} else if(correspondencia.getEdificioRemitente().trim().equals("Particular")) {
				sql = "{? = call uoma.inserta_domicilio_correspondencia(?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, correspondencia.getDomicilioRemitente()
						.getLocalidadId());
				stmt.setInt(3, correspondencia.getDomicilioRemitente()
						.getProvinciaId());
				stmt.setString(4, correspondencia.getDomicilioRemitente()
						.getCalle());
				stmt.setString(5, correspondencia.getDomicilioRemitente()
						.getNumero());
				stmt.setString(6, correspondencia.getDomicilioRemitente()
						.getPiso());
				stmt.setString(7, correspondencia.getDomicilioRemitente()
						.getDepto());
				stmt.setString(8, correspondencia.getDomicilioRemitente()
						.getPostal_codi());
				stmt.setString(9, correspondencia.getDomicilioRemitente()
						.getObservaciones());
				stmt.setString(10, user.getScreenName());
				stmt.executeUpdate();
				id_domicilio_remitente = stmt.getInt(1);
			}

			

			if (null != correspondencia.getDomicilioDestinatario()
					&& correspondencia.getDomicilioDestinatario()
							.getId_domicilio() != 0 && correspondencia.getEdificioDestinatario().trim().equals("Particular")) {
				sql = "{? = call uoma.actualiza_domicilio_correspondencia(?,?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, correspondencia.getDomicilioDestinatario()
						.getLocalidadId());
				stmt.setInt(3, correspondencia.getDomicilioDestinatario()
						.getProvinciaId());
				stmt.setString(4, correspondencia.getDomicilioDestinatario()
						.getCalle());
				stmt.setString(5, correspondencia.getDomicilioDestinatario()
						.getNumero());
				stmt.setString(6, correspondencia.getDomicilioDestinatario()
						.getPiso());
				stmt.setString(7, correspondencia.getDomicilioDestinatario()
						.getDepto());
				stmt.setString(8, correspondencia.getDomicilioDestinatario()
						.getPostal_codi());
				stmt.setString(9, correspondencia.getDomicilioDestinatario()
						.getObservaciones());
				stmt.setInt(10, correspondencia.getDomicilioDestinatario().getId_domicilio());
				stmt.setString(11, user.getScreenName());				
				stmt.executeUpdate();
				id_domicilio_destinatario=correspondencia.getDomicilioDestinatario().getId_domicilio();
			} else if(correspondencia.getEdificioDestinatario().trim().equals("Particular")) {
				sql = "{? = call uoma.inserta_domicilio_correspondencia(?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, correspondencia.getDomicilioDestinatario()
						.getLocalidadId());
				stmt.setInt(3, correspondencia.getDomicilioDestinatario()
						.getProvinciaId());
				stmt.setString(4, correspondencia.getDomicilioDestinatario()
						.getCalle());
				stmt.setString(5, correspondencia.getDomicilioDestinatario()
						.getNumero());
				stmt.setString(6, correspondencia.getDomicilioDestinatario()
						.getPiso());
				stmt.setString(7, correspondencia.getDomicilioDestinatario()
						.getDepto());
				stmt.setString(8, correspondencia.getDomicilioDestinatario()
						.getPostal_codi());
				stmt.setString(9, correspondencia.getDomicilioDestinatario()
						.getObservaciones());
				stmt.setString(10, user.getScreenName());
				stmt.executeUpdate();
				id_domicilio_destinatario = stmt.getInt(1);
			}

			String sql2 = "{? =call uoma.actualiza_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql2.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, correspondencia.getDestino());
			stmt.setDate(3, new java.sql.Date(correspondencia
					.getFechaEnvioRecepcion().getTime()));
			stmt.setString(4, correspondencia.getApellidoRemitente());
			stmt.setString(5, correspondencia.getNombreRemitente());
			stmt.setString(6, correspondencia.getApellidoDestinatario());
			stmt.setString(7, correspondencia.getNombreDestinatario());
			if(correspondencia.getTipo().getIdTipo()>0){
				stmt.setInt(8, correspondencia.getTipo().getIdTipo());
			}else{
				stmt.setNull(8, Types.INTEGER);
			}
			stmt.setString(9, correspondencia.getLugarRecepcion());
			stmt.setString(10, correspondencia.getObservaciones());
			if (correspondencia.getSeccionalRemitente() != null) {
				stmt.setInt(11, correspondencia.getSeccionalRemitente().getId());
			} else {
				stmt.setNull(11, Types.INTEGER);
			}
			if (correspondencia.getSeccionalDestinatario() != null) {
				stmt.setInt(12, correspondencia.getSeccionalDestinatario()
						.getId());
			} else {
				stmt.setNull(12, Types.INTEGER);
			}
			stmt.setString(13, correspondencia.getEdificioRemitente());
			stmt.setString(14, correspondencia.getEdificioDestinatario());
			if(id_domicilio_remitente>0){
				stmt.setInt(15, id_domicilio_remitente);
			}else{
				stmt.setNull(15, Types.INTEGER);
			}
			if(id_domicilio_destinatario>0){
				stmt.setInt(16, id_domicilio_destinatario);
			}else{
				stmt.setNull(16, Types.INTEGER);
			}
			
			stmt.setInt(17, correspondencia.getIdCorrespondencia());
			stmt.setString(18, correspondencia.getRazonPrestadorDestinatario());
			stmt.setString(19, correspondencia.getRazonPrestadorRemitente());
			
			stmt.setBoolean(20, correspondencia.isGastoSeccional());
			stmt.setBoolean(21, correspondencia.isReintegro());
			stmt.setBoolean(22, correspondencia.isPadrones());
			stmt.setBoolean(23, correspondencia.isDiscapacidad());
			stmt.setBoolean(24, correspondencia.isOtros());
			stmt.setBoolean(25, correspondencia.isDocumentacion());
			stmt.setBoolean(26, correspondencia.isFacturacion());
			stmt.setBoolean(27, correspondencia.isTesoreria());
			stmt.setBoolean(28, correspondencia.isMedicamentos());
			
			stmt.setInt(29, correspondencia.getDomicilioRemitente().getLocalidadId());
			stmt.setInt(30, correspondencia.getDomicilioRemitente().getProvinciaId());
			
			stmt.setString(31, correspondencia.getDatosFactura());
			
			stmt.setString(32, correspondencia.getTipoEnvio());
			stmt.setString(33, correspondencia.getOblea());
			
			stmt.setString(34, correspondencia.getCodFarmacia());
			stmt.setString(35, correspondencia.getFarmacia());
			
			stmt.setString(36, user.getScreenName());

			stmt.executeUpdate();
			id_correspondencia = stmt.getInt(1);

			con.commit();

		} catch (Exception e) {
			logger.error("error al actualizar correspondencia", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.fatal("ERROR AL HACER ROLLBACK CORRESPONDENCIA!", e);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		correspondencia.setIdCorrespondencia(id_correspondencia);
		return correspondencia;
	}

}
