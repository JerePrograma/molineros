package ar.com.ospim.procesaArchivos.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import com.liferay.portal.model.User;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmacia.beans.Vademecumreporte;
import ar.com.ospim.farmaciaOspim.beans.BusquedaVademecumFiltro;
import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoListadoSSSalud;
import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoManualDat;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleListadoSSSalud;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleManualDat;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ProcesaArchivosFarmaciaServiceImpl {
	private static final int BATCH_UPDATES = 250;

	private static Log logger = LogFactoryUtil
			.getLog(ProcesaArchivosFarmaciaServiceImpl.class);

	private Connection getConnection() {
		return ConnectionHelper.getReportesOspimConnection();
	}
	
	public List<Vademecum> getVademecum() throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ArrayList<Vademecum> list = new ArrayList<Vademecum>();
		
		try{
			con = getConnection();
			String sql = "{call reporte_vademecum(false,false,false,false,false)}";
			stmt = con.prepareCall(sql.toString());
			stmt.executeQuery();	
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Vademecum vade = new Vademecum(rs);
				list.add(vade);
			}
		}catch (Exception e) {
			logger.error(e);
			throw new SQLException();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return list;
	}
	
	public List<Vademecum> getVademecum( BusquedaVademecumFiltro   filtroBusquedaVade) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ArrayList<Vademecum> list = new ArrayList<Vademecum>();
		
		try{
			con = getConnection();
			
			String sql = "{call reporte_vademecum(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setBoolean(1, filtroBusquedaVade.isPmiMadre());
			stmt.setBoolean(2, filtroBusquedaVade.isPmiHijo());				
			stmt.setBoolean(3, filtroBusquedaVade.isAco());
			stmt.setBoolean(4, filtroBusquedaVade.isVadeGral());
			stmt.setBoolean(5, filtroBusquedaVade.isPadronMolineros());
			stmt.executeQuery();	
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Vademecum vade = new Vademecum(rs);
				list.add(vade);
			}
		}catch (Exception e) {
			logger.error(e);
			throw new SQLException();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return list;
	}
	
	public List<Vademecumreporte> getVademecumAltasBajas() throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ArrayList<Vademecumreporte> list = new ArrayList<Vademecumreporte>();
		
		try{
			con = getConnection();
			Vademecumreporte vademecum = null ;
			String sql = "{call reporte_vademecum_altas_bajas_bajasManualDat_Genericos()}";
			stmt = con.prepareCall(sql.toString());
			stmt.executeQuery();	
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				vademecum = Vademecum.getMapping(rs, "vade_");				
				list.add(vademecum );
			}
		}catch (Exception e) {
			logger.error(e);
			throw new SQLException();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return list;
	}
	
	public void actualizaVademecum(Date periodoArchivo , User user) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		
		try{
			con = getConnection();
				
			String sql = "{call actualiza_vademecum_medicamentos(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(periodoArchivo.getTime()));
			stmt.setString(2, user.getScreenName());
			stmt.executeQuery();	
		}catch (Exception e) {
			logger.error(e);
			throw new SQLException();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}		
	}
	
	public int grabaArchivo(ArchivoListadoSSSalud archivo) throws SQLException {
		int result=0;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("archivo Medicamentos PMO SSS: comienzo a grabar");
			con = getConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalle() != null) {
				stmt=con.prepareStatement("delete from listadosssalud_temp");
				stmt.execute();
				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO listadosssalud_temp(id, registro, atc, generico, nombre, presentacion, pvp, acargos, acargoafil, laboratorio, " +
							"cober, grupoter, observaciones)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetalleListadoSSSalud det = archivo.getDetalle().get(i);
						stmt.setLong(1, det.getId());
						stmt.setInt(2, det.getRegistro());
						stmt.setString(3, det.getAtc());
						stmt.setString(4, det.getGenerico());
						stmt.setString(5, det.getNombre());
						stmt.setString(6, det.getPresentacion());
						stmt.setBigDecimal(7, det.getPvp());
						stmt.setBigDecimal(8, det.getAcargoos());
						stmt.setBigDecimal(9, det.getAcargoafil());
						stmt.setString(10, det.getLaboratorio());
						stmt.setBigDecimal(11, det.getCober());
						stmt.setInt(12, det.getGrupoter());
						stmt.setString(13, det.getObservaciones());						
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();

				}		
			}
			con.commit();
			logger.debug("archivo LISTADO SSS: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo LISTADO SSS");
			logger.error(e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public int grabaArchivo(ArchivoManualDat archivo) throws SQLException {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("archivo ManualDat: comienzo a grabar");
			con = getConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalle() != null) {
				int cantGrabada = 0;
				stmt=con.prepareStatement("delete from medicamentos_temp");
				stmt.execute();
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO medicamentos_temp(troquel, nombre, presentacion, monto_ioma, norma_ioma, cober_ioma, laboratorio, precio, fecha_vig, " +
							"controlado, importado, tipo_venta, iva, cod_dto_pami, cod_lab, nro_registro, baja, cod_barra, unidades, tamanio, " +
							"heladera, sifar, baja_especial)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetalleManualDat det = archivo.getDetalle().get(i);
						stmt.setLong(1, det.getTroquel());
						stmt.setString(2, det.getNombre());
						stmt.setString(3, det.getPresentacion());
						stmt.setBigDecimal(4, det.getMonto_ioma());
						stmt.setString(5, det.getNorma_ioma());
						stmt.setString(6, det.getCober_ioma());
						stmt.setString(7, det.getLaboratorio());
						stmt.setBigDecimal(8, det.getPrecio());
						stmt.setDate(9, new java.sql.Date(det.getFecha_vig()
								.getTime()));
						stmt.setString(10, det.getControlado());
						stmt.setString(11, det.getImportado());
						stmt.setString(12, det.getTipo_venta());
						stmt.setString(13, det.getIva());
						stmt.setString(14, det.getCod_dto_pami());
						stmt.setInt(15, det.getCod_lab());
						stmt.setInt(16, det.getNro_registro());
						stmt.setString(17, det.getBaja());
						stmt.setString(18, det.getCod_barra());
						stmt.setInt(19, det.getUnidades());
						stmt.setString(20, det.getTamanio());
						stmt.setString(21, det.getHeladera());
						stmt.setString(22, det.getSifar());
						stmt.setString(23, det.getBaja_especial());

						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();

				}		
			}
			con.commit();
			logger.debug("archivo MANUAL DAT: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo MANUAL DAT");
			logger.error(e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	private Connection getConnectionFromJavaApplication() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://10.1.1.28:5432/devmolineros",
					"postgres", "barracud4");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
