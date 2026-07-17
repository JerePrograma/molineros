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
<%@ page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %>

<portlet:defineObjects />

<%
  String modo = (String) request.getAttribute("modo");
  if (modo == null) modo = ParamUtil.getString(request, "modo", "ver");
  boolean soloLectura = "ver".equalsIgnoreCase(modo);

  Map formulario = (Map) request.getAttribute("formulario");
  List<Afiliado> grupo = (List<Afiliado>) request.getAttribute("grupoFamiliar");
  if (formulario == null) formulario = new HashMap();
 
  String idForm = (formulario.get("id_solicitud") != null) ? String.valueOf(formulario.get("id_solicitud")) : "";
  String nombreForm = (formulario.get("nombre") != null) ? String.valueOf(formulario.get("nombre")).trim() : "";
  String apellidoForm = (formulario.get("apellido") != null) ? String.valueOf(formulario.get("apellido")).trim() : "";
  String plan = (formulario.get("plan") != null) ? String.valueOf(formulario.get("plan")) : "";
  

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
  
  Integer provinciaId=0;
  for(Provincia p:provincias){
	  if(fProv.toUpperCase().equals(p.getDescripcion().toUpperCase()) ){
		 provinciaId=p.getId(); 
		 break; 
	  }
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


<h3>COTIZAR - <%= nombreAfiliado %></h3>




<div id="<portlet:namespace/>errorFormulario" class="portlet-msg-error" style="display:none;"></div>

<form action="" method="post" name="<portlet:namespace />fmS">
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

<table>
 <tr>
 <td>
   <div  style="margin-bottom:10px; width: 150px;">
      <input type="button" value="« Volver" onclick="location.href='${volverURL}'" />
   </div>
 </td>
 <td  style="text-align: right;">
         <input type="button" value="Guardar" onClick="<portlet:namespace />guardar();"/>&nbsp;
 </td></tr>        
</table>         

<%
  Calendar cal = CalendarFactoryUtil.getCalendar();
  SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

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
  
  
  String cotizaDia=(String)request.getAttribute("cotizaDia");
  String cotizaMes=(String)request.getAttribute("cotizaMes");
  String cotizaAnio=(String)request.getAttribute("cotizaAnio");

  Date fechaCotiz = null;
	try {
		fechaCotiz =  formato.parse(cotizaDia + "/"
				+ (Integer.parseInt(cotizaMes) + 1) + "/"
				+ cotizaAnio);
	} catch (Exception e) {
		fechaCotiz = null;
	}

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
      <td class="lbl"><label>DNI</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>dni_form"
               value="<%= formulario.get("dni") != null ? String.valueOf(formulario.get("dni")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>

      <td class="lbl"><label>Provincia</label></td>
      <td class="val" colspan="3">
        <input type="text"
               name="<portlet:namespace/>provincia"
               value="<%= formulario.get("provincia") != null ? String.valueOf(formulario.get("provincia")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>
    <tr>
      <td class="lbl"><label>CUIL</label></td>
      <td class="val">
        <input type="text"
               name="<portlet:namespace/>cuil_form"
               value="<%= formulario.get("cuil") != null ? String.valueOf(formulario.get("cuil")) : "" %>"
               <%= soloLectura ? "readonly" : "" %> />
      </td>
    </tr>  
  </table>
</fieldset>

 <table class="lfr-table form-grid">
    <tr>
      <td class="lbl"><label>Fecha de Cotización</label></td>
      <td>
       <liferay-ui:input-date
        dayParam="cotizaDia" monthParam="cotizaMes" yearParam="cotizaAnio"
        dayValue="<%= Integer.parseInt(cotizaDia) %>" monthValue="<%= Integer.parseInt(cotizaMes) %>" yearValue="<%= Integer.parseInt(cotizaAnio) %>"
        yearRangeStart="<%= cal.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= cal.get(Calendar.YEAR) + 20 %>"
        dayNullable="false" monthNullable="false" yearNullable="false" disabled="<%= false %>"/>
      </td> 
      <td>
         <input type="button" value="Recotizar" onClick="<portlet:namespace />recotizar();"/>&nbsp;
      </td> 	
     </tr>
    </table> 


<fieldset class="block-labels">
  <b>Grupo Familiar</b>
<table>
    <tr>
      <td>   
       <div id="<portlet:namespace/>divGrupo">
        <liferay-util:include
						page='/html/portlet/afiliados/formulario_cotizar_grupo.jsp'>
			<liferay-util:param value="<%= plan %>"  name="plan" />
			<liferay-util:param value="<%= provinciaId.toString() %>"  name="provincia" />
			<liferay-util:param value="<%= formato.format(fechaCotiz)%>"  name="fecha" />
		</liferay-util:include>
      </div>
    </td>
   </tr>
</table>
     
</fieldset>




</form>

<br/>


<script type="text/javascript">

function <portlet:namespace />recotizar() {
	
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />'+
	'<liferay-portlet:param name="cmd" value="cotizarFormulario"/>'+
	'<liferay-portlet:param name="origen" value="recot"/>'+
	'<liferay-portlet:param name="tabs1" value="seguimiento-formulario"/>'+
	'</liferay-portlet:renderURL>';
	
	submitForm(document.<portlet:namespace />fmS, url);
	
	
}

function <portlet:namespace />guardar() {
	
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />'+
	'<liferay-portlet:param name="cmd" value="grabarCotizar"/>'+
	'<liferay-portlet:param name="tabs1" value="seguimiento-formulario"/>'+
	'</liferay-portlet:renderURL>';
	
	submitForm(document.<portlet:namespace />fmS, url);
	
	
}

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