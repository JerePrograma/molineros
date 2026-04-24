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

import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.TelefonoPrestador;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaLugarAtTelefonosAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaLugarAtTelefonosAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
//		int idTelefono = ParamUtil.getInteger(renderRequest, "idTelefono",0);
		String tipoTel = ParamUtil.getString(renderRequest, "tipoTel");
		String codigoPais = ParamUtil.getString(renderRequest, "codPais");
		String codigoArea = ParamUtil.getString(renderRequest, "codArea");
		String numeroTel = ParamUtil.getString(renderRequest, "numero");
		String extension = ParamUtil.getString(renderRequest, "exten");
		String observaciones = ParamUtil.getString(renderRequest, "obs");
		String propio = ParamUtil.getString(renderRequest, "propio");
		
//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}
		
		TelefonoPrestador tel  = new TelefonoPrestador();
		tel.setTipo(tipoTel);
		tel.setCodigoPais(codigoPais);
		tel.setCodigoArea(codigoArea);
		tel.setNumero(numeroTel);
		tel.setExtension(extension);
		tel.setObservaciones(StringUtils.checkEmpty(observaciones)?"":observaciones);
		tel.setPropio(propio);
		tel.setEstado(Telefono.ESTADOS.NUEVO);
		tel.setId(idAux);
		
		_log.debug("Agregar Telefono: " + tel.toString());	
		
		List<TelefonoPrestador> lugarAtTelefonos = (ArrayList<TelefonoPrestador>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
		
		if(lugarAtTelefonos == null){
			lugarAtTelefonos = new ArrayList<TelefonoPrestador>();
		}
		
		lugarAtTelefonos.add(tel);
//		boolean validaProfEspSubEsp = true;
//		try{
//			validaProfEspSubEsp = validaTelefonoRepetido(profesion, especialidad, subEspecialidad, (ArrayList<ProfesionPrestador>) profEspecPrestador);
//			
//			if(validaProfEspSubEsp){
//				profEspecPrestador.add(profesion);
//			}
//			
//		}catch (ProfesionEspecialidadSubEspecPrestadorException e) {
//			SessionErrors.add(renderRequest, e.getClass().getName());
//		}		
		
		//pongo la lista en session
		session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, lugarAtTelefonos);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lugar_at_telefonos.prestador"));
	}
	
//	private boolean validaTelefonoRepetido(ProfesionPrestador prof, EspecialidadPrestador esp, SubEspecialidadPrestador subEsp, 
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