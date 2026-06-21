package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.GuardadoCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditarRequerimientoCompraServiceImplTest {

    public static void main(String[] args) throws Exception {
        assertConjuntoCompletoAceptado();
        assertDetalleAjenoRechazado();
        assertDetalleRepetidoRechazado();
        assertDetalleOmitidoRechazado();
        assertPrecioNegativoRechazado();
        assertCantidadYTotalDelRequestIgnorados();
        assertPrestadorEnviadoAceptado();
        assertPrestadorAjenoONoEnviadoRechazado();
        assertResultadoEstadoFinal();
    }

    private static void assertConjuntoCompletoAceptado()
            throws Exception {

        ServicioPrueba service =
                new ServicioPrueba();

        service.validarDetalles(
                cantidades(10, 2, 11, 3),
                detalles(
                        detalle(10, null, null),
                        detalle(11, new BigDecimal("5.25"), Integer.valueOf(20))
                )
        );
    }

    private static void assertDetalleAjenoRechazado()
            throws Exception {

        final ServicioPrueba service =
                new ServicioPrueba();

        assertException(
                "detalle ajeno",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarDetalles(
                                cantidades(10, 2, 11, 3),
                                detalles(
                                        detalle(10, null, null),
                                        detalle(99, null, null)
                                )
                        );
                    }
                }
        );
    }

    private static void assertDetalleRepetidoRechazado()
            throws Exception {

        final ServicioPrueba service =
                new ServicioPrueba();

        assertException(
                "detalle repetido",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarDetalles(
                                cantidades(10, 2, 11, 3),
                                detalles(
                                        detalle(10, null, null),
                                        detalle(10, null, null),
                                        detalle(11, null, null)
                                )
                        );
                    }
                }
        );
    }

    private static void assertDetalleOmitidoRechazado()
            throws Exception {

        final ServicioPrueba service =
                new ServicioPrueba();

        assertException(
                "detalle omitido",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarDetalles(
                                cantidades(10, 2, 11, 3),
                                detalles(
                                        detalle(10, null, null)
                                )
                        );
                    }
                }
        );
    }

    private static void assertPrecioNegativoRechazado()
            throws Exception {

        final ServicioPrueba service =
                new ServicioPrueba();

        assertException(
                "precio negativo",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarDetalles(
                                cantidades(10, 2),
                                detalles(
                                        detalle(
                                                10,
                                                new BigDecimal("-0.01"),
                                                null
                                        )
                                )
                        );
                    }
                }
        );
    }

    private static void assertCantidadYTotalDelRequestIgnorados() {
        ServicioPrueba service =
                new ServicioPrueba();

        RequerimientoCompraDetalle detalle =
                detalle(
                        10,
                        new BigDecimal("10.00"),
                        Integer.valueOf(20)
                );

        detalle.setCantidad(Integer.valueOf(999));
        detalle.setPrecioTotalEstimado(
                new BigDecimal("0.01")
        );

        BigDecimal total =
                service.calcularTotal(
                        Integer.valueOf(2),
                        detalle
                );

        assertBigDecimal(
                "total calculado con cantidad persistida",
                new BigDecimal("20.00"),
                total
        );
    }

    private static void assertPrestadorEnviadoAceptado()
            throws Exception {

        ServicioPrueba service =
                new ServicioPrueba();

        service.prestadoresEnviados.add(
                Integer.valueOf(20)
        );

        service.validarPrestador(
                1,
                Integer.valueOf(20)
        );
    }

    private static void assertPrestadorAjenoONoEnviadoRechazado()
            throws Exception {

        final ServicioPrueba service =
                new ServicioPrueba();

        service.prestadoresEnviados.add(
                Integer.valueOf(20)
        );

        assertException(
                "prestador ajeno",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarPrestador(
                                1,
                                Integer.valueOf(30)
                        );
                    }
                }
        );

        assertException(
                "prestador no ENVIADO",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarPrestador(
                                1,
                                Integer.valueOf(40)
                        );
                    }
                }
        );
    }

    private static void assertResultadoEstadoFinal() {
        GuardadoCotizacionResultado incompleta =
                new GuardadoCotizacionResultado(
                        false,
                        WebKeysCompras.ESTADO_A_COTIZAR
                );

        assertBoolean(
                "cotizacion incompleta",
                false,
                incompleta.isCotizacionCompleta()
        );
        assertInt(
                "estado final incompleto",
                WebKeysCompras.ESTADO_A_COTIZAR,
                incompleta.getEstadoFinal()
        );

        GuardadoCotizacionResultado completa =
                new GuardadoCotizacionResultado(
                        true,
                        WebKeysCompras.ESTADO_COTIZADO
                );

        assertBoolean(
                "cotizacion completa",
                true,
                completa.isCotizacionCompleta()
        );
        assertInt(
                "estado final completo",
                WebKeysCompras.ESTADO_COTIZADO,
                completa.getEstadoFinal()
        );
    }

    private static RequerimientoCompraDetalle detalle(
            int id,
            BigDecimal precioUnitario,
            Integer idPrestador) {

        RequerimientoCompraDetalle detalle =
                new RequerimientoCompraDetalle(
                        Integer.valueOf(id)
                );

        detalle.setPrecioUnitarioEstimado(
                precioUnitario
        );
        detalle.setIdPrestador(
                idPrestador
        );

        return detalle;
    }

    private static List<RequerimientoCompraDetalle> detalles(
            RequerimientoCompraDetalle... values) {

        List<RequerimientoCompraDetalle> detalles =
                new ArrayList<RequerimientoCompraDetalle>();

        for (int i = 0; values != null && i < values.length; i++) {
            detalles.add(values[i]);
        }

        return detalles;
    }

    private static Map<Integer, Integer> cantidades(
            int idUno,
            int cantidadUno) {

        Map<Integer, Integer> cantidades =
                new HashMap<Integer, Integer>();

        cantidades.put(
                Integer.valueOf(idUno),
                Integer.valueOf(cantidadUno)
        );

        return cantidades;
    }

    private static Map<Integer, Integer> cantidades(
            int idUno,
            int cantidadUno,
            int idDos,
            int cantidadDos) {

        Map<Integer, Integer> cantidades =
                cantidades(
                        idUno,
                        cantidadUno
                );

        cantidades.put(
                Integer.valueOf(idDos),
                Integer.valueOf(cantidadDos)
        );

        return cantidades;
    }

    private static void assertException(
            String descripcion,
            Ejecucion ejecucion)
            throws Exception {

        try {
            ejecucion.ejecutar();
        } catch (Exception e) {
            return;
        }

        throw new AssertionError(
                descripcion
                        + ": se esperaba rechazo."
        );
    }

    private static void assertBigDecimal(
            String descripcion,
            BigDecimal esperado,
            BigDecimal actual) {

        if (actual == null
                || esperado.compareTo(actual) != 0) {

            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static void assertBoolean(
            String descripcion,
            boolean esperado,
            boolean actual) {

        if (esperado != actual) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static void assertInt(
            String descripcion,
            int esperado,
            int actual) {

        if (esperado != actual) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private interface Ejecucion {

        void ejecutar()
                throws Exception;
    }

    private static class ServicioPrueba
            extends EditarRequerimientoCompraServiceImpl {

        private Set<Integer> prestadoresEnviados =
                new HashSet<Integer>();

        public void validarDetalles(
                Map<Integer, Integer> cantidades,
                List<RequerimientoCompraDetalle> detalles)
                throws Exception {

            validarDetallesCotizacionRecibidos(
                    cantidades,
                    detalles
            );
        }

        public BigDecimal calcularTotal(
                Integer cantidadPersistida,
                RequerimientoCompraDetalle detalle) {

            return calcularPrecioTotalCotizacion(
                    cantidadPersistida,
                    detalle
            );
        }

        public void validarPrestador(
                int idRequerimientoCompra,
                Integer idPrestador)
                throws Exception {

            validarPrestadorCotizacion(
                    null,
                    idRequerimientoCompra,
                    idPrestador
            );
        }

        protected boolean existePrestadorEnviado(
                Connection con,
                int idRequerimientoCompra,
                int idPrestador) {

            return prestadoresEnviados.contains(
                    Integer.valueOf(idPrestador)
            );
        }
    }

    private EditarRequerimientoCompraServiceImplTest() {
    }
}
