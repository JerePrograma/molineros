package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="ReincorporarServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class ReincorporarServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(ReincorporarServiceImpl.class);

	public void actualizaNumAfiliadosGrupo(String cuil_titular, int inte,
			Connection connectionParameter) throws Exception {
		CallableStatement stmt = null;
		try {
			String sqlList = "{call actualiza_num_afiliados_grupo(?,?)}";
			stmt = connectionParameter.prepareCall(sqlList.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			stmt.executeUpdate();
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	// no hay continuidad, creo un nuevo registro y el anterior lo almaceno en
	// la tabla de históricos
	// si desea recuperar laborales y planes de beneficiario, recuperar == 1
	// saco fecha de baja a todo
	// no desea recuperar laborales y planes de beneficiario, recuperar == 0
	// saco fecha de baja (excepto lab y planes)
	public void reincorporarAfiliado(Afiliado afiliado, Date vigen_fecha,
			Date fecha_egreso, /*boolean recuperar,*/ int continuidad, String user,
			int id_motivo_baja_menor_edad, Connection connectionParameter)
			throws Exception {
		CallableStatement stmt = null, stmt2 = null;
		try {
			String sqlList = "{call reincorporarafiliado(?,?,?,?,?,?,?,?)}";
			stmt = connectionParameter.prepareCall(sqlList.toString());
			stmt.setString(1, afiliado.getCuil_titular());
			stmt.setInt(2, afiliado.getInte());
			if (vigen_fecha != null) {
				stmt.setDate(3, new java.sql.Date(vigen_fecha.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			if (fecha_egreso != null) {
				stmt.setDate(4, new java.sql.Date(fecha_egreso.getTime()));
			} else {
				stmt.setNull(4, Types.DATE);
			}
			stmt.setInt(5, 0); //mando 0 asi no toca los planes
//			stmt.setInt(5, recuperar ? 1 : 0);
			stmt.setInt(6, continuidad);
			stmt.setString(7, user);
			if (id_motivo_baja_menor_edad == -1) {
				stmt.setNull(8, Types.INTEGER);
			} else {
				stmt.setInt(8, id_motivo_baja_menor_edad);
			}
			stmt.executeUpdate();
			
//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";	  
			
			stmt2 = connectionParameter.prepareCall(sqlInsert.toString());
			stmt2.setString(1, afiliado.getCuil_titular());
			stmt2.setInt(2, afiliado.getInte());
			stmt2.setInt(3, afiliado.getIdCorrespondencia());
			stmt2.setNull(4, Types.TIMESTAMP); // en este alta de afiliado todavia no conocemos la fecha de la impresion de credencial
			if(fecha_egreso != null){ // para afiliados integrantes que vengan con baja futura
				stmt2.setInt(5, id_motivo_baja_menor_edad);					
				stmt2.setTimestamp(6, new java.sql.Timestamp(fecha_egreso.getTime()) );
			}else{
				stmt2.setNull(5, Types.INTEGER);
				stmt2.setNull(6, Types.TIMESTAMP);
			}
			stmt2.setString(7, "reincorporacion"); // reincorporacion
			stmt2.setString(8, user);
			
			stmt2.executeUpdate();
			
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt2);
		}
	}

	/**
	 * Calcula fecha de baja futura para integrantes, si el integrante es discapacitado se buscara en documentacion del tipo de discapacidad,
	 * si no lo es, se buscara en certificado de estudios...
	 * 
	 * @param connectionParameter
	 * @param cuil
	 * @param inte
	 * @return
	 * @throws Exception
	 */
	public Date calculaFechaBajaFuturaIntegrante(Connection connectionParameter, String cuil_titular, int inte)
			throws Exception {
		
		Date fechaBajaFutura = null;
		Connection con =  null;
		CallableStatement stmt = null;
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		try {
			String sql = "{call calcula_baja_futura_integrante(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			
			ResultSet rs = stmt.executeQuery() ; // un solo resultado obtengo
			if(rs.next()){
				fechaBajaFutura = rs.getDate(1);
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		
		return fechaBajaFutura;
	}

}
