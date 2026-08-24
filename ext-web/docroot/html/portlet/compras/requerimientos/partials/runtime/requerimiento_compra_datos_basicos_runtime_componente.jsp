<%--
Responsabilidad:
    Recupera atributos request e incluye datos básicos en contexto runtime.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp
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
                "compras.requerimiento.req"
        );

boolean esNuevo =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "compras.requerimiento.esNuevo"
                )
        );

String tituloPantalla =
        (String) request.getAttribute(
                "compras.requerimiento.titulo"
        );

boolean puedeEditarEstructuraPantalla =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "compras.requerimiento.puedeEditarEstructura"
                )
        );

boolean puedeEditarSurgePantalla =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "compras.requerimiento.puedeEditarSurge"
                )
        );

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) request.getAttribute(
                "compras.requerimiento.sectores"
        );

String reqSectorId =
        (String) request.getAttribute(
                "compras.requerimiento.reqSectorId"
        );

String sectorDescripcionSoloLectura =
        (String) request.getAttribute(
                "compras.requerimiento.sectorDescripcion"
        );

String cargoOspimVisible =
        (String) request.getAttribute(
                "compras.requerimiento.cargoOspim"
        );

String cargoTercerizadoraVisible =
        (String) request.getAttribute(
                "compras.requerimiento.cargoTercerizadora"
        );

String surgeSeleccionadoCompra =
        (String) request.getAttribute(
                "compras.requerimiento.surgeSeleccionado"
        );

String idTercerizadora =
        (String) request.getAttribute(
                "compras.requerimiento.idTercerizadora"
        );
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_datos_basicos_componente.jsp" %>
