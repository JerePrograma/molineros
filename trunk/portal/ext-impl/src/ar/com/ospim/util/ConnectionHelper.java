package ar.com.ospim.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import ar.com.ospim.mail.MailUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

// Implementamos el servicio que nos da acceso a los datos de la aplicación (BD).
public class ConnectionHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3190031402389089537L;
	
	private static Calendar ultimaAlerta = Calendar.getInstance();
	
	private static Log _log = LogFactoryUtil.getLog(ConnectionHelper.class);

	/**
	 * Method to locate data source.
	 * 
	 * @return
	 */
	private static PooledDataSource datasource;
	private static PooledDataSource cgtdatasource;
	private static PooledDataSource mailingdatasource;
	private static PooledDataSource lportalDatasource;
	private static PooledDataSource reportesOspimDatasource;
	private static PooledDataSource portalempleadores;
	private static PooledDataSource postgresDataSource;
	private static PooledDataSource portalempleadoresV01;

	public static PooledDataSource getDataSource() {
		datasource = new ComboPooledDataSource("portal-ospim");
		return datasource;
	}
	
	public static PooledDataSource getDataSourceCGT() {
		cgtdatasource = new ComboPooledDataSource("portal-cgt");
		return cgtdatasource;
	}
	
	public static PooledDataSource getDataSourceMailing() {
		mailingdatasource = new ComboPooledDataSource("portal-mailing");
		return mailingdatasource;
	}


	public static PooledDataSource getLPortalDataSource() {
		lportalDatasource = new ComboPooledDataSource("portal-lportal");
		return lportalDatasource;
	}

	public static PooledDataSource getReportesOspimDataSource() {
		reportesOspimDatasource = new ComboPooledDataSource(
				"portal-ospim-reportes");
		return reportesOspimDatasource;
	}
	
	public static PooledDataSource getDataSourcePortalEmpleadores() {
		portalempleadores = new ComboPooledDataSource("portalempleadores");
		return portalempleadores;
	}
	
	public static PooledDataSource getDataSourcePortalEmpleadoresV01() {
		portalempleadoresV01 = new ComboPooledDataSource("portalempleadoresV01");
		return portalempleadoresV01;
	}

	public static Connection getConnectionForTransaction() {
		Connection connection = getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (Exception e) {
			// no creo q haga falta cerrarla, pero no molesta..
			ConnectionHelper.cerrar(connection);
			return null;
		}
		return connection;
	}

	@SuppressWarnings("deprecation")
	public static Connection getConnection() {
		Connection conn = null;
		if (datasource == null) {
			datasource = getDataSource();

		}
		if (datasource != null) {
			try {
				int numBusyConnections = datasource.getNumBusyConnections();
				if (numBusyConnections >= 5) {
					Calendar aux = Calendar.getInstance();
					aux.setTime(ultimaAlerta.getTime());
					aux.add(Calendar.MINUTE, 30);
					// si pasaron mas de 30 minutos, volver a mandar alerta
					if (aux.compareTo(Calendar.getInstance()) < 0) {
						ultimaAlerta = Calendar.getInstance();
						// sendMails(numBusyConnections);
					}
//					_log.debug("------------------------>Mas de 5 conexiones: "
//							+ numBusyConnections
//							+ "<----------------------------------");
				}
				/*System.out.println("CANTIDAD DE CNX ANTES DAR: "
						+ datasource.getNumConnections());
				System.out.println("CANTIDAD DE CNX OCUPADAS: "
						+ numBusyConnections);*/
				conn = datasource.getConnection();
				/*System.out.println("CANTIDAD DE CNX AL DAR: "
						+ datasource.getNumConnections());
				System.out.println("CANTIDAD DE CNX OCUPADAS AL DAR: "
						+ numBusyConnections);*/
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}

	public static void sendMails(int cantConexiones) {
		List<String> emails = new ArrayList<String>();
//		emails.add("moreyramj@gmail.com");
//		emails.add("fbrachi@gmail.com");
		emails.add("svalentini@gmail.com");
//		emails.add("carlos.rivas.a@gmail.com");
		MailUtils
				.enviarMailGmailSinAdj(
						"errores.ospim@gmail.com",
						"eRR0Res!",
						emails,
						"Se detectaron muchas conexiones en la aplicacion",
						"Se detectaron "
								+ cantConexiones
								+ " conexiones ocupadas en la aplicacion. Buscar en el log el siguiente texto '------------------------>Mas de 5 conexiones:'", null);

	}

	@SuppressWarnings("deprecation")
	public static Connection getLPortalConnection() {
		Connection conn = null;
		if (lportalDatasource == null) {
			lportalDatasource = getLPortalDataSource();

		}
		if (lportalDatasource != null) {
			try {
				System.out.println("CANTIDAD DE CNX ANTES DAR: "
						+ lportalDatasource.getNumConnections());
				System.out.println("CANTIDAD DE CNX OCUPADAS: "
						+ lportalDatasource.getNumBusyConnections());
				conn = lportalDatasource.getConnection();
				System.out.println("CANTIDAD DE CNX AL DAR: "
						+ lportalDatasource.getNumConnections());
				System.out.println("CANTIDAD DE CNX OCUPADAS AL DAR: "
						+ lportalDatasource.getNumBusyConnections());
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}

	@SuppressWarnings("deprecation")
	public static Connection getReportesOspimConnection() {
		Connection conn = null;
		if (reportesOspimDatasource == null) {
			reportesOspimDatasource = getReportesOspimDataSource();

		}
		if (reportesOspimDatasource != null) {
			try {
				System.out.println("CANTIDAD DE CNX ANTES DAR: "
						+ reportesOspimDatasource.getNumConnections());
				System.out.println("CANTIDAD DE CNX OCUPADAS: "
						+ reportesOspimDatasource.getNumBusyConnections());
				conn = reportesOspimDatasource.getConnection();
				System.out.println("CANTIDAD DE CNX AL DAR: "
						+ reportesOspimDatasource.getNumConnections());
				System.out.println("CANTIDAD DE CNX OCUPADAS AL DAR: "
						+ reportesOspimDatasource.getNumBusyConnections());
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}
	
	public static Connection getConnectionCGT() {
		Connection conn = null;
		if (cgtdatasource == null) {
			cgtdatasource = getDataSourceCGT();

		}
		if (cgtdatasource != null) {
			try {
				conn = cgtdatasource.getConnection();
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}
	
	public static Connection getConnectionMailing() {
		Connection conn = null;
		if (mailingdatasource == null) {
			mailingdatasource = getDataSourceMailing();

		}
		if (mailingdatasource != null) {
			try {
				conn = mailingdatasource.getConnection();
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}
	
	public static Connection getConnectionPortalEmpleadores() {
		Connection conn = null;
		if (portalempleadores == null) {
			portalempleadores = getDataSourcePortalEmpleadores();

		}
		if (portalempleadores != null) {
			try {
				conn = portalempleadores.getConnection();
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}

	public static Connection getConnectionPortalEmpleadoresV01() {
		Connection conn = null;
		if (portalempleadoresV01 == null) {
			portalempleadoresV01 = getDataSourcePortalEmpleadoresV01();

		}
		if (portalempleadoresV01 != null) {
			try {
				conn = portalempleadoresV01.getConnection();
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}

		return conn;
	}
	
	public static void rollback(Connection con) {
		try {
			con.rollback();
		} catch (SQLException e1) {
			_log.error("Error al hacer rollback");
		}
	}

	public static void cerrar(Statement stmt, Connection con) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				_log.debug("error", e);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				_log.debug("error", e);
			}
		}
	}

	public static void cerrar(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				_log.debug("error", e);
			}
		}
	}

	public static void cerrar(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				_log.debug("error", e);
			}
		}
	}
	
	public static Connection getConnectionFromJavaApplication() {
		Properties prop = new Properties();
				
		try {							  
//			prop.load(new FileInputStream("/home/pablo/workspace_produccion/portal/app.server.pablo.properties"));
			prop.load(new FileInputStream("/home/sergio/workspace_produccion/portalMolineros/app.server.sergio.properties"));

			///home/sistemas-01/workspaceNuevo/ext/app.server.sistemas-01.properties
			String ip=prop.getProperty("ip.qa");
			String pass=prop.getProperty("pass.qa");
			String base=prop.getProperty("base.qa");
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://"+ip+":5432/"+base,
					"postgres", pass);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static PooledDataSource getDataSourcePostgres() {
		postgresDataSource = new ComboPooledDataSource("portal-postgres");
		return postgresDataSource;
	}
	
	public static Connection getConnectionPostgres() {
		Connection conn = null;
		if (postgresDataSource == null) {
			postgresDataSource = getDataSourcePostgres();

		}
		if (postgresDataSource != null) {
			try {
				conn = postgresDataSource.getConnection();
			} catch (SQLException e) {
				_log.error("Cannot get connection:" + e.getMessage() + e);
			}
		}
		return conn;
	}

}
