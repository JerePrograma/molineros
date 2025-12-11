package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import ar.com.ospim.afiliados.AfliadoYaTieneConyugeException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.action.ActionUtil;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.AfiliadoInteComparator;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.beans.TipoAporte;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.tercerizadora.services.TercerizadoraFactory;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * 
 * @author Carlos Rivas
 * 
 * @edit SVA 15/05/2014
 * 
 */
public class ReincorporarServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(ReincorporarServiceUtil.class);
	
	private static ReincorporarServiceImpl instance = null;
	
	private static PlanServiceUtil planService = new PlanServiceUtil() ;
	
	public static ReincorporarServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReincorporarServiceImpl();
		}
		return instance;
	}

	public static ArrayList<String> reincorporarGrupofamiliarYGuardarDatos(
			List<Afiliado> afiliados, Date vigen_fecha, User usuario,
			/*boolean recuperarPlanes,*/ Afiliado afiliadoInSession,
			List<SituacionLaboral> situLaborales,
			List<SituacionLaboral> situLaboralesAdd,
			List<SituacionLaboral> situLaboralesUp,/*
			AfiAporteList afiAporteList, int idPlan, int id_plan_omint,
			List<TipoAporte> listaTiposAporte, boolean esCambioPlan, */boolean esRecuperarUltimoPlan, boolean esBajaFutura, /*
			AfiAporteList aportesNuevos,*/List<AfiTercerizadoraServicio> tercerizadoras/*,
			List<AportesYEgreso> aportesValidosParaFechaVigenciaOrigina*/,int continuidad, AfiPlan afiPlanNuevo, AfiPlan afiPlanActual)
			throws Exception {

		Afiliado afiliadoEnBase = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(
						afiliadoInSession.getCuil_titular(), 0);

		Connection connection = ConnectionHelper.getConnectionForTransaction();
		ArrayList<String> mensajes = new ArrayList<String>();
		
		try {
			_log.debug("Reincorporando grupo familiar");
			Date bajaFecha = null;
			int idMotBaja = 0;
			
			Date vigenFechaOriginal = afiliadoEnBase.getVigen_fecha();
			_log.debug("ingreso titular: " + vigenFechaOriginal);
			AfiliadoInteComparator comparator = new AfiliadoInteComparator();
			Collections.sort(afiliados, comparator);
			int idPlan = 0;
			
			for (Afiliado afi : afiliados) {
				int id_motivo_baja_menor_edad = -1;
				bajaFecha = null;
				if (afi != null && afi.getInte() != 0) {
					
//					Analizamos parentesco para evaluar si corresponde calcular fecha de baja futura
					if(afi.getParentesco() != null
						&& (afi.getId_parentesco() == WebKeysAfiliados.HIJO_MENOR 
							|| afi.getId_parentesco() == WebKeysAfiliados.HIJO_MENOR_CONYUGE) 
							|| afi.getId_parentesco() == WebKeysAfiliados.MENOR_BAJO_GUARDA) {
					
						bajaFecha = DateUtils.addYears(afi.getNaci_fecha(),WebKeysGlobal.ANIOS_MAYOR_EDAD);
						
						id_motivo_baja_menor_edad = 4;  //"MAYOR DE 21 AÑOS NO ESTUDIANTE    "
						
					}
					if(afi.getParentesco() != null
							//"Hijo del conyuge soltero de 21 a 25 años cursando estudios regulares", //4
							&& (afi.getId_parentesco() == WebKeysAfiliados.HIJO_MAYOR_CONYUGE 
							// "Hijo soltero de 21 a 25 años cursando estudios regulares", //6	
								|| afi.getId_parentesco() == WebKeysAfiliados.HIJO_MAYOR
							//	"Mayor de 25 años discapacitado" //8 
								|| afi.getId_parentesco() == WebKeysAfiliados.MAYOR_DE_25_AÑOS_DISCAPACITADO )){
						
						bajaFecha =	getInstance().calculaFechaBajaFuturaIntegrante(connection, afiliadoInSession.getCuil_titular(), afi.getInte());
						
						id_motivo_baja_menor_edad = WebKeysAfiliados.HIJO_MAYOR;  //"MAYOR DE 21 AÑOS NO ESTUDIANTE    "
						
						if(afi.getDiscapacitado().equals("1")){ // es discapacitado
							id_motivo_baja_menor_edad = WebKeysAfiliados.CERTIFICADO_POR_INCAPACIDAD; //"VTO. CERTIF. DISCAP./INCAP.       "
						}
					}
					
					if (bajaFecha != null && bajaFecha.before(new Date())) {
						continue;
					}
				}
				
				int intePareja=EditarAfiliadoServiceUtil.getInstance().getTieneConyugeGrupoCuil(afi.getCuil_titular(), afi.getVigen_fecha(),null);
				if ((afi.getId_parentesco() == WebKeysAfiliados.CONCUBINO_DEFAULT
						|| afi.getId_parentesco() == WebKeysAfiliados.CONYUGE_DEFAULT)
						&& intePareja!=0 
						&& afi.getInte()!=0 && intePareja!=afi.getInte()) {		
					throw new AfliadoYaTieneConyugeException();
				}
				
				if (_log.isDebugEnabled()) {
					_log.debug("reincorporando afi cuil_titular "
							+ afi.getCuil_titular() + " inte " + afi.getInte());
				}
				
				/*seteamos aca la fecha de baja porque luego pasa por el actualizar afiliado y segun reglas para inte 0*/
				if(afi.getInte() == 0) {
					
					afi.setBaja_fecha(bajaFecha);
					afi.setId_motivo_baja(idMotBaja);
					
				}
				
				reincorporarAfiliado(afi, vigen_fecha, bajaFecha, continuidad,
						vigenFechaOriginal, usuario.getScreenName(), id_motivo_baja_menor_edad,
						/*recuperarPlanes,*/ connection);
				if (afiliados.size() > 0) {
					actualizaNumAfiliadosGrupo(afi.getCuil_titular(),
							afi.getInte(), connection);
				}
				
			} // fin for (Afiliado afi : afiliados)

			EditarAfiliadoServiceUtil.actualizaAfiliadoEntry(afiliadoInSession, null, null, usuario.getScreenName(),null, null, connection);

			if (situLaboralesUp.size() > 0 || situLaboralesAdd.size() > 0) {
				AfiliadoServiceUtil.guardarSitusLaborales(situLaborales,
						usuario.getScreenName(), connection);
			}

			if(esBajaFutura){
				
//				Si es baja futura y el plan propagado es 3;"COBERTURA" o 19;"COBERTURA - USUFRUCTO" y la vigencia de este planActual y 
//				el plan qu se inicia por la nueva situ laboral son iguales, quiere decir que el plan propagado no llego a ser utilizado como cobertura,
//				y permitiremos la baja fisica del registro p poder hacer el cambio.
				AfiPlan afiPlanVigenteReal = planService.getInstance().buscarUltimoPlanAportes(afiliadoInSession.getCuil_titular());
				if(afiPlanVigenteReal.getVigenDesde().equals(afiPlanNuevo.getVigenDesde()) 
					&& (afiPlanVigenteReal.getPlan().getId() == 3 
						|| afiPlanVigenteReal.getPlan().getId() == 19)	){
					
					planService.borrarPlanDelAfiliado(connection, afiPlanVigenteReal.getId().longValue(), usuario.getScreenName());
				}
				
				planService.cambioDePlanyAportes(connection, afiPlanActual, afiPlanNuevo, false, usuario.getScreenName());
				
			}else if(esRecuperarUltimoPlan && continuidad == 1){ 
				Calendar fechaFinPresOriginal = Calendar.getInstance();
				
//			    me encargo de averiguar cual es, ni hacia falta tomarlo por la pantalla
//				luego con solo voletearle la vigen_hasta y motivo de baja, logro levantar la baja del ultimo plan y aportes...
					AfiPlan afiPlan = planService.buscarUltimoPlanAportes(afiliadoInSession.getCuil_titular());
					afiPlan.setVigenHasta(null);
					afiPlan.setMotivoBaja(null);
					// le actualizamos la fecha de vigencia, si reincorpora con continuidad sera la misma, sino sera la nueva fecha...
					afiPlan.setVigenDesde(vigen_fecha); 
					planService.getInstance().actualizaPlanyAportes(connection, afiPlan, null, usuario.getScreenName());
					
//					tambien voleteamos baja fecha y motivo baja a la ultima tercerizadora
//					aca ajustamos el cambio de Omint a Prevencion
//					TercerizadoraServiceUtil.getInstance().actualizaBajaUltimaTercerizadora(connection, afiliadoInSession.getCuil_titular(), null, usuario.getScreenName());
//					tambien voleteamos baja fecha y motivo baja a la ultima tercerizadora
					List<AfiTercerizadoraServicio> tercerizadoras_recup = new ArrayList<AfiTercerizadoraServicio>();
					AfiTercerizadoraServicio ats = TercerizadoraServiceUtil.getInstance().buscarUltimaTercerizadoraDelAfiliado(connection, afiliadoInSession.getCuil_titular());
					AfiTercerizadoraServicio atsSandwich = null; 
					//cuidamos no perder la fecha de fin prest (es decir la baja anterior)
					fechaFinPresOriginal.setTime(ats.getFechaFinPres());
					
					ats.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					ats.setAfiliado(afiliadoInSession);
					ats.setFechaFinPres(null);
					
					//Si tiene una sola tercerizadora, Se entiende que la fecha incio tiene que ser igual a la vigen fecha
					if (TercerizadoraServiceUtil.getInstance().historicoTercerizadoraDelAfiliado(afiliadoInSession.getCuil_titular()).size() ==1){
						ats.setFechaInicioPres(vigen_fecha);						
					}
					
					tercerizadoras_recup.add(ats);
					
//					if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CSA")){
//						Calendar corteInicio = Calendar.getInstance();
//						corteInicio.set(2015, Calendar.SEPTEMBER, 01); // 1/09/2015
//						AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MPS");
//						atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//						atsAjuste.setAfiliado(afiliadoInSession);
//						atsAjuste.setFechaFinPres(null);
//						atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha);
//						tercerizadoras_recup.add(atsAjuste);
//						
//						corteInicio.add(Calendar.DATE, -1); //corte FIN!!
//						ats.setFechaFinPres(corteInicio.getTime());
//						
//						//grabamos los ajustes de tercerizadoras
//						mensajes = AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession, 
//								usuario,tercerizadoras_recup, afiPlanActual, afiPlan, connection);
					if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MPS")){
						
						Calendar corteInicio = Calendar.getInstance();
						corteInicio.set(2019, Calendar.DECEMBER, 01); // 1/12/2019
						AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MEN");
						atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						atsAjuste.setAfiliado(afiliadoInSession);
						
						AfiTercerizadoraServicio atsAjusteMONO=null;
						AfiTercerizadoraServicio atsAjusteMONO_1=null;
						if(afiPlan.getPlan().getId()==4) {
							atsAjuste.setFechaInicioPres(corteInicio.getTime());
							Calendar corteFinMEN = Calendar.getInstance();
							corteFinMEN.set(2022, Calendar.SEPTEMBER, 30); // 1/12/2019
							
							Calendar corteFinMIM = Calendar.getInstance();
							corteFinMIM.set(2024, Calendar.DECEMBER, 31); // 1/12/2019
							
							atsAjuste.setFechaFinPres(corteFinMEN.getTime());
							atsAjusteMONO=new AfiTercerizadoraServicio("MIM");
							atsAjusteMONO.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjusteMONO.setAfiliado(afiliadoInSession);
							corteFinMEN.add(Calendar.DATE, 1);
							atsAjusteMONO.setFechaInicioPres(vigen_fecha.before(corteFinMEN.getTime())?corteFinMEN.getTime():vigen_fecha);
							atsAjusteMONO.setFechaFinPres(corteFinMIM.getTime());
							
							
							atsAjusteMONO_1=new AfiTercerizadoraServicio("MON");
							atsAjusteMONO_1.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjusteMONO_1.setAfiliado(afiliadoInSession);
							corteFinMIM.add(Calendar.DATE, 1);
							atsAjusteMONO_1.setFechaInicioPres(vigen_fecha.before(corteFinMIM.getTime())?corteFinMIM.getTime():vigen_fecha);
							atsAjusteMONO_1.setFechaFinPres(null);
							
						}else {
						  atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha);	
						  atsAjuste.setFechaFinPres(null);
						}  
						
						
						tercerizadoras_recup.add(atsAjuste);
						if(atsAjusteMONO!=null) {
						   tercerizadoras_recup.add(atsAjusteMONO);
						}
						
						if(atsAjusteMONO_1!=null) {
							   tercerizadoras_recup.add(atsAjusteMONO_1);
						}
						
						corteInicio.add(Calendar.DATE, -1); //corte FIN!!
						ats.setFechaFinPres(corteInicio.getTime());
						ats.setEstado(AfiTercerizadoraServicio.ESTADOS.MODIFICADO);
						
						//grabamos los ajustes de tercerizadoras
						mensajes = AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession, 
								usuario,tercerizadoras_recup, afiPlanActual, afiPlan, connection);	
					}else if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("OMI") || //Omint
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CEU") || //Consolidar
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("PRS") || //Prevencion
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CEM")  || //CEMIC
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CCH")  ||//Chivilcoy
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("GAL")  ||//Galeno
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("ETR")  ||//En tramite
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("HAL")  ||//Hospital Aleman
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MQV")  ||//Capitas en tramite
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("OED")  //ORGANISMOS ESTATALES DDJJ
							){ 
//						en este caso hacemos un sandwitch, mantenemos un periodo con la tercerizadora recuperada
//						hasta la fecha fin que tenía,
//						luego metemos una terceriz En Trámite hasta el corte de sept 2016, 
//						y luego la tercerizadora recuperada nuevamente
						
						Calendar corteInicio = Calendar.getInstance();
						Calendar corteInicioAux = Calendar.getInstance();
//						corteInicio.set(2016, Calendar.SEPTEMBER, 01); // 1/09/2016
//						corteInicio.set(2017, Calendar.SEPTEMBER, 01); // 1/09/2017 solicitado x Sandra 07/11/2017
						corteInicio.set(2018, Calendar.DECEMBER, 01); //  1/12/2018 solicitado x Sandra 10/01/2019
						
						if(fechaFinPresOriginal.getTime().compareTo(ar.com.ospim.util.DateUtils.getMismoDia_00_00hs(corteInicio.getTime()))< 0){
							//1° el pan de abajo (dejamos la tercerizadora como estaba)
//							ats.setFechaFinPres(fechaFinPresOriginal.getTime());
							tercerizadoras_recup.remove(ats);
							
							//3° el pan de arriba (la que queda vigente)
							AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio(ats.getTercerizadora().getId_tercerizadora());
							atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjuste.setAfiliado(afiliadoInSession);
							atsAjuste.setFechaFinPres(null);
							atsAjuste.setFechaInicioPres(corteInicio.getTime());
							tercerizadoras_recup.add(atsAjuste);
							
							 if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("OMI") || //Omint
										ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CEU") //Consolidar
							 ) {			
							
							   //2° el jamon y queso (relleno)
							   corteInicioAux.setTime(fechaFinPresOriginal.getTime());
							   corteInicioAux.add(Calendar.DATE, 1);
							   corteInicio.add(Calendar.DATE, -1); //corte FIN!!
							   atsSandwich = new AfiTercerizadoraServicio("ETR", "En Trámite", corteInicioAux.getTime(), corteInicio.getTime());
							   atsSandwich.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							   tercerizadoras_recup.add(atsSandwich);
							 }
							//grabamos los ajustes de tercerizadoras
							mensajes =  AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession,  
									usuario,tercerizadoras_recup, afiPlanActual, afiPlan, connection);
						}else { // los demas casos para OMI y CEU
								
								TercerizadoraServiceUtil.getInstance().actualizaBajaUltimaTercerizadora(connection, afiliadoInSession.getCuil_titular(), ats.getId(),  null, usuario.getScreenName());	
						}
//						if(vigen_fecha.compareTo(ar.com.ospim.util.DateUtils.getMismoDia_00_00hs(corteInicio.getTime()))< 0){
//
//							ats.setFechaInicioPres(corteInicio.getTime());
//							
//							corteInicio.add(Calendar.DATE, -1); //corte FIN!!
//							AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("ETR");
//							atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//							atsAjuste.setAfiliado(afiliadoInSession);
//							atsAjuste.setFechaFinPres(corteInicio.getTime());
//							atsAjuste.setFechaInicioPres(vigen_fecha);
//							tercerizadoras_recup.add(atsAjuste);
//		
//						}
					}else if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MIM")){ // REINCORPORACION MONOTRIBUTISTA
						Calendar corteFinMIM = Calendar.getInstance();
						corteFinMIM.set(2024, Calendar.DECEMBER, 31); 
					    ats.setFechaFinPres(corteFinMIM.getTime());
					    
					    AfiTercerizadoraServicio atsAjusteMONO_1=null;
					    atsAjusteMONO_1=new AfiTercerizadoraServicio("MON");
						atsAjusteMONO_1.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						atsAjusteMONO_1.setAfiliado(afiliadoInSession);
						corteFinMIM.add(Calendar.DATE, 1);
						atsAjusteMONO_1.setFechaInicioPres(vigen_fecha.before(corteFinMIM.getTime())?corteFinMIM.getTime():vigen_fecha);
						atsAjusteMONO_1.setFechaFinPres(null);
						tercerizadoras_recup.add(atsAjusteMONO_1); 
						
						//grabamos los ajustes de tercerizadoras
						mensajes =  AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession,  
								usuario,tercerizadoras_recup, afiPlanActual, afiPlan, connection);
					
			        }else { // los demas casos 
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						
						TercerizadoraServiceUtil.getInstance().actualizaBajaUltimaTercerizadora(connection, afiliadoInSession.getCuil_titular(), ats.getId(), null, usuario.getScreenName());
						
						if(tercerizadoras!=null && tercerizadoras.size() > 0) {
							for (Iterator iterator = tercerizadoras.iterator(); iterator.hasNext();) {
								
								AfiTercerizadoraServicio tercerizadora = (AfiTercerizadoraServicio) iterator.next();
	
								mensajes.add("Alta tercerizadora: " + 
										(StringUtils.checkNotEmpty(tercerizadora.getTercerizadora().getDescripcion())?tercerizadora.getTercerizadora().getDescripcion():
											tercerizadora.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("ETR")?"EN TRAMITE":tercerizadora.getTercerizadora().getId_tercerizadora()) + 
										" inicio: " + sdf.format(tercerizadora.getFechaInicioPres()) + 
										(tercerizadora.getFechaFinPres()!=null?" fin: " + sdf.format(tercerizadora.getFechaFinPres()):"" ));
							}
						}
					}
					
						
			}else if(esRecuperarUltimoPlan && continuidad == 0){ 
//			    me encargo de averiguar cual es, ni hacia falta tomarlo por la pantalla
//				luego con solo voletearle la vigen_hasta y motivo de baja, logro levantar la baja del ultimo plan y aportes...
					AfiPlan afiPlanBase = planService.buscarUltimoPlanAportes(afiliadoInSession.getCuil_titular());
				    AfiPlan afiPlan_recup = new AfiPlan();
				    idPlan = afiPlanBase.getPlan().getId();
				    
					Plan plan = planService.buscaPlanPorId(idPlan);
					List<TipoAporte> aportes = planService.buscaAportesPorPlan(idPlan);
					plan.setAportes(aportes);
					
					afiPlan_recup.setPlan(plan);
					afiPlan_recup.setId_plan_omint(plan.getId_plan_omint());
					afiPlan_recup.setVigenHasta(null);
					afiPlan_recup.setMotivoBaja(null);
					// le actualizamos la fecha de vigencia, si reincorpora con continuidad sera la misma, sino sera la nueva fecha...
					afiPlan_recup.setVigenDesde(vigen_fecha); 
					afiPlan_recup.setCuil_titular(afiliadoInSession.getCuil_titular());
					afiPlan_recup.setInte(afiliadoInSession.getInte());
					
					planService.insertaPlanyAportes(connection, afiPlan_recup, usuario.getScreenName());

//					tambien voleteamos baja fecha y motivo baja a la ultima tercerizadora
					List<AfiTercerizadoraServicio> tercerizadoras_recup = new ArrayList<AfiTercerizadoraServicio>();
					AfiTercerizadoraServicio ats = TercerizadoraServiceUtil.getInstance().buscarUltimaTercerizadoraDelAfiliado(connection, afiliadoInSession.getCuil_titular());
					ats.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					ats.setAfiliado(afiliadoInSession);
					ats.setFechaFinPres(null);
//					ats.setFechaInicioPres(vigen_fecha);
					
					tercerizadoras_recup.add(ats);
					
					if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CSA")){
						Calendar corteInicio = Calendar.getInstance();
						corteInicio.set(2015, Calendar.SEPTEMBER, 01); // 1/09/2015
						Calendar corteFin = Calendar.getInstance();
						corteFin.set(2019, Calendar.NOVEMBER, 30); // 1/09/2015
						
						if(vigen_fecha.before(corteInicio.getTime()) ||  vigen_fecha.before(corteFin.getTime())) {
						   AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MPS");
						   atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						   atsAjuste.setAfiliado(afiliadoInSession);
						   atsAjuste.setFechaFinPres(corteFin.getTime());
						   atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha);
						   tercerizadoras_recup.add(atsAjuste);
						}
						
						corteInicio.add(Calendar.DATE, -1); //corte FIN!!
						ats.setFechaFinPres(corteInicio.getTime());
						Calendar corteInicio2 = Calendar.getInstance();
						corteInicio2.set(2019, Calendar.DECEMBER, 01); // 1/12/2019
						AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MEN");
						atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						atsAjuste.setAfiliado(afiliadoInSession);
						
/*	Comentado 2023-03-06					
						atsAjuste.setFechaFinPres(null);
						atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio2.getTime())?corteInicio2.getTime():vigen_fecha);
						tercerizadoras_recup.add(atsAjuste);
*/						
						
//	Nuevo 2023-03-06					
						AfiTercerizadoraServicio atsAjusteMONO=null;
						AfiTercerizadoraServicio atsAjusteMONO_1=null;
						if(plan.getId()==4) {
							Calendar corteFinMEN = Calendar.getInstance();
							corteFinMEN.set(2022, Calendar.SEPTEMBER, 30); // 1/12/2019
							
							Calendar corteFinMIM = Calendar.getInstance();
							corteFinMIM.set(2024, Calendar.DECEMBER, 31); 
							
							atsAjuste.setFechaFinPres(corteFinMEN.getTime());
							atsAjusteMONO=new AfiTercerizadoraServicio("MIM");
							atsAjusteMONO.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjusteMONO.setAfiliado(afiliadoInSession);
							corteFinMEN.add(Calendar.DATE, 1);
							atsAjusteMONO.setFechaInicioPres(vigen_fecha.before(corteFinMEN.getTime())?corteFinMEN.getTime():vigen_fecha);
							atsAjusteMONO.setFechaFinPres(corteFinMIM.getTime());
							atsAjuste.setFechaInicioPres(corteInicio2.getTime());
							
							atsAjusteMONO_1=new AfiTercerizadoraServicio("MON");
							atsAjusteMONO_1.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjusteMONO_1.setAfiliado(afiliadoInSession);
							corteFinMIM.add(Calendar.DATE, 1);
							atsAjusteMONO_1.setFechaInicioPres(vigen_fecha.before(corteFinMIM.getTime())?corteFinMIM.getTime():vigen_fecha);
							atsAjusteMONO_1.setFechaFinPres(null);
							
							
						}else {
						  atsAjuste.setFechaFinPres(null);
						  atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio2.getTime())?corteInicio2.getTime():vigen_fecha);
						}  
						
//						atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio2.getTime())?corteInicio2.getTime():vigen_fecha);
						tercerizadoras_recup.add(atsAjuste);
						if(atsAjusteMONO!=null) {
						   tercerizadoras_recup.add(atsAjusteMONO);
						}
						
						if(atsAjusteMONO_1!=null) {
							   tercerizadoras_recup.add(atsAjusteMONO_1);
						}
//Fin nuevo 2023-03-06						
//						corteInicio.add(Calendar.DATE, -1); //corte FIN!!
//						ats.setFechaFinPres(corteInicio.getTime());
					}
					
					
					Calendar corteInicio2 = Calendar.getInstance();
					corteInicio2.set(2019, Calendar.DECEMBER, 01); // 1/12/2019
					
					Calendar corteFin = Calendar.getInstance();
					corteFin.setTime(corteInicio2.getTime());
					corteFin.add(Calendar.DATE, -1); //corte FIN!!
					
					if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MPS") 
							/*&& !corteFin.getTime().before(afiliadoInSession.getVigen_fecha())*/
							){
						Calendar corteInicio = Calendar.getInstance();
						corteInicio.set(2019, Calendar.DECEMBER, 01); // 1/12/2019
						AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MEN");
						atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						atsAjuste.setAfiliado(afiliadoInSession);
/* Comentado 2013-03-06						
						atsAjuste.setFechaFinPres(null);
						atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha);
						tercerizadoras_recup.add(atsAjuste);
*/
						
//Nuevo 2023-03-06					
						AfiTercerizadoraServicio atsAjusteMONO=null;
						AfiTercerizadoraServicio atsAjusteMONO_1=null;
						if(plan.getId()==4) {
							Calendar corteFinMEN = Calendar.getInstance();
							corteFinMEN.set(2022, Calendar.SEPTEMBER, 30); // 1/12/2019
							
							Calendar corteFinMIM = Calendar.getInstance();
							corteFinMIM.set(2024, Calendar.DECEMBER, 31); // 1/12/2019
							
							atsAjuste.setFechaFinPres(corteFinMEN.getTime());
							atsAjusteMONO=new AfiTercerizadoraServicio("MIM");
							atsAjusteMONO.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjusteMONO.setAfiliado(afiliadoInSession);
							corteFinMEN.add(Calendar.DATE, 1);
							atsAjusteMONO.setFechaInicioPres(vigen_fecha.before(corteFinMEN.getTime())?corteFinMEN.getTime():vigen_fecha);
							atsAjusteMONO.setFechaFinPres(corteFinMIM.getTime());
							atsAjuste.setFechaInicioPres(corteInicio.getTime());
							
							atsAjusteMONO_1=new AfiTercerizadoraServicio("MON");
							atsAjusteMONO_1.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjusteMONO_1.setAfiliado(afiliadoInSession);
							corteFinMIM.add(Calendar.DATE, 1);
							atsAjusteMONO_1.setFechaInicioPres(vigen_fecha.before(corteFinMIM.getTime())?corteFinMIM.getTime():vigen_fecha);
							atsAjusteMONO_1.setFechaFinPres(null);
							
						}else {
						  atsAjuste.setFechaFinPres(null);
						  atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha);
						}  
						
//						atsAjuste.setFechaInicioPres(vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha);
						tercerizadoras_recup.add(atsAjuste);
						if(atsAjusteMONO!=null) {
						   tercerizadoras_recup.add(atsAjusteMONO);
						}
						
						if(atsAjusteMONO_1!=null) {
							   tercerizadoras_recup.add(atsAjusteMONO_1);
						}
//Fin nuevo 2023-03-06		
						
						corteInicio.add(Calendar.DATE, -1); //corte FIN!!
						ats.setFechaFinPres(corteInicio.getTime());
						
//						/* salva error de recupear incorrectamente CSA antes del 01/09/2015 */
//						if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MPS") && 
//								vigen_fecha.after(corteInicio.getTime())){  //  30/11/2019
//							tercerizadoras_recup.remove(ats);
//						}
						/* salva error de recupear incorrectamente CSA antes del 01/09/2015 */
						if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MEN") && 
								vigen_fecha.after(corteInicio.getTime())){  //  30/11/2019
							tercerizadoras_recup.remove(ats);
						}

					}
					if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("OMI") || //Omint
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CEU") || //Consolidar
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("PRS") || //Prevencion
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CEM")  ||//CEMIC
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CCH")  ||//Chivilcoy
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("GAL")  ||//Galeno
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("ETR")  ||//En tramite
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("HAL")  ||//Hospital Aleman
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MQV")  ||//Capitas en tramite
							ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("OED")  //ORGANISMOS ESTATALES DDJJ
							){  
						Calendar corteInicio = Calendar.getInstance();
//						corteInicio.set(2016, Calendar.SEPTEMBER, 01); // 1/09/2016
//						corteInicio.set(2017, Calendar.SEPTEMBER, 01); // 1/09/2017 solicitado x Sandra 07/11/2017
						corteInicio.set(2018, Calendar.DECEMBER, 01); //  1/12/2018 solicitado x Sandra 10/11/2019
						ats.setFechaInicioPres(vigen_fecha);
						
						 	
						
						if(vigen_fecha.compareTo(ar.com.ospim.util.DateUtils.getMismoDia_00_00hs(corteInicio.getTime()))< 0){
							
							if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("OMI") || //Omint
									ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CEU") //Consolidar
						    ) {

							   ats.setFechaInicioPres(corteInicio.getTime());
							
						 	   corteInicio.add(Calendar.DATE, -1); //corte FIN!!
							   AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("ETR");
							   atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							   atsAjuste.setAfiliado(afiliadoInSession);
							   atsAjuste.setFechaFinPres(corteInicio.getTime());
							   atsAjuste.setFechaInicioPres(vigen_fecha);
							   tercerizadoras_recup.add(atsAjuste);
							}  
		
						}
					}
					
					
					if(ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MIM")){ //MONOTRIBUTO POR IMESA
						Calendar corteFinMIM = Calendar.getInstance();
						corteFinMIM.set(2024, Calendar.DECEMBER, 31); // 1/12/2019
						ats.setFechaFinPres(corteFinMIM.getTime());
						AfiTercerizadoraServicio atsAjusteMONO_1=new AfiTercerizadoraServicio("MON");
						atsAjusteMONO_1.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						atsAjusteMONO_1.setAfiliado(afiliadoInSession);
						corteFinMIM.add(Calendar.DATE, 1);
						atsAjusteMONO_1.setFechaInicioPres(vigen_fecha.before(corteFinMIM.getTime())?corteFinMIM.getTime():vigen_fecha);
						atsAjusteMONO_1.setFechaFinPres(null); 
						tercerizadoras_recup.add(atsAjusteMONO_1); 
					}
					
					
					
					mensajes = AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession, 
							usuario,tercerizadoras_recup, /*idPlan,*/ afiPlanActual, afiPlan_recup, connection);	
			}else{
					boolean todasTercEstadoAlta = true;
					planService.insertaPlanyAportes(connection, afiPlanNuevo, usuario.getScreenName());
					idPlan= afiPlanNuevo.getPlan().getId();
					
					/* Revisamos si solo ingresan una tercerizadora CSA porque es reincoporacion anterior al 31/08/2015*/
					/* entonces debemos poner una tercerizadora MPS a partir del 1/09/2015*/
					AfiTercerizadoraServicio tercerizadoraMayor=null;		
					for(AfiTercerizadoraServicio terc: tercerizadoras){  // buscar la ultima tercerizadora vigente
//						if(!terc.isBorradoLogico() 
						if (terc.getEstado()!=null && !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)		
							&& (null==terc.getFechaFinPres() 
								|| tercerizadoraMayor==null 
								|| (null!=tercerizadoraMayor && null!=tercerizadoraMayor.getFechaFinPres() 
									&& null!=terc && null!= terc.getFechaFinPres() 
									&& tercerizadoraMayor.getFechaFinPres().before(terc.getFechaFinPres())))){
							tercerizadoraMayor=terc;
						}
//						este control lo hacemos porque al seleccionar el plan, sugiere todas las tercerizadoras que corresponden
						if(todasTercEstadoAlta &&
							terc.getEstado()!=null && !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.ALTA)){
							todasTercEstadoAlta = false;
						}
					}
					if(todasTercEstadoAlta){

						mensajes = AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession, usuario, 
								tercerizadoras, null, afiPlanNuevo, connection);
					}else{
						// la magia, poner la nueva tercerizadora y dar de baja la vigente x ser CSA
//						if(tercerizadoraMayor.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("CSA")){
//							Calendar corteInicio = Calendar.getInstance();
//							corteInicio.set(2015, Calendar.SEPTEMBER, 01); // 1/09/2015
//							AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MPS","MOLINEROS POR PS", 
//									vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha, null) ;
//
//							atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//							atsAjuste.setAfiliado(afiliadoInSession);
//							tercerizadoras.add(atsAjuste);
//							
//							corteInicio.add(Calendar.DATE, -1); //corte FIN!!
//							tercerizadoraMayor.setFechaFinPres(corteInicio.getTime());
//						}
//						tercerizadoras.clear();
//						tercerizadoras.add(tercerizadoraMayor);
//	//					// si no recuperaba planes, debo tomar lo que vino por pantalla				
//
//						mensajes = AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession, usuario, 
//								tercerizadoras, null, afiPlanNuevo, connection);
//					}	
						
						tercerizadoras.clear();
						tercerizadoras.add(tercerizadoraMayor);
						
						if(tercerizadoraMayor.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("MPS")){
							Calendar corteInicio = Calendar.getInstance();
							corteInicio.set(2019, Calendar.DECEMBER, 01); // 1/12/2019
							AfiTercerizadoraServicio atsAjuste = new AfiTercerizadoraServicio("MEN","MOLINEROS POR ENSALUD", 
									vigen_fecha.before(corteInicio.getTime())?corteInicio.getTime():vigen_fecha, null) ;

							atsAjuste.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
							atsAjuste.setAfiliado(afiliadoInSession);
							tercerizadoras.add(atsAjuste);
							
							
//							Nuevo 2023-03-06					
							AfiTercerizadoraServicio atsAjusteMONO=null;
							AfiTercerizadoraServicio atsAjusteMONO_1=null;
							if(idPlan==4) {
								Calendar corteFinMEN = Calendar.getInstance();
								corteFinMEN.set(2022, Calendar.SEPTEMBER, 30); // 1/12/2019
								
								Calendar corteFinMIM = Calendar.getInstance();
								corteFinMIM.set(2024, Calendar.DECEMBER, 31);
								
								atsAjuste.setFechaFinPres(corteFinMEN.getTime());
								atsAjusteMONO=new AfiTercerizadoraServicio("MIM");
								atsAjusteMONO.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
								atsAjusteMONO.setAfiliado(afiliadoInSession);
								corteFinMEN.add(Calendar.DATE, 1);
								atsAjusteMONO.setFechaInicioPres(vigen_fecha.before(corteFinMEN.getTime())?corteFinMEN.getTime():vigen_fecha);
								atsAjusteMONO.setFechaFinPres(corteFinMIM.getTime());
								atsAjuste.setFechaInicioPres(corteInicio.getTime());
								
								
								atsAjusteMONO_1=new AfiTercerizadoraServicio("MON");
								atsAjusteMONO_1.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
								atsAjusteMONO_1.setAfiliado(afiliadoInSession);
								corteFinMIM.add(Calendar.DATE, 1);
								atsAjusteMONO_1.setFechaInicioPres(vigen_fecha.before(corteFinMIM.getTime())?corteFinMIM.getTime():vigen_fecha);
								atsAjusteMONO_1.setFechaFinPres(null);
								
							}else {
							  atsAjuste.setFechaFinPres(null);
							}  
							
							tercerizadoras.add(atsAjuste);
							if(atsAjusteMONO!=null) {
							   tercerizadoras.add(atsAjusteMONO);
							}
							
							if(atsAjusteMONO_1!=null) {
								   tercerizadoras.add(atsAjusteMONO_1);
							}
	//Fin nuevo 2023-03-06	
							corteInicio.add(Calendar.DATE, -1); //corte FIN!!
							tercerizadoraMayor.setFechaFinPres(corteInicio.getTime());
						}
						
	//					// si no recuperaba planes, debo tomar lo que vino por pantalla				

						mensajes = AfiliadoServiceUtil.guardarTercerizadora(afiliadoInSession, usuario, 
								tercerizadoras, null, afiPlanNuevo, connection);
					}	
					
			}
			
			connection.commit();
			
		} catch (Exception e) {
			_log.debug("Error al reincorporar", e);
			ConnectionHelper.rollback(connection);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}
		
		_log.debug("saliendo de reincorporar afis");
		
		return mensajes;
	}

	public static void actualizaNumAfiliadosGrupo(String cuilTitular, int inte,
			Connection connection) throws Exception {
		getInstance().actualizaNumAfiliadosGrupo(cuilTitular, inte, connection);
	}

	public static void reincorporarAfiliado(Afiliado afiliado,
			Date vigen_fecha, Date fecha_egreso, 
			int continuidad, Date vigenFechaOriginal,
			String usuario, int id_motivo_baja_menor_edad,
			Connection connection) throws Exception {
		
		getInstance().reincorporarAfiliado(afiliado, vigen_fecha, fecha_egreso,
				continuidad, usuario, id_motivo_baja_menor_edad, connection);
	}

	

}