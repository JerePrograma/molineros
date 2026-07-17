<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.liferay.portal.kernel.dao.search.*" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>

<portlet:defineObjects />

<liferay-ui:success key="solicitud_derivada_ok" message="La solicitud fue derivada correctamente." />
<liferay-ui:error key="solicitud_derivada_error" message="No se pudo derivar la solicitud." />

<liferay-ui:success key="solicitud_desasignada_ok" message="La solicitud fue desasignada correctamente." />
<liferay-ui:error key="solicitud_desasignada_error" message="No se pudo desasignar la solicitud." />

<%
  List<Map<String,Object>> resultados = (List<Map<String,Object>>) request.getAttribute("resultados");
  if (resultados == null) resultados = new ArrayList<Map<String,Object>>();
  
  List<Map<String,Object>> vendedores = (List<Map<String,Object>>) request.getAttribute("vendedores");
  if (vendedores == null) vendedores = new ArrayList<Map<String,Object>>();

  List<Map<String,Object>> vendedoresActivos = (List<Map<String,Object>>) request.getAttribute("vendedoresActivos");
  if (vendedoresActivos == null) vendedoresActivos = new ArrayList<Map<String,Object>>();
  
  String viewIcon = themeDisplay.getPathThemeImages() + "/common/view.png";
  String editIcon = themeDisplay.getPathThemeImages() + "/common/edit.png";
  String plusIcon = themeDisplay.getPathThemeImages() + "/common/assign.png";
  String cotizarIcon = themeDisplay.getPathThemeImages() + "/common/subscribe.png";
  
  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  User userLogueado = PortalUtil.getUser(renderRequest);
  
  boolean esComercialAdministrador = (userLogueado != null) &&
		    PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

  boolean esComercialSeguimientoMolineros = (userLogueado != null) &&
		    PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_MOLINEROS);

  boolean esComercialSeguimientoNoMolineros = (userLogueado != null) &&
		    PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_NO_MOLINEROS);

  boolean esComercialConsulta = (userLogueado != null) &&
		    PermissionUtil.userContainsRole(userLogueado, WebKeysAfiliados.COMERCIAL_CONSULTA);
  
  boolean puedeEditarSolicitud =
		    esComercialAdministrador ||
		    esComercialSeguimientoMolineros ||
		    esComercialSeguimientoNoMolineros;

  boolean puedeGestionarAsignacion = esComercialAdministrador;
		
  if (resultados.isEmpty()) {
%>
    <div class="portlet-msg-info" style="margin-top:10px;">
      No se encontraron resultados.
    </div>
<%
  } else {

    PortletURL portletURL = renderResponse.createRenderURL();

    List<String> headerNames = new ArrayList<String>();
    headerNames.add("Fecha ingreso");
    headerNames.add("Nombre");
    headerNames.add("DNI");
    headerNames.add("Provincia");
    headerNames.add("Es Molinero?");
    headerNames.add("DDJJ");
    headerNames.add("Estado");
    headerNames.add("Vendedor");
    headerNames.add("Acciones");

    SearchContainer searchContainer = new SearchContainer(
        renderRequest, null, null,
        SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
        portletURL, headerNames,
        LanguageUtil.get(pageContext, "no-results-were-found")
    );

    searchContainer.setTotal(resultados.size());
    List<ResultRow> resultRows = searchContainer.getResultRows();

    for (int i = 0; i < resultados.size(); i++) {
      Map<String,Object> r = resultados.get(i);
      String idStr = String.valueOf(r.get("id_solicitud"));

      //Fecha dd/MM/yyyy
      String fechaIngreso = "";
      Object fc = r.get("fecha_ingreso");
      if (fc instanceof Timestamp) {
        fechaIngreso = sdf.format(new Date(((Timestamp) fc).getTime()));
      } else if (fc instanceof Date) {
        fechaIngreso = sdf.format((Date) fc);
      } else if (fc != null) {
        String s = String.valueOf(fc);
        int idx = s.indexOf(' ');
        fechaIngreso = (idx > 0) ? s.substring(0, idx) : s;
      }
      
     String estadoRaw = (r.get("estado") != null ? String.valueOf(r.get("estado")) : "pendiente");
	 String e = estadoRaw.trim().toLowerCase().replace(" ", "_");

	String estadoTxt;
	if ("pendiente".equals(e)) estadoTxt = "PENDIENTE";
	else if ("asignada".equals(e)) estadoTxt = "ASIGNADA";
	else if ("contactado".equals(e)) estadoTxt = "CONTACTADO";
	else if ("rechazado".equals(e)) estadoTxt = "RECHAZADO";
	else if ("finalizado".equals(e)) estadoTxt = "FINALIZADO";
	else estadoTxt = estadoRaw.replace("_", " ").toUpperCase();
      
	Object molObj = r.get("es_molinero");
	String esMolineroTxt = "NO";
	boolean esMolinero = false;
	
	if (molObj != null) {
	    String sMol = String.valueOf(molObj).trim();
	    if (Boolean.TRUE.equals(molObj)
	        || "true".equalsIgnoreCase(sMol)
	        || "t".equalsIgnoreCase(sMol)
	        || "1".equals(sMol)) {
	        esMolineroTxt = "SI";
	        esMolinero = true;
	    }
	}
	
	Object ddjjObj = r.get("tiene_ddjj");
	String ddjjTxt = "NO";
	if (ddjjObj != null) {
	    String sDdjj = String.valueOf(ddjjObj).trim();
	    if (Boolean.TRUE.equals(ddjjObj)
	        || "true".equalsIgnoreCase(sDdjj)
	        || "t".equalsIgnoreCase(sDdjj)
	        || "1".equals(sDdjj)) {
	        ddjjTxt = "SI";
	    }
	}

	  String vendedorTxt = Validator.isNotNull(r.get("vendedor")) ? String.valueOf(r.get("vendedor")) : "SIN ASIGNAR";
	  String nombre = Validator.isNotNull(r.get("nombre")) ? String.valueOf(r.get("nombre")).trim().toUpperCase() : "";
	  String apellido = Validator.isNotNull(r.get("apellido")) ? String.valueOf(r.get("apellido")).trim().toUpperCase() : "";

	  nombre = nombre.replaceFirst("^[\\s,.-]+", "");
	  apellido = apellido.replaceFirst("^[\\s,.-]+", "");

	  String nombreTxt = "";
	  if (esMolinero) {
	      nombreTxt = nombre;
	  } else {
	      if (!"".equals(apellido) && !"".equals(nombre)) {
	          nombreTxt = apellido + ", " + nombre;
	      } else if (!"".equals(apellido)) {
	          nombreTxt = apellido;
	      } else {
	          nombreTxt = nombre;
	      }
	  }
	  
	  String provinciaTxt = Validator.isNotNull(r.get("provincia")) ? String.valueOf(r.get("provincia")).toUpperCase() : "";	

      ResultRow row = new ResultRow(r, idStr, i);

      row.addText(fechaIngreso);
      row.addText(nombreTxt);
  	  row.addText(Validator.isNotNull(r.get("dni")) ? String.valueOf(r.get("dni")) : "");
  	  row.addText(provinciaTxt);
      row.addText(esMolineroTxt);
      row.addText(ddjjTxt);
      row.addText(estadoTxt);
      row.addText(vendedorTxt);

      // URLs Ver / Editar
      PortletURL verURL = renderResponse.createRenderURL();
      verURL.setWindowState(LiferayWindowState.MAXIMIZED);
      verURL.setParameter("struts_action", "/afiliados/solicitud_afiliacion");
      verURL.setParameter("tabs1", "seguimiento-formulario");
      verURL.setParameter("cmd", "verFormulario");
      verURL.setParameter("id", idStr);
      verURL.setParameter("modo", "ver");

      String segDesdeDia  = ParamUtil.getString(request, "segDesdeDia");
      String segDesdeMes  = ParamUtil.getString(request, "segDesdeMes");
      String segDesdeAnio = ParamUtil.getString(request, "segDesdeAnio");
      String segHastaDia  = ParamUtil.getString(request, "segHastaDia");
      String segHastaMes  = ParamUtil.getString(request, "segHastaMes");
      String segHastaAnio = ParamUtil.getString(request, "segHastaAnio");
      String fNombre      = ParamUtil.getString(request, "nombre");
      String fDni         = ParamUtil.getString(request, "dni");
      String fMolinero = ParamUtil.getString(request, "molinero");
      String fEstado      = ParamUtil.getString(request, "estado");
      String fProv        = ParamUtil.getString(request, "provincia");

      verURL.setParameter("segDesdeDia", segDesdeDia);
      verURL.setParameter("segDesdeMes", segDesdeMes);
      verURL.setParameter("segDesdeAnio", segDesdeAnio);
      verURL.setParameter("segHastaDia", segHastaDia);
      verURL.setParameter("segHastaMes", segHastaMes);
      verURL.setParameter("segHastaAnio", segHastaAnio);
      verURL.setParameter("nombre", fNombre);
      verURL.setParameter("dni", fDni);
      verURL.setParameter("molinero", fMolinero);
      verURL.setParameter("estado", fEstado);
      verURL.setParameter("provincia", fProv);
     
      
      PortletURL editarURL = renderResponse.createRenderURL();
      editarURL.setWindowState(LiferayWindowState.MAXIMIZED);
      editarURL.setParameter("struts_action", "/afiliados/solicitud_afiliacion");
      editarURL.setParameter("tabs1", "seguimiento-formulario");
      editarURL.setParameter("cmd", "verFormulario");
      editarURL.setParameter("id", idStr);
      editarURL.setParameter("modo", "editar");

      editarURL.setParameter("segDesdeDia", segDesdeDia);
      editarURL.setParameter("segDesdeMes", segDesdeMes);
      editarURL.setParameter("segDesdeAnio", segDesdeAnio);
      editarURL.setParameter("segHastaDia", segHastaDia);
      editarURL.setParameter("segHastaMes", segHastaMes);
      editarURL.setParameter("segHastaAnio", segHastaAnio);
      editarURL.setParameter("nombre", fNombre);
      editarURL.setParameter("dni", fDni);
      editarURL.setParameter("molinero", fMolinero);
      editarURL.setParameter("estado", fEstado);
      editarURL.setParameter("provincia", fProv);

      PortletURL volverURL = renderResponse.createRenderURL();
      volverURL.setWindowState(LiferayWindowState.MAXIMIZED);
      volverURL.setParameter("struts_action", "/afiliados/solicitud_afiliacion");
      volverURL.setParameter("tabs1", "seguimiento-formulario");
      volverURL.setParameter("cmd", "buscarSeguimiento");

      volverURL.setParameter("segDesdeDia", segDesdeDia);
      volverURL.setParameter("segDesdeMes", segDesdeMes);
      volverURL.setParameter("segDesdeAnio", segDesdeAnio);
      volverURL.setParameter("segHastaDia", segHastaDia);
      volverURL.setParameter("segHastaMes", segHastaMes);
      volverURL.setParameter("segHastaAnio", segHastaAnio);
      volverURL.setParameter("nombre", fNombre);
      volverURL.setParameter("dni", fDni);
      volverURL.setParameter("molinero", fMolinero);
      volverURL.setParameter("estado", fEstado);
      volverURL.setParameter("provincia", fProv);
      
      PortletURL derivarURL = renderResponse.createActionURL();
      derivarURL.setParameter("struts_action", "/afiliados/solicitud_afiliacion");
      derivarURL.setParameter("cmd", "derivarSolicitud");
      derivarURL.setParameter("idSolicitud", String.valueOf(r.get("id_solicitud")));
      derivarURL.setParameter("redirect", volverURL.toString());

      PortletURL desasignarURL = renderResponse.createActionURL();
      desasignarURL.setParameter("struts_action", "/afiliados/solicitud_afiliacion");
      desasignarURL.setParameter("cmd", "desasignarSolicitud");
      desasignarURL.setParameter("idSolicitud", String.valueOf(r.get("id_solicitud")));
      desasignarURL.setParameter("redirect", volverURL.toString());
      
      
      PortletURL cotizarURL = renderResponse.createRenderURL();
      cotizarURL.setWindowState(LiferayWindowState.MAXIMIZED);
      cotizarURL.setParameter("struts_action", "/afiliados/solicitud_afiliacion");
      cotizarURL.setParameter("tabs1", "seguimiento-formulario");
      cotizarURL.setParameter("cmd", "cotizarFormulario");
      cotizarURL.setParameter("id", idStr);
      cotizarURL.setParameter("modo", "editar");
      cotizarURL.setParameter("origen", "cot");
      
      cotizarURL.setParameter("segDesdeDia", segDesdeDia);
      cotizarURL.setParameter("segDesdeMes", segDesdeMes);
      cotizarURL.setParameter("segDesdeAnio", segDesdeAnio);
      cotizarURL.setParameter("segHastaDia", segHastaDia);
      cotizarURL.setParameter("segHastaMes", segHastaMes);
      cotizarURL.setParameter("segHastaAnio", segHastaAnio);
      cotizarURL.setParameter("nombre", fNombre);
      cotizarURL.setParameter("dni", fDni);
      cotizarURL.setParameter("molinero", fMolinero);
      cotizarURL.setParameter("estado", fEstado);
      cotizarURL.setParameter("provincia", fProv);
      
      
      String acciones =
    	      "<div style='display:inline-flex; gap:6px; align-items:center; white-space:nowrap;'>" +
    	        "<a title='Ver' href='" + verURL.toString() + "'>" +
    	          "<img src='" + viewIcon + "' alt='Ver' style='border:none;vertical-align:middle;'/>" +
    	        "</a>";

    	if (puedeEditarSolicitud) {
    	    acciones +=
    	        "<a title='Editar' href='" + editarURL.toString() + "'>" +
    	          "<img src='" + editIcon + "' alt='Editar' style='border:none;vertical-align:middle;'/>" +
    	        "</a>";
    	}

    	boolean tieneVendedor = Validator.isNotNull(r.get("vendedor"));

    	if (puedeGestionarAsignacion) {
    	    acciones +=
    	      "<button type='button' title='Gestionar asignación' " +
    	              "onclick=\"mostrarPopupAsignacion('" + idStr + "', '" + (tieneVendedor ? "1" : "0") + "', '" + (esMolinero ? "1" : "0") + "');\" " +
    	              "style='background:none;border:none;padding:0;margin:0;cursor:pointer;vertical-align:middle;'>" +
    	        "<img src='" + plusIcon + "' alt='Gestionar asignación' style='border:none;vertical-align:middle;'/>" +
    	      "</button>";
    	}
    	
//Falta ver en que condiciones mostrar
    	 acciones +=
     	        "<a title='Cotizar' href='" + cotizarURL.toString() + "'>" +
     	          "<img src='" + cotizarIcon + "' alt='Cotizar' style='border:none;vertical-align:middle;'/>" +
     	        "</a>";
    	

    	acciones += "</div>";

      row.addText(acciones);

      resultRows.add(row);
    }
%>
    <liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
  }
%>

<div id="<portlet:namespace/>popupOverlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.30); z-index:9998;"></div>

<div id="<portlet:namespace/>popupAsignacion" style="display:none; position:fixed; top:30%; left:50%; transform:translate(-50%, -30%); background:#fff; border:1px solid #999; padding:15px; z-index:9999; min-width:320px; box-shadow:0 2px 10px rgba(0,0,0,0.3);">
    <input type="hidden" id="<portlet:namespace/>popupIdSolicitud" value="" />

<div id="<portlet:namespace/>popupTitulo" style="font-weight:bold; margin-bottom:12px;">Gestionar asignación</div>

    <div style="margin-bottom:10px;">
        <label>Derivar a:</label><br/>
        <select id="<portlet:namespace/>popupVendedorDestino" style="width:100%;">
		    <option value="">Seleccione</option>
		    <%
		    for (Map<String,Object> v : vendedoresActivos) {
		        String vid = String.valueOf(v.get("id"));
		        String vnom = String.valueOf(v.get("nombre"));
		        String vape = String.valueOf(v.get("apellido"));
		    %>
		        <option value="<%= vid %>"><%= HtmlUtil.escape(vape + ", " + vnom) %></option>
		    <%
		    }
		    %>
		</select>
    </div>

   <div style="display:flex; gap:8px; flex-wrap:wrap;">
    <button type="button" onclick="<portlet:namespace/>derivarSolicitudPopup();">Derivar</button>
    <button type="button" id="<portlet:namespace/>btnDesasignar" onclick="<portlet:namespace/>desasignarSolicitudPopup();">Desasignar</button>
    <button type="button" onclick="<portlet:namespace/>cerrarPopupAsignacion();">Cerrar</button>
</div>
</div>

<portlet:actionURL var="derivarSolicitudPopupURL">
    <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
    <portlet:param name="cmd" value="derivarSolicitud" />
</portlet:actionURL>

<portlet:actionURL var="desasignarSolicitudPopupURL">
    <portlet:param name="struts_action" value="/afiliados/solicitud_afiliacion" />
    <portlet:param name="cmd" value="desasignarSolicitud" />
</portlet:actionURL>

<script type="text/javascript">
function ocultarMensaje(id) {
    var el = document.getElementById(id);
    if (el) {
        el.style.transition = 'opacity 0.5s';
        el.style.opacity = '0';
        setTimeout(function() {
            el.style.display = 'none';
        }, 500);
    }
}

function mostrarPopupAsignacion(idSolicitud, tieneVendedor, esMolinero) {
    jQuery('#<portlet:namespace/>popupIdSolicitud').val(idSolicitud);
    jQuery('#<portlet:namespace/>popupVendedorDestino').val('');

    if (tieneVendedor === '1') {
        jQuery('#<portlet:namespace/>btnDesasignar').show();
        jQuery('#<portlet:namespace/>popupTitulo').text('Gestionar asignación');
    } else {
        jQuery('#<portlet:namespace/>btnDesasignar').hide();

        if (esMolinero === '1') {
            jQuery('#<portlet:namespace/>popupTitulo').text('Asignar solicitud molinero');
        } else {
            jQuery('#<portlet:namespace/>popupTitulo').text('Reasignar solicitud');
        }
    }

    jQuery('#<portlet:namespace/>popupOverlay').show();
    jQuery('#<portlet:namespace/>popupAsignacion').show();
}

function <portlet:namespace/>cerrarPopupAsignacion() {
    jQuery('#<portlet:namespace/>popupAsignacion').hide();
    jQuery('#<portlet:namespace/>popupOverlay').hide();
}

function <portlet:namespace/>derivarSolicitudPopup() {
    var idSolicitud = jQuery('#<portlet:namespace/>popupIdSolicitud').val();
    var idVendedorDestino = jQuery('#<portlet:namespace/>popupVendedorDestino').val();

    if (!idVendedorDestino) {
        alert('Seleccione un vendedor.');
        return;
    }

    jQuery.ajax({
        type: 'POST',
        url: '${derivarSolicitudPopupURL}',
        data: {
            idSolicitud: idSolicitud,
            idVendedorDestino: idVendedorDestino
        },
        success: function() {
            <portlet:namespace/>cerrarPopupAsignacion();
            <portlet:namespace/>buscarSeguimiento();
        },
        error: function() {
            alert('No se pudo derivar la solicitud.');
        }
    });
}

function <portlet:namespace/>desasignarSolicitudPopup() {
    var idSolicitud = jQuery('#<portlet:namespace/>popupIdSolicitud').val();

    jQuery.ajax({
        type: 'POST',
        url: '${desasignarSolicitudPopupURL}',
        data: {
            idSolicitud: idSolicitud
        },
        success: function() {
            <portlet:namespace/>cerrarPopupAsignacion();
            <portlet:namespace/>buscarSeguimiento();
        },
        error: function() {
            alert('No se pudo desasignar la solicitud.');
        }
    });
}
</script>
