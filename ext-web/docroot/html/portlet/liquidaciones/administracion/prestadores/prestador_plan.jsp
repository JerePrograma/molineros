<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ include file="/html/portlet/liquidaciones/administracion/prestadores/init.jsp"%>

<%
Prestador prestador  = (Prestador)request.getSession().getAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);

ArrayList<Plan> planes = (ArrayList<Plan>) request.getSession().getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION);

String cmd = (String) request.getAttribute(Constants.CMD);

boolean esEdicion = false;

if (prestador == null  ||
   (  cmd!=null  && cmd.length() > 0  && !request.getAttribute(Constants.CMD).equals(Constants.VIEW)   ) ) {
	esEdicion = true;
}

Calendar fechaVigDesde = Calendar.getInstance();
Calendar fechaVigHasta = Calendar.getInstance();

String tabValue = ParamUtil.getString(request, "tab", null); // "datos"

%>
<portlet:defineObjects />

<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />prestador_fm" id="<portlet:namespace />prestador_fm" >
 	<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
 	<input type="hidden" name="<portlet:namespace />id_prestador"  value="<%=prestador!=null?prestador.getId_prestador():0%>" />
 
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="plan-prestador" />
	</legend>

	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<th><liferay-ui:message	key="descripcion" /></th>
			<th><liferay-ui:message	key="desde" /></th>
	   		<th><liferay-ui:message	key="hasta" /></th>	
			<th>&nbsp;</th>
		</tr>
		<tr>
			<td>
				<select   
					name="<portlet:namespace/>nuevoPlan" id="<portlet:namespace/>nuevoPlan" 
					style="width: 240px;"  <%if(!esEdicion){ %> disabled="disabled" <%} %>  >
					<option value='0'><liferay-ui:message key="seleccione-plan" /></option>
					<option value='-1'>- TODOS LOS PLANES OSPIM -</option>
					<%if(planes!=null){
						for (Plan plan : planes) { %>
						<option value="<%= plan.getId()%>"><%=plan.getDescripcion()%></option>
					<%} }%>			
				</select>
			</td>		
			<td>
	 			<liferay-ui:input-date 
					dayParam="nuevoPlanVigenDesdeDia"
					dayValue="<%= fechaVigDesde.get(Calendar.DATE)%>"
					monthParam="nuevoPlanVigenDesdeMes"
					monthValue="<%= fechaVigDesde.get(Calendar.MONTH) %>"
					yearParam="nuevoPlanVigenDesdeAnio"
					yearValue="<%= fechaVigDesde.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+20%>"
					firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion %>"  />
			</td>		 
			<td><liferay-ui:input-date
						dayNullable="true"
						dayParam="nuevoPlanVigenHastaDia"
						dayValue="<%= fechaVigHasta.get(Calendar.DATE)%>"
						monthNullable="true" 
						monthParam="nuevoPlanVigenHastaMes"
						monthValue="<%= fechaVigHasta.get(Calendar.MONTH) %>"
						yearNullable="true"
						yearParam="nuevoPlanVigenHastaAnio"
						yearValue="<%= fechaVigHasta.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaVigHasta.get(Calendar.YEAR) - 40 %>"
						yearRangeEnd="<%= fechaVigHasta.get(Calendar.YEAR)+60%>"
						firstDayOfWeek="<%= fechaVigHasta.getFirstDayOfWeek() - 1 %>"
						disabled="<%= !esEdicion %>" /></td>	
						
			<td><input type="button" value="<liferay-ui:message key="agregar" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
			      onClick="<portlet:namespace />agregarPresPlan();" /></td>
		</tr>
		<tr>
			<td colspan="8">
				<div id="<portlet:namespace />lista_planes">
					<jsp:include page='/html/portlet/liquidaciones/administracion/prestadores/lista_planes_prestador.jsp' />
				</div>
			</td>
		</tr>
	</table>
</fieldset>

<%if(esEdicion){ %>
<br/>
<div align="left" style="vertical-align: bottom;" >
<input type="button" value="<liferay-ui:message key="back" />"
	onClick="<portlet:namespace />anteriorSolapa();" />
&nbsp;&nbsp;	
<input type="button" value="<liferay-ui:message key="next" />"
	onClick="<portlet:namespace />siguienteSolapa();" />
</div>
<%} %>

</form>
	  		
<script type="text/javascript">
	function <portlet:namespace />agregarPresPlan(){
		
		var idPlan = parseInt(jQuery('#<portlet:namespace />nuevoPlan').val());
		var diaDesde=jQuery('#<portlet:namespace />nuevoPlanVigenDesdeDia').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />nuevoPlanVigenDesdeMes').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />nuevoPlanVigenDesdeAnio').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />nuevoPlanVigenHastaDia').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />nuevoPlanVigenHastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />nuevoPlanVigenHastaAnio').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;	    
 		
		if(idPlan == 0){
			alert("Debe seleccionar un plan");
			return false;
		}

<%-- 		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/lista_planes_prestador';
		url = url+'&idPlan='+idPlan+
		'&vigenDesde='+fechaDesdeFinal+
		'&vigenHasta='+fechaHastaFinal; --%>
		var xportletUrl = '/liquidaciones/lista_planes_prestador';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="idPlan" value="__idPlan"/>'+
		'<liferay-portlet:param name="vigenDesde" value="__vigenDesde"/>'+
		'<liferay-portlet:param name="vigenHasta" value="__vigenHasta"/>'+
	    '</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__idPlan", idPlan);
  	    url = url.replace("__vigenDesde", encodeURI(fechaDesdeFinal));
  	    url = url.replace("__vigenHasta", encodeURI(fechaHastaFinal));
  	    
		jQuery("#<portlet:namespace />lista_planes").load(url); 
		jQuery('#<portlet:namespace />nuevoPlan').val(0);
	}
	
	function <portlet:namespace />siguienteSolapa() {		
			<%-- var accionEnCurso = jQuery('#<portlet:namespace /><%= Constants.CMD %>').val(); --%>
			var accionEnCurso = document.<portlet:namespace />prestador_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />prestador_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
			
			<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_prestadores_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=lugar_atencion'; --%>
			var xportletUrl = '/liquidaciones/editar_prestadores_entry';
			var cmd_ = '<%=Constants.MOVE%>';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="cmd" value="__cmd"/>'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
			'<liferay-portlet:param name="moverATab" value="lugar_atencion"/>'+
			
		    '</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__cmd", cmd_);
	  	    url = url.replace("__accionEnCurso", accionEnCurso); 
			
			document.<portlet:namespace />prestador_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_fm, url); 
	}
	
	function <portlet:namespace />anteriorSolapa() {		
			<%-- var accionEnCurso = jQuery('#<portlet:namespace /><%= Constants.CMD %>').val(); --%>
			var accionEnCurso = document.<portlet:namespace />prestador_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />prestador_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
			
<%-- 		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_prestadores_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=datos'; --%>
			var xportletUrl = '/liquidaciones/editar_prestadores_entry';
			var cmd_ = '<%=Constants.MOVE%>';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="cmd" value="__cmd"/>'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
			'<liferay-portlet:param name="moverATab" value="datos"/>'+
			
		    '</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__cmd", cmd_);
	  	    url = url.replace("__accionEnCurso", accionEnCurso); 
			
			document.<portlet:namespace />prestador_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_fm, url);
	}
</script>


