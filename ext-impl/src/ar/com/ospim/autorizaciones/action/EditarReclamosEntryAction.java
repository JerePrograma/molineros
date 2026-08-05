package ar.com.ospim.autorizaciones.action;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.beans.AfiCuentasBancarias;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.CieDiez;
import ar.com.ospim.afiliados.services.AfiCuentasBancariasServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.autorizaciones.beans.AfiCuentaBancaria;
import ar.com.ospim.autorizaciones.beans.ItemReclamoPrestacionalesTotal;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.desarrolloAppMobile.beans.ClienteAppMobile;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.PermissionUtil;
import ar.com.ospim.util.StringUtils;


	public class EditarReclamosEntryAction extends ReclamosBaseAction {
		
	private Logger _log = Logger.getLogger(this.getClass());
	
	private static final String RECLAMO_PRESTACION_ESTADO_ORIGINAL =
            "RECLAMO_PRESTACION_ESTADO_ORIGINAL";
	
	private static final int PLAN_COBERTURA = 3;
	private static final int PLAN_COBERTURA_TOTAL_O = 9;
	private static final int PLAN_COBERTURA_TOTAL_M = 20;
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();

		PortletSession portletSession = actionRequest.getPortletSession();

		
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		String cmdAction = ParamUtil.getString(actionRequest, Constants.ACTION);
		String contextoCompraNonce = ParamUtil.getString(
				actionRequest,
				WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
				""
		);

        if (!StringUtils.checkEmpty(contextoCompraNonce)) {
            actionResponse.setRenderParameter(
                    WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
                    contextoCompraNonce
            );

            actionResponse.setRenderParameter(
                    "origen",
                    "compras"
            );

            actionResponse.setRenderParameter(
                    Constants.CMD,
                    Constants.SAVE.equals(cmd)
                            ? Constants.SAVE
                            : Constants.ADD
            );
        }
		
		if(!StringUtils.checkEmpty(cmd)){
			if(cmd.equals("upload")){
				UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
				String fileName = uploadReq.getFileName("archivo").toLowerCase();
				_log.info("subiendo archivo :" + fileName);
				if (fileName != null) {
					File zip = uploadReq.getFile("archivo");
					String ss ="";
					if ( fileName.endsWith(".xls")) {
						
						FileInputStream file = new FileInputStream(zip);
						HSSFWorkbook workbook = new HSSFWorkbook(file);
						
						HSSFSheet sheet = workbook.getSheetAt(0);
						Iterator<Row> rowIterator = sheet.iterator();
						
				        while (rowIterator.hasNext()) {
				        	Row currentRow = rowIterator.next();
				        	Iterator<Cell> cellIterator = currentRow.iterator();
				        	while (cellIterator.hasNext()) {
				        		Cell currentCell = cellIterator.next();
				        		int cellIndex = currentCell.getColumnIndex();
				        		Double xval;
				        		switch (cellIndex) {
								case 0:
									xval= currentCell.getNumericCellValue();
									ss +=String.valueOf(xval.longValue()) +";";
									break;
				        		}	
				        	}
				        }	
						session.setAttribute("RECLAMOS_PROCESAR_IMAGENES", ss );	
					}
				}	
			}
		}

		
		ReclamoPrestacional reclamoprestacional =null;	
		Boolean esDatosTab = ParamUtil.getBoolean(actionRequest, "esDatosTab");	
		
		if (esDatosTab){
			reclamoprestacional =getReclamoPrestacionalFromRequest(PortalUtil.getHttpServletRequest(actionRequest), reclamoprestacional ,cmdAction, cmd);
			session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoprestacional );	
			portletSession.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoprestacional);
		}
	}
	
	

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		PortletSession portletSession = renderRequest.getPortletSession();

		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		String cmdAction = ParamUtil.getString(renderRequest, Constants.ACTION);

		//limieza automatica al cambiar entre pestañas
		String tabActual = ParamUtil.getString(renderRequest, "tab", "");
		_log.info("TAB ACTUAL = " + tabActual);
		_log.info("CMD = " + cmd);

		//no se limpia si estoy guardando la cuenta
		if (cmd.equals(WebKeysAutorizaciones.CUENTA)) {
		    _log.info("No se limpia nada porque estoy guardando la cuenta");
		} 
		else {

		    //entra a CTA Bancaria sin seleccionar cuenta, se limpia
		    if ("cta_bancaria".equalsIgnoreCase(tabActual) &&
		        !WebKeysAutorizaciones.CUENTA_SELECT.equals(cmd)) {

		        _log.info("Entro a CTA BANCARIA sin seleccionar cuenta");

		        renderRequest.getPortletSession().removeAttribute(
		            "ID_CUENTA_BANCARIA_SELECCIONADA",
		            PortletSession.PORTLET_SCOPE
		        );
		        session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT);
		    }

		    //sale de CTA Bancaria, limpio
		    if (!"cta_bancaria".equalsIgnoreCase(tabActual)) {

		        _log.info("Salgo de CTA BANCARIA");

		        renderRequest.getPortletSession().removeAttribute(
		            "ID_CUENTA_BANCARIA_SELECCIONADA", 
		            PortletSession.PORTLET_SCOPE
		        );
		        session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT);
		    }
		}
		
		User user = PortalUtil.getUser(renderRequest);
		ReclamoPrestacionalCompraContexto contextoCompra = null;

		try {
			contextoCompra = resolverContextoCompra(
					session,
					renderRequest,
					user
			);
		} catch (Exception contextoError) {
			limpiarSesionHandoffCompra(session);
			_log.warn(
					"Se rechazo un contexto invalido de Compras.",
					contextoError
			);
			SessionErrors.add(renderRequest, "error-reclamo-compras");
			renderRequest.setAttribute(
					"msgErrorReclamoCompras",
					contextoError.getMessage()
			);
			renderRequest.setAttribute(Constants.CMD, Constants.ADD);

			return mapping.findForward(getForward(
					renderRequest,
					"portlet.autorizaciones.reclamosprestacionales."
							+ "editar_reclamos_entry"
			));
		}

		String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString();
		
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		tabSel="null".equalsIgnoreCase(tabSel)?"datos":tabSel;
		
		boolean validaOk = true;
		
		
		
		int idReclamo = 0;
		
        int idReclamoDeBuscador = ParamUtil.getInteger(renderRequest, "id_reclamosel",0); 
        int casoAsociado=ParamUtil.getInteger(renderRequest, "casoasociado",0);       	
        
        int autorizacion =ParamUtil.getInteger(renderRequest, "autorizacion",0);
        

        if(autorizacion==1 ){
        	renderRequest.setAttribute("tabs1", "autorizaciones-prestacionales");
			return mapping.findForward("portlet.autorizaciones.view");
		}
        
       if(StringUtils.checkEmpty(cmd)){ 			
			this.cargarListas(renderRequest);
		}	
		
		ReclamoPrestacional reclamoPrestacional =null;
		ReclamoPrestacional reclamoPrestacionalBase =null;

		// carga la lista de CIE 10
		TraeListasServiceUtil.getListadoCieDiez(renderRequest);	
		
		
		
		if(!StringUtils.checkEmpty(cmd)){
			if (idReclamoDeBuscador != 0){				
				reclamoPrestacionalBase = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);
			}
			
			if(cmd.equals("upload")){
				 return mapping.findForward("portlet.autorizaciones.reporte.imagenes_reclamos");	
			}
			
			//Se utiliza el Reset para volver a estado... Recibido por parametro, en este caso: OBSERVADO 
			if(cmd.equals(Constants.RESET)){
				
				String _obs = ParamUtil.getString(renderRequest, "obs","");
				
				// Cambia estado a 5 -> OBSERVADO
				// ReclamosPrestacionesServiceUtil.cambiarEstado(idReclamoDeBuscador, 5, user.getScreenName());
				ReclamosPrestacionesServiceUtil.cambiarEstado(idReclamoDeBuscador, ReclamoPrestacional.getEstadoObservado(), _obs, user.getScreenName());
				
				reclamoPrestacional = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);						
				session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );					
				session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional );	
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getPrestaciones());
				session.setAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getRevisiones());
				
				// Guarda Flag para refresh de vista
				session.removeAttribute(WebKeysAutorizaciones.RECLAMO_NUEVO_ESTADO_OBS );					
				session.setAttribute(WebKeysAutorizaciones.RECLAMO_NUEVO_ESTADO_OBS, ReclamoPrestacional.getEstadoObservado());
				
				//Envia mail ante cambio de estado a Observado
				ReclamoPrestacional reclamoPrestacionalAux =  null;
				reclamoPrestacionalAux =  ReclamosPrestacionesServiceUtil.getReclamoPrestacional(reclamoPrestacional.getId_reclamo() );	
				ReclamoPrestacionalEmailSeccional.getInstance().enviarEmailSeccionalObservado(reclamoPrestacionalAux, _obs);				
			}
			
			//Reabre el reclamo prestacional, si no tiene una OP y esta en estado cerrado
			if(cmd.equals(Constants.RESTORE)){
				
				
				ReclamosPrestacionesServiceUtil.reabrirReclamo(idReclamoDeBuscador, user.getScreenName());
				
				
				reclamoPrestacional = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);						
				session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );					
				session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional );	
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getPrestaciones());
				session.setAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getRevisiones());					
		    }
			
			
			if(cmd.equals(Constants.DELETE)){
				
				try {
				  ReclamosPrestacionesServiceUtil.borrar(idReclamoDeBuscador , user);
				  
				  	//buscar el reclamo para obtener idExterno
			        ReclamoPrestacional rp = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);

			        if (rp != null) {
			            Integer idExterno = rp.getIdReintegroApp();
			            if (idExterno != null && idExterno > 0) {
			                String token = ClienteAppMobile.obtenerToken();
			                if (token != null) {
			                    try {
			                        // actualiza estado RE en base externa
			                        ClienteAppMobile.actualizarEstadoReintegro(idExterno, "AN", token);
			                    } catch (Exception e) {
			                        _log.error("Error al actualizar estado");
			                    }
			                } else {
			                    _log.warn("Token nulo al eliminar reclamo");
			                }
			            } else {
			                _log.debug("Reclamo eliminado");
			            }
			        }

			    } catch (Exception e) {
			        _log.error("Error eliminando reclamo");
			    }
				
				  BusquedaReclamosPrestacionalesFiltro filtro = null ;
				  
				  filtro =  (BusquedaReclamosPrestacionalesFiltro) session.getAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO);
				  
				  List<ItemReclamoPrestacionalesTotal> busqueda = ReclamosPrestacionesServiceUtil.buscarReclamosPrestacionalTotales(filtro);
 
				  portletSession.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES);
				  portletSession.setAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES,	busqueda);
			      idReclamoDeBuscador=0;
			      if ( WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)){
						return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.busqueda.search.seccional");
				  }else{	
						return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.busqueda.search");
				  }
			}
			if(cmd.equals("cerrar")){

				  ReclamosPrestacionesServiceUtil.cerrarLote(user.getScreenName());
				  String msg = "Se ha Cerrado el lote ";
				  SessionMessages.add(renderRequest, "insertCabOk");
				  renderRequest.setAttribute("msgCabOk", msg);
			      return mapping.findForward("portlet.autorizaciones.view");
		    }
			
			if ( idReclamoDeBuscador==0 || cmd.equals(Constants.UPDATE) ){
				 reclamoPrestacional = (ReclamoPrestacional) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);	
			}else{
				reclamoPrestacional = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);
				 //reclamoPrestacional = (ReclamoPrestacional) portletSession.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);	
				if (reclamoPrestacional.getEstado()==3) {
						ReclamosPrestacionesServiceUtil.setDatosOpReclamoPrestacional(reclamoPrestacional);
				}
			}
			
			

			if (cmd.equals(WebKeysAutorizaciones.CUENTA)) {

			    renderRequest.setAttribute("tab", "cta_bancaria");

			    //se obtiene el reclamo en edicion con lo ingresado en la pantalla
			    ReclamoPrestacional reclamoPrestacionalEdit =
			            getReclamoPrestacionalFromRequest(
			                    PortalUtil.getHttpServletRequest(renderRequest),
			                    reclamoPrestacional,
			                    null,
			                    cmd);

			    //evita nullPointer con imagenes CBU si titular no es Apoderado
			    if (reclamoPrestacionalEdit.getCuenta() != null &&
			        !"2".equalsIgnoreCase(reclamoPrestacionalEdit.getCuenta().getCmbTitular())) {

			        if (StringUtils.checkEmpty(reclamoPrestacionalEdit.getCuenta().getImagenCBU())) {
			            CuentaDocumentoHelper.getImagenNombre(reclamoPrestacionalEdit);
			        }
			    }

			    //ID cuenta seleccionada
			    Integer idCuenta = (Integer) renderRequest.getPortletSession()
			            .getAttribute("ID_CUENTA_BANCARIA_SELECCIONADA", PortletSession.PORTLET_SCOPE);

			    _log.info("CUENTA ID: " + idCuenta);

			    //si se selecciona una cuenta convertir y asociar al reclamo antes de grabar
			    if (idCuenta != null && idCuenta > 0) {
			        Connection con = null;
			        try {
			            con = ConnectionHelper.getConnection();
			            AfiCuentasBancarias afiCuenta =
			                    AfiCuentasBancariasServiceUtil.getCuentaPorId(con, idCuenta);

			            if (afiCuenta != null) {
			                ReclamoPrestacionalCuenta cuenta =
			                        convertirAReclamoCuenta(afiCuenta, reclamoPrestacionalEdit.getId_reclamo());
			                reclamoPrestacionalEdit.setCuenta(cuenta);
			            } else {
			                _log.error("No se encontró cuenta bancaria con ID: " + idCuenta);
			            }
			        } finally {
			            ConnectionHelper.cerrar(con);
			        }
			    }
			    
			    //guarda cuenta en BD
			    ReclamosPrestacionesServiceUtil.altaModiCuenta2(
			            reclamoPrestacionalEdit,
			            user,
			            idCuenta);

			   
			    
			    //limpia la cuenta seleccionada
			    renderRequest.getPortletSession().removeAttribute(
			            "ID_CUENTA_BANCARIA_SELECCIONADA",
			            PortletSession.PORTLET_SCOPE);
			    _log.info("Se limpió ID_CUENTA_BANCARIA_SELECCIONADA");

			    //recarga del reclamo desde BD
			    session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);

			    reclamoPrestacional =
			            ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);


			    //carga la cuenta del reclamo desde BD
			    ReclamoPrestacionalCuenta cuentaGrabada =
			            ReclamosPrestacionesServiceUtil.getReclamoPrestacionalCuenta(idReclamoDeBuscador);

			    if (cuentaGrabada != null) {
			        reclamoPrestacional.setCuenta(cuentaGrabada);
			        _log.info("Cuenta de reclamo cargada: CBU=" + cuentaGrabada.getCbu());
			    } else {
			        _log.info("Reclamo sin cuenta asociada en BD");
			    }

			    //guarda reclamo cuenta actualizado en sesión
			    session.setAttribute(
			            WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION,
			            reclamoPrestacional);
			}


			
			if (cmd.equals(WebKeysAutorizaciones.CUENTA_SELECT)) {

			    renderRequest.setAttribute("tab", "cta_bancaria");
			    renderRequest.setAttribute("imagenes", "si");

			    int idCuenta = ParamUtil.getInteger(renderRequest, "id_cuenta_select", 0);
			    _log.info("CUENTA_SELECT ID: " + idCuenta);

			    Connection con = null;
			    AfiCuentasBancarias afiCuenta = null;

			    try {
			        con = ConnectionHelper.getConnection();
			        afiCuenta = AfiCuentasBancariasServiceUtil.getCuentaPorId(con, idCuenta);
			    } finally {
			        ConnectionHelper.cerrar(con);
			    }

			    ReclamoPrestacionalCuenta cuentaSelect = CuentaDocumentoHelper.getCuentas(afiCuenta);

			    // copiar CUIL manualmente
			    if (afiCuenta != null) {
			        cuentaSelect.setCuil(afiCuenta.isTitular()
			            ? afiCuenta.getCuilTitular()
			            : afiCuenta.getCuilCbu());
			    }
			    // limpiar anterior
			    session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT);

			    // guardar cuenta seleccionada
			    session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT, cuentaSelect);

			    // limpiar y setear ID en portlet scope
			    renderRequest.getPortletSession().removeAttribute("ID_CUENTA_BANCARIA_SELECCIONADA", PortletSession.PORTLET_SCOPE);
			    renderRequest.getPortletSession().setAttribute(
			        "ID_CUENTA_BANCARIA_SELECCIONADA", idCuenta, PortletSession.PORTLET_SCOPE
			    );

			    _log.info("CUENTA_SELECT ID guardada en sesión: " + idCuenta);
			}

			
			
			if (!WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction) && 
					reclamoPrestacional.getEstado()<0){ // VALIDA ESTADO RECLAMO 
				SessionErrors.add(renderRequest, "error-estado-reclamo");
				validaOk=false;
			}
			
			if (!WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction) && 
					reclamoPrestacional.getAlta_fecha() ==null  ){ // FECHA OSPIM 
				SessionErrors.add(renderRequest, "error-fechaingresoospim-reclamo");
				validaOk=false;
			}
			
			Integer estado = Integer.parseInt(ParamUtil.getString(renderRequest, "estado",reclamoPrestacional!=null?String.valueOf(reclamoPrestacional.getEstado()):"0"));		

			AfiPlan afiPlanActual = obtenerPlanActual(reclamoPrestacional
);

				Integer idPlanActual = null;
				String nombrePlanActual = "";

				if (afiPlanActual != null && afiPlanActual.getPlan() != null) {

				    idPlanActual = afiPlanActual.getPlan().getId();
				    nombrePlanActual = afiPlanActual.getPlan().getDescripcion();
				}

				boolean planBloqueado = esPlanBloqueadoParaReclamo(idPlanActual, reclamoPrestacional.getTipoPedido());

				if (planBloqueado) {

				    String mensajePlan = "Afiliado con plan \"" + nombrePlanActual + "\" no puede cargar un reclamo.";

				    SessionErrors.add(renderRequest,"errorPlanNoPermiteReclamo");

				    renderRequest.setAttribute("msgErrorPlanNoPermiteReclamo",mensajePlan);
				}
			
//			Validamos que el afiliado no tenga suspendida la cobertura médica
			List<AfiSuspencionCobertura> suspCoberMedica = null;
			if(reclamoPrestacional.getAfiliado()!=null && StringUtils.checkNotEmpty(reclamoPrestacional.getAfiliado().getCuil_titular())) {
				suspCoberMedica = PlanServiceUtil.getSuspencionesCobMedicaBeneficiario(reclamoPrestacional.getAfiliado().getCuil_titular(), reclamoPrestacional.getAfiliado().getInte());
				
				if(suspCoberMedica!=null && suspCoberMedica.size()>0) {
					AfiSuspencionCobertura ascm = suspCoberMedica.get(0);
					if(ascm.getVigenDesde().before(reclamoPrestacional.getOspim_fecha()) 
							&& (ascm.getVigenHasta() == null 
							|| ascm.getVigenHasta().after(reclamoPrestacional.getOspim_fecha()) ) ) {
						
						SessionErrors.add(renderRequest, "errorAfiliadoSinCobertMed");
						   renderRequest.setAttribute("msgErrorAfiSinCobMed","El Afiliado tiene suspendida la cobertura médica");						
						  
						   if (estado!=2 || reclamoPrestacional.getTipo_gestion_cierre_reclamo() != 5  ){//Si estado no es rechazado o anulado
								validaOk = false;
								if (Constants.VIEW.equals(cmd) || Constants.EDIT.equals(cmd)){
								   	viewAndEdit(mapping, renderRequest, renderResponse, session, reclamoPrestacional, cmdAction, cmdAction, idReclamoDeBuscador);
								}
							}else{
								validaOk = true;
							}
						   
					}
				}
				
			}
			
			List<PrestacionesReclamo> prestacionesAux= (List<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
			if (!cmd.equals(Constants.VIEW) && !cmd.equals(Constants.RESTORE) ){
			  reclamoPrestacional.setEstado(estado);
			}  
			
			String ErrorMsg = "";
            if ((Constants.SAVE.equals(cmd)
                    || Constants.UPDATE.equals(cmd))
                    && prestacionesAux != null
                    && estado != 4) {

                for (PrestacionesReclamo prestacion : prestacionesAux) {
                    if (PrestacionesReclamo.ESTADOS.BAJA.equals(
                            prestacion.getEstado()
                    )) {
                        continue;
                    }

                    ErrorMsg =
                            validarReclamoPrestacionesIncompletas(
                                    prestacion
                            );

                    if (!StringUtils.checkEmpty(ErrorMsg)) {
                        SessionErrors.add(
                                renderRequest,
                                "errorPrestacionComprobante"
                        );

                        renderRequest.setAttribute(
                                "msgErrorPrestacionComprobante",
                                ErrorMsg
                        );

                        validaOk = false;
                        break;
                    }
                }
            }
			
			
			List<PrestacionesReclamo> prestaciones= (List<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
			
			reclamoPrestacional.setPrestaciones(prestaciones);
			List<RevisionesReclamo> revisiones = (List<RevisionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );				
			reclamoPrestacional.setRevisiones(revisiones);
			List<ContactoCRM> contactos = (List<ContactoCRM>) session.getAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION);				
			reclamoPrestacional.setContactosCRM (contactos );
			
			session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
			session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional );	
			
		
			boolean prestaAutorizada = false;
			if ((cmd.equals(Constants.UPDATE ) || cmd.equals(Constants.SAVE )) && prestaciones!= null ){
				for( PrestacionesReclamo r : prestaciones) {// Si hay alguna prestacion autorizada
					if(!PrestacionesReclamo.ESTADOS.BAJA.equals(r.getEstado())   
							&& (r.getEstadoRechazoAprobado() == 1) ){  
						prestaAutorizada = true;
						break;
					}				
				}	
			}
		
			if((prestaAutorizada ==  true || reclamoPrestacional.getEstado() == 3  )
					&& "REINTEGRO".equalsIgnoreCase(reclamoPrestacional.getTipoPedido()) /* && !"0".equals(seccionalDefecto)*/ 
					&& reclamoPrestacional.getTipo_gestion_cierre_reclamo()!=5){//No valida datos de cuenta en ospim al cierre
				
				String errorCuentaMgs = CuentaDocumentoHelper.validaCuentaCambioEstado(reclamoPrestacional); 
				if (!StringUtils.checkEmpty(errorCuentaMgs)){
					SessionErrors.add(renderRequest, "errorCuentaReclamo");
					renderRequest.setAttribute("msgErrorCuentaReclamo",errorCuentaMgs);
					validaOk = false;						
				}
			}
			
			if((prestaAutorizada ==  true || reclamoPrestacional.getEstado() == 3  )
					 && (reclamoPrestacionalBase!=null &&  reclamoPrestacionalBase.getIdSeccional() == 0 )){
				
				String errorCuentaMgs = CuentaDocumentoHelper.validaImagenPrestacion(reclamoPrestacional); 
				if (!StringUtils.checkEmpty(errorCuentaMgs)){
					SessionErrors.add(renderRequest, "errorCuentaReclamo");
					renderRequest.setAttribute("msgErrorCuentaReclamo",errorCuentaMgs);
					validaOk = false;						
					//viewAndEdit(mapping, renderRequest, renderResponse, session, reclamoPrestacionalBase, cmdAction, cmdAction, idReclamoDeBuscador);				
				}
			}
			
			if (validaOk == false && (Constants.VIEW.equals(cmd) || Constants.EDIT.equals(cmd))){
				viewAndEdit(mapping, renderRequest, renderResponse, session, reclamoPrestacional, cmdAction, cmdAction, idReclamoDeBuscador);
			}
			
			/*
			 * Bloqueo definitivo antes de insertar o actualizar.
			 */
			if (
			    planBloqueado &&
			    (
			        Constants.SAVE.equals(cmd) ||
			        Constants.UPDATE.equals(cmd)
			    )
			) {
			    validaOk = false;
			}
			
			// final de validaciones minimas de ingreso		
			if(validaOk){
				
				if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction) ){ 
					reclamoPrestacional.setMarcaSeccional(cmdAction);
				}
				
								
				if(cmd.equals(Constants.SAVE)){
					
					if (contextoCompra == null) {
						asignarTercerizadoraAPrestaciones(
								renderRequest,
								prestaciones
						);
					}
					
					reclamoPrestacional.setPrestaciones(prestaciones);
					reclamoPrestacional.setRevisiones(revisiones);
					
					// edita los contactos seleccionados 
					asignarReferenciasAlosContactos(session , renderRequest );
					// reqasigna la lista con los contactos seleccionados 
					reclamoPrestacional.setContactosCRM(contactos);
					// id seccional
					reclamoPrestacional.setIdSeccional(Integer.parseInt(seccionalDefecto));
					
					if (contextoCompra == null) {
						idReclamo = ReclamosPrestacionesServiceUtil
								.insertar(reclamoPrestacional, user);
						reclamoPrestacional = ReclamosPrestacionesServiceUtil
								.getReclamoPrestacional(idReclamo);
					} else {
						boolean reservaCompraTomada = false;
						int idReclamoCreado = 0;
						String usuarioActual =
								user != null ? user.getScreenName() : "sistema";

						try {
							reclamoPrestacional.setRecuperable(
									contextoCompra.isRecupero()
											|| contextoCompra.isSurge()
							);
							reclamoPrestacional.setSuperintendencia(
									contextoCompra.isSurge()
							);

							RequerimientoCompra requerimientoCompra =
									validarContextoCompraParaGuardar(
											contextoCompra,
											reclamoPrestacional,
											user
									);
							asignarTercerizadoraAPrestaciones(
									requerimientoCompra,
									prestaciones
							);

							RequerimientoCompraReclamoPrestacionalServiceUtil
									.reservarCreacion(
											contextoCompra
													.getIdRequerimientoCompra(),
											contextoCompra.getNonce(),
											usuarioActual
									);

							reservaCompraTomada = true;

                            idReclamoCreado =
                                    RequerimientoCompraReclamoPrestacionalServiceUtil
                                            .crearYVincular(
                                                    contextoCompra
                                                            .getIdRequerimientoCompra(),
                                                    contextoCompra.getNonce(),
                                                    reclamoPrestacional,
                                                    user
                                            );

                            reclamoPrestacional =
                                    ReclamosPrestacionesServiceUtil
                                            .getReclamoPrestacional(
                                                    idReclamoCreado
                                            );

                            if (reclamoPrestacional == null) {
                                throw new Exception(
                                        "El Reclamo Prestacional fue creado, "
                                                + "pero no pudo recuperarse."
                                );
                            }

                            idReclamo = idReclamoCreado;

                            session.removeAttribute(
                                    WebKeysCompras
                                            .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
                            );
						} catch (Exception e) {
							if (reservaCompraTomada) {
								registrarFalloVinculacionCompra(
										contextoCompra,
										idReclamoCreado,
										e,
										usuarioActual
								);
							}

							_log.error(
									"No se pudo completar el alta del Reclamo "
											+ "Prestacional iniciado desde Compras.",
									e
							);
							SessionErrors.add(
									renderRequest,
									"error-reclamo-compras"
							);
							renderRequest.setAttribute(
									"msgErrorReclamoCompras",
									mensajeSeguroVinculacion(e, idReclamoCreado)
							);
							renderRequest.setAttribute(
									Constants.CMD,
									Constants.ADD
							);

							return mapping.findForward(getForward(
									renderRequest,
									"portlet.autorizaciones.reclamosprestacionales."
											+ "editar_reclamos_entry"
							));
						}
					}
					

					session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION );
					
					session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional );	
					session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getPrestaciones());
					session.setAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getRevisiones());
					
					session.setAttribute(
						    RECLAMO_PRESTACION_ESTADO_ORIGINAL,
						    reclamoPrestacional.getEstado()
						);
				}
				
				if(cmd.equals(Constants.EDIT ) || cmd.equals(Constants.VIEW )){
					// me llevo la logica de arriba a un metodo
				   	viewAndEdit(mapping, renderRequest, renderResponse, session, reclamoPrestacional, cmdAction, cmdAction, idReclamoDeBuscador);
				}
				
				if(cmd.equals(Constants.UPDATE )){				
					
					int id = reclamoPrestacional.getId_reclamo();
				    ReclamoPrestacional originalBD = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(id);
				    int estadoAnterior = (originalBD != null) ? originalBD.getEstado() : -99;
					
				    
				    //se agrega
				    Integer estadoOriginalPantalla =
				            (Integer) session.getAttribute(RECLAMO_PRESTACION_ESTADO_ORIGINAL);

				    if (originalBD != null && estadoOriginalPantalla != null) {

				        int estadoActualBD = originalBD.getEstado();

				        if (estadoActualBD != estadoOriginalPantalla.intValue()) {

				            SessionErrors.add(renderRequest, "error-reclamo-modificado");
				            renderRequest.setAttribute(
				                "msgErrorReclamoModificado",
				                "El reclamo fue modificado por otro usuario. Recargue la pantalla antes de guardar."
				            );

				            session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
				            session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
				            session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION);

				            session.setAttribute(
				                WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION,
				                originalBD
				            );
				            session.setAttribute(
				                WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION,
				                originalBD.getPrestaciones()
				            );
				            session.setAttribute(
				                WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION,
				                originalBD.getRevisiones()
				            );

				            renderRequest.setAttribute(Constants.CMD, Constants.EDIT);

				            return mapping.findForward(getForward(
				                renderRequest,
				                WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)
				                    ? "portlet.autorizaciones.reclamosprestacionales_seccional.editar_reclamos_entry"
				                    : "portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry"
				            ));
				        }
				    }

				    if (originalBD != null
				            && originalBD.getEstado() == 3
				            && reclamoPrestacional.getEstado() != 3) {

				        SessionErrors.add(renderRequest, "error-reclamo-ya-cerrado");
				        renderRequest.setAttribute(
				            "msgErrorReclamoYaCerrado",
				            "El reclamo ya fue cerrado por otro usuario. No se puede volver a guardar."
				        );

				        session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
				        session.setAttribute(
				            WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION,
				            originalBD
				        );

				        renderRequest.setAttribute(Constants.CMD, Constants.EDIT);

				        return mapping.findForward(getForward(
				            renderRequest,
				            WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)
				                ? "portlet.autorizaciones.reclamosprestacionales_seccional.editar_reclamos_entry"
				                : "portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry"
				        ));
				    }

				    
				    
					int aux =  reclamoPrestacional.getId_reclamo();					
					ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO resolucionAutorizado;
					resolucionAutorizado=reclamoPrestacional.getEstadoResolucionAutorizada();
					//reclamoPrestacional =getReclamoPrestacionalFromRequest(PortalUtil.getHttpServletRequest(renderRequest), reclamoPrestacional , null,null );					 
     				reclamoPrestacional = (ReclamoPrestacional) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);	

					reclamoPrestacional.setEstadoResolucionAutorizada(resolucionAutorizado);
					reclamoPrestacional.setId(aux ); 
					
					asignarTercerizadoraAPrestaciones(renderRequest, prestaciones);//se agrega
					
					reclamoPrestacional.setPrestaciones(prestaciones);
					reclamoPrestacional.setRevisiones(revisiones);			
					// edita los contactos seleccionados en UI 
					asignarReferenciasAlosContactos(session , renderRequest );
					// reasigna la lista con los contactos seleccionados 
					reclamoPrestacional.setContactosCRM(contactos);
					
					ReclamoPrestacional reclamoPrestacionalAux =  null;
					reclamoPrestacionalAux =  ReclamosPrestacionesServiceUtil.getReclamoPrestacional(reclamoPrestacional.getId_reclamo() );					
					
					//Es una pre carga
					if(reclamoPrestacionalAux.getEstado()== 0 && reclamoPrestacional.getEstado() !=0 ){
						ReclamoPrestacionalEmailSeccional.getInstance().enviarEmailSeccional(reclamoPrestacionalAux);
					}
					reclamoPrestacional.setDebitoPrestadora(false);
					//Ponemos la marca debito prestadora cuando tiene un cargo a la prestadora 
					for (int i = 0; i < reclamoPrestacional.getPrestaciones().size(); i++) {	
						PrestacionesReclamo presReclamo  = (PrestacionesReclamo) reclamoPrestacional.getPrestaciones().get(i);
						if (presReclamo.getEstado() == null || !presReclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA)){
							if (presReclamo.getCargo_ps() > 0 || presReclamo.getCargo_imesa() > 0){
								reclamoPrestacional.setDebitoPrestadora(true);
							}
						}
					}
					
					if (!WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction) && 
							seccionalDefecto != null && "0".equals(seccionalDefecto) &&
							"REINTEGRO".equalsIgnoreCase(reclamoPrestacional.getTipoPedido()) &&
							reclamoPrestacional.getEstado() == 0){
						reclamoPrestacional.setEstado(1);
						
					}				
					
					int estadoNuevo = reclamoPrestacional.getEstado();
				    
				    try {
				        if (estadoNuevo != estadoAnterior) {
				        	ReclamosPrestacionesServiceUtil.update(reclamoPrestacional, user);
							reclamoPrestacional = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(reclamoPrestacional.getId_reclamo() );						
							
				            Integer idExterno = reclamoPrestacional.getIdReintegroApp();
				            if (idExterno != null && idExterno > 0) {
				                String token = ClienteAppMobile.obtenerToken();
				                if (token != null) {
				                    String codigoExterno = null;
				                    if (estadoNuevo == 1) {// PENDIENTE
				                        codigoExterno = "PE";
				                    } else if (estadoNuevo == 3) { // CERRADO
				                        final int TG_RECHAZADO = 5; 

				                        Integer tg = reclamoPrestacional.getTipo_gestion_cierre_reclamo();
				                        boolean cerradoRechazado = (tg != null && tg == TG_RECHAZADO);

				                        if (!cerradoRechazado) {
				                            String desc = reclamoPrestacional.getEstadoReclamoPrestacion(); // "RECHAZADO"/"AUTORIZADO"
				                            if (desc != null && "RECHAZADO".equalsIgnoreCase(desc.trim())) {
				                                cerradoRechazado = true;
				                            }
				                        }
				                        codigoExterno = cerradoRechazado ? "RE" : "CE";
				                    }

				                    
				                    if (codigoExterno != null) {
				                        ClienteAppMobile.actualizarEstadoReintegro(idExterno, codigoExterno, token);
				                        _log.info("Actualizado reintegroApp=" + idExterno + " a estado externo: " + codigoExterno);
				                    } else {
				                        _log.warn("No se actualiza estado");
				                    }
				                } else {
				                    _log.warn("Token nulo");
				                }
				            } else {
				                _log.debug("Reclamo sin idReintegroApp");
				            }
				        } else {
				            _log.debug("Estado sin cambios (anterior=" + estadoAnterior + ", nuevo=" + estadoNuevo + "). ");
				            ReclamosPrestacionesServiceUtil.update(reclamoPrestacional, user);
				        }
				    } catch (Exception e) {
				        _log.error("Error en estado externo del reintegro: ", e);
				    }
				    
					session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );					
					session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional );	
					session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getPrestaciones());
					session.setAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getRevisiones());
					
					//se agrega
					session.setAttribute(
						    RECLAMO_PRESTACION_ESTADO_ORIGINAL,
						    reclamoPrestacional.getEstado()
						);
					
					if(reclamoPrestacional.getEstado() == 3 && !"REINTEGRO".equalsIgnoreCase(reclamoPrestacional.getTipoPedido()) ){
						this.avisoPrestadorInexistente(reclamoPrestacional);
					}					
				}	
				if (SessionErrors.isEmpty(renderRequest)  && (cmd.equals(Constants.UPDATE)  || cmd.equals(Constants.SAVE)
						|| cmd.equals(WebKeysAutorizaciones.CUENTA) ) )	{
					String successMessage = ParamUtil.getString(renderRequest, "successMessage");
					SessionMessages.add(renderRequest, "request_processed", successMessage);																
				}
				renderRequest.setAttribute(
						Constants.CMD,
						Constants.ADD.equals(cmd) ? Constants.ADD : Constants.EDIT
				);
				if (cmd.equals(Constants.VIEW) ){
					   renderRequest.setAttribute(Constants.CMD,Constants.VIEW);					                                    
				}
					    //renderRequest.setAttribute("tabs1", "reclamos-prestacionales");					    
            } else {
                boolean reclamoPersistido =
                        reclamoPrestacional != null
                                && reclamoPrestacional.getId_reclamo() > 0;

                renderRequest.setAttribute(
                        Constants.CMD,
                        reclamoPersistido
                                ? Constants.EDIT
                                : Constants.ADD
                );

                if (reclamoPersistido) {
                    reclamoPrestacional.setMarcaReabrirReclamo(
                            true
                    );
                }
            }
				
		}else{ // es Nuevo			
			  
				session.removeAttribute(RECLAMO_PRESTACION_ESTADO_ORIGINAL);
			
				session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION  );				
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION );				
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION, ReclamosPrestacionesServiceUtil.getPrestacionesAsociadas(casoAsociado) );				
				renderRequest.setAttribute(Constants.CMD, Constants.ADD);								  
				renderRequest.setAttribute("caso_vinculado", casoAsociado);
		
		}
		
		
		 if((Constants.MOVE.equals(cmd))){
				String moverATab = ParamUtil.getString(renderRequest, "moverATab");
				renderRequest.setAttribute("reclamo_id", String.valueOf(idReclamoDeBuscador));
				
				tabSel = moverATab;
				renderRequest.setAttribute("tab", tabSel);

         }
		
		 if(("email".equals(cmd))){
			String moverATab = ParamUtil.getString(renderRequest, "tab_seleccionada");

			ReclamoPrestacional reclamoPrestacionalAux =  null;
			reclamoPrestacionalAux =  ReclamosPrestacionesServiceUtil.getReclamoPrestacional(reclamoPrestacional.getId_reclamo() );
			
			boolean error = validarReclamoSeccional(reclamoPrestacionalAux,  renderRequest);;
			if (error){
				_log.debug("Error datos incompletos");
				
			}else{			 
				renderRequest.setAttribute("reclamo_id", String.valueOf(idReclamoDeBuscador));
					
				try {
					if (reclamoPrestacionalAux.getEstado() == 6) {
				        String obsCambio = "";
				        ReclamosPrestacionesServiceUtil.cambiarEstado(
				            reclamoPrestacionalAux.getId_reclamo(),
				            0,
				            obsCambio,
				            user.getScreenName()
				        );				        
				        reclamoPrestacionalAux = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(
				            reclamoPrestacionalAux.getId_reclamo()
				        );
				    }
				} catch (Exception e) {
				    _log.warn("No se pudo cambiar estado de APP a PRECARGA antes del envío de mail", e);
				}
				
				ReclamosPrestacionesServiceUtil.grabarFechaEnvioSeccional(reclamoPrestacionalAux);
				ReclamoPrestacionalEmailSeccional.getInstance().enviarEmailOspim(reclamoPrestacional);	
				
				session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
				reclamoPrestacionalAux =  ReclamosPrestacionesServiceUtil.getReclamoPrestacional(reclamoPrestacional.getId_reclamo() );	
				session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacionalAux );	
			}
			tabSel = moverATab;
			renderRequest.setAttribute("tab", tabSel);
		 }

		
		if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)){
			return mapping.findForward(getForward(renderRequest,
					"portlet.autorizaciones.reclamosprestacionales_seccional.editar_reclamos_entry"));
		}else{			
			return mapping.findForward(getForward(renderRequest,
					"portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry"));
		}
	}	

	private void limpiarSesionHandoffCompra(HttpSession session) {
		if (session == null) {
			return;
		}

		session.removeAttribute(
				WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
		);
		session.removeAttribute(
				WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION
		);
		session.removeAttribute(
				WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
		);
		session.removeAttribute(
				WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION
		);
		session.removeAttribute(
				WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION
		);
		session.removeAttribute(
				WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION
		);
		session.removeAttribute(
				WebKeysAutorizaciones.RECLAMO_NUEVO_ESTADO_OBS
		);
	}

	private ReclamoPrestacionalCompraContexto resolverContextoCompra(
			HttpSession session,
			PortletRequest request,
			User user) throws Exception {

		String nonceRequest = ParamUtil.getString(
				request,
				WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
				""
		);
		Object contextoObj = session.getAttribute(
				WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
		);

		/*
		 * Sin nonce y sin contexto se conserva el flujo ordinario de Reclamos.
		 * Si hay un handoff de Compras, la ausencia del nonce debe fallar
		 * cerrada: nunca puede degradarse a un alta generica.
		 */
		if (StringUtils.checkEmpty(nonceRequest)) {
			if (contextoObj != null) {
				throw new Exception(
						"El contexto de Compras requiere un nonce valido. "
								+ "Vuelva al requerimiento e inicie nuevamente "
								+ "el Reclamo Prestacional."
				);
			}
			return null;
		}

		if (!(contextoObj instanceof ReclamoPrestacionalCompraContexto)) {
			throw new Exception(
					"El contexto de Compras expiro o ya no esta disponible."
			);
		}

		ReclamoPrestacionalCompraContexto contexto =
				(ReclamoPrestacionalCompraContexto) contextoObj;
		String usuario = user != null ? user.getScreenName() : "";

		if (!contexto.coincideNonce(nonceRequest)
				|| !contexto.perteneceAUsuario(usuario)
				|| !contexto.estaVigente(System.currentTimeMillis())) {

			throw new Exception(
					"El contexto de Compras no es valido o vencio. "
							+ "Vuelva al requerimiento e inicie nuevamente "
							+ "el Reclamo Prestacional."
			);
		}

		return contexto;
	}

	private RequerimientoCompra validarContextoCompraParaGuardar(
			ReclamoPrestacionalCompraContexto contexto,
			ReclamoPrestacional reclamoPrestacional,
			User user) throws Exception {

		if (contexto == null || reclamoPrestacional == null) {
			throw new Exception(
					"No se pudo validar el origen Compras "
							+ "del Reclamo Prestacional."
			);
		}

		if (user == null) {
			throw new Exception(
					"No se pudo determinar el usuario actual."
			);
		}

		boolean permisoCompras = PermissionUtil.userContainsRole(
				user,
				WebKeysCompras.ROL_ABM_COMPRAS
		) || PermissionUtil.userContainsRole(
				user,
				WebKeysCompras.ROL_COTIZAR_COMPRAS
		);
		boolean permisoReclamo = PermissionUtil.userContainsRole(
				user,
				WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST
		);

		if (!permisoCompras || !permisoReclamo) {
			throw new Exception(
					"No posee permisos para crear el Reclamo "
							+ "Prestacional desde Compras."
			);
		}

		RequerimientoCompra requerimiento =
				BusquedaRequerimientoCompraServiceUtil
						.getRequerimientoCompra(
								contexto.getIdRequerimientoCompra()
						);

		if (requerimiento == null
				|| requerimiento.getBajaFecha() != null) {

			throw new Exception(
					"El requerimiento de compra ya no esta activo."
			);
		}

		if (!WebKeysCompras.esCotizado(requerimiento.getEstado())) {
			throw new Exception(
					"El requerimiento de compra ya no esta COTIZADO."
			);
		}

		String cuilRequerimiento = normalizarCuil(
				requerimiento.getAfiliadoCuilTitular()
		);
		String cuilReclamo = normalizarCuil(
				reclamoPrestacional.getCuit_titular()
		);
		int integranteRequerimiento = requerimiento.getAfiliadoInt() != null
				? requerimiento.getAfiliadoInt().intValue()
				: -1;

		if (WebKeysCompras.isEmpty(cuilRequerimiento)
				|| !cuilRequerimiento.equals(cuilReclamo)
				|| integranteRequerimiento != reclamoPrestacional.getInte()) {

			throw new Exception(
					"El afiliado del Reclamo Prestacional no coincide "
							+ "con el requerimiento de compra."
			);
		}

		return requerimiento;
	}

	private void registrarFalloVinculacionCompra(
			ReclamoPrestacionalCompraContexto contexto,
			int idReclamoCreado,
			Exception error,
			String usuario) {

		try {
			if (idReclamoCreado > 0) {
				RequerimientoCompraReclamoPrestacionalServiceUtil
						.marcarErrorPosteriorAlInsert(
								contexto.getIdRequerimientoCompra(),
								contexto.getNonce(),
								idReclamoCreado,
								error != null ? error.getMessage() : null,
								usuario
						);
			} else {
				RequerimientoCompraReclamoPrestacionalServiceUtil
						.liberarReserva(
								contexto.getIdRequerimientoCompra(),
								contexto.getNonce(),
								usuario
						);
			}
		} catch (Exception compensacionError) {
			_log.error(
					"No se pudo compensar la reserva del Reclamo "
							+ "Prestacional iniciado desde Compras. "
							+ "idRequerimiento="
							+ contexto.getIdRequerimientoCompra()
							+ ", idReclamo="
							+ idReclamoCreado,
					compensacionError
			);
		}
	}

	private String mensajeSeguroVinculacion(
			Exception error,
			int idReclamoCreado) {

		if (idReclamoCreado > 0) {
			return "El Reclamo Prestacional "
					+ idReclamoCreado
					+ " fue creado, pero no pudo vincularse "
					+ "completamente con Compras. "
					+ "No intente crearlo nuevamente; "
					+ "requiere reconciliacion.";
		}

		if (error != null
				&& !WebKeysCompras.isEmpty(error.getMessage())) {
			return error.getMessage();
		}

		return "No se pudo crear el Reclamo Prestacional desde Compras.";
	}

	private String normalizarCuil(String value) {
		if (value == null) {
			return "";
		}

		return value.replaceAll("[^0-9]", "");
	}

	//se agrega
	private void asignarTercerizadoraAPrestaciones(
	        PortletRequest request,
	        List<PrestacionesReclamo> prestaciones) {

	    String idTercerizadora = ParamUtil.getString(request, "id_tercerizadora", "");

	    if (StringUtils.checkEmpty(idTercerizadora)
	            || "null".equalsIgnoreCase(idTercerizadora)
	            || "undefined".equalsIgnoreCase(idTercerizadora)) {
	        idTercerizadora = null;
	    }

	    if (prestaciones != null) {
	        for (PrestacionesReclamo p : prestaciones) {
	            if (p.getEstado() == null || !PrestacionesReclamo.ESTADOS.BAJA.equals(p.getEstado())) {
	                p.setIdTercerizadora(idTercerizadora);
		    }
		}
	    }
	}

	private void asignarTercerizadoraAPrestaciones(
			RequerimientoCompra requerimiento,
			List<PrestacionesReclamo> prestaciones) {

		String idTercerizadora = null;
		if (requerimiento != null
				&& requerimiento.getCargoTercerizadora() != null
				&& requerimiento.getCargoTercerizadora().intValue() > 0) {

			idTercerizadora = requerimiento.getIdTercerizadora();
		}

		if (prestaciones != null) {
			for (PrestacionesReclamo prestacion : prestaciones) {
				if (prestacion.getEstado() == null
						|| !PrestacionesReclamo.ESTADOS.BAJA.equals(
						prestacion.getEstado()
				)) {

					prestacion.setIdTercerizadora(idTercerizadora);
				}
			}
		}
	}
	
    private	void asignarReferenciasAlosContactos (HttpSession session, RenderRequest  renderRequest)
    {
    	
    	ArrayList<ContactoCRM> contactos  = (ArrayList<ContactoCRM>) session.getAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION);
    	ArrayList<ContactoCRM> contactoseditados  = new ArrayList<ContactoCRM>();
    	
    	String paramChecked = "";
		if (contactos!=null && contactos.size()>0 )
		{
			for (Iterator<ContactoCRM> iterator = contactos.iterator(); iterator.hasNext();) 
			{
				
				ContactoCRM itemcontacto = iterator.next();		
				paramChecked = "contactorec" + itemcontacto.getIdContacto() ;			
				String valorCheckBox = ParamUtil.getString(renderRequest, paramChecked,"0");
//				_log.debug("item: " + paramChecked + " " + valorCheckBox);
				  
				if(paramChecked != null && valorCheckBox !=null &&  Integer.parseInt(valorCheckBox) >0){					
					itemcontacto.setIdCrmReclamoPrestacional(itemcontacto.getIdContacto());
//					_log.debug("item: " + paramChecked + " Checked  " + valorCheckBox);
					}else{
						itemcontacto.setIdCrmReclamoPrestacional(0);
//						_log.debug("item: " + paramChecked + " Unchecked  " + valorCheckBox);
					}
						
				contactoseditados.add(itemcontacto);
			}
		// se graba la lista editada en la sesion		
		session.setAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION , contactoseditados);
		}
    }
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{

		//carga de listas en sesion que utiliza el jsp
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		
		if(session.getAttribute(WebKeysGlobal.DOCUMENTOS_CIE)== null ){
			List<CieDiez> cieDiez=TraeListasServiceUtil.getListadoCieDiez();
			session.setAttribute(WebKeysGlobal.DOCUMENTOS_CIE,cieDiez);
		}
		
		

		
	}
	
	
	private ActionForward viewAndEdit(ActionMapping mapping,  RenderRequest renderRequest, RenderResponse renderResponse, HttpSession session ,ReclamoPrestacional reclamoPrestacional, String cmdAction , String cmd,int idReclamoDeBuscador ) throws Exception  {
		if (idReclamoDeBuscador==0){						
			renderRequest.setAttribute(Constants.CMD,cmd ); // por la edicion
			if (cmd.equals(Constants.VIEW )) {
				renderRequest.setAttribute("ModoConsulta","si" ); // por la edicion
			}else{
				renderRequest.setAttribute("ModoConsulta","no" ); // por la edicion	
			}
			
			if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)){							
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.reclamosprestacionales_seccional.editar_reclamos_entry"));
			}else{
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry"));							
			}
		}
		
		reclamoPrestacional = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamoDeBuscador);	
		
		session.setAttribute(
			    RECLAMO_PRESTACION_ESTADO_ORIGINAL,
			    reclamoPrestacional.getEstado()
			);
		
		if (reclamoPrestacional.getEstado()==3) {
			ReclamosPrestacionesServiceUtil.setDatosOpReclamoPrestacional(reclamoPrestacional);						
		}
		session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION  );
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION );
		
		session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional );	
		session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getPrestaciones());
		session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION, reclamoPrestacional.getPrestacionesAsociadas() );				  
		session.setAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION , reclamoPrestacional.getRevisiones());
		session.setAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION , reclamoPrestacional.getContactosCRM() );
		
		renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
		
		if (cmd.equals(Constants.VIEW ) ){
			renderResponse.addProperty(Constants.CMD, Constants.VIEW);
		}
		return null;
	}
	
	private void avisoPrestadorInexistente(ReclamoPrestacional reclamoPrestacional){
			
		 List<PrestacionesReclamo> prestaciones = reclamoPrestacional.getPrestaciones();
		 for (PrestacionesReclamo prestacionesReclamo : prestaciones) {
			 if (prestacionesReclamo.getEstado() == null || !prestacionesReclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA)){
				 try {
					List<Prestador> lp = PrestadorServiceUtil.getPrestadores(0, prestacionesReclamo.getComprobanteCUIT() ,null, false);
					 if(lp==null || lp.size()==0){
				        //Prestador Inexistente
						 ReclamoCierrePrestadorInexistenteEmail.getInstance().enviarEmailCierrePrestadorInexistente(prestacionesReclamo,  reclamoPrestacional.getNroReclamo().toString());
				      }
				 } catch (Exception e) {
					 _log.debug("avisoPrestadorInexistente  " + e.getStackTrace());
				}
			 }
		 }
	}

        private String validarReclamoPrestacionesIncompletas(
                PrestacionesReclamo prestacion) {

            if (prestacion == null) {
                return "La prestacion informada es inexistente.";
            }

            if (prestacion.getId_prestacion() <= 0
                    && prestacion.getId_medicamento() <= 0) {

                String codigo =
                        StringUtils.checkEmpty(
                                prestacion.getCodigoPrestacion()
                        )
                                ? "sin codigo"
                                : prestacion.getCodigoPrestacion();

                return "La prestacion "
                        + codigo
                        + " debe editarse y asociarse a una prestacion "
                        + "o medicamento valido antes de grabar.";
            }

            if (prestacion.getId_prestacion() > 0
                    && prestacion.getId_medicamento() > 0) {

                return "La prestacion tiene simultaneamente una prestacion "
                        + "y un medicamento asociados.";
            }

            BigDecimal comprobanteCUIT =
                    BigDecimal.ZERO;

            BigDecimal comprobanteSucursal =
                    BigDecimal.ZERO;

            BigDecimal comprobanteNumero =
                    BigDecimal.ZERO;

            try {
                if (!StringUtils.checkEmpty(
                        prestacion.getComprobanteCUIT()
                )) {
                    comprobanteCUIT =
                            new BigDecimal(
                                    prestacion.getComprobanteCUIT()
                            );
                }

                if (!StringUtils.checkEmpty(
                        prestacion.getComprobanteSucursal()
                )) {
                    comprobanteSucursal =
                            new BigDecimal(
                                    prestacion.getComprobanteSucursal()
                            );
                }

                if (!StringUtils.checkEmpty(
                        prestacion.getComprobanteNro()
                )) {
                    comprobanteNumero =
                            new BigDecimal(
                                    prestacion.getComprobanteNro()
                            );
                }

            } catch (NumberFormatException e) {
                return "Los datos numericos del comprobante "
                        + "poseen un formato invalido.";
            }

            if (prestacion.getComprobanteFecha() == null) {
                return "Debe ingresar la fecha del Comprobante";
            }

            if (StringUtils.checkEmpty(
                    prestacion.getFrecuencia()
            )) {
                return "Debe seleccionar la frecuencia "
                        + "correspondiente del Comprobante";
            }

            if (StringUtils.checkEmpty(
                    prestacion.getComprobanteTipo()
            )) {
                return "Debe seleccionar el tipo "
                        + "correspondiente del Comprobante";
            }

            boolean comprobanteFlexible =
                    "OTR".equalsIgnoreCase(
                            prestacion.getComprobanteTipo()
                    )
                            || "AUT".equalsIgnoreCase(
                            prestacion.getComprobanteTipo()
                    );

            if (!comprobanteFlexible
                    && StringUtils.checkEmpty(
                    prestacion.getComprobanteLetra()
            )) {

                return "Debe seleccionar la letra del Comprobante";
            }

            if (!comprobanteFlexible
                    && StringUtils.checkEmpty(
                    prestacion.getComprobanteCUITSucursal()
            )) {

                return "Debe ingresar el CUIT sucursal "
                        + "del Comprobante";
            }

            if (!comprobanteFlexible
                    && comprobanteCUIT.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                return "Debe ingresar el CUIT del Comprobante";
            }

            if (prestacion.getComprobanteImporte() == null
                    || prestacion.getComprobanteImporte() <= 0) {

                return "Debe ingresar el importe de la Factura "
                        + "del Comprobante";
            }

            if (!comprobanteFlexible
                    && comprobanteSucursal.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                return "Debe ingresar la Sucursal del Comprobante";
            }

            if (!comprobanteFlexible
                    && comprobanteNumero.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                return "Debe ingresar el Nro del Comprobante";
            }

            return "";
        }
	
	
	private boolean  validarReclamoSeccional(ReclamoPrestacional reclamo, RenderRequest renderRequest){
		boolean error = false;
			
		
		if (reclamo.getFechaMailSeccional() != null){
			SessionErrors.add(renderRequest, "error-enviar-mail");
			renderRequest.setAttribute("msg-error-enviar-mail","El reclamos ya fue enviado");
			return true;
		}
		
		//NUEVO: validar que tenga al menos una prestación
	    int activas = 0;
	    if (reclamo.getPrestaciones() != null) {
	        for (PrestacionesReclamo p : reclamo.getPrestaciones()) {
	            if (!PrestacionesReclamo.ESTADOS.BAJA.equals(p.getEstado())) {
	                activas++;
	                break;
	            }
	        }
	    }

	    if (activas == 0) {
	        SessionErrors.add(renderRequest, "error-enviar-mail_4");
	        renderRequest.setAttribute("msg-error-enviar-mail_4","Debe cargar al menos una prestación");
	        error = true;
	    }
	    
		if (reclamo.getCantidadImagenes(reclamo.getId_reclamo()) == 0){
			SessionErrors.add(renderRequest, "error-enviar-mail");
			renderRequest.setAttribute("msg-error-enviar-mail","Debe cargar al menos una imagen (Archivos)");
			error  = true;
		}
		if (reclamo.existeCuentaAfi() == false){
			SessionErrors.add(renderRequest, "error-enviar-mail_1");
			renderRequest.setAttribute("msg-error-enviar-mail_1","Debe ingresar los datos de la cuenta (CTA Bancaria)");
			error  = true;
		}
		
		ReclamoPrestacionalCuenta cuenta = reclamo.getCuenta();
		
		if (cuenta != null){
		String titular = cuenta.getCmbTitular();
		
			if("0".equals(titular)){
				if (cuenta.getImagenCBU() == null || StringUtils.checkEmpty(cuenta.getImagenCBU()) ){
					SessionErrors.add(renderRequest, "error-enviar-mail_2");
					renderRequest.setAttribute("msg-error-enviar-mail_2","Debe agregar un comprobante del CBU  (CTA Bancaria)");
					error  = true;
				}
			}else if ("1".equals(titular)){
				if (cuenta.getImagenCBU() == null || StringUtils.checkEmpty(cuenta.getImagenCBU()) ){
					SessionErrors.add(renderRequest, "error-enviar-mail_2");
					renderRequest.setAttribute("msg-error-enviar-mail_2","Debe agregar un comprobante del CBU (CTA Bancaria)");
					error  = true;
				}
	
				if (cuenta.getImagenNotaAutorizada() == null || StringUtils.checkEmpty(cuenta.getImagenNotaAutorizada()) ){
					SessionErrors.add(renderRequest, "error-enviar-mail_3");
					renderRequest.setAttribute("msg-error-enviar-mail_3","Debe agregar la nota autorizante (CTA Bancaria)");
					error  = true;
				}
	
			}

		}
		
		return error;
		
	}
	
	//copia los datos de la cuenta del afiliado (afi_cuentas_bancarias)
	//y crea una nueva cuenta con la estructura que usa el reclamo (reclamo_prestacional_cuenta_bancaria).
	private ReclamoPrestacionalCuenta convertirAReclamoCuenta(AfiCuentasBancarias afiCuenta, int idReclamo) {
	    ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
	    cuenta.setIdReclamoPrestacional(idReclamo);

	    // siempre es el titular (grupo familiar)
	    cuenta.setCuilGrupoFamiliar(afiCuenta.getCuilTitular());

	    // si es apoderado, acá va el cuil del apoderado
	    cuenta.setCuil(afiCuenta.isTitular()
	    	    ? afiCuenta.getCuilTitular()
	    	    : (afiCuenta.getCuilCbu() != null ? afiCuenta.getCuilCbu() : afiCuenta.getCuilTitular()));


	    cuenta.setCbu(afiCuenta.getCbu());
	    cuenta.setEmail(afiCuenta.getEmail());
	    cuenta.setApellido(afiCuenta.getApellido());
	    cuenta.setNombre(afiCuenta.getNombre());
	    cuenta.setImagenCBU(afiCuenta.getFileCbu());

	    if (afiCuenta.isTitular()) {
	        cuenta.setImagenNotaAutorizada(null);
	        cuenta.setCmbTitular("0");
	    } else {
	        cuenta.setImagenNotaAutorizada(afiCuenta.getFileNotaAutorizada());
	        cuenta.setCmbTitular("1");
	    }

	    return cuenta;
	}

	private boolean esPlanBloqueadoParaReclamo(
	        Integer idPlan,
	        String tipoPedido) {

	    if (!"REINTEGRO".equalsIgnoreCase(tipoPedido)) {
	        return false;
	    }

	    if (idPlan == null) {
	        return false;
	    }

	    return idPlan.intValue() ==
	            PLAN_COBERTURA
	        || idPlan.intValue() ==
	            PLAN_COBERTURA_TOTAL_O
	        || idPlan.intValue() ==
	            PLAN_COBERTURA_TOTAL_M;
	}
	
	private AfiPlan obtenerPlanActual(ReclamoPrestacional reclamo) {

	    if (reclamo == null) {
	        return null;
	    }

	    String cuilTitular = null;

	    if (reclamo.getAfiliado() != null) {

	        cuilTitular = reclamo.getAfiliado().getCuil_titular();

	    } else {
	        cuilTitular = reclamo.getCuit_titular();
	    }

	    if (StringUtils.checkEmpty(cuilTitular)) {
	        return null;
	    }

	    try {

	        AfiPlan afiPlan = PlanServiceUtil.getInstance().buscarUltimoPlanAportes(cuilTitular);

	        if (afiPlan != null && afiPlan.getPlan() != null) {
	            return afiPlan;
	        }

	    } catch (Exception e) {
	        _log.error("Error consultando el plan del afiliado " + cuilTitular, e);
	    }

	    /*Respaldo por si el plan ya viene cargado dentro del objeto del reclamo*/
	    if (reclamo.getAfiliado() != null &&
	        reclamo.getAfiliado().getAfiPlan() != null &&
	        reclamo.getAfiliado().getAfiPlan().getPlan() != null) {
	    	
	        return reclamo.getAfiliado().getAfiPlan();
	    }

	    return null;
	}
	
}
