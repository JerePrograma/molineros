<%@ include file="/html/portlet/compras/requerimientos/partials/_estilos.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/_mensajes.jsp" %>

<div id="<portlet:namespace />compras_layout"
     class="compras-formulario-requerimiento compras-modo-vista">

    <%@ include file="/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp" %>

    <%--
        La vista utiliza el mismo componente que ALTA/EDICION, configurado
        automaticamente como readonly por _afiliado_editable.jsp.
    --%>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_afiliado_editable.jsp" %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/_detalle.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_adjuntos.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_adjudicacion.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_botonera.jsp" %>
</div>

<%@ include file="/html/portlet/compras/requerimientos/partials/_scripts_comunes.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/_scripts_vista.jsp" %>