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
import ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarSeguimientoSurEstadoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarSeguimientoSurEstadoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.seguimientosurestado.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		SeguimientoSur seguimiento=null;
		seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		try {
		   if (!StringUtils.checkEmpty(cmd)) {
			   if(cmd.equals(Constants.ADD) || cmd.equals(Constants.EDIT) ){
				   String estadoDia = ParamUtil.getString(renderRequest,"estadodia");
				   String estadoMes = ParamUtil.getString(renderRequest,"estadomes");
				   String estadoAnio = ParamUtil.getString(renderRequest,"estadoanio");
				   Date estadoFecha = null;
				   try {
					   estadoFecha = formatoDeFechas.parse(estadoDia + "/"
							+ (Integer.parseInt(estadoMes) + 1) + "/"
							+ estadoAnio);
				   } catch (Exception e) {
					   estadoFecha = null;
				   }
		           
				   String observaciones = ParamUtil.getString(renderRequest,"observaciones");
				   String estadoDescripcion = ParamUtil.getString(renderRequest,"estadodescripcion");
				   String usuario = user.getScreenName();
				   Integer estadoId = ParamUtil.getInteger(renderRequest,"estadoid");
				   
				   String motivoDescripcion = ParamUtil.getString(renderRequest,"motivodescripcion");
				   Integer motivoId = ParamUtil.getInteger(renderRequest,"motivoid");
				   
				   
				   if(cmd.equals(Constants.ADD) ){
					   SeguimientoSurEstado detalle= new SeguimientoSurEstado();
					   Integer idDetalle= (int) Math.floor(Math.random()*100);
					   detalle.setFechaEstado(estadoFecha);
					   detalle.setDescripcionEstado(estadoDescripcion);
					   detalle.setIdEstado(estadoId);
					   detalle.setId(idDetalle);
					   detalle.setObservaciones(observaciones);
					   detalle.setUsuario(usuario);
					   detalle.setIdMotivo(motivoId);
					   detalle.setDescripcionMotivo(motivoDescripcion);
					   
					   seguimiento.getEstados().add(detalle);
				   }
				   
				   if(cmd.equals(Constants.EDIT) ){
					   Integer idDetalle = ParamUtil.getInteger(renderRequest,"iddetalle");
					   for(SeguimientoSurEstado ds:seguimiento.getEstados()){
						  if(ds.getId().equals(idDetalle)){
							  
							  ds.setDescripcionEstado(estadoDescripcion); 
							  ds.setFechaEstado(estadoFecha);
							  ds.setIdEstado(estadoId);
							  ds.setObservaciones(observaciones);
							  ds.setUsuario(usuario);
							  ds.setIdMotivo(motivoId);
							  ds.setDescripcionMotivo(motivoDescripcion);
							  
							  break;
						  }
					   }
				   }
			   }
			   if(cmd.equals(Constants.DELETE) ){
				  Integer detalleId= ParamUtil.getInteger(renderRequest,"detalleid");
				  List<SeguimientoSurEstado> ld = new ArrayList<SeguimientoSurEstado>();
				  for(SeguimientoSurEstado d: seguimiento.getEstados()){
					  if(!d.getId().equals(detalleId)){
						  ld.add(d);
					  }
				  }
				  seguimiento.setEstados(ld);
			   }
			
		   }
		
			session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			
			

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.seguimientosurestado.result.search");
	}
	
	
}