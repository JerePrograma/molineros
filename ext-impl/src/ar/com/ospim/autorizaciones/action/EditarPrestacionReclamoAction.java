package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.util.StringUtils;


public class EditarPrestacionReclamoAction  extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(EditarPrestacionReclamoAction .class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		String cmdSeccional = ParamUtil.getString(renderRequest, Constants.ACTION, null);

		String frecuencia = ParamUtil.getString(renderRequest, "frecuencia");
		double importe  = ParamUtil.getDouble(renderRequest, "importe");
		double cantidad = ParamUtil.getDouble(renderRequest, "cantidad");
		double cargoOspim = ParamUtil.getDouble(renderRequest, "cargoospim");
		double cargoPs = ParamUtil.getDouble(renderRequest, "cargops");
		double cargoImesa = ParamUtil.getDouble(renderRequest, "cargoimesa");
		/* Reconocido SSS permanece neutralizado en este flujo. */
		double reconocidoSSS = 0D;
		String prestacion= ParamUtil.getString(renderRequest, "prestacion");
		String observaciones= ParamUtil.getString(renderRequest, "observaciones");		
		int idPrestacion= ParamUtil.getInteger(renderRequest, "idprestacion");
		int idRegistro= ParamUtil.getInteger(renderRequest, "idRegistro");
		int tipoEdicion= ParamUtil.getInteger(renderRequest, "tipoEdicion");
		boolean GrabaEdicion= ParamUtil.getBoolean(renderRequest, "grabaedicion");
		int estadoAprobaRechazado = ParamUtil.getInteger(renderRequest, "estadoAprobacion");
		int recuperableSur = ParamUtil.getInteger(
				renderRequest,
				"recuperableSur",
				0
		);

		if (recuperableSur < 0 || recuperableSur > 3) {
			recuperableSur = 0;
		}

		Integer recuperable = Integer.valueOf(recuperableSur);
		
		String idTercerizadora = ParamUtil.getString(renderRequest, "id_tercerizadora", "");

		if (StringUtils.checkEmpty(idTercerizadora)
		        || "null".equalsIgnoreCase(idTercerizadora)
		        || "undefined".equalsIgnoreCase(idTercerizadora)) {
			idTercerizadora = null;
		}
		
		String codigoPrestacion= ParamUtil.getString(renderRequest, "codigoPrestacion");
		String cpteTipo= ParamUtil.getString(renderRequest, "cpbte_tipo");
		String cpteNro= ParamUtil.getString(renderRequest, "cpbte_nro");
		int cpteDia=ParamUtil.getInteger(renderRequest,"cpbte_dia");
		int cpteMes=ParamUtil.getInteger(renderRequest,"cpbte_mes");
		int cpteAnio=ParamUtil.getInteger(renderRequest,"cpbte_anio");
		Double cpbteCantidad  = ParamUtil.getDouble(renderRequest, "cpbte_cantidad");
		Double cpbteImporte= ParamUtil.getDouble(renderRequest, "cpbte_importe");
		Double cpbteTotal  = ParamUtil.getDouble(renderRequest, "importeFC");
		String cpteCUIT= ParamUtil.getString(renderRequest, "cpbte_cuit");
		String cpteCUITSucursal= ParamUtil.getString(renderRequest, "cpbte_cuit_sucursal");
		String cpteSucursal= ParamUtil.getString(renderRequest, "cpbte_sucursal");
		
		int fechaPrestacionDia = ParamUtil.getInteger(renderRequest, "fecha_prestacion_dia");
		int fechaPrestacionMes = ParamUtil.getInteger(renderRequest, "fecha_prestacion_mes");
		int fechaPrestacionAnio = ParamUtil.getInteger(renderRequest, "fecha_prestacion_anio");
		
		int idMedicamentoEdit = ParamUtil.getInteger(renderRequest, "id_medicamento_edit");
		String nombreMedicamentoEdit = ParamUtil.getString(renderRequest, "nombre_medicamento_edit");

		String codigoSeguimientoFiltroEdit= ParamUtil.getString(renderRequest, "codigoSeguimiento_filtro_edit");
		String descripcionSeguimientoFiltroEdit= ParamUtil.getString(renderRequest, "descripcionSeguimiento_filtro_edit");
		int nomSeleccionadoEdit= ParamUtil.getInteger(renderRequest, "nom_seleccionado_edit",0);
		int tipoNomencladorEdit= ParamUtil.getInteger(renderRequest, "tipoNomenclador_edit",0);

		String cpbteLetra = ParamUtil.getString(renderRequest, "cpbte_letra");

		int idPrestacionNomenclador = 0;
		
		if (nomSeleccionadoEdit==1){
			idPrestacionNomenclador=0;
			List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(tipoNomencladorEdit,"",0, codigoSeguimientoFiltroEdit,false,"");
			for(Nomenclador nom:nomencladores){				   
				if(codigoSeguimientoFiltroEdit.equals(nom.getCodigo())){
					idPrestacionNomenclador= nom.getId_prestacion();
				}	   
			}
			if (idPrestacionNomenclador==0){
				_log.debug("Error en la busqueda de id prestacion : ");
			}
		}
		
		Calendar cpbteFecha = null;
		try {
			if(cpteAnio>0 && cpteDia > 0) {
				cpbteFecha = Calendar.getInstance();
				cpbteFecha.set(cpteAnio, cpteMes, cpteDia);
			}	
		}catch(Exception e){		
			cpbteFecha=null;	
		}
		
		Calendar fechaPrestacion = null;
		try {
			if(fechaPrestacionAnio >0 && fechaPrestacionDia > 0) {
				fechaPrestacion = Calendar.getInstance();
				fechaPrestacion.set(fechaPrestacionAnio, fechaPrestacionMes, fechaPrestacionDia);
			}	
		}catch(Exception e){		
			fechaPrestacion=null;	
		}
		
		renderRequest.setAttribute("tipoEdicion", tipoEdicion);
		
		if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdSeccional)){
			renderRequest.setAttribute("ocultar", "S");
		}

		PrestacionesReclamo presta = new PrestacionesReclamo();
		presta.setIdRegistro(idRegistro);
		_log.debug("Editando prestacion id: " + idRegistro);
		presta.setCodigoPrestacion(codigoPrestacion);
		List<PrestacionesReclamo> listaPrestacionesReclamo = (ArrayList<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
		try {
			int pos = listaPrestacionesReclamo.indexOf(presta);
			presta = listaPrestacionesReclamo.get(pos);
			if (GrabaEdicion) {
				if (estadoAprobaRechazado >1 && observaciones!=""){
					presta.setObservacionesAutorizaRechaza(presta.getObservaciones() + " (OBSERVACION: " + observaciones + " )");
					presta.setObservaciones(presta.getObservaciones() + " (OBSERVACION: " + observaciones + " )");
				}else{
					presta.setObservaciones(observaciones);
				}
				presta.setCantidad(cantidad);
				presta.setImporte(importe);
				presta.setFrecuencia(frecuencia);
				presta.setCargo_ospim(cargoOspim);
				presta.setReconocidoSSS(0D);
				presta.setCargo_ps(cargoPs);
				presta.setCargo_imesa(cargoImesa);
				if (!StringUtils.checkEmpty(idTercerizadora)
				        && !"null".equalsIgnoreCase(idTercerizadora)
				        && !"undefined".equalsIgnoreCase(idTercerizadora)) {
					presta.setIdTercerizadora(idTercerizadora);
				}
				presta.setRecuperable(recuperable);
				presta.setRecuperableSur(
						Boolean.valueOf(recuperable.intValue() == 1)
				);
				presta.setEstado(PrestacionesReclamo.ESTADOS.MODIF);
				presta.setEstadoRechazoAprobado(!PrestacionesReclamo.ESTADOS.BAJA.equals(presta.getEstado()) ? estadoAprobaRechazado : 0);
				presta.setIdRegistro(idRegistro);
				presta.setComprobanteCantidad(cpbteCantidad);
				presta.setComprobanteFecha(cpbteFecha!=null?cpbteFecha.getTime():null);
				presta.setComprobanteImporte(StringUtils.checkNotEmpty(cpbteImporte)?cpbteImporte:null);
				presta.setComprobanteNro(StringUtils.checkNotEmpty(cpteNro)?cpteNro:null);
				presta.setComprobanteTipo(StringUtils.checkNotEmpty(cpteTipo)?cpteTipo:null);
				presta.setComprobanteTotal(StringUtils.checkNotEmpty(cpbteTotal)?cpbteTotal:null);
				presta.setComprobanteCUIT(cpteCUIT);
				presta.setComprobanteSucursal(cpteSucursal);
				presta.setComprobanteLetra(cpbteLetra);
				presta.setComprobanteCUITSucursal(cpteCUITSucursal);
				presta.setFechaPrestacion(fechaPrestacion!=null?fechaPrestacion.getTime():null);
				presta.setId_medicamento(idMedicamentoEdit);
				presta.setId_prestacion(idPrestacionNomenclador);
				if (idMedicamentoEdit != 0){
					presta.setCodigoPrestacion(String.valueOf(idMedicamentoEdit));
					presta.setDescripcion(nombreMedicamentoEdit);
				}else{
					presta.setCodigoPrestacion(codigoSeguimientoFiltroEdit);
					presta.setDescripcion(descripcionSeguimientoFiltroEdit);
				}

				listaPrestacionesReclamo.remove(pos);
				session.removeAttribute(WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION);
				listaPrestacionesReclamo.add(presta);
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION, listaPrestacionesReclamo);
				
				if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdSeccional)){
					return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo_seccional");
				}else{
					return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo");
				}
			}else{
				if(StringUtils.checkNotEmpty(presta.getComprobanteCUIT())) {
					Empresa empr = EmpresaServiceUtil.getEmpleadorCompleto(presta.getComprobanteCUIT(), presta.getComprobanteCUITSucursal());
					presta.setComprobanteRazonSocial(empr!=null&&StringUtils.checkNotEmpty(empr.getRazon_soc())?empr.getRazon_soc():"");
				}
				presta.setRecuperable(recuperable);
				presta.setRecuperableSur(
						Boolean.valueOf(recuperable.intValue() == 1)
				);
				presta.setReconocidoSSS(0D);
				session.setAttribute(WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION, presta);
			}
		}catch(Exception e){
			_log.error("Editando prestacion", e);
		}
		return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.edicion_prestacion_reclamo");
	}
}