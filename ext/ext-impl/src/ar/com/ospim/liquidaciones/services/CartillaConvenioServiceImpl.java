package ar.com.ospim.liquidaciones.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final Log log = LogFactoryUtil.getLog(CartillaConvenioServiceImpl.class);

    private static final String SQL_BUSCAR_CARTILLA =
            "SELECT * FROM convenio_prest.buscar_cartilla_convenio_por_plan(?,?,?,?,?,?,?,?)";

    public List<CartillaConvenioRow> buscarCartillaConvenioPorPlan(
            BusquedaCartillaConvenioFiltro filtro) throws SystemException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<CartillaConvenioRow> lista = new ArrayList<CartillaConvenioRow>();
        BusquedaCartillaConvenioFiltro filtroNormalizado = normalizarFiltro(filtro);

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_BUSCAR_CARTILLA);

            setNullableInteger(stmt, 1, filtroNormalizado.getIdPlan());
            setNullableInteger(stmt, 2, filtroNormalizado.getIdPrestador());
            setNullableString(stmt, 3, filtroNormalizado.getCuitPrestador());
            setNullableString(stmt, 4, filtroNormalizado.getPrestadorDescripcion());
            setNullableInteger(stmt, 5, filtroNormalizado.getIdProvincia());
            setNullableInteger(stmt, 6, filtroNormalizado.getIdLocalidad());
            setNullableInteger(stmt, 7, filtroNormalizado.getIdEspecialidad());
            stmt.setBoolean(8, filtroNormalizado.isIncluyeBajas());

            if (log.isDebugEnabled()) {
                log.debug("[CARTILLA-CONV][SEARCH][PARAMS] " +
                        "idPlan=" + filtroNormalizado.getIdPlan() +
                        ", idPrestador=" + filtroNormalizado.getIdPrestador() +
                        ", cuitPrestador=" + filtroNormalizado.getCuitPrestador() +
                        ", prestadorDescripcion=" + filtroNormalizado.getPrestadorDescripcion() +
                        ", idProvincia=" + filtroNormalizado.getIdProvincia() +
                        ", idLocalidad=" + filtroNormalizado.getIdLocalidad() +
                        ", idEspecialidad=" + filtroNormalizado.getIdEspecialidad() +
                        ", incluyeBajas=" + filtroNormalizado.isIncluyeBajas());
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(CartillaConvenioRow.getMapping(rs, "cart_"));
            }

            log.info("[CARTILLA-CONV][SEARCH][OK] resultados=" + lista.size());
            return lista;

        } catch (Exception e) {
            log.error("[CARTILLA-CONV][SEARCH][ERROR] Error al buscar cartilla de convenios por plan", e);
            throw new SystemException(e);
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private BusquedaCartillaConvenioFiltro normalizarFiltro(BusquedaCartillaConvenioFiltro filtro) {
        BusquedaCartillaConvenioFiltro normalizado =
                (filtro != null) ? filtro : new BusquedaCartillaConvenioFiltro();

        normalizado.setIdPlan(normalizarEnteroPositivo(normalizado.getIdPlan()));
        normalizado.setIdPrestador(normalizarEnteroPositivo(normalizado.getIdPrestador()));
        normalizado.setIdProvincia(normalizarEnteroPositivo(normalizado.getIdProvincia()));
        normalizado.setIdLocalidad(normalizarEnteroPositivo(normalizado.getIdLocalidad()));
        normalizado.setIdEspecialidad(normalizarEnteroPositivo(normalizado.getIdEspecialidad()));

        normalizado.setCuitPrestador(normalizarTexto(normalizado.getCuitPrestador()));
        normalizado.setPrestadorDescripcion(normalizarTexto(normalizado.getPrestadorDescripcion()));

        if (normalizado.getPagina() <= 0) {
            normalizado.setPagina(1);
        }

        return normalizado;
    }

    private Integer normalizarEnteroPositivo(Integer value) {
        return (value != null && value.intValue() > 0) ? value : null;
    }

    private String normalizarTexto(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.length() > 0 ? trimmed : null;
    }

    private void setNullableInteger(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value.intValue());
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }

    private void setNullableString(PreparedStatement stmt, int index, String value) throws SQLException {
        if (value != null) {
            stmt.setString(index, value);
        } else {
            stmt.setNull(index, Types.VARCHAR);
        }
    }

    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                log.warn("[CARTILLA-CONV][SEARCH][WARN] Error cerrando ResultSet", e);
            }
        }
    }
}