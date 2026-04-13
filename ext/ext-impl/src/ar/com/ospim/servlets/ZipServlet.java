package ar.com.ospim.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.afiliados.reportes.ReporteListadoPadron;
import ar.com.ospim.afiliados.reportes.ReporteListadosTercerizadoras;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ZipServlet extends HttpServlet {
	private static Log _log = LogFactoryUtil
			.getLog(ZipServlet.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String REPORTE_TERCERIZADORA_CAPITAS="REPORTE_TERCERIZADORA_CAPITAS";
	private static final String REPORTE_TERCERIZADORA_CAPITAS_HISTORICO="REPORTE_TERCERIZADORA_CAPITAS_HISTORICO";

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		res.setContentType("application/zip");
		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("Content-disposition",
				"attachment;filename=listadoPadron.zip");
		String accion = ParamUtil.getString(req, "reporte");		
		ServletOutputStream out= res.getOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(out);
		
		HSSFWorkbook wb=null;

		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		String id_terc=ParamUtil.getString(req, "id_terc");
		String descripcionTipo = "";
		
		int tipo=ParamUtil.getInteger(req, "tipoInforme");
		if(tipo == 0){
			String tipoStr = ParamUtil.getString(req, "tipoInforme");
			descripcionTipo = tipoStr.trim();
		}else{
			switch (tipo) {
			case 1:
				descripcionTipo = "PadronCompleto";
				break;
			case 2:
				descripcionTipo = "Diferencias";
				break;
			case 3:
				descripcionTipo = "Titulares";
				break;
			case 4:
				descripcionTipo = "ValorizPorCapita";
				break;
			case 5:
				descripcionTipo = "Facturacion";
				break;		
			}
		}	
		id_terc+="_"+descripcionTipo+"_";
		if(accion.equals(REPORTE_TERCERIZADORA_CAPITAS)){
									
			wb=getReporteTercerizadoras(req, res);
			ZipEntry entry = new ZipEntry("Listado_"+id_terc+sdf.format(new Date(System.currentTimeMillis()))+".xls");   
	        zipOut.putNextEntry(entry);   
	        wb.write(zipOut); //writes this workbook to an output stream	        
	        zipOut.closeEntry();
		}else if(accion.equals(REPORTE_TERCERIZADORA_CAPITAS_HISTORICO)){						
			wb=getReporteTercerizadorasHistorico(req, res);
			ZipEntry entry = new ZipEntry("Listado_"+id_terc+ParamUtil.getString(req, "fecha").replace("/", "-")+".xls");   
	        zipOut.putNextEntry(entry);   
	        wb.write(zipOut); //writes this workbook to an output stream	        
	        zipOut.closeEntry();			
		}else{
			getReporte(req, res, zipOut);
		}
		
		zipOut.flush();
		zipOut.close();
		OutputStream outStream = res.getOutputStream();
		outStream.write(baos.toByteArray());
		outStream.flush();
		outStream.close();

	}
	
	private HSSFWorkbook getReporteTercerizadoras(HttpServletRequest req, HttpServletResponse res) throws IOException {
		return ReporteListadosTercerizadoras.getReporteVigentesTercerizadora(req, res);
	}
	
	private HSSFWorkbook getReporteTercerizadorasHistorico(HttpServletRequest req, HttpServletResponse res) throws IOException {
		return ReporteListadosTercerizadoras.getReporteVigentesTercerizadoraHistorico(req, res);
	}

	private void getReporte(HttpServletRequest req, HttpServletResponse res,
			ZipOutputStream out) throws IOException {
		ReporteListadoPadron.getReporte(req, res, out);
	}

}
