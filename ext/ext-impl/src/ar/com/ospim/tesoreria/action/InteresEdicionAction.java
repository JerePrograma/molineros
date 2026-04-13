package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.FormOpcionSSSInvalidoException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EntidadPadronUnificado;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;

import ar.com.ospim.tesoreria.WebKeysInteres;
import ar.com.ospim.tesoreria.beans.interes.Interes;
import ar.com.ospim.tesoreria.service.InteresServiceUtil;

import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class InteresEdicionAction extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);

		Interes _interes = null;

		String fechaInicioInteres = "";
		String fechaFinInteres = "";
		boolean altaAction = false;

		String msg = "";

		int entidad = WebKeysGlobal.OSPIM;
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		if (!StringUtils.checkEmpty(cmd)) {
			fechaInicioInteres = ParamUtil.get(renderRequest, "fecha_inicio", "");
			fechaFinInteres = ParamUtil.get(renderRequest, "fecha_fin", "");

			if ("NEW".equals(cmd)) { // lo voy a usar como -NEW, para crear
										// nuevas entradas en blanco

				_interes = new Interes();

				session.setAttribute(WebKeysInteres.INTERES_EN_EDICION, _interes);
				session.setAttribute("accion", "new");
				
				_log.debug("Usuario: " + user.getScreenName() + " cmd: " + cmd);

				return mapping.findForward(getForward(renderRequest, "portlet.tesoreria.intereses.editar_interes"));
			}

			if (cmd.equals(Constants.EDIT)) {
				_interes = InteresServiceUtil.get(fechaInicioInteres, fechaFinInteres, 0, entidad);
				session.setAttribute(WebKeysInteres.INTERES_EN_EDICION, _interes);
				String accion = ParamUtil.getString(renderRequest, "accion", "edit");
				if ("view".equalsIgnoreCase(accion)) {
					session.setAttribute("accion", "view");
				} else {
					session.setAttribute(WebKeysInteres.ACCION, WebKeysInteres.ACCION_EDIT);
				}

				_log.debug("Usuario: " + user.getScreenName() + " cmd: " + cmd + " fechaInicio: "
						+ _interes.getFechaInicio() + " fechaFin: " + _interes.getFechaFin() + " interesDia: "
						+ _interes.getInteresDia().toString());

				return mapping.findForward(getForward(renderRequest, "portlet.tesoreria.intereses.editar_interes"));
			}
			
			if (cmd.equals(Constants.PREVIEW)) {
				_interes = InteresServiceUtil.get(fechaInicioInteres, fechaFinInteres, 0, entidad);
				session.setAttribute(WebKeysInteres.INTERES_EN_EDICION, _interes);
				String accion = ParamUtil.getString(renderRequest, "accion", "edit");
				if ("view".equalsIgnoreCase(accion)) {
					session.setAttribute("accion", "view");
				} else {
					session.setAttribute(WebKeysInteres.ACCION, WebKeysInteres.ACCION_DELETE);
				}

				_log.debug("Usuario: " + user.getScreenName() + " cmd: " + cmd + " fechaInicio: "
						+ _interes.getFechaInicio() + " fechaFin: " + _interes.getFechaFin() + " interesDia: "
						+ _interes.getInteresDia().toString());

				return mapping.findForward(getForward(renderRequest, "portlet.tesoreria.intereses.eliminar_interes"));
			}


			_interes = (Interes) session.getAttribute(WebKeysInteres.INTERES_EN_EDICION);			
			altaAction = ((fechaInicioInteres == "") && (fechaFinInteres == "")) ? true : false;			
			actualizaInteres(_interes, renderRequest);

			_log.debug(altaAction ? "Alta de Interes" : "Update de Interes");			
			
			if (cmd.equals(Constants.UPDATE)) {
				if (altaAction) {
					// Insert Interes
					if (!insertInteres(_interes, entidad)) {
						// Error
						SessionErrors.add(renderRequest, "errorValida"); 
						renderRequest.setAttribute("msgInsertError", msg);
						_log.debug("Error insertando Interes: " + _interes.getFechaFin() + ' ' + _interes.getFechaFin()
								+ ' ' + _interes.getInteresDia().toString());

					} else {

						// Insert OK
						msg = LanguageUtil.get(defaultLocale, "insert-interes");
						SessionMessages.add(renderRequest, "insertCabOk");
						renderRequest.setAttribute("msgCabOk", msg);
						_log.debug("Usuario: " + user.getScreenName() + " " + cmd + " Interes: "
								+ _interes.getFechaFin() + ' ' + _interes.getFechaFin() + ' '
								+ _interes.getInteresDia().toString());

					}

				} else {
					// Insert Interes
					if (!updateInteres(_interes, fechaInicioInteres, fechaFinInteres, entidad)) {
						// Error
						SessionErrors.add(renderRequest, "errorValida"); 
						renderRequest.setAttribute("msgInsertError", msg);
						_log.debug("Error actualizado Interes: " 
								+ _interes.getFechaFin() + " " + _interes.getFechaFin()
								+ " " + _interes.getInteresDia().toString() 
								+ " para " + fechaInicioInteres + " - " + fechaFinInteres);

					} else {

						// Insert OK
						msg = LanguageUtil.get(defaultLocale, "insert-interes");
						SessionMessages.add(renderRequest, "insertCabOk");
						renderRequest.setAttribute("msgCabOk", msg);
						_log.debug("Usuario: " + user.getScreenName() + " " + cmd + " Interes: "
								+ _interes.getFechaFin() + " " + _interes.getFechaFin()
								+ " " + _interes.getInteresDia().toString() 
								+ " para " + fechaInicioInteres + " - " + fechaFinInteres);

					} 					
				}
			} else if (cmd.equals(Constants.DELETE)) {
				// Insert Interes
				if (!deleteInteres(_interes, entidad)) {
					// Error
					SessionErrors.add(renderRequest, "errorValida"); 
					renderRequest.setAttribute("msgInsertError", msg);
					_log.debug("Error insertando Interes: " + _interes.getFechaFin() + ' ' + _interes.getFechaFin()
							+ ' ' + _interes.getInteresDia().toString());

				} else {

					// Insert OK
					msg = LanguageUtil.get(defaultLocale, "delete-interes");
					SessionMessages.add(renderRequest, "insertCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
					_log.debug("Usuario: " + user.getScreenName() + " " + cmd + " Interes: "
							+ _interes.getFechaFin() + ' ' + _interes.getFechaFin() + ' '
							+ _interes.getInteresDia().toString());

				}				
			}

		} ///////////

		session.setAttribute(WebKeysInteres.INTERES_EN_EDICION, _interes);

		return mapping.findForward("portlet.tesoreria.intereses.interes_adm");

	}

	private boolean insertInteres(Interes interes, int entidad) throws Exception {
		boolean ret = true;
		if (!InteresServiceUtil.add(interes, entidad)) {
			ret = false;
		}
		return ret;
	}

	private boolean updateInteres(Interes interes, 
			String origFechaDesde, String origFechaHasta, 
			int entidad) throws Exception {
		boolean ret = true;
		if (!InteresServiceUtil.update(interes, origFechaDesde, origFechaHasta, entidad)) {
			ret = false;
		}
		return ret;
	}

	private boolean deleteInteres(Interes interes, int entidad) throws Exception {
		boolean ret = true;
		if (!InteresServiceUtil.delete(interes, entidad)) {
			ret = false;
		}
		return ret;
	}

	private void actualizaInteres(Interes interes, RenderRequest renderRequest) {
		String fechaInicio = ParamUtil.getString(renderRequest, "fechaInicioInteres", null);
		String fechaFin = ParamUtil.getString(renderRequest, "fechaFinInteres", null);
		Double interesDia = ParamUtil.getDouble(renderRequest, "importeInteresDia", 0);		

		interes.setFechaInicio(fechaInicio);
		interes.setFechaFin(fechaFin);
		interes.setInteresDia(interesDia);
	}
}
