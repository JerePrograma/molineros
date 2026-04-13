package ar.com.ospim.global.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.empresas.beans.Contacto;
import ar.com.empresas.beans.ReporteEntidadCamaraMasaBean;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.afiliados.empleadores.ImposibleBorrarEmpresaException;
import ar.com.ospim.afiliados.empleadores.index.EmpresasIndex;
import ar.com.ospim.afiliados.services.SeccionalServiceImpl;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.estudioisidro.beans.EstadoGestion;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.estudioisidro.beans.TipoLoteEmpresa;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.RamoEmpresa;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;

/**
 * <a href="BusquedaEmpleadoresServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.BusquedaEmpleadoresServiceImpl</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Martin Moreyra
 * 
 * @see ar.com.ospim.global.services.EmpresaServiceImpl
 * 
 */
public class EmpresaServiceUtil {
	private static Log _log = LogFactoryUtil.getLog(EmpresaServiceUtil.class);

	private static EmpresaServiceImpl instance = null;
	private static SeccionalServiceImpl instanceSeccional = null;

	public static EmpresaServiceImpl getInstance() {
		if (null == instance) {
			instance = new EmpresaServiceImpl();
		}
		return instance;
	}
	
	public static SeccionalServiceImpl getInstanceSeccional() {
		if (null == instanceSeccional) {
			instanceSeccional = new SeccionalServiceImpl();
		}
		return instanceSeccional;
	}

//	public static EstadoGestion getEstadoEmpleador(String cuit) throws Exception {
//		return getInstance().getEstadoEmpleador(cuit);
//	}

	public static List<Empresa> getEmpleadores(String cuit, String descripcion,
			String sucu, int idSeccional) throws Exception {
		return getInstance().getEmpleadores(cuit, descripcion, sucu,
				idSeccional);
	}
	
	public static List<Empresa> getEmpleadores(String cuit, String descripcion,
			String sucu, int idSeccional,Connection con) throws Exception {
		return getInstance().getEmpleadores(cuit, descripcion, sucu, idSeccional, con);
	}

	public static List<Empresa> getEmpleadoresSeguimiento(String cuit,
			String razon, Integer lote, int ramo) throws SQLException {
		return getInstance().getEmpleadoresSeguimiento(cuit, razon, lote, ramo);
	}

	public static Empresa getEmpleadorCompleto(String cuit, String sucu)
			throws Exception {
		return getInstance().getEmpleadorCompleto(cuit, sucu, null);
	}

//	public static Empresa getEmpleadorCompleto(String cuit, String sucu,
//			Connection con) throws Exception {
//		return getInstance().getEmpleadorCompleto(cuit, sucu, con);
//	}

	public static Seccional getSeccionalCompleto(String cuit, int id_seccional)
			throws Exception {
		Seccional s = getInstance().getSeccionalCompleto(cuit, id_seccional);
		Seccional aux =SeccionalServiceUtil.buscarSeccionalById(id_seccional);
		s.setContactos(aux.getContactos());
		return s;
	}

	public static void updateSeccional(Seccional seccional, String username)
			throws Exception {
		
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);

			getInstance().updateSeccional(seccional, con, username);

			// POR AHORA NO ES NECESARIO
			/*
			 * List<Domicilio> domicilios=empresa.getDomicilios(); for(Domicilio
			 * domi: domicilios){
			 * getInstance().saveDomicilioSeccional(empresa.getCuit
			 * (),empresa.getSucursal(), domi, con, username); }
			 *
			 List<Contacto> contactos=empresa.getContactos(); 
			 for(Contacto cont: contactos){
			   getInstance().saveContacto(empresa.getCuit(),empresa
			  .getSucursal(), cont, con, username); 
			 }
			 */
			
			
			//-----------------
			//Contactos		
			Seccional seccionalDB =SeccionalServiceUtil.buscarSeccionalById(seccional.getId());
			if(seccional.getContactos() !=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(Contacto td:seccional.getContactos() ){
					Boolean existe=false;
					for(Contacto tdDB:seccionalDB.getContactos()){
						if( td.getIdContacto()==tdDB.getIdContacto()){
							existe=true;
							break;
						}
					}
					if(!existe){
						getInstanceSeccional().addContacto(seccional.getId(), td, username, con);
					}else{
					 	if(td.getBajaFecha()==null) {
					 		getInstanceSeccional().updateContacto(seccional.getId(), td, username, con);
						}else {
							getInstanceSeccional().deleteContacto(seccional.getId(),td, username, con) ;	
						}
					 }
				}
								
				//Analiza Bajas - apareo con registros existentes en BD
				for(Contacto tdDB:seccionalDB.getContactos() ){
					Boolean existe=false;
					for(Contacto td:seccional.getContactos()){
						if(td.getIdContacto()==tdDB.getIdContacto()){
								existe=true;
								break;
						}
					}
					if(!existe){
					   	   getInstanceSeccional().deleteContacto(seccional.getId(),tdDB, username, con) ;
					}
				}
			  }else{
					if(seccionalDB.getContactos() != null && seccionalDB.getContactos().size()>0){
					   for(Contacto tdDB:seccionalDB.getContactos()){
							 getInstanceSeccional().deleteContacto(seccional.getId(),tdDB, username, con) ;
					   }
			        }
			  }				
			//-----------------				

			
			 con.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}

	}

	public static void save(Empresa empresa, String username)
			throws DuplicateEmpresaIdException, SystemException, SQLException {
		Connection con = null;
		try {
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);

			getInstance().save(empresa, con, username);

			List<Domicilio> domicilios = empresa.getDomicilios();
			if (null != domicilios) {
				for (Domicilio domi : domicilios) {
					if(domi.getEstado() != null){
						getInstance().saveDomicilio(empresa.getCuit(),
								empresa.getSucursal(), domi, con, username);
					}
				}
			}

			List<Contacto> contactos = empresa.getContactos();
			if (null != contactos) {
				for (Contacto cont : contactos) {
					getInstance().saveContacto(empresa.getCuit(),
							empresa.getSucursal(), cont, con, username);
				}
			}

			List<CuentaBancaria> cuentas = empresa.getCuentasBcrias();
			if (null != cuentas) {
				for (CuentaBancaria cuenta : cuentas) {
					getInstance().saveCuentaBancaria(empresa.getCuit(),
							empresa.getSucursal(), cuenta, con, username);
				}
			}

			con.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}

		EmpresasIndex.reindexar(empresa.getCuit(), empresa.getSucursal(),
				empresa.getId_seccional(), empresa.getId_ramo_empresa(),
				empresa.getRazon_soc());
	}

	public static void update(Empresa empresa, String username)
			throws DuplicateEmpresaIdException, SystemException, SQLException {
		
		Connection con = null;
		
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);

			getInstance().update(empresa, con, username);

			List<Domicilio> domicilios = empresa.getDomicilios();
			for (Domicilio domi : domicilios) {
				if(domi.getEstado() != null){
					getInstance().saveDomicilio(empresa.getCuit(),
						empresa.getSucursal(), domi, con, username);
				}	
			}

			List<Contacto> contactos = empresa.getContactos();
			for (Contacto cont : contactos) {
				getInstance().saveContacto(empresa.getCuit(),
						empresa.getSucursal(), cont, con, username);
			}

			List<CuentaBancaria> cuentas = empresa.getCuentasBcrias();
			if (null != cuentas) {
				for (CuentaBancaria cuenta : cuentas) {
					getInstance().saveCuentaBancaria(empresa.getCuit(),
							empresa.getSucursal(), cuenta, con, username);
				}
			}

			con.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}

		EmpresasIndex.reindexar(empresa.getCuit(), empresa.getSucursal(),
				empresa.getId_seccional(), empresa.getId_ramo_empresa(),
				empresa.getRazon_soc());
	}

	public static void borrar(String cuit, String sucu, User user)
			throws ImposibleBorrarEmpresaException, SystemException {
		getInstance().borrar(cuit, sucu, user.getScreenName());
	}

	public static void reactivar(String cuit, String sucu) throws SQLException {
		getInstance().reactivar(cuit, sucu);
	}

	public static void insertarAfiliadoComoEmpresaSiNoExiste(
			String cuil_titular, User user) throws Exception {
		if (getEmpleadorCompleto(cuil_titular, "000") != null) {
			return;
		}
		String razon = getInstance().saveAfiliadoComoEmpresa(cuil_titular,
				user.getScreenName());
		EmpresasIndex.reindexar(cuil_titular, "000", null, 99, razon);
	}

	public static String traerPrestadorDomicilioFiscal(String cuit)
			throws SQLException {

		String domicilio = "";

		try {
			domicilio = getInstance().traerPrestadorDomicilioFiscal(cuit);
		} catch (Exception e) {
			domicilio = "Error al traer domicilio fiscal";
		}

		return domicilio;
	}

	public static void saveCuentasBancaria(Empresa empresa, String username,
			Connection connectionParameter) throws SystemException,
			SQLException, DuplicateEmpresaIdException {

		List<CuentaBancaria> cuentas = null;
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionForTransaction();
			} else {
				con = connectionParameter;
			}

			cuentas = empresa.getCuentasBcrias();
			if (null != cuentas) {
				for (CuentaBancaria cuenta : cuentas) {
					getInstance().saveCuentaBancaria(empresa.getCuit(),
							empresa.getSucursal(), cuenta, con, username);
				}
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
	}

	public static Integer saveCuentaBancaria(String cuit, String sucursal, CuentaBancaria cuenta, String username,
			Connection connectionParameter) throws SystemException,
			SQLException, DuplicateEmpresaIdException {

		Integer idCtaBcriaNueva = 0;
		
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			idCtaBcriaNueva = getInstance().saveCuentaBancaria(cuit,sucursal, cuenta, con, username);

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		return idCtaBcriaNueva;
	}
	
	public static List<CuentaBancaria> getCuentasBancarias(String cuit,
			String sucursal) throws Exception {
		return getInstance().getCuentasBancarias(cuit, sucursal, null);
	}

	public static ReporteEntidadCamaraMasaBean getReporteEntidadCamaraMasa(
			String cuit, String sucursal) throws SQLException {
		return getInstance().getReporteEntidadCamaraMasa(cuit, sucursal);
	}

	public static void buscarDatosEmpresaSeguimientoMolinera(
			LlamadosEstudio seguimiento, PortletRequest renderRequest, String cuitParam) throws SQLException {

		_log.debug("cuitParam: " + cuitParam);
		String cuit = null;
		Connection con = null;
		cuit = ParamUtil.getString(renderRequest, "cuit");
		
		_log.debug("cuit: " + cuit);
		
		if (null == cuit || cuit.trim().length() == 0) {
			renderRequest.getPortletSession().getAttribute("cuit");
			_log.debug("cuit 2: " + cuit);
		}
		
		if(cuit==null|| cuit.trim().length() == 0){
			cuit=cuitParam;
		}
		try {
			con = ConnectionHelper.getConnection();
			
			Empresa empresa = getInstance().getEmpleadorCompleto(cuit, "000", con);
			
			seguimiento.setEmpresa(empresa);
			
			//LLAMADOS
			int size=0;
			int cursor = 0;
			try {
				size = Integer.parseInt((String) renderRequest
						.getAttribute("total"));
			} catch (NumberFormatException nfe) {
				size = 0;
			}

			if (null != renderRequest.getParameter("cur")
					&& !"".equals(renderRequest.getParameter("cur"))) {
				cursor = Integer.parseInt(renderRequest.getParameter("cur"));
			}

			

			try {				
				seguimiento.setLlamados(LlamadoServiceUtil.getLlamadosList(cuit, cursor, con));
				if (size == 0 && null!=seguimiento && null!=seguimiento.getLlamados()&& seguimiento.getLlamados().size()>0) {
					size =seguimiento.getLlamados().get(0).getCantidadTotal();
				}
				
				renderRequest.setAttribute("total", size);
				renderRequest.setAttribute("cur", cursor);			
				/*PortletSession portletSession = renderRequest.getPortletSession();
				portletSession
						.removeAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
				portletSession.setAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO,
						seguimiento);*/			
			} catch (Exception e) {
				_log.error(e);
				// return mapping.findForward("portlet.estudioisidro.error");
			}
			

			try {
				if (size == 0 && null!=seguimiento && null!=seguimiento.getLlamados()&& seguimiento.getLlamados().size()>0) {
					size =seguimiento.getLlamados().get(0).getCantidadTotal();
				}
				
				renderRequest.setAttribute("total", size);
				renderRequest.setAttribute("cur", cursor);
				
				LlamadoServiceUtil.traeTotalesSeguimiento(seguimiento, con);
				
			} catch (Exception e) {
				_log.error(e);
				// return mapping.findForward("portlet.estudioisidro.error");
			}

		} finally {
			ConnectionHelper.cerrar(con);
		}

	}
	
	public static List<EstadoGestion> getEstadosEmpresa() {
		
		return getInstance().getEstadosEmpresa();
	}
	
	public static List<TipoLoteEmpresa> getTiposLoteEmpresa() {
		
		return getInstance().getTiposLoteEmpresa();
	}
	
	public static List<Regimen> getRegimenesRetencionGanancias() {
		
		return getInstance().getRegimenesRetencionGanancias();
	}
	
	public static List<Regimen> getRegimenesRetencionGanancias(RenderRequest portletRequest) {
		
		List<Regimen> regimenes = (List<Regimen>) portletRequest.getPortletSession().getAttribute(
						WebKeysEmpresas.REGIMENES_RET_GANANCIAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (regimenes == null) {
			regimenes = getInstance().getRegimenesRetencionGanancias();
					portletRequest.getPortletSession().setAttribute(
				WebKeysEmpresas.REGIMENES_RET_GANANCIAS_EN_SESSION, regimenes,
				PortletSession.APPLICATION_SCOPE);
		}
		
		return regimenes;
		
	}
	
	public static void updateCBU(Empresa empresa, String username)
			throws DuplicateEmpresaIdException, SystemException, SQLException {
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			getInstance().updateCBU(empresa, con, username);
			con.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	
}
