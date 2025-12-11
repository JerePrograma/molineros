package ar.com.empresas.action;

import java.sql.SQLException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.empresas.beans.Actividad;
import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EntidadCamaraEmpresa;
import ar.com.ospim.global.beans.RamoEmpresa;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;

public class EditarEmpleadoresEntryAction extends EmpleadoresBaseAction {

	private static Log logger = LogFactoryUtil
			.getLog(EditarEmpleadoresEntryAction.class);
	
	protected boolean isCheckMethodOnProcessAction() {
		return _CHECK_METHOD_ON_PROCESS_ACTION;
	}

	private static final boolean _CHECK_METHOD_ON_PROCESS_ACTION = false;

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		logger.debug("entrando...");
		String flag = actionRequest.getParameter("flag");
		String flagEstudio = actionRequest.getParameter("popupSeguimiento");
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		actionRequest.setAttribute("popupSeguimiento", flagEstudio);		
		int entidad=0;		
		if (actionResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}
		try {

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)
					|| cmd.equals(WebKeysGlobal.CAMBIO_SOLAPA)) {
				this.updateEmpresaEntry(actionRequest, actionResponse, cmd);
				if (entidad==WebKeysGlobal.ESTUDIO) {
					setForward(actionRequest,
							"portlet.afiliados.empleadores.view_empleadores_entry");
				} else if (flag != null && flag.equals("true")) {
					actionRequest.setAttribute("cuit",
							ParamUtil.getString(actionRequest, "cuit"));
					setForward(actionRequest,
							"portlet.afiliados.empleadores.editar_empleadores_popup_entry");
					actionRequest.setAttribute("empresa_grabada", "true");
				}

			} else if (cmd.equals(Constants.DELETE)) {
				this.borraEmpresaEntry(actionRequest);
				setForward(actionRequest, "portlet.afiliados.view");
			}
		} catch (DuplicateEmpresaIdException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute("empresa_ya_existe", "true");
			if (flag != null && flag.equals("true")) {
				setForward(actionRequest,
						"portlet.afiliados.empleadores.editar_empleadores_popup_entry");
			}
		}

		if (!cmd.equals(WebKeysGlobal.CAMBIO_SOLAPA)) {
			if (SessionErrors.isEmpty(actionRequest)) {
				String successMessage = ParamUtil.getString(actionRequest,
						"successMessage");
				SessionMessages.add(actionRequest, "request_processed",
						successMessage);
			}
		}
	}

	private void borraEmpresaEntry(ActionRequest actionRequest) {
		// TODO Auto-generated method stub

	}

	private void updateEmpresaEntry(ActionRequest actionRequest, ActionResponse actionResponse, String cmd)
			throws PortalException, SystemException, SQLException {
		Empresa empresa = (Empresa) actionRequest.getPortletSession()
				.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,
						PortletSession.APPLICATION_SCOPE);
		
		int entidad = WebKeysGlobal.OSPIM;
		
		if (actionResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}
		
		
		logger.debug("entrando updateEmpresaEntry...");
		
		boolean popupSeguimiento=ParamUtil.getBoolean(actionRequest, "popupSeguimiento");
		String tabs1=actionRequest.getParameter("tabs1");
		if (entidad!=WebKeysGlobal.ESTUDIO && !popupSeguimiento && null!= tabs1 && !tabs1.equals("datos-fiscales")) {
			String iva = ParamUtil.getString(actionRequest, "iva");
			int camara = ParamUtil.getInteger(actionRequest, "entidad");
			String ganancias = ParamUtil.getString(actionRequest, "gananacias");
			String tipoCaiCae = ParamUtil.getString(actionRequest, "tipo_cai_cae");
			String numeroCaiCae = ParamUtil.getString(actionRequest, "numero_cai_cae");
			
			empresa.setImpIva(iva);
			empresa.setEntidadCamaraEmpresa(new EntidadCamaraEmpresa(camara));
			empresa.setImpGanancias(ganancias);
			empresa.setNumeroCaeCai(StringUtils.checkNotEmpty(numeroCaiCae)?numeroCaiCae:null);
			empresa.setCaeCai(StringUtils.checkNotEmpty(tipoCaiCae)?tipoCaiCae:null);
		} else if (entidad==WebKeysGlobal.ESTUDIO || (popupSeguimiento || null==actionRequest.getParameter("tabs1")  || (null!= actionRequest.getParameter("tabs1") && actionRequest.getParameter("tabs1").equals("datos-fiscales")))){
			String cuit = ParamUtil.getString(actionRequest, "cuit");
			String sucu = ParamUtil.getString(actionRequest, "sucursal");
			String desc = ParamUtil.getString(actionRequest, "desc");
			int ramo = ParamUtil.getInteger(actionRequest, "ramo");
			int actividadPrincipal=ParamUtil.getInteger(actionRequest, "cod_actividad");
			int actividadSecundaria=ParamUtil.getInteger(actionRequest, "cod_actividad_sec");
			String obs = ParamUtil.getString(actionRequest, "observaciones");
			int id_seccional =0;
			id_seccional=ParamUtil.getInteger(actionRequest,
					"id_seccional");
			if(id_seccional==0){
				id_seccional=ParamUtil.getInteger(actionRequest,
						"id_seccionalempre_");
			}
			if(actividadPrincipal==0){
				actividadPrincipal=ParamUtil.getInteger(actionRequest,
						"cod_actividadempre_");
			}
			
			if(actividadSecundaria==0){
				actividadSecundaria=ParamUtil.getInteger(actionRequest,
						"cod_actividad_secempre_");
			}
			
			String destino=ParamUtil.getString(actionRequest, "destino");
			String cbu=ParamUtil.getString(actionRequest, "cbu");
			String cheque=ParamUtil.getString(actionRequest, "cheque");
			Integer codigoRegimen = ParamUtil.getInteger(actionRequest, "regimen");
			Regimen reg = new Regimen(codigoRegimen);
			
			empresa.setCuit(cuit);
			empresa.setSucursal(sucu);
			empresa.setRazon_soc(desc);
			empresa.setRamoEmpresa(new RamoEmpresa(ramo));
			//empresa.setRamoEmpresaSecundario(new RamoEmpresa(ramoSecundario));
			empresa.setObservaciones(obs);
			empresa.setId_seccional(id_seccional);
			empresa.setDestinoCorrespondencia(destino);
			empresa.setCBU(cbu);
			empresa.setPortaCheque(cheque);
			empresa.setActividadPrincipal(new Actividad(actividadPrincipal));
			empresa.setActividadSecundaria(new Actividad(actividadSecundaria));
			empresa.setRegimen(reg);
			
		}

		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			EmpresaServiceUtil.save(empresa, user.getScreenName());
		}else if (cmd.equals(Constants.UPDATE)){
			EmpresaServiceUtil.update(empresa, user.getScreenName());
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getSeccionales(renderRequest);
		TraeListasServiceUtil.getRamosEmpresa(renderRequest);
		TraeListasServiceUtil.getPosicionesIva(renderRequest);
		TraeListasServiceUtil.getEntidadesCamaraEmpresa(renderRequest);
		TraeListasServiceUtil.getBancos(renderRequest);
		EmpresaServiceUtil.getRegimenesRetencionGanancias(renderRequest);
		
		logger.debug("entrando...");
		
		boolean reactivar = ParamUtil.getBoolean(renderRequest, "reactivar");
		String idOp = ParamUtil.getString(renderRequest, "idOp");

		Empresa empresa = null;
		String cambioSolapa = renderRequest.getParameter("cambioSolapa");
//		String popupSeguimiento = renderRequest.getParameter("popupSeguimiento");

		if (reactivar) {
			String cuit = ParamUtil.getString(renderRequest, "cuit");
			String sucu = ParamUtil.getString(renderRequest, "sucu");
			try {
				EmpresaServiceUtil.reactivar(cuit, sucu);
			} catch (Exception e) {
				logger.debug("Error al reactivar empresa", e);
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
			renderRequest.setAttribute("tabs1", "empleadores");
			return mapping.findForward("portlet.afiliados.view");
		}

		if (cambioSolapa != null && cambioSolapa.equals("cambioSolapa")
				|| !SessionErrors.isEmpty(renderRequest)) {

			String accionOriginal = renderRequest
					.getParameter("accionOriginal");
			if (accionOriginal != null) {
				renderRequest.setAttribute("accionOriginal", accionOriginal);
			}

			empresa = (Empresa) renderRequest.getPortletSession().getAttribute(
					WebKeysEmpresas.EMPRESA_EN_EDICION,
					PortletSession.APPLICATION_SCOPE);
			if (empresa == null) {
				empresa = new Empresa();
			}
			/*
			 * if (renderRequest.getParameter("tabs1") != null) { if
			 * (renderRequest.getParameter("tabs1") .equals("datos-fiscales")) {
			 * getEmpleadorFromRequest(PortalUtil
			 * .getHttpServletRequest(renderRequest), empresa); } else { //
			 * salgo de datos fiscales getDatosFiscalesFromRequest(PortalUtil
			 * .getHttpServletRequest(renderRequest), empresa); } }
			 */
		} else {
			renderRequest.getPortletSession().removeAttribute(
					WebKeysEmpresas.EMPRESA_EN_EDICION,
					PortletSession.APPLICATION_SCOPE);
			logger.debug("buscando Empleador Completo...");
			empresa = getEmpleadorEntryCompleto(PortalUtil
					.getHttpServletRequest(renderRequest));
			
////			para el alta de empresa
//			if (empresa == null) {
//				empresa = new Empresa();
//				empresa.setCuit("");
//				empresa.setSucursal("");
//				empresa.setRazon_soc("");
//				empresa.setContactos(new ArrayList<Contacto>());
//				empresa.setContactosElectronicos(new ArrayList<ContactoElectronico>());
//			}
		}
		if(null!=idOp){
			renderRequest.setAttribute("idOp", idOp);
		}

		renderRequest.getPortletSession().setAttribute(
				WebKeysEmpresas.EMPRESA_EN_EDICION, empresa,
				PortletSession.APPLICATION_SCOPE);

		/*if (renderResponse != null && renderResponse.getNamespace() != null
				&& renderResponse.getNamespace().equals("_EST_1_")
				&& null != popupSeguimiento && popupSeguimiento.equals("true")) {
			BuscarSeguimientoEmpresaAction buscarseg = new BuscarSeguimientoEmpresaAction();
			buscarseg.buscarLlamadosCuit(renderRequest);
			return mapping.findForward(getForward(renderRequest,
					"portlet.estudio_isidro.seguimiento_empresa_result"));
		}*/
		
		int entidad=0;		
		if (renderResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}
		if(entidad==WebKeysGlobal.ESTUDIO){
			
			PortletSession portletSession = renderRequest.getPortletSession();
			
			LlamadosEstudio llest = (LlamadosEstudio) portletSession.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
			
			if(llest==null){
				llest=new LlamadosEstudio();
			}
			EmpresaServiceUtil.buscarDatosEmpresaSeguimientoMolinera(llest, renderRequest, null);			

			portletSession.setAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, llest.getEmpresa(),PortletSession.APPLICATION_SCOPE);
			
			portletSession.setAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,llest,PortletSession.APPLICATION_SCOPE);
		
			return mapping.findForward("portlet.afiliados.empleadores.view_empleadores_entry");
		}
		logger.debug("saliendo...");
		return mapping.findForward(getForward(renderRequest,
				"portlet.afiliados.empleadores.editar_empleadores_entry"));
	}
}