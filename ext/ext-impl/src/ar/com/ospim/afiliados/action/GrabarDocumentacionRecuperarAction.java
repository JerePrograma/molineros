/**
 */

package ar.com.ospim.afiliados.action;

import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.reportes.ReporteHistoricoMovimientosAfiliadoExcel;
import ar.com.ospim.afiliados.services.DocumentacionServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.ReincorporarServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

/**
 * <a href="GrabarDocumentacionRecuperarAction.java.html"><b><i>View
 * Source</i></b></a>
 * <p>
 * Graba las documentaciones y recupera el afiliado
 * 
 * @author Carlos Rivas
 * 
 */
public class GrabarDocumentacionRecuperarAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GrabarDocumentacionRecuperarAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		setForward(actionRequest, "portlet.documentacion_recuperar.view");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		Date fecha_ingreso = null;
		Date fecha_egreso = null;
		Date fecha_egreso_titu = null;
		Date bajaFecha = null;
		int id_motivo_baja = -1;
		String cuil_titular = ParamUtil
				.getString(renderRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(renderRequest, "inte");

		User user = PortalUtil.getUser(renderRequest);

		int id_documentacion = ParamUtil.getInteger(renderRequest,
				"id_documentacion");

		int idCorrespondencia = ParamUtil.getInteger(renderRequest, "numero_correspondencia",0);
		
		Afiliado afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil_titular, inte);
		
		String fecha_ingreso_string = renderRequest.getParameter("fechaIngreso");
		String fecha_egreso_string = renderRequest.getParameter("fechaEgreso");

		Afiliado afiTitular = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil_titular, 0);
		
		String fecha_egreso_string_titu = afiTitular.getBaja_fechaAsString();
		int id_motivo_baja_titular = afiTitular.getId_motivo_baja();

		if (null != fecha_ingreso_string) {
			fecha_ingreso = DateUtils.parse(fecha_ingreso_string, "dd/MM/yyyy");
		}
		if (null != fecha_egreso_string) {
			try {
				fecha_egreso = DateUtils.parse(fecha_egreso_string,"dd/MM/yyyy");
				fecha_egreso_titu = DateUtils.parse(fecha_egreso_string_titu,"dd/MM/yyyy");
				
				boolean comparar = fecha_egreso_titu != null ? DateUtils.esMayor(fecha_egreso, fecha_egreso_titu) : false;
				if (comparar) {
					fecha_egreso = fecha_egreso_titu;
					id_motivo_baja = id_motivo_baja_titular;
				}
			} catch (ParseException e) {
				fecha_egreso = null;
			}

			bajaFecha = null;
			if (((afiliado != null
					&& afiliado.getInte() != 0
					&& afiliado.getParentesco() != null
					&& (afiliado.getId_parentesco() == WebKeysAfiliados.HIJO_MENOR 
					|| afiliado.getId_parentesco() == WebKeysAfiliados.HIJO_MENOR_CONYUGE) 
					|| afiliado.getId_parentesco() == WebKeysAfiliados.MENOR_BAJO_GUARDA))
					&& id_documentacion == 0) {
				bajaFecha = org.apache.commons.lang.time.DateUtils.addYears(
						afiliado.getNaci_fecha(),
						WebKeysGlobal.ANIOS_MAYOR_EDAD);
				id_motivo_baja = 4;
				fecha_egreso = bajaFecha;
			}

			/* redundante esta el grabaDocumentacionRetornaLista*/
			boolean compararFecha = false;
			Date fechaMayoriaEdad = org.apache.commons.lang.time.DateUtils.addYears(
					afiliado.getNaci_fecha(), WebKeysGlobal.ANIOS_MAYOR_EDAD);
			if (fecha_egreso != null && fechaMayoriaEdad != null) {
				compararFecha = ar.com.ospim.util.DateUtils.esMayor(
						fecha_egreso, fechaMayoriaEdad);
			}
			int age = ar.com.ospim.util.DateUtils.getEdad(afiliado.getNaci_fecha());
			
			if (id_motivo_baja == -1) {
				if ((id_documentacion == 4) && (compararFecha) && (age < 25)) {
					id_motivo_baja = WebKeysAfiliados.HIJO_MAYOR;
				}
				if (id_documentacion == 5 || id_documentacion == 15) {
					id_motivo_baja = WebKeysAfiliados.CERTIFICADO_POR_INCAPACIDAD;
				}
			}
			/* fin redundante*/
		}
		
		String certificado=null;
		try {
			certificado = renderRequest.getParameter("certificado");	
		}catch(Exception e) {}
		
		afiliado.setIdCorrespondencia(idCorrespondencia);
		
		Connection con = ConnectionHelper.getConnectionForTransaction();
		try {
			if (id_documentacion != 0) {
				List<AfiDocumentacion> afiDocs = null;
				
				// primero grabo el documento en la base de datos
				try {
					DocumentacionServiceUtil
							.grabaDocumentacion(cuil_titular, inte,
									id_documentacion, fecha_ingreso,
									fecha_egreso, user, id_motivo_baja,certificado, con);
					renderRequest.setAttribute(
							WebKeysAfiliados.BUSQUEDA_DOCUMENTOS, afiDocs);
					
					afiDocs = DocumentacionServiceUtil.buscaDocumentacion(cuil_titular, inte);
				} catch (Exception e) {
					_log.error(e);

				}
			}
			int continuidad = esContinuidad(fecha_ingreso, afiliado.getVigen_fecha());
			
			ReincorporarServiceUtil.reincorporarAfiliado(afiliado,
					fecha_ingreso, fecha_egreso, continuidad, afiliado.getVigen_fecha(),
					user.getScreenName(), id_motivo_baja, /*false,*/ con);
			
			ReincorporarServiceUtil.actualizaNumAfiliadosGrupo(
					afiliado.getCuil_titular(), afiliado.getInte(), con);
			
			String view = ParamUtil.getString(renderRequest, "view");

			if (null != view && view.equals("true")) {
				renderRequest.setAttribute("view", view);
			}
			con.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(con);
			_log.error(e);
			SessionErrors.add(renderRequest, e.getClass().getName());
		} finally {
			ConnectionHelper.cerrar(con);
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}
		
		// solo para documentos de discapacidad envio alertas
//		SVA 08/10/2019
//		if(id_documentacion == 5){ 
//			this.enviarNovedadsobreAfiliadoDiscapacidad(cuil_titular);
//		}

		return mapping.findForward("portlet.documentacion_recuperar.result");
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
	
	private void enviarNovedadsobreAfiliadoDiscapacidad(String cuilTitular){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_DISCAPACIDAD);

		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso cambios en afiliado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosGrupoFamiliar_"+cuilTitular+".xls");
		
	}
}