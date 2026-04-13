<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<% 

boolean showABMButtons =  PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);


Organismo organismo  = (Organismo)portletSession.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);

boolean esEdicion = false;

Calendar fechaInicio = CalendarFactoryUtil.getCalendar();

Calendar current = CalendarFactoryUtil.getCalendar();

String cmd=organismo!=null&&organismo.getId_organismo()!=0?"update":"";

%>
<form name="<portlet:namespace />org" id="<portlet:namespace />org">
<fieldset class="block-labels"><legend><liferay-ui:message key="datos-organismo" /></legend>

<table class="lfr-table" width="100%">	
	<tr>
		<td><label><liferay-ui:message key="nombre-organismo" />:</label></td>
		<td>
			<input id="<portlet:namespace />nombre_organismo" name="<portlet:namespace />nombre_organismo" size="50"  type="text" 
			value="<%=(null!=organismo&&organismo.getNombre()!=null)?organismo.getNombre():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
		</td>
		<td>
			<label><liferay-ui:message key="sigla" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />sigla" name="<portlet:namespace />sigla" size="50"  type="text" 
				value="<%=(null!=organismo&&organismo.getSigla()!=null)?organismo.getSigla():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
		</td>	
		<td><label><liferay-ui:message key="ambito" />:</label></td>
		<td>
			<select  name="<portlet:namespace/>ambito" id="<portlet:namespace/>ambito" <%if(!showABMButtons){%>disabled<%}%>>
				<%	for (String amb : WebKeysCGT.AMBITOS) {	%>
						<option value="<%= amb %>" <%= null!=organismo && organismo.getAmbito().equals(amb) ? "selected" : "" %> >
							<%=amb%>
						</option>
				<%	}	%>					
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>	
		<td><label><liferay-ui:message key="telefono" />:</label></td>
		<td><input id="<portlet:namespace />telefono" name="<portlet:namespace />telefono" size="15"  type="text" value="<%=(null!=organismo&&organismo.getTelefono()!=null)?organismo.getTelefono():""%>"" <%if(!showABMButtons){%>readonly<%}%>/></td>
		<td><label><liferay-ui:message key="web" />:</label></td>
		<td><input id="<portlet:namespace />web" name="<portlet:namespace />web" size="35"  type="text" value="<%=(null!=organismo&&organismo.getWeb()!=null)?organismo.getWeb():""%>" <%if(!showABMButtons){%>readonly<%}%>/></td>
		<td><label><liferay-ui:message key="orbita" />:</label></td>
		<td><input id="<portlet:namespace />orbita" name="<portlet:namespace />orbita" size="35"  type="text" value="<%=(null!=organismo&&organismo.getOrbita()!=null)?organismo.getOrbita():""%>" <%if(!showABMButtons){%>readonly<%}%>/></td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="email-short" />:</label></td>
		<td><input id="<portlet:namespace />email_organismo" name="<portlet:namespace />email_organismo" size="15"  type="text" value="<%=(null!=organismo&&organismo.getEmail()!=null)?organismo.getEmail():""%>"" <%if(!showABMButtons){%>readonly<%}%>/></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
			<td colspan="6" width="100%">				
				<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/domicilio_organismo_area.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>				
			</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5" align="center" valign="center">
			<textarea id="<portlet:namespace />observaciones" name="<portlet:namespace />observaciones" cols="160" rows="5" <%if(!showABMButtons){%>readonly<%}%>><%=(null!=organismo&&organismo.getObservaciones()!=null)?organismo.getObservaciones():""%></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
</table>
<div id="div_formas_de_pago"  >
<table class="lfr-table" width="100%">
		<tr>
			<td width="100%">
				<fieldset class="block-labels">
					<legend><liferay-ui:message	key="contacto" /></legend>
					<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/agregar_contacto.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(showABMButtons) %>"/>
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td width="100%">
				<fieldset class="block-labels">
					<legend><liferay-ui:message	key="lineas-trabajo" /></legend>
					<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/agregar_linea.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(showABMButtons) %>"/>
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td width="100%">
				<fieldset class="block-labels">
					<legend><liferay-ui:message	key="areas" /></legend>
					<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/agregar_area.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(showABMButtons) %>"/>
						<liferay-util:param name="id_organismo" value="<%=null!=organismo?String.valueOf(organismo.getId_organismo()):\"\"%>"/>
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td width="100%">
				<fieldset class="block-labels">
					<legend><liferay-ui:message	key="comments" /></legend>
					<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/agregar_comentario.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(showABMButtons) %>"/>
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
</table>
</div>
<%if(showABMButtons){%>
	<div align="center">
		<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveOrganismo();return false;"/>
	</div>
<%}%>
<input type="hidden" name="<portlet:namespace />cmd" id="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd!=null?cmd:""%>"/>
<input type="hidden" name="<portlet:namespace />id_organismo" id="<portlet:namespace />id_organismo" value="<%=organismo!=null?organismo.getId_organismo():""%>"/>
</fieldset>
</form>

				
<script type="text/javascript">	
	var popup;
	function <portlet:namespace />saveOrganismo() {
		 var cmd=document.<portlet:namespace />org.<portlet:namespace />cmd.value;		 
		 if (trim(jQuery('#<portlet:namespace />nombre').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />apellido').val())!= ""){
			 document.getElementById("<portlet:namespace />nombre").focus();
			 alert("Para agregar informacion sobre contactos debe presionar el boton 'Agregar'");
			 return false;
		 }
		//if (<portlet:namespace />validarCampos()) {
			if(cmd==""){
				document.<portlet:namespace />org.<portlet:namespace /><%= Constants.CMD %>.value='<%= Constants.ADD %>';
			}			
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_organismo_entry" /></portlet:actionURL>';			
			document.<portlet:namespace />org.method = 'post';			
			submitForm(document.<portlet:namespace />org, url);			
		//}
	}     
	
	function <portlet:namespace />validarCampos() {

		return true;
	}
	
	function <portlet:namespace />agregarArea(id_organismo){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_area_entry" /></portlet:renderURL>';
		document.<portlet:namespace />org.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';
		document.<portlet:namespace />org.<portlet:namespace />id_organismo.value = id_organismo;
		document.<portlet:namespace />org.method = 'post';
		submitForm(document.<portlet:namespace />org, url);
	
	}	
		
</script>


