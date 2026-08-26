package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fachada estática legacy con las reglas de consulta y normalización.
 */
public class BusquedaRequerimientoCompraServiceUtil {

    private static final int MAX_ITEMS_HISTORICOS_AFILIADO = 20;

    private static BusquedaRequerimientoCompraServiceImpl instance = null;

    public static BusquedaRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new BusquedaRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        return getInstance().buscarRequerimientos(
                filtro != null
                        ? filtro
                        : new RequerimientoCompraFiltro()
        );
    }

    public static List<RequerimientoCompra> buscarRequerimientosListado(
            RequerimientoCompraFiltro filtro,
            boolean incluirReclamoRp) throws Exception {

        RequerimientoCompraFiltro filtroEfectivo =
                filtro != null
                        ? filtro
                        : new RequerimientoCompraFiltro();

        List<RequerimientoCompra> requerimientos =
                getInstance().buscarRequerimientos(
                        filtroEfectivo
                );

        if (!incluirReclamoRp) {
            return requerimientos;
        }

        Integer estadoOriginal = filtroEfectivo.getIdEstado();

        try {
            filtroEfectivo.setIdEstado(
                    Integer.valueOf(
                            WebKeysCompras.ESTADO_RECLAMO_RP
                    )
            );

            List<RequerimientoCompra> requerimientosRp =
                    getInstance().buscarRequerimientos(
                            filtroEfectivo
                    );

            if (!requerimientosRp.isEmpty()) {
                requerimientos.addAll(requerimientosRp);
            }
        } finally {
            filtroEfectivo.setIdEstado(estadoOriginal);
        }

        Collections.sort(
                requerimientos,
                new Comparator<RequerimientoCompra>() {
                    public int compare(
                            RequerimientoCompra a,
                            RequerimientoCompra b) {

                        int idA = a != null
                                ? a.getIdRequerimientoCompra()
                                : 0;
                        int idB = b != null
                                ? b.getIdRequerimientoCompra()
                                : 0;

                        if (idA == idB) {
                            return 0;
                        }

                        return idA > idB
                                ? -1
                                : 1;
                    }
                }
        );

        return requerimientos;
    }

    public static RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().getRequerimientoCompra(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getItems(
            int idRequerimientoCompra) throws Exception {

        return getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> buscarItemsHistoricosAfiliado(
            String cuilTitular,
            int inte,
            int idSector,
            int idRequerimientoExcluir) throws Exception {

        String cuil = WebKeysCompras.trimToNull(cuilTitular);

        if (cuil == null || !cuil.matches("^[0-9]{11}$")) {
            throw new Exception(
                    "Debe informar un CUIL titular válido."
            );
        }

        if (inte < 0) {
            throw new Exception(
                    "Debe informar un integrante válido."
            );
        }

        if (idSector <= 0) {
            throw new Exception(
                    "Debe informar el sector del requerimiento."
            );
        }

        List<RequerimientoCompraDetalle> items =
                getInstance().buscarItemsHistoricosAfiliado(
                        cuil,
                        inte,
                        idSector,
                        idRequerimientoExcluir > 0
                                ? idRequerimientoExcluir
                                : 0,
                        MAX_ITEMS_HISTORICOS_AFILIADO
                );

        for (int i = 0; i < items.size(); i++) {
            RequerimientoCompraDetalle detalle = items.get(i);

            if (detalle == null) {
                continue;
            }

            detalle.setTipoItem(
                    RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR
            );
            detalle.setCodigoItem(
                    detalle.getCodigoNomenclador()
            );
            detalle.setDescripcionItem(
                    detalle.getDescripcionNomenclador()
            );
            detalle.setCantidad(Integer.valueOf(1));
        }

        return items;
    }

    public static List<RequerimientoCompraEstado> listarEstados()
            throws Exception {
        return WebKeysCompras.listarEstados();
    }

    public static List<RequerimientoCompraSector> listarSectores()
            throws Exception {

        return getInstance().listarSectores();
    }

    public static List<TipoPrestacionCompra> listarTiposPrestacion()
            throws Exception {

        return getInstance().listarTiposPrestacion();
    }

    public static RequerimientoCompraEstado getEstado(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().getEstado(idRequerimientoCompra);
    }

    public static RequerimientoCompraSector getSector(int idSector)
            throws Exception {

        if (idSector <= 0) {
            throw new Exception(
                    "Debe informar el sector del requerimiento."
            );
        }

        return getInstance().getSector(idSector);
    }

    public static boolean tieneSituacionMedicaVigente(
            String cuilTitular,
            int inte) throws Exception {

        String cuil = WebKeysCompras.trimToNull(cuilTitular);

        if (cuil == null || !cuil.matches("^[0-9]{11}$")) {
            throw new Exception(
                    "Debe informar un CUIL titular válido."
            );
        }

        if (inte < 0) {
            throw new Exception(
                    "Debe informar un integrante válido."
            );
        }

        return getInstance().tieneSituacionMedicaVigente(
                cuil,
                inte
        );
    }

    public static boolean existeRequerimientoDuplicado(
            String cuilTitular,
            int inte,
            int idPrestacion,
            java.util.Date fechaOrdenMedica,
            int idRequerimientoExcluir)
            throws Exception {

        String cuil =
                WebKeysCompras.trimToNull(
                        cuilTitular
                );

        if (cuil == null
                || !cuil.matches("^[0-9]{11}$")) {

            throw new Exception(
                    "Debe informar un CUIL titular válido."
            );
        }

        if (inte < 0) {
            throw new Exception(
                    "Debe informar un integrante válido."
            );
        }

        if (idPrestacion <= 0) {
            throw new Exception(
                    "Debe informar una prestación válida."
            );
        }

        if (fechaOrdenMedica == null) {
            throw new Exception(
                    "Debe informar la fecha de la Orden médica."
            );
        }

        int idExcluir =
                idRequerimientoExcluir > 0
                        ? idRequerimientoExcluir
                        : 0;

        return getInstance()
                .existeRequerimientoDuplicado(
                        cuil,
                        inte,
                        idPrestacion,
                        fechaOrdenMedica,
                        idExcluir
                );
    }

    public static List<PrestadorCotizacion> buscarPrestadoresEnviados(
            int idRequerimientoCompra,
            String texto,
            int limite) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        int limiteNormalizado =
                limite > 0 && limite <= 50
                        ? limite
                        : 20;

        return getInstance().buscarPrestadoresEnviados(
                idRequerimientoCompra,
                WebKeysCompras.trimToNull(texto),
                limiteNormalizado
        );
    }

    public static List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        List<PrestadorCotizacion> prestadores =
                getInstance().listarPrestadoresEnviados(
                        idRequerimientoCompra,
                        WebKeysCompras
                                .MAX_PRESTADORES_ENVIADOS_REQUERIMIENTO
                                + 1
                );

        if (prestadores.size()
                > WebKeysCompras
                .MAX_PRESTADORES_ENVIADOS_REQUERIMIENTO) {

            throw new Exception(
                    "El requerimiento supera el máximo permitido de "
                            + "prestadores enviados para esta pantalla."
            );
        }

        return prestadores;
    }

    public static boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        return getInstance().hayPrestadoresPendientesNotificacion(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().listarPresupuestos(idRequerimientoCompra);
    }

    public static RequerimientoCompraPresupuesto getPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoPresupuesto <= 0) {
            throw new Exception(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        validarIdRequerimiento(idRequerimientoCompra);

        return getInstance().getPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().listarOrdenesMedicas(idRequerimientoCompra);
    }

    public static RequerimientoCompraPresupuesto getOrdenMedica(
            int idRequerimientoCompra) throws Exception {

        List<RequerimientoCompraPresupuesto> ordenes =
                listarOrdenesMedicas(idRequerimientoCompra);

        return ordenes.isEmpty()
                ? null
                : ordenes.get(0);
    }

    public static RequerimientoCompraPresupuesto getPresupuestoAdjudicado(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        int idPrestador =
                resolverIdPrestadorAdjudicado(
                        idRequerimientoCompra
                );

        List<RequerimientoCompraPresupuesto> presupuestos =
                getInstance().listarPresupuestosPrestador(
                        idRequerimientoCompra,
                        idPrestador
                );

        if (presupuestos.size() > 1) {
            throw new Exception(
                    "Existe más de un presupuesto activo para "
                            + "el prestador adjudicado."
            );
        }

        return presupuestos.isEmpty()
                ? null
                : presupuestos.get(0);
    }

    private static void validarIdRequerimiento(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }
    }

    public static RequerimientoCompraPedidoCotizacion
    getPedidoCotizacionAdjudicado(
            int idRequerimientoCompra)
            throws Exception {

        validarIdRequerimiento(
                idRequerimientoCompra
        );

        int idPrestador =
                resolverIdPrestadorAdjudicado(
                        idRequerimientoCompra
                );

        return getInstance()
                .getPedidoCotizacionPrestador(
                        idRequerimientoCompra,
                        idPrestador
                );
    }

    private static int resolverIdPrestadorAdjudicado(
            int idRequerimientoCompra)
            throws Exception {

        List<Integer> prestadores =
                getInstance()
                        .listarPrestadoresAdjudicados(
                                idRequerimientoCompra
                        );

        if (prestadores.isEmpty()) {
            throw new Exception(
                    "El requerimiento no tiene detalles activos "
                            + "con un prestador adjudicado."
            );
        }

        if (prestadores.size() != 1
                || prestadores.get(0) == null
                || prestadores
                .get(0)
                .intValue() <= 0) {

            throw new Exception(
                    "Todos los detalles activos deben tener "
                            + "el mismo prestador adjudicado válido."
            );
        }

        return prestadores
                .get(0)
                .intValue();
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}
