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
import ar.com.ospim.autorizaciones.beans.SeguimientoSurPrestador;
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

public class EditarSeguimientoSurPrestadorAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarSeguimientoSurPrestadorAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.seguimientosurprestador.result.search");
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
				   String prestadorDia = ParamUtil.getString(renderRequest,"prestadordia");
				   String prestadorMes = ParamUtil.getString(renderRequest,"prestadormes");
				   String prestadorAnio = ParamUtil.getString(renderRequest,"prestadoranio");
				   Date estadoFecha = null;
				   try {
					   estadoFecha = formatoDeFechas.parse(prestadorDia + "/"
							+ (Integer.parseInt(prestadorMes) + 1) + "/"
							+ prestadorAnio);
				   } catch (Exception e) {
					   estadoFecha = null;
				   }
		           
				   String observaciones = ParamUtil.getString(renderRequest,"observaciones");
				   String prestadorCuit = ParamUtil.getString(renderRequest,"prestadorcuit");
				   String prestadorDescripcion = ParamUtil.getString(renderRequest,"prestadordescripcion");
				   String usuario = user.getScreenName();
				   Integer prestadorId = ParamUtil.getInteger(renderRequest,"prestadorid");
				   
				   if(cmd.equals(Constants.ADD) ){
					   SeguimientoSurPrestador detalle= new SeguimientoSurPrestador();
					   Integer idDetalle= (int) Math.floor(Math.random()*100);
					   detalle.setFechaEstado(estadoFecha);
					   detalle.setDescripcionPrestador(prestadorDescripcion);
					   detalle.setIdPrestador(prestadorId);
					   detalle.setCuitPrestador(prestadorCuit);
					   detalle.setId(idDetalle);
					   detalle.setObservaciones(observaciones);
					   detalle.setUsuario(usuario);
					   
					   seguimiento.getPrestadores().add(detalle);
				   }
				   
				   if(cmd.equals(Constants.EDIT) ){
					   Integer idDetalle = ParamUtil.getInteger(renderRequest,"iddetalle");
					   for(SeguimientoSurPrestador ds:seguimiento.getPrestadores()){
						  if(ds.getId().equals(idDetalle)){
							  
							  ds.setFechaEstado(estadoFecha);
							  ds.setDescripcionPrestador(prestadorDescripcion);
							  ds.setIdPrestador(prestadorId);
							  ds.setCuitPrestador(prestadorCuit);
							  ds.setId(idDetalle);
							  ds.setObservaciones(observaciones);
							  ds.setUsuario(usuario);
							  break;
						  }
					   }
				   }
			   }
			   if(cmd.equals(Constants.DELETE) ){
				  Integer detalleId= ParamUtil.getInteger(renderRequest,"detalleid");
				  List<SeguimientoSurPrestador> ld = new ArrayList<SeguimientoSurPrestador>();
				  for(SeguimientoSurPrestador d: seguimiento.getPrestadores()){
					  if(!d.getId().equals(detalleId)){
						  ld.add(d);
					  }
				  }
				  seguimiento.setPrestadores(ld);
			   }
			
		   }
  		   session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.seguimientosurprestador.result.search");
	}
}