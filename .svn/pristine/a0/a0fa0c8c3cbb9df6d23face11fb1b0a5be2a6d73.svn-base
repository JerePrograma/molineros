package ar.com.ospim.autorizaciones.action;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
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
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.BusquedaReclamoFiltro;
import ar.com.ospim.autorizaciones.beans.BusquedaReclamoSeccionalFiltro;
import ar.com.ospim.autorizaciones.beans.ItemReclamoPrestacionalesTotal;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.util.StringUtils;

public class BuscarReclamosPrestacionalesAction extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarAutorizacionesPrestacionalesAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.reclamosprestacionales.result.search");			
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			String cmd = null;
			String accion = null;
		try {
			
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
			
			cmd = ParamUtil.getString(renderRequest, Constants.ACTION);
			accion = ParamUtil.getString(renderRequest, Constants.CMD);
			
			
		
			User user = PortalUtil.getUser(renderRequest);
			String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString();

			
			String entidad = ParamUtil
					.getString(renderRequest, "entidad", null);
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			String fechaOspimDia = ParamUtil.getString(renderRequest,
					"fechaOspimDia");
			String fechaOspimMes = ParamUtil.getString(renderRequest,
					"fechaOspimMes");
			String fechaOspimAnio = ParamUtil.getString(renderRequest,
					"fechaOspimAnio");
			Date fechaOspim = null;
			
			try {
				fechaOspim = formatoDeFechas.parse(fechaOspimDia + "/"
						+ (Integer.parseInt(fechaOspimMes) + 1) + "/"
						+ fechaOspimAnio);
			} catch (Exception e) {
				fechaOspim = null;
			}
			String fechaOspimDiaHta = ParamUtil.getString(renderRequest,
					"fechaOspimDiaHta");
			String fechaOspimMesHta = ParamUtil.getString(renderRequest,
					"fechaOspimMesHta");
			String fechaOspimAnioHta = ParamUtil.getString(renderRequest,
					"fechaOspimAnioHta");
			Date fechaOspimHasta = null;
			
			
			try {
				fechaOspimHasta= formatoDeFechas.parse(fechaOspimDiaHta + "/"
						+ (Integer.parseInt(fechaOspimMesHta) + 1) + "/"
						+ fechaOspimAnioHta);
			} catch (Exception e) {
				fechaOspimHasta= null;
			}
			// fechas cierre reclamo 
			
			String fechaCierreReclamoDia = ParamUtil.getString(renderRequest,
					"fechaCierreReclamoDia");
			String fechaCierreReclamoMes = ParamUtil.getString(renderRequest,
					"fechaCierreReclamoMes");
			String fechaCierreReclamoAnio  = ParamUtil.getString(renderRequest,
					"fechaCierreReclamoAnio");
			Date fechaCierreReclamo= null;
			
			try {
				fechaCierreReclamo= formatoDeFechas.parse(fechaCierreReclamoDia + "/"
						+ (Integer.parseInt(fechaCierreReclamoMes) + 1) + "/"
						+ fechaCierreReclamoAnio);
			} catch (Exception e) {
				fechaCierreReclamo = null;
			}
			
			String fechaCierreReclamoDiaHta = ParamUtil.getString(renderRequest,
					"fechaCierreReclamoDiaHta");
			String fechaCierreReclamoMesHta = ParamUtil.getString(renderRequest,
					"fechaCierreReclamoMesHta");
			String fechaCierreReclamoAnioHta  = ParamUtil.getString(renderRequest,
					"fechaCierreReclamoAnioHta");
			Date fechaCierreReclamoHasta= null;
			
			try {
				fechaCierreReclamoHasta= formatoDeFechas.parse(fechaCierreReclamoDiaHta + "/"
						+ (Integer.parseInt(fechaCierreReclamoMesHta) + 1) + "/"
						+ fechaCierreReclamoAnioHta);
			} catch (Exception e) {
				fechaCierreReclamoHasta = null;
			}			
		// resto de parametros de la busqueda

			int numero = ParamUtil.getInteger(renderRequest, "numero", 0);

			String codPrest = ParamUtil.getString(renderRequest, "codPrest", null);
			String codPrestaci = ParamUtil.getString(renderRequest, "codPrestaci", null);
			String prestador = ParamUtil.getString(renderRequest, "prestador",
					null);

			int estado = ParamUtil.getInteger(renderRequest, "estado", 0);
			String estadoSel = ParamUtil.getString(renderRequest, "estado");


			PortletSession portletSession = renderRequest.getPortletSession();

			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
			int nroReclamo = ParamUtil.getInteger(renderRequest, "nroReclamo", 0);
			int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
			int tipoPrestacion= ParamUtil.getInteger(renderRequest, "tipoprestacion", 0);
			
			String code_prestacion = ParamUtil.getString(renderRequest, "code_prestacion", "0");
			int tipoNomencladorBuscado= ParamUtil.getInteger(renderRequest, "tiponomencladorbuscado");	 			
			String cuilTitular = ParamUtil.getString(renderRequest,"cuil_titular", null);
			int idPrestacion = 0;
			int pagina =ParamUtil.getInteger(renderRequest, "pagina");  
			String codigotipogestion = ParamUtil.getString (renderRequest, "codigotipogestion", "0");
			String resolucion= ParamUtil.getString (renderRequest, "resolucion");
			String tipoPedido = ParamUtil.getString (renderRequest, "tipoPedido");
			String sectorSel =ParamUtil.getString (renderRequest, "sectorSel");
			
			int idSeccionalFiltro= ParamUtil.getInteger(renderRequest, "seccional");
			String idSeccionalFiltroTxt = ParamUtil.getString (renderRequest, "seccional");
			String seccionalDesc = ParamUtil.getString (renderRequest, "seccionalDesc");

			
			
			String tipoDocumento =ParamUtil.getString (renderRequest, "tipoDoc");
			String nroDocumento =ParamUtil.getString (renderRequest, "nroDoc");

			String apellido =ParamUtil.getString (renderRequest, "apellido");
			String nombre =ParamUtil.getString (renderRequest, "nombre");
			String seccionalAfiSel =ParamUtil.getString (renderRequest, "seccionalSelAfi");
			
			String inteFiltro =ParamUtil.getString (renderRequest, "inteFiltro");
			String nroReclamoFiltro = ParamUtil.getString(renderRequest, "nroReclamoFiltro");
			String nroAfiFiltro = ParamUtil.getString(renderRequest, "numero_afi");
			String descSeccionalSelAfi = ParamUtil.getString(renderRequest, "descSeccionalSelAfi");

			String sucursalEntidad = ParamUtil.getString(renderRequest, "sucursal_entidad");
			String entidadEmpresa = ParamUtil.getString(renderRequest, "entidadEmpresa");

			String tipoGestionFiltro = ParamUtil.getString(renderRequest, "tipogestionFiltro");
			String codePrestacionFiltro = ParamUtil.getString(renderRequest, "code_prestacion");
			String descPrestacionFiltro = ParamUtil.getString(renderRequest, "descripcionSeguimiento_filtro");
			String tipoPrestacionSel = ParamUtil.getString(renderRequest, "tipoPrestacionFiltro");
			String descMedicamentoDesc = ParamUtil.getString(renderRequest, "nombre_medicamento");
			String entidadAfi = ParamUtil.getString(renderRequest, "entidadAfi");

			
			
			if (StringUtils.checkNotEmpty(cmd) && WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmd)
					&& Constants.EXPIRE.equals(accion) ){
				
				session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_SECCIONAL);
				portletSession.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES);
				session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_TOTAL_REGISTROS );
				session.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO);
				
				return mapping.findForward("portlet.autorizaciones.reclamosprestacionales_seccional.result.search");

			}
			if (StringUtils.checkNotEmpty(cmd) && Constants.EXPIRE.equals(accion) ){
				
				session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS);
				portletSession.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES);
				session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_TOTAL_REGISTROS );
				session.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO);
				
				return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.result.search");
			}

			
			Integer idSeccioanl ;
		    boolean marcaSeccional = false;   
		    
			if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmd) && "SELECCIONE".equalsIgnoreCase(sectorSel)){
				sectorSel = WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL;
			}

			
		    Integer nroAutorizacion=ParamUtil.getInteger(renderRequest, "nroautorizacion", 0);
		    
		    if (tipoPrestacion==1){
		    	List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(tipoNomencladorBuscado,"",0, code_prestacion ,false,"");
				   for(Nomenclador nom:nomencladores){			   				   
					   idPrestacion= nom.getId_prestacion();   		// prestacion clinica 		   				    
				   }
				   if ( idPrestacion==0){
					   _log.debug("Error en la busqueda de id prestacion : ");
				   }            					
		    }else{	
		    	if (code_prestacion!="" || code_prestacion!="0"){
		    		idPrestacion=Integer.parseInt(code_prestacion); // farmacia
		    	}else{
		    		idPrestacion=0; // no se selecciono ninguna prestacion para la busqueda 
		    	}		    	 
		    }
		    		    
		    int totalrecords=0;	
		    Integer nroLote=ParamUtil.getInteger(renderRequest, "nrolote",0);
		    String nroLoteTxt=ParamUtil.getString(renderRequest, "nrolote");

			//inicio Datos del Comprobante  
			String frecuencia = ParamUtil.getString(renderRequest, "frecuencia", null);
			String comprobanteTipo = ParamUtil.getString(renderRequest, "comprobante_tipo", null);
			String comprobanteSuc = ParamUtil.getString(renderRequest, "comprobante_suc", null);
			String comprobanteNro = ParamUtil.getString(renderRequest, "comprobante_nro", null);

			String fechaComprobanteDia = ParamUtil.getString(renderRequest,
					"fechaComprobanteDia");
			String fechaComprobanteMes = ParamUtil.getString(renderRequest,
					"fechaComprobanteMes");
			String fechaComprobanteAnio  = ParamUtil.getString(renderRequest,
					"fechaComprobanteAnio");
			Date fechaComprobante= null;
			
			try {
				fechaComprobante= formatoDeFechas.parse(fechaComprobanteDia + "/"
						+ (Integer.parseInt(fechaComprobanteMes) + 1) + "/"
						+ fechaComprobanteAnio);
			} catch (Exception e) {
				fechaComprobante = null;
			}	
			
			
			String fechaSeccionalDia = ParamUtil.getString(renderRequest,
					"fechaSeccionalDia");
			String fechaSeccionalMes = ParamUtil.getString(renderRequest,
					"fechaSeccionalMes");
			String fechaSeccionalAnio  = ParamUtil.getString(renderRequest,
					"fechaSeccionalAnio");
			Date fechaSeccional= null;
			
			try {
				fechaSeccional= formatoDeFechas.parse(fechaSeccionalDia + "/"
						+ (Integer.parseInt(fechaSeccionalMes) + 1) + "/"
						+ fechaSeccionalAnio);
			} catch (Exception e) {
				fechaSeccional = null;
			}	
			
			String fechaSeccionalDiaHta = ParamUtil.getString(renderRequest,
					"fechaSeccionalDiaHta");
			String fechaSeccionalMesHta = ParamUtil.getString(renderRequest,
					"fechaSeccionalMesHta");
			String fechaSeccionalAnioHta  = ParamUtil.getString(renderRequest,
					"fechaSeccionalAnioHta");
			
			Date fechaSeccionalhta= null;
			try {
				fechaSeccionalhta= formatoDeFechas.parse(fechaSeccionalDiaHta + "/"
						+ (Integer.parseInt(fechaSeccionalMesHta) + 1) + "/"
						+ fechaSeccionalAnioHta);
			} catch (Exception e) {
				fechaSeccionalhta = null;
			}			
			
			
			
			
			String cuitEntidad = ParamUtil.getString(renderRequest, "cuit_entidad", null);

			if (idSeccionalFiltro != 0){
				idSeccioanl = idSeccionalFiltro;
			}else{				
				idSeccioanl = Integer.parseInt(seccionalDefecto);
			}
			
			int codintegracion = ParamUtil.getInteger(renderRequest, "integracion", 0);
			
			int recuperableSur = ParamUtil.getInteger(renderRequest, "recuperable_sur", 0);

			
			
			//fin Datos del Comprobante  
			
			if (StringUtils.checkNotEmpty(cmd) && WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmd)){
				marcaSeccional = true;
				// guardo el filtro busqueda
				// Se agrega el estado del Reclamo en Seccional 0 PreCarga -> Todos (PreCarga y Observados)
				// Tambien deberia permitir traer "solo precargar o solo observados"
				BusquedaReclamoSeccionalFiltro filtroSelec = new BusquedaReclamoSeccionalFiltro(
						nroReclamoFiltro, tipoPedido, estadoSel, sectorSel, fechaSeccional, 
						fechaSeccionalhta,nroAfiFiltro, cuilTitular, inteFiltro, tipoDocumento, nroDocumento, 
						 apellido, nombre, seccionalAfiSel,descSeccionalSelAfi);
				session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_SECCIONAL);
				session.setAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_SECCIONAL,	filtroSelec);
				   
			}else{
				BusquedaReclamoFiltro filtroReclamo = 
						new BusquedaReclamoFiltro(nroReclamoFiltro, tipoPedido, sectorSel, resolucion, estadoSel, 
								tipoGestionFiltro, nroLoteTxt, seccionalAfiSel, descSeccionalSelAfi, tipoPrestacionSel, 
								fechaOspim, fechaOspimHasta, fechaCierreReclamo, fechaCierreReclamoHasta, nroAfiFiltro,
								cuilTitular, inteFiltro, tipoDocumento, nroDocumento, apellido, nombre, idSeccionalFiltroTxt, 
								seccionalDesc, frecuencia, comprobanteTipo, comprobanteSuc, 
								comprobanteNro, fechaComprobante, cuitEntidad, sucursalEntidad, entidadEmpresa,
								code_prestacion,descMedicamentoDesc,code_prestacion,descPrestacionFiltro,entidadAfi,codintegracion,recuperableSur);
				session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS);
				session.setAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS,	filtroReclamo);
			}

		    
		    BusquedaReclamosPrestacionalesFiltro filtro = new BusquedaReclamosPrestacionalesFiltro(entidad, fechaOspim, fechaOspimHasta, fechaCierreReclamo, fechaCierreReclamoHasta, numero, codPrest, codPrestaci, 
		    													prestador, estado, inte, nroReclamo, nroAfi, tipoPrestacion, code_prestacion, tipoNomencladorBuscado, cuilTitular, idPrestacion, codigotipogestion, resolucion, 
		    													tipoPedido, sectorSel, nroAutorizacion, pagina,nroLote, frecuencia, comprobanteTipo, comprobanteSuc, comprobanteNro, fechaComprobante, cuitEntidad, idSeccioanl,
		    													fechaSeccional, fechaSeccionalhta, marcaSeccional,codintegracion,recuperableSur);
		    
		    if(marcaSeccional && (idSeccioanl==null || idSeccioanl==0) &&   seccionalAfiSel!=null && !"".equals(seccionalAfiSel)){
		    	   filtro.setIdSeccional(Integer.parseInt(seccionalAfiSel));
				   
			}
			
		    List<ItemReclamoPrestacionalesTotal> busqueda = ReclamosPrestacionesServiceUtil.buscarReclamosPrestacionalTotales(filtro);
			if (busqueda.size()>0){
				totalrecords = busqueda.get(0).getTotal_registros();
			}else{
				totalrecords =0;
			}
			portletSession.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES);
			portletSession.setAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES,	busqueda);
			
			session.setAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_TOTAL_REGISTROS, totalrecords );
			session.setAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_OFFSET_REG, pagina);

			session.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO);
			session.setAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO, filtro);

		
			
		} catch (Exception e) {
			_log.error(e);
		}
		if ( WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmd)){
			return mapping.findForward("portlet.autorizaciones.reclamosprestacionales_seccional.result.search");
		}else{			
			return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.result.search");
		}
	}
	
	
}




