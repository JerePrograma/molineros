<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
PortletURL portletURL = renderResponse.createRenderURL();
String portlet_name = "tesoreria";
int entidad_nro=1;		
		
if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
		entidad_nro=3;
}		
if(renderResponse.getNamespace().equals("_EST_1_")){
		portlet_name = "estudio_isidro";
		entidad_nro=2;
}
if(renderResponse.getNamespace().equals("_LIQ_1_")){
		portlet_name = "liquidaciones";
		entidad_nro=2;
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
		entidad_nro=1;
}

List<Chequera> chequeras=(List<Chequera>)renderRequest.getAttribute(WebKeysLiquidaciones.CHEQUERAS);

if(chequeras==null){
	chequeras=ChequeServiceUtil.getUltimasChequeras(entidad_nro);
}
List<String> headerNames = new ArrayList<String>();
headerNames.add("cuenta-bancaria");
headerNames.add("nro-desde");
headerNames.add("nro-hasta");
headerNames.add("usuario");
headerNames.add("fecha");
//headerNames.add("print");
headerNames.add("delete");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-chequeras-were-found"));
					
if (chequeras != null && !chequeras.isEmpty()){
	int total = chequeras.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < chequeras.size(); i++) {	    
		Chequera chequera = (Chequera) chequeras.get(i);
	 	ResultRow row = new ResultRow(chequera,new Integer(1+i), i);
	 	row.addText(chequera.getDescripcion());
		row.addText(String.valueOf(chequera.getNroDesde()));
		row.addText(String.valueOf(chequera.getNroHasta()));
		
		row.addText(String.valueOf(chequera.getUsuario()));
		row.addText(String.valueOf(chequera.getFechaAlta()));
		
		/*StringBuilder sb=new StringBuilder();
		sb.append("<img alt=\"Exportar reporte\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/print.png\" onClick=\"javascript:exportarChequeraExcel('");
		sb.append(chequera.getIdCtaBcria());
		sb.append("','");
		sb.append(chequera.getNroDesde());
		sb.append("','");
		sb.append(chequera.getNroHasta());
		sb.append("');\" />");
		row.addText(sb.toString());*/
		if(!soloVer){
			StringBuilder sb2=new StringBuilder();
			sb2.append("<img alt=\"Eliminar Chequera\" src=\"");
			sb2.append(themeDisplay.getPathThemeImages());
			sb2.append("/common/delete.png\" onClick=\"javascript:borrarChequera('");
			sb2.append(chequera.getId_chequera());		
			sb2.append("');\" />");
			row.addText(sb2.toString());
		}else{
			row.addText("");
		}
		
		resultRows.add(row);
	}
}
%>
<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.DuplicateNumeroChequeException.class %>" message="duplicate-cheque" />
<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.ChequeSinChequeraException.class %>" message="cheque-sin-chequera" />
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

