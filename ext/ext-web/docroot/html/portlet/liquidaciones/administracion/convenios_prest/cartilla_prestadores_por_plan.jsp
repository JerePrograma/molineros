<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.EspecialidadPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.BusquedaCartillaConvenioFiltro" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.CartillaConvenioRow" %>

<%
    BusquedaCartillaConvenioFiltro filtro =
            (BusquedaCartillaConvenioFiltro) request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO);

    if (filtro == null) {
        filtro = new BusquedaCartillaConvenioFiltro();
    }

    List<CartillaConvenioRow> resultados =
            (List<CartillaConvenioRow>) request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);

    if (resultados == null) {
        resultados = new ArrayList<CartillaConvenioRow>();
    }

    List<Plan> planes = (List<Plan>) session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION);
    List<Prestador> prestadores = (List<Prestador>) session.getAttribute(WebKeysLiquidaciones.PRESTADORES_EN_SESSION);
    List<Provincia> provincias = (List<Provincia>) session.getAttribute(WebKeysLiquidaciones.PROVINCIAS_EN_SESSION);
    List<Localidad> localidades = (List<Localidad>) session.getAttribute(WebKeysLiquidaciones.LOCALIDADES_EN_SESSION);
    List<EspecialidadPrestador> especialidades =
            (List<EspecialidadPrestador>) session.getAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION);
%>

<portlet:renderURL var="buscarURL">
    <portlet:param name="struts_action" value="/liquidaciones/cartilla_convenio_por_plan" />
    <portlet:param name="<%= Constants.CMD %>" value="search" />
</portlet:renderURL>

<portlet:actionURL var="exportarURL">
    <portlet:param name="struts_action" value="/liquidaciones/cartilla_convenio_por_plan" />
    <portlet:param name="<%= Constants.CMD %>" value="exportCartillaXls" />
</portlet:actionURL>

<h3>Cartilla de Prestadores por Plan</h3>

<form action="<%= buscarURL %>" method="post">
    <table class="lfr-table">
        <tr>
            <td>Plan</td>
            <td>
                <select name="idPlan">
                    <option value="">-- Seleccionar --</option>
                    <%
                        if (planes != null) {
                            for (Plan p : planes) {
                    %>
                    <option value="<%= p.getId() %>"
                        <%= (filtro.getIdPlan() != null && filtro.getIdPlan().intValue() == p.getId()) ? "selected" : "" %>>
                        <%= p.getDescripcion() %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>

            <td>Prestador exacto</td>
            <td>
                <select name="idPrestador">
                    <option value="">-- Todos --</option>
                    <%
                        if (prestadores != null) {
                            for (Prestador p : prestadores) {
                    %>
                    <option value="<%= p.getId_prestador() %>"
                        <%= (filtro.getIdPrestador() != null && filtro.getIdPrestador().intValue() == p.getId_prestador()) ? "selected" : "" %>>
                        <%= p.getDescripcion() %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>Nombre prestador</td>
            <td>
                <input type="text" name="prestadorDescripcion"
                       value="<%= filtro.getPrestadorDescripcion() != null ? filtro.getPrestadorDescripcion() : "" %>" />
            </td>

            <td>Institución</td>
            <td>
                <input type="text" name="institucion"
                       value="<%= filtro.getInstitucion() != null ? filtro.getInstitucion() : "" %>" />
            </td>
        </tr>

        <tr>
            <td>Provincia</td>
            <td>
                <select name="idProvincia">
                    <option value="">-- Todas --</option>
                    <%
                        if (provincias != null) {
                            for (Provincia p : provincias) {
                    %>
                    <option value="<%= p.getId() %>"
                        <%= (filtro.getIdProvincia() != null && filtro.getIdProvincia().intValue() == p.getId()) ? "selected" : "" %>>
                        <%= p.getDescripcion() %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>

            <td>Localidad</td>
            <td>
                <select name="idLocalidad">
                    <option value="">-- Todas --</option>
                    <%
                        if (localidades != null) {
                            for (Localidad l : localidades) {
                    %>
                    <option value="<%= l.getId() %>"
                        <%= (filtro.getIdLocalidad() != null && filtro.getIdLocalidad().intValue() == l.getId()) ? "selected" : "" %>>
                        <%= l.getDescripcion() %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>Especialidad</td>
            <td>
                <select name="idEspecialidad">
                    <option value="">-- Todas --</option>
                    <%
                        if (especialidades != null) {
                            for (EspecialidadPrestador e : especialidades) {
                    %>
                    <option value="<%= e.getIdEspecialidad() %>"
                        <%= (filtro.getIdEspecialidad() != null && filtro.getIdEspecialidad().intValue() == e.getIdEspecialidad()) ? "selected" : "" %>>
                        <%= e.getDescripcion() %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>

            <td>Incluye bajas</td>
            <td>
                <input type="checkbox" name="incluyeBajas" value="true"
                       <%= filtro.isIncluyeBajas() ? "checked" : "" %> />
            </td>
        </tr>

        <tr>
            <td colspan="4" align="left">
                <input type="submit" value="Buscar" />
                <% if (resultados != null && !resultados.isEmpty()) { %>
                    <input type="button" value="Exportar XLS" onclick="location.href='<%= exportarURL %>';" />
                <% } %>
            </td>
        </tr>
    </table>
</form>

<br/>

<table class="lfr-table" border="1" cellpadding="4" cellspacing="0" width="100%">
    <tr>
        <th>Plan</th>
        <th>Prestador</th>
        <th>CUIT</th>
        <th>Zona</th>
        <th>Especialidad</th>
        <th>Institución</th>
        <th>Domicilio</th>
        <th>Teléfono</th>
        <th>Localidad</th>
        <th>Provincia</th>
    </tr>

    <%
        if (resultados != null && !resultados.isEmpty()) {
            for (CartillaConvenioRow row : resultados) {
    %>
    <tr>
        <td><%= row.getPlanDescripcion() != null ? row.getPlanDescripcion() : "" %></td>
        <td><%= row.getPrestadorDescripcion() != null ? row.getPrestadorDescripcion() : "" %></td>
        <td><%= row.getCuitPrestador() != null ? row.getCuitPrestador() : "" %></td>
        <td><%= row.getZona() != null ? row.getZona() : "" %></td>
        <td><%= row.getEspecialidad() != null ? row.getEspecialidad() : "" %></td>
        <td><%= row.getInstitucion() != null ? row.getInstitucion() : "" %></td>
        <td><%= row.getDomicilio() != null ? row.getDomicilio() : "" %></td>
        <td><%= row.getTelefono() != null ? row.getTelefono() : "" %></td>
        <td><%= row.getLocalidad() != null ? row.getLocalidad() : "" %></td>
        <td><%= row.getProvincia() != null ? row.getProvincia() : "" %></td>
    </tr>
    <%
            }
        } else {
    %>
    <tr>
        <td colspan="10">No se encontraron resultados.</td>
    </tr>
    <%
        }
    %>
</table>