package ar.com.ospim.autorizaciones.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.SeccionalExcel;
import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.beans.BusquedaReporteReclamoFiltro;
import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinarioExcel;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalExcel;
import ar.com.ospim.autorizaciones.beans.SituacionMedicaExcel;
import ar.com.ospim.autorizaciones.exceptions.AfiliadoNoEsBebeException;
import ar.com.ospim.autorizaciones.exceptions.ExcedeCantAutoException;
import ar.com.ospim.autorizaciones.exceptions.NoEsPlanMolineroException;
import ar.com.ospim.autorizaciones.exceptions.PeriodoNoConsecutivoException;
import ar.com.ospim.webservice.service.AfiliadoServiceImpl;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceImpl;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="AutorizacionesServiceUtil.java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * </p>
 * 
 * @author Gustavo Fernandez
 * 
 */
public class AutorizacionesServiceUtil {

	@SuppressWarnings("unused")
	private static Log _log = LogFactoryUtil
			.getLog(AutorizacionesServiceUtil.class);

	private static AutorizacionesServiceImpl instance = null;

	public static AutorizacionesServiceImpl getInstance() {
		if (null == instance) {
			instance = new AutorizacionesServiceImpl();
		}
		return instance;
	}

	public static int getGenerarAutorizacionPmi(String tipoReceta,
			Date fechaReceta, String cuil, int inte, String observaciones,
			String altaUsuario) throws Exception {
		int generarAutorizacionPmi = getInstance()
				.getGenerarAutorizacionPmi(tipoReceta, fechaReceta, cuil, inte,
						observaciones, altaUsuario);
		return generarAutorizacionPmi;
	}

	public static int getBajaAutorizacionPmi(int idAutorizacion, String bajaUsuario)
			throws Exception {
		int bajaAutorizacionPmi = getInstance().getBajaAutorizacionPmi(
				idAutorizacion, bajaUsuario);
		return bajaAutorizacionPmi;
	}

	public static int getEditarAutorizacionPmi(int numReceta,String tipoReceta, 
			Date fechaReceta, String cuil, int inte,String modiUsuario, String obs) throws Exception {
		int editarAutorizacionPmi = getInstance().getEditarAutorizacionPmi(
				numReceta, tipoReceta, fechaReceta, cuil, inte, modiUsuario, obs);
		return editarAutorizacionPmi;
	}

	
	//Lista Reclamos Prestacionales 
	public static List<ReclamoPrestacionalExcel> getListaReclamosPrestacionales (BusquedaReporteReclamoFiltro filtro)
			throws SystemException {
		return getInstance().getListaReclamosPrestacionales( filtro);
	}

	public static List<ReclamoPrestacionalExcel> getListaReclamosPrestacionalesAgrupado (BusquedaReporteReclamoFiltro filtro)
			throws SystemException {
		return getInstance().getListaReclamosPrestacionalesAgrupado( filtro);
	}
	
		
	//Lista Equipos Interdisciplinarios  
	public static List<EquipoInterdisciplinarioExcel> getListaEquiposInterdisciplinarios (Date fechaOspim, int inte, 
			String cuilTitular,int nroReclamo, String estado  ,String motivo )
			throws SystemException {
		return getInstance().getListaEquiposInterdisciplinarios(fechaOspim,  inte, cuilTitular,nroReclamo, estado , motivo );
	}

	//Lista Situacion Medica   
	public static List<SituacionMedicaExcel> getListaSituMedica (Date fechaDesde,Date fechaHasta ,int inte, 
			String cuilTitular,int tipoSitu)
			throws SystemException {
		return getInstance().getListaSituacionMedica (fechaDesde,fechaHasta,  inte, cuilTitular,tipoSitu);
	}

	
	//Lista Recetas
	public static List<AutorizacionesPmi> getListaAutorizacionesPmi(
			Date fechaReceta, String cuil, int inte, int numReceta)
			throws SystemException {
		return getInstance().getListaAutorizacionesPmi(fechaReceta, cuil, inte,
				numReceta);
	}
	
	//Lista Autorizaciones
	public static List<AutorizacionesPmi> getListaAutorizacionesPmiXauto(Date fechaReceta,
			String cuil, int inte, int idAutorizacion)
			throws SystemException {
		return getInstance().getListaAutorizacionesPmiXauto(fechaReceta, cuil, inte,
				idAutorizacion);
	}

	// Validaciones //
	public static boolean getValidaPlanMolinero(String cuil, int inte)
			throws SystemException {
		return getInstance().getValidaPlanMolinero(cuil, inte);
	}

	public static boolean getValidaAutorizacionPMI(String cuil, int inte, Date fechaReceta)
			throws SystemException, AfiliadoNoEsBebeException,
			NoEsPlanMolineroException, ExcedeCantAutoException,
			PeriodoNoConsecutivoException {

		boolean resultado = true;

		if (!getValidaPlanMolinero(cuil, inte)) {
			throw new NoEsPlanMolineroException();
		}
		if (!getValidarEdadAfiliadoAuto(cuil, inte, fechaReceta)) {
			throw new AfiliadoNoEsBebeException();
		}
		return resultado;
	}

	public static boolean getValidarEdadAfiliadoAuto(String cuil, int inte, Date fechaReceta)
			throws ExcedeCantAutoException, AfiliadoNoEsBebeException,
			PeriodoNoConsecutivoException, SystemException {

		boolean result = true;
		Date naciFecha = new AfiliadoServiceImpl().getFechaNacAfiliado(cuil,
				inte);
		Calendar naciFechaCalendar = Calendar.getInstance();
		naciFechaCalendar.setTime(naciFecha);

		Calendar mesesAtras6 = Calendar.getInstance();
		mesesAtras6.add(Calendar.MONTH, -6);
		boolean esMayor6Meses = naciFechaCalendar.before(mesesAtras6);

		Calendar mesesAtras12 = Calendar.getInstance();
		mesesAtras12.add(Calendar.MONTH, -12);
		boolean esMayor12Meses = naciFechaCalendar.before(mesesAtras12);

		int cantAutoPmi = new AutorizacionesServiceImpl()
				.getCantAutorizacionesAfiliado(cuil, inte);

		Date ultimoPeriodo = new AutorizacionesServiceImpl()
				.getValidaPeriodoNoConsecutivo(cuil, inte);

		boolean pasaPeriodo = false;
		if (ultimoPeriodo != null) {
			Calendar ultimoPeriodoCalendar = Calendar.getInstance();
			ultimoPeriodoCalendar.setTime(ultimoPeriodo);
			
			Calendar fechaRecetaCalendar = Calendar.getInstance();
			fechaRecetaCalendar.setTime(fechaReceta);
			
			Calendar fechaRecetaCalendar1Mes = Calendar.getInstance();
			fechaRecetaCalendar1Mes.setTime(ultimoPeriodo);
			fechaRecetaCalendar1Mes.add(Calendar.MONTH, 1);
			
		// los periodos deben ser consecutivos y posteriores a la primera autorizacion.	
		pasaPeriodo=fechaRecetaCalendar.after(ultimoPeriodoCalendar)&&fechaRecetaCalendar.equals(fechaRecetaCalendar1Mes);
		} else {
			pasaPeriodo = true;
		}

		// El bebe es menor a 6 meses y nunca se le dio una auto
		if (!esMayor6Meses && cantAutoPmi == 0 && pasaPeriodo) {
			return true;
		} else if (esMayor6Meses && esMayor12Meses && cantAutoPmi > 0
				&& cantAutoPmi <= 3 && pasaPeriodo) { // el bebe entre 6 y 12 y
														// tiene menos de 3 auto
			return true;
		}

		if (esMayor6Meses && cantAutoPmi == 0) {
			throw new AfiliadoNoEsBebeException();
		}
		if (!esMayor6Meses && !esMayor12Meses && cantAutoPmi >= 3) {
			throw new ExcedeCantAutoException();
		}
		if (esMayor12Meses) {
			throw new AfiliadoNoEsBebeException();
		}
		if (!pasaPeriodo) {
			throw new PeriodoNoConsecutivoException();
		}
		return result;
	}
	
	public static List<AfiDocumentacion> getListaVencimientosCUD (Date fechaOrigen,Integer diasAlVto)
			throws SystemException {
		return getInstance().getListaVencimientosCUD (fechaOrigen,diasAlVto);
	}

}
