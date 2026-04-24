package ar.com.ospim.farmacia.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoManualDat;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleManualDat;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ProcesaArchivosServiceImpl {
	private static final int BATCH_UPDATES = 250;

	private static Log logger = LogFactoryUtil
			.getLog(ProcesaArchivosServiceImpl.class);


	private Connection getConnection() {
		return ConnectionHelper.getReportesOspimConnection();
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
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO medicamentos_temp(troquel, nombre, presentacion, monto_ioma, norma_ioma, cober_ioma, laboratorio, precio, fecha, "
							+ "controlado, importado, tipo_venta, iva, cod_dto_pami,"
							+ "cod_lab, nro_registro, baja, cod_barra, unidades, tamanio, heladera, sifar, baja_especial )"
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
				String sql2 = "{call procesaManualDat()}";
				PreparedStatement stmt2 = con.prepareCall(sql2);
				stmt2.execute();
			}
			logger.debug("archivo MANUAL DAT: detalles listos");
			con.commit();
			logger.debug("archivo MANUAL DAT: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo MANUAL DAT");
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.fatal("ERROR AL HACER ROLLBACK!", e);
			}
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

	public Map<String, String> obtieneAfiliadosSinCuil() {
		System.out.print("obtieneAfiliados");
		Connection con = null;
		PreparedStatement stmt = null;
		con = getConnectionFromJavaApplication();
		System.out.print("con cnx!");
		String sql = "select docu_numero,sexo from afiliado a where cuil is null and (a.baja_fecha is null or a.baja_fecha>'20101201')";
		Map<String, String> cuils = new HashMap<String, String>();
		try {
			stmt = con.prepareStatement(sql.toString());
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				cuils.put(result.getString("docu_numero"), result
						.getString("sexo"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}
		return cuils;
	}

	public void updateAfiliadosSinCuil(Map<String, String> sinCuil) {
		Connection con = null;
		PreparedStatement stmt = null;
		con = getConnectionFromJavaApplication();

		ArrayList<String> documentos = new ArrayList<String>();
		documentos.addAll(sinCuil.keySet());

		for (String doc : documentos) {

			String sql = "update afiliado a set cuil='"
					+ sinCuil.get(doc)
					+ "' where cuil is null and (a.baja_fecha is null or a.baja_fecha>'20101201') and "
					+ "docu_numero='" + doc + "'";
			System.out.println(sql);

			ConnectionHelper.cerrar(stmt, con);
		}

	}

}
