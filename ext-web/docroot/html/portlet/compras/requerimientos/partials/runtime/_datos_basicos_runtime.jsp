<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_init.jsp" %>

<%--
Requiere el requerimiento, sectores, permisos y valores visibles de cabecera.
--%>
<%
RequerimientoCompra req =
        (RequerimientoCompra) request.getAttribute(
                "compras.requerimiento.req"
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

<%@ include file="/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp" %>