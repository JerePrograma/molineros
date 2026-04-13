/**
 */

package ar.com.ospim.novedades.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.DuplicateAfiliadoIdException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.novedades.beans.BusquedaPreAfiliadosFiltro;
import ar.com.ospim.novedades.beans.PreAfiliado;
import ar.com.ospim.novedades.beans.PreAfiliadoTotal;
import ar.com.ospim.novedades.service.PreAfiliadoServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 * 
 */
public class EditarPreAfiliadoAction extends PortletAction {
	
	private static final int ID_ORGANIZACION_OSPIM = 11337;

	private static final int ID_SECC_CAPITAL = 201;
	
	private static Log logger = LogFactoryUtil.getLog(EditarPreAfiliadoAction.class);


	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
		PreAfiliadoTotal preAfi = null;

		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		String cuil = null;
		String inte = null;	
		String msg = "";
		Integer id = null;
//		String tabs1 = "informacion_general";
		String tabs1 = ParamUtil.getString(renderRequest, "tabs1");
		cuil = ParamUtil.getString(renderRequest, "cuil_titular");
		inte = ParamUtil.getString(renderRequest, "inte");
		id = ParamUtil.getInteger(renderRequest, "idPreAfi");
		
		logger.debug("cmd: " + cmd +  " cuil: "+cuil + " inte: "+inte + " id: "+id);
		renderRequest.setAttribute("tabs1", tabs1.toString());	
		
		this.cargarListas(renderRequest);
		
	
//		manejo de solapa imagenes
		if(tabs1.equalsIgnoreCase("imagenes_afiliados")){
			preAfi = (PreAfiliadoTotal) session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);
			if(preAfi==null){
				//no empezo a cargar nada...
				renderRequest.setAttribute("tabs1", "informacion_general");
				renderRequest.setAttribute(Constants.CMD, Constants.SAVE );
				cmd = Constants.ADD;
			}else{
				renderRequest.setAttribute(Constants.CMD, cmd );
			}
		}
//		fin manejo de solapa imagenes
		if(!ar.com.ospim.util.StringUtils.checkEmpty(cmd)){
			
			

			
			
			//Inserta un nuevo titular o integrante
			if(cmd.equalsIgnoreCase(Constants.SAVE) ){
				
				preAfi = this.getAfiliadoFromRequest(renderRequest);
				
				if(StringUtils.checkNotEmpty(preAfi.getCuil_titular())){   // para controlar cambio de solapas
					try{
						int result = PreAfiliadoServiceUtil.existePreAfiliado(preAfi.getCuil_titular()); 
						// 1 es Error o Integrante que ya existe, 2 Titular en Padron, 3 Titular en pre_afiliado
						if(result == 1 || ((result == 2 || result == 3) 
								&& preAfi.getId_parentesco_sss() == 0) // titular 
								&& (preAfi.getTipo_novedad()==null
								    || (preAfi.getTipo_novedad()!=null&&!preAfi.getTipo_novedad().equalsIgnoreCase(Constants.UPDATE)))
							){ 
	//						throw new DuplicateAfiliadoIdException();
							SessionErrors.add(renderRequest, "error-afiliado-repetido");
							SessionErrors.add(renderRequest, DuplicateAfiliadoIdException.class.getName());
						}else{
							int resultInte = PreAfiliadoServiceUtil.existePreAfiliado(preAfi.getCuil()); 

							if(resultInte == 1 || resultInte == 2 || resultInte == 4) {// existe integrante en otro grupo familiar
								renderRequest.setAttribute("esIntegranteDelCuilTitular", preAfi.getCuil_titular());								
								SessionErrors.add(renderRequest, "error-afiliado-repetido");
								SessionErrors.add(renderRequest, DuplicateAfiliadoIdException.class.getName());
							}else{
								id = PreAfiliadoServiceUtil.insertaPreAfiliadoEntry(preAfi, user);
								
								preAfi = PreAfiliadoServiceUtil.getInstance().buscarPreAfiliado(preAfi.getCuil_titular(), preAfi.getInte(), id);
								enviarNovedadaAfiliaciones(preAfi);
								
								session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION, preAfi);
								
							}
						}
						
						renderRequest.setAttribute(Constants.CMD, id==null?Constants.SAVE:Constants.UPDATE );
						
					} catch (Exception e) {
						if (e instanceof DuplicateAfiliadoIdException) {
	//						SessionErrors.add(renderRequest, e.getClass().getName());
							SessionErrors.add(renderRequest, "error-afiliado-repetido");
	//						throw new DuplicateAfiliadoIdException();
						}		
					}
	
					if (SessionErrors.isEmpty(renderRequest)) {
	//					String successMessage = ParamUtil.getString(renderRequest, "successMessage");
	//					SessionMessages.add(renderRequest, "request_processed", successMessage);
						
						msg = LanguageUtil.get(defaultLocale, "insert-pre-afiliado");
						msg = msg + id;
						SessionMessages.add(renderRequest, "insertOk");
						renderRequest.setAttribute("msgOk", msg);
						
						logger.debug("Usuario: " + user.getScreenName() 
								+ " cmd: " + cmd 
								+ " id pre-afi: " + id);
					}
				}else{
					renderRequest.setAttribute(Constants.CMD, Constants.SAVE);
				}
			}	
	
			if(cmd.equalsIgnoreCase(Constants.UPDATE) 
					&& tabs1.equalsIgnoreCase("informacion_general") ){
				
				preAfi = this.getAfiliadoFromRequest(renderRequest);
				
				id = preAfi.getId();
				
				if(id != null && id > 0){ // por el cambio de solapa controlo esto...
					PreAfiliadoServiceUtil.actualizaPreAfiliadoEntry(preAfi, user);
					
					session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
					session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION, preAfi);
					
					msg = LanguageUtil.get(defaultLocale, "update-pre-afiliado");
					msg = msg + id;
					SessionMessages.add(renderRequest, "updateOk");
					renderRequest.setAttribute("msgOk", msg);
					
					logger.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id pre-afi: " + id);
				}
				renderRequest.setAttribute(Constants.CMD, Constants.UPDATE );
			}
				
			if(cmd.equalsIgnoreCase(Constants.EDIT) ){
				
				String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
				inte = ParamUtil.getString(renderRequest, "inte");
//				
//				preAfi = PreAfiliadoServiceUtil.buscarPreAfiliado(cuil_titular, Integer.parseInt(inte));
				id = ParamUtil.getInteger(renderRequest, "id");
				if(id == 0){ // formazamos esto para afiliados del padron...
					id = null;
				}
				preAfi = PreAfiliadoServiceUtil.buscarPreAfiliado(cuil_titular, Integer.parseInt(inte),id);
				
				if(id == null || id==0){
					renderRequest.setAttribute(Constants.CMD, Constants.SAVE );
					renderRequest.setAttribute("tipo_novedad_pre_afi", Constants.UPDATE);
				}else{
					renderRequest.setAttribute(Constants.CMD, Constants.UPDATE );
				}
				
		
				session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
				session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION, preAfi);
				
			}
			
			if(cmd.equalsIgnoreCase(Constants.DELETE) ){
				
				cuil = ParamUtil.getString(renderRequest, "cuil_titular");
				inte = ParamUtil.getString(renderRequest, "inte");
				id = ParamUtil.getInteger(renderRequest, "idPreAfi");
				boolean esCascada = ParamUtil.getBoolean(renderRequest, "esCascada");
				
				PreAfiliadoServiceUtil.borrarPreAfiliado(cuil, Integer.valueOf(inte), id, esCascada, user);
				
//				vuelvo a aplicar la busqueda para que desaparezcan los de baja

				BusquedaPreAfiliadosFiltro filtro = (BusquedaPreAfiliadosFiltro) 
					session.getAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS);
				
				List<PreAfiliadoTotal> busqueda = PreAfiliadoServiceUtil.getBusquedaPreAfiliados(filtro);
				
				int cantResultados = busqueda.size()>0?busqueda.get(0).getTotal_registros():0;

				session.removeAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO);
				session.setAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO, busqueda);
				
				if(busqueda != null && busqueda.size() > 0){
					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_TOTAL_REGISTROS, cantResultados);
					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_OFFSET_REG, 1); //pagina_sel
				}else{
					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_TOTAL_REGISTROS,0 );
					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_OFFSET_REG, 0);
				}
				return mapping.findForward("portlet.pre.carga.afiliados.result.search");

			}
			
			if(cmd.equalsIgnoreCase(Constants.VIEW) ){
				
				String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
				inte = ParamUtil.getString(renderRequest, "inte");
				id = ParamUtil.getInteger(renderRequest, "id");
				
				preAfi = PreAfiliadoServiceUtil.buscarPreAfiliado(cuil_titular, Integer.parseInt(inte), id);

				renderRequest.setAttribute(Constants.CMD, Constants.VIEW );
		
				session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
				session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION, preAfi);
				
				return mapping.findForward(getForward(renderRequest, "portlet.novedades.view_pre_afiliado"));
			}
			
			if(cmd.equalsIgnoreCase(Constants.ADD) ){
				
				int idSecc = getIdSeccionalFromUserOrganization(user);
				
				String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
				
				if(ar.com.ospim.util.StringUtils.checkEmpty(cuil_titular)){
//					es titular, nada que todo siga su curso 
				}else{
//					es integrante, planteamos un supuesto objeto con el cuil titular y el inte en 99 para que me permita otro comportamiento en el abm
					renderRequest.setAttribute("esIntegranteDelCuilTitular", cuil_titular);	
				}
				
				renderRequest.setAttribute("id_seccional_sugerida",idSecc);

				renderRequest.setAttribute(Constants.CMD, Constants.SAVE );

				renderRequest.setAttribute("tipo_novedad_pre_afi", Constants.ADD);
				
				session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);
			}
			
			if(preAfi!=null && preAfi.getInte()!=null && preAfi.getInte() > 0){
				renderRequest.setAttribute("esIntegranteDelCuilTitular", preAfi.getCuil_titular());	
			}
				
			return mapping.findForward(getForward(renderRequest,
					"portlet.novedades.editar_pre_afiliado"));
			
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.novedades.editar_pre_afiliado"));
		
	}
	
	private int getIdSeccionalFromUserOrganization(User user){
		
		int result = ID_SECC_CAPITAL; //= 0;
		List<Organization> orgs = new ArrayList<Organization>();
		try {
			orgs = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());
		} catch (SystemException e) {
			logger.debug("NO POSEE ORGANIZACION EL USER: "+ user.getFullName());
			logger.debug(e);
		}
		
		for (Iterator<Organization> iterator = orgs.iterator(); iterator.hasNext();) {
			Organization o = iterator.next();
			
			if(o.getOrganizationId() == ID_ORGANIZACION_OSPIM){ //Ospim San Juan
				result = ID_SECC_CAPITAL;
			}
		}

		
		return result;

	}
	
	private PreAfiliadoTotal getAfiliadoFromRequest(RenderRequest request) {
		
		Integer id = ParamUtil.getInteger(request, "id",0);

		String tipo_novedad = ParamUtil.getString(request, "tipo_novedad");
		String cuil_titular = ParamUtil.getString(request, "cuil_titular");
		String inte = ParamUtil.getString(request, "inte");
//		Integer inte = ParamUtil.getInteger(request, "inte");
		String cuil = ParamUtil.getString(request, "cuil");
		String nombre = ParamUtil.getString(request, "nombre");
		String apellido = ParamUtil.getString(request, "apellido");
		int idSeccional = ParamUtil.getInteger(request, "id_seccional");
//		String descSeccional = ParamUtil.getString(request, "seccional");
//		Seccional seccional = new Seccional(idSeccional, descSeccional);
		String vigenteFechaMes = ParamUtil.getString(request, "vigenteFechaMes");
		String vigenteFechaDia = ParamUtil.getString(request, "vigenteFechaDia");
		String vigenteFechaAnio = ParamUtil.getString(request,"vigenteFechaAnio");
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date vigenFecha = null;
		try {
			vigenFecha = formatoDeFecha.parse(vigenteFechaDia + "/"
					+ (Integer.parseInt(vigenteFechaMes) + 1) + "/"
					+ vigenteFechaAnio);
		} catch (Exception e) {
			vigenFecha = null;
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
		String calle = ParamUtil.getString(request, "calle", ""); //xq no puede ser null en BD para el integrante q no carga Domi ni Labo
		String numero = ParamUtil.getString(request, "numero");
		String piso = ParamUtil.getString(request, "piso");
		String dpto = ParamUtil.getString(request, "dpto");
		String cod_postal = ParamUtil.getString(request, "cod_postal", ""); //xq no puede ser null en BD para el integrante q no carga Domi ni Labo
		String barrio = ParamUtil.getString(request, "barrio");
		String discapacitado = ParamUtil.getString(request, "discapacitado");
		//String parentesco = ParamUtil.getString(request, "parentesco");
		int parentesco = ParamUtil.getInteger(request, "parentesco");
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
		try {
			naciFecha = formatoDeFecha.parse(fechaNacimientoDia + "/"
					+ (Integer.parseInt(fechaNacimientoMes) + 1) + "/"
					+ fechaNacimientoAnio);
		} catch (Exception e) {
			naciFecha = null;
		}
		int civilEsta = ParamUtil.getInteger(request, "estado_civil");
//		int anteriorOs = ParamUtil.getInteger(request, "obra_social_ant");
		String observaciones = ParamUtil.getString(request, "obs");
		
		String cuit_empleador = ParamUtil.getString(request, "cuit_empleador", ""); //xq no puede ser null en BD para el integrante q no carga Domi ni Labo
		String sucu = ParamUtil.getString(request, "sucur", ""); //xq no puede ser null en BD para el integrante q no carga Domi ni Labo
		String escala = ParamUtil.getString(request, "escala_salarial");
		String razonSocial = ParamUtil.getString(request, "empleador");
		
		Integer categoria = ParamUtil.getInteger(request, "categoria");
		Integer situRevista = ParamUtil.getInteger(request, "situRevista");
		
		String fechaIngreMes = ParamUtil.getString(request,
				"fechaIngresoEmpresaMes");
		String fechaIngreDia = ParamUtil.getString(request,
				"fechaIngresoEmpresaDia");
		String fechaIngreAnio = ParamUtil.getString(request,
				"fechaIngresoEmpresaAnio");
		Date ingreFecha = null;
		try {
			ingreFecha = formatoDeFecha.parse(fechaIngreDia + "/"
					+ (Integer.parseInt(fechaIngreMes) + 1) + "/"
					+ fechaIngreAnio);
		} catch (Exception e) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DATE, 1);
			c.set(Calendar.MONTH, 1);
			c.set(Calendar.YEAR, 1800);
			ingreFecha = c.getTime();
		}
		
		Integer idPlan = ParamUtil.getInteger(request, "nuevoPlan",0);
		String fechaVigenDesdeDia = ParamUtil.getString(request, "fechaVigenDesdeDia");
		String fechaVigenDesdeMes = ParamUtil.getString(request, "fechaVigenDesdeMes");
		String fechaVigenDesdeAnio = ParamUtil.getString(request, "fechaVigenDesdeAnio");
		String fechaVigenHastaDia = ParamUtil.getString(request, "fechaVigenHastaDia");
		String fechaVigenHastaMes = ParamUtil.getString(request, "fechaVigenHastaMes");
		String fechaVigenHastaAnio = ParamUtil.getString(request, "fechaVigenHastaAnio");
		Integer idMotivoBajaPlan = ParamUtil.getInteger(request, "motivoBajaPlan",0);
		
		String id_tercerizadora = ParamUtil.getString(request, "tercerizadora", ""); 

		Date fechaVigenDesde = null;
		Date fechaVigenHasta = null;
		try {
			fechaVigenDesde = formatoDeFecha.parse(fechaVigenDesdeDia + "/"
					+ (Integer.parseInt(fechaVigenDesdeMes) + 1) + "/" +fechaVigenDesdeAnio);
		} catch (Exception e) {
			fechaVigenDesde = null;
		}

		try {
			fechaVigenHasta = formatoDeFecha.parse(fechaVigenHastaDia + "/"
					+ (Integer.parseInt(fechaVigenHastaMes) + 1) + "/" +fechaVigenHastaAnio);
		} catch (Exception e) {
			fechaVigenHasta = null;
		}
		
		PreAfiliadoTotal preAfi = new PreAfiliadoTotal();
		
		preAfi.setId(id);
		
		/*datos beneficiario*/
		preAfi.setApellido(apellido);
		preAfi.setBarrio(barrio);
		preAfi.setCalle(calle);
		preAfi.setCelular(celular);
		preAfi.setCod_area_celular(cod_area_celular);
		preAfi.setCod_area_tel_laboral(cod_area_tel_laboral);
		preAfi.setCod_area_telefono(cod_area_telefono);
		preAfi.setCuil(cuil);
		preAfi.setCuil_titular(cuil_titular);
		preAfi.setDepto(dpto);
		preAfi.setDiscapacitado(discapacitado);
		preAfi.setDocumento_numero(docuNumero);
		preAfi.setDocumento_tipo(documentoTipo);
		preAfi.setDomi_tipo("0");
		preAfi.setEmail(email);
		preAfi.setId_estado_civil_sss(civilEsta);
		preAfi.setId_localidad(localidad);
		preAfi.setId_parentesco_sss(parentesco);
		preAfi.setId_provincia(provincia);
		preAfi.setId_seccional(idSeccional);
		if(inte != null && !StringUtils.checkEmpty(inte)){ // dejamos el null para que la BD analice que tiene que calcular...
			preAfi.setInte(Integer.valueOf(inte));
		}
		preAfi.setNaci_fecha(naciFecha);
		preAfi.setNacionalidad(nacionalidad);
		preAfi.setNombre(nombre);
		preAfi.setNumero(numero);
		preAfi.setObservaciones(observaciones);
		preAfi.setPiso(piso);
		preAfi.setPostal_codi(cod_postal);
		preAfi.setSexo(sexo);
		preAfi.setTel_laboral(tel_laboral);
		preAfi.setTelefono(telefono);
		preAfi.setVigen_fecha(vigenFecha);
		preAfi.setTipo_novedad(tipo_novedad);
		
		/*situ laboral*/
		preAfi.setCuit(cuit_empleador);
		preAfi.setId_categoria(categoria);
		preAfi.setFecha_ingre(ingreFecha);
		preAfi.setId_revista(situRevista);
		preAfi.setSucursal(sucu);
		preAfi.setEscala_salarial(escala);
		preAfi.setRazonSocial(razonSocial); 
		
		if(idPlan > 0){
			/* plan */	
			preAfi.setVigenDesde(fechaVigenDesde);
			preAfi.setVigenHasta(fechaVigenHasta);
			preAfi.setId_plan(idPlan);
			preAfi.setId_motivo_baja(idMotivoBajaPlan);
		
		/* tercerizadora */
			preAfi.setId_tercerizadora(id_tercerizadora);
			preAfi.setFecha_inicio_prestacion(fechaVigenDesde);
			preAfi.setFecha_fin_prestacion(fechaVigenHasta);
		}
		return preAfi;
	}	
	
	private void enviarNovedadaAfiliaciones(PreAfiliado preAfiliado){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.ALTA_PRE_CARGA);
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso carga de pre-afiliado", preAfiliado.getNovedadPreCarga(), destinatarios, 0);
		
	}
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{
		
		TraeListasServiceUtil.getCategoriasLaborales(renderRequest);
		
		TraeListasServiceUtil.getSituacionRevista(renderRequest);
		

	}
}