package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.*;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * Construye un borrador temporal de Reclamo Prestacional a partir de un
 * requerimiento de compra COTIZADO.
 *
 * Esta clase no inserta cabeceras, prestaciones ni relaciones en base de
 * datos. Sólo arma los objetos requeridos por el editor legacy y los publica
 * en la sesión del usuario.
 *
 * Los identificadores de artículos de Compras no se reutilizan como IDs del
 * nomenclador médico. Se transportan como código visible ART-{id}; el ID real
 * de prestación y el ID real de medicamento permanecen en cero hasta que el
 * usuario confirme el nomenclador correspondiente.
 */
public final class ReclamoPrestacionalCompraPrecargaHelper {

    private static final String RECLAMO_PRESTACION_ESTADO_ORIGINAL =
            "RECLAMO_PRESTACION_ESTADO_ORIGINAL";

    private static final String RECLAMOS_PROCESAR_IMAGENES =
            "RECLAMOS_PROCESAR_IMAGENES";

    private static final BigDecimal CIEN =
            BigDecimal.valueOf(
                    100L
            );

    private static final BigDecimal TOLERANCIA_TOTAL =
            new BigDecimal(
                    "0.01"
            );

    private ReclamoPrestacionalCompraPrecargaHelper() {
    }

    /** Valida el editor activo sin convertir un handoff invalido en alta manual. */
    public static ReclamoPrestacionalCompraContexto validarContextoEditor(
            HttpSession session, String nonce, String origen,
            String usuario, int idReclamoSolicitado) throws Exception {
        return validarContextoEditor(session, nonce, origen, usuario, idReclamoSolicitado, false);
    }

    public static ReclamoPrestacionalCompraContexto validarContextoEditor(
            HttpSession session, String nonce, String origen,
            String usuario, int idReclamoSolicitado, boolean permitirSeleccion) throws Exception {
        Object contextoObj = session.getAttribute(
                WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA);
        Object reclamoObj = session.getAttribute(
                WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
        ReclamoPrestacional reclamo = reclamoObj instanceof ReclamoPrestacional
                ? (ReclamoPrestacional) reclamoObj : null;
        if (!permitirSeleccion && idReclamoSolicitado != Integer.MIN_VALUE && reclamo != null
                && reclamo.getId_reclamo() != idReclamoSolicitado) {
            throw new Exception("El formulario pertenece a otro Reclamo Prestacional. "
                    + "Vuelva a abrir la edicion antes de continuar.");
        }
        if (contextoObj instanceof ReclamoPrestacionalCompraContexto
                && ((ReclamoPrestacionalCompraContexto) contextoObj).tieneIdentidadEditorDesconocida()
                && reclamo != null && reclamo.getId_reclamo() <= 0) {
            throw new Exception("No se puede verificar el contexto de Compras de esta sesion anterior. "
                    + "Vuelva al requerimiento para iniciar nuevamente el reclamo.");
        }
        boolean borradorActivo = contextoObj instanceof ReclamoPrestacionalCompraContexto
                && ((ReclamoPrestacionalCompraContexto) contextoObj).esBorradorEnEdicion(reclamo);
        if (WebKeysCompras.isEmpty(nonce)) {
            if ("compras".equalsIgnoreCase(origen)
                    || (contextoObj != null && borradorActivo)) {
                throw new Exception("El contexto de Compras requiere un nonce valido. "
                        + "Vuelva al requerimiento para abrir el Reclamo Prestacional.");
            }
            // Retirar solo metadata residual: conservar cabecera/listas ordinarias.
            if (contextoObj != null) {
                session.removeAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA);
            }
            return null;
        }
        if (!(contextoObj instanceof ReclamoPrestacionalCompraContexto)) {
            throw new Exception("El contexto de Compras expiro o ya no esta disponible.");
        }
        ReclamoPrestacionalCompraContexto contexto =
                (ReclamoPrestacionalCompraContexto) contextoObj;
        if (!contexto.coincideNonce(nonce)
                || !contexto.perteneceAUsuario(usuario)
                || !contexto.estaVigente(System.currentTimeMillis())) {
            throw new Exception("El contexto de Compras no es valido o vencio.");
        }
        if (!borradorActivo || idReclamoSolicitado > 0) {
            throw new Exception("El formulario ya no corresponde al borrador de Compras. "
                    + "Vuelva al requerimiento para abrir el Reclamo Prestacional correcto.");
        }
        return contexto;
    }

    /**
     * Precarga la cabecera y las prestaciones temporales utilizadas por el
     * editor de Reclamos Prestacionales.
     */
    public static Precarga precargar(
            HttpSession session,
            String nonceRequest,
            String usuarioActual) throws Exception {

        if (session == null) {
            throw new Exception(
                    "No se pudo obtener la sesión para precargar "
                            + "el Reclamo Prestacional."
            );
        }

        if (WebKeysCompras.isEmpty(
                nonceRequest
        )) {
            throw new Exception(
                    "No se informó el identificador de la precarga."
            );
        }

        ReclamoPrestacionalCompraContexto contexto;

        synchronized (session) {
            validarSinReclamoEnEdicion(
                    session
            );

            contexto =
                    obtenerContextoValido(
                            session,
                            nonceRequest,
                            usuarioActual
                    );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                contexto
                                        .getIdRequerimientoCompra()
                        );

        validarRequerimiento(
                requerimiento,
                contexto
        );

        ReclamoPrestacional reclamo =
                crearReclamo(
                        requerimiento
                );

        List<PrestacionesReclamo> prestaciones =
                crearPrestaciones(
                        requerimiento
                );

        if (prestaciones.isEmpty()) {
            throw new Exception(
                    "El requerimiento COTIZADO no contiene "
                            + "ítems para precargar."
            );
        }

        List<RevisionesReclamo> revisiones =
                new ArrayList<RevisionesReclamo>();
        revisiones.add(RequerimientoCompraReclamoPrestacionalHelper
                .crearRevisionInicialCompra());

        List<ContactoCRM> contactos =
                new ArrayList<ContactoCRM>();

        /*
         * La cabecera y el atributo específico de sesión comparten la misma
         * instancia de lista. El JSP cuenta desde el reclamo, pero la tabla
         * obtiene las filas desde LISTADO_PRESTACIONES_RECLAMOS_EN_SESION.
         */
        reclamo.setPrestaciones(
                prestaciones
        );

        reclamo.setRevisiones(
                revisiones
        );

        reclamo.setContactosCRM(
                contactos
        );

        Precarga precarga = null;

        try {
            synchronized (session) {
                /*
                 * Se revalidan contexto y edición antes de escribir para
                 * detectar una petición concurrente.
                 */
                obtenerContextoValido(
                        session,
                        nonceRequest,
                        usuarioActual
                );

                validarSinReclamoEnEdicion(
                        session
                );

                precarga =
                        new Precarga(
                                nonceRequest,
                                reclamo,
                                prestaciones,
                                null,
                                revisiones,
                                contactos,
                                session.getAttribute(
                                        WebKeysAutorizaciones
                                                .RECLAMO_PRESTACION_EN_EDICION
                                ),
                                session.getAttribute(
                                        WebKeysAutorizaciones
                                                .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
                                ),
                                session.getAttribute(
                                        WebKeysAutorizaciones
                                                .PRESTACION_EN_PROCESO_DE_EDICION
                                ),
                                session.getAttribute(
                                        WebKeysAutorizaciones
                                                .LISTADO_REVISIONES_RECLAMOS_EN_SESION
                                ),
                                session.getAttribute(
                                        WebKeysAutorizaciones
                                                .LISTADO_CONTACTOS_RECLAMOS_EN_SESION
                                )
                        );

                registrarDetallesOrigen(contexto, requerimiento, prestaciones);
                contexto.setReclamoEnEdicion(reclamo);

                session.setAttribute(
                        WebKeysAutorizaciones
                                .RECLAMO_PRESTACION_EN_EDICION,
                        reclamo
                );

                session.setAttribute(
                        WebKeysAutorizaciones
                                .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION,
                        prestaciones
                );

                session.setAttribute(
                        WebKeysAutorizaciones
                                .LISTADO_REVISIONES_RECLAMOS_EN_SESION,
                        revisiones
                );

                session.setAttribute(
                        WebKeysAutorizaciones
                                .LISTADO_CONTACTOS_RECLAMOS_EN_SESION,
                        contactos
                );
            }

            return precarga;

        } catch (Exception e) {
            limpiarHandoffFallido(
                    session,
                    nonceRequest,
                    precarga
            );

            throw e;
        }
    }

    /**
     * Compensa exclusivamente los objetos escritos por la precarga indicada.
     *
     * La comparación se realiza por identidad para no eliminar objetos que
     * otra petición haya escrito posteriormente.
     */
    public static void limpiarHandoffFallido(
            HttpSession session,
            String nonce,
            Precarga precarga) {

        if (session == null
                || WebKeysCompras.isEmpty(
                nonce
        )) {

            return;
        }

        synchronized (session) {
            Object contextoObj =
                    session.getAttribute(
                            WebKeysCompras
                                    .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
                    );

            if (!(contextoObj
                    instanceof ReclamoPrestacionalCompraContexto)) {

                return;
            }

            ReclamoPrestacionalCompraContexto contexto =
                    (ReclamoPrestacionalCompraContexto)
                            contextoObj;

            if (!contexto.coincideNonce(
                    nonce
            )) {
                return;
            }

            if (precarga != null
                    && precarga.coincideNonce(
                    nonce
            )) {

                restaurarAtributoSiPertenece(
                        session,
                        WebKeysAutorizaciones
                                .RECLAMO_PRESTACION_EN_EDICION,
                        precarga.getReclamoCreado(),
                        precarga.getReclamoAnterior()
                );

                restaurarAtributoSiPertenece(
                        session,
                        WebKeysAutorizaciones
                                .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION,
                        precarga.getPrestacionesCreadas(),
                        precarga.getPrestacionesAnteriores()
                );

                restaurarAtributoSiPertenece(
                        session,
                        WebKeysAutorizaciones
                                .PRESTACION_EN_PROCESO_DE_EDICION,
                        precarga.getPrestacionEnEdicionCreada(),
                        precarga.getPrestacionEnEdicionAnterior()
                );

                restaurarAtributoSiPertenece(
                        session,
                        WebKeysAutorizaciones
                                .LISTADO_REVISIONES_RECLAMOS_EN_SESION,
                        precarga.getRevisionesCreadas(),
                        precarga.getRevisionesAnteriores()
                );

                restaurarAtributoSiPertenece(
                        session,
                        WebKeysAutorizaciones
                                .LISTADO_CONTACTOS_RECLAMOS_EN_SESION,
                        precarga.getContactosCreados(),
                        precarga.getContactosAnteriores()
                );
            }

            session.removeAttribute(
                    WebKeysCompras
                            .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
            );
        }
    }

    /**
     * Prepara la sesión para consultar el RP persistido asociado al
     * requerimiento solicitado.
     *
     * Un RP ya persistido que permanezca en el editor es estado transitorio
     * de navegación y no puede bloquear ni sustituir el vínculo persistido
     * de Compras.
     *
     * Se descarta toda edición temporal anterior, incluso un borrador sin
     * guardar, sin insertar ni modificar reclamos en base de datos.
     */
    public static void prepararSesionParaConsulta(
            HttpSession session,
            int idRequerimientoCompra,
            int idReclamoPrestacional) throws Exception {

        if (session == null) {
            throw new Exception(
                    "No se pudo obtener la sesión del usuario."
            );
        }

        if (idRequerimientoCompra <= 0
                || idReclamoPrestacional <= 0) {

            throw new Exception(
                    "No se pudo determinar el Reclamo Prestacional "
                            + "que debe consultarse."
            );
        }

        synchronized (session) {
            limpiarEstadoEditorSincronizado(session);
        }
    }

    /**
     * Inicia de forma autoritativa un nuevo handoff Compras -> RP.
     *
     * La operación es deliberadamente destructiva respecto del estado
     * transitorio del editor existente en esta HttpSession:
     *
     * - descarta una cabecera RP en edición;
     * - descarta prestaciones, revisiones y contactos temporales;
     * - descarta una prestación en proceso de edición;
     * - descarta selecciones y metadata auxiliar del editor;
     * - descarta contextos y recuperaciones anteriores de Compras.
     *
     * No elimina ni modifica ningún Reclamo Prestacional persistido.
     *
     * La limpieza y la publicación del nuevo contexto ocurren bajo el mismo
     * lock de sesión para impedir que otra petición se interponga entre ambas.
     */
    public static void reiniciarYRegistrarContextoDesdeCompras(
            HttpSession session,
            ReclamoPrestacionalCompraContexto nuevoContexto)
            throws Exception {

        if (session == null) {
            throw new Exception(
                    "No se pudo obtener la sesión del usuario."
            );
        }

        if (nuevoContexto == null) {
            throw new Exception(
                    "No se pudo construir el contexto temporal "
                            + "de Compras."
            );
        }

        if (nuevoContexto.getIdRequerimientoCompra() <= 0) {
            throw new Exception(
                    "El contexto de Compras no contiene "
                            + "un requerimiento válido."
            );
        }

        if (WebKeysCompras.isEmpty(
                nuevoContexto.getNonce()
        )) {

            throw new Exception(
                    "El contexto de Compras no contiene "
                            + "un identificador válido."
            );
        }

        if (WebKeysCompras.isEmpty(
                nuevoContexto.getUsuarioInicio()
        )) {

            throw new Exception(
                    "El contexto de Compras no contiene "
                            + "un usuario válido."
            );
        }

        if (!nuevoContexto.estaVigente(
                System.currentTimeMillis()
        )) {

            throw new Exception(
                    "El contexto de Compras no se encuentra vigente."
            );
        }

        synchronized (session) {

            /*
             * El requerimiento seleccionado en Compras es autoritativo
             * respecto de todo estado transitorio del editor de RP.
             */
            limpiarEstadoEditorSincronizado(
                    session
            );

            session.setAttribute(
                    WebKeysCompras
                            .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA,
                    nuevoContexto
            );
        }
    }

    public static void limpiarEstadoEditorReclamoPrestacional(
            HttpSession session) {

        if (session == null) {
            return;
        }

        synchronized (session) {
            limpiarEstadoEditorSincronizado(
                    session
            );
        }
    }

    private static void limpiarEstadoEditorSincronizado(
            HttpSession session) {

        session.removeAttribute("rp.nuevoReclamo.nonce");
        session.removeAttribute("rp.nuevoReclamo.editor");
        session.removeAttribute("rp.nuevoReclamo.contextoCompra");
        session.removeAttribute(
            WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
        );
        session.removeAttribute(
                WebKeysCompras.RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA
        );
        session.removeAttribute(
                WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION
        );
        session.removeAttribute(
                WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
        );
        session.removeAttribute(
                WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION
        );
        session.removeAttribute(
                WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION
        );
        session.removeAttribute(
                WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION
        );
        session.removeAttribute(
                WebKeysAutorizaciones.RECLAMO_NUEVO_ESTADO_OBS
        );
        session.removeAttribute(
                WebKeysAutorizaciones
                        .LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION
        );
        session.removeAttribute(
                WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT
        );
        session.removeAttribute(
                RECLAMO_PRESTACION_ESTADO_ORIGINAL
        );
        session.removeAttribute(
                RECLAMOS_PROCESAR_IMAGENES
        );
    }

    private static boolean hayEstadoEdicionEnSesion(
            HttpSession session) {

        if (session == null) {
            return false;
        }

        return session.getAttribute(
                WebKeysAutorizaciones
                        .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
        ) != null
                || session.getAttribute(
                WebKeysAutorizaciones
                        .PRESTACION_EN_PROCESO_DE_EDICION
        ) != null
                || session.getAttribute(
                WebKeysAutorizaciones
                        .LISTADO_REVISIONES_RECLAMOS_EN_SESION
        ) != null
                || session.getAttribute(
                WebKeysAutorizaciones
                        .LISTADO_CONTACTOS_RECLAMOS_EN_SESION
        ) != null
                || session.getAttribute(
                WebKeysAutorizaciones
                        .RECLAMO_NUEVO_ESTADO_OBS
        ) != null
                || session.getAttribute(
                WebKeysAutorizaciones
                        .LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION
        ) != null
                || session.getAttribute(
                WebKeysAutorizaciones
                        .RECLAMO_PRESTACION_CUENTA_SELECT
        ) != null
                || session.getAttribute(
                RECLAMO_PRESTACION_ESTADO_ORIGINAL
        ) != null
                || session.getAttribute(
                RECLAMOS_PROCESAR_IMAGENES
        ) != null;
    }

    private static void restaurarAtributoSiPertenece(
            HttpSession session,
            String nombre,
            Object valorCreado,
            Object valorAnterior) {

        Object valorActual =
                session.getAttribute(
                        nombre
                );

        if (valorActual != valorCreado) {
            return;
        }

        if (valorAnterior == null) {
            session.removeAttribute(
                    nombre
            );
        } else {
            session.setAttribute(
                    nombre,
                    valorAnterior
            );
        }
    }

    /**
     * Crea únicamente la cabecera temporal del RP. Al no persistirse, el ID
     * sigue siendo el valor inicial del bean y no se relaciona con el ID del
     * requerimiento.
     */
    public static ReclamoPrestacional crearReclamo(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            throw new Exception(
                    "No se pudo obtener el requerimiento de compra."
            );
        }

        String sector =
                mapearSector(
                        requerimiento
                                .getSectorDescripcion()
                );

        if (WebKeysCompras.isEmpty(
                sector
        )) {
            throw new Exception(
                    "El sector de Compras '"
                            + requerimiento.getSectorDescripcionVisible()
                            + "' no permite generar un Reclamo Prestacional."
            );
        }

        Date fechaAlta =
                new Date();

        int integrante =
                requerimiento.getAfiliadoInt() != null
                        ? requerimiento
                          .getAfiliadoInt()
                          .intValue()
                        : 0;

        ReclamoPrestacional reclamo =
                new ReclamoPrestacional(
                        requerimiento
                                .getAfiliadoCuilTitular(),
                        integrante,
                        fechaAlta,
                        sector,
                        null
                );

        reclamo.setAlta_fecha(
                fechaAlta
        );

        reclamo.setOspim_fecha(
                fechaAlta
        );

        reclamo.setTipoPedido(
                "EXCEPCION"
        );

        /* Estado PENDIENTE del alta normal de Reclamos Prestacionales. */
        reclamo.setEstado(
                WebKeysAutorizaciones
                        .RECLAMO_PRESTACIONAL_ESTADO_CARGADO
        );

        reclamo.setSuperintendencia(
                requerimiento.isSurge()
        );

        reclamo.setAmparo(
                requerimiento.isLegales()
        );

        reclamo.setRecuperable(
                false
        );

        /*
         * El flag recuperable de la cabecera es independiente del combo
         * Integración. El combo persiste el código real del catálogo.
         */
        reclamo.setCodigoIntegracion(
                resolverCodigoIntegracionCabeceraNoRecuperable()
        );

        reclamo.setDebitoPrestadora(
                porcentaje(
                        requerimiento
                                .getCargoTercerizadora()
                ) > 0
        );

        return reclamo;
    }

    public static List<PrestacionesReclamo> crearPrestaciones(
            RequerimientoCompra requerimiento) throws Exception {

        List<PrestacionesReclamo> prestaciones =
                new ArrayList<PrestacionesReclamo>();

        if (requerimiento == null
                || requerimiento.getDetalles() == null) {

            return prestaciones;
        }

        validarPorcentajes(
                requerimiento
        );

        Date fechaPrestacion =
                requerimiento.getIdRequerimientoCompra() > 0
                        ? resolverFechaPrestacionCompra(
                                requerimiento.getIdRequerimientoCompra()
                        )
                        : null;

        BigDecimal totalComprobante =
                BigDecimal.ZERO.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        String cuitComprobante =
                null;

        Integer idPrestadorComprobante =
                null;

        String frecuenciaInicial = ConfiguracionComprasHelper.getFrecuenciaInicialRP();
        String comprobanteInicial = ConfiguracionComprasHelper.getComprobanteInicialRP();
        String sucursalInicial = ConfiguracionComprasHelper.getSucursalComprobanteInicialRP();
        int estadoInicial = ConfiguracionComprasHelper.getEstadoPrestacionInicialRP();
        int tipoInicial = ConfiguracionComprasHelper.getTipoPrestacionInicialRP();
        int maxObservacion = ConfiguracionComprasHelper.getMaxObservacionRP();
        int idRegistro = 1;

        for (RequerimientoCompraDetalle detalle
                : requerimiento.getDetalles()) {

            if (detalle == null) {
                continue;
            }

            PrestacionesReclamo prestacion =
                    crearPrestacion(
                            requerimiento,
                            detalle,
                            idRegistro,
                            fechaPrestacion, frecuenciaInicial, comprobanteInicial,
                            sucursalInicial, estadoInicial, tipoInicial, maxObservacion
                    );

            String cuitDetalle =
                    normalizarCuit(
                            detalle.getPrestadorCuit()
                    );

            Integer idPrestadorDetalle =
                    detalle.getIdPrestador();

            if (idPrestadorDetalle == null
                    || idPrestadorDetalle.intValue() <= 0) {

                throw new Exception(
                        "El ítem "
                                + detalle.getIdString()
                                + " del requerimiento COTIZADO no tiene "
                                + "un prestador adjudicado válido."
                );
            }

            if (idPrestadorComprobante == null) {
                idPrestadorComprobante = idPrestadorDetalle;
            } else if (idPrestadorComprobante.intValue()
                    != idPrestadorDetalle.intValue()) {

                throw new Exception(
                        "El requerimiento COTIZADO contiene ítems "
                                + "adjudicados a prestadores diferentes."
                );
            }

            if (cuitDetalle == null) {
                throw new Exception(
                        "El item "
                                + detalle.getIdString()
                                + " del requerimiento COTIZADO no tiene "
                                + "un CUIT de prestador válido para "
                                + "consolidar el comprobante."
                );
            }

            if (cuitComprobante == null) {
                cuitComprobante =
                        cuitDetalle;

            } else if (!cuitComprobante.equals(
                    cuitDetalle
            )) {

                throw new Exception(
                        "El requerimiento COTIZADO contiene items "
                                + "adjudicados a prestadores con CUIT "
                                + "diferentes. No se puede precargar "
                                + "un único comprobante consolidado."
                );
            }

            BigDecimal cantidadDetalle =
                    BigDecimal.valueOf(
                            detalle
                                    .getCantidad()
                                    .intValue()
                    );

            BigDecimal importeUnitarioDetalle =
                    normalizarImporte(
                            detalle
                                    .getPrecioUnitarioEstimado()
                    );

            BigDecimal totalDetalle =
                    cantidadDetalle.multiply(
                            importeUnitarioDetalle
                    ).setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            totalComprobante =
                    totalComprobante.add(
                            totalDetalle
                    ).setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            prestaciones.add(
                    prestacion
            );

            idRegistro++;
        }

        if (!prestaciones.isEmpty()) {
            precargarImportesComprobanteConsolidado(
                    prestaciones,
                    totalComprobante
            );
        }

        return prestaciones;
    }

    /*
     * Criterio provisional, pendiente de QA: fecha del ultimo adjunto cargado.
     * El mayor ID identifica la ultima alta, sin depender del orden de consulta.
     * No se elige la fecha de documento mayor: puede ser anterior en ese adjunto.
     */
    private static Date resolverFechaPrestacionCompra(
            int idRequerimientoCompra) throws Exception {

        List<RequerimientoCompraPresupuesto> adjuntos =
                BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(
                        idRequerimientoCompra
                );

        RequerimientoCompraPresupuesto masReciente = null;

        for (RequerimientoCompraPresupuesto adjunto : adjuntos) {
            if (adjunto == null
                    || !adjunto.isActivo()
                    || adjunto.getIdRequerimientoPresupuesto() == null) {
                continue;
            }

            if (masReciente == null
                    || adjunto.getIdRequerimientoPresupuesto().intValue()
                    > masReciente.getIdRequerimientoPresupuesto().intValue()) {
                masReciente = adjunto;
            }
        }

        return masReciente != null
                ? masReciente.getFechaDocumento()
                : null;
    }

    public static String mapearSector(
            String sectorCompras) {

        return WebKeysCompras
                .getSectorReclamoPrestacional(
                        sectorCompras
                );
    }

    private static int
    resolverCodigoIntegracionCabeceraNoRecuperable()
            throws Exception {

        List<ReclamosPrestacionalesIntegracion> integraciones =
                TraeListasServiceUtil
                        .getReclamosPrestacionalesIntegracion();

        String descripcionNoRecuperable = ConfiguracionComprasHelper.getDescripcionNoRecuperableRP();
        Integer codigoEncontrado = null;

        if (integraciones != null) {
            for (ReclamosPrestacionalesIntegracion integracion
                    : integraciones) {

                if (integracion == null
                        || integracion.getDescripcion() == null
                        || !descripcionNoRecuperable
                        .equalsIgnoreCase(
                                integracion
                                        .getDescripcion()
                                        .trim()
                        )) {

                    continue;
                }

                if (integracion.getId() <= 0) {
                    throw new Exception(
                            "La integración NO ES RECUPERABLE "
                                    + "no tiene un código válido."
                    );
                }

                if (codigoEncontrado != null) {
                    throw new Exception(
                            "Existe más de una integración "
                                    + "NO ES RECUPERABLE."
                    );
                }

                codigoEncontrado =
                        Integer.valueOf(
                                integracion.getId()
                        );
            }
        }

        if (codigoEncontrado == null) {
            throw new Exception(
                    "No se encontró la integración "
                            + "NO ES RECUPERABLE."
            );
        }

        return codigoEncontrado.intValue();
    }

    /**
     * Regla del flujo Compras -> Reclamo Prestacional:
     *
     * - Surge = recuperable SUR.
     * - Sin Surge = no recuperable.
     *
     * Recupero no habilita Integración en este flujo.
     */
    public static int resolverRecuperable(
            RequerimientoCompra requerimiento) {

        if (requerimiento != null
                && requerimiento.isSurge()) {

            return ConfiguracionComprasHelper.getRecuperableSur();
        }

        return ConfiguracionComprasHelper.getNoRecuperable();
    }

    public static void validarRecuperablesParaGuardar(
            RequerimientoCompra requerimiento,
            List<PrestacionesReclamo> prestaciones) throws Exception {

        if (requerimiento == null) {
            throw new Exception(
                    "No se pudo validar la recuperabilidad del requerimiento."
            );
        }

        int recuperableEsperado =
                resolverRecuperable(
                        requerimiento
                );

        for (int i = 0;
                prestaciones != null && i < prestaciones.size();
                i++) {

            PrestacionesReclamo prestacion =
                    prestaciones.get(i);

            if (prestacion == null
                    || PrestacionesReclamo.ESTADOS.BAJA.equals(
                            prestacion.getEstado()
                    )) {

                continue;
            }

            if (prestacion.getRecuperable() == null
                    || prestacion.getRecuperable().intValue()
                    != recuperableEsperado) {

                throw new Exception(
                        requerimiento.isSurge()
                                ? "Las prestaciones del reclamo deben estar "
                                        + "marcadas como SURGE."
                                : "Las prestaciones del reclamo deben estar "
                                        + "marcadas como NO RECUPERABLE."
                );
            }
        }
    }

    /**
     * Construye una prestación CARGADA, no AUTORIZADA.
     *
     * Los valores del área médica y del comprobante se precargan desde
     * la cotización del requerimiento para que el usuario pueda revisarlos.
     * estadoRechazoAprobado permanece en 0.
     */
    private static PrestacionesReclamo crearPrestacion(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle,
            int idRegistro,
            Date fechaPrestacion, String frecuenciaInicial, String comprobanteInicial,
            String sucursalInicial, int estadoInicial, int tipoInicial, int maxObservacion)
            throws Exception {

        validarDetalleCotizado(
                detalle
        );

        BigDecimal cantidad =
                BigDecimal.valueOf(
                        detalle
                                .getCantidad()
                                .intValue()
                );

        BigDecimal importeUnitario =
                normalizarImporte(
                        detalle
                                .getPrecioUnitarioEstimado()
                );

        BigDecimal totalCalculado =
                cantidad.multiply(
                        importeUnitario
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal totalInformado =
                normalizarImporte(
                        detalle
                                .getPrecioTotalEstimado()
                );

        validarConsistenciaTotal(
                detalle,
                totalCalculado,
                totalInformado
        );

        /*
         * Se usa el total recalculado para que los datos del comprobante,
         * el total autorizado y la distribución de cargos partan de la
         * misma base monetaria.
         */
        BigDecimal total =
                totalCalculado;

        BigDecimal cargoOspim =
                total.multiply(
                        BigDecimal.valueOf(
                                porcentaje(
                                        requerimiento
                                                .getCargoOspim()
                                )
                        )
                ).divide(
                        CIEN,
                        2,
                        RoundingMode.HALF_UP
                );

        /*
         * El remanente se asigna a la tercerizadora para evitar diferencias
         * de centavos por dos redondeos independientes.
         */
        BigDecimal cargoTercerizadora =
                total.subtract(
                        cargoOspim
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        PrestacionesReclamo prestacion =
                new PrestacionesReclamo();

        /* Identificador temporal de la fila dentro de la sesión. */
        prestacion.setIdRegistro(
                idRegistro
        );

        aplicarReferenciaTecnica(
                prestacion,
                requerimiento,
                detalle, tipoInicial, maxObservacion
        );

        prestacion.setFrecuencia(frecuenciaInicial);

        /*
         * Precarga exclusivamente los datos de comprobante que tienen
         * correspondencia directa y verificable en Compras.
         */
        precargarDatosComprobante(
                prestacion,
                detalle,
                cantidad,
                importeUnitario,
                total, comprobanteInicial, sucursalInicial
        );

        /* Propuesta para Autorizado por Área Médica. */
        prestacion.setCantidad(
                cantidad.doubleValue()
        );

        prestacion.setImporte(
                importeUnitario.doubleValue()
        );

        prestacion.setCargo_ospim(
                cargoOspim.doubleValue()
        );

        prestacion.setCargo_ps(
                cargoTercerizadora.doubleValue()
        );

        prestacion.setCargo_imesa(
                Double.valueOf(
                        0D
                )
        );

        prestacion.setReconocidoSSS(
                requerimiento.isSurge()
                        ? total.doubleValue()
                        : 0D
        );

        prestacion.setRecuperable(
                Integer.valueOf(
                        resolverRecuperable(
                                requerimiento
                        )
                )
        );

        prestacion.setRecuperableSur(
                Boolean.valueOf(
                        requerimiento.isSurge()
                )
        );

        prestacion.setIdTercerizadora(
                porcentaje(
                        requerimiento
                                .getCargoTercerizadora()
                ) > 0
                        ? requerimiento.getIdTercerizadora()
                        : null
        );

        prestacion.setCuilTitular(
                requerimiento
                        .getAfiliadoCuilTitular()
        );

        prestacion.setInte(
                requerimiento.getAfiliadoInt() != null
                        ? requerimiento
                          .getAfiliadoInt()
                          .intValue()
                        : 0
        );

        /* Fecha del adjunto de Compras; conserva la edicion manual legacy. */
        prestacion.setFechaPrestacion(
                fechaPrestacion != null
                        ? new Date(fechaPrestacion.getTime())
                        : null
        );

        prestacion.setEstado(
                PrestacionesReclamo
                        .ESTADOS
                        .NUEVO
        );

        /* CARGADO: no implica autorización médica. */
        prestacion.setEstadoRechazoAprobado(
                estadoInicial
        );

        return prestacion;
    }

    /**
     * Precarga los datos de comprobante que poseen una correspondencia
     * inequívoca con el detalle cotizado de Compras.
     *
     * Por regla de negocio del handoff desde Compras, el tipo inicial
     * del comprobante es OTR.
     *
     * La sucursal 000 corresponde al prestador asociado al CUIT,
     * no al punto de venta o sucursal del comprobante.
     *
     * No se inventan letra, punto de venta, número ni fechas.
     */
    private static void precargarDatosComprobante(
            PrestacionesReclamo prestacion,
            RequerimientoCompraDetalle detalle,
            BigDecimal cantidad,
            BigDecimal importeUnitario,
            BigDecimal total, String comprobanteInicial, String sucursalInicial) {

        if (prestacion == null
                || detalle == null) {

            return;
        }

        prestacion.setComprobanteTipo(
                comprobanteInicial
        );

        prestacion.setComprobanteCUITSucursal(
                sucursalInicial
        );

        if (cantidad != null) {
            prestacion.setComprobanteCantidad(
                    Double.valueOf(
                            cantidad.doubleValue()
                    )
            );
        }

        if (importeUnitario != null) {
            prestacion.setComprobanteImporte(
                    Double.valueOf(
                            importeUnitario.doubleValue()
                    )
            );
        }

        if (total != null) {
            prestacion.setComprobanteTotal(
                    Double.valueOf(
                            total.doubleValue()
                    )
            );
        }

        String prestadorCuit =
                WebKeysCompras.trimToNull(
                        detalle.getPrestadorCuit()
                );

        if (prestadorCuit != null) {
            prestacion.setComprobanteCUIT(
                    prestadorCuit
            );
        }

        String prestadorRazonSocial =
                WebKeysCompras.trimToNull(
                        detalle.getPrestadorRazonSocial()
                );

        if (prestadorRazonSocial != null) {
            prestacion.setComprobanteRazonSocial(
                    prestadorRazonSocial
            );
        }
    }

    /**
     * El handoff desde Compras representa un único comprobante para todas
     * las prestaciones precargadas.
     *
     * El modelo legacy almacena los datos del comprobante dentro de cada
     * PrestacionesReclamo. Se replica por eso el mismo importe consolidado
     * sin modificar cantidad, importe ni cargos del Área Médica.
     */
    private static void precargarImportesComprobanteConsolidado(
            List<PrestacionesReclamo> prestaciones,
            BigDecimal totalComprobante) {

        if (prestaciones == null
                || prestaciones.isEmpty()
                || totalComprobante == null) {

            return;
        }

        BigDecimal totalNormalizado =
                totalComprobante.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        Double totalLegacy =
                Double.valueOf(
                        totalNormalizado.doubleValue()
                );

        for (PrestacionesReclamo prestacion
                : prestaciones) {

            if (prestacion == null) {
                continue;
            }

            prestacion.setComprobanteCantidad(
                    Double.valueOf(
                            1D
                    )
            );

            prestacion.setComprobanteImporte(
                    totalLegacy
            );

            prestacion.setComprobanteTotal(
                    totalLegacy
            );
        }
    }
    private static void aplicarReferenciaTecnica(
            PrestacionesReclamo prestacion,
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle, int tipoInicial, int maxObservacion)
            throws Exception {

        String sector =
                mapearSector(
                        requerimiento.getSectorDescripcion()
                );

        if (WebKeysCompras.isEmpty(
                sector
        )) {
            throw new IllegalArgumentException(
                    "El sector del requerimiento no tiene "
                            + "una configuración técnica válida."
            );
        }

        prestacion.setId_prestacionrecord(
                0
        );

        prestacion.setTipoPrestacion(tipoInicial);

        /*
         * Los nuevos detalles de Compras utilizan NOMENCLADOR.
         *
         * La referencia se recupera nuevamente desde el nomenclador
         * canónico porque el tipo, la marca ReinLiq, el código y el
         * estado activo no deben confiarse a los datos de sesión o HTTP.
         */
        if (detalle.tieneNomenclador()) {
            int idPrestacionDetalle =
                    detalle.getIdPrestacionInt();

            int idTipoNomencladorDetalle =
                    detalle.getIdTipoNomencladorInt();

            Nomenclador nomenclador =
                    NomencladorServiceUtil
                            .buscarNomencladorPorId(
                                    idPrestacionDetalle
                            );

            if (nomenclador == null
                    || nomenclador.getId_prestacion()
                    != idPrestacionDetalle
                    || nomenclador.getBaja_fecha() != null) {

                throw new IllegalArgumentException(
                        "La prestación seleccionada ya no existe "
                                + "o no se encuentra activa."
                );
            }

            if (nomenclador.getId_tipo_nomenclador()
                    != idTipoNomencladorDetalle) {

                throw new IllegalArgumentException(
                        "El tipo de nomenclador persistido no "
                                + "corresponde a la prestación seleccionada."
                );
            }

            // La seleccion ya persistida conserva identidad y tipo canonicos.
            // La matriz vigente se aplica solo a selecciones nuevas o cambios.
            String codigoCanonico =
                    nomenclador.getCodigo() == null
                            ? ""
                            : nomenclador.getCodigo().trim();

            String descripcionCanonica =
                    nomenclador.getDescripcion() == null
                            ? ""
                            : nomenclador.getDescripcion().trim();

            if (WebKeysCompras.isEmpty(
                    codigoCanonico
            )) {
                throw new IllegalArgumentException(
                        "La prestación seleccionada no tiene "
                                + "un código válido."
                );
            }

            if (WebKeysCompras.isEmpty(
                    descripcionCanonica
            )) {
                throw new IllegalArgumentException(
                        "La prestación seleccionada no tiene "
                                + "una descripción válida."
                );
            }

            prestacion.setId_medicamento(
                    0
            );

            prestacion.setId_prestacion(
                    nomenclador.getId_prestacion()
            );

            prestacion.setCodigoPrestacion(
                    codigoCanonico
            );

            prestacion.setNombreprestacion(
                    descripcionCanonica
            );

            prestacion.setDescripcion(
                    descripcionCanonica
            );

            prestacion.setNombremedicacion(
                    ""
            );

            return;
        }

        /*
         * Compatibilidad histórica:
         *
         * Las filas que ya estaban persistidas como MEDICAMENTO
         * continúan siendo utilizables exclusivamente en Farmacia.
         * No se habilitan nuevas altas de este tipo.
         */
        if (detalle.tieneMedicamento()) {
            if (!NomencladorCompraBusquedaHelper.esSectorFarmacia(
                    requerimiento.getSectorDescripcion())) {
                throw new IllegalArgumentException(
                        "Un medicamento histórico solo puede "
                                + "utilizarse en Farmacia."
                );
            }

            String nombreMedicamento =
                    detalle.getNombreMedicamentoVisible();

            if (detalle.getIdMedicamentoInt() <= 0
                    || WebKeysCompras.isEmpty(
                    nombreMedicamento
            )) {

                throw new IllegalArgumentException(
                        "El medicamento histórico no conserva "
                                + "una referencia técnica válida."
                );
            }

            prestacion.setId_medicamento(
                    detalle.getIdMedicamentoInt()
            );

            prestacion.setId_prestacion(
                    0
            );

            prestacion.setCodigoPrestacion(
                    detalle.getIdMedicamentoString()
            );

            prestacion.setNombreprestacion(
                    nombreMedicamento
            );

            prestacion.setDescripcion(
                    nombreMedicamento
            );

            prestacion.setNombremedicacion(
                    nombreMedicamento
            );

            return;
        }

        /*
         * Los sectores sin código (actualmente LEGALES dentro del flujo de
         * Reclamo Prestacional) se cotizan válidamente como OBSERVACION.
         *
         * No se fabrica un ID médico: se restaura el contrato original de la
         * integración y se crea una referencia temporal ART-{idDetalle}. El
         * usuario debe confirmar el nomenclador o medicamento real antes de
         * persistir la prestación en Autorizaciones.
         */
        if (detalle.esObservacion()) {
            String descripcionPendiente =
                    WebKeysCompras.trimToNull(
                            detalle.getObservacionesVisible()
                    );

            if (descripcionPendiente == null) {
                throw new IllegalArgumentException(
                        "El detalle de observación de Compras no contiene "
                                + "una descripción para precargar."
                );
            }

            if (descripcionPendiente.length() > maxObservacion) {
                descripcionPendiente =
                        descripcionPendiente.substring(
                                0,
                                maxObservacion
                        );
            }

            String codigoTemporal =
                    detalle.getIdInt() > 0
                            ? "ART-" + detalle.getIdInt()
                            : "COMPRA";

            prestacion.setId_medicamento(
                    0
            );

            prestacion.setId_prestacion(
                    0
            );

            prestacion.setCodigoPrestacion(
                    codigoTemporal
            );

            prestacion.setNombreprestacion(
                    descripcionPendiente
            );

            prestacion.setDescripcion(
                    descripcionPendiente
            );

            prestacion.setNombremedicacion(
                    ""
            );

            return;
        }

        throw new IllegalArgumentException(
                "El detalle de Compras no contiene "
                        + "una referencia técnica válida."
        );
    }

    public static void validarRequerimientoOrigen(RequerimientoCompra requerimiento,
            ReclamoPrestacionalCompraContexto contexto, ReclamoPrestacional reclamo) throws Exception {
        validarRequerimiento(requerimiento, contexto);
        String cuil = normalizarCuit(reclamo.getCuit_titular());
        if (cuil == null || !cuil.equals(normalizarCuit(requerimiento.getAfiliadoCuilTitular()))
                || requerimiento.getAfiliadoInt() == null
                || requerimiento.getAfiliadoInt().intValue() != reclamo.getInte()) {
            throw new Exception("El afiliado del reclamo no coincide con el requerimiento persistido.");
        }
    }

    private static void validarRequerimiento(
            RequerimientoCompra requerimiento,
            ReclamoPrestacionalCompraContexto contexto)
            throws Exception {

        if (requerimiento == null
                || requerimiento.getBajaFecha() != null) {

            throw new Exception(
                    "El requerimiento de compra ya no está activo."
            );
        }

        if (!WebKeysCompras.esCotizado(
                requerimiento.getEstado()
        )) {
            throw new Exception(
                    "El requerimiento de compra ya no está COTIZADO."
            );
        }

        if (!requerimiento.tieneAfiliadoInformado()) {
            throw new Exception(
                    "El requerimiento COTIZADO no tiene "
                            + "un afiliado válido."
            );
        }

        String cuilRequerimiento =
                normalizarCuit(
                        requerimiento
                                .getAfiliadoCuilTitular()
                );

        String cuilContexto =
                normalizarCuit(
                        contexto
                                .getAfiliadoCuilTitular()
                );

        Integer integranteRequerimiento =
                requerimiento.getAfiliadoInt();

        Integer integranteContexto =
                contexto.getAfiliadoInt();

        if (cuilRequerimiento == null
                || cuilContexto == null
                || integranteRequerimiento == null
                || integranteContexto == null
                || !cuilRequerimiento.equals(
                cuilContexto
        )
                || !integranteRequerimiento.equals(
                integranteContexto
        )) {

            throw new Exception(
                    "El afiliado persistido del requerimiento "
                            + "no coincide con el contexto del borrador."
            );
        }
    }

    /**
     * Valida exclusivamente el contrato económico de una cotización cerrada.
     *
     * La referencia técnica no forma parte de esta validación: Compras admite
     * detalles OBSERVACION en sectores sin código. Esos detalles se convierten
     * luego en una referencia temporal y deben completarse en Autorizaciones.
     */
    private static void validarDetalleCotizado(
            RequerimientoCompraDetalle detalle)
            throws Exception {

        if (detalle == null) {
            throw new Exception(
                    "El requerimiento figura COTIZADO, pero contiene "
                            + "un ítem nulo."
            );
        }

        String idItem =
                !WebKeysCompras.isEmpty(
                        detalle.getIdString()
                )
                        ? detalle.getIdString()
                        : "sin ID";

        if (detalle.getCantidad() == null
                || detalle.getCantidad().intValue() <= 0) {

            throw new Exception(
                    "El ítem " + idItem
                            + " del requerimiento COTIZADO no tiene "
                            + "una cantidad válida."
            );
        }

        BigDecimal precioUnitario =
                detalle.getPrecioUnitarioEstimado();

        if (precioUnitario == null
                || precioUnitario.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new Exception(
                    "El ítem " + idItem
                            + " del requerimiento COTIZADO no tiene "
                            + "un precio unitario válido."
            );
        }

        BigDecimal precioTotal =
                detalle.getPrecioTotalEstimado();

        if (precioTotal == null
                || precioTotal.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new Exception(
                    "El ítem " + idItem
                            + " del requerimiento COTIZADO no tiene "
                            + "un precio total válido."
            );
        }

        if (!detalle.tienePrestadorAdjudicado()) {
            throw new Exception(
                    "El ítem " + idItem
                            + " del requerimiento COTIZADO no tiene "
                            + "un prestador adjudicado."
            );
        }
    }

    private static void validarConsistenciaTotal(
            RequerimientoCompraDetalle detalle,
            BigDecimal totalCalculado,
            BigDecimal totalInformado) throws Exception {

        BigDecimal diferencia =
                totalCalculado.subtract(
                        totalInformado
                ).abs();

        if (diferencia.compareTo(
                TOLERANCIA_TOTAL
        ) > 0) {
            throw new Exception(
                    "El ítem "
                            + detalle.getIdString()
                            + " tiene un total inconsistente con su "
                            + "cantidad y precio unitario."
            );
        }
    }

    private static void validarPorcentajes(
            RequerimientoCompra requerimiento)
            throws Exception {

        int cargoOspim =
                porcentaje(
                        requerimiento.getCargoOspim()
                );

        int cargoTercerizadora =
                porcentaje(
                        requerimiento
                                .getCargoTercerizadora()
                );

        if (cargoOspim < 0
                || cargoOspim > 100
                || cargoTercerizadora < 0
                || cargoTercerizadora > 100
                || cargoOspim + cargoTercerizadora != 100) {

            throw new Exception(
                    "Los porcentajes de cargo deben estar "
                            + "entre 0 y 100 y sumar 100."
            );
        }
    }


    private static void registrarDetallesOrigen(ReclamoPrestacionalCompraContexto contexto,
            RequerimientoCompra requerimiento, List<PrestacionesReclamo> prestaciones)
            throws Exception {
        java.util.Map<Integer, Integer> origen = new java.util.HashMap<Integer, Integer>();
        int posicion = 0;
        for (int i = 0; i < requerimiento.getDetalles().size(); i++) {
            RequerimientoCompraDetalle detalle = requerimiento.getDetalles().get(i);
            if (detalle == null) {
                continue;
            }
            if (detalle.getIdInt() <= 0 || posicion >= prestaciones.size()) {
                throw new Exception("No se pudo identificar el detalle de origen de la precarga.");
            }
            PrestacionesReclamo prestacion = prestaciones.get(posicion++);
            if (origen.put(Integer.valueOf(prestacion.getIdRegistro()),
                    Integer.valueOf(detalle.getIdInt())) != null) {
                throw new Exception("La precarga contiene identificadores de fila duplicados.");
            }
        }
        if (posicion != prestaciones.size()) {
            throw new Exception("La precarga no coincide con los detalles persistidos.");
        }
        contexto.definirDetallesOrigen(origen);
    }

    /** La validacion de contexto HTTP/usuario ya se ejecuta en ReclamosBaseAction. */
    public static Nomenclador resolverNomencladorEdicion(HttpSession session,
            PrestacionesReclamo prestacion, int tipo, String codigo, int idResuelto)
            throws Exception {
        return resolverNomencladorEdicion(session, prestacion, tipo, codigo, idResuelto, 1, 0);
    }

    public static Nomenclador resolverNomencladorEdicion(HttpSession session,
            PrestacionesReclamo prestacion, int tipo, String codigo, int idResuelto,
            int nomSeleccionado, int idMedicamentoRecibido) throws Exception {
        Object objeto = session.getAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA);
        if (!(objeto instanceof ReclamoPrestacionalCompraContexto)) {
            throw new Exception("El contexto de Compras ya no esta disponible.");
        }
        ReclamoPrestacionalCompraContexto contexto = (ReclamoPrestacionalCompraContexto) objeto;
        Object reclamo = session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
        if (!contexto.esBorradorEnEdicion(reclamo) || prestacion == null) {
            throw new Exception("La prestacion no pertenece al borrador vigente de Compras.");
        }
        Integer idDetalle = contexto.getIdDetalleOrigen(prestacion.getIdRegistro());
        if (idDetalle == null) {
            throw new Exception("No se puede verificar el detalle de origen. Vuelva al requerimiento.");
        }
        RequerimientoCompra requerimiento = BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(contexto.getIdRequerimientoCompra());
        validarRequerimiento(requerimiento, contexto);
        RequerimientoCompraDetalle origen = encontrarDetalleOrigen(requerimiento, idDetalle.intValue());
        Nomenclador canonicoOrigen = origen.tieneNomenclador()
                ? BusquedaRequerimientoCompraServiceUtil.obtenerNomencladorCanonico(
                    origen.getIdPrestacionInt()) : null;
        String codigoRecibido = codigo != null ? codigo.trim() : "";
        if (origen.tieneNomenclador() && idMedicamentoRecibido > 0) {
            throw new Exception("Una referencia canonica de Compras no puede sustituirse por un medicamento.");
        }
        if (canonicoOrigen != null && nomSeleccionado != 1
                && prestacion.getId_prestacion() == origen.getIdPrestacionInt()
                && (tipo <= 0 || tipo == canonicoOrigen.getId_tipo_nomenclador())
                && (codigoRecibido.length() == 0 || codigoRecibido.equals(
                    canonicoOrigen.getCodigo() != null ? canonicoOrigen.getCodigo().trim() : ""))) {
            validarReferenciaPersistida(origen, canonicoOrigen);
            return canonicoOrigen;
        }
        if (!origen.tieneNomenclador() && nomSeleccionado != 1
                && prestacion.getId_prestacion() <= 0) {
            if (origen.tieneMedicamento()
                    && (idMedicamentoRecibido == 0 || idMedicamentoRecibido == origen.getIdMedicamentoInt())
                    && prestacion.getId_medicamento() == origen.getIdMedicamentoInt()) {
                return null;
            }
            if (origen.esObservacion() && idMedicamentoRecibido <= 0 && idResuelto <= 0
                    && prestacion.getId_medicamento() <= 0) {
                return null;
            }
            throw new Exception("La seleccion no conserva la referencia persistida del detalle de Compras.");
        }
        if (idMedicamentoRecibido > 0) {
            throw new Exception("La seleccion de nomenclador no puede contener un medicamento.");
        }
        if (canonicoOrigen != null && tipo == canonicoOrigen.getId_tipo_nomenclador()
                && codigoRecibido.equals(canonicoOrigen.getCodigo() != null
                    ? canonicoOrigen.getCodigo().trim() : "")) {
            validarReferenciaPersistida(origen, canonicoOrigen);
            return canonicoOrigen;
        }
        int idSeleccionado = idResuelto;
        int tipoSeleccionado = tipo;
        String codigoSeleccionado = codigoRecibido;
        Nomenclador seleccionado;
        if (nomSeleccionado != 1 && prestacion.getId_prestacion() > 0) {
            // Una edicion posterior conserva la seleccion temporal ya validada,
            // pero debe volver a comprobar su configuracion vigente.
            seleccionado = BusquedaRequerimientoCompraServiceUtil
                    .obtenerNomencladorCanonico(prestacion.getId_prestacion());
            if (seleccionado == null || seleccionado.getBaja_fecha() != null
                    || (tipo > 0 && tipo != seleccionado.getId_tipo_nomenclador())
                    || (codigoRecibido.length() > 0 && !codigoRecibido.equals(
                        seleccionado.getCodigo() != null ? seleccionado.getCodigo().trim() : ""))) {
                throw new Exception("La edicion no coincide con la seleccion temporal del nomenclador.");
            }
            idSeleccionado = seleccionado.getId_prestacion();
            tipoSeleccionado = seleccionado.getId_tipo_nomenclador();
            codigoSeleccionado = seleccionado.getCodigo() != null ? seleccionado.getCodigo().trim() : "";
        } else {
            seleccionado = BusquedaRequerimientoCompraServiceUtil.obtenerNomencladorCanonico(idSeleccionado);
        }
        if (seleccionado == null || seleccionado.getId_prestacion() != idSeleccionado
                || seleccionado.getId_tipo_nomenclador() != tipoSeleccionado
                || seleccionado.getBaja_fecha() != null
                || !codigoSeleccionado.equals(seleccionado.getCodigo() != null
                    ? seleccionado.getCodigo().trim() : "")) {
            throw new Exception("La seleccion no coincide con el nomenclador vigente.");
        }
        List<ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra> configuracion =
                BusquedaRequerimientoCompraServiceUtil.getConfiguracionNomenclador(
                        requerimiento.getIdSector().intValue(), origen.getIdTipoPrestacion());
        if (!NomencladorCompraBusquedaHelper.esNomencladorConfigurado(configuracion, seleccionado)) {
            throw new Exception("La prestacion no pertenece a los nomencladores configurados para el detalle.");
        }
        return seleccionado;
    }

    public static RequerimientoCompraDetalle encontrarDetalleOrigen(
            RequerimientoCompra requerimiento, int idDetalle) throws Exception {
        if (requerimiento != null && requerimiento.getDetalles() != null) {
            for (int i = 0; i < requerimiento.getDetalles().size(); i++) {
                RequerimientoCompraDetalle detalle = requerimiento.getDetalles().get(i);
                if (detalle != null && detalle.getIdInt() == idDetalle) {
                    return detalle;
                }
            }
        }
        throw new Exception("El detalle de origen ya no esta disponible en el requerimiento.");
    }

    public static void validarReferenciaPersistida(RequerimientoCompraDetalle origen,
            Nomenclador canonico) throws Exception {
        if (origen == null || !origen.tieneNomenclador() || canonico == null
                || canonico.getId_prestacion() != origen.getIdPrestacionInt()
                || canonico.getBaja_fecha() != null
                || canonico.getId_tipo_nomenclador() <= 0
                || canonico.getId_tipo_nomenclador() != origen.getIdTipoNomencladorInt()) {
            throw new Exception("La referencia persistida no coincide con una prestacion canonica activa.");
        }
    }

    private static ReclamoPrestacionalCompraContexto
    obtenerContextoValido(
            HttpSession session,
            String nonceRequest,
            String usuarioActual) throws Exception {

        Object contextoObj =
                session.getAttribute(
                        WebKeysCompras
                                .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
                );

        if (!(contextoObj
                instanceof ReclamoPrestacionalCompraContexto)) {

            throw new Exception(
                    "El contexto de Compras expiró "
                            + "o ya no está disponible."
            );
        }

        ReclamoPrestacionalCompraContexto contexto =
                (ReclamoPrestacionalCompraContexto)
                        contextoObj;

        if (!contexto.coincideNonce(
                nonceRequest
        )
                || !contexto.perteneceAUsuario(
                usuarioActual
        )
                || !contexto.estaVigente(
                System.currentTimeMillis()
        )) {

            throw new Exception(
                    "El contexto de Compras no es válido o venció. "
                            + "Vuelva al requerimiento e inicie "
                            + "nuevamente el borrador."
            );
        }

        return contexto;
    }

    private static void validarSinReclamoEnEdicion(
            HttpSession session) throws Exception {

        if (session == null) {
            throw new Exception(
                    "No se pudo obtener la sesión del usuario."
            );
        }

        Object reclamoEnEdicion =
                session.getAttribute(
                        WebKeysAutorizaciones
                                .RECLAMO_PRESTACION_EN_EDICION
                );

        if (reclamoEnEdicion != null
                || hayEstadoEdicionEnSesion(
                session
        )) {

            /*
             * Después del reinicio autoritativo desde Compras esto sólo puede
             * ocurrir si otra petición modificó la sesión concurrentemente.
             *
             * No se debe borrar aquí ese estado: se aborta el handoff actual
             * para no sobreescribir una modificación posterior.
             */
            throw new Exception(
                    "La sesión del editor de Reclamo Prestacional "
                            + "cambió mientras se iniciaba la precarga. "
                            + "Vuelva al requerimiento e intente nuevamente."
            );
        }
    }

    private static BigDecimal normalizarImporte(
            BigDecimal value) throws Exception {

        if (value == null) {
            throw new Exception(
                    "Un ítem cotizado no tiene importe."
            );
        }

        if (value.compareTo(
                BigDecimal.ZERO
        ) < 0) {
            throw new Exception(
                    "Un ítem cotizado tiene un importe negativo."
            );
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private static int porcentaje(
            Integer value) {

        return value != null
                ? value.intValue()
                : 0;
    }

    private static String normalizarCuit(
            String value) {

        if (value == null) {
            return null;
        }

        String normalizado =
                value.replaceAll(
                        "[^0-9]",
                        ""
                );

        return normalizado.length() > 0
                ? normalizado
                : null;
    }

    /**
     * Descriptor inmutable de los objetos escritos por una precarga.
     */
    public static final class Precarga {

        private final String nonce;

        private final ReclamoPrestacional reclamoCreado;

        private final List<PrestacionesReclamo>
                prestacionesCreadas;

        private final PrestacionesReclamo
                prestacionEnEdicionCreada;

        private final List<RevisionesReclamo>
                revisionesCreadas;

        private final List<ContactoCRM>
                contactosCreados;

        private final Object reclamoAnterior;

        private final Object prestacionesAnteriores;

        private final Object prestacionEnEdicionAnterior;

        private final Object revisionesAnteriores;

        private final Object contactosAnteriores;

        private Precarga(
                String nonce,
                ReclamoPrestacional reclamoCreado,
                List<PrestacionesReclamo> prestacionesCreadas,
                PrestacionesReclamo prestacionEnEdicionCreada,
                List<RevisionesReclamo> revisionesCreadas,
                List<ContactoCRM> contactosCreados,
                Object reclamoAnterior,
                Object prestacionesAnteriores,
                Object prestacionEnEdicionAnterior,
                Object revisionesAnteriores,
                Object contactosAnteriores) {

            this.nonce =
                    nonce;

            this.reclamoCreado =
                    reclamoCreado;

            this.prestacionesCreadas =
                    prestacionesCreadas;

            this.prestacionEnEdicionCreada =
                    prestacionEnEdicionCreada;

            this.revisionesCreadas =
                    revisionesCreadas;

            this.contactosCreados =
                    contactosCreados;

            this.reclamoAnterior =
                    reclamoAnterior;

            this.prestacionesAnteriores =
                    prestacionesAnteriores;

            this.prestacionEnEdicionAnterior =
                    prestacionEnEdicionAnterior;

            this.revisionesAnteriores =
                    revisionesAnteriores;

            this.contactosAnteriores =
                    contactosAnteriores;
        }

        private boolean coincideNonce(
                String nonceRequest) {

            return nonce != null
                    && nonce.equals(
                    nonceRequest
            );
        }

        private ReclamoPrestacional getReclamoCreado() {
            return reclamoCreado;
        }

        private List<PrestacionesReclamo>
        getPrestacionesCreadas() {

            return prestacionesCreadas;
        }

        private PrestacionesReclamo
        getPrestacionEnEdicionCreada() {

            return prestacionEnEdicionCreada;
        }

        private List<RevisionesReclamo>
        getRevisionesCreadas() {

            return revisionesCreadas;
        }

        private List<ContactoCRM>
        getContactosCreados() {

            return contactosCreados;
        }

        private Object getReclamoAnterior() {
            return reclamoAnterior;
        }

        private Object getPrestacionesAnteriores() {
            return prestacionesAnteriores;
        }

        private Object getPrestacionEnEdicionAnterior() {
            return prestacionEnEdicionAnterior;
        }

        private Object getRevisionesAnteriores() {
            return revisionesAnteriores;
        }

        private Object getContactosAnteriores() {
            return contactosAnteriores;
        }
    }
}
