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
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarSeguimientoSurDetalleAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarSeguimientoSurDetalleAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.seguimientosurdetalle.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		SeguimientoSur seguimiento=null;
		seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		try {
		   if (!StringUtils.checkEmpty(cmd)) {
			   if(cmd.equals(Constants.ADD) || cmd.equals(Constants.EDIT) ){
				   String notificacionDia = ParamUtil.getString(renderRequest,"notificaciondia");
				   String notificacionMes = ParamUtil.getString(renderRequest,"notificacionmes");
				   String notificacionAnio = ParamUtil.getString(renderRequest,"notificacionanio");
				   
				   String estadoDescripcion = ParamUtil.getString(renderRequest,"estadoospimdescripcion");
				   Integer estadoId = ParamUtil.getInteger(renderRequest,"estadoospim");
				   
				   Date notificacion = null;
				   try {
					   notificacion = formatoDeFechas.parse(notificacionDia + "/"
							+ (Integer.parseInt(notificacionMes) + 1) + "/"
							+ notificacionAnio);
				   } catch (Exception e) {
					   notificacion = null;
				   }
				   
				   String observaciones = ParamUtil.getString(renderRequest,"observaciones");
				   
				   if(cmd.equals(Constants.ADD) ){
					   SeguimientoSurDetalle detalle= new SeguimientoSurDetalle();
					   Integer idDetalle= (int) Math.floor(Math.random()*100);
					   detalle.setFechaNotificacion(notificacion);
					   detalle.setEstadoDescripcion(estadoDescripcion);
					   detalle.setEstadoId(estadoId);
					   detalle.setObservaciones(observaciones);
					   detalle.setId(idDetalle);
					   detalle.setFechaCarga(new Date());
					   seguimiento.getDetalles().add(detalle);
				   }
				   
				   if(cmd.equals(Constants.EDIT) ){
					   Integer idDetalle = ParamUtil.getInteger(renderRequest,"iddetalle");
					   for(SeguimientoSurDetalle ds:seguimiento.getDetalles()){
						  if(ds.getId()==idDetalle){
							   ds.setFechaNotificacion(notificacion);
							   ds.setEstadoDescripcion(estadoDescripcion);
							   ds.setEstadoId(estadoId);
							   ds.setObservaciones(observaciones);
							  break;
						  }
					   }
				   }
			   }
			   if(cmd.equals(Constants.DELETE) ){
				  Integer detalleId= ParamUtil.getInteger(renderRequest,"detalleid");
				  List<SeguimientoSurDetalle> ld = new ArrayList<SeguimientoSurDetalle>();
				  for(SeguimientoSurDetalle d: seguimiento.getDetalles()){
					  if(d.getId()!=detalleId){
						  ld.add(d);
					  }
				  }
				  seguimiento.setDetalles(ld);
			   }
			
		   }
		
			session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			
			

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.seguimientosurdetalle.result.search");
	}
	
	private void actualizaDetalle(){
			}
}