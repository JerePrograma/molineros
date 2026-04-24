package ar.com.ospim.prestadores.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.NoSuchConvenioPrestacionalEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;

import com.liferay.portal.SystemException;

/**
 * Mascara del servicio que da acceso a los datos de la aplicación (BD).
 */
public class ConvenioPrestacionalServiceUtil {

	private static ConvenioPrestacionalServiceImpl instance = null;

	public static ConvenioPrestacionalServiceImpl getInstance() {
		if (null == instance) {
			instance = new ConvenioPrestacionalServiceImpl();
		}
		return instance;
	}
	/**
	 * Obtiene el convenioPrestac por su clave primaria
	 *
	 * @param id_convenioPrest
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static ConvenioPrestacional getConvenioPrestacional(int idConvenioPrest)
			throws SystemException, NoSuchConvenioPrestacionalEntryException {

		ConvenioPrestacional convenio = getInstance().getConvenioPrestacional(idConvenioPrest);
		convenio.setConvenioPrestDetalle(getInstance().getConvePrestDetalles(idConvenioPrest));
		return convenio;
	}

	/**
	 * Obtiene un convenio prestacional de un prestador
	 *
	 * @param idPrestador
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static ConvenioPrestacional getConvenioPrestacionalPorPrestador(int idPrestador)
			throws SystemException, NoSuchConvenioPrestacionalEntryException {

		ConvenioPrestacional convenio = getInstance().getConvenioPrestacionalPorPrestador(idPrestador);
		convenio.setConvenioPrestDetalle(getInstance().getConvePrestDetalles(convenio!=null?convenio.getId():0));

		return convenio;
	}

	/**
	 * eliminar un convenio prestacional (baja lógica)
	 *
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static void eliminarConvenioPrestacional(int idConvPrest, String userName)
			throws NoSuchConvenioPrestacionalEntryException, SystemException {
		getInstance().eliminarConvenioPrestacional(idConvPrest, userName);
	}

	public static void cambiarEstadoConvenioPrestacional(int idConvPrest, int estado,
			String userName) throws NoSuchReintegroEntryException,
			SystemException, NoSuchConvenioPrestacionalEntryException {
		getInstance().cambiarEstadoConvenioPrestacional(idConvPrest, estado, userName);
	}

	public static List<ConvenioPrestacional> buscarConveniosPrestacionales(BusquedaConvenioPrestacionalFiltro filtro) throws Exception {
		return getInstance().buscarConveniosPrestacionales(filtro);
	}

	public static ArrayList<ConvenioPrestacionalDetalle> getTotalContratoDetalles(ArrayList<ConvenioPrestacionalDetalle> contratoDetalles, ArrayList<ConvenioPrestacionalDetalle> contratoDetallesSession) {
		contratoDetalles = contratoDetalles != null ? contratoDetalles : new ArrayList<ConvenioPrestacionalDetalle>();
//		contratoDetallesSession = contratoDetallesSession != null ? contratoDetallesSession : new ArrayList<ConvenioPrestacionalDetalle>();
//		for (ConvenioPrestacionalDetalle cd : contratoDetallesSession) {
//			if (cd.getMarcaEdit().equalsIgnoreCase("ADD")) {
//				contratoDetalles.add(cd);
//			} else if (cd.getMarcaEdit().equalsIgnoreCase("DELETE")) {
//				Iterator<ConvenioPrestacionalDetalle> iterator = contratoDetalles.iterator();				
//				while (iterator.hasNext()) {
//					ConvenioPrestacionalDetalle contratoDetalle = iterator.next();
//					if (contratoDetalle.getId_contrato_detalle() == cd.getId_contrato_detalle()) {
//						iterator.remove();
//						break;
//					}
//				}
//			}
//		}
		return contratoDetalles;
	}


	public static String validarDetalleExistente(ConvenioPrestacional convenio) throws Exception {
		return getInstance().validarDetalleExistente(convenio);
	}

	public static boolean validarConvenioPrestadorVigente(int idPrestador) {
		BusquedaConvenioPrestacionalFiltro filtro = new BusquedaConvenioPrestacionalFiltro(null, null, idPrestador, 0, 1);
		boolean result = false;
		ArrayList<ConvenioPrestacional> conveniosPrestador = (ArrayList<ConvenioPrestacional>) getInstance().buscarConveniosPrestacionales(filtro);
		for (ConvenioPrestacional convenio : conveniosPrestador) {
			if (convenio.getBajaFecha() == null) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static int insertarConvenioPrestacional(ConvenioPrestacional convPrest, String screenName) throws SystemException {

		return getInstance().insertarConvenioPrestacional(convPrest, screenName);

	}

	public static List<ConvenioPrestacionalDetalle> getPrestacionesDetallesPorCodigo(int idConvPrest)
			throws SystemException {

		return getInstance().getPrestacionesDetallesPorCodigo(idConvPrest);
	}

	/**
	 *
	 * @param convPrest
	 * @param userName
	 * @throws SystemException
	 */
	public static void actualizarConvenioPrestacional(ConvenioPrestacional convPrest, String userName)
			throws SystemException {

		getInstance().actualizarConvenioPrestacional(convPrest, userName);
	}

	public static List<ConvenioPrestacionalDetalle> detalleValorizarTratamiento(int id_prestador, Date fechaDesde, Date fechaHasta,
			String codigo, int plan) throws SystemException {

		List<ConvenioPrestacionalDetalle> existentes = new ArrayList<ConvenioPrestacionalDetalle>();
		existentes=getInstance().detalleValorizarTratamiento(id_prestador, fechaDesde, fechaHasta, codigo, plan);
		return existentes;

	}

	public static List<ConvenioPrestacionalDetalle> detalleValorizarTratamientoV01(int id_prestador, Date fechaDesde, Date fechaHasta,
			String codigo, int plan) throws SystemException {

		List<ConvenioPrestacionalDetalle> existentes = new ArrayList<ConvenioPrestacionalDetalle>();
		existentes=getInstance().detalleValorizarTratamientoV01(id_prestador, fechaDesde, fechaHasta, codigo, plan);
		return existentes;

	}


	public static Integer getIdPrestacionPorCodigo(String codigo) throws Exception {
		return getInstance().getIdPrestacionPorCodigo(codigo);
	}

	public static String getDescripcionPrestacionPorCodigo(String codigo) throws Exception {
		return getInstance().getDescripcionPrestacionPorCodigo(codigo);
	}
}