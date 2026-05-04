<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@ page import="ar.com.enterpriseadmin.search.UserSearch" %>
<%@ page import="ar.com.enterpriseadmin.search.UserSearchTerms" %>
<%@ page import="ar.com.enterpriseadmin.search.UserDisplayTerms" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(WindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	//verificar los calendars
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	
	
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	fechaInicio.add(Calendar.MONTH, -1);
	
	
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	
	List<String> cartillaTipos = TraeListasServiceUtil.getCartillaTipos();
	List<String> cartillaPlanes = TraeListasServiceUtil.getCartillaPlan();
	List<String> cartillaLocalidades = TraeListasServiceUtil.getCartillaLocalidad();
	List<String> cartillaProvincias = TraeListasServiceUtil.getCartillaProvincia();
	List<String> cartillaEspecialidades = TraeListasServiceUtil.getCartillaEspecialidad();
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="cartilla" /></legend>
		<table class="lfr-table">	
			<tr>
			
			   <tr>
			     <td><liferay-ui:message key="tipo" /></td>
			     <td>
		            <select name="<portlet:namespace />tipo_filtro"
			                   id="<portlet:namespace />tipo_filtro" onchange="" >
			            <option value="0">Seleccione Cartilla</option>
				        <%	for (String c : cartillaTipos) { %>
						   <option value="<%= c%>"><%=c%></option>
				        <%	} %>       
		            </select>
		         </td>
		         
		        <td><label><liferay-ui:message key="prestador"/>:</label></td>
				<td><input id="<portlet:namespace />prestador_filtro" name="<portlet:namespace />prestador_filtro" size="50" maxlength="50" type="text" value=''/></td>
				
				
				 <td><liferay-ui:message key="plan" /></td>
			     <td>
		            <select name="<portlet:namespace />plan_filtro"
			                   id="<portlet:namespace />plan_filtro" onchange="" >
			            <option value="0">Seleccione Plan</option>
				        <%	for (String c : cartillaPlanes) { %>
						   <option value="<%= c%>"><%=c%></option>
				        <%	} %>       
		            </select>
		          </td>
		        </tr>  
		        <tr><td>&nbsp;</td></tr>	
				<tr>
				<td><liferay-ui:message key="localidad" /></td>
			     <td>
		            <select name="<portlet:namespace />localidad_filtro"
			                   id="<portlet:namespace />localidad_filtro" onchange="" >
			            <option value="0">Seleccione Localidad</option>
				        <%	for (String c : cartillaLocalidades) { %>
						   <option value="<%= c%>"><%=c%></option>
				        <%	} %>       
		            </select>
		         </td>
				
				<td><liferay-ui:message key="provincia" /></td>
			     <td>
		            <select name="<portlet:namespace />provincia_filtro"
			                   id="<portlet:namespace />provincia_filtro" onchange="" >
			            <option value="0">Seleccione Provincia</option>
				        <%	for (String c : cartillaProvincias) { %>
						   <option value="<%= c%>"><%=c%></option>
				        <%	} %>       
		            </select>
		         </td>
				
		       </tr>  
			   <tr><td>&nbsp;</td></tr>	
			   <tr>
                 <td><liferay-ui:message key="especialidad" /></td>
			     <td>
		            <select name="<portlet:namespace />especialidad_filtro"
			                   id="<portlet:namespace />especialidad_filtro" onchange="" >
			            <option value="0">Seleccione Especialidad</option>
				        <%	for (String c : cartillaEspecialidades) { %>
						   <option value="<%= c%>"><%=c%></option>
				        <%	} %>       
		            </select>
		         </td>
		         <td><label>Trabaja en: </label></td>
				 <td><input id="<portlet:namespace />trabajaen_filtro" name="<portlet:namespace />trabajaen_filtro" size="50" maxlength="50" type="text" value=''/></td>
				 <td colspan="2">Incluye Bajas <input type="checkbox"  name="<portlet:namespace />bajas_filtro" 
							 id="<portlet:namespace />bajas_filtro"></td>
		       </tr>  
			 <tr><td>&nbsp;</td></tr>	
		</table>	 
		
		
		<table>
		         <tr align="left">
				    <td>&nbsp;</td>
				 </tr>
				 <tr align="left">
				    <td>&nbsp;</td>
					<td align="left" width="100%">						
						<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarCartilla();"
						type="button" />
						<input type="button" value="Limpiar" onClick="<portlet:namespace />initFields();" />&nbsp;
					</td>
				 </tr>
				 <tr>
				    <td colspan="8" align="center"> 
				      <label><font color="red">Se mostraron los primeros 5000 registros</font></label>
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
		<div id="<portlet:namespace />divCartilla">
			    <jsp:include page='/html/portlet/autorizaciones/cartilla_result.jsp' />
		</div>
		
	</fieldset>
	
</form>		

<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();	
	var popupMD;
	
	function <portlet:namespace />buscarCartilla(){
		var tipo=jQuery('#<portlet:namespace />tipo_filtro').val();
		var prestador=jQuery('#<portlet:namespace />prestador_filtro').val();
		var plan=jQuery('#<portlet:namespace />plan_filtro').val();
		var localidad=jQuery('#<portlet:namespace />localidad_filtro').val();
		var provincia=jQuery('#<portlet:namespace />provincia_filtro').val();
		var especialidad=jQuery('#<portlet:namespace />especialidad_filtro').val();
		var trabajaen=jQuery('#<portlet:namespace />trabajaen_filtro').val();
		var bajas=jQuery("#<portlet:namespace/>bajas_filtro").is(':checked');       
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaNom = {"tipo":tipo,"prestador":prestador,"plan":plan,
	 			"localidad":localidad,"provincia":provincia,"especialidad":especialidad,
	 			"trabajaen":trabajaen,"incluyebajas":bajas};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/buscarCartilla" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />divCartilla').load(url,busquedaNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
		});	
		
	}
	
	function <portlet:namespace />initFields(){	
		jQuery('#<portlet:namespace />tipo_filtro').val('0');
		jQuery('#<portlet:namespace />prestador_filtro').val('');
		jQuery('#<portlet:namespace />plan_filtro').val('0');
		jQuery('#<portlet:namespace />localidad_filtro').val('0');
		jQuery('#<portlet:namespace />provincia_filtro').val('0');
		jQuery('#<portlet:namespace />especialidad_filtro').val('0');
		jQuery('#<portlet:namespace />trabajaen_filtro').val('');
	}
	
</script>

