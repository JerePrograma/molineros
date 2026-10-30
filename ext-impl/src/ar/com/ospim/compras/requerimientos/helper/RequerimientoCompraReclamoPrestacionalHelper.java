package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
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
import com.liferay.portal.security.permission.PermissionChecker;
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

    public boolean liberarReserva(int id,String token,String usuario)throws Exception {
        RequerimientoCompraReclamoPrestacionalTransaccion tx=null;
        try{tx=RequerimientoCompraReclamoPrestacionalServiceUtil.abrirTransaccion();
            boolean resultado=liberarReservaEnTransaccion(tx,id,token);if(!resultado)throw new Exception("No se pudo liberar la reserva del Reclamo Prestacional.");
            tx.commit();return true;
        }catch(Exception e){if(tx!=null)try{tx.rollback();}catch(Exception r){e.addSuppressed(r);}throw e;}
        finally{if(tx!=null)tx.cerrar();}
    }

    public boolean marcarErrorPosteriorAlInsert(int id,String token,int reclamo,String error,String usuario)throws Exception {
        RequerimientoCompraReclamoPrestacionalTransaccion tx=null;
        try{tx=RequerimientoCompraReclamoPrestacionalServiceUtil.abrirTransaccion();
            boolean resultado=marcarErrorEnTransaccion(tx,id,token,reclamo,error,usuario);
            if(!resultado)throw new Exception("No se pudo registrar el error de vinculacion del Reclamo Prestacional.");
            tx.commit();return true;
        }catch(Exception e){if(tx!=null)try{tx.rollback();}catch(Exception r){e.addSuppressed(r);}throw e;}
        finally{if(tx!=null)tx.cerrar();}
    }

    public boolean reservarCreacionEnTransaccion(RequerimientoCompraReclamoPrestacionalTransaccion tx,
            int id,String token,String usuario)throws Exception {
        validarIdRequerimiento(id);validarToken(token);token=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(token);
        bloquearRequerimiento(tx,id);Map<String,Object> r=tx.obtenerCabeceraReserva(id);
        int cotizado=ConfiguracionComprasHelper.resolverEstado("COTIZADO",tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS));
        if(r==null||r.get("baja")!=null)throw new Exception("No existe un requerimiento de compra activo con el identificador informado.");
        if(!Integer.valueOf(cotizado).equals(r.get("estado")))throw new Exception("El requerimiento no se encuentra COTIZADO.");
        if(EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria((String)r.get("cuil"))==null||r.get("integrante")==null)
            throw new Exception("El requerimiento no posee un afiliado valido.");
        RequerimientoCompraReclamoPrestacional relacion=tx.obtenerPorRequerimiento(id);
        if(relacion!=null){
            if(relacion.isVinculado())throw new Exception("El requerimiento ya posee un Reclamo Prestacional.");
            if(relacion.isError())throw new Exception("El requerimiento posee un Reclamo Prestacional pendiente de reconciliacion.");
            throw new Exception("Ya existe una creacion de Reclamo Prestacional en proceso para el requerimiento.");
        }
        if(tx.insertarReserva(id,WebKeysCompras.VINCULO_RECLAMO_RESERVADO,token,usuario)!=1)
            throw new Exception("La reserva cambio mientras se guardaba. Actualice la pantalla.");
        return true;
    }

    public boolean finalizarCreacionEnTransaccion(RequerimientoCompraReclamoPrestacionalTransaccion tx,
            int id,String token,int reclamo,String usuario)throws Exception {
        validarIdRequerimiento(id);validarToken(token);if(reclamo<=0)throw new Exception("Debe informar el Reclamo Prestacional creado.");
        token=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(token);bloquearRequerimiento(tx,id);
        RequerimientoCompraReclamoPrestacional r=tx.obtenerPorRequerimiento(id);
        if(r==null)throw new Exception("No existe una reserva para el requerimiento.");
        if(r.isVinculado()&&r.getIdReclamoPrestacionalInt()==reclamo)return true;
        if(!r.isReservado()||!token.equals(r.getTokenReserva()))throw new Exception("La reserva del requerimiento no es valida.");
        if(tx.actualizarVinculo(id,r.getEstado(),r.getTokenReserva(),reclamo,WebKeysCompras.VINCULO_RECLAMO_VINCULADO,null,true,usuario)!=1)
            throw new Exception("La reserva cambio mientras se vinculaba el Reclamo Prestacional.");
        return true;
    }

    public boolean liberarReservaEnTransaccion(RequerimientoCompraReclamoPrestacionalTransaccion tx,int id,String token)throws Exception {
        validarIdRequerimiento(id);validarToken(token);token=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(token);
        bloquearRequerimiento(tx,id);RequerimientoCompraReclamoPrestacional r=tx.obtenerPorRequerimiento(id);
        if(r==null||!r.isReservado()||r.getIdReclamoPrestacional()!=null||!token.equals(r.getTokenReserva()))return false;
        return tx.eliminarReserva(id,r.getEstado(),r.getTokenReserva())==1;
    }

    public boolean marcarErrorEnTransaccion(RequerimientoCompraReclamoPrestacionalTransaccion tx,
            int id,String token,int reclamo,String error,String usuario)throws Exception {
        validarIdRequerimiento(id);validarToken(token);if(reclamo<=0)throw new Exception("Debe informar el Reclamo Prestacional creado.");
        token=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(token);bloquearRequerimiento(tx,id);
        RequerimientoCompraReclamoPrestacional r=tx.obtenerPorRequerimiento(id);
        if(r!=null&&r.isReservado()&&token.equals(r.getTokenReserva()))
            return tx.actualizarVinculo(id,r.getEstado(),r.getTokenReserva(),reclamo,WebKeysCompras.VINCULO_RECLAMO_ERROR,
                    limitarError(error,tx),false,usuario)==1;
        return r!=null&&r.isError()&&r.getIdReclamoPrestacionalInt()==reclamo;
    }

    /** Genera una sola copia y sincroniza el adjunto del RP si cambio. */
    public byte[] imprimirComparativa(
            int idRequerimientoCompra,
            User user,
            ServiceContext serviceContext,
            PermissionChecker permissionChecker) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        RequerimientoCompraReclamoPrestacional relacion =
                obtenerPorRequerimiento(idRequerimientoCompra);
        if (relacion == null || !relacion.isVinculado()) {
            return new PdfServlet().crearComparativaCompraComoAdjunto(
                    idRequerimientoCompra);
        }
        if (user == null || serviceContext == null
                || user.getUserId() != serviceContext.getUserId()
                || user.getCompanyId() != serviceContext.getCompanyId()) {
            throw new Exception(
                    "No se pudo identificar al usuario de la impresion de comparativa.");
        }

        RequerimientoCompraReclamoPrestacionalTransaccion transaccion = null;
        try {
            transaccion = RequerimientoCompraReclamoPrestacionalServiceUtil
                    .abrirTransaccion();
            bloquearRequerimiento(transaccion, idRequerimientoCompra);
            relacion = transaccion.obtenerPorRequerimiento(idRequerimientoCompra);
            boolean vinculado = relacion != null && relacion.isVinculado();
            if (vinculado) {
                if (relacion.getIdRequerimientoCompra() != idRequerimientoCompra
                        || relacion.getBajaFecha() != null) {
                    throw new Exception("El vinculo de la comparativa con el RP no es valido.");
                }
                ReclamoPrestacional reclamo = ReclamosPrestacionesServiceUtil
                        .getReclamoPrestacional(relacion.getIdReclamoPrestacionalInt());
                if (reclamo == null
                        || reclamo.getId_reclamo() != relacion.getIdReclamoPrestacionalInt()
                        || reclamo.getBaja_fecha() != null) {
                    throw new Exception("El RP de la comparativa no existe o no esta activo.");
                }
            }

            // Generar despues del bloqueo evita sobrescribir con otra impresion atrasada.
            byte[] contenido = new PdfServlet().crearComparativaCompraComoAdjunto(
                    idRequerimientoCompra);
            if (vinculado) {
                DocumentoLibraryComprasHelper.actualizarComparativaReclamoPrestacional(
                        idRequerimientoCompra, relacion.getIdReclamoPrestacionalInt(),
                        contenido, serviceContext, permissionChecker);
            }
            transaccion.commit();
            return contenido;
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
                ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getGrupoDocumentLibraryLegacy());
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
                serviceContext, null);
    }

    public int crearYVincular(int idRequerimientoCompra, String tokenReserva,
            ReclamoPrestacional reclamo, User user, ServiceContext serviceContext,
            ReclamoPrestacionalCompraContexto contexto) throws Exception {
        return crearYVincularInterno(idRequerimientoCompra, tokenReserva, reclamo,
                user, serviceContext, contexto);
    }

    private int crearYVincularInterno(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user,
            ServiceContext serviceContext,
            ReclamoPrestacionalCompraContexto contexto) throws Exception {

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
                    usuario, transaccion
            );

            if (contexto != null) {
                validarReferenciasOrigen(transaccion, idRequerimientoCompra,
                        tokenReserva, reclamo, user, contexto);
            }

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


    private void validarReferenciasOrigen(
            RequerimientoCompraReclamoPrestacionalTransaccion transaccion,
            int idRequerimiento, String token, ReclamoPrestacional reclamo, User user,
            ReclamoPrestacionalCompraContexto contexto) throws Exception {
        if (user == null || contexto.getIdRequerimientoCompra() != idRequerimiento
                || !contexto.coincideNonce(token)
                || !contexto.perteneceAUsuario(user.getScreenName())
                || !contexto.estaVigente(System.currentTimeMillis())
                || !contexto.esBorradorEnEdicion(reclamo)
                || contexto.getCantidadDetallesOrigen() <= 0) {
            throw new Exception("No se pudo verificar la identidad del borrador y sus detalles de Compras.");
        }
        RequerimientoCompra requerimiento = transaccion.getRequerimientoCompra(idRequerimiento);
        ReclamoPrestacionalCompraPrecargaHelper.validarRequerimientoOrigen(
                requerimiento, contexto, reclamo);
        ReclamoPrestacionalCompraPrecargaHelper.validarRecuperablesParaGuardar(
                requerimiento, reclamo.getPrestaciones());
        java.util.Set<Integer> detalles = new java.util.HashSet<Integer>();
        if (reclamo.getPrestaciones() == null || reclamo.getPrestaciones().isEmpty()) {
            throw new Exception("El borrador no conserva prestaciones para confirmar.");
        }
        for (int i = 0; i < reclamo.getPrestaciones().size(); i++) {
            PrestacionesReclamo prestacion = reclamo.getPrestaciones().get(i);
            if (prestacion == null) {
                throw new Exception("El borrador contiene una prestacion sin identidad.");
            }
            Integer idDetalle = contexto.getIdDetalleOrigen(prestacion.getIdRegistro());
            if (idDetalle == null || !detalles.add(idDetalle)) {
                throw new Exception("El borrador contiene un detalle de origen desconocido o duplicado.");
            }
            RequerimientoCompraDetalle origen = ReclamoPrestacionalCompraPrecargaHelper
                    .encontrarDetalleOrigen(requerimiento, idDetalle.intValue());
            if (PrestacionesReclamo.ESTADOS.BAJA.equals(prestacion.getEstado())) {
                continue;
            }
            if (prestacion.getId_medicamento() > 0) {
                if (!origen.tieneMedicamento()
                        || prestacion.getId_medicamento() != origen.getIdMedicamentoInt()
                        || prestacion.getId_prestacion() > 0) {
                    throw new Exception("La prestacion no conserva la referencia de medicamento del detalle.");
                }
                continue;
            }
            Nomenclador canonico = transaccion.obtenerNomencladorCanonico(prestacion.getId_prestacion());
            if (origen.tieneNomenclador()
                    && prestacion.getId_prestacion() == origen.getIdPrestacionInt()) {
                ReclamoPrestacionalCompraPrecargaHelper.validarReferenciaPersistida(origen, canonico);
            } else {
                List<SectorNomencladorCompra> configuracion = NomencladorCompraBusquedaHelper
                        .resolverConfiguracion(requerimiento.getIdSector().intValue(),
                                origen.getIdTipoPrestacion(), transaccion);
                if (!NomencladorCompraBusquedaHelper.esNomencladorConfigurado(configuracion, canonico)) {
                    throw new Exception("La nueva referencia no pertenece al nomenclador configurado del detalle.");
                }
            }
            if (canonico == null || canonico.getId_prestacion() != prestacion.getId_prestacion()
                    || canonico.getCodigo() == null || !canonico.getCodigo().trim().equals(
                        prestacion.getCodigoPrestacion() != null ? prestacion.getCodigoPrestacion().trim() : "")) {
                throw new Exception("La referencia tecnica del borrador ya no coincide con el nomenclador canonico.");
            }
        }
    }

    public void borrarReclamoPrestacional(int idReclamo, User user) throws Exception {
        if (idReclamo <= 0 || user == null || WebKeysCompras.isEmpty(user.getScreenName())) {
            throw new Exception("No se pudo identificar el reclamo y el usuario de la baja.");
        }
        RequerimientoCompraReclamoPrestacionalTransaccion transaccion =
                RequerimientoCompraReclamoPrestacionalServiceUtil.abrirTransaccion();
        try {
            transaccion.bloquearReclamoPrestacional(idReclamo);
            List<RequerimientoCompraReclamoPrestacional> anteriores =
                    transaccion.obtenerVinculosPorRPForUpdate(idReclamo);
            if (transaccion.borrarReclamoPrestacional(idReclamo, user.getScreenName()) != 1) {
                throw new Exception("La baja del Reclamo Prestacional no fue confirmada.");
            }
            java.sql.Timestamp fechaBaja = transaccion.bloquearReclamoPrestacional(idReclamo);
            if (fechaBaja == null) {
                throw new Exception("El Reclamo Prestacional no confirmo su fecha de baja.");
            }
            List<RequerimientoCompraReclamoPrestacional> actuales =
                    transaccion.obtenerVinculosPorRPForUpdate(idReclamo);
            for (int i = 0; i < anteriores.size(); i++) {
                RequerimientoCompraReclamoPrestacional anterior = anteriores.get(i);
                if (!anterior.isVinculado() && !WebKeysCompras.VINCULO_RECLAMO_ERROR.equals(anterior.getEstado())) {
                    continue;
                }
                RequerimientoCompraReclamoPrestacional actual = buscarVinculo(
                        actuales, anterior.getIdRequerimientoCompra());
                if (actual != null && WebKeysCompras.VINCULO_RECLAMO_BAJA.equals(actual.getEstado())
                        && actual.getBajaFecha() != null
                        && fechaBaja.equals(actual.getBajaFecha())
                        && actual.getTokenReserva() == null) {
                    continue;
                }
                if (actual == null || !anterior.getEstado().equals(actual.getEstado())) {
                    throw new Exception("El vinculo con Compras cambio durante la baja del reclamo.");
                }
                actual.setEstado(WebKeysCompras.VINCULO_RECLAMO_BAJA);
                actual.setTokenReserva(null);
                if (transaccion.guardarBajaVinculo(actual, fechaBaja, user.getScreenName(),
                        anterior.getEstado()) != 1) {
                    throw new Exception("No se pudo sincronizar la baja con Compras.");
                }
            }
            List<RequerimientoCompraReclamoPrestacional> finales =
                    transaccion.obtenerVinculosPorRPForUpdate(idReclamo);
            for (int i = 0; i < anteriores.size(); i++) {
                RequerimientoCompraReclamoPrestacional anterior = anteriores.get(i);
                if (anterior.isVinculado()
                        || WebKeysCompras.VINCULO_RECLAMO_ERROR.equals(anterior.getEstado())) {
                    RequerimientoCompraReclamoPrestacional finalizado = buscarVinculo(
                            finales, anterior.getIdRequerimientoCompra());
                    if (!WebKeysCompras.VINCULO_RECLAMO_BAJA.equals(finalizado.getEstado())
                            || !fechaBaja.equals(finalizado.getBajaFecha())
                            || finalizado.getTokenReserva() != null
                            || !user.getScreenName().equals(finalizado.getBajaUsr())) {
                        throw new Exception("La baja del vinculo no confirmo fecha, auditoria y estado.");
                    }
                }
            }
            transaccion.commit();
        } catch (Exception e) {
            try {
                transaccion.rollback();
            } catch (Exception rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;
        } finally {
            transaccion.cerrar();
        }
    }

    private RequerimientoCompraReclamoPrestacional buscarVinculo(
            List<RequerimientoCompraReclamoPrestacional> vinculos, int idRequerimiento)
            throws Exception {
        for (int i = 0; i < vinculos.size(); i++) {
            RequerimientoCompraReclamoPrestacional vinculo = vinculos.get(i);
            if (vinculo.getIdRequerimientoCompra() == idRequerimiento) {
                return vinculo;
            }
        }
        throw new Exception("No se pudo releer el vinculo del reclamo con Compras.");
    }

    public void finalizarCreacion(int id,String token,int reclamo,String usuario)throws Exception {
        RequerimientoCompraReclamoPrestacionalTransaccion tx=null;
        try{tx=RequerimientoCompraReclamoPrestacionalServiceUtil.abrirTransaccion();
            finalizarCreacionEnTransaccion(tx,id,token,reclamo,usuario);tx.commit();
        }catch(Exception e){if(tx!=null)try{tx.rollback();}catch(Exception r){e.addSuppressed(r);}throw e;}
        finally{if(tx!=null)tx.cerrar();}
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
                        usuario, transaccion
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
                    usuario, transaccion
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

        String estados = transaccion.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS);
        int estadoActual = transaccion.getEstadoRequerimientoForUpdate(idRequerimientoCompra);
        int destino = ConfiguracionComprasHelper.resolverEstado("RECLAMO_RP", estados);
        if (estadoActual == destino
                || estadoActual == ConfiguracionComprasHelper.resolverEstado("PARA_AUTORIZAR", estados)
                || estadoActual == ConfiguracionComprasHelper.resolverEstado("AUTORIZADO", estados)) return;
        if (!ConfiguracionComprasHelper.resolverTransicionEstado(estadoActual, destino,
                transaccion.getConfiguracionCompra("COMPRAS_TRANSICIONES_ESTADO"), estados)) {
            throw new Exception("El requerimiento vinculado no puede pasar a RECLAMO (RP) desde su estado actual.");
        }
        transaccion.cambiarEstado(idRequerimientoCompra, destino, normalizarUsuario(usuario));
        if (transaccion.getEstadoRequerimientoForUpdate(idRequerimientoCompra) != destino) {
            throw new Exception("El requerimiento no confirmo el estado RECLAMO (RP).");
        }
    }

    private void validarReservaCompatible(
            RequerimientoCompraReclamoPrestacional relacion,
            String tokenReserva,String usuario,RequerimientoCompraReclamoPrestacionalTransaccion tx) throws Exception {

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

        String usuarioActual = tx.resolverUsuarioAuditoria(usuario);

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

    private void validarToken(String token)throws Exception {
        String normalizado=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(token);
        if(normalizado==null||normalizado.length()>64)
            throw new Exception("No se pudo validar el contexto de creacion del Reclamo Prestacional.");
    }

    private String normalizarUsuario(String usuario) {
        return EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(usuario);
    }

    private String limitarError(String error,RequerimientoCompraReclamoPrestacionalTransaccion tx)throws Exception {
        String clave="COMPRAS_RP_MAX_ERROR";
        int max=ConfiguracionComprasHelper.parsearEntero(clave,tx.getConfiguracionCompra(clave),false);
        String valor=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(error);
        if(valor==null)valor=ConfiguracionComprasHelper.exigirValor("COMPRAS_RP_ERROR_DEFECTO",tx.getConfiguracionCompra("COMPRAS_RP_ERROR_DEFECTO"));
        int fin=0,caracteres=0;
        while(fin<valor.length()&&caracteres<max){
            char c=valor.charAt(fin++);
            if(c>=0xD800&&c<=0xDBFF&&fin<valor.length()&&valor.charAt(fin)>=0xDC00&&valor.charAt(fin)<=0xDFFF)fin++;
            caracteres++;
        }
        return valor.substring(0,fin);
    }
}
