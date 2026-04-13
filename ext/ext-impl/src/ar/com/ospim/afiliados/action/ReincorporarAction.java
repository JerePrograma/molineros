package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.ReincorporarServiceUtil;
import ar.com.ospim.afiliados.services.SituLaboralServiceUtil;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ReincorporarAction"><b><i>View Source</i></b></a>
 * <p>
 * Graba las reincorporaciones
 * 
 * @author Carlos Rivas
 * 
 */
public class ReincorporarAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ReincorporarAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				req).getSession();
		EditarAfiliadoEntryAction.removeDataFromSession(req, session);
		
		this.cargarListas(req);
		
		Date vigen_fecha = null;
		String vigen_fecha_string = req.getParameter("vigen_fecha");
		if (null != vigen_fecha_string) {
//			vigen_fecha = DateUtils.parse(vigen_fecha_string, "dd/MM/yyyy");
			vigen_fecha = DateUtils.parse(vigen_fecha_string, "ddMMyyyy");
		}
		Integer idCorrespondencia = ParamUtil.getInteger(req,"numero_correspondencia", 0);

		boolean recupera_planes = ParamUtil.getBoolean(req,"desea_recuperar_planes");
		
		String reincorporar = ParamUtil.getString(req, "reincorporar");
		_log.debug("reincorporar: " + reincorporar);
		_log.debug("vigen_fecha: " + vigen_fecha);
		_log.debug("idCorrespondencia: " + idCorrespondencia);
		_log.debug("recupera_planes: " + recupera_planes);
		
		try {
			List<Afiliado> afiliados = new ArrayList<Afiliado>();
			Date fechaIngreTitu = null;
			Afiliado afiTitu = null;
			if (null != reincorporar && reincorporar.trim().length() > 0) {
				afiliados = listaAfiliadosReincorporar(reincorporar, idCorrespondencia);
				for (int i = 0; i < afiliados.size(); i++) {
					int inteTit = afiliados.get(i).getInte();
					if (inteTit == 0) {
//						fechaIngreTitu = afiliados.get(i).getIngre_fecha();
						fechaIngreTitu = afiliados.get(i).getVigen_fecha();
						afiTitu = afiliados.get(i);
					}
				}
			}
			List<SituacionLaboral> laboralList = SituLaboralServiceUtil.buscaSituLaboral(afiTitu.getCuil_titular(),0);
			
			session.setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL,laboralList);
			
//			si no recupera planes
//			poner la fecha inicio del nuevo plan con la fecha de reincorporacion
			if(!recupera_planes){
				AfiPlan planVigencia = new AfiPlan();
				planVigencia.setCuil_titular(afiTitu.getCuil_titular());
				planVigencia.setInte(0);
				planVigencia.setVigenDesde(vigen_fecha);
				
				session.setAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION, planVigencia);
			}
//			continuidad y rec planes true entro
			if (!(vigen_fecha.compareTo(fechaIngreTitu) == 0 && !recupera_planes)) { 
				session.setAttribute(WebKeysAfiliados.REINCORPORAR_AFILIADOS_A_RECUPERAR,afiliados);
				session.setAttribute(WebKeysAfiliados.REINCORPORAR_VIGEN_FECHA,vigen_fecha);
				session.setAttribute(WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES,Boolean.valueOf(recupera_planes));
				session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,afiTitu);
				afiTitu.setVigen_fecha(vigen_fecha);
				req.setAttribute("tabs1", "informacion_adicional");
				req.setAttribute("tabs_a_mostrar", "informacion_general,informacion_adicional");
				req.setAttribute(WebKeysAfiliados.DESDE_REINCORPORAR, WebKeysAfiliados.DESDE_REINCORPORAR);
				session.setAttribute(Constants.CMD, Constants.UPDATE);
			
				return mapping.findForward("portlet.afiliados.editar_afiliado_entry");
			} else {
				SessionErrors.add(req, "Verifique los datos ingresados.");
			}
		} catch (Exception e) {
			_log.error(e);
			SessionErrors.add(req, e.getClass().getName());
			SessionErrors.add(req, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(req)) {
			SessionMessages.add(req, "request_processed", "");
		}
		return mapping.findForward("portlet.afiliados.reincorporaciones.result");
	}

	public List<Afiliado> listaAfiliadosReincorporar(String cred_selecc, int idCorrespondencia) {
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		StringTokenizer cred = new StringTokenizer(cred_selecc, "-");
		while (cred.hasMoreElements()) {
			StringTokenizer cuilInte = new StringTokenizer(cred.nextToken(), "|");
			String cuil = cuilInte.nextToken();
			String inte = cuilInte.nextToken();
			Afiliado afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuil,Integer.valueOf(inte));
			afiliado.setIdCorrespondencia(idCorrespondencia);
			afiliados.add(afiliado);
		}

		return afiliados;
	}
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{
		
		TraeListasServiceUtil.getMotivosBaja(renderRequest);

		TraeListasServiceUtil.getPlanes(renderRequest);
		
		TraeListasServiceUtil.getTercerizadoraServicio(renderRequest);
		
		TraeListasServiceUtil.getCategoriasLaborales(renderRequest);
		
		TraeListasServiceUtil.getSituacionRevista(renderRequest);

	}
}