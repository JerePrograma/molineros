package ar.com.ospim.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.util.LinkedHashMap;
import java.util.Map;

import ar.com.ospim.afiliados.reportes.ReportesAfiliadoServiceImpl;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;

/**
 * Regresion independiente del contrato de bajas por fecha de proceso.
 * El catalogo inspeccionado define 17 parametros: motivo en 15 y fechas de
 * proceso en 16/17. Esta prueba falla mientras Java no transporte ese contrato.
 * No ejecuta consultas ni abre conexiones. No se debe convertir su fallo en
 * exito cambiando la expectativa a la cantidad incorrecta de parametros.
 */
public class PadronBajasPersistenciaContractTest {

    public static void main(String[] args) throws Exception {
        final Map<Integer, String> parametros = new LinkedHashMap<Integer, String>();
        CallableStatement statement = (CallableStatement) Proxy.newProxyInstance(
                PadronBajasPersistenciaContractTest.class.getClassLoader(),
                new Class[] {CallableStatement.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] values) throws Throwable {
                        if (method.getName().startsWith("set") && values != null && values.length == 2) {
                            parametros.put((Integer) values[0], method.getName());
                        }
                        return null;
                    }
                });
        BusquedaReportePadronFiltro filtro = new BusquedaReportePadronFiltro();
        filtro.setTipoBusqueda(2);
        filtro.setFechaDesde(java.sql.Date.valueOf("2026-01-01"));
        filtro.setFechaHasta(java.sql.Date.valueOf("2026-09-06"));
        filtro.setIdsMotivoBaja("1,");
        Method enlazar = ReportesAfiliadoServiceImpl.class.getDeclaredMethod(
                "setearParametrosQueryPadron", BusquedaReportePadronFiltro.class, CallableStatement.class);
        enlazar.setAccessible(true);
        enlazar.invoke(new ReportesAfiliadoServiceImpl(), filtro, statement);
        if (parametros.size() != 17
                || !"setString".equals(parametros.get(Integer.valueOf(15)))
                || !"setDate".equals(parametros.get(Integer.valueOf(16)))
                || !"setDate".equals(parametros.get(Integer.valueOf(17)))) {
            throw new IllegalStateException(
                    "Contrato de bajas incompatible: catalogo requiere 17 parametros, "
                    + "motivo en 15 y fechas de proceso en 16/17; Java envio " + parametros);
        }
        System.out.println("PADRON_BAJAS_PERSISTENCIA_CONTRACT_OK");
    }
}
