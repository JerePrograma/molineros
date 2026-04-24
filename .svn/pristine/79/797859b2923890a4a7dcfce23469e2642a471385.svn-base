import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.postgresql.util.PSQLException;

import ar.com.ospim.util.ConnectionHelper;

public final class InsertarLiquidacion {

	public static void main(String... aArgs) throws IOException {
		System.out.println(new Date());
		File folderAProcesar = new File(
				"C:\\Users\\sistema-01\\Desktop\\aa");
		File[] filesList = folderAProcesar.listFiles();

		for (int i = 0; i < filesList.length; i++) {
			if (filesList[i].isFile()) {
				BufferedReader reader = new BufferedReader(new FileReader(
						filesList[i].getAbsolutePath()));
				try {
					String line = null;
					ArrayList<Object[]> list = new ArrayList<Object[]>();
					while ((line = reader.readLine()) != null) {
						if (line.trim().length() > 0) {
							Object[] obtenerArrayObj = obtenerArrayObj(line);
							if (obtenerArrayObj != null) {
								list.add(obtenerArrayObj);
							}
						}
						if (list.size() == 50000) {
							insertarRegisstros(list);
							list.clear();
						}
					}
					if (list.size() > 0) {
						insertarRegisstros(list);
					}
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

	private static Object[] obtenerArrayObj(String line) {
		Object res[] = null;
		try {

			if (line.substring(0, 11).trim().equals("")) {
				return null;
			}

			SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
			SimpleDateFormat formatterFormato1 = new SimpleDateFormat("MM/yy");
			String peri = line.substring(12, 19).trim();
			boolean formato1 = peri.substring(peri.indexOf("/") + 1).length() == 2;

			if (!formato1) {
				if (peri.trim().substring(peri.indexOf("/") + 1).contains(" ")) {
					formato1 = true;
				}
			}

			if (formato1) {
				String str0 = line.substring(0, 11).trim(); // Cuit,
				Date str1 = formatterFormato1.parse(line.substring(12, 17)
						.trim());// periodo,
				Date str2 = formatterFormato1.parse(line.substring(18, 23)
						.trim());// fecha
				// ingreso,
				String str3 = line.substring(24, 35).trim();// cuil,
				BigDecimal str4 = getBigDecimal(line.substring(36, 47).trim()
						.replaceAll(",", "."));// remuneración
				BigDecimal str5 = getBigDecimal(line.substring(48, 58).trim()
						.replaceAll(",", "."));// , aporte
				BigDecimal str6 = getBigDecimal(line.substring(59, 69).trim()
						.replaceAll(",", "."));// contribución,
				BigDecimal str7 = getBigDecimal(line.substring(70, 80).trim()
						.replaceAll(",", "."));// total,
				String str8 = line.substring(81, 101).trim();// APE
				String str9 = line.substring(102).trim();// NOMBRE -----,
				res = new Object[] { str0, str1, str2, str3, str4, str5, str6,
						str7, (BigDecimal) null, str8 + ", " + str9 };
			} else if (line.length() == 186) {
				String str0 = line.substring(0, 11).trim(); // Cuit,
				Date str1 = formatter.parse(line.substring(12, 19).trim());// periodo,
				Date str2 = formatter.parse(line.substring(20, 27).trim());// fecha
				// ingreso,
				String str3 = line.substring(28, 39).trim();// cuil,
				BigDecimal str4 = getBigDecimal(line.substring(40, 51).trim());// remuneración
				BigDecimal str5 = getBigDecimal(line.substring(52, 68).trim());// ,
				// aporte
				BigDecimal str6 = getBigDecimal(line.substring(69, 80).trim());// contribución,
				BigDecimal str7 = getBigDecimal(line.substring(81, 91).trim());// total,
				String str8 = line.substring(92).trim();// APE y nombre
				// "ape, nombre" (187)
				// ----------.
				res = new Object[] { str0, str1, str2, str3, str4, str5, str6,
						str7, (BigDecimal) null, str8 };
			} else if (line.length() == 197) {

				String str0 = line.substring(0, 11).trim(); // Cuit,
				Date str1 = formatter.parse(line.substring(12, 19).trim());// periodo,
				Date str2 = formatter.parse(line.substring(20, 27).trim());// fecha
				// ingreso,
				String str3 = line.substring(28, 39).trim();// cuil,
				BigDecimal str4 = getBigDecimal(line.substring(40, 51).trim());// remuneración
				BigDecimal str5 = getBigDecimal(line.substring(52, 68).trim());// ,
				// aporte
				BigDecimal str6 = getBigDecimal(line.substring(69, 80).trim());// contribución,
				BigDecimal str7 = getBigDecimal(line.substring(81, 91).trim());// total,
				BigDecimal str8 = getBigDecimal(line.substring(92, 102).trim());// total_terc
				String str9 = line.substring(103).trim();// APE y nombre
				// "ape, nombre"
				// (198)--------.
				res = new Object[] { str0, str1, str2, str3, str4, str5, str6,
						str7, str8, str9 };
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return res;
	}

	private static BigDecimal getBigDecimal(String trim) {
		if (trim.equals("")) {
			return null;
		}
		return new BigDecimal(trim);
	}

	private static void insertarRegisstros(ArrayList<Object[]> list)
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
					String sql = "INSERT INTO liq_mys (cuit, periodo, fecha_ingreso, cuil, remuneracion, aporte, contribucion, total, total_terc, ape_nom) values (?,?,?,?,?,?,?,?,?,?)";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						Object[] det = list.get(i);
						stmt.setString(1, (String) det[0]); // cuit
						stmt.setDate(2, new java.sql.Date(((Date) det[1])
								.getTime()));// periodo,
						stmt.setDate(3, new java.sql.Date(((Date) det[2])
								.getTime()));// fecha ingreso,
						stmt.setString(4, (String) det[3]);// cuil,
						stmt.setBigDecimal(5, (BigDecimal) det[4]);// remuneración
						stmt.setBigDecimal(6, (BigDecimal) det[5]);// aporte
						stmt.setBigDecimal(7, (BigDecimal) det[6]);// contribución,
						stmt.setBigDecimal(8, (BigDecimal) det[7]);// total,
						stmt.setBigDecimal(9, (BigDecimal) det[8]);// total_terc
						stmt.setString(10, (String) det[9]);// APE y
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
			// NO HACE NADA
			e.printStackTrace();
		}
		return null;
	}
}
