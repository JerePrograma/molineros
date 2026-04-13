<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
Proveedor proveedor = (Proveedor)portletSession.getAttribute(WebKeysUOMA.PROVEEDOR_EN_EDICION,PortletSession.APPLICATION_SCOPE);
List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
List<Regimen> regimenes = (ArrayList<Regimen>) portletSession
.getAttribute(WebKeysEmpresas.REGIMENES_RET_GANANCIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

/*
String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "uoma";
}
*/

String portlet_name=null;
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
String accion=ParamUtil.getString(request,"accion");
boolean esEdicion = true;
//String idPrv=(String)renderRequest.getAttribute("idPrv");
int idPrv=proveedor!=null && proveedor.getId()!= null ?(int)proveedor.getId():0;
String prefijo="empre_";
%>
<form action="" id="<portlet:namespace />fm" name="<portlet:namespace />fm">	
<c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<div id="<portlet:namespace />empresa_div" name="<portlet:namespace />empresa_div" >
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-empresa" /></legend>
<table class="lfr-table">
	<tr>		
		<td><b><label><liferay-ui:message key="cuit" />:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label></b></td>
		<td><input id="<portlet:namespace />cuit"
			name="<portlet:namespace />cuit" size="11" maxlength="11" type="text"
			value="<%= proveedor != null && proveedor.getCuit()!=null ? proveedor.getCuit() : "" %>"
			<% if (proveedor != null && proveedor.getCuit()!=null) { %> <%="readonly='readonly'" %> <%}%> 
			onblur="<portlet:namespace />validoCuit()"/></td>
		<td><b><label><liferay-ui:message key="sucursal" />:</label></b></td>
		<td><input id="<portlet:namespace />sucursal"
			name="<portlet:namespace />sucursal" size="3" maxlength="5"
			type="text"
			value="<%= proveedor != null && proveedor.getSucursal()!=null ? proveedor.getSucursal() : "000" %>"
			<% if (proveedor != null && proveedor.getSucursal()!=null) { %> <%="readonly='readonly'" %> <%}%> 
			/>
		</td>
			
		<td><b><label>&nbsp;<liferay-ui:message key="razon-social" />:</label></b></td>
		<td><input id="<portlet:namespace />desc"
			name="<portlet:namespace />desc" size="30" type="text"
			value="<%= proveedor != null  && proveedor.getRazon_soc()!=null? proveedor.getRazon_soc() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
		</td>
		<td><label id="<portlet:namespace />mensaje" style="color:blue"/></td>
	</tr>	
</table>

<div align="center" id="<portlet:namespace />verificando">
				<table style="align:center;">
					<tr>
						<td>Verificando CUIT</td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
</div>	


<div>&nbsp;</div>


<fieldset class="block-labels"><legend>Impuestos</legend>
 <table class="lfr-table">
    <tr>
    
      <td><label><liferay-ui:message key="posicion-iva" />:</label></td>
	   <td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>iva"
			id="<portlet:namespace/>iva" onchange="<portlet:namespace/>configurarCarga()" >
			<option value=""></option>
			
			<%for(int i = 0; i < WebKeysUOMA.CATEGORIAS_IVA.length; i++ ) {%>
						<option
							value="<%= WebKeysUOMA.CATEGORIAS_IVA[i][0] %>"
							<%if (proveedor != null && proveedor.getImpIva()!= null && 
					              ( WebKeysUOMA.CATEGORIAS_IVA[i][0]).equals(proveedor.getImpIva())) { %>
							selected="selected" <%} %>>
							<%=WebKeysUOMA.CATEGORIAS_IVA[i][1] %>
						</option>
			<% } %>
			</select>
	   </td> 
    
       <td><label hidden="hidden">Categoría Monotributo:</label></td>
	   <td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace />categoriamonotributo"
			id="<portlet:namespace />categoriamonotributo" hidden="hidden" >
			<option value=""></option>
			
			<%for(int i = 0; i < WebKeysUOMA.CATEGORIAS_MONOTRIBUTO.length; i++ ) {%>
						<option
							value="<%= WebKeysUOMA.CATEGORIAS_MONOTRIBUTO[i][0] %>"
							<%if (proveedor != null && proveedor.getMonotributo() != null && 
					              ( WebKeysUOMA.CATEGORIAS_MONOTRIBUTO[i][0]).equals(proveedor.getMonotributo())) { %>
							selected="selected" <%} %>>
							<%=WebKeysUOMA.CATEGORIAS_MONOTRIBUTO[i][1] %>
						</option>
			<% } %>
			</select>
	   </td> 
	   
    </tr>
 
    <tr><td>&nbsp; </td></tr>
  
    <tr>	
        <td>
	      <label>Es sujeto de Retención </label>
	    </td>
	    <td>
	        <input type="checkbox" id="<portlet:namespace />agenteRetencion" name="<portlet:namespace />agenteRetencion" <%=proveedor.isAgenteRetencion()  ?"checked=\"checked\"":"" %>
	            onchange="<portlet:namespace />habilitaRegimen()"/></td>    
        <td colspan="3"><label>Código Régimen Ganancias:</label></td>
		<td colspan="10">
		   <select  name="<portlet:namespace/>regimen" id="<portlet:namespace/>regimen" 
			<% if (!esEdicion) { %> disabled="disabled" <%} %> >
			<option value=""></option>
			<% for (Regimen reg: regimenes) { %>
			<option
				<%= proveedor != null && proveedor.getRegimen()!= null && proveedor.getRegimen().getCodigoRegimen().equals(reg.getCodigoRegimen()) ? "selected" : ""  %>
				value="<%= reg.getCodigoRegimen() %>"><%=reg.getRegimenDescripcion()%></option>
			<% } %>
		   </select>
		</td>	
		<td><b><label>% Exención:</label></b></td>
		<td><input id="<portlet:namespace />porc_retencion"
			name="<portlet:namespace />porc_retencion" size="30" type="text"
			value="<%= proveedor != null  && proveedor.getPorcentajeExencion() !=null? proveedor.getPorcentajeExencion() : "0" %>"
			readonly='readonly'" />
		</td>
	</tr>	
 </table>
</fieldset>



<fieldset class="block-labels"><legend>Actividades</legend>
<table class="lfr-table">	
	<tr>			
		<td colspan="2">
				<liferay-util:include
					page="/html/portlet/empresas/busqueda_actividad.jsp">
					<liferay-util:param name="cod_actividad"
						value="<%= proveedor!=null &&  null!=proveedor.getActividadPrincipal() && proveedor.getActividadPrincipal().getCodigo()!=0 ? String.valueOf(proveedor.getActividadPrincipal().getCodigo()) : new String() %>" />
					<liferay-util:param name="cod_actividad_sec"
						value="<%= proveedor!=null &&  null!=proveedor.getActividadSecundaria() && proveedor.getActividadSecundaria().getCodigo()!=0 ? String.valueOf(proveedor.getActividadSecundaria().getCodigo()) : new String() %>" />	
					<liferay-util:param name="actividad" value="" />
					<liferay-util:param name="actividad_sec" value="" />
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					<liferay-util:param name="prefijo" value="<%=prefijo%>"/>
				</liferay-util:include></td>
		</td>
	</tr>
</table>
</fieldset>

<fieldset class="block-labels"><legend>Formas de Pago</legend>
<table class="lfr-table">	
	<tr>	
	   <td>Tipo de Pago:</td>  		
       <td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>tipoPago"
			id="<portlet:namespace/>tipoPago">
			<option value=""></option>
			
			<%for(int i = 0; i < WebKeysUOMA.FORMAS_PAGO.length; i++ ) {%>
						<option
							value="<%= WebKeysUOMA.FORMAS_PAGO[i][0] %>"
							<%if (proveedor != null && proveedor.getFormaPago() != null && 
					              ( WebKeysUOMA.FORMAS_PAGO[i][0]).equals(proveedor.getFormaPago())) { %>
							selected="selected" <%} %>>
							<%=WebKeysUOMA.FORMAS_PAGO[i][1] %>
						</option>
			<% } %>
			</select>
	   </td> 
	   
	   <td>		
		 <liferay-ui:message key='banco' />&nbsp;&nbsp;			
		 <select  id="<portlet:namespace />id_banco_cta_bcria" name="<portlet:namespace />id_banco_cta_bcria" style="width:250px;">
		        <option value=""></option>
				<% for (Banco b : bancos) { %>
					<option value="<%=b.getId_banco() %>"
					
					<%if (proveedor != null && proveedor.getCuentaBcria() != null && 
							proveedor.getCuentaBcria().getBanco()!=null &&
					               b.getId_banco() == proveedor.getCuentaBcria().getBanco().getId_banco()) { %>
							selected="selected" <%} %>>
					
					<%=b.getDescripcion_banco() %></option>
				<%} %>
		</select>
		</td>		
		
		<td>
			<liferay-ui:message key='cuenta-bancaria' />
			<input type="text" size="15" value="<%= proveedor != null  && proveedor.getCuentaBcria()!=null && proveedor.getCuentaBcria().getDescripcion() != null
			? proveedor.getCuentaBcria().getDescripcion() : "" %>" name="<portlet:namespace />descripcion_cta_bcria" id="<portlet:namespace />descripcion_cta_bcria" />
		</td>
		
		<td>
			<liferay-ui:message key='cbu' />
			<input type="text" size="40" value="<%= proveedor != null  && proveedor.getCuentaBcria()!=null && proveedor.getCuentaBcria().getCBU() != null
			? proveedor.getCuentaBcria().getCBU() : "" %>" name="<portlet:namespace />cbu_cta_bcria" id="<portlet:namespace />cbu_cta_bcria" />&nbsp;
	    </td>		
	   
    </tr>
</table>
</fieldset>
<div>&nbsp;</div>
<fieldset class="block-labels">
      <legend>
         <liferay-ui:message key="domicilios" /> 
      </legend>

      <table class="lfr-table" style="border-collapse: separate; border-spacing: 1px;">
	     <tr>		
			<td>
				<input type="hidden" name="<portlet:namespace/>tipo_domicilio_empre" id="<portlet:namespace/>tipo_domicilio_empre" value="FISCAL" >
				<input type="hidden" name="<portlet:namespace/>id_domicilio" id="<portlet:namespace/>id_domicilio" 
				       value="<%=proveedor != null  && proveedor.getDomicilio() !=null && proveedor.getDomicilio().getId_domicilio() != 0
				         ? proveedor.getDomicilio().getId_domicilio():"" %>" >
			</td>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td>
				<select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>provincia" id="<portlet:namespace/>provincia" 
				onchange="javascript:filtrarLocalidad();" style="width: 100px">
				<option selected value="0">Seleccione una provincia</option>
				<% for (Provincia provincia : provincias) { %>
				   <option value="<%= provincia.getId() %>"
				   
				   <%if (proveedor != null && proveedor.getDomicilio() != null  
						   && proveedor.getDomicilio().getProvincia() != null
					          && provincia.getId() == proveedor.getDomicilio().getProvincia().getId()) { %>
							selected="selected" 
					<%} %>
				   
				   ><%=provincia.getDescripcion()%></option>
				<% } %>
				</select>
			</td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
            <td> 
            <div class="selector-localidad">
			    <select id="<portlet:namespace/>localidad"
				  name="<portlet:namespace/>localidad" <% if (!esEdicion) { %>
				  disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();"
				  style="width: 100px;" >
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option	value="<%= localidad.getId() %>"
					<%if (proveedor != null && proveedor.getDomicilio() != null 
					        &&  proveedor.getDomicilio().getLocalidad() != null 
					          && localidad.getId() == proveedor.getDomicilio().getLocalidad().getId() ) { %>
							selected="selected" <%} %>
					><%=localidad.getDescripcion()%></option>
					<%	}	%>
			    </select>
			 </div>			
			</td>
			
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td>
				<input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" type="text"			
				value="<%= proveedor != null  && proveedor.getDomicilio() !=null && proveedor.getDomicilio().getPostal_codi() != null
			         ? proveedor.getDomicilio().getPostal_codi() : "" %>"
				<% if (!esEdicion) { %> readonly="readonly" %> <%}%>
				/>
			</td>
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td>
			<input id="<portlet:namespace />calle" name="<portlet:namespace />calle"
	            type="text" value="<%=proveedor != null  && proveedor.getDomicilio() !=null && proveedor.getDomicilio().getCalle() != null
				         ? proveedor.getDomicilio().getCalle():"" %>"/>
			
			</td>
			<td><label><liferay-ui:message key="numero" />:</label></td>
			<td><input id="<portlet:namespace />numero"
				     name="<portlet:namespace />numero" size="5" maxlength="5" type="text"			
				onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);"
				value="<%= proveedor != null  && proveedor.getDomicilio() !=null && proveedor.getDomicilio().getNumero() != null
			         ? proveedor.getDomicilio().getNumero() : "" %>" /></td>
				
			<div id='divCodPostal' style="float: right;"></div>	
			<td><label><liferay-ui:message key="piso" />:</label></td>
			<td><input id="<portlet:namespace />piso"
				name="<portlet:namespace />piso" size="5" maxlength="5" type="text" 
				value="<%= proveedor != null  && proveedor.getDomicilio() !=null && proveedor.getDomicilio().getPiso() != null
			         ? proveedor.getDomicilio().getPiso() : "" %>"/></td>
	
			<td><label><liferay-ui:message key="departamento" />:</label></td>
			<td><input id="<portlet:namespace />departamento"
				name="<portlet:namespace />departamento" size="5" maxlength="5" type="text"
				value="<%= proveedor != null  && proveedor.getDomicilio() !=null && proveedor.getDomicilio().getDepto() != null
			         ? proveedor.getDomicilio().getDepto() : "" %>"
				/></td>	
					
	</tr>	
</table>
</fieldset>

<fieldset class="block-labels">
      <legend>Datos de Contacto</legend>
        <table class="lfr-table" style="border-collapse: separate; border-spacing: 1px;">
            <tr>
              <td>
                <label><liferay-ui:message key="email" />:</label>
              </td>
              <td>
                 <input id="<portlet:namespace />email_prv" 	name="<portlet:namespace />email_prv" size="100" type="text"
				value="<%= proveedor != null  && proveedor.getEmail() !=null && proveedor.getEmail().getContacto() != null
			         ? proveedor.getEmail().getContacto()  : "" %>"
				/>
              
              </td>
            </tr>
        </table>
</fieldset>

</fieldset>
<% if (esEdicion) { %>
<br />

<div align="center" id="<portlet:namespace />procesando_save_empresa">
	<table style="align:center;">
		<tr>
			<td><liferay-ui:message key="Grabando..."/></td>
			<td align="center">
				<img alt="<liferay-ui:message key='grabando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</td>
		</tr>
	</table>		
</div>	

<div id="<portlet:namespace />ocultarSaveEmpresa" align="center">
<%if ("E".equalsIgnoreCase(accion)) { %>
<input type="submit" value="<liferay-ui:message key="save" />"
		onClick="<portlet:namespace />saveProveedor();return false;"	 
	/>
<%}%>	
</div>	
	
<input type="hidden" value="" name="view" id="view" />
<input type="hidden" value="" name="flag" id="flag" />
<input type="hidden" name="idPrv" id="idPrv" value="<%=idPrv%>" />
<input type="hidden" name="<portlet:namespace/>id_cta_bcria" id="<portlet:namespace/>id_cta_bcria" value="<%=proveedor != null && proveedor.getCuentaBcria()!=null 
    && proveedor.getCuentaBcria().getId_cuenta_bcria()>0?
    		proveedor.getCuentaBcria().getId_cuenta_bcria():0 %>" >
<input type="hidden"
	value="<%= request.getAttribute("accionOriginal") != null && 
	     !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  
	    	 (idPrv==0? Constants.ADD : Constants.UPDATE)%>"
	name="accionOriginal" id="accionOriginal" />
<%} %>
			<div align="center" id="<portlet:namespace />saveEmpleadorDiv">						
			</div>
</div>	
</form>	
<script type="text/javascript">

jQuery('#<portlet:namespace />procesando_save_empresa').hide();

function <portlet:namespace />showHideDivSaveEmpresa(){		
		if (jQuery("#<portlet:namespace />ocultarSaveEmpresa").css('display') === 'none') {
			jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block')
		}else{
			jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','none')
		}
}

function <portlet:namespace />saveProveedor() {
		
		if (<portlet:namespace />validarCampos()) {	
			var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
			    params+="&accion=E";
//			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/proveedores_editar" /></portlet:renderURL>';
			var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/proveedores_editar';
			url = url + params;
		
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);		
		} 
}
	
function <portlet:namespace />validarCampos() {		
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		var cbu=jQuery('#<portlet:namespace />cbu').val();
		try {
			if (trim(jQuery("#<portlet:namespace />desc").val()).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />desc').focus();
				return false;
			}

			if (trim(cuit).length == 0){
				alert("<liferay-ui:message key='valida-cuit' />");
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
							
			
			if (!isInteger(jQuery('#<portlet:namespace />sucursal').val())){
				alert("<liferay-ui:message key='valida-sucu' />");
				jQuery('#<portlet:namespace />sucursal').focus();
				return false;
			}
			
			if (trim(calle).length == 0){
				alert("<liferay-ui:message key='valida-calle' />");
				jQuery('#<portlet:namespace />calle').focus();
				return false;
			}
			
		} catch (err) {
			return false;
		}
		return true;
}

function filtrarLocalidad() {		
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async: false,
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
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
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
	
	
function <portlet:namespace />validoCuit(){
		var accion = jQuery("#accionOriginal").val();
		var cuit = jQuery("#<portlet:namespace />cuit").val();
		var sucursal = jQuery("#<portlet:namespace />sucursal").val();
		if("add"==accion && cuit !=null){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/existe_cuit_empresa_proveedor'
				+'&cuit='+cuit 
				+'&sucursal='+sucursal;
			jQuery('#<portlet:namespace />verificando').show();
			jQuery.ajax({   
				url: url,
				async: false,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var existe=obj.existe;
					jQuery('#<portlet:namespace />verificando').hide();
					if(existe=="true"){
					   alert("El cuit ya existe en la Base de Datos");
					   //jQuery("#<portlet:namespace/>regimen").val(obj.regimenganancias);
					}else{
						
						jQuery("#<portlet:namespace />mensaje").html(obj.mensaje);
						jQuery("#<portlet:namespace />desc").val(obj.razonsocial);
						jQuery("#<portlet:namespace />cod_actividad<%=prefijo%>").val(obj.actividadprincipal);
						jQuery("#<portlet:namespace />actividad<%=prefijo%>").val(obj.actividadprincipaldescripcion);
						jQuery("#<portlet:namespace />cod_actividad_sec<%=prefijo%>").val(obj.actividadsecundaria);
						jQuery("#<portlet:namespace />actividad_sec<%=prefijo%>").val(obj.actividadsecundariadescripcion);
						jQuery("#<portlet:namespace />iva").val(obj.posicioniva.trim());
						jQuery("#<portlet:namespace />categoriamonotributo").val(obj.monotributo.trim());
						jQuery("#<portlet:namespace />cbu").val(obj.cbu);
						jQuery("#<portlet:namespace/>id_domicilio").val(obj.iddomicilio);
						jQuery("#<portlet:namespace />calle").val(obj.calle);
						jQuery("#<portlet:namespace />numero").val(obj.nro);
						
						jQuery("#<portlet:namespace />piso").val(obj.piso);
						jQuery("#<portlet:namespace />departamento").val(obj.departamento);
						jQuery("#<portlet:namespace />cod_postal").val(obj.codpostal);
						jQuery("#<portlet:namespace/>provincia").val(obj.provincia);
						jQuery("#<portlet:namespace/>localidad").val(obj.localidad);
						jQuery("#<portlet:namespace />email_prv").val(obj.email);
						
						jQuery("#<portlet:namespace />id_banco_cta_bcria").val(obj.idbanco);
						jQuery("#<portlet:namespace />descripcion_cta_bcria").val(obj.nroctabcria);
						jQuery("#<portlet:namespace/>id_cta_bcria").val(obj.idctabcria);
						
						jQuery("#<portlet:namespace/>regimen").val(obj.regimenganancias);
						
						<portlet:namespace />configurarCarga();
						
					}   
				}
			});	
		}
		
}
	
function <portlet:namespace />configurarCarga(){
	
	
	var cIva = jQuery("#<portlet:namespace />iva").val();

	if(cIva=="AC"){
		 jQuery('#<portlet:namespace />agenteRetencion').attr('disabled', false);
		 jQuery('#<portlet:namespace />agenteRetencion').attr('checked', true);
	}else{
		 jQuery('#<portlet:namespace />agenteRetencion').attr('checked', false);
		 jQuery('#<portlet:namespace />agenteRetencion').attr('disabled', true);
		 
	}
	
	<portlet:namespace />habilitaRegimen();


}	


function <portlet:namespace />habilitaRegimen(){
	
	var sujetoRetencion = document.getElementById("<portlet:namespace />agenteRetencion");
	if (sujetoRetencion.checked){
		jQuery('#<portlet:namespace />regimen').attr('disabled', false);
	} else{
		jQuery('#<portlet:namespace />regimen').val("");
		jQuery('#<portlet:namespace />regimen').attr('disabled', true);
	}
	
}	

	
jQuery('#<portlet:namespace />verificando').hide();

<%if(idPrv==0){%>
jQuery('#<portlet:namespace />regimen').val("");
jQuery('#<portlet:namespace />regimen').attr('disabled', true);
jQuery('#<portlet:namespace />agenteRetencion').attr('checked', false);
jQuery('#<portlet:namespace />agenteRetencion').attr('disabled', true);	
<%}%>

</script>