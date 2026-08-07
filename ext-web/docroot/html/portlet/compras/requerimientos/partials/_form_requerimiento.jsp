<%@ include file="/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp" %>

<% if (puedeEditarEstructuraPantalla) { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_afiliado_editable.jsp" %>
<% } else { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_afiliado_readonly.jsp" %>
<% } %>

<%@ include file="/html/portlet/compras/requerimientos/partials/_detalle.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/_adjuntos.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/_adjudicacion.jsp" %>
