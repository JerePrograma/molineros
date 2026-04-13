<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	AfiObservacion ao = (AfiObservacion) request.getAttribute(WebKeysAfiliados.OBSERVACION_GRUPO_FLIAR_VER);
%>

<fieldset class="block-labels">
	<legend><liferay-ui:message key="observacion-interna" /></legend>
	</br>
	<table class="lfr-table" style="width: 100%; height: 100%px;">

	<%if(ao != null){%>
	<tr>
		<td><textarea cols="120" rows="20" readonly="readonly"
			name="<portlet:namespace/>obsGrpFliar" 
			id="<portlet:namespace/>obsGrpFliar"><%= ao.getObservacion() %></textarea></td>
	</tr>
	<tr>
		<td width="100%;">
			<table style="font-size: 8">
				<tr>
					<td><label><liferay-ui:message key="Alta Usuario" />:</label></td>
					<td><%=ao.getAltaUsr() %></td>
					<td><label><liferay-ui:message key="Alta Fecha" />:</label></td>
					<td><%=sdf.format(ao.getAltaFecha()) %></td>
				</tr>
			</table>   
		</td>
	</tr>
	<%}%>
	</table>
</fieldset>