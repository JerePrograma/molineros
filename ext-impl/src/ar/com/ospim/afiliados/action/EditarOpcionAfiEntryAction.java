/**
 */

package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.FormOpcionSSSDuplicadoException;
import ar.com.ospim.afiliados.FormOpcionSSSFechaIgualPressException;
import ar.com.ospim.afiliados.FormOpcionSSSInvalidoException;
import ar.com.ospim.afiliados.FormOpcionSSSNoEnviadoException;
import ar.com.ospim.afiliados.FormOpcionSSSValidacionFechasException;
import ar.com.ospim.afiliados.NoExisteFechaConfiguradaPressSuper;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.exceptions.CuilInvalidoException;
import ar.com.ospim.afiliados.services.AfiOpcionSSUtil;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.FechaOpcionSSSUtil;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.util.CuilUtils;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

/**
 * <a href="EditarAfiliadoEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author SVA
 * 
 */
public class EditarOpcionAfiEntryAction extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());
	
	private AfiOpcionSSUtil util = new AfiOpcionSSUtil();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		// session y traerlos de sesion
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(actionRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(actionRequest);
//		String cmd = (String) session.getAttribute(Constants.CMD);
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
//		String opciones = ParamUtil.getString(actionRequest, "opciones");
		
		DetalleOpcionesSS detOpSS = (DetalleOpcionesSS) session.getAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION);
		session.removeAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION);
		int id_opcion=0;
		
//		if (null != opciones && opciones.equals("true")) {
//			session.setAttribute("opciones", opciones);
//		}
		
		if (cmd != null && cmd != "") {
			
			detOpSS = armarOpcionFormEntry(actionRequest);

			String msgError = AfiOpcionSSUtil.validarOpcionSSS(detOpSS.getCuil(), detOpSS.getNroFormulario(), detOpSS.getRegimen(), 
					detOpSS.getFechaCerti(), detOpSS.getId().intValue());
			
			if(StringUtils.checkNotEmpty(msgError)){
				SessionErrors.add(actionRequest, "error-carga-opcion-sss");
				actionRequest.setAttribute("msgOpcionSSSfail", msgError);
				SessionErrors.add(actionRequest, FormOpcionSSSInvalidoException.class.getName());
			}
			boolean validaCUITEmpleador = CuilUtils.validarNum(detOpSS.getCuit());
			
			if(!validaCUITEmpleador && detOpSS.getRegimen().equalsIgnoreCase("RG")){
				msgError = "El CUIT del Empleador es inválido"; 
				actionRequest.setAttribute("msgOpcionSSSfail", msgError);
//				SessionErrors.add(actionRequest, "El CUIT del Empleador es inválido");
				SessionErrors.add(actionRequest, FormOpcionSSSInvalidoException.class.getName());
			}
			
			if(!validaCUITEmpleador && detOpSS.getRegimen().equalsIgnoreCase("RG")){
				msgError = "El CUIT del Empleador es inválido"; 
				actionRequest.setAttribute("msgOpcionSSSfail", msgError);
//				SessionErrors.add(actionRequest, "El CUIT del Empleador es inválido");
				SessionErrors.add(actionRequest, FormOpcionSSSInvalidoException.class.getName());
			}
			
			boolean cuilValido=CuilUtils.validarNum(detOpSS.getCuil());	
			if(!cuilValido){
				msgError = "El CUIL del Afiliado es inválido"; 
				actionRequest.setAttribute("msgOpcionSSSfail", msgError);
				SessionErrors.add(actionRequest, CuilInvalidoException.class.getName());
			}
//			TODO hacer la validacion solo si esta vigente y tien plan ospim
//			else {
//				int existe = 0;
//				existe=EditarAfiliadoServiceUtil.existeAfiliadoTitular(detOpSS.getCuil());
//				if(existe == 1) {
//					msgError = "El CUIL del Afiliado es un Cuil titular en padrón Ospim"; 
//					actionRequest.setAttribute("msgOpcionSSSfail", msgError);
//					SessionErrors.add(actionRequest, CuilInvalidoException.class.getName());
//				}else {
//					existe=EditarAfiliadoServiceUtil.existeAfiliado(detOpSS.getCuil());
//					if(existe == 1) {
//						msgError = "El CUIL del Afiliado es un Cuil integrante en padrón Ospim"; 
//						actionRequest.setAttribute("msgOpcionSSSfail", msgError);
//						SessionErrors.add(actionRequest, CuilInvalidoException.class.getName());
//					}
//					
//				}
//				
//				
//			}
			
			List<Feriado> feriados = TraeListasServiceUtil.getFeriados(actionRequest);
			
			try{
				Calendar hoy = DateUtils.getCalendarGMTMenos3();
				hoy.set(Calendar.HOUR_OF_DAY, 0);
				hoy.set(Calendar.MINUTE, 0);
				hoy.set(Calendar.SECOND, 0);
				hoy.set(Calendar.MILLISECOND, 0);
				
				if(DateUtils.isMismoDia(detOpSS.getFechaElecc(), hoy.getTime())){
					
					SessionErrors.add(actionRequest, "La fecha de elección no puede ser igual al día de hoy");
					throw new FormOpcionSSSValidacionFechasException();
				}
				if(DateUtils.isMismoDia(detOpSS.getFechaCerti() , hoy.getTime())){
					
					SessionErrors.add(actionRequest, "La fecha de certificación no puede ser igual al día de hoy");
					throw new FormOpcionSSSValidacionFechasException();
				}
			
				if(detOpSS.getFechaElecc().after(hoy.getTime())){
					SessionErrors.add(actionRequest, "La fecha de elección no puede ser posterior al día de hoy");
					throw new FormOpcionSSSValidacionFechasException();
				}
				
				if(detOpSS.getFechaCerti().after(hoy.getTime())){
					SessionErrors.add(actionRequest, "La fecha de certificación no puede ser posterior al día de hoy");
					throw new FormOpcionSSSValidacionFechasException();
				}
					
				int respuesta =  DateUtils.calculaDiasHabilesEntreFechas(detOpSS.getFechaCerti(), detOpSS.getFechaElecc(), true, feriados); 
			
				if(respuesta==0 || respuesta==1 || respuesta==2 || respuesta==3){
	// 				esta ok
					//return true;
	//				jQuery('#<portlet:namespace />valida_fechas').val("OK");
				}else if(respuesta==999){
					SessionErrors.add(actionRequest, "La fecha de certificación no puede ser posterior a la fecha de elección");
					throw new FormOpcionSSSValidacionFechasException();

				}else if(respuesta==998){
					SessionErrors.add(actionRequest, "La fecha de certificación o de elección no puede ser posterior a la fecha del día");
					throw new FormOpcionSSSValidacionFechasException();

				}else{	
					SessionErrors.add(actionRequest, "La fecha de certificación debe ser como máximo 3 días hábiles antes de la fecha de elección");
					throw new FormOpcionSSSValidacionFechasException();

				} 
				_log.debug("Opciones Hoy es: " + sdf.format(hoy.getTime()));
				/* Validar si al Lunes que se desea presentar, la fecha de certificacion es maximo 10 dias anterior.  */
//				Calendar hoy = DateUtils.getCalendarGMTMenos3(); //Calendar.getInstance();
//				if(hoy.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ){
				/*if(hoy.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ){
					// dejamos asi porque es el mismo dia que se presenta, se esta cargando.
//					_log.debug("Opciones Hoy es: " + sdf.format(hoy.getTime()) + " y es Lunes");
					_log.debug("Opciones Hoy es: " + sdf.format(hoy.getTime()) + " y es Martes");
				}else if(hoy.get(Calendar.DAY_OF_WEEK) < Calendar.TUESDAY ){
					hoy.add(Calendar.DATE, Calendar.TUESDAY-hoy.get(Calendar.DAY_OF_WEEK)-1);
				}else{
					hoy.add(Calendar.DATE, (7+Calendar.TUESDAY) - hoy.get(Calendar.DAY_OF_WEEK) );	
				}*/
//				   Calculamos siguiente lunes, evaluamos si es feriado, y si es sumamos n días hasta día hábil.	
//				   hoy.add(Calendar.DATE, (Calendar.SATURDAY - hoy.get(Calendar.DAY_OF_WEEK)+2 )) ;
//				   _log.debug("Opciones calculo proximo Lunes es: " + sdf.format(hoy.getTime()));
//				_log.debug("Opciones calculo proximo Martes es: " + sdf.format(hoy.getTime()));
//				   if(hoy.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && DateUtils.esFeriadoOFinde(hoy.getTime(), feriados)){
			  /* if(hoy.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && DateUtils.esFeriadoOFinde(hoy.getTime(), feriados)){	
				   _log.debug("Opciones proximo día hábil es: " + sdf.format(hoy.getTime()));
				   hoy.add(Calendar.DATE,1);
				   while(DateUtils.esFeriadoOFinde(hoy.getTime(), feriados)){
					   hoy.add(Calendar.DATE,1);
					   _log.debug("Opciones proximo día hábil es: " + sdf.format(hoy.getTime()));
				   }	   
			   }*/
				
			   FechaOpcionSSSUtil opc = new FechaOpcionSSSUtil();
			   
			   Date fechaProximaSSS =  opc.obtenerProximaFechaOpcionPresentar();
			   Date fechaPresentadaSSS = opc.obtenerUltimaFechaPresentadaSSS();
			  	   
			   if (fechaProximaSSS == null ) {
				   SessionErrors.add(actionRequest, "No hay  fecha de presentación a la SSS");
					throw new NoExisteFechaConfiguradaPressSuper();
			   }
			   
			   _log.debug("Fecha de próxima presentacion SSS: " + sdf.format(fechaProximaSSS.getTime()));
			   
			   
			   
			   if (DateUtils.isMismoDia(fechaPresentadaSSS, hoy.getTime())) {
					SessionErrors.add(actionRequest, "No se puede cargar mientras se presenta a la SSS");
					throw new FormOpcionSSSFechaIgualPressException();
			   }
			   
			   
			   
			   
				Date hoy00 = DateUtils.getMismoDia_00_00hs(fechaProximaSSS); 
				respuesta = DateUtils.calculaDiasHabilesEntreFechas(detOpSS.getFechaCerti(), hoy00, false, feriados); 
				if(respuesta > 10){
					SessionErrors.add(actionRequest, "La fecha de certificación debe ser como máximo 10 días hábiles antes de la fecha de presentación a la SSS");
					throw new FormOpcionSSSValidacionFechasException();
				}
				
				if(!SessionErrors.isEmpty(actionRequest)){
					Long idOpcionSss = ParamUtil.getLong(actionRequest, "id_opcionsss");
					
					detOpSS.setId(idOpcionSss);
					
					session.setAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION, detOpSS);
				}
			}catch (Exception e) {
				if (e instanceof FormOpcionSSSValidacionFechasException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof NoExisteFechaConfiguradaPressSuper) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof FormOpcionSSSFechaIgualPressException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
			}	

	//		graba toda la carga inicial
			if(cmd.equals(Constants.ADD) && SessionErrors.isEmpty(actionRequest) ){
				try {
					
					util.validarNroFormDuplicado(detOpSS.getNroFormulario(), detOpSS.getId().intValue() );
					
					id_opcion = util.insertarOpcionSS(detOpSS, user.getScreenName());
					
				}catch (Exception e) {
					if (e instanceof FormOpcionSSSDuplicadoException) {
						SessionErrors.add(actionRequest, e.getClass().getName());
					}
					if (e instanceof FormOpcionSSSNoEnviadoException) {
						SessionErrors.add(actionRequest, e.getClass().getName());
					}
					if (e instanceof FormOpcionSSSFechaIgualPressException) {
						SessionErrors.add(actionRequest, e.getClass().getName());
					}	
				}

			}
			// la correspondencia y algun item ya existe.
			if(cmd.equals(Constants.UPDATE) && SessionErrors.isEmpty(actionRequest)){
				
				Long idOpcionSss = ParamUtil.getLong(actionRequest, "id_opcionsss");
				
				detOpSS.setId(idOpcionSss);
				
				id_opcion = util.actualizarOpcionSS(detOpSS, user.getScreenName());
				
			}
			
			if (id_opcion > 0 && SessionErrors.isEmpty(actionRequest)  ) {
				String successMessage = ParamUtil.getString(actionRequest,"successMessage");
				SessionMessages.add(actionRequest, "request_processed",successMessage);
			
				detOpSS.setId(Long.valueOf(String.valueOf(id_opcion)));
			}else{
				SessionErrors.add(actionRequest, "Error al grabar Opcion de SSS");
//				setForward(actionRequest, "portlet.afiliados.error");
			}
			
			session.setAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION, detOpSS);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		User user = PortalUtil.getUser(renderRequest);

//		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		String cmd = ParamUtil.getString(renderRequest, "editaropcion");
		String cuilOpcion = ParamUtil.getString(renderRequest,"cuil_titular");
		String nroFormulario = ParamUtil.getString(renderRequest,"nro_formulario");
		DetalleOpcionesSS detOpcSS = null;
		
		if(StringUtils.checkNotEmpty(cuilOpcion) && (cmd.equalsIgnoreCase("SI") || cmd.equalsIgnoreCase("NO")) ){
			
			detOpcSS = util.buscarOpcionSssPorCuil(cuilOpcion,nroFormulario );
			
			if(detOpcSS != null){
				session.setAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION, detOpcSS);
			}
			
			if(cmd.equalsIgnoreCase("NO")){ // solo view
				renderRequest.setAttribute("esView", "view");
			}else{
				renderRequest.setAttribute("esView", "edicion");
			}
		}
		
		if (cmd.equals(Constants.DELETE)) {

			util.eliminarOpcionSS(cuilOpcion, nroFormulario, user.getScreenName());
			
			List<Afiliado> busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosOpciones(cuilOpcion, null, null, null, 0, 0, false);
		
			renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION, PortletSession.APPLICATION_SCOPE);
			renderRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION, busqueda,
					PortletSession.APPLICATION_SCOPE);
			
			return mapping.findForward(getForward(renderRequest,
					"portlet.afiliados.opciones.result.search"));		

		}
		
		if (cmd.equals(Constants.RESTORE)) {

			util.recuperarOpcionSS(cuilOpcion, nroFormulario, user.getScreenName());
			
			List<Afiliado> busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosOpciones(cuilOpcion, null, null, null, 0, 0, false);
		
			renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION, PortletSession.APPLICATION_SCOPE);
			renderRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION, busqueda,
					PortletSession.APPLICATION_SCOPE);
			
			return mapping.findForward(getForward(renderRequest,
					"portlet.afiliados.opciones.result.search"));		

		}
		
		StringBuffer tabs1 = new StringBuffer();
		tabs1.append("informacion_general");
		renderRequest.setAttribute("tabs1", tabs1.toString());
			
		return mapping.findForward(getForward(renderRequest,
				"portlet.afiliados.editar_opcion_entry"));
	}

	private DetalleOpcionesSS armarOpcionFormEntry(ActionRequest actionRequest)
			throws Exception {
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);
		
		DetalleOpcionesSS opcionAfi = null;
		opcionAfi = armarOpcionFormulario(request);
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		session.setAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION, opcionAfi);
		
		return opcionAfi;
	}


	private DetalleOpcionesSS armarOpcionFormulario(HttpServletRequest request)
			throws ParseException, PortalException, SystemException {
		
		DetalleOpcionesSS detOpc = null;
		
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		
//		String opciones = ParamUtil.getString(request, "opciones");
		String cuil_titular = ParamUtil.getString(request, "cuil_titular");
		int id_delegacion = ParamUtil.getInteger(request, "id_delegacion"); 
		String descDelegacion = ParamUtil.getString(request, "delegacion");
		int tomo = ParamUtil.getInteger(request, "tomo");
		int libro = ParamUtil.getInteger(request, "libro");
		int formNro = ParamUtil.getInteger(request, "formNro");
		String regimen = ParamUtil.getString(request, "regimen");		
//		String apeynom = ParamUtil.getString(request, "apeynom");
		String apellido = ParamUtil.getString(request, "apellido");
		String nombre = ParamUtil.getString(request, "nombre");
		String cuitEmpleador = ParamUtil.getString(request, "cuit_empleador");
//		String eleccionFechaDia = ParamUtil.getString(request, "fechaEleccionDia");
//		String eleccionFechaMes = ParamUtil.getString(request, "fechaEleccionMes");
//		String eleccionFechaAnio = ParamUtil.getString(request,"fechaEleccionAnio");
//		String certifFechaDia = ParamUtil.getString(request, "fechaCertiDia");
//		String certifFechaMes = ParamUtil.getString(request, "fechaCertiMes");
//		String certifFechaAnio = ParamUtil.getString(request,"fechaCertiAnio");
		Integer eleccionFechaDia = ParamUtil.getInteger(request, "fechaEleccionDia");
		Integer eleccionFechaMes = ParamUtil.getInteger(request, "fechaEleccionMes");
		Integer eleccionFechaAnio = ParamUtil.getInteger(request,"fechaEleccionAnio");
		Integer certifFechaDia = ParamUtil.getInteger(request, "fechaCertiDia");
		Integer certifFechaMes = ParamUtil.getInteger(request, "fechaCertiMes");
		Integer certifFechaAnio = ParamUtil.getInteger(request,"fechaCertiAnio");
//		Date eleccionFecha = null;
		Calendar eleccionFecha = DateUtils.getCalendarGMTMenos3();
		
		try {
//			eleccionFecha = formatoFecha.parse(eleccionFechaDia + "/"
//					+ (Integer.parseInt(eleccionFechaMes) + 1) + "/"
//					+ eleccionFechaAnio);
			eleccionFecha.set(eleccionFechaAnio, eleccionFechaMes /*+ 1*/, eleccionFechaDia, 00, 00, 00);
			eleccionFecha.set(Calendar.MILLISECOND, 000);
		} catch (Exception e) {
			eleccionFecha = null;
		}
//		Date certifFecha = null;
		Calendar certifFecha = DateUtils.getCalendarGMTMenos3();
		try {
//			certifFecha = formatoFecha.parse(certifFechaDia + "/"
//					+ (Integer.parseInt(certifFechaMes) + 1) + "/"
//					+ certifFechaAnio);
			certifFecha.set (certifFechaAnio, certifFechaMes /*+1*/, certifFechaDia, 00, 00, 00);
			certifFecha.set(Calendar.MILLISECOND, 000);
		} catch (Exception e) {
			certifFecha = null;
		}
		String sexo = ParamUtil.getString(request, "sexo");
		int provincia = ParamUtil.getInteger(request, "provincia");
		int localidad = ParamUtil.getInteger(request, "localidad");
		String codAreaTelefono = ParamUtil.getString(request, "cod_area_telefono");
		String telefono = ParamUtil.getString(request, "telefono");
		String codAreaTelLaboral = ParamUtil.getString(request, "cod_area_tel_laboral");
		String telefonoLab = ParamUtil.getString(request, "telefonoLab");
		String codAreaCelular = ParamUtil.getString(request, "cod_area_celular");
		String celular = ParamUtil.getString(request, "celular");
		String email = ParamUtil.getString(request, "email");
		String calle = ParamUtil.getString(request, "calle");
		String numero = ParamUtil.getString(request, "numero");
		int piso = ParamUtil.getInteger(request, "piso");
		String dpto = ParamUtil.getString(request, "dpto");
		String codPostal = ParamUtil.getString(request, "cod_postal");
		int anteriorOs = ParamUtil.getInteger(request, "obra_social_ant");
		String unificaAportes = ParamUtil.getString(request, "unifica_aportes");
		String apeyNomConyuge = ParamUtil.getString(request, "apeyNomConyuge");
		String cuilConyuge	= ParamUtil.getString(request, "cuilConyuge");
		String proyecto	= ParamUtil.getString(request, "proyecto","");
		if(StringUtils.checkEmpty(proyecto)){
			proyecto =null;
		}
		if(regimen.equalsIgnoreCase("MT") || regimen.equalsIgnoreCase("ESD")){ // Regimen Monotributo
			anteriorOs = 0;
			cuitEmpleador="00000000000";
		}else{
			unificaAportes = "NO"; // para RG y ESD no se unifica.
			apeyNomConyuge = "";
			cuilConyuge = "";
		}
		Long id = ParamUtil.getLong(request, "id_opcionsss"); 

		detOpc = new DetalleOpcionesSS("Sc", id_delegacion, descDelegacion, libro, tomo, formNro, regimen, 
				cuil_titular, apellido, nombre, sexo, calle, numero, piso, dpto, String.valueOf(localidad), 
				codAreaTelefono, telefono, codAreaTelLaboral, telefonoLab, codAreaCelular, celular, email, 
				anteriorOs, cuitEmpleador, eleccionFecha.getTime(), certifFecha.getTime(), null, null, codPostal, 
				String.valueOf(provincia), null, unificaAportes, cuilConyuge, apeyNomConyuge, proyecto);
		
		detOpc.setId(id);
		
		return detOpc;
	}
}

