package ar.com.uoma.facturacion.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.beans.ClaseBase;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.BusquedaFacturasFiltro;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.LoginCmsResponse;
import ar.com.uoma.facturacion.Producto;

public class FacturacionServiceUtil {

	private static Log _log = LogFactoryUtil
			.getLog(FacturacionServiceUtil.class);
	
	private static FacturacionServiceImpl instance = new FacturacionServiceImpl();

	public static FacturacionServiceImpl getInstance() {
		return instance;
	}

	public static void setInstance(FacturacionServiceImpl instance) {
		FacturacionServiceUtil.instance = instance;
	}
	
	public static List<Producto> getProductos(){
		
		List<Producto> productos = null;
		
		try {
			
			productos = getInstance().getProductos();
			
		}catch(SystemException e) {
			_log.error(e);
			return new ArrayList<Producto>();
		}
		
		return productos;
		
	}
	
	public static List<Producto> getProductos(PortletRequest portletRequest)
			throws Exception {
		List<Producto> productos = (List<Producto>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysUOMA.PRODUCTOS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (productos == null) {
			productos = getInstance().getProductos();
			portletRequest.getPortletSession().setAttribute(
					WebKeysUOMA.PRODUCTOS_EN_SESSION, productos,
					PortletSession.APPLICATION_SCOPE);
		}
		return productos;
	}
	
	public static List<Cliente> getClientes(PortletRequest portletRequest)
			throws Exception {
		List<Cliente> clientes = (List<Cliente>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysUOMA.CLIENTES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (clientes == null) {
			clientes = getInstance().getClientes();
			portletRequest.getPortletSession().setAttribute(
					WebKeysUOMA.CLIENTES_EN_SESSION, clientes,
					PortletSession.APPLICATION_SCOPE);
		}
		return clientes;
	}
	
	public static List<Cliente> getClientes(String docuNro, String apellido) throws SystemException {
		return getInstance().getClientes(docuNro, apellido);
	}
	
	public static List<Cliente> getClientesPorAnio(String docuNro, String apellido, String cuit) throws SystemException {
		return getInstance().getClientesPorAnio(docuNro, apellido, cuit);
	}
	
	public static int saveFactura(Factura factura, String usuario) throws SystemException, SQLException {
		return getInstance().saveFactura(factura, usuario);
	}
	
	public static int updateFactura(Factura factura, String usuario) throws SystemException, SQLException {
		return getInstance().updateFactura(factura, usuario);
	}
	
	public static Factura getFactura(int idFactura) throws SystemException {
		return getInstance().getFactura(idFactura);
	}
	
	public static List<Factura> getFacturas(BusquedaFacturasFiltro filtro) throws SystemException {
		return getInstance().getFacturas(filtro);
	}
	
	public static List<Factura> getFacturasPeriodo(Date fechaDesde, Date fechaHasta) throws SystemException {
		return getInstance().getFacturasPeriodo(fechaDesde, fechaHasta);
	}
	
	public static LoginCmsResponse buscarLoginCmsResponseVigente() throws SystemException {
		return getInstance().buscarLoginCmsResponseVigente();
	}
	
	public static void insertarLoginCmsResponse(LoginCmsResponse resp, String usuario) throws SystemException {
		getInstance().insertarLoginCmsResponse(resp, usuario);
	}
	@Deprecated
	public static int obtenerProximoNumeroFactura(String ptoVta, String letra) throws SystemException {
		return getInstance().obtenerProximoNumeroFactura(ptoVta, letra);
	}
	
	public static ClaseBase getPtoVtaJurisdiccion(String id) throws SystemException {
		return getInstance().getPtoVtaJurisdiccion(id);
	}
	
	public static Cliente getConfiguracionPtoVta(String id) throws SystemException {
		return getInstance().getConfiguracionPtoVta(id);
	}
	
	public static List<FacturaIngreso> getPagosFacturasPeriodo(Date fechaDesde, Date fechaHasta) throws SystemException {
		return getInstance().getPagosFacturasPeriodo(fechaDesde, fechaHasta);
	}
	
	public static List<Factura> getFacturasPendientesSincronizar() throws SystemException {
		return getInstance().getFacturasPendientesSincronizar() ;
	}
	
	public static Long registraProcesoTransferenciaCentralFactura(Long id, Long idCentral, Date fechaProceso) throws SystemException {
		return getInstance().registraProcesoTransferenciaCentralFactura(id, idCentral, fechaProceso);
	}
	
	public static Cliente getClienteById(Integer idCliente) throws SystemException {
		return getInstance().getClienteById(idCliente);
	}
}
