<%@ include file="/html/portlet/compras/requerimientos/partials/_estilos.jsp" %>
<jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_mensajes_runtime.jsp" />

<% if (modoEditable) { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_form_hidden.jsp" %>
<% } %>

<div id="<portlet:namespace />compras_layout"
     class="compras-formulario-requerimiento <%= !modoEditable ? "compras-modo-vista" : "" %>">

    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_datos_basicos_runtime.jsp" />

    <%--
        Se usa el mismo componente de afiliado en ALTA, EDICION y VISTA.
        El propio partial decide si el componente debe ser editable o readonly.
    --%>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_afiliado_editable.jsp" %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/_detalle.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_adjuntos.jsp" %>
    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_adjudicacion_runtime.jsp" />

    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_orden_medica_alta_runtime.jsp" />

    <c:if test="<%= !esNuevo %>">
        <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_orden_medica_vista_runtime.jsp" />
    </c:if>

    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_botonera_runtime.jsp" />
</div>

<%@ include file="/html/portlet/compras/requerimientos/partials/_scripts_comunes.jsp" %>

<% if (modoEditable) { %>
    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_scripts_edicion_runtime.jsp" />
<% } else { %>
    <jsp:include page="/html/portlet/compras/requerimientos/partials/runtime/_scripts_vista_runtime.jsp" />
<% } %>
