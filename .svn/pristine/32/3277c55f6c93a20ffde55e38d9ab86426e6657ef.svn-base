<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%


PortletURL portletURL = renderResponse.createRenderURL();

DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
List<FechaPresentacionSSS> fechaSSS = null;

FechaOpcionSSSUtil fechaUtil = new FechaOpcionSSSUtil();

fechaSSS=fechaUtil.traerFechasPresentacionSSS();

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Fecha Opción SSS");
headerNames.add("Usuario");
headerNames.add("Fecha Alta");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no_hay_fechas_press_sss"));
					
if (fechaSSS != null && !fechaSSS.isEmpty()){
	int total = fechaSSS.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < fechaSSS.size(); i++) {	    
		FechaPresentacionSSS fecha = (FechaPresentacionSSS) fechaSSS.get(i);
	 	ResultRow row = new ResultRow(fecha,new Integer(1+i), i);
	 	row.addText(dateFormat.format(fecha.getFechaPresSSS()));
		row.addText(fecha.getAltaUsr());
		row.addText(sdf.format(fecha.getAltaFechaUsr()));
	
		
		resultRows.add(row);
	}
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

