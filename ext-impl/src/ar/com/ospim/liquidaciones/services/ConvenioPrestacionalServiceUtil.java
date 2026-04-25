package ar.com.ospim.liquidaciones.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.prestadores.NoSuchConvenioPrestacionalEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

/**
 * Mascara del servicio que da acceso a los datos de la aplicaci�n (BD).
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
	 * eliminar un convenio prest. (baja logica)
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
	
	
	public static String validarDetalleExistente(ConvenioPrestacional convenioPrest) throws SystemException {
		
		ArrayList<ConvenioPrestacionalDetalle> convenioPrestDetalle = (ArrayList<ConvenioPrestacionalDetalle>) convenioPrest.getConvenioPrestDetalle();
		ArrayList<ConvenioPrestacionalDetalle> convenioPrestDetalleAux = (ArrayList<ConvenioPrestacionalDetalle>) convenioPrest.getConvenioPrestDetalle();
		
		String mensaje = null;
		int posActual = 0;
		for (ConvenioPrestacionalDetalle cpd : convenioPrestDetalle) {

			int i=0;
			if(posActual==0){
				i++;
			}
			while(posActual != i && i < convenioPrestDetalleAux.size()){
				ConvenioPrestacionalDetalle aux = convenioPrestDetalleAux.get(i);
//				primero evaluamos los items estan dentro del per�odo del convenio
				if(cpd.getFechaDesde().before(convenioPrest.getVigencia()) || 
						(cpd.getFechaHasta() !=null && convenioPrest.getVencimiento() !=null 
						&& cpd.getFechaHasta().after(convenioPrest.getVencimiento()))){
					mensaje = "El �tem nro: "+ (posActual+1) + " esta fuera del per�odo de vigencia del Convenio. ";
					
					break; // cortamos el WHILE
				}
//				despues evaluamos si hay solapamiento con otros items del mismo tipo nomenclador
				if(cpd.getBajaFecha() == null 
					&& aux.getBajaFecha() == null
					&& cpd.getTipoNomenclador().getId_tipo_nomenclador() == aux.getTipoNomenclador().getId_tipo_nomenclador()
					&& cpd.getIdPlan() == aux.getIdPlan()
					&&(
							(Integer.parseInt(cpd.getCodigoDesde()) <= Integer.parseInt(aux.getCodigoDesde())
							&& Integer.parseInt(cpd.getCodigoHasta()) >= Integer.parseInt(aux.getCodigoDesde()))
					   ||
						   (Integer.parseInt(cpd.getCodigoDesde()) <= Integer.parseInt(aux.getCodigoHasta())
							&& Integer.parseInt(cpd.getCodigoHasta()) >= Integer.parseInt(aux.getCodigoHasta()))
					   ||
						   (Integer.parseInt(cpd.getCodigoDesde()) <= Integer.parseInt(aux.getCodigoDesde())
							&& Integer.parseInt(cpd.getCodigoHasta()) >= Integer.parseInt(aux.getCodigoHasta()))	
					)
				){
					mensaje = "El �tem nro: "+ (posActual+1) + " tiene solapamiento de c�digos de prestaciones con el �tem nro: "+ (i+1);
					break; // cortamos el WHILE
				}
				i++;
			}
			if(mensaje != null){
				break; // cortamos el FOR
			}
			posActual++;

		}
		return mensaje; 
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
	
	public static int insertarConvenioPrestacional(ConvenioPrestacional convPrest, User user) throws SystemException{
		
		return getInstance().insertarConvenioPrestacional(convPrest, user.getScreenName());
		
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


}