package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.crm.beans.ContactoCRM;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
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
public final class ReclamoPrestacionalCompraPrecargaServiceUtil {

    private static final int RECUPERABLE_SUR = 1;
    private static final int NO_RECUPERABLE = 2;
    private static final int RECUPERABLE_INTEGRACION = 3;

    private static final int ESTADO_PRESTACION_CARGADA = 0;

    /*
     * La pantalla informa un máximo funcional de 200 caracteres, aunque
     * algunos textareas legacy tengan maxlength=250.
     */
    private static final int MAX_OBSERVACION = 200;

    private static final BigDecimal CIEN =
            BigDecimal.valueOf(
                    100L
            );

    private static final BigDecimal TOLERANCIA_TOTAL =
            new BigDecimal(
                    "0.01"
            );

    private ReclamoPrestacionalCompraPrecargaServiceUtil() {
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
                                prestaciones.get(0),
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

                /*
                 * El editor superior trabaja sobre la misma primera fila
                 * temporal ya publicada en la grilla; no agrega otra.
                 */
                session.setAttribute(
                        WebKeysAutorizaciones
                                .PRESTACION_EN_PROCESO_DE_EDICION,
                        prestaciones.get(0)
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
                    "El sector del requerimiento no puede mapearse a un "
                            + "sector de Reclamos Prestacionales."
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

        reclamo.setRecuperable(
                requerimiento.isRecupero()
                        || requerimiento.isSurge()
        );

        reclamo.setSuperintendencia(
                requerimiento.isSurge()
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

        int idRegistro = 1;

        for (RequerimientoCompraDetalle detalle
                : requerimiento.getDetalles()) {

            if (detalle == null) {
                continue;
            }

            prestaciones.add(
                    crearPrestacion(
                            requerimiento,
                            detalle,
                            idRegistro
                    )
            );

            idRegistro++;
        }

        return prestaciones;
    }

    public static String mapearSector(
            String sectorCompras) {

        String sector =
                normalizarTexto(
                        sectorCompras
                );

        if (sector.indexOf(
                "DISCAPAC"
        ) >= 0) {
            return "DISCAPACIDAD";
        }

        if (sector.indexOf(
                "FARMAC"
        ) >= 0) {
            return "FARMACIA";
        }

        if (sector.indexOf(
                "ODONTO"
        ) >= 0) {
            return "ODONTOLOGIA";
        }

        if (sector.indexOf(
                "LEGAL"
        ) >= 0) {
            return "LEGALES";
        }

        if (sector.indexOf(
                "PRESTACION"
        ) >= 0
                && sector.indexOf(
                "MEDIC"
        ) >= 0) {

            return "PRESTACIONES MEDICAS";
        }

        return "";
    }

    /**
     * Regla vigente del proyecto:
     *
     * - Surge = recuperable SUR.
     * - Recupero sin Surge = recuperable por Integración.
     * - Ninguno = no recuperable.
     *
     * Si ambos indicadores vienen activos, SUR tiene prioridad.
     */
    public static int resolverRecuperable(
            RequerimientoCompra requerimiento) {

        if (requerimiento != null
                && requerimiento.isSurge()) {

            return RECUPERABLE_SUR;
        }

        if (requerimiento != null
                && requerimiento.isRecupero()) {

            return RECUPERABLE_INTEGRACION;
        }

        return NO_RECUPERABLE;
    }

    /**
     * Construye una prestación CARGADA, no AUTORIZADA.
     *
     * Los valores del área médica se precargan como propuesta económica para
     * que el usuario pueda revisarlos; estadoRechazoAprobado permanece en 0.
     */
    private static PrestacionesReclamo crearPrestacion(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle,
            int idRegistro) throws Exception {

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
         * Se usa el total recalculado para que los campos presentados, el
         * total autorizado y la distribución de cargos partan de la misma
         * base monetaria.
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

        /*
         * El artículo de Compras no es una prestación ni un medicamento del
         * nomenclador de Autorizaciones.
         */
        prestacion.setId_prestacion(
                0
        );

        prestacion.setId_medicamento(
                0
        );

        prestacion.setId_prestacionrecord(
                0
        );

        prestacion.setTipoPrestacion(
                1
        );

        prestacion.setCodigoPrestacion(
                detalle.getIdArticulo() != null
                        ? "ART-" + detalle.getIdArticulo()
                        : "COMPRA"
        );

        prestacion.setNombreprestacion(
                detalle.getArticuloVisible()
        );

        prestacion.setDescripcion(
                detalle.getArticuloVisible()
        );

        prestacion.setNombremedicacion(
                ""
        );

        prestacion.setFrecuencia(
                "UNICA"
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
                0D
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

        /*
         * Una cotización no es una factura. Se usa OTR como tipo temporal y
         * se dejan vacíos los datos documentales que el usuario debe
         * confirmar.
         */
        prestacion.setComprobanteTipo(
                "OTR"
        );

        prestacion.setComprobanteNro(
                null
        );

        prestacion.setComprobanteFecha(
                null
        );

        prestacion.setComprobanteLetra(
                null
        );

        prestacion.setComprobanteSucursal(
                null
        );

        prestacion.setComprobanteCUITSucursal(
                null
        );

        prestacion.setComprobanteCUIT(
                normalizarCuit(
                        detalle.getPrestadorCuit()
                )
        );

        prestacion.setComprobanteRazonSocial(
                detalle.getPrestadorRazonSocial()
        );

        /* Datos presentados provenientes de la adjudicación. */
        prestacion.setComprobanteCantidad(
                Double.valueOf(
                        cantidad.doubleValue()
                )
        );

        prestacion.setComprobanteImporte(
                Double.valueOf(
                        importeUnitario.doubleValue()
                )
        );

        prestacion.setComprobanteTotal(
                Double.valueOf(
                        total.doubleValue()
                )
        );

        /* Debe ser confirmada por el usuario. */
        prestacion.setFechaPrestacion(
                null
        );

        prestacion.setEstado(
                PrestacionesReclamo
                        .ESTADOS
                        .NUEVO
        );

        /* CARGADO: no implica autorización médica. */
        prestacion.setEstadoRechazoAprobado(
                ESTADO_PRESTACION_CARGADA
        );

        prestacion.setObservaciones(
                construirObservacion(
                        requerimiento,
                        detalle
                )
        );

        return prestacion;
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

    private static void validarDetalleCotizado(
            RequerimientoCompraDetalle detalle)
            throws Exception {

        if (detalle == null
                || !detalle.estaCompletoParaCotizacion()
                || detalle.getCantidad() == null
                || detalle.getPrecioUnitarioEstimado() == null
                || detalle.getPrecioTotalEstimado() == null) {

            throw new Exception(
                    "El requerimiento figura COTIZADO, "
                            + "pero contiene un ítem sin cantidad, "
                            + "precio o prestador adjudicado."
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

        if (session.getAttribute(
                WebKeysAutorizaciones
                        .RECLAMO_PRESTACION_EN_EDICION
        ) != null) {

            throw new Exception(
                    "Ya existe un Reclamo Prestacional en edición "
                            + "en esta sesión."
            );
        }
    }

    private static String construirObservacion(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle) {

        StringBuilder observacion =
                new StringBuilder();

        observacion.append(
                "Borrador desde Requerimiento de Compra #"
        );

        observacion.append(
                requerimiento
                        .getIdRequerimientoCompra()
        );

        observacion.append(
                ". Confirmar nomenclador, fecha, comprobante y "
                        + "reconocido SSS."
        );

        agregarObservacion(
                observacion,
                detalle.getObservaciones()
        );

        agregarObservacion(
                observacion,
                requerimiento.getObservaciones()
        );

        String value =
                observacion.toString();

        return value.length() <= MAX_OBSERVACION
                ? value
                : value.substring(
                0,
                MAX_OBSERVACION
        );
    }

    private static void agregarObservacion(
            StringBuilder destino,
            String value) {

        String normalizado =
                WebKeysCompras.trimToNull(
                        value
                );

        if (normalizado != null) {
            destino.append(
                    ' '
            );

            destino.append(
                    normalizado
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

    private static String normalizarTexto(
            String value) {

        if (value == null) {
            return "";
        }

        return Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                ).replaceAll(
                        "\\p{InCombiningDiacriticalMarks}+",
                        ""
                ).trim()
                .toUpperCase()
                .replaceAll(
                        "\\s+",
                        " "
                );
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
