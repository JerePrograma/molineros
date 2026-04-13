package ar.com.ospim.liquidaciones.administracion.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author Gustavo Fernandez
 */

public class EditarEspecialidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(EditarEspecialidadAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
//		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
//			
//		int profesion = ParamUtil.getInteger(renderRequest, "profesionN");
//		int especialidad = ParamUtil.getInteger(renderRequest, "especialidadN");
//		int subEspecialidad = ParamUtil.getInteger(renderRequest, "subEspecialidadN");		
//		String matCategoria = ParamUtil.getString(renderRequest, "matCategoriaN");
//		boolean tituloProfesional =ParamUtil.getBoolean(renderRequest, "tituloProfesionalN"); 
//		boolean tituloEspecialista =ParamUtil.getBoolean(renderRequest, "tituloEspecialistaN"); 
//		
//		int bajaEnBase=2;
//		int idEspe = ParamUtil.getInteger(renderRequest, "idEspe");
//
//		try {			
//			//traigo las especialidades cargada si es que la hay
//			@SuppressWarnings("unchecked")
//			List<EspecialidadPrestador> listaVieja = (ArrayList<EspecialidadPrestador>) session.getAttribute(WebKeysLiquidaciones.ESPECIALIDADES_PRESTADOR_SESSION);
//			EspecialidadPrestador especialidadesPrestador = new EspecialidadPrestador();
//			
//			//creo una lista con los datos que pasa el usuario
//			ArrayList<EspecialidadPrestador> listaEspecialidadesPrestador = new ArrayList <EspecialidadPrestador>();
//		
//			for (int i=0; i<listaVieja.size(); i++) {	    
//		 		EspecialidadPrestador espe = (EspecialidadPrestador) listaVieja.get(i);
//		 		if(espe.getIdEspe()==idEspe){
//		 			listaVieja.remove(i);	
//                }
//			}
//			
//				try{
//					listaEspecialidadesPrestador.add(especialidadesPrestador);
//					/*especialidadesPrestador.setProfesion(profesion);
//					especialidadesPrestador.setEspecialidad(especialidad);
//					especialidadesPrestador.setSubEspecialidad(subEspecialidad);*/
//					especialidadesPrestador.setBajaEnBase(bajaEnBase);
//					especialidadesPrestador.setMatCategoria(matCategoria);
//					especialidadesPrestador.setTituloProfesional(tituloProfesional);
//					especialidadesPrestador.setTituloEspecialista(tituloEspecialista);
//					especialidadesPrestador.setIdEspe(idEspe);
//					
//				}catch (Exception e) {
//					_log.error("Error al editar especialidad del prestador a la lista", e);
//					return null;
//				}		
//				// si existe una listaVieja en session la agrego a la nuevaLista
//				if (listaVieja!=null){
//					listaEspecialidadesPrestador.addAll(listaVieja);
//				}
//			
//			//pongo la lista en session
//			session.removeAttribute(WebKeysLiquidaciones.ESPECIALIDADES_PRESTADOR_SESSION);
//			session.setAttribute(WebKeysLiquidaciones.ESPECIALIDADES_PRESTADOR_SESSION, listaEspecialidadesPrestador);
//		} 
//		catch (Exception e) {
//			_log.error(e);
//		}	
		
		return mapping.findForward("portlet.liquidaciones.especialidad.prestador");
	}
		
}