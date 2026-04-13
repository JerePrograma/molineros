<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<IntegracionCabeceraDS> archivos= IntegracionServiceUtil.lotesProcesados();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Nro.Lote");
headerNames.add("Entidad");
headerNames.add("Período");
headerNames.add("Fecha Proceso");
headerNames.add("Total ");
headerNames.add("Total OK");
headerNames.add("Total ERROR");
headerNames.add("Lote SSS");
headerNames.add("Enviado SSS");
headerNames.add(" ");
headerNames.add("Err");
headerNames.add(" ");
headerNames.add("Det");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	for (int i = 0; i < archivos.size(); i++) {	    
		IntegracionCabeceraDS liq = (IntegracionCabeceraDS) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getId().toString());
	 	row.addText(liq.getEntidad());
	 	row.addText(liq.getPeriodo().toString());
	 	row.addText(sdf.format(liq.getFecha()));
	 	row.addText(liq.getDetalleProcesadosTOTAL().toString());
	 	row.addText(liq.getDetalleProcesadosOK().toString());
	 	row.addText(liq.getDetalleProcesadosError().toString());
	 	row.addText(liq.getLoteSSS().toString());
	 	if(liq.getEnviadoSSS()!=null){
	 	   row.addText(sdf.format(liq.getEnviadoSSS()));
	 	}else{
	 		row.addText("");
	 	}
	 	
	 	StringBuilder sb=new StringBuilder();
	 	if(liq.getEnviadoSSS()==null ){	
	 		sb.append("&nbsp;&nbsp;<img alt=\"Eliminar Lote\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/delete.png\" onClick=\"javascript:eliminarLoteIntegracion('");
	 		sb.append(liq.getId() );
	 		sb.append("');\"");
            sb.append(" title=\"Elimina\"");
 		    sb.append("/>");
		}else{
			sb.append("");
		}
	 	
	 	row.addText(sb.toString());
	 	
	 	sb=new StringBuilder();
	 	if(liq.getDetalleProcesadosError()>0 ){
	 		sb.append("&nbsp;&nbsp;<img alt=\"Ver Errores\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/print.png\" onClick=\"javascript:verErroresLoteIntegracion('");
	 		sb.append(liq.getId() );
	 		sb.append("');\"");
            sb.append(" title=\"Errores\"");
 		    sb.append("/>");
		}else{
			sb.append("");
		}
	 	
	 	row.addText(sb.toString());
	 	
	 	
	 	
	 	sb=new StringBuilder();
	 	if(liq.getDetalleProcesadosError()>0 && liq.getEnviadoSSS()==null){
	 		sb.append("&nbsp;&nbsp;<img alt=\"Verificar Errores\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/portlet/refresh.png\" onClick=\"javascript:verificarErroresLoteIntegracion('");
	 		sb.append(liq.getId() );
	 		sb.append("');\"");
            sb.append(" title=\"Verifica Errores\"");
 		    sb.append("/>");
		}else{
			sb.append("");
		}
	 	
	 	row.addText(sb.toString());
	 	
	 	
	 	sb=new StringBuilder();
	 	sb.append("&nbsp;&nbsp;<img alt=\"Ver Detalle\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
	 	sb.append("/common/print.png\" onClick=\"javascript:verDetalleLoteIntegracion('");
	 	sb.append(liq.getId() );
	 	sb.append("');\"");
           sb.append(" title=\"Detalle\"");
 		   sb.append("/>");
		
	 	row.addText(sb.toString());
	 	
	 	
	 	resultRows.add(row);
	}
}
%>

<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">
function eliminarLoteIntegracion(idLote){
	if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
		return false;
	}else{	
       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_lote='+idLote;
   	   jQuery('#<portlet:namespace />pagos_imputados').load(url); 

	}   
}

function verErroresLoteIntegracion(id){
	var url = '/xlsservlet/?reporte=REPORTE_ERRORES_INTEGRACION'
		+ '&id=' + id
		+ '&soloerrores=SI';
	
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
	
	
}

function verificarErroresLoteIntegracion(idLote){
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	url = url+'&<%= Constants.CMD %>'+'='+'verifica_lote'+'&id_lote='+idLote;
//   	jQuery('#<portlet:namespace />pagos_imputados').load(url); 
   	
   	jQuery('#<portlet:namespace />pagos_imputados').load(url,null, function(){
		jQuery('#<portlet:namespace />buscando').hide();      
     });	
}


function verDetalleLoteIntegracion(id){
	window.location.href ='/xlsservlet/?reporte=REPORTE_ERRORES_INTEGRACION&id='+id+
			              '&soloerrores=NO'+
			              '&rnd=' + Math.floor(Math.random()*100);
}
</script>