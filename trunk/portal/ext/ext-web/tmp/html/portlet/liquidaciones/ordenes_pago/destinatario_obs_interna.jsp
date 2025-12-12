<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
boolean esEdicion = ParamUtil.getBoolean(request, "esEditable");
String destino=ParamUtil.getString(request,"destino",null);
String obsInterna=ParamUtil.getString(request,"obs_interna",null);
%>

	<tr>
		<td>
			<label><liferay-ui:message key="tipo-destinatario" />:</label>
			<select id="<portlet:namespace />tipo_destinatario" name="<portlet:namespace />tipo_destinatario" onchange="changeDestinatario();">
					<option value="SECCIONAL">Seccional</option>
					<option value="OTROS" selected="true">OTROS</option>				
			</select>		
		</td>
		<td rowspan="3">
				<textarea rows="5" cols="40" id="<portlet:namespace />destino" name="<portlet:namespace />destino"  onclick="if (this.value == 'Ingrese Destino') this.value = '';"><%=destino != null ? destino  : "Ingrese Destino..."%></textarea>
		</td>
		<td rowspan="3"><label><liferay-ui:message key="observacion-interna" />:</label></td>
		<td rowspan="3" colspan="3">
			<textarea rows="5" cols="40" id="<portlet:namespace />observacion_interna" name="<portlet:namespace />observacion_interna"><%=obsInterna != null ? obsInterna  : ""%></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">
			<span id="<portlet:namespace />bus_seccional">
					<liferay-ui:message key="seccional" />: <liferay-util:include page="/html/portlet/liquidaciones/busqueda_seccional.jsp">
						<liferay-util:param name="esEdicion" value="true" />
						<liferay-util:param name="destino" value="true" />
						<liferay-util:param name="prefijo" value="DEST" />
					</liferay-util:include>
			</span>
		</td>
	</tr>	

<script type="text/javascript">
	document.getElementById("<portlet:namespace />bus_seccional").style.visibility = "hidden";
	function changeDestinatario(){		
		
		if (document.getElementById("<portlet:namespace />tipo_destinatario").value == "SECCIONAL"){			
			document.getElementById("<portlet:namespace />bus_seccional").style.visibility = "visible";			
		} else {			
			document.getElementById("<portlet:namespace />bus_seccional").style.visibility = "hidden";			
		}
	}
</script>