package ar.com.ospim.liquidaciones.administracion.prestadores.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.PlanPrestadorDuplicadoException;
import ar.com.ospim.liquidaciones.beans.PrestadorPlan;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaPrestadorPlanesAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaPrestadorPlanesAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		List<Plan> planes = (ArrayList<Plan>) session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION);
		
		List<PrestadorPlan> prestadorPlanes = (ArrayList<PrestadorPlan>) session.getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
		PrestadorPlan prestPlan = null;
		
		if(prestadorPlanes == null){
			prestadorPlanes = new ArrayList<PrestadorPlan>();
		}
		
		int idPrestador = ParamUtil.getInteger(renderRequest, "id_prestador");
		int idPlan = ParamUtil.getInteger(renderRequest, "idPlan");
		String fechaVigenDesdeFinal = ParamUtil.getString(renderRequest,"vigenDesde", null);
		String fechaVigenHastaFinal = ParamUtil.getString(renderRequest,"vigenHasta", null);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = null;
		try {
			fechaDesde = sdf.parse(fechaVigenDesdeFinal);
		} catch (Exception e) {
			fechaDesde = null;
		}		
		Date fechaHasta = null;
		try {
			fechaHasta = sdf.parse(fechaVigenHastaFinal);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		if(idPlan == -1){ // Todos los planes Ospim
			_log.debug("Agregar PrestadorPlan - Todos los planes Ospim -");	
			
//			Iteramos todos los planes Ospim, pero puede ser que la lista de planes del prestador,
//			ya tenga alguno cargado previamente, (puede o no ya estar guardado en la BD)
//			entonces reducimos la lista de planes ospim solo a aquellos que no sean del prestador...
			List<Plan> planesAux = new ArrayList<Plan>();
			for (Iterator<Plan> iterator = planes.iterator(); iterator.hasNext();) {
				 Plan p = iterator.next();
				 planesAux.add(p);
			}
			int pos =0;
			for (Iterator<PrestadorPlan> iterator = prestadorPlanes.iterator(); iterator.hasNext();) {
				PrestadorPlan pp = (PrestadorPlan) iterator.next();
				pos = planesAux.indexOf(pp.getPlan());
				if(pos > -1){
					planesAux.remove(pos);
				}
			}
			for (Iterator<Plan> iterator = planesAux.iterator(); iterator.hasNext();) {
				Plan p = iterator.next();
				prestPlan = CargaPlan(planes, idPrestador, new Plan(p.getId(), p.getDescripcion()), fechaDesde, fechaHasta);	
			
				prestadorPlanes.add(prestPlan);
			}
			
			
		}else{ // de a 1 plan individual
		
			prestPlan = CargaPlan(planes, idPrestador, new Plan(idPlan,""), fechaDesde, fechaHasta);	
	
			boolean validaPrestadorPlan = true;
			try{
				validaPrestadorPlan = validaPrestadorPlanes(prestPlan, (ArrayList<PrestadorPlan>) prestadorPlanes);
				
				if(validaPrestadorPlan){
					prestadorPlanes.add(prestPlan);
				}
				
			}catch (PlanPrestadorDuplicadoException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}		
		}
		//pongo la lista en session
		session.removeAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION, prestadorPlanes);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.plan.prestador"));
	}

	private PrestadorPlan CargaPlan(List<Plan> planes,
			int idPrestador, Plan p, Date fechaDesde, Date fechaHasta) {
//		    me aseguro sea un numero negativo para no confundir con IDs de BD
			Random r = new Random(System.currentTimeMillis());
			int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
			if(idAux > 0){
				idAux = (-1)*idAux;
			}
			
			PrestadorPlan prestPlan = new PrestadorPlan(idPrestador, p.getId(), fechaDesde, fechaHasta);
			prestPlan.setEstado(PrestadorPlan.ESTADOS.NUEVO);
			prestPlan.setId(idAux);
			
			int pos = planes.indexOf(p);
//			reemplazo por el objeto de la lista
			p = planes.get(pos);
			prestPlan.setPlan(p);
//			_log.debug("Agregar PrestadorID: " + idPrestador + " Plan: " + prestPlan.toString());
		return prestPlan;
	}
	
	private boolean validaPrestadorPlanes(PrestadorPlan pplan, ArrayList<PrestadorPlan> listaPlan) throws PlanPrestadorDuplicadoException{
		
		boolean result = true;
		for (Iterator<PrestadorPlan> iterator = listaPlan.iterator(); iterator.hasNext();) {
			PrestadorPlan _planPrest =  iterator.next();

			if(_planPrest.getId_plan() == pplan.getId_plan()
				&& _planPrest.getVigencia_desde().equals(pplan.getVigencia_desde())){
				
				result = false;
				throw new PlanPrestadorDuplicadoException();
			}
		}

		return result;
	}
		
}