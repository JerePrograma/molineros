package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.*;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
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
public final class ReclamoPrestacionalCompraPrecargaServiceUtil {

    private static final int RECUPERABLE_SUR = 1;
    private static final int NO_RECUPERABLE = 2;
    private static final int RECUPERABLE_INTEGRACION = 3;
    private static final String
            DESCRIPCION_INTEGRACION_CABECERA_NO_RECUPERABLE =
            "NO RECUPERABLE";
    private static final String COMPROBANTE_TIPO_INICIAL = "OTR";
    private static final String
            COMPROBANTE_CUIT_SUCURSAL_INICIAL =
            "000";

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

        reclamo.setRecuperable(
                false
        );

        /*
         * El flag recuperable de la cabecera es independiente del combo
         * Integracion. El combo utiliza codigoIntegracion.
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

        Integer codigoEncontrado =
                null;

        if (integraciones != null) {
            for (ReclamosPrestacionalesIntegracion integracion
                    : integraciones) {

                if (integracion == null) {
                    continue;
                }

                String descripcion =
                        integracion.getDescripcion();

                String descripcionLarga =
                        integracion.getDescripcionLarga();

                boolean coincideDescripcion =
                        descripcion != null
                                && DESCRIPCION_INTEGRACION_CABECERA_NO_RECUPERABLE
                                .equalsIgnoreCase(
                                        descripcion.trim()
                                );

                boolean coincideDescripcionLarga =
                        descripcionLarga != null
                                && DESCRIPCION_INTEGRACION_CABECERA_NO_RECUPERABLE
                                .equalsIgnoreCase(
                                        descripcionLarga.trim()
                                );

                if (!coincideDescripcion
                        && !coincideDescripcionLarga) {

                    continue;
                }

                if (integracion.getId() <= 0) {
                    throw new Exception(
                            "La integracion NO RECUPERABLE "
                                    + "no tiene un codigo valido."
                    );
                }

                if (codigoEncontrado != null
                        && codigoEncontrado.intValue()
                        != integracion.getId()) {

                    throw new Exception(
                            "Existe mas de una integracion "
                                    + "NO RECUPERABLE."
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
                    "No se encontro la integracion "
                            + "NO RECUPERABLE."
            );
        }

        return codigoEncontrado.intValue();
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
     * Los valores del área médica y del comprobante se precargan desde
     * la cotización del requerimiento para que el usuario pueda revisarlos.
     * estadoRechazoAprobado permanece en 0.
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
                detalle
        );

        prestacion.setFrecuencia(
                "UNICA"
        );

        /*
         * Precarga exclusivamente los datos de comprobante que tienen
         * correspondencia directa y verificable en Compras.
         */
        precargarDatosComprobante(
                prestacion,
                detalle,
                cantidad,
                importeUnitario,
                total
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
     * No se inventan letra, punto de venta, numero ni fechas.
     */
    private static void precargarDatosComprobante(
            PrestacionesReclamo prestacion,
            RequerimientoCompraDetalle detalle,
            BigDecimal cantidad,
            BigDecimal importeUnitario,
            BigDecimal total) {

        if (prestacion == null
                || detalle == null) {

            return;
        }

        prestacion.setComprobanteTipo(
                COMPROBANTE_TIPO_INICIAL
        );

        prestacion.setComprobanteCUITSucursal(
                COMPROBANTE_CUIT_SUCURSAL_INICIAL
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

    private static void aplicarReferenciaTecnica(
            PrestacionesReclamo prestacion,
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle)
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
                            + "una configuracion tecnica valida."
            );
        }

        prestacion.setId_prestacionrecord(
                0
        );

        prestacion.setTipoPrestacion(
                1
        );

        /*
         * Los nuevos detalles de Compras utilizan NOMENCLADOR.
         *
         * La referencia se recupera nuevamente desde el nomenclador
         * canonico porque el tipo, la marca ReinLiq, el codigo y el
         * estado activo no deben confiarse a los datos de sesion o HTTP.
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
                        "La prestacion seleccionada ya no existe "
                                + "o no se encuentra activa."
                );
            }

            if (nomenclador.getId_tipo_nomenclador()
                    != idTipoNomencladorDetalle) {

                throw new IllegalArgumentException(
                        "El tipo de nomenclador persistido no "
                                + "corresponde a la prestacion seleccionada."
                );
            }

            if (!WebKeysCompras
                    .esNomencladorValidoParaSectorCompras(
                            sector,
                            nomenclador
                                    .getId_tipo_nomenclador(),
                            nomenclador
                                    .getMarcaReintegroLiquidacion(),
                            nomenclador.getCodigo()
                    )) {

                if ("FARMACIA".equals(sector)) {
                    throw new IllegalArgumentException(
                            "Para Farmacia debe utilizarse una "
                                    + "prestacion del nomenclador tipo 9."
                    );
                }

                if ("DISCAPACIDAD".equals(sector)) {
                    throw new IllegalArgumentException(
                            "Para Discapacidad debe utilizarse una "
                                    + "prestacion con marca ReinLiq 6 "
                                    + "o el codigo 431003."
                    );
                }

                if ("ODONTOLOGIA".equals(sector)) {
                    throw new IllegalArgumentException(
                            "Para Odontologia debe utilizarse una "
                                    + "prestacion del nomenclador tipo 1."
                    );
                }

                if ("PRESTACIONES MEDICAS".equals(sector)) {
                    throw new IllegalArgumentException(
                            "Prestaciones Medicas no admite "
                                    + "prestaciones del nomenclador tipo 1."
                    );
                }

                throw new IllegalArgumentException(
                        "La prestacion seleccionada no corresponde "
                                + "al sector del requerimiento."
                );
            }

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
                        "La prestacion seleccionada no tiene "
                                + "un codigo valido."
                );
            }

            if (WebKeysCompras.isEmpty(
                    descripcionCanonica
            )) {
                throw new IllegalArgumentException(
                        "La prestacion seleccionada no tiene "
                                + "una descripcion valida."
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
         * Compatibilidad historica:
         *
         * Las filas que ya estaban persistidas como MEDICAMENTO
         * continúan siendo utilizables exclusivamente en Farmacia.
         * No se habilitan nuevas altas de este tipo.
         */
        if (detalle.tieneMedicamento()) {
            if (!"FARMACIA".equals(
                    sector
            )) {
                throw new IllegalArgumentException(
                        "Un medicamento historico solo puede "
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
                        "El medicamento historico no conserva "
                                + "una referencia tecnica valida."
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
                        "El detalle de observacion de Compras no contiene "
                                + "una descripcion para precargar."
                );
            }

            if (descripcionPendiente.length() > MAX_OBSERVACION) {
                descripcionPendiente =
                        descripcionPendiente.substring(
                                0,
                                MAX_OBSERVACION
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
                        + "una referencia tecnica valida."
        );
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
