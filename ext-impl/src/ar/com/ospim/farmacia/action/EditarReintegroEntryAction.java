package ar.com.ospim.farmacia.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.farmacia.services.ReintegroFarmaciaServiceUtil;
import ar.com.ospim.global.ValidaExistePrestacion;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.liquidaciones.DuplicateReintegroIdException;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroPrestacionEntryException;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
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

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);

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
		if (cmd != null && cmd.equals(Constants.DELETE)){				
			return mapping.findForward(getForward(renderRequest,"portlet.farmacia.reintegros.busqueda_reintegro"));
		}
		return mapping.findForward(getForward(renderRequest,"portlet.farmacia.editar_reintegro_entry"));
	}

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		String deletePrestaci = ParamUtil.getString(actionRequest,
				"deletePrestaci", null);		
		int id_reintegro = 0;
		boolean errors = false;
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				id_reintegro = updateReintegroEntry(actionRequest, cmd);
				actionRequest.setAttribute(WebKeysFarmacia.ID_REINTEGRO_EN_EDICION,id_reintegro);
				setForward(actionRequest,"portlet.farmacia.editar_reintegro_entry");
			} else if (cmd.equals(Constants.DELETE) && deletePrestaci == null) {
				borraReintegroEntry(actionRequest);
				//setForward(actionRequest, "portlet.farmacia.view");
				actionRequest.setAttribute(Constants.CMD,Constants.DELETE );

			} else if (cmd.equals(Constants.DELETE) && deletePrestaci != null) {
				// id_reintegro = borraReintegroPrestacionEntry(actionRequest);
			}
		} catch (AfiliadoSinPlanException e) {
			StringBuilder error = new StringBuilder("Afiliado sin plan vigente");
			putError(actionRequest, error);
			errors = true;
		} catch (Exception e) {
			errors = true;
			if (e instanceof NoSuchReintegroEntryException
					|| e instanceof DuplicateReintegroIdException
					|| e instanceof NoSuchReintegroPrestacionEntryException
					|| e instanceof DuplicateReintegroPrestacionIdException) {
				SessionErrors.add(actionRequest, e.getClass().getName());
				setForward(actionRequest, "portlet.farmacia.error");
			}else if (e instanceof ValidaExistePrestacion){
				SessionErrors.add(actionRequest, e.getClass().getName());	
			} else {
				throw e;
			}
		}
		if (SessionErrors.isEmpty(actionRequest) && !errors) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
	}

	@SuppressWarnings("unused")
	private StringBuilder getFechasError() {
		StringBuilder error = new StringBuilder();
		error.append("La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado");
		return error;
	}

	private void putError(ActionRequest actionRequest, StringBuilder error) {
		actionRequest.setAttribute(WebKeysFarmacia.ERROR_PARA_ALERT, error.toString());
	}

	protected void borraReintegroEntry(ActionRequest actionRequest)
			throws Exception {
		int id_reintegro = ParamUtil.getInteger(actionRequest, "numero", 0);
		User user = PortalUtil.getUser(actionRequest);
		actionRequest.setAttribute( "numero",id_reintegro );

		ReintegroFarmaciaServiceUtil.borraReintegroEntry(id_reintegro, user.getScreenName());
	}

	@SuppressWarnings("unchecked")
	protected int updateReintegroEntry(ActionRequest actionRequest,String command) throws ValidaExistePrestacion, PortalException, SystemException {

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		String fechaAnio = ParamUtil.getString(actionRequest, "fechaAnio");
		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(actionRequest,"periodoMesAnio");
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

		String cuil_titular = ParamUtil.getString(actionRequest, "cuil", null);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		String bajaFecha = ParamUtil.getString(actionRequest, "baja_fecha",null);

		int seccional = ParamUtil.getInteger(actionRequest, "id_seccional_r", 0);
		int numero = ParamUtil.getInteger(actionRequest, "numero", 0);

		@SuppressWarnings("unused")
		Date fechaBaja;
		try {
			fechaBaja = formatoDeFecha.parse(bajaFecha);
		} catch (Exception e) {
			fechaBaja = null;
		}
		
		String cbu = ParamUtil.getString(actionRequest, "cbu", null);
		String cuilCuenta = ParamUtil.getString(actionRequest, "cuil_cuenta", null);
		String emailCuenta = ParamUtil.getString(actionRequest, "email_cuenta", null);
		String apellidoCuenta = ParamUtil.getString(actionRequest, "apellido_cuenta", null);
		String nombreCuenta = ParamUtil.getString(actionRequest, "nombre_cuenta", null);

		ArrayList<ReintegroMedicamentoItem> medicamentos = (ArrayList<ReintegroMedicamentoItem>) session
											.getAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
		
	   if (command.equals(Constants.UPDATE) && !this.validarExistePrestaciones(medicamentos)){
		   throw new  ValidaExistePrestacion();
	   }
	
	   if (command.equals(Constants.ADD) && command.equals(Constants.UPDATE) ) {
		   
	   }

		User user = PortalUtil.getUser(actionRequest);
		if (command.equals(Constants.ADD)) {
			numero = ReintegroFarmaciaServiceUtil.cargaReintegroFarmaciaEntry(
					fecha, periodo, cuil_titular, inte, seccional,
					medicamentos, user.getScreenName(),cbu,cuilCuenta,emailCuenta,
					apellidoCuenta,nombreCuenta);
		}
		if (command.equals(Constants.UPDATE)) {
			numero = ReintegroFarmaciaServiceUtil.actualizaReintegroFarmaciaEntry(numero,
					fecha, periodo, cuil_titular, inte, seccional,
					medicamentos, user.getScreenName());
		}
		return numero;
	}
	
	
	
	
	
	
	private boolean validarExistePrestaciones(ArrayList<ReintegroMedicamentoItem> medicamentos ){
	
		boolean  out =  false;
		for (ReintegroMedicamentoItem item : medicamentos) {		
			if (!item.isDelete() && item.getId() > 0) {
				out = true;
			}
		}
		return out;
	}
	
}