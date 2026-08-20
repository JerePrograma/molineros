package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Reglas de consulta y normalización que no pertenecen a la capa SQL.
 */
public final class BusquedaRequerimientoCompraHelper {

    private static final int MAX_ITEMS_HISTORICOS_AFILIADO = 20;

    private final BusquedaRequerimientoCompraServiceImpl service =
            new BusquedaRequerimientoCompraServiceImpl();

    public List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        return service.buscarRequerimientos(
                filtro != null
                        ? filtro
                        : new RequerimientoCompraFiltro()
        );
    }

    public List<RequerimientoCompra> buscarRequerimientosListado(
            RequerimientoCompraFiltro filtro,
            boolean incluirReclamoRp) throws Exception {

        RequerimientoCompraFiltro filtroEfectivo =
                filtro != null
                        ? filtro
                        : new RequerimientoCompraFiltro();

        List<RequerimientoCompra> requerimientos =
                service.buscarRequerimientos(
                        filtroEfectivo
                );

        if (requerimientos == null) {
            requerimientos = new ArrayList<RequerimientoCompra>();
        }

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
                    service.buscarRequerimientos(
                            filtroEfectivo
                    );

            if (requerimientosRp != null
                    && !requerimientosRp.isEmpty()) {
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

    public RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return service.getRequerimientoCompra(idRequerimientoCompra);
    }

    public List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return service.getDetalles(idRequerimientoCompra);
    }

    public List<RequerimientoCompraDetalle> buscarItemsHistoricosAfiliado(
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
                service.buscarItemsHistoricosAfiliado(
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

    public List<RequerimientoCompraEstado> listarEstados() {
        return WebKeysCompras.listarEstados();
    }

    public List<RequerimientoCompraSector> listarSectores()
            throws Exception {

        return service.listarSectores();
    }

    public RequerimientoCompraEstado getEstado(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return service.getEstado(idRequerimientoCompra);
    }

    public RequerimientoCompraSector getSector(int idSector)
            throws Exception {

        if (idSector <= 0) {
            throw new Exception(
                    "Debe informar el sector del requerimiento."
            );
        }

        return service.getSector(idSector);
    }

    public boolean tieneSituacionMedicaVigente(
            String cuilTitular,
            int inte) throws Exception {

        String cuil = WebKeysCompras.trimToNull(cuilTitular);

        if (cuil == null || !cuil.matches("^[0-9]{11}$")) {
            throw new Exception(
                    "Debe informar un CUIL titular valido."
            );
        }

        if (inte < 0) {
            throw new Exception(
                    "Debe informar un integrante válido."
            );
        }

        return service.tieneSituacionMedicaVigente(
                cuil,
                inte
        );
    }

    public boolean existeRequerimientoDuplicado(
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
                    "Debe informar un CUIL titular valido."
            );
        }

        if (inte < 0) {
            throw new Exception(
                    "Debe informar un integrante valido."
            );
        }

        if (idPrestacion <= 0) {
            throw new Exception(
                    "Debe informar una prestacion valida."
            );
        }

        if (fechaOrdenMedica == null) {
            throw new Exception(
                    "Debe informar la fecha de la Orden medica."
            );
        }

        int idExcluir =
                idRequerimientoExcluir > 0
                        ? idRequerimientoExcluir
                        : 0;

        return service
                .existeRequerimientoDuplicado(
                        cuil,
                        inte,
                        idPrestacion,
                        fechaOrdenMedica,
                        idExcluir
                );
    }

    public List<PrestadorCotizacion> buscarPrestadoresEnviados(
            int idRequerimientoCompra,
            String texto,
            int limite) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        int limiteNormalizado =
                limite > 0 && limite <= 50
                        ? limite
                        : 20;

        return service.buscarPrestadoresEnviados(
                idRequerimientoCompra,
                WebKeysCompras.trimToNull(texto),
                limiteNormalizado
        );
    }

    public List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        List<PrestadorCotizacion> prestadores =
                service.listarPrestadoresEnviados(
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

    public boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        return service.hayPrestadoresPendientesNotificacion(
                idRequerimientoCompra
        );
    }

    public List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return service.listarPresupuestos(idRequerimientoCompra);
    }

    public RequerimientoCompraPresupuesto getPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoPresupuesto <= 0) {
            throw new Exception(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        validarIdRequerimiento(idRequerimientoCompra);

        return service.getPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return service.listarOrdenesMedicas(idRequerimientoCompra);
    }

    public RequerimientoCompraPresupuesto getOrdenMedica(
            int idRequerimientoCompra) throws Exception {

        List<RequerimientoCompraPresupuesto> ordenes =
                listarOrdenesMedicas(idRequerimientoCompra);

        return ordenes.isEmpty()
                ? null
                : ordenes.get(0);
    }

    public RequerimientoCompraPresupuesto getPresupuestoAdjudicado(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        List<Integer> prestadores =
                service.listarPrestadoresAdjudicados(
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
                || prestadores.get(0).intValue() <= 0) {

            throw new Exception(
                    "Todos los detalles activos deben tener "
                            + "el mismo prestador adjudicado válido."
            );
        }

        List<RequerimientoCompraPresupuesto> presupuestos =
                service.listarPresupuestosPrestador(
                        idRequerimientoCompra,
                        prestadores.get(0).intValue()
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

    private void validarIdRequerimiento(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }
    }

    public RequerimientoCompraPedidoCotizacion
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

        return service
                .getPedidoCotizacionPrestador(
                        idRequerimientoCompra,
                        idPrestador
                );
    }

    private int resolverIdPrestadorAdjudicado(
            int idRequerimientoCompra)
            throws Exception {

        List<Integer> prestadores =
                service
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
}
