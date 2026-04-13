<%@ include file="/html/portlet/estudio_isidro/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<ArchivoSubidoEstudio> archivos=(List<ArchivoSubidoEstudio>)renderRequest.getAttribute("archivoSubidoEstudio");
if(archivos==null){
	archivos=LlamadoServiceUtil.getArchivosSubidosEstudio()  ;
}
List<String> headerNames = new ArrayList<String>();
headerNames.add("tipo");
headerNames.add("Nro Lote");
headerNames.add("Tipo Lote");
headerNames.add("Cantidad");
headerNames.add("Usuario");
headerNames.add("Proceso");
//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ArchivoSubidoEstudio liq = (ArchivoSubidoEstudio) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	row.addText(liq.getTipo());
	 	row.addText(liq.getNroLote().toString());
	 	row.addText(liq.getTipoLote());
	 	row.addText(String.valueOf(liq.getCantReg()));
	 	row.addText(liq.getUsuario());
	 	row.addText(liq.getFechaProcesoAsString());
		resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

