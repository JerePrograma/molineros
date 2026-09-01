package ar.com.ospim.prestadores.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.exception.LugarAtencionPrestadorException;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.TelefonoPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaLugarAtencionAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaLugarAtencionAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		_log.debug("entre x aca");
		
	}
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		List<PrestadorLugarAtencion> lugaresAtencion = null;
		PrestadorLugarAtencion pla = null;
		
		lugaresAtencion = (ArrayList<PrestadorLugarAtencion>) session.getAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION);
		
		if(cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE) ){
				
			pla = getLugarAtencionCompletoFromRequest(renderRequest, session);
			
			_log.debug(cmd.equals(Constants.ADD)?"Agregar":"Actualizar" + " Lugar Atención: " + pla.toString());
			
			if(lugaresAtencion == null){
				lugaresAtencion = new ArrayList<PrestadorLugarAtencion>();
			}
			// quitamos el item que es esta editando, reemplazandolo por el obtenido por request,
			// para que no nos valide duplicado
			if(cmd.equals(Constants.UPDATE)){ 
				int pos = lugaresAtencion.indexOf(pla);
				lugaresAtencion.remove(pos);
			}
			
			boolean validaLugarAt = true;
			try{
				validaLugarAt = validaLugarAtRepetido(pla, (ArrayList<PrestadorLugarAtencion>) lugaresAtencion);
				
				if(validaLugarAt){
					if(cmd.equals(Constants.ADD) || pla.getId_domicilio() < 0){
						pla.setEstado(PrestadorLugarAtencion.ESTADOS.NUEVO);
					}else{
						pla.setEstado(PrestadorLugarAtencion.ESTADOS.MODIF);
					}
					lugaresAtencion.add(pla);
				}
				
			}catch (LugarAtencionPrestadorException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}	
			
			renderRequest.setAttribute(Constants.CMD, cmd);
			
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
			
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION);

		}
		if(cmd.equals(Constants.EDIT)){
			int idPrest = ParamUtil.getInteger(renderRequest, "prestador_id");
			int idDom = ParamUtil.getInteger(renderRequest, "domicilio_id");
			
			PrestadorLugarAtencion plaAux = new PrestadorLugarAtencion();
			plaAux.setId_domicilio(idDom);
			plaAux.setId_prestador(idPrest);
			
			int pos = lugaresAtencion.indexOf(plaAux);
			plaAux = lugaresAtencion.get(pos);

			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
			
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION, plaAux);
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, plaAux.getTelefonos());
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, plaAux.getContactosElectronicos());
			
//			renderRequest.setAttribute("tab",  "lugar_atencion");
			renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
			
//			return mapping.findForward(getForward(renderRequest,
//					"portlet.liquidaciones.lugar_atencion.prestador"));
		}
		
		if(cmd.equals(Constants.DELETE)){
			int idPrest = ParamUtil.getInteger(renderRequest, "prestador_id");
			int idDom = ParamUtil.getInteger(renderRequest, "domicilio_id");
			
			PrestadorLugarAtencion plaAux = new PrestadorLugarAtencion();
			plaAux.setId_domicilio(idDom);
			plaAux.setId_prestador(idPrest);
			
			int pos = lugaresAtencion.indexOf(plaAux);
			plaAux = lugaresAtencion.get(pos);
			
			if(plaAux.getEstado()==null){ // esta matricula esta en BD
				plaAux.setEstado(PrestadorLugarAtencion.ESTADOS.BAJA);
				plaAux.setBajaFecha(new Date());
			}else{
				lugaresAtencion.remove(pos);
			}
			
			renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
		}
		
		if(cmd.equals(Constants.RESET)){ // limpia el lugar de at en edicion
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
			
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION);
			
			renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
		}
		
		if(cmd.equals(Constants.COPY)){ // copia el lugar de at del prestador indirecto
			
			String cmdEnCurso = ParamUtil.getString(renderRequest, "accionEnCurso"); 
			
			int idPrest = ParamUtil.getInteger(renderRequest, "prestador_id");
			int idDom = ParamUtil.getInteger(renderRequest, "domicilio_id");
			
			PrestadorLugarAtencion lugarAtIndirecto = null; 
			PrestadorLugarAtencion aux = new PrestadorLugarAtencion();
			aux.setId_domicilio(idDom);
			aux.setId_prestador(idPrest);
			
			List<PrestadorLugarAtencion> lugaresPrestIndirecto = PrestadorServiceUtil.getInstance().getLugaresAtencionDelPrestador(idPrest);
			
			int pos = lugaresPrestIndirecto.indexOf(aux);
			lugarAtIndirecto = lugaresPrestIndirecto.get(pos);
			
			List<TelefonoPrestador> telefonos = PrestadorServiceUtil.getInstance().getTelefonos(idPrest, idDom);
			List<ContactoElectronicoPrestador> contactosE = PrestadorServiceUtil.getInstance().getContactosElectronicos(idPrest, idDom);
			
			/* marcamos todos los telefonos y contactos de lugar at. indirecto como indirectos, porque
			 * son directos para el prestador que los posee pero indirectos para el prestador que utiliza ese lugar de at.*/
			
			for (Iterator iterator = contactosE.iterator(); iterator.hasNext();) {
				ContactoElectronicoPrestador contactoElectronicoPrestador = (ContactoElectronicoPrestador) iterator.next();
				contactoElectronicoPrestador.setPropio("I");
			}
			for (Iterator iterator = telefonos.iterator(); iterator.hasNext();) {
				TelefonoPrestador telefonoPrestador = (TelefonoPrestador) iterator.next();
				telefonoPrestador.setPropio("I");
			}
			/* fin marca*/
			lugarAtIndirecto.setTelefonos(telefonos);
			lugarAtIndirecto.setContactosElectronicos(contactosE);
			
//			forzamos Factura = INDIRECTO, y el ID Prestador porque estamos seleccionando un lugar de otro prestador
			lugarAtIndirecto.setFactura("INDIRECTO");
			lugarAtIndirecto.setIdPrestadorAtencion(idPrest);
			
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
			
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION);
			
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION, lugarAtIndirecto);
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, lugarAtIndirecto.getTelefonos());
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, lugarAtIndirecto.getContactosElectronicos());
			
			session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION, lugarAtIndirecto);
			
//			renderRequest.setAttribute("tab",  "lugar_atencion");
			renderRequest.setAttribute(Constants.CMD, cmdEnCurso);
					
		}
		
		//pongo la lista en session
		session.removeAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION);

		session.setAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION, lugaresAtencion);
		
		renderRequest.setAttribute("tab",  "lugar_atencion");
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lugar_atencion.prestador"));
	}
	
	private boolean validaLugarAtRepetido(PrestadorLugarAtencion pla, ArrayList<PrestadorLugarAtencion> listaLugares) 
							throws LugarAtencionPrestadorException{
		
		boolean result = true;
		
		for (Iterator<PrestadorLugarAtencion> iterator = listaLugares.iterator(); iterator.hasNext();) {
			PrestadorLugarAtencion _plat =  iterator.next();

			if(_plat.getNombre().equals(pla.getNombre())){
				result = false;
				throw new LugarAtencionPrestadorException();
			}
		}

		return result;
	}
		
	private PrestadorLugarAtencion getLugarAtencionCompletoFromRequest(RenderRequest renderRequest, HttpSession session){
		
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

		PrestadorLugarAtencion pla= null;
		
		String factura = ParamUtil.getString(renderRequest, "lugarat_factura", "INDIRECTO");
		String nombreLugarAt = ParamUtil.getString(renderRequest, "lugarat_nombre");
		String autoridadHabilitacion = ParamUtil.getString(renderRequest, "lugarat_aut_habilitacion","");
		int numeroHabilitacion = ParamUtil.getInteger(renderRequest, "lugarat_nro_habilitacion",0);
		
		String vigenciaDesdeDia = ParamUtil.getString(renderRequest,"laVigenteDesdeFechaDia");
		String vigenciaDesdeMes = ParamUtil.getString(renderRequest,"laVigenteDesdeFechaMes");
		String vigenciaDesdeAnio = ParamUtil.getString(renderRequest,"laVigenteDesdeFechaAnio");
		Date vigenciaDesdeHabilitacion = null;
		try {
			vigenciaDesdeHabilitacion = formatoDePeriodo.parse(vigenciaDesdeDia + "/"
					+ (Integer.parseInt(vigenciaDesdeMes) + 1) + "/"
					+ vigenciaDesdeAnio);
		} catch (Exception e) {
			vigenciaDesdeHabilitacion = null;
		}
		String vigenciaHastaDia = ParamUtil.getString(renderRequest,"laVigenteHastaFechaDia");
		String vigenciaHastaMes = ParamUtil.getString(renderRequest,"laVigenteHastaFechaMes");
		String vigenciaHastaAnio = ParamUtil.getString(renderRequest,"laVigenteHastaFechaAnio");
		Date vigenciaHastaHabilitacion = null;
		try {
			vigenciaHastaHabilitacion = formatoDePeriodo.parse(vigenciaHastaDia + "/"
					+ (Integer.parseInt(vigenciaHastaMes) + 1) + "/"
					+ vigenciaHastaAnio);
		} catch (Exception e) {
			vigenciaHastaHabilitacion = null;
		}
//		String fechaDesdeHabFinal = ParamUtil.getString(renderRequest,"fechaDesdeHabil", null);		
//		String fechaHastaHabFinal = ParamUtil.getString(renderRequest,"fechaHastaHabil", null);
		int idPrestadorLugarAt = ParamUtil.getInteger(renderRequest, "id_prestador",0);  // OJO que es el prestador de Lugar de Atencion
		boolean presentoCopiaHabilitacion = ParamUtil.getBoolean(renderRequest, "lugarat_pres_copia_habilitacion");
		int idProvincia = ParamUtil.getInteger(renderRequest, "provincia");
		int idLocalidad = ParamUtil.getInteger(renderRequest, "localidad");
		String calle = ParamUtil.getString(renderRequest,"calle", "");
		String numero = ParamUtil.getString(renderRequest,"numero", "");
		String piso = ParamUtil.getString(renderRequest,"piso", "");
		String depto = ParamUtil.getString(renderRequest,"dpto", "");
		String codigoPostal = ParamUtil.getString(renderRequest,"cod_postal", "0");
		String barrio = ParamUtil.getString(renderRequest,"barrio", "");
		String categoriaProfesional = ParamUtil.getString(renderRequest,"cat_prof", null);
		String registroHistoriaClinica = ParamUtil.getString(renderRequest,"reg_histo_clinica", null);
		
//		solo si edito un lugar de at, tendre el domicilio con id, sino le genero uno aleatorio para el borrado de lugar at
//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}
		Integer idDomicilio = ParamUtil.getInteger(renderRequest, "id_domicilio",idAux); 
		// idDomicilio ser 0 si es nuevo y factura = DIRECTO, o
		// un nro positivo edicion y factura = DIRECTO, o
		// un nro positivo si es nuevo o edicion y factura = INDIRECTO
		
		int idPrestador = ParamUtil.getInteger(renderRequest, "id_prestador_prestador",idAux);
		
		List<TelefonoPrestador> telefonos = (ArrayList<TelefonoPrestador>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
		List<ContactoElectronicoPrestador> contactosElectronicos = (ArrayList<ContactoElectronicoPrestador>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
		
		Domicilio dom = new Domicilio();
		
		if(factura.equalsIgnoreCase("INDIRECTO")){
			pla = (PrestadorLugarAtencion) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION); 
			// En edicion esta el lugar de atencion indirecto pero camuflado como LUGAR_ATENCION_PRESTADOR_EN_EDICION
			// tambien existe esta LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION pero se usa solo al seleccionar un lugar at. desde busqueda prestador indirecto.
//			mas abajo le quito el id_prestador indirecto
		}else{
			dom.setBarrio(barrio.toUpperCase());
			dom.setCalle(calle.toUpperCase());
			dom.setDepto(depto.toUpperCase());
			dom.setLocalidadId(idLocalidad);
			dom.setNumero(numero);
			dom.setPiso(piso);
			dom.setPostal_codi(codigoPostal);
			dom.setProvinciaId(idProvincia);
			
			//Para que Provincia y Localidad tengan descripción antes de guardar en BD
		    dom.setProvincia(
		        TraeListasServiceUtil.getProvincia(idProvincia, renderRequest)
		    );

		    dom.setLocalidad(
		        TraeListasServiceUtil.getLocalidad(idLocalidad, renderRequest)
		    );

			dom.setId_domicilio(idDomicilio);
		
			pla = new PrestadorLugarAtencion();
			pla.setAutoridadHabilitacion(autoridadHabilitacion.toUpperCase());
			pla.setCategoriaProfesional(categoriaProfesional);
			pla.setContactosElectronicos(contactosElectronicos);
			pla.setDomicilio(dom);
			pla.setFactura(factura);
			pla.setIdPrestadorAtencion(idPrestadorLugarAt);
			pla.setNombre(nombreLugarAt.toUpperCase());
			pla.setNumeroHabilitacion(numeroHabilitacion);
			pla.setPresentaCopiaHabilitacion(presentoCopiaHabilitacion);
			pla.setRegistroHistoriaClinica(registroHistoriaClinica);
			pla.setTelefonos(telefonos);
			pla.setVigenciaDesdeHabilitacion(vigenciaDesdeHabilitacion);
			pla.setVigenciaHastaHabilitacion(vigenciaHastaHabilitacion);
			pla.setVigen_desde(new Date());
		}
		
		pla.setId_domicilio(idDomicilio);
		pla.setId_prestador(idPrestador);
		
		return pla;
	}
}
