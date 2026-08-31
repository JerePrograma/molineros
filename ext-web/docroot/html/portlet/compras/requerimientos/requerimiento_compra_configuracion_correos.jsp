<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra" %>

<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0L);

List<TipoPrestacionCompra> rubrosConfiguracionCorreos =
        (List<TipoPrestacionCompra>) request.getAttribute(
                WebKeysCompras.TIPOS_PRESTACION_REQUERIMIENTO_COMPRA
        );

if (rubrosConfiguracionCorreos == null) {
    rubrosConfiguracionCorreos =
            new ArrayList<TipoPrestacionCompra>();
}

List<PrestadorCotizacion> prestadoresConfiguracionCorreos =
        (List<PrestadorCotizacion>) request.getAttribute(
                WebKeysCompras.PRESTADORES_HABILITADOS_COTIZACION
        );

if (prestadoresConfiguracionCorreos == null) {
    prestadoresConfiguracionCorreos =
            new ArrayList<PrestadorCotizacion>();
}

String errorConfiguracionCorreos =
        (String) request.getAttribute(
                WebKeysCompras
                        .ERROR_PRESTADORES_HABILITADOS_COTIZACION
        );

int idRubroConfiguracionCorreos =
        ParamUtil.getInteger(
                renderRequest,
                "id_tipo_prestacion",
                0
        );

TipoPrestacionCompra rubroConfiguracionCorreos = null;

for (int i = 0; i < rubrosConfiguracionCorreos.size(); i++) {
    TipoPrestacionCompra rubro = rubrosConfiguracionCorreos.get(i);

    if (rubro != null
            && rubro.getIdInt() == idRubroConfiguracionCorreos) {

        rubroConfiguracionCorreos = rubro;
        break;
    }
}

PortletURL configuracionCorreosURL = renderResponse.createRenderURL();
configuracionCorreosURL.setWindowState(LiferayWindowState.MAXIMIZED);
configuracionCorreosURL.setParameter("struts_action", "/compras/view");
configuracionCorreosURL.setParameter(
        "tabs1",
        "configuracion-de-correos"
);
pageContext.setAttribute(
        "configuracionCorreosURL",
        configuracionCorreosURL
);
%>

<style type="text/css">
    .compras-filtro-requerimientos {
        margin-bottom: 12px;
    }
</style>

<form action="<%= configuracionCorreosURL %>"
      method="get"
      name="<portlet:namespace />fmConfiguracionCorreos"
      onSubmit="submitForm(this); return false;">

    <liferay-portlet:renderURLParams
            varImpl="configuracionCorreosURL" />

    <fieldset class="block-labels compras-filtro-requerimientos">
        <legend>Consulta de correos por rubro</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <label for="<portlet:namespace />id_tipo_prestacion">Rubro:</label>
                </td>
                <td>
                    <select id="<portlet:namespace />id_tipo_prestacion"
                            name="<portlet:namespace />id_tipo_prestacion">

                        <option value="0"
                                <%= idRubroConfiguracionCorreos == 0
                                        ? "selected=\"selected\""
                                        : "" %>>
                            Seleccione
                        </option>

                        <%
                        for (int i = 0;
                             i < rubrosConfiguracionCorreos.size();
                             i++) {

                            TipoPrestacionCompra rubro =
                                    rubrosConfiguracionCorreos.get(i);

                            if (rubro == null || rubro.getIdInt() <= 0) {
                                continue;
                            }
                        %>
                            <option value="<%= rubro.getIdInt() %>"
                                    <%= rubro.getIdInt()
                                            == idRubroConfiguracionCorreos
                                                    ? "selected=\"selected\""
                                                    : "" %>>

                                <%= HtmlUtil.escape(
                                        rubro.getDescripcionVisible()
                                ) %>
                            </option>
                        <%
                        }
                        %>
                    </select>
                </td>
                <td>
                    <input type="submit"
                           value="Consultar" />
                </td>
            </tr>
        </table>
    </fieldset>
</form>

<% if (!WebKeysCompras.isEmpty(errorConfiguracionCorreos)) { %>
    <div class="portlet-msg-error">
        <%= HtmlUtil.escape(errorConfiguracionCorreos) %>
    </div>
<% } else if (idRubroConfiguracionCorreos > 0
        && rubroConfiguracionCorreos == null) { %>
    <div class="portlet-msg-error">
        El rubro seleccionado no es v&#225;lido.
    </div>
<% } else { %>
    <%
    String mensajeVacioConfiguracionCorreos =
            idRubroConfiguracionCorreos == 0
                    ? "Seleccione un rubro para consultar los correos configurados."
                    : "No se encontraron prestadores vigentes y habilitados "
                            + "para cotizar asociados al rubro seleccionado.";

    List<String> headerNamesConfiguracionCorreos =
            new ArrayList<String>();
    headerNamesConfiguracionCorreos.add("#");
    headerNamesConfiguracionCorreos.add("razon-social");
    headerNamesConfiguracionCorreos.add("cuit");
    headerNamesConfiguracionCorreos.add("tipo-prestador");
    headerNamesConfiguracionCorreos.add("Correos configurados");

    SearchContainer searchContainerConfiguracionCorreos =
            new SearchContainer(
                    renderRequest,
                    null,
                    null,
                    SearchContainer.DEFAULT_CUR_PARAM,
                    Integer.MAX_VALUE,
                    configuracionCorreosURL,
                    headerNamesConfiguracionCorreos,
                    mensajeVacioConfiguracionCorreos
            );

    List resultRowsConfiguracionCorreos =
            searchContainerConfiguracionCorreos.getResultRows();
    int numeroPrestadorConfiguracionCorreos = 0;

    for (int i = 0;
         i < prestadoresConfiguracionCorreos.size();
         i++) {

        PrestadorCotizacion prestadorConfiguracionCorreos =
                prestadoresConfiguracionCorreos.get(i);

        if (prestadorConfiguracionCorreos == null) {
            continue;
        }

        numeroPrestadorConfiguracionCorreos++;

        String emailConfiguracionCorreos =
                prestadorConfiguracionCorreos.getEmailVisible();

        if (WebKeysCompras.isEmpty(emailConfiguracionCorreos)) {
            emailConfiguracionCorreos =
                    "Sin correos v\u00e1lidos registrados.";
        }

        ResultRow rowConfiguracionCorreos =
                new ResultRow(
                        prestadorConfiguracionCorreos,
                        String.valueOf(
                                prestadorConfiguracionCorreos
                                        .getIdPrestador()
                        ),
                        i
                );

        rowConfiguracionCorreos.addText(
                HtmlUtil.escape(
                        String.valueOf(
                                numeroPrestadorConfiguracionCorreos
                        )
                )
        );
        rowConfiguracionCorreos.addText(
                HtmlUtil.escape(
                        prestadorConfiguracionCorreos
                                .getRazonSocialVisible()
                )
        );
        rowConfiguracionCorreos.addText(
                HtmlUtil.escape(
                        prestadorConfiguracionCorreos.getCuitVisible()
                )
        );
        rowConfiguracionCorreos.addText(
                HtmlUtil.escape(
                        prestadorConfiguracionCorreos
                                .getTipoPrestadorVisible()
                )
        );
        rowConfiguracionCorreos.addText(
                HtmlUtil.escape(emailConfiguracionCorreos)
        );
        resultRowsConfiguracionCorreos.add(
                rowConfiguracionCorreos
        );
    }

    searchContainerConfiguracionCorreos.setTotal(
            numeroPrestadorConfiguracionCorreos
    );
    %>

    <fieldset class="block-labels compras-resultados-requerimientos">
        <liferay-ui:search-iterator
                searchContainer="<%= searchContainerConfiguracionCorreos %>" />
    </fieldset>
<% } %>
