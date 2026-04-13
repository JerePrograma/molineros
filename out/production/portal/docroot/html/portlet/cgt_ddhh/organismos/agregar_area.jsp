<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%

Calendar current = CalendarFactoryUtil.getCalendar();
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
int id_organismo=ParamUtil.getInteger(renderRequest,"id_organismo");

%>
<table class="lfr-table" width="100%">
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="4" align="center">
			<div align="center" id="<portlet:namespace />areas">
				<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/areas_search_result.jsp">
				</liferay-util:include>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>	
	<tr>
		<%if(id_organismo!=0 && esEdicion){%>
			<td colspan="4">
					<center><input type="button" value="<liferay-ui:message key="agregar-area"/>" onClick="<portlet:namespace />agregarArea('<%=id_organismo%>');" /></center>
			</td>
		<%}else{%>
			<td colspan="4">
				&nbsp;
			</td>
		<%}%>		
	</tr>	
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">	
	
	

	
</script>