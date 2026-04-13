package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.BusquedaCartillaConvenioFiltro;
import ar.com.ospim.liquidaciones.beans.CartillaConvenioRow;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class CartillaConvenioServiceImpl {

    private static final Log _log = LogFactoryUtil.getLog(CartillaConvenioServiceImpl.class);

    public List<CartillaConvenioRow> buscarCartillaConvenioPorPlan(
            BusquedaCartillaConvenioFiltro filtro) throws SystemException {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        List<CartillaConvenioRow> lista = new ArrayList<CartillaConvenioRow>();

        try {
            if (filtro == null) {
                filtro = new BusquedaCartillaConvenioFiltro();
            }

            String sql = "{call convenio_prest.buscar_cartilla_convenio_por_plan(?,?,?,?,?,?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            setNullableInteger(stmt, 1, filtro.getIdPlan());
            setNullableInteger(stmt, 2, filtro.getIdPrestador());
            setNullableString(stmt, 3, filtro.getPrestadorDescripcion());
            setNullableInteger(stmt, 4, filtro.getIdProvincia());
            setNullableInteger(stmt, 5, filtro.getIdLocalidad());
            setNullableInteger(stmt, 6, filtro.getIdEspecialidad());
            setNullableString(stmt, 7, filtro.getInstitucion());
            stmt.setBoolean(8, filtro.isIncluyeBajas());

            _log.debug("[CARTILLA-CONV][SEARCH][PARAMS] filtro=" + filtro);

            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(CartillaConvenioRow.getMapping(rs, "cart_"));
            }

            _log.info("[CARTILLA-CONV][SEARCH][OK] resultados=" + lista.size());
            return lista;

        } catch (Exception e) {
            _log.error("Error al buscar cartilla de convenios por plan", e);
            throw new SystemException(e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception e) { _log.warn(e.getMessage(), e); }
            }
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value != null && value.intValue() > 0) {
            stmt.setInt(index, value.intValue());
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }

    private void setNullableString(CallableStatement stmt, int index, String value) throws Exception {
        if (value != null && value.trim().length() > 0) {
            stmt.setString(index, value.trim());
        } else {
            stmt.setNull(index, Types.VARCHAR);
        }
    }
}