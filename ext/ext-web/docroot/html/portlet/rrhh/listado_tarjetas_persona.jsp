<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	
	String cmd = (String) request.getAttribute(Constants.CMD);
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	List<TarjetaAcceso> tarjetasPersona = (ArrayList<TarjetaAcceso>) request.getAttribute(WebKeysRrhh.TARJETAS_HISTORICO_PERSONA );
	
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Id");
	headerNames.add("Nro Tarjeta");
	headerNames.add("Fecha Alta");
	headerNames.add("Usuario Alta");
	headerNames.add("Fecha Baja");
	headerNames.add("Usuario Baja");
	headerNames.add("Estado");
	
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-tarjetas-were-found"));
	
	
	if (tarjetasPersona != null && tarjetasPersona.size()>0){
		int total = tarjetasPersona.size(); 
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();	
		
	 	for (int i = 0; i < tarjetasPersona.size(); i++) {
	 		TarjetaAcceso  tarjetaAcceso    = (TarjetaAcceso) tarjetasPersona.get(i);
	 		ResultRow row = new ResultRow(tarjetaAcceso,new Integer(1+i), i); 	 
		 	row.addText(String.valueOf(tarjetaAcceso.getId() )); 
		 	row.addText(String.valueOf(tarjetaAcceso.getId_tarjeta_acceso()) );
		 	row.addText(tarjetaAcceso.getAlta_fecha()==null ? "" : sdf.format(tarjetaAcceso.getAlta_fecha()));
		 	row.addText(tarjetaAcceso.getAlta_usr() );
		 	row.addText(tarjetaAcceso.getBaja_fecha() ==null? "" : sdf.format(tarjetaAcceso.getBaja_fecha()) );
		 	row.addText(tarjetaAcceso.getBaja_usr()==null ? "" :tarjetaAcceso.getBaja_usr()  );
		 	row.addText(tarjetaAcceso.getBaja_fecha() ==null? "Activa" : "Baja" );
			resultRows.add(row);		 
	 	}
	 
	}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
