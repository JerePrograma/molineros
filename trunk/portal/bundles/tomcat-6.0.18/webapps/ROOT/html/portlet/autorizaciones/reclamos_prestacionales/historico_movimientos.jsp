<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%
ReclamoPrestacional reclamo = (ReclamoPrestacional)request.getSession()
.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);

int idReclamo = (reclamo != null) ? reclamo.getId_reclamo() : 0;
%>

<fieldset class="block-labels">
  <legend>Histórico de Movimientos</legend>
  
  <h1>Reclamo Prestacional Nro <%=idReclamo%></h1>
</fieldset>

<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />resultadoHistorico">

</div>
</fieldset>

<script type="text/javascript">
jQuery(function(){
  var ns = '<portlet:namespace/>';
  var $buscando = jQuery('#'+ns+'buscando');
  var $resultado = jQuery('#'+ns+'resultadoHistorico');

  var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">' +
              '<portlet:param name="struts_action" value="/autorizaciones/historico_reclamo"/>' +
              '<portlet:param name="idReclamo" value="<%= String.valueOf(idReclamo) %>"/>' +
            '</portlet:renderURL>';
  $buscando.show();
  $resultado.load(url, {}, function(){
    $buscando.hide();
  });
});
</script>
