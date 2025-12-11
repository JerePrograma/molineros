package ar.com.ospim.autorizaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.util.ConnectionHelper;

public class NomencladorServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(NomencladorServiceImpl.class);

	public List<Nomenclador> getListaNomencladorMarcaReinLiq(int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador, int marcaReinLiq)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Nomenclador> list = null;
		try {
			String sql = "{call autorizaciones.busca_nomenclador_marca_reinliq(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			
			if (tipoNomenclador>0) {
				stmt.setInt(1, tipoNomenclador);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (null != descripcionNomenclador && descripcionNomenclador.trim().length() > 0) {
				stmt.setString(2, descripcionNomenclador.toUpperCase());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (especialidad>0) {
				stmt.setInt(3, especialidad);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (null != codigoNomenclador && codigoNomenclador.trim().length() > 0) {
				stmt.setString(4, codigoNomenclador);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (null != recuperaSUR) {
				stmt.setBoolean(5, recuperaSUR);
			} else {
				stmt.setNull(5, Types.BOOLEAN);
			}
			if (null != resolucionNomenclador && resolucionNomenclador.trim().length() > 0) {
				stmt.setString(6, resolucionNomenclador);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			if (marcaReinLiq>0) {
				stmt.setInt(7, marcaReinLiq);
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Nomenclador>();
			while (rs.next()) {
				Nomenclador archivo = Nomenclador.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nomenclador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Nomenclador> getListaNomencladorPrestacionesMedicas(int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Nomenclador> list = null;
		try {
			String sql = "{call autorizaciones.busca_nomenclador_prest_med(?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (tipoNomenclador>0) {
				stmt.setInt(1, tipoNomenclador);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (null != descripcionNomenclador && descripcionNomenclador.trim().length() > 0) {
				stmt.setString(2, descripcionNomenclador);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (especialidad>0) {
				stmt.setInt(3, especialidad);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (null != codigoNomenclador && codigoNomenclador.trim().length() > 0) {
				stmt.setString(4, codigoNomenclador);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (null != recuperaSUR) {
				stmt.setBoolean(5, recuperaSUR);
			} else {
				stmt.setNull(5, Types.BOOLEAN);
			}
			if (null != resolucionNomenclador && resolucionNomenclador.trim().length() > 0) {
				stmt.setString(6, resolucionNomenclador);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Nomenclador>();
			while (rs.next()) {
				Nomenclador archivo = Nomenclador.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nomenclador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}	
	
	public List<Nomenclador> getListaNomenclador(int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Nomenclador> list = null;
		try {
			String sql = "{call autorizaciones.busca_nomenclador(?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (tipoNomenclador>0) {
				stmt.setInt(1, tipoNomenclador);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (null != descripcionNomenclador && descripcionNomenclador.trim().length() > 0) {
				stmt.setString(2, descripcionNomenclador);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (especialidad>0) {
				stmt.setInt(3, especialidad);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (null != codigoNomenclador && codigoNomenclador.trim().length() > 0) {
				stmt.setString(4, codigoNomenclador);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (null != recuperaSUR) {
				stmt.setBoolean(5, recuperaSUR);
			} else {
				stmt.setNull(5, Types.BOOLEAN);
			}
			if (null != resolucionNomenclador && resolucionNomenclador.trim().length() > 0) {
				stmt.setString(6, resolucionNomenclador);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Nomenclador>();
			while (rs.next()) {
				Nomenclador archivo = Nomenclador.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nomenclador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public long insertaNomenclador(Nomenclador nomenclador, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.inserta_nomenclador(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, nomenclador.getId_tipo_nomenclador());
			stmt.setString(2, nomenclador.getDescripcion().toUpperCase());
			stmt.setString(3, nomenclador.getCodigo());
			stmt.setInt(4, nomenclador.getId_especialidad());
			stmt.setDouble(5, nomenclador.getImporte());
			stmt.setString(6, nomenclador.getResolucion());
			stmt.setBoolean(7, nomenclador.getRecuperaSUR());	
			stmt.setString(8, screenName);
			stmt.setDouble(9, nomenclador.getCantidadGaleno());
			stmt.setDouble(10, nomenclador.getCantidadGalenoAyudante());
			stmt.setDouble(11, nomenclador.getCantidadGalenoAnestesista());
			stmt.setDouble(12, nomenclador.getValorGaleno());
			stmt.setString(13, nomenclador.getCodigoHospital());
			stmt.setDouble(14, nomenclador.getCantidadAyudantes());
			stmt.setDouble(15, nomenclador.getValorGalenoGastos());
			stmt.setDouble(16, nomenclador.getCantidadGalenoGastos());		
			stmt.setInt(17, nomenclador.getMarcaReintegroLiquidacion() );
			stmt.setDouble(18, nomenclador.getCoeficienteGastos());
			stmt.setDouble(19, nomenclador.getCoeficienteHonorarios());
			stmt.setBoolean(20, nomenclador.getRequiereAutorizacion());
			stmt.setString(21, nomenclador.getObservaciones());
			stmt.setBoolean(22, nomenclador.isSupra());
			stmt.setBoolean(23, nomenclador.isCirugia());
			stmt.setBoolean(24, nomenclador.isEnviarWSTercerizadora());
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar nomenclador", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}
	
	public static Nomenclador buscarNomencladorPorId(
			int id) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		Nomenclador nomenclador = null;
		try {
			String sql = "{ call autorizaciones.busca_nomenclador_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				nomenclador = Nomenclador.getMapping(rs);
			}

		} catch (Exception e) {
			_log.error("error al buscar Nomenclador por Id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return nomenclador;
	}
	
	public static ModalidadAtencion buscarModalidadAtencionPorId(
			int id) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		ModalidadAtencion modalidad = null;
		try {
			String sql = "{ call autorizaciones.busca_modalidad_atencion_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				modalidad = ModalidadAtencion.getMapping(rs);
			}

		} catch (Exception e) {
			_log.error("error al buscar Modalidad de Atencion por Id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return modalidad;
	}
	
	public long insertaModalidadAtencion(int idnomenclador,NomencladorPlan modalidad ,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.inserta_nomenclador_plan(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idnomenclador);
			stmt.setInt(2, modalidad.getPlan().getId() );
			if(modalidad.getAutorizacion()!=null ) {
			   stmt.setInt(3, modalidad.getAutorizacion().getId());
			}else {
			   stmt.setNull(3,Types.INTEGER);	
			}
			stmt.setString(4, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar nomenclador", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}
	
	public long eliminaModalidadAtencion(int idnomenclador,NomencladorPlan modalidad ,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.elimina_nomenclador_plan(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idnomenclador);
			stmt.setInt(2, modalidad.getPlan().getId() );
			stmt.setInt(3, modalidad.getAutorizacion().getId());
			stmt.setString(4, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar nomenclador plan", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}


	public static List<NomencladorPlan> buscarNomencladorPlanPorId(
			int id) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<NomencladorPlan> np = new ArrayList<NomencladorPlan>();
		try {
			String sql = "{ call autorizaciones.busca_nomenclador_plan_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NomencladorPlan nomplan = NomencladorPlan.getMapping(rs);
				np.add(nomplan);
			}

		} catch (Exception e) {
			_log.error("error al buscar Nomenclador por Id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return np;
	}
	
	
	public int updateNomenclador(Nomenclador nomenclador, String accion,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = nomenclador.getId_prestacion();
		try {
			String sql = "{call autorizaciones.update_nomenclador(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, nomenclador.getId_prestacion());
			stmt.setInt(2, nomenclador.getId_tipo_nomenclador());
			stmt.setString(3, nomenclador.getDescripcion().toUpperCase());
			stmt.setString(4, nomenclador.getCodigo());
			stmt.setInt(5, nomenclador.getId_especialidad());
			stmt.setDouble(6, nomenclador.getImporte());
			stmt.setString(7, nomenclador.getResolucion());
			stmt.setBoolean(8, nomenclador.getRecuperaSUR());	
			stmt.setString(9, screenName);
			stmt.setDouble(10, nomenclador.getCantidadGaleno());
			stmt.setDouble(11, nomenclador.getCantidadGalenoAyudante());
			stmt.setDouble(12, nomenclador.getCantidadGalenoAnestesista());
			stmt.setDouble(13, nomenclador.getValorGaleno());
			stmt.setString(14, nomenclador.getCodigoHospital());
			stmt.setDouble(15, nomenclador.getCantidadAyudantes());
			stmt.setDouble(16, nomenclador.getValorGalenoGastos());
			stmt.setDouble(17, nomenclador.getCantidadGalenoGastos());
			stmt.setString(18, accion);		
			stmt.setInt(19, nomenclador.getMarcaReintegroLiquidacion() );
			stmt.setDouble(20, nomenclador.getCoeficienteGastos());
			stmt.setDouble(21, nomenclador.getCoeficienteHonorarios());
			stmt.setBoolean(22, nomenclador.getRequiereAutorizacion());
			stmt.setString(23, nomenclador.getObservaciones());
			stmt.setBoolean(24, nomenclador.isSupra());
			stmt.setBoolean(25, nomenclador.isCirugia());
			stmt.setBoolean(26, nomenclador.isEnviarWSTercerizadora());
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar nomenclador", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}

	public long eliminaNomenclador(int idNomenclador,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			
			Nomenclador nomenclador= buscarNomencladorPorId(idNomenclador);
			
			String sql = "{call autorizaciones.elimina_nomenclador(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, nomenclador.getId_prestacion());
			stmt.setInt(2, nomenclador.getId_tipo_nomenclador());
			stmt.setString(3, nomenclador.getDescripcion());
			stmt.setString(4, nomenclador.getCodigo());
			stmt.setInt(5, nomenclador.getId_especialidad());
			stmt.setDouble(6, nomenclador.getImporte());
			stmt.setString(7, nomenclador.getResolucion());
			stmt.setBoolean(8, nomenclador.getRecuperaSUR());	
			stmt.setString(9, screenName);
			stmt.setDouble(10, nomenclador.getCantidadGaleno());
			stmt.setDouble(11, nomenclador.getCantidadGalenoAyudante());
			stmt.setDouble(12, nomenclador.getCantidadGalenoAnestesista());
			stmt.setDouble(13, nomenclador.getValorGaleno());
			stmt.setString(14, nomenclador.getCodigoHospital());
			stmt.setDouble(15, nomenclador.getCantidadAyudantes());
			stmt.setDouble(16, nomenclador.getValorGalenoGastos());
			stmt.setDouble(17, nomenclador.getCantidadGalenoGastos());
			
			stmt.setInt(18, nomenclador.getMarcaReintegroLiquidacion() );
			stmt.setDouble(19, nomenclador.getCoeficienteGastos());
			stmt.setDouble(20, nomenclador.getCoeficienteHonorarios());
			stmt.setBoolean(21, nomenclador.getRequiereAutorizacion());
			stmt.setString(22, nomenclador.getObservaciones());
			
			ResultSet rs = stmt.executeQuery();
			
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar nomenclador", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}

	public long recuperaNomenclador(int idNomenclador,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			
			Nomenclador nomenclador= buscarNomencladorPorId(idNomenclador);
			
			String sql = "{call autorizaciones.recupera_nomenclador(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, nomenclador.getId_prestacion());
			stmt.setInt(2, nomenclador.getId_tipo_nomenclador());
			stmt.setString(3, nomenclador.getDescripcion().toUpperCase());
			stmt.setString(4, nomenclador.getCodigo());
			stmt.setInt(5, nomenclador.getId_especialidad());
			stmt.setDouble(6, nomenclador.getImporte());
			stmt.setString(7, nomenclador.getResolucion());
			stmt.setBoolean(8, nomenclador.getRecuperaSUR());	
			stmt.setString(9, screenName);
			stmt.setDouble(10, nomenclador.getCantidadGaleno());
			stmt.setDouble(11, nomenclador.getCantidadGalenoAyudante());
			stmt.setDouble(12, nomenclador.getCantidadGalenoAnestesista());
			stmt.setDouble(13, nomenclador.getValorGaleno());
			stmt.setString(14, nomenclador.getCodigoHospital());
			stmt.setDouble(15, nomenclador.getCantidadAyudantes());
			stmt.setDouble(16, nomenclador.getValorGalenoGastos());
			stmt.setDouble(17, nomenclador.getCantidadGalenoGastos());
			
			stmt.setInt(18, nomenclador.getMarcaReintegroLiquidacion() );
			stmt.setDouble(19, nomenclador.getCoeficienteGastos());
			stmt.setDouble(20, nomenclador.getCoeficienteHonorarios());
			stmt.setBoolean(21, nomenclador.getRequiereAutorizacion());
			stmt.setString(22, nomenclador.getObservaciones());
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al recuperar nomenclador", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}

	public static boolean existeNomencladorPorTipoCodigo(
			int tipo,String codigo) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		Boolean nomenclador = false;
		try {
			String sql = "{ call autorizaciones.existe_nomenclador_tipo_codigo(?,?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, tipo);
			stmt.setString(2, codigo);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				nomenclador = rs.getBoolean(1);
			}

		} catch (Exception e) {
			_log.error("error al buscar Nomenclador por Tipo y Codigo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return nomenclador;
	}
	
	
	public long insertaNomencladorConceptos(PrestacionConcepto prestacionConcepto, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
			con.setAutoCommit(false);
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.insertar_nomenclador_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, prestacionConcepto.getPrestacion().getCodigo());
			stmt.setString(2, prestacionConcepto.getPrestacion().getDescripcion());
			stmt.setInt(3, prestacionConcepto.getHonorariosAmbulatorio().getId());
			stmt.setInt(4, prestacionConcepto.getHonorariosInternacion().getId() );
			stmt.setInt(5, prestacionConcepto.getGastosAmbulatorio().getId());
			stmt.setInt(6, prestacionConcepto.getGastosInternacion().getId());
			stmt.setBigDecimal(7, prestacionConcepto.getCoeficienteGastos());
			stmt.setBigDecimal(8, prestacionConcepto.getCoeficienteHonorarios());
			stmt.setDate(9,  new java.sql.Date(prestacionConcepto.getValidoDesdeHonorariosAmbulatorio().getTime()));
			stmt.setDate(10, new java.sql.Date(prestacionConcepto.getValidoHastaHonorariosAmbulatorio().getTime()));
			stmt.setString(11, screenName);
			stmt.setInt(12, prestacionConcepto.getPrestacion().getMarca_rein_liq());
			stmt.setInt(13, prestacionConcepto.getIdTipoNomenclador());
			stmt.setInt(14, prestacionConcepto.getPrestacion().getId_prestacion());
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			
		} catch (SQLException e) {
			_log.error("Error al insertar nomenclador concepto", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}

	
	public long updateNomencladorConceptos(PrestacionConcepto prestacionConcepto, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.insertar_nomenclador_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, prestacionConcepto.getPrestacion().getCodigo());
			stmt.setString(2, prestacionConcepto.getPrestacion().getDescripcion());
			stmt.setInt(3, prestacionConcepto.getHonorariosAmbulatorio().getId());
			stmt.setInt(4, prestacionConcepto.getHonorariosInternacion().getId() );
			stmt.setInt(5, prestacionConcepto.getGastosAmbulatorio().getId());
			stmt.setInt(6, prestacionConcepto.getGastosInternacion().getId());
			stmt.setBigDecimal(7, prestacionConcepto.getCoeficienteGastos());
			stmt.setBigDecimal(8, prestacionConcepto.getCoeficienteHonorarios());
			stmt.setDate(9,  new java.sql.Date(prestacionConcepto.getValidoDesdeHonorariosAmbulatorio().getTime()));
			stmt.setDate(10, new java.sql.Date(prestacionConcepto.getValidoHastaHonorariosAmbulatorio().getTime()));
			stmt.setString(11, screenName);
			stmt.setInt(12, prestacionConcepto.getPrestacion().getMarca_rein_liq());
			stmt.setInt(13, prestacionConcepto.getIdTipoNomenclador());
			stmt.setInt(14, prestacionConcepto.getPrestacion().getId_prestacion());
			
//			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			
		} catch (SQLException e) {
			_log.error("Error al insertar nomenclador concepto", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}
	
	public PrestacionConcepto getPrestacionesConceptosActualPorIdPrestacion(
			int id, Date desdeEjercicio, Date hastaEjercicio) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.buscar_nomenclador_conceptos_por_id_prestacion(?, ?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setDate(2, new java.sql.Date(desdeEjercicio.getTime()));
			stmt.setDate(3, new java.sql.Date(hastaEjercicio.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return PrestacionConcepto.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error("Error al buscar_nomenclador_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}
	
	public void updateNomencladorConcepto(Connection connectionParameter, int id_prestacion,
			int idNomencladorConcepto, int cocneptoId, Date desde, Date hasta,
			int tipo, String user) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_nomenclador_concepto(?, ?, ?, ?, ?, ?, ?)}";
			_log.debug("obteniendo conexion");
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestacion);
			stmt.setInt(2, idNomencladorConcepto);
			stmt.setInt(3, cocneptoId);
			stmt.setDate(4, new java.sql.Date(desde.getTime()));
			stmt.setDate(5, new java.sql.Date(hasta.getTime()));
			stmt.setInt(6, tipo);
			stmt.setString(7, user);
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al actualizar prestacion concepto", e);
			throw e;
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
				
		}
	}
	
	public void reemplazarNomencladorConcepto(Connection connectionParameter,
			int id_prestacion, Date desdeNuevo, Date desdeOriginal,
			Date hastaNuevo, Date hastaOriginal,
			int idNomencladorConceptoAReemplazar, int idNuevoConcepto,
			Calendar desdeEjercicioActual, Calendar infinito, String user,
			int tipo_id) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call reemplazar_prestacion_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			_log.debug("obteniendo conexion");
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestacion);
			stmt.setDate(2, new java.sql.Date(desdeOriginal.getTime()));
			stmt.setDate(3, new java.sql.Date(hastaOriginal.getTime()));
			stmt.setInt(4, idNomencladorConceptoAReemplazar);
			stmt.setInt(5, idNuevoConcepto);
			stmt.setDate(6, new java.sql.Date(desdeNuevo.getTime()));
			stmt.setDate(7, new java.sql.Date(hastaNuevo.getTime()));
			stmt.setString(8, user);
			stmt.setInt(9, tipo_id);
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al eliminar reemplazar_prestacion_concepto", e);
			throw e;
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}

	}
	
	
	public List<Medicamento> getBusquedaMedicamentos(int troquel, int registro,
			String nombre, String presentacion, String laboratorio, String cod_barras) {
		Connection con = null;
		CallableStatement stmt=null;
		List<Medicamento> listaMedicamentos= null;
		try {
			String sql = "{call autorizaciones.buscar_medicamentos(?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel!=0){
				stmt.setInt(1, troquel);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			if(registro!=0){
				stmt.setInt(2, registro);	
			}else{
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, (null!=nombre&&nombre.trim().equals(""))?null:nombre);
			stmt.setString(4, (null!=presentacion&&presentacion.trim().equals(""))?null:presentacion);
			stmt.setString(5, (null!=laboratorio&&laboratorio.trim().equals(""))?null:laboratorio);
			stmt.setString(6, (null!=cod_barras&&cod_barras.trim().equals(""))?null:cod_barras);

			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<Medicamento>();
			while (rs.next()) {
				Medicamento bp = new Medicamento(rs.getInt("id_medicamento"),rs.getInt("troquel"), rs.getInt("nro_registro"), rs.getString("nombre"),rs.getString("presentacion"), 
								 rs.getString("laboratorio"), rs.getString("accion"), rs.getString("droga"), rs.getBigDecimal("precio_unitario"),BigDecimal.valueOf(0) ,
								 BigDecimal.valueOf(0),rs.getBigDecimal("porc_sssalud"),rs.getBigDecimal("pmoe"), rs.getString("cod_barras"));
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}
	
	public static Nomenclador getEstudiosRequeridosPorId(int id) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		Nomenclador nomenclador = new Nomenclador();
		nomenclador.setRequiereHistoriaClinica(false);
		nomenclador.setRequiereAnatomiaPatologica(false);
		nomenclador.setRequiereEstudiosComplementarios(false);
		nomenclador.setRequiereBiopsia(false);
		try {
			String sql = "{ call autorizaciones.busca_estudios_requeridos_nomenclador_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				nomenclador.setRequiereHistoriaClinica(rs.getBoolean("historia_clinica"));
				nomenclador.setRequiereAnatomiaPatologica(rs.getBoolean("anatomia_patologica"));
				nomenclador.setRequiereEstudiosComplementarios(rs.getBoolean("estudios_complementarios"));
				nomenclador.setRequiereBiopsia(rs.getBoolean("biopsia"));
			}

		} catch (Exception e) {
			_log.error("error al buscar Estudios Requeridos Nomenclador por Id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return nomenclador;
	}
	
	public List<Nomenclador> getListaNomencladorPreautorizaciones(int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Nomenclador> list = null;
		try {
			String sql = "{call autorizaciones.busca_nomenclador_preautorizaciones(?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (tipoNomenclador>0) {
				stmt.setInt(1, tipoNomenclador);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (null != descripcionNomenclador && descripcionNomenclador.trim().length() > 0) {
				stmt.setString(2, descripcionNomenclador);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (especialidad>0) {
				stmt.setInt(3, especialidad);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (null != codigoNomenclador && codigoNomenclador.trim().length() > 0) {
				stmt.setString(4, codigoNomenclador);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (null != recuperaSUR) {
				stmt.setBoolean(5, recuperaSUR);
			} else {
				stmt.setNull(5, Types.BOOLEAN);
			}
			if (null != resolucionNomenclador && resolucionNomenclador.trim().length() > 0) {
				stmt.setString(6, resolucionNomenclador);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Nomenclador>();
			while (rs.next()) {
				Nomenclador archivo = Nomenclador.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nomenclador Preautorizaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public long insertaTopesReintegros(int idnomenclador,NomencladorPlan modalidad ,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.inserta_nomenclador_plan_topes_reintegros(?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idnomenclador);
			stmt.setInt(2, modalidad.getPlan().getId() );
			stmt.setDouble(3, modalidad.getTopeReintegro());
			stmt.setDate(4, new java.sql.Date(modalidad.getVigencia_desde().getTime()));
			if(modalidad.getVigencia_hasta()!=null) {
				stmt.setDate(5, new java.sql.Date(modalidad.getVigencia_hasta().getTime()));
			}else {
			    stmt.setNull(5, Types.DATE);  	
			}
			
			stmt.setString(6, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar nomenclador plan topes reintegros", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}
	
	public List<NomencladorPlan> buscarNomencladorPlanTopesReintegrosPorId(
			Integer id) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<NomencladorPlan> np = new ArrayList<NomencladorPlan>();
		try {
			String sql = "{ call autorizaciones.busca_nomenclador_plan_topes_reintegros_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NomencladorPlan nomplan = NomencladorPlan.getMappingTopes(rs);
				np.add(nomplan);
			}

		} catch (Exception e) {
			_log.error("error al buscar Nomenclador Topes Reintegros por Id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return np;
	}
	
	
	public long updateTopesReintegros(int idnomenclador,NomencladorPlan modalidad ,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.update_nomenclador_plan_topes_reintegros(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, modalidad.getId());
			stmt.setDouble(2, modalidad.getTopeReintegro());
			stmt.setDate(3, new java.sql.Date(modalidad.getVigencia_desde().getTime()));
			if(modalidad.getVigencia_hasta()!=null) {
				stmt.setDate(4, new java.sql.Date(modalidad.getVigencia_hasta().getTime()));
			}else {
			    stmt.setNull(4, Types.DATE);  	
			}
			
			stmt.setString(5, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al updatear nomenclador plan topes reintegros", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}

	public long eliminaTopesReintegros(int idnomenclador,NomencladorPlan modalidad ,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		int id_nomenclador = 0;
		try {
			String sql = "{call autorizaciones.delete_nomenclador_plan_topes_reintegros(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, modalidad.getId());
			stmt.setString(2, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_nomenclador = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete nomenclador plan topes reintegros", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_nomenclador;
	}

	public NomencladorPlan buscarNomencladorPlanTopesReintegros(
			Integer id, Integer idPlan, Date fecha) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		NomencladorPlan np = new NomencladorPlan();
		try {
			String sql = "{ call autorizaciones.busca_nomenclador_plan_topes_reintegros(?,?,?) }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);
			stmt.setInt(2, idPlan);
			stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				np = NomencladorPlan.getMappingTopes(rs);
			}

		} catch (Exception e) {
			_log.error("error al buscar Nomenclador Topes Reintegros ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return np;
	}

	
}
