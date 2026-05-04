<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<portlet:defineObjects />

<liferay-ui:error key="seguimiento_guardado_error" message="No se pudo guardar el seguimiento." />
<c:if test='<%= SessionMessages.contains(renderRequest, "seguimiento_guardado_ok") %>'>
  <liferay-ui:success key="seguimiento_guardado_ok" message="Guardado con éxito." />
</c:if>

<%
  Calendar cal = CalendarFactoryUtil.getCalendar();

  String fNombre = ParamUtil.getString(request, "nombre", "");
  String fApellido = ParamUtil.getString(request, "apellido", "");
  String fDni    = ParamUtil.getString(request, "dni", "");
  String fEstado = ParamUtil.getString(request, "estado", "");
  String fProv   = ParamUtil.getString(request, "provincia", "");
  String fDdjj   = ParamUtil.getString(request, "ddjj", "");
  String autoBuscar = ParamUtil.getString(request, "autoBuscar", "");

  int segDesdeDiaVal  = ParamUtil.getInteger(request, "segDesdeDia",  cal.get(Calendar.DAY_OF_MONTH));
  int segDesdeMesVal  = ParamUtil.getInteger(request, "segDesdeMes",  cal.get(Calendar.MONTH));
  int segDesdeAnioVal = ParamUtil.getInteger(request, "segDesdeAnio", cal.get(Calendar.YEAR));

  int segHastaDiaVal  = ParamUtil.getInteger(request, "segHastaDia",  cal.get(Calendar.DAY_OF_MONTH));
  int segHastaMesVal  = ParamUtil.getInteger(request, "segHastaMes",  cal.get(Calendar.MONTH));
  int segHastaAnioVal = ParamUtil.getInteger(request, "segHastaAnio", cal.get(Calendar.YEAR));

  List<Map<String,Object>> vendedores = (List<Map<String,Object>>) request.getAttribute("vendedores");
  if (vendedores == null) {
      vendedores = ar.com.ospim.afiliados.services.SolicitudAfiliacionServiceUtil.getTodosLosVendedores();
  }

  User userLogueado = PortalUtil.getUser(renderRequest);

  boolean puedeElegirVendedor = (userLogueado != null) &&
      PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

  boolean veMolineros = (userLogueado != null) &&
      PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_MOLINEROS);

  boolean veNoMolineros = (userLogueado != null) &&
      PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_NO_MOLINEROS);

  String vendedorForzado = (String) request.getAttribute("vendedorForzado");
  if (vendedorForzado == null) {
      vendedorForzado = ParamUtil.getString(renderRequest, "vendedor", "");
  }
  String fVendedor = vendedorForzado;

  String molineroForzado = "";
  if (!puedeElegirVendedor) {
      if (veMolineros && !veNoMolineros) {
          molineroForzado = "si";
      } else if (veNoMolineros && !veMolineros) {
          molineroForzado = "no";
      } else {
          String mf = (String) request.getAttribute("molineroForzado");
          molineroForzado = (mf != null) ? mf : "";
      }
  }

  boolean mostrarFiltroMolinero = (molineroForzado == null || molineroForzado.trim().isEmpty());

  String fMolinero = ParamUtil.getString(request, "molinero", "");
  if (!mostrarFiltroMolinero) {
      fMolinero = molineroForzado;
  }
%>

<div align="center" id="<portlet:namespace />seguimientoBuscando" style="display:none;">
  <table style="align:center;">
    <tr>
      <td><liferay-ui:message key='buscando'/></td>
      <td align="center">
        <img alt="<liferay-ui:message key='buscando'/>"
             src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
      </td>
    </tr>
  </table>
</div>

<portlet:renderURL var="buscarURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
  <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="buscarSeguimiento" />
</portlet:renderURL>


<portlet:actionURL var="guardarNotaURL">
  <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="guardarNotaSeguimiento" />
</portlet:actionURL>

<fieldset class="block-labels">
  <legend>Solicitudes de Afiliacion</legend>

  <table class="lfr-table filtros-seguimiento">
  <tr>
    <td class="lbl"><label>Fecha Desde:</label></td>
    <td class="val" colspan="3">
      <liferay-ui:input-date
        dayParam="segDesdeDia" monthParam="segDesdeMes" yearParam="segDesdeAnio"
        dayValue="<%= segDesdeDiaVal %>" monthValue="<%= segDesdeMesVal %>" yearValue="<%= segDesdeAnioVal %>"
        yearRangeStart="<%= cal.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= cal.get(Calendar.YEAR) + 20 %>"
        dayNullable="false" monthNullable="false" yearNullable="false" disabled="<%= false %>"
      />
    </td>

    <td class="lbl"><label>Fecha Hasta:</label></td>
    <td class="val" colspan="3">
      <liferay-ui:input-date
        dayParam="segHastaDia" monthParam="segHastaMes" yearParam="segHastaAnio"
        dayValue="<%= segHastaDiaVal %>" monthValue="<%= segHastaMesVal %>" yearValue="<%= segHastaAnioVal %>"
        yearRangeStart="<%= cal.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= cal.get(Calendar.YEAR) + 20 %>"
        dayNullable="false" monthNullable="false" yearNullable="false" disabled="<%= false %>"
      />
    </td>
  </tr>

  <tr>
    <td class="lbl"><label>Nombre</label></td>
    <td class="val">
      <input type="text" id="<portlet:namespace/>nombre" value="<%= fNombre %>" />
    </td>

    <td class="lbl"><label>DNI</label></td>
    <td class="val">
      <input type="text" id="<portlet:namespace/>dni" value="<%= fDni %>" />
    </td>

    <td class="lbl"><label>Provincia</label></td>
    <td class="val" colspan="3">
      <select id="<portlet:namespace/>provincia">
        <option value="" <%= "".equals(fProv) ? "selected" : "" %>>Todas</option>

        <option value="BUENOS AIRES" <%= "BUENOS AIRES".equals(fProv) ? "selected" : "" %>>Buenos Aires</option>
        <option value="CABA" <%= "CABA".equals(fProv) ? "selected" : "" %>>CABA</option>
        <option value="CATAMARCA" <%= "CATAMARCA".equals(fProv) ? "selected" : "" %>>Catamarca</option>
        <option value="CHACO" <%= "CHACO".equals(fProv) ? "selected" : "" %>>Chaco</option>
        <option value="CHUBUT" <%= "CHUBUT".equals(fProv) ? "selected" : "" %>>Chubut</option>
        <option value="CORDOBA" <%= "CORDOBA".equals(fProv) ? "selected" : "" %>>Córdoba</option>
        <option value="CORRIENTES" <%= "CORRIENTES".equals(fProv) ? "selected" : "" %>>Corrientes</option>
        <option value="ENTRE RIOS" <%= "ENTRE RIOS".equals(fProv) ? "selected" : "" %>>Entre Ríos</option>
        <option value="FORMOSA" <%= "FORMOSA".equals(fProv) ? "selected" : "" %>>Formosa</option>
        <option value="JUJUY" <%= "JUJUY".equals(fProv) ? "selected" : "" %>>Jujuy</option>
        <option value="LA PAMPA" <%= "LA PAMPA".equals(fProv) ? "selected" : "" %>>La Pampa</option>
        <option value="LA RIOJA" <%= "LA RIOJA".equals(fProv) ? "selected" : "" %>>La Rioja</option>
        <option value="MENDOZA" <%= "MENDOZA".equals(fProv) ? "selected" : "" %>>Mendoza</option>
        <option value="MISIONES" <%= "MISIONES".equals(fProv) ? "selected" : "" %>>Misiones</option>
        <option value="NEUQUEN" <%= "NEUQUEN".equals(fProv) ? "selected" : "" %>>Neuquén</option>
        <option value="RIO NEGRO" <%= "RIO NEGRO".equals(fProv) ? "selected" : "" %>>Río Negro</option>
        <option value="SALTA" <%= "SALTA".equals(fProv) ? "selected" : "" %>>Salta</option>
        <option value="SAN JUAN" <%= "SAN JUAN".equals(fProv) ? "selected" : "" %>>San Juan</option>
        <option value="SAN LUIS" <%= "SAN LUIS".equals(fProv) ? "selected" : "" %>>San Luis</option>
        <option value="SANTA CRUZ" <%= "SANTA CRUZ".equals(fProv) ? "selected" : "" %>>Santa Cruz</option>
        <option value="SANTA FE" <%= "SANTA FE".equals(fProv) ? "selected" : "" %>>Santa Fe</option>
        <option value="SANTIAGO DEL ESTERO" <%= "SANTIAGO DEL ESTERO".equals(fProv) ? "selected" : "" %>>Santiago del Estero</option>
        <option value="TIERRA DEL FUEGO" <%= "TIERRA DEL FUEGO".equals(fProv) ? "selected" : "" %>>Tierra del Fuego</option>
        <option value="TUCUMAN" <%= "TUCUMAN".equals(fProv) ? "selected" : "" %>>Tucumán</option>
      </select>
    </td>
  </tr>

  <tr>
  
  <td class="lbl"><label>Es molinero</label></td>
<td class="val">
  <select id="<portlet:namespace/>molinero" <%= mostrarFiltroMolinero ? "" : "disabled='disabled'" %>>
    <% if (mostrarFiltroMolinero) { %>
      <option value="" <%= "".equals(fMolinero) ? "selected" : "" %>>Todos</option>
    <% } %>
    <option value="si" <%= "si".equalsIgnoreCase(fMolinero) ? "selected" : "" %>>Sí</option>
    <option value="no" <%= "no".equalsIgnoreCase(fMolinero) ? "selected" : "" %>>No</option>
  </select>

  <% if (!mostrarFiltroMolinero) { %>
    <input type="hidden" id="<portlet:namespace/>molineroHidden" value="<%= molineroForzado %>" />
  <% } %>
</td>

    <td class="lbl"><label>DDJJ</label></td>
    <td class="val">
      <select id="<portlet:namespace/>ddjj">
        <option value="" <%= "".equals(fDdjj) ? "selected" : "" %>>Todos</option>
        <option value="con" <%= "con".equals(fDdjj) ? "selected" : "" %>>Sí</option>
        <option value="sin" <%= "sin".equals(fDdjj) ? "selected" : "" %>>No</option>
      </select>
    </td>

    <td class="lbl"><label>Estado</label></td>
    <td class="val">
      <select id="<portlet:namespace/>estado">
        <option value="" <%= "".equals(fEstado) ? "selected" : "" %>>Todos</option>
        <option value="pendiente" <%= "pendiente".equalsIgnoreCase(fEstado) ? "selected" : "" %>>Pendiente</option>
        <option value="asignada" <%= "asignada".equalsIgnoreCase(fEstado) ? "selected" : "" %>>Asignada</option>
        <option value="contactado" <%= "contactado".equalsIgnoreCase(fEstado) ? "selected" : "" %>>Contactado</option>
        <option value="rechazado" <%= "rechazado".equalsIgnoreCase(fEstado) ? "selected" : "" %>>Rechazado</option>
        <option value="finalizado" <%= "finalizado".equalsIgnoreCase(fEstado) ? "selected" : "" %>>Finalizado</option>
      </select>
    </td>

    <td class="lbl"><label>Vendedor</label></td>
<td class="val">
  <select id="<portlet:namespace/>vendedor" <%= puedeElegirVendedor ? "" : "disabled='disabled'" %>>
    <% if (puedeElegirVendedor) { %>
      <option value="" <%= "".equals(fVendedor) ? "selected" : "" %>>Todos</option>
      <option value="sin_asignar" <%= "sin_asignar".equals(fVendedor) ? "selected" : "" %>>Sin asignar</option>
    <% } %>

    <%
      for (Map<String,Object> v : vendedores) {
        String vid = String.valueOf(v.get("id"));
        String vnom = String.valueOf(v.get("nombre"));
        String vape = String.valueOf(v.get("apellido"));
    %>
      <option value="<%= vid %>" <%= vid.equals(fVendedor) ? "selected" : "" %>>
        <%= HtmlUtil.escape(vape + ", " + vnom) %>
      </option>
    <%
      }
    %>
  </select>
</td>

    <td class="acciones">
      <input type="button" value="Buscar" onclick="javascript:<portlet:namespace/>buscarSeguimiento();" />
    </td>
  </tr>
</table>
</fieldset>

<br/>

<% if (request.getAttribute("resultados") == null) { %>
  <div class="portlet-msg-info" id="<portlet:namespace/>msgInicial">
    Ingresá filtros y presioná <b>Buscar</b>.
  </div>
<% } %>

<!-- se carga la lista -->
<div id="<portlet:namespace/>seguimientoResultados">
  <% if (request.getAttribute("resultados") != null) { %>
    <liferay-util:include page="/html/portlet/afiliados/seguimiento_list.jsp"/>
  <% } %>
</div>

<script type="text/javascript">
function <portlet:namespace/>buscarSeguimiento(){
  jQuery('#<portlet:namespace/>msgInicial').hide();

  var segDesdeDia  = jQuery('#<portlet:namespace/>segDesdeDia').val();
  var segDesdeMes  = jQuery('#<portlet:namespace/>segDesdeMes').val();
  var segDesdeAnio = jQuery('#<portlet:namespace/>segDesdeAnio').val();

  var segHastaDia  = jQuery('#<portlet:namespace/>segHastaDia').val();
  var segHastaMes  = jQuery('#<portlet:namespace/>segHastaMes').val();
  var segHastaAnio = jQuery('#<portlet:namespace/>segHastaAnio').val();

  var nombre = jQuery('#<portlet:namespace/>nombre').val();
  var apellido = jQuery('#<portlet:namespace/>apellido').val();
  var dni = jQuery('#<portlet:namespace/>dni').val();
  var estado = jQuery('#<portlet:namespace/>estado').val();
  var provincia = jQuery('#<portlet:namespace/>provincia').val();

  var molinero = '';
  var molineroInput = jQuery('#<portlet:namespace/>molinero');

  if (molineroInput.length > 0 && !molineroInput.is(':disabled')) {
    molinero = molineroInput.val();
  } else {
    molinero = jQuery('#<portlet:namespace/>molineroHidden').val() || '';
  }
  
  var ddjj = jQuery('#<portlet:namespace/>ddjj').val();
  var vendedor = '';
  var vendedorInput = jQuery('#<portlet:namespace/>vendedor');

  if (vendedorInput.length > 0 && !vendedorInput.is(':disabled')) {
    vendedor = vendedorInput.val();
  }

  var url = '${buscarURL}';
  url += '&<portlet:namespace/>segDesdeDia='  + segDesdeDia;
  url += '&<portlet:namespace/>segDesdeMes='  + segDesdeMes;
  url += '&<portlet:namespace/>segDesdeAnio=' + segDesdeAnio;

  url += '&<portlet:namespace/>segHastaDia='  + segHastaDia;
  url += '&<portlet:namespace/>segHastaMes='  + segHastaMes;
  url += '&<portlet:namespace/>segHastaAnio=' + segHastaAnio;

  url += '&<portlet:namespace/>nombre=' + encodeURIComponent(nombre || '');
  url += '&<portlet:namespace/>apellido=' + encodeURIComponent(apellido || '');
  url += '&<portlet:namespace/>dni=' + encodeURIComponent(dni || '');
  url += '&<portlet:namespace/>molinero=' + encodeURIComponent(molinero || '');
  url += '&<portlet:namespace/>estado=' + encodeURIComponent(estado || '');
  url += '&<portlet:namespace/>provincia=' + encodeURIComponent(provincia || '');
  url += '&<portlet:namespace/>ddjj=' + encodeURIComponent(ddjj || '');
  url += '&<portlet:namespace/>vendedor=' + encodeURIComponent(vendedor || '');
  url += '&rnd=' + Math.floor(Math.random()*100);

  jQuery('#<portlet:namespace />seguimientoBuscando').show();
  jQuery('#<portlet:namespace/>seguimientoResultados').load(url, function(){
    jQuery('#<portlet:namespace />seguimientoBuscando').hide();
  });
}

function <portlet:namespace/>guardarSeguimiento(id){
  var estadoNuevo = jQuery('#<portlet:namespace/>estadoNuevo_' + id).val();
  var nota = jQuery('#<portlet:namespace/>nota_' + id).val();

  var segDesdeDia  = jQuery('#<portlet:namespace/>segDesdeDia').val();
  var segDesdeMes  = jQuery('#<portlet:namespace/>segDesdeMes').val();
  var segDesdeAnio = jQuery('#<portlet:namespace/>segDesdeAnio').val();
  var segHastaDia  = jQuery('#<portlet:namespace/>segHastaDia').val();
  var segHastaMes  = jQuery('#<portlet:namespace/>segHastaMes').val();
  var segHastaAnio = jQuery('#<portlet:namespace/>segHastaAnio').val();
  var nombre = jQuery('#<portlet:namespace/>nombre').val();
  var dni = jQuery('#<portlet:namespace/>dni').val();
  var molinero = '';
  var molineroInput = jQuery('#<portlet:namespace/>molinero');

  if (molineroInput.length > 0 && !molineroInput.is(':disabled')) {
    molinero = molineroInput.val();
  } else {
    molinero = jQuery('#<portlet:namespace/>molineroHidden').val() || '';
  }
  var estado = jQuery('#<portlet:namespace/>estado').val();
  var provincia = jQuery('#<portlet:namespace/>provincia').val();
  var ddjj = jQuery('#<portlet:namespace/>ddjj').val();
  var vendedor = '';
  var vendedorInput = jQuery('#<portlet:namespace/>vendedor');

  if (vendedorInput.length > 0 && !vendedorInput.is(':disabled')) {
    vendedor = vendedorInput.val();
  }

  jQuery.ajax({
    type: 'POST',
    url: '${guardarNotaURL}',
    data: {
      id: id,
      estadoNuevo: estadoNuevo,
      nota: nota,
      segDesdeDia: segDesdeDia,
      segDesdeMes: segDesdeMes,
      segDesdeAnio: segDesdeAnio,
      segHastaDia: segHastaDia,
      segHastaMes: segHastaMes,
      segHastaAnio: segHastaAnio,
      nombre: nombre,
      dni: dni,
      molinero: molinero,
      estado: estado,
      provincia: provincia,
      ddjj: ddjj,
      vendedor: vendedor
    },
    success: function(){
      <portlet:namespace/>buscarSeguimiento();
    },
    error: function(){
      alert("No se pudo guardar el seguimiento.");
    }
  });
}

jQuery(function() {
  var autoBuscar = '<%= autoBuscar %>';
  if (autoBuscar === '1') {
    <portlet:namespace/>buscarSeguimiento();
  }
});
</script>