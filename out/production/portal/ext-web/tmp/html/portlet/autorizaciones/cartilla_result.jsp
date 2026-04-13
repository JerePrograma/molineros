<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="ar.com.ospim.autorizaciones.beans.Cartilla" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();


List<Cartilla> archivos=(List<Cartilla>)session.getAttribute("CartillasLista");
String usuario_modi = user.getScreenName();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Tipo");
headerNames.add("Plan");
headerNames.add("Prestador");
headerNames.add("Domicilio");
headerNames.add("Teléfono");
headerNames.add("Localidad");
headerNames.add("Provincia");
headerNames.add("Especialidad");
headerNames.add("Trabaja en");
headerNames.add("Fecha Baja");
headerNames.add("Baja/Recupera");
   
   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "cartilla-no-encontrada"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Cartilla liq = (Cartilla) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		
		row.addText(liq.getTipo()!=null?liq.getTipo():"");
		row.addText(liq.getPlan()!=null?liq.getPlan():"");
		row.addText(liq.getPrestador()!=null?liq.getPrestador():"");
		row.addText(liq.getDomicilio()!=null?liq.getDomicilio():"");
		row.addText(liq.getTelefono()!=null?liq.getTelefono():"");
		row.addText(liq.getLocalidad()!=null?liq.getLocalidad():"");
		row.addText(liq.getProvincia()!=null?liq.getProvincia():"");
		row.addText(liq.getEspecialidad()!=null?liq.getEspecialidad():"");
		row.addText(liq.getTrabajaen()!=null?liq.getTrabajaen():"");
		row.addText(liq.getBaja_Fecha_string());
		
		StringBuilder sb=new StringBuilder();
		if(liq.getBajaFecha()==null){		 		
		  sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Baja Prestador\" src=\"");
	      sb.append(themeDisplay.getPathThemeImages());
          sb.append("/common/delete.png\" onClick=\"javascript:bajaCartilla('");
	      sb.append(liq.getId() );
	      sb.append("');\"");
          sb.append(" title=\"Baja\"");
	      sb.append("/>");
		}else{
		  sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Recuperar Prestador\" src=\"");
		  sb.append(themeDisplay.getPathThemeImages());
	 	  sb.append("/common/undo.png\" onClick=\"javascript:recuperaCartilla('");
	 	  sb.append(liq.getId() );
	 	  sb.append("');\"");
          sb.append(" title=\"Recuperar\"");
 		  sb.append("/>");
		}
		row.addText(sb.toString());  
		
		resultRows.add(row);
	}

}
%>
	
 		
	<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();
	var autorizacionEnEdicion;
    var popUpCierre;
    var popUpComprobante;
    
    function bajaCartilla(id_Cartilla){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_cartilla';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_cartilla='+id_Cartilla+'&usuario_modi='+'<%=usuario_modi%>';
	   	   autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Baja Cartilla:" />",modal:true,width:400});
	   	   jQuery(autorizacionEnEdicion).load(url);
		}   
	}
    
    function recuperaCartilla(id_Cartilla){
		if(!confirm("Desea recuperar el prestador dado de baja?")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_cartilla';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.RESTORE %>'+'&id_cartilla='+id_Cartilla+'&usuario_modi='+'<%=usuario_modi%>';
	   	   jQuery("#<portlet:namespace />divCartilla").load(url); 
		}   
	}
    
    function <portlet:namespace />cerrarCartilla(){
		Liferay.Popup.close(autorizacionEnEdicion);
    } 
	</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"  />