package ar.com.ospim.servlets;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.global.reportes.ReportesSIAPTxt;
import ar.com.ospim.afiliados.reportes.ReporteListadosSSTxt;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.CuentasInterbaking;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.OrdenPagoConError;
import ar.com.ospim.autorizaciones.beans.PagosInterbanking;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.constantes.interbaking.ConstantesInterbanking;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.OrdenesPagoInterbanking;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteIntegracion;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.FormaPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class TxtServlet extends HttpServlet {
	private static Log _log = LogFactoryUtil.getLog(TxtServlet.class);
	private static final long serialVersionUID = 1L;

	private static final String REPORTE_LISTADO_SS = "REPORTE_LISTADO_SS";
	private static final String REPORTE_DERIVA_DESREGULADOS = "REPORTE_DERIVA_DESREGULADOS";
	private static final String REPORTE_AFILIADOS_SIN_APORTE = "REPORTE_AFILIADOS_SIN_APORTE";
	private static final String REPORTE_VIEJO_SIST_UOMA = "REPORTE_VIEJO_SIST_UOMA";
	private static final String EXPORTACION_NUEVAS_OPCIONES_SSS = "EXPORTACION_NUEVAS_OPCIONES_SSS";
	private static final String REPORTE_RETENCION_GANANCIAS = "REPORTE_RETENCION_GANANCIAS";
	private static final String REPORTE_COMISIONES_TERCERIZADORAS = "REPORTE_COMISIONES_TERCERIZADORAS";// "REPORTE_COMISIONES_PREVENCION";
	private static final String INTEGRACION_EXPORTAR_FTP = "INTEGRACION_EXPORTAR_FTP";
	private static final String EXPORTAR_CUENTAS_INTERBANKING = "EXPORTAR_CUENTAS_INTERBANKING";
	private static final String PERCEPCIONES_IIBB = "PERCEPCIONES_IIBB";
	private static final String RG3685_LIBRO_IVA_CPTES = "RG3685_LIBRO_IVA_CPTES";
	private static final String RG3685_LIBRO_IVA_ALICUOTAS = "RG3685_LIBRO_IVA_ALICUOTAS";
	private static final String INTEGRACION_EXPORTAR_RENDICION = "INTEGRACION_EXPORTAR_RENDICION";

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String TMPDIR = System.getProperty("java.io.tmpdir");
	private static final String EXPORTAR_CUENTAS_INTERBANKING_OPS = "EXPORTAR_CUENTAS_INTERBANKING_OPS";
	private static final String INTERBANKING_OPS = "INTERBANKING_OPS";
	private static final String PERCEPCIONES_ARBA_IIBB = "PERCEPCIONES_ARBA_IIBB";
	private static final String RETENCIONES_ARBA_IIBB = "RETENCIONES_ARBA_IIBB";
	private static final String RECLAMOS_EXPORTAR_IMAGENES = "RECLAMOS_EXPORTAR_IMAGENES";
	private static final String LIQUIDACIONES_EXPORTAR_IMAGENES = "LIQUIDACIONES_EXPORTAR_IMAGENES";
	private static final String PEDIDO_INFORME_JUBILADOS = "PEDIDO_INFORME_JUBILADOS";

	private static final String CAJA_CHICA_EXPORTAR_IMAGENES = "CAJA_CHICA_EXPORTAR_IMAGENES";
	private static final String ORDENES_PAGO_EXPORTAR_IMAGENES = "ORDENES_PAGO_EXPORTAR_IMAGENES";
	private static final String PREAUTORIZACIONES_EXPORTAR_IMAGENES = "PREAUTORIZACIONES_EXPORTAR_IMAGENES";
	private static final String COMPROBANTES_INTEGRACION_EXPORTAR_IMAGENES = "COMPROBANTES_INTEGRACION_EXPORTAR_IMAGENES";

	private static final String EXPORTAR_CUENTAS_INTERBANKING_EMAIL = "EXPORTAR_CUENTAS_INTERBANKING_EMAIL";
	private static final String EXPORTAR_CUENTAS_INTERBANKING_OPS_EMAIL = "EXPORTAR_CUENTAS_INTERBANKING_OPS_EMAIL";
	
	private static final String INTERBANKING_OPS_EMAIL = "INTERBANKING_OPS_EMAIL";
	private static final String R331_ZIP = "R331_ZIP";
	private static final String RETENCIONES_ARBA_IIBB_A122 = "RETENCIONES_ARBA_IIBB_A122";
	

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Calendar calendar1 = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfecha = new SimpleDateFormat("yyyyMMdd");

		String accion = ParamUtil.getString(req, "reporte");
		try {

			if (accion.equals(REPORTE_LISTADO_SS)) {
				String fechaHasta = ParamUtil.getString(req, "periodoDesde");
				String tipoMov = ParamUtil.getString(req, "tipo");
				SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
				SimpleDateFormat formatDateFile = new SimpleDateFormat("ddMMyyyy");
				Calendar cal = null;
				try {
					Date fechaFin = format
							.parse(Integer.parseInt(fechaHasta.split("_")[0]) + 1 + "-" + fechaHasta.split("_")[1]);
					cal = Calendar.getInstance();
					cal.setTime(fechaFin);
					cal.add(Calendar.MONTH, +1);
					cal.add(Calendar.DATE, -1);
				} catch (ParseException se) {

				}
				if (tipoMov.equals("ALTAS")) {
					ArrayList<String> lista = new ArrayList<String>(
							ReporteListadosSSTxt.generaReporteListadoSSAlta(req, res));
					crearTxt(req, res, lista, formatDateFile.format(cal.getTime()) + "ALTAS_SSS.txt");
				} else if (tipoMov.equals("BAJAS")) {
					ArrayList<String> lista = new ArrayList<String>(
							ReporteListadosSSTxt.generaReporteListadoSSBaja(req, res));
					crearTxt(req, res, lista, formatDateFile.format(cal.getTime()) + "BAJAS_SSS.txt");
				} else if (tipoMov.equals("MODIFICACIONES")) {
					ArrayList<String> lista = new ArrayList<String>(
							ReporteListadosSSTxt.generaReporteListadoSSModificaciones(req, res));
					crearTxt(req, res, lista, formatDateFile.format(cal.getTime()) + "MODIFICACIONES_SSS.txt");
				}
			} else if (accion.equals(REPORTE_DERIVA_DESREGULADOS)) {
				String fecha_liq = ParamUtil.getString(req, "fechaLiq");
				String id_terc = ParamUtil.getString(req, "id_terc");
				ArrayList<String> lista = new ArrayList<String>(ReporteListadosSSTxt.generaArchivoDerivaTerc(req, res));
				crearTxt(req, res, lista, fecha_liq + "OSPIM_" + id_terc + ".txt");
			} else if (accion.equals(REPORTE_AFILIADOS_SIN_APORTE)) {
				String fecha_liq = ParamUtil.getString(req, "fechaLiq");
				String id_terc = ParamUtil.getString(req, "id_terc");
				ArrayList<String> lista = new ArrayList<String>(
						ReporteListadosSSTxt.generaArchivoAfiliadosSinAporte(req, res));
				crearTxt(req, res, lista, fecha_liq + "OSPIM_SIN_APORTE" + id_terc + ".txt");
			} else if (accion.equals(REPORTE_RETENCION_GANANCIAS)) {
				Calendar periodo = Calendar.getInstance();
				periodo.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaDesdeAnio"));
				periodo.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaDesdeMes"));
				periodo.set(Calendar.DAY_OF_MONTH, ParamUtil.getInteger(req, "fechaDesdeDia"));
				String fechaStr = sdf.format(periodo.getTime());
				ArrayList<String> lista = new ArrayList<String>(ReportesSIAPTxt.generaArchivoRetenGanancias(req, res));
				crearTxt(req, res, lista, "RET_GANAN" + fechaStr + ".txt");

			} else if (accion.equals(REPORTE_VIEJO_SIST_UOMA)) {

				String vigenciaArchivoDia = ParamUtil.getString(req, "vigenciaArchivoDia");
				String vigenciaArchivoMes = ParamUtil.getString(req, "vigenciaArchivoMes");
				String vigenciaArchivoAnio = ParamUtil.getString(req, "vigenciaArchivoAnio");

				ArrayList<String> lista = new ArrayList<String>(
						ReporteListadosSSTxt.generaReporteSistemaViejo(req, res));
				crearTxt(req, res, lista, vigenciaArchivoDia + "-" + vigenciaArchivoMes + "-" + vigenciaArchivoAnio
						+ "-SistemaViejoUOMA.txt");

			} else if (accion.equals(EXPORTACION_NUEVAS_OPCIONES_SSS)) {
				ArrayList<String> lista = new ArrayList<String>();
				List<DetalleOpcionesSS> opcionesxExportar = BusquedaAfiliadoServiceUtil
						.buscarOpcionesSSSpendientesExportar();

				if (opcionesxExportar.size() > 0) {
					for (Iterator<DetalleOpcionesSS> iterator = opcionesxExportar.iterator(); iterator.hasNext();) {
						DetalleOpcionesSS detalleOpcionesSS = iterator.next();

						lista.add(detalleOpcionesSS.getRenglonExportacionSSS().toString());
					}
				} else {
					lista.add("No se encontraron nuevas opciones de SSS");
				}
				crearTxt(req, res, lista, "nuevas_opciones_sss_" + sdf.format(calendar1.getTime()) + ".txt");

			} else if (accion.equals(REPORTE_COMISIONES_TERCERIZADORAS)) {
				String fecha_liq = ParamUtil.getString(req, "fechaLiq");
				// String id_terc=ParamUtil.getString(req, "id_terc");
				ArrayList<String> lista = new ArrayList<String>(
						ReporteListadosSSTxt.generaArchivoComisionesPrevencion(req, res));
				crearTxt(req, res, lista, "comisiones_prevencion_" + fecha_liq + ".txt");
			} else if (accion.equals(INTEGRACION_EXPORTAR_FTP)) {
				String periodoStr = ParamUtil.getString(req, "periodo");
				String[] periodoV = periodoStr.split("_");
				boolean informadoFTP = ParamUtil.getBoolean(req, "informado");
				Integer periodo = Integer.parseInt(periodoV[1]) * 100 + Integer.parseInt(periodoV[0]) + 1;
				List<IntegracionDetalleDS> lista = IntegracionServiceUtil.detalleDSByPeriodo(periodo);
				List<String> errores = verificarPeriodoExportacion(lista);

				if (null != errores && !errores.isEmpty()) {

					crearTxt(req, res, (ArrayList<String>) errores, "Errores_Exportacion_Integracion_FTP.txt");

				} else {
					crearTxt_Integracion_FTP(req, res, lista, "112608_ds.txt");
					// if(informadoFTP) {
					IntegracionServiceUtil.updateInformadoFTPDS(periodo, null);
					// }
				}
			} else if (accion.equals(INTEGRACION_EXPORTAR_RENDICION)) {
				Integer periodo = ParamUtil.getInteger(req, "periodo");

				IntegracionDetalleDR filtro = new IntegracionDetalleDR();
				filtro.setSoloErrores(false);
				filtro.setPeriodoPresentacion(periodo);
				filtro.setId(null);
				List<IntegracionDetalleDR> lista = IntegracionServiceUtil.traeListaDetalleDR(0, filtro);

				List<String> errores = new ArrayList<String>();// verificarPeriodoExportacion(lista);

				if (null != errores && !errores.isEmpty()) {

					crearTxt(req, res, (ArrayList<String>) errores, "Errores_Exportacion_Integracion_Rendicion.txt");

				} else {
					crearTxt_Integracion_Rendicion_FTP(req, res, lista,
							"112608-" + periodo.toString().trim() + "_DR.DEVOLUCION.txt");
				}

			} else if (accion.equals(EXPORTAR_CUENTAS_INTERBANKING)) {// Devulve los TXT en un zip
				String nombreArchivoCuentas;
				String nombreArchivoPagos;
				String nombreArchivoOpError;
				String zipNombre;
				String opDesde = ParamUtil.getString(req, "op_desde");
				String opHasta = ParamUtil.getString(req, "op_hasta");
				String in = ParamUtil.getString(req, "in");
				String[] archivosNombre = new String[3];
				int contador = 0;
				List<PagosInterbanking> pagosOk = null;
				List<OrdenPagoConError> pagosError = null;

				if (in != null) {
					in = in.replace('S', ',');
				}

				List<CuentasInterbaking> cuentas = IntegracionServiceUtil.exportarCuentasInterbanking(opDesde, opHasta,
						in);
				OrdenesPagoInterbanking ordenesPagoInter = IntegracionServiceUtil.exportacionPagosInterbanking(opDesde,
						opHasta, in);
				zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
				pagosOk = ordenesPagoInter.getListaPagos();

				nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
				nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
				nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";

				String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
				contador = 0;
				if (!cuentas.isEmpty()) {
					crearTxt_cuentas_interbanking_proveedores_FTP(cuentas, nombreArchivoCuentas, null);
					archivosNombre[0] = nombreArchivoCuentas;
					archivosNombre[1] = nombreArchivoPagos;
					contador = contador + 2;
					if (ordenesPagoInter.getOdenConError() != null && !ordenesPagoInter.getOdenConError().isEmpty()) {
						pagosError = ordenesPagoInter.getOdenConError();
						archivosNombre[2] = nombreArchivoOpError;
						contador = contador + 1;
					}
				} else {
					archivosNombre[0] = nombreArchivoPagos;
					contador = contador + 1;
					if (ordenesPagoInter.getOdenConError() != null && !ordenesPagoInter.getOdenConError().isEmpty()) {
						pagosError = ordenesPagoInter.getOdenConError();
						archivosNombre[1] = nombreArchivoOpError;
						contador = contador + 1;
					}
				}
				crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);
				if (pagosError != null) {
					crearTxtOpConErrores(pagosError, nombreArchivoOpError);
				}

				byte[] buffer = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);

				int length;
				for (int i = 0; i < contador; i++) {
					FileInputStream fin = new FileInputStream(archivo_cuentas + archivosNombre[i]);
					zos.putNextEntry(new ZipEntry(archivosNombre[i]));
					length = 0;
					while ((length = fin.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					fin.close();
				}
				zos.closeEntry();

				zos.close();

				ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/zip");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

				sos.write(baos.toByteArray());

				zos.flush();
				zos.close();
				sos.flush();

				_log.info("Done");
			} else if (accion.equals(EXPORTAR_CUENTAS_INTERBANKING_OPS)) {// Devulve los TXT en un zip
				String nombreArchivoCuentas;
				String nombreArchivoPagos;
				String nombreArchivoOpError;
				String zipNombre;
				String opDesde = ParamUtil.getString(req, "op_desde");
				String opHasta = ParamUtil.getString(req, "op_hasta");
				String in = ParamUtil.getString(req, "in");
				String ctaBcria = ParamUtil.getString(req, "ctabcria");

				String[] archivosNombre = new String[3];
				int contador = 0;
				List<PagosInterbanking> pagosOk = null;
				List<OrdenPagoConError> pagosError = null;

				if (in != null) {
					in = in.replace('S', ',');
				}
				//
				try {
					List<CuentasInterbaking> cuentas = IntegracionServiceUtil.exportarCuentasInterbanking(opDesde,
							opHasta, in);
					OrdenesPagoInterbanking ordenesPagoInter = IntegracionServiceUtil
							.exportacionPagosInterbankingOPS(opDesde, opHasta, in, Integer.parseInt(ctaBcria));
					zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
					pagosOk = ordenesPagoInter.getListaPagos();

					nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";

					String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
					contador = 0;
					if (!cuentas.isEmpty()) {
						crearTxt_cuentas_interbanking_proveedores_FTP(cuentas, nombreArchivoCuentas, null);
						archivosNombre[0] = nombreArchivoCuentas;
						archivosNombre[1] = nombreArchivoPagos;
						contador = contador + 2;
						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre[2] = nombreArchivoOpError;
							contador = contador + 1;
						}
					} else {
						archivosNombre[0] = nombreArchivoPagos;
						contador = contador + 1;
						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre[1] = nombreArchivoOpError;
							contador = contador + 1;
						}
					}
					crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);
					if (pagosError != null) {
						crearTxtOpConErrores(pagosError, nombreArchivoOpError);
					}

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream zos = new ZipOutputStream(baos);

					int length;
					for (int i = 0; i < contador; i++) {
						FileInputStream fin = new FileInputStream(archivo_cuentas + archivosNombre[i]);
						zos.putNextEntry(new ZipEntry(archivosNombre[i]));
						length = 0;
						while ((length = fin.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						fin.close();
					}
					zos.closeEntry();

					zos.close();

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

					sos.write(baos.toByteArray());

					zos.flush();
					zos.close();
					sos.flush();
					_log.info("Done");
				} catch (Exception e) {
					_log.info(e.getMessage());
					ArrayList<String> errores = new ArrayList<String>();
					errores.add(e.getMessage());
					crearTxt(req, res, errores, "Errores_Exportacion_Interbanking_Ordenes_de_Pago.txt");
				}

			} else if (accion.equals(PERCEPCIONES_IIBB)) {
				Integer entidad = ParamUtil.getInteger(req, "entidad");
				String fechaDesdeDia = ParamUtil.getString(req, "fechadesdedia");
				String fechaDesdeMes = ParamUtil.getString(req, "fechadesdemes");
				fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req, "fechadesdeanio");
				String fechaHastaDia = ParamUtil.getString(req, "fechahastadia");
				String fechaHastaMes = ParamUtil.getString(req, "fechahastames");
				fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				String fechaHastaAnio = ParamUtil.getString(req, "fechahastaanio");
				Integer concepto = ParamUtil.getInteger(req, "concepto");
				Date fechaIni = new Date();
				Date fechaFin = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
					fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);
					List<Comprobante> ops = new ArrayList<Comprobante>();

					ops = ComprobanteServiceUtil.getComprobantesIIBB(fechaIni, fechaFin, entidad, concepto, null);

					crearTxt_Percepcion_IIBB_SIFERE(req, res, ops, "SIFERE.txt");

				} catch (Exception e) {
					_log.error("Error al generar reporte percepcion IIBB", e);

				}

			} else if (accion.equals(RG3685_LIBRO_IVA_CPTES)) {
				Integer entidad = ParamUtil.getInteger(req, "entidad");
				String fechaDesdeDia = ParamUtil.getString(req, "fechadesdedia");
				String fechaDesdeMes = ParamUtil.getString(req, "fechadesdemes");
				fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req, "fechadesdeanio");
				String fechaHastaDia = ParamUtil.getString(req, "fechahastadia");
				String fechaHastaMes = ParamUtil.getString(req, "fechahastames");
				fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				String fechaHastaAnio = ParamUtil.getString(req, "fechahastaanio");
				String libro = ParamUtil.getString(req, "libro");
				String cuitEntidad = "";

				Date fechaIni = new Date();
				Date fechaFin = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
					fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);
					List<Comprobante> ops = new ArrayList<Comprobante>();
					ops = ComprobanteServiceUtil.getLibroIVA(fechaIni, fechaFin, libro, entidad, null);

					if (entidad == 1) {
						cuitEntidad = WebKeysGlobal.CUIT_UOMA;

					} else if (entidad == 2) {
						cuitEntidad = WebKeysGlobal.CUIT_OSPIM;
					} else if (entidad == 3) {
						cuitEntidad = WebKeysGlobal.CUIT_AMTIMA;
					}

					if ("COMPRAS".equalsIgnoreCase(libro)) {
						crearTxt_RG3685_Compras_Cptes(req, res, ops, "RG3685_Compras_Comprobantes.txt");

					} else {
						crearTxt_RG3685_Ventas_Cptes(req, res, ops, "RG3685_Ventas_Comprobantes.txt");
					}

				} catch (SystemException e) {
					_log.debug(e.getMessage());
				}
			} else if (accion.equals(RG3685_LIBRO_IVA_ALICUOTAS)) {
				Integer entidad = ParamUtil.getInteger(req, "entidad");
				String fechaDesdeDia = ParamUtil.getString(req, "fechadesdedia");
				String fechaDesdeMes = ParamUtil.getString(req, "fechadesdemes");
				fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req, "fechadesdeanio");
				String fechaHastaDia = ParamUtil.getString(req, "fechahastadia");
				String fechaHastaMes = ParamUtil.getString(req, "fechahastames");
				fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				String fechaHastaAnio = ParamUtil.getString(req, "fechahastaanio");
				String libro = ParamUtil.getString(req, "libro");
				String cuitEntidad = "";

				Date fechaIni = new Date();
				Date fechaFin = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
					fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);
					List<Comprobante> ops = new ArrayList<Comprobante>();
					ops = ComprobanteServiceUtil.getLibroIVA(fechaIni, fechaFin, libro, entidad, null);

					if (entidad == 1) {
						cuitEntidad = WebKeysGlobal.CUIT_UOMA;

					} else if (entidad == 2) {
						cuitEntidad = WebKeysGlobal.CUIT_OSPIM;
					} else if (entidad == 3) {
						cuitEntidad = WebKeysGlobal.CUIT_AMTIMA;
					}

					if ("COMPRAS".equalsIgnoreCase(libro)) {
						crearTxt_RG3685_Compras_Alicuotas(req, res, ops, "RG3685_Compras_Alicuotas.txt");

					} else {
						crearTxt_RG3685_Ventas_Alicuotas(req, res, ops, "RG3685_Ventas_Alicuotas.txt");
					}

				} catch (SystemException e) {
					_log.debug(e.getMessage());
				}
			} else if (accion.equals(INTERBANKING_OPS)) {// Devuelve los TXT en un zip UOMA AMTIMA
				String nombreArchivoCuentas;
				String nombreArchivoPagos;
				String nombreArchivoOpError;
				String zipNombre;
				String opDesde = ParamUtil.getString(req, "op_desde");
				String opHasta = ParamUtil.getString(req, "op_hasta");
				String in = ParamUtil.getString(req, "in");
				String ctaBcria = ParamUtil.getString(req, "ctabcria");
				String entidad = ParamUtil.getString(req, "entidad");

				List<String> archivosNombre = new ArrayList<String>();
				int contador = 0;
				List<PagosInterbanking> pagosOk = null;
				List<OrdenPagoConError> pagosError = null;

				if (in != null) {
					in = in.replace('S', ',');
				}

				try {
					List<CuentasInterbaking> cuentas = IntegracionServiceUtil.getCuentasInterbanking(in, entidad);
					OrdenesPagoInterbanking ordenesPagoInter = IntegracionServiceUtil.getPagosInterbankingOPS(in,
							Integer.parseInt(ctaBcria), entidad);
					zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
					pagosOk = ordenesPagoInter.getListaPagos();

					nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";

					String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
					if (!cuentas.isEmpty()) {
						crearTxt_cuentas_interbanking_proveedores_FTP(cuentas, nombreArchivoCuentas, entidad);
						archivosNombre.add(nombreArchivoCuentas);

						archivosNombre.add(nombreArchivoPagos);

						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre.add(nombreArchivoOpError);

						}

					} else {

						archivosNombre.add(nombreArchivoPagos);
						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre.add(nombreArchivoOpError);
						}

					}

					crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);

					if (pagosError != null) {
						crearTxtOpConErrores(pagosError, nombreArchivoOpError);
					}

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream zos = new ZipOutputStream(baos);

					int length;

					for (String arch : archivosNombre) {
						FileInputStream fin = new FileInputStream(archivo_cuentas + arch);
						zos.putNextEntry(new ZipEntry(arch));
						length = 0;
						while ((length = fin.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						fin.close();

					}

					zos.closeEntry();

					zos.close();

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

					sos.write(baos.toByteArray());

					zos.flush();
					zos.close();
					sos.flush();
					_log.info("Done");

				} catch (Exception e) {
					_log.info(e.getMessage());
					ArrayList<String> errores = new ArrayList<String>();
					errores.add(e.getMessage());
					crearTxt(req, res, errores, "Errores_Exportacion_Interbanking_Ordenes_de_Pago UOMA-AMTIMA.txt");
				}
			} else if (accion.equals(PERCEPCIONES_ARBA_IIBB)) {

				Integer entidad = ParamUtil.getInteger(req, "entidad");
				String fechaDesdeDia = ParamUtil.getString(req, "fechadesdedia");
				String fechaDesdeMes = ParamUtil.getString(req, "fechadesdemes");
				fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req, "fechadesdeanio");
				String fechaHastaDia = ParamUtil.getString(req, "fechahastadia");
				String fechaHastaMes = ParamUtil.getString(req, "fechahastames");
				fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				String fechaHastaAnio = ParamUtil.getString(req, "fechahastaanio");
				String cuitEntidad = "";

				Date fechaIni = new Date();
				Date fechaFin = new Date();
				SimpleDateFormat sdfA = new SimpleDateFormat("yyyyMMddHHmm");
				SimpleDateFormat sdfB = new SimpleDateFormat("yyyyMM");

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
					fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);

					String nombreArchivo = "AR-30531143856-" + sdfB.format(fechaIni) + "0-D7-"
							+ sdfA.format(new Date());

					List<Factura> ops = new ArrayList<Factura>();
					List<Factura> per = new ArrayList<Factura>();
					ops = FacturacionServiceUtil.getFacturasPeriodo(fechaIni, fechaFin);
					for (Factura f : ops) {
						if (f.getPercepcion() != null && f.getPercepcion().compareTo(BigDecimal.ZERO) != 0) {
							per.add(f);
						}
					}
					crearTxt_Percepciones_ARBA_IIBB_Ventas(req, res, per, nombreArchivo + ".txt");

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream zos = new ZipOutputStream(baos);

					int length;

					FileInputStream fin = new FileInputStream(TMPDIR + FILE_SEPARATOR + nombreArchivo + ".txt");
					zos.putNextEntry(new ZipEntry(nombreArchivo + ".txt"));
					length = 0;
					while ((length = fin.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					fin.close();
					zos.closeEntry();

					zos.close();
					zos.flush();

					String md5 = "";
					byte[] hash = MessageDigest.getInstance("MD5").digest(baos.toByteArray());
					StringBuilder resultado = new StringBuilder();

					for (byte unByte : hash) {
						resultado.append(Integer.toString((unByte & 0xff) + 0x100, 16).substring(1));
					}
					md5 = resultado.toString();

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.setHeader("Content-Disposition",
							"attachment; filename=\"" + nombreArchivo + "_" + md5 + ".zip" + "\"");

					sos.write(baos.toByteArray());

					sos.flush();

				} catch (SystemException e) {
					_log.debug(e.getMessage());
				}
			} else if (accion.equals(RETENCIONES_ARBA_IIBB)) {
				Integer entidad = ParamUtil.getInteger(req, "entidad");
				String fechaDesdeDia = ParamUtil.getString(req, "fechadesdedia");
				String fechaDesdeMes = ParamUtil.getString(req, "fechadesdemes");
				fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req, "fechadesdeanio");
				String fechaHastaDia = ParamUtil.getString(req, "fechahastadia");
				String fechaHastaMes = ParamUtil.getString(req, "fechahastames");
				fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				String fechaHastaAnio = ParamUtil.getString(req, "fechahastaanio");
				String cuitEntidad = "";
				String quincena = "0";

				Date fechaIni = new Date();
				Date fechaFin = new Date();
				SimpleDateFormat sdfA = new SimpleDateFormat("yyyyMMddHHmm");
				SimpleDateFormat sdfB = new SimpleDateFormat("yyyyMM");

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
					fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);

					if (Integer.parseInt(fechaDesdeDia) < 16) {
						quincena = "1";
					} else {
						quincena = "2";
					}

					String nombreArchivo = "AR-30531143856-" + sdfB.format(fechaIni) + quincena + "-6-"
							+ sdfA.format(new Date());

					List<OrdenPago> ops = new ArrayList<OrdenPago>();
					List<OrdenPago> per = new ArrayList<OrdenPago>();

					ops = OrdenPagoServiceUtil.reporteOrdenPagoCompleto(fechaIni, fechaFin, 0, null, null, null, null,
							0, null, WebKeysGlobal.UOMA);

					for (OrdenPago o : ops) {
						if (o.getBaja_fecha() == null) {
							for (FormaPago f : o.getPagos()) {
								if (f.getPago() instanceof RetencionIIBB) {
									per.add(o);
								}
							}
						}
					}
					crearTxt_Retenciones_ARBA_IIBB_Compras(req, res, per, nombreArchivo + ".txt");

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream zos = new ZipOutputStream(baos);

					int length;

					FileInputStream fin = new FileInputStream(TMPDIR + FILE_SEPARATOR + nombreArchivo + ".txt");
					zos.putNextEntry(new ZipEntry(nombreArchivo + ".txt"));
					length = 0;
					while ((length = fin.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					fin.close();
					zos.closeEntry();

					zos.close();
					zos.flush();

					String md5 = "";
					byte[] hash = MessageDigest.getInstance("MD5").digest(baos.toByteArray());
					StringBuilder resultado = new StringBuilder();

					for (byte unByte : hash) {
						resultado.append(Integer.toString((unByte & 0xff) + 0x100, 16).substring(1));
					}
					md5 = resultado.toString();

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.setHeader("Content-Disposition",
							"attachment; filename=\"" + nombreArchivo + "_" + md5 + ".zip" + "\"");

					sos.write(baos.toByteArray());

					sos.flush();

				} catch (SystemException e) {
					_log.debug(e.getMessage());
				}
			}else if (accion.equals(RETENCIONES_ARBA_IIBB_A122)) {
					Integer entidad = ParamUtil.getInteger(req, "entidad");
					String fechaDesdeDia = ParamUtil.getString(req, "fechadesdedia");
					String fechaDesdeMes = ParamUtil.getString(req, "fechadesdemes");
					fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
					String fechaDesdeAnio = ParamUtil.getString(req, "fechadesdeanio");
					String fechaHastaDia = ParamUtil.getString(req, "fechahastadia");
					String fechaHastaMes = ParamUtil.getString(req, "fechahastames");
					fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
					String fechaHastaAnio = ParamUtil.getString(req, "fechahastaanio");
					String cuitEntidad = "";
					String quincena = "0";

					Date fechaIni = new Date();
					Date fechaFin = new Date();
					SimpleDateFormat sdfA = new SimpleDateFormat("yyyyMMddHHmm");
					SimpleDateFormat sdfB = new SimpleDateFormat("yyyyMM");

					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					try {
						fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
						fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);

						if (Integer.parseInt(fechaDesdeDia) < 16) {
							quincena = "1";
						} else {
							quincena = "2";
						}

						String nombreArchivo = "ER-30531143856-" + sdfB.format(fechaIni) + quincena + "-6-LOTE"
								+ sdfA.format(new Date());

						List<OrdenPago> ops = new ArrayList<OrdenPago>();
						List<OrdenPago> per = new ArrayList<OrdenPago>();

						ops = OrdenPagoServiceUtil.reporteOrdenPagoCompleto(fechaIni, fechaFin, 0, null, null, null, null,
								0, null, WebKeysGlobal.UOMA);

						for (OrdenPago o : ops) {
							if (o.getBaja_fecha() == null) {
								for (FormaPago f : o.getPagos()) {
									if (f.getPago() instanceof RetencionIIBB) {
										per.add(o);
									}
								}
							}
						}
						crearTxt_Retenciones_ARBA_IIBB_Compras_A122(req, res, per, nombreArchivo + ".txt");

						byte[] buffer = new byte[1024];
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ZipOutputStream zos = new ZipOutputStream(baos);

						int length;

						FileInputStream fin = new FileInputStream(TMPDIR + FILE_SEPARATOR + nombreArchivo + ".txt");
						zos.putNextEntry(new ZipEntry(nombreArchivo + ".txt"));
						length = 0;
						while ((length = fin.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						fin.close();
						zos.closeEntry();

						zos.close();
						zos.flush();
/*
						String md5 = "";
						byte[] hash = MessageDigest.getInstance("MD5").digest(baos.toByteArray());
						StringBuilder resultado = new StringBuilder();

						for (byte unByte : hash) {
							resultado.append(Integer.toString((unByte & 0xff) + 0x100, 16).substring(1));
						}
						md5 = resultado.toString();
*/
						ServletOutputStream sos = res.getOutputStream();
						res.setContentType("application/zip");
						res.setHeader("Content-Disposition",
								"attachment; filename=\"" + nombreArchivo  + ".zip" + "\"");

						sos.write(baos.toByteArray());

						sos.flush();

					} catch (SystemException e) {
						_log.debug(e.getMessage());
					}
				}else if (accion.equals(RECLAMOS_EXPORTAR_IMAGENES)) {

				String zipNombre;
				String in = ParamUtil.getString(req, "in");

				int contador = 0;

				DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
						PortletClassLoaderUtil.getClassLoader());
				DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales");

				DLFolder fC = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Comprobantes");
				long folderIdNew = fC.getFolderId();

				Company company = PortalUtil.getCompany(req);
				User user = PortalUtil.getUser(req);

				if (in != null) {

					String[] rps = in.split(";");
					Set<String> se = new HashSet<String>();
					for (int i = 0; i < rps.length; i++) {
						se.add(rps[i]);
					}
					rps = se.toArray(new String[0]);

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.addHeader("Content-Disposition", "attachment; filename=\"" + "Reclamos_IMG.zip" + "\"");

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ZipOutputStream zipOut = new ZipOutputStream(out);

					for (int i = 0; i < rps.length; i++) {
						zipNombre = "RP_" + rps[i] + ".zip";

						long folderId = f.getFolderId();

						Criterion criterion1 = null;

						criterion1 = RestrictionsFactoryUtil.eq("folderId", folderId);

						criterion1 = RestrictionsFactoryUtil.and(criterion1,
								RestrictionsFactoryUtil.ilike("title", String.valueOf(rps[i]) + "%"));

						dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
								PortletClassLoaderUtil.getClassLoader());
						dlf.add(criterion1);

						List<Object> results = DLFolderLocalServiceUtil.dynamicQuery(dlf);

						if (results.size() > 0) {
							ZipEntry zipEntry = new ZipEntry(zipNombre);
							zipOut.putNextEntry(zipEntry);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ZipOutputStream zos = new ZipOutputStream(baos);

							for (Object f1 : results) {
								DLFileEntry fileEntry = (DLFileEntry) f1;
								FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil.getFileAsStream(
										company.getCompanyId(), user.getUserId(), folderId, fileEntry.getName());
								byte[] buffer = new byte[1024];
								int length;
								zos.putNextEntry(new ZipEntry(fileEntry.getName()));
								length = 0;
								while ((length = fin.read(buffer)) > 0) {
									zos.write(buffer, 0, length);
								}
								fin.close();
								zos.closeEntry();
							}
							zos.flush();
							zos.close();
							zipOut.write(baos.toByteArray());
							zipOut.closeEntry();
						}

						// Levantar desde Portal de Proveedores
						ReclamoPrestacional rp = ReclamosPrestacionesServiceUtil
								.getReclamoPrestacional(Integer.parseInt(rps[i]));
						if (rp != null) {

							Comprobante comprobante = new Comprobante();
							try {
								PrestacionesReclamo presta = rp.getPrestaciones().get(0);

								comprobante.setCuit(presta.getComprobanteCUIT());
								comprobante.setTipoComprobante(presta.getComprobanteTipo());
								comprobante.setLetraComprobante(presta.getComprobanteLetra());
								comprobante.setPtoVenta(Integer.valueOf(presta.getComprobanteSucursal()));
								comprobante.setNroComprobante(presta.getComprobanteNro());

								String idFacturaImg = comprobante.getCuit() + "-" + comprobante.getTipoComprobante()
										+ "-" + comprobante.getLetraComprobante()
										+ String.format("%05d", comprobante.getPtoVenta())
										+ comprobante.getNroComprobante();

								List<DLFileEntryImpl> list = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil
										.getImagenesComprobantes(idFacturaImg, "CPBTE");
								List<DLFileEntryImpl> list1 = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil
										.getImagenesComprobantes(idFacturaImg, "ADJ");
								list.addAll(list1);

								if (list != null && list.size() > 0) {
									ZipEntry zipEntry = new ZipEntry("RP_" + rps[i] + "_PP.zip");
									zipOut.putNextEntry(zipEntry);

									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									ZipOutputStream zos = new ZipOutputStream(baos);

									for (int ii = 0; ii < list.size(); ii++) {
										DLFileEntry doc = list.get(ii); // --
										FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil
												.getFileAsStream(company.getCompanyId(), user.getUserId(), folderIdNew,
														doc.getName());
										byte[] buffer = new byte[1024];
										int length;
										zos.putNextEntry(new ZipEntry(doc.getName()));
										length = 0;
										while ((length = fin.read(buffer)) > 0) {
											zos.write(buffer, 0, length);
										}
										fin.close();
										zos.closeEntry();
									}
									zos.flush();
									zos.close();
									zipOut.write(baos.toByteArray());
									zipOut.closeEntry();
								}
							} catch (Exception e) {
							}
						}
						// Agregado
						// zipOut.closeEntry();
						// Fin Agregado
					}

					zipOut.finish();
					sos.write(out.toByteArray());
					sos.flush();
					sos.close();

					_log.info("Done");
				}
			} else if (accion.equals(LIQUIDACIONES_EXPORTAR_IMAGENES)) {

				String zipNombre;
				String in = ParamUtil.getString(req, "in");

				int contador = 0;

				DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
						PortletClassLoaderUtil.getClassLoader());

				DLFolder fC = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Comprobantes");
				long folderIdNew = fC.getFolderId();

				Company company = PortalUtil.getCompany(req);
				User user = PortalUtil.getUser(req);

				if (in != null) {

					String[] rps = in.split(";");
					Set<String> se = new HashSet<String>();
					for (int i = 0; i < rps.length; i++) {
						se.add(rps[i]);
					}
					rps = se.toArray(new String[0]);

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.addHeader("Content-Disposition", "attachment; filename=\"" + "Liquidaciones_IMG.zip" + "\"");

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ZipOutputStream zipOut = new ZipOutputStream(out);

					for (int i = 0; i < rps.length; i++) {
						zipNombre = "LQ_" + rps[i] + ".zip";

						// Levantar desde Portal de Proveedores
						Liquidacion rp = EditarLiquidacionServiceUtil.getLiquidacionEntry(Integer.parseInt(rps[i]));
						if (rp != null) {
							Comprobante comprobante = new Comprobante();
							try {
								// PrestacionesReclamo presta =rp.getPrestaciones().get(0);
								Prestador prestador = PrestadorServiceUtil.getPrestador(rp.getId_prestador());

								comprobante.setCuit(prestador.getCuit());
								comprobante.setTipoComprobante(rp.getCompro_a_debitar_tipo());
								comprobante.setLetraComprobante(rp.getCompro_a_debitar_letra());
								comprobante.setPtoVenta(Integer.valueOf(rp.getSucu()));
								comprobante.setNroComprobante(rp.getCompro_a_debitar_numero());

								String idFacturaImg = comprobante.getCuit() + "-" + comprobante.getTipoComprobante()
										+ "-" + comprobante.getLetraComprobante()
										+ String.format("%05d", comprobante.getPtoVenta())
										+ comprobante.getNroComprobante();

								List<DLFileEntryImpl> list = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil
										.getImagenesComprobantes(idFacturaImg, "CPBTE");
								List<DLFileEntryImpl> list1 = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil
										.getImagenesComprobantes(idFacturaImg, "ADJ");
								list.addAll(list1);

								if (list != null && list.size() > 0) {
									ZipEntry zipEntry = new ZipEntry("LQ_" + rps[i] + "_PP.zip");
									zipOut.putNextEntry(zipEntry);

									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									ZipOutputStream zos = new ZipOutputStream(baos);

									for (int ii = 0; ii < list.size(); ii++) {
										DLFileEntry doc = list.get(ii); // --
										FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil
												.getFileAsStream(company.getCompanyId(), user.getUserId(), folderIdNew,
														doc.getName());
										byte[] buffer = new byte[1024];
										int length;
										zos.putNextEntry(new ZipEntry(doc.getName()));
										length = 0;
										while ((length = fin.read(buffer)) > 0) {
											zos.write(buffer, 0, length);
										}
										fin.close();
										zos.closeEntry();
									}
									zos.flush();
									zos.close();
									zipOut.write(baos.toByteArray());
									zipOut.closeEntry();
								}
							} catch (Exception e) {
							}
						}
					}
					zipOut.finish();
					sos.write(out.toByteArray());
					sos.flush();
					sos.close();
					_log.info("Done");
				}
			} else if (accion.equals(PEDIDO_INFORME_JUBILADOS)) {

				String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
				String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
				fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
				String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
				String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
				fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");

				Date fechaIni = new Date();
				Date fechaFin = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-" + fechaDesdeAnio);
					fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-" + fechaHastaAnio);

					List<String> ops = new ArrayList<String>();
					ops = LiquidaDesreguladosServiceUtil.getPedidoInformeJubilados(fechaIni, fechaFin);

					crearTxt_Pedido_Informe_Jubilados_Sitaci(req, res, ops, "SitaciPedidoInforme.txt");

				} catch (Exception e) {
					_log.debug(e.getMessage());
				}
			} else if (accion.equals(CAJA_CHICA_EXPORTAR_IMAGENES)) {

				HttpSession session = (HttpSession) req.getSession();
				CajaChica cajaChica = (CajaChica) session.getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
				List<ComprobanteCajaChica> comprobantes = (List<ComprobanteCajaChica>) session
						.getAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION);

				String zipNombre;
				String in = ParamUtil.getString(req, "in");

				int contador = 0;

				DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
						PortletClassLoaderUtil.getClassLoader());

				DLFolder fC = DLFolderLocalServiceUtil.getFolder(10136, 0L, "CajaChica");
				long folderIdNew = fC.getFolderId();

				Company company = PortalUtil.getCompany(req);
				User user = PortalUtil.getUser(req);

				if (comprobantes != null) {

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.addHeader("Content-Disposition",
							"attachment; filename=\"" + cajaChica.getDescripcion() + "_IMG.zip" + "\"");

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ZipOutputStream zipOut = new ZipOutputStream(out);

					for (ComprobanteCajaChica c : comprobantes) {
						try {
							String idImg = c.getImagenNombre();
							String nameFile = c.getImagenNombreFileEntry();
							if (nameFile != null && nameFile.length() > 0) {
								String[] name = nameFile.split("\\.");
								String extension = "";
								try {
									extension = name[name.length - 1];
								} catch (Exception e1) {
								}
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil.getFileAsStream(
										company.getCompanyId(), user.getUserId(), folderIdNew, nameFile);
								ZipEntry zipEntry = new ZipEntry(
										idImg + (extension.length() > 0 ? "." + extension : ""));
								zipOut.putNextEntry(zipEntry);
								byte[] bytes = new byte[1024];
								int length;
								while ((length = fin.read(bytes)) >= 0) {
									zipOut.write(bytes, 0, length);
								}
								fin.close();
								zipOut.flush();
								zipOut.closeEntry();
							}
						} catch (Exception e) {
							_log.debug(e.getMessage());
						}
					}
					zipOut.finish();
					sos.write(out.toByteArray());
					sos.flush();
					sos.close();

				}
			} else if (accion.equals(ORDENES_PAGO_EXPORTAR_IMAGENES)) {

				String zipNombre;
				String in = ParamUtil.getString(req, "in");
				Integer entidad = ParamUtil.getInteger(req, "entidad");

				int contador = 0;

				DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
						PortletClassLoaderUtil.getClassLoader());

				DLFolder fC = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Comprobantes");
				long folderIdNew = fC.getFolderId();

				Company company = PortalUtil.getCompany(req);
				User user = PortalUtil.getUser(req);

				if (in != null) {

					String[] rps = in.split(";");
					Set<String> se = new HashSet<String>();
					for (int i = 0; i < rps.length; i++) {
						se.add(rps[i]);
					}
					rps = se.toArray(new String[0]);

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.addHeader("Content-Disposition", "attachment; filename=\"" + "OrdenesPago_IMG.zip" + "\"");

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ZipOutputStream zipOut = new ZipOutputStream(out);

					for (int i = 0; i < rps.length; i++) {
						zipNombre = "OP_" + rps[i] + ".zip";

						// Levantar desde Portal de Proveedores

						OrdenPago op = null;
						if (entidad.equals(WebKeysGlobal.OSPIM)) {
							op = new OrdenPagoOspim();
						}
						op.setId(Integer.parseInt(rps[i]));

						ComprobanteServiceUtil.getComprobantesConConceptos(op, entidad);

						if (op != null && op.getComprobantes() != null && !op.getComprobantes().isEmpty()) {

							ZipEntry zipEntry = new ZipEntry("OP_" + rps[i] + "_PP.zip");
							zipOut.putNextEntry(zipEntry);

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ZipOutputStream zos = new ZipOutputStream(baos);

							for (Comprobante c : op.getComprobantes()) {
								try {
									String idFacturaImg = c.getCuit() + "-" + c.getTipoComprobante() + "-"
											+ c.getLetraComprobante() + String.format("%05d", c.getPtoVenta())
											+ c.getNroComprobante();

									List<DLFileEntryImpl> list = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil
											.getImagenesComprobantes(idFacturaImg, "CPBTE");
									if (list != null && list.size() > 0) {

										// ByteArrayOutputStream baos = new ByteArrayOutputStream();
										// ZipOutputStream zos = new ZipOutputStream(baos);

										for (int ii = 0; ii < list.size(); ii++) {
											DLFileEntry doc = list.get(ii); // --
											FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil
													.getFileAsStream(company.getCompanyId(), user.getUserId(),
															folderIdNew, doc.getName());
											byte[] buffer = new byte[1024];
											int length;
											zos.putNextEntry(new ZipEntry(doc.getName()));
											length = 0;
											while ((length = fin.read(buffer)) > 0) {
												zos.write(buffer, 0, length);
											}
											fin.close();
											zos.closeEntry();
										}
										// zos.flush();
										// zos.close();
										// zipOut.write(baos.toByteArray());
										// zipOut.closeEntry();
									}
								} catch (Exception e) {
								}

							} //
							zos.flush();
							zos.close();
							zipOut.write(baos.toByteArray());
							zipOut.closeEntry();
						}
					}
					zipOut.finish();
					sos.write(out.toByteArray());
					sos.flush();
					sos.close();
					_log.info("Done");
				}
			} else if (accion.equals(PREAUTORIZACIONES_EXPORTAR_IMAGENES)) {

				String zipNombre;
				String in = ParamUtil.getString(req, "in");

				int contador = 0;

				DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
						PortletClassLoaderUtil.getClassLoader());
				DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "PREAUTORIZACIONES");
				
				DLFolder fAut = DLFolderLocalServiceUtil.getFolder(10136, 0L, "AutorizacionesPrestacionales");
				
				Company company = PortalUtil.getCompany(req);
				User user = PortalUtil.getUser(req);

				if (in != null) {

					String[] rps = in.split(";");
					Set<String> se = new HashSet<String>();
					for (int i = 0; i < rps.length; i++) {
						se.add(rps[i]);
					}
					rps = se.toArray(new String[0]);

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.addHeader("Content-Disposition",
							"attachment; filename=\"" + "Preautorizaciones_IMG.zip" + "\"");

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ZipOutputStream zipOut = new ZipOutputStream(out);

					for (int i = 0; i < rps.length; i++) {
						zipNombre = "PR_" + rps[i] + ".zip";

						long folderId = f.getFolderId();

						Criterion criterion1 = null;

						criterion1 = RestrictionsFactoryUtil.eq("folderId", folderId);

						criterion1 = RestrictionsFactoryUtil.and(criterion1,
								RestrictionsFactoryUtil.ilike("title", "PREAUT_" + String.valueOf(rps[i]) + "%"));

						dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
								PortletClassLoaderUtil.getClassLoader());
						dlf.add(criterion1);

						List<Object> results = DLFolderLocalServiceUtil.dynamicQuery(dlf);

						if (results.size() > 0) {
							ZipEntry zipEntry = new ZipEntry(zipNombre);
							zipOut.putNextEntry(zipEntry);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ZipOutputStream zos = new ZipOutputStream(baos);

							for (Object f1 : results) {
								DLFileEntry fileEntry = (DLFileEntry) f1;
								FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil.getFileAsStream(
										company.getCompanyId(), user.getUserId(), folderId, fileEntry.getName());
								byte[] buffer = new byte[1024];
								int length;
								zos.putNextEntry(new ZipEntry(fileEntry.getName()));
								length = 0;
								while ((length = fin.read(buffer)) > 0) {
									zos.write(buffer, 0, length);
								}
								fin.close();
								zos.closeEntry();
							}
///// - Imagenes Autorizaciones							
							PreAutorizacion pre = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(Integer.parseInt(rps[i]));
							if(pre.getNroAutorizacionPrestacional()>0) {
							   Criterion criterion2 = null;
							   criterion2 = RestrictionsFactoryUtil.eq("folderId",fAut.getFolderId());
							   criterion2=RestrictionsFactoryUtil.and(criterion2,
							   RestrictionsFactoryUtil.ilike("title", pre.getNroAutorizacionPrestacional()+"%" ));
							   dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
										PortletClassLoaderUtil.getClassLoader());
							   dlf.add(criterion2);
							   List<Object> resultsAut = DLFolderLocalServiceUtil.dynamicQuery(dlf);
							   for (Object f1 : resultsAut) {
								   DLFileEntry fileEntry = (DLFileEntry) f1;
								   FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil.getFileAsStream(
										company.getCompanyId(), user.getUserId(), fAut.getFolderId(), fileEntry.getName());
								   byte[] buffer = new byte[1024];
								   int length;
								   zos.putNextEntry(new ZipEntry(fileEntry.getName()));
								   length = 0;
								   while ((length = fin.read(buffer)) > 0) {
									  zos.write(buffer, 0, length);
								   }
								   fin.close();
								   zos.closeEntry();
							   }
							}
/////							
							zos.flush();
							zos.close();
							zipOut.write(baos.toByteArray());
							zipOut.closeEntry();
						}
						
					}

					zipOut.finish();
					sos.write(out.toByteArray());
					sos.flush();
					sos.close();

					_log.info("Done");
				}
			} else if (accion.equals(COMPROBANTES_INTEGRACION_EXPORTAR_IMAGENES)) {

				SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
				String carpetaStr = ParamUtil.getString(req, "carpeta");
				SimpleDateFormat sdfImg = new SimpleDateFormat("yyyyMM");
				String zipNombre;
				Date carpeta = null;
				Integer carpetaInt = 0;

				carpeta = formatoDeFecha.parse(
						"01/" + (Integer.parseInt(carpetaStr.split("_")[0]) + 1) + "/" + carpetaStr.split("_")[1]);

				carpetaInt = Integer.valueOf(sdfImg.format(carpeta));
				ComprobanteFiltro filtro = new ComprobanteFiltro();
				filtro.setCarpeta(carpeta);
				List<ComprobanteIntegracion> lista = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil
						.getListaIntegracion(filtro, 0);

				int contador = 0;

				DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class,
						PortletClassLoaderUtil.getClassLoader());
				DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Comprobantes");

				Company company = PortalUtil.getCompany(req);
				User user = PortalUtil.getUser(req);

				byte[] buffer = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);
				int length;
				String nbe = "";
				Long numero;
				for (Comprobante comprobante : lista) {

					long folderId = f.getFolderId();
					List<DLFileEntryImpl> results = comprobante.getImagenes();

					if (results.size() > 0) {
						for (Object f1 : results) {
							DLFileEntry fileEntry = (DLFileEntry) f1;

							if (fileEntry.getTitle().contains("-0") && fileEntry.getDescription().contains(".pdf")) {
								FileInputStream fin = (FileInputStream) DLFileEntryLocalServiceUtil.getFileAsStream(
										company.getCompanyId(), user.getUserId(), folderId, fileEntry.getName());
								numero = new BigDecimal(comprobante.getNroComprobante()).longValue();
								nbe = comprobante.getAcreedorEmpresa().getCuit() + "_"
										+ comprobanteIntegracion(comprobante.getTipoComprobante(),
												comprobante.getLetraComprobante())
										+ "_" + comprobante.getPtoVenta() + "_" + numero + ".pdf";
								zos.putNextEntry(new ZipEntry(nbe)); // fileEntry.getTitleWithExtension()
								length = 0;
								while ((length = fin.read(buffer)) > 0) {
									zos.write(buffer, 0, length);
								}
								fin.close();
							}
						}
					}
				}
				zos.closeEntry();
				zos.close();

				ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/zip");
				res.addHeader("Content-Disposition",
						"attachment; filename=\"" + "Integracion_IMG_" + sdfImg.format(carpeta) + ".zip" + "\"");

				sos.write(baos.toByteArray());

				zos.flush();
				zos.close();
				sos.flush();
				_log.info("Done");

			} else if (accion.equals(EXPORTAR_CUENTAS_INTERBANKING_EMAIL)) {// Devulve los TXT en un zip
				
				String opDesde = ParamUtil.getString(req, "op_desde");
				String opHasta = ParamUtil.getString(req, "op_hasta");
				String in = ParamUtil.getString(req, "in");
				if (in != null) {
					in = in.replace('S', ',');
				}

				List<CuentasInterbaking> cuentas = IntegracionServiceUtil.exportarCuentasInterbankingEmail(opDesde,
						opHasta, in);
				OrdenesPagoInterbanking ordenesPagoInter = IntegracionServiceUtil.exportacionPagosInterbanking(opDesde,
						opHasta, in);
				
				exportarInterbankingEmail(req,res,cuentas,ordenesPagoInter,null);
				
				/*
				
				String nombreArchivoCuentas;
				String nombreArchivoPagos;
				String nombreArchivoOpError;
				String zipNombre;
				
				String[] archivosNombre = new String[3];
				int contador = 0;
				List<PagosInterbanking> pagosOk = null;
				List<OrdenPagoConError> pagosError = null;
				
				zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
				pagosOk = ordenesPagoInter.getListaPagos();

				nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
				nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
				nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";

				String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
				contador = 0;
				if (!cuentas.isEmpty()) {
					crearTxt_cuentas_interbanking_proveedores_FTP_Email(cuentas, nombreArchivoCuentas, null);
					archivosNombre[0] = nombreArchivoCuentas;
					archivosNombre[1] = nombreArchivoPagos;
					contador = contador + 2;
					if (ordenesPagoInter.getOdenConError() != null && !ordenesPagoInter.getOdenConError().isEmpty()) {
						pagosError = ordenesPagoInter.getOdenConError();
						archivosNombre[2] = nombreArchivoOpError;
						contador = contador + 1;
					}
				} else {
					archivosNombre[0] = nombreArchivoPagos;
					contador = contador + 1;
					if (ordenesPagoInter.getOdenConError() != null && !ordenesPagoInter.getOdenConError().isEmpty()) {
						pagosError = ordenesPagoInter.getOdenConError();
						archivosNombre[1] = nombreArchivoOpError;
						contador = contador + 1;
					}
				}
				crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);
				if (pagosError != null) {
				    crearTxtOpConErrores(pagosError, nombreArchivoOpError);
				} 
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);

				int length;
				for (int i = 0; i < contador; i++) {
					FileInputStream fin = new FileInputStream(archivo_cuentas + archivosNombre[i]);
					zos.putNextEntry(new ZipEntry(archivosNombre[i]));
					length = 0;
					while ((length = fin.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					fin.close();
				}
				zos.closeEntry();

				zos.close();

				ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/zip");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

				sos.write(baos.toByteArray());

				zos.flush();
				zos.close();
				sos.flush();
                */
				
			} else if (accion.equals(EXPORTAR_CUENTAS_INTERBANKING_OPS_EMAIL)) {// Devulve los TXT en un zip
				
				String opDesde = ParamUtil.getString(req, "op_desde");
				String opHasta = ParamUtil.getString(req, "op_hasta");
				String in = ParamUtil.getString(req, "in");
				String ctaBcria = ParamUtil.getString(req, "ctabcria");
				
				if (in != null) {
					in = in.replace('S', ',');
				}
				
				try {
					List<CuentasInterbaking> cuentas = IntegracionServiceUtil.exportarCuentasInterbankingEmail(opDesde,
							opHasta, in);
					OrdenesPagoInterbanking ordenesPagoInter = IntegracionServiceUtil
							.exportacionPagosInterbankingOPS(opDesde, opHasta, in, Integer.parseInt(ctaBcria));
					
					exportarInterbankingEmail(req,res,cuentas,ordenesPagoInter,null);
					
					/*
					String nombreArchivoCuentas;
				    String nombreArchivoPagos;
				    String nombreArchivoOpError;
				    String zipNombre;
				
  				    String[] archivosNombre = new String[3];
				    int contador = 0;
				    List<PagosInterbanking> pagosOk = null;
				    List<OrdenPagoConError> pagosError = null;
  				 
					zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
					pagosOk = ordenesPagoInter.getListaPagos();

					nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";

					String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
					contador = 0;
					if (!cuentas.isEmpty()) {
						crearTxt_cuentas_interbanking_proveedores_FTP_Email(cuentas, nombreArchivoCuentas, null);
						archivosNombre[0] = nombreArchivoCuentas;
						archivosNombre[1] = nombreArchivoPagos;
						contador = contador + 2;
						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre[2] = nombreArchivoOpError;
							contador = contador + 1;
						}
					} else {
						archivosNombre[0] = nombreArchivoPagos;
						contador = contador + 1;
						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre[1] = nombreArchivoOpError;
							contador = contador + 1;
						}
					}
					crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);
					if (pagosError != null) {
						crearTxtOpConErrores(pagosError, nombreArchivoOpError);
					}

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream zos = new ZipOutputStream(baos);

					int length;
					for (int i = 0; i < contador; i++) {
						FileInputStream fin = new FileInputStream(archivo_cuentas + archivosNombre[i]);
						zos.putNextEntry(new ZipEntry(archivosNombre[i]));
						length = 0;
						while ((length = fin.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						fin.close();
					}
					zos.closeEntry();

					zos.close();

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

					sos.write(baos.toByteArray());

					zos.flush();
					zos.close();
					sos.flush();
					
					*/
					
				} catch (Exception e) {
					_log.info(e.getMessage());
					ArrayList<String> errores = new ArrayList<String>();
					errores.add(e.getMessage());
					crearTxt(req, res, errores, "Errores_Exportacion_Interbanking_Ordenes_de_Pago_Email.txt");
				}

			}else if (accion.equals(INTERBANKING_OPS_EMAIL)) {// Devuelve los TXT en un zip UOMA AMTIMA
				String opDesde = ParamUtil.getString(req, "op_desde");
				String opHasta = ParamUtil.getString(req, "op_hasta");
				String in = ParamUtil.getString(req, "in");
				String ctaBcria = ParamUtil.getString(req, "ctabcria");
				String entidad = ParamUtil.getString(req, "entidad");
				
				if (in != null) {
					in = in.replace('S', ',');
				}

				
				
				
				try {
					List<CuentasInterbaking> cuentas = IntegracionServiceUtil.getCuentasInterbankingEmail(in, entidad);
					OrdenesPagoInterbanking ordenesPagoInter = IntegracionServiceUtil.getPagosInterbankingOPS(in,
							Integer.parseInt(ctaBcria), entidad);
					
					exportarInterbankingEmail(req,res,cuentas,ordenesPagoInter,entidad);
					
					/*
					String nombreArchivoCuentas;
					String nombreArchivoPagos;
					String nombreArchivoOpError;
					String zipNombre;

					List<String> archivosNombre = new ArrayList<String>();
					int contador = 0;
					List<PagosInterbanking> pagosOk = null;
					List<OrdenPagoConError> pagosError = null;

					
					zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
					pagosOk = ordenesPagoInter.getListaPagos();

					nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
					nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";

					String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
					if (!cuentas.isEmpty()) {
						crearTxt_cuentas_interbanking_proveedores_FTP_Email(cuentas, nombreArchivoCuentas, entidad);
						archivosNombre.add(nombreArchivoCuentas);

						archivosNombre.add(nombreArchivoPagos);

						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre.add(nombreArchivoOpError);

						}

					} else {

						archivosNombre.add(nombreArchivoPagos);
						if (ordenesPagoInter.getOdenConError() != null
								&& !ordenesPagoInter.getOdenConError().isEmpty()) {
							pagosError = ordenesPagoInter.getOdenConError();
							archivosNombre.add(nombreArchivoOpError);
						}

					}

					crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);

					if (pagosError != null) {
						crearTxtOpConErrores(pagosError, nombreArchivoOpError);
					}

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream zos = new ZipOutputStream(baos);

					int length;

					for (String arch : archivosNombre) {
						FileInputStream fin = new FileInputStream(archivo_cuentas + arch);
						zos.putNextEntry(new ZipEntry(arch));
						length = 0;
						while ((length = fin.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						fin.close();

					}

					zos.closeEntry();

					zos.close();

					ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/zip");
					res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

					sos.write(baos.toByteArray());

					zos.flush();
					zos.close();
					sos.flush();
					_log.info("Done");
					*/

				} catch (Exception e) {
					_log.info(e.getMessage());
					ArrayList<String> errores = new ArrayList<String>();
					errores.add(e.getMessage());
					crearTxt(req, res, errores, "Errores_Exportacion_Interbanking_Ordenes_de_Pago UOMA-AMTIMA.txt");
				}
			}else if (accion.equals(R331_ZIP)) {

			    int anio = ParamUtil.getInteger(req, "anio");
			    int trimestre = ParamUtil.getInteger(req, "trimestre");
			    
			    int mesCierre = trimestre * 3;
			    String periodo = String.format("%02d-%d", mesCierre, anio);
			    
			    if (anio < 2000 || anio > 2100 || trimestre < 1 || trimestre > 4) {
			        res.setContentType("text/plain; charset=UTF-8");
			        res.getWriter().println("Parametros invalidos");
			        return;
			    }

			    String zipName = "Resolucion331_OSPIM_" + periodo + ".zip";

			    res.reset();
			    res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			    res.setHeader("Pragma", "no-cache");
			    res.setContentType("application/zip");
			    res.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");

			    java.util.zip.ZipOutputStream zos = null;

			    try {
			        zos = new java.util.zip.ZipOutputStream(res.getOutputStream());

			        // PADRON
			        byte[] padron = ar.com.ospim.farmaciaOspim.action.DescargarR331Action.generarPadron(anio, trimestre);
			        zos.putNextEntry(new java.util.zip.ZipEntry("PadronBenefPatCronicas_" + periodo + "_OSPIM.txt"));
			        zos.write(padron);
			        zos.closeEntry();

			        // PRESTADORES
			        byte[] prestadores = ar.com.ospim.farmaciaOspim.action.DescargarR331Action.generarPrestadores(anio, trimestre);
			        zos.putNextEntry(new java.util.zip.ZipEntry("Prestadores_" + periodo + "_OSPIM.txt"));
			        zos.write(prestadores);
			        zos.closeEntry();

			        // AFILIADOS POR PROVINCIA
			        byte[] prov = ar.com.ospim.farmaciaOspim.action.DescargarR331Action.generarAfiliadosProvincia(anio, trimestre);
			        zos.putNextEntry(new java.util.zip.ZipEntry("Afiliados_por_provincia_" + periodo + "_OSPIM.txt"));
			        zos.write(prov);
			        zos.closeEntry();

			        // RECETAS POR PRESTADOR
			        byte[] recPrest = ar.com.ospim.farmaciaOspim.action.DescargarR331Action.generarRecetasPorPrestador(anio, trimestre);
			        zos.putNextEntry(new java.util.zip.ZipEntry("Recetas_por_prestador_" + periodo + "_OSPIM.txt"));
			        zos.write(recPrest);
			        zos.closeEntry();

			        // RECETAS POR BENEFICIARIO
			        byte[] recBenef = ar.com.ospim.farmaciaOspim.action.DescargarR331Action.generarRecetasPorBeneficiario(anio, trimestre);
			        zos.putNextEntry(new java.util.zip.ZipEntry("Recetas_por_beneficiario_" + periodo + "_OSPIM.txt"));
			        zos.write(recBenef);
			        zos.closeEntry();

			        // PATOLOGIAS
			        byte[] pat = ar.com.ospim.farmaciaOspim.action.DescargarR331Action.generarPatologias(anio, trimestre);
			        zos.putNextEntry(new java.util.zip.ZipEntry("Patologias_" + periodo + "_OSPIM.txt"));
			        zos.write(pat);
			        zos.closeEntry();
			        
			        zos.finish();
			        return;

			    } catch (Exception e) {
			        _log.error("Error generando ZIP R331", e);

			        //muestra error en txt
			        try {
			            if (zos == null) {
			                zos = new java.util.zip.ZipOutputStream(res.getOutputStream());
			            }
			            zos.putNextEntry(new java.util.zip.ZipEntry("ERROR_Resolucion331_OSPIM.txt"));
			            String msgError = "Error generando ZIP R331: " + obtenerMensajeLimpio(e) + "\n";
			            zos.write(msgError.getBytes("UTF-8"));
			            zos.closeEntry();
			            zos.finish();
			        } catch (Exception ignored) {}

			        return;

			    } finally {
			        try { if (zos != null) zos.close(); } catch (Exception ignored) {}
			    }
			}



		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

	}

	private void crearTxt(HttpServletRequest req, HttpServletResponse res, ArrayList<String> list, String fileName) {

		ServletOutputStream out = null;

		try {
			res.setContentType("text/csv");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			// res.setCharacterEncoding("ASCII");
			out = res.getOutputStream();

			// setup the input as the blob to write out to the client
			// bufferedOutputStream = new BufferedOutputStream(out,4096);
			int cont = 0;
			// Simple read/write loop.
			for (String cadena : list) {
				cont++;
				out.write(cadena.getBytes("UTF-8"));
				// out.write(cadena.getBytes("ASCII"));
				out.write("\r\n".getBytes());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				_log.error(e);
			}
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	private ArrayList<String> verificarPeriodoExportacion(List<IntegracionDetalleDS> lista) {
		ArrayList<String> errores = new ArrayList();
		Map<String, String> entidades = new HashMap();

		for (IntegracionDetalleDS d : lista) {
			entidades.put(d.getTercerizadora(), d.getTercerizadora());
			if (d.getEnviadoSSS() != null) {
				errores.add("Período Ya enviado por FTP");
				break;
			}

			if ("NI".equalsIgnoreCase(d.getError())) { // Prestacion Inexistente
				errores.add("Existen Detalles que presentan errores");
				break;
			}
		}

		/*
		 * Se Saco control para que esten AMbas entidades a pedido del Usuario String
		 * sEntidades =
		 * TraeListasServiceUtil.getSystemConfig("INTEGRACION_VERIFICACION_ENTIDADES");
		 * String[] vEntidades = sEntidades.split(";"); if(errores.isEmpty()) { boolean
		 * ret=true; for(int xi=0;xi<vEntidades.length;xi++) { boolean ent=false; for
		 * (String key : entidades.keySet()) { if(vEntidades[xi].equalsIgnoreCase(key))
		 * { ent=true; break; } } ret = ret && ent; }
		 * 
		 * if(!ret) { errores.add("Falta importar el archivo de alguna Entidad"); } }
		 */
		return errores;
	}

	private void crearTxt_Integracion_FTP(HttpServletRequest req, HttpServletResponse res,
			List<IntegracionDetalleDS> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// res.setContentType("application/x-download");
			// res.setContentType("text/csv");
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			// setup the input as the blob to write out to the client
			// bufferedOutputStream = new BufferedOutputStream(out,4096);
			int cont = 0;
			// Simple read/write loop.
			boolean prestaEspe = false;
			for (IntegracionDetalleDS d : lista) {

				String cadena = "";
				prestaEspe = "97".equalsIgnoreCase(d.getPrestacionCodigo())
						|| "98".equalsIgnoreCase(d.getPrestacionCodigo())
						|| "99".equalsIgnoreCase(d.getPrestacionCodigo());

				cadena = d.getTipoArchivo();
				cadena += "|";
				cadena += d.getIdObraSocial().toString();
				cadena += "|";
				cadena += d.getCuil();
				cadena += "|";
				if (d.getCertificadoCodigo() != null) {
					cadena += String.format("%-40s", d.getCertificadoCodigo().trim().toUpperCase());
				} else {
					cadena += String.format("%-40s", "");
				}
				cadena += "|";
				if (d.getCertificadoVencimiento() != null) {
					cadena += sdf.format(d.getCertificadoVencimiento());
				} else {
					cadena += String.format("%-10s", "");
				}
				cadena += "|";
				cadena += d.getPeriodoPrestacion().toString();
				cadena += "|";
				cadena += String.format("%11s", d.getCuitPrestador().trim()).replace(' ', '0');
				cadena += "|";
				if (prestaEspe) {
					cadena += "00";
				} else {
					cadena += String.format("%02d", d.getComprobanteTipo());
				}
				cadena += "|";
				if (prestaEspe) {
					cadena += "N";
				} else {
					cadena += String.format("%1s", d.getComprobanteTipoEmision().trim()).replace(' ', '0');
				}
				cadena += "|";
				cadena += sdf.format(d.getComprobanteFechaEmision());
				cadena += "|";
				if (prestaEspe) {
					cadena += "00000000000000";
				} else {
					cadena += String.format("%14s",
							d.getComprobanteCAECAI().trim().length() > 14
									? d.getComprobanteCAECAI().trim().substring(0, 14).toUpperCase()
									: d.getComprobanteCAECAI().trim())
							.replace(' ', '0').toUpperCase();
				}
				cadena += "|";
				if (prestaEspe) {
					cadena += "00000";
				} else {
					cadena += String.format("%05d", d.getComprobantePtoVta());
				}
				cadena += "|";
				if (prestaEspe) {
					cadena += "00000000";
				} else {
					cadena += String.format("%08d", d.getComprobanteNro());
				}
				cadena += "|";

				if (prestaEspe) {
					cadena += "00000000000000";
				} else {
					cadena += String.format("%014d", d.getComprobanteImporte().intValue());
				}
				cadena += "|";
				if (prestaEspe) {
					cadena += "00000000000000";
				} else {
					cadena += String.format("%014d", d.getImporteSolicitado().intValue());
				}
				cadena += "|";
				cadena += String.format("%3s", d.getPrestacionCodigo().trim()).replace(' ', '0');
				cadena += "|";
				cadena += String.format("%06d", d.getPrestacionCantidad());
				cadena += "|";
				cadena += String.format("%02d", d.getProvincia());
				cadena += "|";
				cadena += d.getDependencia().trim().toUpperCase();
				cadena += "|";
				out.write(cadena.getBytes("UTF-8"));
				out.write("\r\n".getBytes());

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	/*
	 * private PrintWriter
	 * crearTxt_cuentas_interbanking_FTP(List<CuentasInterbaking> lista, String
	 * fileName) {
	 * 
	 * ServletOutputStream out = null;
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); PrintWriter writer =
	 * null; try { String disposition = TMPDIR + FILE_SEPARATOR + fileName; writer =
	 * new PrintWriter(disposition, "UTF-8");
	 * 
	 * String cadena;
	 * 
	 * cadena =""; //cabecera cadena+= StringUtils.leftPad("1",1,'0'); //tipo
	 * Registro cadena+= padRight(ConstantesInterbanking.CODIGO_INTERBANKING,7);
	 * //Codigo de Cliente cadena+= padRight(ConstantesInterbanking.VINCULADA,9);
	 * //Titular de la cuenta cadena+= padRight(ConstantesInterbanking.ALTA,12);
	 * //Funcion a realizar cadena+= StringUtils.leftPad(sdf.format(new Date()), 8,
	 * '0'); //Fecha de archivo cadena+= padRight("", 163); //Filter (Espacion en
	 * blanco) writer.println(cadena);
	 * 
	 * 
	 * int cantidadCuentas = lista.size(); for(CuentasInterbaking d:lista) {
	 * 
	 * cadena =""; //Cuentas
	 * 
	 * cadena+= StringUtils.leftPad("2", 1, '0'); // Tipo de registro cadena+=
	 * padRight(d.getCbu(),22); // CBU cadena+=
	 * padRight(ConstantesInterbanking.NO,1); //Uso Consulta cadena+=
	 * padRight(ConstantesInterbanking.NO,1);//Uso Débito cadena+=
	 * padRight(ConstantesInterbanking.SI,1);//Uso credito
	 * 
	 * cadena+= padRight("",50);//Referencia de uso
	 * 
	 * cadena+= StringUtils.leftPad("0", 14, '0'); // Tope por dia cadena+=
	 * StringUtils.leftPad("0", 14, '0'); // Tope por tranferencias cadena+=
	 * padRight("",40); //denominacion cadena+= StringUtils.leftPad(d.getCuit(), 11,
	 * '0'); //CUIT cadena+= padRight("",52); //Filler
	 * 
	 * //USOS cadena+= StringUtils.leftPad("3", 1, '0'); //Tipo de registro cadena+=
	 * padRight(ConstantesInterbanking.PROVEEDORES,3); //uso de la cuenta cadena+=
	 * padRight("",11); //Filler
	 * 
	 * //Habilitaciones cadena+= StringUtils.leftPad("", 1, '0'); //Tipo de registro
	 * cadena+= padRight("",2); //Pais cadena+= padRight("",20); //Numero de
	 * documento cadena+= padRight("",1); //Marca de Transfiere cadena+=
	 * padRight("",1); //Marca de Consulta cadena+= padRight("",5); //Filter
	 * 
	 * writer.println(cadena);
	 * 
	 * 
	 * } cadena =""; //Final cadena+= StringUtils.leftPad("5", 1, '0'); //Tipo de
	 * registro cadena+= padRight(ConstantesInterbanking.CODIGO_INTERBANKING,7);
	 * //Codigo de Cliente cadena+=
	 * StringUtils.leftPad(String.valueOf(cantidadCuentas), 6, '0'); //Cantidad de
	 * cuentas cadena+= padRight("",186); //Filler
	 * 
	 * writer.println(cadena);
	 * 
	 * writer.close();
	 * 
	 * 
	 * } catch (Exception e) { _log.error(e); } finally { try { if (out != null){
	 * out.close(); } } catch (Exception e) { _log.error(e); } } return writer; }
	 */

	private PrintWriter crearTxt_cuentas_interbanking_proveedores_FTP(List<CuentasInterbaking> lista, String fileName,
			String entidad) {

		ServletOutputStream out = null;

		PrintWriter writer = null;
		try {
			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			String codigoCliente = "";
			if (entidad == null) {
				codigoCliente = ConstantesInterbanking.CODIGO_INTERBANKING;
			} else if ("UOMA".equals(entidad)) {
				codigoCliente = TraeListasServiceUtil.getSystemConfig("UOMA_CLIENTE_INTERBANKING");
			} else if ("AMTIMA".equals(entidad)) {
				codigoCliente = TraeListasServiceUtil.getSystemConfig("AMTIMA_CLIENTE_INTERBANKING");
			}

			writer = new PrintWriter(disposition, "UTF-8");

			String cadena;

			cadena = "";
			// cabecera
			cadena += StringUtils.leftPad("1", 1, '0'); // tipo Registro
			cadena += padRight(codigoCliente, 7); // Codigo de Cliente
			cadena += padRight("", 152); // Filter (Espacion en blanco)
			writer.println(cadena);

			int cantidadCuentas = lista.size();
			for (CuentasInterbaking d : lista) {

				cadena = "";
				// Cuentas

				cadena += padRight("2", 1); // Tipo de registro
				cadena += padRight("", 22); // No utilizados
				cadena += padRight(formatiarRazonSocialInvalida(d.getDescripcion()), 29); // denominacion
				cadena += padRight(ConstantesInterbanking.SI, 1); // Uso Proveedores
				cadena += padRight(ConstantesInterbanking.NO, 1);// Uso Sueldos
				cadena += padRight(ConstantesInterbanking.NO, 1); // Uso depositos Judiciales
				cadena += StringUtils.leftPad(d.getCuit(), 11, '0'); // CUIT
				cadena += padRight(d.getCbu(), 22); // CBU
				cadena += padRight("", 50);// Referencia de uso
				cadena += padRight("", 22); // Filler

				writer.println(cadena);

			}
			cadena = "";
			// Final
			cadena += padRight("3", 1); // Tipo de registro
			cadena += padRight(codigoCliente, 7); // Codigo de Cliente
			cadena += StringUtils.leftPad(String.valueOf(cantidadCuentas), 6, '0'); // Cantidad de cuentas
			cadena += padRight("", 146); // Filler

			writer.println(cadena);

			writer.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}

	private String formatiarRazonSocialInvalida(String descripcion) {
		String[] caracteres = TraeListasServiceUtil.getSystemConfig("INTERBANKING_CARACTERES_INVALIDOS").split(",");
		for (String val : caracteres) {
			String temp[] = val.split(";");
			descripcion = descripcion.replace(temp[0], temp[1]);
		}
		return descripcion;

	}

	private PrintWriter crearTxt_Pagos_interbanking_FTP(List<PagosInterbanking> lista, String fileName) {

		ServletOutputStream out = null;

		PrintWriter writer = null;
		try {
			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			writer = new PrintWriter(disposition, "UTF-8");

			String cadena = null;

			for (PagosInterbanking d : lista) {
				if ("*M*".equalsIgnoreCase(d.getTipoRegistro())) {
					pagoProveedores(d, cadena, writer);
				} else {
					devitoCuentaOspim(d, cadena, writer);
				}
			}

			writer.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}

	private String pagoProveedores(PagosInterbanking d, String cadena, PrintWriter writer) {

		cadena = "";
		// pagos

		cadena += padRight(d.getTipoRegistro(), 3); // Tipo de registro Contine (M)
		cadena += padRight(d.getNumeroCBU(), 22); // CBU
		cadena += StringUtils.leftPad(formatearNumero(d.getImporteTranferencia()), 17, '0'); // importe de la
																								// Tranferencia

		cadena += padRight(d.getObservacion() != null ? d.getObservacion() : "", 60);// Observacion
		cadena += padRight(d.getTipoComprobante(), 2); // Documento a cancelar (Por ejemplo FA Factura / DB Nota de
														// debetio)
		cadena += padRight(d.getNroComprobante(), 12); // numero de documento a cancelar
		cadena += padRight("", 2); // Tipo de orden de pago
		cadena += padRight("", 12); // Numero de orden de pago
		cadena += padRight("", 12); // codigo de cliente
		cadena += padRight(d.getTipoRetencion() != null ? d.getTipoRetencion() : "", 2); // tipo de retencion (por
																							// ejmeplo 01:IVA /02
																							// Ganancias 03: ingresos
																							// Brutos /04: SUSS)

		cadena += StringUtils.leftPad(formatearNumero(d.getTotalRetencion()), 12, '0'); // Total de retencion

		cadena += padRight("", 12); // numero de nota de credito
		cadena += StringUtils.leftPad(formatearNumero(d.getImporteNotaCredito()), 10, '0'); // importe nota de credito

		cadena += padRight(d.getCUIT(), 11); // CUIT
		cadena += padRight("", 51); // Espacion en blanco

		writer.println(cadena);

		return cadena;

	}

	private String devitoCuentaOspim(PagosInterbanking d, String cadena, PrintWriter writer) {
		SimpleDateFormat sdfecha = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		cadena = "";

		cadena += padRight(d.getTipoRegistro(), 3); // Tipo de registro Contine (U)
		cadena += padRight(d.getNumeroCBU(), 22); // CBU
		cadena += padRight("D", 1); // Indicador de Débito o Crédido. Ingrese D o C
		cadena += StringUtils.leftPad(sdfecha.format(new Date()), 8, '0'); // fecha de solicitud en formato AAAAMMDD
		cadena += padRight("N", 1); // Marca de consolidado. Indique "S" o "N" , Esta marca debe coincidir con lo
									// que posteriormente
									// indicará en la panalla de confección de la tranferencia.
		cadena += padRight("", 61);
		cadena += StringUtils.leftPad("0", 3, '0'); // ingrese triple cero
		cadena += StringUtils.leftPad("0", 2, '0'); // Nro de cuenta corto segun formato Datanet. Ingrese siempre 00
		cadena += padRight(sdf.format(new Date()), 8); // Fecha de archivo dormato MM/DD/YY
		cadena += padRight("", 8); // Nro de secuencia del archivo, se puede usar para importar el mismo archivo
									// dos veces
		cadena += padRight("", 123); // Espacios en blancos

		writer.println(cadena);

		return cadena;

	}

	private PrintWriter crearTxtOpConErrores(List<OrdenPagoConError> listaOpError, String fileName) {

		ServletOutputStream out = null;

		PrintWriter writer = null;
		try {
			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			writer = new PrintWriter(disposition, "UTF-8");

			String cadena;

			for (OrdenPagoConError d : listaOpError) {
				cadena = "";
				// Ope Con Error
				cadena += padRight(String.valueOf(d.getOrdenPago()), 10);
				cadena += padRight(d.getCuitPrestador(), 15);

				writer.println(cadena);

			}
			writer.close();
		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}
	/*
	 * private PrintWriter crearTxtOpConAltaCuenta(List<CuentasInterbaking> listaOp,
	 * String fileName) {
	 * 
	 * ServletOutputStream out = null;
	 * 
	 * PrintWriter writer = null; try { String disposition = TMPDIR + FILE_SEPARATOR
	 * + fileName; writer = new PrintWriter(disposition, "UTF-8");
	 * 
	 * String cadena;
	 * 
	 * for(CuentasInterbaking d:listaOp) { cadena =""; //Ope Con Error cadena+=
	 * padRight(String.valueOf(d.getOrdenPagoId()), 10);
	 * 
	 * writer.println(cadena);
	 * 
	 * } writer.close(); } catch (Exception e) { _log.error(e); } finally { try { if
	 * (out != null){ out.close(); } } catch (Exception e) { _log.error(e); } }
	 * return writer; }
	 */

	private void crearTxt_Percepcion_IIBB_SIFERE(HttpServletRequest req, HttpServletResponse res,
			List<Comprobante> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("00000000.00");

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			int cont = 0;
			for (Comprobante d : lista) {

				String cadena = "";

				cadena = String.format("%03d", d.getConceptos().get(0).getJurisdiccionIIBB());
				cadena += d.getAcreedorEmpresa().getCuit().substring(0, 2) + "-"
						+ d.getAcreedorEmpresa().getCuit().substring(2, 10) + "-"
						+ d.getAcreedorEmpresa().getCuit().substring(10);
				cadena += sdf.format(d.getFechaEmision());

				cadena += String.format("%04d", d.getSucuComprobante());
				if (d.getNroComprobante().trim().length() < 8) {
					cadena += String.format("%08" + "d", d.getNroComprobante().trim());
				} else {
					cadena += d.getNroComprobante().trim();
				}

				if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)) {
					cadena += "F";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)) {
					cadena += "D";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)) {
					cadena += "C";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)) {
					cadena += "R";
				} else {
					cadena += "O";
				}
				cadena += d.getLetraComprobante();

				String importe = df.format(d.getConceptos().get(0).getPercepcionIIBB().doubleValue());
				importe = importe.replace(".", ",");
				cadena += importe;

				out.write(cadena.getBytes("UTF-8"));
				out.write("\r\n".getBytes());

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	/////

	private void crearTxt_RG3685_Compras_Cptes(HttpServletRequest req, HttpServletResponse res, List<Comprobante> lista,
			String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("00000000.00");

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			int cont = 0;
			for (Comprobante d : lista) {

				if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)) {

					String cadena = "";
					// Campo 1
					cadena += sdf.format(d.getFechaEmision());

					// Campo 2
					if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "001";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "006";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "011";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "002";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "007";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "012";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "003";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "008";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "013";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "004";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "009";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "014";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "081";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "082";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "111";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "X".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "083";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "201";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "206";
					} else if (d.getTipoComprobante()
							.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "203";
					} else if (d.getTipoComprobante()
							.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "208";
					}

					// Campo 3
					cadena += String.format("%05d", d.getSucuComprobante());

					// Campo 4
					cadena += String.format("%020d", Integer.parseInt(d.getNroComprobante()));

					// Campo 5
					cadena += "                "; // 16 posiciones

					// Campo 6
					cadena += "80";

					// Campo 7
					cadena += "000000000" + d.getAcreedorEmpresa().getCuit();

					// Campo 8
					if (d.getAcreedorEmpresa().getDescripcion().trim().length() < 30) {
						cadena += String.format("%1$-30s", d.getAcreedorEmpresa().getDescripcion().trim());
					} else {
						cadena += d.getAcreedorEmpresa().getDescripcion().substring(0, 30);
					}

					// Campo 9
					cadena += String.format("%015d",
							(d.getImporteComprobante().multiply(new BigDecimal(100))).intValue());

					// Campo 10
					cadena += String.format("%015d", 0);

					// Campo 11
					cadena += String.format("%015d", (d.getExento().multiply(new BigDecimal(100))).intValue());

					// Campo 12
					cadena += String.format("%015d", (d.getPercepcionIVA().multiply(new BigDecimal(100))).intValue());

					// Campo 13
					cadena += String.format("%015d", 0);

					// Campo 14
					cadena += String.format("%015d", (d.getPercepcionIIBB().multiply(new BigDecimal(100))).intValue());

					// Campo 15
					cadena += String.format("%015d", 0);

					// Campo 16
					cadena += String.format("%015d", 0);

					// Campo 17
					cadena += "PES";

					// Campo 18
					cadena += "0001000000";

					// Campo 19
					int ca = 0;
					if (d.getIva27().compareTo(BigDecimal.ZERO) > 0)
						ca++;
					if (d.getIva21().compareTo(BigDecimal.ZERO) > 0)
						ca++;
					if (d.getIva105().compareTo(BigDecimal.ZERO) > 0)
						ca++;
					cadena += String.valueOf(ca);

					// Campo 20
					if (ca > 0) {
						cadena += "0";
					} else {
						cadena += "A";
					}

					// Campo 21
					cadena += String.format("%015d",
							(d.getIva21().add(d.getIva105().add(d.getIva27())).multiply(new BigDecimal(100)))
									.intValue());

					// Campo 22
					cadena += String.format("%015d", (d.getOtrosTributos().multiply(new BigDecimal(100))).intValue());

					// Campo 23
					cadena += "00000000000";

					// Campo 24
					cadena += "                              ";

					// Campo 25
					cadena += "000000000000000";

					out.write(cadena.getBytes("UTF-8"));
					out.write("\r\n".getBytes());

				}
			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	private void crearTxt_RG3685_Compras_Alicuotas(HttpServletRequest req, HttpServletResponse res,
			List<Comprobante> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("00000000.00");

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			int cont = 0;
			for (Comprobante d : lista) {
				if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
						|| d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)) {

					List<String> alicuotas = new ArrayList<String>();

					if (d.getIva21().compareTo(BigDecimal.ZERO) > 0 || d.getIva105().compareTo(BigDecimal.ZERO) > 0
							|| d.getIva27().compareTo(BigDecimal.ZERO) > 0) {

						if (d.getIva27().compareTo(BigDecimal.ZERO) > 0) {
							alicuotas.add("27");
						}

						if (d.getIva21().compareTo(BigDecimal.ZERO) > 0) {
							alicuotas.add("21");
						}

						if (d.getIva105().compareTo(BigDecimal.ZERO) > 0) {
							alicuotas.add("105");
						}

						for (String s : alicuotas) {

							String cadena = "";
							// Campo 1
							if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "001";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "006";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
									&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "011";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "002";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "007";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
									&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "012";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "003";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "008";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
									&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "013";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "004";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "009";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
									&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "014";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "081";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "082";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
									&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "111";
							} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
									&& "X".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "083";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "201";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "206";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
									&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "203";
							} else if (d.getTipoComprobante()
									.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
									&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
								cadena += "208";
							}

							// Campo 2
							cadena += String.format("%05d", d.getSucuComprobante());

							// Campo 3
							cadena += String.format("%020d", Integer.parseInt(d.getNroComprobante()));

							// Campo 4
							cadena += "80";

							// Campo 5
							cadena += "000000000" + d.getAcreedorEmpresa().getCuit();

							// Cadena 6 7 y 8
							if ("27".equalsIgnoreCase(s)) {
								cadena += String.format("%015d",
										(d.getGravadoIVA27().multiply(new BigDecimal(100))).intValue());
								cadena += "0006";
								cadena += String.format("%015d",
										(d.getIva27().multiply(new BigDecimal(100))).intValue());
							}

							if ("21".equalsIgnoreCase(s)) {
								cadena += String.format("%015d",
										(d.getGravadoIVA21().multiply(new BigDecimal(100))).intValue());
								cadena += "0005";
								cadena += String.format("%015d",
										(d.getIva21().multiply(new BigDecimal(100))).intValue());

							}

							if ("105".equalsIgnoreCase(s)) {
								cadena += String.format("%015d",
										(d.getGravadoIVA105().multiply(new BigDecimal(100))).intValue());
								cadena += "0004";
								cadena += String.format("%015d",
										(d.getIva105().multiply(new BigDecimal(100))).intValue());
							}

							out.write(cadena.getBytes("UTF-8"));
							out.write("\r\n".getBytes());
						}
					}
				}
			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	/////
	/////

	private void crearTxt_RG3685_Ventas_Cptes(HttpServletRequest req, HttpServletResponse res, List<Comprobante> lista,
			String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("00000000.00");

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			int cont = 0;
			for (Comprobante d : lista) {

				String cadena = "";
				// Campo 1
				cadena += sdf.format(d.getFechaEmision());

				// Campo 2
				if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "001";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "006";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
						&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "011";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "002";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "007";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
						&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "012";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "003";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "008";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
						&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "013";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "004";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "009";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
						&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "014";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "081";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "082";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
						&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "111";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
						&& "X".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "083";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "201";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "206";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
						&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "203";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
						&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "208";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
						&& "T".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "195";
				} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO)
						&& "T".equalsIgnoreCase(d.getLetraComprobante())) {
					cadena += "197";
				}

				// Campo 3
				cadena += String.format("%05d", d.getSucuComprobante());

				// Campo 4
				cadena += String.format("%020d", Integer.parseInt(d.getNroComprobante()));

				// Campo 5
				cadena += String.format("%020d", Integer.parseInt(d.getNroComprobante()));

				// Campo 6
				cadena += d.getAcreedorEmpresa().getCartaDoc();

				// Campo 7
				cadena += "000000000" + d.getAcreedorEmpresa().getCuit();

				// Campo 8
				if (d.getAcreedorEmpresa().getDescripcion().trim().length() < 30) {
					cadena += String.format("%1$-30s", d.getAcreedorEmpresa().getDescripcion().trim());
				} else {
					cadena += d.getAcreedorEmpresa().getDescripcion().substring(0, 30);
				}

				// Campo 9
				cadena += String.format("%015d", (d.getImporteComprobante().multiply(new BigDecimal(100))).intValue());

				// Campo 10
				cadena += String.format("%015d", 0);

				// Campo 11
				cadena += String.format("%015d", (d.getPercepcionIVA().multiply(new BigDecimal(100))).intValue());

				// Campo 12
				cadena += String.format("%015d", (d.getExento().multiply(new BigDecimal(100))).intValue());

				// Campo 13
				cadena += String.format("%015d", 0);

				// Campo 14
				cadena += String.format("%015d", (d.getPercepcionIIBB().multiply(new BigDecimal(100))).intValue());

				// Campo 15
				cadena += String.format("%015d", 0);

				// Campo 16
				cadena += String.format("%015d", 0);

				// Campo 17
				cadena += "PES";

				// Campo 18
				cadena += "0001000000";

				// Campo 19
				int ca = 0;
				// if(d.getIva21().compareTo(BigDecimal.ZERO) >0) ca++;
				// if(d.getIva105().compareTo(BigDecimal.ZERO) >0) ca++;
				cadena += String.valueOf(ca);

				// Campo 20
				if (ca > 0) {
					cadena += "0";
				} else {
					cadena += "A";
				}

				// Campo 21
				cadena += String.format("%015d", 0);

				// Campo 22
				cadena += "00000000";

				out.write(cadena.getBytes("UTF-8"));
				out.write("\r\n".getBytes());

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	private void crearTxt_RG3685_Ventas_Alicuotas(HttpServletRequest req, HttpServletResponse res,
			List<Comprobante> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("00000000.00");

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			int cont = 0;
			for (Comprobante d : lista) {

				if ((d.getIva21() != null && d.getIva21().compareTo(BigDecimal.ZERO) > 0)
						|| (d.getIva105() != null && d.getIva105().compareTo(BigDecimal.ZERO) > 0)) {

					String cadena = "";
					// Campo 1
					if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "001";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "006";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "011";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "002";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "007";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "012";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "003";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "008";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_BIS)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "013";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "004";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "009";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "014";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "081";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "082";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "C".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "111";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_TICKET)
							&& "X".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "083";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "201";
					} else if (d.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "206";
					} else if (d.getTipoComprobante()
							.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
							&& "A".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "203";
					} else if (d.getTipoComprobante()
							.equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)
							&& "B".equalsIgnoreCase(d.getLetraComprobante())) {
						cadena += "208";
					}

					// Campo 2
					cadena += String.format("%05d", d.getSucuComprobante());

					// Campo 3
					cadena += String.format("%020d", Integer.parseInt(d.getNroComprobante()));

					// Cadena 4 y 5

					if (d.getIva21().compareTo(BigDecimal.ZERO) > 0) {
						cadena += String.format("%015d",
								(d.getGravadoIVA21().multiply(new BigDecimal(100))).intValue());
						cadena += "0005";
						cadena += String.format("%015d", (d.getIva21().multiply(new BigDecimal(100))).intValue());
					} else if (d.getIva105().compareTo(BigDecimal.ZERO) > 0) {
						cadena += String.format("%015d",
								(d.getGravadoIVA105().multiply(new BigDecimal(100))).intValue());
						cadena += "0004";
						cadena += String.format("%015d", (d.getIva105().multiply(new BigDecimal(100))).intValue());
					}

					out.write(cadena.getBytes("UTF-8"));
					out.write("\r\n".getBytes());

				}

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	/////

	private static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	private static String formatearNumero(BigDecimal val) {
		String v = null;
		if (val == null) {
			v = "0";
			return v;
		}
		DecimalFormat df = new DecimalFormat("####.00");
		v = df.format(val);
		return v.replaceAll(",", "");
	}

	private void crearTxt_Integracion_Rendicion_FTP(HttpServletRequest req, HttpServletResponse res,
			List<IntegracionDetalleDR> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			// setup the input as the blob to write out to the client
			// bufferedOutputStream = new BufferedOutputStream(out,4096);
			int cont = 0;
			// Simple read/write loop.
			String aux = "";
			for (IntegracionDetalleDR d : lista) {

				String cadena = "";

				cadena += d.getClave(); // 1
				cadena += "|";
				cadena += d.getIdObraSocial().toString(); // 2
				cadena += "|";
				cadena += d.getTipoArchivo(); // 3
				cadena += "|";
				cadena += d.getPeriodoPresentacion().toString(); // 4
				cadena += "|";
				cadena += d.getPeriodoPrestacion().toString(); // 5
				cadena += "|";
				cadena += d.getCuil(); // 6
				cadena += "|";
				cadena += String.format("%03d", d.getPrestacionCodigo()); // 7
				cadena += "|";

				Double daux = Double.valueOf(d.getImporteLiquidado() * 100D);
				aux = String.format("%012d", daux.intValue());

				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12); // 8
				cadena += "|";

				daux = Double.valueOf(d.getImporteSolicitado() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 9

				cadena += "|";
				cadena += String.format("%04d", d.getNroEnvioAfip()); // 10
				cadena += "|";
				cadena += d.getCbuCuit(); // 11
				cadena += "|";
				cadena += d.getCbu(); // 12
				cadena += "|";
				if (d.getOrdenPagoI() > 0) {
					cadena += d.getOrdenPagoI().toString().trim(); // 13
				}
				cadena += "|";
				if (d.getOrdenPagoII() > 0) {
					cadena += d.getOrdenPagoII().toString().trim(); // 14
				}
				cadena += "|";
				if (d.getFechaTransferenciaI() != null) {
					cadena += sdf.format(d.getFechaTransferenciaI()); // 15
				}
				cadena += "|";
				if (d.getFechaTransferenciaII() != null) {
					cadena += sdf.format(d.getFechaTransferenciaII()); // 16
				}
				cadena += "|";
				if (d.getCheque() != null) {
					cadena += d.getCheque(); // 17
				}
				cadena += "|";

				daux = Double.valueOf(d.getImporteTransferido() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 18
				cadena += "|";

				daux = Double.valueOf(d.getRetencionGanancias() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 19
				cadena += "|";

				daux = Double.valueOf(d.getRetencionIIBB() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 20
				cadena += "|";

				daux = Double.valueOf(d.getOtrasRetenciones() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 21
				cadena += "|";

				daux = Double.valueOf(d.getImporteAplicado() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 22
				cadena += "|";

				daux = Double.valueOf(d.getFondosPropiosDiscapacidad() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 23
				cadena += "|";

				daux = Double.valueOf(d.getFondosPropiosOtraCuenta() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 24
				cadena += "|";

				cadena += String.format("%08d", d.getNroRecibo()); // 25
				cadena += "|";

				daux = Double.valueOf(d.getImporteTrasladado() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 26
				cadena += "|";

				daux = Double.valueOf(d.getImporteDevuelto() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 27
				cadena += "|";

				daux = Double.valueOf(d.getSaldoNoAplicado() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 28
				cadena += "|";

				daux = Double.valueOf(d.getRecuperoFondosPropios() * 100D);
				aux = String.format("%012d", daux.intValue());
				cadena += aux.substring(0, 10) + "," + aux.substring(10, 12);
				; // 29
				cadena += "|";

				if (d.getObservaciones() != null) {
					cadena += d.getObservaciones().toUpperCase();
				}

				out.write(cadena.getBytes("UTF-8"));
				out.write("\r\n".getBytes());

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	private PrintWriter crearTxt_Percepciones_ARBA_IIBB_Ventas(HttpServletRequest req, HttpServletResponse res,
			List<Factura> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("00000000000.00");
		DecimalFormat dfAli = new DecimalFormat("00.00");
		PrintWriter writer = null;

		try {
			/*
			 * res.setContentType("text/plain"); String disposition =
			 * "attachment; fileName=" + fileName; res.setHeader("Content-Disposition",
			 * disposition);
			 * 
			 * res.setCharacterEncoding("UTF-8"); out = res.getOutputStream();
			 */

			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			writer = new PrintWriter(disposition, "UTF-8");

			for (Factura d : lista) {

				String cadena = "";
				String cuit = d.getCliente().getCuit() == null ? d.getCliente().getCuil() : d.getCliente().getCuit();

				// String importe =
				// df.format(d.getImporteNeto().doubleValue()==0D?d.getImporteExento().doubleValue():d.getImporteNeto().doubleValue()
				// );

				String importe = df.format(d.getImporteBaseSinImpuestos().doubleValue());

				String percep = df.format(d.getPercepcion().doubleValue()).substring(1);
                String alicuota =dfAli.format(d.getPercepcion().doubleValue()*100/d.getImporteBaseSinImpuestos().doubleValue());
				
				cadena += cuit.substring(0, 2) + "-" + cuit.substring(2, 10) + "-" + cuit.substring(10);
				cadena += sdf.format(d.getFecha());

				if (d.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)) {
					cadena += "F";
				} else if (d.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO)) {
					cadena += "D";
				} else if (d.getTipo().equalsIgnoreCase("NCR")) {
					cadena += "C";
					importe = "-" + importe.substring(1);
					percep = "-" + percep.substring(1);
				} else if (d.getTipo().equalsIgnoreCase("NCE")) {
					cadena += "H";
					importe = "-" + importe.substring(1);
					percep = "-" + percep.substring(1);
				} else if (d.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_RECIBO)) {
					cadena += "R";
				} else if (d.getTipo().equalsIgnoreCase("FCE")) {
					cadena += "E";
				} else {
					cadena += "O";
				}
				cadena += d.getLetra();
				cadena += d.getSucursal().substring(0);

				if (d.getNumero().trim().length() < 8) {
					cadena += String.format("%08d", Integer.valueOf(d.getNumero()));
				} else {
					cadena += d.getNumero().trim();
				}

				cadena += importe;
				cadena += alicuota;
				cadena += percep;
				cadena += "A";

				// importe=importe.replace(".",",");

				writer.println(cadena);

			}

			writer.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}

	private PrintWriter crearTxt_Retenciones_ARBA_IIBB_Compras(HttpServletRequest req, HttpServletResponse res,
			List<OrdenPago> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("00000000000.00");
		DecimalFormat dfAli = new DecimalFormat("00.00");
		PrintWriter writer = null;

		try {

			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			writer = new PrintWriter(disposition, "UTF-8");

			for (OrdenPago d : lista) {

				String cadena = "";
				String cuit = d.getCuit(); // d.getCliente().getCuit()==null?d.getCliente().getCuil():d.getCliente().getCuit();
				String importe = "0";
				String alicuota="";
				String montoImponible="";
				for (FormaPago f : d.getFormaPago()) {
					if (f.getPago() instanceof RetencionIIBB) {
						importe = df.format(f.getPago().getImporte().doubleValue());
						
						RetencionIIBB r=(RetencionIIBB) f.getPago();
						
						montoImponible=df.format( f.getPago().getImporte().doubleValue()/r.getAlicuota());
						alicuota=dfAli.format(r.getAlicuota()*100);
						break;
					}
				}

				cadena += cuit.substring(0, 2) + "-" + cuit.substring(2, 10) + "-" + cuit.substring(10);
				cadena += sdf.format(d.getFecha());
				cadena += "00001"; // d.getSucursal().substring(1);
				cadena += String.format("%08d", d.getId());
				cadena += montoImponible;
				cadena += alicuota;
				cadena += importe.substring(1);
				cadena += "A";

				writer.println(cadena);

			}

			writer.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}

	private PrintWriter crearTxt_Retenciones_ARBA_IIBB_Compras_A122(HttpServletRequest req, HttpServletResponse res,
			List<OrdenPago> lista, String fileName) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("0000000000000.00");
		DecimalFormat dfAli = new DecimalFormat("00.00");
		PrintWriter writer = null;

		try {

			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			writer = new PrintWriter(disposition, "UTF-8");

			for (OrdenPago d : lista) {

				String cadena = "";
				String cuit = d.getCuit(); 
				String importe = "0";
				String alicuota="";
				String montoImponible="";
				for (FormaPago f : d.getFormaPago()) {
					if (f.getPago() instanceof RetencionIIBB) {
						importe = df.format(f.getPago().getImporte().doubleValue());
						
						RetencionIIBB r=(RetencionIIBB) f.getPago();
						
						montoImponible=df.format( f.getPago().getImporte().doubleValue()/r.getAlicuota());
						alicuota=dfAli.format(r.getAlicuota()*100);
						break;
					}
				}

				cadena += String.format("%020d", d.getId());
				cadena += cuit;
				cadena += "00001"; // d.getSucursal().substring(1);
				cadena += sdf.format(d.getFecha());
				cadena += alicuota;
				cadena += montoImponible;

				writer.println(cadena);

			}

			writer.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}

	
	private void crearTxt_Pedido_Informe_Jubilados_Sitaci(HttpServletRequest req, HttpServletResponse res,
			List<String> lista, String fileName) {

		ServletOutputStream out = null;

		try {
			res.setContentType("text/plain");
			String disposition = "attachment; fileName=" + fileName;
			res.setHeader("Content-Disposition", disposition);

			res.setCharacterEncoding("UTF-8");
			out = res.getOutputStream();

			int cont = 0;
			for (String d : lista) {

				out.write(d.getBytes("UTF-8"));
				out.write("\r\n".getBytes());

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	private Integer comprobanteIntegracion(String tipo, String letra) {
		Integer tcp = 0;
		if ("FCP".equals(tipo) && "A".equals(letra)) {
			tcp = 1;
		} else if ("RCB".equals(tipo) && "A".equals(letra)) {
			tcp = 2;
		} else if ("FCP".equals(tipo) && "B".equals(letra)) {
			tcp = 3;
		} else if ("RCB".equals(tipo) && "B".equals(letra)) {
			tcp = 4;
		} else if ("FCP".equals(tipo) && "C".equals(letra)) {
			tcp = 5;
		} else if ("RCB".equals(tipo) && "C".equals(letra)) {
			tcp = 6;
		} else if ("FCP".equals(tipo) && "M".equals(letra)) {
			tcp = 7;
		} else if ("RCB".equals(tipo) && "M".equals(letra)) {
			tcp = 8;
		}
		return tcp;
	}

	private PrintWriter crearTxt_cuentas_interbanking_proveedores_FTP_Email(String accion, List<CuentasInterbaking> lista,
			String fileName, String entidad) {

		ServletOutputStream out = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		PrintWriter writer = null;
		try {
			String disposition = TMPDIR + FILE_SEPARATOR + fileName;
			String codigoCliente = "";
			if (entidad == null) {
				codigoCliente = ConstantesInterbanking.CODIGO_INTERBANKING;
			} else if ("UOMA".equals(entidad)) {
				codigoCliente = TraeListasServiceUtil.getSystemConfig("UOMA_CLIENTE_INTERBANKING");
			} else if ("AMTIMA".equals(entidad)) {
				codigoCliente = TraeListasServiceUtil.getSystemConfig("AMTIMA_CLIENTE_INTERBANKING");
			}

			writer = new PrintWriter(disposition, "UTF-8");

			String cadena;

			cadena = "";
			// cabecera
			cadena += StringUtils.leftPad("1", 1, '0'); // tipo Registro
			cadena += padRight(codigoCliente, 7); // Codigo de Cliente
			cadena += padRight("TERCEROS", 9); // Titular de la cuenta
			cadena += padRight(accion.equals("A")?  ConstantesInterbanking.ALTA:"MODIFICACION", 12); // Funcion a realizar
			cadena += StringUtils.leftPad(sdf.format(new Date()), 8, '0'); // Fecha de archivo
			cadena += padRight("", 163); // Filter (Espacion en blanco)

			writer.println(cadena);

			int cantidadCuentas = lista.size();
			for (CuentasInterbaking d : lista) {

				// Cuentas
				cadena = "";
				cadena += padRight("2", 1); // Tipo de registro
				cadena += padRight(d.getCbu(), 22); // CBU
				cadena += StringUtils.leftPad(d.getCuit(), 11, '0'); // CUIT
				cadena += padRight(formatiarRazonSocialInvalida(d.getDescripcion()).trim(), 40); // denominacion
				cadena += padRight("", 50); // //Referencia de uso
				cadena += padRight("", 76); // Filler
				writer.println(cadena);

				// USOS
				cadena = "";
				cadena += StringUtils.leftPad("3", 1, '0'); // Tipo de registro
				cadena += padRight(ConstantesInterbanking.PROVEEDORES, 3); // uso de la cuenta
				cadena += padRight("", 11); // Filler
				writer.println(cadena);

				// MAIL
				cadena = "";
				cadena += StringUtils.leftPad("4", 1, '0'); // Tipo de registro
				cadena += padRight(d.getEmail().trim(), 100); // Direccion Mail
				cadena += padRight("", 99); // Filler
				writer.println(cadena);

			}
			cadena = "";
			// Final
			cadena += padRight("5", 1); // Tipo de registro
			cadena += padRight(codigoCliente, 7); // Codigo de Cliente
			cadena += StringUtils.leftPad(String.valueOf(cantidadCuentas), 6, '0'); // Cantidad de cuentas
			cadena += padRight("", 186); // Filler

			writer.println(cadena);

			writer.close();

		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		return writer;
	}
	
	private void exportarInterbankingEmail(HttpServletRequest req,HttpServletResponse res,List<CuentasInterbaking> cuentas,OrdenesPagoInterbanking ordenesPagoInter,String entidad) throws IOException {
		SimpleDateFormat sdfecha = new SimpleDateFormat("yyyyMMdd");
		String nombreArchivoCuentas;
		String nombreArchivoPagos;
		String nombreArchivoOpError;
		String zipNombre;
		List<String> archivosNombre = new ArrayList<String>();
		int contador = 0;
		List<PagosInterbanking> pagosOk = null;
		List<OrdenPagoConError> pagosError = null;
		zipNombre = sdfecha.format(new Date()) + "_interbanking.zip";
		pagosOk = ordenesPagoInter.getListaPagos();

		nombreArchivoCuentas = "cuentas_" + sdfecha.format(new Date()) + ".txt";
		nombreArchivoPagos = "pagos_proveedores_" + sdfecha.format(new Date()) + ".txt";
		nombreArchivoOpError = "ordenes_pago_con_error_" + sdfecha.format(new Date()) + ".txt";
        List<CuentasInterbaking> ctaAlta=new ArrayList<CuentasInterbaking>();
        List<CuentasInterbaking> ctaModi=new ArrayList<CuentasInterbaking>();
		for(CuentasInterbaking c:cuentas) {
			if(c.getAccion().equals("ALTA")){
				ctaAlta.add(c);
			}else {
				ctaModi.add(c);
			}
		}
        
		String archivo_cuentas = TMPDIR + FILE_SEPARATOR;
		if (!cuentas.isEmpty()) {
			if(!ctaAlta.isEmpty()) {
				crearTxt_cuentas_interbanking_proveedores_FTP_Email("A",ctaAlta, "cuentas_" + sdfecha.format(new Date()) + "_alta.txt", entidad);
				archivosNombre.add("cuentas_" + sdfecha.format(new Date()) + "_alta.txt");
			}
			if(!ctaModi.isEmpty()) {
				crearTxt_cuentas_interbanking_proveedores_FTP_Email("M",ctaModi, "cuentas_" + sdfecha.format(new Date()) + "_modi.txt", entidad);
				archivosNombre.add("cuentas_" + sdfecha.format(new Date()) + "_modi.txt");
			}
			/*
			crearTxt_cuentas_interbanking_proveedores_FTP_Email(cuentas, nombreArchivoCuentas, entidad);
			archivosNombre.add(nombreArchivoCuentas);
            */
			
			archivosNombre.add(nombreArchivoPagos);

			if (ordenesPagoInter.getOdenConError() != null
					&& !ordenesPagoInter.getOdenConError().isEmpty()) {
				pagosError = ordenesPagoInter.getOdenConError();
				archivosNombre.add(nombreArchivoOpError);

			}

		} else {

			archivosNombre.add(nombreArchivoPagos);
			if (ordenesPagoInter.getOdenConError() != null
					&& !ordenesPagoInter.getOdenConError().isEmpty()) {
				pagosError = ordenesPagoInter.getOdenConError();
				archivosNombre.add(nombreArchivoOpError);
			}

		}

		crearTxt_Pagos_interbanking_FTP(pagosOk, nombreArchivoPagos);

		if (pagosError != null) {
			crearTxtOpConErrores(pagosError, nombreArchivoOpError);
		}

		byte[] buffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		int length;

		for (String arch : archivosNombre) {
			FileInputStream fin = new FileInputStream(archivo_cuentas + arch);
			zos.putNextEntry(new ZipEntry(arch));
			length = 0;
			while ((length = fin.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			fin.close();

		}

		zos.closeEntry();

		zos.close();

		ServletOutputStream sos = res.getOutputStream();
		res.setContentType("application/zip");
		res.setHeader("Content-Disposition", "attachment; filename=\"" + zipNombre + "\"");

		sos.write(baos.toByteArray());

		zos.flush();
		zos.close();
		sos.flush();

		
	}
	
	//funcion para mostrar errores limpios a la hora de descargar txt R331
	private String obtenerMensajeLimpio(Exception e) {
	    Throwable t = e;

	    while (t != null) {
	        if (t instanceof org.postgresql.util.PSQLException) {
	            org.postgresql.util.PSQLException pe = (org.postgresql.util.PSQLException) t;
	            if (pe.getServerErrorMessage() != null &&
	                pe.getServerErrorMessage().getMessage() != null) {
	                return pe.getServerErrorMessage().getMessage();
	            }
	        }
	        
	        if (t instanceof java.sql.SQLException) {
	            String msg = t.getMessage();
	            if (msg != null) {
	                int idx = msg.indexOf("\nWhere:");
	                if (idx > 0) {
	                    return msg.substring(0, idx).trim();
	                }
	                return msg.trim();
	            }
	        }

	        t = t.getCause();
	    }

	    return e.getMessage() != null ? e.getMessage() : "Error desconocido";
	}

}
