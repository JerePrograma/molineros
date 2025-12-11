package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="BusquedaAfiliadoServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class SituLaboralServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(SituLaboralServiceImpl.class);

	public void grabaSituLaboral(SituacionLaboral situLaboralAdd, String user,
			Connection con) throws Exception {
		CallableStatement stmt = null;
		CallableStatement stmt1 = null;
		CallableStatement stmt2 = null;
		try {
			String sql = "{call inserta_situ_laboral(?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, situLaboralAdd.getAfiliado().getCuil_titular());
			stmt.setInt(2, situLaboralAdd.getAfiliado().getInte());
			stmt.setString(3, situLaboralAdd.getEmpresa().getCuit());
			stmt.setString(4, situLaboralAdd.getEmpresa().getSucursal());
			stmt.setInt(5, situLaboralAdd.getId_revista());
			stmt.setInt(6, situLaboralAdd.getId_categoria());
			stmt.setDate(7, new java.sql.Date(situLaboralAdd.getFecha_ingre().getTime()));
			stmt.setDate(8, null != situLaboralAdd.getFecha_baja() ? (new java.sql.Date(
							situLaboralAdd.getFecha_baja().getTime())) : null);
			stmt.setString(9, user);
			stmt.setString(10, situLaboralAdd.getEscala_salarial());
			if (situLaboralAdd.getMotivoBaja() != null
					&& situLaboralAdd.getMotivoBaja().getId_motivo_baja() != 0) {
				stmt.setInt(11, situLaboralAdd.getMotivoBaja().getId_motivo_baja());
			} else {
				stmt.setNull(11, Types.INTEGER);
			}
			int rs = stmt.executeUpdate();
			if (situLaboralAdd.isBaja_cascada()) {
				
				/*Como queda afuera la situ nueva recientemente agregada y con estado "add" la editamos */
				
				String sqlEdit = "{call edita_situ_laboral(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				stmt1 = con.prepareCall(sqlEdit.toString());
				stmt1.setString(1, situLaboralAdd.getAfiliado().getCuil_titular());
				stmt1.setInt(2, situLaboralAdd.getAfiliado().getInte());
				stmt1.setString(3, situLaboralAdd.getEmpresa().getCuit());
				stmt1.setString(4, situLaboralAdd.getEmpresa().getSucursal());
				stmt1.setInt(5, situLaboralAdd.getId_revista());
				stmt1.setInt(6, situLaboralAdd.getId_categoria());
				stmt1.setDate(7, new java.sql.Date(situLaboralAdd.getFecha_ingre().getTime())); //situLaboralAdd.getViejaFechaIngreso().getTime()
				stmt1.setDate(8, null != situLaboralAdd.getFecha_baja() ? (new java.sql.Date(situLaboralAdd.getFecha_baja().getTime())): null);
				stmt1.setString(9, user);
				stmt1.setInt(10, situLaboralAdd.getMotivoBaja().getId_motivo_baja());
				stmt1.setString(11, situLaboralAdd.getEscala_salarial());
				stmt1.setDate(12, new java.sql.Date(situLaboralAdd.getFecha_ingre().getTime()));
				if (situLaboralAdd.getMotivoBaja().getId_motivo_baja() == 0) {
					stmt1.setNull(10, Types.INTEGER);
				} else {
					stmt1.setInt(10, situLaboralAdd.getMotivoBaja().getId_motivo_baja());
				}
				stmt1.setDate(13, null != situLaboralAdd.getFecha_baja_logica() ? (new java.sql.Date(situLaboralAdd.getFecha_baja_logica().getTime())): null);
				stmt1.executeUpdate();
				/* fin edit*/
				String sqlList = "{call baja_cascada_sin_situ_laboral(?,?,?,?,?)}";
				stmt2 = con.prepareCall(sqlList.toString());
				stmt2.setString(1, situLaboralAdd.getAfiliado().getCuil_titular());
				stmt2.setInt(2, situLaboralAdd.getAfiliado().getInte());
				stmt2.setDate(3, null != situLaboralAdd.getFecha_baja() ? (new java.sql.Date(
								situLaboralAdd.getFecha_baja().getTime())): null);
				stmt2.setInt(4, situLaboralAdd.getMotivoBaja().getId_motivo_baja());
				stmt2.setString(5, user);
				stmt2.executeUpdate();
			}
		} catch (Exception e) {
			_log.debug("Error!", e);
			throw e;
		} finally {
			try {
				ConnectionHelper.cerrar(stmt1);
				ConnectionHelper.cerrar(stmt2);
			} catch (Exception e) {
				_log.debug("error", e);
			}
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void borraSituLaboral(SituacionLaboral situLaboralDelete, User user,
			Connection con) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call borra_situ_laboral(?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, situLaboralDelete.getAfiliado().getCuil_titular());
			stmt.setInt(2, situLaboralDelete.getAfiliado().getInte());
			stmt.setString(3, situLaboralDelete.getEmpresa().getCuit());
			stmt.setString(4, situLaboralDelete.getEmpresa().getSucursal());
			stmt.setDate(5, new java.sql.Date(situLaboralDelete.getFecha_ingre().getTime()));
			stmt.setString(6, user.getScreenName());
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.debug("Error al grabar situacion laboral", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void editaSituLaboral(SituacionLaboral situLaboralUpdate,
			String user, Connection con) throws Exception {
		CallableStatement stmt = null;
		CallableStatement stmt2 = null;
		CallableStatement stmt3 = null;
		try {
			String sql = "{call edita_situ_laboral(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, situLaboralUpdate.getAfiliado().getCuil_titular());
			stmt.setInt(2, situLaboralUpdate.getAfiliado().getInte());
			stmt.setString(3, situLaboralUpdate.getEmpresa().getCuit());
			stmt.setString(4, situLaboralUpdate.getEmpresa().getSucursal());
			stmt.setInt(5, situLaboralUpdate.getId_revista());
			stmt.setInt(6, situLaboralUpdate.getId_categoria());
			stmt.setDate(7, new java.sql.Date(situLaboralUpdate.getViejaFechaIngreso().getTime()));
			stmt.setDate(8, null != situLaboralUpdate.getFecha_baja() ? (new java.sql.Date(situLaboralUpdate.getFecha_baja().getTime())): null);
			stmt.setString(9, user);
			stmt.setInt(10, situLaboralUpdate.getMotivoBaja().getId_motivo_baja());
			stmt.setString(11, situLaboralUpdate.getEscala_salarial());
			stmt.setDate(12, new java.sql.Date(situLaboralUpdate.getFecha_ingre().getTime()));
			if (situLaboralUpdate.getMotivoBaja().getId_motivo_baja() == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, situLaboralUpdate.getMotivoBaja().getId_motivo_baja());
			}
			stmt.setDate(13, null != situLaboralUpdate.getFecha_baja_logica() ? (new java.sql.Date(situLaboralUpdate.getFecha_baja_logica().getTime())): null);
			stmt.executeUpdate();
			if (situLaboralUpdate.isBaja_cascada()) {
				String sqlList = "{call baja_cascada_sin_situ_laboral(?,?,?,?,?)}";
				stmt2 = con.prepareCall(sqlList.toString());
				stmt2.setString(1, situLaboralUpdate.getAfiliado().getCuil_titular());
				stmt2.setInt(2, situLaboralUpdate.getAfiliado().getInte());
				stmt2.setDate(3, null != situLaboralUpdate.getFecha_baja() ? (new java.sql.Date(
								situLaboralUpdate.getFecha_baja().getTime())): null);
				stmt2.setInt(4, situLaboralUpdate.getMotivoBaja().getId_motivo_baja());
				stmt2.setString(5, user);
				stmt2.executeUpdate();
				
//				Inserta ingreso de legajo del afiliado
				String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";	  
				
				stmt2 = con.prepareCall(sqlInsert.toString());
				stmt2.setString(1, situLaboralUpdate.getAfiliado().getCuil_titular());
				stmt2.setInt(2, situLaboralUpdate.getAfiliado().getInte());
				stmt2.setNull(3, Types.INTEGER);
				stmt2.setNull(4, Types.TIMESTAMP); // se actualiza si imprime credenciales
				// baja_cascada
				stmt2.setInt(5, situLaboralUpdate.getMotivoBaja().getId_motivo_baja());					
				stmt2.setTimestamp(6, new java.sql.Timestamp(situLaboralUpdate.getFecha_baja().getTime()) );
				stmt2.setString(7, "baja");
				stmt2.setString(8, user);
				
				stmt2.executeUpdate();
			}
		} catch (Exception e) {
			_log.debug("Error!", e);
			throw e;
		} finally {

			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt);
		}
	}

	public List<SituacionLaboral> buscaSituLaboral(String cuil_titular, int inte, Connection connectionParameter) throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		List<SituacionLaboral> situLaborales = new ArrayList<SituacionLaboral>();
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();	
			}else{
				con=connectionParameter;
			}
			
			String sql = "{call trae_situ_laborales(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);

			ResultSet rs = stmt.executeQuery();
			
			situLaborales = llenaResultSetSituacionLaboral(rs);
			
		} catch (Exception e) {
			_log.debug("Error al grabar situ laboral!", e);
			throw e;
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return situLaborales;
	}

	public List<SituacionLaboral> llenaResultSetSituacionLaboral(ResultSet rs)
			throws Exception {
		List<SituacionLaboral> situLaborales = new ArrayList<SituacionLaboral>();
		int i = 0;
		while (rs.next()) {
			Empresa bp = new Empresa(rs.getString("cuit"),
					rs.getString("sucursal"), rs.getString("razon_social"));
			Afiliado afi = new Afiliado(rs.getString("cuil_titular"),
					rs.getInt("inte"), rs.getString("cuil"),
					rs.getString("nombre"), rs.getString("apellido"));
			afi.setAportante_titular(rs.getInt("aportante_titular"));
			MotivoBaja motBaja = new MotivoBaja(rs.getInt("id_motivo_baja"),
					rs.getString("motivo_baja"));
			SituacionLaboral sl = new SituacionLaboral(afi, bp,
					rs.getDate("fecha_ingreso"), rs.getDate("fecha_baja"),
					rs.getString("revista"), rs.getString("categoria"),
					rs.getInt("id_categoria"), rs.getInt("id_revista"),
					motBaja, rs.getString("escala_salarial"));
			sl.setId(i);
//			_log.debug("Cuil Titular: " + afi.getCuil_titular() + " - SituLaboral: " + sl.getEmpresa().getRazon_soc());
			situLaborales.add(sl);
			i++;
		}
		return situLaborales;
	}

}
