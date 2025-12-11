package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * BajaAfiliadoPlanReglasAction: intentamos tener validado la fecha de baja y a partir del motivo de baja, evaluamos las reglas
 * 
 * @author sergio
 *
 */

public class BajaAfiliadoPlanReglasAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BajaAfiliadoPlanReglasAction.class);
	private PlanServiceUtil planService = new PlanServiceUtil();
	
	
//	public void processAction(ActionMapping mapping, ActionForm form,
//			PortletConfig portletConfig, ActionRequest actionRequest,
//			ActionResponse actionResponse) throws Exception {
//		setForward(actionRequest, "portlet.afiliados.result.search");
//
//	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		session.removeAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION);
		
		List<MotivoBaja> motivosDeBaja = (ArrayList<MotivoBaja>) session.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION);	
				
		Map<Integer,MotivoBaja> motDeBaja = new HashMap<Integer, MotivoBaja>(); 
		for (Iterator<MotivoBaja> iterator = motivosDeBaja.iterator(); iterator.hasNext();) {
			MotivoBaja mb = iterator.next();
			motDeBaja.put(mb.getId_motivo_baja(), mb);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String cuil = null;
		String bajaFecha = null;
		int idMotBaja = 0;
		int idPlanActual = 0;
		boolean esBajaCascada = false;
		Plan planActual, planVig; 		
		AfiPlan planVigencia = new AfiPlan(); // inicializamos
		MotivoBaja motivoBaja = null;
		Integer idPlanBase = 0;
		Date fechaVigDesde;
		Date fechaVigHasta = null;
		Calendar aux = Calendar.getInstance();
		int mesesabaja=0;
		
//		TODO ver como evaluar categoria = 11 - "RELACION DE DEPENDENCIA"
		
		try {
			cuil = renderRequest.getParameter("cuil").trim();
			bajaFecha = renderRequest.getParameter("bajaFecha").trim();
			idMotBaja = Integer.parseInt(renderRequest.getParameter("idMotivoBaja").trim()); 
			idPlanActual = Integer.parseInt(renderRequest.getParameter("idPlanActual").trim());
			esBajaCascada = ParamUtil.getBoolean(renderRequest,"esBajaCascada");
			
//			 siempre le calculamos la fecha de ingreso como el proximo dia al cambio de plan
//			 no consideramos hacer solo baja de plan
			
//			seteamos 1 dia desp de la baja fecha
			fechaVigDesde = sdf.parse(bajaFecha);
			aux.setTime(fechaVigDesde);
			aux.add(Calendar.DATE, 1);
			fechaVigDesde = aux.getTime();
			
			planVigencia.setVigenDesde(fechaVigDesde);
			planVigencia.setEstado(AfiPlan.ESTADOS.ALTA);
			
//			fin seteo fecha vigencia desde
			if(idMotBaja > 0 && !ar.com.ospim.util.StringUtils.checkEmpty(bajaFecha) ){
				motivoBaja = motDeBaja.get(idMotBaja);
				fechaVigHasta = sdf.parse(bajaFecha);
			}	
//			fin seteo motivo y fecha de baja
			
//			analizo si es falecimiento del titular, no propago los 3 meses si esta solo en el grupo familiar
			boolean hayRestodeGrupoFliar = false;
			
			if(idMotBaja == 2){ // fallecimiento
				List<Afiliado> afiliadosList= BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(cuil);
				for (Iterator<Afiliado> iterator = afiliadosList.iterator(); iterator.hasNext();) {
					Afiliado a = iterator.next();
					if(a.getInte() != 0 && a.getBaja_fecha() == null){
						hayRestodeGrupoFliar = true;
						break;
					}else if(a.getInte() != 0 && a.getBaja_fecha().after(new Date())){
						hayRestodeGrupoFliar = true;
						break;
					}
				}
			}
			
//			Regla 1: si el motivo de baja es: 1-"RENUNCIA", 2-"FALLECIMIENTO", 3-"DESPIDO", 21-"DESEMPLEO"
//			Se debe bajar los planes: 1-"INTEGRAL",2-"TOTAL" a su respectivo plan_base ej, pasamos Integral y Total a 19-Cobertura-Usufructo
			if( (idPlanActual == 1 || idPlanActual == 2) 
				 && (idMotBaja == 1 || (idMotBaja == 2 && hayRestodeGrupoFliar) || idMotBaja == 3 || idMotBaja == 21 ) 
			  ){
//				recuperamos plan para buscar su plan_base
				planActual = planService.buscaPlanPorId(idPlanActual);
				idPlanBase = planActual.getId_plan_base();
//				si tiene plan base, procedemos a generar el plan que quedará vigente tras la baja
				if(idPlanBase != null && idPlanBase > 0){
					planVig = planService.buscaPlanPorId(idPlanBase); 
 
					// si tiene meses de prorroga la baja se los seteamos
					motivoBaja = motDeBaja.get(idMotBaja);
					fechaVigHasta = sdf.parse(bajaFecha);
					aux.setTime(fechaVigHasta);
					if(motivoBaja.getMeses_a_baja() > 0){
						aux.add(Calendar.MONTH, motivoBaja.getMeses_a_baja());
					}
//					seteamos el plan que permanecerá vigente con baja futura
					
					planVigencia.setCuil_titular(cuil);
					planVigencia.setPlan(planVig);
					planVigencia.setId_plan_omint(planVig.getId_plan_omint());
					planVigencia.setVigenHasta(aux.getTime());
					planVigencia.setMotivoBaja(motivoBaja);
				}
				
			}
			
//			Regla 2: si el motivo de baja es: 1-"RENUNCIA", 2-"FALLECIMIENTO", 3-"DESPIDO", 21-"DESEMPLEO"
//			Se debe propagar cobertura por aporte ospim los n meses del motivo de baja, es decir, se poner fecha de baja diferida. 
//			Ej Fecha Renuncia 1/05/2014, la Vigen_hasta del plan será 1/08/2014
//			Planes: 3 - "COBERTURA", 5 - "OSPIM - AMTADH", 9 - "COBERTURA TOTAL O", 19 - "COBERTURA - USUFRUCTO", 22 - "COBERTURA TOTAL OA" 
			if( (idPlanActual == 3 || idPlanActual == 5 || idPlanActual == 9 || idPlanActual == 19 || idPlanActual == 22) 
				 && (idMotBaja == 1 || (idMotBaja == 2 && hayRestodeGrupoFliar) || idMotBaja == 3 || idMotBaja == 21 ) 
			  ){
				// si tiene meses de prorroga la baja se los seteamos
				motivoBaja = motDeBaja.get(idMotBaja);
				fechaVigHasta = sdf.parse(bajaFecha);
				aux.setTime(fechaVigHasta);
				if(motivoBaja.getMeses_a_baja() > 0){
					aux.add(Calendar.MONTH, motivoBaja.getMeses_a_baja());
				}
				fechaVigHasta.setTime(aux.getTimeInMillis()); // esto luego se actualizará al planActual del afiliado que esta en sesion si es baja cascada
			}
			
//			Regla 3: si el motivo de baja es: 1-"RENUNCIA", 2-"FALLECIMIENTO", 3-"DESPIDO", 21-"DESEMPLEO"
//			Se debe propagar cobertura tercerizadora los n meses del motivo de baja, es decir, se poner fecha de baja diferida. 
//			Ej Fecha Renuncia 1/05/2014, la Vigen_hasta del plan será 1/08/2014
//			Planes: 10;"CONSOLIDAR SALUD", 11;"CONSOLIDAR SALUD - SINDICATO", 13;"MEDICINA Y SALUD", 14;"MEDICINA Y SALUD - SINDICATO", 
//			15;"CHIVILCOY", 16;"CHIVILCOY SINDICATO", 23;"OMINT SALUD", 24;"CEMIC SALUD", 25;"GALENO SALUD", 26;"EN TRAMITE", 
//			28;"OMINT - SINDICATO", 31;"CONSOLIDAR SALUD USUFRUCTO", 32;"OMINT USUFRUCTO", 33;"PREVENCION SALUD"

			if( (idPlanActual == 10 || idPlanActual == 11 || idPlanActual == 13 || idPlanActual == 14 || idPlanActual == 15 ||
					idPlanActual == 16 || idPlanActual == 23 || idPlanActual == 24 || idPlanActual == 25 || idPlanActual == 26 ||
						idPlanActual == 28 || idPlanActual == 31 || idPlanActual == 32 || idPlanActual == 33 ) 
				 && (idMotBaja == 1 || idMotBaja == 2 || idMotBaja == 3 || idMotBaja == 21 ) 
			  ){
				// si tiene meses de prorroga la baja se los seteamos
				motivoBaja = motDeBaja.get(idMotBaja);
				fechaVigHasta = sdf.parse(bajaFecha);
				aux.setTime(fechaVigHasta);
				
				if(motivoBaja.getMeses_a_baja() > 0){
					//Agregado DS 2021-08-19 para que en el caso de no existir grupo familiar no prorrogue la baja
					if(idMotBaja==2 && !hayRestodeGrupoFliar) {
						mesesabaja=0;
					}else {
						mesesabaja=motivoBaja.getMeses_a_baja();
					}
					//Fin Agregado
					aux.add(Calendar.MONTH, mesesabaja);
				}
				fechaVigHasta.setTime(aux.getTimeInMillis()); // esto luego se actualizará al planActual del afiliado que esta en sesion si es baja cascada
			}
			
////////////			
//Regla 4 Si la baja es anterior a la fecha  de vigencia del último plan  - DS 31/10/2022
////////////	
			Afiliado afiAux=(Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			if(DateUtils.compararFechasTruncarEnDia(afiAux.getAfiPlan().getVigenDesde(),sdf.parse(bajaFecha))>0){
				List<AfiPlan> lista = null;
				lista = PlanServiceUtil.getInstance().historicoPlanyAportes(afiAux.getCuil_titular());
				
				AfiPlan apn = null;
				
				for(AfiPlan ap:lista) {
					if( (DateUtils.compararFechasTruncarEnDia(sdf.parse(bajaFecha),ap.getVigenDesde())>0  &&
						ap.getVigenHasta()==null) ||
						DateUtils.compararFechasTruncarEnDia(sdf.parse(bajaFecha),ap.getVigenDesde())>0  &&
						DateUtils.compararFechasTruncarEnDia(sdf.parse(bajaFecha),ap.getVigenHasta())<=0) {
						apn = new AfiPlan();
						apn.setMotivoBaja(motivoBaja);
						apn.setVigenHasta(fechaVigHasta); 
						apn.setEstado(AfiPlan.ESTADOS.MODIFICADO);
						apn.setVigenDesde(ap.getVigenDesde());
						apn.setPlan(ap.getPlan());
						apn.setId_plan_omint(ap.getId_plan_omint());
					}
				}
				
				if(apn!=null) {
					Afiliado afiliado=(Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
					afiliado.setAfiPlan(apn);
					session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
					session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
				}
			}
// FIN REgla 4				
				
				
//		si es baja cascada debemos propagar al plan vigente y
//		al motivo baja del plan vigente los datos de baja de la situ laboral	
		if(esBajaCascada){
			
			Afiliado afiliado=(Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

			AfiPlan afiPlan = afiliado.getAfiPlan();
			afiPlan.setMotivoBaja(motivoBaja);
			afiPlan.setVigenHasta(fechaVigHasta); 
			afiPlan.setEstado(AfiPlan.ESTADOS.MODIFICADO);
			
			afiliado.setAfiPlan(afiPlan);
			session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
		}	
			
//		 PROXIMAS REGLAS

		} catch (Exception e) {
			_log.error(e);
			return mapping.findForward("portlet.afiliados.error");
		}
		List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().consultaUltimosComponentesPlanVigente(cuil);

		//almaceno la lista en sesion
		renderRequest.setAttribute("IdsSocio", afiAportes); 
		
		session.setAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION, planVigencia);
		
		renderRequest.setAttribute("baja_cascada1", String.valueOf(esBajaCascada)); 
		// lo necesito porque la propagacion de esta marca la hace afiliados/grabar_situ_laboral despues de actualizar el jsp del plan, y no tomaba el cambio.
	
		
		if(esBajaCascada){
			return mapping.findForward("portlet.afiliados.afi_plan");
		}
		
		return mapping.findForward("portlet.afiliados.nuevo_afi_plan");
	}

}