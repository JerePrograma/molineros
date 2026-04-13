<%@ include file="/html/portlet/empresas/init.jsp" %>
<%
List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
//EntidadPadronUnificado empresa=null;
Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);


String sufi="acta_";

%>
<div id="<portlet:namespace />ocultarCtasBcrias">
<fieldset class="block-labels">
<legend>
		<liferay-ui:message key="cuentas-bancarias" />
</legend>
		

<table class="lfr-table" width="100%">	
	<tr>
		<td>		
			<liferay-ui:message key='banco' />&nbsp;&nbsp;			
			<select  id="<portlet:namespace />id_banco_cta_bcria" name="<portlet:namespace />id_banco_cta_bcria" style="width:250px;">
						<% for (Banco b : bancos) { %>
						<option value="<%=b.getId_banco() %>"><%=b.getDescripcion_banco() %></option>
						<%} %>
			</select>
		</td>		
		<td>
			<liferay-ui:message key='sucursal' /> 
			<input type="text" size="5" value="" name="<portlet:namespace />id_sucursal_cta_bcria" id="<portlet:namespace />id_sucursal_cta_bcria" />
		</td>		
	<%if(portlet_name.equals("tesoreria")){%>
	</tr>
	<tr>
	<%}%>		
		<td>
			<liferay-ui:message key='cuenta-bancaria' />
			<input type="text" size="15" value="" name="<portlet:namespace />descripcion_cta_bcria" id="<portlet:namespace />descripcion_cta_bcria" />
		</td>
		
		<td>
			<liferay-ui:message key='cbu' />
			<input type="text" size="20" value="" name="<portlet:namespace />cbu_cta_bcria" id="<portlet:namespace />cbu_cta_bcria" />&nbsp;
			<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarCtaBcria();" />
		</td>		 
	<tr>
	
	<tr>
		<td colspan="4">
			<div align="center" id="<portlet:namespace />ctas_bcrias_result">
			<liferay-util:include page='/html/portlet/empresas/cuentas_bcrias_search_result.jsp'>
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion)%>" />
			</liferay-util:include>		
			</div>
		</td>
	</tr>
</table>
</fieldset>
</div>

<script type="text/javascript">
	<%if(portlet_name.equals("estudio_isidro")){%>	
	jQuery('#<portlet:namespace />ocultarCtasBcrias').css('display','none')		
	<%}%>
	function <portlet:namespace />showHideDivCtasBcrias(){		
		if (jQuery("#<portlet:namespace />ocultarCtasBcrias").css('display') === 'none') {
			jQuery('#<portlet:namespace />ocultarCtasBcrias').css('display','block')
			jQuery('#<portlet:namespace />arrow_cta_bcria').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />ocultarCtasBcrias').css('display','none')
			jQuery('#<portlet:namespace />arrow_cta_bcria').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	}
	function <portlet:namespace />agregarCtaBcria(){

			<%if(portlet_name.equals("empresas") || portlet_name.equals("estudio_isidro")){%>
				if (trim(document.getElementById("<portlet:namespace />cuit").value)==""){				
			<%}else{%>	
				if (trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)==""){
			<%}%>
			
				alert("Primero debe seleccionar una empresa");
				return false;
			}

			if(document.getElementById("<portlet:namespace />cbu_cta_bcria").value.trim().length>0 
					&& !validarCBU(document.getElementById("<portlet:namespace />cbu_cta_bcria").value, "<liferay-ui:message key='valida-cbu'/>")){
				jQuery('#<portlet:namespace />cbu_cta_bcria').focus();
				return false;
			}

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_cta_bcria_empresa';
				<%if(portlet_name.equals("empresas") || portlet_name.equals("estudio_isidro")){%>	
					url+='&cuit='+trim(document.getElementById("<portlet:namespace />cuit").value);					
				 	url+='&sucursal='+trim(document.getElementById("<portlet:namespace />sucursal").value);				 	
				<%}else{%>
					url+='&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value);				
				 	url+='&sucursal='+trim(document.getElementById("<portlet:namespace />sucursal_entidad<%=sufi%>").value);
			 	<%}%>
				url+='&id_banco_cta_bcria='+document.getElementById("<portlet:namespace />id_banco_cta_bcria").value;				
				url+='&id_sucursal_cta_bcria='+document.getElementById("<portlet:namespace />id_sucursal_cta_bcria").value;				
				url+='&descripcion_cta_bcria='+encodeURI(document.getElementById("<portlet:namespace />descripcion_cta_bcria").value);				
				url+='&cbu_cta_bcria='+document.getElementById("<portlet:namespace />cbu_cta_bcria").value;
				url+='&accion=ADD';
				
			jQuery('#<portlet:namespace />ctas_bcrias_result').load(url, function() {});
			<%if(portlet_name.equals("estudio_isidro")){%>	
			jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
			<%}%>	
	}

	function borraCuenta(cuit, sucursal, id_cta, id_banco, cta_bcria, sucu_bcria, cbu_cta_bcria){		
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_cta_bcria_empresa';
			url+='&accion=DELETE';
			url+='&cuit='+cuit;
		 	url+='&sucursal='+sucursal;
		 	url+='&id_cta_bcria='+id_cta;
			url+='&id_banco_cta_bcria='+id_banco;			
			url+='&descripcion_cta_bcria='+cta_bcria;
			url+='&id_sucursal_cta_bcria='+sucu_bcria;				
			url+='&cbu_cta_bcria='+cbu_cta_bcria;
			jQuery('#<portlet:namespace />ctas_bcrias_result').load(url, function() {});
			<%if(portlet_name.equals("estudio_isidro")){%>	
			jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
			<%}%>	
		}	
	}
	
	function editaCuenta(cuit, sucursal, id_cta, id_banco, cta_bcria, sucu_bcria, cbu_cta_bcria){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_cta_bcria_empresa';
		url+='&accion=DELETE';
		url+='&cuit='+cuit;
	 	url+='&sucursal='+sucursal;
	 	url+='&id_cta_bcria='+id_cta;
		url+='&id_banco_cta_bcria='+id_banco;			
		url+='&descripcion_cta_bcria='+cta_bcria;
		url+='&id_sucursal_cta_bcria='+sucu_bcria;				
		url+='&cbu_cta_bcria='+cbu_cta_bcria;
		jQuery('#<portlet:namespace />ctas_bcrias_result').load(url, function() {	
			jQuery("#<portlet:namespace />id_banco_cta_bcria").val(id_banco);				
			jQuery("#<portlet:namespace />id_sucursal_cta_bcria").val(sucu_bcria);				
			jQuery("#<portlet:namespace />descripcion_cta_bcria").val(cta_bcria);				
			jQuery("#<portlet:namespace />cbu_cta_bcria").val(cbu_cta_bcria);
		});
	}

	jQuery('#<portlet:namespace />buscandoActas').hide();
</script>