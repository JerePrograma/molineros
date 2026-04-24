<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiCuentasBancarias" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.services.AfiCuentasBancariasServiceUtil" %>

<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFolder" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil" %>

<%@ page import="com.liferay.portal.kernel.dao.orm.DynamicQuery" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil" %>

<%@ page import="com.liferay.portal.kernel.util.PortalClassLoaderUtil" %>


<liferay-ui:success key="cuenta-creada-ok" message="Cuenta bancaria guardada correctamente." />
<liferay-ui:success key="cuenta-actualizada-ok" message="Cuenta bancaria actualizada correctamente." />
<liferay-ui:success key="archivo-eliminado-ok" message="Archivo eliminado correctamente." />
<liferay-ui:error key="error-guardar-cuenta" message="Ocurrió un error al guardar la cuenta bancaria." />

<%
String modo = (String) request.getAttribute("modo");
boolean soloLectura = "ver".equalsIgnoreCase(modo);

Afiliado afiliado = (Afiliado) request.getAttribute("AFILIADO_SELECCIONADO");
String cuil = (String) request.getAttribute("cuil");
Integer inte = (Integer) request.getAttribute("inte");
String apellido="";
String nombre="";
String email="";
if(cuil==null){
	cuil = ParamUtil.getString(request, "cuil", "");
	inte = ParamUtil.getInteger(request, "inte");
	apellido = ParamUtil.getString(request, "apellido","");
	nombre = ParamUtil.getString(request, "nombre","");
	email = ParamUtil.getString(request, "email","");
	if(afiliado==null) afiliado=new Afiliado();
	afiliado.setApellido(apellido);
	afiliado.setNombre(nombre);
	afiliado.setEmail(email);
}

List<AfiCuentasBancarias> cuentas = AfiCuentasBancariasServiceUtil.getCuentas(cuil, inte);
AfiCuentasBancarias cuenta = (cuentas != null && !cuentas.isEmpty()) ? cuentas.get(0) : null;

boolean esTitular = (cuenta != null && cuenta.isTitular());

boolean hasNota = (cuenta != null && Validator.isNotNull(cuenta.getFileNotaAutorizada()));
boolean hasCbu  = (cuenta != null && Validator.isNotNull(cuenta.getFileCbu()));

Integer idReintegroApp = (Integer) request.getAttribute("idReintegroApp");
boolean vieneDesdeApp = (idReintegroApp != null && idReintegroApp > 0);

boolean datosApoderadoVacios =
(cuenta == null) ||
(cuenta.isTitular()) ||
(Validator.isNull(cuenta.getNombre()) && Validator.isNull(cuenta.getApellido()));


String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "afiliados";
}
if(renderResponse.getNamespace().equals("_AUT_1_")){
	portlet_name = "autorizaciones";
}
%>

<h3>Cuentas Bancarias</h3>

<form name="<portlet:namespace />fm" id="<portlet:namespace />fm">  

  <input type="hidden" id="<portlet:namespace />hasNota" value="<%= hasNota ? "1" : "0" %>" />
  <input type="hidden" id="<portlet:namespace />hasCbu"  value="<%= hasCbu  ? "1" : "0" %>" />
  <input type="hidden" name="id" value="<%= cuenta != null ? cuenta.getId() : 0 %>"/>
  <input type="hidden" name="cuilTitular" value="<%= cuil %>"/>
  <input type="hidden" name="inte" value="<%= inte %>"/>
  <input type="hidden" id="<portlet:namespace />originalCbu" value="<%= cuenta != null ? cuenta.getCbu() : "" %>" />
  <input type="hidden" id="<portlet:namespace />originalApoderado" 
       value="<%= (cuenta != null && !cuenta.isTitular()) ?
          (cuenta.getCuilCbu() + (cuenta.getNombre() != null ? cuenta.getNombre() : "")) : "" %>" />
  
  <input type="hidden" id="vieneDesdeApp" value="<%= vieneDesdeApp ? "1" : "0" %>" />
  <input type="hidden" id="apoderadoVacioInicial" value="<%= datosApoderadoVacios ? "1" : "0" %>" />
  
  <input type="hidden" name="from" value="<%= ParamUtil.getString(request,"from","") %>"/>
<input type="hidden" name="id_reclamosel" value="<%= ParamUtil.getString(request,"id_reclamosel","0") %>"/>
<input type="hidden" name="tab" value="<%= ParamUtil.getString(request,"tab","") %>"/>
  
  <%
	String urlCbu = "";
	String urlNota = "";
	
	try {
	    long groupId = 10136; 
	    DLFolder folder = DLFolderLocalServiceUtil.getFolder(groupId, 0, "ReclamosPrestacionales");
	    long folderId = folder.getFolderId();
	
	    if (hasCbu) {
	    	DynamicQuery dq = DynamicQueryFactoryUtil
	    		    .forClass(DLFileEntry.class, PortalClassLoaderUtil.getClassLoader())
	    		    .add(RestrictionsFactoryUtil.eq("folderId", folderId))
	    		    .add(RestrictionsFactoryUtil.eq("name", cuenta.getFileCbu()));
	
	    		List<Object> results = DLFileEntryLocalServiceUtil.dynamicQuery(dq);
	
	    		if (!results.isEmpty()) {
	    		    DLFileEntry f = (DLFileEntry) results.get(0);
	
	    		    urlCbu = "/c/document_library/get_file?uuid=" + f.getUuid() +
	    		            "&groupId=" + groupId;
	
	    		}
	    }
	    
	    if (hasNota) {
	    	DynamicQuery dq2 = DynamicQueryFactoryUtil
	    		    .forClass(DLFileEntry.class, PortalClassLoaderUtil.getClassLoader())
	    		    .add(RestrictionsFactoryUtil.eq("folderId", folderId))
	    		    .add(RestrictionsFactoryUtil.eq("name", cuenta.getFileNotaAutorizada()));
	
	    		List<Object> results2 = DLFileEntryLocalServiceUtil.dynamicQuery(dq2);
	
	    		if (!results2.isEmpty()) {
	    		    DLFileEntry f2 = (DLFileEntry) results2.get(0);
	
	    		    urlNota = "/c/document_library/get_file?uuid=" + f2.getUuid() +
	    		            "&groupId=" + groupId;
	
	    		}	
	    }
	
	} catch (Exception e) {
	    System.out.println("ERROR buscando archivo: " + e.getMessage());
	}
%>
  
  
  <table class="lfr-table">
    <tr>
  <td style="width:150px;"><label>Cuil titular:</label></td>
  <td style="width:200px;">
      <input type="text" value="<%= cuil %>" readonly size="13"/>
  </td>

  <td style="width:80px; text-align:right;">
      <label>Inte:</label>
  </td>
  <td style="width:60px;">
      <input type="text" value="<%= inte %>" readonly size="2"/>
  </td>
</tr>


    <tr>
      <td><label>Nombre Afiliado:</label></td>
      <td colspan="3"><input type="text" value="<%= afiliado != null ? (afiliado.getNombre() + ' ' + afiliado.getApellido()) : "" %>" readonly size="40"/></td>
    </tr>

    <tr>
      <td><label>Email:</label></td>
      <td colspan="3">
		  <input type="text" name="email"
		    value="<%= (cuenta != null && cuenta.getEmail() != null && !cuenta.getEmail().trim().isEmpty())
		              ? cuenta.getEmail()
		              : (afiliado != null && afiliado.getEmail() != null ? afiliado.getEmail() : "") %>"
		     size="40"/>
		</td>
    </tr>

    <tr>
      <td><label>CBU:</label></td>
      	<%
	    String cbuParam = ParamUtil.getString(request, "cbu", "");
	    String valorCbu = !cbuParam.isEmpty() ? cbuParam : (cuenta != null ? cuenta.getCbu() : "");
		%>
		
		<td colspan="3">
		    <input type="text" name="cbu" 
		           value="<%= valorCbu %>" 
		           maxlength="22" size="40" 
		           required <%= soloLectura ? "readonly" : "" %>/>
		</td>
    </tr>          
    
    <tr>
  <td><label>Constancia CBU:</label></td>
  <td colspan="3">
    <div style="display:flex;align-items:center;gap:8px;">
      <% if (hasCbu) { 
           String nombreCbu = cuenta.getFileCbu()
               .substring(cuenta.getFileCbu().lastIndexOf('/') + 1);
      %>
        <span id="<portlet:namespace />cbuBox">        
          <span style="margin-left:6px;"><%= nombreCbu %></span>
          <% if (!soloLectura) { %>
            <button type="button"
			        style="font-size:8px; padding:2px 6px; line-height:10px;"
			        onclick="marcarEliminado('<portlet:namespace />hasCbu'); eliminarArchivo('fileCbu')">
			  X
			</button>
          <% } %>
          <img
		    src="<%= themeDisplay.getPathThemeImages() %>/common/view.png"
		    alt="Ver constancia CBU"
		    style="cursor:pointer; vertical-align:middle; width:22px; height:22px;"
		    onclick="window.open('<%= urlCbu %>', '_blank');"
			/>
        </span>
      <% } else { %>
        <input type="file" name="fileCbu" />
      <% } %>
    </div>
  </td>
</tr>

	
    <tr>
	  <td colspan="4">
	    <div style="
	      background:#f0f8ff;
	      border:1px solid #7aa7c7;
	      padding:10px;
	      margin:10px 0;
	      border-radius:6px;
	      font-weight:bold;">
	      Si el CBU es de un apoderado debe seleccionar el check Apoderado, completar los campos Nombre, Apellido y CUIL del apoderado y por último subir la Nota Autorizante.
	    </div>
	  </td>
	</tr>

<% boolean esApoderadoForm = (cuenta != null && !cuenta.isTitular()); %>

    <tr>
      <td colspan="4">
	    <input type="checkbox"
       name="esApoderado"
       id="apoderadoCheck"
       value="true"
       onclick="toggleTitular(this)"
       <%= esApoderadoForm ? "checked" : "" %>
       />
Apoderado

	  </td>
    </tr>

	<tr>
		<td colspan="4">
			<fieldset style="border:1px solid #aaa; padding:10px; border-radius:6px;">
			  <legend style="font-weight:bold;">Datos del Apoderado</legend>		
				  <table>
				    <tr>
				      <td><label>Nombre Apoderado:</label></td>
				      <td><input type="text" name="nombreApoderado" onkeypress="soloLetras(event)" value="<%= (cuenta != null && !cuenta.isTitular()) ? cuenta.getNombre() : "" %>" <%= soloLectura ? "readonly" : esTitular ? "disabled" : "" %> /></td>
				      <td><label>Apellido Apoderado:</label></td>
				      <td><input type="text" name="apellidoApoderado" onkeypress="soloLetras(event)" value="<%= (cuenta != null && !cuenta.isTitular()) ? cuenta.getApellido() : "" %>" <%= soloLectura ? "readonly" : esTitular ? "disabled" : "" %> /></td>
				    </tr>
					
					<%
					String cuilApoderadoMostrar = "";
				
				    if (cuenta != null && !cuenta.isTitular() && cuenta.getCuilCbu() != null) {
				        cuilApoderadoMostrar = cuenta.getCuilCbu();
				    }
					%>
				    <tr>
					  <td><label>CUIL Apoderado:</label></td>
					  <td colspan="3">
					    <input type="text"
					           name="cuilCbu"
					           onkeypress="soloNumeros(event)"
					           value="<%= cuilApoderadoMostrar %>"
					           size="20"
					           <%= soloLectura ? "readonly" : esTitular ? "disabled" : "" %> />
					  </td>
					</tr>
					
					<tr>
  <td><label>Nota autorizante:</label></td>
  <td colspan="3">
    <div style="display:flex;align-items:center;gap:8px;">
      <% if (hasNota) { 
           String nombreNota = cuenta.getFileNotaAutorizada()
               .substring(cuenta.getFileNotaAutorizada().lastIndexOf('/') + 1);
      %>
        <span id="<portlet:namespace />notaBox">
        <span style="margin-left:6px;"><%= nombreNota %></span>
        <% if (!soloLectura) { %>
            <button type="button"
			        style="font-size:8px; padding:2px 6px; line-height:10px;"
			        onclick="marcarEliminado('<portlet:namespace />hasNota'); eliminarArchivo('fileNotaAutorizada')">
			  X
			</button>
          <% } %>
          <img
		    src="<%= themeDisplay.getPathThemeImages() %>/common/view.png"
		    alt="Ver nota autorizante"
		    style="cursor:pointer; vertical-align:middle; width:22px; height:22px;"
		    onclick="window.open('<%= urlNota %>', '_blank');"
			/>
          
        </span>
      <% } else if (!soloLectura) { %>
        <input type="file" name="fileNotaAutorizada" <%= esTitular ? "disabled" : "" %> />
      <% } %>
    </div>
  </td>
</tr>

				</table>			
			</fieldset>
		</td>
	</tr>

    

    <tr>
	  <td colspan="4" align="left">
	  	<% if (!soloLectura) { %>
		    <input type="button"
		       value="<%= (cuenta == null) ? "Guardar" : "Actualizar" %>"
		       onclick="<portlet:namespace />guardarCuenta();" />
	   <% } %>
	  </td>
	</tr>
  </table>
</form>

<portlet:actionURL var="urlGuardarAfi">
    <portlet:param name="struts_action" value="/afiliados/guardar_cuenta_bancaria" />
</portlet:actionURL>

<portlet:actionURL var="urlGuardarAut">
    <portlet:param name="struts_action" value="/autorizaciones/guardar_cuenta_bancaria" />
    <portlet:param name="returnTo" value="autorizaciones" />
</portlet:actionURL>


<script type="text/javascript">
function marcarEliminado(idHidden) {
	  var el = document.getElementById(idHidden);
	  if (el) el.value = "0";
	}
	
function <portlet:namespace />guardarCuenta() {
	  //var form = document.<portlet:namespace />fm;
	  
	  //no necesita saber el namespace
	  var form = document.querySelector('form[name$="fm"]');


	  var esApoderado = form.esApoderado.checked;
	  var titular = !esApoderado;
	  var cbu = form.cbu.value ? jQuery.trim(form.cbu.value) : "";
	  var nombreApoderado = form.nombreApoderado ? jQuery.trim(form.nombreApoderado.value)  : "";
	  var apellidoApoderado = form.apellidoApoderado ? jQuery.trim(form.apellidoApoderado.value): "";
	  var cuilCbu = form.cuilCbu ? jQuery.trim(form.cuilCbu.value) : "";
	  var fileNota = form.fileNotaAutorizada ? form.fileNotaAutorizada.value : "";
	  var fileCbu = form.fileCbu ? form.fileCbu.value : "";
	  
	  var hasNota = document.getElementById("<portlet:namespace />hasNota").value === "1";
	  var hasCbu  = document.getElementById("<portlet:namespace />hasCbu").value  === "1";
	  
	  var existeNota = (!!fileNota) || hasNota;
	  var existeCbu  = (!!fileCbu)  || hasCbu;

	  if (!validarCBU(cbu, "El CBU ingresado es inválido.")) return false;

	  if (titular) {
	    //titular solo requiere constancia CBU
	    if (!existeCbu) {
	      alert("Debe adjuntar la constancia de CBU.");
	      return false;
	    }
	    
	 	//si antes era apoderado y ahora pasa a titular
	    var titularOriginal = <%= (cuenta != null && cuenta.isTitular()) ? "true" : "false" %>;
	    if (!titularOriginal && titular) {
	      if (!fileCbu && hasCbu) {
	        alert("Está cambiando a cuenta titular. Debe adjuntar nueva constancia de CBU.");
	        return false;
	      }
	    }
	    
	  } else {
	    //datos apoderado
	    if (!nombreApoderado || !apellidoApoderado || !cuilCbu) {
	      alert("Debe completar todos los datos del apoderado.");
	      return false;
	    }
	    if (!existeNota) {
	      alert("Debe adjuntar la nota autorizante.");
	      return false;
	    }
	    if (!existeCbu) {
	      alert("Debe adjuntar la constancia de CBU.");
	      return false;
	    }
	    if (!validarCuil(cuilCbu)) {
	        alert("El CUIL/DNI del apoderado debe tener entre 7 y 11 dígitos.");
	        return false;
	      }
	    
	    // Si el CBU anterior pertenece a una cuenta titular debe actualizar constancia.
	    var cbuOriginal = "<%= cuenta != null ? cuenta.getCbu() : "" %>";
	    var titularOriginal = <%= (cuenta != null && cuenta.isTitular()) ? "true" : "false" %>;

	    if (titularOriginal && !titular) {
	      if (!fileCbu && hasCbu) {
	        alert("Está cambiando a una cuenta de apoderado. Debe adjuntar nueva constancia de CBU.");
	        return false;
	      }
	      if (!fileNota && hasNota) {
	        alert("Está cambiando a una cuenta de apoderado. Debe adjuntar nueva nota autorizante.");
	        return false;
	      }
	    }
	  }
	  
	  //cambio de cbu o apoderado
	  var originalCbu = document.getElementById("<portlet:namespace />originalCbu").value.trim();
	  var originalApoderado = document.getElementById("<portlet:namespace />originalApoderado").value.trim();
	  
	  var cbuCambio = (cbu !== "" && originalCbu !== "" && cbu !== originalCbu);
	  var origNombre   = "<%= (cuenta != null && !cuenta.isTitular()) ? cuenta.getNombre() : "" %>";
	  var origApellido = "<%= (cuenta != null && !cuenta.isTitular()) ? cuenta.getApellido() : "" %>";
	  var origCuil = "<%= cuenta != null ? cuenta.getCuilCbu() : "" %>";

	  var apoderadoCambio = (!titular &&
	      (nombreApoderado !== origNombre ||
	       apellidoApoderado !== origApellido ||
	       cuilCbu !== origCuil));

	  //si cambia CBU, se solicita nueva constancia
	  if (cbuCambio && !fileCbu) {
	      alert("Se cambió CBU, debe adjuntar nueva constancia.");
	      return false;
	  }

	  //si cambio apoderado, debe cargar nueva nota
	  //si vino desde la app (porque los datos del apoderado llegan vacíos por diseño)
	  var vieneDesdeApp = document.getElementById("vieneDesdeApp").value === "1";
	  var apoderadoVacioInicial = document.getElementById("apoderadoVacioInicial").value === "1";

	  // si cambio apoderado, debe cargar nueva nota
	  // excepto si viene desde app y es la primera vez que completa esos datos
	  if (apoderadoCambio && !fileNota) {
	     if (!(vieneDesdeApp && apoderadoVacioInicial)) {
	        alert("Los datos del apoderado fueron modificados, debe adjuntar nueva nota autorizante.");
	        return false;
	     }
	  }
	  
	  //si el usuario no borro la constancia, conservarla
	  var hidCbu = document.getElementById("<portlet:namespace />hasCbu");
	  if (hidCbu && hidCbu.value !== "0") {
	      hidCbu.value = "1";
	  }

	  var namespace = '<%= renderResponse.getNamespace() %>';

	  var url = (namespace === "_AUT_1_")
	      ? "<%= urlGuardarAut %>"
	      : "<%= urlGuardarAfi %>";
	      
	     
	  
	  form.method = 'post';
	  form.enctype = 'multipart/form-data';
	  

	  submitForm(form, url);
	}

//bloquea y habilita campos segun titular
function toggleTitular(chk) {
    //var form = document.<portlet:namespace />fm;

    var form = document.querySelector('form[name$="fm"]');

    // chk.checked = APODERADO
    // por lo tanto los campos deben estar HABILITADOS cuando chk.checked = true
    var disabled = !chk.checked;

    var campos = ["nombreApoderado", "apellidoApoderado", "cuilCbu", "fileNotaAutorizada"];

    for (var i = 0; i < campos.length; i++) {
        var campo = campos[i];
        if (form[campo]) {
            form[campo].disabled = disabled;
        }
    }
}


//desactiva boton actualizar si no hubo cambios
function inicializarControlDeCambios() {
    //var form = document.<portlet:namespace />fm;
    var form = document.querySelector('form[name$="fm"]');

    
    var btnActualizar = form.querySelector('input[type="button"][value="Actualizar"]');
    if (!btnActualizar) return;

    //guarda los valores iniciales del formulario
    var valoresIniciales = {};
    Array.from(form.elements).forEach(function (el) {
        if (el.name && el.type !== "button" && el.type !== "submit") {
            valoresIniciales[el.name] = el.type === "checkbox" ? el.checked : el.value;
        }
    });

    //compara valores actuales con iniciales
    function hayCambios() {
        return Array.from(form.elements).some(function (el) {
            if (!el.name || el.type === "button" || el.type === "submit") return false;
            const valorActual = el.type === "checkbox" ? el.checked : el.value;
            return valoresIniciales[el.name] !== valorActual;
        });
    }

    //actualiza el estado del boton
    function actualizarEstadoBoton() {
        btnActualizar.disabled = !hayCambios();
        btnActualizar.style.opacity = btnActualizar.disabled ? "0.6" : "1";
        btnActualizar.style.cursor = btnActualizar.disabled ? "not-allowed" : "pointer";
    }

    //cambios de los campos
    form.addEventListener("input", actualizarEstadoBoton);
    form.addEventListener("change", actualizarEstadoBoton);

    //verificación inicial
    actualizarEstadoBoton();
}

//bloque checkbox si hay datos de apoderado
function verificarCamposApoderado() {
  //var form = document.<portlet:namespace />fm;
  
  var form = document.querySelector('form[name$="fm"]');
  if (!form) return;

  var nombre   = form.nombreApoderado  ? jQuery.trim(form.nombreApoderado.value)  : "";
  var apellido = form.apellidoApoderado? jQuery.trim(form.apellidoApoderado.value): "";
  var cuil     = form.cuilCbu          ? jQuery.trim(form.cuilCbu.value)          : "";
  var fileNota = form.fileNotaAutorizada ? form.fileNotaAutorizada.value : "";
  var hasNota  = document.getElementById("<portlet:namespace />hasNota").value === "1";

  var checkApoderado = form.esApoderado;

  //si hay datos de apoderado o hay nota (archivo nuevo o ya existente), bloquemos el check
   if (nombre !== "" || apellido !== "" || cuil !== "" || fileNota !== "" || hasNota) {
      checkApoderado.style.pointerEvents = "none"; //bloquea completamente los clicks
      checkApoderado.style.opacity = "0.6";
  } else {
      checkApoderado.style.pointerEvents = "auto"; //habilita el click nuevamente
      checkApoderado.style.opacity = "1";
  }
}

//se ejecuta al cargar
window.onload = function() {
    //var form = document.<portlet:namespace />fm;
    
    var form = document.querySelector('form[name$="fm"]');
    var campos = ["nombreApoderado", "apellidoApoderado", "cuilCbu", "fileNotaAutorizada"];

    for (var i = 0; i < campos.length; i++) {
        if (form[campos[i]]) {
            form[campos[i]].addEventListener("input", verificarCamposApoderado);
            form[campos[i]].addEventListener("change", verificarCamposApoderado);
        }
    }

    //verificación inicial con los valores ya cargados
    verificarCamposApoderado();
    
    var chk = document.getElementById("apoderadoCheck");
    if (chk) toggleTitular(chk);
    
    inicializarControlDeCambios();
    
    //bloquea checkbox si solo lectura
    <% if (soloLectura) { %>
    var chk = document.getElementById("apoderadoCheck");
    if (chk) {
        chk.disabled = true;
        chk.onclick = function(e) { e.preventDefault(); return false; };
        chk.style.pointerEvents = "none";
        chk.style.opacity = "0.6";
        chk.style.cursor = "not-allowed";
    }
    <% } %>
};

function eliminarArchivo(tipoArchivo) {
	  if (!confirm("¿Seguro que desea eliminar este archivo?")) return;

	  var form = document.querySelector('form[name$="fm"]');
	  var idCuenta = form.id.value;
	  var cuil = form.cuilTitular.value;
	  var inte = form.inte.value;

	  var url = '<portlet:actionURL>' +
	              '<portlet:param name="struts_action" value="/afiliados/eliminar_archivo_cuenta" />' +
	              '<portlet:param name="<%= Constants.CMD %>" value="eliminar_archivo_cuenta" />' +
	            '</portlet:actionURL>' +
	            '&tipo=' + encodeURIComponent(tipoArchivo) +
	            '&id=' + encodeURIComponent(idCuenta) +
	            '&cuilTitular=' + encodeURIComponent(cuil) +
	            '&inte=' + encodeURIComponent(inte);

	  //llamada AJAX en lugar de submit
	  jQuery.ajax({
		  url: url,
		  type: 'POST',
		  headers: { "X-Requested-With": "XMLHttpRequest" },
		  success: function() {
		    alert("Archivo eliminado correctamente.");

		 	  //actualiza visualmente el bloque correspondiente
		      if (tipoArchivo === 'fileCbu') {
		        jQuery('#<portlet:namespace />cbuBox').html('<input type="file" name="fileCbu" />');
		        document.getElementById("<portlet:namespace />hasCbu").value = "0";
		      } 
		      else if (tipoArchivo === 'fileNotaAutorizada') {
		        jQuery('#<portlet:namespace />notaBox').html('<input type="file" name="fileNotaAutorizada" />');
		        document.getElementById("<portlet:namespace />hasNota").value = "0";

		        //vuelve a evaluar si ya se pueden habilitar campos de titular
		        verificarCamposApoderado();
		      }
		    },
		    error: function() {
		      alert("Error al eliminar el archivo. Intente nuevamente.");
		    }
		  });
		}


//permite solo letras y espacios en nombre y apellido apoderado
function soloLetras(e) {
  var tecla = e.key;
  var patron = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
  if (!patron.test(tecla)) e.preventDefault();
}
document.addEventListener("input", function(e) {
  if (e.target.name === "nombreApoderado" || e.target.name === "apellidoApoderado") {
    e.target.value = e.target.value.toUpperCase();
  }
});	

//permite solo numeros y valida tamaño
function soloNumeros(e) {
  var tecla = e.key;
  if (!/^[0-9]$/.test(tecla)) {
    e.preventDefault();
  }
}

function validarCuil(cuil) {
  if (!/^\d+$/.test(cuil)) return false; // solo números
  return cuil.length >= 7 && cuil.length <= 11;
}

function validarCBU(input, message){	
	if(input.trim().length==22){
		a=input.substring(0,1);
		b=input.substring(1,2);
		c=input.substring(2,3);
		d=input.substring(3,4);
		
		
		if(a+b+c==0){
			alert('ERROR AL VALIDAR CBU. El CBU debe pertenecer a una entidad Bancaria. No se permiten CBU de billeteras virtuales ni de bancos digitales.');
			return false;
		}
		
		q=input.substring(4,5);
		r=input.substring(5,6);
		s=input.substring(6,7);
		
		valida1=input.substring(7,8);
		//alert(a+' '+b+' '+c+' '+d+' '+q+' '+r+' '+s);
		
		suma1=a*7+b*1+c*3+d*9+q*7+r*1+s*3;
		cadenaVal=suma1.toString().substring(suma1.toString().length-1,suma1.toString().length);
		diferencia1= 10-parseInt(cadenaVal);
		
		if(diferencia1==10){
            diferencia1=0;
    	}
		
		if(valida1!=diferencia1){
			alert('ERROR AL VALIDAR CBU, VERIFIQUE NUMEROS');
			return false;				
		}
		
		a=input.substring(8,9);
		b=input.substring(9,10);
		c=input.substring(10,11);
		d=input.substring(11,12);
		e=input.substring(12,13);
		f=input.substring(13,14);
		g=input.substring(14,15);
		h=input.substring(15,16);
		i=input.substring(16,17);
		j=input.substring(17,18);
		k=input.substring(18,19);
		l=input.substring(19,20);
		m=input.substring(20,21);
		
		//alert(a+' '+b+' '+c+' '+d+' '+e+' '+f+' '+g+' '+h+' '+i+' '+j+' '+k+' '+l+' '+m);
		valida2=input.substring(21,22);
		
		suma2=a*3+b*9+c*7+d*1+e*3+f*9+g*7+h*1+i*3+j*9+k*7+l*1+m*3;
		
		cadenaVal2=suma2.toString().substring(suma2.toString().length-1,suma2.toString().length);
		diferencia2= 10-parseInt(cadenaVal2);			
		
		if(diferencia2==10){
            diferencia2=0;
    	}
		
		if(valida2!=diferencia2){
			alert('Ha ingresado un CBU inválido, por favor, verifique dígitos ingresados');
			return false;				
		}
		
		
		if(isPositiveInteger(input)){
			return true
		}		
	}
	alert(message);
	return false;		
}


</script>

<style>
table.lfr-table td {
  padding: 4px 8px;
}
button {
  margin-left: 8px;
}
</style>

