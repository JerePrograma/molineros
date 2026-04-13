<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%

%>
<portlet:defineObjects />

<fieldset class="block-labels"><legend><liferay-ui:message key="Prestador" /></legend>

<table class="lfr-table">
	<tr>
		<td colspan="5">
			<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
		  		<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestador"/>
		  		<liferay-util:param name="cuit_prestador" value=''/>
		  		<liferay-util:param name="nombre_prestador" value=''/>
		  		<liferay-util:param name="esEditable" value='<%= String.valueOf(true) %>'/>
		  		<liferay-util:param name="ext" value="_bp"/>
			</liferay-util:include>
		</td>					
	</tr>
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="5" align="center"><input type="submit"
			value="Aceptar"
			onClick="<portlet:namespace />aceptarCopiaConvenioPrest();return false;" />
			&nbsp; &nbsp; &nbsp; &nbsp;
		<input type="submit"
			value="Cancelar"
			onClick="<portlet:namespace />cancelarCopiaConvenioPrest();return false;" />
		</td>
	</tr>	
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
</table>
</fieldset>
	
<div align="center" id="<portlet:namespace />divBusqueda_prestador_bp">
<table style="align: center;">
	<tr>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>

<div align="center" id="<portlet:namespace />divResultado_conv_prest_bp">
</div>

<script type="text/javascript">
jQuery('#<portlet:namespace />divBusqueda_prestador_bp').hide();
</script>