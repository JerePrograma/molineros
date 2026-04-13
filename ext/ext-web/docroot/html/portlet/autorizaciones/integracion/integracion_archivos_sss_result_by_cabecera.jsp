<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<%


List<String> errores = (List<String>)request.getAttribute("errores");
if (errores != null && !errores.isEmpty()){
	%>
	<table  style="color:red" >
	<%
	for (String error : errores){
		%>
		<tr><td>
		<%=error%>
		</td></tr>
		<%
	}
	%>
	</table>
	<%
}



PortletURL portletURL = renderResponse.createRenderURL();

List<IntegracionCabeceraDS> archivos= IntegracionServiceUtil.lotesSSSCab();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Nro.Lote");
headerNames.add("Entidad");
headerNames.add("Id. Cabecera");
headerNames.add("Enviado SSS");
headerNames.add("Período");
//headerNames.add("Fecha Proceso");
headerNames.add("Total ");
headerNames.add("Total OK");
headerNames.add("Total ERROR");
headerNames.add("Total LIQ");
//headerNames.add("Det");
//headerNames.add("Err");
headerNames.add("Liq");
headerNames.add("Det.Liq/OP");
headerNames.add("Impr.OP");
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
	 	row.addText(liq.getEntidad());
	 	row.addText(liq.getId().toString());
	 	
	 	row.addText(liq.getEnviadoSSS()==null?"":sdf.format(liq.getEnviadoSSS()));
	 	row.addText(liq.getPeriodo().toString());
	 	row.addText(liq.getDetalleProcesadosTOTAL().toString());
	 	row.addText(liq.getDetalleProcesadosOK().toString());
	 	row.addText(liq.getDetalleProcesadosError().toString());
	 	row.addText(liq.getLiquidados().toString());
/*	 	
	 	StringBuilder sb=new StringBuilder();
	 	sb=new StringBuilder();
	 	sb.append("&nbsp;&nbsp;<img alt=\"Ver Detalle\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
	 	sb.append("/document_library/xls.png\" onClick=\"javascript:verDetalleLoteIntegracionSSS('");
	 	sb.append(liq.getLoteSSS());
	 	sb.append("','SSSDET");
	 	sb.append("');\"");
        sb.append(" title=\"Detalle\"");
 		sb.append("/>");
 		row.addText(sb.toString());
 		
 		
        StringBuilder sb10=new StringBuilder();
	 	sb10=new StringBuilder();
	 	sb10.append("&nbsp;&nbsp;<img alt=\"Ver Errores\" src=\"");
		sb10.append(themeDisplay.getPathThemeImages());
	 	sb10.append("/document_library/xls.png\" onClick=\"javascript:verDetalleLoteIntegracionSSS('");
	 	sb10.append(liq.getLoteSSS());
	 	sb10.append("','SSSDETERR");
	 	sb10.append("');\"");
        sb10.append(" title=\"Errores\"");
 		sb10.append("/>");
 		row.addText(sb10.toString());
*/ 		
 		StringBuilder sb1=new StringBuilder();
 		
	 	if( liq.getFechaCierre()!=null && (liq.getLiquidados()==null || liq.getLiquidados()==0) && rolLiquidacion ){
	 		
	 		sb1.append("&nbsp;&nbsp;<img alt=\"Liquidar Lote\" src=\"");
			sb1.append(themeDisplay.getPathThemeImages());
	 		sb1.append("/common/sharing.png\" onClick=\"javascript:liquidarLoteIntegracion('");
	 		sb1.append(liq.getId() );
	 		sb1.append("');\"");
            sb1.append(" title=\"Liquidar\"");
 		    sb1.append("/>");
 		    
		}else{
			sb1.append("");
		}
	 	
	 	row.addText(sb1.toString());
	 	
	 	StringBuilder sb2=new StringBuilder();
	 	StringBuilder sb3=new StringBuilder();
	 	if(liq.getLiquidados()>0 && rolLiquidacion){
	 		
	 		sb2.append("&nbsp;&nbsp;<img alt=\"Detalle Liquidación\" src=\"");
			sb2.append(themeDisplay.getPathThemeImages());
	 		sb2.append("/common/print.png\" onClick=\"javascript:detalleLiquidacionLoteIntegracion('");
	 		sb2.append(liq.getId() );
	 		sb2.append("');\"");
            sb2.append(" title=\"Detalle Liquidación\"");
 		    sb2.append("/>");
 		    
 		    sb3.append("&nbsp;&nbsp;<img alt=\"Impresión de Ordenes de Pago\" src=\"");
			sb3.append(themeDisplay.getPathThemeImages());
	 		sb3.append("/common/print.png\" onClick=\"javascript:imprimirOPLoteIntegracion('");
	 		sb3.append(liq.getId() );
	 		sb3.append("');\"");
            sb3.append(" title=\"Impresión de Ordenes de Pago\"");
		    sb3.append("/>");
 		    
		}else{
			sb2.append("");
			sb3.append("");
		}
	 	
	 	row.addText(sb2.toString());
	 	row.addText(sb3.toString());
	 	
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

function liquidarLoteIntegracion(idLote){
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	url = url+'&<%= Constants.CMD %>'+'='+'liquidar_lote_cabecera'+'&id_lote='+idLote;
   	jQuery('#<portlet:namespace />lotes_sss').load(url,null, function(){
		jQuery('#<portlet:namespace />buscando').hide();      
    });	
}

function detalleLiquidacionLoteIntegracion(idLote){
	var tercerizadora=jQuery("#<portlet:namespace/>tercerizadoraLiq").val();
	window.location.href ='/xlsservlet/?reporte=REPORTE_DETALLE_LIQUIDACION_INTEGRACION_CABECERA&id='+idLote+
	'&tercerizadora=CABECERA'		
	'&rnd=' + Math.floor(Math.random()*100);
}



function imprimirOPLoteIntegracion(idLote){
	popup = Liferay.Popup({title:"Impresión Ordenes de Pago",modal:true,width:420});
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	url = url+'&<%= Constants.CMD %>'+'='+'impresion_op_lote_cabecera'+'&nrolote='+idLote;
   	jQuery(popup).load(url);
}

</script>