import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.objectweb.asm.Type;
import org.postgresql.util.PSQLException;

import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.util.ConnectionHelper;



public final class InsertarItemsAmtima {

	public static void main(String... aArgs) throws IOException {
		System.out.println(new Date());
		File folderAProcesar = new File("/home/martin/Desktop/asd");
		File[] filesList = folderAProcesar.listFiles();

		for (int i = 0; i < filesList.length; i++) {
			if (filesList[i].isFile()) {
				System.out.println(filesList[i].getAbsolutePath());
				BufferedReader reader = new BufferedReader(new FileReader(
						filesList[i].getAbsolutePath()));
				try {
					String line = null;
					ArrayList<ItemOrdenPago> list = new ArrayList<ItemOrdenPago>();
					while ((line = reader.readLine()) != null) {
						if (line.trim().length() > 0) {
							ItemOrdenPago item = new ItemOrdenPago(line);
							list.add(item);
						}
					}
					insertarRegisstros(list);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				} finally {
					reader.close();
				}
			} else if (filesList[i].isDirectory()) {
			}
		}
		System.out.println(new Date());
	}

	private static void insertarRegisstros(ArrayList<ItemOrdenPago> list)
			throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			if (list != null) {
				int cantGrabada = 0;
				while (cantGrabada < list.size()) {
					int falta = list.size() - cantGrabada;
					int hta = 0;
					if (falta > 300) {
						hta = cantGrabada + 300;
					} else {
						hta = list.size();
					}
					String sql = "INSERT INTO liquidacion_farmacia_amtima(" +
							" fecha, periodo, orden_pago_amtima_id, nro_liquidacion, nro_prestador, prestador,  nro_farmacia, farmacia, id_ospim, id_uoma, id_amtima," +
							" inte, nombre_apellido, nro_recetario, nro_troquel, medicamento, cantidad, pvp, total_ospim," +
							"total_amtima, debito, dif_ospim, dif_amtima,porcentaje_ospim,porcentaje_amtima, pmi," +
							"alta_fecha, alta_usr, modi_fecha, modi_usr )" +
							" VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, " +
							" ?, ?, ?, ?, ?, ? ,LOCALTIMESTAMP,'admin', LOCALTIMESTAMP, 'admin');";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						ItemOrdenPago det = list.get(i);
						stmt.setDate(1, new java.sql.Date(det.getFecha().getTime()));
						stmt.setDate(2, new java.sql.Date(det.getPeriodo().getTime()));
						stmt.setInt(3, 17351);
						stmt.setInt(4, det.getNroLiquidacion());
						stmt.setString(5, det.getCodigoPrestador());
						stmt.setString(6, det.getPrestador());
						stmt.setInt(7, det.getNroFarmacia());
						stmt.setString(8, det.getFarmacia());
								
						if (det.getAfiliado().getId_ospim() == 0) {
							stmt.setNull(9, Type.INT);
						} else {
							stmt.setInt(9, det.getAfiliado().getId_ospim());
						}
						if (det.getAfiliado().getId_amtima() == 0) {
							stmt.setNull(10, Type.INT);
						} else {
							stmt.setInt(10, det.getAfiliado().getId_amtima());
						}
						if (det.getAfiliado().getId_uoma() == 0) {
							stmt.setNull(11, Type.INT);
						} else {
							stmt.setInt(11, det.getAfiliado().getId_uoma());
						}
						stmt.setInt(12, det.getAfiliado().getInte());
						stmt.setString(13, det.getAfiliado().getNombre());
						stmt.setString(14,  det.getNroRecetario());
						stmt.setString(15, det.getTroquel());
						stmt.setString(16, det.getMedicamento());
						stmt.setInt(17, det.getCantidad());
						stmt.setBigDecimal(18, det.getPvp());
						stmt.setBigDecimal(19, det.getTotalOspim());
						stmt.setBigDecimal(20, det.getTotalAmtima());
						stmt.setString(21, det.getDebito());
						stmt.setBigDecimal(22,  det.getDifOspim());
						stmt.setBigDecimal(23,  det.getDifAmtima());
						if (det.getPorcentajeOSPIM() == null) {
							stmt.setNull(24, Type.DOUBLE);
						} else {
							stmt.setDouble(24, det.getPorcentajeOSPIM());
						}
						if ( det.getPorcentajeAmtima() == null) {
							stmt.setNull(25, Type.DOUBLE);
						} else {
							stmt.setDouble(25,  det.getPorcentajeAmtima());
						}
						stmt.setString(26, det.getPmi());
						// nombre
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}
			con.commit();
		} catch (PSQLException e) {
			System.out.println(e);
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	private static Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://10.1.1.28:5432/devmolineros",
					"postgres", "barracud4");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
