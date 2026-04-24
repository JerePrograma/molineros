<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="ar.com.ospim.afiliados.services.TelefonoServiceUtil" %>
<%@ page import="ar.com.ospim.global.beans.Telefono" %>

<%
Afiliado afiliado = (Afiliado) request.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
if (afiliado == null) {
afiliado = (Afiliado) session.getAttribute(ar.com.ospim.afiliados.WebKeysAfiliados.AFILIADO_EN_EDICION);
}

Domicilio afiDomicilio = (Domicilio) request.getAttribute(WebKeysCrm.CRM_AFILIADO_DOMICILIO);
String email = (String) request.getAttribute(WebKeysCrm.CRM_AFILIADO_EMAIL);

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "afiliados";
}
if(renderResponse.getNamespace().equals("_CAI_1_")){
	portlet_name = "cai";
} 	
if(renderResponse.getNamespace().equals("_AUT_1_")){
	portlet_name = "autorizaciones";
} 	

String cuilTitular = (afiliado != null) ? afiliado.getCuil_titular() : ParamUtil.getString(request, "cuil_titular");
int inte = (afiliado != null) ? afiliado.getInte() : ParamUtil.getInteger(request, "inte", -1);

/*reconstruimos al afiliado */
if (afiliado == null && cuilTitular != null && cuilTitular.length() > 0 && inte >= 0) {
    try {
        //grupo familiar y por INTE
        List<Afiliado> grupo = BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(cuilTitular);
        if (grupo != null) {
            for (Afiliado a : grupo) {
                if (a.getInte() == inte) { afiliado = a; break; }
            }
        }
        //busqueda por cuil + inte
        if (afiliado == null) {
            List<Afiliado> res = BusquedaAfiliadoServiceUtil.getBusquedaAfiliados(
                cuilTitular, String.valueOf(inte), "", "", 0, "", ""
            );
            if (res != null && !res.isEmpty()) afiliado = res.get(0);
        }
        if (afiliado != null) {
            request.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
        }
    } catch (Exception ignore) {}
}

String inteParam = ParamUtil.getString(request, "inte");

//uso -1 como "desconocido" si no viene afiliado
int idPar = (afiliado != null) ? afiliado.getId_parentesco()
        : ParamUtil.getInteger(request, "id_parentesco", -1);

//si no es titular, conyuge o concubino usa email del titular
boolean usaEmailTitular = !(idPar == WebKeysAfiliados.PARENTESCO_DEFAULT
     || idPar == WebKeysAfiliados.CONYUGE_DEFAULT
     || idPar == WebKeysAfiliados.CONCUBINO_DEFAULT);

if (usaEmailTitular) {
 Afiliado titularEmail = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuilTitular, 0);
 if (titularEmail != null && StringUtils.checkNotEmpty(titularEmail.getEmail())) {
     email = titularEmail.getEmail();
 }
}

//Si es titular, cónyuge o concubino  usa sus propios teléfonos.
//Si es otro parentesco  usa los del titular (inte = 0)
boolean esTitularOConyugeOConcubino =
(idPar == WebKeysAfiliados.PARENTESCO_DEFAULT)   // 0 Titular
|| (idPar == WebKeysAfiliados.CONYUGE_DEFAULT)      // 1 Conyuge
|| (idPar == WebKeysAfiliados.CONCUBINO_DEFAULT);   // 2 Concubino/a

int inteTelefonos = esTitularOConyugeOConcubino ? inte : 0;

List<Telefono> tels = new ArrayList<>();
if (cuilTitular != null && inteTelefonos >= 0) {
tels = TelefonoServiceUtil.getTelefonos(cuilTitular, inteTelefonos);

}

boolean puedeEditar = true;

boolean esTitular = (idPar == WebKeysAfiliados.PARENTESCO_DEFAULT);

Telefono telCel = null;
Telefono telFijo = null;

for (Telefono t : tels) {
    if ("C".equalsIgnoreCase(t.getTipo())) {
        telCel = t;
    } else if ("F".equalsIgnoreCase(t.getTipo())) {
        telFijo = t;
    }
}
%>

<fieldset class="block-labels">
<legend><liferay-ui:message key="domicilio-empresa-ospim" /></legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<input type="hidden" id="<portlet:namespace />idPar" 
       value="<%= idPar %>" />
	<input type="hidden" id="<portlet:namespace/>id_domicilio" 
		   name="<portlet:namespace/>id_domicilio" value="<%=afiDomicilio!=null?afiDomicilio.getId_domicilio():0%>" >	
	
	<tr>
		<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>provincia"
				name="<portlet:namespace/>provincia" onchange="javascript:filtrarLocalidad();" style="width: 150px;" disabled="disabled">
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=afiDomicilio != null && afiDomicilio.getProvinciaId() == provincia.getId() ? "selected" : ""%>
						<%= afiDomicilio == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1">
			 <div class="selector-localidad">
			   <select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" onchange="javascript:filtrarCodAreaTel();javascript:filtrarCodPostal();"
				style="width: 250px;" disabled="disabled">
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option
						<%=afiDomicilio != null && afiDomicilio.getLocalidadId() == localidad.getId() ? "selected" : ""%>
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			  </select>	
			 </div>
		</td>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td colspan="1" style="vertical-align: top"> 
			<div id="wrap-busqueda-calle">
			    <jsp:include page="/html/portlet/crm/busqueda_calle.jsp" />
			  </div> 
		</td>
		<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />numero"
			name="<portlet:namespace />numero" size="5" maxlength="5"
			type="text"
			value="<%= afiDomicilio != null ? afiDomicilio.getNumero() : new String("") %>"
			onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" 
			<% if (!esTitular) { %> readonly="readonly" <% } %>/></td>
	</tr>
	<tr>
		<div id='divCodPostal' style="float: right;"></div>
		<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="5" maxlength="2" type="text"
			value="<%= afiDomicilio != null && afiDomicilio.getPiso() != null ? afiDomicilio.getPiso() : new String("") %>" 
			<% if (!esTitular) { %> readonly="readonly" <% } %>/></td>
		<td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />dpto"
			name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
			value="<%= afiDomicilio != null && afiDomicilio.getDepto() != null ? afiDomicilio.getDepto() : new String("") %>" 
			<% if (!esTitular) { %> readonly="readonly" <% } %>/></td>
			
		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />cod_postal"
			name="<portlet:namespace />cod_postal" size="5" maxlength="4"
			type="text" value="<%= afiDomicilio != null && afiDomicilio.getPostal_codi() != null ? afiDomicilio.getPostal_codi() : new String("") %>" 
			<% if (!esTitular) { %> readonly="readonly" <% } %>></td>
		<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />barrio"
			name="<portlet:namespace />barrio" size="12" maxlength="50"
			type="text" value="<%= afiDomicilio != null && afiDomicilio.getBarrio() != null ? afiDomicilio.getBarrio() : new String("") %>"  
			<% if (!esTitular) { %> readonly="readonly" <% } %>/></td>
	</tr>
</table>
</fieldset>

<fieldset class="block-labels">
<legend>Correo electrónico y Teléfonos</legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
    <tr>		
		<td colspan="1"><label>Cód Area Cel:</label></td>
		<td colspan="1"><input id="<portlet:namespace />cod_area_celular"
			name="<portlet:namespace />cod_area_celular" size="5" maxlength="5"
		        type="text" value="<%= telCel != null && telCel.getCodigoArea()!=null ? telCel.getCodigoArea() : "" %>"
			<% if (!puedeEditar) { %> readonly="readonly" <%} %> /></td>	
		<td colspan="1"><label><liferay-ui:message key="celular" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />celular"
			name="<portlet:namespace />celular" size="15" maxlength="15"
		        type="text" value="<%= telCel != null && telCel.getNumero()!=null ? telCel.getNumero() : "" %>"
			<% if (!puedeEditar) { %> readonly="readonly" <%} %> /></td>	
	    <td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
		<td colspan="3">
		<input id="<portlet:namespace />email"name="<portlet:namespace />email" size="50" maxlength="50" onblur="javascript:<portlet:namespace />validarEmail();"
			type="text" value="<%= !StringUtils.checkEmpty(email)&&!email.equalsIgnoreCase("null") ?email : "" %>" 
			<% if (!puedeEditar) { %> readonly="readonly" <%} %> /></td>	
<input type="hidden" id="<portlet:namespace />email_original"
       name="<portlet:namespace />email_original"
       value="<%= !StringUtils.checkEmpty(email)&&!email.equalsIgnoreCase("null") ? email.trim().toLowerCase() : "" %>" />

				
		</td>
			
	</tr>
	
	<tr><td>&nbsp; </td></tr>
	
	<tr>
		<td colspan="1"><label>Cód Area Tel:</label></td>
		<td colspan="1"><input id="<portlet:namespace />cod_area_telefono"
			name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5"
		        type="text" value="<%= telFijo != null && telFijo.getCodigoArea()!=null ? telFijo.getCodigoArea() : "" %>"			
			<%= !puedeEditar ? "readonly=\"readonly\"" : "" %>//></td>
		<td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />telefono"
			name="<portlet:namespace />telefono" size="15" maxlength="15"
		        type="text" value="<%= telFijo != null && telFijo.getNumero()!=null ? telFijo.getNumero() : "" %>"
			<%= !puedeEditar ? "readonly=\"readonly\"" : "" %>//></td>
		</tr>		
	
	<!-- <tr>
		<td colspan="8"><label><i>La provincia y localidad no se pueden editar, porque puede implicar un cambio de seccional.</i></label></td>
	</tr> -->
</table>
</fieldset>

<br/>
<table class="lfr-table">
	<tr>
		<td>
			<input type="button" name="aceptarCuil" value="Aceptar" onClick='javascript:confirmaActualizacionDomicilioAfiliado();'  >
		</td>
		<td>
		   <label style="color:red">*Cód.Area sin el 0</label>
		</td>
		<td>
		   <label style="color:red">*Celular sin el 15</label>
		</td>
		<td>
		   <label style="color:red">*Teléfono sin espacios ni guiones</label>
		</td>
		
		<!-- <td>&nbsp;</td>
		<td>
			<input type="button" name="cancelarCuil" value="Cancelar" >
		</td> -->
	</tr>
	<tr>
	    <td>&nbsp;</td>
	    <td>
		   <label style="color:red">ejemplo (011) ingresar solo 11</label>
		</td>
		<td>
		   <label style="color:red">ejemplo (15609999) ingresar 609999</label>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

<script type="text/javascript" >

var integrante = "<%= inteParam %>";

function filtrarCodAreaTel() {
	var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_localidad_cod_area_tel&idLocalidad='+idLocalidad;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />cod_area_telefono").length = 0;
			document.getElementById("<portlet:namespace />cod_area_celular").length = 0;
			document.getElementById("<portlet:namespace />cod_area_tel_laboral").length = 0;
			var obj = jQuery.parseJSON(data);						
			jQuery('#<portlet:namespace />cod_area_telefono').val(obj.codAreaTel);
			jQuery('#<portlet:namespace />cod_area_celular').val(obj.codAreaTel);
			jQuery('#<portlet:namespace />cod_area_tel_laboral').val(obj.codAreaTel);
		}
	});	
}


function filtrarLocalidad() {
	var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_provincia_localidad&idProvincia='+idProvincia;
	jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>localidad").length = 0;
			jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			
			jQuery('.selector-localidad select').html(data).fadeIn();

		}
	});
}

function filtrarCodPostal() {
	var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_localidad_codpostal&idLocalidad='+idLocalidad;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />cod_postal").length = 0;						
			var obj = jQuery.parseJSON(data);						
			jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
		}
	});	
}

function <portlet:namespace />buscarCodPostalOnDiv(e) {
	/* var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode */
	if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
		var calle = jQuery("#<portlet:namespace />calle").val();
		var numero = jQuery("#<portlet:namespace />numero").val();
		if (calle.length > 0 && numero > 0) {				
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
			jQuery("#divCodPostal").load(url);		
			jQuery("#divCodPostal").show();
		} else {        
    		jQuery("#divCodPostal").hide("slow");
   		}
	}
}

function <portlet:namespace />cerrarCodPostal() {	
	jQuery("#divCodPostal").hide("slow");
}

<% if (!esTitular) { %>
jQuery(function ($) {
  // NO titulares: bloquea edicion del buscador de calle
  function freeze() {
    var $box = $('#wrap-busqueda-calle').css('position','relative');

    //evita clicks
    if (!$box.find('.readOnlyShield').length) {
      $('<div class="readOnlyShield"/>').css({
        position:'absolute', top:0, left:0, right:0, bottom:0,
        zIndex:999, background:'transparent', pointerEvents:'auto'
      }).appendTo($box);
    }

    //botones/selects deshabilitados
    $box.find('input[type="text"], input:not([type]), textarea').prop('readOnly', true);
    $box.find('button, input[type="button"], input[type="submit"], select').prop('disabled', true);

    //links sin acción
    $box.find('a').on('click', function (e) { e.preventDefault(); });

    //sin tabulador ni teclado/pegado
    $box.find('input, select, textarea, button, a')
        .attr('tabindex','-1')
        .on('keydown keypress input paste', function (e) { e.preventDefault(); });
  }

  freeze();
  setTimeout(freeze,250);
  if (window.Liferay && Liferay.on) Liferay.on('endNavigate', freeze);
});
<% } %>

</script>