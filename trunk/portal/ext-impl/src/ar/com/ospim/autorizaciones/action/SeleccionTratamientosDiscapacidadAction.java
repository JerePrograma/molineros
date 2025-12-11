/**
 */

package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SeleccionTratamientosDiscapacidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SeleccionTratamientosDiscapacidadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.discapacidad.popup.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		try {
			String tratamientos = ParamUtil
					.getString(renderRequest, "tratamientos", null);
			
			if(tratamientos.length()>0){
			  SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			  List<TratamientoDiscapacidadSeguimiento> ltd = new ArrayList<TratamientoDiscapacidadSeguimiento>();
			  String trata[]=tratamientos.split(";");
			  for(int i=0;i<trata.length;i++){
				  
				  TratamientoDiscapacidad td = (TratamientoDiscapacidad) TratamientoDiscapacidadServiceUtil
							.getTratamientoDiscapacidad(Integer.parseInt(trata[i]));
				  
				  TratamientoDiscapacidadSeguimiento tds = new TratamientoDiscapacidadSeguimiento();
				  tds=tds.clonar(td);
				  ltd.add(tds);
			  }
			  
			  
			  for(TratamientoDiscapacidadSeguimiento t: ltd){
				  Boolean existe=false;
				  for(TratamientoDiscapacidadSeguimiento ts:seguimiento.getTratamientos()){
					  if(t.getId_tratamiento()==ts.getId_tratamiento()){
						  existe=true;
						  break;
					  }
				  }
				  if(!existe){
					  seguimiento.getTratamientos().add(t);
				  }
			  }
			  
//			  seguimiento.setTratamientos(ltd);
			  
			  
			  session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.discapacidad.result.search");
		
	}
}