package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.global.beans.Empresa;

import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Fachada estática legacy con las reglas de consulta y normalización.
 */
public class BusquedaRequerimientoCompraServiceUtil {

    @Deprecated
    public static final int REQUERIMIENTOS_POR_PAGINA = 50;


    private static BusquedaRequerimientoCompraServiceImpl instance = null;

    public static BusquedaRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new BusquedaRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static List<RequerimientoCompra> buscarRequerimientosListado(
            RequerimientoCompraFiltro filtro,
            boolean incluirReclamoRp) throws Exception {

        RequerimientoCompraFiltro filtroEfectivo =
                filtro != null
                        ? filtro
                        : new RequerimientoCompraFiltro();

        validarFiltroBusqueda(filtroEfectivo);

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
                            ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getEstadoReclamoRP()
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

    public static List<RequerimientoCompra> buscarRequerimientosListado(
            RequerimientoCompraFiltro filtro,
            boolean incluirReclamoRp,
            int pagina) throws Exception {

        RequerimientoCompraFiltro filtroEfectivo = filtro != null
                ? filtro : new RequerimientoCompraFiltro();
        validarFiltroBusqueda(filtroEfectivo);

        if (!incluirReclamoRp && !filtroEfectivo.tieneFiltros()) {
            return getInstance().buscarRequerimientosInicialesPaginados(Math.max(1, pagina));
        }

        return getInstance().buscarRequerimientosPaginados(
                filtroEfectivo, incluirReclamoRp, Math.max(1, pagina));
    }

    public static RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().getRequerimientoCompra(idRequerimientoCompra);
    }

    public static List<Map<String, Object>> getFilasPedidoPresupuestoPdf(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().getFilasPedidoPresupuestoPdf(idRequerimientoCompra);
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
            String cuilTitular, int inte, int idSector, int idRequerimientoExcluir) throws Exception {
        return ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper
                .buscarItemsHistoricosAfiliado(cuilTitular, inte, idSector, idRequerimientoExcluir);
    }

    public static List<RequerimientoCompraDetalle> consultarItemsHistoricosAfiliado(
            String cuilTitular, int inte, int idSector, int idRequerimientoExcluir, int limite)
            throws Exception {
        return getInstance().buscarItemsHistoricosAfiliado(
                cuilTitular, inte, idSector, idRequerimientoExcluir, limite);
    }

    public static Map<Integer, String> obtenerDrogasNomencladorFarmacia(List<Nomenclador> nomencladores) throws Exception {
        return ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper
                .obtenerDrogasNomencladorFarmacia(nomencladores);
    }

    public static Map<Integer, String> obtenerDrogasDetalleFarmacia(List<RequerimientoCompraDetalle> detalles) throws Exception {
        return ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper
                .obtenerDrogasDetalleFarmacia(detalles);
    }

    public static List<RequerimientoCompraEstado> listarEstados()
            throws Exception {
        return WebKeysCompras.listarEstados();
    }

    public static java.util.List<java.util.Map<String,Object>> listarCatalogoSectores(java.sql.Connection con)throws Exception {
        return getInstance().listarCatalogoSectores(con);
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

    public static boolean existeRequerimientoDuplicado(String cuil,int integrante,int prestacion,
            java.util.Date fecha,int excluir)throws Exception {
        String normalizado=ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper.validarConsultaDuplicado(cuil,integrante,prestacion,fecha);
        return getInstance().existeRequerimientoDuplicado(normalizado,integrante,prestacion,fecha,excluir);
    }

    public static java.util.List<java.util.Map<String,Object>> listarDatosDuplicado(java.sql.Connection con,
            String cuil,int integrante,int prestacion)throws Exception {
        return getInstance().listarDatosDuplicado(con,cuil,integrante,prestacion);
    }

    public static List<PrestadorCotizacion> listarPrestadoresEnviados(java.sql.Connection con,
            int idRequerimientoCompra, int limite) throws Exception {
        return getInstance().listarPrestadoresEnviados(con, idRequerimientoCompra, limite);
    }

    public static List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        List<PrestadorCotizacion> prestadores =
                getInstance().listarPrestadoresEnviados(
                        idRequerimientoCompra,
                        ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getMaxPrestadoresEnviados()
                                + 1
                );

        if (prestadores.size()
                > ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getMaxPrestadoresEnviados()) {

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

    public static List<Empresa> buscarEmpresasCotizacion(
            String cuit,
            String descripcion,
            String sucursal,
            int limite) throws Exception {

        return getInstance().buscarEmpresasCotizacion(
                cuit,
                descripcion,
                sucursal,
                limite
        );
    }

    public static List<Empresa> buscarEmpresasCotizacionRapida(
            String cuit,
            String descripcion,
            String sucursal,
            int limite) throws Exception {

        return getInstance().buscarEmpresasCotizacionRapida(
                cuit,
                descripcion,
                sucursal,
                limite
        );
    }

    public static boolean esRequerimientoHabilitadoBusquedaEmpresaCotizacion(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance()
                .esRequerimientoHabilitadoBusquedaEmpresaCotizacion(
                        idRequerimientoCompra
                );
    }

    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().listarPresupuestos(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraPresupuesto> listarCotizacionesEmpresa(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        return getInstance().listarCotizacionesEmpresa(
                idRequerimientoCompra
        );
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

    public static RequerimientoCompraPresupuesto getCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoPresupuesto <= 0) {
            throw new Exception(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        validarIdRequerimiento(idRequerimientoCompra);

        return getInstance().getCotizacionEmpresa(
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

    public static Map<Integer, List<PrestadorCotizacion>> listarPrestadoresAdjudicadosBatch(
            List<Integer> ids) throws Exception {
        if (ids == null) {
            throw new IllegalArgumentException("Debe informar los requerimientos a exportar.");
        }
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == null) {
                throw new IllegalArgumentException("El requerimiento no es valido.");
            }
            validarIdRequerimiento(ids.get(i).intValue());
        }
        return getInstance().listarPrestadoresAdjudicadosBatch(ids);
    }

    private static void validarFiltroBusqueda(
            RequerimientoCompraFiltro filtro) throws Exception {

        if (filtro == null) {
            return;
        }

        if (filtro.getFechaAltaDesde() != null
                && filtro.getFechaAltaHasta() != null
                && filtro.getFechaAltaDesde().after(
                        filtro.getFechaAltaHasta()
                )) {

            throw new Exception(
                    "La fecha de alta desde no puede ser posterior "
                            + "a la fecha de alta hasta."
            );
        }
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

    /** La precedencia y la validación del contexto se resuelven una vez en SQL. */
    public static List<SectorNomencladorCompra> getConfiguracionNomenclador(
            int idSector, Integer idTipoPrestacion) throws Exception {
        return getInstance().getConfiguracionNomenclador(idSector, idTipoPrestacion);
    }

    public static List<Integer> listarSectoresNomenclador() throws Exception {
        return getInstance().listarSectoresNomenclador();
    }

    public static List<Nomenclador> buscarNomencladores(
            int idSector, Integer idTipoPrestacion,
            String descripcion, String codigo,
            List<Integer> troqueles) throws Exception {
        return getInstance().buscarNomencladores(
                idSector, idTipoPrestacion, descripcion, codigo, troqueles);
    }

    /** AND dentro de una asociación; OR entre las asociaciones efectivas. */
    public static boolean esNomencladorConfigurado(
            List<SectorNomencladorCompra> asociaciones,
            Nomenclador nomenclador) throws Exception {
        return ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper
                .esNomencladorConfigurado(asociaciones, nomenclador);
    }
    public static String getConfiguracionCompra(String clave) throws Exception {
        return getInstance().getConfiguracionCompra(clave);
    }

    public static String getConfiguracionCompra(String clave, java.sql.Connection con)
            throws Exception {
        return getInstance().getConfiguracionCompra(clave, con);
    }

    public static RequerimientoCompra getRequerimientoCompra(java.sql.Connection con,
            int idRequerimiento) throws Exception {
        return getInstance().getRequerimientoCompra(con, idRequerimiento);
    }

    public static RequerimientoCompra getCabeceraRequerimiento(java.sql.Connection con,
            int idRequerimiento) throws Exception {
        return getInstance().getCabeceraRequerimiento(con, idRequerimiento);
    }

    public static List<RequerimientoCompraDetalle> getDetalles(java.sql.Connection con,
            int idRequerimiento) throws Exception {
        return getInstance().getDetalles(con, idRequerimiento);
    }

    public static List<RequerimientoCompraSector> listarSectores(java.sql.Connection con)
            throws Exception {
        return getInstance().listarSectores(con);
    }

    public static List<TipoPrestacionCompra> listarTiposPrestacion(java.sql.Connection con)
            throws Exception {
        return getInstance().listarTiposPrestacion(con);
    }

    public static List<SectorNomencladorCompra> listarAsociacionesNomenclador(
            java.sql.Connection con, int idSector) throws Exception {
        return getInstance().listarAsociacionesNomenclador(con, idSector);
    }

    public static List<SectorNomencladorCompra> listarAsociacionesNomenclador(int idSector)
            throws Exception {
        return getInstance().listarAsociacionesNomenclador(idSector);
    }

    public static Nomenclador obtenerNomencladorCanonico(java.sql.Connection con,
            int idPrestacion) throws Exception {
        return getInstance().obtenerNomencladorCanonico(con, idPrestacion);
    }

    public static Nomenclador obtenerNomencladorCanonico(int idPrestacion) throws Exception {
        return getInstance().obtenerNomencladorCanonico(idPrestacion);
    }

    public static List<Nomenclador> buscarNomencladoresCandidatos(Integer idTipo,
            String descripcion, String codigo) throws Exception {
        return getInstance().buscarNomencladoresCandidatos(idTipo, descripcion, codigo);
    }
    public static boolean existeRequerimientoDuplicado(java.sql.Connection con, String cuil,
            int inte, int idPrestacion, java.util.Date fecha, int idRequerimientoExcluir)
            throws Exception {
        return getInstance().existeRequerimientoDuplicado(
                con, cuil, inte, idPrestacion, fecha, idRequerimientoExcluir);
    }

    public static List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(
            java.sql.Connection con, int idRequerimiento) throws Exception {
        return getInstance().listarOrdenesMedicas(con, idRequerimiento);
    }

    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(
            java.sql.Connection con, int idRequerimiento) throws Exception {
        return getInstance().listarPresupuestos(con, idRequerimiento);
    }

    public static Map<Integer, String> listarDrogasMedicamentosBatch(List<Integer> troqueles)
            throws Exception {
        return getInstance().listarDrogasMedicamentosBatch(troqueles);
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }

    public static RequerimientoCompraPresupuesto bloquearDocumento(java.sql.Connection con,
            int idRequerimiento, int idDocumento) throws Exception {
        return getInstance().bloquearDocumento(con, idRequerimiento, idDocumento);
    }

    public static List<RequerimientoCompraPresupuesto> listarDocumentos(java.sql.Connection con,
            int idRequerimiento, int tipoDocumento) throws Exception {
        return getInstance().listarDocumentos(con, idRequerimiento, tipoDocumento);
    }

    public static List<Empresa> buscarEmpresasCotizacion(java.sql.Connection con,
            String cuit, String descripcion, String sucursal, int limite) throws Exception {
        return getInstance().buscarEmpresasCotizacion(con, cuit, descripcion, sucursal, limite);
    }
}
