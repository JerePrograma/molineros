<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%
  int anioDesde = 2025;
  int anioHasta = 2050;
%>

<fieldset class="block-labels">
  <legend>Descarga R331</legend>

  <table>
    <tr>
      <td><label>Año:</label></td>
      <td>
        <select id="<portlet:namespace/>anio" name="<portlet:namespace/>anio">
          <% for (int y = anioDesde; y <= anioHasta; y++) { %>
            <option value="<%= y %>"><%= y %></option>
          <% } %>
        </select>
      </td>

      <td style="width:20px;"></td>

      <td><label>Trimestre:</label></td>
      <td>
        <select id="<portlet:namespace/>trimestre" name="<portlet:namespace/>trimestre">
          <option value="1">1 </option>
          <option value="2">2 </option>
          <option value="3">3 </option>
          <option value="4">4 </option>
        </select>
      </td>

      <td style="width:20px;"></td>

      <td>
        <input type="button"
               id="<portlet:namespace/>descargarR331"
               value="Descargar"
               title="Descargar" />
      </td>
    </tr>
  </table>

  <div id="<portlet:namespace/>r331_msg" style="margin-top:10px;"></div>
</fieldset>

<script type="text/javascript">
jQuery('#<portlet:namespace/>descargarR331').click(function () {
  var anio = jQuery('#<portlet:namespace/>anio').val();
  var tri  = jQuery('#<portlet:namespace/>trimestre').val();

  window.location.href = '/txtservlet/?reporte=R331_ZIP'
    + '&anio=' + encodeURIComponent(anio)
    + '&trimestre=' + encodeURIComponent(tri);
});
</script>

