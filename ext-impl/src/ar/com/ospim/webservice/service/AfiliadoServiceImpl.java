package ar.com.ospim.webservice.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AfiliadoServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(AfiliadoServiceImpl.class);

	private static AfiliadoServiceImpl instance = null;

	public static AfiliadoServiceImpl getInstance() {
		if (null == instance) {
			instance = new AfiliadoServiceImpl();
		}
		return instance;
	}
	
	public static List<AfiliadoOpe> getTodasNovedadesPadron(){
		
		Connection con = null;
		CallableStatement stmt = null;
		List<AfiliadoOpe> afiliados = new ArrayList<AfiliadoOpe>();
		AfiliadoOpe afi = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
//			con = ConnectionHelper.getConnectionFromJavaApplication() ; // solo para desarrollo
			String sql = "{call informes.lista_novedades_ws() }";
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				
//				afi = getMapping2(rs);
				afi = AfiliadoOpe.getMapping3(rs);
				
				afiliados.add(afi);
			}
		} catch (SQLException e) {
			_log.error("Error SQLException en WS Prevención ", e);
		} catch (Exception e) {
			_log.error("Error Exception en WS Prevención ", e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return afiliados;

	}
	
	public static void updateNovedadesResponse(Integer tipoOper , String cuil_titular, Integer inte, Integer id_transaction, String message_code, String message_description){
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
//			con = ConnectionHelper.getConnectionFromJavaApplication() ;
			con = ConnectionHelper.getConnection() ;
			String sql = "{call informes.actualiza_novedades_ws_omint(?, ?, ?, ?, ?, ?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, tipoOper);
			stmt.setString(2, cuil_titular.replace("-",""));
			stmt.setInt(3, inte);
			stmt.setInt(4, id_transaction);
			stmt.setString(5, message_code);
			stmt.setString(6, message_description);
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

	}
	
	public static void updateNovedadesResponse(Integer tipoOper , String cuil_titular, Integer inte, Integer id_transaction, String message_description){
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
//			con = ConnectionHelper.getConnectionFromJavaApplication() ;
			con = ConnectionHelper.getConnection() ;
			String sql = "{call informes.actualiza_novedades_ws(?, ?, ?, ?, ?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, tipoOper);
//			stmt.setString(2, cuil_titular);
			stmt.setString(2, cuil_titular.replace("-",""));
			stmt.setInt(3, inte);
			stmt.setInt(4, id_transaction);
			if(message_description == null){
				stmt.setNull(5, Types.VARCHAR);
			}else{
				stmt.setString(5, message_description);
			}

			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

	}

	private static Afiliado getMapping(ResultSet rs) throws SQLException{
		
		Afiliado af = new Afiliado();
		af.setId_ospim(rs.getInt("id_ospim"));
		af.setId_amtima(rs.getInt("id_amtima"));
		af.setId_uoma(rs.getInt("id_uoma"));
		String[] secc = rs.getString("seccional").split("-");
		af.setSeccional(new Seccional(Integer.parseInt(secc[0].trim()),secc[1].toString()));
		af.setId_tercerizadora(rs.getString("id_tercerizadora"));
		af.setCuil_titular(getCuilcGuiones(rs.getString("cuil_titular")));
		af.setCuil(getCuilcGuiones(rs.getString("cuil")) );
		af.setInte(rs.getInt("inte"));
		af.setParentesco(rs.getString("parentesco"));
		af.setApellido(rs.getString("apellido"));
		af.setNombre(rs.getString("nombre"));
		af.setDocumento_tipo(rs.getString("documento_tipo"));
		af.setDocu_numero(rs.getString("docu_numero"));
		af.setNaci_fecha(rs.getDate("naci_fecha"));
		af.setSexo(rs.getString("sexo"));
		af.setId_civil_esta(rs.getInt("id_civil_esta"));
		af.setNacionalidad(rs.getInt("id_nacionalidad"));
		Domicilio dom = new Domicilio();
		dom.setProvincia(new Provincia(0, rs.getString("provincia")));
		dom.setLocalidad(new Localidad(0, rs.getString("localidad")));
		dom.setPostal_codi(rs.getString("postal_codi"));
		dom.setCalle(rs.getString("calle"));
		dom.setNumero(rs.getString("numero"));
		dom.setPiso(rs.getString("piso"));
		dom.setDepto(rs.getString("depto"));
		dom.setTelefono(rs.getString("telefono"));
		Domicilio[] domicilios = new Domicilio[1];
		domicilios[0] = dom;
		af.setDomicilios(domicilios);
		af.setIngre_fecha(rs.getDate("ingre_fecha"));
		af.setBaja_fecha(rs.getDate("baja_fecha"));
		af.setCuit(rs.getString("cuit"));
		af.setRazonSoc(rs.getString("razon_soc"));
		af.setUltimo_plan(new Plan(rs.getString("plan_omint")));
		af.setId_categoria(rs.getInt("id_categoria"));
		af.setFPP(rs.getDate("FPP")); 
		af.setDiscapacitado(rs.getString("discapacitado"));
		
		return af;
	}
	
	private static AfiliadoOpe getMapping2(ResultSet rs) throws SQLException{
		
		AfiliadoOpe af = new AfiliadoOpe();
		af.setId_ospim(rs.getInt("id_ospim"));
		af.setId_amtima(rs.getInt("id_amtima"));
		af.setId_uoma(rs.getInt("id_uoma"));
		String[] secc = rs.getString("seccional").split("-");
		af.setSeccional(new Seccional(Integer.parseInt(secc[0].trim()),secc[1].toString()));
		af.setId_tercerizadora(rs.getString("id_tercerizadora"));
		af.setCuil_titular(getCuilcGuiones(rs.getString("cuil_titular")));
		af.setCuil(getCuilcGuiones(rs.getString("cuil")) );
		af.setInte(rs.getInt("inte"));
		af.setParentesco(rs.getString("parentesco"));
		af.setId_parentesco(rs.getInt("id_parentesco_sss"));
		af.setApellido(rs.getString("apellido"));
		af.setNombre(rs.getString("nombre"));
		af.setDocumento_tipo(rs.getString("documento_tipo"));
		af.setDocu_numero(rs.getString("docu_numero"));
		af.setNaci_fecha(rs.getDate("naci_fecha"));
		af.setSexo(rs.getString("sexo"));
		af.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
		af.setNacionalidad(rs.getInt("id_nacionalidad"));
		Domicilio dom = new Domicilio();
		dom.setProvincia(new Provincia(0, rs.getString("provincia")));
		dom.setLocalidad(new Localidad(0, rs.getString("localidad")));
		dom.setPostal_codi(rs.getString("postal_codi"));
		dom.setCalle(rs.getString("calle"));
		dom.setNumero(rs.getString("numero"));
		dom.setPiso(rs.getString("piso"));
		dom.setDepto(rs.getString("depto"));
		dom.setTelefono(rs.getString("telefono"));
		Domicilio[] domicilios = new Domicilio[1];
		domicilios[0] = dom;
		af.setDomicilios(domicilios);
		af.setIngre_fecha(rs.getDate("ingre_fecha"));
		af.setBaja_fecha(rs.getDate("baja_fecha"));
		af.setCuit(rs.getString("cuit"));
		af.setRazonSoc(rs.getString("razon_soc"));
		af.setUltimo_plan(new Plan(rs.getString("plan_omint")));
		af.setId_categoria(rs.getInt("id_categoria"));
		af.setFPP(rs.getDate("FPP")); 
		af.setDiscapacitado(rs.getString("discapacitado"));
		af.setOperacion(rs.getInt("operacion"));
		return af;
	}

	private static String getCuilcGuiones(String nroCuil){
		
		return nroCuil.substring(0, 2) + "-" + nroCuil.substring(2, 10) + "-" + nroCuil.substring(10, 11);
	}
	
	public Date getFechaNacAfiliado(String cuil, int inte)throws SystemException{
		Connection con = null;
		CallableStatement stmt = null;
		Date fechaNac= null;
		try {
			String sql = "{call trae_fecha_nac_afiliado(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuil);
			
			stmt.setInt(2, inte);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fechaNac=rs.getDate(1);
			}

		} catch (Exception e) {
			_log.error("Error al intentar validad edad del afiliado para pmi", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return fechaNac;
	}
		
}
