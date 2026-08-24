<%--
Responsabilidad:
    Ensambla alta y edición preservando el orden funcional de componentes y scripts.
Incluido desde:
    requerimiento_compra_alta.jsp, requerimiento_compra_edicion.jsp
Pantallas o estados de uso:
    Alta y PENDIENTE; ENVIADO A COTIZAR sólo donde la capacidad publicada lo permite.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    compras_layout
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_estilos_componente.jsp" %>
<jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_mensajes_runtime_componente.jsp" />

<% if (modoEditable) { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_campos_ocultos_formulario_componente.jsp" %>
<% } %>

<div id="<portlet:namespace />compras_layout"
     class="compras-formulario-requerimiento <%= !modoEditable ? "compras-modo-vista" : "" %>">

    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_datos_basicos_runtime_componente.jsp" />

    <%--
        Se usa el mismo componente de afiliado en ALTA, EDICION y VISTA.
        El propio partial decide si el componente debe ser editable o readonly.
    --%>
    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_afiliado_editable_componente.jsp" %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_componente.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_documentos_componente.jsp" %>
    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_adjudicacion_runtime_componente.jsp" />

    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_orden_medica_carga_runtime_componente.jsp" />

    <c:if test="<%= !esNuevo %>">
        <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_orden_medica_consulta_runtime_componente.jsp" />
    </c:if>

    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_acciones_runtime_componente.jsp" />
</div>

<jsp:include page="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_base_componente.jsp" />

<% if (modoEditable) { %>
    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_scripts_edicion_runtime_componente.jsp" />
<% } else { %>
    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_scripts_consulta_runtime_componente.jsp" />
<% } %>
