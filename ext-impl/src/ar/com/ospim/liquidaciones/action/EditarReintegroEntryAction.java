package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.PrestacionComprobanteExistenteException;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.liquidaciones.DuplicateReintegroIdException;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.FechaPrestacionMayorFechaBajaExcepcion;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroPrestacionEntryException;
import ar.com.ospim.liquidaciones.PrestacionYaHechaAAfiliadoExcepcion;
import ar.com.ospim.liquidaciones.TopeCantidadIndividualExedidoException;
import ar.com.ospim.liquidaciones.TopeCantidadTotalExedidoException;
import ar.com.ospim.liquidaciones.TopeImporteIndividualExedidoException;
import ar.com.ospim.liquidaciones.TopeImporteTotalExedidoException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdo;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoProtesis;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarReintegroEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class EditarReintegroEntryAction extends PortletAction {

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		String deletePrestaci = ParamUtil.getString(actionRequest,
				"deletePrestaci", null);
		int cambio_estado_numero = ParamUtil.getInteger(actionRequest,
				"cambio_estado_numero", 0);
		int cambio_estado_reintegro = ParamUtil.getInteger(actionRequest,
				"cambio_estado_reintegro", 0);
		int id_reintegro = 0;
		boolean errors = false;
		try {
			if (cmd != null && cmd.length() > 0) {
				if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
					id_reintegro = updateReintegroEntry(actionRequest, cmd);
					actionRequest.setAttribute(
							WebKeysLiquidaciones.ID_REINTEGRO_EN_EDICION,
							id_reintegro);
				} else if (cmd.equals(Constants.DELETE)
						&& deletePrestaci == null) {
					borraReintegroEntry(actionRequest);
					setForward(actionRequest, "portlet.liquidaciones.view");
				} else if (cmd.equals(Constants.DELETE)
						&& deletePrestaci != null) {
					id_reintegro = borraReintegroPrestacionEntry(actionRequest);
				}
			} else {
				if (cambio_estado_numero != 0) {
					cambiarEstadoReintegroEntry(cambio_estado_reintegro,
							cambio_estado_numero, actionRequest);
				}
				String tipo_reintegro = ParamUtil.getString(actionRequest,
						"tipo_rein", WebKeysLiquidaciones.REINTEGRO_PRE);
				actionRequest.setAttribute(
						WebKeysLiquidaciones.ID_REINTEGRO_EN_EDICION,
						cambio_estado_reintegro);
				actionRequest.setAttribute(
						WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION,
						tipo_reintegro);
			}
		} catch (FechaPrestacionMayorFechaBajaExcepcion e) {
			StringBuilder error = getFechasError();
			putError(actionRequest, error);
			errors = true;
		} catch (PrestacionYaHechaAAfiliadoExcepcion e) {
			//SessionErrors.add(actionRequest, e.getClass().getName());
			//setForward(actionRequest, "portlet.liquidaciones.error");
			StringBuilder error = getPrestacionHechaError();
			putError(actionRequest, error);
			errors = true;
			throw new PrestacionYaHechaAAfiliadoExcepcion(e);
		} catch (TopeCantidadIndividualExedidoException e) {
			StringBuilder error = getTopeCantIndivError(e);
			putError(actionRequest, error);
			errors = true;
		} catch (TopeImporteIndividualExedidoException e) {
			StringBuilder error = getTopeImporteIndivError(e);
			putError(actionRequest, error);
			errors = true;
		} catch (TopeCantidadTotalExedidoException e) {
			StringBuilder error = getTopeCantTotalError(e);
			putError(actionRequest, error);
			errors = true;
		} catch (TopeImporteTotalExedidoException e) {
			StringBuilder error = getTopeImporteTotalError(e);
			putError(actionRequest, error);
			errors = true;
		} catch (AfiliadoSinPlanException e) {
			StringBuilder error = new StringBuilder("Afiliado sin plan vigente");
			putError(actionRequest, error);
			errors = true;
		} catch (Exception e) {
			if (e instanceof NoSuchReintegroEntryException
					|| e instanceof DuplicateReintegroIdException
					|| e instanceof NoSuchReintegroPrestacionEntryException
					|| e instanceof DuplicateReintegroPrestacionIdException
					|| e instanceof PrestacionComprobanteExistenteException
					|| e instanceof NoSuchReintegroEntryException
					|| e instanceof PrestacionYaHechaAAfiliadoExcepcion ) {
				
				SessionErrors.add(actionRequest, e.getClass().getName());
				setForward(actionRequest, "portlet.liquidaciones.error");
			} else {
				throw e;
			}
		}
		if (errors) {
			ReintegroPrestacion rPrestacion = getReintegroPrestacionFromRequest(actionRequest);
			Reintegro reintegro = getReintegroFromRequest(actionRequest);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.REINTEGRO_EN_EDICION, reintegro);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.REINTEGRO_PRESTACION_EN_EDICION,
					rPrestacion);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION, reintegro
							.getTipo_reintegro());
			setForward(actionRequest,
					"portlet.liquidaciones.editar_reintegro_entry");
		}
		if (SessionErrors.isEmpty(actionRequest) && !errors) {
			PortalUtil
					.getHttpServletRequest(actionRequest)
					.getSession()
					.removeAttribute(
							WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_EN_EDICION);
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
	}

	private StringBuilder getTopeImporteTotalError(
			TopeImporteTotalExedidoException e) {
		StringBuilder error = new StringBuilder();
		error
				.append("El importe total por reintegros por período no puede ser excedido. Importe total actual:");
		error.append(e.getImporte());
		error.append(" - Cantidad Tope: ");
		error.append(e.getTopeImporte());
		return error;
	}

	private StringBuilder getTopeCantTotalError(
			TopeCantidadTotalExedidoException e) {
		StringBuilder error = new StringBuilder();
		error
				.append("La cantidad total de reintegros por período no puede ser excedida. Cantidad total actual :");
		error.append(e.getCantidad());
		error.append(" - Cantidad Tope: ");
		error.append(e.getTopeCant());
		return error;
	}

	private StringBuilder getTopeImporteIndivError(
			TopeImporteIndividualExedidoException e) {
		StringBuilder error = new StringBuilder();
		error
				.append("El importe total por reintegro no puede ser excedido. Importe total ingresado:");
		error.append(e.getImporte());
		error.append(" - Importe max.: ");
		error.append(e.getTopeIndivImporte());
		return error;
	}

	private StringBuilder getTopeCantIndivError(
			TopeCantidadIndividualExedidoException e) {
		StringBuilder error = new StringBuilder();
		error
				.append("La cantidad por reintegro no puede ser excedida. Cantidad ingresada:");
		error.append(e.getCantidad());
		error.append(" - Cantidad max.:");
		error.append(e.getTopeIndivCant());
		return error;
	}

	private StringBuilder getPrestacionHechaError() {
		StringBuilder error = new StringBuilder();
		error
				.append("La prestación ya fue realizada al afiliado y no puede hacerse dos veces");
		return error;
	}

	private StringBuilder getFechasError() {
		StringBuilder error = new StringBuilder();
		error
				.append("La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado");
		return error;
	}

	private void putError(ActionRequest actionRequest, StringBuilder error) {
		actionRequest.setAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT, error
				.toString());
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getPosicionesIva(renderRequest);	
		try {
			ReintegroActionUtil.getReintegroEntry(renderRequest);
		} catch (Exception e) {
			if (e instanceof NoSuchReintegroEntryException
					|| e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass().getName());
				return mapping.findForward("portlet.liquidaciones.error");
			} else {
				throw e;
			}
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.editar_reintegro_entry"));
	}

	protected void borraReintegroEntry(ActionRequest actionRequest)
			throws Exception {
		int id_reintegro = ParamUtil.getInteger(actionRequest, "numero", 0);
		User user = PortalUtil.getUser(actionRequest);
		ReintegroServiceUtil.borraReintegroEntry(id_reintegro, user
				.getScreenName());
	}

	protected int borraReintegroPrestacionEntry(ActionRequest actionRequest)
			throws Exception {
		int id_reintegro = ParamUtil.getInteger(actionRequest, "id_reintegro",
				0);
		int id_prestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat(
				DateUtils.LONG_MILI_SEC);
		String alta_fecha = ParamUtil
				.getString(actionRequest, "alta_fecha", "");
		Date alta;
		try {
			alta = formatoDeFecha.parse(alta_fecha);
		} catch (Exception e) {
			alta = null;
		}

		
		int id_Reclamo  = ParamUtil.getInteger(actionRequest, "borrar_id_reclamo_prestacion", 0);
		int id_prestacion_reclamo= ParamUtil.getInteger(actionRequest, "borrar_id_prestacion_reclamo", 0);
		
		User user = PortalUtil.getUser(actionRequest);
		
		if (id_Reclamo  !=0 && id_prestacion_reclamo !=0 ) {
			ReintegroServiceUtil.borraReintegroReclamoPrestacion(id_Reclamo  , id_prestacion_reclamo, user.getScreenName() );
		}
		
		int id_plan = ParamUtil.getInteger(actionRequest, "id_plan", 0);
		String tipo_compro = ParamUtil.getString(actionRequest,
				"comprobante_tipo", null);
		String nro_compro = ParamUtil.getString(actionRequest,
				"comprobante_nro", null);
		String tipo_reintegro = ParamUtil.getString(actionRequest, "tipo_r",
				WebKeysLiquidaciones.REINTEGRO_PRE);
		

		ReintegroServiceUtil.borraReintegroPrestacionEntry(id_reintegro,
				id_prestacion, alta, id_plan, tipo_compro, nro_compro, user
						.getScreenName(), tipo_reintegro);
		actionRequest.setAttribute(
				WebKeysLiquidaciones.ID_REINTEGRO_EN_EDICION, id_reintegro);
		actionRequest.setAttribute(
				WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION, tipo_reintegro);
		return id_reintegro;
	}

	protected int updateReintegroEntry(ActionRequest actionRequest,
			String command) throws Exception {

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		String fechaAnio = ParamUtil.getString(actionRequest, "fechaAnio");
		boolean esExcepcion = ParamUtil.getBoolean(actionRequest, "esExcepcion",false);
		
		
		//  Pasa el dato si tiene reclamo prestacional el afiliado selecccionado par el reintegro
		String conReclamoPrestacional = ParamUtil.getString(actionRequest, "con_reclamo_prestacional");
		actionRequest.setAttribute("con_reclamo_prestacional", conReclamoPrestacional);
		
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(actionRequest,
				"periodoMesAnio");
		Date periodo = null;

		try {
			periodo = formatoDePeriodos.parse("0"
					+ String.valueOf((Integer.parseInt(periodoMesAnio
							.substring(0, 1)) + 1)) + "/"
					+ periodoMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodo = null;
		}
		if (periodo == null) {
			try {
				periodo = formatoDePeriodos.parse(Integer
						.parseInt(periodoMesAnio.substring(0, 2))
						+ 1 + "/" + periodoMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodo = null;
			}
		}
		if (periodo == null) {
			String periodoHidden = ParamUtil.getString(actionRequest,
					"periodoHidden");
			try {
				periodo = formatoDePeriodos.parse(periodoHidden);
			} catch (Exception e) {
				periodo = null;
			}
		}

		String entidad = ParamUtil.getString(actionRequest, "entidad", null);

		String cuilTitular = ParamUtil.getString(actionRequest, "cuil", null);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		String bajaFecha = ParamUtil.getString(actionRequest, "baja_fecha",null);

		int seccional = ParamUtil.getInteger(actionRequest, "id_seccional_r", 0);
		int numero = ParamUtil.getInteger(actionRequest, "id_reintegro", 0);

		Date fechaBaja;
		try {
			fechaBaja = formatoDeFecha.parse(bajaFecha);
		} catch (Exception e) {
			fechaBaja = null;
		}

		String prestacionFechaDia = ParamUtil.getString(actionRequest,
				"prestacionFechaDia");
		String prestacionFechaMes = ParamUtil.getString(actionRequest,
				"prestacionFechaMes");
		String prestacionFechaAnio = ParamUtil.getString(actionRequest,
				"prestacionFechaAnio");
		Date prestacionFecha;
		try {
			prestacionFecha = formatoDeFecha.parse(prestacionFechaDia + "/"
					+ (Integer.parseInt(prestacionFechaMes) + 1) + "/"
					+ prestacionFechaAnio);
		} catch (Exception e) {
			prestacionFecha = null;
		}

		int idPrestacion = ParamUtil.getInteger(actionRequest,"id_prestacion", 0);
		String codigo = ParamUtil.getString(actionRequest, "codigo", "");

		String cuitPrestador = ParamUtil.getString(actionRequest,"cuit_prestador", "");
		String nombrePrestador = ParamUtil.getString(actionRequest,"nombre_prestador", "");

		String cuitEntidad = ParamUtil.getString(actionRequest, "cuit_entidad","");
		String sucuEntidad = ParamUtil.getString(actionRequest,"sucursal_entidad", "");

		String cantidad = ParamUtil.getString(actionRequest, "cantidad", "0")
				.equals("") ? "0" : ParamUtil.getString(actionRequest,
				"cantidad", "0");
		
		String importe = ParamUtil.getString(actionRequest, "importe", "0")
				.equals("") ? "0" : ParamUtil.getString(actionRequest,"importe", "0");
		
		String comproaDebitarTipo = ParamUtil.getString(actionRequest,"comprobante_tipo", null);
		String comproaDebitarSucursal = ParamUtil.getString(actionRequest, "comprobante_suc", null);
		String comproaDebitarLetra = ParamUtil.getString(actionRequest, "comprobante_letra", null);
		String comproaDebitarNumero = ParamUtil.getString(actionRequest,"comprobante_nro", null);
		String tercerizado = ParamUtil.getString(actionRequest,
				"descontar_capitas", null);
		double topeCant = ParamUtil
				.getDouble(actionRequest, "tope_cantidad", 0);
		double topeImporte = ParamUtil.getDouble(actionRequest, "tope_importe",
				0);
		double topeImportePlan = ParamUtil.getDouble(actionRequest, "tope_importe_plan",
				0);
		double topeIndivCant = ParamUtil.getDouble(actionRequest,
				"tope_individ_cantidad", 0);
		double topeIndivImporte = ParamUtil.getDouble(actionRequest,
				"tope_individ_importe", 0);
		String obs = ParamUtil.getString(actionRequest, "observaciones", "");
		String tipoReintegro = ParamUtil.getString(actionRequest,
				"tipo_reintegro", WebKeysLiquidaciones.REINTEGRO_PRE);
		String editPrestaci = ParamUtil.getString(actionRequest,
				"editPrestaci", "");

		int pieza = ParamUtil.getInteger(actionRequest, "pieza", 0);
		String cara = ParamUtil.getString(actionRequest, "cara", null);
		int idPrestadorExterno = ParamUtil.getInteger(actionRequest,
				"id_prestador", 0);

		int estado = WebKeysLiquidaciones.REINTEGRO_ESTADO_CARGADO;
		int nroCuotas = ParamUtil.getInteger(actionRequest, "nro_cuotas", 0);
		String presupuesto = ParamUtil.getString(actionRequest, "presupuesto",
				"0").equals("") ? "0" : ParamUtil.getString(actionRequest,
				"presupuesto", "0");

		//////////////
		// datos del reclamo y de a prestacion de existir 
		int idReclamo = ParamUtil.getInteger(actionRequest, "id_reclamo_prestacional", 0);
		int idPrestacionReclamo= ParamUtil.getInteger(actionRequest, "id_prestacion_reclamo_prestacional", 0);
		
		String prestacionComproFechaDia = ParamUtil.getString(actionRequest,
				"prestacionComproFechaDia");
		String prestacionComproFechaMes = ParamUtil.getString(actionRequest,
				"prestacionComproFechaMes");
		String prestacionComproFechaAnio = ParamUtil.getString(actionRequest,
				"prestacionComproFechaAnio");
		Date comproFecha;
		try {
			comproFecha = formatoDeFecha.parse(prestacionComproFechaDia + "/"
					+ (Integer.parseInt(prestacionComproFechaMes) + 1) + "/"
					+ prestacionComproFechaAnio);
		} catch (Exception e) {
			comproFecha = null;
		}
		String comproImporte = ParamUtil.getString(actionRequest, "importeCompro", "0")
				.equals("") ? "0" : ParamUtil.getString(actionRequest,
				"importeCompro", "0");
		int motivoAltaDiscapacidad = ParamUtil.getInteger(actionRequest, "motivoAltaDiscapacidad");
		
		String cargoOspim =  ParamUtil.getString(actionRequest, "cargo_ospim", "0");
		String cargoPrestadora = ParamUtil.getString(actionRequest, "cargo_prestadora", "0");
		String cargoImesa = ParamUtil.getString(actionRequest, "cargo_imesa", "0");
		
		String cbu = ParamUtil.getString(actionRequest, "cbu", null);
		String cuilCuenta = ParamUtil.getString(actionRequest, "cuil_cuenta", null);
		String emailCuenta = ParamUtil.getString(actionRequest, "email_cuenta", null);
		String apellidoCuenta = ParamUtil.getString(actionRequest, "apellido_cuenta", null);
		String nombreCuenta = ParamUtil.getString(actionRequest, "nombre_cuenta", null);
		
		String idTecerizadora = ParamUtil.getString(actionRequest, "id_tercerizadora", "");

		if (StringUtils.checkEmpty(idTecerizadora)
		        || "null".equalsIgnoreCase(idTecerizadora)
		        || "undefined".equalsIgnoreCase(idTecerizadora)) {
		    idTecerizadora = null;
		}
		

		User user = PortalUtil.getUser(actionRequest);
		if (command.equals(Constants.ADD)) {
			if (tipoReintegro.equals(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				estado = ParamUtil.getInteger(actionRequest, "estado",
						WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE);
			}
			if (tipoReintegro.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				estado = ParamUtil.getInteger(actionRequest, "estado",
						WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE);
				nroCuotas = ParamUtil.getInteger(actionRequest, "nro_cuotas",
						0);
			}
			numero = ReintegroServiceUtil.cargaReintegroEntry(fecha, periodo,
					entidad, cuilTitular, inte, seccional, prestacionFecha,
					idPrestacion, codigo, cuitPrestador, nombrePrestador,
					tipoReintegro, estado, user.getScreenName(), cantidad,
					importe, comproaDebitarTipo, comproaDebitarNumero,
					tercerizado, topeCant, topeImporte, topeIndivCant,
					topeIndivImporte, fechaBaja, obs, pieza, cara,
					idPrestadorExterno, presupuesto, nroCuotas, cuitEntidad,
					sucuEntidad, comproFecha, comproImporte, motivoAltaDiscapacidad,esExcepcion,idReclamo,
					idPrestacionReclamo, cargoOspim,cargoPrestadora,comproaDebitarSucursal,comproaDebitarLetra,
					cbu, cuilCuenta, emailCuenta,apellidoCuenta, nombreCuenta,cargoImesa, idTecerizadora );

		} else {
			if (editPrestaci.length() == 0) {
				ReintegroServiceUtil.actualizaReintegroEntry(numero, fecha, cuilTitular, inte, cuitPrestador, nombrePrestador, 
						idPrestacion, codigo, prestacionFecha, cantidad, importe, 
						comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, tercerizado, user.getScreenName(), periodo, topeCant,
						topeImporte, topeIndivCant, topeIndivImporte, fechaBaja, seccional, obs, pieza, cara, tipoReintegro, 
						idPrestadorExterno, presupuesto, nroCuotas, cuitEntidad, sucuEntidad, comproFecha, comproImporte, motivoAltaDiscapacidad, 
						esExcepcion, idReclamo, idPrestacionReclamo, cargoOspim, cargoPrestadora,cargoImesa, idTecerizadora);
			} else {
				SimpleDateFormat formatoDeF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
				String altaFecha = ParamUtil.getString(actionRequest,"prestacion_alta_fecha", "");
				int idPrestacionAnterior = ParamUtil.getInteger(actionRequest, "id_prestacion_anterior", 0);
				String codigoAnterior = ParamUtil.getString(actionRequest,"codigo_anterior", "");
				Date alta;
				try {
					alta = formatoDeF.parse(altaFecha);
				} catch (Exception e) {
					alta = null;
				}
				ReintegroServiceUtil.actualizaReintegroPrestacionEntry(numero,fecha, cuilTitular, inte, cuitPrestador, nombrePrestador, 
						idPrestacion, codigo, prestacionFecha, cantidad, importe, 
						comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, tercerizado, user.getScreenName(), 
						periodo, topeCant, topeImporte, topeIndivCant, topeIndivImporte, fechaBaja, seccional, obs, alta, idPrestacionAnterior, 
						codigoAnterior, tipoReintegro, pieza, cara, idPrestadorExterno, presupuesto, nroCuotas, cuitEntidad, sucuEntidad, 
						comproFecha, comproImporte, motivoAltaDiscapacidad, esExcepcion, cargoOspim, cargoPrestadora,cargoImesa, idTecerizadora);
			}
		}
		return numero;
	}

	private Reintegro getReintegroFromRequest(ActionRequest actionRequest) {
		Reintegro reintegro = new Reintegro();
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		String fechaAnio = ParamUtil.getString(actionRequest, "fechaAnio");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}
		reintegro.setFecha(fecha);
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(actionRequest,
				"periodoMesAnio");
		Date periodo = null;
		try {
			periodo = formatoDePeriodos.parse(Integer.parseInt(periodoMesAnio
					.substring(0, 1))
					+ 1 + "/" + periodoMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodo = null;
		}
		reintegro.setPeriodo(periodo);
		String entidad = ParamUtil.getString(actionRequest, "entidad", null);
		reintegro.setEntidad(entidad);
		String cuil_titular = ParamUtil.getString(actionRequest, "cuil", null);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		reintegro.setAfiliado(new Afiliado(cuil_titular, inte));
		int numero = ParamUtil.getInteger(actionRequest, "id_reintegro", 0);
		reintegro.setId_reintegro(numero);
		return reintegro;
	}

	private ReintegroPrestacion getReintegroPrestacionFromRequest(
			ActionRequest actionRequest) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String tipo_reintegro = ParamUtil.getString(actionRequest,
				"tipo_reintegro", null);
		String prestacionFechaDia = ParamUtil.getString(actionRequest,
				"prestacionFechaDia");
		String prestacionFechaMes = ParamUtil.getString(actionRequest,
				"prestacionFechaMes");
		String prestacionFechaAnio = ParamUtil.getString(actionRequest,
				"prestacionFechaAnio");
		Date prestacionFecha;
		try {
			prestacionFecha = formatoDeFecha.parse(prestacionFechaDia + "/"
					+ (Integer.parseInt(prestacionFechaMes) + 1) + "/"
					+ prestacionFechaAnio);
		} catch (Exception e) {
			prestacionFecha = null;
		}

		ReintegroPrestacion reintegroPrestacion = null;
		if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
			reintegroPrestacion = new ReintegroPrestacionNormal();
		} else if (tipo_reintegro
				.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			reintegroPrestacion = new ReintegroPrestacionOdo();
		} else if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)){
			reintegroPrestacion = new ReintegroPrestacionOdoProtesis();
		}
		BigDecimal presupuesto =new BigDecimal(ParamUtil.getDouble(actionRequest,"presupuesto"));
		reintegroPrestacion.setHonorarios(presupuesto);
		
		reintegroPrestacion.setFecha_prestacion(prestacionFecha);
		int id_prestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);
		reintegroPrestacion.setId_prestacion(id_prestacion);
		String cuitPrestador = ParamUtil.getString(actionRequest,
				"cuit_prestador");
		String nombrePrestador = ParamUtil.getString(actionRequest,
				"nombre_prestador");

		String cuitEntidad = ParamUtil.getString(actionRequest, "cuit_entidad");
		String sucuEntidad = ParamUtil.getString(actionRequest, "sucu_entidad");

		// int id_prestador = ParamUtil.getInteger(actionRequest,
		// "id_prestador",
		// 0);
		// Prestador prestador = new Prestador(cuitPrestador, id_prestador,
		// nombrePrestador);
		reintegroPrestacion.setCuit(cuitPrestador);
		reintegroPrestacion.setDescripcion(nombrePrestador);
		reintegroPrestacion.setCuit_entidad(cuitEntidad);
		reintegroPrestacion.setSucursal_entidad(sucuEntidad);

		String importe = ParamUtil.getString(actionRequest, "importe", null);
		reintegroPrestacion.setImporte(new BigDecimal(importe));
		String compro_a_debitar_tipo = ParamUtil.getString(actionRequest,
				"comprobante_tipo", null);
		reintegroPrestacion.setCompro_a_debitar_tipo(compro_a_debitar_tipo);
		String compro_a_debitar_numero = ParamUtil.getString(actionRequest,
				"comprobante_nro", null);
		reintegroPrestacion.setCompro_a_debitar_numero(compro_a_debitar_numero);
		String tercerizado = ParamUtil.getString(actionRequest,
				"descontar_capitas", null);
		reintegroPrestacion.setTercerizado(tercerizado);
		int pieza = ParamUtil.getInteger(actionRequest, "pieza", 0);
		String cara = ParamUtil.getString(actionRequest, "cara", null);

		if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
			String cantidad = ParamUtil.getString(actionRequest, "cantidad",
					null);
			((ReintegroPrestacionNormal) reintegroPrestacion)
					.setCantidad(new BigDecimal(cantidad));
		} else if (tipo_reintegro
				.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			((ReintegroPrestacionOdoProtesis) reintegroPrestacion)
					.setPieza(pieza);
			((ReintegroPrestacionOdoProtesis) reintegroPrestacion)
					.setCara(cara);
		}
		return reintegroPrestacion;
	}

	protected int cambiarEstadoReintegroEntry(int id_reintegro,
			int estadoNuevo, ActionRequest actionRequest) throws Exception {
		User user = PortalUtil.getUser(actionRequest);
		String tipo_reintegro = ParamUtil.getString(actionRequest, "tipo_rein",
				WebKeysLiquidaciones.REINTEGRO_PRE);
		ReintegroServiceUtil.cambiarEstadoReintegroEntry(id_reintegro,
				estadoNuevo, user.getScreenName(), tipo_reintegro, null);
		return id_reintegro;
	}

}