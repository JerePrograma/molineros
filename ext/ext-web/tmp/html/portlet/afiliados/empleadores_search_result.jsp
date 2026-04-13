<%@ include file="/html/portlet/afiliados/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParent(cuit, razon, sucur) {
    jQuery("#<portlet:namespace />sucur").val(sucur);
    jQuery("#<portlet:namespace />cuit_empleador").val(cuit); 
    jQuery("#<portlet:namespace />empleador").val(razon);    
    jQuery("#<portlet:namespace />secc_seleccionada").val("1");    
    jQuery("#<portlet:namespace />divBtnBuscaEmpleador").hide();
	var cuit_defecto = '<%= WebKeysAfiliados.CUIT_DESEMPLEO_ANSES%>';
	var sucur_defecto = '<%= WebKeysAfiliados.SUCU_DESEMPLEO_ANSES%>';		
	if (cuit == cuit_defecto && sucur == sucur_defecto) {
		jQuery("#<portlet:namespace />categoria").val('2');
		jQuery("#<portlet:namespace />situRevista").val('1');
	}

    
    <portlet:namespace />cerrar();    
 }

</script>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<Empresa> empleadores=null;	
	String cuit=renderRequest.getParameter("cuit_empleador");
	String razon=renderRequest.getParameter("empleador");		
	//...	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.POP_UP);
	portletURL.setParameter("struts_action", "/afiliados/buscar_empleador");
	portletURL.setParameter(Constants.CMD,"PopUp"); 
					
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cuit");
	headerNames.add("empresa");				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL
				, headerNames,
				LanguageUtil.get(pageContext, "no-empresas-were-found"));
	
	empleadores=TraeListasServiceUtil.getEmpleadores(cuit,razon,searchContainer.getStart(),searchContainer.getEnd());
	//recupero coincidencias		
	
	//Seteo el total de la lista.
	int total = 0;
	if (empleadores.size() > 0) {
		total = empleadores.size();
	}

	//Si existe una sola coincidencia la plancho en los campos del parent
	if(total==1){
		Empresa emplUnica=(Empresa) empleadores.get(0);
		%>
			<script type="text/javascript">
				pasarParametrosAParent("<%=emplUnica.getCuit().trim()%>", "<%=emplUnica.getRazon_soc().trim()%>");
			</script>				
		<%
	//More de una coincidencia	
	}else {
	 	searchContainer.setTotal(total);
	 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < empleadores.size(); i++) {
	 		Empresa empleador = (Empresa) empleadores.get(i);
			ResultRow row = new ResultRow(empleador.getCuit(),empleador.getRazon_soc(), i);			
			// Name and short description
			StringBuilder sb = new StringBuilder();
			sb.append("<a href='javascript:pasarParametrosAParent(\"");
			sb.append(empleador.getCuit());
			sb.append("\",\"");
			sb.append(empleador.getRazon_soc());
			sb.append("\",\"");
			sb.append(empleador.getSucursal());
			sb.append("\")'>");			
			sb.append(empleador.getCuit());
			sb.append("</a>");
			row.addText(sb.toString());
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<a href='javascript:pasarParametrosAParent(\"");
			sb2.append(empleador.getCuit());
			sb2.append("\",\"");
			sb2.append(empleador.getRazon_soc());
			sb2.append("\",\"");
			sb2.append(empleador.getSucursal());
			sb2.append("\")'>");
			sb2.append(empleador.getRazon_soc());
			sb2.append("</a>");
			row.addText(sb2.toString());
			resultRows.add(row);
	 	}
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
	}
	
%>

