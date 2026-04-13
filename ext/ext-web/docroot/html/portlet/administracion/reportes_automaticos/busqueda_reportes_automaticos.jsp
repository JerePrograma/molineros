<%@ include file="/html/portlet/administracion/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%
	ReportesAutomaticosConfiguracion rac = (ReportesAutomaticosConfiguracion) renderRequest
			.getAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION);
%>

<table class="lfr-table">
	<tr>
		<td>Email:</td>
		<td><%=rac.getMailFrom()%></td>
	</tr>
	<tr>
		<td>Pass:</td>
		<td><%=rac.getPass()%></td>
	</tr>
	<tr>
		<td>Mails para envio de errores:</td>
		<td><%=rac.getMailsDeError()%></td>
	</tr>
	<tr>
		<td><input id="<portlet:namespace />editar_config" value="<liferay-ui:message key="editar_config"/>" title="<liferay-ui:message key="editar_config" />" type="button" /></td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
</table>
<table>
	<tr>
		<td><input id="<portlet:namespace />correr" value="<liferay-ui:message key="correr"/>" title="<liferay-ui:message key="correr" />" type="button" /></td>
		<td><input id="<portlet:namespace />agregar" value="<liferay-ui:message key="agregar"/>" title="<liferay-ui:message key="agregar" />" type="button" /></td>
	</tr>
</table>

<portlet:defineObjects />
<%
	//Si debe mostrarse el btn de agregar afiliado								
	List<ReporteAutomatico> reportes = (ArrayList<ReporteAutomatico>) renderRequest
			.getAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS);
	PortletURL portletURL = renderResponse.createRenderURL();
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("titulo");
	headerNames.add("Periodicidad");
	headerNames.add("hora");
	headerNames.add("stored-procedure");
	headerNames.add("csv-parameteres");
	headerNames.add("emails");
	headerNames.add("ultima-ejecucion");
	headerNames.add("editar-borrar");
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-reportes-were-found"));

	if (null != reportes) {
		//Seteo el total de la lista.
		int total = reportes.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < reportes.size(); i++) {
			ReporteAutomatico ra = (ReporteAutomatico) reportes.get(i);
			ResultRow row = new ResultRow(ra, ra.getId(), i);
			row.addText(String.valueOf(ra.getId()));
			row.addText(ra.getTitulo());
			StringBuilder perio = new StringBuilder();
			if (ra.getDiaDeLaSemana() != 0) {
				perio.append("Semanal - ");
				perio.append(ra.getDiaDeLaSemanaString());
			} else if (ra.getDiaDelMes() != 0) {
				perio.append("Mensual - el ");
				perio.append(ra.getDiaDelMes());
				perio.append(" de cada mes");
			} else if (ra.getFechaUnicaVez() != null) {
				perio.append("Unica vez - ");
				perio.append(ra.getFechaUnicaVezAsString());
			} else if (ra.isDiario()) {
				perio.append("Diario - ");
				perio.append(ra.isIncluirFinDeSemana() ? "Si" : "No");
				perio.append(" incluye fin de semana");
			}
			row.addText(perio.toString());
			row.addText(String.valueOf(ra.getHora()));
			row.addText(ra.getStoredProcedure());
			row.addText(ra.getCsvParameteres());
			row.addText(ra.getEmails());
			row.addText(ra.getUltimaEjecucionAsString());
			StringBuilder sb = new StringBuilder();
			sb
					.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb
					.append("/common/edit.png\" onClick=\"javascript:editarReporte('");
			sb.append(ra.getId());
			sb.append("');\" />&nbsp;/&nbsp;");
			sb
					.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb
					.append("/common/delete.png\" onClick=\"javascript:borrarReporte('");
			sb.append(ra.getId());
			sb.append("');\" />");
			row.addText(sb.toString());
			resultRows.add(row);
		}
	}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
	var popup;
	var popupConfig;
	
	jQuery('#<portlet:namespace />editar_config').click(function(){
		popupConfig = Liferay.Popup({title:"<liferay-ui:message key="agregar-reporte" />",modal:true,width:700,position:[150,10],xy: ['center', 100],
				 onClose: function() {
			  		var urlReload = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/administracion/view';
					 window.location.href = urlReload;
			 	}});        
	     var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/administracion/editar_configuracion_reportes_automaticos';
	     url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popupConfig).load(url);
	});
	
	jQuery('#<portlet:namespace />agregar').click(function(){
		 popup = Liferay.Popup({title:"<liferay-ui:message key="agregar-reporte" />",modal:true,width:700,position:[150,10],xy: ['center', 100],
				 onClose: function() {
			  		var urlReload = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/administracion/view';
					 window.location.href = urlReload;
			 	}});        
	     var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/administracion/agregar_reportes';
	     url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);
	});

	function editarReporte(id){
		popup = Liferay.Popup({title:"<liferay-ui:message key="agregar-reporte" />",modal:true,width:700,position:[150,10],xy: ['center', 100],
			 onClose: function() {
				var urlReload = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/administracion/view';
				 window.location.href = urlReload;
		 	}});
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/administracion/agregar_reportes';
	    url += '&id=' + id
	    url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);
	}

	function borrarReporte(id){
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/administracion/borrar_reportes';
	    url += '&id=' + id
	    url += '&rnd=' + Math.floor(Math.random()*100);
	    window.location.href = url;
	}

	jQuery('#<portlet:namespace />correr').click(function(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/administracion/correr_reportes" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	});
	
	
</script>