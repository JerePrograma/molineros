package ar.com.ospim.rrhh.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import ar.com.ospim.rrhh.beans.RegistroAcceso;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ProcesaArchivoHorariosServiceImpl {
	private static final int BATCH_UPDATES = 250;

	private static Log logger = LogFactoryUtil
			.getLog(ProcesaArchivoHorariosServiceImpl.class);

	private Connection getConnection() {
		return ConnectionHelper.getReportesOspimConnection();
	}

	// public List<Vademecum> getVademecum() throws SQLException {
	// Connection con = null;
	// con = getConnection();
	// PreparedStatement stmt = null;
	// String sql = "{call reporte_vademecum()}";
	// stmt = con.prepareCall(sql.toString());
	// stmt.executeQuery();
	// ResultSet rs = stmt.executeQuery();
	// ArrayList<Vademecum> list = new ArrayList<Vademecum>();
	// while (rs.next()) {
	// Vademecum deuda = new Vademecum(rs);
	// list.add(deuda);
	// }
	// return list;
	// }

	// public void actualizaVademecum() throws SQLException {
	// Connection con = null;
	// con = getConnection();
	// PreparedStatement stmt = null;
	// String sql = "{call actualiza_vademecum_medicamentos()}";
	// stmt = con.prepareCall(sql.toString());
	// stmt.executeQuery();
	// }

	public int grabaArchivo(List<RegistroAcceso> registroAcceso, int origenEdificio, User user)
			throws SQLException {

		int cantGrabada = 0;
		Connection con = null;
		CallableStatement stmt = null;
		try {
			logger.debug("archivo horarios: comienzo a grabar");
			con = getConnection();
			con.setAutoCommit(false);

			if (registroAcceso != null) {

				while (cantGrabada < registroAcceso.size()) {
					int falta = registroAcceso.size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = registroAcceso.size();
					}

					String sql = "{ call insertar_batch_registro_fichadas(?,?,?,?,?)}";

					stmt = con.prepareCall(sql.toString());

					for (int i = cantGrabada; i < hta; i++) {
						
						RegistroAcceso det = registroAcceso.get(i);
						
						stmt.setInt(1, det.getId_tarjeta_acceso());
						stmt.setTimestamp(2, new java.sql.Timestamp(det.getFecha_registro().getTime()));
						stmt.setString(3, det.getTipo_registro());
//						stmt.setInt(4, det.getPunto_acceso());
						stmt.setInt(4, origenEdificio);
						stmt.setString(5, user.getScreenName());
						
						stmt.addBatch();
						
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}

			logger.debug("archivo Horiarios: detalles listos");
			con.commit();
			logger.debug("archivo Horarios: commiteado");
		} catch (SQLException e) {
			logger.error("Error al insertar archivo horarios, revisar horarios repetidos");
			logger.error(e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cantGrabada;
	}

	public List<RegistroAcceso> buscarLecturasAcceso(Date fechaDesde,
			Date fechaHasta, String id_tarjeta_acceso) {
		Connection con = null;
		CallableStatement stmt = null;
		List<RegistroAcceso> listaAccesos = new ArrayList<RegistroAcceso>();
		try {
			String sql = "{call buscar_lecturas_acceso(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setTimestamp(1, fechaDesde == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_00_00hs(fechaDesde).getTime()));
			stmt.setTimestamp(2, fechaHasta == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_23_59hs(fechaHasta).getTime()));
			stmt.setString(3, id_tarjeta_acceso);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				RegistroAcceso acceso = RegistroAcceso.getMapping(rs, "ra_");
				acceso.setTarjetaAcceso(TarjetaAcceso.getMapping(rs, "ta_"));
				listaAccesos.add(acceso);
			}
		} catch (Exception e) {
			logger.error("Error al traer lecturas de accesos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAccesos;
	}
	
	public List<RegistroAcceso> buscarInformacionUsuario(Date fechaDesde,
			Date fechaHasta, String id_tarjeta_acceso) {
		Connection con = null;
		CallableStatement stmt = null;
		List<RegistroAcceso> listaAccesos = new ArrayList<RegistroAcceso>();
		try {
			String sql = "{call buscar_informacion_usuario(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setTimestamp(1, fechaDesde == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_00_00hs(fechaDesde).getTime()));
			stmt.setTimestamp(2, fechaHasta == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_23_59hs(fechaHasta).getTime()));
			stmt.setString(3, id_tarjeta_acceso);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				RegistroAcceso acceso = RegistroAcceso.getMapping(rs, "ra_");
				acceso.setTarjetaAcceso(TarjetaAcceso.getMapping(rs, "ta_"));
				listaAccesos.add(acceso);
			}
		} catch (Exception e) {
			logger.error("Error al traer lecturas de accesos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAccesos;
	}
	
	public List<RegistroAcceso> buscarInformacionUsuarios(Date fechaDesde,
			Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<RegistroAcceso> listaAccesos = new ArrayList<RegistroAcceso>();
		try {
			String sql = "{call buscar_informacion_usuarios(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setTimestamp(1, fechaDesde == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_00_00hs(fechaDesde).getTime()));
			stmt.setTimestamp(2, fechaHasta == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_23_59hs(fechaHasta).getTime()));
			stmt.setString(3, null);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				RegistroAcceso acceso = RegistroAcceso.getMapping(rs, "ra_");
				acceso.setTarjetaAcceso(TarjetaAcceso.getMapping(rs, "ta_"));
				listaAccesos.add(acceso);
			}
		} catch (Exception e) {
			logger.error("Error al traer lecturas de accesos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAccesos;
	}
	
	public List<RegistroAcceso> buscarControlAcceso(Date fechaDesde, Date fechaHasta) {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<RegistroAcceso> listaAccesos = new ArrayList<RegistroAcceso>();
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call buscar_lecturas_acceso_control(?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setTimestamp(1, fechaDesde == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_00_00hs(fechaDesde).getTime()));
			stmt.setTimestamp(2, fechaHasta == null ? null : new java.sql.Timestamp(
					DateUtils.getMismoDia_23_59hs(fechaHasta).getTime()));

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				RegistroAcceso acceso = RegistroAcceso.getMapping(rs, "ra_");
				acceso.setTarjetaAcceso(TarjetaAcceso.getMapping(rs, "ta_"));
				listaAccesos.add(acceso);
			}
		} catch (Exception e) {
			logger.error("Error al traer control de accesos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAccesos;
	}
	
	public static TreeMap<String,List<TarjetaAcceso>> getAccesoPersonalPorSector() {
		
		Connection con = null;
		CallableStatement stmt = null;
		TarjetaAcceso ta = null;
		List<TarjetaAcceso> listaTarjetas = null;
		TreeMap<String,List<TarjetaAcceso>> resultados = new TreeMap<String,List<TarjetaAcceso>>();
		String entidad, sector, clavePorOrden;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call buscar_sector_empleados_acceso()}";
			
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ta = TarjetaAcceso.getMapping(rs, "ta_");
				
				entidad = ta.getEntidad();
				sector = ta.getSector();
				clavePorOrden = entidad + " / " + sector;
				if(resultados.containsKey(clavePorOrden)){
					listaTarjetas = resultados.get(clavePorOrden);
					listaTarjetas.add(ta);					
				}else{
					listaTarjetas = new ArrayList<TarjetaAcceso>();
					listaTarjetas.add(ta);
					resultados.put(clavePorOrden, listaTarjetas );
				}
				
			}
		} catch (Exception e) {
			logger.error("Error al traer sector empleados accesos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return resultados;
	}
	
	public List<RegistroAcceso> verificaLosDiques() {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<RegistroAcceso> fichadas = new ArrayList<RegistroAcceso>();
		
		RegistroAcceso ra = null;
		TarjetaAcceso ta = null;
		
		
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call registro_verificado_los_diques()}";
			
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				ra = RegistroAcceso.getMapping(rs,"reg_");
				ta = new TarjetaAcceso();
				ta.setApellido(rs.getString("reg_"+"apellido"));
				ta.setNombre(rs.getString("reg_"+"nombre"));
				ra.setTarjetaAcceso(ta);
				
				fichadas.add(ra);
			}
			
		} catch (Exception e) {
			logger.error("Error al traer fichadas a verificar", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return fichadas;
	}
	
	public List<RegistroAcceso> buscarUltimasLecturasAccesoLosDiques() {
		Connection con = null;
		CallableStatement stmt = null;
		List<RegistroAcceso> listaAccesos = new ArrayList<RegistroAcceso>();
		try {
			String sql = "{call buscar_lecturas_acceso_los_diques() }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				RegistroAcceso acceso = RegistroAcceso.getMapping(rs, "ra_");
				acceso.setTarjetaAcceso(TarjetaAcceso.getMapping(rs, "ta_"));
				listaAccesos.add(acceso);
			}
		} catch (Exception e) {
			logger.error("Error al traer lecturas de accesos los diques", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAccesos;
	}
	
	public void corrigeFichadaLosDiques(Integer id, String tipoRegistro, User user) {
		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call corrige_tipo_fichada_los_diques(?, ?, ?) }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, tipoRegistro);
			stmt.setString(3, user.getScreenName());
			
			stmt.executeUpdate();
			
		} catch (Exception e) {
			logger.error("Error al corregir lecturas de accesos los diques", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}
}
