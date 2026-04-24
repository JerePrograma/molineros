package ar.com.ospim.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import ar.com.ospim.util.ConnectionHelper;

public class TestNotaDebTercerosJasperReport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	Connection con = null;
	
	try{	
		con = ConnectionHelper.getConnectionFromJavaApplication();
////		InputStream in = getClass().getClassLoader().getResourceAsStream("jasper/orden_pago/ordenPagoOSPIM.jasper");
////		JasperCompileManager.compileReport("jasper/orden_pago/ordenPagoOSPIM.jrxml");
//		JasperCompileManager.compileReport("ordenPagoOSPIM.jrxml"); //
//		try {
//			JasperPrint print = JasperFillManager.fillReport(in, params, con);			
////			return JasperExportManager.exportReportToPdf(print);
//			
//		} catch (Exception e) {
//			_log.error(e);
//		} finally {
//			try {
//				in.close();
//				con.close();
//			} catch (SQLException e) {
//				_log.error("Error cerrando conexion", e);
//			} catch (IOException e) {
//				_log.error("Error cerrando conexion", e);			}
//		}
//
//	}
			String path = "/home/sergio/workspace_portal/portal/portal/ext-impl/src/ar/com/ospim/test/";//"/home/sergio/workspace_portal/portal/portal/ext-web/docroot/WEB-INF/classes/jasper/";
			String reportName = "notaDebitoTerceros";
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			parameters.put("id_ini", "97239");
			parameters.put("terceros", "0");
			parameters.put("importe_terceros", "0");	
			// compiles jrxml
			JasperCompileManager.compileReportToFile(path + reportName + ".jrxml");
			// fills compiled report with parameters and a connection
			JasperPrint print = JasperFillManager.fillReport(path + reportName + ".jasper", parameters, con);
			// exports report to pdf
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(path + reportName + ".pdf")); // your output goes here
			
			exporter.exportReport();

		} catch (Exception e) {
			throw new RuntimeException("It's not possible to generate the pdf report.", e);
		} finally {
			// it's your responsibility to close the connection, don't forget it!
			if (con != null) {
				try { con.close(); } catch (Exception e) {}
			}
		}

	}
}
