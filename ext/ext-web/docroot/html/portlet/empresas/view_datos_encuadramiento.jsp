<%@ include file="/html/portlet/empresas/init.jsp"%>
<%
Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);

List<RamoEmpresa> ramos = (ArrayList<RamoEmpresa>) portletSession
.getAttribute(WebKeysEmpresas.RAMOS_EMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

//boolean esEdicion = true;

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
boolean esEdicion = true;
if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
}


//String idOp=(String)renderRequest.getAttribute("idOp");
String prefijo="empre_";
String vista = "H";
String tamanio="40";
%>
<div id="<portlet:namespace/>ocultarDatosGral">
<fieldset class="block-labels">
<legend>
<liferay-ui:message	key="datos-encuadramiento" /> 
</legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">	
	<tr>			
		<td><label><liferay-ui:message key="seccional" />:</label></td>
		<td>
				<liferay-util:include
					page="/html/portlet/empresas/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%= empresa!=null ? String.valueOf(empresa.getId_seccional()) : new String() %>" />
					<liferay-util:param name="seccional" value="" />
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					<liferay-util:param name="prefijo" value="<%=prefijo%>"/>
				</liferay-util:include>
		</td>		
		<td><label><liferay-ui:message key="ramo" />:</label></td>
		<%-- <td><select  name="<portlet:namespace/>ramo" id="<portlet:namespace/>ramo" 
				<% if (!esEdicion) { %> disabled="disabled" <%} %> >
			<option value=""></option>
			<% for (RamoEmpresa ramo: ramos) { %>
			<option
				<%= empresa != null && empresa.getRamoEmpresa()!= null && empresa.getRamoEmpresa().equals(ramo) ? "selected" : ""  %>
				value="<%= ramo.getId_ramo_empresa() %>"><%=ramo.getDescripcion()%></option>
			<% } %>
		</select></td> --%>
		<td><input type="hidden" name="<portlet:namespace/>ramo" id="<portlet:namespace/>ramo" 
			value="<%=empresa!=null&&empresa.getRamoEmpresa()!=null?empresa.getRamoEmpresa().getId_ramo_empresa():0 %>" > 
			<%
				int posRamo = ramos.indexOf(empresa!=null?empresa.getRamoEmpresa():"");
			    String ramoDesc = "";
				try{
					ramoDesc = ramos.get(posRamo).getDescripcion();
				}catch(Exception e){
					
				}
			%> <%=ramoDesc%></td>		
		<td><%=empresa != null && empresa.getRamoEmpresa()!= null && (empresa.getRamoEmpresa().getId_ramo_empresa()>0 &&
								  empresa.getRamoEmpresa().getId_ramo_empresa()!=99 ||
								  empresa.getRamoEmpresa().getId_ramo_empresa()!=90)?"ENCUADRADA":"SIN ENCUADRAR"%>
		</td>
		<td>&nbsp;</td>
		<td><label></label><liferay-ui:message key="F.U.M."/></label></td>
		<td><%=empresa!=null?empresa.getModi_fechaAsString():""%> &nbsp; por &nbsp;<%=empresa!=null?empresa.getModi_usr():""%></td>		
	</tr>
	<tr>
		<td colspan="8">
				<liferay-util:include
					page="/html/portlet/empresas/busqueda_actividad.jsp">
					<liferay-util:param name="cod_actividad"
						value="<%= empresa!=null &&  null!=empresa.getActividadPrincipal() && empresa.getActividadPrincipal().getCodigo()!=0 ? String.valueOf(empresa.getActividadPrincipal().getCodigo()) : new String() %>" />
					<liferay-util:param name="cod_actividad_sec"
						value="<%= empresa!=null &&  null!=empresa.getActividadSecundaria() && empresa.getActividadSecundaria().getCodigo()!=0 ? String.valueOf(empresa.getActividadSecundaria().getCodigo()) : new String() %>" />	
					<liferay-util:param name="actividad" value="" />
					<liferay-util:param name="actividad_sec" value="" />
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					<liferay-util:param name="prefijo" value="<%=prefijo%>"/>
					<liferay-util:param name="vista" value="<%=vista%>"/>
					<liferay-util:param name="tamanioDescrip" value="<%=tamanio%>"/>
				</liferay-util:include>
		</td>
	</tr>

<!-- <div>&nbsp;</div>	 -->
<%if(portlet_name.equals("empresas")||portlet_name.equals("liquidaciones")){ %>
		<tr>
			<td><label><liferay-ui:message key="destino-correspondencia" />:</label></td>
			<td>
				<textarea rows="2" cols="40" id="<portlet:namespace />destino" name="<portlet:namespace />destino"><%=empresa != null && null!= empresa.getDestinoCorrespondencia() ? empresa.getDestinoCorrespondencia() : ""%></textarea> 
			</td>
			<td><label><liferay-ui:message key="CBU" />:</label></td>
			<td><input id="<portlet:namespace />cbu"
				name="<portlet:namespace />cbu" size="22" maxlength="22"
				type="text"
				value="<%= empresa != null && null!= empresa.getCBU() ? empresa.getCBU() : "" %>"/></td>
			<td><label><liferay-ui:message key="cheque-a-nombre" />:</label></td>
			<td><input id="<portlet:namespace />cheque"
				name="<portlet:namespace />cheque" size="30" type="text"
				value="<%= empresa != null && null!= empresa.getPortaCheque() ? empresa.getPortaCheque() : "" %>"/>
			</td>			
		</tr>	
		<tr>
			<td >&nbsp;</td>
		</tr>
<%}%>
<%-- <div align="center" id="<portlet:namespace />ocultar_obs"> --%>
	<!-- <fieldset class="block-labels"> -->
		<!-- <table align="center" width="100%"> -->
			<tr> <!-- align="center" -->
				<td align="left" colspan="7" valign="top">
					<span style="vertical-align: top;"><liferay-ui:message key="observaciones" />:</span>		
					<textarea rows="1" cols="50"
					id="<portlet:namespace />observaciones"
					name="<portlet:namespace />observaciones" <% if (!esEdicion) { %>
					<%="readonly='readonly'" %> <%}%>><%= empresa != null && empresa.getObservaciones() != null? empresa.getObservaciones() : "" %></textarea>
				</td>
			</tr>
		<!-- </table> -->
<!-- 	</fieldset>	 -->	
<!-- </div> -->
</table>
</fieldset>
</div>
<script type="text/javascript">	
<%if(portlet_name.equals("estudio_isidro")){%>	
jQuery('#<portlet:namespace />ocultarDatosGral').css('display','none')		
<%}%>
function <portlet:namespace />showHideDivDatosGral(){		
	if (jQuery("#<portlet:namespace />ocultarDatosGral").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarDatosGral').css('display','block');
		jQuery('#<portlet:namespace />ocultarDatosDomiyContac').css('display','block');
		jQuery('#<portlet:namespace />arrow_datos_gral').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarDatosGral').css('display','none');
		jQuery('#<portlet:namespace />ocultarDatosDomiyContac').css('display','none');
		jQuery('#<portlet:namespace />arrow_datos_gral').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		 <portlet:namespace />showHideDivDomicilios();
		 <portlet:namespace />showHideDivContactos();
		 <portlet:namespace />showHideDivContactosPers();
	}
}
/*Ojo que esta en la jsp view_datos_domi_y_contactos*/
function <portlet:namespace />showHideDivDomicilios(){		
	if (jQuery("#<portlet:namespace />ocultarDomicilios").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarDomicilios').css('display','block')
		jQuery('#<portlet:namespace />arrow_domicilios').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarDomicilios').css('display','none')
		jQuery('#<portlet:namespace />arrow_domicilios').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}
/*Ojo que esta en la jsp view_datos_domi_y_contactos*/
function <portlet:namespace />showHideDivContactos(){		
	if (jQuery("#<portlet:namespace />ocultarContactos").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarContactos').css('display','block')
		jQuery('#<portlet:namespace />arrow_contactos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarContactos').css('display','none')
		jQuery('#<portlet:namespace />arrow_contactos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}

function <portlet:namespace />showHideDivContactosPers(){		
	if (jQuery("#<portlet:namespace />ocultarContactosPersonalizados").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarContactosPersonalizados').css('display','block')
		jQuery('#<portlet:namespace />arrow_contactos_pers').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarContactosPersonalizados').css('display','none')
		jQuery('#<portlet:namespace />arrow_contactos_pers').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}

</script>

