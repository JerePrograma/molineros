import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ar.com.ospim.compras.requerimientos.action.BuscarRequerimientosComprasAction;
import ar.com.ospim.compras.requerimientos.action.CambiarEstadoRequerimientoCompraAction;
import ar.com.ospim.compras.requerimientos.action.EditarRequerimientoCompraAction;
import ar.com.ospim.compras.requerimientos.action.VerRequerimientoCompraAction;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;

/* Referencias: ComprasConfiguracionTest.usuarioConRoles y su fixture de datos;
 * ComprasOrdenMedicaPermisoTest.asegurar para invocar reglas productivas.
 * Solo la frontera ServiceImpl recibe datos simulados. No processAction,
 * persistencia, red, Tomcat ni UI; no demuestra permisos efectivos del portal. */
public class ComprasRolesPermisosTest {
    private static final String[] CODIGOS = {"VIEW", "ABM", "ANULAR", "COTIZAR", "AUTORIZAR"};
    private static final String[] ROLES = {"VIEW_COMPRAS", "ABM_COMPRAS", "ANULAR_COMPRAS",
            "COTIZAR_COMPRAS", "AUTORIZAR_COMPRAS"};
    private static final String[] ALIAS = {"cOmPrAs_ViEw", "cOmPrAs_AbM", "cOmPrAs_AnUlAr",
            "cOmPrAs_CoTiZaR", "cOmPrAs_AuToRiZaR"};
    private static final int CONSULTAR = 31;
    private static final int ABM = 2;
    private static final int COTIZAR = 8;
    private static final int ANULAR = 6;
    private static final int AUTORIZAR = 16;
    private static final int EXPORTAR = 27;
    private static int checks;
    private static int scenarios;

    private static class Guardia {
        private final Object objeto;
        private final Method metodo;
        private final int roles;
        private final String nombre;
        private Guardia(Object objeto, Class clase, String metodo, int roles) throws Exception {
            this.objeto = objeto;
            this.metodo = clase.getDeclaredMethod(metodo, new Class[] {User.class});
            this.metodo.setAccessible(true);
            this.roles = roles;
            this.nombre = clase.getSimpleName() + "." + metodo;
        }
        private Object invocar(User user) throws Exception {
            try { return metodo.invoke(objeto, new Object[] {user}); }
            catch (InvocationTargetException error) {
                Throwable causa = error.getCause();
                if (causa instanceof Exception) throw (Exception) causa;
                if (causa instanceof Error) throw (Error) causa;
                throw error;
            }
        }
    }

    private static Guardia[] guardias(boolean incluirExportar) throws Exception {
        BuscarRequerimientosComprasAction buscar = new BuscarRequerimientosComprasAction();
        VerRequerimientoCompraAction ver = new VerRequerimientoCompraAction();
        EditarRequerimientoCompraAction editar = new EditarRequerimientoCompraAction();
        CambiarEstadoRequerimientoCompraAction cambiar = new CambiarEstadoRequerimientoCompraAction();
        List<Guardia> lista = new ArrayList<Guardia>();
        lista.add(new Guardia(buscar, buscar.getClass(), "validarPermisoView", CONSULTAR));
        lista.add(new Guardia(ver, ver.getClass(), "validarPermisoView", CONSULTAR));
        lista.add(new Guardia(editar, editar.getClass(), "validarPermisoConsulta", CONSULTAR));
        lista.add(new Guardia(editar, editar.getClass(), "validarPermisoABM", ABM));
        lista.add(new Guardia(editar, editar.getClass(), "validarPermisoCotizar", COTIZAR));
        lista.add(new Guardia(editar, editar.getClass(), "validarPermisoAnular", ANULAR));
        lista.add(new Guardia(editar, editar.getClass(), "validarPermisoObservacionInterna", AUTORIZAR));
        lista.add(new Guardia(cambiar, cambiar.getClass(), "validarRolCotizar", COTIZAR));
        lista.add(new Guardia(cambiar, cambiar.getClass(), "validarRolAutorizar", AUTORIZAR));
        lista.add(new Guardia(cambiar, cambiar.getClass(), "validarRolAnular", ANULAR));
        lista.add(new Guardia(null, ExportarRequerimientosCompraHelper.class, "puedeConsultar", CONSULTAR));
        if (incluirExportar) {
            lista.add(new Guardia(null, ExportarRequerimientosCompraHelper.class, "puedeExportar", EXPORTAR));
        }
        lista.add(new Guardia(null, ExportarRequerimientosCompraHelper.class, "validarPermiso", EXPORTAR));
        return lista.toArray(new Guardia[lista.size()]);
    }

    private static User usuarioConRoles(String[] nombres) {
        final List<Role> roles = nombres == null ? null : new ArrayList<Role>();
        for (int i = 0; nombres != null && i < nombres.length; i++) {
            if (nombres[i] == null) { roles.add(null); continue; }
            Role rol = new RoleImpl();
            rol.setName(nombres[i]);
            roles.add(rol);
        }
        return new UserImpl() { public List<Role> getRoles() { return roles; } };
    }

    private static void comprobar(Guardia guardia, User usuario, boolean esperado, String caso) throws Exception {
        Object resultado = null;
        Exception rechazo = null;
        try { resultado = guardia.invocar(usuario); }
        catch (Exception error) { rechazo = error; }
        if (rechazo != null) {
            String mensaje = rechazo.getMessage();
            boolean denegacion = mensaje != null && (mensaje.indexOf("No posee permisos") >= 0
                    || mensaje.indexOf("No se pudo determinar el usuario actual") >= 0);
            if (esperado || !denegacion) {
                AssertionError fallo = new AssertionError(caso + " " + guardia.nombre + ": " + rechazo);
                fallo.initCause(rechazo);
                throw fallo;
            }
        } else {
            boolean permitido = resultado instanceof Boolean ? ((Boolean) resultado).booleanValue() : true;
            if (permitido != esperado) {
                throw new AssertionError(caso + " " + guardia.nombre + " esperado=" + esperado + " actual=" + permitido);
            }
        }
        checks++;
    }

    private static void matriz(Guardia[] guardias, String[] nombres, String prefijo) throws Exception {
        for (int rol = 0; rol < nombres.length; rol++) {
            User user = usuarioConRoles(new String[] {nombres[rol]});
            for (int g = 0; g < guardias.length; g++) {
                comprobar(guardias[g], user, (guardias[g].roles & (1 << rol)) != 0, prefijo + " " + CODIGOS[rol]);
            }
            scenarios++;
        }
        System.out.println("PASS matriz " + prefijo);
    }

    private static void configurar(ComprasFuenteDatosFixture datos, String[] nombres) {
        StringBuffer valor = new StringBuffer();
        for (int i = 0; i < CODIGOS.length; i++) {
            if (i > 0) valor.append(';');
            valor.append(CODIGOS[i]).append('=').append(nombres[i]);
        }
        datos.configuracion.put("COMPRAS_ROLES", valor.toString());
    }

    private static void reproducir(Guardia[] guardias) throws Exception {
        User anular = usuarioConRoles(new String[] {ROLES[2]});
        User abm = usuarioConRoles(new String[] {ROLES[1]});
        for (int g = 0; g < guardias.length; g++) {
            if (guardias[g].roles == CONSULTAR || guardias[g].roles == ANULAR) {
                comprobar(guardias[g], anular, false, "REPRO ANULAR rechazado");
                comprobar(guardias[g], abm, true, "REPRO ABM preservado");
                System.out.println("REPRO " + guardias[g].nombre + " ANULAR=false ABM=true");
                scenarios++;
            }
        }
        for (int rol = 0; rol < ROLES.length; rol++) {
            if (rol == 1 || rol == 2) continue;
            for (int g = 0; g < guardias.length; g++) {
                comprobar(guardias[g], usuarioConRoles(new String[] {ROLES[rol]}),
                        (guardias[g].roles & (1 << rol)) != 0, "REPRO controles " + CODIGOS[rol]);
            }
            scenarios++;
        }
        System.out.println("PASS controles VIEW, COTIZAR y AUTORIZAR con capacidades actuales");
        System.out.println("COMPRAS_ROLES_PERMISOS_REPRO_OK scenarios=" + scenarios + " checks=" + checks);
    }

    private static void negativos(Guardia[] guardias) throws Exception {
        User[] usuarios = {null, usuarioConRoles(new String[0]), usuarioConRoles(null),
                usuarioConRoles(new String[] {null}), usuarioConRoles(new String[] {"ROL_AJENO"})};
        for (int u = 0; u < usuarios.length; u++) {
            for (int g = 0; g < guardias.length; g++) comprobar(guardias[g], usuarios[u], false, "negativo " + u);
            scenarios++;
        }
        User combinado = usuarioConRoles(new String[] {ROLES[1], ROLES[2]});
        for (int g = 0; g < guardias.length; g++) {
            comprobar(guardias[g], combinado, (guardias[g].roles & (ABM | 4)) != 0, "ABM mas ANULAR");
        }
        scenarios++;
        System.out.println("PASS negativos y ABM mas ANULAR");
    }

    private static void configuracionEnCaliente(Guardia[] guardias, ComprasFuenteDatosFixture datos) throws Exception {
        String[] personalizados = {"Consulta_Personal", "Administracion_Personal", "Baja_Personal",
                "Cotizacion_Personal", "Autorizacion_Personal"};
        configurar(datos, personalizados);
        matriz(guardias, personalizados, "configuracion personalizada");
        for (int g = 0; g < guardias.length; g++) {
            for (int rol = 0; rol < ROLES.length; rol++) {
                comprobar(guardias[g], usuarioConRoles(new String[] {ROLES[rol]}), false, "rol anterior retirado " + CODIGOS[rol]);
            }
        }
        configurar(datos, ROLES);
        matriz(guardias, ROLES, "configuracion restaurada sin reinicio");
        System.out.println("PASS roles configurados cambian sin reiniciar");
    }

    private static void configuracionFallida(Guardia[] guardias, ComprasFuenteDatosFixture datos) throws Exception {
        datos.fallarLectura = true;
        try {
            User usuario = usuarioConRoles(new String[] {ROLES[2]});
            for (int g = 0; g < guardias.length; g++) {
                try {
                    guardias[g].invocar(usuario);
                    throw new AssertionError("Fallo de configuracion no propagado: " + guardias[g].nombre);
                } catch (IllegalStateException esperado) {
                    if (esperado.getCause() == null || esperado.getMessage().indexOf("COMPRAS_ROLES") < 0) {
                        throw new AssertionError("Fallo de configuracion sin clave o causa: " + guardias[g].nombre);
                    }
                    checks++;
                }
            }
            scenarios++;
        } finally { datos.fallarLectura = false; }
        System.out.println("PASS fallo configuracion conserva causa sin permitir acceso");
    }

    public static void main(String[] args) throws Exception {
        ComprasFuenteDatosFixture datos = new ComprasFuenteDatosFixture();
        datos.cargarConfiguracionPrueba();
        datos.instalar();
        configurar(datos, ROLES);
        boolean repro = args.length > 0 && "repro".equals(args[0]);
        Guardia[] guardias = guardias(!repro);
        if (repro) { reproducir(guardias); return; }
        matriz(guardias, ROLES, "roles individuales");
        matriz(guardias, ALIAS, "aliases legacy y mayusculas");
        negativos(guardias);
        configuracionEnCaliente(guardias, datos);
        configuracionFallida(guardias, datos);
        System.out.println("COMPRAS_ROLES_PERMISOS_OFFLINE_OK scenarios=" + scenarios + " checks=" + checks);
        System.out.println("Validadores Actions, PermissionUtil y helper productivos; sin DB, red, Tomcat ni UI.");
    }
}
