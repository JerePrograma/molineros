<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.*" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<portlet:defineObjects />

<%
  String modo = (String) request.getAttribute("modo");
  if (modo == null) modo = ParamUtil.getString(request, "modo", "ver");
  boolean soloLectura = "ver".equalsIgnoreCase(modo);

  Map formulario = (Map) request.getAttribute("formulario");
  List historial = (List) request.getAttribute("historial");
  if (formulario == null) formulario = new HashMap();
  if (historial == null) historial = new ArrayList();

  String idForm = (formulario.get("id_solicitud") != null) ? String.valueOf(formulario.get("id_solicitud")) : "";
  String nombreForm = (formulario.get("nombre") != null) ? String.valueOf(formulario.get("nombre")).trim() : "";
  String apellidoForm = (formulario.get("apellido") != null) ? String.valueOf(formulario.get("apellido")).trim() : "";

  Object molObjTmp = formulario.get("es_molinero");
  String molStrTmp = (molObjTmp == null) ? "" : String.valueOf(molObjTmp).trim();
  boolean esMolineroTmp =
      Boolean.TRUE.equals(molObjTmp) ||
      "true".equalsIgnoreCase(molStrTmp) ||
      "t".equalsIgnoreCase(molStrTmp) ||
      "1".equals(molStrTmp);

  String nombreAfiliado = "";
  if (esMolineroTmp) {
      nombreAfiliado = nombreForm;
  } else {
      if (!"".equals(apellidoForm) && !"".equals(nombreForm)) {
          nombreAfiliado = apellidoForm + ", " + nombreForm;
      } else if (!"".equals(apellidoForm)) {
          nombreAfiliado = apellidoForm;
      } else {
          nombreAfiliado = nombreForm;
      }
  }
  
  String segDesdeDia  = ParamUtil.getString(request, "segDesdeDia", "");
  String segDesdeMes  = ParamUtil.getString(request, "segDesdeMes", "");
  String segDesdeAnio = ParamUtil.getString(request, "segDesdeAnio", "");
  String segHastaDia  = ParamUtil.getString(request, "segHastaDia", "");
  String segHastaMes  = ParamUtil.getString(request, "segHastaMes", "");
  String segHastaAnio = ParamUtil.getString(request, "segHastaAnio", "");

  String fNombre = ParamUtil.getString(request, "nombre", "");
  String fApellido = ParamUtil.getString(request, "apellido", "");
  String fDni    = ParamUtil.getString(request, "dni", "");
  String fEstado = ParamUtil.getString(request, "estado", "");
  String fProv   = ParamUtil.getString(request, "provincia", "");
  String fMolinero = ParamUtil.getString(request, "molinero", "");

  String fechaIngresoStr = "";
  Object fc = formulario.get("fecha_ingreso");
  if (fc instanceof Timestamp) {
    fechaIngresoStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date(((Timestamp) fc).getTime()));
  } else if (fc instanceof Date) {
    fechaIngresoStr = new SimpleDateFormat("dd/MM/yyyy").format((Date) fc);
  } else if (fc != null) {
    String s = String.valueOf(fc).trim();
    if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
      fechaIngresoStr = s.substring(8,10) + "/" + s.substring(5,7) + "/" + s.substring(0,4);
    } else {
      int idx = s.indexOf(' ');
      String base = (idx > 0) ? s.substring(0, idx) : s;
      fechaIngresoStr = base;
    }
  }

  String fechaNacEdad = "";
  Object fn = formulario.get("fecha_nacimiento");
  Object edadObj = formulario.get("edad");

  String fnTxt = "";
  if (fn instanceof Timestamp) {
    fnTxt = new SimpleDateFormat("dd/MM/yyyy").format(new Date(((Timestamp) fn).getTime()));
  } else if (fn instanceof Date) {
    fnTxt = new SimpleDateFormat("dd/MM/yyyy").format((Date) fn);
  } else if (fn != null) {
    String s = String.valueOf(fn).trim();
    if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
      fnTxt = s.substring(8,10) + "/" + s.substring(5,7) + "/" + s.substring(0,4);
    } else {
      fnTxt = s;
    }
  }

  String edadTxt = (edadObj != null) ? String.valueOf(edadObj) : "";

  if (fnTxt.length() > 0 && edadTxt.length() > 0) {
      fechaNacEdad = fnTxt + " (" + edadTxt + " años)";
  } else if (fnTxt.length() > 0) {
      fechaNacEdad = fnTxt;
  } else if (edadTxt.length() > 0) {
      fechaNacEdad = edadTxt + " años";
  }

  String parejaTxt = "No";
  Object tieneParejaObj = formulario.get("tiene_pareja");
  String tieneParejaStr = (tieneParejaObj == null) ? "" : String.valueOf(tieneParejaObj).trim();

  boolean tienePareja =
      Boolean.TRUE.equals(tieneParejaObj) ||
      "true".equalsIgnoreCase(tieneParejaStr) ||
      "t".equalsIgnoreCase(tieneParejaStr) ||
      "1".equals(tieneParejaStr);

  if (tienePareja) {
      parejaTxt = "Sí";
      if (formulario.get("edad_pareja") != null && !"".equals(String.valueOf(formulario.get("edad_pareja")).trim())) {
          parejaTxt += " (" + String.valueOf(formulario.get("edad_pareja")) + " años)";
      }
  }

  String molineroTxt = "No";
  Object molObj = formulario.get("es_molinero");
  String molStr = (molObj == null) ? "" : String.valueOf(molObj).trim();
  if (Boolean.TRUE.equals(molObj) ||
      "true".equalsIgnoreCase(molStr) ||
      "t".equalsIgnoreCase(molStr) ||
      "1".equals(molStr)) {
      molineroTxt = "Sí";
  }

  String relacionDependenciaTxt = "No";
  Object relObj = formulario.get("relacion_dependencia");
  String relStr = (relObj == null) ? "" : String.valueOf(relObj).trim();
  if (Boolean.TRUE.equals(relObj) ||
      "true".equalsIgnoreCase(relStr) ||
      "t".equalsIgnoreCase(relStr) ||
      "1".equals(relStr)) {
      relacionDependenciaTxt = "Sí";
  }

  boolean tieneDdjj = false;
  Object tieneDdjjObj = formulario.get("tiene_ddjj");
  String tieneDdjjStr = (tieneDdjjObj == null) ? "" : String.valueOf(tieneDdjjObj).trim();
  tieneDdjj =
      Boolean.TRUE.equals(tieneDdjjObj) ||
      "true".equalsIgnoreCase(tieneDdjjStr) ||
      "t".equalsIgnoreCase(tieneDdjjStr) ||
      "1".equals(tieneDdjjStr);

  String ddjjEstado = (formulario.get("ddjj_estado") != null) ? String.valueOf(formulario.get("ddjj_estado")).trim() : "";
  if (ddjjEstado.length() > 0) {
    ddjjEstado = ddjjEstado.replace('_', ' ').toUpperCase();
  }
%>

<liferay-ui:success key="seguimiento_guardado_ok" message="Guardado con éxito." />
<liferay-ui:error key="seguimiento_guardado_error" message="No se pudo guardar el seguimiento." />

<%
Object errorDetalleObj = SessionErrors.contains(renderRequest, "seguimiento_guardado_error_detalle")
    ? SessionErrors.get(renderRequest, "seguimiento_guardado_error_detalle")
    : null;

String errorDetalle = (errorDetalleObj != null) ? String.valueOf(errorDetalleObj) : "";
if (Validator.isNotNull(errorDetalle)) {
%>
  <div class="portlet-msg-error"><%= errorDetalle %></div>
<%
}
%>

<portlet:actionURL var="guardarNotaURL">
  <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="guardarNotaSeguimiento" />
</portlet:actionURL>

<portlet:renderURL var="volverURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
  <portlet:param name="struts_action" value="/afiliados/view" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="buscarSeguimiento" />
  <portlet:param name="autoBuscar" value="1" />
  <portlet:param name="segDesdeDia" value="<%= segDesdeDia %>" />
  <portlet:param name="segDesdeMes" value="<%= segDesdeMes %>" />
  <portlet:param name="segDesdeAnio" value="<%= segDesdeAnio %>" />
  <portlet:param name="segHastaDia" value="<%= segHastaDia %>" />
  <portlet:param name="segHastaMes" value="<%= segHastaMes %>" />
  <portlet:param name="segHastaAnio" value="<%= segHastaAnio %>" />
  <portlet:param name="nombre" value="<%= fNombre %>" />
  <portlet:param name="dni" value="<%= fDni %>" />
  <portlet:param name="estado" value="<%= fEstado %>" />
  <portlet:param name="provincia" value="<%= fProv %>" />
  <portlet:param name="molinero" value="<%= fMolinero %>" />
</portlet:renderURL>

<portlet:actionURL var="guardarFormularioURL">
  <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="guardarFormularioAfiliado" />
</portlet:actionURL>

<h3>Detalle Formulario - <%= nombreAfiliado %></h3>

<div style="margin-bottom:10px;">
  <input type="button" value="« Volver" onclick="location.href='${volverURL}'" />
</div>

<portlet:renderURL var="detalleRedirectURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
  <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="verFormulario" />
  <portlet:param name="id" value="<%= idForm %>" />
  <portlet:param name="modo" value="<%= modo %>" />
  <portlet:param name="segDesdeDia" value="<%= segDesdeDia %>" />
  <portlet:param name="segDesdeMes" value="<%= segDesdeMes %>" />
  <portlet:param name="segDesdeAnio" value="<%= segDesdeAnio %>" />
  <portlet:param name="segHastaDia" value="<%= segHastaDia %>" />
  <portlet:param name="segHastaMes" value="<%= segHastaMes %>" />
  <portlet:param name="segHastaAnio" value="<%= segHastaAnio %>" />
  <portlet:param name="nombre" value="<%= fNombre %>" />
  <portlet:param name="dni" value="<%= fDni %>" />
  <portlet:param name="estado" value="<%= fEstado %>" />
  <portlet:param name="provincia" value="<%= fProv %>" />
  <portlet:param name="molinero" value="<%= fMolinero %>" />
</portlet:renderURL>

<portlet:actionURL var="generarLinkDdjjURL">
  <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
  <portlet:param name="tabs1" value="seguimiento-formulario" />
  <portlet:param name="cmd" value="generarLinkDdjjSolicitud" />
</portlet:actionURL>

<div id="<portlet:namespace/>errorFormulario" class="portlet-msg-error" style="display:none;"></div>

<form method="post" action="${guardarFormularioURL}" onsubmit="return <portlet:namespace/>validarFormularioAfiliado();">
<input type="hidden" name="<portlet:namespace/>id" value="<%= idForm %>" />
<input type="hidden" name="<portlet:namespace/>volverDetalle" value="1" />
<input type="hidden" name="<portlet:namespace/>modo" value="<%= modo %>" />
<input type="hidden" name="<portlet:namespace/>segDesdeDia" value="<%= segDesdeDia %>" />
<input type="hidden" name="<portlet:namespace/>segDesdeMes" value="<%= segDesdeMes %>" />
<input type="hidden" name="<portlet:namespace/>segDesdeAnio" value="<%= segDesdeAnio %>" />
<input type="hidden" name="<portlet:namespace/>segHastaDia" value="<%= segHastaDia %>" />
<input type="hidden" name="<portlet:namespace/>segHastaMes" value="<%= segHastaMes %>" />
<input type="hidden" name="<portlet:namespace/>segHastaAnio" value="<%= segHastaAnio %>" />
<input type="hidden" name="<portlet:namespace/>filtroNombre" value="<%= fNombre %>" />
<input type="hidden" name="<portlet:namespace/>filtroDni" value="<%= fDni %>" />
<input type="hidden" name="<portlet:namespace/>filtroEstado" value="<%= fEstado %>" />
<input type="hidden" name="<portlet:namespace/>filtroProvincia" value="<%= fProv %>" />
<input type="hidden" name="<portlet:namespace/>filtroMolinero" value="<%= fMolinero %>" />
<input type="hidden" name="<portlet:namespace/>redirect" value="${detalleRedirectURL}" />

<%
  String tieneParejaActual = "false";
  Object tpObjEdit = formulario.get("tiene_pareja");
  String tpStrEdit = (tpObjEdit == null) ? "" : String.valueOf(tpObjEdit).trim();
  if (Boolean.TRUE.equals(tpObjEdit) || "true".equalsIgnoreCase(tpStrEdit) || "t".equalsIgnoreCase(tpStrEdit) || "1".equals(tpStrEdit)) {
    tieneParejaActual = "true";
  }

  String tieneHijosTxt = "No";
  Object thObjEdit = formulario.get("tiene_hijos");
  String thStrEdit = (thObjEdit == null) ? "" : String.valueOf(thObjEdit).trim();
  boolean tieneHijosActual =
      Boolean.TRUE.equals(thObjEdit) ||
      "true".equalsIgnoreCase(thStrEdit) ||
      "t".equalsIgnoreCase(thStrEdit) ||
      "1".equals(thStrEdit);

  if (tieneHijosActual) {
    tieneHijosTxt = "Sí";
  }

  String ddjjUrl = (formulario.get("ddjj_url") != null) ? String.valueOf(formulario.get("ddjj_url")).trim() : "";
  String solicitudUrl = (formulario.get("url_solicitud") != null) ? String.valueOf(formulario.get("url_solicitud")).trim() : "";
  String contratoUrl = (formulario.get("contrato_url") != null) ? String.valueOf(formulario.get("contrato_url")).trim() : "";

  String solicitudPdf = (formulario.get("pdf_solicitud") != null) ? String.valueOf(formulario.get("pdf_solicitud")).trim() : "";
  String contratoPdf = (formulario.get("contrato_pdf") != null) ? String.valueOf(formulario.get("contrato_pdf")).trim() : "";
  String pdfUrl = (formulario.get("pdf_url") != null) ? String.valueOf(formulario.get("pdf_url")).trim() : "";

  String estadoSolicitudActual = (formulario.get("estado") != null) ? String.valueOf(formulario.get("estado")).trim().toLowerCase() : "";
  String ddjjUrlActual = ddjjUrl;
  
  String ddjjEstadoRaw = (formulario.get("ddjj_estado") != null) ? String.valueOf(formulario.get("ddjj_estado")).trim().toLowerCase() : "";

  String contratoEstadoRaw = (formulario.get("contrato_estado") != null) ? String.valueOf(formulario.get("contrato_estado")).trim().toLowerCase() : "";
  boolean documentosFirmados = "firmado".equals(contratoEstadoRaw);
%>

<fieldset class="block-labels">
  <legend>Datos personales</legend>

  <table class="lfr-table form-grid">
    <tr>
      <td class="lbl"><label>Fecha ingreso</label></td>
      <td class="val"><input type="text" readonly value="<%= fechaIngresoStr %>" /></td>

      <td class="lbl"><label>Plan</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>plan"
               value="<%= formulario.get("plan") != null ? String.valueOf(formulario.get("plan")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Nombre</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>nombre"
               value="<%= formulario.get("nombre") != null ? String.valueOf(formulario.get("nombre")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>

      <td class="lbl"><label>Apellido</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>apellido"
               value="<%= formulario.get("apellido") != null ? String.valueOf(formulario.get("apellido")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Fecha nac</label></td>
      <td class="val">
        <input type="date"
               name="<portlet:namespace/>fecha_nacimiento"
               value="<%= formulario.get("fecha_nacimiento") != null ? String.valueOf(formulario.get("fecha_nacimiento")).substring(0,10) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>

      <td class="lbl"><label>Edad</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>edad"
               value="<%= formulario.get("edad") != null ? String.valueOf(formulario.get("edad")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>DNI</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>dni_form"
               value="<%= formulario.get("dni") != null ? String.valueOf(formulario.get("dni")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>

      <td class="lbl"><label>Email</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>email"
               value="<%= formulario.get("email") != null ? String.valueOf(formulario.get("email")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Código área</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>codigo_area"
               value="<%= formulario.get("codigo_area") != null ? String.valueOf(formulario.get("codigo_area")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>

      <td class="lbl"><label>Teléfono</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>telefono"
               value="<%= formulario.get("telefono") != null ? String.valueOf(formulario.get("telefono")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Provincia</label></td>
      <td class="val" colspan="3">
        <input type="text"
               name="<portlet:namespace/>provincia"
               value="<%= formulario.get("provincia") != null ? String.valueOf(formulario.get("provincia")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>
  </table>
</fieldset>

<fieldset class="block-labels">
  <legend>Situación familiar y laboral</legend>

  <table class="lfr-table form-grid">
    <tr>
      <td class="lbl"><label>Rel. dependencia</label></td>
      <td class="val">
        <% if (soloLectura) { %>
          <input type="text" readonly value="<%= relacionDependenciaTxt %>" />
        <% } else { %>
          <select name="<portlet:namespace/>relacionDependencia">
            <option value="true" <%= "Sí".equals(relacionDependenciaTxt) ? "selected" : "" %>>Sí</option>
            <option value="false" <%= "No".equals(relacionDependenciaTxt) ? "selected" : "" %>>No</option>
          </select>
        <% } %>
      </td>

      <td class="lbl"><label>Es molinero</label></td>
      <td class="val">
        <% if (soloLectura) { %>
          <input type="text" readonly value="<%= molineroTxt %>" />
        <% } else { %>
          <select name="<portlet:namespace/>esMolinero">
            <option value="false" <%= "No".equals(molineroTxt) ? "selected" : "" %>>No</option>
            <option value="true" <%= "Sí".equals(molineroTxt) ? "selected" : "" %>>Sí</option>
          </select>
        <% } %>
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Tiene pareja</label></td>
      <td class="val">
        <% if (soloLectura) { %>
          <input type="text" readonly value="<%= parejaTxt %>" />
        <% } else { %>
          <select name="<portlet:namespace/>tienePareja">
            <option value="false" <%= "false".equals(tieneParejaActual) ? "selected" : "" %>>No</option>
            <option value="true" <%= "true".equals(tieneParejaActual) ? "selected" : "" %>>Sí</option>
          </select>
        <% } %>
      </td>

      <td class="lbl"><label>Edad pareja</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>edad_pareja"
               value="<%= formulario.get("edad_pareja") != null ? String.valueOf(formulario.get("edad_pareja")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Tiene hijos</label></td>
      <td class="val">
        <% if (soloLectura) { %>
          <input type="text" readonly value="<%= tieneHijosTxt %>" />
        <% } else { %>
          <select name="<portlet:namespace/>tieneHijos">
            <option value="false" <%= !tieneHijosActual ? "selected" : "" %>>No</option>
<option value="true" <%= tieneHijosActual ? "selected" : "" %>>Sí</option>
          </select>
        <% } %>
      </td>

      <td class="lbl"><label>Hijos hasta 21 años</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>cantidad_hijos21"
               value="<%= formulario.get("cantidad_hijos21") != null ? String.valueOf(formulario.get("cantidad_hijos21")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Hijos 21 a 25 (estudiante)</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>cantidad_hijos25"
               value="<%= formulario.get("cantidad_hijos25") != null ? String.valueOf(formulario.get("cantidad_hijos25")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>

      <td class="lbl"><label>Sueldo bruto</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>sueldoBruto"
               value="<%= formulario.get("sueldo_bruto") != null ? String.valueOf(formulario.get("sueldo_bruto")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>
  </table>
</fieldset>

<fieldset class="block-labels">
  <legend>Estado y documentación</legend>

  <table class="lfr-table form-grid">
    <tr>
      <td class="lbl"><label>Estado DDJJ</label></td>
      <td class="val">
        <input type="text" readonly value="<%= tieneDdjj ? (ddjjEstado.length() > 0 ? ddjjEstado : "-") : "SIN DDJJ" %>" />
      </td>
    </tr>

    <tr>
      <td class="lbl"><label>Link DDJJ</label></td>
      <td class="val">
        <% if (ddjjUrl.length() > 0) { %>
          <input type="text" id="<portlet:namespace/>ddjjUrl" readonly value="<%= ddjjUrl %>" />
          <input type="button" value="Copiar" onclick="<portlet:namespace/>copiarDdjjUrl();" />
        <% } else { %>
          <span style="color:#888;"></span>
        <% } %>
      </td>
    </tr>

    <% if (documentosFirmados && solicitudUrl.length() > 0) { %>
<tr>
  <td class="lbl"><label>Solicitud firmada</label></td>
  <td class="val" colspan="3">
    <a href="<%= solicitudUrl %>" target="_blank">Ver solicitud</a>
  </td>
</tr>
<% } %>

<% if (documentosFirmados && contratoUrl.length() > 0) { %>
<tr>
  <td class="lbl"><label>Contrato firmado</label></td>
  <td class="val" colspan="3">
    <a href="<%= contratoUrl %>" target="_blank">Ver contrato</a>
  </td>
</tr>
<% } %>

<% if (documentosFirmados && pdfUrl.length() > 0) { %>
<tr>
  <td class="lbl"><label>DDJJ firmada</label></td>
  <td class="val" colspan="3">
    <a href="<%= pdfUrl %>" target="_blank">Ver DDJJ firmada</a>
  </td>
</tr>
<% } %>
  </table>
</fieldset>

<div class="acciones-formulario">
  <% if (!soloLectura && "incompleto".equals(estadoSolicitudActual) && ddjjUrlActual.length() == 0) { %>
    <button type="submit"
            formaction="${generarLinkDdjjURL}"
            name="<portlet:namespace/>cmd"
            value="generarLinkDdjjSolicitud">
      Generar link DDJJ / Solicitud
    </button>
  <% } %>

  <% if (!soloLectura) { %>
    <input type="submit" value="Guardar datos del afiliado" />
  <% } %>
</div>


</form>

<br/>

<fieldset class="block-labels">
  <legend>Gestión / Seguimiento</legend>

  <form method="post" action="${guardarNotaURL}" onsubmit="return <portlet:namespace/>validarGuardarNota();">
    <input type="hidden" name="<portlet:namespace/>id" value="<%= idForm %>" />
    <input type="hidden" name="<portlet:namespace/>volverDetalle" value="1" />
    <input type="hidden" name="<portlet:namespace/>modo" value="<%= modo %>" />
    <input type="hidden" name="<portlet:namespace/>segDesdeDia"  value="<%= segDesdeDia %>" />
    <input type="hidden" name="<portlet:namespace/>segDesdeMes"  value="<%= segDesdeMes %>" />
    <input type="hidden" name="<portlet:namespace/>segDesdeAnio" value="<%= segDesdeAnio %>" />
    <input type="hidden" name="<portlet:namespace/>segHastaDia"  value="<%= segHastaDia %>" />
    <input type="hidden" name="<portlet:namespace/>segHastaMes"  value="<%= segHastaMes %>" />
    <input type="hidden" name="<portlet:namespace/>segHastaAnio" value="<%= segHastaAnio %>" />
    <input type="hidden" name="<portlet:namespace/>nombre"    value="<%= fNombre %>" />
    <input type="hidden" name="<portlet:namespace/>dni"       value="<%= fDni %>" />
    <input type="hidden" name="<portlet:namespace/>estado"    value="<%= fEstado %>" />
    <input type="hidden" name="<portlet:namespace/>provincia" value="<%= fProv %>" />
<input type="hidden" name="<portlet:namespace/>redirect" value="${detalleRedirectURL}" />

    <table class="lfr-table">
      <tr>
        <td class="lbl" style="width:80px;"><label>Estado</label></td>
        <td style="width:220px;">
          <%
            String estadoActualForm = "";
            if (formulario.get("estado") != null) {
              estadoActualForm = String.valueOf(formulario.get("estado")).trim().toLowerCase();
            }
          %>

          <select name="<portlet:namespace/>estadoNuevo" <%= soloLectura ? "disabled" : "" %>>
          <option value="incompleto" <%= "incompleto".equals(estadoActualForm) ? "selected" : "" %>>Incompleto</option>
            <option value="pendiente" <%= "pendiente".equals(estadoActualForm) ? "selected" : "" %>>Pendiente</option>
            <option value="asignada" <%= "asignada".equals(estadoActualForm) ? "selected" : "" %>>Asignada</option>
            <option value="contactado" <%= "contactado".equals(estadoActualForm) ? "selected" : "" %>>Contactado</option>
            <option value="rechazado" <%= "rechazado".equals(estadoActualForm) ? "selected" : "" %>>Rechazado</option>
            <option value="finalizado" <%= "finalizado".equals(estadoActualForm) ? "selected" : "" %>>Finalizado</option>
          </select>
        </td>       

        <td></td>
      </tr>

      <tr>
        <td class="lbl" style="vertical-align:top;"><label>Nota</label></td>
        <td colspan="3">
          <textarea id="<portlet:namespace/>nota"
                    name="<portlet:namespace/>nota"
                    class="notaGrande"
                    placeholder="Escribí una nota..."
                    <%= soloLectura ? "readonly" : "" %>></textarea>
        </td>
      </tr>
      
      <% if (!soloLectura) { %>
	  <tr>
	    <td></td>
	    <td colspan="2" style="text-align:left; padding-top:10px;">
	      <input type="submit" value="Guardar" />
	    </td>
	  </tr>
  	  <% } %>
    </table>
  </form>

  <br/>

  <b>Historial</b>

  <%
    List<Map<String,Object>> hist = (List<Map<String,Object>>) historial;

    List<String> histHeaders = new ArrayList<String>();
    histHeaders.add("Fecha");
    histHeaders.add("Estado");
    histHeaders.add("Nota");
    histHeaders.add("Usuario");

    SearchContainer histContainer =
        new SearchContainer(
            renderRequest, null, null,
            SearchContainer.DEFAULT_CUR_PARAM,
            Integer.MAX_VALUE,
            renderResponse.createRenderURL(),
            histHeaders,
            "Sin resultados."
        );

    histContainer.setTotal(hist.size());
    List<ResultRow> histRows = histContainer.getResultRows();

    for (int i = 0; i < hist.size(); i++) {
      Map<String,Object> h = hist.get(i);

      ResultRow row = new ResultRow(h, String.valueOf(i), i);

      String fechaHist = "";
      Object fh = h.get("fecha_creacion");
      if (fh instanceof Timestamp) {
        fechaHist = new SimpleDateFormat("dd/MM/yyyy").format(new Date(((Timestamp) fh).getTime()));
      } else if (fh instanceof Date) {
        fechaHist = new SimpleDateFormat("dd/MM/yyyy").format((Date) fh);
      } else if (fh != null) {
        String s = String.valueOf(fh).trim();
        if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
          fechaHist = s.substring(8,10) + "/" + s.substring(5,7) + "/" + s.substring(0,4);
        } else {
          fechaHist = s;
        }
      }
      row.addText(fechaHist);
      String estadoHist = h.get("estado") != null ? String.valueOf(h.get("estado")).trim() : "";
      if (estadoHist.length() > 0) estadoHist = estadoHist.replace('_', ' ').toUpperCase();
      row.addText(estadoHist);

      String notaHtml = "<div style='max-width:620px; white-space:normal; word-break:break-word;'>" +
                        (h.get("nota") != null ? String.valueOf(h.get("nota")) : "") +
                        "</div>";
      row.addText(notaHtml);

      row.addText(h.get("usuario") != null ? String.valueOf(h.get("usuario")) : "");

      histRows.add(row);
    }
  %>

  <div id="<portlet:namespace/>historialIterator">
    <liferay-ui:search-iterator searchContainer="<%= histContainer %>" />
  </div>
</fieldset>

<script type="text/javascript">
function <portlet:namespace/>validarGuardarNota() {
  var notaEl = document.getElementById('<portlet:namespace/>nota');
  if (!notaEl) return true;

  var nota = (notaEl.value || '').replace(/\s+/g, ' ').trim();
  if (nota.length === 0) {
    alert("Escribí una nota antes de guardar.");
    notaEl.focus();
    return false;
  }
  return true;
}

function <portlet:namespace/>copiarDdjjUrl() {
  var el = document.getElementById('<portlet:namespace/>ddjjUrl');
  if (!el) return;

  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard.writeText(el.value).then(function() {
      alert('Link copiado');
    });
  } else {
    el.select();
    document.execCommand('copy');
    alert('Link copiado');
  }
}

function <portlet:namespace/>mostrarErrorFormulario(msg) {
  var box = document.getElementById('<portlet:namespace/>errorFormulario');
  if (!box) {
    alert(msg);
    return false;
  }
  box.style.display = '';
  box.innerHTML = msg;
  return false;
}

function <portlet:namespace/>limpiarErrorFormulario() {
  var box = document.getElementById('<portlet:namespace/>errorFormulario');
  if (box) {
    box.style.display = 'none';
    box.innerHTML = '';
  }
}

function <portlet:namespace/>soloDigitos(v) {
  return (v || '').replace(/\D+/g, '');
}

function <portlet:namespace/>validarFormularioAfiliado() {
  <portlet:namespace/>limpiarErrorFormulario();

  var nombreEl = document.getElementsByName('<portlet:namespace/>nombre')[0];
  var apellidoEl = document.getElementsByName('<portlet:namespace/>apellido')[0];
  var planEl = document.getElementsByName('<portlet:namespace/>plan')[0];
  var dniEl = document.getElementsByName('<portlet:namespace/>dni_form')[0];
  var emailEl = document.getElementsByName('<portlet:namespace/>email')[0];
  var codigoAreaEl = document.getElementsByName('<portlet:namespace/>codigo_area')[0];
  var telefonoEl = document.getElementsByName('<portlet:namespace/>telefono')[0];
  var sueldoEl = document.getElementsByName('<portlet:namespace/>sueldoBruto')[0];

  var tieneParejaEl = document.getElementsByName('<portlet:namespace/>tienePareja')[0];
  var edadParejaEl = document.getElementsByName('<portlet:namespace/>edad_pareja')[0];

  var tieneHijosEl = document.getElementsByName('<portlet:namespace/>tieneHijos')[0];
  var hijos21El = document.getElementsByName('<portlet:namespace/>cantidad_hijos21')[0];
  var hijos25El = document.getElementsByName('<portlet:namespace/>cantidad_hijos25')[0];

  var nombre = nombreEl ? (nombreEl.value || '').trim() : '';
  var apellido = apellidoEl ? (apellidoEl.value || '').trim() : '';
  var plan = planEl ? (planEl.value || '').trim() : '';
  var dni = <portlet:namespace/>soloDigitos(dniEl ? dniEl.value : '');
  var email = emailEl ? (emailEl.value || '').trim() : '';
  var codigoArea = <portlet:namespace/>soloDigitos(codigoAreaEl ? codigoAreaEl.value : '');
  var telefono = <portlet:namespace/>soloDigitos(telefonoEl ? telefonoEl.value : '');
  var sueldo = sueldoEl ? (sueldoEl.value || '').trim() : '';

  var tienePareja = ((tieneParejaEl ? tieneParejaEl.value : 'false') === 'true');
  var edadPareja = <portlet:namespace/>soloDigitos(edadParejaEl ? edadParejaEl.value : '');

  var tieneHijos = ((tieneHijosEl ? tieneHijosEl.value : 'false') === 'true');
  var hijos21 = <portlet:namespace/>soloDigitos(hijos21El ? hijos21El.value : '');
  var hijos25 = <portlet:namespace/>soloDigitos(hijos25El ? hijos25El.value : '');

  var soloLetras = /^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/;
  var emailRx = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

  if (!nombre || !soloLetras.test(nombre)) {
    return <portlet:namespace/>mostrarErrorFormulario('El nombre es obligatorio y solo puede contener letras.');
  }

  if (!apellido || !soloLetras.test(apellido)) {
    return <portlet:namespace/>mostrarErrorFormulario('El apellido es obligatorio y solo puede contener letras.');
  }

  if (plan !== 'LUMA 200' && plan !== 'LUMA 400') {
    return <portlet:namespace/>mostrarErrorFormulario('El plan debe ser LUMA 200 o LUMA 400.');
  }

  if (!/^\d{8}$/.test(dni)) {
    return <portlet:namespace/>mostrarErrorFormulario('El DNI debe tener 8 dígitos.');
  }

  if (!emailRx.test(email)) {
    return <portlet:namespace/>mostrarErrorFormulario('El email no es válido.');
  }

  if (codigoArea.length < 2 || codigoArea.length > 4) {
    return <portlet:namespace/>mostrarErrorFormulario('Código de área inválido.');
  }

  if (telefono.length < 6 || telefono.length > 8) {
    return <portlet:namespace/>mostrarErrorFormulario('Teléfono inválido.');
  }

  if ((codigoArea + telefono).length !== 10) {
    return <portlet:namespace/>mostrarErrorFormulario('Código de área + teléfono deben sumar 10 dígitos.');
  }

  if (tienePareja && !edadPareja) {
    return <portlet:namespace/>mostrarErrorFormulario('Si indicó que tiene pareja, debe informar la edad de la pareja.');
  }

  if (tieneHijos) {
    var h21 = hijos21 ? parseInt(hijos21, 10) : 0;
    var h25 = hijos25 ? parseInt(hijos25, 10) : 0;

    if ((h21 + h25) < 1) {
      return <portlet:namespace/>mostrarErrorFormulario('Si indicó que tiene hijos, debe informar al menos una cantidad.');
    }
  }

  if (sueldo !== '' && !/^\d+([.,]\d+)?$/.test(sueldo)) {
    return <portlet:namespace/>mostrarErrorFormulario('El sueldo bruto es inválido.');
  }

  return true;
}

function <portlet:namespace/>toggleParejaHijos() {
  var tieneParejaEl = document.getElementsByName('<portlet:namespace/>tienePareja')[0];
  var edadParejaEl = document.getElementsByName('<portlet:namespace/>edad_pareja')[0];
  var tienePareja = ((tieneParejaEl ? tieneParejaEl.value : 'false') === 'true');

  if (edadParejaEl && !tienePareja) {
    edadParejaEl.value = '';
  }

  var tieneHijosEl = document.getElementsByName('<portlet:namespace/>tieneHijos')[0];
  var h21El = document.getElementsByName('<portlet:namespace/>cantidad_hijos21')[0];
  var h25El = document.getElementsByName('<portlet:namespace/>cantidad_hijos25')[0];
  var tieneHijos = ((tieneHijosEl ? tieneHijosEl.value : 'false') === 'true');

  if (!tieneHijos) {
    if (h21El) h21El.value = '';
    if (h25El) h25El.value = '';
  }
}

document.addEventListener('DOMContentLoaded', function() {
  var tp = document.getElementsByName('<portlet:namespace/>tienePareja')[0];
  var th = document.getElementsByName('<portlet:namespace/>tieneHijos')[0];

  if (tp) tp.addEventListener('change', <portlet:namespace/>toggleParejaHijos);
  if (th) th.addEventListener('change', <portlet:namespace/>toggleParejaHijos);
});
</script>

<style>
td.lbl { width:150px; vertical-align:middle; }
td.val { width:260px; }

.block-labels input[readonly]{
  background:#f6f6f6;
  border:1px solid #cfcfcf;
  padding:3px 6px;
  border-radius:3px;
}

.block-labels input[type="text"]{
  width:95%;
  box-sizing:border-box;
}

table.lfr-table td{
  padding:4px 8px;
}

.notaGrande{
  width: 98%;
  height: 70px;
  padding: 6px 8px;
  box-sizing: border-box;
  resize: vertical;
}

#<portlet:namespace/>historialIterator .taglib-search-iterator-page-iterator-top {
  display: none !important;
}

.form-grid {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
}

.form-grid td.lbl {
  width: 170px;
  vertical-align: middle;
  font-weight: 600;
  color: #444;
}

.form-grid td.val {
  width: 320px;
}

.form-grid input[type="text"],
.form-grid input[type="date"],
.form-grid select {
  width: 100%;
  max-width: 260px;
  box-sizing: border-box;
}

.acciones-formulario {
  margin-top: 18px;
  display: flex;
  gap: 10px;
}
</style>