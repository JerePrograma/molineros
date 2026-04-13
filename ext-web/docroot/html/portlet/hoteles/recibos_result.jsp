<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
//private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
boolean showAprobacion=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_APROBACION_RECIBOS);
PortletURL portletURL = renderResponse.createRenderURL();
List<Recibo> archivos=(List<Recibo>)session.getAttribute(WebKeysHoteles.RECIBOS_RESULT);
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
DecimalFormat df= new DecimalFormat("#.00");
List<String> headerNames = new ArrayList<String>();
headerNames.add("Sucursal");
headerNames.add("Número");
headerNames.add("Fecha");
headerNames.add("Cliente");
headerNames.add("Total");
headerNames.add("Baja");

if(showAprobacion){
	headerNames.add("Aprobado");
}

headerNames.add("Editar|Anular");
   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				"No se han encontrado Recibos");
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Recibo liq = (Recibo) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		row.addText(liq.getSucursal());
		row.addText(String.valueOf(liq.getNumero()));
		row.addText(sdf.format(liq.getFecha()));
		row.addText(String.format("%-50s",liq.getCliente().getDescripcionCliente()));
		row.addText(df.format(liq.getTotal()));
		row.addText( liq.getFechaBaja()!=null? sdf.format(liq.getFechaBaja()):"");
		
		
		if(showAprobacion){
			if(liq.getAprobadoFecha()!=null){
				row.addText((new SimpleDateFormat("dd/MM/yyyy")).format(liq.getAprobadoFecha()));
			}else{
				
				StringBuilder sb1=new StringBuilder();
				sb1.append("<input type=\"checkbox\"");
//				sb1.append("checked=\"checked\"");
				sb1.append("name=\"aprobado\"");		
				sb1.append("id=\"");
				sb1.append("check-"+liq.getSucursal()+"_"+liq.getNumero());
			    sb1.append("\" value=\"");
				sb1.append(liq.getSucursal()+"_"+liq.getNumero());
				sb1.append("\"/>");		
				
				row.addText(sb1.toString());
				
			}
		}
		
		
		StringBuilder sb=new StringBuilder();
		if(liq.getFechaBaja()==null){
		  sb.append("&nbsp;&nbsp;<img alt=\"Editar Recibo\" src=\"");
          sb.append(themeDisplay.getPathThemeImages());
	      sb.append("/common/edit.png\" onClick=\"javascript:editarRecibo('");
	      sb.append(liq.getSucursal());
	      sb.append("','");
	      sb.append(liq.getNumero()  );
	      sb.append("'");
	      sb.append(");\"");
          sb.append(" title=\"Editar\"");
	      sb.append("/>");
		
	      if(liq.getFechaProceso()==null || showAprobacion){
	        sb.append("&nbsp;&nbsp;<img alt=\"Eliminar Recibo\" src=\"");
            sb.append(themeDisplay.getPathThemeImages());
	        sb.append("/common/delete.png\" onClick=\"javascript:eliminarRecibo('");
	        sb.append(liq.getSucursal());
	        sb.append("','");
	        sb.append(liq.getNumero()  );
	        sb.append("'");
	        sb.append(");\"");
            sb.append(" title=\"Eliminar\"");
	        sb.append("/>");
	      }  
		}else{
		  sb.append("");	
		}
	    
		row.addText(sb.toString());  
				
		resultRows.add(row);
	}

}
%>
	
 		
	<script type="text/javascript">
	
	
	function editarRecibo(hotel,recibo){
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_hotel=" + hotel;
	 	params+="&id_recibo=" + recibo;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	}	
	
	function eliminarRecibo(hotel,recibo){
		popupMD = Liferay.Popup({title:"Anulación de Recibos" ,modal:true,position:[150,50],xy: ['center', 100],width:1000,
			 onClose: function() {
				 <portlet:namespace />buscarRecibos();

		}});        
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" /></portlet:renderURL>';	
	    url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_hotel='+hotel+'&id_recibo='+recibo;
	   	jQuery(popupMD).load(url); 
				  
	}
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>