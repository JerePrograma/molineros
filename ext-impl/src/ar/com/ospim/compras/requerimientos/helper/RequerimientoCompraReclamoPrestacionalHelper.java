package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceImpl;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalTransaccion;

import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orquestacion y reglas del vinculo Compras / Reclamo Prestacional.
 *
 * No administra JDBC. Las operaciones que deben compartir una misma conexion
 * se ejecutan mediante la fachada transaccional opaca del ServiceImpl.
 */
public final class RequerimientoCompraReclamoPrestacionalHelper {

    private final RequerimientoCompraReclamoPrestacionalServiceImpl
            persistence =
            new RequerimientoCompraReclamoPrestacionalServiceImpl();

    private final ReclamoPrestacionalCompraDocumentacionHelper
            documentacionHelper =
            new ReclamoPrestacionalCompraDocumentacionHelper();

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
                    "Existe mas de un requerimiento vinculado al "
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

        List<Integer> idsValidos =
                normalizarIdsRequerimientos(
                        idsRequerimientos
                );

        if (idsValidos.isEmpty()) {
            return resultado;
        }

        List<RequerimientoCompraReclamoPrestacional> relaciones =
                persistence.listarVinculadasPorRequerimientos(
                        WebKeysCompras.VINCULO_RECLAMO_VINCULADO,
                        idsValidos
                );

        for (int i = 0; i < relaciones.size(); i++) {
            RequerimientoCompraReclamoPrestacional relacion =
                    relaciones.get(i);

            if (relacion != null && relacion.isVinculado()) {
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
                    "No se pudo registrar el error de vinculacion "
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

        throw new Exception(
                "No se puede crear el Reclamo Prestacional sin "
                        + "el contexto de su documentacion obligatoria."
        );
    }

    public int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user,
            ServiceContext serviceContext) throws Exception {

        if (serviceContext == null) {
            throw new Exception(
                    "No se pudo determinar el contexto documental del reclamo."
            );
        }

        return crearYVincularInterno(
                idRequerimientoCompra,
                tokenReserva,
                reclamo,
                user,
                serviceContext
        );
    }

    private int crearYVincularInterno(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user,
            ServiceContext serviceContext) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (reclamo == null) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional que se desea crear."
            );
        }

        String usuario =
                user != null
                        ? normalizarUsuario(user.getScreenName())
                        : "sistema";

        RequerimientoCompraReclamoPrestacionalTransaccion
                transaccion = null;
        ReclamoPrestacionalCompraDocumentacionHelper.DocumentacionAdjuntada
                documentacionAdjuntada = null;

        try {
            transaccion =
                    RequerimientoCompraReclamoPrestacionalTransaccion.abrir(
                            persistence
                    );

            bloquearRequerimiento(
                    transaccion,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    transaccion.obtenerPorRequerimiento(
                            idRequerimientoCompra
                    );

            if (relacion != null && relacion.isVinculado()) {
                int idReclamoExistente =
                        relacion.getIdReclamoPrestacionalInt();

                if (idReclamoExistente <= 0) {
                    throw new Exception(
                            "La relacion figura vinculada, pero no contiene "
                                    + "un identificador valido de Reclamo Prestacional."
                    );
                }

                asegurarEstadoReclamoPrestacional(
                        transaccion,
                        idRequerimientoCompra,
                        usuario
                );

                transaccion.commit();
                return idReclamoExistente;
            }

            if (relacion == null) {
                reservarCreacion(
                        transaccion,
                        idRequerimientoCompra,
                        tokenReserva,
                        usuario
                );

                relacion =
                        transaccion.obtenerPorRequerimiento(
                                idRequerimientoCompra
                        );
            }

            validarReservaCompatible(
                    relacion,
                    tokenReserva,
                    usuario
            );

            int idReclamo =
                    transaccion.insertarReclamoPrestacional(
                            reclamo,
                            user
                    );

            if (idReclamo <= 0) {
                throw new Exception(
                        "La insercion no devolvio un identificador valido "
                                + "de Reclamo Prestacional."
                );
            }

            documentacionAdjuntada =
                    documentacionHelper
                            .adjuntarDocumentacionControlada(
                                    idRequerimientoCompra,
                                    idReclamo,
                                    serviceContext
                            );

            finalizarCreacion(
                    transaccion,
                    idRequerimientoCompra,
                    tokenReserva,
                    idReclamo,
                    usuario
            );

            RequerimientoCompraReclamoPrestacional relacionFinal =
                    transaccion.obtenerPorRequerimiento(
                            idRequerimientoCompra
                    );

            if (relacionFinal == null
                    || !relacionFinal.isVinculado()
                    || relacionFinal.getIdReclamoPrestacionalInt()
                    != idReclamo) {

                throw new Exception(
                        "El Reclamo Prestacional fue insertado, pero la "
                                + "relacion con el requerimiento no quedo confirmada."
                );
            }

            asegurarEstadoReclamoPrestacional(
                    transaccion,
                    idRequerimientoCompra,
                    usuario
            );

            transaccion.commit();
            return idReclamo;

        } catch (Exception e) {
            if (transaccion != null) {
                try {
                    transaccion.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            if (documentacionAdjuntada != null) {

                try {
                    documentacionHelper.compensarDocumentacion(
                            documentacionAdjuntada
                    );
                } catch (Exception compensacionError) {
                    e.addSuppressed(compensacionError);
                }
            }

            throw e;

        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
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
                    "No se pudo finalizar la relacion con el Reclamo Prestacional."
            );
        }
    }

    public void reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        RequerimientoCompraReclamoPrestacionalTransaccion
                transaccion = null;

        try {
            transaccion =
                    RequerimientoCompraReclamoPrestacionalTransaccion.abrir(
                            persistence
                    );

            bloquearRequerimiento(
                    transaccion,
                    idRequerimientoCompra
            );

            RequerimientoCompraReclamoPrestacional relacion =
                    transaccion.obtenerPorRequerimiento(
                            idRequerimientoCompra
                    );

            if (relacion != null && relacion.isVinculado()) {
                transaccion.commit();
                return;
            }

            if (relacion != null) {
                validarReservaCompatible(
                        relacion,
                        tokenReserva,
                        usuario
                );

                transaccion.commit();
                return;
            }

            reservarCreacion(
                    transaccion,
                    idRequerimientoCompra,
                    tokenReserva,
                    usuario
            );

            relacion =
                    transaccion.obtenerPorRequerimiento(
                            idRequerimientoCompra
                    );

            validarReservaCompatible(
                    relacion,
                    tokenReserva,
                    usuario
            );

            transaccion.commit();

        } catch (Exception e) {
            if (transaccion != null) {
                try {
                    transaccion.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            throw e;

        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }

    private void reservarCreacion(
            RequerimientoCompraReclamoPrestacionalTransaccion transaccion,
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        if (!transaccion.reservarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                normalizarUsuario(usuario)
        )) {
            throw new Exception(
                    "No se pudo reservar la creacion del Reclamo Prestacional."
            );
        }
    }

    private void finalizarCreacion(
            RequerimientoCompraReclamoPrestacionalTransaccion transaccion,
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        if (!transaccion.finalizarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                normalizarUsuario(usuario)
        )) {
            throw new Exception(
                    "No se pudo finalizar la relacion con el Reclamo Prestacional."
            );
        }
    }

    private void bloquearRequerimiento(
            RequerimientoCompraReclamoPrestacionalTransaccion transaccion,
            int idRequerimientoCompra) throws Exception {

        if (!transaccion.bloquearRequerimiento(
                idRequerimientoCompra
        )) {
            throw new Exception(
                    "No se pudo bloquear transaccionalmente la creacion "
                            + "del Reclamo Prestacional."
            );
        }
    }

    private void asegurarEstadoReclamoPrestacional(
            RequerimientoCompraReclamoPrestacionalTransaccion transaccion,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        int estadoActual =
                transaccion.getEstadoRequerimientoForUpdate(
                        idRequerimientoCompra
                );

        if (!WebKeysCompras.esEstadoValido(estadoActual)) {
            throw new Exception(
                    "El requerimiento posee un estado invalido durante "
                            + "la vinculacion del Reclamo Prestacional."
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
                            + "no puede pasar a RECLAMO (RP) desde su estado actual: "
                            + WebKeysCompras.getEstadoDescripcion(estadoActual)
                            + "."
            );
        }

        transaccion.cambiarEstado(
                idRequerimientoCompra,
                WebKeysCompras.ESTADO_RECLAMO_RP,
                normalizarUsuario(usuario)
        );

        int estadoFinal =
                transaccion.getEstadoRequerimientoForUpdate(
                        idRequerimientoCompra
                );

        if (estadoFinal != WebKeysCompras.ESTADO_RECLAMO_RP) {
            throw new Exception(
                    "El Reclamo Prestacional quedo vinculado, pero el "
                            + "requerimiento no confirmo el estado RECLAMO (RP)."
            );
        }
    }

    private void validarReservaCompatible(
            RequerimientoCompraReclamoPrestacional relacion,
            String tokenReserva,
            String usuario) throws Exception {

        if (relacion == null) {
            throw new Exception(
                    "La reserva del Reclamo Prestacional no pudo ser recuperada."
            );
        }

        if (relacion.isVinculado()) {
            return;
        }

        if (relacion.isError()) {
            throw new Exception(
                    "El Reclamo Prestacional fue creado, pero su vinculacion "
                            + "requiere reconciliacion."
            );
        }

        if (!relacion.isReservado()) {
            throw new Exception(
                    "La relacion del requerimiento posee un estado incompatible "
                            + "con la creacion del Reclamo Prestacional."
            );
        }

        if (WebKeysCompras.isEmpty(relacion.getTokenReserva())
                || !relacion.getTokenReserva().equals(tokenReserva)) {
            throw new Exception(
                    "Ya existe otra creacion de Reclamo Prestacional "
                            + "en proceso para este requerimiento."
            );
        }

        String usuarioReserva =
                WebKeysCompras.trimToNull(relacion.getAltaUsr());

        if (usuarioReserva == null) {
            usuarioReserva =
                    WebKeysCompras.trimToNull(relacion.getModiUsr());
        }

        String usuarioActual = normalizarUsuario(usuario);

        if (usuarioReserva != null
                && !usuarioReserva.equals(usuarioActual)) {
            throw new Exception(
                    "La reserva del Reclamo Prestacional pertenece a otro usuario."
            );
        }
    }

    private List<Integer> normalizarIdsRequerimientos(
            List<Integer> idsRequerimientos) {

        List<Integer> resultado = new ArrayList<Integer>();

        for (int i = 0;
             idsRequerimientos != null && i < idsRequerimientos.size();
             i++) {

            Integer id = idsRequerimientos.get(i);

            if (id != null && id.intValue() > 0) {
                resultado.add(id);
            }
        }

        return resultado;
    }

private void validarIdRequerimiento(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }
    }

    private void validarToken(String tokenReserva) throws Exception {
        if (WebKeysCompras.isEmpty(tokenReserva)) {
            throw new Exception(
                    "No se pudo validar el contexto de creacion "
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
            return "Error de vinculacion no especificado.";
        }

        return value.length() <= 2000
                ? value
                : value.substring(0, 2000);
    }
}
