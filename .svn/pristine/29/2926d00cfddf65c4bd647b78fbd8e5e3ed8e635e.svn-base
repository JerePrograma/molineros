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

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurPrestador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
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

public class EditarSeguimientoSurCodigoNomencladorAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarSeguimientoSurPrestadorAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.seguimientosurcodigonomenclador.result.search");
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
			   if(cmd.equals(Constants.ADD) ){
				   String codigo = ParamUtil.getString(renderRequest,"codigo");
				   String descripcion = ParamUtil.getString(renderRequest,"descripcion");
				   Integer tipoNomenclador = ParamUtil.getInteger(renderRequest,"tiponomenclador");
				   
				   List<Nomenclador> l = NomencladorServiceUtil.getListaNomenclador(tipoNomenclador,descripcion,0,codigo,false,"");
				   if(l.size()>0){
				     Boolean existe=false;
				     for(Nomenclador n: seguimiento.getCodigosPresentados()){
					   if(n.getId_prestacion()==l.get(0).getId_prestacion()){
						  existe =true; 
					   }
				     }
				   
				     if(!existe){
				       String usuario = user.getScreenName();
				       Nomenclador detalle= new Nomenclador();
				     
					   detalle = l.get(0);
					   seguimiento.getCodigosPresentados().add(detalle);
				     
				     }
				   }  
			   }
			   
			   if(cmd.equals(Constants.DELETE) ){
				  Integer detalleId= ParamUtil.getInteger(renderRequest,"detalleid");
				  List<Nomenclador> ld = new ArrayList<Nomenclador>();
				  for(Nomenclador d: seguimiento.getCodigosPresentados() ){
					  if(d.getId_prestacion() !=detalleId){
						  ld.add(d);
					  }
				  }
				  seguimiento.setCodigosPresentados(ld);
			   }
			   
			   Double topeRecupero =0D;
			   for(Nomenclador n:seguimiento.getCodigosPresentados()){
				   topeRecupero += n.getImporte()!=null?n.getImporte():0;
			   }
			   seguimiento.setTopeRecupero(topeRecupero);
			
		   }
  		   session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.seguimientosurcodigonomenclador.result.search");
	}
}