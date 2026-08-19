package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceImpl;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.model.User;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orquestación y reglas del vínculo Compras / Reclamo Prestacional.
 */
public final class RequerimientoCompraReclamoPrestacionalHelper {

    private final RequerimientoCompraReclamoPrestacionalServiceImpl
            persistence =
            new RequerimientoCompraReclamoPrestacionalServiceImpl();

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        return persistence.obtenerPorRequerimiento(
                idRequerimientoCompra
        );
    }

    public RequerimientoCompraReclamoPrestacional
    getRelacionPorReclamoPrestacional(
            int idReclamoPrestacional) throws Exception {

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional."
            );
        }

        List<RequerimientoCompraReclamoPrestacional> relaciones =
                persistence.listarPorReclamoPrestacional(
                        idReclamoPrestacional,
                        WebKeysCompras.VINCULO_RECLAMO_VINCULADO
                );

        if (relaciones.isEmpty()) {
            return null;
        }

        if (relaciones.size() > 1) {
            throw new Exception(
                    "Existe más de un requerimiento vinculado al "
                            + "Reclamo Prestacional."
            );
        }

        RequerimientoCompraReclamoPrestacional relacion =
                relaciones.get(0);

        return relacion != null && relacion.isVinculado()
                ? relacion
                : null;
    }

    public Map<Integer, RequerimientoCompraReclamoPrestacional>
    obtenerVinculadasPorRequerimientos(
            List<Integer> idsRequerimientos) throws Exception {

        Map<Integer, RequerimientoCompraReclamoPrestacional> resultado =
                new HashMap<Integer, RequerimientoCompraReclamoPrestacional>();

        String idsArray =
                construirArrayIdsRequerimientos(
                        idsRequerimientos
                );

        if (idsArray == null) {
            return resultado;
        }

        List<RequerimientoCompraReclamoPrestacional> relaciones =
                persistence.listarVinculadasPorRequerimientos(
                        WebKeysCompras.VINCULO_RECLAMO_VINCULADO,
                        idsArray
                );

        for (int i = 0; i < relaciones.size(); i++) {
            RequerimientoCompraReclamoPrestacional relacion =
                    relaciones.get(i);

            if (relacion != null
                    && relacion.isVinculado()) {

                resultado.put(
                        Integer.valueOf(
                                relacion.getIdRequerimientoCompra()
                        ),
                        relacion
                );
            }
        }

        return resultado;
    }

    public boolean liberarReserva(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        boolean resultado =
                persistence.liberarReserva(
                        idRequerimientoCompra,
                        tokenReserva,
                        normalizarUsuario(usuario)
                );

        if (!resultado) {
            throw new Exception(
                    "No se pudo liberar la reserva del Reclamo Prestacional."
            );
        }

        return true;
    }

    public boolean marcarErrorPosteriorAlInsert(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String error,
            String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        boolean resultado =
                persistence.marcarErrorPosteriorAlInsert(
                        idRequerimientoCompra,
                        tokenReserva,
                        idReclamoPrestacional,
                        limitarError(error),
                        normalizarUsuario(usuario)
                );

        if (!resultado) {
            throw new Exception(
                    "No se pudo registrar el error de vinculación "
                            + "del Reclamo Prestacional."
            );
        }

        return true;
    }

    public int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (reclamo == null) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional "
                            + "que se desea crear."
            );
        }

        Connection con = null;

        String usuario =
                user != null
                        ? user.getScreenName()
                        : "sistema";

        try {
            con = ConnectionHelper.getConnectionForTransaction();

            bloquearRequerimiento(
                    con,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    persistence.obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

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

                asegurarEstadoReclamoPrestacional(
                        con,
                        idRequerimientoCompra,
                        usuario
                );

                con.commit();
                return idReclamoExistente;
            }

            if (relacion == null) {
                reservarCreacion(
                        con,
                        idRequerimientoCompra,
                        tokenReserva,
                        usuario
                );

                relacion =
                        persistence.obtenerPorRequerimiento(
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

            RequerimientoCompraReclamoPrestacional relacionFinal =
                    persistence.obtenerPorRequerimiento(
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

            asegurarEstadoReclamoPrestacional(
                    con,
                    idRequerimientoCompra,
                    usuario
            );

            con.commit();
            return idReclamo;

        } catch (Exception e) {
            ConnectionHelper.rollback(con);
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public void finalizarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        boolean resultado =
                persistence.finalizarCreacion(
                        idRequerimientoCompra,
                        tokenReserva,
                        idReclamoPrestacional,
                        normalizarUsuario(usuario)
                );

        if (!resultado) {
            throw new Exception(
                    "No se pudo finalizar la relación "
                            + "con el Reclamo Prestacional."
            );
        }
    }

    public void reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        Connection con = null;

        try {
            con = ConnectionHelper.getConnectionForTransaction();

            bloquearRequerimiento(
                    con,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    persistence.obtenerPorRequerimiento(
                            con,
                            idRequerimientoCompra
                    );

            if (relacion != null
                    && relacion.isVinculado()) {

                con.commit();
                return;
            }

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
                    persistence.obtenerPorRequerimiento(
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
            ConnectionHelper.rollback(con);
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private void reservarCreacion(
            Connection con,
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        boolean resultado =
                persistence.reservarCreacion(
                        con,
                        idRequerimientoCompra,
                        tokenReserva,
                        normalizarUsuario(usuario)
                );

        if (!resultado) {
            throw new Exception(
                    "No se pudo reservar la creación "
                            + "del Reclamo Prestacional."
            );
        }
    }

    private void finalizarCreacion(
            Connection con,
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        boolean resultado =
                persistence.finalizarCreacion(
                        con,
                        idRequerimientoCompra,
                        tokenReserva,
                        idReclamoPrestacional,
                        normalizarUsuario(usuario)
                );

        if (!resultado) {
            throw new Exception(
                    "No se pudo finalizar la relación "
                            + "con el Reclamo Prestacional."
            );
        }
    }

    private void bloquearRequerimiento(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        if (!persistence.bloquearRequerimiento(
                con,
                idRequerimientoCompra
        )) {
            throw new Exception(
                    "No se pudo bloquear transaccionalmente "
                            + "la creación del Reclamo Prestacional."
            );
        }
    }

    private void asegurarEstadoReclamoPrestacional(
            Connection con,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        int estadoActual =
                persistence.getEstadoRequerimientoForUpdate(
                        con,
                        idRequerimientoCompra
                );

        if (!WebKeysCompras.esEstadoValido(estadoActual)) {
            throw new Exception(
                    "El requerimiento posee un estado inválido "
                            + "durante la vinculación del Reclamo "
                            + "Prestacional."
            );
        }

        if (estadoActual == WebKeysCompras.ESTADO_RECLAMO_RP) {
            return;
        }

        if (!WebKeysCompras.validarTransicionEstado(
                estadoActual,
                WebKeysCompras.ESTADO_RECLAMO_RP
        )) {
            throw new Exception(
                    "El requerimiento vinculado al Reclamo Prestacional "
                            + "no puede pasar a RECLAMO (RP) desde su "
                            + "estado actual: "
                            + WebKeysCompras.getEstadoDescripcion(
                                    estadoActual
                            )
                            + "."
            );
        }

        persistence.cambiarEstado(
                con,
                idRequerimientoCompra,
                WebKeysCompras.ESTADO_RECLAMO_RP,
                normalizarUsuario(usuario)
        );

        int estadoFinal =
                persistence.getEstadoRequerimientoForUpdate(
                        con,
                        idRequerimientoCompra
                );

        if (estadoFinal != WebKeysCompras.ESTADO_RECLAMO_RP) {
            throw new Exception(
                    "El Reclamo Prestacional quedó vinculado, pero "
                            + "el requerimiento no confirmó el estado "
                            + "RECLAMO (RP)."
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

        if (WebKeysCompras.isEmpty(relacion.getTokenReserva())
                || !relacion.getTokenReserva().equals(tokenReserva)) {

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
                normalizarUsuario(usuario);

        if (usuarioReserva != null
                && !usuarioReserva.equals(usuarioActual)) {

            throw new Exception(
                    "La reserva del Reclamo Prestacional pertenece "
                            + "a otro usuario."
            );
        }
    }

    private String construirArrayIdsRequerimientos(
            List<Integer> idsRequerimientos) {

        if (idsRequerimientos == null
                || idsRequerimientos.isEmpty()) {

            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append('{');
        boolean primero = true;

        for (int i = 0; i < idsRequerimientos.size(); i++) {
            Integer id = idsRequerimientos.get(i);

            if (id == null || id.intValue() <= 0) {
                continue;
            }

            if (!primero) {
                value.append(',');
            }

            value.append(id.intValue());
            primero = false;
        }

        if (primero) {
            return null;
        }

        value.append('}');
        return value.toString();
    }

    private void validarIdRequerimiento(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }
    }

    private void validarToken(String tokenReserva)
            throws Exception {

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
}
