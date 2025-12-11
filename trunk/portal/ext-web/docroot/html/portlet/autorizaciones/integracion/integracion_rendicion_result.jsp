<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDR" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();
NumberFormat nf = new DecimalFormat("#0.00");
List<IntegracionCabeceraDR> archivos= IntegracionServiceUtil.lotesRendicion();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Período");
headerNames.add("Fecha Cierre");
headerNames.add("Importe Solicitado");
headerNames.add("Importe Liquidado");
//headerNames.add("Fecha Proceso");
headerNames.add("Total ");
headerNames.add("Total OK");
headerNames.add("Total ERROR");

headerNames.add("Generar Dev.");

headerNames.add("Det");
headerNames.add("Cerrar");
headerNames.add("Eliminar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));

boolean rolRendicion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_RENDICION);
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	for (int i = 0; i < archivos.size(); i++) {	    
		IntegracionCabeceraDR liq = (IntegracionCabeceraDR) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getPeriodo().toString());
	 	row.addText(liq.getFechaCierre() ==null?"":sdf.format(liq.getFechaCierre()));
	 	row.addText(nf.format(liq.getImporteSolicitado()));
	 	row.addText(nf.format(liq.getImporteLiquidado()));
	 	
	 	row.addText(liq.getDetalleProcesadosTOTAL().toString());
	 	row.addText(liq.getDetalleProcesadosOK().toString());
	 	row.addText(liq.getDetalleProcesadosError().toString());
	 	
	 	StringBuilder sb=new StringBuilder();
	 	
	 	sb=new StringBuilder();
	 	if( liq.getFechaCierre()==null && !liq.isTieneDetalleDevolucion()
	 			&& rolRendicion ){
	 	    sb.append("&nbsp;&nbsp;<img alt=\"Habilitar Devolución\" src=\"");
		    sb.append(themeDisplay.getPathThemeImages());
	 	    sb.append("/common/reply.png\" onClick=\"javascript:generarDevolucion('");
	 	    sb.append(liq.getPeriodo());
	 	    sb.append("');\"");
            sb.append(" title=\"Habilitar Devolución\"");
 		    sb.append("/>");
	 	}else{
	 		sb.append("");
	 	}
 		row.addText(sb.toString());
 		
 		
 		sb=new StringBuilder();
	 	if( liq.getFechaCierre()==null && liq.isTieneDetalleDevolucion()
	 			&& rolRendicion ){
	 	    sb.append("&nbsp;&nbsp;<img alt=\"Editar Devolución\" src=\"");
		    sb.append(themeDisplay.getPathThemeImages());
	 	    sb.append("/common/edit.png\" onClick=\"javascript:editarDevolucionPeriodo('");
	 	    sb.append(liq.getPeriodo());
	 	    sb.append("');\"");
            sb.append(" title=\"Editar Devolución\"");
 		    sb.append("/>");
	 	}else{
	 		sb.append("");
	 	}
 		row.addText(sb.toString());
 		
 		
 		StringBuilder sb11=new StringBuilder();
 		if( liq.getFechaCierre()==null && rolRendicion ){
	 		sb11.append("&nbsp;&nbsp;<img alt=\"Cerrar Lote\" src=\"");
			sb11.append(themeDisplay.getPathThemeImages());
	 		sb11.append("/common/define_permissions.png\" onClick=\"javascript:cerrarRendicion('");
	 		sb11.append(liq.getPeriodo() );
	 		sb11.append("');\"");
            sb11.append(" title=\"Cerrar\"");
 		    sb11.append("/>");
		}else{
			sb11.append("");
		}
	 	row.addText(sb11.toString());
	 	
	 	
	 	StringBuilder sb12=new StringBuilder();
	 	
 		if( liq.getFechaCierre()==null && rolRendicion ){
	 		sb12.append("&nbsp;&nbsp;<img alt=\"Borrar\" src=\"");
			sb12.append(themeDisplay.getPathThemeImages());
	 		sb12.append("/common/time.png\" onClick=\"javascript:eliminarRendicion('");
	 		sb12.append(liq.getPeriodo() );
	 		sb12.append("');\"");
            sb12.append(" title=\"Eliminar rendición\"");
 		    sb12.append("/>");
		}else{
			sb12.append("");
		}
 		
	 	row.addText(sb12.toString());
 		
 		
	 	
	 		
	 	resultRows.add(row);
	 		
	}
}
%>

<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
		
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

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
var popup;


function generarDevolucion(periodo){
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	url = url+'&<%= Constants.CMD %>'+'='+'generar_devolucion'+'&periodo='+periodo;
   	jQuery('#<portlet:namespace />lotes_sss').load(url,null, function(){
		jQuery('#<portlet:namespace />buscando').hide();      
    });	
   	
}

function editarDevolucionPeriodo(periodo){

	var params = "&<%= Constants.CMD %>=" + "editar_devolucion_periodo";
 	params+="&periodo=" + periodo;
 	params+="&offset_reg=1";
 	 	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/integracion_editar" /></portlet:renderURL>';
	url = url + params;
	document.<portlet:namespace />fmSSS.method = 'post';
	submitForm(document.<portlet:namespace />fmSSS, url);
}



function eliminarRendicion(periodo){
	var confirmar = false;
	confirmar=confirm ('Esta seguro de eliminar el Período?');
	if(confirmar){
	   
		var params = "&<%= Constants.CMD %>=" + "eliminar_rendicion_periodo";
	 	params+="&periodo=" + periodo;
	 	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/integracion_editar" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fmSSS.method = 'post';
		submitForm(document.<portlet:namespace />fmSSS, url);	
		
	}
}


function cerrarRendicion(periodo){
	var confirmar = false;
	confirmar=confirm ('Esta seguro de cerrar el Período?');
	if(confirmar){
	  jQuery('#<portlet:namespace />buscando').show();
	  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	  url = url+'&<%= Constants.CMD %>'+'='+'cerrar_periodo_rendicion'+'&periodo='+periodo;
   	
   	
   	  jQuery('#<portlet:namespace />lotes_sss').load(url,null, function(){
		   jQuery('#<portlet:namespace />buscando').hide();      
      });	
	}
}


</script>