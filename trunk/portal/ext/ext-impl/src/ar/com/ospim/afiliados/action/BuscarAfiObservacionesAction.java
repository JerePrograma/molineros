package ar.com.ospim.afiliados.action;

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

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiObservacion;
import ar.com.ospim.afiliados.services.AfiObservacionServiceUtil;
import ar.com.ospim.util.StringUtils;

/**
 * @author SVA
 * 
 */
public class BuscarAfiObservacionesAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
// redirige al render
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {

	// preferi no hacer nada x el processAction...
//			System.out.println("pasando x el processAction");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		User usuario = PortalUtil.getUser(renderRequest);
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
		String observacionInterna = ParamUtil.getString(renderRequest, "observacion_interna");
		Integer inte = ParamUtil.getInteger(renderRequest, "integ");
		int idObs = ParamUtil.getInteger(renderRequest, "idObservacionInt",0);
		
		AfiObservacion obsInt =null;
		
		if (!StringUtils.checkEmpty(cmd)) {

			if(cmd.equals(Constants.VIEW) ){ // Prepara popup view una observación interna.

				obsInt = AfiObservacionServiceUtil.getObservacion(idObs);
				renderRequest.setAttribute(WebKeysAfiliados.OBSERVACION_GRUPO_FLIAR_VER, obsInt);

				return mapping.findForward(getForward(renderRequest,"portlet.afiliados.observacion.view"));
			}
  			
			if(cmd.equals(Constants.SEARCH) ){ // Prepara popup view una observación interna.
				
				List<AfiObservacion> obsInternasGrupoFliar =  AfiObservacionServiceUtil.getObservaciones(cuilTitular, inte);
				
				renderRequest.setAttribute(WebKeysAfiliados.OBSERVACIONES_GRUPO_FLIAR, obsInternasGrupoFliar);

				return mapping.findForward(getForward(renderRequest,"portlet.afiliados.observaciones.view"));
			}
			
			if(cmd.equals(Constants.SAVE) ){
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				obsInt = new AfiObservacion(0, cuilTitular, inte, observacionInterna, usuario.getScreenName(), null, null, null);
				
				try {
					AfiObservacionServiceUtil.insertarObservaciones(obsInt, usuario.getScreenName());
				}catch (SystemException e) {
					_log.error("Error al insertr observacion interna afiliado " + e);
					return mapping.findForward(getForward(renderRequest,"portlet.afiliados.error"));
				}
				
				List<AfiObservacion> obsInternasGrupoFliar =  AfiObservacionServiceUtil.getObservaciones(cuilTitular, inte);
				
				renderRequest.setAttribute(WebKeysAfiliados.OBSERVACIONES_GRUPO_FLIAR, obsInternasGrupoFliar);
			}
	
		}

		
		return mapping.findForward(getForward(renderRequest,"portlet.afiliados.observaciones.view"));

	}
	
}
