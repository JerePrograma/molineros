package ar.com.uoma.cuentacorrienteempresa.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import ar.com.uoma.beans.CuentaCorrienteEmpresa;

public class CuentaCorrienteEmpresaServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(CuentaCorrienteEmpresaServiceImpl.class);

	private static CuentaCorrienteEmpresaServiceImpl instance = null;

	public static CuentaCorrienteEmpresaServiceImpl getInstance() {
		if (null == instance) {
			instance = new CuentaCorrienteEmpresaServiceImpl();
		}
		return instance;
	}

	public List<CuentaCorrienteEmpresa> getCuentaCorriente(String cuit, String sucursal, 
			Date fechaDesde, Date fechaHasta, 
			Boolean procesarConsulta, int soloHeader,
			int tipoBoleta, int qrySoloUoma, int qrySoloAmtima,
			int nro_vista, String periodo, int pagina) {
		_log.debug("buscando Cuenta Corriente");

		Connection con = null;
		CallableStatement stmt = null;
		List<CuentaCorrienteEmpresa> ctacte = null;

		try {
			String sql = "";
			
			if (nro_vista == 0) {
				sql = "{call public.saldos_vista_0(?,?,?,?,?,?,?,?,?)}";
			} else if (nro_vista == 1) {
				sql = "{call public.saldos_vista_1(?,?,?,?,?,?,?)}";				
			} else if (nro_vista == 2) {
				if (pagina == 0) {
					// Es exportacion a XLS, utiliza Proc sin Paginacion
					sql = "{call public.saldos_vista_2(?,?,?,?,?,?,?,?)}";
				} else {
					// Es vista de pantalla utiliza Proc con Paginacion
					sql = "{call public.saldos_vista_2(?,?,?,?,?,?,?,?,?)}";	
				}
				
			}

			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
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

			if (fechaDesde != null) {
				stmt.setDate(3, new java.sql.Date(fechaDesde.getTime()));
			}else {
				stmt.setNull(3, Types.DATE);
			}			
			
			if (fechaHasta != null) {
				stmt.setDate(4, new java.sql.Date(fechaHasta.getTime()));
			}else {
				stmt.setNull(4, Types.DATE);
			}
			// Sumarizado por Cuit
			stmt.setInt(5, soloHeader);
			// Solo Uoma
			stmt.setInt(6, qrySoloUoma);				
			// Solo Amtima
			stmt.setInt(7, qrySoloAmtima);		
			// Procesar estado de Cuenta corriente, solo para vista 0
			if (nro_vista == 0) {
				if (procesarConsulta){
					stmt.setInt(8, 1);
				} else {
					stmt.setInt(8, 0);
				} 
				
				stmt.setInt(9, pagina);
			}
 
			if ((nro_vista == 2) || (nro_vista == 3)) {
				stmt.setInt(8, tipoBoleta);
			}
			
			if (nro_vista == 2) {
				if (pagina > 0) {
					stmt.setInt(9, pagina);
				}
			}
						
			if (nro_vista == 3) {
				stmt.setString(9, periodo);
			}
			
			ResultSet rs = stmt.executeQuery();
			ctacte = new ArrayList<CuentaCorrienteEmpresa>();
			int i = 0;
			while (rs.next()) {
				CuentaCorrienteEmpresa _cta = CuentaCorrienteEmpresa.getMapping(i, rs, "r_");
				ctacte.add(_cta);
				i++;
			}
		} catch (Exception e) {
			_log.error("Error al traer Cuenta Corriente ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar Cuenta Corriente");
		return ctacte;
	}
}
