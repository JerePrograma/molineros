package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//import ar.com.ospim.afiliados.action.GuardarOtrosDatosAction.AportesYEgreso;
import ar.com.ospim.afiliados.beans.AfiAporteList;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.model.User;

/**
 * <a href="AporteServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.AporteServiceUtil </code> bean. The
 * static methods of this class calls the same methods of the bean instance.
 * It's convenient to be able to just write one line to call a method on a bean
 * instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceImpl
 * 
 */
public class AporteServiceUtil {

	private static AporteServiceImpl instance = null;

	public static AporteServiceImpl getInstance() {
		if (null == instance) {
			instance = new AporteServiceImpl();
		}
		return instance;
	}

	public static AfiAporteList buscaAportesPorPlan(String id_plan,
			String cuil, int inte) throws Exception {
		return getInstance().buscaAportesPorPlan(id_plan, cuil, inte);
	}

	public static AfiAporteList buscaAportesPorPlan(String id_plan,
			String cuil, int inte, String fechaEgreso, String id_motivo_baja,
			boolean isPlusTres) throws Exception {
		return getInstance().buscaAportesPorPlan(id_plan, cuil, inte,
				fechaEgreso, id_motivo_baja, isPlusTres);
	}
	public static List<AporteAfiliado> buscaAportesAfipAfiliado(String cuil,
			Date fecha_desde) throws Exception {
		return getInstance().buscaAportesAfipAfiliado(cuil, fecha_desde);
	}
	
	public static List<AporteAfiliado> buscaAportesAfipYEmpleadoresAfiliado(String cuil,Date periodo, boolean cuota_amtima, boolean cuota_usufructo,
			boolean art_46, boolean cuota_social_uoma, boolean aporte_solidario_uoma, boolean aporte_afip_ospim, boolean boleta_blanca_ospim,
			boolean boleta_blanca_uoma, boolean	boleta_blanca_amtima) throws Exception {
		
		List<AporteAfiliado> aportes=buscaAportesAfipAfiliado(cuil, periodo);
		
		aportes.addAll(AporteServiceUtil.buscaAportesEmpleadoresAfiliado(cuil, periodo));
		
		List<AporteAfiliado> nuevaLista=new ArrayList<AporteAfiliado>();
		
		for(int i=0;i<aportes.size();i++){

			AporteAfiliado a=aportes.get(i);
			a.setMostrar(false);
			if(cuota_amtima&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_AMTIMA){
				a.setMostrar(true);				
			}
			if(cuota_usufructo&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_USUFRUCTO){
				a.setMostrar(true);
			}
			if(art_46&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_ART_46){
				a.setMostrar(true);
			}
			if(cuota_social_uoma&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_SOCIAL_UOMA){
				a.setMostrar(true);
			}
			if(aporte_solidario_uoma&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_SOLIDARIO_UOMA){
				a.setMostrar(true);
			}
			if(aporte_afip_ospim&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_OS){
				a.setMostrar(true);
			}
			if(boleta_blanca_ospim&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_BLANCA_OSPIM){
				a.setMostrar(true);
			}
			if(boleta_blanca_uoma&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_BLANCA_UOMA){
				a.setMostrar(true);
			}
			if(boleta_blanca_amtima&&a.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_BLANCA_AMTIMA){
				a.setMostrar(true);
			}
			nuevaLista.add(a);
		}
		
		nuevaLista=AporteServiceUtil.sortByDate(nuevaLista);
		
		return nuevaLista;
	}
	
	public static List<AporteAfiliado> buscaAportesEmpleadoresAfiliado(String cuil,
			Date periodo) throws Exception {
	
		
		return getInstance().buscaAportesEmpleadoresAfiliado(cuil, periodo);
	}

//	public static List<AfiAporteList> buscarHistorico(Afiliado afiliado)
//			throws Exception {
//		return getInstance().buscaHistoricoAportesAfipAfiliado(afiliado);
//	}

//	public static List<AportesYEgreso> buscarAportesValidosParaFechaVigencia(
//			String cuil_titular) throws Exception {
//		return getInstance()
//				.buscarAportesValidosParaFechaVigencia(cuil_titular);
//	}
	
	public static List<AporteAfiliado> filtrarLista(List<AporteAfiliado> afiliadosList, boolean cuota_amtima, boolean cuota_usufructo,
			boolean art_46, boolean cuota_social_uoma, boolean aporte_solidario_uoma, boolean aporte_afip_ospim, boolean boleta_blanca_ospim,
			boolean boleta_blanca_uoma, boolean boleta_blanca_amtima){
			
			List<AporteAfiliado> nuevaLista=new ArrayList<AporteAfiliado>();	
			
			for(AporteAfiliado afi:afiliadosList){
				afi.setMostrar(false);
				if(cuota_amtima&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_AMTIMA){
					afi.setMostrar(true);				
				}
				if(cuota_usufructo&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_USUFRUCTO){
					afi.setMostrar(true);
				}
				if(art_46&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_ART_46){
					afi.setMostrar(true);
				}
				if(cuota_social_uoma&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_SOCIAL_UOMA){
					afi.setMostrar(true);
				}
				if(aporte_solidario_uoma&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_SOLIDARIO_UOMA){
					afi.setMostrar(true);
				}
				if(aporte_afip_ospim&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_OS){
					afi.setMostrar(true);
				}
				if(boleta_blanca_ospim&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_BLANCA_OSPIM){
					afi.setMostrar(true);
				}
				if(boleta_blanca_uoma&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_BLANCA_UOMA){
					afi.setMostrar(true);
				}
				if(boleta_blanca_amtima&&afi.getTipoAporte()==WebKeysGlobal.TIPO_BOLETA_BLANCA_AMTIMA){
					afi.setMostrar(true);
				}
				nuevaLista.add(afi);
			}
			
			return nuevaLista;
		}
	
	@SuppressWarnings("unchecked")
	public static List<AporteAfiliado> sortByDate(List<AporteAfiliado> apos) {
		
		Collections.sort(apos, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Comparable<Date>) ((AporteAfiliado) (o2)).getPeriodo())
						.compareTo(((AporteAfiliado) (o1)).getPeriodo());
			}
		});
		
		return apos;
	}

}