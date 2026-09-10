<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%--
Responsabilidad:
    Recupera atributos request e incluye datos básicos en contexto runtime.
Incluido desde:
    requerimiento_compra_ensamblado.jsp, requerimiento_compra_ensamblado.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Atributos request publicados por requerimiento_compra_contexto_publicacion_componente.jsp.
Atributos de request consumidos:
    Claves compras.requerimiento.* leídas por requerimiento_compra_runtime_inicializacion_componente.jsp.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>

<%--
Requiere el requerimiento, sectores, permisos y valores visibles de cabecera.
--%>
<%
RequerimientoCompra req =
        (RequerimientoCompra) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_MODELO
        );

boolean esNuevo =
        Boolean.TRUE.equals(
                request.getAttribute(
                        WebKeysCompras.ATTR_REQUERIMIENTO_ES_NUEVO
                )
        );

String tituloPantalla =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_TITULO
        );

boolean puedeEditarEstructuraPantalla =
        Boolean.TRUE.equals(
                request.getAttribute(
                        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_ESTRUCTURA
                )
        );

boolean puedeEditarSurgePantalla =
        Boolean.TRUE.equals(
                request.getAttribute(
                        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_SURGE
                )
        );

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_SECTORES
        );

String reqSectorId =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_SECTOR_ID
        );

String sectorDescripcionSoloLectura =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_SECTOR_DESCRIPCION
        );

String cargoOspimVisible =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_CARGO_OSPIM
        );

String cargoTercerizadoraVisible =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_CARGO_TERCERIZADORA
        );

String surgeSeleccionadoCompra =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_SURGE_SELECCIONADO
        );

String idTercerizadora =
        (String) request.getAttribute(
                WebKeysCompras.ATTR_REQUERIMIENTO_ID_TERCERIZADORA
        );
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_datos_basicos_componente.jsp" %>
