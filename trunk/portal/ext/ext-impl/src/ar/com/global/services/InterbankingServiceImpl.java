package ar.com.global.services;

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
import org.objectweb.asm.Type;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Contenido;
import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.exceptions.EmailYaRegistradoException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class InterbankingServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(InterbankingServiceImpl.class);

	public List<OrdenPago> getOrdenesPago(Integer opDde,
			Integer opHta,  Date fechaDesde,
			Date fechaHasta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> lista = null;
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call interbanking_ordenes_pago_amtima(?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.interbanking_ordenes_pago(?, ?, ?, ?)}";
			}else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call interbanking_ordenes_pago_ospim(?, ?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (opDde == null) {
				stmt.setNull(1, Type.INT);
			} else {
				stmt.setInt(1, opDde);
			}
			if (opHta == null) {
				stmt.setNull(2, Type.INT);
			} else {
				stmt.setInt(2, opHta);
			}

			if (null != fechaDesde) {
				stmt.setDate(3, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}

			if (null != fechaHasta) {
				stmt.setDate(4, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(4, Types.DATE);
			}


			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPago>();
			while (rs.next()) {
				OrdenPago op = OrdenPago.getMapping(rs, "op__");
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				lista.add(op);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago Interbanking", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	
	public Boolean deleteOrdenesPago(String ops, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> lista = null;
		String sql = null;
		Boolean ret=true;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call interbanking_ordenes_pago_delete_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.interbanking_ordenes_pago_delete(?)}";
			}else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call interbanking_ordenes_pago_delete_ospim(?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ops);
			
			ResultSet rs = stmt.executeQuery();
		} catch (Exception e) {
			_log.error("Error al blanquear ordenes pago Interbanking", e);
			ret=false;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

}
