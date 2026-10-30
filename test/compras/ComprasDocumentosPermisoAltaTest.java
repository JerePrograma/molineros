import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
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
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalService;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Adaptacion de ComprasOrdenMedicaPermisoTest y ComprasComparativaSincronizacionTest.
 * Referencia de contrato: Autorizaciones/DescargarDocumentoCompraReclamoAction,
 * identidad DL y check VIEW. Cuatro creadores reales con servicios sustituidos;
 * PDF sintetico sin datos personales. No DB, red, servidor ni permisos efectivos. */
public class ComprasDocumentosPermisoAltaTest {
    private static final String RESOURCE = DLFileEntry.class.getName();
    private static final long COMPANY = 900L;
    private static final long GROUP = 10136L;
    private static final long USER = 400L;
    private static final long FOLDER = 200L;
    private static final long FILE = 300L;
    private static final long ROLE = 500L;
    private static final long RES = 600L;
    private static final String[] CASES = {"orden_medica", "presupuesto_tipo1",
            "presupuesto_tipo3", "pedido_pdf", "comparativa_pdf"};
    private static int checks;
    private static int scenarios;
    private static File fixturePdf;
    private static byte[] pdf;

    public static void main(String[] args) throws Exception {
        ConfiguracionComprasDatosFixture.instalar();
        int original = PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM;
        ClassLoader previousLoader = PortalClassLoaderUtil.getClassLoader();
        PortalClassLoaderUtil.setClassLoader(ComprasDocumentosPermisoAltaTest.class.getClassLoader());
        File folder = new File(args[0]);
        fixturePdf = File.createTempFile("compras-permiso-fixture-", ".pdf", folder);
        try {
            pdf = generarPdf();
            FileOutputStream output = new FileOutputStream(fixturePdf);
            try { output.write(pdf); } finally { output.close(); }
            for (int algorithm = 5; algorithm <= 6; algorithm++) {
                PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM = algorithm;
                for (int type = 0; type < CASES.length; type++) {
                    alta(type, algorithm);
                }
                helperIdempotente(false);
                helperIdempotente(true);
                comparativaConViewPrevia();
            }
            PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM = 5;
            for (int type = 0; type < CASES.length; type++) {
                fallo(type, "grant");
                fallo(type, "add");
                fallo(type, "delete");
            }
            System.out.println("COMPRAS_DOCUMENTOS_PERMISO_ALTA_BASE scenarios=" + scenarios + " checks=" + checks);
            String rolesOriginales = ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra("COMPRAS_ROLES");
            try {
                for (int algorithm = 5; algorithm <= 6; algorithm++) {
                    PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM = algorithm;
                    for (String rolConfigurado : new String[] {"abm_compras", "ABM_ComPrAs"}) {
                        rolConfiguradoConDistintaCaja(rolConfigurado, 0);
                        rolConfiguradoConDistintaCaja(rolConfigurado, 4);
                    }
                }
            } finally {
                ConfiguracionComprasDatosFixture.configurar("COMPRAS_ROLES", rolesOriginales);
            }
        } finally {
            PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM = original;
            PortalClassLoaderUtil.setClassLoader(previousLoader);
            if (fixturePdf.exists() && !fixturePdf.delete()) {
                throw new Exception("No se pudo retirar el PDF sintetico de prueba.");
            }
        }
        System.out.println("COMPRAS_DOCUMENTOS_PERMISO_ALTA_OFFLINE_OK scenarios="
                + scenarios + " checks=" + checks);
        System.out.println("Creadores y check DL productivos; servicios simulados; sin DB, red ni UI.");
    }

    private static void rolConfiguradoConDistintaCaja(String configurado, int type) throws Exception {
        ConfiguracionComprasDatosFixture.configurar("COMPRAS_ROLES",
                "VIEW=VIEW_COMPRAS;ABM=" + configurado + ";ANULAR=ANULAR_COMPRAS;COTIZAR=COTIZAR_COMPRAS;AUTORIZAR=AUTORIZAR_COMPRAS");
        Fixture f = new Fixture();
        f.roleName = "ABM_Compras";
        f.expectUpdate = type == 4;
        f.install();
        try {
            crear(type, f);
            verificar(f.allowed.contains("VIEW"), "El fallback no agrego VIEW al rol configurado con otra caja.");
            verificar(f.roleFallbacks > 0, "No se ejercito el fallback getRoles de RoleService.");
            verificar(type != 4 || f.allowed.contains("UPDATE"), "Comparativa sin UPDATE en rol configurado.");
            f.preservados();
            scenarios++;
        } finally { f.restore(); }
    }

    private static void alta(int type, int algorithm) throws Exception {
        Fixture f = new Fixture();
        f.expectUpdate = type == 4;
        f.install();
        try {
            Object result = crear(type, f);
            verificar(result != null, "El creador no devolvio documento: " + CASES[type]);
            Method getter = result.getClass().getDeclaredMethod("getFileEntryId", new Class[0]);
            getter.setAccessible(true);
            igual("identidad del resultado", Long.valueOf(FILE), getter.invoke(result, new Object[0]));
            igual("una alta DL", Integer.valueOf(1), Integer.valueOf(f.adds));
            igual("un grant", Integer.valueOf(1), Integer.valueOf(f.writes));
            igual("sin compensacion en exito", Integer.valueOf(0), Integer.valueOf(f.deletes));
            verificar(f.allowed.contains("VIEW"), "Falta VIEW " + CASES[type]);
            igual("UPDATE exclusivo comparativa", Boolean.valueOf(type == 4),
                    Boolean.valueOf(f.allowed.contains("UPDATE")));
            checkDl(f, "VIEW", true);
            checkDl(f, "UPDATE", type == 4);
            f.preservados();
            asegurar(f.entry, type == 4);
            igual("grant idempotente", Integer.valueOf(1), Integer.valueOf(f.writes));
            aprobado(CASES[type] + " algoritmo=" + algorithm);
        } finally { f.restore(); }
    }

    private static void helperIdempotente(boolean comparison) throws Exception {
        Fixture f = new Fixture();
        f.expectUpdate = comparison;
        f.allowed.add("UPDATE");
        f.install();
        try {
            asegurar(f.entry, comparison);
            verificar(f.allowed.contains("UPDATE"), "Se retiro UPDATE previo.");
            verificar(f.allowed.contains("DELETE_DISCUSSION"), "Se retiro una accion previa.");
            igual("union inicial", Integer.valueOf(1), Integer.valueOf(f.writes));
            asegurar(f.entry, comparison);
            igual("sin segunda escritura", Integer.valueOf(1), Integer.valueOf(f.writes));
            f.preservados();
            aprobado("helper preserva acciones e idempotencia update=" + comparison);
        } finally { f.restore(); }
    }

    private static void comparativaConViewPrevia() throws Exception {
        Fixture f = new Fixture();
        f.expectUpdate = true;
        f.allowed.add("VIEW");
        f.install();
        try {
            asegurar(f.entry, true);
            verificar(f.allowed.contains("VIEW") && f.allowed.contains("UPDATE"),
                    "VIEW previo impidio agregar UPDATE a comparativa.");
            igual("una escritura para UPDATE faltante", Integer.valueOf(1), Integer.valueOf(f.writes));
            f.preservados();
            aprobado("comparativa VIEW previo agrega solo UPDATE faltante");
        } finally { f.restore(); }
    }

    private static void fallo(int type, String mode) throws Exception {
        Fixture f = new Fixture();
        f.expectUpdate = type == 4;
        f.failAdd = "add".equals(mode);
        f.failGrant = !f.failAdd;
        f.failDelete = "delete".equals(mode);
        f.install();
        try {
            try {
                crear(type, f);
                throw new AssertionError("Exito falso: " + CASES[type] + " " + mode);
            } catch (Exception expected) {
                verificar(contieneCausa(expected, f.failAdd ? "ADD_SIMULADO" : "GRANT_SIMULADO"),
                        "Se perdio el error original: " + CASES[type] + " " + mode);
            }
            igual("una llamada add", Integer.valueOf(1), Integer.valueOf(f.adds));
            igual("compensar solo entrada creada", Integer.valueOf(f.failAdd ? 0 : 1),
                    Integer.valueOf(f.deletes));
            igual("grant no se intenta sin alta", Integer.valueOf(f.failAdd ? 0 : 1),
                    Integer.valueOf(f.grantAttempts));
            igual("sin grant exitoso", Integer.valueOf(0), Integer.valueOf(f.writes));
            igual("compensacion efectiva", Boolean.valueOf(!f.failAdd && !f.failDelete),
                    Boolean.valueOf(f.deleted));
            igual("sin exito parcial de permiso", Arrays.asList(new String[] {"DELETE_DISCUSSION"}), f.allowed);
            f.preservados();
            aprobado(CASES[type] + " fallo=" + mode);
        } finally { f.restore(); }
    }

    private static Object crear(int type, Fixture f) throws Exception {
        if (type == 0) {
            Helper helper = new Helper(f.context);
            return helper.crearOrdenMedica(73, new OrdenMedicaValidada(fixturePdf,
                    "fixture.pdf", ".pdf", "application/pdf", Date.valueOf("2026-10-28")));
        }
        if (type == 3) {
            return DocumentoLibraryComprasHelper.crearPedidoCotizacion(73, 84,
                    pdf, "fixture.pdf", f.context);
        }
        if (type == 4) {
            return DocumentoLibraryComprasHelper.crearComparativaReclamoPrestacional(
                    73, 95, pdf, f.context);
        }
        Class validated = Class.forName(PresupuestoCompraHelper.class.getName() + "$PresupuestoValidado");
        Constructor constructor = validated.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object input = constructor.newInstance(new Object[] {fixturePdf, "fixture.pdf",
                Integer.valueOf(type == 1 ? 1 : 3), null, null,
                "PRESUPUESTO-FIXTURE.pdf", "Presupuesto fixture", "Documento sintetico"});
        Method method = PresupuestoCompraHelper.class.getDeclaredMethod("crearArchivoPresupuesto",
                new Class[] {Long.TYPE, Long.TYPE, validated, ServiceContext.class});
        return invocar(method, new PresupuestoCompraHelper(),
                new Object[] {Long.valueOf(USER), Long.valueOf(FOLDER), input, f.context});
    }

    private static void asegurar(DLFileEntry entry, boolean comparison) throws Exception {
        if (!comparison) {
            DocumentoLibraryComprasHelper.asegurarPermisoVistaAbm(entry);
            return;
        }
        Method method = DocumentoLibraryComprasHelper.class.getDeclaredMethod("asegurarPermisosAbm",
                new Class[] {DLFileEntry.class, Boolean.TYPE});
        invocar(method, null, new Object[] {entry, Boolean.TRUE});
    }

    private static Object invocar(Method method, Object target, Object[] args) throws Exception {
        method.setAccessible(true);
        try { return method.invoke(target, args); }
        catch (InvocationTargetException error) {
            Throwable cause = error.getCause();
            if (cause instanceof Exception) throw (Exception) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw error;
        }
    }

    private static void checkDl(Fixture f, String action, boolean expected) throws Exception {
        try {
            DLFileEntryPermission.check(f.checker, FOLDER, f.name, action);
            verificar(expected, "DL permitio accion no concedida: " + action);
        } catch (PrincipalException error) {
            verificar(!expected, "DL rechazo accion concedida: " + action);
        }
    }

    private static byte[] generarPdf() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("Fixture offline de permisos Compras, sin datos personales."));
        document.close();
        byte[] result = output.toByteArray();
        verificar(result.length > 5 && result[0] == '%' && result[1] == 'P'
                && result[2] == 'D' && result[3] == 'F' && result[4] == '-', "PDF sintetico invalido.");
        return result;
    }

    private static boolean contieneCausa(Throwable error, String marker) {
        for (Throwable current = error; current != null; current = current.getCause()) {
            if (current.getMessage() != null && current.getMessage().indexOf(marker) >= 0) return true;
        }
        return false;
    }

    private static void igual(String label, Object expected, Object actual) {
        verificar(expected == null ? actual == null : expected.equals(actual),
                label + ": esperado=" + expected + " actual=" + actual);
    }

    private static void verificar(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }

    private static void aprobado(String label) {
        scenarios++;
        System.out.println("OK " + label);
    }

    private static Object proxy(Class type, InvocationHandler handler) {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, handler);
    }

    private static Object model(Class type, final Object[][] values) {
        return proxy(type, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                for (int i = 0; i < values.length; i++) {
                    if (values[i][0].equals(m.getName())) return values[i][1];
                }
                return fallback(p, m, a);
            }
        });
    }

    private static Object fallback(Object p, Method m, Object[] a) {
        if ("toString".equals(m.getName())) return "fixture:" + p.getClass().getInterfaces()[0].getName();
        if ("hashCode".equals(m.getName())) return Integer.valueOf(System.identityHashCode(p));
        if ("equals".equals(m.getName())) return Boolean.valueOf(p == a[0]);
        throw new UnsupportedOperationException(m.toString());
    }

    private static final class Helper extends DocumentoLibraryComprasHelper {
        private Helper(ServiceContext context) throws Exception { super(context); }
    }

    private static final class Fixture implements InvocationHandler {
        private final List<String> allowed = new ArrayList<String>(Arrays.asList(new String[] {"DELETE_DISCUSSION"}));
        private final List<String> owner = new ArrayList<String>(Arrays.asList(new String[] {"VIEW", "UPDATE", "DELETE", "PERMISSIONS"}));
        private final List<String> guest = new ArrayList<String>();
        private final List<String> other = new ArrayList<String>(Arrays.asList(new String[] {"VIEW"}));
        private final List<Field> serviceFields = new ArrayList<Field>();
        private final List<Object> previousServices = new ArrayList<Object>();
        private final ServiceContext context = new ServiceContext();
        private String roleName = "ABM_COMPRAS";
        private int roleFallbacks;
        private boolean expectUpdate;
        private boolean failAdd;
        private boolean failGrant;
        private boolean failDelete;
        private boolean deleted;
        private int adds;
        private int deletes;
        private int grantAttempts;
        private int writes;
        private String name = "fixture.pdf";
        private String title = "Fixture";
        private final DLFileEntry entry;
        private final DLFolder folder;
        private final PermissionChecker checker;

        private Fixture() {
            context.setCompanyId(COMPANY);
            context.setScopeGroupId(GROUP);
            context.setUserId(USER);
            entry = (DLFileEntry) proxy(DLFileEntry.class, this);
            folder = (DLFolder) model(DLFolder.class, new Object[][] {
                {"getFolderId", Long.valueOf(FOLDER)}, {"getGroupId", Long.valueOf(GROUP)},
                {"getCompanyId", Long.valueOf(COMPANY)}, {"getName", "Compras"}
            });
            checker = (PermissionChecker) proxy(PermissionChecker.class, this);
        }

        private void install() throws Exception {
            install(DLFileEntryLocalServiceUtil.class, DLFileEntryLocalService.class);
            install(DLFolderLocalServiceUtil.class, DLFolderLocalService.class);
            install(RoleLocalServiceUtil.class, RoleLocalService.class);
            install(ResourceLocalServiceUtil.class, ResourceLocalService.class);
            install(PermissionLocalServiceUtil.class, PermissionLocalService.class);
            install(ResourcePermissionLocalServiceUtil.class, ResourcePermissionLocalService.class);
            install(ResourceActionLocalServiceUtil.class, ResourceActionLocalService.class);
        }

        private void install(Class util, Class service) throws Exception {
            Field field = util.getDeclaredField("_service");
            field.setAccessible(true);
            serviceFields.add(field);
            previousServices.add(field.get(null));
            field.set(null, proxy(service, this));
        }

        private void restore() throws Exception {
            for (int i = serviceFields.size() - 1; i >= 0; i--) {
                serviceFields.get(i).set(null, previousServices.get(i));
            }
        }

        private void preservados() {
            igual("Owner intacto", Arrays.asList(new String[] {"VIEW", "UPDATE", "DELETE", "PERMISSIONS"}), owner);
            igual("Guest sin acceso", Integer.valueOf(0), Integer.valueOf(guest.size()));
            igual("otro rol intacto", Arrays.asList(new String[] {"VIEW"}), other);
        }

        public Object invoke(Object p, Method m, Object[] a) throws Throwable {
            String method = m.getName();
            if (p == entry) {
                if ("getFileEntryId".equals(method) || "getPrimaryKey".equals(method)) return Long.valueOf(FILE);
                if ("getCompanyId".equals(method)) return Long.valueOf(COMPANY);
                if ("getGroupId".equals(method)) return Long.valueOf(GROUP);
                if ("getFolderId".equals(method)) return Long.valueOf(FOLDER);
                if ("getUserId".equals(method)) return Long.valueOf(USER);
                if ("getName".equals(method)) return name;
                if ("getTitle".equals(method)) return title;
                if ("getUuid".equals(method)) return "uuid-permiso-fixture";
                return fallback(p, m, a);
            }
            if ("addFileEntry".equals(method)) {
                adds++;
                igual("usuario del alta", Long.valueOf(USER), a[0]);
                igual("carpeta del alta", Long.valueOf(FOLDER), a[1]);
                verificar(a[6] instanceof File && ((File) a[6]).length() > 0L, "Alta sin PDF de fixture.");
                verificar(a[7] == context, "Se cambio ServiceContext.");
                verificar(!context.getAddCommunityPermissions() && !context.getAddGuestPermissions(),
                        "Se abrio el alta a comunidad o Guest.");
                if (failAdd) throw new SystemException("ADD_SIMULADO");
                name = (String) a[2];
                title = (String) a[3];
                return entry;
            }
            if ("deleteFileEntry".equals(method)) {
                deletes++;
                verificar(a.length == 1 && a[0] == entry, "Se intento compensar otra entrada.");
                if (failDelete) throw new SystemException("DELETE_SIMULADO");
                deleted = true;
                return null;
            }
            if ("getFolder".equals(method)) {
                igual("grupo de carpeta", Long.valueOf(GROUP), a[0]);
                return folder;
            }
            if ("getDLFileEntry".equals(method)) {
                igual("identidad antes de compensar", Long.valueOf(FILE), a[0]);
                return entry;
            }
            if ("getFileEntry".equals(method)) {
                igual("carpeta del check", Long.valueOf(FOLDER), a[0]);
                igual("nombre del check", name, a[1]);
                return entry;
            }
            if ("hasOwnerPermission".equals(method)) return Boolean.FALSE;
            if ("hasPermission".equals(method)) {
                igual("grupo DL", Long.valueOf(GROUP), a[0]);
                igual("clase DL", RESOURCE, a[1]);
                igual("archivo DL", Long.valueOf(FILE), a[2]);
                return Boolean.valueOf(allowed.contains(a[3]));
            }
            if ("getRole".equals(method)) {
                igual("empresa rol", Long.valueOf(COMPANY), a[0]);
                igual("rol unico configurado", ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getRolAbmCompras(), a[1]);
                if (!roleName.equals(a[1])) throw new com.liferay.portal.NoSuchRoleException("ROL_CON_OTRA_CAJA");
                return model(Role.class, new Object[][] {{"getRoleId", Long.valueOf(ROLE)}, {"getName", roleName}});
            }
            if ("getRoles".equals(method)) {
                igual("empresa catalogo de roles", Long.valueOf(COMPANY), a[0]);
                roleFallbacks++;
                List<Role> roles = new ArrayList<Role>();
                roles.add((Role) model(Role.class, new Object[][] {{"getRoleId", Long.valueOf(ROLE + 1)}, {"getName", "ROL_AJENO"}}));
                roles.add((Role) model(Role.class, new Object[][] {{"getRoleId", Long.valueOf(ROLE)}, {"getName", roleName}}));
                return roles;
            }
            if ("getResource".equals(method)) {
                individual(a);
                return model(Resource.class, new Object[][] {{"getResourceId", Long.valueOf(RES)}});
            }
            if ("getRolePermissions".equals(method)) {
                igual("rol lectura", Long.valueOf(ROLE), a[0]);
                igual("recurso lectura", Long.valueOf(RES), a[1]);
                List<Permission> result = new ArrayList<Permission>();
                for (int i = 0; i < allowed.size(); i++) {
                    result.add((Permission) model(Permission.class, new Object[][] {{"getActionId", allowed.get(i)}}));
                }
                return result;
            }
            if ("setRolePermissions".equals(method)) {
                igual("rol escritura", Long.valueOf(ROLE), a[0]);
                igual("recurso escritura", Long.valueOf(RES), a[2]);
                grant((String[]) a[1]);
                return null;
            }
            if ("getResourceActions".equals(method)) {
                igual("clase de acciones", RESOURCE, a[0]);
                List<ResourceAction> result = new ArrayList<ResourceAction>();
                String[] actions = {"VIEW", "UPDATE", "DELETE", "DELETE_DISCUSSION", "PERMISSIONS"};
                for (int i = 0; i < actions.length; i++) {
                    result.add((ResourceAction) model(ResourceAction.class, new Object[][] {
                        {"getActionId", actions[i]}, {"getBitwiseValue", Long.valueOf(1L << i)}
                    }));
                }
                return result;
            }
            if ("getAvailableResourcePermissionActionIds".equals(method)) {
                individual(a);
                igual("rol lectura6", Long.valueOf(ROLE), a[4]);
                return new ArrayList<String>(allowed);
            }
            if ("setResourcePermissions".equals(method)) {
                individual(a);
                igual("rol escritura6", Long.valueOf(ROLE), a[4]);
                grant((String[]) a[5]);
                return null;
            }
            return fallback(p, m, a);
        }

        private void individual(Object[] a) {
            igual("empresa individual", Long.valueOf(COMPANY), a[0]);
            igual("modelo individual", RESOURCE, a[1]);
            igual("scope individual", Integer.valueOf(4), a[2]);
            igual("primKey individual", String.valueOf(FILE), a[3]);
        }

        private void grant(String[] actions) throws Exception {
            grantAttempts++;
            List<String> updated = Arrays.asList(actions);
            List<String> expected = new ArrayList<String>(allowed);
            if (!expected.contains("VIEW")) expected.add("VIEW");
            if (expectUpdate && !expected.contains("UPDATE")) expected.add("UPDATE");
            verificar(updated.size() == expected.size() && updated.containsAll(expected),
                    "Se concedieron acciones distintas de la union autorizada.");
            verificar(updated.containsAll(allowed), "Se retiraron acciones previas ABM.");
            verificar(updated.contains("VIEW"), "No se agrego VIEW.");
            if (!expectUpdate && !allowed.contains("UPDATE")) {
                verificar(!updated.contains("UPDATE"), "UPDATE concedido a otro tipo de documento.");
            }
            if (expectUpdate) verificar(updated.contains("UPDATE"), "Comparativa sin UPDATE.");
            verificar(!updated.contains("DELETE") && !updated.contains("PERMISSIONS"), "Se agregaron acciones no autorizadas.");
            if (failGrant) throw new SystemException("GRANT_SIMULADO");
            allowed.clear();
            allowed.addAll(updated);
            writes++;
        }
    }
}