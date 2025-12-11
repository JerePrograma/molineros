package ar.com.ospim.afiliados.action;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.services.CambioHistoricoCoberturaServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author sergio
 *
 */

public class CambioHistoricoCoberturaAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(CambioHistoricoCoberturaAction.class);
		
	final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
//		setForward(actionRequest, "portlet.afiliados.result.search");
		_log.debug("entrando a CambioHistoricoCoberturaAction");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		User user = PortalUtil.getUser(renderRequest);

		String cmd = (String) ParamUtil.getString(renderRequest, "cmd_histo");
		Afiliado afi = null;
		List<AfiPlan> planesAfi = null;
		ArrayList<Plan> planes = (ArrayList<Plan>) session.getAttribute(WebKeysAfiliados.PLANES_EN_SESSION);
		ArrayList<TercerizadoraServicio> tercServList = (ArrayList<TercerizadoraServicio>) session.getAttribute(WebKeysAfiliados.TERCERIZADORAS_EN_SESSION);

		List<AfiTercerizadoraServicio> tercerizadorasAfi = null;
		Date vigenciaOriginal=null, vigenciaNueva = null, vigenAValidar=null, 
				bajaOriginal=null, bajaNueva=null, bajaAValidar=null;
		
		boolean sacarBajaAfi = false;
		
		afi = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
		
		if(StringUtils.checkEmpty(cmd)){
			planesAfi = PlanServiceUtil.traeHistoricoPlanes(afi.getCuil_titular());
			tercerizadorasAfi = TercerizadoraServiceUtil.traeHistoricoTercerizadoras(afi.getCuil_titular());
		}else{
			int i = 0;
			boolean noHayMasPlanes = false;
			boolean noHayMasTerc = false;
			
			planesAfi = new ArrayList<AfiPlan>();
			AfiPlan ap = null;
			AfiTercerizadoraServicio ats = null;
			String vigTitular = ParamUtil.getString(renderRequest, "vigen_fecha_titular");
			vigenciaOriginal = DateUtils.parse(vigTitular, DateUtils.SHORT);
			
			String vigNueDia = ParamUtil.getString(renderRequest, "fechaVigenDia");
			String vigNueMes = ParamUtil.getString(renderRequest, "fechaVigenMes");
			String vigNueAnio = ParamUtil.getString(renderRequest, "fechaVigenAnio");
			
			try {
				vigenciaNueva = DateUtils.parse(vigNueDia + "/"
						+ (Integer.parseInt(vigNueMes) + 1) + "/" + vigNueAnio , DateUtils.SHORT);
			} catch (Exception e) {
				vigenciaNueva = null; // No debería pasar esto...
			}
			
			String bajaTitular = ParamUtil.getString(renderRequest, "baja_fecha_titular",null);
			try{
				bajaOriginal = DateUtils.parse(bajaTitular, DateUtils.SHORT);
			}catch (ParseException e) {
				bajaOriginal = null;
			}	
			Integer idBajaTitular = ParamUtil.getInteger(renderRequest, "motivo_baja_titular");
			Integer idNuevoBajaTitular = ParamUtil.getInteger(renderRequest, "motivoBajaAfi");
			
			String bajaNueDia = ParamUtil.getString(renderRequest, "fechaBajaDia");
			String bajaNueMes = ParamUtil.getString(renderRequest, "fechaBajaMes");
			String bajaNueAnio = ParamUtil.getString(renderRequest, "fechaBajaAnio");
			
			try {
				bajaNueva = DateUtils.parse(bajaNueDia + "/"
						+ (Integer.parseInt(bajaNueMes) + 1) + "/" + bajaNueAnio , DateUtils.SHORT);
			} catch (Exception e) {
				bajaNueva = null;
			}
			
	//		planes viejos modificados
			while(!noHayMasPlanes){
			
				ap = getAfiPlanVigentes(renderRequest, afi, i, planes);
				i++;
				
				if(ap == null){
					noHayMasPlanes = true;
				}else{
					planesAfi.add(ap);
				}
			}	
			noHayMasPlanes = false;
			i = 0;
	//		planes nuevos
			while(!noHayMasPlanes){
				
				ap = getAfiPlanNuevo(renderRequest, afi, i, planes);
				i++;
				
				if(ap == null){
					noHayMasPlanes = true;
				}else{
					planesAfi.add(ap);
				}
			}
			
			tercerizadorasAfi = new ArrayList<AfiTercerizadoraServicio>();
			i = 0;
	//		terceriz vigentes
			while(!noHayMasTerc){
				
				ats = getAfiTercVigentes(renderRequest, afi, i, tercServList);
				i++;
				
				if(ats == null){
					noHayMasTerc = true;
				}else{
					tercerizadorasAfi.add(ats);
				}
			}
			
			i = 0;
			noHayMasTerc = false;
//			terceriz nuevas
			while(!noHayMasTerc){
				
				ats = getAfiTercNuevas(renderRequest, afi, i, tercServList);
				i++;
//				voy a darle oportunidad que el nuevo este intercalado entre otras tercerizadoras y no es la primera nueva
				if(i> 10 && ats == null){
					noHayMasTerc = true;
				}else if(ats != null){
					tercerizadorasAfi.add(ats);
				}
			}
		
			/*Validamos solapamientos*/
			String mensajeErrorPlan = PlanServiceUtil.seSolapanVigencias(planesAfi);
			String mensajeErrorTerc = TercerizadoraServiceUtil.seSolapanVigencias(tercerizadorasAfi);
			
			/*Validamos coherencia plan-terceriz*/
			String mensajeErrorVerifPlanTerc =  this.verificarPlanTercerizadora(afi.getCuil_titular(), planesAfi, tercerizadorasAfi);

			/*Validamos que un plan y una tercerizadora empiencen con la vigen desde del afiliado...*/
			if(vigenciaOriginal.equals(vigenciaNueva)){  
				vigenAValidar = vigenciaOriginal;
			}else{
				vigenAValidar = vigenciaNueva;
			}
			String mensajeErrorDesde = this.validarVigenDesde(vigenAValidar, planesAfi, tercerizadorasAfi);
			
//			revisar si estan intentando sacar la baja fecha del afiliado
			if(bajaOriginal!=null && bajaNueva==null){
				sacarBajaAfi = true;
				idNuevoBajaTitular = null;
			}
			/*Validamos que un plan y una tercerizadora terminen con la baja fecha (si tiene) del afiliado...*/
			if(bajaOriginal!=null && bajaOriginal.equals(bajaNueva)){  
				bajaAValidar = bajaOriginal;
			}else{
				bajaAValidar = bajaNueva;
			}
			
			String mensajeErrorHasta = this.validarVigenHasta(sacarBajaAfi, bajaAValidar, planesAfi, tercerizadorasAfi);
			
			if(bajaAValidar != null && StringUtils.checkNotEmpty(mensajeErrorHasta)){
//				baja anterior a vigen desde
				if(vigenAValidar.after(bajaAValidar)){
					mensajeErrorHasta = "La Baja del Afiliado no puede ser anterior a la Vigencia Inicial";
				}
			}
			
			if(StringUtils.checkNotEmpty(mensajeErrorPlan)){
				SessionErrors.add(renderRequest, "errorHisto1");
				renderRequest.setAttribute("msgError1",mensajeErrorPlan);
			}
			if(StringUtils.checkNotEmpty(mensajeErrorTerc)){
				SessionErrors.add(renderRequest, "errorHisto2");
				renderRequest.setAttribute("msgError2",mensajeErrorTerc);
			}
			if(StringUtils.checkNotEmpty(mensajeErrorVerifPlanTerc)){
				SessionErrors.add(renderRequest, "errorHisto3");
				renderRequest.setAttribute("msgError3",mensajeErrorVerifPlanTerc);
			}
			if(StringUtils.checkNotEmpty(mensajeErrorDesde)){
				SessionErrors.add(renderRequest, "errorHisto4");
				renderRequest.setAttribute("msgError4",mensajeErrorDesde);
			}
			if(StringUtils.checkNotEmpty(mensajeErrorHasta)){
				SessionErrors.add(renderRequest, "errorHisto5");
				renderRequest.setAttribute("msgError5",mensajeErrorHasta);
			}
			
		
			/* guardar los cambios*/
			if(SessionErrors.isEmpty(renderRequest)){	
				
				CambioHistoricoCoberturaServiceUtil service = new CambioHistoricoCoberturaServiceUtil();
			
				boolean resultado = service.aplicarCambios(afi, planesAfi, tercerizadorasAfi, (!vigenciaOriginal.equals(vigenciaNueva)), vigenciaNueva,
						(bajaOriginal!=null && !bajaOriginal.equals(bajaNueva)), bajaNueva, idBajaTitular, idNuevoBajaTitular, user);
				
				if(resultado){
					String successMessage = ParamUtil.getString(renderRequest,"successMessage");
					SessionMessages.add(renderRequest, "request_processed", successMessage);
				}
				planesAfi = PlanServiceUtil.traeHistoricoPlanes(afi.getCuil_titular());
				
				tercerizadorasAfi = TercerizadoraServiceUtil.traeHistoricoTercerizadoras(afi.getCuil_titular());
				
				session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
				afi = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(afi.getCuil_titular(), afi.getInte());
				session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afi);
			}
			/* */
		} // fin if(StringUtils.checkEmpty(cmd))
		
		
		renderRequest.setAttribute("planesHistorico", planesAfi);
		renderRequest.setAttribute("tercerizadorasHistorico", tercerizadorasAfi);
		
		renderRequest.setAttribute("tabs1", "cambios_cobertura");
		renderRequest.setAttribute("mostrar_tab_cambio_historico", "mostrar");
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.afiliados.editar_afiliado_entry"));

	}
	
	private AfiPlan getAfiPlanNuevo(RenderRequest renderRequest, Afiliado afi, int i, ArrayList<Plan> planes){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		BigInteger id = null;
			try{
				id = new BigInteger(ParamUtil.getString(renderRequest, "nuevo_idSerial_"+i));
			}catch (Exception e) {
//				llegue al indice donde no hay mas elementos?
				return null;
			}
				
		String estado = ParamUtil.getString(renderRequest, "nuevo_estado_"+i);
		int idPlan = Integer.parseInt(ParamUtil.getString(renderRequest, "nuevo_plan_"+i,"0"));	
		if(idPlan==0){
			return null;
		}
		Plan p = null;
		p = new Plan(idPlan, "");
		int pos = planes.indexOf(p);
		p = planes.get(pos);
		
		Integer idPlanOmint = null;	
		try{
			idPlanOmint = Integer.parseInt(ParamUtil.getString(renderRequest, "nuevoPlanOmintId_"+i,null));
		}catch (NumberFormatException e) {
			idPlanOmint = null;
		}
		Integer idMotivoBaja = null;
		try{
			idMotivoBaja = Integer.parseInt(ParamUtil.getString(renderRequest, "nuevo_motivoBajaPlan_"+i,null));
		}catch (NumberFormatException e) {
			idMotivoBaja = null;
		}
		String fechaVigenDesdeDia = ParamUtil.getString(renderRequest, "nuevo_fechaVigenDesdeDia_"+i);
		String fechaVigenDesdeMes = ParamUtil.getString(renderRequest, "nuevo_fechaVigenDesdeMes_"+i);
		String fechaVigenDesdeAnio = ParamUtil.getString(renderRequest, "nuevo_fechaVigenDesdeAnio_"+i);
		Date fechaVigenDesde = null;
		try {
			fechaVigenDesde = sdf.parse(fechaVigenDesdeDia + "/"
					+ (Integer.parseInt(fechaVigenDesdeMes) + 1) + "/" + fechaVigenDesdeAnio);
		} catch (Exception e) {
			fechaVigenDesde = null;
		}
		String fechaVigenHastaDia = ParamUtil.getString(renderRequest, "nuevo_fechaVigenHastaDia_"+i);
		String fechaVigenHastaMes = ParamUtil.getString(renderRequest, "nuevo_fechaVigenHastaMes_"+i);
		String fechaVigenHastaAnio = ParamUtil.getString(renderRequest, "nuevo_fechaVigenHastaAnio_"+i);
		Date fechaVigenHasta = null;
		try {
			fechaVigenHasta = sdf.parse(fechaVigenHastaDia + "/"
					+ (Integer.parseInt(fechaVigenHastaMes) + 1) + "/" + fechaVigenHastaAnio);
		} catch (Exception e) {
			fechaVigenHasta = null;
		}
		AfiPlan ap = new AfiPlan();
//		Plan p = null;
//		p = new Plan(idPlan, "");
		if(idPlanOmint != null){
			p.setId_plan_omint(idPlanOmint);
			ap.setId_plan_omint(idPlanOmint);
		}
		ap.setId(id);
		ap.setPlan(p);
		if(idMotivoBaja != null){
			ap.setMotivoBaja(new MotivoBaja(idMotivoBaja, ""));
		}		
		ap.setVigenDesde(fechaVigenDesde);
		ap.setVigenHasta(fechaVigenHasta);
		ap.setCuil_titular(afi.getCuil_titular());
		ap.setInte(afi.getInte());
		
//		if(estado.equalsIgnoreCase("baja")){
//			ap.setBajaFecha(new Date());
//		}
		if(estado.equalsIgnoreCase(AfiPlan.ESTADOS.ALTA.toString())){
			ap.setEstado(AfiPlan.ESTADOS.ALTA);
		}
		return ap;
	}
	private AfiPlan getAfiPlanVigentes(RenderRequest renderRequest, Afiliado afi, int i, ArrayList<Plan> planes){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		BigInteger id = new BigInteger(ParamUtil.getString(renderRequest, "idSerial_"+i));
		BigInteger id = null;
		try{
			id = new BigInteger(ParamUtil.getString(renderRequest, "idSerial_"+i));
		}catch (Exception e) {
//			llegue al indice donde no hay mas elementos?
			return null;
		}
		String estado = ParamUtil.getString(renderRequest, "estado_"+i);
		String bajaAfiPlan = ParamUtil.getString(renderRequest, "baja_fecha_"+i);
		Date bajaFecha = null;
		try {
			bajaFecha = DateUtils.parse(bajaAfiPlan, DateUtils.SHORT);
		} catch (ParseException e1) {
			bajaFecha = null;
		}
		int idPlan = Integer.parseInt(ParamUtil.getString(renderRequest, "plan_"+i,"0"));
		if(idPlan == 0){
			idPlan = Integer.parseInt(ParamUtil.getString(renderRequest, "aux_plan_"+i,"0"));
		}
		Plan p = new Plan(idPlan,"");
		int pos = planes.indexOf(p);
		p = planes.get(pos);
		Integer idPlanOmint = null; 	
		try{
			idPlanOmint = Integer.parseInt(ParamUtil.getString(renderRequest, "planOmintId_"+i,null));
		}catch (NumberFormatException e) {
			idPlanOmint = null;
		}
		
		Integer idMotivoBaja = null;
		try{
			idMotivoBaja = Integer.parseInt(ParamUtil.getString(renderRequest, "motivoBajaPlan_"+i,null));
		}catch (NumberFormatException e) {
			idMotivoBaja = null;
		}
		String fechaVigenDesdeDia = ParamUtil.getString(renderRequest, "fechaVigenDesdeDia_"+i);
		String fechaVigenDesdeMes = ParamUtil.getString(renderRequest, "fechaVigenDesdeMes_"+i);
		String fechaVigenDesdeAnio = ParamUtil.getString(renderRequest, "fechaVigenDesdeAnio_"+i);
		Date fechaVigenDesde = null;
		try {
			fechaVigenDesde = sdf.parse(fechaVigenDesdeDia + "/"
					+ (Integer.parseInt(fechaVigenDesdeMes) + 1) + "/" + fechaVigenDesdeAnio);
		} catch (Exception e) {
			String vigenDesde = ParamUtil.getString(renderRequest, "vigen_desde_"+i);
			try {
				fechaVigenDesde = DateUtils.parse(vigenDesde, DateUtils.SHORT);
			} catch (ParseException e1) {
				fechaVigenDesde = null;
			}
		}
		String fechaVigenHastaDia = ParamUtil.getString(renderRequest, "fechaVigenHastaDia_"+i);
		String fechaVigenHastaMes = ParamUtil.getString(renderRequest, "fechaVigenHastaMes_"+i);
		String fechaVigenHastaAnio = ParamUtil.getString(renderRequest, "fechaVigenHastaAnio_"+i);
		Date fechaVigenHasta = null;
		try {
			fechaVigenHasta = sdf.parse(fechaVigenHastaDia + "/"
					+ (Integer.parseInt(fechaVigenHastaMes) + 1) + "/" + fechaVigenHastaAnio);
		} catch (Exception e) {
			if(bajaFecha!=null){
				String vigenHasta = ParamUtil.getString(renderRequest, "vigen_hasta_"+i);
				try {
					fechaVigenHasta = DateUtils.parse(vigenHasta, DateUtils.SHORT);
				} catch (ParseException e1) {
					fechaVigenHasta = null;
				}
			}
			fechaVigenHasta = null;
		}
		AfiPlan ap = new AfiPlan();
		ap.setId(id);
//		Plan p = null;
//		p = new Plan(idPlan, "");
		if(idPlanOmint != null){
			p.setId_plan_omint(idPlanOmint);
			ap.setId_plan_omint(idPlanOmint);
		}
		ap.setPlan(p);
		if(idMotivoBaja != null){
			ap.setMotivoBaja(new MotivoBaja(idMotivoBaja, ""));
		}
		ap.setVigenDesde(fechaVigenDesde);
		ap.setVigenHasta(fechaVigenHasta);
		ap.setCuil_titular(afi.getCuil_titular());
		ap.setInte(afi.getInte());
		
		if(estado.equalsIgnoreCase(AfiPlan.ESTADOS.BAJA.toString())){
			ap.setEstado(AfiPlan.ESTADOS.BAJA);
			ap.setBajaFecha(new Date());
		}else if(estado.equalsIgnoreCase(AfiPlan.ESTADOS.MODIFICADO.toString())){
			ap.setEstado(AfiPlan.ESTADOS.MODIFICADO);
		}else if(estado.equalsIgnoreCase(AfiPlan.ESTADOS.ALTA.toString())){
			ap.setEstado(AfiPlan.ESTADOS.ALTA);	
		}else{
			ap.setBajaFecha(bajaFecha);
		}
		
		return ap;
	}
	private AfiTercerizadoraServicio getAfiTercVigentes(RenderRequest renderRequest, Afiliado afi, int i, ArrayList<TercerizadoraServicio> tercerizadoras){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		BigInteger id = null;
		try{
			id = new BigInteger(ParamUtil.getString(renderRequest, "idTercSerial_"+i));
		}catch (Exception e) {
//			llegue al indice donde no hay mas elementos?
			return null;
		}
		String estado = ParamUtil.getString(renderRequest, "tercEstado_"+i);
		String bajaTercPlan = ParamUtil.getString(renderRequest, "terc_baja_fecha_"+i);
		Date bajaFecha = null;
		try {
			bajaFecha = DateUtils.parse(bajaTercPlan, DateUtils.SHORT);
		} catch (ParseException e1) {
			bajaFecha = null;
		}
		String idTerc = ParamUtil.getString(renderRequest, "tercerizadora_"+i);	
		
		String fechaInicioDia = ParamUtil.getString(renderRequest, "fechaInicioDia_"+i);
		String fechaInicioMes = ParamUtil.getString(renderRequest, "fechaInicioMes_"+i);
		String fechaInicioAnio = ParamUtil.getString(renderRequest, "fechaInicioAnio_"+i);
		Date fechaInicio = null;
		try {
			fechaInicio = sdf.parse(fechaInicioDia + "/"
					+ (Integer.parseInt(fechaInicioMes) + 1) + "/" + fechaInicioAnio);
		} catch (Exception e) {
			String auxFechaInicio = ParamUtil.getString(renderRequest, "aux_ini_fecha_"+i);
			try {
				idTerc = ParamUtil.getString(renderRequest, "aux_tercerizadora_"+i);
				fechaInicio = DateUtils.parse(auxFechaInicio, DateUtils.SHORT);
			} catch (ParseException e1) {
				fechaInicio = null;
			}
		}
		TercerizadoraServicio t = new TercerizadoraServicio(idTerc);
		int pos = tercerizadoras.indexOf(t);
		t = tercerizadoras.get(pos);
		
		String fechaFinDia = ParamUtil.getString(renderRequest, "fechaFinDia_"+i);
		String fechaFinMes = ParamUtil.getString(renderRequest, "fechaFinMes_"+i);
		String fechaFinAnio = ParamUtil.getString(renderRequest, "fechaFinAnio_"+i);
		Date fechaFin = null;
		try {
			fechaFin = sdf.parse(fechaFinDia + "/"
					+ (Integer.parseInt(fechaFinMes) + 1) + "/" + fechaFinAnio);
		} catch (Exception e) {
			String auxFechaFin = ParamUtil.getString(renderRequest, "aux_fin_fecha_"+i);
			try {
				fechaFin = DateUtils.parse(auxFechaFin, DateUtils.SHORT);
			} catch (ParseException e1) {
				fechaFin = null;
			}
		}
		AfiTercerizadoraServicio ats = new AfiTercerizadoraServicio();
		ats.setTercerizadora(t);
		ats.setFechaInicioPres(fechaInicio); 
		ats.setFechaFinPres(fechaFin);
		ats.setId(id);
		ats.setAfiliado(afi);
		
		if(estado.equalsIgnoreCase(AfiTercerizadoraServicio.ESTADOS.BAJA.toString())){
			ats.setEstado(AfiTercerizadoraServicio.ESTADOS.BAJA);
			ats.setBajaFecha(new Date());
		}else if(estado.equalsIgnoreCase(AfiTercerizadoraServicio.ESTADOS.MODIFICADO.toString())){
			ats.setEstado(AfiTercerizadoraServicio.ESTADOS.MODIFICADO);
		}else if(estado.equalsIgnoreCase(AfiTercerizadoraServicio.ESTADOS.ALTA.toString())){
			ats.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);	
		}else{
			ats.setBajaFecha(bajaFecha);
		}
		return ats;
	}
	private AfiTercerizadoraServicio getAfiTercNuevas(RenderRequest renderRequest, Afiliado afi, int i, ArrayList<TercerizadoraServicio> tercerizadoras){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		BigInteger id = null;
		try{
			id = new BigInteger(ParamUtil.getString(renderRequest, "nuevo_idTercSerial_"+i));
		}catch (Exception e) {
//			llegue al indice donde no hay mas elementos?
			return null;
		}
		String estado = ParamUtil.getString(renderRequest, "nuevo_tercEstado_"+i);
		String idTerc = ParamUtil.getString(renderRequest, "nuevo_tercerizadora_"+i);	
		if(idTerc==null || idTerc.length() == 0){
			return null;
		}
		TercerizadoraServicio t = new TercerizadoraServicio(idTerc);
		int pos = tercerizadoras.indexOf(t);
		t = tercerizadoras.get(pos);
		
		String fechaInicioDia = ParamUtil.getString(renderRequest, "nuevo_fechaInicioDia_"+i);
		String fechaInicioMes = ParamUtil.getString(renderRequest, "nuevo_fechaInicioMes_"+i);
		String fechaInicioAnio = ParamUtil.getString(renderRequest, "nuevo_fechaInicioAnio_"+i);
		Date fechaInicio = null;
		try {
			fechaInicio = sdf.parse(fechaInicioDia + "/"
					+ (Integer.parseInt(fechaInicioMes) + 1) + "/" + fechaInicioAnio);
		} catch (Exception e) {
			fechaInicio = null;
		}
		String fechaFinDia = ParamUtil.getString(renderRequest, "nuevo_fechaFinDia_"+i);
		String fechaFinMes = ParamUtil.getString(renderRequest, "nuevo_fechaFinMes_"+i);
		String fechaFinAnio = ParamUtil.getString(renderRequest, "nuevo_fechaFinAnio_"+i);
		Date fechaFin = null;
		try {
			fechaFin = sdf.parse(fechaFinDia + "/"
					+ (Integer.parseInt(fechaFinMes) + 1) + "/" + fechaFinAnio);
		} catch (Exception e) {
			fechaFin = null;
		}
		AfiTercerizadoraServicio ats = new AfiTercerizadoraServicio();
		ats.setTercerizadora(t);
		ats.setFechaInicioPres(fechaInicio); 
		ats.setFechaFinPres(fechaFin);
		ats.setId(id);
		ats.setAfiliado(afi);
		
		if(estado.equalsIgnoreCase(AfiTercerizadoraServicio.ESTADOS.ALTA.toString())){
			ats.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
		}
		return ats;
	}
	
//	A partir del corte con Omint, las tercerizadoras tendran fecha de corte o inicio, 
//	para ello verificaremos que corresponda al intervalo
	public String verificarPlanTercerizadora(String cuilTitular,
			List<AfiPlan> afiPlanes, List<AfiTercerizadoraServicio> afiTercerizadoras) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String mensaje = "No se pudo verificar un plan y su correspondiente tercerizadora";
		ArrayList<TercerizadoraServicio> tercerizadorasPlan = null;
		HashMap<String, TercerizadoraServicio> auxiTerc = null;

		// 1ero recorremos los planes para ver que tercerizadoras corresponden x
		// plan
		int i = 0;
		for (Iterator<AfiPlan> iterator = afiPlanes.iterator(); iterator
				.hasNext();) {
			AfiPlan afiPlan = iterator.next();
			
			if(afiPlan.getBajaFecha() != null || (afiPlan.getEstado()!=null && afiPlan.getEstado().equals(AfiPlan.ESTADOS.BAJA))
					|| afiPlan.getPlan().getId() == 27){
//				no vamos a analizar planes marcados de baja o que ya estan de baja y NO es PLAN DESCONOCIDO (no existe tercerizadora DESCONOCIDO)
				continue;
			}
			_log.debug(" Analizando afi-plan " + afiPlan.getId() + " Plan: " + afiPlan.getPlan().getDescripcion());
			
			tercerizadorasPlan = (ArrayList<TercerizadoraServicio>) TercerizadoraServiceUtil.getInstance()
					.getTercerizadoraPlan(afiPlan.getPlan().getId());

			auxiTerc = new HashMap<String, TercerizadoraServicio>();

			for (TercerizadoraServicio tercServ : tercerizadorasPlan) {
				auxiTerc.put(tercServ.getId_tercerizadora(), tercServ);
			}

			boolean existeCoberturaTercerizadoraParaElPlan = false;
			i=0;
			TercerizadoraServicio terServAux = null;
			
			while (i < afiTercerizadoras.size() 
					&& !existeCoberturaTercerizadoraParaElPlan) {
				
				AfiTercerizadoraServicio terc = afiTercerizadoras.get(i);
				
				_log.debug(" Analizando afi-tercerizadora "+ terc.getId() + " Tercerizadora: " + terc.getTercerizadora().getId_tercerizadora());
				
//				1), revisar que alguna de las tercerizadoras del afiliado,
//				este en la lista de tercerizadoras correspondientes al plan
//				2),evaluar fechas: 
//				2a)si encuentra una tercerizadora que este en el rango de fechas de las tercerizadoras auxiliares por plan
//				2b)evaluar fecha desde y hasta del plan con la tercerizadora.
				if( (terc.getBajaFecha()==null && 
						(terc.getEstado()==null 
						|| (terc.getEstado()!=null && !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA) ))) 
						&& auxiTerc.containsKey(terc.getTercerizadora().getId_tercerizadora())){
//					2a)
					terServAux = auxiTerc.get(terc.getTercerizadora().getId_tercerizadora());
					if(terServAux.getFechaFin()!=null){
						if(terc.getFechaFinPres() == null){
							mensaje = "La tercerizadora " + terc.getTercerizadora().getId_tercerizadora() +" no respeta el corte de vigencia fin("+ 
									sdf.format(terServAux.getFechaFin()) +") de la configuracion de tercerizadora";
							return mensaje;
						}
					}
					if(terServAux.getFechaInicio()!=null){	
						if(terc.getFechaInicioPres() == null || 
								(DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), terServAux.getFechaInicio()) < 0)){
							mensaje = "La tercerizadora " + terc.getTercerizadora().getId_tercerizadora() +" no respeta el corte de vigencia inicio("+ 
									sdf.format(terServAux.getFechaInicio()) +") de la configuracion de tercerizadora";
							return mensaje;
						}
					}
//					2b)
//					int java.util.Calendar.compareTo(Calendar anotherCalendar)
//					public int compareTo(Calendar anotherCalendar) Compares the time values (millisecond offsets from 
//					 the Epoch) represented by two Calendar objects. 
//					Specified by:
//						compareTo in interface Comparable<Calendar>
//					Parameters:
//						anotherCalendar - the Calendar to be compared.
//					Returns:
//						the value 0 if the time represented by the argument is equal to the time represented by this 
//						 Calendar; a value less than 0 if the time of this Calendar is before the time represented by the 
//						 argument; and a value greater than 0 if the time of this Calendar is after the time represented by the 
//						 argument.
					if(terc.getFechaFinPres()==null 
							&& afiPlan.getVigenHasta()==null
							&& (DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), afiPlan.getVigenDesde()) <= 0) 
					  ){
						existeCoberturaTercerizadoraParaElPlan = true;
					
					}else if(terc.getFechaFinPres()!=null 
							&& terServAux.getFechaFin()!=null
//							&& DateUtils.compararFechasTruncarEnDia(terc.getFechaFinPres(), terServAux.getFechaFin()) == 0
							&& DateUtils.compararFechasTruncarEnDia(terc.getFechaFinPres(), terServAux.getFechaFin()) <= 0
							&& (afiPlan.getVigenHasta()==null 
								|| (afiPlan.getVigenHasta()!=null 
								&& DateUtils.compararFechasTruncarEnDia(afiPlan.getVigenHasta(),terc.getFechaFinPres()) >= 0))
							&& (DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), afiPlan.getVigenDesde()) <= 0) 
					  ){
						existeCoberturaTercerizadoraParaElPlan = true;
					}else if(terc.getFechaFinPres()!=null 
							&& terServAux.getFechaFin()!=null
							&& DateUtils.compararFechasTruncarEnDia(terc.getFechaFinPres(), terServAux.getFechaFin()) <= 0
							&& (afiPlan.getVigenHasta()==null 
								|| (afiPlan.getVigenHasta()!=null 
								&& DateUtils.compararFechasTruncarEnDia(afiPlan.getVigenHasta(),terc.getFechaFinPres()) >= 0)
								|| (afiPlan.getVigenHasta()!=null 
								&& DateUtils.compararFechasTruncarEnDia(afiPlan.getVigenHasta(),terServAux.getFechaFin()) <= 0)
								)
							&& (DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), afiPlan.getVigenDesde()) <= 0) 
					  ){
						existeCoberturaTercerizadoraParaElPlan = true;	
					}else if(terc.getFechaFinPres()==null 
							&& terServAux.getFechaFin()==null
							&& (afiPlan.getVigenHasta()==null 
								|| (afiPlan.getVigenHasta()!=null 
								&& DateUtils.compararFechasTruncarEnDia(afiPlan.getVigenHasta(),terc.getFechaInicioPres()) >= 0))
							&& (DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), afiPlan.getVigenDesde()) >= 0) 
					  ){
						existeCoberturaTercerizadoraParaElPlan = true;
					}else if(terc.getFechaFinPres()!=null 
							&& terServAux.getFechaFin()==null
							&& (afiPlan.getVigenHasta()!=null 
								&& DateUtils.compararFechasTruncarEnDia(terc.getFechaFinPres(),afiPlan.getVigenHasta()) >= 0)
							&& (DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), afiPlan.getVigenDesde()) <= 0) 
					  ){
						existeCoberturaTercerizadoraParaElPlan = true;
					}else if(terc.getFechaFinPres()==null 
							&& terServAux.getFechaFin()==null
							&& afiPlan.getVigenHasta()!=null 
							&& (DateUtils.compararFechasTruncarEnDia(terc.getFechaInicioPres(), afiPlan.getVigenDesde()) <= 0) 
					  ){
						existeCoberturaTercerizadoraParaElPlan = true;
					}		
					
						
					
					
				}// si no lo contiene puede ser otra la tercerizadora a analizar, seguimos revisando las terc del afi.
				i++;
			}
			if(!existeCoberturaTercerizadoraParaElPlan){
				mensaje = "No se encontró una tercerizadora para el plan " + afiPlan.getPlan().getDescripcion() +
						" con vigencia desde " + sdf.format(afiPlan.getVigenDesde());
				return mensaje;
			}

		}

		return null;
	}
	
	private String validarVigenDesde(Date vigenAValidar, List<AfiPlan> planes, List<AfiTercerizadoraServicio> tercerizadoras){
		
		String mensajeError = "";
		boolean estaPlanMismaVigDesde = false;
		boolean estaTercerizadoraMismoInicioPres = false;
		
		for (int i = 0; i < planes.size(); i++) {
			AfiPlan ap = planes.get(i);
			if(ap.getBajaFecha() == null && ap.getVigenDesde().equals(vigenAValidar)){
				estaPlanMismaVigDesde = true;
				break;
			}
		}	
		
		for (int i = 0; i < tercerizadoras.size(); i++) {
			AfiTercerizadoraServicio ats = tercerizadoras.get(i);
//			if(!ats.isBorradoLogico()
			if(ats.getBajaFecha() == null &&
				(ats.getEstado() == null || 
				(ats.getEstado() != null && !ats.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)))
					&& ats.getFechaInicioPres().equals(vigenAValidar)){
				estaTercerizadoraMismoInicioPres = true;
				break;
			}
		}	
		
		if(!estaPlanMismaVigDesde){
			mensajeError = "El primer plan de cobertura no comienza al mismo tiempo que la vigencia del afiliado";
		}
		if(!estaTercerizadoraMismoInicioPres){
			mensajeError = "La primer tercerizadora no comienza al mismo tiempo que la vigencia del afiliado";
		}
		return mensajeError;
	}
	
	private String validarVigenHasta(boolean sacarBajaAfi, Date bajaAValidar, List<AfiPlan> planes, List<AfiTercerizadoraServicio> tercerizadoras){
			
		String mensajeError = "";
		boolean estaPlanMismaVigHasta = false;
		boolean estaTercerizadoraMismoFinPres = false;
		
		for (int i = 0; i < planes.size(); i++) {
			AfiPlan ap = planes.get(i);
			
			if(sacarBajaAfi){
				if(ap.getBajaFecha() == null && ap.getVigenHasta() == null){
					if(estaPlanMismaVigHasta){
						mensajeError = "Existe mas de 1 plan de cobertura que finaliza al mismo tiempo que la baja del afiliado (Solapado)";
					}
					estaPlanMismaVigHasta = true;
				}
			}else{
				if(bajaAValidar == null){
					if(ap.getBajaFecha() == null 
							&& ap.getVigenHasta()==null){
						if(estaPlanMismaVigHasta){
							mensajeError = "Existe mas de 1 plan de cobertura que finaliza al mismo tiempo que la baja del afiliado (Solapado)";
						}
						estaPlanMismaVigHasta = true;
	//					break;
					}
				}else{
					if(ap.getBajaFecha() == null 
							&& ap.getVigenHasta()!=null 
							&& ap.getVigenHasta().equals(bajaAValidar)){
						if(estaPlanMismaVigHasta){
							mensajeError = "Existe mas de 1 plan de cobertura que finaliza al mismo tiempo que la baja del afiliado (Solapado)";
						}
						estaPlanMismaVigHasta = true;
	//					break;
					}
				}	
			}
			
		}	
		
		for (int i = 0; i < tercerizadoras.size(); i++) {
			AfiTercerizadoraServicio ats = tercerizadoras.get(i);
			
			if(sacarBajaAfi){
				if(ats.getBajaFecha() == null && ats.getFechaFinPres() == null){
					if(estaTercerizadoraMismoFinPres){
						mensajeError = "Existe mas de 1 tercerizadora que finaliza al mismo tiempo que la baja del afiliado (Solapado)";
					}
					estaTercerizadoraMismoFinPres = true;
				}
			}else{
				if(bajaAValidar == null){
					if(ats.getBajaFecha() == null &&
							(ats.getEstado() == null || 
							(ats.getEstado() != null && !ats.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)))
								&& ats.getFechaFinPres() == null){
							estaTercerizadoraMismoFinPres = true;
//							break;
						}
				}else{
					if(ats.getBajaFecha() == null &&
						(ats.getEstado() == null || 
						(ats.getEstado() != null && !ats.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)))
							&& ats.getFechaFinPres() !=null 
							&& ats.getFechaFinPres().equals(bajaAValidar)){
						estaTercerizadoraMismoFinPres = true;
	//					break;
					}
				}	
			}	
		}	
		
		if(!estaPlanMismaVigHasta){
			mensajeError = "El último plan de cobertura no finaliza al mismo tiempo que la baja del afiliado";
		}
		if(!estaTercerizadoraMismoFinPres){
			mensajeError = "La última tercerizadora no finaliza al mismo tiempo que la baja del afiliado";
		}
		return mensajeError;
	}
}