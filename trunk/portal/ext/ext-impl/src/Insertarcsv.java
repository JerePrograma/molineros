import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.postgresql.util.PSQLException;

import ar.com.ospim.util.ConnectionHelper;

public final class Insertarcsv {

	private static final String SEPARADOR = ",";

	public static void main(String... aArgs) throws IOException {
		System.out.println(new Date());
		File folderAProcesar = new File("C:\\csvs");
		File[] filesList = folderAProcesar.listFiles();

		for (int i = 0; i < filesList.length; i++) {
			if (filesList[i].isFile()) {
				String name = filesList[i].getName().toUpperCase();
				BufferedReader reader = new BufferedReader(new FileReader(
						filesList[i].getAbsolutePath()));
				try {
					String line = null;
					boolean primerLinea = true;
					ArrayList<String[]> list = new ArrayList<String[]>();
					String table = name.split("\\.")[0];
					System.out.println(table);
					String header = null;
					while ((line = reader.readLine()) != null) {
						line = convertirACSVSinComillas(line);
						line += " ";
						if (primerLinea) {
							header = line;
							crearTabla(table, line);
						}
						if (!primerLinea && line.trim().length() > 0) {
							list.add(line.split(SEPARADOR));
						}
						if (list.size() == 50000) {
							insertarRegistros(table, header, list);
							list.clear();
						}
						primerLinea = false;
					}
					if (list.size() > 0) {
						insertarRegistros(table, header, list);
					}
				} catch (BatchUpdateException e) {
					e.printStackTrace();
					e.getNextException().printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
					// proc.moveFile(filesList[i], folderError);
				} finally {
					reader.close();
				}
			} else if (filesList[i].isDirectory()) {
			}
		}
		System.out.println(new Date());
	}

	private static String convertirACSVSinComillas(String line) {
		String nuevo = "";
		boolean comienzo = false;
		for (int i = 0; i < line.length(); i++) {
			char chr = line.charAt(i);
			if (chr == '\"') {
				comienzo = !comienzo;
			}
			if (comienzo && chr == ',') {
				chr = '.';
			}
			if (chr != '\"') {
				nuevo += chr;
			}
		}

		return nuevo;
	}

	private static void crearTabla(String name, String line)
			throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			String sql = "create table " + name + "(";
			String[] cols = line.split(SEPARADOR);
			for (String col : cols) {
				sql += " " + col + " varchar(100),";
			}
			sql = sql.substring(0, sql.length() - 1) + " );";
			stmt = con.prepareStatement(sql.toString());
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	private static void insertarRegistros(String table, String header,
			ArrayList<String[]> list) throws SQLException {
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
					String sql = "INSERT INTO " + table + " ("
							+ header.replaceAll(";", ",") + ") values (";
					for (int j = 0; j < header.split(SEPARADOR).length; j++) {
						sql += "?,";
					}
					sql = sql.substring(0, sql.length() - 1) + ");";

					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						String[] det = list.get(i);
						int nro = 0;
						System.out.println(i);
						for (String val : det) {
							nro++;
							stmt.setString(nro, val.trim().substring(
									0,
									val.trim().length() > 100 ? 100 : val
											.trim().length()));
						}
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
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
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

	/*
	 * ESPECIFICO PARA INSERTAR LOS ARCHIVOS DE LIQUIDACION DE FARMACIAS (SOLO
	 * LOS ITEMS) private static void insertarRegistrosItemsFarmacias(String
	 * table, String header, ArrayList<ItemOrdenPago> list) throws SQLException
	 * { Connection con = null; PreparedStatement stmt = null; try { con =
	 * getConnection(); con.setAutoCommit(false); if (list != null) { int
	 * cantGrabada = 0; while (cantGrabada < list.size()) { int falta =
	 * list.size() - cantGrabada; int hta = 0; if (falta > 300) { hta =
	 * cantGrabada + 300; } else { hta = list.size(); } String sql =
	 * "INSERT INTO liq_temp(" +
	 * "fecha, periodo, nro_liquidacion, nro_prestador, prestador, nro_farmacia,"
	 * + "farmacia, id_ospim, id_amtima, " +
	 * "id_uoma, inte, nombre_apellido, nro_recetario, nro_troquel, medicamento, cantidad,"
	 * + "pvp, total_ospim, total_amtima, debito, dif_ospim, dif_amtima, " +
	 * "porcentaje_ospim, porcentaje_amtima, pmi )" +
	 * "VALUES (?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?," + "?, ?, ?, ?, ?, ?," +
	 * "?, ?, ?, ?, ?, " + "?, ?, ?);";
	 * 
	 * stmt = con.prepareStatement(sql.toString()); for (int i = cantGrabada; i
	 * < hta; i++) { ItemOrdenPago iop = list.get(i); saveItemFarmacia(stmt,
	 * iop.getFecha(), iop.getPeriodo(), iop .getNroLiquidacion(),
	 * iop.getCodigoPrestador(), iop.getPrestador(), iop.getNroFarmacia(), iop
	 * .getFarmacia(), (iop.getAfiliado() .getId_ospim() == 0 ? null : iop
	 * .getAfiliado().getId_ospim()), (iop.getAfiliado().getId_amtima() == 0 ?
	 * null : iop.getAfiliado().getId_amtima()), (iop.getAfiliado().getId_uoma()
	 * == 0 ? null : iop.getAfiliado().getId_uoma()), iop
	 * .getAfiliado().getInte(), iop .getAfiliado().getNombre(), iop
	 * .getNroRecetario(), iop.getTroquel(), iop.getMedicamento(),
	 * iop.getCantidad(), iop .getPvp(), iop.getTotalOspim(), iop
	 * .getTotalAmtima(), iop.getDebito(), iop .getDifOspim(),
	 * iop.getDifAmtima(), iop .getPorcentajeOSPIM(), iop
	 * .getPorcentajeAmtima(), iop.getPmi()); stmt.addBatch(); cantGrabada++; }
	 * stmt.executeBatch(); } } con.commit(); } catch (SQLException e) {
	 * System.out.println(e); try { con.rollback(); } catch (SQLException e1) {
	 * } throw e; } finally { try { con.close(); stmt.close(); } catch
	 * (Exception e) { } } }
	 * 
	 * private static void saveItemFarmacia(PreparedStatement stmt, Date fecha,
	 * Date periodo, Integer nroLiquidacion, String nroPrestador, String
	 * prestador, int nroFarmacia, String farmacia, Integer idOspim, Integer
	 * idAmtima, Integer idUoma, int inte, String nombreApellido, String
	 * nroRecetario, String troquel, String medicamento, Integer cantidad,
	 * BigDecimal pvp, BigDecimal totalOspim, BigDecimal totalAmtima, String
	 * debito, BigDecimal difOspim, BigDecimal difAmtima, Double
	 * porcentajeOspim, Double porcentajeAmtima, String pmi) throws SQLException
	 * {
	 * 
	 * stmt.setDate(1, new java.sql.Date(fecha.getTime())); stmt.setDate(2, new
	 * java.sql.Date(periodo.getTime())); stmt.setInt(3, nroLiquidacion);
	 * stmt.setString(4, nroPrestador); stmt.setString(5, prestador);
	 * stmt.setInt(6, nroFarmacia); stmt.setString(7, farmacia); if (idOspim ==
	 * null) { stmt.setNull(8, Type.INT); } else { stmt.setInt(8, idOspim); } if
	 * (idAmtima == null) { stmt.setNull(9, Type.INT); } else { stmt.setInt(9,
	 * idAmtima); } if (idUoma == null) { stmt.setNull(10, Type.INT); } else {
	 * stmt.setInt(10, idUoma); } stmt.setInt(11, inte); stmt.setString(12,
	 * nombreApellido); stmt.setString(13, nroRecetario); stmt.setString(14,
	 * troquel); stmt.setString(15, medicamento); stmt.setInt(16, cantidad);
	 * stmt.setBigDecimal(17, pvp); stmt.setBigDecimal(18, totalOspim);
	 * stmt.setBigDecimal(19, totalAmtima); stmt.setString(20, debito);
	 * stmt.setBigDecimal(21, difOspim); stmt.setBigDecimal(22, difAmtima); if
	 * (porcentajeOspim == null) { stmt.setNull(23, Type.DOUBLE); } else {
	 * stmt.setDouble(23, porcentajeOspim); } if (porcentajeAmtima == null) {
	 * stmt.setNull(24, Type.DOUBLE); } else { stmt.setDouble(24,
	 * porcentajeAmtima); } stmt.setString(25, pmi); }
	 */
}
