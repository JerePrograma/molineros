package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;


import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.action.ListaMatriculasAction;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.MatriculaNacionalPrestadorException;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.MatriculaProvincialPrestadorException;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.services.CrmServiceUtil;



public class ListaContactosAction extends PortletAction {	
	
	private static Log _log = LogFactoryUtil.getLog(ListaContactosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String cuil = ParamUtil.getString(renderRequest, "cuil_contacto");		
		int inte = ParamUtil.getInteger(renderRequest, "inte_contacto");
		int idreclamoprestacional = ParamUtil.getInteger(renderRequest, "idreclamoprestacion",0);
		boolean consultaregistro =  ParamUtil.getBoolean(renderRequest, "modoconsulta",false);
		
		@SuppressWarnings("unchecked")
		List<ContactoCRM> ultimosContactos;		
        if (idreclamoprestacional >0){
        	if (consultaregistro ){
        		ultimosContactos = CrmServiceUtil.buscarUltimosContactosCRMSoloAsociados(cuil  , inte , idreclamoprestacional );        		
        		renderRequest.setAttribute(Constants.CMD,Constants.VIEW);
        	}else{
        		ultimosContactos = CrmServiceUtil.buscarUltimosContactosCRMconDataReclamo(cuil  , inte , idreclamoprestacional );	
        	}
        	
        }else{
        	ultimosContactos = CrmServiceUtil.buscarUltimosContactosCRMconDataReclamo(cuil  , inte );	
        }
        
		
		if(ultimosContactos  == null){
			ultimosContactos = new ArrayList<ContactoCRM>();
		}				
	
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION );
		session.setAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION , ultimosContactos );
		 
		
		
		
		
		return mapping.findForward(getForward(renderRequest,"portlet.autorizaciones.reclamosprestacionales.contactos.reclamo"));
	}
	
			
}