package ar.com.ospim.rrhh.action ;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.rrhh.WebKeysRrhh;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.rrhh.services.TarjetasServiceUtil;
import ar.com.ospim.util.StringUtils;

	public class EditarTarjetaEntryAction extends PortletAction   {

	private Logger _log = Logger.getLogger(this.getClass());
		

	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
//		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		User usuario = PortalUtil.getUser(renderRequest);
        int idRegTarjeta = ParamUtil.getInteger(renderRequest, "id_registro_tarjeta",0);        
        boolean cambioDeTarjeta  = ParamUtil.getBoolean(renderRequest, "cambioDeTarjeta",false);
        List<TarjetaAcceso> historicoTarjetas = new ArrayList<TarjetaAcceso>();
        
        TarjetaAcceso tarjetaAcceso =null;
		
		if(!StringUtils.checkEmpty(cmd)){
			
	        if(cmd.equals(Constants.ADD) ){ // prepara un contacto en blanco (vacio)
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				renderRequest.removeAttribute(WebKeysRrhh.TARJETA_ACCESO_EN_EDICION);	
				renderRequest.removeAttribute(WebKeysRrhh.TARJETAS_HISTORICO_PERSONA);
				
				renderRequest.setAttribute(Constants.CMD,Constants.ADD);
			}
			
			if(cmd.equals(Constants.SAVE) ){ 
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				tarjetaAcceso =getTarjetaFromRequest(renderRequest );
				idRegTarjeta  = TarjetasServiceUtil.insertar(tarjetaAcceso, usuario) ;
				tarjetaAcceso.setId(idRegTarjeta);
				historicoTarjetas.addAll(TarjetasServiceUtil.getHistoricoTarjetaEmpleado(tarjetaAcceso.getLegajo() ));
				
				 _log.debug("Usuario: " + usuario.getScreenName() 
							+ " cmd: " + cmd 
							+ " id tarj: " + idRegTarjeta);
	
				 SessionMessages.add(renderRequest, "request_processed", ParamUtil.getString(renderRequest, "successMessage"));
				 
				 renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
	
			}
			
			if(cmd.equals(Constants.VIEW) ){ 
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
	
				tarjetaAcceso = TarjetasServiceUtil.getTarjetaAcceso(idRegTarjeta) ;
	
				renderRequest.setAttribute(Constants.CMD, Constants.VIEW);
	
			}
			
			if(cmd.equals(Constants.EDIT )){
				
				tarjetaAcceso = TarjetasServiceUtil.getTarjetaAcceso(idRegTarjeta) ;
				
				historicoTarjetas.addAll(TarjetasServiceUtil.getHistoricoTarjetaEmpleado(tarjetaAcceso.getLegajo() ));
				
				renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
		
			}
	
			if(cmd.equals(Constants.UPDATE) ){ 
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
	
				tarjetaAcceso =getTarjetaFromRequest(renderRequest );
				
				if (cambioDeTarjeta){
					idRegTarjeta  = TarjetasServiceUtil.updatePorCambioDeTarjeta(tarjetaAcceso, usuario) ;						
				}else{
					TarjetasServiceUtil.update(tarjetaAcceso, usuario);						
				}
				tarjetaAcceso = TarjetasServiceUtil.getTarjetaAcceso(tarjetaAcceso.getId()) ;	
				
				SessionMessages.add(renderRequest, "request_processed", ParamUtil.getString(renderRequest, "successMessage"));
				 
				renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
	
			}
			
			if(cmd.equals(Constants.DELETE)){
				TarjetasServiceUtil.borrar(idRegTarjeta, usuario);
				renderRequest.setAttribute("tabs1", "tarjetas-de-personas");
			    return mapping.findForward("portlet.rrhh.view");
		    }
		
			renderRequest.setAttribute(WebKeysRrhh.TARJETA_ACCESO_EN_EDICION, tarjetaAcceso  );	
			renderRequest.setAttribute(WebKeysRrhh.TARJETAS_HISTORICO_PERSONA , historicoTarjetas );

		}else{
			// 
		}
		return mapping.findForward(getForward(renderRequest,"portlet.rrhh.editar.tarjetas"));		                 
	}	

	

	private TarjetaAcceso getTarjetaFromRequest(RenderRequest req) {		
		
		int idTarj = ParamUtil.getInteger(req, "idTarjeta", 0); 
		String piso = ParamUtil.getString(req,"piso");
		String sector = ParamUtil.getString(req,"sector");
		String entidad = ParamUtil.getString(req,"entidad");
		int legajo = ParamUtil.getInteger(req,"legajo");
		int nrotarjeta = ParamUtil.getInteger(req,"nrotarjeta");
		Double horasJornada= ParamUtil.getDouble(req,"horas_jornada");
		String nombre = ParamUtil.getString(req,"nombre");
		String apellido = ParamUtil.getString(req,"apellido");
		
		TarjetaAcceso tarjeta= new TarjetaAcceso();
		tarjeta.setId(idTarj);
		tarjeta.setId_tarjeta_acceso(nrotarjeta);
		tarjeta.setPiso(piso);
		tarjeta.setSector(sector.equalsIgnoreCase("SELECCIONE")?"":sector);
		tarjeta.setEntidad(entidad.equalsIgnoreCase("SELECCIONE")?"":entidad);
		tarjeta.setLegajo(legajo);
		tarjeta.setHoras_jornada(horasJornada);
		tarjeta.setApellido(apellido);
		tarjeta.setNombre(nombre);

		return tarjeta ;
	}

}
