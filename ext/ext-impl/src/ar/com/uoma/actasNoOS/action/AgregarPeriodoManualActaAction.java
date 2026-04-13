package ar.com.uoma.actasNoOS.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa.Detalle;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarPeriodoManualActaAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(AgregarPeriodoManualActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");
		
		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_EST_1_")) {
			String entidadString=ParamUtil.getString(renderRequest,"entidad");
			if(entidadString!=null&&entidadString.equals("U.O.M.A.")){
				entidad = WebKeysGlobal.UOMA;
			}else if(entidadString!=null&&entidadString.equals("A.M.T.I.M.A.")){
				entidad = WebKeysGlobal.AMTIMA;	
			}
		}


		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		String cuil = ParamUtil.getString(renderRequest, "cuil");
		
		String remuneracion_declarada = ParamUtil.getString(renderRequest,
				"remuneracion_declarada", "0");
		String calculado = ParamUtil.getString(renderRequest, "calculado", "0");
		
		String interes = ParamUtil.getString(renderRequest, "interes", "0");
		String pagado = ParamUtil.getString(renderRequest, "pagado", "0");
		String interesAPago = ParamUtil.getString(renderRequest, "interesApago", "0");
		String apellido = ParamUtil.getString(renderRequest, "apellido");
		String nombre = ParamUtil.getString(renderRequest, "nombre");
		String periodo = ParamUtil.getString(renderRequest, "periodo");
		int cantAfiliados= ParamUtil.getInteger(renderRequest, "cant_afi");
		int tipoBoleta= ParamUtil.getInteger(renderRequest, "tipo_boleta");
		String camara=ParamUtil.getString(renderRequest, "camara");

		try {
			renderRequest.setAttribute("mostrar_periodo", periodo);
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			periodo = periodo.replaceAll("_", "-");
			periodo = "01-" + periodo;
			Date periodoDate = format.parse(periodo);			
			List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
			if (peris == null) {
				peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
				acta.setPeriodos(peris);
			}
			
			int fechaIngresoDia = ParamUtil.getInteger(renderRequest,
					"fechaIngresoDia");
			int fechaIngresoMes = ParamUtil.getInteger(renderRequest,
					"fechaIngresoMes");
			
			int fechaIngresoAnio = ParamUtil.getInteger(renderRequest,
					"fechaIngresoAnio");
			GregorianCalendar calendarIngreso= null;
			Date fechaIngreso= null;
			
			
			if (0 != fechaIngresoDia) {
				calendarIngreso = new GregorianCalendar(fechaIngresoAnio, fechaIngresoMes, fechaIngresoDia);
				fechaIngreso = calendarIngreso.getTime();
			} 
			
			ActaPeriodoDeudaEmpresa peri = new ActaPeriodoDeudaEmpresa();
			BigDecimal remuneracionb=new BigDecimal(StringUtils.checkNotEmpty(remuneracion_declarada)?remuneracion_declarada:"0");
			peri.setRemuneracionDeclarada(remuneracionb);
			peri.setCalculado(new BigDecimal(StringUtils.checkNotEmpty(calculado)?calculado:"0"));
			peri.setApellido(apellido);
			peri.setNombre(nombre);
			peri.setPeriodo(periodoDate);			
			peri.setTipoAporte(tipoBoleta);
			peri.setCamara(camara);
			peri.setFechaIngreso(fechaIngreso);
			
			List<Detalle> pagos = new ArrayList<Detalle>();
			int fechaPagoDia = ParamUtil.getInteger(renderRequest,
					"fechaPagoDia");
			int fechaPagoMes = ParamUtil.getInteger(renderRequest,
					"fechaPagoMes");
			
			int fechaPagoAnio = ParamUtil.getInteger(renderRequest,
					"fechaPagoAnio");
			GregorianCalendar calendarPago= null;
			Date fechaPago= null;
			
			
			if (0 != fechaPagoDia && 0 != fechaPagoMes) {
				calendarPago = new GregorianCalendar(fechaPagoAnio, fechaPagoMes, fechaPagoDia);
				fechaPago = calendarPago.getTime();
			} 
		

			BigDecimal montoPagado = BigDecimal.ZERO;
			if (StringUtils.checkNotEmpty(pagado)
					&& !(new BigDecimal(pagado).equals(BigDecimal.ZERO))) {
				montoPagado = new BigDecimal(pagado);
			} else {
				fechaPago = null;
			}

			int id = getProximoId(peris);
			if(null==cuil||cuil.trim().equals("")){				
				cuil=org.apache.commons.lang.StringUtils.leftPad(String.valueOf(id*-1), 11,"0");
			}
			peri.setCuil(cuil);
			BigDecimal calculadoB=new BigDecimal(StringUtils.checkNotEmpty(calculado)?calculado:"0");
			Detalle detalle = new Detalle();
			detalle.setCapitalOriginal(calculadoB);
			detalle.setCapital(calculadoB.subtract(montoPagado));
			detalle.setInteres(new BigDecimal(StringUtils.checkNotEmpty(interes)?interes:"0"));			
			detalle.setId(id);			
			detalle.setAgregadoManual(true);
			detalle.setTipoAporte(tipoBoleta);
			detalle.setCantidadAfiliados(cantAfiliados);
			detalle.setMontoPagado(montoPagado);
			detalle.setFechaPagado(fechaPago);
			detalle.setInteresAFechaPagada(new BigDecimal(StringUtils.checkNotEmpty(interesAPago)?interesAPago:"0"));
			pagos.add(detalle);
			peri.setDetalle(pagos);
			peris.add(peri);
			
			

			Collections.sort(peris, new Comparator<ActaPeriodoDeudaEmpresa>() {
				public int compare(ActaPeriodoDeudaEmpresa o1,
						ActaPeriodoDeudaEmpresa o2) {
					int compareTo = o1.getPeriodo().compareTo(o2.getPeriodo());
					if (compareTo == 0) {
						compareTo = o1.getCuil().compareTo(o2.getCuil());
					}
					return compareTo;
				}
			});


			List<ActaPeriodoDeudaEmpresa> periodos = new ArrayList<ActaPeriodoDeudaEmpresa>();
			
			for (ActaPeriodoDeudaEmpresa actaPeri : peris) {
				if (actaPeri.getPeriodo().equals(periodoDate)){// && actaPeri.getTipoAporte()==tipoBoleta) {
					
					BigDecimal remuTotalAux=actaPeri.getRemuneracionTotal()!=null?actaPeri.getRemuneracionTotal().add(remuneracionb):remuneracionb;
					actaPeri.setCantTotalAfi(actaPeri.getCantTotalAfi()+cantAfiliados);
					actaPeri.setRemuneracionTotal(remuTotalAux);
					periodos.add(actaPeri);
				}
			}
			renderRequest.setAttribute(WebKeysTesoreria.ACTAS_PERIODOS,
					periodos);			

		} catch (Exception e) {
			_log.error("Error al agregar periodo", e);
			return null;
		}

		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
				WebKeysTesoreria.ACTAS_ACTION_EDICION);
		_log.debug("Saliendo de reder");
		if(entidad!=WebKeysGlobal.OSPIM){
			return mapping.findForward("portlet.uoma.actas.editar.periodos.view");
		}else{
			return mapping.findForward("portlet.tesoreria.actas.editar.periodos.view");
		}
	}

	private int getProximoId(List<ActaPeriodoDeudaEmpresa> peris) {
		int id = 0;
		for (ActaPeriodoDeudaEmpresa peri : peris) {
			if (peri.getDetalle() != null) {
				for (ActaPeriodoDeudaEmpresa.Detalle det : peri.getDetalle()) {
					if (det.getId() <= 0 && det.getId() < id) {
						id = det.getId();
					}
				}
			}
		}
		return --id;
	}
}
