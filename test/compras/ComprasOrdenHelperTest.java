import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraOrdenAction;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Ejecuta el Helper productivo. Las consultas y la persistencia se sustituyen
 * mediante sus fronteras protected; no requiere base, Tomcat ni sesion UI. */
public class ComprasOrdenHelperTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        empresa();
        monedaFija();
        observacionesEmpresa();
        entradaFormulario();
        formularioSoloPrecios();
        repararPrecios();
        importes();
        estadoYAdjudicacion();
        medicina();
        edicionCondiciones();
        permisosEdicion();
        flujoAction();
        System.out.println("COMPRAS_ORDEN_HELPER_OFFLINE_OK checks=" + checks);
        System.out.println("Helper productivo; generacion directa con fuentes canonicas; sin DB ni UI.");
    }

    private static void empresa() throws Exception {
        Mock h = empresaFixture();
        h.detalles.get(0).setObservaciones("Comentario tecnico del articulo");
        RequerimientoCompraOrden resultado = h.crearDirecta(40, "Nombre de Compras", "auditoria");
        igual("proveedor canonico", "Empresa de prueba", resultado.getProveedor());
        igual("CUIT canonico vacio", "", resultado.getProveedorCuit());
        igual("cantidad fuente", Integer.valueOf(2), resultado.getDetalles().get(0).getCantidad());
        decimal("precio fuente", "100", resultado.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("subtotal fuente", "200", resultado.getDetalles().get(0).getPrecioTotalEstimado());
        igual("moneda fija en pesos", "ARS", resultado.getMoneda());
        decimal("IVA ausente es cero", "0", resultado.getIva());
        decimal("IIBB ausente es cero", "0", resultado.getIibb());
        decimal("total conocido sin impuestos informados", "200", resultado.getTotal());
        igual("precios completos no requieren formulario", Boolean.FALSE,
                Boolean.valueOf(h.requiereCompletarDatos(resultado)));
        igual("pago no inventado", null, resultado.getFormaPago());
        igual("entrega no inventada", null, resultado.getEntrega());
        igual("contacto no inventado", null, resultado.getProveedorContacto());
        igual("fecha recepcion no inferida", null, resultado.getFechaRecepcion());
        igual("actor autenticado", "auditoria", resultado.getConfeccionadoPor());
        igual("solicitante fuente", "solicitante", resultado.getSolicitante());
        decimal("precio original preservado", "100", h.detalles.get(0).getPrecioUnitarioEstimado());
        igual("comentario tecnico en snapshot", "Equipo de prueba\nComentario tecnico del articulo",
                resultado.getDetalles().get(0).getDescripcionItemVisible());
        igual("comentario sin duplicar", "", resultado.getDetalles().get(0).getObservacionesVisible());
        igual("descripcion original preservada", "Equipo de prueba", h.detalles.get(0).getDescripcionItemVisible());
        igual("comentario original preservado", "Comentario tecnico del articulo", h.detalles.get(0).getObservacionesVisible());
        igual("detalle independiente", Boolean.FALSE, Boolean.valueOf(resultado.getDetalles().get(0) == h.detalles.get(0)));
        igual("auditoria", "auditoria", resultado.getAltaUsr());
        igual("un guardado", Integer.valueOf(1), Integer.valueOf(h.guardados));
        igual("crear no consulta padrones despues de guardar", Integer.valueOf(0), Integer.valueOf(h.consultasPadron));
        resultado.setMoneda(null); resultado.setFormaPago("Condicion de una OC ya emitida");
        RequerimientoCompraOrden repetida = h.crearDirecta(40, "Otro nombre", "auditoria");
        igual("misma orden existente", resultado, repetida);
        igual("moneda fija en reconsulta", "ARS", repetida.getMoneda());
        igual("condicion guardada preservada", "Condicion de una OC ya emitida", repetida.getFormaPago());
        igual("idempotente", Integer.valueOf(1), Integer.valueOf(h.guardados));
        igual("hidratacion existente", h.r, h.preparar(40, "nombre").getRequerimiento());
        igual("solo consulta explicita hidrata", Integer.valueOf(1), Integer.valueOf(h.consultasPadron));
    }

    private static void monedaFija() {
        RequerimientoCompraOrden orden = new RequerimientoCompraOrden();
        igual("moneda por defecto", "ARS", orden.getMoneda());
        String[] compatibles = new String[] { null, "", "   ", "ARS" };
        for (int i = 0; i < compatibles.length; i++) {
            orden.setMoneda(compatibles[i]);
            igual("moneda legacy siempre pesos " + i, "ARS", orden.getMoneda());
        }
        String[] ajenas = new String[] { "USD", "EUR" };
        for (int i = 0; i < ajenas.length; i++) {
            try {
                orden.setMoneda(ajenas[i]);
                throw new AssertionError("Acepto moneda ajena a pesos: " + ajenas[i]);
            } catch (IllegalArgumentException esperado) {
                igual("rechazo no cambia moneda " + ajenas[i], "ARS", orden.getMoneda());
            }
        }
    }

    private static void observacionesEmpresa() throws Exception {
        String[] sectores = new String[] { "Sistemas", "RRHH" };
        for (int i = 0; i < sectores.length; i++) {
            String sector = sectores[i];
            Mock h = observacionEmpresaFixture(sector);
            RequerimientoCompraDetalle original = h.detalles.get(0);
            RequerimientoCompraOrden borrador = h.preparar(40, "Compras");
            igual(sector + " formulario por precio ausente", Boolean.TRUE,
                    Boolean.valueOf(h.requiereCompletarDatos(borrador)));
            igual(sector + " preparar no guarda", Integer.valueOf(0), Integer.valueOf(h.guardados));
            rechaza(h, sector + " emision directa incompleta");
            Map<String, String> entrada = new HashMap<String, String>();
            entrada.put("precio_70", "85000");
            RequerimientoCompraOrden resultado = h.crear(40, entrada, "auditoria");
            RequerimientoCompraDetalle emitido = resultado.getDetalles().get(0);
            igual(sector + " OC emitida con precio", Integer.valueOf(900), Integer.valueOf(resultado.getIdOrdenCompra()));
            igual(sector + " cantidad fuente", Integer.valueOf(1), emitido.getCantidad());
            igual(sector + " texto una sola vez", "CELULAR PRUEBA", emitido.getDescripcionItemVisible());
            igual(sector + " sin observacion duplicada", "", emitido.getObservacionesVisible());
            decimal(sector + " precio obligatorio", "85000", emitido.getPrecioUnitarioEstimado());
            decimal(sector + " subtotal conocido", "85000", emitido.getPrecioTotalEstimado());
            decimal(sector + " IVA opcional cero", "0", resultado.getIva());
            decimal(sector + " IIBB opcional cero", "0", resultado.getIibb());
            decimal(sector + " total conocido", "85000", resultado.getTotal());
            igual(sector + " original sin descripcion tecnica", null, original.getDescripcionItem());
            igual(sector + " observacion original preservada", "CELULAR PRUEBA", original.getObservaciones());
            igual(sector + " precio original preservado", null, original.getPrecioUnitarioEstimado());
            igual(sector + " copia independiente", Boolean.FALSE, Boolean.valueOf(emitido == original));
            igual(sector + " un guardado", Integer.valueOf(1), Integer.valueOf(h.guardados));
            h = observacionEmpresaFixture(sector);
            h.detalles.get(0).setObservaciones("   ");
            rechazaDetalleIncompleto(h, sector + " observacion obligatoria");
        }
        String[] tipos = new String[] { RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO,
                RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR };
        for (int i = 0; i < tipos.length; i++) {
            Mock h = observacionEmpresaFixture("Sistemas");
            h.detalles.get(0).setTipoItem(tipos[i]);
            rechazaDetalleIncompleto(h, tipos[i] + " conserva descripcion tecnica obligatoria");
        }
        Mock ajeno = observacionEmpresaFixture("Prestaciones Medicas");
        rechazaDetalleIncompleto(ajeno, "sector ajeno sin excepcion corporativa");
    }

    private static Mock observacionEmpresaFixture(String sector) {
        Mock h = empresaFixture();
        h.r.setSectorDescripcion(sector);
        RequerimientoCompraDetalle d = h.detalles.get(0);
        d.setDescripcionItem(null);
        d.setObservaciones("CELULAR PRUEBA");
        d.setCantidad(Integer.valueOf(1));
        d.setPrecioUnitarioEstimado(null);
        d.setPrecioTotalEstimado(null);
        return h;
    }

    private static void rechazaDetalleIncompleto(Mock h, String caso) throws Exception {
        try {
            h.crearDirecta(40, "Compras", "auditoria");
        } catch (IllegalArgumentException esperado) {
            igual(caso + " mensaje", "Los detalles del requerimiento estan incompletos. Actualice la pantalla.",
                    esperado.getMessage());
            igual(caso + " sin persistencia", Integer.valueOf(0), Integer.valueOf(h.guardados));
            return;
        }
        throw new AssertionError("No rechazo: " + caso);
    }

    private static void edicionCondiciones() throws Exception {
        Mock h = empresaFixture();
        RequerimientoCompraOrden original = h.crearDirecta(40, "Nombre de Compras", "alta");
        Date emision = original.getFechaEmision();
        BigDecimal precio = original.getDetalles().get(0).getPrecioUnitarioEstimado();
        Map<String, String> campos = new HashMap<String, String>();
        campos.put("condicion", "  Condicion acordada  ");
        campos.put("forma_pago", "Transferencia");
        campos.put("plazo_pago", "30 dias");
        campos.put("entrega", "Domicilio acordado");
        campos.put("observaciones", "Observacion actualizada");
        campos.put("proveedor", "Proveedor manipulado");
        campos.put("moneda", "USD");
        RequerimientoCompraOrden actual = h.actualizar(40, 900, 0, campos, "editor");
        igual("condicion editada y normalizada", "Condicion acordada", actual.getCondicion());
        igual("forma editada", "Transferencia", actual.getFormaPago());
        igual("plazo editado", "30 dias", actual.getPlazoPago());
        igual("entrega editada", "Domicilio acordado", actual.getEntrega());
        igual("observaciones editadas", "Observacion actualizada", actual.getObservaciones());
        igual("numero preservado", Integer.valueOf(900), Integer.valueOf(actual.getIdOrdenCompra()));
        igual("fecha preservada", emision, actual.getFechaEmision());
        igual("proveedor preservado", "Empresa de prueba", actual.getProveedor());
        igual("moneda fija preservada", "ARS", actual.getMoneda());
        igual("alta preservada", "alta", actual.getAltaUsr());
        decimal("precio preservado", precio.toPlainString(), actual.getDetalles().get(0).getPrecioUnitarioEstimado());
        igual("version incrementada", Integer.valueOf(1), Integer.valueOf(actual.getVersionEdicion()));
        igual("auditoria edicion", "editor", actual.getModiUsr());
        igual("fecha edicion", Boolean.TRUE, Boolean.valueOf(actual.getModiFecha() != null));
        rechazaEdicion(h, 40, 900, 0, campos, "editor", "version obsoleta");
        rechazaEdicion(h, 40, 901, 1, campos, "editor", "orden ajena");
        rechazaEdicion(h, 40, 900, -1, campos, "editor", "version ausente");
        rechazaEdicion(h, 40, 900, 1, campos, " ", "auditoria ausente");
        rechazaEdicion(h, 40, 900, 1, null, "editor", "campos ausentes");
        String[] nombres = { "condicion", "forma_pago", "plazo_pago", "entrega", "observaciones" };
        int[] maximos = { 1000, 250, 250, 1000, 4000 };
        for (int i = 0; i < nombres.length; i++) {
            String previo = campos.get(nombres[i]);
            StringBuilder largo = new StringBuilder();
            for (int j = 0; j <= maximos[i]; j++) { largo.append('a'); }
            campos.put(nombres[i], largo.toString());
            rechazaEdicion(h, 40, 900, 1, campos, "editor", nombres[i] + " excedido");
            campos.put(nombres[i], previo);
        }
        h.r.setEstado(WebKeysCompras.ESTADO_ANULADO);
        rechazaEdicion(h, 40, 900, 1, campos, "editor", "estado distinto a OC");
        h.r.setEstado(WebKeysCompras.ESTADO_ORDEN_COMPRA);
        actual = h.actualizar(40, 900, 1, new HashMap<String, String>(), "editor2");
        igual("limpiar condicion", "", actual.getCondicion());
        igual("version segunda edicion", Integer.valueOf(2), Integer.valueOf(actual.getVersionEdicion()));
        igual("creacion conserva idempotencia tras editar", actual, h.crearDirecta(40, "Nombre", "alta"));
        igual("sin nueva alta", Integer.valueOf(1), Integer.valueOf(h.guardados));
        igual("editar no consulta padrones tras commit", Integer.valueOf(0), Integer.valueOf(h.consultasPadron));
    }

    private static void rechazaEdicion(Mock h, int id, int orden, int version,
            Map<String, String> campos, String usuario, String caso) throws Exception {
        int antes = h.actualizaciones;
        try { h.actualizar(id, orden, version, campos, usuario); }
        catch (IllegalArgumentException esperado) {
            igual(caso + " sin UPDATE", Integer.valueOf(antes), Integer.valueOf(h.actualizaciones));
            return;
        }
        throw new AssertionError("No rechazo edicion: " + caso);
    }

    private static void permisosEdicion() throws Exception {
        String[] roles = { WebKeysCompras.ROL_ABM_COMPRAS, WebKeysCompras.ROL_COTIZAR_COMPRAS,
                WebKeysCompras.ROL_VIEW_COMPRAS, WebKeysCompras.ROL_AUTORIZAR_COMPRAS, "OTRO" };
        igual("anonimo no edita", Boolean.FALSE, Boolean.valueOf(RequerimientoCompraOrdenAction.puedeEditar(null)));
        for (int i = 0; i < roles.length; i++) {
            final List<Role> asignados = new ArrayList<Role>();
            Role rol = new RoleImpl(); rol.setName(roles[i]); asignados.add(rol);
            User usuario = new UserImpl() {
                public List<Role> getRoles() { return asignados; }
            };
            igual(roles[i] + " permiso mutacion", Boolean.valueOf(i == 0),
                    Boolean.valueOf(RequerimientoCompraOrdenAction.puedeEditar(usuario)));
            igual(roles[i] + " consulta", Boolean.valueOf(i < 4),
                    Boolean.valueOf(RequerimientoCompraOrdenAction.puedeConsultar(usuario)));
        }
    }

    private static void flujoAction() throws Exception {
        AccionPrueba accion = new AccionPrueba(observacionEmpresaFixture("Sistemas"), WebKeysCompras.ROL_ABM_COMPRAS);
        PeticionPrueba peticion = new PeticionPrueba();
        accion.ejecutar(peticion);
        igual("Action incompleta no consume token", Integer.valueOf(0), Integer.valueOf(accion.consumidos));
        igual("Action incompleta no emite", Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
        igual("Action incompleta no autoimprime", null, peticion.atributos.get("ordenCompraCreada"));
        accion.mostrar(peticion);
        igual("Action muestra formulario nuevo", Boolean.TRUE, peticion.atributos.get("ordenCompraPuedeCrear"));
        igual("Action muestra precio faltante", Boolean.TRUE, peticion.atributos.get("ordenCompraRequiereDatos"));
        igual("Action formulario publica token", Integer.valueOf(1), Integer.valueOf(accion.publicados));
        peticion.parametros.put("cmd", "create"); peticion.parametros.put("precio_70", "");
        accion.ejecutar(peticion);
        igual("Action error de precio conserva entrada", "", ((Map)peticion.atributos.get("ordenCompraEntrada")).get("precio_70"));
        igual("Action error de precio sin persistencia", Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
        igual("Action error sin autoimprimir", null, peticion.atributos.get("ordenCompraCreada"));
        peticion.parametros.put("precio_70", "85000");
        peticion.atributos.clear(); accion.ejecutar(peticion);
        igual("Action solo precio emite", Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        decimal("Action propaga precio", "85000", accion.modelo.guardada.getTotal());
        igual("Action completa permite imprimir", Boolean.TRUE, peticion.atributos.get("ordenCompraCreada"));
        accion = new AccionPrueba(empresaFixture(), WebKeysCompras.ROL_ABM_COMPRAS);
        peticion = new PeticionPrueba(); accion.ejecutar(peticion);
        igual("Action completa emision directa", Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        igual("Action completa consume token", Integer.valueOf(1), Integer.valueOf(accion.consumidos));
        accion.ejecutar(peticion);
        igual("Action repetida idempotente", Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        igual("Action repetida no consume token", Integer.valueOf(1), Integer.valueOf(accion.consumidos));
        Mock historica = observacionEmpresaFixture("RRHH");
        historica.guardada = historica.prepararParaGuardar(40, "Compras"); historica.guardada.setIdOrdenCompra(900);
        historica.r.setEstado(WebKeysCompras.ESTADO_ORDEN_COMPRA);
        accion = new AccionPrueba(historica, WebKeysCompras.ROL_ABM_COMPRAS); peticion = new PeticionPrueba();
        accion.mostrar(peticion);
        igual("Action historica muestra reparacion", Boolean.TRUE, peticion.atributos.get("ordenCompraRequiereDatos"));
        igual("Action historica conserva OC", Boolean.FALSE, peticion.atributos.get("ordenCompraPuedeCrear"));
        peticion.parametros.put("cmd", "update"); peticion.parametros.put("id_orden_compra", "900");
        peticion.parametros.put("version_edicion", "0"); peticion.parametros.put("precio_70", "20");
        accion.ejecutar(peticion);
        igual("Action repara sin nueva OC", Integer.valueOf(0), Integer.valueOf(historica.guardados));
        igual("Action repara una actualizacion", Integer.valueOf(1), Integer.valueOf(historica.actualizaciones));
        decimal("Action repara precio", "20", historica.guardada.getTotal());
        igual("Action reparacion confirmada", Boolean.TRUE, peticion.atributos.get("ordenCompraActualizada"));
        accion.ejecutar(peticion);
        igual("Action version obsoleta rechaza doble reparacion", Integer.valueOf(1), Integer.valueOf(historica.actualizaciones));
        accion = new AccionPrueba(observacionEmpresaFixture("RRHH"), WebKeysCompras.ROL_VIEW_COMPRAS);
        peticion = new PeticionPrueba(); accion.ejecutar(peticion);
        igual("Action consulta no crea", Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
        igual("Action consulta sin consumir token", Integer.valueOf(0), Integer.valueOf(accion.consumidos));
        peticion.atributos.clear(); accion.mostrar(peticion);
        igual("Action consulta no abre formulario de alta", null, peticion.atributos.get("ordenCompraPuedeCrear"));
        igual("Action consulta recibe error", Boolean.TRUE, Boolean.valueOf(peticion.atributos.get("ordenCompraError") != null));
    }

    private static class AccionPrueba extends RequerimientoCompraOrdenAction {
        Mock modelo;
        User usuario;
        int consumidos;
        int publicados;
        AccionPrueba(Mock modelo, String nombreRol) {
            this.modelo = modelo;
            final List<Role> roles = new ArrayList<Role>();
            Role rol = new RoleImpl(); rol.setName(nombreRol); roles.add(rol);
            usuario = new UserImpl() {
                public List<Role> getRoles() { return roles; }
                public String getScreenName() { return "auditoria"; }
                public String getFullName() { return "Nombre de Compras"; }
            };
        }
        protected RequerimientoCompraOrdenHelper obtenerHelper() { return modelo; }
        protected User obtenerUsuario(javax.portlet.PortletRequest request) { return usuario; }
        protected void consumirToken(javax.portlet.ActionRequest request) { consumidos++; }
        protected void publicarToken(javax.portlet.RenderRequest request) { publicados++; }
        void ejecutar(PeticionPrueba p) throws Exception {
            processAction(null, null, null, (javax.portlet.ActionRequest)p.proxy(),
                    (javax.portlet.ActionResponse)java.lang.reflect.Proxy.newProxyInstance(
                            getClass().getClassLoader(), new Class[] { javax.portlet.ActionResponse.class }, p));
        }
        void mostrar(PeticionPrueba p) throws Exception {
            org.apache.struts.action.ActionMapping mapping = new org.apache.struts.action.ActionMapping() {
                public org.apache.struts.action.ActionForward findForward(String nombre) {
                    org.apache.struts.action.ActionForward forward = new org.apache.struts.action.ActionForward();
                    forward.setPath("/prueba.jsp"); return forward;
                }
            };
            render(mapping, null, null, (javax.portlet.RenderRequest)p.proxy(), null);
        }
    }

    private static class PeticionPrueba implements java.lang.reflect.InvocationHandler {
        Map<String, String> parametros = new HashMap<String, String>();
        Map<String, Object> atributos = new HashMap<String, Object>();
        PeticionPrueba() { parametros.put("id_requerimiento_compra", "40"); }
        Object proxy() {
            return java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[] { javax.portlet.ActionRequest.class, javax.portlet.RenderRequest.class }, this);
        }
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
            String nombre = method.getName();
            if ("getParameter".equals(nombre)) return parametros.get(args[0]);
            if ("getParameterValues".equals(nombre)) {
                String valor = parametros.get(args[0]); return valor == null ? null : new String[] { valor };
            }
            if ("getParameterMap".equals(nombre)) {
                Map<String, String[]> valores = new HashMap<String, String[]>();
                for (String clave : parametros.keySet()) valores.put(clave, new String[] { parametros.get(clave) });
                return valores;
            }
            if ("getParameterNames".equals(nombre)) return java.util.Collections.enumeration(parametros.keySet());
            if ("setAttribute".equals(nombre)) { atributos.put((String)args[0], args[1]); return null; }
            if ("getAttribute".equals(nombre)) return atributos.get(args[0]);
            if ("removeAttribute".equals(nombre)) { atributos.remove(args[0]); return null; }
            if ("getAttributeNames".equals(nombre)) return java.util.Collections.enumeration(atributos.keySet());
            if ("toString".equals(nombre)) return "PeticionPrueba";
            if (method.getReturnType().equals(Boolean.TYPE)) return Boolean.FALSE;
            if (method.getReturnType().equals(Integer.TYPE)) return Integer.valueOf(0);
            return null;
        }
    }

    private static void entradaFormulario() throws Exception {
        Map<String, String> entrada = new HashMap<String, String>();
        entrada.put("precio_70", "999999"); entrada.put("cantidad_70", "999");
        entrada.put("iva", "12.50"); entrada.put("iibb", "2.50"); entrada.put("moneda", "USD");
        entrada.put("proveedor", "Proveedor alterado"); entrada.put("proveedorCuit", "00000000000");
        entrada.put("confeccionadoPor", "Actor alterado"); entrada.put("fechaRecepcion", "01/01/2000");
        entrada.put("condicion", " Acuerdo "); entrada.put("forma_pago", "Transferencia");
        entrada.put("plazo_pago", "30 dias"); entrada.put("entrega", " Retiro ");
        entrada.put("observaciones", "Observacion OC");
        entrada.put("solicitante", "Dato inventado"); entrada.put("plan", "Dato inventado");
        RequerimientoCompraOrden resultado = empresaFixture().crear(40, entrada, "auditoria");
        decimal("precio conocido no editable", "100", resultado.getDetalles().get(0).getPrecioUnitarioEstimado());
        igual("cantidad siempre fuente", Integer.valueOf(2), resultado.getDetalles().get(0).getCantidad());
        igual("moneda siempre pesos", "ARS", resultado.getMoneda());
        decimal("IVA del formulario", "12.50", resultado.getIva());
        decimal("IIBB del formulario", "2.50", resultado.getIibb());
        decimal("total con impuestos", "215", resultado.getTotal());
        igual("proveedor siempre fuente", "Empresa de prueba", resultado.getProveedor());
        igual("actor autenticado", "auditoria", resultado.getConfeccionadoPor());
        igual("solicitante siempre fuente", "solicitante", resultado.getSolicitante());
        igual("condicion formulario normalizada", "Acuerdo", resultado.getCondicion());
        igual("pago formulario", "Transferencia", resultado.getFormaPago());
        igual("plazo formulario", "30 dias", resultado.getPlazoPago());
        igual("entrega formulario normalizada", "Retiro", resultado.getEntrega());
        igual("observacion formulario", "Observacion OC", resultado.getObservaciones());
        igual("plan no editable", null, resultado.getPlan());
        igual("fecha no editable", null, resultado.getFechaRecepcion());
    }

    private static void formularioSoloPrecios() throws Exception {
        String[] validos = { "0", "85000", "85000,25", "85000.25" };
        String[] normalizados = { "0", "85000", "85000.25", "85000.25" };
        for (int i = 0; i < validos.length; i++) {
            Mock h = observacionEmpresaFixture("Sistemas");
            Map<String, String> entrada = new HashMap<String, String>();
            entrada.put("precio_70", validos[i]);
            RequerimientoCompraOrden resultado = h.crear(40, entrada, "auditoria");
            decimal("precio explicito valido " + validos[i], normalizados[i], resultado.getTotal());
            igual("solo precio permite emision " + validos[i], Integer.valueOf(1), Integer.valueOf(h.guardados));
            igual("CUIT opcional " + validos[i], "", resultado.getProveedorCuit());
        }
        String[] invalidos = { null, "", "  ", "-1", "texto", "1.001", "100000000000000.00" };
        for (int i = 0; i < invalidos.length; i++) {
            Mock h = observacionEmpresaFixture("RRHH");
            Map<String, String> entrada = new HashMap<String, String>();
            entrada.put("precio_70", invalidos[i]);
            entrada.put("cantidad_70", "999");
            rechazaFormulario(h, entrada, "precio invalido " + invalidos[i]);
        }
        Mock h = observacionEmpresaFixture("Sistemas");
        Map<String, String> ajeno = new HashMap<String, String>();
        ajeno.put("precio_70", "10"); ajeno.put("precio_999", "20");
        rechazaFormulario(h, ajeno, "detalle ajeno no se procesa");
        h = observacionEmpresaFixture("Sistemas");
        RequerimientoCompraDetalle segundo = new RequerimientoCompraDetalle(Integer.valueOf(71));
        segundo.setIdRequerimientoCompra(40); segundo.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION);
        segundo.setObservaciones("SEGUNDO EQUIPO"); segundo.setCantidad(Integer.valueOf(1)); h.detalles.add(segundo);
        Map<String, String> entrada = new HashMap<String, String>(); entrada.put("precio_70", "10");
        rechazaFormulario(h, entrada, "no admite total parcial con segundo precio ausente");
        entrada.put("precio_71", "20");
        decimal("dos precios completados", "30", h.crear(40, entrada, "auditoria").getTotal());
        h = medicinaFixture(); entrada = new HashMap<String, String>();
        entrada.put("precio_70", "99999"); entrada.put("iva", "999"); entrada.put("iibb", "999");
        RequerimientoCompraOrden medica = h.crear(40, entrada, "auditoria");
        decimal("form no modifica precio medico", "50", medica.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("form no modifica IVA medico", "10", medica.getIva());
        decimal("form no modifica IIBB medico", "5", medica.getIibb());
    }

    private static void rechazaFormulario(Mock h, Map<String, String> entrada, String caso) throws Exception {
        try { h.crear(40, entrada, "auditoria"); }
        catch (IllegalArgumentException esperado) {
            igual(caso + " sin persistencia", Integer.valueOf(0), Integer.valueOf(h.guardados));
            igual(caso + " sin mutar precio fuente", null, h.detalles.get(0).getPrecioUnitarioEstimado());
            return;
        }
        throw new AssertionError("No rechazo formulario: " + caso);
    }

    private static void repararPrecios() throws Exception {
        Mock h = observacionEmpresaFixture("Sistemas");
        h.guardada = h.preparar(40, "Compras"); h.guardada.setIdOrdenCompra(900);
        h.r.setEstado(WebKeysCompras.ESTADO_ORDEN_COMPRA);
        Map<String, String> entrada = new HashMap<String, String>();
        try { h.actualizar(40, 900, 0, entrada, "editor"); throw new AssertionError("Reparo sin precio"); }
        catch (IllegalArgumentException esperado) { igual("reparacion incompleta sin UPDATE", Integer.valueOf(0), Integer.valueOf(h.actualizaciones)); }
        entrada.put("precio_70", "85000"); entrada.put("cantidad_70", "99");
        RequerimientoCompraOrden reparada = h.actualizar(40, 900, 0, entrada, "editor");
        decimal("reparacion precio faltante", "85000", reparada.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("reparacion total conocido", "85000", reparada.getTotal());
        igual("reparacion preserva cantidad", Integer.valueOf(1), reparada.getDetalles().get(0).getCantidad());
        igual("reparacion version", Integer.valueOf(1), Integer.valueOf(reparada.getVersionEdicion()));
        igual("reparacion no modifica requerimiento", null, h.detalles.get(0).getPrecioUnitarioEstimado());
        entrada.put("precio_70", "1");
        reparada = h.actualizar(40, 900, 1, entrada, "editor2");
        decimal("precio informado permanece fijo", "85000", reparada.getDetalles().get(0).getPrecioUnitarioEstimado());
        igual("reparacion no crea otra OC", Integer.valueOf(0), Integer.valueOf(h.guardados));
    }

    private static void importes() throws Exception {
        Mock h = empresaFixture(); h.detalles.get(0).setPrecioUnitarioEstimado(null);
        rechaza(h, "precio faltante requiere formulario");
        h = empresaFixture(); h.detalles.get(0).setPrecioUnitarioEstimado(null);
        h.detalles.get(0).setPrecioTotalEstimado(new BigDecimal("999"));
        RequerimientoCompraOrden borrador = h.preparar(40, "Compras");
        igual("sin subtotal aislado sin precio", null, borrador.getDetalles().get(0).getPrecioTotalEstimado());
        decimal("subtotal original aislado sin mutar", "999", h.detalles.get(0).getPrecioTotalEstimado());
        h = empresaFixture(); h.detalles.get(0).setPrecioUnitarioEstimado(BigDecimal.ZERO);
        RequerimientoCompraOrden resultado = h.crearDirecta(40, "Compras", "auditoria");
        decimal("cero informado conservado", "0", resultado.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("subtotal cero informado", "0", resultado.getDetalles().get(0).getPrecioTotalEstimado());
        decimal("total cero con impuestos opcionales", "0", resultado.getTotal());
        resultado.setIva(null); resultado.setIibb(null);
        decimal("IVA null equivale cero", "0", resultado.getIva());
        decimal("IIBB null equivale cero", "0", resultado.getIibb());
        decimal("total cero con impuestos null", "0", resultado.getTotal());
        resultado.getDetalles().get(0).setPrecioUnitarioEstimado(null);
        igual("precio ausente no es cero", null, resultado.getTotal());
        String[] invalidos = new String[] {"-1", "100000000000000.00"};
        for (int i = 0; i < invalidos.length; i++) {
            h = empresaFixture(); h.detalles.get(0).setPrecioUnitarioEstimado(new BigDecimal(invalidos[i]));
            rechaza(h, "precio fuente invalido " + invalidos[i]);
        }
        h = empresaFixture(); h.detalles.get(0).setPrecioUnitarioEstimado(new BigDecimal("99999999999999.99"));
        rechaza(h, "subtotal fuente fuera de rango");
        h = empresaFixture(); h.detalles.get(0).setCantidad(Integer.valueOf(1));
        h.detalles.get(0).setPrecioUnitarioEstimado(new BigDecimal("99999999999999.99"));
        resultado = h.crearDirecta(40, "Compras", "auditoria");
        decimal("importe maximo fuente valido", "99999999999999.99", resultado.getDetalles().get(0).getPrecioTotalEstimado());
        h = empresaFixture(); h.detalles.get(0).setPrecioTotalEstimado(new BigDecimal("999"));
        rechaza(h, "subtotal fuente incoherente");
    }

    private static void estadoYAdjudicacion() throws Exception {
        Mock h = empresaFixture(); h.r.setEstado(WebKeysCompras.ESTADO_PENDIENTE);
        rechaza(h, "estado no autorizado");
        h = empresaFixture(); h.preparar(40, "nombre");
        h.r.setEstado(WebKeysCompras.ESTADO_PENDIENTE);
        rechaza(h, "estado recargado al confirmar");
        h = empresaFixture(); h.r.setBajaFecha(new Date());
        rechaza(h, "requerimiento inactivo");
        h = empresaFixture(); h.empresas.clear(); rechaza(h, "sin adjudicacion");
        h = empresaFixture(); h.empresas.add(h.empresas.get(0)); rechaza(h, "dos empresas adjudicadas");
        h = empresaFixture(); h.empresas.get(0).setIdRequerimiento(Integer.valueOf(99));
        rechaza(h, "empresa de otro requerimiento");
        h = empresaFixture(); h.detalles.get(0).setCantidad(Integer.valueOf(0));
        rechaza(h, "cantidad no positiva");
        h = empresaFixture(); h.detalles.get(0).setIdRequerimientoCompra(99); rechaza(h, "detalle ajeno");
        h = empresaFixture(); h.detalles.add(h.detalles.get(0)); rechaza(h, "detalle duplicado");
        h = empresaFixture();
        RequerimientoCompraOrden sinNombreDuplicado = h.crearDirecta(40, " ", "auditoria");
        igual("actor por identidad sin nombre duplicado", "auditoria", sinNombreDuplicado.getConfeccionadoPor());
        igual("identidad autenticada permite emision", Integer.valueOf(1), Integer.valueOf(h.guardados));
        h = empresaFixture();
        try { h.crearDirecta(40, "Compras", " "); throw new AssertionError("Auditoria vacia aceptada"); }
        catch (IllegalArgumentException esperado) { igual("auditoria obligatoria sin guardado", Integer.valueOf(0), Integer.valueOf(h.guardados)); }
    }

    private static void medicina() throws Exception {
        Mock h = medicinaFixture();
        RequerimientoCompraOrden resultado = h.crearDirecta(40, "Compras", "auditoria");
        igual("moneda medica en pesos", "ARS", resultado.getMoneda());
        igual("RP canonico", Integer.valueOf(68030), Integer.valueOf(resultado.getIdReclamoPrestacional()));
        igual("pago comparativa", "45 dias", resultado.getFormaPago());
        igual("fecha documento", "02/10/2026", new SimpleDateFormat("dd/MM/yyyy").format(resultado.getFechaOrdenMedica()));
        igual("proveedor medico canonico", "Prestador de prueba", resultado.getProveedor());
        igual("CUIT medico canonico", "30700000001", resultado.getProveedorCuit());
        igual("cantidad comparativa", Integer.valueOf(3), resultado.getDetalles().get(0).getCantidad());
        decimal("precio comparativa", "50", resultado.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("IVA comparativa", "10", resultado.getIva()); decimal("IIBB comparativa", "5", resultado.getIibb());
        decimal("total medico conocido", "165", resultado.getTotal());
        igual("cantidad original preservada", Integer.valueOf(2), h.detalles.get(0).getCantidad());
        decimal("precio original medico preservado", "100", h.detalles.get(0).getPrecioUnitarioEstimado());
        h = medicinaFixture(); h.comparativas.get(0).setIva(null);
        resultado = h.crearDirecta(40, "Compras", "auditoria");
        decimal("IVA fuente ausente es cero", "0", resultado.getIva()); decimal("total medico con IVA ausente", "155", resultado.getTotal());
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().get(0).setImporteUnitario(BigDecimal.ZERO);
        decimal("precio medico cero conocido", "0", h.crearDirecta(40, "Compras", "auditoria").getDetalles().get(0).getPrecioUnitarioEstimado());
        h = medicinaFixture(); h.relacion = null; rechaza(h, "sin RP");
        h = medicinaFixture(); h.relacion.setIdRequerimientoCompra(99); rechaza(h, "RP ajeno");
        h = medicinaFixture(); h.comparativas.clear(); rechaza(h, "sin comparativa guardada");
        h = medicinaFixture(); h.comparativas.get(0).setIdPrestador(99); rechaza(h, "comparativa de otro prestador");
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().clear(); rechaza(h, "comparativa incompleta");
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().add(h.comparativas.get(0).getDetalles().get(0));
        rechaza(h, "comparativa duplicada");
        h = medicinaFixture(); h.comparativas.get(0).setIva(new BigDecimal("-1")); rechaza(h, "impuesto adjudicado negativo");
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().get(0).setImporteUnitario(null);
        rechaza(h, "precio medico no informado");
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().get(0).setImporteUnitario(new BigDecimal("1.001"));
        rechaza(h, "precio medico con precision no admitida");
        h = medicinaFixture(); h.detalles.get(0).setIdPrestador(null); rechaza(h, "adjudicacion medica incompleta");
    }

    private static Mock empresaFixture() {
        Mock h = base(); h.r.setSectorDescripcion("Sistemas");
        RequerimientoCompraPresupuesto empresa = new RequerimientoCompraPresupuesto();
        empresa.setIdRequerimientoPresupuesto(Integer.valueOf(101)); empresa.setIdRequerimiento(Integer.valueOf(40));
        empresa.setTipoDocumento(Integer.valueOf(RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA));
        empresa.setDescripcionEmpresa("Empresa de prueba"); empresa.setEmpresaAdjudicada(true); h.empresas.add(empresa);
        return h;
    }

    private static Mock medicinaFixture() throws Exception {
        Mock h = base(); h.r.setSectorDescripcion("Prestaciones Medicas");
        RequerimientoCompraDetalle d = h.detalles.get(0);
        d.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR); d.setIdPrestacion(Integer.valueOf(200));
        d.setIdTipoNomenclador(Integer.valueOf(10)); d.setCodigoNomenclador("INS200");
        d.setDescripcionNomenclador("Insumo de prueba"); d.setIdPrestador(Integer.valueOf(30));
        d.setPrestadorRazonSocial("Prestador de prueba"); d.setPrestadorCuit("30700000001");
        RequerimientoCompraComparativa c = new RequerimientoCompraComparativa();
        c.setIdComparativa(60); c.setIdRequerimiento(40); c.setIdPrestador(30);
        c.setFormaPago(Integer.valueOf(45)); c.setPlazoEntrega("24/48"); c.setEnvio("Obra Social");
        c.setIva(new BigDecimal("10")); c.setIibb(new BigDecimal("5"));
        RequerimientoCompraComparativaDetalle precio = new RequerimientoCompraComparativaDetalle();
        precio.setIdDetalle(61); precio.setIdPrestacion(200); precio.setCantidad(Integer.valueOf(3));
        precio.setImporteUnitario(new BigDecimal("50")); c.getDetalles().add(precio); h.comparativas.add(c);
        h.relacion = new RequerimientoCompraReclamoPrestacional(); h.relacion.setIdRequerimientoCompra(40);
        h.relacion.setIdReclamoPrestacional(Integer.valueOf(68030)); h.relacion.setEstado(WebKeysCompras.VINCULO_RECLAMO_VINCULADO);
        RequerimientoCompraPresupuesto documento = new RequerimientoCompraPresupuesto();
        documento.setIdRequerimiento(Integer.valueOf(40));
        documento.setFechaDocumento(new SimpleDateFormat("dd/MM/yyyy").parse("02/10/2026")); h.documentos.add(documento);
        return h;
    }

    private static Mock base() {
        Mock h = new Mock(); h.r = new RequerimientoCompra(); h.r.setIdRequerimientoCompra(40);
        h.r.setEstado(WebKeysCompras.ESTADO_AUTORIZADO); h.r.setAltaUsr("solicitante"); h.r.setAltaFecha(new Date());
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle(Integer.valueOf(70));
        d.setIdRequerimientoCompra(40); d.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION);
        d.setDescripcionItem("Equipo de prueba"); d.setCantidad(Integer.valueOf(2)); d.setPrecioUnitarioEstimado(new BigDecimal("100"));
        h.detalles.add(d); h.r.setDetalles(h.detalles); return h;
    }

    private static void rechaza(Mock h, String caso) throws Exception {
        try { h.crearDirecta(40, "Compras", "auditoria"); }
        catch (IllegalArgumentException esperado) {
            igual(caso + " sin persistencia", Integer.valueOf(0), Integer.valueOf(h.guardados)); return;
        }
        throw new AssertionError("No rechazo: " + caso);
    }

    private static void decimal(String caso, String esperado, BigDecimal actual) {
        checks++;
        if (actual == null || new BigDecimal(esperado).compareTo(actual) != 0) {
            throw new AssertionError(caso + ": esperado=" + esperado + ", actual=" + actual);
        }
    }

    private static void igual(String caso, Object esperado, Object actual) {
        checks++;
        if (esperado == null ? actual != null : !esperado.equals(actual)) {
            throw new AssertionError(caso + ": esperado=" + esperado + ", actual=" + actual);
        }
    }

    private static class Mock extends RequerimientoCompraOrdenHelper {
        RequerimientoCompra r;
        RequerimientoCompraOrden guardada;
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        List<RequerimientoCompraPresupuesto> empresas = new ArrayList<RequerimientoCompraPresupuesto>();
        List<RequerimientoCompraComparativa> comparativas = new ArrayList<RequerimientoCompraComparativa>();
        List<RequerimientoCompraPresupuesto> documentos = new ArrayList<RequerimientoCompraPresupuesto>();
        RequerimientoCompraReclamoPrestacional relacion;
        int guardados;
        int actualizaciones;
        int consultasPadron;
        protected void completarDatosConsulta(RequerimientoCompraOrden orden, RequerimientoCompra requerimiento) { consultasPadron++; }
        protected RequerimientoCompra obtenerRequerimiento(int id) { return r; }
        protected List<RequerimientoCompraDetalle> obtenerDetalles(int id) { return detalles; }
        protected RequerimientoCompraOrden obtenerOrden(int id) { return guardada; }
        protected RequerimientoCompraOrden guardarOrden(RequerimientoCompraOrden orden, String usuario) {
            guardados++; orden.setIdOrdenCompra(900); guardada = orden; r.setEstado(WebKeysCompras.ESTADO_ORDEN_COMPRA); return orden;
        }
        protected RequerimientoCompraOrden actualizarOrden(RequerimientoCompraOrden cambio, String usuario) {
            actualizaciones++;
            guardada.setCondicion(cambio.getCondicion()); guardada.setFormaPago(cambio.getFormaPago());
            guardada.setPlazoPago(cambio.getPlazoPago()); guardada.setEntrega(cambio.getEntrega());
            guardada.setObservaciones(cambio.getObservaciones()); guardada.setModiUsr(usuario);
            for (int i = 0; i < cambio.getDetalles().size(); i++) {
                RequerimientoCompraDetalle nuevo = cambio.getDetalles().get(i);
                for (int j = 0; j < guardada.getDetalles().size(); j++) {
                    RequerimientoCompraDetalle previo = guardada.getDetalles().get(j);
                    if (previo.getId().equals(nuevo.getId()) && previo.getPrecioUnitarioEstimado() == null) {
                        previo.setPrecioUnitarioEstimado(nuevo.getPrecioUnitarioEstimado());
                        previo.setPrecioTotalEstimado(nuevo.getPrecioTotalEstimado());
                    }
                }
            }
            guardada.setModiFecha(new Date()); guardada.setVersionEdicion(cambio.getVersionEdicion() + 1);
            return guardada;
        }
        protected List<RequerimientoCompraPresupuesto> obtenerCotizacionesEmpresa(int id) { return empresas; }
        protected List<RequerimientoCompraComparativa> obtenerComparativas(int id, int idPrestador) { return comparativas; }
        protected RequerimientoCompraReclamoPrestacional obtenerRelacionRp(int id) { return relacion; }
        protected List<RequerimientoCompraPresupuesto> obtenerOrdenesMedicas(int id) { return documentos; }
    }
}
