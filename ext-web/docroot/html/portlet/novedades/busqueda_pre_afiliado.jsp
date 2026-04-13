<%@ include file="/html/portlet/novedades/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="com.liferay.portal.model.Organization" %>
<%@ page import="com.liferay.portal.service.OrganizationLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %>

<portlet:defineObjects/>

<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_PRE_CARGA_AFI) ||
 				PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
		
 		session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);

 		BusquedaPreAfiliadosFiltro filtro = (BusquedaPreAfiliadosFiltro) request.getSession().getAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS);

 		List<Organization> organizaciones = null;
 		
 		if(seccionalFijada != 0){
 			organizaciones = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());
 			/* organizaciones.add(organizaciones.get(0)); */
 		}else{
 			organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
 		}
 		
 		Calendar fechaDesde = Calendar.getInstance(); 		
 		Calendar fechaHasta = Calendar.getInstance();
		Integer estadoSelec = 0;
		Integer codigo = null;
		String cuil_titular = "";
		String inte = "";
		
 		if(filtro != null){
 			fechaDesde.setTime(filtro.getFechaDesde());
 			fechaHasta.setTime(filtro.getFechaHasta());
 			estadoSelec=filtro.getEstado();
 			codigo=filtro.getId();
 			cuil_titular = filtro.getCuilTitular();
 			inte = String.valueOf(filtro.getInte());
 		}else{
 			fechaDesde.setTime(new Date());
 			fechaHasta.setTime(new Date());
 		}
 			
 		
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-novedades" />
				<% if(seccionalFijada!=0){%>
					en Seccional <%=seccionalString%>
				<%}%>				
				</legend>
				<table class="lfr-table" > <!-- style="border-collapse: separate; border-spacing: 5px;" --> 
					<tr>
						<td><label><liferay-ui:message key="tipo-origen" />:</label></td>
						<td colspan="2">
							<select name="<portlet:namespace/>tipo_origen_empresa"
								id="<portlet:namespace/>tipo_origen_empresa">
								<% if(organizaciones.size() > 1 ){ %>
									<option value=""></option>		
								<%} %>		
								<%
									for (Organization org : organizaciones) {
										 if(!String.valueOf(org.getOrganizationId()).trim().contains("11337") &&
											!String.valueOf(org.getOrganizationId()).equals("98458")  &&
											!String.valueOf(org.getOrganizationId()).equals("109125") &&
											!String.valueOf(org.getOrganizationId()).equals("119803")){ 
											
								%>
									<option value="<%= org.getOrganizationId() %>"><%=org.getName() %></option>
 								<%
										}
									}
								%>
<!-- 							excluimos a
								11337;"Ospim"
								98458;"Uoma"
								109125;"Estudio"
								119803;"Tittarelli" -->
							</select>
						</td>
						<td><label><liferay-ui:message key="codigo" />:</label></td>
						<td><input id="<portlet:namespace />id" name="<portlet:namespace />id" 
							size="3" maxlength="6" type="text" value="<%=codigo!=null?codigo:"" %>"  onkeydown="allowOnlyDigits(event);" /></td>
						<!-- <td colspan="6">&nbsp;</td> -->		
						<td><label><liferay-ui:message key="estado" />:</label></td>
						<td colspan="1"><select name="<portlet:namespace/>estado"
							id="<portlet:namespace/>estado">
							<option value="0" <%if(estadoSelec == 0){ %> selected="selected" <%} %>>Todos</option>
							<option value="1" <%if(estadoSelec == 1){ %> selected="selected" <%} %>>Sin Procesar</option>
							<option value="2" <%if(estadoSelec == 2){ %> selected="selected" <%} %>>Procesados</option>
						</select></td>
						<td colspan="5">&nbsp;</td>		
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td colspan="2"><liferay-ui:input-date dayParam="fechaDesdeDia"
							dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
							dayNullable="<%= true %>" monthParam="fechaDesdeMes"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 30 %>"
							yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 20 %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" /></td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td colspan="2"><liferay-ui:input-date dayParam="fechaHastaDia"
							dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
							dayNullable="<%= true %>" monthParam="fechaHastaMes"
							monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>" yearParam="fechaHastaAnio"
							yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 30 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 20 %>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="12">
						<fieldset class="block-labels"><legend><liferay-ui:message
							key="datos-afiliado" /></legend> <liferay-util:include
							page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
							<liferay-util:param value="<%=String.valueOf(true)%>"
								name="edit_mode" />
							<liferay-util:param value="<%=cuil_titular%>"
								name="cuil" />
							<liferay-util:param value="<%=inte%>"
								name="inte" />
											
						</liferay-util:include></fieldset>
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<%
					  String iconoNov = themeDisplay.getPathThemeImages()+"/common/guest_icon.png"; 
					  String iconoPadron = themeDisplay.getPathThemeImages()+"/common/user_icon.png"; 
					%>
					
					<tr>	
						<td colspan="8">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" 
							type="button" onClick="javascript: <portlet:namespace />buscarPreCargaAfiliados();"/>							
						    &nbsp;&nbsp;
							
							<input type="button" value="<liferay-ui:message key="alta-afiliado" />" onClick="<portlet:namespace />altaPreAfiliado();" />
							
							<input type="button" value="<liferay-ui:message key="modif-benef" />" onClick="<portlet:namespace />altaNovedadAfiliado();" />
						
						</td>
						<td colspan="4" align="left">
							<img src="<%=iconoNov %>" /> &nbsp;<liferay-ui:message key='pre-afi-novedad'/>
							&nbsp;&nbsp;
							<img src="<%=iconoPadron %>" /> &nbsp;<liferay-ui:message key='afi-padron'/>
						</td>
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaPreAfiliadoDiv">
			</div>
			<input type="hidden" value="" id="<portlet:namespace />cuil_titular_aux" />
		</fieldset> 
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	
	function <portlet:namespace />buscarPreCargaAfiliados(){
		
		var id=jQuery('#<portlet:namespace />id').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();		
		<% if(seccionalFijada==0){%>
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
			var seccional_nombre=jQuery('#<portlet:namespace />seccional').val();
		<%}else{%>
			var seccional="<%=seccionalFijada%>";		
			var seccional_nombre="";
		<%}%>
		var tipo_origen=jQuery('#<portlet:namespace />tipo_origen_empresa').val();
		var estado=jQuery('#<portlet:namespace/>estado').val();
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
		
	    var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
	    
		if(!<portlet:namespace />validarBusqueda(id,cuil,inte,seccional,tipo_origen,fechaDesdeFinal,fechaHastaFinal)){
			return false;
		}
		
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_pre_carga_afiliados'

		var busquedaPreAfi = { "estado": estado, "fechaDesdeFinal": fechaDesdeFinal, "fechaHastaFinal": fechaHastaFinal, "pagina" : offset_reg, 
				   /* "viene_de" : viene_de, */ "seccional" : seccional, "origen_empresa" : tipo_origen,
				   "id" : id, "cuil" : cuil, "inte" : inte};
		
        jQuery('#<portlet:namespace />busquedaPreAfiliadoDiv').load(url, busquedaPreAfi, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  } );
	}
	
	function <portlet:namespace />validarBusqueda(id,cuil,inte,seccional,empresa,f_desde,f_hasta){
		if(trim(id.length)==0 && trim(cuil.length)==0 && trim(inte.length)==0 && trim(f_desde.length)==0 
				&& trim(f_hasta.length)==0 && trim(seccional.length)==0 && trim(empresa.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}

	var popup;
	function <portlet:namespace />altaPreAfiliado(){
		popup= Liferay.Popup({title:"<liferay-ui:message key="alta-afiliado" />",modal:true,width:300});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/selecc_alta_pre_afiliado';   		       	
		jQuery(popup).load(url); 
	}

	function <portlet:namespace />tipoAlta(cuil_titu){
		
		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado" />
				  </portlet:renderURL>';

		url = url + '&cuil_titular='+cuil_titu+'&<%=Constants.CMD%>='+'<%=Constants.ADD%>';		  
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url); --%>
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado" />'+
		'<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
	    '</liferay-portlet:renderURL>';

	    url = url.replace("__cuil_titu",cuil_titu);
	
	    document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function <portlet:namespace />altaNovedadAfiliado(){
		var cuil_titular = jQuery('#<portlet:namespace />cuil').val();
		var inte = jQuery('#<portlet:namespace />inte').val();
		
		if (trim(cuil_titular).length == 0 || trim(inte).length == 0 ) {
			alert("<liferay-ui:message key='cuil-titular-obligatorio' />");
			jQuery('#<portlet:namespace />cuil_titular').focus();
			return false;
		}
		
		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
			<portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado" />
		  </portlet:renderURL>';

		url = url + '&cuil_titular='+cuil_titular+'&inte='+inte+'&pre_carga=true'+'&id_pre_afiliado=0'+"&<%=Constants.CMD%>=<%=Constants.EDIT%>";		  
 		--%>
 		

 		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
 		'<liferay-portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado" />'+
 		'<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
 		'<liferay-portlet:param name="integ" value="__inte"/>'+
 		'<liferay-portlet:param name="cmd" value="edit"/>'+
 		'<liferay-portlet:param name="pre_carga" value="true"/>'+
 		'<liferay-portlet:param name="id_pre_afiliado" value="0"/>'+
 	    '</liferay-portlet:renderURL>';

 	    url = url.replace("__cuil_titu",cuil_titu);
 	    url = url.replace("__inte",inte);

	    
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);

	}
	
</script>
