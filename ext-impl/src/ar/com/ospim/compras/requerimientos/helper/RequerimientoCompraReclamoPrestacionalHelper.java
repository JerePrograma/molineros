package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraComparativaServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalTransaccion;

import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Orquestación y reglas del vinculo Compras / Reclamo Prestacional.
 *
 * No administra JDBC. Las operaciones que deben compartir una misma conexión
 * se ejecutan mediante la infraestructura transaccional del servicio.
 */
public final class RequerimientoCompraReclamoPrestacionalHelper {

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        return RequerimientoCompraReclamoPrestacionalServiceUtil.consultarPorRequerimiento(
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
                RequerimientoCompraReclamoPrestacionalServiceUtil.listarPorReclamoPrestacional(
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

        List<Integer> idsValidos =
                normalizarIdsRequerimientos(
                        idsRequerimientos
                );

        if (idsValidos.isEmpty()) {
            return resultado;
        }

        List<RequerimientoCompraReclamoPrestacional> relaciones =
                RequerimientoCompraReclamoPrestacionalServiceUtil.listarVinculadasPorRequerimientos(
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

    public Map<Integer, RequerimientoCompraReclamoPrestacional>
    obtenerRelacionesPorRequerimientos(
            List<Integer> idsRequerimientos) throws Exception {

        Map<Integer, RequerimientoCompraReclamoPrestacional> resultado =
                new HashMap<Integer, RequerimientoCompraReclamoPrestacional>();

        List<Integer> idsValidos =
                normalizarIdsRequerimientos(idsRequerimientos);

        if (idsValidos.isEmpty()) {
            return resultado;
        }

        List<RequerimientoCompraReclamoPrestacional> relaciones =
                RequerimientoCompraReclamoPrestacionalServiceUtil
                        .listarVinculadasPorRequerimientos(null, idsValidos);

        for (int i = 0; i < relaciones.size(); i++) {
            RequerimientoCompraReclamoPrestacional relacion = relaciones.get(i);

            if (relacion != null
                    && (relacion.isVinculado() || relacion.isBaja())) {
                resultado.put(
                        Integer.valueOf(relacion.getIdRequerimientoCompra()),
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
                RequerimientoCompraReclamoPrestacionalServiceUtil.ejecutarLiberacionReserva(
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
                RequerimientoCompraReclamoPrestacionalServiceUtil.registrarErrorPosteriorAlInsert(
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

    public static RevisionesReclamo crearRevisionInicialCompra() {
        RevisionesReclamo revisionCompra = new RevisionesReclamo();
        revisionCompra.setId(-1 - new Random().nextInt(Integer.MAX_VALUE));
        revisionCompra.setEstado(RevisionesReclamo.ESTADOS.NUEVO);
        revisionCompra.setRevisionInicialCompra(true);
        revisionCompra.setFecha_revision(new Date());
        revisionCompra.setUsr_responsable_resolucion(
                "AUDITORIA DE PRESTACIONES MEDICAS");
        revisionCompra.setUsr_presente("AUDITORIA MEDICA");
        revisionCompra.setUsr_resolucion("AUTORIZADO");
        return revisionCompra;
    }

    public int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user) throws Exception {

        ServiceContext serviceContext = new ServiceContext();
        if (user != null) {
            serviceContext.setUserId(user.getUserId());
            serviceContext.setCompanyId(user.getCompanyId());
        }
        serviceContext.setScopeGroupId(
                WebKeysCompras.DOCUMENT_LIBRARY_GROUP_ID_COMPRAS);
        return crearYVincular(
                idRequerimientoCompra, tokenReserva, reclamo, user,
                serviceContext);
    }

    public int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user,
            ServiceContext serviceContext) throws Exception {

        return crearYVincularInterno(
                idRequerimientoCompra, tokenReserva, reclamo, user,
                serviceContext);
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
        DocumentoComprasCreado comparativaCreada = null;
        boolean commitInvocado = false;

        try {
            transaccion =
                    RequerimientoCompraReclamoPrestacionalServiceUtil.abrirTransaccion();

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
                            "La relación figura vinculada, pero no contiene "
                                    + "un identificador válido de Reclamo Prestacional."
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

            boolean tieneComparativa =
                    !RequerimientoCompraComparativaServiceUtil
                            .listar(idRequerimientoCompra).isEmpty();
            if (tieneComparativa
                    && (user == null || serviceContext == null
                        || serviceContext.getUserId() != user.getUserId())) {
                throw new Exception(
                        "No se pudo identificar al usuario del adjunto de la comparativa.");
            }

            List<RevisionesReclamo> revisionesOriginales =
                    reclamo.getRevisiones();
            ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO evaluacionOriginal =
                    reclamo.getEstadoResolucionAutorizada();
            List<RevisionesReclamo> revisionesParaGuardar =
                    new ArrayList<RevisionesReclamo>();
            if (revisionesOriginales != null) {
                revisionesParaGuardar.addAll(revisionesOriginales);
            }

            RevisionesReclamo revisionCompra = crearRevisionInicialCompra();
            for (int i = revisionesParaGuardar.size() - 1; i >= 0; i--) {
                RevisionesReclamo revision = revisionesParaGuardar.get(i);
                if (revision != null && revision.isRevisionInicialCompra()) {
                    revisionCompra.setId(revision.getId());
                    revisionesParaGuardar.remove(i);
                }
            }
            revisionesParaGuardar.add(revisionCompra);

            int idReclamo;
            try {
                reclamo.setRevisiones(revisionesParaGuardar);
                idReclamo = transaccion.insertarReclamoPrestacional(
                        reclamo,
                        user
                );
            } finally {
                // Preservar el borrador para los reintentos del alta.
                reclamo.setRevisiones(revisionesOriginales);
                reclamo.setEstadoResolucionAutorizada(evaluacionOriginal);
            }

            if (idReclamo <= 0) {
                throw new Exception(
                        "La inserción no devolvió un identificador válido "
                                + "de Reclamo Prestacional."
                );
            }

            if (tieneComparativa) {
                byte[] contenido = new PdfServlet()
                        .crearComparativaCompraComoAdjunto(idRequerimientoCompra);
                comparativaCreada = DocumentoLibraryComprasHelper
                        .crearComparativaReclamoPrestacional(
                                idRequerimientoCompra, idReclamo, contenido,
                                serviceContext);
            }

            transaccion.guardarObservacionEstadoReclamo(
                    idReclamo,
                    reclamo.getEstado(),
                    "Requerimiento ID #" + idRequerimientoCompra
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
                                + "relación con el requerimiento no quedó confirmada."
                );
            }

            asegurarEstadoReclamoPrestacional(
                    transaccion,
                    idRequerimientoCompra,
                    usuario
            );

            commitInvocado = true;
            transaccion.commit();
            return idReclamo;

        } catch (Exception e) {
            boolean rollbackConfirmado = transaccion == null;
            if (transaccion != null) {
                try {
                    transaccion.rollback();
                    rollbackConfirmado = !commitInvocado;
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            if (comparativaCreada != null) {
                if (!rollbackConfirmado) {
                    throw new Exception(
                            "No se pudo confirmar el guardado del RP. "
                                    + "Consulte el requerimiento antes de reintentar; "
                                    + "la comparativa se conservó.", e);
                }
                try {
                    DocumentoLibraryComprasHelper
                            .eliminarDocumentoCreado(comparativaCreada);
                } catch (Exception cleanupError) {
                    e.addSuppressed(cleanupError);
                    throw new Exception(
                            "El alta del RP fue revertida, pero no se pudo retirar "
                                    + "la comparativa. Informe a Sistemas antes de reintentar.", e);
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
                RequerimientoCompraReclamoPrestacionalServiceUtil.ejecutarFinalizacionCreacion(
                        idRequerimientoCompra,
                        tokenReserva,
                        idReclamoPrestacional,
                        normalizarUsuario(usuario)
                );

        if (!resultado) {
            throw new Exception(
                    "No se pudo finalizar la relación con el Reclamo Prestacional."
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
                    RequerimientoCompraReclamoPrestacionalServiceUtil.abrirTransaccion();

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
                    "No se pudo reservar la creación del Reclamo Prestacional."
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
                    "No se pudo finalizar la relación con el Reclamo Prestacional."
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
                    "No se pudo bloquear transaccionalmente la creación "
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
                    "El requerimiento posee un estado inválido durante "
                            + "la vinculación del Reclamo Prestacional."
            );
        }

        if (WebKeysCompras.esReclamoRP(estadoActual)
                || WebKeysCompras.esParaAutorizar(estadoActual)
                || WebKeysCompras.esAutorizado(estadoActual)) {
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
                    "El Reclamo Prestacional quedó vinculado, pero el "
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
                    "El Reclamo Prestacional fue creado, pero su vinculación "
                            + "requiere reconciliación."
            );
        }

        if (!relacion.isReservado()) {
            throw new Exception(
                    "La relación del requerimiento posee un estado incompatible "
                            + "con la creación del Reclamo Prestacional."
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
