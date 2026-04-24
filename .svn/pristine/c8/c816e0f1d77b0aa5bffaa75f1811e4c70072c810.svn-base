package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jfree.util.Log;

import ar.com.ospim.afiliados.AfliadoYaTieneConyugeException;
import ar.com.ospim.afiliados.ConyugeNoPuedeSerSolteroException;
import ar.com.ospim.afiliados.DuplicateAfiliadoIdException;
import ar.com.ospim.afiliados.HijoNoPuedeSerCasadoException;
import ar.com.ospim.afiliados.IntegranteGrupoNoBorrableException;
import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.beans.AfiObservacion;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.reportes.ReporteHistoricoMovimientosAfiliadoExcel;
import ar.com.ospim.afiliados.services.AfiObservacionServiceUtil;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.SituLaboralServiceUtil;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarAfiliadoEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * @modif  SVA
 * 
 */
public class EditarAfiliadoEntryAction extends PortletAction {
	
	private PlanServiceUtil planService = new PlanServiceUtil();
	
	private Logger _log = Logger.getLogger(this.getClass());
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		
		String cmd = (String) session.getAttribute(Constants.CMD);
		
		String borrar = ParamUtil.getString(actionRequest, "borrar");
		
		User user = PortalUtil.getUser(actionRequest);
		
		try {
			_log.debug("Cmd : " +cmd + " usuario: "+ user.getScreenName() );
			if (borrar.equals("borrar")) {
				this.borraAfiliadoEntry(actionRequest);
				setForward(actionRequest, "portlet.afiliados.view");
			} else if (cmd.equals(Constants.ADD)
					|| cmd.equals(Constants.UPDATE)) {
				this.armarAfiliadoEntry(actionRequest);
			}

			String tabs1 = "informacion_adicional";

			actionRequest.setAttribute("tabs1", tabs1);
		} catch (Exception e) {
			if (e instanceof NoSuchAfiliadoEntryException
					|| e instanceof DuplicateAfiliadoIdException
					|| e instanceof AfliadoYaTieneConyugeException
					|| e instanceof HijoNoPuedeSerCasadoException
					|| e instanceof ConyugeNoPuedeSerSolteroException) {
				if (e instanceof NoSuchAfiliadoEntryException
						|| e instanceof DuplicateAfiliadoIdException
						|| e instanceof AfliadoYaTieneConyugeException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof HijoNoPuedeSerCasadoException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof ConyugeNoPuedeSerSolteroException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
			} else {
				throw e;
			}
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		this.cargarListas(renderRequest);
		
		// Quito los aportes de session se trata de un nuevo afiliado
		removeDataFromSession(renderRequest, session);
		
		String opciones = null, preCarga =null;
		String idPreAfiliado = null;
		
		Afiliado afiliado = null;
		Afiliado preAfiliado = null;
		
		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(renderRequest, "inte");
		int inteAux = ParamUtil.getInteger(renderRequest, "inteAux");
		// me fijo si me piso la variable el framework
		if (inte == 0) {
			inte=inteAux;
		}
		
		int nroFormulario = ParamUtil.getInteger(renderRequest, "nroFormulario");

		//borrar definitivamente  un integrante del grupo familiar
		String delete = ParamUtil.getString(renderRequest, Constants.CMD);
		if (delete != null && delete.equals(Constants.DELETE)) {
			//Uso esta variable porque el framework me la pisa en algun del super
			 if (EditarAfiliadoServiceUtil.validarBorradoIntegrantes(cuilTitular, inteAux) == 0) {
					SessionErrors.add(renderRequest, IntegranteGrupoNoBorrableException.class.getName());
					return mapping.findForward("portlet.borrar.error");
			 }
			 EditarAfiliadoServiceUtil.deleteAfiliadoEntry(cuilTitular,inteAux);
			 Afiliado afiBorrar = new Afiliado(cuilTitular, inteAux);
			 List<Afiliado> afiResults = (ArrayList<Afiliado>) session.getAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION);
			 afiResults.remove(afiBorrar);
			 renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, PortletSession.APPLICATION_SCOPE);			
				renderRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, afiResults,
					PortletSession.APPLICATION_SCOPE);
			 
			 return mapping.findForward("portlet.afiliados.result.search");
		}
		
//		Observaciones internas
		List<AfiObservacion> obsInternasGrupoFliar =  AfiObservacionServiceUtil.getObservaciones(cuilTitular, inte);
		renderRequest.setAttribute(WebKeysAfiliados.OBSERVACIONES_GRUPO_FLIAR, obsInternasGrupoFliar);
		
//		Suspención de cobertura médica
		List<AfiSuspencionCobertura> suspenciones = PlanServiceUtil.getSuspencionesCobMedicaBeneficiario(cuilTitular, inte);
		
		
		String ddeReincorporar = renderRequest.getParameter(WebKeysAfiliados.DESDE_REINCORPORAR);
		if (StringUtils.checkNotEmpty(ddeReincorporar) && !ddeReincorporar.equalsIgnoreCase("null")) {
			renderRequest.setAttribute(WebKeysAfiliados.DESDE_REINCORPORAR,ddeReincorporar);
		}
		// Logica para sacar todo de la sesion cuando corresponde
		if (SessionErrors.isEmpty(renderRequest)
				&& !(renderRequest.getAttribute("tabs1") != null && renderRequest
						.getAttribute("tabs1").equals("informacion_adicional"))) {
			// hasta aca chequeo que no este cargando la 2da solapa y que no
			// haya errores para mostrar
//			Afiliado afiliado = (Afiliado) session.getAttribute((WebKeysAfiliados.AFILIADO_EN_EDICION));
			afiliado = (Afiliado) session.getAttribute((WebKeysAfiliados.AFILIADO_EN_EDICION));
//			String cuil_titular = ParamUtil.getString(renderRequest,"cuil_titular");
			// hago este chequeo por si estoy volviendo a la primer solapa de la
			// edicion
			if (afiliado != null && cuilTitular != null
					&& !afiliado.getCuil_titular().equals(cuilTitular.trim())) {
				removeDataFromSession(renderRequest, session);
				session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			}
		}

		try {
			
			String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
			if(StringUtils.checkEmpty(cmd)){
				cmd = (String) session.getAttribute(Constants.CMD);
			}
			
			if (Validator.isNotNull(ParamUtil.getString(renderRequest, Constants.CMD))
					&& ParamUtil.getString(renderRequest, Constants.CMD)
							.equalsIgnoreCase(Constants.ADD)) {
				cmd = Constants.ADD;
			}
			
			opciones = ParamUtil.getString(renderRequest, "opciones");
			preCarga = ParamUtil.getString(renderRequest, "pre_carga");
			idPreAfiliado = ParamUtil.getString(renderRequest, "id_pre_afiliado");
			
			if (null != opciones && opciones.equals("true")) {
				session.setAttribute("opciones", opciones);
			}
			if (null != preCarga && preCarga.equals("true") && StringUtils.checkNotEmpty(idPreAfiliado) ) {
				session.setAttribute("pre_carga", preCarga);
				session.setAttribute("id_pre_afiliado", idPreAfiliado);
			}

			String unif_aportes = ParamUtil.getString(renderRequest, "unif_aportes");
			if (!unif_aportes.equals("true")) {
				afiliado = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
				
				if (session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION) == null || inte != afiliado.getInte()) {
					afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuilTitular, inte);
					if ( (cmd != null && !cmd.equalsIgnoreCase("add"))
							|| (null != opciones && opciones.trim().equals("true"))
							|| (null != preCarga && preCarga.trim().equals("true"))
						) {
						if ((null != opciones && opciones.trim().equals("true"))
							|| (null != preCarga && preCarga.trim().equals("true"))) {
							int result = 0;
							
							if(cmd == null || (cmd != null && cmd.equalsIgnoreCase(Constants.ADD))){
								try{
									result = EditarAfiliadoServiceUtil.existeAfiliado(cuilTitular, null);
								}catch (Exception e) {
									SessionErrors.add(renderRequest, DuplicateAfiliadoIdException.class.getName());
								}	
								if(result > 0){
									SessionErrors.add(renderRequest, DuplicateAfiliadoIdException.class.getName());
									return mapping.findForward("portlet.afiliados.error");
								}
							}
							if(!ar.com.ospim.util.StringUtils.checkEmpty(opciones) && opciones.equalsIgnoreCase("true")){
								afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntryOpciones(cuilTitular,nroFormulario);
							}
							if(!ar.com.ospim.util.StringUtils.checkEmpty(preCarga) && preCarga.equalsIgnoreCase("true")){
								afiliado = EditarAfiliadoServiceUtil.getAfiliadoPreCarga(cuilTitular, inte, Integer.parseInt(idPreAfiliado));
								
								if(afiliado.getTipoOperacion().equalsIgnoreCase(Constants.ADD) ){
									cmd = "add";
								}else{
									cmd = "update";
									preAfiliado = afiliado;
									afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuilTitular, inte);
									AfiPlan afiPlan = planService.buscarUltimoPlanAportes(cuilTitular); 
									afiliado.setAfiPlan(afiPlan);
									
									session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION, preAfiliado);
								}
							}
						} else {
							afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuilTitular, inte);
						}
					}
				} else if (afiliado.getApellido() == null
						|| afiliado.getApellido().equals("")) {
					afiliado = getAfiliadoFromRequest(PortalUtil.getHttpServletRequest(renderRequest));
				}
				if(cuilTitular != null && cuilTitular != "" && StringUtils.checkEmpty(preCarga)){	
	//				Buscamos el plan vigente del afiliado si es que tiene...
					AfiPlan afiPlan = planService.buscarUltimoPlanAportes(cuilTitular); 
					afiliado.setAfiPlan(afiPlan);
					List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().consultaUltimosComponentesPlanVigente(cuilTitular);

					List<AfiTercerizadoraServicio> tercerizAfi = TercerizadoraServiceUtil.buscarUltimasTercerizadorasContinuidadDelAfiliado(null, cuilTitular);
					session.setAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION, tercerizAfi);
					
					//almaceno la lista en sesion
					renderRequest.setAttribute("IdsSocio", afiAportes); 
					
					List<SituacionLaboral> laboralList = SituLaboralServiceUtil.buscaSituLaboral(cuilTitular,inte);
					
					session.setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL,laboralList);
					
				}else if(StringUtils.checkNotEmpty(preCarga)){
					ArrayList<AfiTercerizadoraServicio> afiTercerizadoras = new ArrayList<AfiTercerizadoraServicio>();
					AfiTercerizadoraServicio ats = new AfiTercerizadoraServicio(afiliado.getId_tercerizadora(), afiliado.getDesc_tercerizadora(),
							afiliado.getAfiPlan()!=null?afiliado.getAfiPlan().getVigenDesde():null, 
							afiliado.getAfiPlan()!=null?afiliado.getAfiPlan().getVigenHasta():null);
					ats.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					
					if(ats.getTercerizadora().getId_tercerizadora() == null){
						ats = null;
					}else{
						afiTercerizadoras.add(ats);
					}
					Calendar corteInicio = Calendar.getInstance();
					corteInicio.set(2019, Calendar.DECEMBER, 01); // 1/12/2019
					
					Calendar corteFin = Calendar.getInstance();
					corteFin.setTime(corteInicio.getTime());
					corteFin.add(Calendar.DATE, -1); //corte FIN!!

					if( "MPS".equalsIgnoreCase(ats.getTercerizadora().getId_tercerizadora()) && !corteFin.getTime().before(afiliado.getVigen_fecha())  ){
						AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio();
						TercerizadoraServicio terce = new TercerizadoraServicio("MEN" , "MOLINEROS POR ENSALUD");
						atsAjuste.setTercerizadora(terce);
						atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						atsAjuste.setAfiliado(afiliado);
						atsAjuste.setFechaFinPres(null);
						atsAjuste.setFechaInicioPres(afiliado.getVigen_fecha().before(corteInicio.getTime())?corteInicio.getTime():afiliado.getVigen_fecha());
						afiTercerizadoras.add(atsAjuste);

						corteInicio.add(Calendar.DATE, -1); //corte FIN!!
						ats.setFechaFinPres(corteInicio.getTime());
					
					}
					
					
					if(!afiTercerizadoras.isEmpty()){
						session.setAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION, afiTercerizadoras);
					}
				}
				if(afiliado != null) {
//				asignamos las suspenciones de coberturas del afiliado...
					afiliado.setSuspencionCobertura(suspenciones);
				}
				session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
			} else {
				ActionUtil.setAfiliadoExistenteSession(renderRequest);
			}
//			// Quito los aportes de session se trata de un nuevo afiliado
//			removeDataFromSession(renderRequest, session);

			if (Validator.isNotNull(cmd) && cmd.equals(Constants.ADD)) {
				session.setAttribute(Constants.CMD, Constants.ADD);
			} else {
				session.setAttribute(Constants.CMD, Constants.UPDATE);
			}
			
		} catch (Exception e) {
			if (e instanceof NoSuchAfiliadoEntryException
					|| e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass().getName());
				return mapping.findForward("portlet.afiliados.error");
			} else {
				throw e;
			}
		}

		StringBuffer tabs1 = new StringBuffer();

		if (renderRequest.getAttribute("tabs1") == null
				&& renderRequest.getParameter("tabs1") == null) {
			tabs1.append("informacion_general");
		} else if (renderRequest.getAttribute("tabs1") != null) {
			tabs1.append(renderRequest.getAttribute("tabs1"));
		} else if (renderRequest.getParameter("tabs1") != null) {
			tabs1.append(renderRequest.getAttribute("tabs1"));
		}

		renderRequest.setAttribute("tabs1", tabs1.toString());
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.afiliados.editar_afiliado_entry"));
	}

	public static void removeDataFromSession(RenderRequest renderRequest,
			HttpSession session) {
		
		session.removeAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL);
		session.removeAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION);
		session.removeAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);
		
	}

	protected void armarAfiliadoEntry(ActionRequest actionRequest)
			throws Exception {
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);
		Afiliado afiliado = getAfiliadoFromRequest(request);
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
	}

	@SuppressWarnings("unchecked")
	protected void borraAfiliadoEntry(ActionRequest actionRequest)
			throws Exception {
		
		String cuil_titular = ParamUtil.getString(actionRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(actionRequest, "inte");
		int motivo_baja = ParamUtil.getInteger(actionRequest, "motivo_baja", -1);
		int dia_baja = ParamUtil.getInteger(actionRequest, "baja_dia");
		int mes_baja = ParamUtil.getInteger(actionRequest, "baja_mes");
		int anio_baja = ParamUtil.getInteger(actionRequest, "baja_anio");

		if (-1 == motivo_baja) {
			throw new PrincipalException("No existe motivo de baja");
		}
		PortletSession portletSession = actionRequest.getPortletSession();

		List<MotivoBaja> motivosBaja = (ArrayList<MotivoBaja>) portletSession
				.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		GregorianCalendar fecha_baja = null;
		Date baja_fecha = null;
		if (0 != dia_baja && 0 != anio_baja) {
			fecha_baja = new GregorianCalendar(anio_baja, mes_baja, dia_baja);
			baja_fecha = fecha_baja.getTime();
		} else {
			baja_fecha = getMesesABaja(motivosBaja, motivo_baja);
		}

		User user = PortalUtil.getUser(actionRequest);

		EditarAfiliadoServiceUtil.borraAfiliadoEntry(cuil_titular, inte,
				motivo_baja, baja_fecha, user.getScreenName());
		
		List<Afiliado> grupoFamiliar = BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(cuil_titular);
		boolean existeIntegranteDiscap = false;
		boolean existeIntegranteConAntecedentesJud = false;
		
		if(grupoFamiliar!=null && grupoFamiliar.size()>0){
			for (Iterator<Afiliado> iterator = grupoFamiliar.iterator(); iterator.hasNext();) {
				Afiliado af = iterator.next();
				if(af.getDiscapacitado().equals("1")){
					existeIntegranteDiscap = true;
//					break;
				}
				if(af.getTieneAntecedentesJudiciales() == 1){
					existeIntegranteConAntecedentesJud = true;
//					break;
				}
			}
		}
		if(existeIntegranteConAntecedentesJud){
			this.enviarNovedadsobreAfiliadoConAntecJudiciales(cuil_titular);
			
		}

		if(existeIntegranteDiscap){
			this.enviarNovedadsobreAfiliadoDiscapacidad(cuil_titular);
		}
	}

	private Afiliado getAfiliadoFromRequest(HttpServletRequest request)
			throws ParseException, PortalException, SystemException {
		
		String opciones = ParamUtil.getString(request, "opciones");
		String preCarga = ParamUtil.getString(request, "pre_carga");
		
		String cuil_titular = ParamUtil.getString(request, "cuil_titular");
		int inte = ParamUtil.getInteger(request, "inte");
		String cuil = ParamUtil.getString(request, "cuil");
		String nombre = ParamUtil.getString(request, "nombre");
		String apellido = ParamUtil.getString(request, "apellido");
		int idSeccional = ParamUtil.getInteger(request, "id_seccional");
		String descSeccional = ParamUtil.getString(request, "seccional");
		Seccional seccional = new Seccional(idSeccional, descSeccional);
		String vigenteFechaMes = ParamUtil.getString(request, "vigenteFechaMes");
		String vigenteFechaDia = ParamUtil.getString(request, "vigenteFechaDia");
		String vigenteFechaAnio = ParamUtil.getString(request,"vigenteFechaAnio");
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");
		Date vigenFecha = null;
		try {
			vigenFecha = formatoDeFechaV.parse(vigenteFechaDia + "/"
					+ (Integer.parseInt(vigenteFechaMes) + 1) + "/"
					+ vigenteFechaAnio);
		} catch (Exception e) {
			vigenteFechaMes = ParamUtil.getString(request, "vigenteFechaMesAux");
			vigenteFechaDia = ParamUtil.getString(request, "vigenteFechaDiaAux");
			vigenteFechaAnio = ParamUtil.getString(request,"vigenteFechaAnioAux");
			
			try {
				vigenFecha = formatoDeFechaV.parse(vigenteFechaDia + "/"
						+ (Integer.parseInt(vigenteFechaMes) + 1) + "/"
						+ vigenteFechaAnio);
			} catch (Exception e1) {
				vigenFecha = null;
				Log.error("ERROR VIGEN FECHA");
			}
//			Si sale x NULL esta mal!!!
			
		}
		String sexo = ParamUtil.getString(request, "sexo");
		int provincia = ParamUtil.getInteger(request, "provincia");
		int localidad = ParamUtil.getInteger(request, "localidad");
		String cod_area_telefono = ParamUtil.getString(request, "cod_area_telefono");
		String telefono = ParamUtil.getString(request, "telefono");
		String cod_area_tel_laboral = ParamUtil.getString(request, "cod_area_tel_laboral");
		String tel_laboral = ParamUtil.getString(request, "tel_laboral");
		String cod_area_celular = ParamUtil.getString(request, "cod_area_celular");
		String celular = ParamUtil.getString(request, "celular");
		String email = ParamUtil.getString(request, "email");
		String calle = ParamUtil.getString(request, "calle");
		String numero = ParamUtil.getString(request, "numero");
		String piso = ParamUtil.getString(request, "piso");
		String dpto = ParamUtil.getString(request, "dpto");
		String cod_postal = ParamUtil.getString(request, "cod_postal");
		String barrio = ParamUtil.getString(request, "barrio");
		Domicilio domicilio = new Domicilio();
		domicilio.setProvinciaId(provincia);
		domicilio.setLocalidadId(localidad);
		domicilio.setCod_area_telefono(cod_area_telefono);
		domicilio.setTelefono(telefono);
		domicilio.setCod_area_tel_laboral(cod_area_tel_laboral);
		domicilio.setTel_laboral(tel_laboral);
		domicilio.setCod_area_celular(cod_area_celular);
		domicilio.setCelular(celular);
		domicilio.setCalle(calle);
		domicilio.setNumero(numero);
		domicilio.setPiso(piso);
		domicilio.setDepto(dpto);
		domicilio.setPostal_codi(cod_postal);
		domicilio.setBarrio(barrio);
		Domicilio[] dom = { domicilio };
		String discapacitado = ParamUtil.getString(request, "discapacitado");
		String censo2013 = ParamUtil.getString(request, "censo2013");
		int parentesco = ParamUtil.getInteger(request, "parentesco");
		String parentescoDesc =  ParamUtil.getString(request, "parentescoDesc");
		int nacionalidad = ParamUtil.getInteger(request, "nacionalidad");
		String documentoTipo = ParamUtil.getString(request, "documento_tipo");
		String docuNumero = ParamUtil.getString(request, "nroDoc");
		String fechaNacimientoMes = ParamUtil.getString(request,
				"fechaNacimientoMes");
		String fechaNacimientoDia = ParamUtil.getString(request,
				"fechaNacimientoDia");
		String fechaNacimientoAnio = ParamUtil.getString(request,
				"fechaNacimientoAnio");
		Date naciFecha = null;
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			naciFecha = formatoDeFecha.parse(fechaNacimientoDia + "/"
					+ (Integer.parseInt(fechaNacimientoMes) + 1) + "/"
					+ fechaNacimientoAnio);
		} catch (Exception e) {
			naciFecha = null;
		}
		int civilEsta = ParamUtil.getInteger(request, "estado_civil");
		int anteriorOs = ParamUtil.getInteger(request, "obra_social_ant");
		String observaciones = ParamUtil.getString(request, "obs");
		int idUoma = ParamUtil.getInteger(request, "id_uoma");
		int idAmtima = ParamUtil.getInteger(request, "id_amtima");
		int id_ospim = ParamUtil.getInteger(request, "id_ospim");
		int id_correspondencia = ParamUtil.getInteger(request, "numero_correspondencia");
		String tieneAntecJudiciales = ParamUtil.getString(request, "tiene_antecedentes_judiciales","0");
		String clientePreferencial = ParamUtil.getString(request, "cliente_preferencial","0");
		String proyecto = ParamUtil.getString(request, "proyecto",null);
		if(StringUtils.checkEmpty(proyecto)){
			proyecto = null;
		}
		String Id_ospim_baja_fechaS = ParamUtil.getString(request,
				"id_ospim_baja");
		String Id_uoma_baja_fechaS = ParamUtil.getString(request,
				"id_uoma_baja");
		String Id_amtima_baja_fechaS = ParamUtil.getString(request,
				"id_amtima_baja");
		Date idOspimBajaFecha = DateUtils.parse(Id_ospim_baja_fechaS,
				DateUtils.SHORT);
		Date idUomaBajaFecha = DateUtils.parse(Id_uoma_baja_fechaS,
				DateUtils.SHORT);
		Date idAmtimaBajaFecha = DateUtils.parse(Id_amtima_baja_fechaS,
				DateUtils.SHORT);
		String cuit = null;
		Afiliado afiliado = null;
		String razon_soc = null;
		
		Afiliado afiAux = null;
		if(StringUtils.checkNotEmpty(preCarga)){
			HttpSession session = request.getSession();
			afiAux = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			
		}
		if (null != opciones && opciones.equals("true")) {
			cuit = ParamUtil.getString(request, "cuit");
			razon_soc = ParamUtil.getString(request, "razon_soc");
			afiliado = new Afiliado(cuil_titular, inte, id_ospim,
					idOspimBajaFecha, idUomaBajaFecha, idAmtimaBajaFecha,
					idUoma, idAmtima, apellido, nombre, documentoTipo,
					docuNumero, sexo, cuil, naciFecha, civilEsta, nacionalidad,
					parentesco, seccional, anteriorOs, vigenFecha,
					observaciones, discapacitado, dom, cuit, razon_soc, 
					email, id_correspondencia, proyecto);
		} else {
			afiliado = new Afiliado(cuil_titular, inte, id_ospim,
					idOspimBajaFecha, idUomaBajaFecha, idAmtimaBajaFecha,
					idUoma, idAmtima, apellido, nombre, documentoTipo,
					docuNumero, sexo, cuil, naciFecha, civilEsta, nacionalidad,
					parentesco, seccional, anteriorOs, vigenFecha,
					observaciones, discapacitado, censo2013, dom, 
					email, id_correspondencia, tieneAntecJudiciales, 
					clientePreferencial, proyecto);
			
			if (null != preCarga && preCarga.equals("true") && afiAux != null) {
				afiliado.setAfiPlan(afiAux.getAfiPlan());
				afiliado.setId_tercerizadora(afiAux.getId_tercerizadora());
				afiliado.setDesc_tercerizadora(afiAux.getDesc_tercerizadora());
				afiliado.setLista_situ_laboral(afiAux.getLista_situ_laboral());
			}
		}
		
		Date bajaFecha  =  null;
		String bajaFechaParam = ParamUtil.getString(request, "baja_fecha_hidden");
		Integer idMotivoBajaParam = ParamUtil.getInteger(request, "id_motivo_baja_hidden",-1);
		
		try {
			bajaFecha = DateUtils.parse(bajaFechaParam, DateUtils.SHORT);
		} catch (ParseException e1) {
			bajaFecha = null;
		}
		if (afiliado.getParentesco() == null) {
			afiliado.setParentesco(parentescoDesc);
		}
		afiliado.setBaja_fecha(bajaFecha != null ? bajaFecha : null);
		afiliado.setId_motivo_baja(idMotivoBajaParam > 0  ? idMotivoBajaParam : 0);
		
		return afiliado;
	}

	private Date getMesesABaja(List<MotivoBaja> motivosBaja, int motivo_baja) {
		int meses = 0;
		for (MotivoBaja mot : motivosBaja) {
			if (mot.getId_motivo_baja() == motivo_baja) {
				meses = mot.getMeses_a_baja();
			}
		}
		Date baja_fecha = DateUtils.anyadeMeses(new Date(), meses);
		return baja_fecha;
	}
	
	private void enviarNovedadsobreAfiliadoConAntecJudiciales(String cuilTitular){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_LEGALES);

		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso cambios en afiliado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosJudicialGrupoFamiliar_"+cuilTitular+".xls");
		
	}
	
	private void enviarNovedadsobreAfiliadoDiscapacidad(String cuilTitular){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_DISCAPACIDAD);

		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Baja de Afiliado Discapacitado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosGrupoFamiliar_"+cuilTitular+".xls");
		
	}
	
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{
		
		TraeListasServiceUtil.getMotivosBaja(renderRequest);

		TraeListasServiceUtil.getPlanes(renderRequest);
		
		TraeListasServiceUtil.getTercerizadoraServicio(renderRequest);
		
		TraeListasServiceUtil.getCategoriasLaborales(renderRequest);
		
		TraeListasServiceUtil.getSituacionRevista(renderRequest);
		

	}
}