import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.liferay.portal.model.Permission;
import com.liferay.portal.model.Resource;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.model.Role;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.PermissionLocalService;
import com.liferay.portal.service.PermissionLocalServiceUtil;
import com.liferay.portal.service.ResourceActionLocalService;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalService;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalService;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission;

/* Permission checker real de Liferay. Servicios simulados en un proceso aislado;
 * no demuestra permisos efectivos del servidor ni usa DB, red o archivos clinicos. */
public class ComprasOrdenMedicaPermisoTest {
    private static int checks;
    private static final String RESOURCE = DLFileEntry.class.getName();

    public static void main(String[] args) throws Exception {
        Fixture f = new Fixture();
        f.install();
        rechazada(f);
        System.out.println("REPRODUCIDO PrincipalException: usuario sin VIEW, check real folderId/name.");
        if (args.length > 0 && "repro".equals(args[0])) {
            System.out.println("COMPRAS_ORDEN_MEDICA_PERMISO_REPRO_OK checks=" + checks);
            return;
        }
        int original = PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM;
        try {
            permiso(5);
            permiso(6);
            invalida();
        } finally {
            PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM = original;
        }
        System.out.println("COMPRAS_ORDEN_MEDICA_PERMISO_OFFLINE_OK checks=" + checks);
        System.out.println("Helper y check DL productivos; servicios simulados; sin DB, Tomcat ni UI.");
    }

    private static void permiso(int algoritmo) throws Exception {
        PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM = algoritmo;
        Fixture f = new Fixture();
        f.install();
        rechazada(f);
        asegurar(f.entry);
        igual("permiso VIEW agregado algoritmo " + algoritmo, Boolean.TRUE,
                Boolean.valueOf(f.allowed.contains("VIEW")));
        igual("UPDATE conservado", Boolean.TRUE, Boolean.valueOf(f.allowed.contains("UPDATE")));
        igual("sin DELETE ajeno", Boolean.FALSE, Boolean.valueOf(f.allowed.contains("DELETE")));
        igual("una escritura", Integer.valueOf(1), Integer.valueOf(f.writes));
        DLFileEntryPermission.check(f.checker, 200L, "medical.pdf", "VIEW");
        checks++;
        asegurar(f.entry);
        igual("sin escritura repetida", Integer.valueOf(1), Integer.valueOf(f.writes));
    }

    private static void invalida() throws Exception {
        Fixture f = new Fixture();
        f.install();
        try {
            asegurar(null);
            throw new AssertionError("Se acepto una entrada nula");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception esperado) {
            checks++;
        }
        igual("entrada nula no concede", Integer.valueOf(0), Integer.valueOf(f.writes));
    }

    private static void rechazada(Fixture f) throws Exception {
        try {
            DLFileEntryPermission.check(f.checker, 200L, "medical.pdf", "VIEW");
            throw new AssertionError("Falto PrincipalException sin VIEW");
        } catch (PrincipalException esperado) {
            checks++;
        }
    }

    private static void asegurar(DLFileEntry entry) throws Exception {
        Class helper = Class.forName("ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper");
        Method method = helper.getMethod("asegurarPermisoVistaAbm", new Class[] {DLFileEntry.class});
        try {
            method.invoke(null, new Object[] {entry});
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) { throw (Exception) cause; }
            if (cause instanceof Error) { throw (Error) cause; }
            throw e;
        }
    }

    private static void igual(String label, Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + ": esperado=" + expected + " actual=" + actual);
        }
        checks++;
    }

    private static Object proxy(Class type, InvocationHandler handler) {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, handler);
    }

    private static Object model(Class type, final Object[][] values) {
        return proxy(type, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                for (int i = 0; i < values.length; i++) {
                    if (values[i][0].equals(m.getName())) { return values[i][1]; }
                }
                return fallback(p, m, a);
            }
        });
    }

    private static Object fallback(Object p, Method m, Object[] a) {
        if ("toString".equals(m.getName())) { return "fixture:" + p.getClass().getInterfaces()[0].getName(); }
        if ("hashCode".equals(m.getName())) { return Integer.valueOf(System.identityHashCode(p)); }
        if ("equals".equals(m.getName())) { return Boolean.valueOf(p == a[0]); }
        throw new UnsupportedOperationException(m.toString());
    }

    private static class Fixture implements InvocationHandler {
        private final List<String> allowed = new ArrayList<String>(Arrays.asList(new String[] {"UPDATE"}));
        private int writes;
        private final DLFileEntry entry = (DLFileEntry) model(DLFileEntry.class, new Object[][] {
            {"getCompanyId", Long.valueOf(900)}, {"getGroupId", Long.valueOf(100)},
            {"getFolderId", Long.valueOf(200)}, {"getFileEntryId", Long.valueOf(300)},
            {"getUserId", Long.valueOf(400)}, {"getName", "medical.pdf"}
        });
        private final PermissionChecker checker = (PermissionChecker) proxy(PermissionChecker.class, this);

        private void install() {
            new DLFileEntryLocalServiceUtil().setService((DLFileEntryLocalService) proxy(DLFileEntryLocalService.class, this));
            new RoleLocalServiceUtil().setService((RoleLocalService) proxy(RoleLocalService.class, this));
            new ResourceLocalServiceUtil().setService((ResourceLocalService) proxy(ResourceLocalService.class, this));
            new PermissionLocalServiceUtil().setService((PermissionLocalService) proxy(PermissionLocalService.class, this));
            new ResourcePermissionLocalServiceUtil().setService((ResourcePermissionLocalService) proxy(ResourcePermissionLocalService.class, this));
            new ResourceActionLocalServiceUtil().setService((ResourceActionLocalService) proxy(ResourceActionLocalService.class, this));
        }

        public Object invoke(Object p, Method m, Object[] a) throws Throwable {
            String name = m.getName();
            if ("getFileEntry".equals(name)) {
                igual("folder de entrada", Long.valueOf(200), a[0]);
                igual("nombre de entrada", "medical.pdf", a[1]);
                return entry;
            }
            if ("hasOwnerPermission".equals(name)) { return Boolean.FALSE; }
            if ("hasPermission".equals(name)) {
                igual("grupo de check", Long.valueOf(100), a[0]);
                igual("modelo de check", RESOURCE, a[1]);
                igual("id de check", Long.valueOf(300), a[2]);
                igual("accion de check", "VIEW", a[3]);
                return Boolean.valueOf(allowed.contains("VIEW"));
            }
            if ("getRole".equals(name)) {
                igual("empresa del rol", Long.valueOf(900), a[0]);
                igual("rol autorizado", "ABM_COMPRAS", a[1]);
                return model(Role.class, new Object[][] {{"getRoleId", Long.valueOf(500)}, {"getName", "ABM_COMPRAS"}});
            }
            if ("getResource".equals(name)) {
                individual(a, 0);
                return model(Resource.class, new Object[][] {{"getResourceId", Long.valueOf(600)}});
            }
            if ("getRolePermissions".equals(name)) {
                igual("rol permiso", Long.valueOf(500), a[0]);
                igual("recurso permiso", Long.valueOf(600), a[1]);
                List<Permission> result = new ArrayList<Permission>();
                for (int i = 0; i < allowed.size(); i++) {
                    result.add((Permission) model(Permission.class, new Object[][] {{"getActionId", allowed.get(i)}}));
                }
                return result;
            }
            if ("setRolePermissions".equals(name)) {
                igual("rol escritura", Long.valueOf(500), a[0]);
                igual("recurso escritura", Long.valueOf(600), a[2]);
                grant((String[]) a[1]);
                return null;
            }
            if ("getAvailableResourcePermissionActionIds".equals(name)) {
                individual(a, 0);
                igual("rol algoritmo6", Long.valueOf(500), a[4]);
                List<String> result = new ArrayList<String>();
                List<String> requested = (List<String>) a[5];
                for (int i = 0; i < requested.size(); i++) {
                    if (allowed.contains(requested.get(i))) {
                        result.add(requested.get(i));
                    }
                }
                return result;
            }
            if ("getResourceActions".equals(name)) {
                igual("acciones del modelo", RESOURCE, a[0]);
                List<ResourceAction> result = new ArrayList<ResourceAction>();
                result.add(resourceAction("VIEW", 1L));
                result.add(resourceAction("UPDATE", 2L));
                result.add(resourceAction("DELETE", 4L));
                return result;
            }
            if ("setResourcePermissions".equals(name)) {
                individual(a, 0);
                igual("rol escritura6", Long.valueOf(500), a[4]);
                grant((String[]) a[5]);
                return null;
            }
            return fallback(p, m, a);
        }

        private void individual(Object[] a, int offset) {
            igual("empresa individual", Long.valueOf(900), a[offset]);
            igual("modelo individual", RESOURCE, a[offset + 1]);
            igual("ambito individual", Integer.valueOf(4), a[offset + 2]);
            igual("primKey individual", "300", a[offset + 3]);
        }

        private void grant(String[] actions) {
            allowed.clear();
            allowed.addAll(Arrays.asList(actions));
            writes++;
        }

        private ResourceAction resourceAction(String action, long bit) {
            return (ResourceAction) model(ResourceAction.class, new Object[][] {
                {"getActionId", action}, {"getBitwiseValue", Long.valueOf(bit)}
            });
        }
    }
}