package ar.com.ospim.liquidaciones.administracion.prestadores.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.ProfesionEspecialidadSubEspecPrestadorException;
import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.ProfesionPrestador;
import ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaProfEspecialidadesAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaProfEspecialidadesAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idProfesion = ParamUtil.getInteger(renderRequest, "idProfesion");
		int idEspecialidad = ParamUtil.getInteger(renderRequest, "idEspecialidad",0);
		int idSubEspecialidad = ParamUtil.getInteger(renderRequest, "idSubEspecialidad",0);

		String profesionDesc = ParamUtil.getString(renderRequest, "profesion");
		String especialidadDesc = ParamUtil.getString(renderRequest, "especialidad");
		String subEspecialidadDesc = ParamUtil.getString(renderRequest, "subEspecialidad");

		String categoriaProfOspim = ParamUtil.getString(renderRequest, "categoriaProfOspim");
		boolean tituloProfesional = ParamUtil.getBoolean(renderRequest, "tituloProfesional"); 
		boolean tituloEspecialista = ParamUtil.getBoolean(renderRequest, "tituloEspecialista");

//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}
		
		ProfesionPrestador profesion = new ProfesionPrestador(idProfesion, profesionDesc, tituloProfesional, categoriaProfOspim);
		profesion.setEstado(ProfesionPrestador.ESTADOS.NUEVO);
		profesion.setIdPrestProf(idAux);
		_log.debug("Agregar Profesion: " + profesion.toString());	
		
		EspecialidadPrestador especialidad = new EspecialidadPrestador(idEspecialidad, especialidadDesc, tituloEspecialista, idProfesion);
		especialidad.setEstado(EspecialidadPrestador.ESTADOS.NUEVO);
		_log.debug("Agregar Especialidad: " + especialidad.toString());	
		
		SubEspecialidadPrestador subEspecialidad = null;
		if(idSubEspecialidad != 0){
			subEspecialidad = new SubEspecialidadPrestador(idSubEspecialidad, subEspecialidadDesc, idEspecialidad);
			subEspecialidad.setEstado(SubEspecialidadPrestador.ESTADOS.NUEVO);
			
			_log.debug("Agregar Sub-Especialidad: " + subEspecialidad.toString());
		}
		
		List<ProfesionPrestador> profEspecPrestador = (ArrayList<ProfesionPrestador>) session.getAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
		
		if(profEspecPrestador == null){
			profEspecPrestador = new ArrayList<ProfesionPrestador>();
		}
		
		ArrayList<EspecialidadPrestador> especialidades = new ArrayList<EspecialidadPrestador>();
		ArrayList<SubEspecialidadPrestador> subEspecialidades = new ArrayList<SubEspecialidadPrestador>();

		if(subEspecialidad!=null){subEspecialidades.add(subEspecialidad); };
		
		especialidad.setSubEspecialidades(subEspecialidades);
		especialidades.add(especialidad);
		profesion.setEspecialidades(especialidades);
		
		boolean validaProfEspSubEsp = true;
		try{
			validaProfEspSubEsp = validaProfesionEspecialidad(profesion, especialidad, subEspecialidad, (ArrayList<ProfesionPrestador>) profEspecPrestador);
			
			if(validaProfEspSubEsp){
				profEspecPrestador.add(profesion);
			}
			
		}catch (ProfesionEspecialidadSubEspecPrestadorException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}		
		
		//pongo la lista en session
		session.removeAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION, profEspecPrestador);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.profesion.prestador"));
	}
	
	private boolean validaProfesionEspecialidad(ProfesionPrestador prof, EspecialidadPrestador esp, SubEspecialidadPrestador subEsp, 
								ArrayList<ProfesionPrestador> listaProf) throws ProfesionEspecialidadSubEspecPrestadorException{
		
		boolean result = true;
		for (Iterator<ProfesionPrestador> iterator = listaProf.iterator(); iterator.hasNext();) {
			ProfesionPrestador _profPrest =  iterator.next();
			EspecialidadPrestador _espePrest = _profPrest.getEspecialidades().get(0);
			SubEspecialidadPrestador _subEspePrestador = _espePrest.getSubEspecialidades().size()>0?_espePrest.getSubEspecialidades().get(0):null;
			
			if(_profPrest.getIdProfesion() == prof.getIdProfesion() 
				&& _espePrest.getIdEspecialidad() == esp.getIdEspecialidad()
				&& ((_subEspePrestador!=null && subEsp != null && _subEspePrestador.getId() == subEsp.getId()) || true )
				){
				
				result = false;
				throw new ProfesionEspecialidadSubEspecPrestadorException();
			}
		}

		return result;
	}
		
}