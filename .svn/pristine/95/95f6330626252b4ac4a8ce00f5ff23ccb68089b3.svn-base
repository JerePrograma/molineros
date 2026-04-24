package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.beans.BusquedaConsultasIGSFiltro;
import ar.com.ospim.autorizaciones.beans.ConsultaIGSTotal;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;

/**
 * <a href="BusquedaAfiliadoServiceUtil.java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceImpl</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceImpl
 * 
 */
public class BusquedaAfiliadoServiceUtil {

	private static BusquedaAfiliadoServiceImpl instance = null;

	public static BusquedaAfiliadoServiceImpl getInstance() {
		if (null == instance) {
			instance = new BusquedaAfiliadoServiceImpl();
		}
		return instance;
	}

	public static List<Afiliado> getBusquedaAfiliados(String cuil, String inte,
			String tipoDoc, String nroDoc, int seccional, String apellido,
			String nombre) throws Exception {
		return getInstance().getBusquedaAfiliados(cuil, inte, tipoDoc, nroDoc,
				seccional, apellido, nombre);
	}

	
	
	public static List<Afiliado> getBusquedaAfiliadosComponenteReintegro(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, String entidad, int afiNumero, 
			int nroSocioPrev, BigDecimal nroCredenPrev) throws Exception {
		
		int campoUoma = 0;
		for (String entidadi : WebKeysGlobal.ENTIDADES_UOMA) {
			if (entidadi.equalsIgnoreCase(entidad)) {
				campoUoma = WebKeysGlobal.ENTIDADES_UOMA_INDICES[campoUoma];				
				break;
			}
			campoUoma++;
		}
		return getInstance().getBusquedaAfiliadosComponenteReintegro(cuil, inte,
				tipoDoc, nroDoc, seccional, apellido, nombre, campoUoma,
				afiNumero, nroSocioPrev, nroCredenPrev);
	}
	
	public static List<Afiliado> getBusquedaAfiliadosComponente(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, String entidad, int afiNumero, 
			int nroSocioPrev, BigDecimal nroCredenPrev) throws Exception {
		
		int campoUoma = 0;
		for (String entidadi : WebKeysGlobal.ENTIDADES_UOMA) {
			if (entidadi.equalsIgnoreCase(entidad)) {
				campoUoma = WebKeysGlobal.ENTIDADES_UOMA_INDICES[campoUoma];				
				break;
			}
			campoUoma++;
		}
		return getInstance().getBusquedaAfiliadosComponente(cuil, inte,
				tipoDoc, nroDoc, seccional, apellido, nombre, campoUoma,
				afiNumero, nroSocioPrev, nroCredenPrev);
	}
	
	
	public static List<Afiliado> getBusquedaAfiliadosComponenteCredencialUOMA(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, String entidad, int afiNumero, 
			int nroSocioPrev, BigDecimal nroCredenPrev) throws Exception {
		
		int campoUoma = 0;
		for (String entidadi : WebKeysGlobal.ENTIDADES_UOMA) {
			if (entidadi.equalsIgnoreCase(entidad)) {
				campoUoma = WebKeysGlobal.ENTIDADES_UOMA_INDICES[campoUoma];				
				break;
			}
			campoUoma++;
		}
		return getInstance().getBusquedaAfiliadosComponenteCredencialUOMA(cuil, inte,
				tipoDoc, nroDoc, seccional, apellido, nombre, campoUoma,
				afiNumero, nroSocioPrev, nroCredenPrev);
	}
	public static List<Afiliado> getBusquedaAfiliadosOpciones(String cuil,
			String delegacion, String apellido, String nombre, int libro, int nroFormulario, boolean incluyeBajas)
			throws Exception {
		
		return getInstance().getBusquedaAfiliadosOpciones(cuil, delegacion,
				apellido, nombre, libro, nroFormulario, incluyeBajas);
	}
	
	
	
	
	public static List<Afiliado> getBusquedaAfiliadosComponenteReintegro(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, String entidad, int afiNumero, Date fechaPrestacion, BigDecimal nroCredenPrev,int nroSocioPrev)
			throws Exception {
		int campoUoma = 0;
		for (String entidadi : WebKeysGlobal.ENTIDADES_UOMA) {
			if (entidadi.equalsIgnoreCase(entidad)) {
				campoUoma = WebKeysGlobal.ENTIDADES_UOMA_INDICES[campoUoma];				
				break;
			}
			campoUoma++;
		}
		return getInstance().getBusquedaAfiliadosComponenteReintegro(cuil, inte,
				tipoDoc, nroDoc, seccional, apellido, nombre, campoUoma,
				afiNumero, fechaPrestacion, nroCredenPrev, nroSocioPrev);
	}
	
	public static List<Afiliado> getBusquedaAfiliadosComponente(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, String entidad, int afiNumero, Date fechaPrestacion,  int nroSocioPrev, BigDecimal nroCredenPrev)
			throws Exception {
		int campoUoma = 0;
		for (String entidadi : WebKeysGlobal.ENTIDADES_UOMA) {
			if (entidadi.equalsIgnoreCase(entidad)) {
				campoUoma = WebKeysGlobal.ENTIDADES_UOMA_INDICES[campoUoma];				
				break;
			}
			campoUoma++;
		}
		return getInstance().getBusquedaAfiliadosComponente(cuil, inte,
				tipoDoc, nroDoc, seccional, apellido, nombre, campoUoma,
				afiNumero, fechaPrestacion, nroSocioPrev,nroCredenPrev);
	}
	
	public static List<Afiliado> getBusquedaGrupoFliar(String cuil_titular)
			throws Exception {
		return getInstance().getBusquedaGrupoFliar(cuil_titular);
	}
	
	public static List<DetalleOpcionesSS> buscarOpcionesSSSpendientesExportar()
			throws Exception {
		
		return getInstance().buscarOpcionesSSSpendientesExportar();
	}
	
	public static List<DetalleOpcionesSS> buscarOpcionesSSSpendientesExportarXls()
			throws Exception {
		
		return getInstance().buscarOpcionesSSSpendientesExportarXls();
	}
	
	public static List<Domicilio> buscarDomiciliosAfiliado(String cuil_titular, int inte) throws Exception {
		
		return getInstance().buscarDomiciliosAfiliado(cuil_titular, inte);
		
	}
	
	public static Afiliado registraConsultaAfiliadoIGS(String cuilTitular, String nroCreden, String inte, String docuTipo, String docuNumero, String ip,
			String fecha) throws Exception{
		return getInstance().registraConsultaAfiliadoIGS(cuilTitular, nroCreden, inte, docuTipo, docuNumero, ip,fecha);
	}

	public static List<ConsultaIGSTotal> buscarConsultasIGS(BusquedaConsultasIGSFiltro filtro) throws Exception{
		return getInstance().buscarConsultasIGS(filtro);
	}
	
	public static List<ConsultaIGSTotal> buscarConsultasIGS_xls(BusquedaConsultasIGSFiltro filtro) throws Exception{
		return getInstance().buscarConsultasIGS_xls(filtro);
	}
	
}
