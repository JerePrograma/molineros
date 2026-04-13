package ar.com.ospim.afiliados.reportes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.reportes.beans.ReporteAmtimaPMI;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReportesAmtimaPmiServiceImpl {
	private static Log logger = LogFactoryUtil.getLog(ReportesAmtimaPmiServiceImpl.class);
	
	public int generaLoteCartasAjuar(String[] cartas)throws Exception{
		int result=0;		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("Comienzo a grabar archivo OS");
			con = ConnectionHelper.getReportesOspimConnection();			
			
			stmt=con.prepareStatement("select max(id_lote)+1 as id_lote from amtima_ajuares");
			ResultSet rs=stmt.executeQuery();
			rs.next();
			result=rs.getInt("id_lote");
			con.setAutoCommit(false);			
			
			Date fecha_hoy=new Date(System.currentTimeMillis());
			String sql = "INSERT INTO amtima_ajuares(id_lote, id_amtima, inte, alta_fecha)"
					+ "VALUES (?, ?, ?, ?)";
			stmt = con.prepareStatement(sql.toString());
			for(int i=0;i<cartas.length;i++){
				String[] amtimaInte=cartas[i].split("-");		
				stmt.setInt(1, result);
				stmt.setInt(2, Integer.parseInt(amtimaInte[0]));
				stmt.setInt(3, Integer.parseInt(amtimaInte[1]));				
				stmt.setDate(4, new java.sql.Date(fecha_hoy.getTime()));
				stmt.executeUpdate();
			}			
			con.commit();
			
		} catch (SQLException e) {			
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
	
	public List<ReporteAmtimaPMI> getReporteAmtimaPMI(Date fecha_desde, Date fecha_hasta,Boolean soloConyuges)throws Exception{		
		Connection con = null;
		CallableStatement stmt = null;		
		
		List<ReporteAmtimaPMI> result=null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  reporte_pmi_amtima(?,?,?)}";			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_desde.getTime()));
			stmt.setDate(2, new java.sql.Date(fecha_hasta.getTime()));
			stmt.setBoolean(3, soloConyuges);
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<ReporteAmtimaPMI>();
			while (rs.next()) {
				ReporteAmtimaPMI repPMI= new ReporteAmtimaPMI(rs.getDate("Fecha"), rs.getInt("Nro. Socio"), rs.getInt("Inte"), rs.getString("Apellido y Nombre"), rs.getString("Seccional"),
															  rs.getString("Titular"), rs.getString("Empresa"));
				result.add(repPMI);
			}

		
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			
		}
		
		return result;
		
	}
}
