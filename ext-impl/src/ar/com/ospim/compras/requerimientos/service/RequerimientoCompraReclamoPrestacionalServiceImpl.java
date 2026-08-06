package ar.com.ospim.compras.requerimientos.service;

import com.liferay.portal.SystemException;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import com.liferay.portal.model.User;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RequerimientoCompraReclamoPrestacionalServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    RequerimientoCompraReclamoPrestacionalServiceImpl.class
            );

    private static final String SQL_GET_RELACION =
            "SELECT * "
                    + "FROM compras.get_requerimiento_reclamo_prestacional(?)";

    private static final String SQL_RESERVAR =
            "SELECT compras.reservar_reclamo_prestacional(?,?,?)";

    private static final String SQL_FINALIZAR =
            "SELECT compras.finalizar_reclamo_prestacional(?,?,?,?)";

    private static final String SQL_LIBERAR =
            "SELECT compras.liberar_reserva_reclamo_prestacional(?,?,?)";

    private static final String SQL_MARCAR_ERROR =
            "SELECT compras.marcar_error_reclamo_prestacional(?,?,?,?,?)";

    /*
     * Namespace ASCII "RCP" utilizado exclusivamente para serializar
     * la creación del Reclamo Prestacional de un requerimiento.
     */
    private static final int ADVISORY_LOCK_NAMESPACE =
            5391184;

    private static final String SQL_BLOQUEAR_REQUERIMIENTO =
            "SELECT pg_advisory_xact_lock(?,?)";

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_RELACION);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() ? mapRelacion(rs) : null;
        } catch (Exception e) {
            _log.error(
                    "No se pudo consultar la relación entre el requerimiento "
                            + "de compra y el Reclamo Prestacional. "
                            + "idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean liberarReserva(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        return ejecutarBoolean(
                SQL_LIBERAR,
                new Parametrizador() {
                    public void parametrizar(PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(1, idRequerimientoCompra);
                        stmt.setString(2, tokenReserva);
                        stmt.setString(3, normalizarUsuario(usuario));
                    }
                },
                "No se pudo liberar la reserva del Reclamo Prestacional."
        );
    }

    public boolean marcarErrorPosteriorAlInsert(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final int idReclamoPrestacional,
            final String error,
            final String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        return ejecutarBoolean(
                SQL_MARCAR_ERROR,
                new Parametrizador() {
                    public void parametrizar(PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(1, idRequerimientoCompra);
                        stmt.setString(2, tokenReserva);
                        stmt.setInt(3, idReclamoPrestacional);
                        stmt.setString(4, limitarError(error));
                        stmt.setString(5, normalizarUsuario(usuario));
                    }
                },
                "No se pudo registrar el error de vinculación "
                        + "del Reclamo Prestacional."
        );
    }

    protected boolean ejecutarBoolean(
            String sql,
            Parametrizador parametrizador,
            String mensajeError) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(sql);
            parametrizador.parametrizar(stmt);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception(mensajeError);
            }

            boolean resultado = rs.getBoolean(1);

            if (!resultado) {
                throw new Exception(mensajeError);
            }

            return true;
        } catch (Exception e) {
            _log.error(mensajeError, e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private RequerimientoCompraReclamoPrestacional mapRelacion(ResultSet rs)
            throws Exception {

        RequerimientoCompraReclamoPrestacional relacion =
                new RequerimientoCompraReclamoPrestacional();

        relacion.setIdRequerimientoCompra(
                rs.getInt("id_requerimiento")
        );

        int idReclamo = rs.getInt("id_reclamo_prestacional");
        relacion.setIdReclamoPrestacional(
                rs.wasNull() ? null : Integer.valueOf(idReclamo)
        );

        relacion.setEstado(rs.getString("estado"));
        relacion.setTokenReserva(rs.getString("token_reserva"));
        relacion.setReservaFecha(rs.getTimestamp("reserva_fecha"));
        relacion.setUltimoError(rs.getString("ultimo_error"));
        relacion.setAltaFecha(rs.getTimestamp("alta_fecha"));
        relacion.setAltaUsr(rs.getString("alta_usr"));
        relacion.setModiFecha(rs.getTimestamp("modi_fecha"));
        relacion.setModiUsr(rs.getString("modi_usr"));

        return relacion;
    }

    private void validarIdRequerimiento(int idRequerimientoCompra)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }
    }

    private void validarToken(String tokenReserva) throws Exception {
        if (WebKeysCompras.isEmpty(tokenReserva)) {
            throw new Exception(
                    "No se pudo validar el contexto de creación "
                            + "del Reclamo Prestacional."
            );
        }
    }

    private String normalizarUsuario(String usuario) {
        return WebKeysCompras.isEmpty(usuario)
                ? "sistema"
                : usuario.trim();
    }

    private String limitarError(String error) {
        String value = WebKeysCompras.trimToNull(error);

        if (value == null) {
            return "Error de vinculación no especificado.";
        }

        return value.length() <= 2000
                ? value
                : value.substring(0, 2000);
    }

    private void closeQuietly(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }

    protected interface Parametrizador {
        void parametrizar(PreparedStatement stmt) throws Exception;
    }

    public void reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        validarToken(
                tokenReserva
        );

        Connection con =
                null;

        try {
            con =
                    ConnectionHelper
                            .getConnectionForTransaction();

            bloquearRequerimiento(
                    con,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            /*
             * Una repetición posterior a un guardado exitoso se considera
             * satisfecha. crearYVincular devolverá el mismo ID persistido.
             */
            if (relacion != null
                    && relacion.isVinculado()) {

                con.commit();

                return;
            }

            /*
             * Una reserva propia con el mismo token también es un resultado
             * idempotente. No se vuelve a ejecutar la función SQL.
             */
            if (relacion != null) {
                validarReservaCompatible(
                        relacion,
                        tokenReserva,
                        usuario
                );

                con.commit();

                return;
            }

            reservarCreacion(
                    con,
                    idRequerimientoCompra,
                    tokenReserva,
                    usuario
            );

            relacion =
                    obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            validarReservaCompatible(
                    relacion,
                    tokenReserva,
                    usuario
            );

            con.commit();

        } catch (Exception e) {
            ConnectionHelper.rollback(
                    con
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(
                    con
            );
        }
    }

    protected boolean ejecutarBoolean(
            Connection con,
            String sql,
            Parametrizador parametrizador,
            String mensajeError) throws Exception {

        if (con == null) {
            throw new Exception(
                    "No se informó la conexión transaccional."
            );
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt =
                    con.prepareStatement(
                            sql
                    );

            parametrizador.parametrizar(
                    stmt
            );

            rs =
                    stmt.executeQuery();

            if (!rs.next()
                    || !rs.getBoolean(1)) {

                throw new Exception(
                        mensajeError
                );
            }

            return true;

        } finally {
            closeQuietly(
                    rs
            );

            ConnectionHelper.cerrar(
                    stmt
            );
        }
    }

    public int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user) throws Exception {

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        validarToken(
                tokenReserva
        );

        if (reclamo == null) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional "
                            + "que se desea crear."
            );
        }

        Connection con =
                null;

        String usuario =
                user != null
                        ? user.getScreenName()
                        : "sistema";

        try {
            con =
                    ConnectionHelper
                            .getConnectionForTransaction();

            /*
             * Serializa todos los intentos del mismo requerimiento.
             *
             * Una segunda petición debe esperar a que la primera confirme
             * o revierta antes de decidir si inserta.
             */
            bloquearRequerimiento(
                    con,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            /*
             * Doble clic, reenvío del navegador o repetición del SAVE:
             * si ya existe el vínculo definitivo, se devuelve el mismo ID
             * sin insertar otra cabecera.
             */
            if (relacion != null
                    && relacion.isVinculado()) {

                int idReclamoExistente =
                        relacion.getIdReclamoPrestacionalInt();

                if (idReclamoExistente <= 0) {
                    throw new Exception(
                            "La relación figura vinculada, pero no contiene "
                                    + "un identificador válido de Reclamo "
                                    + "Prestacional."
                    );
                }

                con.commit();

                return idReclamoExistente;
            }

            /*
             * Normalmente la reserva ya fue creada al iniciar el flujo desde
             * Compras. Se conserva la posibilidad de crearla aquí para mantener
             * compatibilidad con invocaciones anteriores.
             */
            if (relacion == null) {
                reservarCreacion(
                        con,
                        idRequerimientoCompra,
                        tokenReserva,
                        usuario
                );

                relacion =
                        obtenerPorRequerimiento(
                                con,
                                idRequerimientoCompra
                        );
            }

            validarReservaCompatible(
                    relacion,
                    tokenReserva,
                    usuario
            );

            int idReclamo =
                    new ReclamoPrestacionServiceImpl()
                            .insertar(
                                    con,
                                    reclamo,
                                    user
                            );

            if (idReclamo <= 0) {
                throw new Exception(
                        "La inserción no devolvió un identificador "
                                + "válido de Reclamo Prestacional."
                );
            }

            finalizarCreacion(
                    con,
                    idRequerimientoCompra,
                    tokenReserva,
                    idReclamo,
                    usuario
            );

            /*
             * Se verifica el contrato persistente antes del commit.
             * No alcanza con que la función SQL haya devuelto true.
             */
            RequerimientoCompraReclamoPrestacional relacionFinal =
                    obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            if (relacionFinal == null
                    || !relacionFinal.isVinculado()
                    || relacionFinal.getIdReclamoPrestacionalInt()
                    != idReclamo) {

                throw new Exception(
                        "El Reclamo Prestacional fue insertado, pero "
                                + "la relación con el requerimiento no quedó "
                                + "confirmada correctamente."
                );
            }

            con.commit();

            return idReclamo;

        } catch (Exception e) {
            ConnectionHelper.rollback(
                    con
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(
                    con
            );
        }
    }

    public void finalizarCreacion(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final int idReclamoPrestacional,
            final String usuario) throws Exception {

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        validarToken(
                tokenReserva
        );

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        ejecutarBoolean(
                SQL_FINALIZAR,
                new Parametrizador() {
                    public void parametrizar(
                            PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(
                                1,
                                idRequerimientoCompra
                        );

                        stmt.setString(
                                2,
                                tokenReserva
                        );

                        stmt.setInt(
                                3,
                                idReclamoPrestacional
                        );

                        stmt.setString(
                                4,
                                normalizarUsuario(
                                        usuario
                                )
                        );
                    }
                },
                "No se pudo finalizar la relacion "
                        + "con el Reclamo Prestacional."
        );
    }

    private void finalizarCreacion(
            Connection con,
            final int idRequerimientoCompra,
            final String tokenReserva,
            final int idReclamoPrestacional,
            final String usuario) throws Exception {

        if (con == null) {
            throw new Exception(
                    "No se informo la conexion transaccional."
            );
        }

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        validarToken(
                tokenReserva
        );

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        ejecutarBoolean(
                con,
                SQL_FINALIZAR,
                new Parametrizador() {
                    public void parametrizar(
                            PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(
                                1,
                                idRequerimientoCompra
                        );

                        stmt.setString(
                                2,
                                tokenReserva
                        );

                        stmt.setInt(
                                3,
                                idReclamoPrestacional
                        );

                        stmt.setString(
                                4,
                                normalizarUsuario(
                                        usuario
                                )
                        );
                    }
                },
                "No se pudo finalizar la relacion "
                        + "con el Reclamo Prestacional."
        );
    }

    private RequerimientoCompraReclamoPrestacional
    obtenerPorRequerimiento(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        if (con == null) {
            throw new Exception(
                    "No se informó la conexión transaccional."
            );
        }

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        PreparedStatement stmt =
                null;

        ResultSet rs =
                null;

        try {
            stmt =
                    con.prepareStatement(
                            SQL_GET_RELACION
                    );

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            rs =
                    stmt.executeQuery();

            return rs.next()
                    ? mapRelacion(
                    rs
            )
                    : null;

        } finally {
            closeQuietly(
                    rs
            );

            ConnectionHelper.cerrar(
                    stmt
            );
        }
    }

    private void bloquearRequerimiento(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        if (con == null) {
            throw new Exception(
                    "No se informó la conexión transaccional."
            );
        }

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        PreparedStatement stmt =
                null;

        ResultSet rs =
                null;

        try {
            stmt =
                    con.prepareStatement(
                            SQL_BLOQUEAR_REQUERIMIENTO
                    );

            stmt.setInt(
                    1,
                    ADVISORY_LOCK_NAMESPACE
            );

            stmt.setInt(
                    2,
                    idRequerimientoCompra
            );

            rs =
                    stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception(
                        "No se pudo bloquear transaccionalmente "
                                + "la creación del Reclamo Prestacional."
                );
            }

        } finally {
            closeQuietly(
                    rs
            );

            ConnectionHelper.cerrar(
                    stmt
            );
        }
    }

    private void validarReservaCompatible(
            RequerimientoCompraReclamoPrestacional relacion,
            String tokenReserva,
            String usuario) throws Exception {

        if (relacion == null) {
            throw new Exception(
                    "La reserva del Reclamo Prestacional no pudo "
                            + "ser recuperada."
            );
        }

        if (relacion.isVinculado()) {
            return;
        }

        if (relacion.isError()) {
            throw new Exception(
                    "El Reclamo Prestacional fue creado, pero "
                            + "su vinculación requiere reconciliación."
            );
        }

        if (!relacion.isReservado()) {
            throw new Exception(
                    "La relación del requerimiento posee un estado "
                            + "incompatible con la creación del Reclamo "
                            + "Prestacional."
            );
        }

        if (WebKeysCompras.isEmpty(
                relacion.getTokenReserva()
        )
                || !relacion.getTokenReserva().equals(
                tokenReserva
        )) {

            throw new Exception(
                    "Ya existe otra creación de Reclamo Prestacional "
                            + "en proceso para este requerimiento."
            );
        }

        String usuarioReserva =
                WebKeysCompras.trimToNull(
                        relacion.getAltaUsr()
                );

        if (usuarioReserva == null) {
            usuarioReserva =
                    WebKeysCompras.trimToNull(
                            relacion.getModiUsr()
                    );
        }

        String usuarioActual =
                normalizarUsuario(
                        usuario
                );

        if (usuarioReserva != null
                && !usuarioReserva.equals(
                usuarioActual
        )) {

            throw new Exception(
                    "La reserva del Reclamo Prestacional pertenece "
                            + "a otro usuario."
            );
        }
    }

    public void reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        validarToken(
                tokenReserva
        );

        Connection con = null;

        try {
            con =
                    ConnectionHelper
                            .getConnectionForTransaction();

            /*
             * Serializa todos los intentos de creación correspondientes
             * al mismo requerimiento.
             */
            bloquearRequerimiento(
                    con,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            /*
             * Si el requerimiento ya está vinculado, la operación solicitada
             * ya fue completada. No se crea ni reemplaza otra reserva.
             */
            if (relacion != null
                    && relacion.isVinculado()) {

                con.commit();

                return;
            }

            /*
             * Si ya existe una relación, únicamente se acepta cuando representa
             * la misma reserva. Una reserva diferente o un estado ERROR debe
             * fallar cerrado.
             */
            if (relacion != null) {
                validarReservaCompatible(
                        relacion,
                        tokenReserva,
                        usuario
                );

                con.commit();

                return;
            }

            /*
             * No existe relación: se ejecuta la operación SQL de bajo nivel
             * utilizando la misma conexión y transacción.
             */
            reservarCreacion(
                    con,
                    idRequerimientoCompra,
                    tokenReserva,
                    usuario
            );

            /*
             * Se comprueba que la función SQL haya dejado una reserva
             * recuperable y compatible con este intento.
             */
            relacion =
                    obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            validarReservaCompatible(
                    relacion,
                    tokenReserva,
                    usuario
            );

            con.commit();

        } catch (Exception e) {
            ConnectionHelper.rollback(
                    con
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(
                    con
            );
        }
    }

    public void reservarCreacion(
            Connection con,
            final int idRequerimientoCompra,
            final String tokenReserva,
            final String usuario) throws Exception {

        if (con == null) {
            throw new Exception(
                    "No se informó la conexión transaccional."
            );
        }

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        validarToken(
                tokenReserva
        );

        ejecutarBoolean(
                con,
                SQL_RESERVAR,
                new Parametrizador() {
                    public void parametrizar(
                            PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(
                                1,
                                idRequerimientoCompra
                        );

                        stmt.setString(
                                2,
                                tokenReserva
                        );

                        stmt.setString(
                                3,
                                normalizarUsuario(
                                        usuario
                                )
                        );
                    }
                },
                "No se pudo reservar la creación "
                        + "del Reclamo Prestacional."
        );
    }
}
