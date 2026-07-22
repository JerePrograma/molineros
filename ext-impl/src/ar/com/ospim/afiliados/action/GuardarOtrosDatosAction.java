package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.beans.TipoAporte;
import ar.com.ospim.afiliados.exceptions.FaltanDatosAfiliadoException;
import ar.com.ospim.afiliados.exceptions.FaltanDatosException;
import ar.com.ospim.afiliados.exceptions.FaltanPlanesException;
import ar.com.ospim.afiliados.exceptions.FaltanSituacionesLaboralesException;
import ar.com.ospim.afiliados.exceptions.FaltanTercerizadorasException;
import ar.com.ospim.afiliados.exceptions.SituacionLaboralInvalidaException;
import ar.com.ospim.afiliados.exceptions.TercNoCorrespPlanException;
import ar.com.ospim.afiliados.reportes.ReporteHistoricoMovimientosAfiliadoExcel;
import ar.com.ospim.afiliados.services.AfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.ReincorporarServiceUtil;
import ar.com.ospim.afiliados.services.SituLaboralServiceUtil;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class GuardarOtrosDatosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GuardarOtrosDatosAction.class);

	private PlanServiceUtil planService = new PlanServiceUtil();
	
	@SuppressWarnings("unchecked")
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	
		String updateAfiBorrado = ParamUtil.getString(actionRequest, Constants.EDIT);
      
		if (updateAfiBorrado!= null && updateAfiBorrado.equals(Constants.UPDATE)) {      	
        	EditarAfiliadoEntryAction editar = new EditarAfiliadoEntryAction();
        	editar.armarAfiliadoEntry(actionRequest);
        }
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		PortletSession portletSession = actionRequest.getPortletSession();
		Afiliado afiliadoInSession = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
		
		if (StringUtils.checkEmpty(updateAfiBorrado)) {
		    validarDniBeneficiarioVigente(actionRequest, afiliadoInSession);
		}
		
		Afiliado afiliadoEnBase = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(
				afiliadoInSession.getCuil_titular(), 0);
		
		String ddeReincorporar = actionRequest.getParameter(WebKeysAfiliados.DESDE_REINCORPORAR);
		Boolean esReincorporacion = false; 
		Boolean recuperarUltimoPlan = (Boolean) session.getAttribute(WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES);
		Boolean esBajaFutura = (Boolean) session.getAttribute(WebKeysAfiliados.AFILIADO_BAJA_FUTURA); 

		if(recuperarUltimoPlan == null){
			recuperarUltimoPlan = false;
		}
		
		Boolean baja_cascada = (Boolean) session.getAttribute("baja_cascada");
		if(baja_cascada==null){
			baja_cascada=false;
		}
		
		if(ddeReincorporar != null && ddeReincorporar.trim().equals(WebKeysAfiliados.DESDE_REINCORPORAR)){
			esReincorporacion = true;
		}
		if(esBajaFutura == null){
			esBajaFutura = false;
		}
		// Parametros Situaciones laborales
		List<SituacionLaboral> situLaborales = (ArrayList<SituacionLaboral>) session.getAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL);
		List<SituacionLaboral> situLaboralesAdd = new ArrayList<SituacionLaboral>();
		List<SituacionLaboral> situLaboralesUp = new ArrayList<SituacionLaboral>();
		
		if (null != situLaborales) {
			//aprovecho esta recorrida para evaluar si se estan dando de baja en cascada las situaciones laborales del
			//afiliado y del integrante que unifica.
			//si es cascada, y hay mas de una situacion laboral se agrega fecha de baja a la situ del que unifica.
			Date fechaEgresoSituLaboral = (Date) session.getAttribute("fecha_egreso");
			int idMotivoBaja=0;
			String motivoDesc="";
			
			for (int i = 0; i < situLaborales.size(); i++) {
				if (situLaborales.get(i).getEstado() != null) {
					boolean add = situLaborales.get(i).getEstado().equals("add");
					boolean up = situLaborales.get(i).getEstado().equals("update");
					if (add) {
						situLaboralesAdd.add(situLaborales.get(i));
					} else if (up) {
						//si es update, pudo haber sido dado de baja la situacion laboral
						idMotivoBaja = situLaborales.get(i).getMotivoBaja()!=null?situLaborales.get(i).getMotivoBaja().getId_motivo_baja():0;
						motivoDesc = situLaborales.get(i).getMotivoBaja()!=null?situLaborales.get(i).getMotivoBaja().getDescripcion():"";
						
						situLaboralesUp.add(situLaborales.get(i));
					}
				}
			}
			//ponemos la fecha de baja y el moptivo a la situacion del inte q unifica si corresponde
			if(baja_cascada && fechaEgresoSituLaboral != null && situLaborales.size()>1){
				for (Iterator<SituacionLaboral> iterator = situLaborales.iterator(); iterator.hasNext();) {
					SituacionLaboral sl = iterator.next();
					if(sl.getEstado()==null && sl.getFecha_baja() == null && sl.getAfiliado().getInte() != 0){ // que no pise la baja de otra situ laboral ya dada de baja
						sl.setFecha_baja(fechaEgresoSituLaboral);
						sl.setMotivoBaja(new MotivoBaja(idMotivoBaja, motivoDesc));
						sl.setEstado("update");
						sl.setViejaFechaIngreso(sl.getFecha_ingre());
						situLaboralesUp.add(sl);
					}
				}
			}
		}
		AfiPlan afiPlanNuevo = null, afiPlanActual = null;
				
		if(!recuperarUltimoPlan){
			
			afiPlanNuevo =  this.getPlanNuevo(actionRequest, afiliadoInSession);
		
		
			afiPlanActual =  this.getPlanActual(actionRequest, afiliadoInSession, esBajaFutura);
			
		}	
		
//		List<AfiTercerizadoraServicio> tercerizadoras = (ArrayList<AfiTercerizadoraServicio>) session
//				.getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);
		
		// Parametros Plan y Aportes
//		AfiAporteList afiAporteList = (AfiAporteList) portletSession
//				.getAttribute(WebKeysAfiliados.BUSQUEDA_APORTES, PortletSession.APPLICATION_SCOPE);
//		afiliadoInSession.setAfi_aporte_list(afiAporteList);
//		int idPlan = ParamUtil.getInteger(actionRequest, "plan");
//		int id_plan_omint = ParamUtil.getInteger(actionRequest, "plan-omint");
//		actionRequest.setAttribute("idPlan", idPlan);
//		List<TipoAporte> listaTiposAporte = TraeListasServiceUtil.getTiposAporte(actionRequest);
//		boolean esCambioPlan = afiAporteList != null
//				&& afiAporteList.getPlan() != null ? afiAporteList.getPlan().getId() != idPlan : true;
//		AfiAporteList aportesNuevos = null;
//
//		if (session.getAttribute(WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES) == null
//				|| !((Boolean) session.getAttribute(WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES))
//				|| afiAporteList == null
//				|| afiAporteList.getPlan() == null
//				|| afiAporteList.getListaAportes() == null) {
//			aportesNuevos = getFechasPlan(afiAporteList, actionRequest);
//		}
//
//		List<AportesYEgreso> aportesValidosParaFechaVigenciaOriginal = AporteServiceUtil
//				.buscarAportesValidosParaFechaVigencia(afiliadoInSession.getCuil_titular());

//		Validamos inconsistencias de SitusLab, Plan y Tercerizadoras entre si
//		boolean sonSituLaboralesBajas = estandeBajaLasSituacLaborales(situLaborales);
//		boolean sonPlanesBajas = estandeBajaLosPlanes(afiPlanActual, afiPlanNuevo);
//		boolean sonTercerizadorasBajas = estandeBajaLasTercerizadoras(tercerizadoras);
//		try{
//			
//			if(!sonSituLaboralesBajas && sonPlanesBajas && sonTercerizadorasBajas){
//				throw new SituacionLaboralInvalidaException("Verifique que existe una situación laboral vigente");
//			}
//			if(sonSituLaboralesBajas && !sonPlanesBajas && sonTercerizadorasBajas){
//				throw new FaltanPlanesException("El afiliado tiene un plan vigente");
//			}
//			if(sonSituLaboralesBajas && sonPlanesBajas && !sonTercerizadorasBajas){
//				throw new FaltanTercerizadorasException("El afiliado tiene una tercerizadora vigente");
//			}
//		} catch (SituacionLaboralInvalidaException e) {
//			manejarException(actionRequest, "FaltanSitus", e.getMessage(), e);
//		} catch (FaltanPlanesException e) {
//			manejarException(actionRequest, "FaltanPlanes", e.getMessage(), e);
//		} catch (FaltanTercerizadorasException e) {
//			manejarException(actionRequest, "FaltanTercerizadoras",e.getMessage(), e);
//		}
// fin Validamos inconsistencias de SitusLab, Plan y Tercerizadoras entre si		
		
		if(SessionErrors.isEmpty(actionRequest)){
			if(esReincorporacion || esBajaFutura ){
				
				int continuidad = 0;
				if(afiliadoInSession != null && afiliadoEnBase != null){
					continuidad = esContinuidad(afiliadoInSession.getVigen_fecha(), afiliadoEnBase.getVigen_fecha());
				}
				
				procesarReincorporacionAfiliado(actionRequest, situLaborales,
						situLaboralesAdd, situLaboralesUp/*, afiAporteList, idPlan,
						id_plan_omint, listaTiposAporte, esCambioPlan, */ ,recuperarUltimoPlan, esBajaFutura /*
						aportesNuevos, aportesValidosParaFechaVigenciaOriginal*/,continuidad ,afiPlanNuevo, afiPlanActual);
			} else {
				procesarAfiliado(actionRequest, situLaborales, situLaboralesAdd,
						situLaboralesUp/*, afiAporteList, idPlan, id_plan_omint,
						listaTiposAporte, esCambioPlan, aportesNuevos,
						aportesValidosParaFechaVigenciaOriginal*/,afiPlanActual,afiPlanNuevo, baja_cascada,  updateAfiBorrado);
			}
			message(actionRequest);

		
	//		// borrar aportes de sesión
	//		portletSession.removeAttribute(WebKeysAfiliados.BUSQUEDA_APORTES,
	//				PortletSession.APPLICATION_SCOPE);
	//		session.removeAttribute("fecha_egreso");
			portletSession.removeAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION, PortletSession.APPLICATION_SCOPE);
			session.removeAttribute("baja_cascada");
			
			session.removeAttribute(WebKeysAfiliados.AFILIADO_BAJA_FUTURA);
			
			// resetear lista en sesión de situaciones laborales con las de la BD
			// situLaborales = SituLaboralServiceUtil.buscaSituLaboral(
			// afiliadoInSession.getCuil_titular(),
			// afiliadoInSession.getInte());
			// setear el tab de la pagina
//			this.setearTab(actionRequest, afiliadoInSession, afiliadoInSession.getCuil_titular());
//			setForward(actionRequest, "portlet.afiliados.editar_afiliado_entry");
			
			if(afiliadoInSession.getTieneAntecedentesJudiciales() == 1  ){
				this.enviarNovedadsobreAfiliadoConAntecJudiciales(afiliadoInSession.getCuil_titular());
				
			}
			
			boolean existeIntegranteDiscap = false;
			List<Afiliado> grupoFamiliar = BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(afiliadoInSession.getCuil_titular());
			if(grupoFamiliar!=null && grupoFamiliar.size()>0){
				for (Iterator<Afiliado> iterator = grupoFamiliar.iterator(); iterator.hasNext();) {
					Afiliado af = iterator.next();
					if(af.getDiscapacitado().equals("1")){
						existeIntegranteDiscap = true;
						break;
					}
				}
			}
			
			if (existeIntegranteDiscap && baja_cascada) {

			    this.enviarNovedadsobreAfiliadoDiscapacidad(afiliadoInSession.getCuil_titular());

			    User user = PortalUtil.getUser(actionRequest);

			    Date fecha = afiliadoInSession.getBaja_fecha();
			    if (fecha == null) fecha = (Date) session.getAttribute("fecha_egreso");

			    Integer motivo = afiliadoInSession.getId_motivo_baja();
			    if (motivo == null || motivo == 0) {
			        Integer m = (Integer) session.getAttribute("motivo_baja");
			        if (m != null) motivo = m;
			    }

			    EditarAfiliadoServiceUtil.reenviarAutorizacionesRecortadasPorBaja(
			        afiliadoInSession.getCuil_titular(),
			        fecha,
			        motivo != null ? motivo : 0,
			        user.getScreenName()
			    );
			}

			
			
		}
		this.setearTab(actionRequest, afiliadoInSession, afiliadoInSession.getCuil_titular(),  updateAfiBorrado);
		setForward(actionRequest, "portlet.afiliados.editar_afiliado_entry");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		int inte = 0;
//		FIXME comente esta linea, trae problemas?? deberia estar en session ?
		
//		renderRequest.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,
//				ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuil_titular,inte));
		
		List<SituacionLaboral> laboralList = SituLaboralServiceUtil.buscaSituLaboral(cuil_titular,inte);
		List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().consultaUltimosComponentesPlanVigente(cuil_titular);
		List<AfiTercerizadoraServicio> tercerizAfi = TercerizadoraServiceUtil.buscarUltimasTercerizadorasContinuidadDelAfiliado(null, cuil_titular);
		
		session.setAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION, tercerizAfi);
		session.setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL,laboralList);
		
		//almaceno la lista en request
		renderRequest.setAttribute("IdsSocio", afiAportes); 
		
		return mapping.findForward(getForward(renderRequest,"portlet.afiliados.editar_afiliado_entry"));
	}

	@SuppressWarnings("unchecked")
	private void procesarReincorporacionAfiliado(ActionRequest actionRequest,
			List<SituacionLaboral> situLaborales,
			List<SituacionLaboral> situLaboralesAdd,
			List<SituacionLaboral> situLaboralesUp/*,
			AfiAporteList afiAporteList, int idPlan, int id_plan_omint,
			List<TipoAporte> listaTiposAporte, boolean esCambioPlan, */ ,boolean esRecuperarUltimoPlan, boolean esBajaFutura /*
			AfiAporteList aportesNuevos,
			List<AportesYEgreso> aportesValidosParaFechaVigenciaOriginal*/,int continuidad, AfiPlan afiPlanNuevo, AfiPlan afiPlanActual) {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				actionRequest).getSession();
		Afiliado afiliadoInSession = (Afiliado) session
				.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

//		List<AfiTercerizadoraServicio> tercerizadoras = (ArrayList<AfiTercerizadoraServicio>) session
//				.getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);  
		
		List<AfiTercerizadoraServicio> tercerizadoras = null;
		if(esRecuperarUltimoPlan){ // si recuperan el plan, analizo las tercerizadoras que posee en la BD
//			try {
//				tercerizadoras = TercerizadoraServiceUtil.buscaTercerizadoras(afiliadoInSession.getCuil_titular(), afiliadoInSession.getInte());
//			} catch (Exception e1) {
//				manejarException(actionRequest, "FaltanTercerizadoras",e1.getMessage(), 
//						new FaltanTercerizadorasException("El afiliado debe tener una tercerizadora vigente"));
//			}
		}else{ // si no recupera plan, reviso lso datos cargados en solapa informacion adicional
			tercerizadoras = (ArrayList<AfiTercerizadoraServicio>) session.getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);
		}
				
		List<Afiliado> afiliados = (List<Afiliado>) session
				.getAttribute(WebKeysAfiliados.REINCORPORAR_AFILIADOS_A_RECUPERAR);
		Date vigen_fecha = (Date) session
				.getAttribute(WebKeysAfiliados.REINCORPORAR_VIGEN_FECHA);
		
		
		
//		Boolean recuperar = (Boolean) session.getAttribute(WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES);
//		if (afiAporteList == null || afiAporteList.getPlan() == null) {
//			recuperar = false;
//		}
		
		boolean esSituLaboralVigente = false;
		boolean esTercerizadoraVigente = false;
//		boolean planEsBaja = false;
		
		if(!esBajaFutura){
			 esSituLaboralVigente = this.existenSitusLaboralesVigente(situLaborales, afiliadoInSession.getVigen_fecha());
			 
			 esTercerizadoraVigente = this.existeTercerizadoraVigente(tercerizadoras, afiliadoInSession.getVigen_fecha());
		}else{
//			TODO hacer un metodo que consulte en base si la fecha de alguna nueva tercerizadora, cumple c continuidad del afiliado, sin tener q
//			cambiar la vigencia del afiliado, o sea, si es continuidad, por el plan de propagacion, o por continuidad del anses - desempleo
			
			esTercerizadoraVigente = this.existeTercerizadoraVigente(tercerizadoras, afiliadoInSession.getVigen_fecha());
			 
			afiliados = new ArrayList<Afiliado>();
			afiliados.add(afiliadoInSession);
			vigen_fecha = afiliadoInSession.getVigen_fecha();
		}
		
//		if (!recuperar) {
//			planEsBaja = planEsBaja(afiAporteList, aportesNuevos,afiliadoInSession.getVigen_fecha());
//		}

		try {
			if (esSituLaboralVigente) {
				throw new SituacionLaboralInvalidaException(
						"Verifique que exista una situación laboral vigente");
			}
//			if (planEsBaja
//					|| (afiAporteList.getPlan() == null && aportesNuevos
//							.getPlan() == null)) {
//				throw new FaltanPlanesException(
//						"El afiliado debe tener un plan vigente");
//			}
			if (esTercerizadoraVigente) {
				throw new FaltanTercerizadorasException(
						"El afiliado debe tener una tercerizadora vigente");
			}

//			List<Afiliado> afiliados = (List<Afiliado>) session
//					.getAttribute(WebKeysAfiliados.REINCORPORAR_AFILIADOS_A_RECUPERAR);
//			Date vigen_fecha = (Date) session
//					.getAttribute(WebKeysAfiliados.REINCORPORAR_VIGEN_FECHA);

//			if (afiAporteList == null || afiAporteList.getPlan() == null) {
//				recuperar = false;
//			}

			User user = PortalUtil.getUser(actionRequest);

			ReincorporarServiceUtil.reincorporarGrupofamiliarYGuardarDatos(
					afiliados, vigen_fecha, user, /*recuperar,*/ afiliadoInSession,
					situLaborales, situLaboralesAdd, situLaboralesUp,
					/*afiAporteList, idPlan, id_plan_omint, listaTiposAporte, */esRecuperarUltimoPlan, esBajaFutura,/*
					esCambioPlan, aportesNuevos,*/ tercerizadoras/*,
					aportesValidosParaFechaVigenciaOriginal*/ ,continuidad,afiPlanNuevo, afiPlanActual);

//			Recuperamos el AfiPlan (Plan y Aportes)  para el afiliado titular
			AfiPlan afiPlan = PlanServiceUtil.getInstance().buscarUltimoPlanAportes(afiliadoInSession.getCuil_titular());
			afiliadoInSession.setAfiPlan(afiPlan); 
			//Setteo la nueva fecha de baja al titular de acuerdo al ultimo plan vigente
			afiliadoInSession.setBaja_fecha(afiPlan.getVigenHasta());
			
			//FIXME se deberia agregar el usuario de baja y el motivo en la session aunque despue so
			//toma
			//afiliadoInSession.setBaja_fecha(afiPlan.getMotivoBaja().getBaja_fecha());
			//afiliadoInSession.setBaja_usr(afiPlan.getMotivoBaja().getBaja_usr());
			//afiliadoInSession.setId_motivo_baja(afiPlan.getMotivoBaja().getId_motivo_baja());
			
			session.removeAttribute(WebKeysAfiliados.REINCORPORAR_AFILIADOS_A_RECUPERAR);
			session.removeAttribute(WebKeysAfiliados.REINCORPORAR_VIGEN_FECHA);
			session.removeAttribute(WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES);
			// session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
		} catch (SituacionLaboralInvalidaException e) {
			manejarException(actionRequest, "FaltanSitus", e.getMessage(), e);
		} catch (FaltanPlanesException e) {
			manejarException(actionRequest, "FaltanPlanes", e.getMessage(), e);
		} catch (FaltanDatosException e) {
			manejarException(actionRequest, "FaltanDatos","El plan no está vigente", e);
		} catch (TercNoCorrespPlanException e) {
			manejarException(actionRequest,"TercNoCorrespPlan",
					"La tercerizadora ingresada no se corresponde con el plan. Por favor, verifíquelo.",e);	
		} catch (Exception e) {
			_log.error("Error al guardar otros datos->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",e);
			manejarException(actionRequest, "FaltanDatos", e.getMessage(), e);
		}
		
	}

	@SuppressWarnings("unchecked")
	private void procesarAfiliado(ActionRequest actionRequest,
			List<SituacionLaboral> situLaborales,
			List<SituacionLaboral> situLaboralesAdd,
			List<SituacionLaboral> situLaboralesUp//,
//			AfiAporteList afiAporteList, int idPlan, int id_plan_omint,
//			List<TipoAporte> listaTiposAporte, boolean esCambioPlan,
//			AfiAporteList aportesNuevos,
//			List<AportesYEgreso> aportesValidosParaFechaVigenciaOriginal
			,AfiPlan planActual, AfiPlan planNuevo, Boolean baja_cascada, String updateAfiBorrado)
			throws PortalException, SystemException, Exception, ParseException {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				actionRequest).getSession();
		PortletSession portletSession = actionRequest.getPortletSession();
		Afiliado afiliadoInSession = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

		List<AfiTercerizadoraServicio> tercerizadoras = (ArrayList<AfiTercerizadoraServicio>) session
				.getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);

		// guardar la info recuperada de session
		String accion = (String) session.getAttribute(Constants.CMD);
		String opciones = actionRequest.getParameter("opciones");
		String preCarga = actionRequest.getParameter("pre_carga");
		String idPreCarga = actionRequest.getParameter("id_pre_afiliado");

		User user = PortalUtil.getUser(actionRequest);

		// Fecha ingreso original de la tercerizadora antes de ser editada
		String fechaIngresoOriginalS = null;
		fechaIngresoOriginalS = ParamUtil.getString(actionRequest,"fechaIngresoOriginal");
		Date fechaIngresoOriginal = DateUtils.parse(fechaIngresoOriginalS,DateUtils.SHORT);
		
		boolean esSituLaboralVigente = this.existenSitusLaboralesVigente(situLaborales, afiliadoInSession.getVigen_fecha());

//		boolean ingresoAportesOk = this.validarVigenciaAficonIngresoDeAportes(aportesNuevos, afiliadoInSession.getVigen_fecha(),esCambioPlan)
//				&& aportesValidosParaFechaVigenciaOriginal == null;
		boolean esTercerizadoraVigente = this.existeTercerizadoraVigente(tercerizadoras, afiliadoInSession.getVigen_fecha());

		//updateAfiBorrado  quiero modificar un afiliado dado de baja sin validaciones
		try {
			if (StringUtils.checkEmpty(updateAfiBorrado)  && esSituLaboralVigente && !baja_cascada) {
				throw new SituacionLaboralInvalidaException(
						"Verifique que exista una situación laboral vigente");
			}

//			Revisamos si hay bug de cambio de plan, que al plan nuevo le pone la misma fecha ingreso que al plan anterior
			if(StringUtils.checkEmpty(updateAfiBorrado)  && planActual != null && planNuevo != null && planActual.getVigenDesde() == planNuevo.getVigenDesde()){
				throw new FaltanPlanesException("Se produjo error al sugerir la fecha de vigencia desde del nuevo plan");
			}
				
			if (StringUtils.checkEmpty(updateAfiBorrado)  && esTercerizadoraVigente && !baja_cascada) {
				throw new FaltanTercerizadorasException(
						"El afiliado debe tener una y sola una tercerizadora vigente por período");
			}

			AfiliadoServiceUtil.guardarOtrosDatos(actionRequest, session,
					portletSession, afiliadoInSession, situLaborales, accion, user, baja_cascada,
					situLaboralesAdd, situLaboralesUp, tercerizadoras, fechaIngresoOriginal,
					opciones, preCarga, idPreCarga, planActual, planNuevo, updateAfiBorrado);

			session.removeAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);
			session.removeAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL);
			
			session.removeAttribute("opciones");
			session.removeAttribute("pre_carga");
			session.removeAttribute("pre_carga_id");
			
			session.setAttribute(Constants.CMD, Constants.UPDATE);
		} catch (SituacionLaboralInvalidaException e) {
			manejarException(actionRequest, "FaltanSitus", e.getMessage(), e);
		} catch (FaltanPlanesException e) {
			manejarException(actionRequest, "FaltanPlanes", e.getMessage(), e);
		} catch (FaltanTercerizadorasException e) {
			manejarException(actionRequest, "FaltanTercerizadoras",e.getMessage(), e);
		} catch (TercNoCorrespPlanException e) {
			manejarException(actionRequest,"TercNoCorrespPlan",
					"La tercerizadora ingresada no se corresponde con el plan. Por favor, verifíquelo.",e);
		} catch (FaltanDatosAfiliadoException e) {
			manejarException(actionRequest, "Invalido", "Complete todos los datos del Afiliado", e);
		} catch (FaltanDatosException e) {
			manejarException(actionRequest,"FaltanDatos","El plan, la tercerizadora o la situación laboral no están vigentes",e);
		} catch (FaltanSituacionesLaboralesException e) {
			manejarException(actionRequest, "FaltanSitus", "El laboral del Afiliado no puede ser vacío: Por favor, ingrese un laboral",e);
		} catch (Exception e) {
			_log.error("Error al guardar otros datos->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",e);
			manejarException(actionRequest, "FaltanDatos", "Error inesperado, contacte a sistemas.", e);
		}
		
	}

	private void manejarException(ActionRequest actionRequest,
			String attribute, String mensaje, Exception e) {
		if (attribute != null) {
			actionRequest.setAttribute(attribute, mensaje);
		}
		SessionErrors.add(actionRequest, e.getClass().getName());
		if (actionRequest.getParameter("tabs_a_mostrar") != null) {
			actionRequest.setAttribute("tabs_a_mostrar",
					actionRequest.getParameter("tabs_a_mostrar"));
		}
		if (actionRequest.getParameter(WebKeysAfiliados.DESDE_REINCORPORAR) != null) {
			actionRequest.setAttribute(WebKeysAfiliados.DESDE_REINCORPORAR,
					actionRequest.getParameter(WebKeysAfiliados.DESDE_REINCORPORAR));
		}

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		Afiliado afiliadoInSession = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
		session.setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL, afiliadoInSession.getLista_situ_laboral());
		_log.error("Error al guardar otros datos del afiliado: " + afiliadoInSession.getCuil_titular() + "/" + afiliadoInSession.getInte());
		_log.error("Error al guardar otros datos", e);
	}

	private boolean existeTercerizadoraVigente(
			List<AfiTercerizadoraServicio> tercerizadoras, Date vigen_fecha) {
		if (tercerizadoras == null) {
			return false;
		}
		int i = 0;
		boolean existeTercVigente = true;
		if (vigen_fecha == null) {
			vigen_fecha = new Date(System.currentTimeMillis());
		}
		boolean existeTercVigenteActual = false;
		boolean debeTenerVigenciaActual = false;
		if (DateUtils.compararFechasTruncarEnDia(vigen_fecha, new Date()) < 0) { // Hay
																					// una
																					// terc
																					// con
																					// fecha
																					// anterior
																					// a
																					// hoy...
			debeTenerVigenciaActual = true;
		}
		while (i < tercerizadoras.size()
				&& (!existeTercVigente || (debeTenerVigenciaActual && !existeTercVigenteActual))) {
//			if (!tercerizadoras.get(i).isBorradoLogico()
			if ((tercerizadoras.get(i).getEstado()==null ||
					(tercerizadoras.get(i).getEstado()!=null && !tercerizadoras.get(i).getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)))
					&& DateUtils
							.compararFechasTruncarEnDia(tercerizadoras.get(i)
									.getFechaInicioPres(), vigen_fecha) <= 0) { // No
																			// es
																			// borrado
																			// lógico
																			// y
																			// la
																			// fecha
																			// de
																			// ingreso
																			// es
																			// anterior
																			// a
																			// hoy.
				if (tercerizadoras.get(i).getFechaFinPres() == null) {
					existeTercVigente = true;
				} else if (tercerizadoras.get(i).getFechaFinPres() != null // Tiene
																			// fecha
																			// egreso
																			// y
																			// es
																			// posterior
																			// a
																			// fecha
																			// actual
						&& DateUtils.compararFechasTruncarEnDia(tercerizadoras
								.get(i).getFechaFinPres(), vigen_fecha) >= 0) {
					existeTercVigente = true;
				}
			}
			if (debeTenerVigenciaActual && !existeTercVigenteActual
//					&& !tercerizadoras.get(i).isBorradoLogico()) { 
					&& (tercerizadoras.get(i).getEstado()==null || 
						tercerizadoras.get(i).getEstado()!=null && !tercerizadoras.get(i).getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA))){
																	// No es
																	// borrado
																	// lógico
				if (DateUtils.compararFechasTruncarEnDia(tercerizadoras.get(i)
						.getFechaInicioPres(), vigen_fecha) <= 0) { // Fecha ingreso
																// anterior a
																// hoy
					if (tercerizadoras.get(i).getFechaFinPres() == null) { // Sin
																			// egreso
						existeTercVigenteActual = true;
					} else if (tercerizadoras.get(i).getFechaFinPres() != null
							&& DateUtils.compararFechasTruncarEnDia(
									tercerizadoras.get(i).getFechaFinPres(),
									vigen_fecha) >= 0) { // Tiene fecha de egreo
															// pero es posterior
															// a hoy
						existeTercVigenteActual = true;
					}
				}
			}
			i++;
		}

		boolean solapado = seSolapanVigencias(tercerizadoras);
		if (solapado) {
			return solapado;
		} else if (!debeTenerVigenciaActual) {
			return !existeTercVigente;
		} else {
			return !existeTercVigente || !existeTercVigenteActual;
		}
	}

//	FIXME: Revisar la misma funcion en TercerizadoraServiceUtil
	private boolean seSolapanVigencias(
			List<AfiTercerizadoraServicio> tercerizadoras) {
		boolean result = false;

		for (int i = 0; i < tercerizadoras.size(); i++) {
			AfiTercerizadoraServicio afit1 = tercerizadoras.get(i);
			for (int j = 0; j < tercerizadoras.size(); j++) {
				AfiTercerizadoraServicio afit2 = tercerizadoras.get(j);
//				if (!afit2.equals(afit1) && !afit1.isBorradoLogico() && !afit2.isBorradoLogico()) {
				if (!afit2.equals(afit1) 
						&& afit1.getEstado()!=null && !afit1.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA) 
						&& afit2.getEstado()!=null && !afit2.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA) ) {
					if (DateUtils.compararFechasTruncarEnDia(
							afit1.getFechaInicioPres(), afit2.getFechaInicioPres()) >= 0) {
						if (null == afit2.getFechaFinPres()) {
							result = true;
							break;
						}else if(null==afit1.getFechaFinPres() && DateUtils.compararFechasTruncarEnDia(
								afit2.getFechaFinPres(), afit1.getFechaInicioPres())>=0){
							result = false;
							break;
						}
					}
					if (DateUtils.compararFechasTruncarEnDia(
							afit1.getFechaInicioPres(), afit2.getFechaInicioPres()) <= 0) {
						if (null == afit1.getFechaFinPres()) {
							result = true;
							break;
						} else if (afit2.getFechaFinPres()==null && DateUtils.compararFechasTruncarEnDia(
								afit1.getFechaFinPres(), afit2.getFechaInicioPres())>=0 ){
							result= true;
							break;
						}
					}
				}
			}
			if(result){
				break;
			}

		}
		return result;
	}

	private void setearTab(ActionRequest actionRequest, Afiliado afiliado,
			String cuil_titular, String updateAfiBorrado) {
		
		if (updateAfiBorrado!= null && updateAfiBorrado.equals(Constants.UPDATE)) {
			actionRequest.setAttribute("tabs1", "informacion_general");	
		}else {
			actionRequest.setAttribute("tabs1", "informacion_adicional");
		}
		actionRequest.setAttribute("cuil_titular", cuil_titular);
		actionRequest.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
	}

//	private boolean planEsBaja(AfiAporteList afiAporteList,
//			AfiAporteList aportesNuevos, Date vigen_fecha) {
//		return planEsBaja(aportesNuevos, vigen_fecha);
//	}
//
//	private boolean planEsBaja(AfiAporteList afiAporteNuevo, Date vigen_fecha) {
//		if (afiAporteNuevo == null || afiAporteNuevo.getPlan() == null
//				|| afiAporteNuevo.getListaAportes() == null) { // NO TIENE
//																// APORTE NI
//																// PLAN
//			return true;
//		}
//		boolean existePlanVigente = false;
//		if (afiAporteNuevo.getMapAportes() != null) {
//			int j = 0;
//			if (null == vigen_fecha) { // SI NO TIENE VIGENCIA ES NUEVA
//				vigen_fecha = new Date(System.currentTimeMillis());
//			}
//			while (j < afiAporteNuevo.getListaAportes().size()
//					&& !existePlanVigente) {
//				for (AfiAporte aporte : afiAporteNuevo.getMapAportes().values()) {
//					if (DateUtils.compararFechasTruncarEnDia(
//							aporte.getFecha_ingre(), vigen_fecha) <= 0 // TIENE
//																		// UN
//																		// APORTE
//																		// ANTERIOR
//																		// A LA
//																		// FECHA
//																		// DE
//																		// VIGENCIA
//							&& (aporte.getFecha_egre() == null || DateUtils
//									.compararFechasTruncarEnDia(
//											aporte.getFecha_egre(), vigen_fecha) >= 0)) {
//						existePlanVigente = true;
//					}
//					j++;
//				}
//			}
//		}
//		return !existePlanVigente;
//	}

	private boolean existenSitusLaboralesVigente(List<SituacionLaboral> situLaborales, Date vigen_fecha) {
		
		if (situLaborales == null || situLaborales.isEmpty()) {
	        return true; // no hay situación laboral vigente
	    }
		
		int i = 0;
		boolean existeSituVigente = false;
		boolean existeSituVigenteActual = false;
		if (null == vigen_fecha) {
			vigen_fecha = new Date(System.currentTimeMillis());
		}
		boolean debeTenerVigenciaActual = false; // NO TIENE VIGENCIA ACTUAL
		if (DateUtils.compararFechasTruncarEnDia(vigen_fecha, new Date()) < 0) { // LA
																					// FECHA
																					// DE
																					// VIGENCIA
																					// DEL
																					// AFILIADO
																					// ES
																					// ANTERIOR
																					// AL
																					// DIA
																					// DE
																					// HOY
			debeTenerVigenciaActual = true;
		}
		while (i < situLaborales.size()
				&& (!existeSituVigente || (debeTenerVigenciaActual && !existeSituVigenteActual))) { // FECHA
																									// DESDE
																									// LABORAL
			if (DateUtils.compararFechasTruncarEnDia(situLaborales.get(i)
					.getFecha_ingre(), vigen_fecha) <= 0) { // LA SITUACION
															// LABORAL ES
															// ANTERIOR O IGUAL
															// A LA VIGENCIA DEL
															// AFILIADO
				if (situLaborales.get(i).getFecha_baja() == null
						&& situLaborales.get(i).getFecha_baja_logica() == null) { // EXISTE
																					// UNA
																					// SITU
																					// LABORAL
																					// SIN
																					// BAJA
																					// NI
																					// EGRESO
					existeSituVigente = true;
				} else if (situLaborales.get(i).getFecha_baja() != null
						&& DateUtils.compararFechasTruncarEnDia(situLaborales
								.get(i).getFecha_baja(), vigen_fecha) >= 0) { // EL
																				// EGRESO
																				// DE
																				// LA
																				// SITU
																				// LABORAL
																				// VIGENTE
																				// A
																				// LA
																				// FECHA
																				// DE
																				// VIGENCIA
																				// DEL
																				// AFILIADO
					existeSituVigente = true;
				} else if (situLaborales.get(i).getFecha_baja_logica() != null // LA
																				// BAJA
																				// DE
																				// LA
																				// SITU
																				// LABORAL
																				// VIGENTE
																				// A
																				// LA
																				// FECHA
																				// DE
																				// VIGENCIA
																				// DEL
																				// AFILIADO
						&& DateUtils.compararFechasTruncarEnDia(situLaborales
								.get(i).getFecha_baja_logica(), vigen_fecha) >= 0) {
					existeSituVigente = true;
				}
			}
			if (debeTenerVigenciaActual && !existeSituVigenteActual) { // FECHA
																		// HASTA
				if (DateUtils.compararFechasTruncarEnDia(situLaborales.get(i)
						.getFecha_ingre(), new Date()) <= 0) { // LA FECHA DE
																// INGRESO
																// LABORAL ES
																// MENOR A LA
																// FECHA DEL DIA
																// (NO FUTURA)
					if (situLaborales.get(i).getFecha_baja() == null
							&& situLaborales.get(i).getFecha_baja_logica() == null) { // LA
																						// SITU
																						// LABORAL
																						// NO
																						// TIENE
																						// EGRESO
																						// NI
																						// BAJA
						existeSituVigenteActual = true;
					} else if (situLaborales.get(i).getFecha_baja() != null
							&& DateUtils.compararFechasTruncarEnDia(
									situLaborales.get(i).getFecha_baja(),
									new Date()) < 0) { // NO HAY SITUACION
														// LABORAL VIGENTE AL
														// DIA DE LA FECHA
						// ME FIJO SI FUE DE BAJA POR DESEMPLEO O RENUNCIA
						if ((situLaborales.get(i).getMotivoBaja()
								.getId_motivo_baja() == 3 || situLaborales
								.get(i).getMotivoBaja().getId_motivo_baja() == 1)
								&& situLaborales.get(i).getId_categoria() == 11) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(situLaborales.get(i).getFecha_baja());
							cal.add(Calendar.MONTH, 3);
							Date fechaBaja3Meses = new Date(
									cal.getTimeInMillis());

							if (situLaborales.get(i).getFecha_baja() != null
									&& DateUtils.compararFechasTruncarEnDia(
											fechaBaja3Meses, new Date()) >= 0) { // DENTRO
																					// DE
																					// LOS
																					// 3
																					// MESES
																					// SIGUIENTES,
																					// TODAVIA
																					// ESTA
																					// VIGENTE
								existeSituVigenteActual = true;
							}
						} else { // SINO, ESTA DE BAJA!
							existeSituVigenteActual = false;
						}

					} else if (situLaborales.get(i).getFecha_baja_logica() == null
							&& situLaborales.get(i).getFecha_baja() != null
							&& DateUtils.compararFechasTruncarEnDia(
									situLaborales.get(i).getFecha_baja(),
									new Date()) >= 0) {
						existeSituVigenteActual = true;
					}
				}
			}
			i++;
		}
		if (!debeTenerVigenciaActual) {
			return !existeSituVigente;
		} else {
			return !existeSituVigente || !existeSituVigenteActual;
		}
	}

	private void message(ActionRequest actionRequest) {
		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest, "successMessage");
			SessionMessages.add(actionRequest, "request_processed", successMessage);
		}
	}
	
//	/**
//	 * Creo esta clase obtener informacion sobre la validez de los aportes del
//	 * afiliado a la fecha de vigencia del mismo. NO puede quedar dicha fecha
//	 * sin aportes. El problema con esto es que debo cruzar los datos de aportes
//	 * modificados con los de la base para saber si esta se viola esta
//	 * condicion.
//	 * 
//	 * @author martin
//	 * 
//	 */
//	public static class AportesYEgreso {
//		private int idAporte;
//		private Date fechaEgreso;
//
//		public AportesYEgreso() {
//		}
//
//		public AportesYEgreso(int idAporte) {
//			this.idAporte = idAporte;
//		}
//
//		public int getIdAporte() {
//			return idAporte;
//		}
//
//		public void setIdAporte(int idAporte) {
//			this.idAporte = idAporte;
//		}
//
//		public Date getFechaEgreso() {
//			return fechaEgreso;
//		}
//
//		public void setFechaEgreso(Date fechaEgreso) {
//			this.fechaEgreso = fechaEgreso;
//		}
//
//		public static AportesYEgreso getMapping(ResultSet rs)
//				throws SQLException {
//			AportesYEgreso apo = new AportesYEgreso();
//			apo.setFechaEgreso(rs.getDate("fecha_egreso"));
//			apo.setIdAporte(rs.getInt("id_aporte"));
//			return apo;
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + idAporte;
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			AportesYEgreso other = (AportesYEgreso) obj;
//			if (idAporte != other.idAporte)
//				return false;
//			return true;
//		}
//
//	}

	// Recupera el plan actual, vigente
	private AfiPlan getPlanActual(ActionRequest actionRequest, Afiliado afiInSession, boolean esBajaFutura){
		
		AfiPlan ap = null;	
		MotivoBaja motivoBaja = null;
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		String fechaVigenHastaDia = ParamUtil.getString(actionRequest, "fechaVigenHastaDia");
		String fechaVigenHastaMes = ParamUtil.getString(actionRequest, "fechaVigenHastaMes");
		String fechaVigenHastaAnio = ParamUtil.getString(actionRequest, "fechaVigenHastaAnio");
		Integer idMotivoBajaPlan = ParamUtil.getInteger(actionRequest, "motivoBajaPlan",0);
		
		String estado = ParamUtil.getString(actionRequest, "plan_estado");
		
		Date fechaVigenHasta = null;
		try {
//			fechaVigenDesde = formatoDeFecha.parse(fechaVigenDesdeDia + "/"
//					+ (Integer.parseInt(fechaVigenDesdeMes) + 1) + "/" +fechaVigenDesdeAnio);
			fechaVigenHasta = formatoDeFecha.parse(fechaVigenHastaDia+"/"+(Integer.parseInt(fechaVigenHastaMes)+1)+"/"+fechaVigenHastaAnio);
		} catch (Exception e) {
			fechaVigenHasta = null;
		}
		
		try {
//			hago esto porque la regla de alta x situ laboral cambia el plan actual provisoriamente, hasta borrar el plan cobertura
			if(!esBajaFutura){
				ap = planService.buscarUltimoPlanAportes(afiInSession.getCuil_titular());	
			}else{
				ap = planService.buscarPenultimoPlanAportes(afiInSession.getCuil_titular());
			}
			
			
			
			if(ap != null){
				ap.setInte(afiInSession.getInte());
				ap.setEstado(estado.equalsIgnoreCase(AfiPlan.ESTADOS.MODIFICADO.toString())?AfiPlan.ESTADOS.MODIFICADO:null);
//			si desean editar/cambiar un plan, se debe dar baja al plan actual, para poder cargarlo como nuevo plan... 
				if(fechaVigenHasta != null && idMotivoBajaPlan > 0){
					
					motivoBaja = new MotivoBaja(idMotivoBajaPlan, "");
					// ponemos motivo baja al plan del afiliado
					ap.setVigenHasta(fechaVigenHasta);
					ap.setMotivoBaja(motivoBaja);
					// ponemos motivo baja a todos los aportes del afiliado
					
					if(ap.getAportes()!=null) {
					 for (Iterator<AfiAportes> iterator = ap.getAportes().iterator(); iterator.hasNext();) {
						AfiAportes aa = iterator.next();
						aa.setFechaEgre(fechaVigenHasta);
						aa.setMotivoBaja(motivoBaja);
					 }
					} 
				}
				
			}//fin if(ap != null){
		
		} catch (Exception e) {
			return null;
		}
		
		return ap;
	}
	
	// Recupera el plan nuevo si hay baja del plan actual, o es reincorporacion sin continuidad o sin recuperar plan o alta de afiliado
	private AfiPlan getPlanNuevo(ActionRequest actionRequest, Afiliado afiliadoInSession){
		
		AfiPlan ap = null;
		Plan plan = null;
		List<TipoAporte> aportes;
		MotivoBaja motivoBaja = null;

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		Integer idPlan = ParamUtil.getInteger(actionRequest, "nuevoPlan",0);
		Integer idPlanOmint = ParamUtil.getInteger(actionRequest, "nuevoPlanOmintId");
		String fechaVigenDesdeStr = ParamUtil.getString(actionRequest, "nuevoPlanVigenDesde");
		String fechaVigenHastaDia = ParamUtil.getString(actionRequest, "nuevoPlanVigenHastaDia");
		String fechaVigenHastaMes = ParamUtil.getString(actionRequest, "nuevoPlanVigenHastaMes");
		String fechaVigenHastaAnio = ParamUtil.getString(actionRequest, "nuevoPlanVigenHastaAnio");
		String fechaVigenHastaStr = ParamUtil.getString(actionRequest, "nuevoPlanVigenHasta");
		Integer idMotivoBajaPlan = ParamUtil.getInteger(actionRequest, "motivoBajaPlanNuevo",0);
		
		if(idPlan == 0){ // No hubo Nuevo Plan, o cambio de plan
			// si genera el plan luego de la baja por regla 1, el idPlan del select de planes lo pierdo y puse un hidden para salvar esto
			idPlan = ParamUtil.getInteger(actionRequest, "nuevoPlanAutom",0); 
			if(idPlan == 0){
				return null;
			}	
		}
		
		ap = new AfiPlan();
		
		Date fechaVigenDesde = null;
		Date fechaVigenHasta = null;
		try {
			fechaVigenDesde = formatoDeFecha.parse(fechaVigenDesdeStr);
		} catch (Exception e) {
			fechaVigenDesde = null;
		}

		try {
			fechaVigenHasta = formatoDeFecha.parse(fechaVigenHastaDia + "/"
					+ (Integer.parseInt(fechaVigenHastaMes) + 1) + "/" +fechaVigenHastaAnio);
		} catch (Exception e) {
			fechaVigenHasta = null;
		}
		// pruebo si la fechaVigenHasta es null, podría ser que prepare el plan cobertura despues de la regla 1, y como
		// el calendar esta deshabilitado, tengo que tomar la fecha de baja del hidden
		if(fechaVigenHasta == null){
			try{
				fechaVigenHasta = formatoDeFecha.parse(fechaVigenHastaStr);
			} catch (Exception e) {
				fechaVigenHasta = null;
			}
		}
		if(fechaVigenHasta != null && idMotivoBajaPlan > 0){
			
			motivoBaja = new MotivoBaja(idMotivoBajaPlan, "");
		}	
		try {
			plan = planService.buscaPlanPorId(idPlan);
			aportes = planService.buscaAportesPorPlan(idPlan);
			plan.setAportes(aportes);
			
		} catch (Exception e) {
			return null;
		}
		ap.setPlan(plan);
		ap.setId_plan_omint(idPlanOmint);
		ap.setVigenDesde(fechaVigenDesde);
		ap.setVigenHasta(fechaVigenHasta);
		ap.setCuil_titular(afiliadoInSession.getCuil_titular());
		ap.setInte(afiliadoInSession.getInte());
		ap.setMotivoBaja(motivoBaja); // a veces null si es cambio plan o si al baja cascada propagamos el plan nuevo por regla 1
		
		return ap;
	}
	
	private int esContinuidad(Date vigen_fecha, Date vigenFechaOriginal) {     // ya se deberia ser boolean... :(
		// Valido las reglas necesarias para la recuperación solo un integrante
		int continuidad = 0;
		// dejo comentado esto, ahora la vigen_fecha nunca deberia llegar en
		// null (pueden ponerle cualquier vigen a la reincorporacion de un
		// integrante)
		// if (vigen_fecha == null) {
		// getInstance().reincorporarAfiliado(afiliado, null, fecha_egreso,
		// true, 1, usuario, id_motivo_baja_menor_edad, connection);
		// } else {
		_log.debug("Comparando: " + vigen_fecha + " - " + vigenFechaOriginal
				+ "para decidir si es con cont");
		int compare = ar.com.ospim.util.DateUtils.compararFechasTruncarEnDia(
				vigen_fecha, vigenFechaOriginal);
//		if (compare == 0) {
		if (compare <= 0) {  
// SVA: Agregamos el -1 donde coincide el caso que se desea incorporar con 
// continuidad pero la fecha se ingresa menor a la vigencia del afiliado que estaba en base
			
			continuidad = 1;
		}
		// Sí desea recuperar laborales y planes de beneficiario, recuperar
		// TRUE
		// Sí hay continuidad en las fechas, continuidad 1
		// No hay continuidad en las fechas, continuidad 0
		// No desea recuperar laborales y planes de beneficiario, recuperar
		// FALSE
		// if (continuidad == 0 || recuperar) {
		return continuidad;
	}
	
	private void enviarNovedadsobreAfiliadoConAntecJudiciales(String cuilTitular){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_LEGALES);

		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso cambios en afiliado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosJudicialGrupoFamiliar_"+cuilTitular+".xls");
		
	}
	
	private void enviarNovedadsobreAfiliadoDiscapacidad(String cuilTitular){

		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_DISCAPACIDAD);
	    //List<String> destinatarios = java.util.Arrays.asList("mauro.depascali@hotmail.com");

		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Baja de Afiliado Discapacitado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosGrupoFamiliar_"+cuilTitular+".xls");
	}

	private boolean estandeBajaLasSituacLaborales(List<SituacionLaboral> situsLaboral){
		boolean result = true;
		
		for (Iterator<SituacionLaboral> iterator = situsLaboral.iterator(); iterator.hasNext();) {
			SituacionLaboral sl = iterator.next();
			if(sl.getFecha_baja_logica() == null && sl.getFecha_baja() == null){
				result = false; // encontre al menos un no esta de baja
				break;
			}
		}
		
		return result;
	}
	
	private boolean estandeBajaLosPlanes(AfiPlan planActual, AfiPlan planNuevo){
		boolean result = false;
		
		if(planActual!= null && planActual.getVigenHasta() != null && planNuevo == null){
			result = true;
		}else if(planActual!= null &&  planActual.getVigenHasta() != null && planNuevo != null && planNuevo.getVigenHasta() != null){
			result = true;
		}else if(planActual== null && planNuevo != null && planNuevo.getVigenHasta() != null){ // solo para alta nuevo afi.
			result = true;
		}
		
		return result;
	}
	
	private boolean estandeBajaLasTercerizadoras(List<AfiTercerizadoraServicio> tercerizadoras){
		boolean result = true;
		
		for (Iterator<AfiTercerizadoraServicio> iterator = tercerizadoras.iterator(); iterator.hasNext();) {
			AfiTercerizadoraServicio ts = iterator.next();
			if(ts.getFechaFinPres() == null){
				result = false; // encontre al menos un no esta de baja
				break;
			}
		}
		
		return result;
	}
	
	private void validarDniBeneficiarioVigente(ActionRequest actionRequest, Afiliado afiliado)
	        throws SystemException {

	    if (afiliado == null) {
	        return;
	    }

	    String tipoDoc = afiliado.getDocumento_tipo();
	    String nroDoc = afiliado.getDocu_numero();

	    if (tipoDoc == null || nroDoc == null || nroDoc.trim().equals("")) {
	        return;
	    }

	    if ("ET".equalsIgnoreCase(tipoDoc)) {
	        return;
	    }

	    Afiliado existente = EditarAfiliadoServiceUtil.buscarBeneficiarioVigentePorDni(
	            tipoDoc,
	            nroDoc,
	            afiliado.getCuil_titular(),
	            afiliado.getInte(),
	            afiliado.getVigen_fecha()
	    );

	    if (existente != null) {
	        String mensaje = "El DNI " + nroDoc + " ya se encuentra activo en el grupo familiar "
	                + existente.getCuil_titular() + "-" + existente.getInte()
	                + " (" + existente.getApellido() + ", " + existente.getNombre() + ").";

	        actionRequest.setAttribute("Invalido", mensaje);
	        SessionErrors.add(actionRequest, FaltanDatosAfiliadoException.class.getName());
	    }
	}
}