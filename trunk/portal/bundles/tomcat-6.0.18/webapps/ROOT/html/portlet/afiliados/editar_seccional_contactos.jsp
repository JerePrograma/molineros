<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ page import="ar.com.ospim.afiliados.services.SeccionalServiceUtil" %>
<%
	String viewStr = (String)request.getAttribute("view");
    Seccional seccional=(Seccional)request.getSession().getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
	String accion = (String)request.getSession().getAttribute("accion");
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "afiliados";
	}
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	int id_seccional=seccional!=null?(int)seccional.getId():0;
	List<ClaseBase> cargos=SeccionalServiceUtil.traeCargosSeccional();
	boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);
%>

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<fieldset class="block-labels"><legend>Información Contacto</legend>
  
  
   <legend>Contacto</legend>
	   <table class="lfr-table">
		    <tr>
		    
		      <td><label><liferay-ui:message key="cargo" />:</label></td>
		      <td colspan="1">
		        <select id="<portlet:namespace/>cargoContacto"
				      name="<portlet:namespace/>cargoContacto"  style="width: 150px;">
					  <%	for (ClaseBase cargo : cargos) { %>
					  <option value="<%= cargo.getId() %>"><%=cargo.getDescripcion()%></option>
					  <%} %>
		        </select>
		      </td>
		      
		      <td colspan="1"><label><liferay-ui:message key="nombre" />:</label></td>
			  <td colspan="1"><input id="<portlet:namespace />nombreContacto"
				   name="<portlet:namespace />nombreContacto" size="70" maxlength="70" type="text"/>
			  </td>
			  </tr>
			  
			  <tr align="left">
			    <td>&nbsp;</td>
			</tr>
			
	 </table>
	 <table  class="lfr-table"> 
			  <tr>
			  <td>Tipo Teléfono</td>
			  <td>
			     <select name="<portlet:namespace />tipoTelefonoContacto"
					id="<portlet:namespace />tipoTelefonoContacto" >
						
						<option	value="F">Fijo</option>
						<option	value="M">Móvil</option>
				 </select>
			  </td>
			  
			  <td colspan="1"><label><liferay-ui:message key="telefono" />(Cód.Area + Nro):</label></td>
			  <td colspan="1"><input id="<portlet:namespace />codAreaContacto"  name="<portlet:namespace />codAreaContacto" size="15" maxlength="20" type="text"/> </td>
			  <td colspan="1"><input id="<portlet:namespace />telefonoContacto"  name="<portlet:namespace />telefonoContacto" size="40" maxlength="70" type="text"/> </td>
		      
		      <td>    
		         <c:if test="<%= rolABMSeccionales %>">
		            <input type="button" value="Agregar" 
		            onClick="<portlet:namespace />agregarContactoPersonalSeccional();" />
		         </c:if>    
		     </td>     
            </tr>
            
            <tr>
	          <td valign="top" colspan="15" width="70%">
				<div align="center" id="<portlet:namespace />contactosSeccionalDiv">
					<liferay-util:include page="/html/portlet/afiliados/editar_seccional_asigna_contactos_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			  </td>
            </tr>
            
            
     </table>    
</fieldset>

<table>
      <tr>
         <td>&nbsp;</td>
      </tr>   
      <tr>
       <td>  
          <input id="<portlet:namespace />anterior" value="<liferay-ui:message key="previous"/>"
           title="<liferay-ui:message key="previous" />"  onClick="javascript:submitFormNotSave();"  type="button" />
       </td>
      </tr>
</table>           
 
<input type="hidden" name="<portlet:namespace />id_seccional" id="<portlet:namespace />id_seccional" value="<%=id_seccional%>" />

<input type="hidden" name="<portlet:namespace />id_contacto_personal" id="<portlet:namespace />id_contacto_personal"/>
<input type="hidden" name="<portlet:namespace />edicion_contacto_personal" id="<portlet:namespace />edicion_contacto_personal"/>

<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />

<script type="text/javascript">

function submitFormNotSave(){
	document.getElementById("cambioSolapa").value="cambioSolapa";
	document.getElementById("tabs1").value="datos";
	document.getElementById("view").value="true";
	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_seccional';
		
	var params = "&<%= Constants.CMD %>=" + "CAMBIO_SOLAPA";
	params+= "&accion=" + "CAMBIO_SOLAPA";
	url = url + params;
	submitForm(document.<portlet:namespace />fmSecc, url);
		
}


function <portlet:namespace />validarCamposContactoSeccional(nombre,tel_numero,cod_area){
	var result = true;
	if(nombre==null || nombre==""){
		result=false;
		alert("Debe llenar el Nombre del Contacto.")
	}else if(cod_area==null || cod_area=="" || cod_area=="null"){
		result=false;
		alert("Debe llenar el Código de Area del Contacto.")
	}else if(tel_numero==null || tel_numero==""){
		result=false;
		alert("Debe llenar el Nro de teléfono del Contacto.")
	}
	
	return result;
}

jQuery(document).ready(function() {
});


function <portlet:namespace />agregarContactoPersonalSeccional(){
	var cargoC=jQuery('#<portlet:namespace />cargoContacto').val();
	var cargoD=jQuery('#<portlet:namespace />cargoContacto').find('option:selected').text();
	var tel_numero=jQuery('#<portlet:namespace />telefonoContacto').val();
	var tel_tipo=jQuery('#<portlet:namespace />tipoTelefonoContacto').val();
	var nomyape=jQuery('#<portlet:namespace />nombreContacto').val();
	var idCont = jQuery('#<portlet:namespace/>id_contacto_personal').val();
	var esEdicion=jQuery('#<portlet:namespace/>edicion_contacto_personal').val();
	var cod_area=jQuery('#<portlet:namespace />codAreaContacto').val();
	if(<portlet:namespace />validarCamposContactoSeccional(nomyape,tel_numero,cod_area)){
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_contacto';			
	   url=url+'&tel_tipo=' +tel_tipo
	       +'&tel_numero=' + encodeURI(tel_numero)
		   +'&idContactoC='+ idCont
		   +'&cargoC='+encodeURI(cargoC)
		   +'&cargoD='+encodeURI(cargoD)
		   +'&nomyape='+encodeURI(nomyape)
		   +'&codarea='+encodeURI(cod_area)
		   +'&edicion_contacto_personal='+encodeURI(esEdicion)
		   +'&accion=ADDCONTACTOPERSONAL';
		   
		   			
	   jQuery('#<portlet:namespace />contactosSeccionalDiv').load(url, function() {
		
		jQuery('#<portlet:namespace />cargoContacto').val("");
		jQuery('#<portlet:namespace />telefonoContacto').val("");
		jQuery('#<portlet:namespace />tipoTelefonoContacto').val("");
		jQuery('#<portlet:namespace />nombreContacto').val("");
		jQuery('#<portlet:namespace/>id_contacto_personal').val("");
		jQuery('#<portlet:namespace/>edicion_contacto_personal').val('');
		jQuery('#<portlet:namespace/>codAreaContacto').val('');
	   }
	  );
	}   
	
}


function editarContactoPersonal(idContactoC,cargo,nombre,tipo, numero,codarea){

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_contacto';		
	url=url+'&tipo=' +tipo
	+'&numero=' + encodeURI(numero)
	+'&codarea='+ encodeURI(codarea)
	+'&nombre='+encodeURI(nombre)
	+'&cargo='+encodeURI(cargo)
	+'&idContactoC='+idContactoC
	+'&accion=EDITCONTACTOPERSONAL';			

	jQuery('#<portlet:namespace />contactosSeccionalDiv').load(url, function() {
		jQuery('#<portlet:namespace />cargoContacto').val(cargo);
		jQuery('#<portlet:namespace />telefonoContacto').val(numero);
		jQuery('#<portlet:namespace />tipoTelefonoContacto').val(tipo);
		jQuery('#<portlet:namespace />nombreContacto').val(nombre);
		jQuery('#<portlet:namespace/>id_contacto_personal').val(idContactoC);
		jQuery('#<portlet:namespace/>edicion_contacto_personal').val('SI');
		jQuery('#<portlet:namespace />codAreaContacto').val(codarea);
	});
}

function borraContactoPersonal(idContactoC){
	if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
		return false;
	}else{		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_contacto';		
		url=url+ '&idContactoC='+idContactoC
		+'&accion=DELETECONTACTOPERSONAL';			
		jQuery('#<portlet:namespace />contactosSeccionalDiv').load(url, function() {
					jQuery('#<portlet:namespace />agregandoContacto').hide();  
																	   }
			  );
	}	
}



</script>