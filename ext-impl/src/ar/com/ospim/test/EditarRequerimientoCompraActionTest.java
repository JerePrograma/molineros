package ar.com.ospim.test;

import ar.com.ospim.compras.requerimientos.action.EditarRequerimientoCompraAction;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;

public class EditarRequerimientoCompraActionTest {

    public static void main(String[] args) throws Exception {
        assertSurgeAusentePersisteFalse();
        assertSurgeTruePersisteTrue();
        assertSurgeOnPersisteTrue();
        assertSurgeUnoPersisteTrue();
        assertSurgeFalseExplicitoPersisteFalse();
    }

    private static void assertSurgeAusentePersisteFalse()
            throws Exception {

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(
                        parametrosBase()
                );

        assertBoolean(
                "surge ausente",
                false,
                requerimiento.isSurge()
        );
    }

    private static void assertSurgeTruePersisteTrue()
            throws Exception {

        Map<String, String> params =
                parametrosBase();

        params.put(
                "surge",
                "true"
        );

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(
                        params
                );

        assertBoolean(
                "surge true",
                true,
                requerimiento.isSurge()
        );
    }

    private static void assertSurgeOnPersisteTrue()
            throws Exception {

        Map<String, String> params =
                parametrosBase();

        params.put(
                "surge",
                "on"
        );

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(
                        params
                );

        assertBoolean(
                "surge on",
                true,
                requerimiento.isSurge()
        );
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

    private static void assertSurgeFalseExplicitoPersisteFalse()
            throws Exception {

        Map<String, String> params =
                parametrosBase();

        params.put(
                "surge",
                "false"
        );

        RequerimientoCompra requerimiento =
                requerimientoDesdeRequest(
                        params
                );

        assertBoolean(
                "surge false",
                false,
                requerimiento.isSurge()
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
