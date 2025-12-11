package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class HabilitaCargaDetalleSeguimientoSURAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean result = false;
		HttpSession session = (HttpSession) req.getSession();
		SeguimientoSur seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
		int respuesta = 1;
		
		for(SeguimientoSurDetalle d:seguimiento.getDetalles()){
			if(d.getFechaNotificacion()==null || d.getFechaRespuesta()==null){
			   respuesta=0;
			   break;
			}
		}
		return "{ \"validado\" : \"" + String.valueOf(respuesta) + "\"}";
	}
}