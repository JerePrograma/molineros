package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.CotizacionPrestadorDiagnostico;
import ar.com.ospim.compras.requerimientos.beans.FinalizacionCotizacionPrestador;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.ReservaCotizacionPrestador;
import ar.com.ospim.util.ConnectionHelper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia del proceso de notificación de cotizaciones.
 *
 * Esta clase no genera PDF, no accede a Document Library, no valida emails,
 * no construye correos y no decide estados funcionales.
 */
public class NotificarCotizacionPrestadorServiceImpl {

    private static final String SQL_LISTAR_CANDIDATOS =
            "{call compras.listar_prestadores_notificacion_cotizacion(?)}";

    private static final String SQL_DIAGNOSTICAR_CANDIDATOS =
            "{call compras.diagnosticar_prestadores_notificacion_cotizacion(?)}";

    private static final String SQL_RESERVAR =
            "{call compras.reservar_notificacion_cotizacion_prestador(?,?,?)}";

    private static final String SQL_FINALIZAR =
            "{call compras.finalizar_notificacion_cotizacion_prestador(?,?,?,?,?)}";

    public List<PrestadorCotizacion> listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<PrestadorCotizacion> resultado =
                new ArrayList<PrestadorCotizacion>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_CANDIDATOS);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPrestadorCotizacion(rs));
            }

            return resultado;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public CotizacionPrestadorDiagnostico diagnosticarPrestadores(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_DIAGNOSTICAR_CANDIDATOS);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            CotizacionPrestadorDiagnostico diagnostico =
                    new CotizacionPrestadorDiagnostico();

            diagnostico.setPrestadoresHabilitados(
                    rs.getInt("prestadores_habilitados")
            );
            diagnostico.setPrestadoresCompatiblesSector(
                    rs.getInt("prestadores_compatibles_sector")
            );
            diagnostico.setPrestadoresBloqueadosEstadoPrevio(
                    rs.getInt("prestadores_bloqueados_estado_previo")
            );

            return diagnostico;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public ReservaCotizacionPrestador reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_RESERVAR);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idPrestador);
            stmt.setString(3, usuario);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            ReservaCotizacionPrestador reserva =
                    new ReservaCotizacionPrestador();

            reserva.setReservado(rs.getBoolean("reservado"));
            reserva.setEstadoEnvio(rs.getString("estado_envio"));
            reserva.setEmailDestino(rs.getString("email_destino"));
            reserva.setMotivoCodigo(rs.getString("motivo_codigo"));
            reserva.setMotivoDescripcion(rs.getString("motivo_descripcion"));

            return reserva;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public FinalizacionCotizacionPrestador finalizarCotizacionPrestador(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_FINALIZAR);
            stmt.setInt(1, idRequerimiento);
            stmt.setInt(2, idPrestador);
            stmt.setString(3, estado);
            stmt.setString(4, error);
            stmt.setString(5, usuario);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            FinalizacionCotizacionPrestador finalizacion =
                    new FinalizacionCotizacionPrestador();

            finalizacion.setActualizado(rs.getBoolean("actualizado"));
            finalizacion.setEstadoAnterior(rs.getString("estado_anterior"));
            finalizacion.setEstadoActual(rs.getString("estado_actual"));
            finalizacion.setMotivo(rs.getString("motivo"));

            return finalizacion;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private PrestadorCotizacion mapPrestadorCotizacion(
            ResultSet rs) throws Exception {

        PrestadorCotizacion prestador = new PrestadorCotizacion();
        prestador.setIdPrestador(rs.getInt("id_prestador"));
        prestador.setDescripcion(rs.getString("descripcion"));
        prestador.setCuit(rs.getString("cuit"));
        prestador.setEmail(rs.getString("email"));
        prestador.setIdTipoPrestador(rs.getInt("id_tipo_prestador"));
        prestador.setTipoPrestador(rs.getString("tipo_prestador"));
        return prestador;
    }

    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }
}
