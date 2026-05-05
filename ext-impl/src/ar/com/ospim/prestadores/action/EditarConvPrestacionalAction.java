package ar.com.ospim.prestadores.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ar.com.ospim.liquidaciones.beans.*;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portlet.ActionResponseImpl;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.TipoPago;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional.EstadosConvPrest;
import ar.com.ospim.prestadores.services.ConvenioPrestacionalServiceUtil;
import ar.com.ospim.util.StringUtils;

/**
 * @author SVA
 *
 */
public class EditarConvPrestacionalAction extends PortletAction {

	private final Logger _log = Logger.getLogger(this.getClass());

	private static final String CMD_UPLOAD_XLS_PREVIEW = "uploadXlsPreview";
	private static final String XLS_PARAM_NAME = "archivo_xls";

	private static final String HEADER_CODIGO = "codigo";
	private static final String HEADER_PRESTACION_DESC = "prestaciondesc";
	private static final String HEADER_ID_PLAN = "idplan";
	private static final String HEADER_PLAN_DESC = "plandesc";
	private static final String HEADER_IMPORTE = "importe";

	private static final String DEFAULT_TIPO_VALORIZACION = "IMPORTE";
	private static final BigDecimal DEFAULT_PORCENTAJE = BigDecimal.ZERO;
	private static final BigDecimal DEFAULT_COSEGURO = BigDecimal.ZERO;
	private static final String LEGACY_TIPO_VAL_PORCENTAJE = "porcentaje";

	private static final String CMD_EXPORT_XLS = "exportXls";
	private static final String CMD_DOWNLOAD_XLS_MODELO = "downloadXlsModelo";
	private static final String XLS_MODELO_FILE_NAME = "modelo_convenio_prestacional.xls";
	private static final String ATTR_MODO_EDICION = "CONV_PREST_MODO_EDICION";

	private static final String ATTR_VOLVER_CONVENIOS_URL = "CONV_PREST_VOLVER_URL";

	@Override
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);

		_log.info("[EDIT-CONV-PREST][ACTION][START] Inicio processAction. cmd=" + cmd);

		if (CMD_UPLOAD_XLS_PREVIEW.equals(cmd)) {
			procesarPreviewXls(actionRequest, actionResponse);
			_log.info("[EDIT-CONV-PREST][ACTION][END] Fin processAction por preview XLS");
			return;
		}

		if (CMD_DOWNLOAD_XLS_MODELO.equals(cmd)) {
			descargarXlsModelo(actionRequest, actionResponse);
			_log.info("[EDIT-CONV-PREST][ACTION][END] Fin processAction por descarga XLS modelo");
			return;
		}

		if (CMD_EXPORT_XLS.equals(cmd)) {
			exportarXlsConvenioPrest(actionRequest, actionResponse);
			_log.info("[EDIT-CONV-PREST][ACTION][END] Fin processAction por export XLS");
			return;
		}

		_log.debug("[EDIT-CONV-PREST][ACTION][INFO] No se realiza lógica en processAction");
		_log.info("[EDIT-CONV-PREST][ACTION][END] Fin processAction");
	}

	private void exportarXlsConvenioPrest(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest).getSession();
		int idConvPrest = ParamUtil.getInteger(actionRequest, "id_convprest", 0);

		List<ConvenioPrestacionalDetalle> detalles = obtenerDetallesParaExportacion(session, idConvPrest);

		_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][DATA] idConvPrest=" + idConvPrest
				+ ", detallesExportar=" + (detalles != null ? detalles.size() : 0));

		if (detalles == null || detalles.isEmpty()) {
			throw new IllegalArgumentException("No hay detalles para exportar");
		}

		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;

		try {
			workbook = crearWorkbookExportacionDetalles(detalles);

			HttpServletResponse httpRes = ((ActionResponseImpl) actionResponse).getHttpServletResponse();
			String fileName = construirNombreArchivoExportacion(session);

			httpRes.reset();
			httpRes.setContentType("application/vnd.ms-excel");
			httpRes.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			httpRes.setHeader("Pragma", "public");

			outputStream = httpRes.getOutputStream();
			workbook.write(outputStream);
			outputStream.flush();

			setForward(actionRequest, ActionConstants.COMMON_NULL);

			_log.info("[EDIT-CONV-PREST][XLS][EXPORT][OK] Exportación XLS generada. fileName="
					+ fileName + ", cantidadDetalles=" + detalles.size());
		}
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e) {
					_log.warn("[EDIT-CONV-PREST][XLS][EXPORT][WARN] No se pudo cerrar outputStream", e);
				}
			}

			if (workbook != null) {
				try {
					workbook.close();
				}
				catch (Exception e) {
					_log.warn("[EDIT-CONV-PREST][XLS][EXPORT][WARN] No se pudo cerrar workbook", e);
				}
			}
		}
	}

	private HSSFWorkbook crearWorkbookExportacionDetalles(List<ConvenioPrestacionalDetalle> detalles) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("convenio_prestacional");

		int rowIndex = 0;

		Row headerRow = sheet.createRow(rowIndex++);
		escribirHeaderExportacion(headerRow);

		if (detalles != null) {
			for (ConvenioPrestacionalDetalle det : detalles) {
				if (det == null) {
					continue;
				}

				Row row = sheet.createRow(rowIndex++);
				escribirFilaDetalleExportacion(row, det);
			}
		}

		for (int i = 0; i < 5; i++) {
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}

	@SuppressWarnings("unchecked")
	private List<ConvenioPrestacionalDetalle> obtenerDetallesParaExportacion(HttpSession session, int idConvPrest) throws Exception {

		Object detallesSessionObj = session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
		if (detallesSessionObj instanceof List) {
			List<ConvenioPrestacionalDetalle> detallesSession =
					(List<ConvenioPrestacionalDetalle>) detallesSessionObj;

			if (detallesSession != null && !detallesSession.isEmpty()) {
				_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][SOURCE] Usando CONVENIO_PREST_DETALLES_EN_SESSION. cantidad="
						+ detallesSession.size());
				return detallesSession;
			}
		}

		Object detallesDesgloseObj = session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);
		if (detallesDesgloseObj instanceof List) {
			List<ConvenioPrestacionalDetalle> detallesDesglose =
					(List<ConvenioPrestacionalDetalle>) detallesDesgloseObj;

			if (detallesDesglose != null && !detallesDesglose.isEmpty()) {
				_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][SOURCE] Usando CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE. cantidad="
						+ detallesDesglose.size());
				return detallesDesglose;
			}
		}

		ConvenioPrestacional convenio =
				(ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);

		if (convenio != null && convenio.getConvenioPrestDetalle() != null && !convenio.getConvenioPrestDetalle().isEmpty()) {
			_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][SOURCE] Usando CONVENIO_PREST_EN_EDICION.convenioPrestDetalle. cantidad="
					+ convenio.getConvenioPrestDetalle().size());
			return convenio.getConvenioPrestDetalle();
		}

		if (idConvPrest > 0) {
			ConvenioPrestacional convenioDb = ConvenioPrestacionalServiceUtil.getConvenioPrestacional(idConvPrest);

			if (convenioDb != null && convenioDb.getConvenioPrestDetalle() != null && !convenioDb.getConvenioPrestDetalle().isEmpty()) {
				_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][SOURCE] Usando convenio recargado desde BD. cantidad="
						+ convenioDb.getConvenioPrestDetalle().size());
				return convenioDb.getConvenioPrestDetalle();
			}
		}

		_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][SOURCE] No se encontraron detalles en ninguna fuente");
		return new ArrayList<ConvenioPrestacionalDetalle>();
	}

	@Override
	public ActionForward render(ActionMapping mapping, ActionForm form,
								PortletConfig portletConfig, RenderRequest renderRequest,
								RenderResponse renderResponse) throws Exception {

		_log.info("[EDIT-CONV-PREST][RENDER][START] Inicio render EditarConvPrestacionalAction");

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = httpServletRequest.getSession();
		renderRequest.setAttribute(ATTR_MODO_EDICION, Boolean.valueOf(getModoEdicion(session)));
		User usuario = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		String msg = "";
		resolverBackURL(renderRequest, session, cmd);
		int idConvPrestCab = 0;
		String msgConvenioFail = (String) session.getAttribute("msgConvenioFail");
		if (StringUtils.checkNotEmpty(msgConvenioFail)) {
			renderRequest.setAttribute("msgConvenioFail", msgConvenioFail);
			session.removeAttribute("msgConvenioFail");
		}
		ConvenioPrestacional convPrestacional = null;

		_log.debug("[EDIT-CONV-PREST][RENDER][PARAMS] cmd=" + cmd
				+ ", user=" + (usuario != null ? usuario.getScreenName() : "null")
				+ ", sessionId=" + (session != null ? session.getId() : "null"));

		_log.debug("[EDIT-CONV-PREST][RENDER][LISTAS] Invocando cargarListas");
		cargarListas(renderRequest);
		_log.debug("[EDIT-CONV-PREST][RENDER][LISTAS] cargarListas finalizado");

		if (StringUtils.checkEmpty(cmd)) {
			_log.debug("[EDIT-CONV-PREST][RENDER][NO-CMD] No vino CMD");
			_log.info("[EDIT-CONV-PREST][RENDER][END] Fin render. Forward=portlet.prestadores.editar_convenio_prest_entry");
			renderRequest.setAttribute(ATTR_MODO_EDICION, Boolean.valueOf(getModoEdicion(session)));
			convPrestacional = (ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);
			if (convPrestacional != null) {
				renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
			}
			return mapping.findForward(getForward(renderRequest, "portlet.prestadores.editar_convenio_prest_entry"));
		}

		if (CMD_EXPORT_XLS.equals(cmd) || CMD_DOWNLOAD_XLS_MODELO.equals(cmd)) {

			_log.debug("[EDIT-CONV-PREST][RENDER][SKIP] cmd=" + cmd + " en render. Se preserva session.");

			convPrestacional = (ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);

			renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
			renderRequest.setAttribute(
					Constants.CMD,
					(convPrestacional != null && convPrestacional.getId() > 0) ? Constants.UPDATE : Constants.SAVE
			);

			_log.info("[EDIT-CONV-PREST][RENDER][END] Fin render skip " + cmd);
			renderRequest.setAttribute(ATTR_MODO_EDICION, Boolean.valueOf(getModoEdicion(session)));
			return mapping.findForward(getForward(renderRequest, "portlet.prestadores.editar_convenio_prest_entry"));
		}

		List<ConvenioPrestacionalDetalle> detalles = obtenerDetallesEnSession(session);

		_log.debug("[EDIT-CONV-PREST][RENDER][SESSION] detalles en session="
				+ (detalles != null ? detalles.size() : "null"));

		if (CMD_UPLOAD_XLS_PREVIEW.equals(cmd)) {

			_log.info("[EDIT-CONV-PREST][XLS][RENDER][START] Inicio render preview XLS");

			convPrestacional = (ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);

			renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
			renderRequest.setAttribute(
					Constants.CMD,
					(convPrestacional != null && convPrestacional.getId() > 0) ? Constants.UPDATE : Constants.SAVE
			);

			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);

			_log.debug("[EDIT-CONV-PREST][XLS][RENDER] convenioPreview=" + convPrestacional);
			_log.debug("[EDIT-CONV-PREST][XLS][RENDER] Se preserva lista reconciliada en session");
			_log.info("[EDIT-CONV-PREST][XLS][RENDER][END] Fin render preview XLS");
			setModoEdicion(session, renderRequest, true);
		}

		else if (Constants.ADD.equals(cmd)) {

			_log.info("[EDIT-CONV-PREST][ADD][START] Inicio flujo ADD");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			limpiarSessionConvenio(session);

			renderRequest.setAttribute(Constants.CMD, Constants.SAVE);

			_log.debug("[EDIT-CONV-PREST][ADD][RENDER] Se setea CMD=SAVE");
			_log.info("[EDIT-CONV-PREST][ADD][END] Fin flujo ADD");
			setModoEdicion(session, renderRequest, true);
		}

		else if (Constants.SAVE.equals(cmd)) {
			setModoEdicion(session, renderRequest, true);

			_log.info("[EDIT-CONV-PREST][SAVE][START] Inicio flujo SAVE");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			convPrestacional = resolverCabeceraParaPersistencia(renderRequest, session);
			_log.debug("[EDIT-CONV-PREST][SAVE][CABECERA] convPrestacional=" + convPrestacional);

			renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

			if (convPrestacional == null || convPrestacional.getPrestador() == null
					|| convPrestacional.getPrestador().getId_prestador() <= 0) {

				SessionErrors.add(renderRequest, "conv-prest-xls-formato");
				renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
				_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Cabecera inválida o incompleta");
			}
			else {

				boolean convenioDuplicado = ConvenioPrestacionalServiceUtil
						.validarConvenioPrestadorVigente(convPrestacional.getPrestador().getId_prestador());

				_log.debug("[EDIT-CONV-PREST][SAVE][VALIDACION] convenioDuplicado=" + convenioDuplicado
						+ ", idPrestador=" + convPrestacional.getPrestador().getId_prestador());

				if (convenioDuplicado) {

					SessionErrors.add(renderRequest, "conv-prest-duplicado");
					renderRequest.setAttribute(Constants.CMD, Constants.SAVE);

					_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Se agrega SessionError conv-prest-duplicado");
				}
				else if (detalles == null || detalles.size() == 0) {

					SessionErrors.add(renderRequest, "conv-prest-sin-items");
					renderRequest.setAttribute(Constants.CMD, Constants.SAVE);

					_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Se agrega SessionError conv-prest-sin-items");
				}
				else {

					convPrestacional.setConvenioPrestDetalle(detalles);

					_log.debug("[EDIT-CONV-PREST][SAVE][DETALLES] Se asocian detalles. cantidad="
							+ convPrestacional.getConvenioPrestDetalle().size());

					String errorCabecera = validarCabeceraMinimaParaPersistencia(convPrestacional, detalles);
					String mensaje = null;
					boolean puedeInsertar = true;

					if (errorCabecera != null) {
						puedeInsertar = false;
						SessionErrors.add(renderRequest, errorCabecera);
						renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
						renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
						_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Cabecera mínima inválida. error=" + errorCabecera);
					}
					else {
						try {
							aplicarDefaultsPersistencia(convPrestacional);
							resolverPrestacionesParaPersistencia(convPrestacional);

							// NUEVO: consolida duplicados funcionales en convenios nuevos
							normalizarDetallesNuevosSinPersistenciaPrevia(convPrestacional);

							// Sigue valiendo para duplicados exactos
							validarDuplicadosPorCodigoFechaPlan(convPrestacional.getConvenioPrestDetalle());

							mensaje = validarDetalleExistenteConDiagnostico(convPrestacional);

							if (mensaje != null) {
								puedeInsertar = false;
								SessionErrors.add(renderRequest, "conv-prest-validaciones");
								renderRequest.setAttribute("msgConvenioFail", mensaje);
								renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
								renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

								_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Validación detalle existente. mensaje=" + mensaje);
							}
							else {
								renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
							}
						}
						catch (IllegalArgumentException e) {
							puedeInsertar = false;
							msg = e.getMessage();

							SessionErrors.add(renderRequest, "conv-prest-validaciones");
							renderRequest.setAttribute("msgConvenioFail", msg);
							renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
							renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

							_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Error resolviendo prestaciones para persistencia: " + msg);
						}
					}

					_log.debug("[EDIT-CONV-PREST][SAVE][VALIDACION] mensajeValidacion=" + mensaje);

					if (mensaje != null) {

						puedeInsertar = false;
						msg = mensaje;

						SessionErrors.add(renderRequest, "conv-prest-validaciones");
						renderRequest.setAttribute("msgConvenioFail", msg);
						renderRequest.setAttribute(Constants.CMD, Constants.SAVE);

						_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Se agrega SessionError conv-prest-validaciones");
						_log.debug("[EDIT-CONV-PREST][SAVE][RENDER] msgConvenioFail=" + msg);
					}

					if (puedeInsertar) {

						_log.debug("[EDIT-CONV-PREST][SAVE][SERVICE] Insertando convenio");
						try {
							idConvPrestCab = ConvenioPrestacionalServiceUtil.insertarConvenioPrestacional(
									convPrestacional,
									usuario != null ? usuario.getScreenName() : "");
						}
						catch (IllegalArgumentException e) {
							puedeInsertar = false;
							SessionErrors.add(renderRequest, "conv-prest-validaciones");
							renderRequest.setAttribute("msgConvenioFail", e.getMessage());
							renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
							renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
							_log.debug("[EDIT-CONV-PREST][SAVE][ERROR] Regla temporal de negocio: " + e.getMessage());
						}

						_log.debug("[EDIT-CONV-PREST][SAVE][SERVICE] idConvPrestCab insertado=" + idConvPrestCab);

						convPrestacional = ConvenioPrestacionalServiceUtil.getConvenioPrestacional(idConvPrestCab);

						_log.debug("[EDIT-CONV-PREST][SAVE][SERVICE] convenio recargado=" + convPrestacional);

						msg = LanguageUtil.get(defaultLocale, "insert-convenio") + " " + convPrestacional.getId();

						SessionMessages.add(renderRequest, "insertConvenioOk");

						renderRequest.setAttribute("msgConvenioOk", msg);
						renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
						renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);

						session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
						session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);

						_log.debug("[EDIT-CONV-PREST][SAVE][OK] SessionMessage insertConvenioOk");
						_log.debug("[EDIT-CONV-PREST][SAVE][RENDER] msgConvenioOk=" + msg);
						_log.debug("[EDIT-CONV-PREST][SAVE][RENDER] Se setea CMD=UPDATE");
					}
				}
			}

			_log.info("[EDIT-CONV-PREST][SAVE][END] Fin flujo SAVE");
		}

		else if (Constants.VIEW.equals(cmd)) {
			setModoEdicion(session, renderRequest, false);

			_log.info("[EDIT-CONV-PREST][VIEW][START] Inicio flujo VIEW");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			idConvPrestCab = ParamUtil.getInteger(renderRequest, "id_convenio", 0);

			_log.debug("[EDIT-CONV-PREST][VIEW][PARAMS] id_convenio=" + idConvPrestCab);

			convPrestacional = ConvenioPrestacionalServiceUtil.getConvenioPrestacional(idConvPrestCab);

			renderRequest.setAttribute(Constants.CMD, Constants.VIEW);
			renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);

			_log.debug("[EDIT-CONV-PREST][VIEW][SERVICE] convenioPrestacional=" + convPrestacional);
			_log.debug("[EDIT-CONV-PREST][VIEW][RENDER] Se setea CMD=VIEW");
			_log.debug("[EDIT-CONV-PREST][VIEW][RENDER] Se setea convenio en edición");
			_log.debug("[EDIT-CONV-PREST][VIEW][SESSION] Se limpia CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE");
			_log.info("[EDIT-CONV-PREST][VIEW][END] Fin flujo VIEW");
		}

		else if (Constants.DELETE.equals(cmd)) {

			_log.info("[EDIT-CONV-PREST][DELETE][START] Inicio flujo DELETE");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			idConvPrestCab = ParamUtil.getInteger(renderRequest, "id_convenio_prest", 0);

			_log.debug("[EDIT-CONV-PREST][DELETE][PARAMS] id_convenio_prest=" + idConvPrestCab);

			ConvenioPrestacionalServiceUtil.eliminarConvenioPrestacional(idConvPrestCab,
					usuario != null ? usuario.getScreenName() : "");

			msg = LanguageUtil.get(defaultLocale, "delete-convenio") + " " + idConvPrestCab;

			SessionMessages.add(renderRequest, "deleteConvenioOk");
			renderRequest.setAttribute("msgConvenioOk", msg);

			BusquedaConvenioPrestacionalFiltro filtro =
					(BusquedaConvenioPrestacionalFiltro) session.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_FILTRO);

			List<ConvenioPrestacional> busqueda =
					ConvenioPrestacionalServiceUtil.buscarConveniosPrestacionales(filtro);

			session.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS);
			session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS, busqueda);

			_log.debug("[EDIT-CONV-PREST][DELETE][SERVICE] Convenio eliminado lógicamente");
			_log.debug("[EDIT-CONV-PREST][DELETE][OK] SessionMessage deleteConvenioOk");
			_log.debug("[EDIT-CONV-PREST][DELETE][RENDER] msgConvenioOk=" + msg);
			_log.debug("[EDIT-CONV-PREST][DELETE][SESSION] filtroBusqueda=" + filtro);
			_log.debug("[EDIT-CONV-PREST][DELETE][SERVICE] resultadosBusqueda=" + (busqueda != null ? busqueda.size() : "null"));
			_log.debug("[EDIT-CONV-PREST][DELETE][SESSION] Se actualiza BUSQUEDA_CONVENIOS_PRESTAC_RESULTS");
			_log.info("[EDIT-CONV-PREST][DELETE][END] Fin flujo DELETE con forward eliminar_convenio_prest_entry");
			limpiarSessionConvenio(session);
			return mapping.findForward(getForward(renderRequest, "portlet.prestadores.eliminar_convenio_prest_entry"));
		}

		else if (Constants.APPROVE.equals(cmd) || Constants.REJECT.equals(cmd)) {
			setModoEdicion(session, renderRequest, false);

			_log.info("[EDIT-CONV-PREST][CHANGE-STATE][START] Inicio flujo cambio de estado");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			idConvPrestCab = ParamUtil.getInteger(renderRequest, "id_convenio_prest", 0);

			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][PARAMS] id_convenio_prest=" + idConvPrestCab
					+ ", nuevoEstadoCmd=" + cmd);

			ConvenioPrestacionalServiceUtil.cambiarEstadoConvenioPrestacional(
					idConvPrestCab,
					Constants.APPROVE.equalsIgnoreCase(cmd)
							? ConvenioPrestacional.EstadosConvPrest.APROBADO.getIntValue()
							: ConvenioPrestacional.EstadosConvPrest.RECHAZADO.getIntValue(),
					usuario != null ? usuario.getScreenName() : "");

			msg = LanguageUtil.get(defaultLocale, "update-estado-convenio") + " " + idConvPrestCab;

			SessionMessages.add(renderRequest, "updateEstadoConvPrestoOk");
			renderRequest.setAttribute("msgConvenioOk", msg);

			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);

			convPrestacional = ConvenioPrestacionalServiceUtil.getConvenioPrestacional(idConvPrestCab);

			renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
			renderRequest.setAttribute(Constants.CMD, Constants.VIEW);

			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][SERVICE] Estado actualizado");
			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][OK] SessionMessage updateEstadoConvPrestoOk");
			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][RENDER] msgConvenioOk=" + msg);
			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][SESSION] Se limpian detalles en session");
			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][SERVICE] convenio recargado=" + convPrestacional);
			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][RENDER] Se setea convenio en edición");
			_log.debug("[EDIT-CONV-PREST][CHANGE-STATE][RENDER] Se setea CMD=VIEW");
			_log.info("[EDIT-CONV-PREST][CHANGE-STATE][END] Fin flujo cambio de estado");
		}

		else if (Constants.EDIT.equals(cmd)) {

			_log.info("[EDIT-CONV-PREST][EDIT][START] Inicio flujo EDIT");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			idConvPrestCab = ParamUtil.getInteger(renderRequest, "id_convenio_prest", 0);

			_log.debug("[EDIT-CONV-PREST][EDIT][PARAMS] id_convenio_prest=" + idConvPrestCab);

			convPrestacional = ConvenioPrestacionalServiceUtil.getConvenioPrestacional(idConvPrestCab);

			renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);

			limpiarSessionConvenio(session);
			setModoEdicion(session, renderRequest, true);

			renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION,
					convPrestacional != null ? convPrestacional.getConvenioPrestDetalle()
							: new ArrayList<ConvenioPrestacionalDetalle>());

			_log.debug("[EDIT-CONV-PREST][EDIT][SERVICE] convenioPrestacional=" + convPrestacional);
			_log.debug("[EDIT-CONV-PREST][EDIT][RENDER] Se setea CMD=UPDATE");
			_log.debug("[EDIT-CONV-PREST][EDIT][SESSION] Se limpian atributos previos");
			_log.debug("[EDIT-CONV-PREST][EDIT][SESSION] Se cargan detalles en session. cantidad="
					+ (convPrestacional != null && convPrestacional.getConvenioPrestDetalle() != null
					? convPrestacional.getConvenioPrestDetalle().size() : "null"));
			_log.info("[EDIT-CONV-PREST][EDIT][END] Fin flujo EDIT");
		}

		else if (Constants.UPDATE.equals(cmd)) {
			setModoEdicion(session, renderRequest, true);

			_log.info("[EDIT-CONV-PREST][UPDATE][START] Inicio flujo UPDATE");
			_log.debug("Usuario: " + (usuario != null ? usuario.getScreenName() : "null") + " cmd: " + cmd);

			convPrestacional = resolverCabeceraParaPersistencia(renderRequest, session);
			_log.debug("[EDIT-CONV-PREST][UPDATE][CABECERA] convPrestacional=" + convPrestacional);

			if (convPrestacional == null || convPrestacional.getId() <= 0) {
				SessionErrors.add(renderRequest, "conv-prest-validaciones");
				renderRequest.setAttribute("msgConvenioFail", "No se puede actualizar un convenio sin ID válido");
				renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
				renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

				_log.debug("[EDIT-CONV-PREST][UPDATE][ERROR] Update inválido. idConvenio="
						+ (convPrestacional != null ? convPrestacional.getId() : "null"));

				_log.info("[EDIT-CONV-PREST][UPDATE][END] Fin flujo UPDATE por id inválido");
			}
			else {
				if (convPrestacional != null) {
					convPrestacional.setEstado(EstadosConvPrest.CARGADO);
				}

				if (detalles == null || detalles.size() == 0) {

					SessionErrors.add(renderRequest, "conv-prest-sin-items");
					renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
					renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

					_log.debug("[EDIT-CONV-PREST][UPDATE][ERROR] Se agrega SessionError conv-prest-sin-items");
				} else {

					convPrestacional.setConvenioPrestDetalle(detalles);

					_log.debug("[EDIT-CONV-PREST][UPDATE][DETALLES] Se asocian detalles. cantidad="
							+ convPrestacional.getConvenioPrestDetalle().size());

					boolean actualizo = false;

					if (SessionErrors.isEmpty(renderRequest)) {

						String errorCabecera = validarCabeceraMinimaParaPersistencia(convPrestacional, detalles);

						if (errorCabecera != null) {
							SessionErrors.add(renderRequest, errorCabecera);
							renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
							renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
							_log.debug("[EDIT-CONV-PREST][UPDATE][ERROR] Cabecera mínima inválida. error=" + errorCabecera);
						} else {
							try {
								aplicarDefaultsPersistencia(convPrestacional);
								resolverPrestacionesParaPersistencia(convPrestacional);
								validarDuplicadosPorCodigoFechaPlan(convPrestacional.getConvenioPrestDetalle());

								if (SessionErrors.isEmpty(renderRequest)) {
									try {
										_log.debug("[EDIT-CONV-PREST][UPDATE][SERVICE] Actualizando convenio");
										ConvenioPrestacionalServiceUtil.actualizarConvenioPrestacional(
												convPrestacional,
												usuario != null ? usuario.getScreenName() : "");
										actualizo = true;
									}
									catch (IllegalArgumentException e) {
										SessionErrors.add(renderRequest, "conv-prest-validaciones");
										renderRequest.setAttribute("msgConvenioFail", e.getMessage());
										renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
										renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
										_log.debug("[EDIT-CONV-PREST][UPDATE][ERROR] Regla temporal de negocio: " + e.getMessage());
									}
								}
							} catch (IllegalArgumentException e) {
								SessionErrors.add(renderRequest, "conv-prest-validaciones");
								renderRequest.setAttribute("msgConvenioFail", e.getMessage());
								renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
								renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
								_log.debug("[EDIT-CONV-PREST][UPDATE][ERROR] Error resolviendo prestaciones para persistencia: " + e.getMessage());
							}
						}
					} else {
						_log.debug("[EDIT-CONV-PREST][UPDATE][SKIP] No se actualiza por SessionErrors");
					}

					if (actualizo) {
						idConvPrestCab = convPrestacional.getId();
						convPrestacional = ConvenioPrestacionalServiceUtil.getConvenioPrestacional(idConvPrestCab);

						msg = LanguageUtil.get(defaultLocale, "update-convenio") + " " + idConvPrestCab;

						SessionMessages.add(renderRequest, "updateConvenioOk");
						renderRequest.setAttribute("msgConvenioOk", msg);

						_log.debug("[EDIT-CONV-PREST][UPDATE][SERVICE] convenio recargado=" + convPrestacional);
						_log.debug("[EDIT-CONV-PREST][UPDATE][OK] SessionMessage updateConvenioOk");
						_log.debug("[EDIT-CONV-PREST][UPDATE][RENDER] msgConvenioOk=" + msg);
					}

					renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
					renderRequest.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);
				}
			}
			_log.debug("[EDIT-CONV-PREST][UPDATE][RENDER] Se setea CMD=UPDATE y convenio en edición");
			_log.info("[EDIT-CONV-PREST][UPDATE][END] Fin flujo UPDATE");
		}

		persistirConvenioEnSession(session, convPrestacional);

		renderRequest.setAttribute(ATTR_MODO_EDICION, Boolean.valueOf(getModoEdicion(session)));

		_log.debug("[EDIT-CONV-PREST][RENDER][SESSION] Se persiste convenio en edición y detalles en session");
		_log.debug("[EDIT-CONV-PREST][RENDER][SESSION] convPrestacional=" + convPrestacional
				+ ", detallesSession="
				+ (convPrestacional != null && convPrestacional.getConvenioPrestDetalle() != null
				? convPrestacional.getConvenioPrestDetalle().size() : "null"));
		_log.debug("[EDIT-CONV-PREST][RENDER][MODO] modoEdicionFinal=" + getModoEdicion(session));

		_log.info("[EDIT-CONV-PREST][RENDER][END] Fin render. Forward=portlet.prestadores.editar_convenio_prest_entry");
		return mapping.findForward(getForward(renderRequest, "portlet.prestadores.editar_convenio_prest_entry"));
	}

	private void procesarPreviewXls(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest).getSession();
		UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);

		File file = uploadReq.getFile(XLS_PARAM_NAME);
		String fileName = uploadReq.getFileName(XLS_PARAM_NAME);

		ConvenioPrestacional convenioActual = null;

		_log.info("[EDIT-CONV-PREST][XLS][START] Inicio preview XLS. fileName=" + fileName);

		if (file == null || !file.exists() || file.length() <= 0) {
			SessionErrors.add(actionRequest, "conv-prest-xls-sin-archivo");
			actionResponse.setRenderParameter(Constants.CMD, CMD_UPLOAD_XLS_PREVIEW);
			return;
		}

		if (!StringUtils.checkEmpty(fileName) && !fileName.toLowerCase().endsWith(".xls")) {
			SessionErrors.add(actionRequest, "conv-prest-xls-extension");
			actionResponse.setRenderParameter(Constants.CMD, CMD_UPLOAD_XLS_PREVIEW);
			return;
		}

		try {
			convenioActual = resolverCabeceraParaPreview(uploadReq, session);

			if (convenioActual == null) {
				convenioActual = new ConvenioPrestacional();
			}
			if (convenioActual.getEstado() == null) {
				convenioActual.setEstado(EstadosConvPrest.CARGADO);
			}

			Date fechaDesdeImportacion = resolverFechaDesdeImportacion(uploadReq);

			List<ConvenioPrestacionalDetalle> detallesActuales = obtenerDetallesEnSession(session);

			ConvenioPrestacional convenioImportado =
					parsearConvenioPrestacionalDesdeXls(file, fechaDesdeImportacion, session, convenioActual);

			List<ConvenioPrestacionalDetalle> detallesImportados =
					convenioImportado != null ? convenioImportado.getConvenioPrestDetalle()
							: new ArrayList<ConvenioPrestacionalDetalle>();

			List<ConvenioPrestacionalDetalle> detallesReemplazo =
					reconciliarDetalles(detallesActuales, detallesImportados);

			convenioActual.setConvenioPrestDetalle(detallesReemplazo);

			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convenioActual);
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detallesReemplazo);
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);
			session.removeAttribute("msgConvenioFail");


			SessionMessages.add(actionRequest, "previewConvenioXlsOk");

			_log.info("[EDIT-CONV-PREST][XLS][OK] Preview reemplazado completamente. detallesAnteriores="
					+ (detallesActuales != null ? detallesActuales.size() : 0)
					+ ", detallesImportados=" + (detallesImportados != null ? detallesImportados.size() : 0)
					+ ", detallesResultado=" + detallesReemplazo.size()
					+ ", fechaDesdeImportacion=" + fechaDesdeImportacion
					+ ", idPrestador=" + (convenioActual.getPrestador() != null ? convenioActual.getPrestador().getId_prestador() : 0));

			actionResponse.setRenderParameter(Constants.CMD, CMD_UPLOAD_XLS_PREVIEW);

		}
		catch (Exception e) {
			_log.error("[EDIT-CONV-PREST][XLS][ERROR] Error procesando XLS", e);

			if (convenioActual != null) {
				session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convenioActual);
			}

			session.setAttribute("msgConvenioFail", e.getMessage());
			SessionErrors.add(actionRequest, "conv-prest-validaciones");
			actionResponse.setRenderParameter(Constants.CMD, CMD_UPLOAD_XLS_PREVIEW);
		}
	}

	private ConvenioPrestacional parsearConvenioPrestacionalDesdeXls(File file,
																	 Date fechaDesdeDefault,
																	 HttpSession session,
																	 ConvenioPrestacional convenioContexto) throws Exception {

		FileInputStream fis = null;
		HSSFWorkbook workbook = null;

		try {
			if (fechaDesdeDefault == null) {
				throw new IllegalArgumentException("La Fecha Desde de Datos Prestación es obligatoria antes de importar el XLS");
			}

			fis = new FileInputStream(file);
			workbook = new HSSFWorkbook(fis);

			HSSFSheet sheetDet = obtenerPrimeraHojaConDatos(workbook);
			int idPrestador = convenioContexto != null && convenioContexto.getPrestador() != null
					? convenioContexto.getPrestador().getId_prestador()
					: 0;

			if (idPrestador <= 0) {
				throw new IllegalArgumentException("Debe seleccionar un prestador antes de importar el XLS");
			}

			Map<Integer, String> planesHabilitadosPrestador =
					obtenerPlanesHabilitadosPrestador(idPrestador);

			List<ConvenioPrestacionalDetalle> detalles =
					parsearDetalles(sheetDet, fechaDesdeDefault, session, planesHabilitadosPrestador);

			if (detalles == null || detalles.isEmpty()) {
				throw new IllegalArgumentException("El XLS no contiene filas de detalle válidas");
			}

			ConvenioPrestacional convenio = new ConvenioPrestacional();
			convenio.setId(convenioContexto != null ? convenioContexto.getId() : 0);
			convenio.setEstado(convenioContexto != null && convenioContexto.getEstado() != null
					? convenioContexto.getEstado()
					: EstadosConvPrest.CARGADO);
			convenio.setPrestador(convenioContexto != null ? convenioContexto.getPrestador() : null);
			convenio.setTipoPago(convenioContexto != null ? convenioContexto.getTipoPago() : null);
			convenio.setCondicionDePago(convenioContexto != null ? convenioContexto.getCondicionDePago() : null);
			convenio.setDiaRecepcion(convenioContexto != null ? convenioContexto.getDiaRecepcion() : 0);
			convenio.setVigencia(convenioContexto != null ? convenioContexto.getVigencia() : null);
			convenio.setVencimiento(convenioContexto != null ? convenioContexto.getVencimiento() : null);
			convenio.setConvenioPrestDetalle(detalles);

			_log.info("[EDIT-CONV-PREST][XLS][PARSE-CONVENIO][OK] Convenio parseado desde XLS. detalles=" + detalles.size()
					+ ", idConvenio=" + convenio.getId()
					+ ", idPrestador=" + (convenio.getPrestador() != null ? convenio.getPrestador().getId_prestador() : 0)
					+ ", fechaDesdeDetalles=" + fechaDesdeDefault);

			return convenio;
		}
		finally {
			if (workbook != null) {
				workbook.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
	}

	private List<ConvenioPrestacionalDetalle> parsearDetalles(HSSFSheet sheet,
															  Date fechaDesdeDefault,
															  HttpSession session,
															  Map<Integer, String> planesHabilitadosPrestador) throws Exception {

		if (fechaDesdeDefault == null) {
			throw new IllegalArgumentException("La Fecha Desde de Datos Prestación es obligatoria para construir fecha_desde");
		}

		List<ConvenioPrestacionalDetalle> detalles = new ArrayList<ConvenioPrestacionalDetalle>();
		Map<Integer, PlanNoHabilitadoInfo> planesNoHabilitados = new LinkedHashMap<Integer, PlanNoHabilitadoInfo>();

		Row headerRow = obtenerFilaHeader(sheet);
		if (headerRow == null) {
			throw new IllegalArgumentException("El XLS no contiene encabezado");
		}

		java.util.Map<String, Integer> columnas = mapearColumnas(headerRow);

		Integer colCodigo = columnas.get(HEADER_CODIGO);
		Integer colPrestDesc = columnas.get(HEADER_PRESTACION_DESC);
		Integer colIdPlan = columnas.get(HEADER_ID_PLAN);
		Integer colPlanDesc = columnas.get(HEADER_PLAN_DESC);
		Integer colImporte = columnas.get(HEADER_IMPORTE);

		if (colCodigo == null || colIdPlan == null || colImporte == null) {
			throw new IllegalArgumentException(
					"El XLS debe informar obligatoriamente las columnas codigo, id_plan e importe.");
		}

		java.util.Set<String> keysImportadas = new java.util.HashSet<String>();
		int filasProcesadas = 0;
		int filasAgregadas = 0;

		for (int i = headerRow.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);

			if (row == null || filaVacia(row)) {
				continue;
			}

			filasProcesadas++;

			String codigo = toString(cell(row, colCodigo.intValue()));
			String prestacionDesc = colPrestDesc != null ? toString(cell(row, colPrestDesc.intValue())) : null;
			Integer idPlan = toInteger(cell(row, colIdPlan.intValue()));
			String planDesc = colPlanDesc != null ? toString(cell(row, colPlanDesc.intValue())) : null;
			BigDecimal importe = toBigDecimal(cell(row, colImporte.intValue()), false);

			if (StringUtils.checkEmpty(codigo)) {
				throw new IllegalArgumentException("DETALLE fila " + (i + 1) + ": código obligatorio");
			}

			validarCodigoSiInformado(codigo, i + 1);

			if (idPlan == null || idPlan.intValue() <= 0) {
				throw new IllegalArgumentException("DETALLE fila " + (i + 1) + ": id_plan obligatorio");
			}

			if (importe == null) {
				throw new IllegalArgumentException("DETALLE fila " + (i + 1) + ": importe obligatorio");
			}

			validarPlanObligatorio(idPlan.intValue(), i + 1, session);

			if (planesHabilitadosPrestador == null
					|| !planesHabilitadosPrestador.containsKey(idPlan.intValue())) {

				String nombrePlan = resolverNombrePlanNoHabilitado(idPlan.intValue(), planDesc);

				PlanNoHabilitadoInfo info = planesNoHabilitados.get(idPlan.intValue());

				if (info == null) {
					info = new PlanNoHabilitadoInfo(idPlan.intValue(), nombrePlan);
					planesNoHabilitados.put(idPlan.intValue(), info);
				}

				info.addFila(i + 1);
				continue;
			}

			PrestacionResuelta prestacionResuelta =
					resolverPrestacionParaImportacion(codigo, prestacionDesc, i + 1);

			Prestacion prestacionPreview =
					new Prestacion(prestacionResuelta.getIdPrestacion(), prestacionResuelta.getDescripcion());

			ConvenioPrestacionalDetalle det = new ConvenioPrestacionalDetalle();
			det.setId(0);
			det.setFechaDesde(fechaDesdeDefault);
			det.setFechaHasta(null);
			det.setPrestacion(prestacionPreview);
			det.setCodigo(prestacionResuelta.getCodigo());
			det.setIdPlan(idPlan.intValue());
			det.setPlanDescripcion(planDesc);
			det.setImporte(importe);
			det.setTipoValorizacion(DEFAULT_TIPO_VALORIZACION);
			det.setPorcentaje(DEFAULT_PORCENTAJE);
			det.setCoseguro(DEFAULT_COSEGURO);
			det.setServicio(null);
			det.setEstado(ConvenioPrestacionalDetalle.ESTADOS.NUEVO);

			aplicarDefaultsDetalle(det);

			String key = buildBusinessKey(det);

			if (!keysImportadas.add(key)) {
				throw new IllegalArgumentException(
						"El XLS contiene filas duplicadas por código y plan. No se realizó la importación. Clave duplicada: "
								+ key + " (fila " + (i + 1) + ")");
			}

			detalles.add(det);
			filasAgregadas++;
		}

		if (!planesNoHabilitados.isEmpty()) {
			StringBuilder sb = new StringBuilder("Se encontraron planes no habilitados para el prestador:");

			for (PlanNoHabilitadoInfo info : planesNoHabilitados.values()) {
				sb.append("\n- Plan ")
						.append(info.getNombrePlan())
						.append(": filas ");

				for (int j = 0; j < info.getFilas().size(); j++) {
					if (j > 0) {
						sb.append(", ");
					}
					sb.append(info.getFilas().get(j));
				}
			}

			throw new IllegalArgumentException(sb.toString());
		}

		_log.info("[EDIT-CONV-PREST][XLS][PARSE][OK] Parse de detalles finalizado. filasProcesadas="
				+ filasProcesadas
				+ ", filasAgregadas=" + filasAgregadas
				+ ", resultado=" + detalles.size()
				+ ", fechaDesdeAplicada=" + fechaDesdeDefault);

		return detalles;
	}

	private void aplicarDefaultsPersistencia(ConvenioPrestacional convenio) {

		if (convenio == null || convenio.getConvenioPrestDetalle() == null) {
			return;
		}

		Date vigencia = convenio.getVigencia();
		int nroFila = 0;

		for (ConvenioPrestacionalDetalle det : convenio.getConvenioPrestDetalle()) {
			nroFila++;

			if (det == null) {
				continue;
			}

			if (det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				continue;
			}

			if (det.getFechaDesde() == null) {
				det.setFechaDesde(vigencia);
			}

			if (det.getFechaDesde() == null) {
				throw new IllegalArgumentException("DETALLE fila " + nroFila + ": fecha_desde obligatoria");
			}

			if (det.getFechaHasta() != null && det.getFechaHasta().before(det.getFechaDesde())) {
				throw new IllegalArgumentException("DETALLE fila " + nroFila + ": fecha_hasta no puede ser menor a fecha_desde");
			}

			// servicio deja de persistirse
			det.setServicio(null);

			// IMPORTANTE:
			// ya NO se hace det.setFechaHasta(null),
			// porque rompería la cadena temporal previamente normalizada.
			aplicarDefaultsDetalle(det);
		}
	}

	private void resolverPrestacionesParaPersistencia(ConvenioPrestacional convenio) throws Exception {

		if (convenio == null || convenio.getConvenioPrestDetalle() == null) {
			return;
		}

		java.util.Map<String, Integer> cachePrestaciones = new java.util.HashMap<String, Integer>();

		int nroFila = 0;

		for (ConvenioPrestacionalDetalle det : convenio.getConvenioPrestDetalle()) {
			nroFila++;

			if (det == null) {
				continue;
			}

			if (det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				continue;
			}

			String codigo = det.getCodigo() != null ? det.getCodigo().trim() : null;
			validarCodigoSiInformado(codigo, nroFila);

			String key = normalizarClave(codigo);

			Integer idPrestacion = cachePrestaciones.get(key);

			if (idPrestacion == null) {
				idPrestacion = buscarIdPrestacionPorCodigo(codigo);

				if (idPrestacion == null || idPrestacion.intValue() <= 0) {
					throw new IllegalArgumentException("DETALLE fila " + nroFila
							+ ": no se encontró id_prestacion para código " + codigo);
				}

				cachePrestaciones.put(key, idPrestacion);
			}

			String descripcion = det.getPrestacion() != null
					? det.getPrestacion().getDescripcion()
					: null;

			det.setPrestacion(new Prestacion(idPrestacion.intValue(), descripcion));
			det.setCodigo(codigo);
		}
	}

	private Integer buscarIdPrestacionPorCodigo(String codigo) throws Exception {

		if (StringUtils.checkEmpty(codigo)) {
			return null;
		}

		return ConvenioPrestacionalServiceUtil.getIdPrestacionPorCodigo(codigo.trim());
	}

	private String buscarDescripcionPrestacionPorCodigo(String codigoNorm) throws Exception {

		if (StringUtils.checkEmpty(codigoNorm)) {
		return null;
		}

		return ConvenioPrestacionalServiceUtil.getDescripcionPrestacionPorCodigo(codigoNorm.trim());
	}

	private String validarCabeceraMinimaParaPersistencia(ConvenioPrestacional convPrestacional,
														 List<ConvenioPrestacionalDetalle> detalles) {

		if (convPrestacional == null) {
			return "conv-prest-xls-formato";
		}

		if (convPrestacional.getPrestador() == null
				|| convPrestacional.getPrestador().getId_prestador() <= 0) {
			return "conv-prest-xls-formato";
		}

		if (detalles != null && !detalles.isEmpty() && convPrestacional.getVigencia() == null) {
			return "conv-prest-vigencia-obligatoria";
		}

		return null;
	}

	private String toString(Cell cell) {
		if (cell == null) {
			return null;
		}

		try {
			DataFormatter formatter = new DataFormatter();
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
					.getCreationHelper()
					.createFormulaEvaluator();

			String value = formatter.formatCellValue(cell, evaluator);

			if (value != null) {
				value = value.trim();
			}

			return StringUtils.checkEmpty(value) ? null : value;
		}
		catch (Exception e) {
			return null;
		}
	}

	private Integer toInteger(Cell cell) {
		String value = toString(cell);
		if (StringUtils.checkEmpty(value)) {
			return null;
		}

		try {
			return new BigDecimal(value.trim().replace(",", ".")).intValue();
		}
		catch (Exception e) {
			return null;
		}
	}

	private BigDecimal toBigDecimal(Cell cell, boolean esPorcentaje) {
		if (cell == null) {
			return null;
		}

		String formatted = null;

		try {
			DataFormatter formatter = new DataFormatter();
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
					.getCreationHelper()
					.createFormulaEvaluator();

			formatted = formatter.formatCellValue(cell, evaluator);

			if (formatted != null) {
				formatted = formatted.trim();
			}
		}
		catch (Exception e) {
			formatted = null;
		}

		try {
			CellType cellType = cell.getCellType();

			if (cellType == CellType.FORMULA) {
				cellType = cell.getCachedFormulaResultType();
			}

			if (cellType == CellType.NUMERIC) {
				BigDecimal numericValue = BigDecimal.valueOf(cell.getNumericCellValue());

				if (esPorcentaje && formatted != null && formatted.indexOf('%') >= 0) {
					return numericValue.multiply(new BigDecimal("100"));
				}

				return numericValue;
			}
		}
		catch (Exception e) {
			// fallback al parseo textual
		}

		if (StringUtils.checkEmpty(formatted)) {
			return null;
		}

		try {
			String normalized = formatted.trim().replace(" ", "");
			boolean tienePorcentaje = normalized.indexOf('%') >= 0;

			normalized = normalized.replace("%", "");

			if (normalized.indexOf(',') >= 0 && normalized.indexOf('.') >= 0) {
				if (normalized.lastIndexOf(',') > normalized.lastIndexOf('.')) {
					normalized = normalized.replace(".", "").replace(",", ".");
				}
				else {
					normalized = normalized.replace(",", "");
				}
			}
			else if (normalized.indexOf(',') >= 0) {
				normalized = normalized.replace(",", ".");
			}

			BigDecimal parsed = new BigDecimal(normalized);

			if (esPorcentaje && tienePorcentaje) {
				return parsed;
			}

			return parsed;
		}
		catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<ConvenioPrestacionalDetalle> obtenerDetallesEnSession(HttpSession session) {
		Object detalles = session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
		if (detalles == null) {
			return new ArrayList<ConvenioPrestacionalDetalle>();
		}
		return (List<ConvenioPrestacionalDetalle>) detalles;
	}

	private void limpiarSessionConvenio(HttpSession session) {
		session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);
		session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLE_EN_EDICION);
		session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
		session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);
		session.removeAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
		session.removeAttribute(ATTR_MODO_EDICION);
	}

	private void persistirConvenioEnSession(HttpSession session, ConvenioPrestacional convPrestacional) {

		if (convPrestacional != null) {
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION, convPrestacional);

			if (convPrestacional.getConvenioPrestDetalle() != null) {
				session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION,
						convPrestacional.getConvenioPrestDetalle());
			} else {
				session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			}
		} else {
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
		}
	}

	private ConvenioPrestacional resolverCabeceraParaPersistencia(RenderRequest renderRequest, HttpSession session) throws Exception {

		ConvenioPrestacional fromRequest = this.getConvenioPrestCabeceraFromRequest(renderRequest);
		ConvenioPrestacional fromSession =
				(ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);

		if (fromSession == null) {
			return fromRequest;
		}

		if (fromRequest.getId() <= 0 && fromSession.getId() > 0) {
			fromRequest.setId(fromSession.getId());
		}

		if (fromRequest.getPrestador() == null
				|| fromRequest.getPrestador().getId_prestador() <= 0) {
			fromRequest.setPrestador(fromSession.getPrestador());
		}

		if (fromRequest.getDiaRecepcion() <= 0 && fromSession.getDiaRecepcion() > 0) {
			fromRequest.setDiaRecepcion(fromSession.getDiaRecepcion());
		}

		if (StringUtils.checkEmpty(fromRequest.getCondicionDePago())
				&& !StringUtils.checkEmpty(fromSession.getCondicionDePago())) {
			fromRequest.setCondicionDePago(fromSession.getCondicionDePago());
		}

		if ((fromRequest.getTipoPago() == null || fromRequest.getTipoPago().getId() <= 0)
				&& fromSession.getTipoPago() != null) {
			fromRequest.setTipoPago(fromSession.getTipoPago());
		}

		if (fromRequest.getVigencia() == null && fromSession.getVigencia() != null) {
			fromRequest.setVigencia(fromSession.getVigencia());
		}

		if (fromRequest.getVencimiento() == null && fromSession.getVencimiento() != null) {
			fromRequest.setVencimiento(fromSession.getVencimiento());
		}

		return fromRequest;
	}

	private void cargarListas(RenderRequest renderRequest) throws SystemException {

		_log.debug("[EDIT-CONV-PREST][LISTAS][START] Inicio cargarListas");
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		boolean estanPreCargadasLasListas =
				session.getAttribute(WebKeysLiquidaciones.TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION) != null
						&& session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION) != null
						&& session.getAttribute(WebKeysLiquidaciones.TIPOS_NOMENCLADORES_EN_SESSION) != null;

		_log.debug("[EDIT-CONV-PREST][LISTAS][CHECK] estanPreCargadasLasListas=" + estanPreCargadasLasListas);

		if (!estanPreCargadasLasListas) {
			_log.debug("[EDIT-CONV-PREST][LISTAS][LOAD] Cargando listas en session");
			session.setAttribute(WebKeysLiquidaciones.TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION,
					TraeListasServiceUtil.getTiposPagoContratos(renderRequest));
			session.setAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION,
					TraeListasServiceUtil.getPlanesOspim());
			session.setAttribute(WebKeysLiquidaciones.TIPOS_NOMENCLADORES_EN_SESSION,
					TraeListasServiceUtil.getTiposNomenclador());
			_log.debug("[EDIT-CONV-PREST][LISTAS][LOAD] Listas cargadas en session");
		}
		else {
			_log.debug("[EDIT-CONV-PREST][LISTAS][SKIP] Las listas ya estaban precargadas");
		}

		_log.debug("[EDIT-CONV-PREST][LISTAS][END] Fin cargarListas");
	}

	private ConvenioPrestacional getConvenioPrestCabeceraFromRequest(javax.portlet.PortletRequest request) {

		return buildConvenioPrestCabecera(
				ParamUtil.getInteger(request, "id_convprest", 0),
				ParamUtil.getInteger(request, "id_prestador", 0),
				ParamUtil.getString(request, "cuit_prestador", ""),
				ParamUtil.getString(request, "nombre_prestador", ""),
				ParamUtil.getInteger(request, "estado", EstadosConvPrest.CARGADO.getIntValue()),
				ParamUtil.getInteger(request, "dia_recepcion", 0),
				ParamUtil.getString(request, "condicion_pago", null),
				ParamUtil.getInteger(request, "forma_de_pago", 0),
				ParamUtil.getString(request, "vigenciaDia"),
				ParamUtil.getString(request, "vigenciaMes"),
				ParamUtil.getString(request, "vigenciaAnio"),
				ParamUtil.getString(request, "vencimientoDia"),
				ParamUtil.getString(request, "vencimientoMes"),
				ParamUtil.getString(request, "vencimientoAnio")
		);
	}

	private HSSFSheet obtenerPrimeraHojaConDatos(HSSFWorkbook workbook) {
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			HSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet != null && hojaTieneDatos(sheet)) {
				return sheet;
			}
		}
		throw new IllegalArgumentException("El XLS no contiene hojas con detalle");
	}

	private boolean hojaTieneDatos(HSSFSheet sheet) {
		Row header = obtenerFilaHeader(sheet);
		if (header == null) {
			return false;
		}

		for (int i = header.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null && !filaVacia(row)) {
				return true;
			}
		}
		return false;
	}

	private Row obtenerFilaHeader(HSSFSheet sheet) {
		if (sheet == null) {
			return null;
		}

		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null && !filaVacia(row)) {
				return row;
			}
		}
		return null;
	}

	private Cell cell(Row row, int index) {
		if (row == null) {
			return null;
		}
		return row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
	}

	private boolean filaVacia(Row row) {
		if (row == null) {
			return true;
		}

		short lastCellNum = row.getLastCellNum();
		if (lastCellNum < 0) {
			return true;
		}

		for (int i = 0; i < lastCellNum; i++) {
			Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			String value = toString(cell);
			if (!StringUtils.checkEmpty(value)) {
				return false;
			}
		}
		return true;
	}

	private String normalizarHeader(String value) {
		if (StringUtils.checkEmpty(value)) {
			return null;
		}

		String normalized = Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD);
		normalized = normalized.replaceAll("\\p{M}", "");
		normalized = normalized.replaceAll("[^a-z0-9]", "");

		return StringUtils.checkEmpty(normalized) ? null : normalized;
	}

	private int obtenerSiguienteIdTemporal(List<ConvenioPrestacionalDetalle> detalles) {
		int min = 0;

		if (detalles != null) {
			for (ConvenioPrestacionalDetalle det : detalles) {
				if (det != null && det.getId() < min) {
					min = det.getId();
				}
			}
		}

		return min - 1;
	}

	private java.util.Map<String, Integer> mapearColumnas(Row headerRow) {
		java.util.Map<String, Integer> columnas = new java.util.HashMap<String, Integer>();

		short lastCellNum = headerRow.getLastCellNum();
		for (int i = 0; i < lastCellNum; i++) {
			String valor = toString(cell(headerRow, i));
			String header = normalizarHeader(valor);

			if (!StringUtils.checkEmpty(header)) {
				if (columnas.containsKey(header)) {
					throw new IllegalArgumentException("El XLS contiene encabezados duplicados: " + header);
				}
				columnas.put(header, Integer.valueOf(i));
			}
		}

		return columnas;
	}

	private List<ConvenioPrestacionalDetalle> reconciliarDetalles(
			List<ConvenioPrestacionalDetalle> existentes,
			List<ConvenioPrestacionalDetalle> importados) {

		if (importados == null || importados.isEmpty()) {
			throw new IllegalArgumentException("El XLS no contiene filas válidas para reemplazar la lista actual");
		}

		List<ConvenioPrestacionalDetalle> resultado = new ArrayList<ConvenioPrestacionalDetalle>();
		java.util.Set<String> keysImportadas = new java.util.LinkedHashSet<String>();

		int nextTempId = obtenerSiguienteIdTemporal(existentes);
		int agregados = 0;

		for (ConvenioPrestacionalDetalle imp : importados) {
			if (imp == null) {
				continue;
			}

			String key = buildBusinessKey(imp);

			if (!keysImportadas.add(key)) {
				_log.warn("[EDIT-CONV-PREST][XLS][REPLACE][ERROR] Se detectó duplicado interno antes de reemplazar. key=" + key);
				throw new IllegalArgumentException("El XLS contiene filas duplicadas por código y plan. No se realizó la importación. Clave duplicada: " + key);
			}

			imp.setId(nextTempId--);
			imp.setEstado(ConvenioPrestacionalDetalle.ESTADOS.NUEVO);

			resultado.add(imp);
			agregados++;
		}

		_log.info("[EDIT-CONV-PREST][XLS][REPLACE][OK] Reemplazo completo preparado. existentesAnteriores="
				+ (existentes != null ? existentes.size() : 0)
				+ ", importados=" + (importados != null ? importados.size() : 0)
				+ ", resultado=" + resultado.size()
				+ ", agregados=" + agregados);

		return resultado;
	}

	private String buildBusinessKey(ConvenioPrestacionalDetalle det) {
		if (det == null) {
			return "";
		}

		String codigo = normalizarClave(det.getCodigo());
		String fecha = formatearFechaKey(det.getFechaDesde());

		return codigo + "|" + fecha + "|" + det.getIdPlan();
	}

	private String formatearFechaKey(Date fecha) {
		if (fecha == null) {
			return "";
		}

		return new SimpleDateFormat("yyyyMMdd").format(fecha);
	}

	private String normalizarClave(String value) {
		if (StringUtils.checkEmpty(value)) {
			return "";
		}
		return value.trim().toLowerCase();
	}

	private void validarPlanObligatorio(int idPlan, int nroFila, HttpSession session) {

		if (idPlan <= 0) {
			throw new IllegalArgumentException("DETALLE fila " + nroFila + ": id_plan inválido");
		}

		Object planesObj = session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION);
		if (!(planesObj instanceof List)) {
			return;
		}

		List<?> planes = (List<?>) planesObj;

		for (Object plan : planes) {
			Integer id = extraerIdGenerico(plan, "getIdPlan", "getId_plan", "getId");
			if (id != null && id.intValue() == idPlan) {
				return;
			}
		}

		throw new IllegalArgumentException("DETALLE fila " + nroFila + ": id_plan inexistente (" + idPlan + ")");
	}

	private Integer extraerIdGenerico(Object obj, String... getters) {
		if (obj == null || getters == null) {
			return null;
		}

		for (String getter : getters) {
			try {
				Method m = obj.getClass().getMethod(getter);
				Object value = m.invoke(obj);
				if (value instanceof Number) {
					return Integer.valueOf(((Number) value).intValue());
				}
			}
			catch (Exception e) {
				// probar siguiente getter
			}
		}

		return null;
	}

	private void validarCodigoSiInformado(String codigo, int nroFila) {
		if (StringUtils.checkEmpty(codigo)) {
			return;
		}

		if (codigo.trim().length() > 10) {
			throw new IllegalArgumentException("DETALLE fila " + nroFila + ": código supera longitud máxima (10)");
		}
	}

	private ConvenioPrestacional resolverCabeceraParaPreview(UploadPortletRequest uploadReq, HttpSession session) throws Exception {

		ConvenioPrestacional fromRequest = getConvenioPrestCabeceraFromUploadRequest(uploadReq);
		ConvenioPrestacional fromSession =
				(ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);

		if (fromSession == null) {
			return fromRequest;
		}

		if (fromRequest.getId() <= 0 && fromSession.getId() > 0) {
			fromRequest.setId(fromSession.getId());
		}

		if (fromRequest.getPrestador() == null
				|| fromRequest.getPrestador().getId_prestador() <= 0) {
			fromRequest.setPrestador(fromSession.getPrestador());
		}

		if (fromRequest.getDiaRecepcion() <= 0 && fromSession.getDiaRecepcion() > 0) {
			fromRequest.setDiaRecepcion(fromSession.getDiaRecepcion());
		}

		if (StringUtils.checkEmpty(fromRequest.getCondicionDePago())
				&& !StringUtils.checkEmpty(fromSession.getCondicionDePago())) {
			fromRequest.setCondicionDePago(fromSession.getCondicionDePago());
		}

		if ((fromRequest.getTipoPago() == null || fromRequest.getTipoPago().getId() <= 0)
				&& fromSession.getTipoPago() != null) {
			fromRequest.setTipoPago(fromSession.getTipoPago());
		}

		if (fromRequest.getVigencia() == null && fromSession.getVigencia() != null) {
			fromRequest.setVigencia(fromSession.getVigencia());
		}

		if (fromRequest.getVencimiento() == null && fromSession.getVencimiento() != null) {
			fromRequest.setVencimiento(fromSession.getVencimiento());
		}

		if (fromRequest.getEstado() == null && fromSession.getEstado() != null) {
			fromRequest.setEstado(fromSession.getEstado());
		}

		return fromRequest;
	}

	private ConvenioPrestacional getConvenioPrestCabeceraFromUploadRequest(UploadPortletRequest request) {

		return buildConvenioPrestCabecera(
				getInteger(request, "id_convprest", 0),
				getInteger(request, "id_prestador", 0),
				getString(request, "cuit_prestador", ""),
				getString(request, "nombre_prestador", ""),
				getInteger(request, "estado", EstadosConvPrest.CARGADO.getIntValue()),
				getInteger(request, "dia_recepcion", 0),
				getString(request, "condicion_pago", null),
				getInteger(request, "forma_de_pago", 0),
				getString(request, "vigenciaDia", null),
				getString(request, "vigenciaMes", null),
				getString(request, "vigenciaAnio", null),
				getString(request, "vencimientoDia", null),
				getString(request, "vencimientoMes", null),
				getString(request, "vencimientoAnio", null)
		);
	}

	private ConvenioPrestacional buildConvenioPrestCabecera(int idConvPrest,
															int idPrestador,
															String cuitPrestador,
															String nombrePrestador,
															int estado,
															int diaRecepcion,
															String condicionPago,
															int idFormaDePago,
															String fechaVigDia,
															String fechaVigMes,
															String fechaVigAnio,
															String fechaVencDia,
															String fechaVencMes,
															String fechaVencAnio) {

		_log.debug("[EDIT-CONV-PREST][CABECERA][START] Inicio buildConvenioPrestCabecera");
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");

		ConvenioPrestacional convPres = new ConvenioPrestacional();

		_log.debug("[EDIT-CONV-PREST][CABECERA][PARAMS] idConvPrest=" + idConvPrest
				+ ", idPrestador=" + idPrestador
				+ ", cuitPrestador=" + cuitPrestador
				+ ", nombrePrestador=" + nombrePrestador
				+ ", estado=" + estado
				+ ", diaRecepcion=" + diaRecepcion
				+ ", condicionPago=" + condicionPago
				+ ", idFormaDePago=" + idFormaDePago);

		Date fechaVigencia = null;
		try {
			if (StringUtils.checkNotEmpty(fechaVigDia)
					&& StringUtils.checkNotEmpty(fechaVigMes)
					&& StringUtils.checkNotEmpty(fechaVigAnio)) {

				fechaVigencia = formatoDeFechas.parse(fechaVigDia + "/"
						+ (Integer.parseInt(fechaVigMes) + 1) + "/"
						+ fechaVigAnio);
				_log.debug("[EDIT-CONV-PREST][CABECERA][FECHA] fechaVigencia=" + fechaVigencia);
			}
		} catch (Exception e) {
			fechaVigencia = null;
			_log.error("[EDIT-CONV-PREST][CABECERA][FECHA][ERROR] Error parseando fechaVigencia con valores "
					+ fechaVigDia + "/" + fechaVigMes + "/" + fechaVigAnio, e);
		}

		Date fechaVencimiento = null;
		if (StringUtils.checkNotEmpty(fechaVencDia)
				&& StringUtils.checkNotEmpty(fechaVencMes)
				&& StringUtils.checkNotEmpty(fechaVencAnio)) {
			try {
				fechaVencimiento = formatoDeFechas.parse(fechaVencDia + "/"
						+ (Integer.parseInt(fechaVencMes) + 1) + "/"
						+ fechaVencAnio);
				_log.debug("[EDIT-CONV-PREST][CABECERA][FECHA] fechaVencimiento=" + fechaVencimiento);
			} catch (Exception e) {
				fechaVencimiento = null;
				_log.error("[EDIT-CONV-PREST][CABECERA][FECHA][ERROR] Error parseando fechaVencimiento con valores "
						+ fechaVencDia + "/" + fechaVencMes + "/" + fechaVencAnio, e);
			}
		} else {
			fechaVencimiento = null;
			_log.debug("[EDIT-CONV-PREST][CABECERA][FECHA] fechaVencimiento no informada");
		}

		convPres.setId(idConvPrest);
		switch (estado) {
			case 1:
				convPres.setEstado(EstadosConvPrest.CARGADO);
				_log.debug("[EDIT-CONV-PREST][CABECERA][ESTADO] Estado CARGADO");
				break;
			case 2:
				convPres.setEstado(EstadosConvPrest.APROBADO);
				_log.debug("[EDIT-CONV-PREST][CABECERA][ESTADO] Estado APROBADO");
				break;
			case 3:
				convPres.setEstado(EstadosConvPrest.RECHAZADO);
				_log.debug("[EDIT-CONV-PREST][CABECERA][ESTADO] Estado RECHAZADO");
				break;
			default:
				convPres.setEstado(EstadosConvPrest.CARGADO);
				_log.debug("[EDIT-CONV-PREST][CABECERA][ESTADO] Estado default CARGADO");
				break;
		}

		convPres.setCondicionDePago(condicionPago);
		convPres.setDiaRecepcion(diaRecepcion);
		convPres.setTipo_pago(new TipoPago(idFormaDePago, ""));
		convPres.setPrestador(new Prestador(cuitPrestador, idPrestador, nombrePrestador));
		convPres.setVigencia(fechaVigencia);
		convPres.setVencimiento(fechaVencimiento);

		_log.debug("[EDIT-CONV-PREST][CABECERA][END] convPres=" + convPres);
		return convPres;
	}

	private String getString(UploadPortletRequest request, String param, String defaultValue) {
		try {
			String value = request.getParameter(param);
			return value != null ? value : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private int getInteger(UploadPortletRequest request, String param, int defaultValue) {
		try {
			String value = request.getParameter(param);
			if (StringUtils.checkEmpty(value)) {
				return defaultValue;
			}
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private PrestacionResuelta resolverPrestacionParaImportacion(String codigo, String prestacionDesc, int nroFila) throws Exception {
		String codigoNorm = StringUtils.checkNotEmpty(codigo) ? codigo.trim() : null;
		String descNorm = StringUtils.checkNotEmpty(prestacionDesc) ? prestacionDesc.trim() : null;

		if (codigoNorm == null) {
			throw new IllegalArgumentException("DETALLE fila " + nroFila + ": código obligatorio");
		}

		Integer idPrestacion = buscarIdPrestacionPorCodigo(codigoNorm);

		if (idPrestacion == null || idPrestacion.intValue() <= 0) {
			throw new IllegalArgumentException("DETALLE fila " + nroFila + ": no se encontró prestación para código " + codigoNorm);
		}

		String descripcionResuelta = descNorm;
		if (StringUtils.checkEmpty(descripcionResuelta)) {
			descripcionResuelta = buscarDescripcionPrestacionPorCodigo(codigoNorm);
		}

		return new PrestacionResuelta(idPrestacion.intValue(), codigoNorm, descripcionResuelta);
	}

	private static class PrestacionResuelta {
		private final int idPrestacion;
		private final String codigo;
		private final String descripcion;

		public PrestacionResuelta(int idPrestacion, String codigo, String descripcion) {
			this.idPrestacion = idPrestacion;
			this.codigo = codigo;
			this.descripcion = descripcion;
		}

		public int getIdPrestacion() { return idPrestacion; }
		public String getCodigo() { return codigo; }
		public String getDescripcion() { return descripcion; }
	}

	private Date resolverFechaDesdeImportacion(UploadPortletRequest uploadReq) {

		String dia = getString(uploadReq, "prestacionFechaDesdeDia", null);
		String mes = getString(uploadReq, "prestacionFechaDesdeMes", null);
		String anio = getString(uploadReq, "prestacionFechaDesdeAnio", null);

		if (StringUtils.checkEmpty(dia)
				|| StringUtils.checkEmpty(mes)
				|| StringUtils.checkEmpty(anio)) {
			throw new IllegalArgumentException("Debe informar la Fecha Desde en Datos Prestación antes de importar el XLS");
		}

		try {
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha = formato.parse(dia + "/" + (Integer.parseInt(mes) + 1) + "/" + anio);

			_log.debug("[EDIT-CONV-PREST][XLS][FECHA-DESDE] Fecha desde importación resuelta desde Datos Prestación=" + fecha);

			return fecha;
		}
		catch (Exception e) {
			_log.error("[EDIT-CONV-PREST][XLS][FECHA-DESDE][ERROR] Error parseando Fecha Desde de Datos Prestación. valores="
					+ dia + "/" + mes + "/" + anio, e);
			throw new IllegalArgumentException("La Fecha Desde de Datos Prestación es inválida");
		}
	}

	private void escribirHeaderExportacion(Row headerRow) {
		escribirCeldaTexto(headerRow, 0, "codigo");
		escribirCeldaTexto(headerRow, 1, "prestacion_desc");
		escribirCeldaTexto(headerRow, 2, "id_plan");
		escribirCeldaTexto(headerRow, 3, "plan_desc");
		escribirCeldaTexto(headerRow, 4, "importe");
	}

	private void escribirFilaDetalleExportacion(Row row, ConvenioPrestacionalDetalle det) {

		escribirCeldaTexto(row, 0, safe(det.getCodigo()));
		escribirCeldaTexto(row, 1, det.getPrestacion() != null ? safe(det.getPrestacion().getDescripcion()) : "");
		escribirCeldaTexto(row, 2, det.getIdPlan() > 0 ? String.valueOf(det.getIdPlan()) : "");
		escribirCeldaTexto(row, 3, safe(det.getPlanDescripcion()));
		escribirCeldaTexto(row, 4, formatearNumeroExportacionSiTieneValor(det.getImporte()));

		_log.debug("[EDIT-CONV-PREST][XLS][EXPORT][ROW] rowNum=" + row.getRowNum()
				+ ", codigo=" + det.getCodigo()
				+ ", idPlan=" + det.getIdPlan()
				+ ", importe=" + det.getImporte());
	}

	private void escribirCeldaTexto(Row row, int columnIndex, String value) {
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(value != null ? value : "");
	}

	private String formatearNumeroExportacion(BigDecimal value) {

		if (value == null) {
			return "";
		}

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');

		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
		decimalFormat.setGroupingUsed(true);

		return decimalFormat.format(value);
	}

	private String safe(String value) {
		return value != null ? value : "";
	}

	private String construirNombreArchivoExportacion(HttpSession session) {

		ConvenioPrestacional convenio =
				(ConvenioPrestacional) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);

		int idConvenio = convenio != null ? convenio.getId() : 0;
		int idPrestador = convenio != null && convenio.getPrestador() != null
				? convenio.getPrestador().getId_prestador() : 0;

		return "convenio_prestacional_" + idConvenio + "_prestador_" + idPrestador + ".xls";
	}

	private String formatearNumeroExportacionSiTieneValor(BigDecimal value) {

		if (value == null || BigDecimal.ZERO.compareTo(value) == 0) {
			return "";
		}

		return formatearNumeroExportacion(value);
	}

	private void validarDuplicadosPorCodigoFechaPlan(List<ConvenioPrestacionalDetalle> detalles) {

		if (detalles == null || detalles.isEmpty()) {
			return;
		}

		Set<String> keys = new HashSet<String>();
		int nroFila = 0;

		for (ConvenioPrestacionalDetalle det : detalles) {
			nroFila++;

			if (det == null) {
				continue;
			}

			if (det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				continue;
			}

			String key = buildBusinessKeyValidacion(det);

			if (!keys.add(key)) {
				throw new IllegalArgumentException(
						"DETALLE fila " + nroFila
								+ ": existe un ítem duplicado con misma prestación/código, misma fecha desde y mismo plan. Clave: " + key);
			}
		}
	}

	private String buildBusinessKeyValidacion(ConvenioPrestacionalDetalle det) {
		if (det == null) {
			return "";
		}

		String identidad;

		if (det.getPrestacion() != null && det.getPrestacion().getId() > 0) {
			identidad = "prest:" + det.getPrestacion().getId();
		} else {
			identidad = "cod:" + normalizarClave(det.getCodigo());
		}

		return identidad + "|" + formatearFechaKey(det.getFechaDesde()) + "|" + det.getIdPlan();
	}

	private void descargarXlsModelo(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;

		try {
			workbook = crearWorkbookModeloXls();

			HttpServletResponse httpRes = ((ActionResponseImpl) actionResponse).getHttpServletResponse();

			httpRes.reset();
			httpRes.setContentType("application/vnd.ms-excel");
			httpRes.setHeader("Content-Disposition", "attachment; filename=\"" + XLS_MODELO_FILE_NAME + "\"");
			httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			httpRes.setHeader("Pragma", "public");

			outputStream = httpRes.getOutputStream();
			workbook.write(outputStream);
			outputStream.flush();

			setForward(actionRequest, ActionConstants.COMMON_NULL);

			_log.info("[EDIT-CONV-PREST][XLS][MODELO][OK] Archivo modelo generado correctamente");
		}
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e) {
					_log.warn("[EDIT-CONV-PREST][XLS][MODELO][WARN] No se pudo cerrar outputStream", e);
				}
			}

			if (workbook != null) {
				try {
					workbook.close();
				}
				catch (Exception e) {
					_log.warn("[EDIT-CONV-PREST][XLS][MODELO][WARN] No se pudo cerrar workbook", e);
				}
			}
		}
	}

	private HSSFWorkbook crearWorkbookModeloXls() {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("modelo_convenio_prestacional");

		Row headerRow = sheet.createRow(0);
		escribirHeaderExportacion(headerRow);

		Row exampleRow = sheet.createRow(1);
		escribirCeldaTexto(exampleRow, 0, "010119");
		escribirCeldaTexto(exampleRow, 1, "MODULO DE TRATAMIENTO Y DIAGNOSTICO");
		escribirCeldaTexto(exampleRow, 2, "21");
		escribirCeldaTexto(exampleRow, 3, "COBERTURA TOTAL MA");
		escribirCeldaTexto(exampleRow, 4, "25000,00");

		for (int i = 0; i < 5; i++) {
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}

	private String validarDetalleExistenteConDiagnostico(ConvenioPrestacional convPrestacional) throws Exception {

		_log.info("[EDIT-CONV-PREST][VALIDACION-DET][START] Inicio validación de detalle con diagnóstico");

		if (convPrestacional == null) {
			_log.warn("[EDIT-CONV-PREST][VALIDACION-DET][WARN] convPrestacional es null");
			return null;
		}

		List<ConvenioPrestacionalDetalle> detalles = convPrestacional.getConvenioPrestDetalle();

		_log.debug("[EDIT-CONV-PREST][VALIDACION-DET][CAB] idConvenio=" + convPrestacional.getId()
				+ ", idPrestador=" + (convPrestacional.getPrestador() != null ? convPrestacional.getPrestador().getId_prestador() : 0)
				+ ", cantidadDetalles=" + (detalles != null ? detalles.size() : 0)
				+ ", vigencia=" + formatearFechaDebug(convPrestacional.getVigencia())
				+ ", vencimiento=" + formatearFechaDebug(convPrestacional.getVencimiento()));

		logDetallesDiagnosticoAction(detalles);

		String mensaje = ConvenioPrestacionalServiceUtil.validarDetalleExistente(convPrestacional);

		if (StringUtils.checkNotEmpty(mensaje)) {
			_log.warn("[EDIT-CONV-PREST][VALIDACION-DET][RESULT] mensaje=" + mensaje);
		} else {
			_log.debug("[EDIT-CONV-PREST][VALIDACION-DET][RESULT] Sin conflictos detectados");
		}

		_log.info("[EDIT-CONV-PREST][VALIDACION-DET][END] Fin validación de detalle con diagnóstico");

		return mensaje;
	}

	private void logDetallesDiagnosticoAction(List<ConvenioPrestacionalDetalle> detalles) {

		if (detalles == null || detalles.isEmpty()) {
			_log.debug("[EDIT-CONV-PREST][VALIDACION-DET][DETAILS] Sin detalles para loguear");
			return;
		}

		int idx = 0;

		for (ConvenioPrestacionalDetalle det : detalles) {
			idx++;

			if (det == null) {
				_log.debug("[EDIT-CONV-PREST][VALIDACION-DET][DETAIL] item=" + idx + " -> null");
				continue;
			}

			_log.debug("[EDIT-CONV-PREST][VALIDACION-DET][DETAIL] item=" + idx
					+ ", id=" + det.getId()
					+ ", estado=" + det.getEstado()
					+ ", codigo=" + safe(det.getCodigo())
					+ ", idPlan=" + det.getIdPlan()
					+ ", fechaDesde=" + formatearFechaDebug(det.getFechaDesde())
					+ ", fechaHasta=" + formatearFechaDebug(det.getFechaHasta())
					+ ", idPrestacion=" + (det.getPrestacion() != null ? det.getPrestacion().getId() : 0)
					+ ", prestacionDesc=" + (det.getPrestacion() != null ? safe(det.getPrestacion().getDescripcion()) : "")
					+ ", tipoValorizacion=" + safe(det.getTipoValorizacion())
					+ ", importe=" + det.getImporte()
					+ ", porcentaje=" + det.getPorcentaje()
					+ ", coseguro=" + det.getCoseguro()
					+ ", businessKeyExacta=" + buildBusinessKeyDebug(det));
		}
	}

	private String buildBusinessKeyDebug(ConvenioPrestacionalDetalle det) {
		if (det == null) {
			return "";
		}

		return normalizarCodigoDebug(det.getCodigo())
				+ "|" + formatearFechaDebugKey(det.getFechaDesde())
				+ "|" + det.getIdPlan();
	}

	private String normalizarCodigoDebug(String codigo) {
		return codigo != null ? codigo.trim().toLowerCase() : "";
	}

	private String formatearFechaDebug(Date fecha) {
		if (fecha == null) {
			return "null";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(fecha);
	}

	private String formatearFechaDebugKey(Date fecha) {
		if (fecha == null) {
			return "";
		}
		return new SimpleDateFormat("yyyyMMdd").format(fecha);
	}

	private void setModoEdicion(HttpSession session, RenderRequest renderRequest, boolean modoEdicion) {
		if (renderRequest != null) {
			renderRequest.setAttribute(ATTR_MODO_EDICION, Boolean.valueOf(modoEdicion));
		}
		if (session != null) {
			session.setAttribute(ATTR_MODO_EDICION, Boolean.valueOf(modoEdicion));
		}
	}

	private boolean getModoEdicion(HttpSession session) {
		return session != null && Boolean.TRUE.equals(session.getAttribute(ATTR_MODO_EDICION));
	}

	private void aplicarDefaultsDetalle(ConvenioPrestacionalDetalle det) {
		if (det == null) {
			return;
		}

		String tipo = det.getTipoValorizacion();

		if (StringUtils.checkEmpty(tipo)) {
			det.setTipoValorizacion(DEFAULT_TIPO_VALORIZACION);
		} else if (DEFAULT_TIPO_VALORIZACION.equalsIgnoreCase(tipo.trim())) {
			det.setTipoValorizacion(DEFAULT_TIPO_VALORIZACION);
		} else if (LEGACY_TIPO_VAL_PORCENTAJE.equalsIgnoreCase(tipo.trim())) {
			det.setTipoValorizacion(LEGACY_TIPO_VAL_PORCENTAJE);
		} else {
			det.setTipoValorizacion(DEFAULT_TIPO_VALORIZACION);
		}

		if (det.getImporte() != null) {
			det.setImporte(normalizarDecimal(det.getImporte()));
		}

		if (det.getPorcentaje() == null) {
			det.setPorcentaje(DEFAULT_PORCENTAJE);
		}
		det.setPorcentaje(normalizarDecimal(det.getPorcentaje()));

		if (det.getCoseguro() == null) {
			det.setCoseguro(DEFAULT_COSEGURO);
		}
		det.setCoseguro(normalizarDecimal(det.getCoseguro()));
	}

	private void resolverBackURL(RenderRequest renderRequest, HttpSession session, String cmd) {
		boolean esIngresoPantalla =
				Constants.ADD.equals(cmd)
						|| Constants.VIEW.equals(cmd)
						|| Constants.EDIT.equals(cmd);

		if (esIngresoPantalla) {
			String backURL = ParamUtil.getString(renderRequest, "backURL", null);

			if (StringUtils.checkNotEmpty(backURL)) {
				session.setAttribute(ATTR_VOLVER_CONVENIOS_URL, backURL);
			} else {
				session.removeAttribute(ATTR_VOLVER_CONVENIOS_URL);
			}
		}

		String backURLSession = (String) session.getAttribute(ATTR_VOLVER_CONVENIOS_URL);
		renderRequest.setAttribute(ATTR_VOLVER_CONVENIOS_URL, backURLSession);
	}

	private BigDecimal normalizarDecimal(BigDecimal value) {
		if (value == null) {
			return null;
		}
		return value.setScale(2, RoundingMode.HALF_UP);
	}

	private String buildBusinessKeyOperativa(ConvenioPrestacionalDetalle det) {
		if (det == null) {
			return "";
		}

		String identidad;

		if (det.getPrestacion() != null && det.getPrestacion().getId() > 0) {
			identidad = "prest:" + det.getPrestacion().getId();
		} else {
			identidad = "cod:" + normalizarClave(det.getCodigo());
		}

		return identidad + "|" + det.getIdPlan();
	}

	private void normalizarDetallesNuevosSinPersistenciaPrevia(ConvenioPrestacional convenio) {

		if (convenio == null || convenio.getConvenioPrestDetalle() == null || convenio.getConvenioPrestDetalle().isEmpty()) {
			return;
		}

		// Regla pedida: sólo para convenios nuevos, sin persistencia previa.
		if (convenio.getId() > 0) {
			return;
		}

		List<ConvenioPrestacionalDetalle> originales = convenio.getConvenioPrestDetalle();
		Map<String, ConvenioPrestacionalDetalle> ganadoresPorClave = new LinkedHashMap<String, ConvenioPrestacionalDetalle>();

		int nroFila = 0;

		for (ConvenioPrestacionalDetalle det : originales) {
			nroFila++;

			if (det == null) {
				continue;
			}

			if (det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				continue;
			}

			if (det.getFechaDesde() == null) {
				throw new IllegalArgumentException("DETALLE fila " + nroFila + ": fecha_desde obligatoria");
			}

			String keyOperativa = buildBusinessKeyOperativa(det);
			ConvenioPrestacionalDetalle actual = ganadoresPorClave.get(keyOperativa);

			if (actual == null) {
				ganadoresPorClave.put(keyOperativa, det);
				continue;
			}

			Date fechaActual = actual.getFechaDesde();
			Date fechaNueva = det.getFechaDesde();

			if (fechaNueva.equals(fechaActual)) {
				throw new IllegalArgumentException(
						"DETALLE fila " + nroFila
								+ ": existe un ítem duplicado con misma prestación/código, misma fecha desde y mismo plan. Clave: "
								+ keyOperativa + "|" + formatearFechaKey(fechaNueva));
			}

			// Regla de negocio: sobrevive la fecha_desde más nueva
			if (fechaNueva.after(fechaActual)) {
				_log.warn("[EDIT-CONV-PREST][NORMALIZE][REPLACE] Se reemplaza detalle anterior por uno más nuevo. keyOperativa="
						+ keyOperativa
						+ ", fechaAnterior=" + formatearFechaDebug(fechaActual)
						+ ", fechaNueva=" + formatearFechaDebug(fechaNueva));
				ganadoresPorClave.put(keyOperativa, det);
			} else {
				_log.warn("[EDIT-CONV-PREST][NORMALIZE][DROP] Se descarta detalle por existir otro más nuevo. keyOperativa="
						+ keyOperativa
						+ ", fechaDescartada=" + formatearFechaDebug(fechaNueva)
						+ ", fechaGanadora=" + formatearFechaDebug(fechaActual));
			}
		}

		convenio.setConvenioPrestDetalle(new ArrayList<ConvenioPrestacionalDetalle>(ganadoresPorClave.values()));

		_log.info("[EDIT-CONV-PREST][NORMALIZE][END] Normalización de convenio nuevo aplicada. originales="
				+ originales.size()
				+ ", normalizados=" + convenio.getConvenioPrestDetalle().size());
	}

	private Map<Integer, String> obtenerPlanesHabilitadosPrestador(int idPrestador) {

		if (idPrestador <= 0) {
			throw new IllegalArgumentException("Debe seleccionar un prestador antes de importar el XLS");
		}

		Map<Integer, String> planes = new LinkedHashMap<Integer, String>();

		List<PrestadorPlan> lista = PrestadorServiceUtil.getPlanesDelPrestador(idPrestador);

		if (lista != null) {
			for (PrestadorPlan pp : lista) {
				if (pp == null) {
					continue;
				}

				int idPlan = pp.getId_plan();

				if (idPlan <= 0 && pp.getPlan() != null) {
					idPlan = pp.getPlan().getId();
				}

				if (idPlan <= 0) {
					continue;
				}

				String descripcion = null;

				if (pp.getPlan() != null) {
					descripcion = pp.getPlan().getDescripcion();
				}

				if (StringUtils.checkEmpty(descripcion)) {
					descripcion = String.valueOf(idPlan);
				}

				planes.put(idPlan, descripcion);
			}
		}

		return planes;
	}

	private String resolverNombrePlanNoHabilitado(int idPlan,
												  String planDescXls) {

		String nombrePlan = null;

		if (StringUtils.checkNotEmpty(planDescXls)) {
			nombrePlan = planDescXls.trim();
		}

		if (StringUtils.checkEmpty(nombrePlan)) {
			nombrePlan = String.valueOf(idPlan);
		}

		return nombrePlan;
	}

	private static class PlanNoHabilitadoInfo {
		private final int idPlan;
		private final String nombrePlan;
		private final List<Integer> filas = new ArrayList<Integer>();

		public PlanNoHabilitadoInfo(int idPlan, String nombrePlan) {
			this.idPlan = idPlan;
			this.nombrePlan = nombrePlan;
		}

		public int getIdPlan() {
			return idPlan;
		}

		public String getNombrePlan() {
			return nombrePlan;
		}

		public List<Integer> getFilas() {
			return filas;
		}

		public void addFila(int fila) {
			filas.add(Integer.valueOf(fila));
		}
	}
}
