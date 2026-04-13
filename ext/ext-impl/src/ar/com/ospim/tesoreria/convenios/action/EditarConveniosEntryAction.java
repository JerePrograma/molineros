package ar.com.ospim.tesoreria.convenios.action;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.ExisteReciboConvenioException;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.ConvenioSinActasRelacionadasException;
import ar.com.ospim.tesoreria.ConvenioSinPagosException;
import ar.com.ospim.tesoreria.DuplicateConvenioIdException;
import ar.com.ospim.tesoreria.FaltanCuotasConvenioException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioEstadoSeguimiento;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarConveniosEntryAction extends PortletAction {
	private static final String CHEQUE_DUPLICADO = "CHEQUE_DUPLICADO";

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Convenio convenio = updateConvenioEntry(actionRequest, cmd);
				
				actionRequest.setAttribute("convenio_id",
						String.valueOf(convenio.getId()));
				actionRequest.setAttribute(Constants.CMD,Constants.UPDATE);
			}
		} catch (DuplicateConvenioIdException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (ConvenioSinPagosException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (DuplicateNumeroChequeException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute(CHEQUE_DUPLICADO, e.getCheque());
		} catch (FaltanCuotasConvenioException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (ConvenioSinActasRelacionadasException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (ExisteReciboConvenioException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());				
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
	}

	private Convenio updateConvenioEntry(ActionRequest actionRequest, String cmd)
			throws DuplicateConvenioIdException, ConvenioSinPagosException,
			DuplicateNumeroChequeException, PortalException, SystemException,
			SQLException, ParseException, FaltanCuotasConvenioException,
			ConvenioSinActasRelacionadasException, NumberFormatException,
			FechaMenorACierreContableException, ExisteReciboConvenioException {
		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		Convenio convenio = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		
		if (convenio == null) {
			convenio = new Convenio();
		}
		if(cmd.equals(Constants.UPDATE)){
			convenio.setId(ParamUtil.getInteger(actionRequest, "convenio_id"));
		}
		getConvenioFromRequest(PortalUtil.getHttpServletRequest(actionRequest),
				convenio);

		if (convenio != null) {
			PortalUtil
					.getHttpServletRequest(actionRequest)
					.getSession()
					.setAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION,
							convenio);
		}

		// va a tener por lo menos un ConvenioPago de tipo CUO
		// pero todavia necesita algun ConvenioPago de tipo PGO
		if (convenio.getPagos() == null || convenio.getPagos().size() <= 1) {
			throw new ConvenioSinPagosException();
		}

		if (convenio.getActasRelacionadas() == null
				|| convenio.getActasRelacionadas().size() == 0) {
			throw new ConvenioSinActasRelacionadasException();
		}

		Integer nro = Integer.MIN_VALUE;
		for (ActaRelacionada c : convenio.getActasRelacionadas()) {
			String nroStr = c.getActaRelacionada().getNumero();
			try {
				Integer nroAux = Integer.valueOf(nroStr);
				if (nroAux.compareTo(nro) > 0) {
					nro = nroAux;
				}
			} catch (Exception e) {
				// nothing to do here!
			}
		}

		convenio.setNumero(nro.toString() + "/1");
		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			ConvenioServiceUtil.save(convenio, user);
		} else {
			ConvenioServiceUtil.update(convenio, user);
		}
		return convenio;
	}

	private Convenio getConvenioFromRequest(HttpServletRequest req,
			Convenio convenio) throws ParseException {
		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		// String empleador = ParamUtil.getString(req, "empleador", "");
		int idconvenio=0;
		convenio.setEmpresa(new Empresa(cuit, sucu, null));
		if(convenio.getId()==0){
			idconvenio = ParamUtil.getInteger(req, "convenio_id");	
		}else{
			idconvenio=convenio.getId();
		}
		
		if (ParamUtil.getString(req, "fechaInicioDia") != null
				&& !ParamUtil.getString(req, "fechaInicioDia").equals("")) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaInicioDia = ParamUtil.getString(req, "fechaInicioDia");
			String fechaInicioMes = ParamUtil.getString(req, "fechaInicioMes");
			fechaInicioMes = String
					.valueOf(Integer.valueOf(fechaInicioMes) + 1);
			String fechaInicioAnio = ParamUtil
					.getString(req, "fechaInicioAnio");
			convenio.setFechaInicio(format.parse(fechaInicioDia + "-"
					+ fechaInicioMes + "-" + fechaInicioAnio));
		}
		String convenioNro = ParamUtil.getString(req, "convenio_numero");
		convenio.setNumero(convenioNro);

		String ajusteCapStr = ParamUtil.getString(req,
				"ajuste_capital_forma_pago", "0");
		if (ajusteCapStr.equals("")) {
			ajusteCapStr = "0";
		}

		String ajusteIntStr = ParamUtil.getString(req,
				"ajuste_interes_forma_pago", "0");
		if (ajusteIntStr.equals("")) {
			ajusteIntStr = "0";
		}
		String estadoSeguim = ParamUtil.getString(req, "estado_seguim", null);
		ConvenioEstadoSeguimiento ces = null;
		try{
			int id = Integer.parseInt(estadoSeguim);
			ces = new ConvenioEstadoSeguimiento(id, "");
		}catch (Exception e) {
//			nada, dejamos el null
		}
		convenio.setEstadoSeguimiento(ces);
		convenio.setId(idconvenio);

		convenio.setAjusteCapital(new BigDecimal(ajusteCapStr));
		convenio.setAjusteInteres(new BigDecimal(ajusteIntStr));
		
		//ES ALTA
		if(convenio.getId()==0){
			completarDatosActasRelacionadas(req, convenio);
		}

		List<ConvenioPago> pagos = convenio.getPagos();
		if (pagos == null) {
			pagos = new ArrayList<ConvenioPago>();
			convenio.setPagos(pagos);
		}
		ConvenioPago aPagoCuota = null;
		for (ConvenioPago a : pagos) {
			if (a.getTipo().equals(ConvenioPago.Tipo.CUOTA)) {
				aPagoCuota = a;
			}
		}
		if (aPagoCuota == null) {
			aPagoCuota = new ConvenioPago();
			pagos.add(aPagoCuota);
		}
		aPagoCuota.setTipo(ConvenioPago.Tipo.CUOTA);
		aPagoCuota.setImporte(convenio.getTotal());
		aPagoCuota.setFechaPago(convenio.getFechaInicio());

		BigDecimal interes = convenio.getInteresFromPagos();
		convenio.setInteres(interes);

		return convenio;
	}

	private void completarDatosActasRelacionadas(HttpServletRequest req,
			Convenio convenio) {
		if (convenio.getActasRelacionadas() != null) {
			for (ActaRelacionada ar : convenio.getActasRelacionadas()) {
				String ajuste = ParamUtil.getString(req, "ajuste_capital_"
						+ ar.getActaRelacionada().getId());
				ar.setSaldo(new BigDecimal(ajuste).add(ar.getImporte()));
			}
			BigDecimal deudaActas = convenio.getDeudaFromActasRelacionadas();
			convenio.setDeudaActasRelacionadas(deudaActas);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getBancos(renderRequest);

		HttpServletRequest httpServletRequest = PortalUtil
				.getHttpServletRequest(renderRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Convenio convenio = getConvenioEntry(renderRequest);
		if (!SessionErrors.isEmpty(renderRequest)) {
			String accionOriginal = renderRequest
					.getParameter("accionOriginal");
			if (accionOriginal != null) {
				renderRequest.setAttribute("accionOriginal", accionOriginal);
			}
		} else {
			session.removeAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		}

		if (convenio != null) {
			httpServletRequest.getSession().setAttribute(
					WebKeysTesoreria.CONVENIO_EN_EDICION, convenio);
		}

		renderRequest.setAttribute(WebKeysTesoreria.CONVENIOS_ACTION_EDICION,
				WebKeysTesoreria.CONVENIOS_ACTION_EDICION);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.convenios.editar_convenios_entry"));
	}

	private Convenio getConvenioEntry(RenderRequest renderRequest) {
		Convenio conv = null;
		String idString = renderRequest.getParameter("convenio_id");
		if (idString == null || idString.trim().equals("")
				|| idString.trim().equals("0")) {
			idString = (String) renderRequest.getAttribute("convenio_id");
		}
		if (idString != null && !idString.trim().equals("")) {
			int id = Integer.parseInt(idString);
			if (id > 0) {
				conv = ConvenioServiceUtil.getConvenio(id,0);
			}
		}
		return conv;
	}
}
