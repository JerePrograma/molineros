package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaLugarAtContactosAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaLugarAtContactosAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
//		int idContacto = ParamUtil.getInteger(renderRequest, "idContactoE",0);
		String tipoContacto = ParamUtil.getString(renderRequest, "tipoContacto");
		String descripcion = ParamUtil.getString(renderRequest, "descripcion");
		String observaciones = ParamUtil.getString(renderRequest, "obs");
		String propio = ParamUtil.getString(renderRequest, "propio");
		
//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}
		
		ContactoElectronicoPrestador contactoE  = new ContactoElectronicoPrestador();
//		contactoE.setId(idContacto);
		contactoE.setTipo(ContactoElectronico.Tipo.valueOf(tipoContacto));
		contactoE.setContacto(descripcion);
		contactoE.setObservaciones(StringUtils.checkEmpty(observaciones)?"":observaciones );
		contactoE.setPropio(propio);
		contactoE.setEstado(ContactoElectronico.ESTADOS.NUEVO);
		contactoE.setId(idAux);
		
		_log.debug("Agregar Contacto Electronico: " + contactoE.toString());	
		
		List<ContactoElectronicoPrestador> lugarAtContactos = (ArrayList<ContactoElectronicoPrestador>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
		
		if(lugarAtContactos == null){
			lugarAtContactos = new ArrayList<ContactoElectronicoPrestador>();
		}
		
		lugarAtContactos.add(contactoE);
//		boolean validaProfEspSubEsp = true;
//		try{
//			validaProfEspSubEsp = validaTelefonoDepetido(profesion, especialidad, subEspecialidad, (ArrayList<ProfesionPrestador>) profEspecPrestador);
//			
//			if(validaProfEspSubEsp){
//				profEspecPrestador.add(profesion);
//			}
//			
//		}catch (ProfesionEspecialidadSubEspecPrestadorException e) {
//			SessionErrors.add(renderRequest, e.getClass().getName());
//		}		
		
		//pongo la lista en session
		session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, lugarAtContactos);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lugar_at_contactos.prestador"));
	}
	
//	private boolean validaTelefonoDepetido(ProfesionPrestador prof, EspecialidadPrestador esp, SubEspecialidadPrestador subEsp, 
//								ArrayList<ProfesionPrestador> listaProf) throws ProfesionEspecialidadSubEspecPrestadorException{
//		
//		boolean result = true;
//		for (Iterator<ProfesionPrestador> iterator = listaProf.iterator(); iterator.hasNext();) {
//			ProfesionPrestador _profPrest =  iterator.next();
//			EspecialidadPrestador _espePrest = _profPrest.getEspecialidades().get(0);
//			SubEspecialidadPrestador _subEspePrestador = _espePrest.getSubEspecialidades().get(0);
//			
//			if(_profPrest.getIdProfesion() == prof.getIdProfesion() 
//				&& _espePrest.getIdEspecialidad() == esp.getIdEspecialidad()
//				&& _subEspePrestador.getId() == subEsp.getId()){
//				
//				result = false;
//				throw new ProfesionEspecialidadSubEspecPrestadorException();
//			}
//		}
//
//		return result;
//	}
		
}