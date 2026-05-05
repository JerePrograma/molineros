<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiCuentasBancarias" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
List<AfiCuentasBancarias> cuentas = (List<AfiCuentasBancarias>) renderRequest.getAttribute("HISTORICO_CUENTAS");
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

Afiliado afi = (Afiliado) renderRequest.getAttribute("AFILIADO_SELECCIONADO");
%>

<div class="section-container">
    <div class="portlet-section-header">
        <h3>Histórico de Cuentas Bancarias - <%= afi.getApellido() %>, <%= afi.getNombre() %> - CUIL: <%= afi.getCuil_titular() %></h3>
    </div>

    <% if (cuentas == null || cuentas.isEmpty()) { %>
        <div class="portlet-msg-info" style="margin-top:10px;">
            No se encontraron cuentas bancarias registradas para este afiliado.
        </div>
    <% } else { %>

        <table class="taglib-search-iterator" width="100%" cellpadding="4" cellspacing="0" border="0">
            <thead>
                <tr class="portlet-section-header results-header">
                    <th>Titular/Apoderado</th>
                    <th>CBU</th>
                    <th>Email</th>
                    <th>Apellido y Nombre</th>
                    <th>CUIL</th>
                    <th>Fecha Alta</th>
                    <th>Alta Usuario</th>
                    <th>Fecha Baja</th>
                    <th>Baja Usuario</th>
                </tr>
            </thead>

            <tbody>
                <% for (int i = 0; i < cuentas.size(); i++) {
                       AfiCuentasBancarias c = cuentas.get(i);
                       String claseFila = (i % 2 == 0) ? "results-row alt" : "results-row";
                %>
                    <tr class="<%= claseFila %>">
                        <td><%= c.isTitular() ? "Titular" : "Apoderado" %></td>
                        <td><%= Validator.isNotNull(c.getCbu()) ? c.getCbu() : "" %></td>
                        <td><%= Validator.isNotNull(c.getEmail()) ? c.getEmail() : "" %></td>

                        <td>
                            <%= (Validator.isNotNull(c.getApellido()) ? c.getApellido() : "")
                                + " " +
                                (Validator.isNotNull(c.getNombre()) ? c.getNombre() : "") %>
                        </td>

                        <td>
                            <%= c.isTitular()
                                    ? (Validator.isNotNull(c.getCuilTitular()) ? c.getCuilTitular() : "")
                                    : (Validator.isNotNull(c.getCuilCbu()) ? c.getCuilCbu() : "") %>
                        </td>

                        <td><%= c.getAltaFecha() != null ? sdf.format(c.getAltaFecha()) : "" %></td>
                        <td><%= Validator.isNotNull(c.getAltaUsr()) ? c.getAltaUsr() : "" %></td>
                        <td><%= c.getBajaFecha() != null ? sdf.format(c.getBajaFecha()) : "" %></td>
                        <td><%= Validator.isNotNull(c.getBajaUsr()) ? c.getBajaUsr() : "" %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>

    <% } %>
</div>
