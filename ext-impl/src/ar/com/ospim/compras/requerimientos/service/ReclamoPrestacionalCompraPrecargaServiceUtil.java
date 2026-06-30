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
 * Precarga un Reclamo Prestacional desde un requerimiento de compra
 * COTIZADO.
 *
 * Los identificadores de artículos de Compras no se reutilizan como
 * IDs del nomenclador médico. Se transportan como código visible
 * ART-{id} y se deja el ID de prestación en cero para que el usuario
 * confirme el nomenclador.
 */
public final class ReclamoPrestacionalCompraPrecargaServiceUtil {

    private static final int RECUPERABLE_SUR = 1;
    private static final int NO_RECUPERABLE = 2;
    private static final int RECUPERABLE_INTEGRACION = 3;

    private static final int MAX_OBSERVACION = 250;

    private ReclamoPrestacionalCompraPrecargaServiceUtil() {
    }

    /**
     * Precarga los objetos utilizados por el editor legacy y devuelve
     * un descriptor con las referencias exactas escritas en sesión.
     *
     * Ese descriptor permite compensar un handoff fallido sin eliminar
     * objetos que otra petición hubiera escrito posteriormente.
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

        if (WebKeysCompras.isEmpty(nonceRequest)) {
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
            /*
             * El armado del reclamo ocurre fuera del bloqueo para no
             * retener innecesariamente la sesión durante consultas y
             * conversiones.
             *
             * Antes de escribir se comprueba nuevamente que otra petición
             * no haya sustituido el contexto ni iniciado otra edición.
             */
            synchronized (session) {
                obtenerContextoValido(
                        session,
                        nonceRequest,
                        usuarioActual
                );

                validarSinReclamoEnEdicion(
                        session
                );

                /*
                 * Se guardan los valores anteriores para poder restaurarlos
                 * si la navegación falla después de la precarga.
                 *
                 * RECLAMO_PRESTACION_EN_EDICION debe ser null por la
                 * validación anterior, pero se conserva igualmente el valor
                 * para que el algoritmo de compensación sea completo.
                 */
                precarga =
                        new Precarga(
                                nonceRequest,
                                reclamo,
                                prestaciones,
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
            /*
             * También cubre una eventual escritura parcial de los atributos
             * de sesión dentro del bloque anterior.
             */
            limpiarHandoffFallido(
                    session,
                    nonceRequest,
                    precarga
            );

            throw e;
        }
    }

    /**
     * Compensa exclusivamente la precarga perteneciente al nonce indicado.
     *
     * Reglas:
     *
     * 1. El contexto actual debe seguir teniendo el mismo nonce.
     * 2. Cada atributo sólo se restaura si todavía contiene exactamente
     *    el mismo objeto que escribió esta Precarga.
     * 3. Si otra petición reemplazó un atributo, no se toca.
     * 4. El contexto se elimina únicamente si sigue perteneciendo al
     *    handoff fallido.
     */
    public static void limpiarHandoffFallido(
            HttpSession session,
            String nonce,
            Precarga precarga) {

        if (session == null
                || WebKeysCompras.isEmpty(nonce)) {

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
                /*
                 * Otra petición sustituyó el contexto.
                 * No se elimina ningún dato.
                 */
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

            /*
             * Se elimina sólo después de verificar que el contexto actual
             * todavía corresponde al nonce fallido.
             */
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

        /*
         * La comparación deliberadamente utiliza identidad, no equals().
         * Dos listas o reclamos con los mismos valores pueden pertenecer
         * a peticiones distintas.
         */
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

    public static ReclamoPrestacional crearReclamo(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            throw new Exception(
                    "No se pudo obtener el requerimiento de compra."
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
                        mapearSector(
                                requerimiento
                                        .getSectorDescripcion()
                        ),
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

        reclamo.setEstado(
                0
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

        BigDecimal total =
                normalizarImporte(
                        detalle
                                .getPrecioTotalEstimado()
                );

        BigDecimal cargoOspim =
                total.multiply(
                        BigDecimal.valueOf(
                                porcentaje(
                                        requerimiento
                                                .getCargoOspim()
                                )
                        )
                ).divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );

        /*
         * El remanente se asigna a la tercerizadora para evitar
         * diferencias producidas por dos redondeos independientes.
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

        prestacion.setIdRegistro(
                idRegistro
        );

        /*
         * El artículo de Compras no es un ID del nomenclador médico.
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
                Double.valueOf(0D)
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
         * Una cotización no es una factura. OTR evita valores nulos
         * incompatibles con el JSP legacy, pero el número, la fecha,
         * la letra y las sucursales deben ser confirmados.
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

        prestacion.setFechaPrestacion(
                null
        );

        prestacion.setEstado(
                PrestacionesReclamo
                        .ESTADOS
                        .NUEVO
        );

        prestacion.setEstadoRechazoAprobado(
                0
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
                            + "no coincide con el contexto "
                            + "de creación del RP."
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
                            + "nuevamente el Reclamo Prestacional."
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
                            + "en esta sesión. Finalice o descarte "
                            + "esa edición antes de iniciar otro "
                            + "desde Compras."
            );
        }
    }

    private static String construirObservacion(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle) {

        StringBuilder observacion =
                new StringBuilder();

        observacion.append(
                "Precargado desde Requerimiento de Compra #"
        );

        observacion.append(
                requerimiento
                        .getIdRequerimientoCompra()
        );

        observacion.append(
                ". Confirmar nomenclador, fecha, "
                        + "comprobante y reconocido SSS."
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
     *
     * Las referencias anteriores se conservan exclusivamente para una
     * eventual compensación. No forman parte del flujo funcional normal.
     */
    public static final class Precarga {

        private final String nonce;

        private final ReclamoPrestacional reclamoCreado;

        private final List<PrestacionesReclamo>
                prestacionesCreadas;

        private final List<RevisionesReclamo>
                revisionesCreadas;

        private final List<ContactoCRM>
                contactosCreados;

        private final Object reclamoAnterior;

        private final Object prestacionesAnteriores;

        private final Object revisionesAnteriores;

        private final Object contactosAnteriores;

        private Precarga(
                String nonce,
                ReclamoPrestacional reclamoCreado,
                List<PrestacionesReclamo> prestacionesCreadas,
                List<RevisionesReclamo> revisionesCreadas,
                List<ContactoCRM> contactosCreados,
                Object reclamoAnterior,
                Object prestacionesAnteriores,
                Object revisionesAnteriores,
                Object contactosAnteriores) {

            this.nonce = nonce;
            this.reclamoCreado = reclamoCreado;
            this.prestacionesCreadas = prestacionesCreadas;
            this.revisionesCreadas = revisionesCreadas;
            this.contactosCreados = contactosCreados;
            this.reclamoAnterior = reclamoAnterior;
            this.prestacionesAnteriores = prestacionesAnteriores;
            this.revisionesAnteriores = revisionesAnteriores;
            this.contactosAnteriores = contactosAnteriores;
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

        private Object getRevisionesAnteriores() {
            return revisionesAnteriores;
        }

        private Object getContactosAnteriores() {
            return contactosAnteriores;
        }
    }
}