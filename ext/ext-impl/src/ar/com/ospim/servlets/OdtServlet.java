package ar.com.ospim.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import ar.com.ospim.afiliados.reportes.ReportesAmtimaPmiServiceImpl;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class OdtServlet extends HttpServlet {
	private static Log _log = LogFactoryUtil.getLog(OdtServlet.class);
	private static final long serialVersionUID = 1L;

	private static final String CHEQUES = "jasper/cheque/chequeospim.jasper";
	private static final String CHEQUE_ODT_FILENAME = "Cheque.odt";
	private static final String CHEQUE_DOC_FILENAME = "Cheque.doc";
	
	private static final String CHEQUES_OSPIM_FARMACIA = "jasper/cheque/chequeOspimFarmacia.jasper";
	private static final String CHEQUE_OSPIM_FARMACIA_ODT_FILENAME = "ChequeFarmaciaOspim.odt";

	private static final String CERTIFICADO_AFILIACION = "jasper/certificado_afiliaciones/certificadoAfiliacion.jasper";
	private static final String CERTIFICADO_AFILIACION_ODT_FILENAME = "certificadoAfiliacion.odt";
	private static final String CERTIFICADO_AFILIACION_RTF_FILENAME = "certificadoAfiliacion.rtf";

	private static final String AMTIMA_AJUAR = "jasper/amtima_ajuares/amtimaAjuares.jasper";
	private static final String AMTIMA_AJUAR_ODT_FILENAME = "amtimaAjuares.odt";
	
	private static final String RECIBO_FALLECIMIENTO_JASPER = "jasper/subsidio_fallecimiento/subsidioFallecimiento.jasper";
	private static final String RECIBO_FALLECIMIENTO_ODT_FILENAME = "subsidioFallecimiento.odt";

	private static final String AUTORIZACION_TRATAMIENTO_JASPER = "jasper/tratamientos_discapacidad/autorizacionTratamiento.jasper";
	private static final String AUTORIZACION_TRATAMIENTO_JASPER_ODT = "jasper/tratamientos_discapacidad/autorizacionTratamientoODT.jasper";	
	private static final String AUTORIZACION_TRATAMIENTO_ODT_FILENAME = "autorizacionTratamiento.odt";	
	private static final String AUTORIZACION_TRATAMIENTO_RTF_FILENAME = "autorizacionTratamiento.rtf";
	
	private static final String DOCUMENTACION_FALTANTE = "jasper/tratamientos_discapacidad/documentosFaltantes.jasper";
	private static final String DOCUMENTACION_FALTANTE_ODT = "jasper/tratamientos_discapacidad/documentosFaltantesODT.jasper";
	private static final String DOCUMENTACION_FALTANTE_RTF_FILENAME = "DocumentacionFaltante.rtf";
	private static final String DOCUMENTACION_FALTANTE_ODT_FILENAME = "DocumentacionFaltante.odt";		
	
	private static final String RECIBO_FALLECIMIENTO = "RECIBO_FALLECIMIENTO";

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String accion = ParamUtil.getString(req, "accion");
		try {

			if (accion.equals("cheque")) {
				generaChequeDoc(req, res);
			}
			
			if (accion.equals("chequeOspimFarmacia")) {
				generaChequeOspimFarmacia(req, res);
			}

			if (accion.equals("certificadoAfiliacion")) {
				generaCertificadoAfiliacion(req, res);
			}

			if (accion.equals("cartasAjuar")) {
				generaCartasAjuar(req, res);

			}
			if (accion.equals("autorizacionTratamientoOdt")) {
				autorizacionTratamientoOdt(req, res);
			}
			if (accion.equals("autorizacionTratamientoRtf")) {
				autorizacionTratamientoRtf(req, res);
			}				
			if (accion.equals("documentacionFaltanteRtf")) {
				generaDocumentacionFaltante(req, res);
			}
			if (accion.equals("documentacionFaltanteOdt")) {
				generaDocumentacionFaltanteOdt(req, res);
			}
						
			if(accion.equals(RECIBO_FALLECIMIENTO)){
				generaReciboFallecimiento(req,res);
			}
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

	}
	
	private void generaReciboFallecimiento(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		HashMap<String, String> hm = new HashMap<String, String>();
		
		crearOdt(req, res, RECIBO_FALLECIMIENTO_JASPER, hm, RECIBO_FALLECIMIENTO_ODT_FILENAME);
	}

	private void generaCartasAjuar(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String cartas = ParamUtil.getString(req, "cartas");
		String[] cartasArray = cartas.split(";");
		ReportesAmtimaPmiServiceImpl rapmsi = new ReportesAmtimaPmiServiceImpl();
		int id_lote = rapmsi.generaLoteCartasAjuar(cartasArray);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_lote", String.valueOf(id_lote));
		crearOdt(req, res, AMTIMA_AJUAR, hm, AMTIMA_AJUAR_ODT_FILENAME);
	}

	private void generaCertificadoAfiliacion(HttpServletRequest req,
			HttpServletResponse res) {
		String cuil = ParamUtil.getString(req, "cuil");
		int inte = ParamUtil.getInteger(req, "inte");
		int tipo = ParamUtil.getInteger(req, "tipo");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("cuil", cuil == null ? "" : cuil);
		hm.put("inte", String.valueOf(inte));
		if (tipo == 1) { // ("rtf")
			crearRtf(req, res, CERTIFICADO_AFILIACION, hm,
					CERTIFICADO_AFILIACION_RTF_FILENAME);
		}
		if (tipo == 0) {// "odt"
			crearOdt(req, res, CERTIFICADO_AFILIACION, hm,
					CERTIFICADO_AFILIACION_ODT_FILENAME);
		}
	}

	private void generaCheque(HttpServletRequest req, HttpServletResponse res) {
		String numero = ParamUtil.getString(req, "numero");
		String numero_op = ParamUtil.getString(req, "numero_op");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("numero", numero == null ? "" : numero);
		hm.put("numero_op", numero_op == null ? "" : numero_op);
		crearOdt(req, res, CHEQUES, hm, CHEQUE_ODT_FILENAME);
	}
	
	private void generaChequeDoc(HttpServletRequest req, HttpServletResponse res) {
		String numero = ParamUtil.getString(req, "numero");
		String numero_op = ParamUtil.getString(req, "numero_op");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("numero", numero == null ? "" : numero);
		hm.put("numero_op", numero_op == null ? "" : numero_op);
		crearDoc(req, res, CHEQUES, hm, CHEQUE_DOC_FILENAME);
	}
	
	private void generaChequeOspimFarmacia(HttpServletRequest req, HttpServletResponse res) {
		String numero = ParamUtil.getString(req, "numero");
		String numero_hasta = ParamUtil.getString(req, "numero_hasta");
		String numero_op_ini = ParamUtil.getString(req, "ordenIniId");
		String numero_op_fin = ParamUtil.getString(req, "ordenFinId");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("numero", numero == null ? "" : numero);
		hm.put("numero_hasta", numero_hasta == null ? "" : numero_hasta);
		hm.put("numero_op_ini", numero_op_ini == null ? "" : numero_op_ini);
		hm.put("numero_op_fin", numero_op_fin == null ? "" : numero_op_fin);
		crearOdt(req, res, CHEQUES_OSPIM_FARMACIA, hm, CHEQUE_OSPIM_FARMACIA_ODT_FILENAME);
	}
	
	private void autorizacionTratamientoOdt(HttpServletRequest req,
			HttpServletResponse res) {
		String id_tratamiento = ParamUtil.getString(req, "id_tratamiento", "0");				
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_ini", id_tratamiento);
		hm.put("SUBREPORT_DIR", "jasper/");
		crearOdt(req, res, AUTORIZACION_TRATAMIENTO_JASPER_ODT, hm, AUTORIZACION_TRATAMIENTO_ODT_FILENAME);
	}

	private void autorizacionTratamientoRtf(HttpServletRequest req,
			HttpServletResponse res) {
		String id_tratamiento = ParamUtil.getString(req, "id_tratamiento", "0");				
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_ini", id_tratamiento);
		hm.put("SUBREPORT_DIR", "jasper/");
		crearRtf(req, res, AUTORIZACION_TRATAMIENTO_JASPER, hm, AUTORIZACION_TRATAMIENTO_RTF_FILENAME);
	}
	
	private void generaDocumentacionFaltante(HttpServletRequest req,
			HttpServletResponse res) {
		String cuil_titular = ParamUtil.getString(req, "cuil_titular", "");
		int inte = ParamUtil.getInteger(req, "inte", 0);
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(req,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(req,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(req,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(req,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("cuil_titular", cuil_titular);
		hm.put("inte", String.valueOf(inte));
		hm.put("fecha_desde", DateUtils.format(fechaDesde, DateUtils.SHORT));
		hm.put("fecha_hasta", DateUtils.format(fechaHasta, DateUtils.SHORT));
		hm.put("SUBREPORT_DIR", "jasper/tratamientos_discapacidad/");
		crearRtf(req, res, DOCUMENTACION_FALTANTE, hm, DOCUMENTACION_FALTANTE_RTF_FILENAME);
	}
	
	private void generaDocumentacionFaltanteOdt(HttpServletRequest req,
			HttpServletResponse res) {
		String cuil_titular = ParamUtil.getString(req, "cuil_titular", "");
		int inte = ParamUtil.getInteger(req, "inte", 0);
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(req,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(req,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(req,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(req,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("cuil_titular", cuil_titular);
		hm.put("inte", String.valueOf(inte));
		hm.put("fecha_desde", DateUtils.format(fechaDesde, DateUtils.SHORT));
		hm.put("fecha_hasta", DateUtils.format(fechaHasta, DateUtils.SHORT));
		hm.put("SUBREPORT_DIR", "jasper/tratamientos_discapacidad/");
		crearOdt(req, res, DOCUMENTACION_FALTANTE_ODT, hm, DOCUMENTACION_FALTANTE_ODT_FILENAME);
	}

	private void crearOdt(HttpServletRequest req, HttpServletResponse res,
			String jasperFile, HashMap<String, String> params,
			String outPdfFileName) {
		Connection con = ConnectionHelper.getConnection();
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				jasperFile);

		try {
			JasperPrint print = JasperFillManager.fillReport(in, params, con);
			print.setLeftMargin(0);
			print.setBottomMargin(0);
			print.setTopMargin(0);			
			print.setOrientation(OrientationEnum.PORTRAIT);
			res.setContentType("application/rtf");
			res.setHeader("Content-Disposition", "attachment; filename=\""
					+ outPdfFileName + "\"");
			res.setHeader("Cache-Control", "no-cache");

			OutputStream outStream = res.getOutputStream();
			// ACA LE DIGO Q USE ODT,
			// el archivo de jasper es el mismo tanto para pdf u odt, lo que
			// cambia es
			// el exporter

			JROdtExporter exporter = new JROdtExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outStream);
			exporter.exportReport();
			outStream.flush();
		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				in.close();
				con.close();
			} catch (SQLException e) {
				_log.error("Error cerrando conexion", e);
			} catch (IOException e) {
				_log.error("Error cerrando conexion", e);
			}
		}
	}

	private void crearRtf(HttpServletRequest req, HttpServletResponse res,
			String jasperFile, HashMap<String, String> params,
			String outPdfFileName) {
		Connection con = ConnectionHelper.getConnection();
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				jasperFile);

		try {
			JasperPrint print = JasperFillManager.fillReport(in, params, con);
			print.setLeftMargin(0);
			print.setBottomMargin(0);
			print.setOrientation(OrientationEnum.PORTRAIT);
			res.setContentType("application/rtf");
			res.setHeader("Content-Disposition", "attachment; filename=\""
					+ outPdfFileName + "\"");
			res.setHeader("Cache-Control", "no-cache");

			OutputStream outStream = res.getOutputStream();
			// ACA LE DIGO Q USE ODT,
			// el archivo de jasper es el mismo tanto para pdf u odt, lo que
			// cambia es
			// el exporter

			JRRtfExporter exporter = new JRRtfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outStream);
			exporter.exportReport();
			outStream.flush();
		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				in.close();
				con.close();
			} catch (SQLException e) {
				_log.error("Error cerrando conexion", e);
			} catch (IOException e) {
				_log.error("Error cerrando conexion", e);
			}
		}
	}

	private void crearDoc(HttpServletRequest req, HttpServletResponse res,
			String jasperFile, HashMap<String, String> params,
			String outPdfFileName) {
		Connection con = ConnectionHelper.getConnection();
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				jasperFile);

		try {
			JasperPrint print = JasperFillManager.fillReport(in, params, con);
			print.setLeftMargin(0);
			print.setBottomMargin(0);
			print.setTopMargin(0);			
			print.setOrientation(OrientationEnum.PORTRAIT);
			res.setContentType("application/rtf");
			res.setHeader("Content-Disposition", "attachment; filename=\""
					+ outPdfFileName + "\"");
			res.setHeader("Cache-Control", "no-cache");

			OutputStream outStream = res.getOutputStream();
			// ACA LE DIGO Q USE ODT,
			// el archivo de jasper es el mismo tanto para pdf u odt, lo que
			// cambia es
			// el exporter

			JRDocxExporter exporter = new JRDocxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outStream);
			exporter.exportReport();
			outStream.flush();
		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				in.close();
				con.close();
			} catch (SQLException e) {
				_log.error("Error cerrando conexion", e);
			} catch (IOException e) {
				_log.error("Error cerrando conexion", e);
			}
		}
	}

	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

}
