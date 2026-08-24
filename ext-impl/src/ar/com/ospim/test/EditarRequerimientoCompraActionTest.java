package ar.com.ospim.test;

import ar.com.ospim.compras.requerimientos.action.EditarRequerimientoCompraAction;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;

public class EditarRequerimientoCompraActionTest {

    public static void main(String[] args) throws Exception {
        assertSurgeAusenteEsRechazado();
        assertSurgeInvalidoEsRechazado("true");
        assertSurgeInvalidoEsRechazado("on");
        assertSurgeInvalidoEsRechazado("false");
        assertSurgeUnoPersisteTrue();
        assertSurgeCeroPersisteFalse();
        assertLegalesMarcadoPersisteTrue();
        assertLegalesDesmarcadoPersisteFalse();
        assertLegalesAusenteTerminaEnFalse();

        System.out.println("EDITAR_REQUERIMIENTO_COMPRA_ACTION_TEST_OK");
        System.exit(0);
    }

    private static void assertSurgeAusenteEsRechazado()
            throws Exception {

        assertSurgeInvalido(
                "surge ausente",
                parametrosBase()
        );
    }

    private static void assertSurgeInvalidoEsRechazado(
            String valor)
            throws Exception {

        Map<String, String> params =
                parametrosBase();

        params.put(
                "surge",
                valor
        );

        assertSurgeInvalido(
                "surge invalido " + valor,
                params
        );
    }

    private static void assertSurgeInvalido(
            String descripcion,
            Map<String, String> params)
            throws Exception {

        try {
            requerimientoDesdeRequest(params);
            throw new AssertionError(
                    descripcion + ": se esperaba rechazo"
            );
        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();

            if (causa == null
                    || causa.getMessage() == null
                    || causa.getMessage().indexOf("Surge") < 0) {

                throw e;
            }
        }
    }

    private static void assertSurgeUnoPersisteTrue()
            throws Exception {

        Map<String, String> params =
                parametrosBase();

        params.put(
                "surge",
                "1"
        );

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(
                        params
                );

        assertBoolean(
                "surge 1",
                true,
                requerimiento.isSurge()
        );
    }

    private static void assertSurgeCeroPersisteFalse()
            throws Exception {

        Map<String, String> params =
                parametrosBase();

        params.put(
                "surge",
                "0"
        );

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(
                        params
                );

        assertBoolean(
                "surge 0",
                false,
                requerimiento.isSurge()
        );
    }

    private static void assertLegalesMarcadoPersisteTrue()
            throws Exception {

        assertLegalesMarcadoPersisteTrueEnSector("1", "FARMACIA");
        assertLegalesMarcadoPersisteTrueEnSector("2", "NO FARMACIA");
    }

    private static void assertLegalesMarcadoPersisteTrueEnSector(
            String idSector,
            String descripcionSector) throws Exception {

        Map<String, String> params =
                parametrosValidos();

        params.put("sector_id", idSector);
        params.put("legales", "true");

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(params);

        assertBoolean(
                "legales true sector " + descripcionSector,
                true,
                requerimiento.isLegales()
        );
    }

    private static void assertLegalesDesmarcadoPersisteFalse()
            throws Exception {

        Map<String, String> params = parametrosValidos();
        params.put("legales", "false");

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(params);

        assertBoolean(
                "legales false explicito",
                false,
                requerimiento.isLegales()
        );
    }

    private static void assertLegalesAusenteTerminaEnFalse()
            throws Exception {

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(parametrosValidos());

        assertBoolean(
                "legales ausente",
                false,
                requerimiento.isLegales()
        );
    }

    private static RequerimientoCompra requerimientoDesdeRequest(
            Map<String, String> params) throws Exception {

        EditarRequerimientoCompraAction action =
                new EditarRequerimientoCompraAction();

        Method method =
                EditarRequerimientoCompraAction.class.getDeclaredMethod(
                        "getRequerimientoFromRequest",
                        ActionRequest.class
                );

        method.setAccessible(
                true
        );

        return (RequerimientoCompra) method.invoke(
                action,
                request(params)
        );
    }

    private static ActionRequest request(
            Map<String, String> params) {

        return (ActionRequest) Proxy.newProxyInstance(
                ActionRequest.class.getClassLoader(),
                new Class[] {ActionRequest.class},
                new RequestHandler(params)
        );
    }

    private static Map<String, String> parametrosBase() {
        Map<String, String> params =
                new HashMap<String, String>();

        params.put(
                "sector_id",
                "1"
        );

        params.put(
                "cargo_ospim",
                "100"
        );

        params.put(
                "cargo_tercerizadora",
                "0"
        );

        return params;
    }

    private static Map<String, String> parametrosValidos() {
        Map<String, String> params = parametrosBase();
        params.put("surge", "0");
        return params;
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

    private static class RequestHandler
            implements InvocationHandler {

        private final Map<String, String> params;

        RequestHandler(
                Map<String, String> params) {

            this.params =
                    params;
        }

        public Object invoke(
                Object proxy,
                Method method,
                Object[] args) {

            String name =
                    method.getName();

            if ("getParameter".equals(name)) {
                return params.get(
                        args[0]
                );
            }

            if ("getParameterValues".equals(name)) {
                String value =
                        params.get(
                                args[0]
                        );

                return value != null
                        ? new String[] {value}
                        : null;
            }

            if ("getParameterMap".equals(name)) {
                Map<String, String[]> result =
                        new HashMap<String, String[]>();

                for (Map.Entry<String, String> entry : params.entrySet()) {
                    result.put(
                            entry.getKey(),
                            new String[] {entry.getValue()}
                    );
                }

                return result;
            }

            Class returnType =
                    method.getReturnType();

            if (Boolean.TYPE.equals(returnType)) {
                return Boolean.FALSE;
            }

            if (Integer.TYPE.equals(returnType)) {
                return Integer.valueOf(0);
            }

            if (Long.TYPE.equals(returnType)) {
                return Long.valueOf(0);
            }

            return null;
        }
    }

    private EditarRequerimientoCompraActionTest() {
    }
}
