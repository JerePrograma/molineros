package ar.com.ospim.autorizaciones.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class EditarNomencladorAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
	private String CODIGO_MEDICAMENTOS = "400000";
	
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
//Tipos de Operaciones para la tabla nomenclador_bitacora
//A - Alta	
//M - Modificación sobre tabla nomenclador	
//P - Modificación sobre tabla nomenclador_plan
//R - Recupera nomenclador con fecha de baja
/////////////////////////////////////////////////////////	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		Nomenclador nomenclador=null;
		List<NomencladorPlan> listModalidad = null;
		List<NomencladorPlan> listTopesReintegros = null;
		long idNomenclador = 0;
		String msg = "";
		PrestacionConcepto prestacionConcepto=null;
		PrestacionConcepto prestacionConceptoOriginal=null;
		
        int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		
		if (!StringUtils.checkEmpty(cmd)) {
			idNomenclador = ParamUtil.getInteger(renderRequest,"id_nomenclador", 0);
			if(cmd.equals(Constants.WRITE) ){ // lo voy a usar como -NEW, para crear nuevas entradas en blanco
				
				nomenclador = new Nomenclador();
				prestacionConcepto = new PrestacionConcepto();
				
				Concepto cha = new Concepto(-1);
				Concepto chi = new Concepto(-1);
				Concepto cga = new Concepto(-1);
				Concepto cgi = new Concepto(-1);
				prestacionConcepto.setHonorariosAmbulatorio(cha);
				prestacionConcepto.setHonorariosInternacion(chi);
				prestacionConcepto.setGastosAmbulatorio(cga);
				prestacionConcepto.setGastosInternacion(cgi);
				
				prestacionConceptoOriginal= new PrestacionConcepto();
				Concepto chaO = new Concepto(-1);
				Concepto chiO = new Concepto(-1);
				Concepto cgaO = new Concepto(-1);
				Concepto cgiO = new Concepto(-1);
				prestacionConceptoOriginal.setHonorariosAmbulatorio(chaO);
				prestacionConceptoOriginal.setHonorariosInternacion(chiO);
				prestacionConceptoOriginal.setGastosAmbulatorio(cgaO);
				prestacionConceptoOriginal.setGastosInternacion(cgiO);
				
				session.removeAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);
				session.removeAttribute("ejercicio_desde_original");
				session.setAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION, nomenclador);
				session.setAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION, prestacionConcepto);
				session.setAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_ORIGINAL, prestacionConceptoOriginal);
				session.setAttribute("ejercicio_desde_original", DateUtils.getDesdeEjercicioActual().getTime());
				session.setAttribute("accion","edit");
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.editar_nomenclador"));
			}
			
            if(cmd.equals(Constants.EDIT) ){ 
            	session.removeAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);
            	session.removeAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION);
            	session.removeAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_ORIGINAL);
            	session.removeAttribute("ejercicio_desde_original");
            	
            	nomenclador = NomencladorServiceUtil.buscarNomencladorPorId((int)idNomenclador);
				
            	listModalidad=NomencladorServiceUtil.buscarNomencladorPlanPorId((int)idNomenclador);
            	
            	listTopesReintegros=NomencladorServiceUtil.buscarNomencladorPlanTopesReintegrosPorId((int)idNomenclador);
            	
            	String dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
        		String hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
        		prestacionConcepto = NomencladorServiceUtil.getPrestacionesConceptos((int)idNomenclador, format.parse(dd),format.parse(hta));
            	
        		prestacionConceptoOriginal = NomencladorServiceUtil.getPrestacionesConceptos((int)idNomenclador, format.parse(dd),format.parse(hta));
        		
				session.setAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION, nomenclador);
				session.setAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION, listModalidad);
				session.setAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION, prestacionConcepto);
				session.setAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_ORIGINAL, prestacionConceptoOriginal);
				session.setAttribute("ejercicio_desde_original", prestacionConcepto.getValidoDesdeGastosAmbulatorio());
				session.setAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS, listTopesReintegros);
				
				String accion = ParamUtil.getString(renderRequest, "accion", "edit");
				if("view".equalsIgnoreCase(accion)){
					session.setAttribute("accion","view");
				}else{
					session.setAttribute("accion","edit");
				}
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id nomenclador: " + idNomenclador
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.editar_nomenclador"));		
			}
			
            if (cmd.equals(Constants.DELETE)) { // borrado logico
            	
            	NomencladorServiceUtil.eliminaNomenclador((int)idNomenclador, user.getScreenName());
            	
            	List<Nomenclador>ln= (List<Nomenclador>) session.getAttribute("Nomenclador");
           	    for(Nomenclador n:ln){
            		if(n.getId_prestacion()==idNomenclador){
            		   n.setBaja_fecha(new Date());	
            		}
            	}
            	session.setAttribute("Nomenclador",ln);
            	
				msg = LanguageUtil.get(defaultLocale, "delete-nomenclador");
				msg = msg + idNomenclador;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id corr: " + idNomenclador
						);
				
				return mapping.findForward("portlet.autorizaciones.buscar_nomenclador");
			
			}
            
            if (cmd.equals(Constants.RESTORE)) { //Recupera Nomenclador Eliminado
            	
            	NomencladorServiceUtil.recuperaNomenclador((int)idNomenclador, user.getScreenName());
            	
            	List<Nomenclador>ln= (List<Nomenclador>) session.getAttribute("Nomenclador");
           	    for(Nomenclador n:ln){
            		if(n.getId_prestacion()==idNomenclador){
            		   n.setBaja_fecha(null);	
            		}
            	}
            	session.setAttribute("Nomenclador",ln);
            	
				msg = LanguageUtil.get(defaultLocale, "restore-nomenclador");
				msg = msg + idNomenclador;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id corr: " + idNomenclador
						);
				
				return mapping.findForward("portlet.autorizaciones.buscar_nomenclador");
			
			}
            
// Topes Reintegros            
            if (cmd.equals("topeAdd")) {
                String fechaDesdeDia = ParamUtil.getString(renderRequest,"diadde");
        		String fechaDesdeMes = ParamUtil.getString(renderRequest,"mesdde");
        		String fechaDesdeAnio = ParamUtil.getString(renderRequest,"aniodde");
        		Date fechaDesde = null;
        		try {
        			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
        					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
        					+ fechaDesdeAnio);
        		} catch (Exception e) {
        			fechaDesde = null;
        		}
        		
        		
        		String fechaHastaDia = ParamUtil.getString(renderRequest,"diahta");
        		String fechaHastaMes = ParamUtil.getString(renderRequest,"meshta");
        		String fechaHastaAnio = ParamUtil.getString(renderRequest,"aniohta");
        		Date fechaHasta = null;
        		try {
        			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
        					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
        					+ fechaHastaAnio);
        		} catch (Exception e) {
        			fechaHasta = null;
        		}
        		
        		Long idRenglon = ParamUtil.getLong(renderRequest,"idren");
        		
            	List<NomencladorPlan> lista = (List<NomencladorPlan>) session
        				.getAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS);

        		if (lista == null) {
        			lista = new ArrayList<NomencladorPlan>();
        		}
        		
        		
        		
        		String plan="";
        		
        		plan=ParamUtil.getString(renderRequest,"plan");
        		
        		Double tope=0D;
        		tope=ParamUtil.getDouble(renderRequest,"importe");
        		
        		int id = 0;
        		id= (int)Math.floor((Math.random()*100)); //Asigna id ficticio, hay que reemplazarlo por el real cuando se defina
        		
        		Boolean isNew = true;
        		for(NomencladorPlan n:lista){
        			if(idRenglon==0L) {
        			
        			  if(n.getPlan().getId()==Integer.parseInt(plan) && n.getVigencia_desde().equals(fechaDesde) &&
        				   	((fechaHasta==null &&  n.getVigencia_hasta()==null) ||
        				   	 (fechaHasta!=null &&  n.getVigencia_hasta()!=null && n.getVigencia_hasta().equals(fechaHasta)) 
        				   	)
        				){
        		          isNew= false;
        		          break;
        			  }
        			}else {
        				if(n.getId()==idRenglon) {
        					n.setVigencia_desde(fechaDesde);
        					n.setVigencia_hasta(fechaHasta);
        	        		n.setTopeReintegro(tope);
        	        		Plan p = PlanServiceUtil.getInstance().buscaPlanPorId(Integer.parseInt(plan));
        	        		n.setPlan(p);
        	        		isNew=false;
        	        		break;
        				}
        			}
        		}
        		
        		if(isNew){
        		   NomencladorPlan np = new NomencladorPlan();
        		   np.setId(id*-1);
        		   Plan p = PlanServiceUtil.getInstance().buscaPlanPorId(Integer.parseInt(plan));
        		   np.setPlan(p);
        		   np.setVigencia_desde(fechaDesde);
        		   np.setVigencia_hasta(fechaHasta);
        		   np.setTopeReintegro(tope);
        		   lista.add(np);
        		}
        		renderRequest.setAttribute("esEdicion", "true");
        		session.setAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS, lista);
        		
        		return mapping.findForward("portlet.autorizaciones.nomenclador.nomencladorplan_topesreintegros.search.result");
            }
            
            if (cmd.equals("topeDelete")) {
                Long idRenglon = ParamUtil.getLong(renderRequest,"idren");
        		List<NomencladorPlan> lista = (List<NomencladorPlan>) session
        				.getAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS);
        		List<NomencladorPlan> listaNew = new ArrayList<NomencladorPlan>();
            	
        		for(NomencladorPlan n:lista){
        			if(n.getId()!=idRenglon) {
        				listaNew.add(n);
        			}
        		}
            	renderRequest.setAttribute("esEdicion", "true");
        		session.setAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS, listaNew);
        		return mapping.findForward("portlet.autorizaciones.nomenclador.nomencladorplan_topesreintegros.search.result");
            }
//Fin Topes Reintegros            
            
            //Recupera Datos cargados en la jsp
			nomenclador = (Nomenclador) session.getAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION);
			prestacionConcepto = (PrestacionConcepto) session.getAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION);
			listModalidad= (List<NomencladorPlan>) session.getAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);
			listTopesReintegros=(List<NomencladorPlan>) session.getAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS);
			
			int marcaReintegroLiq =0;
			Double coeficienteHonorarios=0D;
			Double coeficienteGastos=0D;
            if (cmd.equals(WebKeysGlobal.CAMBIO_SOLAPA)) {
            	if(null!= renderRequest.getParameter("tabs1") &&renderRequest.getParameter("tabs1").equals("datos")){
            	   actualizaNomencladorContable(nomenclador,prestacionConcepto,renderRequest);	
            	}
            	
            	if(null!= renderRequest.getParameter("tabs1") &&renderRequest.getParameter("tabs1").equals("datos-contables")){
            		actualizaNomenclador(nomenclador,prestacionConcepto,renderRequest);
            	}
            	session.setAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION, nomenclador);
            	session.setAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION , prestacionConcepto);
            	
            	return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.editar_nomenclador"));
            }
            
                        
            actualizaNomencladorContable(nomenclador,prestacionConcepto,renderRequest);
            
			if(cmd.equals(Constants.UPDATE) ){
				if(idNomenclador == 0){// primera vez, antes del insert...
					
				   if(!NomencladorServiceUtil.existeNomencladorPorTipoCodigo(nomenclador.getId_tipo_nomenclador(), nomenclador.getCodigo())){		
					
					  idNomenclador = insertNomenclador(nomenclador, user.getScreenName(),listModalidad,prestacionConcepto,listTopesReintegros);
					  nomenclador.setId_prestacion((int) idNomenclador );
					
					  msg = LanguageUtil.get(defaultLocale, "insert-nomenclador");
					  msg = msg + idNomenclador;
					  SessionMessages.add(renderRequest, "insertCabOk");
					  renderRequest.setAttribute("msgCabOk", msg);
					  _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id corr: " + idNomenclador
							);
				   }else{
					    msg = LanguageUtil.get(defaultLocale, "Ya existe un nomenclador con este tipo y codigo");
					    SessionErrors.add(renderRequest, "avisoNomencladorDuplicado");
						renderRequest.setAttribute("msgInsertError",msg );
						_log.debug("Usuario: " + user.getScreenName() 
								+ " cmd: " + cmd 
								+ " id corr: " + idNomenclador
								);   
				   }
					
				}else if(idNomenclador!=0){
					Date ejercicioOriginal = (Date) session.getAttribute("ejercicio_desde_original");
					
					updateNomenclador(nomenclador, user.getScreenName(),listModalidad,prestacionConcepto,ejercicioOriginal,listTopesReintegros);
					
					String dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
	        		String hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
					prestacionConcepto = NomencladorServiceUtil.getPrestacionesConceptos((int)idNomenclador, format.parse(dd),format.parse(hta));
					session.setAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION , prestacionConcepto);
					
					msg = LanguageUtil.get(defaultLocale, "update-nomenclador");
					msg = msg + idNomenclador;
					SessionMessages.add(renderRequest, "updateCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
					_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id corr: " + idNomenclador
							);
				}
			}
	
		}
		
		session.setAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION, nomenclador);
		renderRequest.setAttribute("tabs1", "datos");
		return mapping.findForward("portlet.autorizaciones.editar_nomenclador");
	}
	
	
	private long insertNomenclador(Nomenclador nomenclador, String user,List<NomencladorPlan>listModalidad,PrestacionConcepto prestacionConcepto,List<NomencladorPlan>listTopes) throws Exception{
		long id = 0;
		
		id = NomencladorServiceUtil.insertaNomenclador(nomenclador, user,listModalidad,prestacionConcepto,listTopes);
		return id;
	}
	
	private long updateNomenclador(Nomenclador nomenclador, String user,List<NomencladorPlan>listModalidad,PrestacionConcepto prestacionConcepto,Date ejercicioOriginal,List<NomencladorPlan>listTopes) throws Exception{
		long id = 0;
		
		id = NomencladorServiceUtil.updateNomenclador(nomenclador, user,listModalidad,prestacionConcepto,ejercicioOriginal,listTopes);
		return id;
	}
	
	private void actualizaNomenclador(Nomenclador nomenclador,PrestacionConcepto prestacionConcepto,RenderRequest renderRequest){
		String codigoNomenclador = ParamUtil.getString(renderRequest, "codigoNomenclador", null);
		int tipoNomenclador = ParamUtil.getInteger(renderRequest,"tipoNomenclador", 0);
		String descripcionNomenclador = ParamUtil.getString(renderRequest, "descripcionNomenclador", null);
		int especialidad = ParamUtil.getInteger(renderRequest,"especialidad", 0);
		Boolean recuperaSur = ParamUtil.getBoolean(renderRequest, "recuperaSUR",false);
		String resolucionNomenclador = ParamUtil.getString(renderRequest, "resolucionNomenclador", null);
		Double importe = ParamUtil.getDouble(renderRequest, "importe_nomenclador", 0D);
		Double cantidadGaleno = ParamUtil.getDouble(renderRequest, "cantidad_galeno", 0D);
		Double cantidadGalenoAyudante = ParamUtil.getDouble(renderRequest, "cantidad_galeno_ayudante", 0D);
		Double cantidadGalenoAnestesista = ParamUtil.getDouble(renderRequest, "cantidad_galeno_anestesista", 0D);
		Double valorGaleno = ParamUtil.getDouble(renderRequest, "valor_galeno", 0D);
		String codigoHospital = ParamUtil.getString(renderRequest, "codigoHospital", null);
		Double cantidadAyudantes = ParamUtil.getDouble(renderRequest, "cantidad_ayudantes", 0D);
		Double valorGalenoGastos = ParamUtil.getDouble(renderRequest, "valor_galeno_gastos", 0D);
		Double cantidadGalenoGastos = ParamUtil.getDouble(renderRequest, "cantidad_galeno_gastos", 0D);
		String descripcionNomencladorMedicamento = ParamUtil.getString(renderRequest,"nombre_medicamento",null);
		Boolean requiereAutorizacion = ParamUtil.getBoolean(renderRequest, "requiereAutorizacion",false);
		String observaciones = ParamUtil.getString(renderRequest, "observacionesNomenclador", null);
		Boolean supra = ParamUtil.getBoolean(renderRequest, "supra",false);
		Boolean cirugia = ParamUtil.getBoolean(renderRequest, "cirugia",false);
		Boolean enviarWSTerce = ParamUtil.getBoolean(renderRequest, "enviarWSTercerizadora",false);
		
		int troquel = ParamUtil.getInteger(renderRequest,"troquel", 0);
		
		nomenclador.setId_tipo_nomenclador(tipoNomenclador);
		nomenclador.setCodigo(codigoNomenclador);
		if(tipoNomenclador==9 && descripcionNomencladorMedicamento!=null){
		   nomenclador.setDescripcion(descripcionNomencladorMedicamento);
		   nomenclador.setTroquelMedicamento(troquel);
		}else{
		   nomenclador.setDescripcion(descripcionNomenclador);
		   nomenclador.setTroquelMedicamento(0);
		}
		
		if(tipoNomenclador==9 && nomenclador.getMarcaReintegroLiquidacion()==0){
			nomenclador.setMarcaReintegroLiquidacion(3);
		}
		
		if(tipoNomenclador==9 && nomenclador.getCoeficienteGastos() ==null){
			nomenclador.setCoeficienteGastos(1D);
		}
		
		if(tipoNomenclador==9 && nomenclador.getCoeficienteHonorarios() ==null){
			nomenclador.setCoeficienteHonorarios(0D);
		}
		if(tipoNomenclador==9){
			int[] conceptoMedicantos= setearConceptosMedicamentos(renderRequest);
			if(conceptoMedicantos[0]!=0){
				if(prestacionConcepto.getHonorariosAmbulatorio()==null || prestacionConcepto.getHonorariosAmbulatorio().getId()==-1){	
		            Concepto cha = new Concepto(conceptoMedicantos[0]);
		            prestacionConcepto.setHonorariosAmbulatorio(cha);
				}    
				if(prestacionConcepto.getGastosAmbulatorio()==null || prestacionConcepto.getGastosAmbulatorio().getId()==-1){
		            Concepto cga = new Concepto(conceptoMedicantos[0]);
		            prestacionConcepto.setGastosAmbulatorio(cga);
				}    
			}
			if(conceptoMedicantos[1]!=0){
				if(prestacionConcepto.getHonorariosInternacion()==null || prestacionConcepto.getHonorariosInternacion().getId()==-1){	
			       Concepto chi = new Concepto(conceptoMedicantos[1]);
			       prestacionConcepto.setHonorariosInternacion(chi);
				}
				if(prestacionConcepto.getGastosInternacion()==null || prestacionConcepto.getGastosInternacion().getId()==-1){	
			       Concepto cgi = new Concepto(conceptoMedicantos[1]);
			       prestacionConcepto.setGastosInternacion(cgi);
				}   
			}
		}
		
		nomenclador.setId_especialidad(especialidad);
		nomenclador.setRecuperaSUR(recuperaSur);
		nomenclador.setResolucion(resolucionNomenclador);
		nomenclador.setImporte(importe);
		nomenclador.setCantidadGaleno(cantidadGaleno);
		nomenclador.setCantidadGalenoAnestesista(cantidadGalenoAnestesista);
		nomenclador.setCantidadGalenoAyudante(cantidadGalenoAyudante);
		nomenclador.setValorGaleno(valorGaleno);
		nomenclador.setCodigoHospital(codigoHospital);
		nomenclador.setCantidadAyudantes(cantidadAyudantes);
		nomenclador.setValorGalenoGastos(valorGalenoGastos);
		nomenclador.setCantidadGalenoGastos(cantidadGalenoGastos);
		nomenclador.setRequiereAutorizacion(requiereAutorizacion);
		nomenclador.setObservaciones(observaciones);
		nomenclador.setSupra(supra);
		nomenclador.setCirugia(cirugia);
		nomenclador.setEnviarWSTercerizadora(enviarWSTerce);
		
	}
	
	private void actualizaNomencladorContable(Nomenclador nomenclador,PrestacionConcepto prestacionConcepto,RenderRequest renderRequest){
	   int marcaReintegroLiq =0;
	   Double coeficienteHonorarios=0D;
	   Double coeficienteGastos=0D;
	   String ejercicio="";
	   int idHonorariosAmbulatorio =0;
	   int idHonorariosInternacion=0;
	   int idGastosAmbulatorio=0;
	   int idGastosInternacion=0;
	   
	   SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	   marcaReintegroLiq  = ParamUtil.getInteger(renderRequest,"marcaReintegroLiq", 0);
	   coeficienteHonorarios=ParamUtil.getDouble(renderRequest,"coeficiente_honorarios");
	   coeficienteGastos=ParamUtil.getDouble(renderRequest,"coeficiente_gastos");
	   ejercicio=ParamUtil.getString(renderRequest, "ejercicio","");
	   idHonorariosAmbulatorio  = ParamUtil.getInteger(renderRequest,"honorarios_ambulatorio", -1);
	   idHonorariosInternacion  = ParamUtil.getInteger(renderRequest,"honorarios_internacion", -1);
	   idGastosAmbulatorio  = ParamUtil.getInteger(renderRequest,"gastos_ambulatorio", -1);
	   idGastosInternacion  = ParamUtil.getInteger(renderRequest,"gastos_internacion", -1);
	   
	   String[] ejercicios =ejercicio.split("-"); 
	   try {
		   Date fDde = format.parse("01-08-"+ejercicios[0]);
		   Date fHta = DateUtils.getInfinito().getTime();    //format.parse("31-07-"+ejercicios[1]);
		   prestacionConcepto.setValidoDesdeHonorariosAmbulatorio(fDde);
		   prestacionConcepto.setValidoDesdeGastosAmbulatorio(fDde);
		   prestacionConcepto.setValidoDesdeGastosInternacion(fDde);
		   prestacionConcepto.setValidoDesdeHonorariosInternacion(fDde);
		   
		   prestacionConcepto.setValidoHastaHonorariosAmbulatorio(fHta);
		   prestacionConcepto.setValidoHastaGastosAmbulatorio(fHta);
		   prestacionConcepto.setValidoHastaGastosInternacion(fHta);
		   prestacionConcepto.setValidoHastaHonorariosInternacion(fHta);
		   
	   } catch (ParseException e) {}
	   
	   Concepto cha = new Concepto(idHonorariosAmbulatorio);
	   prestacionConcepto.setHonorariosAmbulatorio(cha);
	   
	   Concepto chi = new Concepto(idHonorariosInternacion);
	   prestacionConcepto.setHonorariosInternacion(chi);
	   
	   Concepto cga = new Concepto(idGastosAmbulatorio);
	   prestacionConcepto.setGastosAmbulatorio(cga);
	   
	   Concepto cgi = new Concepto(idGastosInternacion);
	   prestacionConcepto.setGastosInternacion(cgi);
	   
	   nomenclador.setMarcaReintegroLiquidacion(marcaReintegroLiq);
	   nomenclador.setCoeficienteGastos(coeficienteGastos);
	   nomenclador.setCoeficienteHonorarios(coeficienteHonorarios);
	}
	
	private int[] setearConceptosMedicamentos(RenderRequest req) {
		int cods[]={0,0};
		List<PrestacionConcepto> prestacionesConceptos = ConceptoServiceUtil
				.getPrestacionesConceptos(DateUtils.getDesdeEjercicioActual(),
						DateUtils.getHastaEjercicioActual());
		for (PrestacionConcepto pc : prestacionesConceptos) {
			if (pc.getPrestacion().getCodigo().equals(CODIGO_MEDICAMENTOS)) {
				cods[0]= pc.getGastosAmbulatorio().getId();
				cods[1]= pc.getGastosInternacion().getId();
			}
		}
		return cods;
	}
}
