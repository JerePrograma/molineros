<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/farmacia_ospim/init.jsp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.farmaciaOspim.helper.VademecumAdmifarmHelper" %>
<%@ page import="ar.com.ospim.farmaciaOspim.services.VademecumAdmifarmServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>

<%
VademecumAdmifarmHelper.validarPermiso(user);

String errorVademecum = (String) SessionErrors.get(renderRequest, "vademecum-error");
String exitoVademecum = (String) SessionMessages.get(renderRequest, "vademecum-importado");
%>

<liferay-ui:error key="vademecum-error"
    message='<%= HtmlUtil.escape(errorVademecum == null ? "No se pudo completar la operacion." : errorVademecum) %>' />
<liferay-ui:success key="vademecum-importado"
    message='<%= HtmlUtil.escape(exitoVademecum == null ? "Vademecum importado." : exitoVademecum) %>' />

<div id="<portlet:namespace />vademecumAdmifarm">
<%
String[] tiposVademecum = { "ampliado", "pmo" };
SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

for (String tipoVademecum : tiposVademecum) {
    String tituloVademecum = "ampliado".equals(tipoVademecum) ? "Ampliado" : "PMO";
    List<Object[]> importaciones = Collections.emptyList();
    boolean errorConsulta = false;

    try {
        importaciones = VademecumAdmifarmServiceUtil.getImportaciones(tipoVademecum);
    } catch (Exception e) {
        errorConsulta = true;
        LogFactoryUtil.getLog("farmacia.vademecum_admifarm").error(
                "Error consultando historico " + tipoVademecum, e);
    }

    String tokenVademecum = VademecumAdmifarmHelper.obtenerToken(
            renderRequest.getPortletSession(), user.getUserId(), tipoVademecum);
%>
    <portlet:actionURL var="importarVademecumURL"
        windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
        <portlet:param name="struts_action" value="/farmaciaospim/vademecum_admifarm" />
    </portlet:actionURL>

    <form id="<portlet:namespace />formVademecum_<%= tipoVademecum %>"
        name="<portlet:namespace />formVademecum_<%= tipoVademecum %>"
        action="<%= importarVademecumURL %>" method="post" enctype="multipart/form-data"
        onsubmit="return <portlet:namespace />importarVademecum('<%= tipoVademecum %>');">

        <input type="hidden" name="operacion" value="importar" />
        <input type="hidden" name="tipo" value="<%= tipoVademecum %>" />
        <input type="hidden" name="tokenVademecum" value="<%= tokenVademecum %>" />

        <fieldset class="block-labels">
            <legend>Importar Vademecum <%= tituloVademecum %></legend>
            <table class="lfr-table" style="width: 100%;">
                <tr>
                    <td style="vertical-align: top; width: 45%;">
                        <table class="lfr-table">
                            <tr>
                                <td>
                                    <label for="<portlet:namespace />archivo_<%= tipoVademecum %>">
                                        Seleccionar archivo:
                                    </label>
                                </td>
                                <td>
                                    <input type="file" name="archivo" accept=".xls,.xlsx"
                                        id="<portlet:namespace />archivo_<%= tipoVademecum %>" />
                                </td>
                                <td>
                                    <a href="javascript:void(0)"
                                        onclick="help(event, '<portlet:namespace />helpVademecum_<%= tipoVademecum %>')">
                                        <img style="height: 16px; width: 16px" src="/html/images/help.png"
                                            title="Ayuda" alt="Ayuda" />
                                    </a>
                                </td>
                                <td>
                                    <input type="submit" value="Subir archivo"
                                        id="<portlet:namespace />botonImportar_<%= tipoVademecum %>" />
                                </td>
                            </tr>
                        </table>
                        <div id="<portlet:namespace />procesandoVademecum_<%= tipoVademecum %>"
                            style="display: none; margin-top: 15px;">
                            <liferay-ui:message key="procesando" />
                            <img alt="Procesando"
                                src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
                        </div>
                    </td>
                    <td style="vertical-align: top; width: 55%;">
                        <table style="width: 100%;">
                            <tr>
                                <td><label>Listado de Vademecum <%= tituloVademecum %> importados:</label></td>
                            </tr>
                            <tr><td>&nbsp;</td></tr>
                            <% if (errorConsulta) { %>
                                <tr><td>No se pudo consultar el historico. Vuelva a intentar.</td></tr>
                            <% } else if (importaciones.isEmpty()) { %>
                                <tr><td>No hay archivos subidos.</td></tr>
                            <% } else { %>
                                <tr><td>Mostrando <%= importaciones.size() %> resultados.</td></tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td>
                                        <table class="vademecum-tabla">
                                            <thead>
                                                <tr>
                                                    <th>Fecha Importaci&oacute;n</th>
                                                    <th>Cantidad de registros</th>
                                                    <th>Descargar</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <%
                                            int numeroFila = 0;
                                            for (Object[] importacion : importaciones) {
                                                Timestamp fechaImportacion = (Timestamp) importacion[0];
                                                String claseFila = numeroFila++ % 2 == 0
                                                        ? "vademecum-fila-par" : "vademecum-fila-impar";
                                            %>
                                                <portlet:actionURL var="descargarVademecumURL"
                                                    windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
                                                    <portlet:param name="struts_action"
                                                        value="/farmaciaospim/vademecum_admifarm" />
                                                    <portlet:param name="operacion" value="descargar" />
                                                    <portlet:param name="tipo" value="<%= tipoVademecum %>" />
                                                    <portlet:param name="fecha"
                                                        value="<%= fechaImportacion.toString() %>" />
                                                </portlet:actionURL>
                                                <tr class="<%= claseFila %>">
                                                    <td title="<%= fechaImportacion.toString() %>">
                                                        <%= formatoFecha.format(fechaImportacion) %>
                                                    </td>
                                                    <td><%= importacion[1] %></td>
                                                    <td>
                                                        <a href="<%= descargarVademecumURL %>"
                                                            title="Descargar contenido, altas, bajas y modificaciones">
                                                            Descargar
                                                        </a>
                                                    </td>
                                                </tr>
                                            <% } %>
                                            </tbody>
                                        </table>
                                    </td>
                                </tr>
                            <% } %>
                        </table>
                    </td>
                </tr>
            </table>
        </fieldset>
    </form>

    <div id="<portlet:namespace />helpVademecum_<%= tipoVademecum %>"
        class="containerPlus draggable {buttons:'c', skin:'default', width:'700', title:'Ayuda', closed:'true'}"
        style="top: 100px; left: 250px">
        Importe un archivo .xls o .xlsx de Vademecum <%= tituloVademecum %>. Se lee la primera hoja.
        <br /><br />
        El registro identifica cada medicamento y debe estar informado, sin repetirse.
        El archivo debe contener datos; las filas completamente vac&iacute;as no se importan.
        <br /><br />
        Se conserva cada carga completa y se compara con el &uacute;ltimo hist&oacute;rico del mismo tipo.
        Si no existe hist&oacute;rico, se respalda primero el vigente con la fecha de ese respaldo.
        <br /><br />
        Descargar incluye el contenido importado, altas, bajas, modificaciones (antes y ahora) y un resumen.
        La primera hoja descargada puede volver a importarse. L&iacute;mite: 20 MB y 65.535 registros.
    </div>
<% } %>
</div>

<script type="text/javascript">
function <portlet:namespace />importarVademecum(tipo) {
    var archivo = document.getElementById("<portlet:namespace />archivo_" + tipo);
    var boton = document.getElementById("<portlet:namespace />botonImportar_" + tipo);
    var procesando = document.getElementById("<portlet:namespace />procesandoVademecum_" + tipo);

    if (archivo == null || archivo.value == null || archivo.value == "") {
        alert("Debe seleccionar un archivo Excel.");
        return false;
    }
    boton.disabled = true;
    boton.value = "Procesando...";
    procesando.style.display = "block";
    return true;
}
</script>

<style type="text/css">
    #<portlet:namespace />vademecumAdmifarm .vademecum-tabla {
        width: 100%;
        border-collapse: collapse;
    }
    #<portlet:namespace />vademecumAdmifarm .vademecum-tabla th {
        background-color: #918580;
        color: #FFFFFF;
        font-weight: bold;
        padding: 8px 6px;
        text-align: center;
    }
    #<portlet:namespace />vademecumAdmifarm .vademecum-tabla td {
        padding: 7px 6px;
        text-align: center;
    }
    #<portlet:namespace />vademecumAdmifarm .vademecum-fila-par {
        background-color: #F3EEEB;
    }
    #<portlet:namespace />vademecumAdmifarm .vademecum-fila-impar {
        background-color: #DED9D6;
    }
</style>
