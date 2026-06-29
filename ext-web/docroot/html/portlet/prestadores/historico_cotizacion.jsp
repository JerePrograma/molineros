<%@ include file="/html/portlet/prestadores/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ page import="ar.com.ospim.liquidaciones.WebKeysLiquidaciones" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<portlet:defineObjects />

<%
Prestador prestador =
        (Prestador) request.getSession().getAttribute(
                WebKeysLiquidaciones.PRESTADOR_EN_EDICION
        );

int idPrestador =
        prestador != null
                ? prestador.getId_prestador()
                : 0;

String descripcionPrestador =
        prestador != null &&
        prestador.getDescripcion() != null
                ? prestador.getDescripcion()
                : "";
%>

<fieldset class="block-labels">
    <legend>Histórico de habilitación para cotizar</legend>

    <h1>
        Prestador:
        <%= HtmlUtil.escape(descripcionPrestador) %>
    </h1>
</fieldset>

<fieldset class="block-labels">

    <div
        align="center"
        id="<portlet:namespace />buscandoHistoricoCotizacion">

        <table>
            <tr>
                <td>
                    <liferay-ui:message key="buscando" />
                </td>

                <td align="center">
                    <img
                        alt="<liferay-ui:message key='buscando' />"
                        src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif"
                    />
                </td>
            </tr>
        </table>
    </div>

    <div
        align="center"
        id="<portlet:namespace />resultadoHistoricoCotizacion">
    </div>

</fieldset>

<script type="text/javascript">
jQuery(function () {

    var ns = '<portlet:namespace />';

    var $buscando = jQuery(
        '#' + ns + 'buscandoHistoricoCotizacion'
    );

    var $resultado = jQuery(
        '#' + ns + 'resultadoHistoricoCotizacion'
    );

    var url =
        '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">' +
            '<portlet:param name="struts_action" value="/prestadores/historico_cotizacion" />' +
            '<portlet:param name="idPrestador" value="<%= String.valueOf(idPrestador) %>" />' +
        '</portlet:renderURL>';

    $buscando.show();

    $resultado.load(
        url,
        {},
        function (responseText, status) {

            $buscando.hide();

            if (status === 'error') {
                $resultado.html(
                    '<div class="portlet-msg-error">' +
                        'No se pudo cargar el histórico.' +
                    '</div>'
                );
            }
        }
    );
});
</script>