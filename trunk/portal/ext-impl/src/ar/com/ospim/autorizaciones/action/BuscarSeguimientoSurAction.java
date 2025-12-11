package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.BusquedaSeguimientoSurFiltro;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.util.DateUtils;

public class BuscarSeguimientoSurAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarSeguimientoSurAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String popup = null;
		
		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

			int anio = ParamUtil.getInteger(renderRequest,"anio");
			int bimestre= ParamUtil.getInteger(renderRequest,"bimestre");
			int tipoExpediente=ParamUtil.getInteger(renderRequest,"tipoexpediente");
			int autorizaOmint= ParamUtil.getInteger(renderRequest,"autorizaomint");
			String nroSolicitud= ParamUtil.getString(renderRequest, "nrosolicitudsur");
			String codigoPresentado = ParamUtil.getString(renderRequest, "codigopresentado");
			String descripcionPresentado = ParamUtil.getString(renderRequest, "descripcionpresentado");
			String nroExpediente = ParamUtil.getString(renderRequest, "nroexpedientesur");
			
			String cuil = ParamUtil.getString(renderRequest, "cuil");
			String inte = ParamUtil.getString(renderRequest,"inte");
			
			
			String diaDesde = ParamUtil.getString(renderRequest, "fechadesdedia");
			String mesDesde = ParamUtil.getString(renderRequest, "fechadesdemes");
			String anioDesde = ParamUtil.getString(renderRequest, "fechadesdeanio");
			
			
			String diaHasta = ParamUtil.getString(renderRequest, "fechahastadia");
			String mesHasta = ParamUtil.getString(renderRequest, "fechahastames");
			String anioHasta = ParamUtil.getString(renderRequest, "fechahastaanio");
			
			
			String diaDesdeSur = ParamUtil.getString(renderRequest, "fechaestsurdesdedia");
			String mesDesdeSur = ParamUtil.getString(renderRequest, "fechaestsurdesdemes");
			String anioDesdeSur = ParamUtil.getString(renderRequest, "fechaestsurdesdeanio");
			
			
			String diaHastaSur = ParamUtil.getString(renderRequest, "fechaestsurhastadia");
			String mesHastaSur = ParamUtil.getString(renderRequest, "fechaestsurhastames");
			String anioHastaSur = ParamUtil.getString(renderRequest, "fechaestsurhastaanio");
			
			Date fechaDde = formatoDePeriodo.parse(diaDesde +"/"+(Integer.parseInt(mesDesde) + 1)+"/"+anioDesde);
			Date fechaHta = formatoDePeriodo.parse(diaHasta +"/"+(Integer.parseInt(mesHasta) + 1)+"/"+anioHasta);
			fechaHta= DateUtils.anyadeDias(fechaHta,1);				
		
			Date fechaDdeSur;
			try {
				fechaDdeSur = formatoDePeriodo.parse(diaDesdeSur +"/"+(Integer.parseInt(mesDesdeSur) + 1)+"/"+anioDesdeSur);
			} catch (Exception e) {
				fechaDdeSur = null;
			}
			Date fechaHtaSur;
			try {
				fechaHtaSur = formatoDePeriodo.parse(diaHastaSur +"/"+(Integer.parseInt(mesHastaSur) + 1)+"/"+anioHastaSur);
				fechaHtaSur= DateUtils.anyadeDias(fechaHtaSur,1);				
			} catch (Exception e) {
				fechaHtaSur =  null;
			}
			
			boolean incluyeBajas= ParamUtil.getBoolean(renderRequest,"incluyebajas");
			String estadoExpediente=ParamUtil.getString(renderRequest,"estado");
			String[] estadoSSSExpediente=renderRequest.getParameterValues("estadosss");
			int tipoTercerizadora = ParamUtil.getInteger(renderRequest,"tipoTercerizadora");
			String estadoSSS="";
			if(estadoSSSExpediente!=null &&  estadoSSSExpediente.length>0){
				if(estadoSSSExpediente.length==1 && (
						!"null".equalsIgnoreCase(estadoSSSExpediente[0]) && !"0".equalsIgnoreCase(estadoSSSExpediente[0]) && estadoSSSExpediente[0]!=null )){
					estadoSSS+=estadoSSSExpediente[0]+",";
				}else if(estadoSSSExpediente.length>1){
				  for(int xi=0; xi<estadoSSSExpediente.length;xi++){
					estadoSSS +=estadoSSSExpediente[xi]+",";
				  }
				}
			}
			
			String[] estadoSSSHisExpediente=renderRequest.getParameterValues("estadosss_his");
			String estadoHisSSS="";
			
			if(estadoSSSHisExpediente!=null &&  estadoSSSHisExpediente.length>0){
				if(estadoSSSHisExpediente.length==1 && (
						!"null".equalsIgnoreCase(estadoSSSHisExpediente[0]) && !"0".equalsIgnoreCase(estadoSSSHisExpediente[0]) && estadoSSSHisExpediente[0]!=null )){
					estadoHisSSS+=estadoSSSHisExpediente[0]+",";
				}else if(estadoSSSHisExpediente.length>1){
				  for(int xi=0; xi<estadoSSSHisExpediente.length;xi++){
					  estadoHisSSS +=estadoSSSHisExpediente[xi]+",";
				  }
				}
			}
			
			
			String clase = ParamUtil.getString(renderRequest, "clase");
			int claseNro = ParamUtil.getInteger(renderRequest, "clasenro");
			
			String diaCorresHasta = ParamUtil.getString(renderRequest, "fechaCorreshastadia");
			String mesCorresHasta = ParamUtil.getString(renderRequest, "fechaCorreshastames");
			String anioCorresHasta = ParamUtil.getString(renderRequest, "fechaCorreshastaanio");
			
			String diaCorresDesde = ParamUtil.getString(renderRequest,"fechaCorresdesdedia");
			String mesCorresDesde = ParamUtil.getString(renderRequest,"fechaCorresdesdemes");
			String anioCorresDesde= ParamUtil.getString(renderRequest,"fechaCorresdesdeanio");
			String nroCorrespondencia  = ParamUtil.getString(renderRequest,"nroCorrespondencia");
			String convenioTercerizadora= ParamUtil.getString(renderRequest,"convenioTercerizadora");
			
			Date fechaCorresDesde = null;
			Date fechaCorresHasta = null;
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
			
			try {
				fechaCorresDesde = formatoDeFechas.parse(diaCorresDesde + "/"
						+ (Integer.parseInt(mesCorresDesde) + 1) + "/"
						+ anioCorresDesde);
			} catch (Exception e) {
				fechaCorresDesde = null;
			}

			try {
				fechaCorresHasta = formatoDeFechas.parse(diaCorresHasta + "/"
						+ (Integer.parseInt(mesCorresHasta) + 1) + "/"
						+ anioCorresHasta);
			} catch (Exception e) {
				fechaCorresHasta = null;
			}
			
			popup = ParamUtil.getString(renderRequest, "popup");
			
			String usuarioAlta = ParamUtil.getString(renderRequest, "usuarioalta");
			
			String diaEstadoDesde = ParamUtil.getString(renderRequest, "fechaestadodesdedia");
			String mesEstadoDesde = ParamUtil.getString(renderRequest, "fechaestadodesdemes");
			String anioEstadoDesde = ParamUtil.getString(renderRequest, "fechaestadodesdeanio");
			
			
			String diaEstadoHasta = ParamUtil.getString(renderRequest, "fechaestadohastadia");
			String mesEstadoHasta = ParamUtil.getString(renderRequest, "fechaestadohastames");
			String anioEstadoHasta = ParamUtil.getString(renderRequest, "fechaestadohastaanio");
			
			Date fechaEstadoDde = null;
			Date fechaEstadoHta = null;
			try {
				fechaEstadoDde = formatoDePeriodo.parse(diaEstadoDesde +"/"+(Integer.parseInt(mesEstadoDesde) + 1)+"/"+anioEstadoDesde);
			} catch (Exception e) {
				fechaEstadoDde = null;
			}
			
			try {
				fechaEstadoHta = formatoDePeriodo.parse(diaEstadoHasta +"/"+(Integer.parseInt(mesEstadoHasta) + 1)+"/"+anioEstadoHasta);
			} catch (Exception e) {
				fechaEstadoDde = null;
			}
			Integer ddjj=ParamUtil.getInteger(renderRequest, "ddjj");
			String codigoHIV = ParamUtil.getString(renderRequest, "codigoHIV");
			
			session.removeAttribute(WebKeysAutorizaciones.SEGUIMIENTO_SUR_FILTRO);
			session.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_SEGUIMIENTO_SUR_RESULT);
			
			BusquedaSeguimientoSurFiltro filtro = new BusquedaSeguimientoSurFiltro(anio, bimestre, tipoExpediente, 
					autorizaOmint, nroSolicitud, codigoPresentado, descripcionPresentado, nroExpediente, cuil, inte, 
					fechaDde, fechaHta, incluyeBajas, estadoExpediente, clase, usuarioAlta, estadoSSS, claseNro, 
					fechaCorresDesde, fechaCorresHasta, tipoTercerizadora, nroCorrespondencia, convenioTercerizadora, 
					fechaEstadoDde, fechaEstadoHta, estadoHisSSS, fechaDdeSur, fechaHtaSur, ddjj, codigoHIV);
			
			List<SeguimientoSur> autorizaciones = new ArrayList<SeguimientoSur>();
			
			autorizaciones = SeguimientoSurServiceUtil.getListaSeguimientoSur(filtro);
			
			
			
			
			
/* DS - 15/05/2017 Se comenta por cambio en politica de pagos			
			for(SeguimientoSur s:autorizaciones){
				if(s.getNro_expediente()!=null && !"".equalsIgnoreCase(s.getNro_expediente()) && s.getCierre_fecha()==null){
					if(SeguimientoSurServiceUtil.existeMovimientoBancoSeguimientoSur(s.getNro_expediente()) ){
                       SeguimientoSurServiceUtil.cierraSeguimiento(s.getId(),new Date(), "PG");
                       
                       String estadoPagado =TraeListasServiceUtil.getSystemConfig("ESTADO_PAGADO_SUR");
                       String[] estadoPagadoV =estadoPagado.split("\\|"); // Se agregaron las 2 barras invertidas, porque no estaba funcionando
                                                                          //bien el split solo con el |  
                       
                       SeguimientoSurEstado e = new SeguimientoSurEstado();
                       e.setFechaEstado(new Date());
                       e.setIdEstado(Integer.parseInt(estadoPagadoV[0]));
                       e.setDescripcionEstado(estadoPagadoV[1]);
                       s.getEstados().add(e);
                       
                       s.setCierre_fecha(new Date());
                       s.setCierre_motivo("PG");
					}
				}
			}
*/			
//			session.removeAttribute("SeguimientosSUR");
//			session.setAttribute("SeguimientosSUR", autorizaciones);
			
			session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACIONES_FILTRO,filtro);
			session.setAttribute(WebKeysAutorizaciones.BUSQUEDA_SEGUIMIENTO_SUR_RESULT, autorizaciones);
			
		} catch (Exception e) {
			_log.error(e);
		}

		return mapping.findForward("portlet.autorizaciones.buscar_seguimientosur");
	}

}