package ar.com.uoma.cuentacorrienteempresa.action;

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

import ar.com.uoma.WebKeysUOMA;

import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.CentroCosto;
import ar.com.uoma.beans.SaldoInicial;
import ar.com.uoma.cuentacorrienteempresa.services.SaldoInicialServiceUtil;

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

public class SaldoInicialEdicionAction extends PortletAction {

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

		SaldoInicial _saldo = null;

		boolean altaAction = false;

		String id = "";
		String msg = "";
		String cuit_empresa = "";
		String sucursal_empresa = "";
		
		String saldo_periodo = "";
		String saldo_monto = "";
		String saldo_tipocta = "";

		int entidad = WebKeysGlobal.OSPIM;
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		if (!StringUtils.checkEmpty(cmd)) {
			id = ParamUtil.get(renderRequest, "campo_id", "");			
			cuit_empresa = ParamUtil.get(renderRequest, "cuit_entidad", "");
			sucursal_empresa = ParamUtil.get(renderRequest, "sucursal_entidad", "");
			saldo_periodo = ParamUtil.get(renderRequest, "periodo", "");
			saldo_monto = ParamUtil.get(renderRequest, "monto", "");
			saldo_tipocta = ParamUtil.get(renderRequest, "tipocuenta", "");
			
			if ("NEW".equals(cmd)) { // lo voy a usar como -NEW, para crear
										// nuevas entradas en blanco

				_saldo = new SaldoInicial();
				_saldo.setCuit(cuit_empresa);
				_saldo.setSucursal(sucursal_empresa);
				session.setAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION, _saldo);
				session.setAttribute("accion", "new");
				
				_log.debug("Usuario: " + user.getScreenName() + " cmd: " + cmd);

				return mapping.findForward(getForward(renderRequest, "portlet.uoma.cuentacorriente.editar_saldoinicial"));
				
			}

			if (cmd.equals(Constants.EDIT)) {
				_saldo = SaldoInicialServiceUtil.get(Integer.valueOf(id), cuit_empresa, sucursal_empresa);
				session.setAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION, _saldo);
				String accion = ParamUtil.getString(renderRequest, "accion", "edit");
				if ("view".equalsIgnoreCase(accion)) {
					session.setAttribute("accion", "view");
				} else {
					session.setAttribute(WebKeysUOMA.ACCION, WebKeysUOMA.ACCION_EDIT);
				}

				return mapping.findForward(getForward(renderRequest, "portlet.uoma.cuentacorriente.editar_saldoinicial"));
			}
			
			if (cmd.equals(Constants.PREVIEW)) {
				_saldo = SaldoInicialServiceUtil.get(Integer.valueOf(id), cuit_empresa, sucursal_empresa);
				session.setAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION, _saldo);
				String accion = ParamUtil.getString(renderRequest, "accion", "edit");
				if ("view".equalsIgnoreCase(accion)) {
					session.setAttribute("accion", "view");
				} else {
					session.setAttribute(WebKeysUOMA.ACCION, WebKeysUOMA.ACCION_DELETE);
				}

				return mapping.findForward(getForward(renderRequest, "portlet.uoma.cuentacorriente.eliminar_saldoinicial"));
			}


			_saldo = (SaldoInicial) session.getAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION);
			
			String _act = ParamUtil.get(renderRequest, "view", "");
			altaAction = false; 
			if (_act.equals(WebKeysUOMA.ACCION_NEW)) 
				altaAction = true;
			
			actualizaSaldoInicial(_saldo, renderRequest);

			_log.debug(altaAction ? "Alta de Saldo Inicial" : "Update de Saldo Inicial");			
			
			if (cmd.equals(Constants.UPDATE)) {
				if (altaAction) {
					// Insert Interes
					if (!insertSaldoInicial(_saldo)) {
						// Error
						SessionErrors.add(renderRequest, "errorValida"); 
						renderRequest.setAttribute("msgInsertError", msg);
					} else {

						// Insert OK
						msg = LanguageUtil.get(defaultLocale, "insert-saldoinicial");
						SessionMessages.add(renderRequest, "insertCabOk");
						renderRequest.setAttribute("msgCabOk", msg);
					}

				} else {
					// Insert Interes
					if (!updateSaldoInicial(_saldo)) {
						// Error
						SessionErrors.add(renderRequest, "errorValida"); 
						renderRequest.setAttribute("msgInsertError", msg);
					} else {

						// Insert OK
						msg = LanguageUtil.get(defaultLocale, "update-saldoinicial");
						SessionMessages.add(renderRequest, "insertCabOk");
						renderRequest.setAttribute("msgCabOk", msg);
					} 
				}
			} else if (cmd.equals(Constants.DELETE)) {
				// Insert Interes
				if (!deleteSaldoInicial(Integer.valueOf(id))) {
					// Error
					SessionErrors.add(renderRequest, "errorValida"); 
					renderRequest.setAttribute("msgInsertError", msg);
				} else {

					// Insert OK
					msg = LanguageUtil.get(defaultLocale, "delete-saldoinicial");
					SessionMessages.add(renderRequest, "insertCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
				}				
			}

		} ///////////

		session.setAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION, _saldo);

		return mapping.findForward("portlet.uoma.cuentacorriente.saldoinicial_adm");

	}

	private boolean insertSaldoInicial(SaldoInicial saldo) throws Exception {
		boolean ret = true;
		if (!SaldoInicialServiceUtil.add(saldo)) {
			ret = false;
		}
		return ret;
	}

	private boolean updateSaldoInicial(SaldoInicial saldo) throws Exception {
		boolean ret = true;
		if (!SaldoInicialServiceUtil.update(saldo)) {
			ret = false;
		}
		return ret;
	}

	private boolean deleteSaldoInicial(int id) throws Exception {
		boolean ret = true;
		if (!SaldoInicialServiceUtil.delete(id)) {
			ret = false;
		}
		return ret;
	}

	private void actualizaSaldoInicial(SaldoInicial saldo, RenderRequest renderRequest) {
		String _cuit = ParamUtil.getString(renderRequest, "campo_cuit", null);
		String _suc = ParamUtil.getString(renderRequest, "campo_suc", null);
		Double _monto = ParamUtil.getDouble(renderRequest, "campo_monto", 0);		
		String _per = ParamUtil.getString(renderRequest, "campo_periodo", null);		
		String _cta = ParamUtil.getString(renderRequest, "campo_tipocta", null);
		
		saldo.setCuit(_cuit);
		saldo.setSucursal(_suc);
		saldo.setMonto(_monto);
		saldo.setPeriodo_STR(_per);
		saldo.setTipoBoleta(Integer.valueOf(_cta));
	}
}
