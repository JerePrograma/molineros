package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.GuardadoCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.GestorOrdenMedicaDocumento;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditarRequerimientoCompraServiceImplTest {

    public static void main(String[] args) throws Exception {
        /*
         * La pertenencia/completitud de detalles, las cantidades persistidas
         * y la validación del prestador ENVIADO son responsabilidad de
         * compras.guardar_cotizacion_requerimiento. Aquí solo se prueban las
         * validaciones Java que permanecen antes de invocar la función.
         */
        assertConjuntoCompletoAceptado();
        assertDetalleRepetidoRechazado();
        assertPrecioNegativoRechazado();
        assertPrestadorUnicoAceptado();
        assertPrestadoresMixtosRechazados();
        assertResultadoEstadoFinal();
        assertGuardarRequerimientoVinculaAfiliadoIdOspim();
        assertAltaSinOrdenMedicaRechazadaPorMetodoHistorico();
        assertAltaConOrdenMedicaConfirmaTransaccion();
        assertFalloSqlPosteriorADlCompensaYRevierte();
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

    private static void assertPrestadorUnicoAceptado()
            throws Exception {

        ServicioPrueba service =
                new ServicioPrueba();

        service.validarDetalles(
                cantidades(10, 2, 11, 3),
                detalles(
                        detalle(
                                10,
                                new BigDecimal("10.00"),
                                Integer.valueOf(20)
                        ),
                        detalle(
                                11,
                                new BigDecimal("5.00"),
                                Integer.valueOf(20)
                        )
                )
        );
    }

    private static void assertPrestadoresMixtosRechazados()
            throws Exception {

        final ServicioPrueba service =
                new ServicioPrueba();

        assertException(
                "prestadores adjudicados mixtos",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        service.validarDetalles(
                                cantidades(10, 2, 11, 3),
                                detalles(
                                        detalle(
                                                10,
                                                new BigDecimal("10.00"),
                                                Integer.valueOf(20)
                                        ),
                                        detalle(
                                                11,
                                                new BigDecimal("5.00"),
                                                Integer.valueOf(30)
                                        )
                                )
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

    private static void assertGuardarRequerimientoVinculaAfiliadoIdOspim()
            throws Exception {

        ServicioGuardarPrueba service =
                new ServicioGuardarPrueba();

        RequerimientoCompra requerimiento =
                new RequerimientoCompra();

        requerimiento.setIdSector(Integer.valueOf(5));
        requerimiento.setAfiliadoCuilTitular("20111111112");
        requerimiento.setAfiliadoInt(Integer.valueOf(2));
        requerimiento.setAfiliadoIdOspim(Integer.valueOf(123456));
        requerimiento.setAfiliadoNombre("Nombre");
        requerimiento.setAfiliadoApellido("Apellido");
        requerimiento.setAfiliadoDocumentoTipo("DNI");
        requerimiento.setAfiliadoDocumentoNro("11222333");
        requerimiento.setAfiliadoDireccion("Calle 123");
        requerimiento.setAfiliadoLocalidad("Localidad");
        requerimiento.setAfiliadoProvincia("Provincia");
        requerimiento.setAfiliadoCelular("111-222");
        requerimiento.setAfiliadoTelefono("333-444");
        requerimiento.setAfiliadoEmail("afiliado@example.com");
        requerimiento.setCargoOspim(Integer.valueOf(80));
        requerimiento.setCargoTercerizadora(Integer.valueOf(20));
        requerimiento.setIdTercerizadora("CSA");
        requerimiento.setRecupero(true);
        requerimiento.setSurge(true);
        requerimiento.setObservaciones("Observaciones");

        int id =
                service.guardarCabeceraPrueba(
                        requerimiento,
                        "tester"
                );

        assertInt("id devuelto", 987, id);
        assertInt("cantidad out parameters", 1, service.outParameters.size());
        assertInt("cantidad parametros", 22, service.parametros.size());
        assertInt(
                "out parameter",
                java.sql.Types.INTEGER,
                ((Integer) service.outParameters.get(
                        Integer.valueOf(1)
                )).intValue()
        );
        assertInt("cantidad placeholders", 23, contar(service.sql, '?'));
        assertObject("p_id", null, service.parametros.get(Integer.valueOf(2)));
        assertObject("p_afiliado_cuil_titular", "20111111112", service.parametros.get(Integer.valueOf(3)));
        assertObject("p_afiliado_int", Integer.valueOf(2), service.parametros.get(Integer.valueOf(4)));
        assertObject("p_afiliado_id_ospim", Integer.valueOf(123456), service.parametros.get(Integer.valueOf(5)));
        assertObject("p_afiliado_nombre", "Nombre", service.parametros.get(Integer.valueOf(6)));
        assertObject("p_afiliado_apellido", "Apellido", service.parametros.get(Integer.valueOf(7)));
        assertObject("p_afiliado_documento_tipo", "DNI", service.parametros.get(Integer.valueOf(8)));
        assertObject("p_afiliado_documento_nro", "11222333", service.parametros.get(Integer.valueOf(9)));
        assertObject("p_afiliado_direccion", "Calle 123", service.parametros.get(Integer.valueOf(10)));
        assertObject("p_afiliado_localidad", "Localidad", service.parametros.get(Integer.valueOf(11)));
        assertObject("p_afiliado_provincia", "Provincia", service.parametros.get(Integer.valueOf(12)));
        assertObject("p_afiliado_celular", "111-222", service.parametros.get(Integer.valueOf(13)));
        assertObject("p_afiliado_telefono", "333-444", service.parametros.get(Integer.valueOf(14)));
        assertObject("p_afiliado_email", "afiliado@example.com", service.parametros.get(Integer.valueOf(15)));
        assertObject("p_id_sector", Integer.valueOf(5), service.parametros.get(Integer.valueOf(16)));
        assertObject("p_cargo_ospim", Integer.valueOf(80), service.parametros.get(Integer.valueOf(17)));
        assertObject("p_cargo_tercerizadora", Integer.valueOf(20), service.parametros.get(Integer.valueOf(18)));
        assertObject("p_id_tercerizadora", "CSA", service.parametros.get(Integer.valueOf(19)));
        assertObject("p_recupero", Boolean.TRUE, service.parametros.get(Integer.valueOf(20)));
        assertObject("p_surge", Boolean.TRUE, service.parametros.get(Integer.valueOf(21)));
        assertObject("p_observaciones", "Observaciones", service.parametros.get(Integer.valueOf(22)));
        assertObject("p_usuario", "tester", service.parametros.get(Integer.valueOf(23)));
    }

    private static void assertAltaSinOrdenMedicaRechazadaPorMetodoHistorico()
            throws Exception {

        EditarRequerimientoCompraServiceImpl service =
                new EditarRequerimientoCompraServiceImpl();

        try {
            service.guardarRequerimientoCompra(
                    requerimientoAltaValido(),
                    "tester"
            );
            throw new AssertionError(
                    "el método histórico no debe crear sin Orden médica"
            );
        } catch (Exception expected) {
            if (expected.getMessage() == null
                    || expected.getMessage().indexOf("Orden médica") < 0) {

                throw expected;
            }
        }
    }

    private static void assertAltaConOrdenMedicaConfirmaTransaccion()
            throws Exception {

        ServicioAltaOrdenMedicaPrueba service =
                new ServicioAltaOrdenMedicaPrueba(false);
        GestorDocumentoPrueba gestor = new GestorDocumentoPrueba();
        File archivo = crearImagenTemporal();

        try {
            int id = service.guardarNuevoRequerimientoCompraConOrdenMedica(
                    requerimientoAltaValido(),
                    ordenMedicaValida(archivo),
                    gestor,
                    "tester"
            );

            assertInt("id alta con Orden médica", 987, id);
            assertBoolean("commit alta con Orden médica", true, service.commit);
            assertBoolean("sin rollback exitoso", false, service.rollback);
            assertBoolean("sin compensación exitosa", false, gestor.eliminado);
            assertBoolean("archivo DL creado", true, gestor.creado);
        } finally {
            archivo.delete();
        }
    }

    private static void assertFalloSqlPosteriorADlCompensaYRevierte()
            throws Exception {

        ServicioAltaOrdenMedicaPrueba service =
                new ServicioAltaOrdenMedicaPrueba(true);
        GestorDocumentoPrueba gestor = new GestorDocumentoPrueba();
        File archivo = crearImagenTemporal();

        try {
            try {
                service.guardarNuevoRequerimientoCompraConOrdenMedica(
                        requerimientoAltaValido(),
                        ordenMedicaValida(archivo),
                        gestor,
                        "tester"
                );
                throw new AssertionError("se esperaba fallo SQL posterior a DL");
            } catch (Exception expected) {
                assertBoolean("rollback por fallo SQL", true, service.rollback);
                assertBoolean("sin commit por fallo SQL", false, service.commit);
                assertBoolean("compensación DL", true, gestor.eliminado);
            }
        } finally {
            archivo.delete();
        }
    }

    private static RequerimientoCompra requerimientoAltaValido() {
        RequerimientoCompra requerimiento = new RequerimientoCompra();
        requerimiento.setIdSector(Integer.valueOf(5));
        requerimiento.setCargoOspim(Integer.valueOf(100));
        requerimiento.setCargoTercerizadora(Integer.valueOf(0));
        return requerimiento;
    }

    private static OrdenMedicaValidada ordenMedicaValida(File archivo) {
        return new OrdenMedicaValidada(
                archivo,
                "orden-medica.png",
                ".png",
                "image/png",
                java.sql.Date.valueOf("2026-08-12")
        );
    }

    private static File crearImagenTemporal() throws Exception {
        File archivo = File.createTempFile("orden-medica", ".png");
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(archivo);
            out.write(new byte[] {
                    (byte) 0x89, 0x50, 0x4E, 0x47,
                    0x0D, 0x0A, 0x1A, 0x0A
            });
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return archivo;
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

    private static void assertObject(
            String descripcion,
            Object esperado,
            Object actual) {

        if (esperado == null
                ? actual != null
                : !esperado.equals(actual)) {

            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static int contar(
            String value,
            char esperado) {

        int total = 0;

        for (int i = 0; value != null && i < value.length(); i++) {
            if (value.charAt(i) == esperado) {
                total++;
            }
        }

        return total;
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
                    detalles
            );
        }

        public BigDecimal calcularTotal(
                Integer cantidadPersistida,
                RequerimientoCompraDetalle detalle) {

            if (cantidadPersistida == null
                    || detalle == null
                    || detalle.getPrecioUnitarioEstimado() == null) {

                return null;
            }

            return detalle
                    .getPrecioUnitarioEstimado()
                    .multiply(
                            BigDecimal.valueOf(
                                    cantidadPersistida.longValue()
                            )
                    );
        }

        public void validarPrestador(
                int idRequerimientoCompra,
                Integer idPrestador)
                throws Exception {

            if (idPrestador == null
                    || !prestadoresEnviados.contains(idPrestador)) {

                throw new Exception(
                        "El prestador no fue notificado correctamente."
                );
            }
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

    private static class ServicioGuardarPrueba
            extends EditarRequerimientoCompraServiceImpl {

        private String sql;
        private Map<Integer, Object> parametros =
                new LinkedHashMap<Integer, Object>();
        private Map<Integer, Object> outParameters =
                new LinkedHashMap<Integer, Object>();

        protected Connection obtenerConexionGuardarRequerimiento() {
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class[] {Connection.class},
                    new ConnectionHandler(this)
            );
        }

        private int guardarCabeceraPrueba(
                RequerimientoCompra requerimiento,
                String usuario) throws Exception {

            return guardarCabeceraEnConexion(
                    obtenerConexionGuardarRequerimiento(),
                    requerimiento,
                    usuario
            );
        }
    }

    private static class ServicioAltaOrdenMedicaPrueba
            extends EditarRequerimientoCompraServiceImpl {

        private final boolean fallarRegistroOrden;
        private boolean commit;
        private boolean rollback;
        private int prepareCallCount;

        private ServicioAltaOrdenMedicaPrueba(boolean fallarRegistroOrden) {
            this.fallarRegistroOrden = fallarRegistroOrden;
        }

        protected Connection obtenerConexionGuardarNuevoConOrdenMedica() {
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class[] {Connection.class},
                    new InvocationHandler() {
                        public Object invoke(
                                Object proxy,
                                Method method,
                                Object[] args) throws Throwable {

                            String name = method.getName();

                            if ("prepareCall".equals(name)) {
                                prepareCallCount++;
                                return crearCallableAlta(
                                        fallarRegistroOrden
                                                && prepareCallCount == 2
                                );
                            }

                            if ("commit".equals(name)) {
                                commit = true;
                                return null;
                            }

                            if ("rollback".equals(name)) {
                                rollback = true;
                                return null;
                            }

                            return defaultValue(method.getReturnType());
                        }
                    }
            );
        }

        private CallableStatement crearCallableAlta(final boolean fallar) {
            return (CallableStatement) Proxy.newProxyInstance(
                    CallableStatement.class.getClassLoader(),
                    new Class[] {CallableStatement.class},
                    new InvocationHandler() {
                        public Object invoke(
                                Object proxy,
                                Method method,
                                Object[] args) throws Throwable {

                            String name = method.getName();

                            if ("execute".equals(name)) {
                                if (fallar) {
                                    throw new java.sql.SQLException(
                                            "fallo simulado después de DL"
                                    );
                                }
                                return Boolean.TRUE;
                            }

                            if ("getInt".equals(name)) {
                                return Integer.valueOf(987);
                            }

                            return defaultValue(method.getReturnType());
                        }
                    }
            );
        }
    }

    private static class GestorDocumentoPrueba
            implements GestorOrdenMedicaDocumento {

        private boolean creado;
        private boolean eliminado;

        public DocumentoComprasCreado crearOrdenMedica(
                int idRequerimiento,
                OrdenMedicaValidada ordenMedica) {

            creado = true;
            return new DocumentoComprasCreado(
                    10112L,
                    50L,
                    99L,
                    "uuid-prueba",
                    "ORDEN-MEDICA-COMPRA-" + idRequerimiento + "-abc.png",
                    "Orden médica"
            );
        }

        public void eliminarDocumento(DocumentoComprasCreado documento) {
            eliminado = true;
        }
    }

    private static class ConnectionHandler implements InvocationHandler {

        private final ServicioGuardarPrueba service;

        private ConnectionHandler(ServicioGuardarPrueba service) {
            this.service = service;
        }

        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("prepareCall".equals(method.getName())) {
                service.sql =
                        (String) args[0];

                return Proxy.newProxyInstance(
                        CallableStatement.class.getClassLoader(),
                        new Class[] {CallableStatement.class},
                        new CallableStatementHandler(service)
                );
            }

            return defaultValue(method.getReturnType());
        }
    }

    private static class CallableStatementHandler implements InvocationHandler {

        private final ServicioGuardarPrueba service;

        private CallableStatementHandler(ServicioGuardarPrueba service) {
            this.service = service;
        }

        public Object invoke(Object proxy, Method method, Object[] args) {
            String name =
                    method.getName();

            if ("registerOutParameter".equals(name)) {
                service.outParameters.put(
                        (Integer) args[0],
                        args[1]
                );

                return null;
            }

            if ("setNull".equals(name)) {
                service.parametros.put(
                        (Integer) args[0],
                        null
                );

                return null;
            }

            if (name.startsWith("set")
                    && args != null
                    && args.length >= 2
                    && args[0] instanceof Integer) {

                service.parametros.put(
                        (Integer) args[0],
                        args[1]
                );

                return null;
            }

            if ("execute".equals(name)) {
                return Boolean.TRUE;
            }

            if ("getInt".equals(name)) {
                return Integer.valueOf(987);
            }

            return defaultValue(method.getReturnType());
        }
    }

    private static Object defaultValue(Class type) {
        if (type == null || Void.TYPE.equals(type)) {
            return null;
        }

        if (!type.isPrimitive()) {
            return null;
        }

        if (Boolean.TYPE.equals(type)) {
            return Boolean.FALSE;
        }

        if (Integer.TYPE.equals(type)) {
            return Integer.valueOf(0);
        }

        if (Long.TYPE.equals(type)) {
            return Long.valueOf(0L);
        }

        if (Double.TYPE.equals(type)) {
            return Double.valueOf(0D);
        }

        if (Float.TYPE.equals(type)) {
            return Float.valueOf(0F);
        }

        if (Short.TYPE.equals(type)) {
            return Short.valueOf((short) 0);
        }

        if (Byte.TYPE.equals(type)) {
            return Byte.valueOf((byte) 0);
        }

        if (Character.TYPE.equals(type)) {
            return Character.valueOf((char) 0);
        }

        return null;
    }

    private EditarRequerimientoCompraServiceImplTest() {
    }
}
