<%@ include file="/html/portlet/compras/init.jsp" %>

<%@ page import="ar.com.ospim.autorizaciones.beans.ItemSituacionMedicaTotal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%
List<ItemSituacionMedicaTotal> situacionesMedicas =
        (List<ItemSituacionMedicaTotal>)
                renderRequest.getAttribute(
                        WebKeysCompras
                                .SITUACIONES_MEDICAS_VIGENTES_COMPRA
                );

if (situacionesMedicas == null) {
    situacionesMedicas =
            new ArrayList<ItemSituacionMedicaTotal>();
}

SimpleDateFormat formatoFecha =
        new SimpleDateFormat(
                "dd/MM/yyyy"
        );
%>

<div class="compras-situacion-medica-vigente">

    <h3>
        Situación Médica vigente
    </h3>

    <% if (situacionesMedicas.isEmpty()) { %>

        <div class="portlet-msg-info">
            No se encontraron situaciones médicas vigentes.
        </div>

    <% } else { %>

        <table class="lfr-table"
               width="100%">

            <thead>
                <tr>
                    <th>
                        Nro
                    </th>

                    <th>
                        CUIL
                    </th>

                    <th>
                        Inte
                    </th>

                    <th>
                        Afiliado
                    </th>

                    <th>
                        Tipo Situación Médica
                    </th>

                    <th>
                        Vig. Desde
                    </th>

                    <th>
                        Vig. Hasta
                    </th>

                    <th>
                        Discapacitado
                    </th>
                </tr>
            </thead>

            <tbody>

                <%
                for (int i = 0;
                        i < situacionesMedicas.size();
                        i++) {

                    ItemSituacionMedicaTotal situacion =
                            situacionesMedicas.get(i);

                    if (situacion == null) {
                        continue;
                    }

                    String cuilVisible =
                            "";

                    String integranteVisible =
                            "";

                    String afiliadoVisible =
                            "";

                    if (situacion.getAfiliado() != null) {

                        cuilVisible =
                                situacion
                                        .getAfiliado()
                                        .getCuil_titularMasked();

                        integranteVisible =
                                situacion
                                        .getAfiliado()
                                        .getInteAsString();

                        afiliadoVisible =
                                situacion
                                        .getAfiliado()
                                        .getApellidoNombre();
                    }

                    String tipoVisible =
                            situacion.getTipoSituMedica() != null
                                    ? situacion.getTipoSituMedica()
                                    : "";

                    String fechaDesdeVisible =
                            situacion.getFechaVigen_Desde() != null
                                    ? formatoFecha.format(
                                            situacion
                                                    .getFechaVigen_Desde()
                                    )
                                    : "";

                    String fechaHastaVisible =
                            situacion.getFechaVigen_Hasta() != null
                                    ? formatoFecha.format(
                                            situacion
                                                    .getFechaVigen_Hasta()
                                    )
                                    : "Sin vencimiento";
                %>

                    <tr class="<%= i % 2 == 0
                            ? "portlet-section-body"
                            : "portlet-section-alternate" %>">

                        <td>
                            <%= HtmlUtil.escape(
                                    situacion.getId_String()
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    cuilVisible
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    integranteVisible
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    afiliadoVisible
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    tipoVisible
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    fechaDesdeVisible
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    fechaHastaVisible
                            ) %>
                        </td>

                        <td>
                            <%= situacion.isDiscapacitado()
                                    ? "Sí"
                                    : "No" %>
                        </td>

                    </tr>

                <%
                }
                %>

            </tbody>

        </table>

    <% } %>

</div>