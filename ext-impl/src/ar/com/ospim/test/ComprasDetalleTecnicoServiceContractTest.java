package ar.com.ospim.test;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;
import ar.com.ospim.farmacia.beans.Medicamento;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ComprasDetalleTecnicoServiceContractTest {

    public static void main(String[] args) throws Exception {
        assertMedicamentoCanonicoYParametros();
        assertNomencladorCanonicoYParametros();
        assertCamposCruzadosRechazados();
        assertTipoIncompatibleConSectorRechazado();
        assertIdInexistenteRechazado();
        assertTextoManipuladoRechazado();
        assertSinMetodosDeArticulo();
        System.out.println("CONTRATO_DETALLE_TECNICO_COMPRAS_OK");
    }

    private static void assertMedicamentoCanonicoYParametros() throws Exception {
        Servicio servicio = new Servicio("Farmacia");
        Medicamento medicamento = new Medicamento();
        medicamento.setId_medicamento(101);
        medicamento.setTroquel(12345);
        medicamento.setNombre("MEDICAMENTO");
        medicamento.setPresentacion("10 MG");
        servicio.medicamento = medicamento;

        RequerimientoCompraDetalle detalle = base("MEDICAMENTO");
        detalle.setIdMedicamento(Integer.valueOf(101));
        detalle.setTroquel(Integer.valueOf(12345));
        detalle.setNombreMedicamento("MEDICAMENTO 10 MG");

        int id = servicio.guardarDetalle(detalle, "tester");

        assertEquals("id retornado", Integer.valueOf(777), Integer.valueOf(id));
        assertEquals("firma de 13 parametros", Integer.valueOf(13),
                Integer.valueOf(servicio.parametros.size()));
        assertEquals("13 placeholders", Integer.valueOf(13),
                Integer.valueOf(contar(servicio.sql, '?')));
        assertEquals("tipo", "MEDICAMENTO", servicio.parametros.get(Integer.valueOf(3)));
        assertEquals("nomenclador nulo", null, servicio.parametros.get(Integer.valueOf(4)));
        assertEquals("id medicamento", Integer.valueOf(101), servicio.parametros.get(Integer.valueOf(8)));
        assertEquals("troquel", Integer.valueOf(12345), servicio.parametros.get(Integer.valueOf(9)));
        assertEquals("nombre canonico", "MEDICAMENTO 10 MG", servicio.parametros.get(Integer.valueOf(10)));
        assertEquals("cantidad", Integer.valueOf(2), servicio.parametros.get(Integer.valueOf(11)));
        assertEquals("usuario", "tester", servicio.parametros.get(Integer.valueOf(13)));
    }

    private static void assertNomencladorCanonicoYParametros() throws Exception {
        Servicio servicio = new Servicio("Prestaciones Medicas");
        Nomenclador nomenclador = new Nomenclador();
        nomenclador.setId_prestacion(201);
        nomenclador.setId_tipo_nomenclador(8);
        nomenclador.setCodigo("NM-001");
        nomenclador.setDescripcion("PRESTACION CANONICA");
        servicio.nomenclador = nomenclador;

        RequerimientoCompraDetalle detalle = base("NOMENCLADOR");
        detalle.setIdPrestacion(Integer.valueOf(201));
        detalle.setIdTipoNomenclador(Integer.valueOf(8));
        detalle.setCodigoNomenclador("nm-001");
        detalle.setDescripcionNomenclador("prestacion canonica");

        servicio.guardarDetalle(detalle, "tester");

        assertEquals("id prestacion", Integer.valueOf(201), servicio.parametros.get(Integer.valueOf(4)));
        assertEquals("tipo nomenclador", Integer.valueOf(8), servicio.parametros.get(Integer.valueOf(5)));
        assertEquals("codigo reconstruido", "NM-001", servicio.parametros.get(Integer.valueOf(6)));
        assertEquals("descripcion reconstruida", "PRESTACION CANONICA", servicio.parametros.get(Integer.valueOf(7)));
        assertEquals("medicamento nulo", null, servicio.parametros.get(Integer.valueOf(8)));
    }

    private static void assertCamposCruzadosRechazados() throws Exception {
        final Servicio servicio = new Servicio("Farmacia");
        servicio.medicamento = medicamentoCanonico();
        final RequerimientoCompraDetalle detalle = base("MEDICAMENTO");
        detalle.setIdMedicamento(Integer.valueOf(101));
        detalle.setNombreMedicamento("MEDICAMENTO 10 MG");
        detalle.setIdPrestacion(Integer.valueOf(201));

        assertRechazo("campos cruzados", new Ejecucion() {
            public void ejecutar() throws Exception {
                servicio.guardarDetalle(detalle, "tester");
            }
        });
    }

    private static void assertTipoIncompatibleConSectorRechazado() throws Exception {
        final Servicio servicio = new Servicio("Legales");
        servicio.medicamento = medicamentoCanonico();
        final RequerimientoCompraDetalle detalle = base("MEDICAMENTO");
        detalle.setIdMedicamento(Integer.valueOf(101));
        detalle.setNombreMedicamento("MEDICAMENTO 10 MG");

        assertRechazo("tipo incompatible", new Ejecucion() {
            public void ejecutar() throws Exception {
                servicio.guardarDetalle(detalle, "tester");
            }
        });
    }

    private static void assertIdInexistenteRechazado() throws Exception {
        final Servicio servicio = new Servicio("Farmacia");
        final RequerimientoCompraDetalle detalle = base("MEDICAMENTO");
        detalle.setIdMedicamento(Integer.valueOf(999));
        detalle.setNombreMedicamento("INEXISTENTE");

        assertRechazo("id inexistente", new Ejecucion() {
            public void ejecutar() throws Exception {
                servicio.guardarDetalle(detalle, "tester");
            }
        });
    }

    private static void assertTextoManipuladoRechazado() throws Exception {
        final Servicio servicio = new Servicio("Prestaciones Medicas");
        Nomenclador nomenclador = new Nomenclador();
        nomenclador.setId_prestacion(201);
        nomenclador.setId_tipo_nomenclador(8);
        nomenclador.setCodigo("NM-001");
        nomenclador.setDescripcion("PRESTACION CANONICA");
        servicio.nomenclador = nomenclador;

        final RequerimientoCompraDetalle detalle = base("NOMENCLADOR");
        detalle.setIdPrestacion(Integer.valueOf(201));
        detalle.setIdTipoNomenclador(Integer.valueOf(8));
        detalle.setCodigoNomenclador("CODIGO MANIPULADO");
        detalle.setDescripcionNomenclador("PRESTACION CANONICA");

        assertRechazo("codigo manipulado", new Ejecucion() {
            public void ejecutar() throws Exception {
                servicio.guardarDetalle(detalle, "tester");
            }
        });
    }

    private static void assertSinMetodosDeArticulo() {
        Method[] methods = EditarRequerimientoCompraServiceImpl.class.getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().toLowerCase().contains("articulo")) {
                throw new AssertionError("Metodo residual: " + methods[i].getName());
            }
        }
    }

    private static RequerimientoCompraDetalle base(String tipo) {
        RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();
        detalle.setIdRequerimientoCompra(Integer.valueOf(42));
        detalle.setTipoItem(tipo);
        detalle.setCantidad(Integer.valueOf(2));
        detalle.setObservaciones("OBS");
        return detalle;
    }

    private static Medicamento medicamentoCanonico() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId_medicamento(101);
        medicamento.setTroquel(12345);
        medicamento.setNombre("MEDICAMENTO");
        medicamento.setPresentacion("10 MG");
        return medicamento;
    }

    private static void assertRechazo(String nombre, Ejecucion ejecucion)
            throws Exception {
        try {
            ejecucion.ejecutar();
        } catch (Exception esperado) {
            return;
        }
        throw new AssertionError(nombre + ": se esperaba rechazo");
    }

    private static void assertEquals(String nombre, Object esperado, Object actual) {
        if (esperado == null ? actual != null : !esperado.equals(actual)) {
            throw new AssertionError(nombre + ": esperado=" + esperado + ", actual=" + actual);
        }
    }

    private static int contar(String value, char buscado) {
        int total = 0;
        for (int i = 0; value != null && i < value.length(); i++) {
            if (value.charAt(i) == buscado) {
                total++;
            }
        }
        return total;
    }

    private interface Ejecucion {
        void ejecutar() throws Exception;
    }

    private static final class Servicio
            extends EditarRequerimientoCompraServiceImpl {

        private final RequerimientoCompra requerimiento;
        private Nomenclador nomenclador;
        private Medicamento medicamento;
        private String sql;
        private Map<Integer, Object> parametros =
                new LinkedHashMap<Integer, Object>();

        private Servicio(String sector) {
            requerimiento = new RequerimientoCompra();
            requerimiento.setIdRequerimientoCompra(42);
            requerimiento.setIdSector(Integer.valueOf(1));
            requerimiento.setSectorDescripcion(sector);
            requerimiento.setEstado(1);
        }

        protected RequerimientoCompra obtenerRequerimientoDetalle(int id) {
            return requerimiento;
        }

        protected Nomenclador obtenerNomencladorCanonico(int id) {
            return nomenclador;
        }

        protected Medicamento obtenerMedicamentoCanonico(int id) {
            return medicamento;
        }

        protected Connection obtenerConexionGuardarDetalle() {
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class[] {Connection.class},
                    new ConnectionHandler(this)
            );
        }
    }

    private static final class ConnectionHandler implements InvocationHandler {
        private final Servicio servicio;

        private ConnectionHandler(Servicio servicio) {
            this.servicio = servicio;
        }

        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("prepareCall".equals(method.getName())) {
                servicio.sql = (String) args[0];
                return Proxy.newProxyInstance(
                        CallableStatement.class.getClassLoader(),
                        new Class[] {CallableStatement.class},
                        new StatementHandler(servicio)
                );
            }
            return valorDefault(method.getReturnType());
        }
    }

    private static final class StatementHandler implements InvocationHandler {
        private final Servicio servicio;

        private StatementHandler(Servicio servicio) {
            this.servicio = servicio;
        }

        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("setNull".equals(name)) {
                servicio.parametros.put((Integer) args[0], null);
                return null;
            }
            if (name.startsWith("set") && args != null && args.length >= 2
                    && args[0] instanceof Integer) {
                servicio.parametros.put((Integer) args[0], args[1]);
                return null;
            }
            if ("executeQuery".equals(name)) {
                return Proxy.newProxyInstance(
                        ResultSet.class.getClassLoader(),
                        new Class[] {ResultSet.class},
                        new ResultSetHandler()
                );
            }
            return valorDefault(method.getReturnType());
        }
    }

    private static final class ResultSetHandler implements InvocationHandler {
        private int nextCalls;

        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("next".equals(method.getName())) {
                nextCalls++;
                return Boolean.valueOf(nextCalls == 1);
            }
            if ("getInt".equals(method.getName())) {
                return Integer.valueOf(777);
            }
            if ("wasNull".equals(method.getName())) {
                return Boolean.FALSE;
            }
            return valorDefault(method.getReturnType());
        }
    }

    private static Object valorDefault(Class type) {
        if (type == null || Void.TYPE.equals(type) || !type.isPrimitive()) {
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

    private ComprasDetalleTecnicoServiceContractTest() {
    }
}
