package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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

import ar.com.ospim.afiliados.action.ActionUtil;
import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.AfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.DocumentacionServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.AutoPrestacional;
import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.reportes.action.CUDAvisoVencimiento;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateTratamientoDiscapacidadIdException;
import ar.com.ospim.liquidaciones.TopeCantidadIndividualExedidoException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.util.StringUtils;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * <a href="EditarTratamientoEntryAction.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */

public class EditarAutorizacionPrestacionalEntryAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(EditarAutorizacionPrestacionalEntryAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
//		setForward(actionRequest,
//				"portlet.autorizaciones.autorizacion_prestacional.result");
	}

	public String validarDocumentacionDiscapacidad(RenderRequest renderRequest,
			String cmd) throws Exception {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);

		String cuil = ParamUtil.getString(renderRequest, "cuil", null);
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		Boolean disca= ParamUtil.getBoolean(renderRequest,"es_discapacitado");
		
		if (cmd.equalsIgnoreCase(Constants.UPDATE)) {
			
			AutorizacionPrestacional td = AutorizacionPrestacionalServiceUtil.getAutorizacionPrestacional(id_tratamiento);
			cuil = td.getAfiliado().getCuil_titular();
			inte = td.getAfiliado().getInte();
			disca= td.isDiscapacitado();///----Nuevo Monotributo
		}

		ArrayList<AfiDocumentacion> docList = (ArrayList<AfiDocumentacion>) DocumentacionServiceUtil
				.buscaDocumentacionDiscapacidad(cuil, inte);

		String mensajeDocumento = "";

		if ( disca && (docList == null || docList.size() == 0)) {   ///----Nuevo Monotributo  
			return "NO SE ENCUENTRA CERTIFICADO DE DISCAPACIDAD PARA DICHO AFILIADO";
		}

		if (disca && docList != null && docList.size() > 0) {
		   AfiDocumentacion ultimoVigente = (AfiDocumentacion) Collections.max(
				docList, new Comparator<AfiDocumentacion>() {
					public int compare(AfiDocumentacion o1, AfiDocumentacion o2) {
						return o1.getFecha_baja().compareTo(o2.getFecha_baja());
					}
				});

		   AfiDocumentacion primeroVigente = (AfiDocumentacion) Collections.min(
				docList, new Comparator<AfiDocumentacion>() {
					public int compare(AfiDocumentacion o1, AfiDocumentacion o2) {
						return o1.getFecha_baja().compareTo(o2.getFecha_baja());
					}
				});
		
		

		   if (!((fechaDesde.after(primeroVigente.getFecha_ingre()) || fechaDesde
				.equals(primeroVigente.getFecha_ingre()))
				&& (ultimoVigente.getFecha_baja() == null
						|| fechaHasta.before(ultimoVigente.getFecha_baja()) || fechaHasta
						.equals(ultimoVigente.getFecha_baja())))) {
			  mensajeDocumento = "CERTIFICADO VIGENTE DESDE "
					+ primeroVigente.getFecha_ingreAsString() + " HASTA "
					+ ultimoVigente.getFecha_bajaAsString();
		   }
		
		}
		
		return mensajeDocumento;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				renderRequest).getSession();

		String cmd = ParamUtil.getString(renderRequest, "accionOriginal");
		String inteAux=ParamUtil.getString(renderRequest,"integrante");
		
		User user = PortalUtil.getUser(renderRequest);
		List<String>errores = new ArrayList<String>();
		
		String tabSelec =  ParamUtil.getString(renderRequest, "tab", null);
		

		if (tabSelec != null){
			session.setAttribute("tab", tabSelec);
			int idTratamiento = ParamUtil.getInteger(renderRequest,"id_tratamiento", 0);
			session.setAttribute("id_tratamiento", idTratamiento);
			return mapping.findForward("portlet.autorizaciones.editar_autorizacion_prestacional");
		}
		
		if (cmd.equals("new")) {
			session.setAttribute("id_tratamiento", 0);
			session.setAttribute("cuil_titular",  ParamUtil.getString(renderRequest, "cuil_titular"));
			session.setAttribute("inte", ParamUtil.getString(renderRequest,"inte"));
			session.setAttribute("esDiscapacitado",  ParamUtil.getString(renderRequest,"esdiscapacitado"));
			return mapping.findForward("portlet.autorizaciones.editar_autorizacion_prestacional");
		}
		
		if (cmd.equals(Constants.VIEW)) {
			renderRequest.setAttribute("id_tratamiento", ParamUtil.getInteger(renderRequest,"id_tratamiento", 0));
			session.setAttribute("cuil_titular",  ParamUtil.getString(renderRequest, "cuil_titular"));
			if(inteAux!=null && !"".equalsIgnoreCase(inteAux)) {
			  session.setAttribute("inte", inteAux);
			}else {
			  session.setAttribute("inte", ParamUtil.getString(renderRequest,"inte"));
			}  
			session.setAttribute("esDiscapacitado",  ParamUtil.getString(renderRequest,"esdiscapacitado"));
			return mapping.findForward("portlet.autorizaciones.editar_autorizacion_prestacional");
		}
		
		
		if (cmd.equals("avisovencimientocud")) {
			
			Integer ret=avisosVencimientosCUD(renderRequest);
			
			if(ret>0) {
				SessionMessages.add(renderRequest, "request_processed",
						"Se encontraron "+ ret +" afiliados a los cuales se les envió el aviso");
			}else {
				errores.add("No se encontraron afiliados con las condiciones para enviarles el aviso");
				renderRequest.setAttribute("errores", errores);
			}
			return mapping.findForward("portlet.autorizaciones.reporte.avisos_vencimientos_cud");
		}
		
		
		
		if (cmd.equals(Constants.DELETE)) {
			borraTratamientoDiscapacidadEntry(renderRequest);
		}

		
		String mensaje = validarDocumentacionDiscapacidad(renderRequest, cmd);
		if (mensaje.length() == 0) {
			mensaje = validarAfiliadoPrestacion(renderRequest,cmd);
		}
		
		int idtratamiento = 0;
		if (mensaje.length() == 0) {
			try {
				if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
					idtratamiento = updateTratamientoEntry(renderRequest, cmd);
				} else  if (cmd.equalsIgnoreCase("estado")) {
					idtratamiento = cambioEstadoTratamientoEntry(renderRequest);
				}
			} catch (DuplicateTratamientoDiscapacidadIdException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			} catch (Exception e) {
				_log.error(e);
				e.printStackTrace();
				SessionErrors.add(renderRequest, Exception.class.getName());
			}
		} else {
			
			errores.add(mensaje);
			SessionErrors.add(renderRequest, "mensajeCertificado");
    		renderRequest.setAttribute("errores", errores);
			
			/*
			renderRequest.setAttribute("mensajeCertificado", mensaje);
			return mapping
					.findForward("portlet.autorizaciones.autorizacion_prestacional.error");
            */					
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
			renderRequest.setAttribute("id_tratamiento", idtratamiento);
			session.setAttribute("id_tratamiento", idtratamiento);
			
			AutorizacionPrestacional tratamiento = null;
			tratamiento = AutorizacionPrestacionalServiceUtil.getAutorizacionPrestacional(idtratamiento);
			if (tratamiento.getIdPreautorizacion() != 0){
				//Actualizamos el estado de la preautizacion
				String out = null; 
		        switch(tratamiento.getEstado()) 
		        { 
		            case 1: 
		                out = "AU"; //AUTORIZADO
		                break; 
		            case 2: 
		                out = "OB"; //OBSERVADO
		                break; 
		            case 4: 
		                out = "AU"; //AUTORIZADO
		                break;
		            default: 
		        } 
			    if (out !=  null){			    	
			    	PreAutorizacionServiceUtil.updateEstadoPreautorizacion(tratamiento.getIdPreautorizacion(),  out, user.getScreenName());
			    	if(tratamiento.getPrestador()!=null  && tratamiento.getPrestador().getId_prestador()>0) {
			    		PreAutorizacionServiceUtil.updatePrestadorPreautorizacion(tratamiento.getIdPreautorizacion(), 
			    				tratamiento.getPrestador().getId_prestador(), user.getScreenName());
			    	}
			    }
			}
			
			session.setAttribute("tab", "datos");	
		}

		return mapping.findForward("portlet.autorizaciones.editar_autorizacion_prestacional");
		
	}

	private int updateTratamientoEntry(RenderRequest actionRequest, String cmd)
			throws PortalException, SystemException,
			DuplicatePrestadorIdException,
			DuplicateTratamientoDiscapacidadIdException {

		int idTratamiento = ParamUtil.getInteger(actionRequest,
				"id_tratamiento", 0);
		int idPrestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);
		String cuil = ParamUtil.getString(actionRequest, "cuil", null);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		String cantidad = ParamUtil.getString(actionRequest, "cantidad", "0")
				.equals("") ? "0" : ParamUtil.getString(actionRequest,
				"cantidad", "0");
		String importeTotal = ParamUtil.getString(actionRequest,
				"importe_total", "0").equals("") ? "0" : ParamUtil.getString(
				actionRequest, "importe_total", "0");
		String periodicidad = ParamUtil.getString(actionRequest,
				"periodicidad", null).equals("") ? null : ParamUtil.getString(
				actionRequest, "periodicidad", null);
	/*	String periodoDesde = ParamUtil.getString(actionRequest,
				"periodo_desde", "") == null ? "" : ParamUtil.getString(
				actionRequest, "periodo_desde", "");
		String periodoHasta = ParamUtil.getString(actionRequest,
				"periodo_hasta", "") == null ? "" : ParamUtil.getString(
				actionRequest, "periodo_hasta", "");*/

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(actionRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(actionRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(actionRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(actionRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(actionRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(actionRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

    	int idPrestador = ParamUtil.getInteger(actionRequest,"id_prestador", 0);
    	
		String cuitAcreedor = actionRequest.getParameter("cuit_entidad");
		String sucuAcreedor = actionRequest.getParameter("sucursal_entidad");
		String idSeccional = actionRequest.getParameter("id_seccional");
		
		
		
		//DS Inicio
		sucuAcreedor = "000";
		//DS Fin

/*		
		if (StringUtils.checkNotEmpty(idSeccional)
				&& Integer.parseInt(idSeccional) != 0) {
			sucuAcreedor = "000";
		}
*/
		String observaciones = ParamUtil.getString(actionRequest,
				"observaciones", null);
		String observaciones_int = ParamUtil.getString(actionRequest,
				"observaciones_int", null);
		boolean recuperaApe = Boolean.valueOf(ParamUtil.getString(
				actionRequest, "recupera_ape", "false"));
		int estado = ParamUtil.getInteger(actionRequest, "estado", 0);
		String documentacion = ParamUtil.getString(actionRequest,
				"documentacion", "null");
		
		//DS -2023-08-25 Agregado porque no leia todos los documentos seleccionados. No encuentro motivo
		String[] docs=actionRequest.getParameterValues("documentacion");
		if(docs.length>0) {
		   documentacion=Arrays.toString(docs).replace("[","").replace("]","").replace(" ","");
		}else {
		   documentacion="null";	
		}

		String cantidadViajesMes = ParamUtil.getString(actionRequest,
				"cantidad_viajes_mes", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "cantidad_viajes_mes", "0");
		String cantidadKilometrosDia = ParamUtil.getString(actionRequest,
				"cantidad_kilometros_dia", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "cantidad_kilometros_dia", "0");
		String cantidadKilometrosMes = ParamUtil.getString(actionRequest,
				"cantidad_kilometros_mes", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "cantidad_kilometros_mes", "0");
		String importeKilometroUnit = ParamUtil.getString(actionRequest,
				"importe_kilometro_unit", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "importe_kilometro_unit", "0");
		String hsEsperaDia = ParamUtil.getString(actionRequest,
				"hs_espera_dia", "0").equals("") ? "0" : ParamUtil.getString(
				actionRequest, "hs_espera_dia", "0");
		String hsEsperaMes = ParamUtil.getString(actionRequest,
				"hs_espera_mes", "0").equals("") ? "0" : ParamUtil.getString(
				actionRequest, "hs_espera_mes", "0");
		String importeHsEsperaUnit = ParamUtil.getString(actionRequest,
				"importe_hs_espera_unit", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "importe_hs_espera_unit", "0");

		String importeTercerizado = ParamUtil.getString(actionRequest,
				"importe_tercerizado", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "importe_tercerizado", "0");
		String idTercerizadora = ParamUtil.getString(actionRequest,
				"id_tercerizadora", "");

		String esExcepcion = ParamUtil.getString(actionRequest,
				"es_excepcion", "");
		
		boolean esDiscapacitado = ParamUtil.getBoolean(actionRequest,"es_discapacitado");
		boolean esLeche = ParamUtil.getBoolean(actionRequest,"es_leche");
		boolean esDependencia = ParamUtil.getBoolean(actionRequest,"es_dependencia");
		
		String motivoExcepcion = ParamUtil.getString(actionRequest,
				"motivo_excepcion", "");
		
		
		int idPreautorizacion = ParamUtil.getInteger(actionRequest,
				"idPreautorizacionAux", 0);
		
		
		User user = PortalUtil.getUser(actionRequest);
		int idTratamientoOut = idTratamiento;
		int copago = ParamUtil.getInteger(actionRequest, "copago", 0);
		if (cmd.equals(Constants.ADD)) {
			if (documentacion.equalsIgnoreCase("null")) {
				estado = WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO;
			} else {	
					estado = WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE;
			}
			
			AutoPrestacional autorizacionesPrestacionales =  
					new AutoPrestacional(idTratamiento,idPrestacion, cuil, inte, cantidad, importeTotal,
							periodicidad, fechaDesde, fechaHasta, user, cuitAcreedor,
							sucuAcreedor, idSeccional, observaciones, recuperaApe,
							estado, documentacion, cantidadViajesMes,
							cantidadKilometrosDia, cantidadKilometrosMes,
							importeKilometroUnit, hsEsperaDia, hsEsperaMes,
							importeHsEsperaUnit, importeTercerizado,
							idTercerizadora,idPrestador,esExcepcion,esDiscapacitado,motivoExcepcion,esLeche,esDependencia,observaciones_int);
			
			autorizacionesPrestacionales.setCopago(copago);
			
			idTratamientoOut = AutorizacionPrestacionalServiceUtil.save(autorizacionesPrestacionales, idPreautorizacion);
			actionRequest.setAttribute("id_tratamiento", String
					.valueOf(idTratamientoOut));

		} else {
			idTratamientoOut = ParamUtil.getInteger(actionRequest,
					"id_tratamiento");
			
			AutoPrestacional autorizacionesPrestacionales =  
					new AutoPrestacional(idTratamiento,idPrestacion, cuil, inte, cantidad, importeTotal,
							periodicidad, fechaDesde, fechaHasta, user, cuitAcreedor,
							sucuAcreedor, idSeccional, observaciones, recuperaApe,
							estado, documentacion, cantidadViajesMes,
							cantidadKilometrosDia, cantidadKilometrosMes,
							importeKilometroUnit, hsEsperaDia, hsEsperaMes,
							importeHsEsperaUnit, importeTercerizado,
							idTercerizadora,idPrestador,esExcepcion,esDiscapacitado,motivoExcepcion,esLeche,esDependencia,observaciones_int);
			
			autorizacionesPrestacionales.setCopago(copago);
			
			AutorizacionPrestacionalServiceUtil.update(autorizacionesPrestacionales, idPreautorizacion );
			
			
			
		}
		return idTratamientoOut;
	}

	// POSIBLES VALIDACIONES EN UN FUTURO
	private StringBuilder getTopeCantIndivError(
			TopeCantidadIndividualExedidoException e) {
		StringBuilder error = new StringBuilder();
		error
				.append("La cantidad por reintegro no puede ser excedida. Cantidad ingresada:");
		error.append(e.getCantidad());
		error.append(" - Cantidad max.:");
		error.append(e.getTopeIndivCant());
		return error;
	}

	private StringBuilder getPrestacionHechaError() {
		StringBuilder error = new StringBuilder();
		error
				.append("La prestación ya fue realizada al afiliado y no puede hacerse dos veces");
		return error;
	}

	private StringBuilder getFechasError() {
		StringBuilder error = new StringBuilder();
		error
				.append("La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado");
		return error;
	}

	private void putError(ActionRequest actionRequest, StringBuilder error) {
		actionRequest.setAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT, error
				.toString());
	}

	protected void borraTratamientoDiscapacidadEntry(RenderRequest renderRequest)
			throws Exception {
		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);
		User user = PortalUtil.getUser(renderRequest);
		AutorizacionPrestacionalServiceUtil.borrar(id_tratamiento, user);
//		TratamientoDiscapacidadServiceUtil.borrar(id_tratamiento, user);
	}

	protected int cambioEstadoTratamientoEntry(RenderRequest renderRequest)
			throws Exception {
		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);
		int id_estado = ParamUtil.getInteger(renderRequest, "estado", 0);
		String motivo =ParamUtil.getString(renderRequest, "motivo", "");
						
		User user = PortalUtil.getUser(renderRequest);
		AutorizacionPrestacionalServiceUtil.cambiarEstadoAutorizacion(id_tratamiento, id_estado,user.getScreenName(),motivo);
		
//		TratamientoDiscapacidadServiceUtil.cambiarEstadoTratamiento(
//				id_tratamiento, id_estado, user.getScreenName());
		return id_tratamiento;
	}

	
	public String validarAfiliadoPrestacion(RenderRequest renderRequest,String cmd) throws Exception {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);

		String cuil = ParamUtil.getString(renderRequest, "cuil", null);
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		if (cmd.equalsIgnoreCase(Constants.UPDATE)) {
			
			AutorizacionPrestacional td = AutorizacionPrestacionalServiceUtil.getAutorizacionPrestacional(id_tratamiento);
			cuil = td.getAfiliado().getCuil_titular();
			inte = td.getAfiliado().getInte();
		}
        
	    Afiliado afiliado =EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil, inte);
		
		String mensajeDocumento = "";

		if(afiliado.getBaja_fecha()!=null) {
		  if( (fechaDesde!=null && afiliado.getBaja_fecha().before(fechaDesde)) || 
				     (fechaHasta!=null && fechaHasta.after(afiliado.getBaja_fecha()))){
		   mensajeDocumento=getFechasError().toString();
		  }
		}  
		return mensajeDocumento;
	}
	
	
	////////////////////////
	////////////////////////
	
	private Integer avisosVencimientosCUD(RenderRequest actionRequest)
			throws Exception {

		Integer qDias = ParamUtil.getInteger(actionRequest,
				"qDias", 0);
	
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(actionRequest,
				"fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest,
				"fechaMes");
		String fechaAnio = ParamUtil.getString(actionRequest,
				"fechaAnio");
		Date fecha = null;
		try {
			fecha = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}
		User user = PortalUtil.getUser(actionRequest);
		
		CUDAvisoVencimiento cav = new CUDAvisoVencimiento();
		cav.setDiasAlVencimiento(qDias);
		cav.setFechaOrigen(fecha);
		Integer ret=cav.generaAvisoVencimiento();
		
		
		return ret;
	}
	
	////////////////////////
	////////////////////////
	
	
	
	
}