package ar.com.uoma.proveedores.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hsqldb.Types;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.Proveedor;

public class ProveedoresServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(ProveedoresServiceImpl.class);

	private static ProveedoresServiceImpl instance = null;

	public static ProveedoresServiceImpl getInstance() {
		if (null == instance) {
			instance = new ProveedoresServiceImpl();
		}
		return instance;
	}

	public List<Proveedor> getProveedores(String cuit, String sucursal,String razonSocial,Integer id) {
		_log.debug("buscando Proveedores");

		Connection con = null;
		CallableStatement stmt = null;
		List<Proveedor> proveedores = null;
//		Proveedor par = null;
		try {
			String sql = "{call uoma.trae_proveedores(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (cuit != null) {
				stmt.setString(1, cuit);
			}else {
				stmt.setNull(1,  Types.VARCHAR);
			}
			
			if (sucursal != null) {
				stmt.setString(2, sucursal);
			}else {
				stmt.setNull(2,  Types.VARCHAR);
			}
		 
			if (razonSocial != null) {
				stmt.setString(3, razonSocial);
			}else {
				stmt.setNull(3,  Types.VARCHAR);
			}
			
			if (id != null) {
				stmt.setInt(4, id);
			}else {
				stmt.setNull(4,  Types.INTEGER);
			}
			
					
			ResultSet rs = stmt.executeQuery();
			proveedores = new ArrayList<Proveedor>();
			while (rs.next()) {
				Proveedor prv = Proveedor.getMapping(rs, "");
				proveedores.add(prv);
			}
		} catch (Exception e) {
			_log.error("Error al traer proveedores ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar proveedores");
		return proveedores;
	}
	
	
	public Integer insertaProveedor(Proveedor p, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_proveedor = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call uoma.proveedores_insertar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			if(StringUtils.checkNotEmpty(p.getCuit())) {
				stmt.setString(1,p.getCuit());
			}else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if(StringUtils.checkNotEmpty(p.getSucursal())) {
				stmt.setString(2,p.getSucursal());
			}else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if(StringUtils.checkNotEmpty(p.getRazon_soc())) {
				stmt.setString(3,p.getRazon_soc());
			}else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if(p.getImpIva()!=null) {
				stmt.setString(4,p.getImpIva());
			}else {
				stmt.setNull(4, Types.VARCHAR);
			}
			
			if(p.getMonotributo()!=null && !"".equalsIgnoreCase(p.getMonotributo())) {
				stmt.setString(5,p.getMonotributo());
			}else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			stmt.setBoolean(6, p.isAgenteRetencion());
			
			if(p.getRegimen()!=null && p.getRegimen().getCodigoRegimen()!=null) {
				stmt.setInt(7, p.getRegimen().getCodigoRegimen());
			}else {
				stmt.setNull(7, Types.INTEGER);
			}
			
			
			if(p.getActividadPrincipal()!=null && p.getActividadPrincipal().getCodigo()>0) {
				stmt.setInt(8,p.getActividadPrincipal().getCodigo());
			}else {
				stmt.setNull(8, Types.INTEGER);
			}
			
			if(p.getActividadSecundaria()!=null && p.getActividadSecundaria().getCodigo()>0) {
				stmt.setInt(9,p.getActividadSecundaria().getCodigo());
			}else {
				stmt.setNull(9, Types.INTEGER);
			}

			if(p.getFormaPago()!=null) {
				stmt.setString(10, p.getFormaPago());
			}else {
				stmt.setNull(10, Types.VARCHAR);
			}
			
			if(p.getCuentaBcria()!=null && p.getCuentaBcria().getBanco() !=null && p.getCuentaBcria().getBanco().getId_banco()>0) {
				stmt.setInt(11,  p.getCuentaBcria().getBanco().getId_banco());
			}else {
				stmt.setNull(11, Types.INTEGER);
			}
			
			
			if(StringUtils.checkNotEmpty(p.getCuentaBcria().getDescripcion())) {
				stmt.setString(12, p.getCuentaBcria().getDescripcion());
			}else {
			    stmt.setNull(12, Types.VARCHAR);	
			}
			
			if( StringUtils.checkNotEmpty(p.getCuentaBcria().getDescripcion()) 
					&& StringUtils.checkNotEmpty(p.getCuentaBcria().getCBU())) {
				stmt.setString(13, p.getCuentaBcria().getCBU());
			}else {
				stmt.setNull(13, Types.VARCHAR);
			}
			
			
			if(p.getDomicilio()!=null && p.getDomicilio().getId_domicilio()>0) {
				stmt.setInt(14,p.getDomicilio().getId_domicilio());
			}else {
				stmt.setNull(14, Types.INTEGER);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getProvincia()!=null &&  p.getDomicilio().getProvincia().getId()>0) {
				stmt.setInt(15, p.getDomicilio().getProvincia().getId());
			}else {
				stmt.setNull(15,Types.INTEGER);
			}

			if(p.getDomicilio()!=null && p.getDomicilio().getLocalidad()!=null &&  p.getDomicilio().getLocalidad().getId()>0) {
				stmt.setInt(16, p.getDomicilio().getLocalidad().getId());
			}else {
				stmt.setNull(16,Types.INTEGER);
			}

			if(p.getDomicilio()!=null && p.getDomicilio().getPostal_codi() !=null) {
				stmt.setString(17,p.getDomicilio().getPostal_codi() );
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(p.getDomicilio()!=null && StringUtils.checkNotEmpty(p.getDomicilio().getCalle())) {
				stmt.setString(18,p.getDomicilio().getCalle() );
			}else {
				stmt.setNull(18,Types.VARCHAR);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getNumero() !=null) {
				stmt.setString(19,p.getDomicilio().getNumero() );
			}else {
				stmt.setNull(19,Types.VARCHAR);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getPiso() !=null) {
				stmt.setString(20,p.getDomicilio().getPiso() );
			}else {
				stmt.setNull(20,Types.VARCHAR);
			}
			
			
			if(p.getDomicilio()!=null && p.getDomicilio().getDepto() !=null) {
				stmt.setString(21,p.getDomicilio().getDepto() );
			}else {
				stmt.setNull(21,Types.VARCHAR);
			}
			
			if(p.getContactosElectronicos()!=null && p.getContactosElectronicos().size()>0) {
				stmt.setString(22, p.getContactosElectronicos().get(0).getContacto());
			}else {
				stmt.setNull(22, Types.VARCHAR);
			}
			
			stmt.setString(23, screenName);
			
			if(p.getCuentaBcria()!= null && p.getCuentaBcria().getId_cuenta_bcria()>0) {
				stmt.setInt(24, p.getCuentaBcria().getId_cuenta_bcria());
			}else {
				stmt.setNull(24, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_proveedor = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			_log.error("Error al insertar Proveedor", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_proveedor;
	}


	public Integer updateProveedor(Proveedor p, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_proveedor = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call uoma.proveedores_update(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			if(p.getCuit()!=null) {
				stmt.setString(1,p.getCuit());
			}else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if(p.getSucursal()!=null) {
				stmt.setString(2,p.getSucursal());
			}else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if(p.getRazon_soc()!=null) {
				stmt.setString(3,p.getRazon_soc());
			}else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if(p.getImpIva()!=null) {
				stmt.setString(4,p.getImpIva());
			}else {
				stmt.setNull(4, Types.VARCHAR);
			}
			
			if(p.getMonotributo()!=null && !"".equalsIgnoreCase(p.getMonotributo())) {
				stmt.setString(5,p.getMonotributo());
			}else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			stmt.setBoolean(6, p.isAgenteRetencion());
			
			if(p.getRegimen()!=null && p.getRegimen().getCodigoRegimen()!=null) {
				stmt.setInt(7, p.getRegimen().getCodigoRegimen());
			}else {
				stmt.setNull(7, Types.INTEGER);
			}
			
			
			if(p.getActividadPrincipal()!=null && p.getActividadPrincipal().getCodigo()>0) {
				stmt.setInt(8,p.getActividadPrincipal().getCodigo());
			}else {
				stmt.setNull(8, Types.INTEGER);
			}
			
			if(p.getActividadSecundaria()!=null && p.getActividadSecundaria().getCodigo()>0) {
				stmt.setInt(9,p.getActividadSecundaria().getCodigo());
			}else {
				stmt.setNull(9, Types.INTEGER);
			}

			if(p.getFormaPago()!=null) {
				stmt.setString(10, p.getFormaPago());
			}else {
				stmt.setNull(10, Types.VARCHAR);
			}
			
			if(p.getCuentaBcria()!=null && p.getCuentaBcria().getBanco() !=null && p.getCuentaBcria().getBanco().getId_banco()>0) {
				stmt.setInt(11,  p.getCuentaBcria().getBanco().getId_banco());
			}else {
				stmt.setNull(11, Types.INTEGER);
			}
			
			
			if(p.getCuentaBcria().getDescripcion()!=null) {
				stmt.setString(12, p.getCuentaBcria().getDescripcion());
			}else {
			    stmt.setNull(12, Types.VARCHAR);	
			}
			
			if(p.getCuentaBcria()!=null && p.getCuentaBcria().getCBU() !=null) {
				stmt.setString(13, p.getCuentaBcria().getCBU());
			}else {
				stmt.setNull(13, Types.VARCHAR);
			}
			
			
			if(p.getDomicilio()!=null && p.getDomicilio().getId_domicilio()>0) {
				stmt.setInt(14,p.getDomicilio().getId_domicilio());
			}else {
				stmt.setNull(14, Types.INTEGER);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getProvincia()!=null &&  p.getDomicilio().getProvincia().getId()>0) {
				stmt.setInt(15, p.getDomicilio().getProvincia().getId());
			}else {
				stmt.setNull(15,Types.INTEGER);
			}

			if(p.getDomicilio()!=null && p.getDomicilio().getLocalidad()!=null &&  p.getDomicilio().getLocalidad().getId()>0) {
				stmt.setInt(16, p.getDomicilio().getLocalidad().getId());
			}else {
				stmt.setNull(16,Types.INTEGER);
			}

			if(p.getDomicilio()!=null && p.getDomicilio().getPostal_codi() !=null) {
				stmt.setString(17,p.getDomicilio().getPostal_codi() );
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getCalle() !=null) {
				stmt.setString(18,p.getDomicilio().getCalle() );
			}else {
				stmt.setNull(18,Types.VARCHAR);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getNumero() !=null) {
				stmt.setString(19,p.getDomicilio().getNumero() );
			}else {
				stmt.setNull(19,Types.VARCHAR);
			}
			
			if(p.getDomicilio()!=null && p.getDomicilio().getPiso() !=null) {
				stmt.setString(20,p.getDomicilio().getPiso() );
			}else {
				stmt.setNull(20,Types.VARCHAR);
			}
			
			
			if(p.getDomicilio()!=null && p.getDomicilio().getDepto() !=null) {
				stmt.setString(21,p.getDomicilio().getDepto() );
			}else {
				stmt.setNull(21,Types.VARCHAR);
			}
			
			if(p.getContactosElectronicos()!=null && p.getContactosElectronicos().size()>0) {
				stmt.setString(22, p.getContactosElectronicos().get(0).getContacto());
			}else {
				stmt.setNull(22, Types.VARCHAR);
			}
			
			stmt.setString(23, screenName);
			
			stmt.setInt(24,p.getId());
			
			if(p.getCuentaBcria()!= null && p.getCuentaBcria().getId_cuenta_bcria()>0) {
				stmt.setInt(25, p.getCuentaBcria().getId_cuenta_bcria());
			}else {
				stmt.setNull(25, Types.INTEGER);
			}


			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_proveedor = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			_log.error("Error al insertar Proveedor", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_proveedor;
	}

	
	
}
