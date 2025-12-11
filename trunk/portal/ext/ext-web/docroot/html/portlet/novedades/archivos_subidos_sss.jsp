<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="ar.com.ospim.novedades.beans.ArchivoNovedad" %>
<%@ page import="ar.com.ospim.novedades.service.NovedadesServiceUtil"%>
<%@ page import="java.util.Locale" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();
SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy",  new Locale("es", "ES"));
//SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

List<ArchivoNovedad> archivos=(List<ArchivoNovedad>)renderRequest.getAttribute("archivosNovedades");
if(archivos==null){
	archivos= NovedadesServiceUtil.getInstance().getArchivosNovedades(null);
}
List<String> headerNames = new ArrayList<String>();
//headerNames.add("Fecha Archivo");
headerNames.add("Periodo Archivo");
headerNames.add("Descripcion");
headerNames.add("cant-registros");
headerNames.add("Usuario Import.");
headerNames.add("Fecha Import.");

//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ArchivoNovedad an = (ArchivoNovedad) archivos.get(i);
	 	ResultRow row = new ResultRow(an,new Integer(1+i), i);
	 	row.addText(sdf.format(an.getFechaArchivo()));
		row.addText(an.getDescripcion());	
		row.addText(String.valueOf(an.getCantRegistros()));
		row.addText(an.getImportUsr());
		row.addText(sdf2.format(an.getImportFecha()));
		
		resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

