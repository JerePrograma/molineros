package ar.com.ospim.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import ar.com.ospim.autorizaciones.beans.SituacionMedica;
import ar.com.ospim.autorizaciones.services.SituacionesMedicasServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserServiceUtil;

public class PdfServlet extends HttpServlet {
	private static Log _log = LogFactoryUtil.getLog(PdfServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String CREDENCIALES = "jasper/credencial/credencial_OSPIM_A4.jasper";
	private static final String CREDENCIAL_PDF_FILENAME = "Credencial.pdf";
	
	private static final String CREDENCIALES_EXENTO = "jasper/afiliaciones/credencial_exepcion_copago.jasper";
	private static final String CREDENCIAL_EXENTO_PDF_FILENAME = "credencial_exepcion_copago.pdf";
	
	private static final String CREDENCIALES_CES = "jasper/credencial/credencial_OSPIM_CES_A4.jasper";
	private static final String CREDENCIAL_CES_PDF_FILENAME = "Credencial_CES.pdf";

	private static final String CHEQUES = "jasper/cheque/chequeospim.jasper";
	private static final String CHEQUE_PDF_FILENAME = "Cheque.pdf";
	
	private static final String ORDEN_PAGO = "jasper/orden_pago/ordenPagoAmtima.jasper";
	private static final String ORDEN_PAGO_PDF_FILENAME = "OrdenPago.pdf";
	
	private static final String ORDEN_PAGO_FARMACIA = "jasper/orden_pago/ordenPagoAmtimaFarmacia.jasper";
	private static final String ORDEN_PAGO_FARMACIA_PDF_FILENAME = "OrdenPagoFarmacia.pdf";
	
	//public static final String ORDEN_PAGO_OSPIM = "jasper/orden_pago/ordenPagoOspim.jasper";
	public static final String ORDEN_PAGO_OSPIM = "jasper/orden_pago/ordenPagoOSPIM.jasper";
	public static final String ORDEN_PAGO_OSPIM_INTEGRACION = "jasper/orden_pago/ordenPagoOSPIM_Integracion_v1.jasper";
	public static final String ORDEN_PAGO_OSPIM_PDF_FILENAME = "OrdenPagoOspim.pdf";
	
	public static final String COMPROBANTE_RETEN_GANANCIAS = "jasper/orden_pago/certificadoRetencion.jasper";
	public static final String COMPROBANTE_RETEN_GANANCIAS_PDF_FILENAME = "certificadoRetencion.pdf";
	
	public static final String COMPROBANTE_RETEN_IIBB = "jasper/orden_pago/certificadoRetencionIIBB.jasper";
	public static final String COMPROBANTE_RETEN_IIBB_PDF_FILENAME = "certificadoRetencionIIBB.pdf";
	
	public static final String COMPROBANTE_RETEN_IVA = "jasper/orden_pago/certificadoRetencionIVA.jasper";
	public static final String COMPROBANTE_RETEN_IVA_PDF_FILENAME = "certificadoRetencionIVA.pdf";
		
	private static final String ORDEN_PAGO_UOMA = "jasper/orden_pago/ordenPagoUOMA.jasper";
	private static final String ORDEN_PAGO_UOMA_PDF_FILENAME = "OrdenPagoUoma.pdf";
	
	private static final String ORDEN_PAGO_OSPIM_FARMACIA = "jasper/orden_pago/ordenPagoOSFar.jasper";
	//private static final String ORDEN_PAGO_OSPIM_FARMACIA = "jasper/orden_pago/ordenPagoOspimFarmacia.jasper";
	
	private static final String NOTA_DEBITO_LIQUIDACION = "jasper/orden_pago/notaDebitoTerceros.jasper";
	
	private static final String NOTA_DEBITO_LIQUIDACION_PDF_FILENAME = "NotaDebito.pdf";

	private static final String DOCUMENTACION_FALTANTE = "jasper/tratamientos_discapacidad/documentosFaltantes.jasper";
	private static final String DOCUMENTACION_FALTANTE_PDF_FILENAME = "DocumentacionFaltante.pdf";
	
	public static final String AUTORIZACION_TRATAMIENTO_JASPER = "jasper/tratamientos_discapacidad/autorizacionPrestacional.jasper";
	public static final String AUTORIZACION_TRATAMIENTO_ODT_FILENAME = "autorizacionTratamiento.pdf";
	
	private static final String AUTORIZACION_RECETAS_PMI_JASPER = "jasper/autorizaciones/recetasPmi.jasper";
	private static final String AUTORIZACION_RECETAS_PMI_PDF_FILENAME = "RecetasPmi.pdf";

	private static final String TARJETACOORDENADAS = "jasper/credencial/tarjetacoordenadas_v01.jasper";
	private static final String TARJETACOORDENADAS_PDF_FILENAME = "TarjetaCoordenadas.pdf";
	
	private static final String CAJA_CHICA_RECIBO_UOMA = "jasper/caja_chica/reciboCajaChicaUOMAContenedor.jasper";
	private static final String CAJA_CHICA_RECIBO_OSPIM = "jasper/caja_chica/reciboCajaChicaOspim.jasper";
	private static final String CAJA_CHICA_RECIBO_PDF_FILENAME = "ReciboCajaChica.pdf";	
	
	private static final String RPTRECLAMOPRESTACIONAL= "jasper/reclamo_prestacionales/reclamoprestacional.jasper";
	private static final String RPTRECLAMOPRESTACIONAL_PDF_FILENAME = "ReclamoPrestacional.pdf";
	
	private static final String RPTEQUIPODISCIPLINARIO = "jasper/equipo_interdisciplinario/equipointer.jasper";
	private static final String RPTEQUIPOINTERDISCIPLINARIO_PDF_FILENAME = "EquipoInterdisciplinario.pdf";
	
	
	private static final String RPTRECLAMOPRESTACIONALLOTE= "jasper/reclamo_prestacionales/reclamos_prestacionales_por_lote.jasper";
	private static final String RPTRECLAMOPRESTACIONAL_PDF_FILENAME_LOTE = "ReclamoPrestacionalLote.pdf";
	
	private static final String UOMA_FACTURA= "jasper/facturacion/factura.jasper";
	private static final String UOMA_FACTURA_PDF_FILENAME = "factura.pdf";
	
	private static final String RESUMEN_LIQUIDACION_RESERVA= "jasper/hoteles/resumen_gastos_por_reserva.jasper";
	private static final String RESUMEN_LIQUIDACION_RESERVA_PDF_FILENAME = "reserva_resumen.pdf";
	
	private static final String RESUMEN_LIQUIDACION_RESERVA_GENERAL= "jasper/hoteles/resumen_gastos_por_reserva_general.jasper";
	private static final String RESUMEN_LIQUIDACION_RESERVA_GENERAL_PDF_FILENAME = "reserva_resumen_general.pdf";
	
	private static final String RESUMEN_GOBERNANTA_RESERVA= "jasper/hoteles/resumen_hab_gobernanta.jasper";
	private static final String RESUMEN_GOBERNANTA_RESERVA_PDF_FILENAME = "habitaciones_gobernanta.pdf";
	
	private static final String ESTADISTICA_DESAYUNO_HOTELES= "jasper/hoteles/estadistica_desayunos.jasper";
	private static final String ESTADISTICA_DESAYUNO_HOTELES_PDF_FILENAME = "estadistica_desayunos.pdf";
	
	public static final String RECIBO_OSPIM = "jasper/tesoreria/recibo_ospim.jasper";
	public static final String RECIBO_OSPIM_PDF_FILENAME = "ReciboOspim_xxx.pdf";
	
	public static final String RECIBO_AMTIMA = "jasper/tesoreria/recibo_amtima.jasper";
	public static final String RECIBO_AMTIMA_PDF_FILENAME = "ReciboAmtima_xxx.pdf";
	
	public static final String RECIBO_UOMA = "jasper/tesoreria/recibo_uoma.jasper";
	public static final String RECIBO_UOMA_PDF_FILENAME = "ReciboUoma_xxx.pdf";
	
	private static final String SITUACION_MEDICA_ANTICONCEPCION = "jasper/situacion_medica/anticoncepcion_ospim.jasper";
	private static final String SITUACION_MEDICA_ANTICONCEPCION_PDF_FILENAME = "Formulario Anticoncepción Ospim.pdf";
	
	private static final String SITUACION_MEDICA_CRONICOS = "jasper/situacion_medica/cronicos_ospim.jasper";
	private static final String SITUACION_MEDICA_CRONICOS_PDF_FILENAME = "Formulario Crónicos Ospim.pdf";

	private static final String REQUERIMIENTO_COMPRA =
			"jasper/compras/requerimiento_compra.jasper";

	private static final String REQUERIMIENTO_COMPRA_PDF_FILENAME =
			"RequerimientoCompra.pdf";

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String accion = ParamUtil.getString(req, "accion");

		if ("requerimientoCompra".equals(accion)) {
			generaRequerimientoCompra(req, res);
			return;
		}

		if (accion.equals("credencial")) {
			generaCredencial(req, res);
		}
		

		if (accion.equals("credencialExentoCoPago")) {
			generarCredencialExentoCoPago(req, res);
		}

		if (accion.equals("credencialCES")) {
			generarCredencialCES(req, res);
		}
		
		if (accion.equals("cheque")) {
			generaCheque(req, res);
		}		

		if (accion.equals("ordenPago")) {
			generaOrdenPago(req, res);
		}
		
		if (accion.equals("ordenPagoFarmacia")) {
			generaOrdenPagoFarmacia(req, res);
		}
		
		if (accion.equals("ordenPagoUoma")) {
			generaOrdenPagoUoma(req, res);
		}
		
		if (accion.equals("ordenPagoOspimFarmacia")) {
			generaOrdenPagoOspimFarmacia(req, res);
		}
		
		if (accion.equals("ordenPagoOspim")) {
			generaOrdenPagoOspim(req, res);
		}

		if (accion.equals("notaDebitoLiquidacion")) {
			generaNotaDebito(req, res);
		}
		if(accion.equals("notaDebitoLiquidacionxOP")){
			generaNotaDebitoXOp(req, res);
		}
		
		if (accion.equals("documentacionFaltante")) {
			generaDocumentacionFaltante(req, res);
		}
		if (accion.equals("autorizacionTratamiento")) {
			autorizacionTratamiento(req, res);
		}	
		
		if (accion.equals("autorizacionRecetaPmi")) {
			autorizacionRecetaPmi(req, res);
		}
		
		if (accion.equals("comproRetenGanancias")){
			generaComprobanteRetencion(req, res);
		}
		
		if (accion.equals("comproRetenIIBB")){
			generaComprobanteRetencionIIBB(req, res);
		}
		
		if (accion.equals("comproRetenIVA")){
			generaComprobanteRetencionIVA(req, res);
		}
		
		if (accion.equals("tarjetacoordenadas")) {
			generaTarjetaCoordenadas(req, res);
		}
		
		if (accion.equals("recibocajachica")) {
			generaReciboCajaChica(req, res);
		}
		
		
		if (accion.equals("reclamoprestacional")) {
			generaRptReclamoPrestacional(req, res);
		}
		
		if (accion.equals("equipointerdisciplinario")) {
			generaRptEquipoInterdisciplinario(req, res);
		}
		
		if (accion.equals("ordenPagoOspimIntegracion")) {
			generaOrdenPagoOspimIntegracion(req, res);
		}
		
		if (accion.equals("reclamoprestacionallote")) {
			generaRptReclamoPrestacionalLote(req, res);
		}
		
		if (accion.equals("generarFacturaUOMA")) {
			generaPdfFacturaUOMA(req, res);
		}
		
		if (accion.equals("resumenliquidacionreserva")) {
			generaPdfResumenLiquidacionReserva(req, res);
		}
		
		if (accion.equals("resumenliquidacionreservageneral")) {
			try {
				generaPdfResumenLiquidacionReservaGeneral(req, res);
			} catch (Exception e) {
				_log.error(e);
			} 
		}
		
		if (accion.equals("reportediariohabitacionesgobernanta")) {
			try {
				generaPdfHabitacionesGobernanta(req, res);
			} catch (Exception e) {
				_log.error(e);
			} 
		}
		
		if (accion.equals("estadisticadesayunohoteles")) {
			try {
				generaEstadisticaDesayunoHoteles(req, res);
			} catch (Exception e) {
				_log.error(e);
			} 
		}
		
		if (accion.equals("reciboIngresoOspim")) {
			generaReciboIngresoOspim(req, res);
		}
		if (accion.equals("reciboIngresoAmtima")) {
			generaReciboIngresoAmtima(req, res);
		}

		if (accion.equals("reciboIngresoUoma")) {
			generaReciboIngresoUoma(req, res);
		}
		
		if (accion.equals("situacionMedicaPdf")) {
		    generaSituacionMedicaPdf(req, res);
		}
	}

	private void generaOrdenPagoOspim(HttpServletRequest req,
			HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_ini");		
		HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("id_ini", id_orden_pagoIni);		
		hm.put("ID_ORDEN_PAGO", id_orden_pagoIni);
        hm.put("SUBREPORT_DIR", "jasper/");
		hm.put("pathimage", "jasper/firma_carolina.jpg");
		crearPdf(req, res, ORDEN_PAGO_OSPIM, hm, ORDEN_PAGO_OSPIM_PDF_FILENAME);
	}
	
	private void generaOrdenPagoOspimFarmacia(HttpServletRequest req,
			HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_ini");		
		String id_orden_pagoFin = ParamUtil.getString(req, "id_fin");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_op_ini", id_orden_pagoIni);
		hm.put("id_op_fin", id_orden_pagoFin);		
		crearPdf(req, res, ORDEN_PAGO_OSPIM_FARMACIA, hm, ORDEN_PAGO_OSPIM_PDF_FILENAME);
	}

	private void generaNotaDebito(HttpServletRequest req,
			HttpServletResponse res) {
		String id_nota_debitoIni = ParamUtil.getString(req, "id_liquidacion");
		String terceros = ParamUtil.getString(req, "terceros", "0"); // "1" liquidacion debitos terceros, "0" no
		String importe_terceros = ParamUtil.getString(req, "importe_terceros", "0"); //importe total terceros, o cero
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_ini", id_nota_debitoIni);
		hm.put("terceros", terceros);
		hm.put("importe_terceros", importe_terceros);		
		crearPdf(req, res, NOTA_DEBITO_LIQUIDACION, hm, NOTA_DEBITO_LIQUIDACION_PDF_FILENAME);
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
		crearPdf(req, res, DOCUMENTACION_FALTANTE, hm, DOCUMENTACION_FALTANTE_PDF_FILENAME);
	}
	
	private void autorizacionRecetaPmi(HttpServletRequest req,
			HttpServletResponse res) {
		String id_autorizacion_pmi = ParamUtil.getString(req, "id_autorizacion_pmi");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_autorizacion_pmi", id_autorizacion_pmi);
		hm.put("SUBREPORT_DIR", "jasper/");
		crearPdf(req, res, AUTORIZACION_RECETAS_PMI_JASPER, hm, AUTORIZACION_RECETAS_PMI_PDF_FILENAME);
	}	
	
	private void autorizacionTratamiento(HttpServletRequest req,
			HttpServletResponse res) {
		String id_tratamiento = ParamUtil.getString(req, "id_tratamiento", "0");				
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_ini", id_tratamiento);
		hm.put("SUBREPORT_DIR", "jasper/");
		crearPdf(req, res, AUTORIZACION_TRATAMIENTO_JASPER, hm, AUTORIZACION_TRATAMIENTO_ODT_FILENAME);
	}
	
	private void generaCheque(HttpServletRequest req, HttpServletResponse res) {
		String numero = ParamUtil.getString(req, "numero");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("numero", numero);
		crearPdf(req, res, CHEQUES, hm, CHEQUE_PDF_FILENAME);
	}

	private void generaOrdenPago(HttpServletRequest req, HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_orden_pago_ini");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_ini", id_orden_pagoIni);
		crearPdf(req, res, ORDEN_PAGO, hm, ORDEN_PAGO_PDF_FILENAME);
	}
	
	private void generaOrdenPagoUoma(HttpServletRequest req, HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_orden_pago_ini");		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("ID_ORDEN_PAGO", id_orden_pagoIni);		
		crearPdf(req, res, ORDEN_PAGO_UOMA, hm,"OrdenPagoUoma_" +id_orden_pagoIni+".pdf");
	}
	
	private void generaOrdenPagoFarmacia(HttpServletRequest req, HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_orden_pago_ini");
		String id_orden_pagoFin = ParamUtil.getString(req, "id_orden_pago_fin");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_ini", id_orden_pagoIni);
		hm.put("id_fin", id_orden_pagoFin);
		crearPdf(req, res, ORDEN_PAGO_FARMACIA, hm, ORDEN_PAGO_FARMACIA_PDF_FILENAME);
	}
	
	private void generaOrdenPagoOspimIntegracion(HttpServletRequest req, HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "op_desde");
		String id_orden_pagoFin = ParamUtil.getString(req, "op_hasta");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("P_INI", id_orden_pagoIni);
		hm.put("P_FIN", id_orden_pagoFin);
		hm.put("SUBREPORT_DIR", "jasper/orden_pago/");
		crearPdf(req, res, ORDEN_PAGO_OSPIM_INTEGRACION, hm, ORDEN_PAGO_OSPIM_PDF_FILENAME);
	}
	
	private void generaCredencial(HttpServletRequest req,
			HttpServletResponse res) {
		String id_lote = ParamUtil.getString(req, "id_lote");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_lote", id_lote);
		Date vto =DateUtils.getLastDateOfMonth(new Date(), true);
		
		Calendar calendar = Calendar.getInstance(); 
	    calendar.setLenient(false);
	    calendar.setTime(vto); 
	    calendar.add(calendar.MONTH, 1);  
		
		hm.put("vto", new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		hm.put("SUBREPORT_DIR", "jasper/");
		crearPdf(req, res, CREDENCIALES, hm, CREDENCIAL_PDF_FILENAME);

	}
	
	private void generarCredencialExentoCoPago(HttpServletRequest req,
			HttpServletResponse res) {
		String cuil_titular = ParamUtil.getString(req, "cuil");
		String inte = ParamUtil.getString(req, "inte");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("cuil_titular", cuil_titular);
		hm.put("inte", inte);
		hm.put("SUBREPORT_DIR", "jasper/afiliaciones/");
		crearPdf(req, res, CREDENCIALES_EXENTO, hm, CREDENCIAL_EXENTO_PDF_FILENAME);

	}
	
	private void generarCredencialCES(HttpServletRequest req,
			HttpServletResponse res) {
		String id_lote = ParamUtil.getString(req, "id_lote");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_lote", id_lote);
		Date vto =DateUtils.getLastDateOfMonth(new Date(), true);
		
		Calendar calendar = Calendar.getInstance(); 
	    calendar.setLenient(false);
	    calendar.setTime(vto); 
//	    calendar.add(calendar.MONTH, 1);  
		
		hm.put("vto", new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		hm.put("SUBREPORT_DIR", "jasper/credencial/");
		crearPdf(req, res, CREDENCIALES_CES, hm, CREDENCIAL_CES_PDF_FILENAME);

	}
	
	private void generaComprobanteRetencion(HttpServletRequest req,
			HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_ini");
		String entidad = ParamUtil.getString(req, "entidad");	
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_op_p", id_orden_pagoIni);
		hm.put("entidad_p", entidad);		
		crearPdf(req, res, COMPROBANTE_RETEN_GANANCIAS, hm, COMPROBANTE_RETEN_GANANCIAS_PDF_FILENAME);
	}
	
	private void generaComprobanteRetencionIIBB(HttpServletRequest req,
			HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_ini");
		String entidad = ParamUtil.getString(req, "entidad");	
		String tipo = ParamUtil.getString(req, "tipo");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_op_p", id_orden_pagoIni);
		hm.put("entidad_p", entidad);	
		hm.put("tipo_p", tipo);
		hm.put("jurisdiccion_p", null);
		crearPdf(req, res, COMPROBANTE_RETEN_IIBB, hm, COMPROBANTE_RETEN_IIBB_PDF_FILENAME);
	}

	private void generaComprobanteRetencionIVA(HttpServletRequest req,
			HttpServletResponse res) {
		String id_orden_pagoIni = ParamUtil.getString(req, "id_ini");
		String entidad = ParamUtil.getString(req, "entidad");	
		String tipo = ParamUtil.getString(req, "tipo");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_op_p", id_orden_pagoIni);
		hm.put("entidad_p", entidad);	
		hm.put("tipo_p", tipo);
		hm.put("jurisdiccion_p", null);
		crearPdf(req, res, COMPROBANTE_RETEN_IVA, hm, COMPROBANTE_RETEN_IVA_PDF_FILENAME);
	}
	
	
	private void generaTarjetaCoordenadas(HttpServletRequest req,
			HttpServletResponse res) {
		String id_user = ParamUtil.getString(req, "id_user");
		
		try {
			User usr = UserServiceUtil.getUserById(Long.parseLong(id_user));
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("usuario_id", id_user);
			hm.put("destinatario", usr.getLastName() +","+usr.getFirstName());
			crearPdf(req, res, TARJETACOORDENADAS, hm,  TARJETACOORDENADAS_PDF_FILENAME);
		} catch (NumberFormatException e) {
			_log.error(e);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
	}

	
	private void generaRptReclamoPrestacional(HttpServletRequest req,
			HttpServletResponse res) {
		String id_reclamo = ParamUtil.getString(req, "idreclamo");		
		try {			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("idreclamo", id_reclamo );
			hm.put("SUBREPORT_DIR", "jasper/reclamo_prestacionales/");
			hm.put("pathimage", "jasper/logo_negro.jpg");
			crearPdf(req, res, RPTRECLAMOPRESTACIONAL, hm,  RPTRECLAMOPRESTACIONAL_PDF_FILENAME );
		} catch (NumberFormatException e) {
			_log.error(e);
		}
	}
	
	private void generaRptEquipoInterdisciplinario(HttpServletRequest req,
			HttpServletResponse res) {
		String id_equipo= ParamUtil.getString(req, "idequipo");		
		try {			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("idequipo", id_equipo);
			hm.put("SUBREPORT_DIR", "jasper/equipo_interdisciplinario/");
			hm.put("pathimage", "jasper/logo_negro.jpg");
			crearPdf(req, res, RPTEQUIPODISCIPLINARIO , hm,  RPTEQUIPOINTERDISCIPLINARIO_PDF_FILENAME );
		} catch (NumberFormatException e) {
			_log.error(e);
		}
	}

	private void crearPdf(HttpServletRequest req, HttpServletResponse res,
	                      String jasperFile, HashMap<String, String> params,
	                      String outPdfFileName) {

		Connection con = null;
		InputStream in = null;

		try {
			con = ConnectionHelper.getConnection();
			in = getClass().getClassLoader().getResourceAsStream(jasperFile);

			if (in == null) {
				_log.error("No se encontro jasper en classpath: " + jasperFile);
				res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"No se encontro jasper: " + jasperFile);
				return;
			}

			_log.info("Generando PDF. jasperFile=" + jasperFile + " params=" + params);

			JasperPrint print = JasperFillManager.fillReport(in, params, con);

			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + outPdfFileName + "\"");
			res.setHeader("Cache-Control", "no-cache");

			OutputStream outStream = res.getOutputStream();
			JasperExportManager.exportReportToPdfStream(print, outStream);
			outStream.flush();
			outStream.close();

		} catch (Throwable t) {
			_log.error("Error grave generando PDF. jasperFile=" + jasperFile + " params=" + params, t);
			try {
				if (!res.isCommitted()) {
					res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Error generando PDF: " + jasperFile);
				}
			} catch (IOException ioe) {
				_log.error("Error enviando respuesta de error", ioe);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					_log.error("Error cerrando jasper input stream", e);
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					_log.error("Error cerrando conexion", e);
				}
			}
		}
	}
	
	private void generaNotaDebitoXOp(HttpServletRequest req,
			HttpServletResponse res) {
		String liquidaciones = ParamUtil.getString(req, "id_liquidaciones");			
		crearPdfFromByteArray(req, res, crearPdfsNotaDebito(liquidaciones), NOTA_DEBITO_LIQUIDACION_PDF_FILENAME);
	}
	
	
	private void generaReciboCajaChica(HttpServletRequest req,
			HttpServletResponse res) {
		String entidad = ParamUtil.getString(req, "entidad");
		String id_caja_chica = ParamUtil.getString(req, "id_caja_chica");
		String id_seccional = ParamUtil.getString(req, "id_seccional");
		String des_seccional = ParamUtil.getString(req, "des_seccional");
		String nombre = ParamUtil.getString(req, "nombre");
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id_caja_chica", id_caja_chica);
		hm.put("id_seccional", id_seccional);
		hm.put("entidad", entidad);	
		hm.put("SUBREPORT_DIR", "jasper/");
		hm.put("des_seccional", des_seccional);
		hm.put("nombre", nombre);
		
		crearPdf(req, res,Integer.parseInt(entidad)==WebKeysGlobal.UOMA ? CAJA_CHICA_RECIBO_UOMA:CAJA_CHICA_RECIBO_OSPIM, hm, CAJA_CHICA_RECIBO_PDF_FILENAME);
	}
	
	
	private void crearPdfFromByteArray(HttpServletRequest req, HttpServletResponse res,
			byte[] out,	String outPdfFileName) {		
		try {		
			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", "attachment; filename=\""
					+ outPdfFileName + "\"");
			res.setHeader("Cache-Control", "no-cache");

			OutputStream outStream = res.getOutputStream();
			outStream.write(out);
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			_log.error(e);
		} 
	}
	
	public byte[] crearPdfsNotaDebito(String liquidaciones) {
		byte[] bytes = null;
		Connection con = ConnectionHelper.getConnection();
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				NOTA_DEBITO_LIQUIDACION);
		try {			
			
			StringTokenizer tokenizer=new StringTokenizer(liquidaciones,"|");			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(in);
			
			List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>(); 
			while(tokenizer.hasMoreTokens()){				
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("id_ini", tokenizer.nextToken());
				hm.put("terceros", "0");
				hm.put("importe_terceros", "0");	
				JasperPrint jasperPrint=JasperFillManager.fillReport(jasperReport, hm, con);
				if(jasperPrint.getPages().size()>1){
					jasperPrints.add(jasperPrint);
				}
			}
			JRPdfExporter exporter = new JRPdfExporter();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrints);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
			bytes = out.toByteArray();
					
			return bytes;
			
		} catch (Exception e) {			
			_log.error(e);
			return null;
		}
		
	}
	
	
	private void generaRptReclamoPrestacionalLote(HttpServletRequest req,
			HttpServletResponse res) {
		String nroLote = ParamUtil.getString(req, "nrolote");		
		try {			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("p_nroLote", nroLote );
			hm.put("SUBREPORT_DIR", "jasper/reclamo_prestacionales/");
			hm.put("pathimage", "jasper/logo_negro.jpg");
			crearPdf(req, res, RPTRECLAMOPRESTACIONALLOTE, hm,  RPTRECLAMOPRESTACIONAL_PDF_FILENAME_LOTE );
		} catch (NumberFormatException e) {
			_log.error(e);
		}
	}
	
	private void generaPdfFacturaUOMA(HttpServletRequest req,
			HttpServletResponse res) {
		
		String idFactura = ParamUtil.getString(req, "id_factura");		
		String idEjemplar = ParamUtil.getString(req, "id_ejemplar");
		String mostrarRazSoc = ParamUtil.getString(req, "mostrarRazonSoc");
		
		try {			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("id_fc_p", idFactura );
			hm.put("id_ejemplar", idEjemplar );
			hm.put("imprimeRazonSocial", mostrarRazSoc );
			hm.put("SUBREPORT_DIR", "jasper/facturacion/");
			hm.put("pathimage", "jasper/logo_negro.jpg");
			crearPdf(req, res, UOMA_FACTURA, hm,  UOMA_FACTURA_PDF_FILENAME );
		} catch (NumberFormatException e) {
			_log.error(e);
		}
	}
	
	
	private void generaPdfResumenLiquidacionReserva(HttpServletRequest req,
			HttpServletResponse res) {
		String reserva = ParamUtil.getString(req, "reserva");
		String anio = ParamUtil.getString(req, "anio");
		String hotel = ParamUtil.getString(req, "hotel");		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("reserva_p", reserva);
		hm.put("anio_p", anio);	
		hm.put("hotel_p", hotel);	
		hm.put("pathimage", "jasper");
		crearPdf(req, res, RESUMEN_LIQUIDACION_RESERVA, hm, RESUMEN_LIQUIDACION_RESERVA_PDF_FILENAME);
	}
	
	
	private void generaPdfResumenLiquidacionReservaGeneral(HttpServletRequest req,
			HttpServletResponse res) throws NumberFormatException, ParseException {
		
		String reserva = ParamUtil.getString(req, "reserva");
		String anio = ParamUtil.getString(req, "anio");
		String hotel = ParamUtil.getString(req, "hotel");
		String habitacion = ParamUtil.getString(req, "habitacion");
		
		String diaDesde = ParamUtil.getString(req, "fechadesdedia");
		String mesDesde = ParamUtil.getString(req, "fechadesdemes");
		String anioDesde = ParamUtil.getString(req, "fechadesdeanio");
		
		String diaHasta = ParamUtil.getString(req, "fechahastadia");
		String mesHasta = ParamUtil.getString(req, "fechahastames");
		String anioHasta = ParamUtil.getString(req, "fechahastaanio");
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date fechaDde = formatoDeFechas.parse(diaDesde +"/"+(Integer.parseInt(mesDesde) + 1)+"/"+anioDesde);
		Date fechaHta = formatoDeFechas.parse(diaHasta +"/"+(Integer.parseInt(mesHasta) + 1)+"/"+anioHasta);
		HashMap<String, String> hm = new HashMap<String, String>();
		if("".equalsIgnoreCase(reserva)) reserva="0";
		if("".equalsIgnoreCase(habitacion)) habitacion="";
		hm.put("reserva_p", reserva);
		hm.put("anio_p", anio);	
		hm.put("hotel_p", hotel);
		hm.put("habitacion_p", habitacion);
		hm.put("fechadde_p", sdf.format(fechaDde));
		hm.put("fechahta_p", sdf.format(fechaHta));
		hm.put("pathimage", "jasper");
		hm.put("SUBREPORT_DIR", "jasper/hoteles/");
		crearPdf(req, res, RESUMEN_LIQUIDACION_RESERVA_GENERAL, hm, RESUMEN_LIQUIDACION_RESERVA_GENERAL_PDF_FILENAME);
	}
	
	private void generaPdfHabitacionesGobernanta(HttpServletRequest req,
			HttpServletResponse res) throws NumberFormatException, ParseException {

		String anio = ParamUtil.getString(req, "anio");

		String diaDesde = ParamUtil.getString(req, "fechadesdedia");
		String mesDesde = ParamUtil.getString(req, "fechadesdemes");
		String anioDesde = ParamUtil.getString(req, "fechadesdeanio");
		Boolean piso1 = ParamUtil.getBoolean(req, "piso1");
		Boolean piso2 = ParamUtil.getBoolean(req, "piso2");
		Boolean piso3 = ParamUtil.getBoolean(req, "piso3");
		Boolean piso4 = ParamUtil.getBoolean(req, "piso4");
		Boolean piso5 = ParamUtil.getBoolean(req, "piso5");
		Boolean piso6 = ParamUtil.getBoolean(req, "piso6");
		Boolean piso7 = ParamUtil.getBoolean(req, "piso7");
		Boolean piso8 = ParamUtil.getBoolean(req, "piso8");
		Boolean piso9 = ParamUtil.getBoolean(req, "piso9");
		Boolean piso10 = ParamUtil.getBoolean(req, "piso10");
		
		String pisosSeleccionados = "";
		
		if(piso1) {
			pisosSeleccionados = pisosSeleccionados + "1,";
		}
		if(piso2) {
			pisosSeleccionados = pisosSeleccionados + "2,";
		}
		if(piso3) {
			pisosSeleccionados = pisosSeleccionados + "3,";
		}
		if(piso4) {
			pisosSeleccionados = pisosSeleccionados + "4,";
		}
		if(piso5) {
			pisosSeleccionados = pisosSeleccionados + "5,";
		}
		if(piso6) {
			pisosSeleccionados = pisosSeleccionados + "6,";
		}
		if(piso7) {
			pisosSeleccionados = pisosSeleccionados + "7,";
		}
		if(piso8) {
			pisosSeleccionados = pisosSeleccionados + "8,";
		}
		if(piso9) {
			pisosSeleccionados = pisosSeleccionados + "9,";
		}
		if(piso10) {
			pisosSeleccionados = pisosSeleccionados + "10,";
		}
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date fechaDde = formatoDeFechas.parse(diaDesde +"/"+(Integer.parseInt(mesDesde) + 1)+"/"+anioDesde);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("anio_p", anio);	
		hm.put("fecha_p", sdf.format(fechaDde));
		hm.put("pisosSeleccionados_p", pisosSeleccionados);
		hm.put("path_image", "jasper");
		hm.put("SUBREPORT_DIR", "jasper/hoteles/");
		crearPdf(req, res, RESUMEN_GOBERNANTA_RESERVA, hm, RESUMEN_GOBERNANTA_RESERVA_PDF_FILENAME);
	}
	
	
	private void generaEstadisticaDesayunoHoteles(HttpServletRequest req,
			HttpServletResponse res) throws NumberFormatException, ParseException {
/*		
		String reserva = ParamUtil.getString(req, "reserva");
		String anio = ParamUtil.getString(req, "anio");
		String habitacion = ParamUtil.getString(req, "habitacion");
*/		
		
		String hotel = ParamUtil.getString(req, "hotel");
		String diaDesde = ParamUtil.getString(req, "fechadesdedia");
		String mesDesde = ParamUtil.getString(req, "fechadesdemes");
		String anioDesde = ParamUtil.getString(req, "fechadesdeanio");
		
		String diaHasta = ParamUtil.getString(req, "fechahastadia");
		String mesHasta = ParamUtil.getString(req, "fechahastames");
		String anioHasta = ParamUtil.getString(req, "fechahastaanio");
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date fechaDde = formatoDeFechas.parse(diaDesde +"/"+(Integer.parseInt(mesDesde) + 1)+"/"+anioDesde);
		Date fechaHta = formatoDeFechas.parse(diaHasta +"/"+(Integer.parseInt(mesHasta) + 1)+"/"+anioHasta);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaDde);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		fechaDde =calendar.getTime();
		
		
		HashMap<String, String> hm = new HashMap<String, String>();
//		if("".equalsIgnoreCase(reserva)) reserva="0";
//		if("".equalsIgnoreCase(habitacion)) habitacion="";
//		hm.put("reserva_p", reserva);
//		hm.put("anio_p", anio);	
		hm.put("hotel_p", hotel);
//		hm.put("habitacion_p", habitacion);
		hm.put("desde_p", sdf.format(fechaDde));
		hm.put("hasta_p", sdf.format(fechaHta));
		hm.put("pathimage", "jasper");
//		hm.put("SUBREPORT_DIR", "jasper/hoteles/");
		crearPdf(req, res, ESTADISTICA_DESAYUNO_HOTELES, hm, ESTADISTICA_DESAYUNO_HOTELES_PDF_FILENAME);
	}
	
	
	public byte[] crearPdfComoAdjunto(String jasperFile, HashMap<String, String> params,
			String outPdfFileName) { 
		Connection con = ConnectionHelper.getConnection();
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				jasperFile);

		try {
			JasperPrint print = JasperFillManager.fillReport(in, params, con);	
			return JasperExportManager.exportReportToPdf(print);
			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				in.close();
				con.close();
			} catch (SQLException e) {
				_log.error("Error cerrando conexion", e);
			} catch (IOException e) {
				_log.error("Error cerrando conexion", e);			}
		}
		return null;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}
	
	private void generaReciboIngresoOspim(HttpServletRequest req,
			HttpServletResponse res) {
		String id_recibo = ParamUtil.getString(req, "id");		
		String cuit = ParamUtil.getString(req, "cuit");	
		HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("id_ini", id_orden_pagoIni);		
		hm.put("ID_RECIBO", id_recibo);
		String nameFile = RECIBO_OSPIM_PDF_FILENAME;
		nameFile = nameFile.replace("xxx", cuit);
		crearPdf(req, res, RECIBO_OSPIM, hm, nameFile);
	}
	
	private void generaReciboIngresoAmtima(HttpServletRequest req,
			HttpServletResponse res) {
		String id_recibo = ParamUtil.getString(req, "id");		
		String cuit = ParamUtil.getString(req, "cuit");	
		HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("id_ini", id_orden_pagoIni);		
		hm.put("ID_RECIBO", id_recibo);
		String nameFile = RECIBO_AMTIMA_PDF_FILENAME;
		nameFile = nameFile.replace("xxx", cuit);
		crearPdf(req, res, RECIBO_AMTIMA, hm, nameFile);
	}
	
	private void generaReciboIngresoUoma(HttpServletRequest req,
			HttpServletResponse res) {
		String id_recibo = ParamUtil.getString(req, "id");		
		String cuit = ParamUtil.getString(req, "cuit");	
		HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("id_ini", id_orden_pagoIni);		
		hm.put("ID_RECIBO", id_recibo);
		String nameFile = RECIBO_UOMA_PDF_FILENAME;
		nameFile = nameFile.replace("xxx", cuit);
		crearPdf(req, res, RECIBO_UOMA, hm, nameFile);
	}
	
	
	public byte[] crearPdfsOrdenPago(Integer idOP) {
		byte[] bytes = null;
		Connection con = ConnectionHelper.getConnection();
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				ORDEN_PAGO_OSPIM_INTEGRACION);
		try {			
			
			String idOPStr = String.valueOf(idOP);
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(in);
			
			List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>(); 
			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("P_INI", idOPStr);
			hm.put("P_FIN", idOPStr);
			hm.put("SUBREPORT_DIR", "jasper/orden_pago/");
			JasperPrint jasperPrint=JasperFillManager.fillReport(jasperReport, hm, con);
			if(jasperPrint.getPages().size()>1){
					jasperPrints.add(jasperPrint);
			}
			
			JRPdfExporter exporter = new JRPdfExporter();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrints);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
			bytes = out.toByteArray();
					
			return bytes;
			
		} catch (Exception e) {			
			_log.error(e);
			return null;
		}
		
	}
	
	private void generaSituacionMedicaPdf(HttpServletRequest req, HttpServletResponse res) {
	    String idSituacion = ParamUtil.getString(req, "id_situacion", "0");

	    try {
	        SituacionMedica situacionMedica =
	            SituacionesMedicasServiceUtil.getSituacionMedica(
	                Integer.valueOf(idSituacion),
	                null,
	                0
	            );

	        if (situacionMedica == null || situacionMedica.getIdTipoSituMedica() == 0) {
	            _log.error("No se encontro situacion medica para id: " + idSituacion);
	            return;
	        }

	        SituacionesMedicasServiceUtil.generarFormularioSiNoExiste(
	            situacionMedica.getIdSituacionMedica(),
	            situacionMedica.getIdTipoSituMedica(),
	            "pdfservlet"
	        );

	        String jasperFile = null;
	        String pdfFileName = null;

	        switch (situacionMedica.getIdTipoSituMedica()) {

	            case 1:
	                jasperFile = SITUACION_MEDICA_ANTICONCEPCION;
	                pdfFileName = SITUACION_MEDICA_ANTICONCEPCION_PDF_FILENAME;
	                break;
	                
	            case 6:
	                jasperFile = SITUACION_MEDICA_CRONICOS;
	                pdfFileName = SITUACION_MEDICA_CRONICOS_PDF_FILENAME;
	                break;

	            default:
	                _log.error(
	                    "No hay reporte PDF configurado para tipo_situ_medica: "
	                    + situacionMedica.getIdTipoSituMedica()
	                    + " id_situacion: "
	                    + idSituacion
	                );
	                return;
	        }

	        HashMap<String, String> hm = new HashMap<String, String>();
	        hm.put("ID_SITUACION", idSituacion);
	        hm.put("LOGO_OSPIM", "jasper/situacion_medica/logo_ospim.png");
	        hm.put("SUBREPORT_DIR", "jasper/situacion_medica/");

	        crearPdf(
	            req,
	            res,
	            jasperFile,
	            hm,
	            pdfFileName
	        );

	    } catch (Exception e) {
	        _log.error(
	            "Error generando PDF de situacion medica. id_situacion: " + idSituacion,
	            e
	        );
	    }
	}

	private void generaRequerimientoCompra(HttpServletRequest req,
	                                       HttpServletResponse res) throws IOException {

		int idRequerimiento = ParamUtil.getInteger(req, "id_requerimiento", 0);

		if (idRequerimiento <= 0) {
			res.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Parametro id_requerimiento obligatorio"
			);
			return;
		}

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("ID_REQUERIMIENTO", String.valueOf(idRequerimiento));

		crearPdf(
				req,
				res,
				REQUERIMIENTO_COMPRA,
				hm,
				"RequerimientoCompra_" + idRequerimiento + ".pdf"
		);
	}
}
