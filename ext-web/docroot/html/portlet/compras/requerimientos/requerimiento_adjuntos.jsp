<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>

<portlet:defineObjects/>

<%
RequerimientoCompra reqImagenes =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqImagenes == null) {
    reqImagenes =
            (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (reqImagenes == null) {
    reqImagenes = new RequerimientoCompra();
}

int idRequerimientoCompraImagenes = reqImagenes.getIdRequerimientoCompra();

Object soloLecturaAttrImagenes =
        renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String modoImagenes = ParamUtil.getString(renderRequest, "modo", "");
String strutsActionImagenes = ParamUtil.getString(renderRequest, "struts_action", "");

boolean soloLecturaImagenes =
        Boolean.TRUE.equals(soloLecturaAttrImagenes)
        || "ver".equalsIgnoreCase(modoImagenes)
        || "/compras/ver_requerimiento".equals(strutsActionImagenes);

boolean puedeABMImagenes =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

boolean puedeEditarImagenes =
        idRequerimientoCompraImagenes > 0
        && puedeABMImagenes
        && reqImagenes.isEditable()
        && !soloLecturaImagenes;

PortletURL uploadImagenesURL = renderResponse.createActionURL();
uploadImagenesURL.setWindowState(WindowState.MAXIMIZED);
uploadImagenesURL.setParameter("struts_action", "/compras/upload_imagenes_requerimiento");

String modoRetornoImagenes = soloLecturaImagenes ? "ver" : "";
%>

<form action="<%= uploadImagenesURL.toString() %>"
      method="post"
      name="<portlet:namespace />compra_img_fm"
      id="<portlet:namespace />compra_img_fm"
      enctype="multipart/form-data">

    <fieldset class="block-labels">
        <legend>Archivos del requerimiento</legend>

        <liferay-ui:error key="errorUploadFile"
                          message="<%=(String) request.getAttribute(\"msgInsertError\") %>" />

        <c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "requerimiento-compra-archivo-guardado") %>">
            <div class="portlet-msg-success">
                Archivo del requerimiento guardado correctamente.
            </div>
        </c:if>

        <c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "requerimiento-compra-archivo-borrado") %>">
            <div class="portlet-msg-success">
                Archivo del requerimiento eliminado correctamente.
            </div>
        </c:if>

        <c:if test="<%= idRequerimientoCompraImagenes <= 0 %>">
            <div class="portlet-msg-info">
                Debe guardar el requerimiento antes de subir archivos.
            </div>
        </c:if>

        <c:if test="<%= puedeEditarImagenes %>">
            <table class="lfr-table">
                <tr>
                    <td>Añadir archivo:</td>
                    <td>
                        <input type="file"
                               name="importa_imagenes"
                               id="<portlet:namespace />importa_imagenes" />
                    </td>

                    <td>&nbsp;</td>

                    <td>
                        <label>Descripción:</label>
                    </td>

                    <td>
                        <input id="<portlet:namespace />descripcionFile"
                               name="<portlet:namespace />descripcionFile"
                               size="90"
                               maxlength="120"
                               type="text"
                               value="" />
                    </td>

                    <td>
                        <input id="<portlet:namespace />uploadIMGCompra"
                               value="Subir archivo"
                               title="Subir archivo"
                               onclick="return <portlet:namespace />uploadImagenRequerimientoCompra();"
                               type="button" />
                    </td>
                </tr>
            </table>
        </c:if>

        <c:if test="<%= idRequerimientoCompraImagenes > 0 && !puedeEditarImagenes && !soloLecturaImagenes %>">
            <div class="portlet-msg-info">
                Los archivos solo pueden administrarse en estado Borrador.
            </div>
        </c:if>
    </fieldset>

    <input type="hidden"
           name="<portlet:namespace />imagen"
           id="<portlet:namespace />imagen"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />id_requerimiento_compra"
           id="<portlet:namespace />id_requerimiento_compra_img"
           value="<%= idRequerimientoCompraImagenes %>" />

    <input type="hidden"
           name="<portlet:namespace />folderid"
           id="<portlet:namespace />folderid"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />filename"
           id="<portlet:namespace />filename"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />modo"
           id="<portlet:namespace />modo_img"
           value="<%= HtmlUtil.escape(modoRetornoImagenes) %>" />

    <div id="<portlet:namespace />listado_imagenes_requerimiento">
        <jsp:include page="/html/portlet/compras/requerimientos/requerimiento_adjuntos_search_documentos.jsp" />
    </div>
</form>

<script type="text/javascript">
    function <portlet:namespace />uploadImagenRequerimientoCompra() {
        var form = document.getElementById('<portlet:namespace />compra_img_fm');

        if (!form) {
            alert('No se encontró el formulario de archivos.');
            return false;
        }

        var file = document.getElementById('<portlet:namespace />importa_imagenes');

        if (!file || file.value == '') {
            alert('Debe seleccionar un archivo.');
            return false;
        }

        document.getElementById('<portlet:namespace />imagen').value = '<%= Constants.ADD %>';
        document.getElementById('<portlet:namespace />folderid').value = '';
        document.getElementById('<portlet:namespace />filename').value = '';

        form.submit();

        return false;
    }

    function <portlet:namespace />deleteImagenRequerimientoCompra(folderId, filename) {
        var form = document.getElementById('<portlet:namespace />compra_img_fm');

        if (!form) {
            alert('No se encontró el formulario de archivos.');
            return false;
        }

        if (!confirm('¿Está seguro que desea eliminar este archivo?')) {
            return false;
        }

        document.getElementById('<portlet:namespace />imagen').value = '<%= Constants.DELETE %>';
        document.getElementById('<portlet:namespace />folderid').value = folderId;
        document.getElementById('<portlet:namespace />filename').value = filename;

        form.submit();

        return false;
    }
</script>