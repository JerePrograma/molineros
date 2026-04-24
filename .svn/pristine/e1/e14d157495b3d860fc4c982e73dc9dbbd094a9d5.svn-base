package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.util.ConnectionHelper;

/**
 * <a href="DocumentacionServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class DocumentacionServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(DocumentacionServiceImpl.class);

	public String grabaDocumentacion(String cuil,
			int inte, int id_documentacion, Date fechaIngreso,
			Date fechaEgreso, User user, int id_motivo_baja,String certificado,Connection connectionParameter) throws Exception {
		CallableStatement stmt = null, stmt1 = null;
		Connection conn = null;
		String result = null; 
		
		if(connectionParameter == null){
			conn = ConnectionHelper.getConnection();
		}else{
			conn = connectionParameter;
		}
		try {
			String sql = "{call inserta_documento(?,?,?,?,?,?,?,?)}";
			stmt = conn.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setInt(3, id_documentacion);
			stmt.setDate(4, new java.sql.Date(fechaIngreso.getTime()));
			stmt.setDate(5, null != fechaEgreso ? (new java.sql.Date(
					fechaEgreso.getTime())) : null);
			stmt.setString(6, user.getScreenName());
			stmt.setInt(7, id_motivo_baja);
			if(certificado!=null) {
			  stmt.setString(8, certificado);
			}else {
			  stmt.setNull(8,Types.VARCHAR);	
			}
		
			ResultSet rsEdit = stmt.executeQuery();
			
			while (rsEdit.next()) {
				result = rsEdit.getString(1);
			}
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt);
				ConnectionHelper.cerrar(stmt1,conn);
			}else{
				ConnectionHelper.cerrar(stmt);
				ConnectionHelper.cerrar(stmt1);
			}
		}
		return result;
	}

	
	public String editaDocumentacion(String cuil,
			int inte, int id_documentacion, Date fechaIngreso,
			Date fechaEgreso, User user, int id,String certificado) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		String result = null; 
		try {
			String sql = "{call edita_documentacion(?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setInt(3, id_documentacion);
			stmt.setDate(4, new java.sql.Date(fechaIngreso.getTime()));
			stmt.setDate(5, null != fechaEgreso ? (new java.sql.Date(
					fechaEgreso.getTime())) : null);
			stmt.setString(6, user.getScreenName());
			stmt.setInt(7, id);
			if(certificado!=null) {
			   stmt.setString(8, certificado);
			}else {
			   stmt.setNull(8,Types.VARCHAR);	
			}
			//result = stmt.executeUpdate();
			ResultSet rsEdit = stmt.executeQuery();
			
			while (rsEdit.next()) {
				result = rsEdit.getString(1);
			}
					
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public int  borraDocumentacion(String cuil,
			int inte, int id_documento, Date fechaIngreso, User user,
			Date fechaMayoriaEdad, int id) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<AfiDocumentacion> documentos = null;
		int result =0;
		try {
			// borro documentacion
			String sql = "{call borra_documentacion(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setInt(3, id_documento);
			stmt.setDate(4, new java.sql.Date(fechaIngreso.getTime()));
			stmt.setString(5, user.getScreenName());
			stmt.setDate(6, fechaMayoriaEdad != null ? (new java.sql.Date(
					fechaMayoriaEdad.getTime())) : null);
			stmt.setInt(7, id);
			result = stmt.executeUpdate();
			
			

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public List<AfiDocumentacion> buscaDocumentos(String cuil, int inte)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<AfiDocumentacion> documentos = null;
		try {
			con = ConnectionHelper.getConnection();
			// Busco documentacion
			String sqlList = "{call trae_documentacion_afi(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			documentos = new ArrayList<AfiDocumentacion>();
			while (rs.next()) {
				AfiDocumentacion bp = new AfiDocumentacion(rs
						.getString("cuil_titular"), rs.getInt("inte"), rs
						.getInt("id_documento"), rs.getString("descripcion"),
						rs.getDate("fecha_ingreso"), rs.getDate("fecha_egreso"), rs.getInt("id"),rs.getString("codigo_cud"));
				documentos.add(bp);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return documentos;
	}

}
