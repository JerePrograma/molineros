package ar.com.ospim.compras.requerimientos.helper;

import java.sql.Connection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

/** Configuracion de Compras leida en cada operacion, sin cache. */
public final class ConfiguracionComprasHelper {
    public static final String CONFIG_CONCEPTOS_AUTOMATICOS = "COMPRAS_CONCEPTOS_AUTOMATICOS";
    public static final String CONFIG_RUBROS_CONCEPTO_AUTOMATICO = "COMPRAS_RUBROS_CONCEPTO_AUTOMATICO";
    public static final String CONFIG_CANTIDAD_CONCEPTO_AUTOMATICO = "COMPRAS_CANTIDAD_CONCEPTO_AUTOMATICO";

    private ConfiguracionComprasHelper() {
    }

    public static String leerObligatoria(String clave) {
        try {
            return exigirValor(clave, BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave));
        } catch (Exception e) {
            throw new ConfiguracionInvalidaException(clave, e);
        }
    }

    public static String leerObligatoria(String clave, Connection con) {
        if (con == null) {
            throw new IllegalArgumentException("La conexion de la operacion es obligatoria.");
        }
        try {
            return exigirValor(clave, BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave, con));
        } catch (Exception e) {
            throw new ConfiguracionInvalidaException(clave, e);
        }
    }

    private static ConfiguracionInvalidaException invalida(String clave) {
        return new ConfiguracionInvalidaException(clave);
    }

    public static String exigirValor(String clave, String valor) {
        if (valor == null || valor.trim().length() == 0) {
            throw invalida(clave);
        }
        return valor.trim();
    }

    public static Map<String, String> parsearPares(String clave, String valor) {
        String[] tokens = exigirValor(clave, valor).split(";", -1);
        Map<String, String> resultado = new LinkedHashMap<String, String>();
        for (int i = 0; i < tokens.length; i++) {
            int separador = tokens[i].indexOf('=');
            if (separador <= 0 || separador != tokens[i].lastIndexOf('=')) {
                throw invalida(clave);
            }
            String codigo = tokens[i].substring(0, separador).trim();
            String contenido = tokens[i].substring(separador + 1).trim();
            if (codigo.length() == 0 || contenido.length() == 0 || resultado.containsKey(codigo)) {
                throw invalida(clave);
            }
            resultado.put(codigo, contenido);
        }
        return resultado;
    }

    public static int parsearEntero(String clave, String valor, boolean permiteCero) {
        String texto = exigirValor(clave, valor);
        if (!texto.matches("[0-9]+")) {
            throw invalida(clave);
        }
        try {
            int numero = Integer.parseInt(texto);
            if (numero < 0 || (!permiteCero && numero == 0)) {
                throw invalida(clave);
            }
            return numero;
        } catch (NumberFormatException e) {
            throw invalida(clave);
        }
    }

    private static int entero(String clave, boolean permiteCero) {
        return parsearEntero(clave, leerObligatoria(clave), permiteCero);
    }

    private static String valorMapa(String clave, String codigo) {
        Map<String, String> valores = parsearPares(clave, leerObligatoria(clave));
        String valor = valores.get(codigo);
        if (valor == null) {
            throw invalida(clave);
        }
        return valor;
    }

    private static int enteroMapa(String clave, String codigo, boolean permiteCero) {
        Map<String, String> valores = parsearPares(clave, leerObligatoria(clave));
        for (String valor : valores.values()) {
            parsearEntero(clave, valor, permiteCero);
        }
        String valor = valores.get(codigo);
        if (valor == null) {
            throw invalida(clave);
        }
        return parsearEntero(clave, valor, permiteCero);
    }

    public static boolean contieneEntero(String clave, String valor, int buscado) {
        String[] tokens = exigirValor(clave, valor).split(";", -1);
        boolean contiene = false;
        for (int i = 0; i < tokens.length; i++) {
            int numero = parsearEntero(clave, tokens[i], false);
            contiene = contiene || numero == buscado;
        }
        return contiene;
    }

    private static boolean contieneSector(String clave, String sectorDescripcion) {
        return contieneSectorValor(clave, sectorDescripcion, leerObligatoria(clave));
    }

    private static boolean contieneSectorValor(String clave, String sectorDescripcion, String valor) {
        String[] tokens = exigirValor(clave, valor).split(";", -1);
        String sector = WebKeysCompras.normalizarSectorCompra(sectorDescripcion);
        boolean coincide = false;
        for (int i = 0; i < tokens.length; i++) {
            String token = exigirValor(clave, tokens[i]);
            if (!token.equals(WebKeysCompras.normalizarSectorCompra(token))) {
                throw invalida(clave);
            }
            coincide = coincide || token.equals(sector);
        }
        return coincide;
    }

    public static Map<String, String> getSectoresReclamoPrestacional() {
        String clave = "COMPRAS_SECTORES_RP";
        Map<String, String> valores = parsearPares(clave, leerObligatoria(clave));
        for (String sector : valores.keySet()) {
            if (!sector.equals(WebKeysCompras.normalizarSectorCompra(sector))) {
                throw invalida(clave);
            }
        }
        return valores;
    }

    public static String getSectorReclamoPrestacional(String descripcion) {
        String sector = getSectoresReclamoPrestacional().get(WebKeysCompras.normalizarSectorCompra(descripcion));
        return sector != null ? sector : "";
    }

    public static boolean esSectorDetalleObservacion(String descripcion) {
        return contieneSector("COMPRAS_SECTORES_DETALLE_OBSERVACION", descripcion);
    }

    public static boolean esSectorSinCotizacionPrestador(String descripcion) {
        return contieneSector("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR", descripcion);
    }

    public static boolean compararBusquedaSinAcentos(String descripcion) {
        return contieneSector("COMPRAS_SECTORES_BUSQUEDA_SIN_ACENTOS", descripcion);
    }

    public static Integer getFiltroTipoNomenclador(String descripcion) {
        String clave = "COMPRAS_FILTROS_NOMENCLADOR";
        Map<String, String> valores = parsearPares(clave, leerObligatoria(clave));
        for (String valor : valores.values()) {
            parsearEntero(clave, valor, true);
        }
        String valor = valores.get(WebKeysCompras.normalizarSectorCompra(descripcion));
        return valor != null ? Integer.valueOf(parsearEntero(clave, valor, true)) : null;
    }

    public static String getSectorDescripcionVisible(String descripcion) {
        Map<String, String> valores = parsearPares("COMPRAS_SECTORES_DESCRIPCION_VISIBLE", leerObligatoria("COMPRAS_SECTORES_DESCRIPCION_VISIBLE"));
        String visible = valores.get(WebKeysCompras.normalizarSectorCompra(descripcion));
        return visible != null ? visible : WebKeysCompras.normalizarTexto(descripcion).toUpperCase(java.util.Locale.ROOT);
    }

    public static int getTipoNomencladorFarmacia() {
        return entero("COMPRAS_TIPO_NOMENCLADOR_FARMACIA", false);
    }

    public static Integer resolverConceptoAutomatico(int sector, String valor) {
        return resolverRelacionAutomatica(CONFIG_CONCEPTOS_AUTOMATICOS, sector, valor);
    }

    public static Integer resolverRubroConceptoAutomatico(int sector, String valor) {
        return resolverRelacionAutomatica(CONFIG_RUBROS_CONCEPTO_AUTOMATICO, sector, valor);
    }

    private static Integer resolverRelacionAutomatica(String clave, int sector, String valor) {
        Map<String, String> valores = parsearPares(clave, valor);
        for (Map.Entry<String, String> entrada : valores.entrySet()) {
            parsearEntero(clave, entrada.getKey(), false);
            parsearEntero(clave, entrada.getValue(), true);
        }
        String configurado = valores.get(String.valueOf(sector));
        if (configurado == null) {
            throw invalida(clave);
        }
        int id = parsearEntero(clave, configurado, true);
        return id == 0 ? null : Integer.valueOf(id);
    }

    public static int resolverCantidadConceptoAutomatico(String valor) {
        return parsearEntero(CONFIG_CANTIDAD_CONCEPTO_AUTOMATICO, valor, false);
    }

    public static Integer getConceptoAutomatico(int sector) {
        return resolverConceptoAutomatico(sector, leerObligatoria(CONFIG_CONCEPTOS_AUTOMATICOS));
    }

    public static Integer getRubroConceptoAutomatico(int sector) {
        return resolverRubroConceptoAutomatico(sector, leerObligatoria(CONFIG_RUBROS_CONCEPTO_AUTOMATICO));
    }

    public static int getCantidadConceptoAutomatico() {
        return resolverCantidadConceptoAutomatico(leerObligatoria(CONFIG_CANTIDAD_CONCEPTO_AUTOMATICO));
    }

    public static Integer getConceptoAutomatico(int sector, Connection con) {
        return resolverConceptoAutomatico(sector, leerObligatoria(CONFIG_CONCEPTOS_AUTOMATICOS, con));
    }

    public static Integer getRubroConceptoAutomatico(int sector, Connection con) {
        return resolverRubroConceptoAutomatico(sector, leerObligatoria(CONFIG_RUBROS_CONCEPTO_AUTOMATICO, con));
    }

    public static int getCantidadConceptoAutomatico(Connection con) {
        return resolverCantidadConceptoAutomatico(leerObligatoria(CONFIG_CANTIDAD_CONCEPTO_AUTOMATICO, con));
    }

    public static final String CONFIG_ESTADOS_IDS = "COMPRAS_ESTADOS_IDS";
    public static final String CONFIG_ESTADOS_ENVIO = "COMPRAS_ESTADOS_ENVIO";

    private static Map<String, Integer> resolverIdsEstados(String valor) {
        Map<String, String> estados = parsearPares(CONFIG_ESTADOS_IDS, valor);
        Map<String, Integer> resultado = new LinkedHashMap<String, Integer>();
        List<Integer> ids = new ArrayList<Integer>();
        for (Map.Entry<String, String> entrada : estados.entrySet()) {
            if (!entrada.getKey().matches("[A-Z_]+")) {
                throw invalida(CONFIG_ESTADOS_IDS);
            }
            Integer id = Integer.valueOf(parsearEntero(CONFIG_ESTADOS_IDS, entrada.getValue(), false));
            if (ids.contains(id)) {
                throw invalida(CONFIG_ESTADOS_IDS);
            }
            ids.add(id);
            resultado.put(entrada.getKey(), id);
        }
        return resultado;
    }

    public static int resolverEstado(String codigo, String valor) {
        Integer id = resolverIdsEstados(valor).get(codigo);
        if (id == null) {
            throw invalida(CONFIG_ESTADOS_IDS);
        }
        return id.intValue();
    }

    public static int getEstado(String codigo) {
        return resolverEstado(codigo, leerObligatoria(CONFIG_ESTADOS_IDS));
    }

    public static String resolverEstadoEnvio(String codigo, String valor) {
        Map<String, String> estados = parsearPares(CONFIG_ESTADOS_ENVIO, valor);
        List<String> ids = new ArrayList<String>();
        for (Map.Entry<String, String> entrada : estados.entrySet()) {
            if (!entrada.getKey().matches("[A-Z_]+") || !entrada.getValue().matches("[A-Z_]{1,20}")
                    || ids.contains(entrada.getValue())) {
                throw invalida(CONFIG_ESTADOS_ENVIO);
            }
            ids.add(entrada.getValue());
        }
        String id = estados.get(codigo);
        if (id == null) {
            throw invalida(CONFIG_ESTADOS_ENVIO);
        }
        return id;
    }

    public static String getEstadoEnvio(String codigo) {
        return resolverEstadoEnvio(codigo, leerObligatoria(CONFIG_ESTADOS_ENVIO));
    }

    private static Map<Integer, String> leerEstadosDescripciones() {
        String clave = "COMPRAS_ESTADOS_DESCRIPCIONES";
        Map<String, String> valores = parsearPares(clave, leerObligatoria(clave));
        Map<Integer, String> estados = new LinkedHashMap<Integer, String>();
        for (Map.Entry<String, String> entrada : valores.entrySet()) {
            Integer id = Integer.valueOf(parsearEntero(clave, entrada.getKey(), false));
            if (estados.put(id, entrada.getValue()) != null) {
                throw invalida(clave);
            }
        }
        return estados;
    }

    public static List<RequerimientoCompraEstado> listarEstados() {
        Map<Integer, String> valores = leerEstadosDescripciones();
        List<RequerimientoCompraEstado> estados = new ArrayList<RequerimientoCompraEstado>();
        for (Map.Entry<Integer, String> entrada : valores.entrySet()) {
            RequerimientoCompraEstado estado = new RequerimientoCompraEstado();
            estado.setId(entrada.getKey());
            estado.setDescripcion(entrada.getValue());
            estados.add(estado);
        }
        return estados;
    }

    public static String getEstadoDescripcion(int id) {
        String descripcion = leerEstadosDescripciones().get(Integer.valueOf(id));
        return descripcion != null ? descripcion : "";
    }

    public static boolean esEstadoValido(int id) {
        return leerEstadosDescripciones().containsKey(Integer.valueOf(id));
    }

    public static boolean resolverEstadoPermitido(String operacion, int estado, String valor) {
        return contieneEntero("COMPRAS_ESTADOS_" + operacion, valor, estado);
    }

    public static boolean estadoPermitido(String operacion, int estado) {
        String clave = "COMPRAS_ESTADOS_" + operacion;
        return resolverEstadoPermitido(operacion, estado, leerObligatoria(clave));
    }

    public static boolean resolverTransicionEstado(int actual, int nuevo,
            String valorTransiciones, String valorEstadosIds) {
        String clave = "COMPRAS_TRANSICIONES_ESTADO";
        Map<String, Integer> estados = resolverIdsEstados(valorEstadosIds);
        Map<String, String> transiciones = parsearPares(clave, valorTransiciones);
        boolean permitida = false;
        for (Map.Entry<String, String> entrada : transiciones.entrySet()) {
            int origen = parsearEntero(clave, entrada.getKey(), false);
            if (!estados.containsValue(Integer.valueOf(origen))) {
                throw invalida(clave);
            }
            String[] destinos = entrada.getValue().split(",", -1);
            for (int i = 0; i < destinos.length; i++) {
                int destino = parsearEntero(clave, destinos[i], false);
                if (!estados.containsValue(Integer.valueOf(destino))) {
                    throw invalida(clave);
                }
                permitida = permitida || (origen == actual && destino == nuevo && actual != nuevo);
            }
        }
        return estados.containsValue(Integer.valueOf(actual))
                && estados.containsValue(Integer.valueOf(nuevo)) && permitida;
    }

    public static boolean validarTransicionEstado(int actual, int nuevo) {
        return resolverTransicionEstado(actual, nuevo,
                leerObligatoria("COMPRAS_TRANSICIONES_ESTADO"), leerObligatoria(CONFIG_ESTADOS_IDS));
    }

    public static int getRecuperableSur() {
        return entero("COMPRAS_RP_RECUPERABLE_SUR", false);
    }

    public static int getNoRecuperable() {
        return entero("COMPRAS_RP_NO_RECUPERABLE", false);
    }

    public static int getRecuperableIntegracion() {
        return entero("COMPRAS_RP_RECUPERABLE_INTEGRACION", false);
    }

    public static int getEstadoPrestacionInicialRP() {
        return entero("COMPRAS_RP_ESTADO_PRESTACION_INICIAL", true);
    }

    public static int getTipoPrestacionInicialRP() {
        return entero("COMPRAS_RP_TIPO_PRESTACION_INICIAL", false);
    }

    public static int getMaxObservacionRP() {
        return entero("COMPRAS_RP_MAX_OBSERVACION", false);
    }

    public static String getFrecuenciaInicialRP() {
        return leerObligatoria("COMPRAS_RP_FRECUENCIA_INICIAL");
    }

    public static String getComprobanteInicialRP() {
        return leerObligatoria("COMPRAS_RP_COMPROBANTE_INICIAL");
    }

    public static String getDescripcionNoRecuperableRP() {
        return leerObligatoria("COMPRAS_RP_DESCRIPCION_NO_RECUPERABLE");
    }

    public static long getVigenciaContextoMillis() {
        String clave = "COMPRAS_RP_VIGENCIA_CONTEXTO_MILLIS";
        String valor = leerObligatoria(clave);
        if (!valor.matches("[0-9]+")) {
            throw invalida(clave);
        }
        try {
            long vigencia = Long.parseLong(valor);
            if (vigencia <= 0) {
                throw invalida(clave);
            }
            return vigencia;
        } catch (NumberFormatException e) {
            throw invalida(clave);
        }
    }

    public static long getGrupoDocumentLibraryLegacy() {
        String clave = "COMPRAS_DL_GRUPO_LEGACY";
        String valor = leerObligatoria(clave);
        if (!valor.matches("[0-9]+")) {
            throw invalida(clave);
        }
        try {
            long grupo = Long.parseLong(valor);
            if (grupo <= 0) {
                throw invalida(clave);
            }
            return grupo;
        } catch (NumberFormatException e) {
            throw invalida(clave);
        }
    }

    public static String getRolViewCompras() {
        return valorMapa("COMPRAS_ROLES", "VIEW");
    }

    public static String getRolAbmCompras() {
        return valorMapa("COMPRAS_ROLES", "ABM");
    }

    public static String getRolAnularCompras() {
        return valorMapa("COMPRAS_ROLES", "ANULAR");
    }

    public static String getRolCotizarCompras() {
        return valorMapa("COMPRAS_ROLES", "COTIZAR");
    }

    public static String getRolAutorizarCompras() {
        return valorMapa("COMPRAS_ROLES", "AUTORIZAR");
    }

    public static int getMaxPresupuestosPorCarga() {
        return enteroMapa("COMPRAS_LIMITES", "PRESUPUESTOS_POR_CARGA", false);
    }

    public static int getMaxPrestadoresEnviados() {
        return enteroMapa("COMPRAS_LIMITES", "PRESTADORES_ENVIADOS", false);
    }

    public static int getMaxExtensionDocumento() {
        return enteroMapa("COMPRAS_LIMITES", "DL_EXTENSION", false);
    }

    public static int getMaxTituloDocumento() {
        return enteroMapa("COMPRAS_LIMITES", "DL_TITULO", false);
    }

    public static int getEstadoPendiente() {
        return getEstado("PENDIENTE");
    }

    public static int getEstadoACotizar() {
        return getEstado("A_COTIZAR");
    }

    public static int getEstadoCotizado() {
        return getEstado("COTIZADO");
    }

    public static int getEstadoReclamoRP() {
        return getEstado("RECLAMO_RP");
    }

    public static int getEstadoOrdenCompra() {
        return getEstado("ORDEN_COMPRA");
    }

    public static int getEstadoParaAutorizar() {
        return getEstado("PARA_AUTORIZAR");
    }

    public static int getEstadoAutorizado() {
        return getEstado("AUTORIZADO");
    }

    public static int getEstadoAnulado() {
        return getEstado("ANULADO");
    }

    public static boolean esPendiente(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("PENDIENTE");
    }

    public static boolean esACotizar(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("A_COTIZAR");
    }

    public static boolean esCotizado(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("COTIZADO");
    }

    public static boolean esReclamoRP(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("RECLAMO_RP");
    }

    public static boolean esParaAutorizar(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("PARA_AUTORIZAR");
    }

    public static boolean esAutorizado(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("AUTORIZADO");
    }

    public static boolean puedeEnviarAAutorizar(
            int estado,
            String sectorDescripcion,
            boolean hayDetalles,
            boolean hayCotizacionesEmpresa) {

        if (esSectorSinCotizacionPrestador(sectorDescripcion)) {
            return esPendiente(estado) && hayDetalles && hayCotizacionesEmpresa;
        }
        return puedeEnviarAAutorizar(estado);
    }

    public static boolean puedeEnviarAAutorizar(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("ENVIAR_AUTORIZAR", estado);
    }

    public static boolean puedeConsultarReclamoPrestacional(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("CONSULTAR_RP", estado);
    }

    public static boolean esOrdenCompra(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("ORDEN_COMPRA");
    }

    public static boolean esAnulado(int estado) {
        return estado == ConfiguracionComprasHelper.getEstado("ANULADO");
    }

    public static boolean puedeCorregirItem(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("CORREGIR_ITEM", estado);
    }

    public static boolean puedeEditarEstructura(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("EDITAR_ESTRUCTURA", estado);
    }

    public static boolean puedeEliminarDetalle(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("ELIMINAR_DETALLE", estado);
    }

    public static boolean puedeEditarSurge(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("EDITAR_SURGE", estado);
    }

    public static boolean puedeEditarCotizacion(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("EDITAR_COTIZACION", estado);
    }

    public static boolean puedeAdministrarPresupuestos(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("ADMINISTRAR_PRESUPUESTOS", estado);
    }

    public static boolean puedeVerCotizacion(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("VER_COTIZACION", estado);
    }

    public static boolean puedeVerPresupuestos(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("VER_PRESUPUESTOS", estado);
    }

    public static boolean puedeEnviarACotizar(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("ENVIAR_COTIZAR", estado);
    }

    public static boolean puedeReintentarNotificaciones(
            int estado,
            boolean hayPrestadoresPendientes) {

        return puedeReintentarNotificaciones(estado)
                && hayPrestadoresPendientes;
    }

    public static boolean puedeReintentarNotificaciones(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("REINTENTAR_NOTIFICACIONES", estado);
    }

    public static boolean puedePasarAOrdenCompra(
            int estado,
            String sectorDescripcion,
            boolean hayDetalles,
            boolean hayCotizacionesEmpresa) {

        return esAutorizado(estado)
                && esSectorSinCotizacionPrestador(
                        sectorDescripcion
                )
                && hayDetalles
                && hayCotizacionesEmpresa;
    }

    public static boolean puedeAnular(
            int estado,
            boolean reclamoPrestacionalDeBaja) {

        return puedeAnular(estado)
                || (esReclamoRP(estado) && reclamoPrestacionalDeBaja);
    }

    public static boolean puedeAnular(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("ANULAR", estado);
    }

    public static boolean esSoloLectura(int estado) {
        return ConfiguracionComprasHelper.estadoPermitido("SOLO_LECTURA", estado);
    }

    public static boolean puedeCambiarEstado(
            int estadoActual,
            int estadoNuevo) {

        return validarTransicionEstado(
                estadoActual,
                estadoNuevo
        );
    }

    public static boolean validarTransicionEstado(
            int estadoActual,
            int estadoNuevo,
            String sectorDescripcion) {

        if (esParaAutorizar(estadoNuevo)) {
            return puedeEnviarAAutorizar(
                    estadoActual, sectorDescripcion, true, true);
        }
        return validarTransicionEstado(estadoActual, estadoNuevo);
    }

    public static String getSucursalComprobanteInicialRP() {
        String clave = "COMPRAS_RP_SUCURSAL_INICIAL";
        String valor = leerObligatoria(clave);
        if (!valor.matches("[0-9]+")) {
            throw invalida(clave);
        }
        return valor;
    }

    public static final String CONFIG_ESTADOS_FINALES_NOTIFICACION = "COMPRAS_ENVIO_ESTADOS_FINALES";
    public static final String CONFIG_ESTADOS_REINTENTO_NOTIFICACION = "COMPRAS_ENVIO_ESTADOS_REINTENTO";

    private static List<String> resolverEstadosNotificacion(String clave, String valor) {
        String[] tokens = exigirValor(clave, valor).split(";", -1);
        List<String> estados = new ArrayList<String>();
        for (int i = 0; i < tokens.length; i++) {
            String estado = tokens[i].trim();
            if (!estado.matches("[A-Z_]+") || estados.contains(estado)) {
                throw invalida(clave);
            }
            estados.add(estado);
        }
        return estados;
    }

    public static List<String> resolverEstadosFinalesNotificacion(String valor) {
        return resolverEstadosNotificacion(CONFIG_ESTADOS_FINALES_NOTIFICACION, valor);
    }

    public static List<String> resolverEstadosReintentoNotificacion(String valor) {
        return resolverEstadosNotificacion(CONFIG_ESTADOS_REINTENTO_NOTIFICACION, valor);
    }

    public static List<String> getEstadosFinalesNotificacion() {
        return resolverEstadosFinalesNotificacion(leerObligatoria(CONFIG_ESTADOS_FINALES_NOTIFICACION));
    }

    public static List<String> getEstadosReintentoNotificacion() {
        return resolverEstadosReintentoNotificacion(leerObligatoria(CONFIG_ESTADOS_REINTENTO_NOTIFICACION));
    }

    public static final String CONFIG_TIPOS_CONTACTO_EMAIL = "COMPRAS_CONTACTO_TIPOS_EMAIL";
    public static final String CONFIG_PRIORIDADES_CONTACTO_EMAIL = "COMPRAS_CONTACTO_PRIORIDADES_EMAIL";
    public static final String CONFIG_MAX_ERROR_NOTIFICACION = "COMPRAS_MAX_ERROR_NOTIFICACION";

    public static List<String> resolverTiposContactoEmail(String valor) {
        return resolverEstadosNotificacion(CONFIG_TIPOS_CONTACTO_EMAIL, valor);
    }

    public static Map<String, Integer> resolverPrioridadesContactoEmail(String valor) {
        Map<String, String> datos = parsearPares(CONFIG_PRIORIDADES_CONTACTO_EMAIL, valor);
        Map<String, Integer> prioridades = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, String> entrada : datos.entrySet()) {
            if (!entrada.getKey().matches("[A-Z_]+")) {
                throw invalida(CONFIG_PRIORIDADES_CONTACTO_EMAIL);
            }
            prioridades.put(entrada.getKey(), Integer.valueOf(parsearEntero(
                    CONFIG_PRIORIDADES_CONTACTO_EMAIL, entrada.getValue(), false)));
        }
        return prioridades;
    }

    public static int resolverMaxErrorNotificacion(String valor) {
        return parsearEntero(CONFIG_MAX_ERROR_NOTIFICACION, valor, false);
    }

    public static boolean puedeGenerarReclamoPrestacional(String sectorDescripcion) {
        return getSectorReclamoPrestacional(sectorDescripcion).length() > 0;
    }

    public static int getMaxItemsHistoricosAfiliado() {
        return enteroMapa("COMPRAS_LIMITES", "ITEMS_HISTORICOS_AFILIADO", false);
    }

    public static int getMaxOrdenesMedicasPorCarga() {
        return enteroMapa("COMPRAS_LIMITES", "ORDENES_MEDICAS_POR_CARGA", false);
    }

    public static int resolverLimite(String codigo, String valor) {
        String clave = "COMPRAS_LIMITES";
        Map<String, String> valores = parsearPares(clave, valor);
        for (String contenido : valores.values()) {
            parsearEntero(clave, contenido, false);
        }
        String limite = valores.get(codigo);
        if (limite == null) {
            throw invalida(clave);
        }
        return parsearEntero(clave, limite, false);
    }

    public static int getRequerimientosPorPagina() {
        return enteroMapa("COMPRAS_LIMITES", "REQUERIMIENTOS_POR_PAGINA", false);
    }

    public static int getCantidadDetalleInicial() {
        return entero("COMPRAS_CANTIDAD_DETALLE_INICIAL", false);
    }

    public static boolean resolverSectorSinCotizacionPrestador(String descripcion, String valor) {
        return contieneSectorValor("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR", descripcion, valor);
    }

    public static boolean resolverSectorDetalleObservacion(String descripcion, String valor) {
        return contieneSectorValor("COMPRAS_SECTORES_DETALLE_OBSERVACION", descripcion, valor);
    }

    public static boolean resolverCompararBusquedaSinAcentos(String descripcion, String valor) {
        return contieneSectorValor("COMPRAS_SECTORES_BUSQUEDA_SIN_ACENTOS", descripcion, valor);
    }

    /** Error seguro de configuracion; la causa tecnica solo se registra en backend. */
    public static class ConfiguracionInvalidaException extends IllegalStateException {
        private static final long serialVersionUID = 1L;
        private final String clave;

        public ConfiguracionInvalidaException(String clave) {
            super("Configuracion de Compras no disponible o invalida: " + clave + ".");
            this.clave = clave;
        }

        public ConfiguracionInvalidaException(String clave, Throwable causa) {
            super("Configuracion de Compras no disponible o invalida: " + clave + ".", causa);
            this.clave = clave;
        }

        public String getClave() {
            return clave;
        }
    }

    public static final String CONFIG_OC_MAX_IMPORTE = "COMPRAS_OC_MAX_IMPORTE";

    public static boolean resolverSectorSeleccionable(String descripcion, String valorExcluidos) {
        return !contieneSectorValor("COMPRAS_SECTORES_NO_SELECCIONABLES", descripcion, valorExcluidos);
    }

    public static BigDecimal resolverMaxImporteOrdenCompra(String valor) {
        String texto = exigirValor(CONFIG_OC_MAX_IMPORTE, valor);
        if (!texto.matches("[0-9]+(\\.[0-9]{1,2})?")) {
            throw invalida(CONFIG_OC_MAX_IMPORTE);
        }
        try {
            BigDecimal limite = new BigDecimal(texto);
            // Techo estructural de numeric(18,2), confirmado en el catalogo.
            if (limite.signum() <= 0 || limite.compareTo(new BigDecimal("9999999999999999.99")) > 0) {
                throw invalida(CONFIG_OC_MAX_IMPORTE);
            }
            return limite;
        } catch (NumberFormatException error) {
            throw invalida(CONFIG_OC_MAX_IMPORTE);
        }
    }

    public static BigDecimal getMaxImporteOrdenCompra() {
        return resolverMaxImporteOrdenCompra(leerObligatoria(CONFIG_OC_MAX_IMPORTE));
    }

    public static List<String> resolverListaOpciones(String clave, String valor) {
        String[] tokens = exigirValor(clave, valor).split(";", -1);
        List<String> opciones = new ArrayList<String>();
        for (int i = 0; i < tokens.length; i++) {
            for (int j = 0; j < tokens[i].length(); j++) {
                char caracter = tokens[i].charAt(j);
                if (caracter < 32 || caracter == 127) {
                    throw invalida(clave);
                }
            }
            String opcion = tokens[i].trim();
            if (opcion.length() == 0 || opciones.contains(opcion)) {
                throw invalida(clave);
            }
            opciones.add(opcion);
        }
        return opciones;
    }

    public static List<String> getListaOpciones(String clave) {
        return resolverListaOpciones(clave, leerObligatoria(clave));
    }
}
