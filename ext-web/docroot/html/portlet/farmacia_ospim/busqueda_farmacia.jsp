<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	response.setHeader("Cache-Control", "no-store"); 
	response.setHeader("Pragma", "no-cache"); 
	response.setDateHeader("Expires", 0); 
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_FARMACIA_OSPIM  );
	PortletURL portletURL = renderResponse.createRenderURL();	
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/farmaciaospim/view");
	showABMButtons=true;

	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	String pathurl = request.getContextPath();
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmaciaospim";
	}
	
	
%>
<form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;">
	<liferay-portlet:renderURLParams varImpl="portletURL" />
 
 <fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="farmacia-ospim-buscador" />
		</legend>
		<table>		
		<tr><td colspan="12">&nbsp;</td></tr>	
		<tr>			 
			<td><label><liferay-ui:message key="Cuit" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />cuitFarmacia" name="<portlet:namespace />cuitFarmacia" size="35" maxlength="11" type="text" value="" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="descripcion" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />descripcionFarmacia" name="<portlet:namespace />descripcionFarmacia" size="35" maxlength="25" type="text" value="" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="codigo-farmacia-mandataria" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />codemandataria" name="<portlet:namespace />codemandataria" size="20" maxlength="16" type="text" value="" />
			</td>
			</tr>	
			<tr>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			</tr>
			<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
						
			<td colspan="1">
			<select id="<portlet:namespace/>farmaciaprovincia"
				name="<portlet:namespace/>farmaciaprovincia"	onchange="javascript:filtrarLocalidad();" style="width: 150px;">
				
				<option			value="0">Seleccione Provincia</option>
			        	<%	for (Provincia provincia : provincias) { %>
					<option
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
					
			</select></td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1">
			<div class="selector-localidad">
			   
			   <select id="<portlet:namespace/>farmacialocalidad"
				name="<portlet:namespace/>farmacialocalidad"  onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			  </select>	
			 </div>
			 </td>
			 
		</tr>	
		</table>
</fieldset>
		<table>
		<tr> 	<td colspan="12">&nbsp;</td> 	</tr>
		<tr>
			<td align="left" coslpan="1"><input id="<portlet:namespace />buscar"
				value="<liferay-ui:message key="buscar"/>"
				title="<liferay-ui:message key="buscar" />"
				type="button" /></td>
			<td colspan="12">&nbsp;&nbsp;&nbsp;</td>					
			<td coslpan="1">
			<c:if test="<%=showABMButtons%>">
			<input type="button" 
				value="<liferay-ui:message key="compose"/>"
				title="<liferay-ui:message key="nueva-farmacia-ospim" />"				
				onClick="<portlet:namespace />altaFarmaciaOspim();" />
			</c:if>	
				</td>		
			<td colspan="1" >
			</td>	
		</tr>
		<tr>
			<td colspan="12" align="center">
				&nbsp;
			</td>
		</tr>
	   </table>
	<fieldset class="block-labels">
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
		<div align="center"
			id="<portlet:namespace />busquedaFarmaciaOspim">
			</div>
	</fieldset>
</form>

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
	value="" />

<input  type="hidden" name="pagina" id="pagina" value="x" />
<script type="text/javascript">
	
    jQuery("#<portlet:namespace />fechaArchivoDia").hide();
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/farmaciaospim/buscar_farmacia_registros_sesion';
	jQuery('#<portlet:namespace />busquedaFarmaciaOspim').load(url); 
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaFarmaciaOpsim();});
	
	function <portlet:namespace />busquedaFarmaciaOpsim(nroPagina){
		jQuery('#<portlet:namespace />buscando').show();		
		jQuery("#pagina").val(pagina_sel);		
		var farmaciaCuit=jQuery('#<portlet:namespace />cuitFarmacia').val(); 
		var farmaciaDescripcion=jQuery('#<portlet:namespace />descripcionFarmacia').val();
		var farmaciaCodigoMandataria =jQuery('#<portlet:namespace />codemandataria').val();
		var farmaciaLocalidad =jQuery('#<portlet:namespace/>farmacialocalidad').val();
		var farmaciaProvincia =jQuery('#<portlet:namespace/>farmaciaprovincia').val();
		var pagina_sel=0;
		
		if (nroPagina!=0) {
			pagina_sel =jQuery("#<portlet:namespace/>pagina_sel").val();
		}
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/farmaciaospim/buscar_farmacia_ospim&farmaciaCuit='+farmaciaCuit+		
		'&farmaciaDescripcion='+farmaciaDescripcion+'&farmaciaLocalidad='+farmaciaLocalidad+'&farmaciaProvincia='+farmaciaProvincia+'&farmaciaCodigoMandataria='+farmaciaCodigoMandataria+'&pagina_sel='+pagina_sel;
        jQuery('#<portlet:namespace />busquedaFarmaciaOspim').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	function <portlet:namespace />altaFarmaciaOspim () {		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_farmacia_entry" /></portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>farmaciaprovincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_provincia_localidad&idProvincia='+idProvincia;		 
		jQuery("#<portlet:namespace/>farmacialocalidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				document.getElementById("<portlet:namespace/>farmacialocalidad").length = 0;
				jQuery("#<portlet:namespace/>farmacialocalidad").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
				jQuery('.selector-localidad select').html(data).fadeIn();

			}
		});
	}
	
	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmaciaospim/id_localidad_codpostal&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}
	
	
</script>