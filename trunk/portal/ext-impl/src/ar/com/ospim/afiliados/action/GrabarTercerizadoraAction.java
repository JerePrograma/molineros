package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;
import ar.com.ospim.tercerizadora.services.TercerizadoraFactory;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class GrabarTercerizadoraAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarTercerizadoraAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				renderRequest).getSession();
		List<AfiTercerizadoraServicio> tercerizadoraList = (List<AfiTercerizadoraServicio>) session
				.getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);

		try {
//			String cuil_titular = ParamUtil.getString(renderRequest,
//					"cuil_titular");
//			int inte = ParamUtil.getInteger(renderRequest, "inte");
//			if (null == tercerizadoraList) {
//				tercerizadoraList = TercerizadoraServiceUtil
//						.buscaTercerizadoras(cuil_titular, inte);
//				session.setAttribute(WebKeysAfiliados.TERCERIZADORAS_EN_SESSION,
//						tercerizadoraList);
//			}

			String accion = renderRequest.getParameter("accion");
//			if (accion.trim().equals("bajaTercerizadora")) {
//				bajaTercerizadora(renderRequest, tercerizadoraList);
//			} else if (accion.trim().equals("altaTercerizadora")) {
//				altaTercerizadora(renderRequest, tercerizadoraList);
//			} else 
				
			if (accion.trim().equals("bajaTercerizadoraPorSituLaboral")) {
				bajaTercerizadoraPorBajaSituLaboral(renderRequest,
						tercerizadoraList);
			}else if (accion.trim().equals("ajustaTercAlCambioDePlan")) {
				ajustarTercerizadoraVigente(renderRequest,
						tercerizadoraList);
			}	
		} catch (Exception e) {
			_log.debug("Error al editar/insertar tercerizadora", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		return mapping.findForward("portlet.tercerizadora.result.search");
	}

	private void ajustarTercerizadoraVigente(RenderRequest renderRequest,
			List<AfiTercerizadoraServicio> afiliadoTercerizadoras){
		
		String cuilTitular = ParamUtil.getString(renderRequest,"cuil_titular");
//		Integer inte = ParamUtil.getInteger(renderRequest,"inte");
		Integer idPlan = ParamUtil.getInteger(renderRequest,"idPlanNuevo");
//		ArrayList<TercerizadoraServicio> tercerizadorasPlanNuevo;
//		AfiTercerizadoraServicio tercAfiVigente = null;
//		AfiTercerizadoraServicio tercAfiNueva = null;
//		AfiTercerizadoraServicio tercAfiContinuidad = null;
//		TercerizadoraServicio tercPlanVigente = null;
//		
		Date fechaHastaPlanActual = DateUtils.getFechaHasta(renderRequest);
		Date fechaDesdeNuevoPlan = DateUtils.getFechaDesde(renderRequest);
		Date fechaHastaNuevoPlan = this.getFechaHastaPlanNuevo(renderRequest);
//
//		deshacemos cambios nuevos, porque puede ser que se arrepientan de bajas cascadas o seleccionan otro plan nuevo, etc...
		if(afiliadoTercerizadoras != null && afiliadoTercerizadoras.size() >0){
			for (int j=0; j<afiliadoTercerizadoras.size();j++){
				
				AfiTercerizadoraServicio afiTercServ = afiliadoTercerizadoras.get(j); 
						
				if(afiTercServ.getEstado()!=null && afiTercServ.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.ALTA) ){ //afiTercServ.isNuevo()
					afiliadoTercerizadoras.remove(afiTercServ);
				}
			}
		}
//		try {
//			tercerizadorasPlanNuevo=(ArrayList<TercerizadoraServicio>) TercerizadoraServiceUtil.getInstance().getTercerizadoraPlan(idPlan);
//			
//			//traigo la ultimo xq estan ordenads x fecha fin y la vigente queda última.
//			tercPlanVigente = tercerizadorasPlanNuevo.get(tercerizadorasPlanNuevo.size()-1);  
//			
////			CASO 1: Primer plan del afiliado, seteamos la primer tercerizadora
//			if(afiliadoTercerizadoras == null || afiliadoTercerizadoras.size() == 0){
//				
////				Si la fecha del plan es mayor o igual al dia que empieza la tercerizadora
//				if(tercPlanVigente.getFechaInicio()==null || 
//						(tercPlanVigente.getFechaInicio() !=null &&	
//					fechaDesdeNuevoPlan.compareTo(tercPlanVigente.getFechaInicio()) >= 0 )){
//					tercAfiVigente = new AfiTercerizadoraServicio();
//					tercAfiVigente.setAfiliado(new Afiliado(cuil_titular, inte));
//					tercAfiVigente.setTercerizadora(tercPlanVigente);
//					tercAfiVigente.setFechaInicioPres(fechaDesdeNuevoPlan);
//					tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
////					tercAfiVigente.setNuevo(true);
//					tercAfiVigente.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA); 
//				}else{ // existe continuidad de tercerizadoras para la vigencia desde del plan
//					tercAfiContinuidad = new AfiTercerizadoraServicio();
//					tercAfiContinuidad.setAfiliado(new Afiliado(cuil_titular, inte));
//					tercAfiContinuidad.setTercerizadora(tercerizadorasPlanNuevo.get(0));
//					tercAfiContinuidad.setFechaInicioPres(fechaDesdeNuevoPlan);
//					tercAfiContinuidad.setFechaFinPres(tercerizadorasPlanNuevo.get(0).getFechaFin());
////					tercAfiContinuidad.setNuevo(true);
//					tercAfiContinuidad.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//					
//					tercAfiVigente = new AfiTercerizadoraServicio();
//					tercAfiVigente.setAfiliado(new Afiliado(cuil_titular, inte));
//					tercAfiVigente.setTercerizadora(tercPlanVigente);
//					tercAfiVigente.setFechaInicioPres(tercPlanVigente.getFechaInicio());
//					tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
////					tercAfiVigente.setNuevo(true);
//					tercAfiVigente.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//				}
//			}else{
////				deshacemos cambios nuevos, porque puede ser que se arrepientan de bajas cascadas o seleccionan otro plan nuevo, etc...
////				for (int j=0; j<afiliadoTercerizadoras.size();j++){
////					
////					AfiTercerizadoraServicio afiTercServ = afiliadoTercerizadoras.get(j); 
////							
////					if(afiTercServ.isNuevo()){
////						afiliadoTercerizadoras.remove(afiTercServ);
////					}
////				}
////				suponer que la tercerizadora vigente del afiliado es la ultima esta bien? no lo se...
//				tercAfiVigente = afiliadoTercerizadoras.get(afiliadoTercerizadoras.size()-1);
//				
////				CASO 2:cambia a otro plan verificar si es diferente tercerizadora: 
////				debemos chequear si esta la tercerizadora vigente en la lista de las tercerizadoras correspondiente al plan nuevo
//				int pos = tercerizadorasPlanNuevo.indexOf(tercAfiVigente.getTercerizadora());
//				if(pos == -1){ // no la encontro
//					if((tercAfiVigente.getTercerizadora().getFechaFin()!=null 
//						&& tercAfiVigente.getTercerizadora().getFechaFin().compareTo(fechaHastaPlanActual) >= 0)
//							|| tercAfiVigente.getTercerizadora().getFechaFin()==null	
//							){
//						tercAfiVigente.setFechaFinPres(fechaHastaPlanActual);
//					}else{
//						
//						tercAfiVigente.setFechaFinPres(tercAfiVigente.getTercerizadora().getFechaFin());
//					}
//					
////					Si la fecha del plan es mayor o igual al dia que empieza la tercerizadora
//					if(tercPlanVigente.getFechaInicio()==null || 
//							(tercPlanVigente.getFechaInicio() !=null &&	
//						fechaDesdeNuevoPlan.compareTo(tercPlanVigente.getFechaInicio()) >= 0 )){
//						tercAfiNueva = new AfiTercerizadoraServicio();
//						tercAfiNueva.setAfiliado(new Afiliado(cuil_titular, inte));
//						tercAfiNueva.setTercerizadora(tercPlanVigente);
//						tercAfiNueva.setFechaInicioPres(fechaDesdeNuevoPlan);
//						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
////						tercAfiNueva.setNuevo(true);
//						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//					}else{ // existe continuidad de tercerizadoras para la vigencia desde del plan
//						tercAfiContinuidad = new AfiTercerizadoraServicio();
//						tercAfiContinuidad.setAfiliado(new Afiliado(cuil_titular, inte));
//						tercAfiContinuidad.setTercerizadora(tercerizadorasPlanNuevo.get(0));
//						tercAfiContinuidad.setFechaInicioPres(fechaDesdeNuevoPlan);
//						tercAfiContinuidad.setFechaFinPres(tercerizadorasPlanNuevo.get(0).getFechaFin());
////						tercAfiContinuidad.setNuevo(true);
//						tercAfiContinuidad.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//						
//						tercAfiNueva = new AfiTercerizadoraServicio();
//						tercAfiNueva.setAfiliado(new Afiliado(cuil_titular, inte));
//						tercAfiNueva.setTercerizadora(tercPlanVigente);
//						tercAfiNueva.setFechaInicioPres(tercPlanVigente.getFechaInicio());
//						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
////						tercAfiNueva.setNuevo(true);
//						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//					}
//				} else{ // encontro la tercerizador vigente con la que necesita el plan
////					CASO 3 necesito prolongar la duracion de la tercerizadora,					
////						tercAfiVigente = new AfiTercerizadoraServicio();
////						tercAfiVigente.setAfiliado(new Afiliado(cuil_titular, inte));
////						tercAfiVigente.setTercerizadora(tercPlanVigente);
////						tercAfiVigente.setFechaInicioPres(fechaDesdeNuevoPlan);
//						tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
////						tercAfiVigente.setNuevo(true);
//					
//					
//				}
//			}
//			
//			if(afiliadoTercerizadoras == null || afiliadoTercerizadoras.size() == 0){
//				afiliadoTercerizadoras = new ArrayList<AfiTercerizadoraServicio>();
//				afiliadoTercerizadoras.add(tercAfiVigente);
//				
//			}
//			if(tercAfiContinuidad != null){
//				afiliadoTercerizadoras.add(tercAfiContinuidad);
//			}
//			if(tercAfiNueva != null){
//				afiliadoTercerizadoras.add(tercAfiNueva);
//			}
		
		    afiliadoTercerizadoras = TercerizadoraFactory.getTercerizadorasDependientePlanPeriodo(cuilTitular, 
		    		idPlan, fechaHastaPlanActual, fechaDesdeNuevoPlan, fechaHastaNuevoPlan, afiliadoTercerizadoras);
		
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
					renderRequest).getSession();
			session.setAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION,afiliadoTercerizadoras);
			
//		} catch (Exception e) {
//			_log.error(e);
//		}
	}

	private Date getFechaHastaPlanNuevo(RenderRequest renderRequest) {
		Date fechaHastaNuevoPlan = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaDesdeDia = ParamUtil.getString(renderRequest, "fechaHastaNuevoDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest, "fechaHastaNuevoMes");
			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
			String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechaHastaNuevoAnio");

			fechaHastaNuevoPlan = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
		} catch (Exception e) {
//			Nada, seguro que no tenia vigen jhasta el cambio de plan
		}
		return fechaHastaNuevoPlan;
	}
	
	private void bajaTercerizadoraPorBajaSituLaboral(
			RenderRequest renderRequest,
			List<AfiTercerizadoraServicio> tercerizadoraList) {
		
		Date fechaEgreMayor = getFechaEgreMayor(tercerizadoraList);
		Date fechaEgreso = getFechaEgreso(renderRequest);
		Calendar fechaEgresoMas3Meses = DateUtils.getCalendarGMTMenos3();
		fechaEgresoMas3Meses.setTime(fechaEgreso);
		fechaEgresoMas3Meses.add(Calendar.MONTH, 3);
		
		String isPlusTres = ParamUtil.getString(renderRequest, "isPlusTres");
		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
		Integer idMotBaja = ParamUtil.getInteger(renderRequest, "idMotivoBaja");
		renderRequest.setAttribute("isPlusTres", isPlusTres);
		boolean esDeshacerCambioUltimaTercerizadora = false;
		Calendar aux = Calendar.getInstance();
//		Deberían llegar como maximo las últimas 2 tercerizadoras si hubo continuidad.
		for (AfiTercerizadoraServicio terce : tercerizadoraList) {
			if (fechaEgreso != null && isPlusTres.equalsIgnoreCase("true")) {
				
				if (terce.getFechaFinPres() == null
						|| (terce.getFechaFinPres() != null 
							&& fechaEgreMayor !=null 
							&& DateUtils.compararFechasTruncarEnDia(terce.getFechaFinPres(), 
							fechaEgreMayor) == 0)) {
					
					aux.setTime(fechaEgreso);
					
					boolean hayRestodeGrupoFliar = false;
					
					if(idMotBaja == 2){ // fallecimiento
						List<Afiliado> afiliadosList = new ArrayList<Afiliado>();
						try {
							afiliadosList = BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(cuilTitular);
						} catch (Exception e) {
							_log.error(e);
						}
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
					if(idMotBaja == 1 || (idMotBaja == 2 && hayRestodeGrupoFliar) || idMotBaja == 3 || idMotBaja == 21 ){ 
						aux.add(Calendar.MONTH, 3);
					}
					
//					Si la baja fecha, es anterior al inicio de la tercerizadora, damos de baja esa tercerizadora, 
//					y la fecha de baja se la ponemos a la otra..
					if(terce.getFechaInicioPres() != null 
							&& DateUtils.compararFechasTruncarEnDia(terce.getFechaInicioPres(),isPlusTres.equalsIgnoreCase("true")?fechaEgresoMas3Meses.getTime():fechaEgreso) > 0){
						esDeshacerCambioUltimaTercerizadora = true;
//						terce.setBorradoLogico(true);
						terce.setEstado(AfiTercerizadoraServicio.ESTADOS.BAJA);
					}else{
						terce.setFechaFinPres(aux.getTime()); //fechaEgreso					
					}
				}
			} else if (fechaEgreso != null && isPlusTres.equalsIgnoreCase("false")) {
				if (terce.getFechaFinPres() == null
						|| (terce.getFechaFinPres() != null 
								&& fechaEgreMayor !=null 
								&& DateUtils.compararFechasTruncarEnDia(terce.getFechaFinPres(), 
								fechaEgreMayor) == 0)) {
					
//					Si la baja fecha, es anterior al inicio de la tercerizadora, damos de baja esa tercerizadora, 
//					y la fecha de baja se la ponemos a la otra..
					if(terce.getFechaInicioPres() != null && DateUtils.compararFechasTruncarEnDia(terce.getFechaInicioPres(), 
							fechaEgreso) > 0){
						esDeshacerCambioUltimaTercerizadora = true;
//						terce.setBorradoLogico(true);
						terce.setEstado(AfiTercerizadoraServicio.ESTADOS.BAJA);
					}else{
						terce.setFechaFinPres(fechaEgreso);						
					}
				}
			} else if (fechaEgreso == null) { // limpiando la baja del plan xq es recuperacion de baja futura...
				
				if (terce.getFechaFinPres() == null
						|| (terce.getFechaFinPres() != null 
								&& fechaEgreMayor !=null 
								&& DateUtils.compararFechasTruncarEnDia(terce.getFechaFinPres(), 
								fechaEgreMayor) == 0)) {
					
					terce.setFechaFinPres(null);	
				}
				
			}
		}
//		vuelvo a recorrer las tercerizadoras para marcar la fecha fin en la otra
		if(esDeshacerCambioUltimaTercerizadora){
			for (AfiTercerizadoraServicio terce : tercerizadoraList) {
				
				if(terce.getEstado()==null 
						|| (terce.getEstado()!=null && !terce.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA))){ //!terce.isBorradoLogico()
//					esto lo tuve que comentar porque para planes Ospim se da 3 meses y para los otros no, y solo me llega la marca isPlusTRes
//					if(isPlusTres!=null&&isPlusTres.equalsIgnoreCase("true")){
//						terce.setFechaFinPres(aux.getTime());
//					}else{
					  if(terce.getFechaInicioPres() != null && DateUtils.compararFechasTruncarEnDia(terce.getFechaInicioPres(), 
							fechaEgreso) <= 0  &&
						 (terce.getFechaFinPres()==null || (terce.getFechaFinPres()!=null && DateUtils.compararFechasTruncarEnDia(terce.getFechaFinPres(), 
									fechaEgreso) > 0)	
						 )  ){
					
						  terce.setFechaFinPres(fechaEgreso);
					  }	
//					}	
				}
			}

		}

	}

	private Date getFechaEgreMayor(
			List<AfiTercerizadoraServicio> tercerizadoraList) {
		Date fechaEgreMayor = null;
		for (int j = 0; j < tercerizadoraList.size(); j++) {
//			if(!tercerizadoraList.get(j).isBorradoLogico()){
			if((tercerizadoraList.get(j).getEstado()!=null 
					&& !tercerizadoraList.get(j).getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA))
					|| tercerizadoraList.get(j).getEstado()==null){
				if (fechaEgreMayor == null) {
					fechaEgreMayor = tercerizadoraList.get(j).getFechaFinPres();
				}
				if (tercerizadoraList.get(j).getFechaFinPres() != null) {
					int comp = tercerizadoraList.get(j).getFechaFinPres()
							.compareTo(fechaEgreMayor);
					if (comp > 0) {
						fechaEgreMayor = tercerizadoraList.get(j).getFechaFinPres();
					}
				} else {
					fechaEgreMayor = null;
				}
			}	
		}
		return fechaEgreMayor;
	}

//	private void altaTercerizadora(RenderRequest renderRequest,
//			List<AfiTercerizadoraServicio> tercerizadoraList)
//			throws ParseException {
//		List<TercerizadoraServicio> tercerizadorasServicio = TraeListasServiceUtil
//				.getTercerizadoraServicio(renderRequest);
//
//		HttpServletRequest req = PortalUtil
//				.getHttpServletRequest(renderRequest);
//		Date fechaIni = DateUtils.getFechaDesde(req);
//		Date fechaFin = DateUtils.getFechaHasta(req);
//		String id_tercerizadora = ParamUtil.getString(renderRequest,
//				"id_tercerizadora");
//		int indexOf = tercerizadorasServicio.indexOf(new TercerizadoraServicio(
//				id_tercerizadora));
//		TercerizadoraServicio tercerizadora = tercerizadorasServicio
//				.get(indexOf);
//		AfiTercerizadoraServicio afiTercerizadoraServicio = new AfiTercerizadoraServicio(
//				id_tercerizadora, tercerizadora.getDescripcion(), fechaIni,
//				fechaFin);
//
//		if (tercerizadoraList.contains(afiTercerizadoraServicio)) {
//			return;
//		}
//
//		String ingreOriginal = renderRequest.getParameter("ingre_original");
//
//		if (StringUtils.checkNotEmpty(ingreOriginal)) {
//			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//			Date ingreOriginalDate = format.parse(ingreOriginal);
//			afiTercerizadoraServicio.setFechaInicioPreseditada(ingreOriginalDate);
//
//			for (AfiTercerizadoraServicio ats : tercerizadoraList) {
//				if (ats.getTercerizadora().getId_tercerizadora()
//						.equals(id_tercerizadora)
//						&& DateUtils
//								.compararFechasTruncarEnDia(
//										ats.getFechaInicioPresOriginal(),
//										ingreOriginalDate) == 0) {
//					ats.setFechaInicioPreseditada(ingreOriginalDate);
//					ats.setFechaInicioPres(afiTercerizadoraServicio
//							.getFechaInicioPres());
//					ats.setFechaFinPres(afiTercerizadoraServicio.getFechaFinPres());
//				}
//			}
//		} else {
//			afiTercerizadoraServicio.setNuevo(true);
//			tercerizadoraList.add(afiTercerizadoraServicio);
//		}
//	}
//
//	private void bajaTercerizadora(RenderRequest renderRequest,
//			List<AfiTercerizadoraServicio> tercerizadoraList)
//			throws ParseException, Exception {
//		Date fecha_ingreso = null;
//		String id_tercerizadora = ParamUtil.getString(renderRequest,
//				"id_tercerizadora");
//		String fecha_ingreso_string = renderRequest
//				.getParameter("fechaIngreso");
//		if (null != fecha_ingreso_string) {
//			fecha_ingreso = DateUtils.parse(fecha_ingreso_string,
//					DateUtils.SHORT);
//		}
//		Date fecha_egreso = getFechaEgreso(renderRequest);
//
//		try {
//			AfiTercerizadoraServicio afiTercerizadoraServicio = new AfiTercerizadoraServicio(
//					id_tercerizadora, "", fecha_ingreso, fecha_egreso);
//			int indexOf = tercerizadoraList.indexOf(afiTercerizadoraServicio);
//			tercerizadoraList.get(indexOf).setBorradoLogico(true);
//		} catch (Exception e) {
//			_log.error("error al actualizar tecerizadora en sesion", e);
//			throw e;
//		}
//	}

	private Date getFechaEgreso(RenderRequest renderRequest) {
		Date fecha_egreso = null;
		String fecha_egreso_string = renderRequest.getParameter("fechaEgreso");
		if (StringUtils.checkNotEmpty(fecha_egreso_string)) {
			try {
				fecha_egreso = DateUtils.parse(fecha_egreso_string,
						"dd/MM/yyyy");
			} catch (ParseException e) {
				fecha_egreso = null;
			}
		}
		return fecha_egreso;
	}
}