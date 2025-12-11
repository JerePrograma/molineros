package ar.com.ospim.correspondencia.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.correspondencia.beans.BusquedaBandejaCorreoFiltro;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondenciaTotal;
import ar.com.ospim.correspondencia.beans.TipoRemitente;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * @author sva
 * 
 */
public class CorrespondenciaServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(CorrespondenciaServiceUtil.class);
	
	private static CorrespondenciaServiceImpl instance = null;

	public static CorrespondenciaServiceImpl getInstance() {
		if (null == instance) {
			instance = new CorrespondenciaServiceImpl();
		}
		return instance;
	}

	
	public static int savePaquete(ArrayList<ItemCorrespondencia> list, String descripcion, User user)
			throws SystemException {
		_log.debug("Insertando paquete");
		int id = 0;
		id = getInstance().savePaquete(list, descripcion, user.getScreenName());
		getInstance().saveListaItemsParaPaquete(list, id, user.getScreenName());		
		
		return id;
	}
	
	
	/**
	 * carga una nueva cabecera
	 * 
	 * @throws SystemException
	 * @throws SQLException
	 */
	public static long insertaCabeceraCorrespondencia(String edificio, Date fecha,
			long numeroCorrespondencia, String tipoRegistro, String tipoEnvio,
			String oblea, String screenName) throws SystemException {
		// validaFechas(bajaFecha, prestacionFecha);
		long idCorrespondencia = getInstance().insertaCabeceraCorrespondencia(edificio,
				fecha, numeroCorrespondencia, tipoRegistro, tipoEnvio, oblea,
				screenName);
		return idCorrespondencia;
	}

	public static List<ItemCorrespondencia> buscarItemsPorIdCorrespondencia(int id_correspondencia) throws SystemException{
		
		return getInstance().buscarItemsPorIdCorrespondencia(id_correspondencia);
		
	}

	public static ItemCorrespondencia buscarItemCorrespondenciaPorId(int id) throws SystemException{
		
		return getInstance().buscarItemCorrespondenciaPorId(id);
		
	}
	
	public static void actualizaCabeceraCorrespondencia(String edificio, Date fecha,
			long numeroCorrespondencia, String tipoRegistro, String tipoEnvio,
			String oblea, String screenName) throws SystemException {
		
		getInstance().actualizaCabeceraCorrespondencia(
				edificio, fecha, numeroCorrespondencia, tipoRegistro,
				tipoEnvio, oblea, screenName);
	}

	public static void cargaCorrespondenciaDetalleEntry(ArrayList<ItemCorrespondencia> items, User user) throws Exception {

		getInstance().insertaItemsCorrespondencia(items, user);		
	}
	
	public static long cargaCorrespondenciaDetalleEntry(ItemCorrespondencia item, User user) throws Exception {

		return getInstance().insertaItemCorrespondencia(item, user);		
	}

	public static void borraCorrespondenciaDetalleEntry(int id,
			String screenName) throws SystemException {
		getInstance().borraCorrespondenciaDetalleEntry(id, screenName);
	}

	public static void actualizarEstadoItems(Connection connectioParameter, ArrayList<ItemCorrespondencia> list,
			String estado, String screenName) throws SystemException {
		
		getInstance().actualizarEstadoItems(connectioParameter, list, estado, screenName);
		
	}
	
	public static void actualiza_estado_paquete(Connection connectioParameter, int id, String estado, String screenName) throws SystemException {
		
		getInstance().actualiza_estado_paquete(connectioParameter, id, estado, screenName);
		
	}
//	public static void borrar_todos_items_paquete(int id, String screenName) throws SystemException {
//		
//		getInstance().borrar_todos_items_paquete(id, screenName);
//		
//	}
	public static void borrarItemsDelPaquete(Connection connectioParameter, int id, String screenName) throws SystemException {
		
		getInstance().borrarItemDelPaquete(connectioParameter, id, screenName);
		
	}
		
	public static void marcarRecibidoCorresp(int id, String marca, Integer idContacto, 
			String comentariosCierreCRM, boolean esCierre,
			String screenName, String usuarioSector) throws SystemException {
		
		getInstance().marcarRecibido(id, marca, idContacto, comentariosCierreCRM, esCierre, screenName, usuarioSector);
	}
	
//	@Deprecated
//	public static List<ItemCorrespondencia> bandejaEntrada(User user, boolean esRecepcionista) throws SystemException { 
//
//		return getInstance().bandejaEntrada(user, esRecepcionista);	
//	}

	public static void updateItemCorrespondencia(int id, String tipo_remitente_destinatario, String edificio, String sector, String usuario, String empresa_remite, String sector_remite, String usuario_remite, String contenido, 
												String cuil_titular, int inte, int codigo_farmacia, String descripcion_otro, int id_prestador, String cuit_proveedor, 
												String sucu_proveedor , int id_punto_venta, String compro_tipo, String compro_nro, String cuit, String compro_letra,  
												int compro_sucu, Date compro_periodo, BigDecimal importe, Date fecha_emision, Date fecha_vencimiento, int id_seccional, 
												String seguimiento_paq, String screenName) 
												throws SystemException {
		getInstance().update_item(id, tipo_remitente_destinatario, edificio, sector, usuario, empresa_remite, sector_remite, usuario_remite, contenido, cuil_titular, inte, codigo_farmacia, 
								descripcion_otro, id_prestador, cuit_proveedor, sucu_proveedor, id_punto_venta, compro_tipo, compro_nro, cuit, compro_letra,
								compro_sucu, compro_periodo ,importe, fecha_emision, fecha_vencimiento, id_seccional, seguimiento_paq, screenName);
	}
	
//	public static List<ItemCorrespondencia> bandejaEntrada(User user, boolean esRecepcionista, BusquedaBandejaCorreoFiltro filtro) throws SystemException { 
//
//		return getInstance().bandejaEntrada(user, esRecepcionista, filtro);	
//	}
	
	public static List<ItemCorrespondenciaTotal> bandejaEntradaPagina(User user, boolean esRecepcionista, BusquedaBandejaCorreoFiltro filtro,
																	boolean perteneceLiquidaciones) throws SystemException { 

		return getInstance().bandejaEntradaPagina(user, esRecepcionista, filtro, perteneceLiquidaciones);	
	}
	
	public static void actualizarCorrespondenciaHistorico(int id) throws SystemException {
		
		getInstance().actualiza_historico(id);
		
	}
	
	public static boolean buscarFCPrestadorDuplicado(String cuitPrestador, int idPtoVenta, String tipoComprobante, String letraComprobante, 
			String nroComprobante, int sucuComprobante) throws SystemException {
		
		return getInstance().buscarFCPrestadorDuplicado(cuitPrestador, idPtoVenta, tipoComprobante, letraComprobante, nroComprobante, sucuComprobante);
	}

	public List<TipoRemitente> getTiposRemitentes() throws SystemException {
		List<TipoRemitente> tiposRemitentes = new ArrayList<TipoRemitente>();
		
		tiposRemitentes = getInstance().getTiposRemitentes();
		
		return tiposRemitentes;
	}
}