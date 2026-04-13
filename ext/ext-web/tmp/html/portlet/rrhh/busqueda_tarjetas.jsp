<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server


		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_CONSULTA_RRHH);
		PortletURL portletURL = renderResponse.createRenderURL();
		portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
		portletURL.setParameter("struts_action", "/rrhh/view");
		BusquedaTarjetasFiltro   filtroTarjeta   = (BusquedaTarjetasFiltro)request.getSession().getAttribute(WebKeysRrhh.FILTRO_BUSQUEDA_REGISTROS_TARJETAS);
%>		
		
		
<form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;"><liferay-portlet:renderURLParams
	varImpl="portletURL" /> 
 
		<fieldset class="block-labels">
				<legend>Información de tarjetas</legend>

<table >
	
	<tr>

	<td colspan="12">&nbsp;</td>
	
			<td> 			<label>Nro Tarjeta:&nbsp;&nbsp;</label> 			 </td>
			<td>			 
			 <input id="<portlet:namespace />nrocard" name="<portlet:namespace />nrocard" size="10" maxlength="8" type="text" 
			 value="<%= filtroTarjeta  != null && filtroTarjeta.getNroTarjeta()>0     ? String.valueOf(filtroTarjeta.getNroTarjeta())    : "" %>" 
			 onkeydown="allowOnlyDigits(event);" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td> 			<label>Legajo:&nbsp;&nbsp;</label> 			 </td>
			<td>			 
			 <input id="<portlet:namespace />legajopersona" name="<portlet:namespace />legajopersona" size="6" maxlength="6" type="text"			  
			  value="<%= filtroTarjeta  != null  &&  filtroTarjeta.getLegajo()>0  ? String.valueOf(filtroTarjeta.getLegajo())   : "" %>" 
			  onkeydown="allowOnlyDigits(event);" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>			 
			<td> 			<label>Apellido:&nbsp;&nbsp;</label> 			 </td>
			<td>			 
			 <input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="20" type="text" 
			 value="<%= filtroTarjeta  != null     ? filtroTarjeta.getApellido()   : "" %>" 		 />
			</td>
			<td colspan="12">&nbsp;</td>
			<td> 			<label>Nombre:&nbsp;&nbsp;</label> 			 </td>
			<td>			 
			 <input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="20" type="text" 
			 value="<%= filtroTarjeta  != null     ? filtroTarjeta.getNombre()    : "" %>" 		 />  
			</td>
			<td colspan="12">&nbsp;</td>
			<td> 			<label>Entidad:&nbsp;&nbsp;</label> 			 </td>
			<td>
			<select name="<portlet:namespace />entidad"
						id="<portlet:namespace />entidad" onchange="">						
						    <option value="TODAS">SELECCIONE</option>
							<%for(int i = 0; i < WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH.length; i++ ) {%>
							<option
								value="<%=WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH[i][0] %>"
								<%if (filtroTarjeta != null && filtroTarjeta.getEntidad() !=null && 
						        WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH[i][1].equals(filtroTarjeta.getEntidad())  ) { %>
								selected="selected" <%} %>
								>
								<%=WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH[i][1] %>
							</option>
							<% } %>
     	    </select>
			 </td>
			 <td colspan="12">&nbsp;</td>
			 <td> 			<label>Sector:&nbsp;&nbsp;</label> 			 </td>
			<td>
			<select name="<portlet:namespace />sector"
						id="<portlet:namespace />sector" onchange="">
						    <option value="TODOS">SELECCIONE</option>
							<%for(int i = 0; i < WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH.length; i++ ) {%>
							<option
								value="<%=WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH[i][1] %>"
								<%if (filtroTarjeta != null && filtroTarjeta.getSector() !=null && 
										WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH[i][1].equals(filtroTarjeta.getSector())  ) { %>
								selected="selected" <%} %>
								>
								<%=WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH[i][1] %>
							</option>
							<% } %>
     	    </select>
			 </td>
	</tr>
		<tr>	<td colspan="12">&nbsp;</td>	</tr>
		<tr>	<td colspan="12">&nbsp;</td>	</tr>
</table>

<table>
	<tr>
		<td colspan="1"  ><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />" type="button"
			/> </td>
		<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	
		<td colspan="1"  ><input id="<portlet:namespace />nuevo"
			value="<liferay-ui:message key="nueva-tarjeta"/>"
			title="<liferay-ui:message key="nueva-tarjeta" />" type="button"
			onClick="<portlet:namespace />altaTarjeta();"
			/> </td>
		
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
			
			
			<div align="center" id="<portlet:namespace />busquedaRegistrosTarjetas">
			</div>
			
			
		</fieldset>

<input type="hidden"   name="pagina" id="pagina" value="5"/>
			
<script type="text/javascript">

jQuery('#<portlet:namespace />buscando').hide();

jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaDatosTarjetas();});


function <portlet:namespace />busquedaDatosTarjetas(){
	var nrocard=jQuery('#<portlet:namespace />nrocard').val();
	var nombre=jQuery('#<portlet:namespace />nombre').val();
	var apellido=jQuery('#<portlet:namespace />apellido').val();
	var entidad=jQuery('#<portlet:namespace />entidad').val();
	var sector=jQuery('#<portlet:namespace />sector').val();
	var estadoselentidad   =document.getElementById("<portlet:namespace />entidad");
	var estadoselsector   =document.getElementById("<portlet:namespace />sector");
	var legajopersona=jQuery('#<portlet:namespace />legajopersona').val();
	
	if (estadoselentidad.selectedIndex==0){  
		entidad="";
	}
	if (estadoselsector.selectedIndex==0){  
		sector="";
	}
	
	jQuery('#<portlet:namespace />buscando').show();
	
	var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();    
	jQuery("#pagina").val(pagina_sel);
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/rrhh/buscar_tarjetas&nroCard='
	+nrocard+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+encodeURI(entidad)+'&sector='+sector+'&pagina_sel='+pagina_sel+'&legajopersona='+legajopersona;
    jQuery('#<portlet:namespace />busquedaRegistrosTarjetas').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );
}

function <portlet:namespace />altaTarjeta() {		
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>">
			<portlet:param name="cmd" value="add" />
		    <portlet:param name="struts_action" value="/rrhh/editar_borrar_tarjetas_entry" />
	</portlet:renderURL>';		
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

<%if (filtroTarjeta!=null) { %>
    <portlet:namespace />busquedaDatosTarjetas()
<%}%>

</script>