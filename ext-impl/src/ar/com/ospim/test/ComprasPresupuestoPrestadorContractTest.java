package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato del presupuesto único por prestador y su estado real. */
public final class ComprasPresupuestoPrestadorContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String BASE = "ext-impl/src/ar/com/ospim/compras/";

    private static final String MIGRACION =
            BASE + "sql/20260721_presupuesto_activo_prestador.sql";

    private static final String INDICE_PRESUPUESTO_ACTIVO =
            "ux_compras_presupuesto_requerimiento_prestador_activo";

    private ComprasPresupuestoPrestadorContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String schema = leer(BASE + "sql/compras_schema.sql");
        String migracion = leerArchivoObligatorio(MIGRACION);
        String action = leer(
                BASE + "requerimientos/action/"
                        + "UploadPresupuestosComprasAction.java"
        );
        String jsp = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_adjuntos.jsp"
        );
        String webKeys = leer(BASE + "WebKeysCompras.java");
        String searchImpl = leer(
                BASE + "requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceImpl.java"
        );
        String editImpl = leer(
                BASE + "requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );
        String editUtil = leer(
                BASE + "requerimientos/service/"
                        + "EditarRequerimientoCompraServiceUtil.java"
        );
        String searchUtil = leer(
                BASE + "requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceUtil.java"
        );

        verificarEsquemaCanonico(schema);
        verificarMigracion(migracion);
        verificarListadoPersistido(searchImpl, webKeys);
        verificarCarga(action, jsp);
        verificarAusenciaDeSincronizacionLocal(action, editImpl);
        verificarAusenciaDeProyeccionVisual(searchUtil);
        contiene(
                editUtil,
                "import java.util.List conservado",
                "import java.util.List;"
        );

        System.out.println("CONTRATO_COMPRAS_PRESUPUESTO_PRESTADOR_OK");
    }

    private static void verificarEsquemaCanonico(String schema) {
        String tablaEstadoPrestador = seccion(
                schema,
                "tabla de estados del prestador",
                "CREATE TABLE compras.requerimiento_cotizacion_prestador (",
                "CREATE INDEX ix_compras_cotizacion_requerimiento_estado"
        );

        String estadoNormalizado = normalizarEspacios(tablaEstadoPrestador);

        contiene(
                estadoNormalizado,
                "CHECK individual con COTIZADO",
                "CONSTRAINT ck_compras_cotizacion_estado_envio CHECK ( "
                        + "estado_envio IN ( 'PENDIENTE', 'PROCESANDO', "
                        + "'ENVIADO', 'COTIZADO', 'ERROR', "
                        + "'EMAIL_INVALIDO' ) )"
        );

        String indice = seccion(
                schema,
                "indice unico parcial canonico",
                "CREATE UNIQUE INDEX " + INDICE_PRESUPUESTO_ACTIVO,
                "CREATE INDEX ix_compras_presupuesto_folder_name"
        );

        verificarIndiceParcial(indice, "indice unico parcial canonico");

        String registrar = seccion(
                schema,
                "alta atomica de presupuesto",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.registrar_requerimiento_presupuesto(",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.listar_requerimiento_presupuestos("
        );

        minimoOcurrencias(
                registrar,
                "bloqueos del alta",
                "FOR UPDATE",
                2
        );
        contiene(
                registrar,
                "alta solo para requerimiento A COTIZAR",
                "v_estado_requerimiento <> 2"
        );
        contiene(
                registrar,
                "alta solo para prestador ENVIADO",
                "v_estado_envio <> 'ENVIADO'"
        );
        contiene(
                registrar,
                "alta rechaza otro presupuesto activo",
                "AND rp.baja_fecha IS NULL"
        );
        enOrden(
                registrar,
                "asociacion y estado atomicos en el alta",
                "INSERT INTO compras.requerimiento_presupuesto",
                "SET estado_envio = 'COTIZADO'",
                "AND estado_envio = 'ENVIADO'"
        );

        String baja = seccion(
                schema,
                "baja atomica de presupuesto",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.baja_requerimiento_presupuesto(",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.reactivar_requerimiento_presupuesto("
        );

        minimoOcurrencias(baja, "bloqueos de la baja", "FOR UPDATE", 3);
        contiene(
                baja,
                "baja solo para requerimiento A COTIZAR",
                "v_estado_requerimiento <> 2"
        );
        contiene(
                baja,
                "baja logica de la asociacion",
                "SET baja_fecha = now()"
        );
        enOrden(
                baja,
                "restauracion COTIZADO a ENVIADO",
                "IF NOT EXISTS (",
                "AND rp.baja_fecha IS NULL",
                "SET estado_envio = 'ENVIADO'",
                "AND estado_envio = 'COTIZADO'"
        );

        String reactivar = seccion(
                schema,
                "reactivacion atomica de presupuesto",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.reactivar_requerimiento_presupuesto(",
                "-- REQUERIMIENTO COTIZADO -> RECLAMO PRESTACIONAL"
        );

        minimoOcurrencias(
                reactivar,
                "bloqueos de la reactivacion",
                "FOR UPDATE",
                3
        );
        contiene(
                reactivar,
                "reactivacion solo para requerimiento A COTIZAR",
                "v_estado_requerimiento <> 2"
        );
        contiene(
                reactivar,
                "reactivacion vuelve a comprobar unicidad",
                "rp.id_requerimiento_presupuesto "
                        + "<> p_id_requerimiento_presupuesto"
        );
        enOrden(
                reactivar,
                "reactivacion y estado COTIZADO",
                "SET baja_fecha = NULL",
                "SET estado_envio = 'COTIZADO'"
        );

        verificarCierreCotizacion(schema);
        verificarReservaNotificacion(schema);
    }

    private static void verificarCierreCotizacion(String schema) {
        String trigger = seccion(
                schema,
                "integridad de cierre por trigger",
                "CREATE FUNCTION compras.validar_requerimiento_fila()",
                "CREATE TRIGGER trg_compras_requerimiento_validar"
        );

        String cierreTrigger = seccion(
                trigger,
                "cierre 2 a 3 por trigger",
                "IF OLD.estado = 2 AND NEW.estado = 3 THEN",
                "IF NEW.estado = 99 THEN"
        );

        String cierreTriggerNormalizado = normalizarEspacios(cierreTrigger);

        contiene(
                cierreTriggerNormalizado,
                "cierre exige prestador COTIZADO",
                "rcp.estado_envio = 'COTIZADO'"
        );
        contiene(
                cierreTriggerNormalizado,
                "cierre exige presupuesto activo",
                "FROM compras.requerimiento_presupuesto rp"
        );
        contiene(
                cierreTriggerNormalizado,
                "presupuesto activo en cierre",
                "AND rp.baja_fecha IS NULL"
        );

        String guardarCotizacion = seccion(
                schema,
                "guardado de cotizacion",
                "CREATE FUNCTION compras.guardar_cotizacion_requerimiento(",
                "CREATE FUNCTION "
                        + "compras.listar_prestadores_cotizacion_requerimiento("
        );

        enOrden(
                guardarCotizacion,
                "cierre funcional exige presupuesto y COTIZADO",
                "FROM compras.requerimiento_presupuesto rp",
                "AND rp.baja_fecha IS NULL",
                "FROM compras.requerimiento_cotizacion_prestador rcp",
                "AND rcp.estado_envio = 'COTIZADO'",
                "PERFORM compras.cambiar_estado_requerimiento("
        );
    }

    private static void verificarReservaNotificacion(String schema) {
        String reserva = seccion(
                schema,
                "reserva de reintento de notificacion",
                "compras.reservar_notificacion_cotizacion_prestador(",
                "compras.finalizar_notificacion_cotizacion_prestador("
        );

        String guardaCotizado = seccion(
                reserva,
                "guarda de prestador COTIZADO",
                "IF v_estado_actual = 'COTIZADO' THEN",
                "IF v_estado_actual = 'PROCESANDO' THEN"
        );

        contiene(
                guardaCotizado,
                "reintento informa YA_COTIZADO",
                "'YA_COTIZADO'::TEXT"
        );
        contiene(
                guardaCotizado,
                "reintento COTIZADO termina sin mutar",
                "RETURN;"
        );
        enOrden(
                reserva,
                "guarda COTIZADO anterior a la reserva",
                "IF v_estado_actual = 'COTIZADO' THEN",
                "RETURN;",
                "UPDATE compras.requerimiento_cotizacion_prestador",
                "estado_envio =",
                "'PROCESANDO'"
        );
    }

    private static void verificarMigracion(String migracion) {
        contiene(
                migracion,
                "migracion ejecutable con ON_ERROR_STOP",
                "\\set ON_ERROR_STOP on"
        );
        contiene(
                migracion,
                "orden de despliegue documentado",
                "ORDEN DE DESPLIEGUE"
        );

        String migracionMinusculas = migracion.toLowerCase();
        contiene(
                migracionMinusculas,
                "orden para base existente",
                "base existente"
        );
        contiene(
                migracionMinusculas,
                "despliegue de Java/JSP documentado",
                "java/jsp"
        );
        contiene(
                migracionMinusculas,
                "esquema reservado para instalacion nueva",
                "instalacion nueva"
        );
        contiene(
                migracionMinusculas,
                "esquema canonico identificado",
                "compras_schema.sql"
        );
        antesDe(
                migracionMinusculas,
                "SQL se despliega antes que Java/JSP",
                "base existente",
                "java/jsp"
        );

        String migracionSql = normalizarEspacios(migracion);

        contiene(
                migracionSql,
                "preflight agrupa la clave activa",
                "GROUP BY rp.id_requerimiento, rp.id_prestador"
        );
        contiene(
                migracionSql,
                "preflight detecta duplicados activos",
                "HAVING count(*) > 1"
        );
        contiene(
                migracionSql,
                "preflight aborta con diagnostico",
                "RAISE EXCEPTION"
        );
        contiene(
                migracionMinusculas,
                "diagnostico funcional de duplicados",
                "duplicad"
        );
        antesDe(
                migracion,
                "preflight anterior al indice",
                "HAVING count(*) > 1",
                INDICE_PRESUPUESTO_ACTIVO
        );

        contiene(
                migracionSql,
                "migracion amplia el CHECK",
                "ADD CONSTRAINT ck_compras_cotizacion_estado_envio CHECK"
        );
        contiene(
                migracionSql,
                "migracion admite COTIZADO",
                "'COTIZADO'"
        );
        contiene(
                migracionSql,
                "indice validado por catalogo",
                "FROM pg_index i"
        );
        contiene(
                migracionSql,
                "indice exige unicidad y validez",
                "i.indisunique AND i.indisvalid AND i.indisready "
                        + "AND i.indnatts = 2"
        );
        contiene(
                migracionSql,
                "indice incorrecto se reemplaza",
                "DROP INDEX compras."
                        + INDICE_PRESUPUESTO_ACTIVO
        );
        contiene(
                migracionSql,
                "indice se crea cuando falta",
                "CREATE UNIQUE INDEX " + INDICE_PRESUPUESTO_ACTIVO
        );
        verificarIndiceParcial(
                migracion,
                "indice unico parcial incremental"
        );

        contiene(
                migracion,
                "migracion reemplaza integridad de cierre",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.validar_requerimiento_fila()"
        );
        contiene(
                migracion,
                "migracion reemplaza integridad de detalle",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.validar_requerimiento_detalle_fila()"
        );
        contiene(
                migracion,
                "migracion reemplaza guardado de cotizacion",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.guardar_cotizacion_requerimiento("
        );
        contiene(
                migracion,
                "migracion reemplaza busqueda de prestadores",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.buscar_prestadores_enviados("
        );
        contiene(
                migracion,
                "migracion reemplaza alta de presupuesto",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.registrar_requerimiento_presupuesto("
        );
        contiene(
                migracion,
                "migracion reemplaza baja de presupuesto",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.baja_requerimiento_presupuesto("
        );
        contiene(
                migracion,
                "migracion reemplaza reactivacion de presupuesto",
                "CREATE OR REPLACE FUNCTION "
                        + "compras.reactivar_requerimiento_presupuesto("
        );
        contiene(
                migracion,
                "migracion reemplaza diagnostico de notificacion",
                "compras.diagnosticar_prestadores_notificacion_cotizacion("
        );
        contiene(
                migracion,
                "migracion protege el reintento COTIZADO",
                "compras.reservar_notificacion_cotizacion_prestador("
        );
        enOrden(
                migracion,
                "migracion transaccional completa",
                "BEGIN;",
                "HAVING count(*) > 1",
                INDICE_PRESUPUESTO_ACTIVO,
                "compras.registrar_requerimiento_presupuesto(",
                "compras.baja_requerimiento_presupuesto(",
                "compras.reactivar_requerimiento_presupuesto(",
                "COMMIT;"
        );
    }

    private static void verificarIndiceParcial(
            String contenido,
            String etiqueta) {

        String normalizado = normalizarEspacios(contenido);

        contiene(
                normalizado,
                etiqueta,
                "ON compras.requerimiento_presupuesto ( "
                        + "id_requerimiento, id_prestador ) "
                        + "WHERE baja_fecha IS NULL"
        );
    }

    private static void verificarListadoPersistido(
            String searchImpl,
            String webKeys) {

        contiene(
                webKeys,
                "constante Java COTIZADO",
                "ENVIO_COTIZADO = \"COTIZADO\""
        );

        String sqlListado = seccion(
                searchImpl,
                "SQL de listado de prestadores",
                "private static final String SQL_LISTAR_PRESTADORES_ENVIADOS",
                "private static final String "
                        + "SQL_HAY_PRESTADORES_PENDIENTES_NOTIFICACION"
        );

        contiene(
                sqlListado,
                "listado conserva el id real",
                "SELECT DISTINCT p.id_prestador"
        );
        contiene(
                sqlListado,
                "listado conserva el estado persistido",
                "+ \"rcp.estado_envio \""
        );
        contiene(
                sqlListado,
                "listado parametriza ENVIADO y COTIZADO",
                "rcp.estado_envio IN (?, ?)"
        );

        String metodoListado = seccion(
                searchImpl,
                "metodo de listado de prestadores",
                "public List<PrestadorCotizacion> listarPrestadoresEnviados(",
                "public boolean hayPrestadoresPendientesNotificacion("
        );

        enOrden(
                metodoListado,
                "parametros ENVIADO y COTIZADO",
                "stmt.setString(1, WebKeysCompras.ENVIO_ENVIADO)",
                "stmt.setString(2, WebKeysCompras.ENVIO_COTIZADO)",
                "rs = stmt.executeQuery()",
                "mapPrestadorCotizacion(rs)"
        );
    }

    private static void verificarCarga(String action, String jsp) {
        String validacionServidor = seccion(
                action,
                "validacion de carga en servidor",
                "protected List<PresupuestoValidado> validarPresupuestos(",
                "protected void guardarPresupuestosValidados("
        );

        String validacionServidorNormalizada =
                normalizarEspacios(validacionServidor);

        contiene(
                validacionServidorNormalizada,
                "servidor solo acepta prestadores ENVIADO",
                "WebKeysCompras.ENVIO_ENVIADO.equals("
        );
        contiene(
                validacionServidorNormalizada,
                "servidor registra prestadores seleccionados",
                "Set<Integer> prestadoresSeleccionados"
        );
        contiene(
                validacionServidorNormalizada,
                "servidor rechaza prestador repetido",
                "if (!prestadoresSeleccionados.add("
                        + "idPrestadorSeleccionado))"
        );
        contiene(
                validacionServidorNormalizada,
                "mensaje servidor para prestador repetido",
                "un archivo por prestador"
        );

        String selector = seccion(
                jsp,
                "selector de prestadores disponibles",
                "List<PrestadorCotizacion> "
                        + "prestadoresDisponiblesPresupuestos",
                "PortletURL uploadPresupuestosURL"
        );

        contiene(
                selector,
                "selector solo incluye ENVIADO",
                "WebKeysCompras.ENVIO_ENVIADO.equals("
        );
        contiene(
                selector,
                "maximo limitado por disponibilidad real",
                "Math.min("
        );
        contiene(
                selector,
                "maximo usa cantidad disponible",
                "prestadoresDisponiblesPresupuestos.size()"
        );

        String validacionCliente = seccion(
                jsp,
                "validacion de carga en cliente",
                "function <portlet:namespace />"
                        + "uploadPresupuestoRequerimientoCompra()",
                "function <portlet:namespace />"
                        + "deletePresupuestoRequerimientoCompra("
        );

        contiene(
                validacionCliente,
                "cliente registra prestadores seleccionados",
                "var prestadoresSeleccionados"
        );
        contiene(
                validacionCliente,
                "cliente rechaza prestador repetido",
                "if (prestadoresSeleccionados[prestador])"
        );
        contiene(
                validacionCliente,
                "cliente marca prestador seleccionado",
                "prestadoresSeleccionados[prestador] = true"
        );
        contiene(
                validacionCliente,
                "cliente usa maximo dinamico",
                "> <%= maxPresupuestosCargaActual %>"
        );
    }

    private static void verificarAusenciaDeSincronizacionLocal(
            String action,
            String editImpl) {

        String validacion = seccion(
                action,
                "validacion de carga sin lock local",
                "protected List<PresupuestoValidado> validarPresupuestos(",
                "protected void guardarPresupuestosValidados("
        );
        String guardado = seccion(
                action,
                "guardado de carga sin lock local",
                "protected void guardarPresupuestosValidados(",
                "protected DocumentoPresupuestoCreado "
                        + "crearArchivoPresupuesto("
        );
        String registro = seccion(
                editImpl,
                "registro JDBC sin lock local",
                "public int registrarPresupuesto(",
                "public boolean darDeBajaPresupuesto("
        );

        noContiene(validacion, "validacion sin synchronized", "synchronized");
        noContiene(guardado, "guardado sin synchronized", "synchronized");
        noContiene(registro, "registro JDBC sin synchronized", "synchronized");
    }

    private static void verificarAusenciaDeProyeccionVisual(
            String searchUtil) {

        noContiene(
                searchUtil,
                "sin proyeccion visual de COTIZADO",
                "PrestadorCotizacionConPresupuesto"
        );

        Path proyeccion = Paths.get(
                BASE + "requerimientos/beans/"
                        + "PrestadorCotizacionConPresupuesto.java"
        );

        if (Files.exists(proyeccion)) {
            throw new AssertionError("La proyeccion visual no debe existir.");
        }
    }

    private static String leerArchivoObligatorio(String ruta)
            throws Exception {

        Path archivo = Paths.get(ruta);

        if (!Files.isRegularFile(archivo)) {
            throw new AssertionError(
                    "No existe el script incremental obligatorio: " + ruta
            );
        }

        return leer(ruta);
    }

    private static String leer(String ruta) throws Exception {
        String contenido = new String(
                Files.readAllBytes(Paths.get(ruta)),
                ISO_8859_1
        );

        return contenido.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String seccion(
            String contenido,
            String etiqueta,
            String inicio,
            String fin) {

        int posicionInicio = contenido.indexOf(inicio);

        if (posicionInicio < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontro el inicio [" + inicio + "]"
            );
        }

        int posicionFin = contenido.indexOf(
                fin,
                posicionInicio + inicio.length()
        );

        if (posicionFin < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontro el fin [" + fin + "]"
            );
        }

        return contenido.substring(posicionInicio, posicionFin);
    }

    private static String normalizarEspacios(String contenido) {
        return contenido.replaceAll("\\s+", " ").trim();
    }

    private static void contiene(
            String contenido,
            String etiqueta,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontro [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String contenido,
            String etiqueta,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontro [" + prohibido + "]"
            );
        }
    }

    private static void minimoOcurrencias(
            String contenido,
            String etiqueta,
            String buscado,
            int minimo) {

        int cantidad = 0;
        int posicion = 0;

        while ((posicion = contenido.indexOf(buscado, posicion)) >= 0) {
            cantidad++;
            posicion += buscado.length();
        }

        if (cantidad < minimo) {
            throw new AssertionError(
                    etiqueta + ": se esperaban al menos " + minimo
                            + " ocurrencias de [" + buscado + "] y hubo "
                            + cantidad
            );
        }
    }

    private static void antesDe(
            String contenido,
            String etiqueta,
            String primero,
            String segundo) {

        enOrden(contenido, etiqueta, primero, segundo);
    }

    private static void enOrden(
            String contenido,
            String etiqueta,
            String... esperados) {

        int posicion = 0;

        for (int i = 0; i < esperados.length; i++) {
            int encontrada = contenido.indexOf(esperados[i], posicion);

            if (encontrada < 0) {
                throw new AssertionError(
                        etiqueta + ": no se encontro en orden ["
                                + esperados[i] + "]"
                );
            }

            posicion = encontrada + esperados[i].length();
        }
    }
}
