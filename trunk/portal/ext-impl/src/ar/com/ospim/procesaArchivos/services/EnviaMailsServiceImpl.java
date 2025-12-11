package ar.com.ospim.procesaArchivos.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EnviaMailsServiceImpl {

	private static Log logger = LogFactoryUtil
			.getLog(ProcesaArchivosServiceImpl.class);

	public List<String> getListaEmails() throws SQLException{
		Connection con = null;
		PreparedStatement stmt = null;
		List<String> list = null;

		logger.debug("Comienzo a grabar archivo OS");
		try{
			con = getConnectionFromJavaApplication();
			
			// Verifico si ya procesé el archivo por footer PK...
			String sql = "{call lista_emails_vademecum_amtima()}";
			stmt = con.prepareCall(sql.toString());
			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<String>();
			
			while (rs.next()) {			
				list.add(rs.getString("email"));
			}
			
		}catch (Exception e) {
			throw new SQLException();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return list;
	}
	
	public List<String> getListaEmailsEmpleadores() throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		List<String> list = null;

		try{
			logger.debug("Comienzo a grabar archivo OS");
			con = getConnectionFromJavaApplication();
	
			// Verifico si ya procesé el archivo por footer PK...
			String sql = "{call lista_emails_empleadores()}";
			stmt = con.prepareCall(sql.toString());
			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<String>();
			
			while (rs.next()) {			
				list.add(rs.getString("email"));
			}
			
		}catch (Exception e) {
			throw new SQLException();
		}finally{
			ConnectionHelper.cerrar(stmt, con);
		}	
		return list;
	}


	private Connection getConnectionFromJavaApplication() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("/home/fbrachi/workspace/ext/app.server.fbrachi.properties"));			
			String ip=prop.getProperty("ip.produccion");
			String pass=prop.getProperty("pass.produccion");
			String base=prop.getProperty("base.produccion");
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

}
