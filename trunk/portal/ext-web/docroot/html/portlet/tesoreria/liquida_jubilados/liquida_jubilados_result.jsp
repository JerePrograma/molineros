<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil" %>
<%@ page import="ar.com.ospim.procesaArchivos.beans.JubiladosSitaci" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<JubiladosSitaci> archivos= LiquidaDesreguladosServiceUtil.getPeriodosProcesadosJubilados();

List<String> headerNames = new ArrayList<String>();

headerNames.add("Período");
headerNames.add("Cant.Registros ");
headerNames.add("Total Importe");
headerNames.add("Fecha Liquidado");
headerNames.add("Importe Liquidado");
headerNames.add("Liquidar");
headerNames.add("Ver");
headerNames.add("Eliminar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#0.00");
	
	for (int i = 0; i < archivos.size(); i++) {	    
		JubiladosSitaci liq = (JubiladosSitaci) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getPeriodo() );
	 	row.addText(liq.getTotalRegistros().toString());
	 	row.addText(df.format(liq.getConceptoImporte()));
	 	
	 	if(liq.getFechaLiquidado()!=null){
	 	   row.addText(sdf.format(liq.getFechaLiquidado()));
	 	}else{
	 		row.addText("");
	 	}
	 	
	 	
	 	if(liq.getImporteLiquidado()!=null){
		 	   row.addText(df.format(liq.getImporteLiquidado()));
		}else{
		 		row.addText("");
		}
	 	
	 	
	 	StringBuilder sb=new StringBuilder();
//	 	if(liq.getFechaLiquidado()==null ){	
	 		sb.append("&nbsp;&nbsp;<img alt=\"Liquidar Período\" src=\"");
			//sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/html/images/money_add.bmp\" onClick=\"javascript:liquidar('");
	 		sb.append(liq.getPeriodo() );
	 		sb.append("');\"");
            sb.append(" title=\"Liquidar Período\"");
 		    sb.append("/>");
//		}else{
//			sb.append("");
//		}
	 	
	 	row.addText(sb.toString());
	 	
	 	
	 	StringBuilder sb10=new StringBuilder();
	 	sb10=new StringBuilder();
	 	sb10.append("&nbsp;&nbsp;<img alt=\"Ver Errores\" src=\"");
		sb10.append(themeDisplay.getPathThemeImages());
	 	sb10.append("/document_library/xls.png\" onClick=\"javascript:verDetalle('");
	 	sb10.append(liq.getPeriodo());
	 	sb10.append("');\"");
        sb10.append(" title=\"Ver Detalle\"");
 		sb10.append("/>");
 		row.addText(sb10.toString());
 		
 		
 		StringBuilder sb2=new StringBuilder();
//	 	if(liq.getEnviadoSSS()==null ){	
	 		sb2.append("&nbsp;&nbsp;<img alt=\"Eliminar Período\" src=\"");
			sb2.append(themeDisplay.getPathThemeImages());
	 		sb2.append("/common/delete.png\" onClick=\"javascript:eliminar('");
	 		sb2.append(liq.getPeriodo() );
	 		sb2.append("');\"");
            sb2.append(" title=\"Elimina\"");
 		    sb2.append("/>");
//      }else{
//			sb.append("");
//		}
	 	
	 	row.addText(sb2.toString());
	 	
	 	
	 	resultRows.add(row);
	}
}
%>

<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">

function liquidar(periodo){
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/tesoreria/liquidacion_jubilados';
   	url = url+'&cmd'+'='+'liquidar'+'&periodo='+periodo;
   	jQuery('#<portlet:namespace />jubilados_liquidados').load(url,null, function(){
		jQuery('#<portlet:namespace />buscando').hide();      
    });	
}

function verDetalle(periodo){
	window.location.href ='/xlsservlet/?reporte=JUBILADOS_SITACI_DETALLE&periodo='+periodo+
			              '&rnd=' + Math.floor(Math.random()*100);
}


function eliminarLoteIntegracion(idLote){
	if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
		return false;
	}else{	
       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_lote='+idLote;
   	   jQuery('#<portlet:namespace />pagos_imputados').load(url); 

	}   
}

function eliminar(periodo){
	if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
		return false;
	}else{
	  jQuery('#<portlet:namespace />buscando').show();
	  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/tesoreria/liquidacion_jubilados';
   	  url = url+'&cmd'+'='+'eliminar'+'&periodo='+periodo;
   	  jQuery('#<portlet:namespace />jubilados_liquidados').load(url,null, function(){
		jQuery('#<portlet:namespace />buscando').hide();      
      });
	}  
}

</script>