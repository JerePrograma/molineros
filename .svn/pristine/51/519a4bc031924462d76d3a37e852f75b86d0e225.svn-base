<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%
String nroFarmacia  = "";
boolean esEdicion = false;
boolean inHabilitar = false;
String cmd = (String) request.getAttribute(Constants.CMD);
String portlet_name = ParamUtil.getString(request, "portlet_name");
String pathurl = request.getContextPath();
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "farmaciaospim";
}
Farmacia     farmacia   = (Farmacia)request.getSession().getAttribute(WebKeysFarmaciaOspim.FARMACIA_EN_EDICION );
if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)){
	inHabilitar= true;
}
if (cmd != null && cmd.equalsIgnoreCase(Constants.EDIT)){
    esEdicion = true;	
}
if(farmacia != null  ){
	nroFarmacia ="Nro Registro : " + "000"+  String.valueOf(farmacia.getId_farmacia() );
}

//NUEVO
//Para que vuelva a la vista anterior con el ancho de pantalla MAXIMIZED
PortletURL backURL = renderResponse.createRenderURL();
backURL.setWindowState(LiferayWindowState.MAXIMIZED);
backURL.setParameter("struts_action", "/farmaciaospim/view");
backURL.setParameter("tabs1", "farmacia-ospim");

themeDisplay.getPortletDisplay().setShowBackIcon(true);
themeDisplay.getPortletDisplay().setURLBack(backURL.toString());

%>
<style>

div.divHeaderNro {
  position: absolute;
  top: 210px;
  right:30;
  left:1000px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%
}

</style>

<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />sitmedica_fm" id="<portlet:namespace />sitmedica_fm" >    
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input  type="hidden" id="<portlet:namespace />id_registro_farmacia"	name="<portlet:namespace />id_registro_farmacia" size="8"  value="<%=Validator.isNotNull(farmacia)  ? farmacia.getId_farmacia()   : "0"  %>"  />    
	
		
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="Registro de Farmacia" /> 
	</legend>
	<div class="divHeaderNro">		     
		  <label align='center' ><b> <%=nroFarmacia%> </b>  </label>   
    </div>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td colspan="5"><liferay-util:include
						page="/html/portlet/farmacia/busqueda_padron_entidades.jsp">
						<liferay-util:param name="esEditable"
							value='<%= String.valueOf( !inHabilitar ) %>' />
						<%if (farmacia!= null ) {%>							
						<liferay-util:param name="cuit" value="<%= farmacia.getEmpresa().getCuit() %>"  />
						<liferay-util:param name="sucu" value='000'  />
						<liferay-util:param name="razon" value="" />
						<%}else{ %>
						<liferay-util:param name="cuit" value='' />
						<liferay-util:param name="sucu" value='000' />
						<liferay-util:param name="razon" value='' />
						<%}%>
						<liferay-util:param name="id_seccional" value='' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value=''/>				
					</liferay-util:include></td>
		</tr>
	</table>
	<table   class="lfr-table">
    <tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
		<tr>
		<td colspan="1">
		<label align='center' id="<portlet:namespace/>captionfarmacia"> <liferay-ui:message	key="Farmacia" />	</label>
		</td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />farmacia"
			name="<portlet:namespace />farmacia" size="40" maxlength="60"
			type="text"
			value="<%= farmacia  != null &&  farmacia.getFarmacia()  !=null   ? farmacia.getFarmacia()   : "" %>" />			
		</td>	
		<!-- 	
		<td colspan="1">
		<label align='center' id="<portlet:namespace/>captioncodigo"> <liferay-ui:message	key="codigo" />	</label>
		</td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />codigo"
			name="<portlet:namespace />codigo" size="8" maxlength="8"
			type="text"
			value="<%= farmacia  != null &&  farmacia.getCodigo()  !=null   ? farmacia.getCodigo()   : "" %>" />			
		</td> -->
		<td colspan="2">
		<label align='center' id="<portlet:namespace/>captioncodigofarmacia"><liferay-ui:message	key="codigo-farmacia" /></label>
		</td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />codigofarmacia"
			name="<portlet:namespace />codigofarmacia" size="8" maxlength="8"
			type="text" 
			value="<%= farmacia != null &&  farmacia.getCodigoFarmacia()   !=null   ? farmacia.getCodigoFarmacia()   : "" %>" />			
		</td>
		<!-- 
		<td colspan="2">		
		<label align='center' id="<portlet:namespace/>captioncodeMandataria"><liferay-ui:message	key="codigo-farmacia-mandataria" />	</label>
		</td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />codigofarmaciamandataria"
			name="<portlet:namespace />codigofarmaciamandataria" size="8" maxlength="6"
			type="text" 
			value="<%= farmacia != null &&  farmacia.getCodigoFarmaciaMandataria()    !=null   ? farmacia.getCodigoFarmaciaMandataria()   : "" %>" />			
		</td>
		-->
		<td colspan="2"><label align='center' id="<portlet:namespace/>captionbasedesc">Base Desc</label></td>
		<td colspan="1">
		<select <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
						name="<portlet:namespace/>baseDescuento"
						id="<portlet:namespace />baseDescuento" > 
						<option value=""></option>						
						<option value="PVP" <%=Validator.isNotNull(farmacia) && farmacia.getBaseDto().equals("PVP")    ? "selected" : ""  %>  >PVP</option>
                        <option value="OSP+AMT" <%=Validator.isNotNull(farmacia) && farmacia.getBaseDto().equals("OSP+AMT")  ? "selected" : ""  %>  >OSP+AMT</option>                        																			
		</select>
		</td>
		<td colspan="2"><label align='center' id="<portlet:namespace/>captionpordesc">% Desc</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />porcedesc"
			name="<portlet:namespace />porcedesc" size="5" maxlength="100" onkeydown="allowOnlyDigits(event);"
			type="text"
			value="<%= farmacia != null &&  farmacia.getPorcDescuento()  !=null   ? farmacia.getPorcDescuento()   : "" %>" />			
		</td>
		</tr>
		<tr>
		<td colspan="12">&nbsp;</td>
	    </tr>	
		</table>
		<table  class="lfr-table">
		<tr>
		<td>
		<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="5"><liferay-util:include
					page="/html/portlet/farmacia/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%= farmacia != null && farmacia.getSeccional()!=null && farmacia.getSeccional().getId()>0 ? String.valueOf(farmacia.getSeccional().getId()) : new String()  %>" />
					<liferay-util:param name="seccional"
						value="<%= farmacia != null && farmacia.getSeccional()!=null && farmacia.getSeccional().getDescripcion()!=null ? farmacia.getSeccional().getDescripcion() : new String()  %>" />
                    <% if (!inHabilitar) { %>
					<liferay-util:param name="esEdicion" value="true" />
					<%}else{ %>
					<liferay-util:param name="esEdicion" value="true" />
					<%} %>	
				</liferay-util:include></td>
		</tr>
		</table>	   
		</td>
		<td colspan="1"><label align='center' id="<portlet:namespace/>captioncamara">Camara</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />camara"
			name="<portlet:namespace />camara" size="35" maxlength="65"
			type="text"
			value="<%= farmacia != null &&  farmacia.getCamara()!=null   ? farmacia.getCamara()   : "" %>" />			
		</td>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captioncalle">Calle</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />calle"
			name="<portlet:namespace />calle" size="30" maxlength="45"
			type="text"
			value="<%= farmacia != null &&  farmacia.getCalle()  !=null   ? farmacia.getCalle()  : "" %>" />			
		</td>
		<td colspan="1"><label align='center' id="<portlet:namespace/>captiontelefono">Telefono</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />telefono"
			name="<portlet:namespace />telefono" size="20" maxlength="20"
			type="text" 
			value="<%= farmacia != null &&  farmacia.getTelefono()   !=null   ? farmacia.getTelefono()   : "" %>" />			
		</td>
		</tr>
		<tr>
		<td colspan="12">&nbsp;</td>
	    </tr>
		</table>
		<table   class="lfr-table">
		<tr>
		
		<!--  
		<td>
		<table align='center' class="lfr-table" style="border-collapse: separate; border-spacing: 0px;" >
		<tr>
		<td>
		   <fieldset class="block-labels"><legend><liferay-ui:message	key="buscador-datos-colegio" /></legend> 
	 	   <liferay-util:include page='/html/portlet/farmacia_ospim/busqueda_colegio.jsp'>
                               <% if (!inHabilitar) { %>
                                 <liferay-util:param value="true" name="edit_mode" />
                                 <liferay-util:param value="false" name="inhabilitar" />
							   <%}else{ %>
                                 <liferay-util:param value="false" name="edit_mode" />
                                 <liferay-util:param value="true" name="inhabilitar" />
							   <%} %>					
                               <liferay-util:param name="codigoColegio" value="<%=farmacia!=null?farmacia.getColegio().getCodigo()   :null%>" /> 
                               
		    </liferay-util:include>
		    </fieldset>		
		</td>
		<td>
		      <div id="<portlet:namespace />divNuevoColegio">
		      <a href="javascript:<portlet:namespace />agregarNuevoColegio();" id="<portlet:namespace />nuevoColegio" ><liferay-ui:icon image="add" /></a>
		      </div>
		</td>
		</tr>		
		</table>
		</td>
		-->
		
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>provincia"
				name="<portlet:namespace/>provincia"  <% if (inHabilitar) { %> disabled="disabled" <%} %>
				onchange="javascript:filtrarLocalidad();" style="width: 150px;">
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=farmacia != null && farmacia.getDomicilioDefault().getProvinciaId() == provincia.getId() ? "selected" : ""%>
						<%= farmacia == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1">
			 <div class="selector-localidad">
			   <%if(farmacia != null) {%>
			   <select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (inHabilitar) { %> disabled="disabled" <%} %> 
				 onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option
						<%=farmacia != null && farmacia.getDomicilioDefault().getLocalidadId() == localidad.getId() ? "selected" : ""%>
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			  </select>
			  <%} else{%>
			  	<select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" 
				 onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
				 </select>	
			<%} %>		
			 </div>
			</td> 
		
		
		
		
		
		</tr>
		
		
		
		
</table>
</fieldset>
<br/>
<div  id="<portlet:namespace />saveFarmaciaDiv" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<input type="button" value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />saveFarmacia();"  title="<liferay-ui:message key="Graba los Datos Ingresados." />"/>	
</td>
</tr>		
</table>
</div>


<div  id="<portlet:namespace />diveditFarmacia" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<% if (!inHabilitar ) { %>
<div id='<portlet:namespace />divBotonEdicion'>
<input type="button" value="<liferay-ui:message key="Grabar"  />"
	onClick="<portlet:namespace />editaFarmacia();"  title="<liferay-ui:message key="Edita los Datos Ingresados." />"/>
</div>		
<% }%>
</td>
</tr>
</table>
</div>


<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>

<input	 type="hidden" name="<portlet:namespace />consultaequipointer"  id="<portlet:namespace />consultaequipointer" value="<%=inHabilitar  ? true  : false  %>"/>
<input  type="hidden" id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value="1"/>


<div id='validarExistenciaCuit' style="float: right;"></div>

<%-- <input  type="hidden" id="<portlet:namespace />farmacia"   name="<portlet:namespace />farmacia" value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getFarmacia() )   ? farmacia.getFarmacia()    : ""  %>" /> --%>	
<input  type="hidden" id="<portlet:namespace />cuit"   name="<portlet:namespace />cuit" value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getEmpresa().getCuit() )   ? farmacia.getEmpresa().getCuit()    : ""  %>" />
<input  type="hidden" id="<portlet:namespace />sucursal"   name="<portlet:namespace />sucursal" value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getEmpresa().getSucursal() )   ? farmacia.getEmpresa().getSucursal()    : ""  %>" />		
<input  type="hidden" id="<portlet:namespace />id_seccional_sel"	name="<portlet:namespace />id_seccional_sel" size="8"  value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getSeccional()  )   ? farmacia.getSeccional().getId_seccional()      : ""  %>"	type="text"  />
<input  type="hidden" id="<portlet:namespace />nombre_seccional_sel"	name="<portlet:namespace />nombre_seccional_sel" size="8"  value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getSeccional()  )   ? farmacia.getSeccional().getId_seccional()      : ""  %>"	type="text"  />	
<input  type="hidden" id="<portlet:namespace />codigoColegio"   name="<portlet:namespace />codigoColegio" value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getColegio().getCodigo())   ? farmacia.getColegio().getCodigo()    : ""  %>" />
<input  type="hidden" id="<portlet:namespace />nombreColegio_sel"	name="<portlet:namespace />nombreColegio_sel" size="8"  value="<%=Validator.isNotNull(farmacia) && Validator.isNotNull(farmacia.getColegio()  )   ? farmacia.getColegio().getDescripcion()      : ""  %>"	type="text"  />
<input  type="hidden" id="cuitvalidoprestador" name="cuitvalidoprestador" value="" />

</form>

<script type="text/javascript">

jQuery("#<portlet:namespace />cuit_entidad").blur(function(){ validaCuitFarmacia();  });


var popupMD;

jQuery('#<portlet:namespace />buscando').hide();
jQuery("#<portlet:namespace />printButton").hide();
jQuery("#<portlet:namespace />periodoDia").hide();
jQuery('#<portlet:namespace />diveditFarmacia').hide();
// *******************************************************************************************************************************
// *******************************************************************************************************************************
function <portlet:namespace />saveFarmacia() {		
	var cuit  = jQuery("#<portlet:namespace />cuit_entidad").val();
	var sucursal = jQuery("#<portlet:namespace />sucursal_entidad").val();	
	var idSeccional= jQuery("#<portlet:namespace />id_seccional").val();
	var nombreSeccional= jQuery("#<portlet:namespace />seccional").val();
	//var namecole  = jQuery("#<portlet:namespace />detalleColegio").val();	
		
	jQuery("#<portlet:namespace />cuit").val(cuit);		
	jQuery("#<portlet:namespace />sucursal").val(sucursal);
	jQuery("#<portlet:namespace />id_seccional_sel").val(idSeccional);
	jQuery("#<portlet:namespace />nombre_seccional_sel").val(nombreSeccional);
	//jQuery("#<portlet:namespace />nombreColegio_sel").val(namecole);	 	
	if ( validaDatos())  {
		var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
		document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';
		
		<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_farmacia_entry" /></portlet:actionURL>';
		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=true";
		 --%>
		
		var xportletUrl = '/farmaciaospim/editar_borrar_farmacia_entry';
		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+					
		'<liferay-portlet:param name="esDatosTab" value="true"/>'+							
		'</liferay-portlet:actionURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__accionEnCurso", accionEnCurso);
  	
  	    
		document.<portlet:namespace />sitmedica_fm.method = 'post';		
		submitForm(document.<portlet:namespace />sitmedica_fm, url);
	}	
}	

function <portlet:namespace />editaFarmacia() {
	<% if (esEdicion){	%>
	var cuit  = jQuery("#<portlet:namespace />cuit_entidad").val();
	//var farmacia = jQuery("#<portlet:namespace />entidad").val();
	var sucursal = jQuery("#<portlet:namespace />sucursal_entidad").val();	
	var idSeccional= jQuery("#<portlet:namespace />id_seccional").val();
	var nombreSeccional= jQuery("#<portlet:namespace />seccional").val();
	//var namecole  = jQuery("#<portlet:namespace />detalleColegio").val();	
	//jQuery("#<portlet:namespace />farmacia").val(farmacia);	
	jQuery("#<portlet:namespace />cuit").val(cuit);		
	jQuery("#<portlet:namespace />sucursal").val(sucursal);
	jQuery("#<portlet:namespace />id_seccional_sel").val(idSeccional);
	jQuery("#<portlet:namespace />nombre_seccional_sel").val(nombreSeccional);
	//jQuery("#<portlet:namespace />nombreColegio_sel").val(namecole);	
     if (validaDatos())	{
			var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';
			
			var provinciaId = jQuery("#<portlet:namespace/>provincia").val();
			var localidadId = jQuery("#<portlet:namespace/>localidad").val();
			
			<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_farmacia_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + "&esDatosTab=false"+"&id_registro_farmacia=<%=farmacia.getId_farmacia()  %>";
			 --%>
			var xportletUrl = '/farmaciaospim/editar_borrar_farmacia_entry';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+					
			'<liferay-portlet:param name="esDatosTab" value="true"/>'+							
			'<liferay-portlet:param name="id_registro_farmacia" value="__id_registro_farmacia"/>'+
			'<liferay-portlet:param name="id_provincia" value="__id_provincia"/>'+
			'<liferay-portlet:param name="id_localidad" value="__id_localidad"/>'+
			
			'</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__accionEnCurso", accionEnCurso);
	  	    url = url.replace("__id_registro_farmacia", <%=farmacia.getId_farmacia()  %>);
	  	    url = url.replace("__id_provincia", provinciaId);
	  	    url = url.replace("__id_localidad", localidadId);
	  	  
	  	    
			document.<portlet:namespace />sitmedica_fm.method = 'post';			
			submitForm(document.<portlet:namespace />sitmedica_fm, url);			
			
   	 }				
     <%}%>
}
<% if (esEdicion){	%>
    divsBuscaEmpresaAfip();
<%}%>
<% if (inHabilitar){	%>
    divsBuscaEmpresaAfip();
	deshabilitarControles();
<%}%>


<%if (farmacia != null && farmacia.getColegio().getCodigo()!=null ) {%>
  <portlet:namespace />buscarColegioDetalleCodigo(); // carga los datos del colegio  
<%}%>

<%--
<%if (farmacia != null && farmacia.getSeccional() !=null && farmacia.getSeccional().getDescripcion()!=null ) {%> 
 jQuery("#<portlet:namespace />id_seccional").val(<%= farmacia.getSeccional().getIdSeccional() %>);
 jQuery("#<portlet:namespace />seccional").val('<%= farmacia.getSeccional().getDescripcion() %>'); 
<%}%>
--%>

function divsBuscaEmpresaAfip()
{
	jQuery('#<portlet:namespace />diveditFarmacia').show();
	jQuery('#<portlet:namespace />saveFarmaciaDiv').hide();
	<portlet:namespace />buscarEntidad();
}
function deshabilitarControles()
{
	document.getElementById("<portlet:namespace />seccional").disabled = true;
	document.getElementById("<portlet:namespace />id_seccional").disabled = true;
	document.getElementById("<portlet:namespace />cuit_entidad").disabled = true;
	document.getElementById("<portlet:namespace />entidad").disabled = true;
	document.getElementById("<portlet:namespace />sucursal_entidad").disabled = true;
}
function validaDatos(){		
	var resp;
	resp=jQuery('#cuitvalidoprestador').val();
	
	if (resp=="1" ){
		alert('El Cuit corresponde a otra Farmacia ya cargada.');
		return false;
	}
	if (resp=="2" ){
		alert('El Cuit no Existe en la Informacion de AFIP.');
		return false;
	}
	{/*
	if (jQuery("#<portlet:namespace />codigo").val() == '') {
		alert ("Falta ingresar el codigo.");		
		jQuery("#<portlet:namespace />codigo").focus();
		return false;
	}*/}
	
	if (jQuery("#<portlet:namespace />codigofarmacia").val() == '') {
		alert ("Falta el codigo de farmacia.");
		jQuery("#<portlet:namespace />codigofarmacia").focus();
		return false;
	}	
	if (jQuery("#<portlet:namespace />sucursal_entidad").val() == '') {
		alert ("Falta ingresar la sucursal de la entidad.");
		jQuery("#<portlet:namespace />sucursal_entidad").focus();
		return false;
	}
	if (jQuery("#<portlet:namespace />cuit_entidad").val() == '') {
		alert ("Falta ingresar el cuit de la entidad.");
		jQuery("#<portlet:namespace />cuit_entidad").focus();
		return false;
	}	
	if (jQuery("#<portlet:namespace />farmacia").val() == '') {
		alert ("Falta ingresar el nombre de la Farmacia.");
		jQuery("#<portlet:namespace />entidad").focus();
		return false;
	}	
	valProvincia  =jQuery("#<portlet:namespace/>provincia").val();
	if (valProvincia <1) {
		alert ("Falta ingresar la provincia de la Farmacia.");
		jQuery("#<portlet:namespace/>provincia").focus();
		return false;
	}
	valLocalidad =jQuery("#<portlet:namespace/>localidad").val();
	if (valLocalidad <1) {
		alert ("Falta ingresar la localidad de la Farmacia.");
		jQuery("#<portlet:namespace/>localidad").focus();
		return false;
	}
	
	
	return true;
}


function seleccionaCamposColegio(codigo,descripcion ){
	jQuery('#<portlet:namespace />detalleColegio').val(descripcion);
	jQuery('#<portlet:namespace />codigoColegio').val(codigo); // campo hidden que setea el dato de la base 
}	

function limpiaCamposBusquedaColegio(){
	jQuery('#<portlet:namespace />codigoColegio').val("");
}

function <portlet:namespace />agregarNuevoColegio (){   		   
		   popupPAT = Liferay.Popup({title:"Nuevo Colegio Farmacia",modal:true,width:350});		   
		   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/nuevo_colegio_farmacia';
		   jQuery(popupPAT).load(url);
}

function <portlet:namespace />cerrarColegioNuevo(id,des){
			if(popupPAT){		
			Liferay.Popup.close(popupPAT);
		}	
		if(id>0){
		   jQuery('#<portlet:namespace />codigoColegio').val(id);
		   jQuery('#<portlet:namespace />detalleColegio').val(des);
	   }
} 

function <portlet:namespace />cerrarSecc(){	
		<portlet:namespace />cerrarDivSecc();
		if(popup){		
			Liferay.Popup.close(popup);
		}
}
	 
function <portlet:namespace />cerrarDivSecc(){
		jQuery("#divSeccional").hide("slow");		
}
	
function validaCuitFarmacia () {
		   var cuit  = jQuery("#<portlet:namespace />cuit_entidad").val();
	      /*  var params="";	       
	       params += "&nroCuitFarmacia="+cuit; */	       
	       jQuery('#cuitvalidoprestador').val(2);
	       <%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmaciaospim/validar_cuit_farmacia';
	  	   url = url + params;
	  	    --%>
	  	   var xportletUrl = '/farmaciaospim/validar_cuit_farmacia';
			
		   var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="nroCuitFarmacia" value="__nroCuitFarmacia"/>'+					
			'</liferay-portlet:renderURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__nroCuitFarmacia", cuit);
	  	    
	  	   
	  	   jQuery.ajax({   
	  	   url: url,
	  		success: function(data) {
				var obj = jQuery.parseJSON(data);
	  			var respuesta = obj.nroCuitExisteEnAfip;
	  			var idFarmacia =obj.idFarmacia;
	  			var idFarmaciaActual;
	  			if (respuesta=="false"){ //  encontro el cuit
	  				jQuery('#cuitvalidoprestador').val(2);
	  			    return true;
	  			}
	  			if (respuesta=="true"){ //  encontro el cuit
	  					idFarmaciaActual= jQuery("#<portlet:namespace />id_registro_farmacia").val();		  			
			  			if (idFarmaciaActual!=idFarmacia &&  idFarmacia !=0) { // es  cuit de otra  farmacia			
			  				jQuery('#cuitvalidoprestador').val(1);
			  			}else{				
			  				jQuery('#cuitvalidoprestador').val(0);
			  			}	
	  			}else{ // no lo encontro
	  				jQuery('#cuitvalidoprestador').val(2);
	  			}	  		
	  			}
					});
     return true  ;    
}

function filtrarCodPostal() {
	var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_codpostal&idLocalidad='+idLocalidad;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />cod_postal").length = 0;						
			var obj = jQuery.parseJSON(data);						
			jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
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


	  	  	
</script>