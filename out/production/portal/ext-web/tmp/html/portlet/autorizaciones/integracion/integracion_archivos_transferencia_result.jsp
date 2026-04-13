<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<IntegracionCabeceraDS> archivos= IntegracionServiceUtil.lotesTransferenciasExtractos();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Nro.Lote");
headerNames.add("Fecha Proceso");
headerNames.add("Usuario");
headerNames.add("Total ");
headerNames.add("OP.Encontradas ");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));

boolean rolLiquidacion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_LIQUIDACION);
boolean rolGeneracion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_GENERACION);
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	for (int i = 0; i < archivos.size(); i++) {	    
		IntegracionCabeceraDS liq = (IntegracionCabeceraDS) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getLoteSSS().toString());
	 	row.addText(sdf.format(liq.getFecha()));
	 	row.addText(liq.getEntidad());
	 	row.addText(liq.getDetalleProcesadosTOTAL().toString());
		row.addText(liq.getDetalleProcesadosOK().toString());
		
	 	resultRows.add(row);
	 		
	}
}
%>

<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
		
		
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
</fieldset>		

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
var popup;



</script>