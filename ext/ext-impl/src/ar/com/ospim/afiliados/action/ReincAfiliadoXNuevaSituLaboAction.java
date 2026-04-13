package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.PlanServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * BajaAfiliadoPlanReglasAction: intentamos tener validado la fecha de baja y a partir del motivo de baja, evaluamos las reglas
 * 
 * @author sergio
 *
 */

public class ReincAfiliadoXNuevaSituLaboAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ReincAfiliadoXNuevaSituLaboAction.class);
	
	private PlanServiceUtil planService = new PlanServiceUtil();
	
	final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
	
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		long diferencia = 0;
		String cuil = null;
		String fechaBaja = null, fechaInicioSituLabo = null, fechaDesdeAfiPlanCobertura;
		
		AfiPlan planActual, planNuevo = new AfiPlan(); // inicializamos

		Date afiBajaFechaFutura = null;
		Date afiPlanCoberturaDesdeFecha = null;
		Date fechaVigDesde = null;
//		Date fechaVigHasta = null;
		Calendar aux = Calendar.getInstance();
		
		try {
			cuil = renderRequest.getParameter("cuil").trim();
			fechaInicioSituLabo = renderRequest.getParameter("fecInicSituLabo").trim(); // dd/MM/yyyy
			fechaBaja = renderRequest.getParameter("fechaBajaAfiliado").trim(); // dd/MM/yyyy
			fechaDesdeAfiPlanCobertura = renderRequest.getParameter("afiPlanCoberturaVigDesde").trim(); //yyyy-MM-dd
			
			_log.debug("Fecha Baja Futura: "+fechaBaja );
			_log.debug("Fecha Vig Cobertura: "+fechaDesdeAfiPlanCobertura );
			_log.debug("Fecha Inicio SituLaboral: "+fechaInicioSituLabo );
			
			
			planNuevo.setCuil_titular(cuil);
			planNuevo.setInte(0);
			
			afiBajaFechaFutura = sdf.parse(fechaBaja);
			fechaVigDesde = sdf.parse(fechaInicioSituLabo);
			afiPlanCoberturaDesdeFecha = sdf2.parse(fechaDesdeAfiPlanCobertura);
			
			Afiliado afi = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			planActual = afi.getAfiPlan();
			
//			Evaluamos los casos posibles:
//			1) la afiBajaFechaFutura = fechaVigDesde + 1 dia (o sea al dia siguiente de la baja futura), entonces, 
//			   sería como reincorporar con continuidad, mantener ids socio q correspondan, esto es utilizando todo el periodo de 
//			   propagacion del plan cobertura/cober-usu;
			diferencia = ( afiBajaFechaFutura.getTime() - fechaVigDesde.getTime() )/MILLSECS_PER_DAY; 
			if(diferencia == -1){
//				es justo la diferencia para agregar un plan c contiduidad del plan propagado
				planNuevo.setVigenDesde(fechaVigDesde);
			}
//			2) la fecha inicio es igual a la fecha baja del afiliado + 1 dia, entonces, sería como reincorporar con continuidad, 
//			mantener ids socio q correspondan, esto significa que el periodo de propagacion del plan cobertura/cober-usu no fue utilizado, 

//			se debe borrar este plan y dejar situacion como cambio de plan referido al plan anterior, ojo que habria q levantar alguna baja de aporte 
//			del plan anterior...
			
			diferencia = ( afiPlanCoberturaDesdeFecha.getTime() - fechaVigDesde.getTime() )/MILLSECS_PER_DAY; 
//			baja del plan de cobertura, cuidado al volver atras el plan anterior, porque los ids pueden tener f baja
			if(diferencia == 0){
////			es justo la diferencia para agregar un plan c contiduidad del plan propagado
				planActual = planService.getInstance().buscarPenultimoPlanAportes(cuil);
				
				afi.setAfiPlan(planActual);
				
				planNuevo.setVigenDesde(fechaVigDesde);
				
				session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
				session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afi);
				
			}
//			3) la fecha inicio esta dentro de la baja de la ultima situ laboral y la baja futura, por lo tanto puede que haya consumido dias 
//			del plan cobertura, por lo que habria que actualizar la baja fecha del plan cobertura, y hacer un cambio de plan a partir de ahi.
			
			if(fechaVigDesde.after(afiPlanCoberturaDesdeFecha) && fechaVigDesde.before(afiBajaFechaFutura)){
				aux.setTime(fechaVigDesde);
				aux.add(Calendar.DATE, -1);
				planActual.setVigenHasta(aux.getTime());
				afi.setAfiPlan(planActual);
//				actualizaPlanActual = true;
				
				planNuevo.setVigenDesde(fechaVigDesde);
				
				session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
				session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afi);
			}
			
//			4) la fecha de inicio de la situ laboral, es superior a la fecha de vigen hasta del plan propagado:
//				que estamos haciendo futurologia? no paso la cobertura y el afiliado va a empezar a trabajar todavia mas a futuro????
//			    en todo caso, hay tiempo para reincorporarlo mas adelante... 
		
//		 PROXIMAS REGLAS
			
			session.setAttribute(WebKeysAfiliados.AFILIADO_BAJA_FUTURA, true);
			
		} catch (Exception e) {
			_log.error(e);
			return mapping.findForward("portlet.afiliados.error");
		}
//		List<AfiAportes> afiAportes = planService.getInstance().buscaUltimosIdsSocio(cuil) ;
		List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().consultaUltimosComponentesPlanVigente(cuil);

		//almaceno la lista en sesion
		renderRequest.setAttribute("IdsSocio", afiAportes); 
		
		session.setAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION, planNuevo);
		
		return mapping.findForward("portlet.afiliados.afi_plan");
	}

}