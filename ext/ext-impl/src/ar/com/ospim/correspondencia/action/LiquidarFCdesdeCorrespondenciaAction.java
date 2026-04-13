package ar.com.ospim.correspondencia.action;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.persistence.PortletUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class LiquidarFCdesdeCorrespondenciaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(LiquidarFCdesdeCorrespondenciaAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
//		url de alta de liq
//		http://localhost:8080/web/guest/liquidaciones?p_p_id=LIQ_1&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_LIQ_1_struts_action=%2Fliquidaciones%2Feditar_liquidacion_entry
//		 String portletName = (String)actionRequest.getAttribute(WebKeys.PORTLET_ID);
//		 String portletName = "LIQ_1";
//         ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
//   
//         PortletURL redirectURL = PortletURLFactoryUtil.create(PortalUtil.getHttpServletRequest(actionRequest),portletName,themeDisplay.getLayout().getPlid(), PortletRequest.RENDER_PHASE);
//         redirectURL.setParameter("jspPage", "/portlet/liquidaciones/editar_liquidacion.jsp");
//         redirectURL.setParameter("struts_action", "/liquidaciones/editar_liquidacion_entry");
//         redirectURL.setPortletMode(PortletMode.VIEW);
//         redirectURL.setWindowState(WindowState.MAXIMIZED);
         
         
//         SessionErrors.add(actionRequest, "WRONG_FIRST_NAME_ERROR");
//         actionResponse.sendRedirect(redirectURL.toString());
        
//         return  setForward(actionRequest, redirectURL.toString());
         
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		int idItemCorr = ParamUtil.getInteger(renderRequest, "item_corr");
		int posicionItemBusqueda = ParamUtil.getInteger(renderRequest, "item_posicion");
		ItemCorrespondencia item = CorrespondenciaServiceUtil.buscarItemCorrespondenciaPorId(idItemCorr);
		List<UserGroup> grupos = UserGroupLocalServiceUtil.getUserUserGroups(user.getUserId());
		UserGroup grupo = grupos.get(0);
		Calendar cal = Calendar.getInstance();
		
		if(item != null ){
			
			String tipoRemitente = item.getTipoRemitenteDestinatario();
			
			if(tipoRemitente.equalsIgnoreCase("Prestador") && (Long.getLong(item.getSector()) == Long.getLong(String.valueOf(grupo.getUserGroupId()))) && 
					grupo.getUserGroupId() == new Double(99214)) {
//					getName().equalsIgnoreCase("liquidaciones") ){
//				if(tipoRemitente.equalsIgnoreCase("Prestador") && item.getSectorDescripcion().equalsIgnoreCase("liquidaciones") ){
				Prestador prestador = item.getPrestador();
				
		//		ptoVenta, tipoComprobante, nroComprobante, cuitEmisor, fechaEmision, fechaRecepcion, importeComprobante, letraComprobante, sucuComprobante, fechaVencimiento
				Comprobante comp = new Comprobante(item.getCompro_sucu(), item.getCompro_tipo(), item.getCompro_nro(), 
						/*cuitEmisor*/ prestador.getCuit() , item.getFecha_emision(), /*fechaRecepcion*/ item.getAlta_fecha(), item.getImporte(), item.getCompro_letra(), item.getCompro_sucu(), item.getFecha_vencimiento());
				
		//		Le pre-cargamos el comprobante a liquidaciones
				if(prestador!=null && item.getComprobanteString() != null && item.getImporte() != null && item.getImporte().intValue() > 0 ){
					
					PrestadorLugarAtencion pla = new PrestadorLugarAtencion();
					pla.setPrestador(prestador);
					Liquidacion liquidacion = new Liquidacion();
					
					liquidacion.setComprobante(comp);
					liquidacion.setId_prestador(prestador.getId_prestador());
					liquidacion.setPrestador_lugar_atencion(pla);
					
					liquidacion.setCompro_a_debitar_letra(item.getCompro_letra());
					liquidacion.setCompro_a_debitar_numero(item.getCompro_nro());
					liquidacion.setCompro_a_debitar_tipo(item.getCompro_tipo());
					liquidacion.setSucu(item.getCompro_sucu());
					liquidacion.setImporte(item.getImporte());
					liquidacion.setDebitado(new BigDecimal(0));
					liquidacion.setTercerizado("1");
					liquidacion.setFecha_emitido(item.getFecha_emision());
					liquidacion.setFecha_recibido(item.getCabecera().getFecha());
					liquidacion.setCargoOspim(new BigDecimal(0.00));
					liquidacion.setCargoPS(new BigDecimal(0.00));
					liquidacion.setCargoEnSalud(new BigDecimal(0.00));
					liquidacion.setCargoOmint(new BigDecimal(0.00));
					liquidacion.setCargoCemic(new BigDecimal(0.00));
					cal.setTime(item.getCabecera().getFecha());
					cal.add(Calendar.MONTH, 1);
					liquidacion.setFecha_vencimiento(cal.getTime());
					
					
					
					List<Concepto> entidades = TraeListasServiceUtil.getInstance().getConceptoLiquidacion( item.getFecha_emision() );
					
					renderRequest.getPortletSession().setAttribute(
							WebKeysLiquidaciones.CONCEPTOS_LIQUIDACION, entidades,
							PortletSession.APPLICATION_SCOPE);
					renderRequest.setAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);	
					renderRequest.setAttribute("paga", "0");
					session.setAttribute("posicion_item_corresp_busq_result", posicionItemBusqueda);
					
					return mapping.findForward("portlet.liquidaciones.editar_liquidacion_entry");
				}
			}
		}
		return mapping.findForward("portlet.correspondencia.view");
		
	}

}