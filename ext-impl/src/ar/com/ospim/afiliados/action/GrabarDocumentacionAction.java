/**
 */

package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.reportes.ReporteHistoricoMovimientosAfiliadoExcel;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.CredencialesServiceUtil;
import ar.com.ospim.afiliados.services.DocumentacionServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.ibm.icu.math.BigDecimal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="GrabarDocumentacionAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba las documentaciones
 * 
 * @author Federico Brachi
 * 
 */
public class GrabarDocumentacionAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GrabarDocumentacionAction.class);
	
	private PlanServiceUtil planService = new PlanServiceUtil();


	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.documentacion.view");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		Date fecha_ingreso = null;
		Date fecha_egreso = null;
		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		
		int id=ParamUtil.getInteger(renderRequest, "afi_id");
		int inte = ParamUtil.getInteger(renderRequest, "inte");
		User user = PortalUtil.getUser(renderRequest);
		Afiliado afiliado = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
		Date fechaMayoriaEdad = null;
		String mensajeEditarDoc = null;
		
		int result = 0;
		if (afiliado.getId_parentesco() == WebKeysAfiliados.HIJO_MENOR
				|| afiliado.getId_parentesco() == WebKeysAfiliados.HIJO_MENOR_CONYUGE
				|| afiliado.getId_parentesco() == WebKeysAfiliados.MENOR_BAJO_GUARDA) {
			fechaMayoriaEdad = org.apache.commons.lang.time.DateUtils.addYears(
					afiliado.getNaci_fecha(), WebKeysGlobal.ANIOS_MAYOR_EDAD);
		}
		String id_documentacion = ParamUtil.getString(renderRequest,"id_documentacion");
		String[] idDoc_idMotBaja = id_documentacion.split("\\|");
		int idDocumentacion = 0;
		int idMotivoBaja = 0;
		try{
			idDocumentacion = Integer.parseInt(idDoc_idMotBaja[0]);
			idMotivoBaja = Integer.parseInt(idDoc_idMotBaja[1]);
		}catch (Exception e) {
			//nada, a veces al borrar pasa por aca sin motivo de baja
		}	
		String fecha_ingreso_string = renderRequest.getParameter("fechaIngreso");
		String fecha_egreso_string = renderRequest.getParameter("fechaEgreso");
		if (null != fecha_ingreso_string) {
			fecha_ingreso = DateUtils.parse(fecha_ingreso_string, "dd/MM/yyyy");
		}
		if (null != fecha_egreso_string) {
			try {
				fecha_egreso = DateUtils.parse(fecha_egreso_string,
						"dd/MM/yyyy");
			} catch (ParseException e) {
				fecha_egreso = null;
			}
		}
		
		String certificado = ParamUtil.getString(renderRequest, "certificado");
		
		try {
			List<AfiDocumentacion> afiDocs = null;
			if (null != ParamUtil.getString(renderRequest, "borrarDoc")
					&& ParamUtil.getString(renderRequest, "borrarDoc").trim().equals("true")) {
				    result = DocumentacionServiceUtil.borraDocumentacion(cuil_titular, inte, 
						idDocumentacion, fecha_ingreso, user, fechaMayoriaEdad, id);
			} else if (null != renderRequest.getParameter("editarDoc")
					&& renderRequest.getParameter("editarDoc").trim().equals("true")) {
				mensajeEditarDoc = DocumentacionServiceUtil.editaDocumentacion(cuil_titular, inte, 
						idDocumentacion, fecha_ingreso, fecha_egreso, user, id,certificado);
			} else {
				mensajeEditarDoc = DocumentacionServiceUtil.grabaDocumentacion(cuil_titular, inte, 
						idDocumentacion, fecha_ingreso, fecha_egreso, user, idMotivoBaja,certificado);
			}
			afiDocs = DocumentacionServiceUtil.buscaDocumentacion(cuil_titular, inte);
			renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_DOCUMENTOS, afiDocs);
			String view = ParamUtil.getString(renderRequest, "view");
			if (null != view && view.equals("true")) {
				renderRequest.setAttribute("view", view);
			}
		} catch (Exception e) {
			_log.error(e);
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			//Generar Credencia nocopago
			//5   CERTIFICADO POR INCAPACIDAD ---- 12 PLAN MATERNO INFANTIL
			if (idDocumentacion == 5 || idDocumentacion == 12){
				AfiPlan afiPlan = planService.buscarUltimoPlanAportes(cuil_titular); 
				if (afiPlan != null && (("KRONO".equalsIgnoreCase(afiPlan.getPlan().getDescripcionEnsalud()) 
						&& ("A".equalsIgnoreCase(afiPlan.getPlan().getFarmaciaEnsalud()) 
						|| "B".equalsIgnoreCase(afiPlan.getPlan().getFarmaciaEnsalud() )))
					    ||  "DELTA".equalsIgnoreCase(afiPlan.getPlan().getDescripcionEnsalud())) ){
					renderRequest.setAttribute("isCredencial","S");
					renderRequest.setAttribute("cuil_titular_aux",cuil_titular);
					renderRequest.setAttribute("inte_aux", String.valueOf(inte));
					
					CredencialesServiceUtil.insertarCredencial(cuil_titular, inte, user.getScreenName());
				}
				
			}
			SessionMessages.add(renderRequest, "request_processed", "");
			if (mensajeEditarDoc != null  && mensajeEditarDoc.length() > 2) {
				SessionMessages.add(renderRequest, "documentacionAfiOk", "");
				renderRequest.setAttribute("msgDocumentacionAfiOk",mensajeEditarDoc);
				
				
			}
		}
		//Traigo datos para resfrescar pantalla afiliaciones
		List<Afiliado> afiliados =  BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(cuil_titular, String.valueOf(inte) , null,null,
															0,null,null,null,0,0,null);
		
		Afiliado afi = afiliados.iterator().next();
		renderRequest.setAttribute("idParentescoDoc",String.valueOf(afi.getId_parentesco()));
		renderRequest.setAttribute("discapacitadoDoc", afi.getDiscapacitado());
		//Seteamos la marca de discapacidad aunque este en tramite
		if (idDocumentacion == 15) {
			renderRequest.setAttribute("discapacitadoDoc", "1");
		}
		// solo para documentos de discapacidad envio alertas
//		SVA 08/10/2019
//		if(idDocumentacion == 5){ 
//			this.enviarNovedadsobreAfiliadoDiscapacidad(cuil_titular);
//		}
		return mapping.findForward("portlet.documentacion.result");
	}
	
	private void enviarNovedadsobreAfiliadoDiscapacidad(String cuilTitular){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_DISCAPACIDAD);
		
		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso cambios en afiliado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosGrupoFamiliar_"+cuilTitular+".xls");
		
	}
}