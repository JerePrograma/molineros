<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
 
<%

String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}
String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);
List<ModalidadAtencion> modalidadAtencionList=TraeListasServiceUtil.getModalidadAtencion() ;
List<NomencladorPlan> modalidades = (List<NomencladorPlan>) request.getSession().getAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);
List<Plan> planList=TraeListasServiceUtil.getPlanesMolineros();
%>


<table width="60%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		   <tr>
                   <td width="70px"><label><liferay-ui:message key="nivel-cobertura"/>  </label></td>
                   	               <td>
	               <select name="<portlet:namespace/>planNomenclador" id="<portlet:namespace/>planNomenclador">
				     <option value="0">Seleccione plan</option>
				     <option value="9999">TODOS LOS PLANES</option>
				     <%	for (Plan terce : planList) { %>
						<option value="<%= terce.getId() %>"><%=terce.getDescripcion()%></option>
				     <%	} %>
			         </select>
		           </td>
                   
<!--                     
	               <td  width="150px"><input id="<portlet:namespace />planNomenclador" name="<portlet:namespace />planNomenclador" size="20" maxlength="15" type="text" value=''/></td>
-->	               
	               
	               <td><label><liferay-ui:message key="recetas-pmi"/>:</label></td>
	               <td>
	                 <select name="<portlet:namespace/>autorizacionNomenclador" id="<portlet:namespace/>autorizacionNomenclador">
				     <option value="0">Seleccione autorizacion</option>
				     <%	for (ModalidadAtencion terce : modalidadAtencionList) { %>
						<option value="<%= terce.getId()%>"><%=terce.getDescripcion()%></option>
				     <%	} %>
			         </select>
		           </td>
<!--  	               
	               <td><input id="<portlet:namespace />autorizacionNomenclador" name="<portlet:namespace />autorizacionNomenclador" size="10" maxlength="10" type="text" value=''/></td>
-->	               
	               <td>
		               <input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarModalidad();" />
	               </td>
            </tr>
		<%} %>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoModalidad">
				<table style="align: center;" width="100%">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="12">
				<div align="center" id="<portlet:namespace />modalidadesAtencion">
					<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/nomencladorPlan_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<script type="text/javascript">
	function <portlet:namespace />agregarModalidad(){
		jQuery('#<portlet:namespace />agregandoModalidad').show();	
		var plan=jQuery('#<portlet:namespace />planNomenclador').val();
		var autorizacion=jQuery('#<portlet:namespace />autorizacionNomenclador').val();
		if(plan!=0 && autorizacion!=0){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_nomencladorPlan' 
			+ '&plan=' + encodeURI(plan)
			+ '&autorizacion=' +encodeURI(autorizacion)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />modalidadesAtencion').load(url, function() {
														jQuery('#<portlet:namespace />agregandoModalidad').hide();
														jQuery('#<portlet:namespace />planNomenclador').val('');
														jQuery('#<portlet:namespace />autorizacionNomenclador').val('')
										   }
			 );
		}else{
			jQuery('#<portlet:namespace />agregandoModalidad').hide();
		}	
	}

	function borraModalidad(idMod){
		var id = idMod.split("|");
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_nomencladorPlan'
			+  '&id_plan=' +id[0]
			+  '&id_autorizacion=' +id[1]
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />modalidadesAtencion').load(url, function() {
									jQuery('#<portlet:namespace />agregandoModalidad').hide();
			}
	   );
	}
	
	jQuery('#<portlet:namespace />agregandoModalidad').hide();
</script>