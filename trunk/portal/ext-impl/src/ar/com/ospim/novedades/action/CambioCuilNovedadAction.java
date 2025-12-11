package ar.com.ospim.novedades.action;

import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.novedades.beans.AfiliadoCambioCuil;
import ar.com.ospim.novedades.beans.Novedad;
import ar.com.ospim.novedades.beans.NovedadTotal;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CambioCuilNovedadAction extends PortletAction {

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int idNovedad = ParamUtil.getInteger(renderRequest, "id_novedad");
		
		String afiTipoDoc = ParamUtil.getString(renderRequest, "tipoDoc");
		String afiNroDoc = ParamUtil.getString(renderRequest, "nroDoc");

		User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
		Afiliado afi = null;
		List<Afiliado> afiliados = null;
		
		Novedad nove = null;
		
		try {
			nove = NovedadesServiceUtil.getInstance().getNovedadById(idNovedad);
			
			if(afiTipoDoc.isEmpty() && afiNroDoc.isEmpty()){
				afiliados = EditarAfiliadoServiceUtil.getAfiliadosPorDocumento(String.valueOf(nove.getDocumento_numero()),nove.getDocumento_tipo()) ;
			}else{
//				si entra por aca es porque recibimos el tipo y nro de documento del afiliado a cambiar...
//				procesamos el cambio...
				afiliados = EditarAfiliadoServiceUtil.getAfiliadosPorDocumento(afiNroDoc,afiTipoDoc) ;
				
				for (Iterator<Afiliado> iterator = afiliados.iterator(); iterator.hasNext();) {
					Afiliado afiliado = iterator.next();
					if(afiliado.getBaja_fecha() == null){
						afi = afiliado;
					}
				}
				AfiliadoCambioCuil cambioCuil = new AfiliadoCambioCuil();
//				datos del nuevo CUIL de Afiliado
				cambioCuil.setCuil(nove.getCuil());
				// el doc y nro viene con lso datos q tenemos en el padron, por eso lo utilizo para buscar al afiliado,
				// ahora hay q descomponer el cuil nuevo para obtener su dni y nro...
				cambioCuil.setDocumento_numero(nove.getCuil().substring(2, 10));
				cambioCuil.setDocumento_tipo(nove.getDocumento_tipo());
				if(afi.getInte() == 0 && nove.getCodigo_parentesco() == 0){ //parentesco = 0 TITULAR
					cambioCuil.setCuil_titular(nove.getCuil());
//					cambioCuil.setCuil_titular(nove.getCuil_titular()); // viene el que tenemos nosotros en la BD x eso no lo uso
					cambioCuil.setInte(afi.getInte());
				}else{
					cambioCuil.setCuil_titular(afi.getCuil_titular());	
					cambioCuil.setInte(afi.getInte());
				}
				
//				datos del CUIL de Afiliado
				cambioCuil.setCuil_anterior(afi.getCuil());
				cambioCuil.setCuil_titular_anterior(afi.getCuil_titular());
				cambioCuil.setInte_anterior(afi.getInte());
				cambioCuil.setDocumento_tipo_anterior(afi.getDocumento_tipo());
				cambioCuil.setDocumento_numero_anterior(afi.getDocu_numero());
				cambioCuil.setVigen_fecha(afi.getVigen_fecha());
				
				boolean result = NovedadesServiceUtil.getInstance().cambiaCuil(cambioCuil, user.getScreenName());
				
				if(result){
					return mapping.findForward(getForward(renderRequest, "portlet.novedades.result.search"));
				}
			}
			renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD ,nove);
			renderRequest.setAttribute("cambio_cuil_afiliados" , afiliados);
			
		} catch (Exception e) {
			setForward(renderRequest, "portlet.afiliados.error");
		}

		return mapping.findForward(getForward(renderRequest, "portlet.novedades.cambio.cuil.popup"));
	}

}
