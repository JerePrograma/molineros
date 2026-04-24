package ar.com.ospim.afiliados.services;

import java.math.BigInteger;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.exceptions.FaltanTercerizadorasException;
import ar.com.ospim.afiliados.exceptions.TercNoCorrespPlanException;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="TercerizadoraServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.TercerizadoraServiceUtil </code> bean.
 * The static methods of this class calls the same methods of the bean instance.
 * It's convenient to be able to just write one line to call a method on a bean
 * instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.afiliados.services.TercerizadoraServiceImpl
 * 
 */
public class TercerizadoraServiceUtil {

	private static Log _log = LogFactoryUtil
			.getLog(TercerizadoraServiceUtil.class);

	private static TercerizadoraServiceImpl instance = null;

	public static TercerizadoraServiceImpl getInstance() {
		if (null == instance) {
			instance = new TercerizadoraServiceImpl();
		}
		return instance;
	}

	public static void borraTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaIngreso, User user)
			throws Exception {
		getInstance().borraTercerizadora(cuil, inte, id_tercerizadora,
				fechaIngreso, user);

	}

	public static void grabaTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaIngreso, Date fechaEgreso,
			User user, Connection con) throws Exception {
		getInstance().grabaTercerizadora(cuil, inte, id_tercerizadora,
				fechaIngreso, fechaEgreso, user, con);
	}

	public static List<AfiTercerizadoraServicio> buscaTercerizadoras(
			String cuil, int inte, Connection con) throws Exception {
		return getInstance().buscaTercerizadoras(cuil, inte, con);
	}

	public static List<AfiTercerizadoraServicio> buscaTercerizadoras(
			String cuil, int inte) throws Exception {
		Connection con = null;
		List<AfiTercerizadoraServicio> lista = null;
		try {
			con = ConnectionHelper.getConnection();
			lista = buscaTercerizadoras(cuil, inte, con);
			con.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(con);
		}
		return lista;
	}

	public static void editarTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaIngreso, Date fechaEgreso,
			User user, Connection con, Date fechaIngresoOriginal)
			throws Exception {
		getInstance().editaTercerizadora(cuil, inte, id_tercerizadora,
				fechaIngreso, fechaEgreso, user, con, fechaIngresoOriginal);
	}

	public static ArrayList<String> editarTercerizadora(Afiliado afiliado, 
			User user, List<AfiTercerizadoraServicio> tercerizadoras, /*int id_plan*/AfiPlan planActual, AfiPlan planNuevo,
			Connection connectionParameter) throws Exception {
		
		ArrayList<String> mensajesOperacion = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			if (tercerizadoras == null || tercerizadoras.size() == 0) {
				throw new FaltanTercerizadorasException();
			}else if(!verificarPlanTercerizadora(afiliado.getCuil_titular(), planActual, planNuevo, tercerizadoras)){
				throw new TercNoCorrespPlanException();
			}	
			for (AfiTercerizadoraServicio tercerizadora : tercerizadoras) {
//				if (tercerizadora.isBorradoLogico()) {
				if (tercerizadora.getEstado()!=null && tercerizadora.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)) {
					borraTercerizadora(afiliado.getCuil_titular(), afiliado.getInte(), tercerizadora
							.getTercerizadora().getId_tercerizadora(),
							tercerizadora.getFechaInicioPres(), user, connectionParameter);
//				}else if(!verificarPlanTercerizadora(afiliado.getCuil_titular(), planActual, planNuevo, tercerizadoras)){ 
//				}else if(!verificarPlanTercerizadora(id_plan, tercerizadoras)){
//					throw new TercNoCorrespPlanException();
					
//				}else if (tercerizadora.isNuevo()) {
				}else if (tercerizadora.getEstado()!=null && tercerizadora.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.ALTA)) {
						
						grabaTercerizadora(afiliado.getCuil_titular(), afiliado.getInte(), tercerizadora.getTercerizadora().getId_tercerizadora(),
							tercerizadora.getFechaInicioPres(),
							tercerizadora.getFechaFinPres(), user,connectionParameter);
						
						mensajesOperacion.add("Alta tercerizadora: " + 
								(StringUtils.checkNotEmpty(tercerizadora.getTercerizadora().getDescripcion())?tercerizadora.getTercerizadora().getDescripcion():
									tercerizadora.getTercerizadora().getId_tercerizadora().equalsIgnoreCase("ETR")?"EN TRAMITE":tercerizadora.getTercerizadora().getId_tercerizadora()) + 
								" inicio: " + sdf.format(tercerizadora.getFechaInicioPres()) + 
								(tercerizadora.getFechaFinPres()!=null?" fin: " + sdf.format(tercerizadora.getFechaFinPres()):"" ));
						
				} else {
					editarTercerizadora(afiliado.getCuil_titular(), afiliado.getInte(), tercerizadora
							.getTercerizadora().getId_tercerizadora(),
							tercerizadora.getFechaInicioPres(),
							tercerizadora.getFechaFinPres(), user,
							connectionParameter,
							tercerizadora.getFechaInicioPresEditada());
				}
			}
		} catch (Exception e) {
			_log.debug("Error al grabar terc.", e);
			throw e;
		}
		
		return mensajesOperacion;
	}
	

//	public static boolean verificarPlanTercerizadora(int id_plan, List<AfiTercerizadoraServicio> tercerizadoras) throws Exception{
//		
//		boolean resultado=false;
//		
//		ArrayList<TercerizadoraServicio> tercerizadorasPlan=(ArrayList<TercerizadoraServicio>)getInstance().getTercerizadoraPlan(id_plan);
//		
//		AfiTercerizadoraServicio tercerizadoraMayor=null;		
//		for(AfiTercerizadoraServicio terc: tercerizadoras){
//			
//			if(!terc.isBorradoLogico() && (null==terc.getFecha_baja() || tercerizadoraMayor==null || (null!=tercerizadoraMayor && null!=tercerizadoraMayor.getFecha_baja() && null!=terc && null!= terc.getFecha_baja() && tercerizadoraMayor.getFecha_baja().before(terc.getFecha_baja())))){
//				tercerizadoraMayor=terc;
//			}			
//		}
//		
//		for(TercerizadoraServicio terceriz: tercerizadorasPlan){
//			if(tercerizadoraMayor.getTercerizadora().getId_tercerizadora().equals(terceriz.getId_tercerizadora())){
//				return true;
//			}
//		}
//		return resultado;		
//	}
	
//	A partir del corte con Omint, las tercerizadoras tendran fecha de corte o inicio, 
//	para ello verificaremos que corresponda al intervalo
	public static boolean verificarPlanTercerizadora(String cuilTitular,
			AfiPlan planActual, AfiPlan planNuevo,
			List<AfiTercerizadoraServicio> tercerizadoras) throws Exception {

		ArrayList<TercerizadoraServicio> tercerizadorasPlan = null;
		HashMap<String, TercerizadoraServicio> auxiTerc = null;

		boolean resultado = true;
		List<AfiPlan> planesDelAfiliado = new ArrayList<AfiPlan>();

//		// revisamos si lguna tercerizadora historica no tiene nada que ver con
//		// el plan actual o nuevo
//		for (Iterator<AfiTercerizadoraServicio> iterator = tercerizadoras.iterator(); iterator.hasNext();) {
//			AfiTercerizadoraServicio ats = iterator.next();
//
//			if (planActual != null
//					&& planActual.getVigenDesde().after(ats.getFecha_baja())) {
//				tercerizadoras.remove(ats);
//			}
//		}

		// if(tercerizadoras.size()>2){
		// planesDelAfiliado =
		// PlanServiceUtil.getInstance().historicoPlanyAportes(cuilTitular);
		// }else{
		AfiPlan ultPlanDelAfiliado = PlanServiceUtil.getInstance()
				.buscarUltimoPlanAportes(cuilTitular);
		if(ultPlanDelAfiliado!=null && planActual !=null){ // para los casos que no es Alta
			planesDelAfiliado.add(ultPlanDelAfiliado);
		}
		// }
		// si el plan actual esta modificado, debemos quitarlo de la lista del
		// historico
		// List<AfiPlan> planesDelAfiliado =
		// PlanServiceUtil.getInstance().historicoPlanyAportes(cuilTitular);
		if (planActual != null) {
			planesDelAfiliado.remove(planActual); // quita al plan identico de
													// la BD que esta obsoleto
			planesDelAfiliado.add(planActual);
		}
		// si el plan nuevo esta cargado, lo agregamos a la lista para controles
		if (planNuevo != null) {
			planesDelAfiliado.add(planNuevo);
		}

		// 1ero recorremos los planes para ver que tercerizadoras corresponden x
		// plan
		int i = 0;
		for (Iterator<AfiPlan> iterator = planesDelAfiliado.iterator(); iterator
				.hasNext();) {
			AfiPlan afiPlan = iterator.next();

			tercerizadorasPlan = (ArrayList<TercerizadoraServicio>) getInstance()
					.getTercerizadoraPlan(afiPlan.getPlan().getId());

			auxiTerc = new HashMap<String, TercerizadoraServicio>();

			for (TercerizadoraServicio tercServ : tercerizadorasPlan) {
				auxiTerc.put(tercServ.getId_tercerizadora(), tercServ);
			}

			boolean existeCoberturaTercerizadoraParaElPlan = false;

			while (i < tercerizadoras.size() && resultado
					&& !existeCoberturaTercerizadoraParaElPlan) {
				AfiTercerizadoraServicio terc = tercerizadoras.get(i);
				
				//intentamos que si hay tercerizadras historicas del afi para el control de una terceriz vigente
				//no sea analizado frente al planActual y planNuevo
				
				if (planActual != null && terc.getFechaFinPres() != null
						&& planActual.getVigenDesde().compareTo(terc.getFechaFinPres()) > 0) {
					i++;
//					break;
				}else{
				
					TercerizadoraServicio ts = auxiTerc.get(terc.getTercerizadora()
							.getId_tercerizadora());
					// No puede no encontrar una tercerizadora para las
					// tercerizadoras del plan
					if (ts == null) {
						if(i == tercerizadoras.size()){
							resultado = false;
							break; // funciona para el while ????
						}else{
							i++;
							continue;
						}
					}
					// la vigencia hasta de la terceriz del afi no debe ser mayor a
					// la fecha corte fin de ts
//					if (!terc.isBorradoLogico() && ts.getFechaFin() != null
					if (terc.getEstado()!=null 
							&& !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)
							&& ts.getFechaFin() != null
							// && (terc.getFecha_baja() == null ||
							// terc.getFecha_baja().compareTo(ts.getFechaFin()) > 0
							// ) ){
							&& (terc.getFechaFinPres() == null || DateUtils
									.compararFechasTruncarEnDia(
											terc.getFechaFinPres(), ts.getFechaFin()) > 0)) {
						resultado = false;
						// break;
					}
					// la vigencia desde de la terceriz del afi no debe ser mayor a
					// la fecha corte inicio de ts
//					if (!terc.isBorradoLogico() && ts.getFechaInicio() != null
					if (terc.getEstado()!=null 
							&& !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)
							&& ts.getFechaInicio() != null
							// && (terc.getFecha_ingre() != null &&
							// terc.getFecha_ingre().compareTo(ts.getFechaInicio())
							// < 0 ) ){
							&& (terc.getFechaInicioPres() != null && DateUtils
									.compararFechasTruncarEnDia(
											terc.getFechaInicioPres(),
											ts.getFechaInicio()) < 0)) {
						resultado = false;
						// break;
					}
	
					// la vigencia hasta del plan debe estar cubierta x alguna
					// tercerizadora
//					if (!terc.isBorradoLogico()
					if (terc.getEstado()!=null 
							&& !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)
							// &&
							// afiPlan.getVigenDesde().compareTo(terc.getFecha_ingre())
							// >= 0
							&& DateUtils.compararFechasTruncarEnDia(
									afiPlan.getVigenDesde(), terc.getFechaInicioPres()) >= 0
							&& (afiPlan.getVigenHasta() == null && terc
									.getFechaFinPres() == null)) {
						existeCoberturaTercerizadoraParaElPlan = true;
						// break;
//					} else if (!terc.isBorradoLogico()
					}else if (terc.getEstado()!=null 
							&& !terc.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)	
							// &&
							// afiPlan.getVigenDesde().compareTo(terc.getFecha_ingre())
							// >= 0
							&& DateUtils.compararFechasTruncarEnDia(
									afiPlan.getVigenDesde(), terc.getFechaInicioPres()) >= 0
							&& afiPlan.getVigenHasta() != null
							&& terc.getFechaFinPres() != null
							// &&
							// afiPlan.getVigenHasta().compareTo(terc.getFecha_baja())
							// >= 0 ){
							&& DateUtils.compararFechasTruncarEnDia(
									afiPlan.getVigenHasta(), terc.getFechaFinPres()) >= 0) {
						existeCoberturaTercerizadoraParaElPlan = true;
					}
					
					i++;
				}
			}

		}

		return resultado;
	}

	public static void borraTercerizadora(String cuil_titular, int inte,
			String id_tercerizadora, Date fecha_ingreso, User user,
			Connection con) throws Exception {
		getInstance().borraTercerizadora(cuil_titular, inte, id_tercerizadora,
				fecha_ingreso, user, con);

	}
	
	public static void actualizaBajaUltimaTercerizadora(Connection con, String cuil_titular, BigInteger idTercerizadora, Date fechaEgreso, String user) throws Exception {
		getInstance().actualizaBajaUltimaTercerizadora(con, cuil_titular, idTercerizadora, fechaEgreso, user); 

	}
	
	public static AfiTercerizadoraServicio buscarUltimaTercerizadoraDelAfiliado(Connection con, String cuil_titular) throws Exception {
		
		return getInstance().buscarUltimaTercerizadoraDelAfiliado(con, cuil_titular); 

	}
	
	public static List<AfiTercerizadoraServicio> buscarUltimasTercerizadorasContinuidadDelAfiliado(Connection con, String cuil_titular) throws Exception {
		return getInstance().buscarUltimasTercerizadorasContinuidadDelAfiliado(con, cuil_titular); 
	}
	
	public static List<AfiTercerizadoraServicio> historicoTercerizadoraDelAfiliado(String cuil_titular) throws Exception {
		return getInstance().historicoTercerizadoraDelAfiliado(cuil_titular);
	}	
	
	public static List<AfiTercerizadoraServicio> traeHistoricoTercerizadoras(String cuil_titular) throws Exception {
		
		return getInstance().traeHistoricoTercerizadoras(cuil_titular);
		
	}

	/**
	 * 
	 * @param tercerizadoras
	 * @return mensaje, si es vacio no se solapan, si hay solapamiento, el mensaje sirve para indicar cuales Tercerizadoras se solapan...
	 * 
	 */
	public static String seSolapanVigencias(
			List<AfiTercerizadoraServicio> tercerizadoras) {
		
		String mensaje = null;

		for (int i = 0; i < tercerizadoras.size(); i++) {
			AfiTercerizadoraServicio afit1 = tercerizadoras.get(i);
			for (int j = 0; j < tercerizadoras.size(); j++) {
				AfiTercerizadoraServicio afit2 = tercerizadoras.get(j);
//				if (!afit2.equals(afit1) && !afit1.isBorradoLogico() && !afit2.isBorradoLogico()) {
				if (!afit2.equals(afit1) 
						&& afit1.getEstado()!=null && !afit1.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA) 
						&& afit2.getEstado()!=null && !afit2.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA) ) {
					if (DateUtils.compararFechasTruncarEnDia(
							afit1.getFechaInicioPres(), afit2.getFechaInicioPres()) >= 0) {
						if (null == afit2.getFechaFinPres()) {
							mensaje = "Se solapan las tercerizadoras: " +afit1.getTercerizadora().getDescripcion() + " y " + afit2.getTercerizadora().getDescripcion();
							break;
						}else if(null==afit1.getFechaFinPres() && DateUtils.compararFechasTruncarEnDia(
								afit2.getFechaFinPres(), afit1.getFechaInicioPres())>=0){
							mensaje = null;
							break;
						}
					}
					if (DateUtils.compararFechasTruncarEnDia(
							afit1.getFechaInicioPres(), afit2.getFechaInicioPres()) <= 0) {
						if (null == afit1.getFechaFinPres()) {
							mensaje = "Se solapan las tercerizadoras: " +afit1.getTercerizadora().getDescripcion() + " y " + afit2.getTercerizadora().getDescripcion();
							break;
						} else if (afit2.getFechaFinPres()==null && DateUtils.compararFechasTruncarEnDia(
								afit1.getFechaFinPres(), afit2.getFechaInicioPres())>=0 ){
							mensaje = "Se solapan las tercerizadoras: " +afit1.getTercerizadora().getDescripcion() + " y " + afit2.getTercerizadora().getDescripcion();
							break;
						}
					}
				}
			}
			if(mensaje !=null){
				break;
			}

		}
		return mensaje;
	}

	public static void actualizaTercerizadora(AfiTercerizadoraServicio ats, User user, Connection connectionParameter) throws Exception {
		
		getInstance().actualizaTercerizadora(ats, user, connectionParameter);
		
	}
}